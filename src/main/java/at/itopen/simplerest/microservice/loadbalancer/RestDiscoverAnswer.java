/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author roland
 */
public class RestDiscoverAnswer {

    long timestamp;
    List<RestService> services = new ArrayList<>();

    /**
     *
     * @param lb
     * @return
     */
    public static RestDiscoverAnswer buildAnswer(LoadBalancer lb) {

        Service me = new Service();
        me.setLastseen(System.currentTimeMillis());
        me.setInfo(SystemCheck.getInstance().getaktSystemInfoData());
        me.setBaseurl(lb.getConfig().getBaseurl());
        me.setType(lb.getConfig().getServicetype());
        me.setId(lb.getConfig().getServiceid());
        RestDiscoverAnswer rda = new RestDiscoverAnswer();
        if (lb.isAvailable()) {
            rda.services.add(RestService.fromService(me));
        }
        lb.getServices().getAllActiveServices().forEach((s) -> {
            rda.services.add(RestService.fromService(s));
        });

        rda.timestamp = System.currentTimeMillis();
        return rda;
    }

    /**
     *
     * @return
     */
    public List<RestService> getServices() {
        return services;
    }

    /**
     *
     * @return
     */
    public long getTimestamp() {
        return timestamp;
    }

}
