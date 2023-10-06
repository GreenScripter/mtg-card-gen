package greenscripter.mtgcardgen.data;

import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public final class Utils {

	public static byte[] fileToBytes(File f) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = new FileInputStream(f);
		int read = 0;
		byte[] buffer = new byte[8192];
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		in.close();
		return out.toByteArray();
	}

	public static void bytesToFile(byte[] b, File f) throws IOException {
		FileOutputStream out = new FileOutputStream(f);
		out.write(b);
		out.close();
	}

	public static void save(String toSave, File where) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(where));
			out.write(toSave);
			out.flush();
			out.close();
		} catch (Exception e) {

		}
	}

	public static void save(List<String> toSave, File where) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(where));
			for (String str : toSave) {
				out.write(str);
				out.newLine();
			}
			out.flush();
			out.close();
		} catch (Exception e) {

		}
	}

	public static ArrayList<String> loadStringList(File where) {
		ArrayList<String> out = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(where));
			String thisline = in.readLine();
			while (thisline != null) {
				out.add(thisline);
				thisline = in.readLine();
			}
			in.close();
		} catch (Exception e) {

		}
		return out;
	}

	public static String loadStrings(File where) {
		ArrayList<String> out = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(where));
			String thisline = in.readLine();
			while (thisline != null) {
				out.add(thisline);
				thisline = in.readLine();
			}
			in.close();
		} catch (Exception e) {

		}
		StringBuilder str = new StringBuilder();
		for (String at : out) {
			str.append(at);
			str.append(" ");
		}
		return str.toString();
	}

}
