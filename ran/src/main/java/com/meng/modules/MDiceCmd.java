package com.meng.modules;
import com.meng.*;
import com.meng.bilibili.live.*;
import com.meng.config.*;
import java.io.*;
import java.util.*;
import com.meng.messageProcess.*;

public class MDiceCmd extends BaseModule {

	private String[] cmdMsg;
	private int pos=0;

	@Override
	public BaseModule load() {
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (msg.charAt(0) != '.') {
			return false;
		}
		cmdMsg = msg.split(" ");
		pos = 0;
		if (pos < cmdMsg.length) {
			try {
				switch (next()) {
					case ".r":
						String rs = next();
						Autoreply.sendMessage(fromGroup, 0, String.format("%s投掷%s:D100 = %d", ConfigManager.instance.getNickName(fromQQ), rs == null ?"": rs, Autoreply.instance.random.nextInt(100)));
						return true;
					case ".ra":
						String ras = next();
						Autoreply.sendMessage(fromGroup, 0, String.format("%s进行检定:D100 = %d/%s", ConfigManager.instance.getNickName(fromQQ), Autoreply.instance.random.nextInt(Integer.parseInt(ras)), ras));
						return true;
					case ".li":
						Autoreply.sendMessage(fromGroup, 0, String.format("%s的疯狂发作-总结症状:\n1D10=%d\n症状: 狂躁：调查员患上一个新的狂躁症，在1D10=%d小时后恢复理智。在这次疯狂发作中，调查员将完全沉浸于其新的狂躁症状。这是否会被其他人理解（apparent to other people）则取决于守秘人和此调查员。\n1D100=%d\n具体狂躁症: 臆想症（Nosomania）：妄想自己正在被某种臆想出的疾病折磨。(KP也可以自行从狂躁症状表中选择其他症状)", ConfigManager.instance.getNickName(fromQQ), Autoreply.instance.random.nextInt(11), Autoreply.instance.random.nextInt(11), Autoreply.instance.random.nextInt(101)));
						return true;
					case ".ti":
						Autoreply.sendMessage(fromGroup, 0, String.format("%s的疯狂发作-临时症状:\n1D10=%d\n症状: 逃避行为：调查员会用任何的手段试图逃离现在所处的位置，状态持续1D10=%d轮。", ConfigManager.instance.getNickName(fromQQ), Autoreply.instance.random.nextInt(11), Autoreply.instance.random.nextInt(11)));
						return true;
					case ".rd":
						Autoreply.sendMessage(fromGroup, 0, String.format("由于%s %s骰出了: D100=%d", next(), ConfigManager.instance.getNickName(fromGroup, fromQQ), Autoreply.instance.random.nextInt()));
						return true;
					case ".nn":
						String name = next();
						if (name == null) {
							ConfigManager.instance.setNickName(fromQQ, null);
							Autoreply.sendMessage(fromGroup, 0, "我以后会用你的QQ昵称称呼你");
							return true;
						}
						if (name.length() > 30) {
							Autoreply.sendMessage(fromGroup, 0, "太长了,记不住");
							return true;
						}
						ConfigManager.instance.setNickName(fromQQ, name);
						Autoreply.sendMessage(fromGroup, 0, "我以后会称呼你为" + name);
						return true;
					case ".help":
						Autoreply.sendMessage(fromGroup, 0, ModuleManager.instance.getModule(MAdminMsg.class).userPermission.toString());
						return true;
					case ".live":
							String msgSend;
							StringBuilder stringBuilder = new StringBuilder();
							for (Map.Entry<Integer,LivePerson> entry:Autoreply.instance.liveListener.livePersonMap.entrySet()) {	
								if (entry.getValue().lastStatus) {
									stringBuilder.append(ConfigManager.instance.getPersonInfoFromBid(entry.getKey()).name).append("正在直播").append(entry.getValue().liveUrl).append("\n");
								}
							}
							msgSend = stringBuilder.toString();
							Autoreply.sendMessage(fromGroup, fromQQ, msgSend.equals("") ? "居然没有飞机佬直播" : msgSend);
							return true;	
				}
			} catch (NumberFormatException ne) {
				Autoreply.sendMessage(fromGroup, 0, "参数错误");
			}
		}
		return false;
	}

	private String next() {
		try {
			return cmdMsg[pos++];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
}

