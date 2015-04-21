package com.rpc.client;

import com.rpc.counter.Counter;
import com.rpc.model.RpcRequest;
import com.rpc.model.RpcResponse;

/**
 * RpcClientTransaction
 * @author lichunlei
 *
 */
public class RpcClientTransaction {
	
	private RpcRequest request;

	private RpcResponse response;
	
	private String seq;
	
	private RpcClientTransactionListener listener;
	
	private long startTime;
	
	private long expiredTime;
	
	private Class outputArgsClass;
	
	private Counter counter;
	
	public RpcClientTransaction(RpcRequest request,Class outClass,int timeout){
		this.request = request;
		this.seq = request.getSeq();
		this.outputArgsClass = outClass;
		startTime = System.currentTimeMillis();
		this.expiredTime = startTime + timeout;
		startTime = System.currentTimeMillis();
	}

	public RpcRequest getRequest() {
		return request;
	}

	public void setRequest(RpcRequest request) {
		this.request = request;
	}

	public RpcResponse getResponse() {
		return response;
	}

	public void setResponse(RpcResponse response) {
		this.response = response;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public RpcClientTransactionListener getListener() {
		return listener;
	}

	public void setListener(RpcClientTransactionListener listener) {
		this.listener = listener;
	}
	
	public Class getOutputArgsClass() {
		return outputArgsClass;
	}

	public void setOutputArgsClass(Class outputArgsClass) {
		this.outputArgsClass = outputArgsClass;
	}

	public boolean isTimeout(){
		return System.currentTimeMillis() > expiredTime;
	}

	public Counter getCounter() {
		return counter;
	}

	public void setCounter(Counter counter) {
		this.counter = counter;
	}
	
	

}
