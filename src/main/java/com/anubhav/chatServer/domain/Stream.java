package com.anubhav.chatServer.domain;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/*
 * contains input and output stream
 */
public class Stream {
	private BufferedReader in;
	private PrintWriter out;
	
	public Stream(InputStream in , OutputStream out) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new PrintWriter(out,true);  //autoflush is on
	}

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}
	
	

}
