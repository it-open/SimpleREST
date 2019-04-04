/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

import at.itopen.simplerest.conversion.Conversion;
import java.util.Map;

/**
 *
 * @author roland
 */
public abstract class RestEndpoint {

    private final String endpointName;
    private EndpointDocumentation endpointDocumentation;
    private RestPath parent;

    /**
     *
     * @param endpointName
     */
    public RestEndpoint(String endpointName) {
        this.endpointName = endpointName;
    }

    /**
     *
     * @param endpointDocumentation
     */
    public final void setDocumentation(EndpointDocumentation endpointDocumentation) {
        this.endpointDocumentation = endpointDocumentation;
    }

    /**
     *
     * @return
     */
    public EndpointDocumentation getDocumentation() {
        return endpointDocumentation;
    }

    /**
     *
     * @return
     */
    public String getEndpointName() {
        return endpointName;
    }

    /**
     *
     * @param conversion
     * @return
     */
    protected boolean checkEndpoint(Conversion conversion) {
        if (this instanceof AuthenticatedRestEndpoint) {
            if (!conversion.getRequest().getUser().isAuthenticated()) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    public RestPath getParent() {
        return parent;
    }

    /**
     *
     * @param parent
     */
    public void setParent(RestPath parent) {
        this.parent = parent;
    }

    /**
     *
     * @return
     */
    public RootPath getRootPath() {
        return getParent().getRootPath();
    }

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    public void CallEndpoint(Conversion conversion, Map<String, String> UrlParameter) {
        Call(conversion, UrlParameter);
    }

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    public abstract void Call(Conversion conversion, Map<String, String> UrlParameter);

}
