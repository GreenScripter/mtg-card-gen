package greenscripter.mtgcardgen.data.gen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import java.io.File;

import com.google.gson.Gson;

import greenscripter.mtgcardgen.data.Utils;
import greenscripter.mtgcardgen.generation.CleanedCard;

public class OracleFromColor {

	public static void main(String[] args) throws Exception {
		CleanedCard[] cards = new Gson().fromJson(new String(Utils.fileToBytes(new File("Cards.json"))), CleanedCard[].class);

		System.out.println(cards.length);
		List<String> lines = new ArrayList<>();
		int maxLetters = 0;
		int maxOracle = 0;
		int maxType = 0;
		int totalWords = 0;
		for (int i = 0; i < 2; i++) {
			shuffleArray(cards);
			for (CleanedCard card : cards) {
				if (card.oracle.contains("{tk}") || card.oracle.contains("sticker") || card.oracle.contains("attraction")) continue;

				StringBuilder sb = new StringBuilder();

				//			//			for (int i = 0; i < 33 - letters.length; i++) {
				//			//				sb.append("= ");
				//			//			}
				//			if (!card.type.contains("Instant") && !card.type.contains("Sorcery")) continue;
				//				if (card.oracle.split(" ").length<40) continue;
				//				if (i % 2 == 0) {
				//					sb.append(card.cmc);
				//				} else {
				//					sb.append("random");
				//				}
				//				sb.append(" ****0 ");
				//				Collections.shuffle(card.colors);
				Collections.sort(card.colors);
				sb.append("[ " + card.colors.stream().reduce((s1, s2) -> s1 + " " + s2).orElse("") + " ]");
				sb.append(" ****1 ");
				sb.append(card.type);

				//			for (int i = 0; i < /*6*/81 - card.type.split(" ").length; i++) {
				//				sb.append(" =");
				//			}
				//			maxLetters = Math.max(letters.length, maxLetters);
				maxType = Math.max(card.type.split(" ").length, maxType);
				maxOracle = Math.max(card.oracle.split(" ").length, maxOracle);

				sb.append(" ****2 ");
				sb.append(card.cost.toLowerCase());
				sb.append(" ****3 ");
				lines.add(sb.toString().replace("  ", " "));
				totalWords += lines.get(lines.size() - 1).split(" ").length;

			}
		}
		List<String> tmplines = new ArrayList<>(lines);
		for (int i = 0; i < 10; i++) {
			Collections.shuffle(tmplines);

			lines.addAll(tmplines);
		}
		for (String s : lines) {
			System.out.println(s.split(" ").length);

			System.out.println(s);
		}
		System.out.println(maxLetters);
		System.out.println(maxType);
		System.out.println(maxOracle);
		System.out.println(totalWords / cards.length);
		Utils.save(lines, new File("colortotype.txt"));
	}

	// Implementing Fisher–Yates shuffle
	static void shuffleArray(CleanedCard[] ar) {
		// If running on Java 6 or older, use `new Random()` on RHS here
		Random rnd = ThreadLocalRandom.current();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			CleanedCard a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
}
