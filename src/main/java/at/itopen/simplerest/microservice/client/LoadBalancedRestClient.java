/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.client;

import at.itopen.simplerest.RestHttpServer;
import at.itopen.simplerest.client.RestClient.REST_METHOD;
import at.itopen.simplerest.client.RestFile;
import at.itopen.simplerest.microservice.loadbalancer.Service;
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
public class LoadBalancedRestClient {

    RestHttpServer restHttpServer;
    String url;
    Map<String, String> params = new HashMap<>();

    REST_METHOD method;
    String json = null;
    Map<String, RestFile> files = new HashMap<>();

    /**
     *
     * @param restHttpServer
     * @param url
     * @param method
     */
    public LoadBalancedRestClient(RestHttpServer restHttpServer, String url, REST_METHOD method) {
        this.restHttpServer = restHttpServer;
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        this.url = url;
        this.method = method;
    }

    private final Map<String, String> headers = new HashMap<>();

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public LoadBalancedRestClient setParameter(String key, String value) {
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
    public LoadBalancedRestClient addFile(String name, RestFile file) {
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
    public LoadBalancedRestClient setJson(String json) {
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
     * @param key
     * @param Value
     * @return
     */
    public LoadBalancedRestClient setHeader(String key, String Value) {
        headers.put(key, Value);
        return this;
    }

    private RestResponse work(Service service, String url) throws Exception {
        String serviceurl = service.getBaseurl();
        restHttpServer.getLoadBalancer().getServices().logAccess(service);
        if (!serviceurl.endsWith("/")) {
            serviceurl += "/";
        }
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;
        try {

            if (method.equals(REST_METHOD.POST) || method.equals(REST_METHOD.PUT)) {

                HttpPost postRequest = new HttpPost(serviceurl + url);
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
            restHttpServer.getLoadBalancer().getServices().serviceError(service);
            Logger.getLogger(LoadBalancedRestClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception(ex);
        } finally {
            httpClient.close();
        }
        return new RestResponse(response);
    }

    private Service serviceSelect(String servicetype) {
        Service service = restHttpServer.getLoadBalancer().getServices().getRandomForServiceType(servicetype);
        return service;
    }

    private Service serviceid2Service(String serviceid) {
        Service service = restHttpServer.getLoadBalancer().getServices().getServiceById(serviceid);
        if (service == null) {
            return null;
        }
        return service;
    }

    public RestResponse toSingleService(String servicetype, boolean retryonfail) {
        while (true) {
            Service service = serviceSelect(servicetype);
            if (url != null) {
                try {
                    return work(service, url);
                } catch (Exception ex) {
                    Logger.getLogger(LoadBalancedRestClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (!retryonfail) {
                break;
            }
        }
        return null;
    }

    public void toSingleServiceFireAndForget(String servicetype, boolean retryonfail) {
        new Thread("SSFF:" + url) {
            @Override
            public void run() {
                while (true) {
                    Service service = serviceSelect(servicetype);
                    if (url != null) {
                        try {
                            work(service, url);
                        } catch (Exception ex) {
                            Logger.getLogger(LoadBalancedRestClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (!retryonfail) {
                        break;
                    }
                }
            }
        }.start();
    }

    public RestResponse toDistinctService(String serviceid, boolean retryonfail) {
        while (true) {
            Service service = serviceid2Service(serviceid);
            if (url != null) {
                try {
                    return work(service, url);
                } catch (Exception ex) {
                    Logger.getLogger(LoadBalancedRestClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (!retryonfail) {
                break;
            }
        }
        return null;
    }

    public void toDistinctServiceFireAndForget(String serviceid, boolean retryonfail) {
        new Thread("DSFF:" + url) {
            @Override
            public void run() {
                while (true) {
                    Service service = serviceid2Service(serviceid);
                    if (url != null) {
                        try {
                            work(service, url);
                        } catch (Exception ex) {
                            Logger.getLogger(LoadBalancedRestClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (!retryonfail) {
                        break;
                    }
                }
            }
        }.start();
    }

    public void toAllServicesFireAndForget(String servicetype, boolean retryonfail) {
        new Thread("SSFF:" + url) {
            @Override
            public void run() {
                for (Service service : restHttpServer.getLoadBalancer().getServices().getAllActiveServices()) {
                    if (!service.getType().equals(servicetype)) {
                        continue;
                    }
                    while (true) {

                        if (url != null) {
                            try {

                                work(service, url);
                                break;
                            } catch (Exception ex) {
                                Logger.getLogger(LoadBalancedRestClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (!retryonfail) {
                            break;
                        }
                    }
                }
            }
        }.start();
    }

    public void toAllServicesFireAndForget(boolean retryonfail) {
        new Thread("SSFF:" + url) {
            @Override
            public void run() {
                for (Service service : restHttpServer.getLoadBalancer().getServices().getAllActiveServices()) {
                    while (true) {

                        if (url != null) {
                            try {

                                work(service, url);
                                break;
                            } catch (Exception ex) {
                                Logger.getLogger(LoadBalancedRestClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
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
