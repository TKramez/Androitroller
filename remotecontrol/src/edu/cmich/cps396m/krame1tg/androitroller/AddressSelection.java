package edu.cmich.cps396m.krame1tg.androitroller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.cmich.cps396m.krame1tg.androitroller.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddressSelection extends ControllerActivity {
	
	/**
	 * This is the service responsible for sending the keys to the server.
	 */
	private RemoteControlService service;
	
	/**
	 * The users currently selected control configuration, if any.
	 */
	private ControlConfiguration config;
	
	/**
	 * Monitors the connection with the service to keep track of
	 * whether a reconnect is in order or now.
	 */
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
			((Button) findViewById(R.id.disconnect)).setVisibility(View.INVISIBLE);
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = ((RemoteControlService.RemoteControlBinder) binder).getService();
			if (service.isConnected()) {
				((Button) findViewById(R.id.disconnect)).setVisibility(View.VISIBLE);
			}
		}
	};
	
	/**
	 * Binds the service to the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_selection);
		
		doBindService();
	}
	
	/**
	 * Loads the address stored in the preferences if there is one.
	 * Sets the visibility of the disconnect button and loads the config
	 * if one is provided.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = this.getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE);
		((EditText) findViewById(R.id.addressBox)).setText(prefs.getString("address", ""));
		
		if (service != null && service.isConnected()) {
			((Button) findViewById(R.id.disconnect)).setVisibility(View.VISIBLE);
		} else {
			((Button) findViewById(R.id.disconnect)).setVisibility(View.INVISIBLE);
		}
		
		Intent i = getIntent();
		if (i != null && i.hasExtra("config")) {
			config = (ControlConfiguration) i.getSerializableExtra("config");
		}
	}
	
	/**
	 * Saves the address to the preferences.
	 */
	@Override
	protected void onPause() {
		SharedPreferences prefs = this.getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putString("address", ((EditText) findViewById(R.id.addressBox)).getText().toString());
		edit.commit();
		
		super.onPause();
	}
	
	/**
	 * Unbinds from the service when the activity is destroyed.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}
	
	/**
	 * Binds the service to the activity.
	 */
	private void doBindService() {
		bindService(new Intent(this, RemoteControlService.class), conn, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * Unbinds the service from the activity
	 */
	private void doUnbindService() {
		unbindService(conn);
	}

	/**
	 * Handles all the button presses for the activity.
	 * If the submit button is clicked it attempts to connect to the server.
	 * If the disconnect button is click it attempts to disconnect from the server.
	 * If the select configuration button is click it switched to the SelectConfiguration
	 * activity without modifying the connection.
	 * @param v The button that was clicked.
	 */
	public void submitClick(View v) {
		if (v.getId() == R.id.btn_Submit) {
			EditText text = (EditText) findViewById(R.id.addressBox);
			
			Connection con = new Connection(new OnTaskComplete() {

				@Override
				public void onComplete(Socket sock) {
					if (sock != null) {
						((Button) findViewById(R.id.disconnect)).setVisibility(View.VISIBLE);
						Toast.makeText(AddressSelection.this, "Success", Toast.LENGTH_LONG).show();
						service.setSocket(sock);
						if (config == null) {
							switchActivity(SelectConfiguration.class);
						} else {
							switchActivity(Controller.class);
						}
					} else {
						((Button) findViewById(R.id.disconnect)).setVisibility(View.INVISIBLE);
					}
				}
			});
			con.execute(text.getText().toString());
		} else if (v.getId() == R.id.disconnect) {
			service.disconnect();
			((Button) findViewById(R.id.disconnect)).setVisibility(View.INVISIBLE);
		} else if (v.getId() == R.id.selectConfig) {
			switchActivity(SelectConfiguration.class);
		}
	}
	
	/**
	 * Interface that allows for a callback when the connection is modified.
	 * @author Tyler Kramer
	 *
	 */
	public interface OnTaskComplete {
		
		/**
		 * Is called when the connection is completed.
		 * @param sock
		 */
		void onComplete(Socket sock);
	}
	
	/**
	 * An AsyncTask that is used to attempt to connect to the specified address.
	 * @author Tyler Kramer
	 *
	 */
	private class Connection extends AsyncTask<String, String, Socket> {
		
		/**
		 * The callback for notifying the activity when the Socket is connected.
		 */
		private OnTaskComplete callback;
		
		/**
		 * Constructs the Connection with the specified callback
		 * @param callback The callback for when the Socket is connected.
		 */
		public Connection(OnTaskComplete callback) {
			this.callback = callback;
		}
		
		/**
		 * Attempts to connect the Application to the Server in the background.
		 * Routes errors through the publishProgress method for displaying in
		 * the main thread. Returns null on failure.
		 */
		@Override
		protected Socket doInBackground(String... params) {
			Socket sock = null;
			try {
				sock = new Socket();
				sock.connect(new InetSocketAddress(params[0], 5000), 5000);
			} catch (UnknownHostException e) {
				this.publishProgress("Could not connect to " + params[0]);
				sock = null;
			} catch (IOException e) {
				this.publishProgress("Connection failed. Maybe the server isn't started or is already connected to another device.");
				sock = null;
			}
			return sock;
		}
		
		/**
		 * Primarily used to report errors in connecting to the users.
		 */
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			Toast.makeText(AddressSelection.this, values[0], Toast.LENGTH_LONG).show();
		}
		
		/**
		 * Notifies the callback with the newly connected socket.
		 */
		@Override
		protected void onPostExecute(Socket result) {
			super.onPostExecute(result);
			if (callback != null)
				callback.onComplete(result);
		}
	}
}