package com.meng.modules;

import com.meng.*;
import com.meng.config.*;
import com.meng.config.javabeans.*;
import com.meng.SJFInterfaces.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ThreeManager extends BaseGroupModule {

	private HashMap<Long,Boolean> changeMap=new HashMap<>();
	private HashSet<Long> checkSet=new HashSet<>();

	@Override
    public ThreeManager load() {
		checkSet.add(2487765013L);
		checkSet.add(1033317031L);
		checkSet.addAll(ConfigManager.instance.configJavaBean.adminList);
		checkSet.addAll(ConfigManager.instance.configJavaBean.masterList);
		for (PersonInfo pi:ConfigManager.instance.configJavaBean.personInfo) {
			if (pi.qq != 0) {
				checkSet.add(pi.qq);
			}
		}
        Autoreply.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					while (true) {
						for (long fromQQ:checkSet) {
							if (changeMap.get(fromQQ) != null && changeMap.get(fromQQ)) {
								return;
							}
							File folder = new File(Autoreply.appDirectory + "user\\");
							if (!folder.exists()) {
								folder.mkdirs();
							}
							File headImageFile = new File(Autoreply.appDirectory + "user\\" + fromQQ + ".jpg");
							if (!headImageFile.exists()) {
								downloadHead(fromQQ, "");
								changeMap.put(fromQQ, false);
								return;
							}
							if (downloadHead(fromQQ, "a").length() == headImageFile.length()) {
								changeMap.put(fromQQ, false);
								return;
							}
							changeMap.put(fromQQ, true);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {}
						}
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e) {

						}
					}
				}
			});
		return this;
    }

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (changeMap.get(fromQQ) != null && changeMap.get(fromQQ)) {
			Autoreply.sendMessage(fromGroup, fromQQ, Autoreply.instance.CC.image(ModuleManager.instance.getGroupModule(MPicEdit.class).jingShenZhiZhuByAt(fromGroup, 0, Autoreply.instance.CC.at(fromQQ))));
			changeMap.put(fromQQ, false);
			return true;
		}
		return false;
	}

	private File downloadHead(long qq, String a) {
        URL url;
		File headImageFile;
        try {
            url = new URL("http://q2.qlogo.cn/headimg_dl?bs=" + qq + "&dst_uin=" + qq + "&dst_uin=" + qq + "&;dst_uin=" + qq + "&spec=5&url_enc=0&referer=bu_interface&term_type=PC");
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
			headImageFile = new File(Autoreply.appDirectory + "user\\" + a + qq + ".jpg");
			FileOutputStream fileOutputStream = new FileOutputStream(headImageFile);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
			return null;
        }
		return headImageFile;
	}
}
