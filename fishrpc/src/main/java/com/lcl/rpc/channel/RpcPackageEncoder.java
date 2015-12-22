package com.lcl.rpc.channel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.alibaba.fastjson.JSONObject;
import com.lcl.rpc.common.RpcUtil;
import com.lcl.rpc.model.RpcMessageType;
import com.lcl.rpc.model.RpcPackage;
import com.lcl.rpc.model.RpcRequest;
import com.lcl.rpc.model.RpcResponse;

public class RpcPackageEncoder extends OneToOneEncoder {
	
	private byte version = 1;

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		
		RpcPackage pack = new RpcPackage();
		pack.setVersion(version);
		
		if(msg instanceof RpcRequest){
			pack.setMsgType(RpcMessageType.RpcRequest);
			pack.setPack(JSONObject.toJSONString(msg));
		}
		else if(msg instanceof RpcResponse) {
			pack.setMsgType(RpcMessageType.RpcResponse);
			pack.setPack(JSONObject.toJSONString(msg));
		}
		else if(msg instanceof String){
			if("keepalive".equals(msg)){
				pack.setMsgType(RpcMessageType.KeepAlive);
			}
			else if("ack".equals(msg)){
				pack.setMsgType(RpcMessageType.Ack);
			}
			else{
				pack.setMsgType(RpcMessageType.Unknown);
			}
			pack.setPack(msg.toString());
		}
		
		byte[] bytes = RpcUtil.packageMsg(pack);
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(bytes);
		return buffer;
		
	}

}
