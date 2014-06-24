package com.inf380.ead.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Model {

	private final ArrayList<User> userList = new ArrayList<>();


	public void init() throws IOException{

		File file = new File("src/main/resources/users.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String lu = null;
		while((lu = reader.readLine())!= null){
			String[] str = lu.split(" ");
			userList.add(new User(str[0], str[1]));
		}
		reader.close();
	}


	public boolean log(String login, String password){
		boolean found=false;
		for(int i=0; i<userList.size() && !found; i++){
			if(userList.get(i).getLogin().equals(login) && userList.get(i).getPassword().equals(password) ){
				found=true;
			}
		}
		return found;
	}

	public void register(String login, String password) throws FileNotFoundException{
		userList.add(new User(login, password));
		File file = new File("src/main/resources/users.txt");
		PrintWriter writer = new PrintWriter(file);
		writer.println(login + " "+ password);
		writer.close();
	}
}
