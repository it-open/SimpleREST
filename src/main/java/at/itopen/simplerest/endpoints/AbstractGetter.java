/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author roland
 * @param <O> Object
 */
public abstract class AbstractGetter<O> {

    @JsonIgnore
    private Conversion conversion;

    /**
     *
     * @return
     */
    public Conversion getConversion() {
        return conversion;
    }

    /**
     *
     * @param conversion
     * @return
     */
    public AbstractGetter<O> setConversion(Conversion conversion) {
        this.conversion = conversion;
        return this;
    }

    /**
     *
     * @param data
     */
    abstract public void internalGetData(O data);

}
