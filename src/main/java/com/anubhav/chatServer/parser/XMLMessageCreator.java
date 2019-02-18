package com.anubhav.chatServer.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLMessageCreator {
	private XMLParser parser;
	private static String basicTemplate = "<ack><id></id></ack>";
	private static XMLMessageCreator obj;
	private static StringWriter writer = new StringWriter();
	private static StreamResult result = new StreamResult(writer);
	private static Transformer transform ;
	private Document document;
	static {
		obj = new XMLMessageCreator();
		try {
			obj.parser = new XMLParser();
			obj.document = obj.parser.parse(basicTemplate);
			transform = TransformerFactory.newInstance().newTransformer();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	
	private XMLMessageCreator() {
		
	}
	
	public static XMLMessageCreator getInstance() {
		return obj;
	}
	
	public void setId(long id) {
		document.getElementsByTagName("id").item(0).setTextContent(id+"");;
	}
	
	public String getAckMessage() {
		
		String res = null;
		try {
			//transform = TransformerFactory.newInstance().newTransformer();
			transform.transform(new DOMSource(document), result);
			res = writer.toString();
			writer = new StringWriter();
			result = new StreamResult(writer);
			
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public String getNewMessageWithNewNode(Document document,String name ,String value) {
		String res = null;
		Element element = document.getDocumentElement();
		Node node = document.createElement(name);
		element.appendChild(node);
		document.getElementsByTagName(name).item(0).setTextContent(value);
		try {
			transform.transform(new DOMSource(document), result);
			res = writer.toString();
			writer = new StringWriter();
			result = new StreamResult(writer);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public Document getMessage() {
		return this.document;
	}
	

}
