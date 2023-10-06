package greenscripter.mtgcardgen.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import greenscripter.mtgcardgen.generation.Card;
import greenscripter.mtgcardgen.generation.CleanedCard;

public class OracleExtractor {

	public static void main(String[] args) throws Exception {
		List<Card> cards = CardExtractor.getFilteredCards();
		List<String> keywords = KeywordManager.getKeywords();
		List<CleanedCard> oracle = Collections.synchronizedList(new ArrayList<>());
		Set<String> planeswalkerTypes = Collections.synchronizedSet(new HashSet<>());
		cards.parallelStream().forEach(c -> {
			if (c.type_line.contains("Planeswalker")) {
				planeswalkerTypes.add(c.type_line.substring(c.type_line.lastIndexOf(" ") + 1).toLowerCase());
			}
			//			if (c.oracle_text.contains("'")) {
			//				int s = c.oracle_text.indexOf("'");
			//				System.out.println(c.oracle_text.substring(Math.max(s-10, 0), Math.min(s + 10, c.oracle_text.length())));
			//			}
		});
		planeswalkerTypes.remove("planeswalker");
		System.out.println(planeswalkerTypes);
		cards.parallelStream().forEach(c -> {
			String cleaned = c.oracle_text.replace(c.name, "~");

			if (cleaned.replace("named ~", "").contains("named")) {
				//				System.out.println(cleaned.substring(cleaned.indexOf("named")));
				boolean found = false;
				for (Card c2 : cards) {
					if (cleaned.contains("named " + c2.name)) {
						cleaned = cleaned.replace(c2.name, "@");
						found = true;
					}
				}
				if (!found) {
					cleaned = cleaned.replaceAll("named (?>[A-Z](?>\\w|\\')*( |.))*", "named @ $1");

					//					System.out.println(c.name);
					//					System.out.println(cleaned);
					//					System.out.println(c.oracle_text);
				}
			}

			cleaned = cleaned.replace("—", " — ").replace("  ", " ").replace("  ", " ");

			for (String s : c.keywords) {
				if (keywords.contains(s)) continue;
				if (cleaned.contains(s + " — ")) {
					cleaned = cleaned.replace(s + " — ", "");
				}
			}

			cleaned = cleaned.replaceAll("([^{][0-9][^}])", " $1 ")//
					.replaceAll("(^|[^{])(.)\\/(.)([^}]|)", "$1$2 / $3$4")//
					.replaceAll("\\(.*?\\)", "")//
					.replace("[", " [ ")//
					.replace("]", " ] ")//
					.replace("'", " ' ")//
					.replace("}{", "} {")//
					.replace("'s", " 's")//
					.replace(",", " ,")//
					.replace(".", " .")//
					.replace(";", " .")//
					.replace("!", " .")//
					.replace("\n", " | ")//
					//					.replace("—", "-")//emdash to minus
					.replace("—", " — ")//emdash
					.replace("−", "-")//unicode minus to minus
					.replace("-", " - ")//minus
					.replace("+", " + ")//
					.replace("\"", " \" ")//
					.replace("(", "")//
					.replace(")", "")//
					.replace(":", " :")//
					.toLowerCase();
			cleaned = cleaned.replaceAll("partner with .*? \\|", "partner with @ |");

			if (c.name.contains(",")) {
				String name = c.name.substring(0, c.name.indexOf(","));

				cleaned = cleaned.replace(name.toLowerCase(), "~");
			}
			if (c.name.contains(" the ") && c.type_line.contains("Creature")) {
				String name = c.name.substring(0, c.name.indexOf(" the "));

				cleaned = cleaned.replace(name.toLowerCase(), "~");
			}
			if (c.name.contains(" of ") && c.type_line.contains("Creature")) {
				String name = c.name.substring(0, c.name.indexOf(" of "));

				cleaned = cleaned.replace(name.toLowerCase(), "~");
			}
			if (cleaned.contains("planeswalker")) {
				for (String s : planeswalkerTypes) {
					cleaned = cleaned.replace(s, "");
				}
			}
			while (cleaned.contains("  ")) {
				cleaned = cleaned.replace("  ", " ");
			}

			cleaned = cleaned.replace("can ' t", "can't")//
					.replace("don ' t", "don't")//
					.replace("it ' s", "it's")//
					.replace("you ' ve", "you've")//
					.replace("you ' re", "you're")//
					.replace("didn ' t", "didn't")//
					.replace("wasn ' t", "wasn't")//
					.replace("aren ' t", "aren't")//
					.replace("hasn ' t", "hasn't")//
					.replace("doesn ' t", "doesn't")//
					.replace("isn ' t", "isn't")//
					.replace("they ' re", "they're")//
					.replace("they ' ve", "they've")//
					.replace("that ' s", "that's")//
					.replace("' s", "'s")//
					.replace("jump - start", "jump-start");
			if (cleaned.contains(" vitu ")) {
				System.out.println(cleaned);
				System.out.println(c.name);
				System.out.println(c.oracle_text);
			}
			cleaned = cleaned.trim();
			if (!cleaned.contains("meld")) {
				CleanedCard card = new CleanedCard();
				card.oracle = cleaned;
				card.cost = c.mana_cost.replace("}{", "} {");
				card.colors = c.colors;
				card.name = c.name;
				card.type = c.type_line;
				card.cmc = "" + (int) (c.cmc);
				card.rarity = c.rarity;
				if (c.loyalty != null) {
					card.size = c.loyalty;
				} else {
					if (c.power != null) {
						card.size = c.power + "/" + c.toughness;
					}
				}
				if (card.oracle.contains("{tk}") || card.oracle.contains("sticker") || card.oracle.contains("attraction")) return;

				oracle.add(card);
			} else {
			}
		});

		System.out.println("cleaned");

		Set<String> words = Collections.synchronizedSet(new HashSet<>());

		for (CleanedCard card : oracle) {
			String[] parts = card.oracle.split(" ");
			for (String s : parts) {
				words.add(s);
			}
		}

		words.remove("");
		System.out.println("collected words");
		List<String> nonPlural = Arrays.asList("s", "its", "");
		List<String> plural = new ArrayList<>();
		for (String s : words) {
			if (nonPlural.contains(s)) continue;
			if (s.endsWith("s") && words.contains(s.substring(0, s.length() - 1))) {
				plural.add(s);
			}
		}
		System.out.println(plural);
		//		oracle.parallelStream().forEach(card -> {
		//			for (String s : plural) {
		//				if (card.oracle.contains(s)) {
		//					card.oracle = card.oracle.replace(" " + s + " ", " " + s.substring(0, s.length() - 1) + " s ");
		//				}
		//			}
		//		});
		//		words.removeAll(plural);

		words.stream().sorted().forEach(System.out::println);
		System.out.println(words.size());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Utils.bytesToFile(gson.toJson(oracle).getBytes(), new File("Cards.json"));

	}

	public static List<CleanedCard> getCleanedCards() throws Exception {
		return Arrays.asList(new Gson().fromJson(new String(Utils.fileToBytes(new File("Cards.json"))), CleanedCard[].class));
	}

}