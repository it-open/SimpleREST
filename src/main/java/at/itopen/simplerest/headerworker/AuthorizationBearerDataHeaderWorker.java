/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.Request;
import at.itopen.simplerest.security.JwtAuthUser;
import at.itopen.simplerest.security.RestSecurityHelper;

/**
 *
 * @author roland
 */
public class AuthorizationBearerDataHeaderWorker extends AbstractHeaderWorker {

    /**
     *
     * @param conversion
     */
    @Override
    public void work(Conversion conversion) {
        Request request = conversion.getRequest();
        String value = request.getHeaders().getAll("authorization").get(1);
        RestSecurityHelper.JwtInfo info = RestSecurityHelper.JWSDECRYPT(value);
        if (info != null) {
            if (request.getUser() instanceof JwtAuthUser) {
                ((JwtAuthUser) request.getUser()).setJwtAuth(conversion, info.getId(), info.getIssuer(), info.getSubject());
                request.getHeaders().remove("authorization");
            }
        }

    }

}
