/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.path.RestPath;
import java.util.List;
import java.util.Map;

/**
 *
 * @author roland
 */
public abstract class CRUDHelper {

    public CRUDHelper(String entry, RestPath parentPath) {

        RestPath sub = new RestPath(entry);
        parentPath.addSubPath(sub);
        

        parentPath.addRestEndpoint(new PutOrPostEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.addNewItem(conversion, UrlParameter);
            }
        });

       

        parentPath.addRestEndpoint(new GetEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.getAllItem(conversion, UrlParameter);
            }
        });

        sub.addRestEndpoint(new GetEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.getSingeItem(conversion, UrlParameter);
            }
        });

        sub.addRestEndpoint(new PutOrPostEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.updateItem(conversion, UrlParameter);
            }
        });

        

        sub.addRestEndpoint(new DeleteEndpoint(":id") {
            @Override
            public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                CRUDHelper.this.deleteItem(conversion, UrlParameter);
            }
        });

    }
    

    public abstract void addNewItem(Conversion conversion, Map<String,String> UrlParameter);

    public abstract void getSingeItem(Conversion conversion, Map<String,String> UrlParameter);

    public abstract void getAllItem(Conversion conversion, Map<String,String> UrlParameter);

    public abstract void updateItem(Conversion conversion, Map<String,String> UrlParameter);

    public abstract void deleteItem(Conversion conversion, Map<String,String> UrlParameter);

}
