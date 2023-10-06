package greenscripter.mtgcardgen.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import greenscripter.mtgcardgen.data.gen.OracleFromColor;
import greenscripter.mtgcardgen.data.gen.OracleFromNames;
import greenscripter.mtgcardgen.data.gen.OracleFromType;
import greenscripter.mtgcardgen.data.gen.OracleItemizer;
import greenscripter.mtgcardgen.data.gen.OracleToCastingCost;
import greenscripter.mtgcardgen.data.gen.OracleToNames;

public class DataGenerator {

	public static void main(String[] args) throws Exception {
		OracleExtractor.main(args);
		
		OracleFromColor.main(args);
		OracleFromNames.main(args);
		OracleFromType.main(args);
		OracleToCastingCost.main(args);
		OracleToNames.main(args);
		OracleItemizer.main(args);
		
		Files.copy(Path.of("cardoracle.txt"), Path.of("Python/rulestextgen/data/cardoracle.txt"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Path.of("cardtoname.txt"), Path.of("Python/namegen/data/cardtoname.txt"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Path.of("cardtocost.txt"), Path.of("Python/costgen/data/cardtocost.txt"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Path.of("cardfromtype.txt"), Path.of("Python/typegen/data/cardfromtype.txt"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Path.of("nametocard.txt"), Path.of("Python/namegen/data/nametocard.txt"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Path.of("colortotype.txt"), Path.of("Python/colorgen/data/colortotype.txt"), StandardCopyOption.REPLACE_EXISTING);
		
	}

}
