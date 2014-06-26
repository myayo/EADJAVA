package com.inf380.ead.endpoint;

import java.io.File;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.inf380.ead.config.Configuration;
import com.inf380.ead.service.CompileRunService;

@ServerEndpoint("/compileRunEndpoint")
public class CompileRunEndpoint {


	private static CompileRunService compileRunService=new CompileRunService();

	/**
	 * message = {action : 'run' ou 'compile' ou 'compilerun'
	 * 			  path : 'chemin vers le dossier des fichiers sources'
	 * 			  mainClassPath: 'chemin vers le fichier main'}
	 * @throws Exception 
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws Exception{
		System.out.println("receiving message  : "+message);
		//decode message
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		String action = jsonObject.getString("action");
		String path = "";
		String mainClassName = "";
		compileRunService.setWebSocketSession(session);
		switch (action) {
		case "compile":
			path = jsonObject.getString("path");
			compileRunService.compile(path, path + File.separator + "bin");
			break;
		case "run":
			path = jsonObject.getString("path");
			mainClassName = jsonObject.getString("mainClassPath");
			compileRunService.run(path +  File.separator +"bin", mainClassName);
			break;
		case "compilerun":
			path = jsonObject.getString("path");
			mainClassName = jsonObject.getString("mainClassName");
			mainClassName = mainClassName.substring(path.length() + 1);
			mainClassName = mainClassName.substring(0, mainClassName.indexOf('.'));
			String username = jsonObject.getString("username");
			String projectPath = Configuration.projectsBaseUrl + username +File.separator+ path;
			compileRunService.compileAndRun(projectPath, projectPath + File.separator+"bin", mainClassName);
			break;
		case "stop":
			System.out.println("old action "+action+" going to be stop");
			compileRunService.stop();
			break;
		}
	}
}
