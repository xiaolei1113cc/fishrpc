package com.rpc.client;

import com.rpc.model.RpcException;

/**
 * RpcClientTransactionListener
 * @author lichunlei
 *
 */
public interface RpcClientTransactionListener<T> {
	
	/**
	 * 正常返回
	 * @param o
	 */
	public void callBack(T o);
	
	/**
	 * 异常返回
	 * @param ex
	 */
	public void error(RpcException ex);

}
