package com.rpc.server;

import java.lang.reflect.Method;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rpc.annotation.RpcMethod;
import com.rpc.annotation.RpcService;
import com.rpc.zkclient.RpcRegister;

/**
 * RpcServerFactory
 * @author lichunlei
 *
 */
public class RpcServerFactory {
	
	private static Logger logger = LoggerFactory.getLogger(RpcServerFactory.class);
	
	private static RpcServerFactory instance = new RpcServerFactory();
	private static RpcRegister register;
	
	public static RpcServerFactory getInstance() {
		return instance;
	}
	
	private RpcServerFactory() {
		
	}

	private Hashtable<String,Method> handlers = new Hashtable<String,Method>();
	private Hashtable<String,Object> services = new Hashtable<String,Object>();
	
	/**
	 * 注册RpcService，可以同时注册多个RpcSerive
	 * 只是注意@RpcSerive的name不要重复
	 * @param rpcService
	 */
	public void register(RpcServerConfig config,Object rpcService){
		logger.info("register service:"+rpcService.getClass().getName());
		
		if(config == null) {
			logger.error("config is null");
			throw new IllegalArgumentException("config is null");
		}
		
		RpcService rpcSvr = rpcService.getClass().getAnnotation(RpcService.class);
		if(rpcSvr == null)
		{
			logger.error(rpcService.getClass().getName() + " has no annotation @RpcService");
			throw new IllegalArgumentException("You must add annotation @RpcService to:" + rpcService.getClass().getName());
		}
		
		if(services.contains(rpcSvr.name())){
			logger.error("Duplicate annotation @RpcService :" + rpcSvr.name());
			throw new IllegalArgumentException("Duplicate annotation @RpcService :" + rpcSvr.name());
		}
		//注册service
		services.put(rpcSvr.name(), rpcService);
		//注册method
		Method[] methods = rpcService.getClass().getMethods();
		for(Method m : methods){
			RpcMethod meth = m.getAnnotation(RpcMethod.class);
			if(meth != null)
			{
				String key = getMethodKey(rpcSvr.name(),meth.name());
				if(handlers.contains(key)) {
					logger.error("Duplicate annotation @RpcMethod :" + key);
					throw new IllegalArgumentException("Duplicate annotation @RpcMethod :" + key);
				}
				handlers.put(key, m);
			}
		}
		//有zk配置的话，走zk配置
		if(register == null && config.getZkConnection()!=null)
			register = new RpcRegister(config.getZkConnection(),config.getZkTimeout());
		if(register != null)
			register.registerServer(rpcSvr.name(), config.getIp(), config.getPort());
		
	}
	
	public Method getMethod(String service,String method) {
		String key = getMethodKey(service,method);
		return handlers.get(key);
	}
	
	private String getMethodKey(String service,String method){
		return service + "_" + method;
	}
	
	public Object getService(String service){
		return services.get(service);
	}
	
	
}
