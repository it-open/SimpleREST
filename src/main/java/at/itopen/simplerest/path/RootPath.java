/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

import at.itopen.simplerest.RestHttpServer;

/**
 *
 * @author roland
 */
public class RootPath extends RestPath {

    private RestEndpoint NOT_FOUND = null;
    private RestEndpoint INDEX = null;
    private RestEndpoint EXCEPTION = null;
    private RestHttpServer server;

    /**
     *
     */
    public RootPath() {
        super("/");
    }

    /**
     *
     * @return
     */
    public RestHttpServer getRestHttpServer() {
        return server;
    }

    /**
     *
     * @param server
     */
    public void setRestHttpServer(RestHttpServer server) {
        this.server = server;
    }

    /**
     *
     * @return
     */
    public RestEndpoint getNOT_FOUND() {
        return NOT_FOUND;
    }

    /**
     *
     * @param NOT_FOUND
     */
    public void setNOT_FOUND(RestEndpoint NOT_FOUND) {
        this.NOT_FOUND = NOT_FOUND;
    }

    /**
     *
     * @return
     */
    public RestEndpoint getINDEX() {
        return INDEX;
    }

    /**
     *
     * @param INDEX
     */
    public void setINDEX(RestEndpoint INDEX) {
        this.INDEX = INDEX;
    }

    /**
     *
     * @return
     */
    public RestEndpoint getEXCEPTION() {
        return EXCEPTION;
    }

    /**
     *
     * @param EXCEPTION
     */
    public void setEXCEPTION(RestEndpoint EXCEPTION) {
        this.EXCEPTION = EXCEPTION;
    }

}
