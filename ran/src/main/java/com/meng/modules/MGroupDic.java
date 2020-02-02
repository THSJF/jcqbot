package com.meng.modules;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.config.*;
import com.meng.modules.*;
import com.meng.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public class MGroupDic extends BaseModule {

    private HashMap<Long, DicReplyGroup> groupMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> dic = new HashMap<>();

	@Override
	public BaseModule load() {
        File dicFile = new File(Autoreply.appDirectory + "dic\\dic.json");
        if (!dicFile.exists()) {
            saveDic(dicFile, dic);
        }
        dic = new Gson().fromJson(Tools.FileTool.readString(dicFile), new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType());
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (!ConfigManager.instance.isFunctionEnable(fromGroup, ModuleManager.ID_GroupDic)) {
			return false;
		}
		if (checkPublicDic(fromGroup, fromQQ, msg)) {
            return true;
        }
		DicReplyGroup dig=groupMap.get(fromGroup);
		if (dig == null) {
			dig = new DicReplyGroup(fromGroup);
			groupMap.put(fromGroup, dig);
		}
        return dig.checkMsg(fromGroup, fromQQ, msg);
    }

    private boolean checkPublicDic(long group, long qq, String msg) {  
		for (String key : dic.keySet()) {
            if (key.contains(msg)) {
				ArrayList<String> ans=dic.get(key);
                Autoreply.sendMessage(group, qq, ans.get(Autoreply.instance.random.nextInt(ans.size())));
                return true;
            }
        }
        return false;
    }

	private class DicReplyGroup {
		private HashMap<String, ArrayList<String>> dic = new HashMap<>();
		public DicReplyGroup(long group) {
			File dicFile = new File(Autoreply.appDirectory + "dic\\dic" + group + ".json");
			if (!dicFile.exists()) {
				saveDic(dicFile, dic);
			}
			Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {
			}.getType();
			dic = Autoreply.gson.fromJson(Tools.FileTool.readString(dicFile), type);
		}
		public boolean checkMsg(long group, long qq, String msg) {
			for (String key : dic.keySet()) {
				if (Pattern.matches(".*" + key + ".*", msg.replace(" ", "").trim())) {
					ArrayList<String> ans=dic.get(key);
					Autoreply.sendMessage(group, qq, ans.get(Autoreply.instance.random.nextInt(ans.size())));
					return true;
				}
			}
			return false;
		}
	}
	private void saveDic(File dicFile, HashMap<String, ArrayList<String>> dic) {
		try {
			FileOutputStream fos = new FileOutputStream(dicFile);
			OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			writer.write(new Gson().toJson(dic));
			writer.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
