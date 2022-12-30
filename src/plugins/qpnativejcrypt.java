import java.io.*;

public class qpnativejcrypt implements qpAlgorithm, Serializable{

	public qpnativejcrypt(){ }

	public native boolean testWord(String word, String target);
	
	public String algorithmName(){
		return "nativejcrypt";
	}
	
	static { System.loadLibrary("qpnativejcryptImp"); }
}
