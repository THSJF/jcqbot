package com.meng.config;

import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.concurrent.*;

public class CookieManager {

    public Cookie cookie=new Cookie();

	public CookieManager() {
		File jsonBaseConfigFile = new File(Autoreply.appDirectory + "cookie.json");
        if (!jsonBaseConfigFile.exists()) {
            saveConfig();
		}
        Type type = new TypeToken<Cookie>() {
		}.getType();
        cookie = Autoreply.gson.fromJson(Tools.FileTool.readString(Autoreply.appDirectory + "cookie.json"), type);
	}

	public void setGrzx(String grzx) {
		cookie.cookieMap.put(424494698, grzx);
	}

	public String getGrzx() {
		return cookie.cookieMap.get(424494698);
	}

	public void setHina(String hina) {
		cookie.cookieMap.put(64483321, hina);
	}

	public String getHina() {
		return cookie.cookieMap.get(64483321);
	}

	public void setStar(String star) {
		cookie.cookieMap.put(424461971, star);
	}

	public String getStar() {
		return cookie.cookieMap.get(424461971);
	}

	public void setLuna(String luna) {
		cookie.cookieMap.put(424444960, luna);
	}

	public String getLuna() {
		return cookie.cookieMap.get(424444960);
	}

	public void setSunny(String sunny) {
		cookie.cookieMap.put(424436973, sunny);
	}

	public String getSunny() {
		return cookie.cookieMap.get(424436973);
	}

	public void saveConfig() {
        try {
            File file = new File(Autoreply.appDirectory + "cookie.json");
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(Autoreply.gson.toJson(cookie));
            writer.flush();
            fos.close();
		} catch (IOException e) {
            e.printStackTrace();
		}  
	}

	public class Cookie {
		public ConcurrentHashMap<Integer,String> cookieMap=new ConcurrentHashMap<>();
	}
}
