package com.rpc.test;

import java.util.Timer;
import java.util.TimerTask;

import com.rpc.counter.CounterFactory;
import com.rpc.server.RpcServerBootstrap;
import com.rpc.server.RpcServerFactory;

public class RpcServerTest {
	
	public static Timer timer;
	
	public static void main(String[] args) {
		
		RpcServerFactory.getInstance().register(new RpcServerMock());
		
		RpcServerBootstrap server = new RpcServerBootstrap(9001);
		server.start();
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				System.out.println("***********************************************************");
				System.out.println(CounterFactory.getInstance().getLog("RPC"));
				System.out.println(CounterFactory.getInstance().getLog("RPC-AddServer-add"));
				System.out.println(CounterFactory.getInstance().getLog("RPC-AddServer-duang"));
			}
			
		}, 1000,10000);
		
	}

}
