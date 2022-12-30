package qp;

import java.io.*;

public class Benchmark implements Runnable{
  private double iteractionsPerSecond;
  private Search search;
  private boolean proceed = true;
  private Thread benchmarkThread;

  public void start(Search s) {
        if (benchmarkThread == null) {
	    search = s;
            benchmarkThread = new Thread(this, "qpBenchmark");
            benchmarkThread.start();
        }
    }

  public double getIPS(){
	return iteractionsPerSecond;
  }

/**
* Retorna o tempo estimado para o fim da busca, considerando que nao havera sucesso (tempo total).
* O tempo retornado encontra-se em segundos. Este metodo leva em conta o numero
* de regras ainda por aplicar ao dicionario.
*/
  public double getETF(Rules r){
	return ((r.size() * search.dictionarySize()) - search.stringsTested()) / iteractionsPerSecond;
  }

/**
* Retorna uma <i>string</i> que representa o tempo estimado para o fim da busca, considerando que 
* nao havera sucesso (tempo total). Esta <i>string</i> ja esta "normalizada" para a unidade de tempo
* mais proxima.
*/
  public String getETFString(Rules r){
  	String str;
	double d = ((r.size() * search.dictionarySize()) - search.stringsTested()) / iteractionsPerSecond;
	int i;
	if ( d < 60 ){
		i = new Double(d).intValue();
		d = (double) i / 100;
		str =  d+" segundos";
	}
	else {
		d /= 60;
		if ( d < 60 ){
			d *= 100;
			i = new Double(d).intValue();
			d = (double) i / 100;
			str =  d+" minutos";
		}
		else {
			d /= 24;
			d *= 100;
			i = new Double(d).intValue();
			d = (double) i / 100;
			str = d+" horas";
		}
	}
	return str;
  }

/**
*
* Retorna o tempo estimado para o fim da busca, considerando que nao havera sucesso.
* O tempo retornado encontra-se em segundos.
*
*/
  public double getETF(){
	return (search.dictionarySize() - search.stringsTested()) / iteractionsPerSecond;
  }

/**
* Retorna uma <i>string</i> que representa o tempo estimado para o fim da busca, considerando que 
* nao havera sucesso (tempo total). Esta <i>string</i> ja esta "normalizada" para a unidade de tempo
* mais proxima.
*/
  public String getETFString(){
  	String str;
	double d = (search.dictionarySize() - search.stringsTested()) / iteractionsPerSecond;
	int i;
	if ( d < 60 ){
		i = new Double(d).intValue();
		d = (double) i / 100;
		str =  d+" segundos";
	}
	else {
		d /= 60;
		if ( d < 60 ){
			d *= 100;
			i = new Double(d).intValue();
			d = (double) i / 100;
			str =  d+" minutos";
		}
		else {
			d /= 24;
			d *= 100;
			i = new Double(d).intValue();
			d = (double) i / 100;
			str = d+" horas";
		}
	}
	return str;
  }

/**
*
* O calculo de iteracoes por segundo e feito atraves de um fluxo de execucao proprio.
* Este fluxo mantem o calculo atualizado refazendo-o uma vez a cada segundo.
*
*/
  public void run(){
	double stringsTestedAnt = 0;
	double stringsTestedNow = 0;
	long sleepTimeAdjust = 1;
	Thread myThread = Thread.currentThread();
	while (benchmarkThread == myThread) {
		try { //dormir por um segundo
        		Thread.sleep(sleepTimeAdjust * 1000);
	        } catch (InterruptedException e) {}
		//atualizacao do valor de iteracoes por segundo
		stringsTestedNow = search.stringsTested();
		iteractionsPerSecond = (stringsTestedNow - stringsTestedAnt) / sleepTimeAdjust;
		stringsTestedAnt = stringsTestedNow;
		// se uma iteracao dura mais de um segundo, ajustar o tempo para conseguir medir
		if(iteractionsPerSecond == 0)
			sleepTimeAdjust += 1;
	}
  }

  public void stop() {
	benchmarkThread = null;
  }

}
