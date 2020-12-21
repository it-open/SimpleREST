/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.security;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.Cookie;

/**
 *
 * @author roland
 */
public interface CookieAuthUser {

    /**
     *
     * @param conversion
     * @param cookie
     */
    void setCookieAuth(Conversion conversion, Cookie cookie);

}
