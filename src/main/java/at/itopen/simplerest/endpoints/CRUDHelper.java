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
public abstract class CRUDHelper {

    public CRUDHelper(String entry, RestPath parentPath) {

        RestPath sub = new RestPath(entry);
        parentPath.addSubPath(sub);

        parentPath.addRestEndpoint(new PostEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                addNewItem(conversion, UrlParameter);
            }
        });

        parentPath.addRestEndpoint(new PutEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                addNewItem(conversion, UrlParameter);
            }
        });

        parentPath.addRestEndpoint(new GetEndpoint(entry) {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                getAllItem(conversion, UrlParameter);
            }
        });

        sub.addRestEndpoint(new GetEndpoint("*") {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                getSingeItem(conversion, UrlParameter);
            }
        });

        sub.addRestEndpoint(new PostEndpoint("*") {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                updateItem(conversion, UrlParameter);
            }
        });

        sub.addRestEndpoint(new PutEndpoint("*") {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                updateItem(conversion, UrlParameter);
            }
        });

        sub.addRestEndpoint(new DeleteEndpoint("*") {
            @Override
            public void Call(Conversion conversion, List<String> UrlParameter) {
                deleteItem(conversion, UrlParameter);
            }
        });

    }

    public abstract void addNewItem(Conversion conversion, List<String> UrlParameter);

    public abstract void getSingeItem(Conversion conversion, List<String> UrlParameter);

    public abstract void getAllItem(Conversion conversion, List<String> UrlParameter);

    public abstract void updateItem(Conversion conversion, List<String> UrlParameter);

    public abstract void deleteItem(Conversion conversion, List<String> UrlParameter);

}
