package com.meng.modules;

import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.SJFInterfaces.*;
import com.meng.config.*;
import com.meng.dice.*;
import com.meng.gameData.TouHou.zun.*;
import com.meng.sjfmd.libs.*;
import com.meng.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author 司徒灵羽
 */

public class MSpellCollect extends BaseGroupModule implements IPersistentData {
	public ConcurrentHashMap<Long,HashSet<String>> userSpellsMap=new ConcurrentHashMap<>();
	public ConcurrentHashMap<Long,ArchievementBean> archiMap=new ConcurrentHashMap<>();
	private HashSet<Long> todayCard=new HashSet<>();
	private HashSet<Long> todaySign=new HashSet<>();
	private File spellFile;
	public ArrayList<Archievement> archList=new ArrayList<>();

	@Override
	public MSpellCollect load() {
		spellFile = new File(Autoreply.appDirectory + "/properties/spells.json");
        if (!spellFile.exists()) {
            saveConfig();
        }
        userSpellsMap = GSON.fromJson(FileTool.readString(Autoreply.appDirectory + "/properties/spells.json"), new TypeToken<ConcurrentHashMap<Long,HashSet<String>>>() {}.getType());
		DataPersistenter.read(this);
        archiMap = GSON.fromJson(FileTool.readString(Autoreply.appDirectory + "/properties/archievement.json"), new TypeToken<ConcurrentHashMap<Long,ArchievementBean>>() {}.getType());
		archList.add(new Archievement("恶魔领地", "收集东方红魔乡全部符卡", ArchievementBean.th6All, TH06GameData.spells.length, TH06GameData.spells));
		archList.add(new Archievement("完美樱花", "收集东方妖妖梦全部符卡",  ArchievementBean.th7All, TH07GameData.spells.length, TH07GameData.spells));
		archList.add(new Archievement("永恒之夜", "收集东方永夜抄lastspell和lastword外全部符卡",  ArchievementBean.th8All, TH08GameData.spells.length, TH08GameData.spells));
		archList.add(new Archievement("信仰之山", "收集东方风神录全部符卡",  ArchievementBean.th10All, TH10GameData.spells.length, TH10GameData.spells));
		archList.add(new Archievement("地底之灵", "收集东方地灵殿全部符卡",  ArchievementBean.th11All, TH11GameData.spells.length, TH11GameData.spells));
		archList.add(new Archievement("未知物体", "收集东方星莲船全部符卡",  ArchievementBean.th12All, TH12GameData.spells.length, TH12GameData.spells));
		archList.add(new Archievement("十个欲望", "收集东方神灵庙overdrive外全部符卡",  ArchievementBean.th13All, TH13GameData.spells.length, TH13GameData.spells));
		archList.add(new Archievement("两个选择", "收集东方辉针城全部符卡",  ArchievementBean.th14All, TH14GameData.spells.length, TH14GameData.spells));
		archList.add(new Archievement("疯狂国度", "收集东方绀珠传全部符卡",  ArchievementBean.th15All, TH15GameData.spells.length, TH15GameData.spells));
		archList.add(new Archievement("四季之星", "收集东方天空璋全部符卡",  ArchievementBean.th16All, TH16GameData.spells.length, TH16GameData.spells));
		archList.add(new Archievement("狡猾之兽", "收集东方鬼形兽全部符卡",  ArchievementBean.th17All, TH17GameData.spells.length, TH17GameData.spells));
		archList.add(new Archievement("纯化的神灵", "收集Lunatic难度纯狐所有符卡", ArchievementBean.JunkoSpells, 10, "「掌上的纯光」", "「杀意的百合」", "「现代的神灵界」", "「战栗的寒冷之星」", "「纯粹的疯狂」", "「地上秽的纯化」", "纯符「纯粹的弹幕地狱」"));
		archList.add(new Archievement("Perfect Cherry Blossom", "收集Lunatic难度西行寺幽幽子所有符卡", ArchievementBean.yoyoko, 10, "亡乡「亡我乡 -自尽-」", "亡舞「生者必灭之理 -魔境-」",  "华灵「蝶幻」", "幽曲「埋骨于弘川 -神灵-」", "樱符「完全墨染的樱花 -开花-」", "「反魂蝶 -八分咲-」"));
		archList.add(new Archievement("樱花飞舞", "收集樱符「完全墨染的樱花 -封印-」,樱符「完全墨染的樱花 -亡我-」,樱符「完全墨染的樱花 -春眠-」,樱符「完全墨染的樱花 -开花-」", ArchievementBean.sakura, 15, "樱符「完全墨染的樱花 -封印-」", "樱符「完全墨染的樱花 -亡我-」", "樱符「完全墨染的樱花 -春眠-」", "樱符「完全墨染的樱花 -开花-」"));
		archList.add(new Archievement("春天来了", "获得春符「惊喜之春」", ArchievementBean.LilyWhite, 10, "春符「惊喜之春」"));
		archList.add(new Archievement("素质三连", "收集「红色的幻想乡」,神光「无忤为宗」,「纯粹的疯狂」", ArchievementBean.threeHits, 8, "「红色的幻想乡」", "神光「无忤为宗」", "「纯粹的疯狂」"));
		archList.add(new Archievement("麻将即信仰", "获得「信仰之山」或「风神之神德」" , ArchievementBean.MountainOfFaith, 5, Archievement.judgeOr, "「信仰之山」", "「风神之神德」"));
		archList.add(new Archievement("极冰盛宴", "获得冰符「冰瀑」,雹符「冰雹暴风」,冻符「完美冻结」,雪符「钻石风暴」,冰符「冰袭方阵」,冰符「Ultimate Blizzard」,「里·Perfect Summer Ice」" , ArchievementBean.ice, 25, "冰符「冰瀑」", "雹符「冰雹暴风」", "冻符「完美冻结」", "雪符「钻石风暴」", "冰符「冰袭方阵」", "冰符「Ultimate Blizzard」", "「里·Perfect Summer Ice」"));
		archList.add(new Archievement("伪物理学家", "获得想起「波与粒的境界」" , ArchievementBean.physics, 5, "想起「波与粒的境界」"));
		archList.add(new Archievement("城管执法", "获得博丽灵梦lastspell和lastword外所有符卡" , ArchievementBean.reimu, 20, "梦符「二重结界」", "梦境「二重大结界」", "灵符「梦想封印 散」", "散灵「梦想封印 寂」", "梦符「封魔阵」", "神技「八方鬼缚阵」", "神技「八方龙杀阵」", "灵符「梦想封印 集」", "回灵「梦想封印 侘」", "境界「二重弹幕结界」", "大结界「博丽弹幕结界」"));
		archList.add(new Archievement("星光爆裂", "获得雾雨魔理沙lastspell和lastword外所有符卡", ArchievementBean.marisa, 20, "魔符「银河」", "魔空「小行星带」", "魔符「星尘幻想」", "黑魔「黑洞边缘」", "恋符「非定向光线」", "恋风「星光台风」", "恋符「极限火花」", "恋心「二重火花」", "光符「地球光」", "光击「击月」"));
		archList.add(new Archievement("春之岸边", "获得棒符「忙碌探知棒」", ArchievementBean.spring, 5, "棒符「忙碌探知棒」"));
		archList.add(new Archievement("神绮的影子", "获得大魔法「魔神复诵」", ArchievementBean.moshenfusong, 5, "大魔法「魔神复诵」"));
		archList.add(new Archievement("撸猫", "获得橙和燐的所有符卡", ArchievementBean.cat, 27, MDiceImitate.cat));
		archList.add(new Archievement("时间都去哪了", "获得十六夜咲夜的所有符卡", ArchievementBean.time, 9, "奇术「误导」", "奇术「幻惑误导」", "幻在「钟表的残骸」", "幻幽「迷幻杰克」", "幻象「月神之钟」", "幻世「世界」", "女仆秘技「操弄玩偶」", "女仆秘技「杀人玩偶」", "奇术「永恒的温柔」"));
		archList.add(new Archievement("漂移轮椅", "获得「里·Crazy Fall Wind」", ArchievementBean.piaoyilunyi, 9, "「里·Crazy Fall Wind」"));
		archList.add(new Archievement("四个季节", "获得「里·Breeze Cherry Blossom」,「里·Perfect Summer Ice」,「里·Crazy Fall Wind」,「里·Extreme Winter」", ArchievementBean.fourSeasons, 9, "「里·Breeze Cherry Blossom」", "「里·Perfect Summer Ice」", "「里·Crazy Fall Wind」", "「里·Extreme Winter」"));
		archList.add(new Archievement("Hidden Bugs in Four Spells", "获得尽符「血腥的深山谋杀」,笹符「七夕星祭」,冥加「在你背后」,舞符「背后之祭」", ArchievementBean.hiddenBugInFourSpells, 16, "尽符「血腥的深山谋杀」", "笹符「七夕星祭」", "冥加「在你背后」", "舞符「背后之祭」"));
		archList.add(new Archievement("99.61%", "获得所有6面Lunatic终符", ArchievementBean._9961, 16, "「红色的幻想乡」", "「反魂蝶 -八分咲-」", "秘术「天文密葬法」", "神宝「蓬莱的玉枝 -梦色之乡-」", "「风神之神德」", "「地底太阳」", "飞钵「传说的飞空圆盘」", "「新生的神灵」", "「七个一寸法师」", "纯符「纯粹的弹幕地狱」", "「里·Breeze Cherry Blossom」", "「里·Perfect Summer Ice」", "「里·Crazy Fall Wind」", "「里·Extreme Winter」", "「Idola Diabolus」"));
		archList.add(new Archievement("固定弹固定撞", "获得紫奥义「弹幕结界」", ArchievementBean.fixBulletFixMiss, 4, "紫奥义「弹幕结界」"));
		archList.add(new Archievement("无人气", "获得叶符「狂舞的落叶」", ArchievementBean.noSupport, 4, "叶符「狂舞的落叶」"));
		archList.add(new Archievement("辉光之针的小人族", "获得少名针妙丸的所有Lunatic符卡", ArchievementBean.shimiyomaru, 16, "小弹「小人的荆棘路」", "小槌「变得更大吧」", "妖剑「辉针剑」", "小槌「你给我变大吧」", "「进击的小人」", "「一寸之壁」", "「七个一寸法师」"));
		archList.add(new Archievement("信仰是为了虚幻之人", "获得东方风神录中东风谷早苗Lunatic所有符卡", ArchievementBean.sanae, 16, "秘术「一脉相传的弹幕」", "奇迹「客星辉煌之夜」", "开海「摩西之奇迹」", "准备「召请建御名方神」", "大奇迹「八坂之神风」"));
		archList.add(new Archievement("赏月", "获得狱符「地狱之蚀」,「阿波罗捏造说」,月「月狂冲击」", ArchievementBean.moon, 8, "狱符「地狱之蚀」", "「阿波罗捏造说」", "月「月狂冲击」"));
		archList.add(new Archievement("恐怖的回忆", "获得古明地觉所有复制卡", ArchievementBean.memory, 32, MDiceImitate.memory));
		archList.add(new Archievement(" ", "获得    所有符卡", ArchievementBean.koishi, 20, "表象「先祖托梦」", "表象「弹幕偏执症」", "本能「本我的解放」", "抑制「超我」", "反应「妖怪测谎机」", "潜意识「弹幕的墨迹测验」", "复燃「恋爱的埋火」", "深层「潜意识的基因」", "「被厌恶者的哲学」", "「Subterranean Rose」"));
		archList.add(new Archievement("■■■", "获得■■■所有符卡", ArchievementBean.rumia, 16, "月符「月光」", "夜符「夜雀」", "暗符「境界线」"));
		archList.add(new Archievement("隐藏的弹幕:瞄准", "获得结界「生与死的境界」", ArchievementBean.hideTH7Ex, 8, "结界「生与死的境界」"));
		archList.add(new Archievement("隐藏的弹幕:引力", "获得「地狱的人工太阳」或「地底太阳」", ArchievementBean.hideTH11, 8, Archievement.judgeOr, "「地狱的人工太阳」", "「地底太阳」"));
		archList.add(new Archievement("隐藏的弹幕:分裂", "获得大魔法「魔神复诵」", ArchievementBean.hideTH12, 8, "大魔法「魔神复诵」"));
		archList.add(new Archievement("隐藏的弹幕:交叉", "获得恨弓「源三位赖政之弓」", ArchievementBean.hideTH12Ex, 8, "恨弓「源三位赖政之弓」"));
		archList.add(new Archievement("隐藏的弹幕:高亮", "获得貉符「满月下的腹鼓舞」", ArchievementBean.hideTH13, 8, "貉符「满月下的腹鼓舞」"));
		archList.add(new Archievement("饼符", "获得饼打过的东西", ArchievementBean.shaoBing, 24, "恨弓「源三位赖政之弓」", "纯符「纯粹的弹幕地狱」", "「里·Breeze Cherry Blossom」", "「里·Perfect Summer Ice」", "「里·Crazy Fall Wind」", "「里·Extreme Winter」"));
		archList.add(new Archievement("元素践踏", "获得帕秋莉任意符卡，包含水火", ArchievementBean.iceAndFire, 16, Archievement.judgeNameContains, "水", "火"));
		archList.add(new Archievement("少儿不宜", "获得厄符「厄神大人的生理节律」和龟符「龟甲地狱」", ArchievementBean.r18, 9, "厄符「厄神大人的生理节律」", "龟符「龟甲地狱」"));
		archList.add(new Archievement("灼眼冰晶", "获得帕秋莉任意符卡，包含水金", ArchievementBean.shiningIce, 8, Archievement.judgeNameContains, "水", "金"));
		archList.add(new Archievement("元素之主", "获得帕秋莉任意符卡，包含水火木金土", ArchievementBean.elementMaster, 8, Archievement.judgeNameContains, "水", "火", "木", "金", "土"));
		archList.add(new Archievement("灵气符卡", "获得紫奥义「弹幕结界」", ArchievementBean.smartSpell, 8, "紫奥义「弹幕结界」"));
		archList.add(new Archievement("随机的狙", "获得飞钵「飞行幻想」或飞钵「传说的飞空圆盘」", ArchievementBean.randomSnipe, 8, Archievement.judgeOr, "飞钵「飞行幻想」", "飞钵「传说的飞空圆盘」"));
		archList.add(new Archievement("鸡你太美", "获得庭渡久侘歌的任意符卡", ArchievementBean.cxk, 8, Archievement.judgeOr, "水符「分水的试练」", "水符「分水的上级试炼」", "水符「分水的顶级试炼」", "光符「瞭望的试练」", "光符「瞭望的上级试炼」", "光符「瞭望的顶级试炼」", "鬼符「鬼渡的试练」", "鬼符「鬼渡的上级试炼」", "鬼符「鬼渡的狱级试炼」", "血战「血之分水岭」", "血战「狱界视线」", "血战「全灵鬼渡」"));
		archList.add(new Archievement("双杀空格", "获得雅符「春之京人偶」", ArchievementBean.doubleKill, 8, "雅符「春之京人偶」"));
		archList.add(new Archievement("复活", "获得「不死鸟重生」", ArchievementBean.lastwordEx, 28, "「不合时令的蝶雨」"));
		archList.add(new Archievement("蝶雨", "获得「不合时令的蝶雨」", ArchievementBean.lastWordST1, 28, "「不合时令的蝶雨」"));
		archList.add(new Archievement("护眼符卡", "获得「失明的夜雀」", ArchievementBean.lastWordST2, 28, "「失明的夜雀」"));
		archList.add(new Archievement("瞎眼符卡", "获得「日出之国的天子」", ArchievementBean.lastWordST3, 28, "「日出之国的天子」"));
		archList.add(new Archievement("秒杀2un", "获得「远古的骗术」", ArchievementBean.lastWordST5, 28, "「远古的骗术」"));
		archList.add(new Archievement("幻觉", "获得「月的红眼」", ArchievementBean.lastWordST5_2, 28, "「月的红眼」"));
		archList.add(new Archievement("封你没得跑", "获得「天网蛛网捕蝶之法」", ArchievementBean.lastWordST6A, 28, "「天网蛛网捕蝶之法」"));
		archList.add(new Archievement("蓬莱的树海", "获得「蓬莱的树海」", ArchievementBean.lastWordST6B, 28, "「蓬莱的树海」"));
		archList.add(new Archievement("梦想天生", "获得「梦想天生」", ArchievementBean.lastWordReimu, 28, "「梦想天生」"));
		archList.add(new Archievement("结界之主", "获得「深弹幕结界 梦幻泡影」", ArchievementBean.lastWordYukari, 28, "「深弹幕结界 梦幻泡影」"));
		archList.add(new Archievement("体术流", "获得「彗星」", ArchievementBean.lastWordMarisa, 28, "「彗星」"));
		archList.add(new Archievement("控偶师", "获得「猎奇剧团的怪人」", ArchievementBean.lastWordAlice, 28, "「猎奇剧团的怪人」"));
		archList.add(new Archievement("the world!", "获得「收缩的世界」", ArchievementBean.lastWordSakuya, 28, "「收缩的世界」"));
		archList.add(new Archievement("微移的威严", "获得「绯红的宿命」", ArchievementBean.lastWordRemilia, 28, "「绯红的宿命」"));
		archList.add(new Archievement("剑术大师", "获得「待宵反射卫星斩」", ArchievementBean.lastWordYomu, 28, "「待宵反射卫星斩」"));
		archList.add(new Archievement("西行妖盛开", "获得「西行寺无余涅槃」", ArchievementBean.lastWordYoyoku, 28, "「西行寺无余涅槃」"));
		archList.add(new Archievement("简单的困难", "获得所有难度纯符", ArchievementBean.easyHard, 6, "纯符「单纯的子弹地狱」", "纯符「纯粹的弹幕地狱」"));
		archList.add(new Archievement("不可能的盛开", "获得任意难度反魂蝶", ArchievementBean.impossbleBloom, 6, Archievement.judgeOr, "「反魂蝶 -一分咲-」", "「反魂蝶 -三分咲-」", "「反魂蝶 -五分咲-」", "「反魂蝶 -八分咲-」"));
		//霜舞精灵
		//神灵界 星辰降落的神灵庙
		//郭敬明 七个一寸法师
		// 轻工业( 

		SJFExecutors.executeAtFixedRate(new Runnable(){

				@Override
				public void run() {
					todaySign.clear();
					todayCard.clear();
				}
			}, TimeFormater.getNextDay(), 24 * 60 * 60 * 1000 , TimeUnit.MILLISECONDS);
		return this;
	}



	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (!ConfigManager.getGroupConfig(fromGroup).isSpellCollectEnable()) {
			return false;
		}
		if (msg.startsWith("#幻币转账") && fromQQ == ConfigManager.getOgg()) {
			List<Long> chan=Autoreply.instance.CC.getAts(msg);
			if (!ConfigManager.isMaster(chan.get(1))) {
				return false;
			}
			int coins=0;
			try {
				coins = (int)Float.parseFloat(msg.substring(msg.indexOf("转账", 6) + 2, msg.indexOf("个幻币")));		
			} catch (NumberFormatException e) {
				Autoreply.sendMessage(fromGroup, 0, e.toString());
			}
			HashSet<String> tmpSet=userSpellsMap.get(chan.get(0));
			if (tmpSet == null) {
				tmpSet = new HashSet<String>();
				userSpellsMap.put(chan.get(0), tmpSet);
			}
			Random r=new Random();
			StringBuilder sb=new StringBuilder();
			sb.append(ConfigManager.getNickName(fromQQ));
			sb.append("获得了:");
			for (int i=0;i < coins * 3;++i) {
				String s;
				if (r.nextInt(150) == 20) {
					s = TH08GameData.lastword[r.nextInt(TH08GameData.lastword.length)];
				} else if (r.nextInt(300) == 25) {
					s = TH13GameData.overdrive[r.nextInt(TH13GameData.overdrive.length)];
				} else {
					s = MDiceImitate.spells[r.nextInt(MDiceImitate.spells.length)];
				}
				tmpSet.add(s);
				sb.append("\n").append(s);
			}
			saveConfig();
			checkArchievement(fromGroup, chan.get(0), tmpSet);
			if (sb.toString().length() > 200) {
				Autoreply.sendMessage(fromGroup, fromQQ, "内容过长,不详细说明获得的符卡，但记录已保存");
				return true;
			}
			Autoreply.sendMessage(fromGroup, 0, sb.toString());
			return true;
		}

		if (msg.startsWith("幻币抽卡 ")) {
			try {
				int restCoins=ModuleManager.getGroupModule(MCoinManager.class).getCoinsCount(fromQQ);
				int useCoins=Integer.parseInt(msg.substring(5));
				if (useCoins > restCoins) {
					Autoreply.sendMessage(fromGroup, 0, "本地幻币不足");
					return true;
				}	
				HashSet<String> gotSpellsSet=userSpellsMap.get(fromQQ);
				if (gotSpellsSet == null) {
					gotSpellsSet = new HashSet<String>();
					userSpellsMap.put(fromQQ, gotSpellsSet);
				}
				Random r=new Random();
				StringBuilder sb=new StringBuilder();
				sb.append(ConfigManager.getNickName(fromQQ));
				sb.append("获得了:");
				for (int i=0;i < useCoins * 3;++i) {
					String s;
					if (r.nextInt(150) == 20) {
						s = TH08GameData.lastword[r.nextInt(TH08GameData.lastword.length)];
					} else if (r.nextInt(300) == 25) {
						s = TH13GameData.overdrive[r.nextInt(TH13GameData.overdrive.length)];
					} else  {
						s = MDiceImitate.spells[r.nextInt(MDiceImitate.spells.length)];
					}
					gotSpellsSet.add(s);
					sb.append("\n").append(s);
				}
				saveConfig();
				ModuleManager.getGroupModule(MCoinManager.class).subCoins(fromQQ, useCoins);
				checkArchievement(fromGroup, fromQQ, gotSpellsSet);
				if (sb.toString().length() > 200) {
					Autoreply.sendMessage(fromGroup, fromQQ, "内容过长,不详细说明获得的符卡，但记录已保存");
					return true;
				}
				Autoreply.sendMessage(fromGroup, 0, sb.toString());	
			} catch (Exception e) {
				Autoreply.sendMessage(fromGroup, 0, e.toString());
				return true;
			}
			return true;
		}

		if (msg.equals("签到")) {
			if (todaySign.contains(fromQQ)) {
				Autoreply.sendMessage(fromGroup, 0, "你今天签到过啦");
				return true;
			}
			Autoreply.sendMessage(fromGroup, 0, "签到成功,获得1幻币");
			ModuleManager.getGroupModule(MCoinManager.class).addCoins(fromQQ, 1);
			todaySign.add(fromQQ);
			return true;
		}

		if (msg.equals("抽卡")) {
			if (todayCard.contains(fromQQ)) {
				Autoreply.sendMessage(fromGroup, 0, "你今天已经抽过啦");
				return true;
			}
			HashSet<String> tmpSet=userSpellsMap.get(fromQQ);
			if (tmpSet == null) {
				tmpSet = new HashSet<String>();
				userSpellsMap.put(fromQQ, tmpSet);
			}
			Random r=new Random();
			StringBuilder sb=new StringBuilder();
			sb.append(ConfigManager.getNickName(fromQQ));
			sb.append("获得了:");
			for (int i=0;i < 5;++i) {
				String s;
				if (r.nextInt(150) == 20) {
					s = TH08GameData.lastword[r.nextInt(TH08GameData.lastword.length)];
				} else if (r.nextInt(300) == 25) {
					s = TH13GameData.overdrive[r.nextInt(TH13GameData.overdrive.length)];
				} else {	
					s = MDiceImitate.spells[r.nextInt(MDiceImitate.spells.length)];
				}
				tmpSet.add(s);
				sb.append("\n").append(s);
			}
			Autoreply.sendMessage(fromGroup, 0, sb.toString());
			saveConfig();
			checkArchievement(fromGroup, fromQQ, tmpSet);
			todayCard.add(fromQQ);
			return true;
		}

		if (msg.startsWith("购买符卡 ")) {
			String spellName=msg.substring(5);
			if (ModuleManager.getGroupModule(MCoinManager.class).getCoinsCount(fromQQ) < 40) {
				Autoreply.sendMessage(fromGroup, 0, "幻币不足,需要40幻币");
				return true;
			}
			if (isCointains(spellName)) {		
				HashSet<String> gotSpellSet=userSpellsMap.get(fromQQ);
				if (gotSpellSet == null) {
					gotSpellSet = new HashSet<String>();
					userSpellsMap.put(fromQQ, gotSpellSet);
				}
				gotSpellSet.add(spellName);
				Autoreply.sendMessage(fromGroup, 0, ConfigManager.getNickName(fromQQ) + "获得了:" + spellName);
				saveConfig();
				checkArchievement(fromGroup, fromQQ, gotSpellSet);	
				ModuleManager.getGroupModule(MCoinManager.class).subCoins(fromQQ, 40);
			} else {
				Autoreply.sendMessage(fromGroup, 0, "符卡名错误");
			}
			return true;
		}

		if (msg.equals("查看符卡")) {
			StringBuilder sb=new StringBuilder();
			HashSet<String> gotSpells=userSpellsMap.get(fromQQ);
			if (gotSpells == null) {
				Autoreply.sendMessage(fromGroup, 0, "你没有参加过抽卡");
				return true;
			}
			sb.append(ConfigManager.getNickName(fromQQ));
			sb.append("获得了:");
			int i=0;
			for (String s:MDiceImitate.spells) {
				if (gotSpells.contains(s)) {
					sb.append("\n").append(s);
					++i;
					if (i > 40) {
						Autoreply.sendMessage(fromGroup, fromQQ, sb.toString());
						sb.setLength(0);
						i = 0;
					}
				}
			}
			Autoreply.sendMessage(fromGroup, fromQQ, sb.toString());
			sb.setLength(0);
			sb.append("lastword:");
			boolean hasLastword=false;
			for (String s:TH08GameData.lastword) {
				if (gotSpells.contains(s)) {
					sb.append("\n").append(s);
					hasLastword = true;
				}
			}
			if (hasLastword) {
				Autoreply.sendMessage(fromGroup, fromQQ, sb.toString());
			}
			sb.setLength(0);
			boolean hasOverdrive=false;
			sb.append("overdrive:");
			for (String s:TH13GameData.overdrive) {
				if (gotSpells.contains(s)) {
					sb.append("\n").append(s);
					hasOverdrive = true;
				}
			}
			if (hasOverdrive) {
				Autoreply.sendMessage(fromGroup, fromQQ, sb.toString());
			}
			return true;	
		}

		if (msg.equals("查看成就")) {
			StringBuilder sb=new StringBuilder("列表:");
			ArchievementBean ab=archiMap.get(fromQQ);
			if (ab == null) {
				Autoreply.sendMessage(fromGroup, 0, "你没有获得成就");
				return true;
			}
			for (Archievement ac:archList) {
				sb.append("\n").append(ac.name).append(ab.isArchievementGot(ac.archNum) ?":√ ": ":x");//.append(" 奖励:").append(ac.coins).append("幻币");
			}
			Autoreply.sendMessage(fromGroup, 0, sb.toString());
			return true;	
		}

		if (msg.startsWith("成就条件 ")) {
			String archName=msg.substring(5);
			for (Archievement ac:archList) {
				if (archName.equals(ac.name)) {
					Autoreply.sendMessage(fromGroup, 0, ac.describe);
					return true;
				}
			}
			return true;	
		}

		if (msg.startsWith("幻币兑换 ")) {
			try {
				ModuleManager.getGroupModule(MCoinManager.class).exchangeCoins(fromGroup, fromQQ, Integer.parseInt(msg.substring(5)));
			} catch (Exception e) {
				Autoreply.sendMessage(fromGroup, 0, e.toString());
			}
			return true;	
		}

		if (msg.equals("~coins")) {
			Autoreply.sendMessage(fromGroup, 0, "你有" + ModuleManager.getGroupModule(MCoinManager.class).getCoinsCount(fromQQ) + "个幻币");
			return true;
		}

		if (!ConfigManager.isAdminPermission(fromQQ)) {
			return false;
		}
		if (msg.startsWith("移除成就 ")) {
			String arch=msg.substring(5, msg.indexOf("[") - 1);
			long toQQ=Autoreply.instance.CC.getAt(msg);
			ArchievementBean ab=archiMap.get(toQQ);
			if (ab == null) {
				ab = new ArchievementBean();
				archiMap.put(toQQ, ab);
			}
			for (Archievement ac:archList) {
				if (ac.name.equals(arch)) {
					ab.deleteArchievment(ac.archNum);
					Autoreply.sendMessage(fromGroup, toQQ, "为" + toQQ + "移除成就" + arch);
					saveArchiConfig();
					return true;
				}
			}
			return true;
		}
		if (msg.startsWith("移除符卡 ")) {
			String arch=msg.substring(5, msg.indexOf("[") - 1);
			long toQQ=Autoreply.instance.CC.getAt(msg);
			HashSet<String> spells=userSpellsMap.get(toQQ);
			if (spells == null) {
				spells = new HashSet<>();
				userSpellsMap.put(toQQ, spells);
			}
			for (String ac:MDiceImitate.spells) {
				if (ac.equals(arch)) {
					spells.remove(ac);
					Autoreply.sendMessage(fromGroup, toQQ, "为" + toQQ + "移除符卡" + arch);
					saveConfig();
					return true;
				}
			}
			return true;
		}
		if (msg.startsWith("添加符卡 ")) {
			String arch=msg.substring(5, msg.indexOf("[") - 1);
			long toQQ=Autoreply.instance.CC.getAt(msg);
			HashSet<String> spells=userSpellsMap.get(toQQ);
			if (spells == null) {
				spells = new HashSet<>();
				userSpellsMap.put(toQQ, spells);
			}
			for (String ac:MDiceImitate.spells) {
				if (ac.equals(arch)) {
					spells.add(ac);
					Autoreply.sendMessage(fromGroup, toQQ, "为" + toQQ + "添加符卡" + arch);
					saveConfig();
					checkArchievement(fromGroup, fromQQ, spells);
					return true;
				}
			}
			return true;
		}
		return false;
	}

	public void checkArchievement(long fromGroup, long toQQ, HashSet<String> gotSpell) {
		ArchievementBean ab=archiMap.get(toQQ);
		if (ab == null) {
			ab = new ArchievementBean();
			archiMap.put(toQQ, ab);
		}
		for (Archievement ac:archList) {
			if (ac.getNewArchievement(ab, gotSpell)) {
				ab.addArchievement(ac.archNum);
				Autoreply.sendMessage(fromGroup, toQQ, "获得成就:" + ac.name + "\n获得奖励:" + ac.coins + "\n条件:" + ac.describe);	
				ModuleManager.getGroupModule(MCoinManager.class).addCoins(toQQ, ac.coins);
			}
		}
		saveArchiConfig();
	}

	private boolean isCointains(String name) {
		for (String s:MDiceImitate.spells) {
			if (s.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void saveConfig() {
        try {
            FileOutputStream fos = new FileOutputStream(spellFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(GSON.toJson(userSpellsMap));
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void saveArchiConfig() {
		DataPersistenter.save(this);
    }

	@Override
	public String getPersistentName() {
		return "/properties/archievement.json";
	}

	@Override
	public Type getDataType() {
		return new TypeToken<ConcurrentHashMap<Long,ArchievementBean>>() {}.getType();
	}

	@Override
	public Object getDataBean() {
		return archiMap;
	}

	@Override
	public void setDataBean(Object o) {
		archiMap = (ConcurrentHashMap) o;
	}
}
