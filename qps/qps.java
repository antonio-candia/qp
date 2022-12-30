import java.io.*;
import java.util.*;
//import Task.*;
//import Rules.*;
//import Search.*;


public class qps {
  //defines needed
  static String algorithms[] = {"crypt", "md5"};
  static int CRYPT=0;
  static int MD5=1;
  
  public qps(String dictFile, Task t, Rules r) {
	String result, tstStr;
        double inicio, fim, dif;
        String word;
        String currentRule;
	Search search = new Search(dictFile, t);
        inicio = System.currentTimeMillis();
	currentRule = t.loadState();

	System.out.println("Hash: ["+t.getCurrentTarget()+"] Algorithm: ["+t.getCurrentAlgorithm()+"]");
	System.out.println("Current State: "+currentRule);
	if(currentRule == null)
		currentRule = r.getNextRule();
	else{
		System.out.println("Resuming previous run.");
		r.skipToRule(currentRule);
		currentRule = r.getNextRule();
	}	
        while (currentRule != null) {
		System.out.println("Current State: "+currentRule);
		if((result = search.byRule(currentRule)) != null){
	          System.out.println("*** Cracked! *** - the password is ["+result+"]");
                  fim = System.currentTimeMillis();
                  dif = fim - inicio;
		  System.out.println("Caching password.");
		  t.cachePwdFound(result);
	          System.exit(0);
	        }
		t.saveState(currentRule);
		currentRule = r.getNextRule();
        }
	System.out.println("Sorry, password not found.");
	System.out.println(search.stringsTested()+" strings tested by dictionary search method.");
        fim = System.currentTimeMillis();
        dif = fim - inicio;
        System.out.println(dif+" msecs ellapsed. Average:"+(dif/search.stringsTested())+" msecs/iteration.");
  }

	// uso: qps <dictfile> <hash> <algorithm>
   public static void main(String args[]) {
   	qps search;
	String tst;
	Task t;
	int alg = -1;
	
	if(args.length != 3){
		System.out.println("uso: qps <dicionario> <hash> <algoritmo>");
		System.exit(-1);
	}
	Rules ruleHandler = new Rules("rules.qp");
	if(args[2].equals("crypt"))
		alg = CRYPT;
	else
		if(args[2].equals("md5"))
			alg = MD5;
	t = new Task(args[1], alg);
	if((tst=t.pwdIsInCache()) != null){
		System.out.println("*** Cache Hit! ***");
        	System.out.println("*** Cracked! *** - the password is ["+tst+"]");
		System.exit(0);
	}
	search = new qps(args[0], t, ruleHandler);
	System.exit(0);
   }
}
