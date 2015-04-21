package com.rpc.client;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.rpc.counter.Counter;
import com.rpc.counter.CounterFactory;
import com.rpc.model.RpcException;
import com.rpc.model.RpcRequest;
import com.rpc.test.AddInputArgs;
import com.rpc.test.AddOutputArgs;

/**
 * RpcClient
 * RpcClient client = new RpcClient("127.0.0.1",9001);
 * 
 * public static void testAdd(RpcClient client) throws InterruptedException{
		Random rand = new Random(System.currentTimeMillis());
		
		AddInputArgs input = new AddInputArgs();
		input.setX(rand.nextInt(10000));
		input.setY(rand.nextInt(20000));
		
		client.send("AddServer", "add", input, AddOutputArgs.class,new RpcClientTransactionListener(){

			@Override
			public void callBack(Object o) {
				AddOutputArgs output = (AddOutputArgs)o;
				System.out.println("sum:" + output.getSum());
				
			}

			@Override
			public void error(RpcException ex) {
				System.out.println(ex.toString());
			}
			
		});
		
	}
 * 
 * @author lichunlei
 *
 */

public class RpcClient {

	private static Logger logger = LoggerFactory.getLogger(RpcClient.class);
	
	private String host;
	private int port;
	private ClientBootstrap bootstrap;
	private Channel channel; 
	private int timeout = 10000;
	private Timer timer;
	
	
	public RpcClient(String host,int port) throws InterruptedException{
		this(host,port,10000);
	}
	
	public RpcClient(String host,int port,int timeout){
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		bootstrap = new ClientBootstrap(  
                new NioClientSocketChannelFactory(  
                        Executors.newCachedThreadPool(),  
                        Executors.newCachedThreadPool())); 
		
		RpcClientPipelineFactory pipelineFactory = new RpcClientPipelineFactory(this);
		bootstrap.setPipelineFactory(pipelineFactory);

		bootstrap.setOption("tcpNoDelay", true);  
        bootstrap.setOption("keepAlive", true);
               
		connect();
		//检查连接状态
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				if(channel == null || !channel.isConnected()){
					try {
						connect();
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						logger.error("connect error:",e);
					}
				}
			}
			
		}, 1000, 1000);
		
		
	}
	
	public synchronized void  connect(){
		if(channel == null || !channel.isConnected()){
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
			//future.awaitUninterruptibly(1000);
			channel = future.getChannel();
		}
		
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void send(String service,String method,Object input,Class outClass,RpcClientTransactionListener listener) throws RpcException {
		//counter
		Counter counter = CounterFactory.getInstance().getConter(String.format("rpcClient-%s-%s",service,method));
		counter.begin();
				
		if(!channel.isConnected())
		{
			counter.end();
			counter.fail();
			throw new RpcException(400,"connection error");
		}

		//send
		String body = null;
		if(input != null)
			body = JSONObject.toJSONString(input);		
		RpcRequest request = new RpcRequest(service,method,body);
		RpcClientTransaction trans = new RpcClientTransaction(request,outClass,timeout);
		trans.setCounter(counter);
		trans.setListener(listener);
		RpcClientTransactionFactory.getInstance().addClientTransaction(trans);
		//网络异常，重新连接
		channel.write(request.toJsonString());

	}

}
