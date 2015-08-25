package com.rpc.client.balance;

import java.util.List;

import com.rpc.client.RpcClient;

public abstract class RpcBalance {
	
	protected List<RpcClient> list;
	
	public abstract RpcClient chooseClient() ;

	public List<RpcClient> getList() {
		return list;
	}

	public void setList(List<RpcClient> list) {
		this.list = list;
	}
	
	

}
