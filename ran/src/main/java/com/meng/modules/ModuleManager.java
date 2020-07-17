package com.meng.modules;

import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.config.*;
import com.meng.remote.*;
import com.meng.tip.*;
import com.sobte.cqp.jcq.entity.*;
import java.util.*;

import static com.meng.Autoreply.sendMessage;
import static com.meng.Autoreply.CC;
import static com.sobte.cqp.jcq.event.JcqApp.CQ;

public class ModuleManager extends BaseModule implements IGroupMessage, IPrivateMessage, IDiscussMessage, IGroupEvent, IFriendEvent, IRequest, IMsg {

	public static ModuleManager instance;

	private ArrayList<IGroupMessage> groupModules = new ArrayList<>();
	private ArrayList<IPrivateMessage> privateModules = new ArrayList<>();
	private ArrayList<IDiscussMessage> discussModules = new ArrayList<>();
	private ArrayList<IHelpMessage> helpModules = new ArrayList<>();
	private ArrayList<IGroupEvent> groupEventModules = new ArrayList<>();
	private ArrayList<IFriendEvent> friendEventModules = new ArrayList<>();

	@Override
	public ModuleManager load() {
		loadModules(new SenctenceCollecet().load());
		loadModules(new MessageRefuse().load());
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
		loadModules(new GroupEventListener());
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
		Autoreply.instance.remoteWebSocket.sendMsg(1, fromGroup, fromQQ, msg, msgId);
		++RemoteWebSocket.botInfoBean.msgPerSec;
		if (!ConfigManager.getGroupConfig(fromGroup).isMainSwitchEnable()) {
			return true;
		}
		for (IGroupMessage m : groupModules) {
			if (m.onGroupMessage(fromGroup, fromQQ, msg, msgId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onPrivateMsg(long fromQQ, String msg, int msgId) {
		for (IPrivateMessage m : privateModules) {
			if (m.onPrivateMsg(fromQQ, msg, msgId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDiscussMessage(long fromDiscuss, long fromQQ, String msg, int msgId) {
		for (IDiscussMessage m : discussModules) {
			if (m.onDiscussMessage(fromDiscuss, fromQQ, msg, msgId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onGroupFileUpload(int sendTime, long fromGroup, long fromQQ, String file) {
		if (!ConfigManager.getGroupConfig(fromGroup).isMainSwitchEnable()) {
            return true;
        }
		for (IGroupEvent e : groupEventModules) {
			if (e.onGroupFileUpload(sendTime, fromGroup, fromQQ, file)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onGroupAdminChange(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
		if (!ConfigManager.getGroupConfig(fromGroup).isMainSwitchEnable()) {
            return true;
        }
		for (IGroupEvent e : groupEventModules) {
			if (e.onGroupAdminChange(subtype, sendTime, fromGroup, beingOperateQQ)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onGroupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		if (!ConfigManager.getGroupConfig(fromGroup).isMainSwitchEnable()) {
            return true;
        }
		for (IGroupEvent e : groupEventModules) {
			if (e.onGroupMemberDecrease(subtype, sendTime, fromGroup, fromQQ, beingOperateQQ)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onGroupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		if (!ConfigManager.getGroupConfig(fromGroup).isMainSwitchEnable()) {
            return true;
        }
		if (beingOperateQQ == CQ.getLoginQQ()) {
            return true;
        }
		for (IGroupEvent e : groupEventModules) {
			if (e.onGroupMemberIncrease(subtype, sendTime, fromGroup, fromQQ, beingOperateQQ)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onRequestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {
		if (ConfigManager.isBlockQQ(fromQQ)) {
			CQ.setFriendAddRequest(responseFlag, Autoreply.REQUEST_REFUSE, "");
			sendMessage(0, 2856986197L, "拒绝了" + fromQQ + "加为好友");
            return true;
        }
		for (IGroupEvent e : groupEventModules) {
			if (e.onRequestAddGroup(subtype, sendTime, fromGroup, fromQQ, msg, responseFlag)) {
				return true;
			}
		}
        /*
         * REQUEST_ADOPT 通过 REQUEST_REFUSE 拒绝
         */
        //    QQInfo qInfo = CQ.getStrangerInfo(fromQQ);
        //    CQ.setFriendAddRequest(responseFlag, REQUEST_ADOPT, qInfo.getNick()); //
        // sendMessage(0, fromQQ, "本体2856986197");
        sendMessage(0, 2856986197L, fromQQ + "把我加为好友");
        // 同意好友添加请求
		return true;
	}

	@Override
	public boolean onFriendAdd(int sendTime, long fromQQ) {
		for (IFriendEvent e : friendEventModules) {
			if (e.onFriendAdd(sendTime, fromQQ)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onRequestAddFriend(int sendTime, long fromQQ, String msg, String responseFlag) {
		for (IFriendEvent e : friendEventModules) {
			if (e.onRequestAddFriend(sendTime, fromQQ, msg, responseFlag)) {
				return true;
			}
		}
		return false;
	}

	public static <T extends IGroupMessage> T getGroupModule(Class<T> t) {
		for (IGroupMessage m :instance.groupModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public static <T extends IPrivateMessage> T getPrivateModule(Class<T> t) {
		for (IPrivateMessage m : instance.privateModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public static <T extends IDiscussMessage> T getDiscussModule(Class<T> t) {
		for (IDiscussMessage m : instance.discussModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public static <T extends IHelpMessage> T getHelpModule(Class<T> t) {
		for (IHelpMessage m : instance.helpModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public static <T extends IGroupEvent> T getGroupEventModule(Class<T> t) {
		for (IGroupEvent m : instance.groupEventModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}

	public static <T extends IFriendEvent> T getFriendEventModule(Class<T> t) {
		for (IFriendEvent m : instance.friendEventModules) {
			if (m.getClass() == t) {
				return (T)m;
			}
		}
		return null;
	}
}

