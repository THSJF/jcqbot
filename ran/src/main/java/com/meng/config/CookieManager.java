package com.meng.config;

import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.sjfmd.libs.*;
import com.meng.tools.*;
import java.io.*;
import java.nio.charset.*;
import java.util.concurrent.*;

public class CookieManager {

	public ConcurrentHashMap<Integer,String> cookieMap=new ConcurrentHashMap<>();

	public CookieManager() {
		File jsonBaseConfigFile = new File(Autoreply.appDirectory + "cookieMap.json");
        if (!jsonBaseConfigFile.exists()) {
            saveConfig();
		}
        cookieMap = GSON.fromJson(FileTool.readString(Autoreply.appDirectory + "cookieMap.json"), new TypeToken<ConcurrentHashMap<Integer,String>>() {}.getType());
	}

	public void setGrzx(String grzx) {
		cookieMap.put(424494698, grzx);
	}

	public String getGrzx() {
		return cookieMap.get(424494698);
	}

	public void setHina(String hina) {
		cookieMap.put(64483321, hina);
	}

	public String getHina() {
		return cookieMap.get(64483321);
	}

	public void setStar(String star) {
		cookieMap.put(424461971, star);
	}

	public String getStar() {
		return cookieMap.get(424461971);
	}

	public void setLuna(String luna) {
		cookieMap.put(424444960, luna);
	}

	public String getLuna() {
		return cookieMap.get(424444960);
	}

	public void setSunny(String sunny) {
		cookieMap.put(424436973, sunny);
	}

	public String getSunny() {
		return cookieMap.get(424436973);
	}

	public void saveConfig() {
        try {
            File file = new File(Autoreply.appDirectory + "cookieMap.json");
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(GSON.toJson(cookieMap));
            writer.flush();
            fos.close();
		} catch (IOException e) {
            e.printStackTrace();
		}  
	}
}
