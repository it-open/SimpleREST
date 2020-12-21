/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.JsonHelper;
import at.itopen.simplerest.conversion.Conversion;
import java.util.Map;

/**
 *
 * @author roland
 * @param <T> Type
 */
public abstract class JsonPutOrPostEndpoint<T> extends PutOrPostEndpoint {

    Class dataClass;
    T data;

    /**
     *
     * @param endpointName
     * @param dataClass
     */
    public JsonPutOrPostEndpoint(String endpointName, Class dataClass) {
        super(endpointName);
        this.dataClass = dataClass;
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
     * @param urlParameter
     */
    @Override
    public void CallEndpoint(Conversion conversion, Map<String, String> urlParameter) {
        if (conversion.getRequest().getContentData() != null) {
            data = (T) JsonHelper.fromString(conversion.getRequest().getContentData(), dataClass);
        }
        super.CallEndpoint(conversion, urlParameter); //To change body of generated methods, choose Tools | Templates.
    }

}
