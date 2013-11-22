package edu.cmich.cps396m.krame1tg.androitroller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.cmich.cps396m.krame1tg.androitroller.R;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SelectConfiguration extends ControllerActivity {

	/**
	 * List of configurations created by the user.
	 */
	private List<ControlConfiguration> configs;
	
	/**
	 * Adapter that holds the configurations for display.
	 */
	private ControlConfigAdapter adapter;
	
	/**
	 * Position of the currently selected configuration.
	 */
	private int selected = -1;
	
	/**
	 * Loads the configs into memory and into the listview.
	 * Sets the clicklistener for the listview.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_configuration);
		
		String fileName = getFilesDir().getAbsolutePath() + File.separator + "configs";
		configs = new ArrayList<ControlConfiguration>();
		adapter = new ControlConfigAdapter(this, android.R.layout.simple_list_item_1, configs, fileName);
		
		final ListView lv = (ListView) findViewById(R.id.configListView);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				final ControlConfiguration config = configs.get(arg2);
				AlertDialog dialog = new AlertDialog.Builder(SelectConfiguration.this)
										.setTitle(config.getName())
										.setCancelable(true)
										.setItems(new CharSequence[] {"Play", "Edit", "Move Buttons","Delete"}, new OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,	int which) {
												switch (which) {
												case 0: // Play
													if (config != null) {
														Intent i = new Intent(SelectConfiguration.this, Controller.class);
														i.putExtra("config", config);
														switchActivity(i);
													}
													break;
												case 1: // Edit
													if (config != null) {
														selected = arg2;
														Intent i = new Intent(SelectConfiguration.this, CustomizeConfig.class);
														i.putExtra("config", config);
														switchActivityForResult(i, selected);
													}
													break;
												case 2: // Move Buttons
													if (config != null) {
														selected = arg2;
														Intent intent = new Intent(SelectConfiguration.this, Controller.class);
														intent.putExtra("config", config);
														intent.putExtra("customize", true);
														switchActivityForResult(intent, selected);
													}
													break;
												case 3: // Delete
													configs.remove(arg2);
													adapter.notifyDataSetChanged();
													break;
												}
											}})
										.create();
				dialog.show();
			}
		});
	}
	
	/**
	 * Receives the result of the CustomizeConfig activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if (resultCode == RESULT_OK) {
			if (requestCode == selected) {
				ControlConfiguration config = (ControlConfiguration) data.getSerializableExtra("config");
				configs.remove(selected);
				configs.add(selected, config);
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * Controls all the click of the activity.
	 * If new config is clicked it creates a default configuration.
	 * If connect is click it switches to the AddressSelection activity.
	 * @param v
	 */
	public void newConfigClick(View v) {
		if (v.getId() == R.id.btn_NewConfig) {
			configs.add(new ControlConfiguration("Default"));
			adapter.notifyDataSetChanged();
		} else if (v.getId() == R.id.connect) {
			switchActivity(AddressSelection.class);
		}
	}

	/**
	 * Loads the configurations from the file.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		adapter.readConfigs();
	}
}