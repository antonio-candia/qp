import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.sql.Time;

import qp.*;
//import qp.plugins.*;

class qpsbfr{
  private static char[] charSet = {'0','1','2','3','4','5','6','7','8','9',
			'a','b','c','d','e','f','g','h','i','j',
			'k','l','m','n','o','p','q','r','s','t',
			'u','v','w','x','y','z','A','B','C','D',
			'E','F','G','H','I','J','K','L','M','N',
			'O','P','Q','R','S','T','U','V','W','X',
			'Y','Z','.','?','!','@','#','$','%','&',
			'*','(',')','-','_','=','+','[',']','{',
			'}','\\','/','|',':',';',','};
//  static Benchmark b;

  public static void main(String[] args) {
	if(args.length != 5){
		System.out.println("uso: qpsbr <hash> <algoritmo> <larguradepalavra> <string inicial> <string final>");
		System.exit(-1);
	}
	qpAlgorithm qpAlg = (qpAlgorithm) createObject("qp"+args[1]);
	System.out.println(qpAlg.algorithmName());
	Task t;
	t = new Task(args[0], qpAlg);
	String tst;
/*	if((tst=t.pwdIsInCache()) != null){
		System.out.println("*** Cache Hit! ***");
        	System.out.println("*** Cracked! *** - the password is ["+tst+"]");
		System.exit(0);
	}
*/
	new qpsbfr(t, new Integer(args[2]).intValue(), new String(args[3]), new String(args[4]));
  }
  
  public qpsbfr(Task t, int width, String start, String end){
	Search search = new Search(t);
	long inicio, fim, dif;
        inicio = System.currentTimeMillis();
//	String currentRule = t.loadState();

	System.out.println("Hash: ["+t.getCurrentTarget()+"] Algorithm: ["+t.getCurrentAlgorithmName()+"]");
	String result;
//	b = new Benchmark();
//	Timer timer = new Timer();
//	timer.scheduleAtFixedRate(new printStats(), (long) 0, (long) 1000);
//	b.start(search);
	
	if((result = search.byCharacterSet(charSet, width, start, end)) != null){
	          System.out.println("*** Cracked! *** - the password is ["+result+"]");
                  fim = System.currentTimeMillis();
                  dif = fim - inicio;
	          System.out.println(search.stringsTested()+" strings tested by dictionary search method.");
	          System.out.println(new Double(dif/1000).floatValue()+" secs ellapsed. Average:"+(dif/search.stringsTested())+" msecs/iteration.");
		  System.out.println("Caching password.");
//		  t.cachePwdFound(result);
//		  b.stop();
	          System.exit(0);
        }
//	timer.cancel();
//	b.stop();
	System.out.println("Sorry, password not found.");
	System.out.println(search.stringsTested()+" strings tested by brute-force search method.");
        fim = System.currentTimeMillis();
        dif = fim - inicio;
        System.out.println(new Time(dif).toString()+" ellapsed. Average:"+(dif/search.stringsTested())+" msecs/iteration.");

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
