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
 *  File        : DocReference.java
 *  Modified    :
 *                ShDi    ASP2JAVA Tool  2001-03-14  - Created Using the ASP file DocReference.asp
 *                BaKa    Call Id: 64640 Added saveReturnITEM.
 *                BaKa    Call Id: 64786 Changed the method 'search' just to avoid "no data found" when detail
 *                        part has no rows.
 *    010522      Shdilk  Call Id 64971 : Added new RMB, View Document with External Viewer
 *    010525      Shdilk  Call Id 65494
 *    010528      Dikalk  Call Id 64971
 *    010618      Shdilk  Call Id 66263
 *    010703      Shdilk  Call Id 66637 : Modified editDocument
 *    020403      Mdahse  Bug 29016. Replaced call to Report_SYS.Get_Table_Comment___
 *                        with new method Doc_Reference_API.Get_Lu_Name_
 *    020411      Mdahse  Bug 29016 again. Fixed my fix...
 *    020802      Shthlk  Bug Id 31460, Added new function getObjectDes(),to get the correct Object Description.
 *    021230      DiKalk  2002-2 SP3 Merge:
 *    021230      DiKalk  021022  ThAblk   Bug 30125, Enabled FIND in itemblk. Added a new addWhereCondition in okFindITEM.
 *    021230      DiKalk  Added "No Data Found" for the item block. Added a where condition for countFindITEM.
 *    021230      DiKalk  021023  ThAblk   Bug 30126, Added Edit function to the item block. When editing, made some fields
 *    021230      DiKalk  read only depending on SURVEY_LOCKED_FLAG_DB.
 *    021230      DiKalk  021023  ThAblk   Bug 32968, Added a setFunction to SURVEY_LOCKED_FLAG_DB to get the DB value.
 *    021230      DiKalk  Made SURVEY_LOCKED_FLAG not ReadOnly. Made saveReturnITEM work for edits.
 *    021230      DiKalk  021029  ThAblk   Bug 30126, Commented call to okFindITEM() in dlgOk() to avoid a "Object has been modified by another user" error
 *    021230      DiKalk  when saving a "Create and Connect New Document".
 *    021230      DiKalk  021105  ThAblk   Bug 30126, Changed SaveReturnITEM when it's not New__."
 *    030106      inoslk  Added Doc Sheet and related changes.
 *    2003-01-09  THWILK  Bug 34675 Fixed the problem of refreshing the navigator tree manually(when going through 'create and connect document' link and when 'Save & Return' is being clicked)
 *                        when creating a document relation to an object in plant design module.
 *    2003-01-21  THWILK  Bug 34675 Fixed the problem of refreshing the navigator tree manually (when 'Save & New' is being clicked-new function added)
 *                        when creating a document relation to an object in plant design module.
 *    2003-02-17  MDAHSE  Changed history style.
 *                        Fixed call 94257 (removed the "Documents" action menu choice)
 *    2003-02-17  MDAHSE  Fixed call 94344 (moved Create and Connect New Document)
 *    2003-02-19  MDAHSE  Fixed call 94344 again. Did not work first time... :)
 *    2003-02-21  MDAHSE  Fixed call 94344 again. Renamed the menu.
 *    2003-03-05  INOSLK  Added dummy field TEMP3.
 *    2003-03-25  SHTHLK  Bug 36054 Modifed adjust() to return to the Overview mode when Itemset is 0.
 *    2003-04-02  DIKALK  Replaced calls to getOCXString() with DocmawUtil.getClientMgrObjectStr()
 *    2003-07-21  BAKALK  Added Config doc no.
 *    2003-08-01  NISILK  Removed the find when there are no records in the item set
 *    2003-08-07  INOSLK  SP4 Merge: Bug IDs 34675, 36054.
 *    2003-08-28  INOSLK  Call ID 101731: Modified doReset() and clone().
 *    2003-09-11  SHTOLK  Call ID 102913, Added a new translation constant 'DOCMAWDOCREFERENCECANNOTEDIT'
 *    2003-09-25  INOSLK  Call ID 104142 - Set ID1/ID2 Uppercase.
 *    2003-09-29  NISILK  Fixed Call 104447. Modified methods dlgOk and createNewDoc.
 *    2003-10-02  NISILK  Fixed Call 104784.
 *    2003-10-14  INOSLK  Call ID 106322 - Modified preDefine(),validate(),createNewDoc().
 *                        Added code to fetch default doc sheet in validate().
 *    2003-10-16  INOSLK  Call ID 106322 - Reflected LU name change from Doc_Def_Values_API to Doc_Number_Generator_Type_API.
 *    2003-10-16  DIKALK  Added doc_sheet to all methods that didn't have doc_sheet.
 *                        Cleaned up a lot of the code in this page
 *    2003-10-18  NISILK  Fixed Call 106882. Modified methods adjust and predefine.
 *    2003-10-21  BAKALK  Call ID 108094: Added new method okFindITEMWithErrorMsg().
 *                        Modified adjust(): Enabled find in item layout.
 *    2003-10-27  SHTOLK  Call ID 109340: Modified editDocument() & tranferToEdmMacro() methods to 'CREATENEW' file action.
 *    2003-12-05  BAKALK  Web Alignment done.Found "No data found" msg comming in unneccessary time , fixed it too.
 *    2004-02-17  BAKALK  Replaced document.ClientUtil.connect with __connect.
 *    2004-02-23  DIKALK  Call ID: 112744. Ensuring rows are selected when performing mulitrow operations
 *    2004-03-23  DIKALK  SP1-Merge.
 *    2004-06-17  DIKALK  Implemented Send Mail functionality.
 *    2004-07-27  DIKALK  Merged Bud Id 41756.
 *    2004-09-13  JAKALK  IID FIPR308A. Added new view_name in okFindObjid()
 *    2004-09-30  SUKMLK  createNewDoc changed. Now it transfers a parameter to DocTitleOvw, and recieves any
 *                        pages created, and adds them into a the current folder.
 *    2004-10-20  DIKALK  Merged Bug 46460
 *    2004-10-21  DIKALK  Merged Bug 46292
 *    2005-08-08  SHTHLK  Call Id 125866, Added quick-link to DOC_NO.
 *    2005-10-13  MDAHSE  Changed to use new drop control.
 *    2005-10-24  CHODLK  Modified method run(),Search() to accpet a query string param MCH_CODE,SITE.  [IFS Maintenance 800xA.].
 *    2005-10-31  AMNALK  Merged Bug Id 53112.
 *    2005-12-09  DIKALK  Added checks to ensure files with Unicode characters are excluded when DnDing files
 *    2006-02-12  ThWilk  Call ID 132041,Modified Validate and predefine methods.
 *    2006-03-10  CHODLK  Modified method preDefine() [Call ID 136425].
 *    060704      NIJALK  Bug Id 57719, Modified validate(), preDefine(),getContents(). Added java script function checkLocked(). 
 *    2006-07-14  Bakalk  Fixed Bug 58214, fixing security issues regarding HTML/Script errors.
 *    2006-07-19  BAKALK  Bug ID 58216, Fixed Sql Injection.
 *    2006-10-13  NIJALK  Bug 61028, Set the max length to DOC_SHEET.
 *    2007-05-08  BAKALK  Call id 141258: Added new command "Insert Existing Document". Multiple documents can be connected by this.
 *    2007-07-18  BAKALK  Call Id: 146508,modified editDocument() and isEditable().
 *    2007-07-20  BAKALK  Call Id: 145289,modified search().
 *    2007-08-01  JANSLK  Call Id: 144662, Modified and validate and preDefine so that selecting a doc no from lov brings the required
 *                        data to the window. 
 *    2007-08-09  NaLrlk  XSS Correction.
 *    2007-08-10  AmNilk  Eliminated SQLInjections security vulnerability.
 *    2007-09-11  Bakalk  Removed alert() from javascript method validateInsertData().
 *    2006-10-19  BAKALK  Bug ID 68091, Modified copyFileTo(), added a overload method for transferToEdmMacro().
 *    2007-11-15  AMNALK  Bug Id 67336, Added new function checkFileOperationEnable() and disabled the file operations on mixed or multiple structure documents.
 *    2008-04-10  SHTHLK  Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
 *    2008-09-10  AMNALK  Bug Id 76792, Modified getContents() to initialise the Drag and Drop area.
 *    2009-06-09  RUMELK  Bug Id 82596, Added variable bCloseWindow and code to close the window if request is coming from Project Documents tab when connecting new documents.
 *    2009-07-01  VIRALK  Bug Id 84478, Including "__INIT=1"  in the query string for open LOV window
 *    2009-07-03  DULOLK  Bug Id 81340, Moved check for receiving VIEW and OBJID before SEARCH since saving link with data automatically appends SEARCH into the url.
 *    2009-07-21  DULOLK  Bug Id 81340, Modified reload() by removing query submit and calling a data transfer to accomodate correct url to be saved by Save Link.
 *    2009-09-01  SHTHLK  Bug Id 85487, Moved "insertExistingDoc" and "createNewDoc" to the itembar.
 *    2009-10-02  SHTHLK  Bug Id 85361, Made modifications to filter the correct revision based on the KEEP_LAST_DOC_REV value 
 *    2010-04-02  VIRALK  Bug Id 88317, Modified createNewDoc(). Restrict creation of new files for * users. 
 *    2010-07-16  AMNALK  Bug Id 89939, Added menu actions "Add to Briefcase..." and "Go to Briefcase".
 * -----------------------APP7.5 SP7---------------------------------------------------
 *    2010-07-21  ARWILK  Bug Id 73606, Modified field BOOKING_LIST lov to filter from ID1 and ID2
 * ------------------------------------------------------------------------------------
 */


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.edm.*;
import java.util.StringTokenizer;


public class DocReference extends ASPPageProvider
{

	//===============================================================
	// Static constants
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocReference");


	//===============================================================
	// Instances created on page creation (immutable attributes)
	//===============================================================
	private ASPLog log;
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;
	private ASPForm frm;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPBlock itemblk;
	private ASPRowSet itemset;
	private ASPCommandBar itembar;
	private ASPTable itemtbl;
	private ASPBlockLayout itemlay;
	private ASPBlock dlgblk;
	private ASPRowSet dlgset;
	private ASPCommandBar dlgbar;
	private ASPBlockLayout dlglay;
	private ASPField f;
	private ASPBlock dummyblk;

	//===============================================================
	// Transient temporary variables (never cloned)
	//===============================================================

	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;
	private ASPBuffer buff;
	private ASPBuffer row;
	private ASPBuffer dataBuffer;

	private int headrowno;
	private int currrow;
	private int n;
	private int curRowNo;

	private boolean bFromOkFindObjid;
	private boolean debug_enabled;
	private boolean isNew;
	private boolean duplicated;
	private boolean userFinds;

	private boolean bTranferToEDM;
	private boolean createnew;
	private boolean bInsertExistDoc;
	private boolean bRefreshParent;

	private String root_path;
	private String sPassingParameters;
	private String nInvoiceNo;
	private String root_key_ref;
	private String root_lu_name;
	private String insertdoc;
	private String comnd;
	private String last_doc_rev;
	private String last_doc_rev_decode;
	private String searchURL;
	private String objid;
	private String luName;
	private String keyRef;
	private String dlg_doc_class;
	private String strBodyTag;
	private String txt;
	private String sFilePath;
	private String sCheckedOut;
	private String sCheckedIn;
	private String sLocalFileName;
	private String refreshParentMethod;
	private boolean bCheckOutEmpty;

	// Bug Id 82596, start
	private boolean bCloseWindow;
	// Bug Id 82596, end

	// private String retval; //Bug id 88317

	//Bug Id 89939, start
	private boolean bShowBCLov;
	private String error_msg;
	//Bug Id 89939, end

   private static final String prefix_wf_ob_dr = "__WF_OB_DR__";
   protected String strIFSCliMgrOCX;
   private boolean create_connect_def_doc;
   
	//===============================================================
	// Construction
	//===============================================================
	public DocReference(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	protected void doReset() throws FndException
	{
		//Resetting mutable attributes
		headrowno = 0;
		currrow   = 0;
		n         = 0;
		curRowNo  = 0;

		debug_enabled    = false;
		isNew            = false;
		duplicated       = false;
		userFinds        = false;
		bTranferToEDM    = false;
		createnew        = false;
		bInsertExistDoc  = false;
		bFromOkFindObjid = false;
		bRefreshParent   = false;

		trans               = null;
		cmd                 = null;
		q                   = null;
		buff                = null;
		row                 = null;
		dataBuffer          = null;

		root_path           = null;
		sPassingParameters  = null;
		nInvoiceNo          = null;
		root_key_ref        = null;
		root_lu_name        = null;
		insertdoc           = null;
		comnd               = null;
		last_doc_rev        = null;
		last_doc_rev_decode = null;
		searchURL           = null;
		objid               = null;
		luName              = null;
		keyRef              = null;
		dlg_doc_class       = null;
		strBodyTag          = null;
		txt                 = null;
		sFilePath           = null;
		sCheckedOut         = null;
		sLocalFileName      = null;
		sCheckedIn          = null;
		bCheckOutEmpty  = false;
		super.doReset();
	}

	

	public void run() throws FndException
	{
		ASPManager mgr = getASPManager();

		// MDAHSE, 2000-12-21
		// Debug flag, set this to true for
		// dbmon output on webserver
		debug_enabled = false;

		// MDAHSE, 2000-12-21
		log = mgr.getASPLog();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();
		frm = mgr.getASPForm();

		root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
		isNew = ctx.readFlag("ISNEW",false);

		// ENMALK
		sPassingParameters = ctx.readValue("SPASSINGPARAMETERS","");
		nInvoiceNo = ctx.readValue("NINVOICENO","");

		//THWILK 2003-01-09 Bug 34675
		refreshParentMethod = ctx.readValue("REFRESHNAME","");

		curRowNo = (int)ctx.readNumber("CURROWNO",0);

		// MDAHSE, 2000-12-21
		root_key_ref = "";
		root_lu_name = "";

		// MDAHSE, 2000-12-26
		// Removed variable okFindObjidRun

		duplicated = false;
		userFinds  = false;
		bTranferToEDM = false;
		createnew = false;
		bInsertExistDoc = false;
		insertdoc = "N";

		// get State Values
		DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);
		sCheckedIn = dm_const.edm_file_check_in;
		sCheckedOut = dm_const.edm_file_check_out;

		error_msg = ""; // Bug Id 89939
		strIFSCliMgrOCX        = "";

		//Bug id 88317 Start
		
		// if( !mgr.isEmpty(mgr.getQueryStringValue("execute")) )
		//    execute(); 
		//Bug id 88317 End

		if (mgr.commandBarActivated())
		{
			comnd = mgr.readValue("__COMMAND");

			if ("ITEM.Duplicate".equals(comnd))
			{
				duplicated = true;
			}
			else if ("ITEM.OkFind".equals(comnd))
			{
				userFinds = true;
			}

			eval(mgr.commandBarFunction());		//EVALInjections_Safe AMNILK 20070810
		}
		else if (mgr.commandLinkActivated())
			eval(mgr.commandLinkFunction());
		else if (mgr.dataTransfered())
		{
			ASPBuffer transferBuffer = mgr.getTransferedData();
			String transferAction = transferBuffer.getValue("ACTION");

			if (transferAction != null)
			{
				if (transferAction.equalsIgnoreCase("ADD DOCUMENTS TO FOLDER"))
				{
				   // Bug Id 82596, start
				   if("FROM_PROJECT_INFO".equalsIgnoreCase(transferBuffer.getValue("SUB_ACTION")))
				   {
				      bCloseWindow = true;
				   }
				   // Bug Id 82596, end

				   addDocumentsToFolder(transferBuffer);
				}
			}
			else
			{
				okFind();
			}
		}
                // Bug Id 81340, Start
                // MDAHSE, 2000-12-19
		// Added functionality for receiving VIEW and OBJID
		else if ((!mgr.isEmpty(mgr.getQueryStringValue("view")) || !mgr.isEmpty(mgr.getQueryStringValue("VIEW"))) && (!mgr.isEmpty(mgr.getQueryStringValue("objid")) || !mgr.isEmpty(mgr.getQueryStringValue("OBJID"))))
			okFindObjid();
                // Bug Id 81340, End
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
		{
			okFind();
			okFindITEM();
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		else if ("TRUE".equals(mgr.readValue("SELECT_INSERT_FILE")))
			insertExistingDocOk();

		else if ("TRUE".equals(mgr.readValue("REFRESH_ROW")))
			refreshCurrentRow();

		else if (!mgr.isEmpty(mgr.getQueryStringValue("PASS_DATA")))
		{
			sPassingParameters = mgr.readValue("PASS_DATA");
			nInvoiceNo = mgr.readValue("INVOICE_NO");
			searchConnections();
		}
		else if ((!mgr.isEmpty(mgr.getQueryStringValue("KEY_REF"))) && (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME"))))
			keyRefLuNameTransferred();		
		else if ("YES".equals(mgr.readValue("DRAG_AND_DROP")))
		{
		   String create_connect_def = mgr.isEmpty(mgr.readValue("CREATE_CONNECT_DEF")) ? "" : mgr.readValue("CREATE_CONNECT_DEF");
			String luName;
			String KeyRef;
			ASPBuffer fileBuffer;

			luName= headset.getRow().getValue("LU_NAME");
			keyRef= headset.getRow().getValue("KEY_REF");

			String fileNames = (mgr.readValue("FILES_DROPPED"));
			fileNames = fileNames.replaceAll("\\\\","\\\\\\\\");

			fileBuffer = mgr.newASPBuffer();
			fileBuffer.addItem("FILE_NAMES",fileNames);
			fileBuffer.addItem("LU_NAME",luName);
			fileBuffer.addItem("KEY_REF",keyRef);

			fileBuffer.traceBuffer("Buffer contents");
			if (mgr.isEmpty(create_connect_def))
			   mgr.transferDataTo("FileImport.page?FROM_OTHER=YES",fileBuffer);
			else
			   mgr.transferDataTo("FileImport.page?FROM_OTHER=YES&CREATE_CONNECT_DEF=TRUE",fileBuffer);
		}

		/* Start - IFS Maintenance 800xA Integration -- */
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")) || !mgr.isEmpty(mgr.getQueryStringValue("SITE")) )
		{
			search();
			getObjectDes();
		}
		/* End - IFS Maintenance 800xA Integration -- */

                //Bug Id 89939, start
                else if ("TRUE".equals(mgr.readValue("BRIEFCASE_SELECTED")))
                   addToBriefcase();
                //Bug Id 89939, end
                
		if (!mgr.isEmpty(mgr.getQueryStringValue("REFRESH_METHOD")))
		{
			refreshParentMethod = mgr.getQueryStringValue("REFRESH_METHOD");
		}

		adjust();

		ctx.writeFlag("ISNEW",isNew);

		// ENMALK
		ctx.writeValue("SPASSINGPARAMETERS",sPassingParameters);
		ctx.writeValue("NINVOICENO",nInvoiceNo);
		ctx.writeNumber("CURROWNO",curRowNo);
		ctx.writeValue("REFRESHNAME",refreshParentMethod);
	}

	public void addDocumentsToFolder(ASPBuffer transferBuffer)
	{
		ASPManager mgr = getASPManager();
		ASPBuffer subBuffer;
		int nItems;
		String doc_class, doc_no, doc_sheet, doc_rev, new_doc_key_ref, new_doc_lu_name;
		int doc_class_pos, doc_no_pos, doc_sheet_pos, doc_rev_pos;

		// Bug Id 82596, start
		//nItems = transferBuffer.countItems()-3;
		nItems = transferBuffer.countItems()-4;
		// Bug Id 82596, end

		new_doc_lu_name = transferBuffer.getValue("NEW_DOC_LU_NAME");
		new_doc_key_ref = transferBuffer.getValue("NEW_DOC_KEY_REF");

		doc_class_pos = doc_no_pos = doc_sheet_pos = doc_rev_pos = -1;

		for (int i = 0; i < nItems; i++)
		{
			subBuffer = transferBuffer.getBufferAt(i);

			if (i==0)
			{
				doc_class_pos = subBuffer.getItemPosition("DOC_CLASS");
				doc_no_pos    = subBuffer.getItemPosition("DOC_NO");
				doc_sheet_pos = subBuffer.getItemPosition("FIRST_SHEET_NO");
				doc_rev_pos   = subBuffer.getItemPosition("FIRST_REVISION");

			}

			doc_class = subBuffer.getValueAt(doc_class_pos);
			doc_no = subBuffer.getValueAt(doc_no_pos);
			doc_sheet = subBuffer.getValueAt(doc_sheet_pos);
			doc_rev = subBuffer.getValueAt(doc_rev_pos);



			if (doc_class != null && doc_no != null && doc_sheet != null && doc_rev != null)
			{
				trans.clear();

				cmd = trans.addCustomCommand("ADDTODOCFOLDER","Doc_Reference_Object_Api.Create_New_Reference");
				cmd.addParameter("DOC_CLASS", doc_class);
				cmd.addParameter("DOC_NO", doc_no);
				cmd.addParameter("DOC_SHEET", doc_sheet);
				cmd.addParameter("DOC_REV", doc_rev);
				cmd.addParameter("LU_NAME", new_doc_lu_name);
				cmd.addParameter("KEY_REF", new_doc_key_ref);

				mgr.perform(trans);
			}

		}

		reload(new_doc_lu_name, new_doc_key_ref);
	}

	// MDAHSE, 2000-12-21
	// Added easy-to-use debug function

	public void  debug( String text,boolean popup)
	{
		ASPManager mgr = getASPManager();

		if (debug_enabled)
		{
			log.debug(text);

			// If popup argument is true, display message
			// in an alert box also
			if (popup)
			{
				mgr.showAlert(text);
			}
		}
	}


	public void  validate()
	{
		ASPManager mgr = getASPManager();
		String val,docname,stitle;
		String doc_class ="", doc_sheet = "",doc_rev = "", numberGenerator = "", numGenTrx = "", id1 = "", id2 = "";

		val = mgr.readValue("VALIDATE");

		if ("INSERT_DATA".equals(val))
		{
			String data = mgr.readValue("INSERT_DATA");
			mgr.responseWrite(data);
			mgr.endResponse();
		}
		else if ("DLG_DOC_CLASS".equals(val))
		{
			trans.clear();

			cmd = trans.addCustomFunction("GETDEFREV","Doc_Class_Default_API.Get_Default_Value_","DLG_DOC_REV");
			cmd.addParameter("DLG_DOC_CLASS");
			cmd.addParameter("TEMP1","DocTitle");
			cmd.addParameter("TEMP2","DOC_REV");

			cmd = trans.addCustomFunction("GETDOCSHEET","Doc_Class_Default_API.Get_Default_Value_","FIRST_SHEET_NO");
			cmd.addParameter("DLG_DOC_CLASS");
			cmd.addParameter("TEMP1","DocTitle");
			cmd.addParameter("TEMP3","DOC_SHEET");

			//NUMBER_GENERATOR
			cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
			cmd.addParameter("DLG_DOC_CLASS");
			cmd.addParameter("DUMMY1","DocTitle");
			cmd.addParameter("DUMMY2","NUMBER_GENERATOR");

			cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
			cmd.addParameter("DLG_DOC_CLASS");
			cmd.addParameter("DUMMY1","DocTitle");
			cmd.addParameter("DUMMY2","NUMBER_COUNTER");

			trans = mgr.validate(trans);
			doc_rev = trans.getValue("GETDEFREV/DATA/DLG_DOC_REV");
			doc_sheet = trans.getValue("GETDOCSHEET/DATA/FIRST_SHEET_NO");
			numberGenerator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
			id1 = trans.getValue("ID1COM/DATA/ID1");

			trans.clear();

			cmd = trans.addCustomFunction("GETCLIENTVAL","Doc_Number_Generator_Type_API.Decode","OUT_1");
			cmd.addParameter("NUMBER_GENERATOR",numberGenerator);

			if ("ADVANCED".equals(numberGenerator))
			{
				cmd = trans.addCustomFunction("ID2COM","Doc_Number_Counter_API.Get_Default_Id2","ID2");
				cmd.addParameter("DUMMY1",id1);
			}

			trans = mgr.validate(trans);
			numGenTrx = trans.getValue("GETCLIENTVAL/DATA/OUT_1");
			if ("ADVANCED".equals(numberGenerator))
				id2 = trans.getValue("ID2COM/DATA/ID2");
			else
			{
				id1 = "";
				id2 = "";
			}
			if (("0".equals(id2)) || (id2 == null))
				id2 = "";

			StringBuffer response = new StringBuffer(mgr.isEmpty(doc_rev) ? "A1" : doc_rev);
			response.append("^");
			response.append(mgr.isEmpty(doc_sheet) ? "1" : doc_sheet);
			response.append("^");
			response.append(mgr.isEmpty(numberGenerator) ? "" : numberGenerator);
			response.append("^");
			response.append(mgr.isEmpty(numGenTrx) ? "" : numGenTrx);
			response.append("^");
			response.append(mgr.isEmpty(id1) ? "" : id1);
			response.append("^");
			response.append(mgr.isEmpty(id2) ? "" : id2);
			response.append("^");
			mgr.responseWrite(response.toString());
			mgr.endResponse();
		}
		else if ("DOC_CLASS".equals(val))
		{
			cmd = trans.addCustomFunction("DOCNAME","DOC_CLASS_API.Get_Name","SDOCCLASSNAME");
			cmd.addParameter("DOC_CLASS");

			cmd = trans.addCustomFunction("LASTDOCREV","Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_","KEEP_LAST_DOC_REV");
			cmd.addParameter("DOC_CLASS");
			trans = mgr.validate(trans);

			docname = trans.getValue("DOCNAME/DATA/SDOCCLASSNAME");
			last_doc_rev = trans.getValue("LASTDOCREV/DATA/KEEP_LAST_DOC_REV");

			trans.clear();

			cmd = trans.addCustomFunction("LASTDOCREVDECODE","ALWAYS_LAST_DOC_REV_API.Decode","KEEP_LAST_DOC_REV");
			if (mgr.isEmpty(last_doc_rev))
				cmd.addParameter("TEMP1","F");
			else
				cmd.addParameter("TEMP1",last_doc_rev);
			trans = mgr.validate(trans);
			last_doc_rev_decode = trans.getValue("LASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");
			trans.clear();

			txt = (mgr.isEmpty(docname) ? "" : docname) + "^"+last_doc_rev_decode+ "^"+last_doc_rev+ "^"; //Bug Id 85361
			mgr.responseWrite(txt);
			mgr.endResponse();
		}
		else if ("DOC_NO".equals(val))
		{
			String reqstr = null;
			int startpos = 0;
			int endpos = 0;
			int i = 0;
			String ar[] = new String[4];
			String doc_no = "";
			String new_doc_no = mgr.readValue("DOC_NO","");

			if (new_doc_no.indexOf("^",0)>0)
			{
				for (i=0 ; i<ar.length; i++)
				{
					endpos = new_doc_no.indexOf("^",startpos);
					reqstr = new_doc_no.substring(startpos,endpos);
					ar[i] = reqstr;
					startpos= endpos+1;
				}
                                doc_class = ar[0];
				doc_no = ar[1];
				doc_sheet = ar[2];
				doc_rev = ar[3];
			}
			else
			{
                                doc_class = mgr.readValue("DOC_CLASS"); 
				doc_no =  mgr.readValue("DOC_NO");
				doc_sheet = mgr.readValue("DOC_SHEET");
				doc_rev = mgr.readValue("DOC_REV"); 
			}

			trans.clear();
			cmd = trans.addCustomFunction("GETDOCTITLE","DOC_TITLE_API.Get_Title","SDOCTITLE");
                        if (! "".equals(doc_class)) {
                            cmd.addParameter("DOC_CLASS", doc_class);
                        }else{
                            cmd.addParameter("DOC_CLASS");
                        }
			cmd.addParameter("DOC_NO",doc_no);
			trans = mgr.validate(trans);
			stitle = trans.getValue("GETDOCTITLE/DATA/SDOCTITLE");
			trans.clear();
			
                        cmd = trans.addCustomFunction("DOCNAME","DOC_CLASS_API.Get_Name","SDOCCLASSNAME");
			cmd.addParameter("DOC_CLASS", doc_class);

			cmd = trans.addCustomFunction("LASTDOCREV","Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_","KEEP_LAST_DOC_REV");
			cmd.addParameter("DOC_CLASS", doc_class);
			trans = mgr.validate(trans);

			docname = trans.getValue("DOCNAME/DATA/SDOCCLASSNAME");
			last_doc_rev = trans.getValue("LASTDOCREV/DATA/KEEP_LAST_DOC_REV");

			trans.clear();

			cmd = trans.addCustomFunction("LASTDOCREVDECODE","ALWAYS_LAST_DOC_REV_API.Decode","KEEP_LAST_DOC_REV");
			if (mgr.isEmpty(last_doc_rev))
				cmd.addParameter("TEMP1","F");
			else
				cmd.addParameter("TEMP1",last_doc_rev);

			trans = mgr.validate(trans);
			last_doc_rev_decode = trans.getValue("LASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");
			trans.clear();
			//Bug Id 85361. Start                        
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
			sql.append("Select substr(DECODE(?");
			sql.append(",'F',?");
			sql.append(",'R','");
			sql.append(trans.getValue("GETSPECR/DATA/DUMMY"));
			sql.append("','L','");
			sql.append(trans.getValue("GETSPECL/DATA/DUMMY"));
			sql.append("'),1,6) DOC_REV FROM DUAL");
			trans.clear();

			ASPQuery query = trans.addQuery("GETDOCREV", sql.toString());
			query.addParameter("KEEP_LAST_DOC_REV_DB",last_doc_rev);
		        query.addParameter("DOC_REV",doc_rev);         
			trans = mgr.validate(trans);
			doc_rev = trans.getValue("GETDOCREV/DATA/DOC_REV");

			if (mgr.isEmpty(doc_rev) || "null".equals(doc_rev))
			       doc_rev = ""; 
			trans.clear();
			//Bug Id 85361. End


			txt = (mgr.isEmpty(stitle)? "" : stitle) +"^"+ (mgr.isEmpty(doc_class)? "" : doc_class) +"^"+ (mgr.isEmpty(doc_no)? "" : doc_no) +"^"+ (mgr.isEmpty(doc_sheet)? "" : doc_sheet) +"^"+ (mgr.isEmpty(doc_rev)? "" : doc_rev) +"^"+ (mgr.isEmpty(docname) ? "" : docname) + "^"+(mgr.isEmpty(last_doc_rev_decode) ? "" : last_doc_rev_decode)+ "^" + (mgr.isEmpty(last_doc_rev) ? "" : last_doc_rev)+ "^";  //Bug Id 85361
			mgr.responseWrite(txt);
			mgr.endResponse();

		}
                //bug 57719, Start
                else if ("SURVEY_LOCKED_FLAG".equals(val))
                {
                    trans.clear();

                    cmd = trans.addCustomFunction("GETDB","Lock_Document_Survey_Api.Encode","SURVEY_LOCKED_FLAG_DB");
                    cmd.addParameter("SURVEY_LOCKED_FLAG");

                    trans = mgr.validate(trans);
                    String sLockedFlag = trans.getValue("GETDB/DATA/SURVEY_LOCKED_FLAG_DB");

                    txt = (mgr.isEmpty(sLockedFlag) ? "" : sLockedFlag) + "^";
                    mgr.responseWrite(txt);
                    mgr.endResponse();
                }
                //bug 57719, End
                //Bug Id 85361. Start 
		else if ("KEEP_LAST_DOC_REV".equals(val))
		{
  		    String lastDocRev = mgr.readValue("KEEP_LAST_DOC_REV");

		    doc_class = mgr.readValue("DOC_CLASS");
		    String doc_no = mgr.readValue("DOC_NO");
		    doc_sheet = mgr.readValue("DOC_SHEET");
		    doc_rev = mgr.readValue("DOC_REV");

                    trans.clear();

                    cmd = trans.addCustomFunction("GETREVDB","Always_Last_Doc_Rev_API.Encode","DUMMY");
                    cmd.addParameter("DUMMY1",lastDocRev);
                    
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
                    String lastDocRevDB = trans.getValue("GETREVDB/DATA/DUMMY");
		    StringBuffer sql = new StringBuffer();
		    sql.append("Select substr(DECODE(?");
		    sql.append(",'F',?");
		    sql.append(",'R','");
                    sql.append(trans.getValue("GETSPECR/DATA/DUMMY"));
                    sql.append("','L','");
                    sql.append(trans.getValue("GETSPECL/DATA/DUMMY"));
                    sql.append("'),1,6) DOC_REV FROM DUAL");
                    trans.clear();
                    
		    ASPQuery query = trans.addQuery("GETDOCREV", sql.toString());
                    query.addParameter("KEEP_LAST_DOC_REV_DB",lastDocRevDB);
		    query.addParameter("DOC_REV",doc_rev);            
		    trans = mgr.validate(trans);
		    doc_rev = trans.getValue("GETDOCREV/DATA/DOC_REV");

	            if (mgr.isEmpty(doc_rev) || "null".equals(doc_rev))
			       doc_rev = ""; 

                    mgr.responseWrite( doc_rev + "^" +(mgr.isEmpty(lastDocRevDB) ? "" : lastDocRevDB) + "^");
                    mgr.endResponse();
		}
                //Bug Id 85361. End
		else
		{
			mgr.showError("VALIDATE not implemented");
			mgr.endResponse();
		}
	}


	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		if ( mgr.dataTransfered())
			search();
		else
		{
			searchURL = mgr.createSearchURL(headblk);

			trans.clear();
			q = trans.addQuery(headblk);
			q.includeMeta("ALL");

			mgr.submit(trans);

			if (headset.countRows() == 0)
				mgr.showAlert("DOCMAWDOCREFERENCENODATA: No data found.");

			eval(headset.syncItemSets());

			if (itemset.countRows()>0)
				okFindITEM();
		}
		getObjectDes();
	}


	// MDAHSE, 2000-12-19
	// Added new okFindObjid for taking care of
	// requests made by sending in OBJID and VIEW

	public void  okFindObjid()
	{
		String comment,view_name;
		String commentArr[];

		String luNameArr[];

		ASPManager mgr = getASPManager();

		bFromOkFindObjid = true;

		// So that this page can be added to My Links
		mgr.createSearchURL(headblk);

		view_name = mgr.readValue("view");
		if (mgr.isEmpty(view_name))
			view_name=mgr.readValue("VIEW");
		objid = mgr.readValue("objid");
		if (mgr.isEmpty(objid))
			objid=mgr.readValue("OBJID");

		// A list of incoming view names
		// where we actually want to work with
		// another view/LU.
		// For example, when inserting a reference to
		// a EquipmentFunctional object, insert EquipmentObject
		// as LU_NAME instead in DocReferenceObject


		// MDAHSE, 2001-01-16
		// Removed view conversions for the following views:

		// ACTIVE_ROUND
		// ACTIVE_SEPARATE
		// HISTORICAL_SEPARATE
		// HISTORICAL_ROUND


		if ("EQUIPMENT_FUNCTIONAL".equals(view_name))
			view_name="EQUIPMENT_OBJECT";
		else if ("EQUIPMENT_SERIAL".equals(view_name))
			view_name="EQUIPMENT_OBJECT";
		else if ("SEPARATE_STANDARD_JOB".equals(view_name))
			view_name="STANDARD_JOB";
		else if ("ROUND_STANDARD_JOB".equals(view_name))
			view_name="STANDARD_JOB";
		else if ("EQUIPMENT_ALL_OBJECT".equals(view_name))
			view_name="EQUIPMENT_OBJECT";
		//IID FIPR308A Begin
		else if ("POSTING_PROPOSAL_INVOICE_WEB".equals(view_name))
			view_name="MAN_SUPP_INVOICE";
		//IID FIPR308A End
		else
			view_name = view_name;

		boolean bSetClosed = checkWfState(view_name, objid);
		
		// Initialize the transaction
		trans.clear();

		// We really should use a reference to this value in the next database call
		// so that we only make ONE submit to the database.

		cmd = trans.addCustomFunction("GETLUNAME", "Doc_Reference_API.Get_Lu_Name_","DUMMY");
		cmd.addParameter("TEMP1", view_name);

		trans = mgr.perform(trans);

		root_lu_name = trans.getValue("GETLUNAME/DATA/DUMMY");

		// Initialize the second transaction
		trans.clear();

		cmd = trans.addCustomCommand("GETKEYREF","Client_SYS.Get_Key_Reference");

		cmd.addParameter("KEY_REF");						 // Our out-variable from the procedure call
		cmd.addParameter("LU_NAME",root_lu_name);	// pass root_lu_name as a parameter
		cmd.addParameter("OBJID",objid);

		// Submit this query
		trans = mgr.perform(trans);

		// Get the resulting values
		root_key_ref = trans.getValue("GETKEYREF/DATA/KEY_REF");

		// MDAHSE, 2000-12-16
		// Removed reference to variable okFindObjidRun

		keyRefLuNameTransferred();
		
		if (bSetClosed)
         setDrLock(view_name);
	}
	
	// Added by Terry 20140514
	// Check work flow status and set document reference to lock.
	private boolean checkWfState(String view_name, String objid)
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      String objid_ = (String)ctx.findGlobalObject(prefix_wf_ob_dr + view_name);
      if ("ALL".equals(objid_))
         return true;
      
      if (!mgr.isEmpty(objid) && objid.equals(objid_))
         return true;
      
      return false;
   }
   
   private void setDrLock(String view_name)
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      if (headset.countRows() > 0 && "Unlocked".equals(headset.getValue("OBJSTATE")))
      {
         setLock();
      }
      ctx.setGlobalObject(prefix_wf_ob_dr + view_name, null);
   }
   // Added end

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

		cmd = trans.addEmptyCommand("HEAD","DOC_REFERENCE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		dataBuffer = trans.getBuffer("HEAD/DATA");
		headset.addRow(dataBuffer);
	}


	public void search()
	{
		ASPManager mgr = getASPManager();
		mgr.createSearchURL(headblk);
      String lu_name_="";
      String key_ref_="";
      boolean fromEquipmentObject = false;
		q = trans.addQuery(headblk);
		if (mgr.dataTransfered())
		{
			q.addOrCondition( mgr.getTransferedData() );
		}

		/* Start - IFS Maintenance 800xA Integration -- */
		else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")) || !mgr.isEmpty(mgr.getQueryStringValue("SITE")))
		{
         fromEquipmentObject = true;
         String site = (mgr.isEmpty(mgr.getQueryStringValue("SITE")) ? " " : mgr.getQueryStringValue("SITE"));
			String mchcode = (mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")) ? " " : mgr.getQueryStringValue("MCH_CODE"));

         lu_name_ = "EquipmentObject";
         key_ref_ = "CONTRACT=" + site + "^MCH_CODE=" + mchcode + "^";

			q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
         q.addParameter("LU_NAME",lu_name_);
         q.addParameter("KEY_REF",key_ref_);
		}
		/* End - IFS Maintenance 800xA Integration -- */

		q.includeMeta("ALL");
		mgr.querySubmit(trans,headblk);
		if (headset.countRows()==0)
		{
         if (!fromEquipmentObject) {
            ASPBuffer temp2=mgr.getTransferedData();
   			lu_name_= temp2.getBufferAt(0).getValueAt(0);
   			key_ref_= temp2.getBufferAt(0).getValueAt(1);
         }

			

			String attr = "LU_NAME"+ (char)31 + lu_name_ + (char)30 +"KEY_REF"+(char)31 +key_ref_+(char)30+"DOC_COUNT"+(char)31 +"0"+(char)30;
			cmd = trans.addCustomCommand("NEW","DOC_REFERENCE_API.New__");
			cmd.addParameter("INFO");
			cmd.addParameter("OBJID");
			cmd.addParameter("OBJVERSION");
			cmd.addParameter("ATTRHEAD",attr);
			cmd.addParameter("ACTION","DO");
			trans = mgr.perform(trans);

			searchURL = mgr.createSearchURL(headblk);

			trans.clear();
			q = trans.addQuery(headblk);
         if (fromEquipmentObject) {
            q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
            q.addParameter("LU_NAME",lu_name_);
            q.addParameter("KEY_REF",key_ref_);
         }else
            q.addOrCondition( mgr.getTransferedData() );

			q.includeMeta("ALL");

			mgr.submit(trans);

			eval(headset.syncItemSets());

		}
		okFindITEM();
	}


	public void  reload()
	{
		ASPManager mgr = getASPManager();

		luName= headset.getRow().getValue("LU_NAME");
		keyRef= headset.getRow().getValue("KEY_REF");

		buff=mgr.newASPBuffer();
		row=buff.addBuffer("1");
		row.addItem("LU_NAME",luName);
		row.addItem("KEY_REF",keyRef);

		if (!mgr.isEmpty(refreshParentMethod))
		{
			bRefreshParent=true;
			mgr.transferDataTo("DocReference.page?bRefreshParent=true&REFRESH_METHOD="+refreshParentMethod,buff);
		}
		else
			mgr.transferDataTo("DocReference.page",buff);
	}

	public void reload(String lu_name, String key_ref)
	{
		ASPManager mgr = getASPManager();
		
      //Bug Id 81340, Start
      buff=mgr.newASPBuffer();
		row=buff.addBuffer("1");
		row.addItem("LU_NAME",lu_name);
		row.addItem("KEY_REF",key_ref);
      mgr.transferDataTo("DocReference.page",buff);
      //Bug Id 81340, End
	}


	public void  searchConnections()
	{
		ASPManager mgr = getASPManager();

		searchURL = mgr.createSearchURL(headblk);

		q = trans.addQuery(headblk);

		// ENMALK
		if (mgr.dataTransfered())
			q.addOrCondition( mgr.getTransferedData() );

		q.includeMeta("ALL");
		q = trans.addQuery(itemblk);
		q.addMasterConnection("HEAD","LU_NAME","ITEM_LU_NAME");
		q.addMasterConnection("HEAD","KEY_REF","ITEM_KEY_REF");
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
			mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENODATA: No data found."));
		else
		{
			okFind();
			if (headset.countRows()>0)
				okFindITEM();
		}
	}


	public void  keyRefLuNameTransferred()
	{
		ASPManager mgr = getASPManager();
		String logicalunit,insdesc;

		trans.clear();
		q = trans.addEmptyQuery(headblk);

		// MDAHSE, 2000-12-26
		// Now, we have to check the VIEW and OBJID parameters again
		// so that we know where we came from
		// (from a call to okFindObjid or somewhere else)

		if ((!mgr.isEmpty(mgr.getQueryStringValue("view")) || !mgr.isEmpty(mgr.getQueryStringValue("VIEW"))) && (!mgr.isEmpty(mgr.getQueryStringValue("objid")) || !mgr.isEmpty(mgr.getQueryStringValue("OBJID"))))
		{
         //bug 58216 starts
         q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
         q.addParameter("LU_NAME",root_lu_name);
         q.addParameter("KEY_REF",root_key_ref);
         //bug 58216 ends 
		}
		// MDAHSE 2001-01-02
		// Missed the else-condition...
		else
		{
         //bug 58216 starts
         q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
         q.addParameter("LU_NAME",mgr.readValue("LU_NAME"));
         q.addParameter("KEY_REF",mgr.readValue("KEY_REF"));
         //bug 58216 ends 
		}

		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			trans.clear();
			cmd = trans.addCustomFunction( "LOGICALUNIT", "OBJECT_CONNECTION_SYS.GET_LOGICAL_UNIT_DESCRIPTION", "SLUDESC" );
			cmd.addParameter("LU_NAME");
			trans = mgr.validate(trans);
			logicalunit = trans.getValue("LOGICALUNIT/DATA/SLUDESC");

			trans.clear();
			cmd = trans.addCustomFunction( "INSDESC", "OBJECT_CONNECTION_SYS.GET_INSTANCE_DESCRIPTION", "SINSTANCEDESC" );
			cmd.addParameter("LU_NAME");
			cmd.addParameter("VIEW_NAME","");
			cmd.addParameter("KEY_REF");

			trans = mgr.validate(trans);

			insdesc = trans.getValue("INSDESC/DATA/SINSTANCEDESC");
			dataBuffer = trans.getBuffer("HEAD/DATA");
			trans.clear();
			headset.addRow(dataBuffer);

			buff = headset.getRow();

			// MDAHSE, 2000-12-26
			// Changed this check to see if we come from
			// the new okFindObjid function or not.

			if ((!mgr.isEmpty(mgr.getQueryStringValue("KEY_REF"))) && (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME"))))
			{
				buff.setValue("LU_NAME",mgr.readValue("LU_NAME"));
				buff.setValue("KEY_REF",mgr.readValue("KEY_REF"));
			}
			else
			{
				// If we did not get the KEY_REF and LU_NAME in the request string
				// we came here from okFindObjid and therefore we
				// set the values we got there
				buff.setValue("LU_NAME",root_lu_name);
				buff.setValue("KEY_REF",root_key_ref);
			}

			buff.setValue("DOC_OBJECT_DESC",mgr.readValue("DOC_OBJECT_DESC"));
			buff.setValue("SLUDESC",logicalunit);
			buff.setValue("SINSTANCEDESC",insdesc);
			buff.setValue("DOC_COUNT","0");

			headset.setRow(buff);
			trans.clear();
			mgr.submit(trans);
			trans.clear();
		}
		else
			okFindITEM();

		getObjectDes();
	}


	public void getObjectDes()
	{
		ASPManager mgr = getASPManager();

		if (headset.countRows()>0)
		{

			trans.clear();
			cmd = trans.addCustomCommand( "OBJECTDES", "OBJECT_CONNECTION_SYS.get_connection_description");
			cmd.addParameter("DOC_OBJECT_DESC");
			if (mgr.isEmpty(headset.getRow().getValue("LU_NAME")))
				cmd.addParameter("LU_NAME",root_lu_name);
			else
				cmd.addParameter("LU_NAME",headset.getRow().getValue("LU_NAME"));

			if (mgr.isEmpty(headset.getRow().getValue("KEY_REF")))
				cmd.addParameter("KEY_REF",root_key_ref);
			else
				cmd.addParameter("KEY_REF",headset.getRow().getValue("KEY_REF"));
			trans = mgr.perform(trans);


			buff = headset.getRow();
			buff.setFieldItem("DOC_OBJECT_DESC",trans.getValue("OBJECTDES/DATA/DOC_OBJECT_DESC"));
			trans.clear();
			headset.setRow(buff);
			trans.clear();
		}
	}


	public void  okFindITEM()
	{

		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addQuery(itemblk);
		q.setOrderByClause("CONNECTED_DATE");

		// MDAHSE, 2000-12-26
		// Figure out if If we came from okFindObjid or not
		// If the query parameters KEY_REF and LU_NAME are set,
		// we probably did not.

		if ((!mgr.isEmpty(mgr.getQueryStringValue("KEY_REF"))) && (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME"))))
		{
         //bug 58216 starts
         q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
         q.addParameter("LU_NAME",headset.getRow().getValue("LU_NAME"));
         q.addParameter("KEY_REF",headset.getRow().getValue("KEY_REF"));
         //bug 58216 ends 
		}
		// MDAHSE, 2001-01-18
		// Forgot to take care of TRANSFER before
		else if (mgr.dataTransfered())
		{
          //bug 58216 starts
         q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
         q.addParameter("LU_NAME",headset.getRow().getValue("LU_NAME"));
         q.addParameter("KEY_REF",headset.getRow().getValue("KEY_REF"));
         //bug 58216 ends 
		}
		else
		{
			// I guess we came from the okFindObjid function then...
			if (bFromOkFindObjid)
			{
            //bug 58216 starts
            q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
            q.addParameter("LU_NAME",root_lu_name);
            q.addParameter("KEY_REF",root_key_ref);
            //bug 58216 ends 
			}
			else
			{
            //bug 58216 starts
            q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
            q.addParameter("LU_NAME",headset.getRow().getValue("LU_NAME"));
            q.addParameter("KEY_REF",headset.getRow().getValue("KEY_REF"));
            //bug 58216 ends 
			}
		}



		q.includeMeta("ALL");

		headrowno = headset.getCurrentRowNo();
		mgr.querySubmit(trans, itemblk);

		/*
		 Removed Error msg: No Data Found, since we donot need this
		 unless user make search himself.
		*/
		headset.goTo(headrowno);
	}


	public void  okFindITEMWithErrorMsg()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addQuery(itemblk);
		q.setOrderByClause("DOC_CLASS, DOC_NO, DOC_SHEET");

		// MDAHSE, 2000-12-26
		// Figure out if If we came from okFindObjid or not
		// If the query parameters KEY_REF and LU_NAME are set,
		// we probably did not.

		if ((!mgr.isEmpty(mgr.getQueryStringValue("KEY_REF"))) && (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME"))))
		{
         //bug 58216 starts
         q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
         q.addParameter("LU_NAME",headset.getRow().getValue("LU_NAME"));
         q.addParameter("KEY_REF",headset.getRow().getValue("KEY_REF"));
         //bug 58216 ends 
		}
		// MDAHSE, 2001-01-18
		// Forgot to take care of TRANSFER before
		else if (mgr.dataTransfered())
		{
         //bug 58216 starts
         q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
         q.addParameter("LU_NAME",headset.getRow().getValue("LU_NAME"));
         q.addParameter("KEY_REF",headset.getRow().getValue("KEY_REF"));
         //bug 58216 ends 
		}
		else
		{
			// I guess we came from the okFindObjid function then...
			if (bFromOkFindObjid)
			{
            //bug 58216 starts
            q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
            q.addParameter("LU_NAME",root_lu_name);
            q.addParameter("KEY_REF",root_key_ref);
            //bug 58216 ends 

			}
			else
			{
            //bug 58216 starts
            q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
            q.addParameter("LU_NAME",headset.getRow().getValue("LU_NAME"));
            q.addParameter("KEY_REF",headset.getRow().getValue("KEY_REF"));
            //bug 58216 ends 
			}
		}

		q.includeMeta("ALL");

		headrowno = headset.getCurrentRowNo();
		mgr.querySubmit(trans, itemblk);

		if (itemset.countRows()==0 && userFinds)
			mgr.showAlert("DOCMAWDOCREFERENCENODATA: No data found.");
		headset.goTo(headrowno);
	}



	public void  countFindITEM()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(itemblk);
		q.setSelectList("to_char(count(*)) N");
		if ((!mgr.isEmpty(mgr.getQueryStringValue("KEY_REF"))) && (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME"))))
		{
         //bug 58216 starts
         q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
         q.addParameter("LU_NAME",headset.getRow().getValue("LU_NAME"));
         q.addParameter("KEY_REF",headset.getRow().getValue("KEY_REF"));
         //bug 58216 ends 
		}
		else
		{
         //bug 58216 starts
         q.addWhereCondition("LU_NAME= ? and KEY_REF= ?");
         q.addParameter("LU_NAME",headset.getRow().getValue("LU_NAME"));
         q.addParameter("KEY_REF",headset.getRow().getValue("KEY_REF"));
         //bug 58216 ends 
		}

		mgr.submit(trans);
		itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
		itemset.clear();
	}


	public void  newRowITEM()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addEmptyCommand("ITEM","DOC_REFERENCE_OBJECT_API.New__",itemblk);
		cmd.setOption("ACTION","PREPARE");

		cmd.setParameter("LU_NAME", headset.getValue("LU_NAME"));
		cmd.setParameter("KEY_REF", headset.getValue("KEY_REF"));

		trans = mgr.perform(trans);
		dataBuffer = trans.getBuffer("ITEM/DATA");
		itemset.addRow(dataBuffer);
	}


	public void  refreshCurrentRow()
	{

		if (headlay.isMultirowLayout())
		{
			headset.goTo(curRowNo);
		}
		else
			headset.selectRow();

		if (headset.countRows() > 0)
		   headset.refreshRow();  //  change the edm status of the file after edit,checkin etc operations
	}

	public void saveReturnITEM()
	{
		ASPManager mgr = getASPManager();
		itemset.changeRow();

		if ("New__".equals(itemset.getRowStatus()))
		{
			trans.clear();
			cmd = trans.addEmptyCommand("ITEM","DOC_REFERENCE_OBJECT_API.New__",itemblk);
			cmd.setOption("ACTION","DO");
			cmd.setParameter("LU_NAME",headset.getValue("LU_NAME"));
			cmd.setParameter("KEY_REF",headset.getValue("KEY_REF"));
			cmd.setParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));
			cmd.setParameter("DOC_NO",mgr.readValue("DOC_NO"));
			cmd.setParameter("DOC_SHEET",mgr.readValue("DOC_SHEET"));
			cmd.setParameter("DOC_REV",mgr.readValue("DOC_REV"));
			cmd.setParameter("CATEGORY",mgr.readValue("CATEGORY"));
			cmd.setParameter("COPY_FLAG",mgr.readValue("COPY_FLAG"));
			cmd.setParameter("KEEP_LAST_DOC_REV",mgr.readValue("KEEP_LAST_DOC_REV"));
			cmd.setParameter("SURVEY_LOCKED_FLAG",mgr.readValue("SURVEY_LOCKED_FLAG"));

			trans = mgr.perform(trans);
			ASPBuffer buff=mgr.newASPBuffer();
			ASPBuffer row=buff.addBuffer("1");
			row.addItem("LU_NAME",headset.getValue("LU_NAME"));
			row.addItem("KEY_REF",headset.getValue("KEY_REF"));

			if (!mgr.isEmpty(refreshParentMethod))
			{
				bRefreshParent=true;
				mgr.transferDataTo("DocReference.page?bRefreshParent=true&REFRESH_METHOD="+refreshParentMethod,buff);
			}
			else
				mgr.transferDataTo("DocReference.page",buff);
		}
		else
		{
			currrow = itemset.getCurrentRowNo();
			mgr.submit(trans);
			itemset.goTo(currrow);
		}
	}


	public void saveNewITEM()
	{
		ASPManager mgr = getASPManager();
		itemset.changeRow();

		if ("New__".equals(itemset.getRowStatus()))
		{
			trans.clear();
			cmd = trans.addEmptyCommand("ITEM","DOC_REFERENCE_OBJECT_API.New__",itemblk);
			cmd.setOption("ACTION","DO");
			cmd.setParameter("LU_NAME",headset.getValue("LU_NAME"));
			cmd.setParameter("KEY_REF",headset.getValue("KEY_REF"));
			cmd.setParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));
			cmd.setParameter("DOC_NO",mgr.readValue("DOC_NO"));
			cmd.setParameter("DOC_SHEET",mgr.readValue("DOC_SHEET"));
			cmd.setParameter("DOC_REV",mgr.readValue("DOC_REV"));
			cmd.setParameter("CATEGORY",mgr.readValue("CATEGORY"));
			cmd.setParameter("COPY_FLAG",mgr.readValue("COPY_FLAG"));
			cmd.setParameter("KEEP_LAST_DOC_REV",mgr.readValue("KEEP_LAST_DOC_REV"));
			cmd.setParameter("SURVEY_LOCKED_FLAG",mgr.readValue("SURVEY_LOCKED_FLAG"));
			trans = mgr.perform(trans);

			if (!mgr.isEmpty(refreshParentMethod))
			{
				bRefreshParent=true;
			}
		}
		else
		{
			currrow = itemset.getCurrentRowNo();
			mgr.submit(trans);
			itemset.goTo(currrow);
		}
		newRowITEM();
	}
	
	public void deleteDoc()
	{
	   deleteITEM();
	}
	
	public void deleteITEM()
   {
      ASPManager mgr = getASPManager();

      itemset.store();
      
      deleteDocument();
      
      if (itemlay.isMultirowLayout())
      {
         itemset.setSelectedRowsRemoved();
         itemset.unselectRows();
      }
      else
         itemset.setRemoved();

      trans.clear();
      mgr.submit(trans);
      okFindITEM();
      // itemset.refreshAllRows();
   }

	public void setObsolete()
	{
	   ASPManager mgr = getASPManager();
	   int curr_row = 0;
	   
	   curr_row = headset.getCurrentRowNo();
	   
	   if (itemlay.isMultirowLayout())
	   {
	      itemset.storeSelections();
	      if (itemset.countSelectedRows() == 0)
	      {
	         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
	         return;
	      }
	   }
	   else
	   {
	      itemset.selectRow();
	   }
	   
	   trans.clear();
	   
	   boolean perform = false;
	   ASPBuffer sel_rows = itemset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,TEMP_EDIT_ACCESS,DOC_OBJSTATE");
	   ASPCommand cmd;
	   for (int i = 0; i < sel_rows.countItems(); i++)
	   {
	      if ("TRUETRUE".equals(sel_rows.getBufferAt(i).getValueAt(4)) && !"Obsolete".equals(sel_rows.getBufferAt(i).getValueAt(5)))
	      {
	         cmd = trans.addCustomCommand("SETOBSOLETE" + i, "DOC_ISSUE_API.Promote_To_Obsolete_");
	         cmd.addParameter("DOC_CLASS", sel_rows.getBufferAt(i).getValueAt(0));
	         cmd.addParameter("DOC_NO", sel_rows.getBufferAt(i).getValueAt(1));
	         cmd.addParameter("DOC_SHEET", sel_rows.getBufferAt(i).getValueAt(2));
	         cmd.addParameter("DOC_REV", sel_rows.getBufferAt(i).getValueAt(3));
	         perform = true;
	      }
	   }
	   if (perform)
	   {
	      trans = mgr.perform(trans);
	      headset.goTo(curr_row);
	      itemset.refreshAllRows();
	   }
	}
	
	public void deleteDocument()
	{
	   ASPManager mgr = getASPManager();
	   int noOFRowsSelected = 1;
	   int count = 0;
	   
	   // Bug 57779, End
	   if (itemlay.isMultirowLayout())
	   {
	      itemset.storeSelections();
	      itemset.setFilterOn();
	      noOFRowsSelected = itemset.countRows();
	   }
	   else
	   {
	      itemset.selectRow();
	   }
	   
	   // Set all temporary documents to Obsolete
	   setObsolete();
	   
	   boolean error = false;
	   for (count = 0; count < noOFRowsSelected; count++)
	   {
	      if ("TRUETRUE".equals(itemset.getRow().getValue("TEMP_EDIT_ACCESS")) && !"Obsolete".equals(itemset.getRow().getValue("DOC_OBJSTATE"))) {
	         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEDELETEALLNOTCORRECTSTATE: Only documents in state Obsolete can be deleted."));
	         error = true;
	         break;
	      }
	      
	      if (itemlay.isMultirowLayout()) {
	         itemset.next();
	      }
	   }
	   
	   if (error)
	   {
	      if (itemlay.isMultirowLayout())
	      {
	         itemset.setFilterOff();
	      }
	      return;
	   }
	   
	   ASPBuffer buff = mgr.newASPBuffer();
	   buff.addItem("DOC_TYPE", "ORIGINAL");
	   buff.addItem("FILE_ACTION", "DELETEALL");
	   buff.addItem("SAME_ACTION_TO_ALL", "YES");
	   transferTempToEdmMacro(buff);
	   itemset.setFilterOff();
	}

	public String getFileType()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addCustomFunction("FILTYPE", "EDM_FILE_API.GET_FILE_TYPE", "DUMMY");
		cmd.addParameter("DOC_CLASS",  itemset.getRow().getValue("DOC_CLASS"));
		cmd.addParameter("DOC_NO",     itemset.getRow().getValue("DOC_NO"));
		cmd.addParameter("DOC_SHEET",  itemset.getRow().getValue("DOC_SHEET"));
		cmd.addParameter("DOC_REV",    itemset.getRow().getValue("DOC_REV"));
		cmd.addParameter("DUMMY_TYPE", "ORIGINAL");
		trans = mgr.perform(trans);

		String result = trans.getValue("FILTYPE/DATA/DUMMY");
		return result;
	}

	public String getFileTypes()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      trans.clear();
      ASPQuery query = trans.addQuery("GETORIGINALFILETYPES", "SELECT file_type, file_extention FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW') ORDER BY file_type");
      trans = mgr.perform(trans);

      ASPBuffer buf = trans.getBuffer("GETORIGINALFILETYPES");

      String file_types = "";
      int rows = Integer.parseInt(buf.getBuffer("INFO").getValue("ROWS"));
      //int rows = buf.countItems() - 1;

      if (rows > buf.countItems() - 1){  // only if no of file types are more than row-no-limitation in web client
        trans.clear();
        query = trans.addQuery("GETORIGINALFILETYPES", "SELECT file_type, file_extention FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW') ORDER BY file_type");
        query.setBufferSize(rows);
        trans = mgr.perform(trans);
        buf = trans.getBuffer("GETORIGINALFILETYPES");
      }

      for (int i = 0; i < buf.countItems() - 1; i++)
      {
         file_types += buf.getBufferAt(i).getValueAt(0) + "^" + buf.getBufferAt(i).getValueAt(1) + "^";
      }
      return file_types;
   }

	public void transferToEdmMacro(String doc_type, String action)
	{
		ASPManager mgr = getASPManager();


		if (itemlay.isSingleLayout())
		{
			itemset.unselectRows();
			itemset.selectRow();
		}
		else
		{
			itemset.storeSelections();
		}

		ASPBuffer data = itemset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
		String doc_class,doc_no,doc_sheet,doc_rev,file_type;
		file_type = null;

		doc_class = itemset.getValue("DOC_CLASS");
		doc_no    = itemset.getValue("DOC_NO");
		doc_sheet = itemset.getValue("DOC_SHEET");
		doc_rev   = itemset.getValue("DOC_REV");

		if (! bCheckOutEmpty)
		{
			file_type = getFileType();
		}
		if (!mgr.isEmpty(sPassingParameters))
		{
			if (! bCheckOutEmpty)
				mgr.redirectTo(sPassingParameters+"&INVOICE_NO="+mgr.URLEncode(nInvoiceNo)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_SHEET="+mgr.URLEncode(doc_sheet)+"&DOC_REV="+mgr.URLEncode(doc_rev)+"&DOC_TYPE="+mgr.URLEncode(mgr.getASPField("DOC_TYPE").getValue())+"&FILE_TYPE="+mgr.URLEncode(file_type));
			else
			{
				mgr.redirectTo(sPassingParameters+"&INVOICE_NO="+mgr.URLEncode(nInvoiceNo)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_SHEET="+mgr.URLEncode(doc_sheet)+"&DOC_REV="+mgr.URLEncode(doc_rev)+"&DOC_TYPE="+mgr.URLEncode(mgr.getASPField("DOC_TYPE").getValue()));
				bCheckOutEmpty = false;
			}

		}

		sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", action, doc_type, data);
		bTranferToEDM = true;
		curRowNo=headset.getCurrentRowNo();
	}

   //Bug Id 68091, start 
   public void transferToEdmMacro(ASPBuffer buff)
	{
		ASPManager mgr = getASPManager();

		if (itemlay.isSingleLayout())
		{
			itemset.unselectRows();
			itemset.selectRow();
		}
		else
			itemset.selectRows();

		ASPBuffer data = itemset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
		sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page",buff,data);
		bTranferToEDM = true;
	}
   //Bug Id 68091, end


   public void transferTempToEdmMacro(ASPBuffer buff)
   {
      ASPManager mgr = getASPManager();

      if (itemlay.isSingleLayout())
      {
         itemset.unselectRows();
         itemset.selectRow();
      }
      else
         itemset.selectRows();

      ASPBuffer data = itemset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,TEMP_EDIT_ACCESS");
      Buffer data_buff = data.getBuffer();
      Buffer transfer_buff = data_buff.newInstance();
      boolean first_row = true;
      for (int i = 0; i < data_buff.countItems(); i++)
      {
         ifs.fnd.buffer.Item one_row = data_buff.getItem(i);
         if ("DATA".equals(one_row.getName()))
         {
            Buffer org = one_row.getBuffer();
            Buffer row;
            if ("TRUETRUE".equals(org.getItem(4).getValue()))
            {
               row = (Buffer)org.clone();
               if (first_row)
               {
                  row.getItem(0).setName("DOC_CLASS");
                  row.getItem(1).setName("DOC_NO");
                  row.getItem(2).setName("DOC_SHEET");
                  row.getItem(3).setName("DOC_REV");
                  row.getItem(4).setName("TEMP_EDIT_ACCESS");
                  first_row = false;
               }
               transfer_buff.addItem("DATA", row);
            }
         }
      }
      
      if (transfer_buff.countItems() > 0)
      {
         ASPBuffer transfer_buffer = mgr.newASPBuffer(transfer_buff);
         sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buff, transfer_buffer);
         bTranferToEDM = true;
      }
   }

	public void  viewCopy()
	{
		ASPManager mgr = getASPManager();

		if (itemlay.isMultirowLayout() && itemset.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENOROWS: No Rows Selected."));
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

		if (itemlay.isMultirowLayout() && itemset.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end

		transferToEdmMacro("ORIGINAL", "VIEW");
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


	public void editDocument() throws FndException
	{
		String status;
		String checkOutUser;
		String fndUser;
		fndUser = getLoginUser();

		ASPManager mgr = getASPManager();


		if (itemlay.isMultirowLayout() && itemset.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENOROWS: No Rows Selected."));
			return;
		}

      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);
		String sApproved = dm_const.doc_issue_approved;
		String sObsolete = dm_const.doc_issue_obsolete;
		String sReleased = dm_const.doc_issue_released;

      int curr_row;
		if (itemlay.isMultirowLayout())
			curr_row = itemset.getRowSelected();
		else
			curr_row	= itemset.getCurrentRowNo();

		itemset.goTo(curr_row);

      if (sApproved.equals (itemset.getRow().getValue("DOCTSATUS")) || sReleased.equals (itemset.getRow().getValue("DOCTSATUS")) || sObsolete.equals ( itemset.getRow().getValue("DOCTSATUS")))
      {
   		mgr.showAlert("DOCMAWDOCREFERENCENOTEDITINAPPORREL: You are not allowed to edit a document in state Approved, Released or Obsolete.");
   		itemset.unselectRows();
   		itemset.setFilterOff();
   		return;
      }
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		}
		//Bug Id 67336, end
		

		if (isEditable())
		{
         

			trans.clear();
			cmd = trans.addCustomCommand("EDMDOCSTATUS", "EDM_FILE_API.Get_Document_Status");
			cmd.addParameter("DUMMY1");
			cmd.addParameter("DUMMY2");
			cmd.addParameter("DUMMY3");
			cmd.addParameter("DUMMY4");
			cmd.addParameter("DUMMY5");
			cmd.addParameter("DUMMY6");
			cmd.addParameter("DOC_CLASS",itemset.getRow().getValue("DOC_CLASS"));
			cmd.addParameter("DOC_NO",itemset.getRow().getValue("DOC_NO"));
			cmd.addParameter("DOC_SHEET",itemset.getRow().getValue("DOC_SHEET"));
			cmd.addParameter("DOC_REV",itemset.getRow().getValue("DOC_REV"));
			cmd.addParameter("DOC_TYPE","ORIGINAL");
			trans = mgr.perform(trans);

			status  = trans.getValue("EDMDOCSTATUS/DATA/DUMMY1");
			checkOutUser = trans.getValue("EDMDOCSTATUS/DATA/DUMMY2");

			trans.clear();
			if (mgr.isEmpty(status))
			{
				bCheckOutEmpty = true;
				transferToEdmMacro("ORIGINAL","CREATENEW");
			}
			else if ((  sCheckedOut.equals(status ) ) &&  ( !(checkOutUser.equals(fndUser)) ))
			{
				mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCECHECKEDOUTMSG: The document is checked out by another user."));
				itemset.unselectRows();
				itemset.setFilterOff();
				return;
			}
			else
				transferToEdmMacro("ORIGINAL","CHECKOUT");

		}
		else
			mgr.showAlert("DOCMAWDOCREFERENCECANNOTEDIT: You must have edit access to be able to edit this document.");

		

	}



	public void  printDocument()
	{
		ASPManager mgr = getASPManager();

		if (itemlay.isMultirowLayout() && itemset.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENOROWS: No Rows Selected."));
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



	public void copyFileTo()
	{
		ASPManager mgr = getASPManager();

		if (itemlay.isMultirowLayout() && itemset.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENOROWS: No Rows Selected."));
			return;
		}
		//Bug Id 67336, start
		if (CheckFileOperationEnable()) 
		{
			return;
		 }
		//Bug Id 67336, end

      //Bug Id 68091, start 
      ASPBuffer buff = mgr.newASPBuffer();
      String action = "GETCOPYTODIR";
		String doc_type = "ORIGINAL";
		String same_action = "YES";
		String cpy_dir = "";

		buff.addItem("DOC_TYPE",doc_type);
		buff.addItem("FILE_ACTION",action);
		buff.addItem("SAME_ACTION_TO_ALL",same_action);
		buff.addItem("SELECTED_DIRECTORY",cpy_dir);
		transferToEdmMacro(buff);
      //Bug Id 68091, end 
	}

	//Bug Id 67336, start
	private boolean CheckFileOperationEnable()
	{
	   ASPManager mgr = getASPManager();
	   
	   if (itemlay.isMultirowLayout())
	   {
	      itemset.storeSelections();
	      itemset.setFilterOn();
	      String prestructure = " ";
	      String structure;
	      if (itemset.countSelectedRows() > 1)
	      {
	         for (int k = 0;k < itemset.countSelectedRows();k++)
	         {
	            structure = itemset.getValue("STRUCTURE");
	            if (" ".equals(prestructure)) 
	            {
	               prestructure = structure;
	            }
	            if (!prestructure.equals(structure)) 
	            {
	               mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
	               itemset.setFilterOff();
	               return true;
	            }
	            if ("1".equals(prestructure) && "1".equals(structure)) 
	            {
	               mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
	               itemset.setFilterOff();
	               return true;
	            }
	            prestructure = structure;
	            itemset.next();
	         }
	      }
	      itemset.setFilterOff();
	   }
	   return false;
	}
	//Bug Id 67336, end

	public void  checkInDocument()
	{
		ASPManager mgr = getASPManager();

		if (itemlay.isMultirowLayout() && itemset.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENOROWS: No Rows Selected."));
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


	//Bug id 88317 Start
   // public String checkCreateNewAllowed()
   // {
   //    ASPManager mgr = getASPManager();
      
   //    trans.clear();
      
   //    cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User", "DUMMY2");
   //    cmd = trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User", "DUMMY1");
   //    cmd.addReference("DUMMY2", "FNDUSER/DATA");
      
   //    trans = mgr.perform(trans);
   //    String person_id = trans.getValue("STARUSER/DATA/DUMMY1");
   //    String output="false";
      
   //    if ("*".equals(person_id)){
         
   //       output="true" ;
   //    }
   //    debug("########## output: " + output);
   //    return output ;
   // }
   
   // public void  execute()
   // {
   //    ASPManager mgr = getASPManager();      
   //    retval = mgr.readValue("execute");
   //    if( "checkCreateNewAllowed".equals(retval) )
   //    {
   //       mgr.responseWrite( checkCreateNewAllowed() );
   //       mgr.endResponse();
   //       debug("########## checkCreateNewAllowed finished");      
   //    }
   // }
   //Bug id 88317 End


	public void  undoCheckOut()
	{
		transferToEdmMacro("ORIGINAL","UNDOCHECKOUT");
	}


	public void  sendToMailRecipient()
	{
		ASPManager mgr = getASPManager();

		transferToEdmMacro("ORIGINAL", "SENDMAIL");
	}


	public void documentInfo()
	{
		ASPManager mgr = getASPManager();

		if (itemlay.isMultirowLayout() && itemset.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCENOROWS: No Rows Selected."));
			return;
		}
		else
		{
			itemset.selectRows();
		}
		
      ASPBuffer buff = itemset.getSelectedRows("DOC_CLASS,SUB_CLASS");
      String doc_class = buff.getValue("DATA/DOC_CLASS");
      String sub_class = buff.getValue("DATA/SUB_CLASS");
      String url = DocmawConstants.getCorrespondingDocIssuePage(doc_class, sub_class);

		mgr.transferDataTo(url, itemset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV"));
	}


	public void  createNewDoc()
	{
		ASPManager mgr = getASPManager();

		// Bug id 88317 start
		// trans.clear();
		
		// cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User", "DUMMY2");
		// cmd = trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User", "DUMMY1");
		// cmd.addReference("DUMMY2", "FNDUSER/DATA");
		
		// trans = mgr.perform(trans);
		// String person_id = trans.getValue("STARUSER/DATA/DUMMY1");
		
		// if ("*".equals(person_id)){
		//    mgr.showAlert(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
		//    return;
		// }
		// Bug id 88317 end



		String lu_name_= headset.getRow().getValue("LU_NAME");
		String key_ref_= headset.getRow().getValue("KEY_REF");

		ASPBuffer transferBuffer = mgr.newASPBuffer();

		transferBuffer.addItem ("ACTION","NEW_DOCUMENT_FOR_CONNECTED_DOCUMENTS");
		transferBuffer.addItem ("LU_NAME", lu_name_);
		transferBuffer.addItem ("KEY_REF", key_ref_);

		mgr.transferDataTo("DocTitleTemp.page", transferBuffer);


		/*
		ASPManager mgr = getASPManager();
		String doc_rev = "",doc_sheet = "",numberGenerator = "",numGenTrx = "",id1="",id2="";

		if (itemset.countRows()>0)
		{
			currrow = itemset.getCurrentRowNo();
			if (itemlay.isMultirowLayout())
				itemset.goTo(itemset.getRowSelected());
			dlg_doc_class = itemset.getValue("DOC_CLASS");
			itemset.goTo(currrow);
		}
		else
			dlg_doc_class = "";

		dlgset.clear();
		trans.clear();

		if (!mgr.isEmpty(dlg_doc_class))
		{
			cmd = trans.addCustomFunction("GETDEFREV","DOC_CLASS_DEFAULT_API.Get_Default_Value_","DLG_DOC_REV");
			cmd.addParameter("DLG_DOC_CLASS",dlg_doc_class);
			cmd.addParameter("TEMP1","DocTitle");
			cmd.addParameter("TEMP2","DOC_REV");

			cmd = trans.addCustomFunction("DEFDOCSHEET","Doc_Class_Default_API.Get_Default_Value_","FIRST_SHEET_NO");
			cmd.addParameter("DLG_DOC_CLASS",dlg_doc_class);
			cmd.addParameter("TEMP1","DocTitle");
			cmd.addParameter("TEMP3","DOC_SHEET");

			cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
			cmd.addParameter("DLG_DOC_CLASS",dlg_doc_class);
			cmd.addParameter("DUMMY1","DocTitle");
			cmd.addParameter("DUMMY2","NUMBER_GENERATOR");

			cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
			cmd.addParameter("DLG_DOC_CLASS",dlg_doc_class);
			cmd.addParameter("DUMMY1","DocTitle");
			cmd.addParameter("DUMMY2","NUMBER_COUNTER");

			trans = mgr.perform(trans);
			doc_rev = trans.getValue("GETDEFREV/DATA/DLG_DOC_REV");
			doc_sheet = trans.getValue("DEFDOCSHEET/DATA/FIRST_SHEET_NO");
			numberGenerator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
			id1 = trans.getValue("ID1COM/DATA/ID1");

			trans.clear();
			cmd = trans.addCustomFunction("GETCLIENTVAL","Doc_Number_Generator_Type_API.Decode","OUT_1");
			cmd.addParameter("NUMBER_GENERATOR",numberGenerator);

			if ("ADVANCED".equals(numberGenerator))
			{
			  cmd = trans.addCustomFunction("ID2COM","Doc_Number_Counter_API.Get_Default_Id2","ID2");
			  cmd.addParameter("DUMMY1",id1);
			}

			trans = mgr.perform(trans);
			numGenTrx = trans.getValue("GETCLIENTVAL/DATA/OUT_1");
			if ("ADVANCED".equals(numberGenerator))
				id2 = trans.getValue("ID2COM/DATA/ID2");
			else
			{
				id1 = "";
				id2 = "";
			}
			if ("0".equals(id2))
			id2 = "";
		}

		dataBuffer = mgr.newASPBuffer();
		dataBuffer.setFieldItem("DLG_DOC_CLASS",dlg_doc_class);
		dataBuffer.setFieldItem("DLG_DOC_NO","*");

		if (!mgr.isEmpty(dlg_doc_class))
		{
			dataBuffer.setFieldItem("FIRST_SHEET_NO",doc_sheet);
			dataBuffer.setFieldItem("DLG_DOC_REV",doc_rev);
			dataBuffer.setFieldItem("NUMBER_GENERATOR",numberGenerator);
			dataBuffer.setFieldItem("NUM_GEN_TRANSLATED",numGenTrx);
			dataBuffer.setFieldItem("ID1",id1);
			dataBuffer.setFieldItem("ID2",id2);
		}

		dataBuffer.setFieldItem("DLG_DOC_TITLE","");
		n = dlgset.addRow(dataBuffer);
		dlgset.goTo(n);
		trans.clear();
		createnew = true;
		*/
	}

	public void createConnectDefDoc()
	{
	   strIFSCliMgrOCX = DocmawUtil.getClientMgrObjectStr();
	   create_connect_def_doc = true;
	}

	public void  insertExistingDoc()
	{
		bInsertExistDoc = true;
	}


	public void  insertExistingDocOk()
	{
		ASPManager mgr = getASPManager();
      String insertedData = mgr.readValue("INSERT_DATA");
      String currDocDetail = "";
      StringTokenizer st = new StringTokenizer(insertedData, ";"); 
      int counter = 0;
      
      while (st.hasMoreTokens())
      {
         currDocDetail = st.nextToken();

         StringTokenizer temSt = new StringTokenizer(currDocDetail, "^"); 
         String docClass     = temSt.nextToken();
         String docClassDesc = temSt.nextToken();
         String docNo        = temSt.nextToken();
         String title        = temSt.nextToken();
         String docSheet     = temSt.nextToken();
         String docRev       = temSt.nextToken();
         String docState     = temSt.nextToken();

          trans.clear();

         cmd = trans.addCustomFunction("LASTDOCREV"+counter,"Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_","KEEP_LAST_DOC_REV");
		   cmd.addParameter("DOC_CLASS",mgr.readValue("RET_DOC_CLASS"));
		   trans = mgr.perform(trans);

		   last_doc_rev = trans.getValue("LASTDOCREV"+counter+"/DATA/KEEP_LAST_DOC_REV");

		   trans.clear();

		   cmd = trans.addCustomFunction("LASTDOCREVDECODE"+counter,"ALWAYS_LAST_DOC_REV_API.Decode","KEEP_LAST_DOC_REV");
		   if (mgr.isEmpty(last_doc_rev))
			   cmd.addParameter("TEMP1","F");
		   else
			   cmd.addParameter("TEMP1",last_doc_rev);

                //Bug Id 85361, Start 
                cmd = trans.addCustomFunction("GETSPECR"+counter,"DOC_ISSUE_API.Get_Spec_Issue","DUMMY");
                cmd.addParameter("DUMMY1",docClass);
                cmd.addParameter("DUMMY1",docNo);
                cmd.addParameter("DUMMY1",docSheet);
                cmd.addParameter("DUMMY1","R");

                cmd = trans.addCustomFunction("GETSPECL"+counter,"DOC_ISSUE_API.Get_Spec_Issue","DUMMY");
                cmd.addParameter("DUMMY1",docClass);
                cmd.addParameter("DUMMY1",docNo);
                cmd.addParameter("DUMMY1",docSheet);
                cmd.addParameter("DUMMY1","L");
		//Bug Id 85361, End

		   trans = mgr.perform(trans);
		   last_doc_rev_decode = trans.getValue("LASTDOCREVDECODE"+counter+"/DATA/KEEP_LAST_DOC_REV");

		//Bug Id 85361, Start 
		StringBuffer sql = new StringBuffer();
		sql.append("Select substr(DECODE(?");
		sql.append(",'F',?");
		sql.append(",'R','");
                sql.append(trans.getValue("GETSPECR"+counter+"/DATA/DUMMY"));
                sql.append("','L','");
                sql.append(trans.getValue("GETSPECL"+counter+"/DATA/DUMMY"));
                sql.append("'),1,6) DOC_REV FROM DUAL");
                trans.clear();
                    
		ASPQuery query = trans.addQuery("GETDOCREV"+counter, sql.toString());
                query.addParameter("KEEP_LAST_DOC_REV_DB",last_doc_rev);
		query.addParameter("DOC_REV",docRev);            
		trans = mgr.validate(trans);
		docRev = trans.getValue("GETDOCREV"+counter+"/DATA/DOC_REV");
		//Bug Id 85361, End

         trans.clear();

         cmd = trans.addEmptyCommand("ITEM" + counter, "DOC_REFERENCE_OBJECT_API.New__", itemblk);
         cmd.setOption("ACTION", "PREPARE");
         cmd.setParameter("LU_NAME", headset.getValue("LU_NAME") );
         cmd.setParameter("KEY_REF", headset.getValue("KEY_REF"));
         cmd.setParameter("DOC_CLASS", docClass);
         cmd.setParameter("DOC_NO", docNo);
         cmd.setParameter("DOC_SHEET", docSheet);
         cmd.setParameter("DOC_REV", docRev);
         trans = mgr.perform(trans); 
         itemset.addRow(trans.getBuffer("ITEM" + counter + "/DATA"));

         ASPBuffer data = itemset.getRow();
         data.setFieldItem("KEEP_LAST_DOC_REV", last_doc_rev_decode);
         data.setFieldItem("DOCTSATUS", docState);
         
         itemset.setRow(data);
		   
         counter++;
      }

      trans.clear();

      if (counter > 0) 
      {
         mgr.submit(trans);         
      }
      okFindITEM();
      itemlay.setLayoutMode(itemlay.MULTIROW_LAYOUT);
	}


	public boolean  isEditable()// if u call this from any other place than editDocument(), make sure u r in correct row in itemset. :bakalk
	{
		ASPManager mgr = getASPManager();

      trans.clear();

		cmd = trans.addCustomFunction("GETEDITACC","DOC_ISSUE_API.Get_Edit_Access_","DUMMY1");
		cmd.addParameter("DOC_CLASS", itemset.getRow().getValue("DOC_CLASS"));
		cmd.addParameter("DOC_NO",    itemset.getRow().getValue("DOC_NO"));
		cmd.addParameter("DOC_SHEET", itemset.getRow().getValue("DOC_SHEET"));
		cmd.addParameter("DOC_REV",   itemset.getRow().getValue("DOC_REV"));

		trans = mgr.perform(trans);
		String accessToEdit = trans.getValue("GETEDITACC/DATA/DUMMY1");
		trans.clear();

		if ("TRUE".equals(accessToEdit))
			return true;
		else
			return false;
	}


	public String getLoginUser()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addCustomFunction("NTSESSION","FND_SESSION_API.Get_Fnd_User","LOGUSER");
		trans = mgr.perform(trans);
		String identity = trans.getValue("NTSESSION/DATA/LOGUSER");
		trans.clear();
		return identity;
	}

   // -----------------------------------------------------------------------------
   // -------------------- Perform Header and Item functions ----------------------
   // -----------------------------------------------------------------------------

   public void performHEAD(String command)
   {
      int currow;

      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      currow = headset.getCurrentRowNo();
      if (headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();
      headset.markSelectedRows(command);
      mgr.submit(trans);
      headset.goTo(currow);
   }

   public void setLock()
   {
      performHEAD("Set_Lock__");
   }
   
   public void setUnlock()
   {
      performHEAD("Set_Unlock__");
   }

	//-----------------------------------------------------------------------------
	//------------------------  CMDBAR BUTTON FUNCTIONS  --------------------------
	//-----------------------------------------------------------------------------

	public void  dlgOk()
	{
		ASPManager mgr = getASPManager();
		String doc_no="";

		createnew = true;
		dlgset.changeRow();
		eval(dlgblk.generateAssignments());

		if (mgr.isEmpty(dlgset.getRow().getValue("DLG_DOC_CLASS")))
		{
			mgr.showError(mgr.translate("DOCMAWDOCREFERENCEVALREQCLASS: Required value for field (Doc. Class)."));
		}
		else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_NO").getValue()))
		{
			mgr.showError(mgr.translate("DOCMAWDOCREFERENCEVALREQDOCNO: Required value for field (Doc. No.)."));
		}
		else if (mgr.isEmpty(mgr.getASPField("FIRST_SHEET_NO").getValue()))
		{
			mgr.showError(mgr.translate("DOCMAWDOCREFERENCEVALREQFIRSTSHEETNO: Required value for field (Doc Sheet)."));
		}
		else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_REV").getValue()))
		{
			mgr.showError(mgr.translate("DOCMAWDOCREFERENCEVALREQDOCREV: Required value for field (Doc. Rev.)."));
		}
		else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_TITLE").getValue()))
		{
			mgr.showError(mgr.translate("DOCMAWDOCREFERENCEVALREQNO: Required value for field (Title)."));
		}
		else
		{
			trans.clear();

			if ("ADVANCED".equals(mgr.readValue("NUMBER_GENERATOR")))
			{
				String sAttr="";
				cmd = trans.addCustomFunction("GENNUMBER","DOC_TITLE_API.Generate_Doc_Number","DLG_DOC_NO");
				cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));
				cmd.addParameter("BOOKING_LIST",mgr.readValue("BOOKING_LIST"));
				cmd.addParameter("ID1",mgr.readValue("ID1"));
				cmd.addParameter("ID2",mgr.readValue("ID2"));
				cmd.addParameter("ATTR",sAttr);
				trans = mgr.perform(trans);
				doc_no = trans.getValue("GENNUMBER/DATA/DLG_DOC_NO");
			}
			trans.clear();

			cmd = trans.addCustomCommand("CRENEWDOCPROC","DOC_TITLE_API.Create_New_Document_");
			cmd.addParameter("DLG_DOC_CLASS",dlgset.getRow().getValue("DLG_DOC_CLASS"));
			if ("ADVANCED".equals(mgr.readValue("NUMBER_GENERATOR")))
			{
				cmd.addParameter("DLG_DOC_NO",doc_no);
			}
			else
			{
				cmd.addParameter("DLG_DOC_NO",dlgset.getRow().getValue("DLG_DOC_NO"));
			}
			cmd.addParameter("FIRST_SHEET_NO",dlgset.getRow().getValue("FIRST_SHEET_NO")); // inoslk - Adding doc sheet
			cmd.addParameter("DLG_DOC_REV",dlgset.getRow().getValue("DLG_DOC_REV"));
			cmd.addParameter("DLG_DOC_TITLE",dlgset.getRow().getValue("DLG_DOC_TITLE"));

			cmd = trans.addCustomCommand("CRENEWREFPROC","DOC_REFERENCE_OBJECT_API.Create_New_Reference__");
			cmd.addParameter("LU_NAME", headset.getValue("LU_NAME"));
			cmd.addParameter("KEY_REF", headset.getValue("KEY_REF"));
			cmd.addReference("DLG_DOC_CLASS","CRENEWDOCPROC/DATA");
			cmd.addReference("DLG_DOC_NO","CRENEWDOCPROC/DATA");
			cmd.addReference("FIRST_SHEET_NO","CRENEWDOCPROC/DATA");
			cmd.addReference("DLG_DOC_REV","CRENEWDOCPROC/DATA");

			trans = mgr.perform(trans);

			dlgset.clear();
			trans.clear();
			createnew = false;
		}
		reload();
	}


	public void  dlgCancel()
	{
		dlgset.clear();
	}

   //Bug Id 89939, start
   public  void startAddingToBriefcase() throws FndException
   {
      ASPManager mgr = getASPManager();
      int noOfRowsSelected = 1;
      int count = 0;
      boolean bReturn = false;
      
      bShowBCLov = true;
      
      if (itemlay.isMultirowLayout())
      {
         itemset.storeSelections();
         itemset.setFilterOn();
         noOfRowsSelected = itemset.countRows();
         itemset.first();
      }
      else
         itemset.selectRow();
      
      trans.clear();
      
      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);
      String    sPrelimin       = dm_const.doc_issue_preliminary;
      String    sApproved       = dm_const.doc_issue_approved;
      String    sReleased       = dm_const.doc_issue_released;
      
      for (count=0; count<noOfRowsSelected; count++)
      {
         if (!((sPrelimin.equals(itemset.getRow().getValue("DOCTSATUS"))) || (sReleased.equals(itemset.getValue("DOCTSATUS"))) || sApproved.equals(itemset.getRow().getValue("DOCTSATUS"))))
         {
            error_msg = mgr.translate("DOCMAWDOCREFERENCENOVALIDSTATE: Only Documents in state Preliminary, Approved or Released can be added to a briefcase");
            bReturn = true;
            break;
         }

         if ("FALSE".equals(itemset.getValue("CAN_ADD_TO_BC")))  
         {
            error_msg = mgr.translate("DOCMAWDOCREFERENCENOEDITACCESSADDTOBRIEF: You must have Edit access to the Document(s) that you want to add to your Briefcase!");
            bReturn = true;
            break;
         }
         
         cmd = trans.addCustomCommand("FILEREFEXIST" + count,"DOC_BRIEFCASE_ISSUE_API.File_Ref_Exist");
         cmd.addParameter("DOC_CLASS",itemset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",itemset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET",itemset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",itemset.getRow().getValue("DOC_REV"));
         cmd.addParameter("DUMMY1","ORIGINAL");
         
         if (itemlay.isMultirowLayout())
         {
            itemset.next();
         }
      }
      try
      {
         mgr.submitEx(trans);
      }
	   catch(Exception e)
      {
         bReturn = true;
         error_msg = mgr.translate("DOCMAWDOCREFERENCEDOCNOFILEREF: Document requires a file reference to connect to a briefcase.");
      }  
      
      if (bReturn)
      {
         bShowBCLov = false;
         if (itemlay.isMultirowLayout())
         {
            itemset.setFilterOff();
         }
         return;
      }
   }

   public void addToBriefcase() throws FndException
   {
      ASPManager mgr = getASPManager();
      int noOfRowsSelected = 1;
      int count = 0;

      boolean bReturn = false;
      String briefcase_no = mgr.readValue("BRIEFCASENO");

              
      if (itemlay.isMultirowLayout())
      {
         itemset.setFilterOn();
         noOfRowsSelected = itemset.countRows();
         itemset.first();
      }

      trans.clear();
      
      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);

      // Has the briefcase been selected
      if (mgr.readValue("BRIEFCASE_SELECTED").equals("FALSE"))
      {
         bShowBCLov = true;
         if (itemlay.isMultirowLayout())
         {
            itemset.setFilterOff();
         }
         return;
      }

      // Check if bc is in state created
      trans.clear();

      q = trans.addQuery("GETBCSTATE","SELECT STATE FROM DOC_BRIEFCASE WHERE BRIEFCASE_NO = ?");
      q.addParameter("BRIEFCASE_NO",briefcase_no);

      trans = mgr.perform(trans);

      String sBcCreated      = dm_const.doc_briefcase_created;
      if (!sBcCreated.equals(trans.getValue("GETBCSTATE/DATA/STATE")))
      {
         mgr.showAlert("DOCMAWDDOCREFERENCEINVALIDBCSTATE: Briefcase must be in state 'Created' in order to add documents.");
         bShowBCLov = false;
         itemset.setFilterOff();
         return;
      }

      bShowBCLov = false;
      trans.clear();

      if (itemlay.isMultirowLayout())
      {
         itemset.setFilterOn();
         itemset.first();
      }

      for (count = 0; count<noOfRowsSelected; count++)
      {
         // Add new record to doc_briecase_issue
         cmd = trans.addCustomCommand("ADDDOCTOBC" + count,"DOC_BRIEFCASE_ISSUE_API.Add_To_Briefcase");
         cmd.addParameter("BRIEFCASE_NO",briefcase_no);
         cmd.addParameter("DOC_CLASS",itemset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",itemset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET",itemset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",itemset.getRow().getValue("DOC_REV"));

         if (itemlay.isMultirowLayout())
         {
            itemset.next();
         }

      }

      mgr.perform(trans);

      if (itemlay.isMultirowLayout())
      {
         itemset.first();
      }
      
      for (count=0;count<noOfRowsSelected;count++)
      {
         itemset.refreshRow();
         if (itemlay.isMultirowLayout())
         {
            itemset.next();
         }
      }

      if (itemlay.isMultirowLayout())
      {
         itemset.setFilterOff();
         itemset.unselectRows();
      }

   }

   public void goToBriefcase()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer temp = mgr.newASPBuffer();
    
      if (itemlay.isMultirowLayout())
      {
         itemset.selectRows();
         itemset.setFilterOn();
      }
      else
         itemset.selectRow();
    
      temp = itemset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      
          
      mgr.transferDataTo("DocBriefcase.page",temp);
      itemset.setFilterOff();
   }
   //Bug Id 89939, end


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.disableDocMan();

		headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();
      
      headblk.addField("OBJSTATE").
      setHidden();
      
      headblk.addField("OBJEVENTS").
      setHidden();

		headblk.addField("LU_NAME").
		setMandatory().
		setMaxLength(8).
		setReadOnly().
		setHidden();

		headblk.addField("KEY_REF").
		setMandatory().
		setMaxLength(600).
		setReadOnly().
		setHidden();

		headblk.addField("VIEW_NAME").
		setHidden().
		setFunction("''");

		headblk.addField("DOC_COUNT").
		setHidden();

		headblk.addField("SLUDESC").
		setSize(20).
		setMaxLength(2000).
		setReadOnly().
		setFunction("OBJECT_CONNECTION_SYS.GET_LOGICAL_UNIT_DESCRIPTION(:LU_NAME)").
		setLabel("DOCMAWDOCREFERENCESLUDESCRIPTION: Object");

		headblk.addField("SINSTANCEDESC").
		setSize(20).
		setMaxLength(2000).
		setReadOnly().
		setFunction("OBJECT_CONNECTION_SYS.GET_INSTANCE_DESCRIPTION(:LU_NAME,'',:KEY_REF)").
		setLabel("DOCMAWDOCREFERENCESINSTANCEDESC: Object Key");

		headblk.addField("DOC_OBJECT_DESC").
		setReadOnly().
		setMaxLength(200).
		setLabel("DOCMAWDOCREFERENCEDOCOBJECTDESC: Object Desc");

		headblk.addField("STATE").
      setReadOnly().
      setSize(10).
      setLabel("DOCMAWDOCREFERENCESTATE: State");
		
		headblk.addField("INFO").
		setHidden().
		setFunction("''");

		headblk.addField("ATTRHEAD").
		setHidden().
		setFunction("''");

		headblk.addField("ACTION").
		setHidden().
		setFunction("''");

		headblk.setView("DOC_REFERENCE");
		headblk.defineCommand("DOC_REFERENCE_API","New__,Modify__,Remove__,Set_Lock__,Set_Unlock__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.BACK);

      headbar.addSecureCustomCommand("SetLock", "DOCMAWDOCREFERENCESETLOCK: Lock", "DOC_REFERENCE_API.Set_Lock__");
      headbar.addSecureCustomCommand("SetUnlock", "DOCMAWDOCREFERENCESETUNLOCK: Unlock", "DOC_REFERENCE_API.Set_Unlock__");

      headbar.addCommandValidConditions("SetLock",   "OBJSTATE", "Enable", "Unlocked");
      headbar.addCommandValidConditions("SetUnlock", "OBJSTATE", "Enable", "Locked");
      
		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("DOCMAWDOCREFERENCECONDOCU: Connected Documents"));


		headlay = headblk.getASPBlockLayout();
		headlay.setDialogColumns(2);
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);


		//
		// Connected documents
		//

		itemblk = mgr.newASPBlock("ITEM");

		itemblk.disableDocMan();

		itemblk.addField("ITEM_OBJID").
		setHidden().
		setDbName("OBJID");

		itemblk.addField("ITEM_OBJVERSION").
		setHidden().
		setDbName("OBJVERSION");

		itemblk.addField("ITEM_LU_NAME").
		setMandatory().
		setHidden().
		setDbName("LU_NAME");

		itemblk.addField("ITEM_KEY_REF").
		setMandatory().
		setHidden().
		setDbName("KEY_REF");
		
		itemblk.addField("VIEW_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      setLabel("DOCMAWDOCREFERENCEVIEWFILE: View File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV", "NEWWIN").
      setAsImageField();
		
		itemblk.addField("CHECK_IN_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      setLabel("DOCMAWDOCREFERENCECHECKINFILE: Check In File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=ORIGINAL", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV", "NEWWIN").
      setAsImageField();

		itemblk.addField("DOC_CLASS").
		setSize(10).
		setMaxLength(12).
		setMandatory().
		setUpperCase().
		setReadOnly().
		setDynamicLOV("DOC_CLASS").
		setLOVProperty("TITLE",mgr.translate("DOCMAWDOCREFERENCEDOCCLASS1: List of Document Class")).
		setCustomValidation("DOC_CLASS","SDOCCLASSNAME,KEEP_LAST_DOC_REV,KEEP_LAST_DOC_REV_DB").//Bug Id 85361
		setLabel("DOCMAWDOCREFERENCEDOCCLASS: Doc Class");

		itemblk.addField("SDOCCLASSNAME").
		setDbName("DOC_NAME").
		setSize(10).
		setReadOnly().
		setLabel("DOCMAWDOCREFERENCESDOCCLASSNAME: Doc Class Desc");
		
		itemblk.addField("SUB_CLASS").
      setSize(10).
      setReadOnly().
      setHidden().
      setDynamicLOV("DOC_SUB_CLASS","DOC_CLASS").
      setLabel("DOCMAWDOCREFERENCESSUBCLASS: Sub Class");
		
		itemblk.addField("SUB_CLASS_NAME").
      setSize(10).
      setReadOnly().
      setLabel("DOCMAWDOCREFERENCESSUBCLASSNAME: Sub Class Name");

		itemblk.addField("DOC_CODE").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCREFERENCESDOCCODE: Doc Code");
		
		itemblk.addField("INNER_DOC_CODE").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCREFERENCESINNERDOCCODE: Inner Doc Code");
		
		itemblk.addField("SDOCTITLE").
		setDbName("DOC_TITLE").
		setSize(40).
		setReadOnly().
		setFieldHyperlink("DocIssue.page", "PAGE_URL", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV").
		setLabel("DOCMAWDOCREFERENCESDOCTITLE: Title");

		itemblk.addField("DOC_REV").
		setSize(20).
		setMandatory().
		setUpperCase().
		setReadOnly().
		setDynamicLOV("DOC_ISSUE","DOC_CLASS,DOC_NO,DOC_SHEET").
		setLOVProperty("TITLE",mgr.translate("DOCMAWDOCREFERENCEDOCREV1: List of Document Revision")).
		setLabel("DOCMAWDOCREFERENCEDOCREV: Doc Rev");
		
		itemblk.addField("DOC_STATE").
      setSize(10).
      setReadOnly().
      setLabel("DOCMAWDOCREFERENCESDOCSTATE: Doc State");
		
		itemblk.addField("CONNECTED_PERSON").
      setSize(10).
      setReadOnly().
      setDynamicLOV("PERSON_INFO_LOV").
      setLabel("DOCMAWDOCREFERENCECONNECTEDPERSON: Connected Person");
		
		itemblk.addField("CONNECTED_PERSON_NAME").
      setSize(10).
      setReadOnly().
      setFunction("Person_Info_API.Get_Name(:CONNECTED_PERSON)").
      setLabel("DOCMAWDOCREFERENCECONNECTEDPERSONNAME: Connected Person Name");
		mgr.getASPField("CONNECTED_PERSON").setValidation("CONNECTED_PERSON_NAME");
		
		itemblk.addField("CONNECTED_DATE", "Date").
      setSize(10).
      setReadOnly().
      setLabel("DOCMAWDOCREFERENCECONNECTEDDATE: Connected Date");
		
		itemblk.addField("SEND_UNIT_NAME").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCREFERENCESSENDUNITNAME: Send Unit Name");
		
		itemblk.addField("SIGN_PERSON").
      setSize(10).
      setReadOnly().
      setLabel("DOCMAWDOCREFERENCESSIGNPERSON: Sign Person");
		
		itemblk.addField("COMPLETE_DATE", "Date").
      setSize(10).
      setReadOnly().
      setLabel("DOCMAWDOCREFERENCESCOMPLETEDATE: Complete Date");

		itemblk.addField("DOCTSATUS").
		setSize(20).
		setReadOnly().
		setFunction("substr(DOC_ISSUE_API.Get_State(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV),1,200)").
		setLabel("DOCMAWDOCREFERENCEDOCTSATUS: Status");

		//
		// Hidden Fields
		//
		
		itemblk.addField("DOC_NO").
      setSize(20).
      setMaxLength(120).
      setMandatory().
      setUpperCase().
      setHidden().
      setLOV("DocNumLov.page","DOC_CLASS").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV","SDOCTITLE,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,SDOCCLASSNAME,KEEP_LAST_DOC_REV,KEEP_LAST_DOC_REV_DB").//Bug Id 85361
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCREFERENCEDOCNO1: List of Document No")).
      setLabel("DOCMAWDOCREFERENCEDOCNO: Doc No");
		
		itemblk.addField("DOC_SHEET").
      setSize(20).
      //Bug 61028, Start
      setMaxLength(10).
      //Bug 61028, End
      setMandatory().
      setUpperCase().
      setHidden().
      setDynamicLOV("DOC_ISSUE_LOV1","DOC_CLASS,DOC_NO,DOC_REV").
      setLOVProperty("TITLE", mgr.translate("DOCMAWDOCREFERENCEDOCSHEET1: List of Doc Sheets")).
      setLabel("DOCMAWDOCREFERENCEDOCSHEET: Doc Sheet");
		
		itemblk.addField("CATEGORY").
      setSize(20).
      setMaxLength(5).
      setUpperCase().
      setHidden().
      setDynamicLOV("DOC_REFERENCE_CATEGORY").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCREFERENCEDOCAT: List of Document Category")).
      setLabel("DOCMAWDOCREFERENCECATEGORY: Association Category");

      itemblk.addField("COPY_FLAG").
      setSize(20).
      setMandatory().
      setSelectBox().
      setHidden().
      enumerateValues("Doc_Reference_Copy_Status_API").
      setLabel("DOCMAWDOCREFERENCECOPYFLAG: Copy Status");

      itemblk.addField("KEEP_LAST_DOC_REV").
      setSize(20).
      setMaxLength(100).
      setMandatory().
      setSelectBox().
      setHidden().
      unsetSearchOnDbColumn().
      enumerateValues("Always_Last_Doc_Rev_API").
      setCustomValidation("KEEP_LAST_DOC_REV,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV","DOC_REV,KEEP_LAST_DOC_REV_DB").//Bug Id 85361
      setLabel("DOCMAWDOCREFERENCEKEEPLASTDOCREV: Update Revision");

      //Bug Id 85361. Start
      itemblk.addField("KEEP_LAST_DOC_REV_DB").
      setHidden().
      unsetSearchOnDbColumn().
      setFunction("Always_Last_Doc_Rev_API.Encode(:KEEP_LAST_DOC_REV)");
      //Bug Id 85361. End

      itemblk.addField("SURVEY_LOCKED_FLAG").
      setSize(20).
      setMandatory().
      setSelectBox().
      setHidden().
      enumerateValues("LOCK_DOCUMENT_SURVEY_API").
      unsetSearchOnDbColumn().
      //Bug 57719, Start
      setCustomValidation("SURVEY_LOCKED_FLAG","SURVEY_LOCKED_FLAG_DB").
      //Bug 57719, End
      setLabel("DOCMAWDOCREFERENCESURVEYLOCKEDFLAG: Doc Connection Status");
		
		itemblk.addField("SURVEY_LOCKED_FLAG_DB").
      setHidden().
      unsetSearchOnDbColumn().
      setFunction("Lock_Document_Survey_Api.Encode(:SURVEY_LOCKED_FLAG)");
		
		itemblk.addField("FILE_TYPE").
		setHidden().
		setFunction("EDM_FILE_API.GET_FILE_TYPE(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,'ORIGINAL')");

		//Bug Id 67336, start
		itemblk.addField("STRUCTURE").
		setHidden().
		setFunction("DOC_TITLE_API.Get_Structure_(DOC_CLASS,DOC_NO)");
		//Bug Id 67336, end

		// Bug Id 89939, start
		itemblk.addField("CAN_ADD_TO_BC").
		setHidden().
		setFunction("DOC_ISSUE_API.can_add_to_bc(DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV)");
		
		itemblk.addField("BRIEFCASE_NO"). 
		setHidden().
		setDynamicLOV("DOC_BC_LOV1").
		setFunction("DOC_BRIEFCASE_ISSUE_API.GET_BRIEFCASE_NO(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV)");
		
		itemblk.addField("EDMSTATUS").
		setHidden().
		setFunction("EDM_FILE_API.GET_DOC_STATE_NO_USER(DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV, 'ORIGINAL')");
		
		itemblk.addField("IS_ELE_DOC").
      setCheckBox("FALSE,TRUE").
      setFunction("EDM_FILE_API.Have_Edm_File(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
      setReadOnly().
      setHidden().
      setLabel("DOCMAWDOCREFERENCEISELEDOC: Is Ele Doc").
      setSize(5);
		
		itemblk.addField("PAGE_URL").
      setFunction("Doc_Issue_API.Get_Page_Url(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
      setReadOnly().
      setHidden();
		
		itemblk.addField("TEMP_EDIT_ACCESS").
		setFunction("NVL(Doc_Class_API.Get_Temp_Doc(:DOC_CLASS), 'FALSE') || NVL(Doc_Issue_API.Get_Edit_Access_For_Rep_(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV), 'FALSE')").
		setHidden();
		
		itemblk.addField("COMP_DOC").
      setFunction("NVL(Doc_Class_API.Get_Comp_Doc(:DOC_CLASS), 'FALSE')").
      setHidden();
		
		itemblk.addField("TEMP_DOC").
      setFunction("NVL(Doc_Class_API.Get_Temp_Doc(:DOC_CLASS), 'FALSE')").
      setHidden();
		
		itemblk.addField("DOC_OBJSTATE").
      setFunction("DOC_ISSUE_API.Get_Objstate__(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
      setHidden().
      setLabel("DOCMAWDOCISSUESTATE: Doc Status");
		
		itemblk.addField("CHECK_CONNECTED_PERSON").
		setFunction("DECODE(connected_person, Person_Info_API.Get_Id_For_User(Fnd_Session_API.Get_Fnd_User), 'TRUE', 'FALSE')").
		setReadOnly().
		setLabel("DOCMAWDOCREFERENCECHECKCONNECTEDPERSON: Check Connected Person").
      setHidden();
		
		itemblk.addField("TRANSFERED").
		setCheckBox("FALSE,TRUE").
		setReadOnly().
      setHidden();
		// Bug Id 89939, end

		itemblk.setView("DOC_REFERENCE_OBJECT");
		itemblk.defineCommand("DOC_REFERENCE_OBJECT_API","New__,Modify__,Remove__");
		itemblk.setMasterBlock(headblk);
		itemset = itemblk.getASPRowSet();
		itembar = mgr.newASPCommandBar(itemblk);
		itembar.enableCommand(itembar.FIND);
		itembar.disableCommand(itembar.NEWROW);
		itembar.disableCommand(itembar.DUPLICATEROW);
		itembar.disableCommand(itembar.OVERVIEWEDIT);
		itembar.disableCommand(itembar.DELETE);
		itembar.disableCommand(itembar.EDITROW);
		
		//Bug 57719, Start, Added check on function checkLocked()
		itembar.defineCommand(itembar.SAVERETURN,"saveReturnITEM","checkLocked()");
		//Bug 57719, End
		itembar.defineCommand(itembar.OKFIND,   "okFindITEMWithErrorMsg");
		itembar.defineCommand(itembar.COUNTFIND,"countFindITEM");
		itembar.defineCommand(itembar.NEWROW,    "newRowITEM");
		itembar.defineCommand(itembar.SAVENEW, "saveNewITEM");
		itembar.defineCommand(itembar.DELETE, "deleteITEM");
		
		//Bug Id 85487, Start
		itembar.addCustomCommand("createNewDoc", mgr.translate("DOCMAWDOCREFERENCECREATEDOC: Create New Document"));
		itembar.addSecureCustomCommand("createConnectDefDoc", mgr.translate("DOCMAWDOCREFERENCECREATECONNDEFDOC: Create And Connect Document..."), "DOC_REFERENCE_OBJECT_API.New__", "../common/images/toolbar/" + mgr.getLanguageCode() + "/createConnectDefDoc.gif", true);
		// itembar.setCmdConfirmMsg("createConnectDefDoc", "DOCMAWDOCREFERENCECREATECONNDEFDOCMSG: Confirm create and connect document?");
		itembar.addSecureCustomCommand("insertExistingDoc",mgr.translate("DOCMAWDOCREFERENCEINEXISTDOC: Insert Existing Document..."), "DOC_REFERENCE_OBJECT_API.New__", "../common/images/toolbar/" + mgr.getLanguageCode() + "/addDocument.gif", true);
		itembar.addCustomCommandSeparator();
		itembar.forceEnableMultiActionCommand("createNewDoc");
		itembar.forceEnableMultiActionCommand("createConnectDefDoc");
		itembar.forceEnableMultiActionCommand("insertExistingDoc");
		itembar.removeFromRowActions("insertExistingDoc");
		itembar.removeFromRowActions("createNewDoc");
		itembar.removeFromRowActions("createConnectDefDoc");
		//Bug Id 85487, End

		//Bug Id 89939, start
		// itembar.addSecureCustomCommand("startAddingToBriefcase","DOCMAWDOCREFERENCEADDTOBC: Add to Briefcase...","DOC_BRIEFCASE_ISSUE_API.Add_To_Briefcase"); 
		// itembar.addCustomCommand("goToBriefcase","DOCMAWDOCREFERENCEGOTOBC: Go to Briefcase"); 
		// itembar.addCommandValidConditions("goToBriefcase","BRIEFCASE_NO","Disable",null);
		//Bug Id 89939, end

		// File Operations
		// itembar.addSecureCustomCommand("editDocument",mgr.translate("DOCMAWDOCREFERENCEEDITDOC: Edit Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
		itembar.addSecureCustomCommand("deleteDoc",mgr.translate("DOCMAWDOCREFERENCEDELETEDOC: Delete"),"DOC_REFERENCE_OBJECT_API.Remove__");  //Bug Id 70286
		itembar.setCmdConfirmMsg("deleteDoc", "DOCMAWDOCREFERENCEDELETEDOCCONFIRM: Confirm delete document(s)?");
		itembar.addCustomCommandSeparator();
		itembar.addSecureCustomCommand("viewOriginal",mgr.translate("DOCMAWDOCREFERENCEVIEVOR: View Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar.addSecureCustomCommand("checkInDocument",mgr.translate("DOCMAWDOCREFERENCECHECKINDOC: Check In Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		// itembar.addSecureCustomCommand("undoCheckOut",mgr.translate("DOCMAWDOCREFERENCEUNDOCHECKOUT: Undo Check Out Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		// itembar.addSecureCustomCommand("viewOriginalWithExternalViewer",mgr.translate("DOCMAWDOCREFERENCEVIEWOREXTVIEWER: View Document with Ext. App"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		// itembar.addSecureCustomCommand("viewCopy",mgr.translate("DOCMAWDOCREFERENCEVIEWCO: View Copy"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		// itembar.addSecureCustomCommand("printDocument",mgr.translate("DOCMAWDOCREFERENCEPRINTDOC: Print Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		// itembar.addSecureCustomCommand("copyFileTo",mgr.translate("DOCMAWDOCISSUECOPYFILETO: Copy File To..."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		// itembar.addSecureCustomCommand("sendToMailRecipient",mgr.translate("DOCMAWDOCREFERENCEWSENDMAIL: Send by E-mail..."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
		itembar.addCustomCommand("documentInfo",mgr.translate("DOCMAWDOCREFERENCEDOCINFO: Document Info..."));

		itembar.addCommandValidConditions("checkInDocument", "TEMP_EDIT_ACCESS", "Enable", "TRUETRUE");
		itembar.addCommandValidConditions("checkInDocument", "CHECK_CONNECTED_PERSON", "Disable", "FALSE");
		itembar.addCommandValidConditions("checkInDocument", "TRANSFERED", "Disable", "TRUE");
		itembar.addCommandValidConditions("checkInDocument", "SURVEY_LOCKED_FLAG_DB", "Disable", "1");
		
		itembar.addCommandValidConditions("deleteDoc", "TEMP_EDIT_ACCESS", "Disable", "TRUEFALSE");
		itembar.addCommandValidConditions("deleteDoc", "CHECK_CONNECTED_PERSON", "Disable", "FALSE");
		itembar.addCommandValidConditions("deleteDoc", "TRANSFERED", "Disable", "TRUE");
		itembar.addCommandValidConditions("deleteDoc", "SURVEY_LOCKED_FLAG_DB", "Disable", "1");
		
		// Add operations to comand groups
		// itembar.addCustomCommandGroup("FILE", mgr.translate("DOCMAWDOCREFERENCEFILECMDGROUP: File Operations"));
		// itembar.setCustomCommandGroup("editDocument", "FILE");
		// itembar.setCustomCommandGroup("checkInDocument", "FILE");
		// itembar.setCustomCommandGroup("undoCheckOut", "FILE");
		// itembar.setCustomCommandGroup("viewOriginal", "FILE");
		// itembar.setCustomCommandGroup("viewOriginalWithExternalViewer", "FILE");
		// itembar.setCustomCommandGroup("viewCopy", "FILE");
		// itembar.setCustomCommandGroup("printDocument", "FILE");
		// itembar.setCustomCommandGroup("copyFileTo", "FILE");


		itembar.enableMultirowAction();
		// itembar.removeFromMultirowAction("viewOriginalWithExternalViewer");
		// itembar.removeFromMultirowAction("undoCheckOut");


		itemtbl = mgr.newASPTable(itemblk);
		itemtbl.setTitle(mgr.translate("DOCMAWDOCREFERENCEDOCUCC: Documents"));
		itemtbl.enableRowSelect();

		itemlay = itemblk.getASPBlockLayout();
		itemlay.setDialogColumns(2);
		itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

		itemlay.setSimple("CONNECTED_PERSON_NAME");
		
		//
		// Create and connect documents
		//

		dlgblk = mgr.newASPBlock("DLG");

		dlgblk.addField("DLG_DOC_CLASS").
		setSize(20).
		setDynamicLOV("DOC_CLASS").
		setMandatory().
		setUpperCase().
		setCustomValidation("DLG_DOC_CLASS","DLG_DOC_REV,FIRST_SHEET_NO,NUMBER_GENERATOR,NUM_GEN_TRANSLATED,ID1,ID2").
		setLabel("DOCMAWDOCREFERENCEDLGDOCCLASS: Doc Class");

		dlgblk.addField("DLG_DOC_NO").
		setSize(20).
		setMandatory().
		setUpperCase().
		setLabel("DOCMAWDOCREFERENCEDLGDOCNO: No");

		dlgblk.addField("FIRST_SHEET_NO").
		setSize(20).
		setMandatory().
		setUpperCase().
		setLabel("DOCMAWDOCREFERENCEFIRSTSHEETNO: First Sheet No");

		dlgblk.addField("DLG_DOC_REV").
		setSize(20).
		setMandatory().
		setUpperCase().
		setLabel("DOCMAWDOCREFERENCEDLGDOCTITLE: Revision");

		dlgblk.addField("DLG_DOC_TITLE").
		setSize(20).
		setMandatory().
		setLabel("DOCMAWDOCREFERENCEDLGDOCREV: Title");

		// Configurable doc no

		dlgblk.addField("NUMBER_GENERATOR").
		setHidden().
		setFunction("''");

		dlgblk.addField("NUM_GEN_TRANSLATED").
		setReadOnly().
		setUpperCase().
		setFunction("''").
		setLabel("DOCREFERENCENUMBERGENERATOR: Number Generator");


		dlgblk.addField("ID1").
		setReadOnly().
		setFunction("''").
		setUpperCase().
		setLOV("Id1Lov.page").
		setLabel("DOCREFERENCENUMBERCOUNTERID1: Number Counter ID1");

		dlgblk.addField("ID2").
		setSize(20).
		setUpperCase().
		setMaxLength(30).
		setFunction("''").
		setLOV("Id2Lov.page","ID1").
		setLabel("DOCREFERENCENUMBERCOUNTERID2: Number Counter ID2");

		dlgblk.addField("BOOKING_LIST").
		setSize(20).
		setMaxLength(30).
		setUpperCase().
		setFunction("''").
		setLOV("BookListLov.page", "ID1,ID2").//Bug Id 73606
		setLabel("DOCREFERENCEBOOKINGLIST: Booking List");

		dlgblk.setTitle(mgr.translate("DOCMAWDOCREFERENCECREANDCONNDOC: Create and Connect New Document"));

		dlgset = dlgblk.getASPRowSet();
		dlgbar = mgr.newASPCommandBar(dlgblk);
		dlgbar.enableCommand(dlgbar.OKFIND);
		dlgbar.defineCommand(dlgbar.OKFIND,"dlgOk");
		dlgbar.enableCommand(dlgbar.CANCELFIND);
		dlgbar.defineCommand(dlgbar.CANCELFIND,"dlgCancel");

		dlglay = dlgblk.getASPBlockLayout();
		dlglay.setDialogColumns(2);
		dlglay.setDefaultLayoutMode(dlglay.CUSTOM_LAYOUT);
		dlglay.setEditable();


		//
		// dummy block
		//

		dummyblk = mgr.newASPBlock("DUMMY");

		dummyblk.addField("DOC_TYPE");
		dummyblk.addField("RETURN");
		dummyblk.addField("ATTR");
		dummyblk.addField("TEMP1");
		dummyblk.addField("TEMP2");
		dummyblk.addField("TEMP3");
		dummyblk.addField("DUMMY");
		dummyblk.addField("DUMMY_TYPE");
		dummyblk.addField("DUMMY1");
		dummyblk.addField("DUMMY2");
		dummyblk.addField("DUMMY3");
		dummyblk.addField("DUMMY4");
		dummyblk.addField("DUMMY5");
		dummyblk.addField("DUMMY6");
		dummyblk.addField("LOGUSER");
		dummyblk.addField("OUT_1");
	}
	
	protected String getImageFieldTag(ASPField imageField, ASPRowSet rowset, int rowNum) throws FndException
   {
      ASPManager mgr = getASPManager();
      String imgSrc = mgr.getASPConfig().getImagesLocation();
      if (rowset.countRows() > 0)
      {
         if ("VIEW_FILE".equals(imageField.getName()))
         {
            if ("TRUE".equals(rowset.getValueAt(rowNum, "IS_ELE_DOC")))
            {
               imgSrc += "folder.gif";
               return "<img src=\""+imgSrc+"\" height=\"16\" width=\"16\" border=\"0\">";
            }
            else
            {
               return "";
            }
         }
         else if ("CHECK_IN_FILE".equals(imageField.getName()))
         {
            if (headset.countRows() > 0 && "Unlocked".equals(headset.getValue("OBJSTATE")))
            {
               if (rowset.countRows() > 0 && 
                   "TRUETRUE".equals(rowset.getValueAt(rowNum, "TEMP_EDIT_ACCESS")) &&
                   "TRUE".equals(rowset.getValueAt(rowNum, "CHECK_CONNECTED_PERSON")) &&
                   "FALSE".equals(rowset.getValueAt(rowNum, "TRANSFERED")) &&
                   "0".equals(rowset.getValueAt(rowNum, "SURVEY_LOCKED_FLAG_DB")))
               {
                  imgSrc += "document_upload.gif";
                  return "<img src=\""+imgSrc+"\" height=\"16\" width=\"16\" border=\"0\">";
               }
            }
         }
      }
      return "";
   }


	public void  adjust()
	{
		ASPManager mgr = getASPManager();
		strBodyTag = mgr.generateBodyTag();

		if (bInsertExistDoc)
			strBodyTag=mgr.replace(strBodyTag,"javascript:onLoad","javascript:onLoad_");

		if (itemlay.isEditLayout())
		{
			mgr.getASPField("DOC_CLASS").setReadOnly();
			mgr.getASPField("DOC_NO").setReadOnly();
			if ("1".equals(itemset.getValue("SURVEY_LOCKED_FLAG_DB")))
			{
				mgr.getASPField("DOC_SHEET").setReadOnly();
				mgr.getASPField("DOC_REV").setReadOnly();
				mgr.getASPField("COPY_FLAG").setReadOnly();
				mgr.getASPField("KEEP_LAST_DOC_REV").setReadOnly();
				mgr.getASPField("SURVEY_LOCKED_FLAG").setReadOnly();
			}
		}

		if (itemset.countRows()== 0 && (itemlay.getLayoutMode()== itemlay.SINGLE_LAYOUT))
		{
			itemlay.setLayoutMode(itemlay.MULTIROW_LAYOUT);
		}
		
		if (itemlay.isSingleLayout())
		{
		   itembar.disableCommand("deleteDoc");
		   if (itemset.countRows() > 0)
		   {
		      if ("TRUEFALSE".equals(itemset.getValue("TEMP_EDIT_ACCESS")) || "FALSE".equals(itemset.getValue("CHECK_CONNECTED_PERSON")) ||
		          "TRUE".equals(itemset.getValue("TRANSFERED")) || "1".equals(itemset.getValue("SURVEY_LOCKED_FLAG_DB")))
		         itembar.disableCommand(itembar.DELETE);
		      else
		         itembar.enableCommand(itembar.DELETE);
		   }
		   else
		      itembar.disableCommand(itembar.DELETE);
		}

		if (!itemlay.isSingleLayout() && !itemlay.isMultirowLayout())
		{
		   itembar.disableCommand("deleteDoc");
         itembar.disableCommand(itembar.DELETE);
		}
		
		setCommandStatus();
	}
	
	private void setCommandStatus()
   {
      // Check MAIN State
      if (headset.countRows() > 0)
      {
         if ("Locked".equals(headset.getValue("OBJSTATE")))
         {
            itembar.disableCommand(itembar.NEWROW);
            itembar.disableCommand(itembar.DUPLICATEROW);
            itembar.disableCommand(itembar.DELETE);
            itembar.disableCommand(itembar.EDITROW);
            itembar.disableCustomCommand("insertExistingDoc");
            itembar.disableCustomCommand("createNewDoc");
            itembar.disableCustomCommand("createConnectDefDoc");
            itembar.disableCustomCommand("checkInDocument");
            itembar.disableCustomCommand("documentInfo");
            itembar.disableCustomCommand("deleteDoc");
         }
      }
   }

	//===============================================================
	//  HTML
	//===============================================================
	protected String getDescription()
	{
		return "DOCMAWDOCREFERENCETITLE: Connected Documents";
	}

	protected String getTitle()
	{
		return "DOCMAWDOCREFERENCETITLE: Connected Documents";
	}

	protected AutoString getContents() throws FndException
	{
		AutoString out = getOutputStream();
		ASPManager mgr = getASPManager();
		boolean show_ocx = false;

		out.clear();

		out.append("<html>\n");
		out.append("<head>\n");
		out.append(mgr.generateHeadTag("DOCMAWDOCREFERENCETITLE: Connected Documents"));
		out.append("</head>\n");
		out.append("<body ");
		out.append(strBodyTag);
		out.append(">\n");
		out.append("<form ");
		out.append(mgr.generateFormTag());
		out.append(">\n");
		out.append("  <input type=\"hidden\" name=\"CHECK_INSERT\" value=\"");
		out.append(insertdoc);
		out.append("\">\n");
		out.append("  <input type=\"hidden\" name=\"INSERT_DATA\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"RET_DOC_CLASS\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"RET_DOC_CLASS_NAME\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"RET_DOC_NO\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"RET_DOC_SHEET\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"RET_DOC_TITLE\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"RET_DOC_REV\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"RET_DOC_STATUS\" value=\"\">\n");
		out.append("  <input type=\"hidden\" name=\"SELECT_INSERT_FILE\" value=\"FALSE\">\n");
		out.append("  <input type=\"hidden\" name=\"REFRESH_ROW\" value=\"FALSE\">\n");
		out.append("  <input type=\"hidden\" NAME=\"DRAG_AND_DROP\" value=\"\">\n");
		out.append("  <input type=\"hidden\" NAME=\"FILES_DROPPED\" value=\"\">\n");
		out.append("  <input type=\"hidden\" NAME=\"CREATE_CONNECT_DEF\" value=\"\">\n");
		out.append(mgr.startPresentation("DOCMAWDOCREFERENCETITLE: Connected Documents"));

                //Bug Id 89939, start
                if (bShowBCLov)
                {
                   out.append("<input type=\"hidden\" name=\"BRIEFCASE_SELECTED\" value=\"FALSE\">\n");
                   out.append("<input type=\"hidden\" name=\"BRIEFCASENO\" value=\"\">\n");
                }
                if (!mgr.isEmpty(this.error_msg))
                   appendDirtyJavaScript("alert(\""+this.error_msg+"\")\n");
                   
                //Bug Id 89939, end  

		if (createnew)
		{
			out.append(dlglay.show());
		}
		else
		{

			if (headlay.isVisible())
			{
				out.append(headlay.show());
			}
			
			if (headset.countRows() > 0)
         {
            if ("Unlocked".equals(headset.getValue("OBJSTATE")))
               show_ocx = true;
            else
               show_ocx = false;
         }

			if (mgr.isExplorer() && show_ocx)
			{
				//Bug Id 76792, start
				out.append("<script type=\"text/JavaScript\" for=\"DropArea\" event=\"DocmanDragDrop()\">\n");

				String unicode_msg = mgr.translate("DOCMAWDOCREFERENCEUNICODECHARS: The Drag and Drop Area does not support adding files with Unicode characters and any such file will be excluded. Do you want to continue?");

				out.append("function document.form.DropArea::OLEDragDrop(data, effect, button, shift, x, y){\n");
				out.append("   var filesDropped = \"\";\n");
				out.append("   var path = \"\";\n");
				out.append("   var fullFileName;\n");
				out.append("   var foundUnicodeFiles = false;\n");
				//out.append(" debugger; \n");
				//Bug id 88317 Start
				// out.append("	var ret =__connectURL('");  
				// out.append(mgr.getURL());
				// out.append("?execute=checkCreateNewAllowed');\n");
				
				// out.append("	  if (ret==\"true\")\n");        
				// out.append("   {\n");
				// out.append("      alert(\"");
				// out.append(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
				// out.append("\")\n");
				// out.append("          return;\n");
				// out.append("   }\n");
				
				//Bug id 88317 End
				out.append("   if(data.GetFormat(15)){\n");
				out.append("      var e = new Enumerator(data.Files);\n");
				out.append("      while(!e.atEnd()){\n");
				out.append("         fullFileName =  \"\" + e.item(); \n");
				out.append("         if (fullFileName.indexOf(\"?\") != -1)\n");
				out.append("            foundUnicodeFiles = true;\n");
				out.append("         else\n");
				out.append("            filesDropped += fullFileName + \"|\"; \n");
				out.append("         e.moveNext();\n");
				out.append("      }\n");
				out.append("   }\n");
				out.append("     filesDropped = filesDropped.substr(0,filesDropped.length-1);\n"); //remove last '|'
				out.append("     document.form.DropArea.Backcolor = oldBackColor;\n");
				out.append("     if (foundUnicodeFiles)\n");
				out.append("     {\n");
				out.append("         if (!confirm(\"" + unicode_msg + "\"))\n");
				out.append("            return;\n");
				out.append("     }\n");
				out.append("     if (filesDropped != \"\")\n");
				out.append("     {\n");
				out.append("         document.form.DRAG_AND_DROP.value =\"YES\";\n");
				out.append("         document.form.FILES_DROPPED.value =filesDropped;\n");
				out.append("         submit();\n");
				out.append("     }\n");
				out.append("}\n");
				out.append(DocmawUtil.writeOleDragOverFunction(mgr.translate("DOCMAWDOCREFERENCEROPOBJECTDROPHERE: Drop files here to create and connect documents"),
				      mgr.translate("DOCMAWDOCREFERENCEDROPOBJECTACCEPTED: Files to drop"),
				      mgr.translate("DOCMAWDOCREFERENCEDROPFILESONLY: Only files are accepted")));
				out.append("</script>  \n");
				//Bug Id 76792, end

				out.append("<table width=300 height=25 align=center>");
				out.append(" <tr>");
				out.append("  <td>");
				out.append(DocmawUtil.drawDnDArea("", ""));
				out.append("  </td>\n");
				out.append(" </tr>");
				out.append("</table >");
				
				// Added by Terry 20140630
				// Show file select dialog
				if (create_connect_def_doc && !mgr.isEmpty(strIFSCliMgrOCX))
				{
				   String file_types_list = getFileTypes();
				   out.append(strIFSCliMgrOCX);
				   appendDirtyJavaScript("file_types = '" + file_types_list + "';\n");
				   appendDirtyJavaScript("var files = document.form.oCliMgr.ShowFileBrowser(\"\", file_types, true);\n");
				   appendDirtyJavaScript("if (files != \"\")\n");
				   appendDirtyJavaScript("{\n");
				   appendDirtyJavaScript("   document.form.DRAG_AND_DROP.value = \"YES\";\n");
				   appendDirtyJavaScript("   document.form.CREATE_CONNECT_DEF.value = \"TRUE\";\n");
				   appendDirtyJavaScript("   document.form.FILES_DROPPED.value = files;\n");
				   appendDirtyJavaScript("   submit();\n");
				   appendDirtyJavaScript("}\n");
				}
				// Added end
			}

			if ((itemlay.isVisible()) && (headset.countRows()>0))
			{
				out.append(itemlay.show());
			}
		}


		//-----------------------------------------------------------------------------
		//----------------------------  CLIENT FUNCTIONS  -----------------------------
		//-----------------------------------------------------------------------------

		if ("true".equals(mgr.getQueryStringValue("bRefreshParent")))
		{
			appendDirtyJavaScript("opener."+mgr.encodeStringForJavascript(mgr.getQueryStringValue("REFRESH_METHOD"))+"(); \n");
		}

		if (bRefreshParent==true)
		{
			appendDirtyJavaScript("opener."+ mgr.encodeStringForJavascript(refreshParentMethod) +"(); \n");
		}
		//Bug Id 85361. Start 
		if (itemlay.isEditLayout()||itemlay.isNewLayout())
		{
                    appendDirtyJavaScript("disableDocRev();\n");
		}
		//Bug Id 85361. End
		appendDirtyJavaScript("var strCategoryIniVal = \"\";\n");
		appendDirtyJavaScript("var strFlag = \"\"\n");

		appendDirtyJavaScript("function lovCategory(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   strCategoryIniVal = getField_('CATEGORY',i).value;\n");
		appendDirtyJavaScript("   strFlag=\"TRUE\";\n");
		appendDirtyJavaScript("   openLOVWindow('CATEGORY',i,'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_REFERENCE_CATEGORY&__FIELD="+ mgr.URLEncode(mgr.translate("DOCMAWDOCREFERENCEASSOCCAT: Document Category")) +"',500,500,'validateCategory');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function validateCategory(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    if( getRowStatus_('ITEM',i)=='QueryMode__' ) \n");
		appendDirtyJavaScript(" return;\n");
		appendDirtyJavaScript("    setDirty();\n");
		appendDirtyJavaScript("    if(!checkCategory(i) ) return;\n");
		appendDirtyJavaScript("    if(strFlag==\"TRUE\")   {\n");
		appendDirtyJavaScript(" strFlag = \"FALSE\";\n");
		appendDirtyJavaScript(" if(strCategoryIniVal.length < 5) {\n");
		appendDirtyJavaScript("     strNewValue = strCategoryIniVal + getField_('CATEGORY',i).value;\n");
		appendDirtyJavaScript("     getField_('CATEGORY',i).value = strNewValue;\n");
		appendDirtyJavaScript(" }\n");
		appendDirtyJavaScript(" else if(strCategoryIniVal.length==5) {\n");
		appendDirtyJavaScript("     strCategoryIniVal = strCategoryIniVal.substr(0,4);\n");
		appendDirtyJavaScript("     strNewValue = strCategoryIniVal + getField_('CATEGORY',i).value;\n");
		appendDirtyJavaScript("     getField_('CATEGORY',i).value = strNewValue;\n");
		appendDirtyJavaScript(" }\n");
		appendDirtyJavaScript("    }\n");
		appendDirtyJavaScript("}\n");

		if (bTranferToEDM)
		{
			appendDirtyJavaScript("   window.open(\"");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
			appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
		}

                appendDirtyJavaScript("function onLoad_()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    message = readCookie(f.__PAGE_ID.value);\n");
		appendDirtyJavaScript("    onLoad();\n");
		appendDirtyJavaScript("    // Open Insert Document LOV and insert data of a existing document\n");
		appendDirtyJavaScript("    if( message.length <= 1 )\n");
		appendDirtyJavaScript(" openDocumentInsert();\n");
		appendDirtyJavaScript("    else {\n");
		appendDirtyJavaScript(" fld = getField_('CHECK_INSERT',-1);\n");
		appendDirtyJavaScript(" fld.value = fld.defaultValue = \"N\";\n");
		appendDirtyJavaScript("    }\n");
		appendDirtyJavaScript("}\n");


		appendDirtyJavaScript("function openDocumentInsert()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    openLOVWindow('INSERT_DATA',-1,'");
		appendDirtyJavaScript(root_path);
		//bug id 84478 (__INIT = 1)
		appendDirtyJavaScript("docmaw/DocIssueLov2.page?MULTICHOICE=true&__INIT=1',500,500,'validateInsertData');\n");
		appendDirtyJavaScript("    fld = getField_('CHECK_INSERT',-1);\n");
		appendDirtyJavaScript("    fld.value = fld.defaultValue = \"N\";\n");
		appendDirtyJavaScript("}\n");


		appendDirtyJavaScript("function validateInsertData(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    j = 0;\n");
		appendDirtyJavaScript("    r = __connect(\n");
		appendDirtyJavaScript("        '");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=INSERT_DATA'\n");
		appendDirtyJavaScript("        + '&INSERT_DATA=' + getValue_('INSERT_DATA',i)\n");
		appendDirtyJavaScript("        );\n");
		appendDirtyJavaScript("    if( checkStatus_(r,'DOC_REV',i,'Doc.Rev.') ) {\n");
		appendDirtyJavaScript("        fld = getField_('CHECK_INSERT',-1);\n");
		appendDirtyJavaScript("        fld.value = fld.defaultValue = \"N\";\n");
		appendDirtyJavaScript("    }\n");
		appendDirtyJavaScript("    ret_val = document.form.INSERT_DATA.value;\n");
		appendDirtyJavaScript("    //alert(ret_val+\"gggggggg\") ;\n");
      /*
		appendDirtyJavaScript("    ret_string_Array=new Array();\n");
		appendDirtyJavaScript("    ret_string_Array=ret_val.split(\"^\");\n");
		appendDirtyJavaScript("    document.form.RET_DOC_CLASS.value = ret_string_Array[0];\n");
		appendDirtyJavaScript("    document.form.RET_DOC_CLASS_NAME.value = ret_string_Array[1];\n");
		appendDirtyJavaScript("    document.form.RET_DOC_NO.value = ret_string_Array[2];\n");
		appendDirtyJavaScript("    document.form.RET_DOC_SHEET.value = ret_string_Array[3];\n");
		appendDirtyJavaScript("    document.form.RET_DOC_TITLE.value = ret_string_Array[4];\n");
		appendDirtyJavaScript("    document.form.RET_DOC_REV.value = ret_string_Array[5];\n");
		appendDirtyJavaScript("    document.form.RET_DOC_STATUS.value = ret_string_Array[6]; \n");
      */
		appendDirtyJavaScript("    document.form.SELECT_INSERT_FILE.value = \"TRUE\";\n");
		appendDirtyJavaScript("    submit();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function refreshParent()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript(" document.form.REFRESH_ROW.value=\"TRUE\"\n");
		appendDirtyJavaScript(" submit() \n");
		appendDirtyJavaScript("}\n");

                //Bug 57719, Start
                appendDirtyJavaScript("function checkLocked()\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("result = true;\n");
                appendDirtyJavaScript("if (document.form.SURVEY_LOCKED_FLAG_DB.value == '1')\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("      if (confirm(\"");
                appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWLOCKWARNING: You are about to lock this object-document connection. The operation is irreversible. Click OK to proceed or Cancel to abort."));
                appendDirtyJavaScript("\")) \n");
                appendDirtyJavaScript("		result = true;\n");
                appendDirtyJavaScript("   else\n");
                appendDirtyJavaScript("           result = false;\n");
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("   return result;\n");
                appendDirtyJavaScript("}\n");
                //Bug 57719, End
		//Bug Id 85361. Start
                appendDirtyJavaScript("function validateDocNo(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
                appendDirtyJavaScript("	setDirty();\n");
                appendDirtyJavaScript(" if(getValue_('DOC_NO',i) == '')\n");
                appendDirtyJavaScript("	        getField_('SDOCTITLE',i).value = '';\n");
                appendDirtyJavaScript("	if( !checkDocNo(i) ) return;\n");
                appendDirtyJavaScript("	if( getValue_('DOC_CLASS',i).indexOf('%') != -1) return;\n");
                appendDirtyJavaScript("	if( getValue_('DOC_NO',i).indexOf('%') != -1) return;\n");
                appendDirtyJavaScript("	if( getValue_('DOC_SHEET',i).indexOf('%') != -1) return;\n");
                appendDirtyJavaScript("	if( getValue_('DOC_REV',i).indexOf('%') != -1) return;\n");
                appendDirtyJavaScript("	window.status='Please wait for validation';\n");
                appendDirtyJavaScript("	 r = __connect(\n");
                appendDirtyJavaScript("		APP_ROOT+ 'docmaw/DocReference.page'+'?VALIDATE=DOC_NO'\n");
                appendDirtyJavaScript("		+ '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
                appendDirtyJavaScript("		+ '&DOC_NO=' + URLClientEncode(getValue_('DOC_NO',i))\n");
                appendDirtyJavaScript("		+ '&DOC_SHEET=' + URLClientEncode(getValue_('DOC_SHEET',i))\n");
                appendDirtyJavaScript("		+ '&DOC_REV=' + URLClientEncode(getValue_('DOC_REV',i))\n");
                appendDirtyJavaScript("		);\n");
                appendDirtyJavaScript("	window.status='';\n");
                appendDirtyJavaScript("	if( checkStatus_(r,'DOC_NO',i,'Doc No') )\n");
                appendDirtyJavaScript("	{\n");
                appendDirtyJavaScript("	   assignValue_('SDOCTITLE',i,0);\n");
		appendDirtyJavaScript("	   assignValue_('DOC_CLASS',i,1);\n");
                appendDirtyJavaScript("	   assignValue_('DOC_NO',i,2);\n");
                appendDirtyJavaScript("	   assignValue_('DOC_SHEET',i,3);\n");
                appendDirtyJavaScript("	   assignValue_('DOC_REV',i,4);\n");
                appendDirtyJavaScript("	   assignValue_('SDOCCLASSNAME',i,5);\n");
                appendDirtyJavaScript("	   assignSelectBoxValue_('KEEP_LAST_DOC_REV',i,6);\n");
                appendDirtyJavaScript("	   assignValue_('KEEP_LAST_DOC_REV_DB',i,7);\n");
                appendDirtyJavaScript("    validateKeepLastDocRev(i);\n");
                appendDirtyJavaScript("	}\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function validateDocSheet(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("    if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
                appendDirtyJavaScript("    setDirty();\n");
                appendDirtyJavaScript("    if( !checkDocSheet(i) ) return;\n");
		appendDirtyJavaScript("       validateKeepLastDocRev(i);\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function validateKeepLastDocRev(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("    if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
                appendDirtyJavaScript("    setDirty();\n");
                appendDirtyJavaScript("    if( !checkKeepLastDocRev(i) ) return;\n");
                appendDirtyJavaScript("    r = __connect(\n");
                appendDirtyJavaScript("        '");
                appendDirtyJavaScript(mgr.getURL());
                appendDirtyJavaScript("?VALIDATE=KEEP_LAST_DOC_REV'\n");
                appendDirtyJavaScript("        + '&KEEP_LAST_DOC_REV=' + getField_('KEEP_LAST_DOC_REV',i).options[getField_('KEEP_LAST_DOC_REV',i).selectedIndex].value\n");
                appendDirtyJavaScript("        + '&DOC_CLASS=' + getValue_('DOC_CLASS',i)\n");
                appendDirtyJavaScript("        + '&DOC_NO=' + getValue_('DOC_NO',i)\n");
                appendDirtyJavaScript("        + '&DOC_SHEET=' + getValue_('DOC_SHEET',i)\n");
                appendDirtyJavaScript("        + '&DOC_REV=' + getValue_('DOC_REV',i)\n");
                appendDirtyJavaScript("        );\n");
                appendDirtyJavaScript("    if( checkStatus_(r,'KEEP_LAST_DOC_REV',i,'Update Revision') )\n");
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("        assignValue_('DOC_REV',i,0);\n");
                appendDirtyJavaScript("        assignValue_('KEEP_LAST_DOC_REV_DB',i,1);\n");
                appendDirtyJavaScript("    }\n");              
                appendDirtyJavaScript("    disableDocRev()\n");
                appendDirtyJavaScript("}\n");
                
               appendDirtyJavaScript("function disableDocRev()\n");
               appendDirtyJavaScript("{\n");
               appendDirtyJavaScript("    if ((document.form.KEEP_LAST_DOC_REV_DB.value == 'F') || (document.form.KEEP_LAST_DOC_REV_DB.value == \"\"))\n");
               appendDirtyJavaScript("       document.form.DOC_REV.readOnly=0;\n");               
               appendDirtyJavaScript("    else  \n");
               appendDirtyJavaScript("       document.form.DOC_REV.readOnly=1;\n");
               appendDirtyJavaScript("}\n");      

               appendDirtyJavaScript("function preLovDocRev(i,params)\n");
               appendDirtyJavaScript("{\n");               
               appendDirtyJavaScript("   if (document.form.DOC_REV.readOnly == 0)\n");
               appendDirtyJavaScript("   {\n");
               appendDirtyJavaScript("      if(params)\n"); 
               appendDirtyJavaScript("         PARAM = params;\n");
               appendDirtyJavaScript("      else\n"); 
               appendDirtyJavaScript("         PARAM = '';\n");
               appendDirtyJavaScript("      var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
               appendDirtyJavaScript("      MULTICH=''+enable_multichoice;\n");
               appendDirtyJavaScript("      MASK ='';\n");	 
               appendDirtyJavaScript("      KEY_VALUE = (getValue_('DOC_REV',i).indexOf('%') !=-1)? getValue_('DOC_REV',i):'';\n");
               appendDirtyJavaScript("	     lovDocRev(i,params);\n");
               appendDirtyJavaScript("   }\n");
               appendDirtyJavaScript("   else\n"); 
               appendDirtyJavaScript("      return;\n"); 
               appendDirtyJavaScript("}\n");               
                //Bug Id 85361. End
		// Bug Id 82596, start
		if(bCloseWindow)
		{
		   bCloseWindow = false;
		   appendDirtyJavaScript("eval(\"opener.refreshParent()\")\n");
		   appendDirtyJavaScript("window.close()\n");
		}
		// Bug Id 82596, end

		//Bug Id 89939, start  for javascript
               if (bShowBCLov)
               {
                  appendDirtyJavaScript("lovBriefcase(-1);");	

                  appendDirtyJavaScript("function lovBriefcase(i)\n");
                  appendDirtyJavaScript("{\n");
                  appendDirtyJavaScript("        openLOVWindow('BRIEFCASENO',i,\n");
                  appendDirtyJavaScript("        '");
                  appendDirtyJavaScript(root_path);
                  appendDirtyJavaScript("common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_BC_LOV1&__FIELD="+mgr.URLEncode (mgr.translate("DOCMAWDOCREFERENCEBCNO: Briefcase No"))+"&__INIT=1&__AUTO%5FSEARCH=N'\n");
                  appendDirtyJavaScript("        ,500,500,'addDocumentToBriefcase');\n");
                  appendDirtyJavaScript("}\n");

                  appendDirtyJavaScript("function addDocumentToBriefcase() {\n");
                  appendDirtyJavaScript("   document.form.BRIEFCASE_SELECTED.value = 'TRUE';");
                  appendDirtyJavaScript("   submit();");
                  appendDirtyJavaScript("}\n");
               }

               //Bug Id 89939, end

		out.append(mgr.endPresentation());
		out.append("</form>\n");
		out.append("</body>\n");
		out.append("</html>");

		return out;
	}

}
