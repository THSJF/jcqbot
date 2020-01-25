package com.meng.remote;

import java.io.*;
import java.net.*;
import java.util.*;

public class SendRunnable implements Runnable {

	private Socket socket;
	private BotDataPack dataToSend=null;

	public SendRunnable(Socket s) {
		socket = s;
	}

	@Override
	public void run() {
		try {
			OutputStream outputStream = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			while (!socket.isClosed()) {
				if (dataToSend != null) {
					oos.writeObject(dataToSend);
					oos.flush();
					dataToSend = null;
				}
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendData(BotDataPack bdp) {
		dataToSend = bdp;
	}
}
