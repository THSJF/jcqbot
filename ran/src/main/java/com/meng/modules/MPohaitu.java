package com.meng.modules;

import com.meng.*;
import com.meng.config.*;
import com.meng.SJFInterfaces.*;
import com.meng.tools.*;
import com.sobte.cqp.jcq.entity.*;
import java.io.*;
import java.util.*;

public class MPohaitu extends BaseGroupModule {

	@Override
	public MPohaitu load() {
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if(!ConfigManager.getGroupConfig(fromGroup).isPoHaiEnable()){
			return false;
		}
		if (msg.equals("迫害图")) {
			String[] strings = (new File(Autoreply.appDirectory + "pohai/")).list();
			StringBuilder sBuilder = new StringBuilder("现在有");
			for (String s : strings) {
				sBuilder.append(" ").append(s);
			}
			sBuilder.append("的迫害图");
			Autoreply.sendMessage(fromGroup, fromQQ, sBuilder.toString());
			return true;
		}
		if (msg.endsWith("迫害图")) {
			switch (msg) {
				case "零食迫害图":
					msg = "鸽鸽迫害图";
					break;
				case "旭东迫害图":
					msg = "天星厨迫害图";
					break;
				case "星小渚迫害图":
					msg = "杏子迫害图";
					break;
			}
			File[] files = (new File(Autoreply.appDirectory + "pohai/" + msg.replace("迫害图", ""))).listFiles();
			if (files != null && files.length > 0) {
				Autoreply.instance.threadPool.execute(new DeleteMessageRunnable(Autoreply.sendMessage(fromGroup, fromQQ, Autoreply.instance.CC.image((File) Tools.ArrayTool.rfa(files)))));
				ModuleManager.getGroupModule(MUserCounter.class).incPohaitu(fromQQ);
				ModuleManager.getGroupModule(MGroupCounter.class).incPohaitu(fromGroup);
				ModuleManager.getGroupModule(MUserCounter.class).incPohaitu(Autoreply.CQ.getLoginQQ());
			}
			return true;
		}
		if (!ConfigManager.isAdminPermission(fromQQ)) {
			return false;
		}
		if (msg.contains("迫害图[CQ:image")) {
			String pohaituName = msg.substring(0, msg.indexOf("[CQ:image") - 3);
			switch (pohaituName) {
				case "零食":
					pohaituName = "鸽鸽";
					break;
				case "旭东":
					pohaituName = "天星厨";
					break;
				case "星小渚":
					pohaituName = "杏子";
					break;
				default:
					break;
			}
			List<CQImage> imgList = Autoreply.instance.CC.getCQImages(msg);
			for (CQImage cqImage : imgList) {
				try {
					Autoreply.instance.fileTypeUtil.checkFormat(cqImage.download(Autoreply.appDirectory + File.separator + "pohai/" + pohaituName, cqImage.getMd5()));
				} catch (IOException e) {
					e.printStackTrace();
					Autoreply.sendMessage(fromGroup, fromQQ, e.toString());
					return true;
				}
			}
			Autoreply.sendMessage(fromGroup, fromQQ, imgList.size() + "张图添加成功");
			return true;
		}
		return false;
	}
}
