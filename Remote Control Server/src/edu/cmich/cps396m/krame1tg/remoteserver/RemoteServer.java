package edu.cmich.cps396m.krame1tg.remoteserver;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class RemoteServer extends JFrame implements ConnectionListener {

	private static final long serialVersionUID = 5645923761446606416L;
	protected static final Logger log = Logger.getLogger("Remote Control Server");
	
	static {
		try {
			log.addHandler(new FileHandler("remote.log", 1024000, 1));
		} catch (SecurityException e) {
			log.log(Level.WARNING, "Error", e);
		} catch (IOException e) {
			log.log(Level.WARNING, "Error", e);
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				log.info("Starting");
				RemoteServer server = new RemoteServer();
				server.setVisible(true);
			}
		});
	}
	
	/**
	 * The current server instance used by the server.
	 */
	private Server server;
	
	/**
	 * The label displaying to the user whether or not there is a connection.
	 */
	private JLabel connection;
	
	/**
	 * Creates a new RemoteServer interface.
	 */
	public RemoteServer() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Remote Control Server");
		this.setLayout(new GridLayout(2, 1));
		this.setAlwaysOnTop(true);
		
		try {
			JLabel address = new JLabel();
			InetAddress inet = InetAddress.getLocalHost();
			address.setText(inet.getHostAddress());
			this.add(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		connection = new JLabel("No Connection");
		this.add(connection);
		this.pack();
		
		server = new Server();
		server.setListener(this);
		new Thread(server).start();
	}

	/**
	 * Changes the text on screen when a connection
	 * is created or closed.
	 */
	@Override
	public void onConnectionChanged(boolean isConnected) {
		if (isConnected) {
			connection.setText("Connection");
			log.info("User connected.");
		} else {
			connection.setText("No Connection");
			log.info("User disconnected.");
		}
		
		this.pack();
	}
}