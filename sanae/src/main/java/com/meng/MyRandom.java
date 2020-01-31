package com.meng;
import java.util.*;

public class MyRandom extends Random {

	/**
	 * 
	 */
	private static final long serialVersionUID = 36887219237309276L;

	@Override
	public int nextInt() {
		return  (int)(System.currentTimeMillis() % (super.nextInt() + 1));
	}

	@Override
	public int nextInt(int n) {
		return (int)(System.currentTimeMillis() % (super.nextInt(n) + 1));
	}

}
