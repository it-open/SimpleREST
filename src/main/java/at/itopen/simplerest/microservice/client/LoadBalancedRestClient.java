/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.client;

import at.itopen.simplerest.RestHttpServer;
import at.itopen.simplerest.client.RestClient;
import at.itopen.simplerest.client.RestClient.RESTMETHOD;
import at.itopen.simplerest.client.RestFile;
import at.itopen.simplerest.client.RestResponse;
import at.itopen.simplerest.microservice.loadbalancer.Service;
import at.itopen.simplerest.microservice.message.MessageRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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
public class LoadBalancedRestClient extends RestClient {

    RestHttpServer restHttpServer;

    /**
     *
     * @param restHttpServer
     * @param url
     * @param method
     */
    public LoadBalancedRestClient(RestHttpServer restHttpServer, String url, RESTMETHOD method) {
        super(url, method);
        this.restHttpServer = restHttpServer;

    }

    private RestResponse work(Service service, String url) throws Exception {
        String serviceurl = service.getBaseurl();
        restHttpServer.getLoadBalancer().getServices().logAccess(service);
        if (!serviceurl.endsWith("/")) {
            serviceurl += "/";
        }
        DefaultHttpClient httpClient = new DefaultHttpClient();
        CloseableHttpResponse response = null;
        long start = System.nanoTime();
        try {

            if (getMethod().equals(RESTMETHOD.POST) || getMethod().equals(RESTMETHOD.PUT)) {

                HttpPost postRequest = new HttpPost(serviceurl + url);
                HttpEntity entity;

                if (getJson() == null) {
                    MultipartEntityBuilder data = MultipartEntityBuilder.create()
                            .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    getParams().entrySet().forEach((e) -> {
                        data.addTextBody(e.getKey(), e.getValue());
                    });
                    getFiles().entrySet().forEach((e) -> {
                        RestFile file = e.getValue();
                        data.addBinaryBody(e.getKey(), file.getData(), file.getContentType(), file.getName());
                    });
                    entity = data.build();
                } else {
                    entity = new StringEntity(
                            getJson(),
                            ContentType.APPLICATION_JSON);
                }

                postRequest.setEntity(entity);
                response = httpClient.execute(postRequest);

            }

            if (getMethod().equals(RESTMETHOD.GET)) {

                StringBuilder out = new StringBuilder();
                getParams().entrySet().forEach((e) -> {
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

            if (getMethod().equals(RESTMETHOD.DELETE)) {

                StringBuilder out = new StringBuilder();
                getParams().entrySet().forEach((e) -> {
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
            return null;
        } finally {
            httpClient.close();
        }
        return new RestResponse(response, start);
    }

    private Service serviceSelect(String servicetype) {
        List<Service> all = restHttpServer.getLoadBalancer().getServices().getServiceType(servicetype);
        if (all.isEmpty()) {
            return null;
        }
        double rmax = 0;
        rmax = all.stream().map((service) -> service.getRating()).reduce(rmax, (accumulator, item) -> accumulator + item);
        double rseek = Math.random() * rmax;
        for (Service service : all) {
            rseek -= service.getRating();
            if (rseek <= 0) {
                return service;
            }
        }
        Collections.shuffle(all);
        return all.get(0);
    }

    private Service serviceid2Service(String serviceid) {
        Service service = restHttpServer.getLoadBalancer().getServices().getServiceById(serviceid);
        if (service == null) {
            return null;
        }
        return service;
    }

    /**
     *
     * @param servicetype
     * @param retryonfail
     * @return
     */
    public RestResponse toSingleService(String servicetype, boolean retryonfail) {
        while (true) {
            Service service = serviceSelect(servicetype);
            if (getUrl() != null) {
                try {
                    return work(service, getUrl());
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

    /**
     *
     * @param servicetype
     * @param retryonfail
     */
    public void toSingleServiceFireAndForget(String servicetype, boolean retryonfail) {
        new Thread("SSFF:" + getUrl()) {
            @Override
            public void run() {
                while (true) {
                    Service service = serviceSelect(servicetype);
                    if (getUrl() != null) {
                        try {
                            work(service, getUrl());
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

    /**
     *
     * @param serviceid
     * @param retryonfail
     * @return
     */
    public RestResponse toDistinctService(String serviceid, boolean retryonfail) {
        while (true) {
            Service service = serviceid2Service(serviceid);
            if (getUrl() != null) {
                try {
                    return work(service, getUrl());
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

    /**
     *
     * @param serviceid
     * @param retryonfail
     */
    public void toDistinctServiceFireAndForget(String serviceid, boolean retryonfail) {
        new Thread("DSFF:" + getUrl()) {
            @Override
            public void run() {
                while (true) {
                    Service service = serviceid2Service(serviceid);
                    if (getUrl() != null) {
                        try {
                            work(service, getUrl());
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

    /**
     *
     * @param servicetype
     * @param retryonfail
     */
    public void toAllServicesFireAndForget(String servicetype, boolean retryonfail) {
        new Thread("SSFF:" + getUrl()) {
            @Override
            public void run() {
                for (Service service : restHttpServer.getLoadBalancer().getServices().getAllActiveServices()) {
                    if (!service.getType().equals(servicetype)) {
                        continue;
                    }
                    while (true) {

                        if (getUrl() != null) {
                            try {

                                work(service, getUrl());
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

    /**
     *
     * @param retryonfail
     */
    public void toAllServicesFireAndForget(boolean retryonfail) {
        new Thread("SSFF:" + getUrl()) {
            @Override
            public void run() {
                for (Service service : restHttpServer.getLoadBalancer().getServices().getAllActiveServices()) {
                    while (true) {

                        if (getUrl() != null) {
                            try {

                                work(service, getUrl());
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

    /**
     *
     * @param servicetype
     * @param message
     * @return
     */
    public String sendMessagetoQueue(String servicetype, MessageRequest<? extends Object> message) {
        if (getUrl() == null) {
            return null;
        }
        message.setTargetUrl(getUrl());
        message.setSenderId(restHttpServer.getLoadBalancer().getConfig().getServiceid());
        if (getMethod().equals(RESTMETHOD.POST) || getMethod().equals(RESTMETHOD.PUT)) {

            message.setHeaders(getHeaders());

            while (true) {
                Service targetservice = serviceSelect(servicetype);
                message.getGuarantorServiceIds().clear();
                message.getGuarantorServiceIds().add(restHttpServer.getLoadBalancer().getConfig().getServiceid());
                List<Service> services = restHttpServer.getLoadBalancer().getServices().getAllActiveServices();
                Collections.shuffle(services);
                for (Service service : services) {
                    if (service.getId().equals(targetservice.getId())) {
                        continue;
                    }
                    message.getGuarantorServiceIds().add(service.getId());
                    if (message.getGuarantorServiceIds().size() == 2) {
                        break;
                    }
                }
                setJson(message);
                try {
                    RestResponse res = work(targetservice, getUrl());
                    if (res.getStatusCode() == 200) {
                        setUrl("/loadbalancer/guarator/indroduce");
                        message.getGuarantorServiceIds().forEach((id) -> {
                            if (!id.equals(restHttpServer.getLoadBalancer().getConfig().getServiceid())) {
                                toDistinctServiceFireAndForget(id, false);
                            }
                        });
                        return message.getMessageid();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(LoadBalancedRestClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            throw new RuntimeException("Message must be POST or PUT");
        }
    }

}
