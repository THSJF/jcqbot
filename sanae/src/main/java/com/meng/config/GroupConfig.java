package com.meng.config;

import java.util.*;

public class GroupConfig extends Object {
    public long groupNumber = 0;
	public int f1=0;
    public int repeatMode = 0;

	@Override
	public int hashCode() {
		int i=0;
		return super.hashCode();
	}

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GroupConfig)) {
            return false;
        }
        GroupConfig p = (GroupConfig) obj;
        return this.groupNumber == p.groupNumber;
    }
}
