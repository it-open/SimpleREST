/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author roland
 */
public class RestDelete extends AbstractRestFunction<RestDelete> {

    Map<String, String> urlParams = new HashMap<>();

    /**
     *
     * @param url
     * @param headers
     * @param httpClient
     */
    public RestDelete(String url, Map<String, String> headers, DefaultHttpClient httpClient) {
        super(url, headers, httpClient);
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public RestDelete setParameter(String key, String value) {
        urlParams.put(key, value);
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public String getUrl() {
        StringBuilder out = new StringBuilder();
        urlParams.entrySet().forEach((e) -> {
            if (out.length() == 0) {
                out.append("?");
            } else {
                out.append("&");
            }
            out.append(e.getKey()).append("=").append(e.getValue());
        });
        return super.getUrl() + out.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public RestResponse work() throws IOException {

        HttpDelete getRequest = new HttpDelete(getUrl());

        HttpResponse response = getHttpClient().execute(getRequest);
        return new RestResponse(response);

    }

}
