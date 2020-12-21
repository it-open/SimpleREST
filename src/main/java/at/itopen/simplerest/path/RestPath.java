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
    private RestPath parent;

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
     * @return
     */
    public RestEndpoint addRestEndpoint(RestEndpoint endpoint) {
        endpoints.add(endpoint);
        endpoint.setParent(this);
        return endpoint;
    }

    /**
     *
     * @param restPath
     * @return
     */
    public RestPath addSubPath(RestPath restPath) {
        subPaths.add(restPath);
        restPath.setParent(this);
        return restPath;
    }

    /**
     *
     * @param restPathName
     * @return
     */
    public RestPath addSubPath(String restPathName) {
        RestPath restPath = new RestPath(restPathName);
        restPath.setParent(this);
        subPaths.add(restPath);
        return restPath;
    }

    /**
     *
     * @param restPathName
     * @return
     */
    public RestPath getSubPath(String restPathName) {
        for (RestPath rp : subPaths) {
            if (rp.getPathName().equals(restPathName)) {
                return rp;
            }
        }
        return addSubPath(restPathName);

    }

    /**
     *
     * @return
     */
    public String getPathName() {
        return pathName;
    }

    /**
     *
     * @param catchAllEndPoint
     */
    public void setCatchAllEndPoint(RestEndpoint catchAllEndPoint) {
        this.catchAllEndPoint = catchAllEndPoint;
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
        if (!(this instanceof RootPath)) {
            return getParent().getRootPath();
        } else {
            return (RootPath) this;
        }
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
        if (depth == uriPath.size() - 1) {
            EndpointWorker endpointWorker = findEndpoint(uriPath.get(depth), pathParameter, conversion);
            if (endpointWorker != null) {
                return endpointWorker;
            }
        } else {
            for (RestPath restPath : subPaths) {
                if (restPath.getPathName().equalsIgnoreCase(uriPath.get(depth))) {
                    if (!restPath.checkPath(conversion, uriPath.get(depth))) {
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
                    if (!restPath.checkPath(conversion, uriPath.get(depth))) {
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
     * @param location
     * @return
     */
    public RestPath pathForLocation(String location) {
        location = location.trim();
        if (location.startsWith("/")) {
            location = location.substring(1);
        }
        String path = location.split("\\/")[0];
        String subPath = "";
        if (location.length() > path.length()) {
            subPath = location.substring(path.length() + 1);
        }

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
     * @param pathData
     * @return
     */
    protected boolean checkPath(Conversion conversion, String pathData) {
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
