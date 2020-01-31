package com.meng.modules;

import com.meng.*;
import com.meng.config.*;
import com.meng.tools.*;
import com.sobte.cqp.jcq.entity.*;
import java.io.*;
import java.util.*;

public class MNvzhuang extends BaseModule {

	@Override
	public BaseModule load() {
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (msg.equals("随机女装")) {
			File[] files = (new File(Autoreply.appDirectory + "nvzhuang/")).listFiles();
			File folder = (File) Tools.ArrayTool.rfa(files);
			File[] pics = folder.listFiles();
			ModuleManager.instance.getModule(MUserCounter.class).incSetu(fromQQ);
			ModuleManager.instance.getModule(MGroupCounter.class).incSetu(fromGroup);
			ModuleManager.instance.getModule(MUserCounter.class).incSetu(Autoreply.CQ.getLoginQQ());
			Autoreply.instance.threadPool.execute(new DeleteMessageRunnable(Autoreply.sendMessage(fromGroup, fromQQ, Autoreply.instance.CC.image((File) Tools.ArrayTool.rfa(pics)))));
		} else if (msg.endsWith("女装")) {
			File[] files = (new File(Autoreply.appDirectory + "nvzhuang/" + msg.replace("女装", ""))).listFiles();
			if (files != null && files.length > 0) {
				ModuleManager.instance.getModule(MUserCounter.class).incSetu(fromQQ);
				ModuleManager.instance.getModule(MGroupCounter.class).incSetu(fromGroup);
				ModuleManager.instance.getModule(MUserCounter.class).incSetu(Autoreply.CQ.getLoginQQ());
				Autoreply.instance.threadPool.execute(new DeleteMessageRunnable(Autoreply.sendMessage(fromGroup, fromQQ, Autoreply.instance.CC.image((File) Tools.ArrayTool.rfa(files)))));
			}
			return true;
		}
		if (!ConfigManager.instance.isAdmin(fromQQ)) {
			return false;
		}
		if (msg.contains("女装[CQ:image")) {
			String setuName = msg.substring(0, msg.indexOf("[CQ:image") - 2);
			List<CQImage> imgList = Autoreply.instance.CC.getCQImages(msg);
			for (CQImage cqImage : imgList) {
				try {
					Autoreply.instance.fileTypeUtil.checkFormat(cqImage.download(Autoreply.appDirectory + File.separator + "nvzhuang/" + setuName, cqImage.getMd5()));
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
