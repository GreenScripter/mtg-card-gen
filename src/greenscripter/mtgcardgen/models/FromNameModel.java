package greenscripter.mtgcardgen.models;

import java.io.IOException;

import greenscripter.mtgcardgen.generation.DataUtils;
import greenscripter.mtgcardgen.generation.Inference;
import greenscripter.mtgcardgen.generation.MLCard;

public class FromNameModel extends Inference {

	public FromNameModel() throws IOException {
		super("namegen", new String[] { "--from-name" });
	}

	public String getRandomName() throws IOException {
		String line = request("****3 ****3 ****3 ****3 ");
		return DataUtils.getSegment(line.substring(6 * 4), 1);
	}

	public MLCard getRandomNamed() throws IOException {
		String line = request("****3 ****3 ****3 ****3 ");
		return parseLine(new MLCard(), line.substring(6 * 3), 1);
	}

	public MLCard generateFromName(String name) throws IOException {
		String line = request("****3 " + DataUtils.stripName(name) + " ****1");
		return parseLine(new MLCard(), line, 1);
	}

	public MLCard generateFromName(MLCard named) throws IOException {
		String line = request("****3 " + DataUtils.stripName(named.name) + " ****1");
		return parseLine(named, line, 2);
	}

	private MLCard parseLine(MLCard card, String line, int start) {
		line = line.substring(6);

		if (start <= 1) card.name = DataUtils.stripName(DataUtils.getSegment(line, 1));
		if (start <= 2) card.cost = DataUtils.stripCost(DataUtils.getSegment(line, 2));
		if (start <= 3) card.type = DataUtils.stripType(DataUtils.getSegment(line, 3));

		return card;

	}

	public MLCard fillInTypeFromNameCost(MLCard named) throws IOException {
		String line = request("****3 " + named.name + " ****1 " + named.cost + " ****2");
		named.type = DataUtils.stripType(DataUtils.getSegment(line, 3));
		return named;
	}
}
