/* 
*                 IFS Research & Development 
*
*  This program is protected by copyright law and by international
*  conventions. All licensing, renting, lending or copying (including
*  for private use), and all other use of the program, which is not
*  expressively permitted by IFS Research & Development (IFS), is a
*  violation of the rights of IFS. Such violations will be reported to the
*  appropriate authorities.
*
*  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
*  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
* ----------------------------------------------------------------------------
*  File        : DocSubjectLov.java
*  WYRALK      : 2007-04-04  - Created. Lov to retrive the subject_no 
*  ASSALK      : 2007-08-20  - Merged Bug 64508. Checked in to sparx harverst. 

* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocSubjectLov extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocSubjectLov");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public DocSubjectLov(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	protected void doReset() throws FndException
	{
		super.doReset();               
	}

	public ASPPoolElement clone(Object obj) throws FndException
	{
		DocSubjectLov page = (DocSubjectLov)(super.clone(obj));

		// Cloning immutable attributes
		page.blk = page.getASPBlock(blk.getName());
		page.tbl = page.getASPTable(tbl.getName());
		page.lay = page.blk.getASPBlockLayout();              

		return page;
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();               
	}        

	public void  preDefine()
	{
	                     
                ASPManager mgr = getASPManager();


		blk = mgr.newASPBlock("HEAD");
		blk.setView("DOC_SUBJECT");
                
        blk.addField("SUBJECT_NO").
            setSize(20).
            setLabel("DOCMAWDOCSUBJECTLOVSUBJECT_NO: Folder Name");
            
        blk.addField("SUBJECT_REV").
            setSize(20).
            setLabel("DOCMAWDOCSUBJECTLOVSUBJECT_REV: Revision");
        
        blk.addField("SUBJECT_VER").
            setSize(20).
            setLabel("DOCMAWDOCSUBJECTLOVSUBJECT_VER: Variant");
            
        blk.addField("SUBJECT_NAME").
            setSize(20).
            setLabel("DOCMAWDOCSUBJECTLOVSUBJECT_NAME: Description");
        
        blk.addField("SUBJECT_MODE").
            setSize(25).
            setLabel("DOCMAWDOCSUBJECTLOVSUBJECT_MODE: Status");
        
        blk.addField("USER_CREATED").
            setSize(20).
            setLabel("DOCMAWDOCSUBJECTLOVUSER_CREATED: Created By");
             
               		
		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("DOCMAWDOCSUBJECTLOVUSER: Folder Name"));
		tbl.setKey("SUBJECT_NO");              
		defineLOV(); 
               


            }

}
