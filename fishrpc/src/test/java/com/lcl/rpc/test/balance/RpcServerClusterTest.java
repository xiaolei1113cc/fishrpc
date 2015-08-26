package com.lcl.rpc.test.balance;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.lcl.rpc.counter.CounterFactory;
import com.lcl.rpc.server.RpcServerBootstrap;
import com.lcl.rpc.server.RpcServerConfig;
import com.lcl.rpc.server.RpcServerFactory;
import com.lcl.rpc.test.RpcServerMock;

public class RpcServerClusterTest {
	
	public static Timer timer;
	private String host;
	private int port;
	private String zkConnection;
	
	public void start() {
		
		final RpcServerConfig config = new RpcServerConfig();
		config.setIp(host);
		config.setPort(port);
		config.setZkConnection(zkConnection);
		config.setZkTimeout(2000);
		
		RpcServerBootstrap server = new RpcServerBootstrap(config.getIp(),config.getPort());
		server.start();
		
		RpcServerFactory.getInstance().register(config,new RpcServerMock());
		
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				
				System.out.println(config.getIp() + ":" + config.getPort() + "*****************");
				System.out.println(CounterFactory.getInstance().getLog("RPC"));
				System.out.println(CounterFactory.getInstance().getLog("RPC-AddServer-add"));
				System.out.println(CounterFactory.getInstance().getLog("RPC-AddServer-duang"));
			}
			
		}, 1000,10000);
		
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
	
	public String getZkConnection() {
		return zkConnection;
	}

	public void setZkConnection(String zkConnection) {
		this.zkConnection = zkConnection;
	}

	public static void main(String[] args){
		//for(int i=0;i<10;i++){
			RpcServerClusterTest server = new RpcServerClusterTest();
			server.setHost("127.0.0.1");
			server.setPort(9003);
			server.setZkConnection("192.168.77.254:2181");
			server.start();
		//}
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
