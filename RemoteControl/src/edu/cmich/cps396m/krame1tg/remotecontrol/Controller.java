package edu.cmich.cps396m.krame1tg.remotecontrol;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Controller extends ControllerActivity implements OnTouchListener {
	
	/**
	 * Used to provide tactile feedback on button press.
	 */
	private Vibrator vibrate;
	
	/**
	 * The ControlConfiguration selected by the user.
	 */
	private ControlConfiguration config;
	
	/**
	 * The service used to send messages to the server.
	 */
	private RemoteControlService service;
	
	/**
	 * Monitors the connection with the service to keep track of
	 * whether a reconnect is in order or now.
	 */
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = ((RemoteControlService.RemoteControlBinder)binder).getService();
			
			Intent i = getIntent();
			
			if (!service.isConnected()) {
				switchActivity(AddressSelection.class);
				
			} else if (i.hasExtra("config")) {
				config = (ControlConfiguration) i.getSerializableExtra("config");
				setUpButtons();
			} else {
				switchActivity(SelectConfiguration.class);
			}
		}
	};
	
	/**
	 * Gets the system vibrator for providing feedback on button press.
	 * Binds the service and sets touchlisteners for the buttons.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		
		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		bindService(new Intent(this, RemoteControlService.class), conn, Context.BIND_AUTO_CREATE);
		
		for (int k : CustomizeConfig.buttons) {
			((Button) findViewById(k)).setOnTouchListener(this);
		}
	}
	
	/**
	 * Assigns the buttons their transparency and text based on the selected configuration.
	 */
	private void setUpButtons() {
		RelativeLayout layout = (RelativeLayout)((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
		boolean transparent = config.isTransparent();
		for (int k = 0; k < layout.getChildCount(); k++) {
			View view = layout.getChildAt(k);
			if (view instanceof Button) {
				String key = config.getKeyText(view.getId());
				if (key == null) {
					config.reMap(view.getId(), "ESC");
					key = config.getKeyText(view.getId());
				}
				key = key.substring(0, Math.min(3, key.length()));
				key = pad(key, ' ', 3);
				if (key != null) {
					Button button = (Button) view;
					button.setText(key);
					if (transparent)
						button.setAlpha(.1F);
				}
			}
		}
	}
	
	/**
	 * Whether or not a button is currently pressed.
	 */
	private boolean down = false;
	
	/**
	 * Determines if a touch is within a button and if so
	 * sends the corresponding key code to the server.
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getActionMasked();
		Rect rect = new Rect();
		v.getHitRect(rect);
		final float x = event.getX() + rect.left, y = event.getY() + rect.top;

		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP || !rect.contains((int)x, (int)y)) {
			service.sendKey(config.getKey(v.getId()), RemoteControlService.KEY_RELEASE);
			down = false;
			return false;
		} else {
			if (!down && vibrate.hasVibrator()) {
				vibrate.vibrate(100);
				down = true;
			}
			service.sendKey(config.getKey(v.getId()), RemoteControlService.KEY_PRESS);
		}


		return true;
	}

	/**
	 * Pads a string at the front and pad to the provided lenght with the provided
	 * character.
	 * @param key String to pad
	 * @param padding Character to pad with
	 * @param k The desired length of the string
	 * @return The resulting String
	 */
	private String pad(String key, char padding, int k) {
		StringBuilder result = new StringBuilder(key);
		
		while (result.length() < k) {
			result.append(padding);
			if (result.length() < k)
				result.insert(0, padding);
		}
		
		return result.toString();		
	}
	
	/**
	 * Unbinds the service when the activity is destroyed.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
	}
}