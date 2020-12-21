/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.client.RestClient;
import at.itopen.simplerest.client.RestResponse;
import at.itopen.simplerest.client.WrappedResponse;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.GetEndpoint;
import java.util.Map;
import static junit.framework.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author roland
 */
public class SimpleRestTest {

    private static RestHttpServer server;

    public SimpleRestTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = RestHttpServer.start(9000);
        server.getRootEndpoint().addSubPath("test").addRestEndpoint(new GetEndpoint(":id") {
            @Override
            public void call(Conversion conversion, Map<String, String> urlParameter) {
                TestJson tj = new TestJson(urlParameter.get("id"), Integer.MAX_VALUE, Double.MAX_VALUE);
                conversion.getResponse().setData(tj);
            }
        });
        System.out.println("Server started!");

    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testRestGet() {
        RestClient rc = new RestClient("http://localhost:9000/test/hallo", RestClient.RESTMETHOD.GET);
        RestResponse rr = rc.toSingle(false);
        assertNotNull(rr);
        WrappedResponse<TestJson> erg = rr.getWrappedResponse(TestJson.class);
        assertNotNull(erg);
        assertNotNull(erg.getData());
        assertEquals("hallo", erg.getData().getText());
    }
}
