package com.lcl.rpc.client;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleStateAwareChannelHandler extends SimpleChannelHandler {
	
	private static Logger logger = LoggerFactory.getLogger(IdleStateAwareChannelHandler.class);

	private RpcClient client;
	
	public IdleStateAwareChannelHandler(RpcClient client) {
		super();
		this.client = client;
	}

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

		if (e instanceof IdleStateEvent) {
			channelIdle(ctx, (IdleStateEvent) e);
		} else {
			super.handleUpstream(ctx, e);
		}

	}

	private void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
		client.keepalive();
		//如果超过3分钟没有收到keepalive的ack消息
		long lastAckSecondes = (System.currentTimeMillis() - client.getLastAck())/1000;
		if( lastAckSecondes > 150){
			logger.error("keep alive time out (s):" + lastAckSecondes);
			try{
				client.close();
				client.connect();
			}catch(Exception ex) {
				
			}
		}
	}
}
