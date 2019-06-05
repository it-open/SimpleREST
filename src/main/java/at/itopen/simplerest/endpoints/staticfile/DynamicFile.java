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

    public DynamicFile(byte[] data, String filename, ContentType contentType) {
        this.data = data;
        this.filename = filename;
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public String getStringData() {
        return new String(data);
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setData(String data) {
        this.data = data.getBytes();
    }

}
