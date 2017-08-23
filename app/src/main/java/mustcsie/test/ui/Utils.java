package mustcsie.test.ui;

public class Utils {
	private static long lastClickTime;
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		if ( time - lastClickTime < 1000) {
			return false;
		}
		lastClickTime = time;
		return true;
	}
}
