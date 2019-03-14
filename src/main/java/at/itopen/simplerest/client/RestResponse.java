/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

import at.itopen.simplerest.Json;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author roland
 */
public class RestResponse {

    private byte[] data = null;
    private final HttpResponse res;
    private long roundtripTime;

    /**
     *
     * @param res
     * @param startnano
     */
    public RestResponse(HttpResponse res, long startnano) {
        this.roundtripTime = System.nanoTime() - startnano;
        this.res = res;
        try {
            data = EntityUtils.toByteArray(res.getEntity());
        } catch (IOException ex) {
            Logger.getLogger(RestResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     *
     * @return
     */
    public String getDataAsString() {
        return new String(data);
    }

    /**
     *
     * @return
     */
    public JsonNode getJSON() {
        try {
            return Json.getJSON_CONVERTER().readTree(getDataAsString());
        } catch (IOException ex) {
            Logger.getLogger(RestResponse.class.getName()).log(Level.SEVERE, getDataAsString(), ex);
        }
        return null;
    }

    /**
     *
     * @return
     */
    public int getStatusCode() {
        if (res == null) {
            return 444;
        }
        return res.getStatusLine().getStatusCode();
    }

    /**
     *
     * @return
     */
    public String getStatusText() {
        if (res == null) {
            return "No Connection to Host";
        }
        return res.getStatusLine().getReasonPhrase();
    }

    /**
     *
     * @return
     */
    public ProtocolVersion getProtocolVersion() {
        if (res == null) {
            return new ProtocolVersion("", 0, 0);
        }
        return res.getProtocolVersion();
    }

    /**
     *
     * @return
     */
    public Locale getLocale() {
        if (res == null) {
            return Locale.getDefault();
        }
        return res.getLocale();
    }

    /**
     *
     * @param name
     * @return
     */
    public String getHeader(String name) {
        if (res == null) {
            return null;
        }
        return res.getFirstHeader(name).getValue();
    }

    /**
     *
     * @return
     */
    public long getContentLength() {
        if (res == null) {
            return -1;
        }
        return res.getEntity().getContentLength();
    }

    public long getRoundtripTimeNs() {
        return roundtripTime;
    }

}
