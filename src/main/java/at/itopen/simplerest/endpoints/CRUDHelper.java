/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.ContentType;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.path.EndpointDocumentation;
import at.itopen.simplerest.path.RestEndpoint;
import at.itopen.simplerest.path.RestPath;
import java.util.Map;

/**
 *
 * @author roland
 */
public abstract class CRUDHelper {

    RestEndpoint get;
    RestEndpoint put;
    RestEndpoint del;
    RestEndpoint getall;
    RestEndpoint newp;

    /**
     *
     * @param entry
     * @param parentPath
     */
    public CRUDHelper(String entry, RestPath parentPath) {

        RestPath sub = new RestPath(entry);
        parentPath.addSubPath(sub);

        newp = parentPath.addRestEndpoint(new PutOrPostEndpoint(entry) {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                CRUDHelper.this.addNewItem(conversion, urlParameter);
            }
        });

        getall = parentPath.addRestEndpoint(new GetEndpoint(entry) {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                CRUDHelper.this.getAllItem(conversion, urlParameter);
            }
        });

        get = sub.addRestEndpoint(new GetEndpoint(":id") {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                CRUDHelper.this.getSingeItem(conversion, urlParameter);
            }
        });

        put = sub.addRestEndpoint(new PutOrPostEndpoint(":id") {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                CRUDHelper.this.updateItem(conversion, urlParameter);
            }
        });

        del = sub.addRestEndpoint(new DeleteEndpoint(":id") {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                CRUDHelper.this.deleteItem(conversion, urlParameter);
            }
        });

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
        getall.setDocumentation(new EndpointDocumentation("Get all " + objectname, ContentType.JSON, null, getClass));
        newp.setDocumentation(new EndpointDocumentation("Add a new " + objectname, ContentType.JSON, newClass, getClass));
        put.setDocumentation(new EndpointDocumentation("Update " + objectname, ContentType.JSON, putClass, getClass).addPathParameter("id", "ID Number of Object"));
        del.setDocumentation(new EndpointDocumentation("Remove a " + objectname, ContentType.JSON, null, getClass).addPathParameter("id", "ID Number of Object"));

    }

    /**
     *
     * @param conversion
     * @param urlParameter
     */
    public abstract void addNewItem(Conversion conversion, Map<String, String> urlParameter);

    /**
     *
     * @param conversion
     * @param urlParameter
     */
    public abstract void getSingeItem(Conversion conversion, Map<String, String> urlParameter);

    /**
     *
     * @param conversion
     * @param urlParameter
     */
    public abstract void getAllItem(Conversion conversion, Map<String, String> urlParameter);

    /**
     *
     * @param conversion
     * @param urlParameter
     */
    public abstract void updateItem(Conversion conversion, Map<String, String> urlParameter);

    /**
     *
     * @param conversion
     * @param urlParameter
     */
    public abstract void deleteItem(Conversion conversion, Map<String, String> urlParameter);

}
