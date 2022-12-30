import java.rmi.*;
import java.io.*;
import qp.qpAlgorithm;

public class qpcrypt implements qpAlgorithm, Serializable, Remote{
	public qpcrypt(){
		// ???
	}

	public boolean testWord(String word, String target){
	      	String tstStr = new String(Crypt.crypt(target, word));
	        if(tstStr.equals(target))
	        	return true;
		else
			return false;

	}

	public String algorithmName(){
		return "crypt";
	}
}
