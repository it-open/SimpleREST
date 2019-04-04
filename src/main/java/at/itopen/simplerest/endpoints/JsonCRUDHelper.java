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
 * @param <T>
 */
public abstract class JsonCRUDHelper<T> {
    
    RestEndpoint get,put,del,getall,newp;
    
    
    private class PostNew extends JsonPutOrPostEndpoint<T>{

            public PostNew(String endpointName,Class dataClass) {
                super(endpointName,dataClass);
            }

            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                JsonCRUDHelper.this.addNewItem(conversion, UrlParameter,getData());
            }
            
        }
    
    private class PostUpdate extends JsonPutOrPostEndpoint<T>{

            public PostUpdate(String endpointName,Class dataClass) {
                super(endpointName,dataClass);
            }

            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                JsonCRUDHelper.this.updateItem(conversion, UrlParameter,getData(),UrlParameter.get("id"));
            }
            
        }
    
    /**
     *
     * @param entry
     * @param parentPath
     * @param dataClass
     */
    public JsonCRUDHelper(String entry, RestPath parentPath,Class dataClass) {

        RestPath sub = new RestPath(entry);
        parentPath.addSubPath(sub);
        
        
        newp=parentPath.addRestEndpoint(new PostNew(entry,dataClass));
        
      

        getall=parentPath.addRestEndpoint(new GetEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                JsonCRUDHelper.this.getAllItem(conversion, UrlParameter);
            }
        });

        get=sub.addRestEndpoint(new GetEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                JsonCRUDHelper.this.getSingeItem(conversion, UrlParameter,UrlParameter.get("id"));
            }
        });

        put=sub.addRestEndpoint(new PostUpdate(":id",dataClass));

       

        del=sub.addRestEndpoint(new DeleteEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                JsonCRUDHelper.this.deleteItem(conversion, UrlParameter,UrlParameter.get("id"));
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
     * @param data
     */
    public abstract void addNewItem(Conversion conversion, Map<String,String> UrlParameter,T data);

    /**
     *
     * @param conversion
     * @param UrlParameter
     * @param id
     */
    public abstract void getSingeItem(Conversion conversion, Map<String,String> UrlParameter,String id);

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
     * @param data
     * @param id
     */
    public abstract void updateItem(Conversion conversion, Map<String,String> UrlParameter,T data,String id);

    /**
     *
     * @param conversion
     * @param UrlParameter
     * @param id
     */
    public abstract void deleteItem(Conversion conversion, Map<String,String> UrlParameter,String id);

}
