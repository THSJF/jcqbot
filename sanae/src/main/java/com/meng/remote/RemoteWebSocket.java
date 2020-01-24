package com.meng.remote;
import org.java_websocket.server.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import java.nio.*;
import com.meng.*;
import java.net.*;

public class RemoteWebSocket extends WebSocketServer {
	BotDataPack msgPack;

	public RemoteWebSocket() {
		super(new InetSocketAddress(7777));
	}
	@Override
	public void onOpen(WebSocket p1, ClientHandshake p2) {
		System.out.println("remote connect");
	}

	@Override
	public void onClose(WebSocket p1, int p2, String p3, boolean p4) {
		System.out.println("remote disconnect");
	}

	@Override
	public void onMessage(WebSocket p1, String p2) {
		// TODO: Implement this method
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		Autoreply.ins.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					msgPack = BotDataPack.encode(BotDataPack.onGroupMsg);
					try {
						Thread.sleep(1000);
						broadcast(msgPack.getData());
					} catch (Exception e) {}
				}
			});
	}

	@Override
	public void onError(WebSocket p1, Exception p2) {
		// TODO: Implement this method
	}

	@Override
	public void onStart() {
		setConnectionLostTimeout(100);
	}

	public void sendMsg(int type, long group, long qq, String msg, long msgId) {
		msgPack.write(type).write(group).write(qq).write(msg).write((int)msgId);
	}
}
