package com.rpc.server;

public class RpcServerConfig {
	private String ip;
	private int port;
	private String zkConnection;
	private int zkTimeout = 2000;
	
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
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
