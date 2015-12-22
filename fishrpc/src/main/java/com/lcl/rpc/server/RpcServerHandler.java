package com.lcl.rpc.server;

import java.lang.reflect.Method;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lcl.rpc.common.RpcUtil;
import com.lcl.rpc.counter.Counter;
import com.lcl.rpc.counter.CounterFactory;
import com.lcl.rpc.model.RpcMessageType;
import com.lcl.rpc.model.RpcPackage;
import com.lcl.rpc.model.RpcRequest;
import com.lcl.rpc.model.RpcResponse;

public class RpcServerHandler extends SimpleChannelHandler{

	private static Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
		RpcPackage pack = (RpcPackage)e.getMessage();
		if(pack.getMsgType().getValue() == RpcMessageType.RpcRequest.getValue()){
			String message = pack.getPack();
			logger.info(String.format("server received request:%s", message));
			//计数器 － RPC
			Counter rpcCounter = CounterFactory.getInstance().getConter("RPC");
			rpcCounter.begin();
			//System.out.println("server received message: " + message);
			try{
				RpcRequest request = RpcRequest.fromJsonString(message);		
				String service = request.getService();
				String method = request.getMethod();
				Object s = RpcServerFactory.getInstance().getService(service);
				//service not found
				if(s == null)
				{
					RpcResponse response = new RpcResponse(request.getSeq(),RpcResponse.SERVICE_NOT_FOUND,"service not found");
					e.getChannel().write(response.toJsonString());
					return;
				}
				Method m = RpcServerFactory.getInstance().getMethod(service, method);
				//method not found
				if(m == null)
				{
					RpcResponse response = new RpcResponse(request.getSeq(),RpcResponse.METHOD_NOT_FOUND,"method not found");
					e.getChannel().write(response.toJsonString());
					return;
				}
				//计数器－业务处理部分
				Counter methodCounter = CounterFactory.getInstance().getConter(String.format("RPC-%s-%s", service,method));
				methodCounter.begin();
				RpcResponse response = new RpcResponse(request.getSeq(),200);
				try{
					Object result = null;
					if(m.getParameterTypes() != null && m.getParameterTypes().length > 0) {
						Class inputType = m.getParameterTypes()[0];
						Object inputArgs = JSONObject.parseObject(request.getBody(),inputType);
						result = m.invoke(s, inputArgs);
					}
					else
						result = m.invoke(s);
					
					if(result != null) {
						String body = JSONObject.toJSONString(result);
						response.setBody(body);
					}
				}catch(Exception ex) {
					 response = new RpcResponse(request.getSeq(),500);
					 response.setBody(RpcUtil.getErrorInfoFromException(ex));
					 methodCounter.fail();
				}
				finally{
					methodCounter.end();
				}
		
				e.getChannel().write(response);
			}catch(Exception ex){
				logger.error("RpcServerHandler.messageReceived error:",ex);
				rpcCounter.fail();
			}
			finally	{
				rpcCounter.end();
			}
		}
		else if(pack.getMsgType().getValue() == RpcMessageType.RpcResponse.getValue()){
			String message = pack.getPack();
			logger.info(String.format("server received response:%s", message));
		}
		else if(pack.getMsgType().getValue() == RpcMessageType.KeepAlive.getValue()) {
			logger.info(String.format("server received keepalive:%s",e.getRemoteAddress().toString()));
			//System.out.println(String.format("server received keepalive:%s",e.getRemoteAddress().toString()));
			e.getChannel().write("ack");
		}
		else {
			logger.error("unkown MessageType:" + pack.getMsgType().getMsgType());
		}
	}
	
//	@Override
//	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
//			throws Exception {
//		String message = e.getMessage().toString();
//		logger.info(String.format("server received message:%s", message));
//		//计数器 － RPC
//		Counter rpcCounter = CounterFactory.getInstance().getConter("RPC");
//		rpcCounter.begin();
//		//System.out.println("server received message: " + message);
//		try{
//			RpcRequest request = RpcRequest.fromJsonString(message);		
//			String service = request.getService();
//			String method = request.getMethod();
//			Object s = RpcServerFactory.getInstance().getService(service);
//			//service not found
//			if(s == null)
//			{
//				RpcResponse response = new RpcResponse(request.getSeq(),RpcResponse.SERVICE_NOT_FOUND,"service not found");
//				e.getChannel().write(response.toJsonString());
//				return;
//			}
//			Method m = RpcServerFactory.getInstance().getMethod(service, method);
//			//method not found
//			if(m == null)
//			{
//				RpcResponse response = new RpcResponse(request.getSeq(),RpcResponse.METHOD_NOT_FOUND,"method not found");
//				e.getChannel().write(response.toJsonString());
//				return;
//			}
//			//计数器－业务处理部分
//			Counter methodCounter = CounterFactory.getInstance().getConter(String.format("RPC-%s-%s", service,method));
//			methodCounter.begin();
//			RpcResponse response = new RpcResponse(request.getSeq(),200);
//			try{
//				Object result = null;
//				if(m.getParameterTypes() != null && m.getParameterTypes().length > 0) {
//					Class inputType = m.getParameterTypes()[0];
//					Object inputArgs = JSONObject.parseObject(request.getBody(),inputType);
//					result = m.invoke(s, inputArgs);
//				}
//				else
//					result = m.invoke(s);
//				
//				if(result != null) {
//					String body = JSONObject.toJSONString(result);
//					response.setBody(body);
//				}
//			}catch(Exception ex) {
//				 response = new RpcResponse(request.getSeq(),500);
//				 response.setBody(RpcUtil.getErrorInfoFromException(ex));
//				 methodCounter.fail();
//			}
//			finally{
//				methodCounter.end();
//			}
//	
//			e.getChannel().write(response.toJsonString());
//		}catch(Exception ex){
//			logger.error("RpcServerHandler.messageReceived error:",ex);
//			rpcCounter.fail();
//		}
//		finally	{
//			rpcCounter.end();
//		}
//		
//	}

}
