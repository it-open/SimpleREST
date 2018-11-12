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
public class AuthRestPath extends RestPath implements AuthenticatedRestPath{
    
    
    public AuthRestPath(String pathName) {
        super(pathName);
    }

    @Override
    protected boolean checkPath(Conversion conversion) {
        conversion.getRequest().getUser().setAuthenticated(true);
        return super.checkPath(conversion); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
