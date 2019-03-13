/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

/**
 *
 * @author roland
 */
public abstract class AbstratctServiceRating {

    /**
     *
     * @param service
     * @return
     */
    public abstract double rate(Service service);

}
