/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints.staticfile;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class StaticFileEndpoint extends StaticEndpoint {

    private final File basePath;

    /**
     *
     * @param basePath
     * @param cachePolicy
     */
    public StaticFileEndpoint(File basePath, CachePolicyInterface cachePolicy) {
        super(cachePolicy);
        this.basePath = basePath;
    }

    /**
     *
     * @param fileName
     * @return
     */
    @Override
    public byte[] readStatic(String fileName) {
        try {
            String name = basePath.getAbsolutePath() + fileName;
            File in=new File(name);
            if (!in.exists()) return null;
            byte[] data=java.nio.file.Files.readAllBytes(in.toPath());
            return data;
        } catch (IOException ex) {
            Logger.getLogger(StaticFileEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
