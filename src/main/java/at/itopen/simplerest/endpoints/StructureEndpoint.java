/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.path.AuthenticatedRestEndpoint;
import at.itopen.simplerest.path.AuthenticatedRestPath;
import at.itopen.simplerest.path.RestEndpoint;
import at.itopen.simplerest.path.RestPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author roland
 */
public class StructureEndpoint extends GetEndpoint {

    
    private class Item{
        String name;
        boolean auth;

        public String getName() {
            return name;
        }

        public boolean isAuth() {
            return auth;
        }

        public Item(String name, boolean auth) {
            this.name = name;
            this.auth = auth;
        }
        
    }
    
    private class PathItem extends Item
    {

        public PathItem(String name, boolean auth) {
            super(name, auth);
        }
        
        
        List<Item> subItems=new ArrayList<>();

        public List<Item> getSubItems() {
            return subItems;
        }
        
    }
    
    private class EndPointItem extends Item
    {

        public EndPointItem(String method, String name, boolean auth) {
            super(name, auth);
            this.method = method;
        }
        
        String method;


        public String getMethod() {
            return method;
        }
        
    }
    
    /**
     *
     * @param endpointName
     */
    public StructureEndpoint(String endpointName) {
        super(endpointName);
    }

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    @Override
    public void Call(Conversion conversion, Map<String,String> UrlParameter) {
        PathItem root=new PathItem("/", false);
        subPath(conversion.getServer().getRootEndpoint(),root);
        conversion.getResponse().setData(root);
                
    }
    
    private void subPath(RestPath path,PathItem item)
    {
        for (RestPath sub:path.getSubPaths())
        {
            boolean auth=item.auth || (sub instanceof AuthenticatedRestPath);
            PathItem pi=new PathItem(sub.getPathName(), auth);
            item.getSubItems().add(pi);
            subPath(sub, pi);
        }
        
        for (RestEndpoint sub:path.getEndpoints())
        {
            boolean auth=item.auth || (sub instanceof AuthenticatedRestEndpoint);
            String method="ALL";
            if (sub instanceof GetEndpoint) method="GET";
            if (sub instanceof PostEndpoint) method="POST";
            if (sub instanceof PutEndpoint) method="PUT";
            if (sub instanceof DeleteEndpoint) method="DELETE";
            if (sub instanceof PutOrPostEndpoint) method="PUT,POST";
            
            EndPointItem pi=new EndPointItem(method,sub.getEndpointName(), auth);
            item.getSubItems().add(pi);
        }
    }

    
    
    
}
