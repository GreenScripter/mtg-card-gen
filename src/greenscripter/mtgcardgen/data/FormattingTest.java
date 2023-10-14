package greenscripter.mtgcardgen.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import greenscripter.mtgcardgen.generation.Card;
import greenscripter.mtgcardgen.generation.CleanedCard;
import greenscripter.mtgcardgen.generation.DataUtils;

public class FormattingTest {

	public static void main(String[] args) throws Exception {
		List<CleanedCard> cardsClean = OracleExtractor.getCleanedCards();
		List<Card> cards = CardExtractor.getFilteredCards();

		Map<String, Card> originals = new HashMap<>();
		for (Card c : cards) {
			c.oracle_text = c.oracle_text.replaceAll("\\(.*?\\)", "")//
					.replace(" \n", "\n")//
					.replace(";", ",")//
					.replace("—", "-")//
					.replace("−", "-")//
					.trim();
			originals.put(c.name, c);
		}

		for (CleanedCard c : cardsClean) {
			if (c.oracle.contains("@")) continue;
			if (c.name.contains(",")) continue;
			if (c.name.contains(" the ") && c.type.contains("Creature")) continue;
			if (c.name.contains(" of ") && c.type.contains("Creature")) continue;
			Card original = originals.get(c.name);
			if (original.oracle_text.contains("} .")) continue;
			if (original.oracle_text.contains("Cycling—")) continue;
			if (original.oracle_text.contains("Suspend")) continue;
			if (original.oracle_text.contains("Cumulative upkeep")) continue;
			if (original.oracle_text.contains("Entwine")) continue;
			if (original.oracle_text.contains("Escape")) continue;
			if (original.oracle_text.contains("Modular")) continue;
			if (original.oracle_text.contains("Awaken")) continue;
			if (original.oracle_text.contains("Escalate")) continue;
			if (original.oracle_text.contains("Evoke")) continue;
			if (original.oracle_text.contains("Flashback")) continue;
			if (original.oracle_text.contains("Madness")) continue;
			if (original.oracle_text.contains("Buyback")) continue;
			if (original.oracle_text.contains("one -")) continue;
			if (original.oracle_text.contains("two -")) continue;
			if (original.oracle_text.contains("one or both -")) continue;
			if (original.oracle_text.contains("one or more -")) continue;
			if (c.oracle.contains("control a planeswalker")) continue;
			if (original.oracle_text.matches("(.|\n)* - (.|\\n)*")) continue;

			if (!DataUtils.prettyText(c.oracle, c.name, "").equals(original.oracle_text)) {
				System.out.println(DataUtils.prettyText(c.oracle, c.name, ""));
				System.out.println(original.oracle_text);
				System.out.println();

			}
		}

	}

}
