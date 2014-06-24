package com.inf380.ead.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FileServiceTest {

	private FileService fileService;

	@Before
	public void setUp(){
		fileService = new FileService();
	}

	@Test
	public void testCreateDirectory() throws IOException{
		String pathname="src/test/resources/directoryTest";
		fileService.createOrUpdateFile(pathname, "directory", null);
		File file = new File(pathname);
		assertTrue(file.exists());
		assertTrue(file.isDirectory());
		fileService.deleteFile(pathname);
		assertFalse(file.exists());
	}

	@Test
	public void testCreateFile() throws IOException{
		String pathname="src/test/resources/Main.java";
		fileService.createOrUpdateFile(pathname, "file", "public void");
		File file = new File(pathname);
		assertTrue(file.exists());
		assertTrue(file.isFile());
		assertTrue(file.length()>0);
		fileService.deleteFile(pathname);
		assertFalse(file.exists());
	}

	@Test
	public void testGetProject(){
		fileService.setProjectsBaseUrl("src/test/resources/");
		List<String> projects = fileService.getProjects("Marcel");
		assertEquals(projects.size(), 2);
		assertTrue(projects.contains("Test"));
		assertTrue(projects.contains("Project"));
	}
}
