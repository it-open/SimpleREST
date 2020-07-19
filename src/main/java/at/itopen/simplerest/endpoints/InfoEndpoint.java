/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.path.RestEndpoint;
import java.util.Map;

/**
 *
 * @author roland
 */
public class InfoEndpoint extends RestEndpoint{

    /**
     *
     * @param name
     */
    public InfoEndpoint(String name) {
        super(name);
    }
    
    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    @Override
    public void Call(Conversion conversion, Map<String, String> UrlParameter) {
        conversion.getResponse().setData(conversion.getRequest());
        
    }
    
}
