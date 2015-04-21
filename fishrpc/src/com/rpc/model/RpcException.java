package com.rpc.model;

/**
 * RpcException
 * @author lichunlei
 *
 */
public class RpcException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4872585015838721228L;
	
	
	private int code;
	private String error;
	
	public RpcException(int code,String error){
		this.code = code;
		this.error = error;
	}
	
	@Override
	public String toString() {
		return "RpcException [code=" + code + ", error=" + error + "]";
	}

}
