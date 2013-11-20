package edu.cmich.cps396m.krame1tg.androitroller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
	 * Binds the service and sets touch listeners for the buttons.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		
		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		bindService(new Intent(this, RemoteControlService.class), conn, Context.BIND_AUTO_CREATE);
		
		for (ButtonMappings map : ButtonMappings.buttons) {
			findViewById(map.button).setOnTouchListener(this);
		}
	}
	
	/**
	 * Assigns the buttons their transparency and text based on the selected configuration.
	 */
	private void setUpButtons() {
		RelativeLayout mainlayout = (RelativeLayout)findViewById(R.id.rl_main);
		if (config.getBackground() != null){
			File imgfile = new File(config.getBackground());
	
		    if(imgfile.exists()){
		        BitmapFactory.Options options = new BitmapFactory.Options();	
		        Bitmap bitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath(), options);
		        mainlayout.setBackground(new BitmapDrawable(this.getResources(), bitmap));
		    }
		}
		
		boolean transparent = config.isTransparent();
		for (ButtonMappings map : ButtonMappings.buttons) {
			View view = findViewById(map.button);
			MappingAndLocation mapping = config.getMapping(view.getId());
			String key = config.getKeyText(view.getId());
			if (key == null) {
				config.remap(view.getId(), "ESC");
			}
			if (mapping.isLocationSet()) {
				view.setX(mapping.getX());
				view.setY(mapping.getY());
			}
			if (transparent) {
				view.setBackgroundResource(map.drawable);
			}
		}
		TextView name = (TextView) findViewById(R.id.controllerName);
		name.setText(config.getName());
		if (transparent)
			name.setAlpha(.1F);
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