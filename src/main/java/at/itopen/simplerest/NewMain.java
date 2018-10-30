/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.CRUDHelper;
import at.itopen.simplerest.endpoints.ErrorEndpoint;
import at.itopen.simplerest.endpoints.IndexEndpoint;
import at.itopen.simplerest.endpoints.NotFoundEndpoint;
import at.itopen.simplerest.endpoints.StructureEndpoint;
import at.itopen.simplerest.path.RestEndpoint;
import at.itopen.simplerest.path.RootPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
       
        
        RestHttpServer.Start(8081);
        RestHttpServer.enableIndex("TestProg", "1.0", "IT-Open", "office@it-open.at");
        RestHttpServer.enableExceptionHandling();
        RestHttpServer.enableNotFoundHandling();
        RestHttpServer.enableStructure("structure");
        RestHttpServer.enableRestUrlList("urls");
        
        try {
            RestHttpServer.getRootEndpoint().addRestEndpoint(new RestEndpoint("test"){
                @Override
                public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                    conversion.getResponse().setData("Super");
                }
            
            });
            RestHttpServer.getRootEndpoint().addRestEndpoint(new RestEndpoint("upload"){
                @Override
                public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                    System.out.println(conversion.getRequest().getFiles().get("data").getName());
                }
            
            });
            
            RestHttpServer.getRootEndpoint().addRestEndpoint(new JsonUserEndpoint("post"));
            
            final List<String> data= new ArrayList<>();
                    data.add("Hallo");
                    data.add("Roland");
            
            new CRUDHelper("data", RestHttpServer.getRootEndpoint()) {
                
                
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
        } catch (Exception ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        
    }
    
}
