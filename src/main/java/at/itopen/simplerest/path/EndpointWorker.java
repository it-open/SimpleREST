/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

import at.itopen.simplerest.conversion.Conversion;
import java.util.List;

/**
 *
 * @author roland
 */
public class EndpointWorker {
    
    RestEndpoint restEndpoint;
    List<String> pathParameter;

    public EndpointWorker(RestEndpoint restEndpoint, List<String> pathParameter) {
        this.restEndpoint = restEndpoint;
        this.pathParameter = pathParameter;
    }

    public List<String> getPathParameter() {
        return pathParameter;
    }

    public RestEndpoint getRestEndpoint() {
        return restEndpoint;
    }
    
    public void work(Conversion conversion)
    {
        restEndpoint.Call(conversion, pathParameter);
    }
    
    
    
    
}
