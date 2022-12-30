import java.io.*;

public class qpjavacrypto implements qpAlgorithm, Serializable{
	public qpjavacrypto(){
		// ???
	}

	public boolean testWord(String word, String target){

		StringEncrypter desEncrypter = new StringEncrypter("aaaa");
		String tstenc = desEncrypter.encrypt("AC");
	      	String tstStr = desEncrypter.decrypt(target);
		System.out.println(target+":"+tstenc+":"+tstStr);
	        FrequencyAnalysis fc = new FrequencyAnalysis();
		float freqTab[][] = new float[256][256];
		float freqTab2[][] = new float[256][256];
		freqTab = fc.FrequencyCount(tstStr);
		freqTab2 = fc.LoadFrequencyTable("freqtab");
		double loglnorm = fc.logLikelihood(freqTab2, freqTab,tstStr.length()) / (tstStr.length()-1);
	        if(loglnorm > 3.5)
	        	return true;
		else
			return false;

	}


	public String algorithmName(){
		return "javacrypto";
	}
}
