package com.lcl.rpc.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.lcl.rpc.model.RpcMessageType;
import com.lcl.rpc.model.RpcPackage;

/**
 * RpcUtil
 * @author lichunlei
 *
 */
public class RpcUtil {
	
	
	public static String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "bad getErrorInfoFromException";
        }
    }
	
	public static byte[] packageMsg(String msg,int version,int msgType) throws UnsupportedEncodingException {
		byte[] sendbytes = null;
		if(msg != null && msg.length() > 0){
			byte[] bytes = msg.getBytes("utf-8");
			sendbytes = new byte[bytes.length + 2];
			sendbytes[0] = (byte)version;
			sendbytes[1] = (byte)msgType;
			
			System.arraycopy(bytes, 0, sendbytes, 2, bytes.length);
		}
		else{
			sendbytes = new byte[2];
			sendbytes[0] = (byte)version;
			sendbytes[1] = (byte)msgType;
		}
		return sendbytes;
	}
	
	public static byte[] packageMsg(RpcPackage pack) throws UnsupportedEncodingException {
		
		return packageMsg(pack.getPack(),pack.getVersion(),pack.getMsgType().getValue());
	}
	
	public static RpcPackage unPackageMsg(byte[] bytes) throws UnsupportedEncodingException {
		
		if(bytes.length < 2)
			return null;
		
		byte version = bytes[0];
		RpcMessageType messageType = RpcMessageType.parse(bytes[1]);
		if(bytes.length == 2)
			return new RpcPackage(version,messageType,null);
		
		byte[] msgBytes = Arrays.copyOfRange(bytes, 2, bytes.length);
		
		String message = new String(msgBytes,"utf-8");
		return new RpcPackage(version,messageType,message);
	}

}
