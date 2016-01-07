package com.lcl.rpc.client;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lcl.rpc.common.RpcUtil;
import com.lcl.rpc.counter.Counter;
import com.lcl.rpc.model.RpcException;
import com.lcl.rpc.model.RpcMessageProto;
import com.lcl.rpc.model.RpcMessageType;
import com.lcl.rpc.model.RpcPackage;


/**
 * RpcClientHandler
 * @author lichunlei
 *
 */

public class RpcClientHandler extends SimpleChannelHandler{
	
	private static Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);
	//call back 线程池
	private static Executor executor = Executors.newCachedThreadPool();
	
	private RpcClient client;
	
	public RpcClientHandler(RpcClient client){
		super();
		this.client = client;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		
		RpcPackage pack = (RpcPackage)e.getMessage();
		
		if(pack.getMsgType().getValue() == RpcMessageType.RpcResponse.getValue()){
			byte[] message = pack.getPack();
			RpcMessageProto.RpcResponse response = null;
			try {
				response = RpcMessageProto.RpcResponse.parseFrom(message);
				logger.info("client received response: " + RpcUtil.RpcResponseToString(response));
				
			} catch (InvalidProtocolBufferException e1) {
				logger.error("RpcResonse parse error:{}",e1);
			}
			if(response == null)
				return;
			String seq =response.getSeq();
			RpcClientTransaction trans = RpcClientTransactionFactory.getInstance().getClientTransaction(seq);
			RpcClientTransactionFactory.getInstance().deleteClientTransaction(seq);
			
			if(trans != null) {
				
				trans.setResponse(response);
				//counter 时间区间为client－server－client，不包含client回调之后再执行的时间
				Counter counter = trans.getCounter();
				if(counter != null)
					counter.end();
				
				if(response.getStatus() == 200){			
					String body = response.getBody();
					Object result = null;
					if(body != null && body.length() > 0 )
						result = JSONObject.parseObject(body, trans.getOutputArgsClass());
					trans.getListener().callBack(result);
				}
				else { //error happened
					if(counter != null)
						counter.fail();
					RpcException ex = new RpcException(response.getStatus(),response.getBody());
					trans.getListener().error(ex);
				}
				
			}
			else {
				logger.warn("discard client transaction(maybe time out) response.body: " + response.getBody());
			}
		}
		else if(pack.getMsgType().getValue() == RpcMessageType.RpcRequest.getValue()){
			//String message = pack.getPack();
			byte[] message = pack.getPack();
			RpcMessageProto.RpcRequest request;
			try {
				request = RpcMessageProto.RpcRequest.parseFrom(message);
				logger.info("client received request: " + RpcUtil.RpcRequestToString(request));
			} catch (InvalidProtocolBufferException e1) {
				logger.error("RpcRequest parse error:{}",e1 );
			}
			
		}
		else if(pack.getMsgType().getValue() == RpcMessageType.Ack.getValue()){
			logger.info("client received ack:" + e.getRemoteAddress().toString());
			//System.out.println("client received ack:" + e.getRemoteAddress().toString());
			client.setLastAck(System.currentTimeMillis());
		}
		else {
			logger.error("client received unkown MessageType: " + pack.getMsgType().getMsgType());
		}
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		//网络异常，需要重新连接
		logger.error("RpcClientHandler.exceptionCaught: ",e.getCause());
		//client.connect();
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, e);
		
	}



}
