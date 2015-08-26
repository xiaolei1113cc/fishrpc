package com.lcl.rpc.counter;

/**
 * 计数器
 * @author lichunlei
 *
 */
public interface Counter {

	/*
	 * 开始
	 */
	public void begin();
	
	/*
	 * 正常结束
	 */
	public void end();
	
	/*
	 * 执行失败
	 */
	public void fail();

}
