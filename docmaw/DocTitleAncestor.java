package ifs.docmaw;

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
import ifs.fnd.util.Str;
import ifs.genbaw.GenbawConstants;

public abstract class DocTitleAncestor extends ASPPageProvider {
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
   private String  sFilePath;
   private boolean bTranferToEDM;
   
   //new method intended to be implemented by subclasses .begin..
   protected abstract String getCurrentPageDocClass();
   protected String getCurrentPageSubDocClass(){
      return null;
   }
   protected void customizeHeadblk(){
      ASPManager mgr = getASPManager();
      headblk.addField("REV_TITLE").
      setInsertable().
      setLabel("DOCISSUERECEIVEREVTITLE: Rev Title").
      setSize(100);

      headblk.addField("DOC_CODE").
      setInsertable().
      setLabel("DOCISSUERECEIVEDOCCODE: Doc Code").
      setSize(20);
      
      headblk.addField("SEND_UNIT_NO").
      setInsertable().
      setLabel("DOCISSUERECEIVESENDUNITNO: Send Unit No").
      setCustomValidation("SEND_UNIT_NO", "SEND_UNIT_NAME").
      setDynamicLOV("GENERAL_ZONE_LOV").
      setSize(20);
      
      headblk.addField("SEND_UNIT_NAME").
      setReadOnly().
      setLabel("DOCISSUERECEIVESENDUNITNAME: Send Unit Name").
      setFunction("GENERAL_ZONE_API.GET_ZONE_DESC(:SEND_UNIT_NO)").
      setReadOnly().
      setSize(20);
      
      headblk.addField("MAIN_CONTENT").
      setInsertable().
      setLabel("DOCISSUERECEIVEMAINCONTENT: Main Content").
      setSize(20);
      
      headblk.addField("RECEIVE_UNIT_NO").
      setInsertable().
      setLabel("DOCISSUERECEIVEUNITNO: Receive Unit No").
      setDynamicLOV("GENERAL_ZONE_LOV").
      setCustomValidation("RECEIVE_UNIT_NO", "RECEIVE_UNIT_NAME").
      setSize(20);
      
      headblk.addField("RECEIVE_UNIT_NAME").
      setLabel("DOCISSUERECEIVEUNITNAME: Receive Unit Name").
      setFunction("GENERAL_ZONE_API.Get_Zone_Desc(:RECEIVE_UNIT_NO)").
      setReadOnly().
      setSize(20);
      mgr.getASPField("RECEIVE_UNIT_NO").setValidation("RECEIVE_UNIT_NAME");
      
      headblk.addField("PURPOSE_NO").
      setInsertable().
      setMandatory().
      setDynamicLOV("DOC_COMMUNICATION_SEQ").
      setLabel("DOCISSUERECEIVEPURPOSENO: Purpose No").
      setSize(20);
      
      headblk.addField("PURPOSE_NAME").
      setLabel("DOCISSUERECEIVEPURPOSENAME: Purpose Name").
      setFunction("DOC_COMMUNICATION_SEQ_API.Get_Purpose_Name(:PURPOSE_NO)").
      setReadOnly().
      setSize(20);
      mgr.getASPField("PURPOSE_NO").setValidation("PURPOSE_NAME");
      // group 1 end
      // group 2 begin
      headblk.addField("PROJ_NO").
      setInsertable().
      setMandatory().
      setLabel("DOCISSUEPROJNO: Proj No").
      setDynamicLOV("GENERAL_PROJECT").
      setSize(20);
      
      headblk.addField("PROJ_NAME").
      setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC(:PROJ_NO)").
      setLabel("DOCISSUEPROJNAME: Proj Name").
      setReadOnly().
      setSize(20); 
      mgr.getASPField("PROJ_NO").setValidation("PROJ_NAME");
      
      
      headblk.addField("COPIES","Number").
      setInsertable().
      setLabel("DOCISSUERECEIVECOPIES: Copies").
      setSize(20);
      
      headblk.addField("PAGES","Number").
      setInsertable().
      setLabel("DOCISSUERECEIVEPAGES: Pages").
      setSize(20);
      
      headblk.addField("RECEIVE_DATE","Date").
      setInsertable().
      setLabel("DOCISSUERECEIVERECEIVEDATE: Receive Date").
      setSize(20);
      
      headblk.addField("SEND_DATE","Date").
      setInsertable().
      setLabel("DOCISSUERECEIVESENDDATE: Send Date").
      setSize(20);
      
      headblk.addField("EMERGENCY").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCISSUERECEIVEEMERGENCY: Emergency").
      setSize(20);
      
      headblk.addField("EMERGENCY_DATE","Date").
      setInsertable().
      setLabel("DOCISSUERECEIVEEMERGENCYDATE: Emergency Date").
      setSize(20);
      
      headblk.addField("RECEIPT").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCISSUERECEIVERECEIPT: Receipt").
      setSize(20);
      
      headblk.addField("RECEIPT_REQUEST","Date").
      setInsertable().
      setLabel("DOCISSUERECEIVERECEIPTREQUEST: Receipt Request").
      setSize(20);
      
      headblk.addField("USER_CREATED").
      setLabel("DOCMAWDOCISSUECREATEDBY: Created By").
      setDefaultNotVisible().
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id")).
      setReadOnly().
      setSize(20);
      headblk.addField("USER_CREATED_NAME").setFunction("Fnd_User_Api.Get_Description(:USER_CREATED)").setReadOnly().setSize(20);
      mgr.getASPField("USER_CREATED").setValidation("USER_CREATED_NAME");
      
      headblk.addField("DT_CRE","Date").
      setSize(20).
      setReadOnly().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDATECREATED: Date Created");

      //group 2 end
      headblk.addField("RESPONSE_CODE").
      setInsertable().
      setLabel("DOCISSUERECEIVERESPONSECODE: Response Code").
      setSize(20);
      
      headblk.addField("COMPLETE_DATE","Date").
      setInsertable().
      setLabel("DOCISSUERECEIVECOMPLETEDATE: Complete Date").
      setSize(20);
      
      headblk.addField("INNER_DOC_CODE").
      setInsertable().
      setLabel("DOCISSUERECEIVEINNERDOCCODE: Inner Doc Code").
      setSize(20);
      
      headblk.addField("AS_BUILT_DRAWING").
      setInsertable().
      setLabel("DOCISSUERECEIVEASBUILTDRAWING: As Built Drawing").
      setSize(20);
      
      headblk.addField("TRANSFER_NO").
      setInsertable().
      setLabel("DOCISSUERECEIVETRANSFERNO: Transfer No").
      setSize(20);
      
      headblk.addField("DOC_STATE").
      enumerateValues("Doc_Issue_State_API").
      setSelectBox().
      setInsertable().
      setLabel("DOCISSUERECEIVEDOCSTATE: Doc State").
      setSize(20);
      
      headblk.addField("SIGN_PERSON").
      setInsertable().
      setLabel("DOCISSUERECEIVESIGNPERSON: Sign Person").
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id")).
      setSize(20);
      
      headblk.addField("SIGN_PERSON_NAME").setFunction("Person_Info_Api.Get_Name(:SIGN_PERSON)").setReadOnly().setSize(20);
      mgr.getASPField("SIGN_PERSON").setValidation("SIGN_PERSON_NAME");
      

      headblk.addField("MACH_GRP_NO").
      setInsertable().
      setLabel("DOCISSUERECEIVEMACHGRPNO: Mach Grp No").
      setDynamicLOV("GENERAL_MACH_GROUP").
      setSize(20); 
      
      headblk.addField("MACH_GRP_DESC").setFunction("General_Mach_Group_Api.Get_Mach_Grp_Desc(:MACH_GRP_NO)").setReadOnly().setSize(20); 
      mgr.getASPField("MACH_GRP_NO").setValidation("MACH_GRP_DESC");
      
      headblk.addField("ROWSTATE").setFunction("DOC_ISSUE_API.Get_State(:DOC_CLASS,:DOC_NO,:FIRST_SHEET_NO,:FIRST_REVISION)").setLabel("DOCISSUERECEIVEROWSTATE: Rowstate").setReadOnly().setSize(20);
      
      headblk.addField("TRANSLATE_TITLE").
      setInsertable().
      setLabel("DOCISSUETRANSLATETITLE: Translate Title").
      setSize(100);
      
      headblk.addField("DOC_LOC_NO").
      setInsertable().
      setLabel("DOCISSUEDOCLOCNO: Doc Loc No").
      setSize(20);
      
      headblk.addField("ATTACH_PAGES","Number").
      setInsertable().
      setLabel("DOCISSUEATTACHPAGES: Attach Pages").
      setSize(20);
      
      headblk.addField("WBS_NO").
      setInsertable().
      setLabel("DOCISSUEWBSNO: Wbs No").
      setSize(20);
      
      headblk.addField("COMPONENT_NO").
      setInsertable().
      setLabel("DOCISSUECOMPONENTNO: Component No").
      setSize(20);   
      
      headblk.addField("DOC_TYPE_ATTR").
      setInsertable().
      setLabel("DOCISSUEDOCTYPEATTR: Doc Type Attr").
      setSize(20);
      
      headblk.addField("SUB_CLASS").
      setInsertable().
      setLabel("DOCISSUESUBCLASS: Sub Class").
      setDynamicLOV("DOC_SUB_CLASS","DOC_CLASS").
      setSize(20);
      
      headblk.addField("SUB_CLASS_NAME").setFunction("Doc_Sub_Class_Api.Get_Sub_Class_Name(:DOC_CLASS,:SUB_CLASS)").setLabel("DOCISSUESUBCLASSNAME: Sub Class Name").setReadOnly().setSize(20); 
      mgr.getASPField ("SUB_CLASS").setValidation("SUB_CLASS_NAME");
      
      headblk.addField("ATTACH_TYPE").
      setInsertable().
      setLabel("DOCISSUEATTACHTYPE: Attach Type").
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("WHERE", DocmawConstants.isLibaryDocClass(getCurrentPageDocClass()) ? " COMP_DOC='TRUE' " : " SUPP_DOC='TRUE' ").
      setSize(20);
      
      headblk.addField("ATTACH_TYPE_NAME").setFunction("DOC_CLASS_API.Get_Name(:ATTACH_TYPE)").setLabel("DOCISSUEATTACHTYPENAME: Attach Type Name").setReadOnly().setSize(20);    
      mgr.getASPField("ATTACH_TYPE").setValidation("ATTACH_TYPE_NAME");
      
      headblk.addField("MEETING_TYPE").
      setInsertable().
      setLabel("DOCISSUEMEETINGTYPE: Meeting Type").
      setDynamicLOV("DOC_MEETING_TYPE").
      setSize(20);      
      
      headblk.addField("MEETING_TYPE_NAME").setFunction("Doc_Meeting_Type_Api.Get_Type_Name(:MEETING_TYPE)").setLabel("DOCISSUEMEETINGTYPENAME: Meeting Type Name").setReadOnly().setSize(20);    
      mgr.getASPField("MEETING_TYPE").setValidation("MEETING_TYPE_NAME");
      
      headblk.addField("MAIN_SEND").
      setInsertable().
      setLabel("DOCISSUEMAINSEND: Main Send").
      setDynamicLOV("GENERAL_ZONE_LOV", "PROJ_NO", 500, 550, true, true).
      setSize(20);
      
      headblk.addField("MAIN_SEND_NAME").setFunction("''").setLabel("DOCISSUEMAINSENDNAME: Main Send Name").setReadOnly().setSize(20);  
      
      headblk.addField("CO_SEND").
      setInsertable().
      setLabel("DOCISSUECOSEND: Co Send").
      setDynamicLOV("GENERAL_ZONE_LOV", "PROJ_NO", 500, 550, true, true).
      setSize(20);
      headblk.addField("CO_SEND_NAME").setFunction("''").setLabel("DOCISSUECOSENDNAME: Co Send Name").setReadOnly().setSize(20);
      
      headblk.addField("INNER_SEND").
      setInsertable().
      setLabel("DOCISSUEINNERSEND: Inner Send").
      setDynamicLOV("GENERAL_DEPARTMENT_LOV", "SEND_UNIT_NO PARENT_ORG_NO", 500, 550, true, true).
      setSize(20);
      
      headblk.addField("INNER_SEND_NAME").setFunction("''").setLabel("DOCISSUEINNERSENDNAME: Inner Send Name").setReadOnly().setSize(20);      
      
      headblk.addField("SEC_LEVEL").
      setInsertable().
      setLabel("DOCISSUESECLEVEL: Sec Level").
      setDynamicLOV("DOC_SEC_LEVEL").
      setSize(20);
      
      headblk.addField("LEVEL_NAME").setFunction("Doc_Sec_Level_Api.Get_Level_Name(:SEC_LEVEL)").setLabel("DOCISSUELEVELNAME: Level Name").setReadOnly().setSize(20);
      mgr.getASPField("SEC_LEVEL").setValidation("LEVEL_NAME");
      
      headblk.addField("PAGE_SIZE").
      setInsertable().
      setLabel("DOCISSUEPAGESIZE: Page Size").
      setDynamicLOV("DOC_PAGE_SIZE").
      setSize(20);
      
      headblk.addField("SIZE_NAME").setFunction("Doc_Page_Size_Api.Get_Size_Name(:PAGE_SIZE)").setLabel("DOCISSUESIZELNAME: Size Name").setReadOnly().setSize(20);  
      mgr.getASPField("PAGE_SIZE").setValidation("SIZE_NAME");
      
      headblk.addField("BOOKLET_NO").
      setInsertable().
      setLabel("DOCISSUEBOOKLETNO: Booklet No").
      setDynamicLOV("DOC_BOOKLET").
      setSize(20);
      
      headblk.addField("BOOKLET_NAME").setFunction("Doc_Booklet_Api.Get_Booklet_Name(:BOOKLET_NO)").setLabel("DOCISSUEBOOKLETNAME: Booklet Name").setReadOnly().setSize(20); 
      mgr.getASPField("BOOKLET_NO").setValidation("BOOKLET_NAME");
      
      headblk.addField("SPECIALTY_NO").
      setInsertable().
      setLabel("DOCISSUESPECIALTYNO: Specialty No").
      setDynamicLOV("GENERAL_SPECIALTY", "PROJ_NO").
      setSize(20);
      
      headblk.addField("SPECIALTY_DESC").setFunction("General_Specialty_Api.Get_Specialty_Desc(:PROJ_NO, :SPECIALTY_NO)").setLabel("DOCISSUESPECIALTYDESC: Specialty Desc").setReadOnly().setSize(20);  
      mgr.getASPField("SPECIALTY_NO").setValidation("SPECIALTY_DESC");
      
      headblk.addField("PROFESSION_NO").
      setInsertable().
      setLabel("DOCISSUEPROFESSIONNO: Profession No").
      setDynamicLOV("DOC_PROFESSION").
      setSize(20);
      
      headblk.addField("PROFESSION_NAME").
      setFunction("DOC_PROFESSION_API.Get_Profession_Name(:PROFESSION_NO)").
      setLabel("DOCISSUEPROFESSIONDESC: Profession Name").
      setReadOnly().
      setSize(20);  
      mgr.getASPField("PROFESSION_NO").setValidation("PROFESSION_NAME");
      
      headblk.addField("COMPONENT_TYPE").
      setInsertable().
      setLabel("DOCISSUECOMPONENTTYPE: Component Type").
      setDynamicLOV("GENERAL_COMPONENT_TYPE", "PROJ_NO").
      setSize(20);
      
      headblk.addField("COMPONENT_TYPE_DESC").setFunction("General_Component_Type_Api.Get_Component_Type_Desc(:PROJ_NO, :COMPONENT_TYPE)").setLabel("DOCISSUECOMPONENTTYPEDESC: Component Type Desc").setReadOnly().setSize(20); 
      mgr.getASPField("COMPONENT_TYPE").setValidation("COMPONENT_TYPE_DESC");
      
      headblk.addField("LOT_NO").
      setInsertable().
      setLabel("DOCISSUELOTNO: Lot No").
      setDynamicLOV("GENERAL_LOT", "PROJ_NO").
      setSize(20);
      
      headblk.addField("LOT_DESC").setFunction("GENERAL_LOT_API.Get_Lot_Desc(:PROJ_NO, :LOT_NO)").setLabel("DOCISSUELOTDESC: Lot Desc").setReadOnly().setSize(20);  
      mgr.getASPField("LOT_NO").setValidation("LOT_DESC");
      
      headblk.addField("SEND_DEPT").
      setInsertable().
      setLabel("DOCISSUESENDDEPT: Send Dept").
      setDynamicLOV("GENERAL_DEPARTMENT_LOV", "SEND_UNIT_NO PARENT_ORG_NO").
      setSize(20);
      
      headblk.addField("SEND_DEPT_DESC").setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:SEND_DEPT)").setLabel("DOCISSUESENDDEPTDESC: Send Dept Desc").setReadOnly().setSize(20);  
      mgr.getASPField("SEND_DEPT").setValidation("SEND_DEPT_DESC");
      
      headblk.addField("ROOM_NO").
      setInsertable().
      setLabel("DOCISSUEROOMNO: Room No").
      setDynamicLOV("GENERAL_ROOM", "PROJ_NO").
      setSize(20);
      
      headblk.addField("ROOM_DESC").setFunction("GENERAL_ROOM_API.Get_Room_Desc(:PROJ_NO, :ROOM_NO)").setLabel("DOCISSUEROOMDESC: Room Desc").setReadOnly().setSize(20);  
      mgr.getASPField("ROOM_NO").setValidation("ROOM_DESC");
      
      headblk.addField("GRADE_NO").
      setInsertable().
      setLabel("DOCISSUEGRADENO: Grade No").
      setDynamicLOV("GENERAL_QUALITY_GRADE").
      setSize(20);
      
      headblk.addField("GRADE_DESC").setFunction("GENERAL_QUALITY_GRADE_API.Get_Grade_Desc(:GRADE_NO)").setLabel("DOCISSUEGRADEDESC: Grade Desc").setReadOnly().setSize(20);  
      mgr.getASPField("GRADE_NO").setValidation("GRADE_DESC");
      
      headblk.addField("EXTAND_DOC_CODE1").
      setInsertable().
      setLabel("DOCISSUERECEIVEEXTANDDOCCODE1: Extand Doc Code1").
      setSize(20);
      
      headblk.addField("EXTAND_DOC_CODE2").
      setInsertable().
      setLabel("DOCISSUERECEIVEEXTANDDOCCODE2: Extand Doc Code2").
      setSize(20);
      
      headblk.addField("EXTAND_DOC_CODE3").
      setInsertable().
      setLabel("DOCISSUERECEIVEEXTANDDOCCODE3: Extand Doc Code3").
      setSize(20);
   }
   
   protected abstract void customizeHeadlay();
   
   protected void customizeFieldFromDocClass(){
      headblk.addField("FROM_DOC_CLASS").
      setInsertable().
      setLabel("DOCTITLEFROMDOCCLASS: From Doc Class").
      setHidden().
      setSize(12);
   }
   
   protected void customizeHeadView(){
      headblk.setView("DOC_TITLE");
   }
   
   
   
   
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
//      data.setFieldItem("FIRST_REVISION", first_rev );
      data.setFieldItem("FIRST_SHEET_NO", first_sheet );
      data.setFieldItem("STRUCTURE", structure );
      data.setFieldItem("LANGUAGE_CODE", language );
   }
   
   // new method end.
   //===============================================================
   // Construction
   //===============================================================
   public DocTitleAncestor(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
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
      
      sFilePath = null;
      bTranferToEDM = false;

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
      else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
         performRefreshParent();
      //Added by lqw 20131019
      else
      {
         String parentPage = mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE);
         if(!Str.isEmpty(parentPage) && !"null".equals(parentPage))
         {
            okFind();
         }
      }
      //Added end.
      adjust();

      //context writes
        ctx.writeValue("LAYOUT_MODE", layout_mode);
        ctx.writeFlag("BSAVEFORDOCREFERENCE", bSaveForDocReference);

        // Bug Id 82596, start
        ctx.writeFlag("BFROMPROJECTINFO", bFromProjectInfo);
   // Bug Id 82596, end

   }
   
   public void performRefreshParent()
   {
      ASPManager mgr = getASPManager();

      //
      // Refresh the selected rows
      //
      
      if (headlay.isSingleLayout())
      {
         if (headset.countRows() > 0)
         {
            headset.refreshRow();
         }
      } 
      else
      {
         refreshHeadset();
      }
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
      q.addWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         q.addWhereCondition("SUB_CLASS='" + subClass + "'");
      }
      if (mgr.dataTransfered())
      {
         //ASPBuffer buff = mgr.getTransferedData();
         q.addOrCondition(transferBuffer);
      }
      
      String parentPage = mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE);
      if(!Str.isEmpty(parentPage)){
         String parentDocClass = mgr.readValue(DocmawConstants.PARENT_DOC_CLASS);
         String parentDocNo = mgr.readValue(DocmawConstants.PARENT_DOC_NO);
         String parentDocSheet = mgr.readValue(DocmawConstants.PARENT_DOC_SHEET);
         String parentDocRev = mgr.readValue(DocmawConstants.PARENT_DOC_REV);
         StringBuffer sb = new StringBuffer(" ");
         sb.append(" PARENT_DOC_CLASS = '").append(parentDocClass).append("' ");
         sb.append(" AND PARENT_DOC_NO = '").append(parentDocNo).append("' ");
         sb.append(" AND PARENT_DOC_SHEET = '").append(parentDocSheet).append("' ");
         sb.append(" AND PARENT_DOC_REV = '").append(parentDocRev).append("' ");
         q.addWhereCondition(sb.toString());
      }else{
         String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
         String tempPersonProjects = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_PROJECTS);
         StringBuffer sb = new StringBuffer("(");
         if(!"()".equals(tempPersonZones)){
            sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
         }else{
            sb.append("(1=2)");
         }
         sb.append(")");
         q.addWhereCondition(sb.toString());
      }

      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);


      
      if(Str.isEmpty(parentPage)){
         if (headset.countRows() == 0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNODATA: No data found."));
            headset.clear();
         }
      }
      layout_mode = String.valueOf(headlay.getHistoryMode());
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      q = trans.addQuery(headblk);
      q.setWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         q.addWhereCondition("SUB_CLASS='" + subClass + "'");
      }
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   private void refreshHeadset() 
   {
      ASPManager mgr = getASPManager();

      searchURL = mgr.createSearchURL(headblk);

      ctx.writeValue("SEARCHURL", searchURL);

      trans.clear();
      q = trans.addEmptyQuery(headblk);
      q.addWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         q.addWhereCondition("SUB_CLASS='" + subClass + "'");
      }
      if (mgr.dataTransfered())
      {
         //ASPBuffer buff = mgr.getTransferedData();
         q.addOrCondition(transferBuffer);
      }
      
      String parentPage = mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE);
      if(!Str.isEmpty(parentPage)){
         String parentDocClass = mgr.readValue(DocmawConstants.PARENT_DOC_CLASS);
         String parentDocNo = mgr.readValue(DocmawConstants.PARENT_DOC_NO);
         String parentDocSheet = mgr.readValue(DocmawConstants.PARENT_DOC_SHEET);
         String parentDocRev = mgr.readValue(DocmawConstants.PARENT_DOC_REV);
         StringBuffer sb = new StringBuffer(" ");
         sb.append(" PARENT_DOC_CLASS = '").append(parentDocClass).append("' ");
         sb.append(" AND PARENT_DOC_NO = '").append(parentDocNo).append("' ");
         sb.append(" AND PARENT_DOC_SHEET = '").append(parentDocSheet).append("' ");
         sb.append(" AND PARENT_DOC_REV = '").append(parentDocRev).append("' ");
         q.addWhereCondition(sb.toString());
      }else{
         String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
         String tempPersonProjects = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_PROJECTS);
         StringBuffer sb = new StringBuffer("(");
         if(!"()".equals(tempPersonZones)){
            sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
         }else{
            sb.append("(1=2)");
         }
         sb.append(")");
         q.addWhereCondition(sb.toString());
      }

      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
      
      if(Str.isEmpty(parentPage)){
         if (headset.countRows() == 0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNODATA: No data found."));
            headset.clear();
         }
      }
      layout_mode = String.valueOf(headlay.getHistoryMode());
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
      
      // Added by Terry 20140617
      // duplicate
      String doc_code         = headset.getRow().getFieldValue("DOC_CODE");
      String rev_title        = headset.getRow().getFieldValue("REV_TITLE");
      String main_content     = headset.getRow().getFieldValue("MAIN_CONTENT");
      String response_code    = headset.getRow().getFieldValue("RESPONSE_CODE");
      String copies           = headset.getRow().getFieldValue("COPIES");
      String pages            = headset.getRow().getFieldValue("PAGES");
      String receive_date     = headset.getRow().getFieldValue("RECEIVE_DATE");
      String send_date        = headset.getRow().getFieldValue("SEND_DATE");
      String emergency        = headset.getRow().getFieldValue("EMERGENCY");
      String emergency_date   = headset.getRow().getFieldValue("EMERGENCY_DATE");
      String receipt          = headset.getRow().getFieldValue("RECEIPT");
      String receipt_request  = headset.getRow().getFieldValue("RECEIPT_REQUEST");
      String complete_date    = headset.getRow().getFieldValue("COMPLETE_DATE");
      String inner_doc_code   = headset.getRow().getFieldValue("INNER_DOC_CODE");
      String as_built_drawing = headset.getRow().getFieldValue("AS_BUILT_DRAWING");
      String transfer_no      = headset.getRow().getFieldValue("TRANSFER_NO");
      String doc_state        = headset.getRow().getFieldValue("DOC_STATE");
      String proj_no          = headset.getRow().getFieldValue("PROJ_NO");
      String sign_person      = headset.getRow().getFieldValue("SIGN_PERSON");
      String mach_grp_no      = headset.getRow().getFieldValue("MACH_GRP_NO");
      String purpose_no       = headset.getRow().getFieldValue("PURPOSE_NO");
      String receive_unit_no  = headset.getRow().getFieldValue("RECEIVE_UNIT_NO");
      String send_unit_no     = headset.getRow().getFieldValue("SEND_UNIT_NO");
      String translate_title  = headset.getRow().getFieldValue("TRANSLATE_TITLE");
      String doc_loc_no       = headset.getRow().getFieldValue("DOC_LOC_NO");
      String attach_pages     = headset.getRow().getFieldValue("ATTACH_PAGES");
      String wbs_no           = headset.getRow().getFieldValue("WBS_NO");
      String component_no     = headset.getRow().getFieldValue("COMPONENT_NO");
      String doc_type_attr    = headset.getRow().getFieldValue("DOC_TYPE_ATTR");
      String sub_class        = headset.getRow().getFieldValue("SUB_CLASS");
      String attach_type      = headset.getRow().getFieldValue("ATTACH_TYPE");
      String meeting_type     = headset.getRow().getFieldValue("MEETING_TYPE");
      String main_send        = headset.getRow().getFieldValue("MAIN_SEND");
      String co_send          = headset.getRow().getFieldValue("CO_SEND");
      String inner_send       = headset.getRow().getFieldValue("INNER_SEND");
      String sec_level        = headset.getRow().getFieldValue("SEC_LEVEL");
      String page_size        = headset.getRow().getFieldValue("PAGE_SIZE");
      String booklet_no       = headset.getRow().getFieldValue("BOOKLET_NO");
      String specialty_no     = headset.getRow().getFieldValue("SPECIALTY_NO");
      String component_type   = headset.getRow().getFieldValue("COMPONENT_TYPE");
      String lot_no           = headset.getRow().getFieldValue("LOT_NO");
      String send_dept        = headset.getRow().getFieldValue("SEND_DEPT");
      String room_no          = headset.getRow().getFieldValue("ROOM_NO");
      String grade_no         = headset.getRow().getFieldValue("GRADE_NO");
      String profession_no    = headset.getRow().getFieldValue("PROFESSION_NO");
      String extand_doc_code1 = headset.getRow().getFieldValue("EXTAND_DOC_CODE1");
      String extand_doc_code2 = headset.getRow().getFieldValue("EXTAND_DOC_CODE2");
      String extand_doc_code3 = headset.getRow().getFieldValue("EXTAND_DOC_CODE3");
      // Added end
      
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

      // Added by Terry 20140617
      // duplicate
      data.setFieldItem("DOC_CODE",         doc_code);
      data.setFieldItem("REV_TITLE",        rev_title);
      data.setFieldItem("MAIN_CONTENT",     main_content);
      data.setFieldItem("RESPONSE_CODE",    response_code);
      data.setFieldItem("COPIES",           copies);
      data.setFieldItem("PAGES",            pages);
      data.setFieldItem("RECEIVE_DATE",     receive_date);
      data.setFieldItem("SEND_DATE",        send_date);
      data.setFieldItem("EMERGENCY",        emergency);
      data.setFieldItem("EMERGENCY_DATE",   emergency_date);
      data.setFieldItem("RECEIPT",          receipt);
      data.setFieldItem("RECEIPT_REQUEST",  receipt_request);
      data.setFieldItem("COMPLETE_DATE",    complete_date);
      data.setFieldItem("INNER_DOC_CODE",   inner_doc_code);
      data.setFieldItem("AS_BUILT_DRAWING", as_built_drawing);
      data.setFieldItem("TRANSFER_NO",      transfer_no);
      data.setFieldItem("DOC_STATE",        doc_state);
      data.setFieldItem("PROJ_NO",          proj_no);
      data.setFieldItem("SIGN_PERSON",      sign_person);
      data.setFieldItem("MACH_GRP_NO",      mach_grp_no);
      data.setFieldItem("PURPOSE_NO",       purpose_no);
      data.setFieldItem("RECEIVE_UNIT_NO",  receive_unit_no);
      data.setFieldItem("SEND_UNIT_NO",     send_unit_no);
      data.setFieldItem("TRANSLATE_TITLE",  translate_title);
      data.setFieldItem("DOC_LOC_NO",       doc_loc_no);
      data.setFieldItem("ATTACH_PAGES",     attach_pages);
      data.setFieldItem("WBS_NO",           wbs_no);
      data.setFieldItem("COMPONENT_NO",     component_no);
      data.setFieldItem("DOC_TYPE_ATTR",    doc_type_attr);
      data.setFieldItem("SUB_CLASS",        sub_class);
      data.setFieldItem("ATTACH_TYPE",      attach_type);
      data.setFieldItem("MEETING_TYPE",     meeting_type);
      data.setFieldItem("MAIN_SEND",        main_send);
      data.setFieldItem("CO_SEND",          co_send);
      data.setFieldItem("INNER_SEND",       inner_send);
      data.setFieldItem("SEC_LEVEL",        sec_level);
      data.setFieldItem("PAGE_SIZE",        page_size);
      data.setFieldItem("BOOKLET_NO",       booklet_no);
      data.setFieldItem("SPECIALTY_NO",     specialty_no);
      data.setFieldItem("COMPONENT_TYPE",   component_type);
      data.setFieldItem("LOT_NO",           lot_no);
      data.setFieldItem("SEND_DEPT",        send_dept);
      data.setFieldItem("ROOM_NO",          room_no);
      data.setFieldItem("GRADE_NO",         grade_no);
      data.setFieldItem("PROFESSION_NO",    profession_no);
      data.setFieldItem("EXTAND_DOC_CODE1", extand_doc_code1);
      data.setFieldItem("EXTAND_DOC_CODE2", extand_doc_code2);
      data.setFieldItem("EXTAND_DOC_CODE3", extand_doc_code3);
      // Added end
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
                        ;
                   
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
        // data.setFieldItem("TITLE","TEST...");//TODO ..
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
   
   //Added by Lqw 20131018
   String parentPage = mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE);
   if(!Str.isEmpty(parentPage) && !"null".equals(parentPage)){
//      data.setFieldItem("PARENT_DOC_CLASS", "PRJCSD");
//      data.setFieldItem("PARENT_DOC_NO", "1000146");
//      data.setFieldItem("PARENT_DOC_SHEET", "1");
//      data.setFieldItem("PARENT_DOC_REV", "A1");
      data.setFieldItem("PARENT_DOC_CLASS", mgr.readValue(DocmawConstants.PARENT_DOC_CLASS));
      data.setFieldItem("PARENT_DOC_NO", mgr.readValue(DocmawConstants.PARENT_DOC_NO));
      data.setFieldItem("PARENT_DOC_SHEET", mgr.readValue(DocmawConstants.PARENT_DOC_SHEET));
      data.setFieldItem("PARENT_DOC_REV", mgr.readValue(DocmawConstants.PARENT_DOC_REV));
   }
   //Added end.
       
        headset.setRow(data);
        mgr.submit(trans);
        trans.clear(); 
        
         // --modified the doc_issue instance to have the retained language code and the format - start --
         sAttr = "LANGUAGE_CODE" + String.valueOf((char)31) + sLanguageCode + String.valueOf((char)30)
               + "FORMAT_SIZE"   + String.valueOf((char)31) + sFormatSize + String.valueOf((char)30)
               ;

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
         
         //Added by lqw 20131011, do not redirect in case of contractor uploading.
         if(Str.isEmpty(parentPage) && !"null".equals(parentPage) && !bSaveForDocReference){
            mgr.redirectTo(DocmawConstants.getCorrespondingDocIssuePage(getCurrentPageDocClass(), getCurrentPageSubDocClass()) + 
                  "?EDIT_LAYOUT=TRUE" +
                  "&DOC_CLASS=" + headset.getRow().getValue("DOC_CLASS") + 
                  "&DOC_NO=" + headset.getRow().getValue("DOC_NO") +
                  "&DOC_SHEET=" + headset.getRow().getValue("FIRST_SHEET_NO") +
                  "&DOC_REV=" + headset.getRow().getValue("FIRST_REVISION"));
         }
         //Added end.
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
      cmd.setParameter("DOC_CLASS", getCurrentPageDocClass() );
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         cmd.setParameter("SUB_CLASS", subClass);
      }
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

   private String haveEdmFile(String doc_class, String doc_no, String doc_sheet, String doc_rev)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomFunction("HAVEEDMFILE", "Edm_File_API.Have_Edm_File", "DUMMY1");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("FIRST_SHEET_NO", doc_sheet);
      cmd.addParameter("FIRST_REVISION", doc_rev);
      trans = mgr.perform(trans);
      return trans.getValue("HAVEEDMFILE/DATA/DUMMY1");
   }
   
   private String findEdmFileNoState(String doc_class, String doc_no, String doc_sheet, String doc_rev, String edm_state)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("FINDFILESTATE", "Edm_File_API.Find_Edm_File_No_State", "DUMMY1");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("FIRST_SHEET_NO", doc_sheet);
      cmd.addParameter("FIRST_REVISION", doc_rev);
      cmd.addParameter("DUMMY2", "ORIGINAL");
      cmd.addParameter("DUMMY2", edm_state);
      trans = mgr.perform(trans);
      return trans.getValue("FINDFILESTATE/DATA/DUMMY1");
   }
   
   public void deleteSelectDocFile()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      String action = "DELETESEL";
      String doc_type = "ORIGINAL";
      int noOfRowsSelected = 1;

      if ("Y".equals(headset.getRow().getValue("ISSUEEDITACCESS")))
      {
         if (headlay.isMultirowLayout())
         {
            headset.storeSelections();
            headset.setFilterOn();
            noOfRowsSelected = headset.countRows();
         }
         else
         {
            headset.selectRow();
         }

         boolean error = false;
         for (int count = 0; count < noOfRowsSelected; count++)
         {
            String doc_class = headset.getRow().getValue("DOC_CLASS");
            String doc_no = headset.getRow().getValue("DOC_NO");
            String doc_sheet = headset.getRow().getValue("FIRST_SHEET_NO");
            String doc_rev = headset.getRow().getValue("FIRST_REVISION");

            if ("FALSE".equals(haveEdmFile(doc_class, doc_no, doc_sheet, doc_rev)))
            {
               if (headlay.isMultirowLayout())
                  mgr.showAlert(mgr.translate("DOCMAWDOCISSUEMULTINOTOBSOLETE: You are attempting to delete file(s) from one or more documents that have no file(s) checked in."));
               else
                  mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOTOBSOLETE: You are attempting to delete file(s) from a document that has no file(s) checked in."));
               error = true;
               break;
            }

            if ("TRUE".equals(findEdmFileNoState(doc_class, doc_no, doc_sheet, doc_rev, "Checked In")))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUEALLFILESMUSTBECHECKEDIN: All document file(s) must be checked in before deleting."));
               error = true;
               break;
            }
         }

         if (error)
         {
            if (headlay.isMultirowLayout())
            {
               headset.setFilterOff();
            }
            return;
         }

         buff.addItem("DOC_TYPE", doc_type);
         buff.addItem("FILE_ACTION", action);
         buff.addItem("SAME_ACTION_TO_ALL", "YES");
         transferToEdmMacro(buff);
         if (headlay.isMultirowLayout())
         {
            headset.setFilterOff();
         }
      }
      else
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILEMULTI: You don't have the necessary access rights to selected documents in order to delete their files"));
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILE: You don't have the necessary access rights to the document in order to delete its file(s)"));
      }
   }
   
   public void deleteDocument()
   {
      ASPManager mgr = getASPManager();
      int noOFRowsSelected = 1;
      int count = 0;
      
      // Bug 57779, End
      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         noOFRowsSelected = headset.countRows();
      }
      else
      {
         headset.selectRow();
      }
      
      // Set all exchange documents to Obsolete
      setObsolete();
      
      ASPBuffer buff = mgr.newASPBuffer();
      buff.addItem("DOC_TYPE", "ORIGINAL");
      buff.addItem("FILE_ACTION", "DELETEALL");
      buff.addItem("SAME_ACTION_TO_ALL", "YES");
      transferToEdmMacro(buff);
      headset.setFilterOff();
   }
   
   public void setObsolete()
   {
      ASPManager mgr = getASPManager();
      int curr_row = 0;
      
      curr_row = headset.getCurrentRowNo();
      
      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         if (headset.countSelectedRows() == 0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
            return;
         }
      }
      else
      {
         headset.selectRow();
      }
      
      trans.clear();
      
      boolean perform = false;
      ASPBuffer sel_rows = headset.getSelectedRows("DOC_CLASS,DOC_NO,FIRST_SHEET_NO,FIRST_REVISION,ISSUEEDITACCESS");
      ASPCommand cmd;
      for (int i = 0; i < sel_rows.countItems(); i++)
      {
         if ("Y".equals(sel_rows.getBufferAt(i).getValueAt(4)))
         {
            cmd = trans.addCustomCommand("SETOBSOLETE" + i, "DOC_ISSUE_API.Promote_To_Obsolete_");
            cmd.addParameter("DOC_CLASS", sel_rows.getBufferAt(i).getValueAt(0));
            cmd.addParameter("DOC_NO", sel_rows.getBufferAt(i).getValueAt(1));
            cmd.addParameter("FIRST_SHEET_NO", sel_rows.getBufferAt(i).getValueAt(2));
            cmd.addParameter("FIRST_REVISION", sel_rows.getBufferAt(i).getValueAt(3));
            perform = true;
         }
      }
      if (perform)
      {
         trans = mgr.perform(trans);
         headset.goTo(curr_row);
         headset.refreshAllRows();
      }
   }
   
   // Added by Terry 20121019
   // Download documents function
   public void downloadDocuments()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      if (checkAccessExt(true, false))
      {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "DOWNLOAD");
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("SAME_ACTION_TO_ALL", "YES");
         buff.addItem("LAUNCH_FILE", "NO");
         transferToEdmMacro(buff);
      }
      else
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENODOWNACCESSDOWNMULTI: You don't have download access to one or more of the selected documents.");
         else
            mgr.showAlert("DOCMAWDOCISSUENODOWNACCESSDOWN: You must have download access of this document.");
      }
   }

   public boolean checkAccessExt(boolean check_download, boolean check_print)
   {
      if (!check_download && !check_print)
         return true;

      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      // headset.store();
      String user_id = mgr.getFndUser();
      ASPBuffer sel_buf = headset.getSelectedRows("DOC_CLASS,DOC_NO,FIRST_SHEET_NO,FIRST_REVISION");
      int countRows = sel_buf.countItems();
      if (countRows > 0)
      {
         for (int i = 0; i < countRows; i++) {
            ASPBuffer subBuff = sel_buf.getBufferAt(i);
            String doc_class = subBuff.getValueAt(0);
            String doc_no = subBuff.getValueAt(1);
            String doc_sheet = subBuff.getValueAt(2);
            String doc_rev = subBuff.getValueAt(3);
            ASPCommand cmd = trans.addCustomCommand("USERGETACCEXT" + i, "Document_Issue_Access_API.User_Get_Access_Ext");
            cmd.addParameter("DOC_CLASS", doc_class);
            cmd.addParameter("DOC_NO", doc_no);
            cmd.addParameter("FIRST_SHEET_NO", doc_sheet);
            cmd.addParameter("FIRST_REVISION", doc_rev);
            cmd.addParameter("ATTR", user_id);
            cmd.addParameter("DUMMY1");
            cmd.addParameter("DUMMY2");
         }
         trans = mgr.perform(trans);
         for (int i = 0; i < countRows; i++) {
            if (check_download) {
               String download_access = (mgr.isEmpty(trans.getValue("USERGETACCEXT" + i + "/DATA/DUMMY1")) ? "FALSE" : trans.getValue("USERGETACCEXT" + i + "/DATA/DUMMY1"));
               if ("FALSE".equals(download_access)) {
                  return false;
               }
            }

            if (check_print) {
               String print_access = (mgr.isEmpty(trans.getValue("USERGETACCEXT" + i + "/DATA/DUMMY2")) ? "FALSE" : trans.getValue("USERGETACCEXT" + i + "/DATA/DUMMY2"));
               if ("FALSE".equals(print_access)) {
                  return false;
               }
            }
         }
         return true;
      }
      return false;
   }
   
   public void transferToEdmMacro(ASPBuffer buff)
   {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout())
      {
         headset.unselectRows();
         headset.selectRow();
      }
      else
         headset.selectRows();

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,FIRST_SHEET_NO,FIRST_REVISION");
      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buff, data);
      bTranferToEDM = true;
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
         mgr.transferDataTo(DocmawConstants.getCorrespondingDocIssuePage(getCurrentPageDocClass(), getCurrentPageSubDocClass()),keys);
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
      
      
      customizeHeadblk();
      
      //
      // Hidden Fields
      //
      
      
      headblk.addField("IS_ELE_DOC").
      // setFunction("EDM_FILE_API.Have_Edm_File(:DOC_CLASS,:DOC_NO,:FIRST_SHEET_NO,:FIRST_REVISION)").
      setReadOnly().
      setHidden().
      setLabel("DOCTITLEISELEDOC: Is Ele Doc").
      setSize(5);
      
      
      headblk.addField("PARENT_DOC_CLASS").setInsertable().setHidden();
      headblk.addField("PARENT_DOC_NO").setInsertable().setHidden();
      headblk.addField("PARENT_DOC_SHEET").setInsertable().setHidden();
      headblk.addField("PARENT_DOC_REV").setInsertable().setHidden();
      
      customizeFieldFromDocClass();
      
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
      setSelectBox().
      enumerateValues("Doc_Reference_Object_Req_API").
      setLabel("DOCMAWDOCTITLEOVWOBJCONNREQ: Object Connection").
      setHidden();

      headblk.addField("MAKE_WASTE_REQ").
      setSize(20).
      setSelectBox().
      enumerateValues("Doc_Make_Waste_Req_API").
      setLabel("DOCMAWDOCTITLEOVWMAKEWASTEREQ: Destroy").
      setHidden();
      
      
      headblk.addField("VIEW_FILE_REQ").
      setSize(20).
      setSelectBox().
      enumerateValues("Doc_View_Copy_Req_API").
      setLabel("DOCMAWDOCTITLEOVWVIEWFILEREQ: View Copy").
      setHidden();
      
      headblk.addField("SAFETY_COPY_REQ").
      setSize(20).
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
      setLabel("DOCMAWDOCTITLEOVWDOCUMENTREVISION: Document Revision");
      
      
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

      customizeHeadView();
      
      headblk.defineCommand("DOC_TITLE_API","New__,Modify__,Remove__");
      headblk.enableFuncFieldsNonSelect();

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
      headbar.addSecureCustomCommand("deleteDocument", mgr.translate("DOCMAWDOCISSUEDELETEDOC: Delete Document Revision"), "DOC_ISSUE_API.Remove__");
      headbar.addCommandValidConditions("deleteDocument", "DOC_CLASS", "Enable", 
            DocmawConstants.EXCH_RECEIVE + ";" +
            DocmawConstants.EXCH_SEND + ";" +
            DocmawConstants.EXCH_PROPHASE + ";" +
            DocmawConstants.EXCH_CONSTRUCT + ";" +
            DocmawConstants.EXCH_DESIGN + ";" +
            DocmawConstants.EXCH_EQUIPMENT + ";" +
            DocmawConstants.EXCH_TEST + ";" +
            DocmawConstants.EXCH_COMPLETION + ";" +
            DocmawConstants.EXCH_STANDARD);
      headbar.addSecureCustomCommand("deleteSelectDocFile", mgr.translate("DOCMAWDOCISSUEDELETESELECTDOCFILE: Delete Selected Document File"), "EDM_FILE_API.Remove__");
      headbar.addCommandValidConditions("deleteSelectDocFile", "DOC_CLASS", "Enable", 
            DocmawConstants.EXCH_RECEIVE + ";" +
            DocmawConstants.EXCH_SEND + ";" +
            DocmawConstants.EXCH_PROPHASE + ";" +
            DocmawConstants.EXCH_CONSTRUCT + ";" +
            DocmawConstants.EXCH_DESIGN + ";" +
            DocmawConstants.EXCH_EQUIPMENT + ";" +
            DocmawConstants.EXCH_TEST + ";" +
            DocmawConstants.EXCH_COMPLETION + ";" +
            DocmawConstants.EXCH_STANDARD);
      headbar.removeFromMultirowAction("deleteSelectDocFile");
      headbar.addSecureCustomCommand("downloadDocuments", mgr.translate("DOCMAWDOCISSUEDOWNLOADDOC: Download Documents"), "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");
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

      headbar.disableCustomCommand("queryDocContents");
      headbar.disableCustomCommand("changereportArchiveSettings");
      headbar.disableCustomCommand("copyTitle");
      headbar.disableCustomCommand("setStructAttribAll");
      headbar.disableCustomCommand("unsetStructAttribAll");
      
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      headlay.setSimple("RECEIVE_UNIT_NAME");
      headlay.setSimple("SEND_UNIT_NAME");
      headlay.setSimple("PURPOSE_NAME");
      headlay.setSimple("PROJ_NAME");
      headlay.setSimple("MACH_GRP_DESC");
      headlay.setSimple("SIGN_PERSON_NAME");
      headlay.setSimple("USER_CREATED_NAME");
      headlay.setSimple("SUB_CLASS_NAME");
      headlay.setSimple("ATTACH_TYPE_NAME");
      headlay.setSimple("MEETING_TYPE_NAME");
      headlay.setSimple("MAIN_SEND_NAME");
      headlay.setSimple("CO_SEND_NAME");
      headlay.setSimple("INNER_SEND_NAME");
      headlay.setSimple("LEVEL_NAME");
      headlay.setSimple("SIZE_NAME");
      headlay.setSimple("BOOKLET_NAME");
      headlay.setSimple("SPECIALTY_DESC");
      headlay.setSimple("COMPONENT_TYPE_DESC");
      headlay.setSimple("PROFESSION_NAME");
      headlay.setSimple("LOT_DESC");
      headlay.setSimple("SEND_DEPT_DESC");
      headlay.setSimple("ROOM_DESC");
      headlay.setSimple("GRADE_DESC");
     
      customizeHeadlay();
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCTITLEOVWOVEDOCTI: Overview - Document Titles"));
      headtbl.enableRowSelect();

   }


   public void  adjust() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      try{
         ASPField[] fields = headblk.getFields();
         ASPField tempField = null;
         for (int i = 0; i < fields.length; i++) {
            tempField = fields[i];
            tempField.setHidden();
         }
      }catch(Throwable th){
      }
      

      if ("HEAD.NewRow".equals(mgr.readValue("__COMMAND")))
         mgr.getASPField("DOC_NO").deactivateLOV();
      
      if(headblk.getFuncFieldsNonSelect()){
         this.setFuncFieldValue(headblk);
      }
      
      
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
         headbar.disableCommand("downloadDocuments");
      }
      else
      {
         headbar.enableMultirowAction();
      }
      
      
      String parentPage = mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE);
      String pageEditable = mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE_EDITABLE);
      if(!Str.isEmpty(parentPage) && !"null".equals(parentPage) && !Str.isEmpty(pageEditable) && !"null".equals(pageEditable)){
         headbar.disableCommand(ASPCommandBar.NEWROW);
         headbar.disableCommand(ASPCommandBar.DELETE);
         headbar.disableCommand(ASPCommandBar.EDITROW);
         headbar.disableCommand(ASPCommandBar.EDIT);
         headbar.disableCommand(ASPCommandBar.DUPLICATE);
         headbar.disableCommand(ASPCommandBar.DUPLICATEROW);
         
         headbar.disableCustomCommand("deleteDocument");
         headbar.disableCustomCommand("deleteSelectDocFile");
      }
      
      if(!Str.isEmpty(parentPage) && !"null".equals(parentPage))
      {
         headbar.disableCustomCommand("transferToDocInfo");
         String parentDocClass = mgr.readValue(DocmawConstants.PARENT_DOC_CLASS);
         if (DocmawConstants.EXCH_RECEIVE.equals(parentDocClass))
         {
            headbar.disableCustomCommand("deleteDocument");
            headbar.disableCustomCommand("deleteSelectDocFile");
         }
      }
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
            
            String parentPage = mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE);
            String pageEditable = mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE_EDITABLE);
            if(!Str.isEmpty(parentPage) && !"null".equals(parentPage) && !Str.isEmpty(pageEditable) && !"null".equals(pageEditable)){
               return "";
            }
            
            imgSrc += "document_upload.gif";
            return "<img src=\""+imgSrc+"\" height=\"16\" width=\"16\" border=\"0\">";
         }
      }
      return "";
   }



   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return getTitle();
   }

   protected abstract String getTitle();

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(headlay.show());
      appendToHTML("<input type=hidden name=\"GO_TO_COPY_TITLE\" value=\"\">\n");
      
      appendToHTML("<input type=\"hidden\" name=\"REFRESH_PARENT\" value=\"FALSE\">\n");
      
      //Added by lqw 20131015
      String parentPage = mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE);
      if(!Str.isEmpty(parentPage) && !"null".equals(parentPage)){
         appendToHTML("<input type=hidden name=\""+DocmawConstants.INTER_PAGE_PARENT_PAGE+"\" value=\""+mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE)+"\">\n");
         appendToHTML("<input type=hidden name=\""+DocmawConstants.PARENT_DOC_CLASS+"\" value=\""+mgr.readValue(DocmawConstants.PARENT_DOC_CLASS)+"\">\n");
         appendToHTML("<input type=hidden name=\""+DocmawConstants.PARENT_DOC_NO+"\" value=\""+mgr.readValue(DocmawConstants.PARENT_DOC_NO)+"\">\n");
         appendToHTML("<input type=hidden name=\""+DocmawConstants.PARENT_DOC_SHEET+"\" value=\""+mgr.readValue(DocmawConstants.PARENT_DOC_SHEET)+"\">\n");
         appendToHTML("<input type=hidden name=\""+DocmawConstants.PARENT_DOC_REV+"\" value=\""+mgr.readValue(DocmawConstants.PARENT_DOC_REV)+"\">\n");
         appendToHTML("<input type=hidden name=\""+DocmawConstants.INTER_PAGE_PARENT_PAGE_EDITABLE+"\" value=\""+mgr.readValue(DocmawConstants.INTER_PAGE_PARENT_PAGE_EDITABLE)+"\">\n");
      }
      //Added end.
      
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
      
      if (bTranferToEDM)
      {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }
      
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
      
      if(headlay.isNewLayout() || headlay.isEditLayout()){
         appendDirtyJavaScript("function checkMandatory_(field,label,msg)  \n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("  if( field.type.substr(0,6)=='select' )  \n");
         appendDirtyJavaScript("  {  \n");
         appendDirtyJavaScript("    if( field.options[field.selectedIndex].value=='' )\n");
         appendDirtyJavaScript("      return missingValueError_(field,label,msg);  \n");
         appendDirtyJavaScript("  }  \n");
         appendDirtyJavaScript("  else if( field.type.substr(0,6)=='hidden' )  \n");
         appendDirtyJavaScript("  {  \n");
         appendDirtyJavaScript("    return true; \n");
         appendDirtyJavaScript("  } \n");
         appendDirtyJavaScript("  else  \n");
         appendDirtyJavaScript("  {  \n");
         appendDirtyJavaScript("    if( field.value == '' )  \n");
         appendDirtyJavaScript("      return missingValueError_(field,label,msg);  \n");
         appendDirtyJavaScript("  }  \n");
         appendDirtyJavaScript("  return true;  \n");
         appendDirtyJavaScript("}\n");
      }
      
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");
   }
   
   protected abstract void lastMethod();
}
