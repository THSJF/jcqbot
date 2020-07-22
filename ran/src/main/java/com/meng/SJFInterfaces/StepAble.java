package com.meng.SJFInterfaces;

import com.sobte.cqp.jcq.entity.*;
import java.util.*;

import static com.meng.Autoreply.CC;

public class StepAble implements IGroupMessage {
	private HashMap<Long,StepBean> stepHolder = new HashMap<>();

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {

		CQImage cQImage = CC.getCQImage(msg);
		if (msg.startsWith("-sp") && cQImage != null) {

		} else if (msg.startsWith("-sp")) {
			initStep(fromGroup, fromQQ, msg);
		} else if (stepHolder.get(fromQQ) != null) {
			processStep(fromGroup, fromQQ, msg);
		}
		return false;
	}

	private void initStep(long fromGroup, long fromQQ, String msg) {
		if (stepHolder.get(fromQQ) == null) {
			StepBean stepBean = new StepBean();
			stepHolder.put(fromQQ, stepBean);
			stepBean.incStep();
		}
	}

	private void processStep(long fromGroup, long fromQQ, String msg) {
		StepBean sb=stepHolder.get(fromQQ);
		switch (sb.getStep()) {
			case 1:

				break;
		}
	}
}
