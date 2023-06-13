package prova.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdbc.prova.Conexao;
import prova.model.Usuario;

@WebServlet("/Cadastro")
public class Cadastrar extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static String validacao_email_ok = null;
	private static String validacao_email_erro_vazio = "Email vazio";
	private static String validacao_email_erro_invalido = "Email invalido";
	private static String validacao_senha_ok = null;
	private static String validacao_senha_erro_vazia = "Senha vazia";
	private static String validacao_senha_erro_UpperCase = "A senha precisa de 1 caracter maiusculo";
	private static String validacao_senha_erro_igual_login = "Senha não pode ser igual ao login";
	private static String validacao_senha_erro_tamanho = "A senha deve ter no minimo 4 caracteres e no maximo 8";
	private static String validacao_senha_erro_diferente = "As senhas não são iguais";
	private static String validacao_cadastro_ok = null;
	private static String validacao_cadastro_erro_banco ="Erro ao fazer a conexão com o Banco de dados";
	private static String validacao_cadastro_erro_generico = "Erro ao cadastrar usuario";
	private static String validacao_nome_ok= null;
	private static String validacao_nome_erro_vazio = "Nome vazio";
	private static String validacao_senha_erro_especiais = "A senha precisa de caracteres especiais";
	protected void doPost(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException{
		
		Date date = Calendar.getInstance().getTime();
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		String data = df.format(date);
		Usuario user = new Usuario();
		String erro;
		
		String nome = request.getParameter("nome");
		String email = request.getParameter("email");
		String senha1 = request.getParameter("senha1");
		String senha2 = request.getParameter("senha2");
		String celular = request.getParameter("celular");
		String sexo = request.getParameter("sexo");
		if(nome == null ) {
			enviarErro(validacao_nome_erro_vazio, request, response);
			return;
		}else if (email == null) {
			enviarErro(validacao_email_erro_vazio, request, response);
			return;
		}else if (senha1 == null || senha2 == null) {
			enviarErro(validacao_senha_erro_diferente, request, response);
			return;
		}
		sexo = sexo.trim();
		email = email.trim();
		senha1= senha1.trim();
		senha2 = senha2.trim();
		nome = nome.trim();
		
		user.setPes_nome(nome);
		user.setPes_email(email);
		user.setPes_celular(celular);
		user.setPes_senha(senha1);
		user.setPes_sexo(sexo);
		user.setPes_data_cadastro(data);
		try {
			erro = validarLogin(nome);
			if(erro != validacao_nome_ok) {
				throw new Exception(erro);
			}
			erro = validarEmail(email);
			if(erro != validacao_email_ok) {
				throw new Exception(erro);
			}
			erro = validarSenha(senha1,senha2,nome);
			if(erro != validacao_senha_ok) {
				throw new Exception(erro);
			}
		}catch(Exception e) {
			enviarErro(e.getMessage(), request, response);
			return;
		}
		erro = cadastrar(user);
		if(erro == validacao_cadastro_ok) {
			request.getSession().setAttribute("user", user);
			response.sendRedirect("Home.jsp");
		}else 
			enviarErro(erro, request, response);
	}
	
	private String validarLogin(String login) {
		if (login.isBlank())
			return validacao_nome_erro_vazio;

		return validacao_nome_ok;
	}
	
	private String validarEmail(String email) {
		if (email.isBlank())
			return validacao_email_erro_vazio;

		int atIndex = email.indexOf('@');
		if (atIndex == -1)
			return validacao_email_erro_invalido;

		int dotIndex = email.substring(atIndex).indexOf('.');
		if (dotIndex == -1)
			return validacao_email_erro_invalido;

		return validacao_email_ok;
	}
	private String validarSenha(String senha1, String senha2, String login) {
		if (!senha1.equals(senha2))
			return validacao_senha_erro_diferente;

		if (senha1.isBlank())
			return validacao_senha_erro_vazia;

		if (senha1.toLowerCase().equals(login.toLowerCase()))
			return validacao_senha_erro_igual_login;

		if (senha1.length() < 4 || senha1.length() > 8)
			return validacao_senha_erro_tamanho;
		boolean uppercase=false;
		char[] chars = senha1.toCharArray();
		for(int i = 0; i<senha1.length(); i++) {
			for(int ascii =65; ascii < 91; ascii++) {
				if(chars[i]==ascii) {
					uppercase = true;
				}
			}
		}
		if(uppercase ==false) {
			return validacao_senha_erro_UpperCase;
		}
		boolean especial=false;
		char[] charsEspecial = senha1.toCharArray();
		for(int i = 0; i<senha1.length(); i++) {
			for(int ascii =33; ascii < 65; ascii++) {
				if(charsEspecial[i]==ascii) {
					especial = true;
				}
			}
		}
		if(especial ==false) {
			return validacao_senha_erro_especiais;
		}
		return validacao_senha_ok;
	}
	private String cadastrar(Usuario user) {
		String result = validacao_cadastro_erro_generico;

		Connection con = Conexao.getConnection();
		if (con == null)
			return validacao_cadastro_erro_banco;

		String sql = "INSERT INTO prv_pessoa (pes_nome, pes_email, pes_senha, pes_celular, pes_data_cadastro, pes_sexo ) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');";
		sql = String.format(sql, user.getPes_nome(), user.getPes_email(), user.getPes_senha(), user.getPes_celular(), user.getPes_data_cadastro(), user.getPes_sexo());

		try {
			Statement stmt = con.createStatement();
			int resultSet = stmt.executeUpdate(sql);
			if (resultSet != 0) {
				result = validacao_cadastro_ok;
			}

			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result = validacao_cadastro_erro_generico;
		}

		return result;
	}
	private void enviarErro(String erro, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("erro", erro);
		RequestDispatcher dispatcher = request.getRequestDispatcher("Cadastro.jsp");
		dispatcher.forward(request, response);
	}
}
