/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints.staticfile;

/**
 *
 * @author roland
 */
public interface CachePolicyInterface {

    /**
     *
     * @param cacheItem
     */
    void offer(CacheItem cacheItem);

    /**
     *
     * @param name
     * @return
     */
    CacheItem get(String name);

}
