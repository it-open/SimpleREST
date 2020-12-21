/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public final class JsonHelper {

    private static final ObjectMapper JSON_CONVERTER = new ObjectMapper();

    private JsonHelper() {
    }

    /**
     * A Global JsonHelper Converter vrom Jackson
     *
     * @return
     */
    public static ObjectMapper getJsonConverter() {
        return JSON_CONVERTER;
    }

    static {
        //JSON_CONVERTER.registerModule(new AfterburnerModule().setUseValueClassLoader(false));
        JSON_CONVERTER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        JSON_CONVERTER.setDefaultPrettyPrinter(null);
        JSON_CONVERTER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JSON_CONVERTER.configure(SerializationFeature.EAGER_SERIALIZER_FETCH, true);
        JSON_CONVERTER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        JSON_CONVERTER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    /**
     *
     * @param <T> Type
     * @param data Data
     * @param type typeClass
     * @return Object fromString
     */
    public static <T> T fromString(String data, Class<T> type) {
        try {
            return getJsonConverter().readValue(data, type);
        } catch (IOException ex) {
            Logger.getLogger(JsonHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param <T> Type
     * @param data Data
     * @param type type Reference
     * @return Object fromString
     */
    public static <T> T fromString(String data, TypeReference type) {
        try {
            return (T) getJsonConverter().readValue(data, type);
        } catch (IOException ex) {
            Logger.getLogger(JsonHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param data Data
     * @return as String
     */
    public static String toString(Object data) {
        try {
            return getJsonConverter().writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param data Data
     * @return Node from String
     */
    public static JsonNode fromString(String data) {
        try {
            return getJsonConverter().readTree(data);
        } catch (IOException ex) {
            Logger.getLogger(JsonHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param jsonNode Node
     * @return Pretty Printed Node
     */
    public static String prettyPrintJsonString(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return "Sorry, pretty print didn't work";
        }
    }

}
