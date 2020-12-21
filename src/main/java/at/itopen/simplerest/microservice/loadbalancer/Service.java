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
public class Service {

    /**
     *
     */
    public enum SERVICESTATUS {

        /**
         *
         */
        SEEN,
        /**
         *
         */
        ACTIVE,
        /**
         *
         */
        STALE,
        /**
         *
         */
        GONE
    }

    private String id;
    private String type;
    private String baseurl;
    private SERVICESTATUS status;
    private long lastseen = 0;
    private SystemInfoData info;
    private double rating = 0;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the baseurl
     */
    public String getBaseurl() {
        return baseurl;
    }

    /**
     * @param baseurl the baseurl to set
     */
    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    /**
     * @return the lastseen
     */
    public long getLastseen() {
        return lastseen;
    }

    /**
     * @param lastseen the lastseen to set
     */
    public void setLastseen(long lastseen) {
        this.lastseen = lastseen;
    }

    /**
     * @return the info
     */
    public SystemInfoData getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(SystemInfoData info) {
        this.info = info;
    }

    /**
     * @return the status
     */
    public SERVICESTATUS getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(SERVICESTATUS status) {
        this.status = status;
    }

    /**
     * @return the rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

}
