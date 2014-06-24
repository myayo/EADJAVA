package com.inf380.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.inf380.ead.model.Model;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Model model;

	/**
	 * @throws IOException 
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() throws IOException {
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
			HttpSession session = request.getSession();
			session.getAttribute("");
			request.setAttribute("login", "success");
		}else{
			request.setAttribute("login", "failed");
			request.setAttribute("ErrorMsg", "Nom utilisateur ou mot de passe incorrect");
		}
	}

}
