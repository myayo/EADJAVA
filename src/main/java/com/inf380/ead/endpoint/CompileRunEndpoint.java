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

/**
 * Class that represent a websocket server connexion the compilation task
 * it's open the connexion, receive mesage from javascript websocket
 * and send back Text message.
 * @author myayo
 *
 */
@ServerEndpoint("/compileRunEndpoint")
public class CompileRunEndpoint {


	/**
	 * Compiler object to execute or compile program
	 */
	private final CompileRunService compileRunService = new CompileRunService();


	/**
	 * Receive message from websocket client
	 * @param message : Json message receive from javascript websocket client <br/>
	 * 				     {action : the action that should br done i.e('run' or 'compile' or 'compilerun' or 'stop') <br/>
	 * 			  		 path : 'source file path'
	 * 			  		 mainClassPath: 'main class path'}
	 * @param session : The session of the websocket connexion, it is use to send response
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
			//get the class name i.e remove absolute path
			mainClassName = mainClassName.substring(path.length() + 1);
			mainClassName = mainClassName.substring(0, mainClassName.indexOf('.'));

			String username = jsonObject.getString("username");
			String projectPath = Configuration.projectsBaseUrl + username +File.separator+ path;
			compileRunService.compileAndRun(projectPath, projectPath + File.separator+"bin", mainClassName);
			break;
		case "stop":
			compileRunService.stop();
			break;
		}
	}
}
