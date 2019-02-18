package com.anubhav.chatServer.id;

public class CorrelationIdGenerator {
	public static long nextId = 0;
	private static CorrelationIdGenerator obj;
	
	static {
		obj = new CorrelationIdGenerator();
	}
	
	private CorrelationIdGenerator() {
		
	}
	
	public static CorrelationIdGenerator getInstance() {
		return obj;
	}
	
	public long getNextId() {
		return nextId++;
	}

}
