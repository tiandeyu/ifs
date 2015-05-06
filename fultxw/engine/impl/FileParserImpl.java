package ifs.fultxw.engine.impl;

import ifs.fultxw.engine.api.FileParser;
import ifs.fultxw.engine.util.XpdfToText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.poi.hwpf.extractor.WordExtractor;


public class FileParserImpl implements FileParser{
	
	
	public static void main(String[] args) {
		FileParserImpl paser = new FileParserImpl();
		String result = null;
		String filePath = null;
		
//		filePath = "E:\\fulltexttmp\\doc.doc";
//		filePath = "ftp://localhost/IFSAPP/doc.doc";//ftp://localhost/IFSAPP/doc.doc
		filePath = "D:\\ifs75sp7\\fulltext_temp\\pdf.pdf";
		
//		result = paser.parseDoc1(filePath,false);
		result = paser.parsePdf(filePath,false);
		
		System.out.println(result);
		org.apache.poi.hwpf.extractor.WordExtractor d;
	}
	
	private String parseDoc1(String filePath,boolean deleteFileAfterReturn) {
		File file = new File(filePath);
		if(0 == file.length()){//some file may not exist on the ftp
			return "";
		}
		String sWordText = "";
		WordExtractor wordExtractor = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(filePath);
			wordExtractor = new WordExtractor(fs);
			if (wordExtractor != null) {
				sWordText = wordExtractor.getTextFromPieces();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fs.close();
				if(deleteFileAfterReturn){
					file.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			fs = null;
			wordExtractor = null;
		}
		return sWordText;
	}
	
	private String parseDoc1(InputStream is) {
		String sWordText = "";
		WordExtractor wordExtractor = null;
		try {
			wordExtractor = new WordExtractor(is);
			if (wordExtractor != null) {
				sWordText = wordExtractor.getTextFromPieces();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wordExtractor = null;
		}
		return sWordText;
	}
	
	public String parseDoc(String filePath) {//conflict with ht workflow,using method 
		return parseDoc1(filePath,true);

	}

	public String parseDoc(String filePath, boolean deleteOriginalFile) {
		return parseDoc1(filePath,deleteOriginalFile);
   }

	
	public String parseDoc(InputStream is) {
		return parseDoc1(is);
//		String text = null;
//		try {
//			WordExtractor extrator = new WordExtractor();
//			text = extrator.extractText(is);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return text;
	}

	
	public String parsePdf(String filePath) {
		 return parsePdf( filePath, true);
	}
	
	
	public String parsePdf(String filePath, boolean deleteOriginalFile) {
		String result = "";
		String pdfTxtFile = filePath + ".txt";
		try {
			XpdfToText xpToTxt = new XpdfToText();
			String success = xpToTxt.XpdftoText(filePath, pdfTxtFile);
			if (!"".equals(success)){
				return "";
			}
			// 读取提取生成的txt文件
			File txtFile = new File(pdfTxtFile);
			if (txtFile.exists() && txtFile.length() > 0) {
				result = parseTxt(pdfTxtFile);
			}
			// 删除临时生成的txt文件
			System.out.println(pdfTxtFile + "文件：" + result);
			txtFile.delete();
			//删除原文件
			if(deleteOriginalFile){
				File originalFile = new File(filePath);
				originalFile.delete();
			}
			txtFile = null;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	
	public String parseXls(String filePath) {
		
		return null;
	}

	
	public String parseTif(String filePath) {
		
		return null;
	}

	
	public String parseTxt(String filePath) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), "GBK"));
			String line = new String();
			StringBuffer temp = new StringBuffer();

			while ((line = reader.readLine()) != null) {
				temp.append(line);
			}
			reader.close();
			return temp.toString();
		} catch (Exception ex) {
			return "";
		}
	}

	
	public String parsePpt(String filePath) {
		
		return null;
	}









	public String parseXls(String filePath, boolean deleteOriginalFile) {
	   // TODO Auto-generated method stub
	   return null;
   }

}
