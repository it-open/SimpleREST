/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

import at.itopen.simplerest.conversion.Conversion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author roland
 */
public class RestPath {

    private final String pathName;
    private final List<RestEndpoint> endpoints = new ArrayList<>();
    private final List<RestPath> subPaths = new ArrayList<>();

    public RestPath(String pathName) {
        this.pathName = pathName;
    }

    public void addRestEndpoint(RestEndpoint endpoint) {
        endpoints.add(endpoint);
    }

    public void addSubPath(RestPath restPath) {
        subPaths.add(restPath);
    }

    public String getPathName() {
        return pathName;
    }

    public EndpointWorker findEndpoint(Conversion conversion, int depth, Map<String,String> pathParameter) {
        List<String> uriPath = conversion.getRequest().getUri().getPath();
        if (depth == (uriPath.size() - 1)) {
            EndpointWorker endpointWorker = findEndpoint(uriPath.get(depth), pathParameter, conversion);
            if (endpointWorker != null) {
                return endpointWorker;
            }
        } else {
            for (RestPath restPath : subPaths) {
                if (restPath.getPathName().equalsIgnoreCase(uriPath.get(depth))) {
                    if (!restPath.checkPath(conversion)) {
                        continue;
                    }
                    EndpointWorker endpointWorker = restPath.findEndpoint(conversion, depth + 1, pathParameter);
                    if (endpointWorker != null) {
                        return endpointWorker;
                    }
                }
            }
            for (RestPath restPath : subPaths) {
                if (restPath.getPathName().startsWith(":")) {
                    if (!restPath.checkPath(conversion)) {
                        continue;
                    }
                    Map<String,String> clonePathParameter = new HashMap<>(pathParameter);
                    clonePathParameter.put(restPath.getPathName().substring(1),uriPath.get(depth));
                    EndpointWorker endpointWorker = restPath.findEndpoint(conversion, depth + 1, clonePathParameter);
                    if (endpointWorker != null) {

                        return endpointWorker;

                    }
                }
            }
        }
        return null;

    }

    private EndpointWorker findEndpoint(String name, Map<String,String> pathParameter, Conversion conversion) {
        for (RestEndpoint endpoint : endpoints) {
            if (endpoint.getEndpointName().equalsIgnoreCase(name)) {
                if (endpoint.checkEndpoint(conversion)) {
                    return new EndpointWorker(endpoint, pathParameter);
                }
            }
        }
        for (RestEndpoint endpoint : endpoints) {
            if (endpoint.getEndpointName().startsWith(":")) {
                pathParameter.put(endpoint.getEndpointName().substring(1),name);
                if (endpoint.checkEndpoint(conversion)) {
                    return new EndpointWorker(endpoint, pathParameter);
                }
            }
        }
        return null;
    }

    protected boolean checkPath(Conversion conversion) {
        if (this instanceof AuthenticatedRestPath)
        {
            if (!conversion.getRequest().getUser().isAuthenticated())
                return false;
        }
        return true;
    }

    public List<RestEndpoint> getEndpoints() {
        return endpoints;
    }

    public List<RestPath> getSubPaths() {
        return subPaths;
    }

    
    
}
