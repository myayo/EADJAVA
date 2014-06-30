package com.inf380.ead.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;

/**
 * class for compilation or execution of java file
 */
public class CompileRunService {

	/**
	 * Thread pool to execute Thread action
	 */
	private ExecutorService executorService;

	/**
	 * WebSocket session object to send response to the client
	 */
	private Session webSocketSession;


	public CompileRunService() {
		executorService = Executors.newFixedThreadPool(10);
	}

	public void setWebSocketSession(Session webSocketSession) {
		this.webSocketSession = webSocketSession;
	}

	/**
	 * Method to compile java program
	 * @param sourcesPath - the program source directory
	 * @param outPutPath - the directory to store the compile program
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void compile(String sourcesPath, String outPutPath) throws InterruptedException, ExecutionException {
		CompileRunTask task = new CompileRunTask("compile", sourcesPath, outPutPath);
		task.setWebSocketSession(webSocketSession);
		executorService.execute(task);
	}

	/**
	 * Method to run compile java program
	 * @param compilePath - the compile program directory
	 * @param mainClass - the main class to execute
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void run(String compilePath, String mainClass) throws InterruptedException, ExecutionException {
		CompileRunTask task = new CompileRunTask("run", compilePath, mainClass);
		task.setWebSocketSession(webSocketSession);
		executorService.execute(task);
	}

	/**
	 * Method to compile and then run java program
	 * @param sourcesPath - the compile program directory
	 * @param classPath - the main class to execute
	 * @param mainClass - the main class to execute
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void compileAndRun(String sourcesPath, String classPath,
			String mainClass) throws InterruptedException, ExecutionException {
		CompileRunTask task = new CompileRunTask("compilerun", sourcesPath,
				classPath, mainClass);
		task.setWebSocketSession(webSocketSession);
		executorService.execute(task);
	}

	/**
	 * Method to stop execution of a java program
	 */
	public void stop() {
		try {
			executorService.shutdownNow();
			executorService.awaitTermination(15, TimeUnit.SECONDS);
			if (executorService.isShutdown()){
				webSocketSession.getBasicRemote().sendText("Execution Stopped...");
				executorService = Executors.newFixedThreadPool(10);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
