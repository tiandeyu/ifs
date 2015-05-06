package ifs.fultxw.engine.api;

import java.io.InputStream;

/**
 * 
 * @author lqw
 *
 */
public interface FileParser {
	
	/**
	 * extract doc file's content into a string
	 * @param filePath
	 * @return
	 */
	String parseDoc(String filePath);
	/**
	 * extract doc file's content into a string
	 * @param filePath
	 * @return
	 */
	String parseDoc(String filePath,boolean deleteOriginalFile);
	
	/**
	 * extract doc file's content into a string
	 * @param is
	 * @return
	 */
	String parseDoc(InputStream is);
	/**
	 * extract pdf file's content into a string
	 * @param filePath
	 * @return
	 */
	String parsePdf(String filePath);
	
	/**
	 * extract pdf file's content into a string
	 * @param filePath
	 * @return
	 */
	String parsePdf(String filePath,boolean deleteOriginalFile);
	/**
	 * extract xls file's content into a string
	 * @param filePath
	 * @return
	 */
	String parseXls(String filePath,boolean deleteOriginalFile);
	/**
	 * extract xls file's content into a string
	 * @param filePath
	 * @return
	 */
	String parseXls(String filePath);
	/**
	 * extract doc file's content into a string
	 * @param filePath
	 * @return
	 */
	String parseTif(String filePath);
	/**
	 * extract doc file's content into a string
	 * @param filePath
	 * @return
	 */
	String parseTxt(String filePath);
	/**
	 * extract doc file's content into a string
	 * @param filePath
	 * @return
	 */
	String parsePpt(String filePath);
	
}
