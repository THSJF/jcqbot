package com.meng.modules;
import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.config.*;
import com.meng.config.javabeans.*;
import com.meng.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;

public class SenctenceCollecet extends BaseModule {

	private SenBean senb=new SenBean();
	private File sbFile;

	@Override
	public BaseModule load() {
		sbFile = new File(Autoreply.appDirectory + "sb.json");
        if (!sbFile.exists()) {
            saveConfig();
        }
        Type type = new TypeToken<RanCfgBean>() {
        }.getType();
        senb = Autoreply.gson.fromJson(Tools.FileTool.readString(sbFile), type);
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (msg.startsWith("c.add.") && ConfigManager.instance.isMaster(fromQQ)) {
			senb.ketWord.add(msg.substring(6));
			saveConfig();
			Autoreply.sendMessage(fromGroup, 0, "添加" + msg + "成功");
			return true;
		}
		for (String s:senb.ketWord) {
			if (msg.contains(s)) {
				senb.colleted.add(msg);
				saveConfig();
				break;
			}
		}
		return false;
	}

	private class SenBean {
		private HashSet<String> ketWord=new HashSet<>();
		private HashSet<String> colleted=new HashSet<>();
	}

	private void saveConfig() {
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
