package edu.cmich.cps396m.krame1tg.androitroller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class RemoteControlService extends Service {

	/**
	 * Code to tell the server to press the key down
	 */
	public static final int KEY_PRESS = 0;
	
	/**
	 * Code to tell the server to release the key
	 */
	public static final int KEY_RELEASE = 1;
	
	/**
	 * The socket used for communication.
	 */
	private Socket sock;
	
	/**
	 * The stream used to send data to the server.
	 */
	private DataOutputStream output;
	
	/**
	 * The binder used to bind to the service.
	 */
	private IBinder binder = new RemoteControlBinder();
	
	/**
	 * Checks whether or not the service is connected to the server.	
	 * @return Whether or not the service is connected. Always returns true on emulator.
	 */
	public boolean isConnected() {
		//Log.e("TK", "isConnected always returns true."); return true;
		return sock != null;
	}
	
	/**
	 * Disconnects the service from the server.
	 */
	public void disconnect() {
		if (sock != null) {
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sock = null;
		}
	}
	
	/**
	 * Disconnects the service when the service is destroyed.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		disconnect();
	}
	
	/**
	 * Sends a key code to the server.
	 * @param key The key to send
	 * @param action The action code to send.
	 */
	public void sendKey(int key, int action) {
		try {
			output.writeInt(key);
			output.writeInt(action);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the socket of the service.
	 * @param sock The socket to use
	 */
	public void setSocket(Socket sock) {
		if (this.output != null) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			output = null;
		}
		if (this.sock != null) {
			try {
				this.sock.close();
				this.sock = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.sock = sock;
		try {
			output = new DataOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public class RemoteControlBinder extends Binder {
		
		public RemoteControlService getService() {
			return RemoteControlService.this;
		}
	}
}