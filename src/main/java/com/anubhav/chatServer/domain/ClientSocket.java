package com.anubhav.chatServer.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * ServerSocket represents an active connection between client and server
 */

public class ClientSocket {
	
	private Socket socket;
	private Stream stream;
	
	public ClientSocket(Socket socket) throws IOException {
		this.socket = socket;
		stream = new Stream(socket.getInputStream() , socket.getOutputStream());
	}

	public Socket getSocket() {
		return socket;
	}
	
	public BufferedReader getInputReader() {
		return stream.getIn();
	}
	
	public PrintWriter getOutputWriter() {
		return stream.getOut();
	}
	
	public void close() throws IOException {
		stream.getIn().close();
		stream.getOut().close();
		socket.close();
	}
}
