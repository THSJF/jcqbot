package com.meng.SJFInterfaces;

public interface INeedPersistent {
	public String getPersistentName();
	public Class<?> getDataClass();
	public Object getDataBean();
	public void setDataBean(Object o);
}
