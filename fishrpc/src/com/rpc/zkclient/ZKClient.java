package com.rpc.zkclient;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKClient {
	
	private static Logger logger = LoggerFactory.getLogger(ZKClient.class);
	public static final String RpcServerRoot = "/rpcserver";
	
	
	private String connStr;
	private int timeout = 2000;

	public ZKClient(String connStr,int timeout) {
		this.connStr = connStr;
		this.timeout = timeout;
		
		
	}

}
