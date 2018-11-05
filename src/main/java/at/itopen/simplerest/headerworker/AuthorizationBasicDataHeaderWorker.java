/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Request;
import at.itopen.simplerest.security.BasicAuthUser;
import java.util.Base64;


/**
 *
 * @author roland
 */
public class AuthorizationBasicDataHeaderWorker extends AbstractHeaderWorker{

    /**
     *
     */
    public AuthorizationBasicDataHeaderWorker() {
     
    }

    /**
     *
     * @param request
     */
    @Override
    public void work(Request request) {
        String value=request.getHeaders().getAll("authorization").get(1);
        value=new String(Base64.getDecoder().decode(value));
        String[] parts=value.split(":");
        if (parts.length==2)
        {
            if (request.getUser() instanceof BasicAuthUser)
            {
                ((BasicAuthUser)request.getUser()).setAuth(request,parts[0], parts[1]);
                request.getHeaders().remove("authorization");
            }            
        }
        
                
        
    }
    
   

    
}
