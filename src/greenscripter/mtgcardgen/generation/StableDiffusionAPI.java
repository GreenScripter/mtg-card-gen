package greenscripter.mtgcardgen.generation;

import java.util.Base64;
import java.util.List;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

public class StableDiffusionAPI {

	public static String target;
	public String localTarget;

	public static Gson gson = new Gson();

	public StableDiffusionAPI(String target) {
		localTarget = target;
	}

	public synchronized BufferedImage requestImage(String prompt) throws IOException {
		return txt2img(localTarget, prompt);
	}

	public static synchronized BufferedImage txt2img(String prompt) throws IOException {
		return txt2img(target, prompt);
	}

	public static BufferedImage txt2img(String target, String prompt) throws IOException {
		String url = target + "/sdapi/v1/txt2img";
		Txt2Img data = new Txt2Img();
		data.cfg_scale = 7;
		data.steps = 20;
		data.restore_faces = true;
		data.negative_prompt = "border, frame, watermark, text, nudity, naked, nsfw";
		data.prompt = prompt;

		byte[] response = sendPOST(url, gson.toJson(data).getBytes());
		Txt2ImgResp results = gson.fromJson(new String(response), Txt2ImgResp.class);

		return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(results.images.get(0))));
	}

	private static byte[] sendPOST(String url, byte[] data) throws IOException {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");

		con.connect();
		con.getOutputStream().write(data);

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			return con.getInputStream().readAllBytes();
		} else {
			return null;
		}

	}

	public static class Txt2Img {

		int width = 512;
		int height = 512;
		int cfg_scale = 7;
		int steps = 15;
		boolean restore_faces = false;
		String prompt;
		String negative_prompt;
		String sampler_index = "Euler a";
	}

	public static class Txt2ImgResp {

		List<String> images;
	}
}
