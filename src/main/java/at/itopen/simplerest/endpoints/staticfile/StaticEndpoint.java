/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints.staticfile;

import at.itopen.simplerest.conversion.ContentType;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.HttpStatus;
import at.itopen.simplerest.path.RestEndpoint;
import java.io.File;
import java.net.URLConnection;
import java.util.Map;

/**
 *
 * @author roland
 */
public abstract class StaticEndpoint extends RestEndpoint {

    private final CachePolicyInterface cachePolicy;

    /**
     *
     * @param cachePolicy
     */
    public StaticEndpoint(CachePolicyInterface cachePolicy) {
        super("STATIC");
        this.cachePolicy = cachePolicy;
    }
    
    /**
     *
     * @return
     */
    public String getFileSeperator()
    {
        return File.separator;
    }

    @Override
    public void Call(Conversion conversion, Map<String, String> UrlParameter) {
        StringBuilder fileName = new StringBuilder("");
        boolean index=true;
        if (UrlParameter!=null)
        for (int i = 0; UrlParameter.containsKey("" + i); i++) {
            String part = UrlParameter.get("" + i);
            if (part.startsWith("..")) {
                part = "";
            }
            index=false;
            fileName.append(getFileSeperator()).append(part);
        }
        if (index)
            fileName.append(getFileSeperator()).append("index.html");
        if (fileName.toString().endsWith(getFileSeperator()))
            fileName.append(getFileSeperator()).append("index.html");
        
        CacheItem cacheitem = cachePolicy.get(fileName.toString());
        if (cacheitem == null) {
            byte[] data = readStatic(fileName.toString());
            if (data != null) {
                ContentType ct = ContentType.fromFileName(fileName.toString());
                cacheitem = new CacheItem(ct, fileName.toString(), data);
                cachePolicy.offer(cacheitem);
                conversion.getResponse().setContentType(ct);
                conversion.getResponse().setData(data);
            } else {
                conversion.getResponse().setStatus(HttpStatus.NotFound);
            }
        } else {
            conversion.getResponse().setData(cacheitem.getData());
            conversion.getResponse().setContentType(cacheitem.getType());
        }

    }

    /**
     *
     * @param fileName
     * @return
     */
    public abstract byte[] readStatic(String fileName);

}
