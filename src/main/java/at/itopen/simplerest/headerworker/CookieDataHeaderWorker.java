/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Cookie;
import at.itopen.simplerest.conversion.Request;

/**
 *
 * @author roland
 */
public class CookieDataHeaderWorker extends AbstractHeaderWorker {

    public CookieDataHeaderWorker() {

    }

    @Override
    public void work(Request request) {

        String value = request.getHeaders().get("cookie");
        String[] values = value.split("=");
        if (values.length > 1) {
            for (String val1 : values) {
                String[] parts = val1.trim().split("=");
                if (parts.length == 2) {
                    request.getCookies().add(new Cookie(parts[0], parts[1]));
                }
            }
        }
        request.getHeaders().remove("cookie");
    }

}
