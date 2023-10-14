package greenscripter.mtgcardgen.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import greenscripter.mtgrenderer.CardRenderer;

public class DataUtils {

	//	public static void main(String[] args) {
	//		System.out.println(prettyText("{t} flying | {1} , {t}, sacrifice ~ : gain 1 life . | at the beginning of your combat step , sacrifice an artifact, a non-goblin , scry 1 , * / + x create a 1 / 1 legendary goblin wizard creature land token with \" regenerate {b} \" , ~ gets + 1 / - 1 or + 10 / + 10 . | {b} , {t} : tap target creature . if it is blue , ~ deals damage equal to ~ 's power to that creature . @ | iii - sacrifice this saga . ", "Test", "Other Name"));
	//	}

	public static String capitalizeWords(String s) {
		String[] parts = s.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			sb.append(capitalize(parts[i]));
			if (i < parts.length - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	public static String capitalize(String s) {
		if (s == null) return "Null";
		if (s.length() <= 1) {
			return s.toUpperCase();
		}
		return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
	}

	public static String capitalizeFirstLetter(String s) {
		if (s == null) return "Null";
		if (s.length() <= 1) {
			return s.toUpperCase();
		}
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public static String prettyText(String oracle, String cardName, String referenceName) {
		oracle = oracle.replace(" i ", " I ");
		oracle = oracle.replace(" ii ", " II ");
		oracle = oracle.replace(" iii ", " III ");

		oracle = oracle.replace("| ", "\n");
		oracle = oracle.replace("|", "\n").trim();
		oracle = oracle.replace("<pipe>", "|");

		if (oracle.length() == 0) return oracle;

		for (String s : subtypes) {
			oracle = oracle.replace(" " + s.toLowerCase() + " ", " " + s + " ");
			oracle = oracle.replace(" " + s.toLowerCase() + "s ", " " + s + "s ");
			if (oracle.endsWith(s.toLowerCase())) {
				oracle = oracle.replace(" " + s.toLowerCase(), " " + s);
			}
			if (oracle.endsWith(" " + s.toLowerCase() + "s")) {
				oracle = oracle.replace(" " + s.toLowerCase() + "s", " " + s + "s");
			}
		}

		oracle = oracle.replace(" non - ", " non-");
		oracle = oracle.replace("\nnon - ", "\nnon-");

		oracle = oracle.replace(" x ", " X ");
		oracle = oracle.replace(" y ", " Y ");
		oracle = oracle.replace(" z ", " Z ");

		oracle = oracle.replaceAll("(([-+]) )?([0-9xXyYzZ*]+) \\/ (([-+]) )?([0-9xXyYzZ*])", "$2$3/$5$6");
		oracle = oracle.replaceAll("([0-9]+) — ([0-9]+) \\| ", "$1-$2 | ");
		oracle = oracle.replaceAll("([0-9]+) \\+ \\| ", "$1+ | ");
		oracle = oracle.replace(" ,", ",");
		oracle = oracle.replace(" .", ".");
		oracle = oracle.replace(" :", ":");
		oracle = oracle.replace(" 's", "'s");
		oracle = oracle.replace("} {", "}{");

		StringBuilder sb = new StringBuilder(oracle);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));

		boolean inSymbol = false;
		boolean inQuotes = false;
		boolean inSingleQuotes = false;
		boolean inCost = false;
		List<Integer> inCostStarts = new ArrayList<>();

		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '{') {
				inSymbol = true;
			}
			if (sb.charAt(i) == '}') {
				inSymbol = false;
			}
			if (sb.charAt(i) == '[' && i + 1 < sb.length()) {
				sb.deleteCharAt(i + 1);
			}
			if (sb.charAt(i) == ']' && i > 0) {
				sb.deleteCharAt(i - 1);
				i--;
			}
			if (sb.charAt(i) == '}') {
				inSymbol = false;
			}
			if (i >= 1 && i < sb.length() && sb.charAt(i) == '"') {
				if (inQuotes) {
					sb.deleteCharAt(i - 1);
					i--;
				} else {
					if (i < sb.length() - 1) {
						sb.deleteCharAt(i + 1);
					} else {
						sb.deleteCharAt(i);
						i--;
					}
					inCostStarts.clear();
					inCost = false;
				}
				inQuotes = !inQuotes;
			}
			if (i >= 1 && i + 1 < sb.length() && sb.charAt(i) == '\'' && sb.charAt(i + 1) == ' ' && sb.charAt(i - 1) == ' ') {
				if (inSingleQuotes) {
					sb.deleteCharAt(i - 1);
					i--;
				} else {
					if (i < sb.length() - 1) {
						sb.deleteCharAt(i + 1);
					} else {
						sb.deleteCharAt(i);
						i--;
					}
					inCostStarts.clear();
					inCost = false;
				}
				inSingleQuotes = !inSingleQuotes;
			}

			if (sb.charAt(i) == ',') {
				inCost = true;
				inCostStarts.add(i);
			}

			if (sb.charAt(i) == ':' && inCost) {
				for (int j : inCostStarts) {
					sb.setCharAt(j + 2, Character.toUpperCase(sb.charAt(j + 2)));
				}
				inCostStarts.clear();
				inCost = false;
			}
			if (sb.charAt(i) == '\n') {
				inCost = false;
				inCostStarts.clear();
			}
			if (inSymbol) sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));

			if (i >= 1 && (sb.charAt(i - 1) == '\n' || sb.charAt(i - 1) == '"')) {
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
			}
			if (i >= 2 && sb.charAt(i - 2) == '—') {
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
			}
			if (i >= 2 && sb.charAt(i - 2) == '|' && sb.charAt(i - 1) == ' ') {
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
			}
			if (i >= 1 && (sb.charAt(i) == '\n' && sb.charAt(i - 1) == ' ')) {
				sb.deleteCharAt(i - 1);
				i--;
			}
			if (i >= 2 && (sb.charAt(i - 2) == '.' || sb.charAt(i - 2) == ':' || sb.charAt(i - 2) == '•')) {
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
			}
			if (i >= 3 && sb.charAt(i - 3) == '.' && sb.charAt(i - 2) == '"' && sb.charAt(i - 1) == ' ') {
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
			}

			if (i >= 4 && (sb.charAt(i - 4) == 'I' && (sb.charAt(i - 2) == '-' || sb.charAt(i - 2) == '—'))) {
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
			}
			if (i >= 3 && (sb.charAt(i - 3) == 's' && sb.charAt(i - 2) == ' ' && sb.charAt(i - 1) == '\'' && sb.charAt(i) == ' ')) {
				sb.deleteCharAt(i - 2);
				i--;
			}
		}

		oracle = sb.toString();
		oracle = oracle.replaceAll("(-|\\+) ([0-9X]+): ", "$1$2: ");
		oracle = oracle.replaceAll(", sacrifice (.*?):", ", Sacrifice $1:");
		oracle = oracle.replaceAll(", remove (.*?):", ", Remove $1:");
		oracle = oracle.replaceAll("\nLevel ([0-9]+) \\+", "\nLEVEL $1+");
		oracle = oracle.replaceAll("\nLevel ([0-9]+) \\- ([0-9]+)", "\nLEVEL $1-$2");
		oracle = oracle.replace("and / or", "and/or");
		oracle = oracle.replace("weren ' t", "weren't");
		//		oracle = oracle.replace("non-aura", "non-Aura");
		oracle = oracle.replace("Eldrazi scion", "Eldrazi Scion");
		oracle = oracle.replace("arcane", "Arcane");
		oracle = oracle.replace("elves", "Elves");
		oracle = oracle.replace("werewolves", "Werewolves");
		oracle = oracle.replace("wolves", "Wolves");
		oracle = oracle.replace("ring tempts you", "Ring tempts you");
		oracle = oracle.replace("For mirrodin.", "For Mirrodin!");
		oracle = oracle.replace("monster role", "Monster Role");
		oracle = oracle.replace("young hero role", "Young Hero Role");
		oracle = oracle.replace("wicked role", "Wicked Role");
		oracle = oracle.replace("royal role", "Royal Role");
		oracle = oracle.replace("cursed role", "Cursed Role");
		oracle = oracle.replace("sorcerer role", "Sorcerer Role");
		oracle = oracle.replace("ring-bearer", "Ring-bearer");
		oracle = oracle.replace("~", cardName);
		oracle = oracle.replace("@", referenceName);

		oracle = oracle.strip();

		return oracle;
	}

	public static String splitName(String normalName) {
		//		System.out.println("Split: " + normalName);
		String[] letters = normalName.toLowerCase().split("");
		StringBuilder sb = new StringBuilder();
		for (String s : letters) {
			sb.append(s.replace(" ", "<ns>"));
			sb.append(" ");
		}
		if (letters.length > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String mergeName(String splitName) {
		//		System.out.println("Merge: " + splitName);

		String out = capitalizeWords(splitName.replace(" ", "").replace("<ns>", " ")).replace("Of ", "of ").replace("The ", "the ").replace("And ", "and ");
		if (out.contains("-")) {
			for (int i = 0; i < out.length(); i++) {
				if (out.charAt(i) == '-' && i + 1 < out.length()) {
					out = out.substring(0, i + 1) + Character.toUpperCase(out.charAt(i + 1)) + out.substring(i + 2);
				}
			}
		}
		out = capitalizeFirstLetter(out);
		return out;
	}

	public static String getSegment(String line, int i) {
		if (line.indexOf("****" + i) == -1) {
			return "";
		}
		//		if (i > 1) {
		if (line.indexOf("****" + (i - 1)) == -1) {
			if (line.indexOf("****" + (i - 2)) != -1) line = line.substring(line.indexOf("****" + (i - 2)) + ("****" + (i - 2)).length());
		} else {
			line = line.substring(line.indexOf("****" + (i - 1)) + ("****" + (i - 1)).length());
		}

		//		}
		return line.substring(0, line.indexOf("****" + i)).strip();
	}

	public static BufferedImage render(MLCard card, String user, BufferedImage art) throws Exception {
		return render(card, user, art, "AI   •   EN", "S 0000");
	}

	public static BufferedImage render(MLCard card, String user, BufferedImage art, String setMark, String count) throws Exception {
		String name = DataUtils.mergeName(card.name);
		String stats = card.stats == null || card.stats.equals("") ? null : card.stats.replace(" ", "");
		String cost = card.cost.replace("{", "").replace("}", "").replace("/", "").toUpperCase();
		BufferedImage render = CardRenderer.renderCard(name, card.type, cost, DataUtils.prettyText(card.text, name, "@"), stats, false, art);
		CardRenderer.renderMetaText(render, "Stable Diffusion", setMark, count, "Generated by " + user, stats != null);
		return render;
	}

	public static List<CleanedCard> allCards;
	public static Set<String> subtypes = new HashSet<>();
	public static Set<String> supertypes = new HashSet<>();
	public static Set<String> validTypeComponents = new HashSet<>();
	public static Set<String> validNameLetters = new HashSet<>();
	public static Set<String> validOracle = new HashSet<>();
	public static Set<String> validCost = new HashSet<>();
	public static Set<String> validStats = new HashSet<>();

	static {
		try {
			allCards = getCleanedCards();
			for (CleanedCard c : allCards) {
				if (c.oracle.contains("{tk}") || c.oracle.contains("sticker") || c.oracle.contains("attraction")) continue;

				String type = c.type;

				validTypeComponents.addAll(List.of(type.split(" ")));
				validNameLetters.addAll(List.of(DataUtils.splitName(c.name).split(" ")));
				validOracle.addAll(List.of(c.oracle.split(" ")));
				validCost.addAll(List.of(c.cost.toLowerCase().split(" ")));
				validStats.addAll(List.of((c.size == null ? "" : c.size).replace("/", " / ").split(" ")));

				String[] parts = type.split("—");
				supertypes.addAll(List.of(parts[0].strip().split(" ")));
				if (parts.length > 1) {
					subtypes.addAll(List.of(parts[1].strip().split(" ")));
				}
				subtypes.add("Prism");
				subtypes.add("Servo");
				subtypes.add("Sculpture");
				subtypes.add("Adventure");
				subtypes.add("Saproling");
				subtypes.add("Blood");
				subtypes.add("Blinkmoth");
				subtypes.add("Background");
			}
			supertypes.remove("Stickers");
			//			validNameLetters.add("<>");
			//			System.out.println(supertypes);
			//
			//			System.out.println(subtypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String combine(String s1, String s2) {
		return s1 + " " + s2;
	}

	public static boolean isValidTypeWord(String s) {
		return validTypeComponents.contains(s);
	}

	public static boolean isValidType(String s) {
		return validTypeComponents.containsAll(List.of(s.split(" ")));
	}

	public static String stripType(String s) {
		String r = List.of(s.split(" ")).stream().filter(DataUtils::isValidTypeWord).reduce(DataUtils::combine).orElse("");
		return r;
	}

	public static boolean isValidNameChar(String s) {
		return validNameLetters.contains(s);
	}

	public static boolean isValidName(String s) {
		return validNameLetters.containsAll(List.of(s.split(" ")));
	}

	public static String stripName(String s) {
		return List.of(s.split(" ")).stream().filter(DataUtils::isValidNameChar).reduce(DataUtils::combine).orElse("");
	}

	public static boolean isValidOracleWord(String s) {
		return validOracle.contains(s);
	}

	public static boolean isValidOracle(String s) {
		return validOracle.containsAll(List.of(s.split(" ")));
	}

	public static String stripOracle(String s) {
		return List.of(s.split(" ")).stream().filter(DataUtils::isValidOracleWord).reduce(DataUtils::combine).orElse("");
	}

	public static boolean isValidCostSymbol(String s) {
		return validCost.contains(s);
	}

	public static boolean isValidCost(String s) {
		return validCost.containsAll(List.of(s.split(" ")));
	}

	public static String stripCost(String s) {
		return List.of(s.split(" ")).stream().filter(DataUtils::isValidCostSymbol).reduce(DataUtils::combine).orElse("");
	}

	public static boolean isValidStatsChar(String s) {
		return validStats.contains(s);
	}

	public static boolean isValidStats(String s) {
		return validStats.containsAll(List.of(s.split(" ")));
	}

	public static String stripStats(String s) {
		return List.of(s.split(" ")).stream().filter(DataUtils::isValidStatsChar).reduce(DataUtils::combine).orElse("");
	}

	public static List<CleanedCard> getCleanedCards() throws Exception {
		return Arrays.asList(new Gson().fromJson(Files.readString(new File("Cards.json").toPath()), CleanedCard[].class));
	}

	public static String getArtPrompt(MLCard card) {
		if (card.type.contains("Creature")) {
			return "a painting of a " + card.type.substring(card.type.indexOf("—") + 1) + " , " + DataUtils.mergeName(card.name) + ", mtg art , magic the gathering concept art , mtg art style , d&d concept art , nixeu and greg rutkowski , magic : the gathering art , magic the gathering artwork , jeff easley dramatic light , dale keown and greg rutkowski";
		} else if (card.type.contains("Land")) {
			return "a painting of a " + card.type + " , " + DataUtils.mergeName(card.name) + ", mtg art , magic the gathering concept art , mtg art style , d&d concept art , nixeu and greg rutkowski , magic : the gathering art , magic the gathering artwork , jeff easley dramatic light , dale keown and greg rutkowski";
		} else {
			return "a painting of a " + DataUtils.mergeName(card.name) + ", mtg art , magic the gathering concept art , mtg art style , d&d concept art , nixeu and greg rutkowski , magic : the gathering art , magic the gathering artwork , jeff easley dramatic light , dale keown and greg rutkowski";
		}
	}

	public static Color getCardColor(MLCard card) {
		String color = "colorless";
		if (card.cost.contains("w")) {
			color = color.equals("colorless") ? "white" : "gold";
		}
		if (card.cost.contains("u")) {
			color = color.equals("colorless") ? "blue" : "gold";
		}
		if (card.cost.contains("b")) {
			color = color.equals("colorless") ? "black" : "gold";
		}
		if (card.cost.contains("r")) {
			color = color.equals("colorless") ? "red" : "gold";
		}
		if (card.cost.contains("g")) {
			color = color.equals("colorless") ? "green" : "gold";
		}

		return switch (color) {
			case "white" -> new Color(248, 231, 185);
			case "blue" -> new Color(14, 104, 171);
			case "black" -> new Color(21, 11, 0);
			case "red" -> new Color(211, 32, 42);
			case "green" -> new Color(0, 115, 62);
			case "gold" -> new Color(225, 196, 50);
			default -> new Color(156, 156, 156);
		};
	}

	public static byte[] getBytes(BufferedImage image) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
}
