/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

import at.itopen.simplerest.Json;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
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
public class RestClient {

    String url;
    Map<String, String> params = new HashMap<>();

    public static enum REST_METHOD {
        GET, PUT, POST, DELETE
    };
    REST_METHOD method;
    String json = null;
    Map<String, RestFile> files = new HashMap<>();

    /**
     *
     * @param url
     * @param method
     */
    public RestClient(String url, REST_METHOD method) {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        this.url = url;
        this.method = method;
    }

    private final Map<String, String> headers = new HashMap<>();

    public Map<String, RestFile> getFiles() {
        return files;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getJson() {
        return json;
    }

    public REST_METHOD getMethod() {
        return method;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public RestClient setParameter(String key, String value) {
        if (json != null) {
            throw new RuntimeException("JSON already set. No Parameters allowed!");
        }
        params.put(key, value);
        return this;
    }

    /**
     *
     * @param name
     * @param file
     * @return
     */
    public RestClient addFile(String name, RestFile file) {
        if (json != null) {
            throw new RuntimeException("JSON already set. No Files allowed!");
        }
        if (method.equals(REST_METHOD.POST) || method.equals(REST_METHOD.PUT)) {
            files.put(name, file);
        } else {
            throw new RuntimeException("Files only Allowed on GET or PUT");
        }
        return this;
    }

    /**
     *
     * @param json
     * @return
     */
    public RestClient setJson(String json) {
        if (!params.isEmpty()) {
            throw new RuntimeException("Parameters already set. No JSON allowed!");
        }
        if (!files.isEmpty()) {
            throw new RuntimeException("Files already set. No JSON allowed!");
        }
        if (method.equals(REST_METHOD.POST) || method.equals(REST_METHOD.PUT)) {
            this.json = json;
        } else {
            throw new RuntimeException("No JSON allowed on GET or DELETE");
        }
        return this;
    }

    public RestClient setJson(Object object) {
        return setJson(Json.toString(object));
    }

    /**
     *
     * @param key
     * @param Value
     * @return
     */
    public RestClient setHeader(String key, String Value) {
        headers.put(key, Value);
        return this;
    }

    private RestResponse work() throws Exception {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;
        long start = System.nanoTime();
        try {

            if (method.equals(REST_METHOD.POST) || method.equals(REST_METHOD.PUT)) {

                HttpPost postRequest = new HttpPost(url);
                HttpEntity entity;

                if (json == null) {
                    MultipartEntityBuilder data = MultipartEntityBuilder.create()
                            .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    params.entrySet().forEach((e) -> {
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
                response = httpClient.execute(postRequest);

            }

            if (method.equals(REST_METHOD.GET)) {

                StringBuilder out = new StringBuilder();
                params.entrySet().forEach((e) -> {
                    if (out.length() == 0) {
                        out.append("?");
                    } else {
                        out.append("&");
                    }
                    out.append(e.getKey()).append("=").append(e.getValue());
                });
                HttpGet getRequest = new HttpGet(url + out.toString());

                response = httpClient.execute(getRequest);

            }

            if (method.equals(REST_METHOD.DELETE)) {

                StringBuilder out = new StringBuilder();
                params.entrySet().forEach((e) -> {
                    if (out.length() == 0) {
                        out.append("?");
                    } else {
                        out.append("&");
                    }
                    out.append(e.getKey()).append("=").append(e.getValue());
                });
                HttpDelete getRequest = new HttpDelete(url + out.toString());

                response = httpClient.execute(getRequest);

            }

        } catch (IOException ex) {
            return null;
        } finally {
            httpClient.close();
        }
        return new RestResponse(response, start);
    }

    public RestResponse toSingle(boolean retryonfail) {
        if (url != null) {
            while (true) {
                try {
                    return work();
                } catch (Exception ex) {
                    Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (!retryonfail) {
                    break;
                }
            }
        }
        return null;
    }

    public void toSingleFireAndForget(boolean retryonfail) {
        new Thread("SUFF:" + url) {
            @Override
            public void run() {
                if (url != null) {
                    while (true) {

                        try {
                            work();
                        } catch (Exception ex) {
                            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if (!retryonfail) {
                            break;
                        }
                    }
                }
            }
        }.start();
    }

}
