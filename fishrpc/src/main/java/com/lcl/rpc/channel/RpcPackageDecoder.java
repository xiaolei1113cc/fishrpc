package com.lcl.rpc.channel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.lcl.rpc.common.RpcUtil;
import com.lcl.rpc.model.RpcPackage;

public class RpcPackageDecoder extends OneToOneDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		ChannelBuffer buffer = (ChannelBuffer)msg;
		byte[] bytes = buffer.array();
		RpcPackage pack = RpcUtil.unPackageMsg(bytes);
		return pack;
	}

}
