package com.meng.config.sanae;

import com.meng.*;
import com.meng.config.*;
import com.meng.dice.*;
import com.meng.modules.*;
import com.meng.remote.*;
import com.meng.tools.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;

public class SanaeServer extends WebSocketServer {

	public SanaeServer(int port) {
		super(new InetSocketAddress(port));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Autoreply.sendMessage(Autoreply.mainGroup, 0, "websocket连接");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Autoreply.sendMessage(Autoreply.mainGroup, 0, "websocket断开");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		++RemoteWebSocket.botInfoBean.recFrom;
	}
	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		++RemoteWebSocket.botInfoBean.recFrom;
		SanaeDataPack rsdp=SanaeDataPack.decode(message.array());
		SanaeDataPack sdp = SanaeDataPack.encode(rsdp);
		switch (rsdp.getOpCode()) {
			case SanaeDataPack.opConfigFile:
				sdp.write(Autoreply.gson.toJson(ConfigManager.instance.configJavaBean));
				break;
			case SanaeDataPack.opGameOverSpell:
				sdp.write(((MDiceImitate)ModuleManager.instance.getModule(MDiceImitate.class)).md5RanStr(rsdp.readLong(), MDiceImitate.spells));
				break;
			case SanaeDataPack.opGameOverPersent:
				String md5=Tools.Hash.MD5(String.valueOf(rsdp.readLong() + System.currentTimeMillis() / (24 * 60 * 60 * 1000)));
				char c=md5.charAt(0);
				if (c == '0') {
					sdp.write(9961);
				} else if (c == '1') {
					sdp.write(9760);
				} else {
					sdp.write(Integer.parseInt(md5.substring(26), 16) % 10001);
				}
				break;
			case SanaeDataPack.opIncSpeak:
				((MGroupCounter)ModuleManager.instance.getModule(MGroupCounter.class)).incSpeak(rsdp.readLong());
				((MUserCounter)ModuleManager.instance.getModule(MUserCounter.class)).incSpeak(rsdp.readLong());
				break;
			case SanaeDataPack.opIncRepeat:
				((MGroupCounter)ModuleManager.instance.getModule(MGroupCounter.class)).incFudu(rsdp.readLong());
				((MUserCounter)ModuleManager.instance.getModule(MUserCounter.class)).incFudu(rsdp.readLong());
				break;
			case SanaeDataPack.opIncRepeatStart:
				((MUserCounter)ModuleManager.instance.getModule(MUserCounter.class)).incFudujiguanjia(rsdp.readLong());
				break;
			case SanaeDataPack.opIncRepeatBreak:
				((MGroupCounter)ModuleManager.instance.getModule(MGroupCounter.class)).incRepeatBreaker(rsdp.readLong());
				((MUserCounter)ModuleManager.instance.getModule(MUserCounter.class)).incRepeatBreaker(rsdp.readLong());
				break;
			case SanaeDataPack.opSetNick:
				ConfigManager.instance.setNickName(rsdp.readLong(), rsdp.readString());
				break;
			case SanaeDataPack.opSeqContent:
				File jsonFile = new File(Autoreply.appDirectory + "seq.json");
				sdp.write(Tools.FileTool.readString(jsonFile));
				break;
			case SanaeDataPack.opAddBlack:
				ConfigManager.instance.addBlack(rsdp.readLong(), rsdp.readLong());
				Autoreply.sendMessage(Autoreply.mainGroup, 0, "添加成功");
				break;
		}
		if (sdp != null) {
			++RemoteWebSocket.botInfoBean.sendTo;
			conn.send(sdp.getData());
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	@Override
	public void onStart() {
		System.out.println("Server started!");
		setConnectionLostTimeout(100);
	}

	public void send(final SanaeDataPack sdp) {
		Autoreply.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					++RemoteWebSocket.botInfoBean.sendTo;
					broadcast(sdp.getData());
				}
			});
	}
}
