/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.security;

import at.itopen.simplerest.conversion.Conversion;

/**
 *
 * @author roland
 */
public interface BasicAuthUser {

    /**
     *
     * @param conversion
     * @param name
     * @param password
     */
    public void setAuth(Conversion conversion, String name, String password);

}
