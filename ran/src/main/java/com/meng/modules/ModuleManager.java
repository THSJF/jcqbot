package com.meng.modules;

import com.meng.*;
import com.meng.config.*;
import com.meng.SJFInterfaces.*;
import com.meng.tip.*;
import java.util.*;
import java.io.*;

public class ModuleManager extends BaseModule implements IGroupMessage, IPrivateMessage, IDiscussMessage, IGroupEvent, IFriendEvent {

	public static ModuleManager instance;

	private ArrayList<IGroupMessage> groupModules = new ArrayList<>();
	private ArrayList<IPrivateMessage> privateModules = new ArrayList<>();
	private ArrayList<IDiscussMessage> discussModules = new ArrayList<>();
	private ArrayList<IHelpMessage> helpModules = new ArrayList<>();
	private ArrayList<IGroupEvent> groupEventModules = new ArrayList<>();
	private ArrayList<IFriendEvent> friendEventModules = new ArrayList<>();

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
	public ModuleManager load() {
		loadModules(new SenctenceCollecet().load());
		loadModules(new MGroupCounterChart().load());
		loadModules(new MGroupCounter().load());
		loadModules(new MUserCounter().load());
		loadModules(new MTimeTip().load());
		loadModules(new MAdminMsg().load());
		loadModules(new MWarnMsg().load());
		loadModules(new MRepeater().load());
		loadModules(new MoShenFuSong().load());
		loadModules(new MCoinManager().load());
		loadModules(new MMsgAt().load());
		loadModules(new MBiliUpdate().load());
		loadModules(new MDiceImitate().load());
		loadModules(new MDiceCmd().load());
		loadModules(new MSpellCollect().load());
		loadModules(new MOcr().load());
		loadModules(new MBarcode().load());
		loadModules(new FanPoHaiManager().load());
		loadModules(new ThreeManager().load());
		loadModules(new MBanner().load());
		loadModules(new MusicManager().load());
		loadModules(new MPicSearch().load());
		loadModules(new MPicEdit().load());
		loadModules(new MBiliLinkInfo().load());
		loadModules(new MNumberProcess().load());
		loadModules(new MSetu().load());
		loadModules(new MPohaitu().load());
		loadModules(new MNvzhuang().load());
		loadModules(new VirusManager().load());
		loadModules(new MSeq().load());
		//modules.add(new MGroupDic().load());
		Autoreply.instance.threadPool.execute(getGroupModule(MTimeTip.class));
		instance = this;
		return this;
	}

	private void loadModules(Object module) {
		if (module instanceof IGroupMessage) {
			groupModules.add((IGroupMessage)module);
		}
		if (module instanceof IPrivateMessage) {
			privateModules.add((IPrivateMessage)module);
		}
		if (module instanceof IDiscussMessage) {
			discussModules.add((IDiscussMessage)module);
		}
		if (module instanceof IHelpMessage) {
			helpModules.add((IHelpMessage)module);
		}
		if (module instanceof IGroupEvent) {
			groupEventModules.add((IGroupEvent)module);
		}
		if (module instanceof IFriendEvent) {
			friendEventModules.add((IFriendEvent)module);
		}
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (!ConfigManager.instance.isFunctionEnable(fromGroup, ModuleManager.ID_MainSwitch)) {
			return true;
		}
		for (int i=0;i < groupModules.size();++i) {
			if (groupModules.get(i).onGroupMessage(fromGroup, fromQQ, msg, msgId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onPrivateMsg(long fromQQ, String msg, int msgId) {
		for (int i=0;i < privateModules.size();++i) {
			if (privateModules.get(i).onPrivateMsg(fromQQ, msg, msgId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDiscussMessage(long fromDiscuss, long fromQQ, String msg, int msgId) {
		for (int i=0;i < discussModules.size();++i) {
			if (discussModules.get(i).onDiscussMessage(fromDiscuss, fromQQ, msg, msgId)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean onGroupFileUpload(int sendTime, long fromGroup, long fromQQ, String file) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onGroupAdminChange(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onGroupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onGroupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onRequestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onFriendAdd(int sendTime, long fromQQ) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onRequestAddFriend(int sendTime, long fromQQ, String msg, String responseFlag) {
		// TODO: Implement this method
		return false;
	}

	public <T extends IGroupMessage> T getGroupModule(Class<T> t) {
		for (IGroupMessage m : groupModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public <T extends IPrivateMessage> T getPrivateModule(Class<T> t) {
		for (IPrivateMessage m : privateModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public <T extends IDiscussMessage> T getDiscussModule(Class<T> t) {
		for (IDiscussMessage m : discussModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public <T extends IHelpMessage> T getHelpModule(Class<T> t) {
		for (IHelpMessage m : helpModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public <T extends IGroupEvent> T getGroupEventModule(Class<T> t) {
		for (IGroupEvent m : groupEventModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public <T extends IFriendEvent> T getFriendEventModule(Class<T> t) {
		for (IFriendEvent m : friendEventModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}
}

