package com.lcl.rpc.model;

public class RpcResponseStatus {
	
	public static final int OK = 200;
	//client error
	public static final int CLIENT_ERROR = 400;
	public static final int SERVICE_NOT_FOUND = 403;
	public static final int METHOD_NOT_FOUND = 404;
	public static final int SERVER_NOT_FOUND = 405;
	//server error
	public static final int SEVER_ERROR = 500;
	public static final int TRANSACTION_TIMEOUT = 505;

}
