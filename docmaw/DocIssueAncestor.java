package ifs.docmaw;

import ifs.docmaw.edm.DocmawFtp;
import ifs.docmaw.edm.DocumentTransferHandler;
import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPField;
import ifs.fnd.asp.ASPHTMLFormatter;
import ifs.fnd.asp.ASPLog;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPopup;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTabContainer;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.buffer.Buffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.fnd.util.Str;
import ifs.fultxw.engine.impl.FullTextServiceImpl;
import ifs.genbaw.GenbawConstants;
import ifs.hzwflw.util.URL;

import java.util.ArrayList;
import java.util.StringTokenizer;

public abstract class DocIssueAncestor extends DocSrv {

   // ===============================================================
   // Static constants
   // ===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocIssue");

   // ===============================================================
   // Instances created on page creation (immutable attributes)
   // ===============================================================
   protected ASPContext ctx;
   protected ASPHTMLFormatter fmt;

   protected ASPBlock headblk;
   protected ASPRowSet headset;
   protected ASPCommandBar headbar;
   protected ASPTable headtbl;
   protected ASPBlockLayout headlay;

   protected ASPBlock itemblk2;
   protected ASPRowSet itemset2;
   protected ASPCommandBar itembar2;
   protected ASPTable itemtbl2;
   protected ASPBlockLayout itemlay2;

   protected ASPBlock itemblk3;
   protected ASPRowSet itemset3;
   protected ASPCommandBar itembar3;
   protected ASPTable itemtbl3;
   protected ASPBlockLayout itemlay3;

   protected ASPBlock itemblk4;
   protected ASPRowSet itemset4;
   protected ASPCommandBar itembar4;
   protected ASPTable itemtbl4;
   protected ASPBlockLayout itemlay4;

   protected ASPBlock itemblk6;
   protected ASPRowSet itemset6;
   protected ASPCommandBar itembar6;
   protected ASPTable itemtbl6;
   protected ASPBlockLayout itemlay6;

   protected ASPBlock itemblk7;
   protected ASPRowSet itemset7;
   protected ASPCommandBar itembar7;
   protected ASPTable itemtbl7;
   protected ASPBlockLayout itemlay7;

   protected ASPBlock itemblk9;
   protected ASPRowSet itemset9;
   protected ASPCommandBar itembar9;
   protected ASPTable itemtbl9;
   protected ASPBlockLayout itemlay9;

   protected ASPBlock itemblk11;
   protected ASPRowSet itemset11;
   protected ASPCommandBar itembar11;
   protected ASPTable itemtbl11;
   protected ASPBlockLayout itemlay11;
   
   
//   protected ASPBlock itemblk12;
//   protected ASPRowSet itemset12;
//   protected ASPCommandBar itembar12;
//   protected ASPTable itemtbl12;
//   protected ASPBlockLayout itemlay12;
   
   protected ASPBlock itemblk13;
   protected ASPRowSet itemset13;
   protected ASPCommandBar itembar13;
   protected ASPTable itemtbl13;
   protected ASPBlockLayout itemlay13;
   
   protected ASPBlock doc_issue_callback_blk;
   protected ASPRowSet doc_issue_callback_set;
   protected ASPCommandBar doc_issue_callback_bar;
   protected ASPTable doc_issue_callback_tbl;
   protected ASPBlockLayout doc_issue_callback_lay;
   
   protected ASPBlock doc_issue_distribution_blk;
   protected ASPRowSet doc_issue_distribution_set;
   protected ASPCommandBar doc_issue_distribution_bar;
   protected ASPTable doc_issue_distribution_tbl;
   protected ASPBlockLayout doc_issue_distribution_lay;
   
   protected ASPBlock dummyblk;

   protected ASPTabContainer tabs;
   private ASPField f;

   private ASPPopup popup_status;
   private ASPPopup popup_file_operations;
   private ASPPopup popup_access;
   private ASPPopup popup_general;

   // ===============================================================
   // Transient temporary variables (never cloned)
   // ===============================================================
   protected ASPTransactionBuffer trans;
   private ASPBuffer keys;
   private ASPBuffer data;
   protected ASPBuffer bc_Buffer;
   protected ASPBuffer trans_Buffer;
   private ASPCommand cmd;
   protected ASPQuery q;

   protected boolean bConfirm;
   private boolean bConfirmEx;
   protected boolean bCopyProfile;
   protected boolean bObjectConnection;
   protected boolean bItem3Duplicate;
   protected boolean bItem5Duplicate;
   protected boolean bItem6Duplicate;
   protected boolean bTranferToEDM;
   protected boolean bTranferToCreateLink;// DMPR303
   protected boolean launchFile;
   protected boolean client_confirmation;
   private boolean bShowTransLov;
   protected boolean showInMulti;
   protected boolean bOpenWizardWindow = false;
   private boolean modifySubWindow4NewRev;
   private boolean bOpenReleaseWizardWindow = false;
   private boolean bShowStructure = false;
   private boolean bConfiramtion4SettingStructureType;
   protected boolean bSetStructure;
   private boolean isProjInstalled = false;

   protected String root_path;
   protected String strIFSCliMgrOCX;
   protected String sMessage;
   protected String sPersonError;
   protected String sHistoryMode;
   protected String confirm_func;
   protected String unconfirm_func;
   protected String searchURL;
   protected String savetype;
   private String sFilePath;
   private String sClientFunction;
   protected String sendingUrl;
   protected String transfered_CurrRow;
   private String sUrl;
   protected String enableHistoryNewButton;
   // Bug 53039, Start
   protected boolean bDoCheckForAllAccess = false;
   // Bug 53039, End
   // Bug 56685, Start
   private boolean bPrintViewCopy = false;
   // Bug 56685, End

   // Docman constants
   private String sCheckedIn;
   private String sCheckedOut;
   protected String sPrelimin;
   protected String sAppInProg;
   protected String sApproved;
   protected String sObsolete;
   protected String sReleased;
   private String dGroup;
   protected String dAll;
   private String dUser;
   protected String trans_id_sent;

   // Bug Id 67105, Start
   protected String sMandatoryFieldsList;
   protected String sMandatoryFields;
   protected boolean bMandatoryFieldsEmpty;
   // Bug Id 67105, End

   // Bug 70808, Start
   private static final int BACKGROUNDJOB_LIMIT = 1000;
   private String objArray[];
   protected boolean bPerformBackgroundJob;
   protected boolean bDialogBoxPopped;
   private boolean bBGJobAlertShown;
   private boolean bConfirmBackgroundJob;
   private boolean bReleaseBGJPossible;
   // Bug 70808, End

   // Bug Id 77080 Start
   protected String sFinishCheckIn;
   protected String sOpeInProg;
   // Bug Id 77080 End

   // Bug Id 81806, start
   private DocumentTransferHandler doc_hdlr;
   // Bug Id 81806, end

   // create index start, Added By lqw 20130827
   private boolean bCreateIndexSucceed = false;
   protected String show_in_navigator;
   
   // create index end

   private final static String TRANSFER_PARAM_NAME = "__TRANSFER";
   
   // ===============================================================
   // Construction
   // ===============================================================
   public DocIssueAncestor(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   // new method intended to be implemented by subclasses .begin..
   protected abstract String getCurrentPageDocClass();

   protected String getCurrentPageSubDocClass(){
      return null;
   }
   
   // new method end.
   protected void customizeHeadblk() {
      ASPManager mgr = getASPManager();
      headblk.addField("REV_TITLE").setInsertable()
            .setLabel("DOCISSUERECEIVEREVTITLE: Rev Title").setSize(100);

      headblk.addField("DOC_CODE").setInsertable()
            .setLabel("DOCISSUERECEIVEDOCCODE: Doc Code").setSize(20);

      headblk.addField("SEND_UNIT_NO").setInsertable()
            .setLabel("DOCISSUERECEIVESENDUNITNO: Send Unit No")
            .setDynamicLOV("GENERAL_ZONE_LOV")
            .setCustomValidation("SEND_UNIT_NO", "SEND_UNIT_NAME").setSize(20);

      headblk.addField("SEND_UNIT_NAME").setInsertable()
            .setLabel("DOCISSUERECEIVESENDUNITNAME: Send Unit Name")
            .setFunction("GENERAL_ZONE_API.GET_ZONE_DESC(:SEND_UNIT_NO)")
            .setReadOnly().setSize(20);

      headblk.addField("MAIN_CONTENT").setInsertable()
            .setLabel("DOCISSUERECEIVEMAINCONTENT: Main Content").setHeight(5)
            .setSize(100);

      headblk.addField("RECEIVE_UNIT_NO").setInsertable()
            .setLabel("DOCISSUERECEIVERECEIVEUNITNO: Receive Unit No")
            .setDynamicLOV("GENERAL_ZONE_LOV").setSize(20);

      headblk.addField("RECEIVE_UNIT_NAME").setInsertable()
            .setLabel("DOCISSUERECEIVERECEIVEUNITNAME: Receive Unit Name")
            .setFunction("GENERAL_ZONE_API.Get_Zone_Desc(:RECEIVE_UNIT_NO)")
            .setReadOnly().setSize(20);

      headblk.addField("PURPOSE_NO").setInsertable()
            .setDynamicLOV("DOC_COMMUNICATION_SEQ")
            .setLabel("DOCISSUERECEIVEPURPOSENO: Purpose No")
            .setCustomValidation("PURPOSE_NO", "PURPOSE_NAME").setSize(20);

      headblk
            .addField("PURPOSE_NAME")
            .setInsertable()
            .setLabel("DOCISSUERECEIVEPURPOSENAME: Purpose Name")
            .setFunction(
                  "DOC_COMMUNICATION_SEQ_API.Get_Purpose_Name(:PURPOSE_NO)")
            .setReadOnly().setSize(20);

      headblk
            .addField("HEAD_VIEW_FILE")
            .setFunction("''")
            .setReadOnly()
            .unsetQueryable()
            .setLabel("DOCMAWDOCISSUEHEADVIEWFILE: View File")
            .setHyperlink(
                  "../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL",
                  "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV", "NEWWIN")
            .setAsImageField();
      // group 1 end

      // group 2 begin
      headblk.addField("INDEX_CREATED").setInsertable()
            .setCheckBox("FALSE,TRUE")
            .setLabel("DOCISSUERECEIVEINDEXCREATED: Index Created").setSize(5);

      headblk.addField("PROJ_NO").setInsertable()
            .setLabel("DOCISSUEPROJNO: Proj No")
            .setCustomValidation("PROJ_NO", "PROJ_NAME")
            .setDynamicLOV("GENERAL_PROJECT").setSize(20);

      headblk.addField("PROJ_NAME")
            .setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC(:PROJ_NO)")
            .setLabel("DOCISSUEPROJNAME: Proj Name").setReadOnly().setSize(20);

      headblk.addField("COPIES", "Number").setInsertable()
            .setLabel("DOCISSUERECEIVECOPIES: Copies").setSize(20);

      headblk.addField("PAGES", "Number").setInsertable()
            .setLabel("DOCISSUERECEIVEPAGES: Pages").setSize(20);

      headblk.addField("RECEIVE_DATE", "Date").setInsertable()
            .setLabel("DOCISSUERECEIVERECEIVEDATE: Receive Date").setSize(20);

      headblk.addField("SEND_DATE", "Date").setInsertable()
            .setLabel("DOCISSUERECEIVESENDDATE: Send Date").setSize(20);

      headblk.addField("EMERGENCY").setInsertable().setCheckBox("FALSE,TRUE")
            .setLabel("DOCISSUERECEIVEEMERGENCY: Emergency").setSize(20);

      headblk.addField("EMERGENCY_DATE", "Date").setInsertable()
            .setLabel("DOCISSUERECEIVEEMERGENCYDATE: Emergency Date")
            .setSize(20);

      headblk.addField("RECEIPT").setInsertable().setCheckBox("FALSE,TRUE")
            .setLabel("DOCISSUERECEIVERECEIPT: Receipt").setSize(20);

      headblk.addField("RECEIPT_REQUEST", "Date").setInsertable()
            .setLabel("DOCISSUERECEIVERECEIPTREQUEST: Receipt Request")
            .setSize(20);
      headblk
      .addField("USER_CREATED")
      .setSize(20)
      .setMaxLength(30)
      .setReadOnly()
      .setDefaultNotVisible()
      .setDynamicLOV("PERSON_INFO_USER")
      .setLOVProperty(
            "TITLE",
            mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id"))
            .setLabel("DOCMAWDOCISSUECREATEDBY: Created By");

      headblk.addField("USER_CREATED_NAME").setFunction("Fnd_User_Api.Get_Description(:USER_CREATED)").setReadOnly().setSize(20);

      headblk.addField("DT_CRE", "Date").setSize(20).setReadOnly()
            .setDefaultNotVisible()
            .setLabel("DOCMAWDOCISSUEDATECREATED: Date Created");

      headblk.addField("STATE").setSize(20).setMaxLength(253).setSelectBox()
            .setReadOnly().enumerateValues("DOC_STATE_API")
            .unsetSearchOnDbColumn().setLabel("DOCMAWDOCISSUESTATE: Status");

      // group 2 end
      headblk.addField("SIGN_STATUS_DB").setHidden();

      headblk.addField("RESPONSE_CODE").setInsertable()
            .setLabel("DOCISSUERECEIVERESPONSECODE: Response Code").setSize(20);

      headblk.addField("COMPLETE_DATE", "Date").setInsertable()
            .setLabel("DOCISSUERECEIVECOMPLETEDATE: Complete Date").setSize(20);

      headblk.addField("INNER_DOC_CODE").setInsertable()
            .setLabel("DOCISSUERECEIVEINNERDOCCODE: Inner Doc Code")
            .setSize(20);

      headblk.addField("AS_BUILT_DRAWING").setInsertable()
            .setLabel("DOCISSUERECEIVEASBUILTDRAWING: As Built Drawing")
            .setSize(20);

      headblk.addField("TRANSFER_NO").setInsertable()
            .setLabel("DOCISSUERECEIVETRANSFERNO: Transfer No").setSize(20);

      headblk.addField("DOC_STATE").enumerateValues("Doc_Issue_State_API")
            .setSelectBox().setInsertable()
            .setLabel("DOCISSUERECEIVEDOCSTATE: Doc State").setSize(20);

      headblk.addField("SIGN_PERSON").
      setInsertable().
      setLabel("DOCISSUERECEIVESIGNPERSON: Sign Person").
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id")).
      setSize(20);
      
      headblk.addField("SIGN_PERSON_NAME").
      setFunction("Person_Info_Api.Get_Name(:SIGN_PERSON)").
      setReadOnly().
      setSize(20);
      mgr.getASPField("SIGN_PERSON").setValidation("SIGN_PERSON_NAME");

      headblk.addField("MACH_GRP_NO").
      setInsertable().
      setLabel("DOCISSUERECEIVEMACHGRPNO: Mach Grp No").
      setDynamicLOV("GENERAL_MACH_GROUP").
      setSize(20); 
      headblk.addField("MACH_GRP_DESC").
      setFunction("General_Mach_Group_Api.Get_Mach_Grp_Desc(:MACH_GRP_NO)").
      setReadOnly().
      setSize(20); 
      mgr.getASPField("MACH_GRP_NO").setValidation("MACH_GRP_DESC");

      headblk.addField("DOC_REV").
      setSize(20).
      setMaxLength(6).
      setReadOnly().
      setUpperCase().
      setLabel("DOCMAWDOCISSUEDOCREV: Doc Rev");

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
      
      headblk.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setReadOnly().
      setUpperCase().
      setHidden().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUEHEADDOCCLASS: Doc Class");

      headblk.addField("DOC_CLASS_NAME").
      setSize(20).
      setReadOnly().
      setHidden().
      setDefaultNotVisible().
      setFunction("DOC_CLASS_API.GET_NAME(:DOC_CLASS)").
      setLabel("DOCMAWDOCISSUEHEADDOCCLASSNAME: Doc Class Desc");
      mgr.getASPField("DOC_CLASS").setValidation("DOC_CLASS_NAME");

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
      
      headblk.addField("FOR_USE_NOTICE").
      setInsertable().
      setLabel("DOCISSUEFORUSENOTICE: For Use Notice").
      setDynamicLOV("DOC_ISSUE_LOV2").
      setLOVProperty("WHERE", "DOC_CLASS = '" + DocmawConstants.PROJ_RECEIVE + "' AND PURPOSE_NO LIKE '18%'").
      setSize(20);
      
      headblk.addField("FOR_USE").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCISSUEFORUSE: For Use").
      setSize(5);
      
      headblk.addField("FOR_USE_DATE", "Date").
      setInsertable().
      setLabel("DOCISSUEFORUSEDATE: For Use Date").
      setSize(20);
   }

   protected abstract void customizeHeadlay();

   protected void customizeFieldFromDocClass() {
      headblk.addField("FROM_DOC_CLASS").setInsertable()
            .setLabel("DOCTITLEFROMDOCCLASS: From Doc Class").setHidden()
            .setSize(12);
   }

   /*protected void customizeTabs() {
      ASPManager mgr = getASPManager();
      if( DocmawConstants.EXCH_SEND.equals(getCurrentPageDocClass()) ||DocmawConstants.EXCH_RECEIVE.equals(getCurrentPageDocClass())){
         tabs.addTab(mgr.translate("DOCMAWDOCISSUETARGETORG: Target Org"), "javascript:commandSet('HEAD.activateSendDept','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEATTACHMENTUPLOAD: Attach Upload"), "javascript:commandSet('HEAD.activateAttachmentUpload','')");
      }else if( DocmawConstants.PROJ_SEND.equals(getCurrentPageDocClass())){
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEOBJECTS: Objects"), "javascript:commandSet('HEAD.activateObjects','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUECONSISTS: Consists Of"), "javascript:commandSet('HEAD.activateConsistsOf','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEWHEREUSED: Where Used"), "javascript:commandSet('HEAD.activateWhereUsed','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEACCESS: Access"), "javascript:commandSet('HEAD.activateAccess','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEHISTORY: History"), "javascript:commandSet('HEAD.activateHistory','')");
         tabs.addTab("FILE_REF", mgr.translate("DOCMAWDOCISSUEFILEREFERENCES: File Refs."), "javascript:commandSet('HEAD.activateFileReferences','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUETARGETORG: Target Org"), "javascript:commandSet('HEAD.activateSendDept','')");
      }else if( DocmawConstants.PROJ_DESIGN.equals(getCurrentPageDocClass()) || DocmawConstants.PROJ_EQUIPMENT.equals(getCurrentPageDocClass())){
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEOBJECTS: Objects"), "javascript:commandSet('HEAD.activateObjects','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUECONSISTS: Consists Of"), "javascript:commandSet('HEAD.activateConsistsOf','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEWHEREUSED: Where Used"), "javascript:commandSet('HEAD.activateWhereUsed','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEACCESS: Access"), "javascript:commandSet('HEAD.activateAccess','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEHISTORY: History"), "javascript:commandSet('HEAD.activateHistory','')");
         tabs.addTab("FILE_REF", mgr.translate("DOCMAWDOCISSUEFILEREFERENCES: File Refs."), "javascript:commandSet('HEAD.activateFileReferences','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEHISTORYREVISION: History Revison"), "javascript:commandSet('HEAD.activateHistoryRevision','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUECALLBACK: Callback"), "javascript:commandSet('HEAD.activateCallback','')");
      }else{
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEOBJECTS: Objects"), "javascript:commandSet('HEAD.activateObjects','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUECONSISTS: Consists Of"), "javascript:commandSet('HEAD.activateConsistsOf','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEWHEREUSED: Where Used"), "javascript:commandSet('HEAD.activateWhereUsed','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEACCESS: Access"), "javascript:commandSet('HEAD.activateAccess','')");
         tabs.addTab(mgr.translate("DOCMAWDOCISSUEHISTORY: History"), "javascript:commandSet('HEAD.activateHistory','')");
         tabs.addTab("FILE_REF", mgr.translate("DOCMAWDOCISSUEFILEREFERENCES: File Refs."), "javascript:commandSet('HEAD.activateFileReferences','')");
      }
   }*/

   protected void customizeOut(AutoString out) {
   }
   
   protected void showAttachUpload(AutoString out, String attachTypeArg){
      if(headlay.isSingleLayout()){
         String docClass = headset.getValue("DOC_CLASS");
         String docNo = headset.getValue("DOC_NO");
         String docSheet = headset.getValue("DOC_SHEET");
         String docRev = headset.getValue("DOC_REV"); 
         String attachType = Str.isEmpty(attachTypeArg) ? headset.getValue("ATTACH_TYPE") : attachTypeArg;
         String signStatus = headset.getValue("SIGN_STATUS_DB");
         
         String basicUrl = null;
         if(DocmawConstants.EXCH_DESIGN.equals(attachType)){
            basicUrl = DocmawConstants.EXCH_DESIGN_TITLE_TAB_PAGE;
         }else if(DocmawConstants.EXCH_EQUIPMENT.equals(attachType)){
            basicUrl = DocmawConstants.EXCH_EQUIPMENT_TITLE_TAB_PAGE;
         }else if(DocmawConstants.EXCH_CONSTRUCT.equals(attachType)){
            basicUrl = DocmawConstants.EXCH_CONSTRUCT_TITLE_TAB_PAGE;
         }else if(DocmawConstants.EXCH_PROPHASE.equals(attachType)){
            basicUrl = DocmawConstants.EXCH_PROPHASE_TITLE_TAB_PAGE;
         }else if(DocmawConstants.EXCH_STANDARD.equals(attachType)){
            basicUrl = DocmawConstants.EXCH_STANDARD_TITLE_TAB_PAGE;
         }else if(DocmawConstants.EXCH_TEMP.equals(attachType)){
            basicUrl = DocmawConstants.EXCH_TEMP_TITLE_TAB_PAGE;
         }else{
            String tip = getASPManager().translate("CANNOTDETERMINEWHICHKINDOFATTACHMENTOADD: Can not determine what kind of attachment(s) to add/show.");
            out.append("<FONT class=normalTextValue> &nbsp;&nbsp;&nbsp;" + tip + "</FONT>");
            return;
         }
         
         URL iframeUrl = new URL(basicUrl);
         iframeUrl.addParameters(DocmawConstants.INTER_PAGE_PARENT_PAGE, "DocIssueContractorSend");
         iframeUrl.addParameters(DocmawConstants.PARENT_DOC_CLASS, docClass);
         iframeUrl.addParameters(DocmawConstants.PARENT_DOC_NO, docNo);
         iframeUrl.addParameters(DocmawConstants.PARENT_DOC_SHEET, docSheet);
         iframeUrl.addParameters(DocmawConstants.PARENT_DOC_REV, docRev);
         
         if(this instanceof DocIssueReceiveAncestor || "SENDED".equals(signStatus)|| "PARTIALLYSIGNED".equals(signStatus)|| "SIGNED".equals(signStatus)){
            iframeUrl.addParameters(DocmawConstants.INTER_PAGE_PARENT_PAGE_EDITABLE, "TRUE");
         }
         out.append("<iframe src=\"" + iframeUrl.toString() + "\" scrolling=\"no\" width=\"100%\" height=\"100%\" frameborder=\"0\" style=\"margin: 0px 0px 0px 0px\"></iframe>");
      }
   }

   protected void showAttachUpload(AutoString out){
      showAttachUpload(out, null);
   }

   public void run() throws FndException {
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      fmt = mgr.newASPHTMLFormatter();

      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      bConfirm = false;
      bCopyProfile = false;
      bObjectConnection = false;
      bItem3Duplicate = false;
      bItem5Duplicate = false;
      bItem6Duplicate = false;
      bTranferToEDM = false;
      bTranferToCreateLink = false;// DMPR303

      launchFile = false;
      strIFSCliMgrOCX = "";
      bOpenWizardWindow = false;
      bMandatoryFieldsEmpty = false;// Bug Id 67105

      sFinishCheckIn = "FinishCheckIn";// Bug Id 77080
      sOpeInProg = "Operation In Progress";// Bug Id 77080

      sMessage = "";
      sPersonError = mgr
            .translate("DOCMAWDOCISSUEPERSON_ERROR: Both group id and person id can not have a value.");

      savetype = ctx.readValue("SAVETYPE", "");
      sHistoryMode = ctx.readValue("HISTMODE", "7");
      sendingUrl = ctx.readValue("SEND_URL", "");
      transfered_CurrRow = ctx.readValue("TRANSFERED_CURRROW", "");
      trans_id_sent = ctx.readValue("TRANS_ID_SENT", "");

      showInMulti = ctx.readFlag("SHOWINMULTI", false);

      bc_Buffer = ctx.readBuffer("BC_BUFF");
      trans_Buffer = ctx.readBuffer("TRANS_BUFFER");
      show_in_navigator = ctx.readValue("SHOW_IN_NAVIGATOR");

      initializeSession();
      getMandatoryFieldsForQuery();// Bug Id 67105
      trans.clear();
      if (mgr.commandBarActivated()) {
         String comnd = mgr.readValue("__COMMAND");

         if ("HEAD.activateObjects".equals(comnd)
               || "HEAD.activateConsistsOf".equals(comnd)
               || "HEAD.activateWhereUsed".equals(comnd)
               || "HEAD.activateAccess".equals(comnd)
               || "HEAD.activateHistory".equals(comnd)
               || "HEAD.activateFileReferences".equals(comnd)
               || "HEAD.activateSendDept".equals(comnd))

         {
            if (itemset2.countRows() > 0
                  && "New__".equals(itemset2.getRowStatus())) {
               itemset2.changeRow();
               itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
            }
            if (itemset3.countRows() > 0
                  && "New__".equals(itemset3.getRowStatus())) {
               itemset3.changeRow();
               itemlay3.setLayoutMode(itemlay3.MULTIROW_LAYOUT);
            }
            if (itemset4.countRows() > 0
                  && "New__".equals(itemset4.getRowStatus())) {
               itemset4.changeRow();
               itemlay4.setLayoutMode(itemlay4.MULTIROW_LAYOUT);
            }
            if (itemset6.countRows() > 0
                  && "New__".equals(itemset6.getRowStatus())) {
               itemset6.changeRow();
               itemlay6.setLayoutMode(itemlay6.MULTIROW_LAYOUT);
            }
            if (itemset7.countRows() > 0
                  && "New__".equals(itemset7.getRowStatus())) {
               itemset7.changeRow();
               itemlay7.setLayoutMode(itemlay7.MULTIROW_LAYOUT);
            }
            if (itemset9.countRows() > 0
                  && "New__".equals(itemset9.getRowStatus())) {
               itemset9.changeRow();
               itemlay9.setLayoutMode(itemlay9.MULTIROW_LAYOUT);
            }
            if (itemset11.countRows() > 0
                  && "New__".equals(itemset11.getRowStatus())) {
               itemset11.changeRow();
               itemlay11.setLayoutMode(itemlay11.MULTIROW_LAYOUT);
            }
         } else if ("ITEM3.DuplicateRow".equals(comnd))
            bItem3Duplicate = true;
         if ("ITEM5.DuplicateRow".equals(comnd))
            bItem5Duplicate = true;
         if ("ITEM6.DuplicateRow".equals(comnd))
            bItem6Duplicate = true;
         eval(mgr.commandBarFunction());
      } else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();// pls do not insert any condition here since validate
                    // method migh not be called under some condition: bakalk
      else if (!mgr.isEmpty(mgr.readValue("NODE_ADDRESS")))
      {
         show_in_navigator = "TRUE";
         runQuery();
      } else if (mgr.commandLinkActivated())
         eval(mgr.commandLinkFunction()); // EVALInjections_Safe AMNILK 20070810
      else if (mgr.dataTransfered())
         populateTransferredData();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("CONTRACTORECEIVESEARCH"))
            || !mgr.isEmpty(mgr.getQueryStringValue("SEARCH"))
            || (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")) && !mgr
                  .isEmpty(mgr.getQueryStringValue("DOC_NO")))  )
         runQuery();

      else if (!mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_CLASS"))
            && !mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_NO"))
            && !mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_SHEET"))
            && !mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_REV"))) // Bug Id // 85223
         findSubDocuments();

      else if (!mgr.isEmpty(mgr.getQueryStringValue("FINDLAY")))
         openInFindLayout();
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO")))
            && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")))
            && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_REV"))))
         runQuery();
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO")))
            && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_REV"))))
         runQuery();
      else if (!mgr.isEmpty(mgr.readValue("MULTIROWACTION")))
         eval(mgr.readValue("MULTIROWACTION")); // EVALInjections_Safe AMNILK
                                                // 20070810
      // Bug Id 57275, Start
      else if ("TRUE".equals(mgr.readValue("CHANGE_CURRENT_DOCUMENT")))
         setCurrentDocument();
      // Bug Id 57275, End
      else if ("OK".equals(mgr.readValue("CONFIRM"))) {
         client_confirmation = true;
         confirm_func = ctx.readValue("CONFIRMFUNC", "");

         // Bug 70808, Start
         String bgj_confirm = ctx.readValue("BGJ2BDONE", "");
         String bgj_diag_popped = ctx.readValue("BGJ_DIALOG_POPPED", "");

         if ("OK".equals(mgr.readValue("BGJ_CONFIRMED"))
               || "TRUE".equals(bgj_confirm))
            bPerformBackgroundJob = true;
         else
            bPerformBackgroundJob = false;

         if ("TRUE".equals(bgj_diag_popped))
            bDialogBoxPopped = true;
         else
            bDialogBoxPopped = false;

         if ("setObsolete();".equals(confirm_func))
            setObsolete();
         else if ("saveLast()".equals(confirm_func))
            saveLast();
         else if ("deleteITEM6Last()".equals(confirm_func))
            deleteITEM6Last();
         else if ("copyAccessTem()".equals(confirm_func))
            copyAccessTem();
         else if ("replaceRevInStructure()".equals(confirm_func)) {
            bSetStructure = true;
            replaceRevisionFinish();
         }
         // Bug 70808, End

         // Bug Id 77080 Start
//         else if ("resetStatus()".equals(confirm_func))
//            resetStatus();
         // Bug Id 77080 End
      } else if ("CANCEL".equals(mgr.readValue("CONFIRM"))) {
         unconfirm_func = ctx.readValue("UNCONFIRMFUNC", "");
         eval(unconfirm_func + ";"); // EVALInjections_Safe AMNILK 20070810
      } else if ("TRUE".equals(mgr.readValue("OBJECT_INSERTED")))
         insertNewObject();
      else if ("TRUE".equals(mgr.readValue("MODE_CHANGED")))
         historyModeChanged();
      else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
         performRefreshParent();
      else if (!mgr.isEmpty(mgr.readValue("DROPED_FILES_PATH"))
            && !mgr.isEmpty(mgr.readValue("DROPED_FILES_LIST")))
         lookIntoDroppedFiles();

      if (!mgr.isEmpty(mgr.readValue("DOC_CLASS_FROM_WIZ")))
         addNewRev();

      adjust();
      tabs.saveActiveTab();

      ctx.writeValue("HISTMODE", sHistoryMode);
      ctx.writeValue("SAVETYPE", savetype);
      ctx.writeValue("SEND_URL", sendingUrl);
      ctx.writeValue("TRANS_ID_SENT", trans_id_sent);
      ctx.writeValue("TRANSFERED_CURRROW", transfered_CurrRow);
      ctx.writeFlag("SHOWINMULTI", showInMulti);
      ctx.writeValue("SHOW_IN_NAVIGATOR", show_in_navigator);
   }

   public void validate() {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");

      if ("ITEM6_PERSON_ID".equals(val)) {
         trans.clear();
         cmd = trans.addCustomFunction("GETNAME", "PERSON_INFO_API.Get_Name",
               "PERSONNAME");
         cmd.addParameter("ITEM6_PERSON_ID");
         cmd = trans.addCustomFunction("GETUSER",
               "PERSON_INFO_API.Get_User_Id", "PERSONUSERID");
         cmd.addParameter("ITEM6_PERSON_ID");
         trans = mgr.validate(trans);

         String name = trans.getValue("GETNAME/DATA/PERSONNAME");
         String user = trans.getValue("GETUSER/DATA/PERSONUSERID");

         if (mgr.isEmpty(name))
            name = "";
         if (mgr.isEmpty(user))
            user = "";

         mgr.responseWrite(name + "^" + user + "^");
      } else if ("TEMP_PROFILE_ID".equals(val)) {

         trans.clear();
         cmd = trans.addCustomFunction("PROFILEDESC",
               "APPROVAL_PROFILE_API.Get_Description", "DESCRIPTION");
         cmd.addParameter("PROFILE_ID", mgr.readValue("TEMP_PROFILE_ID"));
         trans = mgr.validate(trans);

         String profiledesc = trans.getValue("PROFILEDESC/DATA/DESCRIPTION");
         String txt = (mgr.isEmpty(profiledesc) ? "" : profiledesc + "^");
         mgr.responseWrite(txt);
      } else if ("DOC_STATUS".equals(val)) {
         String curr = mgr.readValue("DOC_STATUS");
         mgr.responseWrite(curr + "^");
      } else if ("SUB_DOC_NO".equals(val) || "ITEM4_DOC_NO".equals(val)) {
         trans.clear();
         cmd = trans.addCustomFunction("GETTITLE1", "DOC_TITLE_API.Get_Title",
               "DUMMY_OUT_1");
         cmd.addParameter("SUB_DOC_CLASS");
         cmd.addParameter("SUB_DOC_NO");
         trans = mgr.validate(trans);

         String title = trans.getValue("GETTITLE1/DATA/DUMMY_OUT_1");
         String txt = (mgr.isEmpty(title) ? "" : title + "^");
         mgr.responseWrite(txt);
      } else if ("SUB_DOC_REV".equals(val)) {
         trans.clear();
         cmd = trans.addCustomFunction("GETNOCHLDRN",
               "DOC_STRUCTURE_API.Number_Of_Children_", "DUMMY_OUT_1");
         cmd.addParameter("SUB_DOC_CLASS");
         cmd.addParameter("SUB_DOC_NO");
         cmd.addParameter("SUB_DOC_SHEET");
         cmd.addParameter("SUB_DOC_REV");
         trans = mgr.validate(trans);

         String noofchildren = trans.getValue("GETNOCHLDRN/DATA/DUMMY_OUT_1");
         String txt = (mgr.isEmpty(noofchildren) ? "" : noofchildren + "^");
         mgr.responseWrite(txt);
      } else if ("PROJ_NO".equals(val)) {
         trans.clear();
         cmd = trans.addCustomFunction("GETPROJECTNAME",
               "GENERAL_PROJECT_API.Get_Proj_Desc", "DUMMY_OUT_1");
         cmd.addParameter("PROJ_NO");

         trans = mgr.validate(trans);

         String projName = trans.getValue("GETPROJECTNAME/DATA/DUMMY_OUT_1");
         String txt = (mgr.isEmpty(projName) ? "" : projName + "^");
         mgr.responseWrite(txt);
      } else if ("SEND_UNIT_NO".equals(val) || "ORG_NO".equals(val)) {
         trans.clear();
         cmd = trans.addCustomFunction("GETUNITNAME",
               "GENERAL_ZONE_API.Get_Zone_Desc", "DUMMY_OUT_1");
         if ("SEND_UNIT_NO".equals(val)) {
            cmd.addParameter("SEND_UNIT_NO");
         } else {
            cmd.addParameter("ORG_NO");
         }

         trans = mgr.validate(trans);

         String projName = trans.getValue("GETUNITNAME/DATA/DUMMY_OUT_1");
         String txt = (mgr.isEmpty(projName) ? "" : projName + "^");
         mgr.responseWrite(txt);
      } else if ("RECEIVE_UNIT_NO".equals(val)) {
         trans.clear();
         cmd = trans.addCustomFunction("GETUNITNAME",
               "GENERAL_ZONE_API.Get_Zone_Desc", "DUMMY_OUT_1");
         cmd.addParameter("RECEIVE_UNIT_NO");

         trans = mgr.validate(trans);

         String projName = trans.getValue("GETUNITNAME/DATA/DUMMY_OUT_1");
         String txt = (mgr.isEmpty(projName) ? "" : projName + "^");
         mgr.responseWrite(txt);
      } else if ("PURPOSE_NO".equals(val)) {
         trans.clear();
         cmd = trans.addCustomFunction("GETPURPOSEDESC",
               "DOC_COMMUNICATION_SEQ_API.Get_Purpose_Name", "DUMMY_OUT_1");
         cmd.addParameter("PURPOSE_NO");

         trans = mgr.validate(trans);

         String purposeName = trans.getValue("GETPURPOSEDESC/DATA/DUMMY_OUT_1");
         String txt = (mgr.isEmpty(purposeName) ? "" : purposeName + "^");
         mgr.responseWrite(txt);
      } else if ("SIGN_PERSON".equals(val)) {
         trans.clear();
         cmd = trans.addCustomFunction("GETPURPOSEDESC",
               "PERSON_INFO_API.Get_Name_For_User", "DUMMY_OUT_1");
         cmd.addParameter("SIGN_PERSON");

         trans = mgr.validate(trans);

         String personName = trans.getValue("GETPERSONDESC/DATA/DUMMY_OUT_1");
         String txt = (mgr.isEmpty(personName) ? "" : personName + "^");
         mgr.responseWrite(txt);
      }
      mgr.endResponse();
   }

   public void copyAccessMsg() {
      ASPManager mgr = getASPManager();

      // Bug 53039, Start
      if (dAll.equals(headset.getRow().getValue("ACCESS_CONTROL"))) {
         bDoCheckForAllAccess = true;
      } else
         bDoCheckForAllAccess = false;
      // Bug 53039, End

      if (!"TRUE".equals(isAccessOwner())) {
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUENOPERMISSIONTOCOPYACCESSTMPL: You must have administrative rights to be able to copy an access template."));
         return;
      }

      if (!isValidColumnValue("STATE", sApproved, sReleased, false)) {
         mgr.showAlert("DOCMAWDOCISSUECANNOTCOPYACCESSTEMPLINAPPORREL: You cannot copy an access template when document(s) are in state Approved or Released.");
         return;
      }

      bConfirm = true;
      sMessage = mgr
            .translate("DOCMAWDOCISSUEQACCTEM: Do you want to copy the access template from the document class? NOTE: This will not affect the current access rows.");
      ctx.writeValue("CONFIRMFUNC", "copyAccessTem()");
      ctx.writeValue("UNCONFIRMFUNC", "onUnconfirm()");
   }

   public void copyAccessTem() {
      ASPManager mgr = getASPManager();

      trans.clear();

      if (headlay.isMultirowLayout())
         headset.selectRows();
      else
         headset.selectRow();

      headset.setFilterOn();
      headset.first();
      int count = 0;

      do {
         cmd = trans.addCustomCommand("COPYACCESSTEMPLATE" + count++,
               "Document_Issue_Access_API.Copy_Access_Template__");
         cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
      } while (headset.next());

      trans = mgr.perform(trans);

      headset.setFilterOff();

      if (headlay.isSingleLayout())
         okFindITEM6();
   }

   public void lookIntoDroppedFiles() {
      ASPManager mgr = getASPManager();
      String sFileList = mgr.readValue("DROPED_FILES_LIST");
      String sDroppedFilePath = mgr.readValue("DROPED_FILES_PATH");
      String[] sFiles = split(sFileList, "|");

      String fullFileName = "";
      if (sDroppedFilePath.charAt(sDroppedFilePath.length() - 1) == '\\')
         fullFileName = sDroppedFilePath + sFiles[0];
      else {
         fullFileName = sDroppedFilePath + "\\" + sFiles[0];
         sDroppedFilePath += "\\";
      }

      if (sFiles.length == 1)
         checkInTheDroppedFile(fullFileName);
      else if (sFiles.length > 1) {
         sFileList = "";
         for (int k = 0; k < sFiles.length; k++) {
            sFileList += sDroppedFilePath + sFiles[k] + "|";
         }
         // Modified by Terry 20130325
         // Origninal: moveDroppedFilesToFileImport(sFileList);
         checkInTheDroppedFile(sFileList);
         // Modified end
      }
   }

   public void checkInTheDroppedFile(String sFileName) {
      ASPManager mgr = getASPManager();
      headset.goTo(headset.getCurrentRowNo());
      ASPBuffer currentRow = headset.getRow();
      ASPBuffer buff = mgr.newASPBuffer();

      ASPBuffer actionData = mgr.newASPBuffer();

      String action = "CHECKIN";
      String doc_type = "ORIGINAL";
      String same_action = "NO";

      actionData.addItem("DOC_TYPE", doc_type);
      actionData.addItem("FILE_ACTION", action);
      actionData.addItem("SAME_ACTION_TO_ALL", same_action);

      ASPBuffer row = buff.addBuffer("1");

      row.addItem("DOC_CLASS", currentRow.getValue("DOC_CLASS"));
      row.addItem("DOC_NO", currentRow.getValue("DOC_NO"));
      row.addItem("DOC_SHEET", currentRow.getValue("DOC_SHEET"));
      row.addItem("DOC_REV", currentRow.getValue("DOC_REV"));
      row.addItem("FILE_NAME", sFileName);

      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr,
            "EdmMacro.page", actionData, buff);
      bTranferToEDM = true;

   }

   public void moveDroppedFilesToFileImport(String fileNames) {

      ASPManager mgr = getASPManager();
      ASPBuffer fileBuffer = mgr.newASPBuffer();
      fileBuffer
            .addItem("FILE_NAMES", fileNames.replaceAll("\\\\", "\\\\\\\\"));
      mgr.transferDataTo(
            "FileImport.page?FROM_OTHER=YES&DOC_CLASS="
                  + mgr.URLEncode(headset.getRow().getValue("DOC_CLASS")),
            fileBuffer);

   }

   public void openInFindLayout() {
      headlay.setLayoutMode(headlay.FIND_LAYOUT);
   }

   protected void populateTransferredData() {
      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE"))) {
         ASPManager mgr = getASPManager();
         ASPBuffer buf = mgr.getTransferedData();

         String doc_class = buf.getValue("DATA/DOC_CLASS");
         String doc_no = buf.getValue("DATA/DOC_NO");
         String doc_sheet = buf.getValue("DATA/DOC_SHEET");
         String doc_rev = buf.getValue("DATA/DOC_REV");

         findDocumentsInStructure(doc_class, doc_no, doc_sheet, doc_rev);
      } else {
         runQuery();
      }
   }

   public void runQuery() {
      ASPManager mgr = getASPManager();
      ASPLog log = mgr.getASPLog();
      searchURL = mgr.createSearchURL(headblk);

      trans.clear();
      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());

      q.addWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         q.addWhereCondition("SUB_CLASS='" + subClass + "'");
      }
      // Data isolation
      String tempPersonZones = mgr.getASPContext().findGlobal(
            ifs.genbaw.GenbawConstants.PERSON_ZONES);
      StringBuffer sb = new StringBuffer("(");
      if (!"()".equals(tempPersonZones)) {
         sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
      } else {
         sb.append("(1=1)");
      }
      sb.append(")");
      q.addWhereCondition(sb.toString());

      q.setOrderByClause("DT_CRE DESC");
      q.includeMeta("ALL");

      mgr.querySubmit(trans, headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENODATA: No data found."));
         eval(headset.syncItemSets());
         return;
      }
      
      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_KEY"))) 
      {
         String doc_key = mgr.getQueryStringValue("DOC_KEY");
         String[] keys = split(doc_key, '^');
         int k = 0;
         headset.first();
         while (!(headset.getValue("DOC_CLASS").equals(keys[0])
               && headset.getValue("DOC_NO").equals(keys[1])
               && headset.getValue("DOC_SHEET").equals(keys[2]) && headset.getValue("DOC_REV").equals(keys[3]))) {
            headset.next();
         }
      }
      okFindActiveTab();
   }
   
   public void runQueryForExch(){
      ASPManager mgr = getASPManager();
      ASPLog log  = mgr.getASPLog();
      searchURL = mgr.createSearchURL(headblk);
      
      trans.clear();
      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      
      q.addWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         q.addWhereCondition("SUB_CLASS='" + subClass + "'");
      }
      
      ctx = mgr.getASPContext();
      String personDefaultZone = ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE);
      if(mgr.isEmpty(personDefaultZone)){
         mgr.showAlert("DOCISSUECANNOTFINDUSERDEFALUTZONENO: Can not find user's default zone no.");
         return;
      }
      
      q.addWhereCondition("  exists (select 1 from doc_issue_target_org t where t.doc_class = DOC_ISSUE_REFERENCE.doc_class and t.doc_no = DOC_ISSUE_REFERENCE.doc_no and t.doc_sheet = DOC_ISSUE_REFERENCE.doc_sheet and t.doc_rev = DOC_ISSUE_REFERENCE.doc_rev and t.org_no ='"+personDefaultZone+"' )");
      
      
      q.setOrderByClause("DT_CRE DESC");
      q.includeMeta("ALL");
      
      mgr.querySubmit(trans,headblk);
      
      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENODATA: No data found."));
         eval(headset.syncItemSets());
         return;
      }
      
      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_KEY")))
      {
         String doc_key = mgr.getQueryStringValue("DOC_KEY");
         String[] keys  = split(doc_key,'^');
         int k=0;
         headset.first();
         while (!(headset.getValue("DOC_CLASS").equals(keys[0]) &&
               headset.getValue("DOC_NO").equals(keys[1]) &&
               headset.getValue("DOC_SHEET").equals(keys[2]) &&
               headset.getValue("DOC_REV").equals(keys[3]) ))
         {
            headset.next();
         }
      }
      okFindActiveTab();
      okFindITEM11();
   }

 /*  // Bug Id 77080, Start
   public void resetStatus() {
      ASPManager mgr = getASPManager();
      boolean bMultiRow;

      if ("OK".equals(mgr.readValue("CONFIRM"))) {
         client_confirmation = true;
         confirm_func = ctx.readValue("CONFIRMFUNC", "");
      }

      if ("TRUE".equals(mgr.readValue("MULTIROW_EDIT")))
         bMultiRow = true;

      if (headlay.isMultirowLayout() && headset.selectRows() == 0) {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      if (isUserDocmanAdministrator()) {

         boolean error = false;
         int curr_row = 0;
         String res;

         if (!client_confirmation
               && !isValidColumnValue("EDM_DB_STATE", sOpeInProg, true)) {
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUEOPINPRO: The file status can be reset only for documents in status Operation In Progress."));
         } else {

            if (!client_confirmation) {
               getClientConformation(
                     mgr.translate("DOCMAWDOCISSUECONFIRMRESET: You are trying to reset the file status of this document(s). If a file exists in the repository for this document, the file status will be changed to Checked In. If not, the file reference will be removed. Make sure that no documents are in transit before performing this operation because there may be large files which are currently being checked in or checked out."),
                     "resetStatus()");
            } else {

               if (headlay.isMultirowLayout()) {

                  headset.storeSelections();
                  curr_row = headset.getRowSelected();
               } else {
                  headset.selectRow();
                  curr_row = headset.getCurrentRowNo();
               }

               headset.setFilterOn();
               headset.first();

               do {
                  String doc_class = headset.getRow().getValue("DOC_CLASS");
                  String doc_no = headset.getRow().getValue("DOC_NO");
                  String doc_sheet = headset.getRow().getValue("DOC_SHEET");
                  String doc_rev = headset.getRow().getValue("DOC_REV");
                  String doc_type = "ORIGINAL";
                  String file_no = "1";

                  res = checkFileExistInRep(doc_class, doc_no, doc_sheet,
                        doc_rev, doc_type);
                  if (res == "exist") {
                     // String checked_in = "FinishCheckIn";
                     trans.clear();
                     ASPCommand cmd = trans.addCustomCommand("STATECHECKIN",
                           "Edm_File_API.Set_File_State_No_History");
                     cmd.addParameter("DUMMY_IN_1", doc_class);
                     cmd.addParameter("DUMMY_IN_1", doc_no);
                     cmd.addParameter("DUMMY_IN_1", doc_sheet);
                     cmd.addParameter("DUMMY_IN_1", doc_rev);
                     cmd.addParameter("DUMMY_IN_1", doc_type);
                     cmd.addParameter("DUMMY_IN_1", sFinishCheckIn);
                     trans = mgr.perform(trans);

                     addHistoryRecord(
                           doc_class,
                           doc_no,
                           doc_sheet,
                           doc_rev,
                           "RESETSTATUS",
                           mgr.translate("DOCMAWDOCISSUEFILECHECKEDIN: File status is manually reset to Checked In."));

                  } else if (res == "notexist") {
                     trans.clear();
                     ASPCommand cmd = trans.addCustomCommand("STATECHECKIN",
                           "Edm_File_API.Delete_File_Ref_No_History");
                     cmd.addParameter("DUMMY_IN_1", doc_class);
                     cmd.addParameter("DUMMY_IN_1", doc_no);
                     cmd.addParameter("DUMMY_IN_1", doc_sheet);
                     cmd.addParameter("DUMMY_IN_1", doc_rev);
                     cmd.addParameter("DUMMY_IN_1", doc_type);
                     cmd.addParameter("DUMMY_IN_1", file_no);
                     trans = mgr.perform(trans);

                     addHistoryRecord(
                           doc_class,
                           doc_no,
                           doc_sheet,
                           doc_rev,
                           "RESETSTATUS",
                           mgr.translate("DOCMAWDOCISSUEFILEDELETED: File status is manually reset by deleting the file reference."));
                  }

               } while (headset.next());

               headset.refreshAllRows();
               client_confirmation = false;
               // headset.goTo(curr_row);

            }// else
            headset.setFilterOff();
         }
         // Refresh the child blocks if requred..
         if (headlay.isSingleLayout()) {
            if (tabs.getActiveTab() == 6) {
               okFindITEM9();
            } else if (tabs.getActiveTab() == 5) {
               okFindITEM7();
            }
         }

      }// if not admin
      else {
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUENOTADMIN: You need DOCMAN ADMINISTRATOR privileges to Reset File Status."));

      }

   }*/

   public void addHistoryRecord(String doc_class, String doc_no,
         String doc_sheet, String doc_rev, String info_cat, String note) {
      ASPManager mgr = getASPManager();
      trans.clear();
      ASPCommand cmd = trans.addCustomCommand("STATECHECKIN",
            "Document_Issue_History_Api.Insert_New_Line_");
      cmd.addParameter("DUMMY_IN_1", doc_class);
      cmd.addParameter("DUMMY_IN_1", doc_no);
      cmd.addParameter("DUMMY_IN_1", doc_sheet);
      cmd.addParameter("DUMMY_IN_1", doc_rev);
      cmd.addParameter("DUMMY_IN_1", info_cat);
      cmd.addParameter("DUMMY_IN_1", note);
      trans = mgr.perform(trans);

   }

   public String checkFileExistInRep(String doc_class, String doc_no,
         String doc_sheet, String doc_rev, String doc_type) {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("CHECKFILEEXIST",
            "Edm_File_Api.Check_Exist");
      cmd.addParameter("DUMMY_OUT_1");
      cmd.addParameter("DUMMY_IN_1", doc_class);
      cmd.addParameter("DUMMY_IN_1", doc_no);
      cmd.addParameter("DUMMY_IN_1", doc_sheet);
      cmd.addParameter("DUMMY_IN_1", doc_rev);
      cmd.addParameter("DUMMY_IN_1", doc_type);

      trans = mgr.perform(trans);
      String res = "";
      String exist;
      exist = trans.getValue("CHECKFILEEXIST/DATA/DUMMY_OUT_1");

      trans.clear();
      cmd = trans.addCustomFunction("EDMREPINFO",
            "Edm_File_Api.Get_Edm_Repository_Info", "DUMMY6");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV", doc_rev);
      cmd.addParameter("DOC_TYPE", doc_type);
      trans = mgr.perform(trans);

      String edmRepInfo = trans.getValue("EDMREPINFO/DATA/DUMMY6");

      String ftp_port = getStringAttribute(edmRepInfo, "LOCATION_PORT");
      String ftp_address = getStringAttribute(edmRepInfo, "LOCATION_ADDRESS");
      String ftp_user = getStringAttribute(edmRepInfo, "LOCATION_USER");
      String ftp_password = getStringAttribute(edmRepInfo, "LOCATION_PASSWORD");
      String local_file_name = getStringAttribute(edmRepInfo, "LOCAL_FILE_NAME");
      String temp_path = getStringAttribute(edmRepInfo, "LOCAL_PATH");
      String ftp_file_name = getStringAttribute(edmRepInfo, "REMOTE_FILE_NAME");
      String location_type = getStringAttribute(edmRepInfo, "LOCATION_TYPE");
      boolean result = false;

      if ("TRUE".equals(exist)) {

         if ("2".equals(location_type)) {
            DocmawFtp ftp_server = new DocmawFtp();

            int port_no = mgr.isEmpty(ftp_port) ? 21 : Integer
                  .parseInt(ftp_port);
            try {
               debug("ftp_address" + ftp_address);
               debug("ftp_user" + ftp_user);
               debug("ftp_password" + ftp_password);
               debug("port_no" + port_no);

               result = ftp_server.login(ftp_address, ftp_user, ftp_password,
                     port_no);

            } catch (Exception e) {
               mgr.showAlert(mgr
                     .translate(
                           "DOCMAWDOCSRVFTPLOGINFAILED1: Login to FTP server &1 on port: &2 with login name &3 failed.",
                           ftp_address, Integer.toString(port_no), ftp_user));

            }
            if (result) {

               try {

                  if (!ftp_server.checkExist(ftp_file_name)) {
                     ftp_server.logoff();
                     res = "notexist";

                  } else {
                     res = "exist";
                     ftp_server.logoff();
                  }

               } catch (Exception e) {
                  res = "notexist";

               }
            }

         } else if ("1".equals(location_type)) {
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUESHAREDNOSUP: Shared repository is not supported by Docmaw. Please use Docman to reset this file."));
         } else {
            trans.clear();
            cmd = trans.addCustomFunction("EXIST",
                  "Edm_File_Storage_API.File_Exist", "DUMMY_OUT_1");
            cmd.addParameter("DUMMY_IN_1", doc_class);
            cmd.addParameter("DUMMY_IN_1", doc_no);
            cmd.addParameter("DUMMY_IN_1", doc_sheet);
            cmd.addParameter("DUMMY_IN_1", doc_rev);
            cmd.addParameter("DUMMY_IN_1", doc_type);
            trans = mgr.perform(trans);

            if ("TRUE".equals(trans.getValue("EXIST/DATA/DUMMY_OUT_1"))) {
               res = "exist";
            } else {
               res = "notexist";
            }
         }
      } else {
         res = "notexist";
      }
      return res;
   }

   // Bug Id 77080, End

   protected void findSubDocuments() {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      // Bug Id 85223, Start, Added Doc_sheet and doc_rev
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", mgr.readValue("SUB_DOC_CLASS"));
      q.addParameter("DOC_NO", mgr.readValue("SUB_DOC_NO"));
      q.addParameter("DOC_SHEET", mgr.readValue("SUB_DOC_SHEET"));
      q.addParameter("DOC_REV", mgr.readValue("SUB_DOC_REV"));
      // Bug Id 85223, End
      q.setOrderByClause("DT_CRE DESC");
      q.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);
      okFindITEM2();
   }

   // Bug Id 67105, Start
   protected void getMandatoryFieldsForQuery() {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("MANDATORYFIELDS",
            "DOCMAN_DEFAULT_API.Get_Default_Value_", "DUMMY3");
      cmd.addParameter("DUMMY1", "DocIssue");
      cmd.addParameter("DUMMY2", "DOC_ISSUE_MANDATORY_FIELDS");
      trans = mgr.perform(trans);
      sMandatoryFieldsList = trans.getValue("MANDATORYFIELDS/DATA/DUMMY3");

      String sMandatoryField;
      String sField;

      if ((!mgr.isEmpty(sMandatoryFieldsList))
            && stringIndex(sMandatoryFieldsList, "^") > 0) {

         StringTokenizer st = new StringTokenizer(sMandatoryFieldsList, "^");
         while (st.hasMoreTokens()) {
            sMandatoryField = st.nextToken();
            StringTokenizer stfields = new StringTokenizer(
                  headblk.getFieldList(), ",");

            while (stfields.hasMoreTokens()) {
               sField = stfields.nextToken();
               if (sField.equals(sMandatoryField)) {
                  if (mgr.isEmpty(sMandatoryFields))
                     sMandatoryFields = mgr.getASPField(sField).getLabel()
                           + ",";
                  else
                     sMandatoryFields = sMandatoryFields + " "
                           + mgr.getASPField(sField).getLabel() + ",";
               }
            }
         }
         if (!mgr.isEmpty(sMandatoryFields))
            sMandatoryFields = sMandatoryFields.substring(0,
                  sMandatoryFields.length() - 1);
      }
   }

   // Bug Id 67105, End

   public void okFind() {

      ASPManager mgr = getASPManager();
      searchURL = mgr.createSearchURL(headblk);
      boolean bValueExists = true; // Bug Id 77651
      // Bug Id 67105, Start
      if (!mgr.isEmpty(sMandatoryFields)) {
         String sMandatoryField;
         String sField;
         bValueExists = false; // Bug Id 77651

         StringTokenizer st = new StringTokenizer(sMandatoryFieldsList, "^");
         while (st.hasMoreTokens()) {
            sMandatoryField = st.nextToken();
            StringTokenizer stfields = new StringTokenizer(
                  headblk.getFieldList(), ",");

            while (stfields.hasMoreTokens()) {
               sField = stfields.nextToken();
               // Bug Id 77651, Start
               if (sField.equals(sMandatoryField)
                     && (!mgr.isEmpty(mgr.readValue(sField)))
                     && (!"%".equals(mgr.readValue(sField)))) {
                  bValueExists = true;
                  break;
               }
            }
            if (bValueExists)
               break;
            // Bug Id 77651, End
         }
      }
      // Bug Id 77651, Start
      if (bValueExists)
         bMandatoryFieldsEmpty = false;
      else
         bMandatoryFieldsEmpty = true;
      // Bug Id 77651, End
      if (bMandatoryFieldsEmpty) {
         trans = mgr.newASPTransactionBuffer();
         headset.clear();
         eval(headset.syncItemSets());
         return;
      }
      // Bug Id 67105, End
      else {
         trans.clear();
         q = trans.addQuery(headblk);
         q.addWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
         q.addWhereCondition("OBJSTATE != 'Obsolete' ");
         String subClass = getCurrentPageSubDocClass();
         if(!Str.isEmpty(subClass)){
            q.addWhereCondition("SUB_CLASS='" + subClass + "'");
         }
         // Data isolation
         String tempPersonZones = mgr.getASPContext().findGlobal(
               ifs.genbaw.GenbawConstants.PERSON_ZONES);
         StringBuffer sb = new StringBuffer("(");
         if (!"()".equals(tempPersonZones)) {
            sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
         } else {
            sb.append("(1=2)");
         }
         sb.append(")");
         q.addWhereCondition(sb.toString());

         q.setOrderByClause("DT_CRE DESC");
         q.includeMeta("ALL");

         if (mgr.dataTransfered()) {
            q.addOrCondition(mgr.getTransferedData());
         }

         mgr.querySubmit(trans, headblk);

         if (headset.countRows() == 0) {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENODATA: No data found."));
            eval(headset.syncItemSets());
            return;
         }

//         eval(headset.syncItemSets());
         okFindActiveTab();
      }
   }
   
   private void okFindActiveTab()
   {
      switch(tabs.getActiveTab())
      {
      case 1:
         okFindITEM2();
         break;
      case 2:
         okFindITEM3();
         break;
      case 3:
         okFindITEM4();
         break;
      case 4:
         okFindITEM6();
         break;
      case 5:
         okFindITEM7();
         break;
      case 6:
         okFindITEM9();
         break;
      case 7:
         okFindITEM11();
         break;
      case 8:
         break;
      case 9:
         okFindITEM13();
         break;
      case 10:
         okFindCallback();
         break;
      case 12:
         okFindDistribution();
      default:
         break;
      }
   }
   
   public void okFindForExch(){
      ASPManager mgr = getASPManager();
      searchURL = mgr.createSearchURL(headblk);
      boolean bValueExists = true; //Bug Id 77651
      //Bug Id 67105, Start
      if (! mgr.isEmpty(sMandatoryFields)) 
      {
         String sMandatoryField;
         String sField;
         bValueExists = false; //Bug Id 77651
         
         StringTokenizer st = new StringTokenizer(sMandatoryFieldsList, "^");
         while (st.hasMoreTokens())
         {
            sMandatoryField = st.nextToken();
            StringTokenizer stfields = new StringTokenizer(headblk.getFieldList(), ",");
            
            while (stfields.hasMoreTokens())
            {
               sField = stfields.nextToken();
               //Bug Id 77651, Start
               if (sField.equals(sMandatoryField) && (! mgr.isEmpty(mgr.readValue(sField))) && (! "%".equals(mgr.readValue(sField))))
               {
                  bValueExists = true; 
                  break;
               }
            }
            if (bValueExists) 
               break;
            //Bug Id 77651, End
         }
      }
      //Bug Id 77651, Start
      if (bValueExists) 
         bMandatoryFieldsEmpty = false;
      else
         bMandatoryFieldsEmpty = true;
      //Bug Id 77651, End
      if (bMandatoryFieldsEmpty) 
      {
         trans   = mgr.newASPTransactionBuffer();
         headset.clear();
         eval(headset.syncItemSets());
         return;
      }
      //Bug Id 67105, End
      else
      {
         trans.clear();
         q = trans.addQuery(headblk);
         q.addWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
         String subClass = getCurrentPageSubDocClass();
         if(!Str.isEmpty(subClass)){
            q.addWhereCondition("SUB_CLASS='" + subClass + "'");
         }
         ctx = mgr.getASPContext();
         String personDefaultZone = ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE);
         if(mgr.isEmpty(personDefaultZone)){
            mgr.showAlert("DOCISSUECANNOTFINDUSERDEFALUTZONENO: Can not find user's default zone no.");
            return;
         }
         
         q.addWhereCondition("  exists (select 1 from doc_issue_target_org t where t.doc_class = DOC_ISSUE_REFERENCE.doc_class and t.doc_no = DOC_ISSUE_REFERENCE.doc_no and t.doc_sheet = DOC_ISSUE_REFERENCE.doc_sheet and t.doc_rev = DOC_ISSUE_REFERENCE.doc_rev and t.org_no ='"+personDefaultZone+"' )");
         
         q.setOrderByClause("DT_CRE DESC");
         q.includeMeta("ALL");
         
         if (mgr.dataTransfered())
         {
            q.addOrCondition(mgr.getTransferedData());
         }
         
         mgr.querySubmit(trans,headblk);
         
         if (headset.countRows() == 0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENODATA: No data found."));
            eval(headset.syncItemSets());
            return;
         }
         
         eval(headset.syncItemSets());
         okFindActiveTab();
         okFindITEM11();
      }
   }

   public void findDocumentsInStructure(String doc_class, String doc_no,
         String doc_sheet, String doc_rev) {
      ASPManager mgr = getASPManager();

      // build query to fetch all documents in structure..
      StringBuffer select_structure = new StringBuffer();

      // SQLInjections_Safe AMNILK 20070810
      select_structure.append("SELECT '");
      select_structure.append(doc_class);
      select_structure.append("' DOC_CLASS, '");
      select_structure.append(doc_no);
      select_structure.append("' DOC_NO, '");
      select_structure.append(doc_sheet);
      select_structure.append("' DOC_SHEET, '");
      select_structure.append(doc_rev);
      select_structure.append("' DOC_REV ");
      select_structure.append("FROM DUAL ");
      select_structure.append("UNION ALL ");
      select_structure
            .append("SELECT sub_doc_class, sub_doc_no, sub_doc_sheet, sub_doc_rev ");
      select_structure.append("FROM doc_structure ");
      select_structure.append("CONNECT BY doc_class = PRIOR sub_doc_class ");
      select_structure.append("AND doc_no = PRIOR sub_doc_no ");
      select_structure.append("AND doc_sheet = PRIOR sub_doc_sheet ");
      select_structure.append("AND doc_rev = PRIOR sub_doc_rev ");
      select_structure.append("START WITH doc_class = ?");
      select_structure.append(" AND doc_no = ?");
      select_structure.append(" AND doc_sheet = ?");
      select_structure.append(" AND doc_rev = ?");

      // retrieve structure..
      trans.clear();
      q = trans.addQuery("GET_STRUCTURE", select_structure.toString());
      q.addParameter("DOC_CLASS", doc_class);
      q.addParameter("DOC_NO", doc_no);
      q.addParameter("DOC_SHEET", doc_sheet);
      q.addParameter("DOC_REV", doc_rev);

      trans = mgr.perform(trans);

      ASPBuffer doc_buf = trans.getBuffer("GET_STRUCTURE");

      // remove the last INFO item from this buffer..
      doc_buf.removeItemAt(doc_buf.countItems() - 1);

      // populate the master with the all documents in the structure..
      trans.clear();
      ASPQuery query = trans.addEmptyQuery(headblk);
      query.addOrCondition(doc_buf);
      query.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);

      // initialise the current row..
      headset.first();

      // activate title tab..
      refreshActiveTab();
   }

   public void showStructureInNavigator() {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.selectRows();
      else
         headset.selectRow();

      ASPBuffer keys = headset
            .getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      sUrl = DocumentTransferHandler.getDataTransferUrl(mgr,
            "DocStructureNavigator.page", keys);
      bShowStructure = true;
   }

   public void setStructAttribAll() {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();

      headset.setFilterOn();
      trans.clear();
      for (int k = 0; k < headset.countSelectedRows(); k++) {
         ASPCommand cmd = trans.addCustomCommand("SETSTRUCTURE" + k,
               "DOC_TITLE_API.Set_Structure_All_");
         cmd.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         headset.next();
      }
      trans = mgr.perform(trans);
      headset.setFilterOff();
      headset.unselectRows();

      refreshHeadset();

   }

   public void unsetStructAttribAll() {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();

      headset.setFilterOn();
      trans.clear();
      for (int k = 0; k < headset.countSelectedRows(); k++) {
         ASPCommand cmd = trans.addCustomCommand("SETSTRUCTURE" + k,
               "DOC_TITLE_API.Unset_Structure_All_");
         cmd.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         headset.next();
      }
      trans = mgr.perform(trans);
      headset.setFilterOff();
      headset.unselectRows();

      refreshHeadset();
   }

   private void refreshHeadset() {
      ASPManager mgr = getASPManager();
      trans.clear();
      ASPQuery q = trans.addEmptyQuery(headblk);
      q.addOrCondition(headset.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV"));
      q.setOrderByClause("DT_CRE DESC");
      q.includeMeta("ALL");
      int row_no = headset.getCurrentRowNo();
      mgr.querySubmit(trans, headblk);
      headset.goTo(row_no);
      eval(headset.syncItemSets());
      okFindITEM2();
   }

   public void countFind() {
      ASPManager mgr = getASPManager();
      boolean bValueExists = true; // Bug Id 77651
      // Bug Id 67105, Start
      if (!mgr.isEmpty(sMandatoryFields)) {

         String sMandatoryField;
         String sField;

         StringTokenizer st = new StringTokenizer(sMandatoryFieldsList, "^");
         while (st.hasMoreTokens()) {
            sMandatoryField = st.nextToken();
            StringTokenizer stfields = new StringTokenizer(
                  headblk.getFieldList(), ",");
            bValueExists = false; // Bug Id 77651

            while (stfields.hasMoreTokens()) {
               sField = stfields.nextToken();
               // Bug Id 77651, Start
               if (sField.equals(sMandatoryField)
                     && (!mgr.isEmpty(mgr.readValue(sField)))
                     && (!"%".equals(mgr.readValue(sField)))) {
                  bValueExists = true;
                  break;
               }
            }
            if (bValueExists)
               break;
            // Bug Id 77651, End
         }
      }
      // Bug Id 77651, Start
      if (bValueExists)
         bMandatoryFieldsEmpty = false;
      else
         bMandatoryFieldsEmpty = true;
      // Bug Id 77651, End
      if (bMandatoryFieldsEmpty) {
         trans = mgr.newASPTransactionBuffer();
         headset.clear();
         eval(headset.syncItemSets());
         return;
      }
      // Bug Id 67105, End
      else {
         q = trans.addQuery(headblk);
         q.setSelectList("to_char(count(*)) N");
         
         q.addWhereCondition("DOC_CLASS='" + getCurrentPageDocClass() + "'");
         q.addWhereCondition("OBJSTATE != 'Obsolete' ");
         // Data isolation
         String tempPersonZones = mgr.getASPContext().findGlobal(
               ifs.genbaw.GenbawConstants.PERSON_ZONES);
         StringBuffer sb = new StringBuffer("(");
         if (!"()".equals(tempPersonZones)) {
            sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
         } else {
            sb.append("(1=2)");
         }
         sb.append(")");
         q.addWhereCondition(sb.toString());

         mgr.submit(trans);
         headlay.setCountValue(toInt(headset.getValue("N")));
         headset.clear();
      }
   }

   public void okFindITEM2() {
      ASPManager mgr = getASPManager();
      if (tabs.getActiveTab() == 1) {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addQuery(itemblk2);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans, itemblk2);
         headset.goTo(headrowno);
      }
   }

   public void countFindITEM2() {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      // Bug ID 45944, inoslk, start
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getValue("N")));
      itemset2.clear();
   }

   public void newRowITEM2() {
      bObjectConnection = true;
      itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
   }

   public void insertNewObject() {
      ASPManager mgr = getASPManager();
      String[] sKeyRefArray;
      String sLuName = mgr.readValue("TEMP_LU_NAME");
      String sKeyRefStr = mgr.readValue("TEMP_KEY_REF");
      String sKeyRef = null;
      String sDecodedLastDocRev;
      StringTokenizer st = null;
      int nTmpCounter = 0; // used to create unique buffer names.
      int nSelectedRows = 0;
      int headrowno = 0;// Bug Id:68450

      /*
       * the stored selections (in method connectObject()) will be retrieved
       * here in detail mode.
       */
      if (headlay.isMultirowLayout()) {
         headset.setFilterOn();
         nSelectedRows = headset.countSelectedRows();
         headset.first();
      } else {// Bug Id:68450, start
         nSelectedRows = 1;
         headrowno = headset.getCurrentRowNo();
      }// Bug Id:68450, end

      for (int a = 0; a < nSelectedRows; a++) {
         // split() method cannot be used for pipe charactor.therefore a
         // tokenizer is used.
         st = new StringTokenizer(sKeyRefStr, "||");
         while (st.hasMoreTokens()) {
            sKeyRef = st.nextToken();
            trans.clear();

            // gets the default values for the mandatory items

            cmd = trans.addCustomFunction("LASTDOCREV",
                  "Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_",
                  "KEEP_LAST_DOC_REV");
            cmd.addParameter("DOC_CLASS", headset.getRow()
                  .getValue("DOC_CLASS"));
            // used to get the default value using the same db call in case
            // retrived value is null.
            cmd = trans.addCustomFunction("DEFAULTLASTDOCREVDECODE",
                  "ALWAYS_LAST_DOC_REV_API.Decode", "KEEP_LAST_DOC_REV");
            cmd.addParameter("DUMMY1", "F");
            cmd = trans.addCustomFunction("LASTDOCREVDECODE",
                  "ALWAYS_LAST_DOC_REV_API.Decode", "KEEP_LAST_DOC_REV");
            cmd.addInReference("DUMMY1", "LASTDOCREV/DATA", "KEEP_LAST_DOC_REV");

            cmd = trans.addEmptyCommand("ITEM2" + nTmpCounter,
                  "DOC_REFERENCE_OBJECT_API.New__", itemblk2);
            cmd.setOption("ACTION", "PREPARE");
            cmd.setParameter("LU_NAME", sLuName);
            cmd.setParameter("KEY_REF", sKeyRef);
            cmd.setParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
            cmd.setParameter("DOC_NO", headset.getValue("DOC_NO"));
            cmd.setParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
            cmd.setParameter("DOC_REV", headset.getValue("DOC_REV"));
            trans = mgr.perform(trans);
            // adds the mandatory items to the rowset to be submitted to the
            // server when 'DO' action is invoked.
            itemset2.addRow(trans.getBuffer("ITEM2" + nTmpCounter + "/DATA"));

            sDecodedLastDocRev = trans
                  .getValue("LASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");
            if (mgr.isEmpty(sDecodedLastDocRev))
               sDecodedLastDocRev = trans
                     .getValue("DEFAULTLASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");

            // Bug Id 65675, Start
            String[] keyRefArray;
            keyRefArray = split(sKeyRef.substring(0, sKeyRef.length() - 1), '^');//

            String keyValues = "";
            for (int i = 0; i < keyRefArray.length; i++) {
               keyValues = keyValues
                     + (keyRefArray[i].substring(
                           keyRefArray[i].indexOf("=") + 1,
                           keyRefArray[i].length()));
               keyValues = keyValues + "^";
            }
            // Bug Id 65675, End

            data = itemset2.getRow();
            data.setFieldItem("KEEP_LAST_DOC_REV", sDecodedLastDocRev);
            data.setFieldItem("KEY_VALUE", keyValues); // Bug Id 65675
            itemset2.setRow(data);

            trans.clear();
            nTmpCounter++;
         }
         headset.next();
      }
      headset.setFilterOff();

      // submit the changes if at least one object is inserted.
      if (nTmpCounter > 0) {
         mgr.submit(trans);
         // Bug Id:68450, start
         if (headlay.isSingleLayout()) {
            headset.goTo(headrowno);
         }
         // Bug Id:68450, end
      }
      itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
   }

   public void okFindITEM3() {
      ASPManager mgr = getASPManager();
      if (tabs.getActiveTab() == 2) {
         if (headset.countRows() == 0)
            return;

         trans.clear();
         q = trans.addEmptyQuery(itemblk3);
         // q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans, itemblk3);
         headset.goTo(headrowno);
      }
   }

   public void countFindITEM3() {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      // Bug ID 45944, inoslk, start
      // q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getValue("N")));
      itemset3.clear();
   }

   public void newRowITEM3() {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM3", "DOC_STRUCTURE_API.New__", itemblk3);
      cmd.setOption("ACTION", "PREPARE");
      cmd.setParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      itemset3.addRow(data);

      if (bItem3Duplicate) {
         data = itemset3.getRow();
         trans.clear();
         cmd = trans.addCustomFunction("GETTITLE", "DOC_TITLE_API.Get_Title",
               "SSUBDOCTITLE");
         cmd.addParameter("SUB_DOC_CLASS", itemset3.getValue("SUB_DOC_CLASS"));
         cmd.addParameter("SUB_DOC_NO", itemset3.getValue("SUB_DOC_NO"));
         cmd = trans.addCustomFunction("GETNOSUBDOCS",
               "DOC_STRUCTURE_API.Number_Of_Children_", "NNOOFCHILDREN");
         cmd.addParameter("SUB_DOC_CLASS", itemset3.getValue("SUB_DOC_CLASS"));
         cmd.addParameter("SUB_DOC_NO", itemset3.getValue("SUB_DOC_NO"));
         cmd.addParameter("SUB_DOC_SHEET", itemset3.getValue("SUB_DOC_SHEET"));
         cmd.addParameter("SUB_DOC_REV", itemset3.getValue("SUB_DOC_REV"));
         trans = mgr.perform(trans);
         data.setFieldItem("SSUBDOCTITLE",
               trans.getValue("GETTITLE/DATA/SSUBDOCTITLE"));
         data.setFieldItem("NNOOFCHILDREN",
               trans.getValue("GETNOSUBDOCS/DATA/NNOOFCHILDREN"));
         itemset3.setRow(data);
         trans.clear();
      }
   }

   public void newRowITEM4() {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM4", "DOC_STRUCTURE_API.New__", itemblk4);
      cmd.setOption("ACTION", "PREPARE");
      cmd.setParameter("SUB_DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("SUB_DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("SUB_DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("SUB_DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM4/DATA");
      itemset4.addRow(data);
   }

   public void duplicateRowITEM4() {
      if (itemlay4.isMultirowLayout())
         itemset4.goTo(itemset4.getRowSelected());

      ASPBuffer data = itemset4.getRow();
      itemset4.addRow(data);
      itemlay4.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
   }

   public void okFindITEM4() {
      ASPManager mgr = getASPManager();

      if (tabs.getActiveTab() == 3) {
         if (headset.countRows() == 0)
            return;

         trans.clear();
         q = trans.addQuery(itemblk4);
         // q.addWhereCondition("SUB_DOC_CLASS = ? AND SUB_DOC_NO = ? AND SUB_DOC_SHEET = ? AND SUB_DOC_REV = ?");
         q.addParameter("SUB_DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("SUB_DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("SUB_DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("SUB_DOC_REV", headset.getValue("DOC_REV"));
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans, itemblk4);
         headset.goTo(headrowno);
      }
   }

   public void countFindITEM4() {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk4);
      q.setSelectList("to_char(count(*)) N");
      // q.addWhereCondition("SUB_DOC_CLASS = ? AND SUB_DOC_NO = ? AND SUB_DOC_SHEET = ? AND SUB_DOC_REV = ?");
      q.addParameter("SUB_DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("SUB_DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("SUB_DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("SUB_DOC_REV", headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay4.setCountValue(toInt(itemset4.getValue("N")));
      itemset4.clear();
   }

   public void cancelFind() {
      ASPManager mgr = getASPManager();
      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }

   public boolean isStructure(String docClass, String docNo) {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("TITLESTRUCTURE",
            "Doc_Title_Api.Get_Structure_", "STRUCTURE");
      cmd.addParameter("DOC_CLASS", docClass);
      cmd.addParameter("DOC_NO", docNo);

      trans = mgr.perform(trans);
      double structure_type = trans
            .getNumberValue("TITLESTRUCTURE/DATA/STRUCTURE");
      trans.clear();

      return ((int) structure_type == 1);

   }

   public void saveReturnITEM3() throws FndException {
      ASPManager mgr = getASPManager();

      if (ctx.readFlag("REPLACE_REVISION", false)) {
         // Get keys of revision being replaced..

         ctx.writeValue("REPLACE_DOC_CLASS", itemset3.getValue("SUB_DOC_CLASS"));
         ctx.writeValue("REPLACE_DOC_NO", itemset3.getValue("SUB_DOC_NO"));
         ctx.writeValue("REPLACE_DOC_SHEET", itemset3.getValue("SUB_DOC_SHEET"));
         ctx.writeValue("REPLACE_DOC_REV", itemset3.getValue("SUB_DOC_REV"));

         itemset3.changeRow();

         // Save replacing revision's data from the current to the current row..
         // check if the replace doc is of structure type
         if (this.isStructure(itemset3.getValue("SUB_DOC_CLASS"),
               itemset3.getValue("SUB_DOC_NO"))) {

            replaceRevisionFinish();
         } else {
            bConfiramtion4SettingStructureType = true;
            ctx.writeValue("CONFIRMFUNC", "replaceRevInStructure()");
            ctx.writeValue("UNCONFIRMFUNC", "okFindITEM3()");
            // bConfirmEx = true;
            sMessage = mgr
                  .translate("MADESTRUCTURETYPE: The Document Revision you have selected to replace the source document will be made Structure Type.\\nPress Ok to continue.\\nPress Cancel to abort this operation.");
         }
      } else {
         int head_row = headset.getCurrentRowNo();
         int row = itemset3.getCurrentRowNo();
         itemset3.changeRow();
         mgr.submit(trans);
         headset.goTo(head_row);
         itemset3.goTo(row);
      }

      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE"))) {
         // Repopulate the structure..
         int row = headset.getCurrentRowNo();
         headset.first();
         findDocumentsInStructure(headset.getValue("DOC_CLASS"),
               headset.getValue("DOC_NO"), headset.getValue("DOC_SHEET"),
               headset.getValue("DOC_REV"));
         headset.goTo(row);

         // Refresh the navigator to reflect the new/changed revision..
         appendDirtyJavaScript("window.parent.DocStructureNavigatorTree.refreshNavigator();\n");
      }
   }

   public void saveReturnITEM() {
      ASPManager mgr = getASPManager();

      String head_lu_name = headset.getValue("LU_NAME");
      String head_key_ref = headset.getValue("KEY_REF");

      trans.clear();
      cmd = trans.addCustomCommand("CHECKAPP",
            "APPROVAL_ROUTING_API.Check_App_Profile");
      cmd.addParameter("DUMMY1");
      cmd.addParameter("DUMMY2");
      cmd.addParameter("LU_NAME", head_lu_name);
      cmd.addParameter("KEY_REF", head_key_ref);
      cmd.addParameter("PROFILE_ID", mgr.readValue("PROFILE_ID"));
      trans = mgr.perform(trans);

      String persons = trans.getValue("CHECKAPP/DATA/DUMMY1");
      String groups = trans.getValue("CHECKAPP/DATA/DUMMY2");

      if (!mgr.isEmpty(persons) || !mgr.isEmpty(groups)) {
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUEAPPTEMPLATEACCESS: The user(s) and/or Group(s) in the Approval Template who didn't have View Access to the document(s) have been given that right. Please verify that this is correct under the Access tab."));
      }

      int row = headset.getCurrentRowNo();
      trans.clear();

      headset.changeRow();
      mgr.submit(trans);

      headset.goTo(row);
      performRefreshParent();
   }

   boolean isEqual(String s1, String s2) {
      ASPManager mgr = getASPManager();
      if (mgr.isEmpty(s1)) {
         if (mgr.isEmpty(s2)) {
            return true;
         }
         return false;
      }
      return (s1.equals(s2));
   }

   // Bug Id 73162 End

   public void saveLast() {
      ASPManager mgr = getASPManager();

      trans.clear();
      int currrow = headset.getCurrentRowNo();
      mgr.submit(trans);
      trans.clear();
      headset.goTo(currrow);
      okFindITEM6();

   }

   public void okFindITEM6() {
      ASPManager mgr = getASPManager();
      if (tabs.getActiveTab() == 4) {
         if (headset.countRows() == 0)
            return;

         trans.clear();
         q = trans.addQuery(itemblk6);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
         q.setOrderByClause("ACCESS_OWNER DESC, GROUP_ID, PERSON_ID");
         q.setBufferSize(1000);
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
   }

   public void countFindITEM6() {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk6);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay6.setCountValue(toInt(itemset6.getValue("N")));
      itemset6.clear();
   }

   public void newRowITEM6() {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM6", "DOCUMENT_ISSUE_ACCESS_API.New__",
            itemblk6);
      cmd.setOption("ACTION", "PREPARE");
      cmd.setParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM6/DATA");
      itemset6.addRow(data);

      boolean lay_mode = itemtbl6.isEditable();

      if (bItem6Duplicate) {
         data = itemset6.getRow();
         trans.clear();
         cmd = trans.addCustomFunction("GETNAME", "PERSON_INFO_API.Get_Name",
               "PERSONNAME");
         cmd.addParameter("ITEM6_PERSON_ID");
         cmd = trans.addCustomFunction("GETUSER",
               "PERSON_INFO_API.Get_User_Id", "PERSONUSERID");
         cmd.addParameter("ITEM6_PERSON_ID");
         cmd = trans.addCustomFunction("GETGROUPDES",
               "DOCUMENT_GROUP_API.Get_Group_Description", "GROUPDESCRIPTION");
         cmd.addParameter("ITEM6_GROUP_ID");
         trans = mgr.perform(trans);
         data.setFieldItem("PERSONNAME",
               trans.getValue("GETNAME/DATA/PERSONNAME"));
         data.setFieldItem("PERSONUSERID",
               trans.getValue("GETUSER/DATA/PERSONUSERID"));
         data.setFieldItem("GROUPDESCRIPTION",
               trans.getValue("GETGROUPDES/DATA/GROUPDESCRIPTION"));
         itemset6.setRow(data);
         trans.clear();
      }
   }

   public void deleteRowITEM6() {
      ASPManager mgr = getASPManager();

      if (itemlay6.isMultirowLayout()) {
         itemset6.storeSelections();
         itemset6.setFilterOn();
      }

      trans.clear();
      cmd = trans.addCustomCommand("CHECKDEL",
            "DOCUMENT_ISSUE_ACCESS_API.REMOVE__");
      cmd.addParameter("INFO", null);
      cmd.addParameter("OBJID", itemset6.getValue("OBJID"));
      cmd.addParameter("OBJVERSION", itemset6.getValue("OBJVERSION"));
      cmd.addParameter("ACTION", "CHECK");
      trans = mgr.perform(trans);

      String sInfo = trans.getValue("CHECKDEL/DATA/INFO");
      trans.clear();
      itemset6.setFilterOff();
      if (!mgr.isEmpty(sInfo)) {
         bConfirm = true;
         sMessage = DocmawUtil.getItemValue(sInfo, "WARNING");
         ctx.writeValue("CONFIRMFUNC", "deleteITEM6Last()");
         ctx.writeValue("UNCONFIRMFUNC", "onUnconfirm()");
      } else
         deleteITEM6Last();
   }

   public void deleteITEM6Last() {
      ASPManager mgr = getASPManager();

      trans.clear();
      itemset6.setSelectedRowsRemoved();
      int currrow = headset.getCurrentRowNo();
      mgr.submit(trans);
      trans.clear();
      headset.goTo(currrow);
   }

   public String generateHistoryWhereCond() {

      switch (sHistoryMode.charAt(0)) {
      case '1':
         return " info_category_db IN ('CHECKIN', 'CHECKOUT', 'UNRESERVED','DELETEFILE')";
      case '2':
         return " info_category_db = 'ACCESS'";
      case '3':
         return " info_category_db NOT IN ('ACCESS', 'CHECKIN', 'CHECKOUT', 'UNRESERVED', 'INFO', 'APPROVED', 'REJECTED', 'APPROVALSTEPREMOVED','DELETEFILE')";
      case '4':
         return " info_category_db IN ('APPROVED', 'REJECTED','APPROVALSTEPREMOVED')";
      case '5':
         return " info_category_db = 'INFO' ";
      case '6':
         return " info_category_db IN ('CONNECTED', 'DISCONNECTED')";
      default:
         return "";
      }
   }

   public void historyModeChanged() {
      ASPManager mgr = getASPManager();

      sHistoryMode = mgr.readValue("HISTORY_MODE");
      okFindITEM7();
   }

   public void okFindITEM7() {
      ASPManager mgr = getASPManager();

      if (tabs.getActiveTab() == 5) {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addQuery(itemblk7);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
         q.addWhereCondition(generateHistoryWhereCond()); // SQLInjections_Safe
                                                          // AMNILK 20070810
         q.setOrderByClause("LINE_NO DESC");
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans, itemblk7); // Bug Id 78853
         headset.goTo(headrowno);
      }
   }

   public void countFindITEM7() {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk7);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      q.addWhereCondition(generateHistoryWhereCond()); // SQLInjections_Safe
                                                       // AMNILK 20070810
      mgr.submit(trans);
      itemlay7.setCountValue(toInt(itemset7.getValue("N")));
      itemset7.clear();
   }

   public void newRowITEM7() {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM7", "DOCUMENT_ISSUE_HISTORY_API.New__",
            itemblk7);
      cmd.setOption("ACTION", "PREPARE");
      cmd.setParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM7/DATA");
      itemset7.addRow(data);
   }

   public void countFindITEM8() {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk9);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay9.setCountValue(toInt(itemset9.getValue("N")));
      itemset9.clear();
   }

   public void okFindITEM9() {
      ASPManager mgr = getASPManager();

      if (tabs.getActiveTab() == 6) {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addQuery(itemblk9);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
   }

   public void okFindITEM11()
   {
      ASPManager mgr = getASPManager();
      
      if (headset.countRows() == 0)
         return;
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(itemblk11);
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans, itemblk11);
      headset.goTo(headrowno);
   }
   
   
   public void okFindITEM13() {
      ASPManager mgr = getASPManager();
      
      if (headset.countRows() == 0)
         return;
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(itemblk13);
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? ");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addWhereCondition("OBJSTATE ='Obsolete'");
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans, itemblk13);
      headset.goTo(headrowno);
   }
   

   public void newRowITEM11() {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("ITEM11", "DOC_ISSUE_TARGET_ORG_API.New__",
            itemblk11);
      cmd.setOption("ACTION", "PREPARE");
      cmd.setParameter("ITEM11_DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("ITEM11_DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("ITEM11_DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("ITEM11_DOC_REV", headset.getValue("DOC_REV"));
      cmd.setParameter("ITEM11_SIGN_STATUS", "FALSE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM11/DATA");
      itemset11.addRow(data);
   }
   
   public void okFindCallback()
   {
      ASPManager mgr = getASPManager();
      
      if (headset.countRows() == 0)
         return;
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(doc_issue_callback_blk);
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,doc_issue_callback_blk);
      headset.goTo(headrowno);
   }
   
   public void newRowCallback()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("CALLBACK","DOC_ISSUE_CALLBACK_API.New__",doc_issue_callback_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("CALLBACK_DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("CALLBACK_DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("CALLBACK_DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("CALLBACK_DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("CALLBACK/DATA");
      doc_issue_callback_set.addRow(data);
   }
   
   public void okFindDistribution()
   {
      ASPManager mgr = getASPManager();
      
      if (headset.countRows() == 0)
         return;
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(doc_issue_distribution_blk);
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      q.setOrderByClause("DIST_NO");
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,doc_issue_distribution_blk);
      headset.goTo(headrowno);
   }
   

   protected void initializeSession() throws FndException {
      ASPManager mgr = getASPManager();
      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);

      sCheckedIn = "Checked In";
      sCheckedOut = dm_const.edm_file_check_out;
      sPrelimin = dm_const.doc_issue_preliminary;
      sAppInProg = dm_const.doc_issue_approval_in_progress;
      sApproved = dm_const.doc_issue_approved;
      sObsolete = dm_const.doc_issue_obsolete;
      sReleased = dm_const.doc_issue_released;
      dGroup = dm_const.doc_user_access_group;
      dAll = dm_const.doc_user_access_all;
      dUser = dm_const.doc_user_access_user;

      String initialised = ctx.readValue("INITIALISED", null);
      String fnd_user = ctx.readValue("FNDUSER", null);
      // Bug 57779, Start
      String person_id = ctx.readValue("PERSONID", null);
      boolean docman_admin = ctx.readFlag("DOCMAN_ADMIN", false);
      // Bug 57779, End
      boolean show_user_settings = ctx.readFlag("SHOW_USER_SETTINGS", false);
      boolean show_mandatory_settings = ctx.readFlag("SHOW_MANDATORY_SETTINGS",
            false); // Bug Id 67105

      if (mgr.isEmpty(initialised)) {
         trans.clear();
         cmd = trans.addCustomFunction("FNDUSER",
               "Fnd_Session_Api.Get_Fnd_User", "DUMMY1");
         // Bug 57779, Start
         cmd = trans.addCustomFunction("DOCMAN_ADMIN",
               "Docman_Security_Util_API.Check_Docman_Administrator", "DUMMY1");
         // Bug 57779, End
         cmd = trans.addCustomFunction("PERSONID",
               "Person_Info_API.Get_Id_For_User", "DUMMY1");
         cmd.addParameter("USER_ID", fnd_user);

         trans.addPresentationObjectQuery("DOCMAW/dlgUserSettings.page,DOCMAW/MandatorySearchFieldsConfigDlg.page");// Bug
                                                                                                                    // Id
                                                                                                                    // 67105,
                                                                                                                    // Added
                                                                                                                    // MandatorySearchFieldsConfigDlg.page
         trans = mgr.perform(trans);

         fnd_user = trans.getValue("FNDUSER/DATA/DUMMY1");
         // Bug 57779, Start
         person_id = trans.getValue("PERSONID/DATA/DUMMY1");
         docman_admin = "TRUE".equals(trans
               .getValue("DOCMAN_ADMIN/DATA/DUMMY1"));
         // Bug 57779, End

         if (trans.getSecurityInfo().namedItemExists(
               "DOCMAW/dlgUserSettings.page"))
            show_user_settings = true;
         else
            show_user_settings = false;

         // Bug Id 67105, Start
         if (trans.getSecurityInfo().namedItemExists(
               "DOCMAW/MandatorySearchFieldsConfigDlg.page"))
            show_mandatory_settings = true;
         else
            show_mandatory_settings = false;
         // Bug Id 67105, End
      }

      ctx.writeValue("FNDUSER", fnd_user);
      // Bug 57779, Start
      ctx.writeFlag("DOCMAN_ADMIN", docman_admin);
      // Bug 57779, End
      ctx.writeValue("PERSONID", person_id);
      ctx.writeFlag("SHOW_USER_SETTINGS", show_user_settings);
      ctx.writeFlag("SHOW_MANDATORY_SETTINGS", show_mandatory_settings);// Bug
                                                                        // Id
                                                                        // 67105
      ctx.writeValue("MODE", mgr.readValue("MODE", ctx.readValue("MODE", "")));
   }

   /**
    * isValidColumnValue() validates the specified column. The validation
    * depends on whether match is true or false:
    * 
    * - match is true : the method returns true if the value of the parameters,
    * column and validity_check, always match. - match is false : the method
    * returns true if the value of the parameters, column and validity_check,
    * always mismatch Works for single and multiple rows
    */
   public boolean isValidColumnValue(String column, String validity_check,
         boolean match) {
      boolean invalid = true;
      int noOFSelectedRows = 1;

      if (headlay.isSingleLayout()) {
         headset.selectRow();
      } else {
         headset.storeSelections();
         headset.setFilterOn();
         noOFSelectedRows = headset.countRows();
      }

      for (int k = 0; k < noOFSelectedRows; k++) {
         // Added by Terry 20130314
         // Page speed.
         String doc_class = headset.getRow().getValue("DOC_CLASS");
         String doc_no = headset.getRow().getValue("DOC_NO");
         String doc_sheet = headset.getRow().getValue("DOC_SHEET");
         String doc_rev = headset.getRow().getValue("DOC_REV");
         String column_value;
         if ("GETVIEWACCES".equals(column))
            column_value = getViewAccess(doc_class, doc_no, doc_sheet, doc_rev);
         else if ("GETEDITACCESS".equals(column))
            column_value = getEditAccess(doc_class, doc_no, doc_sheet, doc_rev);
         else
            column_value = headset.getRow().getValue(column);
         // Added end
         // Modified by Terry 20130314
         // Original:
         // if ((match &&
         // !validity_check.equals(headset.getRow().getValue(column))) ||
         // (!match &&
         // validity_check.equals(headset.getRow().getValue(column))))
         if ((match && !validity_check.equals(column_value))
               || (!match && validity_check.equals(column_value)))
         // Modified end
         {
            invalid = false;
            break;
         }
         if (headlay.isMultirowLayout()) {
            headset.next();
         }
      }

      if (headlay.isMultirowLayout()) {
         headset.setFilterOff();
      }
      return invalid;
   }

   /**
    * isValidColumnValue() validates the specified column. The validation
    * depends on whether match is true or false:
    * 
    * - match is true : the method returns true if the value of the parameters,
    * column and validity_check1 *or* validity_check2, always match. - match is
    * false : the method returns true if the value of the parameters, column and
    * validity_check1 *or* validity_check2, always mismatch Works for single and
    * multiple rows
    */
   public boolean isValidColumnValue(String column, String validity_check1,
         String validity_check2, boolean match) {

      boolean invalid = true;
      int noOFSelectedRows = 1;

      if (headlay.isSingleLayout()) {
         headset.selectRow();
      } else {
         headset.storeSelections();
         headset.setFilterOn();
         noOFSelectedRows = headset.countRows();
      }
      for (int k = 0; k < noOFSelectedRows; k++) {
         if ((match && (!validity_check1.equals(headset.getRow().getValue(
               column)) && !validity_check2.equals(headset.getRow().getValue(
               column))))
               || (!match && (validity_check1.equals(headset.getRow().getValue(
                     column)) || validity_check2.equals(headset.getRow()
                     .getValue(column))))) {
            invalid = false;
            break;
         }
         if (headlay.isMultirowLayout()) {
            headset.next();
         }

      }
      if (headlay.isMultirowLayout()) {
         headset.setFilterOff();
      }
      return invalid;
   }

   /**
    * isEmptyColumnValue() checks to see if the specified column is empty.
    * 
    * @param column
    * 
    * @return Returns true if the value of the specified column in empty for any
    *         of the rows, false otherwise
    */
   public boolean isEmptyColumnValue(String column) {

      ASPManager mgr = getASPManager();
      boolean empty = false;
      int noOfRowsSelected = 1;

      if (headlay.isSingleLayout()) {
         headset.selectRow();
      } else {
         headset.selectRows();
         headset.setFilterOn();
         noOfRowsSelected = headset.countRows();
      }

      for (int k = 0; k < noOfRowsSelected; k++) {
         if (mgr.isEmpty(headset.getRow().getValue(column))) {
            empty = true;
            break;
         }
         if (headlay.isMultirowLayout()) {
            headset.next();
         }
      }

      if (headlay.isMultirowLayout()) {
         headset.setFilterOff();
      }
      return empty;
   }

   public void getClientConformation(String conf_msg, String calling_method) {
      bConfirm = true;
      sMessage = conf_msg;
      ctx.writeValue("CONFIRMFUNC", calling_method);
   }

   public void getClientConformationEx(String conf_msg, String calling_method) {
      bConfirmEx = true;
      sMessage = conf_msg;
      ctx.writeValue("CONFIRMFUNC", calling_method);
   }

   // Bug Id 70808, Start
   private int getLimitForBGJob() {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPCommand cmdBGJob = trans.addCustomFunction("GETLIMITFORGJOB",
            "DOCMAN_DEFAULT_API.Get_Default_Value_", "DUMMY3");
      cmdBGJob.addParameter("DUMMY1", "DocIssue");
      cmdBGJob.addParameter("DUMMY2", "LIMIT_NORMAL_STATE_CHANGES");
      trans = mgr.perform(trans);
      int limit = Integer.parseInt(trans
            .getValue("GETLIMITFORGJOB/DATA/DUMMY3"));
      return limit;
   }

   private void getSelectedDocsObjects() {
      int rowcount = 0;
      int jobcount = 0;
      int nBGJobs = 0;
      double rows = (double) headset.countSelectedRows();
      double num = rows / BACKGROUNDJOB_LIMIT;
      int numOfBGJ = (int) Math.ceil(num);
      objArray = new String[numOfBGJ];

      headset.setFilterOn();
      headset.first();
      do {
         if (rowcount == BACKGROUNDJOB_LIMIT) {
            rowcount = 0;
            jobcount++;
         }
         if (rowcount == 0) {
            objArray[jobcount] = "";
         }
         objArray[jobcount] += headset.getRow().getValue("OBJID") + "^";
         rowcount++;
      } while (headset.next());
   }

   // Bug Id 70808, End

/*   public void createNewSheet() {
      ASPManager mgr = getASPManager();
      String userAccess;

      if (headlay.isMultirowLayout()) {
         headset.storeSelections();
         headset.setFilterOn();
      } else {
         headset.unselectRows();
         headset.selectRow();
      }

      headset.refreshRow();
      String fnd_user = getFndUser();

      // Modified by Terry 20120927
      // Original:
      
       * // Bug id 88317 start
       * 
       * trans.clear();
       * 
       * cmd = trans.addCustomFunction("FNDUSER",
       * "Fnd_Session_API.Get_Fnd_User", "DUMMY2"); cmd =
       * trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User",
       * "DUMMY1"); cmd.addReference("DUMMY2", "FNDUSER/DATA");
       * 
       * trans = mgr.perform(trans); String person_id =
       * trans.getValue("STARUSER/DATA/DUMMY1");
       * 
       * 
       * if ("*".equals(person_id)){ mgr.showAlert(mgr.translate(
       * "DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."
       * )); return; }
       
      // Bug id 88317 end
      // Modified end

      // Added by Terry 20130326
      String edm_db_state = findEdmFileNoState(
            headset.getRow().getValue("DOC_CLASS"),
            headset.getRow().getValue("DOC_NO"),
            headset.getRow().getValue("DOC_SHEET"),
            headset.getRow().getValue("DOC_REV"), sCheckedIn);
      if ("TRUE".equals(edm_db_state)) {
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUECREATENEWSHEETCHECKINNOT: You can not create a new sheet for document, there are some files need to check in.")); // Bug
                                                                                                                                                           // Id
                                                                                                                                                           // 76849
         headset.setFilterOff();
         return;
      }
      // Added end

      if ("TRUE".equals(headset.getRow().getValue("STRUCTURE"))) {
         mgr.showAlert("DOCMAWDOCISSUECANNOTCREATENEWSHEETFROMSTRUCTURE: You cannot create a new sheet for a structure document.");
         return;
      }

      if (dGroup.equals(headset.getRow().getValue("ACCESS_CONTROL"))) {
         trans.clear();
         cmd = trans.addCustomFunction("USERACCESS",
               "DOCUMENT_ISSUE_ACCESS_API.User_Get_Access", "USERGETACCESS");
         cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
         cmd.addParameter("LOGUSER", fnd_user);
         trans = mgr.perform(trans);

         userAccess = trans.getValue("USERACCESS/DATA/USERGETACCESS");
         trans.clear();
      } else if (dAll.equals(headset.getRow().getValue("ACCESS_CONTROL")))
         userAccess = "EDIT";
      else {
         // Bug 57779, Start
         if (isUserDocmanAdministrator()
               || (headset.getRow().getValue("USER_CREATED").equals(fnd_user)))
            // Bug 57779, End

            userAccess = "EDIT";
         else
            userAccess = "";
      }

      if (!("EDIT".equals(userAccess))) {
         mgr.showAlert("DOCMAWDOCISSUENORIGHTSTOCREATENEWSHEET: You must have edit access to be able to create a new sheet to this document.");
         headset.setFilterOff();
         headset.unselectRows();
         return;
      }

      // to preserve the record set :bakalk
      data = headset.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

      if (headlay.isMultirowLayout()) {
         headset.storeSelections();
         headset.setFilterOn();
      } else {
         headset.unselectRows();
         headset.selectRow();
      }

      headset.refreshRow();

      String doc_no = headset.getValue("DOC_NO");
      String doc_class = headset.getValue("DOC_CLASS");
      String doc_sheet = headset.getValue("DOC_SHEET");
      String doc_rev = headset.getValue("DOC_REV");
      String url = mgr.getURL();

      mgr.transferDataTo(
            "NewSheetWizard.page?DOC_NO=" + mgr.URLEncode(doc_no)
                  + "&DOC_CLASS=" + mgr.URLEncode(doc_class) + "&DOC_SHEET="
                  + mgr.URLEncode(doc_sheet) + "&DOC_REV="
                  + mgr.URLEncode(doc_rev) + "&SEND_URL=" + mgr.URLEncode(url),
            data);
      headset.setFilterOff();
      headset.unselectRows();
   }
*/
   /**
    * Sets the selected document(s) to state 'Obsolete'.
    * 
    */
   public void setObsolete() {
      ASPManager mgr = getASPManager();
      int curr_row = 0;

      if (headlay.isMultirowLayout()) {
         headset.storeSelections();
         if (headset.countSelectedRows() == 0) {
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
            return;
         }
      } else {
         headset.selectRow();
         curr_row = headset.getCurrentRowNo();
      }

      if (!client_confirmation
            && !isValidColumnValue("STATE", sObsolete, sAppInProg, false))
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUEOBSOORAPPINPROGRESSCANNOTOBSO: Documents in state Obsolete or Approval In Progress cannot be set to obsolete."));
      else {
         if (!client_confirmation) {
            getClientConformation(
                  mgr.translate("DOCMAWDOCISSUESETTOOBSOLETE: Do you wish set the document to obsolete?"),
                  "setObsolete();");
            headset.setFilterOff();
         } else {
            headset.refreshAllRows();
            if (headset.countSelectedRows() > 1
                  && ctx.readFlag("CONFIRM_OBSOLETING_STRUCTURE", true)) {
               getClientConformation(
                     mgr.translate("DOCMAWDOCISSUEWARNSETMULTIPLESTRUCTURETOOBSOLETE: Warning: one or more of the documents you are trying to set to obsolete is part of a document structure. Do you want to continue with this operation?"),
                     "setObsolete();");
               ctx.writeFlag("CONFIRM_OBSOLETING_STRUCTURE", false);
            } else if (ctx.readFlag("CONFIRM_OBSOLETING_STRUCTURE", true)) {
               getClientConformation(
                     mgr.translate("DOCMAWDOCISSUEWARNSETSTRUCTURETOOBSOLETE: Warning: the document you are trying to set to obsolete is part of a document structure. Do you want to continue with this operation?"),
                     "setObsolete();");
               ctx.writeFlag("CONFIRM_OBSOLETING_STRUCTURE", false);
            } else {
               trans.clear();
               if (headlay.isSingleLayout())
                  headset.markRow("PROMOTE_TO_OBSOLETE__");
               else {
                  headset.setFilterOn();
                  markSelection("PROMOTE_TO_OBSOLETE__");
                  headset.setFilterOff();
               }

               int row = headset.getCurrentRowNo();
               mgr.submit(trans);
               client_confirmation = false;
               headset.goTo(row);
            }

            headset.setFilterOff();
            headset.goTo(curr_row);
         }
      }
   }

   public void onUnconfirm() {
      bConfirm = false;
      sMessage = "";
      ctx.writeValue("CONFIRMFUNC", "");
   }

   // Adds the newly created revision and repopulates the record set.
   public void addNewRev() {
      ASPManager mgr = getASPManager();

      ASPBuffer rec_set = headset.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

      ASPBuffer temp_buff = rec_set.getBufferAt(0).copy();

      int currrow = headset.getCurrentRowNo();
      int newrow = 0;
      boolean created_rev_found = false;

      temp_buff.setValueAt(0,
            mgr.URLDecode(mgr.readValue("DOC_CLASS_FROM_WIZ")));
      temp_buff.setValueAt(1, mgr.URLDecode(mgr.readValue("DOC_NO_FROM_WIZ")));
      temp_buff.setValueAt(2,
            mgr.URLDecode(mgr.readValue("DOC_SHEET_FROM_WIZ")));
      temp_buff.setValueAt(3, mgr.URLDecode(mgr.readValue("DOC_REV_FROM_WIZ")));
      rec_set.addBuffer("DATA", temp_buff);

      trans.clear();
      q = trans.addEmptyQuery(headblk);
      q.addOrCondition(rec_set);
      q.setOrderByClause("DT_CRE DESC");
      q.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);

      if (headlay.isSingleLayout()) {
         headset.goTo(currrow);

         do {
            if ((headset.getRow().getValue("DOC_CLASS").equals(temp_buff
                  .getValueAt(0)))
                  && (headset.getRow().getValue("DOC_NO").equals(temp_buff
                        .getValueAt(1)))
                  && (headset.getRow().getValue("DOC_SHEET").equals(temp_buff
                        .getValueAt(2)))
                  && (headset.getRow().getValue("DOC_REV").equals(temp_buff
                        .getValueAt(3)))) {
               newrow = headset.getCurrentRowNo();
               created_rev_found = true;
            }
            headset.previous();
         } while (!created_rev_found);

         headset.goTo(newrow);
      }

      // Refresh all child blocks
      okFindITEM2();
      okFindITEM3();
      okFindITEM4();
      okFindITEM6();
      okFindITEM7();
      okFindITEM9();
   }

   public void transferToEdmMacro(ASPBuffer buff)
   {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout()) {
         headset.unselectRows();
         headset.selectRow();
      } else
         headset.selectRows();

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buff, data);
      bTranferToEDM = true;
   }
   
   public void transferDSToEdmMacro(ASPBuffer buff)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      if (headlay.isSingleLayout())
      {
         headset.unselectRows();
         headset.selectRow();
      } else
         headset.selectRows();

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,DOC_CODE");
      Buffer data_buff = data.getBuffer();
      Buffer transfer_buff = data_buff.newInstance();
      
      ASPBuffer main_doc_folder = mgr.newASPBuffer();
      
      for (int i = 0; i < data_buff.countItems(); i++)
      {
         ifs.fnd.buffer.Item one_row = data_buff.getItem(i);
         if ("DATA".equals(one_row.getName()))
         {
            Buffer org = one_row.getBuffer();
            String doc_folder = org.getString(4);
            org.removeItem(4);
            org.addItem("DOC_FOLDER", doc_folder);
            transfer_buff.addItem("DATA", org);
            // Save folder of main doc
            main_doc_folder.addItem(i + "", doc_folder);
            
            ASPQuery qry = trans.addQuery("GET_STRUCTURE" + i, 
                  "SELECT sub_doc_class doc_class, sub_doc_no doc_no, sub_doc_sheet doc_sheet, sub_doc_rev doc_rev " +
                  "FROM   doc_structure " +
                  "WHERE  doc_class = ? " +
                  "AND    doc_no = ? " +
                  "AND    doc_sheet = ? " +
                  "AND    doc_rev = ? " +
                  "AND    associate_id = 'Transfer' " +
                  "AND    Edm_File_API.Have_Edm_File(sub_doc_class, sub_doc_no, sub_doc_sheet, sub_doc_rev) = 'TRUE' " +
                  "AND    Doc_Class_API.Get_Temp_Doc(sub_doc_class) = 'FALSE'");
            qry.addParameter("DOC_CLASS", (String)org.getItem(0).getValue());
            qry.addParameter("DOC_NO", (String)org.getItem(1).getValue());
            qry.addParameter("DOC_SHEET", (String)org.getItem(2).getValue());
            qry.addParameter("DOC_REV", (String)org.getItem(3).getValue());
            qry.includeMeta("ALL");
            qry.setBufferSize(10000);
         }
      }
      
      trans = mgr.perform(trans);
      
      for (int i = 0; i < data_buff.countItems(); i++)
      {
         ifs.fnd.buffer.Item one_row = data_buff.getItem(i);
         if ("DATA".equals(one_row.getName()))
         {
            ASPBuffer doc_buf = trans.getBuffer("GET_STRUCTURE" + i);
            
            // Get saved main doc folder
            String doc_folder = main_doc_folder.getValue(i + "");
            
            // remove the last INFO item from this buffer..
            doc_buf.removeItemAt(doc_buf.countItems() - 1);
            Buffer ds_buff = doc_buf.getBuffer();
            
            for (int j = 0; j < ds_buff.countItems(); j++)
            {
               ifs.fnd.buffer.Item ds_one_row = ds_buff.getItem(j);
               Buffer sub_doc = ds_one_row.getBuffer();
               if (!mgr.isEmpty(doc_folder))
                  sub_doc.addItem("DOC_FOLDER", doc_folder);
               transfer_buff.addItem("DATA", sub_doc);
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

   /**
    * Builds a url containing all selected rows to EdmMacro for execution. The
    * fields selected for each row are DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV and
    * EDM_FILE_TYPE where EDM_FILE_TYPE is the file type of the original
    * document
    */
   public void transferToEdmMacro(String doc_type, String action) {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout()) {
         headset.unselectRows();
         headset.selectRow();
      } else
         headset.selectRows();

      ASPBuffer data = headset
            .getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr,
            "EdmMacro.page", action, doc_type, data);
      bTranferToEDM = true;
   }

   public void checkEnableHistoryNewRow() {

      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("GETCLIENTVAL",
            "DOCUMENT_ISSUE_HISTORY_CAT_API.DECODE", "DUMMY1");
      cmd.addParameter("ITEM7_DUMMY1", "INFO");
      trans = mgr.perform(trans);
      String infoCategory = trans.getValue("GETCLIENTVAL/DATA/DUMMY1");
      trans.clear();

      cmd = trans.addCustomFunction("GETLOGEVENT",
            "DOC_CLASS_HISTORY_SETTINGS_API.GET_LOGG_EVENT",
            "ITEM7_ENABLE_HISTORY_NEW");
      cmd.addParameter("INFO_CATEGORY", infoCategory);
      cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      trans = mgr.perform(trans);

      enableHistoryNewButton = trans
            .getValue("GETLOGEVENT/DATA/ITEM7_ENABLE_HISTORY_NEW");
      trans.clear();
   }

   protected String getFndUser() {
      return ctx.readValue("FNDUSER", null);
   }

   // Bug 57779, Start
   private boolean isUserDocmanAdministrator() {
      return ctx.readFlag("DOCMAN_ADMIN", false);
   }

   // Bug 57779, End

   private String getPersonId() {
      return ctx.readValue("PERSONID", null);
   }

   protected boolean isUserSettingsEnable() {
      return ctx.readFlag("SHOW_USER_SETTINGS", false);
   }

   // Bug Id 67105, Start
   protected boolean isMandatorySettingsEnable() {
      return ctx.readFlag("SHOW_MANDATORY_SETTINGS", false);
   }

   // Bug Id 67105, End

   public boolean isEditable() {
      int curr_row;
      if (headlay.isMultirowLayout())
         curr_row = headset.getRowSelected();
      else
         curr_row = headset.getCurrentRowNo();

      headset.goTo(curr_row);

      // Modified by Terry 20130314
      // Page speed.
      // Original:
      // if ("TRUE".equals(headset.getValue("GETEDITACCESS")))
      if ("TRUE".equals(getEditAccess(headset.getValue("DOC_CLASS"),
            headset.getValue("DOC_NO"), headset.getValue("DOC_SHEET"),
            headset.getValue("DOC_REV"))))
         // Modified end
         return true;
      else
         return false;
   }

   public boolean isViewable() {
      // Modified by Terry 20130314
      // Page speed.
      if ("TRUE".equals(getViewAccess(headset.getValue("DOC_CLASS"),
            headset.getValue("DOC_NO"), headset.getValue("DOC_SHEET"),
            headset.getValue("DOC_REV"))))
         return true;
      else
         return false;
      // Modified end
   }

   public boolean isSecurityCheckpointEnabled() {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("SECURITYCHECKPOINTREQ",
            "Doc_Class_Default_API.Get_Default_Value_", "DUMMY3");
      cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("DUMMY1", "DocIssue");
      cmd.addParameter("DUMMY2", "SET_APPROVED_SEC_CHKPT");

      ASPCommand cmdCheckActive = trans.addCustomFunction(
            "SECURITYCHECKPOINTACTIVE",
            "Security_SYS.Security_Checkpoint_Activated", "DUMMY3");
      cmdCheckActive.addParameter("DUMMY1", "DOCMAN_DOC_REV_SET_TO_APPROVED");

      trans = mgr.perform(trans);
      String security_checkpoint_req = trans
            .getValue("SECURITYCHECKPOINTREQ/DATA/DUMMY3");
      String secutiry_checkpoint_active = trans
            .getValue("SECURITYCHECKPOINTACTIVE/DATA/DUMMY3");
      trans.clear();

      if ("Y".equals(security_checkpoint_req)
            && "TRUE".equalsIgnoreCase(secutiry_checkpoint_active))
         return true;
      else
         return false;
   }

   public void viewOriginal() {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0) {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      /*
       * if (isEmptyColumnValue("CHECKED_IN_SIGN")) { if
       * (headlay.isMultirowLayout()) mgr.showAlert(mgr.translate(
       * "DOCMAWDOCISSUENOVIEWEMPTYFILEMULTI: One or more documents you're trying to view has no file checked in yet."
       * )); else mgr.showAlert(mgr.translate(
       * "DOCMAWDOCISSUENOVIEWEMPTYFILE: The document you're trying to view has no file checked in yet."
       * )); return; }
       */
      // Bug Id 67336, start
      if (checkFileOperationEnable()) {
         return;
      }
      // Bug Id 67336, end

      if (isValidColumnValue("GETVIEWACCES", "TRUE", true)) {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "VIEW");
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
         buff.addItem("LAUNCH_FILE", mgr.readValue("LAUNCH_FILE"));
         transferToEdmMacro(buff);
      } else {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSVIEWMULTI: You don't have view access to one or more of the selected documents.");
         else
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSVIEW: You must have view access to be able to view this document.");
      }
   }

   // Added by Terry 20121019
   // Download documents function
   public void downloadDocuments() {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0) {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      if (isValidColumnValue("GETVIEWACCES", "TRUE", true) && checkAccessExt(true, false)) {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "DOWNLOAD");
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("SAME_ACTION_TO_ALL", "YES");
         buff.addItem("LAUNCH_FILE", "NO");
         transferToEdmMacro(buff);
      } else {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENODOWNACCESSDOWNMULTI: You don't have download access to one or more of the selected documents.");
         else
            mgr.showAlert("DOCMAWDOCISSUENODOWNACCESSDOWN: You must have download access of this document.");
      }
   }

   public boolean checkAccessExt(boolean check_download, boolean check_print) {
      if (!check_download && !check_print)
         return true;

      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      // headset.store();
      String user_id = mgr.getFndUser();
      ASPBuffer sel_buf = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      int countRows = sel_buf.countItems();
      if (countRows > 0) {
         for (int i = 0; i < countRows; i++) {
            ASPBuffer subBuff = sel_buf.getBufferAt(i);
            String doc_class = subBuff.getValueAt(0);
            String doc_no = subBuff.getValueAt(1);
            String doc_sheet = subBuff.getValueAt(2);
            String doc_rev = subBuff.getValueAt(3);
            ASPCommand cmd = trans.addCustomCommand("USERGETACCEXT" + i, "Document_Issue_Access_API.User_Get_Access_Ext");
            cmd.addParameter("DUMMY_IN_1", doc_class);
            cmd.addParameter("DUMMY_IN_1", doc_no);
            cmd.addParameter("DUMMY_IN_1", doc_sheet);
            cmd.addParameter("DUMMY_IN_1", doc_rev);
            cmd.addParameter("DUMMY_IN_1", user_id);
            cmd.addParameter("DUMMY1");
            cmd.addParameter("DUMMY2");
         }
         trans = mgr.perform(trans);
         for (int i = 0; i < countRows; i++) {
            if (check_download) {
               String download_access = (mgr.isEmpty(trans
                     .getValue("USERGETACCEXT" + i + "/DATA/DUMMY1")) ? "FALSE"
                     : trans.getValue("USERGETACCEXT" + i + "/DATA/DUMMY1"));
               if ("FALSE".equals(download_access)) {
                  return false;
               }
            }

            if (check_print) {
               String print_access = (mgr.isEmpty(trans
                     .getValue("USERGETACCEXT" + i + "/DATA/DUMMY2")) ? "FALSE"
                     : trans.getValue("USERGETACCEXT" + i + "/DATA/DUMMY2"));
               if ("FALSE".equals(print_access)) {
                  return false;
               }
            }
         }
         return true;
      }
      return false;
   }
   
   public void downloadDocsAndSubDocs()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0) {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      if (isValidColumnValue("GETVIEWACCES", "TRUE", true) && checkDSDownloadAccess())
      {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "DOWNLOAD");
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("SAME_ACTION_TO_ALL", "YES");
         buff.addItem("LAUNCH_FILE", "NO");
         transferDSToEdmMacro(buff);
      }
      else
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENODOWNACCESSDOWNMULTI: You don't have download access to one or more of the selected documents.");
         else
            mgr.showAlert("DOCMAWDOCISSUENODOWNACCESSDOWN: You must have download access of this document.");
      }
   }
   
   public boolean checkDSDownloadAccess()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      // headset.store();
      String user_id = mgr.getFndUser();
      ASPBuffer sel_buf = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      int countRows = sel_buf.countItems();
      if (countRows > 0)
      {
         for (int i = 0; i < countRows; i++)
         {
            ASPBuffer subBuff = sel_buf.getBufferAt(i);
            String doc_class = subBuff.getValueAt(0);
            String doc_no = subBuff.getValueAt(1);
            String doc_sheet = subBuff.getValueAt(2);
            String doc_rev = subBuff.getValueAt(3);
            ASPCommand cmd = trans.addCustomFunction("USERGETACCEXT" + i, "Document_Issue_Access_API.Get_DS_Download_Access", "DUMMY_OUT_1");
            cmd.addParameter("DUMMY_IN_1", doc_class);
            cmd.addParameter("DUMMY_IN_1", doc_no);
            cmd.addParameter("DUMMY_IN_1", doc_sheet);
            cmd.addParameter("DUMMY_IN_1", doc_rev);
            cmd.addParameter("DUMMY_IN_1", user_id);
         }
         trans = mgr.perform(trans);
         for (int i = 0; i < countRows; i++)
         {
            String download_access = (mgr.isEmpty(trans.getValue("USERGETACCEXT" + i + "/DATA/DUMMY_OUT_1")) ? "FALSE" : trans.getValue("USERGETACCEXT" + i + "/DATA/DUMMY_OUT_1"));
            if ("FALSE".equals(download_access))
               return false;
         }
         return true;
      }
      return false;
   }

   private String getEdmFileState(String doc_class, String doc_no, String doc_sheet, String doc_rev) {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETFILESTATE",
            "Edm_File_API.Get_Doc_State_Db", "EDM_DB_STATE");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV", doc_rev);
      cmd.addParameter("DUMMY_IN_1", "ORIGINAL");
      trans = mgr.perform(trans);
      return trans.getValue("GETFILESTATE/DATA/EDM_DB_STATE");
   }

   private String haveEdmFile(String doc_class, String doc_no,
         String doc_sheet, String doc_rev) {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("HAVEEDMFILE",
            "Edm_File_API.Have_Edm_File", "DUMMY_OUT_1");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV", doc_rev);
      trans = mgr.perform(trans);
      return trans.getValue("HAVEEDMFILE/DATA/DUMMY_OUT_1");
   }

   private String findEdmFileNoState(String doc_class, String doc_no,
         String doc_sheet, String doc_rev, String edm_state) {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("FINDFILESTATE",
            "Edm_File_API.Find_Edm_File_No_State", "EDM_DB_STATE");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV", doc_rev);
      cmd.addParameter("DUMMY_IN_1", "ORIGINAL");
      cmd.addParameter("DUMMY_IN_1", edm_state);
      trans = mgr.perform(trans);
      return trans.getValue("FINDFILESTATE/DATA/EDM_DB_STATE");
   }

   private String getViewAccess(String doc_class, String doc_no,
         String doc_sheet, String doc_rev) {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETVIEWACCESS",
            "DOC_ISSUE_API.Get_View_Access_", "GETVIEWACCES");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV", doc_rev);
      trans = mgr.perform(trans);
      return trans.getValue("GETVIEWACCESS/DATA/GETVIEWACCES");
   }

   private String getEditAccess(String doc_class, String doc_no,
         String doc_sheet, String doc_rev) {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETEDITACCESS",
            "DOC_ISSUE_API.Get_Edit_Access_", "GETEDITACCESS");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV", doc_rev);
      trans = mgr.perform(trans);
      return trans.getValue("GETEDITACCESS/DATA/GETEDITACCESS");
   }

   // Added end

   public void editDocument() {
      String status;
      String checkOutUser;
      String fndUser;
      boolean bMultiRow = false;
      fndUser = getFndUser();

      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0) {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      // Bug Id 67336, start
      if (checkFileOperationEnable()) {
         return;
      }
      // Bug Id 67336, end

      if (!isValidColumnValue("STATE", sApproved, sReleased, false)
            || !isValidColumnValue("STATE", sObsolete, false)) {
         mgr.showAlert("DOCMAWDOCISSUECANNOTEDITINAPPORREL: You are not allowed to edit a document in state Approved, Released or Obsolete.");
         return;
      }

      ASPBuffer buff = mgr.newASPBuffer();
      buff.addItem("FILE_ACTION", "CHECKOUT");
      buff.addItem("DOC_TYPE", "ORIGINAL");
      buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
      buff.addItem("LAUNCH_FILE", mgr.readValue("LAUNCH_FILE"));

      if ("TRUE".equals(mgr.readValue("MULTIROW_EDIT")))
         bMultiRow = true;

      if ((isValidColumnValue("GETEDITACCESS", "TRUE", true))) {
         if (!(bMultiRow) && (isEmptyColumnValue("EDM_STATUS"))) {
            transferToEdmMacro("ORIGINAL", "CREATENEW");
            return;
         } else if ((bMultiRow) && (isEmptyColumnValue("EDM_STATUS"))) {
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUECREATENEWMSG: Cannot create new documents by multirow operations."));
            return;
         } else {
            transferToEdmMacro(buff);
            return;
         }
      } else
         mgr.showAlert("DOCMAWDOCISSUECANNOTEDIT: You must have edit access to be able to edit this document.");
   }

   public void printDocument() {
      ASPManager mgr = getASPManager();

      // Bug Id 67336, start
      if (checkFileOperationEnable()) {
         return;
      }
      // Bug Id 67336, end

      if (headlay.isMultirowLayout() && headset.selectRows() == 0) {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      /*
       * if (isEmptyColumnValue("CHECKED_IN_SIGN")) { if
       * (headlay.isMultirowLayout()) mgr.showAlert(mgr.translate(
       * "DOCMAWDOCISSUENOPRINTEMPTYFILEMULTI: One or more documents you're trying to print has no file checked in yet."
       * )); else mgr.showAlert(mgr.translate(
       * "DOCMAWDOCISSUENOPRINTEMPTYFILE: The document you're trying to print has no file checked in yet."
       * )); return; }
       */

      if (isValidColumnValue("GETVIEWACCES", "TRUE", true)
            && checkAccessExt(false, true)) {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "PRINT");
         // Bug Id 56685, Start
         // Bug Id 77556, start
         if (bPrintViewCopy) {
            buff.addItem("DOC_TYPE", "VIEW");
            bPrintViewCopy = false;
         } else {
            buff.addItem("DOC_TYPE", "ORIGINAL");
         }
         // Bug Id 77556, end
         // Bug Id 56685, End
         buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
         transferToEdmMacro(buff);
      } else {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSPRINTMULTI: You don't have view and print access to one or more of the selected documents to print.");
         else
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSPRINT: You must have view and print access to be able to print this document.");

      }
   }

   public void checkInSelectDocument() {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      String action = "CHECKINSEL";
      String doc_type = "ORIGINAL";
      String same_action = "NO";

      if (headlay.isMultirowLayout() && headset.selectRows() == 0) {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      // Bug Id 67336, start
      if (checkFileOperationEnable()) {
         return;
      }
      // Bug Id 67336, end

      if (isValidColumnValue("GETEDITACCESS", "TRUE", true)) {
         if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
            same_action = "YES";

         buff.addItem("DOC_TYPE", doc_type);
         buff.addItem("FILE_ACTION", action);
         buff.addItem("SAME_ACTION_TO_ALL", same_action);
         transferToEdmMacro(buff);
      } else
         mgr.showAlert("DOCMAWDOCISSUECANNOTCHECKIN: You must have edit access to be able to check documents in.");
   }

   public void checkInDocument() {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      String action = "CHECKIN";
      String doc_type = "ORIGINAL";
      String same_action = "NO";

      if (headlay.isMultirowLayout() && headset.selectRows() == 0) {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      // Bug Id 67336, start
      if (checkFileOperationEnable()) {
         return;
      }
      // Bug Id 67336, end

      if (isValidColumnValue("GETEDITACCESS", "TRUE", true)) {
         if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
            same_action = "YES";

         buff.addItem("DOC_TYPE", doc_type);
         buff.addItem("FILE_ACTION", action);
         buff.addItem("SAME_ACTION_TO_ALL", same_action);
         transferToEdmMacro(buff);
      } else
         mgr.showAlert("DOCMAWDOCISSUECANNOTCHECKIN: You must have edit access to be able to check documents in.");
   }

   public void undoCheckOut() {
      ASPManager mgr = getASPManager();

      // Modified by Terry 20121024
      // Original:

      // if (!isValidColumnValue("EDM_STATUS", sCheckedOut, true))
      // {
      // if (headlay.isMultirowLayout())
      // mgr.showAlert("DOCMAWDOCISSUENOTBEENCHECKEDOUTYETMULTI: One or more documents have not been checked out.");
      // else
      // mgr.showAlert("DOCMAWDOCISSUENOTBEENCHECKEDOUTYET: This document's files have not been checked out.");
      // }
      // else
      // {
      if (!isValidColumnValue("GETEDITACCESS", "TRUE", true))
         mgr.showAlert("DOCMAWDOCISSUECANNOTUNDOCHECKOUT: You need edit access to undo a previous check out.");
      else
         transferToEdmMacro("ORIGINAL", "UNDOCHECKOUT");
      // }
   }

   public void userSettings() {
      sUrl = "../docmaw/dlgUserSettings.page?PARAM=DOCISSUE";
      bOpenWizardWindow = true;
   }

   // Bug Id 67105, Start
/*   public void mandatorySettings() {
      sUrl = "../docmaw/MandatorySearchFieldsConfigDlg.page?URL="
            + getPoolKey();
      bOpenWizardWindow = true;
   }*/

   // Bug Id 67105, End

   public String isAccessOwner() {
      int noOfRowsSelected = 1;
      ASPManager mgr = getASPManager();
      String accessowner = "FALSE";

      if (headlay.isMultirowLayout()) {
         headset.storeSelections();
         headset.setFilterOn();
         noOfRowsSelected = headset.countRows();
      } else
         headset.selectRow();

      String fnd_user = getFndUser();

      trans.clear();
      int count = 0;
      for (count = 0; count < noOfRowsSelected; count++) {
         // Bug 53039, Start, Added check on bDoCheckForAllAccess
         if (dGroup.equals(headset.getRow().getValue("ACCESS_CONTROL"))
               || bDoCheckForAllAccess) {
            cmd = trans.addCustomFunction("ISACCESSOWNER" + count,
                  "Document_Issue_Access_Api.Is_User_Access_Owner",
                  "ACCESSOWNER");
            cmd.addParameter("DOC_CLASS", headset.getRow()
                  .getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", headset.getRow()
                  .getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
            cmd.addParameter("USER_ID", fnd_user);
         }
         // Bug 53039, End
         if (headlay.isMultirowLayout()) {
            headset.next();
         }

      }

      trans = mgr.perform(trans);

      if (headlay.isMultirowLayout()) {
         headset.first();
      }
      count = 0;
      for (count = 0; count < noOfRowsSelected; count++) {
         // Bug 53039, Start, Added check on bDoCheckForAllAccess
         if (dGroup.equals(headset.getRow().getValue("ACCESS_CONTROL"))
               || bDoCheckForAllAccess) {
            accessowner = trans.getValue("ISACCESSOWNER" + count
                  + "/DATA/ACCESSOWNER");
         }
         // Bug 53039, End
         else {
            // Bug 57779, Start
            if (isUserDocmanAdministrator()
                  || (headset.getRow().getValue("USER_CREATED")
                        .equals(fnd_user)))
               // Bug 57779, End
               accessowner = "TRUE";
            else
               accessowner = "FALSE";
         }
         if ("FALSE".equals(accessowner)) {
            break;
         }

         if (headlay.isMultirowLayout()) {
            headset.next();
         }
      }

      // Bug 53039, Start
      if (bDoCheckForAllAccess)
         bDoCheckForAllAccess = false;
      // Bug 53039, End
      if (headlay.isMultirowLayout()) {
         headset.setFilterOff();
      }
      return accessowner;
   }

   public String hasEditAccess() {

      ASPManager mgr = getASPManager();
      String accessowner = "EDIT";
      int noOfRowsSelected = 1;
      int count;

      if (headlay.isMultirowLayout()) {
         headset.storeSelections();
         headset.setFilterOn();
         noOfRowsSelected = headset.countRows();
      } else {
         headset.selectRow();
      }

      if (noOfRowsSelected == 0) {
         return "FALSE";
      }

      String fnd_user = getFndUser();
      String person_id = getPersonId();

      trans.clear();
      for (count = 0; count < noOfRowsSelected; count++) {
         cmd = trans.addCustomFunction("EDITACCESS" + count,
               "Document_Issue_Access_API.Person_Get_Access", "DUMMY_OUT_1");
         cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
         cmd.addParameter("DUMMY_IN_1", person_id);
         if (headlay.isMultirowLayout()) {
            headset.next();
         }
      }

      trans = mgr.perform(trans);

      for (count = 0; count < noOfRowsSelected; count++) {
         accessowner = trans.getValue("EDITACCESS" + count
               + "/DATA/DUMMY_OUT_1");
         if (!"EDIT".equals(accessowner)) {
            break;
         }
      }
      if (headlay.isMultirowLayout()) {
         headset.setFilterOff();
      }

      return accessowner;
   }

   public void deleteDocument() {
      ASPManager mgr = getASPManager();
      String fnd_user = getFndUser();
      String person_id = getPersonId();
      int noOFRowsSelected = 1;
      int count = 0;

      // Bug 53039, Start
      if (dAll.equals(headset.getRow().getValue("ACCESS_CONTROL")))
         bDoCheckForAllAccess = true;
      else
         bDoCheckForAllAccess = false;
      // Bug 53039, End

      // Bug Id 67336, start
      if (checkFileOperationEnable()) {
         return;
      }
      // Bug Id 67336, end

      // Bug 57779, Start
      if ("TRUE".equals(isAccessOwner()) || isUserDocmanAdministrator()) {
         // Bug 57779, End
         if (headlay.isMultirowLayout()) {
            headset.storeSelections();
            headset.setFilterOn();
            noOFRowsSelected = headset.countRows();
         } else {
            headset.selectRow();
         }
         boolean error = false;
         for (count = 0; count < noOFRowsSelected; count++) {
            String edm_file_state = findEdmFileNoState(headset.getRow()
                  .getValue("DOC_CLASS"), headset.getRow().getValue("DOC_NO"),
                  headset.getRow().getValue("DOC_SHEET"), headset.getRow()
                        .getValue("DOC_REV"), sCheckedIn);
            if ("TRUE".equals(edm_file_state)) {
               mgr.showAlert(mgr
                     .translate("DOCMAWDOCISSUEDELETEALLALLFILESMUSTBECHECKEDIN: All document file(s) must be checked in before deleting."));
               error = true;
               break;
            }

            if (!sObsolete.equals(headset.getRow().getValue("STATE"))) {
               mgr.showAlert(mgr
                     .translate("DOCMAWDOCISSUEDELETEALLNOTCORRECTSTATE: Only documents in state Obsolete can be deleted."));
               error = true;
               break;
            }

            if (headlay.isMultirowLayout()) {
               headset.next();
            }

         }

         if (error) {
            if (headlay.isMultirowLayout()) {
               headset.setFilterOff();
            }
            return;
         }

         ctx.writeValue("OPERATION", "DELETEALL");

         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("FILE_ACTION", "DELETEALL");
         buff.addItem("SAME_ACTION_TO_ALL",
               (mgr.readValue("SAME_ACTION_TO_ALL") == null) ? "NO" : "YES");
         transferToEdmMacro(buff);
         if (headlay.isMultirowLayout()) {
         }
         headset.setFilterOff();
      } else {
         if (noOFRowsSelected > 1)
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUENOTACCESSOWNERMULTI: You don't have administrative rights to delete selected documents and their files "));
         else
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUENOTACCESSOWNER: You don't have administrative rights to delete this document and its file(s)"));
      }
   }

   public void deleteDocumentFile() {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      String action = "DELETE";
      String doc_type = "ORIGINAL";
      String same_action = "NO";
      String fnd_user = getFndUser();
      String person_id = getPersonId();
      int noOfRowsSelected = 1;

      // Bug Id 67336, start
      if (checkFileOperationEnable()) {
         return;
      }
      // Bug Id 67336, end

      // Bug 57779, Start
      if ((isValidColumnValue("GETEDITACCESS", "TRUE", true) && "EDIT"
            .equals(hasEditAccess()))
            || isUserDocmanAdministrator()
            || dAll.equals(headset.getRow().getValue("ACCESS_CONTROL"))) // Bug
                                                                         // 67441
      // Bug 57779, End
      {
         if (headlay.isMultirowLayout()) {
            headset.storeSelections();
            headset.setFilterOn();
            noOfRowsSelected = headset.countRows();
         } else {
            headset.selectRow();
         }

         trans.clear();
         boolean error = false;
         int count = 0;
         for (count = 0; count < noOfRowsSelected; count++) {
            String doc_class = headset.getRow().getValue("DOC_CLASS");
            String doc_no = headset.getRow().getValue("DOC_NO");
            String doc_sheet = headset.getRow().getValue("DOC_SHEET");
            String doc_rev = headset.getRow().getValue("DOC_REV");

            if ("FALSE".equals(haveEdmFile(doc_class, doc_no, doc_sheet,
                  doc_rev))) {
               if (headlay.isMultirowLayout())
                  mgr.showAlert(mgr
                        .translate("DOCMAWDOCISSUEMULTINOTOBSOLETE: You are attempting to delete file(s) from one or more documents that have no file(s) checked in."));
               else
                  mgr.showAlert(mgr
                        .translate("DOCMAWDOCISSUENOTOBSOLETE: You are attempting to delete file(s) from a document that has no file(s) checked in."));

               error = true;
               break;
            }

            if ("TRUE".equals(findEdmFileNoState(doc_class, doc_no, doc_sheet,
                  doc_rev, sCheckedIn))) {
               mgr.showAlert(mgr
                     .translate("DOCMAWDOCISSUEALLFILESMUSTBECHECKEDIN: All document file(s) must be checked in before deleting."));
               error = true;
               break;
            }

            // Bug Id 57664, Start
            if (!sPrelimin.equals(headset.getRow().getValue("STATE"))
                  && !sObsolete.equals(headset.getRow().getValue("STATE"))
                  && !("TRUE".equals(headset.getValue("APPROVAL_UPDATE")) && sAppInProg
                        .equals(headset.getValue("STATE")))) {
               mgr.showAlert(mgr
                     .translate("DOCMAWDOCISSUENOTCORRECTSTATE: You are not allowed to delete document file(s) when the document is in state ")
                     + headset.getRow().getValue("STATE"));
               error = true;
               break;
            }
            // Bug Id 57664, End

            cmd = trans.addCustomFunction("CHECKEDOUTREDLINE" + count,
                  "Edm_File_API.Get_Check_Out2", "DUMMY_OUT_1");
            cmd.addParameter("DOC_CLASS", headset.getRow()
                  .getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", headset.getRow()
                  .getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
            cmd.addParameter("DOC_TYPE", "REDLINE");
            if (headlay.isMultirowLayout()) {
               headset.next();
            }

         }

         if (error) {
            if (headlay.isMultirowLayout()) {
               headset.setFilterOff();
            }
            return;
         }

         trans = mgr.perform(trans);
         String redline_state;
         for (int x = 0; x < noOfRowsSelected; x++) {
            redline_state = trans.getValue("CHECKEDOUTREDLINE" + x
                  + "/DATA/DUMMY_OUT_1");
            if ("TRUE".equals(redline_state)) {
               mgr.showAlert(mgr
                     .translate("DOCMAWDOCISSUEREDLINECHECKOUT: The Redline file must be checked in before deleting."));
               if (headlay.isMultirowLayout()) {
                  headset.setFilterOff();
               }
               return;
            }
         }

         if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
            same_action = "YES";

         buff.addItem("DOC_TYPE", doc_type);
         buff.addItem("FILE_ACTION", action);
         buff.addItem("SAME_ACTION_TO_ALL", same_action);
         transferToEdmMacro(buff);
         if (headlay.isMultirowLayout()) {
            headset.setFilterOff();
         }

      } else {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILEMULTI: You don't have the necessary access rights to selected documents in order to delete their files"));
         else
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILE: You don't have the necessary access rights to the document in order to delete its file(s)"));

      }

   }

   public void deleteSelectDocFile() {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      String action = "DELETESEL";
      String doc_type = "ORIGINAL";
      String same_action = "NO";
      String fnd_user = getFndUser();
      String person_id = getPersonId();
      int noOfRowsSelected = 1;

      // Bug Id 67336, start
      if (checkFileOperationEnable()) {
         return;
      }
      // Bug Id 67336, end

      // Bug 57779, Start
      if ((isValidColumnValue("GETEDITACCESS", "TRUE", true) && "EDIT"
            .equals(hasEditAccess()))
            || isUserDocmanAdministrator()
            || dAll.equals(headset.getRow().getValue("ACCESS_CONTROL"))) // Bug
                                                                         // 67441
      // Bug 57779, End
      {
         if (headlay.isMultirowLayout()) {
            headset.storeSelections();
            headset.setFilterOn();
            noOfRowsSelected = headset.countRows();
         } else {
            headset.selectRow();
         }

         trans.clear();
         boolean error = false;
         int count = 0;
         for (count = 0; count < noOfRowsSelected; count++) {
            String doc_class = headset.getRow().getValue("DOC_CLASS");
            String doc_no = headset.getRow().getValue("DOC_NO");
            String doc_sheet = headset.getRow().getValue("DOC_SHEET");
            String doc_rev = headset.getRow().getValue("DOC_REV");

            if ("FALSE".equals(haveEdmFile(doc_class, doc_no, doc_sheet,
                  doc_rev))) {
               if (headlay.isMultirowLayout())
                  mgr.showAlert(mgr
                        .translate("DOCMAWDOCISSUEMULTINOTOBSOLETE: You are attempting to delete file(s) from one or more documents that have no file(s) checked in."));
               else
                  mgr.showAlert(mgr
                        .translate("DOCMAWDOCISSUENOTOBSOLETE: You are attempting to delete file(s) from a document that has no file(s) checked in."));

               error = true;
               break;
            }

            if ("TRUE".equals(findEdmFileNoState(doc_class, doc_no, doc_sheet,
                  doc_rev, sCheckedIn))) {
               mgr.showAlert(mgr
                     .translate("DOCMAWDOCISSUEALLFILESMUSTBECHECKEDIN: All document file(s) must be checked in before deleting."));
               error = true;
               break;
            }

            // Bug Id 57664, Start
            if (!sPrelimin.equals(headset.getRow().getValue("STATE"))
                  && !sObsolete.equals(headset.getRow().getValue("STATE"))
                  && !("TRUE".equals(headset.getValue("APPROVAL_UPDATE")) && sAppInProg
                        .equals(headset.getValue("STATE")))) {
               mgr.showAlert(mgr
                     .translate("DOCMAWDOCISSUENOTCORRECTSTATE: You are not allowed to delete document file(s) when the document is in state ")
                     + headset.getRow().getValue("STATE"));
               error = true;
               break;
            }
            // Bug Id 57664, End

            cmd = trans.addCustomFunction("CHECKEDOUTREDLINE" + count,
                  "Edm_File_API.Get_Check_Out2", "DUMMY_OUT_1");
            cmd.addParameter("DOC_CLASS", headset.getRow()
                  .getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", headset.getRow()
                  .getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
            cmd.addParameter("DOC_TYPE", "REDLINE");
            if (headlay.isMultirowLayout()) {
               headset.next();
            }
         }

         if (error) {
            if (headlay.isMultirowLayout()) {
               headset.setFilterOff();
            }
            return;
         }

         trans = mgr.perform(trans);
         String redline_state;
         for (int x = 0; x < noOfRowsSelected; x++) {
            redline_state = trans.getValue("CHECKEDOUTREDLINE" + x
                  + "/DATA/DUMMY_OUT_1");
            if ("TRUE".equals(redline_state)) {
               mgr.showAlert(mgr
                     .translate("DOCMAWDOCISSUEREDLINECHECKOUT: The Redline file must be checked in before deleting."));
               if (headlay.isMultirowLayout()) {
                  headset.setFilterOff();
               }
               return;
            }
         }

         if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
            same_action = "YES";

         buff.addItem("DOC_TYPE", doc_type);
         buff.addItem("FILE_ACTION", action);
         buff.addItem("SAME_ACTION_TO_ALL", same_action);
         transferToEdmMacro(buff);
         if (headlay.isMultirowLayout()) {
            headset.setFilterOff();
         }

      } else {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILEMULTI: You don't have the necessary access rights to selected documents in order to delete their files"));
         else
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILE: You don't have the necessary access rights to the document in order to delete its file(s)"));
      }
   }

   public void deleteRow() {
      ASPManager mgr = getASPManager();

      headset.store();
      if (headlay.isMultirowLayout()) {
         headset.setSelectedRowsRemoved();
         headset.unselectRows();
      } else
         headset.setRemoved();

      mgr.submit(trans);
      // Refresh the headset only if there are items.
      if (headset.countRows() > 0)
         refreshHeadset();

   }

   public void sign()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      String personDefaultZone = ctx
            .findGlobal(GenbawConstants.PERSON_DEFAULT_ZONE);
      if (mgr.isEmpty(personDefaultZone)) {
         mgr.showAlert("DOCISSUECANNOTFINDUSERDEFALUTZONENO: Can not find user's default zone no.");
         return;
      }

      headset.store();
      String src_doc_class = headset.getValue("DOC_CLASS");
      String src_doc_no = headset.getValue("DOC_NO");
      String src_doc_sheet = headset.getValue("DOC_SHEET");
      String src_doc_rev = headset.getValue("DOC_REV");
      

   
      trans.clear();
      cmd = trans.addCustomFunction("GETSIGNSTATUS","DOC_ISSUE_TARGET_ORG_API.Get_Sign_Status", "SIGN_STATUS_DB");
      cmd.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO",    headset.getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV",   headset.getValue("DOC_REV"));
      cmd.addParameter("ORG_NO",    personDefaultZone);
      
      trans = mgr.validate(trans);
      String retvalue = trans.getValue("GETSIGNSTATUS/DATA/SIGN_STATUS_DB");
   
      if("TRUE".equals(retvalue)){
         mgr.showAlert("DOCISSUEANCESTORYOUHAVEALREADYSIGNEDTHEDOCUMENT: You have already signed the document.");
         return ;
      }
      
      trans.clear();
      

      cmd = trans.addCustomCommand("SIGNTHEDOC", "DOC_ISSUE_API.Sign_The_Doc");
      cmd.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      cmd.addParameter("OBJVERSION", DocmawConstants.PROJ_RECEIVE);
      cmd.addParameter("ORG_NO", personDefaultZone);
      cmd.addParameter("OBJID", null);
      trans = mgr.perform(trans);

      String doc_class = trans.getValue("SIGNTHEDOC/DATA/DOC_CLASS");
      String doc_no = trans.getValue("SIGNTHEDOC/DATA/DOC_NO");
      String doc_sheet = trans.getValue("SIGNTHEDOC/DATA/DOC_SHEET");
      String doc_rev = trans.getValue("SIGNTHEDOC/DATA/DOC_REV");
      String copy_file = trans.getValue("SIGNTHEDOC/DATA/OBJID");
      
      if ("TRUE".equals(copy_file)) {
         try {
            copyFileInRepository(src_doc_class, src_doc_no, src_doc_sheet,
                  src_doc_rev, doc_class, doc_no, doc_sheet, doc_rev);
         } catch (FndException e) {
            e.printStackTrace();
         }
         //TODO LQW NOT TEST YET.
         StringBuilder sb = new StringBuilder();
         sb.append("      SELECT DI.FROM_DOC_CLASS    FROM_DOC_CLASS,");
         sb.append("      DI.FROM_DOC_NO       FROM_DOC_NO,");
         sb.append("      DI.FROM_DOC_SHEET    FROM_DOC_SHEET,");
         sb.append("      DI.FROM_DOC_REV      FROM_DOC_REV,");
         sb.append("      DI.DOC_CLASS         DOC_CLASS,");
         sb.append("      DI.DOC_NO            DOC_NO,");
         sb.append("      DI.DOC_SHEET         DOC_SHEET,");
         sb.append("      DI.DOC_REV           DOC_REV");
         sb.append("      FROM DOC_STRUCTURE DS, DOC_ISSUE DI");
         sb.append("      WHERE DS.SUB_DOC_CLASS = DI.DOC_CLASS");
         sb.append("        AND DS.SUB_DOC_NO = DI.DOC_NO");
         sb.append("        AND DS.SUB_DOC_SHEET = DI.DOC_SHEET");   
         sb.append("        AND DS.SUB_DOC_REV = DI.DOC_REV");
         sb.append("        AND DS.DOC_CLASS = ?");
         sb.append("        AND DS.DOC_NO = ?");
         sb.append("        AND DS.DOC_SHEET = ?");
         sb.append("        AND DS.DOC_REV = ?");
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         String sql = sb.toString();
         ASPQuery q = trans.addQuery("BPROCESSNAMES", sql);
         q.addParameter("DOC_CLASS", doc_class);
         q.addParameter("DOC_NO", doc_no);
         q.addParameter("DOC_SHEET", doc_sheet);
         q.addParameter("DOC_REV", doc_rev);
         trans = mgr.perform(trans);
         ASPBuffer buffer = trans.getBuffer("BPROCESSNAMES");
         int count = buffer.countItems();
         String[] tempStrArray = null;
         for (int i = 0; i < count; i++)
         {
            if("DATA".equals(buffer.getNameAt(i)))
            {
               tempStrArray = new String[8];
               tempStrArray[0] = buffer.getBufferAt(i).getValue("FROM_DOC_CLASS");
               tempStrArray[1] = buffer.getBufferAt(i).getValue("FROM_DOC_NO");
               tempStrArray[2] = buffer.getBufferAt(i).getValue("FROM_DOC_SHEET");
               tempStrArray[3] = buffer.getBufferAt(i).getValue("FROM_DOC_REV");
               tempStrArray[4] = buffer.getBufferAt(i).getValue("DOC_CLASS");
               tempStrArray[5] = buffer.getBufferAt(i).getValue("DOC_NO");
               tempStrArray[6] = buffer.getBufferAt(i).getValue("DOC_SHEET");
               tempStrArray[7] = buffer.getBufferAt(i).getValue("DOC_REV");
               try {
                  copyFileInRepository(tempStrArray[0], tempStrArray[1], tempStrArray[2], tempStrArray[3], 
                                       tempStrArray[4], tempStrArray[5], tempStrArray[6], tempStrArray[7]);
               } catch (FndException e) {
                  e.printStackTrace();
               }
            }
         }
      }

      mgr.showAlert(mgr.translate("DOCISSUEALERTSIGNSUCCESSFULLY: Sign the doc successfully."));
      headset.refreshRow();
      okFindITEM11();
   }
   
   public void transferToSignedDoc()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      else
         headset.selectRow();

      String doc_class = headset.getValue("DOC_CLASS");
      String doc_no = headset.getValue("DOC_NO");
      String doc_sheet = headset.getValue("DOC_SHEET");
      String doc_rev = headset.getValue("DOC_REV");
      
      headset.setFilterOff();

      ASPCommand cmd = trans.addCustomCommand("GETCREATEDDOC", "Doc_Issue_API.Get_Created_Doc");
      cmd.addParameter("FROM_DOC_CLASS", doc_class);
      cmd.addParameter("FROM_DOC_NO", doc_no);
      cmd.addParameter("FROM_DOC_SHEET", doc_sheet);
      cmd.addParameter("FROM_DOC_REV", doc_rev);
      cmd.addParameter("DOC_CLASS", null);
      cmd.addParameter("DOC_NO", null);
      cmd.addParameter("DOC_SHEET", null);
      cmd.addParameter("DOC_REV", null);
      cmd.addParameter("SUB_CLASS", null);
      
      trans = mgr.perform(trans);
      
      doc_class = trans.getValue("GETCREATEDDOC/DATA/DOC_CLASS");
      doc_no = trans.getValue("GETCREATEDDOC/DATA/DOC_NO");
      doc_sheet = trans.getValue("GETCREATEDDOC/DATA/DOC_SHEET");
      doc_rev = trans.getValue("GETCREATEDDOC/DATA/DOC_REV");
      String sub_class = trans.getValue("GETCREATEDDOC/DATA/SUB_CLASS");
      
      if (mgr.isEmpty(doc_class) || mgr.isEmpty(doc_no) || mgr.isEmpty(doc_sheet) || mgr.isEmpty(doc_rev))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANTCREATEDDOC: Can not transfer to uncreated document."));
      else
      {
         String url = DocmawConstants.getCorrespondingDocIssuePage(doc_class, sub_class);
         if (!Str.isEmpty(url))
            mgr.redirectTo(url + "?DOC_CLASS=" + mgr.URLEncode(doc_class) +
                                 "&DOC_NO=" + mgr.URLEncode(doc_no) +
                                 "&DOC_SHEET=" + mgr.URLEncode(doc_sheet) +
                                 "&DOC_REV=" + mgr.URLEncode(doc_rev));
      }
   }

   public void addToIndex() {
      ASPManager mgr = getASPManager();
      int noOfRowsSelected = 1;
      int count = 0;

      if (headlay.isMultirowLayout()) {
         headset.storeSelections();
      } else
         headset.selectRow();

      // Return if no rows are selected in MultiRow layout
      if (headlay.isMultirowLayout() && headset.countSelectedRows() == 0) {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      if (headlay.isMultirowLayout()) {
         headset.setFilterOn();
         noOfRowsSelected = headset.countRows();
         headset.first();
      }
      trans.clear();

      String tmp_doc_class = null;
      String tmp_doc_no = null;
      String tmp_doc_sheet = null;
      String tmp_doc_rev = null;
      for (count = 0; count < noOfRowsSelected; count++) {
         tmp_doc_class = headset.getRow().getValue("DOC_CLASS");
         tmp_doc_no = headset.getRow().getValue("DOC_NO");
         tmp_doc_sheet = headset.getRow().getValue("DOC_SHEET");
         tmp_doc_rev = headset.getRow().getValue("DOC_REV");
         FullTextServiceImpl fullTextService = new FullTextServiceImpl();
         String ret = fullTextService.createIndex(tmp_doc_class, tmp_doc_no,
               tmp_doc_sheet, tmp_doc_rev);
         if (ret
               .equals(ifs.fultxw.engine.Constants.RET_ERROR_INDEX_PATH_NOT_CONFIGURED)) {
            mgr.showAlert("DOCISSUEANCESTORINDEXCREATEERRORINDEXPATHNOTCONFIGURED: Index path not configured for Class: "
                  + tmp_doc_class);
            bCreateIndexSucceed = false;
            break;
         } else if (ret
               .equals(ifs.fultxw.engine.Constants.RET_ERROR_CLASS_COLUMN_NO_CONFIGURED)) {
            mgr.showAlert("DOCISSUEANCESTORINDEXCREATEERRORINDEXPATHNOTCONFIGURED: Index Column not configured for Class: "
                  + tmp_doc_class);
            bCreateIndexSucceed = false;
            break;
         } else {
            bCreateIndexSucceed = true;
         }

         if (headlay.isMultirowLayout()) {
            headset.next();
         }
      }

      if (bCreateIndexSucceed) {
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUEADDINDEXSUCCEED: The document(s) was successfully indexed."));
      }
   }

   public void createTrans() {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      // store selections
      headset.store();

      ASPBuffer buff = headset.getSelectedRows();
      int countRows = headset.countSelectedRows();
      if (countRows == 0) {
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSRECEIVETRANSNOREC: You must select a receive letter record."));
      } else {
         for (int i = 0; i < countRows; i++) {
            ASPBuffer currRow = buff.getBufferAt(i);
            String doc_class = mgr.isEmpty(currRow.getValue("DOC_CLASS")) ? ""
                  : currRow.getValue("DOC_CLASS");
            String doc_no = mgr.isEmpty(currRow.getValue("DOC_NO")) ? ""
                  : currRow.getValue("DOC_NO");
            String doc_sheet = mgr.isEmpty(currRow.getValue("DOC_SHEET")) ? ""
                  : currRow.getValue("DOC_SHEET");
            String doc_rev = mgr.isEmpty(currRow.getValue("DOC_REV")) ? ""
                  : currRow.getValue("DOC_REV");
            if (!mgr.isEmpty(doc_class) && !mgr.isEmpty(doc_no)
                  && !mgr.isEmpty(doc_sheet) && !mgr.isEmpty(doc_rev)) {
               ASPCommand cmd = trans.addCustomCommand("CREATETRANS" + i,
                     "DOC_RECEIVE_TRANS_API.Create_Trans");
               cmd.addParameter("DOC_CLASS", doc_class);
               cmd.addParameter("DOC_NO", doc_no);
               cmd.addParameter("DOC_SHEET", doc_sheet);
               cmd.addParameter("DOC_REV", doc_rev);
            }
         }
         trans = mgr.perform(trans);

         if (1 == countRows) {
            ASPBuffer currRow = buff.getBufferAt(0);
            String url = "../doctrw/DocReceiveTrans.page" + "?DOC_CLASS="
                  + currRow.getValue("DOC_CLASS") + "&DOC_NO="
                  + currRow.getValue("DOC_NO") + "&DOC_SHEET="
                  + currRow.getValue("DOC_SHEET") + "&DOC_REV="
                  + currRow.getValue("DOC_REV");
            mgr.redirectTo(url);
         } else {
            mgr.showAlert(mgr
                  .translate("DOCMAWDOCISSRECEIVETRANSSUCC: Create receive letter transaction successfully."));
         }
      }
   }
   
   public void createDocDistribution() throws FndException 
   {
      ASPManager mgr = getASPManager();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      // store selections
      headset.store();
      
      ASPBuffer selected_keys = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,SUB_CLASS");
      
      String sel_doc_class = headset.getValue("DOC_CLASS");
      String sel_sub_class = headset.getValue("SUB_CLASS");
      
      // headset.unselectRows();
      
      int countRow = selected_keys.countItems();
      if (countRow > 0)
      {
         // create doc distribution ctl
         ASPCommand cmd = trans.addCustomCommand("CREDIST", "Doc_Distribution_Ctl_API.New_Doc_Distribution");
         cmd.addParameter("DUMMY_IN_1", "");
         cmd.addParameter("DUMMY_IN_1", sel_doc_class);
         cmd.addParameter("DUMMY_IN_1", sel_sub_class);
         cmd.addParameter("DUMMY_IN_1", "");
         cmd.addParameter("DUMMY_IN_1", "");
         cmd.addParameter("DUMMY_OUT_1");
         trans = mgr.perform(trans);
         String dist_no = trans.getValue("CREDIST/DATA/DUMMY_OUT_1");
         if (!mgr.isEmpty(dist_no))
         {
            trans.clear();
            // copy document list
            for (int i = 0; i < countRow; i++) 
            {
               ASPBuffer subBuff = selected_keys.getBufferAt(i);
               String doc_class = subBuff.getValueAt(0);
               String sub_class = subBuff.getValueAt(4);
               
               if (!sel_doc_class.equals(doc_class) || (!mgr.isEmpty(sel_sub_class) && !sel_sub_class.equals(sub_class)))
                  continue;
               
               String doc_no = subBuff.getValueAt(1);
               String doc_sheet = subBuff.getValueAt(2);
               String doc_rev = subBuff.getValueAt(3);
               cmd = trans.addCustomCommand("CREFILES" + i, "Doc_Distribution_Files_API.Create_File");
               cmd.addParameter("IN_1", dist_no);
               cmd.addParameter("IN_1", doc_class);
               cmd.addParameter("IN_1", doc_no);
               cmd.addParameter("IN_1", doc_sheet);
               cmd.addParameter("IN_1", doc_rev);
            }
            trans = mgr.perform(trans);
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUECREDISTSUCC: Create doc distribution &1 successfully.", dist_no));
            String url = "../docctw/DocDistributionCtl.page?DIST_NO=" + dist_no;
            String location;
            String show_in_nav = ctx.readValue("SHOW_IN_NAVIGATOR", "FALSE");
            show_in_nav = mgr.isEmpty(show_in_nav) ? "FALSE" : show_in_nav;
            if ("FALSE".equals(show_in_nav))
               location = "window.location = \"";
            else
               location = "window.parent.location = \"";
            
            appendDirtyJavaScript(location);
            appendDirtyJavaScript(mgr.encodeStringForJavascript(url));
            appendDirtyJavaScript("\";\n");
         }
      }
      else
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOSELROW: You must select some documents."));
      }
   }

   public void setToCopyFile() {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      if (!isValidColumnValue("GETVIEWACCES", "TRUE", true)) {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSCOPYFILETOMULTI: You don't have view access to one or more of the selected documents.");
         else
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSCOPYFILETO: You must have view access to be able to copy this document's files.");

         return;
      }

      /*
       * if (isEmptyColumnValue("CHECKED_IN_SIGN")) { if
       * (headlay.isMultirowLayout()) mgr.showAlert(mgr.translate(
       * "DOCMAWDOCISSUENOCOPYEMPTYFILEMULTI: One or more documents you're trying to copy has no file checked in yet."
       * )); else mgr.showAlert(mgr.translate(
       * "DOCMAWDOCISSUENOCOPYEMPTYFILE: The document you're trying to copy has no file checked in yet."
       * )); return; }
       */

      // Bug Id 67336, start
      if (checkFileOperationEnable()) {
         return;
      }
      // Bug Id 67336, end

      String action = "GETCOPYTODIR";
      String doc_type = "ORIGINAL";
      // Modified by Terry 20120926
      // Original: String same_action = "NO";
      String same_action = "YES";
      // Modified end
      String cpy_dir = "";
      // Modified by Terry 20120926
      // Original:
      // if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
      // {
      // same_action = "YES";
      // }

      buff.addItem("DOC_TYPE", doc_type);
      buff.addItem("FILE_ACTION", action);
      buff.addItem("SAME_ACTION_TO_ALL", same_action);
      buff.addItem("SELECTED_DIRECTORY", cpy_dir);
      transferToEdmMacro(buff);
   }

   public void createNewRevision() {
      ASPManager mgr = getASPManager();
      String userAccess;

      if (headlay.isMultirowLayout()) {
         headset.storeSelections();
         headset.setFilterOn();
      } else {
         headset.unselectRows();
         headset.selectRow();
      }

      headset.refreshRow();

      // Bug id 88317 start
      // Modified by Terry 20120927
      // Original:
      /*
       * trans.clear();
       * 
       * cmd = trans.addCustomFunction("FNDUSER",
       * "Fnd_Session_API.Get_Fnd_User", "DUMMY2"); cmd =
       * trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User",
       * "DUMMY1"); cmd.addReference("DUMMY2", "FNDUSER/DATA");
       * 
       * trans = mgr.perform(trans); String person_id =
       * trans.getValue("STARUSER/DATA/DUMMY1");
       * 
       * 
       * if ("*".equals(person_id)){ mgr.showAlert(mgr.translate(
       * "DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."
       * )); return; }
       */
      // Bug id 88317 end
      // Modified end

      trans.clear(); // Bug Id 40809
      // Bug Id 75490, Start
      String edm_db_state = findEdmFileNoState(
            headset.getRow().getValue("DOC_CLASS"),
            headset.getRow().getValue("DOC_NO"),
            headset.getRow().getValue("DOC_SHEET"),
            headset.getRow().getValue("DOC_REV"), sCheckedIn);
      if ("TRUE".equals(edm_db_state)) {
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUECREATENEWREVCHECKINNOT: You can not create a new revision for documents, there are some files need to check in.")); // Bug
                                                                                                                                                             // Id
                                                                                                                                                             // 76849
         headset.setFilterOff();
         return;
      }
      // Bug Id 75490, End
      cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User",
            "LOGUSER");
      trans = mgr.perform(trans);
      String fndUser = trans.getValue("FNDUSER/DATA/LOGUSER");
      trans.clear();

      if (dGroup.equals(headset.getRow().getValue("ACCESS_CONTROL"))) {
         cmd = trans.addCustomFunction("USERACCESS",
               "DOCUMENT_ISSUE_ACCESS_API.User_Get_Access", "USERGETACCESS");
         cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
         cmd.addParameter("LOGUSER", fndUser);
         trans = mgr.perform(trans);

         userAccess = trans.getValue("USERACCESS/DATA/USERGETACCESS");
         trans.clear();
      } else if (dAll.equals(headset.getRow().getValue("ACCESS_CONTROL")))
         userAccess = "EDIT";
      else {
         // Bug 57778, Start
         if (isUserDocmanAdministrator()
               || (headset.getRow().getValue("USER_CREATED").equals(fndUser)))
            // Bug 57778, End
            userAccess = "EDIT";
         else
            userAccess = "";
      }

      if ("EDIT".equals(userAccess)) {
         ASPBuffer act_buf = mgr.newASPBuffer();
         act_buf.addItem("DOC_TYPE", "ORIGINAL");
         act_buf.addItem("ACTION", "NEW_REVISION");

         ASPBuffer data_buff = headset
               .getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

         sUrl = DocumentTransferHandler.getDataTransferUrl(mgr,
               "NewRevisionWizard.page", act_buf, data_buff);
         bOpenWizardWindow = true;
         modifySubWindow4NewRev = true;
      } else
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUENOEDITACCESSTOCREATENEWREV: You must have edit access to be able to create a new revision of this document."));

      headset.setFilterOff();
      headset.unselectRows();
   }

   // Bug Id 67336, start
   private boolean checkFileOperationEnable() {
      ASPManager mgr = getASPManager();
      if (headlay.isMultirowLayout()) {
         headset.storeSelections();
         headset.setFilterOn();
         String prestructure = " ";
         String structure;
         if (headset.countSelectedRows() > 1) {
            for (int k = 0; k < headset.countSelectedRows(); k++) {
               structure = headset.getValue("STRUCTURE");
               if (" ".equals(prestructure)) {
                  prestructure = structure;
               }

               if (!prestructure.equals(structure)) {
                  mgr.showAlert(mgr
                        .translate("DOCMAWDOCISSUENOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
                  headset.setFilterOff();
                  return true;
               }

               if ("TRUE".equals(prestructure) && "TRUE".equals(structure)) {
                  mgr.showAlert(mgr
                        .translate("DOCMAWDOCISSUENOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
                  headset.setFilterOff();
                  return true;
               }
               prestructure = structure;
               headset.next();
            }
         } 
         headset.setFilterOff();
      }
      return false;
   }

   // Bug Id 67336, end

   public void previousLevel() {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("DOCSTRUC",
            "DOC_STRUCTURE_API.Number_Of_Parents_", "RETURN");
      cmd.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);

      // If no parent documents revisions found then..
      if ("0".equals(trans.getValue("DOCSTRUC/DATA/RETURN"))) {
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUETOPSTRUC: You are now at the top of the document structure."));
      } else {
         // Fetch the list of parent documents..

         // SQLInjections_Safe AMNILK 20070810

         StringBuffer sql = new StringBuffer(
               "SELECT DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV FROM DOC_STRUCTURE WHERE SUB_DOC_CLASS = ?");
         sql.append(" AND SUB_DOC_NO = ?");
         sql.append(" AND SUB_DOC_SHEET = ?");
         sql.append(" AND SUB_DOC_REV = ?");

         trans.clear();
         q = trans.addQuery("PARENTS", sql.toString());
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV"));

         trans = mgr.perform(trans);

         // Redirect to parent document revisions..
         trans.getBuffer("PARENTS").removeItem("INFO");
         mgr.transferDataTo("DocIssue.page", trans.getBuffer("PARENTS"));
      }
   }
   
   private void callNewWindow(String transfer_page, ASPBuffer buff) throws FndException 
   {
      ASPManager mgr = getASPManager();
      String serialized_data = mgr.pack(buff);
      String connection_str;
      if (transfer_page.indexOf("?") != -1)
         connection_str = "&";
      else
         connection_str = "?";
      String url = transfer_page + connection_str + TRANSFER_PARAM_NAME + "=" + serialized_data;
      appendDirtyJavaScript("showNewBrowser_('"+ url + "', 550, 550, 'YES'); \n");
   }
   
   public void importAttachInfo() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      // store selections
      if (headlay.isSingleLayout())
      {
         headset.unselectRows();
         headset.selectRow();
      }
      else
         headset.selectRows();
      
      ASPBuffer selected_keys = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,ATTACH_TYPE,DOC_CODE");
      int countRow = selected_keys.countItems();
      if (countRow > 0)
      {
         Buffer data_buff = selected_keys.getBuffer();
         Buffer transfer_buff = data_buff.newInstance();
         boolean first_row = true;
         for (int i = 0; i < selected_keys.countItems(); i++)
         {
            ifs.fnd.buffer.Item one_row = data_buff.getItem(i);
            Buffer org = one_row.getBuffer();
            Buffer row = (Buffer)org.clone();
            if (first_row)
            {
               row.getItem(0).setName("PARENT_DOC_CLASS");
               row.getItem(1).setName("PARENT_DOC_NO");
               row.getItem(2).setName("PARENT_DOC_SHEET");
               row.getItem(3).setName("PARENT_DOC_REV");
               row.getItem(4).setName("DOC_CLASS");
               row.getItem(5).setName("TRANSFER_NO");
               first_row = false;
            }
            transfer_buff.addItem("DATA", row);
         }
         
         if (transfer_buff.countItems() > 0)
         {
            ASPBuffer transfer_buffer = mgr.newASPBuffer(transfer_buff);
            callNewWindow("../genbaw/ImportExcel.page?IMPTITLE=" + mgr.translate("DOCISSUEANCESTORIMPORTAI: Import Attach Info"), transfer_buffer);
         }
         else
         {
            mgr.showAlert(mgr.translate("DOCISSUEANCESTORIMPORTAINOSELROW: You must select a document info."));
         }
      }
      else
      {
         mgr.showAlert(mgr.translate("DOCISSUEANCESTORIMPORTAINOSELROW: You must select a document info."));
      }
   }

   public void sendDoc() throws FndException {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      // ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      ArrayList bufferList = new ArrayList();

      // store selections
      headset.store();

      ASPBuffer selected_keys = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,DOC_STATE");
      headset.unselectRows();

      // loop keys
      int rowCount = selected_keys.countItems();
      for (int i = 0; i < rowCount; i++) {
         ASPBuffer subBuff = selected_keys.getBufferAt(i);
         String doc_state = subBuff.getValueAt(4);
         // if (mgr.isEmpty(doc_state) || "FALSE".equalsIgnoreCase(doc_state))
         // {
         ASPCommand cmd = trans.addCustomCommand("SENDDOC" + i, "DOC_ISSUE_SEND_UTIL_NEW_API.Doc_Issue_Send");
         cmd.addParameter("DOC_CLASS", subBuff.getValueAt(0));
         cmd.addParameter("DOC_NO", subBuff.getValueAt(1));
         cmd.addParameter("DOC_SHEET", subBuff.getValueAt(2));
         cmd.addParameter("DOC_REV", subBuff.getValueAt(3));
         cmd.addParameter("OBJVERSION", DocmawConstants.EXCH_RECEIVE);
         cmd.addParameter("OBJID", null);
         bufferList.add(String.valueOf(i));
         // }
      }

      trans = mgr.perform(trans);

      for (int j = 0; j < bufferList.size(); j++) {
         ASPBuffer srcBuff = selected_keys.getBufferAt(Integer.valueOf((String) bufferList.get(j)));
         String src_doc_class = srcBuff.getValueAt(0);
         String src_doc_no = srcBuff.getValueAt(1);
         String src_doc_sheet = srcBuff.getValueAt(2);
         String src_doc_rev = srcBuff.getValueAt(3);

         String doc_class = trans.getValue("SENDDOC" + (String) bufferList.get(j) + "/DATA/DOC_CLASS");
         String doc_no = trans.getValue("SENDDOC" + (String) bufferList.get(j) + "/DATA/DOC_NO");
         String doc_sheet = trans.getValue("SENDDOC" + (String) bufferList.get(j) + "/DATA/DOC_SHEET");
         String doc_rev = trans.getValue("SENDDOC" + (String) bufferList.get(j) + "/DATA/DOC_REV");
         String copy_file = trans.getValue("SENDDOC"  + (String) bufferList.get(j) + "/DATA/OBJID");

         if (!mgr.isEmpty(doc_class) && !doc_class.equals(src_doc_class)) {
            /*mgr.showAlert("File copy info: from document " + src_doc_class
                  + "^" + src_doc_no + "^" + src_doc_sheet + "^" + src_doc_rev
                  + " to document " + doc_class + "^" + doc_no + "^"
                  + doc_sheet + "^" + doc_rev + ".");
*/
            mgr.showAlert("DOCISSUEANCESTORSENDDOCSUCCESSFULLY: Send the document successfully.");
            if ("TRUE".equals(copy_file)) {
               copyFileInRepository(src_doc_class, src_doc_no, src_doc_sheet,
                     src_doc_rev, doc_class, doc_no, doc_sheet, doc_rev);
            }
            //TODO LQW NOT TEST YET.
            trans.clear();
            StringBuilder sb = new StringBuilder();
            sb.append("      SELECT DI.FROM_DOC_CLASS    FROM_DOC_CLASS,");
            sb.append("      DI.FROM_DOC_NO       FROM_DOC_NO,");
            sb.append("      DI.FROM_DOC_SHEET    FROM_DOC_SHEET,");
            sb.append("      DI.FROM_DOC_REV      FROM_DOC_REV,");
            sb.append("      DI.DOC_CLASS         DOC_CLASS,");
            sb.append("      DI.DOC_NO            DOC_NO,");
            sb.append("      DI.DOC_SHEET         DOC_SHEET,");
            sb.append("      DI.DOC_REV           DOC_REV");
            sb.append("      FROM DOC_STRUCTURE DS, DOC_ISSUE DI");
            sb.append("      WHERE DS.SUB_DOC_CLASS = DI.DOC_CLASS");
            sb.append("        AND DS.SUB_DOC_NO = DI.DOC_NO");
            sb.append("        AND DS.SUB_DOC_SHEET = DI.DOC_SHEET");
            sb.append("        AND DS.SUB_DOC_REV = DI.DOC_REV");
            sb.append("        AND DS.DOC_CLASS = ?");
            sb.append("        AND DS.DOC_NO = ?");
            sb.append("        AND DS.DOC_SHEET = ?");
            sb.append("        AND DS.DOC_REV = ?");
            String sql = sb.toString();
            ASPQuery q = trans.addQuery("BPROCESSNAMES", sql);
            q.addParameter("DOC_CLASS", doc_class);
            q.addParameter("DOC_NO", doc_no);
            q.addParameter("DOC_SHEET", doc_sheet);
            q.addParameter("DOC_REV", doc_rev);
            trans = mgr.perform(trans);
            ASPBuffer buffer = trans.getBuffer("BPROCESSNAMES");
            int count = buffer.countItems();
            String[] tempStrArray = null;
            for (int i = 0; i < count; i++)
            {
               if("DATA".equals(buffer.getNameAt(i)))
               {
                  tempStrArray = new String[8];
                  tempStrArray[0] = buffer.getBufferAt(i).getValue("FROM_DOC_CLASS");
                  tempStrArray[1] = buffer.getBufferAt(i).getValue("FROM_DOC_NO");
                  tempStrArray[2] = buffer.getBufferAt(i).getValue("FROM_DOC_SHEET");
                  tempStrArray[3] = buffer.getBufferAt(i).getValue("FROM_DOC_REV");
                  tempStrArray[4] = buffer.getBufferAt(i).getValue("DOC_CLASS");
                  tempStrArray[5] = buffer.getBufferAt(i).getValue("DOC_NO");
                  tempStrArray[6] = buffer.getBufferAt(i).getValue("DOC_SHEET");
                  tempStrArray[7] = buffer.getBufferAt(i).getValue("DOC_REV");
                  try {
                     copyFileInRepository(tempStrArray[0], tempStrArray[1], tempStrArray[2], tempStrArray[3], 
                                          tempStrArray[4], tempStrArray[5], tempStrArray[6], tempStrArray[7]);
                  } catch (FndException e) {
                     e.printStackTrace();
                  }
               }
            }
         }

      }
      refreshHeadset();
   }

//   // DMPR303 START
//   public void createLink() {
//      ASPManager mgr = getASPManager();
//      transferToCreateLink();
//   }

   public void transferToCreateLink() {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout()) {
         headset.unselectRows();
         headset.selectRow();
      } else
         headset.selectRows();

      ASPBuffer data = headset
            .getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,DOCTITLE");

      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr,
            "CreateLink.page", data);
      bTranferToCreateLink = true;
   }

   // DMPR303 END.

   public void performRefreshParent() {
      ASPManager mgr = getASPManager();

      //
      // Perform any necessary actions before
      // refreshing
      //

      if ("DELETEALL".equals(ctx.readValue("OPERATION"))) {
         refreshHeadset();
         refreshActiveTab();
         return;
      }

      if ("RELEASE".equals(ctx.readValue("OPERATION"))) {
         refreshHeadset();
         okFindITEM6();
      }
      
      if ("DOC_ISSUE_TARGET_ORG_API".equals(mgr.readValue("REFRESH_CHILD"))) 
      {
         refreshHeadset();
         activateSendDept();
      }

      //
      // Refresh the selected rows
      //
      if (headlay.isSingleLayout()) {
         headset.refreshRow();
         eval(headset.syncItemSets());
         okFindITEM2();
         okFindITEM3();
         okFindITEM4();
         okFindITEM6();
         okFindITEM7();
         okFindITEM9();
      } else {
         if (headset.countSelectedRows() == 0)
            headset.selectRows();

         headset.setFilterOn();

         headset.first();
         do {
            headset.refreshRow(); // Note: this operation is expensive when
                                  // doing for many rows
         } while (headset.next());

         headset.setFilterOff();

         if (!"YES".equals(mgr.readValue("LEAVE_ROWS_SELECTED"))) {
            headset.unselectRows();
         }
      }
   }

   private void refreshActiveTab() {
      if (tabs.getActiveTab() == 1) {
         okFindITEM2();
      } else if (tabs.getActiveTab() == 2) {
         okFindITEM3();
      } else if (tabs.getActiveTab() == 3) {
         okFindITEM4();
      } else if (tabs.getActiveTab() == 4) {
         okFindITEM6();
      } else if (tabs.getActiveTab() == 5) {
         okFindITEM7();
      } else if (tabs.getActiveTab() == 6) {
         okFindITEM9();
      } else if (tabs.getActiveTab() == 7) {
         okFindITEM11();
      } else if (tabs.getActiveTab() == 9) {
         okFindITEM13();
      } else if (tabs.getActiveTab() == 10) {
         okFindCallback();
      } else if (tabs.getActiveTab() == 12) {
         okFindDistribution();
      }
   }

   public void objectDetails() {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      if (itemlay2.isMultirowLayout()) {
         itemset2.storeSelections();
         itemset2.setFilterOn();
      }

      String lu_name = itemset2.getValue("LU_NAME");
      String key_ref = itemset2.getValue("KEY_REF");
      String page_url;
      String parameters = mgr.replace(key_ref, "^", "&");
      parameters = parameters.substring(0, parameters.length() - 1);

      // Added by Terry 20131108
      // Get page url from lu name config
      ASPCommand cmd = trans.addCustomFunction("GETLUPAGE", "General_Lu_Page_API.Get_Page_Url", "SLUDESC");
      cmd.addParameter("LU_NAME", lu_name);
      trans = mgr.perform(trans);
      page_url = Str.isEmpty(trans.getValue("GETLUPAGE/DATA/SLUDESC")) ? "" : trans.getValue("GETLUPAGE/DATA/SLUDESC");
      if (!Str.isEmpty(page_url))
         mgr.redirectTo(page_url + "?" + parameters);
      // Added end
      else
      {
         if ("EngPartRevision".equals(lu_name))
            mgr.redirectTo("../pdmcow/EngPartRevisionOvw.page?" + parameters);
         else if ("DocSubject".equals(lu_name))
            mgr.redirectTo("DocSubjectOvw.page?" + parameters);
         else if ("Project".equals(lu_name))
            mgr.redirectTo("../projw/ProjectDetails.page?" + parameters);
         else if ("Activity".equals(lu_name))
            mgr.redirectTo("../projw/Activity.page?" + parameters);
         else if ("ProdStructureHead".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("RecipeStructureHead".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("PlanStructureHead".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("ConfigStructureHead".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("ProdStructAlternate".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("RecipeStructAlternate".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("PlanStructAlternate".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("ConfigStructAlternate".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("ProdStructure".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("RecipeStructure".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("PlanStructure".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("ConfigStructure".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("ManufStructureHead".equals(lu_name))
            mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"
                  + parameters);
         else if ("RoutingHead".equals(lu_name))
            mgr.redirectTo("../mfgstw/RoutingHead.page?" + parameters);
         else if ("RoutingAlternate".equals(lu_name))
            mgr.redirectTo("../mfgstw/RoutingHead.page?" + parameters);
         else if ("RoutingOperation".equals(lu_name))
            mgr.redirectTo("../mfgstw/RoutingHead.page?" + parameters);
         else if ("PaymentPlanAuth".equals(lu_name))
            mgr.redirectTo("../invoiw/AuthorizePaymentPlanOvw.page?" + parameters);
         else if ("Delimitation".equals(lu_name))
            mgr.redirectTo("../equipw/Delimitation.page?" + parameters);
         else if ("EquipmentObjType".equals(lu_name))
            mgr.redirectTo("../equipw/EquipmentObjTypeOvw.page?" + parameters);
         else if ("EquipmentObject".equals(lu_name))
            mgr.redirectTo("../equipw/ObjectOvw.page?" + parameters);
         else if ("TypeDesignation".equals(lu_name))
            mgr.redirectTo("../equipw/TypeDesignationOvw.page?" + parameters);
         else if ("DelimitationOrder".equals(lu_name))
            mgr.redirectTo("../pcmw/DelimitationOrderTab.page?" + parameters);
         else if ("Permit".equals(lu_name))
            mgr.redirectTo("../pcmw/Permit.page?" + parameters);
         else if ("PmAction".equals(lu_name))
            mgr.redirectTo("../pcmw/PmAction.page?" + parameters);
         else if ("StandardJob".equals(lu_name))
            mgr.redirectTo("../pcmw/StandardJobOvw.page?" + parameters);
         else if ("ShopOrd".equals(lu_name))
            mgr.redirectTo("../shporw/ShopOrd.page?" + parameters);
         else if ("ShopMaterialAlloc".equals(lu_name))
            mgr.redirectTo("../shporw/ShopMaterialAlloc.page?" + parameters);
         else if ("ShopOrderProp".equals(lu_name))
            mgr.redirectTo("../shporw/ShopOrderProp.page?" + parameters);
         else if ("PlantArticle".equals(lu_name))
            mgr.redirectTo("../pladew/PlantDesignPartDtlStd.page?" + parameters);
         else if ("PlantCable".equals(lu_name))
            mgr.redirectTo("../pladew/PlantObjectDtlStd.page?" + parameters);
         else if ("PlantChannel".equals(lu_name))
            mgr.redirectTo("../pladew/PlantObjectDtlStd.page?" + parameters);
         else if ("PlantCircuit".equals(lu_name))
            mgr.redirectTo("../pladew/PlantObjectDtlStd.page?" + parameters);
         else if ("PlantConnectionPoint".equals(lu_name))
            mgr.redirectTo("../pladew/PlantObjectDtlStd.page?" + parameters);
         else if ("PlantIoCard".equals(lu_name))
            mgr.redirectTo("../pladew/PlantObjectDtlStd.page?" + parameters);
         else if ("PlantObject".equals(lu_name))
            mgr.redirectTo("../pladew/PlantObjectDtlStd.page?" + parameters);
         else if ("PlantSignal".equals(lu_name))
            mgr.redirectTo("../pladew/PlantObjectDtlStd.page?" + parameters);
         else if ("ActiveRound".equals(lu_name))
            mgr.redirectTo("../pcmw/ActiveRound.page?" + parameters);
         else if ("ActiveSeparate".equals(lu_name))
            mgr.redirectTo("../pcmw/ActiveSeparate2.page?" + parameters);
         else if ("HistoricalRound".equals(lu_name))
            mgr.redirectTo("../pcmw/HistoricalRound.page?" + parameters);
         else if ("HistoricalSeparate".equals(lu_name))
            mgr.redirectTo("../pcmw/HistoricalSeparateRMB.page?" + parameters);
         else if ("CcCase".equals(lu_name))
            mgr.redirectTo("../callcw/CcCaseDetail.page?" + parameters);
         else if ("CcCaseTask".equals(lu_name))
            mgr.redirectTo("../callcw/CcTaskDetail.page?" + parameters);
         else if ("CcCaseSolution".equals(lu_name))
            mgr.redirectTo("../callcw/CcCaseSolution.page?" + parameters);
         // Bug Id 72420, Start
         else if ("ProjectMiscProcurement".equals(lu_name))
            mgr.redirectTo("../projw/ProjectMiscProcurementOvw1.page?"
                  + parameters);
         // Bug Id 72420, End
         // Bug Id 72471, Start
         else if ("DocIssue".equals(lu_name))
            mgr.redirectTo("DocIssue.page?" + parameters);
         // Bug Id 72471, End
         // Bug Id 89200, start
         else if ("InventoryPart".equals(lu_name))
            mgr.redirectTo("../invenw/InventoryPart.page?" + parameters);
         // Bug Id 89200, end
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANTPER: This operation cannot perform on the selected record."));
      }

      itemset2.unselectRows();
      itemset2.setFilterOff();
   }

   protected void setCurrentDocument() {
      ASPManager mgr = getASPManager();

      String parent_doc_class = mgr.readValue("PARENT_DOC_CLASS");
      String parent_doc_no = mgr.readValue("PARENT_DOC_NO");
      String parent_doc_sheet = mgr.readValue("PARENT_DOC_SHEET");
      String parent_doc_rev = mgr.readValue("PARENT_DOC_REV");
      String doc_class = mgr.readValue("CURRENT_DOC_CLASS");
      String doc_no = mgr.readValue("CURRENT_DOC_NO");
      String doc_sheet = mgr.readValue("CURRENT_DOC_SHEET");
      String doc_rev = mgr.readValue("CURRENT_DOC_REV");

      // Check if the parent document exits in the current set..
      headset.first();
      if (!parent_doc_class.equals(headset.getValue("DOC_CLASS"))
            || !parent_doc_no.equals(headset.getValue("DOC_NO"))
            || !parent_doc_sheet.equals(headset.getValue("DOC_SHEET"))
            || !parent_doc_rev.equals(headset.getValue("DOC_REV"))) {
         findDocumentsInStructure(parent_doc_class, parent_doc_no,
               parent_doc_sheet, parent_doc_rev);
      }

      // Now find the matching sub document..
      headset.first();
      do {
         if (doc_class.equals(headset.getValue("DOC_CLASS"))
               && doc_no.equals(headset.getValue("DOC_NO"))
               && doc_sheet.equals(headset.getValue("DOC_SHEET"))
               && doc_rev.equals(headset.getValue("DOC_REV"))) {
            headlay.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
            refreshActiveTab();

            // return, with this being
            // the current document..
            return;
         }
      } while (headset.next());
   }

   public void transferToDocGroup() {
      ASPManager mgr = getASPManager();

      if (itemlay6.isMultirowLayout()) {
         itemset6.storeSelections();
         itemset6.setFilterOn();
      }

      String sGroupId = itemset6.getValue("GROUP_ID");
      itemset6.setFilterOff();

      // Bug 57781, Start, Changed the redirect page to DocumentBasicTempl.page
      if (mgr.isEmpty(sGroupId))
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUECANTPERSONINFO: This operation cannot perform on the selected record."));
      else
         mgr.redirectTo("DocumentBasicTempl.page?GROUP_ID="
               + mgr.URLEncode(sGroupId));
      // Bug 57781, End
   }

   public void transferToPersonInfo() {
      ASPManager mgr = getASPManager();

      if (itemlay6.isMultirowLayout()) {
         itemset6.storeSelections();
         itemset6.setFilterOn();
      }

      String personId = itemset6.getValue("PERSON_ID");
      itemset6.setFilterOff();

      if (mgr.isEmpty(personId))
         mgr.showAlert(mgr
               .translate("DOCMAWDOCISSUECANTPERSONINFO: This operation cannot perform on the selected record."));
      else
         mgr.redirectTo("../enterw/PersonInfo.page?PERSON_ID="
               + mgr.URLEncode(personId));
   }

   public void replaceRevision() {
      ASPManager mgr = getASPManager();

      if (itemlay3.isMultirowLayout())
         itemset3.goTo(itemset3.getRowSelected());

      // make the current row editable..
      itemlay3.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);

      // since a new revision needs to be specified, the
      // following fileds must be editable..
      mgr.getASPField("SUB_DOC_CLASS").unsetReadOnly();
      mgr.getASPField("SUB_DOC_NO").unsetReadOnly();
      mgr.getASPField("SUB_DOC_SHEET").unsetReadOnly();
      mgr.getASPField("SUB_DOC_REV").unsetReadOnly();

      // modify attributes and value of field REPLACE_REVISION_TITLE
      mgr.getASPField("REPLACE_REVISION_TITLE").unsetHidden();

      itemlay3.setLabelSpan("REPLACE_REVISION_TITLE", 6);

      // modify title to let the user know what he is doing..
      itemblk3
            .setTitle(mgr
                  .translate("DOCMAWDOCISSUEREPLACEREVISIONBLOCKTITLE: Replace Revision..."));

      // diable unncessary command bar actions..
      itembar3.disableCommand(ASPCommandBar.BACKWARD);
      itembar3.disableCommand(ASPCommandBar.FORWARD);

      // set a context variable to indicate that the current
      // revision has to be replced when saving..
      ctx.writeFlag("REPLACE_REVISION", true);
   }

   public void replaceRevisionFinish() {
      // Replace revision..
      ASPManager mgr = getASPManager();

      String replace_doc_class = ctx.readValue("REPLACE_DOC_CLASS", "");
      String replace_doc_no = ctx.readValue("REPLACE_DOC_NO", "");
      String replace_doc_sheet = ctx.readValue("REPLACE_DOC_SHEET", "");
      String replace_doc_rev = ctx.readValue("REPLACE_DOC_REV", "");

      ASPCommand cmd = trans.addCustomCommand("REPLACE_REVISION",
            "Doc_Structure_API.Replace_Issue_");
      cmd.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      cmd.addParameter("SUB_DOC_CLASS", replace_doc_class);
      cmd.addParameter("SUB_DOC_NO", replace_doc_no);
      cmd.addParameter("SUB_DOC_SHEET", replace_doc_sheet);
      cmd.addParameter("SUB_DOC_REV", replace_doc_rev);
      cmd.addParameter("DOC_CLASS", itemset3.getValue("SUB_DOC_CLASS"));
      cmd.addParameter("DOC_NO", itemset3.getValue("SUB_DOC_NO"));
      cmd.addParameter("DOC_SHEET", itemset3.getValue("SUB_DOC_SHEET"));
      cmd.addParameter("DOC_REV", itemset3.getValue("SUB_DOC_REV"));

      if (bSetStructure) {
         cmd = trans.addCustomCommand("SET_STRUCTURE_TYPE",
               "Doc_Title_API.Set_Structure_");
         cmd.addParameter("DOC_CLASS", itemset3.getValue("SUB_DOC_CLASS"));
         cmd.addParameter("DOC_NO", itemset3.getValue("SUB_DOC_NO"));
         cmd.addParameter("NNOOFCHILDREN", "1");// Number type

      }
      trans = mgr.perform(trans);

      // Refresh the tab to reflect the modifications..
      okFindITEM3();
   }

   public void transferToDocInfoFromConsistOf() {
      ASPManager mgr = getASPManager();

      if (itemlay3.isMultirowLayout()) {
         itemset3.storeSelections();
         itemset3.setFilterOn();
      } else
         itemset3.selectRow();

      if (itemset3.countRows() > 0)
      {
         String page_url = itemset3.getValue("ITEM3_PAGE_URL");
         mgr.redirectTo(page_url + "?DOC_CLASS="
               + mgr.URLEncode(itemset3.getValue("SUB_DOC_CLASS")) + "&DOC_NO="
               + mgr.URLEncode(itemset3.getValue("SUB_DOC_NO")) + "&DOC_SHEET="
               + mgr.URLEncode(itemset3.getValue("SUB_DOC_SHEET")) + "&DOC_REV="
               + mgr.URLEncode(itemset3.getValue("SUB_DOC_REV")));
      }
   }

   public void transferToDocInfoFromWhereUsed() {
      ASPManager mgr = getASPManager();

      if (itemlay4.isMultirowLayout()) {
         itemset4.storeSelections();
         itemset4.setFilterOn();
      } else
         itemset4.selectRow();

      if (itemset4.countRows() > 0)
      {
         String page_url = itemset4.getValue("ITEM4_PAGE_URL");
         mgr.redirectTo(page_url + "?DOC_CLASS="
               + mgr.URLEncode(itemset4.getValue("DOC_CLASS")) + "&DOC_NO="
               + mgr.URLEncode(itemset4.getValue("DOC_NO")) + "&DOC_SHEET="
               + mgr.URLEncode(itemset4.getValue("DOC_SHEET")) + "&DOC_REV="
               + mgr.URLEncode(itemset4.getValue("DOC_REV")));
      }
   }

   //
   // Methods on Handling Strings
   //

   private int stringIndex(String mainString, String subString) {
      int a = mainString.length();
      int index = -1;

      for (int i = 0; i < a; i++)
         if (mainString.startsWith(subString, i)) {
            index = i;
            break;
         }
      return index;
   }

   private String replaceString(String mainString, String subString,
         String replaceString) {
      String retString = "";
      int posi;
      posi = stringIndex(mainString, subString);

      while (posi != -1) {
         retString += mainString.substring(0, posi) + replaceString;
         mainString = mainString.substring(posi + subString.length(),
               mainString.length());
         posi = stringIndex(mainString, subString);
      }
      return retString + mainString;
   }

   public int howManyOccurance(String str, char c) {
      int strLength = str.length();
      int occurance = 0;
      for (int index = 0; index < strLength; index++)
         if (str.charAt(index) == c)
            occurance++;
      return occurance;
   }

   protected String[] split(String str, char c) {
      int length_ = howManyOccurance(str, c);
      int strLength = str.length();
      int occurance = 0;
      int index = 0;
      String[] tempString = new String[length_ + 1];

      while (strLength > 0) {
         occurance = str.indexOf(c);
         if (occurance == -1) {
            tempString[index] = str;
            break;
         } else {
            tempString[index++] = str.substring(0, occurance);
            str = str.substring(occurance + 1, strLength);
            strLength = str.length();
         }
      }
      return tempString;
   }

   // Mark the selected rows with the status
   private void markSelection(String status) {
      int selectedrows = headset.countRows();
      headset.first();
      do {
         headset.markRow(status);
      } while (headset.next());

   }

   public void activateObjects() {
      tabs.setActiveTab(1);
      okFindITEM2();
   }

   public void activateConsistsOf() {
      tabs.setActiveTab(2);
      okFindITEM3();
   }

   public void activateWhereUsed() {
      tabs.setActiveTab(3);
      okFindITEM4();
   }

   public void activateAccess() {
      tabs.setActiveTab(4);
      okFindITEM6();
   }

   public void activateHistory() {
      tabs.setActiveTab(5);
      checkEnableHistoryNewRow();
      okFindITEM7();
   }

   public void activateFileReferences() {
      tabs.setActiveTab(6);
      okFindITEM9();
   }

   public void activateSendDept() {
      /*if (DocmawConstants.EXCH_RECEIVE.equals(getCurrentPageDocClass())
            || DocmawConstants.EXCH_SEND.equals(getCurrentPageDocClass())) {
         tabs.setActiveTab(1);
      } else {
         tabs.setActiveTab(7);
      }*/
      tabs.setActiveTab(7);
      okFindITEM11();
   }

   public void activateAttachmentUpload()
   {
      tabs.setActiveTab(8);
      /*if (DocmawConstants.EXCH_SEND.equals(getCurrentPageDocClass()) || DocmawConstants.EXCH_RECEIVE.equals(getCurrentPageDocClass())) {
      }*/
   }
   
   public void activateHistoryRevision() {
      tabs.setActiveTab(9);
      okFindITEM13();
   }
   
   public void activateCallback()
   {
      tabs.setActiveTab(10);
      okFindCallback();
   }
   
   public void activateTempAttach()
   {
      tabs.setActiveTab(11);
   }
   
   public void activateDistribution()
   {
      tabs.setActiveTab(12);
      okFindDistribution();
   }
   

//   public void connectObject() {
//      headset.storeSelections();
//      newRowITEM2();
//   }

   public void preDefine() throws FndException {

      super.preDefine();

      String sDocDis, sCreateNewRev, sSetToObs;

      ASPManager mgr = getASPManager();
      disableConfiguration();

      headblk = mgr.newASPBlock("HEAD");
      // Bug Id 72471, enabled docman for DocIssue. Removed the disableDocMan()
      // command

      if (mgr.isExplorer()) {
         sDocDis = mgr.translate("DOCMAWDOCISSUEDOCDIS: Document Distribution...");
         sCreateNewRev = mgr.translate("DOCMAWDOCISSUECNEWREVISION: Create New Revision...");
         sSetToObs = mgr.translate("DOCMAWDOCISSUESETDOCTOOBS: Set Document to Obsolete...");
      } else {
         sDocDis = mgr.translate("DOCMAWDOCISSUEDIS: Distribute...");
         sCreateNewRev = mgr.translate("DOCMAWDOCISSUECNEWREV: Create new rev...");
         sSetToObs = mgr.translate("DOCMAWDOCISSUESETTOOBS: Set to Obsolete...");
      }

      headblk.addField("OBJID").setHidden();

      headblk.addField("OBJVERSION").setHidden();

      headblk.addField("OBJSTATE").setHidden();

      headblk.addField("OBJEVENTS").setHidden();

      customizeHeadblk();
      //
      // Hidden Fields
      //

      customizeFieldFromDocClass();

      headblk.addField("FROM_DOC_NO").setInsertable()
            .setLabel("DOCISSUEFROMDOCNO: From Doc No").setHidden()
            .setSize(120);

      headblk.addField("FROM_DOC_SHEET").setInsertable()
            .setLabel("DOCISSUEFROMDOCSHEET: From Doc Sheet").setHidden()
            .setSize(10);

      headblk.addField("FROM_DOC_REV").setInsertable()
            .setLabel("DOCISSUEFROMDOCREV: From Doc Rev").setHidden()
            .setSize(6);

      headblk.addField("PROJECT_NO").setInsertable()
            .setLabel("DOCISSUEPROJECTNO: Project No").setSize(5).setHidden();
      headblk.addField("ZONE_NO").setInsertable()
            .setLabel("DOCISSUEZONENO: Zone No").setSize(5).setHidden();

      headblk
            .addField("IS_ELE_DOC")
            .setCheckBox("FALSE,TRUE")
//            .setFunction(
//                  "EDM_FILE_API.Have_Edm_File(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)")
            .setReadOnly().setHidden().setLabel("DOCTITLEISELEDOC: Is Ele Doc")
            .setSize(5);

      headblk.addField("DOC_NO").setSize(20).setMaxLength(120).setReadOnly()
            .setUpperCase().setDynamicLOV("DOC_TITLE", "DOC_CLASS").setHidden()
            .setLabel("DOCMAWDOCISSUEDOCNO: Doc No");

      headblk.addField("DOC_SHEET").setSize(20).setMaxLength(10).setReadOnly()
            .setUpperCase().setHidden().setDynamicLOV("DOC_ISSUE_LOV1")
            .setLabel("DOCMAWDOCISSUEDOCSHEET: Doc Sheet");

      // bug id 77080 Start
      headblk.addField("EDM_DB_STATE").setHidden();
      // bug id 77080 End

      headblk.addField("STRUCTURE").setCheckBox("FALSE,TRUE").setReadOnly()
            .setHidden().setLabel("DOCMAWDOCISSUEMAINBLKSTRUC: Structure");

      headblk.addField("ACCESS_CONTROL").setSize(20).setMaxLength(20)
            .setSelectBox().setReadOnly().setHidden()
            .enumerateValues("Doc_User_Access_Api")
            .setLabel("DOCMAWDOCISSUEACCESSCONTROL: Access");

      headblk.addField("DT_RELEASED", "Date").setSize(20).setReadOnly()
            .setHidden().setLabel("DOCMAWDOCISSUEDTRELEASED: Date Released");

      headblk.addField("REV_NO", "Number").setSize(20).setAlignment("RIGHT").
      // Bug Id 90011, start
            setReadOnly().setHidden().
            // Bug Id 90011, end
            setLabel("DOCMAWDOCISSUEOVWREVNO: Rev No");

      headblk.addField("ALTDOCNO").setDbName("ALTERNATE_DOCUMENT_NUMBER")
            .setSize(20).setMaxLength(120).setReadOnly().setDefaultNotVisible()
            .setHidden().setLabel("DOCMAWDOCISSUEALTERNATEDOCNO: Alt Doc No");

      headblk.addField("DESCRIPTION1").setSize(20).setMaxLength(100)
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEDESCRIPTION1: Description 1");

      headblk.addField("DESCRIPTION2").setSize(20).setMaxLength(100)
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEDESCRIPTION2: Description 2");

      headblk.addField("DESCRIPTION3").setSize(20).setMaxLength(100)
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEDESCRIPTION3: Description 3");

      headblk.addField("DESCRIPTION4").setSize(20).setMaxLength(100)
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEDESCRIPTION4: Description 4");

      headblk.addField("DESCRIPTION5").setSize(20).setMaxLength(100)
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEDESCRIPTION5: Description 5");

      headblk.addField("DESCRIPTION6").setSize(20).setMaxLength(100)
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEDESCRIPTION6: Description 6");

      headblk.addField("NEXT_DOC_SHEET").setSize(20).setMaxLength(10)
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUENEXTSHEET: Next Doc Sheet");

      headblk.addField("NO_OF_SHEETS", "Number").setSize(20).setMaxLength(5)
            .setReadOnly().setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUENOOFSHEETS: Total Sheets");

      headblk.addField("SHEET_ORDER").setSize(20).setMaxLength(10)
            .setDefaultNotVisible().setHidden()
            .setLabel("SHEETORDER: Sheet Order");

      headblk.addField("LU_NAME").setHidden();

      headblk.addField("KEY_REF").setHidden();

      headblk.addField("FORM_NAME").setHidden()
            .setFunction("'Document Management'");

      //
      // Fields in the 'General' group box
      //

      headblk.addField("DOC_REV_TEXT").setSize(20).setMaxLength(2000)
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEDOCREVTEXT: Revision Text");

      headblk.addField("DT_DOC_REV", "Date").setSize(20).setReadOnly()
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEDTDOCREV: Revision Date");

      headblk.addField("ACCESS_CONTROL_DB").setHidden();

      headblk.addField("FILE_STATUS_DES").setHidden().setFunction("''");

      headblk.addField("DT_OBSOLETE", "Date").setSize(20).setReadOnly()
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEDTOBSOLETE: Date Obsolete");

      headblk.addField("INFO").setSize(20).setMaxLength(2000)
            .setDefaultNotVisible().setHidden()
            .setLabel("DOCMAWDOCISSUEINFO: Note");

      headblk.addField("LANGUAGE_CODE").setSize(20).setMaxLength(2)
            .setDefaultNotVisible().setHidden()
            .setDynamicLOV("APPLICATION_LANGUAGE")
            .setLabel("DOCMAWDOCISSUELANGUAGECODE: Language");

      // Bug Id 70553, Start
      headblk.addField("FORMAT_SIZE").setSize(20).setMaxLength(6)
            .setUpperCase().setDefaultNotVisible().setHidden()
            .setDynamicLOV("DOC_FORMAT")
            .setLabel("DOCMAWDOCISSUEFORMATSIZE: Format");
      // Bug Id 70553, End

      headblk.addField("DOC_RESP_DEPT").setSize(20).setMaxLength(4)
            .setUpperCase().setHidden().setDefaultNotVisible()
            .setLabel("DOCMAWDOCISSUEDOCRESPDEPT: Responsible Department");

      headblk.addField("DOC_RESP_SIGN").setSize(20).setMaxLength(30)
            .setHidden().setDefaultNotVisible()
            .setLabel("DOCMAWDOCISSUEDOCRESPSIGN: Person");

      // Bug Id 70553, Start
      headblk.addField("REASON_FOR_ISSUE").setSize(20).setUpperCase()
            .setHidden().setDefaultNotVisible()
            .setDynamicLOV("DOCUMENT_REASON_FOR_ISSUE")
            .setLabel("DOCMAWDOCISSUEREASONFORISSUE: Reason For Issue");
      // Bug Id 70553, End

      headblk
            .addField("APPROVAL_UPDATE")
            .setCheckBox("FALSE,TRUE")
            .setDefaultNotVisible()
            .setHidden()
            .setLabel(
                  "DOCMAWDOCISSUEAPPROVALUPDATE: Update allowed during approval");

      headblk.addField("SCALE").setDefaultNotVisible().setHidden()
            .setDynamicLOV("DOC_SCALE_LOV1")
            .setLabel("DOCMAWDOCISSUESCALE: Scale");

      //
      // Fields in the 'Development' group box
      //

      headblk.addField("DT_PLANNED_START", "Date").setSize(20).setHidden()
            .setLabel("DOCMAWDOCISSUEDTPLANNEDSTART: Planned Start");

      headblk.addField("DT_PLANNED_FINISH", "Date").setSize(20).setHidden()
            .setLabel("DOCMAWDOCISSUEDTPLANNEDFINISH: Planned Finish");

      headblk.addField("DT_ACTUAL_FINISH", "Date").setSize(20).setHidden()
            .setLabel("DOCMAWDOCISSUEDTACTUALFINISH: Actual Finish");

      headblk.addField("DOC_STATUS", "Number", "0.00##%").setSize(20)
            .setMaxLength(5).setDefaultNotVisible().setHidden()
            .setCustomValidation("DOC_STATUS", "DOC_STATUS")
            .setLabel("DOCMAWDOCISSUEDOCSTATUS: Progress");

      headblk
            .addField("USER_SIGN")
            .setSize(20)
            .setMaxLength(30)
            .setReadOnly()
            .setHidden()
            .setDefaultNotVisible()
            .setDynamicLOV("PERSON_INFO_USER")
            .setLOVProperty(
                  "TITLE",
                  mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id"))
            .setLabel("DOCMAWDOCISSUEUSERSIGN: Modified By");

      headblk.addField("DT_CHG", "Date").setSize(20).setHidden()
            .setDefaultNotVisible().setReadOnly()
            .setLabel("DOCMAWDOCISSUEDTCHG: Date Modified");

      headblk.addField("DAYS_EXPIRED", "Number").setSize(20).setMaxLength(6)
            .setHidden().setDefaultNotVisible()
            .setLabel("DOCMAWDOCISSUEDAYSEXPIRED: Expiration Days");

      headblk.addField("GETEDITACCESS").setHidden().setFunction("''");

      headblk.addField("GETVIEWACCES").setHidden().setFunction("''");

      headblk.addField("PROFILE_ID").setSize(20).setMaxLength(10)
            .setUpperCase().setHidden().setDefaultNotVisible()
            .setDynamicLOV("APPROVAL_PROFILE")
            .setLabel("DOCMAWDOCISSUEPROFILEID: Approval Template");

      headblk.addField("EDM_STATUS").setHidden();

      headblk.addField("DOC_CONTROL").setHidden();

      headblk.setView("DOC_ISSUE_REFERENCE");
      headblk
            .defineCommand(
                  "DOC_ISSUE_API",
                  "New__, Modify__, Remove__, PROMOTE_TO_APP_IN_PROGRESS__, PROMOTE_TO_APPROVED__, PROMOTE_TO_RELEASED__, PROMOTE_TO_OBSOLETE__, DEMOTE_TO_PRELIMINARY__,SET_DOC_REV_TO_APPROVED__");
      headblk.enableFuncFieldsNonSelect();
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);

      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.defineCommand(headbar.CANCELFIND, "cancelFind");
      headbar.defineCommand(headbar.SAVERETURN, "saveReturnITEM");
      headbar.defineCommand(headbar.DELETE, "deleteRow");
      
      headbar.addSecureCustomCommand("CreateDocConnection", "DOCMAWCREDOCCONN: Create Doc Connection...", "Doc_Structure_API.Make_Connection_", "../common/images/toolbar/" + mgr.getLanguageCode() + "/connectDocument.gif", true);
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_VIEW, "DOC_ISSUE_LOV");
//      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_VIEW_PARAMS, "ATTACH_TYPE DOC_CLASS");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_WHERE, "NVL(Doc_Class_API.Get_Comp_Doc(doc_class), 'FALSE') = 'TRUE'");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_PARPA, "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_FIEPA, "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_TARG_FIE, "SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_TARG_PKG, "Doc_Structure_API");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_ADD_TAR_VIEW, "DOC_STRUCTURE");
      headbar.removeFromMultirowAction("CreateDocConnection");
      
      headbar.addCommandValidConditions("CreateDocConnection", "DOC_CLASS", "Enable", 
            DocmawConstants.PROJ_RECEIVE + ";" +
            DocmawConstants.PROJ_SEND + ";" +
            DocmawConstants.PROJ_PROPHASE + ";" +
            DocmawConstants.PROJ_CONSTRUCT + ";" +
            DocmawConstants.PROJ_DESIGN + ";" +
            DocmawConstants.PROJ_EQUIPMENT + ";" +
            DocmawConstants.PROJ_TEST + ";" +
            DocmawConstants.PROJ_COMPLETION + ";" +
            DocmawConstants.PROJ_STANDARD);
      
      headbar.addSecureCustomCommand("CreateDocAttachment", "DOCMAWCREDOCATTACH: Create Doc Attachment...", "Doc_Structure_API.Make_Connection_", "../common/images/toolbar/" + mgr.getLanguageCode() + "/addAttachment.gif", true);
      headbar.setCmdProperty("CreateDocAttachment", headbar.CMD_PRO_VIEW, "DOC_ISSUE_LOV");
      headbar.setCmdProperty("CreateDocAttachment", headbar.CMD_PRO_VIEW_PARAMS, "ATTACH_TYPE DOC_CLASS");
      headbar.setCmdProperty("CreateDocAttachment", headbar.CMD_PRO_WHERE, "NVL(Doc_Class_API.Get_Comp_Doc(doc_class), 'FALSE') = 'TRUE'");
      headbar.setCmdProperty("CreateDocAttachment", headbar.CMD_PRO_PARPA, "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      headbar.setCmdProperty("CreateDocAttachment", headbar.CMD_PRO_FIEPA, "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,'Transfer'");
      headbar.setCmdProperty("CreateDocAttachment", headbar.CMD_PRO_TARG_FIE, "SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV,ASSOCIATE_ID");
      headbar.setCmdProperty("CreateDocAttachment", headbar.CMD_PRO_TARG_PKG, "Doc_Structure_API");
      headbar.setCmdProperty("CreateDocAttachment", headbar.CMD_PRO_ADD_TAR_VIEW, "DOC_STRUCTURE");
      headbar.removeFromMultirowAction("CreateDocAttachment");
      headbar.addCommandValidConditions("CreateDocAttachment", "DOC_CLASS", "Enable", DocmawConstants.PROJ_SEND);
      headbar.addCommandValidConditions("CreateDocAttachment", "SIGN_STATUS_DB", "Disable", "SENDED;PARTIALLYSIGNED;SIGNED");
      
      headbar.addSecureCustomCommand("CreateUnit", "DOCMAWCREATEUNIT: Create Unit...", "DOC_ISSUE_TARGET_ORG_API.Create_Target_Org", "../common/images/toolbar/" + mgr.getLanguageCode() + "/addZone.gif", true);
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_VIEW, "GENERAL_ZONE_LOV");
      // headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_WHERE, "COMM_CODE IS NOT NULL");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_PARPA, "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_FIEPA, "ZONE_NO,'FALSE','FALSE'");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_TARG_FIE, "ORG_NO,SIGN_STATUS,IS_MAIN");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_TARG_PKG, "DOC_ISSUE_TARGET_ORG_API");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_ADD_TAR_VIEW, "DOC_ISSUE_TARGET_ORG");
      headbar.removeFromMultirowAction("CreateUnit");
      headbar.addCommandValidConditions("CreateUnit", "DOC_CLASS", "Enable", DocmawConstants.PROJ_SEND + ";" + DocmawConstants.EXCH_SEND);
      headbar.addCommandValidConditions("CreateUnit", "SIGN_STATUS_DB", "Disable", "SENDED;PARTIALLYSIGNED;SIGNED");

      // State Changing Operations
      headbar.addSecureCustomCommand("setObsolete", sSetToObs,
            "DOC_ISSUE_API.PROMOTE_TO_OBSOLETE__"); // Bug Id 70286
      headbar.addCustomCommandSeparator();

      // File Operations
      headbar.addSecureCustomCommand("checkInDocument",
            mgr.translate("DOCMAWDOCISSUECHECKINDOC: Check In Document"),
            "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); // Bug Id 70286
      headbar.addSecureCustomCommand("viewOriginal",
            mgr.translate("DOCMAWDOCISSUEVIEVOR: View Document"),
            "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); // Bug Id 70286
      headbar.addSecureCustomCommand("editDocument",
            mgr.translate("DOCMAWDOCISSUEEDITDOC: Edit Document"),
            "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); // Bug Id 70286
      headbar
            .addSecureCustomCommand(
                  "checkInSelectDocument",
                  mgr.translate("DOCMAWDOCISSUECHECKINSELECTEDDOC: Check In Selected Document"),
                  "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); // Bug Id
                                                                    // 70286
      headbar.addSecureCustomCommand("undoCheckOut", mgr
            .translate("DOCMAWDOCISSUEUNDOCHECKOUT: Undo Check Out Document"),
            "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); // Bug Id 70286
      headbar.addSecureCustomCommand("printDocument",
            mgr.translate("DOCMAWDOCISSUEPRINTDOC: Print Document"),
            "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); // Bug Id 70286
      headbar.addSecureCustomCommand("deleteDocument",
            mgr.translate("DOCMAWDOCISSUEDELETEDOC: Delete Document Revision"),
            "DOC_ISSUE_API.Remove__"); // Bug Id 70286
      headbar.addSecureCustomCommand("deleteDocumentFile",
            mgr.translate("DOCMAWDOCISSUEDELETEDOCFILE: Delete Document File"),
            "EDM_FILE_API.Remove__"); // Bug Id 70286
      headbar
            .addSecureCustomCommand(
                  "deleteSelectDocFile",
                  mgr.translate("DOCMAWDOCISSUEDELETESELECTDOCFILE: Delete Selected Document File"),
                  "EDM_FILE_API.Remove__");
      headbar.addSecureCustomCommand("downloadDocuments",
            mgr.translate("DOCMAWDOCISSUEDOWNLOADDOC: Download Documents..."),
            "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");
      headbar.addSecureCustomCommand("downloadDocsAndSubDocs",
            mgr.translate("DOCMAWDOCISSUEDOWNLOADDOCANSSUB: Download Docs And Transfered Docs..."),
            "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");
      
      headbar.addCustomCommandSeparator();
      headbar.addSecureCustomCommand("setToCopyFile",
            mgr.translate("DOCMAWDOCISSUECOPYFILETO: Copy File To..."),
            "DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); // Bug Id 70286
      headbar.addCustomCommand("userSettings",
            mgr.translate("DOCMAWDOCISSUEUSERSETTINGS: User Settings..."));
      headbar.addCustomCommandSeparator();
      // Bug ID 77080, Start
//      headbar.addCustomCommand("resetStatus",
//            mgr.translate("DOCMAWDOCISSUERESETSTATUS: Reset File Status"));
      // Bug ID 77080, End
      headbar.addCustomCommandSeparator();

      // Added by Terry 20130314
      headbar.addCommandValidConditions("editDocument", "DOC_CONTROL",
            "Disable", "TRUE");
      headbar.addCommandValidConditions("checkInSelectDocument", "DOC_CONTROL",
            "Disable", "TRUE");
      headbar.addCommandValidConditions("undoCheckOut", "DOC_CONTROL",
            "Disable", "TRUE");
      /*headbar.addCommandValidConditions("deleteSelectDocFile", "DOC_CONTROL",
            "Disable", "TRUE");*/
      headbar.addCommandValidConditions("setToCopyFile", "DOC_CONTROL",
            "Disable", "TRUE");
      // Added end

      // Bug Id 54528, Start , Created new sub menu "Templates" , Added
      // commands.
//      headbar.addSecureCustomCommand("connectObject",
//            "DOCMAWDOCISSUEADDTOBCCONNETOBJECT: Connect Object",
//            "DOC_REFERENCE_OBJECT_API.New__"); // Bug Id 70286
      headbar.addCustomCommandSeparator();

      // Bug Id 54528, End

      // Document Structure
      headbar
            .addCustomCommand(
                  "showStructureInNavigator",
                  mgr.translate("DOCMAWDOCISSUESHOWSTRUCTUREINNAVIGATOR: Show In Navigator"));
      headbar.addSecureCustomCommand("setStructAttribAll", mgr
            .translate("DOCMAWDOCISSUESTRUCATTRSET: Set Structure Attribute"),
            "DOC_TITLE_API.Set_Structure_All_");// Bug Id 70286
      headbar
            .addSecureCustomCommand(
                  "unsetStructAttribAll",
                  mgr.translate("DOCMAWDOCISSUESTRUCATTRUNSET: Unset Structure Attribute"),
                  "DOC_TITLE_API.Unset_Structure_All_"); // Bug Id 70286
      /*
      headbar.addCustomCommandGroup("STRUCTURE", mgr
            .translate("DOCMAWDOCISSUEDOCUMENTSTRUCTURE: Document Structure"));
      headbar.setCustomCommandGroup("showStructureInNavigator", "STRUCTURE");
      headbar.setCustomCommandGroup("setStructAttribAll", "STRUCTURE");
      headbar.setCustomCommandGroup("unsetStructAttribAll", "STRUCTURE");
      */
      
      headbar.disableCustomCommand("showStructureInNavigator");
      headbar.disableCustomCommand("setStructAttribAll");
      headbar.disableCustomCommand("unsetStructAttribAll");
      
      // headbar.addCustomCommandSeparator();
      // DMPR303 start
//      headbar.addSecureCustomCommand("createLink",
//            mgr.translate("DOCMAWDOCISSUECREATLINK: Create Document Link..."),
//            "DOC_ISSUE_API.LATEST_REVISION"); // Bug Id 70286
      // headbar.addCustomCommandSeparator();
      // DMPR303 End.
      headbar.addSecureCustomCommand("createNewRevision", sCreateNewRev,
            "DOC_ISSUE_API.NEW_REVISION2__"); // Bug Id 70286
//      headbar.addSecureCustomCommand("createNewSheet",
//            mgr.translate("DOCMAWDOCISSUECREATENEWSHEET: Create New Sheet..."),
//            "DOC_ISSUE_API.CREATE_NEW_SHEET__"); // Bug Id 70286
//      headbar.addSecureCustomCommand("mandatorySettings",
//            "DOCMAWDOCISSUEMADATORY: Configure Mandatory Search Fields...",
//            "DOCMAN_DEFAULT_API.Set_Default_Value_");// Bug Id 67105 //Bug Id
                                                     // 70286

      headbar.enableMultirowAction();
      // w.a following commands are not supportive for multi row actions
      headbar.removeFromMultirowAction("undoCheckOut");

      headbar.removeFromMultirowAction("createNewRevision");
//      headbar.removeFromMultirowAction("createNewSheet");
//      headbar.removeFromMultirowAction("mandatorySettings");// Bug Id 67105

      // Added by Terry 20121022
      headbar.removeFromMultirowAction("checkInSelectDocument");
      headbar.removeFromMultirowAction("deleteSelectDocFile");
      headbar.removeFromMultirowAction("checkInDocument");
      headbar.removeFromMultirowAction("viewOriginal");
      headbar.removeFromMultirowAction("editDocument");
      headbar.removeFromMultirowAction("checkInSelectDocument");
      headbar.removeFromMultirowAction("undoCheckOut");
      headbar.removeFromMultirowAction("printDocument");
      headbar.removeFromMultirowAction("setToCopyFile");
      // Added end
      
      // Added by Terry 20140414
      // Document control menu
      headbar.addSecureCustomCommand("createDocDistribution", mgr.translate("DOCMAWDOCISSUECREATEDIST: Create Distribution..."), "DOC_DISTRIBUTION_CTL_API.New_Doc_Distribution");
      // Added end

      // Added by lqw 20130801
      headbar.addSecureCustomCommand("sign", mgr.translate("DOCMAWDOCISSUESIGNTHEDOC: Sign The Doc..."), "DOC_ISSUE_API.Sign_The_Doc");
      headbar.addSecureCustomCommand("transferToSignedDoc", mgr.translate("DOCMAWDOCISSUETRANSFERTOSIGNDOC: Transfer To Signed Doc..."), "DOC_ISSUE_API.Sign_The_Doc");
      headbar.removeFromMultirowAction("sign");
      headbar.removeFromMultirowAction("transferToSignedDoc");
      
      headlay = headblk.getASPBlockLayout();
      customizeHeadlay();
      // Added end
      headtbl = mgr.newASPTable(headblk);
      headtbl.enableRowSelect();
      headtbl.setTitle("xxxxx");
      headtbl.enableTitleNoWrap();
      
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      
      headlay.setSimple("RECEIVE_UNIT_NAME");
      headlay.setSimple("SEND_UNIT_NAME");
      headlay.setSimple("PURPOSE_NAME");
      headlay.setSimple("PROJ_NAME");
      headlay.setSimple("MACH_GRP_DESC");
      headlay.setSimple("SIGN_PERSON_NAME");
      headlay.setSimple("USER_CREATED_NAME");
      headlay.setSimple("DOC_CLASS_NAME");
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
      
      
      // Tab commands
      headbar.addCustomCommand("activateObjects", "Objects");
      headbar.addCustomCommand("activateConsistsOf", "Consists Of");
      headbar.addCustomCommand("activateWhereUsed", "Where Used");
      headbar.addCustomCommand("activateAccess", "Access");
      headbar.addCustomCommand("activateHistory", "History");
      headbar.addCustomCommand("activateFileReferences", "File Refs.");
      headbar.addCustomCommand("activateSendDept", "Target Orgs");
      headbar.addCustomCommand("activateAttachmentUpload", "Attachment Upload");
      headbar.addCustomCommand("activateHistoryRevision", "History Revision");
      headbar.addCustomCommand("activateCallback", "Callback");
      headbar.addCustomCommand("activateTempAttach", "Temp Attachment");
      headbar.addCustomCommand("activateDistribution", "Distribution");
      
      // add to index start, Added By lqw 20130827
      headbar.addCustomCommand("addToIndex", "DOCISSUEANCESTORADDTOINDEX: Add to Index...");
      headbar.addSecureCustomCommand("createTrans", "DOCISSUERECEIVECREATERECEIVETRANS: Create Trans...","DOC_RECEIVE_TRANS_API.Create_Trans");
      headbar.addCommandValidConditions("createTrans", "INNER_DOC_CODE", "Enable", "null");
      
      headbar.addCustomCommandSeparator();
      headbar.addSecureCustomCommand("importAttachInfo", mgr.translate("DOCISSUEANCESTORIMPORTAI: Import Attach Info..."), "DOC_TITLE_API.New__");
      headbar.removeFromMultirowAction("importAttachInfo");
      headbar.addCommandValidConditions("importAttachInfo", "DOC_CLASS", "Enable", DocmawConstants.EXCH_SEND);
      headbar.addCommandValidConditions("importAttachInfo", "SIGN_STATUS_DB", "Disable", "SENDED;PARTIALLYSIGNED;SIGNED");
      headbar.addCommandValidConditions("importAttachInfo", "ATTACH_TYPE", "Disable", "NULL");
      
      headbar.addSecureCustomCommand("sendDoc", "DOCISSUECONTRACTORSENDSENDDOC: Send Doc...","DOC_ISSUE_SEND_UTIL_NEW_API.Doc_Issue_Send");
      // add to index end.

      //
      // Object Connections
      //
      itemblk2 = mgr.newASPBlock("ITEM2");
      
      itemblk2.disableDocMan();
      
      itemblk2.addField("ITEM2_OBJID").setHidden().setDbName("OBJID");

      itemblk2.addField("ITEM2_OBJVERSION").setHidden().setDbName("OBJVERSION");

      itemblk2.addField("ITEM2_LU_NAME").setDbName("LU_NAME").setHidden();

      itemblk2.addField("ITEM2_KEY_REF").setDbName("KEY_REF").setHidden();

      itemblk2.addField("KEY_VALUE").setHidden();

      itemblk2.addField("ITEM2_DOC_CLASS").setDbName("DOC_CLASS").setHidden();

      itemblk2.addField("ITEM2_DOC_NO").setDbName("DOC_NO").setHidden();

      itemblk2.addField("ITEM2_DOC_SHEET").setDbName("DOC_SHEET").setHidden();

      itemblk2
            .addField("SLUDESC")
            .setSize(20)
            .setMaxLength(2000)
            .setReadOnly()
            .setFunction(
                  "OBJECT_CONNECTION_SYS.Get_Logical_Unit_Description(:ITEM2_LU_NAME)")
            .setLabel("DOCMAWDOCISSUESLUDESC: Object");

      itemblk2
            .addField("SINSTANCEDESC")
            .setSize(40)
            .setMaxLength(2000)
            .setReadOnly()
            .setFunction(
                  "OBJECT_CONNECTION_SYS.Get_Instance_Description(:ITEM2_LU_NAME, NULL, :ITEM2_KEY_REF)")
            .setLabel("DOCMAWDOCISSUESINSTANCEDESC: Object Key");

      itemblk2.addField("DOC_OBJECT_DESC").setSize(20).setMaxLength(100)
            .setReadOnly().setLabel("DOCMAWDOCISSUEDOCOBJECTDESC: Object Desc");

      itemblk2.addField("CATEGORY").setSize(20).setMaxLength(5)
            .setDynamicLOV("DOC_REFERENCE_CATEGORY").setUpperCase()
            .setLabel("DOCMAWDOCISSUECATEGORY: Association Category");

      itemblk2.addField("KEEP_LAST_DOC_REV").setSize(20).setMaxLength(200)
            .setMandatory().setSelectBox()
            .enumerateValues("ALWAYS_LAST_DOC_REV_API")
            .setLabel("DOCMAWDOCISSUEKEEPLASTDOCREV: Update Revision");

      itemblk2.addField("SKEEPCODE").setHidden()
            .setFunction("Always_Last_Doc_Rev_API.Encode(:KEEP_LAST_DOC_REV)");
      mgr.getASPField("KEEP_LAST_DOC_REV").setValidation("SKEEPCODE");

      itemblk2
            .addField("ITEM2_DOC_REV")
            .setDbName("DOC_REV")
            .setSize(20)
            .setMaxLength(6)
            .setUpperCase()
            .setMandatory()
            .setDynamicLOV("DOC_ISSUE",
                  "ITEM2_DOC_CLASS DOC_CLASS,ITEM2_DOC_NO DOC_NO")
            .setLabel("DOCMAWDOCISSUEITEM2DOCREV: Document Revision");

      itemblk2.addField("COPY_FLAG").setSize(20).setMaxLength(200)
            .setMandatory().setSelectBox()
            .enumerateValues("DOC_REFERENCE_COPY_STATUS_API")
            .setLabel("DOCMAWDOCISSUECOPYFLAG: Copy Status");

      itemblk2
            .addField("NNOOFDOCUMENTS", "Number")
            .setSize(20)
            .setMaxLength(4)
            .setReadOnly()
            .setFunction(
                  "DOC_REFERENCE_OBJECT_API.Get_Number_Of_References(:ITEM2_LU_NAME,:ITEM2_KEY_REF)")
            .setLabel(
                  "DOCMAWDOCISSUENNOOFDOCUMENTS: No of Docs Connected to Object");

      itemblk2.addField("SURVEY_LOCKED_FLAG").setSize(20).setMaxLength(200)
            .setReadOnly()
            .setLabel("DOCMAWDOCISSUESURVEYLOCKEDFLAG: Doc Connection Status");

      itemblk2.setView("DOC_REFERENCE_OBJECT");
      itemblk2.defineCommand("DOC_REFERENCE_OBJECT_API",
            "New__,Modify__,Remove__");
      itemblk2.setMasterBlock(headblk);

      itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.OKFIND, "okFindITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND, "countFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW, "newRowITEM2");
      itembar2.addCustomCommand("objectDetails",
            mgr.translate("DOCMAWDOCISSUEOBJDETAILS: Object Details..."));

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("DOCMAWDOCISSUEDOCOBJECT: Objects"));

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(2);
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);

      //
      // Document Structure - Consists of
      //

      itemblk3 = mgr.newASPBlock("ITEM3");

      itemblk3.disableDocMan();

      itemblk3.addField("ITEM3_OBJID").setDbName("OBJID").setHidden();

      itemblk3.addField("ITEM3_OBJVERSION").setDbName("OBJVERSION").setHidden();

      itemblk3.addField("LEVEL_STR").setReadOnly()
            .setLabel("DOCISSUEFILESLEVELSTR: Level").setBold().setSize(20);

      itemblk3
            .addField("ITEM3_VIEW_FILE")
            .setFunction("''")
            .setReadOnly()
            .unsetQueryable()
            .setLabel("DOCMAWDOCISSUEHEADVIEWFILE: View File")
            .setHyperlink(
                  "../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL",
                  "SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV",
                  "NEWWIN").setAsImageField();

      itemblk3
            .addField("REPLACE_REVISION_TITLE")
            .setHidden()
            .setReadOnly()
            .setBold()
            .setFunction("''")
            .setLabel(
                  "DOCMAWDOCISSUEREPLACEREVISIONTITLE: Enter Replacement Document:");

      itemblk3.addField("SUB_DOC_CLASS").setSize(20).setMaxLength(12)
            .setMandatory().setUpperCase().setReadOnly().setInsertable()
            .setDynamicLOV("DOC_CLASS")
            .setLabel("DOCMAWDOCISSUESUBDOCCLASS: Doc Class");

      itemblk3
            .addField("SUB_DOC_NO")
            .setSize(20)
            .setMaxLength(120)
            .setMandatory()
            .setUpperCase()
            .setReadOnly()
            .setInsertable()
            .setDynamicLOV("DOC_TITLE", "SUB_DOC_CLASS DOC_CLASS")
            .setCustomValidation("SUB_DOC_CLASS,SUB_DOC_NO", "SSUBDOCTITLE")
            .setFieldHyperlink("DocIssue.page", "ITEM3_PAGE_URL","SUB_DOC_CLASS DOC_CLASS,SUB_DOC_NO DOC_NO,SUB_DOC_SHEET DOC_SHEET,SUB_DOC_REV DOC_REV", "NEWWIN")
            .setLabel("DOCMAWDOCISSUESUBDOCNO: Doc No").setHidden();

      itemblk3
            .addField("SUB_DOC_SHEET")
            .setSize(20)
            .setMaxLength(10)
            .setMandatory()
            .setUpperCase()
            .setReadOnly()
            .setInsertable()
            .setDynamicLOV("DOC_ISSUE_LOV1", "SUB_DOC_CLASS DOC_CLASS, SUB_DOC_NO DOC_NO")
            .setLOVProperty("TITLE", mgr.translate("DOCMAWDOCISSUESUBDOCSHEET1: List of Based on Sheets"))
            .setLabel("DOCMAWDOCISSUESUBDOCSHEET: Doc Sheet").setHidden();

      itemblk3.addField("ITEM3_INNER_DOC_CODE").
      setFunction("Doc_Issue_API.Get_Inner_Doc_Code(:SUB_DOC_CLASS,:SUB_DOC_NO,:SUB_DOC_SHEET,:SUB_DOC_REV)").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUESSUBINNERDOCCODE: Inner Doc Code");
      
      itemblk3.addField("ITEM3_DOC_CODE").
      setFunction("Doc_Issue_API.Get_Doc_Code(:SUB_DOC_CLASS,:SUB_DOC_NO,:SUB_DOC_SHEET,:SUB_DOC_REV)").
      setSize(20).
      setReadOnly().
      setFieldHyperlink("DocIssue.page", "ITEM3_PAGE_URL","SUB_DOC_CLASS DOC_CLASS,SUB_DOC_NO DOC_NO,SUB_DOC_SHEET DOC_SHEET,SUB_DOC_REV DOC_REV", "NEWWIN").
      setLabel("DOCMAWDOCISSUESSUBDOCCODE: Doc Code");
      
      itemblk3
      .addField("SSUBDOCTITLE")
      .setSize(40)
      .setMaxLength(80)
      .setReadOnly()
      .setFunction("DOC_ISSUE_API.Get_Rev_Title(:SUB_DOC_CLASS, :SUB_DOC_NO, :SUB_DOC_SHEET, :SUB_DOC_REV)") 
      .setFieldHyperlink("DocIssue.page", "ITEM3_PAGE_URL","SUB_DOC_CLASS DOC_CLASS,SUB_DOC_NO DOC_NO,SUB_DOC_SHEET DOC_SHEET,SUB_DOC_REV DOC_REV", "NEWWIN")
      .setLabel("DOCMAWDOCISSUESSUBDOCTITLE: Title");
      
      itemblk3
            .addField("SUB_DOC_REV")
            .setSize(20)
            .setMaxLength(6)
            .setMandatory()
            .setUpperCase()
            .setReadOnly()
            .setInsertable()
            .setDynamicLOV("DOC_ISSUE", "SUB_DOC_CLASS DOC_CLASS, SUB_DOC_NO DOC_NO, SUB_DOC_SHEET DOC_SHEET")
            .setCustomValidation("SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV", "NNOOFCHILDREN")
            .setLabel("DOCMAWDOCISSUESUBDOCREV: Revision");

      itemblk3.addField("ITEM3_ASSOCIATE_ID").
      setDbName("ASSOCIATE_ID").
      setSize(10).
      setInsertable().
      setDynamicLOV("DOC_ASSOCIATION_TYPE").
      setLabel("DOCMAWDOCISSUESASSOCIATEID: Associate Id");
      
      itemblk3.addField("ITEM3_ASSOCIATE_DESC").
      setSize(10).
      setReadOnly().
      setFunction("DOC_ASSOCIATION_TYPE_API.Get_Description(:ITEM3_ASSOCIATE_ID)").
      setLabel("DOCMAWDOCISSUESASSOCIATEDESC: Associate Desc");
      mgr.getASPField("ITEM3_ASSOCIATE_ID").setValidation("ITEM3_ASSOCIATE_DESC");
      
      itemblk3
            .addField("ITEMBLK3_STATE")
            .setSize(20)
            .setMaxLength(253)
            .setReadOnly()
            .setFunction("DOC_ISSUE_API.Get_State (:SUB_DOC_CLASS, :SUB_DOC_NO, :SUB_DOC_SHEET, :SUB_DOC_REV)")
            .setLabel("DOCMAWDOCISSUECONSISTSOFSTATE: Document Status");

      itemblk3
            .addField("NNOOFCHILDREN", "Number")
            .setSize(20)
            .setMaxLength(6)
            .setReadOnly()
            .setFunction(
                  "DOC_STRUCTURE_API.Number_Of_Children_(:SUB_DOC_CLASS, :SUB_DOC_NO, :SUB_DOC_SHEET, :SUB_DOC_REV)")
            .setLabel("DOCMAWDOCISSUENNOOFCHILDREN: No of Child documents");

      itemblk3.addField("CONSISTS_OF_RELATIVE_PATH").setDbName("RELATIVE_PATH")
            .setSize(40).setMaxLength(256)
            .setLabel("DOCMAWDOCISSUERELATIVEPATH: Relative Path");

      itemblk3.addField("ITEM3_DOC_CLASS").setDbName("DOC_CLASS").setHidden();

      itemblk3.addField("ITEM3_DOC_NO").setDbName("DOC_NO").setHidden();

      itemblk3.addField("ITEM3_DOC_SHEET").setDbName("DOC_SHEET").setHidden();

      itemblk3.addField("ITEM3_DOC_REV").setDbName("DOC_REV").setHidden();

      itemblk3
            .addField("ITEM3_IS_ELE_DOC")
            .setCheckBox("FALSE,TRUE")
            .setFunction(
                  "EDM_FILE_API.Have_Edm_File(:SUB_DOC_CLASS,:SUB_DOC_NO,:SUB_DOC_SHEET,:SUB_DOC_REV)")
            .setReadOnly().setLabel("DOCTITLEISELEDOC: Is Ele Doc").setHidden()
            .setSize(5);

      itemblk3.addField("ITEM3_PAGE_URL")
            .setFunction("Doc_Issue_API.Get_Page_Url(:SUB_DOC_CLASS, :SUB_DOC_NO, :SUB_DOC_SHEET, :SUB_DOC_REV)")
            .setHidden();

      itemblk3
            .setView("(SELECT ds.*, LPAD(' ', 2 * (LEVEL - 1), ' ') || 'й╕йд' LEVEL_STR FROM DOC_STRUCTURE ds"
                  + " CONNECT BY ds.doc_class = PRIOR ds.sub_doc_class "
                  + " AND        ds.doc_no = PRIOR ds.sub_doc_no "
                  + " AND        ds.doc_sheet = PRIOR ds.sub_doc_sheet "
                  + " AND        ds.doc_rev = PRIOR ds.sub_doc_rev "
                  + " START WITH ds.doc_class = ? "
                  + " AND        ds.doc_no = ? "
                  + " AND        ds.doc_sheet = ? "
                  + " AND        ds.doc_rev = ? )");
      itemblk3.defineCommand("DOC_STRUCTURE_API", "New__,Modify__,Remove__");
      itemblk3.setMasterBlock(headblk);

      itemset3 = itemblk3.getASPRowSet();

      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.OKFIND, "okFindITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND, "countFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW, "newRowITEM3");
      itembar3.defineCommand(itembar3.SAVERETURN, "saveReturnITEM3");
      itembar3.addCustomCommand("transferToDocInfoFromConsistOf",
            mgr.translate("DOCMAWDOCISSUEDOCINFO: Document Info..."));
      itembar3
            .addSecureCustomCommand(
                  "replaceRevision",
                  mgr.translate("DOCMAWDOCISSUEREPLACEREVISION: Replace Revision..."),
                  "Doc_Structure_API.Replace_Issue_"); // Bug Id 70286
      itembar3.addCustomCommand("previousLevel",
            mgr.translate("DOCMAWDOCISSUEPREVLEVEL: Previous Level"));
      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("DOCMAWDOCISSUEDOCCONSIT: Consists Of"));

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDialogColumns(2);
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);

      //
      // Doucment Structure - Where used
      //

      itemblk4 = mgr.newASPBlock("ITEM4");

      itemblk4.disableDocMan();

      itemblk4.addField("ITEM4_OBJID").setDbName("OBJID").setHidden();

      itemblk4.addField("ITEM4_OBJVERSION").setDbName("OBJVERSION").setHidden();

      itemblk4.addField("ITEM4_LEVEL_STR").setReadOnly().setDbName("LEVEL_STR")
            .setLabel("DOCMAWDOCISSUEITEM4LEVELSTR: Level").setBold()
            .setSize(20);

      itemblk4
            .addField("ITEM4_VIEW_FILE")
            .setFunction("''")
            .setReadOnly()
            .unsetQueryable()
            .setLabel("DOCMAWDOCISSUEITEM4VIEWFILE: View File")
            .setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "ITEM4_DOC_CLASS,ITEM4_DOC_NO,ITEM4_DOC_SHEET,ITEM4_DOC_REV", "NEWWIN").setAsImageField();

      itemblk4.addField("ITEM4_DOC_CLASS").setDbName("DOC_CLASS").setSize(20)
            .setMaxLength(12).setMandatory().setUpperCase().setReadOnly()
            .setInsertable().setDynamicLOV("DOC_CLASS")
            .setLabel("DOCMAWDOCISSUEITEM4DOCCLASS: Doc Class");

      itemblk4
            .addField("ITEM4_DOC_NO")
            .setDbName("DOC_NO")
            .setSize(20)
            .setMaxLength(120)
            .setMandatory()
            .setUpperCase()
            .setReadOnly()
            .setInsertable()
            .setDynamicLOV("DOC_TITLE", "ITEM4_DOC_CLASS DOC_CLASS")
            .setCustomValidation("DOC_CLASS,DOC_NO", "ITEM4_SSUBDOCTITLE")
            .setFieldHyperlink("DocIssue.page", "ITEM4_PAGE_URL","ITEM4_DOC_CLASS DOC_CLASS,ITEM4_DOC_NO DOC_NO,ITEM4_DOC_SHEET DOC_SHEET,ITEM4_DOC_REV DOC_REV", "NEWWIN")
            .setLabel("DOCMAWDOCISSUEITEM4DOCNO: Doc No").setHidden();

      itemblk4
            .addField("ITEM4_DOC_SHEET")
            .setDbName("DOC_SHEET")
            .setSize(20)
            .setMaxLength(10)
            .setMandatory()
            .setUpperCase()
            .setReadOnly()
            .setInsertable()
            .setDynamicLOV("DOC_ISSUE_LOV1",
                  "ITEM4_DOC_CLASS DOC_CLASS, ITEM4_DOC_NO DOC_NO")
            .setLOVProperty(
                  "TITLE",
                  mgr.translate("DOCMAWDOCISSUESUBDOCSHEET1: List of Based on Sheets"))
            .setLabel("DOCMAWDOCISSUEITEM4DOCSHEET: Doc Sheet").setHidden();

      itemblk4.addField("ITEM4_INNER_DOC_CODE").
      setFunction("Doc_Issue_API.Get_Inner_Doc_Code(:ITEM4_DOC_CLASS,:ITEM4_DOC_NO,:ITEM4_DOC_SHEET,:ITEM4_DOC_REV)").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEITEM4INNERDOCCODE: Inner Doc Code");
      
      itemblk4.addField("ITEM4_DOC_CODE").
      setFunction("Doc_Issue_API.Get_Doc_Code(:ITEM4_DOC_CLASS,:ITEM4_DOC_NO,:ITEM4_DOC_SHEET,:ITEM4_DOC_REV)").
      setSize(20).
      setReadOnly().
      setFieldHyperlink("DocIssue.page", "ITEM4_PAGE_URL","ITEM4_DOC_CLASS DOC_CLASS,ITEM4_DOC_NO DOC_NO,ITEM4_DOC_SHEET DOC_SHEET,ITEM4_DOC_REV DOC_REV", "NEWWIN").
      setLabel("DOCMAWDOCISSUEITEM4DOCCODE: Doc Code");
      
      itemblk4
      .addField("ITEM4_SSUBDOCTITLE")
      .setSize(40)
      .setMaxLength(80)
      .setReadOnly()
      .setFunction("DOC_ISSUE_API.Get_REV_Title(:ITEM4_DOC_CLASS,:ITEM4_DOC_NO, :ITEM4_DOC_SHEET, :ITEM4_DOC_REV)")
      .setFieldHyperlink("DocIssue.page", "ITEM4_PAGE_URL","ITEM4_DOC_CLASS DOC_CLASS,ITEM4_DOC_NO DOC_NO,ITEM4_DOC_SHEET DOC_SHEET,ITEM4_DOC_REV DOC_REV", "NEWWIN")
      .setLabel("DOCMAWDOCISSUEITEM4SSUBDOCTITLE: Title");
      
      itemblk4
            .addField("ITEM4_DOC_REV")
            .setDbName("DOC_REV")
            .setSize(20)
            .setMaxLength(6)
            .setMandatory()
            .setUpperCase()
            .setReadOnly()
            .setInsertable()
            .setDynamicLOV("DOC_ISSUE", "ITEM4_DOC_CLASS DOC_CLASS, ITEM4_DOC_NO DOC_NO, ITEM4_DOC_SHEET DOC_SHEET")
            .setCustomValidation("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV", "ITEM4_NNOOFCHILDREN")
            .setLabel("DOCMAWDOCISSUEITEM4DOCREV: Revision");

      mgr.getASPField("ITEM4_DOC_NO").setValidation("ITEM4_SSUBDOCTITLE");

      itemblk4.addField("ITEM4_ASSOCIATE_ID").
      setDbName("ASSOCIATE_ID").
      setSize(10).
      setInsertable().
      setDynamicLOV("DOC_ASSOCIATION_TYPE").
      setLabel("DOCMAWDOCISSUESASSOCIATEID: Associate Id");
      
      itemblk4.addField("ITEM4_ASSOCIATE_DESC").
      setSize(10).
      setReadOnly().
      setFunction("DOC_ASSOCIATION_TYPE_API.Get_Description(:ITEM4_ASSOCIATE_ID)").
      setLabel("DOCMAWDOCISSUESASSOCIATEDESC: Associate Desc");
      mgr.getASPField("ITEM4_ASSOCIATE_ID").setValidation("ITEM4_ASSOCIATE_DESC");
      
      itemblk4
            .addField("ITEMBLK4_STATE")
            .setSize(20)
            .setMaxLength(253)
            .setReadOnly()
            .setFunction("DOC_ISSUE_API.Get_State (:ITEM4_DOC_CLASS, :ITEM4_DOC_NO, :ITEM4_DOC_SHEET, :ITEM4_DOC_REV)")
            .setLabel("DOCMAWDOCISSUECONSISTSOFSTATE: Document Status");

      itemblk4
            .addField("ITEM4_NNOOFCHILDREN", "Number")
            .setSize(20)
            .setMaxLength(6)
            .setReadOnly()
            .setFunction("DOC_STRUCTURE_API.Number_Of_Children_(:ITEM4_DOC_CLASS, :ITEM4_DOC_NO, :ITEM4_DOC_SHEET, :ITEM4_DOC_REV)")
            .setLabel("DOCMAWDOCISSUEITEM4NNOOFCHILDREN: No of Child documents");
      mgr.getASPField("ITEM4_DOC_REV").setValidation("ITEM4_NNOOFCHILDREN");

      itemblk4.addField("WHERE_USED_RELATIVE_PATH").setDbName("RELATIVE_PATH")
            .setSize(40).setMaxLength(256)
            .setLabel("DOCMAWDOCISSUERELATIVEPATH: Relative Path");

      itemblk4.addField("ITEM4_SUB_DOC_CLASS").setDbName("SUB_DOC_CLASS")
            .setHidden();

      itemblk4.addField("ITEM4_SUB_DOC_NO").setDbName("SUB_DOC_NO").setHidden();

      itemblk4.addField("ITEM4_SUB_DOC_SHEET").setDbName("SUB_DOC_SHEET")
            .setHidden();

      itemblk4.addField("ITEM4_SUB_DOC_REV").setDbName("SUB_DOC_REV")
            .setHidden();

      itemblk4
            .addField("ITEM4_IS_ELE_DOC")
            .setCheckBox("FALSE,TRUE")
            .setFunction("EDM_FILE_API.Have_Edm_File(:ITEM4_DOC_CLASS,:ITEM4_DOC_NO,:ITEM4_DOC_SHEET,:ITEM4_DOC_REV)")
            .setReadOnly()
            .setLabel("DOCMAWDOCISSUEITEM4ISELEDOC: Is Ele Doc")
            .setHidden()
            .setSize(5);

      itemblk4.addField("ITEM4_PAGE_URL")
            .setFunction("Doc_Issue_API.Get_Page_Url(:ITEM4_DOC_CLASS,:ITEM4_DOC_NO,:ITEM4_DOC_SHEET,:ITEM4_DOC_REV)")
            .setHidden();

      itemblk4
            .setView("(SELECT ds.*, LPAD(' ', 2 * (LEVEL - 1), ' ') || 'й╕йд' LEVEL_STR FROM DOC_STRUCTURE ds"
                  + " CONNECT BY PRIOR ds.doc_class = ds.sub_doc_class "
                  + " AND        PRIOR ds.doc_no = ds.sub_doc_no "
                  + " AND        PRIOR ds.doc_sheet = ds.sub_doc_sheet "
                  + " AND        PRIOR ds.doc_rev = ds.sub_doc_rev "
                  + " START WITH ds.sub_doc_class = ? "
                  + " AND        ds.sub_doc_no = ? "
                  + " AND        ds.sub_doc_sheet = ? "
                  + " AND        ds.sub_doc_rev = ? )");
      itemblk4.defineCommand("DOC_STRUCTURE_API", "New__,Modify__,Remove__");
      itemblk4.setMasterBlock(headblk);

      itemset4 = itemblk4.getASPRowSet();

      itembar4 = mgr.newASPCommandBar(itemblk4);
      itembar4.defineCommand(itembar4.OKFIND, "okFindITEM4");
      itembar4.defineCommand(itembar4.COUNTFIND, "countFindITEM4");
      itembar4.defineCommand(itembar4.NEWROW, "newRowITEM4");
      itembar4.defineCommand(itembar4.DUPLICATEROW, "duplicateRowITEM4");
      itembar4.addCustomCommand("transferToDocInfoFromWhereUsed", mgr.translate("DOCMAWDOCISSUEDOCINFO: Document Info..."));

      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setTitle(mgr.translate("DOCMAWDOCISSUEDOCWHEUSED: Where Used"));

      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDialogColumns(2);
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);

      //
      // Document Access
      //

      itemblk6 = mgr.newASPBlock("ITEM6");

      itemblk6.disableDocMan();

      itemblk6.addField("ITEM6_OBJID").setDbName("OBJID").setHidden();

      itemblk6.addField("ITEM6_OBJVERSION").setDbName("OBJVERSION").setHidden();

      itemblk6.addField("ITEM6_DOC_CLASS").setDbName("DOC_CLASS").setHidden();

      itemblk6.addField("ITEM6_DOC_NO").setDbName("DOC_NO").setHidden();

      itemblk6.addField("ITEM6_DOC_SHEET").setDbName("DOC_SHEET").setHidden();

      itemblk6.addField("ITEM6_DOC_REV").setDbName("DOC_REV").setHidden();

      itemblk6.addField("ITEM6_LINE_NO", "Number").setDbName("LINE_NO")
            .setHidden();

      itemblk6.addField("ACCESS_OWNER", "Number").setSize(20)
            .setCheckBox("0,1")
            .setLabel("DOCMAWDOCISSUEACCESSOWNER: Administrator");

      itemblk6.addField("ITEM6_GROUP_ID").setDbName("GROUP_ID").setSize(20)
            .setUpperCase().setReadOnly().setInsertable()
            .setDynamicLOV("DOCUMENT_GROUP")
            .setLabel("DOCMAWDOCISSUEITEM6GROUPID: Group ID");

      itemblk6
            .addField("GROUPDESCRIPTION")
            .setSize(20)
            .setFunction(
                  "DOCUMENT_GROUP_API.Get_Group_Description(:ITEM6_GROUP_ID)")
            .setReadOnly()
            .setLabel("DOCMAWDOCISSUEGROUPDESCRIPTION: Group Desc");
      mgr.getASPField("ITEM6_GROUP_ID").setValidation("GROUPDESCRIPTION");

      itemblk6
            .addField("ITEM6_PERSON_ID")
            .setDbName("PERSON_ID")
            .setSize(20)
            .setUpperCase()
            .setReadOnly()
            .setInsertable()
            .setDynamicLOV("PERSON_INFO_LOV")
            .setCustomValidation("ITEM6_PERSON_ID,ITEM6_GROUP_ID",
                  "PERSONNAME,PERSONUSERID")
            .setLabel("DOCMAWDOCISSUEITEM6PERSONID: Person ID");

      itemblk6.addField("PERSONNAME").setSize(20).setReadOnly()
            .setFunction("PERSON_INFO_API.Get_Name(:ITEM6_PERSON_ID)")
            .setLabel("DOCMAWDOCISSUEPERSONNAME: Name");

      itemblk6.addField("PERSONUSERID").setSize(20).setReadOnly()
            .setFunction("PERSON_INFO_API.Get_User_Id(:ITEM6_PERSON_ID)")
            .setLabel("DOCMAWDOCISSUEPERSONUSERID: User ID");

      itemblk6.addField("EDIT_ACCESS", "Number").setSize(20).setCheckBox("0,1")
            .setLabel("DOCMAWDOCISSUEEDITACCESS: Edit Access");

      itemblk6.addField("VIEW_ACCESS", "Number").setSize(20).setCheckBox("0,1")
            .setLabel("DOCMAWDOCISSUEVIEWACCESS: View Access");

      // Added by Terry 20100125
      itemblk6.addField("DOWNLOAD_ACCESS", "Number").setCheckBox("0,1")
            .setSize(20)
            .setLabel("DOCMAWDOCISSUEDOWNLOADACCESS: Download Access");

      itemblk6.addField("PRINT_ACCESS", "Number").setCheckBox("0,1")
            .setSize(20).setLabel("DOCMAWDOCISSUEPRINTACCESS: Print Access");
      // Added end

      itemblk6.addField("ITEM6_NOTE").setDbName("NOTE").setSize(20)
            .setLabel("DOCMAWDOCISSUEITEM6NOTE: Note");

      /*
       * itemblk6.addField("ITEM6_KEY_REF"). setFunction(
       * "Doc_Issue_Api.Get_Key_Ref_(:ITEM6_DOC_CLASS,:ITEM6_DOC_NO,:ITEM6_DOC_SHEET,:ITEM6_DOC_REV)"
       * ). setHidden();
       */

      itemblk6
            .addField("ITEM6_EXIST_IN_APPROVAL")
            .setReadOnly()
            .setFunction(
                  "Approval_Routing_Api.Exist_In_Approval_Routing_('DocIssue',Doc_Issue_Api.Get_Key_Ref_(:ITEM6_DOC_CLASS,:ITEM6_DOC_NO,:ITEM6_DOC_SHEET,:ITEM6_DOC_REV),:ITEM6_PERSON_ID,:ITEM6_GROUP_ID)")
            .setHidden();

      itemblk6.setView("DOCUMENT_ISSUE_ACCESS");
      itemblk6.defineCommand("DOCUMENT_ISSUE_ACCESS_API",
            "New__,Modify__,Remove__");
      itemblk6.setMasterBlock(headblk);

      itemset6 = itemblk6.getASPRowSet();
      itembar6 = mgr.newASPCommandBar(itemblk6);
      itembar6.defineCommand(itembar6.OKFIND, "okFindITEM6");
      itembar6.defineCommand(itembar6.COUNTFIND, "countFindITEM6");
      itembar6.defineCommand(itembar6.NEWROW, "newRowITEM6");
      itembar6.defineCommand(itembar6.DELETE, "deleteRowITEM6");
      itembar6.addCustomCommand("transferToDocGroup",
            mgr.translate("DOCMAWDOCISSUETRDOCGROUP: Document Group..."));
      itembar6.addCustomCommand("transferToPersonInfo",
            mgr.translate("DOCMAWDOCISSUEPERSONINFO: Person Info..."));

      itemtbl6 = mgr.newASPTable(itemblk6);
      itemtbl6.setTitle(mgr.translate("DOCMAWDOCISSUEDOCACESS: Access"));

      itemlay6 = itemblk6.getASPBlockLayout();
      itemlay6.setDialogColumns(2);
      itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);

      //
      // Document History
      //

      itemblk7 = mgr.newASPBlock("ITEM7");

      itemblk7.disableDocMan();

      itemblk7.addField("ITEM7_OBJID").setDbName("OBJID").setHidden();

      itemblk7.addField("ITEM7_OBJVERSION").setDbName("OBJVERSION").setHidden();

      itemblk7.addField("ITEM7_DOC_CLASS").setDbName("DOC_CLASS").setHidden();

      itemblk7.addField("ITEM7_DOC_NO").setDbName("DOC_NO").setHidden();

      itemblk7.addField("ITEM7_DOC_SHEET").setDbName("DOC_SHEET").setHidden();

      itemblk7.addField("ITEM7_DOC_REV").setDbName("DOC_REV").setHidden();

      itemblk7.addField("ITEM7_LINE_NO", "Number").setDbName("LINE_NO")
            .setSize(20).setReadOnly()
            .setLabel("DOCMAWDOCISSUEITEM7LINENO: Line No");

      itemblk7.addField("INFO_CATEGORY").setSize(20).setMandatory()
            .setReadOnly()
            .setLabel("DOCMAWDOCISSUEINFOCATEGORY: Info Category");

      itemblk7.addField("ITEM7_STATUS").setDbName("STATUS").setSize(20)
            .setReadOnly().setLabel("DOCMAWDOCISSUEITEM7STATUS: Status");

      itemblk7.addField("ITEM7_NOTE").setDbName("NOTE").setSize(20)
            .setMaxLength(2000).setLabel("DOCMAWDOCISSUEITEM7NOTE: Note");

      itemblk7.addField("ITEM7_CREATED_BY").setDbName("CREATED_BY").setSize(20)
            .setReadOnly().setLabel("DOCMAWDOCISSUEITEM7CREATEDBY: Created By");

      itemblk7
            .addField("ITEM7_SPERSONNAME")
            .setSize(20)
            .setReadOnly()
            .setFunction("PERSON_INFO_API.Get_Name_For_User(:ITEM7_CREATED_BY)")
            .setLabel("DOCMAWDOCISSUEITEM7SPERSONNAME: Name");

      itemblk7.addField("ITEM7_DATE_CREATED", "Date").setDbName("DATE_CREATED")
            .setSize(20).setReadOnly()
            .setLabel("DOCMAWDOCISSUEITEM7DATECREATED: Created");

      itemblk7.addField("ITEM7_ENABLE_HISTORY_NEW").setHidden()
            .setFunction("''");

      itemblk7.addField("ITEM7_DUMMY1").setHidden().setFunction("''");

      itemblk7.setView("DOCUMENT_ISSUE_HISTORY");
      itemblk7.defineCommand("DOCUMENT_ISSUE_HISTORY_API", "New__,Remove__");
      itemblk7.setMasterBlock(headblk);

      itemset7 = itemblk7.getASPRowSet();

      itembar7 = mgr.newASPCommandBar(itemblk7);
      itembar7.disableCommand(itembar7.EDITROW);
      itembar7.disableCommand(itembar7.DELETE);

      itembar7.defineCommand(itembar7.OKFIND, "okFindITEM7");
      itembar7.defineCommand(itembar7.COUNTFIND, "countFindITEM7");
      itembar7.defineCommand(itembar7.NEWROW, "newRowITEM7");
      itembar7.enableCommand(itembar7.FIND);

      itemtbl7 = mgr.newASPTable(itemblk7);
      itemtbl7.setTitle(mgr.translate("DOCMAWDOCISSUEDOCHIST: History"));

      itemlay7 = itemblk7.getASPBlockLayout();
      itemlay7.setDialogColumns(2);
      itemlay7.setDefaultLayoutMode(itemlay7.MULTIROW_LAYOUT);

      //
      // File References
      //

      itemblk9 = mgr.newASPBlock("ITEM9");

      itemblk9.disableDocMan();

      itemblk9.addField("ITEM9_OBJID").setDbName("OBJID").setHidden();

      itemblk9.addField("ITEM9_OBJVERSION").setDbName("OBJVERSION").setHidden();

      itemblk9.addField("ITEM9_DOC_CLASS").setDbName("DOC_CLASS").setHidden();

      itemblk9.addField("ITEM9_DOC_NO").setDbName("DOC_NO").setHidden();

      itemblk9.addField("ITEM9_DOC_SHEET").setDbName("DOC_SHEET").setHidden();

      itemblk9.addField("ITEM9_DOC_REV").setDbName("DOC_REV").setHidden();

      itemblk9.addField("ITEM9_DOC_TYPE").setDbName("DOC_TYPE").setSize(12)
            .setLabel("DOCMAWDOCISSUEITEM9DOCTYPE: Document Type");

      itemblk9.addField("ITEM9_FILE_NO", "Number").setDbName("FILE_NO")
            .setHidden();

      itemblk9.addField("ITEM9_FILE_NAME").setDbName("FILE_NAME").setSize(254)
            .setDefaultNotVisible()
            .setLabel("DOCMAWDOCISSUEITEM9REPFILENAME: Repository File Name");

      itemblk9.addField("ITEM9_USER_FILE_NAME").setDbName("USER_FILE_NAME")
            .setSize(254)
            .setLabel("DOCMAWDOCISSUEITEM9FILENAME: Original File Name");

      itemblk9.addField("ITEM9_LOCAL_PATH").setDbName("LOCAL_PATH")
            .setSize(254).setHidden()
            .setLabel("DOCMAWDOCISSUEITEM9LOCALPATH: Local Path");

      itemblk9
            .addField("ITEM9_LOCAL_FILENAME")
            .setDbName("LOCAL_FILENAME")
            .setSize(254)
            .setHidden()
            .setLabel("DOCMAWDOCISSUECHECKEDOUTFILENAME: Checked Out File Name");

      itemblk9.addField("ITEM9_FILETYPE").setDbName("FILE_TYPE").setSize(30)
            .setLabel("DOCMAWDOCISSUEITEM9FILETYPE: File Type");

      itemblk9.addField("ITEM9_STATE").setDbName("STATE").setSize(253)
            .setLabel("DOCMAWDOCISSUEITEM9STATE: State");

      itemblk9.addField("ITEM9_CHECKED_OUT_SIGN").setDbName("CHECKED_OUT_SIGN")
            .setSize(10).setReadOnly()
            .setLabel("DOCMAWDOCISSUEITEM9CHECKEDOUTSIGN: Checked Out Sign");

      itemblk9.addField("ITEM9_CHECKED_OUT_DATE", "Date")
            .setDbName("CHECKED_OUT_DATE").setSize(10).setReadOnly()
            .setLabel("DOCMAWDOCISSUEITEM9CHECKEDOUTDATE: Checked Out Date");

      itemblk9.setView("EDM_FILE");
      itemblk9.defineCommand("EDM_FILE_API", "New__,Modify__,Remove__");
      itemblk9.setMasterBlock(headblk);

      itemset9 = itemblk9.getASPRowSet();

      itembar9 = mgr.newASPCommandBar(itemblk9);
      itembar9.defineCommand(itembar9.OKFIND, "okFindITEM9");
      itembar9.disableCommand(itembar9.COUNTFIND);
      itembar9.disableCommand(itembar9.EDITROW);
      itembar9.disableCommand(itembar9.NEWROW);
      itembar9.disableCommand(itembar9.DELETE);
      itembar9.disableCommand(itembar9.DUPLICATEROW);

      itemtbl9 = mgr.newASPTable(itemblk9);
      itemtbl9.setTitle("DOCMAWDOCISSUEFILEREF: File Refs.");

      itemlay9 = itemblk9.getASPBlockLayout();
      itemlay9.setDefaultLayoutMode(itemlay9.MULTIROW_LAYOUT);

      itemblk11 = mgr.newASPBlock("ITEM11");
      itemblk11.addField("ITEM11_OBJID").setHidden().setDbName("OBJID");
      itemblk11.addField("ITEM11_OBJVERSION").setHidden()
            .setDbName("OBJVERSION");
      itemblk11.addField("ITEM11_DOC_CLASS").setDbName("DOC_CLASS")
            .setMandatory().setHidden().setInsertable()
            .setLabel("DOCISSUETARGETORGITEM0DOCCLASS: Doc Class").setSize(12);
      itemblk11.addField("ITEM11_DOC_NO").setDbName("DOC_NO").setMandatory()
            .setHidden().setInsertable()
            .setLabel("DOCISSUETARGETORGITEM0DOCNO: Doc No").setSize(120);
      itemblk11.addField("ITEM11_DOC_SHEET").setDbName("DOC_SHEET")
            .setMandatory().setHidden().setInsertable()
            .setLabel("DOCISSUETARGETORGITEM0DOCSHEET: Doc Sheet").setSize(10);
      itemblk11.addField("ITEM11_DOC_REV").setDbName("DOC_REV").setMandatory()
            .setHidden().setInsertable()
            .setLabel("DOCISSUETARGETORGITEM0DOCREV: Doc Rev").setSize(6);
      
      itemblk11.addField("ORG_NO").
      setMandatory().
      setInsertable().
      setLabel("DOCISSUETARGETORGORGNO: Org No").
      setDynamicLOV("GENERAL_ZONE_LOV").
      // setLOVProperty("WHERE", "COMM_CODE IS NOT NULL").
      setCustomValidation("ORG_NO", "ORG_NAME").
      setSize(20);
      
      itemblk11.addField("ORG_NAME").setReadOnly()
            .setLabel("DOCISSUETARGETORGORGNAME: Org Name").setSize(20);
      itemblk11.addField("ITEM11_SIGN_STATUS").setDbName("SIGN_STATUS")
            .setLabel("DOCISSUETARGETORGITEM0SIGNSTATUS: Sign Status")
            .setReadOnly().setCheckBox("FALSE,TRUE").setSize(5);
      itemblk11.addField("IS_MAIN").setInsertable().setCheckBox("FALSE,TRUE")
            .setLabel("DOCISSUETARGETORGISMAIN: Is Main").setSize(5);
      itemblk11.setView("DOC_ISSUE_TARGET_ORG");
      itemblk11.defineCommand("DOC_ISSUE_TARGET_ORG_API",
            "New__,Modify__,Remove__");
      itemblk11.setMasterBlock(headblk);
      itemset11 = itemblk11.getASPRowSet();
      itembar11 = mgr.newASPCommandBar(itemblk11);
      itembar11.defineCommand(itembar11.OKFIND, "okFindITEM11");
      itembar11.defineCommand(itembar11.NEWROW, "newRowITEM11");
      itemtbl11 = mgr.newASPTable(itemblk11);
      itemtbl11.setTitle("DOCISSUETARGETORGITEMHEAD1: DocIssueTargetOrg");
      itemtbl11.enableRowSelect();
      itemtbl11.setWrap();
      itemlay11 = itemblk11.getASPBlockLayout();
      itemlay11.setSimple("ORG_NAME");
      itemlay11.setDefaultLayoutMode(itemlay11.MULTIROW_LAYOUT);
      
      
      
//      itemblk12 = mgr.newASPBlock("ITEM12");
//      itemblk12.addField("ITEM12_DOC_CLASS").setDbName("DOC_CLASS").setSize(20).setHidden();
//      itemblk12.addField("ITEM12_DOC_NO").setDbName("DOC_NO").setSize(20).setHidden();
//      itemblk12.addField("ITEM12_DOC_SHEET").setDbName("DOC_SHEET").setSize(20).setHidden();
//      itemblk12.addField("ITEM12_DOC_REV").setDbName("DOC_REV").setSize(20).setHidden();
//      itemblk12.addField("ITEM12_SUB_DOC_CLASS").setDbName("SUB_DOC_CLASS").setLabel("DOCISSUEATTACHLISTDOCCLASS: Doc Class").setSize(20);
//      itemblk12.addField("ITEM12_SUB_DOC_CLASS_NAME").setFunction("DOC_CLASS_API.Get_Name(:ITEM12_SUB_DOC_CLASS)").setLabel("DOCISSUEATTACHLISTDOCCLASSNAME: Doc Class Name").setSize(20);
//      itemblk12.addField("ITEM12_SUB_DOC_NO").setDbName("SUB_DOC_NO").setSize(20).setHidden();
//      itemblk12.addField("ITEM12_SUB_DOC_SHEET").setDbName("SUB_DOC_SHEET").setSize(20).setHidden();
//      itemblk12.addField("ITEM12_SUB_DOC_REV").setDbName("SUB_DOC_REV").setSize(20).setHidden();
//      itemblk12.addField("ITEM12_DOC_CODE").setDbName("DOC_CODE").setLabel("DOCISSUEATTACHLISTDOCCODE: Doc Code").setSize(20);
//      itemblk12.addField("ITEM12_REV_TITLE").setDbName("REV_TITLE").setLabel("DOCISSUEATTACHLISTREVTITLE: Rev Title").setSize(20);
//      itemblk12.addField("ITEM12_SUB_CLASS").setDbName("SUB_CLASS").setLabel("DOCISSUEATTACHLISTDOCCLASS: Sub Class").setSize(20);
//      itemblk12.addField("ITEM12_SUB_CLASS_NAME").setFunction("Doc_Sub_Class_Api.Get_Sub_Class_Name(:ITEM12_SUB_DOC_CLASS,:ITEM12_SUB_CLASS)").setLabel("DOCISSUEATTACHLISTDOCCLASS: Sub Class Name").setSize(20);
//      itemblk12.setView("(SELECT DS.*,DI.DOC_CODE DOC_CODE,DI.REV_TITLE REV_TITLE,DI.SUB_CLASS SUB_CLASS FROM DOC_STRUCTURE DS , DOC_ISSUE DI" + 
//                        " WHERE DS.SUB_DOC_CLASS = DI.DOC_CLASS" + 
//                        " AND DS.SUB_DOC_NO = DI.DOC_NO" + 
//                        " AND DS.SUB_DOC_SHEET = DI.DOC_SHEET" + 
//                        " AND DS.SUB_DOC_REV = DI.DOC_REV)");
//      itemblk12.setMasterBlock(headblk);
//      itembar12 = mgr.newASPCommandBar(itemblk12);
//      itemtbl12 = mgr.newASPTable(itemblk12);
//      itemlay12 = itemblk12.getASPBlockLayout();
//      itemlay12.setSimple("ITEM12_SUB_DOC_CLASS_NAME");
//      itemlay12.setSimple("ITEM12_SUB_CLASS_NAME");
//      itemlay12.setDefaultLayoutMode(itemlay12.MULTIROW_LAYOUT);
//      REV_TITLE,TRANSLATE_TITLE,DOC_CODE,INNER_DOC_CODE,STATE,
       itemblk13 = mgr.newASPBlock("ITEM13");
       itemblk13.addField("ITEM13_DOC_CLASS").setDbName("DOC_CLASS").setSize(20).setHidden();
       itemblk13.addField("ITEM13_DOC_NO").setDbName("DOC_NO").setSize(20).setHidden();
       itemblk13.addField("ITEM13_DOC_SHEET").setDbName("DOC_SHEET").setSize(20).setHidden();
       itemblk13.addField("ITEM13_DOC_CODE").setDbName("DOC_CODE").setLabel("DOCISSUEHISTORYREVISIONDOCCODE: Doc Code").setSize(20).setHyperlink("DocmawIntermediatePage.page", "ITEM13_DOC_CLASS,ITEM13_DOC_NO,ITEM13_DOC_SHEET,ITEM13_DOC_REV,ITEM13_SUB_CLASS");
       itemblk13.addField("ITEM13_REV_TITLE").setDbName("REV_TITLE").setLabel("DOCISSUEHISTORYREVISIONREVTITLE: Rev Title").setSize(20).setHyperlink("DocmawIntermediatePage.page", "ITEM13_DOC_CLASS,ITEM13_DOC_NO,ITEM13_DOC_SHEET,ITEM13_DOC_REV,ITEM13_SUB_CLASS");//setHyperlink("ITEM13_PAGE_URL", "ITEM13_PAGE_URL", "ITEM13_DOC_CLASS,ITEM13_DOC_NO,ITEM13_DOC_SHEET,ITEM13_DOC_REV", "POST", false);
       itemblk13.addField("ITEM13_TRANSLATE_TITLE").setDbName("TRANSLATE_TITLE").setLabel("DOCISSUEHISTORYREVISIONTRANSLATETITLE: Translated Title").setSize(20);
       itemblk13.addField("ITEM13_INNER_DOC_CODE").setDbName("INNER_DOC_CODE").setLabel("DOCISSUEHISTORYREVISIONINNERDOCCODE: Inner Doc Code").setSize(20);
       itemblk13.addField("ITEM13_STATE").setDbName("STATE").setLabel("DOCISSUEHISTORYREVISIONSTATE: STATE").setSize(20);
       itemblk13.addField("ITEM13_DOC_REV").setDbName("DOC_REV").setSize(20).setLabel("DOCISSUEHISTORYREVISIONDOCREV: Doc Rev").setSize(20);
       itemblk13.addField("ITEM13_SUB_CLASS").setDbName("SUB_CLASS").setLabel("DOCISSUEHISTORYREVISIONDOCCLASS: Sub Class").setSize(20);
       itemblk13.addField("ITEM13_SUB_CLASS_NAME").setFunction("Doc_Sub_Class_Api.Get_Sub_Class_Name(:ITEM13_DOC_CLASS,:ITEM13_SUB_CLASS)").setLabel("DOCISSUEHISTORYREVISIONSUBDOCCLASS: Sub Class Name").setSize(20);
//       itemblk13.addField("ITEM13_PAGE_URL").setFunction("DOC_CLASS_API.Get_Page_Url(:ITEM13_DOC_CLASS)").setLabel("DOCISSUEHISTORYREVISIONPAGEURL: Page Url").setSize(20).setHidden();
       itemblk13.setView( "DOC_ISSUE_REFERENCE");
       itemblk13.setMasterBlock(headblk);
       itembar13 = mgr.newASPCommandBar(itemblk13);
       itemtbl13 = mgr.newASPTable(itemblk13);
       itemlay13 = itemblk13.getASPBlockLayout();
       itemlay13.setSimple("ITEM13_SUB_CLASS_NAME");
       itemlay13.setDefaultLayoutMode(itemlay13.MULTIROW_LAYOUT);
      
       
       //
       // Document Callback
       //
       
       doc_issue_callback_blk = mgr.newASPBlock("CALLBACK");
       doc_issue_callback_blk.addField("CALLBACK_OBJID").
       setHidden().
       setDbName("OBJID");

       doc_issue_callback_blk.addField("CALLBACK_OBJVERSION").
       setHidden().
       setDbName("OBJVERSION");

       doc_issue_callback_blk.addField("CALLBACK_DOC_CLASS").
       setDbName("DOC_CLASS").
       setMandatory().
       setInsertable().
       setHidden().
       setLabel("DOCISSUECALLBACKCALLBACKDOCCLASS: Doc Class").
       setSize(12);

       doc_issue_callback_blk.addField("CALLBACK_DOC_NO").
       setDbName("DOC_NO").
       setMandatory().
       setInsertable().
       setHidden().
       setLabel("DOCISSUECALLBACKCALLBACKDOCNO: Doc No").
       setSize(120);
       
       doc_issue_callback_blk.addField("CALLBACK_DOC_SHEET").
       setDbName("DOC_SHEET").
       setMandatory().
       setInsertable().
       setHidden().
       setLabel("DOCISSUECALLBACKCALLBACKDOCSHEET: Doc Sheet").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_DOC_REV").
       setDbName("DOC_REV").
       setMandatory().
       setInsertable().
       setHidden().
       setLabel("DOCISSUECALLBACKCALLBACKDOCREV: Doc Rev").
       setSize(5);
       
       doc_issue_callback_blk.addField("SEQ_NO", "Number").
       unsetInsertable().
       setReadOnly().
       setHidden().
       setLabel("DOCISSUECALLBACKSEQNO: Seq No").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_DOC_CODE").
       setDbName("DOC_CODE").
       unsetInsertable().
       setReadOnly().
       setLabel("DOCISSUECALLBACKDOCCODE: Doc Code").
       setSize(20);
       
       doc_issue_callback_blk.addField("CALLBACK_INNER_DOC_CODE").
       setDbName("INNER_DOC_CODE").
       unsetInsertable().
       setReadOnly().
       setLabel("DOCISSUECALLBACKINNERDOCCODE: Inner Doc Code").
       setSize(20);
       
       doc_issue_callback_blk.addField("CALLBACK_REV_TITLE").
       setDbName("REV_TITLE").
       unsetInsertable().
       setReadOnly().
       setLabel("DOCISSUECALLBACKREVTITLE: Rev Title").
       setSize(30);
       
       doc_issue_callback_blk.addField("CALLBACK_DOC_STATE").
       setDbName("DOC_STATE").
       unsetInsertable().
       setReadOnly().
       setLabel("DOCISSUECALLBACKDOCSTATE: Doc State").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_DEPT").
       setInsertable().
       setLabel("DOCISSUECALLBACKCALLBACKDEPT: Callback Dept").
       setDynamicLOV("GENERAL_DEPARTMENT_CU_LOV").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_DEPT_NAME").
       setLabel("DOCISSUECALLBACKCALLBACKDEPTNAME: Callback Dept Name").
       setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:CALLBACK_DEPT)").
       setReadOnly().
       setSize(20);
       mgr.getASPField("CALLBACK_DEPT").setValidation("CALLBACK_DEPT_NAME");
       
       doc_issue_callback_blk.addField("CALLBACK_UNIT").
       setInsertable().
       setLabel("DOCISSUECALLBACKCALLBACKUNIT: Callback Unit").
       setDynamicLOV("GENERAL_ZONE_LOV").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_UNIT_NAME").
       setLabel("DOCISSUECALLBACKCALLBACKUNITNAME: Callback Unit Name").
       setFunction("GENERAL_ZONE_API.Get_Zone_Desc(:CALLBACK_UNIT)").
       setReadOnly().
       setSize(20);
       mgr.getASPField("CALLBACK_UNIT").setValidation("CALLBACK_UNIT_NAME");
       
       doc_issue_callback_blk.addField("CALLBACK_PAPER_QTY").
       setDbName("PAPER_QTY").
       unsetInsertable().
       setReadOnly().
       setLabel("DOCISSUECALLBACKPAPERQTY: Paper Qty").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_BLUEPRINT_QTY", "Number").
       setDbName("BLUEPRINT_QTY").
       unsetInsertable().
       setReadOnly().
       setLabel("DOCISSUECALLBACKBLUEPRINTQTY: Blueprint Qty").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_WHITEPRINT_QTY", "Number").
       setDbName("WHITEPRINT_QTY").
       unsetInsertable().
       setReadOnly().
       setLabel("DOCISSUECALLBACKWHITEPRINTQTY: Whiteprint Qty").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_CONTROLLED_NO").
       setDbName("CONTROLLED_NO").
       unsetInsertable().
       setReadOnly().
       setLabel("DOCISSUECALLBACKCONTROLLEDNO: Controlled No").
       setSize(20);

       doc_issue_callback_blk.addField("CALLBACKED").
       setInsertable().
       setCheckBox("FALSE,TRUE").
       setLabel("DOCISSUECALLBACKCALLBACKED: Callbacked").
       setSize(5);
       
       doc_issue_callback_blk.addField("CALLBACK_DATE","Date").
       setInsertable().
       setLabel("DOCISSUECALLBACKCALLBACKDATE: Callback Date").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_RECEIVER").
       setDbName("RECEIVER").
       setInsertable().
       setLabel("DOCISSUECALLBACKRECEIVER: Receiver").
       setDynamicLOV("PERSON_INFO_LOV").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_RECEIVER_NAME").
       setLabel("DOCISSUECALLBACKRECEIVERNAME: Receiver Name").
       setFunction("PERSON_INFO_API.Get_Name(:CALLBACK_RECEIVER)").
       setReadOnly().
       setSize(10);
       mgr.getASPField("CALLBACK_RECEIVER").setValidation("CALLBACK_RECEIVER_NAME");
       
       doc_issue_callback_blk.addField("FROM_DIST_NO").
       setLabel("DOCISSUECALLBACKFROMDISTNO: From Dist No").
       setReadOnly().
       unsetInsertable().
       setHidden().
       setHyperlink("../docctw/DocDistributionCtl.page", "FROM_DIST_NO DIST_NO", "NEWWIN").
       setSize(10);
       
       doc_issue_callback_blk.addField("FROM_DIST_SEQ").
       setLabel("DOCISSUECALLBACKFROMDISTSEQ: From Dist Seq").
       setReadOnly().
       setFunction("DOC_DISTRIBUTION_CTL_API.Get_Dist_Seq(:FROM_DIST_NO)").
       setHyperlink("../docctw/DocDistributionCtl.page", "FROM_DIST_NO DIST_NO", "NEWWIN").
       setSize(10);
       
       doc_issue_callback_blk.addField("GENERATE_DATE","Date").
       setReadOnly().
       unsetInsertable().
       setLabel("DOCISSUECALLBACKGENERATEDATE: Generate Date").
       setSize(10);
       
       doc_issue_callback_blk.addField("CALLBACK_NOTE").
       setDbName("NOTE").
       setInsertable().
       setLabel("DOCISSUECALLBACKNOTE: Note").
       setSize(50);
       
       doc_issue_callback_blk.setView("DOC_ISSUE_CALLBACK_REF");
       doc_issue_callback_blk.defineCommand("DOC_ISSUE_CALLBACK_API", "New__,Modify__,Remove__");
       doc_issue_callback_blk.setMasterBlock(headblk);
       doc_issue_callback_set = doc_issue_callback_blk.getASPRowSet();
       doc_issue_callback_bar = mgr.newASPCommandBar(doc_issue_callback_blk);
       doc_issue_callback_bar.defineCommand(doc_issue_callback_bar.OKFIND, "okFindCallback");
       doc_issue_callback_bar.defineCommand(doc_issue_callback_bar.NEWROW, "newRowCallback");
       doc_issue_callback_tbl = mgr.newASPTable(doc_issue_callback_blk);
       doc_issue_callback_tbl.setTitle("DOCISSUECALLBACKITEMHEAD1: DocIssueCallback");
       doc_issue_callback_tbl.enableRowSelect();
       doc_issue_callback_tbl.setWrap();
       doc_issue_callback_lay = doc_issue_callback_blk.getASPBlockLayout();
       doc_issue_callback_lay.setDefaultLayoutMode(doc_issue_callback_lay.MULTIROW_LAYOUT);
       doc_issue_callback_lay.setSimple("CALLBACK_DEPT_NAME");
       doc_issue_callback_lay.setSimple("CALLBACK_UNIT_NAME");
       doc_issue_callback_lay.setSimple("CALLBACK_RECEIVER_NAME");
       
       //
       // Document Distribution
       //
       
       doc_issue_distribution_blk = mgr.newASPBlock("DOCDIST");
       doc_issue_distribution_blk.addField("DOCDIST_OBJID").
       setDbName("OBJID").
       setHidden();
       
       doc_issue_distribution_blk.addField("DOCDIST_OBJVERSION").
       setDbName("OBJVERSION").
       setHidden();
       
       doc_issue_distribution_blk.addField("DOCDIST_DOC_CLASS").
       setDbName("DOC_CLASS").
       setMandatory().
       setInsertable().
       setLabel("DOCISSUEDISTRIBUTIONDOCCLASS: Doc Class").
       setSize(10).
       setHidden();
       
       doc_issue_distribution_blk.addField("DOCDIST_DOC_NO").
       setDbName("DOC_NO").
       setMandatory().
       setInsertable().
       setLabel("DOCISSUEDISTRIBUTIONDOCNO: Doc No").
       setSize(10).
       setHidden();
       
       doc_issue_distribution_blk.addField("DOCDIST_DOC_SHEET").
       setDbName("DOC_SHEET").
       setMandatory().
       setInsertable().
       setLabel("DOCISSUEDISTRIBUTIONDOCSHEET: Doc Sheet").
       setSize(10).
       setHidden();
       
       doc_issue_distribution_blk.addField("DOCDIST_DOC_REV").
       setDbName("DOC_REV").
       setMandatory().
       setInsertable().
       setLabel("DOCISSUEDISTRIBUTIONDOCREV: Doc Rev").
       setSize(10).
       setHidden();
       
       doc_issue_distribution_blk.addField("DIST_NO").
       setReadOnly().
       setSecureHyperlink("../docctw/DocDistributionCtl.page", "DIST_NO", "NEWWIN").
       setLabel("DOCISSUEDISTRIBUTIONDISTNO: Dist No").
       setSize(10);
       
       doc_issue_distribution_blk.addField("DIST_SEQ").
       setReadOnly().
       setSecureHyperlink("../docctw/DocDistributionCtl.page", "DIST_NO", "NEWWIN").
       setLabel("DOCISSUEDISTRIBUTIONDISTSEQ: Dist Seq").
       setSize(20);
       
       doc_issue_distribution_blk.addField("DIST_TYPE").
       setReadOnly().
       setLabel("DOCISSUEDISTRIBUTIONDISTTYPE: Dist Type").
       setSize(10);
       
       doc_issue_distribution_blk.addField("DIST_SUB_TYPE").
       setReadOnly().
       setLabel("DOCISSUEDISTRIBUTIONDISTSUBTYPE: Dist Sub Type").
       setSize(10);
       
       doc_issue_distribution_blk.addField("UNDERTAKER").
       setReadOnly().
       setLabel("DOCISSUEDISTRIBUTIONUNDERTAKER: Undertaker").
       setSize(10);
       
       doc_issue_distribution_blk.addField("DIST_DATE", "Date").
       setReadOnly().
       setLabel("DOCISSUEDISTRIBUTIONDISTDATE: Dist Date").
       setSize(10);                  
       
       doc_issue_distribution_blk.setView("DOC_DISTRIBUTION_FILES_CTL");     
       doc_issue_distribution_set = doc_issue_distribution_blk.getASPRowSet();
       doc_issue_distribution_bar = mgr.newASPCommandBar(doc_issue_distribution_blk);
       doc_issue_distribution_blk.setMasterBlock(headblk);
       doc_issue_distribution_bar.defineCommand(doc_issue_distribution_bar.OKFIND, "okFindDistribution");
       
       doc_issue_distribution_bar.enableMultirowAction();
       doc_issue_distribution_tbl = mgr.newASPTable(doc_issue_distribution_blk);
       doc_issue_distribution_tbl.setTitle("DOCISSUEDISTRIBUTIONTBLTITLE: Doc Issue Distribution");
       doc_issue_distribution_tbl.enableRowSelect();
       doc_issue_distribution_tbl.setWrap();
       doc_issue_distribution_lay = doc_issue_distribution_blk.getASPBlockLayout();
       doc_issue_distribution_lay.setDefaultLayoutMode(doc_issue_distribution_lay.MULTIROW_LAYOUT);
       
       
      //
      // DUMMY BLOCK
      //
      dummyblk = mgr.newASPBlock("DUMMY");

      dummyblk.addField("DOC_TYPE");
      dummyblk.addField("RETURN");
      dummyblk.addField("ATTR");
      dummyblk.addField("APPSTAT_DUMMY");
      dummyblk.addField("ACTION");
      dummyblk.addField("DUMMY1");
      dummyblk.addField("DUMMY2");
      dummyblk.addField("DUMMY3");
      dummyblk.addField("DUMMY4");
      dummyblk.addField("DUMMY5");
      dummyblk.addField("DUMMY6");

      dummyblk.addField("ACCESSOWNER");
      dummyblk.addField("LOGUSER");
      dummyblk.addField("USER_ID");
      dummyblk.addField("USERGETACCESS");
      dummyblk.addField("DUMMY_IN_1");
      dummyblk.addField("DUMMY_IN_2");
      dummyblk.addField("DUMMY_OUT_1");

      //
      // Tab definitions
      //
      tabs = mgr.newASPTabContainer();
      // customizeTabs();
      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);
      
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEOBJECTS: Objects"), "javascript:commandSet('HEAD.activateObjects','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUECONSISTS: Consists Of"), "javascript:commandSet('HEAD.activateConsistsOf','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEWHEREUSED: Where Used"), "javascript:commandSet('HEAD.activateWhereUsed','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEACCESS: Access"), "javascript:commandSet('HEAD.activateAccess','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEHISTORY: History"), "javascript:commandSet('HEAD.activateHistory','')");
      tabs.addTab("FILE_REF", mgr.translate("DOCMAWDOCISSUEFILEREFERENCES: File Refs."), "javascript:commandSet('HEAD.activateFileReferences','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUETARGETORG: Target Org"), "javascript:commandSet('HEAD.activateSendDept','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEATTACHMENTUPLOAD: Attach Upload"), "javascript:commandSet('HEAD.activateAttachmentUpload','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEHISTORYREVISION: History Revison"), "javascript:commandSet('HEAD.activateHistoryRevision','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUECALLBACK: Callback"), "javascript:commandSet('HEAD.activateCallback','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUETEMPATTACH: Temp"), "javascript:commandSet('HEAD.activateTempAttach','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEDISTRIBUTION: Distribution"), "javascript:commandSet('HEAD.activateDistribution','')");
      
      //
      // Static JavaScript
      //

      appendJavaScript("function getElements(name)\n");
      appendJavaScript("{\n");
      appendJavaScript("   return eval('document.form.' + name);\n");
      appendJavaScript("}\n");

      appendJavaScript("function selectDocument(parent_doc_class, parent_doc_no, parent_doc_sheet, parent_doc_rev, doc_class, doc_no, doc_sheet, doc_rev, multi_selection)\n");
      appendJavaScript("{\n");
      appendJavaScript("    if (!single_layout && multi_selection)\n");
      appendJavaScript("    {\n");
      appendJavaScript("       var selectbox = getElements(\"__SELECTED1\");\n");
      appendJavaScript("       for (var x = 0; x < arrDocNo.length; x++)\n");
      appendJavaScript("       {\n");
      appendJavaScript("          if (arrDocNo[x] == doc_no)\n");
      appendJavaScript("          {\n");
      appendJavaScript("             selectbox[x].checked = !selectbox[x].checked;\n");
      appendJavaScript("             CCA(selectbox[x], x);\n");
      appendJavaScript("             return;\n");
      appendJavaScript("          }\n");
      appendJavaScript("       }\n");
      appendJavaScript("    }\n");
      appendJavaScript("    else\n");
      appendJavaScript("    {\n");
      appendJavaScript("       document.form.PARENT_DOC_CLASS.value = parent_doc_class\n");
      appendJavaScript("       document.form.PARENT_DOC_NO.value = parent_doc_no\n");
      appendJavaScript("       document.form.PARENT_DOC_SHEET.value = parent_doc_sheet\n");
      appendJavaScript("       document.form.PARENT_DOC_REV.value = parent_doc_rev\n");
      appendJavaScript("       document.form.CURRENT_DOC_CLASS.value = doc_class\n");
      appendJavaScript("       document.form.CURRENT_DOC_NO.value = doc_no\n");
      appendJavaScript("       document.form.CURRENT_DOC_SHEET.value = doc_sheet\n");
      appendJavaScript("       document.form.CURRENT_DOC_REV.value = doc_rev\n");
      appendJavaScript("       document.form.CHANGE_CURRENT_DOCUMENT.value = \"TRUE\"\n");
      appendJavaScript("       submit() \n");
      appendJavaScript("    }\n");
      appendJavaScript("}\n");

      /*
       * appendJavaScript("function validateViewAccess(i)\n");
       * appendJavaScript("{\n");
       * appendJavaScript("if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n"
       * ); appendJavaScript("setDirty();\n");
       * appendJavaScript("if( !checkViewAccess(i) ) return;\n");
       * appendJavaScript("}\n");
       */

   }

   public void adjust() throws FndException
   {
      ASPManager mgr = getASPManager();
      String identity = getFndUser();

      if (showInMulti && (headlay.getLayoutMode() == headlay.SINGLE_LAYOUT)) {
         headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
         showInMulti = false;
      }
      //Added by lqw 20131028
      try{
         ASPField[] fields = headblk.getFields();
         ASPField tempField = null;
         for (int i = 0; i < fields.length; i++) {
            tempField = fields[i];
            tempField.setHidden();
         }
      }catch(Throwable th){
         
      }
      if(headblk.getFuncFieldsNonSelect()){
         this.setFuncFieldValue(headblk);
      }
      //Added end.
      
      //
      // Field visibility depending on layout
      //

      if (headlay.isFindLayout()) {
         headlay.setSimple("FILE_STATUS_DES");
      }


      if (headset.countRows() <= 0) {
         headbar.disableCommand("setObsolete");
         headbar.disableCommand("viewOriginal");
         headbar.disableCommand("editDocument");
         headbar.disableCommand("checkInSelectDocument");
         headbar.disableCommand("printDocument");
         headbar.disableCommand("checkInDocument");
         headbar.disableCommand("undoCheckOut");
         headbar.disableCommand("setToCopyFile");
         headbar.disableCommand("deleteDocument");
         headbar.disableCommand("deleteDocumentFile");
         headbar.disableCommand("deleteSelectDocFile");
         headbar.disableCommand("downloadDocuments");
         headbar.disableCommand("downloadDocsAndSubDocs");
         headbar.disableCommand("createNewRevision");
         headbar.disableCommand("userSettings");

         headset.clear();
         headlay.setLayoutMode(headlay.FIND_LAYOUT);
      }

      //
      // Things to adjust on every run of the page
      //

      headbar.removeCustomCommand("activateObjects");
      headbar.removeCustomCommand("activateConsistsOf");
      headbar.removeCustomCommand("activateWhereUsed");
      headbar.removeCustomCommand("activateAccess");
      headbar.removeCustomCommand("activateHistory");
      headbar.removeCustomCommand("activateFileReferences");
      headbar.removeCustomCommand("activateSendDept");
      headbar.removeCustomCommand("activateAttachmentUpload");
      headbar.removeCustomCommand("activateHistoryRevision");
      headbar.removeCustomCommand("activateCallback");
      headbar.removeCustomCommand("activateTempAttach");
      headbar.removeCustomCommand("activateDistribution");

      if (headlay.isSingleLayout()) {
         if (headset.countRows() > 0) {
            // Bug 53039, Start
            if (dAll.equals(headset.getRow().getValue("ACCESS_CONTROL")))
               bDoCheckForAllAccess = true;

            // Added by Terry 20130314
            // Page speed.
            String doc_class = headset.getValue("DOC_CLASS");
            String doc_no = headset.getValue("DOC_NO");
            String doc_sheet = headset.getValue("DOC_SHEET");
            String doc_rev = headset.getValue("DOC_REV");
            String edit_access = getEditAccess(doc_class, doc_no, doc_sheet, doc_rev);
            String view_access = getViewAccess(doc_class, doc_no, doc_sheet, doc_rev);
            // Added end

            // Modified by Terry 20130314
            // Page speed.
            if ("TRUE".equals(isAccessOwner()) || "TRUE".equals(edit_access)) {
               itembar6.enableCommand(itembar6.EDITROW);
            } else {
               itembar6.disableCommand(itembar6.EDITROW);
            }
            // Bug Id 56725, End

            if ("TRUE".equals(edit_access)) {
               headbar.enableCommand(headbar.EDITROW);
            } else {
               headbar.disableCommand(headbar.EDITROW);
            }

            if ("TRUE".equals(view_access)) {
               itembar2.enableCommand(itembar2.EDITROW);
            } else {
               itembar2.disableCommand(itembar2.EDITROW);
            }
            // Modified end
         }
      }

      if (headset.countRows() == 0) {
         // If no data in the master then remove unwanted buttons
         if ((headlay.getLayoutMode() == 0) || (headlay.isSingleLayout())) {
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.FORWARD);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand(headbar.BACK);
         }
      }

      if (headlay.getLayoutMode() == ASPBlockLayout.SINGLE_LAYOUT
            || headlay.getLayoutMode() == ASPBlockLayout.NONE) {
         if (headset.countRows() == 0) {
            headlay.setLayoutMode(headlay.NONE);
            headbar.disableCommand(headbar.OKFIND);
            headbar.disableCommand(headbar.COUNTFIND);
            headbar.disableCommand(headbar.SAVERETURN);
            headbar.disableCommand(headbar.SAVENEW);
            headbar.disableCommand(headbar.CANCELNEW);
            headbar.disableCommand(headbar.CANCELEDIT);
            headbar.disableCommand(headbar.BACK);
         } else
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }

      if (tabs.getActiveTab() == 5) {
         if (headset.countRows() > 0) // Bug Id 71698
            checkEnableHistoryNewRow();
      }

      if ("FALSE".equals(enableHistoryNewButton)) {
         itembar7.disableCommand(itembar7.NEWROW);
      }

      // disable RMB for non-IE browsers
      if (!mgr.isExplorer()) {
         headbar.disableCustomCommand("setToCopyFile");
         headbar.disableCustomCommand("userSettings");
      }

      if (!isUserSettingsEnable())
         headbar.disableCustomCommand("userSettings");

      // Bug Id 67105, Start
//      if (!isMandatorySettingsEnable())
//         headbar.disableCustomCommand("mandatorySettings");
      // Bug Id 67105, End

      // Cannot change doc revision text after released or obsolete.
      if (headlay.isEditLayout()) {
         if ((sReleased.equals(headset.getValue("STATE")))
               || (sObsolete.equals(headset.getValue("STATE"))))
            mgr.getASPField("DOC_REV_TEXT").setReadOnly();

         if ((sReleased.equals(headset.getValue("STATE")))
               || (sObsolete.equals(headset.getValue("STATE")))
               || (sApproved.equals(headset.getValue("STATE"))))
            mgr.getASPField("NEXT_DOC_SHEET").setReadOnly();
         else if ((sAppInProg.equals(headset.getValue("STATE")))
               && ("FALSE".equals(headset.getValue("APPROVAL_UPDATE"))))
            mgr.getASPField("NEXT_DOC_SHEET").setReadOnly();

      }

      if (headlay.isSingleLayout())
         headset.unselectRows();

      // disbale Find button if this is the "Explore Structure" mode
      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE"))) {
         adjustExploreStructure();
      }

      /*if (headlay.isFindLayout()) {
         mgr.getASPField("PROJ_NAME").setHidden();
         mgr.getASPField("PURPOSE_NAME").setHidden();
         mgr.getASPField("SEND_UNIT_NAME").setHidden();
         mgr.getASPField("RECEIVE_UNIT_NAME").setHidden();
      }*/
      
      
      if(!DocmawConstants.PROJ_RECEIVE.equals(getCurrentPageDocClass())){
         headbar.disableCommand("createTrans");
      }
      
      if(DocmawConstants.EXCH_SEND.equals(getCurrentPageDocClass()))
      {
         headbar.removeCustomCommand("editDocument");
         headbar.removeCustomCommand("checkInSelectDocument");
         headbar.removeCustomCommand("undoCheckOut");
         headbar.removeCustomCommand("printDocument");
         headbar.removeCustomCommand("setToCopyFile");
         headbar.removeCustomCommand("userSettings");
         headbar.removeCustomCommand("showStructureInNavigator");
         headbar.removeCustomCommand("setStructAttribAll");
         headbar.removeCustomCommand("unsetStructAttribAll");
         headbar.removeCustomCommand("createNewRevision");
      }
      
      if(DocmawConstants.EXCH_RECEIVE.equals(getCurrentPageDocClass()))
      {
         headbar.disableCommand(headbar.NEWROW);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.EDIT);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.SAVERETURN);
         headbar.disableCommand(headbar.DELETE);
         headbar.removeCustomCommand("setObsolete");
         headbar.removeCustomCommand("checkInDocument");
         headbar.removeCustomCommand("viewOriginal");
         headbar.removeCustomCommand("editDocument");
         headbar.removeCustomCommand("checkInSelectDocument");
         headbar.removeCustomCommand("undoCheckOut"); 
         headbar.removeCustomCommand("printDocument");
         headbar.removeCustomCommand("deleteDocument"); 
         headbar.removeCustomCommand("deleteDocumentFile");
         headbar.removeCustomCommand("deleteSelectDocFile");
//         headbar.removeCustomCommand("downloadDocuments");
         headbar.removeCustomCommand("setToCopyFile");
         headbar.removeCustomCommand("userSettings");
//         headbar.removeCustomCommand("resetStatus");
//         headbar.removeCustomCommand("connectObject");
         headbar.removeCustomCommand("showStructureInNavigator");
         headbar.removeCustomCommand("setStructAttribAll");
         headbar.removeCustomCommand("unsetStructAttribAll");
//         headbar.removeCustomCommand("createLink");
         headbar.removeCustomCommand("createNewRevision"); 
//         headbar.removeCustomCommand("createNewSheet");
//         headbar.removeCustomCommand("mandatorySettings");
         
         if (headlay.isSingleLayout() && headset.countRows() > 0)
         {
            String doc_class = headset.getValue("DOC_CLASS");
            String doc_no = headset.getValue("DOC_NO");
            String doc_sheet = headset.getValue("DOC_SHEET");
            String doc_rev = headset.getValue("DOC_REV");
            if ("FALSE".equals(haveSignDocument(doc_class, doc_no, doc_sheet, doc_rev)))
               headbar.removeCustomCommand("sign");
            
            if ("FALSE".equals(haveCreatedDocument(doc_class, doc_no, doc_sheet, doc_rev)))
               headbar.removeCustomCommand("transferToSignedDoc");
         }
      }
      else
      {
         headbar.removeCustomCommand("sign");
         headbar.removeCustomCommand("transferToSignedDoc");
      }
      
      if(!(DocmawConstants.PROJ_SEND.equals(getCurrentPageDocClass()) || DocmawConstants.EXCH_SEND.equals(getCurrentPageDocClass())))
      {
         headbar.disableCommand("sendDoc");
      }
      else
      {
         headbar.disableCommand(ASPCommandBar.OVERVIEWEDIT);
         if(headset.countDbRows() > 0 && headlay.isSingleLayout())
         {
            String signStatus =  headset.getDbValue("SIGN_STATUS_DB");
            if("SENDED".equals(signStatus)|| "PARTIALLYSIGNED".equals(signStatus)|| "SIGNED".equals(signStatus))
            {
               headbar.disableCommand("sendDoc");
               headbar.disableCommand(ASPCommandBar.DELETE);
               headbar.disableCommand(ASPCommandBar.EDIT);
               headbar.disableCommand(ASPCommandBar.EDITROW);
               
               itembar11.disableCommand(ASPCommandBar.NEWROW);
               itembar11.disableCommand(ASPCommandBar.DELETE);
               itembar11.disableCommand(ASPCommandBar.EDIT);
               itembar11.disableCommand(ASPCommandBar.EDITROW);
            }
         }
         else
            headbar.disableCommand("sendDoc");
      }
      
      if(!DocmawConstants.isLibaryDocClass(getCurrentPageDocClass()))
      {
         headbar.disableCommand("addToIndex");
         headbar.removeCustomCommand("createDocDistribution");
      }
      
      if (headset.countRows() > 0 && (headlay.isSingleLayout() || headlay.isCustomLayout()))
      {
         String doc_class = headset.getValue("DOC_CLASS");
         String sub_class = headset.getValue("SUB_CLASS");
         if (DocmawConstants.PROJ_SEND.equals(doc_class))
         {
            tabs.setTabRemoved(8, true);
            tabs.setTabRemoved(9, true);
            tabs.setTabRemoved(10, true);
            tabs.setTabRemoved(11, true);
            tabs.setTabRemoved(12, true);
         }
         else if (DocmawConstants.PROJ_DESIGN.equals(doc_class) || DocmawConstants.PROJ_EQUIPMENT.equals(doc_class) || (DocmawConstants.PROJ_CONSTRUCT.equals(doc_class) && DocmawConstants.PROJ_CONSTRUCT_PROCEDURE.equals(sub_class)))
         {
            tabs.setTabRemoved(7, true);
            tabs.setTabRemoved(8, true);
            tabs.setTabRemoved(11, true);
         }
         else if (DocmawConstants.EXCH_SEND.equals(doc_class) || DocmawConstants.EXCH_RECEIVE.equals(doc_class))
         {
            tabs.setTabRemoved(1, true);
            tabs.setTabRemoved(2, true);
            tabs.setTabRemoved(3, true);
            tabs.setTabRemoved(4, true);
            tabs.setTabRemoved(5, true);
            tabs.setTabRemoved(6, true);
            tabs.setTabRemoved(9, true);
            tabs.setTabRemoved(10, true);
            tabs.setTabRemoved(11, true);
            tabs.setTabRemoved(12, true);
         }
         else
         {
            tabs.setTabRemoved(7, true);
            tabs.setTabRemoved(8, true);
            tabs.setTabRemoved(9, true);
            tabs.setTabRemoved(10, true);
            tabs.setTabRemoved(11, true);
            tabs.setTabRemoved(12, true);
         }
      }
   }
   
   private String haveSignDocument(String doc_class, String doc_no, String doc_sheet, String doc_rev)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomFunction("HAVESIGNDOCUMENT", "DOC_ISSUE_TARGET_ORG_API.Have_Sign_Document", "DUMMY_OUT_1");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV", doc_rev);
      trans = mgr.perform(trans);
      return trans.getValue("HAVESIGNDOCUMENT/DATA/DUMMY_OUT_1");
   }
   
   private String haveCreatedDocument(String doc_class, String doc_no, String doc_sheet, String doc_rev)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomFunction("HAVECREATEDDOCUMENT", "Doc_Issue_API.Have_Created_Doc", "DUMMY_OUT_1");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV", doc_rev);
      trans = mgr.perform(trans);
      return trans.getValue("HAVECREATEDDOCUMENT/DATA/DUMMY_OUT_1");
   }

   protected void adjustExploreStructure() throws FndException {
      ASPManager mgr = getASPManager();

      // disable find button to prevent frames from
      // being "out-of-synch"
      headbar.disableCommand(ASPCommandBar.FIND);

      int count = headset.countRows();
      int current_row = headset.getCurrentRowNo();

      appendDirtyJavaScript("var single_layout = " + headlay.isSingleLayout()
            + ";\n");
      appendDirtyJavaScript("var arrDocNo = new Array(" + count + ");\n");
      headset.first();
      for (int x = 0; x < count; x++) {
         appendDirtyJavaScript("arrDocNo[" + x + "] = \""
               + mgr.encodeStringForJavascript(headset.getValue("DOC_NO"))
               + "\";\n");
         headset.next();
      }
      headset.goTo(current_row);
   }

   public String tabsFinish() {
      return tabs.showTabsFinish();
   }

   private void drawPageHeader(AutoString out) {
      ASPManager mgr = getASPManager();

      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE"))) {
         // In this mode, the header is not reaquired..
      } else {
         if (headlay.isMultirowLayout())
            out.append(mgr
                  .startPresentation("DOCMAWDOCISSUEOVERVIEWREVISIONS: Overview - Documents"));
         else
            out.append(mgr.startPresentation(getTitle()));
      }
   }

   private void drawPageFooter(AutoString out) {
      ASPManager mgr = getASPManager();

      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE"))) {
         // disableFooter();
      }

      out.append(mgr.endPresentation());
   }

   protected String getDescription() {
      return "DOCMAWDOCISSUEDLGTITLE: Select Approval Template to Copy";
   }

   protected String getTitle() {
      return "DOCMAWDOCISSUEDOCUMENTINFO: Document Info";
   }

   protected AutoString getContents() throws FndException {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");

      if (bCopyProfile) {
         out.append(mgr
               .generateHeadTag("DOCMAWDOCISSUEDLGTITLE: Select Approval Template to Copy"));
      } else {
         if (headlay.isMultirowLayout())
            out.append(mgr
                  .generateHeadTag("DOCMAWDOCISSUEOVERVIEWREVISIONS: Overview - Documents"));
         else
            out.append(mgr
                  .generateHeadTag("DOCMAWDOCISSUEDOCUMENTINFO: Document Info"));
      }

      out.append("</head>\n");
      out.append("<body " + mgr.generateBodyTag());
      out.append("><form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"RETURN_OBJ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CONFIRM\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"TEMP_LU_NAME\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"TEMP_KEY_REF\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"OBJECT_INSERTED\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"MODE_CHANGED\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"REFRESH_PARENT\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"REFRESH_CHILD\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"MULTIROWACTION\" value=\"\">\n");

      out.append("<input type=\"hidden\" name=\"SAME_ACTION_TO_ALL\" value=\"NO\">\n");
      out.append("<input type=\"hidden\" name=\"CPY_TO_DIR_PATH\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"LEAVE_ROWS_SELECTED\" value=\"NO\">\n");
      out.append("<input type=\"hidden\" name=\"DOC_CLASS_FROM_WIZ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DOC_NO_FROM_WIZ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DOC_SHEET_FROM_WIZ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DOC_REV_FROM_WIZ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"MULTIROW_EDIT\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"LAUNCH_FILE\" value=\"YES\">\n");
      out.append("<input type=\"hidden\" name=\"CHANGE_CURRENT_DOCUMENT\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"PARENT_DOC_CLASS\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PARENT_DOC_NO\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PARENT_DOC_SHEET\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PARENT_DOC_REV\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CURRENT_DOC_CLASS\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CURRENT_DOC_NO\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CURRENT_DOC_SHEET\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CURRENT_DOC_REV\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DROPED_FILES_LIST\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DROPED_FILES_PATH\" value=\"\">\n");
      // Bug 70808, Start
      out.append("<input type=\"hidden\" name=\"BGJ_CONFIRMED\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"BGJ_DIALOG_POPPED\" value=\"\">\n");
      // Bug 70808, End

      customizeOut(out);

      drawPageHeader(out);

      if (headlay.isVisible()) {
         if (headset.countRows() > 0 && headlay.isMultirowLayout()) {

            //
            // Generate html for displaying additional objects (menu bar, row
            // selection, radio buttons)
            // that are shown only in the multirow layout
            //

            // Custom menu bar for performing multirow actions

            // Radio buttons for selecting the way file operations should work
            out.append("<table border=0 cellspacing=0 cols=7 style='display:none'>\n");
            out.append("<tr><td>&nbsp;&nbsp;</td><td colspan=\"1\" width=\"100%\">\n<table border=0 cellspacing=0 cellpadding=2 cols=2 width=\"65%\">\n");
            out.append("<tr><td colspan=\"1\" nowrap>");
            out.append(fmt.drawRadio(
                  "DOCMAWDOCISSUEAPPLYSAMETOALL: Execute same action for all",
                  "sameActionRadio", "APPLY_SAME_TO_ALL", false,
                  "onclick=\"javascript:setSameAction(this)\""));
            out.append("</td><td colspan=\"1\" nowrap>");
            out.append(fmt
                  .drawRadio(
                        "DOCMAWDOCISSUELAUNCHTHEFILE: Launch Files    (for View and Check Out)",
                        "launchFileRadio", "LAUNCH_FILE", true,
                        "onclick=\"javascript:setLuanch(this)\""));
            out.append("</td></tr><tr><td colspan=\"1\" nowrap>\n");
            out.append(fmt
                  .drawRadio(
                        "DOCMAWDOCISSUEAPPLYDIFFTOEACH: Choose action for each document",
                        "sameActionRadio", "APPLY_DIFF_TO_EACH", true,
                        "onclick=\"javascript:setSameAction(this)\""));
            out.append("</td><td colspan=\"1\" nowrap>");
            out.append(fmt.drawRadio(
                  "DOCMAWDOCISSUEDONTLAUNCHTHEFILE: Don't Launch",
                  "launchFileRadio", "DONT_LAUNCH_FILES", false,
                  "onclick=\"javascript:setLuanch(this)\""));
            out.append("</td></tr></table>\n");
            out.append("</td></tr></table>\n");
         } else if (headlay.isSingleLayout()) {
            if (!"DocIssueStructureQuery".equals(getPageName()) && "FALSE".equals(haveEdmFile(
                  headset.getRow().getValue("DOC_CLASS"), headset.getRow().getValue("DOC_NO"),
                  headset.getRow().getValue("DOC_SHEET"), headset.getRow().getValue("DOC_REV")))
                  && !DocmawConstants.EXCH_RECEIVE.equals(getCurrentPageDocClass())
                  && mgr.isExplorer()) {
               // ===== Client script =====

               out.append("<script type=\"text/JavaScript\" for=\"DropArea\" event=\"DocmanDragDrop()\">\n");

               String unicode_msg = mgr
                     .translate("DOCMAWDOCISSUEUNICODECHARS: The Drag and Drop Area does not support adding files with Unicode characters and any such file will be excluded. Do you want to continue?");

               // Bug Id 92125, Start
               out.append("function document.form.DropArea::OLEDragDrop(data, effect, button, shift, x, y){\n");
               out.append("   var filesDropped = \"\";\n");
               out.append("   var path = \"\";\n");
               out.append("   var fullFileName;\n");
               out.append("   var foundUnicodeFiles = false;\n");
               out.append("   var count = 0;\n");
               out.append("   var bShowFileExtError = false;\n");
               out.append("   if(data.GetFormat(15)){\n");
               out.append("      var e = new Enumerator(data.Files);\n");
               out.append("      while(!e.atEnd()){\n");
               out.append("         count += 1;\n");
               out.append("         fullFileName =  \"\" + e.item(); \n");
               out.append("         if (fullFileName.indexOf(\"?\") != -1)\n");
               out.append("            foundUnicodeFiles = true;\n");
               out.append("         else\n");
               out.append("            filesDropped += fullFileName.substr(fullFileName.lastIndexOf(\"\\\\\")+1) + \"|\"; \n");
               out.append("         if((fullFileName.substr(fullFileName.lastIndexOf(\"\\\\\")+1)).lastIndexOf(\".\") === -1)\n");
               out.append("            bShowFileExtError = true;\n");
               out.append("         if (path == \"\")\n");
               out.append("            path = fullFileName.substr(0,fullFileName.lastIndexOf(\"\\\\\")); \n");
               out.append("         e.moveNext();\n");
               out.append("      }\n");
               out.append("   }\n");
               out.append("   document.form.DropArea.Backcolor = oldBackColor;\n");
               out.append("   if (bShowFileExtError==true && count===1)\n");
               out.append("   {\n");
               out.append("      alert(\"");
               out.append(mgr
                     .translate("DOCMAWDOCISSUENOEXTALERT: File(s) without extension(s) are not allowed."));
               out.append("\");\n");
               out.append("      return;\n");
               out.append("   }\n");
               out.append("   filesDropped = filesDropped.substr(0,filesDropped.length-1);\n"); // remove
                                                                                                // last
                                                                                                // '|'
               out.append("   if (foundUnicodeFiles)\n");
               out.append("   {\n");
               out.append("      if (!confirm(\"" + unicode_msg + "\"))\n");
               out.append("         return;\n");
               out.append("   }\n");
               out.append("   if (filesDropped != \"\")\n");
               out.append("   {\n");
               out.append("       document.form.DROPED_FILES_LIST.value=filesDropped;\n");
               out.append("       document.form.DROPED_FILES_PATH.value=path;\n");
               out.append("       submit();\n");
               out.append("   }\n");
               out.append("}\n");
               // Bug Id 92125, End

               out.append(DocmawUtil.writeOleDragOverFunction(
                     mgr.translate("DOCMAWDOCISSUEDROPOBJECTDROPHERE: Drop one file here to check in or many files to import"),
                     mgr.translate("DOCMAWDOCISSUEDROPAREAACCEPTEDMSG: Files to drop"),
                     mgr.translate("DOCMAWDOCISSUEDROPAREAILLEGALMSG: Only files are accepted")));

               out.append("</script>  \n");
               // Add drop area here

               out.append("   <table width=300 height=25 border=\"0\">\n");
               out.append("     <tr>\n");
               out.append("       <td>\n");
               out.append("        &nbsp;&nbsp;");
               out.append(DocmawUtil.drawDnDArea("", ""));
               out.append("       </td>\n");
               out.append("     </tr>");
               out.append("   </table>\n");

               // End of drop area
            }
         }

         // Bug Id 67105, Start
         if (headlay.isFindLayout() && !mgr.isEmpty(sMandatoryFields)) {
            if (bMandatoryFieldsEmpty) {
               out.append("<TABLE cellspacing=0 cellpadding=0 border=0 width=100%>\n");
               out.append("<TR>\n");
               out.append("<td>&nbsp;&nbsp;</td>\n");
               out.append("<td width=100%>\n");
               out.append("<TABLE id=cntMAIN bgcolor=#FFCC66 height=10 cellSpacing=0 cellPadding=0  width=100% border=0> \n");
               out.append("<TBODY>\n");
               out.append("<TR>\n");
               out.append("<TD width=100% height=30>&nbsp;&nbsp;\n");
               out.append("<FONT class=normalTextLabel>\n");
               out.append(mgr
                     .translate("DOCMAWDOCISSUEQUERYREQ: This page requires one of the following fields to be filled in before searching: "));// Bug
                                                                                                                                              // Id
                                                                                                                                              // 77651
               out.append(sMandatoryFields);
               out.append("<BR>&nbsp;&nbsp;\n");
               out.append(mgr
                     .translate("DOCMAWDOCISSUEQUERYINVALIDE: Note: A single % is not enough."));
               out.append("</FONT>\n");
               out.append("</TD>\n");
               out.append("</TR>\n");
               out.append("</table>\n");
               out.append("</TD><td>&nbsp;&nbsp;</td></TR>\n");
               out.append("</TABLE>\n");
            } else {
               out.append("<TABLE cellspacing=0 cellpadding=0 border=0 width=100%>\n");
               out.append("<TR>\n");
               out.append("<td>&nbsp;&nbsp;</td>\n");
               out.append("<td width=100%>\n");
               out.append("<TABLE class=pageFormWithoutBottomLine id=cntMAIN height=10 cellSpacing=0 cellPadding=0  width=100% border=0> \n");
               out.append("<TBODY>\n");
               out.append("<TR>\n");
               out.append("<TD width=100% height=30>&nbsp;&nbsp;\n");
               out.append("<FONT class=normalTextLabel>\n");
               out.append(mgr
                     .translate("DOCMAWDOCISSUEQUERYREQ: This page requires one of the following fields to be filled in before searching: "));// Bug
                                                                                                                                              // Id
                                                                                                                                              // 77651
               out.append(sMandatoryFields);
               out.append("<BR>&nbsp;&nbsp;\n");
               out.append(mgr
                     .translate("DOCMAWDOCISSUEQUERYINVALIDE: Note: A single % is not enough."));
               out.append("</FONT>\n");
               out.append("</TD>\n");
               out.append("</TR>\n");
               out.append("</table>\n");
               out.append("</TD><td>&nbsp;&nbsp;</td></TR>\n");
               out.append("</TABLE>\n");
            }
         }
         // Bug Id 67105, End
         out.append(headlay.show());
      }

      else {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         out.append(headlay.show());
      }

      if (headset.countRows() > 0) {
         if (headlay.isSingleLayout() || headlay.isCustomLayout()) {

            //
            // Generate html for displaying the detailed view of the page
            //
            out.append(tabs.showTabsInit());
            if (tabs.getActiveTab() == 1)
            {
               out.append(itemlay2.show());
            }
            else if (tabs.getActiveTab() == 2)
            {
               out.append(itemlay3.show());
            }
            else if (tabs.getActiveTab() == 3)
            {
               out.append(itemlay4.show());
            }
            else if (tabs.getActiveTab() == 4)
            {
               out.append(itemlay6.show());
            }
            else if (tabs.getActiveTab() == 5)
            {
               out.append(itembar7.showBar());
               out.append("<table cellpadding=\"10\" cellspacing=\"0\" border=\"0\" width=\"");
               out.append("\">\n");
               out.append("<tr><td>");
               out.append(fmt.drawRadio(
                     "DOCMAWDOCISSUECHECKEDINOUT: Checked In/Out",
                     "HISTORY_MODE", "1", "1".equals(sHistoryMode),
                     "OnClick=\"modeChanged()\""));
               out.append("</td><td>");
               out.append(fmt.drawRadio("DOCMAWDOCISSUEACCESSCB: Access",
                     "HISTORY_MODE", "2", "2".equals(sHistoryMode),
                     "OnClick=\"modeChanged()\""));
               out.append("</td><td>");
               out.append(fmt.drawRadio("DOCMAWDOCISSUEOTHERSCB: Others",
                     "HISTORY_MODE", "3", "5".equals(sHistoryMode),
                     "OnClick=\"modeChanged()\""));
               out.append("</td></tr><tr><td>");
               out.append(fmt.drawRadio(
                     "DOCMAWDOCISSUEAPPROUTING: Approval Routing",
                     "HISTORY_MODE", "4", "4".equals(sHistoryMode),
                     "OnClick=\"modeChanged()\""));
               out.append("</td><td>");
               out.append(fmt.drawRadio(
                     "DOCMAWDOCISSUEINFORMATION: Information", "HISTORY_MODE",
                     "5", "5".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
               out.append("</td><td>");
               out.append(fmt.drawRadio("DOCMAWDOCISSUESTRUCTURE: Structure",
                     "HISTORY_MODE", "6", "6".equals(sHistoryMode),
                     "OnClick=\"modeChanged()\""));
               out.append("</td><td>");
               out.append(fmt.drawRadio("DOCMAWDOCISSUEALL: All",
                     "HISTORY_MODE", "7", "7".equals(sHistoryMode),
                     "OnClick=\"modeChanged()\""));
               out.append("</td></tr>");
               out.append("</table>\n");
               out.append(itemlay7.generateDataPresentation());
            }
            else if (tabs.getActiveTab() == 6)
            {
               out.append(itemlay9.show());
            }
            else if (tabs.getActiveTab() == 7)
            {
               out.append(itemlay11.show());
            }
            else if (tabs.getActiveTab() == 8)
            {
               showAttachUpload(out);
            }
            else if (tabs.getActiveTab() == 9)
            {
               out.append(itemlay13.show());
            }
            else if (tabs.getActiveTab() == 10)
            {
               out.append(doc_issue_callback_lay.show());
            }           
            else if (tabs.getActiveTab() == 11)
            {
               showAttachUpload(out,DocmawConstants.EXCH_TEMP);
            }
            else if (tabs.getActiveTab() == 12)
            {
               out.append(doc_issue_distribution_lay.show());
            }
            out.append(tabsFinish());
         }
      }

      //
      // CLIENT FUNCTIONS
      //

      // Bug 70808, Start
      if (bConfirmBackgroundJob) {
         bConfirmBackgroundJob = false;
         ctx.writeValue("BGJ_DIALOG_POPPED", "TRUE");
         appendDirtyJavaScript("displayBGJConfirmWindow();");
      }
      // Bug 70808, End

      if (bConfirm) {
         appendDirtyJavaScript("displayConfirmBox();");
         bConfirm = false;
      }

      if (bConfirmEx) {
         appendDirtyJavaScript("displayConfirmBoxEx('"
               + mgr.encodeStringForJavascript(sMessage) + "');");
         bConfirmEx = false;
      }

      if (bConfiramtion4SettingStructureType) {
         appendDirtyJavaScript("displayConfirmBox();");

      }

      appendDirtyJavaScript("last_value = '';\n"); // For Association catogory
                                                   // functionality
      appendDirtyJavaScript("popup_window = '';\n"); // Bug Id 70808

      appendDirtyJavaScript("function validateKeepLastDocRev(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if (getRowStatus_('ITEM2',i)=='QueryMode__') return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("if( !checkKeepLastDocRev(i) ) return;\n");
      appendDirtyJavaScript("    r = __lookup(\n"); // tested
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")
            + "common/scripts/Lookup.page");
      appendDirtyJavaScript("?',\n");
      appendDirtyJavaScript("        'DUAL',\n");
      appendDirtyJavaScript("URLClientEncode(");
      appendDirtyJavaScript("        'Always_Last_Doc_Rev_API.Encode('\n");
      appendDirtyJavaScript("        + \"'\" + getValue_('KEEP_LAST_DOC_REV',i) + \"'\"\n");
      appendDirtyJavaScript("        + ') SKEEPCODE'), '', '');\n");
      appendDirtyJavaScript("if (checkStatus_(r,'KEEP_LAST_DOC_REV',i,'Update Revision')){\n");
      appendDirtyJavaScript("    assignValue_('SKEEPCODE',i,0);\n");
      appendDirtyJavaScript("        // Overrride normal validate function\n");
      appendDirtyJavaScript("        // According to the selected value changen readonly state of the doc revision\n");
      appendDirtyJavaScript("        keepcode = __getValidateValue(0);\n"); // tested
      appendDirtyJavaScript("        if( keepcode == 'F')\n"); // we check with
                                                               // db value
      appendDirtyJavaScript("           getField_('ITEM2_DOC_REV',i).readOnly = false;\n");
      appendDirtyJavaScript("        else\n");
      appendDirtyJavaScript("           getField_('ITEM2_DOC_REV',i).readOnly = true;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      // Bug 70808, Start
      appendDirtyJavaScript("function displayBGJConfirmWindow()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    var cookie_value = readCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\");\n");
      appendDirtyJavaScript("    removeCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\", COOKIE_PATH);\n");
      appendDirtyJavaScript("    unconfirm = (readCookie(f.__PAGE_ID.value).length>1);\n");
      appendDirtyJavaScript("    onLoad();\n");
      appendDirtyJavaScript("    if (cookie_value ==\"TRUE\" && (!unconfirm)&&(document.form.CONFIRM.value==\"\"))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("      lov_window = window.open(");
      appendDirtyJavaScript("      \"" + sUrl + "\",");
      appendDirtyJavaScript("      \"anotherWindow\",");
      appendDirtyJavaScript("      \"width=400, height=130, left=200, top=200\");\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("       document.form.CONFIRM.value=\"\";\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function submitParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("}\n");
      // Bug Id 70808, End

      appendDirtyJavaScript("function displayConfirmBox()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   unconfirm = (readCookie(f.__PAGE_ID.value).length>1);\n");
      appendDirtyJavaScript("   onLoad();\n");
      appendDirtyJavaScript("   if ((!unconfirm)&&(document.form.CONFIRM.value==\"\"))\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (confirm('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sMessage));
      appendDirtyJavaScript("'))\n");
      appendDirtyJavaScript("         document.form.CONFIRM.value='OK';\n");
      appendDirtyJavaScript("      else\n");
      appendDirtyJavaScript("         document.form.CONFIRM.value='CANCEL';\n");
      appendDirtyJavaScript("      submit();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("      document.form.CONFIRM.value=\"\";\n");
      appendDirtyJavaScript("}\n");

      if (bObjectConnection) {
         appendDirtyJavaScript("openObjectConnections();\n");
         appendDirtyJavaScript("function openObjectConnections()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("    openLOVWindow('RETURN_OBJ',-1,\n");
         appendDirtyJavaScript("        '");
         appendDirtyJavaScript(root_path);
         appendDirtyJavaScript("docmaw/ObjectConnection.page?SERVICE_LIST=DocReferenceObject&__DYNAMIC_DEF_KEY=&FROM_DOCISSUE=TRUE'\n"); // Bug
                                                                                                                                         // ID
                                                                                                                                         // 65462
         appendDirtyJavaScript("        ,500,500,'insertRecord');\n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("function insertRecord()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   ret_val = document.form.RETURN_OBJ.value;\n");
         appendDirtyJavaScript("   seperate_pt = ret_val.indexOf('~');\n");
         appendDirtyJavaScript("   lu_name = ret_val.substr(0,seperate_pt);\n");
         appendDirtyJavaScript("   key_ref = ret_val.substr(seperate_pt+1,ret_val.length-seperate_pt-1);\n");
         appendDirtyJavaScript("   document.form.TEMP_LU_NAME.value = lu_name;\n");
         appendDirtyJavaScript("   document.form.TEMP_KEY_REF.value = key_ref;\n");
         appendDirtyJavaScript("   document.form.OBJECT_INSERTED.value = \"TRUE\";\n");
         appendDirtyJavaScript("   submit();\n");
         appendDirtyJavaScript("}\n");

      }

      //
      // Access tab read only settings
      //

      appendDirtyJavaScript("function checkItem6GroupId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    fld = getField_('ITEM6_GROUP_ID',i);\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM6',i)!='New__' && !IS_EXPLORER && !checkReadOnly_(fld,'Group Id') ) return false;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM6_PERSON_ID',i)!='' && getValue_('ITEM6_GROUP_ID',i)!='' )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       alert('");
      appendDirtyJavaScript(sPersonError);
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("       getField_('ITEM6_GROUP_ID',i).value = '';\n");
      appendDirtyJavaScript("       getField_('GROUPDESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    fld.value = fld.value.toUpperCase();\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkItem6PersonId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    fld = getField_('ITEM6_PERSON_ID',i);\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM6',i)!='New__' && !IS_EXPLORER && !checkReadOnly_(fld,'Person Id') ) return false;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM6_GROUP_ID',i)!='' && getValue_('ITEM6_PERSON_ID',i)!='' )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       alert('");
      appendDirtyJavaScript(sPersonError);
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("       getField_('ITEM6_PERSON_ID',i).value = '';\n");
      appendDirtyJavaScript("       getField_('PERSONNAME',i).value = '';\n");
      appendDirtyJavaScript("       getField_('PERSONUSERID',i).value = '';\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    fld.value = fld.value.toUpperCase();\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem6PersonId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkItem6PersonId(i) ) return;\n");
      appendDirtyJavaScript("   if( getValue_('ITEM6_PERSON_ID',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("   if( getValue_('ITEM6_GROUP_ID',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("   if( getValue_('ITEM6_PERSON_ID',i)=='' )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      getField_('PERSONNAME',i).value = '';\n");
      appendDirtyJavaScript("      getField_('PERSONUSERID',i).value = '';\n");
      appendDirtyJavaScript("      return;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    window.status='Please wait for validation';\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("    APP_ROOT+ 'docmaw/DocIssue.page'+'?VALIDATE=ITEM6_PERSON_ID'\n");
      appendDirtyJavaScript("    + '&ITEM6_PERSON_ID=' + URLClientEncode(getValue_('ITEM6_PERSON_ID',i))\n");
      appendDirtyJavaScript("    + '&ITEM6_GROUP_ID=' + URLClientEncode(getValue_('ITEM6_GROUP_ID',i))\n");
      appendDirtyJavaScript("    );\n");
      appendDirtyJavaScript("    window.status='';\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'ITEM6_PERSON_ID',i,'Person ID') )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       assignValue_('PERSONNAME',i,0);\n");
      appendDirtyJavaScript("       assignValue_('PERSONUSERID',i,1);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if(getField_('PERSONUSERID',i).value =='')\n");
      appendDirtyJavaScript("       if(!confirm('"
            + mgr.translate("DOCMAWDOCISSUENOUSERCONNECTEDPERSON: There is no User Id connected to Person Id")
            + ":'+getField_('ITEM6_PERSON_ID',i).value +'\\n"
            + mgr.translate("DOCMAWDOCISSUESTILLWANTPROCEED: Still want to proceed")
            + "?'))\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          getField_('PERSONNAME',i).value = '';\n");
      appendDirtyJavaScript("          getField_('PERSONUSERID',i).value = '';\n");
      appendDirtyJavaScript("          getField_('ITEM6_PERSON_ID',i).value = '';\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkAccessOwner(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    access_owner = getField_('ACCESS_OWNER',i);\n");
      appendDirtyJavaScript("    edit_access = getField_('EDIT_ACCESS',i);\n");
      appendDirtyJavaScript("    view_access = getField_('VIEW_ACCESS',i);\n");
      // Added by Terry 20100125
      appendDirtyJavaScript("    download_access = getField_('DOWNLOAD_ACCESS',i);\n");
      appendDirtyJavaScript("    print_access = getField_('PRINT_ACCESS',i);\n");
      // Added end
      appendDirtyJavaScript("    if(access_owner.checked)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       edit_access.checked = true;\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      // Added by Terry 20100125
      appendDirtyJavaScript("       download_access.checked = true;\n");
      appendDirtyJavaScript("       print_access.checked = true;\n");
      // Added end
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkEditAccess(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    access_owner = getField_('ACCESS_OWNER',i);\n");
      appendDirtyJavaScript("    edit_access = getField_('EDIT_ACCESS',i);\n");
      appendDirtyJavaScript("    view_access = getField_('VIEW_ACCESS',i);\n");
      appendDirtyJavaScript("    if((!edit_access.checked)&&(access_owner.checked))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       edit_access.checked = true;\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if(edit_access.checked)\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkViewAccess(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    //alert('view access changed........')\n");

      appendDirtyJavaScript("    access_owner = getField_('ACCESS_OWNER',i);\n");
      appendDirtyJavaScript("    edit_access = getField_('EDIT_ACCESS',i);\n");
      appendDirtyJavaScript("    view_access = getField_('VIEW_ACCESS',i);\n");
      appendDirtyJavaScript("    if(getField_('ITEM6_EXIST_IN_APPROVAL',i).value=='TRUE' && !view_access.checked)\n");
      appendDirtyJavaScript("       if(!confirm('"
            + mgr.translate("DOCMAWDOCISSUEREMVOEPERSONORGROUPINAPP: The person or group exists in approval routing. By removing this person/group you will remove their ability to review the document.")
            + "'))\n");
      appendDirtyJavaScript("          view_access.checked = true;\n");
      appendDirtyJavaScript("    if((!view_access.checked)&&((access_owner.checked)||(edit_access.checked)))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");

      // Added by Terry 20100202
      appendDirtyJavaScript("function checkDownloadAccess(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    access_owner = getField_('ACCESS_OWNER',i);\n");
      appendDirtyJavaScript("    view_access = getField_('VIEW_ACCESS',i);\n");
      appendDirtyJavaScript("    download_access = getField_('DOWNLOAD_ACCESS',i);\n");
      appendDirtyJavaScript("    print_access = getField_('PRINT_ACCESS',i);\n");
      appendDirtyJavaScript("    if(download_access.checked)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      appendDirtyJavaScript("       print_access.checked = true;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if((!download_access.checked)&&(access_owner.checked))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       download_access.checked = true;\n");
      appendDirtyJavaScript("       print_access.checked = true;\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkPrintAccess(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    access_owner = getField_('ACCESS_OWNER',i);\n");
      appendDirtyJavaScript("    view_access = getField_('VIEW_ACCESS',i);\n");
      appendDirtyJavaScript("    download_access = getField_('DOWNLOAD_ACCESS',i);\n");
      appendDirtyJavaScript("    print_access = getField_('PRINT_ACCESS',i);\n");
      appendDirtyJavaScript("    if((!print_access.checked)&&((access_owner.checked)||(download_access.checked)))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       print_access.checked = true;\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if(print_access.checked)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");
      // Added end

      //
      // Association catogoty settings
      //

      appendDirtyJavaScript("function lovCategory(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    last_value =  getValue_('CATEGORY',i);\n");
      appendDirtyJavaScript("    openLOVWindow('CATEGORY',i,\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(root_path);
      appendDirtyJavaScript("common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_REFERENCE_CATEGORY&__FIELD="
            + mgr.URLEncode(mgr
                  .translate("DOCMAWDOCISSUECATEGORY: Association Category"))
            + "'\n");
      appendDirtyJavaScript("        ,500,500,'validateCategory');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateCategory(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if ( last_value != '' )\n");
      appendDirtyJavaScript("       getField_('CATEGORY',i).value = last_value+getValue_('CATEGORY',i);\n");
      appendDirtyJavaScript("    last_value = '';\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if( !checkCategory(i) ) return;\n");
      appendDirtyJavaScript("}\n");

      //
      // History mode changed
      //

      appendDirtyJavaScript("function modeChanged()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.MODE_CHANGED.value = \"TRUE\";\n");
      appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("}\n");

      // Tranfer to EdmMacro.page file

      if (bTranferToEDM) {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }

      // DMPR303 START
      if (bTranferToCreateLink) {
         appendDirtyJavaScript("   var openWindow = true; \n");
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=400,height=625,left=100,top=100\");\n");
      }
      // DMPR303 START

      appendDirtyJavaScript("function lovTempProfileId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("        openLOVWindow('TEMP_PROFILE_ID',i,\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(root_path);
      appendDirtyJavaScript("common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=APPROVAL_PROFILE&__FIELD=Profile+ID'\n");
      appendDirtyJavaScript("        ,500,500,'validateTempProfileId');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateTempProfileId(i)\n");
      appendDirtyJavaScript("{   \n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if( !checkTempProfileId(i) ) return;\n");
      appendDirtyJavaScript("    r = __connect(\n"); // tested
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=TEMP_PROFILE_ID'\n");
      appendDirtyJavaScript("        + '&TEMP_PROFILE_ID=' + getValue_('TEMP_PROFILE_ID',i)\n");
      appendDirtyJavaScript("        );\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'TEMP_PROFILE_ID',i,'Profile ID') )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        assignValue_('TEMP_DESCRIPTION',i,0);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      if (launchFile) {
         appendDirtyJavaScript("sDocumentFolder = oCliMgr.GetDocumentFolder();\n");
         appendDirtyJavaScript(sClientFunction);
      }

      appendDirtyJavaScript("function assignValue_(name,i,pos)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if (name == 'DOC_STATUS')\n");
      appendDirtyJavaScript("{   \n");
      appendDirtyJavaScript("    curr = getField_('DOC_STATUS',i).value \n");
      appendDirtyJavaScript("    cutt = '\\%' \n");
      appendDirtyJavaScript("    if ( curr.indexOf(cutt) == -1)\n");
      appendDirtyJavaScript("    fld.value = curr+cutt;\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    fld.value = curr;\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    fld = getField_(name,i);\n");
      appendDirtyJavaScript("    fld.value = fld.defaultValue = __getValidateValue(pos); \n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n");

      //
      // Row selection
      //

      appendDirtyJavaScript("function selectAll() {\n");
      appendDirtyJavaScript("  for (i=0; i<f.elements.length; i++) {\n");
      appendDirtyJavaScript("     if (f.elements[i].type == \"checkbox\" && f.elements[i].name == \"__SELECTED1\") {\n");
      appendDirtyJavaScript("        f.elements[i].checked = true;\n");
      appendDirtyJavaScript("        try{\n");
      appendDirtyJavaScript("           CCA(f.elements[i]);\n");
      appendDirtyJavaScript("        }catch(err){}\n");
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function deSelectAll() {\n");
      appendDirtyJavaScript("  for (i=0; i<f.elements.length; i++) {\n");
      appendDirtyJavaScript("     if (f.elements[i].type == \"checkbox\" && f.elements[i].name == \"__SELECTED1\") {\n");
      appendDirtyJavaScript("        f.elements[i].checked = false;\n");
      appendDirtyJavaScript("        try{\n");
      appendDirtyJavaScript("           CCA(f.elements[i]);\n");
      appendDirtyJavaScript("        }catch(err){}\n");
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function invertSelection() {\n");
      appendDirtyJavaScript("  for (i=0; i<f.elements.length; i++) {\n");
      appendDirtyJavaScript("     if (f.elements[i].type == \"checkbox\" && f.elements[i].name == \"__SELECTED1\") {\n");
      appendDirtyJavaScript("        f.elements[i].checked = !(f.elements[i].checked);\n");
      appendDirtyJavaScript("        try{\n");
      appendDirtyJavaScript("           CCA(f.elements[i]);\n");
      appendDirtyJavaScript("        }catch(err){}\n");
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");

      if (headlay.isMultirowLayout() && !bCopyProfile) {
         appendDirtyJavaScript("function setSameAction(obj){\n");
         appendDirtyJavaScript("    if (obj.value!=\"APPLY_SAME_TO_ALL\") {\n");
         appendDirtyJavaScript("  document.form.SAME_ACTION_TO_ALL.value=\"NO\";\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("    else{\n");
         appendDirtyJavaScript("  document.form.SAME_ACTION_TO_ALL.value=\"YES\";\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("}\n");

         /*
          * appendDirtyJavaScript(
          * "  if (document.form.sameActionRadio[0].checked==true)\n");
          * appendDirtyJavaScript
          * ("       document.form.SAME_ACTION_TO_ALL.value=\"YES\"\n");
          */

         appendDirtyJavaScript("function setLuanch(obj){\n");
         appendDirtyJavaScript("    if (obj.value!=\"LAUNCH_FILE\") {\n");
         appendDirtyJavaScript("  document.form.LAUNCH_FILE.value=\"NO\";\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("    else{\n");
         appendDirtyJavaScript("  document.form.LAUNCH_FILE.value=\"YES\";\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("}\n");
      }

      appendDirtyJavaScript("function multiRowCopyFileTo()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if (document.form.sameActionRadio[0].checked==true)\n");
      appendDirtyJavaScript("     document.form.SAME_ACTION_TO_ALL.value=\"YES\"\n");
      appendDirtyJavaScript("   document.form.MULTIROWACTION.value = 'setToCopyFile();';");
      appendDirtyJavaScript("  submit();\n");
      appendDirtyJavaScript("}   \n");

      appendDirtyJavaScript("function refreshParentRowsSelected()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" document.form.LEAVE_ROWS_SELECTED.value=\"YES\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      if (bOpenWizardWindow) {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sUrl));

         if (modifySubWindow4NewRev) {
            appendDirtyJavaScript("\",\"anotherWindow\",\"status, resizable, scrollbars, width=727, height=612, left=100, top=100\");\n");
         } else {
            appendDirtyJavaScript("\",\"anotherWindow\",\"status, resizable, scrollbars, width=727, height=500, left=100, top=100\");\n");
         }

      }

      if (bOpenReleaseWizardWindow) {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sUrl));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status, resizable, scrollbars, width=850, height=450, left=100, top=100\");\n");
      }

      if (bShowStructure) {
         appendDirtyJavaScript("window.parent.location = \"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sUrl));
         appendDirtyJavaScript("\";\n");
      }

      appendDirtyJavaScript("function addNewRow(doc_class,doc_no,doc_sheet,doc_rev)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.DOC_CLASS_FROM_WIZ.value=doc_class\n");
      appendDirtyJavaScript(" document.form.DOC_NO_FROM_WIZ.value=doc_no\n");
      appendDirtyJavaScript(" document.form.DOC_SHEET_FROM_WIZ.value=doc_sheet\n");
      appendDirtyJavaScript(" document.form.DOC_REV_FROM_WIZ.value=doc_rev\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      // here we over ride the client method for lovs
      appendDirtyJavaScript("function lovDocNo(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('DOC_NO',i).indexOf('%') !=-1)? getValue_('DOC_NO',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('DOC_NO',i,\n");
      appendDirtyJavaScript("     '"
            + root_path
            + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_TITLE&__FIELD="
            + mgr.URLEncode(mgr
                  .translate("DOCMAWDOCISSUELISTOFVALUEDOCNO: Doc No"))
            + "&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("     + '&__KEY_VALUE=' + URLClientEncode(getValue_('DOC_NO',i))\n");
      appendDirtyJavaScript("     + '&DOC_NO=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("     ,550,500,'validateDocNo');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovDocSheet(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('DOC_SHEET',i).indexOf('%') !=-1)? getValue_('DOC_SHEET',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('DOC_SHEET',i,\n");
      appendDirtyJavaScript("   '"
            + root_path
            + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_ISSUE_LOV1&__FIELD="
            + mgr.URLEncode(mgr
                  .translate("DOCMAWDOCISSUELISTOFVALUEDOCSHEET: Doc Sheet"))
            + "&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('DOC_SHEET',i))\n");
      appendDirtyJavaScript("   + '&DOC_SHEET=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("     + '&DOC_NO=' + URLClientEncode(getValue_('DOC_NO',i))\n");
      appendDirtyJavaScript("   ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovDocRev(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('DOC_REV',i).indexOf('%') !=-1)? getValue_('DOC_REV',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('DOC_REV',i,\n");
      appendDirtyJavaScript("   '"
            + root_path
            + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_ISSUE&__FIELD="
            + mgr.URLEncode(mgr
                  .translate("DOCMAWDOCISSUELISTOFVALUEDOCREV: Doc Rev"))
            + "&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('DOC_REV',i))\n");
      appendDirtyJavaScript("   + '&DOC_REV=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("     + '&DOC_NO=' + URLClientEncode(getValue_('DOC_NO',i))\n");
      appendDirtyJavaScript("     + '&DOC_SHEET=' + URLClientEncode(getValue_('DOC_SHEET',i))\n");
      appendDirtyJavaScript("   ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("}\n");

      // Bug 70553, Start
      appendDirtyJavaScript("function lovFormatSize(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('FORMAT_SIZE',i).indexOf('%') !=-1)? getValue_('FORMAT_SIZE',i):'';\n");
      appendDirtyJavaScript("   var doc_class_ = getValue_('DOC_CLASS',i);\n");
      appendDirtyJavaScript("   if (doc_class_ == \"\" || doc_class_ == null) \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('FORMAT_SIZE',i,\n");
      appendDirtyJavaScript("      '"
            + root_path
            + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_FORMAT&__FIELD="
            + mgr.URLEncode(mgr
                  .translate("DOCMAWDOCISSUELISTOFVALUEFORMAT: Format"))
            + "&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('FORMAT_SIZE',i))\n");
      appendDirtyJavaScript("      + '&FORMAT_SIZE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("      ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('FORMAT_SIZE',i,\n");
      appendDirtyJavaScript("      '"
            + root_path
            + "docmaw/FormatLov.page?__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('FORMAT_SIZE',i))\n");
      appendDirtyJavaScript("      + '&FORMAT_SIZE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("      + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("      ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovReasonForIssue(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('REASON_FOR_ISSUE',i).indexOf('%') !=-1)? getValue_('REASON_FOR_ISSUE',i):'';\n");
      appendDirtyJavaScript("   var doc_class_ = getValue_('DOC_CLASS',i);\n");
      appendDirtyJavaScript("   if (doc_class_ == \"\" || doc_class_ == null) \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('REASON_FOR_ISSUE',i,\n");
      appendDirtyJavaScript("      '"
            + root_path
            + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOCUMENT_REASON_FOR_ISSUE&__FIELD="
            + mgr.URLEncode(mgr
                  .translate("DOCMAWDOCISSUEREASONFORISSUE: Reason For Issue"))
            + "&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('REASON_FOR_ISSUE',i))\n");
      appendDirtyJavaScript("      + '&REASON_FOR_ISSUE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("      ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('REASON_FOR_ISSUE',i,\n");
      appendDirtyJavaScript("      '"
            + root_path
            + "docmaw/ReasonForIssueLov.page?__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('REASON_FOR_ISSUE',i))\n");
      appendDirtyJavaScript("      + '&REASON_FOR_ISSUE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("      + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("      ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      // Bug 70553, End

      appendDirtyJavaScript("function getPageName()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    return \"docissue\"\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function showMultiMenu(call,selboxnam,tblnr,cond)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var t = eval(\"f.\"+selboxnam);\n");
      appendDirtyJavaScript("   var popup_list =  eval(\"popup_list_\"+tblnr);\n");
      appendDirtyJavaScript("   var rows_selected=\"\";\n");
      appendDirtyJavaScript("   var btn_cmds = \"\";                      \n");
      appendDirtyJavaScript("   if(t)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       if(t.length)\n");
      appendDirtyJavaScript("       {   \n");
      appendDirtyJavaScript("           for(i=0;i<t.length;i++)\n");
      appendDirtyJavaScript("           {\n");
      appendDirtyJavaScript("              if (t[i].checked )\n");
      appendDirtyJavaScript("                 rows_selected += \",\"+(i); \n");
      appendDirtyJavaScript("           }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("           if (t.checked)\n");
      appendDirtyJavaScript("              rows_selected = \",0\";\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    if (rows_selected==\"\") {\n");
      appendDirtyJavaScript("       //alert(\"no row selected\");\n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("       var call1 = call.substring(0,call.indexOf('[')+1);\n");
      appendDirtyJavaScript("       var call2 = call.substring(call.indexOf('[')+1,call.indexOf(']'));\n");
      appendDirtyJavaScript("       var call3 = call.substring(call.indexOf(']'),call.length); \n");
      appendDirtyJavaScript("       var lst1 = call2.split(',');\n");
      appendDirtyJavaScript("       var lst2 = rows_selected.split(',');\n");
      appendDirtyJavaScript("       var tf_list = \"\"\n");
      appendDirtyJavaScript("       var list_count = 0;\n");
      appendDirtyJavaScript("       if(lst2.length>1)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("           for (l=0;l<popup_list.length;l++)\n");
      appendDirtyJavaScript("           {\n");
      appendDirtyJavaScript("               if(popup_list[l].length>0)\n");
      appendDirtyJavaScript("               {\n");
      appendDirtyJavaScript("                   list_count = popup_list[l].length;\n");
      appendDirtyJavaScript("                   break;\n");
      appendDirtyJavaScript("               }\n");
      appendDirtyJavaScript("           }\n");
      appendDirtyJavaScript("          for (j=5;j<list_count;j++)\n");
      appendDirtyJavaScript("           {\n");
      appendDirtyJavaScript("               if(cond)\n");
      appendDirtyJavaScript("                 tmp_tf = \"true\";\n");
      appendDirtyJavaScript("               else\n");
      appendDirtyJavaScript("                 tmp_tf = \"false\";\n");
      appendDirtyJavaScript("              for(k=1;k<lst2.length;k++)\n");
      appendDirtyJavaScript("              {\n");
      appendDirtyJavaScript("                  if(lst2[k] != '')\n");
      appendDirtyJavaScript("                  {\n");
      appendDirtyJavaScript("                     if(cond)\n");
      appendDirtyJavaScript("                     {\n");
      appendDirtyJavaScript("                        if(popup_list[lst2[k]]!= '')\n");
      appendDirtyJavaScript("                        {\n");
      appendDirtyJavaScript("                           if(popup_list[lst2[k]][j] == false) \n");
      appendDirtyJavaScript("                              tmp_tf = 'false';\n");
      appendDirtyJavaScript("                        }\n");
      appendDirtyJavaScript("                        else\n");
      appendDirtyJavaScript("                            tmp_tf = lst1[j-1];\n");
      appendDirtyJavaScript("                     }\n");
      appendDirtyJavaScript("                     else\n");
      appendDirtyJavaScript("                     {\n");
      appendDirtyJavaScript("                         if(popup_list[lst2[k]]!= '')\n");
      appendDirtyJavaScript("                         {\n");
      appendDirtyJavaScript("                            if(popup_list[lst2[k]][j] == true) \n");
      appendDirtyJavaScript("                              tmp_tf = 'true';\n");
      appendDirtyJavaScript("                         }\n");
      appendDirtyJavaScript("                         else\n");
      appendDirtyJavaScript("                            tmp_tf = lst1[j-1];\n");
      appendDirtyJavaScript("                     }\n");
      appendDirtyJavaScript("                  }\n");
      appendDirtyJavaScript("              }\n");
      appendDirtyJavaScript("              if(lst1[j-1]=='false')\n");
      appendDirtyJavaScript("                 tmp_tf = 'false';\n");
      appendDirtyJavaScript("              tf_list +=   \",\" + tmp_tf;\n");
      appendDirtyJavaScript("           }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       var lst = call2.split(',');\n");
      appendDirtyJavaScript("       for(j=0;j<4;j++)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          if (j<3)\n");
      appendDirtyJavaScript("            btn_cmds += lst[j]+\",\";\n");
      appendDirtyJavaScript("          else\n");
      appendDirtyJavaScript("            btn_cmds += lst[j];\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       if(tf_list.length>0)\n");
      appendDirtyJavaScript("          call2 = btn_cmds+tf_list;\n");
      appendDirtyJavaScript("       call = call1+call2+call3;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   while(call.indexOf('FALSE') != -1)\n");
      appendDirtyJavaScript("      call = call.replace('FALSE','false');\n");
      appendDirtyJavaScript("   eval(call);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function displayConfirmBoxEx(message)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    var cookie_value = readCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\");\n");
      appendDirtyJavaScript("    removeCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\", COOKIE_PATH);\n");
      appendDirtyJavaScript("    unconfirm = (readCookie(f.__PAGE_ID.value).length>1);\n");
      appendDirtyJavaScript("    onLoad();\n");
      appendDirtyJavaScript("    if (cookie_value ==\"TRUE\" && (!unconfirm)&&(document.form.CONFIRM.value==\"\"))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       if (confirm(message))\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("           setHEADCommand('setApproved');\n");
      appendDirtyJavaScript("           document.form.CONFIRM.value='OK';\n");
      appendDirtyJavaScript("           commandSet('HEAD.Perform', '');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("           document.form.CONFIRM.value='CANCEL';\n");
      appendDirtyJavaScript("           submit();\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("       document.form.CONFIRM.value=\"\";\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function writeConfirmationCookie()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    writeCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\", \"TRUE\", \"\", COOKIE_PATH);\n");
      appendDirtyJavaScript("}\n");

      // Bug Id 57275, Start
      appendDirtyJavaScript("function validateSetApproval()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    document.form.CONFIRM.value=\"\";\n");
      appendDirtyJavaScript("    writeConfirmationCookie();\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");
      // Bug Id 57275, End

      drawPageFooter(out);
      out.append("</p>\n");
      out.append("</form>\n");
      if (mgr.isExplorer())
         out.append(strIFSCliMgrOCX);
      out.append("</body></html>");
      
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
      
      
      return out;
   }

   protected String getImageFieldTag(ASPField imageField, ASPRowSet rowset,
         int rowNum) throws FndException {
      ASPManager mgr = getASPManager();
      String imgSrc = mgr.getASPConfig().getImagesLocation();
      if (rowset.countRows() > 0) {
         if (("VIEW_FILE").equals(imageField.getName())
               || ("HEAD_VIEW_FILE").equals(imageField.getName())) {
            if ("TRUE".equals(rowset.getValueAt(rowNum, "IS_ELE_DOC"))) {
               imgSrc += "folder.gif";
               return "<img src=\"" + imgSrc + "\" height=\"16\" width=\"16\" border=\"0\">";
            } else {
               return "";
            }
         } else if ("ITEM3_VIEW_FILE".equals(imageField.getName())) {
            if ("TRUE".equals(rowset.getValueAt(rowNum, "ITEM3_IS_ELE_DOC"))) {
               imgSrc += "folder.gif";
               return "<img src=\"" + imgSrc + "\" height=\"16\" width=\"16\" border=\"0\">";
            } else {
               return "";
            }
         } else if ("ITEM4_VIEW_FILE".equals(imageField.getName())) {
            if ("TRUE".equals(rowset.getValueAt(rowNum, "ITEM4_IS_ELE_DOC"))) {
               imgSrc += "folder.gif";
               return "<img src=\"" + imgSrc + "\" height=\"16\" width=\"16\" border=\"0\">";
            } else {
               return "";
            }
         }
      }
      return "";
   }
   
   protected abstract void lastMethod();
}
