package com.anubhav.chatServer.thread;

import java.util.HashMap;

import com.anubhav.chatServer.domain.ClientSocket;

/*
 * This class will be converted to thread soon!
 */

public class ActiveConnectionThread {

	HashMap<String ,ClientSocket> map ; 
	
	public ActiveConnectionThread() {
		map = new HashMap<String,ClientSocket>();
	}
	
	public ClientSocket getClientSocketById(String id) {
		return map.get(id);
	}
	
	public void putClientSocket(String id , ClientSocket socket) {
		map.put(id, socket);
	}
}
