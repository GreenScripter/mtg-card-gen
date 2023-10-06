package greenscripter.mtgcardgen.models;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import java.io.IOException;

import greenscripter.mtgcardgen.generation.DataUtils;
import greenscripter.mtgcardgen.generation.Inference;
import greenscripter.mtgcardgen.generation.MLCard;

public class RulesTextModel extends Inference {

	public RulesTextModel() throws IOException {
		super("rulestextgen", new String[0]);
	}

	public RulesTextModel(String[] args) throws IOException {
		super("rulestextgen", args);
	}

	public MLCard generateUnnamedCard() throws IOException {
		String line = request("****4");

		return parseLine(new MLCard(), line, 1);
	}

	public MLCard fillInCard(MLCard c) throws IOException {
		String line = request("****4");

		return parseLine(c, line, 1);
	}

	public List<MLCard> generateUnnamedCards() throws IOException {
		String init = request("<longgen>****4");

		String[] lines = init.substring(5).replace("****4", "****4<split>").split(Matcher.quoteReplacement("<split>"));

		List<MLCard> cards = new ArrayList<>();

		for (String line : lines) {
			try {
				MLCard card = parseLine(new MLCard(), "****4" + line, 1);
				if (card.type.contains("Attraction")) continue;
				if (card.type.isBlank()) continue;
				cards.add(card);
			} catch (Exception e) {

			}
		}

		return cards;
	}

	public MLCard fillInFromCost(MLCard card) throws IOException {
		String line = request("****4  " + DataUtils.stripCost(card.cost) + " ****1");
		return parseLine(card, line, 2);
	}

	public MLCard fillInTextStatsFromCostType(MLCard card) throws IOException {
		String line = request("****4  " + DataUtils.stripCost(card.cost) + " ****1 " + DataUtils.stripType(card.type) + " ****2");
		return parseLine(card, line, 3);
	}

	public MLCard fillInTextStatsFromType(MLCard card) throws IOException {
		String line = request("****1 " + DataUtils.stripType(card.type) + " ****2");
		return parseLine(card, line, 3);
	}

	public MLCard fillInStatsFromCostTypeText(MLCard card) throws IOException {
		String line = request("****4  " + DataUtils.stripCost(card.cost) + " ****1 " + DataUtils.stripType(card.type) + " ****2 " + DataUtils.stripOracle(card.text) + " ****3");
		return parseLine(card, line, 3);
	}

	private MLCard parseLine(MLCard card, String line, int start) {
		line = line.substring(6);

		if (start <= 1) card.cost = DataUtils.stripCost(DataUtils.getSegment(line, 1));
		if (start <= 2) card.type = DataUtils.stripType(DataUtils.getSegment(line, 2));
		if (start <= 3) card.text = DataUtils.stripOracle(DataUtils.getSegment(line, 3));
		if (start <= 4) card.stats = DataUtils.stripStats(DataUtils.getSegment(line, 4));

		return card;

	}
}
