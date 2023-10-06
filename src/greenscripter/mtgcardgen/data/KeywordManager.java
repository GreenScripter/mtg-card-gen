package greenscripter.mtgcardgen.data;

import java.util.Arrays;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

public class KeywordManager {

	public static List<String> getKeywords() throws Exception {
		if (!new File("Keywords.json").exists()) {
			ListResponse r1 = new Gson().fromJson(new String(sendGET("https://api.scryfall.com/catalog/keyword-abilities")), ListResponse.class);
			ListResponse r2 = new Gson().fromJson(new String(sendGET("https://api.scryfall.com/catalog/keyword-actions")), ListResponse.class);
			ListResponse r3 = new Gson().fromJson(new String(sendGET("https://api.scryfall.com/catalog/ability-words")), ListResponse.class);
			r1.data.addAll(r2.data);
			r1.data.addAll(r3.data);
			Utils.bytesToFile(new Gson().toJson(r1.data).getBytes(), new File("Keywords.json"));
		}
		return Arrays.asList(new Gson().fromJson(new String(Utils.fileToBytes(new File("Keywords.json"))), String[].class));
	}

	static class ListResponse {

		List<String> data;
	}

	private static byte[] sendGET(String url) throws IOException {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			return con.getInputStream().readAllBytes();
		} else {
			return null;
		}

	}

}
