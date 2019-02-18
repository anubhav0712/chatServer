package com.anubhav.chatServer.domain;

import org.w3c.dom.Document;

public class Message {
	private Document document;
	private String msg;
	
	public Message(Document document, String msg) {
		super();
		this.document = document;
		this.msg = msg;
	}

	public Document getDocument() {
		return document;
	}
	
	

	public String getMsg() {
		return msg;
	}
	
	
	
}
