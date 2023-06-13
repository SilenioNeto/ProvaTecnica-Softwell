package prova.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdbc.prova.Conexao;
import prova.model.Usuario;

@WebServlet("/Autenticador")
public class Autenticador extends HttpServlet{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 		
			throws ServletException, IOException {
				Usuario user = new Usuario();		
				String login = request.getParameter("email");
				String senha = request.getParameter("senha");
				user.setPes_email(login);
				user.setPes_senha(senha);
				if(autenticar(user)){
					request.getSession().setAttribute("user", user);
					response.sendRedirect("Home.jsp");
				}else{
					request.setAttribute("erro", "Usuário ou Senha Inválidos!");
				RequestDispatcher dispatcher = request.getRequestDispatcher("Login.jsp");
					dispatcher.forward(request, response);
				}
			}
	private boolean autenticar(Usuario user) {
		   boolean autenticado = false;		
		   Connection con = Conexao.getConnection();
		   String sql = "select *  " +  
		"from prv_pessoa  " +  
		           "where " +  "pes_email = '" + user.getPes_email().trim() +  "' and " + "pes_senha = '" + user.getPes_senha().trim() + "';" ;
		   try {
			Statement stmt = con.createStatement();		
			ResultSet resultSet = stmt.executeQuery(sql);
			if (resultSet.next()) {	
				user.setPes_nome(resultSet.getString("pes_nome"));
				autenticado = true; 	
		}	
			resultSet.close();
			stmt.close();		
		   }catch (SQLException e) {
			e.printStackTrace();
		   }
		   return autenticado;		
		}
}
