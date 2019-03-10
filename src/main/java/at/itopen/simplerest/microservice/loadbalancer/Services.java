/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author roland
 */
public class Services {

    private final Map<String, List<Service>> services = new HashMap<>();
    private AbstratctServiceRating rating = null;
    private LoadBalancer loadBalancer;

    /**
     *
     * @param loadBalancer
     */
    public Services(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     *
     * @param service
     */
    public void addService(Service service) {
        if (getServiceById(service.getId()) == null) {
            if (!services.containsKey(service.getType())) {
                services.put(service.getType(), new ArrayList<>());
            }
            services.get(service.getType()).add(service);

        }
    }

    /**
     *
     * @param id
     * @param baseurl
     */
    public void addService(String id, String baseurl) {

    }

    /**
     *
     * @param service
     */
    public void updateService(Service service) {
        if (rating != null) {
            service.setRating(rating.rate(service));
        }
    }

    /**
     *
     * @param rating
     */
    public void setRating(AbstratctServiceRating rating) {
        this.rating = rating;
    }

    /**
     *
     * @param type
     * @return
     */
    public List<Service> getServiceType(String type) {
        if (!services.containsKey(type)) {
            services.put(type, new ArrayList<>());
        }
        return new ArrayList<>(services.get(type));
    }

    /**
     *
     * @return
     */
    public List<Service> getAllActiveServices() {
        List<Service> out = new ArrayList<>();
        services.values().forEach((lists) -> {
            lists.stream().filter((s) -> (s.getStatus().equals(Service.SERVICE_STATUS.ACTIVE))).sorted(new Comparator<Service>() {
                @Override
                public int compare(Service o1, Service o2) {
                    return new Double(o1.getRating()).compareTo(o2.getRating());
                }
            }).forEachOrdered((s) -> {
                out.add(s);
            });
        });
        return out;
    }

    /**
     *
     * @param service
     */
    public void removeService(Service service) {
        services.get(service.getType()).remove(service);
    }

    /**
     *
     * @param servicetype
     * @return
     */
    public String getBaseUrlsForServiceType(String servicetype) {
        List<String> out = new ArrayList<>();
        List<Service> all = new ArrayList<>(getServiceType(servicetype));
        all.sort((Service o1, Service o2) -> new Double(o1.getRating()).compareTo(o2.getRating()));
        if (!all.isEmpty()) {
            Service service = all.get(0);
            service.setRating(service.getRating() - loadBalancer.getConfig().getServiceRating().wearleveling());
            return service.getBaseurl();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public List<Service> getAllServices() {
        List<Service> out = new ArrayList<>();
        services.values().forEach((lists) -> {
            lists.stream().forEach((s) -> {
                out.add(s);
            });
        });
        return out;
    }

    /**
     *
     * @param id
     * @return
     */
    public Service getServiceById(String id) {
        for (List<Service> list : services.values()) {
            for (Service service : list) {
                if (service.getId().equals(id)) {
                    return service;
                }
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public AbstratctServiceRating getRating() {
        return rating;
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        if (services.isEmpty()) {
            return true;
        }
        for (List<Service> list : services.values()) {
            for (Service service : list) {
                if (service.getStatus().equals(Service.SERVICE_STATUS.ACTIVE)) {
                    return false;
                }
            }
        }
        return true;
    }

}
