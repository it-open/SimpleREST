/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

/**
 *
 * @author roland
 */
public abstract class AbstractGetter<OBJECT> {

    public AbstractGetter() {
    }

    abstract public void internalGetData(OBJECT data);

}
