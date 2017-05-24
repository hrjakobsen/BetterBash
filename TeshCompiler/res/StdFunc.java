import java.util.ArrayDeque;

public class StdFunc {
	public static void print(String s) {
		System.out.println(s);
	}

	public static String intToStr(long i) {
		return Long.toString(i);
	}
	public static String floatToStr(double d) {return Double.toString(d); }
	public static String charToStr(String c) { return c; }
	public static String boolToStr(int b) { if (b != 0) return "true"; else return "false"; }
	public static int channelIsEmpty(ArrayDeque channel) {
		if (channel.isEmpty()) {
			return 1;
		} else {
			return 0;
		}
	}

}
