/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author roland
 * @param <T>
 */
public abstract class AbstractRestFunction<T> {

    private final Map<String, String> headers = new HashMap<>();

    private final String url;
    DefaultHttpClient httpClient;

    /**
     *
     * @param url
     * @param headers
     * @param httpClient
     */
    public AbstractRestFunction(String url, Map<String, String> headers, DefaultHttpClient httpClient) {
        this.url = url;
        this.headers.putAll(headers);
        this.httpClient = httpClient;
    }

    /**
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param key
     * @param Value
     * @return
     */
    public T setHeader(String key, String Value) {
        headers.put(key, Value);
        return (T) this;
    }

    /**
     *
     * @return
     */
    protected DefaultHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public abstract RestResponse work() throws IOException;

}
