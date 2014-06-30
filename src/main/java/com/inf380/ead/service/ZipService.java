package com.inf380.ead.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip service that create zip file
 * @author myayo & Hanzhi
 */
public class ZipService {

	/**
	 * Create zip file from directory pathName
	 * @param pathName
	 * @throws IOException
	 */
	public byte[] createZip(String pathName) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		File dir = new File(pathName);
		List<String> fileList = getFileList(dir, dir.getAbsolutePath());
		ZipOutputStream zos=new ZipOutputStream(baos);

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
		baos.close();
		return baos.toByteArray();
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
