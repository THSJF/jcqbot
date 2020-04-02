package com.meng.config;

import com.meng.*;
import com.meng.bilibili.live.*;
import com.meng.config.javabeans.*;
import com.meng.modules.*;
import com.meng.remote.*;
import com.meng.tools.*;
import com.sobte.cqp.jcq.entity.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;

public class RitsukageServer extends WebSocketServer {

	public RitsukageServer(int port) {
		super(new InetSocketAddress(port));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		//	conn.send("Welcome to the server!"); //This method sends a message to the new client
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		//	broadcast(conn + " has left the room!");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		++RemoteWebSocket.botInfoBean.recFrom;
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		++RemoteWebSocket.botInfoBean.recFrom;
		RitsukageDataPack dp=RitsukageDataPack.decode(message.array());
		if (dp.getTarget() == ConfigManager.instance.configJavaBean.ogg) {
			oggProcess(conn, dp);
		}
	}

	private void oggProcess(WebSocket ogg, RitsukageDataPack recievedDataPack) {
		RitsukageDataPack dataToSend=null;
		switch (recievedDataPack.getOpCode()) {
			case RitsukageDataPack._0notification:
				break;
			case RitsukageDataPack._1verify:
				break;
			case RitsukageDataPack._2getLiveList:
				HashSet<PersonInfo> hashSet=new HashSet<>();
				for (LivePerson lp:Autoreply.instance.liveListener.livePersonMap.values()) {
					if (lp.lastStatus) {
						hashSet.add(ConfigManager.instance.getPersonInfoFromLiveId(Long.parseLong(lp.roomID)));
					}		
				}
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._3returnLiveList, recievedDataPack.getTimeStamp());
				dataToSend.writePersonSet(hashSet);
				break;
			case RitsukageDataPack._3returnLiveList:
				break;
			case RitsukageDataPack._4liveStart:
				break;
			case RitsukageDataPack._5liveStop:
				break;
			case RitsukageDataPack._6speakInLiveRoom:
				break;
			case RitsukageDataPack._7newVideo:
				break;
			case RitsukageDataPack._8newArtical:
				break;
			case RitsukageDataPack._9getPersonInfoByName:
				PersonInfo pi=ConfigManager.instance.getPersonInfoFromName(recievedDataPack.readString(1));
				if (pi != null) {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._13returnPersonInfo, recievedDataPack.getTimeStamp());
					dataToSend.write(pi);
				} else {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._0notification, recievedDataPack.getTimeStamp());
					dataToSend.write(1, "");
				}
				break;
			case RitsukageDataPack._10getPersonInfoByQQ:
				PersonInfo pi10=ConfigManager.instance.getPersonInfoFromQQ(recievedDataPack.readNum(1));
				if (pi10 != null) {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._13returnPersonInfo, recievedDataPack.getTimeStamp());
					dataToSend.write(pi10);
				} else {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._0notification, recievedDataPack.getTimeStamp());
					dataToSend.write(1, "");
				}
				break;
			case RitsukageDataPack._11getPersonInfoByBid:
				PersonInfo pi11=ConfigManager.instance.getPersonInfoFromBid(recievedDataPack.readNum(1));
				if (pi11 != null) {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._13returnPersonInfo, recievedDataPack.getTimeStamp());
					dataToSend.write(pi11);
				} else {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._0notification, recievedDataPack.getTimeStamp());
					dataToSend.write(1, "");
				}
				break;
			case RitsukageDataPack._12getPersonInfoByBiliLive:
				PersonInfo pi12=ConfigManager.instance.getPersonInfoFromLiveId(recievedDataPack.readNum(1));
				if (pi12 != null) {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._13returnPersonInfo, recievedDataPack.getTimeStamp());
					dataToSend.write(pi12);
				} else {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._0notification, recievedDataPack.getTimeStamp());
					dataToSend.write(1, "");
				}
				break;
			case RitsukageDataPack._13returnPersonInfo:
				break;
			case RitsukageDataPack._14coinsAdd:
				//dp=DataPack.encode(DataPack._14coinsAdd,dp.getTimeStamp());
				//dp.write(
				break;
			case RitsukageDataPack._15groupBan:
				Tools.CQ.ban(recievedDataPack.readNum(1), recievedDataPack.readNum(2), (int)recievedDataPack.readNum(3));
				dataToSend = RitsukageDataPack.encode((short)0, recievedDataPack.getTimeStamp());
				dataToSend.write(1, "禁言成功");
				break;
			case RitsukageDataPack._16groupKick:
				Autoreply.CQ.setGroupKick(recievedDataPack.readNum(1), recievedDataPack.readNum(2), recievedDataPack.readNum(3) == 1);
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._0notification, recievedDataPack.getTimeStamp());
				dataToSend.write(1, "踢出群成功");
				break;
			case RitsukageDataPack._17heartBeat:
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._0notification, recievedDataPack.getTimeStamp());
				dataToSend.write(1, "心跳收到");
				break;
			case RitsukageDataPack._18FindInAll:		
				long findqq=recievedDataPack.readNum(1);
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._19returnFind, recievedDataPack.getTimeStamp());
				List<Group> joinedGroups = Autoreply.CQ.getGroupList();
				HashSet<Long> qqInThis = new HashSet<>();
				for (Group group : joinedGroups) {
					if (group.getId() == 959615179L || group.getId() == 666247478L) {
						continue;
					}
					ArrayList<Member> members = (ArrayList<Member>) Autoreply.CQ.getGroupMemberList(group.getId());
					for (Member member : members) {
						if (member.getQqId() == findqq) {
							qqInThis.add(group.getId());
							break;
						}
					}
				}
				dataToSend.writeLongSet(qqInThis);
				break;
			case RitsukageDataPack._19returnFind:
				break;
			case RitsukageDataPack._20pic:
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._21returnPic, recievedDataPack.getTimeStamp());
				try { 
					File jpg=((MPicEdit)ModuleManager.instance.getModule(MPicEdit.class)).jingShenZhiZhuByAt(-1, -1, Autoreply.instance.CC.at(recievedDataPack.readNum(1)));
					long filelength = jpg.length();
					byte[] filecontent = new byte[(int) filelength];
					FileInputStream in = new FileInputStream(jpg);
					in.read(filecontent);
					in.close();
					dataToSend.writeData(filecontent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case RitsukageDataPack._21returnPic:
				break;
			case RitsukageDataPack._22pic2:
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._21returnPic, recievedDataPack.getTimeStamp());
				try { 
					File jpg=((MPicEdit)ModuleManager.instance.getModule(MPicEdit.class)).shenChuByAt(-1, -1, Autoreply.instance.CC.at(recievedDataPack.readNum(1)));
					long filelength = jpg.length();
					byte[] filecontent = new byte[(int) filelength];
					FileInputStream in = new FileInputStream(jpg);
					in.read(filecontent);
					in.close();
					dataToSend.writeData(filecontent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case RitsukageDataPack._23returnPic2:
				break;
			case RitsukageDataPack._24MD5Random:
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._25returnMD5Random, recievedDataPack.getTimeStamp());
				String md5=Tools.Hash.MD5(String.valueOf(recievedDataPack.readNum(1) + System.currentTimeMillis() / (24 * 60 * 60 * 1000)));
				char c=md5.charAt(0);
				if (c == '0') {
					dataToSend.write(1, 9961);
				} else if (c == '1') {
					dataToSend.write(1, 9760);
				} else {
					dataToSend.write(1, Integer.parseInt(md5.substring(26), 16) % 10001);
				}
				break;
			case RitsukageDataPack._25returnMD5Random:
				break;
			case RitsukageDataPack._26MD5neta:
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._27returnMD5neta, recievedDataPack.getTimeStamp());
				dataToSend.write(1, ((MDiceImitate)ModuleManager.instance.getModule(MDiceImitate.class)).md5RanStr(recievedDataPack.readNum(1), MDiceImitate.neta));
				break;
			case RitsukageDataPack._27returnMD5neta:
				break;
			case RitsukageDataPack._28MD5music:
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._29returnMD5music, recievedDataPack.getTimeStamp());
				dataToSend.write(1, ((MDiceImitate)ModuleManager.instance.getModule(MDiceImitate.class)).md5RanStr(recievedDataPack.readNum(1), MDiceImitate.music));
				break;
			case RitsukageDataPack._29returnMD5music:
				break;
			case RitsukageDataPack._30MD5grandma:
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._31returnMD5grandma, recievedDataPack.getTimeStamp());
				if (Tools.Hash.MD5(String.valueOf(recievedDataPack.readNum(1) + System.currentTimeMillis() / (24 * 60 * 60 * 1000))).charAt(0) == '0') {
					dataToSend.write(1, "八云紫");
				} else {
					dataToSend.write(1, ((MDiceImitate)ModuleManager.instance.getModule(MDiceImitate.class)).md5RanStr(recievedDataPack.readNum(1), MDiceImitate.name));
				}
				break;
			case RitsukageDataPack._31returnMD5grandma:
				break;
			case RitsukageDataPack._32MD5overSpell:
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._33returnMD5overSpell, recievedDataPack.getTimeStamp());
				dataToSend.write(1, ((MDiceImitate)ModuleManager.instance.getModule(MDiceImitate.class)).md5RanStr(recievedDataPack.readNum(1), MDiceImitate.spells));
				break;
			case RitsukageDataPack._33returnMD5overSpell:
				break;
			case RitsukageDataPack._34sendDanmaku:
				Autoreply.instance.naiManager.sendDanmaku(recievedDataPack.readNum(1) + "", recievedDataPack.readString(2), recievedDataPack.readString(1));
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._0notification, recievedDataPack.getTimeStamp());
				dataToSend.write(1, "发送完成");
				break;
			case RitsukageDataPack._35groupAdd:
				PersonInfo pi35=ConfigManager.instance.getPersonInfoFromQQ(recievedDataPack.readNum(3));
				long addId=recievedDataPack.readNum(1);
				long addgroup=recievedDataPack.readNum(2);
				long addQq=recievedDataPack.readNum(3);
				if (pi35 != null) {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._36returnGroupAdd, recievedDataPack.getTimeStamp());
					dataToSend.write(1, addId);
					dataToSend.write(2, 1);
					dataToSend.write(1, "此帐号为飞机佬账号");
					dataToSend.write(2, ConfigManager.instance.getNickName(addQq));
				} else if (ConfigManager.instance.isGroupAutoAllow(addQq)) {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._36returnGroupAdd, recievedDataPack.getTimeStamp());
					dataToSend.write(1, addId);
					dataToSend.write(2, 1);
					dataToSend.write(1, "此帐号在自动同意列表中");
					dataToSend.write(2, ConfigManager.instance.getNickName(addQq));
				} else if (ConfigManager.instance.isBlackQQ(addQq)) {
					dataToSend = RitsukageDataPack.encode(RitsukageDataPack._36returnGroupAdd, recievedDataPack.getTimeStamp());
					dataToSend.write(1, addId);
					dataToSend.write(2, 0);
					dataToSend.write(1, "黑名单用户");
					dataToSend.write(2, ConfigManager.instance.getNickName(addQq));	
				}	
				break;
			case RitsukageDataPack._37setGroupName:
				long group37=recievedDataPack.readNum(1);
				long qq37=recievedDataPack.readNum(2);
				String name37=recievedDataPack.readString(1);
				Autoreply.CQ.setGroupCard(group37, qq37, name37);
				dataToSend = RitsukageDataPack.encode((short)0, recievedDataPack.getTimeStamp());
				dataToSend.write(1, "操作完成");
				break;
			case RitsukageDataPack._38setSpecialTitle:
				long group38=recievedDataPack.readNum(1);
				long qq38=recievedDataPack.readNum(2);
				long time38=recievedDataPack.readNum(3);
				String name38=recievedDataPack.readString(1);
				Autoreply.CQ.setGroupSpecialTitle(group38, qq38, name38, time38);
				dataToSend = RitsukageDataPack.encode((short)0, recievedDataPack.getTimeStamp());
				dataToSend.write(1, "操作完成");
				break;
			default:
				dataToSend = RitsukageDataPack.encode(RitsukageDataPack._0notification, recievedDataPack.getTimeStamp());
				dataToSend.write(1, "操作类型错误");
		}
		++RemoteWebSocket.botInfoBean.sendTo;
		ogg.send(dataToSend.getData());
		//	DataPack ndp=DataPack.encode(DataPack._0notification, dataPack.getTimeStamp());
		//	ndp.write("成功");
		//	oggConnect.send(ndp.getData());
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

}

