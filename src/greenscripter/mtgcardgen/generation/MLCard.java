package greenscripter.mtgcardgen.generation;

import java.io.Serializable;

public class MLCard implements Serializable {

	static final long serialVersionUID = 139772418704551509l;
	public String cost;
	public String type;
	public String text;
	public String name;
	public String stats;
	public String prompt;
	public String image;
	int number;

	public MLCard() {

	}

	public MLCard(String cost, String type, String text, String name, String stats, String prompt, String image) {
		this.cost = cost;
		this.type = type;
		this.text = text;
		this.name = name;
		this.stats = stats;
		this.prompt = prompt;
		this.image = image;
	}

	public MLCard(MLCard card) {
		this(card.cost, card.type, card.text, card.name, card.stats, card.prompt, card.image);
	}

}
