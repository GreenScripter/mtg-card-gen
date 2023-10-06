package greenscripter.mtgcardgen.models;

import java.io.IOException;

import greenscripter.mtgcardgen.generation.DataUtils;
import greenscripter.mtgcardgen.generation.Inference;
import greenscripter.mtgcardgen.generation.MLCard;

public class ManaCostModel extends Inference {

	public ManaCostModel() throws IOException {
		super("costgen", new String[] {});
	}

	public MLCard getCostFromCard(MLCard card) throws IOException {
		String line = request("****4 " + card.text + " ****1 " + card.type + " ****2 " + (card.stats.equals("") ? "****3" : (card.stats + " ****3")));
		card.cost = DataUtils.stripCost(DataUtils.getSegment(line.substring(6), 4));
		return card;
	}

}
