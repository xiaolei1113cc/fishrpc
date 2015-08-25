package com.rpc.zkclient;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.I0Itec.zkclient.*;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import com.alibaba.fastjson.JSONObject;

public class RpcRegister {
	
	private static final Logger logger = LoggerFactory.getLogger(RpcRegister.class);
	public static final String RpcServerRoot = "/rpcserver";

	
	private String connStr;
	private int timeout = 2000;
	private ZkClient zkClient;
	
	public RpcRegister(String connStr,int timeout) {
		logger.info(String.format("connStr:%s,timeout:%s", connStr,timeout));
		this.connStr = connStr;
		this.timeout = timeout;
		zkClient = new ZkClient(connStr,timeout);
	}
	
	public void registerServer(String serviceName,String ip,int port){
		if(!zkClient.exists(RpcServerRoot))
			zkClient.createPersistent(RpcServerRoot);
		
		String servicePath = RpcServerRoot + "/" + serviceName;
		if(!zkClient.exists(servicePath))
			zkClient.createPersistent(servicePath);
		RpcServerNode data = new RpcServerNode(serviceName,ip,port);
		
		String childrenPrefix = servicePath + "/" + "server";
		
		zkClient.createEphemeralSequential(childrenPrefix, data);
	}
	
	/**
	 * 侦听某个服务的改变
	 * @param serviceName
	 * @param listener
	 */
	public void listen(String serviceName,IZkChildListener listener) {
		if(!zkClient.exists(RpcServerRoot))
			zkClient.createPersistent(RpcServerRoot);
		
		String servicePath = RpcServerRoot + "/" + serviceName;
		if(!zkClient.exists(servicePath))
			zkClient.createPersistent(servicePath);
		
		zkClient.subscribeChildChanges(servicePath, listener);
	}
	/**
	 * 侦听所有服务的改变
	 * @param listener
	 */
	public void listen(IZkChildListener listener) {
		if(!zkClient.exists(RpcServerRoot))
			zkClient.createPersistent(RpcServerRoot);
		
		zkClient.subscribeChildChanges(RpcServerRoot, listener);
	}
	
	public RpcServerNode getDate(String path){
		RpcServerNode node = (RpcServerNode)zkClient.readData(path);
		return node;
	}
	
	public List<String> getChildren(String path){
		return zkClient.getChildren(path);
	}
	
	public static void main(String[] args) {
		final RpcRegister register = new RpcRegister("192.168.77.254",2181);
		
		register.listen("TestServer", new IZkChildListener(){

			@Override
			public void handleChildChange(String parentPath,
					List<String> currentChilds) throws Exception {
				for(String str : currentChilds){
					String dataPath = parentPath + "/"+ str;
					System.out.println("handleChildChange:" + dataPath);
					RpcServerNode node = register.getDate(dataPath);
					System.out.println(node.toJson());
				}
			}
			
		});
		
		register.registerServer("TestServer", "127.0.0.1", 7001);
		register.registerServer("TestServer", "127.0.0.1", 7002);
		
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * rpc server node
	 * @author lichunlei
	 *
	 */
	public static class RpcServerNode implements ZkSerializer,Serializable{
		private String serviceName;
		private String ip;
		private int port;
		
		public RpcServerNode(){
			
		}
		
		public RpcServerNode(String serviceName,String ip,int port) {
			this.serviceName = serviceName;
			this.ip = ip;
			this.port = port;
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		@Override
		public byte[] serialize(Object data) throws ZkMarshallingError {
			String json = JSONObject.toJSONString(this);
			byte[] bys = json.getBytes(Charset.forName("utf-8"));
			return bys;
		}

		@Override
		public Object deserialize(byte[] bytes) throws ZkMarshallingError {
			try {
				String json = new String(bytes,"utf-8");
				RpcServerNode node = JSONObject.parseObject(json,RpcServerNode.class);
				return node;
			} catch (UnsupportedEncodingException e) {
				logger.error("UnsupportedEncodingException : ",e);
			}
			return null;
		}
		
		public String toJson(){
			return JSONObject.toJSONString(this);
		}

	}
}
