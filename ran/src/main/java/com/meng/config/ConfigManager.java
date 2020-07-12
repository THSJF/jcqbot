package com.meng.config;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.config.javabeans.*;
import com.meng.config.sanae.*;
import com.meng.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import com.meng.SJFInterfaces.*;

public class ConfigManager implements INeedPersistent {

	public static ConfigManager instance;
    public ConfigHolder configHolder = new ConfigHolder();
	private GroupConfig emptyConfig = new GroupConfig();

    public ConfigManager() {
		instance = this;
        DataPersistenter.read(this);
		Autoreply.instance.threadPool.execute(new SocketDicManager(this));
    }

	public boolean containsGroup(long group) {
		for (GroupConfig gf:configHolder.groupConfigs) {
			if (gf.n == group) {
				return true;
			}
		}
		return false;
	}

	public void setFunctionEnabled(long fromGroup, int functionID, boolean enable) {
		GroupConfig gc=getGroupConfig(fromGroup);
		if (enable) {
			gc.f1 |= (1 << functionID);
		} else {
			gc.f1 &= ~(1 << functionID);
		}
		saveConfig();
	}

	public void setNickName(long qq, String nickname) {
		if (nickname != null) {
			configHolder.nicknameMap.put(qq, nickname);
		} else {
			configHolder.nicknameMap.remove(qq);
		}
		saveConfig();
	}

	public String getNickName(long qq) {
		String nick=null;
		nick = configHolder.nicknameMap.get(qq);
		if (nick == null) {
			PersonInfo pi=getPersonInfoFromQQ(qq);
			if (pi == null) {
				nick = Autoreply.CQ.getStrangerInfo(qq).getNick();
			} else {
				nick = pi.name;
			}
		}
		return nick;
	}

	public String getNickName(long group, long qq) {
		String nick=null;
		nick = configHolder.nicknameMap.get(qq);
		if (nick == null) {
			PersonInfo pi=getPersonInfoFromQQ(qq);
			if (pi == null) {
				nick = Autoreply.CQ.getGroupMemberInfo(group, qq).getNick();
			} else {
				nick = pi.name;
			}
		}
		return nick;
	}

    public boolean isMaster(long fromQQ) {
        return configHolder.masterList.contains(fromQQ);
    }

    public boolean isAdmin(long fromQQ) {
        return configHolder.adminList.contains(fromQQ) || configHolder.masterList.contains(fromQQ);
    }

    public GroupConfig getGroupConfig(long fromGroup) {
        for (GroupConfig gc : configHolder.groupConfigs) {
            if (fromGroup == gc.n) {
                return gc;
            }
        }
        return emptyConfig;
    }

    public boolean isNotReplyQQ(long qq) {
        return configHolder.QQNotReply.contains(qq) || configHolder.blackListQQ.contains(qq);
    }

    public boolean isBlackQQ(long qq) {
        return configHolder.blackListQQ.contains(qq);
    }

    public boolean isBlackGroup(long qq) {
        return configHolder.blackListGroup.contains(qq);
    }

    public boolean isNotReplyWord(String word) {
        for (String nrw : configHolder.wordNotReply) {
            if (word.contains(nrw)) {
                return true;
            }
        }
        return false;
    }

    public PersonInfo getPersonInfoFromQQ(long qq) {
        for (PersonInfo pi : configHolder.personInfo) {
            if (pi.qq == qq) {
                return pi;
            }
        }
        return null;
    }

    public PersonInfo getPersonInfoFromName(String name) {
        for (PersonInfo pi : configHolder.personInfo) {
            if (pi.name.equals(name)) {
                return pi;
            }
        }
        return null;
    }

    public PersonInfo getPersonInfoFromBid(long bid) {
        for (PersonInfo pi : configHolder.personInfo) {
            if (pi.bid == bid) {
                return pi;
            }
        }
        return null;
    }

	public PersonInfo getPersonInfoFromLiveId(long lid) {
        for (PersonInfo pi : configHolder.personInfo) {
            if (pi.bliveRoom == lid) {
                return pi;
			}
		}
        return null;
	}

    public void addBlack(long group, final long qq) {
        configHolder.blackListQQ.add(qq);
        configHolder.blackListGroup.add(group);
        for (GroupConfig groupConfig : configHolder.groupConfigs) {
            if (groupConfig.n == group) {
                configHolder.groupConfigs.remove(groupConfig);
                break;
            }
        }
        saveConfig();
        Autoreply.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					//    HashSet<Group> groups = Tools.CQ.findQQInAllGroup(qq);
					//   for (Group g : groups) {
                    // if (Tools.CQ.ban(g.getId(), qq, 300)) {
                    //    sendMessage(g.getId(), 0, "不要问为什么你会进黑名单，你干了什么自己知道");
                    //   }
					//    }
				}
			});
        Autoreply.sendMessage(Autoreply.mainGroup, 0, "已将用户" + qq + "加入黑名单");
        Autoreply.sendMessage(Autoreply.mainGroup, 0, "已将群" + group + "加入黑名单");
    }

	public void setOgg(long qqNum) {
		configHolder.ogg = qqNum;
		saveConfig();
	}

	
	@Override
	public String getPersistentName() {
		return "configV3.json";
	}

	@Override
	public Class<?> getDataClass() {
		return ConfigHolder.class;
	}

	@Override
	public ConfigHolder getDataBean() {
		return configHolder;
	}

	@Override
	public void setDataBean(Object o) {
		if (o.getClass() != getDataClass()) {
			throw new RuntimeException("bean类型错误");
		}
		configHolder = (ConfigHolder) o;
	}
	
    public void saveConfig() {
		SanaeDataPack sdp = SanaeDataPack.encode(SanaeDataPack.opConfigFile);
		sdp.write(Autoreply.gson.toJson(ConfigManager.instance.configHolder));
		Autoreply.instance.sanaeServer.send(sdp);
        DataPersistenter.save(this);
    }
}
