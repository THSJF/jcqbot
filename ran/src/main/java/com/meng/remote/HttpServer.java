package com.meng.remote;

import java.io.*;
import java.net.*;
import com.meng.*;

public class HttpServer implements Runnable {

	public boolean onConnect=false;
	private SendRunnable sr;
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(7778);
			while (true) {
				while(onConnect){
					Thread.sleep(1000);
				}
				Socket s=serverSocket.accept();
				sr=new SendRunnable(s);
				Autoreply.instence.threadPool.execute(sr);
				Autoreply.instence.threadPool.execute(new ReceiveRunnable(s,sr));
				Thread.sleep(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
