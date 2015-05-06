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
*  File        :  ApprovalCurrentStepDocApproveOvw.java
*  Converted   :  Baka using ASP2JAVA Tool on  2001-03-07
*  Created     :  Using the ASP file ApprovalCurrentStepDocApproveOvw.asp
*
*  14/05/2001   Shdilk   Call Id 64844 added an Lov to field Group_ID
*  28/05/2001   Prsalk   Call Id 65237, chaged .asp to .page
*  29/05/2001   Diaklk   Call Id 64971 added methods View Document with Ext. App., Copy File To...
*  26/12/2002   Dikalk   2002-2 SP3 Merge "Larelk - Bug 31903 increased the field length  in person_id"
*  03/02/2003   Prsalk   Added Sheet Number (doc_sheet)
*  31/03/2003   Shthlk   Bug 36076 Error message that appears when performing a unauthorized 'Approve' or 'Reject' is rephrased.
*  31/03/2003            The Note field get open only for authorized users. Removed methods addNote() and addNote2().
*  03/08/2003   NiSilk   Fixed call 95769, modified methods run() and adjust()
*  06/08/2003   InoSlk   SP4 Merge: Bug 36076
*  10/10/2003   InoSlk   Call ID 106797 - Modified method transferToDocInfo().
*  10/13/2003   InoSlk   Call ID 106830 - Added method makeQuery(). Modified methods okFind(),
*                        next_app_step(),onConfirmNextAppStep(),rejectStep(),onConfirmRejectStep(), preDefine().
*  10/12/2003   Bakalk   Web Alignment done.
*  23/12/2003   Bakalk   Some more modification on web alignment.(multirow actions).
*  24/02/2004   DIKALK   Reviewed, refactored and cleaned this page. (more things can still
*                        be done though)
*  24/02/2004   DIKALK   Call 112747. Ensuring rows have been selected before performing
*                        multirow operations
*  29/09/2004   DIKALK   Merged Bug ID 36198
*  01/08/2005   SHTHLK   Merged bug Id 52098, Added new field Description.
*  12/03/2006   RUCSLK   Merged Bug ID 135671, Documents are not getting approved when the user enter his/her un and pw
*                        in Documents to Approve page. 
*  15/03/2006   CHODLK   Merged Bug ID 55249 Change to  the desc. field on a change request does not change the Object Desc. in Overview Approval
*  17/07/2006   BAKALK   Bug ID 58216, Fixed Sql Injection.
*  28/07/2006   BAKALK   Bug ID 58216, found more places to fix, and did them.
*  11/10/2006   BAKALK   Bug 61103, fixed Sql errors
*  11/06/2007   BAKALK   Call Id 144103, Bug 61103, Modified okNote().
*  070806       ILSOLK   Eliminated SQLInjection.
*  070808       ILSOLK   XSS Correction 
*  070925       DinHlk   Changed the title of the page.
*  15/10/2007   AMNALK   Bug Id 67336, Added new function checkFileOperationEnable() and disabled the file operations on mixed or multiple structure documents.
*  03/03/2008   VIRALK   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*  080410       SHTHLK   Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*  25/07/2008	AMNALK   Bug Id 75783, Done lots of changes to handle the approve/reject notes when approving or rejecting.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.edm.*;
import java.util.*;//Bug 61103


public class ApprovalCurrentStepDocApproveOvw extends ASPPageProvider
{
	//
	// Static constants
	//
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.ApprovalCurrentStepDocApproveOvw");


	//
	// Instances created on page creation (immutable attributes)
	//
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;
	private ASPBlock blk;
	private ASPRowSet rowset;
	private ASPCommandBar cmdbar;
	private ASPTable tbl;
	private ASPBlockLayout lay;


	//
	// Transient temporary variables (never cloned)
	//
	private ASPTransactionBuffer trans;
	private boolean bConfirm;
	private boolean bTranferToEDM;
	private String bHeadShow;
	private String bAddNote;
	private String bAddNote2;
	private String sMessage;
	private String confirm_func;
	private String bodyTag;
	private String user_where;
	private String sFilePath;
   //Bug 61103
   private String user_parameters;

	//Bug Id 75783, start
	private String sNote; 
	private boolean bMultipleSelected;
	private boolean bShowUpdateNoteOptions;
	//Bug Id 75783, end

	//
	// Construction
	//
	public ApprovalCurrentStepDocApproveOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	protected void doReset() throws FndException
	{
		//Resetting mutable attributes
		trans         = null;
		bConfirm      = false;
		bHeadShow     = null;
		bAddNote      = null;
		bAddNote2     = null;
		bTranferToEDM = false;
		sMessage      = null;
		confirm_func  = null;
		bodyTag       = null;
		user_where    = null;
		sFilePath     = null;
		//Bug Id 75783, start
		sNote	     	  = null;
		bMultipleSelected = false;
		bShowUpdateNoteOptions	  = false;
		//Bug Id 75783, end

		super.doReset();
	}


	public ASPPoolElement clone(Object obj) throws FndException
	{
		ApprovalCurrentStepDocApproveOvw page = (ApprovalCurrentStepDocApproveOvw)(super.clone(obj));

		// Initializing mutable attributes
		page.trans         = null;
		page.bConfirm      = false;
		page.bHeadShow     = null;
		page.bAddNote      = null;
		page.bAddNote2     = null;
		page.bTranferToEDM = false;
		page.sMessage      = null;
		page.confirm_func  = null;
		page.bodyTag       = null;
		page.user_where    = null;

		//Bug Id 75783, start
		page.sNote	      = null;
		page.bMultipleSelected= false;
		page.bShowUpdateNoteOptions      = false;
		//Bug Id 75783, end

		// Cloning immutable attributes
		page.ctx       = page.getASPContext();
		page.fmt       = page.getASPHTMLFormatter();
		page.blk       = page.getASPBlock(blk.getName());
		page.rowset    = page.blk.getASPRowSet();
		page.cmdbar    = page.blk.getASPCommandBar();
		page.tbl       = page.getASPTable(tbl.getName());
		page.lay       = page.blk.getASPBlockLayout();
		page.sFilePath = null;

		return page;
	}


	public void run()
	{
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt    = mgr.newASPHTMLFormatter();

		bHeadShow = "TRUE";
		bAddNote = "FALSE";
		bAddNote2 = "FALSE";
		sMessage = "";

		user_where      = ctx.readValue("USER_WHERE","");
      //Bug 61103
      user_parameters = ctx.readValue("USER_PARAMETERS","");

		//Bug Id 75783, start
		sNote			= ctx.readValue("NOTE", ""); 
		bMultipleSelected 	= false;
		bShowUpdateNoteOptions	= false;
		//Bug Id 75783, end

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.buttonPressed("CANCEL_NOTE"))
			cancelNote();
		//else if (mgr.buttonPressed("OK_NOTE"))
		//   okNote();  
		else if (mgr.buttonPressed("CANCEL_NOTE2"))
			cancelNote();
		//else if (mgr.buttonPressed("OK_NOTE2"))
		//   okNote2();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) || mgr.dataTransfered())
			okFind();
		else if ("OK".equals(mgr.readValue("CONFIRM")))
		{
			confirm_func = ctx.readValue("CONFIRMFUNC","onUnconfirm()");
			eval(confirm_func+";"); // SQLInjection_Safe ILSOLK 20070807
		}
		else if ("CANCEL".equals(mgr.readValue("CONFIRM")))
		{
			onUnconfirm();
		}
		else
			lay.setLayoutMode(lay.FIND_LAYOUT);

		ctx.writeValue("USER_WHERE",user_where);
      //Bug 61103
      ctx.writeValue("USER_PARAMETERS",user_parameters);

		// Bug Id 75783, start
		ctx.writeValue("NOTE", sNote);
		// Bug Id 75783, end

		adjust();
	}


	public void  validate()
	{
		//no validations so far
	}


	public void  okFind()
	{
		ASPManager mgr = getASPManager();
		mgr.createSearchURL(blk);

		trans.clear();
		ASPQuery q = trans.addEmptyQuery(blk);
		makeQuery(q);
		q.addWhereCondition("LU_NAME='DocIssue'");
		q.addWhereCondition("DOC_ISSUE_API.Get_Objstate_By_Keyref(KEY_REF)='Approval In Progress'");
		q.setOrderByClause("LU_NAME,STEP_NO");
		q.includeMeta("ALL");
		if (mgr.dataTransfered())
			q.addOrCondition( mgr.getTransferedData() );

		mgr.querySubmit(trans,blk);

		if (rowset.countRows() == 0)
			mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNODATA: No data found."));
	}


	public void makeQuery(ASPQuery q)
	{
		user_where = "";
      //Bug 61103
      user_parameters = "";
		String temp_where = "";
		ASPManager mgr = getASPManager();

       //bug 58216 starts

		if (!mgr.isEmpty(mgr.readValue("DOC_CLASS")) && mgr.getASPField("DOC_CLASS").isQueryable())
		{
			temp_where = "SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(KEY_REF,'DOC_CLASS'),1,12) LIKE ?";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("DOC_CLASS_DESC")) && mgr.getASPField("DOC_CLASS_DESC").isQueryable())
		{
			// Check if case sensitive
			if (mgr.isEmpty(mgr.readValue("__CASESS")))
				temp_where = "UPPER(DOC_CLASS_API.Get_Name(SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(KEY_REF,'DOC_CLASS'),1,12))) LIKE UPPER(?)";
			else
				temp_where = "DOC_CLASS_API.Get_Name(SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(KEY_REF,'DOC_CLASS'),1,12)) LIKE ?";

			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("DOC_NO")) && mgr.getASPField("DOC_NO").isQueryable())
		{
			temp_where = "SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(KEY_REF,'DOC_NO'),1,12) LIKE ?";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("DOC_SHEET")) && mgr.getASPField("DOC_SHEET").isQueryable())
		{
			temp_where = "SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(KEY_REF,'DOC_SHEET'),1,12) LIKE ?";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("DOC_REV")) && mgr.getASPField("DOC_REV").isQueryable())
		{
			temp_where = "SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(KEY_REF,'DOC_REV'),1,12) LIKE ?";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("CONNECTION_DESCRIPTION")) && mgr.getASPField("CONNECTION_DESCRIPTION").isQueryable())
		{
			if (mgr.isEmpty(mgr.readValue("__CASESS")))
				temp_where = "UPPER(CONNECTION_DESCRIPTION) LIKE UPPER(?)";
			else
				temp_where = "CONNECTION_DESCRIPTION LIKE ?";

			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("STEP_NO")) && mgr.getASPField("STEP_NO").isQueryable())
		{
			temp_where = "STEP_NO LIKE ?";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("PERSON_ID")) && mgr.getASPField("PERSON_ID").isQueryable())
		{
			temp_where = "PERSON_ID LIKE ?";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("PERSDESCRIPTION")) && mgr.getASPField("PERSDESCRIPTION").isQueryable())
		{
			if (mgr.isEmpty(mgr.readValue("__CASESS")))
				temp_where = "UPPER(Eng_Person_API.Get_Name_For_User(PERSON_ID)) LIKE UPPER(?)";
			else
				temp_where = "Eng_Person_API.Get_Name_For_User(PERSON_ID) LIKE ?";

			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("PREV_APPROVAL_DATE")) && mgr.getASPField("PREV_APPROVAL_DATE").isQueryable())
		{
			temp_where = "PREV_APPROVAL_DATE between add_months(?,0) and add_months(?,0)+(1-1/(24*60*60))";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("APPROVAL_DT")) && mgr.getASPField("APPROVAL_DT").isQueryable())
		{
			temp_where = "TRUNC(APPROVAL_DT,0) LIKE ?";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("SAPPROVALSTATUS")) && mgr.getASPField("SAPPROVALSTATUS").isQueryable())
		{
			temp_where = "APPROVAL_ROUTING_API.Get_Approved_Status(LU_NAME,KEY_REF) LIKE ?";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("NOTE")) && mgr.getASPField("NOTE").isQueryable())
		{
			if (mgr.isEmpty(mgr.readValue("__CASESS")))
				temp_where = "UPPER(NOTE) LIKE UPPER(?)";
			else
				temp_where = "NOTE LIKE ?";

			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		if (!mgr.isEmpty(mgr.readValue("GROUP_ID")) && mgr.getASPField("GROUP_ID").isQueryable())
		{
			temp_where = "GROUP_ID LIKE ?";
			if (mgr.isEmpty(user_where))
				user_where = temp_where;
			else
				user_where = user_where + "AND " + temp_where;
		}

		q.addWhereCondition(user_where); // SQLInjection_Safe ILSOLK 20070807
      

      if (!mgr.isEmpty(mgr.readValue("DOC_CLASS")) && mgr.getASPField("DOC_CLASS").isQueryable())
		{
         //Bug 61103
         user_parameters += "DOC_CLASS^" + mgr.readValue("DOC_CLASS") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("DOC_CLASS_DESC")) && mgr.getASPField("DOC_CLASS_DESC").isQueryable())
		{
         //Bug 61103
         user_parameters += "DOC_CLASS_DESC^" + mgr.readValue("DOC_CLASS_DESC") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("DOC_NO")) && mgr.getASPField("DOC_NO").isQueryable())
		{
         //Bug 61103
         user_parameters += "DOC_NO^" + mgr.readValue("DOC_NO") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("DOC_SHEET")) && mgr.getASPField("DOC_SHEET").isQueryable())
		{
        //Bug 61103
         user_parameters += "DOC_SHEET^" + mgr.readValue("DOC_SHEET") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("DOC_REV")) && mgr.getASPField("DOC_REV").isQueryable())
		{
         //Bug 61103
         user_parameters += "DOC_REV^" + mgr.readValue("DOC_REV") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("CONNECTION_DESCRIPTION")) && mgr.getASPField("CONNECTION_DESCRIPTION").isQueryable())
		{
         //Bug 61103
         user_parameters += "CONNECTION_DESCRIPTION^" + mgr.readValue("CONNECTION_DESCRIPTION") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("STEP_NO")) && mgr.getASPField("STEP_NO").isQueryable())
		{
         //Bug 61103
         user_parameters += "STEP_NO^" + mgr.readValue("STEP_NO") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("PERSON_ID")) && mgr.getASPField("PERSON_ID").isQueryable())
		{
         //Bug 61103
         user_parameters += "PERSON_ID^" + mgr.readValue("PERSON_ID") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("PERSDESCRIPTION")) && mgr.getASPField("PERSDESCRIPTION").isQueryable())
		{
			//Bug 61103
         user_parameters += "PERSDESCRIPTION^" + mgr.readValue("PERSDESCRIPTION") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("PREV_APPROVAL_DATE")) && mgr.getASPField("PREV_APPROVAL_DATE").isQueryable())
		{
         //Bug 61103
         user_parameters += "PREV_APPROVAL_DATE^" + mgr.readValue("PREV_APPROVAL_DATE") + "|";
         user_parameters += "PREV_APPROVAL_DATE^" + mgr.readValue("PREV_APPROVAL_DATE") + "|" ;
      }

		if (!mgr.isEmpty(mgr.readValue("APPROVAL_DT")) && mgr.getASPField("APPROVAL_DT").isQueryable())
		{
         //Bug 61103
         user_parameters += "APPROVAL_DT^" + mgr.readValue("APPROVAL_DT") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("SAPPROVALSTATUS")) && mgr.getASPField("SAPPROVALSTATUS").isQueryable())
		{
         //Bug 61103
         user_parameters += "SAPPROVALSTATUS^" + mgr.readValue("SAPPROVALSTATUS") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("NOTE")) && mgr.getASPField("NOTE").isQueryable())
		{
         //Bug 61103
         user_parameters += "NOTE^" + mgr.readValue("NOTE") + "|";
		}

		if (!mgr.isEmpty(mgr.readValue("GROUP_ID")) && mgr.getASPField("GROUP_ID").isQueryable())
		{
         //Bug 61103
         user_parameters += "GROUP_ID^" + mgr.readValue("GROUP_ID") + "|";
		}
      //Bug 61103
      addParameters(q,user_parameters);
       //bug 58216 ends.
	}

   //Bug 61103 starts
   private void addParameters(ASPQuery q,String user_parameters)
   {
      java.util.StringTokenizer paraAndValues= new StringTokenizer(user_parameters,"|");
      StringTokenizer currentValues;
      String currentParameters;
      int nParameters  = paraAndValues.countTokens(); 
      for (int i=0;i<nParameters;i++) {
         currentParameters = paraAndValues.nextToken();
         currentValues = new StringTokenizer(currentParameters,"^");
         q.addParameter(currentValues.nextToken(),currentValues.nextToken());
      }
   }
   //Bug 61103 ends


	public void  countFind()
	{
		ASPManager mgr = getASPManager();

		ASPQuery q = trans.addQuery(blk);
		q.addWhereCondition("LU_NAME='DocIssue'");
		q.addWhereCondition("DOC_ISSUE_API.Get_Objstate_By_Keyref(KEY_REF)='Approval In Progress'");
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		lay.setCountValue(toInt(rowset.getRow().getValue("N")));
		rowset.clear();
	}


	public void  newRow()
	{
		ASPManager mgr = getASPManager();

		ASPCommand cmd = trans.addEmptyCommand("MAIN","APPROVAL_ROUTING_API.New__",blk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		rowset.addRow(trans.getBuffer("MAIN/DATA"));
	}


	public void  transferToDocInfo()
	{
		ASPManager mgr = getASPManager();

		rowset.unselectRows();

		if (lay.isMultirowLayout() && rowset.selectRows() == 0)
		{
			mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOROWS: No Rows Selected."));
			return;
		}
		else if (lay.isSingleLayout())
		{
			rowset.selectRow();
		}

		ASPBuffer selected_rows = rowset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
		mgr.transferDataTo("DocIssue.page", selected_rows);
	}


	public void  onUnconfirm()
	{
		bConfirm = false;
		sMessage = "";
		ctx.writeValue("CONFIRMFUNC","");
	}


	public void okNote()
	{
		ASPManager mgr = getASPManager();
		trans.clear();
		int noOfselectedRows = 0;

		//Bug Id 75783, start
		String attr = "NOTE" + (char)31 + (mgr.isEmpty(mgr.readValue("NOTE"))? "" : mgr.readValue("NOTE")) + (char)(30);
		//Bug Id 75783, end
       

		if (lay.isMultirowLayout())
		{	// rowset is already filtered.
			rowset.first();
			rowset.setFilterOn();
			noOfselectedRows = rowset.countRows();
		}
		else
		{
			noOfselectedRows = 1;
		}



		String note = mgr.readValue("NOTE");
		for (int k=0;k<noOfselectedRows;k++)
		{
			//Bug Id 75783, start
			if ("DONOTADDCOMMENT".equals(mgr.readValue("DONOTADDCOMMENT")))
			{
			    attr = "NOTE" + (char)31 + (mgr.isEmpty(rowset.getValue("NOTE"))? "" : rowset.getValue("NOTE")) + (char)(30);
			}
			//Bug Id 75783, end

			String sql =  "SELECT OBJID, OBJVERSION FROM APPROVAL_ROUTING ";
			sql += "WHERE LU_NAME = ? AND KEY_REF = ? AND LINE_NO = ? AND STEP_NO = ?";

			ASPQuery query = trans.addQuery("OBJ_ID_VER" + k, sql);
			query.addParameter("LU_NAME", rowset.getValue("LU_NAME"));
			query.addParameter("KEY_REF", rowset.getValue("KEY_REF"));
			query.addParameter("LINE_NO", rowset.getValue("LINE_NO"));
			query.addParameter("STEP_NO", rowset.getValue("STEP_NO"));

			ASPCommand cmd = trans.addCustomCommand("MODIFY_NOTE" + k, "APPROVAL_ROUTING_API.Modify__");
			cmd.addParameter("INFO","");
			cmd.addReference("OBJID", "OBJ_ID_VER" + k + "/DATA");
			cmd.addReference("OBJVERSION", "OBJ_ID_VER" + k + "/DATA");
			cmd.addParameter("ATTR", attr);
			cmd.addParameter("ACTION","DO");

			if (lay.isMultirowLayout())
				rowset.next();
		}

		rowset.setFilterOff();
		mgr.submit(trans);
		onConfirmNextAppStep();
	}


	public void cancelFind()
	{
		ASPManager mgr = getASPManager();
		mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
	}


	public void  cancelNote()
	{
		ASPManager mgr = getASPManager();

		bAddNote  = "FALSE";
		bHeadShow = "TRUE";
		bAddNote2 = "FALSE";

		trans.clear();
		rowset.unselectRows();
		rowset.setFilterOff();
		mgr.submit(trans);
		trans.clear();
	}


	public void  okNote2()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		//Bug Id 75783, start
		String attr = "NOTE" + (char)(31) + (mgr.isEmpty(mgr.readValue("NOTE"))? "" : mgr.readValue("NOTE")) + (char)(30);
		//Bug Id 75783, end
		int noOfSelectedRows = 1;

		if (lay.isMultirowLayout())
		{
			rowset.first();
			rowset.setFilterOn();
			noOfSelectedRows = rowset.countRows();
		}

		for (int k=0; k < noOfSelectedRows;k++)
		{

			//Bug Id 75783, start
			if ("DONOTADDCOMMENT".equals(mgr.readValue("DONOTADDCOMMENT")))
			{
			    attr = "NOTE" + (char)31 + (mgr.isEmpty(rowset.getValue("NOTE"))? "" : rowset.getValue("NOTE")) + (char)(30);
			}
			//Bug Id 75783, end

			String sql =  "SELECT OBJID, OBJVERSION FROM APPROVAL_ROUTING ";
			sql += "WHERE LU_NAME = ? AND KEY_REF = ? AND LINE_NO = ? AND STEP_NO = ?";

			ASPQuery query = trans.addQuery("OBJ_ID_VER" + k, sql);
			query.addParameter("LU_NAME", rowset.getValue("LU_NAME"));
			query.addParameter("KEY_REF", rowset.getValue("KEY_REF"));
			query.addParameter("LINE_NO", rowset.getValue("LINE_NO"));
			query.addParameter("STEP_NO", rowset.getValue("STEP_NO"));

			ASPCommand cmd = trans.addCustomCommand("MODIFY_NOTE" + k, "APPROVAL_ROUTING_API.Modify__");
			cmd.addParameter("INFO", "");
			cmd.addReference("OBJID", "OBJ_ID_VER" + k + "/DATA");
			cmd.addReference("OBJVERSION", "OBJ_ID_VER" + k + "/DATA");
			cmd.addParameter("ATTR", attr);
			cmd.addParameter("ACTION", "DO");
			if (lay.isMultirowLayout())
			{
				rowset.next();
			}
		}

		rowset.setFilterOff();
		trans = mgr.perform(trans);
		trans.clear();

		onConfirmRejectStep();
	}


	public void  approveStep()
	{
		ASPManager mgr = getASPManager();
		int noOfselectedRows = 1;

		if (lay.isMultirowLayout() && rowset.selectRows() == 0)
		{
			mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOROWS: No Rows Selected."));
			return;
		}

		if (lay.isMultirowLayout())
		{
			rowset.storeSelections();
			rowset.setFilterOn();
			noOfselectedRows = rowset.countRows();
		}
		else
		{
			rowset.selectRow();
		}

		if (lay.isMultirowLayout())
		{
			rowset.first();
		}

		for (int k=0;k< noOfselectedRows;k++)
		{
			//Bug Id 75783, start
			sNote = rowset.getValue("NOTE");
			//Bug Id 75783, end

			ASPCommand cmd = trans.addCustomFunction("CHECKRIGHTS" + k,"APPROVAL_ROUTING_API.Check_Ack_Rights","RESULT");
			cmd.addParameter("LINE_NO",rowset.getValue("LINE_NO"));
			cmd.addParameter("STEP_NO",rowset.getValue("STEP_NO"));
			cmd.addParameter("PERSON_ID",rowset.getValue("PERSON_ID"));
			cmd.addParameter("LU_NAME",rowset.getValue("LU_NAME"));
			cmd.addParameter("KEY_REF",rowset.getValue("KEY_REF"));
			cmd.addParameter("GROUP_ID",rowset.getValue("GROUP_ID"));
			if (lay.isMultirowLayout())
			{
				rowset.next();
			}

			//Bug Id 75783, start
			if (noOfselectedRows > 1 && sNote != null) 
			{
			    bShowUpdateNoteOptions = true;
			}
			//Bug Id 75783, end
		}

		//Bug Id 75783, start
		//Made note empty when multiple records are selected.
		if (noOfselectedRows > 1) 
		{
		    sNote = ""; 
		    bMultipleSelected = true;

		}
		//Bug Id 75783, end


		trans = mgr.perform(trans);
		boolean bNoRights = false;
		int count= 0;
		for (int k=0;k< noOfselectedRows;k++)
		{
			String return_val = trans.getValue("CHECKRIGHTS" + k + "/DATA/RESULT");
			count++;
			if (!"TRUE".equals(return_val))
			{
				bNoRights = true;
				break;
			}

		}

		trans.clear();

		if (!bNoRights)
		{
			bHeadShow = "FALSE";
			bAddNote  = "TRUE";
			bAddNote2 = "FALSE";
		}
		else
		{
			if (rowset.countRows()==1)
			{
				mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOTAUTH: You are not authorized to approve this step"));
			}
			else if (rowset.countRows()>1)
			{
				mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOTAUTHINNO: You are not authorized to approve this step.(Record No: &1 of your selections)",(count+"")));
			}
			rowset.setFilterOff();
		}
	}


	public void onConfirmNextAppStep()
	{
		ASPManager mgr = getASPManager();
		int noOfRowsSelected = 1;

		if (lay.isMultirowLayout())
		{
			rowset.first();
			rowset.setFilterOn();
			noOfRowsSelected = rowset.countRows();         
		}

		trans.clear();

		for (int i = 0; i < noOfRowsSelected; i++)
		{
			ASPCommand cmd = trans.addCustomCommand("APPROVESTEPS"+i, "APPROVAL_ROUTING_API.Set_Next_App_Step");
			cmd.addParameter("LU_NAME", rowset.getValue("LU_NAME"));
			cmd.addParameter("KEY_REF", rowset.getValue("KEY_REF"));
			cmd.addParameter("LINE_NO", rowset.getValue("LINE_NO"));
			cmd.addParameter("STEP_NO", rowset.getValue("STEP_NO"));
			cmd.addParameter("APPSTAT_DUMMY", "APP");
			if (lay.isMultirowLayout())
			{
				rowset.next();
			}

		}

		int curr_row = 0;

		if (lay.isSingleLayout())
		{
			curr_row = rowset.getCurrentRowNo();
		}

		trans = mgr.perform(trans);

		rowset.setFilterOff();
		rowset.unselectRows();

		trans.clear();
		ASPQuery q = trans.addEmptyQuery(blk);
		q.addWhereCondition(user_where); // SQLInjection_Safe ILSOLK 20070807
      //Bug 61103
      addParameters(q,user_parameters);
		q.addWhereCondition("LU_NAME='DocIssue'");
		q.addWhereCondition("DOC_ISSUE_API.Get_Objstate_By_Keyref(KEY_REF)='Approval In Progress'");
		q.setOrderByClause("LU_NAME,STEP_NO");
		q.includeMeta("ALL");

		mgr.querySubmit(trans, blk);

		if (lay.isSingleLayout())
		{
			if (curr_row < rowset.countRows())
			{
				rowset.goTo(curr_row);
			}
			else
			{
				rowset.last();
			}
		}
	}


	public void  rejectStep()
	{
		ASPManager mgr = getASPManager();

		int noOfRowsSelected = 1;

		if (lay.isMultirowLayout() && rowset.selectRows() == 0)
		{
			mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOROWS: No Rows Selected."));
			return;
		}

		if (lay.isMultirowLayout())
		{
			rowset.setFilterOn();
			noOfRowsSelected = rowset.countRows();
		}
		else
		{
			rowset.selectRows();
		}

		int count=0;
		boolean  bAlreadyRejeted = false;

		for (int k=0;k< noOfRowsSelected;k++)
		{
			if ("REJ".equals(rowset.getValue("APPROVAL_STATUS_DB")))
			{
				bAlreadyRejeted = true;
				break;
			}
			count++;
			if (lay.isMultirowLayout())
			{
				rowset.next();
			}

		}

		if (bAlreadyRejeted)
		{
			if (rowset.countRows()==1)
			{
				mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWALREADYREJ: This step has already been rejected!."));
			}
			else if (rowset.countRows()>1)
			{
				mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWALREADYREJWITHNO: This step has already been rejected!.(Record No: &1 of your selections)",(count+"")));
			}
			rowset.setFilterOff();
			rowset.unselectRows();
		}
		else
		{
			if (lay.isMultirowLayout())
			{
				rowset.first();
			}

			for (int k=0;k< noOfRowsSelected;k++)
			{
				//Bug Id 75783, start
				sNote = rowset.getValue("NOTE");
				//Bug Id 75783, end

				ASPCommand cmd = trans.addCustomFunction("CHECKRIGHTS"+k,"APPROVAL_ROUTING_API.Check_Ack_Rights","RESULT");
				cmd.addParameter("LINE_NO",rowset.getValue("LINE_NO"));
				cmd.addParameter("STEP_NO",rowset.getValue("STEP_NO"));
				cmd.addParameter("PERSON_ID",rowset.getValue("PERSON_ID"));
				cmd.addParameter("LU_NAME",rowset.getValue("LU_NAME"));
				cmd.addParameter("KEY_REF",rowset.getValue("KEY_REF"));
				cmd.addParameter("GROUP_ID",rowset.getValue("GROUP_ID"));
				if (lay.isMultirowLayout())
				{
					rowset.next();
				}

				//Bug Id 75783, start
				if (noOfRowsSelected > 1 && sNote != null) 
				{
				    bShowUpdateNoteOptions = true;
				}
				//Bug Id 75783, end

			}

			//Bug Id 75783, start
			//Made note empty when multiple records are selected.
			if (noOfRowsSelected > 1) 
			{
			    sNote = ""; 
			    bMultipleSelected = true;

			}
			//Bug Id 75783, end

			trans = mgr.perform(trans);
			count = 0;
			boolean bNotRejRights = false;
			for (int k=0;k< noOfRowsSelected;k++)
			{
				String return_val = trans.getValue("CHECKRIGHTS" + k + "/DATA/RESULT");
				count++;

				if (!"TRUE".equals(return_val))
				{
					bNotRejRights = true;
					break;
				}

			}

			trans.clear();

			if (!bNotRejRights)
			{
				bHeadShow = "FALSE";
				bAddNote  = "FALSE";
				bAddNote2 = "TRUE";
			}
			else
			{
				if (rowset.countRows()==1)
				{
					mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWCANTREJ: You are not authorized to reject this step."));
				}
				else if (rowset.countRows()>1)
				{
					mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWCANTREJINNO: You are not authorized to approve this step.(Record No: &1 of your selections)",(count+"")));
				}

				if (lay.isMultirowLayout())
				{
					rowset.setFilterOff();
				}
			}
		}
	}


	public void  onConfirmRejectStep()
	{
		ASPManager mgr = getASPManager();
		int noOfSelectedRows = 1;

		trans.clear();

		if (lay.isMultirowLayout())
		{
			rowset.first();
			rowset.setFilterOn();
			noOfSelectedRows = rowset.countRows();//already filtered if it is in multirow
		}

		for (int k = 0; k < noOfSelectedRows; k++)
		{
			ASPCommand cmd = trans.addCustomCommand ("CHECK_STEPS" + k, "APPROVAL_ROUTING_API.Set_Next_App_Step");
			cmd.addParameter("LU_NAME",rowset.getValue("LU_NAME"));
			cmd.addParameter("KEY_REF",rowset.getValue("KEY_REF"));
			cmd.addParameter("LINE_NO",rowset.getValue("LINE_NO"));
			cmd.addParameter("STEP_NO",rowset.getValue("STEP_NO"));
			cmd.addParameter("APPSTAT_DUMMY","REJ");
			rowset.next();
		}

		int curr_row = 0;

		if (lay.isSingleLayout())
		{
			curr_row = rowset.getCurrentRowNo();
		}

		trans = mgr.perform(trans);

		if (lay.isMultirowLayout())
		{
			rowset.setFilterOff();
		}

		rowset.unselectRows();

		trans.clear();
		ASPQuery q = trans.addEmptyQuery(blk);
		q.addWhereCondition(user_where); // SQLInjection_Safe ILSOLK 20070807
      //Bug 61103
      addParameters(q,user_parameters);
		q.addWhereCondition("LU_NAME='DocIssue'");
		q.addWhereCondition("DOC_ISSUE_API.Get_Objstate_By_Keyref(KEY_REF)='Approval In Progress'");
		q.setOrderByClause("LU_NAME,STEP_NO");
		q.includeMeta("ALL");

		mgr.querySubmit(trans, blk);

		if (lay.isSingleLayout())
		{
			if (curr_row<rowset.countRows())
			{
				rowset.goTo(curr_row);
			}
			else
			{
				rowset.last();
			}
		}
	}

	public void  query()
	{
		ASPManager mgr = getASPManager();

		mgr.redirectTo("DocIssueSearch.page");
	}


	public void tranferToEdmMacro(String doc_type, String action)
	{
		ASPManager mgr = getASPManager();

		if (lay.isSingleLayout())
		{
			rowset.unselectRows();
			rowset.selectRow();
		}
		else
			rowset.storeSelections();

		ASPBuffer data = rowset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
		sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", action, doc_type, data);
		bTranferToEDM = true;
	}



	public void  viewCopy()
	{
		ASPManager mgr = getASPManager();

		if (lay.isMultirowLayout() && rowset.selectRows() == 0)
		{
			mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		tranferToEdmMacro("VIEW","VIEW");
	}


	public void  viewOriginal()
	{
		ASPManager mgr = getASPManager();

		if (lay.isMultirowLayout() && rowset.selectRows() == 0)
		{
			mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		tranferToEdmMacro("ORIGINAL","VIEW");
	}


	public void  printDocument()
	{
		ASPManager mgr = getASPManager();

		if (lay.isMultirowLayout() && rowset.selectRows() == 0)
		{
			mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		tranferToEdmMacro("ORIGINAL","PRINT");
	}



	public void copyFileTo()
	{
		ASPManager mgr = getASPManager();

		if (lay.isMultirowLayout() && rowset.selectRows() == 0)
		{
			mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		tranferToEdmMacro("ORIGINAL","GETCOPYTODIR");
	}


	public void  viewOriginalWithExternalViewer()
	{
	    //Bug Id 67336, start
	    if (CheckFileOperationEnable()) 
	    {
		    return;
	    }
	    //Bug Id 67336, end

		tranferToEdmMacro("ORIGINAL","VIEWWITHEXTVIEWER");
	}

	//Bug Id 67336, start
	private boolean CheckFileOperationEnable()
	{
	    ASPManager mgr = getASPManager();

	    if (lay.isMultirowLayout())
	    {
		rowset.storeSelections();
		rowset.setFilterOn();
		String temp = " ";
		String structure;
		if (rowset.countSelectedRows() > 1)
		{
		    for (int k = 0;k < rowset.countSelectedRows();k++)
		    {
			structure = rowset.getValue("STRUCTURE");
			if (" ".equals(temp)) 
			{
			    temp = structure;
			}
			if (!temp.equals(structure)) 
			{
			    mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
			    rowset.setFilterOff();
			    return true;
			}
			if ("1".equals(temp) && "1".equals(structure)) 
			{
			    mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
			    rowset.setFilterOff();
			    return true;
			}
			temp = structure;
			rowset.next();
		    }
		}
		rowset.setFilterOff();
	    }
	    return false;
	}
	//Bug Id 67336, end

	//
	//  Methods on Handling Strings
	//
	private int stringIndex(String mainString,String subString)
	{
		int a=mainString.length();
		int index=-1;

		for (int i=0;i<a;i++)
			if (mainString.startsWith(subString,i))
			{
				index=i;
				break;
			}

		return index;
	}


	private String replaceString(String mainString,String subString,String replaceString)
	{
		String retString = "";
		int posi;
		posi = stringIndex(mainString, subString);

		while (posi!=-1)
		{
			retString+=mainString.substring(0,posi)+replaceString;
			mainString=mainString.substring(posi+subString.length(),mainString.length());
			posi = stringIndex(mainString, subString);
		}

		return retString+mainString;
	}


	public void checkApproveSecurity() throws FndException
	{

		okNote();
	}

	public void checkRejectSecurity() throws FndException
	{

		okNote2();
	}

	public void  preDefine() throws FndException
	{
		ASPManager mgr = getASPManager();


		blk = mgr.newASPBlock("MAIN");

		blk.addField( "OBJID" ).
		setHidden();

		blk.addField( "OBJVERSION" ).
		setHidden();

		blk.addField("KEY_REF").
		setHidden().
		setMaxLength(10);

		blk.addField("KEY_VAL").
		setHidden().
		setFunction("''");

		blk.addField("DOC_TYPE").
		setFunction("'ORIGINAL'").
		setHidden().
		setMaxLength(10);

		blk.addField("DOC_CLASS").
		setSize(20).
		setMaxLength(12).
		setReadOnly().
		setUpperCase().
		setDynamicLOV("DOC_CLASS").
		setLOVProperty("TITLE",mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCLISTCLAS: List of Classes")).
		setFunction("SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(:KEY_REF,'DOC_CLASS'),1,12)").
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCCLASS: Class");

		blk.addField("DOC_CLASS_DESC").
		setSize(20).
		setMaxLength(2000).
		setReadOnly().
		setFunction("DOC_CLASS_API.Get_Name(SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(:KEY_REF,'DOC_CLASS'),1,12))").
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCCLASSDESC: Class Name");

		blk.addField("DOC_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setReadOnly().
		setFunction("SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(:KEY_REF,'DOC_NO'),1,24)").
		setDynamicLOV("DOC_TITLE").
		setLOVProperty("TITLE",mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCNUMBERS: List of Document Numbers")).
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCNO: Number");

		blk.addField("DOC_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setFunction("SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(:KEY_REF,'DOC_SHEET'),1,24)").
		setReadOnly().
		setDynamicLOV("DOC_TITLE").
		setLOVProperty("TITLE", mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCNUMBERS: List of Document Numbers")).
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCSHEET: Sheet");

		blk.addField("DOC_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setReadOnly().
		setFunction("SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(:KEY_REF,'DOC_REV'),1,24)").
		setDynamicLOV("DOC_ISSUE").
		setLOVProperty("TITLE",mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCREVISIONS: List of Document Revisions")).
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCREV: Revision");

		blk.addField("DOC_STATUS").
		setHidden().
		setFunction("DOC_ISSUE_API.Get_State_By_Keyref(:KEY_REF)");

		blk.addField("CONNECTION_DESCRIPTION").
		setSize(20).
		setMaxLength(100).
		setReadOnly().
		setFunction("APPROVAL_ROUTING_API.Get_Connection_Desc(LU_NAME,KEY_REF)").
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWCONNECTIONDESCRIPTION: Title");

		blk.addField("STEP_NO", "Number").
		setSize(20).
		setReadOnly().
		setAlignment("RIGHT").
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWSTEPNO: Step No");

		blk.addField("DESCRIPTION").
		setSize(20).
		setMaxLength(120).
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDESCRIPTION: Approval Description").
		setReadOnly();

		blk.addField("PERSON_ID").
		setSize(20).
		setMaxLength(30).
		setUpperCase().
		setReadOnly().
		setDynamicLOV("PERSON_INFO_LOV").
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWPERSONID: Person ID");

		blk.addField("PERSDESCRIPTION").
		setSize(20).
		setMaxLength(2000).
		setReadOnly().
		setFunction("Eng_Person_API.Get_Name_For_User(:PERSON_ID)").
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWPERSDESCRIPTION: Name");
		mgr.getASPField("PERSON_ID").setValidation("PERSDESCRIPTION");

		blk.addField("EDMINFO").
		setHidden().
		setFunction("''");

		blk.addField("EDMREPINFO").
		setHidden().
		setFunction("''");

		blk.addField("PREV_APPROVAL_DATE", "Date").
		setSize(20).
		setReadOnly().
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWPREVAPPROVALDATE: Earliest Date For Approval");

		blk.addField("APPROVAL_DT", "Number").
		setSize(20).
		setReadOnly().
		setFunction("TRUNC(:APPROVAL_DT,0)").
		setAlignment("RIGHT").
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNAPPROVALDT: Days Elapsed");

		blk.addField("SAPPROVALSTATUS").
		setSize(20).
		setMaxLength(200).
		setReadOnly().
		setDefaultNotVisible().
		setFunction("APPROVAL_ROUTING_API.Get_Approved_Status(:LU_NAME,:KEY_REF)").
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWSAPPROVALSTATUS: Approval Steps");

		blk.addField("APPROVAL_STATUS").
		setHidden();

		blk.addField("APPROVAL_STATUS_DB").
		setHidden().
		setFunction("Approval_Status_API.Encode(:APPROVAL_STATUS)");

		blk.addField("APPSTAT_DUMMY").
		setHidden().
		setFunction("''");

		blk.addField("NOTE").
		setSize(20).
		setMaxLength(2000).
		setDefaultNotVisible().
		setReadOnly().
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOTE: Note");

		blk.addField("LINE_NO", "Number").
		setHidden();

		blk.addField("LU_NAME").
		setHidden();

		blk.addField("GROUP_ID").
		setSize(20).
		setDynamicLOV("DOCUMENT_GROUP").
		setUpperCase().
		setLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWGRPID: Group ID");

		blk.addField("RESULT").
		setHidden().
		setFunction("''");

		blk.addField("FILE_TYPE").
		setHidden().
		setFunction("''");

		blk.addField("INFO").
		setHidden().
		setFunction("''");

		blk.addField("ATTR").
		setHidden().
		setFunction("''");

		blk.addField("ACTION").
		setHidden().
		setFunction("''");

		//Bug Id 67336, start
		blk.addField("STRUCTURE").
		setHidden().
		setFunction("DOC_TITLE_API.Get_Structure_(SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(:KEY_REF,'DOC_CLASS'),1,12),SUBSTR(CLIENT_SYS.Get_Key_Reference_Value(:KEY_REF,'DOC_NO'),1,24))");
		//Bug Id 67336, end

		blk.setView("APPROVAL_CURRENT_STEP");
		blk.defineCommand("APPROVAL_ROUTING_API","New__,Modify__,Remove__");

		rowset = blk.getASPRowSet();

		cmdbar = mgr.newASPCommandBar(blk);

		tbl = mgr.newASPTable(blk);
		tbl.setTitle(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWOVAPP: Documents To Approve"));
		tbl.enableRowSelect();

		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);

		cmdbar.disableCommand(cmdbar.NEWROW);
		cmdbar.disableCommand(cmdbar.DUPLICATEROW);
		cmdbar.disableCommand(cmdbar.DELETE);
		cmdbar.disableCommand(cmdbar.EDITROW);
		cmdbar.enableCommand(cmdbar.COUNT);
		cmdbar.defineCommand (cmdbar.CANCELFIND,"cancelFind");

		cmdbar.addSecureCustomCommand("approveStep", mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWAPPSTEP: Approve step"),"APPROVAL_ROUTING_API.Set_Next_App_Step");  //Bug Id 36076,Shthlk  //Bug Id 70286
		cmdbar.addSecureCustomCommand("rejectStep", mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWREJSTEP: Reject Step"),"APPROVAL_ROUTING_API.Set_Next_App_Step");	//Bug Id 36076,Shthlk  //Bug Id 70286
		cmdbar.addSecureCustomCommand("viewOriginal", mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWVIEVOR: View Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
		cmdbar.addSecureCustomCommand("viewCopy", mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWVIEWCO: View Copy"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
		cmdbar.addSecureCustomCommand("viewOriginalWithExternalViewer", mgr.translate("DOCMAWDOCREFERENCEVIEWOREXTVIEWER: View Document with Ext. App"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
		cmdbar.addSecureCustomCommand("printDocument", mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWPRINTDOC: Print Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
		cmdbar.addSecureCustomCommand("copyFileTo", mgr.translate("DOCMAWDOCISSUECOPYFILETO: Copy File To..."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
		cmdbar.addCustomCommand("transferToDocInfo", mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDOCI: Document Info..."));

		cmdbar.addCustomCommand("checkApproveSecurity","Checking approve security rights");
		cmdbar.disableCommand("checkApproveSecurity");
		cmdbar.addCustomCommand("checkRejectSecurity","Checking Rejecting security rights");
		cmdbar.disableCommand("checkRejectSecurity");

		cmdbar.enableMultirowAction();
		cmdbar.removeFromMultirowAction("viewOriginalWithExternalViewer");// w.a.


		appendJavaScript("function executeServerCommand(command)\n");
		appendJavaScript("{\n");
		appendJavaScript("   f.__MAIN_Perform.value = command;\n");
		appendJavaScript("   commandSet('MAIN.Perform','');\n");
		appendJavaScript("}\n");

	}


	public void  adjust()
	{
		ASPManager mgr = getASPManager();

		// Following code replaces the onLoad() with DisplayConfirmBox()
		// This will add confirm box support to the form

		bodyTag = mgr.generateBodyTag();
		if (bConfirm)
		{
			bConfirm = false;
			String re = "/javascript:onLoad/gi";
			bodyTag = replaceString(bodyTag, re, "javascript:displayConfirmBox");
			debug("debug: "+bodyTag);
		}

		if (rowset.countRows () == 0)
		{
			lay.setLayoutMode(lay.FIND_LAYOUT);
		}
	}


	protected String getDescription()
	{
		return "DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDESC: Overview - Documents To Approve";
	}


	protected String getTitle()
	{
		return "DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWTITLE: Overview - Documents To Approve";
	}


	protected AutoString getContents() throws FndException
	{
		AutoString out = getOutputStream();
		out.clear();
		ASPManager mgr = getASPManager();
		out.append("<html>\n");
		out.append("<head>");
		out.append(mgr.generateHeadTag("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWTITLE: Overview Documents To Approve"));
		out.append("</head>\n");
		out.append("<body ");
		out.append(bodyTag);
		out.append(">\n");
		out.append("<form ");
		out.append(mgr.generateFormTag());
		out.append(">\n");
		out.append("  <input type=\"hidden\" name=\"CONFIRM\" value=\"\">\n");

		out.append(mgr.startPresentation("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWTITLE: Overview Documents To Approve"));



		if ("TRUE".equals(bHeadShow))
		{
			out.append(lay.show());
		}


		else if ("TRUE".equals(bAddNote))
		{

			out.append("<table>\n");
			out.append("  <tr>\n");
			out.append("  <td>&nbsp;&nbsp;</td>\n");
			out.append("  <td>");
			out.append("  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"200\" width=\"240\">\n");
			//Bug Id 75783, start
			if (bMultipleSelected && bShowUpdateNoteOptions) 
			{
			    out.append("   <tr><td>\n");
			    out.append(fmt.drawReadLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDORADIOAPP: Note: you are about to approve several steps and some of them have comments already."));
			    out.append("   </tr></td>\n");
			    out.append("   <tr><td>\n");
			    out.append("   </tr></td>\n");
			    out.append("   <tr><td>\n");
			    out.append(fmt.drawRadio("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDONTRADIO: Don't add any comment (keep existing comment if any)", "DONOTADDCOMMENT", "DONOTADDCOMMENT", false, "OnClick='handleApproveButton(\"DONOTADDCOMMENT\");'"));
			    out.append("   </tr></td>\n");
			    out.append("   <tr><td>\n");
			    out.append(fmt.drawRadio("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDORADIO: Add the same comment to all", "DOADDCOMMENT", "DOADDCOMMENT", false, "OnClick='handleApproveButton(\"DOADDCOMMENT\");'"));
			    out.append("   </tr></td>\n");
			    out.append("   <tr><td>\n");
			    out.append("   </tr></td>\n");
			}
			//Bug Id 75783, end
			out.append("   <tr><td>\n");
			out.append(fmt.drawReadLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDLGAPPCOMMENT: Enter Comment:"));
			out.append("   </td></tr>       \n");
			out.append("   <tr>\n");
			out.append("    <td height=\"17\"><font class=WriteTextValue><textarea class=WriteTextValue rows=\"15\" name=\"NOTE\" cols=\"75\">");
			out.append("");
			out.append(sNote); //Bug Id 75783
			out.append("</textarea></font></td>\n");
			out.append("   </tr>       \n");
			out.append("   <tr>    \n");
			out.append("         <td align=\"right\">");
			out.append(fmt.drawSubmit("CANCEL_NOTE",mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWCANCELNOTE: Cancel"),""));
			out.append("&nbsp;                           \n");
			//out.append(fmt.drawSubmit("OK_NOTE",mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWOKNOTE:   OK  "),""));
			out.append(fmt.drawButton("OK_NOTE", mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWOKNOTE:   OK  "), "onClick=\"executeServerCommand('checkApproveSecurity')\""));
			out.append("    </tr>\n");
			out.append("  </table>  \n");
			out.append("  </td>\n");
			out.append("</table>   \n");
			out.append("  <input type=\"hidden\" name=\"__MAIN_Perform\" value=\"\">\n");
		}
		else if ("TRUE".equals(bAddNote2))
		{
			out.append("  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"200\" width=\"240\">\n");
			//Bug Id 75783, start
			if (bMultipleSelected && bShowUpdateNoteOptions) 
			{
			    out.append("   <tr><td>\n");
			    out.append(fmt.drawReadLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDORADIOREJ: Note: you are about to reject several steps and some of them have comments already."));
			    out.append("   </tr></td>\n");
			    out.append("   <tr><td>\n");
			    out.append("   </tr></td>\n");
			    out.append("   <tr><td>\n");
			    out.append(fmt.drawRadio("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDONTRADIO: Don't add any comment (keep existing comment if any)", "DONOTADDCOMMENT", "DONOTADDCOMMENT", false, "OnClick='handleRejectButton(\"DONOTADDCOMMENT\");'"));
			    out.append("   </tr></td>\n");
			    out.append("   <tr><td>\n");
			    out.append(fmt.drawRadio("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDORADIO: Add the same comment to all", "DOADDCOMMENT", "DOADDCOMMENT", false, "OnClick='handleRejectButton(\"DOADDCOMMENT\");'"));
			    out.append("   </tr></td>\n");
			    out.append("   <tr><td>\n");
			    out.append("   </tr></td>\n");
			}
			//Bug Id 75783, end
			out.append("   <tr><td>\n");
			out.append(fmt.drawReadLabel("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWDLGREJCOMMENT: Enter Comment:"));
			out.append("   </td></tr>       \n");
			out.append("   <tr>\n");
			out.append("    <td height=\"17\"><font class=WriteTextValue><textarea class=WriteTextValue rows=\"15\" name=\"NOTE\" cols=\"75\">");
			out.append("");
			out.append(sNote); //Bug Id 75783
			out.append("</textarea></font></td>\n");
			out.append("    <td height=\"36\"></td>\n");
			out.append("   </tr>       \n");
			out.append("  </table>  \n");
			out.append("  <table>\n");
			out.append("   <tr>    \n");
			out.append("         <td align=\"left\">");
			out.append(fmt.drawSubmit("CANCEL_NOTE2",mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWCANCELNOTE2: Cancel"),""));
			out.append("&nbsp;                           \n");
			//out.append(fmt.drawSubmit("OK_NOTE2",mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWOKNOTE2:   OK  "),""));
			out.append(fmt.drawButton("OK_NOTE2", mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWOKNOTE2:   OK  "), "onClick=\"executeServerCommand('checkRejectSecurity')\""));
			out.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
			out.append("    </tr>\n");
			out.append("  </table>   \n");
			out.append("  <input type=\"hidden\" name=\"__MAIN_Perform\" value=\"\">\n");
		}


		//
		//    CLIENT FUNCTIONS
		//

		// Tranfer to EdmMacro

		if (bTranferToEDM)
		{
			appendDirtyJavaScript("   window.open(\"");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
			appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
		}

		// Display confirmbox and get user response\n");
		appendDirtyJavaScript("function displayConfirmBox()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   unconfirm = (readCookie(f.__PAGE_ID.value).length>1);\n");
		appendDirtyJavaScript("   onLoad();\n");
		appendDirtyJavaScript("   if ((!unconfirm)&&(document.form.CONFIRM.value==\"\"))\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      if (confirm('");
		appendDirtyJavaScript(sMessage);
		appendDirtyJavaScript("'))\n");
		appendDirtyJavaScript("         document.form.CONFIRM.value='OK';\n");
		appendDirtyJavaScript("      else\n");
		appendDirtyJavaScript("         document.form.CONFIRM.value='CANCEL';\n");
		appendDirtyJavaScript("      submit();\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("   else\n");
		appendDirtyJavaScript("      document.form.CONFIRM.value=\"\";\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function refreshParent()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("}\n");

		//Bug Id 75783, start
		appendDirtyJavaScript("function handleApproveButton(condition)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if(condition == \"DONOTADDCOMMENT\")\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      document.form.OK_NOTE.disabled = false;\n");
		appendDirtyJavaScript("      document.form.NOTE.disabled = true;\n");
		appendDirtyJavaScript("      document.form.DOADDCOMMENT.checked = false;\n");
		appendDirtyJavaScript("	  }\n");
		appendDirtyJavaScript("   else\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      document.form.OK_NOTE.disabled = false;\n");
		appendDirtyJavaScript("      document.form.NOTE.disabled = false;\n");
		appendDirtyJavaScript("      document.form.DONOTADDCOMMENT.checked = false;\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function handleRejectButton(condition)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if(condition == \"DONOTADDCOMMENT\")\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      document.form.OK_NOTE2.disabled = false;\n");
		appendDirtyJavaScript("      document.form.NOTE.disabled = true;\n");
		appendDirtyJavaScript("      document.form.DOADDCOMMENT.checked = false;\n");
		appendDirtyJavaScript("	  }\n");
		appendDirtyJavaScript("   else\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      document.form.OK_NOTE2.disabled = false;\n");
		appendDirtyJavaScript("      document.form.NOTE.disabled = false;\n");
		appendDirtyJavaScript("      document.form.DONOTADDCOMMENT.checked = false;\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("}\n");

                if (bMultipleSelected && bShowUpdateNoteOptions && "TRUE".equals(bAddNote)) 
		{
		    appendDirtyJavaScript("      document.form.OK_NOTE.disabled = true;\n");
		}
		else if (bMultipleSelected && bShowUpdateNoteOptions && "TRUE".equals(bAddNote2)) 
		{
		    appendDirtyJavaScript("      document.form.OK_NOTE2.disabled = true;\n");
		}
		//Bug Id 75783, end

		out.append(mgr.endPresentation());
		out.append("</form>\n");
		out.append("</body>\n");
		out.append("</html>");
		return out;
	}

}
