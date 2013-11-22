package edu.cmich.cps396m.krame1tg.androitroller;

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

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

public class ControlConfigAdapter extends ArrayAdapter<ControlConfiguration> {

	private String fileName;
	private List<ControlConfiguration> configs;
	
	public ControlConfigAdapter(Context context, int resource, List<ControlConfiguration> configs, String fileName) {
		super(context, resource, configs);
		
		this.fileName = fileName;
		this.configs = configs;
		readConfigs();
		
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		
		saveConfigs();
	}
	
	/**
	 * Reads the configurations asynchronously from the file.
	 */
	protected void readConfigs() {
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
				configs.clear();
				for (ControlConfiguration config : result) {
					configs.add(config);
				}
				ControlConfigAdapter.this.notifyDataSetChanged();
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
}