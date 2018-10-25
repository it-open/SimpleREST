/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Request;
import java.util.Base64;


/**
 *
 * @author roland
 */
public class AuthorizationBasicDataHeaderWorker extends AbstractHeaderWorker{

 

    public AuthorizationBasicDataHeaderWorker() {
     
    }

    
    
    @Override
    public void work(Request request) {
        String value=request.getHeaders().getAll("authorization").get(1);
        value=new String(Base64.getDecoder().decode(value));
        String[] parts=value.split(":");
        if (parts.length==2)
        {
            request.getUser().setName(parts[0]);
            request.getUser().setPassword(parts[1]);
        }
        //Decode Base64   User:pass
                
        
    }
    
   

    
}
