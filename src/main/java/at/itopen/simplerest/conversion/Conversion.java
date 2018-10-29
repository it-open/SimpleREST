/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class Conversion {
    
    private Request request;
    private final Response response;
    private final ChannelHandlerContext ctx;
    private Exception exception;
    private long startTime=System.currentTimeMillis();
    
    public Conversion(ChannelHandlerContext ctx) {
        startTime=System.nanoTime();
        this.ctx=ctx;
        response=new Response();
        request=new Request(ctx);
    }
    
    public void parse(Object msg)
    {
        try{
            request.parse(msg);
        } catch (Exception ex)
        {
            Logger.getLogger(Conversion.class.getName()).log(Level.SEVERE, ex.getMessage(),ex);
        }
        finally{
            ReferenceCountUtil.release(msg);
        }
    }
    
    public void destroy()
    {
        ctx.flush();
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
    
    public long getNanoDuration()
    {
     return System.nanoTime()-startTime;
    }
    
    
    
    
    
    
    
}
