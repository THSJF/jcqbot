package com.meng.sjfmd.libs;

import com.meng.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class ExceptionCatcher implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Map<String,String> paramsMap=new HashMap<>();
    private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private String TAG=this.getClass().getSimpleName();
    private static ExceptionCatcher mInstance;
	private String fileName;

    private ExceptionCatcher() {
	}

    public static synchronized ExceptionCatcher getInstance() {
        if (null == mInstance) {
            mInstance = new ExceptionCatcher();
		}
        return mInstance;
	}

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
	}

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
		} else {
            try {
                Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
            System.exit(0);
		}
	}

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
		}
        collectDeviceInfo();
        addCustomInfo();
        saveCrashInfo2File(ex);
        return true;
	}

    public void collectDeviceInfo() {

	}

    private void addCustomInfo() {

	}

    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb=new StringBuffer();
        for (Map.Entry<String,String> entry : paramsMap.entrySet()) {
            String key=entry.getKey();
            String value=entry.getValue();
            sb.append(key + "=" + value + "\n");
		}

        Writer writer=new StringWriter();
        PrintWriter printWriter=new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause=ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
		}
        printWriter.close();
        String result=writer.toString();
        sb.append(result);
        try {
            long timestamp=System.currentTimeMillis();
            String time=format.format(new Date());
			fileName = "crash-" + time + "-" + timestamp + ".log";
			String path=Autoreply.appDirectory + "/crash/";
			File dir=new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileOutputStream fos=new FileOutputStream(path + fileName);
			fos.write(sb.toString().getBytes());
			fos.close();
            return fileName;
		} catch (Exception e) {

		}
        return null;
	}
}
