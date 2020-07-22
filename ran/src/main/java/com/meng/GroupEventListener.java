package com.meng;

import com.meng.SJFInterfaces.*;
import com.meng.config.*;
import com.meng.config.javabeans.*;
import com.meng.tools.*;
import com.sobte.cqp.jcq.entity.*;

import static com.meng.Autoreply.sendMessage;
import static com.meng.Autoreply.CC;
import static com.sobte.cqp.jcq.event.JcqApp.CQ;

/**
 * @author 司徒灵羽
 */

public class GroupEventListener implements IGroupEvent ,IRequest {

	@Override
	public boolean onGroupFileUpload(int sendTime, long fromGroup, long fromQQ, String file) {
		// GroupFile com.meng.groupFile = CQ.getGroupFile(file);
        // if (com.meng.groupFile == null) { // 解析群文件信息，如果失败直接忽略该消息
        // return MSG_IGNORE;
        // }
        //   fileInfoManager.check(subType, sendTime, fromGroup, fromQQ, file);
		return false;
	}

	@Override
	public boolean onGroupAdminChange(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
		if (subtype == 1) {
            sendMessage(fromGroup, 0, CC.at(beingOperateQQ) + "你绿帽子没莉");
        } else if (subtype == 2) {
            sendMessage(fromGroup, 0, CC.at(beingOperateQQ) + "群主给了你个绿帽子");
        }
		return false;
	}

	@Override
	public boolean onGroupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		if (subtype == 1) {
			if (!ConfigManager.getGroupConfig(fromGroup).isMainSwitchEnable()) {
                return true;
            }
			QQInfo qInfo = Autoreply.CQ.getStrangerInfo(beingOperateQQ);
            sendMessage(fromGroup, 0, ConfigManager.getNickName(beingOperateQQ)  + "(" + qInfo.getQqId() + ")" + "跑莉");
        } else if (subtype == 2) {
            if (beingOperateQQ == 2856986197L) {
				ConfigManager.addBlack(fromGroup, fromQQ);
				Autoreply.CQ.setGroupLeave(fromGroup, false);
                return true;
            }
            if (beingOperateQQ == 2558395159L) {
                Autoreply.CQ.setGroupLeave(fromGroup, false);
                return true;
            }
            if (beingOperateQQ == Autoreply.CQ.getLoginQQ()) {
                ConfigManager.addBlack(fromGroup, fromQQ);
                return true;
            }
			QQInfo qInfo = Autoreply.CQ.getStrangerInfo(beingOperateQQ);
            QQInfo qInfo2 = Autoreply.CQ.getStrangerInfo(fromQQ);
            sendMessage(fromGroup, 0, ConfigManager.getNickName(beingOperateQQ) + "(" + qInfo.getQqId() + ")" + "被" + ConfigManager.getNickName(fromQQ) + "(" + qInfo2.getQqId() + ")" + "玩完扔莉");
        }
		return false;
	}

	@Override
	public boolean onGroupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		if (ConfigManager.isBlackQQ(beingOperateQQ)) {
            Tools.CQ.ban(fromGroup, fromQQ, 300);
        }
        PersonInfo personInfo = ConfigManager.getPersonInfoFromQQ(beingOperateQQ);
        if (personInfo != null && personInfo.name.equals("熊哥")) {
            sendMessage(959615179L, 0, Autoreply.instance.CC.at(-1) + "熊加入了群" + fromGroup);
            return true;
        }
        if (!ConfigManager.getGroupConfig(fromGroup).isMainSwitchEnable()) {
            return true;
        }
        if (personInfo != null) {
            sendMessage(fromGroup, 0, "欢迎" + ConfigManager.getNickName(beingOperateQQ));
        } else {
            sendMessage(fromGroup, 0, "欢迎新大佬");
        }
		return false;
	}

	@Override
	public boolean onRequestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {
		System.out.println("groupAdd");
        if (subtype == 1) {
            if (ConfigManager.isBlackQQ(fromQQ)) {
                CQ.setGroupAddRequest(responseFlag, REQUEST_GROUP_ADD, REQUEST_ADOPT, null);
                Tools.CQ.ban(fromGroup, fromQQ, 2592000);
                sendMessage(fromGroup, fromQQ, "不要问为什么你会进黑名单，你干了什么自己知道");
                return true;
            }
            PersonInfo personInfo = ConfigManager.getPersonInfoFromQQ(fromQQ);
            if (personInfo != null) {
                CQ.setGroupAddRequest(responseFlag, REQUEST_GROUP_ADD, REQUEST_ADOPT, null);
                //        sendMessage(fromGroup, 0, "欢迎" + personInfo.name);
            } else {
                sendMessage(fromGroup, 0, "有人申请加群，绿帽赶紧瞅瞅");
            }
        } else if (subtype == 2) {
            if (ConfigManager.isBlackQQ(fromQQ) || ConfigManager.isBlackGroup(fromGroup)) {
				CQ.setGroupAddRequest(responseFlag, REQUEST_GROUP_ADD, REQUEST_REFUSE, "");
				sendMessage(0, 2856986197L, "拒绝了" + fromQQ + "邀请我加入群" + fromGroup);
                return true;
            }
			if (ConfigManager.isMaster(fromQQ) || ConfigManager.getGroupConfig(fromGroup).n != 0) {
				CQ.setGroupAddRequest(responseFlag, REQUEST_GROUP_INVITE, REQUEST_ADOPT, null);
				sendMessage(0, 2856986197L, "Master" + fromQQ + "邀请我加入群" + fromGroup);			
				return true;
			}
            sendMessage(0, 2856986197L, fromQQ + "邀请我加入群" + fromGroup);
        }
		return false;
	}

}
