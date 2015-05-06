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
*  File        : DocPackageProjOvw.java
* 		VAGULK  2004-07-08  - Created.
*     BAKALK  2006-07-19    Bug ID 58216, Fixed Sql Injection.
*  03/03/2008   VIRALK   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*  ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.edm.*;


public class DocPackageProjOvw extends ASPPageProvider
{

	//===============================================================
	// Static constants
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocPackageProjOvw");


	//===============================================================
	// Instances created on page creation (immutable attributes)
	//===============================================================
	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	//===============================================================
	// Transient temporary variables (never cloned)
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPQuery q;
	private String searchURL;
	private ASPCommand cmd;
	private ASPBuffer data;
	private String calling_url;
	private ASPBuffer buff;

	private String iscalled;
	private ASPBuffer row;
	private String curRowNo;
	

	//===============================================================
	// Construction
	//===============================================================
	public DocPackageProjOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	public void run()
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();
		iscalled=ctx.readValue("ISCALLED","0");
		curRowNo = ctx.readValue("CURROWNO","0");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
		{
			validate();
		}

		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
		{
			search();
		}

		else if (mgr.dataTransfered())
		{
			okFind();
		}

		else if ((!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))||(!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME"))))
		{
			okFind();
		}


		else if ("TRUE".equals(mgr.readValue("REFRESH_ROW")))
		{
			refreshCurrentRow();
		}

		else
		{
			headlay.setLayoutMode(headlay.FIND_LAYOUT);
		}


		adjust();
		ctx.writeValue("ISCALLED",iscalled);
		ctx.writeValue("CURROWNO",curRowNo);
	}


	public void  validate()
	{
		ASPManager mgr = getASPManager();
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}


	public void  countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}


	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		searchURL = mgr.createSearchURL(headblk);

		q = trans.addQuery(headblk);
		if (mgr.dataTransfered())
			q.addOrCondition( mgr.getTransferedData() );
		q.includeMeta("ALL");
		mgr.querySubmit(trans,headblk);

		if (headset.countRows() == 0)
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEPROJOVWNODATA: No data found."));
	}


	public void  newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","DOC_ISSUE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}


	public void  keyRefLuNameTransferred()
	{
		ASPManager mgr = getASPManager();

		searchURL = mgr.createSearchURL(headblk);

		trans.clear();
		q = trans.addEmptyQuery(headblk);
      //bug 58216 starts
      q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
      q.addParameter("LU_NAME",mgr.readValue("LOGICAL_UNIT_NAME"));
      q.addParameter("KEY_REF",mgr.readValue("KEY_REF"));
      //bug 58216 ends
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEPROJOVWNODATAFOUND: No data found."));
			headset.clear();
		}
	}


	public void  search()
	{
		iscalled="1";
		okFind();
	}

	public void  refreshCurrentRow()
	{
		if (headlay.isMultirowLayout())
			headset.goTo(Integer.parseInt(curRowNo));
		else
			headset.selectRow();

		//  change the edm status of the file after edit,checkin etc operations
		headset.refreshRow();
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		// MDAHSE, 2001-01-17, we do not want to connect
		// a documnent to the connection, right?
		// So we disable the Documents action
		// and user our menu choice instead

		//headblk.disableDocMan();

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("ACTIVITY_CONNECTED","Number").
		setSize(12).
		setLabel("DOCMAWDOCPACKAGEPROJOVWCONNECTION: Connection").
		setInsertable().
		setCheckBox("0,1");  

		headblk.addField("ACTIVITY_SEQ", "Number").
		setSize(20).
		setReadOnly().
		setInsertable().
		setLabel("DOCMAWDOCPACKAGEPROJOVWACTIVITYID: Activity Seq.");

		headblk.addField("PACKAGE_NO").
		setSize(20).
		setMaxLength(10).
		setMandatory().
		setReadOnly().
		setInsertable().
		setUpperCase().
		setLabel("DOCMAWDOCPACKAGEPROJOVWPACKAGENO: Package");

		headblk.addField("DESCRIPTION").
		setSize(20).
		setReadOnly().
		setMaxLength(200).
		setMandatory().
		setLabel("DOCMAWDOCPACKAGEPROJOVWPACKAGEIDDESCRIPTION: Package Description");

		headblk.addField("DATE_CREATED","Datetime").
		setSize(20).
		setMaxLength(10).
		setInsertable().
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEPROJOVWDATECREATED: Date Created");

		headblk.addField("USER_CREATED").
		setSize(20).
		setMaxLength(30).
		setInsertable().
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEPROJOVWCREATEDBY: Created By");

		headblk.addField("COPIED_FROM_PKG").
		setSize(20).
		setMaxLength(10).
		setInsertable().
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEPROJOVWCREATEDBY: Created By");

		headblk.addField("RESPONSIBLE").
		setSize(20).
		setMaxLength(30).
		setMandatory().
		setDynamicLOV("PERSON_INFO_LOV").
		setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEPROJOVWPACKAGEIDRESPONSE: List of Person Id")).
		setLabel("DOCMAWDOCPACKAGEPROJOVWPACKAGEIDRESPONSIBLE: Responsible");

		headblk.addField("RESPONSIBLE_NAME").
		setSize(20).
		setMaxLength(2000).
		setReadOnly().
		setMandatory().
		setLabel("DOCMAWDOCPACKAGEPROJOVWRESPONSIBLENAME: Responsible Name").
		setFunction("PERSON_INFO_API.Get_Name(:RESPONSIBLE)");
		//mgr.getASPField("RESPONSIBLE").setValidation("RESPONSIBLE_NAME");

      //bug 58216 starts
      headblk.addField("LU_NAME").setFunction("''").setHidden();
      headblk.addField("KEY_REF").setFunction("''").setHidden();
      //bug 58216 ends




		headblk.setView("DOC_PACKAGE_ID");
		headblk.defineCommand("DOC_PACKAGE_ID_API","Modify__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DUPLICATEROW);

		// File Operations
		headbar.enableMultirowAction();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("DOCMAWDOCPACKAGEPROJOVWCONNECTDOCPAC: Connect Doc Package to Activity"));
		headtbl.enableRowSelect();

		headbar.addCustomCommand("connectToActivity",mgr.translate("DOCMAWDOCPACKAGEPROJOVWCONNECTACTIVITY: Connect To Activity..."));

		headlay = headblk.getASPBlockLayout();
		headlay.setDialogColumns(2);
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

	}


	public void  adjust()
	{
		if (headset.countRows() == 0)
		{
			headlay.setLayoutMode(headlay.FIND_LAYOUT);
		}
	}


	//===============================================================
	//  HTML
	//===============================================================
	protected String getDescription()
	{
		return "DOCMAWDOCPACKAGEPROJOVWTITLE: Overview - Connect Doc Package to Activity";
	}


	protected String getTitle()
	{
		return "DOCMAWDOCPACKAGEPROJOVWTITLE: Overview - Connect Doc Package to Activity";
	}


	protected AutoString getContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		AutoString out = getOutputStream();
		out.clear();
		out.append("<html>\n");
		out.append("<head>\n");
		out.append(mgr.generateHeadTag("DOCMAWDOCPACKAGEPROJOVWTITLE: Overview - Connect Doc Package to Activity"));
		out.append("</head>\n");
		out.append("<body ");
		out.append(mgr.generateBodyTag());
		out.append(">\n");
		out.append("<form ");
		out.append(mgr.generateFormTag());
		out.append(">\n");
		out.append(mgr.startPresentation("DOCMAWDOCPACKAGEPROJOVWTITLE: Overview - Connect Doc Package to Activity"));

		out.append("<input type=\"hidden\" name=\"REFRESH_ROW\" value=\"FALSE\">\n");
		out.append(headlay.show());

		//
		// CLIENT FUNCTIONS
		//

		out.append(mgr.endPresentation());
		out.append("</form>\n");
		out.append("</body>\n");
		out.append("</html>");
		return out;
	}

}
