package com.jam_int.jt316_jt318_m8;

import communication.ShoppingList;

public class SocketHandler {
	private static ShoppingList socket;

	public static synchronized ShoppingList getSocket(){

		return socket;
	}

	public static synchronized void setSocket(ShoppingList socket){
		SocketHandler.socket = socket;
	}
}
