package com.meng.modules;

import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.config.*;
import com.meng.remote.*;
import com.meng.tip.*;
import com.meng.tools.*;
import com.sobte.cqp.jcq.entity.*;
import java.lang.reflect.*;
import java.util.*;

import static com.meng.Autoreply.sendMessage;
import static com.meng.Autoreply.CC;
import static com.sobte.cqp.jcq.event.JcqApp.CQ;

/**
 * @author 司徒灵羽
 */

public class ModuleManager extends BaseModule implements IGroupMessage, IPrivateMessage, IDiscussMessage, IGroupEvent, IFriendEvent, IRequest, IMsg {

	public static ModuleManager instance;

	private ArrayList<IGroupMessage> groupModules = new ArrayList<>();
	private ArrayList<IPrivateMessage> privateModules = new ArrayList<>();
	private ArrayList<IDiscussMessage> discussModules = new ArrayList<>();
	private ArrayList<IHelpMessage> helpModules = new ArrayList<>();
	private ArrayList<IGroupEvent> groupEventModules = new ArrayList<>();
	private ArrayList<IFriendEvent> friendEventModules = new ArrayList<>();
	private ArrayList<Object> all = new ArrayList<>();

	@Override
	public ModuleManager load() {
		load(ReflectCommand.class, false);
		load(SenctenceCollecet.class);
		load(MessageRefuse.class);
		load(MGroupCounterChart.class);
		load(MGroupCounter.class);
		load(MUserCounter.class);
		load(MTimeTip.class);
		load(MAdminMsg.class);
		load(MWarnMsg.class);
		load(MRepeater.class);
		//load(MoShenFuSong.class);
		load(MCoinManager.class);
		load(MMsgAt.class);
		//load(MBiliUpdate.class);
		load(MDiceImitate.class);
		load(MDiceCmd.class);
		load(MSpellCollect.class);
		load(MOcr.class);
		load(MBarcode.class);
		//load(FanPoHaiManager.class);
		load(ThreeManager.class);
		load(MBanner.class);
		//load(MusicManager.class);
		load(MPicSearch.class);
		load(MPicEdit.class);
		load(MBiliLinkInfo.class);
		load(MNumberProcess.class);
		//load(MSetu.class);
		//load(MPohaitu.class);
		//load(MNvzhuang.class);
		//load(VirusManager.class);
		load(MSeq.class);
		//modules.add(new MGroupDic.class);
		load(GroupEventListener.class, false);
		SJFExecutors.execute(getGroupModule(MTimeTip.class));
		instance = this;
		return this;
	}

	public void load(Class<?> cls) {
		load(cls, true);
	}

	public void load(Class<?> cls, boolean needLoad) {
		Object o = null;
		try {
			o = cls.newInstance();
			if (needLoad) {
				Method m = o.getClass().getMethod("load");
				m.invoke(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (o == null) {
			Autoreply.sendMessage(Autoreply.yysGroup, 0, "加载失败:" + cls.getName());
		}
		loadModules(o);
	}

	public void load(String className, boolean needLoad) {
		try {
			load(Class.forName(className), needLoad);
		} catch (ClassNotFoundException e) {
			Autoreply.sendMessage(Autoreply.yysGroup, 0, "加载失败:" + className);
		}
	}

	public void loadModules(Object module) {
		all.add(module);
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
			System.out.println(m.getClass().getName());
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

	public static Object getModule(Class t) {
		for (Object m :instance.all) {
			if (m.getClass() == t) {
				return m;
			}
		}
		return null;
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

