/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

import at.itopen.simplerest.security.BasicUser;
import at.itopen.simplerest.security.DefaultUser;
import at.itopen.simplerest.security.RestSecurity;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class Request {

    private String protocolName;
    private int protocolMajorVersion, protocolMinorVersion;
    private String method;
    private Uri uri;
    private IpAdress sourceIp=null;
    private final HttpHeaders headers;
    private String contentData = null;
    private Map<String, String> params;
    private List<Cookie> cookies;
    private transient ChannelHandlerContext ctx;
    private Map<String,MultipartFile> files;
    private transient HttpPostRequestDecoder httpDecoder = null;
    private BasicUser user;

    public Request(ChannelHandlerContext ctx) {

        this.ctx = ctx;
        headers = new HttpHeaders();
        protocolName = "EMPTY";
        protocolMajorVersion = 0;
        protocolMinorVersion = 0;
        method = "NONE";
        uri = null;
        params = new HashMap<>();
        files = new HashMap<>();
        cookies = new ArrayList<>();
        sourceIp=new IpAdress((InetSocketAddress)ctx.channel().remoteAddress());
        try {
            user = (BasicUser) RestSecurity.getUserClass().getConstructor().newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (user == null) {
            user = new DefaultUser();
        }

    }

    public IpAdress getSourceIp() {
        return sourceIp;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }
    
    
    public BasicUser getUser() {
        return user;
    }

    public void parse(Object msg) {
        if (msg instanceof HttpRequest) {

            HttpRequest request = (HttpRequest) msg;
            protocolName = request.protocolVersion().protocolName();
            protocolMajorVersion = request.protocolVersion().majorVersion();
            protocolMinorVersion = request.protocolVersion().minorVersion();
            method = request.method().name();
            uri = new Uri(request.uri());
            params.putAll(uri.queryParam);
            headers.addHeaders(request.headers());
            if (method.equals("POST")) {
                httpDecoder = new HttpPostRequestDecoder((HttpRequest) msg);
            }

        }
        if (msg instanceof HttpContent) {

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;
                headers.addHeaders(trailer.trailingHeaders());
            }

            HttpContent content = (HttpContent) msg;
            if (!(content.content() instanceof EmptyByteBuf)) {
                contentData = content.content().getCharSequence(0, content.content().capacity(), Charset.forName("UTF-8")).toString();
                if (getHttpDecoder() != null) {
                    getHttpDecoder().offer(content);
                }
            }

        }
    }

    public HttpPostRequestDecoder getHttpDecoder() {
        return httpDecoder;
    }

    /**
     * @return the protocolName
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * @return the protocolMajorVersion
     */
    public int getProtocolMajorVersion() {
        return protocolMajorVersion;
    }

    /**
     * @return the protocolMinorVersion
     */
    public int getProtocolMinorVersion() {
        return protocolMinorVersion;
    }

    public Map<String,MultipartFile> getFiles() {
        return files;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public String getParam(String name, String defaultValue) {
        if (params.containsKey(name)) {
            return params.get(name);
        } else {
            return defaultValue;
        }
    }

    public String getParam(String name) {
        return getParam(name, null);
    }

    /**
     * @return the uri
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * @return the headers
     */
    public HttpHeaders getHeaders() {
        return headers;
    }

    /**
     * @return the contentData
     */
    public String getContentData() {
        return contentData;
    }

    public void setContentData(String contentData) {
        this.contentData = contentData;
    }

    public void clearContentData() {
        this.contentData = null;
    }

    public long getContentLength() {
        if (hasContent()) {
            return contentData.length();
        } else {
            return 0;
        }
    }

    public boolean hasContent() {
        return contentData != null;
    }

}
