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
    private RestEndpoint catchAllEndPoint = null;

    /**
     *
     * @param pathName
     */
    public RestPath(String pathName) {
        this.pathName = pathName;
    }

    /**
     *
     * @param endpoint
     */
    public void addRestEndpoint(RestEndpoint endpoint) {
        endpoints.add(endpoint);
    }

    /**
     *
     * @param restPath
     */
    public RestPath addSubPath(RestPath restPath) {
        subPaths.add(restPath);
        return restPath;
    }

    /**
     *
     * @return
     */
    public String getPathName() {
        return pathName;
    }

    public void setCatchAllEndPoint(RestEndpoint catchAllEndPoint) {
        this.catchAllEndPoint = catchAllEndPoint;
    }

    /**
     *
     * @param conversion
     * @param depth
     * @param pathParameter
     * @return
     */
    public EndpointWorker findEndpoint(Conversion conversion, int depth, Map<String, String> pathParameter) {
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
                    Map<String, String> clonePathParameter = new HashMap<>(pathParameter);
                    clonePathParameter.put(restPath.getPathName().substring(1), uriPath.get(depth));
                    EndpointWorker endpointWorker = restPath.findEndpoint(conversion, depth + 1, clonePathParameter);
                    if (endpointWorker != null) {

                        return endpointWorker;

                    }
                }
            }
        }
        if (catchAllEndPoint != null) {
            for (int i = depth; i < uriPath.size(); i++) {
                pathParameter.put("" + (i - depth), uriPath.get(i));
            }
            return new EndpointWorker(catchAllEndPoint, pathParameter);
        }
        return null;

    }

    /**
     *
     * @param conversion
     * @param depth
     * @param pathParameter
     * @return
     */
    public RestPath pathForLocation(String location) {
        String path = location.split("\\/")[0];
        String subPath = "";
        if (location.length()>path.length())
                subPath=location.substring(path.length() + 1);

        for (RestPath restPath : subPaths) {
            if (restPath.getPathName().equalsIgnoreCase(path)) {
                if (subPath.isEmpty()) {
                    return restPath;
                } else {
                    return restPath.pathForLocation(subPath);
                }
            }
        }

        return null;

    }

    private EndpointWorker findEndpoint(String name, Map<String, String> pathParameter, Conversion conversion) {
        for (RestEndpoint endpoint : endpoints) {
            if (endpoint.getEndpointName().equalsIgnoreCase(name)) {
                if (endpoint.checkEndpoint(conversion)) {
                    return new EndpointWorker(endpoint, pathParameter);
                }
            }
        }
        for (RestEndpoint endpoint : endpoints) {
            if (endpoint.getEndpointName().startsWith(":")) {
                pathParameter.put(endpoint.getEndpointName().substring(1), name);
                if (endpoint.checkEndpoint(conversion)) {
                    return new EndpointWorker(endpoint, pathParameter);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param conversion
     * @return
     */
    protected boolean checkPath(Conversion conversion) {
        if (this instanceof AuthenticatedRestPath) {
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
    public List<RestEndpoint> getEndpoints() {
        return endpoints;
    }

    /**
     *
     * @return
     */
    public List<RestPath> getSubPaths() {
        return subPaths;
    }

}
