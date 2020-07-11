package com.meng.modules;

import com.google.gson.*;
import com.meng.*;
import com.meng.bilibili.main.*;
import com.meng.config.*;
import com.meng.config.javabeans.*;
import com.meng.SJFInterfaces.*;
import com.meng.tools.*;

public class MBiliUpdate extends BaseGroupModule {

    private String[] words = new String[]{"更了吗", "出来更新", "什么时候更新啊", "在？看看更新", "怎么还不更新", "更新啊草绳"};

	@Override
	public MBiliUpdate load() {
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (msg.contains("今天更了吗") && isUpper(msg.substring(0, msg.indexOf("今天更了吗")))) {
            long videoUpdateTime = 0;
            long articalUpdateTime = 0;
            Gson gson = new Gson();
            NewVideoBean.Data.Vlist vlist = null;
            NewArticleBean.Data.Articles articles = null;
            int upId = getUpId(msg.substring(0, msg.indexOf("今天更了吗")));
            if (upId == 0) {
                return false;
            }
            try {
                vlist = gson.fromJson(Tools.Network.getSourceCode("https://space.bilibili.com/ajax/member/getSubmitVideos?mid=" + upId + "&page=1&pagesize=1").replace("\"3\":", "\"n3\":").replace("\"4\":", "\"n4\":"), NewVideoBean.class).data.vlist.get(0);
            } catch (Exception e) {
            }
            try {
                articles = gson.fromJson(Tools.Network.getSourceCode("http://api.bilibili.com/x/space/article?mid=" + upId + "&pn=1&ps=1&sort=publish_time&jsonp=jsonp"), NewArticleBean.class).data.articles.get(0);
            } catch (Exception e) {
            }
            if (vlist != null && articles == null) {
                videoUpdateTime = vlist.created * 1000;
                tipVideo(fromGroup, msg, videoUpdateTime, vlist);
            } else if (vlist == null && articles != null) {
                articalUpdateTime = articles.publish_time * 1000;
                tipArticle(fromGroup, msg, articalUpdateTime, articles);
            } else if (vlist != null && articles != null) {
                videoUpdateTime = vlist.created * 1000;
                articalUpdateTime = articles.publish_time * 1000;
                if (articalUpdateTime > videoUpdateTime) {
                    tipArticle(fromGroup, msg, articalUpdateTime, articles);
                } else {
                    tipVideo(fromGroup, msg, videoUpdateTime, vlist);
                }
            }
            return true;
        }
        return false;
    }

    private void tipVideo(long fromGroup, String msg, long videoUpdateTime, NewVideoBean.Data.Vlist vlist) {
        if (System.currentTimeMillis() - videoUpdateTime < 86400000) {
            Autoreply.sendMessage(fromGroup, 0, "更新莉,,,https://www.bilibili.com/video/av" + vlist.aid);
		} else {
            Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.at(getUpQQ(msg.substring(0, msg.indexOf("今天更了吗")))) + Tools.ArrayTool.rfa(words));
            int days = (int) ((System.currentTimeMillis() - videoUpdateTime) / 86400000);
            if (days <= 30) {
                Autoreply.sendMessage(fromGroup, 0, "你都" + days + "天没更新了");
            } else {
                Autoreply.sendMessage(fromGroup, 0, +days + "天不更新,你咕你[CQ:emoji,id=128052]呢");
            }
        }
    }

    private void tipArticle(long fromGroup, String msg, long articalUpdateTime, NewArticleBean.Data.Articles articles) {
        if (System.currentTimeMillis() - articalUpdateTime < 86400000) {
            Autoreply.sendMessage(fromGroup, 0, "更新莉,,,https://www.bilibili.com/read/cv" + articles.id);
        } else {
            Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.at(getUpQQ(msg.substring(0, msg.indexOf("今天更了吗")))) + Tools.ArrayTool.rfa(words));
            int days = (int) ((System.currentTimeMillis() - articalUpdateTime) / 86400000);
            if (days <= 30) {
                Autoreply.sendMessage(fromGroup, 0, "你都" + days + "天没更新了");
            } else {
                Autoreply.sendMessage(fromGroup, 0, +days + "天不更新,你咕你[CQ:emoji,id=128052]呢");
            }
        }
    }

    private boolean isUpper(String msg) {
        for (PersonInfo cb : ConfigManager.instance.configJavaBean.personInfo) {
            if (msg.equals(cb.name) && cb.bid != 0) {
                return true;
            }
        }
        return false;
    }

    private int getUpId(String msg) {
        for (PersonInfo cb : ConfigManager.instance.configJavaBean.personInfo) {
            if (cb.bid == 0) {
                continue;
            }
            if (msg.equals(cb.name)) {
                return cb.bid;
            }
        }
        return 0;
    }

    private long getUpQQ(String msg) {
        for (PersonInfo cb : ConfigManager.instance.configJavaBean.personInfo) {
            if (msg.equals(cb.name)) {
                return cb.qq;
            }
        }
        return 0;
    }
}
