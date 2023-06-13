package prova.testes;

import jdbc.prova.Conexao;

public interface TesteDeConexao {
  public static void main(String[] args) {
	Conexao.getConnection();
	
}
}
