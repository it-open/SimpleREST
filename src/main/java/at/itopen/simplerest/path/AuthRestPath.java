/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

/**
 *
 * @author roland
 */
public class AuthRestPath extends RestPath implements AuthenticatedRestPath{
    
    public AuthRestPath(String pathName) {
        super(pathName);
    }
    
}
