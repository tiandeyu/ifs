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
*  File        : DocPackageId.java
*  Modified    :
*     17-04-01    dikalk   Call ID 64024 : Fixed error when selecting duplicate option in child table
*     18-04-01    bakalk   Call ID 62836 : Added Lov for PACKAGE_NO, but disabled for New Lay out.
*     18-04-01    bakalk   Call ID 62836 : Made Lov visible only for Find Layout.
*     17-05-01    Shdilk   Call Id 64971 : Added new RMB, View Document with External Viewer
*     29-05-01    Diaklk   Call Id 64971 : Added method Copy File To...
*     06-06-01    Bakalk   Call Id 66106 : Changed the title of lov for 'Responsible'.
*     29-06-01    Shdilk   Call Id 66637 : Modified editDocument
*     07-08-01    Prsalk   Call ID - 62828 Added Copy Original Document functionality.
*     30-05-02    Shthlk   Bug ID - 27768: Modified validate function to support different decimal signs
*     04-12-02    Dikalk   Removed obsolete methods, doReset() and Clone()
*     04-12-02    Dikalk   Cleaned up preDefine() (removed unnecessary setting of properties to fields,
*                          set uniform field sizes, etc.)
*     04-12-02    Dikalk   Added doc_sheet to preDefine() and all relevant methods.
*     05-12-02    Dikalk   Added validation of doc_rev when selecting a value for doc_sheet in method validate();
*     26-12-02    Dikalk   2002-2 SP3 Merge
*     01-04-03    Dikalk   Added doReset() and clone() to support Web Client 3.5.1
*     02-04-03    Dikalk   Replaced calls to getOCXString() with DocmawUtil.getClientMgrObjectStr()
*     11-09-03    Shtolk   Call Id 102913, Added a new translation constant 'DOCMAWDOCPACKAGEIDCANNOTEDIT'
*     11-09-03    Dikalk   Cleaned up some of the code. Also fixed the Copy Original Document rmb.
*     11-12-03    Bakalk   Web Alignment done.
*     18-12-03    Bakalk   some modifiction done in editdocument().( modified multirow actions).
*     12-02-04    Bakalk   Replaced document.ClientUtil.connect with __connect.
*     23-12-04    DIKALK   Call ID: 112745. Ensuring rows are selected when performing mulitrow operations
*     23-12-04    DIKALK   Cleaned up some of the code.
*     29-03-04    Bakalk   Merged SP1.
*     08-06-04    VAGULK   Added fields for Project Connections - EMPR228 Project Connections.
*     15-07-04    VAGULK   Added RMB "Connect To Activity..."
*     05-08-05    SHTHLK   Call Id 125251, Modified connectToActivity. Added connectToActivityClient and setActivitySeq.
*     05-08-05             "Disconnect from Activity" RMB was added and implemented. Made sure that "Connect To Activity..." is working. 
*     05-10-21    AMNALK   Fixed Call Id 127795.
*     05-11-30    SUKMLK   Fixed call 129255.  
*     06-02-05    KARALK   Fixed call 132925.
*     06-03-14    AMNALK   Fixed call 137267.
*     06-03-20    RUCSLK   Fixed call 137268
*     06-07-19    BAKALK   Bug ID 58216, Fixed Sql Injection.
*     07-08-09    NaLrlk   XSS Correction.
*     07-08-13    AmNilk   Eliminated SQL Injections.
*     07-08-20    ASSALK   Merged Bug 64508, Added dynamicLOV to USER_CREATED.
*     07-08-24    ASSALK   Merged Bug 66007, Added the lov page DocPackageDocNoLov.page to doc_no and validated accordingly.
*     07-11-15    AMNALK   Bug Id 67336, Added new function checkFileOperationEnable() and disabled the file operations on mixed or multiple structure documents.
*     08-03-03    VIRALK   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*     08-04-02    DULOLK   Bug Id 71824, Modified copyOriginalFiles().
*     08-04-15    DULOLK   Bug Id 71824, Added new method copyOriginal(). Modified run(), copyOriginalDocument(), preDefine() and getContents().
*     08-04-16    SHTHLK   Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*     08-04-28    DULOLK   Bug Id 71824, Modified copyOriginalDocument().
*     08-05-13    NIJALK   Bug Id 73637, Modified CheckFileOperationEnable().
*     08-05-26    NIJALK   Bug 73637, Modified editDocument(). Removed isEditable().
*     08-07-22    VIRALK   Bug 74523, Modified copyOriginalDocument()
*     09-03-26    AMNALK   Bug 81591, Modified validate().
*     09-11-04    AMNALK   Bug 85848, Modified validate() and preDefine().
*     10-04-02    VIRALK   Bug 88317, Modified copyOriginalDocument(). Restrict creation of new files for * users. 
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.edm.*;

public class DocPackageId extends DocSrv
{

	//===============================================================
	// Static constants
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocPackageId");


	//===============================================================
	// Instances created on page creation (immutable attributes)
	//===============================================================
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;
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
	private ASPBlock dummyblk;

	//===============================================================
	// Transient temporary variables (never cloned)
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPBuffer data;
	private ASPCommand cmd;
	private ASPQuery q;

	private String db_val;
	private String from_package;
	private String pack_des;
	private String new_package;
	private String new_pack_des;
	private String message;
	private String dummyType;
	private String copied_package;
	private String file_type;
	private String select_val;
	private String sFilePath;
	private String sCheckedOut;
	private String sClientFunction;
	private String cdoc_class;
	private String cdoc_no;
	private String cdoc_sheet;
	private String cdoc_rev;
	private String cfile_type;
	private String corig_doc_no;
	private String corig_doc_sheet;
	private String corig_doc_rev;
	private String cobj_id;
	private String cobj_version;
	private int itemRowNo;
	private int curRowNo;
	private boolean bTranferToEDM;
	private boolean item_duplicated;
	private boolean head_duplicated;
	private boolean showdialog;
	private boolean showConfirmConnectFiles;
	private boolean emptyFileType;
	private boolean launchFile;
	private boolean bGetConfirmation;
	private boolean bProceedToDocInfo;
        //Bug Id 71824, Start
        private boolean bCopyOnlyData;
        private boolean bCopyOriginalFiles;        
        //Bug Id 71824, End

	private String performRMB;
	private String URLString;
	private String WindowName;

	 //Bug Id 74523, Start
	private int MAX_FILE_NAME_LENGTH = 250;
	//Bug Id 74523, End

        //Bug Id 88317, Start
	private String errorMessage;
	//Bug Id 88317, End


	//===============================================================
	// Construction
	//===============================================================
	public DocPackageId(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}



	public void run() throws FndException
	{
		ASPManager mgr = getASPManager();


		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();
		fmt = mgr.newASPHTMLFormatter();
		showdialog = ctx.readFlag("SHOWDIALOG",false);
		db_val = ctx.readValue("DB_VAL","");
		from_package = mgr.readValue("FROM_PACKAGE");
		pack_des = mgr.readValue("PACK_DES");
		new_package = mgr.readValue("NEW_PACKAGE");
		new_pack_des = mgr.readValue("NEW_PACK_DES");
		curRowNo = (int)ctx.readNumber("CURROWNO",0);

		itemRowNo = (int)ctx.readNumber("ITEMROWNO",0);
		cdoc_class = ctx.readValue("CDOC_CLASS","");
		cdoc_no = ctx.readValue("CDOC_NO","");
		cdoc_sheet = ctx.readValue("CDOC_SHEET","");
		cdoc_rev = ctx.readValue("CDOC_REV","");
		cfile_type = ctx.readValue("CFILE_TYPE","");
		corig_doc_no = ctx.readValue("CORIG_DOC_NO","");
		corig_doc_sheet = ctx.readValue("CORIG_DOC_SHEET","");
		corig_doc_rev = ctx.readValue("CORIG_DOC_REV","");
		cobj_id = ctx.readValue("COBJ_ID","");
		cobj_version = ctx.readValue("COBJ_VERSION","");

		bTranferToEDM = false;
		item_duplicated = false;
		head_duplicated = false;
		message = "";
		showConfirmConnectFiles = false;
		emptyFileType = false;

		if ("".equals(db_val))
			getSelectComboValue();
		if ("CHANGE".equals(mgr.readValue("SELECT_VAL")))
			checkReadOnly();

                //Bug Id 71824, Start
		if (mgr.commandBarActivated())
		{
			String commnd = mgr.readValue("__COMMAND");

			if ("HEAD.DuplicateRow".equals(commnd))
				head_duplicated = true;

			if ("ITEM0.DuplicateRow".equals(commnd))
				item_duplicated = true;

			eval(mgr.commandBarFunction());
		}
		else if (mgr.commandLinkActivated())
			eval (mgr.commandLinkFunction());	//EVALInjections_Safe AMNILK 20070810
		else if (mgr.dataTransfered())
			search();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			search();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("PACKAGE_NO")))
			okFind();
		else if (mgr.buttonPressed("OKBUT"))
			copyPackage();
		else if (mgr.buttonPressed("CANBUT"))
			backToForm();
		else if ("TRUE".equals(mgr.readValue("REFRESH_ROW")))
			refreshCurrentRow();
		else if ("OK".equals(mgr.readValue("CONFIRMCONNECTION")))
                {
                        bCopyOnlyData = false;
                        bCopyOriginalFiles = true;
			copyOriginalDocument();
                }
                else if ("CANCEL".equals(mgr.readValue("CONFIRMCONNECTION")))
                {
                        bCopyOnlyData = true;
                        bCopyOriginalFiles = false;
                        copyOriginalDocument();
                }
                else if ("TRUE".equals(mgr.readValue("COPYORIGINALFILES"))) {
			copyOriginalFiles();
                }
		else if ("OK".equals(mgr.readValue("PROCEED_TO_DOC_INFO")))
		{
			bProceedToDocInfo = true;
			bGetConfirmation = false;
			transferToDocInfo();
		}
		else if ("CANCEL".equals(mgr.readValue("PROCEED_TO_DOC_INFO")))
		{
			bProceedToDocInfo = false;
			bGetConfirmation = false;
			itemset0.setFilterOff();
			itemset0.unselectRows();
		}
                //Bug Id 71824, End

		adjust();

		ctx.writeValue("DB_VAL",db_val);
		ctx.writeFlag("SHOWDIALOG",showdialog);
		ctx.writeNumber("CURROWNO",curRowNo);

		ctx.writeNumber("ITEMROWNO",itemRowNo);
		ctx.writeValue("CDOC_CLASS",cdoc_class);
		ctx.writeValue("CDOC_NO",cdoc_no);
		ctx.writeValue("CDOC_SHEET",cdoc_sheet);
		ctx.writeValue("CDOC_REV",cdoc_rev);
		ctx.writeValue("CFILE_TYPE",cfile_type);
		ctx.writeValue("CORIG_DOC_NO",corig_doc_no);
		ctx.writeValue("CORIG_DOC_SHEET",corig_doc_sheet);
		ctx.writeValue("CORIG_DOC_REV",corig_doc_rev);
		ctx.writeValue("COBJ_ID",cobj_id);
		ctx.writeValue("COBJ_VERSION",cobj_version);

	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

	public void  getSelectComboValue()
	{
		ASPManager mgr = getASPManager();

		String sqlText = "Select Distinct Doc_Package_Revision_API.Decode(1) ASSOCIATED_REVISION_TYPE from DOC_PACKAGE_TEMPLATE_EXTRA";
		cmd = trans.addQuery("REVTYPE", sqlText);
		trans = mgr.perform(trans);

		db_val = trans.getValue("REVTYPE/DATA/ASSOCIATED_REVISION_TYPE");
		trans.clear();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();
		String val = mgr.readValue("VALIDATE");

		if ("ASSOCIATED_REVISION_TYPE".equals(val))
		{
			dummyType = mgr.readValue("ASSOCIATED_REVISION_TYPE");

			String doc_class = mgr.readValue("DOC_CLASS");
			String doc_no = mgr.readValue("DOC_NO");
			String doc_sheet = mgr.readValue("DOC_SHEET");
			String doc_rev = mgr.readValue("DOC_REV");

         //Bug ID 45944, inoslk, start
         trans.clear();

         cmd = trans.addCustomFunction("GETSPECR","DOC_ISSUE_API.Get_Spec_Issue","DUMMY");
         cmd.addParameter("DUMMY1",doc_class);
         cmd.addParameter("DUMMY1",doc_no);
         cmd.addParameter("DUMMY1",doc_sheet);
         cmd.addParameter("DUMMY1","R");

         cmd = trans.addCustomFunction("GETSPECL","DOC_ISSUE_API.Get_Spec_Issue","DUMMY");
         cmd.addParameter("DUMMY1",doc_class);
         cmd.addParameter("DUMMY1",doc_no);
         cmd.addParameter("DUMMY1",doc_sheet);
         cmd.addParameter("DUMMY1","L");

         trans = mgr.perform(trans);
         
         //Bug Id 81591, start
	 String R_Rev = trans.getValue("GETSPECR/DATA/DUMMY");
	 String L_Rev = trans.getValue("GETSPECL/DATA/DUMMY");
	 //Bug Id 81591, end

			StringBuffer sql = new StringBuffer();

			//SQLInjections_Safe AMNILK 20070810
		
		        sql.append("Select substr(DECODE(substr(Doc_Package_Revision_API.Encode( ?");
			sql.append("),1,200),'1',?");
			sql.append(",'2',?");
			sql.append(",'3',?");
			sql.append("),1,6) DOC_REV FROM DUAL");


			trans.clear();

			ASPQuery query = trans.addQuery("GETDOCREV", sql.toString());

			query.addParameter("ASSOCIATED_REVISION_TYPE",dummyType);
			query.addParameter("DOC_REV",doc_rev);
                        //Bug Id 81591, start
			query.addParameter("DUMMY",R_Rev);
			query.addParameter("DUMMY",L_Rev);
                        //Bug Id 81591, end

			trans = mgr.validate(trans);
			doc_rev = trans.getValue("GETDOCREV/DATA/DOC_REV");

			if (mgr.isEmpty(doc_rev) || "null".equals(doc_rev))
				doc_rev = "";

			mgr.responseWrite(doc_rev + "^");
		}
		else if ("DOC_NO".equals(val))
		{
                        String sDocClass = "";
                        String sDocNo = "";
                        String sLovStr = mgr.readValue("DOC_NO");
                        int index = sLovStr.indexOf("^");
                        if (index>-1) {
                             String [] key = split(sLovStr,"^");
      
                             sDocNo = key[0];
                             sDocClass = key[1];
                             mgr.responseWrite((mgr.isEmpty(sDocClass) ? "" : sDocClass) + "^");
                             mgr.responseWrite((mgr.isEmpty(sDocNo) ? "" : sDocNo) + "^");
                        }
                        else
                        {
                             sDocNo = sLovStr;
      
                             trans.clear();
                             ASPQuery q = trans.addQuery("DOCCLASS", "SELECT DOC_CLASS FROM DOC_TITLE WHERE DOC_NO=?");
                             q.addParameter("DOC_NO",mgr.readValue("DOC_NO"));
                             trans = mgr.validate(trans);
      
                             sDocClass = trans.getValue("DOCCLASS/DATA/DOC_CLASS");
      
                             mgr.responseWrite((mgr.isEmpty(sDocClass) ? "" : sDocClass) + "^");
                             mgr.responseWrite((mgr.isEmpty(sDocNo) ? "" : sDocNo) + "^");
                             
                        }
			trans.clear();
			cmd = trans.addCustomFunction("DOCTITLE", "DOC_TITLE_API.GET_TITLE", "DOC_TITLE_TITLE");
			cmd.addParameter("DOC_CLASS",sDocClass);
			cmd.addParameter("DOC_NO",sDocNo);
			trans = mgr.validate(trans);

			String doc_title = trans.getValue("DOCTITLE/DATA/DOC_TITLE_TITLE");
			mgr.responseWrite((mgr.isEmpty(doc_title) ? "" : doc_title) + "^");

                        trans.clear();
			cmd = trans.addCustomFunction("DOCCLASSDESC","DOC_CLASS_API.GET_DOC_NAME","DOC_CLASS_DOC_NAME");
			cmd.addParameter("DOC_CLASS",sDocClass);
			trans = mgr.validate(trans);
			
                        String doc_class_desc = trans.getValue("DOCCLASSDESC/DATA/DOC_CLASS_DOC_NAME");
			mgr.responseWrite((mgr.isEmpty(doc_class_desc) ? "" : doc_class_desc) +  "^");
		}
		else if ("DOC_SHEET".equals(val))
		{
                   dummyType = mgr.readValue("ASSOCIATED_REVISION_TYPE");

		   //Bug Id 85848, start

                   String reqstr = null;
                   int startpos = 0;
                   int endpos = 0;
                   int i = 0;
                   String ar[] = new String[4];
			
                   String new_doc_sheet = mgr.readValue("DOC_SHEET","");

                   String doc_class = mgr.readValue("DOC_CLASS");
                   String doc_no = mgr.readValue("DOC_NO");
                   String doc_sheet = mgr.readValue("DOC_SHEET");
                   String doc_rev = mgr.readValue("DOC_REV");

                   String docRev = "";

                   if (new_doc_sheet.indexOf("^",0)>0)
                   {
                      for (i=0 ; i<ar.length; i++)
                      {
                         endpos = new_doc_sheet.indexOf("^",startpos);
                         reqstr = new_doc_sheet.substring(startpos,endpos);
                         ar[i] = reqstr;
                         startpos= endpos+1;
                      }
                      doc_class = ar[0];
                      doc_no = ar[1];
                      doc_sheet = ar[2];
                      doc_rev = ar[3];
                   }

                   trans.clear();
                   cmd = trans.addCustomFunction("ASSREVTYPE","Doc_Package_Revision_API.Encode","DUMMY");
                   cmd.addParameter("DUMMY1",dummyType);

                   trans = mgr.perform(trans);

                   String associated_Type_DB = trans.getValue("ASSREVTYPE/DATA/DUMMY");

                   if ("1".equals(associated_Type_DB) )
                   {
                      if (mgr.isEmpty(doc_rev)) 
                      {
                         trans.clear();

                         cmd = trans.addCustomFunction("GETSPECL","DOC_ISSUE_API.Get_Spec_Issue","DUMMY");
                         cmd.addParameter("DUMMY1",doc_class);
                         cmd.addParameter("DUMMY1",doc_no);
                         cmd.addParameter("DUMMY1",doc_sheet);
                         cmd.addParameter("DUMMY1","L");

                         trans = mgr.perform(trans);

                         docRev = trans.getValue("GETSPECL/DATA/DUMMY");
                      }
                      else
                         docRev = doc_rev;
                   }
                   else
                   {
                      //Bug ID 45944, inoslk, start
                      trans.clear();

                      cmd = trans.addCustomFunction("GETSPECR","DOC_ISSUE_API.Get_Spec_Issue","DUMMY");
                      cmd.addParameter("DUMMY1",doc_class);
                      cmd.addParameter("DUMMY1",doc_no);
                      cmd.addParameter("DUMMY1",doc_sheet);
                      cmd.addParameter("DUMMY1","R");

                      cmd = trans.addCustomFunction("GETSPECL","DOC_ISSUE_API.Get_Spec_Issue","DUMMY");
                      cmd.addParameter("DUMMY1",doc_class);
                      cmd.addParameter("DUMMY1",doc_no);
                      cmd.addParameter("DUMMY1",doc_sheet);
                      cmd.addParameter("DUMMY1","L");

                      trans = mgr.perform(trans);

                      StringBuffer sql = new StringBuffer();
                      //SQLInjections_Safe AMNILK 20070810

                      sql.append("Select substr(DECODE(substr(Doc_Package_Revision_API.Encode( ?");
                      sql.append("),1,200),'1',?");
                      sql.append(",'2',?");
                      sql.append(",'3',?");
                      sql.append("),1,6) DOC_REV FROM DUAL");

                      String r_spec_issue = trans.getValue("GETSPECR/DATA/DUMMY");
                      String l_spec_issue = trans.getValue("GETSPECL/DATA/DUMMY");

                      trans.clear();

                      ASPQuery query = trans.addQuery("GETDOCREV", sql.toString());

                      query.addParameter("ASSOCIATED_REVISION_TYPE",dummyType);
                      query.addParameter("DOC_REV",doc_rev);
                      query.addParameter("DUMMY",r_spec_issue);
                      query.addParameter("DUMMY",l_spec_issue);

                      trans = mgr.validate(trans);

		      docRev = trans.getValue("GETDOCREV/DATA/DOC_REV");
		   }

		   mgr.responseWrite((mgr.isEmpty(doc_sheet) ? "" : doc_sheet) + "^" + (mgr.isEmpty(docRev) ? "" : docRev) + "^"); 
		   //Bug Id 85848, end

		
		}
		else if ("MILESTONE_NO".equals(val))
		{
			// Milestone Description
			cmd = trans.addCustomFunction("GETMILESTONEDESC", "DOC_MILESTONE_API.Get_Description", "DOC_MILESTONE_DESCRIPTION");
			cmd.addParameter("MILESTONE_PROFILE");
			cmd.addParameter("MILESTONE_NO");

			// Milestone Progress
			cmd = trans.addCustomFunction("GETMILESTONEPROGRESS","DOC_MILESTONE_API.Get_Progress","DOC_MILESTONE_PROGRESS");
			cmd.addParameter("MILESTONE_PROFILE");
			cmd.addParameter("MILESTONE_NO");

			// Milestone Duration
			cmd = trans.addCustomFunction("GETMILESTONEDURATION","DOC_MILESTONE_API.Get_Duration","DOC_MILESTONE_DURATION");
			cmd.addParameter("MILESTONE_PROFILE");
			cmd.addParameter("MILESTONE_NO");

			trans = mgr.validate(trans);
			String mile_desc = trans.getValue("GETMILESTONEDESC/DATA/DOC_MILESTONE_DESCRIPTION");
			//String sign = mgr.getConfigParameter("LANGUAGE/en/DECIMAL_SEPARATOR"); // To be removed from the foundation.
                        String sign = String.valueOf(mgr.getDecimalSeparator(DataFormatter.INTEGER)); // New method for above.
                        

			double mile_prog = Double.valueOf(trans.getValue("GETMILESTONEPROGRESS/DATA/DOC_MILESTONE_PROGRESS")).doubleValue()*100;
			String mile_prog1 = Double.toString(mile_prog ) + "%";

			int numberIndex = mile_prog1.indexOf(".");
			if (numberIndex > 0)
			{
				String Attr1 = mile_prog1.substring(0,numberIndex);
				String Attr2 = mile_prog1.substring(numberIndex+1);
				mile_prog1 = Attr1 + sign + Attr2;
			}

			double mile_dur = Double.valueOf(trans.getValue("GETMILESTONEDURATION/DATA/DOC_MILESTONE_DURATION")).doubleValue()*100;
			String mile_dur1 = Double.toString(mile_dur) + "%";

			numberIndex = mile_dur1.indexOf(".");
			if (numberIndex > 0)
			{
				String Attr1 = mile_dur1.substring(0,numberIndex);
				String Attr2 = mile_dur1.substring(numberIndex+1);
				mile_dur1 = Attr1 + sign + Attr2;
			}

			String response = (mgr.isEmpty(mile_desc) ? "" : mile_desc) + "^"+ (mgr.isEmpty(mile_prog1) ? "" : mile_prog1) + "^" +(mgr.isEmpty(mile_dur1) ? "" : mile_dur1) + "^";
			mgr.responseWrite(response);
		}
		mgr.endResponse();
	}



//=============================================================================
//  Command Bar functions for Head Part
//=============================================================================

	public void  search()
	{
		okFind();

		if (headset.countRows()>0)
		{
			okFindITEM0();
		}
	}


	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		mgr.createSearchURL(headblk);

		trans.clear();
		q = trans.addQuery(headblk);
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		q.setOrderByClause("PACKAGE_NO");
		q.includeMeta("ALL");
		mgr.querySubmit(trans,headblk);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNODATA: No data found."));
			headset.clear();
		}
		eval(headset.syncItemSets());
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


	public void  newRow()
	{
		ASPManager mgr = getASPManager();

		String package_desc = "";
		String package_no = "";
		String responsible = "";
		String copy_from = "";

		if (head_duplicated)
		{
			package_no = headset.getRow().getValue("PACKAGE_NO");
			package_desc = headset.getRow().getValue("DESCRIPTION");
			responsible = headset.getRow().getValue("RESPONSIBLE");
			copy_from = headset.getRow().getValue("COPIED_FROM_PKG");
		}
		cmd = trans.addEmptyCommand("HEAD","DOC_PACKAGE_ID_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);

		if (head_duplicated)
		{
			data = headset.getRow();
			trans.clear();

			cmd = trans.addCustomFunction("GETNAME","PERSON_INFO_API.Get_Name","RESPONSIBLE_NAME");
			cmd.addParameter("RESPONSIBLE", responsible);
			trans = mgr.perform(trans);

			data.setFieldItem("PACKAGE_NO", package_no);
			data.setFieldItem("DESCRIPTION", package_desc);
			data.setFieldItem("RESPONSIBLE", responsible);
			data.setFieldItem("COPIED_FROM_PKG", copy_from);
			data.setFieldItem("RESPONSIBLE_NAME", trans.getValue("GETNAME/DATA/RESPONSIBLE_NAME"));
			headset.setRow(data);
			itemset0.clear();
		}
		mgr.getASPField("DATE_CREATED").unsetReadOnly();
	}

//=============================================================================
//  Command Bar functions for Detail Part
//=============================================================================

	public void  okFindITEM0()
	{
		ASPManager mgr = getASPManager();

		if (headset.countRows()>0)
		{
			trans.clear();
			q = trans.addQuery(itemblk0);
			q.addWhereCondition("PACKAGE_NO = ?");
			q.addParameter("ITEM0_PACKAGE_NO", headset.getValue("PACKAGE_NO"));
			q.setOrderByClause("DOC_CLASS,DOC_NO");
			q.includeMeta("ALL");
			int headrowno = headset.getCurrentRowNo();
			mgr.submit(trans);
			headset.goTo(headrowno);
		}
		else
			itemset0.clear();
	}


	public void  countFindITEM0()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(itemblk0);
      //bug 58216 starts
      q.addWhereCondition("PACKAGE_NO = ?");
      q.addParameter("PACKAGE_NO",headset.getRow().getValue("PACKAGE_NO"));
      //bug 58216 ends.
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
		itemset0.clear();
	}


	public void  newRowITEM0()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("ITEM0","DOC_PACKAGE_TEMPLATE_API.New__",itemblk0);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM0/DATA");
		data.setFieldItem("ITEM0_PACKAGE_NO",headset.getRow().getValue("PACKAGE_NO"));
		itemset0.addRow(data);

		if (item_duplicated)
		{
			data = itemset0.getRow();
			trans.clear();

			cmd = trans.addCustomFunction("GETDOCNAME","DOC_CLASS_API.GET_DOC_NAME","DOC_CLASS_DOC_NAME");
			cmd.addParameter("DOC_CLASS",itemset0.getRow().getValue("DOC_CLASS"));

			cmd = trans.addCustomFunction("GETTITLE","DOC_TITLE_API.GET_TITLE","DOC_TITLE_TITLE");
			cmd.addParameter("DOC_CLASS",itemset0.getRow().getValue("DOC_CLASS"));
			cmd.addParameter("DOC_NO",itemset0.getRow().getValue("DOC_NO"));

			cmd = trans.addCustomFunction("GETMILEPROFDES","DOC_MILESTONE_PROFILE_API.Get_Description","MILESTONE_PROFILE_DESCR");
			cmd.addParameter("MILESTONE_PROFILE",itemset0.getRow().getValue("MILESTONE_PROFILE"));

			cmd = trans.addCustomFunction("GETMILEDES","DOC_MILESTONE_API.GET_DESCRIPTION","DOC_MILESTONE_DESCRIPTION");
			cmd.addParameter("MILESTONE_PROFILE",itemset0.getRow().getValue("MILESTONE_PROFILE"));
			cmd.addParameter("MILESTONE_NO",itemset0.getRow().getValue("MILESTONE_NO"));

			cmd = trans.addCustomFunction("GETPROGRESS","DOC_MILESTONE_API.GET_PROGRESS","DOC_MILESTONE_PROGRESS");
			cmd.addParameter("MILESTONE_PROFILE",itemset0.getRow().getValue("MILESTONE_PROFILE"));
			cmd.addParameter("MILESTONE_NO",itemset0.getRow().getValue("MILESTONE_NO"));

			cmd = trans.addCustomFunction("GETDURATION","DOC_MILESTONE_API.GET_DURATION","DOC_MILESTONE_DURATION");
			cmd.addParameter("MILESTONE_PROFILE",itemset0.getRow().getValue("MILESTONE_PROFILE"));
			cmd.addParameter("MILESTONE_NO",itemset0.getRow().getValue("MILESTONE_NO"));

			cmd = trans.addCustomFunction("ENCODEREVTYPE","Doc_Package_Revision_API.Encode","DUMMY_TYPE");
			cmd.addParameter("ASSOCIATED_REVISION_TYPE",itemset0.getRow().getValue("ASSOCIATED_REVISION_TYPE"));

			trans = mgr.perform(trans);

			data.setFieldItem("DOC_CLASS_DOC_NAME",trans.getValue("GETDOCNAME/DATA/DOC_CLASS_DOC_NAME"));
			data.setFieldItem("DOC_TITLE_TITLE",trans.getValue("GETTITLE/DATA/DOC_TITLE_TITLE"));
			data.setFieldItem("MILESTONE_PROFILE_DESCR",trans.getValue("GETMILEPROFDES/DATA/MILESTONE_PROFILE_DESCR"));
			data.setFieldItem("DOC_MILESTONE_DESCRIPTION",trans.getValue("GETMILEDES/DATA/DOC_MILESTONE_DESCRIPTION"));

			if (!mgr.isEmpty(trans.getValue("GETPROGRESS/DATA/DOC_MILESTONE_PROGRESS")))
			{
				double docMilestoneProg=Double.valueOf(trans.getValue("GETPROGRESS/DATA/DOC_MILESTONE_PROGRESS")).doubleValue()*100;
				data.setFieldItem("DOC_MILESTONE_PROGRESS",Double.toString(docMilestoneProg)+"%");
			}

			if (!mgr.isEmpty(trans.getValue("GETDURATION/DATA/DOC_MILESTONE_DURATION")))
			{
				double docMilestoneDura=Double.valueOf(trans.getValue("GETDURATION/DATA/DOC_MILESTONE_DURATION")).doubleValue()*100;
				data.setFieldItem("DOC_MILESTONE_DURATION",Double.toString(docMilestoneDura)+"%");
			}

			dummyType =  trans.getValue("ENCODEREVTYPE/DATA/DUMMY_TYPE");
			trans.clear();

			String new_doc_rev;
			if ("1".equals(dummyType))
				new_doc_rev = "";
			else
			{
				String dummy_value = null;
				if ("2".equals(dummyType))
					dummy_value = "R";
				else if ("3".equals(dummyType))
					dummy_value = "L";

				cmd = trans.addCustomFunction("GETSPECISSUE", "DOC_ISSUE_API.Get_Spec_Issue","DOC_REV");
				cmd.addParameter("DOC_CLASS", itemset0.getRow().getValue("DOC_CLASS"));
				cmd.addParameter("DOC_NO", itemset0.getRow().getValue("DOC_NO"));
				cmd.addParameter("DOC_SHEET", itemset0.getRow().getValue("DOC_SHEET"));
				cmd.addParameter("DUMMY_TYPE", dummy_value);
				trans = mgr.perform(trans);

				new_doc_rev = trans.getValue("GETSPECISSUE/DATA/DOC_REV");
			}
			data.setFieldItem("DOC_REV", new_doc_rev);
			itemset0.setRow(data);
		}
	}


	public void  refreshCurrentRow()
	{
		if (headlay.isMultirowLayout())
		{
			headset.goTo(curRowNo);
		}
		else
			headset.selectRow();

		headset.refreshRow();  //  change the edm status of the file after edit,checkin etc operations

		if (itemlay0.isMultirowLayout())
		{
			itemset0.goTo(itemRowNo);
		}
		else
			itemset0.selectRow();

		itemset0.refreshRow();
	}


//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  transferToDocInfo()
	{
		ASPManager mgr = getASPManager();
		boolean all_ok = false;
		ASPBuffer keys = null;

		if (itemlay0.isMultirowLayout() && itemset0.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNOROWS: No Rows Selected."));
			return;
		}
		else
		{
			if (itemlay0.isMultirowLayout())
				itemset0.storeSelections();
			else
				itemset0.selectRow();

			itemset0.setFilterOn();
		}
		if ((mgr.isEmpty(itemset0.getRow().getValue("DOC_NO"))) || (mgr.isEmpty(itemset0.getRow().getValue("DOC_SHEET"))) || (mgr.isEmpty(itemset0.getRow().getValue("DOC_REV"))))
		{
			if (bProceedToDocInfo == false)
				bGetConfirmation = true;
		}
		else
			all_ok = true;

		if (all_ok || bProceedToDocInfo)
		{
			if (all_ok)
				keys = itemset0.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
			else if ((mgr.isEmpty(itemset0.getRow().getValue("DOC_NO"))) && (mgr.isEmpty(itemset0.getRow().getValue("DOC_SHEET"))) && (mgr.isEmpty(itemset0.getRow().getValue("DOC_REV"))))
				keys = itemset0.getSelectedRows("DOC_CLASS");
			else if ((mgr.isEmpty(itemset0.getRow().getValue("DOC_SHEET"))) && (mgr.isEmpty(itemset0.getRow().getValue("DOC_REV"))))
				keys = itemset0.getSelectedRows("DOC_CLASS,DOC_NO");
			else if (mgr.isEmpty(itemset0.getRow().getValue("DOC_REV")))
				keys = itemset0.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET");

			keys.traceBuffer("Keys *********** ");
			itemset0.setFilterOff();
			itemset0.unselectRows();

			//ASPBuffer keys = itemset0.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
			String calling_url = mgr.getURL();
			ctx.setGlobal("CALLING_URL", calling_url);
			mgr.transferDataTo("DocIssue.page", keys);
		}
	}


	public void  copyDocumentPackage()
	{
		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
		}
		else
			headset.selectRow();

		from_package = headset.getRow().getValue("PACKAGE_NO");
		pack_des = headset.getRow().getValue("DESCRIPTION");
		showdialog=true;
	}


	public void  originalDocumentPackage()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
		}
		else
			headset.selectRow();

		String copied_package = headset.getRow().getValue("COPIED_FROM_PKG");

		if ("".equals(copied_package) || ( mgr.isEmpty(copied_package) ))
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNOORE: This pakage hasn't a original document"));
		else
		{
			trans.clear();
			q = trans.addEmptyQuery(headblk);
         //bug 58216 starts
         q.addWhereCondition("PACKAGE_NO = ?");
         q.addParameter("PACKAGE_NO",copied_package);
         //bug 58216 end
			q.includeMeta("ALL");
			mgr.querySubmit(trans,headblk);

			trans.clear();
			q = trans.addEmptyQuery(itemblk0);
         //bug 58216 starts
         q.addWhereCondition("PACKAGE_NO = ?");
         q.addParameter("PACKAGE_NO",copied_package);
         //bug 58216 end
			q.includeMeta("ALL");
			mgr.querySubmit(trans,itemblk0);
		}
	}


	//
	// File Operations
	//

	public String getFileType(String DocClass, String DocNo, String DocSheet, String DocRev, String DocType)
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addCustomFunction("FILTYPE", "EDM_FILE_API.GET_FILE_TYPE", "DUMMY");
		cmd.addParameter("DOC_CLASS", DocClass);
		cmd.addParameter("DOC_NO", DocNo);
		cmd.addParameter("DOC_SHEET", DocSheet);
		cmd.addParameter("DOC_REV", DocRev);
		cmd.addParameter("DUMMY_TYPE", "ORIGINAL");
		trans = mgr.perform(trans);
		return trans.getValue("FILTYPE/DATA/DUMMY");
	}


	public void  transferToEdmMacro(String doc_type, String action)//w.a.
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout())
		{
			itemset0.storeSelections();

		}
		else
		{
			itemset0.unselectRows();
			itemset0.selectRow();
		}
		bTranferToEDM = true;
		ASPBuffer data =  itemset0.getSelectedRows();
		sFilePath =  DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", action, doc_type, data);
		curRowNo=headset.getCurrentRowNo();
	}


	public void  transferOriginalToEdmMacro(String doc_type, String action)
	{
		ASPManager mgr = getASPManager();
		cdoc_no = corig_doc_no;
		cdoc_sheet = corig_doc_sheet;
		cdoc_rev = corig_doc_rev;

		bTranferToEDM = true;
		sFilePath  = "EdmMacro.page?DOC_CLASS="+mgr.URLEncode(cdoc_class );
		sFilePath += "&DOC_NO="+mgr.URLEncode(cdoc_no);
		sFilePath += "&DOC_SHEET="+mgr.URLEncode(cdoc_sheet);
		sFilePath += "&DOC_REV="+mgr.URLEncode(cdoc_rev);
		sFilePath += "&DOC_TYPE="+mgr.URLEncode(doc_type);
		sFilePath += "&FILE_TYPE="+mgr.URLEncode(cfile_type);
		sFilePath += "&PROCESS_DB="+mgr.URLEncode(action);
		sFilePath += "&ORIG_DOC_NO="+mgr.URLEncode(corig_doc_no);
		sFilePath += "&ORIG_DOC_SHEET="+mgr.URLEncode(corig_doc_sheet);
		sFilePath += "&ORIG_DOC_REV="+mgr.URLEncode(corig_doc_rev);
		sFilePath += "&ITEM_OBJID="+mgr.URLEncode(cobj_id );
		sFilePath += "&ITEM_OBJVERSION="+mgr.URLEncode(cobj_version);

		curRowNo=headset.getCurrentRowNo();
	}


	public void  viewCopy()
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout() && itemset0.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		transferToEdmMacro("VIEW","VIEW");
	}


	public void  viewOriginal()
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout() && itemset0.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		transferToEdmMacro("ORIGINAL","VIEW");
	}


	public void  viewOriginalWithExternalViewer()
	{
	    	//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		transferToEdmMacro("ORIGINAL","VIEWWITHEXTVIEWER");
	}


	public String  getLoginUser()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addCustomFunction("NTSESSION", "FND_SESSION_API.Get_Fnd_User", "DUMMY1");
		trans = mgr.perform(trans);
		return trans.getValue("NTSESSION/DATA/DUMMY1");
	}


	public void editDocument() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout() && itemset0.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

                //Bug 73637, Start
		transferToEdmMacro("ORIGINAL","CHECKOUT");
                //Bug 73637, End
	}


	public void  printDocument()
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout() && itemset0.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		transferToEdmMacro("ORIGINAL","PRINT");
	}


	public void  checkInDocument()
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout() && itemset0.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		transferToEdmMacro("ORIGINAL","CHECKIN");
	}


	public void  undoCheckOut()
	{
		transferToEdmMacro("ORIGINAL","UNDOCHECKOUT");
	}


	public void copyFileTo()
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout() && itemset0.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		transferToEdmMacro("ORIGINAL","GETCOPYTODIR");
	}


	public void getStateVals() throws FndException
	{
		ASPManager mgr = getASPManager();
		DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);
		sCheckedOut = dm_const.edm_file_check_out;
	}


	public void  checkReadOnly()
	{
		ASPManager mgr = getASPManager();

		itemset0.changeRow();
		if (itemset0.getRow().getValue("ASSOCIATED_REVISION_TYPE") == db_val)
			mgr.getASPField("DOC_REV").unsetReadOnly();
		else
			mgr.getASPField("DOC_REV").setReadOnly();
	}


	public void copyPackage()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addCustomCommand("COPYDOCPACK","DOC_PACKAGE_ID_API.Copy_Doc_Package_To_Templ");
		cmd.addParameter("PACKAGE_FROM",from_package);
		cmd.addParameter("NEW_PACKAGE1",mgr.readValue("NEW_PACKAGE1"));
		cmd.addParameter("NEW_PACK_DES",mgr.readValue("NEW_PACK_DES1"));
		trans = mgr.submit(trans);

		String create_package = trans.getValue("COPYDOCPACK/DATA/NEW_PACKAGE1");
		showdialog = false;
		headset.setFilterOff();

		trans.clear();
		q = trans.addEmptyQuery(headblk);
      //bug 58216 starts
      q.addWhereCondition("PACKAGE_NO = ?");
      q.addParameter("PACKAGE_NO",create_package);
      //bug 58216 end
		q.includeMeta("ALL");
		mgr.querySubmit(trans,headblk);
		okFindITEM0();
	}


	public void  backToForm()
	{
		headset.setFilterOn();
		showdialog = false;
	}

        //Bug Id 71824, Start
        public void copyOriginal()
        {
		ASPManager mgr = getASPManager();

                   // Bug id 88317 start

      trans.clear();

      cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User", "DUMMY2");
      cmd = trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User", "DUMMY1");
      cmd.addReference("DUMMY2", "FNDUSER/DATA");
            
      trans = mgr.perform(trans);
      String person_id = trans.getValue("STARUSER/DATA/DUMMY1");


      if ("*".equals(person_id)){
         mgr.showAlert(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
         bCopyOriginalFiles = false;
         bCopyOnlyData = false;
         return;
      }
      // Bug id 88317 end


		if (itemlay0.isMultirowLayout())
		{
			itemset0.goTo(itemset0.getRowSelected());
			itemRowNo = itemset0.getCurrentRowNo();
			itemset0.selectRows();
		}
		else
			itemset0.selectRow();

		if (mgr.isEmpty(itemset0.getValue("ORIGINAL_DOC_NO")) || mgr.isEmpty(itemset0.getValue("ORIGINAL_DOC_SHEET")) || mgr.isEmpty(itemset0.getValue("ORIGINAL_DOC_REV")) || !mgr.isEmpty(itemset0.getValue("DOC_NO")))
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDYOUCANTPERFORM: You cannot Copy Original Document for this record."));
			return;
		}



		String doc_class      = itemset0.getValue("DOC_CLASS");
		String file_type      = getFileType(doc_class, itemset0.getValue("DOC_NO"), itemset0.getValue("DOC_SHEET"), itemset0.getValue("DOC_REV"), "ORIGINAL");
		String orig_doc_no    = itemset0.getValue("ORIGINAL_DOC_NO");
		String orig_doc_sheet = itemset0.getValue("ORIGINAL_DOC_SHEET");
		String orig_doc_rev   = itemset0.getValue("ORIGINAL_DOC_REV");

                trans.clear();
     
                cmd = trans.addCustomFunction("FILTYPE", "Edm_File_Api.Get_File_Type", "DUMMY1");
                cmd.addParameter("DOC_CLASS",  doc_class);
                cmd.addParameter("DOC_NO",     orig_doc_no);
                cmd.addParameter("DOC_SHEET",  orig_doc_sheet);
                cmd.addParameter("DOC_REV",    orig_doc_rev);
                cmd.addParameter("DUMMY1",     "ORIGINAL");
     
                trans = mgr.perform(trans);
     
                String orig_file_type = trans.getValue("FILTYPE/DATA/DUMMY1");
     
                if (mgr.isEmpty(orig_file_type))
                     emptyFileType = true;
     
                message = mgr.translate("DOCMAWDOCPACKAGEIDCONNECTFILES: Do you wish to copy the connected files to the new document too?") + "\n" 
                           + mgr.translate("DOCMAWDOCPACKAGEIDCONNECTFILESOK: Press OK to proceed.") + "\n" 
                           + mgr.translate("DOCMAWDOCPACKAGEIDCONNECTFILESCANCEL: Press CANCEL to copy only the document data.");
                showConfirmConnectFiles = true;
        }
        
	public void copyOriginalDocument()
	{
		ASPManager mgr = getASPManager();		
                
                if (itemlay0.isMultirowLayout())
			itemset0.goTo(itemRowNo);
		else
			itemset0.selectRow();
                      
               

		String doc_class      = itemset0.getValue("DOC_CLASS");
		String file_type      = getFileType(doc_class, itemset0.getValue("DOC_NO"), itemset0.getValue("DOC_SHEET"), itemset0.getValue("DOC_REV"), "ORIGINAL");
		String orig_doc_no    = itemset0.getValue("ORIGINAL_DOC_NO");
		String orig_doc_sheet = itemset0.getValue("ORIGINAL_DOC_SHEET");
		String orig_doc_rev   = itemset0.getValue("ORIGINAL_DOC_REV");

		// Fetch the first rev and the title of the original document...
		trans.clear();
		cmd = trans.addCustomFunction("FIRSTREV", "Doc_Class_Default_API.Get_Default_Value_", "DUMMY1");
		cmd.addParameter("DOC_CLASS", doc_class);
		cmd.addParameter("DUMMY",     "DocTitle");
		cmd.addParameter("DUMMY",     "DOC_REV");

		cmd = trans.addCustomFunction("GETTITLE", "Doc_Title_Api.Get_Title", "DUMMY1");
		cmd.addParameter("DOC_CLASS",       doc_class);
		cmd.addParameter("ORIGINAL_DOC_NO", orig_doc_no);

                cmd = trans.addCustomFunction("GETUSERACCESS", "Doc_Issue_api.Get_View_Access_", "DUMMY1");
		cmd.addParameter("DOC_CLASS",       doc_class);
		cmd.addParameter("ORIGINAL_DOC_NO", orig_doc_no);
                cmd.addParameter("ORIGINAL_DOC_SHEET", orig_doc_sheet);
                cmd.addParameter("ORIGINAL_DOC_REV", orig_doc_rev);

		trans = mgr.perform(trans);

		String first_rev = trans.getValue("FIRSTREV/DATA/DUMMY1");
		String doc_title = mgr.translate("DOCMAWDOCSRVDOCPACKAGEIDCOPIEDFROM: Copied From: ") + trans.getValue("GETTITLE/DATA/DUMMY1");
		//Bug Id 74523, start
		if (MAX_FILE_NAME_LENGTH < doc_title.length()) 
		{
		    doc_title = doc_title.substring(0, MAX_FILE_NAME_LENGTH);
		}
		//Bug Id 74523, end
                String user_access = trans.getValue("GETUSERACCESS/DATA/DUMMY1");
                
                if (!user_access.equals("TRUE")) {
                   bCopyOriginalFiles = false;
                }
                if (bCopyOnlyData || user_access.equals("TRUE")) {
         		// Create a new title...
         		trans.clear();
         		cmd = trans.addCustomCommand("CREATENEWDOC", "Doc_Title_Api.Create_New_Document_");
         		cmd.addParameter("DOC_CLASS",  doc_class);
         		cmd.addParameter("DOC_NO",     "");
         		cmd.addParameter("DOC_SHEET",  orig_doc_sheet);
         		cmd.addParameter("DOC_REV",    first_rev);
         		cmd.addParameter("DUMMY1",     doc_title);
         		cmd.addParameter("DUMMY1",     "");
         		cmd.addParameter("DUMMY1",     "");
         
         		// Copy info. from the original document...
         		cmd = trans.addCustomCommand("COPYORIGGEN", "Doc_Issue_Original_Api.Copy_Original_General");
         		cmd.addParameter("DOC_CLASS",          doc_class);
         		cmd.addParameter("ORIGINAL_DOC_NO",    orig_doc_no);
         		cmd.addParameter("ORIGINAL_DOC_SHEET", orig_doc_sheet);
         		cmd.addParameter("ORIGINAL_DOC_REV",   orig_doc_rev);
         		cmd.addParameter("DOC_CLASS",          doc_class);
         		cmd.addReference("DOC_NO",             "CREATENEWDOC/DATA");
         		cmd.addParameter("DOC_SHEET",          orig_doc_sheet);
         		cmd.addParameter("DOC_REV",            first_rev);
         
         		cmd = trans.addCustomCommand("COPYSHEETGENERAL", "Doc_Issue_Sheet_Api.Copy_Sheet_General");
         		cmd.addParameter("DOC_CLASS",          doc_class);
         		cmd.addParameter("ORIGINAL_DOC_NO",    orig_doc_no);
         		cmd.addParameter("ORIGINAL_DOC_SHEET", orig_doc_sheet);
         		cmd.addParameter("ORIGINAL_DOC_REV",   orig_doc_rev);
         		cmd.addParameter("DOC_CLASS",          doc_class);
         		cmd.addReference("DOC_NO",             "CREATENEWDOC/DATA");
         		cmd.addParameter("DOC_SHEET",          orig_doc_sheet);
         		cmd.addParameter("DOC_REV",            first_rev);
         
         		cmd = trans.addCustomCommand("COPYINFOGENERAL","Doc_Issue_Api.Copy_Info_General");
         		cmd.addParameter("DOC_CLASS",          doc_class);
         		cmd.addReference("DOC_NO",             "CREATENEWDOC/DATA");
         		cmd.addParameter("DOC_SHEET",          orig_doc_sheet);
         		cmd.addParameter("DOC_REV",            first_rev);
         		cmd.addParameter("DOC_CLASS",          doc_class);
         		cmd.addParameter("ORIGINAL_DOC_NO",    orig_doc_no);
         		cmd.addParameter("ORIGINAL_DOC_SHEET", orig_doc_sheet);
         		cmd.addParameter("ORIGINAL_DOC_REV",   orig_doc_rev);
         		cmd.addParameter("DUMMY1",             "1");
         		cmd.addParameter("DUMMY1",             "1");
                        if (bCopyOriginalFiles)
                           cmd.addParameter("DUMMY1",             "1");
                        else
                           cmd.addParameter("DUMMY1",             "0");
         		cmd.addParameter("DUMMY1",             "0");


			//bug id 88317 start
         		try{
                         
         			trans = mgr.performEx(trans);
                            }
			catch(ASPLog.ExtendedAbortException e)
                       {
                         Buffer info = e.getExtendedInfo();
                         try{
                             errorMessage = info.getItem("ERROR_MESSAGE").toString();
                             errorMessage = errorMessage.substring(errorMessage.indexOf("=") + 1);
                             mgr.showAlert(errorMessage);
                             bCopyOriginalFiles = false;
                             }
			 catch(ifs.fnd.buffer.ItemNotFoundException inf)
                         {
                          errorMessage = "";
                          mgr.showAlert(errorMessage);
                          
                         }
                       return;
                       }
			//bug id 88317 End
         
         		String doc_no = trans.getValue("CREATENEWDOC/DATA/DOC_NO");
         
         		trans.clear();
         		cmd = trans.addCustomCommand("MODIFY", "Doc_Package_Template_Api.Modify__");
         		cmd.addParameter("DUMMY1");
         		cmd.addParameter("OBJID",      itemset0.getValue("OBJID"));
         		cmd.addParameter("OBJVERSION", itemset0.getValue("OBJVERSION"));
                        cmd.addParameter("DUMMY1",     "DOC_NO" + DocmawUtil.FIELD_SEPARATOR + doc_no + DocmawUtil.RECORD_SEPARATOR + "DOC_SHEET" + DocmawUtil.FIELD_SEPARATOR + orig_doc_sheet + DocmawUtil.RECORD_SEPARATOR + "DOC_REV" + DocmawUtil.FIELD_SEPARATOR + first_rev + DocmawUtil.RECORD_SEPARATOR);
         		cmd.addParameter("DUMMY1",     "DO");
         
         		trans = mgr.perform(trans);
         
         		itemset0.refreshRow();
                }
                else{
                   mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGEIDNOVIEWACCESS: You need View access to the original document in order to copy connected file(s)."));
		   return;
                }
	}
        //Bug Id 71824, End


	public void  copyOriginalFiles()
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout())
			itemset0.goTo(itemRowNo);
		else
			itemset0.selectRow();

		String doc_class      = itemset0.getValue("DOC_CLASS");
		String doc_no         = itemset0.getValue("DOC_NO");
		String doc_sheet      = itemset0.getValue("DOC_SHEET");
		String doc_rev        = itemset0.getValue("DOC_REV");
		String orig_doc_no    = itemset0.getValue("ORIGINAL_DOC_NO");
		String orig_doc_sheet = itemset0.getValue("ORIGINAL_DOC_SHEET");
		String orig_doc_rev   = itemset0.getValue("ORIGINAL_DOC_REV");

		try
                   {
                           copyFileInRepository(doc_class, orig_doc_no, orig_doc_sheet, orig_doc_rev, doc_class, doc_no, doc_sheet, doc_rev);
                   }
                   catch (FndException fnd)
                   {
                           mgr.showAlert("DOCMAWDDOCPACKAGEIDFILECOPYERROR: An error occured while trying to copy the file in FTP server.\nThe file was not copied.");
                   }
                }


	//Bug Id 67336, start
	private boolean CheckFileOperationEnable()
	{
	    ASPManager mgr = getASPManager();

	    if (itemlay0.isMultirowLayout())
	    {
		itemset0.storeSelections();
		itemset0.setFilterOn();
		String temp = " ";
		String structure;
		if (itemset0.countSelectedRows() > 1)
		{
		    for (int k = 0;k < itemset0.countSelectedRows();k++)
		    {
			structure = itemset0.getValue("STRUCTURE");
                        //Bug 73637, Start
                        if (mgr.isEmpty(structure))
                            structure = "0";
                        //Bug 73637, End
			if (" ".equals(temp)) 
			{
			    temp = structure;
			}
			if (!temp.equals(structure)) 
			{
			    mgr.showAlert(mgr.translate("DOCMAWDDOCPACKAGEIDNOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
			    itemset0.setFilterOff();
			    return true;
			}
			if ("1".equals(temp) && "1".equals(structure)) 
			{
			    mgr.showAlert(mgr.translate("DOCMAWDDOCPACKAGEIDNOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
			    itemset0.setFilterOff();
			    return true;
			}
			temp = structure;
			itemset0.next();
		    }
		}
		itemset0.setFilterOff();
	    }
	    return false;
	}
	//Bug Id 67336, end
        

	public void connectToActivity()
	{
	    ASPManager mgr = getASPManager();

	    if (headlay.isMultirowLayout())
	       headset.store();
	    else
	    {
	       headset.unselectRows();
	       headset.selectRow();
	    }
	    int nRow = headset.getCurrentRowNo();
	    headset.setNumberValue("ACTIVITY_SEQ", mgr.readNumberValue("ACTIVITY_SEQ_DOC"));
	    trans = mgr.submit(trans);
	    headset.goTo(nRow);
	    headset.refreshRow();
	}

	public void diconnectFromActivity()
	{
	    ASPManager mgr = getASPManager();
            trans.clear();
	    if (headlay.isMultirowLayout())
	       headset.store();
	    else
	    {
	       headset.unselectRows();
	       headset.selectRow();
	    }
	    int nRow = headset.getCurrentRowNo();
	    headset.setNumberValue("ACTIVITY_SEQ", mgr.readNumberValue("ACTIVITY_SEQ_DOC"));
	    trans = mgr.submit(trans);
	    headset.goTo(nRow);
	    headset.refreshRow();
	}



	public void preDefine() throws FndException
	{
		ASPManager mgr = getASPManager();

		disableConfiguration();

		headblk = mgr.newASPBlock("HEAD");

		headblk.disableDocMan();

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("PACKAGE_NO").
		setSize(20).
		setMaxLength(10).
		setMandatory().
		setReadOnly().
		setInsertable().
		setUpperCase().
		setLabel("DOCMAWDOCPACKAGEIDPACKAGENO: Package");

		headblk.addField("DESCRIPTION").
		setSize(20).
		setMaxLength(200).
		setMandatory().
		setLabel("DOCMAWDOCPACKAGEIDDESCRIPTION: Package Desc");

		headblk.addField("PROGRESS", "Number","0.##%").
		setSize(20).
		setMaxLength(5).
		setReadOnly().
                setLabel("DOCMAWDOCPACKAGEIDPROGRESS: Progress").
		setFunction("DOC_PACKAGE_ID_API.Calculate_Progress(:PACKAGE_NO)");

		headblk.addField("HOURS", "Number").
		setSize(20).
		setMaxLength(10).
		setReadOnly().
                setLabel("DOCMAWDOCPACKAGEIDHOURS: Hours").
		setFunction("DOC_PACKAGE_ID_API.Calculate_Hours_Planned(:PACKAGE_NO)");

		headblk.addField("RESPONSIBLE").
		setSize(20).
		setMaxLength(30).
                setDynamicLOV("PERSON_INFO_LOV").
		setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id")).
		setLabel("DOCMAWDOCPACKAGEIDRESPONSIBLE: Responsible");

		headblk.addField("RESPONSIBLE_NAME").
		setSize(20).
		setMaxLength(2000).
		setReadOnly().
                setLabel("DOCMAWDOCPACKAGEIDRESPONSIBLENAME: Responsible Name").
		setFunction("PERSON_INFO_API.Get_Name(:RESPONSIBLE)");
		mgr.getASPField("RESPONSIBLE").setValidation("RESPONSIBLE_NAME");

		headblk.addField("DATE_CREATED","Date").
		setSize(20).
		setMaxLength(10).
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEIDDATECREATED: Date Created");

		headblk.addField("USER_CREATED").
		setSize(20).
		setMaxLength(30).
                setDynamicLOV("PERSON_INFO_USER").
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEIDCREATEDBY: Created By");

		headblk.addField("COPIED_FROM_PKG").
		setSize(20).
		setMaxLength(10).
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEIDCOPIEDFROMPKG: Copied From Package");

		headblk.addField("COPIED_FROM_PKG_DESCRIPTION").
		setMaxLength(2000).
		setSize(20).
		setDefaultNotVisible().
		setReadOnly().
		setFunction("DOC_PACKAGE_ID_API.Get_Description(:COPIED_FROM_PKG)").
		setLabel("DOCMAWDOCPACKAGEIDCOPIEDFRMPKGDESC: Description");

		headblk.addField("PACKAGE_FROM").
		setHidden().
		setFunction("''");

		headblk.addField("NEW_PACKAGE1").
		setHidden().
		setFunction("''");

		headblk.addField("NEW_PACK_DES").
		setHidden().
		setFunction("''");

		headblk.addField("PROGRAM_ID").
		setFunction("ACTIVITY_API.Get_Program_Id(ACTIVITY_SEQ)").
		setSize(20).
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEIDPROGRAMID: Program ID");

		headblk.addField("PROGRAMID_DESC").
		setFunction("ACTIVITY_API.Get_Program_Description(ACTIVITY_SEQ)").
		setSize(20).
		setReadOnly().
		setMaxLength(2000);

		headblk.addField("PROJECT_ID").
		setFunction("ACTIVITY_API.Get_Project_Id(ACTIVITY_SEQ)").
		setSize(20).
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEIDPROJECTID: Project ID");

		headblk.addField("PROJECTID_DESC").
		setFunction("ACTIVITY_API.Get_Project_Name(ACTIVITY_SEQ)").
		setSize(20).
		setReadOnly().
		setMaxLength(2000);

		headblk.addField("SUB_PROJECT_ID").
		setFunction("ACTIVITY_API.Get_Sub_Project_Id(ACTIVITY_SEQ)").
		setSize(20).
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEIDSUBPROJECTID: Sub Project ID");

		headblk.addField("SUBPROJECTID_DESC").
		setFunction("ACTIVITY_API.Get_Sub_Project_Description(ACTIVITY_SEQ)").
		setSize(20).
		setReadOnly().
		setMaxLength(2000);

		headblk.addField("ACTIVITY_ID").
		setFunction("ACTIVITY_API.Get_Activity_No(ACTIVITY_SEQ)").
		setSize(20).
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEIDACTIVITYID: Activity ID");

		headblk.addField("ACTIVITY_DESC").
		setFunction("ACTIVITY_API.Get_Description(ACTIVITY_SEQ)").
		setSize(20).
		setReadOnly().
		setMaxLength(2000);

		headblk.addField("ACTIVITY_SEQ", "Number").
		setSize(20).
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEIDACTIVITYSEQ: Activity Seq.");


		headblk.setView("DOC_PACKAGE_ID");
		headblk.defineCommand("DOC_PACKAGE_ID_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.addSecureCustomCommand("copyDocumentPackage",mgr.translate("DOCMAWDOCPACKAGEIDCOPYDOCPACK: Copy Document Package..."),"DOC_PACKAGE_ID_API.Copy_Doc_Package_To_Templ");//Bug Id 70286
		headbar.addCustomCommand("originalDocumentPackage",mgr.translate("DOCMAWDOCPACKAGEIDOREDOCPACK: Original Document Package..."));
		headbar.addCustomCommandSeparator();
		
		if (mgr.isModuleInstalled("PROJ"))
		{
		   headbar.addSecureCustomCommand("connectToActivity",mgr.translate("DOCMAWDOCPACKAGEPROJOVWCONNECTACTIVITY: Connect To Activity..."),"DOC_PACKAGE_ID_API.Modify__"); //Bug Id 70286 
		   headbar.addSecureCustomCommand("diconnectFromActivity",mgr.translate("DOCMAWDOCPACKAGEPROJOVWDICONNECTACTIVITY: Disconnect from Activity"),"DOC_PACKAGE_ID_API.Modify__"); //Bug Id 70286 
		   headbar.defineCommand("connectToActivity","connectToActivity","connectToActivityClient");
		   headbar.defineCommand("diconnectFromActivity","diconnectFromActivity","disconnectFromActivityClient");
		   headbar.addCommandValidConditions("connectToActivity", "ACTIVITY_SEQ", "Enable", "");
                   headbar.addCommandValidConditions("diconnectFromActivity", "ACTIVITY_SEQ", "Disable", "");
		}

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("DOCMAWDOCPACKAGEIDDOCPACKAGEID: Document Package"));

		headlay = headblk.getASPBlockLayout();
		headlay.setFieldOrder("PACKAGE_ID");
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);

		headlay.defineGroup(mgr.translate("DOCMAWDOCPACKAGEIDGENERAL: General"),"PACKAGE_NO,DESCRIPTION,PROGRESS,HOURS,DATE_CREATED,USER_CREATED,RESPONSIBLE,RESPONSIBLE_NAME,COPIED_FROM_PKG,COPIED_FROM_PKG_DESCRIPTION",false,true);
		headlay.defineGroup(mgr.translate("DOCMAWDOCPACKAGEIDPROJECTCONNECTION: Project Connection"),"PROGRAM_ID,PROGRAMID_DESC,PROJECT_ID,PROJECTID_DESC,SUB_PROJECT_ID,SUBPROJECTID_DESC,ACTIVITY_ID,ACTIVITY_DESC,ACTIVITY_SEQ",true,true);
		headlay.setDialogColumns(2);

		headlay.setSimple("RESPONSIBLE_NAME");
		headlay.setSimple("COPIED_FROM_PKG_DESCRIPTION");
		headlay.setSimple("PROGRAMID_DESC");
		headlay.setSimple("PROJECTID_DESC");
		headlay.setSimple("SUBPROJECTID_DESC");
		headlay.setSimple("ACTIVITY_DESC");


		//
		// Documents
		//

		itemblk0 = mgr.newASPBlock("ITEM0");

		itemblk0.addField("ITEM0_OBJID").
		setDbName("OBJID").
		setHidden();

		itemblk0.addField("ITEM0_OBJVERSION").
		setDbName("OBJVERSION").
		setHidden();

		itemblk0.addField("ITEM0_PACKAGE_NO").
		setDbName("PACKAGE_NO").
		setHidden();

		itemblk0.addField("PACKAGE_ORDER_NO","Number").
		setHidden();

		itemblk0.addField("DOC_CLASS").
		setSize(20).
		setMaxLength(12).
		setInsertable().
		setReadOnly().
		setUpperCase().
		setMandatory().
		setDynamicLOV("DOC_CLASS").
		setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDLCLASS: List of Document Class")).
		setLabel("DOCMAWDOCPACKAGEIDDOCCLASS: Doc Class");

		itemblk0.addField("DOC_CLASS_DOC_NAME").
		setSize(20).
		setMaxLength(2000).
		setReadOnly().
		setFunction("DOC_CLASS_API.GET_DOC_NAME (:DOC_CLASS)").
		setLabel("DOCMAWDOCPACKAGEIDDOCCLASSDOCNAME: Doc Class Desc");
		mgr.getASPField("DOC_CLASS").setValidation("DOC_CLASS_DOC_NAME");

		itemblk0.addField("DOC_NO").
		setSize(20).
		setMaxLength(120).
		//setInsertable().
		setUpperCase().
		setLOV("DocPackageDocNoLov.page","DOC_CLASS").
		setCustomValidation("DOC_CLASS,DOC_NO","DOC_CLASS,DOC_NO,DOC_TITLE_TITLE,DOC_CLASS_DOC_NAME").
		setLabel("DOCMAWDOCPACKAGEIDDOCNO: Doc No");

		itemblk0.addField("DOC_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setLOV("DocNumLov.page","DOC_CLASS,DOC_NO"). //Bug Id 85848
		setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEISLOVDOCSHEET: List of Document Sheets")).
		setCustomValidation("ASSOCIATED_REVISION_TYPE,DOC_CLASS,DOC_NO,DOC_SHEET","DOC_SHEET,DOC_REV"). //Bug Id 85848
		setLabel("DOCMAWDOCPACKAGEIDDOCSHEET: Doc Sheet");

		itemblk0.addField("DOC_REV").
		setUpperCase().
		setSize(20).
		setMaxLength(12).
		setDynamicLOV("DOC_ISSUE_LOV","DOC_CLASS,DOC_NO,DOC_SHEET").
		setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDDOCREVE: List of Document Revisions")).
		setLabel("DOCMAWDOCPACKAGEIDDOCREV: Doc Rev");

		itemblk0.addField("DOC_TITLE_TITLE").
		setSize(20).
		setMaxLength(2000).
		setReadOnly().
		setFunction("DOC_TITLE_API.GET_TITLE (:DOC_CLASS,:DOC_NO)").
		setLabel("DOCMAWDOCPACKAGEIDDOCTITLETITLE: Title");

		itemblk0.addField("ASSOCIATED_REVISION_TYPE").
		setSize(20).
		setMaxLength(200).
		setMandatory().
		setSelectBox().
		enumerateValues("Doc_Package_Revision_API").
		setCustomValidation("ASSOCIATED_REVISION_TYPE,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV","DOC_REV").
		setLabel("DOCMAWDOCPACKAGEIDASSOCIATEDREVISIONTYPE: Associated Type");

		itemblk0.addField("DUMMY_TYPE").
		setHidden().
		setFunction("''");

		itemblk0.addField("STATUS").
		setSize(20).
		setFunction("DOC_ISSUE_API.Get_State(:DOC_CLASS, :DOC_NO, :DOC_SHEET, :DOC_REV)").
		setLabel("DOCMAWDOCPACKAGEIDSTATUS: Status");

		itemblk0.addField("LATEST_REVISION").
		setSize(20).
		setMaxLength(2000).
		setReadOnly().
		setUpperCase().
		setFunction("DOC_PACKAGE_TEMPLATE_API.Get_Last_Revision(:PACKAGE_NO, :PACKAGE_ORDER_NO)").
		setLabel("DOCMAWDOCPACKAGEIDLATESTREVISION: Latest Revision");

		itemblk0.addField("MILESTONE_PROFILE").
		setSize(20).
		setUpperCase().
		setMaxLength(10).
		setMandatory().
		setDynamicLOV("DOC_MILESTONE_PROFILE").
		setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDMPROF: List of Object Categories")).
		setLabel("DOCMAWDOCPACKAGEIDMILESTONEPROFILE: Doc Milestone Profile");

		itemblk0.addField("MILESTONE_PROFILE_DESCR").
		setSize(20).
		setMaxLength(2000).
		setDefaultNotVisible().
		setReadOnly().
		setFunction("DOC_MILESTONE_PROFILE_API.Get_Description(:MILESTONE_PROFILE)").
		setLabel("DOCMAWDOCPACKAGEIDMILESTONEPROFILEDESCR: Milestone Template Desc");
		mgr.getASPField("MILESTONE_PROFILE").setValidation("MILESTONE_PROFILE_DESCR");

		itemblk0.addField("MILESTONE_NO").
		setSize(20).
		setMaxLength(10).
		setDefaultNotVisible().
		setUpperCase().
		setDynamicLOV("DOC_MILESTONE","MILESTONE_PROFILE").
		setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDMPROF: List of Object Categories")).
		setCustomValidation("MILESTONE_PROFILE,MILESTONE_NO","DOC_MILESTONE_DESCRIPTION,DOC_MILESTONE_PROGRESS,DOC_MILESTONE_DURATION").
		setLabel("DOCMAWDOCPACKAGEIDMILESTONENO: Doc Milestone No");

		itemblk0.addField("DOC_MILESTONE_DESCRIPTION").
		setSize(20).
		setMaxLength(2000).
		setDefaultNotVisible().
		setReadOnly().
		setFunction("DOC_MILESTONE_API.GET_DESCRIPTION (:MILESTONE_PROFILE,:MILESTONE_NO)").
		setLabel("DOCMAWDOCPACKAGEIDDOCMILESTONEDESCRIPTION: Milestone No Desc");

		itemblk0.addField("DOC_MILESTONE_PROGRESS","Number","0.0##%").
		setSize(20).
		setMaxLength(4).
		setDefaultNotVisible().
		setReadOnly().
		setAlignment("RIGHT").
		setFunction("DOC_MILESTONE_API.GET_PROGRESS (:MILESTONE_PROFILE,:MILESTONE_NO)").
		setLabel("DOCMAWDOCPACKAGEIDDOCMILESTONEPROGRESS: Cumulative Progress");

		itemblk0.addField("PROGRESS_WEIGHT","Number").
		setSize(20).
		setMaxLength(10).
		setAlignment("RIGHT").
		setLabel("DOCMAWDOCPACKAGEIDPROGRESSWEIGHT: Planned Hours");

		itemblk0.addField("RELEASED").
		setSize(20).
		setMaxLength(2000).
		setDefaultNotVisible().
		setReadOnly().
		setFunction("DOC_ISSUE_API.Get_Released(:DOC_CLASS, :DOC_NO, :DOC_SHEET, :LATEST_REVISION)").
		setLabel("DOCMAWDOCPACKAGEIDRELEASED: Released");

		itemblk0.addField("OBSOLETE").
		setSize(20).
		setMaxLength(2000).
		setDefaultNotVisible().
		setReadOnly().
		setFunction("DOC_ISSUE_API.Get_Obsolete(:DOC_CLASS, :DOC_NO, :DOC_SHEET, :LATEST_REVISION)").
		setLabel("DOCMAWDOCPACKAGEIDOBSOLETE: Obsolete");

		itemblk0.addField("DOC_MILESTONE_DURATION","Number","0.0##%").
		setSize(20).
		setDefaultNotVisible().
		setReadOnly().
		setAlignment("RIGHT").
		setFunction("DOC_MILESTONE_API.GET_DURATION (:MILESTONE_PROFILE,:MILESTONE_NO)").
		setLabel("DOCMAWDOCPACKAGEIDDOCMILESTONEDURATION: Cumulative Duration");

		itemblk0.addField("ITEM0_DESCRIPTION").
		setDbName("DESCRIPTION").
		setSize(20).
		setMaxLength(200).
		setInsertable().
		setDefaultNotVisible().
		setLabel("DOCMAWDOCPACKAGEIDITEM0DESCRIPTION: Doc Package Line Desc");

		itemblk0.addField("USER_CHANGED").
		setSize(20).
		setMaxLength(30).
		setReadOnly().
		setDefaultNotVisible().
		setLabel("DOCMAWDOCPACKAGEIDUSERCHANGED: Modified By");

		itemblk0.addField("DATE_CHANGED","Date").
		setSize(20).
		setReadOnly().
		setDefaultNotVisible().
		setLabel("DOCMAWDOCPACKAGEIDDATECHANGED: Date Modified");

		itemblk0.addField("ITEM0_USER_CREATED").
		setDbName("USER_CREATED").
		setSize(20).
		setMaxLength(30).
		setDefaultNotVisible().
		setReadOnly().
		setLabel("DOCMAWDOCPACKAGEIDITEM0USERCREATED: Created By");

		itemblk0.addField("ITEM0_DATE_CREATED","Date").
		setDbName("DATE_CREATED").
		setSize(20).
		setReadOnly().
		setDefaultNotVisible().
		setLabel("DOCMAWDOCPACKAGEIDITEM0DATECREATED: Date Created");

		itemblk0.addField("ORIGINAL_DOC_NO").
		setHidden();

		itemblk0.addField("ORIGINAL_DOC_SHEET").
		setHidden();

		itemblk0.addField("ORIGINAL_DOC_REV").
		setHidden();

		itemblk0.addField("DURATION_WEIGHT","Number").
		setHidden();

		itemblk0.addField("DOC_PACKAGE_ID_DESCRIPTION").
		setHidden().
		setFunction("DOC_PACKAGE_ID_API.GET_DESCRIPTION (:PACKAGE_NO)");

		itemblk0.addField("DOC_PACKAGE_ID_USER_CREATED").
		setHidden().
		setFunction("DOC_PACKAGE_ID_API.GET_USER_CREATED (:PACKAGE_NO)");

		itemblk0.addField("DOC_PACKAGE_ID_COPIED_FROM_PKG").
		setHidden().
		setFunction("DOC_PACKAGE_ID_API.GET_COPIED_FROM_PKG (:PACKAGE_NO)");

		itemblk0.addField("DOC_ISSUE_FORMAT_SIZE").
		setHidden().
		setFunction("DOC_ISSUE_API.GET_FORMAT_SIZE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");

		itemblk0.addField("DOC_TYPE").
		setHidden().
		setFunction("'ORIGINAL'");

		itemblk0.addField("DUMMY").
		setHidden().
		setFunction("''");

		//Bug Id 67336, start
		itemblk0.addField("STRUCTURE").
		setHidden().
		setFunction("DOC_TITLE_API.Get_Structure_(:DOC_CLASS,:DOC_NO)");
		//Bug Id 67336, end

		itemblk0.setView("DOC_PACKAGE_TEMPLATE_EXTRA");
		itemblk0.defineCommand("DOC_PACKAGE_TEMPLATE_API","New__,Modify__,Remove__");
		itemblk0.setMasterBlock(headblk);

		itemset0 = itemblk0.getASPRowSet();

		itembar0 = mgr.newASPCommandBar(itemblk0);
		itembar0.enableCommand(itembar0.FIND);

		itembar0.addSecureCustomCommand("copyOriginal",mgr.translate("DOCMAWDOCPACKAGEIDCPORIGDOC: Copy Original Document"),"Doc_Title_Api.Create_New_Document_");  //Bug ID 71824  //Bug Id 70286
		itembar0.addCustomCommandSeparator();
		itembar0.addSecureCustomCommand("editDocument",mgr.translate("DOCMAWDOCPACKAGEIDEDITDOC: Edit Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar0.addSecureCustomCommand("checkInDocument",mgr.translate("DOCMAWDOCPACKAGEIDCHECKINDOC: Check In Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar0.addSecureCustomCommand("undoCheckOut",mgr.translate("DOCMAWDOCPACKAGEIDUNDOCHECKOUT: Undo Check Out Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar0.addSecureCustomCommand("viewOriginal",mgr.translate("DOCMAWDOCPACKAGEIDVIEVOR: View Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar0.addSecureCustomCommand("viewOriginalWithExternalViewer",mgr.translate("DOCMAWDOCREFERENCEVIEWOREXTVIEWER: View Document with Ext. App"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar0.addSecureCustomCommand("viewCopy",mgr.translate("DOCMAWDOCPACKAGEIDVIEWCO: View Copy"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar0.addSecureCustomCommand("printDocument",mgr.translate("DOCMAWDOCPACKAGEIDPRINTDOC: Print Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar0.addSecureCustomCommand("copyFileTo",mgr.translate("DOCMAWDOCISSUECOPYFILETO: Copy File To..."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar0.addCustomCommand("transferToDocInfo",mgr.translate("DOCMAWDOCPACKAGEIDDOCINFOAC: Document Info..."));

		itembar0.enableMultirowAction();//w.a.
		itembar0.removeFromMultirowAction("copyOriginal");   //Bug ID 71824
		itembar0.removeFromMultirowAction("viewOriginalWithExternalViewer");
		itembar0.removeFromMultirowAction("undoCheckOut");

		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.setTitle(mgr.translate("DOCMAWDOCPACKAGEIDDOCUME: Documents"));
		itemtbl0.enableRowSelect();

		itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
		itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
		itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");

		itemlay0 = itemblk0.getASPBlockLayout();
		itemlay0.setDialogColumns(2);
		itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
		itemblk0.setTitle(mgr.translate("DOCMAWDOCPACKAGEIDCONNDOC: Documents"));


		dummyblk = mgr.newASPBlock("DUMMY");

		dummyblk.addField("DUMMY1");
		dummyblk.addField("DUMMY2");
		dummyblk.addField("DUMMY3");
		dummyblk.addField("DUMMY4");
		dummyblk.addField("DUMMY5");
		dummyblk.addField("DUMMY6");

		super.preDefine();
	}


	public void adjust() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (headset.countRows() == 0)
		{
			headbar.disableCommand(headbar.EDITROW);
			headbar.disableCommand(headbar.DUPLICATEROW);
			headbar.disableCommand(headbar.BACK);
			headbar.disableCommand(headbar.DELETE);
			headbar.disableCommand(headbar.FORWARD);
			headbar.removeCustomCommand("copyDocumentPackage");
			headbar.removeCustomCommand("originalDocumentPackage");
		}

		if (itemlay0.isEditLayout()||itemlay0.isNewLayout())
		{

			if (db_val.equals(itemset0.getRow().getValue("ASSOCIATED_REVISION_TYPE")))				
				mgr.getASPField("DOC_REV").unsetReadOnly();
			else				
				mgr.getASPField("DOC_REV").setReadOnly();

			if (itemset0.getRow().getValue("DOC_NO")==null)
				mgr.getASPField("DOC_NO").unsetReadOnly();
			else
				mgr.getASPField("DOC_NO").setReadOnly();			  
		}

		select_val = "NOTCHANGE";
		if (headlay.isFindLayout())
			mgr.getASPField("PACKAGE_NO").setDynamicLOV("DOC_PACKAGE_ID");
	}


	protected String getDescription()
	{
		return "DOCMAWDOCPACKAGEIDTITLE: Document Package";
	}

	protected String getTitle()
	{
		return "DOCMAWDOCPACKAGEIDTITLE: Document Package";
	}

	protected AutoString getContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		AutoString out = getOutputStream();
		out.clear();
		out.append("<html>\n");
		out.append("<head>\n");
		out.append(mgr.generateHeadTag("DOCMAWDOCPACKAGEIDTITLE: Document Package"));
		out.append("</head>\n");
		out.append("<body ");
		out.append(mgr.generateBodyTag());
		out.append(">\n");
		out.append("<form ");
		out.append(mgr.generateFormTag());
		out.append(">\n");
		out.append("  <input type =\"hidden\" name=\"FROM_PACKAGE\" value=\"");
		out.append(from_package);
		out.append("\" >\n");
		out.append("  <input type =\"hidden\" name=\"PACK_DES\" value=\"");
		out.append(pack_des);
		out.append("\">\n");
		out.append("  <input type =\"hidden\" name=\"NEW_PACKAGE\" value=\"");
		out.append(new_package);
		out.append("\">\n");
		out.append("  <input type =\"hidden\" name=\"NEW_PACK_DES\" value=\"");
		out.append(new_pack_des);
		out.append("\">\n");
		out.append("  <input type =\"hidden\" name=\"SELECT_VAL\" value=\"");
		out.append(select_val);
		out.append("\">\n");
		out.append("  <input type =\"hidden\" name=\"CONFIRMCONNECTION\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"REFRESH_ROW\" value=\"FALSE\">\n");
		out.append("  <input type=\"hidden\" name=\"PROCEED_TO_DOC_INFO\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"ACTIVITY_SEQ_DOC\" value=\"\">\n");
                out.append("  <input type =\"hidden\" name=\"COPYORIGINALFILES\" value=\"\">\n");  //Bug Id 71824
		out.append(mgr.startPresentation("DOCMAWDOCPACKAGEIDTITLE: Document Package"));

		if (showdialog)
		{
			out.append("<table border=\"0\" width=\"100%\" style=\"border: 1px solid\" bgcolor=\"green\" class=\"BlockLayoutTable\">\n");
			out.append("<tr><td align=center>\n");
			out.append("<table border=\"0\" width=\"600\" height=\"1\">\n");
			out.append("<tr>\n");
			out.append("<td width=\"500\" height=\"35\"><font face=\"Verdana\" size=\"2\">");
			out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCPACKAGEIDTABLETITLE: Copy Document Package")));
			out.append("</font></td>\n");
			out.append("</tr>\n");
			out.append("<tr>\n");
			out.append("<td width=\"237\" height=\"10\"></td>\n");
			out.append("</tr>\n");
			out.append("<tr>\n");
			out.append("<td nowrap width=\"230\">");
			out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCPACKAGEIDCPACFROM: Copy Package From:")));
			out.append("</td>\n");
			out.append("<td nowrap width=\"200\">");
			out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCPACKAGEIDPACDES: Description:")));
			out.append("</td>\n");
			out.append("</tr>\n");
			out.append("<tr>\n");
			out.append("<td nowrap width=\"200\">");
			out.append(fmt.drawTextField("PACKAGE_FROM",from_package,"readOnly"));
			out.append("</td>\n");
			out.append("<td nowrap width=\"200\">");
			out.append(fmt.drawTextField("PACK_DES",pack_des,"readOnly"));
			out.append("</td>\n");
			out.append("<td nowrap>");
			out.append(fmt.drawSubmit("OKBUT",mgr.translate("DOCMAWDOCPACKAGEIDAPPOK:  Ok    "),""));
			out.append("</td>\n");
			out.append("</tr>\n");
			out.append("<tr>\n");
			out.append("<td nowrap width=\"200\">");
			out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCPACKAGEIDNPACKNAME: New Package Name:")));
			out.append("</td>\n");
			out.append("<td nowrap width=\"200\">");
			out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCPACKAGEIDNPACDES: Description:")));
			out.append("</td>\n");
			out.append("</tr>\n");
			out.append("<tr>\n");
			out.append("<td nowrap width=\"200\">");

			//initialise variables
			if (mgr.isEmpty(new_package))
				new_package="";
			if (mgr.isEmpty(new_pack_des))
				new_pack_des="";

			out.append(fmt.drawTextField("NEW_PACKAGE1",new_package,"onBlur= setToUpper()",20,10));
			out.append("</td>\n");
			out.append("<td nowrap width=\"200\">");
			out.append(fmt.drawTextField("NEW_PACK_DES1",new_pack_des,""));
			out.append("</td>\n");
			out.append("<td nowrap width=\"200\">");
			out.append(fmt.drawSubmit("CANBUT",mgr.translate("DOCMAWDOCPACKAGEIDAPPCANCEL: Cancel"),""));
			out.append("</td>\n");
			out.append("</tr>\n");
			out.append("</table>\n");
			out.append("</td></tr></table>\n");

		}
		else
		{
			if (headlay.isVisible())
			{
				out.append(headlay.show());
			}
			if (itemlay0.isVisible() && headset.countRows()>0)
			{
				out.append(itemlay0.show());
			}
		}

		//-----------------------------------------------------------------------------
		//----------------------------  CLIENT FUNCTIONS  -----------------------------
		//-----------------------------------------------------------------------------

                //Bug Id 71824, Start
		if ((showConfirmConnectFiles) && (!emptyFileType))
		{
			appendDirtyJavaScript(" if (confirm('");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(message));
			appendDirtyJavaScript("')) {\n");
			appendDirtyJavaScript("   f.CONFIRMCONNECTION.value='OK';\n");
			appendDirtyJavaScript("   submit();\n");
			appendDirtyJavaScript(" }\n");
                        appendDirtyJavaScript(" else{\n");
                        appendDirtyJavaScript("   f.CONFIRMCONNECTION.value='CANCEL';\n");
			appendDirtyJavaScript("   submit();\n");
                        appendDirtyJavaScript(" }\n");
		}
                if (bCopyOriginalFiles) {
                        appendDirtyJavaScript("   f.COPYORIGINALFILES.value='TRUE';\n");
			appendDirtyJavaScript("   submit();\n");
                }
                //Bug Id 71824, End

		appendDirtyJavaScript("function validateAssociatedRevisionType(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("    setDirty();\n");
		appendDirtyJavaScript("    if( !checkAssociatedRevisionType(i) ) return;\n");
		appendDirtyJavaScript("    r = __connect(\n");
		appendDirtyJavaScript("        '");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=ASSOCIATED_REVISION_TYPE'\n");
		appendDirtyJavaScript("        + '&ASSOCIATED_REVISION_TYPE=' + getField_('ASSOCIATED_REVISION_TYPE',i).options[getField_('ASSOCIATED_REVISION_TYPE',i).selectedIndex].value\n");
		appendDirtyJavaScript("        + '&DOC_CLASS=' + getValue_('DOC_CLASS',i)\n");
		appendDirtyJavaScript("        + '&DOC_NO=' + getValue_('DOC_NO',i)\n");
		appendDirtyJavaScript("        + '&DOC_SHEET=' + getValue_('DOC_SHEET',i)\n");
		appendDirtyJavaScript("        + '&DOC_REV=' + getValue_('DOC_REV',i)\n");
		appendDirtyJavaScript("        );\n");
		appendDirtyJavaScript("    if( checkStatus_(r,'ASSOCIATED_REVISION_TYPE',i,'Associated Type') )\n");
		appendDirtyJavaScript("    {\n");
		appendDirtyJavaScript("        assignValue_('DOC_REV',i,0);\n");
		appendDirtyJavaScript("    }\n");
		appendDirtyJavaScript("    field = document.form.ASSOCIATED_REVISION_TYPE;\n");
		appendDirtyJavaScript("    lu_name = field.options[field.selectedIndex].value;\n");
		appendDirtyJavaScript("    f.SELECT_VAL.value = \"CHANGE\";\n");
		appendDirtyJavaScript("    submit();\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("function setToUpper()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    f.NEW_PACKAGE1.value = f.NEW_PACKAGE1.value.toUpperCase();\n");
		appendDirtyJavaScript("}\n");

		if (bTranferToEDM)
		{
			appendDirtyJavaScript("window.open(\"");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
			appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
		}


		appendDirtyJavaScript("function refreshParent()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript(" document.form.REFRESH_ROW.value=\"TRUE\"\n");
		appendDirtyJavaScript(" submit() \n");
		appendDirtyJavaScript("}\n");

		if (bGetConfirmation)
		{
			appendDirtyJavaScript("if (confirm('");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.translate("DOCMAWDOCPACKAGEIDCONFIRMPROCEEDTODOCINFO: There is not enough information to find exactly one document. You may get a lot of records. Do you want to proceed?")));
			appendDirtyJavaScript("'))\n");
			appendDirtyJavaScript("   document.form.PROCEED_TO_DOC_INFO.value='OK';\n");
			appendDirtyJavaScript("else\n");
			appendDirtyJavaScript("   document.form.PROCEED_TO_DOC_INFO.value='CANCEL';\n");
			appendDirtyJavaScript("submit();\n");
		}

		appendDirtyJavaScript("function connectToActivityClient()\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("   window.open('../projw/ConObjToActivityDlg.page','CONOBJDLG','status=yes,resizable,width=750,height=550');\n");
                appendDirtyJavaScript("   return false;\n");
                appendDirtyJavaScript("}\n");


                appendDirtyJavaScript("function setActivitySeq(activitySeq)\n");
                appendDirtyJavaScript("{\n");
	        appendDirtyJavaScript("   document.form.ACTIVITY_SEQ_DOC.value= activitySeq;\n");
                appendDirtyJavaScript("   commandSet('HEAD.connectToActivity','');\n");
                appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function disconnectFromActivityClient()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   return confirm('" + mgr.translateJavaScript("DOCMAWDOCPACKAGEIDDISCACT: Are you sure you want to remove the Activity Connection ?") + "');\n");
		appendDirtyJavaScript("}\n");

		out.append(mgr.endPresentation());
		out.append("</form>\n");
		out.append("</body>\n");
		out.append("</html>");
		return out;
	}
}
