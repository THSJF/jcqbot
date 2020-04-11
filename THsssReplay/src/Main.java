import java.io.*;
import java.nio.charset.*;
import java.util.*;
import rep.*;

public class Main {

	/*夏夜祭replay结构:
	 (数字存储都是小端模式)

	 关卡key数据在前,机体信息时间信息等在后
	 每一个关卡的key数据首尾相连,每一面的开头都是ushort 57358(0xE00E)
	 key数据和信息之间用ushort 4080分开(0x0FF0)
	 一帧的key数据为2字节


	 57358 本面key数据
	 57358 本面key数据
	 .....
	 57358 本面key数据
	 4080
	 机体信息,时间信息

	 */
	public static void main(String[] args) {
		Replay replay = new Replay("/storage/emulated/0/tencent/QQfile_recv/thSSS_01.rpy");
		System.out.println(replay.info);
		StringBuilder sb=new StringBuilder();
		sb.append(replay.info.toString());
		KClass kclass=new KClass();
		for (int i=0;i < replay.info.MyPlaneData.size();++i) {
			sb.append("stage ").append((i + 1)).append("\n");
			replay.ReplayData.position = (int) replay.info.MyPlaneData.get(i).DataPosition;
			int keyValue=replay.ReadKey();
			if (keyValue == 57358) {
				int flag=0;
				keyValue = replay.ReadKey();
				while (keyValue != 4080 && keyValue != 57358) {
					kclass.Hex2Key(keyValue);
					sb.append(kclass.toString()).append("|");
					if (++flag % 60 == 0) {
						sb.append("\n");
					}
					keyValue = replay.ReadKey();
				} 
			}
			sb.append("\n\n\n");
		}
		saveText("/storage/emulated/0/1.txt", sb.toString());
		System.out.println("ok");
	}

	public static void saveText(String path, String str) {
		try {
			File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(str);
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
