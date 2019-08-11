/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.security.RestUser;

/**
 *
 * @author roland
 */
public class AuthRestPath extends RestPath implements AuthenticatedRestPath {

    RestUser.AUTH_TYPE[] types;

    /**
     *
     * @param pathName
     */
    public AuthRestPath(String pathName, RestUser.AUTH_TYPE... types) {
        super(pathName);
        this.types = types;
    }

    @Override
    protected boolean checkPath(Conversion conversion, String pathData) {
        if (conversion.getRequest().getUser() == null) {
            return false;
        }
        if (conversion.getRequest().getUser().isAuthenticated() == false) {
            return false;
        }
        if (conversion.getRequest().getUser() instanceof RestUser) {
            for (RestUser.AUTH_TYPE type : types) {
                if (type.equals(((RestUser) conversion.getRequest().getUser()).getAuth_type())) {
                    return true;
                }
            }
        }
        return false;
    }

}
