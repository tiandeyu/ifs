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
 *  File        : SearchDocumentContents.java
 *  Modified    :
 *    Diaklk    2001-05-29   Call Id 64971 added methods View Document with Ext. App., Copy File To..., Undo Check Out Document.
 *    Shdilk    2001-07-09   Call Id 66637 Modified editDocument
 *    mdahse    2001-08-16   Fixed bug in addNewSearchResults
 *    Shthlk    2002-12-16   Bug Id 34593, Modified addNewSearchResults(). Check bug id for more info
 *    Nisilk    2003-02-05   Added Doc Sheet
 *    Dikalk    2003-04-02   Replaced calls to getOCXString() with DocmawUtil.getClientMgrObjectStr()
 *    MDAHSE    2003-04-29   Encoded search string to Base54 before sending it to CSS
 *    Shtolk    2003-08-06   2003-2 SP4 Merge: Bug Id 34593.
 *    Inoslk    2003-09-02   Call ID 101731: Modified doReset() and clone().
 *                           Deleted method getStateVals() and added constants from DocmawConstants.
 *                           Deleted unused variables.
 *    DIAKLK    2004-03-23   SP1-Merge. Bug Id 43378: Running macros when editing document that have
 *                           already been checked out
 *    SHTHLK    2005-08-12   Merged Bug Id 52371: Added missing fields as in content search portlet.
 *    BAKALK    2006-01-05   Fixed the call:130703 Added Oracle text search instead of Ms Index Server.
 *                           Remove doReset() and clone()
 *                           Implemented 'count' in find layout which has been made disable.
 *    BAKALK    2006-01-10   Removmed stuff  connected to Index Server which is no longer used.
 *    CHODLK    2006-02-24   Modified method doTextSearch() to check the existance of TXTSER module [Call ID 132659].
 *    CHODLK    2006-02-24   Modified method getContents(),added getCustomHelpBanner() [Call ID 132771 ].
 *    BAKALK    2006-03-27   Fixed call id: 137989
 *    MDAHSE    2006-03-28   Call ID 138069 - Removed Use Natural Language check box.
 *    BAKALK    2006-07-26   Bug ID 58216, Fixed Sql Injection.
 *    JANSLK    2007-08-08   Changed all non-translatable constants.
 *    DINHLK    2007-09-20   Call ID 148858, Added methods okFindWithoutTextSearch(), countFindWithoutTextSearch() 
 *                           and modified methods okFind(), countFind().
 *    SHTHLK    2007-09-17   Bug Id 67645, Modified getSearchValue() to enable navigate through all the records
 *    AMNILK    2007-10-15   Merged Bug Id 67645 to the APP75.
 *    SHTHLK    2007-11-21   Bug Id 64551, Disable content search if TXTSER isn't insalled or if the user doesn't have rights.
 *    DINHLK    2007-11-28   Bug Id 66800, Modified getSearchValue(),getSearchCount(), prepareWhereCondition() and preDefine()
 *    DINHLK    2007-12-04   Bug Id 66800, Removed methods okFindWithoutTextSearch(), countFindWithoutTextSearch() 
 *                           and modified methods okFind(), countFind().
 * ----------------------------------------------------------------------------
 */


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class SearchDocumentContents extends ASPPageProvider
{

	//===============================================================
	// Static constants
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.SearchDocumentContents");


	//===============================================================
	// Instances created on page creation (immutable attributes)
	//===============================================================
	private ASPContext ctx;
	private ASPBlock blk;
	private ASPRowSet rowset;
	private ASPCommandBar cmdbar;
	private ASPTable tbl;
	private ASPBlockLayout lay;
	private ASPBlock blk1;
	private ASPBlock blk2;
	private ASPBlock dummyblk;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned)
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;

	private boolean multirow;
	private boolean bTranferToEDM;
	private boolean bTxtSerExist; //Bug Id 64551

	private String free_text_qry;
	private String SearchString;
	private String SEARCH_NO;
	private String sCheckedIn;
	private String sCheckedOut;
	private String record_separator;
	private String field_separator;
	private String STR;
	private String sFilePath;

	private int counter;

	//===============================================================
	// Construction
	//===============================================================
	public SearchDocumentContents(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	public void run() throws FndException
	{
		ASPManager mgr = getASPManager();

		ctx  = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		multirow = ctx.readFlag("MULTIROW", true);
		record_separator = String.valueOf((char)30);
		field_separator = String.valueOf((char)31);
		bTranferToEDM = false;
		bTxtSerExist = TxtserAvailable();//Bug Id 64551

		DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);
		sCheckedIn  = dm_const.edm_file_check_in;
		sCheckedOut = dm_const.edm_file_check_out;

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered());
		//search();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("STR")));
		//search();

		adjust();
		ctx.writeFlag("MULTIROW", multirow);
	}

	//=============================================================================
	//  Validation
	//=============================================================================

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		mgr.showError("VALIDATE not implemented");
	}

	//=============================================================================
	//  Command Bar functions
	//=============================================================================
	//Bug Id 64551, Start
	public boolean  TxtserAvailable()  
	{
            ASPManager mgr = getASPManager();

	    ASPBuffer buf;
	    boolean btxtSerExist =false;
	    
	    ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	    
	    trans.clear();
	    trans.addSecurityQuery("TEXT_SEARCH_DOMAIN");
	    trans = mgr.perform(trans);
	    buf = trans.getSecurityInfo();    
    
	    if( buf.itemExists("TEXT_SEARCH_DOMAIN"))
	    {
		btxtSerExist = true;
	    }
	    return btxtSerExist;
	}
	//Bug Id 64551, End

	public void  newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("MAIN","DOC_ISSUE_SEARCH_RESULT_API.New__",blk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		ASPBuffer data = trans.getBuffer("MAIN/DATA");
		rowset.addRow(data);
	}

       

	public void  countFind()
	{
		ASPManager mgr = getASPManager();

		free_text_qry = mgr.readValue("FREE_TEXT_QRY");

		if (mgr.isEmpty(free_text_qry))
		{
			mgr.showAlert(mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSMANDATO: The field Free Text Query is mandatory"));
			lay.setLayoutMode(lay.FIND_LAYOUT);         
		}
      
      doTextSearch();
      getSearchCount();      		
	}

   
	public void okFind()
	{
		ASPManager mgr = getASPManager();

		free_text_qry = mgr.readValue("FREE_TEXT_QRY");

		if (mgr.isEmpty(free_text_qry))
		{
		  mgr.showAlert(mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSMANDATO: The field Free Text Query is mandatory"));
        return;         
		}
      
      doTextSearch();
      getSearchValue();              	
	}



	public void getSearchValue()
	{
		ASPManager mgr = getASPManager();
		//Bug Id 66800, Start - Enable query of all columns in the block blk
		mgr.getASPField("FREE_TEXT_QRY").unsetQueryable();
		q = trans.addQuery(blk);
		mgr.getASPField("FREE_TEXT_QRY").setQueryable();
		//Bug Id 66800, End
		prepareWhereCondition(q);
		q.setOrderByClause("hit_rate desc");
		q.includeMeta("ALL");
		mgr.querySubmit(trans,blk);//Bug Id 67645
		free_text_qry = "";

		if (rowset.countRows() == 0)
		{
			mgr.showAlert("DOCMAWSEARCHDOCUMENTCONTENTSNODOCS: No documents matched the query.");
			//eval(blk.regenerateAssignments());
		}
		else
			lay.setLayoutMode(lay.MULTIROW_LAYOUT);
	}



	public void  getSearchCount()
	{
		ASPManager mgr = getASPManager();
		//Bug Id 66800, Start - Enable query of all columns in the block blk
		mgr.getASPField("FREE_TEXT_QRY").unsetQueryable();
		q = trans.addQuery(blk);
		mgr.getASPField("FREE_TEXT_QRY").setQueryable();
		//Bug Id 66800, End
		prepareWhereCondition(q);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		lay.setCountValue(toInt(rowset.getRow().getValue("N")));
		rowset.clear();
	}



	private void prepareWhereCondition(final ASPQuery q)
	{
		String NTUser,CreUser,seq_no;
		String doc_class_,class_desc_,doc_no_,doc_sheet_,doc_rev_,doc_title_,file_state_;
		int pointer1;

		ASPManager mgr = getASPManager();

		NTUser   = mgr.getUserId();
		pointer1 = NTUser.lastIndexOf("\\",NTUser.length());
		CreUser  =  NTUser.substring(pointer1+1, NTUser.length());

		CreUser  = CreUser.toUpperCase( );
		seq_no   = SEARCH_NO;

      //bug 58216 starts
      q.addWhereCondition("USER_CREATED = ? AND SEARCH_NO = ?" );
      q.addParameter("USER_CREATED",CreUser);
      q.addParameter("SEARCH_NO",seq_no);
      //bug 58216 end
	}



	private void deleteOldSearchResults(String sSearchNo, String sUser)
	{
		// Remove all searches older than that specified in the ASPConfig.ifm (default 24hours)

		ASPManager              mgr = getASPManager();
		ASPTransactionBuffer  trans = getASPManager().newASPTransactionBuffer();
		ASPCommand              cmd = mgr.newASPCommand();

		trans.clear();
		cmd = trans.addCustomCommand("DEL", "DOC_ISSUE_SEARCH_RESULT_API.Delete_All");
		cmd.addParameter("SEARCH_NO", sSearchNo);
		cmd.addParameter("USER_CREATED",sUser);
		trans = mgr.perform(trans);
	}


	public void doTextSearch()
	{
		ASPManager mgr = getASPManager();

		String SearchString  = mgr.readValue("FREE_TEXT_QRY");
		String searchId="";

		if (!("".equals(SearchString)))
		{
			String single = "false";
			String myChar;

			for (int i = 0; i < 255; i++)
			{
				myChar = String.valueOf((char)i);
				if (myChar.equals(SearchString))
					single = "true";
			}

			if ("true".equals(single))
			{
				mgr.showAlert(mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSIGNOREWORD: The query contained only ignored words, please refine your query and try again"));
			}
			else if (mgr.isModuleInstalled("TXTSER"))
			{

				String searchDomains = "DOCCON" + (char)31 + "TRUE" + (char)30 ;

				trans.clear();

				cmd = trans.addCustomCommand("SEARCHRESULT","TEXT_SEARCH_API.Search");
				cmd.addParameter("SEARCH_RESULT_ID","");
				cmd.addParameter("SEARCH_STR",SearchString);
				cmd.addParameter("SEARCH_CAT", searchDomains);
				cmd.addParameter("SEARCH_TYPE","ADVANCED");
				cmd.addParameter("MAX_CAT","");
				cmd.addParameter("MAX_TOT","");
				cmd.addParameter("FROM_DOCMAN","TRUE");

				trans = mgr.perform(trans);

				SEARCH_NO = trans.getValue("SEARCHRESULT/DATA/SEARCH_RESULT_ID");
				trans.clear();

			}
			else
			{
				mgr.showAlert(mgr.translate("DOCMAWSERCHDOCUMENTCONTENTSMODULEDOEWSNTEXITS: The Text Search module (TXTSER) is not installed."));
			}
		}
	}


	public boolean  isEditable()
	{
		ASPManager mgr = getASPManager();

		int curr_row;
		if (lay.isMultirowLayout())
			curr_row = rowset.getRowSelected();
		else
			curr_row	= rowset.getCurrentRowNo();

		rowset.goTo(curr_row);

		trans.clear();

		cmd = trans.addCustomFunction("GETEDITACC","DOC_ISSUE_API.Get_Edit_Access_","DUMMY1");
		cmd.addParameter("DOC_CLASS",rowset.getRow().getValue("DOC_CLASS"));
		cmd.addParameter("DOC_NO",rowset.getRow().getValue("DOC_NO"));
		cmd.addParameter("DOC_SHEET",rowset.getRow().getValue("DOC_SHEET"));
		cmd.addParameter("DOC_REV",rowset.getRow().getValue("DOC_REV"));
		trans = mgr.perform(trans);
		String accessToEdit = trans.getValue("GETEDITACC/DATA/DUMMY1");
		trans.clear();

		if ("TRUE".equals(accessToEdit))
			return true;
		else
			return false;
	}

	public String  getLoginUser()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addCustomFunction("NTSESSION","FND_SESSION_API.Get_Fnd_User","LOGUSER");
		trans = mgr.perform(trans);
		String identity = trans.getValue("NTSESSION/DATA/LOGUSER");
		trans.clear();
		//mgr.showAlert("Fnd User" + identity);
		return identity;
	}

	//=============================================================================
	//  Command Bar Custom Group functions
	//=============================================================================

	public void  docinfo()
	{
		ASPManager mgr = getASPManager();
		ASPBuffer keys,row;

		if (lay.isMultirowLayout())
		{
			rowset.storeSelections();
			rowset.setFilterOn();
		}
		else
			rowset.selectRow();

		keys = mgr.newASPBuffer();
		row = keys.addBuffer(String.valueOf(1));
		row.addItem("DOC_NO",rowset.getRow().getValue("DOC_NO"));
		row.addItem("DOC_SHEET",rowset.getRow().getValue("DOC_SHEET"));
		row.addItem("DOC_CLASS",rowset.getRow().getValue("DOC_CLASS"));
		row.addItem("DOC_REV",rowset.getRow().getValue("DOC_REV"));
		rowset.setFilterOff();
		mgr.transferDataTo("DocIssue.page",keys);
	}

	// ========= File operations ====================

	public void  tranferToEdmMacro( String doc_type,String action)
	{
		ASPManager mgr = getASPManager();
		String doc_class,doc_no,doc_sheet,doc_rev,file_type;

		if (lay.isMultirowLayout())
		{
			rowset.storeSelections();
			rowset.setFilterOn();
		}
		// retrieve keys from table
		doc_class = rowset.getValue("DOC_CLASS");
		doc_no = rowset.getValue("DOC_NO");
		doc_sheet = rowset.getValue("DOC_SHEET");
		doc_rev = rowset.getValue("DOC_REV");
		file_type = rowset.getValue("FILE_TYPE");
		bTranferToEDM = true;
		sFilePath  = "EdmMacro.page?DOC_CLASS="+mgr.URLEncode(doc_class);
		sFilePath += "&DOC_NO="+mgr.URLEncode(doc_no);
		sFilePath += "&DOC_SHEET="+mgr.URLEncode(doc_sheet);
		sFilePath += "&DOC_REV="+mgr.URLEncode(doc_rev);
		sFilePath += "&DOC_TYPE="+mgr.URLEncode(doc_type);
		sFilePath += "&FILE_TYPE="+mgr.URLEncode(file_type);
		sFilePath += "&PROCESS_DB="+mgr.URLEncode(action);
		rowset.unselectRows();
		rowset.setFilterOff();
	}


	public void  viewCopy()
	{

		tranferToEdmMacro("VIEW","VIEW");
	}


	public void  viewOriginal()
	{

		tranferToEdmMacro("ORIGINAL","VIEW");
	}


	public void  editDocument()
	{

		String status;
		String checkOutUser;
		String fndUser;
		fndUser = getLoginUser();

		ASPManager mgr = getASPManager();

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
			cmd.addParameter("DOC_CLASS",rowset.getRow().getValue("DOC_CLASS"));
			cmd.addParameter("DOC_NO",rowset.getRow().getValue("DOC_NO"));
			cmd.addParameter("DOC_SHEET",rowset.getRow().getValue("DOC_SHEET"));
			cmd.addParameter("DOC_REV",rowset.getRow().getValue("DOC_REV"));
			cmd.addParameter("DOC_TYPE","ORIGINAL");

			trans = mgr.perform(trans);


			status  = trans.getValue("EDMDOCSTATUS/DATA/DUMMY1");
			checkOutUser = trans.getValue("EDMDOCSTATUS/DATA/DUMMY2");

			trans.clear();

			if ((  sCheckedOut.equals(status ) ) &&  ( !(checkOutUser.equals(fndUser)) ))
			{
				mgr.showAlert(mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSCHECKEDOUTMSG: The document is checked out by another user."));
				return;
			}
			else
				tranferToEdmMacro("ORIGINAL","CHECKOUT");
		}
		else
			mgr.showAlert("DOCMAWSEARCHDOCUMENTCONTENTSCANNOTEDIT: You don't have permission to edit this document");

	}


	public void  printDocument()
	{

		tranferToEdmMacro("ORIGINAL","PRINT");
	}


	public void  checkInDocument()
	{

		tranferToEdmMacro("ORIGINAL","CHECKIN");
	}


	public void getCopyOfFileToDir()
	{

		tranferToEdmMacro("ORIGINAL","GETCOPYTODIR");
	}


	public void  viewOriginalWithExternalViewer()
	{

		tranferToEdmMacro("ORIGINAL","VIEWWITHEXTVIEWER");
	}


	public void  undoCheckOut()
	{
		tranferToEdmMacro("ORIGINAL","UNDOCHECKOUT");
	}





	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		mgr.beginASPEvent();

		blk = mgr.newASPBlock("MAIN");
		blk.setView("DOC_ISSUE_SEARCH_RESULT2");
		blk.defineCommand("DOC_ISSUE_SEARCH_RESULT_API","New__,Modify__,Remove__");

		f = blk.addField("FREE_TEXT_QRY");
		f.setSize(20);
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSFREETEXTQRY: Free Text Query");
		f.setFunction("''");

		f = blk.addField("SEARCH_NO", "Number");
		f.setMandatory();
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSSEARCHNO: Search Number");
		f.setSize(20);
		f.setHidden();
		f = blk.addField("HIT_RATE", "Number");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSSCORE: Score");
		f.setSize(20);
		f.setReadOnly();
		f.unsetQueryable();

		f = blk.addField("DOCUMENT_KEY");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDOCUMENTKEY: Document Key").
		setSize(30);

		f = blk.addField("DOC_CLASS");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDOCCLASS: Document Class");
		f.setSize(20);
		f.setMaxLength(12);
		f.setReadOnly();
		f.setUpperCase();

		f = blk.addField("CLASS_DESC");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSCLASSDESC: Doc Class Description");
		f.setSize(20);
		f.setMaxLength(2000);
		f.setReadOnly();

		f = blk.addField("DOC_NO");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDOCNO: Document Number");
		f.setSize(20);
		f.setMaxLength(120);
		f.setReadOnly();

		f = blk.addField("DOC_SHEET");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDOCSHEET: Document Sheet");
		f.setSize(10);
		f.setMaxLength(10);
		f.setReadOnly();

		f = blk.addField("DOC_REV");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDOCREV: Document Revision");
		f.setSize(20);
		f.setMaxLength(6);
		f.setReadOnly();
		f.setUpperCase();

		f = blk.addField("DOC_TITLE");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDOCTITLE: Document Title");
		f.setSize(20);
		f.setMaxLength(2000);
		f.setReadOnly();

		f = blk.addField("STATE");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSSTATUS: Status");
		

		//Bug Id 66800, Start - Set USER_CREATED hidden and displayed DI_USER_CHANGED  and DI_USER_CHANGED

		f = blk.addField("USER_CREATED");
		f.setHidden();
                
		f = blk.addField("DI_USER_CREATED");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDIUSERCREATED: Created by");

		f = blk.addField("DI_USER_CHANGED");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDIUSERCHANGED: Modified by");
                //Bug Id 66800, End

		f = blk.addField("FILE_STATE");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSFILESTATE: File State");
		f.setSize(20);
		f.setMaxLength(253);
		f.setReadOnly();

		f = blk.addField("DOCUMENT_CONTENTS");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDOCUMENTCONTENTS: Document Contents");
		if (mgr.isExplorer())
			f.setSize(20);
		else
			f.setSize(20);
		f.setMaxLength(2000);
		f.setReadOnly();
		f.unsetQueryable();

		//Bug Id 66800, Start - Replaced DT_CRE with DT_DI_CRE. Added DT_DI_CHG
		f = blk.addField("DT_DI_CRE","Date");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDTCRE: Created Date");

		f = blk.addField("DT_DI_CHG","Date");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDTCHG: Modified Date");
		//Bug Id 66800, End

		f = blk.addField("DT_DI_RELEASED", "Date");
		f.setLabel("DOCMAWSEARCHDOCUMENTCONTENTSDTDIRELEASED: Released Date");

		f = blk.addField("DOC_TYPE");
		f.setFunction("'ORIGINAL'");
		f.setHidden();
		f.setSize(20);

		f = blk.addField("FILE_TYPE");
		f.setHidden();
		f.setFunction("EDM_FILE_API.GET_FILE_TYPE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')");

		//for oracle text search --------------------------
		f = blk.addField("SEARCH_STR");
		f.setHidden();
		f.setFunction("''");

		f = blk.addField("SEARCH_CAT");
		f.setHidden();
		f.setFunction("''");

		f = blk.addField("SEARCH_TYPE");
		f.setHidden();
		f.setFunction("''");

		f = blk.addField("MAX_CAT");
		f.setHidden();
		f.setFunction("''");

		f = blk.addField("MAX_TOT");
		f.setHidden();
		f.setFunction("''");

		f = blk.addField("FROM_DOCMAN");
		f.setHidden();
		f.setFunction("''");

		f = blk.addField("SEARCH_RESULT_ID");
		f.setHidden();
		f.setFunction("''");

		//-------------------------------------------------------

		rowset = blk.getASPRowSet();
		cmdbar = mgr.newASPCommandBar(blk);
		cmdbar.setBorderLines(false,true);

		//----- File Operations -------------------
		cmdbar.addCustomCommand("viewOriginal",mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSVIEVOR: View Document"));
		cmdbar.addCustomCommand("viewCopy",mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSVIEWCO: View Copy"));
		cmdbar.addCustomCommand("viewOriginalWithExternalViewer",mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSVIEWOREXTVIEWER: View Document with Ext. App"));
		cmdbar.addCustomCommand("printDocument",mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSPRINTDOC: Print Document"));
		cmdbar.addCustomCommand("editDocument",mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSEDITDOC: Edit Document"));
		cmdbar.addCustomCommand("checkInDocument",mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSCHECKINDOC: Check In Document"));
		cmdbar.addCustomCommand("undoCheckOut",mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSUNDOCHECKOUT: Undo Check Out Document"));
		cmdbar.addCustomCommand("getCopyOfFileToDir",mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSCOPYFILETO: Copy File To..."));
		//-----------------------------------------
		cmdbar.addCustomCommand("docinfo",mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSDOCINFO: Document Info..."));

		tbl = mgr.newASPTable( blk );
		tbl.setTitle(mgr.translate("DOCMAWSEARCHDOCUMENTCONTENTSSDER: Search Document Contents"));
		lay = blk.getASPBlockLayout();
		lay.setDefaultLayoutMode(lay.SINGLE_LAYOUT);
		lay.setDialogColumns(2);
		tbl.unsetEditable();
		//----------------------------------------------------------------
		blk1 = mgr.newASPBlock("SUB");
		f = blk1.addField( "INFO" );
		f.setHidden();
		f = blk1.addField( "ACTION" );
		f.setHidden();
		f = blk1.addField("ATTR");
		f.setHidden();

		//----------------------------------------------------------------
		blk2 = mgr.newASPBlock("TEST");
		f = blk2.addField( "OBJID" );
		f.setHidden();
		f = blk2.addField( "OBJVERSION" );
		f.setHidden();
		blk2.setView("DOC_ISSUE_SEARCH_RESULT");
		blk2.defineCommand("DOC_ISSUE_SEARCH_RESULT_API","New__,Modify__,Remove__");


		//----------------------------------------------------------------
		dummyblk = mgr.newASPBlock("DUMMY");

		f = dummyblk.addField("DUMMY1");
		f = dummyblk.addField("DUMMY2");
		f = dummyblk.addField("DUMMY3");
		f = dummyblk.addField("DUMMY4");
		f = dummyblk.addField("DUMMY5");
		f = dummyblk.addField("DUMMY6");
		f = dummyblk.addField("LOGUSER");
		f = dummyblk.addField("LU_NAME");
		f = dummyblk.addField("ENTRY_CODE");
	}


	public void  adjust()
	{
		ASPManager mgr = getASPManager();

		if (rowset.countRows() == 0)
		{
			cmdbar.removeCustomCommand("viewCopy");
			cmdbar.removeCustomCommand("viewOriginal");
			cmdbar.removeCustomCommand("docinfo");
			cmdbar.removeCustomCommand("editDocument");
			//cmdbar.removeCustomCommand("printDocument");
			cmdbar.removeCustomCommand("checkInDocument");
			cmdbar.disableCommand(cmdbar.CANCELFIND);
			lay.setLayoutMode(lay.FIND_LAYOUT);
		}
		cmdbar.disableCommand(cmdbar.NEWROW);
		cmdbar.disableCommand(cmdbar.SAVENEW);
		cmdbar.disableCommand(cmdbar.EDITROW);
		cmdbar.disableCommand(cmdbar.DUPLICATEROW);
		cmdbar.disableCommand(cmdbar.DELETE);
		//cmdbar.disableCommand(cmdbar.COUNTFIND);
		cmdbar.removeCommandGroup(cmdbar.CMD_GROUP_EDIT);

		if (lay.isMultirowLayout())
		{
			mgr.getASPField("FREE_TEXT_QRY").setHidden();
		}
	}


	//===============================================================
	//  HTML
	//===============================================================
	private String getCustomHelpBanner()
	{
	    //Bug Id 64551, Start
	    String sWarning="";
	    
	    if (! bTxtSerExist)
	    {
		sWarning =
		"<TABLE id=cntMAIN bgcolor=#FFCC66 height=10 cellSpacing=0 cellPadding=0  width=100% border=0>" +
		"<TBODY>" +
		"<TR>"+
		"<TD width=100% height=30>" +
		"<FONT class=normalTextLabel>" + getASPManager().translate("DOCMAWSEARCHDOCPAGESNOTXTSERMSG: The Text Search (TXTSER) module is not available (either it is not installed or you do not have access to it). Because of this free text search is disabled.") + "</FONT>" +
		"</TD>" +
		"<TR>" +
		"</table>";
	    }
	    else
	    {
		sWarning =
		"<TABLE class=pageFormWithoutBottomLine id=cntMAIN height=10 cellSpacing=0 cellPadding=0  width=100% border=0>" +
		"<TBODY>" +
		"<TR>"+
		"<TD width=100% height=30>" +
		"<FONT class=normalTextLabel>" + getASPManager().translate("DOCMAWSEARCHDOCPAGESIMPLECLIENTHELP: Implements Oracle Text search technology. Note that all searches are case insensitive. Please read the online documentation for more information.") + "</FONT>" +
		"</TD>" +
		"<TR>" +
		"</table>";
	    }
	    //Bug Id 64551, End
	    String content =
			"<TABLE cellSpacing=0 cellPadding=0 width=100% border=0 >" +
			"<TBODY>" +
			"<TR>" +
			"<TD>&nbsp;&nbsp;</TD>" +
			"<td >" +	
			sWarning + //Bug Id 64551
			"</td>" +
			"<TD>&nbsp;&nbsp;</TD>" +
			"</TR>" +
			"</TBODY>" +
			"</TABLE>";

			return content;
	}


	protected String getDescription()
	{
		return "DOCMAWSEARCHDOCUMENTCONTENTSTITLE: Search Document Contents";
	}

	protected String getTitle()
	{
		return "DOCMAWSEARCHDOCUMENTCONTENTSTITLE: Search Document Contents";
	}

	protected AutoString getContents() throws FndException
	{
		AutoString out = getOutputStream();
		out.clear();
		ASPManager mgr = getASPManager();

      out.append("<html>\n");
		out.append("<head>\n");
		out.append(mgr.generateHeadTag("DOCMAWSEARCHDOCUMENTCONTENTSTITLE: Search Document Contents"));
		out.append("</head>\n");
		out.append("<body ");
		out.append(mgr.generateBodyTag());
		out.append(">\n");
		out.append("<form ");
		out.append(mgr.generateFormTag());
		out.append(">\n");
		out.append(mgr.startPresentation("DOCMAWSEARCHDOCUMENTCONTENTSTITLE: Search Document Contents"));
		
      out.append(this.getCustomHelpBanner());

		if (bTxtSerExist && lay.isVisible()) //Bug Id 64551
		{
			out.append(lay.show());
		}
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		if (bTranferToEDM)
		{
			appendDirtyJavaScript("   window.open(\"");
			appendDirtyJavaScript(sFilePath);
			appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");

			appendDirtyJavaScript("function refreshParent()\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("}\n");
		}

		out.append(mgr.endPresentation());
		out.append("</form>\n");
		out.append("</body>\n");
		out.append("</html>");

		return out;

	}

}
