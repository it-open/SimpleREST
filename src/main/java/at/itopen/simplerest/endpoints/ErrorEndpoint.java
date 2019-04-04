/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.path.RestEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author roland
 */
public class ErrorEndpoint extends RestEndpoint {

    
    private class ErrorData {
        String message;
        List<String> lines=new ArrayList<>();

        public ErrorData(String message) {
            if (message==null)
                message="Null Pointer!";
            this.message = message;
        }
        
        public void addLine(String line)
        {
            lines.add(line);
        }

        public String getMessage() {
            return message;
        }

        public List<String> getLines() {
            return lines;
        }
        
        
        
        
        
    }
        
    private ErrorData data;
    
    /**
     *
     */
    public ErrorEndpoint() {
        super("ERROR");
        
    }
    
    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    @Override
    public void Call(Conversion conversion, Map<String,String> UrlParameter) {
        Exception exception=conversion.getException();
        data=new ErrorData(exception.getMessage());
        for (StackTraceElement stackTraceElement:exception.getStackTrace())
        {
            data.addLine(stackTraceElement.toString());
        }
        conversion.getResponse().setData(data);
        
    }
    
}
