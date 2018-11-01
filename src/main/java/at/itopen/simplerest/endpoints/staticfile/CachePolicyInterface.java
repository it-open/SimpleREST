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
    
    public void offer(CacheItem cacheItem);
    public CacheItem get(String Name);
    
}
