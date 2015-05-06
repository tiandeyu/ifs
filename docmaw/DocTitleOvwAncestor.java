package ifs.docmaw;

import java.util.Date;

import ifs.docmaw.edm.DocumentTransferHandler;
import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPField;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.genbaw.GenbawConstants;

public abstract class DocTitleOvwAncestor extends ASPPageProvider{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocTitleOvwAncestor");

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

   private String searchURL;
   private String doc_class;
   private String doc_no;
   private String doc_sheet;
   private String doc_rev;
   private String root_path;
   private String err_msg;
   private boolean bGoToCopyTitle;
   private boolean bSaveForDocReference;
   private String layout_mode;
   // Bug id 59182 start.
   private String sUrl;   
   private boolean bReportSettings = false; 
   // Bug id 59182 End

   // Bug Id 82596, start
   private boolean bFromProjectInfo;
   // Bug Id 82596, end

   private ASPTransactionBuffer trans;
   private ASPCommand           cmd;
   private ASPQuery             q;
   private ASPBuffer            data;
   private ASPBuffer            keys;
   private ASPBuffer            transferBuffer;
   
   //new method intended to be implemented by subclasses .begin..
   protected abstract String getCurrentPageDocClass();
   
   protected abstract String getCorrespondingDocIssuePage();
   
   protected abstract void customizeHeadblk();
   
   protected abstract void customizeHeadlay();
   // new method end.
   //new method not  intended to be implemented by subclasses .begin..
   private void setDefaultValues(ASPBuffer data){
      ASPManager mgr = getASPManager();
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



      trans = mgr.validate(trans);
    
      String view_file_req_decode  = trans.getValue("VIEWFILEREQDECODE/DATA/VIEW_FILE_REQ");
      String obj_con_req_decode    = trans.getValue("OBJCONNREQDECODE/DATA/OBJ_CONN_REQ");
      String make_waste_req_decode = trans.getValue("MAKEWASTEDECODE/DATA/MAKE_WASTE_REQ");
      String safety_copy_decode    = trans.getValue("SAFTYCOPYDECODE/DATA/SAFETY_COPY_REQ");
      String num_gen_translated    = trans.getValue("GETCLIENTVAL/DATA/NUM_GEN_TRANSLATED");
      
      trans.clear();
      data.setFieldItem("VIEW_FILE_REQ", view_file_req_decode );
      data.setFieldItem("OBJ_CONN_REQ",  obj_con_req_decode );
      data.setFieldItem("MAKE_WASTE_REQ", make_waste_req_decode );
      data.setFieldItem("SAFETY_COPY_REQ", safety_copy_decode );
      data.setFieldItem("NUMBER_GENERATOR", num_gen_translated );
      data.setFieldItem("FIRST_REVISION", first_rev );
      data.setFieldItem("FIRST_SHEET_NO", first_sheet );
      data.setFieldItem("STRUCTURE", structure );
      data.setFieldItem("LANGUAGE_CODE", language );
   }
   
   // new method end.
   //===============================================================
   // Construction
   //===============================================================
   public DocTitleOvwAncestor(ASPManager mgr, String page_path)
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
      }

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
      else if ("PROJ_NO".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETPROJECTNAME","GENERAL_PROJECT_API.Get_Proj_Desc","PROJ_NAME");
         cmd.addParameter("PROJ_NO");
         
         trans = mgr.validate(trans);
         
         String projName = trans.getValue("GETPROJECTNAME/DATA/PROJ_NAME");
         String txt = (mgr.isEmpty(projName) ? "" : projName + "^") ;
         mgr.responseWrite(txt);
      }
      else if ("SEND_UNIT_NO".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETUNITNAME","GENERAL_ZONE_API.Get_Zone_Desc","SEND_UNIT_NAME");
         cmd.addParameter("SEND_UNIT_NO");
         
         trans = mgr.validate(trans);
         
         String projName = trans.getValue("GETUNITNAME/DATA/SEND_UNIT_NAME");
         String txt = (mgr.isEmpty(projName) ? "" : projName + "^") ;
         mgr.responseWrite(txt);
      }
      else if ("RECEIVE_UNIT_NO".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETUNITNAME","GENERAL_ZONE_API.Get_Zone_Desc","RECEIVE_UNIT_NAME");
         cmd.addParameter("RECEIVE_UNIT_NO");
         
         trans = mgr.validate(trans);
         
         String projName = trans.getValue("GETUNITNAME/DATA/RECEIVE_UNIT_NAME");
         String txt = (mgr.isEmpty(projName) ? "" : projName + "^") ;
         mgr.responseWrite(txt);
      }
      else if ("PURPOSE_NO".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETPURPOSEDESC","DOC_COMMUNICATION_SEQ_API.Get_Purpose_Name","PURPOSE_NAME");
         cmd.addParameter("PURPOSE_NO");
         
         trans = mgr.validate(trans);
         
         String purposeName = trans.getValue("GETPURPOSEDESC/DATA/PURPOSE_NAME");
         String txt = (mgr.isEmpty(purposeName) ? "" : purposeName + "^") ;
         mgr.responseWrite(txt);
      }
      else if ("SIGN_PERSON".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETPURPOSEDESC","PERSON_INFO_API.Get_Name_For_User","SIGN_PERSON_NAME");
         cmd.addParameter("SIGN_PERSON");
         
         trans = mgr.validate(trans);
         
         String personName = trans.getValue("GETPERSONDESC/DATA/SIGN_PERSON_NAME");
         String txt = (mgr.isEmpty(personName) ? "" : personName + "^") ;
         mgr.responseWrite(txt);
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
      q.setWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
      if (mgr.dataTransfered())
      {
         //ASPBuffer buff = mgr.getTransferedData();
         q.addOrCondition(transferBuffer);
      }

      //q.addOrCondition( "DOC_CLASS='"+mgr.readValue("DOC_CLASS")+"' AND DOC_NO='"+ mgr.readValue("DOC_NO")+"'");

      String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
      String tempPersonProjects = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_PROJECTS);
      StringBuffer sb = new StringBuffer("(");
      if(!"()".equals(tempPersonZones)){
         sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
      }else{
         sb.append("(1=2)");
      }
      sb.append(" OR ");
      if(!"()".equals(tempPersonProjects)){
         sb.append("(PROJECT_NO IN " + tempPersonProjects).append(")");;
      }else{
         sb.append("(1=2)");
      }
      sb.append(")");
      q.addWhereCondition(sb.toString());
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
      q.setWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
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
      String sTitleRev = ""; 
      int currrow = headset.getCurrentRowNo();

      headset.changeRow();
      
      
      String tempDefaultPrjNo = ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT);
      String tempDefaultZoneNo = ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE);
      
      if(mgr.isEmpty(tempDefaultPrjNo)){
         mgr.showAlert("DOCTITLEOVWANCESTORCANNOTFINDDEFAULTPROJECT: Can not find default Project.");
         headlay.setLayoutMode(headlay.NEW_LAYOUT);
         return ;
      }
      
      
      if(mgr.isEmpty(tempDefaultZoneNo)){
         mgr.showAlert("DOCTITLEOVWANCESTORCANNOTFINDDEFAULTZONE: Can not find default Zone.");
         headlay.setLayoutMode(headlay.NEW_LAYOUT);
         return ;
      }
      
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

                sAttr =  "TITLE_REV_NOTE"             + String.valueOf((char)31) + mgr.readValue("TITLE_REV_NOTE")            + String.valueOf((char)30)
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
                        + "DOC_CODE"                  + String.valueOf((char)31) + mgr.readValue("DOC_CODE", "")             + String.valueOf((char)30)
                        + "REV_TITLE"                 + String.valueOf((char)31) + mgr.readValue("REV_TITLE", "")            + String.valueOf((char)30)
                        + "MAIN_CONTENT"              + String.valueOf((char)31) + mgr.readValue("MAIN_CONTENT", "")         + String.valueOf((char)30)
                        + "RESPONSE_CODE"             + String.valueOf((char)31) + mgr.readValue("RESPONSE_CODE", "")        + String.valueOf((char)30)
                        + "INDEX_CREATED"             + String.valueOf((char)31) + mgr.readValue("INDEX_CREATED", "")        + String.valueOf((char)30)
                        + "COPIES"                    + String.valueOf((char)31) + mgr.readValue("COPIES", "")               + String.valueOf((char)30)
                        + "PAGES"                     + String.valueOf((char)31) + mgr.readValue("PAGES", "")                + String.valueOf((char)30)
                        + "EMERGENCY"                 + String.valueOf((char)31) + mgr.readValue("EMERGENCY", "")            + String.valueOf((char)30)
                        + "RECEIPT"                   + String.valueOf((char)31) + mgr.readValue("RECEIPT", "")              + String.valueOf((char)30)
                        + "RECEIPT_REQUEST"           + String.valueOf((char)31) + mgr.readValue("RECEIPT_REQUEST", "")      + String.valueOf((char)30)
                        + "COMPLETE_DATE"             + String.valueOf((char)31) + mgr.readValue("COMPLETE_DATE", "")        + String.valueOf((char)30)
                        + "INNER_DOC_CODE"            + String.valueOf((char)31) + mgr.readValue("INNER_DOC_CODE", "")       + String.valueOf((char)30)
                        + "AS_BUILT_DRAWING"          + String.valueOf((char)31) + mgr.readValue("AS_BUILT_DRAWING", "")     + String.valueOf((char)30)
                        + "TRANSFER_NO"               + String.valueOf((char)31) + mgr.readValue("TRANSFER_NO", "")          + String.valueOf((char)30)
                        + "DOC_STATE"                 + String.valueOf((char)31) + mgr.readValue("DOC_STATE", "")            + String.valueOf((char)30)
                        + "PROJ_NO"                   + String.valueOf((char)31) + mgr.readValue("PROJ_NO", "")              + String.valueOf((char)30)
                        + "SIGN_PERSON"               + String.valueOf((char)31) + mgr.readValue("SIGN_PERSON", "")          + String.valueOf((char)30)
                        + "MACH_GRP_NO"               + String.valueOf((char)31) + mgr.readValue("MACH_GRP_NO", "")          + String.valueOf((char)30)
                        + "PURPOSE_NO"                + String.valueOf((char)31) + mgr.readValue("PURPOSE_NO", "")           + String.valueOf((char)30)
                        + "RECEIVE_UNIT_NO"           + String.valueOf((char)31) + mgr.readValue("RECEIVE_UNIT_NO", "")      + String.valueOf((char)30)
                        + "SEND_UNIT_NO"              + String.valueOf((char)31) + mgr.readValue("SEND_UNIT_NO", "")         + String.valueOf((char)30)
                        + "PROJECT_NO"                + String.valueOf((char)31) + (ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT))           + String.valueOf((char)30)
                        + "ZONE_NO"                   + String.valueOf((char)31) + (ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE))                + String.valueOf((char)30)
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
        data.setFieldItem("DOC_CLASS",getCurrentPageDocClass());
        data.setFieldItem("TITLE","TEST...");//TODO ..
        setDefaultValues(data);

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
               + "FORMAT_SIZE"   + String.valueOf((char)31) + sFormatSize + String.valueOf((char)30)
               + "DOC_CODE"   + String.valueOf((char)31) + mgr.readValue("DOC_CODE", "")  + String.valueOf((char)30)
               + "REV_TITLE"   + String.valueOf((char)31) + mgr.readValue("REV_TITLE", "")  + String.valueOf((char)30)
               + "MAIN_CONTENT"   + String.valueOf((char)31) + mgr.readValue("MAIN_CONTENT", "")  + String.valueOf((char)30)
               + "RESPONSE_CODE"   + String.valueOf((char)31) + mgr.readValue("RESPONSE_CODE", "")  + String.valueOf((char)30)
               + "INDEX_CREATED"   + String.valueOf((char)31) + mgr.readValue("INDEX_CREATED", "")  + String.valueOf((char)30)
               + "COPIES"   + String.valueOf((char)31) + mgr.readValue("COPIES", "")  + String.valueOf((char)30)
               + "PAGES"   + String.valueOf((char)31) + mgr.readValue("PAGES", "")  + String.valueOf((char)30)
               + "EMERGENCY"   + String.valueOf((char)31) + mgr.readValue("EMERGENCY", "")  + String.valueOf((char)30)
               + "RECEIPT"   + String.valueOf((char)31) + mgr.readValue("RECEIPT", "")  + String.valueOf((char)30)
               + "RECEIPT_REQUEST"   + String.valueOf((char)31) + mgr.readValue("RECEIPT_REQUEST", "")  + String.valueOf((char)30)
               + "COMPLETE_DATE"   + String.valueOf((char)31) + mgr.readValue("COMPLETE_DATE", "")  + String.valueOf((char)30)
               + "INNER_DOC_CODE"   + String.valueOf((char)31) + mgr.readValue("INNER_DOC_CODE", "")  + String.valueOf((char)30)
               + "AS_BUILT_DRAWING"   + String.valueOf((char)31) + mgr.readValue("AS_BUILT_DRAWING", "")  + String.valueOf((char)30)
               + "TRANSFER_NO"   + String.valueOf((char)31) + mgr.readValue("TRANSFER_NO", "")  + String.valueOf((char)30)
               + "DOC_STATE"     + String.valueOf((char)31) + mgr.readValue("DOC_STATE", "")  + String.valueOf((char)30)
               + "PROJ_NO"       + String.valueOf((char)31) + mgr.readValue("PROJ_NO", "")  + String.valueOf((char)30)
               + "SIGN_PERSON"   + String.valueOf((char)31) + mgr.readValue("SIGN_PERSON", "")  + String.valueOf((char)30)
               + "MACH_GRP_NO"   + String.valueOf((char)31) + mgr.readValue("MACH_GRP_NO", "")  + String.valueOf((char)30)
               + "PURPOSE_NO"    + String.valueOf((char)31) + mgr.readValue("PURPOSE_NO", "")  + String.valueOf((char)30)
               + "RECEIVE_UNIT_NO"   + String.valueOf((char)31) + mgr.readValue("RECEIVE_UNIT_NO", "")  + String.valueOf((char)30)
               + "SEND_UNIT_NO"   + String.valueOf((char)31) + mgr.readValue("SEND_UNIT_NO", "")  + String.valueOf((char)30)
               + "PROJECT_NO"     + String.valueOf((char)31) + (ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT))            + String.valueOf((char)30)
               + "ZONE_NO"        + String.valueOf((char)31) + (ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE))              + String.valueOf((char)30)
               ;

         headset.last();
         
         String strRcvDate = headset.getDbValue("RECEIVE_DATE");//mgr.readValue("RECEIVE_DATE", "") ;
         String strSndDate = headset.getDbValue("SEND_DATE");//mgr.readValue("SEND_DATE", "") ;
         String strEmgDate = headset.getDbValue("EMERGENCY_DATE");//mgr.readValue("EMERGENCY_DATE", "") ;
         
         strRcvDate =  mgr.isEmpty(strRcvDate) ? "" : strRcvDate;
         strSndDate =  mgr.isEmpty(strSndDate) ? "" : strSndDate;
         strEmgDate =  mgr.isEmpty(strEmgDate) ? "" : strEmgDate;
         
         sAttr +=   "RECEIVE_DATE"   + String.valueOf((char)31) + strRcvDate + String.valueOf((char)30) 
                  + "SIGN_STATUS"               + String.valueOf((char)31) + "Initial"                                 + String.valueOf((char)30)
                  + "SEND_DATE"   + String.valueOf((char)31) + strSndDate  + String.valueOf((char)30)
                  + "EMERGENCY_DATE"   + String.valueOf((char)31) + strEmgDate  + String.valueOf((char)30)
                  + "FROM_DOC_CLASS"   + String.valueOf((char)31) + headset.getRow().getValue("DOC_CLASS")  + String.valueOf((char)30)
                  + "FROM_DOC_NO"   + String.valueOf((char)31) + headset.getRow().getValue("DOC_NO")  + String.valueOf((char)30)
                  + "FROM_DOC_SHEET"   + String.valueOf((char)31) + headset.getRow().getValue("FIRST_SHEET_NO")  + String.valueOf((char)30)
                  + "FROM_DOC_REV"   + String.valueOf((char)31) + headset.getRow().getValue("FIRST_REVISION")  + String.valueOf((char)30);

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
         
         
         String sAttrForDito =  "DOC_CLASS" + String.valueOf((char)31) + headset.getRow().getValue("DOC_CLASS") + String.valueOf((char)30)
                              + "DOC_NO" + String.valueOf((char)31) + headset.getRow().getValue("DOC_NO") + String.valueOf((char)30)
                              + "DOC_SHEET" + String.valueOf((char)31) + headset.getRow().getValue("FIRST_SHEET_NO") + String.valueOf((char)30)
                              + "DOC_REV" + String.valueOf((char)31) + headset.getRow().getValue("FIRST_REVISION") + String.valueOf((char)30)
                              + "ORG_NO" + String.valueOf((char)31) + headset.getRow().getValue("RECEIVE_UNIT_NO") + String.valueOf((char)30)
                              + "SIGN_STATUS" + String.valueOf((char)31) + "FALSE" + String.valueOf((char)30)
                              + "IS_MAIN" + String.valueOf((char)31) + "TRUE" + String.valueOf((char)30);
         
         cmd = trans.addCustomCommand("UPDATEDOCISSUETARGETORG","DOC_ISSUE_TARGET_ORG_API.New__");
         cmd.addParameter("INFO");
         cmd.addParameter("DUMMY1");
         cmd.addParameter("DUMMY2");
         cmd.addParameter("ATTR",sAttrForDito);
         cmd.addParameter("DUMMY1","DO");
         
         mgr.perform(trans);
         trans.clear();
         // --modified the doc_issue instance to have the retained language code and the format - end --

         headset.goTo(currrow);
         
         if (!("__SAVERETURN".equals(finalAction)))
         {
            newRow();            
         }
         mgr.redirectTo(getCorrespondingDocIssuePage() + "?EDIT_LAYOUT=TRUE&DOC_CLASS=" + headset.getRow().getValue("DOC_CLASS") + "&DOC_NO=" + headset.getRow().getValue("DOC_NO"));
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
      cmd.setParameter("PROJECT_NO", ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT));
      cmd.setParameter("ZONE_NO", ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE));
//      _person_default_project
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
         mgr.transferDataTo(getCorrespondingDocIssuePage(),keys);
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
      setLabel("DOCMAWDOCTITLEOVWDOCCLASS: Doc Class").
      setHidden();
      
      headblk.addField("SDOCNAME").
      setSize(20).
      setReadOnly().
      
      setFunction("DOC_CLASS_API.GET_NAME(:DOC_CLASS)").
      setLabel("DOCMAWDOCTITLEOVWSDOCNAME: Doc Class Desc").
      setHidden();
      
      headblk.addField("DOC_NO").
      setSize(20).
      setMaxLength(120).
      setReadOnly().
      setInsertable().
      setUpperCase().
      setDynamicLOV("DOC_TITLE").
      setSecureHyperlink("DocIssue.page", "DOC_CLASS,DOC_NO").
      setLabel("DOCMAWDOCTITLEOVWDOCNO: Doc No").
      setHidden();
      
   
      
      
      customizeHeadblk();
      
      //
      // Hidden Fields
      //
      
      
      headblk.addField("FROM_DOC_CLASS").
      setInsertable().
      setLabel("DOCTITLEFROMDOCCLASS: From Doc Class").
      setHidden().
      setSize(12);
      
      headblk.addField("FROM_DOC_NO").
      setInsertable().
      setLabel("DOCTITLEFROMDOCNO: From Doc No").
      setHidden().
      setSize(120);
      
      headblk.addField("FROM_DOC_SHEET").
      setInsertable().
      setLabel("DOCTITLEFROMDOCSHEET: From Doc Sheet").
      setHidden().
      setSize(10);
      
      headblk.addField("FROM_DOC_REV").
      setInsertable().
      setLabel("DOCTITLEFROMDOCREV: From Doc Rev").
      setHidden().
      setSize(6);
      
      headblk.addField("PROJECT_NO").
      setInsertable().
      setLabel("DOCTITLEPROJECTNO: Project No").
      setSize(5).
      setHidden();
      headblk.addField("ZONE_NO").
      setInsertable().
      setLabel("DOCTITLEZONENO: Zone No").
      setSize(5).
      setHidden();
      headblk.addField("STRUCTURE").
      setCheckBox("0,1").
      setLabel("DOCTITLESTRUCTURE: Structure").
      setHidden();
      
      // Bug 118728 the structure checkbox didn't get saved bacause of the use of .setFunction()
      
      headblk.addField("OBJ_CONN_REQ").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_Reference_Object_Req_API").
      setLabel("DOCMAWDOCTITLEOVWOBJCONNREQ: Object Connection").
      setHidden();

      headblk.addField("MAKE_WASTE_REQ").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_Make_Waste_Req_API").
      setLabel("DOCMAWDOCTITLEOVWMAKEWASTEREQ: Destroy").
      setHidden();
      
      
      headblk.addField("VIEW_FILE_REQ").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_View_Copy_Req_API").
      setLabel("DOCMAWDOCTITLEOVWVIEWFILEREQ: View Copy").
      setHidden();
      
      headblk.addField("SAFETY_COPY_REQ").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_Safety_Copy_Req_API").
      setLabel("DOCMAWDOCTITLEOVWSAFETYCOPYREQ: Safety Copy").
      setHidden();
      headblk.addField("TITLE").
      setSize(20).
      setMaxLength(250).
      setLabel("DOCMAWDOCTITLEOVWDOCTITLE: Title").
      setHidden();

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
      setLabel("DOCMAWDOCTITLEOVWFIRSTSHEET: First Sheet No").
      setHidden();

      headblk.addField("FIRST_REVISION").
      setSize(20).
      setMaxLength(6).
      setReadOnly().
      setInsertable().
      setUpperCase().
      unsetQueryable().
      setCustomValidation("FIRST_REVISION","TITLE_REV"). //Bug 73808, Start, Added custom validation
      setLabel("DOCMAWDOCTITLEOVWFIRSTREV: First Document Revision").
      setHidden();
      
      
      headblk.addField("ALTERNATE_DOCUMENT_NUMBER").
      setSize(20).
      setMaxLength(120).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCTITLEOVWALTEDOCNU: Alternate Doc No").
      setHidden();
      
      
      headblk.addField("NUMBER_GENERATOR").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setHidden().
      setFunction("''").
      setHidden();
      

      headblk.addField("NUM_GEN_TRANSLATED").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setFunction("''").
      setLabel("DOCMAWDOCTITLEOVWNUMBERGENERATOR: Number Generator").
      setHidden();
      

      headblk.addField("ID1").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLOV("Id1Lov.page").
      setLabel("DOCMAWDOCTITLEOVWNUMBERCOUNTERID1: Number Counter ID1").
      setHidden();
      

      headblk.addField("ID2").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLOV("Id2Lov.page","ID1").
      setLabel("DOCMAWDOCTITLEOVWNUMBERCOUNTERID2: Number Counter ID2").
      setHidden();
      

      headblk.addField("LANGUAGE_CODE"). 
      setFunction("''").
      setSize(20).
      setMaxLength(20).
      setDynamicLOV("APPLICATION_LANGUAGE").
      setLabel("DOCMAWDOCTITOWVANGUAGECODE: Language Code").
      setHidden();
      

      headblk.addField("FORMAT_SIZE").
      setFunction("''").
      setSize(20).
      setMaxLength(20).
      setUpperCase().
      setDynamicLOV("DOC_CLASS_FORMAT_LOV","DOC_CLASS").
      setLabel("DOCMAWDOCTITOWVFORMATSIZE: Format").
      setHidden();

      headblk.addField("BOOKING_LIST").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLOV("BookListLov.page", "ID1,ID2").//Bug Id 73606
      setLabel("DOCMAWDOCTITLEOVWBOOKINGLIST: Booking List").
      setHidden();
      

      headblk.addField("INFO").
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCTITLEOVWINFO: Note").
      setHidden();
      

      headblk.addField("TITLE_REV").
      setSize(20).
      setMaxLength(6).
      setInsertable().
      setUpperCase().
      setLabel("DOCMAWDOCTITLEOVWTITLEREVISION: Title Revision").
      setHidden();
      

      headblk.addField("TITLE_REV_NOTE").
      setSize(20).
      setMaxLength(2000).
      setInsertable().
      setLabel("DOCMAWDOCTITLEOVWTITLEREVISIONNOTE: Title Revision Note").
      setHidden();
      
   

      headblk.addField("ORIG_DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWBASECLASS1: List of Based on Document Class")).
      setLabel("DOCMAWDOCTITLEOVWORIGDOCCLASS: Based on Doc Class").
      setHidden();
      

      headblk.addField("ORIG_DOC_NO").
      setSize(20).
      setMaxLength(120).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOC_TITLE").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWBASENO1: List of Based on Document No")).
      setLabel("DOCMAWDOCTITLEOVWORIGDOCNO: Based on Doc No").
      setHidden();
      

      headblk.addField("ORIG_DOC_SHEET").
      setSize(20).
      setDefaultNotVisible().
      setDynamicLOV("DOC_ISSUE_LOV1").
      setLabel("DOCMAWDOCTITLEOVWBASESHEET: Based on Sheet").
      setHidden();
      

      headblk.addField("ORIG_DOC_REV").
      setSize(20).
      setMaxLength(6).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOC_ISSUE").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWBASEREV: List of Based on Document Revision")).
      setLabel("DOCMAWDOCTITLEOVWORIGDOCREV: Based on Doc Revision").
      setHidden();
      

      headblk.addField("REPL_BY_DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setDefaultNotVisible().
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWREPCLASS: List of Replaced by Document Class")).
      setLabel("DOCMAWDOCTITLEOVWREPLBYDOCCLASS: Replaced by Doc Class").
      setHidden();
      

      headblk.addField("REPL_BY_DOC_NO").
      setSize(20).
      setMaxLength(120).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOC_TITLE","REPL_BY_DOC_CLASS DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWREPNO: List of Replaced by Document No")).
      setLabel("DOCMAWDOCTITLEOVWREPLBYDOCNO: Replaced by Doc No").
      setHidden();
      

      headblk.addField("CONFIDENTIAL").
      setSize(20).
      setMaxLength(15).
      setUpperCase().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCTITLEOVWCONFIDENTIAL: Confidential").
      setHidden();
      

      headblk.addField("ISO_CLASSIFICATION").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCTITLEOVWISOCLASSIFICATION: ISO Classification").
      setHidden();
      

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
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      customizeHeadlay();
      
     
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
      return getTitle();
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
      appendDirtyJavaScript("    '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=FIRST_REVISION'\n");
      appendDirtyJavaScript("    + '&FIRST_REVISION=' + URLClientEncode(getValue_('FIRST_REVISION',i))\n");
      appendDirtyJavaScript("    );\n");
      appendDirtyJavaScript(" window.status='';\n");
      appendDirtyJavaScript(" if( checkStatus_(r,'TITLE_REV',i,'Title Rev') )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    assignValue_('TITLE_REV',i,0);\n");
      appendDirtyJavaScript(" }\n");
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
      appendDirtyJavaScript(" if( checkStatus_(r,'DOC_CLASS',i,'Doc Class') )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    assignValue_('SDOCNAME',i,0);\n");
      appendDirtyJavaScript("    assignSelectBoxValue_('VIEW_FILE_REQ',i,1);\n");
      appendDirtyJavaScript("    assignSelectBoxValue_('OBJ_CONN_REQ',i,2);\n");
      appendDirtyJavaScript("    assignSelectBoxValue_('MAKE_WASTE_REQ',i,3);\n");
      appendDirtyJavaScript("    assignSelectBoxValue_('SAFETY_COPY_REQ',i,4);\n");
      appendDirtyJavaScript("    assignValue_('FIRST_REVISION',i,5);\n");
      appendDirtyJavaScript("    assignValue_('FIRST_SHEET_NO',i,6);\n");
      appendDirtyJavaScript("    assignValue_('TITLE_REV',i,7);\n");
      appendDirtyJavaScript("    assignValue_('NUMBER_GENERATOR',i,8);\n");
      appendDirtyJavaScript("    assignValue_('NUM_GEN_TRANSLATED',i,9);\n");
      appendDirtyJavaScript("    assignValue_('ID1',i,10);\n");
      appendDirtyJavaScript("    assignValue_('ID2',i,11);\n");
      appendDirtyJavaScript("    if (i>0) //Editable tables\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       assignCheckBoxValue_('_STRUCTURE',i-1,12,'1');\n");
      appendDirtyJavaScript("       setCheckBox_('STRUCTURE',getField_('_STRUCTURE',i-1).checked,i,'1');\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("       assignCheckBoxValue_('STRUCTURE',i,12,'1');\n");
      appendDirtyJavaScript("    assignValue_('LANGUAGE_CODE',i,13);\n");
      appendDirtyJavaScript(" }\n");
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
      appendDirtyJavaScript("    lovId1(i,params);\n");
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
      appendDirtyJavaScript("    lovId2(i,params);\n");
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
      appendDirtyJavaScript("    lovBookingList(i,params);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n"); 
      appendDirtyJavaScript("      return;\n"); 
      appendDirtyJavaScript("}\n");
      //Bug Id 74966, End
   }

}
