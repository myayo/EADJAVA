package com.inf380.ead.endpoint;

import java.io.File;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.OnMessage;
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
	public String onMessage(String message) throws Exception{
		System.out.println("receiving message  : "+message);
		String result = "";
		//decode message
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		String action = jsonObject.getString("action");
		String path = jsonObject.getString("path");
		String mainClassName = "";
		switch (action) {
		case "compile":
			result = compileRunService.compile(path, path + "/bin");
			break;
		case "run":
			mainClassName = jsonObject.getString("mainClassPath");
			result = compileRunService.run(path + "/bin", mainClassName);
			break;
		case "compilerun":
			mainClassName = jsonObject.getString("mainClassName");
			mainClassName = mainClassName.substring(path.length() + 1);
			mainClassName = mainClassName.substring(0, mainClassName.indexOf('.'));
			mainClassName.replaceAll("/", ".");
			String username = jsonObject.getString("username");
			String projectPath = Configuration.projectsBaseUrl + username +File.separator+ path;
			result = compileRunService.compileAndRun(projectPath, projectPath + File.separator+"bin", mainClassName);
			break;
		case "stop":
			System.out.println("old action "+action+" going to be stop");
			result = compileRunService.stop();
		}
		System.out.println("Result : "+result);
		return result;
	}
}
