package edu.cmich.cps396m.krame1tg.remoteserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

	/**
	 * The ServerSocket to accept connections with.
	 */
	private ServerSocket serv;
	
	/**
	 * The listener to notify of a connection change.
	 */
	private ConnectionListener listener;
	
	/**
	 * The Connection for the server to use.
	 */
	private Connection con;
	
	/**
	 * Creates a new server.
	 */
	public Server() {
		try {
			RemoteServer.log.info("Server created.");
			serv = new ServerSocket(5000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs the server in another thread.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Socket sock = serv.accept();
				con = new Connection(sock);
				if (listener != null)
					listener.onConnectionChanged(true);
				con.listen();
				if (listener != null)
					listener.onConnectionChanged(false);
			} catch (IOException e) {
				e.printStackTrace();
				this.close();
			}
		}
	}
	
	/**
	 * Sets the listener of this Server to the specified listener.
	 * @param listener
	 */
	public void setListener(ConnectionListener listener) {
		this.listener = listener;
	}

	public void close() {
		if (serv != null) {
			try {
				serv.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		RemoteServer.log.info("Server closed.");
	}
}