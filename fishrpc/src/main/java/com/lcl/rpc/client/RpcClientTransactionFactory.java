package com.lcl.rpc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lcl.rpc.model.RpcException;
import com.lcl.rpc.model.RpcResponse;

/**
 * RpcClientTransactionFactory
 * @author lichunlei
 *
 */
public class RpcClientTransactionFactory {
	
	private static Logger logger = LoggerFactory.getLogger(RpcClientTransactionFactory.class);
	private static RpcClientTransactionFactory instance = new RpcClientTransactionFactory();

	private ConcurrentHashMap<String,RpcClientTransaction> map = null;
	private Timer timer;
	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private RpcClientTransactionFactory(){
		map = new ConcurrentHashMap<String,RpcClientTransaction>();
		timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				try {
					final List<RpcClientTransaction> timeoutTrans = new ArrayList<RpcClientTransaction>();	
					
					//for(String key : map.keySet()){
					while(map.keys().hasMoreElements()) {
						String key = map.keys().nextElement();
						RpcClientTransaction clientTrans = map.get(key);
						if(clientTrans.isTimeout())
						{
							timeoutTrans.add(clientTrans);
							//map.remove(key);
						}
					}
					executor.execute(new Runnable(){
						@Override
						public void run() {
							try{
								for(RpcClientTransaction trans : timeoutTrans){
									map.remove(trans.getSeq());
									RpcException ex = new RpcException(RpcResponse.TRANSACTION_TIMEOUT,"transaction timeout:"+trans.getSeq());
									trans.getListener().error(ex);
								}
							}catch(Exception ex){
								logger.error("RpcClientTransaction timeout callback error:",ex);
							}
						}
					});
				}catch(Exception ex) {
					logger.error("timeout schedule error:",ex);
				}
				
			}
			
		}, 1000, 500);
	}
	
	public static RpcClientTransactionFactory getInstance(){
		return instance;
	}

	public void addClientTransaction(RpcClientTransaction trans){
		map.put(trans.getSeq(), trans);
	}
	
	public void deleteClientTransaction(String seq) {
		map.remove(seq);
	}
	
	public RpcClientTransaction getClientTransaction(String seq) {
		return map.get(seq);
	}
	
}
