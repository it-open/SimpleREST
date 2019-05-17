/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 *
 * @author roland
 */
public class RestHttpServerInitializer extends ChannelInitializer<SocketChannel> {

    RestHttpServer webserver;

    /**
     *
     * @param webserver
     */
    public RestHttpServerInitializer(RestHttpServer webserver) {
        this.webserver = webserver;
    }

    /**
     *
     * @param socketChannel
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast("http.decoder", new HttpRequestDecoder());
        //p.addLast("");
        p.addLast("http.encoder", new HttpResponseEncoder());
        p.addLast("deflater", new HttpContentCompressor(1));
        //p.addLast("http.deflater", new HttpResponseEncoder());
        p.addLast("http.request-handler", new RestHttpRequestDispatchHandler(webserver));
    }

}
