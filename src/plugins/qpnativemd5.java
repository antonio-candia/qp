public class qpnativecrypt implements qpAlgorithm{

	public qpnativecrypt(){ }

	public native boolean testWord(String word, String target);
	
	public String algorithmName(){
		return "nativecrypt";
	}
	
	static {
    		System.loadLibrary("qpnativecryptImp");
  }
}