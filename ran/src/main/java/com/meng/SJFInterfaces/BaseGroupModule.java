package com.meng.SJFInterfaces;

public abstract class BaseGroupModule extends BaseModule implements IGroupMessage {

	@Override
	public abstract boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId);
}
