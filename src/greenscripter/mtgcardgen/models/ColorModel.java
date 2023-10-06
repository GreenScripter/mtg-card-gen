package greenscripter.mtgcardgen.models;

import java.io.IOException;

import greenscripter.mtgcardgen.generation.DataUtils;
import greenscripter.mtgcardgen.generation.Inference;
import greenscripter.mtgcardgen.generation.MLCard;

public class ColorModel extends Inference {

	public ColorModel() throws IOException {
		super("colorgen", new String[] {});
	}

	public MLCard getCardFromColor(String colors) throws IOException {
		String colorList = colors.toUpperCase().chars().sorted().mapToObj(i -> "" + (char) i).reduce((s1, s2) -> s1 + " " + s2).orElse("");
		String line = request("****3  [ " + colorList + " ] ****1");
		MLCard card = new MLCard();
		card.cost = DataUtils.stripCost(DataUtils.getSegment(line.substring(6), 3));
		card.type = DataUtils.stripType(DataUtils.getSegment(line.substring(6), 2));
		return card;
	}

	public MLCard getCardFromColorType(String colors, String type) throws IOException {
		String colorList = colors.toUpperCase().chars().sorted().mapToObj(i -> "" + (char) i).reduce((s1, s2) -> s1 + " " + s2).orElse("");
		String line = request("****3  [ " + colorList + " ] ****1 " + type + " ****2");
		MLCard card = new MLCard();
		card.cost = DataUtils.stripCost(DataUtils.getSegment(line.substring(6), 3));
		card.type = DataUtils.getSegment(line.substring(6), 2);
		return card;
	}

}
