package com.meng.modules;
import com.meng.*;
import com.meng.config.*;
import com.meng.tools.*;
import java.io.*;

public class MMsgAt extends BaseModule {

	@Override
	public BaseModule load() {
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (msg.contains("~") || msg.contains("～")) {
			return false;
		}
		if (Tools.CQ.isAtme(msg)) {
			// 过滤特定的文字
			// @消息发送者并复读内容
			if (msg.contains("蓝") || msg.contains("藍") || msg.contains("赠送")) {
				return true;
			}
			if (fromQQ == 2558395159L || fromQQ == 1281911569L || fromQQ == ConfigManager.instance.configJavaBean.ogg) {
				return true;
			}
			Autoreply.sendMessage(fromGroup, 0, msg.replace("[CQ:at,qq=" + Autoreply.CQ.getLoginQQ() + "]", "[CQ:at,qq=" + fromQQ + "]"));
			return true;
		}
		return false;
	}
}

