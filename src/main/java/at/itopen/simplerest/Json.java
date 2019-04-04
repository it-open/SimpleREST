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
public class Json {

    private static final ObjectMapper JSON_CONVERTER = new ObjectMapper();

    /**
     * A Global Json Converter vrom Jackson
     *
     * @return
     */
    public static ObjectMapper getJSON_CONVERTER() {
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
     * @param <T>
     * @param data
     * @param type
     * @return
     */
    public static <T> T fromString(String data, Class<T> type) {
        try {
            return getJSON_CONVERTER().readValue(data, type);
        } catch (IOException ex) {
            Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param <T>
     * @param data
     * @param type
     * @return
     */
    public static <T> T fromString(String data, TypeReference type) {
        try {
            return getJSON_CONVERTER().readValue(data, type);
        } catch (IOException ex) {
            Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param data
     * @return
     */
    public static String toString(Object data) {
        try {
            return getJSON_CONVERTER().writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param data
     * @return
     */
    public static JsonNode fromString(String data) {
        try {
            return getJSON_CONVERTER().readTree(data);
        } catch (IOException ex) {
            Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param jsonNode
     * @return
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
