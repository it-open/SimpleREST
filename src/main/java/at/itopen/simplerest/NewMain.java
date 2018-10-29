/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.ErrorEndpoint;
import at.itopen.simplerest.endpoints.IndexEndpoint;
import at.itopen.simplerest.endpoints.NotFoundEndpoint;
import at.itopen.simplerest.path.RestEndpoint;
import at.itopen.simplerest.path.RootPath;
import java.util.List;
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
        RestHttpServer ht=new RestHttpServer(8081);
        try {
            RootPath.setINDEX(new IndexEndpoint("TestProg", "1.0", "IT-Open", "office@it-open.at"));
            RootPath.setEXCEPTION(new ErrorEndpoint());
            RootPath.setNOT_FOUND(new NotFoundEndpoint());
            RootPath.getROOT().addRestEndpoint(new RestEndpoint("test"){
                @Override
                public void Call(Conversion conversion, List<String> UrlParameter) {
                    conversion.getResponse().setData("Super");
                }
            
            });
            ht.run();
        } catch (Exception ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        
    }
    
}
