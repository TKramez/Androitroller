package edu.cmich.cps396m.krame1tg.remotecontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectConfiguration extends ControllerActivity {

	/**
	 * List of configurations created by the user.
	 */
	private List<ControlConfiguration> configs;
	
	/**
	 * Adapter that holds the configurations for display.
	 */
	private ArrayAdapter<ControlConfiguration> adapter;
	
	/**
	 * Where the configurations are stored.
	 */
	private String fileName;
	
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
		
		fileName = getFilesDir().getAbsolutePath() + File.separator + "configs";
		configs = new ArrayList<ControlConfiguration>();
		adapter = new ArrayAdapter<ControlConfiguration>(this, android.R.layout.simple_list_item_1, configs);
		
		final ListView lv = (ListView) findViewById(R.id.configListView);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				final ControlConfiguration config = configs.get(arg2);
				AlertDialog dialog = new AlertDialog.Builder(SelectConfiguration.this)
										.setTitle(config.getName())
										.setCancelable(true)
										.setItems(new CharSequence[] {"Play", "Edit", "Delete"}, new OnClickListener() {

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
												case 2: // Delete
													configs.remove(arg2);
													adapter.notifyDataSetChanged();
													break;
												}
											}})
										.create();
				dialog.show();
			}
		});
		
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
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
		readConfigs();
	}
	
	/**
	 * Saves the configurations to the file.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		saveConfigs();
	}
	
	/**
	 * Reads the configurations asynchronously from the file.
	 */
	private void readConfigs() {
		configs.clear();
		new AsyncTask<Void, Void, List<ControlConfiguration>>() {

			@Override
			protected List<ControlConfiguration> doInBackground(Void... params) {
				List<ControlConfiguration> list = new ArrayList<ControlConfiguration>();
				try {
				ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(fileName)));
				int num = input.readInt();
				for (int k = 0; k < num; k++) {
					ControlConfiguration config = (ControlConfiguration) input.readObject();
					list.add(config);
				}
				input.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (StreamCorruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return list;
			}
			
			protected void onPostExecute(List<ControlConfiguration> result) {
				for (ControlConfiguration config : result) {
					configs.add(config);
				}
				adapter.notifyDataSetChanged();
			};
		}.execute();
	}
	
	/**
	 * Writes the configurations to the file asynchronously.
	 */
	private void saveConfigs() {
		new AsyncTask<ControlConfiguration, Void, Void>() {

			@Override
			protected Void doInBackground(ControlConfiguration... params) {
				try {
					ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(new File(fileName)));
					writer.writeInt(params.length);
					for (int k = 0; k < params.length; k++) {
						writer.writeObject(params[k]);
					}
					writer.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute(configs.toArray(new ControlConfiguration[configs.size()]));
	}
	
	/**
	 * Recieves the result of the CustomizeConfig activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			if (requestCode == selected) {
				ControlConfiguration config = (ControlConfiguration) data.getSerializableExtra("config");
				configs.remove(selected);
				configs.add(selected, config);
				adapter.notifyDataSetChanged();
				saveConfigs();
			}
		}
	}
}