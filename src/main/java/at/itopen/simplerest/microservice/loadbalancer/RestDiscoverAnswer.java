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
