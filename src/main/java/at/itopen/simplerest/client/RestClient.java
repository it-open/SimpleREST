/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

import at.itopen.simplerest.Json;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

/**
 *
 * @author roland
 */
public class RestClient {

    private String url;
    private Map<String, String> params = new HashMap<>();

    /**
     *
     */
    public static enum REST_METHOD {

        /**
         *
         */
        GET,
        /**
         *
         */
        PUT,
        /**
         *
         */
        POST,
        /**
         *
         */
        DELETE
    };
    private REST_METHOD method;
    private String json = null;
    private Map<String, RestFile> files = new HashMap<>();
    private boolean ignoreSSLErrors = false;

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

    /**
     *
     * @return
     */
    public Map<String, RestFile> getFiles() {
        return files;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     *
     * @return
     */
    public String getJson() {
        return json;
    }

    /**
     *
     * @return
     */
    public REST_METHOD getMethod() {
        return method;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getParams() {
        return params;
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
     * @param url
     */
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

    public RestClient setIgnoreSSLErrors(boolean ignoreSSLErrors) {
        this.ignoreSSLErrors = ignoreSSLErrors;
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

    /**
     *
     * @param object
     * @return
     */
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

    public RestClient authBasic(String user, String pass) {

        headers.put("Authorization", "Basic " + Base64.encodeBase64String((user + ":" + pass).getBytes()));
        return this;
    }

    public RestClient authKey(String key) {

        headers.put("Authorization", "Bearer " + key);
        return this;
    }

    public CloseableHttpClient getAllSSLClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};

        HttpClientBuilder builder = HttpClientBuilder.create();
        RegistryBuilder registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();

        SSLContext context = SSLContext.getInstance("SSL");
        if (ignoreSSLErrors) {
            context.init(null, trustAllCerts, null);
        } else {
            context.init(null, null, null);
        }
        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(context, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        builder.setSSLSocketFactory(sslConnectionFactory);

        registryBuilder.register("https", sslConnectionFactory);

        PlainConnectionSocketFactory plainConnectionSocketFactory = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainConnectionSocketFactory);

        Registry<ConnectionSocketFactory> registry = registryBuilder.build();

        HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);

        builder.setConnectionManager(ccm);

        return builder.build();

    }

    private RestResponse work() throws Exception {

        CloseableHttpClient httpClient = getAllSSLClient();
        CloseableHttpResponse response = null;
        RestResponse rr = null;
        long start = System.nanoTime();
        try {

            if (method.equals(REST_METHOD.POST) || method.equals(REST_METHOD.PUT)) {

                HttpPost postRequest = new HttpPost(url);
                HttpEntity entity;
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    postRequest.setHeader(header.getKey(), header.getValue());
                }

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
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    getRequest.setHeader(header.getKey(), header.getValue());
                }

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
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    getRequest.setHeader(header.getKey(), header.getValue());
                }

                response = httpClient.execute(getRequest);

            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        } finally {
            rr = new RestResponse(response, start);
            httpClient.close();
        }
        return rr;
    }

    /**
     *
     * @param retryonfail
     * @return
     */
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

    /**
     *
     * @param retryonfail
     */
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
