package edu.cmich.cps396m.krame1tg.androitroller;

import edu.cmich.cps396m.krame1tg.androitroller.R;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class CustomizeConfig extends ControllerActivity {

	private static int GALLERY_REQUEST = 5557;

	/**
	 * The currently selected ControlConfiguration
	 */
	private ControlConfiguration config;
	
	/**
	 * The path to the selected background.
	 */
	private String background;
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
		
		this.background = config.getBackground();
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
			config.setBackground(this.background);
			i.putExtra("config", config);
			setResult(RESULT_OK, i);
			finish();
		} else if (v.getId() == R.id.codes) {
			AlertDialog dialog = new AlertDialog.Builder(this)
								 .setCancelable(true)
								 .setTitle("Key Codes")
								 .setItems(ControlConfiguration.getValidKeyCodes().toArray(new String[ControlConfiguration.getValidKeyCodes().size()]), null)
								 .create();
			dialog.show();
		} else if (v.getId() == R.id.btn_bkgrd) {
			AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("Set Background")
				.setCancelable(true)
				.setItems(new CharSequence[] {"Select from Gallery", "Blank Background"}, new OnClickListener() {
					@Override
					public void onClick(
							DialogInterface dialog,	int which) {
						switch (which) {
						case 0: // Select new image from gallery
						    Intent intent = new Intent();	
						    intent.setType("image/*");
						    intent.setAction(Intent.ACTION_GET_CONTENT);
						    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
							break;
						case 1: // Set the background image to none
                            background = null;
							break;
						}
					}})
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

	@Override
	/**
	 * onActivityResult examines resultCode and requestCode and acts accordingly
	 * with received data.  The behavior for the requestCodes is:
	 *    
	 * GALLERY_REQUEST: sets the path stored in background to the path of the 
	 * selected image.
	 *     
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == GALLERY_REQUEST){
				Uri selectUri = data.getData();
				this.background = getRealPathFromURI(selectUri);
				Log.d("Androitroller", this.background);
			}
		}
	}

    /**
	 * CREDIT: from http://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
	 * AUTHOR USERNAME: PercyPercy.
	 * 
	 * getRealPathFromURI accepts a URI and converts it to a string representing the absolute filepath of
	 * the given URI.  The URI should refer to a image selected from the gallery.
	 */
	public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}