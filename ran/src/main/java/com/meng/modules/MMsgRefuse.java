package com.meng.modules;

import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.config.*;
import com.meng.tools.*;
import java.util.concurrent.*;

public class MMsgRefuse extends BaseGroupModule {

	public ConcurrentHashMap<Long,FireWallBean> msgMap=new ConcurrentHashMap<>();

	@Override
	public MMsgRefuse load() {
		SJFExecutors.execute(new Runnable(){

				@Override
				public void run() {
					while (true) {
						for (FireWallBean mb:msgMap.values()) {
							mb.lastSeconedMsgs = 0;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
					}
				}
			});
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (ConfigManager.isBlockQQ(fromQQ) || ConfigManager.isBlockWord(msg)) {
            return true;
        }
		FireWallBean mtmb=msgMap.get(fromQQ);
		if (mtmb == null) {
			mtmb = new FireWallBean();
			msgMap.put(fromQQ, mtmb);
		}
		//发言间隔过短
		if (System.currentTimeMillis() - mtmb.lastSpeakTimeStamp < 500) {
			++mtmb.timeSubLowTimes;
		} else {
			mtmb.timeSubLowTimes = 0;
		}
		if (mtmb.timeSubLowTimes > 5) {
			if (!mtmb.tiped) {
				mtmb.tiped = true;
				Autoreply.sendMessage(fromGroup, 0, "你说话真快");
			}
			return true;
		}
		//重复次数过多
		mtmb.lastSpeakTimeStamp = System.currentTimeMillis();
		if (mtmb.lastMsg.equals(msg)) {
			++mtmb.repeatTime;
		} else {
			mtmb.repeatTime = 0;
		}
		if (mtmb.repeatTime > 5) {
			if (!mtmb.tiped) {
				mtmb.tiped = true;
				Autoreply.sendMessage(fromGroup, 0, "怎么又是这句话");
			}
			mtmb.lastMsg = msg;
			return true;
		}
		mtmb.lastMsg = msg;
		//一秒内消息过多
		++mtmb.lastSeconedMsgs;
		if (mtmb.lastSeconedMsgs > 4) {
			if (!mtmb.tiped) {
				mtmb.tiped = true;
				Autoreply.sendMessage(fromGroup, 0, "你真稳");
			}
			return true;
		}
		mtmb.tiped = false;
		return false;
	}

	private class FireWallBean {
		public long qq;//qq
		public long lastSpeakTimeStamp;//最后一次发言时间
		public long timeSubLowTimes;//最后两次发言时间差过短次数
		public int repeatTime;//同一句话重复次数
		public int lastSeconedMsgs;//一秒内消息数量
		public String lastMsg = "";//最后一句话
		public boolean tiped = false;//刷屏提示
	}

}

