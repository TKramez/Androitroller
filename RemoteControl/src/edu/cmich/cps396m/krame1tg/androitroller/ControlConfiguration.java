package edu.cmich.cps396m.krame1tg.androitroller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import edu.cmich.cps396m.krame1tg.androitroller.R;
import android.util.SparseArray;

public class ControlConfiguration implements Serializable {

	private static final long serialVersionUID = 1866258106502122172L;
	
	/**
	 * Used to get the key text for a key code.
	 */
	private static SparseArray<String> keyText = new SparseArray<String>();
	
	/**
	 * Used to map texts for keys to their codes.
	 */
	private static HashMap<String, Integer> keyMap = new HashMap<String, Integer>();
	
	static {
		initMaps();
	}

	/**
	 * Initializes the mappings of key codes to Strings.
	 * Only called once on first reference to the ControlConfiguration class.
	 */
	private static void initMaps() {
		keyMap.put("0", 48);
		keyMap.put("1", 49);
		keyMap.put("2", 50);
		keyMap.put("3", 51);
		keyMap.put("4", 52);
		keyMap.put("5", 53);
		keyMap.put("6", 54);
		keyMap.put("7", 55);
		keyMap.put("8", 56);
		keyMap.put("9", 57);
		keyMap.put("UP", 38);
		keyMap.put("DOWN", 40);
		keyMap.put("LEFT", 37);
		keyMap.put("RIGHT", 39);
		keyMap.put("A", 65);
		keyMap.put("B", 66);
		keyMap.put("C", 67);
		keyMap.put("D", 68);
		keyMap.put("E", 69);
		keyMap.put("F", 70);
		keyMap.put("G", 71);
		keyMap.put("H", 72);
		keyMap.put("I", 73);
		keyMap.put("J", 74);
		keyMap.put("K", 75);
		keyMap.put("L", 76);
		keyMap.put("M", 77);
		keyMap.put("N", 78);
		keyMap.put("O", 79);
		keyMap.put("P", 80);
		keyMap.put("Q", 81);
		keyMap.put("R", 82);
		keyMap.put("S", 83);
		keyMap.put("T", 84);
		keyMap.put("U", 85);
		keyMap.put("V", 86);
		keyMap.put("W", 87);
		keyMap.put("X", 88);
		keyMap.put("Y", 89);
		keyMap.put("Z", 90);
		keyMap.put("SPACE", 32);
		keyMap.put("ENTER", 10);
		keyMap.put("ESC", 27);
		keyMap.put("F1", 112);
		keyMap.put("F2", 113);
		keyMap.put("F3", 114);
		keyMap.put("F4", 115);
		keyMap.put("F5", 116);
		keyMap.put("F6", 117);
		keyMap.put("F7", 118);
		keyMap.put("F8", 119);
		keyMap.put("F9", 120);
		keyMap.put("F10", 121);
		keyMap.put("F11", 122);
		keyMap.put("F12", 123);
		
		for (String key : keyMap.keySet()) {
			keyText.put(keyMap.get(key), key);
		}
	}
	
	/**
	 * Gets all valid key code Strings
	 * @return array of key code Strings
	 */
	public static String[] getValidKeyCodes() {
		String[] keys = keyMap.keySet().toArray(new String[keyMap.keySet().size()]);
		Arrays.sort(keys);
		
		return keys;
	}

	/**
	 * Mapping of button ids to button locations and key mapping
	 */
	private HashMap<Integer, MappingAndLocation> map;
	
	/**
	 * Name of the ControlConfiguration
	 */
	private String name;
	
	/**
	 * Whether or not buttons are transparent in the Controller activity
	 */
	private boolean transparent;
	
	/**
	 * Creates a new ControlConfiguration with the specified name
	 * @param name The name of the new ControlConfiguration
	 */
	public ControlConfiguration(String name) {
		this(name, new HashMap<Integer, MappingAndLocation>());
		generateDefaultMap();
	}
	
	/**
	 * Creates a new ControlConfiguration with the specified name and mappings
	 * @param name The name of the new ControlConfiguration
	 * @param map The mappings the new configuration is to use
	 */
	public ControlConfiguration(String name, HashMap<Integer, MappingAndLocation> map) {
		this.name = name;
		this.map = map;
	}
	
	/**
	 * Sets the name of the ControlConfiguration
	 * @param name The new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name of the ControlConfiguration
	 * @return The name of the ControlConfiguration
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets whether or not the buttons should be transparent.
	 * @return Whether or not the buttons should be transparent
	 */
	public boolean isTransparent() {
		return transparent;
	}

	/**
	 * Sets whether or not the buttons should be transparent.
	 * @param transparent true if to transparent false otherwise.
	 */
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the key code mapped to the id
	 * @param k The id of the button to be looked up
	 * @return The key code mapped to the button
	 */
	public int getKey(int k) {
		if (map.containsKey(k))
			return map.get(k).getKey();
		else
			return -1;
	}
	
	/**
	 * The text for the key mapped to the button id
	 * @param k The id of the button to look up
	 * @return The text of the key mapped to the button id
	 */
	public String getKeyText(int k) {
		if (map.containsKey(k))
			return keyText.get(getKey(k), null);
		else
			return null;
	}
	
	/**
	 * Gets the location and key mapping associated with the specified button.
	 * @param id The id of the button to recieve the mapping for
	 * @return The mapping for the specified button
	 */
	public MappingAndLocation getMapping(int id) {
		if (map.containsKey(id))
			return map.get(id);
		else
			return null;
	}
	
	/**
	 * Generates the default ControlConfiguration
	 */
	private void generateDefaultMap() {
		map.put(R.id.btnUP, new MappingAndLocation(keyMap.get("UP")));
		map.put(R.id.btnDOWN, new MappingAndLocation(keyMap.get("DOWN")));
		map.put(R.id.btnRIGHT, new MappingAndLocation(keyMap.get("RIGHT")));
		map.put(R.id.btnLEFT, new MappingAndLocation(keyMap.get("LEFT")));
		map.put(R.id.btnA, new MappingAndLocation(keyMap.get("A")));
		map.put(R.id.btnB, new MappingAndLocation(keyMap.get("B")));
		map.put(R.id.btnX, new MappingAndLocation(keyMap.get("X")));
		map.put(R.id.btnY, new MappingAndLocation(keyMap.get("Y")));
		
		map.put(R.id.F1, new MappingAndLocation(keyMap.get("F1")));
		map.put(R.id.F2, new MappingAndLocation(keyMap.get("F2")));
		map.put(R.id.F3, new MappingAndLocation(keyMap.get("F3")));
		map.put(R.id.F4, new MappingAndLocation(keyMap.get("F4")));
		map.put(R.id.F5, new MappingAndLocation(keyMap.get("F5")));
		map.put(R.id.F6, new MappingAndLocation(keyMap.get("F6")));
		map.put(R.id.F7, new MappingAndLocation(keyMap.get("F7")));
		transparent = false;
	}
	
	/**
	 * Checks whether the key is a valid mapping for customization.
	 * @param key The string to check
	 * @return Whether or not the key is valid
	 */
	public boolean validateKey(String key) {
		return keyMap.containsKey(key);
	}
	
	/**
	 * Remaps the button to the new key.
	 * @param button The id of the button to be remapped.
	 * @param key The new key to remap to the button.
	 */
	public void remap(int button, String key) {
		MappingAndLocation loc = map.get(button);
		loc.setKey(keyMap.get(key));
	}
	
	/**
	 * A container for the mappings and locations of the buttons.
	 * @author Tyler Kramer
	 *
	 */
	public class MappingAndLocation implements Serializable {

		private static final long serialVersionUID = -8740769209778816736L;
		
		/**
		 * The key and x and y location.
		 */
		private int key, x, y;
		
		/**
		 * Creates a new MappingAndLocation for the specified key with default x and y
		 * @param key The key code
		 */
		public MappingAndLocation(int key) {
			this(key, -1, -1);
		}
		
		/**
		 * Creates a new MappingAndLocation for the specified key with the specified x and y.
		 * @param key The key code
		 * @param x The x position of the button
		 * @param y The y position of the button
		 */
		public MappingAndLocation(int key, int x, int y) {
			this.key = key;
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Checks whether the location is different from the default.
		 * @return Whether or not the button has been moved.
		 */
		public boolean isLocationSet() {
			return x != -1 && y != -1;
		}

		/**
		 * Sets the key to the specified key
		 * @param key The new key code to be used.
		 */
		public void setKey(int key) {
			this.key = key;
		}
		
		/**
		 * Gets the key assigned to this mapping.
		 * @return The key assigned
		 */
		public int getKey() {
			return key;
		}

		/**
		 * Gets the x location assigned to this mapping.
		 * @return The x location of this mapping
		 */
		public int getX() {
			return x;
		}
		
		/**
		 * Sets the x location assigned to this mapping
		 * @param x The new x location of this mapping
		 */
		public void setX(int x) {
			this.x = x;
		}

		/**
		 * Gets the y location assigned to this mapping.
		 * @return The y location of this mapping
		 */
		public int getY() {
			return y;
		}
		
		/**
		 * Sets the y location assigned to this mapping.
		 * @param y The new y location of this mapping
		 */
		public void setY(int y) {
			this.y = y;
		}
	}
}