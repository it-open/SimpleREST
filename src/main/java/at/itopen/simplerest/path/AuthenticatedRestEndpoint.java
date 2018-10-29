/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

import at.itopen.simplerest.conversion.Conversion;

/**
 *
 * @author roland
 */
public abstract class AuthenticatedRestEndpoint extends RestEndpoint{
    
    public AuthenticatedRestEndpoint(String pathName) {
        super(pathName);
    }

    @Override
    protected boolean checkEndpoint(Conversion conversion) {
        if (conversion.getRequest().getUser().isAuthenticated())
            return super.checkEndpoint(conversion); //To change body of generated methods, choose Tools | Templates.
        else
            return false;
    }
    
    
    
}
