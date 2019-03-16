/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.message;

import at.itopen.simplerest.Json;
import at.itopen.simplerest.client.RestClient;
import at.itopen.simplerest.microservice.client.LoadBalancedRestClient;
import at.itopen.simplerest.microservice.loadbalancer.LoadBalancer;
import at.itopen.simplerest.microservice.loadbalancer.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author roland
 */
public class Guarantor {

    private final LoadBalancer loadBalancer;
    private final Map<String, GuarantorMessage> messages = new HashMap<>();

    /**
     *
     * @param loadBalancer
     */
    public Guarantor(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     *
     * @param service
     */
    public void serviceRemoved(Service service) {
        List<GuarantorMessage> gms;
        synchronized (messages) {
            gms = new ArrayList<>(messages.values());
        }
        for (GuarantorMessage gm : gms) {
            if (gm.getRequest().getGuarantorServiceIds().contains(service.getId())) {
                gm.getRequest().getGuarantorServiceIds().remove(service.getId());
            }
            if (gm.getRequest().getReceiverId().equals(service.getId())) {
                synchronized (messages) {
                    messages.remove(gm.getRequest().getMessageid());
                }
                resendMessage(gm);
            }
        }

    }

    private void resendMessage(GuarantorMessage gm) {
        LoadBalancedRestClient lbrc = loadBalancer.RestClient(gm.getRequest().getTargetUrl(), RestClient.REST_METHOD.POST);
        gm.getRequest().getHeaders().entrySet().forEach((header) -> {
            lbrc.setHeader(header.getKey(), header.getValue());
        });
        Object json = Json.fromString(gm.getRawJson(), Object.class);
        Service receiverService = gm.getReceiverService();
        if (receiverService != null) {
            lbrc.sendMessagetoQueue(receiverService.getType(), new MessageRequest<>(json));
        }
    }

    /**
     *
     * @param rawJson
     */
    public void introduced(String rawJson) {
        GuarantorMessage gm = new GuarantorMessage(rawJson);
        String messageId = gm.getRequest().getMessageid();
        Service receiverService = loadBalancer.getServices().getServiceById(gm.getRequest().getReceiverId());
        gm.setReceiverService(receiverService);
        gm.setQueued(true);
        synchronized (messages) {
            if (messages.containsKey(messageId)) {
                messages.remove(messageId);
            }
            messages.put(messageId, gm);
        }
    }

    /**
     *
     * @param status
     */
    public void status(MessageStatus status) {
        GuarantorMessage gm = messages.get(status.getMessageid());
        gm.setWorking(status.isWorking());
        gm.setFinisehd(status.isFinished());
    }

}
