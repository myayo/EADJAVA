package com.inf380.ead.endpoint;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.inf380.ead.config.Configuration;
import com.inf380.ead.service.FileService;

/**
 * Class that represent a websocket server connexion for file Management
 * it's open the connexion, receive mesage from javascript websocket
 * and send back Json message.
 * @author myayo
 */
@ServerEndpoint("/fileEndPoint")
public class FileEndPoint {

	/**
	 * Receive message from websocket client
	 * @param message : Json message receive from javascript websocket client <br/>
	 * 				     {action : the action that should br done i.e('createFile' or 'getProject' or 'loadProjectFile' or 'removeFile') <br/>
	 * 			  		 path : 'source file path'
	 * 			  		 src: 'file content (empty for folder)'
	 * 					 username: 'the username'}
	 * @param session : The session of the websocket connexion, it is use to send response
	 * @throws Exception
	 */
	@OnMessage
	public String onMessage(String message) throws IOException{
		System.out.println("receiving message  : "+message);
		String result = "";
		FileService fileService = new FileService();
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		String action = jsonObject.getString("action");
		String path = "";
		String username = jsonObject.getString("username");
		switch (action) {
		case "createFile":
			path = jsonObject.getString("path");
			String type = jsonObject.getString("type");
			String src = null;
			if(type.equals("file")){
				src = jsonObject.getString("src");
			}
			boolean success = fileService.createOrUpdateFile(path, type, src, username);
			result =  success ? "sucess" : "error";
			break;
		case "getProject":
			//get the file
			List<String> projectsName = fileService.getProjects(username);
			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			for (String project : projectsName) {
				arrayBuilder.add(project);
			}
			JsonObject obj = Json.createObjectBuilder().add("action", action).add("projects", arrayBuilder).build();
			result = obj.toString();
			break;
		case "loadProjectFile":
			//get the file tree;
			path = jsonObject.getString("path");
			JsonObject fileTree = Json.createObjectBuilder().add("action", action)
					.add("files", fileService.getFileTree(Configuration.projectsBaseUrl+ username + File.separator + path, username)).build();
			result = fileTree.toString();
			break;

		case "removeFile" :
			path = jsonObject.getString("path");
			fileService.deleteFile(Configuration.projectsBaseUrl + username + File.separator + path);
			break;
		default:
			break;
		}
		System.out.println("result " + result);
		return result;
	}

}
