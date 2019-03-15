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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class Services {

    private final Map<String, List<Service>> services = new HashMap<>();
    private AbstratctServiceRating rating = null;
    private LoadBalancer loadBalancer;
    private Map<String, Long> accesscounter = new HashMap<>();
    private Map<String, Double> accessvalues = new HashMap<>();

    /**
     *
     * @param loadBalancer
     */
    public Services(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;

        new Thread("AccessCounter") {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);

                        Map<String, Long> accesscounter2 = accesscounter;
                        Map<String, Double> newRating = new HashMap<>();
                        accesscounter = new HashMap<>();
                        for (String type : services.keySet()) {
                            double rating = 0;
                            int count = 0;
                            for (Service service : getServiceType(type)) {
                                rating += service.getRating();
                                count++;
                            }
                            rating = rating / count;
                            long accesscount = 0;
                            if (accesscounter2.containsKey(type)) {
                                accesscount = accesscounter2.get(type);
                            }
                            if (accesscount < 10) {
                                accesscount = 10;
                            }
                            rating = rating / (accesscount * 2);
                            newRating.put(type, rating);
                        }
                        accessvalues = newRating;

                    } catch (Throwable t) {
                        Logger.getLogger(Services.class.getName()).log(Level.SEVERE, "AccessCounter", t);
                    }
                }
            }

        }.start();

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
        loadBalancer.getGuarantor().serviceRemoved(service);
    }

    public void serviceError(Service service) {
        getServiceById(service.getId()).setRating(-1000);
    }

    /**
     *
     * @param servicetype
     * @return
     */
    public Service getRandomForServiceType(String servicetype) {
        List<String> out = new ArrayList<>();
        List<Service> all = new ArrayList<>(getServiceType(servicetype));
        all.sort((Service o1, Service o2) -> new Double(o1.getRating()).compareTo(o2.getRating()));
        if (!all.isEmpty()) {
            Service service = all.get(0);
            if (service.getBaseurl() == null) {
                return getRandomForServiceType(servicetype);
            } else {
                return service;
            }
        }
        return null;
    }

    public void serviceUsed(Service service) {
        service.setRating(service.getRating() - getAccessRating(service.getType()));
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
                if (service == null) {
                    continue;
                }
                if (service.getStatus() == null) {
                    continue;
                }
                if (service.getStatus().equals(Service.SERVICE_STATUS.ACTIVE)) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized void logAccess(Service service) {
        String type = service.getType();
        if (!accesscounter.containsKey(type)) {
            accesscounter.put(type, 1L);
        } else {
            accesscounter.put(type, accesscounter.get(type) + 1);
        }
        serviceUsed(service);
    }

    public double getAccessRating(String serviceType) {
        if (accessvalues.containsKey(serviceType)) {
            return accessvalues.get(serviceType);
        } else {
            return 0;
        }
    }

}
