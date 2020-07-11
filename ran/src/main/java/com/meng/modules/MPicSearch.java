package com.meng.modules;

import com.meng.*;
import com.meng.config.*;
import com.meng.SJFInterfaces.*;
import com.meng.tools.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import com.sobte.cqp.jcq.entity.*;

public class MPicSearch extends BaseGroupModule {

    private HashMap<Long, String> userNotSendPicture = new HashMap<>();

	@Override
	public MPicSearch load() {
		return this;
	}

	@Override
	public boolean onGroupMessage(long fromGroup, long fromQQ, String msg, int msgId) {
		if(!ConfigManager.instance.isFunctionEnable(fromGroup,ModuleManager.ID_PicSearch)){
			return false;
		}
		if (msg.equalsIgnoreCase("sp.help")) {
            if (fromGroup != 0) {
                sendMsg(fromGroup, fromQQ, "使用方式已私聊发送");
            }
            sendMsg(0, fromQQ, "使用sp.图片数量.目标网站搜索图片。其中.图片数量和目标网站都可以省略但如果要选择目标网站必须加上图片数量。");
            sendMsg(0, fromQQ, "例--\n在所有网站中搜索一个结果:sp\n在所有网站中搜索3个结果:sp.3\n在Pixiv中搜索1个结果sp.1.5");
            sendMsg(0, fromQQ, "网站代号：\n0 H-Magazines\n2 H-Game CG\n3 DoujinshiDB\n5 pixiv Images\n8 Nico Nico Seiga\n9 Danbooru\n10 drawr Images\n11 Nijie Images\n12 Yande.re\n13 Openings.moe\n15 Shutterstock\n16 FAKKU\n18 H-Misc\n19 2D-Market\n20 MediBang\n21 Anime\n22 H-Anime\n23 Movies\n24 Shows\n25 Gelbooru\n26 Konachan\n27 Sankaku Channel\n28 Anime-Pictures.net\n29 e621.net\n30 Idol Complex\n31 bcy.net Illust\n32 bcy.net Cosplay\n33 PortalGraphics.net (Hist)\n34 deviantArt\n35 Pawoo.net\n36 Manga");
            return true;
        }
		
		CQImage cQImage = Autoreply.instance.CC.getCQImage(msg);
		File imageFile=null;
		
		try {
			imageFile = Autoreply.instance.fileTypeUtil.checkFormat(cQImage.download(Autoreply.appDirectory + "downloadImages/", cQImage.getMd5()));
		} catch (IOException e) {
			e.printStackTrace();
		}

        if (imageFile != null && (msg.toLowerCase().startsWith("sp"))) {
            try {
                ModuleManager.instance.getGroupModule(MUserCounter.class).incSearchPicture(fromQQ);
                ModuleManager.instance.getGroupModule(MGroupCounter.class).incSearchPicture(fromGroup);
                ModuleManager.instance.getGroupModule(MUserCounter.class).incSearchPicture(Autoreply.CQ.getLoginQQ());
                sendMsg(fromGroup, fromQQ, "土豆折寿中……");
                int needPic = 1;
                int database = 999;
                if (msg.startsWith("sp.")) {
                    String[] ss = msg.replaceAll("\\[.*]", "").split("\\.");
                    needPic = Integer.parseInt(ss[1]);
                    database = ss.length >= 3 ? Integer.parseInt(ss[2]) : 999;
                }
                Autoreply.instance.threadPool.execute(new SearchThread(fromGroup, fromQQ, imageFile, needPic, database));
            } catch (Exception e) {
                sendMsg(fromGroup, fromQQ, e.toString());
            }
            return true;
        } else if (imageFile == null && (msg.startsWith("sp.") || msg.equals("sp"))) {
            userNotSendPicture.put(fromQQ, msg);
            sendMsg(fromGroup, fromQQ, "需要一张图片");
            return true;
        } else if (imageFile != null && userNotSendPicture.get(fromQQ) != null) {
            try {
                sendMsg(fromGroup, fromQQ, "土豆折寿中……");
                ModuleManager.instance.getGroupModule(MUserCounter.class).incSearchPicture(fromQQ);
                ModuleManager.instance.getGroupModule(MGroupCounter.class).incSearchPicture(fromGroup);
                ModuleManager.instance.getGroupModule(MUserCounter.class).incSearchPicture(Autoreply.CQ.getLoginQQ());
                int needPic = 1;
                int database = 999;
                if (userNotSendPicture.get(fromQQ).startsWith("sp.")) {
                    String[] ss = userNotSendPicture.get(fromQQ).split("\\.");
                    needPic = Integer.parseInt(ss[1]);
                    database = ss.length >= 3 ? Integer.parseInt(ss[2]) : 999;
                }
                Autoreply.instance.threadPool.execute(new SearchThread(fromGroup, fromQQ, imageFile, needPic, database));
            } catch (Exception e) {
                sendMsg(fromGroup, fromQQ, e.toString());
            }
            userNotSendPicture.remove(fromQQ);
            return true;
        }
        return false;
    }

    public int sendMsg(long fromGroup, long fromQQ, String msg) {
        if (fromGroup == 0) {
            return Autoreply.sendMessage(0, fromQQ, msg);
        } else {
            return Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.at(fromQQ) + msg);
        }
    }

	public class SearchThread implements Runnable {

		private long fromQQ = 0;
		private long fromGroup = -1;
		private File pic = null;
		private int picNumFlag = 0;
		private PicResults mResults;
		private int resultCount = 1;
		private int database = 999;

		public SearchThread(long fromGroup, long fromQQ, File pic, int resultCount, int database) {
			this.fromGroup = fromGroup;
			this.fromQQ = fromQQ;
			this.pic = pic;
			this.resultCount = resultCount;
			this.database = database;
		}

		@Override
		public void run() {
			try {
				check(pic);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		void check(File picF) {
			FileInputStream fInputStream;
			try {
				fInputStream = new FileInputStream(picF);
				Connection.Response response = Jsoup.connect("https://saucenao.com/search.php?db=" + database).timeout(60000).data("file", "image.jpg", fInputStream).method(Connection.Method.POST).execute();
				if (response.statusCode() != 200) {
					Autoreply.instance.threadPool.execute(new DeleteMessageRunnable(Autoreply.sendMessage(fromGroup, fromQQ, "statusCode" + response.statusCode())));
				}
				mResults = new PicResults(Jsoup.parse(response.body()));
			} catch (Exception e) {
				sendMsg(fromGroup, fromQQ, e.toString());
				picF.delete();
				return;
			}
			picF.delete();
			int size = mResults.getResults().size();
			if (size < 1) {
				sendMsg(fromGroup, fromQQ, "没有相似度较高的图片");
			}
			if (fromGroup != -1) {
				resultCount = resultCount > 3 ? 3 : resultCount;
			}
			size = size > resultCount ? resultCount : size;
			for (int i = 0; i < size; i++) {
				StringBuilder sBuilder = new StringBuilder("");
				PicResults.Result tmpr = mResults.getResults().get(i);
				File dFile = null;
				try {
					File files = new File(Autoreply.appDirectory + "picSearch\\tmp\\");
					if (!files.exists()) {
						files.mkdirs();
					}
					URL url = new URL(tmpr.mThumbnail);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(60000);
					InputStream is = connection.getInputStream();
					dFile = new File(Autoreply.appDirectory + "picSearch\\tmp\\", Autoreply.instance.random.nextInt() + picNumFlag++ + "pic.jpg");
					FileOutputStream out = new FileOutputStream(dFile);
					int ii = 0;
					while ((ii = is.read()) != -1) {
						out.write(ii);
					}
					out.close();
					is.close();
				} catch (Exception e) {
					sendMsg(fromGroup, fromQQ, e.toString());
				}
				String[] titleAndMetadata = tmpr.mTitle.split("\n", 2);
				if (titleAndMetadata.length > 0) {
					sBuilder.append("\n").append(titleAndMetadata[0]).append("\n");
					if (titleAndMetadata.length == 2) {
						tmpr.mColumns.add(0, titleAndMetadata[1]);
					}
					for (String string : tmpr.mColumns) {
						sBuilder.append(string).append("\n");
					}
				}
				sBuilder.append(Autoreply.instance.CC.image(dFile)).append("\n");
				if (tmpr.mExtUrls.size() == 2) {
					sBuilder.append("图片&画师:").append(tmpr.mExtUrls.get(1)).append("\n");
					sBuilder.append(tmpr.mExtUrls.get(0)).append("\n");
				} else if (tmpr.mExtUrls.size() == 1) {
					sBuilder.append("链接:").append(tmpr.mExtUrls.get(0)).append("\n");
				}
				if (!tmpr.mSimilarity.isEmpty()) {
					sBuilder.append("相似度:").append(tmpr.mSimilarity);
				}
				String tmp = sBuilder.toString().isEmpty() ? "没有相似度较高的图片" : sBuilder.toString();
				sendMsg(fromGroup, fromQQ, tmp.contains("sankakucomplex") ? tmp + "\n小哥哥注意身体哦" : tmp);
				dFile.delete();
			}
		}

		class PicResults {

			private final String CLASS_RESULT_CONTENT_COLUMN = "resultcontentcolumn";
			private final String CLASS_RESULT_IMAGE = "resultimage";
			private final String CLASS_RESULT_MATCH_INFO = "resultmatchinfo";
			private final String CLASS_RESULT_SIMILARITY_INFO = "resultsimilarityinfo";
			private final String CLASS_RESULT_TABLE = "resulttable";
			private final String CLASS_RESULT_TITLE = "resulttitle";
			private final String URL_LOOKUP_SUBSTRING = "https://saucenao.com/info.php?lookup_type=";

			private ArrayList<Result> mResults = new ArrayList<>();

			public PicResults(Document document) {
				for (Element result : document.getElementsByClass(CLASS_RESULT_TABLE)) {
					Element resultImage = result.getElementsByClass(CLASS_RESULT_IMAGE).first();
					Element resultMatchInfo = result.getElementsByClass(CLASS_RESULT_MATCH_INFO).first();
					Element resultTitle = result.getElementsByClass(CLASS_RESULT_TITLE).first();
					Elements resultContentColumns = result.getElementsByClass(CLASS_RESULT_CONTENT_COLUMN);
					Result newResult = new Result();
					newResult.loadSimilarityInfo(resultMatchInfo);
					newResult.loadThumbnail(resultImage);
					newResult.loadTitle(resultTitle);
					newResult.loadExtUrls(resultMatchInfo, resultContentColumns);
					newResult.loadColumns(resultContentColumns);
					mResults.add(newResult);
				}
			}

			public ArrayList<Result> getResults() {
				return mResults;
			}

			class Result {
				String mSimilarity;
				String mThumbnail;
				String mTitle;
				ArrayList<String> mExtUrls = new ArrayList<>();
				ArrayList<String> mColumns = new ArrayList<>();

				private void loadSimilarityInfo(Element resultMatchInfo) {
					try {
						mSimilarity = resultMatchInfo.getElementsByClass(CLASS_RESULT_SIMILARITY_INFO).first().text();
					} catch (NullPointerException e) {
						System.out.println("Unable to load similarity info");
					}
				}

				private void loadThumbnail(Element resultImage) {
					try {
						Element img = resultImage.getElementsByTag("img").first();

						if (img.hasAttr("data-src")) {
							mThumbnail = img.attr("data-src");
						} else if (img.hasAttr("src")) {
							mThumbnail = img.attr("src");
						}
					} catch (NullPointerException e) {
						System.out.println("Unable to load thumbnail");
					}
				}

				private void loadTitle(Element resultTitle) {
					try {
						mTitle = new HtmlToPlainText().getPlainText(resultTitle);
					} catch (NullPointerException e) {
						System.out.println("Unable to load title");
					}
				}

				private void loadExtUrls(Element resultMatchInfo, Elements resultContentColumns) {
					try {
						for (Element a : resultMatchInfo.getElementsByTag("a")) {
							String href = a.attr("href");

							if (!href.isEmpty() && !href.startsWith(URL_LOOKUP_SUBSTRING)) {
								mExtUrls.add(href);
							}
						}

						for (Element resultContentColumn : resultContentColumns) {
							for (Element a : resultContentColumn.getElementsByTag("a")) {
								String href = a.attr("href");
								if (!href.isEmpty() && !href.startsWith(URL_LOOKUP_SUBSTRING)) {
									mExtUrls.add(href);
								}
							}
						}
					} catch (NullPointerException e) {
						System.out.println("Unable to load external URLs");
					}
					Collections.sort(mExtUrls);
				}

				private void loadColumns(Elements resultContentColumns) {
					try {
						for (Element resultContentColumn : resultContentColumns) {
							mColumns.add(new HtmlToPlainText().getPlainText(resultContentColumn));
						}
					} catch (NullPointerException e) {
						System.out.println("Unable to load columns");
					}
				}
			}
		}
	}

	private class HtmlToPlainText {

		public String getPlainText(Element element) {
			FormattingVisitor formatter = new FormattingVisitor();
			NodeTraversor.traverse(formatter, element);

			return formatter.toString().trim();
		}

		private class FormattingVisitor implements NodeVisitor {
			private static final int mMaxWidth = 80;
			private int mWidth = 0;
			private StringBuilder mAccum = new StringBuilder();

			@Override
			public void head(Node node, int depth) {
				String name = node.nodeName();
				if (node instanceof TextNode) {
					append(((TextNode) node).text());
				} else if (name.equals("li")) {
					append("\n * ");
				} else if (name.equals("dt")) {
					append("  ");
				} else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) {
					append("\n");
				} else if (name.equals("strong")) {
					append(" ");
				}
			}

			// Hit when all of the node's children (if any) have been visited
			@Override
			public void tail(Node node, int depth) {
				String name = node.nodeName();
				if (StringUtil.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")) {
					append("\n");
				}
			}

			// Appends text to the string builder with a simple word wrap method
			private void append(String text) {
				// Reset com.meng.counter if starts with a newline. only from formats above,
				// not in natural text
				if (text.startsWith("\n")) {
					mWidth = 0;
				}

				// Don't accumulate long runs of empty spaces
				if (text.equals(" ")
                    && (mAccum.length() == 0 || StringUtil.in(mAccum.substring(mAccum.length() - 1), " ", "\n"))) {
					return;
				}

				// Won't fit, needs to wrap
				if (text.length() + mWidth > mMaxWidth) {
					String[] words = text.split("\\s+");

					for (int i = 0; i < words.length; i++) {
						String word = words[i];
						boolean last = i == words.length - 1;
						// Insert a space if not the last word
						if (!last) {
							word += " ";
						}
						// Wrap and reset com.meng.counter
						if (word.length() + mWidth > mMaxWidth) {
							mAccum.append("\n").append(word);
							mWidth = word.length();
						} else {
							mAccum.append(word);
							mWidth += word.length();
						}
					}
				} else {
					// Fits as is, without need to wrap text
					mAccum.append(text);
					mWidth += text.length();
				}
			}

			@Override
			public String toString() {
				return mAccum.toString();
			}
		}
	}

}
