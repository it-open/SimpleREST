/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.conversion.ContentType;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.HttpStatus;
import at.itopen.simplerest.conversion.Response;
import at.itopen.simplerest.headerworker.Headerworker;
import at.itopen.simplerest.path.EndpointWorker;
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

/**
 * The Dispatcher does the Magic. It Handles all Netty requests
 *
 * @author roland
 */
public class RestHttpRequestDispatchHandler extends ChannelInboundHandlerAdapter {

    //private static final Logger LOG = Logger.getLogger(RestHttpRequestDispatchHandler.class.getName());
    private final Map<String, Conversion> connections = new HashMap<>();
    private final RestHttpServer server;

    /**
     *
     * @param server
     */
    public RestHttpRequestDispatchHandler(RestHttpServer server) {
        this.server = server;
    }

    /**
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            if (msg instanceof HttpMessage && HttpUtil.is100ContinueExpected((HttpMessage) msg)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }

            getConn(ctx).parse(msg);

        } catch (Exception ex) {
            ReferenceCountUtil.release(msg);
        }
    }

    private static class ResponseWrapper {

        public int code;
        public String message;
        public String info;
        public double generationMsSeconds;
        public Object data;

        public ResponseWrapper(int code, String message, String info, long generationNanoSeconds, Object data) {
            this.code = code;
            this.message = message;
            this.info = info;
            this.generationMsSeconds = generationNanoSeconds / 1000000.0;
            this.data = data;
        }

    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        Conversion conversion = getConn(ctx);
        if (conversion.getRequest().getHttpDecoder() != null) {
            if (conversion.getRequest().getHttpDecoder().isMultipart() && !conversion.getRequest().getHttpDecoder().hasNext()) {
                return;
            }
        }
        process(conversion, ctx);
        connections.remove(ctx.channel().id().asLongText());

        ctx.flush();
    }

    /**
     *
     * @param conversion
     * @param ctx
     * @throws Exception
     */
    public static void process(Conversion conversion, ChannelHandlerContext ctx) throws Exception {
        System.out.println(conversion.getRequest().getMethod() + " " + conversion.getRequest().getUri() + " (" + ctx.channel().id().asLongText() + ")");
        Headerworker.work(conversion);
        EndpointWorker worker = null;
        if ("EMPTY".equals(conversion.getRequest().getProtocolName())) {
            return;
        }

        try {
            if (conversion.getRequest().getUri().getPath().isEmpty()) {
                if (conversion.getServer().getRootEndpoint(conversion).getINDEX() != null) {
                    worker = new EndpointWorker(conversion.getServer().getRootEndpoint(conversion).getINDEX(), null);
                }
            } else {
                worker = conversion.getServer().getRootEndpoint(conversion).findEndpoint(conversion, 0, new HashMap<>());
            }
            if (worker != null) {
                conversion.getResponse().setContentType(ContentType.JSON);
                conversion.getResponse().setStatus(HttpStatus.OK);
                worker.work(conversion);
            } else {
                conversion.getResponse().setStatus(HttpStatus.NotFound);
                if (conversion.getServer().getRootEndpoint(conversion).getNOTFOUND() != null) {
                    conversion.getResponse().setContentType(ContentType.JSON);
                    conversion.getServer().getRootEndpoint(conversion).getNOTFOUND().CallEndpoint(conversion, null);
                }
            }
        } catch (Exception e) {
            conversion.setException(e);
        }

        if (conversion.getException() != null) {
            conversion.getResponse().setStatus(HttpStatus.InternalServerError);
            if (conversion.getServer().getRootEndpoint(conversion).getEXCEPTION() != null) {
                worker = new EndpointWorker(conversion.getServer().getRootEndpoint(conversion).getEXCEPTION(), null);
                conversion.getResponse().setContentType(ContentType.JSON);
                worker.work(conversion);
            }
        }

        if (conversion.getResponse().hasData()) {
            if (conversion.getResponse().getContentType().equals(ContentType.JSON)) {
                String json = "";
                if (conversion.getResponse().isWrapJson()) {
                    ResponseWrapper wrapper = new ResponseWrapper(conversion.getResponse().getStatus().getCode(), conversion.getResponse().getStatus().getDescription(), conversion.getResponse().getStatusMessage(), conversion.getNanoDuration(), conversion.getResponse().getData());
                    json = JsonHelper.toString(wrapper);
                } else if (conversion.getResponse().getData() instanceof String) {
                    if (conversion.getResponse().isConvertStringToJson()) {
                        json = JsonHelper.toString(conversion.getResponse().getData());
                    } else {
                        json = (String) conversion.getResponse().getData();
                    }

                } else {
                    json = JsonHelper.toString(conversion.getResponse().getData());
                }
                ByteBuf bb = Unpooled.copiedBuffer(json, Charset.defaultCharset());
                writeJSON(ctx, HttpResponseStatus.valueOf(conversion.getResponse().getStatus().getCode()), bb, conversion.getResponse());
            } else {
                ByteBuf bb = null;
                if (conversion.getResponse().getData() instanceof String) {
                    bb = Unpooled.copiedBuffer((String) conversion.getResponse().getData(), Charset.defaultCharset());
                }
                if (conversion.getResponse().getData() instanceof byte[]) {
                    bb = Unpooled.copiedBuffer((byte[]) conversion.getResponse().getData());
                }
                if (bb != null) {
                    write(ctx, HttpResponseStatus.valueOf(conversion.getResponse().getStatus().getCode()), bb, conversion.getResponse().getContentType().getMimeType(), conversion.getResponse());
                } else {
                    ResponseWrapper wrapper = new ResponseWrapper(conversion.getResponse().getStatus().getCode(), conversion.getResponse().getStatus().getDescription(), conversion.getResponse().getStatusMessage(), conversion.getNanoDuration(), conversion.getResponse().getData());
                    String json = JsonHelper.toString(wrapper);
                    bb = Unpooled.copiedBuffer(json, Charset.defaultCharset());
                    writeJSON(ctx, HttpResponseStatus.valueOf(conversion.getResponse().getStatus().getCode()), bb, conversion.getResponse());
                }
            }

        } else {
            ResponseWrapper wrapper = new ResponseWrapper(conversion.getResponse().getStatus().getCode(), conversion.getResponse().getStatus().getDescription(), conversion.getResponse().getStatusMessage(), conversion.getNanoDuration(), conversion.getResponse().getData());
            String json = JsonHelper.toString(wrapper);
            ByteBuf bb = Unpooled.copiedBuffer(json, Charset.defaultCharset());
            writeJSON(ctx, HttpResponseStatus.valueOf(conversion.getResponse().getStatus().getCode()), bb, conversion.getResponse());

        }

    }

    /**
     *
     * @param ctx
     * @return
     */
    public Conversion getConn(ChannelHandlerContext ctx) {
        String key = ctx.channel().id().asLongText();
        if (connections.containsKey(key)) {
            return connections.get(key);
        } else {
            Conversion c = new Conversion(ctx, server);
            connections.put(key, c);
            return c;
        }
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx); //To change body of generated methods, choose Tools | Templates.

        getConn(ctx);
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx); //To change body of generated methods, choose Tools | Templates.
        connections.remove(ctx.channel().id().asLongText());
    }

    private static void writeJSON(ChannelHandlerContext ctx, HttpResponseStatus status, ByteBuf content, Response response) {
        write(ctx, status, content, "application/json; charset=utf-8", response);
    }

    private static void write(ChannelHandlerContext ctx, HttpResponseStatus status, ByteBuf content, String contentType, Response response) {
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
            if (response.getCookies().size() > 0) {
                msg.headers().set(HttpHeaderNames.SET_COOKIE, response.getCookieString());
            }
            for (Map.Entry<String, String> headerField : response.getHeaderData().entrySet()) {
                msg.headers().set(headerField.getKey(), headerField.getValue());
            }

            // not keep-alive
            ctx.write(msg).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
