package com.anubhav.chatServer.parser;


import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLParser {

	private DocumentBuilder dom;
	private InputSource src;
	
	public XMLParser() throws ParserConfigurationException {
		dom = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		src = new InputSource();
	}
	
	public Document parse(String msg) throws SAXException, IOException {
		src.setCharacterStream(new StringReader(msg));
		return dom.parse(src);
	}
}
