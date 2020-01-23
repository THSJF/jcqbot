package com.meng;

import com.meng.game.TouHou.*;
import com.meng.groupMsgProcess.*;
import java.util.*;

public class ModuleManager extends BaseModule {
	public static ModuleManager instence;
	private ArrayList<BaseModule> modules = new ArrayList<>();

	@Override
	public BaseModule load() {
		modules.add(new GroupCounter().load());
		modules.add(new MessageRefuse().load());
		modules.add(new ReportManager().load());
		modules.add(new MessageWaitManager().load());
		modules.add(new DiceCommand().load());
		modules.add(new FaithManager().load());
		modules.add(new ContainsAtManager().load());
		modules.add(new TouHouDataManager().load());
		modules.add(new SpellCollect().load());
		modules.add(new TouHouKnowledge().load());
		modules.add(new RepeaterManager().load());
		modules.add(new CQCodeManager().load());
		modules.add(new DiceImitate().load());
		modules.add(new SeqManager());
		modules.add(new DicReply().load());
		instence = this;
		enable = true;
		return this;
	}

	@Override
	public boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId) {
		if (msg.equals("查看活跃数据")) {
			Autoreply.sendMessage(fromGroup, fromQQ, "https://qqweb.qq.com/m/qun/activedata/active.html?gc=" + fromGroup);
			return true;
		}
		for (int i=0;i < modules.size();++i) {
			if (modules.get(i).onMsg(fromGroup, fromQQ, msg, msgId)) {
				return true;
			}
		}
		return false;
	}

	public BaseModule getModule(Class baseModule) {
		return getModule(baseModule.getSimpleName());
	}

	public BaseModule getModule(String simpleClassName) {
		for (int i=0;i < modules.size();++i) {
			BaseModule bm=modules.get(i);
			if (bm.getClass().getSimpleName().equals(simpleClassName)) {
				return bm;
			}
		}
		return null;
	}
}
