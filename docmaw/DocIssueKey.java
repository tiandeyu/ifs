package ifs.docmaw;

import ifs.fnd.service.IfsNames;

/**
 * @author Terry
 *
 */
public class DocIssueKey {

	public String doc_class;
	public String doc_no;
	public String doc_sheet;
	public String doc_rev;
	
	/**
	 * @param docClass
	 * @param docNo
	 * @param docSheet
	 * @param docRev
	 */
	public DocIssueKey(String docClass, String docNo, String docSheet, String docRev) 
	{
		super();
		doc_class = docClass;
		doc_no = docNo;
		doc_sheet = docSheet;
		doc_rev = docRev;
	}
	
	public DocIssueKey()
	{
		super();
		doc_class = "";
		doc_no = "";
		doc_sheet = "";
		doc_rev = "";
	}
	
	/**
	 * @return the doc_class
	 */
	public String getDoc_class() {
		return doc_class;
	}
	/**
	 * @param docClass the doc_class to set
	 */
	public void setDoc_class(String docClass) {
		doc_class = docClass;
	}
	/**
	 * @return the doc_no
	 */
	public String getDoc_no() {
		return doc_no;
	}
	/**
	 * @param docNo the doc_no to set
	 */
	public void setDoc_no(String docNo) {
		doc_no = docNo;
	}
	/**
	 * @return the doc_sheet
	 */
	public String getDoc_sheet() {
		return doc_sheet;
	}
	/**
	 * @param docSheet the doc_sheet to set
	 */
	public void setDoc_sheet(String docSheet) {
		doc_sheet = docSheet;
	}
	/**
	 * @return the doc_rev
	 */
	public String getDoc_rev() {
		return doc_rev;
	}
	/**
	 * @param docRev the doc_rev to set
	 */
	public void setDoc_rev(String docRev) {
		doc_rev = docRev;
	}
	
	
	public String getKeyString() 
	{
		return 	"DOC_CLASS=" + doc_class + IfsNames.textSeparator + 
				"DOC_NO=" + doc_no + IfsNames.textSeparator + 
				"DOC_SHEET=" + doc_sheet + IfsNames.textSeparator +
				"DOC_REV=" + doc_rev + IfsNames.textSeparator;
	}
	
}
