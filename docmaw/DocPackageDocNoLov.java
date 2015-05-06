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
*  File        : DocPackageDocNoLov.java 
*  Created     : 070618   AMNALK  
* ----------------------------------------------------------------------------
*  ASSALK  070824  Checked in to sparx harvest under bug 66007 merge.
*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class DocPackageDocNoLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocPackageDocNoLov");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPBlockLayout lay;

	//===============================================================
	// Construction 
	//===============================================================
	public DocPackageDocNoLov(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();   
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		blk = mgr.newASPBlock("HEAD");
		blk.setView("DOC_TITLE");

		blk.addField("DOC_CLASS").
		setSize(20).
		setLabel("DOCMAWDOCPACKAGEDOCNOLOVDOCCLASS: Doc Class");

		blk.addField("DOC_NO").
		setLabel("DOCMAWDOCPACKAGEDOCNOLOVDOCNO: Doc No").
		setSize(20);

		blk.addField("TITLE").
		setLabel("DOCMAWDOCPACKAGEDOCNOLOVDOCTITLE: Title").
		setSize(60);

		blk.addField("VIEW_FILE_REQ").
		setLabel("DOCMAWDOCPACKAGEDOCNOLOVVIEWFILEREQ: View Copy").
		setSize(10);

		blk.addField("OBJ_CONN_REQ").
		setLabel("DOCMAWDOCPACKAGEDOCNOLOVOBJCONNREQ: Object Required").
		setSize(10);

		blk.addField("MAKE_WASTE_REQ").
		setLabel("DOCMAWDOCPACKAGEDOCNOLOVMAKEWASTEREQ: Destroy").
		setSize(10);

		blk.addField("SAFETY_COPY_REQ").
		setLabel("DOCMAWDOCPACKAGEDOCNOLOVSAFETYCOPYREQ: Safety Copy").
		setSize(10);


		blk.addField("RET_FIELD").
		setFunction("DOC_NO"+"||"+"'^'"+"||"+"DOC_CLASS"+"||"+"'^'").
		setHidden();

		tbl = mgr.newASPTable(blk);

		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);

		tbl.setTitle(mgr.translate("DOCMAWDOCPACKAGEDOCNOLOVDOCNO1: List of Document Numbers"));
                tbl.setKey("RET_FIELD"); 
		defineLOV();
	}

}
