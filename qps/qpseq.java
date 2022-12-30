import java.io.*;
import java.util.*;
import md5.*;


public class qpseq {
  //defines needed
  static String algorithms[] = {"crypt", "md5"};
  static int CRYPT=0;
  static int MD5=1;
  
  
  public qpseq(String dictFile, String target, int algorithm) {

    String rulesFilename = "rules.qp";
    String result, tstStr;
    double inicio, fim, dif;

    try {
      File file = new File(dictFile);
      File rules = new File(rulesFilename);
      if (file.exists() && file.isFile() && rules.exists() && rules.isFile()) {
        long lastModified = file.lastModified();
        System.out.println("Dictionary file: "+dictFile);
        System.out.println("Date  : " + new Date(lastModified));
        System.out.println("-----");
	lastModified = rules.lastModified();
        System.out.println("Rules file: "+rulesFilename);
        System.out.println("Date  : " + new Date(lastModified));
        System.out.println("-----");

	 // processamento do dicionario a partir das regras
        double cont = 0;
        BufferedReader br = null;
        BufferedReader rulesBr = null;
        FileReader rulesFr = new FileReader(rules);
        rulesBr = new BufferedReader(rulesFr);
	 
        String word;
        String currentRule;
        inicio = System.currentTimeMillis();
        while ((currentRule = rulesBr.readLine()) != null) {
          FileReader fr = new FileReader(file);
          br = new BufferedReader(fr);
          System.out.println("Current rule: "+currentRule);
	  if(currentRule.charAt(0) != '#')
            while ((word = br.readLine()) != null) {
	      cont++;
              //geracao do dicionario modificado
	      result = new String (applyRule(currentRule, word));

	      //teste da palavra gerada
	      switch(algorithm){
	        case 0: //crypt
	      		tstStr = new String(Crypt.crypt(target, new String(result)));
			break;
	        case 1: //md5
	      		tstStr = new String(MD5Crypt.crypt(new String(result), target));
	      		break;
	        default: //no such algorithm
			tstStr = "nothing";
	      		break;
	      }
	      
	      if(tstStr.equals(target)){
	        System.out.println("*** Cracked! *** - the password is ["+result+"]");
	        System.out.println(cont+" strings tested by dictionary search method.");
                fim = System.currentTimeMillis();
                dif = fim - inicio;
                System.out.println(dif+" msecs ellapsed. Average:"+(dif/cont)+" msecs/iteration.");
		System.out.println("Caching password.");
		cachePwdFound(result, target, algorithm);
	        System.exit(0);
	      }
	    }
	  qpSaveState(algorithm ,target, currentRule);
	  br.close();
        }
	System.out.println("Sorry, password not found.");
	System.out.println(cont+" strings tested by dictionary search method.");
        fim = System.currentTimeMillis();
        dif = fim - inicio;
        System.out.println(dif+" msecs ellapsed. Average:"+(dif/cont)+" msecs/iteration.");
      }
    } catch (IOException e) {
       System.err.println("Problem: " + e);
    } 
  }
   
  static String pwdIsInCache(String hash, int type){
    String cacheFilename = "cache.qp";
    try {
      RandomAccessFile cache = new RandomAccessFile(cacheFilename, "rw");
      String line;
      while((line=cache.readLine()) != null)
        if(algorithms[type].equals(new String(line.substring(0,line.indexOf('\t')))))
          if(hash.equals(line.substring(line.indexOf('\t')+1, line.lastIndexOf('\t'))))
	    return line.substring(line.lastIndexOf('\t')+1, line.length());

      cache.close();
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
    return null;
  }

  static void cachePwdFound(String passwd, String hash, int type){
    String cacheFilename = "cache.qp";
    try {
      RandomAccessFile cache = new RandomAccessFile(cacheFilename, "rw");
      cache.seek(cache.length());
      cache.writeBytes(algorithms[type]+'\t'+hash+'\t'+passwd+'\n');
      cache.close();
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
  }

  static void qpSaveState(int type, String hash, String rule){
    String cacheFilename = "state.qp";
    try {
	RandomAccessFile cache = new RandomAccessFile(cacheFilename, "rw");
	String line;
	while((line=cache.readLine()) != null)
		if(algorithms[type].equals(new String(line.substring(0,line.indexOf('\t')))))
			if(hash.equals(line.substring(line.indexOf('\t')+1, line.lastIndexOf('\t')))){
			//se já houve um "checkpoint" anterior já existe uma linha no arquivo de estado 
			//para esta senha, então somente deve ser atualizada.
			System.out.println(cache.getFilePointer()+"  "+line.length());
			cache.seek(cache.getFilePointer() - line.length() - 1);
			cache.writeBytes(algorithms[type]+'\t'+hash+'\t'+rule+'\n');
			cache.close();
			return;
			}
	cache.seek(cache.length());
	cache.writeBytes(algorithms[type]+'\t'+hash+'\t'+rule+'\n');
	cache.close();
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
  }


  String applyRule(String rule, String word){
    StringBuffer sbrule = new StringBuffer(rule);
    StringBuffer sbword = new StringBuffer(word);
    StringBuffer sbtemp = new StringBuffer(word);

    for(int cur=0; cur < rule.length(); cur++){
      switch(rule.charAt(cur)){
        case ':': //do nothing to the word
		break;
	case 'c': //capitalise: capitalises the first character only
		sbtemp = new StringBuffer(sbword.toString().toUpperCase());
		sbword = new StringBuffer(sbword.toString().toLowerCase());
		sbword.setCharAt(0, sbtemp.charAt(0));
		break;
	case 'C': //ncapitalise: capitalises the word and forces any other letters to lowercase
		sbtemp = new StringBuffer(sbword.toString().toLowerCase());
		sbword = new StringBuffer(sbword.toString().toUpperCase());
		sbword.setCharAt(0, sbtemp.charAt(0));
		break;
	case 'd': //duplicate the word
		sbword = new StringBuffer(sbword.toString()+sbword.toString());
		break;
	case 'f': //reflect: appends a reversed copy of the word
		sbword = new StringBuffer(sbword.toString()+sbword.reverse().toString());
		break;
	case 'i': //iNX inserts the character X at position N
		try{
		  sbword.insert(Character.digit(rule.charAt(cur+1), 16), rule.charAt(cur+2));
		} catch(RuntimeException ignore){
		  /* if the insertion cannot be done, do nothing */
		}
		cur+=2;
		break;
	case 'l': //forces any letters of the word to lowercase
		sbword = new StringBuffer(sbword.toString().toLowerCase());
		break;
	case 'o': //oNX overstrikes the character at position N with X
		try{
		  sbword.setCharAt(Character.digit(rule.charAt(cur+1), 16), rule.charAt(cur+2));
		} catch(RuntimeException ignore){
		  /* if the overstriking cannot be done, do nothing */
		}
		cur+=2;
		break;
	case 'r': //reverses the word
		sbword = sbword.reverse();
		break;
	case 's': //sXY replaces all instances of X with character Y
		sbword = new StringBuffer(sbword.toString().replace(rule.charAt(cur+1),rule.charAt(cur+2)));
		cur+=2;
		break;
	case 'u': //forces any letters of the word to uppercase
		sbword = new StringBuffer(sbword.toString().toUpperCase());
		break;
	case '$': //appends next character to the word
		cur++;
		sbword = new StringBuffer(sbword.toString()+rule.charAt(cur));
		break;
	case '^': //prepends next character to the word
		cur++;
		sbword = new StringBuffer(rule.charAt(cur)+sbword.toString());
		break;
	case '[': //deletes the first character from the word
		sbword.deleteCharAt(0);
		break;
	case ']': //deletes the last character from the word
		sbword.deleteCharAt(sbword.length()-1);
		break;
	case '@': //purges all instances of the character from the word
		cur++;
		int i=0;
		String temp = new String(rule.charAt(cur)+"");
		while((i=sbword.indexOf(temp)) != -1)
		  sbword.deleteCharAt(i);
		break;
	case '\'': // 'N truncates the word at N
		cur++;
		sbword.setLength(Character.digit(rule.charAt(cur), 16));
		break;
	default: System.err.println("Warning: non-existant rule -> "+rule.charAt(cur));
		break;
      }
    }
    
    return sbword.toString();
  }

/*
  void multipleDictFilesRead(void) {
    File myDir = new File("dicts/"); // or any required directory pathname
    File[] files = myDir.listFiles();
    // can also use a FileFilter
    for (int i=0 ; i < files.length ; i++) {
      try {
      FileInputStream in = new FileInputStream(files[i]);
      // Do here your reading and handling
      in.close();
      }
      catch (IOException e)
      {
        System.err.println("Could not read file: " + files[i]);
        e.printStackTrace();
      }
    }
  }
*/


   public static void main(String args[]) {
   	qpseq search;
	String tst;
	if(args[2].equals("crypt")){
		if((tst=pwdIsInCache(args[1], CRYPT)) != null){
			System.out.println("*** Cache Hit! ***");
	        	System.out.println("*** Cracked! *** - the password is ["+tst+"]");
			System.exit(0);
		}
		search = new qpseq(args[0], args[1], CRYPT);
	}
	if(args[2].equals("md5")){
		if((tst=pwdIsInCache(args[1], MD5)) != null){
			System.out.println("*** Cache Hit! ***");
	        	System.out.println("*** Cracked! *** - the password is ["+tst+"]");
			System.exit(0);
		}
		search = new qpseq(args[0], args[1], MD5);
	}
	System.exit(0);
   }
}
