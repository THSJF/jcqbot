package com.meng.remote;
import com.meng.*;
import com.meng.tools.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;
import java.net.*;

public class SoftUpgradeServer extends WebSocketServer {

	public SoftUpgradeServer(InetSocketAddress in) {
		super(in);
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
		System.out.println(p2);
		CheckNewBean cnb=Autoreply.gson.fromJson(p2, CheckNewBean.class);
		EachSoftInfo esi=Autoreply.gson.fromJson(Tools.FileTool.readString(Autoreply.appDirectory + "/software/info.json"), SoftInfoBean.class).infos.get(cnb.packageName);
		if (esi == null || cnb.nowVersionCode >= esi.lastestVersionCode) {
			p1.send("");
			return;
		}
		File f=new File(Autoreply.appDirectory + "/software/" + cnb.packageName + ".apk");
		esi.lastestSize = (int)f.length();
		Iterator<SoftInfo> iterator=esi.infoList.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().versionCode > cnb.nowVersionCode) {
				break;
			}
			iterator.remove();
		}
		p1.send(Autoreply.gson.toJson(esi));
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		BotDataPack rec=BotDataPack.decode(message.array());
		if (rec.getOpCode() == BotDataPack.opGetApp) {
			String packageName=rec.readString();
			File app=new File(Autoreply.appDirectory + "/software/" + packageName + ".apk");
			BotDataPack toSend=BotDataPack.encode(BotDataPack.opGetApp);
			toSend.write(app);
			conn.send(toSend.getData());
		} else if (rec.getOpCode() == BotDataPack.opCrashLog) {
			File fc=new File(Autoreply.appDirectory + "/softlog/" + rec.readString() + "-" + rec.readInt() + ".log");
			rec.readFile(fc);
			conn.send(BotDataPack.encode(BotDataPack.opTextNotify).write("发送成功").getData());
		}
		super.onMessage(conn, message);
	}

	@Override
	public void onError(WebSocket p1, Exception p2) {
		p2.printStackTrace();
	}

	@Override
	public void onStart() {
		// TODO: Implement this method
	}

	public class SoftInfoBean {
		public HashMap<String,EachSoftInfo> infos=new HashMap<>();
	}

	public class EachSoftInfo {
		public String lastestVersionName;
		public int lastestVersionCode;
		public int lastestSize;
		public ArrayList<SoftInfo> infoList=new ArrayList<>();
	}

	public class SoftInfo {
		public String versionName;
		public int versionCode;
		public String versionDescribe;
	}

	public class CheckNewBean {
		public String packageName;
		public int nowVersionCode;
	}
}
