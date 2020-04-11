package rep;

import java.io.*;
import java.util.*;

public class Replay {

	public String path;
	public ByteReader ReplayData;
	public ReplayInfo info;
	public Replay(String path) {
		this.path = path;
		ReplayData = new ByteReader(Tools.readByteArray(new File(path)));
		info = ReadTitle();
	}

	private ReplayInfo ReadTitle() {
		List<String> listNum = null ;
		try {
			BufferedReader bf = new BufferedReader(new FileReader(new File(path)));
			String line;
			listNum = new ArrayList<String>();
			while (!"".equals(line = bf.readLine()) && line != null) {
				listNum.add(line);	
			}
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] strArray1 = listNum.toArray(new String[listNum.size()]);
		try {
			//int num = 0;
			//do { } while(!strArray1[++num].equals("ReplayInformation") && num < strArray1.length - 1);
			//其实就是跳过第一行 直接num=1即可
			int num=1;
			ReplayInfo replayInfo = new ReplayInfo();
			replayInfo.Version = strArray1[num + 1];
			replayInfo.PlayerName = strArray1[num + 2];
			replayInfo.Date = strArray1[num + 3];
			replayInfo.Time = strArray1[num + 4];
			replayInfo.MyPlaneName = strArray1[num + 5];
			replayInfo.WeaponType = strArray1[num + 6];
			replayInfo.Rank = DifficultLevel.valueOf(Integer.parseInt(strArray1[num + 7]));
			replayInfo.StartStage = strArray1[num + 8];
			replayInfo.LastStage = strArray1[num + 9];
			replayInfo.SlowRate = strArray1[num + 10];
			for (int index = num + 11;index < strArray1.length;++index) {
				String[] strArray2 = strArray1[index].split("\t");
				MyPlaneInfo myPlaneInfo = new MyPlaneInfo();
				myPlaneInfo.Life = Integer.parseInt(strArray2[0]);
				myPlaneInfo.Spell = Integer.parseInt(strArray2[1]);
				myPlaneInfo.Power = Integer.parseInt(strArray2[2]);
				myPlaneInfo.Score = Long.parseLong(strArray2[3]);
				myPlaneInfo.Graze = Integer.parseInt(strArray2[4]);
				myPlaneInfo.PosX = Float.parseFloat(strArray2[5]);
				myPlaneInfo.PosY = Float.parseFloat(strArray2[6]);
				myPlaneInfo.LifeChip = Integer.parseInt(strArray2[7]);
				myPlaneInfo.SpellChip = Integer.parseInt(strArray2[8]);
				myPlaneInfo.LifeUpCount = Integer.parseInt(strArray2[9]);
				myPlaneInfo.StarPoint = Integer.parseInt(strArray2[10]);
				myPlaneInfo.HighItemScore = Integer.parseInt(strArray2[11]);
				myPlaneInfo.Rate = Float.parseFloat(strArray2[12]);
				myPlaneInfo.LastColor = EnchantmentType.valueOf(Integer.parseInt(strArray2[13]));
				myPlaneInfo.DataPosition = Long.parseLong(strArray2[14]);
				replayInfo.MyPlaneData.add(myPlaneInfo);
			}
			return replayInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return new ReplayInfo();
		}
	}

	public int ReadKey() {
		return ReplayData.readUShort();
	} 
}
