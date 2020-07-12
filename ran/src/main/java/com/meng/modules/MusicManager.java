package com.meng.modules;

import com.meng.*;
import com.meng.config.*;
import com.meng.gameData.TouHou.zun.*;
import com.meng.SJFInterfaces.*;
import com.meng.tools.*;
import java.io.*;
import java.util.*;

public class MusicManager extends BaseGroupModule {
	public static String musicFolder="C://thbgm/";
	private HashMap<Long,QA> resultMap=new HashMap<>();

	@Override
	public MusicManager load() {
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if (!ConfigManager.instance.getGroupConfig(fromGroup).isMusicEnable()) {
			return false;
		}
		judgeAnswer(fromGroup, fromQQ, msg);
		if (msg.equals("原曲认知")) {
			File musicFragment=createMusicCut(new Random().nextInt(16), 10, fromGroup, fromQQ);
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.record(musicFragment.getName()));	
			return true;
		}
		if (msg.startsWith("原曲认知 ")) {
			switch (msg) {
				case "原曲认知 E":
				case "原曲认知 e":
				case "原曲认知 easy":
				case "原曲认知 Easy":
					Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.record(createMusicCut(new Random().nextInt(16), 10, fromGroup, fromQQ).getName()));	
					break;
				case "原曲认知 N":
				case "原曲认知 n":
				case "原曲认知 normal":
				case "原曲认知 Normal":
					Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.record(createMusicCut(new Random().nextInt(16), 6, fromGroup, fromQQ).getName()));
					break;
				case "原曲认知 H":
				case "原曲认知 h":
				case "原曲认知 hard":
				case "原曲认知 Hard":
					Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.record(createMusicCut(new Random().nextInt(16), 3, fromGroup, fromQQ).getName()));
					break;
				case "原曲认知 L":
				case "原曲认知 l":
				case "原曲认知 lunatic":
				case "原曲认知 Lunatic":
					Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.record(createMusicCut(new Random().nextInt(16), 1, fromGroup, fromQQ).getName()));
					break;		
			}
		}
		return false;
	}

	private File createMusicCut(int musicNum, int needSeconeds, long fromGroup,  long fromQQ) {
		File[] games=new File(musicFolder).listFiles();
		int game=new Random().nextInt(games.length);
		File fmtFile = new File(musicFolder + games[game].getName() + "/thbgm.fmt");
		File resultFile=null;
		THfmt thfmt = new THfmt(fmtFile);
        thfmt.load();
		MusicInfo muiscInfo=thfmt.musicInfos[musicNum];
		byte[] music=new byte[needSeconeds * muiscInfo.bitsPerSample * muiscInfo.channels * muiscInfo.rate / 8];
		readFile(music, getStartBytes(musicNum, thfmt, needSeconeds), games[game].getName());
		WavHeader wavHeader=new WavHeader();
		byte[] finalFile=Tools.ArrayTool.mergeArray(wavHeader.getWavHeader(musicNum, thfmt, needSeconeds), music);
		final String newFileName="C://Users/Administrator/Desktop/酷Q Pro/data/record/" + System.currentTimeMillis() + ".wav";
		try {
			resultFile = new File(newFileName);
			if (resultFile.exists()) {
				resultFile.delete();
			}
			FileOutputStream fom=new FileOutputStream(resultFile);
			fom.write(finalFile, 0, finalFile.length);
			fom.flush();
			fom.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Autoreply.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {}
					new File(newFileName).delete();
				}
			});
		QA qa = new QA();
		qa.setTrueAns(0);
		switch (games[game].getName()) {
			case "th10":
				qa.a.add(TH10GameData.musicName[musicNum]);
				break;
			case "th11":
				qa.a.add(TH11GameData.musicName[musicNum]);
				break;
			case "th12":
				qa.a.add(TH12GameData.musicName[musicNum]);
				break;
			case "th14":
				qa.a.add(TH14GameData.musicName[musicNum]);
				break;
			case "th15":
				qa.a.add(TH15GameData.musicName[musicNum]);
				break;
			case "th16":
				qa.a.add(TH16GameData.musicName[musicNum]);
				break;
		}
		randomMisic(qa.a);
		qa.exangeAnswer(0);
		resultMap.put(fromQQ, qa);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		sb.append("名字是:\n");
		for (String s:qa.a) {
			sb.append(i++).append(": ").append(s).append("\n");
		}
		sb.append("回答序号即可");
		Autoreply.sendMessage(fromGroup, 0, sb.toString());
		return resultFile;
	}

	private void randomMisic(ArrayList<String> list) {
		for (int i=0;i < 3 ;++i) {
			while (true) {
				String musicName=MDiceImitate.music[new Random().nextInt(MDiceImitate.music.length)];
				if (!list.contains(musicName)) {
					list.add(musicName);
					break;
				}
			}
		}
	}

	private void judgeAnswer(long fromGroup, long fromQQ, String msg) {
		QA qar = resultMap.get(fromQQ);
		if (qar != null && msg.equalsIgnoreCase("-qar")) {
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.at(fromQQ) + "你还没有回答");
			return ;
		}
		if (qar != null) {
			int userAnser=-1;
			try {
				userAnser = Integer.parseInt(msg);
			} catch (NumberFormatException e) {}
			if (qar.getTrueAns().contains(userAnser) && qar.getTrueAns().size() == 1) {
				Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.at(fromQQ) + "回答正确");
			} else {
				Autoreply.sendMessage(fromGroup, 0, String.format("%s回答错误", Autoreply.instance.CC.at(fromQQ)));
			}
		}
		resultMap.remove(fromQQ);
	}

	private int getStartBytes(int musicNum, THfmt thfmt, int needSeconeds) {
		MusicInfo muiscInfo=thfmt.musicInfos[musicNum];
		int oneFrameBytes=muiscInfo.bitsPerSample / 8 * muiscInfo.channels;	
		int startFtame=new Random().nextInt(muiscInfo.length / oneFrameBytes);
		int SecNeedBytes=needSeconeds * muiscInfo.bitsPerSample * muiscInfo.channels * muiscInfo.rate / 8;
		int questionStartBytes=muiscInfo.start + startFtame * oneFrameBytes;
		if (muiscInfo.length - startFtame * oneFrameBytes < SecNeedBytes) {
			questionStartBytes = startFtame * oneFrameBytes - SecNeedBytes;
		}
		return questionStartBytes;
	}

	private byte[] readFile(byte[] data, int offset, String name) {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(musicFolder + name + "/thbgm.dat", "r");
            randomAccessFile.seek(offset);
            randomAccessFile.readFully(data);
            randomAccessFile.close();
        } catch (Exception e) {
            throw new RuntimeException("bgm read failed");
        }
        return data;
    }

	private class THfmt {
		private int position = 0;
		private byte[] fileByte;
		private MusicInfo[] musicInfos;
		private String[] names;
		public THfmt(File file) {
			if (!file.exists()) {
				throw new RuntimeException("file not found:" + file.getAbsolutePath());
			}
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				fileByte = new byte[(int) file.length()];
				fileInputStream.read(fileByte);
				fileInputStream.close();
			} catch (Exception e) {
				throw new RuntimeException("read file failed:" + file.getAbsolutePath());
			}
			musicInfos = new MusicInfo[(int) (file.length() / 52)];
			names = new String[musicInfos.length];
		}
		public void load() {
			for (int i = 0; i < musicInfos.length; ++i) {
				MusicInfo musicInfo = new MusicInfo();
				musicInfo.name = readName();
				musicInfo.start = readInt();
				musicInfo.unknown1 = readInt();
				musicInfo.repeatStart = readInt();
				musicInfo.length = readInt();
				musicInfo.format = readShort();
				musicInfo.channels = readShort();
				musicInfo.rate = readInt();
				musicInfo.avgBytesPerSec = readInt();
				musicInfo.blockAlign = readShort();
				musicInfo.bitsPerSample = readShort();
				musicInfo.cbSize = readShort();
				musicInfo.pad = readShort();
				String name = new String(musicInfo.name);
				names[i] = name.substring(0, name.indexOf(0));
				musicInfos[i] = musicInfo;
			} 
		}
		private short readShort() {
			return (short) (fileByte[position++] & 0xff | (fileByte[position++] & 0xff) << 8);
		}
		private int readInt() {
			return (fileByte[position++] & 0xff) | (fileByte[position++] & 0xff) << 8 | (fileByte[position++] & 0xff) << 16 | (fileByte[position++] & 0xff) << 24;
		}
		private byte[] readName() {
			byte[] ba = new byte[16];
			for (int i = 0; i < ba.length; ++i) {
				ba[i] = fileByte[position++];
			}
			return ba;
		}
	}

	private class WavHeader {
		private byte[] header;
		private int writePointer=0;
		public byte[] getWavHeader(int num, THfmt fmt, int second) {
			MusicInfo mi=fmt.musicInfos[num];
			int oneSecBytes=mi.bitsPerSample * mi.channels * mi.rate / 8;
			header = new byte[44];
			write("RIFF");	//ckid：4字节 RIFF 标志，大写
			write(second * oneSecBytes + 44 - 8);//cksize：4字节文件长度，这个长度不包括"RIFF"标志(4字节)和文件长度本身所占字节(4字节),即该长度等于整个文件长度 - 8  
			write("WAVE");//fcc type：4字节 "WAVE" 类型块标识, 大写  
			write("fmt ");//ckid：4字节 表示"fmt" chunk的开始,此块中包括文件内部格式信息，小写, 最后一个字符是空格  
			write((int)mi.bitsPerSample);//cksize：4字节，文件内部格式信息数据的大小，过滤字节（一般为00000010H）  
			write(mi.format);//FormatTag：2字节，音频数据的编码方式，1：表示是PCM 编码  
			write(mi.channels);//Channels：2字节，声道数，单声道为1，双声道为2 
			write(mi.rate);//SamplesPerSec：4字节，采样率，如44100  
			write(oneSecBytes);//BytesPerSec：4字节，音频数据传送速率, 单位是字节。其值为采样率×每次采样大小。播放软件利用此值可以估计缓冲区的大小；  
			write(mi.blockAlign);//BlockAlign：2字节，每次采样的大小 = 采样精度*声道数/8(单位是字节); 这也是字节对齐的最小单位, 譬如 16bit 立体声在这里的值是 4 字节。  
			write(mi.bitsPerSample);//BitsPerSample：2字节，每个声道的采样精度; 譬如 16bit 在这里的值就是16。如果有多个声道，则每个声道的采样精度大小都一样的；  
			write("data");//ckid：4字节，数据标志符（data），表示 "data" chunk的开始。此块中包含音频数据，小写；  
			write(second * oneSecBytes);//cksize：音频数据的长度，4字节，audioDataLen = totalDataLen - 36 = fileLenIncludeHeader - 44  
			return header;
		}
		private void write(byte[] bs) {
			for (int i=0;i < bs.length;++i) {
				header[writePointer++] = bs[i];
			}
		}
		private void write(String s) {
			for (int i=0;i < s.length();++i) {
				write(s.charAt(i));
			}
		}
		private void write(int i) {
			byte[] bs=new byte[4];
			bs[0] = (byte) ((i >> 0) & 0xff);
			bs[1] = (byte) ((i >> 8) & 0xff);
			bs[2] = (byte) ((i >> 16) & 0xff);
			bs[3] = (byte) ((i >> 24) & 0xff);
			write(bs);
		}
		private void write(short s) {
			byte[] bs=new byte[2];
			bs[0] = (byte) ((s >> 0) & 0xff);
			bs[1] = (byte) ((s >> 8) & 0xff);
			write(bs);
		}
		private void write(char c) {
			header[writePointer++] = (byte) c;
		}
	}

	private class MusicInfo {
		public byte[] name;
		public int start;
		public int unknown1;
		public int repeatStart;
		public int length;
		public short format;
		public short channels;
		public int rate;
		public int avgBytesPerSec;
		public short blockAlign;
		public short bitsPerSample;
		public short cbSize;
		public short pad;

		@Override
		public String toString() {
			StringBuilder sb=new StringBuilder();
			sb.append(new String(name)).append(" ");
			sb.append(start).append(" ");
			sb.append(unknown1).append(" ");
			sb.append(repeatStart).append(" ");
			sb.append(length).append(" ");
			sb.append(format).append(" ");
			sb.append(channels).append(" ");
			sb.append(rate).append(" ");
			sb.append(avgBytesPerSec).append(" ");
			sb.append(blockAlign).append(" ");
			sb.append(bitsPerSample).append(" ");
			sb.append(cbSize).append(" ");
			sb.append(pad).append(" ");
			return sb.toString();
		}
		//    public int beanSize = 52;
	}
}
