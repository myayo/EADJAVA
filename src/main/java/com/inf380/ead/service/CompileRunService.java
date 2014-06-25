package com.inf380.ead.service;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class CompileRunService {
	
	private ExecutorService executorService=Executors.newFixedThreadPool(10);
	public ArrayList<Future<String>> listTask=new ArrayList<Future<String>>();

	/************Constructor***************/
	
	public CompileRunService(){
	}
	

	

	/************Methods
	 * @throws ExecutionException 
	 * @throws InterruptedException ***************/
	public String compile(String sourcesPath,String classPath) throws InterruptedException, ExecutionException{
		String result="";
		CompileRunTask task;
		Future<String> future = null;
		task=new CompileRunTask("compile",sourcesPath,classPath);
		future = executorService.submit(task);
		listTask.add(future);
		result=future.get();
		if(future.isDone()){
			listTask.remove(listTask.size()-1);
		}
		return result;
	}
	public String run(String classPath, String mainClass) throws InterruptedException, ExecutionException{
		String result="";
		CompileRunTask task;
		Future<String> future = null;
		task=new CompileRunTask("run",classPath, mainClass);
		future = executorService.submit(task);
		listTask.add(future);
		result=future.get();
		if(future.isDone()){
			listTask.remove(listTask.size()-1);
		}
		return result;
	}
	
	public String compileAndRun(String sourcesPath,String classPath, String mainClass) throws InterruptedException, ExecutionException{
		String result="";
		CompileRunTask task;
		Future<String> future = null;
		task=new CompileRunTask("compilerun",sourcesPath,classPath, mainClass);
		future = executorService.submit(task);
		listTask.add(future);
		result=future.get();
		if(future.isDone()){
			listTask.remove(listTask.size()-1);
		}
		return result;
	}
	
	public String stop(){
		for(int i=0; i<listTask.size();i++){
			listTask.get(i).cancel(true);
		}
		String result = "Stopped!";
		return result;

	}
	
	
}

