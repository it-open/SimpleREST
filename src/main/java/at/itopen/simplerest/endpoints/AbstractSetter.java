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
 */
public abstract class AbstractSetter<OBJECT> {

    public AbstractSetter() {
    }

    @JsonIgnore
    private Conversion conversion;

    public Conversion getConversion() {
        return conversion;
    }

    public AbstractSetter<OBJECT> setConversion(Conversion conversion) {
        this.conversion = conversion;
        return this;
    }

    abstract public void internalSetData(OBJECT data);

}
