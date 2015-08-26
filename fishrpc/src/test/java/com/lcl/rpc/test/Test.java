package com.lcl.rpc.test;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

	
	public static void main(String[] args){
		String[] str = new String[100];
		//初始化这一段不用写
		for(int i=0;i<100;i++){
			str[i] = "str" + i%10;
		}
		
		Hashtable<String,AtomicInteger> sTable = count(str);
		printTable(sTable);
		
		
	}
	//计算
	public static Hashtable<String,AtomicInteger> count(String[] str){
		Hashtable<String,AtomicInteger> sTable = new Hashtable<String,AtomicInteger>();
		for(int i=0;i<str.length;i++){
			String s = str[i];
			if(sTable.containsKey(s)){
				AtomicInteger c = sTable.get(s);
				c.incrementAndGet();
			}
			else {
				AtomicInteger c = new AtomicInteger(1);
				sTable.put(s, c);
			}
		}
		return sTable;
	}
	//输出
	public static void printTable(Hashtable<String,AtomicInteger> sTable){
		for(String s : sTable.keySet())
			System.out.println(String.format("String:%s,count:%d", s,sTable.get(s).intValue()));
	}
}
