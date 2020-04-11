package rep;

import java.lang.reflect.*;

public class KClass {
	static StringBuilder sb=new StringBuilder();
    public boolean ArrowUp = false;
    public boolean ArrowDown = false;
    public boolean ArrowLeft = false;
    public boolean ArrowRight = false;
    public boolean Key_Shift = false;
    public boolean Key_Z = false;
    public boolean Key_X = false;
    public boolean Key_C = false;
    public boolean Key_Ctrl = false;

	@Override
	public String toString() {
		sb.setLength(0);
		if (ArrowUp && ArrowLeft) {
			sb.append("↖");
		} else if (ArrowUp && ArrowRight) {
			sb.append("↗");
		} else if (ArrowDown && ArrowLeft) {
			sb.append("↙");
		} else if (ArrowDown && ArrowRight) {
			sb.append("↘");
		} else if (ArrowUp) {
			sb.append("↑");
		} else if (ArrowDown) {
			sb.append("↓");
		} else if (ArrowLeft) {
			sb.append("←");
		} else if (ArrowRight) {
			sb.append("→");
		} else {
			sb.append("○");
		}
		if (Key_Shift) {
			sb.append("f");
		}
		if (Key_Z) {
			sb.append("s");
		}
		if (Key_X) {
			sb.append("b");
		}
		if (Key_Ctrl) {
			sb.append("c");
		}
		return sb.toString();
	}

    public void Hex2Key(int keyValue) {
		keyValue >>= 7;
		Key_Ctrl = keyValue % 2 == 1;
		keyValue >>= 1;
		Key_C = keyValue % 2 == 1;
		keyValue >>= 1;
		Key_X = keyValue % 2 == 1;
		keyValue >>= 1;
		Key_Z = keyValue % 2 == 1;
		keyValue >>= 1;
		Key_Shift = keyValue % 2 == 1;
		keyValue >>= 1;
		ArrowRight = keyValue % 2 == 1;
		keyValue >>= 1;
		ArrowLeft = keyValue % 2 == 1;
		keyValue >>= 1;
		ArrowDown = keyValue % 2 == 1;
		keyValue >>= 1;
		ArrowUp = keyValue % 2 == 1;
		keyValue >>= 1;
    }
}
