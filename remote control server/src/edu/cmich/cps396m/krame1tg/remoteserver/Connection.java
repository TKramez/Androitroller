package edu.cmich.cps396m.krame1tg.remoteserver;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {

	/**
	 * The socket for this connection to use.
	 */
	private Socket socket;
	
	/**
	 * The input stream for this connection to listen to.
	 */
	private DataInputStream input;
	
	/**
	 * The robot to press the keys for this connection.
	 */
	private Robot robot;
	
	/**
	 * Creates a new Connection with the specified socket.
	 * @param socket The socket for this connection to use.
	 */
	public Connection(Socket socket) {
		RemoteServer.log.info("New Connection");
		this.socket = socket;
		try {
			input = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			this.close();
		}
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			this.close();
		}
	}
	
	/**
	 * Returns whether or not this connection is closed.
	 * @return
	 */
	public boolean isClosed() {
		return socket == null;
	}

	/**
	 * Closes this connection
	 */
	private void close() {
		try {
			if (input != null)
				input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		input = null;
		socket = null;
		robot = null;
		RemoteServer.log.info("Connection close.");
	}

	/**
	 * Starts the server listening for key presses.
	 */
	public void listen() {
		while (!this.isClosed()) {
			try {
				final int code = input.readInt();
				int action = input.readInt();

				Runnable runnable;

				switch (action) {
				case 0:
					runnable = new Runnable() {
						
						@Override
						public void run() {
							robot.keyPress(code);
						}
					};
					break;
				case 1:
					runnable = new Runnable() {
						
						@Override
						public void run() {
							robot.keyRelease(code);
						}
					};					
					break;
				default:
					continue;
				}
				
				new Thread(runnable).start();
			} catch (IOException e) {
				this.close();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
}