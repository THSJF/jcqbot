package rep;

import java.io.*;
import java.nio.charset.*;

public class Tools {

	public static String readString(String fileName) {
		return readString(new File(fileName));
	}

	public static String readString(File f) {
		String s = "";
		try {      
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

	public static byte[] readByteArray(File f) {
		byte[] filecontent=null;
		try {
			long filelength = f.length();
			filecontent = new byte[(int) filelength];
			FileInputStream in = new FileInputStream(f);
			in.read(filecontent);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filecontent;
	}
}
