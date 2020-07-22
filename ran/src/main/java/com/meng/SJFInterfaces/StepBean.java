package com.meng.SJFInterfaces;

import java.util.*;

public class StepBean<V> {
	
	private int step = 0;
	private V value;
	
	public void incStep() {
		++step;
	}

	public int getStep() {
		return step;
	}

	public V get() {
		return value;
	}

	public void setValue(V v) {
		value = v;
	}
}
