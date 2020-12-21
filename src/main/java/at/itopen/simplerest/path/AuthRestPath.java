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

    RestUser.AUTHTYPE[] types;

    /**
     *
     * @param pathName
     * @param types
     */
    public AuthRestPath(String pathName, RestUser.AUTHTYPE... types) {
        super(pathName);
        this.types = types;
    }

    /**
     *
     * @param conversion
     * @param pathData
     * @return
     */
    @Override
    protected boolean checkPath(Conversion conversion, String pathData) {
        if (conversion.getRequest().getUser() == null) {
            return false;
        }
        if (conversion.getRequest().getUser().isAuthenticated() == false) {
            return false;
        }
        if (conversion.getRequest().getUser() instanceof RestUser) {
            for (RestUser.AUTHTYPE type : types) {
                if (type.equals(((RestUser) conversion.getRequest().getUser()).getAuthType())) {
                    return true;
                }
            }
        }
        return false;
    }

}
