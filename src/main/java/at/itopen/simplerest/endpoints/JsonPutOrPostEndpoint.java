/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.RestHttpRequestDispatchHandler;
import at.itopen.simplerest.conversion.Conversion;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 * @param <T>
 */
public abstract class JsonPutOrPostEndpoint<T> extends PutOrPostEndpoint{

    Class dataClass;
    T data;
    
    
    

    
    
    /**
     *
     * @param endpointName
     * @param dataClass
     */
    public JsonPutOrPostEndpoint(String endpointName,Class dataClass) {
        super(endpointName);
        this.dataClass=dataClass;
    }

    /**
     *
     * @return
     */
    public T getData() {
        return data;
    }
    
    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    @Override
    public void CallEndpoint(Conversion conversion, Map<String,String> UrlParameter) {
        if (conversion.getRequest().getContentData()!=null)
        {
            try {
                data=(T)RestHttpRequestDispatchHandler.getJSON_CONVERTER().readValue(conversion.getRequest().getContentData(), dataClass);
            } catch (IOException ex) {
                Logger.getLogger(JsonPutOrPostEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.CallEndpoint(conversion, UrlParameter); //To change body of generated methods, choose Tools | Templates.
    }
    
   

}
