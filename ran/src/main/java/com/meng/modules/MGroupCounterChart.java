package com.meng.modules;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.config.*;
import com.meng.tools.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.time.*;   

public class MGroupCounterChart extends BaseModule {
	public HashMap<Long,GroupSpeak> groupsMap = new HashMap<>(32);
	private File historyFile;

	@Override
	public BaseModule load() {
		historyFile = new File(Autoreply.appDirectory + "properties\\GroupCount2.json");
        if (!historyFile.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(historyFile);
                OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                writer.write(new Gson().toJson(groupsMap));
                writer.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        groupsMap = Autoreply.gson.fromJson(Tools.FileTool.readString(historyFile), new TypeToken<HashMap<Long, GroupSpeak>>() {}.getType());
		Autoreply.instance.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					saveData();
				}
			});
		enable = true;
		return this;
	}
	public class GroupSpeak {
		public int all=0;
		public HashMap<String,HashMap<Integer,Integer>> hour=new HashMap<>(16);		
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (!ConfigManager.instance.isFunctionEnable(fromGroup, ModuleManager.ID_GroupCountChart)) {
			return false;
		}
		GroupSpeak gs=groupsMap.get(fromGroup);
		if (gs == null) {
			gs = new GroupSpeak();
			groupsMap.put(fromGroup, gs);
		}
		++gs.all;
		if (msg.equals("-发言统计")) {
			HashMap<Integer,Integer> everyHourHashMap = gs.hour.get(Tools.CQ.getDate());
			if (everyHourHashMap == null) {
				everyHourHashMap = new HashMap<>();
				gs.hour.put(Tools.CQ.getDate(), everyHourHashMap);
			}
			Date da=new Date();
			int nowHour=da.getHours();
			if (everyHourHashMap.get(nowHour) == null) {
				everyHourHashMap.put(nowHour, 1);
			} else {
				int stored=everyHourHashMap.get(nowHour);
				++stored;
				everyHourHashMap.put(nowHour, stored);
			}

			if (everyHourHashMap == null || everyHourHashMap.size() == 0) {
				Autoreply.sendMessage(fromGroup, 0, "无数据");
				return true;
			}
			StringBuilder sb=new StringBuilder(String.format("群内共有%d条消息,今日消息情况:\n", groupsMap.get(fromGroup).all));
			for (int i=0;i < 24;++i) {
				if (everyHourHashMap.get(i) == null) {
					continue;
				}
				sb.append(String.format("%d:00-%d:00  共%d条消息\n", i, i + 1, everyHourHashMap.get(i)));
			}
			Autoreply.sendMessage(fromGroup, 0, sb.toString());
			TimeSeries dtimeseries = new TimeSeries("你群发言");
			Calendar dc = Calendar.getInstance();
			dc.add(Calendar.HOUR_OF_DAY, -24);
			HashMap<Integer,Integer> deveryHour=gs.hour.get(Tools.CQ.getDate(dc.getTimeInMillis()));
			for (int i=dc.get(Calendar.HOUR_OF_DAY);i < 24;++i) {
				dtimeseries.add(new Hour(i, dc.get(Calendar.DATE), dc.get(Calendar.MONTH) + 1, dc.get(Calendar.YEAR)), deveryHour.get(i) == null ?0: deveryHour.get(i));
			}
			dc = Calendar.getInstance();
			deveryHour = gs.hour.get(Tools.CQ.getDate(dc.getTimeInMillis()));
			for (int i=0;i <= dc.get(Calendar.HOUR_OF_DAY);++i) {
				dtimeseries.add(new Hour(i, dc.get(Calendar.DATE), dc.get(Calendar.MONTH) + 1, dc.get(Calendar.YEAR)), deveryHour.get(i) == null ?0: deveryHour.get(i));
			}
			TimeSeriesCollection dtimeseriescollection = new TimeSeriesCollection();  
			dtimeseriescollection.addSeries(dtimeseries);
			JFreeChart djfreechart = ChartFactory.createTimeSeriesChart("你群24小时发言", "时间", "", dtimeseriescollection, true, true, true);  
			XYPlot dxyplot = (XYPlot) djfreechart.getPlot();  
			DateAxis ddateaxis = (DateAxis) dxyplot.getDomainAxis();  
			ddateaxis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));  
			ChartPanel dframe1 = new ChartPanel(djfreechart, true);  
			ddateaxis.setLabelFont(new Font("黑体", Font.BOLD, 14));  		
			ddateaxis.setTickLabelFont(new Font("宋体", Font.BOLD, 12));  
			djfreechart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));  
			djfreechart.getTitle().setFont(new Font("宋体", Font.BOLD, 20));  
			File pic=null;
			try {
				pic = new File(Autoreply.appDirectory + "downloadImages/" + System.currentTimeMillis() + ".jpg");
				ChartUtils.saveChartAsJPEG(pic, 1.0f, dframe1.getChart(), 800, 480);
			} catch (IOException e) {}
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.image(pic));
			TimeSeries timeseries = new TimeSeries("你群发言");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -30);
			for (int i=0;i <= 30;++i) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				HashMap<Integer,Integer> everyHour=gs.hour.get(Tools.CQ.getDate(cal.getTimeInMillis()));
				int oneDay=0;
				if (everyHour == null) {
					oneDay = 0;
				} else {
					for (int oneHour:everyHour.values()) {
						oneDay += oneHour;
					}
				}
				timeseries.add(new Day(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)), oneDay);
			}
			TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();  
			timeseriescollection.addSeries(timeseries);
			JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("你群30天发言", "时间", "", timeseriescollection, true, true, true);  
			XYPlot xyplot = (XYPlot) jfreechart.getPlot();  
			DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();  
			dateaxis.setDateFormatOverride(new SimpleDateFormat("MM-dd"));  
			ChartPanel frame1 = new ChartPanel(jfreechart, true);  
			dateaxis.setLabelFont(new Font("黑体", Font.BOLD, 14)); 
			dateaxis.setTickLabelFont(new Font("宋体", Font.BOLD, 12));
			jfreechart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));  
			jfreechart.getTitle().setFont(new Font("宋体", Font.BOLD, 20));
			File pic2=null;
			try {
				pic2 = new File(Autoreply.appDirectory + "downloadImages/" + System.currentTimeMillis() + ".jpg");
				ChartUtils.saveChartAsJPEG(pic, 1.0f, frame1.getChart(), 800, 480);
			} catch (IOException e) {}
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.image(pic2));
			return true;
		}	
		return false;
	}

	public HashMap<Integer,Integer> getSpeak(long group, String date) {
		GroupSpeak gs = groupsMap.get(group);
		if (gs == null) {
			return null;
		}
		HashMap<Integer,Integer> hr = gs.hour.get(date);
		if (hr == null) {
			return null;
		}
		return hr;
	}

	private void saveData() {
        while (true) {
            try {
                Thread.sleep(60000);
                FileOutputStream fos = new FileOutputStream(historyFile);
                OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                writer.write(new Gson().toJson(groupsMap));
                writer.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

