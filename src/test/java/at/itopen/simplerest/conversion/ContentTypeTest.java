/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

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
public class ContentTypeTest {
    
    public ContentTypeTest() {
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
     * Test of getSimpleName method, of class ContentType.
     */
    @Test
    public void testGetSimpleName() {
        System.out.println("getSimpleName");
        ContentType instance = ContentType.HTML;
        String expResult = "html";
        String result = instance.getSimpleName();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getMimeType method, of class ContentType.
     */
    @Test
    public void testGetMimeType() {
        System.out.println("getMimeType");
        ContentType instance = ContentType.HTML;
        String expResult = "text/html";
        String result = instance.getMimeType();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getFileExtensions method, of class ContentType.
     */
    @Test
    public void testGetFileExtensions() {
        System.out.println("getFileExtensions");
        ContentType instance = ContentType.HTML;
        String[] expResult = {"html","htm"};
        String[] result = instance.getFileExtensions();
        assertArrayEquals(expResult, result);
   
    }

    /**
     * Test of fromMimeType method, of class ContentType.
     */
    @Test
    public void testFromMimeType() {
        System.out.println("fromMimeType");
        String mimeType = "text/html";
        ContentType expResult = ContentType.HTML;
        ContentType result = ContentType.fromMimeType(mimeType);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of fromFileExtension method, of class ContentType.
     */
    @Test
    public void testFromFileExtension() {
        System.out.println("fromFileExtension");
        String fileExtension = "htm";
        ContentType expResult = ContentType.HTML;
        ContentType result = ContentType.fromFileExtension(fileExtension);
        assertEquals(expResult, result);

    }

    /**
     * Test of fromFileName method, of class ContentType.
     */
    @Test
    public void testFromFileName() {
        System.out.println("fromFileName");
        String fileName = "test.htm";
        ContentType expResult = ContentType.HTML;
        ContentType result = ContentType.fromFileName(fileName);
        assertEquals(expResult, result);
       
    }

   
    
}
