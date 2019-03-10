/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints.staticfile;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author roland
 */
public class FullCachePolicy implements CachePolicyInterface{
    
    private final Map<String,CacheItem> cacheData=new HashMap<>();

    /**
     *
     * @param cacheItem
     */
    @Override
    public void offer(CacheItem cacheItem) {
        cacheData.put(cacheItem.getName(), cacheItem);
    }

    /**
     *
     * @param Name
     * @return
     */
    @Override
    public CacheItem get(String Name) {
        if (cacheData.containsKey(Name)) 
            return cacheData.get(Name);
        else
            return null;
    }

    
    
}
