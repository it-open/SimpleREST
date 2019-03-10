/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import at.itopen.simplerest.RestHttpRequestDispatchHandler;
import at.itopen.simplerest.client.RestBuilder;
import at.itopen.simplerest.client.RestResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public final class LoadBalancer {

    private final LoadBalancerConfig config;
    private final RestBuilder restBuilder = new RestBuilder();
    private List<String> undiscovered = new ArrayList<>();
    private boolean available = true;
    private final Services services;

    /**
     *
     * @param config
     */
    public LoadBalancer(LoadBalancerConfig config) {
        this.config = config;
        services = new Services(this);
        getServices().setRating(config.getServiceRating());
        new Thread("1s Service Checker") {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        if (!isAvailable()) {
                            break;
                        }
                        serviceChecker();
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        Logger.getLogger(LoadBalancer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }.start();

        new Thread("Service State Checker") {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        if (!isAvailable()) {
                            break;
                        }
                        serviceStatecheck();
                        Thread.sleep(10);
                    } catch (Exception ex) {
                        Logger.getLogger(LoadBalancer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }.start();
        config.getRestHttpServer().getRootEndpoint().addSubPath("loadbalancer").addSubPath(":security").addRestEndpoint(new RestDiscover("remote"));
        config.getRestHttpServer().getRootEndpoint().addSubPath("loadbalancer").addRestEndpoint(new RestStatus("status"));

    }

    /**
     *
     * @return
     */
    public Services getServices() {
        return services;
    }

    /**
     *
     * @param url
     */
    public void addUndiscovered(String url) {
        undiscovered.add(url);
    }

    /**
     *
     * @return
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     *
     * @param available
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     *
     * @return
     */
    public LoadBalancerConfig getConfig() {
        return config;
    }

    /**
     *
     */
    public void serviceStatecheck() {
        for (Service service : getServices().getAllServices()) {
            long time = (System.currentTimeMillis() - service.getLastseen()) / 1000;
            Service.SERVICE_STATUS status = Service.SERVICE_STATUS.ACTIVE;
            if (time > config.getService_stale_seconds()) {
                status = Service.SERVICE_STATUS.STALE;
            }
            if (time > config.getService_gone_seconds()) {
                status = Service.SERVICE_STATUS.GONE;
            }
            if (time > (config.getService_gone_seconds() * 2)) {
                getServices().removeService(service);
            }
            service.setStatus(status);
            service.setRating(getServices().getRating().rate(service));
        }
    }

    /**
     *
     */
    public void serviceChecker() {
        if (!undiscovered.isEmpty()) {
            List<String> discover = undiscovered;
            undiscovered = new ArrayList<>();
            for (String url : discover) {
                checkService(url);
            }
        }

        if (getServices().isEmpty()) {
            config.getInitialDiscoveryUrl().forEach((initial) -> {
                checkService(initial);
            });
        } else {
            for (Service service : getServices().getAllServices()) {
                if (!service.getStatus().equals(Service.SERVICE_STATUS.ACTIVE)) {
                    checkService(service.getBaseurl());
                    continue;
                }
                if ((service.getLastseen() + config.getService_recheck_seconds()) > System.currentTimeMillis()) {
                    checkService(service.getBaseurl());
                }
            }
        }
    }

    /**
     *
     * @param url
     */
    public void checkService(String url) {
        try {
            if (!url.endsWith("/")) {
                url += "/";
            }
            String key = getConfig().getServiceid();
            if (getConfig().getSharedSecret() != null) {
                key = AES.encrypt(getConfig().getSharedSecret(), key.substring(0, 15), key);
            }
            url += "loadbalancer/" + key + "/remote";
            RestResponse response = restBuilder.POST(url).setJson(RestHttpRequestDispatchHandler.getJSON_CONVERTER().writeValueAsString(RestDiscoverQuestion.makeQuestion(this))).work();
            if (response == null) {
                return;
            }
            if (response.getStatusCode() == 200) {
                RestDiscoverAnswer answer = RestHttpRequestDispatchHandler.getJSON_CONVERTER().readValue(response.getDataAsString(), RestDiscoverAnswer.class);
                long timediff = System.currentTimeMillis() - answer.getTimestamp();

                for (RestService restService : answer.getServices()) {
                    if (restService.getId().equals(config.getServiceid())) {
                        continue;
                    }
                    Service service = getServices().getServiceById(restService.getId());
                    boolean isnew = false;
                    if (service == null) {
                        service = new Service();
                        isnew = true;
                        service.setId(restService.getId());
                        service.setBaseurl(restService.getBaseurl());
                    }
                    service.setType(restService.getType());
                    long time = (restService.getLastseen() - timediff);
                    if (time > service.getLastseen()) {
                        service.setInfo(restService.getInfo());
                        service.setLastseen(time);
                    }
                    if (isnew) {
                        getServices().addService(service);
                    }

                }

            }
        } catch (java.net.ConnectException ex) {
            // No Connection to Host. Could be normal
        } catch (java.net.SocketException ex) {
            // Socket gone. Could be normal
        } catch (IOException ex) {
            Logger.getLogger(LoadBalancer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}