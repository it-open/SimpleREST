/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

import at.itopen.simplerest.RestHttpServer;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author roland
 */
public class LoadBalancedRestBuilder {

    RestHttpServer restHttpServer;

    /**
     *
     * @param restHttpServer
     */
    public LoadBalancedRestBuilder(RestHttpServer restHttpServer) {
        this.restHttpServer = restHttpServer;
    }

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
     * @param service
     * @param Url
     * @return
     */
    public RestGet GET(String service, String Url) {
        if (Url.startsWith("/")) {
            Url = Url.substring(1);
        }
        Url = service2url(service) + Url;
        return new RestGet(Url, headers, httpClient);
    }

    /**
     *
     * @param service
     * @param Url
     * @return
     */
    public RestPost POST(String service, String Url) {
        if (Url.startsWith("/")) {
            Url = Url.substring(1);
        }
        Url = service2url(service) + Url;
        return new RestPost(Url, headers, httpClient);
    }

    /**
     *
     * @param service
     * @param Url
     * @return
     */
    public RestDelete DELETE(String service, String Url) {
        if (Url.startsWith("/")) {
            Url = Url.substring(1);
        }
        Url = service2url(service) + Url;
        return new RestDelete(Url, headers, httpClient);
    }

    private String service2url(String service) {
        String baseUrl = restHttpServer.getLoadBalancer().getServices().getBaseUrlsForServiceType(service);
        if (baseUrl != null) {
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            return baseUrl;
        } else {
            return null;
        }
    }

    /**
     *
     */
    public void shutdown() {
        httpClient.close();
    }

}
