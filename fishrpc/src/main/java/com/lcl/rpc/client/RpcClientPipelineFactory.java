package com.lcl.rpc.client;

import java.nio.charset.Charset;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.lcl.rpc.channel.RpcPackageDecoder;
import com.lcl.rpc.channel.RpcPackageEncoder;

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
		
		pipeline.addLast("idleStateHandler", new IdleStateHandler(new HashedWheelTimer(),0,0,60));
		pipeline.addLast("idleStateAwareHandler", new IdleStateAwareChannelHandler(client));
		
		pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
		pipeline.addLast("lengthEncoder", new LengthFieldPrepender(4,false));	
//		pipeline.addLast("stringDecoder", new StringDecoder(Charset.forName("utf-8")));
//		pipeline.addLast("stringEncoder", new StringEncoder(Charset.forName("utf-8")));  
		pipeline.addLast("packDecoder", new RpcPackageDecoder());
		pipeline.addLast("packEncoder", new RpcPackageEncoder());
//		ExecutionHandler executionHandler = new ExecutionHandler(new MemoryAwareThreadPoolExecutor(200,10480,10480));
//		pipeline.addLast("executor", executionHandler);

		pipeline.addLast("handler", new RpcClientHandler(client));
	  
	    return pipeline;  
	}

}
