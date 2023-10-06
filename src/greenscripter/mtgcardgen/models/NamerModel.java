package greenscripter.mtgcardgen.models;

import java.io.IOException;

import greenscripter.mtgcardgen.generation.DataUtils;
import greenscripter.mtgcardgen.generation.Inference;
import greenscripter.mtgcardgen.generation.MLCard;

public class NamerModel extends Inference {

	public NamerModel() throws IOException {
		super("namegen", new String[0]);
	}

	public MLCard generateCardNameTypeCost() throws IOException {
		String line = request("****4");

		return parseLine(new MLCard(), line, 1);
	}

	public String getRandomName() throws IOException {
		String line = request("****3 ****3 ****3 ****3 ****3");
		return DataUtils.stripName(DataUtils.getSegment(line.substring(6 * 4), 4));
	}

	public MLCard fillInName(MLCard card) throws IOException {
		String line = request("****4  " + card.cost + " ****1 " + card.type + " ****2 " + (card.stats.equals("") ? "****3" : (card.stats + " ****3")));
		return parseLine(card, line, 4);
	}

	private MLCard parseLine(MLCard card, String line, int start) {
		line = line.substring(6);

		if (start <= 1) card.cost = DataUtils.stripCost(DataUtils.getSegment(line, 1));
		if (start <= 2) card.type = DataUtils.stripType(DataUtils.getSegment(line, 2));
		if (start <= 4) card.name = DataUtils.stripName(DataUtils.splitName(DataUtils.mergeName(DataUtils.stripName(DataUtils.getSegment(line, 4)))));
		if (start <= 3) card.stats = DataUtils.stripStats(DataUtils.getSegment(line, 3));

		return card;

	}
}
