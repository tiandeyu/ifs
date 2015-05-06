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
*  File        : PermitTypeRMB.java 
*  Created     : ASP2JAVA Tool  010212
*  Modified    :
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  VAGULK  011020  Added savereturnItem,savenewItem and cancelnewItem (71129) &modified the Error msg given
*  PAPELK  031023  Call Id 108308.Added a 'Back' button to page.
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  SAPRLK  040401  Removed Back button functionality for Web Alignment.
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  050922  Bug26414: Added RMB 'Connected Objects'.
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  AMDILK  070807  Call Id 144576: Modified okFind()
*  ILSOLK  070824  Modified okFind().(Call ID 147647). 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PermitTypeRMB extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PermitTypeRMB");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock itemblk0;
	private ASPRowSet itemset0;
	private ASPCommandBar itembar0;
	private ASPTable itemtbl0;
	private ASPBlockLayout itemlay0;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String returnBuffer;
	private ASPTransactionBuffer trans;
	private String calling_url;
	private String form_name;
	private String primary_key;
	private ASPBuffer buff;
	private ASPBuffer row;
	private String val;
	private ASPCommand cmd;
	private String strItemDesc;
	private String strAttrDesc;
	private ASPQuery q;
	private String n;
	private ASPBuffer data;
	private int headrowno;
	private boolean multirow;
	private int currrow;
	private String permit_type_id; 
	private String txt;
	private boolean saveNewError;
	private String sWoNo;
	private String sRowNo;
        private boolean bOpenNewWindow;
        private String urlString;
        private String newWinHandle;

	//===============================================================
	// Construction 
	//===============================================================
	public PermitTypeRMB(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		returnBuffer =  "0";
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();

		calling_url=ctx.getGlobal("CALLING_URL");

		returnBuffer = ctx.readValue("CTX1",returnBuffer);
		permit_type_id = ctx.readValue("CTX2",permit_type_id);
		saveNewError = ctx.readFlag("SAVENEWERROR",saveNewError);
		sWoNo = ctx.readValue("CTXWONO","");
		sRowNo = ctx.readValue("CTXROWNO","");

		if (   mgr.commandBarActivated(   ))
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
		{
			okFind();  
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("PERMIT_TYPE_ID")))
		{
			mgr.setPageExpiring();   
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
			okFind();   
		}

		ctx.writeValue("CTXWONO",sWoNo);
		ctx.writeValue("CTXROWNO",sRowNo);
		ctx.writeValue("CTX1",returnBuffer);
		ctx.writeValue("CTX2",permit_type_id);
		ctx.writeFlag("SAVENEWERROR",saveNewError);

	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("PERMIT_ATT_CODE".equals(val))
		{
			cmd = trans.addCustomFunction("ITEMDESC", "Permit_Attribute_API.Get_Description", "ITEM0_DESCRIPTION" );
			cmd.addParameter("PERMIT_ATT_CODE");

			cmd = trans.addCustomFunction("ATTRDESC", "Permit_Attribute_API.Get_Attr_Descr", "ATTDESCR" );
			cmd.addParameter("PERMIT_ATT_CODE");

			trans = mgr.validate(trans);

			strItemDesc = trans.getValue("ITEMDESC/DATA/ITEM0_DESCRIPTION");
			strAttrDesc = trans.getValue("ATTRDESC/DATA/ATTDESCR");

			txt = (mgr.isEmpty(strItemDesc) ? "" :strItemDesc ) + "^" + (mgr.isEmpty(strAttrDesc) ? "" :strAttrDesc) + "^" ;

			mgr.responseWrite(txt);
			mgr.endResponse();
		}
	}

/////----------------------------------------------------------------------------
/////------------------------ GCreated Functions --------------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();
                ASPBuffer row;
		String sPermitTypeId  = null;

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");

		if (mgr.dataTransfered())
		{
		    buff = mgr.getTransferedData();
		    row = buff.getBufferAt(0); 
		    sPermitTypeId = row.getValue("PERMIT_TYPE_ID");
		}

		if ( ! mgr.isEmpty(sPermitTypeId) )
		{
                   q.addWhereCondition("PERMIT_TYPE_ID = ?");
		   q.addParameter("PERMIT_TYPE_ID", sPermitTypeId);
		}


		if (!(headlay.isMultirowLayout()))
		{
			q = trans.addQuery(itemblk0);
			q.addMasterConnection("HEAD","PERMIT_TYPE_ID","ITEM0_PERMIT_TYPE_ID");
			q.includeMeta("ALL");
		}

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWPERMITTYPERMBNODATA: No data found."));
			headset.clear();
		}
	}


	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		n = headset.getRow().getValue("N");
		headlay.setCountValue(toInt(n));
		headset.clear();
	}


	public void newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","PERMIT_TYPE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data); 
	}

/////----------------------------------------------------------------------------
/////-------------------------- ITEM Functions ---------------------------------
//-----------------------------------------------------------------------------

	public void newOkFindITEM0()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addQuery(itemblk0);
		//Bug 58216 Start
		q.addWhereCondition("PERMIT_TYPE_ID = ?");
		q.addParameter("PERMIT_TYPE_ID",headset.getRow().getValue("PERMIT_TYPE_ID"));
		//Bug 58216 End
		q.includeMeta("ALL");

		headrowno = headset.getCurrentRowNo();
		mgr.submit(trans);
		if (itemset0.countRows() == 0 &&  ("ITEM0.OkFind".equals(mgr.readValue("__COMMAND"))))
		{
			mgr.showAlert(mgr.translate("PCMWPERMITTYPERMBNODATA: No data found."));
			itemset0.clear();
		}
		headset.goTo(headrowno);
	}


	public void okFindITEM0()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addQuery(itemblk0);
		//Bug 58216 Start
		q.addWhereCondition("PERMIT_TYPE_ID = ?");
		q.addParameter("PERMIT_TYPE_ID",headset.getRow().getValue("PERMIT_TYPE_ID"));
		//Bug 58216 End

		q.includeMeta("ALL");

		headrowno = headset.getCurrentRowNo();
		mgr.submit(trans);
		headset.goTo(headrowno);
	}


	public void countFindITEM0()
	{
		ASPManager mgr = getASPManager();

		headrowno = headset.getCurrentRowNo();
		q = trans.addQuery(itemblk0);
		//Bug 58216 Start
		q.addWhereCondition("PERMIT_TYPE_ID = ?");
		q.addParameter("PERMIT_TYPE_ID",headset.getRow().getValue("PERMIT_TYPE_ID"));
		//Bug 58216 End
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		n = itemset0.getRow().getValue("N");
		itemlay0.setCountValue(toInt(n));
		itemset0.clear();
		headset.goTo(headrowno);
	}


	public void newRowITEM0()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("ITEM0","PERMIT_TYPE_ATTRIBUTE_API.New__",itemblk0);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM0/DATA");
		data.setFieldItem("ITEM0_PERMIT_TYPE_ID",headset.getRow().getValue("PERMIT_TYPE_ID"));
		itemset0.addRow(data);
	}

	public void cancelNewItem()
	{
		int itemlay = itemlay0.getHistoryMode();

		if ( itemset0.countRows() > 0 )
			itemset0.clearRow();
		else
			itemset0.clear();


		if (saveNewError)
		{
			saveNewError = false;
			itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
		}
		else
		{
			itemlay0.setLayoutMode(itemlay);
		}
	}

	public void saveReturnItem()
	{
		ASPManager mgr = getASPManager();
		int currHead = headset.getCurrentRowNo();
		int currrowItem = itemset0.getCurrentRowNo();
		itemset0.changeRow();
		q = trans.addQuery(headblk);

		String headPermitType = headset.getRow().getValue("PERMIT_TYPE_ID");
		String newAttr = mgr.readValue("PERMIT_ATT_CODE");
		
		//Bug 58216 Start
		q = trans.addQuery("DUMMY1","SELECT count(*) FROM PERMIT_ATTRIBUTE WHERE PERMIT_ATT_CODE = ?");
		q.addParameter("PERMIT_ATT_CODE",newAttr);
		q = trans.addQuery("DUMMY2","SELECT count(*) FROM PERMIT_TYPE_ATTRIBUTE WHERE PERMIT_ATT_CODE = ? and PERMIT_TYPE_ID = ?");
		q.addParameter("PERMIT_ATT_CODE",newAttr);
		q.addParameter("PERMIT_TYPE_ID",headPermitType);
		//Bug 58216 End

		trans = mgr.perform(trans);

		ASPBuffer tempBuff1 = trans.getBuffer("DUMMY1/DATA");
		ASPBuffer tempBuff2 = trans.getBuffer("DUMMY2/DATA");

		String num1 = tempBuff1.getValueAt(0);
		String num2 = tempBuff2.getValueAt(0);
		trans.clear();

		if ("0".equals(num1))
		{
			mgr.showAlert(mgr.translate("PCMWPERMITTYPERMBWRONGATTR: PermitAttribute does not exist."));
			itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
		}
		else if (!("0".equals(num2)))
		{
			mgr.showAlert(mgr.translate("PCMWPERMITTYPERMBSAMEATTR: PermitAttribute already exists."));
			itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
		}
		else
		{
			mgr.submit(trans);
			itemset0.goTo(currrowItem);
		}
		headset.goTo(currHead);
	}


	public void saveNewItem()
	{
		ASPManager mgr = getASPManager();
		int currHead = headset.getCurrentRowNo();
		int currrowItem = itemset0.getCurrentRowNo();
		itemset0.changeRow();
		q = trans.addQuery(headblk);

		String headPermitType = headset.getRow().getValue("PERMIT_TYPE_ID");
		String newAttr = mgr.readValue("PERMIT_ATT_CODE");

		//Bug 58216 Start
		q = trans.addQuery("DUMMY1","SELECT count(*) FROM PERMIT_ATTRIBUTE WHERE PERMIT_ATT_CODE = ?");
		q.addParameter("PERMIT_ATT_CODE",newAttr);
		q = trans.addQuery("DUMMY2","SELECT count(*) FROM PERMIT_TYPE_ATTRIBUTE WHERE PERMIT_ATT_CODE = ? and PERMIT_TYPE_ID = ?");
		q.addParameter("PERMIT_ATT_CODE",newAttr);
		q.addParameter("ITEM0_PERMIT_TYPE_ID",headPermitType);
		//Bug 58216 End


		trans = mgr.perform(trans);

		ASPBuffer tempBuff1 = trans.getBuffer("DUMMY1/DATA");
		ASPBuffer tempBuff2 = trans.getBuffer("DUMMY2/DATA");

		String num1 = tempBuff1.getValueAt(0);
		String num2 = tempBuff2.getValueAt(0);
		trans.clear();

		if ("0".equals(num1))
		{
			mgr.showAlert(mgr.translate("PCMWPERMITTYPERMBWRONGATTR: PermitAttribute does not exist."));
			saveNewError = true;
			itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
		}
		else if (!("0".equals(num2)))
		{
			mgr.showAlert(mgr.translate("PCMWPERMITTYPERMBSAMEATTR: PermitAttribute already exists."));
			saveNewError = true;
			itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
		}
		else
		{
			mgr.submit(trans);
			trans.clear();
			newRowITEM0();
			saveNewError = false;
		}
		headset.goTo(currHead);
	}

        public void characteristics()
        {
            ASPManager mgr = getASPManager();

            if (headlay.isMultirowLayout())
                headset.goTo(headset.getRowSelected());
            else
                headset.selectRow();

            bOpenNewWindow = true;
            urlString = "../appsrw/TechnicalObjectReference.page?LU_NAME=" + mgr.URLEncode(headset.getRow().getValue("LU_NAME")) +
                        "&KEY_REF=" + mgr.URLEncode(headset.getRow().getValue("KEY_REF"));


            newWinHandle = "frmTecObject";      

        }

        public void documents()
        {
            ASPManager mgr = getASPManager();

            if (headlay.isMultirowLayout())
                headset.goTo(headset.getRowSelected());
            else
                headset.selectRow();

            bOpenNewWindow = true;
            urlString = "../docmaw/DocReference.page?LU_NAME=" + mgr.URLEncode(headset.getRow().getValue("LU_NAME")) +
                        "&KEY_REF=" + mgr.URLEncode(headset.getRow().getValue("KEY_REF"));


            newWinHandle = "frmDocObject";      

        }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------


	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("PERMIT_TYPE_ID");
		f.setSize(15);
		f.setMandatory();
		f.setReadOnly();
		f.setInsertable();
		f.setMaxLength(4); 
		f.setUpperCase();
		f.setLabel("PCMWPERMITTYPERMBPTYPEID: Permit Type");

		f = headblk.addField("DESCRIPTION");
		f.setSize(43);
		f.setMandatory();
		f.setMaxLength(40);
		f.setReadOnly();
		f.setInsertable();   
		f.setLabel("PCMWPERMITTYPERMBDESC: Description");

                f = headblk.addField("LU_NAME");
                f.setHidden();
                f.setFunction("'PermitType'");

                f = headblk.addField("KEY_REF");
                f.setHidden();
                f.setFunction("CONCAT('PERMIT_TYPE_ID=',CONCAT(PERMIT_TYPE_ID,'^'))");

		headblk.setView("PERMIT_TYPE");
		headblk.defineCommand("PERMIT_TYPE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWPERMITTYPERMBHD: Attributes for Permit Type"));
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");

                headbar.addCustomCommand("characteristics",mgr.translate("PCMWCONCHARS: Characteristics..."));
                headbar.addCustomCommand("documents",mgr.translate("PCMWCONDOCS: Documents..."));

                headbar.addCustomCommandGroup("CONNOBJ", mgr.translate("PCMWPERMITTYPECONOBJ: Connected Objects"));
                headbar.setCustomCommandGroup("characteristics", "CONNOBJ"); 
                headbar.setCustomCommandGroup("documents", "CONNOBJ");

                headbar.removeFromMultirowAction("documents");
                headbar.removeFromMultirowAction("characteristics");

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);


		// ----------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------


		itemblk0 = mgr.newASPBlock("ITEM0");

		f = itemblk0.addField("ITEM0_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk0.addField("ITEM0_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk0.addField("ITEM0_PERMIT_TYPE_ID");
		f.setSize(20);
		f.setDynamicLOV("PERMIT_TYPE",600,450);
		f.setHidden();
		f.setLabel("PCMWPERMITTYPERMBIT0PTID: Permit Type");
		f.setDbName("PERMIT_TYPE_ID");
		f.setUpperCase(); 

		f = itemblk0.addField("PERMIT_ATT_CODE");
		f.setSize(17);
		f.setDynamicLOV("PERMIT_ATTRIBUTE",600,480);
		f.setMandatory();
		f.setMaxLength(15);
		f.setLabel("PCMWPERMITTYPERMBPATTCODE: Attribute Code");
		f.setUpperCase();
		f.setReadOnly();
		f.setInsertable();
		f.setCustomValidation("PERMIT_ATT_CODE", "ITEM0_DESCRIPTION,ATTDESCR");

		f = itemblk0.addField("ITEM0_DESCRIPTION");
		f.setSize(40);
		f.setMaxLength(60);
		f.setLabel("PCMWPERMITTYPERMBIT0DESC: Short Description");
		f.setFunction("Permit_Attribute_API.Get_Description(:PERMIT_ATT_CODE)");
		f.setReadOnly();

		f = itemblk0.addField("ATTDESCR");
		f.setSize(40);
		f.setLabel("PCMWPERMITTYPERMBATTDESCR: Description");
		f.setFunction("Permit_Attribute_API.Get_Attr_Descr(:PERMIT_ATT_CODE)");
		f.setReadOnly();
		f.setInsertable();
		f.setHidden();

		itemblk0.setView("PERMIT_TYPE_ATTRIBUTE");
		itemblk0.defineCommand("PERMIT_TYPE_ATTRIBUTE_API","New__,Modify__,Remove__");
		itemblk0.setMasterBlock(headblk);

		itemset0 = itemblk0.getASPRowSet();

		itembar0 = mgr.newASPCommandBar(itemblk0);
		itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnItem","checkItem0Fields()");
		itembar0.defineCommand(itembar0.SAVENEW,"saveNewItem","checkItem0Fields()");   
		itembar0.defineCommand(itembar0.CANCELNEW,"cancelNewItem");   

		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.setTitle(mgr.translate("PCMWPERMITTYPERMBITM0: Attribute details"));
		itemtbl0.setWrap();

		itemlay0 = itemblk0.getASPBlockLayout();
		itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT); 

		itembar0.enableCommand(itembar0.FIND);
		itembar0.defineCommand(itembar0.NEWROW, "newRowITEM0");   
		itembar0.defineCommand(itembar0.COUNTFIND, "countFindITEM0");   
		itembar0.defineCommand(itembar0.OKFIND, "newOkFindITEM0"); 
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWPERMITTYPERMBATTRFORPER: Attributes for Permit Type";
	}

	protected String getTitle()
	{
		return "PCMWPERMITTYPERMBATTRFORPER: Attributes for Permit Type";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		if (headlay.isSingleLayout() && (headset.countRows() > 0))
			appendToHTML(itemlay0.show());

		appendToHTML("<br>\n");

                if (bOpenNewWindow)
                {
                    appendDirtyJavaScript("  window.open(\"");
                    appendDirtyJavaScript(urlString);
                    appendDirtyJavaScript("\", \"");
                    appendDirtyJavaScript(newWinHandle);
                    appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
                }
	}
}
