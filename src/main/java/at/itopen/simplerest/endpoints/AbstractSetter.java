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
 * @param <OBJECT>
 */
public abstract class AbstractSetter<OBJECT> {

    /**
     *
     */
    public AbstractSetter() {
    }

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
    public AbstractSetter<OBJECT> setConversion(Conversion conversion) {
        this.conversion = conversion;
        return this;
    }

    /**
     *
     * @param data
     */
    abstract public void internalSetData(OBJECT data);

}
