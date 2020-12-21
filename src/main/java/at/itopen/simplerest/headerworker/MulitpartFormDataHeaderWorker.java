/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.MultipartFile;
import at.itopen.simplerest.conversion.Request;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class MulitpartFormDataHeaderWorker extends AbstractHeaderWorker {

    /**
     *
     * @param conversion
     */
    @Override
    public void work(Conversion conversion) {
        Request request = conversion.getRequest();

        if (request.getHttpDecoder() != null) {
            //System.out.println(request.getHttpDecoder().isMultipart());
            while (request.getHttpDecoder().hasNext()) {
                InterfaceHttpData interfaceHttpData = request.getHttpDecoder().next();
                {
                    if (interfaceHttpData instanceof Attribute) {
                        Attribute attribute = (Attribute) interfaceHttpData;
                        try {
                            request.addParam(attribute.getName(), attribute.getValue());
                        } catch (IOException ex) {
                            Logger.getLogger(MulitpartFormDataHeaderWorker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (interfaceHttpData instanceof MixedFileUpload) {
                        MixedFileUpload mixedFileUpload = (MixedFileUpload) interfaceHttpData;
                        try {
                            MultipartFile multipartFile;
                            multipartFile = new MultipartFile(mixedFileUpload.get(), mixedFileUpload.getFilename(), mixedFileUpload.getContentType());
                            request.getFiles().put(mixedFileUpload.getName(), multipartFile);
                        } catch (IOException ex) {
                            Logger.getLogger(MulitpartFormDataHeaderWorker.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }
            }

        }

        request.getHeaders().remove("content-type");
        request.getHeaders().remove("content-length");
        request.setContentData(null);

    }

}
