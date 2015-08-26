package com.lcl.rpc.counter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 计数器收集器
 * @author lichunlei
 *
 */
public class CounterCollector {
	
	private String countName;
	
	private AtomicInteger countPerSecond = new AtomicInteger(0);
	private AtomicInteger countPerMinute = new AtomicInteger(0);
	private AtomicLong count = new AtomicLong(0);
	private AtomicLong failCount = new AtomicLong(0);
	//耗时累计值，单位毫秒
	private AtomicLong avgCost = new AtomicLong(0);
	//最小耗时，单位毫秒
	private int minCost;
	//最大耗时
	private int maxCost;
	//上一秒次数
	private int cPerSecond;
	//上一分钟次数
	private boolean isFirstMinute;
	private int cPerMinute;
	//上一天的访问次数
	private boolean isFirstDay;
	private long cPerDay;
	
	private Timer timer;

	public CounterCollector(String name){
		this.countName = name;
		isFirstDay = true;
		isFirstMinute = true;
		timer = new Timer();
		//1秒钟1次
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				cPerSecond = countPerSecond.intValue();
				countPerSecond.set(0);
			}
			
		}, 1000, 1000);
		//1分钟1次
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				cPerMinute = countPerMinute.intValue();
				countPerMinute.set(0);
				isFirstMinute = false;
			}
			
		}, 60000, 60000);
		//1天1次重新计算
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				cPerDay = count.longValue();
				count = new AtomicLong(0);
				failCount = new AtomicLong(0);
				avgCost = new AtomicLong(0);
				isFirstDay = false;
			}
			
		}, 24*60*60*1000,24*60*60*1000);
		
	}
	
	public void increment(){
		countPerSecond.incrementAndGet();
		countPerMinute.incrementAndGet();
		count.incrementAndGet();
	}
	
	public void fail(){
		failCount.incrementAndGet();
	}
	
	public int getCountPerSecond(){
		return cPerSecond;
	}

	public int getCountPerMinute(){
		if(isFirstMinute)
			return (int)count.longValue();
		return cPerMinute;
	}
	
	public long getCountPerDay(){
		if(isFirstDay)
			return count.longValue();
		return cPerDay;
	}
	
	public long getCount(){
		return count.longValue();
	}
	
	public long getFailCount(){
		return failCount.longValue();
	}
	
	public String getName(){
		return countName;
	}

	public int getMinCost() {
		return minCost;
	}

	public int getMaxCost() {
		return maxCost;
	}
	
	public float getAvgCost(){
		return (float)avgCost.longValue()/count.longValue();
	}
	
	public void addAvgCost(int cost){
		avgCost.addAndGet(cost);
		if(cost < minCost)
			minCost = cost;
		if(cost > maxCost)
			maxCost = cost;
	}
}
