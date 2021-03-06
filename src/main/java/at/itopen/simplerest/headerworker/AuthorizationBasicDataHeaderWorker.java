/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.Request;
import at.itopen.simplerest.security.BasicAuthUser;
import java.util.Base64;

/**
 *
 * @author roland
 */
public class AuthorizationBasicDataHeaderWorker extends AbstractHeaderWorker {

    /**
     *
     * @param conversion
     */
    @Override
    public void work(Conversion conversion) {
        Request request = conversion.getRequest();
        String value = request.getHeaders().getAll("authorization").get(1);
        value = new String(Base64.getDecoder().decode(value));
        String[] parts = value.split(":");
        if (parts.length == 2) {
            if (request.getUser() instanceof BasicAuthUser) {
                ((BasicAuthUser) request.getUser()).setAuth(conversion, parts[0], parts[1]);
                request.getHeaders().remove("authorization");
            }
        }
        if (parts.length == 1) {
            if (request.getUser() instanceof BasicAuthUser) {
                ((BasicAuthUser) request.getUser()).setAuth(conversion, parts[0], null);
                request.getHeaders().remove("authorization");
            }
        }

    }

}
