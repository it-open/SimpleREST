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
public class DynamicFile {

    byte[] data;
    String filename;
    ContentType contentType;

    /**
     *
     * @param data
     * @param filename
     * @param contentType
     */
    public DynamicFile(byte[] data, String filename, ContentType contentType) {
        this.data = data;
        this.filename = filename;
        this.contentType = contentType;
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
    public String getStringData() {
        return new String(data);
    }

    /**
     *
     * @return
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     *
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     *
     * @param contentType
     */
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     *
     * @param data
     */
    public void setData(String data) {
        this.data = data.getBytes();
    }

}
