package com.meng.groupChat;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.meng.*;
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
        Type type = new TypeToken<HashMap<String, HashSet<String>>>() {
        }.getType();
        dic = new Gson().fromJson(Tools.FileTool.readString(dicFile), type);
		enable = true;
		return this;
	}

    public void clear() {
        groupMap.clear();
    }

	public void addDic(long group) {
        groupMap.put(group, new DicReplyGroup(group));
    }

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (checkPublicDic(fromGroup, fromQQ, msg)) {
            return true;
        }
        return groupMap.get(fromGroup).checkMsg(fromGroup, fromQQ, msg);
    }

    private boolean checkPublicDic(long group, long qq, String msg) {
        for (String key : dic.keySet()) {
            if (key.equals(msg)) {
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
