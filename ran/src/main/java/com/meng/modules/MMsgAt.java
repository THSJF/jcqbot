package com.meng.modules;
import com.meng.*;
import com.meng.config.*;
import com.meng.SJFInterfaces.*;
import com.meng.tools.*;
import java.io.*;

public class MMsgAt extends BaseGroupModule {

	@Override
	public MMsgAt load() {
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (msg.contains("~") || msg.contains("～")) {
			return false;
		}
		if (Tools.CQ.isAtme(msg)) {
			if (fromQQ == 2558395159L || fromQQ == 1281911569L || fromQQ == ConfigManager.instance.configHolder.ogg) {
				return true;
			}
			Autoreply.sendMessage(fromGroup, 0, msg.replace("[CQ:at,qq=" + Autoreply.CQ.getLoginQQ() + "]", "[CQ:at,qq=" + fromQQ + "]"));
			return true;
		}
		return false;
	}
}

