/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author roland
 */
public class RestBuilder {

    private final Map<String, String> headers = new HashMap<>();

    DefaultHttpClient httpClient = new DefaultHttpClient();

    /**
     *
     * @param key
     * @param Value
     */
    public void setHeader(String key, String Value) {
        headers.put(key, Value);
    }

    /**
     *
     * @param Url
     * @return
     */
    public RestGet GET(String Url) {
        return new RestGet(Url, headers, httpClient);
    }

    /**
     *
     * @param Url
     * @return
     */
    public RestPost POST(String Url) {
        return new RestPost(Url, headers, httpClient);
    }

    /**
     *
     * @param Url
     * @return
     */
    public RestDelete DELETE(String Url) {
        return new RestDelete(Url, headers, httpClient);
    }

    /**
     *
     */
    public void shutdown() {
        httpClient.close();
    }

}
