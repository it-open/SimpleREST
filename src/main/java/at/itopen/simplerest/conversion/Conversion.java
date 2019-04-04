/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

import at.itopen.simplerest.RestHttpServer;
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
    private RestHttpServer server;
    
    
    /**
     *
     * @param ctx
     * @param server
     */
    public Conversion(ChannelHandlerContext ctx,RestHttpServer server) {
        startTime=System.nanoTime();
        this.ctx=ctx;
        response=new Response();
        request=new Request(ctx);
        this.server=server;
    }

    /**
     *
     * @param msg
     */
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
    
    /**
     *
     */
    public void destroy()
    {
        ctx.flush();
    }

    /**
     *
     * @return
     */
    public Request getRequest() {
        return request;
    }

    /**
     *
     * @return
     */
    public Response getResponse() {
        return response;
    }

    /**
     *
     * @param exception
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     *
     * @return
     */
    public Exception getException() {
        return exception;
    }
    
    /**
     *
     * @return
     */
    public long getNanoDuration()
    {
     return System.nanoTime()-startTime;
    }

    /**
     *
     * @return
     */
    public RestHttpServer getServer() {
        return server;
    }
    
    
    
    
    
    
    
    
    
}
