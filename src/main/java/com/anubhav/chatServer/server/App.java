package com.anubhav.chatServer.server;

import java.io.IOException;

import com.anubhav.chatServer.thread.MainServerThread;

/**
 * Main server class
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	try {
    		
			MainServerThread main = MainServerThread.getInstance(9999);
			main.startThread();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
