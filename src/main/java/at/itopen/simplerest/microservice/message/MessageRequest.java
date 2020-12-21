/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author roland
 * @param <T>
 */
public class MessageRequest<T> {

    private String messageid;
    private String targetUrl;
    private String senderId;
    private String receiverId;
    private List<String> guarantorServiceIds = new ArrayList<>();
    private Map<String, String> headers = new HashMap<>();

    private T data;

    /**
     *
     * @param data
     */
    public MessageRequest(T data) {
        this.messageid = UUID.randomUUID().toString();
        this.data = data;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     *
     * @param headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     *
     * @param targetUrl
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     *
     * @return
     */
    public String getTargetUrl() {
        return targetUrl;
    }

    /**
     * @return the messageid
     */
    public String getMessageid() {
        return messageid;
    }

    /**
     * @param messageid the messageid to set
     */
    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    /**
     * @return the senderId
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the guarantorServiceIds
     */
    public List<String> getGuarantorServiceIds() {
        return guarantorServiceIds;
    }

    /**
     * @param guarantorServiceIds the guarantorServiceIds to set
     */
    public void setGuarantorServiceIds(List<String> guarantorServiceIds) {
        this.guarantorServiceIds = guarantorServiceIds;
    }

    /**
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     *
     * @param receiverId
     */
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     *
     * @return
     */
    public String getReceiverId() {
        return receiverId;
    }

}
