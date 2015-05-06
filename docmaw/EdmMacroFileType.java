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
*  File        : EdmMacroActionLov.java
*  Nisilk      : 2003-02-03  - Created. Lov to retrive the File Type
*  Bakalk      : 2003-08-25  - Fixed the Call 95412: Added a new field ADD_ALL which is returned
*                              by lov window. 

* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class EdmMacroFileType extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.EdmMacroActionLov");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public EdmMacroFileType(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	protected void doReset() throws FndException
	{
		super.doReset();               
	}

	public ASPPoolElement clone(Object obj) throws FndException
	{
		EdmMacroFileType page = (EdmMacroFileType)(super.clone(obj));

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
		blk.setView("EDM_MACRO");
                
      blk.addField("MACRO_ID").
                setSize(20).
                setLabel("MACROID: Macro Id");              
		
		blk.addField("PROCESS").
                setSize(15).
                setLabel("PROCESS: Process");              
		                
      blk.addField("FILE_TYPE").
                setLabel("FILETYPE: File Type");             
		                
		blk.addField("ACTION").
                setSize(40).
                setLabel("ACTION: Action");
      blk.addField("ADD_ALL").
               setFunction("FILE_TYPE||'^'||PROCESS||'^'||ACTION || '^'||MACRO_ID").
               setHidden();
		             
               		
		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("EDMMACROFILETYPE: List of File Type"));
		tbl.setKey("ADD_ALL");              
		defineLOV(); 
               


            }

}
