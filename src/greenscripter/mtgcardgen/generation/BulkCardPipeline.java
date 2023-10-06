package greenscripter.mtgcardgen.generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import greenscripter.mtgcardgen.models.NamerModel;
import greenscripter.mtgcardgen.models.RulesTextModel;

public class BulkCardPipeline {

	List<StableDiffusionAPI> diffusions = new ArrayList<>();

	ArrayBlockingQueue<MLCard> cardQueue = new ArrayBlockingQueue<>(100);
	ArrayBlockingQueue<MLCard> nameQueue = new ArrayBlockingQueue<>(100);

	List<MLCard> results = new ArrayList<>();

	ExecutorService threads;
	ExecutorService saveThreads;

	RulesTextModel rules = new RulesTextModel();
	NamerModel namer = new NamerModel();

	File artFolder = new File("bulkMLArt");
	File cardFolder = new File("bulkMLRenders");
	File cardFile = new File("bulkMLCards.json");

	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	Gson gsonC = new Gson();

	public static void main(String[] args) throws IOException {
		Inference.python = args[0];
		Inference.mainFolder = args[1];
		String name = args[2];

		System.setProperty("java.awt.headless", "true");

		String[] urls = new String[args.length - 3];
		System.arraycopy(args, 3, urls, 0, args.length - 3);
		BulkCardPipeline pipeline = new BulkCardPipeline(List.of(urls));

		pipeline.generate(10000, name, "OVR");
		System.exit(0);
	}

	public BulkCardPipeline(Collection<String> apiUrls) throws IOException {
		apiUrls.stream().map(StableDiffusionAPI::new).forEach(diffusions::add);
		threads = Executors.newCachedThreadPool();
		saveThreads = Executors.newCachedThreadPool();
	}

	public void generate(int count, String name, String set) throws IOException {
		artFolder.mkdir();
		cardFolder.mkdir();
		threads.execute(() -> {
			MLCard card = null;
			try {
				while (true) {
					card = nameQueue.take();
					namer.fillInName(card);
					cardQueue.put(card);

				}
			} catch (InterruptedException e) {
			} catch (IOException e) {
				System.err.println(gsonC.toJson(card));
				e.printStackTrace();
			}
			System.out.println("Shut down Namer");

		});
		FileOutputStream out = new FileOutputStream("bulkTempCards.json");

		for (StableDiffusionAPI api : diffusions) {
			threads.execute(() -> {
				MLCard cardE = null;
				try {
					while (true) {
						MLCard card = cardQueue.take();
						cardE = card;
						card.prompt = getArtPrompt(card);
						BufferedImage image = api.requestImage(card.prompt);
						saveThreads.execute(() -> {
							try {
								String artName = UUID.randomUUID().toString();
								card.image = artName + ".png";
								System.out.println("Generated art " + DataUtils.mergeName(card.name) + " " + artName);
								ImageIO.write(image, "png", new File(artFolder, artName + ".png"));
								BufferedImage cardImage = DataUtils.render(card, name, image, set + " • EN", "S " + String.format("%0" + 4 + "d", card.number));
								ImageIO.write(cardImage, "png", new File(cardFolder, artName + ".png"));
								out.write((new String(gsonC.toJson(card)) + "\n").getBytes());

							} catch (Exception e) {
								System.err.println(gsonC.toJson(card));
								e.printStackTrace();
							}
						});
					}
				} catch (InterruptedException e) {
				} catch (IOException e) {
					System.err.println(gsonC.toJson(cardE));
					e.printStackTrace();
				}
				System.out.println("Shut down " + api.localTarget);
			});
		}
		int number = 0;

		while (results.size() < count) {
			List<MLCard> cards = rules.generateUnnamedCards();
			while (cards.size() + results.size() > count) {
				cards.remove(cards.size() - 1);
			}
			for (int i = 0; i < cards.size(); i++) {
				cards.get(i).number = ++number;
			}
			for (MLCard c : cards) {
				try {
					nameQueue.put(c);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				results.add(c);
			}
		}
		while (!cardQueue.isEmpty() || !nameQueue.isEmpty()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		threads.shutdownNow();
		try {
			threads.awaitTermination(1000, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		saveThreads.shutdown();
		try {
			saveThreads.awaitTermination(1000, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		out.close();

		Files.write(cardFile.toPath(), gson.toJson(results.stream().map(PrettyCard::new).collect(Collectors.toList())).getBytes(StandardCharsets.UTF_8));
	}

	public String getArtPrompt(MLCard card) throws IOException {
		if (card.type.contains("Creature")) {
			return "a painting of a " + card.type.substring(card.type.indexOf("—") + 1) + " , " + DataUtils.mergeName(card.name) + ", mtg art , magic the gathering concept art , mtg art style , d&d concept art , nixeu and greg rutkowski , magic : the gathering art , magic the gathering artwork , jeff easley dramatic light , dale keown and greg rutkowski";
		} else if (card.type.contains("Land")) {
			return "a painting of a " + card.type + " , " + DataUtils.mergeName(card.name) + ", mtg art , magic the gathering concept art , mtg art style , d&d concept art , nixeu and greg rutkowski , magic : the gathering art , magic the gathering artwork , jeff easley dramatic light , dale keown and greg rutkowski";
		} else {
			return "a painting of a " + DataUtils.mergeName(card.name) + ", mtg art , magic the gathering concept art , mtg art style , d&d concept art , nixeu and greg rutkowski , magic : the gathering art , magic the gathering artwork , jeff easley dramatic light , dale keown and greg rutkowski";
		}
		//		return prompter.getPromptFromCard(card);
	}

	public static class PrettyCard {

		MLCard mlcard;
		String name;
		String type;
		String stats;
		String text;
		String cost;

		public PrettyCard(MLCard card) {
			mlcard = card;
			update();
		}

		public void update() {
			try {
				name = DataUtils.mergeName(mlcard.name);
				stats = mlcard.stats == null || mlcard.stats.equals("") ? null : mlcard.stats.replace(" ", "");
				cost = mlcard.cost.replace(" ", "").toUpperCase();
				text = DataUtils.prettyText(mlcard.text, name, "@");
				type = mlcard.type;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
