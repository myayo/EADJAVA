package com.inf380.ead.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.apache.commons.io.FileUtils;

import com.inf380.ead.config.Configuration;

/**
 * Class that manage file Operation
 */
public class FileService {


	/**
	 * Create a file or a directory and store it to 
	 * the server fileSystem
	 * @param pathname : the relativePath of the file or the directory
	 * @param type : the type i.e (File or directory)
	 * @param content: the content of the file
	 * @throws IOException 
	 */
	public boolean createOrUpdateFile(String pathname, String type, String content, String username) throws IOException{
		boolean success = false;
		File file= new File(Configuration.projectsBaseUrl + username + "/" +pathname);
		//create or save directory
		if(type.equals("directory")){
			if(!file.exists()){
				success =  file.mkdirs();
			}
		}else if(type.equals("file")){
			//create File
			if(!file.exists()){
				file.getParentFile().mkdirs();
				success = file.createNewFile();
			}else{
				success = true;
			}
			if (success) {
				//add content
				FileWriter writer = new FileWriter(file);
				writer.write(content);
				writer.flush();
				writer.close();
			}
		}
		return success;
	}

	/**
	 * Delete file or directory according to his pathname
	 * @param pathname : the relativePath of the file or the directory
	 * @throws IOException
	 */
	public void deleteFile(String pathname) throws IOException{
		File file= new File(pathname);
		if(file.isDirectory()){
			FileUtils.deleteDirectory(file);
		}else{
			file.delete();
		}
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	public List<String> getProjects(String username) {
		List<String> projects = new ArrayList<>();
		System.out.println("Project base Url " +Configuration.projectsBaseUrl);
		File userFolder = new File(Configuration.projectsBaseUrl+ username);
		if(userFolder.exists() && userFolder.isDirectory()){
			for (File file : userFolder.listFiles()) {
				if(file.isDirectory()){
					projects.add(file.getName());
				}
			}
		}
		return projects;
	}

	public JsonObjectBuilder getFileTree(String dir,String username) throws IOException{
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
		File file = new File(dir);
		if(file.exists()){
			objectBuilder.add("label", file.getName());
			objectBuilder.add("path", file.getAbsolutePath().substring((Configuration.projectsBaseUrl+username).length() + 1));
			if(file.isDirectory()){
				//build children
				JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
				for (File ChildrenFile : file.listFiles()) {
					if(!ChildrenFile.getName().equalsIgnoreCase("bin"))
						arrayBuilder.add(getFileTree(ChildrenFile.getAbsolutePath(), username));
				}
				objectBuilder.add("children", arrayBuilder);
			}else{
				//get content
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String lu = null;
				String content = "";
				while((lu = reader.readLine())!= null){
					content += lu +"\n"; 
				}
				reader.close();
				objectBuilder.add("src", content);
			}
		}
		return objectBuilder;
	}
}


