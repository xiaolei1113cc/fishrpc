package com.rpc.test;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rpc.client.RpcClient;
import com.rpc.client.RpcClientTransactionListener;
import com.rpc.counter.CounterFactory;
import com.rpc.model.RpcException;

public class RpcClientTest {
	
	public static Random rand = new Random(System.currentTimeMillis());
	
	private static int count = 0;
	private static long costSum = 0;
	
	private static long allStart;
	
	private static int times = 1000000;
	
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
			
		}, 1000, 1000);
		
		allStart = System.currentTimeMillis();
		for(int i=0;i<times;i++){
			executor.execute(new Runnable(){
				@Override
				public void run() {
					test(client);
				}
			});
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		Thread.sleep(10000);
//		
//		System.out.println("count:" + count + " avrage cost(ms): " + costSum/count);
		
		
		
	}
	
	public static void test(RpcClient client){
		AddInputArgs input = new AddInputArgs();
		input.setX(rand.nextInt(10000));
		input.setY(rand.nextInt(20000));
		final long start = System.currentTimeMillis();
		try {
			client.send("AddServer", "add", input, AddOutputArgs.class,new RpcClientTransactionListener(){
			//client.send("AddServer", "duang", null, Void.class,new RpcClientTransactionListener(){

				@Override
				public void callBack(Object o) {
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
