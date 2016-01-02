package com.lcl.rpc.server;

import java.lang.reflect.Method;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lcl.rpc.common.RpcUtil;
import com.lcl.rpc.counter.Counter;
import com.lcl.rpc.counter.CounterFactory;
import com.lcl.rpc.model.RpcMessageProto;
import com.lcl.rpc.model.RpcMessageType;
import com.lcl.rpc.model.RpcPackage;
import com.lcl.rpc.model.RpcResponseStatus;


public class RpcServerHandler extends SimpleChannelHandler{

	private static Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
		RpcPackage pack = (RpcPackage)e.getMessage();
		if(pack.getMsgType().getValue() == RpcMessageType.RpcRequest.getValue()){
			//String message = pack.getPack();
			byte[] message = pack.getPack();
			//计数器 － RPC
			Counter rpcCounter = CounterFactory.getInstance().getConter("RPC");
			rpcCounter.begin();
			//System.out.println("server received message: " + message);
			try{
				RpcMessageProto.RpcRequest request = RpcMessageProto.RpcRequest.parseFrom(message);
				logger.info(String.format("server received request:%s", RpcUtil.RpcRequestToString(request)));
				
				String service = request.getService();
				String method = request.getMethod();
				Object s = RpcServerFactory.getInstance().getService(service);
				//service not found
				if(s == null)
				{
					//RpcResponse response = new RpcResponse(request.getSeq(),RpcResponse.SERVICE_NOT_FOUND,"service not found");
					//e.getChannel().write(response.toJsonString());
					RpcMessageProto.RpcResponse.Builder response = RpcMessageProto.RpcResponse.newBuilder();
					response.setVersion(1);
					response.setSeq(request.getSeq());
					response.setStatus(RpcResponseStatus.SERVER_NOT_FOUND);
					response.setBody("service not found");
					e.getChannel().write(response.build());
					return;
				}
				Method m = RpcServerFactory.getInstance().getMethod(service, method);
				//method not found
				if(m == null)
				{
//					RpcResponse response = new RpcResponse(request.getSeq(),RpcResponse.METHOD_NOT_FOUND,"method not found");
//					e.getChannel().write(response.toJsonString());
					RpcMessageProto.RpcResponse.Builder response = RpcMessageProto.RpcResponse.newBuilder();
					response.setVersion(1);
					response.setSeq(request.getSeq());
					response.setStatus(RpcResponseStatus.METHOD_NOT_FOUND);
					response.setBody("method not found");
					
					e.getChannel().write(response.build());
					return;
				}
				//计数器－业务处理部分
				Counter methodCounter = CounterFactory.getInstance().getConter(String.format("RPC-%s-%s", service,method));
				methodCounter.begin();
				//RpcResponse response = new RpcResponse(request.getSeq(),200);
				RpcMessageProto.RpcResponse.Builder response = RpcMessageProto.RpcResponse.newBuilder();
				response.setSeq(request.getSeq());
				response.setStatus(200);
				response.setVersion(1);
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
					// response = new RpcResponse(request.getSeq(),500);
					response.setStatus(500);
					 response.setBody(RpcUtil.getErrorInfoFromException(ex));
					 methodCounter.fail();
				}
				finally{
					methodCounter.end();
				}
		
				e.getChannel().write(response.build());
			}catch(Exception ex){
				logger.error("RpcServerHandler.messageReceived error:",ex);
				rpcCounter.fail();
			}
			finally	{
				rpcCounter.end();
			}
		}
		else if(pack.getMsgType().getValue() == RpcMessageType.RpcResponse.getValue()){
			byte[] message = pack.getPack();
			try {
				RpcMessageProto.RpcResponse response = RpcMessageProto.RpcResponse.parseFrom(message);
				logger.info(String.format("server received response:%s", RpcUtil.RpcResponseToString(response)));
			} catch (InvalidProtocolBufferException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
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

}
