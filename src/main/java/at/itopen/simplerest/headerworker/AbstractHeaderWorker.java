/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Conversion;

/**
 *
 * @author roland
 */
public abstract class AbstractHeaderWorker {

    /**
     *
     * @param conversion
     */
    public abstract void work(Conversion conversion);

}
