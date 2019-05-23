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
 * @param <T>
 */
public abstract class JsonCRUDHelper<GETTER extends AbstractGetter<OBJECT>, SETTER extends AbstractSetter<OBJECT>, OBJECT, USER> {

    RestEndpoint get, put, del, getall, newp;

    private RestUser<USER> getUser(Conversion conversion) {
        BasicUser bu = conversion.getRequest().getUser();
        if (bu == null) {
            return null;
        }
        if (bu instanceof RestUser) {
            return ((RestUser<USER>) bu);
        } else {
            return null;
        }
    }

    private class PostNew extends JsonPutOrPostEndpoint<SETTER> {

        public PostNew(String endpointName, Class dataClass) {
            super(endpointName, dataClass);
        }

        @Override
        public void Call(Conversion conversion, Map<String, String> UrlParameter) {
            OBJECT data = newObject();
            getData().internalSetData(data);
            JsonCRUDHelper.this.addNewItem(conversion, UrlParameter, data, getUser(conversion));
        }

    }

    private class PostUpdate extends JsonPutOrPostEndpoint<SETTER> {

        public PostUpdate(String endpointName, Class dataClass) {
            super(endpointName, dataClass);
        }

        @Override
        public void Call(Conversion conversion, Map<String, String> UrlParameter) {
            OBJECT data = newObject();
            getData().internalSetData(data);
            JsonCRUDHelper.this.updateItem(conversion, UrlParameter, data, UrlParameter.get("id"), getUser(conversion));
        }

    }

    final Class<GETTER> getterType;
    final Class<OBJECT> objectType;

    /**
     *
     * @param entry
     * @param parentPath
     * @param dataClass
     */
    public JsonCRUDHelper(String entry, RestPath parentPath, Class setterclass) {

        Type tgetter = getClass().getGenericSuperclass();
        getterType = (Class) ((ParameterizedType) tgetter).getActualTypeArguments()[0];

        Type tobject = getClass().getGenericSuperclass();
        objectType = (Class) ((ParameterizedType) tobject).getActualTypeArguments()[2];

        RestPath sub = new RestPath(entry);
        parentPath.addSubPath(sub);

        newp = parentPath.addRestEndpoint(new PostNew(entry, setterclass));

        getall = parentPath.addRestEndpoint(new GetEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, Map<String, String> UrlParameter) {
                List<OBJECT> erg = JsonCRUDHelper.this.getAllItem(conversion, UrlParameter, getUser(conversion));
                List<GETTER> send = new ArrayList<>();
                for (OBJECT o : erg) {
                    send.add(newGetter(o));
                }
                conversion.getResponse().setData(send);
            }
        });

        get = sub.addRestEndpoint(new GetEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String, String> UrlParameter) {
                OBJECT o = JsonCRUDHelper.this.getSingeItem(conversion, UrlParameter, UrlParameter.get("id"), getUser(conversion));
                if (o != null) {
                    conversion.getResponse().setData(newGetter(o));
                } else {
                    conversion.getResponse().setStatus(HttpStatus.NotFound);
                }
            }
        });

        put = sub.addRestEndpoint(new PostUpdate(":id", setterclass));

        del = sub.addRestEndpoint(new DeleteEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String, String> UrlParameter) {
                JsonCRUDHelper.this.deleteItem(conversion, UrlParameter, UrlParameter.get("id"), getUser(conversion));
            }
        });

    }

    private GETTER newGetter(OBJECT data) {
        try {
            for (Constructor c : getterType.getClass().getConstructors()) {
                if (c.getParameterCount() == 0) {
                    GETTER getter = (GETTER) c.newInstance();
                    getter.internalGetData(data);
                    return getter;
                }
            }
            GETTER getter = (GETTER) getterType.newInstance();
            getter.internalGetData(data);
            return getter;
        } catch (InstantiationException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private OBJECT newObject() {
        try {
            for (Constructor c : objectType.getClass().getConstructors()) {
                if (c.getParameterCount() == 0) {
                    return (OBJECT) c.newInstance();
                }
            }
            return (OBJECT) objectType.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(JsonCRUDHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<OBJECT> filterRead(List<OBJECT> items, RestUser<USER> user, Conversion conversion) {
        List<OBJECT> erg = new ArrayList<>();
        for (OBJECT item : items) {
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
    public void Documentation(Class getClass, Class putClass, Class newClass, String objectname) {

        get.setDocumentation(new EndpointDocumentation("Get a single " + objectname, ContentType.JSON, null, getClass).addPathParameter("id", "ID Number of Object"));
        getall.setDocumentation(new EndpointDocumentation("Get all " + objectname, ContentType.JSON, null, getClass));
        newp.setDocumentation(new EndpointDocumentation("Add a new " + objectname, ContentType.JSON, newClass, getClass));
        put.setDocumentation(new EndpointDocumentation("Update " + objectname, ContentType.JSON, putClass, getClass).addPathParameter("id", "ID Number of Object"));
        del.setDocumentation(new EndpointDocumentation("Remove a " + objectname, ContentType.JSON, null, getClass).addPathParameter("id", "ID Number of Object"));

    }

    /**
     *
     * @param conversion
     * @param UrlParameter
     * @param data
     */
    public abstract void addNewItem(Conversion conversion, Map<String, String> UrlParameter, OBJECT data, RestUser<USER> user);

    /**
     *
     * @param conversion
     * @param UrlParameter
     * @param id
     */
    public abstract OBJECT getSingeItem(Conversion conversion, Map<String, String> UrlParameter, String id, RestUser<USER> user);

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    public abstract List<OBJECT> getAllItem(Conversion conversion, Map<String, String> UrlParameter, RestUser<USER> user);

    /**
     *
     * @param conversion
     * @param UrlParameter
     * @param data
     * @param id
     */
    public abstract void updateItem(Conversion conversion, Map<String, String> UrlParameter, OBJECT data, String id, RestUser<USER> user);

    /**
     *
     * @param conversion
     * @param UrlParameter
     * @param id
     */
    public abstract void deleteItem(Conversion conversion, Map<String, String> UrlParameter, String id, RestUser<USER> user);

}
