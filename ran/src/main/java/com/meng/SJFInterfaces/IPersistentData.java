package com.meng.SJFInterfaces;
import java.lang.reflect.*;

public interface IPersistentData {
	public String getPersistentName();
	public Type getDataType();
	public Object getDataBean();
	public void setDataBean(Object o);
}
