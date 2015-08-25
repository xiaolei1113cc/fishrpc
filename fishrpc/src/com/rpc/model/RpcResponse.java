package com.rpc.model;

import com.alibaba.fastjson.JSONObject;

/**
 * RpcResponse
 * @author lichunlei
 *
 */
public class RpcResponse {
	
	public static final int OK = 200;
	//client error
	public static final int CLIENT_ERROR = 400;
	public static final int SERVICE_NOT_FOUND = 403;
	public static final int METHOD_NOT_FOUND = 404;
	public static final int SERVER_NOT_FOUND = 405;
	//server error
	public static final int SEVER_ERROR = 500;
	public static final int TRANSACTION_TIMEOUT = 505;
	
	private int version;
	
	private String seq;
	
	private int status;
	
	private String body;
	
	public RpcResponse() {
		
	}
	
	public RpcResponse(String seq,int status,String body){
		this.seq = seq;
		this.status = status;
		this.body = body;
	}
	
	
	public RpcResponse(String seq,int status){
		this.seq = seq;
		this.status = status;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String toJsonString(){
		String str = JSONObject.toJSONString(this);
		return str;
	}
	
	public static RpcResponse fromJsonString(String str){
		RpcResponse response = (RpcResponse)JSONObject.parseObject(str,RpcResponse.class);
		return response;
	}
	
	
}
