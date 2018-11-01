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
public class NoCachePolicy implements CachePolicyInterface{

    @Override
    public void offer(CacheItem cacheItem) {
        
    }

    @Override
    public CacheItem get(String Name) {
        return null;
    }

    
    
}
