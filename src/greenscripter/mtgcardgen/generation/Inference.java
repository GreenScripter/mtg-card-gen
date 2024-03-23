package greenscripter.mtgcardgen.generation;

import java.util.Arrays;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Inference {

	public static void main(String[] args) throws IOException {
		python = args[0];
		mainFolder = args[1];
		Inference inf = new Inference("rulestextgen", new String[0]);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String line = in.readLine();
			String result = inf.request(line);
			System.out.println(result);
		}

	}

	public static String python;
	public static String mainFolder;

	Object sync = python;

	String model;
	String[] extraArgs;

	Process process;

	BufferedReader reader;
	BufferedWriter writer;

	public Inference(String model, String[] extraArgs) throws IOException {
		this.model = model;
		this.extraArgs = extraArgs;

		String[] args = new String[3 + extraArgs.length];
		args[0] = python;
		args[1] = "-u";
		args[2] = "inference.py";
		System.arraycopy(extraArgs, 0, args, 3, extraArgs.length);

		process = Runtime.getRuntime().exec(args, null, new File(mainFolder, model));
		new Thread(() -> {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while (true) {
					String line = in.readLine();
					if (line == null) return;
					System.out.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		reader = process.inputReader();
		writer = process.outputWriter();
	}

	public synchronized String request(String input) throws IOException {
		synchronized (sync) {
			System.out.println("Request to " + model + " " + Arrays.toString(extraArgs) + ": [" + input + "]");
			writer.append(input);
			writer.newLine();
			writer.flush();
			return reader.readLine();
		}
	}

	public Object getSync() {
		return sync;
	}

	public void setSync(Object sync) {
		this.sync = sync;
	}

}
