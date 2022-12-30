package qp;

import java.io.*;
import java.util.*;
import java.rmi.*;

/**
* Esta classe define os metodos que realizam uma busca de solucao no
* espaco delimitado por um dicionario de palavras e um conjunto de regras
* de modificacao.
*
* @author Antonio Marcos Candia
*/
public class Search implements Serializable{
  //defines needed
  transient Thread mythread;
  protected double stringsTested=0;
  private static Task currentTask;
  private static String currentDict = "dict.qp";
  private double dictSize;
  private boolean proceed = true;

  public Search(String dictFile, Task t) {
    currentDict = dictFile;
    currentTask = t;
    mythread = Thread.currentThread();
  }

  public Search(Task t) {
	currentTask = t;
	mythread = Thread.currentThread();
  }

  public double stringsTested(){
	return stringsTested;
  }

  public double dictionarySize(){
	return dictSize;
  }

  public String dictionaryName(){
	return currentDict;
  }

/**
* Este metodo realiza uma busca por dicionario modificado.
* O parametro deve conter uma regra de modificacao que sera usada
* para perturbar cada palavra do dicionario do sistema. (para mais
* detalhes sobre as regras, ver classe Rules)
*/
  public String byRule(String rule){
    String tstWord;

    try {
      File file = new File(currentDict);
      if (file.exists() && file.isFile()) {
	  // processamento do dicionario a partir das regras
        BufferedReader br = null;
        String word;
	FileReader fr = new FileReader(file);
	br = new BufferedReader(fr);

	String target = new String(currentTask.getCurrentTarget());
	long dictSizeNew = 0;
        while ((word = br.readLine()) != null && proceed && !mythread.isInterrupted()) {
		dictSizeNew++;
		stringsTested++;
                //geracao de palavra modificada
	        tstWord = Rules.transmogrify(word, rule);
	        //teste da palavra gerada
	        if (currentTask.currentAlgorithm.testWord(tstWord, target))
			return tstWord;
	}
	dictSize = dictSizeNew;
	br.close();

      }
    } catch (IOException e) {
       System.err.println("Problem: " + e);
    } 
    return null;
  }
/**
* Este metodo realiza uma busca por dicionario modificado.
* O primeiro parametro deve conter uma regra de modificacao que sera usada
* para perturbar cada palavra do dicionario do sistema. (para mais
* detalhes sobre as regras, ver classe Rules)
* Os parametros seguintes demarcam que parte do dicionario sera usada, em
* termos de numeros de linhas inicial e final. Isto significa que este metodo
* foi projetado para realizar a busca utilizando um subconjunto de palavras do
* dicionario, possibilitando uma busca particionada.
*/
  public String byRule(String rule, int start, int end){
    String tstWord;

    try {
      File file = new File(currentDict);
      if (file.exists() && file.isFile()) {
	  // processamento do dicionario a partir das regras
        LineNumberReader br = null;
        String word="";
	FileReader fr = new FileReader(file);
	br = new LineNumberReader(fr);
	int lineNumber = 0;
	for (;lineNumber < start; lineNumber++)
		br.readLine();

	String target = new String(currentTask.getCurrentTarget());
	dictSize = end - start + 1;
        while ((word = br.readLine()) != null && proceed && !mythread.isInterrupted()) {
		stringsTested++;
		if(lineNumber == end)
			break;
		lineNumber++;
                //geracao de palavra modificada
	        tstWord = Rules.transmogrify(word, rule);
	        //teste da palavra gerada
	        if (currentTask.currentAlgorithm.testWord(tstWord, target))
			return tstWord;
	}
	br.close();
	fr.close();

      }
    } catch (IOException e) {
       System.err.println("Problem: " + e);
    } 
    return null;
  }


/**
* Este metodo realiza uma busca por conjunto de caracteres.
* Os parametros devem conter um conjunto de caracteres a ser usado,
* que deve estar na forma de um vetor de caracteres, e a largura de
* palavra, que indica qual o tamanho das palavras que serao
* testadas durante a busca.
*/
  public String byCharacterSet(char[] charSet, int width){
	String tstWord;
	String target = new String(currentTask.getCurrentTarget());
	//calculo do tamanho da busca por analise combinatoria
	dictSize = Math.pow((double) charSet.length, (double) width);

	boolean proceed = true;
	char[] tstChar = new char[width];
	int[] tstCharInt = new int[width];
	int i;
	for(i=0; i < width; i++)
		tstCharInt[i] = 0;
	do {
		for(i=0; i < width; i++)
			tstChar[i] = charSet[tstCharInt[i]];
		stringsTested++;
	        //teste da palavra gerada
		tstWord = new String(tstChar);
	        if ((boolean)currentTask.currentAlgorithm.testWord(tstWord, target))
			return tstWord;
		//gera proxima palavra
		for(i=width-1; i >= 0; i--){
			tstCharInt[i]++;
			if(tstCharInt[i] == charSet.length)
				tstCharInt[i] = 0;
			else
				break; // cai fora do for
		}
	} while (stringsTested < dictSize && proceed && !mythread.isInterrupted()); 

	return null;
  }

/**
* Este metodo realiza uma busca por conjunto de caracteres.
* Os parametros devem conter um conjunto de caracteres a ser usado,
* que deve estar na forma de um vetor de caracteres, e a largura de
* palavra, que indica qual o tamanho das palavras que serao
* testadas durante a busca. Este metodo tambem requer uma string de inicio
* e uma de fim de busca, significando que o mesmo foi projetado para realizar
* uma busca em um subconjunto do espaco total de buscas assim delimitado e
* possibilitando uma busca particionada.
*/
  public String byCharacterSet(char[] charSet, int width, String start, String end){
	String tstWord;
	String target = new String(currentTask.getCurrentTarget());

	boolean proceed = true;
	char[] tstChar = new char[width];
	int[] tstCharInt = new int[width];
	int i, j;
	for(i=0; i < width; i++){
		j = 0;
		while(charSet[j] != start.charAt(i))
			j++;
		tstCharInt[i] = j;
	}
	do {
		for(i=0; i < width; i++)
			tstChar[i] = charSet[tstCharInt[i]];
		stringsTested++;
	        //teste da palavra gerada
		tstWord = new String(tstChar);
	        if (currentTask.currentAlgorithm.testWord(tstWord, target))
			return tstWord;
		//gera proxima palavra
		for(i=width-1; i >= 0; i--){
			tstCharInt[i]++;
			if (tstCharInt[i] == charSet.length)
				tstCharInt[i] = 0;
			else 
				break; // cai fora do for
		}
		//verifica se chegou ao fim da faixa
		for(i=width-1; i >= 0; i--){
			if(tstChar[i] == end.charAt(i)){
				if(i==0)
					proceed = false;
			} else
				break;
		}
	} while (proceed && !mythread.isInterrupted()); 

	return null;
  }

  public void stop(){
  	proceed = false;
  }
}
