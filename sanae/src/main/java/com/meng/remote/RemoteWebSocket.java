package com.meng.remote;
import org.java_websocket.server.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import java.nio.*;
import com.meng.*;
import java.net.*;
import com.sobte.cqp.jcq.entity.*;
import java.util.*;
import com.meng.config.*;

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
		BotDataPack rec=BotDataPack.decode(message.array());
		BotDataPack toSend=null;
		switch (rec.getOpCode()) {
			case BotDataPack.opLoginQQ:
				toSend = BotDataPack.encode(rec.getOpCode());
				toSend.write(Autoreply.CQ.getLoginQQ());
				break;
			case BotDataPack.opLoginNick:
				toSend = BotDataPack.encode(rec.getOpCode());
				toSend.write(Autoreply.CQ.getLoginNick());
				break;
			case BotDataPack.opPrivateMsg:
				toSend = BotDataPack.encode(rec.getOpCode());
				toSend.write(Autoreply.sendMessage(0, rec.readLong(), rec.readString()));
				break;
			case BotDataPack.opGroupMsg:
				toSend = BotDataPack.encode(rec.getOpCode());
				toSend.write(Autoreply.sendMessage(rec.readLong(), 0, rec.readString()));
				break;
			case BotDataPack.opDiscussMsg:
				//toSend = BotDataPack.encode(botDataPack.getOpCode());
				break;
			case BotDataPack.opDeleteMsg:
				Autoreply.CQ.deleteMsg(rec.readInt());
				break;
			case BotDataPack.opSendLike:
				Autoreply.CQ.sendLikeV2(rec.readLong(), rec.readInt());
				break;
			case BotDataPack.opCookies:
				toSend = BotDataPack.encode(rec.getOpCode());
				toSend .write(Autoreply.CQ.getCookies());
				break;
			case BotDataPack.opCsrfToken:
				toSend = BotDataPack.encode(rec.getOpCode());
				toSend.write(Autoreply.CQ.getCsrfToken());
				break;
			case BotDataPack.opRecord:
				//	toSend = BotDataPack.encode(rec.getOpCode());
				break;
			case BotDataPack.opGroupKick:
				Autoreply.CQ.setGroupKick(rec.readLong(), rec.readLong(), rec.readBoolean());
				break;
			case BotDataPack.opGroupBan:
				Autoreply.CQ.setGroupBan(rec.readLong(), rec.readLong(), rec.readInt());
				break;
			case BotDataPack.opGroupAdmin:
				Autoreply.CQ.setGroupAdmin(rec.readLong(), rec.readLong(), rec.readBoolean());
				break;
			case BotDataPack.opGroupWholeBan:
				Autoreply.CQ.setGroupWholeBan(rec.readLong(), rec.readBoolean());
				break;
			case BotDataPack.opGroupAnonymousBan:
				Autoreply.CQ.setGroupAnonymousBan(rec.readLong(), rec.readString(), rec.readLong());
				break;
			case BotDataPack.opGroupAnonymous:
				Autoreply.CQ.setGroupAnonymous(rec.readLong(), rec.readBoolean());
				break;
			case BotDataPack.opGroupCard:
				Autoreply.CQ.setGroupCard(rec.readLong(), rec.readLong(), rec.readString());
				break;
			case BotDataPack.opGroupLeave:
				Autoreply.CQ.setGroupLeave(rec.readLong(), rec.readBoolean());
				break;
			case BotDataPack.opGroupSpecialTitle:
				Autoreply.CQ.setGroupSpecialTitle(rec.readLong(), rec.readLong(), rec.readString(), rec.readLong());
				break;
			case BotDataPack.opGroupMemberInfo:
				toSend = BotDataPack.encode(rec.getOpCode());
				Member m=Autoreply.ins.CQ.getGroupMemberInfo(rec.readLong(), rec.readLong());
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
			case BotDataPack.opDiscussLeave:
				toSend = BotDataPack.encode(rec.getOpCode());
				break;
			case BotDataPack.opFriendAddRequest:
				toSend = BotDataPack.encode(rec.getOpCode());
				break;
			case BotDataPack.opGroupMemberList:
				toSend = BotDataPack.encode(rec.getOpCode());
				break;
			case BotDataPack.opGroupList:
				toSend = BotDataPack.encode(rec.getOpCode());
				break;
			case BotDataPack.getConfig:
				toSend = BotDataPack.encode(rec.getOpCode());
				toSend.write(Autoreply.gson.toJson(ConfigManager.instence.RanConfig));
				break;
		/*	case BotDataPack.opGroupInfo:
				toSend = BotDataPack.encode(rec.getOpCode());
				ArrayList<Group> gl=(ArrayList<Group>) Autoreply.ins.CQ.getGroupList();
				long gid=rec.readLong();
				for (Group g:gl) {
					if (g.getId() == gid) {
						toSend.write(g.getId()).write(g.getName());
						break;
					}
				}
				break;
*/
		}
		if (toSend != null) {
			conn.send(toSend.getData());
		}
	}

	@Override
	public void onError(WebSocket p1, Exception p2) {
		// TODO: Implement this method
	}

	@Override
	public void onStart() {
		setConnectionLostTimeout(1800);
	}

	public void sendMsg(int type, long group, long qq, String msg, long msgId) {
		msgPack.write(type).write(group).write(qq).write(msg).write((int)msgId);
	}
}
