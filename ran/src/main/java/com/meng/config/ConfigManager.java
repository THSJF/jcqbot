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
import java.util.*;

public class ConfigManager implements IPersistentData {

	private static ConfigManager instance = new ConfigManager();
    private ConfigHolder configHolder = new ConfigHolder();
	private static final GroupConfig emptyConfig = new GroupConfig();

	private ConfigManager() {

	}

	public static ConfigHolder getConfigHolder() {
		return instance.configHolder;
	}

    public static void init() {
		DataPersistenter.read(instance);
		Autoreply.instance.threadPool.execute(new SJFServerSocket(instance));
    }

	public static void setFunctionEnabled(long fromGroup, int functionID, boolean enable) {
		if (enable) {
			getGroupConfig(fromGroup).f1 |= (1 << functionID);
		} else {
			getGroupConfig(fromGroup).f1 &= ~(1 << functionID);
		}
		saveConfig();
		
		try {
			Method m = instance.getClass()
			 .getMethod("setYYSLj", boolean.class);
			m.invoke(instance,true);
		} catch (Exception e) {
			
		}

	}

	public static void addGroupConfig(GroupConfig gc) {
		instance.configHolder.groupConfigs.add(gc);
		saveConfig();
	}
	
	public static void removeGroupConfig(long gcn) {
		for (GroupConfig gc : instance.configHolder.groupConfigs) {
			if (gc.n == gcn) {
				instance.configHolder.groupConfigs.remove(gc);
				break;
			}
		}
		saveConfig();
	}

	public static void removeGroupConfig(GroupConfig gc) {
		instance.configHolder.groupConfigs.remove(gc);
		saveConfig();
	}

	public static void removeGroupConfig(Collection<GroupConfig> gc) {
		instance.configHolder.groupConfigs.removeAll(gc);
		saveConfig();
	}

    public static GroupConfig getGroupConfig(long fromGroup) {
        for (GroupConfig gc : instance.configHolder.groupConfigs) {
            if (fromGroup == gc.n) {
                return gc;
            }
        }
        return emptyConfig;
    }

	public static Set<GroupConfig> getGroupConfigs() {
		return Collections.unmodifiableSet(instance.configHolder.groupConfigs);
	}

	public static void addBlockQQ(long qq) {
        instance.configHolder.blockOnlyQQ.add(qq);
		saveConfig();
    }

	public static void removeBlockQQ(long qq) {
        instance.configHolder.blockOnlyQQ.remove(qq);
		saveConfig();
    }

	public static boolean isBlockQQ(long qq) {
        return instance.configHolder.blockOnlyQQ.contains(qq) || instance.configHolder.blackQQ.contains(qq);
    }

	public static boolean isBlockOnlyQQ(long qq) {
        return instance.configHolder.blockOnlyQQ.contains(qq);
    }
	
	public static void addBlackQQ(long qq) {
        instance.configHolder.blackQQ.add(qq);
		saveConfig();
    }

	public static void removeBlackQQ(long q) {
		instance.configHolder.blackQQ.remove(q);
		saveConfig();
	}

    public static boolean isBlackQQ(long qq) {
        return instance.configHolder.blackQQ.contains(qq);
    }

	public static void addBlackGroup(long group) {
		instance.configHolder.blackGroup.add(group);
		saveConfig();
	}

	public static void removeBlackGroup(long g) {
		instance.configHolder.blackGroup.remove(g);
		saveConfig();
	}

    public static boolean isBlackGroup(long qq) {
        return instance.configHolder.blackGroup.contains(qq);
    }

	public static void addBlockWord(String str) {
		instance.configHolder.blockWord.add(str);
		saveConfig();
	}

	public static void removeBlockWord(String str) {
		instance.configHolder.blockWord.remove(str);
		saveConfig();
	}
	
    public static boolean isBlockWord(String word) {
        for (String nrw : instance.configHolder.blockWord) {
            if (word.contains(nrw)) {
                return true;
            }
        }
        return false;
    }

	public static void addPersonInfo(PersonInfo pi) {
		instance.configHolder.personInfos.add(pi);
		saveConfig();
	}

	public static Set<PersonInfo> getPersonInfo() {
		return Collections.unmodifiableSet(instance.configHolder.personInfos);
	}

	public static void removePersonInfo(PersonInfo pi) {
		instance.configHolder.personInfos.remove(pi);
		saveConfig();
	}

	public static PersonInfo getPersonInfoFromQQ(long qq) {
        for (PersonInfo pi : instance.configHolder.personInfos) {
            if (pi.qq == qq) {
                return pi;
            }
        }
        return null;
    }

    public static PersonInfo getPersonInfoFromName(String name) {
        for (PersonInfo pi : instance.configHolder.personInfos) {
            if (pi.name.equals(name)) {
                return pi;
            }
        }
        return null;
    }

    public static PersonInfo getPersonInfoFromBid(long bid) {
        for (PersonInfo pi : instance.configHolder.personInfos) {
            if (pi.bid == bid) {
                return pi;
            }
        }
        return null;
    }

	public static PersonInfo getPersonInfoFromLiveId(long lid) {
        for (PersonInfo pi : instance.configHolder.personInfos) {
            if (pi.bliveRoom == lid) {
                return pi;
			}
		}
        return null;
	}

	public static boolean isMaster(long fromQQ) {
        return instance.configHolder.masters.contains(fromQQ);
    }

	public static void addMaster(long qq) {
		instance.configHolder.masters.add(qq);
		saveConfig();
	}

	public static void removeMaster(long m) {
		instance.configHolder.masters.remove(m);
		saveConfig();
	}

	public static Set<Long> getMasters() {
		return Collections.unmodifiableSet(instance.configHolder.masters);
	}

	public static boolean isAdminPermission(long fromQQ) {
        return instance.configHolder.admins.contains(fromQQ) || instance.configHolder.masters.contains(fromQQ);
    }

	public static void addAdmin(long qq) {
		instance.configHolder.admins.add(qq);
		saveConfig();
	}

	public static void removeAdmin(long a) {
		instance.configHolder.admins.remove(a);
		saveConfig();
	}

	public static Set<Long> getAdmins() {
		return Collections.unmodifiableSet(instance.configHolder.admins);
	}

	public static void setNickName(long qq, String nickname) {
		if (nickname != null) {
			instance.configHolder.nicknameMap.put(qq, nickname);
		} else {
			instance.configHolder.nicknameMap.remove(qq);
		}
		saveConfig();
	}

	public static String getNickName(long qq) {
		String nick = null;
		nick = instance.configHolder.nicknameMap.get(qq);
		if (nick == null) {
			PersonInfo pi = getPersonInfoFromQQ(qq);
			if (pi == null) {
				nick = Autoreply.CQ.getStrangerInfo(qq).getNick();
			} else {
				nick = pi.name;
			}
		}
		return nick;
	}

	public static String getNickName(long group, long qq) {
		String nick=null;
		nick = instance.configHolder.nicknameMap.get(qq);
		if (nick == null) {
			PersonInfo pi = getPersonInfoFromQQ(qq);
			if (pi == null) {
				nick = Autoreply.CQ.getGroupMemberInfo(group, qq).getNick();
			} else {
				nick = pi.name;
			}
		}
		return nick;
	}

    public static void addBlack(long group, final long qq) {
        instance.configHolder.blackQQ.add(qq);
        instance.configHolder.blackGroup.add(group);
        for (GroupConfig groupConfig : instance.configHolder.groupConfigs) {
            if (groupConfig.n == group) {
                instance.configHolder.groupConfigs.remove(groupConfig);
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

	public static void setOgg(long qqNum) {
		instance.configHolder.ogg = qqNum;
		saveConfig();
	}

	public static long getOgg() {
		return instance.configHolder.ogg;
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
		return instance.configHolder;
	}

	@Override
	public void setDataBean(Object o) {
		if (o.getClass() != getDataClass()) {
			throw new RuntimeException("bean类型错误");
		}
		instance.configHolder = (ConfigHolder) o;
	}

    public static void saveConfig() {
		SanaeDataPack sdp = SanaeDataPack.encode(SanaeDataPack.opConfigFile);
		sdp.write(Autoreply.gson.toJson(ConfigManager.instance.configHolder));
		Autoreply.instance.sanaeServer.send(sdp);
        DataPersistenter.save(instance);
    }
}
