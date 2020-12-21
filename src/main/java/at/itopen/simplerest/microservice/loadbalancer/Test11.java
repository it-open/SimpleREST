/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import at.itopen.simplerest.RestHttpServer;
import at.itopen.simplerest.client.RestClient;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.GetEndpoint;
import at.itopen.simplerest.microservice.client.LoadBalancedRestClient;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class Test11 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RestHttpServer server = RestHttpServer.start(9003);
        LoadBalancerConfig config = new LoadBalancerConfig(server, "http://<IP>:<PORT>/", "test2");
        config.addInitialDiscoverUrl("http://127.0.0.1:9000");
        config.setSharedSecret("Roland Schuller");
        server.enableLoadBalancer(config);
        server.getRootEndpoint().addRestEndpoint(new GetEndpoint("ping") {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                System.out.println("Ping:" + conversion.getRequest().getParam("num"));
            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Test11.class.getName()).log(Level.SEVERE, null, ex);
        }
        LoadBalancedRestClient rc = server.getLoadBalancer().restClient("ping", RestClient.RESTMETHOD.GET);
        rc.setParameter("num", "100");
        rc.toAllServicesFireAndForget(false);
    }

}
