/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.GetEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author roland
 */
public class RestStatus extends GetEndpoint {

    /**
     *
     * @param endpointName
     */
    public RestStatus(String endpointName) {
        super(endpointName);
    }

    @Override
    public void Call(Conversion conversion, Map<String, String> UrlParameter) {
        LoadBalancer lb = getRootPath().getRestHttpServer().getLoadBalancer();
        Service me = new Service();
        me.setLastseen(System.currentTimeMillis());
        me.setInfo(SystemCheck.getInstance().getaktSystemInfoData());
        me.setBaseurl(lb.getConfig().getBaseurl());
        me.setType(lb.getConfig().getServicetype());
        me.setId(lb.getConfig().getServiceid());
        List<Service> out = new ArrayList<>();
        out.add(me);
        lb.getServices().getAllActiveServices().forEach((s) -> {
            out.add(s);
        });

        conversion.getResponse().setData(out);
    }

}
