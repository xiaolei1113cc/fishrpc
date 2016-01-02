package com.lcl.rpc.client;

import com.lcl.rpc.counter.Counter;
import com.lcl.rpc.model.RpcMessageProto;


/**
 * RpcClientTransaction
 * @author lichunlei
 *
 */
public class RpcClientTransaction {
	
	private RpcMessageProto.RpcRequest request;

	private RpcMessageProto.RpcResponse response;
	
	private String seq;
	
	private RpcClientTransactionListener listener;
	
	private long startTime;
	
	private long expiredTime;
	
	private Class outputArgsClass;
	
	private Counter counter;
	
	public RpcClientTransaction(RpcMessageProto.RpcRequest request,Class outClass,int timeout){
		this.request = request;
		this.seq = request.getSeq();
		this.outputArgsClass = outClass;
		startTime = System.currentTimeMillis();
		this.expiredTime = startTime + timeout;
		startTime = System.currentTimeMillis();
	}

	public RpcMessageProto.RpcRequest getRequest() {
		return request;
	}

	public void setRequest(RpcMessageProto.RpcRequest request) {
		this.request = request;
	}

	public RpcMessageProto.RpcResponse getResponse() {
		return response;
	}

	public void setResponse(RpcMessageProto.RpcResponse response) {
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
