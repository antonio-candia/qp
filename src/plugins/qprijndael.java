import java.rmi.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import qp.qpAlgorithm;

public class qprijndael implements qpAlgorithm, Serializable, Remote{
	public qprijndael(){
		// ???
	}

	public boolean testWord(String word, String target){
		try{
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
       		kgen.init(128); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
       		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	       	// Instantiate the cipher
	       	Cipher cipher = Cipher.getInstance("AES");

       		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] encrypted =
	         cipher.doFinal(("This is just an example").getBytes());
		String tstStr = new String(encrypted);
	        if(tstStr.equals(target))
	        	return true;
		else
			return false;
		}catch (NoSuchAlgorithmException e){
			System.out.println(e);
		}catch (NoSuchPaddingException e){
			System.out.println(e);
		}catch (InvalidKeyException e){
			System.out.println(e);
		}catch (IllegalBlockSizeException e){
			System.out.println(e);
		}catch (BadPaddingException e){
			System.out.println(e);
		}
		return false;

	}

	public String algorithmName(){
		return "Rijndael (AES)";
	}
}
