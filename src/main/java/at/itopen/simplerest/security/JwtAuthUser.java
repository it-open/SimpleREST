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
public interface JwtAuthUser {
    
    public void setJwtAuth(String Id,String issuer,String Subject);
    
}
