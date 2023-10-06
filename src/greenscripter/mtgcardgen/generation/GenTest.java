package greenscripter.mtgcardgen.generation;

import java.io.IOException;

import com.google.gson.Gson;

import greenscripter.mtgcardgen.models.ColorModel;
import greenscripter.mtgcardgen.models.FromNameModel;
import greenscripter.mtgcardgen.models.ManaCostModel;
import greenscripter.mtgcardgen.models.NamerModel;
import greenscripter.mtgcardgen.models.RulesTextModel;
import greenscripter.mtgcardgen.models.TypeModel;

public class GenTest {

	public static void main(String[] args) throws IOException {
		Inference.python = args[0];
		Inference.mainFolder = args[1];
		StableDiffusionAPI.target = args[2];

		NamerModel namer = new NamerModel();
		RulesTextModel rules = new RulesTextModel();
		FromNameModel name = new FromNameModel();
		ManaCostModel mana = new ManaCostModel();
		ColorModel color = new ColorModel();
		TypeModel type = new TypeModel();
		for (int i = 0; i < 10; i++) {
			try {
				//				System.out.println(prompt.getPromptFromCard(namer.fillInName(rules.generateUnnamedCard())));
				long start = System.currentTimeMillis();
				MLCard c = namer.fillInName(rules.fillInTextStatsFromCostType(type.getCardFromType("Creature")));
				System.out.println(new Gson().toJson(c));
				String cardprompt = DataUtils.getArtPrompt(c);
				System.out.println(cardprompt);
				StableDiffusionAPI.txt2img(cardprompt);
				System.out.println(name.getRandomName());
				System.out.println(mana.getCostFromCard(c));
				System.out.println(color.getCardFromColor("wur"));
				System.out.println("Time taken: " + (System.currentTimeMillis() - start) + " ms.");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//		System.exit(0);
	}

}
