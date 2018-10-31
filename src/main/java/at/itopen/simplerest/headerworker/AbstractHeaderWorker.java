/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Request;

/**
 *
 * @author roland
 */
public abstract class AbstractHeaderWorker {
    
    /**
     *
     * @param requst
     */
    public abstract void work(Request requst);
    
}
