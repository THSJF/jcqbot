package com.meng.modules;

import com.meng.*;
import com.meng.dice.*;
import com.meng.groupChat.*;
import com.meng.groupChat.Sequence.*;
import com.meng.messageProcess.*;
import com.meng.ocr.*;
import com.meng.tools.*;
import java.io.*;
import java.util.*;
import com.meng.bilibili.*;
import com.meng.bilibili.main.*;
import com.meng.tip.*;

public class ModuleManager extends BaseModule {
	public static ModuleManager instance;
	private ArrayList<BaseModule> modules = new ArrayList<>();

	@Override
	public BaseModule load() {
		add(new MGroupCounterChart().load());
		add(new MGroupCounter().load());
		add(new MTimeTip().load());
		add(new MAdminMsg().load());
		add(new MWarnMsg().load());
		add(new MRepeater().load());
		add(new MCoinManager().load());
		add(new MMsgAt().load());
		add(new NewUpdateManager().load());

		add(new MDiceImitate().load());
		add(new MSpellCollect().load());
		add(new MOcr().load());
		add(new MBarcode().load());
		add(new FanPoHaiManager().load());
		add(new ThreeManager().load());
		add(new MBanner().load());
		add(new MCQCodeProcess().load());
		add(new MusicManager().load());
		add(new MPicSearch().load());
		add(new MPicEdit().load());
	

		add(new MBiliLinkInfo().load());
		add(new MNumberProcess().load());
		add(new MSetu().load());
		add(new MPohaitu().load());
		add(new MNvzhuang().load());

		add(new VirusManager().load());
		add(new MSeq().load());
		add(new MGroupDic().load());
		add(new MNvzhuang().load());
		Autoreply.instance.threadPool.execute(getModule(MTimeTip.class));
		instance = this;
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		for (int i=0;i < modules.size();++i) {
			if (modules.get(i).onMsg(fromGroup, fromQQ, msg, msgId, imgs)) {
				return true;
			}
		}
		return false;
	}

	public <T extends BaseModule> T getModule(Class<T> module) {
		int s = modules.size();
		for (int i=0;i < s;++i) {
			BaseModule t = modules.get(i);
			if (t.getClass() == module.getClass()) {
				return (T)t;
			}
		}
		return null;
	}

	private void add(BaseModule T) {
		modules.add(T);
	}
}

