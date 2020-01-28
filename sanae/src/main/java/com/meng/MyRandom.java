package com.meng;
import java.util.*;

public class MyRandom extends Random {

	@Override
	public int nextInt() {
		return  (int)(System.currentTimeMillis() % super.nextInt());
	}

	@Override
	public int nextInt(int n) {
		return (int)(System.currentTimeMillis() % super.nextInt(n));
	}

}
