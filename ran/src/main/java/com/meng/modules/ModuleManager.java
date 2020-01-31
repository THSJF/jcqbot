package com.meng.modules;

import com.meng.*;
import com.meng.tools.*;
import java.io.*;
import java.util.*;
import com.meng.tip.*;

public class ModuleManager extends BaseModule {
	public static ModuleManager instance;
	private ArrayList<BaseModule> modules = new ArrayList<>();

	@Override
	public BaseModule load() {
		modules.add(new MGroupCounterChart().load());
		modules.add(new MGroupCounter().load());
		modules.add(new MTimeTip().load());
		modules.add(new MAdminMsg().load());
		modules.add(new MWarnMsg().load());
		modules.add(new MRepeater().load());
		modules.add(new MoShenFuSong().load());
		modules.add(new MCoinManager().load());
		modules.add(new MMsgAt().load());
		modules.add(new MBiliUpdate().load());
		modules.add(new MDiceImitate().load());
		modules.add(new MSpellCollect().load());
		modules.add(new MOcr().load());
		modules.add(new MBarcode().load());
		modules.add(new FanPoHaiManager().load());
		modules.add(new ThreeManager().load());
		modules.add(new MBanner().load());
		modules.add(new MCQCodeProcess().load());
		modules.add(new MusicManager().load());
		modules.add(new MPicSearch().load());
		modules.add(new MPicEdit().load());
		modules.add(new MBiliLinkInfo().load());
		modules.add(new MNumberProcess().load());
		modules.add(new MSetu().load());
		modules.add(new MPohaitu().load());
		modules.add(new MNvzhuang().load());
		modules.add(new VirusManager().load());
		modules.add(new MSeq().load());
		modules.add(new MGroupDic().load());
		modules.add(new MNvzhuang().load());
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
			if (t.getClass().getSimpleName().equals(module.getClass().getSimpleName())) {
				return (T)t;
			}
		}
		return null;
	}
}

