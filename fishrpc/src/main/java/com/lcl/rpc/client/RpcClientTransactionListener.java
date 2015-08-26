package com.lcl.rpc.client;

import com.lcl.rpc.model.RpcException;

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
