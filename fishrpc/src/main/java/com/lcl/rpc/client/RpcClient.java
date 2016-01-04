package com.lcl.rpc.client;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lcl.rpc.counter.Counter;
import com.lcl.rpc.counter.CounterFactory;
import com.lcl.rpc.model.RpcException;
import com.lcl.rpc.model.RpcMessageProto;

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
	boolean close = false;
	private long lastAck = System.currentTimeMillis();
	private AtomicLong seq = new AtomicLong(0);
	
	
	public RpcClient(String host,int port) throws InterruptedException{
		this(host,port,10000);
	}
	
	public RpcClient(String host,int port,int timeout){
		
		logger.info(String.format("RpcClient host:%s,port:%s,timeout:%s", host,port,timeout));
		
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
		TimerTask timerTask = new TimerTask(){

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
			
		};
		
		TimerTask keepaliveTask = new TimerTask(){

			@Override
			public void run() {
				try{		
					keepalive();
	
					//如果超过3分钟没有收到keepalive的ack消息
					long lastAckSecondes = (System.currentTimeMillis() - lastAck)/1000;
					if( lastAckSecondes >200){
						logger.error("keep alive time out (s):" + lastAckSecondes);
						try{
							channel.close();
							channel = null;
						}catch(Exception ex) {
							
						}
					}
				}catch(Exception ex){
					logger.error("keepalive error:{}",ex);
				}
				
			}
			
		};
		//每30s检查一下链接状态
		timer.schedule(timerTask, 10000, 30000);
		//每分钟一次keepalive
		timer.schedule(keepaliveTask, 5000, 60000);
		
		
	}
	
	public synchronized void  connect(){
		
		if(channel == null || !channel.isConnected()){
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
			//future.awaitUninterruptibly(1000);
			channel = future.getChannel();
		}
		
	}
	
	public void keepalive(){
		if(channel != null && channel.isConnected())
			channel.write("keepalive");
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
	
	public long getLastAck() {
		return lastAck;
	}

	public void setLastAck(long lastAck) {
		this.lastAck = lastAck;
	}

	public <T> void send(String service,String method,Object input,Class<T> outClass,RpcClientTransactionListener<T> listener) throws RpcException {
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
			
		RpcMessageProto.RpcRequest.Builder request = RpcMessageProto.RpcRequest.newBuilder();
		request.setSeq(String.valueOf(seq.incrementAndGet()));
		request.setVersion(1);
		request.setService(service);
		request.setMethod(method);
		if(body != null)
			request.setBody(body);
		RpcClientTransaction trans = new RpcClientTransaction(request.build(),outClass,timeout);
		trans.setCounter(counter);
		trans.setListener(listener);
		RpcClientTransactionFactory.getInstance().addClientTransaction(trans);
		//网络异常，重新连接
		channel.write(request.build());

	}
	
	public void close(){
		logger.info(String.format("RpcClient.close host:%s,port:%s,timeout:%s", host,port,timeout));
		try{
			close = true;
			channel.close();
			timer.cancel();
		}catch(Exception ex) {
			logger.error("close error:",ex);
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RpcClient other = (RpcClient) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		return true;
	}


	
}
