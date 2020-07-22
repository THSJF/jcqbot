package com.meng.modules;

import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.config.javabeans.*;
import com.meng.sjfmd.libs.*;
import com.meng.tools.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class MCoinManager extends BaseGroupModule {
	private HashMap<Long, Integer> countMap = new HashMap<>();
	private File file;

	@Override
	public MCoinManager load() {
		file = new File(Autoreply.appDirectory + "properties\\coins.json");
		if (!file.exists()) {
			saveData();
		}
		countMap = GSON.fromJson(FileTool.readString(file), new TypeToken<HashMap<Long, Integer>>() {}.getType());
		SJFExecutors.execute(new Runnable() {
				@Override
				public void run() {
					backupData();
				}
			});
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (msg.equals("~coins")) {
			Autoreply.sendMessage(fromGroup, 0, "你有" + getCoinsCount(fromQQ) + "个幻币");
			return true;
		}
		return false;
	}

	public void addCoins(long fromQQ, int coins) {
		if (coins < 0) {
			return;
		}
		if (countMap.get(fromQQ) != null) {
			int qqCoin=countMap.get(fromQQ);
			qqCoin += coins;
			countMap.put(fromQQ, qqCoin);
		} else {
			countMap.put(fromQQ, coins);
		}
		saveData();
	}

	public boolean subCoins(long fromQQ, int coins) {
		if (coins < 0) {
			return false;
		}
		if (countMap.get(fromQQ) != null) {
			int qqCoin=countMap.get(fromQQ);
			if (qqCoin < coins) {
				return false;
			}
			qqCoin -= coins;
			countMap.put(fromQQ, qqCoin);
			saveData();
			return true;
		}
		return false;
	}

	public int getCoinsCount(long fromQQ) {
		if (countMap.get(fromQQ) == null) {
			return 0;
		}
		return countMap.get(fromQQ);
	}

	public void exchangeCoins(long fromGroup, long fromQQ, int coins) {
		if (subCoins(fromQQ, coins)) {
			RitsukageDataPack rdp=RitsukageDataPack.encode(RitsukageDataPack._14coinsAdd, System.currentTimeMillis());
			rdp.write(1, fromQQ);
			Autoreply.instance.connectServer.broadcast(rdp.getData());
			//	Autoreply.sendMessage(1023432971L, 0, "~addcoins " + coins + " " + fromQQ);
			Autoreply.sendMessage(fromGroup, 0, "兑换" + coins + "个幻币至小律影");
		} else {
			Autoreply.sendMessage(fromGroup, 0, "兑换失败");
		}
	}

	private void saveData() {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			writer.write(GSON.toJson(countMap));
			writer.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void backupData() {
		while (true) {
			try {
				Thread.sleep(86400000);
				File backup = new File(file.getAbsolutePath() + ".bak" + System.currentTimeMillis());
				FileOutputStream fos = new FileOutputStream(backup);
				OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
				writer.write(GSON.toJson(countMap));
				writer.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
