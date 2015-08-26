package com.lcl.rpc.test.balance;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lcl.rpc.client.RpcClient;
import com.lcl.rpc.client.RpcClientTransactionListener;
import com.lcl.rpc.client.balance.RpcBalanceClient;
import com.lcl.rpc.counter.CounterFactory;
import com.lcl.rpc.model.RpcException;
import com.lcl.rpc.test.AddInputArgs;
import com.lcl.rpc.test.AddOutputArgs;

public class RpcClientBalanceTest {
	
	private static Random rand = new Random(System.currentTimeMillis());
	private static int times = Integer.MAX_VALUE;
	
	public static void main(String[] args){
		ExecutorService executor = Executors.newFixedThreadPool(20);
		
		final RpcBalanceClient balanceClient = new RpcBalanceClient("192.168.77.254:2181",2000,"AddServer",RpcBalanceClient.Balance_Random);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				System.out.println(CounterFactory.getInstance().getLog(String.format("rpcClient-%s-%s", "AddServer","add")));
			}
			
		}, 100, 1000);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for(int i=0;i<times;i++){
			for(int j=0;j<20;j++) {
				executor.execute(new Runnable(){
					@Override
					public void run() {
						test(balanceClient);
					}
				});
			}
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void test(RpcBalanceClient client){
		AddInputArgs input = new AddInputArgs();
		input.setX(rand.nextInt(10000));
		input.setY(rand.nextInt(20000));
		try {
			client.send("add", input, AddOutputArgs.class,new RpcClientTransactionListener<AddOutputArgs>(){
			//client.send("AddServer", "duang", null, Void.class,new RpcClientTransactionListener(){

				@Override
				public void callBack(AddOutputArgs o) {
					AddOutputArgs output = (AddOutputArgs)o;
//					
				}

				@Override
				public void error(RpcException ex) {

				}
				
			});
		} catch (RpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
