/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.Json;
import at.itopen.simplerest.conversion.Conversion;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 *
 * @author roland
 * @param <T>
 */
public abstract class JsonPostEndpoint<T> extends PostEndpoint {

    Class genericType = null;
    T data;

    /**
     *
     * @param endpointName
     */
    public JsonPostEndpoint(String endpointName) {
        super(endpointName);
        Type sooper = getClass().getGenericSuperclass();
        genericType = (Class) ((ParameterizedType) sooper).getActualTypeArguments()[0];
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
    public void CallEndpoint(Conversion conversion, Map<String, String> UrlParameter) {
        if (conversion.getRequest().getContentData() != null) {
            data = (T) Json.fromString(conversion.getRequest().getContentData(), genericType);
        }
        super.CallEndpoint(conversion, UrlParameter); //To change body of generated methods, choose Tools | Templates.
    }

}
