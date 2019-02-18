package com.anubhav.chatServer.db;

import java.util.HashMap;

import com.anubhav.chatServer.domain.ClientSocket;

public class ServerSocketPool {
	private HashMap<Integer , ClientSocket> map;
	
	public ServerSocketPool() {
		map = new HashMap<Integer , ClientSocket>();
	}

	public boolean containsId(int id) {
		return map.containsKey(id);
	}
	
	public ClientSocket getSocketFromId(int id) {
		return map.get(id);
	}
	
	public boolean saveEntry(int id , ClientSocket socket) {
		if(map.containsKey(id))return false;
		map.put(id, socket);
		return true;
	}
	
	public boolean removeEntry(int id) {
		if(map.remove(id)==null)return false;
		return true;
	}
}
