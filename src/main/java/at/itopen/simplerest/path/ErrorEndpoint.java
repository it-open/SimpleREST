/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

import at.itopen.simplerest.conversion.Conversion;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author roland
 */
public class ErrorEndpoint extends RestEndpoint {

    
    private class ErrorData {
        String message;
        List<String> lines=new ArrayList<>();

        public ErrorData(String message) {
            this.message = message;
        }
        
        public void addLine(String line)
        {
            lines.add(line);
        }
        
        
        
    }
        
    private ErrorData data;
    
    
    
    public ErrorEndpoint() {
        super("ERROR");
        
    }
    
    
    
    

    @Override
    public void Call(Conversion conversion, List<String> UrlParameter) {
        Exception exception=conversion.getException();
        data=new ErrorData(exception.getMessage());
        for (StackTraceElement stackTraceElement:exception.getStackTrace())
        {
            data.addLine(stackTraceElement.toString());
        }
        conversion.getResponse().setData(data);
        
    }
    
}
