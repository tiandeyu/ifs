package ifs.fultxw.engine.util;

import ifs.fultxw.engine.Constants;
import ifs.fultxw.engine.dao.FullTextDao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class XpdfToText {   
	private static String XPDFTOOLS = "pdftotext.exe";
	
	 /**
     * extract characters from pdf file sourcePath into targetPath 
     * D:\Develpment\xpdf\pdftotext.exe -layout -enc GBK -q -nopgbrk d:\pdf.pdf d:\pdf.pdf.txt
     * @return
     */
	public String XpdftoText(String sourcePath,String targetPath)throws IOException{
		File source = new File(sourcePath);   
		if (!source.exists()){
			System.out.println("sourcePath="+sourcePath);
			return "To be Transfered Source File does Not Exist.";
		}
		source=null;
	    StringBuffer commandBuffer = new StringBuffer();
		commandBuffer.append(getXpdfToolsDir()+"\\"+XPDFTOOLS);
		commandBuffer.append(" ");
		commandBuffer.append("-layout");
		commandBuffer.append(" ");
		commandBuffer.append("-enc");
		commandBuffer.append(" ");
		commandBuffer.append("GBK");
		commandBuffer.append(" ");
		commandBuffer.append("-q");
		commandBuffer.append(" ");
		commandBuffer.append("-nopgbrk");
		commandBuffer.append(" ");
		commandBuffer.append(sourcePath);
		commandBuffer.append(" ");
		commandBuffer.append(targetPath);
		
		Process process = Runtime.getRuntime().exec(commandBuffer.toString());
		System.out.println("commandBuffer.toString() = " + commandBuffer.toString());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));   
		String ret = bufferedReader.readLine();
		
		while (ret != null); 
		  try {
			  System.out.println(ret);
			   process.waitFor();   
			   ret = bufferedReader.readLine();
		  } catch (InterruptedException e) {
			  // TODO: handle exception
			   e.printStackTrace();
		  }
		 return "";
	}
	
	private String getXpdfToolsDir(){
		return FullTextDao.fullTextConfig.get(Constants.PARA_XPDF_ROOT_PATH);
	}
	
	public static void main(String[] args) {
		XpdfToText pdfToText = new XpdfToText();
		try {
			pdfToText.XpdftoText("D:\\pdf.pdf", "D:\\pdf.pdf.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}  