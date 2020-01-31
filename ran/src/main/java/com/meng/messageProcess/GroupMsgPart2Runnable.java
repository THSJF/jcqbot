package com.meng.messageProcess;

import com.meng.*;
import com.meng.bilibili.live.*;
import com.meng.config.javabeans.*;
import com.meng.modules.*;
import com.meng.remote.*;
import com.meng.tools.*;
import com.sobte.cqp.jcq.entity.*;
import java.io.*;
import java.util.*;

import static com.meng.Autoreply.sendMessage;

public class GroupMsgPart2Runnable implements Runnable {
    private int msgId = 0;
    private long fromGroup = 0;
    private long fromQQ = 0;
    private String msg = null;
    private long timeStamp = 0;

    public GroupMsgPart2Runnable(long fromGroup, long fromQQ, String msg, int msgId, long timeStamp) {
        this.fromGroup = fromGroup;
        this.fromQQ = fromQQ;
        this.msg = msg;
		this.msgId = msgId;
		this.timeStamp = timeStamp;
	}

    @Override
    public void run() {
		File[] imageFiles=null;
		List<CQImage> images = Autoreply.instance.CC.getCQImages(msg);
        if (images.size() != 0) {
            imageFiles = new File[images.size()];
            for (int i = 0, imagesSize = images.size(); i < imagesSize; i++) {
				CQImage image = images.get(i);
                try {
                    imageFiles[i] = Autoreply.instance.fileTypeUtil.checkFormat(image.download(Autoreply.appDirectory + "downloadImages/", image.getMd5()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		if (ModuleManager.instance.onMsg(fromGroup, fromQQ, msg, msgId, imageFiles)) {
			++RemoteWebSocket.botInfoBean.msgCmdPerSec;
		}
	}
}
