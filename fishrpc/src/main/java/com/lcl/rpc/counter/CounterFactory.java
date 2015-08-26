package com.lcl.rpc.counter;

import java.util.Hashtable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * CounterFactory
 * @author lichunlei
 *
 */
public class CounterFactory {
	
	private Hashtable<String,CounterCollector> counterCollectors = new Hashtable<String,CounterCollector>();
	private static CounterFactory instance = new CounterFactory();
	
	public static CounterFactory getInstance(){
		return instance;
	}
	
	public Counter getConter(String name){
		if(!counterCollectors.containsKey(name)){
			synchronized(instance){
				if(!counterCollectors.containsKey(name)){
					CounterCollector  col = new CounterCollector(name);
					counterCollectors.put(name, col);
				}
			}
		}

		CounterCollector c = counterCollectors.get(name);
		Counter counter = new CounterImpl(c);
		
		return counter;
	}
	
	public long getAllCount(String name){
		CounterCollector c = counterCollectors.get(name);
		if(c == null)
			return 0;
		return c.getCount();
	}
	
	public long getAllFailCount(String name){
		CounterCollector c = counterCollectors.get(name);
		if(c == null)
			return 0;
		return c.getFailCount();
	}
	/**
	 * 平均耗时，单位毫秒
	 * @param name
	 * @return
	 */
	public float getAvgCost(String name){
		CounterCollector c = counterCollectors.get(name);
		if(c == null)
			return 0;
		return c.getAvgCost();
	}
	/**
	 * 最大耗时，单位毫秒
	 * @param name
	 * @return
	 */
	public int getMaxCost(String name){
		CounterCollector c = counterCollectors.get(name);
		if(c == null)
			return 0;
		return c.getMaxCost();
	}
	/**
	 * 最小耗时，单位毫秒
	 * @param name
	 * @return
	 */
	public int getMinCost(String name){
		CounterCollector c = counterCollectors.get(name);
		if(c == null)
			return 0;
		return c.getMinCost();
	}
	/**
	 * 前一秒钟执行次数
	 * @param name
	 * @return
	 */
	public int getCountPerSecond(String name){
		CounterCollector c = counterCollectors.get(name);
		if(c == null)
			return 0;
		return c.getCountPerSecond();
	}
	/**
	 * 前一分钟执行次数
	 * @param name
	 * @return
	 */
	public int getCountPerMinute(String name){
		CounterCollector c = counterCollectors.get(name);
		if(c == null)
			return 0;
		return c.getCountPerMinute();
	}
	
	public long getCountPerDay(String name){
		CounterCollector c = counterCollectors.get(name);
		if(c == null)
			return 0;
		return c.getCountPerDay();
	}
	

	public String getLog(String name){
		return String.format("name:%s: allCount:%s,failCount:%s,countPerSecond:%s,countPerMinute:%s,countPerDay:%s,avgCost:%s,maxCost:%s,minCost:%s", 
				name,
				CounterFactory.getInstance().getAllCount(name),
				CounterFactory.getInstance().getAllFailCount(name),
				CounterFactory.getInstance().getCountPerSecond(name),
				CounterFactory.getInstance().getCountPerMinute(name),
				CounterFactory.getInstance().getCountPerDay(name),
				CounterFactory.getInstance().getAvgCost(name),
				CounterFactory.getInstance().getMaxCost(name),
				CounterFactory.getInstance().getMinCost(name));
	}
	
	
	public static void main(String[] args){
		Executor executor = Executors.newFixedThreadPool(50);
		final String name = "test";
		
		int size = 1000000;
		for(int i=0;i<size;i++){
			final int ti = i;
			executor.execute(new Runnable(){
				@Override
				public void run() {
					Counter c = CounterFactory.getInstance().getConter(name);
					c.begin();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					c.end();
					if( ti%1000 ==999 ){
						System.out.println("all count:"+CounterFactory.getInstance().getAllCount(name));
						System.out.println("fail count:"+CounterFactory.getInstance().getAllFailCount(name));
						System.out.println("count per second:"+CounterFactory.getInstance().getCountPerSecond(name));
						System.out.println("count per minute:"+CounterFactory.getInstance().getCountPerMinute(name));
						System.out.println("avgCost:" + CounterFactory.getInstance().getAvgCost(name));
						System.out.println("maxCost:" + CounterFactory.getInstance().getMaxCost(name));
						System.out.println("minCost:" + CounterFactory.getInstance().getMinCost(name));
					}
				}
			});
		}

		
		
	}

}
