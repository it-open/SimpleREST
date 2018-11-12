/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.endpoints.DocumentationEndpoint;
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
 * The Rest HTTP Server Object handles Netty Calls and does the REST Magic
 * @author roland
 */
public class RestHttpServer {

    private final int port;
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final EventLoopGroup worker;

    /**
     * Constructor, starts the Rest Server
     * @param port Port to run at
     * @return the HTTP Server. Not really needed
     */
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

    /**
     * Enable an index at the /. 
     * @param programmName Name of the Programm
     * @param apiVersion Which Version doe the APi have?
     * @param maintainer Hwo ist the Maintainer
     * @param email Mail of the Maintainer
     */
    public static void enableIndex(String programmName, String apiVersion, String maintainer, String email) {
        RootPath.setINDEX(new IndexEndpoint(programmName, apiVersion, maintainer, email));
    }

    /**
     * Enable Ecxption Lgging if something happens on the API or your Code (With full Stack Trace)
     */
    public static void enableExceptionHandling() {
        RootPath.setEXCEPTION(new ErrorEndpoint());
    }

    /**
     * Enable a 404 Json Page which tells the User that the Page has not been found
     */
    public static void enableNotFoundHandling() {
        RootPath.setNOT_FOUND(new NotFoundEndpoint());
    }

    
    /**
     * Shows the complete Structure of the REST API 
     * @param structurePath
     * @param path
     */
    public static void enableStructure(String structurePath,RestPath path) {
        if (path==null)
            path=RootPath.getROOT();
        path.addRestEndpoint(new StructureEndpoint(structurePath));
    }
    
    
    /**
     * Shows a complete List of all Urls
     * @param urlListPath
     * @param path
     */
    public static void enableRestUrlList(String urlListPath,RestPath path) {
        if (path==null)
            path=RootPath.getROOT();
        path.addRestEndpoint(new UrlListEndpoint(urlListPath));
    }
    
    public static void enableRestDoc(String urlListPath,RestPath path) {
        if (path==null)
            path=RootPath.getROOT();
        path.addRestEndpoint(new DocumentationEndpoint(urlListPath));
    }

    /**
     * Get the Startpoint of all Rest Calls '/'
     * Make all Sub Path on this this Path.
     * @return
     */
    public static RestPath getRootEndpoint() {
        return RootPath.getROOT();
    }

    /**
     *
     */
    public RestHttpServer() {
        this(SystemPropertyUtil.getInt("default.port", 18080));
    }

    /**
     *
     * @param port
     */
    public RestHttpServer(int port) {
        this.port = port;
        this.worker = new NioEventLoopGroup(SystemPropertyUtil.getInt("events.workerThreads", 300), new DefaultThreadFactory("nio-worker", Thread.MAX_PRIORITY));
        RootPath.setROOT(new RestPath("/"));
    }

    /**
     *
     * @throws Exception
     */
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

    /**
     *
     */
    protected void shutdown() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

    /**
     *
     * @param bootstrap
     */
    protected void setChannelOptions(ServerBootstrap bootstrap) {
        bootstrap.childOption(ChannelOption.MAX_MESSAGES_PER_READ, 36)
                .childOption(ChannelOption.TCP_NODELAY, true);
    }

}
