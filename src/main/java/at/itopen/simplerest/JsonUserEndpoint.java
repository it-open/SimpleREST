/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.JsonPostEndpoint;
import java.util.List;

/**
 *
 * @author roland
 */
public class JsonUserEndpoint extends JsonPostEndpoint<JsonUser>{

    public JsonUserEndpoint(String endpointName) {
        super(endpointName);
    }

    @Override
    public void Call(Conversion conversion, List<String> UrlParameter) {
        System.out.println("User:"+getData().getUsername()+" : "+getData().getPassword());
    }
    
}
