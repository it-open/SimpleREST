/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints.staticfile;

import at.itopen.simplerest.Example;
import at.itopen.simplerest.conversion.ContentType;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.HttpStatus;
import at.itopen.simplerest.path.RestEndpoint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class StaticFileEndpoint extends RestEndpoint {

    private final File basePath;
    private final CachePolicyInterface cachePolicy;

    public StaticFileEndpoint(File basePath, CachePolicyInterface cachePolicy) {
        super("STATIC");
        this.basePath = basePath;
        this.cachePolicy = cachePolicy;
    }

    @Override
    public void Call(Conversion conversion, Map<String, String> UrlParameter) {
        StringBuilder fileName = new StringBuilder(basePath.getAbsolutePath());
        for (int i = 0; UrlParameter.containsKey("" + i); i++) {
            String part = UrlParameter.get("" + i);
            if (part.equals("..")) {
                part = "";
            }
            fileName.append(File.separator).append(part);
        }
        CacheItem cacheitem = cachePolicy.get(fileName.toString());
        if (cacheitem == null) {
            File inFile = new File(fileName.toString());
            if (inFile.exists()) {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(fileName.toString());
                    byte[] data = fis.readAllBytes();
                    fis.close();
                    String mimeType = URLConnection.guessContentTypeFromName(fileName.toString());
                    ContentType ct = ContentType.fromMimeType(mimeType);
                    cacheitem = new CacheItem(ct, fileName.toString(), data);
                    cachePolicy.offer(cacheitem);
                    conversion.getResponse().setContentType(ct);
                    conversion.getResponse().setData(data);

                } catch (IOException ex) {
                    Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                conversion.getResponse().setStatus(HttpStatus.NotFound);
            }
        } else {
            conversion.getResponse().setData(cacheitem.getData());
            conversion.getResponse().setContentType(cacheitem.getType());
        }

    }

}
