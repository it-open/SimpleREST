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

    public StaticEndpoint(CachePolicyInterface cachePolicy) {
        super("STATIC");
        this.cachePolicy = cachePolicy;
    }
    
    public String getFileSeperator()
    {
        return File.separator;
    }

    @Override
    public void Call(Conversion conversion, Map<String, String> UrlParameter) {
        StringBuilder fileName = new StringBuilder("");
        for (int i = 0; UrlParameter.containsKey("" + i); i++) {
            String part = UrlParameter.get("" + i);
            if (part.equals("..")) {
                part = "";
            }
            fileName.append(getFileSeperator()).append(part);
        }
        CacheItem cacheitem = cachePolicy.get(fileName.toString());
        if (cacheitem == null) {
            byte[] data = readStatic(fileName.toString());
            if (data != null) {
                String mimeType = URLConnection.guessContentTypeFromName(fileName.toString());
                ContentType ct = ContentType.fromMimeType(mimeType);
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

    public abstract byte[] readStatic(String fileName);

}
