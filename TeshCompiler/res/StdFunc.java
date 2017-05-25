import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;
import java.nio.*;

public class StdFunc {
	public static void print(String s) {
		System.out.println(s);
	}
	public static String read() throws IOException {
		Scanner scn = new Scanner(System.in);
		return scn.next();
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
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				binfile b = new binfile();
				b.directory = file.getAbsolutePath();
				b.name = file.getName();
				files.add(b);
			}
		}
		return files;
	}

	public static long intVal(String str) {
		return Long.parseLong(str);
	}
	public static double floatVal(String str) {
		return Double.parseDouble(str);
	}

	public static textfile openTextfile(String str) {
		textfile t = new textfile();
		File f = new File(str);
		if (f.exists()) {
			t.directory = f.getPath();
			t.name = f.getName();
			t.error = 0;
		} else {
			try {
				f.createNewFile();
			} catch (IOException error) {
				t.error = 1;
			}
			t.directory = f.getPath();
			t.name = f.getName();
			t.error = 0;
		}
		return t;
	}

	public static binfile openBinfile(String str) {
		binfile b = new binfile();
		File f = new File(str);
		if (f.exists()) {
			b.directory = f.getPath();
			b.name = f.getName();
			b.error = 0;
		} else {
			try {
				f.createNewFile();
			} catch (IOException error) {
				b.error = 1;
			}
			b.directory = f.getPath();
			b.name = f.getName();
			b.error = 0;
		}
		return b;
	}

	public static int writeData(binfile b, ArrayList<Long> data) {
		byte[] bytes = new byte[data.size()];
		for (int i = 0; i < data.size(); i++) {
			Long d = data.get(i);
			bytes[i] = d.byteValue();
		}
		File f = new File(b.directory);
		if (f.exists() && f.canWrite()) {
			try {
				Files.write(Paths.get(b.directory), bytes);
			} catch (IOException err) {
				return 0;
			}
			return 1;
		} else {
			return 0;
		}
	}

	public static int writeText(textfile t, String s) {
		File f = new File(t.directory);
		if (f.exists() && f.canWrite()) {
			try {
				Files.write(Paths.get(t.directory), s.getBytes());
			} catch (IOException err) {
				return 0;
			}
			return 1;
		} else {
			return 0;
		}
	}
}
