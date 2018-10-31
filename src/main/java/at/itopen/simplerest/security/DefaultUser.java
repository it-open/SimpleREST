/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.security;

/**
 *
 * @author roland
 */
public class DefaultUser extends BasicUser implements BasicAuthUser,JwtAuthUser {

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
    public void setAuth(String name, String password) {
        this.name=name;
        this.password=password;
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
     * @param Id
     * @param issuer
     * @param Subject
     */
    @Override
    public void setJwtAuth(String Id, String issuer, String Subject) {
        this.name=name;
        this.id=Id;
        this.subject=Subject;
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
