/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author roland
 */
public class JsonIT {

    /**
     *
     */
    public JsonIT() {
    }

    /**
     * Test of fromString method, of class Json.
     */
    @Test
    public void testFromString_String_Class() {
        System.out.println("fromString");
        List<String> data = new ArrayList<>();
        data.add("Test1");
        data.add("'\"Test2\"'");

        String json = Json.toString(data);
        List<String> result = Json.fromString(json, List.class);

        assertEquals(data, result);

    }

}
