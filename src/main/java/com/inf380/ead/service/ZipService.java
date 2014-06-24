package com.inf380.ead.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipService {

	/**
	 * Create zip file from directory pathName
	 * @param pathName
	 * @throws IOException
	 */
	public void createZip(String pathName) throws IOException{
		File dir = new File(pathName);
		List<String> fileList = getFileList(dir, dir.getAbsolutePath());
		String zipName= pathName.substring(0, pathName.lastIndexOf("/") +1) + dir.getName()+".zip";
		FileOutputStream fos=new FileOutputStream(zipName);
		ZipOutputStream zos=new ZipOutputStream(fos);

		for(String filePath:fileList){
			ZipEntry ze=new ZipEntry(filePath);

			zos.putNextEntry(ze);
			FileInputStream fis = new FileInputStream(pathName+File.separator+filePath);
			byte[] buffer=new byte[1024];
			int len;
			while((len=fis.read(buffer))>0){
				zos.write(buffer, 0, len);
			}
			zos.closeEntry();
			fis.close();
		}
		zos.close();
		fos.close();
	}

	private List<String> getFileList(File dir, String sourceFolder) {
		List<String> filesInDir = new ArrayList<String>();
		File[] files=dir.listFiles();
		for(File file:files){
			if(file.isFile()){
				filesInDir.add(file.getAbsoluteFile().toString().substring(sourceFolder.length()+1, file.getAbsoluteFile().toString().length()));
			}
			else {
				filesInDir.addAll(getFileList(file, sourceFolder));
			}
		}
		return filesInDir;
	}
}
