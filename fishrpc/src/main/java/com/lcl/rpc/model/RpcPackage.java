package com.lcl.rpc.model;

public class RpcPackage {
	
	private byte version = 1;
	private RpcMessageType msgType;
	
	private String pack;

	public RpcPackage(){
		
	}
	public RpcPackage(byte version,RpcMessageType msgType,String pack){
		this.version = version;
		this.msgType = msgType;
		this.pack = pack;
	}
	
	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public RpcMessageType getMsgType() {
		return msgType;
	}

	public void setMsgType(RpcMessageType msgType) {
		this.msgType = msgType;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}
	
	

}
