package com.meng.bilibili.live;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.bilibili.main.*;
import com.meng.config.*;
import com.meng.config.javabeans.*;
import com.meng.config.sanae.*;
import com.meng.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;
import org.jsoup.*;

public class LiveListener implements Runnable {

    public ConcurrentHashMap<Integer, LivePerson> livePersonMap = new ConcurrentHashMap<>();
    private boolean loadFinish = false;
    private ConcurrentHashMap<String, Long> liveTimeMap = new ConcurrentHashMap<>();

    public LiveListener() {
		Autoreply.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					for (PersonInfo cb : ConfigManager.getPersonInfo()) {
						checkPerson(cb);
					}
					loadFinish = true;
				}
			});
        File liveTimeFile = new File(Autoreply.appDirectory + "liveTime.json");
        if (!liveTimeFile.exists()) {
            saveLiveTime();
		}
        try {
            liveTimeMap = new Gson().fromJson(Tools.FileTool.readString(liveTimeFile), new TypeToken<ConcurrentHashMap<String, Long>>() {}.getType());
		} catch (Exception e) {
            e.printStackTrace();
		}
	}

    private void checkPerson(PersonInfo personInfo) {
        if (personInfo.bliveRoom == -1) {
            return;
		}
        if (personInfo.bliveRoom == 0) {
            if (personInfo.bid != 0) {
                SpaceToLiveJavaBean sjb = new Gson().fromJson(Tools.BilibiliTool.getLiveRoomInfo(personInfo.bid), SpaceToLiveJavaBean.class);
                if (sjb.data.roomid == 0) {
                    personInfo.bliveRoom = -1;
                    ConfigManager.saveConfig();
                    return;
				}
                personInfo.bliveRoom = sjb.data.roomid;
                ConfigManager.saveConfig();
                System.out.println("检测到用户" + personInfo.name + "(" + personInfo.bid + ")的直播间" + personInfo.bliveRoom);
                try {
                    Thread.sleep(100);
				} catch (InterruptedException e) {
                    e.printStackTrace();
				}
			}
		}
	}

    @Override
    public void run() {
        while (true) {
            try {
                if (!loadFinish) {
                    Thread.sleep(1000);
                    continue;
				}
                for (PersonInfo personInfo : ConfigManager.getPersonInfo()) {
                    if (personInfo.bliveRoom == 0 || personInfo.bliveRoom == -1) {
                        continue;
					}
                    SpaceToLiveJavaBean sjb = new Gson().fromJson(Tools.BilibiliTool.getLiveRoomInfo(personInfo.bid), SpaceToLiveJavaBean.class);
                    boolean living = sjb.data.liveStatus == 1;
					if (living) {
						if (Autoreply.instance.danmakuListenerManager.getListener(personInfo.bliveRoom) == null) {
							DanmakuListener dl=new DanmakuListener(new URI("wss://broadcastlv.chat.bilibili.com:2245/sub"), personInfo);
							dl.connect();
							Autoreply.instance.danmakuListenerManager.listener.add(dl);
						}
					} else {
						DanmakuListener dl=Autoreply.instance.danmakuListenerManager.getListener(personInfo.bliveRoom);
						if (dl != null) {
							dl.close();
						} 
					}
                    LivePerson livePerson =livePersonMap.get(personInfo.bid);
					if (livePerson == null) {
						livePerson = new LivePerson();
						livePersonMap.put(personInfo.bid,livePerson);
					} 
					livePerson.liveStartTimeStamp = System.currentTimeMillis();
                    livePerson.liveUrl = sjb.data.url;
					livePerson.roomID = sjb.data.roomid;
                    if (livePerson.needTip) {
                        if (!livePerson.lastStatus && living) {
                            onStart(personInfo, livePerson);
						} else if (livePerson.lastStatus && !living) {
                            onStop(personInfo, livePerson);
						}
					}
                    livePerson.lastStatus = living;
                    livePerson.needTip = true;
                    Thread.sleep(1000);
				}
			} catch (Exception e) {
                System.out.println("直播监视出了问题：");
                e.printStackTrace();
			}
		}
	}

    private void onStart(PersonInfo personInfo, LivePerson livePerson) {
        livePerson.liveStartTimeStamp = System.currentTimeMillis();
        tipStart(personInfo);
	}

    private void onStop(PersonInfo personInfo, LivePerson livePerson) {
        countLiveTime(personInfo, livePerson);
        tipFinish(personInfo);
	}

    private void countLiveTime(PersonInfo personInfo, LivePerson livePerson) {
		if (liveTimeMap.get(personInfo.name) == null) {
			liveTimeMap.put(personInfo.name, 0L);
		}
        long time = liveTimeMap.get(personInfo.name);
		time = time + (System.currentTimeMillis() - livePerson.liveStartTimeStamp);
		liveTimeMap.put(personInfo.name, time);
        saveLiveTime();
	}

    private void tipStart(PersonInfo p) {
		RitsukageDataPack dp=RitsukageDataPack.encode(RitsukageDataPack._4liveStart, System.currentTimeMillis());
		dp.write(1, p.bliveRoom);
		dp.write(1, p.name);
		Autoreply.instance.connectServer.broadcast(dp.getData());
		SanaeDataPack sdp=SanaeDataPack.encode(SanaeDataPack.opLiveStart);
		sdp.write(p.name).write(p.bliveRoom);
		Autoreply.instance.sanaeServer.send(sdp);
		        Autoreply.sendMessage(Autoreply.mainGroup, 0, p.name + "开始直播" + p.bliveRoom);
        	}

    private void tipFinish(PersonInfo p) {
		RitsukageDataPack dp=RitsukageDataPack.encode(RitsukageDataPack._5liveStop, System.currentTimeMillis());
		dp.write(1, p.bliveRoom);
		dp.write(1, p.name);
		Autoreply.instance.connectServer.broadcast(dp.getData());
		SanaeDataPack sdp=SanaeDataPack.encode(SanaeDataPack.opLiveStop);
		sdp.write(p.name).write(p.bliveRoom);
		Autoreply.instance.sanaeServer.send(sdp);
		        Autoreply.sendMessage(Autoreply.mainGroup, 0, p.name + "直播结束" + p.bliveRoom);
        	}

    public String getLiveTimeCount() {
        Iterator<Entry<String, Long>> iterator = liveTimeMap.entrySet().iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            Entry<String, Long> entry = iterator.next();
            stringBuilder.append(entry.getKey()).append(cal(entry.getValue())).append("\n");
		}
        return stringBuilder.toString();
	}

    private String cal(long miSec) {
		long second=miSec / 1000;
        long h = 0;
        long min = 0;
        long temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp > 60) {
                min = temp / 60;
			}
		} else {
            min = second / 60;
		}
        if (h == 0) {
            return min + "分";
		} else {
            return h + "时" + min + "分";
		}
	}
	
    private void saveLiveTime() {
        try {
            File file = new File(Autoreply.appDirectory + "liveTime.json");
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(new Gson().toJson(liveTimeMap));
            writer.flush();
            fos.close();
		} catch (IOException e) {
            e.printStackTrace();
		}
	}
}
