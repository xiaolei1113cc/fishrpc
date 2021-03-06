package com.lcl.rpc.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;


public class RpcServerBootstrap {
	
	private int port;
	private String host;
	
	private ServerBootstrap bootstrap;
	
	public RpcServerBootstrap(String host,int port){
		this.host = host;
		this.port = port;
	}

	public void start(){
		// Server服务启动器  
		NioServerSocketChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors.newFixedThreadPool(16),Executors.newCachedThreadPool());
        bootstrap = new ServerBootstrap(channelFactory);  
        // 设置一个处理客户端消息和各种消息事件的类(Handler)  
        
        RpcServerPipelineFactory pipelineFactory = new RpcServerPipelineFactory();
        
        bootstrap.setPipelineFactory(pipelineFactory);  
        // 开放port端口供客户端访问。  
        bootstrap.bind(new InetSocketAddress(host,port));  
        
        bootstrap.setOption("child.tcpNoDelay", true);  
        bootstrap.setOption("child.keepAlive", true);
	}
	
	
	public void stop(){
		bootstrap.releaseExternalResources();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	
	
	
	
}
