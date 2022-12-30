import java.rmi.*;
import java.io.*;
import qp.qpAlgorithm;

public class qpjcrypt implements qpAlgorithm, Serializable, Remote{
	public qpjcrypt(){
		// ???
	}

	public boolean testWord(String word, String target){
	      	String tstStr = new String(JCrypt.crypt(target, word));
	        if(tstStr.equals(target))
	        	return true;
		else
			return false;

	}

	public String algorithmName(){
		return "jcrypt";
	}
}
