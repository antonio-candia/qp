import java.io.*;
import qp.qpAlgorithm;

public class qpnativerijndael implements qpAlgorithm, Serializable{

	public qpnativerijndael(){ }

	public native boolean testWord(String word, String target);
	
	public String algorithmName(){
		return "nativerijndael";
	}
	
	static { System.loadLibrary("qpnativerijndaelImp"); }
}
