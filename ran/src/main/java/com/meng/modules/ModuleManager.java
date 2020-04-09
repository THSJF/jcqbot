package com.meng.modules;

import com.meng.*;
import com.meng.config.*;
import com.meng.tip.*;
import java.io.*;
import java.util.*;

public class ModuleManager extends BaseModule {
	public static ModuleManager instance;
	private ArrayList<BaseModule> modules = new ArrayList<>();

	public static final int ID_MainSwitch=0;
	public static final int ID_Repeater = 1;
	public static final int ID_MoShenFuSong=2;
	public static final int ID_BilibiliNewUpdate=3;
	public static final int ID_Dice=4;
	public static final int ID_SpellCollect=5;
	public static final int ID_OCR=6;
	public static final int ID_Barcode=7;
	public static final int ID_Banner=8;
	public static final int ID_CQCode=9;
	public static final int ID_Music=10;
	public static final int ID_PicSearch=11;
	public static final int ID_BiliLink=12;
	public static final int ID_Setu=13;
	public static final int ID_PoHaiTu=14;
	public static final int ID_NvZhuang=15;
	public static final int ID_GuanZhuangBingDu=16;
	public static final int ID_Seq=17;
	public static final int ID_GroupDic=18;
	public static final int ID_CheHuiMotu=19;
	public static final int ID_PicEdit=20;
	public static final int ID_UserCount=21;
	public static final int ID_GroupCount=22;
	public static final int ID_GroupCountChart=23;

	@Override
	public BaseModule load() {
		modules.add(new SenctenceCollecet().load());
		modules.add(new MGroupCounterChart().load());
		modules.add(new MGroupCounter().load());
		modules.add(new MUserCounter().load());
		modules.add(new MTimeTip().load());
		modules.add(new MAdminMsg().load());
		modules.add(new MWarnMsg().load());
		modules.add(new MRepeater().load());
		modules.add(new MoShenFuSong().load());
		modules.add(new MCoinManager().load());
		modules.add(new MMsgAt().load());
		modules.add(new MBiliUpdate().load());
		modules.add(new MDiceImitate().load());
		modules.add(new MDiceCmd().load());
		modules.add(new MSpellCollect().load());
		modules.add(new MOcr().load());
		modules.add(new MBarcode().load());
		modules.add(new FanPoHaiManager().load());
		modules.add(new ThreeManager().load());
		modules.add(new MBanner().load());
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
		//modules.add(new MGroupDic().load());
		Autoreply.instance.threadPool.execute(getModule(MTimeTip.class));
		instance = this;
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (!ConfigManager.instance.isFunctionEnable(fromGroup, ModuleManager.ID_MainSwitch)) {
			return true;
		}
		for (int i=0;i < modules.size();++i) {
			if (modules.get(i).onMsg(fromGroup, fromQQ, msg, msgId, imgs)) {
				return true;
			}
		}
		return false;
	}

	public <T extends BaseModule> T getModule(Class<T> t) {
		for (int i=0;i < modules.size();++i) {
			BaseModule bm=modules.get(i);
			if (bm.getClass().getSimpleName().equals(t.getSimpleName())) {
				return (T)bm;
			}
		}
		return null;
	}
}

