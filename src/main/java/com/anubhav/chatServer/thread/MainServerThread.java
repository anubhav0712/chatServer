package com.anubhav.chatServer.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.parsers.ParserConfigurationException;

import com.anubhav.chatServer.domain.ClientSocket;

/*
 * Main thread that accepts connection
 * (only one object of this class should be created == singleton)
 */

public class MainServerThread implements Runnable{
	
	private static MainServerThread obj;
	private ServerSocket serverSocket;
	private ActiveConnectionThread activeConnection;
	private NewClientHandlerThread newClientHandlerThread;
	private MessageThread messageThread;
	private boolean started;
	private int port;
	
	static{
		obj = new MainServerThread();
	}
	
	private MainServerThread() {
		started = false;
	}
	
	public static MainServerThread getInstance(int port) throws IOException {
		obj.port = port;
		obj.serverSocket = new ServerSocket(obj.port);
		obj.activeConnection = new ActiveConnectionThread();
		obj.messageThread = new MessageThread(obj.activeConnection);
		obj.newClientHandlerThread =new NewClientHandlerThread(obj.activeConnection ,obj.messageThread);
		
		return obj;
	}
	
	public void startThread() {
		if(started)return;
		Thread ob = new Thread(obj);
		ob.start();
		System.out.println("Main Server Started...");
		obj.newClientHandlerThread.startThread();
		System.out.println("Client Handler Started...");
		obj.messageThread.startThread();
		System.out.println("Message Handler Started...");
		started = true;
	}
	
	public void run() {
		
		while(true) {
			try {
				Socket socket = serverSocket.accept();
				ClientSocket client = new ClientSocket(socket);
				newClientHandlerThread.put(client);
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	
}
