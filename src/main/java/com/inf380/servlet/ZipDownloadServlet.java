package com.inf380.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inf380.ead.config.Configuration;
import com.inf380.ead.service.ZipService;

/**
 * Servlet implementation class ZipDownloadServlet
 */
public class ZipDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ZipService zipService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ZipDownloadServlet() {
        super();
        zipService = new ZipService();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("user");
		String path = request.getParameter("path");
		String projectPath = Configuration.projectsBaseUrl + username + File.separator + path;
		byte[] zip = zipService.createZip(projectPath);
		ServletOutputStream sos = response.getOutputStream();
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename="+path+".zip");
        sos.write(zip);
        sos.flush();
	}

}
