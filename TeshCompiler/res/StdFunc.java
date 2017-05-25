import java.util.*;
import java.io.*;

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
	public static int empty(ArrayDeque channel) {
		if (channel.isEmpty()) {
			return 1;
		} else {
			return 0;
		}
	}

	public static ArrayList getFilesFromDir(String s) {
		File folder = new File(s);
		File[] listOfFiles = folder.listFiles();
		ArrayList files = new ArrayList();

		for (File file : listOfFiles) {
			binfile b = new binfile();
			b.directory = file.getAbsolutePath();
			b.name = file.getName();
			files.add(b);
		}

		return files;
	}

}
