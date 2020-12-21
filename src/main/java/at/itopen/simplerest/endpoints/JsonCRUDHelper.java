/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.ContentType;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.HttpStatus;
import at.itopen.simplerest.path.EndpointDocumentation;
import at.itopen.simplerest.path.RestEndpoint;
import at.itopen.simplerest.path.RestPath;
import at.itopen.simplerest.security.BasicUser;
import at.itopen.simplerest.security.RestUser;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 * @param <G> Getter
 * @param <S> Setter
 * @param <O> Object
 * @param <U> User
 */
public abstract class JsonCRUDHelper<G extends AbstractGetter<O>, S extends AbstractSetter<O>, O, U> {

    RestEndpoint get;
    RestEndpoint put;
    RestEndpoint del;
    RestEndpoint getall;
    RestEndpoint newp;

    /**
     *
     * @param conversion
     * @return
     */
    protected RestUser<U> getUser(Conversion conversion) {
        BasicUser bu = conversion.getRequest().getUser();
        if (bu == null) {
            return null;
        }
        if (bu instanceof RestUser) {
            return (RestUser<U>) bu;
        } else {
            return null;
        }
    }

    private class PostNew extends JsonPutOrPostEndpoint<S> {

        public PostNew(String endpointName, Class dataClass) {
            super(endpointName, dataClass);
        }

        @Override
        public void call(Conversion conversion, Map<String, String> urlParameter) {
            O data = newObject();
            getData().setConversion(conversion).internalSetData(data);
            data = JsonCRUDHelper.this.addNewItem(conversion, urlParameter, data, getUser(conversion));
            if (data != null) {
                conversion.getResponse().setData(newGetter(conversion, data));
            }
        }

    }

    private class PostUpdate extends JsonPutOrPostEndpoint<S> {

        public PostUpdate(String endpointName, Class dataClass) {
            super(endpointName, dataClass);
        }

        @Override
        public void call(Conversion conversion, Map<String, String> urlParameter) {
            JsonCRUDHelper.this.updateItem(conversion, urlParameter, (S) getData().setConversion(conversion), urlParameter.get("id"), getUser(conversion));
        }

    }

    final Class<G> getterType;
    final Class<S> setterType;
    final Class<O> objectType;

    /**
     *
     * @return
     */
    public Class<G> getGetterType() {
        return getterType;
    }

    /**
     *
     * @return
     */
    public Class<O> getObjectType() {
        return objectType;
    }

    /**
     *
     * @return
     */
    public Class<S> getSetterType() {
        return setterType;
    }

    /**
     *
     * @param ep
     */
    public void addGlobalEntry(RestEndpoint ep) {
        sub.addRestEndpoint(ep);
    }

    /**
     *
     * @param ep
     */
    public void addIDEntry(RestEndpoint ep) {
        sub.getSubPath(":id").addRestEndpoint(ep);
    }

    private RestPath sub;

    /**
     *
     * @param entry
     * @param parentPath
     * @param doku
     */
    public JsonCRUDHelper(String entry, RestPath parentPath, String doku) {

        Type tgetter = getClass().getGenericSuperclass();
        getterType = (Class) ((ParameterizedType) tgetter).getActualTypeArguments()[0];

        Type tsetter = getClass().getGenericSuperclass();
        setterType = (Class) ((ParameterizedType) tsetter).getActualTypeArguments()[1];
        Class setterclass = setterType;

        Type tobject = getClass().getGenericSuperclass();
        objectType = (Class) ((ParameterizedType) tobject).getActualTypeArguments()[2];

        sub = parentPath.getSubPath(entry);

        newp = parentPath.addRestEndpoint(new PostNew(entry, setterclass));

        getall = parentPath.addRestEndpoint(new GetEndpoint(entry) {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                List<O> erg = JsonCRUDHelper.this.getAllItem(conversion, urlParameter, getUser(conversion));
                List<G> send = new ArrayList<>();
                for (O o : erg) {
                    send.add(newGetter(conversion, o));
                }
                conversion.getResponse().setData(send);
            }
        });

        get = sub.addRestEndpoint(new GetEndpoint(":id") {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                O o = JsonCRUDHelper.this.getSingeItem(conversion, urlParameter, urlParameter.get("id"), getUser(conversion));
                if (o != null) {
                    conversion.getResponse().setData(newGetter(conversion, o));
                } else {
                    conversion.getResponse().setStatus(HttpStatus.NotFound);
                }
            }
        });

        put = sub.addRestEndpoint(new PostUpdate(":id", setterclass));

        del = sub.addRestEndpoint(new DeleteEndpoint(":id") {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                JsonCRUDHelper.this.deleteItem(conversion, urlParameter, urlParameter.get("id"), getUser(conversion));
            }
        });

        documentation(getterType, setterclass, setterclass, doku);

    }

    /**
     *
     * @param conversion
     * @param data
     * @return
     */
    protected G newGetter(Conversion conversion, O data) {
        try {
            for (Constructor c : getterType.getClass().getConstructors()) {
                if (c.getParameterCount() == 0) {
                    G getter = (G) c.newInstance();
                    getter.setConversion(conversion).internalGetData(data);
                    return getter;
                }
            }
            G getter = (G) getterType.newInstance();
            getter.setConversion(conversion).internalGetData(data);
            return getter;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param conversion
     * @param data
     * @return
     */
    protected S newSetter(Conversion conversion, O data) {
        try {
            for (Constructor c : setterType.getClass().getConstructors()) {
                if (c.getParameterCount() == 0) {
                    S setter = (S) c.newInstance();
                    setter.setConversion(conversion).internalSetData(data);
                    return setter;
                }
            }
            S setter = (S) setterType.newInstance();
            setter.setConversion(conversion).internalSetData(data);
            return setter;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @return
     */
    protected O newObject() {
        try {
            for (Constructor c : objectType.getClass().getConstructors()) {
                if (c.getParameterCount() == 0) {
                    return (O) c.newInstance();
                }
            }
            return (O) objectType.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {

        }
        return null;
    }

    /**
     *
     * @param items
     * @param user
     * @param conversion
     * @return
     */
    public List<O> filterRead(List<O> items, RestUser<U> user, Conversion conversion) {
        List<O> erg = new ArrayList<>();
        for (O item : items) {
            if (user.may(conversion, item, RestUser.AccessType.READ)) {
                erg.add(item);
            }
        }
        return erg;
    }

    /**
     *
     * @param getClass
     * @param putClass
     * @param newClass
     * @param objectname
     */
    public void documentation(Class getClass, Class putClass, Class newClass, String objectname) {

        get.setDocumentation(new EndpointDocumentation("Get a single " + objectname, ContentType.JSON, null, getClass).addPathParameter("id", "ID Number of Object"));
        getall.setDocumentation(new EndpointDocumentation("Get all " + objectname, ContentType.JSON, null, getClass).setOutlist(true));
        newp.setDocumentation(new EndpointDocumentation("Add a new " + objectname, ContentType.JSON, newClass, getClass));
        put.setDocumentation(new EndpointDocumentation("Update " + objectname, ContentType.JSON, putClass, getClass).addPathParameter("id", "ID Number of Object"));
        del.setDocumentation(new EndpointDocumentation("Remove a " + objectname, ContentType.JSON, null, getClass).addPathParameter("id", "ID Number of Object"));

    }

    /**
     *
     * @param conversion
     * @param urlParameter
     * @param data
     * @param user
     * @return
     */
    public abstract O addNewItem(Conversion conversion, Map<String, String> urlParameter, O data, RestUser<U> user);

    /**
     *
     * @param conversion
     * @param urlParameter
     * @param id
     * @param user
     * @return
     */
    public abstract O getSingeItem(Conversion conversion, Map<String, String> urlParameter, String id, RestUser<U> user);

    /**
     *
     * @param conversion
     * @param urlParameter
     * @param user
     * @return
     */
    public abstract List<O> getAllItem(Conversion conversion, Map<String, String> urlParameter, RestUser<U> user);

    /**
     *
     * @param conversion
     * @param urlParameter
     * @param data
     * @param id
     * @param user
     */
    public abstract void updateItem(Conversion conversion, Map<String, String> urlParameter, S data, String id, RestUser<U> user);

    /**
     *
     * @param conversion
     * @param urlParameter
     * @param id
     * @param user
     */
    public abstract void deleteItem(Conversion conversion, Map<String, String> urlParameter, String id, RestUser<U> user);

}
