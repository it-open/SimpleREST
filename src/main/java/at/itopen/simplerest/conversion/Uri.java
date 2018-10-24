/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author roland
 */
public class Uri {
    
    List<String> path;
    Map<String,String> queryParam;
    String fragment=null;

    public Uri(String uri) {
        path=new ArrayList<>();
        queryParam=new HashMap<>();
        uri=decodeFragment(uri);
        uri=decodeParams(uri);
        decodePath(uri);
    }
    
    private String decodeFragment(String uri)
    {
        if (uri.contains("#"))
        {
            int pos=uri.lastIndexOf('#');
            fragment=uri.substring(pos+1);
            return uri.substring(0, pos);
        }
        return uri;
    }
    
    private String decodeParams(String uri)
    {
        if (uri.contains("?"))
        {
            int pos=uri.lastIndexOf('?');
            String paramString=uri.substring(pos+1);
            for (String param:paramString.split("&")){
                String[] parts=param.split("=");
                if (parts.length==2)
                {
                    queryParam.put(parts[0], parts[1]);
                }
            }
            return uri.substring(0, pos);
        }
        return uri;
    }
    
    private void decodePath(String uri)
    {
        for (String item:uri.split("/"))
        {
            item=item.trim();
            if (item.length()>0)
                path.add(item);
        }
        
    }

    public String getFragment() {
        return fragment;
    }

    public List<String> getPath() {
        return Collections.unmodifiableList(path);
    }
    
    public Set<String> getParamNames()
    {
        return queryParam.keySet();
    }
    
    public String getParam(String name, String defaultValue)
    {
        if (queryParam.containsKey(name))
            return queryParam.get(name);
        else
            return defaultValue;
    }
    
    public String getParam(String name)
    {
        return getParam(name, null);
    }

    @Override
    public String toString() {
        StringBuilder out=new StringBuilder(String.join("/", path));
        if (!queryParam.isEmpty())
        {
            out.append("?");
            boolean first=true;
            for (String name:getParamNames())
            {
                if (!first)
                    out.append("&");
                out.append(name).append("=").append(getParam(name));
                first=false;
            }
        }
        if (fragment!=null)
        {
            out.append("#").append(getFragment());
        }
        
        return out.toString();
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
