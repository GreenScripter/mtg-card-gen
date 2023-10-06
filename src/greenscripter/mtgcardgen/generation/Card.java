package greenscripter.mtgcardgen.generation;
import java.util.List;
import java.util.Map;

public class Card {
	
	public String lang;
	public List<CardFace> card_faces;
	public Map<String, String> legalities;
	public List<String> colors;
	public List<String> produced_mana;
	public String type_line;
	public String name;
	public List<String> keywords;
	public String loyalty;
	public String power;
	public String toughness;
	public String flavor_text;
	public String rarity;
	public String oracle_text;
	public String mana_cost;
	public String id;
	public Map<String, String> image_uris;
	public String layout;
	public double cmc;

}