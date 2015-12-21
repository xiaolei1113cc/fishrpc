package com.lcl.rpc.server;

import java.nio.charset.Charset;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class RpcServerPipelineFactory implements  ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline =  Channels.pipeline();
		   
		pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
		pipeline.addLast("lengthEncoder", new LengthFieldPrepender(4,false));	
		pipeline.addLast("stringDecoder", new StringDecoder(Charset.forName("utf-8")));
		pipeline.addLast("stringEncoder", new StringEncoder(Charset.forName("utf-8")));    
		pipeline.addLast("handler", new RpcServerHandler());
   
        return pipeline;  
	}

}
