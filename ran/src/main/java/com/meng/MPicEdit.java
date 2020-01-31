package com.meng;

import com.google.zxing.*;
import com.meng.*;
import com.sobte.cqp.jcq.entity.*;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import com.meng.modules.*;
import com.meng.config.*;


public class MPicEdit extends BaseModule {

	public JingShenZhiZhuManager jszzm=new JingShenZhiZhuManager();
	public ShenChuManager scm=new ShenChuManager();

	@Override
	public BaseModule load() {
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (msg.startsWith("精神支柱[CQ:at")) {
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.image(jingShenZhiZhuByAt(fromGroup, fromQQ, msg)));
			return true;
		} else if (msg.startsWith("神触[CQ:at")) {
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.image(shenChuByAt(fromGroup, fromQQ, msg)));
			return true;
		} else if (ConfigManager.instance.isMaster(fromQQ)) {
			if (msg.startsWith("精神支柱[CQ:image")) {
				Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.image(jingShenZhiZhuByPic(fromGroup, fromQQ, msg)));
                return true;
			} else if (msg.startsWith("神触[CQ:image")) {
				Autoreply.sendMessage(fromGroup, 0, Autoreply.instance.CC.image(shenChuByPic(fromGroup, fromQQ, msg)));
				return true;
			}
		}
		return false;
	}

	public File jingShenZhiZhuByPic(final long fromGroup, long fromQQ, final String msg) {
		CQImage cm = Autoreply.instance.CC.getCQImage(msg);
		if (cm == null) {
			return null;
		}
		try {
			return jszzm.create(cm.download(Autoreply.appDirectory + "jingshenzhizhu\\" + System.currentTimeMillis() + ".jpg"));
		} catch (IOException e) {}	
		return null;
	}

	public File jingShenZhiZhuByAt(final long fromGroup, final long fromQQ, final String msg) {
		long id = Autoreply.instance.CC.getAt(msg);
		if (id == -1000 || id == -1) {
			id = fromQQ;
		}
		return jszzm.create(downloadHead(new File(Autoreply.appDirectory + "user\\" + id + ".jpg"), id), id);
	}

	public File shenChuByAt(final long fromGroup, final long fromQQ, final String msg) {
		long id = Autoreply.instance.CC.getAt(msg);
		if (id == -1000 || id == -1) {
			id = fromQQ;
		}
		return scm.create(downloadHead(new File(Autoreply.appDirectory + "user\\" + id + ".jpg"), id), id);
	}

	public File shenChuByPic(final long fromGroup, long fromQQ, final String msg) {
		CQImage cm = Autoreply.instance.CC.getCQImage(msg);
		if (cm == null) {
			return null;
		}
		try {
			return jszzm.create(cm.download(Autoreply.appDirectory + "jingshenzhizhu\\" + System.currentTimeMillis() + ".jpg"));
		} catch (IOException e) {}	
		return null;
	}

	private File downloadHead(File image, long id) {
        URL url;
        try {
            url = new URL("http://q2.qlogo.cn/headimg_dl?bs=" + id + "&dst_uin=" + id + "&dst_uin=" + id + "&;dst_uin=" + id + "&spec=5&url_enc=0&referer=bu_interface&term_type=PC");
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(image);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

	public class JingShenZhiZhuManager {

		public JingShenZhiZhuManager() {
			File files = new File(Autoreply.appDirectory + "jingshenzhizhu\\");
			if (!files.exists()) {
				files.mkdirs();
			}
		}

		public File create(File headFile) {
			return create(headFile, System.currentTimeMillis());
		}

		public File create(File headFile, long id) {
			try {
				File retFile = new File(Autoreply.appDirectory + "jingshenzhizhu\\" + id + ".jpg");
				BufferedImage src;
				src = ImageIO.read(headFile);
				BufferedImage des1 = chgPic(rotatePic(src, 346), 190);
				Image im = ImageIO.read(new File(Autoreply.appDirectory + "pic\\6.png"));
				BufferedImage b = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				b.getGraphics().drawImage(im, 0, 0, null);
				b.getGraphics().drawImage(des1, -29, 30, null);
				ImageIO.write(b, "png", retFile);
				return retFile;
			} catch (IOException e) {
				return null;
			}
		}

		private BufferedImage rotatePic(Image src, int angel) {
			int srcWidth = src.getWidth(null);
			int srcHeight = src.getHeight(null);
			if (angel >= 90) {
				if (angel / 90 % 2 == 1) {
					srcHeight = srcHeight ^ srcWidth;
					srcWidth = srcHeight ^ srcWidth;
					srcHeight = srcHeight ^ srcWidth;
				}
			}
			double r = Math.sqrt(srcHeight * srcHeight + srcWidth * srcWidth) / 2;
			double len = 2 * Math.sin(Math.toRadians(angel % 90) / 2) * r;
			double angelAlpha = (Math.PI - Math.toRadians(angel % 90)) / 2;
			double angelDaltaWidth = Math.atan((double) srcHeight / srcWidth);
			double angelDaltaHeight = Math.atan((double) srcWidth / srcHeight);
			int lenDaltaWidth = (int) (len * Math.cos(Math.PI - angelAlpha - angelDaltaWidth));
			int lenDaltaHeight = (int) (len * Math.cos(Math.PI - angelAlpha - angelDaltaHeight));
			int desWidth = srcWidth + lenDaltaWidth * 2;
			int desHeight = srcHeight + lenDaltaHeight * 2;
			Rectangle rectDes = new Rectangle(new Dimension(desWidth, desHeight));
			BufferedImage res = new BufferedImage(rectDes.width, rectDes.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = res.createGraphics();
			g2.translate((rectDes.width - srcWidth) / 2, (rectDes.height - srcHeight) / 2);
			g2.rotate(Math.toRadians(angel), srcWidth / 2, srcHeight / 2);
			g2.drawImage(src, null, null);
			return res;
		}

		private BufferedImage chgPic(BufferedImage img, int newSize) {
			BufferedImage img2 = new BufferedImage(newSize, newSize, BufferedImage.TYPE_INT_ARGB);
			img2.getGraphics().drawImage(img, 0, 0, newSize, newSize, null);
			return img2;
		}
	}

	public class ShenChuManager {

		public ShenChuManager() { 
			File files = new File(Autoreply.appDirectory + "shenchu\\");
			if (!files.exists()) {
				files.mkdirs();
			}
		}

		public File create(File headFile) {
			return create(headFile, System.currentTimeMillis());
		}

		public File create(File headFile, long id) {
			try {
				File retFile = new File(Autoreply.appDirectory + "shenchu\\" + id + ".jpg");
				BufferedImage src;
				src = ImageIO.read(headFile);
				BufferedImage des1 = new BufferedImage(228, 228, BufferedImage.TYPE_INT_ARGB);
				des1.getGraphics().drawImage(src, 0, 0, 228, 228, null);
				Image im = ImageIO.read(new File(Autoreply.appDirectory + "pic\\shenchuback.png"));
				BufferedImage b = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				b.getGraphics().drawImage(im, 0, 0, null);
				b.getGraphics().drawImage(des1, 216, -20, null);
				ImageIO.write(b, "png", retFile); 
				return retFile;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
