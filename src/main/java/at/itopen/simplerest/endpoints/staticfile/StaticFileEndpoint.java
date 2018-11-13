/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints.staticfile;

import at.itopen.simplerest.Example;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class StaticFileEndpoint extends StaticEndpoint {

    private final File basePath;

    public StaticFileEndpoint(File basePath, CachePolicyInterface cachePolicy) {
        super(cachePolicy);
        this.basePath = basePath;
    }

    @Override
    public byte[] readStatic(String fileName) {
        String name = basePath.getAbsolutePath() + fileName;
        try (FileInputStream fis = new FileInputStream(name)) {
            byte[] data = fis.readAllBytes();
            return data;
        } catch (IOException ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

}
