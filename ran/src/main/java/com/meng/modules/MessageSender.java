package com.meng.modules;

import com.meng.SJFInterfaces.*;
import com.sobte.cqp.jcq.entity.*;
import java.util.*;

/**
 * @author 司徒灵羽
 */
 
public class MessageSender implements ISendGroup,ISendPrivate,ISendDiscuss {

	private CoolQ CQ = null;

	public void setCQ(CoolQ cq) {
		CQ = cq;
	}

	@Override
	public int sendGroup(long toGroup, String msg) {
		return CQ.sendGroupMsg(toGroup, msg);
	}

	@Override
	public int sendGroup(long toGroup, List<String> msg) {
		List<String> smsg = new ArrayList<>();
		smsg.addAll(msg);
		Collections.shuffle(smsg);
		return sendGroup(toGroup, smsg.get(0));
	}

	@Override
	public int sendPrivate(long toQQ, String msg) {
		return CQ.sendPrivateMsg(toQQ, msg);
	}

	@Override
	public int sendPrivate(long toQQ, List<String> msg) {
		List<String> smsg = new ArrayList<>();
		smsg.addAll(msg);
		Collections.shuffle(smsg);
		return sendPrivate(toQQ, smsg.get(0));
	}

	@Override
	public int sendDiscuss(long toDiscuss, String msg) {
		return CQ.sendDiscussMsg(toDiscuss, msg);
	}

	@Override
	public int sendDiscuss(long toDiscuss, List<String> msg) {
		List<String> smsg = new ArrayList<>();
		smsg.addAll(msg);
		Collections.shuffle(smsg);
		return sendDiscuss(toDiscuss, smsg.get(0));
	}
}
