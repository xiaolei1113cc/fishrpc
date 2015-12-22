package com.lcl.rpc.test;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lcl.rpc.client.RpcClient;
import com.lcl.rpc.client.RpcClientTransactionListener;
import com.lcl.rpc.counter.CounterFactory;
import com.lcl.rpc.model.RpcException;

public class RpcClientTest {
	
	public static Random rand = new Random(System.currentTimeMillis());
	
	private static int count = 0;
	private static long costSum = 0;
	
	private static long allStart;
	
	private static int times = Integer.MAX_VALUE;
	
	public static void main(String[] args) throws InterruptedException{
		final RpcClient client = new RpcClient("127.0.0.1",9001);
		//final RpcClient client = new RpcClient("10.0.8.78",9001);
		ExecutorService executor = Executors.newFixedThreadPool(20);
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				System.out.println(CounterFactory.getInstance().getLog(String.format("rpcClient-%s-%s", "AddServer","add")));
			}
			
		}, 100, 1000);
		
		allStart = System.currentTimeMillis();
		for(int i=0;i<times;i++){
			for(int j=0;j<20;j++) {
				executor.execute(new Runnable(){
					@Override
					public void run() {
						test(client);
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
//		test(client);
		
		Thread.sleep(100000);
//		
//		System.out.println("count:" + count + " avrage cost(ms): " + costSum/count);
		
		
		
	}
	
	public static void test(RpcClient client){
		AddInputArgs input = new AddInputArgs();
		input.setX(rand.nextInt(10000));
		input.setY(rand.nextInt(20000));
		try {
			client.send("AddServer", "add", input, AddOutputArgs.class,new RpcClientTransactionListener<AddOutputArgs>(){
			//client.send("AddServer", "duang", null, Void.class,new RpcClientTransactionListener(){

				@Override
				public void callBack(AddOutputArgs o) {
					//AddOutputArgs output = (AddOutputArgs)o;
//					long end = System.currentTimeMillis();
//					long cost = end - start;
//					count++;
//					costSum = costSum + cost;
//					//System.out.println("result: " + output.getSum());
//					if(count == times){
//						long allCost = System.currentTimeMillis() - allStart;
//						System.out.println(times + " cost millionsecond : " + allCost);
//						System.out.println("count:" + count + " avrage cost(ms): " + costSum/count);
//					}
				}

				@Override
				public void error(RpcException ex) {
//					long end = System.currentTimeMillis();
//					long cost = end - start;
//					count++;
//					costSum = costSum + cost;
//					//System.out.println("result: " + output.getSum());
//					if(count == times){
//						long allCost = System.currentTimeMillis() - allStart;
//						System.out.println(times + " cost millionsecond : " + allCost);
//						System.out.println("count:" + count + " avrage cost(ms): " + costSum/count);
//					}
				}
				
			});
		} catch (RpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
