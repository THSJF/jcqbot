package com.meng.tools;

import com.google.gson.reflect.*;
import com.meng.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import com.meng.modules.*;

public class MCoinManager extends BaseModule {
	private HashMap<Long, Integer> countMap = new HashMap<>();
	private File file;

	@Override
	public BaseModule load() {
		file = new File(Autoreply.appDirectory + "properties\\coins.json");
		if (!file.exists()) {
			saveData();
		}
		Type type = new TypeToken<HashMap<Long, Integer>>() {
		}.getType();
		countMap = Autoreply.gson.fromJson(Tools.FileTool.readString(file), type);
		Autoreply.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					backupData();
				}
			});
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
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
			Autoreply.sendMessage(1023432971L, 0, "~addcoins " + coins + " " + fromQQ);
			Autoreply.sendMessage(fromGroup, 0, "兑换" + coins + "个幻币至小律影");
		} else {
			Autoreply.sendMessage(fromGroup, 0, "兑换失败");
		}
	}

	private void saveData() {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			writer.write(Autoreply.gson.toJson(countMap));
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
				writer.write(Autoreply.gson.toJson(countMap));
				writer.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
