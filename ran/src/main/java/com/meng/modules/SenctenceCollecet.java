package com.meng.modules;

import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.config.*;
import com.meng.SJFInterfaces.*;
import com.meng.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;

public class SenctenceCollecet extends BaseGroupModule {

	private SenBean senb=new SenBean();
	private File sbFile;

	@Override
	public SenctenceCollecet load() {
		sbFile = new File(Autoreply.appDirectory + "sb.json");
        if (!sbFile.exists()) {
            save();
        }
        senb = Autoreply.gson.fromJson(Tools.FileTool.readString(sbFile), new TypeToken<SenBean>() {}.getType());
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (msg.startsWith("c.add.") && ConfigManager.isMaster(fromQQ)) {
			senb.ketWord.add(msg.substring(6));
			save();
			Autoreply.sendMessage(fromGroup, 0, "添加" + msg + "成功");
			return true;
		}
		for (String s:senb.ketWord) {
			if (msg.contains(s)) {
				senb.colleted.add(msg);
				save();
				break;
			}
		}
		return false;
	}

	private class SenBean {
		private HashSet<String> ketWord=new HashSet<>();
		private HashSet<String> colleted=new HashSet<>();
	}

	private void save() {
		try {
			FileOutputStream fos = new FileOutputStream(sbFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(Autoreply.gson.toJson(senb));
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
