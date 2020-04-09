package com.meng.tools;

import com.google.gson.*;
import com.meng.*;
import com.meng.config.*;
import com.meng.modules.*;
import com.sobte.cqp.jcq.entity.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.security.*;
import java.text.*;
import java.util.*;
import org.jsoup.*;

public class Tools {

	public static Map<String, String> liveHead = new HashMap<>();
    public static Map<String, String> mainHead = new HashMap<>();

	public static final String DEFAULT_ENCODING = "UTF-8";

	static{
		liveHead.put("Host", "api.live.bilibili.com");
        liveHead.put("Accept", "application/json, text/javascript, */*; q=0.01");
        liveHead.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        liveHead.put("Connection", "keep-alive");
        liveHead.put("Origin", "https://live.bilibili.com");

        mainHead.put("Host", "api.bilibili.com");
        mainHead.put("Accept", "application/json, text/javascript, */*; q=0.01");
        mainHead.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        mainHead.put("Connection", "keep-alive");
        mainHead.put("Origin", "https://www.bilibili.com");
	}

	public static class CMD {
		public static String executeCmd(String command) throws IOException {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("cmd /c " + command);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
			String line = null;
			StringBuilder build = new StringBuilder();
			while ((line = br.readLine()) != null) {
				build.append(line);
			}
			return build.toString();
		}
	}
	public static class FileTool {
		public static String readString(String fileName) {
			return readString(new File(fileName));
		}
		public static String readString(File f) {
			String s = "{}";
			try {      
				if (!f.exists()) {
					f.createNewFile();
				}
				long filelength = f.length();
				byte[] filecontent = new byte[(int) filelength];
				FileInputStream in = new FileInputStream(f);
				in.read(filecontent);
				in.close();
				s = new String(filecontent, StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return s;
		}
	}

	public static class Hash {
		public static String MD5(String str) {
			try {
				return MD5(str.getBytes());
			} catch (Exception e) {
				return null;
			}
		}

		public static String MD5(byte[] bs) {
			try {
				MessageDigest mdTemp = MessageDigest.getInstance("MD5");
				mdTemp.update(bs);
				return toHexString(mdTemp.digest());
			} catch (Exception e) {
				return null;
			}
		}

		public static String MD5(File file) {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				return MD5(inputStream);
			} catch (Exception e) {
				return null;
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		public static String MD5(InputStream inputStream) {
			try {
				MessageDigest mdTemp = MessageDigest.getInstance("MD5");
				byte[] buffer = new byte[1024];
				int numRead = 0;
				while ((numRead = inputStream.read(buffer)) > 0) {
					mdTemp.update(buffer, 0, numRead);
				}
				return toHexString(mdTemp.digest());
			} catch (Exception e) {
				return null;
			}
		}
		private static String toHexString(byte[] md) {
			char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
			int j = md.length;
			char str[] = new char[j * 2];
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[2 * i] = hexDigits[byte0 >>> 4 & 0xf];
				str[i * 2 + 1] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		}
	}

	public static class Network {
		public static Map<String, String> cookieToMap(String value) {
			Map<String, String> map = new HashMap<>();
			String[] values = value.split("; ");
			for (String val : values) {
				String[] vals = val.split("=");
				if (vals.length == 2) {
					map.put(vals[0], vals[1]);
				} else if (vals.length == 1) {
					map.put(vals[0], "");
				}
			}
			return map;
		}
		public static String getRealUrl(String surl) throws Exception {
			URL url = new URL(surl);
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			String nurl = conn.getURL().toString();
			System.out.println("realUrl" + nurl);
			in.close();
			return nurl;
		}
		public static String getSourceCode(String url) {
			return getSourceCode(url, null);
		}
		public static String getSourceCode(String url, String cookie) {
			Connection.Response response = null;
			Connection connection;
			try {
				connection = Jsoup.connect(url).ignoreContentType(true).method(Connection.Method.GET);
				if (cookie != null) {
					connection.cookies(cookieToMap(cookie));
				}
				response = connection.execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (response != null) {
				return response.body();
			} else {
				return null;
			}
		}
	}

	public static class CQ {
		public static String getTime() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		public static String getTime(long timeStamp) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeStamp));
		}
		public static String getDate() {
			return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		public static String getDate(long timeStamp) {
			return new SimpleDateFormat("yyyy-MM-dd").format(new Date(timeStamp));
		}

		public static void findQQInAllGroup(long fromGroup, long fromQQ, String msg) {
			long findqq;
			try {
				findqq = Long.parseLong(msg.substring(10));
			} catch (Exception e) {
				findqq = Autoreply.instance.CC.getAt(msg);
			}
			if (findqq <= 0) {
				Autoreply.sendMessage(fromGroup, fromQQ, "QQ账号错误");
				return;
			}
			Autoreply.sendMessage(fromGroup, fromQQ, "running");
			HashSet<Group> hashSet = findQQInAllGroup(findqq);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(findqq).append("在这些群中出现");
			for (Group l : hashSet) {
				stringBuilder.append("\n").append(l.getId()).append(l.getName());
			}
			Autoreply.sendMessage(fromGroup, fromQQ, stringBuilder.toString());
		}
		public static HashSet<Group> findQQInAllGroup(long findQQ) {
			List<Group> groups = Autoreply.CQ.getGroupList();
			HashSet<Group> hashSet = new HashSet<>();
			for (Group group : groups) {
				if (group.getId() == 959615179L || group.getId() == 666247478L) {
					continue;
				}
				ArrayList<Member> members = (ArrayList<Member>) Autoreply.CQ.getGroupMemberList(group.getId());
				for (Member member : members) {
					if (member.getQqId() == findQQ) {
						hashSet.add(group);
						break;
					}
				}
			}
			return hashSet;
		}
		public static boolean isAtme(String msg) {
			List<Long> list = Autoreply.instance.CC.getAts(msg);
			long me = Autoreply.CQ.getLoginQQ();
			for (long l : list) {
				if (l == me) {
					return true;
				}
			}
			return false;
		}
		public static boolean ban(long fromGroup, long banQQ, int time) {
			if (banQQ == 2558395159L || ConfigManager.instance.isAdmin(banQQ)) {
				return false;
			}
			Member me = Autoreply.CQ.getGroupMemberInfoV2(fromGroup, Autoreply.CQ.getLoginQQ());
			Member ban = Autoreply.CQ.getGroupMemberInfoV2(fromGroup, banQQ);
			if (me.getAuthority() - ban.getAuthority() > 0) {
				Autoreply.CQ.setGroupBan(fromGroup, banQQ, time);
				return true;
			} else {
				Member ogg = Autoreply.CQ.getGroupMemberInfoV2(fromGroup, ConfigManager.instance.configJavaBean.ogg);
				if (ogg != null && ogg.getAuthority() - ban.getAuthority() > 0) {
					Autoreply.sendMessage(Autoreply.mainGroup, 0, "~mutegroupuser " + fromGroup + " " + (time / 60) + " " + banQQ);
					return true;
				}
			}
			return false;
		}
		public static void ban(long fromGroup, HashSet<Long> banQQs, int time) {
			long[] qqs = new long[banQQs.size()];
			int i = 0;
			for (long qq : banQQs) {
				qqs[i++] = qq;
			}
			ban(fromGroup, qqs, time);
		}
		public static void ban(long fromGroup, long[] banQQs, float time) {
			Member me = Autoreply.CQ.getGroupMemberInfoV2(fromGroup, Autoreply.CQ.getLoginQQ());
			Member ogg = Autoreply.CQ.getGroupMemberInfoV2(fromGroup, ConfigManager.instance.configJavaBean.ogg);
			StringBuilder banqqs = new StringBuilder("");
			for (long banQQ : banQQs) {
				if (banQQ == 2558395159L) {
					continue;
				}
				Member ban = Autoreply.CQ.getGroupMemberInfoV2(fromGroup, banQQ);
				if (me.getAuthority() - ban.getAuthority() > 0) {
					Autoreply.CQ.setGroupBan(fromGroup, banQQ, (int)time);
				} else if (ogg != null && ogg.getAuthority() - ban.getAuthority() > 0) {
					banqqs.append(" ").append(banQQ);
				}
			}
			if (!banqqs.toString().equals("")) {
				Autoreply.sendMessage(Autoreply.mainGroup, 0, "~mutegroupuser " + fromGroup + " " + (time / 60) + banqqs.toString());
			}
		}
		public static void setRandomPop() {
			Tools.Network.getSourceCode("http://logic.content.qq.com/bubble/setup?callback=&id=" + new Random().nextInt(269) + "&g_tk=" + Autoreply.CQ.getCsrfToken(), Autoreply.CQ.getCookies());
		}

		/*  public static String getG_tk(String skey) {
		 int hash = 5381;
		 int flag = skey.length();
		 for (int i = 0; i < flag; i++) {
		 hash = hash + hash * 32 + skey.charAt(i);
		 }
		 return String.valueOf(hash & 0x7fffffff);
		 }*/
	}

	public static class ArrayTool {
		public static byte[] mergeArray(byte[]... arrays) {
			int allLen=0;
			for (byte[] bs:arrays) {
				allLen += bs.length;
			}
			byte[] finalArray=new byte[allLen];
			int flag=0;
			for (byte[] byteArray:arrays) {
				for (int i=0;i < byteArray.length;++flag,++i) {
					finalArray[flag] = byteArray[i];
				}
			}
			return finalArray;
		}

		public static String[] mergeArray(String[]... arrays) {
			int allLen=0;
			for (String[] bs:arrays) {
				allLen += bs.length;
			}
			String[] finalArray=new String[allLen];
			int flag=0;
			for (String[] byteArray:arrays) {
				for (int i=0;i < byteArray.length;++flag,++i) {
					finalArray[flag] = byteArray[i];
				}
			}
			return finalArray;
		}
		public static Object rfa(Object[] array) {
			return array[Autoreply.instance.random.nextInt(array.length)];
		}
	}

	public static class BitConverter {
		public static byte[] getBytes(short s) {
			byte[] bs=new byte[2];
			bs[0] = (byte) ((s >> 0) & 0xff);
			bs[1] = (byte) ((s >> 8) & 0xff) ;
			return bs;	
		}

		public static byte[] getBytes(int i) {
			byte[] bs=new byte[4];
			bs[0] = (byte) ((i >> 0) & 0xff);
			bs[1] = (byte) ((i >> 8) & 0xff);
			bs[2] = (byte) ((i >> 16) & 0xff);
			bs[3] = (byte) ((i >> 24) & 0xff);
			return bs;	
		}

		public static byte[] getBytes(long l) {
			byte[] bs=new byte[8];
			bs[0] = (byte) ((l >> 0) & 0xff);
			bs[1] = (byte) ((l >> 8) & 0xff);
			bs[2] = (byte) ((l >> 16) & 0xff);
			bs[3] = (byte) ((l >> 24) & 0xff);
			bs[4] = (byte) ((l >> 32) & 0xff);
			bs[5] = (byte) ((l >> 40) & 0xff);
			bs[6] = (byte) ((l >> 48) & 0xff);
			bs[7] = (byte) ((l >> 56) & 0xff);
			return bs;
		}

		public static byte[] getBytes(float f) {
			int i = Float.floatToIntBits(f);
			byte[] bs=new byte[4];
			bs[0] = (byte) ((i >> 24) & 0xff);
			bs[1] = (byte) ((i >> 16) & 0xff);
			bs[2] = (byte) ((i >> 8) & 0xff);
			bs[3] = (byte) ((i >> 0) & 0xff);
			return bs;	
		}

		public static byte[] getBytes(double d) {
			long l = Double.doubleToLongBits(d);
			byte[] bs = new byte[8];
			bs[0] = (byte) ((l >> 56) & 0xff);
			bs[1] = (byte) ((l >> 48) & 0xff);
			bs[2] = (byte) ((l >> 40) & 0xff);
			bs[3] = (byte) ((l >> 32) & 0xff);
			bs[4] = (byte) ((l >> 24) & 0xff);
			bs[5] = (byte) ((l >> 16) & 0xff);
			bs[6] = (byte) ((l >> 8) & 0xff);
			bs[7] = (byte) ((l >> 0) & 0xff);
			return bs;
		}

		public static byte[] getBytes(String s) {
			try {
				return s.getBytes(DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}

		public static short toShort(byte[] data, int pos) {
			return (short) ((data[pos] & 0xff) << 0 | (data[pos + 1] & 0xff) << 8);
		}

		public static short toShort(byte[] data) {
			return toShort(data , 0);
		}

		public static int toInt(byte[] data, int pos) {
			return (data[pos] & 0xff) << 0 | (data[pos + 1] & 0xff) << 8 | (data[pos + 2] & 0xff) << 16 | (data[pos + 3] & 0xff) << 24;
		}

		public static int toInt(byte[] data) {
			return toInt(data, 0);
		}

		public static long toLong(byte[] data, int pos) {
			return ((data[pos] & 0xffL) << 0) | (data[pos + 1] & 0xffL) << 8 | (data[pos + 2] & 0xffL) << 16 | (data[pos + 3] & 0xffL) << 24 | (data[pos + 4] & 0xffL) << 32 | (data[pos + 5] & 0xffL) << 40 | (data[pos + 6] & 0xffL) << 48 | (data[pos + 7] & 0xffL) << 56;
		}

		public static long toLong(byte[] data) {
			return toLong(data , 0);
		}

		public static float toFloat(byte[] data, int pos) {
			int i= (data[pos] & 0xff) << 24 | (data[pos + 1] & 0xff) << 16 | (data[pos + 2] & 0xff) << 8 | (data[pos + 3] & 0xff) << 0;
			return Float.intBitsToFloat(i);
		}

		public static float toFloat(byte[] data) {
			return toFloat(data , 0);
		}

		public static double toDouble(byte[] data, int pos) {
			long l = ((data[pos] & 0xffL) << 56) | (data[pos + 1] & 0xffL) << 48 | (data[pos + 2] & 0xffL) << 40 | (data[pos + 3] & 0xffL) << 32 | (data[pos + 4] & 0xffL) << 24 | (data[pos + 5] & 0xffL) << 16 | (data[pos + 6] & 0xffL) << 8 | (data[pos + 7] & 0xffL) << 0;
			return Double.longBitsToDouble(l);
		}

		public static double toDouble(byte[] data) {
			return toDouble(data, 0);
		}

		public static String toString(byte[] data, int pos, int byteCount) {
			try {
				return new String(data, pos, byteCount, DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}

		public static String toString(byte[] data) {
			return toString(data, 0, data.length);
		}
	}

	public static class Base64 {
		public static final byte[] encode(String str) {
			try {
				return encode(str.getBytes(DEFAULT_ENCODING));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
		public static final byte[] encode(byte[] byteData) {
			if (byteData == null) { 
				throw new IllegalArgumentException("byteData cannot be null");
			}
			int iSrcIdx; 
			int iDestIdx; 
			byte[] byteDest = new byte[((byteData.length + 2) / 3) * 4];
			for (iSrcIdx = 0, iDestIdx = 0; iSrcIdx < byteData.length - 2; iSrcIdx += 3) {
				byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx] >>> 2) & 077);
				byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx + 1] >>> 4) & 017 | (byteData[iSrcIdx] << 4) & 077);
				byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx + 2] >>> 6) & 003 | (byteData[iSrcIdx + 1] << 2) & 077);
				byteDest[iDestIdx++] = (byte) (byteData[iSrcIdx + 2] & 077);
			}
			if (iSrcIdx < byteData.length) {
				byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx] >>> 2) & 077);
				if (iSrcIdx < byteData.length - 1) {
					byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx + 1] >>> 4) & 017 | (byteData[iSrcIdx] << 4) & 077);
					byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx + 1] << 2) & 077);
				} else {
					byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx] << 4) & 077);
				}
			}
			for (iSrcIdx = 0; iSrcIdx < iDestIdx; iSrcIdx++) {
				if (byteDest[iSrcIdx] < 26) {
					byteDest[iSrcIdx] = (byte) (byteDest[iSrcIdx] + 'A');
				} else if (byteDest[iSrcIdx] < 52) {
					byteDest[iSrcIdx] = (byte) (byteDest[iSrcIdx] + 'a' - 26);
				} else if (byteDest[iSrcIdx] < 62) {
					byteDest[iSrcIdx] = (byte) (byteDest[iSrcIdx] + '0' - 52);
				} else if (byteDest[iSrcIdx] < 63) {
					byteDest[iSrcIdx] = '+';
				} else {
					byteDest[iSrcIdx] = '/';
				}
			}
			for (; iSrcIdx < byteDest.length; iSrcIdx++) {
				byteDest[iSrcIdx] = '=';
			}
			return byteDest;
		}

		public final static byte[] decode(String str) throws IllegalArgumentException {
			byte[] byteData = null;
			try {
				byteData = str.getBytes(DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (byteData == null) { 
				throw new IllegalArgumentException("byteData cannot be null");
			}
			int iSrcIdx; 
			int reviSrcIdx; 
			int iDestIdx; 
			byte[] byteTemp = new byte[byteData.length];
			for (reviSrcIdx = byteData.length; reviSrcIdx - 1 > 0 && byteData[reviSrcIdx - 1] == '='; reviSrcIdx--) {
				; // do nothing. I'm just interested in value of reviSrcIdx
			}
			if (reviSrcIdx - 1 == 0)	{ 
				return null; 
			}
			byte byteDest[] = new byte[((reviSrcIdx * 3) / 4)];
			for (iSrcIdx = 0; iSrcIdx < reviSrcIdx; iSrcIdx++) {
				if (byteData[iSrcIdx] == '+') {
					byteTemp[iSrcIdx] = 62;
				} else if (byteData[iSrcIdx] == '/') {
					byteTemp[iSrcIdx] = 63;
				} else if (byteData[iSrcIdx] < '0' + 10) {
					byteTemp[iSrcIdx] = (byte) (byteData[iSrcIdx] + 52 - '0');
				} else if (byteData[iSrcIdx] < ('A' + 26)) {
					byteTemp[iSrcIdx] = (byte) (byteData[iSrcIdx] - 'A');
				}  else if (byteData[iSrcIdx] < 'a' + 26) {
					byteTemp[iSrcIdx] = (byte) (byteData[iSrcIdx] + 26 - 'a');
				}
			}
			for (iSrcIdx = 0, iDestIdx = 0; iSrcIdx < reviSrcIdx && iDestIdx < ((byteDest.length / 3) * 3); iSrcIdx += 4) {
				byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx] << 2) & 0xFC | (byteTemp[iSrcIdx + 1] >>> 4) & 0x03);
				byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx + 1] << 4) & 0xF0 | (byteTemp[iSrcIdx + 2] >>> 2) & 0x0F);
				byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx + 2] << 6) & 0xC0 | byteTemp[iSrcIdx + 3] & 0x3F);
			}
			if (iSrcIdx < reviSrcIdx) {
				if (iSrcIdx < reviSrcIdx - 2) {
					byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx] << 2) & 0xFC | (byteTemp[iSrcIdx + 1] >>> 4) & 0x03);
					byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx + 1] << 4) & 0xF0 | (byteTemp[iSrcIdx + 2] >>> 2) & 0x0F);
				} else if (iSrcIdx < reviSrcIdx - 1) {
					byteDest[iDestIdx++] = (byte) ((byteTemp[iSrcIdx] << 2) & 0xFC | (byteTemp[iSrcIdx + 1] >>> 4) & 0x03);
				}  else {
					throw new IllegalArgumentException("Warning: 1 input bytes left to process. This was not Base64 input");
				}
			}
			return byteDest;
		}
	}

	public static class BilibiliTool {

		public static String getCvInfo(long cvId) {
			return Tools.Network.getSourceCode("http://api.bilibili.com/x/article/viewinfo?id=" + cvId + "&mobi_app=pc&jsonp=jsonp");
		}

		/*public static void startWatchLive(int posInAccountList) {
		 Intent intentOne = new Intent(Autoreply.instance, GuaJiService.class);
		 intentOne.putExtra("pos", posInAccountList);
		 Autoreply.instance.startService(intentOne);
		 }
		 */
		public static String getMyInfo(String cookie) {
			return Tools.Network.getSourceCode("http://api.bilibili.com/x/space/myinfo?jsonp=jsonp", cookie);
		}

		public static String getLiveRoomInfo(long uid) {
			return Tools.Network.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid);
		}

		public static String getRelation(long uid) {
			return Tools.Network.getSourceCode("https://api.bilibili.com/x/relation/stat?vmid=" + uid + "&jsonp=jsonp");
		}

		public static String getUpstat(long uid) {
			return Tools.Network.getSourceCode("https://api.bilibili.com/x/space/upstat?mid=" + uid + "&jsonp=jsonp");
		}

		public static boolean setBan(long roomId, long blockId, String hour) {
			if (Integer.parseInt(hour) == 0) {  
				String jsonStr=Tools.Network.getSourceCode("https://api.live.bilibili.com/liveact/ajaxGetBlockList?roomid=" + roomId + "&page=1", Autoreply.instance.cookieManager.getGrzx());
				BanBean bb=new Gson().fromJson(jsonStr, BanBean.class);
				String eventId="";
				for (BanBean.Data data:bb.data) {
					if (data.uid == blockId) {
						eventId += data.id;
						break;
					}
				}
				Connection.Response response = null;
				try {
					Connection connection = Jsoup.connect("https://api.live.bilibili.com/banned_service/v1/Silent/del_room_block_user");
					String csrf = Tools.Network.cookieToMap(Autoreply.instance.cookieManager.getGrzx()).get("bili_jct");
					connection.userAgent(Autoreply.instance.userAgent)
						.headers(liveHead)
						.ignoreContentType(true)
						.referrer("https://live.bilibili.com/" + roomId)
						.cookies(Tools.Network.cookieToMap(Autoreply.instance.cookieManager.getGrzx()))
						.method(Connection.Method.POST)
						.data("roomid", String.valueOf(roomId))
						.data("id", eventId)
						.data("csrf_token", csrf)
						.data("csrf", csrf)
						.data("visit_id", "");
					response = connection.execute();
					if (response.statusCode() != 200) {
						return false;
					}
					JsonObject obj = new JsonParser().parse(response.body()).getAsJsonObject();
					if (obj.get("code").getAsInt() == 0 && obj.get("message").getAsString().equals("")) {
						return true;
					}
				} catch (Exception e) {
					return false;
				} 
			} else {
				Connection.Response response = null;
				try {
					Connection connection = Jsoup.connect("https://api.live.bilibili.com/banned_service/v2/Silent/add_block_user");
					String csrf = Tools.Network.cookieToMap(Autoreply.instance.cookieManager.getGrzx()).get("bili_jct");
					connection.userAgent(Autoreply.instance.userAgent)
						.headers(liveHead)
						.ignoreContentType(true)
						.referrer("https://live.bilibili.com/" + roomId)
						.cookies(Tools.Network.cookieToMap(Autoreply.instance.cookieManager.getGrzx()))
						.method(Connection.Method.POST)
						.data("hour", hour)
						.data("roomid", String.valueOf(roomId))
						.data("block_uid", String.valueOf(blockId))
						.data("csrf_token", csrf)
						.data("csrf", csrf)
						.data("visit_id", "");
					response = connection.execute();
					if (response.statusCode() != 200) {
						return false;
					}
					JsonObject obj = new JsonParser().parse(response.body()).getAsJsonObject();
					if (obj.get("code").getAsInt() == 0 && obj.get("message").getAsString().equals("")) {
						return true;			
					}
				} catch (Exception e) {
					return false;
				}
			}
			return false;
		}

		public static void sendArticalJudge(long cvId, String msg, String cookie) {
			Connection connection = Jsoup.connect("https://api.bilibili.com/x/v2/reply/add");
			String csrf = Tools.Network.cookieToMap(cookie).get("bili_jct");
			connection.userAgent(Autoreply.instance.userAgent)
                .headers(mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/")
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("oid", String.valueOf(cvId))
                .data("type", "12")//似乎是固定12 目前还没发现不是12的
                .data("message", msg)
                .data("plat", "1")
                .data("jsonp", "jsonp")
                .data("csrf", csrf);
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(response.body()).getAsJsonObject();
				System.out.println(obj.get("message").getAsString());
			}
		}

		public static String startLive(long roomID, String cookie) throws IOException {
			Connection connection = Jsoup.connect("https://api.live.bilibili.com/room/v1/Room/startLive");
			String csrf = Tools.Network.cookieToMap(cookie).get("bili_jct");
			connection.userAgent(Autoreply.instance.userAgent)
			    .headers(liveHead)
			    .ignoreContentType(true)
			    .referrer("https://link.bilibili.com/p/center/index")
			    .cookies(Tools.Network.cookieToMap(cookie))
			    .method(Connection.Method.POST)
			    .data("room_id", String.valueOf(roomID))
			    .data("platform", "pc")
			    .data("area_v2", "235")
			    .data("csrf_token", csrf)
			    .data("csrf", csrf);
			Connection.Response response = connection.execute();
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
			}
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response.body()).getAsJsonObject();
			// System.out.println(obj.get("message").getAsString());
			if (obj.get("code").getAsInt() == 0) {
				return "开播成功";
			}
			return obj.get("message").getAsString();
		}

		public static String stopLive(int roomID, String cookie) throws IOException {
			Connection connection = Jsoup.connect("https://api.live.bilibili.com/room/v1/Room/stopLive");
			String csrf = Tools.Network.cookieToMap(cookie).get("bili_jct");
			connection.userAgent(Autoreply.instance.userAgent).
				headers(liveHead).
				ignoreContentType(true).
				referrer("https://link.bilibili.com/p/center/index").
				cookies(Tools.Network.cookieToMap(cookie)).
				method(Connection.Method.POST).
				data("room_id", String.valueOf(roomID)).
				data("csrf_token", csrf).
				data("csrf", csrf);
			Connection.Response response = connection.execute();
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
			}
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response.body()).getAsJsonObject();
			if (obj.get("code").getAsInt() == 0) {
				return "关闭成功";
			}
			return obj.get("message").getAsString();

		}

		public static String renameLive(int roomID, String newName, String cookie) throws IOException {
			Connection connection = Jsoup.connect("https://api.live.bilibili.com/room/v1/Room/update");
			String csrf = Tools.Network.cookieToMap(cookie).get("bili_jct");
		    connection.userAgent(Autoreply.instance.userAgent)
			    .headers(liveHead)
			    .ignoreContentType(true)
			    .referrer("https://link.bilibili.com/p/center/index")
			    .cookies(Tools.Network.cookieToMap(cookie))
			    .method(Connection.Method.POST)
			    .data("room_id", String.valueOf(roomID))
			    .data("title", newName)
			    .data("csrf_token", csrf)
			    .data("csrf", csrf);
			Connection.Response response = connection.execute();
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
			}
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response.body()).getAsJsonObject();
			return obj.get("message").getAsString();
		}

		public static int sendLiveSign(String cookie) {
			Connection connection = Jsoup.connect("https://api.live.bilibili.com/sign/doSign");
			connection.userAgent(Autoreply.instance.userAgent)
                .headers(liveHead)
                .ignoreContentType(true)
                .referrer("https://live.bilibili.com/" + new Random().nextInt() % 9721949)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.GET);
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}	if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
			}
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response.body()).getAsJsonObject();
			return obj.get("code").getAsInt();
		}

		public static void sendHotStrip(long myUid, long roomMasterUid, long roomID, int count, String cookie) {
			Connection connection = Jsoup.connect("http://api.live.bilibili.com/gift/v2/gift/send");
			String csrf = Tools.Network.cookieToMap(cookie).get("bili_jct");
			connection.userAgent(Autoreply.instance.userAgent)
                .headers(liveHead)
                .ignoreContentType(true)
                .referrer("https://live.bilibili.com/" + roomID)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("uid", String.valueOf(myUid))
                .data("gift_id", "1")
                .data("ruid", String.valueOf(roomMasterUid))
                .data("gift_num", String.valueOf(count))
                .data("coin_type", "silver")
                .data("bag_id", "0")
                .data("platform", "pc")
                .data("biz_code", "live")
                .data("biz_id", String.valueOf(roomID))
                .data("rnd", String.valueOf(System.currentTimeMillis() / 1000))
                .data("metadata", "")
                .data("price", "0")
                .data("csrf_token", csrf)
                .data("csrf", csrf)
                .data("visit_id", "");
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(response.body()).getAsJsonObject();
				System.out.println((obj.get("message").getAsString()));
			}
		}

		public static void followUser(String cookie, long UID) {
			Connection conn1 = Jsoup.connect("https://api.bilibili.com/x/relation/modify?cross_domain=true");
			conn1.userAgent(Autoreply.instance.userAgent)
                .headers(mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + new Random().nextInt() % 47957369)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("fid", String.valueOf(UID))
                .data("act", "1")
                .data("re_src", "122")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response res1=null;
			try {
				res1 = conn1.execute();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (res1.statusCode() != 200) {
				System.out.println(res1.statusCode());
				return;
			}
			JsonParser parser = new JsonParser();
			//JsonObject obj1 = parser.parse(res1.body()).getAsJsonObject();
			//Autoreply.instence.showToast(obj1.get("message").getAsString());
			Connection conn2 = Jsoup.connect("https://api.bilibili.com/x/relation/tags/addUsers?cross_domain=true");
			conn2.userAgent(Autoreply.instance.userAgent)
                .headers(mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + new Random().nextInt() % 47957369)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("fids", String.valueOf(UID))
                .data("tagids", "0")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response res2=null;
			try {
				res2 = conn2.execute();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (res2.statusCode() != 200) {
				System.out.println(res2.statusCode());
				return;
			}
			JsonObject obj2 = parser.parse(res2.body()).getAsJsonObject();
			System.out.println(obj2.get("message").getAsString());
		}

		public static void sendCvCoin(int count, long CvId, String cookie) {
			Connection connection = Jsoup.connect("https://api.bilibili.com/x/web-interface/coin/add");
			connection.userAgent(Autoreply.instance.userAgent)
                .headers(mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/read/cv" + CvId)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("aid", String.valueOf(CvId))
                .data("multiply", String.valueOf(count))
			    .data("upid", String.valueOf(Autoreply.instance.gson.fromJson(Tools.BilibiliTool.getCvInfo(CvId), CvInfo.class).data.mid))
                .data("avtype", "2")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(response.body()).getAsJsonObject();
				System.out.println(obj.get("message").getAsString());
			}
		}

		public static void sendAvCoin(int count, long AID, String cookie) {
			Connection connection = Jsoup.connect("https://api.bilibili.com/x/web-interface/coin/add");
			connection.userAgent(Autoreply.instance.userAgent)
                .headers(mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + AID)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("aid", String.valueOf(AID))
                .data("multiply", String.valueOf(count))
                .data("select_like", "0")
                .data("cross_domain", "true")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(response.body()).getAsJsonObject();
				System.out.println(obj.get("message").getAsString());
			}
		}

		public static void sendVideoJudge(String msg, long AID, String cookie) {
			Connection connection = Jsoup.connect("https://api.bilibili.com/x/v2/reply/add");
			connection.userAgent(Autoreply.instance.userAgent)
                .headers(mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + AID)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("oid", String.valueOf(AID))
                .data("type", "1")
                .data("message", msg)
                .data("jsonp", "jsonp")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(response.body()).getAsJsonObject();
				System.out.println(obj.get("message").getAsString());
			}
		}

		public static void sendCvLike(long cvID, String cookie) {
			Connection connection = Jsoup.connect("https://api.bilibili.com/x/article/like");
			connection.userAgent(Autoreply.instance.userAgent)
                .headers(mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/read/cv" + cvID)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("id", String.valueOf(cvID))
                .data("type", "1")
			    .data("jsonp", "jsonp")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(response.body()).getAsJsonObject();
				System.out.println(obj.get("message").getAsString());
			}
		}

		public static void sendAvLike(long AID, String cookie) {
			Connection connection = Jsoup.connect("https://api.bilibili.com/x/web-interface/archive/like");
			connection.userAgent(Autoreply.instance.userAgent)
                .headers(mainHead)
                .ignoreContentType(true)
                .referrer("https://www.bilibili.com/video/av" + AID)
                .cookies(Tools.Network.cookieToMap(cookie))
                .method(Connection.Method.POST)
                .data("aid", String.valueOf(AID))
                .data("like", "1")
                .data("csrf", Tools.Network.cookieToMap(cookie).get("bili_jct"));
			Connection.Response response=null;
			try {
				response = connection.execute();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (response.statusCode() != 200) {
				System.out.println(response.statusCode());
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(response.body()).getAsJsonObject();
				System.out.println(obj.get("message").getAsString());
			}
		}

		public static void sendLiveDanmaku(String msg, String cookie, long roomId) {
			Connection.Response response = null;
			try {
				Connection connection = Jsoup.connect("http://api.live.bilibili.com/msg/send");
				String csrf = Tools.Network.cookieToMap(cookie).get("bili_jct");
			    connection.userAgent(Autoreply.instance.userAgent)
				    .headers(liveHead)
				    .ignoreContentType(true)
				    .referrer("https://live.bilibili.com/" + roomId)
				    .cookies(Tools.Network.cookieToMap(cookie))
				    .method(Connection.Method.POST)
				    .data("color", "16777215")
				    .data("fontsize", "25")
				    .data("msg", msg)
				    .data("rnd", String.valueOf(System.currentTimeMillis() / 1000))
				    .data("roomid", String.valueOf(roomId))
				    .data("bubble", "0")
				    .data("csrf_token", csrf)
				    .data("csrf", csrf);
				response = connection.execute();
				if (response.statusCode() != 200) {
					System.out.println(response.statusCode());
				}
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(response.body()).getAsJsonObject();
				switch (obj.get("code").getAsInt()) {
					case 0:
						if (!obj.get("message").getAsString().equals("")) {
							System.out.println(obj.getAsJsonObject("message").getAsString());
						} else {
							System.out.println(roomId + "发送成功");
						}
						break;
					case 1990000:
						System.out.println("需要在官方客户端进行账号风险验证");
						break;
					default:
						System.out.println(response.body());
						break;
				}
			} catch (Exception e) {
				if (response != null) {
					System.out.println(response.body());
				}
			}
		}
	}
	public class BanBean {
		public int code;
		public String msg;
		public String  message;
		public ArrayList<Data> data;

		public class Data {
			public long id;
			public long roomid;
			public long uid;
			public int type;
			public long adminid;
			public String block_end_time;
			public String ctime;
			public String msg;
			public String msg_time;
			public String uname;
			public String admin_name;
		}
	}
}
