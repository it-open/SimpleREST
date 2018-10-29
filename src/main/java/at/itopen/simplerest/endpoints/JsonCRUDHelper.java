/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.path.RestPath;
import java.util.List;

/**
 *
 * @author roland
 */
public abstract class JsonCRUDHelper<T> {
    
    private class PostNew extends JsonPostEndpoint<T>{

            public PostNew(String endpointName) {
                super(endpointName);
            }

            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                JsonCRUDHelper.this.addNewItem(conversion, UrlParameter);
            }
            
        }
    
    private class PostUpdate extends JsonPostEndpoint<T>{

            public PostUpdate(String endpointName) {
                super(endpointName);
            }

            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                JsonCRUDHelper.this.updateItem(conversion, UrlParameter);
            }
            
        }
    
    private class PutNew extends JsonPutEndpoint<T>{

            public PutNew(String endpointName) {
                super(endpointName);
            }

            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                JsonCRUDHelper.this.addNewItem(conversion, UrlParameter);
            }
            
        }
    
    private class PutUpdate extends JsonPutEndpoint<T>{

            public PutUpdate(String endpointName) {
                super(endpointName);
            }

            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                JsonCRUDHelper.this.updateItem(conversion, UrlParameter);
            }
            
        }
    

    public JsonCRUDHelper(String entry, RestPath parentPath) {

        RestPath sub = new RestPath(entry);
        parentPath.addSubPath(sub);
        
        
        parentPath.addRestEndpoint(new PostNew(entry));
        
        parentPath.addRestEndpoint(new PutNew(entry));

        parentPath.addRestEndpoint(new GetEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                JsonCRUDHelper.this.getAllItem(conversion, UrlParameter);
            }
        });

        sub.addRestEndpoint(new GetEndpoint("*") {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                JsonCRUDHelper.this.getSingeItem(conversion, UrlParameter);
            }
        });

        sub.addRestEndpoint(new PostUpdate("*"));

        sub.addRestEndpoint(new PutUpdate("*"));

        sub.addRestEndpoint(new DeleteEndpoint("*") {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                JsonCRUDHelper.this.deleteItem(conversion, UrlParameter);
            }
        });

    }
    

    public abstract void addNewItem(Conversion conversion, List<String> UrlParameter);

    public abstract void getSingeItem(Conversion conversion, List<String> UrlParameter);

    public abstract void getAllItem(Conversion conversion, List<String> UrlParameter);

    public abstract void updateItem(Conversion conversion, List<String> UrlParameter);

    public abstract void deleteItem(Conversion conversion, List<String> UrlParameter);

}
