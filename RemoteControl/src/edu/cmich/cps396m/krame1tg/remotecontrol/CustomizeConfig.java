package edu.cmich.cps396m.krame1tg.remotecontrol;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class CustomizeConfig extends ControllerActivity {

	/**
	 * The currently selected ControlConfiguration
	 */
	private ControlConfiguration config;
	
	/**
	 * Gets the intent and returns if there was no config in it
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customize_config);
		
		Intent i = getIntent();
		
		if (i == null || !i.hasExtra("config"))
			finish();
		
		
		config = (ControlConfiguration) i.getSerializableExtra("config");
		initFields();
	}
	
	/**
	 * Initializes the textboxes to the current values in the config.
	 */
	private void initFields() {
		((EditText) findViewById(R.id.configName)).setText(config.getName());
		
		for (ButtonMappings map : ButtonMappings.buttons) {
			((EditText) findViewById(map.editText)).setText(config.getKeyText(map.button));
		}
		
		((CheckBox) findViewById(R.id.transparent)).setChecked(config.isTransparent());
	}
	
	/**
	 * Checks if all the fields are valid.
	 * @return Whether all the fields are valid.
	 */
	private boolean areFieldsValid() {
		boolean result = true;
		
		for (ButtonMappings map : ButtonMappings.buttons) {
			result &= isValid((EditText) findViewById(map.editText));
		}
		
		return result;
	}
	
	/**
	 * Checks if a provided textbox is valid.
	 * @param text The textbox to check.
	 * @return Whether or not it is valid.
	 */
	private boolean isValid(EditText text) {
		boolean result = false;
		text.setText(text.getText().toString().trim());
		if (config.validateKey(text.getText().toString())) {
			result =  true;
			text.setTextColor(Color.BLACK);
		} else {
			result = false;
			text.setTextColor(Color.RED);
		}

		return result;
	}
	
	/**
	 * Handles all the clicks of the activity.
	 * Saves and returns if save is clicked.
	 * Displays a list of valid codes if codes is clicked.
	 * @param v The button that was clicked.
	 */
	public void btnClick(View v) {
		if (v.getId() == R.id.save) {
			if (!areFieldsValid()) {
				Toast.makeText(this, "One or more fields contains an invalid code.", Toast.LENGTH_LONG).show();
				return;
			}
			remapConfig();
			config.setTransparent(((CheckBox) findViewById(R.id.transparent)).isChecked());
			Intent i = getIntent();
			EditText text = (EditText) findViewById(R.id.configName);
			config.setName(text.getText().toString());
			i.putExtra("config", config);
			setResult(RESULT_OK, i);
			finish();
		} else if (v.getId() == R.id.codes) {
			AlertDialog dialog = new AlertDialog.Builder(this)
								 .setCancelable(true)
								 .setTitle("Key Codes")
								 .setItems(ControlConfiguration.getValidKeyCodes(), null)
								 .create();
			dialog.show();
		}
	}

	/**
	 * Saves the new values to the config.
	 */
	private void remapConfig() {
		for (ButtonMappings map : ButtonMappings.buttons) {
			config.remap(map.button, ((EditText) findViewById(map.editText)).getText().toString());
		}
	}
}