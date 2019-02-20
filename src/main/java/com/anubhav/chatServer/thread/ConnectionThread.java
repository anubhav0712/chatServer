package com.anubhav.chatServer.thread;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.anubhav.chatServer.domain.ClientSocket;
import com.anubhav.chatServer.domain.Message;
import com.anubhav.chatServer.helper.RedisClient;
import com.anubhav.chatServer.parser.XMLParser;

public class ConnectionThread implements Runnable{
	
	private static final String XML_MAC = "mac";
	private static final String XML_ID = "id";
	private static final String XML_UNIQUE = "unique";
	private static final String STAGE_1 = "s1";
	private static final String STAGE_2 = "s2";
	private static final String STAGE_3 = "s3";
	private static final String UNIQUE_CHARACTER_SET = "abcdefghijklmnopqrstuvwxyz0123456789";
	private static final String UNIQUE_INITIATED_MESSAGE = "<message><type>fromserver</type><code>unique_send</code></message>";
	private static final String REGISTRATION_MESSAGE = "<message><type>fromserver</type><code>register</code></message>";
	private static final String UNIQUE_TEST_PASSED_MESSAGE="<message><type>fromserver</type><code>unique_passed</code></message>";
	private ClientSocket client;
	private XMLParser parser;
	private Message message;
	private MessageThread messageThread;
	private ActiveConnectionThread activeConnection;
	private RedisClient redisClient;
	
	private String id = null;
	private String stage = null;
	private String mac = null;
	private String unique =null;
	private long unique_timestamp=0l;
	
	public ConnectionThread(ClientSocket client ,MessageThread messageThread , ActiveConnectionThread activeConnection,RedisClient redisClient) throws ParserConfigurationException {
		this.client = client;
		this.parser = new XMLParser();
		this.messageThread = messageThread;
		this.activeConnection = activeConnection;
		this.redisClient = redisClient;
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
					if(crossValidateIdData(message)) {
						client.getOutputWriter().println(UNIQUE_TEST_PASSED_MESSAGE);
						activeConnection.putClientSocket(id, client);
					}
				}
				else if(regularCheck(message)){
					System.out.println("stage 0 passed");
					if(STAGE_1.equals(stage)) { // expecting registration message
						System.out.println("stage 1");
						createUnique();
						client.getOutputWriter().println(UNIQUE_INITIATED_MESSAGE);
						stage = STAGE_2;
					}
					else if(STAGE_2.equals(stage)) { // expecting unique message
						System.out.println("stage 2");
						if(checkUnique()) {
							client.getOutputWriter().println(UNIQUE_TEST_PASSED_MESSAGE);
							saveDetailsInRedis();
							stage = STAGE_3;
							activeConnection.putClientSocket(id, client);
						}
						else throw new Exception();
					}
					else if(STAGE_3.equals(stage)) { // client is fully authenticated
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
		String redisResult = redisClient.get(id);
		if(redisResult==null) {
			sendRegistrationAck();
			return false;
		}
		mac = redisResult;
		return true;
	}
	
	public boolean crossValidateIdData(Message message) {
		id = message.getDocument().getElementsByTagName(XML_ID).item(0).getTextContent();
		boolean res = false;
		String macInMessage = message.getDocument().getElementsByTagName(XML_MAC).item(0).getTextContent();
		if(isIdPresentInRedis() && mac.equals(macInMessage)) {
			stage = STAGE_3;
			return true;
		}
		sendRegistrationAck();
		stage = STAGE_1;
		mac = macInMessage;
		return res;
	}
	
	public boolean regularCheck(Message message) {
		String idInMessage = message.getDocument().getElementsByTagName(XML_ID).item(0).getTextContent();
		String macInMessage = message.getDocument().getElementsByTagName(XML_MAC).item(0).getTextContent();
		return id.equals(idInMessage)&&mac.equals(macInMessage);
	}
	
	public void createUnique() {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(int i=0;i<5;i++) {
			index = (int)(Math.ceil(Math.random()*100))%36;
			sb.append(UNIQUE_CHARACTER_SET.charAt(index));
		}
		unique = sb.toString();
		client.getOutputWriter().println(unique);
		unique_timestamp = System.currentTimeMillis(); 
	}

	public boolean checkUnique() {
		String uniqueInMessage = message.getDocument().getElementsByTagName(XML_UNIQUE).item(0).getTextContent();
		return unique.equals(uniqueInMessage);
	}
	
	public void saveDetailsInRedis() {
		redisClient.set(id, mac);
		
	}
	
	public void sendRegistrationAck() {
		client.getOutputWriter().println(REGISTRATION_MESSAGE);
	}

}
