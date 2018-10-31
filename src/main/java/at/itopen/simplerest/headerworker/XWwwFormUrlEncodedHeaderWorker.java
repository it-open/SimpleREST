/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Request;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class XWwwFormUrlEncodedHeaderWorker extends AbstractHeaderWorker{

    /**
     *
     * @param request
     */
    @Override
    public void work(Request request) {
        String data=request.getContentData();
        try {
            data = URLDecoder.decode(data, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(XWwwFormUrlEncodedHeaderWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        decodeParams(data, request);
        request.getHeaders().remove("content-type");
        request.getHeaders().remove("content-length");
        request.setContentData(null);
        
    }
    
    private void decodeParams(String line,Request request)
    {
            for (String param:line.split("&")){
                String[] parts=param.split("=");
                if (parts.length==2)
                {
                    request.addParam(parts[0], parts[1]);
                }
            }
            
       
    }
    
}
