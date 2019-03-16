/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import at.itopen.simplerest.Json;
import at.itopen.simplerest.client.RestClient;
import at.itopen.simplerest.client.RestResponse;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.JsonPostEndpoint;
import at.itopen.simplerest.endpoints.PostEndpoint;
import at.itopen.simplerest.microservice.client.LoadBalancedRestClient;
import at.itopen.simplerest.microservice.message.Guarantor;
import at.itopen.simplerest.microservice.message.MessageStatus;
import at.itopen.simplerest.microservice.message.ServiceIpPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public final class LoadBalancer {

    private final LoadBalancerConfig config;
    private List<String> undiscovered = new ArrayList<>();
    private boolean available = true;
    private final Services services;
    private final Guarantor guarantor;

    /**
     *
     * @param config
     */
    public LoadBalancer(LoadBalancerConfig config) {
        this.config = config;
        services = new Services(this);
        guarantor = new Guarantor(this);

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
        config.getRestHttpServer().getRootEndpoint().addSubPath("loadbalancer").addSubPath(new ServiceIpPath("guarantor")).addRestEndpoint(new PostEndpoint("introduce") {
            @Override
            public void Call(Conversion conversion, Map<String, String> UrlParameter) {
                getGuarantor().introduced(conversion.getRequest().getContentData());
            }
        });
        config.getRestHttpServer().getRootEndpoint().addSubPath("loadbalancer").addSubPath(new ServiceIpPath("guarantor")).addRestEndpoint(new JsonPostEndpoint<MessageStatus>("state") {
            @Override
            public void Call(Conversion conversion, Map<String, String> UrlParameter) {
                getGuarantor().status(getData());
            }
        });

    }

    /**
     *
     * @param url
     * @param method
     * @return
     */
    public LoadBalancedRestClient RestClient(String url, RestClient.REST_METHOD method) {
        return new LoadBalancedRestClient(config.getRestHttpServer(), url, method);
    }

    /**
     *
     * @return
     */
    public Guarantor getGuarantor() {
        return guarantor;
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
                if (service == null) {
                    continue;
                }
                if (service.getStatus() == null) {
                    continue;
                }
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
     * @param restDiscoverQuestion
     * @return
     */
    public String encryptUrl(RestDiscoverQuestion restDiscoverQuestion) {

        String key = getConfig().getServiceid();
        String initv = Encryption.correctINITV("" + restDiscoverQuestion.getTimestamp());

        if (getConfig().getSharedSecret() != null) {
            key = Encryption.AESencrypt(getConfig().getSharedSecret(), initv, key);
        }

        return key;
    }

    /**
     *
     * @param restDiscoverQuestion
     * @param key
     * @return
     */
    public String decryptUrl(RestDiscoverQuestion restDiscoverQuestion, String key) {

        String initv = Encryption.correctINITV("" + restDiscoverQuestion.getTimestamp());
        if (getConfig().getSharedSecret() != null) {
            key = Encryption.AESdecrypt(getConfig().getSharedSecret(), initv, key);
        }

        return key;
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
            RestDiscoverQuestion restDiscoverQuestion = buildRestDiscoverQuestion();
            String key = encryptUrl(restDiscoverQuestion);

            url += "loadbalancer/" + key + "/remote";

            RestResponse response = new RestClient(url, RestClient.REST_METHOD.POST).setJson(Json.toString(restDiscoverQuestion)).toSingle(false);
            if (response == null) {
                return;
            }
            if (response.getStatusCode() == 200) {
                RestDiscoverAnswer answer = Json.fromString(response.getDataAsString(), RestDiscoverAnswer.class);
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

        } catch (Exception ex) {
            Logger.getLogger(LoadBalancer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public RestDiscoverQuestion buildRestDiscoverQuestion() {
        return new RestDiscoverQuestion(getConfig().getServiceid(), getConfig().getBaseurl());
    }

    /**
     *
     * @return
     */
    public RestDiscoverAnswer buildRestDiscoverAnswer() {

        Service me = new Service();
        me.setLastseen(System.currentTimeMillis());
        me.setInfo(SystemCheck.getInstance().getaktSystemInfoData());
        me.setBaseurl(getConfig().getBaseurl());
        me.setType(getConfig().getServicetype());
        me.setId(getConfig().getServiceid());
        RestDiscoverAnswer rda = new RestDiscoverAnswer();
        if (isAvailable()) {
            rda.services.add(RestService.fromService(me));
        }
        getServices().getAllActiveServices().forEach((s) -> {
            rda.services.add(RestService.fromService(s));
        });

        rda.timestamp = System.currentTimeMillis();
        return rda;
    }

}
