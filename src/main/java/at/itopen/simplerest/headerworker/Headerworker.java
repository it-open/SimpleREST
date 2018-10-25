/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author roland
 */
public class Headerworker {
    
    private final static Map<String,Map<String,List<AbstractHeaderWorker>>> HEADER_WORKERS=new HashMap<>();
    
    static {
        addWorker("authorization","",new SeperatorDataHeaderWorker("authorization"," "));
        addWorker("content-type","",new SeperatorDataHeaderWorker("content-type",";"));
        addWorker("accept-language","",new SeperatorDataHeaderWorker("accept-language",";"));
        addWorker("content-type","application/x-www-form-urlencoded",new XWwwFormUrlEncodedHeaderWorker());
        addWorker("content-type","multipart/form-data",new MulitpartFormDataHeaderWorker());
        addWorker("authorization","Basic",new AuthorizationBasicDataHeaderWorker());
    }
    
    
    public static void addWorker(String key,String value,AbstractHeaderWorker abstractHeaderWorker)
    {
        gotoList(key, value).add(abstractHeaderWorker);
    }
    
    public static void clearWorkers(String key,String value)
    {
        gotoList(key, value).clear();
    }
    
    public static void work(Request request){
        List<String> names=new ArrayList<>();
        names.addAll(request.getHeaders().getNames());
        for (String name:names)
        {
            for (String value:request.getHeaders().getAll(name)){
                for (AbstractHeaderWorker abstractHeaderWorker:gotoList(name, ""))
                {
                    abstractHeaderWorker.work(request);
                }
            }
            for (String value:request.getHeaders().getAll(name)){
                for (AbstractHeaderWorker abstractHeaderWorker:gotoList(name, value))
                {
                    abstractHeaderWorker.work(request);
                }
            }
        }
        
    }
    
    private static List<AbstractHeaderWorker> gotoList(String key, String value)
    {
        if (!HEADER_WORKERS.containsKey(key))
            HEADER_WORKERS.put(key, new HashMap<>());
        Map<String,List<AbstractHeaderWorker>> section=HEADER_WORKERS.get(key);
        
        if (!section.containsKey(value))
            section.put(value, new ArrayList<>());
        
        return section.get(value);
    }
    
}
