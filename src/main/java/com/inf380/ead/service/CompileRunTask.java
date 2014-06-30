package com.inf380.ead.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.websocket.Session;

import org.apache.commons.io.FileUtils;

/**
 * Thread to execute compile or run operation
 *
 */
public class CompileRunTask implements Runnable {

	/**
	 * The operation to execute could be 'run', 'compilerun', 'compile'
	 */
	private String operation;

	/**
	 * The path to the code source
	 */
	private String sourcesPath;
	/**
	 * The directory of the compile program
	 */
	private String binPath;
	/**
	 * The main class to execute
	 */
	private String mainClass;
	/**
	 * The session to send message to the client
	 */
	private Session webSocketSession;

	/**
	 * Constructor
	 */
	public CompileRunTask(){
	}

	/**
	 * Constructor
	 * @param operation : the operation to execute
	 * @param sourcePath : the code source directory
	 * @param binPath : the compile program directory
	 */
	public CompileRunTask(String operation, String sourcePath,String binPath){
		this.operation=operation;
		this.sourcesPath=sourcePath;
		this.binPath=binPath;
	}

	/**
	 * Constructor
	 * @param operation : the operation to execute
	 * @param sourcePath : the code source directory
	 * @param binPath : the compile program directory
	 * @param mainClass : the main class to execute
	 */
	public CompileRunTask(String operation, String sourcePath,String binPath , String mainClass){
		this(operation, sourcePath, binPath);
		this.mainClass=mainClass;
	}

	public void setWebSocketSession(Session webSocketSession) {
		this.webSocketSession = webSocketSession;
	}


	/**
	 * compile all the java file present in the package sourcesDir
	 */
	private String compile( String sourcesDirPathName, String classOutputDirPathName ) throws IOException {	
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
	private void run(String mainClassName, String classOutputDirPathName) {
		System.out.println( "Running main from class "+ mainClassName+"..." );
		String result="";
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
					try {
						Thread.sleep(2000);
						webSocketSession.getBasicRemote().sendText(result);
						result = "";
						i = 0;
					} catch (Exception e) {
						return;
					}
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
	private ArrayList<File> getPkgFiles(String pkgPathName){
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
				result= compile(sourcesPath, binPath);
				//send the result (i.e: compilation Succeed or compilation failed + caused)
				webSocketSession.getBasicRemote().sendText(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(operation.equals("run")){

			//run the compile file of the project
			run( mainClass, binPath);
		}
		else if(operation.equals("compilerun")){
			try {
				//Attempt to compile the file;
				result= compile(sourcesPath, binPath);
				//send the result (i.e: compilation Succeed or compilation failed + caused)
				webSocketSession.getBasicRemote().sendText(result);
				if(result.equals("Compilation Succeed!")){
					//run the compile file of the project
					run( mainClass, binPath);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

