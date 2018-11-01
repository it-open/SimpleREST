/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints.staticfile;

import at.itopen.simplerest.conversion.ContentType;

/**
 *
 * @author roland
 */
public class CacheItem {

    private final ContentType type;
    private final String name;
    private final byte[] data;
    private final long cacheTime;

    public CacheItem(ContentType type, String name, byte[] data) {
        this.type = type;
        this.name = name;
        this.data = data;
        cacheTime = System.currentTimeMillis();
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public byte[] getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public ContentType getType() {
        return type;
    }

}
