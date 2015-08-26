package com.lcl.rpc.counter;

/**
 * end()要求写在finally里面。示例如下：
 * try{
 * 		count.begin();
 * }catch(Exception ex){
 * 		count.fail();
 * }finally{
 * 		count.end();
 * }
 * 
 * @author lichunlei
 *
 */
public class CounterImpl implements Counter {

	private long start;
	private long cost;
	private boolean isFail = false;
	private CounterCollector collector;
	
	
	public CounterImpl(CounterCollector collector){
		this.collector = collector;
	}
	
	@Override
	public void begin() {
		start = System.currentTimeMillis();
		collector.increment();
		
	}

	@Override
	public void end() {
		cost = System.currentTimeMillis() - start;
		collector.addAvgCost((int)cost);
	}

	@Override
	public void fail() {
		isFail = true;
		collector.fail();
	}

}
