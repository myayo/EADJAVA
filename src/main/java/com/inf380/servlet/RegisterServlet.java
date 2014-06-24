package com.inf380.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inf380.ead.model.Model;

/**
 * Servlet implementation class LoginServlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Model model;

	/**
	 * @throws IOException 
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterServlet() throws IOException {
		super();
		model = new Model();
		model.init();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		boolean userExist = model.log(username, password);
		if(userExist){
			request.setAttribute("ErrorMsg", "Nom utilisateur déjà utilisé");
		}else{
			//rediriger vers la page de l'editeur
		}
	}

}
