package com.anubhav.chatServer.thread;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.parsers.ParserConfigurationException;

import com.anubhav.chatServer.domain.ClientSocket;

public class NewClientHandlerThread implements Runnable{
	private ActiveConnectionThread activeConnection;
	private MessageThread messageThread;
	private Queue<ClientSocket> queue;
	private String str;

	public NewClientHandlerThread(ActiveConnectionThread activeConnection , MessageThread messageThread) {
		queue = new LinkedList<ClientSocket>();
		this.activeConnection = activeConnection; 
		this.messageThread = messageThread;
	}
	
	public void put(ClientSocket clientSocket) {
		queue.add(clientSocket);
	}
	
	public void startThread() {
		Thread ob=new Thread(this);
		ob.start();
	}
	
	public void run() {
		while(true) {
			if(queue.isEmpty()) {
				sleep(50);
				continue;
			}
			ClientSocket socket = queue.poll();
			try {
				System.out.println("Handling New Client ::: "+socket.hashCode());
				socket.getSocket().setSoTimeout(6000);
				System.out.println("Waiting for client reply ::: "+socket.hashCode());
				str = socket.getInputReader().readLine();
				activeConnection.putClientSocket(str, socket);
				socket.getSocket().setSoTimeout(0);
				System.out.println("client ::: "+socket.hashCode() +" registered with id ::: "+str);
				try {
					ConnectionThread connectionThread = new ConnectionThread(socket, messageThread);
					connectionThread.startThread();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			} 
			catch (SocketException e) {
				System.out.println("timeout for client ::: "+socket.hashCode()+" closing connection!");
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sleep(int milli) {
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
