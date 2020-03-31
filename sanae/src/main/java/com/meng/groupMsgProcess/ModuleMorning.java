package com.meng.groupMsgProcess;
import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.tools.*;
import com.sobte.cqp.jcq.entity.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;

public class ModuleMorning extends BaseModule {

	private ArrayList<QQInfo> gotUp;

	@Override
	public BaseModule load() {
		Type type = new TypeToken<ArrayList<QQInfo>>() {
		}.getType();
		File gotUpF = new File(Autoreply.appDirectory + "/gotUp.json");
		if (!gotUpF.exists()) {
			gotUp = new ArrayList<>();
			saveConfig();
		}
        gotUp = Autoreply.gson.fromJson(Tools.FileTool.readString(gotUpF), type);
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId) {
		if (msg.equals("早上好")) {
			for (QQInfo qif:gotUp) {
				if (qif.getQqId() == fromQQ) {
					return false;
				}
			}
			QQInfo qi=Autoreply.CQ.getStrangerInfo(fromQQ);
			if (qi != null) {
				gotUp.add(qi);
			} else {
				qi = new QQInfo();
				qi.setQqId(fromQQ);
				qi.setGender(1);
				gotUp.add(qi);
			}
			Autoreply.sendMessage(fromGroup, 0, String.format("你是今天第%d位起床的%s哦", gotUp.size(), qi.getGender() == 0 ?"少年": "少女"));
		}
		return false;
	}

	private void saveConfig() {
		try {
			FileOutputStream fos = new FileOutputStream(new File(Autoreply.appDirectory + "/gotUp.json"));
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(Autoreply.gson.toJson(gotUp));
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void reset() {
		gotUp.clear();
	}
}
