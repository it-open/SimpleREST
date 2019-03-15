/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.message;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.microservice.loadbalancer.Service;
import at.itopen.simplerest.path.RestPath;

/**
 *
 * @author roland
 */
public class ServiceIpPath extends RestPath {

    public ServiceIpPath(String pathName) {
        super(pathName);
    }

    @Override
    protected boolean checkPath(Conversion conversion) {
        String sourceIP = conversion.getRequest().getSourceIp().toString();
        for (Service service : getRootPath().getRestHttpServer().getLoadBalancer().getServices().getAllServices()) {
            if (sourceIP.equals(service.getInfo().getNet_ip())) {
                return true;
            }
        }
        return false;
    }

}
