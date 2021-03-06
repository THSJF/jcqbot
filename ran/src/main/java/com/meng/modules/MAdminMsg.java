package com.meng.modules;

import com.google.gson.*;
import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.bilibili.live.*;
import com.meng.config.*;
import com.meng.config.javabeans.*;
import com.meng.sjfmd.libs.*;
import com.meng.tools.*;
import com.meng.tools.override.*;
import com.sobte.cqp.jcq.entity.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MAdminMsg extends BaseGroupModule {
	private MyLinkedHashMap<String,String> masterPermission=new MyLinkedHashMap<>();
	private MyLinkedHashMap<String,String> adminPermission=new MyLinkedHashMap<>();
	public MyLinkedHashMap<String,String> userPermission=new MyLinkedHashMap<>();

	@Override
    public MAdminMsg load() {
		masterPermission.put("小律影专用指令:setconnect", "");
		masterPermission.put(".start|.stop", "总开关");
		masterPermission.put("find:[QQ号]", "在配置文件中查找此人");
		masterPermission.put("z.add[艾特至少一人]", "点赞列表");
		masterPermission.put("zan-now", "立即启动点赞线程,尽量不要用");
		masterPermission.put("block[艾特一人]", "屏蔽列表");
		masterPermission.put("black[艾特一人]", "黑名单");
		masterPermission.put("System.gc();", "System.gc();");
		masterPermission.put("-live.[start|stop]", "开关直播(hina)");
		masterPermission.put("-live.rename.[字符串]", "直播改名(hina)");
		masterPermission.put("blackgroup [群号]", "群加入黑名单,多群用空格隔开");
		masterPermission.put("av更新时间:[UID]", "用户最新后更新视频时间");
		masterPermission.put("avJson:[AV号]", "av信息");
		masterPermission.put("cv更新时间:[UID]", "用户最后更新文章时间");
		masterPermission.put("cvJson:[CV号]", "cv信息");
		masterPermission.put("直播状态lid:[直播间号]", "直播间状态");
		masterPermission.put("直播状态bid:[UID]", "从UID获取直播间状态");
		masterPermission.put("获取直播间:[UID]", "从UID获取直播间ID");
		masterPermission.put("直播时间统计", "统计的直播时间");
		masterPermission.put("群广播:[字符串]", "在所有回复的群里广播");
		masterPermission.put("nai.[称呼|直播间号].[内容]", "三月精账号发送弹幕");
		masterPermission.put("bav:[AV号]", "视频信息");
		masterPermission.put("bcv:[CV号]", "文章信息");
		masterPermission.put("blv:[直播间号]", "直播间信息");
		masterPermission.put("精神支柱[图片]|神触[图片]", "使用图片生成表情包");
		masterPermission.put("cookie.[称呼].[cookie字符串]", "设置cookie,可选值Sunny,Luna,Star,XingHuo,Hina,grzx");
		masterPermission.put("send.[群号].[内容]", "内容转发至指定群");
		masterPermission.put("mother.[字符串]", "直播间点歌问候");
		masterPermission.put("lban.[直播间号|直播间主人].[被禁言UID|被禁言者称呼].[时间]", "直播间禁言,单位为小时");
		masterPermission.put("移除成就 [成就名] [艾特一人]", "移除此人的该成就");

		adminPermission.put("findInAll:[QQ号]", "查找共同群");
		adminPermission.put("ban.[QQ号|艾特].[时间]|ban.[群号].[QQ号].[时间]", "禁言,单位为秒");
		adminPermission.put("加图指令懒得写了", "色图迫害图女装");
		adminPermission.put("蓝统计", "蓝发言统计");
		adminPermission.put("线程数", "线程池信息");
		adminPermission.put(".on|.off", "不修改配置文件的单群开关");
		adminPermission.put(".admin enable|.admin disable", "修改配置文件的单群开关");
		adminPermission.put(".live", "不管配置文件如何,都回复直播列表");

		userPermission.put(".live", "正在直播列表");
		userPermission.put(".nn [名字]", "设置蓝对你的称呼,如果不设置则恢复默认称呼");
		userPermission.put("-int [int] [+|-|*|/|<<|>>|>>>|%|^|&||] [int]", "int运算(溢出)");
		userPermission.put("-uint [int]", "int字节转uint(boom)");
		userPermission.put("抽卡", "抽卡");
		userPermission.put("给蓝master幻币转账", "抽卡，1币3卡");
		userPermission.put("查看成就", "查看成就列表");
		userPermission.put("查看符卡", "查看已获得的符卡,会刷屏，少用");
		userPermission.put("成就条件 [成就名]", "查看获得条件");
		userPermission.put("幻币兑换 [整数]", "本地幻币兑换至小律影");
		userPermission.put("~coins", "查看幻币数量");
		userPermission.put("幻币抽卡 [整数]", "使用本地幻币抽卡");
		userPermission.put("购买符卡 [符卡名]", "购买指定符卡,除lastword");
		userPermission.put("原曲认知 [E|N|H|L]", "原曲认知测试,只能回答自己的问题");

		masterPermission.putAll(adminPermission);
		masterPermission.putAll(userPermission);
		adminPermission.putAll(userPermission);
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (fromQQ == 2856986197L || fromQQ == 2528419891L) {
			if (msg.startsWith("bchat.")) {
				String[] strs=msg.split("\\.", 3);
				PersonInfo pi=ConfigManager.getPersonInfoFromName(strs[1]);
				String resu;
				if (pi == null) {
					resu = Autoreply.instance.naiManager.sendChat(strs[1], strs[2]);
				} else {
					resu = Autoreply.instance.naiManager.sendChat(pi.bliveRoom + "", strs[2]);
				}	
				if (!resu.equals("")) {
					Autoreply.sendMessage(fromGroup, 0, resu);
				}
				return true;
			}
			if (msg.startsWith("blink.")) {
				String[] strs=msg.split("\\.", 2);
				PersonInfo pi=ConfigManager.getPersonInfoFromName(strs[1]);
				if (pi == null) {	  
					JsonParser parser = new JsonParser();
					JsonObject obj = parser.parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + strs[1] + "&quality=4&platform=web")).getAsJsonObject();
					JsonArray ja = obj.get("data").getAsJsonObject().get("durl").getAsJsonArray();
					Autoreply.sendMessage(fromGroup, 0, ja.get(0).getAsJsonObject().get("url").getAsString());
				} else {
					JsonParser parser = new JsonParser();
					JsonObject obj = parser.parse(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=" + pi.bliveRoom + "&quality=4&platform=web")).getAsJsonObject();
					JsonArray ja = obj.get("data").getAsJsonObject().get("durl").getAsJsonArray();
					Autoreply.sendMessage(fromGroup, 0, ja.get(0).getAsJsonObject().get("url").getAsString());			  
				}	
				return true;
			}
		}
		if (!ConfigManager.isAdminPermission(fromQQ) && Autoreply.CQ.getGroupMemberInfo(fromGroup, fromQQ).getAuthority() < 2) {
			return false;
		}
		if (msg.equals(".on")) {
            GroupConfig groupConfig =ConfigManager.getGroupConfig(fromGroup);
            if (groupConfig == null) {
                Autoreply.sendMessage(fromGroup, fromQQ, "本群没有默认配置");
                return true;
            }
            ConfigManager.getGroupConfig(fromGroup).setMainSwitchEnable(true);
            Autoreply.sendMessage(fromGroup, fromQQ, "已启用");
            ConfigManager.save();
			return true;
		}

        if (msg.equals(".off")) {
			ConfigManager.getGroupConfig(fromGroup).setMainSwitchEnable(false);
			Autoreply.sendMessage(fromGroup, 0, "已停用");
            return true;
        }
		if (msg.equals(".live")) {
			String msgSend;
			StringBuilder stringBuilder = new StringBuilder();
			for (Map.Entry<Integer,LivePerson> entry:Autoreply.instance.liveListener.livePersonMap.entrySet()) {	
				if (entry.getValue().lastStatus) {
					stringBuilder.append(ConfigManager.getPersonInfoFromBid(entry.getKey()).name).append("正在直播").append(entry.getValue().liveUrl).append("\n");
				}
			}
			msgSend = stringBuilder.toString();
			Autoreply.sendMessage(fromGroup, fromQQ, msgSend.equals("") ? "居然没有飞机佬直播" : msgSend);
			return true;
		}
		if (msg.startsWith("findInAll:")) {
			Tools.CQ.findQQInAllGroup(fromGroup, fromQQ, msg);
			return true;
		}
        if (!ConfigManager.isMaster(fromQQ) && Autoreply.CQ.getGroupMemberInfo(fromGroup, fromQQ).getAuthority() < 3) {
			return false;
		}

		if (msg.startsWith("群广播:")) {
			if (msg.contains("~") || msg.contains("～")) {
				Autoreply.sendMessage(fromGroup, 0, "包含屏蔽的字符");
				return true;
			}
			String broadcast=msg.substring(4);
			HashSet<Group> hs=new HashSet<>();
			List<Group> glist=Autoreply.CQ.getGroupList();
			for (Group g:glist) {
				GroupConfig gc=ConfigManager.getGroupConfig(g.getId());
				if (!ConfigManager.getGroupConfig(fromGroup).isMainSwitchEnable()) {
					continue;
				}
				Autoreply.sendMessage(gc.n, 0, broadcast);
				hs.add(g);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
			}
			String result="在以下群发送了广播:";
			for (Group g:hs) {
				result += "\n";
				result += g.getId();
				result += ":";
				result += g.getName();
			}
			Autoreply.sendMessage(fromGroup, 0, result);
			return true;
		}
		if (msg.equals(".stop")) {
			Autoreply.sendMessage(fromGroup, 0, "disabled");
			Autoreply.sleeping = true;
			return true;
		}
		if (msg.equals(".start")) {
			Autoreply.sleeping = false;
			Autoreply.sendMessage(fromGroup, 0, "enabled");
			return true;
		}
		if (msg.startsWith("-live")) {
			String[] str=msg.split("\\.");
			PersonInfo pi=ConfigManager.getPersonInfoFromQQ(fromQQ);
			String name;
			if (pi == null) {
				name = "" + fromQQ;
			} else {
				name = pi.name;
			}
			switch (str[1]) {
				case "start":
					try {
						Autoreply.sendMessage(fromGroup, 0, Tools.BilibiliTool.startLive(9721948, Autoreply.instance.cookieManager.getHina()));
						Autoreply.sendMessage(Autoreply.mainGroup, 0, name + "开启了直播");
					} catch (IOException e) {}
					break;
				case "stop":
					try {
						Autoreply.sendMessage(fromGroup, 0, Tools.BilibiliTool.stopLive(9721948, Autoreply.instance.cookieManager.getHina()));
						Autoreply.sendMessage(Autoreply.mainGroup, 0, name + "关闭了直播");
					} catch (IOException e) {}
					break;
				case "rename":
					try {
						Autoreply.sendMessage(fromGroup, 0, Tools.BilibiliTool.renameLive(9721948, str[2], Autoreply.instance.cookieManager.getHina()));
						Autoreply.sendMessage(Autoreply.mainGroup, 0, name + "为直播改了名:" + str[2]);
					} catch (IOException e) {}
			}	
			return true;
		}
		if (msg.startsWith("lban.")) {
			String[] ss=msg.split("\\.");
			String rid=ss[1];
			String uid=ss[2];
			PersonInfo mas=ConfigManager.getPersonInfoFromName(ss[1]);
			if (mas != null) {
				rid += mas.bliveRoom;
			}
			PersonInfo ban=ConfigManager.getPersonInfoFromName(ss[2]);
			if (ban != null) {
				uid += ban.bid;
			}
			if (Tools.BilibiliTool.setBan(Long.parseLong(rid), Long.parseLong(uid), ss[2])) {
				Autoreply.sendMessage(Autoreply.mainGroup, 0, String.format("%s在%s中被禁言%s小时", uid, rid, ss[2]));
			}
			return true;
		}
		if (msg.startsWith("mother.")) {
			if (msg.length() > 7) {
				if (Autoreply.instance.danmakuListenerManager.addMotherWord(msg.substring(7))) {
					Autoreply.sendMessage(fromGroup, 0, msg.substring(7) + "已添加");
				} else {
					Autoreply.sendMessage(fromGroup, 0, "添加失败");
				}
			} else {
				Autoreply.sendMessage(fromGroup, 0, "参数有误");
			}
			return true;
		}
		if (msg.startsWith("block[CQ:at")) {
			StringBuilder sb = new StringBuilder();
			List<Long> qqs = Autoreply.instance.CC.getAts(msg);
			sb.append("屏蔽列表添加:");
			for (int i = 0, qqsSize = qqs.size(); i < qqsSize; i++) {
				long qq = qqs.get(i);
				ConfigManager.addBlockQQ(qq);
				sb.append(qq).append(" ");
			}
			ConfigManager.save();
			Autoreply.sendMessage(fromGroup, fromQQ, sb.toString());
			return true;
		}
		if (msg.startsWith("black[CQ:at")) {
			StringBuilder sb = new StringBuilder();
			List<Long> qqs = Autoreply.instance.CC.getAts(msg);
			sb.append("黑名单添加:");
			for (int i = 0, qqsSize = qqs.size(); i < qqsSize; i++) {
				long qq = qqs.get(i);
				ConfigManager.addBlackQQ(qq);
				sb.append(qq).append(" ");
			}
			ConfigManager.save();
			Autoreply.sendMessage(fromGroup, fromQQ, sb.toString());
			return true;
		}
		if (msg.startsWith("find:")) {
			String name = msg.substring(5);
			HashSet<PersonInfo> hashSet = new HashSet<>();
			for (PersonInfo personInfo : ConfigManager.getPersonInfo()) {
				if (personInfo.name.contains(name)) {
					hashSet.add(personInfo);
				}
				if (personInfo.qq != 0 && String.valueOf(personInfo.qq).contains(name)) {
					hashSet.add(personInfo);
				}
				if (personInfo.bid != 0 && String.valueOf(personInfo.bid).contains(name)) {
					hashSet.add(personInfo);
				}
				if (personInfo.bliveRoom != 0 && String.valueOf(personInfo.bliveRoom).contains(name)) {
					hashSet.add(personInfo);
				}
			}
			Autoreply.sendMessage(fromGroup, fromQQ, GSON.toJson(hashSet));
			return true;
		}
		if (msg.equals("线程数")) {
			String s = "taskCount：" + SJFExecutors.getTaskCount() + "\n" +
				"completedTaskCount：" + SJFExecutors.getCompletedTaskCount() + "\n" +
				"largestPoolSize：" + SJFExecutors.getLargestPoolSize() + "\n" +
				"poolSize：" + SJFExecutors.getPoolSize() + "\n" +
				"activeCount：" + SJFExecutors.getActiveCount();
			Autoreply.sendMessage(fromGroup, fromQQ, s);
			return true;
		}
		if (msg.equalsIgnoreCase("System.gc();")) {
			System.gc();
			Autoreply.sendMessage(fromGroup, fromQQ, "gc start");
			return true;
		}
		if (msg.equals("zan-now")) {
			Autoreply.sendMessage(fromGroup, fromQQ, "start");
			Autoreply.instance.zanManager.sendZan();
			Autoreply.sendMessage(fromGroup, fromQQ, "finish");
			return true;
		}
		if (Autoreply.instance.zanManager.checkAdd(fromGroup, fromQQ, msg)) {
			return true;
		}
		if (msg.equals("直播时间统计")) {
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.liveListener.getLiveTimeCount());
			return true;
		}
		if (msg.startsWith("nai.")) {
			String[] sarr = msg.split("\\.", 3);
			PersonInfo pInfo = ConfigManager.getPersonInfoFromName(sarr[1]);
			if (pInfo != null) {
				Autoreply.instance.naiManager.check(fromGroup, pInfo.bliveRoom + "", fromQQ, sarr[2]);
			} else {
				Autoreply.instance.naiManager.check(fromGroup, sarr[1], fromQQ, sarr[2]);
			}
			return true;
		}
		if (msg.equals("精神支柱")) {
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.image(new File(Autoreply.appDirectory + "pic\\alice.png")));
			return true;
		}
		if (msg.equals("生成位置")) {
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.location(35.594993, 118.869838, 15, "守矢神社", "此生无悔入东方 来世愿生幻想乡"));
		}
		if (msg.startsWith("生成位置")) {
			String[] args = msg.split(",");
			if (args.length == 6) {
				try {
					Autoreply.sendMessage(fromGroup, 0,
										  Autoreply.instance.CC.location(
											  Double.parseDouble(args[2]),
											  Double.parseDouble(args[1]),
											  Integer.parseInt(args[3]),
											  args[4],
											  args[5]));
					return true;
				} catch (Exception e) {
					Autoreply.sendMessage(fromGroup, fromQQ, "参数错误,生成位置.经度double.纬度double.倍数int.名称string.描述string");
					return true;
				}
			}
		}

		String[] strings = msg.split("\\.", 3);
		if (strings[0].equals("send")) {
			if (msg.contains("~") || msg.contains("～")) {
				Autoreply.sendMessage(fromGroup, 0, "包含屏蔽的字符");
				return true;
			}
			switch (strings[2]) {
				case "喵":
					MessageDeleter.autoDelete(Autoreply.sendMessage(Long.parseLong(strings[1]), 0, Autoreply.instance.CC.record("miao.mp3")));
					break;
				case "娇喘":
					MessageDeleter.autoDelete(Autoreply.sendMessage(Long.parseLong(strings[1]), 0, Autoreply.instance.CC.record("mmm.mp3")));
					break;
				default:
					Autoreply.sendMessage(Long.parseLong(strings[1]), 0, strings[2]);
					break;
			}
			return true;
		}
		if (msg.startsWith("精神支柱[CQ:image")) {
			ModuleManager.getGroupModule(MPicEdit.class).jingShenZhiZhuByPic(fromGroup, fromQQ, msg);
			return true;
		}
		if (msg.startsWith("神触[CQ:image")) {
			ModuleManager.getGroupModule(MPicEdit.class).shenChuByAt(fromGroup, fromQQ, msg);
			return true;
		}
		if (msg.startsWith("设置群头衔[CQ:at")) {
			String title = msg.substring(msg.indexOf("]") + 1);
			System.out.println(Autoreply.CQ.setGroupSpecialTitle(fromGroup, Autoreply.instance.CC.getAt(msg), title, -1));
			return true;
		}
		if (msg.startsWith("设置群名片[CQ:at")) {
			String title = msg.substring(msg.indexOf("]") + 1);
			System.out.println(Autoreply.CQ.setGroupCard(fromGroup, Autoreply.instance.CC.getAt(msg), title));
			return true;
		}
        return false;
	}

}
