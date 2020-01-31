package com.meng.modules;

import java.io.*;

public abstract class BaseModule {

	public boolean enable = false;
	private String moduleName = null;

	public final boolean onMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (!enable) {
			return false;
		}
		return processMsg(fromGroup, fromQQ, msg, msgId, imgs);
	}
	public final String getModuleName() {
		if (moduleName == null) {
			moduleName = getClass().getName();
		}
		return moduleName;
	}
	public abstract BaseModule load();
	protected abstract boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs);
}
