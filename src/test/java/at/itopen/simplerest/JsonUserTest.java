/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

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
public class JsonUserTest {
    
    public JsonUserTest() {
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
     * Test of getUsername method, of class JsonUser.
     */
    @org.junit.jupiter.api.Test
    public void testUsername() {
        System.out.println("Username");
        JsonUser instance = new JsonUser();
        String expResult = "";
        assertEquals(expResult, instance.getUsername());
        instance.setUsername("USER");
        assertEquals("USER", instance.getUsername());
    }
    
     /**
     * Test of getUsername method, of class JsonUser.
     */
    @org.junit.jupiter.api.Test
    public void testPassword() {
        System.out.println("Password");
        JsonUser instance = new JsonUser();
        String expResult = "";
        assertEquals(expResult, instance.getPassword());
        instance.setPassword("Password");
        assertEquals("Password", instance.getPassword());
    }

    
}
