/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.JsonPostEndpoint;
import java.util.Map;

/**
 * Example for an Endpoint what gets username and password via Json POST
 * @author roland
 */
public class JsonUserEndpoint extends JsonPostEndpoint<JsonUser>{

    /**
     * Constructor
     * @param endpointName Name of the Endpoint
     */
    public JsonUserEndpoint(String endpointName) {
        super(endpointName);
    }

    /**
     * Endpoint is Called. Data loaded
     * @param conversion the request and Response Object
     * @param UrlParameter Path Parameters if there are any
     */
    @Override
    public void Call(Conversion conversion, Map<String,String> UrlParameter) {
        System.out.println("User:"+getData().getUsername()+" : "+getData().getPassword());
    }
    
}
