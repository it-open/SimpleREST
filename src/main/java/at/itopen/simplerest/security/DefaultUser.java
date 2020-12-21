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
public class DefaultUser extends BasicUser implements BasicAuthUser, JwtAuthUser {

    private String id;
    private String subject;
    private String name;
    private String password;

    /**
     *
     * @param name
     * @param password
     */
    @Override
    public void setAuth(Conversion conversion, String name, String password) {
        this.name = name;
        this.password = password;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param id
     * @param issuer
     * @param subject
     */
    @Override
    public void setJwtAuth(Conversion conversion, String id, String issuer, String subject) {
        this.name = issuer;
        this.id = id;
        this.subject = subject;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getSubject() {
        return subject;
    }

}
