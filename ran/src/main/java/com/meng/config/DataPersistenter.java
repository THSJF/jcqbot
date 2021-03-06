package com.meng.config;

import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.sjfmd.libs.*;
import com.meng.tools.*;
import java.io.*;
import java.nio.charset.*;

public class DataPersistenter {

	private DataPersistenter() {
		
	}

	public static boolean save(IPersistentData pb) {
		try {
            File file = new File(Autoreply.appDirectory + pb.getPersistentName());
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(GSON.toJson(pb.getDataBean()));
            writer.flush();
            fos.close();
			return true;
        } catch (IOException e) {
            e.printStackTrace();
			return false;
        }
	}

	public static boolean read(IPersistentData pb) {    
        try {
			pb.setDataBean(GSON.fromJson(FileTool.readString(Autoreply.appDirectory + pb.getPersistentName()), pb.getDataType()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
