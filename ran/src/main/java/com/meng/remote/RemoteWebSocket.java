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
	public static BotMsgInfo botInfoBean=new BotMsgInfo();
	public RemoteWebSocket() {
		super(new InetSocketAddress(8888));
		Autoreply.instence.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					while (true) {
						msgPack = BotDataPack.encode(BotDataPack.onGroupMsg);
						try {
							Thread.sleep(1000);
							broadcast(msgPack.getData());
							BotDataPack bbbbb=BotDataPack.encode(BotDataPack.onPerSecMsgInfo);
							bbbbb.write(botInfoBean.sendTo).write(botInfoBean.recFrom).write(botInfoBean.msgPerSec).write(botInfoBean.msgCmdPerSec).write(botInfoBean.msgSendPerSec);
							broadcast(bbbbb.getData());
							botInfoBean.reset();
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
				Autoreply.CQ.setGroupBan(rec.readLong(), rec.readLong(), rec.readLong());
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
				Member m=Autoreply.instence.CQ.getGroupMemberInfo(rec.readLong(), rec.readLong());
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
				Autoreply.CQ.setFriendAddRequest(rec.readString(), rec.readInt(), rec.readString());
				break;
			case BotDataPack.opGroupMemberList:
				toSend = BotDataPack.encode(rec.getOpCode());
				List<Member> ml=Autoreply.CQ.getGroupMemberList(rec.readLong());
				for (Member mlm:ml) {
					toSend.
						write(mlm.getGroupId()).
						write(mlm.getQqId()).
						write(mlm.getNick()).
						write(mlm.getCard()).
						write(mlm.getGender()).
						write(mlm.getAge()).
						write(mlm.getArea()).
						write(mlm.getAddTime().getTime()).
						write(mlm.getLastTime().getTime()).
						write(mlm.getLevelName()).
						write(mlm.getAuthority()).
						write(mlm.getTitle()).
						write(mlm.getTitleExpire().getTime()).
						write(mlm.isBad()).
						write(mlm.isModifyCard());
				}
				break;
			case BotDataPack.opGroupList:
				toSend = BotDataPack.encode(rec.getOpCode());
				List<Group> gl=Autoreply.CQ.getGroupList();
				for (Group g:gl) {
					toSend.write(g.getId()).write(g.getName());
				}
				break;
			case BotDataPack.getConfig:
				toSend = BotDataPack.encode(rec.getOpCode());
				toSend.write(Autoreply.gson.toJson(Autoreply.instence.configManager.configJavaBean));
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
