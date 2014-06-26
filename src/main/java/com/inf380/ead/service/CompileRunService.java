package com.inf380.ead.service;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;


public class CompileRunService {

	private final ExecutorService executorService;
	private Session webSocketSession;

	/************Constructor***************/

	public CompileRunService(){
		executorService = Executors.newFixedThreadPool(10);
	}

	public void setWebSocketSession(Session webSocketSession) {
		this.webSocketSession = webSocketSession;
	}




	/************Methods
	 * @throws ExecutionException 
	 * @throws InterruptedException ***************/
	public void compile(String sourcesPath,String classPath) throws InterruptedException, ExecutionException{
		CompileRunTask task = new CompileRunTask("compile",sourcesPath,classPath);
		task.setWebSocketSession(webSocketSession);
		executorService.execute(task);
	}
	public void run(String classPath, String mainClass) throws InterruptedException, ExecutionException{
		CompileRunTask task = new CompileRunTask("run",classPath, mainClass);
		task.setWebSocketSession(webSocketSession);
		executorService.execute(task);
	}

	public void compileAndRun(String sourcesPath,String classPath, String mainClass) throws InterruptedException, ExecutionException{
		CompileRunTask task = new CompileRunTask("compilerun",sourcesPath,classPath, mainClass);
		task.setWebSocketSession(webSocketSession);
		executorService.execute(task);
	}

	public void stop(){
		try {
			executorService.shutdownNow();
			executorService.awaitTermination(10, TimeUnit.SECONDS);
			if(executorService.isShutdown())
				webSocketSession.getBasicRemote().sendText("Execution Stopped...");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}


}

