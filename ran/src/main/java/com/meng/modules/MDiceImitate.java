package com.meng.modules;

import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.config.*;
import com.meng.gameData.TouHou.zun.*;
import com.meng.sjfmd.libs.*;
import com.meng.tools.*;
import java.util.*;

public class MDiceImitate extends BaseGroupModule {
	public static String[] spells;
	public static String[] neta;
	public static String[] music;
	public static String[] name;

	private static String[] pl01 = new String[]{"别打砖块了，来飞机"};
	private static String[] pl02 = new String[]{"范围重视型", "高灵击伤害 平衡型", "威力重视型"};
    private static String[] pl03 = new String[]{"博丽灵梦", "魅魔", "雾雨魔理沙", "爱莲", "小兔姬", "卡娜·安娜贝拉尔", "朝仓理香子", "北白河千百合", "冈崎梦美"};
    private static String[] pl04 = new String[]{"博丽灵梦 诱导", "博丽灵梦 大范围", "雾雨魔理沙 激光", "雾雨魔理沙 高速射击"};
    private static String[] pl05 = new String[]{"博丽灵梦", "雾雨魔理沙", "魅魔", "幽香"};
    private static String[] pl09 = new String[]{"博丽灵梦", "雾雨魔理沙", "十六夜咲夜", "魂魄妖梦", "铃仙·优昙华院·因幡", "琪露诺", "莉莉卡·普莉兹姆利巴", "梅露兰·普莉兹姆利巴", "露娜萨·普莉兹姆利巴", "米斯蒂娅·萝蕾拉", "因幡帝", "射命丸文", "梅蒂欣·梅兰可莉", "风见幽香", "小野冢小町", "四季映姬·亚玛萨那度"};
 	private static String[] plDiff = new String[]{"easy", "normal", "hard", "lunatic"};

	public static HashSet<String> cat=new HashSet<>();
	public static HashSet<String> memory=new HashSet<>();
	public static HashSet<String> pachouli=new HashSet<>();

	@Override
	public MDiceImitate load() {
		spells = new String[]{};
		spells = Tools.ArrayTool.mergeArray(spells, 
											TH06GameData.spells,
											TH07GameData.spells,
											TH08GameData.spells,
											TH10GameData.spells,
											TH11GameData.spells,
											TH12GameData.spells,
											TH13GameData.spells,
											TH14GameData.spells,
											TH15GameData.spells,
											TH16GameData.spells,
											TH17GameData.spells);
		neta = new String[]{
			"红lnb",
			"红lnm",
			"妖lnm",
			"妖lnn",
			"永lnm",
			"风lnm",
			"风lnn",
			"殿lnm",
			"船lnm",
			"船lnn",
			"庙lnm",
			"城lnm",
			"绀lnm",
			"璋lnn"};
		music = new String[]{
			//th4
			"bad apple",
		};
		music = Tools.ArrayTool.mergeArray(music,
										   TH06GameData.musicName,
										   TH07GameData.musicName,
										   TH08GameData.musicName,
										   TH10GameData.musicName,
										   TH11GameData.musicName,
										   TH12GameData.musicName,
										   TH13GameData.musicName,
										   TH14GameData.musicName,
										   TH15GameData.musicName,
										   TH16GameData.musicName,
										   TH17GameData.musicName);
		name = new String[]{
			//th2
			"里香",
			"明罗",
			"魅魔",
			//th3
			"爱莲", 
			"小兔姬", 
			"卡娜·安娜贝拉尔",
			"朝仓理香子", 
			"北白河千百合", 
			"冈崎梦美",
			//th4
			"奥莲姬",
			"胡桃",
			"艾丽",
			"梦月",
			"幻月",
			//th5
			"萨拉",
			"露易兹",
			"雪",
			"舞",
			"梦子",
			"神绮"};
		name = Tools.ArrayTool.mergeArray(name,
										  TH06GameData.charaName,
										  TH07GameData.charaName,
										  TH08GameData.charaName,
										  new String[]{
											  //th9
											  "梅蒂欣·梅兰可莉",
											  "风见幽香",
											  "小野冢小町",
											  "四季映姬"},
										  TH10GameData.charaName,
										  TH11GameData.charaName,
										  TH12GameData.charaName,
										  new String[]{
											  //th12.8
											  "桑尼·米尔克",
											  "露娜·切露德",
											  "斯塔·萨菲雅"},
										  TH13GameData.charaName,
										  new String[]{
											  //th13.5
											  "秦心"},
										  TH14GameData.charaName,
										  new String[]{
											  //th14.5
											  "宇佐见堇子"},
										  TH15GameData.charaName,
										  new String[]{
											  //th15.5
											  "依神紫苑",
											  "依神女苑"},
										  TH16GameData.charaName,
										  TH17GameData.charaName);
		addArrayToSet(memory, "想起「二重黑死蝶」", "想起「粼粼水底之心伤」");
		addArrayToSet(pachouli, "火符「火神之光」", "土&金符「翡翠巨石」");
		addArrayToSet(pachouli, "月符「静息的月神」", "火水木金土符「贤者之石」");

		Collections.addAll(cat, new String[]{
							   "仙符「凤凰卵」",
							   "仙符「凤凰展翅」",
							   "式符「飞翔晴明」",
							   "阴阳「道满晴明」",	
							   "阴阳「晴明大纹」",
							   "天符「天仙鸣动」",
							   "翔符「飞翔韦驮天」",
							   "童符「护法天童乱舞」",
							   "仙符「尸解永远」",
							   "鬼符「鬼门金神」",
							   "方符「奇门遁甲」",
							   "鬼符「青鬼赤鬼」",
							   "鬼神「飞翔毘沙门天」",
							   "猫符「猫的步伐」",
							   "猫符「怨灵猫乱步」",
							   "咒精「僵尸妖精」",
							   "咒精「怨灵凭依妖精」",
							   "恨灵「脾脏蛀食者」",
							   "尸灵「食人怨灵」",
							   "赎罪「旧地狱的针山」",
							   "赎罪「古时之针与痛楚的怨灵」",
							   "「死灰复燃」",
							   "「小恶灵复活」",
							   "妖怪「火焰的车轮」"
						   });
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		String[] ss = msg.split("\\.");
        if (ss.length > 1) {
            if (ss[0].equals("roll")) {
                switch (ss[1]) {
                    case "pl":
                    case "plane":
                    case "player":
                        if (ss.length == 3) {
                            rollPlane(ss[2], fromGroup);
                        } else if (ss.length == 4) {
                            rollPlane(ss[2] + "." + ss[3], fromGroup);
                        }
                        break;
                    case "游戏":
                    case "game":
                        Autoreply.sendMessage(fromGroup, 0, "th" + (Autoreply.instance.random.nextInt(16) + 1));
                        break;
                    case "diff":
                    case "difficult":
                    case "难度":
                        Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(plDiff));
                        break;
                    case "stage":
                    case "关卡":
                    case "面数":
                        rollStage(ss, fromGroup);
                        break;
                    case "help":
                    case "帮助":
                        String str = "\nroll.game roll.游戏 可以随机选择游戏\nroll.difficult roll.diff roll.难度 可以随机选择难度\nroll.player roll.pl roll.plane接作品名或编号可随机选择机体（仅官方整数作）\nroll.stage roll.关卡 roll.面数 加玩家名可用来接力时随机选择面数，多个玩家名之间用.隔开\n";
                        Autoreply.sendMessage(fromGroup, 0, str);
                        break;
                }
                return true;
            }
        }
		String pname=ConfigManager.getNickName(fromGroup, fromQQ);
		String md5=Hash.getMd5Instance().calculate(String.valueOf(fromQQ + System.currentTimeMillis() / (24 * 60 * 60 * 1000)));
		char c=md5.charAt(0);
		switch (msg) {
			case ".jrrp":
				float fpro=0f;
				if (c == '0') {
					fpro = 99.61f;
				} else if (c == '1') {
					fpro = 97.60f;
				} else {
					fpro = ((float)(md5Random(fromQQ) % 10001)) / 100;
				}
				Autoreply.sendMessage(fromGroup, 0, String.format("%s今天会在%.2f%%处疮痍", pname, fpro));
				return true;	
			case "。jrrp":
				Autoreply.sendMessage(fromGroup, 0, String.format("%s今天会在%s疮痍", pname, md5RanStr(fromQQ, spells)));
		   		return true;
		}

		if (msg.startsWith(".draw")) {
			String drawcmd=msg.substring(6);
			switch (drawcmd) {
				case "help":
					Autoreply.sendMessage(fromGroup, 0, "当前有:spell neta music grandma game all");
					return true;
				case "spell":
					Autoreply.sendMessage(fromGroup, 0, spells[new Random().nextInt(spells.length)]);
					return true;
				case "neta":
					Autoreply.sendMessage(fromGroup, 0, String.format("%s今天宜打%s", pname, md5RanStr(fromQQ, neta)));
					return true;
				case "music":
					Autoreply.sendMessage(fromGroup, 0, String.format("%s今天宜听%s", pname, md5RanStr(fromQQ, music)));
					return true;
				case "grandma":
					if (Hash.getMd5Instance().calculate(String.valueOf(fromQQ + System.currentTimeMillis() / (24 * 60 * 60 * 1000))).charAt(0) == '0') {
						Autoreply.sendMessage(fromGroup, 0, String.format("%s今天宜认八云紫当奶奶", pname));
						return true;
					}
					Autoreply.sendMessage(fromGroup, 0, String.format("%s今天宜认%s当奶奶", pname, md5RanStr(fromQQ, name)));
					return true;
				case "game":
					String s=randomGame(pname, fromQQ, true);
					s += ",";
					s += randomGame(pname, fromQQ + 1, false);
					Autoreply.sendMessage(fromGroup, 0, s);
					return true;
				case "jrrp":
					Autoreply.sendMessage(fromGroup, 0, String.format("%s今天会在%s疮痍", pname, md5RanStr(fromQQ, spells)));
					return true;
				case "all":
					String sss=String.format("%s今天宜打%s", pname, md5RanStr(fromQQ, neta));
					sss += "\n";
					sss += String.format("%s今天宜听%s", pname, md5RanStr(fromQQ, music));
					sss += "\n";
					if (Hash.getMd5Instance().calculate(String.valueOf(fromQQ + System.currentTimeMillis() / (24 * 60 * 60 * 1000))).charAt(0) == '0') {
						sss += String.format("%s今天宜认八云紫当奶奶", pname);
					} else {
						sss += String.format("%s今天宜认%s当奶奶", pname, md5RanStr(fromQQ, name));
					}
					sss += "\n";
					sss += randomGame(pname, fromQQ, true);
					sss += ",";
					sss += randomGame(pname, fromQQ + 1, false);
					sss += "\n";
					float fpro=0f;
					if (c == '0') {
						fpro = 99.61f;
					} else if (c == '1') {
						fpro = 97.60f;
					} else {
						fpro = ((float)(md5Random(fromQQ) % 10001)) / 100;
					}
					sss += String.format("%s今天会在%.2f%%处疮痍", pname, fpro);
					Autoreply.sendMessage(fromGroup, 0, sss);
					return true;			
				default:
					Autoreply.sendMessage(fromGroup, 0, "可用.draw help查看帮助");
			}	
			return true;
		}

		return false;
	}

	private String randomGame(String pname, long fromQQ, boolean goodAt) {
		int gameNo=md5Random(fromQQ) % 16 + 2;
		String gameName = null;
		String charaName = null;
		switch (gameNo) {
			case 2:
				gameName = "封魔录";
				charaName = md5RanStr(fromQQ + 2, pl02);
				break;
			case 3:
				gameName = "梦时空";
				charaName = md5RanStr(fromQQ + 2, pl03);
				break;
			case 4:
				gameName = "幻想乡";
				charaName = md5RanStr(fromQQ + 2, pl04);
				break;
			case 5:
				gameName = "怪绮谈";
				charaName = md5RanStr(fromQQ + 2, pl05);
				break;
			case 6:
				gameName = "红魔乡";
				charaName = md5RanStr(fromQQ + 2, TH06GameData.players);
				break;
			case 7:
				gameName = "妖妖梦";
				charaName = md5RanStr(fromQQ + 2, TH07GameData.players);
				break;
			case 8:
				gameName = "永夜抄";
				charaName = md5RanStr(fromQQ + 2, TH08GameData.players);
				break;
			case 9:
				gameName = "花映冢";
				charaName = md5RanStr(fromQQ + 2, pl09);
				break;
			case 10:
				gameName = "风神录";
				charaName = md5RanStr(fromQQ + 2, TH10GameData.players);
				break;
			case 11:
				gameName = "地灵殿";
				charaName = md5RanStr(fromQQ + 2, TH11GameData.players);
				break;
			case 12:
				gameName = "星莲船";
				charaName = md5RanStr(fromQQ + 2, TH12GameData.players);
				break;
			case 13:
				gameName = "神灵庙";
				charaName = md5RanStr(fromQQ + 2, TH13GameData.players);
				break;
			case 14:
				gameName = "辉针城";
				charaName = md5RanStr(fromQQ + 2, TH14GameData.players);
				if (goodAt) {
					return String.format("%s今天宜用%s-%s打%s", pname, charaName, md5RanStr(fromQQ + 1, TH14GameData.playerSub), gameName);
				} else {
					return String.format("忌用%s-%s打%s", charaName, md5RanStr(fromQQ + 1, TH14GameData.playerSub), gameName);
				}
			case 15:
				gameName = "绀珠传";
				charaName = md5RanStr(fromQQ + 2, TH15GameData.players);
				break;
			case 16:
				gameName = "天空璋";
				charaName = md5RanStr(fromQQ + 2, TH16GameData.players);
				if (goodAt) {
					return String.format("%s今天宜用%s-%s打%s", pname, charaName, md5RanStr(fromQQ + 1, TH16GameData.playerSub), gameName);
				} else {
					return String.format("忌用%s-%s打%s", charaName, md5RanStr(fromQQ + 1, TH16GameData.playerSub), gameName);
				}
			case 17:
				gameName = "鬼形兽";
				charaName = md5RanStr(fromQQ + 2, TH17GameData.players);
				if (goodAt) {
					return String.format("%s今天宜用%s-%s打%s", pname, charaName, md5RanStr(fromQQ + 1, TH17GameData.playerSub), gameName);
				} else {
					return String.format("忌用%s-%s打%s", charaName, md5RanStr(fromQQ + 1, TH17GameData.playerSub), gameName);
				}
			default:
				return "";
		}
		if (goodAt) {
			return String.format("%s今天宜用%s打%s", pname, charaName, gameName);
		} else {
			return String.format("忌用%s打%s", charaName, gameName);
		}
	}

	private void rollPlane(String ss, long fromGroup) {
        switch (ss) {
            case "东方灵异传":
            case "th1":
            case "th01":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(pl01));
                break;
            case "东方封魔录":
            case "th2":
            case "th02":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(pl02));
                break;
            case "东方梦时空":
            case "th3":
            case "th03":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(pl03));
                break;
            case "东方幻想乡":
            case "th4":
            case "th04":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(pl04));
                break;
            case "东方怪绮谈":
            case "th5":
            case "th05":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(pl05));
                break;
            case "东方红魔乡":
            case "th6":
            case "th06":
            case "tEoSD":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH06GameData.players));
                break;
            case "东方妖妖梦":
            case "th7":
            case "th07":
            case "PCB":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH07GameData.players));
                break;
            case "东方永夜抄":
            case "th8":
            case "th08":
            case "IN":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH08GameData.players));
                break;
            case "东方花映冢":
            case "th9":
            case "th09":
            case "PoFV":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(pl09));
                break;
            case "东方风神录":
            case "th10":
            case "MoF":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH10GameData.players));
                break;
            case "东方地灵殿":
            case "th11":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH11GameData.players));
                break;
            case "东方星莲船":
            case "th12":
            case "UFO":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH12GameData.players));
                break;
            case "东方神灵庙":
            case "th13":
            case "TD":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH13GameData.players));
                break;
            case "东方辉针城":
            case "th14":
            case "DDC":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH14GameData.players) + " " + Tools.ArrayTool.rfa(TH14GameData.playerSub));
                break;
            case "东方绀珠传":
            case "th15":
            case "LoLK":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH15GameData.players));
                break;
            case "东方天空璋":
            case "th16":
            case "HSiFS":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH16GameData.players) + " " + Tools.ArrayTool.rfa(TH16GameData.playerSub));
                break;
            case "东方鬼形兽":
            case "th17":
            case "WBaWC":
                Autoreply.sendMessage(fromGroup, 0, Tools.ArrayTool.rfa(TH17GameData.players) + "+" + Tools.ArrayTool.rfa(TH17GameData.playerSub));
                break;

            case "东方文花帖":
            case "th9.5":
            case "StB":
                //       case "东方文花帖DS":
                //       case "th12.5":
                //       case "DS":
            case "妖精大战争":
            case "th12.8":
            case "弹幕天邪鬼":
            case "th14.3":
            case "ISC":
            case "秘封噩梦日记":
            case "th16.5":
            case "VD":
                Autoreply.sendMessage(fromGroup, 0, "就一个飞机你roll你[CQ:emoji,id=128052]呢");
                break;
            default:
                Autoreply.sendMessage(fromGroup, 0, "只有2un飞机游戏");
                break;
        }
    }

    private void rollStage(String[] ss, long fromGroup) {
        HashMap<Integer, String> hMap = new HashMap<>();
        for (int i = 2; i < ss.length; i++) {
            hMap.put(Autoreply.instance.random.nextInt(), ss[i]);
        }
        int flag = 1;
        StringBuilder sBuilder = new StringBuilder();
        for (Integer key : hMap.keySet()) {
            sBuilder.append("stage").append(flag).append(":").append(hMap.get(key)).append("\n");
            flag++;
        }
        Autoreply.sendMessage(fromGroup, 0, sBuilder.append("完成").toString());
    }

	private int md5Random(long fromQQ) {
		String md5=Hash.getMd5Instance().calculate(String.valueOf(fromQQ + System.currentTimeMillis() / (24 * 60 * 60 * 1000)));
		return Integer.parseInt(md5.substring(26), 16);
	}

	public String md5RanStr(long fromQQ, String[] arr) {
		return arr[md5Random(fromQQ) % arr.length];
	}

	private void addArrayToSet(Set<String> set, String start, String stop) {
		int istart=0;
		int istop=0;
		for (int i=0;i < spells.length;++i) {
			if (spells[i].equals(start)) {
				istart = i;
			} else if (spells[i].equals(stop)) {
				istop = i;
			}	
		}
		for (int i=istart;i <= istop;++i) {
			set.add(spells[i]);
		}
	}
}
