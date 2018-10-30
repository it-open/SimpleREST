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
        
       
        
        CRUDHelper helper;
        RestHttpServer ht=new RestHttpServer(8081);
        try {
            RootPath.setINDEX(new IndexEndpoint("TestProg", "1.0", "IT-Open", "office@it-open.at"));
            RootPath.setEXCEPTION(new ErrorEndpoint());
            RootPath.setNOT_FOUND(new NotFoundEndpoint());
            RootPath.getROOT().addRestEndpoint(new StructureEndpoint("structure"));
            RootPath.getROOT().addRestEndpoint(new RestEndpoint("test"){
                @Override
                public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                    conversion.getResponse().setData("Super");
                }
            
            });
            RootPath.getROOT().addRestEndpoint(new RestEndpoint("upload"){
                @Override
                public void Call(Conversion conversion, Map<String,String> UrlParameter) {
                    System.out.println(conversion.getRequest().getFiles().get("data").getName());
                }
            
            });
            
            RootPath.getROOT().addRestEndpoint(new JsonUserEndpoint("post"));
            
            final List<String> data= new ArrayList<>();
                    data.add("Hallo");
                    data.add("Roland");
            
            helper=new CRUDHelper("data", RootPath.getROOT()) {
                
                
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
            ht.run();
        } catch (Exception ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        
    }
    
}
