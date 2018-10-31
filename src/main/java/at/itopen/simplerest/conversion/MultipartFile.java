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
public class MultipartFile {
    
    private byte[] data;
    String name;
    String contentType;

    /**
     *
     * @param data
     * @param name
     * @param contentType
     */
    public MultipartFile(byte[] data, String name, String contentType) {
        this.data = data;
        this.name = name;
        this.contentType = contentType;
    }

    /**
     *
     * @return
     */
    public String getContentType() {
        return contentType;
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
    
    
    
}
