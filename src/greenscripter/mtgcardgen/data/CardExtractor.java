package greenscripter.mtgcardgen.data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.File;

import com.google.gson.Gson;

import greenscripter.mtgcardgen.generation.Card;

public class CardExtractor {
	
	public static void main(String[] args) throws Exception {
		List<Card> cards = getFilteredCards();
		//names
		System.out.println(cards.size());
		List<String> names = cards.stream().map(c -> c.name).toList();
		Utils.save(names, new File("Names.txt"));
		
		//types
		//artifact, creature, enchantment, instant, land, planeswalker, sorcery
		
		List<String> cardTypes = cards.stream().map(c -> pad(c.name) + "\n" + types(c.type_line)).toList();
		Utils.save(cardTypes, new File("cardtypes.txt"));
		
		List<String> check = Utils.loadStringList(new File("cardstest.txt"));
		System.out.println(check.size());
		List<String> a = new ArrayList<>(check);
		
		check.removeAll(names);
		a.removeAll(check);
		System.out.println(a);
		System.out.println(check.size());
		for (String s : check) {
			for (String s2 : names) {
				if (s2.contains(s)) {
					System.out.println(s2 + " -> " + s);
					break;
				}
			}
		}
		System.out.println(check.size());
		Utils.save(check, new File("cleaned.txt"));
		//				System.out.println(check.stream().reduce((s1, s2) -> s1 + "\n" + s2));
		
		Set<String> words = new HashSet<>();
		for (Card c : cards) {
			words.addAll(Arrays.asList(c.oracle_text.replace(c.name, "~").replaceAll("(^|[^{]).\\/.([^}]|)", " / ").replaceAll("\\(.*?\\)", "").replace("[", " [ ").replace("]", " ] ").replace("'", " ' ").replace("}{", "} {").replace("'s", " 's").replace(",", " ,").replace(".", " ,").replace("\n", " | ").replace("—", "-").replace("-", " - ").toLowerCase().replace("\"", "").replace("(", "").replace(")", "").replace(":", " :").split(" ")));
		}
		System.out.println(words.size());
		System.out.println(words);
		Utils.save(new ArrayList<>(words), new File("Words.txt"));
		
		StringBuilder data = new StringBuilder();
		int length = 0;
		for (Card c : cards) {
			String text = c.oracle_text.replace(c.name, "~").replace("\n", "|").replaceAll("\\(.*?\\)", "") + "\n";
			if (text.length() > 1) {
				data.append(c.type_line + "%");
				data.append(text);
				length += text.length();
			}
		}
		System.out.println(length / cards.size());
		Utils.bytesToFile(data.toString().getBytes(), new File("oracle.txt"));
		
		//		List<String> export = Utils.loadStringList(new File("cleaned.txt"));
		//		for (String s : export) {
		//			Utils.bytesToFile(s.getBytes(), new File("tests", ThreadLocalRandom.current().nextInt() + "test.txt"));
		//		}
	}
	
	public static List<Card> getFilteredCards() throws Exception {
		File folder = new File(".");
		File oracle = Arrays.stream(folder.listFiles()).filter(f -> f.getName().startsWith("oracle-cards-")).findFirst().orElse(null);
		Gson gson = new Gson();
		Card[] origin = gson.fromJson(new String(Utils.fileToBytes(oracle)), Card[].class);
		List<Card> cards = new ArrayList<>();
		for (Card c : origin) {
			if (!c.lang.equals("en")) {
				continue;
			}
			if (c.card_faces != null) {
				continue;
			}
			if (!c.legalities.get("vintage").equals("legal")) {
				continue;
			}
			cards.add(c);
			
		}
		return cards;
	}
	
	public static String pad(String pad) {
		while (pad.length() < 64) {
			pad = pad + ((char) 0);
		}
		return pad;
	}
	
	public static String types(String typeline) {
		return "" + (typeline.contains("Artifact") ? 1 : 0) + " " + (typeline.contains("Creature") ? 1 : 0) + " " + (typeline.contains("Enchantment") ? 1 : 0) + " " + (typeline.contains("Instant") ? 1 : 0) + " " + (typeline.contains("Land") ? 1 : 0) + " " + (typeline.contains("Planeswalker") ? 1 : 0) + " " + (typeline.contains("Sorcery") ? 1 : 0);
	}
	
}

class Classified {
	
	public Classified(String text, String label) {
		this.text = text;
		this.label = label;
	}
	
	String text;
	String label;
}