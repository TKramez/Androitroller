package edu.cmich.cps396m.krame1tg.remoteserver;

public interface ConnectionListener {

	/**
	 * Is fired when a connection is made or closed.
	 * @param isConnected
	 */
	public void onConnectionChanged(boolean isConnected);
}