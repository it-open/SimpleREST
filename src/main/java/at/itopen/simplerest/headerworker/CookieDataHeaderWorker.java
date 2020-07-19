/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.Cookie;
import at.itopen.simplerest.conversion.Request;
import at.itopen.simplerest.security.CookieAuthUser;

/**
 *
 * @author roland
 */
public class CookieDataHeaderWorker extends AbstractHeaderWorker {

    /**
     *
     */
    public CookieDataHeaderWorker() {

    }

    /**
     *
     * @param conversion
     */
    @Override
    public void work(Conversion conversion) {
        Request request = conversion.getRequest();

        String value = request.getHeaders().get("cookie");
        String[] values = value.split(";");
        for (String val1 : values) {
            String[] parts = val1.trim().split("=");
            if (parts.length == 2) {
                Cookie cookie = new Cookie(parts[0], parts[1]);
                request.getCookies().add(cookie);
                if (request.getUser() instanceof CookieAuthUser) {
                    ((CookieAuthUser) request.getUser()).setCookieAuth(conversion, cookie);
                }
            }
        }

        request.getHeaders().remove("cookie");
    }

}
