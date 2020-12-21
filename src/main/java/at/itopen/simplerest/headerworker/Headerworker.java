/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Conversion;
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

    private final static Map<String, Map<String, List<AbstractHeaderWorker>>> HEADER_WORKERS = new HashMap<>();

    static {
        addWorker("authorization", "", new SeperatorDataHeaderWorker("authorization", " "));
        addWorker("content-type", "", new SeperatorDataHeaderWorker("content-type", ";"));
        addWorker("cookie", "", new CookieDataHeaderWorker());
        addWorker("accept-language", "", new SeperatorDataHeaderWorker("accept-language", ";"));
        addWorker("content-type", "application/x-www-form-urlencoded", new XWwwFormUrlEncodedHeaderWorker());
        addWorker("content-type", "multipart/form-data", new MulitpartFormDataHeaderWorker());
        addWorker("authorization", "Basic", new AuthorizationBasicDataHeaderWorker());
        addWorker("authorization", "Bearer", new AuthorizationBearerDataHeaderWorker());
        addWorker("content-type", "application/json", new JsonDataWorker());
    }

    /**
     *
     * @param key
     * @param value
     * @param abstractHeaderWorker
     */
    public static void addWorker(String key, String value, AbstractHeaderWorker abstractHeaderWorker) {
        gotoList(key, value).add(abstractHeaderWorker);
    }

    /**
     *
     * @param key
     * @param value
     */
    public static void clearWorkers(String key, String value) {
        gotoList(key, value).clear();
    }

    /**
     *
     * @param conversion
     */
    public static void work(Conversion conversion) {
        Request request = conversion.getRequest();
        List<String> names = new ArrayList<>();
        names.addAll(request.getHeaders().getNames());
        for (String name : names) {
            for (AbstractHeaderWorker abstractHeaderWorker : gotoList(name, "")) {
                abstractHeaderWorker.work(conversion);
            }
            for (String value : request.getHeaders().getAll(name)) {
                for (AbstractHeaderWorker abstractHeaderWorker : gotoList(name, value)) {
                    abstractHeaderWorker.work(conversion);
                }
            }
        }

    }

    private static List<AbstractHeaderWorker> gotoList(String key, String value) {
        if (!HEADER_WORKERS.containsKey(key)) {
            HEADER_WORKERS.put(key, new HashMap<>());
        }
        Map<String, List<AbstractHeaderWorker>> section = HEADER_WORKERS.get(key);

        if (!section.containsKey(value)) {
            section.put(value, new ArrayList<>());
        }

        return section.get(value);
    }

}
