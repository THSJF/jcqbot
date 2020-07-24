package com.meng.tools;

import com.meng.*;
import java.util.concurrent.*;

public class MessageDeleter {
	
	public static void autoDelete(final int msgId, int second) {
		SJFExecutors.executeAfterTime(new Runnable(){

				@Override
				public void run() {
					Autoreply.CQ.deleteMsg(msgId);
				}
			}, second, TimeUnit.SECONDS);
	}
	
	public static void autoDelete(final int msgId) {
		SJFExecutors.executeAfterTime(new Runnable(){

				@Override
				public void run() {
					Autoreply.CQ.deleteMsg(msgId);
				}
			}, 60, TimeUnit.SECONDS);
	}
}
