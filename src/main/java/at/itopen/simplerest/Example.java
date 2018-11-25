/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.conversion.ContentType;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.CRUDHelper;
import at.itopen.simplerest.endpoints.GetEndpoint;
import at.itopen.simplerest.endpoints.staticfile.NoCachePolicy;
import at.itopen.simplerest.endpoints.staticfile.StaticFileEndpoint;
import at.itopen.simplerest.path.RestEndpoint;
import at.itopen.simplerest.path.RestPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class Example {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
       
        
        RestHttpServer server=RestHttpServer.Start(3000);
        server.enableIndex("TestProg", "1.0", "IT-Open", "office@it-open.at");
        server.enableExceptionHandling();
        server.enableNotFoundHandling();
        server.enableStructure("structure",null);
        server.enableRestUrlList("urls",null);
        server.enableRestDoc("doc",null);
        
        try {
            server.getRootEndpoint().addRestEndpoint(new RestEndpoint("test"){
                @Override
                public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                    conversion.getResponse().setData("Super");
                }
            
            });
            server.getRootEndpoint().addSubPath(new RestPath("html")).setCatchAllEndPoint(new StaticFileEndpoint(new File("/home/roland/src/bergland-amtstafel/web"), new NoCachePolicy()));
            server.getRootEndpoint().addRestEndpoint(new GetEndpoint("image"){
                @Override
                public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                    conversion.getResponse().setContentType(ContentType.JPEG);
                    FileInputStream fis;
                    try {
                        fis = new FileInputStream("/home/roland/Bilder/Panorama.jpg");
                        conversion.getResponse().setData(fis.readAllBytes());
                        fis.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                }
            
            });
            server.getRootEndpoint().addRestEndpoint(new RestEndpoint("upload"){
                @Override
                public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                    System.out.println(conversion.getRequest().getFiles().get("data").getName());
                }
            
            });
            
            server.getRootEndpoint().addRestEndpoint(new JsonUserEndpoint("post"));
            
            final List<String> data= new ArrayList<>();
                    data.add("Hallo");
                    data.add("Roland");
            
            CRUDHelper helper=new CRUDHelper("data", server.getRootEndpoint()) {
                
                
                @Override
                public void addNewItem(Conversion conversion, Map<String,String> UrlParameter) {
                    data.add("Hallo");
                }
                
                @Override
                public void getSingeItem(Conversion conversion, Map<String,String> UrlParameter) {
                    String index=UrlParameter.get("id");
                    conversion.getResponse().setData(data.get(Integer.parseInt(index)));
                }
                
                @Override
                public void getAllItem(Conversion conversion, Map<String,String> UrlParameter) {
                    conversion.getResponse().setData(data);
                }
                
                @Override
                public void updateItem(Conversion conversion, Map<String,String> UrlParameter) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                
                @Override
                public void deleteItem(Conversion conversion, Map<String,String> UrlParameter) {
                    String index=UrlParameter.get("id");
                    data.remove(Integer.parseInt(index));
                }
            };
            helper.Documentation(JsonUser.class, JsonUser.class, JsonUser.class, "User");
        } catch (Exception ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        
    }
    
}
