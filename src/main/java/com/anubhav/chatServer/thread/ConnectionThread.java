package com.anubhav.chatServer.thread;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.anubhav.chatServer.domain.ClientSocket;
import com.anubhav.chatServer.domain.Message;
import com.anubhav.chatServer.parser.XMLParser;

public class ConnectionThread implements Runnable{
	
	private static final String XMLMac = "mac";
	private static final String XMLId = "id";
	private static final String XMLUnique = "unique";
	private static final String stage1 = "s1";
	private static final String stage2 = "s2";
	private static final String stage3 = "s3";
	
	private static final String uniqueInitiatedMessage = "<message><type>fromserver</type><code>unique_send</code></message>";
	private static final String registrationMessage = "<message><type>fromserver</type><code>register</code></message>";
	private static final String uniqueTestPassedMessage="<message><type>fromserver</type><code>unique_passed</code></message>";
	private ClientSocket client;
	private XMLParser parser;
	private Message message;
	private MessageThread messageThread;
	private ActiveConnectionThread activeConnection;
	
	private String id = null;
	private String stage = null;
	private String mac = null;
	private String unique =null;
	private long unique_timestamp=0l;
	
	public ConnectionThread(ClientSocket client ,MessageThread messageThread , ActiveConnectionThread activeConnection) throws ParserConfigurationException {
		this.client = client;
		this.parser = new XMLParser();
		this.messageThread = messageThread;
		this.activeConnection = activeConnection;
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
				if(id == null) {
					System.out.println("stage 0 ");
					if(crossValidateIdData(message))messageThread.put(message);
				}
				else if(regularCheck(message)){
					System.out.println("stage 0 passed");
					if(stage1.equals(stage)) { // expecting registration message
						System.out.println("stage 1");
						createUnique();
						client.getOutputWriter().println(uniqueInitiatedMessage);
						stage = stage2;
					}
					else if(stage2.equals(stage)) { // expecting unique message
						System.out.println("stage 2");
						if(checkUnique()) {
							client.getOutputWriter().println(uniqueTestPassedMessage);
							saveDetailsInRedis();
							stage = stage3;
							activeConnection.putClientSocket(id, client);
						}
						else throw new Exception();
					}
					else if(stage3.equals(stage)) { // client is fully authenticated
					messageThread.put(message);
					}
					else throw new Exception();
				}
				else throw new Exception();	
			} 
			catch (Exception e) {
				e.printStackTrace();
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
			
		}
		
	}
	
	/*
	 * This method check whether the id of the client is present in redis
	 * and takes action accordingly
	 */
	
	public boolean isIdPresentInRedis() {
		String redisResult = null;
		/*
		 * check whether id is redis if
		 * 		and store it in "redis result"
		 * 		if it is null means not present i hope so
		 */
		if(redisResult==null) {
			sendRegistrationAck();
			return false;
		}
		mac = redisResult;
		return true;
	}
	
	public boolean crossValidateIdData(Message message) {
		id = message.getDocument().getElementsByTagName(XMLId).item(0).getTextContent();
		boolean res = false;
		String macInMessage = message.getDocument().getElementsByTagName(XMLMac).item(0).getTextContent();
		if(isIdPresentInRedis() && mac.equals(macInMessage)) {
			stage = stage3;
			return true;
		}
		sendRegistrationAck();
		stage = stage1;
		mac = macInMessage;
		return res;
	}
	
	public boolean regularCheck(Message message) {
		String idInMessage = message.getDocument().getElementsByTagName(XMLId).item(0).getTextContent();
		String macInMessage = message.getDocument().getElementsByTagName(XMLMac).item(0).getTextContent();
		return id.equals(idInMessage)&&mac.equals(macInMessage);
	}
	
	public void createUnique() {
		String random = "abc";
		client.getOutputWriter().println(random);
		unique = random;
		unique_timestamp = System.currentTimeMillis(); 
	}

	public boolean checkUnique() {
		String uniqueInMessage = message.getDocument().getElementsByTagName(XMLUnique).item(0).getTextContent();
		return unique.equals(uniqueInMessage);
	}
	
	public void saveDetailsInRedis() {
		
	}
	
	public void sendRegistrationAck() {
		client.getOutputWriter().println(registrationMessage);
	}

}
