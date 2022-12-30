package qp;

import java.io.*;
import java.util.*;
import qp.*;

/**
* Esta classe contem a definicao de uma tarefa a ser realizada pelo sistema.
* Tal definicao se da pela ligacao entre um algoritmo de criptografia e um
* alvo (<i>target</i>) dado, normalmente, por um <i>hash</i> criado pelo algoritmo.
* A classe tambem possui metodos para gravacao/leitura de um estado de execucao,
* permitindo assim a criacao de pontos de verificacao (<i>checkpoints</i>) pelo
* sistema.
*
* @author Antonio Marcos Candia
*/
public class Task implements Serializable{
  //local variables
  private static String currentTaskTarget;
  private static Rules currentRuleHandler;
  static qpAlgorithm currentAlgorithm;
  
  public Task(String hash, qpAlgorithm algorithm){
	currentTaskTarget = hash;
	currentAlgorithm = algorithm;
	currentRuleHandler = null;
	return;
  }

  public Task(String hash, qpAlgorithm algorithm, Rules rules){
	currentTaskTarget = hash;
	currentAlgorithm = algorithm;
	currentRuleHandler = rules;
	return;
  }

  public String getCurrentTarget(){
	return currentTaskTarget;
  }
  
  public void setCurrentTarget(String newTarget){
	currentTaskTarget = newTarget;
	return;
  }
  
  public String getCurrentRule(){
	return currentRuleHandler.getCurrentRule();
  }
  
  public String getCurrentAlgorithmName(){
	return currentAlgorithm.algorithmName();
  }
  
/**
* Metodo que verifica se a tarefa corrente nao possui uma entrada na cache de senhas ja encontradas.
*/
  public static String pwdIsInCache(){
    String cacheFilename = "cache.qp";
    try {
      RandomAccessFile cache = new RandomAccessFile(cacheFilename, "rw");
      String line;
      while((line=cache.readLine()) != null)
        if(currentAlgorithm.algorithmName().equals(new String(line.substring(0,line.indexOf('\t')))))
          if(currentTaskTarget.equals(line.substring(line.indexOf('\t')+1, line.lastIndexOf('\t'))))
	    return line.substring(line.lastIndexOf('\t')+1, line.length());

      cache.close();
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
    return null;
  }

/**
* Este metodo cria uma entrada no arquivo "cache.qp", guardando a senha encontrada para a tarefa
* corrente e permitindo que a mesma seja recuperada posteriormente sem ser necessario recorrer a
* uma nova busca.
*/
  public static void cachePwdFound(String passwd){
    String cacheFilename = "cache.qp";
    try {
      RandomAccessFile cache = new RandomAccessFile(cacheFilename, "rw");
      cache.seek(cache.length());
      cache.writeBytes(currentAlgorithm.algorithmName()+'\t'+currentTaskTarget+'\t'+passwd+'\n');
      cache.close();
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
  }

/**
* Metodo que salva o estado atual de uma tarefa para posterior reinicio (<i>resume</i>).
* O estado atual e salvo no arquivo "state.qp".
*/
  public static void saveState(String rule){
    String stateFilename = "state.qp";
    try {
	RandomAccessFile state = new RandomAccessFile(stateFilename, "rw");
	if (state.length() != 0){
    		String tempStateFilename = "state.tmp";
		RandomAccessFile statetmp = new RandomAccessFile(tempStateFilename, "rw");
		String line;
		int flag = 0;
		while((line=state.readLine()) != null)
			if(currentAlgorithm.algorithmName().equals(new String(line.substring(0,line.indexOf('\t')))))
				if(currentTaskTarget.equals(line.substring(line.indexOf('\t')+1, line.lastIndexOf('\t')))){
				//se ja houve um "checkpoint" anterior ja existe uma linha no arquivo de estado 
				//para esta senha, entao somente deve ser atualizada.
				statetmp.writeBytes(currentAlgorithm.algorithmName()+'\t'+currentTaskTarget+'\t'+rule+"\n");
				flag = 1;
				}
			else
				statetmp.writeBytes(line+"\n");
		if(flag == 0)
	 		statetmp.writeBytes(currentAlgorithm.algorithmName()+'\t'+currentTaskTarget+'\t'+rule+"\n");
		state.close();
		statetmp.close();
		File stateorg = new File(stateFilename);
		File statenew = new File(tempStateFilename);
		stateorg.delete();
		statenew.renameTo(stateorg);
	}
	else {
		state.writeBytes(currentAlgorithm.algorithmName()+'\t'+currentTaskTarget+'\t'+rule+"\n");
		state.close();
	}
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
  }

/**
* Metodo que carrega um estado anterior de execucao do arquivo "state.qp".
*/
  public static String loadState(){
    String stateFilename = "state.qp";
    try {
	RandomAccessFile state = new RandomAccessFile(stateFilename, "rw");
	if (state.length() != 0){
		String line;
		while((line=state.readLine()) != null)
			if(currentAlgorithm.algorithmName().equals(new String(line.substring(0,line.indexOf('\t')))))
				if(currentTaskTarget.equals(line.substring(line.indexOf('\t')+1, line.lastIndexOf('\t')))){
				//se ja houve um "checkpoint" anterior ja existe uma linha no arquivo de estado 
				//para esta senha, entao a ultima regra usada deve ser retornada
				state.close();
				return line.substring(line.lastIndexOf('\t'), line.length()).trim();
			}
	}
	state.close();
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
    return null;
  }

}
