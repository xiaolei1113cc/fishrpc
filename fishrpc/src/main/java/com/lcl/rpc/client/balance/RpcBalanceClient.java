package com.lcl.rpc.client.balance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lcl.rpc.client.RpcClient;
import com.lcl.rpc.client.RpcClientTransaction;
import com.lcl.rpc.client.RpcClientTransactionFactory;
import com.lcl.rpc.client.RpcClientTransactionListener;
import com.lcl.rpc.counter.Counter;
import com.lcl.rpc.counter.CounterFactory;
import com.lcl.rpc.model.RpcException;
import com.lcl.rpc.model.RpcResponseStatus;
import com.lcl.rpc.zkclient.RpcRegister;
import com.lcl.rpc.zkclient.RpcRegister.RpcServerNode;

/**
 * 负载均衡客户端，需要zookeeper
 * @author lichunlei
 *
 */
public class RpcBalanceClient implements IZkChildListener{
	
	private static final Logger logger = LoggerFactory.getLogger(RpcBalanceClient.class);
	
	//随机负载均衡策略
	public static final String Balance_Random = "random";
	//一致性hash
	public static final String Balance_ConsistanceHash = "consistancehash";
	
	private String zkConnection;
	private int zkTimeout;
	private String balance;
	private String service;
	private RpcRegister register;
	RpcBalance rpcBalance = null;

	private List<RpcClient> localList = new ArrayList<RpcClient>();
	
	/**
	 * 构造函数
	 * @param zkConnection
	 * @param zkTimeout
	 * @param balance
	 */
	public RpcBalanceClient(String zkConnection,int zkTimeout,String service,String balance){
		logger.info(String.format("RpcBalanceClient zkConnection:%s,zkTimeout:%s,service:%s,balance:%s", zkConnection,zkTimeout,service,balance));
		
		this.zkConnection = zkConnection;
		this.zkTimeout = zkTimeout;
		this.balance = balance;
		if(Balance_Random.equals(this.balance))
			rpcBalance = new RandomRpcBalance();
		else
			rpcBalance = new ConsistantHashRpcBalance();
		this.service = service;
		register = new RpcRegister(zkConnection,zkTimeout);
		register.listen(service,this);
		//初始的时候拿一下数据
		String path = register.RpcServerRoot + "/" + service;
		try {
			this.handleChildChange(path, register.getChildren(path));
		} catch (Exception e) {
			logger.error("handleChildChange error: ",e);
		}
	}
	
	/**
	 * 构造函数，默认随即负载
	 * @param zkConnection
	 * @param zkTimeout
	 */
	public RpcBalanceClient(String zkConnection,int zkTimeout,String service){
		this(zkConnection, zkTimeout, service,Balance_Random);
	}

	@Override
	public void handleChildChange(String service, List<String> currentChilds)
			throws Exception {
		String parentPath = service;
		logger.info("handleChildChange parentPath:" + parentPath);

		List<String> servers = register.getChildren(parentPath);
		List<RpcClient> remoteList = new ArrayList<RpcClient>();
		if(servers != null) {
			for(String server : servers){
				String serverPath = parentPath + "/" + server;
				RpcServerNode node = register.getDate(serverPath);
				
				RpcClient client = new RpcClient(node.getIp(),node.getPort());
				remoteList.add(client);				
			}
		}

		
		logger.info("remote zk:" + JSONObject.toJSONString(remoteList));
		
		//对比ServiceMap和map的区别，新服务加入，或者是没有节点的服务删掉
		logger.info("compare service:"+service);
		boolean changed = false;
		//新增的服务
		if(localList.size() == 0){
			if(remoteList.size() > 0){
				for(RpcClient client : remoteList) {
					client.connect();
					localList.add(client);
					changed = true;
				}
			}
		}
		//所有服务都停了
		else if(remoteList.size() == 0) {
			for(RpcClient client : localList)
				client.close();
			localList.clear();
			changed = true;
		}
		else {
			//zk有，本地没有，加上
			for(RpcClient client : remoteList)
				if(!localList.contains(client)){
					client.connect();
					localList.add(client);
					changed = true;
				}
			//本地有，远程没有，删掉
			for(RpcClient client : localList)
				if(!remoteList.contains(client)){
					localList.remove(client);
					client.close();
					changed = true;
				}
		}
			
		if(changed){
//			if(Balance_Random.equals(this.balance))
//				rpcBalance = new RandomRpcBalance();
//			else
//				rpcBalance = new ConsistantHashRpcBalance();
				
			rpcBalance.setList(localList);				
		}
			

		
		logger.info("local List:" + JSONObject.toJSONString(localList));
	}

	public <T> void send(String method,Object input,Class<T> outClass,RpcClientTransactionListener<T> listener) throws RpcException {

		if(rpcBalance == null)
			throw new RpcException(RpcResponseStatus.SERVER_NOT_FOUND,"balance is null");
		String key = String.valueOf(input.hashCode());
		RpcClient client = rpcBalance.chooseClient(key);
		if(client == null)
			throw new RpcException(RpcResponseStatus.SERVER_NOT_FOUND,"no server running");
		logger.info(String.format("RpcBalanceClient send to %s:%s",client.getHost(),client.getPort()));
		client.send(service, method, input, outClass, listener);
		
	}
	
	public static void main(String[] args){
		RpcBalanceClient balanceClient = new RpcBalanceClient("192.168.77.254",2181,"AddServer");
		
		try {
			Thread.sleep(10*60*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
