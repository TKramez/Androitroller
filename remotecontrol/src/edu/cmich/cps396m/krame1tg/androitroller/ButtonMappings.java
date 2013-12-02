package edu.cmich.cps396m.krame1tg.androitroller;

import edu.cmich.cps396m.krame1tg.androitroller.R;

/**
 * This class is used to map the id of the buttons to their transparent
 * image as well as the associated edit text on the customize config activity.
 * @author Tyler Kramer
 *
 */
public class ButtonMappings {

	/**
	 * List of all available button mappings
	 */
	protected static final ButtonMappings[] buttons = {
		new ButtonMappings(R.id.btnUP, R.drawable.btn_tup, R.id.editUP),
		new ButtonMappings(R.id.btnDOWN, R.drawable.btn_tdown, R.id.editDOWN),
		new ButtonMappings(R.id.btnLEFT, R.drawable.btn_tleft, R.id.editLEFT),
		new ButtonMappings(R.id.btnRIGHT, R.drawable.btn_tright, R.id.editRIGHT),
		new ButtonMappings(R.id.btnA, R.drawable.btn_ta, R.id.editA),
		new ButtonMappings(R.id.btnB, R.drawable.btn_tb, R.id.editB),
		new ButtonMappings(R.id.btnX, R.drawable.btn_tx, R.id.editX),
		new ButtonMappings(R.id.btnY, R.drawable.btn_ty, R.id.editY),
		new ButtonMappings(R.id.F1, R.drawable.btn_tf1, R.id.editF1),
		new ButtonMappings(R.id.F2, R.drawable.btn_tf2, R.id.editF2),
		new ButtonMappings(R.id.F3, R.drawable.btn_tf3, R.id.editF3),
		new ButtonMappings(R.id.F4, R.drawable.btn_tf4, R.id.editF4),
		new ButtonMappings(R.id.F5, R.drawable.btn_tf5, R.id.editF5),
		new ButtonMappings(R.id.F6, R.drawable.btn_tf6, R.id.editF6),
		new ButtonMappings(R.id.F7, R.drawable.btn_tf7, R.id.editF7)
	};
	
	public final int button;
	public final int drawable;
	public final int editText;
	
	public ButtonMappings(int button, int drawable, int editText) {
		this.button = button;
		this.drawable = drawable;
		this.editText = editText;
	}
}