/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.conversion.ContentType;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.HttpStatus;
import at.itopen.simplerest.headerworker.Headerworker;
import at.itopen.simplerest.path.EndpointWorker;
import at.itopen.simplerest.path.RootPath;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class RestHttpRequestDispatchHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = Logger.getLogger(RestHttpRequestDispatchHandler.class.getName());
    private static final ObjectMapper JSON_CONVERTER = new ObjectMapper();
    private final Map<Integer, Conversion> connections = new HashMap<>();
    
    static{
        JSON_CONVERTER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        JSON_CONVERTER.setDefaultPrettyPrinter(new MinimalPrettyPrinter());
        JSON_CONVERTER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static ObjectMapper getJSON_CONVERTER() {
        return JSON_CONVERTER;
    }
    
    

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            if ((msg instanceof HttpMessage) && HttpUtil.is100ContinueExpected((HttpMessage) msg)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }

            Conversion conversion = connections.get(ctx.hashCode());
            conversion.parse(msg);

            
        } catch (Exception ex) {
            ReferenceCountUtil.release(msg);
        }
    }
    
    
    
    private class ResponseWrapper{
        public int code;
        public String message;
        public double generationMsSeconds;
        public Object data;

        public ResponseWrapper(int code, String message, long generationNanoSeconds, Object data) {
            this.code = code;
            this.message = message;
            this.generationMsSeconds=generationNanoSeconds/1000000.0;
            this.data = data;
        }

        
        
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        Conversion conversion = connections.get(ctx.hashCode());
        if ((conversion.getRequest().getHttpDecoder().isMultipart()) && (!conversion.getRequest().getHttpDecoder().hasNext()))
            return;
        System.out.println(conversion.getRequest().getMethod()+" "+conversion.getRequest().getUri()+" ("+ctx.hashCode()+")");
        Headerworker.work(conversion.getRequest());
        EndpointWorker worker = null;
        
        try{
            if (conversion.getRequest().getUri().getPath().size() == 0) {
                if (RootPath.getINDEX()!=null)
                    worker = new EndpointWorker(RootPath.getINDEX(), null);
            }
            else {
                worker = RootPath.getROOT().findEndpoint(conversion, 0, new HashMap<>());
            }
            if (worker != null) {
                conversion.getResponse().setContentType(ContentType.JSON);
                conversion.getResponse().setStatus(HttpStatus.OK);
                worker.work(conversion);
            } else {
                conversion.getResponse().setStatus(HttpStatus.NotFound);
                if (RootPath.getNOT_FOUND() != null) {
                    conversion.getResponse().setContentType(ContentType.JSON);
                    RootPath.getNOT_FOUND().CallEndpoint(conversion, null);
                }
            }
        }catch(Exception e)
        {
            conversion.setException(e);
            conversion.getResponse().setStatus(HttpStatus.InternalServerError);
            if (RootPath.getEXCEPTION()!=null){
                    worker = new EndpointWorker(RootPath.getEXCEPTION(), null);
                    conversion.getResponse().setContentType(ContentType.JSON);
                    worker.work(conversion);
            }
            
        }

        if (conversion.getResponse().hasData()) {
            if (conversion.getResponse().getContentType().equals(ContentType.JSON)) {
                ResponseWrapper wrapper=new ResponseWrapper(conversion.getResponse().getStatus().getCode(), conversion.getResponse().getStatus().getDescription(),conversion.getNanoDuration(), conversion.getResponse().getData());
                String json = JSON_CONVERTER.writeValueAsString(wrapper);
                ByteBuf bb = Unpooled.copiedBuffer(json, Charset.defaultCharset());
                writeJSON(ctx, HttpResponseStatus.valueOf(conversion.getResponse().getStatus().getCode()), bb);
            } else {
                ByteBuf bb = null;
                if (conversion.getResponse().getData() instanceof String) {
                    bb = Unpooled.copiedBuffer((String) conversion.getResponse().getData(), Charset.defaultCharset());
                }
                if (conversion.getResponse().getData() instanceof byte[]) {
                    bb = Unpooled.copiedBuffer((byte[]) conversion.getResponse().getData());
                }
                if (bb != null) {
                    write(ctx, HttpResponseStatus.valueOf(conversion.getResponse().getStatus().getCode()), bb, conversion.getResponse().getContentType().getMimeType());
                } else {
                    write(ctx, HttpResponseStatus.SERVICE_UNAVAILABLE, Unpooled.EMPTY_BUFFER, conversion.getResponse().getContentType().getMimeType());
                }
            }
            
        }else
        {
            write (ctx,HttpResponseStatus.valueOf(conversion.getResponse().getStatus().getCode()),null,conversion.getResponse().getContentType().getMimeType());
        }

        ctx.flush();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx); //To change body of generated methods, choose Tools | Templates.
        connections.put(ctx.hashCode(), new Conversion(ctx));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx); //To change body of generated methods, choose Tools | Templates.
        connections.remove(ctx.hashCode());
    }

    private static void writeJSON(ChannelHandlerContext ctx, HttpResponseStatus status, ByteBuf content) {
        write(ctx, status, content, "application/json; charset=utf-8");
    }

    private static void write(ChannelHandlerContext ctx, HttpResponseStatus status, ByteBuf content, String contentType) {
        if (ctx.channel().isWritable()) {
            FullHttpResponse msg = null;
            if (content != null) {
                msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
                msg.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
            } else {
                msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
            }

            if (msg.content() != null) {
                msg.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.content().readableBytes());
            }

            // not keep-alive
            ctx.write(msg).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
