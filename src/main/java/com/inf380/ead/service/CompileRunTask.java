package com.inf380.ead.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.websocket.Session;

import org.apache.commons.io.FileUtils;


public class CompileRunTask implements Runnable {

	private String operation;
	private String sourcesPath, classPath, mainClass;
	private Session webSocketSession;

	public CompileRunTask(){
	}

	public CompileRunTask(String op, String sP,String cP){
		operation=op;
		sourcesPath=sP;
		classPath=cP;
	}

	public CompileRunTask(String op, String sP,String cP, String mC){
		operation=op;
		sourcesPath=sP;
		classPath=cP;
		mainClass=mC;
	}

	/************Getters and Setters***************/


	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getSourcesPath() {
		return sourcesPath;
	}

	public void setSourcesPath(String sourcesPath) {
		this.sourcesPath = sourcesPath;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public void setWebSocketSession(Session webSocketSession) {
		this.webSocketSession = webSocketSession;
	}

	/************Methods***************/

	/**
	 * compile all the java file present in the package sourcesDir
	 */
	public String compile( String sourcesDirPathName, String classOutputDirPathName ) throws IOException {	
		String result = null;
		boolean success=true;
		System.out.println( "Compiling files from "+ sourcesDirPathName+"..." );
		//create bin directory if not exist
		File file = new File(classOutputDirPathName);
		if(!file.exists()){
			FileUtils.deleteDirectory(file);
			file.mkdirs();
		}
		//compile all the java files with the command javac
		//the class files generated are put in the directory classOutputDir
		ArrayList<File> javaFiles=getPkgFiles(sourcesDirPathName);
		for(int i=0; i<javaFiles.size() && success; i++){
			Process p = Runtime.getRuntime()
					.exec( "javac -d "+classOutputDirPathName+" -sourcepath  "+sourcesDirPathName+ " "+javaFiles.get(i).getAbsolutePath() );
			try {
				//wait process end
				p.waitFor();
			} catch( InterruptedException ie ) { System.out.println( ie ); }
			int ret = p.exitValue();
			success=(ret==0);
			if(!success){
				result="Compilation Failed!\n";
				try {
					result += "----compilation error----\n" +getLines(p.getErrorStream());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(success){
			result="Compilation Succeed!";
		}
		return result;
	}


	/**
	 * run the class containing the method main
	 */
	public String run(String mainClassName, String classOutputDirPathName) {
		System.out.println( "Running main from class "+ mainClassName+"..." );
		String result=null;
		try {
			//run the class containing the method main with the command java
			ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp",classOutputDirPathName, mainClassName);
			Process p = processBuilder.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			int i = 0;
			while ((line = br.readLine())!= null) {
				result += line + "\n";
				i++;
				if(i == 100){
					Thread.sleep(2000);
					webSocketSession.getBasicRemote().sendText(result);
					result = "";
					i = 0;
				}
			}

			webSocketSession.getBasicRemote().sendText(result);
			//wait process end
			p.waitFor();
			webSocketSession.getBasicRemote().sendText("Finished");
		} 
		catch( InterruptedException ie ) { System.out.println( ie );} 
		catch (IOException e) {e.printStackTrace();}
		catch (Exception e) {e.printStackTrace();}
		return result;
	}

	/**
	 * compile the java files in the directory sourcesDir
	 * put the generate class in the directory classOutputDir
	 * then execute the class containing the method main
	 */
	public String compileRun(String sourcesDirPathName, String classOutputDirPathName , String mainClass){
		String result = null;
		try {
			result=compile(sourcesDirPathName,classOutputDirPathName);
			if(result.equals("Compilation Succeed!")){
				result=run(mainClass, classOutputDirPathName);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Print the lines in the stream
	 */
	public String getLines(InputStream is) throws Exception {
		String result = "";
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine())!= null) {
			result += line + "\n";
		}
		return result;
	}

	/** 
	 * return the list of java files present in the package
	 */  
	public ArrayList<File> getPkgFiles(String pkgPathName){
		File[] files=new File(pkgPathName).listFiles();
		ArrayList<File> javaFiles=new ArrayList<File>();

		for (File file : files) {
			if(file.isDirectory()){
				javaFiles.addAll(getPkgFiles(file.getAbsolutePath()));
			}else{
				if(file.getName().endsWith(".java")){
					javaFiles.add(file);
				}
			}
		}
		return javaFiles;

	}

	@Override
	public void run() {

		String result="";
		if(operation.equals("compile")){
			try {
				//Attempt to compile the file;
				result= compile(sourcesPath, classPath);
				//send the result (i.e: compilation Succeed or compilation failed + caused)
				webSocketSession.getBasicRemote().sendText(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(operation.equals("run")){

			try {
				//run the compile file of the project
				result= run( mainClass, classPath);
				//send the result
				webSocketSession.getBasicRemote().sendText(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(operation.equals("compilerun")){
			try {
				//Attempt to compile the file;
				result= compile(sourcesPath, classPath);
				//send the result (i.e: compilation Succeed or compilation failed + caused)
				webSocketSession.getBasicRemote().sendText(result);
				if(result.equals("Compilation Succeed!")){
					//run the compile file of the project
					result= run( mainClass, classPath);
					//send the result
					webSocketSession.getBasicRemote().sendText(result);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}

