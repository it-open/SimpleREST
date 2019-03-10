/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author roland
 */
public class RestPost extends AbstractRestFunction<RestPost> {

    Map<String, String> urlParams = new HashMap<>();
    Map<String, RestFile> files = new HashMap<>();
    String json = null;

    /**
     *
     * @param url
     * @param headers
     * @param httpClient
     */
    public RestPost(String url, Map<String, String> headers, DefaultHttpClient httpClient) {
        super(url, headers, httpClient);
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public RestPost setParameter(String key, String value) {
        urlParams.put(key, value);
        return this;
    }

    /**
     *
     * @param name
     * @param file
     * @return
     */
    public RestPost addFile(String name, RestFile file) {
        files.put(name, file);
        return this;
    }

    /**
     *
     * @param json
     * @return
     */
    public RestPost setJson(String json) {
        this.json = json;
        return this;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public RestResponse work() throws IOException {

        HttpPost postRequest = new HttpPost(getUrl());
        HttpEntity entity = null;

        if (json == null) {
            MultipartEntityBuilder data = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            urlParams.entrySet().forEach((e) -> {
                data.addTextBody(e.getKey(), e.getValue());
            });
            files.entrySet().forEach((e) -> {
                RestFile file = e.getValue();
                data.addBinaryBody(e.getKey(), file.getData(), file.getContentType(), file.getName());
            });
            entity = data.build();
        } else {
            entity = new StringEntity(
                    json,
                    ContentType.APPLICATION_JSON);
        }

        postRequest.setEntity(entity);
        HttpResponse response = getHttpClient().execute(postRequest);
        return new RestResponse(response);

    }

}
