/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.path.RestEndpoint;
import java.util.Map;


/**
 *
 * @author roland
 */
public class NotFoundEndpoint extends RestEndpoint {
  
    public NotFoundEndpoint() {
        super("NOT-FOUND");
    }
    
    @Override
    public void Call(Conversion conversion, Map<String,String> UrlParameter) {
        conversion.getResponse().setData(conversion.getRequest().getUri().toString());
        
    }
    
}
