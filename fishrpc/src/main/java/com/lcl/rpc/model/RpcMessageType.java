package com.lcl.rpc.model;

public enum RpcMessageType {

	Unknown(0,"Unknown"),
	KeepAlive(1,"keepAlive"),
	Register(2,"Register"),
	RpcRequest(3,"RpcRequest"),
	RpcResponse(4,"RpcResponse");
	
	private int value;
	private String msgType;
	
	RpcMessageType(int value,String msgType){
		this.value = value;
		this.msgType = msgType;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	public static RpcMessageType parse(int value) {
		switch(value){
			case 1:
				return KeepAlive;
			case 2:
				return Register;
			case 3:
				return RpcRequest;
			case 4:
				return RpcResponse;
			default:
				return Unknown;
				
		}
	}
	
	
}
