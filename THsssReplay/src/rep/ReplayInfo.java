package rep;

import java.lang.reflect.*;
import java.util.*;

public class ReplayInfo {
    public ArrayList<MyPlaneInfo> MyPlaneData = new ArrayList<MyPlaneInfo>();
    public String Version;
    public String PlayerName;
    public String MyPlaneName;
    public String Date;
    public String Time;
    public String WeaponType;
    public DifficultLevel Rank;
    public String StartStage;
    public String LastStage;
    public String SlowRate;

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		Field[] fs = this.getClass().getDeclaredFields();
		for (Field field : fs) {
			String name = field.getName();
			try {
				sb.append(name).append("=").append(field.get(this)).append("\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
