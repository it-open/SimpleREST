/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import at.itopen.simplerest.RestHttpServer;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.GetEndpoint;
import java.util.Map;

/**
 *
 * @author roland
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RestHttpServer server = RestHttpServer.start(9000);

        LoadBalancerConfig config = new LoadBalancerConfig(server, "http://<IP>:<PORT>/", "test1");
        config.addInitialDiscoverUrl("http://127.0.0.1:9001");
        config.setSharedSecret("Roland Schuller");
        server.enableLoadBalancer(config);
        server.getRootEndpoint().addRestEndpoint(new GetEndpoint("ping") {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                System.out.println("Ping:" + conversion.getRequest().getParam("num"));
            }
        });
    }

}
