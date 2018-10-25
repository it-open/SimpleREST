/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Request;
import at.itopen.simplerest.security.BasicAuthUser;
import at.itopen.simplerest.security.JwtAuthUser;
import at.itopen.simplerest.security.RestSecurity;
import java.util.Base64;

/**
 *
 * @author roland
 */
public class AuthorizationBearerDataHeaderWorker extends AbstractHeaderWorker {

    public AuthorizationBearerDataHeaderWorker() {

    }

    @Override
    public void work(Request request) {
        String value = request.getHeaders().getAll("authorization").get(1);
        RestSecurity.JwtInfo info = RestSecurity.JWS_DECRYPT(value);
        if (info != null) {
            if (request.getUser() instanceof JwtAuthUser) {
                ((JwtAuthUser) request.getUser()).setJwtAuth(info.getId(), info.getIssuer(), info.getSubject());
                request.getHeaders().remove("authorization");
            }
        }

    }

}
