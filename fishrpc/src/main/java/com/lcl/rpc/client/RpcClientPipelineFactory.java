package com.lcl.rpc.client;

import java.nio.charset.Charset;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * RpcClientPipelineFactory
 * @author lichunlei
 *
 */

public class RpcClientPipelineFactory implements  ChannelPipelineFactory{

	private RpcClient client;

	
	public RpcClientPipelineFactory(RpcClient client){
		this.client = client;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		
		pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
		pipeline.addLast("lengthEncoder", new LengthFieldPrepender(4,false));	
		pipeline.addLast("stringDecoder", new StringDecoder(Charset.forName("utf-8")));
		pipeline.addLast("stringEncoder", new StringEncoder(Charset.forName("utf-8")));  
		pipeline.addLast("handler", new RpcClientHandler(client));
	  
	    return pipeline;  
	}

}
