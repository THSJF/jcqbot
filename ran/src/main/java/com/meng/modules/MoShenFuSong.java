package com.meng.modules;

import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.config.*;
import com.meng.tools.*;
import java.io.*;
import java.util.*;

/**
 * @author 司徒灵羽
 */
 
public class MoShenFuSong extends BaseGroupModule {

	@Override
	public MoShenFuSong load() {
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if(!ConfigManager.getGroupConfig(fromGroup).isMoShenFuSongEnable()){
			return false;
		}
		if (msg.contains("大膜法")) { 
            switch (msg.toLowerCase()) {
                case "大膜法 膜神复诵":
					SJFExecutors.execute(new MoRunnable(fromGroup, fromQQ, new Random().nextInt(4)));
					break;
                case "大膜法 膜神复诵 easy":
					SJFExecutors.execute(new MoRunnable(fromGroup, fromQQ, 0));
					break;
                case "大膜法 膜神复诵 normal":
					SJFExecutors.execute(new MoRunnable(fromGroup, fromQQ, 1));
					break;
                case "大膜法 膜神复诵 hard":
					SJFExecutors.execute(new MoRunnable(fromGroup, fromQQ, 2));
					break;
                case "大膜法 膜神复诵 lunatic":
					SJFExecutors.execute(new MoRunnable(fromGroup, fromQQ, 3));
					break;
                case "大膜法 膜神复诵 overdrive":
					SJFExecutors.execute(new MoRunnable(fromGroup, fromQQ, 4));
					break;
                case "大膜法 c568连":
					SJFExecutors.execute(new MoRunnable(fromGroup, fromQQ, 5));
					break;
                default:
					break;
			}
            return true;
		}
		if (msg.equals("大芳法 芳神复诵")) {
			SJFExecutors.execute(new MoRunnable(fromGroup, fromQQ, 6));
			return true;
		}
		return false;
	}

	private class MoRunnable implements Runnable {

		private long fromGroup;
		private long fromQQ;
		private int flag;

		public MoRunnable(long fromGroup, long fromQQ, int flag) {
			this.fromGroup = fromGroup;
			this.flag = flag;
			this.fromQQ = fromQQ;
		}

		@Override
		public void run() {
			File[] files = (new File(Autoreply.appDirectory + "膜神复诵/")).listFiles();
			File[] filesFFF = (new File(Autoreply.appDirectory + "发发发/")).listFiles();
			switch (flag) {
				case 0:
					for (int i = 0; i < 4; ++i) {
						Autoreply.CQ.sendGroupMsg(fromGroup, Autoreply.instance.CC.image(Tools.ArrayTool.rfa(files)));
						sleeps(2000);
					}
					break;
				case 1:
					for (int i = 0; i < 5; ++i) {
						Autoreply.CQ.sendGroupMsg(fromGroup, Autoreply.instance.CC.image(Tools.ArrayTool.rfa(files)));
						sleeps(1000);
					}
					break;
				case 2:
					for (int i = 0; i < 6; ++i) {
						Autoreply.CQ.sendGroupMsg(fromGroup, Autoreply.instance.CC.image(Tools.ArrayTool.rfa(files)));
						sleeps(500);
					}
					break;
				case 3:
					for (int i = 0; i < 8; ++i) {
						Autoreply.CQ.sendGroupMsg(fromGroup, Autoreply.instance.CC.image(Tools.ArrayTool.rfa(files)));
						sleeps(100);
					}
					break;
				case 4:
					for (int i = 0; i < 24; ++i) {
						Autoreply.CQ.sendGroupMsg(fromGroup, Autoreply.instance.CC.image(Tools.ArrayTool.rfa(files)));
						sleeps(100);
					}
					break;
				case 5:
					if (ConfigManager.isMaster(fromQQ)) {
						for (int i = 0; i < 68; ++i) {
							Autoreply.CQ.sendGroupMsg(fromGroup, Autoreply.instance.CC.image(Tools.ArrayTool.rfa(files)));
						}
					}
					break;
				case 6:
					for (int i = 0; i < 5; ++i) {
						Autoreply.CQ.sendGroupMsg(fromGroup, Autoreply.instance.CC.image(Tools.ArrayTool.rfa(filesFFF)));
					}
					break;
			}
		}
	}

    private void sleeps(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
