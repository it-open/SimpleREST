/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints.staticfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class StaticResourceEndpoint extends StaticEndpoint {

    private final String resourcePath;

    /**
     *
     * @param resourcePath
     * @param cachePolicy
     */
    public StaticResourceEndpoint(String resourcePath, CachePolicyInterface cachePolicy) {
        super(cachePolicy);
        this.resourcePath = resourcePath;
    }

    /**
     *
     * @return
     */
    @Override
    public String getFileSeperator() {
        return "/";
    }
    
    /**
     *
     * @param fileName
     * @return
     */
    @Override
    public byte[] readStatic(String fileName) {
        String name = resourcePath + fileName;
        return readResourceFile(name);
    }
    
    /**
     *
     * @param name
     * @return
     */
    public byte[] readResourceFile(final String name) {
        try {
            InputStream is = getClass().getResourceAsStream(name);
            if (is == null) {
                is = Thread.currentThread().getClass().getResource(name).openStream();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] part = new byte[1024];
            int len;

            while ((len = is.read(part)) > 0) {
                baos.write(part, 0, len);
            }
            is.close();
            return baos.toByteArray();

        } catch (IOException ex) {
            Logger.getLogger(StaticResourceEndpoint.class.getName()).log(Level.SEVERE, name, ex);
        }
        return new byte[0];
    }

    

}
