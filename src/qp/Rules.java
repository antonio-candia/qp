package qp;

import java.io.*;
import java.util.*;

/**
* Esta classe contem os metodos que manipulam o arquivo de regras do sistema.
* Tambem e esta classe que contem o metodo que aplica uma regra sobre uma
* palavra do dicionario.
*
* @author Antonio Marcos Candia
*/
public class Rules implements Serializable{
  private LinkedList<String> rulesList;

  public Rules(String rulesFilename){
	rulesList = new LinkedList<String>();
	try{
      	File rules = new File(rulesFilename);
		if(rules.exists() && rules.isFile()){
        		BufferedReader rulesBr = null;
	      		FileReader rulesFr = new FileReader(rules);
			rulesBr = new BufferedReader(rulesFr);
			String line;
			while ((line = rulesBr.readLine()) != null)
				if(line.charAt(0) != '#')
					rulesList.add(line);
		}
	}
	catch(IOException e) {
		System.err.println("Problem: " + e);
	} 
  	return;
  }

  public Rules(){
    //do nothing, if it's a non-rulesfile search
  }

  public String getNextRule( ) throws NoSuchElementException {
	if(rulesList.size() > 0)
	  	return rulesList.removeFirst().toString();
	else
		return null;
  }

  public String getCurrentRule( )throws NoSuchElementException {
	return rulesList.getFirst().toString();
  }

  public int size(){
	return rulesList.size();
  }

//removes from the rules list all the rules already used
  public void skipToRule(String rule) throws NoSuchElementException{
	String tst;
	try{
		for(int i = 0; i < rulesList.size(); i++){
			tst = rulesList.get(i).toString();
			if(tst.compareTo(rule) == 0){
				tst = rulesList.removeFirst().toString();
				while(!tst.equals(rule))
					tst = rulesList.removeFirst().toString();
				break;
			}
		}
	}catch(RuntimeException endOfList){
		//do nothing if the list ends without a match	
	}
  }
   
/**
* Este metodo aplica diretamente a regra corrente sobre uma palavra.
* @returns String contendo a palavra modificada.
*/ 
  public String applyCurrentRule(String word){
    String rule = rulesList.getFirst().toString();
    return transmogrify(word, rule);
  }

/**
* Este metodo modifica uma palavra de acordo com uma regra.
*/ 
  public static String transmogrify(String word, String rule){
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
}
