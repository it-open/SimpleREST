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

    public Conversion conversion() {
        return conversion;
    }

    public AbstractSetter<OBJECT> ConversionSet(Conversion conversion) {
        this.conversion = conversion;
        return this;
    }

    abstract public void internalSetData(OBJECT data);

}
