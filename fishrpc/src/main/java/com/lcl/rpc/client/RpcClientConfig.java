package com.lcl.rpc.client;

public class RpcClientConfig {
	
	private String zkConnection;
	private int zkTimeout = 2000;
	
	
	public String getZkConnection() {
		return zkConnection;
	}
	public void setZkConnection(String zkConnection) {
		this.zkConnection = zkConnection;
	}
	public int getZkTimeout() {
		return zkTimeout;
	}
	public void setZkTimeout(int zkTimeout) {
		this.zkTimeout = zkTimeout;
	}
	
	

}
