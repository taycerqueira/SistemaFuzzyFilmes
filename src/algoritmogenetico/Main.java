package algoritmogenetico;

import java.util.ArrayList;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import weka.core.converters.ConverterUtils.DataSource;

public class Main {
	
	static String nomeBase = "basefilmes_53atributos";
	static DataSource source;

	public static void main(String[] args) throws Exception {
		
		source = new DataSource (nomeBase + ".arff");
		
		//Cria o sistema fuzzy a partir do arquivo fcl
		FIS fis = FIS.load((nomeBase+".fcl"), true);

		if (fis == null) {
			System.err.println("Can't load file: '" + nomeBase + "'");
			System.exit(1);
		}
		
		ArrayList<Rule> regras = (ArrayList<Rule>) fis.getFunctionBlock(null).getFuzzyRuleBlock("No1").getRules();
		int quantidadeRegras = regras.size();
		
		Rule[] vetorRegras = new Rule[quantidadeRegras];
		

	}
	
	public static void calculaFitness(){
		
	}

	

}
