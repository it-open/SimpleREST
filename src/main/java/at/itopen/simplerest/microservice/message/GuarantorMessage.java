/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.message;

import at.itopen.simplerest.Json;
import at.itopen.simplerest.microservice.loadbalancer.Service;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author roland
 */
public class GuarantorMessage {

    private final MessageRequest<Object> request;
    private final String rawJson;
    private Service receiverService;
    private boolean queued = false;
    private boolean working = false;
    private boolean finisehd = false;
    private final long fistSeen;
    private long lastSeen;

    public GuarantorMessage(String rawJson) {
        this.request = Json.fromString(rawJson, MessageRequest.class);
        JsonNode node = Json.fromString(rawJson);
        JsonNode data = node.get("data");
        this.rawJson = Json.prettyPrintJsonString(data);
        this.fistSeen = System.currentTimeMillis();
        this.lastSeen = System.currentTimeMillis();
    }

    public long getAge() {
        return System.currentTimeMillis() - fistSeen;
    }

    public long getActiveAge() {
        return System.currentTimeMillis() - lastSeen;
    }

    public void setQueued(boolean queued) {
        this.queued = queued;
        this.lastSeen = System.currentTimeMillis();
    }

    public void setFinisehd(boolean finisehd) {
        this.finisehd = finisehd;
        this.lastSeen = System.currentTimeMillis();
    }

    public void setWorking(boolean working) {
        this.working = working;
        this.lastSeen = System.currentTimeMillis();
    }

    public boolean isFinisehd() {
        return finisehd;
    }

    public boolean isQueued() {
        return queued;
    }

    public boolean isWorking() {
        return working;
    }

    public String getRawJson() {
        return rawJson;
    }

    public MessageRequest<Object> getRequest() {
        return request;
    }

    /**
     * @return the receiverService
     */
    public Service getReceiverService() {
        return receiverService;
    }

    /**
     * @param receiverService the receiverService to set
     */
    public void setReceiverService(Service receiverService) {
        this.receiverService = receiverService;
    }

}
