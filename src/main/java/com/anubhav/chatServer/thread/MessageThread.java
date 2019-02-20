package com.anubhav.chatServer.thread;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

import com.anubhav.chatServer.domain.ClientSocket;
import com.anubhav.chatServer.domain.Message;
import com.anubhav.chatServer.helper.CorrelationIdGenerator;
import com.anubhav.chatServer.parser.XMLMessageCreator;

public class MessageThread implements Runnable{
	private Queue<Message> queue;
	private ActiveConnectionThread activeConnectionThread;
	private CorrelationIdGenerator correlationIdGenerator;
	private XMLMessageCreator messageCreator;
	
	public MessageThread(ActiveConnectionThread activeConnectionThread) {
		queue = new LinkedList<Message>();
		this.activeConnectionThread = activeConnectionThread;
		this.correlationIdGenerator = CorrelationIdGenerator.getInstance();
		this.messageCreator = XMLMessageCreator.getInstance();
	}
	
	public void put(Message message) {
		queue.add(message);
	}
	
	public void startThread() {
		Thread ob = new Thread(this);
		ob.start();
	}

	public void run() {
		while(true) {
			if(queue.isEmpty()) {
				sleep(50);
				continue;
			}
			Message msg = queue.poll();
			String to = msg.getDocument().getElementsByTagName("to").item(0).getTextContent();
			String from = msg.getDocument().getElementsByTagName("from").item(0).getTextContent();
			Long id = correlationIdGenerator.getNextId();
			String message = messageCreator.getNewMessageWithNewNode(msg.getDocument(), "id", id.toString());
			System.out.println("new message for ::: "+to);
			System.out.println("message"+message);
			ClientSocket reciever = activeConnectionThread.getClientSocketById(to);
			if(reciever!=null && isReachable(reciever)) {
				reciever.getOutputWriter().println(message);
				System.out.println("message send!");
			}
			else {
				System.out.println("receiver not active");
			}
			ClientSocket sender = activeConnectionThread.getClientSocketById(from);
			
			messageCreator.setId(id);
			message = messageCreator.getAckMessage();
			System.out.println("sending ack to ::: "+from);
			System.out.println(message);
			if(sender!=null && isReachable(sender)) {
				sender.getOutputWriter().println(message);
				System.out.println("ack send");
			}
			else {
				System.out.println("sender not active");
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
	
	public static boolean isReachable(ClientSocket socket) {
		boolean res = !socket.getSocket().isClosed();
		return res;
	}
	
	
}
