package com.meng.modules;

import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.config.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * @author 司徒灵羽
 */

public class SenctenceCollecet extends BaseGroupModule implements IPersistentData {

	private SenBean senb = new SenBean();

	@Override
	public SenctenceCollecet load() {
		DataPersistenter.read(this);
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

	private static class SenBean {
		private HashSet<String> ketWord=new HashSet<>();
		private HashSet<String> colleted=new HashSet<>();
	}

	private void save() {
		DataPersistenter.save(this);
    }

	@Override
	public String getPersistentName() {
		return "sb.json";
	}

	@Override
	public Type getDataType() {
		return SenBean.class;
	}

	@Override
	public Object getDataBean() {
		return senb;
	}

	@Override
	public void setDataBean(Object o) {
		senb = (SenctenceCollecet.SenBean) o;
	}
}
