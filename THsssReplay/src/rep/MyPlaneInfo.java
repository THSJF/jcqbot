package rep;

import java.lang.reflect.*;

public class MyPlaneInfo {
    public int Life;
    public int Spell;
    public int Power;
    public long Score;
    public int Graze;
    public float PosX;
    public float PosY;
    public int LifeChip;
    public int SpellChip;
    public int LifeUpCount;
    public int StarPoint;
    public int HighItemScore;
    public float Rate;
    public EnchantmentType LastColor;
    public long DataPosition;

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("\n");
		Field[] fs = this.getClass().getDeclaredFields();
		for (Field field : fs) {
			String name = field.getName();
			try {
				sb.append(name).append(" = ").append(field.get(this)).append("\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
