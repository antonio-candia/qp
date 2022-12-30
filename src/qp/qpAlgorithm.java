package qp;

import java.io.*;
/*
* interface a ser utilizada para generalizacao das classes
* de algoritmos de criptografia para uso no sistema.
*/
public interface qpAlgorithm extends Serializable{
	//metodo que deve testar uma palavra. Deve retornar true se
	//word decripta target com sucesso e false caso contrario
	public boolean testWord(String word, String target);
	
	//metodo que retorna o nome do algoritmo em questao
	public String algorithmName();

}
