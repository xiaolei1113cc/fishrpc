package com.lcl.rpc.channel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.lcl.rpc.common.RpcUtil;
import com.lcl.rpc.model.RpcMessageProto;
import com.lcl.rpc.model.RpcMessageType;
import com.lcl.rpc.model.RpcPackage;

public class RpcPackageEncoder extends OneToOneEncoder {
	
	private byte version = 1;

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		
		RpcPackage pack = new RpcPackage();
		pack.setVersion(version);
		
		if(msg instanceof RpcMessageProto.RpcRequest){
			pack.setMsgType(RpcMessageType.RpcRequest);
			//pack.setPack(JSONObject.toJSONString(msg));
			byte[] req = ((RpcMessageProto.RpcRequest) msg).toByteArray();
			pack.setPack(req);
		}
		else if(msg instanceof RpcMessageProto.RpcResponse) {
			pack.setMsgType(RpcMessageType.RpcResponse);
			//pack.setPack(JSONObject.toJSONString(msg));
			byte[] res = ((RpcMessageProto.RpcResponse) msg).toByteArray();
			pack.setPack(res);
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
			pack.setPack(msg.toString().getBytes("utf-8"));
		}
		
		byte[] bytes = RpcUtil.packageMsg(pack);
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(bytes);
		return buffer;
		
	}

}
