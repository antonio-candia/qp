import java.io.*;
import qp.qpAlgorithm;

public class qpnativeaes implements qpAlgorithm, Serializable{

	public qpnativeaes(){ }

	public native boolean testWord(String word, String target);
	
	public String algorithmName(){
		return "nativeaes";
	}
	
	static { System.loadLibrary("qpnativeaesImp"); }
}
