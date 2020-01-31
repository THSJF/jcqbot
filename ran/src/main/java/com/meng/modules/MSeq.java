package com.meng.modules;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.tools.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class MSeq extends BaseModule {
    private ArrayList<SeqBean> seqs=new ArrayList<>();
	private HashMap<String, ArrayList<String>> jsonData = new HashMap<>();

	@Override
	public BaseModule load() {
		File jsonFile = new File(Autoreply.appDirectory + "seq.json");
        if (!jsonFile.exists()) {
            saveData();
		}
        jsonData = new Gson().fromJson(Tools.FileTool.readString(jsonFile), new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType());
   		for (String key : jsonData.keySet()) {
			ArrayList<String> al=jsonData.get(key);
			String[] content=al.toArray(new String[al.size()]);
			int flag=0;
			if (key.startsWith("time")) {
				flag = 1;
			} else if (key.startsWith("menger")) {
				flag = 2;
			}
			seqs.add(new SeqBean(content, flag));
		}
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		for (SeqBean sb:seqs) {
			if (msg.equals(sb.content[0])) {
				sb.pos = 0;
			}
			if (msg.equals(sb.content[sb.pos])) {
				if (sb.flag == 1) {
					ModuleManager.instance.getModule(MUserCounter.class).decLife(fromQQ);
					ModuleManager.instance.getModule(MGroupCounter.class).decLife(fromGroup);
				} else if (sb.flag == 2) {
					ModuleManager.instance.getModule(MUserCounter.class).incMengEr(fromQQ);
					ModuleManager.instance.getModule(MGroupCounter.class).incMengEr(fromGroup);
				}
				++sb.pos;			
				if (sb.pos < sb.content.length) {
					++sb.pos;
					if (sb.pos >= sb.content.length - 1) {
						sb.pos = 0;
					}
					if (sb.pos == 0) {
						Autoreply.sendMessage(fromGroup, 0, sb.content[sb.content.length - 1]);
					} else {
						Autoreply.sendMessage(fromGroup, 0, sb.content[sb.pos - 1]);
					}
					return true;
				}
				break;
			}
		}
		return false;
	}

	private void saveData() {
        try {
            File file = new File(Autoreply.appDirectory + "seq.json");
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(new Gson().toJson(jsonData));
            writer.flush();
            fos.close();
		} catch (IOException e) {
            e.printStackTrace();
		}
	}

	private class SeqBean {
		public String[] content;
		public int pos=0;
		public int flag=0;
		public SeqBean(String[] array, int flag) {
			content = array;
			this.flag = flag;
		}
	} 
}
