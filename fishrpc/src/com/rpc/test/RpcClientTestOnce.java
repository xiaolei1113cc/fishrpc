package com.rpc.test;

import java.util.Random;

import com.rpc.client.RpcClient;
import com.rpc.client.RpcClientTransactionListener;
import com.rpc.model.RpcException;

public class RpcClientTestOnce {

	public static void main(String[] args){
		 try {
			RpcClient client = new RpcClient("127.0.0.1",9001);
			testDuang(client);
		} catch (RpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testAdd(RpcClient client) throws RpcException{
		Random rand = new Random(System.currentTimeMillis());
		
		AddInputArgs input = new AddInputArgs();
		input.setX(rand.nextInt(10000));
		input.setY(rand.nextInt(20000));
		
		client.send("AddServer", "add", input, AddOutputArgs.class,new RpcClientTransactionListener(){

			@Override
			public void callBack(Object o) {
				AddOutputArgs output = (AddOutputArgs)o;
				System.out.println("sum:" + output.getSum());
				
			}

			@Override
			public void error(RpcException ex) {
				System.out.println(ex.toString());
			}
			
		});
		
	}
	
	public static void testDuang(RpcClient client) throws RpcException{
		client.send("AddServer", "duang", null, Void.class, new RpcClientTransactionListener(){

			@Override
			public void callBack(Object o) {
				System.out.println("duang ~ ~ ~ ");
				
			}

			@Override
			public void error(RpcException ex) {
				System.out.println(ex.toString());
			}
			
		});
	}
	
}