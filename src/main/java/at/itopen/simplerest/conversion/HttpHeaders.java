/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author roland
 */
public class HttpHeaders {
    
    Map<String,List<String>> data;

    public HttpHeaders() {
        data=new HashMap<>();
    }
    
    public final void addHeaders(io.netty.handler.codec.http.HttpHeaders headers)
    {
        for (String name:(Set<String>)headers.names())
        {
            List<String> values=new ArrayList<>();
            values.addAll((List<String>)headers.getAll(name));
            data.put(name.toLowerCase(), Collections.unmodifiableList(values));
        }
    }
    
    public Set<String> getNames()
    {
        return data.keySet();
    }
    
    public boolean contains(String name)
    {
        return data.containsKey(name);
    }
    
    public String get(String name)
    {
        return get(name,null);
    }
    
    public String get(String name,String defaultValue)
    {
        if (contains(name))
        {
            return data.get(name).get(0);
        }
        else
            return defaultValue;
    }
    
    public List<String> getAll(String name)
    {   if (data.containsKey(name))
        return data.get(name);
    else
        return new ArrayList<>();
    }
    
    public List<String> getAllOrEmpty(String name)
    {
        if (contains(name))
            return data.get(name);
        else
            return new ArrayList<String>();
    }
    
    public void put(String name, String value)
    {
        List<String> list=new ArrayList<>();
        list.add(value);
        data.put(name, list);
    }
    
    public void add(String name, String value)
    {
        if (!data.containsKey(name))
            data.put(name, new ArrayList<>());
        data.get(name).add(value);
    }
    public void put(String name, String... value)
    {
        List<String> list=new ArrayList<>();
        list.addAll(Arrays.asList(value));
        data.put(name, list);
    }
    
    public void remove(String name)
    {
        data.remove(name);
    }

    
    
    
}
