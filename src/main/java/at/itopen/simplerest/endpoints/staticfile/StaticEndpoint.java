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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public abstract class StaticEndpoint extends RestEndpoint {

    private final CachePolicyInterface cachePolicy;
    private final List<Dynamic> dynamics = new ArrayList<>();

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
     * @param dynamic
     */
    public void addDynamic(Dynamic dynamic) {
        dynamics.add(dynamic);
    }

    /**
     *
     * @return
     */
    public String getFileSeperator() {
        return File.separator;
    }

    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    @Override
    public void Call(Conversion conversion, Map<String, String> UrlParameter) {
        StringBuilder fileName = new StringBuilder("");
        boolean index = true;
        if (UrlParameter != null) {
            for (int i = 0; UrlParameter.containsKey("" + i); i++) {
                String part = UrlParameter.get("" + i);
                if (part.startsWith("..")) {
                    part = "";
                }
                index = false;
                fileName.append(getFileSeperator()).append(part);
            }
        }
        if (index) {
            fileName.append(getFileSeperator()).append("index.html");
        }
        if (fileName.toString().endsWith(getFileSeperator())) {
            fileName.append(getFileSeperator()).append("index.html");
        }

        String name = fileName.toString();

        DynamicFile dfile = null;
        CacheItem cacheitem = cachePolicy.get(name);
        if (cacheitem == null) {
            byte[] data = readStatic(name);
            if (data != null) {
                ContentType ct = ContentType.fromFileName(name);
                cacheitem = new CacheItem(ct, name, data);
                cachePolicy.offer(cacheitem);
                dfile = new DynamicFile(data, name, ContentType.fromFileName(name));
            } else {
                if (fileName != null) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Static File not found:" + fileName.toString());
                }
                conversion.getResponse().setStatus(HttpStatus.NotFound);
            }
        } else {
            dfile = new DynamicFile(cacheitem.getData(), cacheitem.getName(), cacheitem.getType());
        }

        if (dfile != null) {
            for (Dynamic d : dynamics) {
                for (String ext : d.getExtension()) {
                    if (name.endsWith(ext)) {
                        d.call(conversion, UrlParameter, dfile);
                    }
                }
            }
            conversion.getResponse().setContentType(dfile.getContentType());
            conversion.getResponse().setData(dfile.getData());
        } else {
            conversion.getResponse().setStatus(HttpStatus.NotFound);

        }

    }

    /**
     *
     * @param fileName
     * @return
     */
    public abstract byte[] readStatic(String fileName);

}
