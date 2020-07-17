package com.meng.SJFInterfaces;

public interface IPersistentData {
	public String getPersistentName();
	public Class<?> getDataClass();
	public Object getDataBean();
	public void setDataBean(Object o);
}
