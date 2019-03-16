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
public class RestDiscoverQuestion {

    private String senderid;
    private String senderbaseurl;
    private long timestamp;

    /**
     *
     * @param senderid
     * @param senderbaseurl
     */
    public RestDiscoverQuestion(String senderid, String senderbaseurl) {
        this.senderid = senderid;
        this.senderbaseurl = senderbaseurl;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     *
     */
    public RestDiscoverQuestion() {
    }

    /**
     * @return the senderid
     */
    public String getSenderid() {
        return senderid;
    }

    /**
     * @param senderid the senderid to set
     */
    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    /**
     * @return the senderbaseurl
     */
    public String getSenderbaseurl() {
        return senderbaseurl;
    }

    /**
     * @param senderbaseurl the senderbaseurl to set
     */
    public void setSenderbaseurl(String senderbaseurl) {
        this.senderbaseurl = senderbaseurl;
    }

    /**
     *
     * @return
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     *
     * @param timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
