package com.inf380.ead.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ZipServiceTest {

	@Test
	public void createZip() throws IOException{
		ZipService zip = new ZipService();
		zip.createZip("src/test/resources/test1");
		File file = new File("src/test/resources/test1.zip");
		assertTrue(file.exists());
		assertTrue(file.length()>0);
	}
}
