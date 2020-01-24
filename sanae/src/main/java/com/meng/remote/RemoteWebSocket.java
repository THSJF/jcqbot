package com.meng.remote;
import org.java_websocket.server.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import java.nio.*;
import com.meng.*;
import java.net.*;

public class RemoteWebSocket extends WebSocketServer {
	BotDataPack msgPack;

	public RemoteWebSocket(int port) {
		super(new InetSocketAddress(port));
		Autoreply.ins.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					msgPack = BotDataPack.encode(BotDataPack.opGroupMsg);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
					broadcast(msgPack.getData());
				}
			});
	}
	@Override
	public void onOpen(WebSocket p1, ClientHandshake p2) {
		// TODO: Implement this method
	}

	@Override
	public void onClose(WebSocket p1, int p2, String p3, boolean p4) {
		// TODO: Implement this method
	}

	@Override
	public void onMessage(WebSocket p1, String p2) {
		// TODO: Implement this method
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {

	}

	@Override
	public void onError(WebSocket p1, Exception p2) {
		// TODO: Implement this method
	}

	@Override
	public void onStart() {
		// TODO: Implement this method
	}
	public void sendMsg(int type, long group, long qq, String msg, long msgId) {
		msgPack.write(type).write(group).write(qq).write(msg).write((int)msgId);
	}
}
