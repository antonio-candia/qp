import md5.*;
import qp.qpAlgorithm;

/**
* Classe que implementa qpAlgorithm para o algoritmo MD5
* atualmente em uso nas senhas criptografadas de sistemas UNIX,
* incluindo as variantes de Linux.
*/
public class qpmd5 implements qpAlgorithm{

	/**
	* O parametro word e criptografado utilizando-se o mesmo salt de target.
	* O metodo retorna true se o resultado da criptografia for igual a target.
	*/
	public boolean testWord(String word, String target){
      		String tstStr = new String(MD5Crypt.crypt(word, target));
	        if(tstStr.equals(target))
	        	return true;
		else
			return false;

	}

	/**
	* Este metodo retorna o nome que sera utilizado pelo sistema para gravar logs,
	* estado de execucao e cache de sucessos em execucoes anteriores.
	*/
	public String algorithmName(){
		return "MD5";
	}
}
