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
    public RestEndpoint getNOTFOUND() {
        return NOT_FOUND;
    }

    /**
     *
     * @param notFound
     */
    public void setNOTFOUND(RestEndpoint notFound) {
        this.NOT_FOUND = notFound;
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
     * @param index
     */
    public void setINDEX(RestEndpoint index) {
        this.INDEX = index;
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
     * @param exception
     */
    public void setEXCEPTION(RestEndpoint exception) {
        this.EXCEPTION = exception;
    }

}
