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
	 * List of all available textboxes.
	 */
	private static final int[] edits = {
		R.id.editUP,
		R.id.editDOWN,
		R.id.editLEFT,
		R.id.editRIGHT,
		R.id.editA,
		R.id.editB,
		R.id.editX,
		R.id.editY,
		R.id.editF1,
		R.id.editF2,
		R.id.editF3,
		R.id.editF4,
		R.id.editF5,
		R.id.editF6,
		R.id.editF7
	};
	
	/**
	 * List of all available buttons.
	 */
	protected static final int[] buttons = {
		R.id.btnUP,
		R.id.btnDOWN,
		R.id.btnLEFT,
		R.id.btnRIGHT,
		R.id.btnA,
		R.id.btnB,
		R.id.btnX,
		R.id.btnY,
		R.id.F1,
		R.id.F2,
		R.id.F3,
		R.id.F4,
		R.id.F5,
		R.id.F6,
		R.id.F7
	};
	
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
		
		for (int k = 0; k < edits.length; k++) {
			((EditText) findViewById(edits[k])).setText(config.getKeyText(buttons[k]));
		}
		
		((CheckBox) findViewById(R.id.transparent)).setChecked(config.isTransparent());
	}
	
	/**
	 * Checks if all the fields are valid.
	 * @return Whether all the fields are valid.
	 */
	private boolean areFieldsValid() {
		boolean result = true;
		
		for (int k = 0; k < edits.length; k++) {
			result &= isValid((EditText) findViewById(edits[k]));
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
		for (int k = 0; k < edits.length; k++) {
			config.remap(buttons[k], ((EditText) findViewById(edits[k])).getText().toString());
		}
	}
}