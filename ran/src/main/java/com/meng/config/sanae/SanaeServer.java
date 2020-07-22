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
import com.meng.sjfmd.libs.*;

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
				sdp.write(GSON.toJson(ConfigManager.getConfigHolder()));
				break;
			case SanaeDataPack.opGameOverSpell:
				sdp.write(ModuleManager.getGroupModule(MDiceImitate.class).md5RanStr(rsdp.readLong(), MDiceImitate.spells));
				break;
			case SanaeDataPack.opGameOverPersent:
				String md5=Hash.getMd5Instance().calculate(String.valueOf(rsdp.readLong() + System.currentTimeMillis() / (24 * 60 * 60 * 1000)));
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
				ModuleManager.getGroupModule(MGroupCounter.class).incSpeak(rsdp.readLong());
				ModuleManager.getGroupModule(MUserCounter.class).incSpeak(rsdp.readLong());
				break;
			case SanaeDataPack.opIncRepeat:
				ModuleManager.getGroupModule(MGroupCounter.class).incFudu(rsdp.readLong());
				ModuleManager.getGroupModule(MUserCounter.class).incFudu(rsdp.readLong());
				break;
			case SanaeDataPack.opIncRepeatStart:
				ModuleManager.getGroupModule(MUserCounter.class).incFudujiguanjia(rsdp.readLong());
				break;
			case SanaeDataPack.opIncRepeatBreak:
				ModuleManager.getGroupModule(MGroupCounter.class).incRepeatBreaker(rsdp.readLong());
				ModuleManager.getGroupModule(MUserCounter.class).incRepeatBreaker(rsdp.readLong());
				break;
			case SanaeDataPack.opSetNick:
				ConfigManager.setNickName(rsdp.readLong(), rsdp.readString());
				break;
			case SanaeDataPack.opSeqContent:
				sdp.write(FileTool.readString(new File(Autoreply.appDirectory + "seq.json")));
				break;
			case SanaeDataPack.opAddBlack:
				ConfigManager.addBlack(rsdp.readLong(), rsdp.readLong());
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
		SJFExecutors.execute(new Runnable(){

				@Override
				public void run() {
					++RemoteWebSocket.botInfoBean.sendTo;
					broadcast(sdp.getData());
				}
			});
	}
}
