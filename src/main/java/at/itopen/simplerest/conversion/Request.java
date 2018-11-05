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
    private IpAdress sourceIp = null;
    private final HttpHeaders headers;
    private String contentData = null;
    private Map<String, String> params;
    private List<Cookie> cookies;
    private transient ChannelHandlerContext ctx;
    private Map<String, MultipartFile> files;
    private transient HttpPostRequestDecoder httpDecoder = null;
    private BasicUser user;

    /**
     *
     * @param ctx
     */
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
        sourceIp = new IpAdress((InetSocketAddress) ctx.channel().remoteAddress());

    }

    /**
     *
     * @return
     */
    public IpAdress getSourceIp() {
        return sourceIp;
    }

    /**
     *
     * @return
     */
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     *
     * @return
     */
    public BasicUser getUser() {
        if (user == null) {
            try {
                user = (BasicUser) RestSecurity.getUserClass().getConstructor().newInstance();
            } catch (NoSuchMethodException | SecurityException | InstantiationException | InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
                Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (user == null) {
                user = new DefaultUser();
            }
        }
        return user;
    }

    /**
     *
     * @param msg
     */
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

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
    public Map<String, MultipartFile> getFiles() {
        return files;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void addParam(String key, String value) {
        params.put(key, value);
    }

    /**
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public String getParam(String name, String defaultValue) {
        if (params.containsKey(name)) {
            return params.get(name);
        } else {
            return defaultValue;
        }
    }

    /**
     *
     * @param name
     * @return
     */
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

    /**
     *
     * @param contentData
     */
    public void setContentData(String contentData) {
        this.contentData = contentData;
    }

    /**
     *
     */
    public void clearContentData() {
        this.contentData = null;
    }

    /**
     *
     * @return
     */
    public long getContentLength() {
        if (hasContent()) {
            return contentData.length();
        } else {
            return 0;
        }
    }

    /**
     *
     * @return
     */
    public boolean hasContent() {
        return contentData != null;
    }

}
