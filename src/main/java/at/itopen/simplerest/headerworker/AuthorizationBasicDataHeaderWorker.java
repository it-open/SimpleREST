/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Request;

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
        //Decode Base64   User:pass
                
        
    }
    
   

    
}
