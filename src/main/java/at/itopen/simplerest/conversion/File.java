/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

/**
 *
 * @author roland
 */
public class File {
    
    private byte[] data;
    String name;
    String contentType;

    public File(byte[] data, String name, String contentType) {
        this.data = data;
        this.name = name;
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    
    public byte[] getData() {
        return data;
    }

    public String getName() {
        return name;
    }
    
    
    
}
