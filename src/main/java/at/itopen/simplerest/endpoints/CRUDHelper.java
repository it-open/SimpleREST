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
import java.util.List;
import java.util.Map;

/**
 *
 * @author roland
 */
public abstract class CRUDHelper {

    
    RestEndpoint get,put,del,getall,newp;
    /**
     *
     * @param entry
     * @param parentPath
     */
    public CRUDHelper(String entry, RestPath parentPath) {

        RestPath sub = new RestPath(entry);
        parentPath.addSubPath(sub);
        

        newp=parentPath.addRestEndpoint(new PutOrPostEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.addNewItem(conversion, UrlParameter);
            }
        });

       

        getall=parentPath.addRestEndpoint(new GetEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.getAllItem(conversion, UrlParameter);
            }
        });

        get=sub.addRestEndpoint(new GetEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.getSingeItem(conversion, UrlParameter);
            }
        });

        put=sub.addRestEndpoint(new PutOrPostEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.updateItem(conversion, UrlParameter);
            }
        });

        

        del=sub.addRestEndpoint(new DeleteEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.deleteItem(conversion, UrlParameter);
            }
        });

    }
    
    public void Documentation(Class getClass,Class putClass,Class newClass,String objectname)
    {
        get.setDocumentation(new EndpointDocumentation("Get a single "+objectname, ContentType.JSON, null, getClass).addPathParameter("id", "ID Number of Object"));
        getall.setDocumentation(new EndpointDocumentation("Get all "+objectname, ContentType.JSON, null, getClass));
        newp.setDocumentation(new EndpointDocumentation("Add a new "+objectname, ContentType.JSON, newClass, getClass));
        put.setDocumentation(new EndpointDocumentation("Update "+objectname, ContentType.JSON, putClass, getClass).addPathParameter("id", "ID Number of Object"));
        del.setDocumentation(new EndpointDocumentation("Remove a "+objectname, ContentType.JSON, null, getClass).addPathParameter("id", "ID Number of Object"));
        
    }
    
    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    public abstract void addNewItem(Conversion conversion, Map<String,String> UrlParameter);

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    public abstract void getSingeItem(Conversion conversion, Map<String,String> UrlParameter);

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    public abstract void getAllItem(Conversion conversion, Map<String,String> UrlParameter);

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    public abstract void updateItem(Conversion conversion, Map<String,String> UrlParameter);

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    public abstract void deleteItem(Conversion conversion, Map<String,String> UrlParameter);

}
