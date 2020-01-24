package com.meng.remote;
import org.java_websocket.server.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import java.nio.*;
import com.meng.*;
import java.net.*;
import com.sobte.cqp.jcq.entity.*;
import java.util.*;

public class RemoteWebSocket extends WebSocketServer {
	BotDataPack msgPack;

	public RemoteWebSocket() {
		super(new InetSocketAddress(7777));
		Autoreply.ins.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					while (true) {
						msgPack = BotDataPack.encode(BotDataPack.onGroupMsg);
						try {
							Thread.sleep(1000);
							broadcast(msgPack.getData());
						} catch (Exception e) {}
					}
				}
			});
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
		BotDataPack botDataPack=BotDataPack.decode(message.array());
		BotDataPack toSend=null;
		switch(botDataPack.getOpCode()){
			case BotDataPack.opGroupMemberInfo:
				toSend=BotDataPack.encode(botDataPack.getOpCode());
				Member m=Autoreply.ins.CQ.getGroupMemberInfo(botDataPack.readLong(),botDataPack.readLong());
				toSend.
				write(m.getGroupId()).
				write(m.getQqId()).
				write(m.getNick()).
				write(m.getCard()).
				write(m.getGender()).
				write(m.getAge()).
				write(m.getArea()).
				write(m.getAddTime().getTime()).
				write(m.getLastTime().getTime()).
				write(m.getLevelName()).
				write(m.getAuthority()).
				write(m.getTitle()).
				write(m.getTitleExpire().getTime()).
				write(m.isBad()).
				write(m.isModifyCard());
				break;
			case BotDataPack.opGroupInfo:
				toSend=BotDataPack.encode(botDataPack.getOpCode());
				ArrayList<Group> gl=(ArrayList<Group>) Autoreply.ins.CQ.getGroupList();
				long gid=botDataPack.readLong();
				for(Group g:gl){
					if(g.getId()==gid){
						toSend.write(g.getId()).write(g.getName());
						break;
					}
				}
				break;
				
		}
		if(toSend!=null){
			conn.send(toSend.getData());
		}
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
