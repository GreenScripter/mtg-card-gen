package greenscripter.mtgcardgen.models;

import java.io.IOException;

import greenscripter.mtgcardgen.generation.DataUtils;
import greenscripter.mtgcardgen.generation.Inference;
import greenscripter.mtgcardgen.generation.MLCard;

public class TypeModel extends Inference {

	public TypeModel() throws IOException {
		super("typegen", new String[] {});
	}

	public MLCard getCardFromType(String singletype) throws IOException {
		String line = request("****3  " + singletype + " ****1");
		MLCard card = new MLCard();
		card.cost = DataUtils.stripCost(DataUtils.getSegment(line.substring(6), 3));
		card.type = DataUtils.stripType(DataUtils.getSegment(line.substring(6), 2));
		return card;
	}

	public String extendType(String singletype) throws IOException {
		return getCardFromType(singletype).type;
	}

}
