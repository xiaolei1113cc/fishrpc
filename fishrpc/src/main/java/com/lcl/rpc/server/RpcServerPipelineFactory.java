package com.lcl.rpc.server;


import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;


import com.lcl.rpc.channel.RpcPackageDecoder;
import com.lcl.rpc.channel.RpcPackageEncoder;

public class RpcServerPipelineFactory implements  ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline =  Channels.pipeline();
		
		//length decoder and encoder
		pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
		pipeline.addLast("lengthEncoder", new LengthFieldPrepender(4,false));	
//		pipeline.addLast("stringDecoder", new StringDecoder(Charset.forName("utf-8")));
//		pipeline.addLast("stringEncoder", new StringEncoder(Charset.forName("utf-8")));  
		pipeline.addLast("packDecoder", new RpcPackageDecoder());
		pipeline.addLast("packEncoder", new RpcPackageEncoder());
//		ExecutionHandler executionHandler = new ExecutionHandler(new MemoryAwareThreadPoolExecutor(16,10480,10480));
//		pipeline.addLast("executor", executionHandler);
		
		//timeout handler  180秒
		pipeline.addLast("timeoutHandler", new ReadTimeoutHandler(new HashedWheelTimer(),180));
		pipeline.addLast("handler", new RpcServerHandler());
   
        return pipeline;  
	}

}
