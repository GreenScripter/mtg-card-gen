package greenscripter.mtgcardgen.generation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import greenscripter.mtgcardgen.generation.BulkCardPipeline.PrettyCard;

public class BulkCardAnalyzer {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		Inference.python = args[0];
		Inference.mainFolder = args[1];
		String name = args[2];

		System.setProperty("java.awt.headless", "true");

		String[] urls = new String[args.length - 3];
		System.arraycopy(args, 3, urls, 0, args.length - 3);

		File cardFile = new File("bulkMLCards.json");
		PrettyCard[] cards = new Gson().fromJson(Files.readString(cardFile.toPath()), PrettyCard[].class);

		File cardFolder = new File("bulkMLRenders");
		File artFolder = new File("bulkMLArt");

		//		RulesTextModel rules = new RulesTextModel();
		//		FromNameModel fromName = new FromNameModel();

		int len = 5;
		int count = 10000;
		ExecutorService threads = Executors.newFixedThreadPool(10);
		for (PrettyCard card : cards) {
			//			if (card.type.length() < 1) {
			//				fromName.generateFromName(card.mlcard);
			//				rules.fillInTextStatsFromCostType(card.mlcard);
			//				card.update();
			threads.execute(() -> {
				BufferedImage rendered;
				try {
					rendered = DataUtils.render(card.mlcard, "Overlord", ImageIO.read(new File(artFolder, card.mlcard.image)), "OVR • EN", String.format("%0" + len + "d", card.mlcard.number) + " / " + count);
					ImageIO.write(rendered, "png", new File(cardFolder, card.mlcard.image));
					System.out.println(card.mlcard.number);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			});
			//			}
		}
		//		Utils.save(new GsonBuilder().setPrettyPrinting().create().toJson(List.of(cards).stream().map(c -> c.mlcard).map(PrettyCard::new).collect(Collectors.toList())), cardFile);

	}

}
