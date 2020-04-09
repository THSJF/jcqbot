package com.meng.modules;

import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.decoder.*;
import com.meng.*;
import com.meng.tools.*;
import com.sobte.cqp.jcq.message.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;

public class MBarcode extends BaseModule {

	@Override
	public BaseModule load() {
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (imgs == null) {
            return false;
        }
        for (File barcode : imgs) {
            Result result = decodeImage(barcode);
            if (result != null) {
                String barResult = result.getText();
                if (barResult.startsWith("https://qm.qq.com/cgi-bin/qm/qr?k=")) {
                    String html = Tools.Network.getSourceCode(barResult);
                    int flag = html.indexOf("var rawuin = ") + "var rawuin = ".length();
                    String groupNum = html.substring(flag, html.indexOf(";", flag));
                    Autoreply.sendMessage(fromGroup, fromQQ, "群号为:" + groupNum);
                } else {
                	Autoreply.sendMessage(fromGroup, fromQQ, "二维码类型:" + result.getBarcodeFormat().toString() + "\n内容:" + result.getText());
                }
                return true;
            }
        }
        return false;
    }

	public BufferedImage createBarcode(String text, BarcodeFormat format, int size) {
		try {
			Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			if (format == BarcodeFormat.AZTEC) {
				hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.aztec.encoder.Encoder.DEFAULT_AZTEC_LAYERS);// 默认，可以不设
			} else if (format == BarcodeFormat.PDF_417) {
				hints.put(EncodeHintType.ERROR_CORRECTION, 2);
			} else {
				hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			}
			BitMatrix bitMatrix = new MultiFormatWriter().encode(CQCode.decode(text), format, size, size, hints);
			int h = bitMatrix.getHeight();
			int w = bitMatrix.getWidth();
			BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					im.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
				}
			}
			return im;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Result decodeImage(File img) {
		BufferedImage image;
		Result result = null;
		try {
			image = ImageIO.read(img);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap bitmap = new BinaryBitmap(binarizer);
			Map<DecodeHintType, Object> map = new HashMap<DecodeHintType, Object>();
			map.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			result = new MultiFormatReader().decode(bitmap, map);
		} catch (Exception e1) {
		}
		return result;
	}

	public class BufferedImageLuminanceSource extends LuminanceSource {

		private static final double MINUS_45_IN_RADIANS = -0.7853981633974483;
		private final BufferedImage image;
		private final int left;
		private final int top;

		public BufferedImageLuminanceSource(BufferedImage image) {
			this(image, 0, 0, image.getWidth(), image.getHeight());
		}

		public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height) {
			super(width, height);
			if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
				this.image = image;
			} else {
				int sourceWidth = image.getWidth();
				int sourceHeight = image.getHeight();
				if (left + width > sourceWidth || top + height > sourceHeight) {
					throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
				}
				this.image = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_BYTE_GRAY);
				WritableRaster raster = this.image.getRaster();
				int[] buffer = new int[width];
				for (int y = top; y < top + height; y++) {
					image.getRGB(left, y, width, 1, buffer, 0, sourceWidth);
					for (int x = 0; x < width; x++) {
						int pixel = buffer[x];
						if ((pixel & 0xFF000000) == 0) {
							pixel = 0xFFFFFFFF;
						}
						buffer[x] = (306 * ((pixel >> 16) & 0xFF) + 601 * ((pixel >> 8) & 0xFF) + 117 * (pixel & 0xFF) + 0x200) >> 10;
					}
					raster.setPixels(left, y, width, 1, buffer);
				}

			}
			this.left = left;
			this.top = top;
		}

		@Override
		public byte[] getRow(int y, byte[] row) {
			if (y < 0 || y >= getHeight()) {
				throw new IllegalArgumentException("Requested row is outside the image: " + y);
			}
			int width = getWidth();
			if (row == null || row.length < width) {
				row = new byte[width];
			}
			image.getRaster().getDataElements(left, top + y, width, 1, row);
			return row;
		}

		@Override
		public byte[] getMatrix() {
			int width = getWidth();
			int height = getHeight();
			int area = width * height;
			byte[] matrix = new byte[area];
			image.getRaster().getDataElements(left, top, width, height, matrix);
			return matrix;
		}

		@Override
		public boolean isCropSupported() {
			return true;
		}

		@Override
		public LuminanceSource crop(int left, int top, int width, int height) {
			return new BufferedImageLuminanceSource(image, this.left + left, this.top + top, width, height);
		}

		@Override
		public boolean isRotateSupported() {
			return true;
		}

		@Override
		public LuminanceSource rotateCounterClockwise() {
			int sourceWidth = image.getWidth();
			int sourceHeight = image.getHeight();
			AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, sourceWidth);
			BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g = rotatedImage.createGraphics();
			g.drawImage(image, transform, null);
			g.dispose();
			int width = getWidth();
			return new BufferedImageLuminanceSource(rotatedImage, top, sourceWidth - (left + width), getHeight(), width);
		}

		@Override
		public LuminanceSource rotateCounterClockwise45() {
			int width = getWidth();
			int height = getHeight();
			int oldCenterX = left + width / 2;
			int oldCenterY = top + height / 2;
			AffineTransform transform = AffineTransform.getRotateInstance(MINUS_45_IN_RADIANS, oldCenterX, oldCenterY);
			int sourceDimension = Math.max(image.getWidth(), image.getHeight());
			BufferedImage rotatedImage = new BufferedImage(sourceDimension, sourceDimension, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g = rotatedImage.createGraphics();
			g.drawImage(image, transform, null);
			g.dispose();
			int halfDimension = Math.max(width, height) / 2;
			int newLeft = Math.max(0, oldCenterX - halfDimension);
			int newTop = Math.max(0, oldCenterY - halfDimension);
			int newRight = Math.min(sourceDimension - 1, oldCenterX + halfDimension);
			int newBottom = Math.min(sourceDimension - 1, oldCenterY + halfDimension);
			return new BufferedImageLuminanceSource(rotatedImage, newLeft, newTop, newRight - newLeft, newBottom - newTop);
		}

	}

}
