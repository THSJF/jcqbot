package com.meng;

import com.google.gson.*;
import com.meng.bilibili.*;
import com.meng.bilibili.main.*;
import com.meng.config.*;
import com.meng.config.javabeans.*;
import com.meng.modules.*;
import com.meng.tools.*;
import java.util.*;

public class OggInterface {

    public boolean processOgg(final long fromQQ, String msg) {
        if (msg.startsWith("findInAll:")) {
            final String finalMsg = msg;
            Autoreply.instance.threadPool.execute(new Runnable() {
					@Override
					public void run() {
						Tools.CQ.findQQInAllGroup(0, fromQQ, finalMsg);
					}
				});
            return true;
        }
        if (msg.startsWith("ban")) {
            String[] arr = msg.split("\\.");
            if (arr.length == 4) {
				// ModuleManager.instance.getModule(Banner.class).checkBan(0, fromQQ, msg);
            }
            return true;
        }
        if (msg.startsWith("av更新时间:")) {
            sendPrivateMessage(fromQQ, String.valueOf(ModuleManager.instance.getModule(NewUpdateManager.class).getAVLastUpdateTime(msg.substring(7))));
            return true;
        }
        if (msg.startsWith("avJson:")) {
            sendPrivateMessage(fromQQ, ModuleManager.instance.getModule(NewUpdateManager.class).getAVJson(msg.substring(7)));
            return true;
        }
        if (msg.startsWith("cv更新时间:")) {
            sendPrivateMessage(fromQQ, String.valueOf(ModuleManager.instance.getModule(NewUpdateManager.class).getCVLastUpdateTime(msg.substring(7))));
            return true;
        }
        if (msg.startsWith("cvJson:")) {
            sendPrivateMessage(fromQQ, ModuleManager.instance.getModule(NewUpdateManager.class).getCVJson(msg.substring(7)));
            return true;
        }
        if (msg.startsWith("直播状态lid:")) {
            String html = Tools.Network.getSourceCode("https://live.bilibili.com/" + msg.substring(8));
            String jsonInHtml = html.substring(html.indexOf("{\"roomInitRes\":"), html.lastIndexOf("}") + 1);
            JsonObject data = new JsonParser().parse(jsonInHtml).getAsJsonObject().get("baseInfoRes").getAsJsonObject().get("data").getAsJsonObject();
            sendPrivateMessage(fromQQ, data.get("live_status").getAsInt() == 1 ? "true" : "false");
            return true;
        }
        if (ModuleManager.instance.getModule(MBiliLinkInfo.class).checkOgg(0, fromQQ, msg)) {
            return true;
        }
        if (msg.startsWith("直播状态bid:")) {
            SpaceToLiveJavaBean sjb = Autoreply.gson.fromJson(Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + msg.substring(8)), SpaceToLiveJavaBean.class);
            sendPrivateMessage(fromQQ, sjb.data.liveStatus == 1 ? "true" : "false");
            return true;
        }
        if (msg.startsWith("获取直播间:")) {
            sendPrivateMessage(fromQQ, Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + msg.substring(6)));
            return true;
        }
        if (msg.startsWith("add{")) {
            PersonInfo personInfo;
            try {
                personInfo = Autoreply.gson.fromJson(msg.substring(3), PersonInfo.class);
            } catch (Exception e) {
                sendPrivateMessage(fromQQ, e.toString());
                return true;
            }
            if (personInfo != null) {
				ConfigManager.instance.configJavaBean.personInfo.add(personInfo);
                ConfigManager.instance.saveConfig();
                sendPrivateMessage(fromQQ, msg + "成功");
            } else {
                sendPrivateMessage(fromQQ, "一个玄学问题导致了失败");
            }
            return true;
        }
        if (msg.startsWith("del{")) {
            PersonInfo p;
            try {
                p = Autoreply.gson.fromJson(msg.substring(3), PersonInfo.class);
            } catch (Exception e) {
                sendPrivateMessage(fromQQ, e.toString());
                return true;
            }
            if (p != null) {
                ConfigManager.instance.configJavaBean.personInfo.remove(p);
                ConfigManager.instance.saveConfig();
                sendPrivateMessage(fromQQ, msg + "成功");
            } else {
                sendPrivateMessage(fromQQ, "一个玄学问题导致了失败");
            }
            return true;
        }
        if (msg.startsWith("find:")) {
            String name = msg.substring(5);
            HashSet<PersonInfo> hashSet = new HashSet<>();
            for (PersonInfo personInfo : ConfigManager.instance.configJavaBean.personInfo) {
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
            sendPrivateMessage(fromQQ, Autoreply.gson.toJson(hashSet));
            return true;
        }
        return false;
    }

    private void sendPrivateMessage(long qq, String msg) {
        Autoreply.sendMessage(0, qq, msg);
    }
}
