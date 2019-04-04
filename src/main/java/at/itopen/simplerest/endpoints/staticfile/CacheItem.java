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

    /**
     *
     * @param type
     * @param name
     * @param data
     */
    public CacheItem(ContentType type, String name, byte[] data) {
        this.type = type;
        this.name = name;
        this.data = data;
        cacheTime = System.currentTimeMillis();
    }

    /**
     *
     * @return
     */
    public long getCacheTime() {
        return cacheTime;
    }

    /**
     *
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public ContentType getType() {
        return type;
    }

}
