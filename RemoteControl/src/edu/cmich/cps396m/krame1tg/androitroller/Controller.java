package edu.cmich.cps396m.krame1tg.androitroller;

import java.io.File;
import java.util.HashSet;

import edu.cmich.cps396m.krame1tg.androitroller.ControlConfiguration.MappingAndLocation;
import edu.cmich.cps396m.krame1tg.androitroller.R;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Controller extends ControllerActivity implements OnTouchListener {
	
	/**
	 * Used to provide tactile feedback on button press.
	 */
	private Vibrator vibrate;
	
	private HashSet<Integer> hasMoved = new HashSet<Integer>();
	
	/**
	 * The ControlConfiguration selected by the user.
	 */
	private ControlConfiguration config;
	
	/**
	 * The service used to send messages to the server.
	 */
	private RemoteControlService service;
	
	private boolean isCustomize;
	
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
			
			if (!service.isConnected()) {
				Toast.makeText(Controller.this, "Not connected to server.", Toast.LENGTH_LONG).show();
				setResult(RESULT_CANCELED);
				finish();
			} 
		}
	};
	
	/**
	 * Gets the system vibrator for providing feedback on button press.
	 * Binds the service and sets touch listeners for the buttons.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		
		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		Intent intent = getIntent();
		
		isCustomize = intent.getBooleanExtra("customize", false);
		
		if (intent.hasExtra("config")) {
			config = (ControlConfiguration) intent.getSerializableExtra("config");
			setUpButtons();
		} else {
			Toast.makeText(this, "No configuration selected.", Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
			finish();
		}

		if (!isCustomize) {
			bindService(new Intent(this, RemoteControlService.class), conn, Context.BIND_AUTO_CREATE);
		} else {
			findViewById(R.id.saveController).setVisibility(View.VISIBLE);
			findViewById(R.id.resetController).setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Assigns the buttons their transparency based on the selected configuration.
	 */
	private void setUpButtons(boolean reset) {
		if (!reset)
			setBackground();

		boolean transparent = config.isTransparent();
		for (ButtonMappings map : ButtonMappings.buttons) {
			View button = findViewById(map.button);
			MappingAndLocation mapping = config.getMapping(button.getId());
			String key = config.getKeyText(button.getId());
			if (key == null) {
				button.setVisibility(View.INVISIBLE);
			} else {
				if (reset || mapping.isLocationSet()) {
					Log.d("TK", mapping.getX() + ", " + mapping.getY());
					button.setTranslationX(mapping.getX());
					button.setTranslationY(mapping.getY());
					Log.d("TK", button.getX() + ", " + button.getY());
				}
				if (transparent) {
					button.setBackgroundResource(map.drawable);
				}
				button.setOnTouchListener(this);
			}
		}
		TextView name = (TextView) findViewById(R.id.controllerName);
		name.setText(config.getName());
		if (transparent)
			name.setAlpha(.1F);
	}
	
	/**
	 * Assigns the buttons their transparency based on the selected configuration.
	 */
	private void setUpButtons() {
		setUpButtons(false);
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void setBackground() {
		if (config.getBackground() != null){
			RelativeLayout mainlayout = (RelativeLayout)findViewById(R.id.rl_main);
			File imgfile = new File(config.getBackground());

			if(imgfile.exists()){
				BitmapFactory.Options options = new BitmapFactory.Options();	
				Bitmap bitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath(), options);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					mainlayout.setBackground(new BitmapDrawable(this.getResources(), bitmap));
				} else {
					mainlayout.setBackgroundDrawable(new BitmapDrawable(this.getResources(), bitmap));
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
	public boolean onTouch(View button, MotionEvent event) {
		int action = event.getActionMasked();
		Rect rect = new Rect();
		button.getHitRect(rect);
		final float x = event.getX() + rect.left, y = event.getY() + rect.top;

		if (!isCustomize) {
			if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP || !rect.contains((int)x, (int)y)) {
				service.sendKey(config.getKey(button.getId()), RemoteControlService.KEY_RELEASE);
				down = false;
				return false;
			} else {
				if (!down && vibrate.hasVibrator()) {
					vibrate.vibrate(100);
					down = true;
				}
				service.sendKey(config.getKey(button.getId()), RemoteControlService.KEY_PRESS);
			}
		} else {
			if (rect.contains((int) x, (int)y)) {
				hasMoved.add(button.getId());
				button.setX(x);
				button.setY(y);
				button.invalidate();
			}
		}


		return true;
	}
	
	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.saveController:
			saveLocations();
			Intent intent = new Intent();
			intent.putExtra("config", config);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.resetController:
			for (ButtonMappings mapping : ButtonMappings.buttons) {
				config.getMapping(mapping.button).resetLocation();
			}
			setUpButtons(true);
			break;
		}
	}
	
	private void saveLocations() {
		for (ButtonMappings mapping : ButtonMappings.buttons) {
			if (hasMoved.contains(mapping.button)) {
				View button = findViewById(mapping.button);
				config.getMapping(mapping.button).setLocation(button.getTranslationX(), button.getTranslationY());
			}
		}
	}

	/**
	 * Unbinds the service when the activity is destroyed.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (service != null)
			unbindService(conn);
	}

}