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
*  File        : LuNameLov.java
*  Nisilk      : 2003-09-09  - Created. 

* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class LuNameLov extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.LuNameLov");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;        

	//===============================================================
	// Construction 
	//===============================================================
	public LuNameLov(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	protected void doReset() throws FndException
	{
		super.doReset();               
	}

	public ASPPoolElement clone(Object obj) throws FndException
	{
		LuNameLov page = (LuNameLov)(super.clone(obj));

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
		blk.setView("OBJECT_CONNECTION");
                
                
                
                blk.addField("LU_DESC").
                setSize(30).
                setLabel("LUDESC: Lu Description");              

                blk.addField("LU_NAME").
                setSize(25).
                //setHidden().
                setLabel("LUNAME: Lu Name");              
		
		
		             
               		
		tbl = mgr.newASPTable(blk);
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
		tbl.setTitle(mgr.translate("OBJECTCONNLUNAME: Lu Names"));
		tbl.setKey("LU_NAME");              
		defineLOV(); 
               


            }

}
