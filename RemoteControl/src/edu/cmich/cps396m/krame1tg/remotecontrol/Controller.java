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
import android.widget.TextView;

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
			findViewById(k).setOnTouchListener(this);
		}
	}
	
	/**
	 * Assigns the buttons their transparency and text based on the selected configuration.
	 */
	private void setUpButtons() {
		boolean transparent = config.isTransparent();
		for (int k = 0; k < CustomizeConfig.buttons.length; k++) {
			View view = findViewById(CustomizeConfig.buttons[k]);
			String key = config.getKeyText(view.getId());
			if (key == null) {
				config.reMap(view.getId(), "ESC");
			}
			if (transparent)
				view.setAlpha(.1F);
		}
		((TextView) findViewById(R.id.controllerName)).setText(config.getName());
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
	 * Unbinds the service when the activity is destroyed.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
	}
}