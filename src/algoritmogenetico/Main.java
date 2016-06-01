package algoritmogenetico;

import java.util.ArrayList;
import java.util.Random;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import weka.core.converters.ConverterUtils.DataSource;

public class Main {
	
	static String nomeBase = "basefilmes_53atributos";
	static DataSource source;

	public static void main(String[] args) throws Exception {
		
		source = new DataSource (nomeBase + ".arff");
		
		//Cria o sistema fuzzy a partir do arquivo fcl
		FIS fis = FIS.load((nomeBase + ".fcl"), true);

		if (fis == null) {
			System.err.println("Can't load file: '" + nomeBase + "'");
			System.exit(1);
		}
		
		ArrayList<Rule> regras = (ArrayList<Rule>) fis.getFunctionBlock(null).getFuzzyRuleBlock("No1").getRules();
		int quantidadeRegras = regras.size();
		
		int tamPopulacao = 100; //quantidade da população inicial
		
		boolean[][] populacao = new boolean[tamPopulacao][quantidadeRegras];
		
		//Gera população inicial
		Random randon = new Random();
		for(int i = 0; i < tamPopulacao; i++){
			for(int j = 0; j < quantidadeRegras; j++){
				populacao[i][j] = randon.nextBoolean();
			}
		}
		
		double[] fitness =  new double[tamPopulacao];
		
		for(int i = 0; i < tamPopulacao; i++){
			for(int j = 0; j < quantidadeRegras; j++){
				fitness[i] = calculaFitness(populacao[i]);
			}
		}

	}
	
	public static double calculaFitness(boolean individuo[]){
		
		int acuracia = 0;
		
		return acuracia;
		
	}
	

}
