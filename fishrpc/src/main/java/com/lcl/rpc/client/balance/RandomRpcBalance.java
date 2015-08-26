package com.lcl.rpc.client.balance;

import java.util.List;
import java.util.Random;

import com.lcl.rpc.client.RpcClient;

public class RandomRpcBalance extends RpcBalance {
	
	private Random random = new Random(System.currentTimeMillis());

	@Override
	public RpcClient chooseClient(String key) {
		if(this.list == null || this.list.size() == 0)
			return null;
		
		int r = random.nextInt(list.size());
		
		return list.get(r);
		
	}

}
