/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.security;

import at.itopen.simplerest.conversion.Cookie;
import at.itopen.simplerest.conversion.Request;

/**
 *
 * @author roland
 */
public interface CookieAuthUser {

    /**
     *
     * @param request
     * @param cookie
     */
    public void setCookieAuth(Request request, Cookie cookie);

}
