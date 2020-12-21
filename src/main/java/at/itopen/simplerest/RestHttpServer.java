/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.endpoints.DocumentationEndpoint;
import at.itopen.simplerest.endpoints.ErrorEndpoint;
import at.itopen.simplerest.endpoints.IndexEndpoint;
import at.itopen.simplerest.endpoints.NotFoundEndpoint;
import at.itopen.simplerest.endpoints.StructureEndpoint;
import at.itopen.simplerest.endpoints.UrlListEndpoint;
import at.itopen.simplerest.microservice.loadbalancer.LoadBalancer;
import at.itopen.simplerest.microservice.loadbalancer.LoadBalancerConfig;
import at.itopen.simplerest.path.RestEndpoint;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Rest HTTP Server Object handles Netty Calls and does the REST Magic
 *
 * @author roland
 */
public class RestHttpServer {

    private final int port;
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final EventLoopGroup worker;
    private final RootPath root = new RootPath();
    private LoadBalancer loadBalancer = null;
    private final Map<String, RootPath> subdomainpath = new HashMap<String, RootPath>();

    /**
     * Constructor, starts the Rest Server
     *
     * @param port Port to run at
     * @return the HTTP Server. Not really needed
     */
    public static RestHttpServer start(int port) {
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
     *
     * @param subdomain
     * @return
     */
    public synchronized RootPath getRootPathforSubdomain(String subdomain) {
        if (subdomain == null) {
            return root;
        }
        if (subdomain.isEmpty()) {
            return root;
        }
        if (!subdomainpath.containsKey(subdomain)) {
            subdomainpath.put(subdomain, new RootPath());
        }
        return subdomainpath.get(subdomain);
    }

    /**
     * Enable an index at the /.
     *
     * @param programmName Name of the Programm
     * @param apiVersion Which Version doe the APi have?
     * @param maintainer Hwo ist the Maintainer
     * @param email Mail of the Maintainer
     */
    public void enableIndex(String programmName, String apiVersion, String maintainer, String email) {
        root.setINDEX(new IndexEndpoint(programmName, apiVersion, maintainer, email));
    }

    /**
     *
     * @param restEndpoint
     */
    public void enableIndex(RestEndpoint restEndpoint) {
        root.setINDEX(restEndpoint);
    }

    /**
     *
     * @param config
     */
    public void enableLoadBalancer(LoadBalancerConfig config) {
        if (loadBalancer != null) {
            System.out.println("Load Balancer already configured. Dont do it a second time. Could break!");
        }
        loadBalancer = new LoadBalancer(config);
    }

    /**
     *
     * @return
     */
    public LoadBalancer getLoadBalancer() {
        if (loadBalancer == null) {
            System.out.println("Configure Load Balancer first");
        }
        return loadBalancer;
    }

    /**
     * Enable Ecxption Lgging if something happens on the API or your Code (With
     * full Stack Trace)
     */
    public void enableExceptionHandling() {
        root.setEXCEPTION(new ErrorEndpoint());
    }

    /**
     * Enable a 404 Json Page which tells the User that the Page has not been
     * found
     */
    public void enableNotFoundHandling() {
        root.setNOTFOUND(new NotFoundEndpoint());
    }

    /**
     * Shows the complete Structure of the REST API
     *
     * @param structurePath
     * @param path
     */
    public void enableStructure(String structurePath, RestPath path) {
        if (path == null) {
            path = root;
        }
        path.addRestEndpoint(new StructureEndpoint(structurePath));
    }

    /**
     * Shows a complete List of all Urls
     *
     * @param urlListPath
     * @param path
     */
    public void enableRestUrlList(String urlListPath, RestPath path) {
        if (path == null) {
            path = root;
        }
        path.addRestEndpoint(new UrlListEndpoint(urlListPath));
    }

    /**
     *
     * @param urlListPath
     * @param path
     */
    public void enableRestDoc(String urlListPath, RestPath path) {
        if (path == null) {
            path = root;
        }
        path.addRestEndpoint(new DocumentationEndpoint(urlListPath));
    }

    /**
     * Get the Startpoint of all Rest Calls '/' Make all Sub Path on this this
     * Path.
     *
     * @param conversion
     * @return
     */
    public RootPath getRootEndpoint(Conversion conversion) {
        String host = conversion.getRequest().getHost();
        for (Map.Entry<String, RootPath> e : subdomainpath.entrySet()) {
            String sd = e.getKey() + ".";
            if (host.startsWith(sd)) {
                return e.getValue();
            }
        }
        return root;
    }

    /**
     * Get the Startpoint of all Rest Calls '/' Make all Sub Path on this this
     * Path.
     *
     * @param subdomain
     * @return
     */
    public RootPath getRootEndpoint(String subdomain) {
        for (Map.Entry<String, RootPath> e : subdomainpath.entrySet()) {
            String sd = e.getKey() + ".";
            if (subdomain.equals(sd)) {
                return e.getValue();
            }
        }
        return root;
    }

    /**
     * Get the Startpoint of all Rest Calls '/' Make all Sub Path on this this
     * Path.
     *
     * @return
     */
    public RootPath getRootEndpoint() {
        return root;
    }

    /**
     * Get the Startpoint of all Rest Calls '/' Make all Sub Path on this this
     * Path.
     *
     * @param conversion
     * @param path
     * @return
     */
    public RestPath getPath(Conversion conversion, String path) {
        return getRootEndpoint(conversion).pathForLocation(path);
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
    }

    /**
     *
     * @throws Exception
     */
    public void run() throws Exception {
        this.root.setRestHttpServer(this);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class).childHandler(
                    new RestHttpServerInitializer(this));
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
    public void shutdown() {
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

    /**
     *
     * @return
     */
    public int getPort() {
        return port;
    }

}
