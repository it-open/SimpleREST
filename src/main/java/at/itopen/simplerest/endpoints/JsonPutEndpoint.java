/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.RestHttpRequestDispatchHandler;
import at.itopen.simplerest.conversion.Conversion;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public abstract class JsonPutEndpoint<T> extends PutEndpoint{

    Class genericType=null;
    T data;
    
    public JsonPutEndpoint(String endpointName) {
        super(endpointName);
        Type sooper = getClass().getGenericSuperclass();
        genericType = (Class)((ParameterizedType)sooper).getActualTypeArguments()[ 0 ];
    }

    public T getData() {
        return data;
    }
    
    
    @Override
    public void CallEndpoint(Conversion conversion, Map<String,String> UrlParameter) {
        if (conversion.getRequest().getContentData()!=null)
        {
            try {
                data=(T)RestHttpRequestDispatchHandler.getJSON_CONVERTER().readValue(conversion.getRequest().getContentData(), genericType);
            } catch (IOException ex) {
                Logger.getLogger(JsonPutEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.CallEndpoint(conversion, UrlParameter); //To change body of generated methods, choose Tools | Templates.
    }
 

}
