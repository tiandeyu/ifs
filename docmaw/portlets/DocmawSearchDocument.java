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
 * File        : DocmawSearchDocument.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    ToFj  2000-APR-12 - Created.
 *    ToFj  2000-MAY-31 - Modified predefine, added GetASPManager().newASPCommandBar(blk)
 *    Sand  2000-Jun-22 - Corrected the "Advanced search" link.
 *    Sand  2000-Jul-18 - Call ID 45834, Removed Debug message boxes. Set portlet to remember radio button value and last search/
 *    ShDi  2000-Dec-18 - Corrected the Call ID 76499
 *    ShDi  2001-Jan-03 - Call ID 76525 (added the view button)
 *    Baka  2001-Feb_28 - Bug Id 19310  Visibility of View/Edit buttons set as per  current user.
 *
 *    dikalk  2001-Apr-30.. - Removed MS dependant code, made to communicate with ContentSearchServer.class at index server
 The results returned by the ContentSearchServer.class are written to the database
 *    ShDi    2001-Jun-08 - Call ID 66166  Added refreshParent function
 *    ShDi    2001-Jun-19 - Call ID 66417  limited DOCUMENT_CONTENTS fileld up to 100 charactors
 *    PrSa    2001-Jul-03 - Call ID 66763 ,The "import ifs.docmaw.contentssearch.*" command was removed
 *    mdahse  2001-aug-16 - Fixed bug in addNewSearchResults
 *    Prsalk     2003-Jan-16 - Added Doc_Sheet (Sheet Number)
 *    Prsalk     2003-Jan-21 - Replaced "accessStr" with three string variables.
 *    Thwilk     2003-Feb-28 - Bug Id 35985 - Changed the "DocIssue.asp" into "DocIssue.page" in "Advanced Search" Link.
 *    Shthlk     2003-Mar-03 - Bug Id 36085 - Modified addNewSearchResults(). Check bug id for more info
 *    MDAHSE     2003-04-29  Encoded search string to Base54 before sending it to CSS
 *    MDAHSE     2003-04-30  Now uses values from Docman Default for index server URL and catalog
 *    MDAHSE     2003-05-07  Better logic when no hits in index server is found. Behaves
 *                           the same as when doing attribute search.
 *    INOSLK     2003-08-08  SP4 Merge: Bud IDs 35985,36085.
 *    SHTHLK     2004-11-09  Bug Id 47858, Modified run method to initialize SKIP_ROWS for each new find.
 *    SHTHLK     2004-11-09  Bug Id 47794, Modified isAccessibleMethod method to use the person_id instead of the user_id.
 *    BAKALK     2005-05-26  Statnett: Now Oracle Text Search is used to read contents and attributes instead using MS Index Server.
 *    CHODLK     2005-12-14  Merged Statnett text search functionality to the portlet.And code cleanup.
 *    CHODLK     2005-12-16  Layout modifications.
 *    BAKALK     2006-01-10  Removmed stuff  connected to Index Server which is no longer used.
 *    CHODLK     2006-02-24  Modified method run() to check the existance of TXTSER module [Call ID 132659].
 *    CHODLK     2006-02-24  Modified method printContents() [Call ID 132771].
 *    RUCSLK     2006-03-21  Merged the bug 56231
 *    BAKALK     2006-03-27  Fixed call id: 137989
 *    DIKALK     2006-05-31  Bug 57779, Replaced appowner with Docman Administrator
 *    DIKALK     2006-06-05  Bug 57779, Checked to see if a file is connected before showing the view icon
 *    BAKALK     2006-07-26  Bug ID 58216, Fixed Sql Injection.
 *    NaLrlk     2007-08-13  XSS Correction.
 *    SHTHLK     2007-11-21  Bug Id 64551, Disable content search if TXTSER isn't insalled or if the user doesn't have rights.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.docmaw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import java.util.*;
import java.io.*;
import java.lang.*;

/**
 */
public class DocmawSearchDocument extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int size = 15;
	// MDAHSE, 2001-08-16
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.portlets.DocmawSearchDocument");

	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;
   private ASPBuffer data;  //Bug Id 56231.


	//==========================================================================
	//  Mutable attributes
	//==========================================================================


	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================

	private transient int      skip_rows;
	private transient int      db_rows;
	private String             search;  
	private String             searchstore;
	private String             search_no_value;
	private String             search_string;
	private String             searchDomains;  
	private String             database;
	private String             loginUser; //baka
	//private String[]        accessStr;
	private String             sGroup;
	private String             sAll;
	private String             sUser;
   private String             sortFieldVal; //Bug Id 56231
	boolean      docclassset    = false;
	boolean      docnoset          = false;
	boolean      docsheetset          = false;
	boolean      docrevset         = false;
	boolean      docclassdescset   = false;
	boolean      doctitleset       = true;
	boolean      docstatusset      = true;
	boolean      doccrebyset       = true;
	boolean      docmodbyset       = false;
	boolean      docfilestateset   = false;
	boolean      docdtcreset       = false;
	boolean      docdtrelset       = false;
	boolean      doccontset        = false;
	boolean      rbsel        = false;
	private boolean             bContentsOn = false;
	private boolean             bAttributesOn =false;
	private boolean             bObjConnectionsOn =false; // Bug 57079, Added new variable.
	private boolean             bTxtSerExist =false;//Bug Id 64551
 

	//==========================================================================
	//  Construction
	//==========================================================================

	public DocmawSearchDocument( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
	}

	public ASPPage construct() throws FndException
	{
		return super.construct();
	}

	//==========================================================================
	//  Methods for implementation of pool
	//==========================================================================

	protected void doReset() throws FndException
	{
		super.doReset();
		//   no reset of variables since they should be "remembered".
	}

	public ASPPoolElement clone( Object mgr ) throws FndException
	{
		DocmawSearchDocument page = (DocmawSearchDocument)(super.clone(mgr));

		page.ctx    = page.getASPContext();
		page.blk    = page.getASPBlock(blk.getName());
		page.rowset = page.blk.getASPRowSet();
		page.tbl    = page.getASPTable(tbl.getName());

		return page;
	}


	protected void preDefine()
	{
		ctx = getASPContext();

		blk = newASPBlock("MAIN");

		addField(blk, "HIT_RATE","Number").setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSCORE: Score");

		addField(blk, "DOCUMENT_KEY").
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCUMENTKEY: Doc Key").
		setSize(30);

		addField(blk, "DOC_CLASS").
		setHidden().
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCCLASS: Doc Class").
		setSize(12);

		addField(blk, "CLASS_DESC").
		setHidden().
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTCLASSDESC: Doc Class Description");

		addField(blk, "DOC_NO").
		setAlignment("RIGHT").
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCNO: Doc No").
		setHidden().
		setSize(10);

		addField(blk, "DOC_REV").
		setHidden().
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCREV: Doc Revision");

		addField(blk, "DOC_SHEET").
		setHidden().
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCSHEET: Doc Sheet");

		addField(blk, "DOC_TITLE").
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCTITLE: Doc Title").
		setSize(10);

		addField(blk, "STATE").
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENT: Status");

		addField(blk, "DI_USER_CREATED").
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDIUSERCREATED: Created by");

		addField(blk, "DI_USER_CHANGED").
		setHidden().
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDIUSERCHANGED: Modified by");

		addField(blk, "FILE_STATE").
		setHidden().
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTFILESTATE: File State");

		addField(blk, "DOCUMENT_CONTENTS").
		setHidden().
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCUMENTCONTENTS: Doc Contents").
		setSize(15);

		addField(blk, "DT_DI_CRE", "Date").
		setHidden().
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDTDICRE: Date Created");

		addField(blk, "DT_DI_RELEASED", "Date").
		setHidden().
		setLabel("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDTDIRELEASED: Date Released");

		addField(blk, "SEARCH_NO","Number").setFunction("''").setHidden();
		addField(blk, "LOG_USER").setFunction("''").setHidden();
		addField(blk, "ACCESSIBILITY").setFunction("''").setHidden();
		addField(blk, "ACCESSCONTROL").setFunction("''").setHidden();
		addField(blk, "OWNER").setFunction("''").setHidden();
		addField(blk, "GETCLIENTACCESS").setFunction("''").setHidden();

		addField(blk, "INFO").setFunction("''").setHidden();
		addField(blk, "OBJID").setFunction("''").setHidden();
		addField(blk, "OBJVERSION").setFunction("''").setHidden();
		addField(blk, "ATTR").setFunction("''").setHidden();
		addField(blk, "ACTION").setFunction("''").setHidden();

		addField(blk, "DUMMY01").setFunction("''").setHidden();
		addField(blk, "DUMMY02").setFunction("''").setHidden();

		// parameters for search function
		addField(blk, "SEARCH_STR").setFunction("''").setHidden();
		addField(blk, "SEARCH_CAT").setFunction("''").setHidden();
		addField(blk, "SEARCH_TYPE").setFunction("''").setHidden();
		addField(blk, "MAX_CAT").setFunction("''").setHidden();
		addField(blk, "MAX_TOT").setFunction("''").setHidden();
		addField(blk, "FROM_DOCMAN").setFunction("''").setHidden();
		addField(blk, "SEARCH_RESULT_ID").setFunction("''").setHidden();


		addField(blk, "FILE_TYPE").
		setFunction("EDM_FILE_API.GET_FILE_TYPE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')").
		setHidden();

		getASPField("DOCUMENT_KEY").setHyperlink("docmaw/DocIssue.page","DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

		blk.setView("DOC_ISSUE_SEARCH_RESULT2");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

		rowset = blk.getASPRowSet();
		getASPManager().newASPCommandBar(blk);

		init();
	}

	protected void init()
	{

		blk    = getASPBlock("MAIN");
		rowset = blk.getASPRowSet();
		tbl    = getASPTable(blk.getName());
		ASPContext ctx = getASPContext();

		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );
		String cmd = readValue("CMD");

		if ("PREV".equals(cmd) && skip_rows>=size)
		{
			skip_rows -= size;
		}
		else if ("NEXT".equals(cmd) && skip_rows<=db_rows-size)
		{
			skip_rows += size;
		}
		ctx.writeValue("SKIP_ROWS",skip_rows+"");
		loginUser= loginUserMethod();
		//accessStr=getAccessVals();
		getAccessVals();
	}

	private String replaceRecFieldSeparator(String result)
	{
		//replace $@#6546 and $@#6547 substrings in result with standard IFS field and record separaters

		int nIndex;
		String sStart, sRest;

		nIndex = 0;
		while (nIndex!=-1)
		{
			nIndex = result.indexOf("$@#6546");
			if (nIndex!=-1)
			{
				sStart = result.substring(0,nIndex) + String.valueOf((char)31);  //replace with standard field separator
				sRest = result.substring(nIndex + 7);
				result = sStart + sRest;
			}

		}

		nIndex = 0;
		while (nIndex!=-1)
		{
			nIndex = result.indexOf("$@#6547");
			if (nIndex!=-1)
			{
				sStart = result.substring(0,nIndex) + String.valueOf((char)30);  //replace with standard record separator
				sRest = result.substring(nIndex + 7);
				result = sStart + sRest;
			}
		}

		return result;
	}

   
	//Bug Id 56231 Start.
   protected void runCustom() 
   {   
       
       data = getASPManager().newASPBuffer();
       String [] sortFields = {"HIT_RATE", "DOCUMENT_KEY", "DOC_TITLE", "STATE", "DI_USER_CREATED"};

       ASPBuffer temp;

       for (int i = 0; i < sortFields.length; i++)
       {
          temp = data.addBuffer(Integer.toString(i));
          temp.addItem("SORT_FIELD_VAL", sortFields[i]);
          temp.addItem("SORT_FIELD_DESC", getASPField(sortFields[i]).getLabel());
       }       
   }
   //Bug Id 56231 End.

	//-- Bug Id 57079, Start, Added new checkbox for search object connections--
	protected void run()
	{
		String commd = readValue("CMD");
		search_string = readProfileValue("DOCCONTSEARCH", "");
		bContentsOn   = readProfileFlag("CONTENTS_ON",false);
		bAttributesOn  = readProfileFlag("ATRRIBUTES_ON",false);
		bObjConnectionsOn = readProfileFlag("OBJCONNECTIONS_ON",false);
      sortFieldVal      =readProfileValue("SORT_FIELD_VAL", "DOCUMENT_KEY"); //Bug Id 56231.

		//Bug Id 47858, Start
		if ("FIND".equals(commd))
		{
			skip_rows = 0;
			ctx.writeValue("SKIP_ROWS",skip_rows+"");
		}
		else if ("NEXT".equals(commd) || "PREV".equals(commd))
		{
			search_no_value = getASPContext().readValue("LASTSEARCHID",null);
		}

		if ("FIND".equals(commd) || "NEXT".equals(commd) || "PREV".equals(commd))
		{
			ASPManager           mgr    = getASPManager();
			ASPTransactionBuffer trans  = getASPManager().newASPTransactionBuffer();
			ASPTransactionBuffer trans2 = mgr.newASPTransactionBuffer();
			ASPQuery             qry    = trans.addQuery(blk);
			ASPCommand           cmd    = mgr.newASPCommand();
			ASPCommand           cmd2   = mgr.newASPCommand();

			//qry.setOrderByClause("HIT_RATE DESC"); Bug id 56231
			qry.includeMeta("ALL");

			search_string = readValue("SEARCH");
			bContentsOn        = "TRUE".equals(readValue("SEARCH_CONTENTS"));
			bAttributesOn    = "TRUE".equals(readValue("SEARCH_ATTRIBUTES"));
			bObjConnectionsOn = "TRUE".equals(readValue("SEARCH_OBJCONNECTIONS"));
			searchDomains = "";


			if (bContentsOn)
			{
				searchDomains = "DOCCON" + (char)31 + "TRUE" + (char)30 ;
			}

			if (bAttributesOn)
			{
				searchDomains += "DOCATR" + (char)31 + "TRUE" + (char)30  ;
			}

			if (bObjConnectionsOn)
			{
				searchDomains += "OBJCON" + (char)31 + "TRUE" + (char)30  ;
			}

			if ("FIND".equals(commd) && (bAttributesOn || bContentsOn || bObjConnectionsOn))
			{
				if (mgr.isModuleInstalled("TXTSER"))
				{

					trans2.clear();

					cmd = trans2.addCustomCommand("SEARCHRESULT","TEXT_SEARCH_API.Search");
					cmd.setParameter(this,"SEARCH_RESULT_ID","");
					cmd.setParameter(this,"SEARCH_STR",search_string);
					cmd.setParameter(this,"SEARCH_CAT", searchDomains);
					cmd.setParameter(this,"SEARCH_TYPE","ADVANCED");
					cmd.setParameter(this,"MAX_CAT","");
					cmd.setParameter(this,"MAX_TOT","");
					cmd.setParameter(this,"FROM_DOCMAN","TRUE");

					trans2 = mgr.perform(trans2);   

					search_no_value = trans2.getValue("SEARCHRESULT/DATA/SEARCH_RESULT_ID");
					trans2.clear();

					// store what kind of search was performed.
					searchstore = "";
					if (bAttributesOn)
					{
						searchstore = "ATTRIBUTES";
					}
					if (bContentsOn)
					{
						searchstore = searchstore + (mgr.isEmpty(searchstore) ? "CONTENTS" : " AND CONTENTS");
						//Bug Id 56231 Start.
						getASPField("HIT_RATE").unsetHidden();
					}
					if (bObjConnectionsOn)
					{
						searchstore = searchstore + (mgr.isEmpty(searchstore) ? "OBJECT CONNECTIONS" : " AND OBJECT CONNECTIONS");
					}

					// show hit rate for contents search
					//getASPField("HIT_RATE").unsetHidden();

				}
				else
				{
					mgr.showAlert(mgr.translate("DOCMAWSERCHSEACHDOCUMENTMODULEDOEWSNTEXITS: The Text Search module (TXTSER) is not installed."));
				}

			}

          //bug 58216 starts
         qry.addWhereCondition("SEARCH_NO= ?" );
         qry.addParameter(this,"SEARCH_NO",search_no_value);
         //bug 58216 end

         //Bug Id 56231 Start.
         qry.setOrderByClause("'" + sortFieldVal + "'");     
         if ("HIT_RATE".equals(sortFieldVal)) 
             qry.setOrderByClause("HIT_RATE DESC");
         else if ("DOC_TITLE".equals(sortFieldVal)) 
             qry.setOrderByClause("DOC_TITLE");
         else if("DI_USER_CREATED".equals(sortFieldVal)) 
            qry.setOrderByClause("DI_USER_CREATED");
         else if ("STATE".equals(sortFieldVal)) 
             qry.setOrderByClause("STATE");
         else
             qry.setOrderByClause("DOCUMENT_KEY");
         //Bug Id 56231 End.


			qry.setBufferSize(size);
			if ("FIND".equals(commd))
				qry.skipRows(0);
			else
				qry.skipRows(skip_rows);
			submit(trans);
			db_rows = blk.getASPRowSet().countDbRows();
			getASPContext().writeValue("DB_ROWS", db_rows+"" );

			//  Show or hide columns according to customized settings.
			docclassset       = readProfileFlag("DOC_CLASS_SET", false);
			docnoset          = readProfileFlag("DOC_NO_SET", false);
			docsheetset       = readProfileFlag("DOC_SHEET_SET", false);
			docrevset         = readProfileFlag("DOC_REV_SET", false);
			docclassdescset   = readProfileFlag("DOC_CLASS_DESC_SET", false);
			doctitleset       = readProfileFlag("DOC_TITLE_SET", true);
			docstatusset      = readProfileFlag("DOC_STATUS_SET", true);
			doccrebyset       = readProfileFlag("DOC_CRE_BY_SET", true);
			docmodbyset       = readProfileFlag("DOC_MOD_BY_SET", false);
			docfilestateset   = readProfileFlag("DOC_FILE_STATE_SET", false);
			docdtcreset       = readProfileFlag("DOC_DT_CRE_SET", false);
			docdtrelset       = readProfileFlag("DOC_DT_REL_SET", false);
			doccontset        = readProfileFlag("DOC_CONT_SET", false);

			if (docclassset)
				getASPField("DOC_CLASS").unsetHidden();
			else
				getASPField("DOC_CLASS").setHidden();

			if (docnoset)
				getASPField("DOC_NO").unsetHidden();
			else
				getASPField("DOC_NO").setHidden();

			if (docsheetset)
				getASPField("DOC_SHEET").unsetHidden();
			else
				getASPField("DOC_SHEET").setHidden();


			if (docrevset)
				getASPField("DOC_REV").unsetHidden();
			else
				getASPField("DOC_REV").setHidden();

			if (docclassdescset)
				getASPField("CLASS_DESC").unsetHidden();
			else
				getASPField("CLASS_DESC").setHidden();

			if (doctitleset)
				getASPField("DOC_TITLE").unsetHidden();
			else
				getASPField("DOC_TITLE").setHidden();

			if (docstatusset)
				getASPField("STATE").unsetHidden();
			else
				getASPField("STATE").setHidden();

			if (doccrebyset)
				getASPField("DI_USER_CREATED").unsetHidden();
			else
				getASPField("DI_USER_CREATED").setHidden();

			if (docmodbyset)
				getASPField("DI_USER_CHANGED").unsetHidden();
			else
				getASPField("DI_USER_CHANGED").setHidden();

			if (docfilestateset)
				getASPField("FILE_STATE").unsetHidden();
			else
				getASPField("FILE_STATE").setHidden();

			if (docdtcreset)
				getASPField("DT_DI_CRE").unsetHidden();
			else
				getASPField("DT_DI_CRE").setHidden();

			if (docdtrelset)
				getASPField("DT_DI_RELEASED").unsetHidden();
			else
				getASPField("DT_DI_RELEASED").setHidden();

			if (doccontset)
				getASPField("DOCUMENT_CONTENTS").unsetHidden();
			else
				getASPField("DOCUMENT_CONTENTS").setHidden();

		}
		getASPContext().writeValue("LASTSEARCHID", search_no_value);
		writeProfileValue("DOCCONTSEARCH", search_string);
		writeProfileFlag("CONTENTS_ON", bContentsOn);
		writeProfileFlag("ATRRIBUTES_ON", bAttributesOn);
		writeProfileFlag("OBJCONNECTIONS_ON", bObjConnectionsOn);

	}
	//-- Bug Id 57079, End --
	//==========================================================================
	//
	//==========================================================================

	public String getTitle( int mode )
	{
		return(translate(getDescription()) + " " + (("FIRST".equals(search)) ? "" : nvl(search,nvl(searchstore,"")) + "-") + nvl(search_string,""));
	}

	public void printCustomTable() throws FndException
	{
		ASPManager mgr = getASPManager();

		beginTable();
		beginTableTitleRow();
		if (!getASPField("HIT_RATE").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSCORE: Score"));
		if (!getASPField("DOCUMENT_KEY").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCUMENTKEY: Doc Key"));
		if (!getASPField("DOC_CLASS").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCCLASS: Doc Class"));

		if (!getASPField("CLASS_DESC").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTCLASSDESC: Doc Class Description"));
		if (!getASPField("DOC_NO").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCNO: Doc No"));
		if (!getASPField("DOC_SHEET").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCSHEET: Doc Sheet"));
		if (!getASPField("DOC_REV").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCREV: Doc Revision"));

		if (!getASPField("DOC_TITLE").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCTITLE: Doc Title"));
		if (!getASPField("STATE").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENT: Status"));
		if (!getASPField("DI_USER_CREATED").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDIUSERCREATED: Created by"));

		if (!getASPField("DI_USER_CHANGED").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDIUSERCHANGED: Modified by"));
		if (!getASPField("FILE_STATE").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTFILESTATE: File State"));
		if (!getASPField("DOCUMENT_CONTENTS").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCUMENTCONTENTS: Doc Contents"),175,1,"centre");
		if (!getASPField("DT_DI_CRE").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDTDICRE: Date Created"));
		if (!getASPField("DT_DI_RELEASED").isHidden())
			printTableCell(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDTDIRELEASED: Date Released"));


		printTableCell("");
		endTableTitleRow();
		beginTableBody();
		boolean isAccessible; //baka  local variables
		String myDocClass;
		String myDocNo;
		String myDocSheet;
		String myDocRev;
		String myPersonId;
      //Bug 57779, Start
      boolean docman_admin = isUserDocmanAdministrator();
      //Bug 57779, End

      rowset.first();
		for (int i=0; i<rowset.countRows(); i++)
		{
			if (!getASPField("HIT_RATE").isHidden())
			{
				beginTableCell();
				printText(rowset.getRow().getFieldValue(this, "HIT_RATE"));
				endTableCell();

			}

			String url =   "docmaw/DocIssue.page"+
								"?DOC_CLASS="+mgr.URLEncode(rowset.getValue("DOC_CLASS"))+
								"&DOC_NO="+mgr.URLEncode(rowset.getValue("DOC_NO"))+
								"&DOC_SHEET="+mgr.URLEncode(rowset.getValue("DOC_SHEET"))+
								"&DOC_REV="+mgr.URLEncode(rowset.getValue("DOC_REV"));

			if (!getASPField("DOCUMENT_KEY").isHidden())
			{
				beginTableCell();
				printLink(rowset.getValue("DOCUMENT_KEY"),url);
				endTableCell();
			}

			if (!getASPField("DOC_CLASS").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("DOC_CLASS"));
				endTableCell();
			}

			if (!getASPField("CLASS_DESC").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("CLASS_DESC"));
				endTableCell();
			}

			if (!getASPField("DOC_NO").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("DOC_NO"));
				endTableCell();
			}

			if (!getASPField("DOC_SHEET").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("DOC_SHEET"));
				endTableCell();
			}

			if (!getASPField("DOC_REV").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("DOC_REV"));
				endTableCell();
			}

			if (!getASPField("DOC_TITLE").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("DOC_TITLE"));
				endTableCell();
			}

			if (!getASPField("STATE").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("STATE"));
				endTableCell();
			}

			if (!getASPField("DI_USER_CREATED").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("DI_USER_CREATED"));
				endTableCell();
			}

			if (!getASPField("DI_USER_CHANGED").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("DI_USER_CHANGED"));
				endTableCell();
			}

			if (!getASPField("FILE_STATE").isHidden())
			{
				beginTableCell();
				printText(rowset.getValue("FILE_STATE"));
				endTableCell();
			}

			if (!getASPField("DOCUMENT_CONTENTS").isHidden())
			{
				beginTableCell(175,4,"left");
				printText(rowset.getValue("DOCUMENT_CONTENTS"));
				endTableCell();
			}

			if (!getASPField("DT_DI_CRE").isHidden())
			{
				beginTableCell();
				printText(rowset.getRow().getFieldValue(this,"DT_DI_CRE"));
				endTableCell();
			}

			if (!getASPField("DT_DI_RELEASED").isHidden())
			{
				beginTableCell();
				printText(rowset.getRow().getFieldValue(this,"DT_DI_RELEASED"));
				endTableCell();
			}

			String sFilePath  = "docmaw/EdmMacro.page?DOC_CLASS="+mgr.URLEncode(rowset.getValue("DOC_CLASS"));
			sFilePath += "&DOC_NO="+mgr.URLEncode(rowset.getValue("DOC_NO"));
			sFilePath += "&DOC_SHEET="+mgr.URLEncode(rowset.getValue("DOC_SHEET"));
			sFilePath += "&DOC_REV="+mgr.URLEncode(rowset.getValue("DOC_REV"));
			sFilePath += "&DOC_TYPE=ORIGINAL";
			sFilePath += "&FILE_TYPE="+mgr.URLEncode(rowset.getValue("FILE_TYPE"));
			sFilePath += "&PROCESS_DB=VIEW";
			myDocClass=rowset.getValue("DOC_CLASS");
			myDocNo=rowset.getValue("DOC_NO");
			myDocSheet=rowset.getValue("DOC_SHEET");
			myDocRev=rowset.getValue("DOC_REV"); ////

			String accessControl=getAccessControl(myDocClass,myDocNo,myDocSheet,myDocRev);

         // Bug 57779, Start
         isAccessible = false;
			if (sAll.equals(accessControl))
				isAccessible=true;
			//else if(accessStr[2].equals(accessControl)){
			else if (sUser.equals(accessControl))
			{
				if (loginUser.equals(rowset.getValue("DI_USER_CREATED")) || docman_admin)
					isAccessible=true;
				else
					isAccessible=false;
			}
         else if (sGroup.equals(accessControl))
         {
            isAccessible = (isAccessibleMethod(myDocClass,myDocNo,myDocSheet,myDocRev,loginUser) || docman_admin);
         }

         // Bug 57779, End

			String noAcc=mgr.translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTNOACCESS: NOAccess");

			beginTableCell();
			if (isAccessible && !mgr.isEmpty(rowset.getValue("FILE_STATE"))) // Bug 57779
				appendToHTML("<a href=\"javascript:"+addProviderPrefix()+"openInNewWindow('"+ mgr.HTMLEncode(sFilePath) +"');\"><img border=0 src='docmaw/images/open.gif' alt='"+ mgr.HTMLEncode(translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDOCMAWNEWDOCUMENTSALTVIEW: View")) +"'></a>");
			endTableCell();

			nextTableRow();
			rowset.next();
		}
		rowset.first();

		endTableBody();
		endTable();
	}


	public void printContents() throws FndException
	{
	    //Bug Id 64551, Start, Show the form only if TXTSER is there and user has rights
	    bTxtSerExist= TxtserAvailable();

	    if (bTxtSerExist)
	    {
		
	    
		printHiddenField("CMD","");

		beginTransparentTable();

		nextTableRow();
		beginTableCell();
		printText("DOCMAWSEARCHDOCUMENTCONTENTSUSERHELPTXT: Implements Oracle Text search technology. Note that all searches are case insensitive. Please read the online documentation for more information.");
		endTableCell();

		nextTableRow();
		beginTableCell();
		printSpaces(1);
		endTableCell();

		nextTableRow();
		setFontStyle(BOLD);
		beginTableCell();
		printField("SEARCH", search_string, 30);
		printSpaces(2);
		printSubmitButton("SUBMITSEARCH","DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSUBMITBUTTONSEARCH: Search", "performSearchDoc");
		endTableCell();
		endFont();

		nextTableRow();
		beginTableCell(CENTER,true);
		printSpaces(50);
		printLink("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTAAS: Advanced Search","docmaw/DocIssue.page?FINDLAY=TRUE");
		endTableCell();

		nextTableRow();
		beginTableCell();
		printText("DOCMAWPORTLETSDOCMAWSEARCHOPTIONTEXT: Search Options:");
		endTableCell();

		nextTableRow();
		beginTableCell(LEFT);
		printSpaces(3);
		printCheckBox("SEARCH_ATTRIBUTES", bAttributesOn);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTATTRIB:  Attributes");
		endTableCell();

		nextTableRow();
		beginTableCell(LEFT);
		printSpaces(3);
		printCheckBox("SEARCH_CONTENTS", bContentsOn);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTCONTENS:  Contents");
		endTableCell();

		nextTableRow();
		beginTableCell(LEFT);
		printSpaces(3);
		printCheckBox("SEARCH_OBJCONNECTIONS", bObjConnectionsOn);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTOBJCONNECTIONS:  Object Connections");
		endTableCell();

		endTable();

		printCustomTable();
	    }
	    else
	    {
		beginTransparentTable();
                
		nextTableRow();
		beginTableCell();
		printSpaces(1);
		endTableCell();
		
		nextTableRow();
		beginTableCell();
		appendToHTML("<div  id='_" + getId() + "_crtdoc_status' style='background-color:#FFCC66'>\n");
		printText("DOCMAWSEARCHDOCUMENTCONTENTSNOTXTSERMSG: The Text Search (TXTSER) module is not available (either it is not installed or you do not have access to it). Because of this free text search is disabled.");
		appendToHTML("</div>\n");
		endTableCell();
                endTable();
	    }
	    //Bug Id 64551, End

		if (size < db_rows)
		{
			printNewLine();

			if (skip_rows>0)
				printSubmitLink("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTPRV: Previous","prevCust");
			else
				printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTPRV: Previous");

			printSpaces(5);
			String rows = translate("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
			printText( rows );
			printSpaces(5);

			if (skip_rows<db_rows-size)
				printSubmitLink("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTNEXT: Next","nextCust");
			else
				printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTNEXT: Next");

			printNewLine();
			printNewLine();

			appendDirtyJavaScript(
										"function prevCust(obj,id)"+
										"{"+
										"   getPortletField(id,'CMD').value = 'PREV';"+
										"}\n"+
										"function nextCust(obj,id)"+
										"{"+
										"   getPortletField(id,'CMD').value = 'NEXT';"+
										"}\n");
		}
		else
			printNewLine();

		appendDirtyJavaScript(
									"function findCust(obj,id)"+
									"{"+
									"   getPortletField(id,'CMD').value = 'FIND';\n"+
									"}\n"+
									"function performSearchDoc(obj,id)"+
									"{"+
									" getPortletField(id,'CMD').value = 'FIND';\n"+
									"}\n");

		appendDirtyJavaScript(
									"function "+addProviderPrefix()+"openInNewWindow( file_path )\n"+
									"{ \n"+"   window.open(file_path,'anotherWindow','status,resizable,scrollbars,width=500,height=500,left=100,top=100');\n"+"} \n");

		appendDirtyJavaScript(
									"function refreshParent()"+
									"{"+
									"}\n");
	}

	public String getBoxStyle( int mode )
	{
		return "";
	}

	public static String getDescription()
	{
		return "DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTDESC: Search Document";
	}

	public boolean canCustomize()
	{
		return true;
	}

	private String nvl( String str, String instead_of_empty )
	{
		return Str.isEmpty(str) ? instead_of_empty : str;
	}

	public void printCustomBody()
	{
		ASPManager           mgr   = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      //Bug Id 56231 Start.
      docclassset       = readProfileFlag("DOC_CLASS_SET", false);
      docnoset       = readProfileFlag("DOC_NO_SET", false);
      docsheetset       = readProfileFlag("DOC_SHEET_SET", false);
      docrevset       = readProfileFlag("DOC_REV_SET", false);
      docclassdescset       = readProfileFlag("DOC_CLASS_DESC_SET", false);
      docmodbyset       = readProfileFlag("DOC_MOD_BY_SET", false);
      docfilestateset       = readProfileFlag("DOC_FILE_STATE_SET", false);
      docdtcreset       = readProfileFlag("DOC_DT_CRE_SET", false);
      docdtrelset       = readProfileFlag("DOC_DT_REL_SET", false);
      doccontset       = readProfileFlag("DOC_CONT_SET", false);
      sortFieldVal      =readProfileValue("SORT_FIELD_VAL", "DOCUMENT_KEY"); 
      //Bug Id 56231 End

		printNewLine();
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETHEAD:  Select the columns to show in the result:");

		printNewLine();
		printCheckBox("DOC_CLASS_SET", docclassset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCCLASS:   Document class");
		printNewLine();

		printCheckBox("DOC_NO_SET", docnoset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCNO:   Document number");
		printNewLine();

		printCheckBox("DOC_SHEET_SET", docsheetset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCSHEET:   Document sheet");
		printNewLine();

		printCheckBox("DOC_REV_SET", docrevset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCREV:   Document revision");
		printNewLine();

		printCheckBox("DOC_CLASS_DESC_SET", docclassdescset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCDESC:   Document class description");
		printNewLine();

		printCheckBox("DOC_TITLE_SET", doctitleset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCTITLE:   Document title");
		printNewLine();

		printCheckBox("DOC_STATUS_SET", docstatusset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCSTAT:   Document status");
		printNewLine();

		printCheckBox("DOC_CRE_BY_SET", doccrebyset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCCREBY:   Document creator");
		printNewLine();

		printCheckBox("DOC_MOD_BY_SET", docmodbyset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCMODBY:   Last document modificator");
		printNewLine();

		printCheckBox("DOC_FILE_STATE_SET", docfilestateset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCFILE:   Document file state");
		printNewLine();

		printCheckBox("DOC_DT_CRE_SET", docdtcreset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCDTCRE:   Document creation date");
		printNewLine();

		printCheckBox("DOC_DT_REL_SET", docdtrelset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETDOCDTREL:   Document release date");
		printNewLine();

		printCheckBox("DOC_CONT_SET", doccontset);
		printText("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSETCONTENTS:   Document contents");

      //Bug Id 56231 Start.
      printNewLine(); 
      printNewLine();
      printText("DOCMAWPORTLETSDOCMAWSEARCHSORTBY:   Sort By : ");
      printSpaces(10);
      printMandatorySelectBox("SORT_FIELD_VAL", data, sortFieldVal);
      printNewLine();
      printNewLine();
      printNewLine();
      printNewLine();
      //Bug Id 56231 End.

		trans = mgr.perform(trans);
	}

	public void submitCustomization()
	{
		docclassset   = "TRUE".equals(readValue("DOC_CLASS_SET"));
		writeProfileFlag("DOC_CLASS_SET", docclassset);

		docnoset   = "TRUE".equals(readValue("DOC_NO_SET"));
		writeProfileFlag("DOC_NO_SET", docnoset);

		docsheetset   = "TRUE".equals(readValue("DOC_SHEET_SET"));
		writeProfileFlag("DOC_SHEET_SET", docsheetset);

		docrevset   = "TRUE".equals(readValue("DOC_REV_SET"));
		writeProfileFlag("DOC_REV_SET", docrevset);

		docclassdescset   = "TRUE".equals(readValue("DOC_CLASS_DESC_SET"));
		writeProfileFlag("DOC_CLASS_DESC_SET", docclassdescset);

		doctitleset   = "TRUE".equals(readValue("DOC_TITLE_SET"));
		writeProfileFlag("DOC_TITLE_SET", doctitleset);

		docstatusset   = "TRUE".equals(readValue("DOC_STATUS_SET"));
		writeProfileFlag("DOC_STATUS_SET", docstatusset);

		doccrebyset   = "TRUE".equals(readValue("DOC_CRE_BY_SET"));
		writeProfileFlag("DOC_CRE_BY_SET", doccrebyset);

		docmodbyset   = "TRUE".equals(readValue("DOC_MOD_BY_SET"));
		writeProfileFlag("DOC_MOD_BY_SET", docmodbyset);

		docfilestateset   = "TRUE".equals(readValue("DOC_FILE_STATE_SET"));
		writeProfileFlag("DOC_FILE_STATE_SET", docfilestateset);

		docdtcreset   = "TRUE".equals(readValue("DOC_DT_CRE_SET"));
		writeProfileFlag("DOC_DT_CRE_SET", docdtcreset);

		docdtrelset   = "TRUE".equals(readValue("DOC_DT_REL_SET"));
		writeProfileFlag("DOC_DT_REL_SET", docdtrelset);

		doccontset   = "TRUE".equals(readValue("DOC_CONT_SET"));
		writeProfileFlag("DOC_CONT_SET", doccontset);
      
      //Bug Id 56231 Start.
      sortFieldVal = readValue("SORT_FIELD_VAL");
      writeProfileValue("SORT_FIELD_VAL",sortFieldVal); 
      //Bug Id 56231 End.
	}



	private String  loginUserMethod()
	{

		ASPManager           mgr   = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd=mgr.newASPCommand();

		trans.clear();
		cmd.defineCustomFunction(this,"FND_SESSION_API.Get_Fnd_User()","LOG_USER");
		trans.addCommand("NTSESSION", cmd);
		trans = mgr.perform(trans);
		String identity = trans.getValue("NTSESSION/DATA/LOG_USER");
		trans.clear();

		//mgr.showAlert("Fnd User" + identity);
		return  identity;
	}


	//Bug Id 47794, Modified isAccessibleMethod definition to send the user_id_ not the person_id
	private boolean isAccessibleMethod(String doc_class_,String doc_no_,String doc_sheet_,String doc_rev_,String user_id_)
	{

		ASPManager           mgr   = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd=mgr.newASPCommand();

		//Bug Id 47794, Start
		String person_id_ ;     

		trans.clear();
		cmd.defineCustomFunction(this,"PERSON_INFO_API.Get_Id_For_User","DUMMY01");
		cmd.addParameter(this,"DUMMY02",user_id_);
		trans.addCommand("DOCUMENTSEARCHPERSON", cmd);
		trans = mgr.perform(trans);
		person_id_=trans.getValue("DOCUMENTSEARCHPERSON/DATA/DUMMY01");

		trans.clear();
		cmd.defineCustomFunction(this,"DOCUMENT_ISSUE_ACCESS_API.Person_Get_Access","ACCESSIBILITY");
		cmd.addParameter(this,"DOC_CLASS", doc_class_);
		cmd.addParameter(this,"DOC_NO",doc_no_);
		cmd.addParameter(this,"DOC_SHEET",doc_sheet_);
		cmd.addParameter(this,"DOC_REV",doc_rev_);
		cmd.addParameter(this,"DUMMY01",person_id_);
		trans.addCommand("DOCUMENTSEACHACCESS", cmd);
		trans = mgr.perform(trans);
		String tempString=trans.getValue("DOCUMENTSEACHACCESS/DATA/ACCESSIBILITY");
		//Bug Id 47794, End
		if ("NONE".equals(tempString))
		{
			trans.clear();
			return false;
		}
		else
		{
			trans.clear();
			return true;
		}

	}


	private void getAccessVals()
	{
		ASPManager mgr   = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd=mgr.newASPCommand();
		trans.clear();
		String txt;
		txt = "select Doc_User_Access_API.Decode('G') DGROUP,";
		txt += "Doc_User_Access_API.Decode('A') DALL,";
		txt += "Doc_User_Access_API.Decode('U') DUSER from DUAL";

		trans.addQuery("GETCLIENTACCESS",txt);
		trans = mgr.perform(trans);

		trans.getBuffer("GETCLIENTACCESS/DATA");

		//String[] myString={ trans.getValue("GETCLIENTACCESS/DATA/DGROUP"), trans.getValue("GETCLIENTACCESS/DATA/DALL"),trans.getValue("GETCLIENTACCESS/DATA/DUSER")};
		sGroup = trans.getValue("GETCLIENTACCESS/DATA/DGROUP");
		sAll = trans.getValue("GETCLIENTACCESS/DATA/DALL");
		sUser = trans.getValue("GETCLIENTACCESS/DATA/DUSER");

		trans.clear();
	}



	public String getAccessControl(String doc_class_ ,String doc_no_,String doc_sheet_,String doc_rev_ )
	{

		ASPManager           mgr   = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd=mgr.newASPCommand();
		trans.clear();
		cmd.defineCustomFunction(this,"DOC_ISSUE_API.Get_Access_Control","ACCESSCONTROL");
		cmd.setParameter(this,"DOC_CLASS",doc_class_);
		cmd.setParameter(this,"DOC_NO",doc_no_);
		cmd.setParameter(this,"DOC_SHEET",doc_sheet_);
		cmd.setParameter(this,"DOC_REV",doc_rev_);
		trans.addCommand("DOCUMENTCCESSCONTROL", cmd);
		trans = mgr.perform(trans);
		return trans.getValue("DOCUMENTCCESSCONTROL/DATA/ACCESSCONTROL");
	}

   // Bug 57779, Start
   private boolean isUserDocmanAdministrator()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = mgr.newASPCommand();
      cmd.defineCustomFunction(this, "Docman_Security_Util_API.Check_Docman_Administrator", "DUMMY01");
      trans.addCommand("DOCMAN_ADMIN", cmd);


      trans = mgr.perform(trans);
      return "TRUE".equals(trans.getValue("DOCMAN_ADMIN/DATA/DUMMY01"));
   }
   // Bug 57779, End

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
	btxtSerExist = true;
      
      return btxtSerExist;
   }
   //Bug Id 64551, End
}
