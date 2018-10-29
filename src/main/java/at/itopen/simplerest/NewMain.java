/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.Uri;
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
