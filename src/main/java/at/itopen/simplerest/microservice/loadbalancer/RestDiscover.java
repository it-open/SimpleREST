/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.HttpStatus;
import at.itopen.simplerest.endpoints.JsonPostEndpoint;
import java.util.Map;

/**
 *
 * @author roland
 */
public class RestDiscover extends JsonPostEndpoint<RestDiscoverQuestion> {

    /**
     *
     * @param endpointName
     */
    public RestDiscover(String endpointName) {
        super(endpointName);
    }

    /**
     *
     * @param conversion
     * @param urlParameter
     */
    @Override
    public void call(Conversion conversion, Map<String, String> urlParameter) {

        LoadBalancer lb = getRootPath().getRestHttpServer().getLoadBalancer();

        RestDiscoverQuestion question = getData();
        String security = urlParameter.get("security");

        String key = lb.decryptUrl(question, security);
        if (question.getSenderid().equals(key)) {

            if (lb.getServices().getServiceById(question.getSenderid()) == null) {
                lb.addUndiscovered(question.getSenderbaseurl());
            }

            RestDiscoverAnswer answer = lb.buildRestDiscoverAnswer();
            conversion.getResponse().setWrapJson(false);
            conversion.getResponse().setData(answer);
        } else {
            conversion.getResponse().setStatus(HttpStatus.NotFound);
        }
    }

}
