package jdbc.prova;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
	public static Connection getConnection() {
		Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/prova_jr","postgres","surf123");
			System.out.println("Conectado!");
		}catch(SQLException e){
			System.out.println("Erro - Conexao" + e);
		} catch (ClassNotFoundException e) {
			System.out.println("Erro - Driver" + e);
		}
		return con;
	}
}
