/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

import at.itopen.simplerest.conversion.Conversion;
import java.util.Map;

/**
 *
 * @author roland
 */
public class EndpointWorker {
    
    RestEndpoint restEndpoint;
    Map<String,String> pathParameter;

    public EndpointWorker(RestEndpoint restEndpoint, Map<String,String> pathParameter) {
        this.restEndpoint = restEndpoint;
        this.pathParameter = pathParameter;
    }

    public Map<String,String> getPathParameter() {
        return pathParameter;
    }

    public RestEndpoint getRestEndpoint() {
        return restEndpoint;
    }
    
    public void work(Conversion conversion)
    {
        restEndpoint.CallEndpoint(conversion, pathParameter);
    }
    
    
    
    
}
