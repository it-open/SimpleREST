/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Request;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class MulitpartFormDataHeaderWorker extends AbstractHeaderWorker{

    @Override
    public void work(Request request) {
        
        if (request.getHttpDecoder()!=null)
            {
                //System.out.println(request.getHttpDecoder().isMultipart());
                for (InterfaceHttpData interfaceHttpData: request.getHttpDecoder().getBodyHttpDatas())
                {
                    if (interfaceHttpData instanceof Attribute)
                    {
                        Attribute attribute=(Attribute)interfaceHttpData;
                        try {
                            request.addParam(attribute.getName(), attribute.getValue());
                        } catch (IOException ex) {
                            Logger.getLogger(MulitpartFormDataHeaderWorker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }
              
            }
        
        
        
        request.getHeaders().remove("content-type");
        request.getHeaders().remove("content-length");
        request.setContentData(null);
        
    }
    
    /**
   * Extracts and returns the boundary token from a line.
   * 
   * @return the boundary token.
   */
  private String extractBoundary(String line) {
    // Use lastIndexOf() because IE 4.01 on Win98 has been known to send the
    // "boundary=" string multiple times.  Thanks to David Wall for this fix.
    int index = line.lastIndexOf("boundary=");
    if (index == -1) {
      return null;
    }
    String boundary = line.substring(index + 9);  // 9 for "boundary="
    if (boundary.charAt(0) == '"') {
      // The boundary is enclosed in quotes, strip them
      index = boundary.lastIndexOf('"');
      boundary = boundary.substring(1, index);
    }

    // The real boundary is always preceeded by an extra "--"
    boundary = "--" + boundary;

    return boundary;
  }

    
}
