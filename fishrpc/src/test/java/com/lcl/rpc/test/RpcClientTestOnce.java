package com.lcl.rpc.test;

import java.util.Random;

import com.lcl.rpc.client.RpcClient;
import com.lcl.rpc.client.RpcClientTransactionListener;
import com.lcl.rpc.model.RpcException;

public class RpcClientTestOnce {

	public static void main(String[] args){
		 try {
			RpcClient client = new RpcClient("127.0.0.1",9001);
			testAdd(client);
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
		
		client.send("AddServer", "add", input, AddOutputArgs.class,new RpcClientTransactionListener<AddOutputArgs>(){

			@Override
			public void callBack(AddOutputArgs o) {
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
		client.send("AddServer", "duang", null, Void.class, new RpcClientTransactionListener<Void>(){

			@Override
			public void callBack(Void o) {
				System.out.println("duang ~ ~ ~ ");
				
			}

			@Override
			public void error(RpcException ex) {
				System.out.println(ex.toString());
			}
			
		});
	}
	
}
