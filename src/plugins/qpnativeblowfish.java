import java.io.*;
import qp.qpAlgorithm;

public class qpnativeblowfish implements qpAlgorithm, Serializable{

	public qpnativeblowfish(){ }

	public native boolean testWord(String word, String target);
	
	public String algorithmName(){
		return "nativeblowfish";
	}
	
	static { System.loadLibrary("qpnativeblowfishImp"); }
}
