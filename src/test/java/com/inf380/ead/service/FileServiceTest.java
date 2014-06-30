package com.inf380.ead.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.inf380.ead.config.Configuration;

public class FileServiceTest {

	private FileService fileService;

	@Before
	public void setUp(){
		Configuration.projectsBaseUrl = "src/test/resources/";
		fileService = new FileService();
	}

	@Test
	public void testCreateDirectory() throws IOException{
		String pathname="Test";
		fileService.createOrUpdateFile(pathname, "directory", null, "Abbes");
		String fileAbsolutePath = Configuration.projectsBaseUrl + "Abbes" + "/" +pathname;
		File file = new File(fileAbsolutePath);
		assertTrue(file.exists());
		assertTrue(file.isDirectory());
		fileService.deleteFile(fileAbsolutePath);
		assertFalse(file.exists());
	}

	@Test
	public void testCreateFile() throws IOException{
		String pathname="Test/Main.java";
		fileService.createOrUpdateFile(pathname, "file", "public void", "Abbes");
		String fileAbsolutePath = Configuration.projectsBaseUrl + "Abbes" + "/" +pathname;
		File file = new File(fileAbsolutePath);
		assertTrue(file.exists());
		assertTrue(file.isFile());
		assertTrue(file.length()>0);
		fileService.deleteFile(fileAbsolutePath);
		assertFalse(file.exists());
	}

	@Test
	public void testGetProject(){
		List<String> projects = fileService.getProjects("Marcel");
		assertEquals(projects.size(), 2);
		assertTrue(projects.contains("Test"));
		assertTrue(projects.contains("test1"));
	}
}
