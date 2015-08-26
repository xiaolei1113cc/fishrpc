package com.rpc.client.balance;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.rpc.client.RpcClient;

public class ConsistantHashRpcBalance extends RpcBalance {
	
	private SortedMap<Long,RpcClient> map = new TreeMap<Long,RpcClient>();
	private int nCopies = 160; 


	public RpcClient chooseClient(String key) {
		byte[] digest = computeMd5(key);  
        Long k = ((long)(digest[3]&0xFF) << 24)  
                | ((long)(digest[2]&0xFF) << 16)  
                | ((long)(digest[1]&0xFF) << 8)  
                | (digest[0]&0xFF);    
        //如果找到这个节点，直接取节点，返回  
        if(!map.containsKey(k)) {  
        //得到大于当前key的那个子Map，然后从中取出第一个key，就是大于且离它最近的那个key  
            SortedMap<Long, RpcClient> tailMap=map.tailMap(k);  
            if(tailMap.isEmpty()) {  
                k=map.firstKey();  
            } else {  
                k=tailMap.firstKey();  
            }  
            //在JDK1.6中，ceilingKey方法可以返回大于且离它最近的那个key  
            //For JDK1.6 version  
//          key = ketamaNodes.ceilingKey(key);  
//          if (key == null) {  
//              key = ketamaNodes.firstKey();  
//          }  
        }  

        return map.get(k);  
	}
	
	public void setList(List<RpcClient> list) {
		this.list = list;
		SortedMap<Long,RpcClient> mapNew = new TreeMap<Long,RpcClient>();
		//对所有节点，生成nCopies个虚拟结点  
        for(RpcClient node : list) {  
            //每四个虚拟结点为一组，为什么这样？下面会说到  
            for(int i=0; i<nCopies / 4; i++) {  
                //getKeyForNode方法为这组虚拟结点得到惟一名称  
                byte[] digest=computeMd5(node.getHost() + ":" + node.getPort()+"_"+i);  
            /** Md5是一个16字节长度的数组，将16字节的数组每四个字节一组， 
                        分别对应一个虚拟结点，这就是为什么上面把虚拟结点四个划分一组的原因*/  
                for(int h=0;h<4;h++) {  
                  //对于每四个字节，组成一个long值数值，做为这个虚拟节点的在环中的惟一key  
                    Long k = ((long)(digest[3+h*4]&0xFF) << 24)  
                        | ((long)(digest[2+h*4]&0xFF) << 16)  
                        | ((long)(digest[1+h*4]&0xFF) << 8)  
                        | (digest[h*4]&0xFF);  
                      
                    mapNew.put(k, node);  
                }  
            }  
        }
        map = mapNew;
	}
	
	private byte[] computeMd5(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var6) {
            throw new IllegalStateException(var6.getMessage(), var6);
        }

        md5.reset();
        Object bytes = null;

        byte[] bytes1;
        try {
            bytes1 = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException var5) {
            throw new IllegalStateException(var5.getMessage(), var5);
        }

        md5.update(bytes1);
        return md5.digest();
    }
	
	public static void main(String[] args) throws InterruptedException {
		ConsistantHashRpcBalance balance = new ConsistantHashRpcBalance();
		List<RpcClient> list = new ArrayList<RpcClient>();
		RpcClient client1 = new RpcClient("127.0.0.1",9001);
		RpcClient client2 = new RpcClient("127.0.0.1",9002);
		RpcClient client3 = new RpcClient("127.0.0.2",9001);
		RpcClient client4 = new RpcClient("127.0.0.2",9002);
		list.add(client1);
		list.add(client2);
		list.add(client3);
		list.add(client4);
		
		balance.setList(list);
		
		Map<String,Integer> map = new HashMap<String,Integer>();
		
		
		for(int i=0;i<100000;i++){
			String user = "" + i;
			RpcClient client = balance.chooseClient(user);
			System.out.println(String.format("user:%s,hash to:%s,port:%s",user,client.getHost(),client.getPort()));	
			String key = client.getHost() + ":" + client.getPort();
			if(!map.containsKey(key))
				map.put(key, 0);
			
			Integer count = map.get(key);
			map.put(key, count+1);
		}
		
		for(String key : map.keySet())
			System.out.println("server:"+key + " hash count:" + map.get(key).intValue());
	}

}
