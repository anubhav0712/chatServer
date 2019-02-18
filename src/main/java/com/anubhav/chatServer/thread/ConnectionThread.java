package com.anubhav.chatServer.thread;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.anubhav.chatServer.domain.ClientSocket;
import com.anubhav.chatServer.domain.Message;
import com.anubhav.chatServer.parser.XMLParser;

public class ConnectionThread implements Runnable{
	
	private ClientSocket client;
	private XMLParser parser;
	private Message message;
	private MessageThread messageThread;
	
	public ConnectionThread(ClientSocket client ,MessageThread messageThread) throws ParserConfigurationException {
		this.client = client;
		this.parser = new XMLParser();
		this.messageThread = messageThread;
	}
	
	public void startThread() {
		Thread ob = new Thread(this);
		ob.start();
	}

	public void run() {
		while(true) {
			try {
				String msg = client.getInputReader().readLine();
				System.out.println("Message recieved in connection thread : "+msg);
				message = new Message(parser.parse(msg) , msg);
				messageThread.put(message);
			} 
			catch (IOException e) {
				System.out.println(client.getSocket().getInetAddress()+ "got broked!");
				try {
					System.out.println(client.getSocket().getInetAddress()+" closing socket");
					client.close();
					break;
				} catch (IOException e1) {
					System.out.println("Not able to close socket");
					e1.printStackTrace();
				}
			}
			catch (SAXException e) {
				e.printStackTrace();
			}
		}
		
	}

}
