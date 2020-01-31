package com.meng.modules;
import com.meng.*;
import java.io.*;

public class MNumberProcess extends BaseModule {

	@Override
	public BaseModule load() {
		enable = true;
		return this;
	}

	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId, File[] imgs) {
		if (msg.startsWith("-int ")) {
			try {
				String[] args=msg.split(" ", 4);
				int a1=Integer.parseInt(args[1]);
				int a2=Integer.parseInt(args[3]);
				String resu="failed";
				switch (args[2]) {
					case "+":
						resu = "result:" + (a1 + a2);
						break;
					case "-":
						resu = "result:" + (a1 - a2);
						break;
					case "*":
						resu = "result:" + (a1 * a2);
						break;
					case "/":
						resu = "result:" + (a1 / a2);
						break;
					case ">>":
						resu = "result:" + (a1 >> a2);
						break;
					case ">>>":
						resu = "result:" + (a1 >>> a2);
						break;
					case "<<":
						resu = "result:" + (a1 << a2);
						break;
					case "^":
						resu = "result:" + (a1 ^ a2);
						break;
					case "%":
						resu = "result:" + (a1 % a2);
						break;
					case "|":
						resu = "result:" + (a1 | a2);
						break;
					case "&amp;"://&
						resu = "result:" + (a1 & a2);
						break;
					case "~":
						resu = "result:" + (~a1);
						break;
				}
				Autoreply.sendMessage(fromGroup, 0, resu);
			} catch (Exception e) {
				Autoreply.sendMessage(fromGroup, 0, e.toString());
			}
			return true;
		}
		if (msg.startsWith("-uint ")) {
			String[] args=msg.split("\\s", 2);
			try {
				Autoreply.sendMessage(fromGroup, 0, (Integer.parseInt(args[1]) & 0x00000000ffffffffL) + "");
			} catch (Exception e) {
				Autoreply.sendMessage(fromGroup, 0, e.toString());
			}
			return true;
		}
		return false;
	}
}
