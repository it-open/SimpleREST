/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.endpoints.ErrorEndpoint;
import at.itopen.simplerest.endpoints.IndexEndpoint;
import at.itopen.simplerest.endpoints.NotFoundEndpoint;
import at.itopen.simplerest.endpoints.StructureEndpoint;
import at.itopen.simplerest.endpoints.UrlListEndpoint;
import at.itopen.simplerest.path.RestPath;
import at.itopen.simplerest.path.RootPath;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 */
public class RestHttpServer {

    private final int port;
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final EventLoopGroup worker;

    public static RestHttpServer Start(int port) {
        final RestHttpServer restHttpServer = new RestHttpServer(port);
        new Thread("Start Server") {
            @Override
            public void run() {
                try {
                    restHttpServer.run();
                } catch (Exception ex) {
                    Logger.getLogger(RestHttpServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
        return restHttpServer;
    }

    public static void enableIndex(String programmName, String apiVersion, String maintainer, String email) {
        RootPath.setINDEX(new IndexEndpoint(programmName, apiVersion, maintainer, email));
    }

    public static void enableExceptionHandling() {
        RootPath.setEXCEPTION(new ErrorEndpoint());
    }

    public static void enableNotFoundHandling() {
        RootPath.setNOT_FOUND(new NotFoundEndpoint());
    }

    public static void enableStructure(String structurePath) {
        enableStructure(structurePath, null);
    }
    
    public static void enableStructure(String structurePath,RestPath path) {
        if (path==null)
            path=RootPath.getROOT();
        path.addRestEndpoint(new StructureEndpoint(structurePath));
    }
    
    public static void enableRestUrlList(String urlListPath){
        enableRestUrlList(urlListPath, null);
    }
    
    public static void enableRestUrlList(String urlListPath,RestPath path) {
        if (path==null)
            path=RootPath.getROOT();
        path.addRestEndpoint(new UrlListEndpoint(urlListPath));
    }

    public static RestPath getRootEndpoint() {
        return RootPath.getROOT();
    }

    public RestHttpServer() {
        this(SystemPropertyUtil.getInt("default.port", 18080));
    }

    public RestHttpServer(int port) {
        this.port = port;
        this.worker = new NioEventLoopGroup(SystemPropertyUtil.getInt("events.workerThreads", 300), new DefaultThreadFactory("nio-worker", Thread.MAX_PRIORITY));
        RootPath.setROOT(new RestPath("/"));
    }

    public void run() throws Exception {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class).childHandler(
                    new RestHttpServerInitializer());
            setChannelOptions(bootstrap);

            Channel ch = bootstrap.bind(port).sync().channel();

            System.out.println(">> startUp server [" + ch.localAddress().toString() + "]");

            ch.closeFuture().sync(); // blocked

        } finally {
            shutdown();
        }
    }

    protected void shutdown() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

    protected void setChannelOptions(ServerBootstrap bootstrap) {
        bootstrap.childOption(ChannelOption.MAX_MESSAGES_PER_READ, 36)
                .childOption(ChannelOption.TCP_NODELAY, true);
    }

}
