/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import at.itopen.simplerest.RestHttpServer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author roland
 */
public class LoadBalancerConfig {

    private String baseurl;
    private final String serviceid;
    private String servicetype;
    private AbstratctServiceRating serviceRating;
    private final RestHttpServer restHttpServer;
    private final List<String> initialDiscoveryUrl = new ArrayList<>();
    private String sharedSecret = null;

    private int service_recheck_seconds = 4;
    private int service_stale_seconds = 6;
    private int service_gone_seconds = 10;

    /**
     *
     * @param restHttpServer
     * @param baseurl
     * @param servicetype
     */
    public LoadBalancerConfig(RestHttpServer restHttpServer, String baseurl, String servicetype) {
        this.serviceid = UUID.randomUUID().toString();
        this.restHttpServer = restHttpServer;
        baseurl = baseurl.replace("<PORT>", "" + restHttpServer.getPort());
        baseurl = baseurl.replace("<IP>", SystemCheck.getInstance().getaktSystemInfoData().getNetIp());
        this.baseurl = baseurl;
        this.servicetype = servicetype;
        this.serviceRating = new BaseServiceRating();
    }

    /**
     *
     * @return
     */
    public List<String> getInitialDiscoveryUrl() {
        return initialDiscoveryUrl;
    }

    /**
     *
     * @param url
     */
    public void addInitialDiscoverUrl(String url) {
        initialDiscoveryUrl.add(url);
    }

    /**
     *
     * @return
     */
    public String getBaseurl() {
        return baseurl;
    }

    /**
     *
     * @return
     */
    public AbstratctServiceRating getServiceRating() {
        return serviceRating;
    }

    /**
     *
     * @return
     */
    public String getServiceid() {
        return serviceid;
    }

    /**
     *
     * @return
     */
    public String getServicetype() {
        return servicetype;
    }

    /**
     *
     * @param baseurl
     */
    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    /**
     *
     * @param serviceRating
     */
    public void setServiceRating(AbstratctServiceRating serviceRating) {
        this.serviceRating = serviceRating;
    }

    /**
     *
     * @param servicetype
     */
    public void setServicetype(String servicetype) {
        this.servicetype = servicetype;
    }

    /**
     *
     * @return
     */
    public String getSharedSecret() {
        return sharedSecret;
    }

    /**
     *
     * @param sharedSecret
     */
    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = EncryptionHelper.correctKEY(sharedSecret);
    }

    /**
     * @return the restHttpServer
     */
    public RestHttpServer getRestHttpServer() {
        return restHttpServer;
    }

    /**
     * @return the service_recheck_seconds
     */
    public int getServiceRecheckSeconds() {
        return service_recheck_seconds;
    }

    /**
     * @param service_recheck_seconds the service_recheck_seconds to set
     */
    public void setServiceRecheckSeconds(int service_recheck_seconds) {
        this.service_recheck_seconds = service_recheck_seconds;
    }

    /**
     * @return the service_stale_seconds
     */
    public int getServiceStaleSeconds() {
        return service_stale_seconds;
    }

    /**
     * @param service_stale_seconds the service_stale_seconds to set
     */
    public void setServiceStaleSeconds(int service_stale_seconds) {
        this.service_stale_seconds = service_stale_seconds;
    }

    /**
     * @return the service_gone_seconds
     */
    public int getServiceGoneSeconds() {
        return service_gone_seconds;
    }

    /**
     * @param service_gone_seconds the service_gone_seconds to set
     */
    public void setServiceGoneSeconds(int service_gone_seconds) {
        this.service_gone_seconds = service_gone_seconds;
    }

}
