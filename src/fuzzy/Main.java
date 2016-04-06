package fuzzy;

import wangmendel.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.Stats;

public class Main {
	
	//static String nomeBase = "basefilmes";
	static String nomeBase = "basefilmes_53atributos";
	static DataSource source;
	static int quantConjuntosFuzzy = 3;
	static double porcentagemTeste = 0.1;
	
	public static void main(String[] args) throws Exception {
		
		source = new DataSource (nomeBase + ".arff");
	    Instances D = source.getDataSet();
	    //imprime informações associadas à base de dados
	    int numInstancias = D.numInstances();
	    System.out.println("* Quantidade de instâncias: " + numInstancias);  
	    System.out.println("* Quantidade de atributos: " + D.numAttributes());
	    System.out.println("* Quantidade de conjuntos fuzzy por atributo: " + quantConjuntosFuzzy);
		
	    int quantidade = (int) (numInstancias*porcentagemTeste);
	    System.out.println("Quantidade de instâncias de teste: " + quantidade);
		//Separo as instâncias que irão ser utilizadas para teste
		int[] indicesTeste = sortearInstanciasTeste(quantidade, numInstancias);
		
		//Gera as regras usando wang-mendel e gera o arquivo .fcl
		//wangMendel(indicesTeste);
		
		//Cria o sistema fuzzy a partir do arquivo fcl
		FIS fis = FIS.load((nomeBase+".fcl"), true);

		if (fis == null) {
			System.err.println("Can't load file: '" + nomeBase + "'");
			System.exit(1);
		}
		
		//Testa o sistema
		testarSistema(indicesTeste, fis);
		
		//JFuzzyChart.get().chart(fb);

		// Set inputs
		//fb.setVariable("temperatura", 965);
		//fb.setVariable("volume", 11);

		// Evaluate
		//fb.evaluate();

		// Show output variable's chart
		//fb.getVariable("polarity").defuzzify();
		

        // Print ruleSet
        //System.out.println(fis);
		
		// Print ruleSet
		//System.out.println(fb);
		//System.out.println("Pressão: " + fb.getVariable("pressao").getValue());
		
		//fis.getVariable("polarity").chartDefuzzifier(true);
		
		 // Show each rule (and degree of support)
	    //for(Rule r : fis.getFunctionBlock("caldeira").getFuzzyRuleBlock("No1").getRules())
	      //System.out.println(r);
		


	}
	
	public static void wangMendel(int[] indicesTeste){
		
		long inicio = System.currentTimeMillis(); 
	    
		try {
		    
		    System.out.println("=> Executando algoritmo de Wang-Mendel. Aguarde...");
		    WangMendel wm = new WangMendel(source, quantConjuntosFuzzy, indicesTeste);
		    ArrayList<Regra> regras = wm.gerarRegras();
		    
		    System.out.println("=> Quantidade de regras geradas: " + regras.size());
		    
		    long fim  = System.currentTimeMillis();  
		    System.out.println("* Tempo de execução (min/seg): " + new SimpleDateFormat("mm:ss").format(new Date(fim - inicio)));
		    
		    //Pega o nome do atributo que corresponde a classe. Aqui considero que esse atributo é sempre o último
		    String nomeClasse = source.getDataSet().attribute(source.getDataSet().numAttributes() - 1).name();
		    
		    //Gera o bloco de regras em um arquivo .txt
		    //gerarRuleblock(regras, nomeBase, nomeClasse);
		    
		    //Gera arquivo .fcl (fuzzy control language)
		    gerarArquivoFcl(regras, nomeClasse, wm.getListaAtributos());   

		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void testarSistema(int indicesTeste[], FIS fis) throws Exception{
		
		Instances instancias = source.getDataSet();
		for (int k = 0; k < 100; k++) {
		//for (int k = 0; k < source.getDataSet().size(); k++ ) {
			
			//Se a instância for de teste...
			if(isParaTeste(k, indicesTeste)){
				
				System.out.println("Número da instância: " + k);
				Instance instancia = instancias.get(k);
				//Seta as entradas
				//Considerando que o último atributo é sempre o atributo que corresponde a classe da instância, por isso usa-se o -1
				for (int i = 0; i < (instancias.numAttributes() - 1); i++) {
					
					if(instancias.attribute(i).isNumeric()){

						String nomeAtributo = instancias.attribute(i).name();
						double valor = instancia.value(i);
						//System.out.println(nomeAtributo + ": " + valor);
						
						fis.setVariable(nomeAtributo, valor);
					}	
				}

				// Evaluate
				fis.evaluate();
				
			    // Show each rule (and degree of support)
			    //for(net.sourceforge.jFuzzyLogic.rule.Rule r : fis.getFunctionBlock(null).getFuzzyRuleBlock("No1").getRules() )
			      //System.out.println(r);
				
				//Variable tip = fis.getFunctionBlock(null).getVariable("polarity");
				//JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);
				
				
				System.out.println("Polarity: " + fis.getVariable("polarity").getValue());
				System.out.println("Real Polarity: " + instancia.stringValue(instancias.numAttributes() - 1));
				//JFuzzyChart.get().chart(fis.getFunctionBlock(null));
				//System.out.println(fis);
				break;
					
			}
		}
		
		
	}
	
	private static boolean isParaTeste(int k, int[] indicesTeste){
		
		boolean resultado = false;
		for(int i = 0; i < indicesTeste.length; i++){
			if(indicesTeste[i] == k){
				resultado =  true;
				break;
			}
		}
		return resultado;
	}
	
	private static int[] sortearInstanciasTeste(int quantidade, int numInstancias){
		
		int[] indices = new int[quantidade];
		
		List<Integer> numeros = new ArrayList<Integer>();
		for (int i = 0; i < numInstancias; i++) { 
		    numeros.add(i);
		}
		//Embaralhamos os números:
		Collections.shuffle(numeros);
		//Adicionamos os números aleatórios no vetor
		for (int i = 0; i < quantidade; i++) {
			indices[i] = numeros.get(i);
		}
		
		return indices;
		
	}
	
	private static void gerarArquivoFcl(ArrayList<Regra> regras, String nomeClasse, ArrayList<Atributo> listaAtributos) throws IOException{
		
	    System.out.println("=> Gerando arquivo .fcl...");
	    
	    //Gerar arquivo contendo o RULEBLOCK para ser inserido em um arquivo .fcl
	    FileWriter arquivo = new FileWriter(nomeBase + ".fcl"); 
	    PrintWriter texto = new PrintWriter(arquivo);
	    
	    texto.println("FUNCTION_BLOCK " + nomeBase);
	    
	    texto.println("VAR_INPUT");  
	    for(Atributo atributo : listaAtributos){
	    	
	    	//Atualmente a o JFuzzyLogic só dá suporte ao tipo REAL
	    	texto.print("\t" + atributo.getNome() + " : REAL;\n"); 
	    	
	    }
	    texto.println("END_VAR\n");
	    
	    texto.println("VAR_OUTPUT");
	    	//Atualmente a o JFuzzyLogic só dá suporte ao tipo REAL
	    	texto.print("\t" + nomeClasse + " : REAL;");
	    texto.println("\nEND_VAR\n");
	    
	    for(Atributo atributo : listaAtributos){
	    	
	    	texto.println("FUZZIFY " + atributo.getNome());
	    	//Os pontos dos conjuntos fuzzy são de pertinência triangular (3 pontos por conjunto)
	    	for (ConjuntoFuzzy conjuntoFuzzy : atributo.getConjuntosFuzzy()) {	
	    		texto.print("\t TERM ");
	    		texto.print(conjuntoFuzzy.getIdConjunto());
	    		texto.print(" := ");
	    		texto.print("(" + conjuntoFuzzy.getLimiteInferior() + ", 0) ");
	    		texto.print("(" + conjuntoFuzzy.getM() + ", 1) ");
	    		texto.print("(" + conjuntoFuzzy.getLimiteSuperior() + ", 0);\n");
			}
	    	texto.println("END_FUZZIFY\n");
	    
	    }
	    
	    texto.println("DEFUZZIFY " + nomeClasse);
	    //Termos utilizados para trabalhar com a base de filmes onde os termos da classe (polarity) são {positive, negative}
	    //Como o JFuzzyLogica só trabalha com números reais, utilizarei {1, -1}, onde 1 é positivo e -1 negativo.
	    texto.println("\t TERM positive := 1;");
	    texto.println("\t TERM negative := -1;");
	    //texto.println("\t METHOD : COG;"); // Use 'Center Of Gravity' defuzzification method
	    texto.println("\t METHOD : COGS;"); // Centre of Gravity for Singletons
	    //texto.println("\t DEFAULT := 0;"); // Default value is 0 (if no rule activates defuzzifier)
	    texto.println("END_DEFUZZIFY\n");
	    
	    texto.println("RULEBLOCK No1");
	    texto.println("\t AND : MIN;"); //Use 'min' for 'and'
	    texto.println("\t ACT : MIN;"); //Use 'min' activation method
	    //texto.println("\t ACT : PROD;"); //Use 'Product' activation method
	    texto.println("\t ACCU : MAX;\n"); //Use 'max' accumulation method
	    
	    //Regras
	    int contadorRegra = 1;
	    for (Regra regra : regras) {
	    	
	    	texto.print("\t RULE " + contadorRegra);
	    	texto.print(" : IF ");
	    	
	    	boolean primeiroConjunto = true;
	    	
	    	for (ConjuntoFuzzy conjunto : regra.getAntecedentes()) {
	    		
	    		if(primeiroConjunto){
	    			
		    		texto.print(conjunto.getNomeAtributo());
		    		texto.print(" IS ");
		    		texto.print(conjunto.getIdConjunto());
		    		
		    		primeiroConjunto = false;
	    			
	    		}
	    		else{
	    			
	    			texto.print(" AND ");
		    		texto.print(conjunto.getNomeAtributo());
		    		texto.print(" IS ");
		    		texto.print(conjunto.getIdConjunto());
	    			
	    		}
				
			}
	    	
	    	texto.print(" THEN ");
	    	texto.print(nomeClasse);
	    	texto.print(" IS ");
	    	texto.print(regra.getConsequente());
	    	texto.print(";\n");
	    	
	    	contadorRegra++;
			
		}
	     
	    texto.println("END_RULEBLOCK\n");
	    
	    texto.println("END_FUNCTION_BLOCK");
	    
	    arquivo.close();
	    System.out.println("Arquivo .fcl gerado com sucesso.");
		
	}
	
	private static void gerarRuleblock(String nomeBase, ArrayList<Regra> regras, String nomeClasse) throws IOException{
		
	    System.out.println("=> Gerando RULEBLOCK...");
	    
	    //Gerar arquivo contendo o RULEBLOCK para ser inserido em um arquivo .flc
	    FileWriter arquivo = new FileWriter(nomeBase + ".txt"); 
	    PrintWriter texto = new PrintWriter(arquivo);
	    
	    int contadorRegra = 1;
	    for (Regra regra : regras) {
	    	
	    	texto.print("RULE " + contadorRegra);
	    	texto.print(" : IF ");
	    	
	    	boolean primeiroConjunto = true;
	    	
	    	for (ConjuntoFuzzy conjunto : regra.getAntecedentes()) {
	    		
	    		if(primeiroConjunto){
	    			
		    		texto.print(conjunto.getNomeAtributo());
		    		texto.print(" IS ");
		    		texto.print(conjunto.getIdConjunto());
		    		
		    		primeiroConjunto = false;
	    			
	    		}
	    		else{
	    			
	    			texto.print(" AND ");
		    		texto.print(conjunto.getNomeAtributo());
		    		texto.print(" IS ");
		    		texto.print(conjunto.getIdConjunto());
	    			
	    		}

			}
	    	
	    	texto.print(" THEN ");
	    	texto.print(nomeClasse);
	    	texto.print(" IS ");
	    	texto.print(regra.getConsequente());
	    	texto.print(";\n");
	    	
	    	contadorRegra++;
			
		}
	    
	    arquivo.close();
	    System.out.println("Arquivo contendo o RULEBLOCK gerado com sucesso.");
		
	}

}
