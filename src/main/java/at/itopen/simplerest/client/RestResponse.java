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

    /**
     *
     * @param res
     */
    public RestResponse(HttpResponse res) {
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
        return res.getStatusLine().getStatusCode();
    }

    /**
     *
     * @return
     */
    public String getStatusText() {
        return res.getStatusLine().getReasonPhrase();
    }

    /**
     *
     * @return
     */
    public ProtocolVersion getProtocolVersion() {
        return res.getProtocolVersion();
    }

    /**
     *
     * @return
     */
    public Locale getLocale() {
        return res.getLocale();
    }

    /**
     *
     * @param name
     * @return
     */
    public String getHeader(String name) {
        return res.getFirstHeader(name).getValue();
    }

    /**
     *
     * @return
     */
    public long getContentLength() {
        return res.getEntity().getContentLength();
    }

}
