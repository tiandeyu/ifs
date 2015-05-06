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
*  File        : DocTitleOvw.java
*  Modified    :
*    ShDilk   2001-02-22   ASP2JAVA Tool: Created Using the ASP file DocTitleOvw.asp
*    Bakalk   2001-04-19   Call Id:62833, Avoided whole column of "First Document Revision"
*                          being white (in adjust).
*    Shdilk   2001-04-27   Call Id:64604
*    Shdilk   2001-05-02   Call Id:62844
*    Bakalk   2001-09-10   Call Id:68669, Added a Lov for "User Created"
*    Bakalk   2002-11-11   Added doc_sheet to 2002 2 track.
*    Nisilk   2002-12-17   Call Id:92201, Changed the column order in DocTitleOverview
*    InoSlk   2002-12-27   Call ID:92247, Removed Action Create New Sheet
*    Nisilk   2002-12-30   Fixes were done according to the SP3 Merge.
*    InoSlk   2003-01-14   Call ID:92640, Changed Parameter Name to DOC_SHEET in call to Get_Default Value
*    Bakalk   2003-02-20   Added new fields for "Document Number Generator",
*                          saveReturn and saveNew over ridden.
*    Bakalk   2003-03-03   Modified saveNewRecords() and added some new fields:ORIG_DOC_SHEET,REV_NO_APP and REV_NO_MAX.
*    Bakalk   2003-04-02   Modified validate() now NUMBER_COUNTER ID1 getting values only if NUMBER_GENERATOR is "ADVANCED" .
*    Bakalk   2003-04-02   Modified saveNewRecord() Modified the error msg. saveNewRecord
*    Bakalk   2003-04-09   Modified copyTitle(). now we sends the row set along with request to CopyTitleWizard.
*    DhPelk   2003-05-20   Fixed call 95271. Made ID1 editable and connected it to a Lov
*    DhPelk   2003-05-22   Fixed call 95632. Removed uppercase from Title Revision Note
*    Dikalk   2003-07-28   Rechecked the clone() and doReset() methods. Removed unnecessary member variables.
*    Dikalk   2003-07-28   Optimized the validate() method by several magnitudes
*    InoSlk   2003-07-31   Call ID 96350: Re arranged the field layout in preDefine().
*    InoSlk   2003-07-31   Made 'Based on Sheet' not visible in MultiRow Layout.
*    Bakalk   2003-08-21   Added lov (or made them work in find layout) due to calls: 101234 and 101378.
*    NiSilk   2003-09-15   Fixed Call ID:103204 - Made REV_NO_APP and REV_NO_MAX fields read only.
*    ThWilk   2003-09-19   Fixed Call ID:103649 - Booking_list field was set to uppercase and  LOV
*                          window size were removed.Modified Function lovDocNo.
*    INOSLK   2003-09-25   Call ID 104142 - Set ID1/ID2 Uppercase.
*    NISILK   2003-10-02   Call ID 104784 - Modified method validate.
*    NISILK   2003-10-08   Call ID 106569. Modified method saveNewRecord.
*    INOSLK   2003-10-08   Call ID 106322. Modified method preDefine() and validate().
*    INOSLK   2003-10-16   Call ID 106322. Reflected LU name change from Doc_Def_Values_API to Doc_Number_Generator_Type_API.
*    INOSLK   2003-10-28   Modified copyTitle() as a workaround for Web Client error in Copy Title Wiz.
*    NISILK   2003-11-05   Fixed Call ID 110043. Changed the error message in copyTitle.
*    NISILK   2003-11-10   Call 110043. Added a warning message before proceeding to copy title wizard
*                          Added method goToCopyTitle() and modified methods, copyTitle().
*    BAKALK   2003-12-03   Web Alignment done.
*    BAKALK   2003-12-23   small modifcation done for disbling action button in introductory page.(web alignment multirow action)
*    BAKALK   2003-12-24   Set fields in same order as window client.
*    BAKALK   2004-01-05   small fix in field order.
*    DIKALK   2004-01-05   Merged Bud Id 44650.
*    SUKMLK   2004-09-15   Added checkbox for structure property.
*    SUKMLK   2004-09-30   Modified to take a parameters from a calling page which and create a new docs
*                          and return docs to the calling page.
*    DIKALK   2004-10-01   Merged Bud Id 44179
*    DIKALK   2004-10-18   Merged Bud Id 45870
*    DIKALK   2004-10-21   Merged Bud Id 46341
*    SUKMLK   2004-11-02   Fixed unnessasary LOV bug. B119128
*    SUKMLK   2004-11-02   Fixed null action being passed for data transfer B119304
*                          Fixed "file not found" bug when data transfering from copy title wizard
*    SUKMLK   2004-12-12   B119952 Fixed the problem with default values for structure not populating
*    SUKMLK   2005-06-17   Fixed Bug Id B125004.
*    SUKMLK   2005-06-20   Fixed Bug Id B125005.
*    BAKALK   2005-06-28   Fixed Bug Id B125005.
*    BAKALK   2005-06-29   Fixed Bug Id B125005,fixed some more problems.
*    BAKALK   2006-07-25   Bug ID 58216, Fixed Sql Injection.
*    KARALK   2006-10-12   Big id 59182, new RMB launch report archive settings dialog box.
*    NIJALK   2006-10-13   Bug 61028, Increased the length of field FIRST_SHEET_NO.
*    CHODLK   2007-02-13   Modified method saveNewRecord() and preDefine() to add the language ,format fields.
*    CHSELK   2007-04-05   Merged bug 63867.
*             THWILK   2007-03-02   Bug 63867, Removed the setMandatory() property of "FIRST_REVISION".
*    BAKALK   2007-05-10   Call ID 142524, Modified saveNewRecord().
*    BAKALK   2007-05-16   Call ID 143999, Modified saveNewRecord().
*    BAKALK   2007-05-24   Call ID 144112, Modified many places where bSaveForDocReference takes place. LANGUAGE_CODE and FORMAT_SIZE hidden conditionaly.
*    ASSALK   2007-08-08   Merged Bug 65075, Set TITLE_REV_NOTE to be visible in FIND_LAYOUT
*    ASSALK   2007-08-09   Merged Bug Id 65645, Added two new methods setStructAttribAll() and unsetStructAttribAll().
*    BAKALK   2007-08-10   Call ID 147216, Modified saveNewRecord().
*    BAKALK   2007-08-13   Call ID 144112, Modified validate() and predefine().
*    NaLrlk   2007-08-13   XSS Correction.
*    NIJALK   2008-03-17   Bug 71661, Modified preDefine().
*    VIRALK   2008-03-18   Bug Id 61529, Increaced the height of the reportarchivesettings window
*    SHTHLK   2008-04-10   Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*    SHTHLK   2008-06-30   Bug Id 73808, Modified duplicateRow(), validate(), printContents() and preDefine() to enable duplicate work as suppose to.
*    SHTHLK   2008-07-03   Bug Id 71147, Modified saveNewRecord().
*    SHTHLK   2008-09-29   Bug Id 74966, Added javascript method to handle validation of Doc_Class, to disable lovs
*    AMCHLK   2008-12-02   Bug Id 78595, Removed the two data fields "REV_NO_APP" and "REV_NO_MAX"
*    RUMELK   2009-06-09   Bug Id 82596, Added variable bFromProjectInfo and modified code to handle requests coming form Project Documents tab.
*    VIRALK   2010-04-02   Bug Id 88317, Modified copyTitle() Restrict creation of new files for * users. 
* -----------------------APP7.5 SP7---------------------------------------------------
*    ARWILK   2010-07-21   Bug Id 73606, Modified field BOOKING_LIST lov to filter from ID1 and ID2
* ------------------------------------------------------------------------------------
*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.edm.*;

public class DocTitleOvw extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocTitleOvw");

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   protected ASPContext       ctx;
   protected ASPBlock       headblk;
   protected ASPRowSet      headset;
   protected ASPCommandBar  headbar;
   protected ASPTable       headtbl;
   protected ASPBlockLayout headlay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================

   protected String searchURL;
   protected String doc_class;
   protected String doc_no;
   protected String doc_sheet;
   protected String doc_rev;
   protected String root_path;
   protected String err_msg;
   protected boolean bGoToCopyTitle;
   protected boolean bSaveForDocReference;
   protected String layout_mode;
   // Bug id 59182 start.
   protected String sUrl;   
   protected boolean bReportSettings = false; 
   // Bug id 59182 End

   // Bug Id 82596, start
   protected boolean bFromProjectInfo;
   // Bug Id 82596, end

   protected ASPTransactionBuffer trans;
   protected ASPCommand           cmd;
   protected ASPQuery             q;
   protected ASPBuffer            data;
   protected ASPBuffer            keys;
   protected ASPBuffer            transferBuffer;   

   //===============================================================
   // Construction
   //===============================================================
   public DocTitleOvw(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      ctx     = mgr.getASPContext();
      bSaveForDocReference = ctx.readFlag("BSAVEFORDOCREFERENCE", false);

      // Bug Id 82596, start
      bFromProjectInfo = ctx.readFlag("BFROMPROJECTINFO", false);
      // Bug Id 82596, end

      trans = mgr.newASPTransactionBuffer();
      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      //Context reads
        layout_mode = ctx.readValue("LAYOUT_MODE");


      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if (mgr.dataTransfered())
      {
          transferBuffer = mgr.getTransferedData();

          String transferAction = transferBuffer.getValue("ACTION");


          if (mgr.isEmpty(transferAction))
             transferAction = "EMPTY";

          if (transferAction.equalsIgnoreCase("NEW_DOCUMENT_FOR_CONNECTED_DOCUMENTS"))
          {
              String new_doc_lu_name = transferBuffer.getValue("LU_NAME");
              String new_doc_key_ref = transferBuffer.getValue("KEY_REF");

              bSaveForDocReference = true;

	      // Bug Id 82596, start
	      if("FROM_PROJECT_INFO".equals(transferBuffer.getValue("SUB_ACTION")))
	      {
		 bFromProjectInfo = true;

	      }
	      // Bug Id 82596, end

              ctx.writeValue("NEW_DOC_LU_NAME", new_doc_lu_name);
              ctx.writeValue("NEW_DOC_KEY_REF", new_doc_key_ref);

              newRow();
              headlay.setLayoutMode(headlay.NEW_LAYOUT);
              ASPField ref_doc_no = mgr.getASPField("DOC_NO");
              ref_doc_no.deactivateLOV();
          }
          else
          {
              bSaveForDocReference = false;

	      // Bug Id 82596, start
	      bFromProjectInfo = false;
	      // Bug Id 82596, end

              okFind();
          }
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if ("TRUE".equals(mgr.readValue("GO_TO_COPY_TITLE")))
      {
         goToCopyTitle();
      }else
          okFind();  
      adjust();
       
      //context writes
        ctx.writeValue("LAYOUT_MODE", layout_mode);
        ctx.writeFlag("BSAVEFORDOCREFERENCE", bSaveForDocReference);

        // Bug Id 82596, start
        ctx.writeFlag("BFROMPROJECTINFO", bFromProjectInfo);
	// Bug Id 82596, end

   }

   //=============================================================================
   //   VALIDATE FUNCTION
   //=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");

      if ("DOC_CLASS".equals(val))
      {
         // Doc Name
         trans.clear();
         cmd = trans.addCustomFunction("NAME", "DOC_CLASS_API.Get_Name", "SDOCNAME");
         cmd.addParameter("DOC_CLASS");

         // View copy required ?
         cmd = trans.addCustomFunction("VIEWFILEREQ", "Doc_Class_Default_API.Get_Default_Value_", "VIEW_FILE_REQ");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","VIEW_FILE_REQ");

         // Object connection required ?
         cmd = trans.addCustomFunction("OBJCONNREQ", "Doc_Class_Default_API.Get_Default_Value_", "OBJ_CONN_REQ");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","OBJ_CONN_REQ");

         // Destroy ?
         cmd = trans.addCustomFunction("MAKEWASTE","Doc_Class_Default_API.Get_Default_Value_","MAKE_WASTE_REQ");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","MAKE_WASTE_REQ");

         // Safety copy ?
         cmd = trans.addCustomFunction("SAFTYCOPY","Doc_Class_Default_API.Get_Default_Value_","SAFETY_COPY_REQ");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","SAFETY_COPY_REQ");

         // Structure ?
         cmd = trans.addCustomFunction("STRUCTURE","Doc_Class_Default_API.Get_Default_Value_","STRUCTURE");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","STRUCTURE");

         // First revision
         cmd = trans.addCustomFunction("FIRSTREV","Doc_Class_Default_API.Get_Default_Value_","FIRST_REVISION");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","DOC_REV");

         // First sheet no
         cmd = trans.addCustomFunction("FIRSTSHEET","Doc_Class_Default_API.Get_Default_Value_","FIRST_SHEET_NO");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY3","DOC_SHEET");

         // Number generator
         cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY2","DocTitle");
         cmd.addParameter("DUMMY3","NUMBER_GENERATOR");

         // Number generator
         cmd = trans.addCustomFunction("LANGUAGECODE","Doc_Class_Default_API.Get_Default_Value_","LANGUAGE_CODE");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY2","DocLanguage");
         cmd.addParameter("DUMMY3","LANGUAGE_CODE");

         

         trans = mgr.validate(trans);

         String doc_name         = trans.getValue("NAME/DATA/SDOCNAME");
         String view_file_req    = trans.getValue("VIEWFILEREQ/DATA/VIEW_FILE_REQ");
         String obj_con_req      = trans.getValue("OBJCONNREQ/DATA/OBJ_CONN_REQ");
         String make_waste_req   = trans.getValue("MAKEWASTE/DATA/MAKE_WASTE_REQ");
         String safety_copy      = trans.getValue("SAFTYCOPY/DATA/SAFETY_COPY_REQ");
         String first_rev        = trans.getValue("FIRSTREV/DATA/FIRST_REVISION");
         String first_sheet      = trans.getValue("FIRSTSHEET/DATA/FIRST_SHEET_NO");
         String number_generator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
         String structure        = trans.getValue("STRUCTURE/DATA/STRUCTURE");
         String language = "";
         language        = trans.getValue("LANGUAGECODE/DATA/LANGUAGE_CODE");
         

         trans.clear();

         // Decode view copy
         cmd = trans.addCustomFunction("VIEWFILEREQDECODE", "Doc_View_Copy_Req_API.Decode", "VIEW_FILE_REQ");
         if (mgr.isEmpty(view_file_req))
            cmd.addParameter("DUMMY1","Y");
         else
            cmd.addParameter("DUMMY1", view_file_req);

         // Decode object connection
         cmd = trans.addCustomFunction("OBJCONNREQDECODE","Doc_Reference_Object_Req_API.Decode","OBJ_CONN_REQ");
         if (mgr.isEmpty(obj_con_req))
            cmd.addParameter("DUMMY1","Y");
         else
            cmd.addParameter("DUMMY1",obj_con_req);

         // Decode destroy
         cmd = trans.addCustomFunction("MAKEWASTEDECODE","Doc_Make_Waste_Req_API.Decode","MAKE_WASTE_REQ");
         if (mgr.isEmpty(make_waste_req))
            cmd.addParameter("DUMMY1","N");
         else
            cmd.addParameter("DUMMY1",make_waste_req);

         // Decode safety copy
         cmd = trans.addCustomFunction("SAFTYCOPYDECODE","Doc_Safety_Copy_Req_API.Decode","SAFETY_COPY_REQ");
         if (mgr.isEmpty(safety_copy))
            cmd.addParameter("DUMMY1","N");
         else
            cmd.addParameter("DUMMY1",safety_copy);

         cmd = trans.addCustomFunction("GETCLIENTVAL","Doc_Number_Generator_Type_API.Decode","NUM_GEN_TRANSLATED");
         cmd.addParameter("NUMBER_GENERATOR",number_generator);

         // Number generator advanced ?
         if ("ADVANCED".equals(number_generator))
         {
            cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
            cmd.addParameter("DOC_CLASS");
            cmd.addParameter("DUMMY2","DocTitle");
            cmd.addParameter("DUMMY3","NUMBER_COUNTER");
         }

         trans = mgr.validate(trans);
         String view_file_req_decode  = trans.getValue("VIEWFILEREQDECODE/DATA/VIEW_FILE_REQ");
         String obj_con_req_decode    = trans.getValue("OBJCONNREQDECODE/DATA/OBJ_CONN_REQ");
         String make_waste_req_decode = trans.getValue("MAKEWASTEDECODE/DATA/MAKE_WASTE_REQ");
         String safety_copy_decode    = trans.getValue("SAFTYCOPYDECODE/DATA/SAFETY_COPY_REQ");
         String num_gen_translated    = trans.getValue("GETCLIENTVAL/DATA/NUM_GEN_TRANSLATED");
         String id1                   = trans.getValue("ID1COM/DATA/ID1");

         trans.clear();
         cmd = trans.addCustomFunction("ID2COM","Doc_Number_Counter_API.Get_Default_Id2","ID2");
         cmd.addParameter("DUMMY2",id1);
         trans = mgr.validate(trans);
         String id2                   = trans.getValue("ID2COM/DATA/ID2");
         if ("0".equals(id2))
            id2 = "";

         // Build response string for validation
         // and send it to the client
         StringBuffer response = new StringBuffer(mgr.isEmpty(doc_name) ? "" : doc_name);
         response.append("^");
         response.append(view_file_req_decode);
         response.append("^");
         response.append(obj_con_req_decode);
         response.append("^");
         response.append(make_waste_req_decode);
         response.append("^");
         response.append(safety_copy_decode);
         response.append("^");
         response.append(mgr.isEmpty(first_rev) ? "A1" : first_rev);
         response.append("^");
         response.append(mgr.isEmpty(first_sheet) ? "1" : first_sheet);
         response.append("^");
         response.append(mgr.isEmpty(first_rev) ? "A1" : first_rev);
         response.append("^");
         response.append(mgr.isEmpty(number_generator) ? "" : number_generator);
         response.append("^");
         response.append(mgr.isEmpty(num_gen_translated) ? "" : num_gen_translated);
         response.append("^");
         response.append(mgr.isEmpty(id1) ? "" : id1 );
         response.append("^");
         response.append(mgr.isEmpty(id2) ? "" : id2 );
         response.append("^");
         response.append(structure);
         response.append("^");
         response.append(language);
         response.append("^");
         mgr.responseWrite(response.toString());
      }
      //Bug 73808, Start
      else if ("FIRST_REVISION".equals(val))
      {
          String first_rev = mgr.readValue("FIRST_REVISION");
          StringBuffer response = new StringBuffer(mgr.isEmpty(first_rev) ? "" : first_rev);
          response.append("^");

          mgr.responseWrite(response.toString());
      }
      //Bug 73808, End
      mgr.endResponse();
   }

   //=============================================================================
   //   CMDBAR FUNCTIONS
   //=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      searchURL = mgr.createSearchURL(headblk);

      ctx.writeValue("SEARCHURL", searchURL);

      trans.clear();
      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
      {
         //ASPBuffer buff = mgr.getTransferedData();
         q.addOrCondition(transferBuffer);
      }

      //q.addOrCondition( "DOC_CLASS='"+mgr.readValue("DOC_CLASS")+"' AND DOC_NO='"+ mgr.readValue("DOC_NO")+"'");

      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNODATA: No data found."));
         headset.clear();
      }

      layout_mode = String.valueOf(headlay.getHistoryMode());

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



   public void  duplicateRow()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      headlay.setLayoutMode(headlay.NEW_LAYOUT);

      doc_class              = headset.getRow().getValue("DOC_CLASS");
      String doc_class_desc  = headset.getRow().getValue("SDOCNAME");
      String title           = headset.getRow().getValue("TITLE");
      String obj_conn_req    = headset.getRow().getValue("OBJ_CONN_REQ");
      String view_file_req   = headset.getRow().getValue("VIEW_FILE_REQ");
      String make_waste_req  = headset.getRow().getFieldValue("MAKE_WASTE_REQ");
      String safety_copy_req = headset.getRow().getFieldValue("SAFETY_COPY_REQ");
      String structure       = headset.getRow().getFieldValue("STRUCTURE");
      String id1             = headset.getRow().getFieldValue("ID1");
      String id2             = headset.getRow().getFieldValue("ID2");
      //Bug 73808, Start
      String first_doc_rev   = headset.getRow().getFieldValue("FIRST_REVISION");
      String first_doc_sheet = headset.getRow().getFieldValue("FIRST_SHEET_NO");
      String booking_list    = headset.getRow().getFieldValue("BOOKING_LIST");
      String alt_doc_no      = headset.getRow().getFieldValue("ALTERNATE_DOCUMENT_NUMBER");
      String confidential    = headset.getRow().getFieldValue("CONFIDENTIAL");
      String iso_cfn         = headset.getRow().getFieldValue("ISO_CLASSIFICATION");
      String note            = headset.getRow().getFieldValue("INFO");
      String based_class     = headset.getRow().getFieldValue("ORIG_DOC_CLASS");
      String based_no        = headset.getRow().getFieldValue("ORIG_DOC_NO");
      String based_sheet     = headset.getRow().getFieldValue("ORIG_DOC_SHEET");
      String based_rev       = headset.getRow().getFieldValue("ORIG_DOC_REV");
      String repl_class      = headset.getRow().getFieldValue("REPL_BY_DOC_CLASS");
      String repl_no         = headset.getRow().getFieldValue("REPL_BY_DOC_NO");
      String title_rev       = headset.getRow().getFieldValue("TITLE_REV");
      String title_rev_note  = headset.getRow().getFieldValue("TITLE_REV_NOTE");
      //Bug 73808, End

      trans.clear();
      //Bug 73808, Start
      if (mgr.isEmpty(first_doc_rev))
      {
          cmd = trans.addCustomFunction("FIRSTREV","DOC_CLASS_DEFAULT_API.Get_Default_Value_","FIRST_REVISION");
          cmd.addParameter("DOC_CLASS",doc_class);
          cmd.addParameter("DUMMY1","DocTitle");
          cmd.addParameter("DUMMY2","DOC_REV");
      }
      
      if (mgr.isEmpty(first_doc_sheet))
      {
          cmd = trans.addCustomFunction("FIRSTSHEET","DOC_CLASS_DEFAULT_API.Get_Default_Value_","FIRST_SHEET_NO");
          cmd.addParameter("DOC_CLASS",doc_class);
          cmd.addParameter("DUMMY1","DocTitle");
          cmd.addParameter("DUMMY2","DOC_SHEET");
      }
      //Bug 73808, End

      cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DUMMY2","DocTitle");
      cmd.addParameter("DUMMY3","NUMBER_GENERATOR");

      cmd = trans.addCustomFunction("GETCLIENTVAL", "Doc_Number_Generator_Type_API.Decode", "NUM_GEN_TRANSLATED");
      cmd.addReference("NUMBER_GENERATOR", "NUMBERGENERATOR/DATA");

      cmd = trans.addEmptyCommand("HEAD","DOC_TITLE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);

      data = trans.getBuffer("HEAD/DATA");
      //Bug 73808, Start
      if (mgr.isEmpty(first_doc_rev))
          first_doc_rev = trans.getValue("FIRSTREV/DATA/FIRST_REVISION");

      if (mgr.isEmpty(first_doc_sheet))
          first_doc_sheet = trans.getValue("FIRSTSHEET/DATA/FIRST_SHEET_NO");
      //Bug 73808, End

      String translated_num_generator = trans.getValue("GETCLIENTVAL/DATA/NUM_GEN_TRANSLATED");
      String number_generator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");



      if ("ADVANCED".equals(number_generator))
         {
            trans.clear();
            cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
            cmd.addParameter("DOC_CLASS",doc_class);
            cmd.addParameter("DUMMY2","DocTitle");
            cmd.addParameter("DUMMY3","NUMBER_COUNTER");

            cmd = trans.addCustomFunction("ID2COM","Doc_Number_Counter_API.Get_Default_Id2","ID2");
            cmd.addReference("ID1","ID1COM/DATA");

	    //Bug 73808, Start
	    cmd = trans.addCustomFunction("ID2EXIST","Doc_Number_Counter_API.Check_Exist","DUMMY1");
	    cmd.addReference("ID1","ID1COM/DATA");
	    cmd.addParameter("ID2",id2);
	    //Bug 73808, End
	    
	    trans = mgr.perform(trans);
            
	    id1 = trans.getValue("ID1COM/DATA/ID1");
	    //Bug 73808, Start
	    if (!"TRUE".equals(trans.getValue("ID2EXIST/DATA/DUMMY1")))
              id2 = trans.getValue("ID2COM/DATA/ID2");
	    //Bug 73808, End

         }
	//Bug 73808, Start
	else
	{
	    id1 = "";
	    id2 = "";
	    booking_list = "";
	}
	//Bug 73808, End
      if ("".equals(first_doc_rev))
         first_doc_rev = "A1";
      
      //Bug 73808, Start
      if ("".equals(first_doc_sheet))
         first_doc_sheet = "1";
      //Bug 73808, End
      trans.clear();

      data.setFieldItem("DOC_CLASS",doc_class);
      data.setFieldItem("SDOCNAME",doc_class_desc);
      data.setFieldItem("FIRST_REVISION",first_doc_rev);
      data.setFieldItem("FIRST_SHEET_NO",first_doc_sheet); //Bug 73808
      data.setFieldItem("TITLE",title);
      data.setFieldItem("OBJ_CONN_REQ",obj_conn_req);
      data.setFieldItem("VIEW_FILE_REQ",view_file_req);
      data.setFieldItem("MAKE_WASTE_REQ",make_waste_req);
      data.setFieldItem("SAFETY_COPY_REQ",safety_copy_req);
      data.setFieldItem("DOC_NO","");
      data.setFieldItem("STRUCTURE", structure);
      data.setFieldItem("ID1", id1);
      data.setFieldItem("ID2", id2);
      data.setFieldItem("NUM_GEN_TRANSLATED", translated_num_generator);
      data.setFieldItem("NUMBER_GENERATOR", number_generator);
      //Bug 73808, Start
      data.setFieldItem("BOOKING_LIST",booking_list);
      data.setFieldItem("ALTERNATE_DOCUMENT_NUMBER",alt_doc_no);
      data.setFieldItem("CONFIDENTIAL",confidential);
      data.setFieldItem("ISO_CLASSIFICATION",iso_cfn);
      data.setFieldItem("INFO",note);
      data.setFieldItem("ORIG_DOC_CLASS",based_class);
      data.setFieldItem("ORIG_DOC_NO",based_no);
      data.setFieldItem("ORIG_DOC_SHEET",based_sheet);
      data.setFieldItem("ORIG_DOC_REV",based_rev);
      data.setFieldItem("REPL_BY_DOC_CLASS",repl_class);
      data.setFieldItem("REPL_BY_DOC_NO",repl_no);
      data.setFieldItem("TITLE_REV",title_rev);
      data.setFieldItem("TITLE_REV_NOTE",title_rev_note);
      //Bug 73808, End

      headset.addRow(data);

      if (!headlay.isMultirowLayout())
         headset.unselectRows();
   }

   //=============================================================================
   //   CMDBAR CUSTOM FUNCTIONS
   //=============================================================================

   public void cancelNew()
   {
       debug ("-------------------[DocTitleOvw] cancelNew called");
       ASPManager mgr = getASPManager();


       if (bSaveForDocReference) // If creating a new document for document folders pass the keys of the new doc(s) back to DocReference.page
       {
           transferNewDocsToDocReference();
       }
       else
       {
         // Clear any unsaved rows that were added to the buffer.
         try // Bugfix call 118729
         {
             headset.clearRow();
             int mode = Integer.parseInt(layout_mode);
             headlay.setLayoutMode(mode);
         }
         catch (Exception e)
         {
             //layout mode set to just after navigating to document titles overview and selecting new.
             headlay.setLayoutMode(headlay.FIND_LAYOUT);
             headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
         };

         /* ORIGINAL CODE
         int mode = Integer.parseInt(layout_mode);

         // Clear any unsaved rows that were added to the buffer..
         debug ("-------------------[DocTitleOvw] Clearing the current row...");
         headset.clearRow();

         headlay.setLayoutMode(mode);
         */
       }
       debug ("-------------------[DocTitleOvw] cancelNew ended");
   }

   public void  saveReturn()
   {
      saveNewRecord("__SAVERETURN");


      if (bSaveForDocReference) // If creating a new document for document folders pass the keys of the new doc(s) back to DocReference.page
      {
          transferNewDocsToDocReference();
      }
   }

   public void transferNewDocsToDocReference()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer transferBuffer = mgr.newASPBuffer();

      transferBuffer.clear();

      String new_doc_lu_name = ctx.readValue("NEW_DOC_LU_NAME");
      String new_doc_key_ref = ctx.readValue("NEW_DOC_KEY_REF");

      transferBuffer = headset.getRows("DOC_CLASS,DOC_NO,FIRST_SHEET_NO,FIRST_REVISION");
      transferBuffer.addItem("ACTION", "ADD DOCUMENTS TO FOLDER");
      transferBuffer.addItem("NEW_DOC_KEY_REF",new_doc_key_ref);
      transferBuffer.addItem("NEW_DOC_LU_NAME",new_doc_lu_name);

      // Bug Id 82596, start
      if(bFromProjectInfo)
      {
	 transferBuffer.addItem ("SUB_ACTION","FROM_PROJECT_INFO");
	 bFromProjectInfo = false;
      }
      else
      {
	 transferBuffer.addItem ("SUB_ACTION","NULL");
      }
      // Bug Id 82596, end

      bSaveForDocReference = false;

      mgr.transferDataTo("DocReference.page", transferBuffer);

   }

   public void saveNewRecord(String finalAction)
   {
      ASPManager mgr = getASPManager();
      String doNo = "";
      String sAttr="";
      String sLanguageCode = "";
      String sFormatSize = "";
      String sTitleRev = ""; //Bug Id 71147
      int currrow = headset.getCurrentRowNo();

      headset.changeRow();
      if ("New__".equals(headset.getRowStatus()))
      {
         if ("ADVANCED".equals(mgr.readValue("NUMBER_GENERATOR")))
         {
            if (mgr.isEmpty(mgr.readValue("BOOKING_LIST")) && mgr.isEmpty(mgr.readValue("ID1")))
            {
               mgr.showError(mgr.translate("NOTSELECTEDANDNODEFAULT: No Booking List is selected and no default Number Counter is configured for document class") + " " + mgr.readValue("DOC_CLASS"));
            }
            else
            {
                // Bug Id 78595 start - removed two attributes "REV_NO_APP" AND "REV_NO_MAX"

                sAttr =  "TITLE_REV_NOTE"            + String.valueOf((char)31) + mgr.readValue("TITLE_REV_NOTE")           + String.valueOf((char)30)
                        + "TITLE_REV"                 + String.valueOf((char)31) + mgr.readValue("TITLE_REV")                + String.valueOf((char)30)
                        + "ALTERNATE_DOCUMENT_NUMBER" + String.valueOf((char)31) + mgr.readValue("ALTERNATE_DOCUMENT_NUMBER")+ String.valueOf((char)30)
                        + "REPL_BY_DOC_NO"            + String.valueOf((char)31) + mgr.readValue("REPL_BY_DOC_NO")           + String.valueOf((char)30)
                        + "REPL_BY_DOC_CLASS"         + String.valueOf((char)31) + mgr.readValue("REPL_BY_DOC_CLASS")        + String.valueOf((char)30)
                        + "ORIG_DOC_REV"              + String.valueOf((char)31) + mgr.readValue("ORIG_DOC_REV")             + String.valueOf((char)30)
                        + "ORIG_DOC_NO"               + String.valueOf((char)31) + mgr.readValue("ORIG_DOC_NO")              + String.valueOf((char)30)
                        + "ORIG_DOC_CLASS"            + String.valueOf((char)31) + mgr.readValue("ORIG_DOC_CLASS")           + String.valueOf((char)30)
                        + "CONFIDENTIAL"              + String.valueOf((char)31) + mgr.readValue("CONFIDENTIAL")             + String.valueOf((char)30)
                        + "ISO_CLASSIFICATION"        + String.valueOf((char)31) + mgr.readValue("ISO_CLASSIFICATION")       + String.valueOf((char)30)
                        + "USER_CREATED"              + String.valueOf((char)31) + mgr.readValue("USER_CREATED")             + String.valueOf((char)30)
                        + "DT_CRE"                    + String.valueOf((char)31) + mgr.readValue("DT_CRE")                   + String.valueOf((char)30)
                        + "SAFETY_COPY_REQ"           + String.valueOf((char)31) + mgr.readValue("SAFETY_COPY_REQ")          + String.valueOf((char)30)
                        + "MAKE_WASTE_REQ"            + String.valueOf((char)31) + mgr.readValue("MAKE_WASTE_REQ")           + String.valueOf((char)30)
                        + "OBJ_CONN_REQ"              + String.valueOf((char)31) + mgr.readValue("OBJ_CONN_REQ")             + String.valueOf((char)30)
                        + "VIEW_FILE_REQ"             + String.valueOf((char)31) + mgr.readValue("VIEW_FILE_REQ")            + String.valueOf((char)30)
                        + "INFO"                      + String.valueOf((char)31) + mgr.readValue("INFO")                     + String.valueOf((char)30)
                        + "TITLE"                     + String.valueOf((char)31) + mgr.readValue("TITLE")                    + String.valueOf((char)30)
                        + "DOC_CLASS"                 + String.valueOf((char)31) + mgr.readValue("DOC_CLASS")                + String.valueOf((char)30)
                        + "ORIG_DOC_SHEET"            + String.valueOf((char)31) + mgr.readValue("ORIG_DOC_SHEET")           + String.valueOf((char)30)
                     /* + "LANGUAGE_CODE"                + String.valueOf((char)31) + mgr.readValue("LANGUAGE_CODE")               + String.valueOf((char)30)
                        + "FORMAT_SIZE"                + String.valueOf((char)31) + mgr.readValue("FORMAT_SIZE")               + String.valueOf((char)30)*/;
                   
                // Bug Id 78595 end

               ASPTransactionBuffer trans2 = mgr.newASPTransactionBuffer();
               trans2.clear();
               cmd = trans2.addCustomFunction("GENNUMBER","DOC_TITLE_API.Generate_Doc_Number","DUMMY1");
               cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));
               cmd.addParameter("BOOKING_LIST",mgr.readValue("BOOKING_LIST"));
               cmd.addParameter("ID1",mgr.readValue("ID1"));
               cmd.addParameter("ID2",mgr.readValue("ID2"));
               cmd.addParameter("ATTR",sAttr);
               trans2 = mgr.perform(trans2);
               doNo   = trans2.getValue("GENNUMBER/DATA/DUMMY1");
            }
         }else 
            doNo   = mgr.readValue("DOC_NO");

        data = headset.getRow();
        data.setFieldItem("DOC_NO",doNo);

        //removes two items from the rowset to avoid error raised from the DOC_TITLE_API.New_ method.
        sLanguageCode = mgr.isEmpty(data.getFieldValue("LANGUAGE_CODE"))?"":data.getFieldValue("LANGUAGE_CODE");
        sFormatSize = mgr.isEmpty(data.getFieldValue("FORMAT_SIZE"))?"":data.getFieldValue("FORMAT_SIZE");
       

        //Bug Id 71147, start
	//Title revision should be same as the First Revision
	if (mgr.isEmpty(mgr.readValue("TITLE_REV")))
	{
	   ASPTransactionBuffer trans3 = mgr.newASPTransactionBuffer();
	   trans3.clear();
	   cmd = trans3.addCustomFunction("FIRSTREV","Doc_Class_Default_API.Get_Default_Value_","FIRST_REVISION");
	   cmd.addParameter("DOC_CLASS");
	   cmd.addParameter("DUMMY1","DocTitle");
	   cmd.addParameter("DUMMY2","DOC_REV");

	   trans3 = mgr.perform(trans3);
	   sTitleRev   = trans3.getValue("FIRSTREV/DATA/FIRST_REVISION");
	   sTitleRev = (mgr.isEmpty(sTitleRev) ? "A1" : sTitleRev);
	   
	   data.setFieldItem("TITLE_REV",sTitleRev);
	}
	//Bug Id 71147, end
       
        headset.setRow(data);
        mgr.submit(trans);
        trans.clear();       

         // --modified the doc_issue instance to have the retained language code and the format - start --
         sAttr = "LANGUAGE_CODE" + String.valueOf((char)31) + sLanguageCode + String.valueOf((char)30)
               + "FORMAT_SIZE"   + String.valueOf((char)31) + sFormatSize + String.valueOf((char)30);

         headset.last();

         cmd = trans.addCustomCommand("GETOBJIDVERSION","DOC_ISSUE_API.Get_Id_Version_By_Keys");
         cmd.addInParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
         cmd.addInParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
         cmd.addInParameter("FIRST_SHEET_NO", headset.getRow().getValue("FIRST_SHEET_NO"));
         cmd.addInParameter("FIRST_REVISION", headset.getRow().getValue("FIRST_REVISION"));
         cmd.addParameter("DUMMY1");
         cmd.addParameter("DUMMY2");
         cmd = trans.addCustomCommand("UPDATEDOCISSUE","DOC_ISSUE_API.Modify__");
         cmd.addParameter("INFO");
         cmd.addInReference("OBJID","GETOBJIDVERSION/DATA","DUMMY1");
         cmd.addInReference("OBJVERSION","GETOBJIDVERSION/DATA","DUMMY2");
         cmd.addParameter("ATTR",sAttr);
         cmd.addParameter("DUMMY1","DO");
         mgr.perform(trans);
         trans.clear();
         // --modified the doc_issue instance to have the retained language code and the format - end --

         headset.goTo(currrow);

         if (!("__SAVERETURN".equals(finalAction)))
         {
            newRow();            
         }
      }
      else
      {
         currrow = headset.getCurrentRowNo();
         mgr.submit(trans);
         trans.clear(); 
         headset.goTo(currrow);
      }

   }


   public void newRow()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("HEAD","DOC_TITLE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
      //layout_mode = String.valueOf(headlay.getHistoryMode());
      //debug("----------------[Layout mode] " + layout_mode);
   }


   public void  saveNew()
   {
      String new_doc_lu_name = ctx.readValue("NEW_DOC_LU_NAME");
      String new_doc_key_ref = ctx.readValue("NEW_DOC_KEY_REF");

      saveNewRecord("saveNew");

      ctx.writeValue("NEW_DOC_LU_NAME", new_doc_lu_name);
      ctx.writeValue("NEW_DOC_KEY_REF", new_doc_key_ref);

   }



   public void  transferToDocInfo()
   {
      ASPManager mgr = getASPManager();

      headset.storeSelections();
      if (headlay.isSingleLayout())
         headset.selectRow();
      keys=headset.getSelectedRows("DOC_CLASS,DOC_NO");

      if (keys.countItems()>0)
      {
         mgr.transferDataTo("DocIssue.page",keys);
      }
      else
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNORECSEL: No records selected!"));
      }

   }


   public void  queryDocContents()
   {
      ASPManager mgr = getASPManager();
      mgr.redirectTo("SearchDocumentContents.page");
   }


   public void  getDocSheet()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      //bug 58216 starts
      ASPQuery query = trans.addQuery("DOCSHEET","DOC_ISSUE","DOC_SHEET","DOC_NO= ? AND DOC_CLASS= ?","DOC_SHEET");
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_CLASS",doc_class);
      //bug 58216 end
      trans   = mgr.perform(trans);
      ASPBuffer mytemp1 = trans.getBuffer("DOCSHEET");
      trans.clear();
      int i=0;
      while ("".equals(mytemp1.getBufferAt(i).getValueAt(0)))
      {
         i++;
      }
      doc_sheet =  mytemp1.getBufferAt(i).getValueAt(0);
      trans.clear();

      //bug 58216 starts
      ASPQuery q = trans.addQuery("DOCREV","DOC_ISSUE","DOC_REV","DOC_NO= ? AND DOC_CLASS= ?","DOC_REV");
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_CLASS",doc_class);
      //bug 58216 end

      trans=mgr.perform(trans);

      ASPBuffer mytemp2 = trans.getBuffer("DOCREV");

      trans.clear();

      i=0;

      while ("".equals(mytemp2.getBufferAt(i).getValueAt(0)))
      {
         i++;
      }
      doc_rev =  mytemp2.getBufferAt(i).getValueAt(0);

   }


   public void  copyTitle()
   {
      ASPManager mgr = getASPManager();
      err_msg = "";
      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      headset.refreshRow();

      doc_no     = headset.getValue("DOC_NO");
      doc_class  = headset.getValue("DOC_CLASS");

      ctx.writeValue("DOC_CLASS",doc_class);
      ctx.writeValue("DOC_NO",doc_no);

      // Bug id 88317 start

      trans.clear();

      cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User", "DUMMY2");
      cmd = trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User", "DUMMY1");
      cmd.addReference("DUMMY2", "FNDUSER/DATA");
            
      trans = mgr.perform(trans);
      String person_id = trans.getValue("STARUSER/DATA/DUMMY1");


      if ("*".equals(person_id)){
         mgr.showAlert(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
         return;
      }
      // Bug id 88317 end

      // Added as a workaround for web client error, Call ID 109255
      trans.clear();

      cmd = trans.addCustomFunction("COPYTITLEALLOWED", "Doc_Title_API.Check_Copy_Title_Allowed_", "DUMMY1");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);

      trans = mgr.perform(trans);
      String copy_title_allowed = trans.getValue("COPYTITLEALLOWED/DATA/DUMMY1");

      if ("FALSE".equals(copy_title_allowed))
         mgr.showAlert(mgr.translate("DOCMAWCOPYTITLEOVWALLISSUESOBSOLETE: Cannot copy from this title because all document revisions are in state Obsolete."));
      else
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETDOCSTATE","DOC_ISSUE_API.Is_Allowed_State","DUMMY1");
         cmd.addParameter("DOC_CLASS",doc_class);
         cmd.addParameter("DOC_NO",doc_no);

         trans = mgr.perform(trans);
         String isAllowedState = trans.getValue("GETDOCSTATE/DATA/DUMMY1");

         if ("FALSE".equals(isAllowedState))
         {
            err_msg = mgr.translate("DOCMAWCOPYTITLEWIZARDNOTALLOWEDSTATE: Document revisions which are in state Obsolete will not be copied to the new title.");
            bGoToCopyTitle = true;
         }
         else
         {
            goToCopyTitle();
         }
      }
   }

   public void goToCopyTitle()
   {
      ASPManager mgr =  getASPManager();
      String url = mgr.getURL();

      doc_class = ctx.readValue("DOC_CLASS");
      doc_no = ctx.readValue("DOC_NO");

      headset.setFilterOff();
      headset.unselectRows();

      keys  = headset.getRows("DOC_CLASS,DOC_NO");
      mgr.transferDataTo("CopyTitleWizard.page?DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&SEND_URL="+mgr.URLEncode(url),keys);
   }

   // Bug id 59182 start.
   public void changereportArchiveSettings()
   {
      ASPManager mgr = getASPManager();
      String url = mgr.getURL();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      doc_class = headset.getRow().getValue("DOC_CLASS");
      doc_no = headset.getRow().getValue("DOC_NO");
      
      keys  = headset.getRows("DOC_CLASS,DOC_NO");
      sUrl = DocumentTransferHandler.getDataTransferUrl(mgr, "ReportArchiveSettings.page?DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&SEND_URL="+mgr.URLEncode(url),keys);
      bReportSettings = true;
      
   }
   // Bug id 59182 End.

   public void setStructAttribAll()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
	  headset.storeSelections();
      else
	  headset.selectRow();

      headset.setFilterOn();
      trans.clear();
      for (int k = 0;k < headset.countSelectedRows();k++)
      {
	 ASPCommand cmd = trans.addCustomCommand ("SETSTRUCTURE"+k, "DOC_TITLE_API.Set_Structure_All_");
	 cmd.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
	 cmd.addParameter("DOC_NO",headset.getValue("DOC_NO"));
	 headset.next();
      }
      trans = mgr.perform(trans);
      headset.setFilterOff();
      headset.unselectRows();

      headset.refreshAllRows();

   }

   public void unsetStructAttribAll()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
	  headset.storeSelections();
      else
	 headset.selectRow();

      headset.setFilterOn();
      trans.clear();
      for (int k = 0;k < headset.countSelectedRows();k++)
      {
	 ASPCommand cmd = trans.addCustomCommand ("SETSTRUCTURE"+k, "DOC_TITLE_API.Unset_Structure_All_");
	 cmd.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
	 cmd.addParameter("DOC_NO",headset.getValue("DOC_NO"));
	 headset.next();
      }
      trans = mgr.perform(trans);
      headset.setFilterOff();
      headset.unselectRows();

      headset.refreshAllRows();

   }

   public void editTitle()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
          headset.goTo(headset.getRowSelected());

      if (headset.getValue("ISSUEEDITACCESS").equals("N"))
         mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNOEDIT: You do not have sufficient priviledges to edit this title."));
      else if (headset.getValue("ISSUEEDITACCESS").equals("R"))
         mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWRELISSUESEXIST: Cannot edit this title because there are Released or Approved issues connected to it."));
      else
         headlay.setLayoutMode(headlay.EDIT_LAYOUT);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      headblk = mgr.newASPBlock("HEAD");

      // MDAHSE, 2001-01-18
      headblk.disableDocMan();

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setMandatory().
      setReadOnly().
      setInsertable().
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWDOCCLASS1: List of Document Class")).
      setCustomValidation("DOC_CLASS","SDOCNAME,VIEW_FILE_REQ,OBJ_CONN_REQ,MAKE_WASTE_REQ,SAFETY_COPY_REQ,FIRST_REVISION,FIRST_SHEET_NO,TITLE_REV,NUMBER_GENERATOR,NUM_GEN_TRANSLATED,ID1,ID2,STRUCTURE,LANGUAGE_CODE").
      setLabel("DOCMAWDOCTITLEOVWDOCCLASS: Doc Class");

      headblk.addField("SDOCNAME").
      setSize(20).
      setReadOnly().
      setFunction("DOC_CLASS_API.GET_NAME(:DOC_CLASS)").
      setLabel("DOCMAWDOCTITLEOVWSDOCNAME: Doc Class Desc");

      headblk.addField("DOC_NO").
      setSize(20).
      setMaxLength(120).
      setReadOnly().
      setInsertable().
      setUpperCase().
      setDynamicLOV("DOC_TITLE").
      setSecureHyperlink("DocIssue.page", "DOC_CLASS,DOC_NO").
      setLabel("DOCMAWDOCTITLEOVWDOCNO: Doc No");

      headblk.addField("TITLE").
      setSize(20).
      setMaxLength(250).
      setMandatory().
      setLabel("DOCMAWDOCTITLEOVWDOCTITLE: Title");

      //Bug 71661, Start, Unset Queryable
      headblk.addField("FIRST_SHEET_NO").
      setSize(20).
      //Bug 61028, Start, increased the length
      setMaxLength(10).
      //Bug 61028, End
      setReadOnly().
      setInsertable().
      setUpperCase().
      unsetQueryable().
      setLabel("DOCMAWDOCTITLEOVWFIRSTSHEET: First Sheet No");

      headblk.addField("FIRST_REVISION").
      setSize(20).
      setMaxLength(6).
      setReadOnly().
      setInsertable().
      setUpperCase().
      unsetQueryable().
      setCustomValidation("FIRST_REVISION","TITLE_REV"). //Bug 73808, Start, Added custom validation
      setLabel("DOCMAWDOCTITLEOVWFIRSTREV: First Document Revision");
      //Bug 71661, End

      headblk.addField("DOC_CODE").
      setSize(20).
      setInsertable().
      setLabel("DOCMAWDOCTITLEOVWDOCCODE: Doc Code");
      
      headblk.addField("VIEW_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      unsetInsertable().
      setLabel("DOCMAWDOCTITLEOVWVIEWFILE: View File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "DOC_CLASS,DOC_NO,FIRST_SHEET_NO,FIRST_REVISION", "NEWWIN").
      setAsImageField();
      
      headblk.addField("CHECK_IN_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      unsetInsertable().
      setLabel("DOCMAWDOCTITLEOVWCHECKINFILE: Check In File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=ORIGINAL", "DOC_CLASS,DOC_NO,FIRST_SHEET_NO,FIRST_REVISION", "NEWWIN").
      setAsImageField();
      
      headblk.addField("ALTERNATE_DOCUMENT_NUMBER").
      setSize(20).
      setMaxLength(120).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCTITLEOVWALTEDOCNU: Alternate Doc No");

      headblk.addField("NUMBER_GENERATOR").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setHidden().
      setFunction("''");

      headblk.addField("NUM_GEN_TRANSLATED").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setFunction("''").
      setLabel("DOCMAWDOCTITLEOVWNUMBERGENERATOR: Number Generator");

      headblk.addField("ID1").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLOV("Id1Lov.page").
      setLabel("DOCMAWDOCTITLEOVWNUMBERCOUNTERID1: Number Counter ID1");

      headblk.addField("ID2").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLOV("Id2Lov.page","ID1").
      setLabel("DOCMAWDOCTITLEOVWNUMBERCOUNTERID2: Number Counter ID2");

      headblk.addField("LANGUAGE_CODE"). 
      setFunction("''").
      setSize(20).
      setMaxLength(20).
      setDynamicLOV("APPLICATION_LANGUAGE").
      setLabel("DOCMAWDOCTITOWVANGUAGECODE: Language Code");

      headblk.addField("FORMAT_SIZE").
      setFunction("''").
      setSize(20).
      setMaxLength(20).
      setUpperCase().
      setDynamicLOV("DOC_CLASS_FORMAT_LOV","DOC_CLASS").
      setLabel("DOCMAWDOCTITOWVFORMATSIZE: Format");

      headblk.addField("BOOKING_LIST").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLOV("BookListLov.page", "ID1,ID2").//Bug Id 73606
      setLabel("DOCMAWDOCTITLEOVWBOOKINGLIST: Booking List");

      headblk.addField("INFO").
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCTITLEOVWINFO: Note");

      headblk.addField("TITLE_REV").
      setSize(20).
      setMaxLength(6).
      setInsertable().
      setUpperCase().
      setLabel("DOCMAWDOCTITLEOVWTITLEREVISION: Title Revision");

      headblk.addField("TITLE_REV_NOTE").
      setSize(20).
      setMaxLength(2000).
      setInsertable().
      setLabel("DOCMAWDOCTITLEOVWTITLEREVISIONNOTE: Title Revision Note");

      headblk.addField("USER_CREATED").
      setSize(20).
      setMaxLength(30).
      setReadOnly().
      setUpperCase().
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWLISTOFPERSONID: List of Person Id")).
      setLabel("DOCMAWDOCTITLEOVWUSERCREATED: Created By");

      headblk.addField("DT_CRE","Date").
      setSize(20).
      setReadOnly().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCTITLEOVWDTCRE: Date Created");

      headblk.addField("ORIG_DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWBASECLASS1: List of Based on Document Class")).
      setLabel("DOCMAWDOCTITLEOVWORIGDOCCLASS: Based on Doc Class");

      headblk.addField("ORIG_DOC_NO").
      setSize(20).
      setMaxLength(120).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOC_TITLE").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWBASENO1: List of Based on Document No")).
      setLabel("DOCMAWDOCTITLEOVWORIGDOCNO: Based on Doc No");

      headblk.addField("ORIG_DOC_SHEET").
      setSize(20).
      setDefaultNotVisible().
      setDynamicLOV("DOC_ISSUE_LOV1").
      setLabel("DOCMAWDOCTITLEOVWBASESHEET: Based on Sheet");

      headblk.addField("ORIG_DOC_REV").
      setSize(20).
      setMaxLength(6).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOC_ISSUE").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWBASEREV: List of Based on Document Revision")).
      setLabel("DOCMAWDOCTITLEOVWORIGDOCREV: Based on Doc Revision");

      headblk.addField("REPL_BY_DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setDefaultNotVisible().
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWREPCLASS: List of Replaced by Document Class")).
      setLabel("DOCMAWDOCTITLEOVWREPLBYDOCCLASS: Replaced by Doc Class");

      headblk.addField("REPL_BY_DOC_NO").
      setSize(20).
      setMaxLength(120).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOC_TITLE","REPL_BY_DOC_CLASS DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWREPNO: List of Replaced by Document No")).
      setLabel("DOCMAWDOCTITLEOVWREPLBYDOCNO: Replaced by Doc No");

      headblk.addField("OBJ_CONN_REQ").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_Reference_Object_Req_API").
      setLabel("DOCMAWDOCTITLEOVWOBJCONNREQ: Object Connection");

      headblk.addField("VIEW_FILE_REQ").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_View_Copy_Req_API").
      setLabel("DOCMAWDOCTITLEOVWVIEWFILEREQ: View Copy");

      headblk.addField("STRUCTURE").
      setCheckBox("0,1").
      setLabel("DOCTITLESTRUCTURE: Structure");
      // Bug 118728 the structure checkbox didn't get saved bacause of the use of .setFunction()

      headblk.addField("MAKE_WASTE_REQ").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_Make_Waste_Req_API").
      setLabel("DOCMAWDOCTITLEOVWMAKEWASTEREQ: Destroy");

      headblk.addField("SAFETY_COPY_REQ").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_Safety_Copy_Req_API").
      setLabel("DOCMAWDOCTITLEOVWSAFETYCOPYREQ: Safety Copy");

      headblk.addField("CONFIDENTIAL").
      setSize(20).
      setMaxLength(15).
      setUpperCase().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCTITLEOVWCONFIDENTIAL: Confidential");

      headblk.addField("ISO_CLASSIFICATION").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCTITLEOVWISOCLASSIFICATION: ISO Classification");

      headblk.addField("IS_ELE_DOC").
      setCheckBox("FALSE,TRUE").
      setFunction("EDM_FILE_API.Have_Edm_File(:DOC_CLASS,:DOC_NO,:FIRST_SHEET_NO,:FIRST_REVISION)").
      setReadOnly().
      setHidden().
      setLabel("DOCMAWDOCTITLEOVWISELEDOC: Is Ele Doc").
      setSize(5);
      
      headblk.addField("DUMMY1").
      setHidden().
      setFunction("''");

      headblk.addField("DUMMY2").
      setHidden().
      setFunction("''");

      headblk.addField("DUMMY3").
      setHidden().
      setFunction("''");

      headblk.addField("ATTR").
      setHidden().
      setFunction("''");

      headblk.addField("ISSUEEDITACCESS").
      setHidden().
      setFunction("Doc_Title_API.Check_Title_Editable_(:DOC_CLASS,:DOC_NO)");

      headblk.setView("DOC_TITLE");
      headblk.defineCommand("DOC_TITLE_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.DELETE);
      headbar.defineCommand(headbar.NEWROW,"newRow");
      headbar.defineCommand(headbar.SAVERETURN,"saveReturn");
      headbar.defineCommand(headbar.SAVENEW,"saveNew");
      headbar.defineCommand(headbar.CANCELNEW,"cancelNew");
      headbar.defineCommand(headbar.DUPLICATEROW,"duplicateRow");
      headbar.defineCommand(headbar.EDITROW, "editTitle");
      headbar.addSecureCustomCommand("queryDocContents",mgr.translate("DOCMAWDOCTITLEOVWSDC: Search Document Contents..."),"TEXT_SEARCH_API.Search");  //Bug Id 70286
      headbar.addCustomCommand("transferToDocInfo",mgr.translate("DOCMAWDOCTITLEOVWDOCINFO: Document Info..."));
      headbar.addSecureCustomCommand("copyTitle",mgr.translate("CREATEDOCTITLE: Copy Document Title..."),"DOC_TITLE_API.Copy_Doc_Title");  //Bug Id 70286
      headbar.addSecureCustomCommand("changereportArchiveSettings",mgr.translate("REPORTARCHIVESETTINGS: Change report archive settings..."),"DOC_TITLE_API.update_title");//Bug id 59182.  //Bug Id 70286

      // Structure Attribute
      headbar.addSecureCustomCommand("setStructAttribAll",mgr.translate("DOCMAWDOCISSUESTRUCATTRSET: Set Structure Attribute"),"DOC_TITLE_API.Set_Structure_All_"); //Bug Id 70286
      headbar.addSecureCustomCommand("unsetStructAttribAll",mgr.translate("DOCMAWDOCISSUESTRUCATTRUNSET: Unset Structure Attribute"),"DOC_TITLE_API.Unset_Structure_All_"); //Bug Id 70286

      headbar.addCustomCommandGroup("SETSTRUCTURE",mgr.translate("DOCMAWDOCISSUEDOCUMENTSETSTRUCTURE: Structure"));
      headbar.setCustomCommandGroup("setStructAttribAll", "SETSTRUCTURE");
      headbar.setCustomCommandGroup("unsetStructAttribAll", "SETSTRUCTURE");
      headbar.addCustomCommandSeparator();

      headbar.enableMultirowAction();
      headbar.removeFromMultirowAction("copyTitle");
      headbar.removeFromMultirowAction("changereportArchiveSettings");//Bug id 59182 start.

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCTITLEOVWOVEDOCTI: Overview - Document Titles"));
      headtbl.enableRowSelect();
      
      headtbl.disableSelectionOption();
      
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      headlay.defineGroup("Main","OBJID,OBJVERSION,DOC_CLASS,SDOCNAME,DOC_NO,FIRST_SHEET_NO,FIRST_REVISION,TITLE,DOC_CODE,VIEW_FILE,CHECK_IN_FILE,NUM_GEN_TRANSLATED,BOOKING_LIST,ID1,ID2,LANGUAGE_CODE,FORMAT_SIZE",false,true);
      // Bug Id 78595 Start - removed the two fields "REV_NO_APP" AND "REV_NO_MAX"
      headlay.defineGroup(mgr.translate("DOCMAWDOCTITLEATTRIBUTES: Document Title Attributes"),"OBJ_CONN_REQ,VIEW_FILE_REQ,STRUCTURE,MAKE_WASTE_REQ,SAFETY_COPY_REQ,ALTERNATE_DOCUMENT_NUMBER,CONFIDENTIAL,ISO_CLASSIFICATION,INFO,ORIG_DOC_CLASS,ORIG_DOC_NO,ORIG_DOC_SHEET,ORIG_DOC_REV,REPL_BY_DOC_CLASS,REPL_BY_DOC_NO,DT_CRE,USER_CREATED,TITLE_REV,TITLE_REV_NOTE",true,false);
      // Bug Id 78595 End
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
            imgSrc += "document_upload.gif";
            return "<img src=\""+imgSrc+"\" height=\"16\" width=\"16\" border=\"0\">";
         }
      }
      return "";
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if ("HEAD.NewRow".equals(mgr.readValue("__COMMAND")))
         mgr.getASPField("DOC_NO").deactivateLOV();
      

      if (headlay.getLayoutMode()!= headlay.NEW_LAYOUT)
      {
         mgr.getASPField("NUM_GEN_TRANSLATED").setHidden();
         mgr.getASPField("ID1").setHidden();
         mgr.getASPField("BOOKING_LIST").setHidden();
         mgr.getASPField("ID2").setHidden();
      }

      if (!bSaveForDocReference) {
         mgr.getASPField("LANGUAGE_CODE").setHidden();
         mgr.getASPField("FORMAT_SIZE").setHidden();
      }

      if (headset.countRows()<= 0)
      {
         headbar.disableMultirowAction();
      }
      else
      {
         headbar.enableMultirowAction();
      }
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCTITLEOVWTITLE: Overview - Document Titles";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCTITLEOVWTITLE: Overview - Document Titles";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(headlay.show());
      appendToHTML("<input type=hidden name=\"GO_TO_COPY_TITLE\" value=\"\">\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("function validateFirstRevision(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript(" if( !checkFirstRevision(i) ) return;\n");
      //Bug 73808, Start
      appendDirtyJavaScript(" window.status='"+ mgr.translateJavaScript("DOCMAWDOCTITLEOVWNPLEASEWAITFORVAL: Please wait for validation")+"';");
      appendDirtyJavaScript(" r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=FIRST_REVISION'\n");
      appendDirtyJavaScript("		+ '&FIRST_REVISION=' + URLClientEncode(getValue_('FIRST_REVISION',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'TITLE_REV',i,'Title Rev') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('TITLE_REV',i,0);\n");
      appendDirtyJavaScript("	}\n");
      //Bug 73808, End
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovDocNo(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript(" try {var enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('DOC_NO',i).indexOf('%') !=-1)? getValue_('DOC_NO',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('DOC_NO',i,\n");
      appendDirtyJavaScript("     '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_TITLE&__FIELD=Doc+No&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("     + '&__KEY_VALUE=' + URLClientEncode(getValue_('DOC_NO',i))\n");
      appendDirtyJavaScript("     + '&DOC_NO=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("     ,550,500,'validateDocNo');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovOrigDocNo(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if(params) param = params;\n");
      appendDirtyJavaScript("else param = '';\n");
      appendDirtyJavaScript("var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("var key_value = (getValue_('ORIG_DOC_NO',i).indexOf('%') !=-1)? getValue_('ORIG_DOC_NO',i):'';\n");
      appendDirtyJavaScript("openLOVWindow('ORIG_DOC_NO',i,\n");
      appendDirtyJavaScript("'"+ root_path +"common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_TITLE&__FIELD=Based+on+Doc+No&__INIT=1'+param+'&__TITLE=List+of+Based+on+Document+No&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ORIG_DOC_NO',i))\n");
      appendDirtyJavaScript("+ '&ORIG_DOC_NO=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("+ '&DOC_CLASS=' + URLClientEncode(getValue_('ORIG_DOC_CLASS',i))\n");
      appendDirtyJavaScript(",550,500,'validateOrigDocNo');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovOrigDocSheet(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("   var key_value = (getValue_('ORIG_DOC_SHEET',i).indexOf('%') !=-1)? getValue_('ORIG_DOC_SHEET',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('ORIG_DOC_SHEET',i,\n");
      appendDirtyJavaScript("   '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_ISSUE_LOV1&__FIELD=Orig+Doc+Sheet&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('ORIG_DOC_SHEET',i))\n");
      appendDirtyJavaScript("   + '&ORIG_DOC_SHEET=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&DOC_CLASS=' + URLClientEncode(getValue_('ORIG_DOC_CLASS',i))\n");
      appendDirtyJavaScript("     + '&DOC_NO=' + URLClientEncode(getValue_('ORIG_DOC_NO',i))\n");
      appendDirtyJavaScript("   ,550,500,'validateOrigDocSheet');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovOrigDocRev(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("   var key_value = (getValue_('ORIG_DOC_REV',i).indexOf('%') !=-1)? getValue_('ORIG_DOC_REV',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('ORIG_DOC_REV',i,\n");
      appendDirtyJavaScript("   '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_ISSUE&__FIELD=Doc+Rev&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('ORIG_DOC_REV',i))\n");
      appendDirtyJavaScript("   + '&ORIG_DOC_REV=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&DOC_CLASS=' + URLClientEncode(getValue_('ORIG_DOC_CLASS',i))\n");
      appendDirtyJavaScript("     + '&DOC_NO=' + URLClientEncode(getValue_('ORIG_DOC_NO',i))\n");
      appendDirtyJavaScript("     + '&DOC_SHEET=' + URLClientEncode(getValue_('ORIG_DOC_SHEET',i))\n");
      appendDirtyJavaScript("   ,550,500,'validateOrigDocRev');\n");
      appendDirtyJavaScript("}\n");

      if (bGoToCopyTitle)
      {
         appendDirtyJavaScript("alert(\""+err_msg+"\");\n");
         appendDirtyJavaScript("document.forms[0].GO_TO_COPY_TITLE.value=\"TRUE\";\n");
         appendDirtyJavaScript("submit();\n");

      }
      // bug id 59182 start.
      if (bReportSettings)     
      {
          appendDirtyJavaScript("   window.open(\"");
          appendDirtyJavaScript(mgr.encodeStringForJavascript(sUrl));
          //Bug Id 61529 start
          appendDirtyJavaScript("\",\"anotherWindow\",\"status,  width=450, height=230, left=200, top=200\");\n");
          //Bug Id 61529 start

      }
      // bug id 59182 end.
      //Bug Id 74966, Start
      appendDirtyJavaScript("function validateDocClass(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if( getRowStatus_('HEAD',i)=='QueryMode__' ) \n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if(!checkDocClass(i) ) return;\n");
      appendDirtyJavaScript("    var r = __connect('" + mgr.getURL() + "?VALIDATE=DOC_CLASS'+'&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i)));\n");
      appendDirtyJavaScript("    window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'DOC_CLASS',i,'Doc Class') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('SDOCNAME',i,0);\n");
      appendDirtyJavaScript("		assignSelectBoxValue_('VIEW_FILE_REQ',i,1);\n");
      appendDirtyJavaScript("		assignSelectBoxValue_('OBJ_CONN_REQ',i,2);\n");
      appendDirtyJavaScript("		assignSelectBoxValue_('MAKE_WASTE_REQ',i,3);\n");
      appendDirtyJavaScript("		assignSelectBoxValue_('SAFETY_COPY_REQ',i,4);\n");
      appendDirtyJavaScript("		assignValue_('FIRST_REVISION',i,5);\n");
      appendDirtyJavaScript("		assignValue_('FIRST_SHEET_NO',i,6);\n");
      appendDirtyJavaScript("		assignValue_('TITLE_REV',i,7);\n");
      appendDirtyJavaScript("		assignValue_('NUMBER_GENERATOR',i,8);\n");
      appendDirtyJavaScript("		assignValue_('NUM_GEN_TRANSLATED',i,9);\n");
      appendDirtyJavaScript("		assignValue_('ID1',i,10);\n");
      appendDirtyJavaScript("		assignValue_('ID2',i,11);\n");
      appendDirtyJavaScript("		if (i>0) //Editable tables\n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("			assignCheckBoxValue_('_STRUCTURE',i-1,12,'1');\n");
      appendDirtyJavaScript("			setCheckBox_('STRUCTURE',getField_('_STRUCTURE',i-1).checked,i,'1');\n");
      appendDirtyJavaScript("		}\n");
      appendDirtyJavaScript("		else\n");
      appendDirtyJavaScript("			assignCheckBoxValue_('STRUCTURE',i,12,'1');\n");
      appendDirtyJavaScript("		assignValue_('LANGUAGE_CODE',i,13);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("    if (document.form.NUMBER_GENERATOR.value ==\"ADVANCED\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("      if (document.form.ID1.value ==\"\")\n");
      appendDirtyJavaScript("         document.form.ID1.readOnly=0;\n");
      appendDirtyJavaScript("      else  \n"); 
      appendDirtyJavaScript("         document.form.ID1.readOnly=1;\n");
      appendDirtyJavaScript("      document.form.ID2.readOnly=0;\n");
      appendDirtyJavaScript("      document.form.BOOKING_LIST.readOnly=0;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else  \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       document.form.ID1.readOnly=1;\n");
      appendDirtyJavaScript("       document.form.ID2.readOnly=1;\n");
      appendDirtyJavaScript("       document.form.BOOKING_LIST.readOnly=1;\n");
      appendDirtyJavaScript("       document.form.BOOKING_LIST.value=\"\";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");
      //disabled Id1 lov
      appendDirtyJavaScript("function preLovId1(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.ID1.readOnly == 0)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(params)\n"); 
      appendDirtyJavaScript("         PARAM = params;\n");
      appendDirtyJavaScript("      else\n"); 
      appendDirtyJavaScript("         PARAM = '';\n");
      appendDirtyJavaScript("      var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("      MULTICH=''+enable_multichoice;\n");
      appendDirtyJavaScript("      MASK ='';\n");	 
      appendDirtyJavaScript("      KEY_VALUE = (getValue_('ID1',i).indexOf('%') !=-1)? getValue_('ID1',i):'';\n");
      appendDirtyJavaScript("	   lovId1(i,params);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n"); 
      appendDirtyJavaScript("      return;\n"); 
      appendDirtyJavaScript("}\n");
      
      //disabled Id2 lov
      appendDirtyJavaScript("function preLovId2(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.ID2.readOnly == 0)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(params)\n"); 
      appendDirtyJavaScript("         PARAM = params;\n");
      appendDirtyJavaScript("      else\n"); 
      appendDirtyJavaScript("         PARAM = '';\n");
      appendDirtyJavaScript("      var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("      MULTICH=''+enable_multichoice;\n");
      appendDirtyJavaScript("      MASK ='';\n");	 
      appendDirtyJavaScript("      KEY_VALUE = (getValue_('ID2',i).indexOf('%') !=-1)? getValue_('ID2',i):'';\n");
      appendDirtyJavaScript("	   lovId2(i,params);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n"); 
      appendDirtyJavaScript("      return;\n"); 
      appendDirtyJavaScript("}\n");
      
      //disabled bookinglist lov
      appendDirtyJavaScript("function preLovBookingList(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.BOOKING_LIST.readOnly == 0)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(params)\n"); 
      appendDirtyJavaScript("         PARAM = params;\n");
      appendDirtyJavaScript("      else\n"); 
      appendDirtyJavaScript("         PARAM = '';\n");
      appendDirtyJavaScript("      var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("      MULTICH=''+enable_multichoice;\n");
      appendDirtyJavaScript("      MASK ='';\n");	 
      appendDirtyJavaScript("     KEY_VALUE = (getValue_('BOOKING_LIST',i).indexOf('%') !=-1)? getValue_('BOOKING_LIST',i):'';\n");
      appendDirtyJavaScript("	   lovBookingList(i,params);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n"); 
      appendDirtyJavaScript("      return;\n"); 
      appendDirtyJavaScript("}\n");
      //Bug Id 74966, End
   }

}
