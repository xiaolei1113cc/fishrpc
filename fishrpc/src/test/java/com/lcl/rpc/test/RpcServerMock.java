package com.lcl.rpc.test;

import com.lcl.rpc.annotation.RpcMethod;
import com.lcl.rpc.annotation.RpcService;



@RpcService(name="AddServer")
public class RpcServerMock {
	
	@RpcMethod(name="add")
	public AddOutputArgs add(AddInputArgs input){
		AddOutputArgs output = new AddOutputArgs();
		int sum = input.getX() + input.getY();
		output.setSum(sum);
		return output;
	}
	
	@RpcMethod(name="duang")
	public void duang(){
		//System.out.println("duang ~ ~ ~");
	}

}
