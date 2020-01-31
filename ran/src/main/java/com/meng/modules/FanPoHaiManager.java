package com.meng.modules;

import com.meng.*;
import com.meng.config.javabeans.*;
import com.meng.tools.*;
import com.sobte.cqp.jcq.entity.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import com.meng.config.*;

public class FanPoHaiManager extends BaseModule {
    private HashSet<FingerPrint> fingerPrints = new HashSet<>(64);
    private int pohaicishu = 0;
    private int alpohai = Autoreply.instance.random.nextInt(5) + 2;

	@Override
    public BaseModule load() {
		Autoreply.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					File[] pohaitu = new File(Autoreply.appDirectory + "fan\\").listFiles();
					if (pohaitu != null) {
						for (File file : pohaitu) {
							try {
								fingerPrints.add(new FingerPrint(ImageIO.read(file)));
							} catch (Exception e) {
								System.out.println(file.getAbsolutePath());
							}
						}
					}
					System.out.println("反迫害启动完成");
					Autoreply.sleeping = false;
					Autoreply.sendMessage(Autoreply.mainGroup, 0, "~reconnect");
					Autoreply.instance.enable();
				}
			});
		enable = true;
		return this;
    }

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		try {
            boolean bpohai = false;
            if (msg.contains("迫害") && !msg.contains("反迫害")) {
                ++pohaicishu;
                if (pohaicishu == alpohai) {
                    bpohai = true;
                    pohaicishu = 0;
                    alpohai = Autoreply.instance.random.nextInt() % 5 + 2;
                }
            }
            // 判定图片相似度
            if (!bpohai && imgs != null) {
                float simi = 0.0f;
                for (File file : imgs) {
                    FingerPrint fp1 = new FingerPrint(ImageIO.read(file));
                    for (FingerPrint fingerPrint : fingerPrints) {
                        if (fingerPrint == null) {
                            continue;
                        }
                        float tf = fingerPrint.compare(fp1);
                        if (tf > simi) {
                            simi = tf;
                        }
                    }
                    bpohai = simi > 0.95f;
                }
            }
            if (bpohai || msg.equals("[CQ:bface,p=11361,id=1188CED678E40F79A536C60658990EE7]")) {
                String folder = "";
                PersonInfo personInfo = ConfigManager.instance.getPersonInfoFromQQ(fromQQ);
                if (personInfo != null) {
                    folder = Autoreply.appDirectory + "pohai/" + personInfo.name + "/";
                }
                File file = new File(folder);
                if (msgId != -1) {
                    GroupConfig groupConfig = ConfigManager.instance.getGroupConfig(fromGroup);
                    if (groupConfig != null && groupConfig.isCheHuiMoTu()) {
                        Member me = Autoreply.CQ.getGroupMemberInfoV2(fromGroup, Autoreply.CQ.getLoginQQ());
                        Member ban = Autoreply.CQ.getGroupMemberInfoV2(fromGroup, Autoreply.CQ.getLoginQQ());
                        if (me.getAuthority() - ban.getAuthority() > 0) {
                            Autoreply.CQ.deleteMsg(msgId);
                        }
                    }
                }
                if (folder.equals("") || !file.exists()) {
                    switch (Autoreply.instance.random.nextInt(3)) {
                        case 0:
                            Autoreply.sendMessage(fromGroup, 0, "鬼鬼");
                            break;
                        case 1:
                            Autoreply.sendMessage(fromGroup, 0, "除了迫害和膜你还知道什么");
                            break;
                        case 2:
                            Autoreply.sendMessage(fromGroup, 0, "草绳");
                            break;
                    }
                    return true;
                } else {
                    File[] files = file.listFiles();
                    if (files != null) {
                        Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.image((File) Tools.ArrayTool.rfa(files)));
                        ModuleManager.instance.getModule(MUserCounter.class).incPohaitu(Autoreply.CQ.getLoginQQ());
                        ModuleManager.instance.getModule(MGroupCounter.class).incPohaitu(fromGroup);
                    }
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
