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
public class UrlListEndpoint extends GetEndpoint {

    /**
     *
     * @param endpointName
     */
    public UrlListEndpoint(String endpointName) {
        super(endpointName);
    }

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    @Override
    public void Call(Conversion conversion, Map<String,String> UrlParameter) {
        String path="/";
        List<String> endpoints=new ArrayList<>();
        subPath(conversion.getServer().getRootEndpoint(),endpoints,path,false);
        conversion.getResponse().setData(endpoints);
                
    }
    
    private void subPath(RestPath path,List<String> endpoints,String pathname,boolean isauth)
    {
        for (RestPath sub:path.getSubPaths())
        {
            boolean auth=isauth || (sub instanceof AuthenticatedRestPath);
            String newPathName=pathname+sub.getPathName()+"/";
            subPath(sub, endpoints,newPathName,auth);
        }
        
        for (RestEndpoint sub:path.getEndpoints())
        {
            boolean auth=isauth || (sub instanceof AuthenticatedRestEndpoint);
            String method="ALL";
            if (sub instanceof GetEndpoint) method="GET";
            if (sub instanceof PostEndpoint) method="POST";
            if (sub instanceof PutEndpoint) method="PUT";
            if (sub instanceof DeleteEndpoint) method="DELETE";
            if (sub instanceof PutOrPostEndpoint) method="PUT,POST";
            
            String line="";
            if (auth)
                line+="!";
            line+=method+" "+pathname+sub.getEndpointName();
            endpoints.add(line);
        }
    }

    
    
    
}
