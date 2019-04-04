/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.path.RestEndpoint;

/**
 *
 * @author roland
 */
public abstract class PutEndpoint extends RestEndpoint{

    /**
     *
     * @param endpointName
     */
    public PutEndpoint(String endpointName) {
        super(endpointName);
    }

    /**
     *
     * @param conversion
     * @return
     */
    @Override
    protected boolean checkEndpoint(Conversion conversion) {
        if ("PUT".equals(conversion.getRequest().getMethod()))
            return super.checkEndpoint(conversion); //To change body of generated methods, choose Tools | Templates.
        else
            return false;
    }

   

}
