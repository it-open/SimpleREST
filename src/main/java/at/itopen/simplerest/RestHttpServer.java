/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.SystemPropertyUtil;

/**
 *
 * @author roland
 */
public class RestHttpServer {
    
   

	private final int port;
	private EventLoopGroup boss = new NioEventLoopGroup();
	private EventLoopGroup worker = new NioEventLoopGroup(SystemPropertyUtil.getInt("events.workerThreads", 300),
		new DefaultThreadFactory("nio-worker", Thread.MAX_PRIORITY));

	public RestHttpServer() {
		port = SystemPropertyUtil.getInt("default.port", 18080);
	}

	public RestHttpServer(int port) {
		this.port = port;
	}

	public void init() {
	}

	public void run() throws Exception {
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(boss, worker).channel(NioServerSocketChannel.class).childHandler(
				new RestHttpServerInitializer());
			setChannelOptions(bootstrap);

			Channel ch = bootstrap.bind(port).sync().channel();

			System.out.println(">> startUp server ["+ ch.localAddress().toString()+"]");

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
