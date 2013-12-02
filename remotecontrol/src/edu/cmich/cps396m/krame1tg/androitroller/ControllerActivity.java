package edu.cmich.cps396m.krame1tg.androitroller;

import android.app.Activity;
import android.content.Intent;

public abstract class ControllerActivity extends Activity {

	/**
	 * Switches the activity to the specified class or
	 * creates a new instance if it hasn't been started.
	 * @param clazz The class of the activity to switch to
	 */
	protected void switchActivity(Class<? extends Activity> clazz) {
		Intent i = new Intent(this, clazz);
		switchActivity(i);
	}
	
	/**
	 * Switches the activity to the specified intent or
	 * creates a new instance if it hasn't been starte.
	 * @param i The intent to switch to
	 */
	protected void switchActivity(Intent i) {
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
	}
	
	/**
	 * Switches the activity to the specified intent or
	 * creates a new instance if it hasn't been started.
	 * Switches for result.
	 * @param i Intent to switch to
	 * @param code The code to use
	 */
	protected void switchActivityForResult(Intent i, int code) {
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(i, code);
	}
}