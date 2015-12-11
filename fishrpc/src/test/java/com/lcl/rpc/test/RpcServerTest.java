package com.lcl.rpc.test;

import java.util.Timer;
import java.util.TimerTask;

import com.lcl.rpc.counter.CounterFactory;
import com.lcl.rpc.server.RpcServerBootstrap;
import com.lcl.rpc.server.RpcServerConfig;
import com.lcl.rpc.server.RpcServerFactory;

public class RpcServerTest {
	
	public static Timer timer;
	
	public static void main(String[] args) {
		
		RpcServerConfig config = new RpcServerConfig();
		config.setIp("127.0.0.1");
		config.setPort(9001);
//		config.setZkConnection("192.168.77.254:2181");
//		config.setZkTimeout(2000);
		
		RpcServerBootstrap server = new RpcServerBootstrap(config.getIp(),config.getPort());
		server.start();
		
		RpcServerFactory.getInstance().register(config,new RpcServerMock());
		
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				System.out.println("***********************************************************");
				System.out.println(CounterFactory.getInstance().getCounterLog());
				System.out.println(CounterFactory.getInstance().getCounterLog("AddServer","add"));
				System.out.println(CounterFactory.getInstance().getCounterLog("AddServer","duang"));
			}
			
		}, 1000,10000);
		
	}

}
