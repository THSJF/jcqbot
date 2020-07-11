package com.meng.modules;

import com.madgag.gif.fmsware.*;
import com.meng.*;
import com.meng.config.*;
import com.meng.config.javabeans.*;
import com.meng.SJFInterfaces.*;
import com.meng.tools.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;


public class MRepeater extends BaseGroupModule {

	private HashMap<Long, Repeater> repeaters = new HashMap<>();

	@Override
	public MRepeater load() {
		return this;
	} 

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (!ConfigManager.instance.isFunctionEnable(fromGroup, ModuleManager.ID_Repeater)) {
			return false;
		}
		Repeater rp=repeaters.get(fromGroup);
		if (rp == null) {
			rp = new Repeater(fromGroup);
			repeaters.put(fromGroup, rp);
		}
        return rp.check(fromGroup, fromQQ, msg);
    }

	private class Repeater {
		private String lastMessageRecieved = "";
		private boolean lastStatus = false;
		private long groupNumber = 0;

		public Repeater(long groupNumber) {
			this.groupNumber = groupNumber;
		}

		public boolean check(long fromGroup, long fromQQ, String msg) {
			boolean b = false; 
			b = checkRepeatStatu(fromGroup, fromQQ, msg);
			lastMessageRecieved = msg;
			return b;
		}

		// 复读状态
		private boolean checkRepeatStatu(long group, long qq, String msg) {
			boolean b = false;
			if (!lastStatus && lastMessageRecieved.equals(msg)) {
				b = repeatStart(group, qq, msg);
			}
			if (lastStatus && lastMessageRecieved.equals(msg)) {
				b = repeatRunning(group, qq, msg);
			}
			if (lastStatus && !lastMessageRecieved.equals(msg)) {
				b = repeatEnd(group, qq, msg);
			}
			lastStatus = lastMessageRecieved.equals(msg);
			return b;
		}

		private boolean repeatEnd(long group, long qq, String msg) {
			return false;
		}

		private boolean repeatRunning(long group, long qq, String msg) {
			return false;
		}

		private boolean repeatStart(long group,  long qq,  String msg) {
			Autoreply.sendMessage(group, 0, msg);
			return true;
		}
	}
}
