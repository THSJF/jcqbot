package com.meng;

import com.meng.bilibili.live.*;
import com.meng.bilibili.main.*;
import com.meng.config.*;
import com.meng.config.sanae.*;
import com.meng.modules.*;
import com.meng.remote.*;
import com.meng.tip.*;
import com.meng.tools.*;
import com.meng.tools.override.*;
import com.sobte.cqp.jcq.entity.*;
import com.sobte.cqp.jcq.event.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author 司徒灵羽
 */

public class Autoreply extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

    public static Autoreply instance;
    public String createdImageFolder;
    public Random random = new Random();
    public static CQCodeCC CC = new CQCodeCC();
	public NaiManager naiManager;
	//private FileInfoManager fileInfoManager = new FileInfoManager();
	public ZanManager zanManager;
    public UpdateListener updateListener;
    public LiveListener liveListener;
	public FileTypeUtil fileTypeUtil = new FileTypeUtil();
	public CookieManager cookieManager;
	public DanmakuListenerManager danmakuListenerManager;
	public RitsukageServer connectServer;
	public SanaeServer sanaeServer;

	public RemoteWebSocket remoteWebSocket;
	public String userAgent="Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
	public static long mainGroup=1023432971l;
	public static long yysGroup = 617745343L;
	public static boolean sleeping=true;

	private MessageSender sender;

    public static void main(String[] args) {
        CQ = new CoolQ(1000);
        CQ.logInfo("[JCQ] TEST Demo", "测试启动");
        Autoreply demo = new Autoreply();
        demo.startup();
		//   demo.enable();
    }

    @Override
    public String appInfo() {
        // 应用AppID,规则见 http://d.cqp.me/Pro/开发/基础信息#appid
        String AppID = "com.meng.autoreply";// 记住编译后的文件和json也要使用appid做文件名
        return CQAPIVER + "," + AppID;
    }

    @Override
    public int startup() {
        // 获取应用数据目录(无需储存数据时，请将此行注释)
        instance = this;
        appDirectory = CQ.getAppDirectory();
		createdImageFolder = Autoreply.appDirectory + "createdImages/";
        // 返回如：D:\CoolQ\app\com.sobte.cqp.jcq\app\com.example.demo\
        System.out.println("开始加载");
		ModuleManager.instance = new ModuleManager();
		ModuleManager.instance.load();
		sender = (MessageSender) ModuleManager.getModule(MessageSender.class);
		sender.setCQ(CQ);
		cookieManager = new CookieManager();
        long startTime = System.currentTimeMillis();

        zanManager = new ZanManager();
        liveListener = new LiveListener();
        updateListener = new UpdateListener();
		naiManager = new NaiManager();
		try {
			connectServer = new RitsukageServer(9961);
			connectServer.start();
		} catch (Exception e) {}
		try {
			sanaeServer = new SanaeServer(9760);
			sanaeServer.start();
		} catch (Exception e) {}
		remoteWebSocket = new RemoteWebSocket();
		remoteWebSocket.start();
        FileTipManager fileTipManager = new FileTipManager();
        fileTipManager.addTip(807242547L, 1592608126L);
        //new TimeTipManager().start();
        SJFExecutors.execute(liveListener);
        SJFExecutors.execute(updateListener);
		//   threadPool.execute(fileTipManager);
		danmakuListenerManager = new DanmakuListenerManager();
        SJFExecutors.execute(new CleanRunnable());
		SJFExecutors.execute(new BirthdayTip());
		ConfigManager.init();
		System.out.println("加载完成,用时" + (System.currentTimeMillis() - startTime));
        return 0;
    }

    @Override
    public int exit() {
		SJFExecutors.shutdownNow();
		System.exit(0);
        return 0;
    }

    @Override
    public int enable() {
        enable = true;
        return 0;
    }

    @Override
    public int disable() {
        enable = false;
        return 0;
    }

    /**
     * 私聊消息 (Type=21)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subType 子类型，11/来自好友 1/来自在线状态 2/来自群 3/来自讨论组
     * @param msgId   消息ID
     * @param fromQQ  来源QQ
     * @param msg     消息内容
     * @param font    字体
     * @return 返回值*不能*直接返回文本 如果要回复消息，请调用api发送<br>
     * 这里 返回 {@link IMsg#MSG_INTERCEPT MSG_INTERCEPT} - 截断本条消息，不再继续处理
     * <br>
     * 注意：应用优先级设置为"最高"(10000)时，不得使用本返回值<br>
     * 如果不回复消息，交由之后的应用/过滤器处理，这里 返回 {@link IMsg#MSG_IGNORE MSG_IGNORE} -
     * 忽略本条消息
     */
    @Override
    public int privateMsg(int subType, final int msgId, final long fromQQ, final String msg, int font) {
        // if (fromQQ != 2856986197L) {
        // return MSG_IGNORE;
        // }
        if (ConfigManager.isBlockQQ(fromQQ) || ConfigManager.isBlockWord(msg)) {
            return MSG_IGNORE;
        }
        SJFExecutors.execute(new Runnable() {
				@Override
				public void run() {
					if (ConfigManager.isMaster(fromQQ)) {
						if (msg.equals("喵")) {
							sendMessage(0, fromQQ, CC.record("miao.mp3"));
							return;
						}
						if (msg.equals("娇喘")) {
							sendMessage(0, fromQQ, CC.record("mmm.mp3"));
							return;
						}
						String[] strings = msg.split("\\.", 3);
						if (strings[0].equals("send")) {
							switch (strings[2]) {
								case "喵":
									sendMessage(Long.parseLong(strings[1]), 0, CC.record("miao.mp3"));
									break;
								case "娇喘":
									sendMessage(Long.parseLong(strings[1]), 0, CC.record("mmm.mp3"));
									break;
								default:
									sendMessage(Long.parseLong(strings[1]), 0, strings[2]);
									break;
							}
							return;
						}
					}
				}
			});
		if (ModuleManager.instance.onPrivateMsg(fromQQ, msg, msgId)) {
			++RemoteWebSocket.botInfoBean.msgCmdPerSec;
		}
        return MSG_IGNORE;
    }

    /**
     * 群消息 (Type=2)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subType       子类型，目前固定为1
     * @param msgId         消息ID
     * @param fromGroup     来源群号
     * @param fromQQ        来源QQ号
     * @param fromAnonymous 来源匿名者
     * @param msg           消息内容
     * @param font          字体
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    @Override
    public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        // if (fromGroup != 312342896L)
        // return MSG_IGNORE;
		//if (fromGroup != 1023432971L)
		//return MSG_IGNORE;
		if (fromGroup != 617745343L)
			return MSG_IGNORE;
        // 如果消息来自匿名者
		if (fromQQ == 80000000L && !fromAnonymous.equals("")) {
            // 将匿名用户信息放到 anonymous 变量中
            // Anonymous anonymous = CQ.getAnonymous(fromAnonymous);
            // CQ.setGroupBan(fromGroup, anonymous.getAid(), 60);
        }
		if (ModuleManager.instance.onGroupMessage(fromGroup, fromQQ, msg, msgId)) {
			++RemoteWebSocket.botInfoBean.msgCmdPerSec;
		}
        return MSG_IGNORE;
    }

    /**
     * 讨论组消息 (Type=4)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype     子类型，目前固定为1
     * @param msgId       消息ID
     * @param fromDiscuss 来源讨论组
     * @param fromQQ      来源QQ号
     * @param msg         消息内容
     * @param font        字体
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    @Override
    public int discussMsg(int subtype, int msgId, long fromDiscuss, long fromQQ, String msg, int font) {
		if (ModuleManager.instance.onDiscussMessage(fromDiscuss, fromQQ, msg, msgId)) {
			++RemoteWebSocket.botInfoBean.msgCmdPerSec;
		}
        return MSG_IGNORE;
    }

    /**
     * 群文件上传事件 (Type=11)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subType   子类型，目前固定为1
     * @param sendTime  发送时间(时间戳)// 10位时间戳
     * @param fromGroup 来源群号
     * @param fromQQ    来源QQ号
     * @param file      上传文件信息
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    @Override
    public int groupUpload(int subType, int sendTime, long fromGroup, long fromQQ, String file) {
     	ModuleManager.instance.onGroupFileUpload(sendTime, fromGroup, fromQQ, file);
        return MSG_IGNORE;
    }

    /**
     * 群事件-管理员变动 (Type=101)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype        子类型，1/被取消管理员 2/被设置管理员
     * @param sendTime       发送时间(时间戳)
     * @param fromGroup      来源群号
     * @param beingOperateQQ 被操作QQ
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    @Override
    public int groupAdmin(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
		ModuleManager.instance.onGroupAdminChange(subtype, sendTime, fromGroup, beingOperateQQ);
        return MSG_IGNORE;
    }

    /**
     * 群事件-群成员减少 (Type=102)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype        子类型，1/群员离开 2/群员被踢
     * @param sendTime       发送时间(时间戳)
     * @param fromGroup      来源群号
     * @param fromQQ         操作者QQ(仅子类型为2时存在)
     * @param beingOperateQQ 被操作QQ
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    @Override
    public int groupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
       	ModuleManager.instance.onGroupMemberDecrease(subtype, sendTime, fromGroup, fromQQ, beingOperateQQ);
        return MSG_IGNORE;
    }

    /**
     * 群事件-群成员增加 (Type=103)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype        子类型，1/管理员已同意 2/管理员邀请
     * @param sendTime       发送时间(时间戳)
     * @param fromGroup      来源群号
     * @param fromQQ         操作者QQ(即管理员QQ)
     * @param beingOperateQQ 被操作QQ(即加群的QQ)
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    @Override
    public int groupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		ModuleManager.instance.onGroupMemberIncrease(subtype, sendTime, fromGroup, fromQQ, beingOperateQQ);
        return MSG_IGNORE;
    }

    /**
     * 好友事件-好友已添加 (Type=201)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype  子类型，目前固定为1
     * @param sendTime 发送时间(时间戳)
     * @param fromQQ   来源QQ
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    @Override
    public int friendAdd(int subtype, int sendTime, long fromQQ) {
		ModuleManager.instance.onFriendAdd(sendTime, fromQQ);
        return MSG_IGNORE;
    }

    /**
     * 请求-好友添加 (Type=301)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype      子类型，目前固定为1
     * @param sendTime     发送时间(时间戳)
     * @param fromQQ       来源QQ
     * @param msg          附言
     * @param responseFlag 反馈标识(处理请求用)
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    @Override
    public int requestAddFriend(int subtype, int sendTime, final long fromQQ, String msg, final String responseFlag) {
		ModuleManager.instance.onRequestAddFriend(sendTime, fromQQ, msg, responseFlag);
		return MSG_IGNORE;
    }

    /**
     * 请求-群添加 (Type=302)<br>
     * 本方法会在酷Q【线程】中被调用。<br>
     *
     * @param subtype      子类型，1/他人申请入群 2/自己(即登录号)受邀入群
     * @param sendTime     发送时间(时间戳)
     * @param fromGroup    来源群号
     * @param fromQQ       来源QQ
     * @param msg          附言
     * @param responseFlag 反馈标识(处理请求用)
     * @return 关于返回值说明, 见 {@link #privateMsg 私聊消息} 的方法
     */
    @Override
    public int requestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {
        // 这里处理消息
		ModuleManager.instance.onRequestAddGroup(subtype, sendTime, fromGroup, fromQQ, msg, responseFlag);
        /*
         * REQUEST_ADOPT 通过 REQUEST_REFUSE 拒绝 REQUEST_GROUP_ADD 群添加
         * REQUEST_GROUP_INVITE 群邀请
         */
		/*
         * if (fromGroup == 859561731L) { // 台长群 return MSG_IGNORE; }
         *
         * if (subtype == 1) { // 本号为群管理，判断是否为他人申请入群 if (fromQQ == 3035936740L |
         * fromQQ == 169901502L | fromQQ == 2963261413L | fromQQ == 946433685L)
         * { CQ.setGroupAddRequest(responseFlag, REQUEST_GROUP_ADD,
         * REQUEST_REFUSE, "烧饼禁入"); } else { CQ.setGroupAddRequest(responseFlag,
         * REQUEST_GROUP_ADD, REQUEST_ADOPT, null);// 同意入群
         * sendMessage(fromGroup, 0, "新人的验证信息------\n" + msg); } } else if
         * (subtype == 2) { CQ.setGroupAddRequest(responseFlag,
         * REQUEST_GROUP_INVITE, REQUEST_ADOPT, null);// 同意进受邀群 sendMessage(0,
         * 2856986197L, fromQQ + "邀请我加入群" + fromGroup); }
         */
        return MSG_IGNORE;
    }

	public static int sendMessage(long toGroup, long toQQ, String msg) {
		if (sleeping) {
            return -1;
        }
		int value=-1;
        // 处理词库中为特殊消息做的标记
		++RemoteWebSocket.botInfoBean.msgSendPerSec;
		// Tools.CQ.setRandomPop();
		try {
            if (msg.startsWith("red:")) {
                msg = msg.substring(4);
				++RemoteWebSocket.botInfoBean.msgSendPerSec;
                if (ModuleManager.getGroupModule(MGroupDic.class).onGroupMessage(toGroup, toQQ, msg, -1)) {
                    return -1;
                }
            }
            String[] stri = msg.split(":");
            switch (stri[0]) {
                case "image":
					value = instance.sender.sendGroup(toGroup, stri[2].replace("--image--", instance.CC.image(new File(appDirectory + stri[1]))));
					break;
				case "atFromQQ":
					value = instance.sender.sendGroup(toGroup, instance.CC.at(toQQ) + stri[1]);
					break;
				case "atQQ":
					value = instance.sender.sendGroup(toGroup, instance.CC.at(Long.parseLong(stri[1])) + stri[2]);
					break;
				case "imageFolder":
					File[] files = (new File(appDirectory + stri[1])).listFiles();
                    value = instance.sender.sendGroup(toGroup, stri[2].replace("--image--", instance.CC.image((File) Tools.ArrayTool.rfa(files))));
					break;
				default:
					value = instance.sender.sendGroup(toGroup, msg);
            }
			instance.remoteWebSocket.sendMsg(1, toGroup, CQ.getLoginQQ(), msg, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return value;
	}

	public static int sendMessage(long toQQ, String msg) {
		return instance.sender.sendPrivate(toQQ, msg);
	}
}
