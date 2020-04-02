package com.meng.bilibili.live;

import com.meng.*;
import com.meng.tools.*;
import java.io.*;
import java.net.*;

public class NaiManager {

	public String sendChat(String roomId, String msg) {
		sendDanmaku(roomId, Autoreply.instance.cookieManager.getHina(), msg);
        return "";
	}

	public String grzxMsg(String roomId, String msg) {
		sendDanmaku(roomId, Autoreply.instance.cookieManager.getGrzx(), msg);
		return "";
	}

    public void check(long fromGroup, String roomId, long fromQQ, String msg) {
		sendDanmaku(roomId, Autoreply.instance.cookieManager.getSunny(), msg);
		sendDanmaku(roomId, Autoreply.instance.cookieManager.getLuna(), msg);
		sendDanmaku(roomId, Autoreply.instance.cookieManager.getStar(), msg);
        Autoreply.sendMessage(fromGroup, fromQQ, roomId + "已奶");
    }

    public void sendDanmaku(String roomId, String cookie, String msg) {
		Tools.BilibiliTool.sendLiveDanmaku(msg, cookie, Long.parseLong(roomId));
	}

    public String encode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "Issue while encoding" + e.getMessage();
        }
    }
}
