package com.tungsten.hmclpe.utils.io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;
import java.net.InetAddress;

import android.os.Handler;
import android.os.Message;

public class SocketServer {

	private DatagramPacket packet;
	private DatagramSocket socket;
	private Listener mListener;

	public interface Listener{
		void onReceive(String msg);
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String s = (String)msg.obj;
			mListener.onReceive(s);
		}
	};
	
	public SocketServer(String ip,int port,Listener mListener){
		this.mListener = mListener;
		try {
			// 要接收的报文
			byte[] bytes = new byte[1024];
			packet = new DatagramPacket(bytes, bytes.length);
			// 创建socket并指定端口
			socket = new DatagramSocket(port,InetAddress.getByName(ip));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void start() {
		if(packet == null || socket == null)
			return;
		new Thread(() -> {
			// 接收socket客户端发送的数据。如果未收到会一致阻塞
			try {
				while(true){
					socket.receive(packet);
					String receiveMsg = new String(packet.getData(), 0, packet.getLength());
					//System.out.println(packet.getLength());
					System.out.println(receiveMsg);
					Message msg = new Message();
					msg.obj = receiveMsg;
					mHandler.sendMessage(msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void stop(){
		// 关闭socket
		socket.close();
	}

}
