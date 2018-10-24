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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

	private static Logger log = Logger.getLogger(RestHttpRequestDispatchHandler.class.getName());
        private static Gson gson=new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        private Map<Integer,Conversion> connections= new HashMap<>();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		try {
			if ((msg instanceof HttpMessage) && HttpUtil.is100ContinueExpected((HttpMessage)msg))
				ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
                        
			Conversion conversion=connections.get(ctx.hashCode());
                        conversion.parse(msg);
                        
                        
                        //System.out.println(conversion.getRequest().getUri());
		} catch(Exception ex) {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	
                        Conversion conversion=connections.get(ctx.hashCode());
                        Headerworker.work(conversion.getRequest());
                        //Business Logic
                        conversion.getResponse().setData(conversion.getRequest());
                        conversion.getResponse().setContentType(ContentType.JSON);
                        conversion.getResponse().setStatus(HttpStatus.OK);
                        
                        
                        if (conversion.getResponse().hasData()) {
                            if (conversion.getResponse().getContentType().equals(ContentType.JSON))
                            {
                                String json=gson.toJson(conversion.getResponse().getData());
                                ByteBuf bb=Unpooled.copiedBuffer(json, Charset.defaultCharset());
                                writeJSON(ctx, HttpResponseStatus.valueOf(conversion.getResponse().getStatus().getCode()), bb);
                            }
                            else
                            {
                                ByteBuf bb=null;
                                if (conversion.getResponse().getData() instanceof String)
                                    bb=Unpooled.copiedBuffer((String)conversion.getResponse().getData(), Charset.defaultCharset());
                                if (conversion.getResponse().getData() instanceof byte[])
                                    bb=Unpooled.copiedBuffer((byte[])conversion.getResponse().getData());
                                if (bb!=null)
                                    write(ctx,HttpResponseStatus.valueOf(conversion.getResponse().getStatus().getCode()),bb,conversion.getResponse().getContentType().getMimeType());
                                else
                                    write(ctx,HttpResponseStatus.SERVICE_UNAVAILABLE,Unpooled.EMPTY_BUFFER,conversion.getResponse().getContentType().getMimeType());
                            }
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
        
        private static void write(ChannelHandlerContext ctx, HttpResponseStatus status, ByteBuf content,String contentType) {
		if (ctx.channel().isWritable()) {
			FullHttpResponse msg = null;
			if (content != null) {
				msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
				msg.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
			} else {
				msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
			}

			if (msg.content() != null)
				msg.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.content().readableBytes());

			// not keep-alive
			ctx.write(msg).addListener(ChannelFutureListener.CLOSE);
		}
	}

	

}