package com.meng.remote;

import java.io.*;
import java.net.*;

public class ReceiveRunnable implements Runnable {
	private Socket socket;
	private SendRunnable sendRunnable;

	private BotDataPack dataRec;
	public ReceiveRunnable(Socket s, SendRunnable sr) {
		socket = s;
		sendRunnable = sr;
	}

	@Override
	public void run() {
		try {
			InputStream inputStream = socket.getInputStream();
			ObjectInputStream dataInputStream = new ObjectInputStream(inputStream);
			while (!socket.isClosed()) {
				dataRec = (BotDataPack) dataInputStream.readObject();
				if (dataRec != null) {
					System.out.println("服务器读取客户端的：" + dataRec.toString());
				}
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
