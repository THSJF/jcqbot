package com.meng.config;

import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.tools.*;
import java.io.*;
import java.nio.charset.*;

public class DataPersistenter {

	private DataPersistenter() {
		
	}

	public static boolean save(INeedPersistent pb) {
		try {
            File file = new File(Autoreply.appDirectory + pb.getPersistentName());
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(Autoreply.gson.toJson(pb.getDataBean()));
            writer.flush();
            fos.close();
			return true;
        } catch (IOException e) {
            e.printStackTrace();
			return false;
        }
	}

	public static boolean read(INeedPersistent pb) {    
        try {
			pb.setDataBean(Autoreply.gson.fromJson(Tools.FileTool.readString(Autoreply.appDirectory + pb.getPersistentName()), pb.getDataClass()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
