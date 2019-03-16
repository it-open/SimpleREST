/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author roland
 */
public class JsonIT {

    public JsonIT() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
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
