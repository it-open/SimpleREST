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
public interface JwtAuthUser {

    /**
     *
     * @param conversion
     * @param id
     * @param issuer
     * @param subject
     */
    void setJwtAuth(Conversion conversion, String id, String issuer, String subject);

}
