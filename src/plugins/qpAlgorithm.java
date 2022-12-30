import java.io.*;
/*
* interface a ser utilizada para generalizacao das classes
* de algoritmos de criptografia para uso no sistema.
*/
public interface qpAlgorithm extends Serializable{
	//método que deve testar uma palavra. Deve retornar true se
	//word decripta target com sucesso e false caso contrario
	public boolean testWord(String word, String target);
	
	//método que retorna o nome do algoritmo em questão
	public String algorithmName();

}
