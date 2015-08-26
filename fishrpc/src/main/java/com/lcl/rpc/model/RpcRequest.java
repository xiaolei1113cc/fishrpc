package com.lcl.rpc.model;

import java.util.UUID;

import com.alibaba.fastjson.JSONObject;

/**
 * RpcRequest
 * @author lichunlei
 *
 */
public class RpcRequest {
	
	private int version = 1;
	
	private String seq;
	
	private String service;
	
	private String method;

	private String body;
	
	public RpcRequest(){
		
	}
	
	public RpcRequest(String service,String method,String body){
		this.seq = UUID.randomUUID().toString();
		this.service = service;
		this.method = method;
		this.body = body;
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

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String toJsonString(){
//		JSONObject json = new JSONObject();
//		json.put("version", version);
//		json.put("seq", seq);
//		json.put("service", service);
//		json.put("method", method);
//		json.put("body", body);
		String str = JSONObject.toJSONString(this);
		return str;
	}
	
	public static RpcRequest fromJsonString(String str){
		RpcRequest request = JSONObject.parseObject(str, RpcRequest.class);
		return request;
	}
	
	public static void main(String[] args) {
		RpcRequest request = new RpcRequest("service","method","{\"x\":1,\"y\":1}");
		
		String str = request.toJsonString();
		
		RpcRequest request2 = RpcRequest.fromJsonString(str);
		System.out.println(request2.toJsonString());
	}
	

}
