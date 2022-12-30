import java.io.*;
import java.util.*;
import java.lang.reflect.*;

class qpsr {

  public static void main(String[] args) {
	if(args.length != 5){
		System.out.println("uso: qpsr <dicionario> <hash> <algoritmo> <inicio> <fim>");
		System.exit(-1);
	}
	qpAlgorithm qpAlg = (qpAlgorithm) createObject("qp"+args[2]);
	System.out.println(qpAlg.algorithmName());
	Task t;
	Rules r = new Rules("rules.qp");
	t = new Task(args[1], qpAlg);
	String tst;
	if((tst=t.pwdIsInCache()) != null){
		System.out.println("*** Cache Hit! ***");
        	System.out.println("*** Cracked! *** - the password is ["+tst+"]");
		System.exit(0);
	}

	Search search = new Search(args[0], t);
	long inicio, fim, dif;
        inicio = System.currentTimeMillis();
	String currentRule = t.loadState();

	System.out.println("Hash: ["+t.getCurrentTarget()+"] Algorithm: ["+t.getCurrentAlgorithmName()+"]");
	System.out.println("Rules Size: ["+r.size()+"] Dictionary Size: ["+search.dictionarySize()+"]");
	System.out.println("Current State: "+currentRule);
	if(currentRule == null)
		currentRule = r.getNextRule();
	else{
		System.out.println("Resuming previous run.");
		r.skipToRule(currentRule);
		currentRule = r.getNextRule();
	}	
	String result;
	Benchmark b = new Benchmark();
	b.start(search);
        while (currentRule != null) {
		System.out.println("Current rule: "+currentRule+" IPS: "+b.getIPS()+" ETF: "+new Double(b.getETF(r)).intValue());
		if((result = search.byRule(currentRule, new Integer(args[3]).intValue(), new Integer(args[4]).intValue())) != null){
	          System.out.println("*** Cracked! *** - the password is ["+result+"]");
                  fim = System.currentTimeMillis();
                  dif = fim - inicio;
	          System.out.println(search.stringsTested()+" strings tested by dictionary search method.");
	          System.out.println(new Double(dif/1000).intValue()+" secs ellapsed. Average:"+(dif/search.stringsTested())+" msecs/iteration.");
		  System.out.println("Caching password.");
		  t.cachePwdFound(result);
		  b.stop();
	          System.exit(0);
	        }
		t.saveState(currentRule);
		currentRule = r.getNextRule();
        }
	b.stop();
	System.out.println("Sorry, password not found.");
	System.out.println(search.stringsTested()+" strings tested by dictionary search method.");
        fim = System.currentTimeMillis();
        dif = fim - inicio;
        System.out.println(new Double(dif).intValue()/1000+" secs ellapsed. Average:"+(dif/search.stringsTested())+" msecs/iteration.");


	System.exit(0);
   }

   static Object createObject(String className) {
      Object object = null;
      try {
          Class classDefinition = Class.forName(className);
          object = classDefinition.newInstance();
      } catch (InstantiationException e) {
          System.out.println(e);
      } catch (IllegalAccessException e) {
          System.out.println(e);
      } catch (ClassNotFoundException e) {
          System.out.println(e);
      }
      return object;
   }

}
