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
        Dynamic dynamic = null;
        for (Dynamic d : dynamics) {
            for (String ext : d.getExtension()) {
                if (name.endsWith(ext)) {
                    dynamic = d;
                }
            }
        }
        if (dynamic != null) {
            DynamicFile dfile = new DynamicFile(readStatic(fileName.toString()), name, ContentType.fromFileName(name));
            dynamic.call(conversion, UrlParameter, dfile);
            conversion.getResponse().setContentType(dfile.getContentType());
            conversion.getResponse().setData(dfile.getData());
        } else {
            CacheItem cacheitem = cachePolicy.get(name);
            if (cacheitem == null) {
                byte[] data = readStatic(name);
                if (data != null) {
                    ContentType ct = ContentType.fromFileName(name);
                    cacheitem = new CacheItem(ct, name, data);
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

    }

    /**
     *
     * @param fileName
     * @return
     */
    public abstract byte[] readStatic(String fileName);

}
