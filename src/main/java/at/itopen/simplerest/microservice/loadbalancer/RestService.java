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
public class RestService {

    private String id;
    private String type;
    private String baseurl;
    private long lastseen;
    private SystemInfoData info;

    /**
     *
     * @param service
     * @return
     */
    public static RestService fromService(Service service) {
        RestService rs = new RestService();
        rs.id = service.getId();
        rs.type = service.getType();
        rs.baseurl = service.getBaseurl();
        rs.lastseen = service.getLastseen();
        rs.info = service.getInfo();
        return rs;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the baseurl
     */
    public String getBaseurl() {
        return baseurl;
    }

    /**
     * @return the lastseen
     */
    public long getLastseen() {
        return lastseen;
    }

    /**
     * @return the info
     */
    public SystemInfoData getInfo() {
        return info;
    }

}
