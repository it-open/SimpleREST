/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import at.itopen.simplerest.RestHttpServer;

/**
 *
 * @author roland
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RestHttpServer server = RestHttpServer.Start(9000);
        
        LoadBalancerConfig config = new LoadBalancerConfig(server, "http://<IP>:<PORT>/", "test1");
        config.addInitialDiscoverUrl("http://127.0.0.1:9001");
        server.enableLoadBalancer(config);
    }
    
}
