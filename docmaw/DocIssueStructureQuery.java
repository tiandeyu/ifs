package ifs.docmaw;

import java.util.StringTokenizer;

import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPLog;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;

public class DocIssueStructureQuery extends DocIssueAncestor
{
   protected String nodeAddress = null;

   protected String doc_class;
   protected String doc_no;
   protected String doc_sheet;
   protected String doc_rev;
   
   public DocIssueStructureQuery(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }
   
   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      nodeAddress = ctx.readValue("__NODE_ADDRESS", mgr.readValue("NODE_ADDRESS"));
      
      String doc_class_ = mgr.getQueryStringValue("DOC_CLASS");
      if (Str.isEmpty(doc_class_))
         doc_class_ = mgr.getQueryStringValue("SUB_DOC_CLASS");
      if (Str.isEmpty(doc_class_))
         doc_class_ = mgr.getQueryStringValue("ITEM3_DOC_CLASS");
      if (Str.isEmpty(doc_class_))
         doc_class_ = mgr.getQueryStringValue("ITEM4_DOC_CLASS");
      
      String doc_no_ = mgr.getQueryStringValue("DOC_NO");
      if (Str.isEmpty(doc_no_))
         doc_no_ = mgr.getQueryStringValue("SUB_DOC_NO");
      if (Str.isEmpty(doc_no_))
         doc_no_ = mgr.getQueryStringValue("ITEM3_DOC_NO");
      if (Str.isEmpty(doc_no_))
         doc_no_ = mgr.getQueryStringValue("ITEM4_DOC_NO");
      
      String doc_sheet_ = mgr.getQueryStringValue("DOC_SHEET");
      if (Str.isEmpty(doc_sheet_))
         doc_sheet_ = mgr.getQueryStringValue("SUB_DOC_SHEET");
      if (Str.isEmpty(doc_sheet_))
         doc_sheet_ = mgr.getQueryStringValue("ITEM3_DOC_SHEET");
      if (Str.isEmpty(doc_sheet_))
         doc_sheet_ = mgr.getQueryStringValue("ITEM4_DOC_SHEET");
      
      String doc_rev_ = mgr.getQueryStringValue("DOC_REV");
      if (Str.isEmpty(doc_rev_))
         doc_rev_ = mgr.getQueryStringValue("SUB_DOC_REV");
      if (Str.isEmpty(doc_rev_))
         doc_rev_ = mgr.getQueryStringValue("ITEM3_DOC_REV");
      if (Str.isEmpty(doc_rev_))
         doc_rev_ = mgr.getQueryStringValue("ITEM4_DOC_REV");
      
      doc_class = ctx.readValue("__DOC_CLASS", doc_class_);
      doc_no = ctx.readValue("__DOC_NO", doc_no_);
      doc_sheet = ctx.readValue("__DOC_SHEET", doc_sheet_);
      doc_rev = ctx.readValue("__DOC_REV", doc_rev_);
      
      show_in_navigator = "TRUE";
      
      super.run();
      
      if( !Str.isEmpty(nodeAddress) )
         ctx.writeValue("__NODE_ADDRESS", nodeAddress);
      
      if( !Str.isEmpty(doc_class) )
         ctx.writeValue("__DOC_CLASS", doc_class);
      if( !Str.isEmpty(doc_no) )
         ctx.writeValue("__DOC_NO", doc_no);
      if( !Str.isEmpty(doc_sheet) )
         ctx.writeValue("__DOC_SHEET", doc_sheet);
      if( !Str.isEmpty(doc_rev) )
         ctx.writeValue("__DOC_REV", doc_rev);
   }
   
   public void okFind()
   {
      runQuery();
   }
   
   public void runQuery()
   {
      ASPManager mgr = getASPManager();
      ASPLog log = mgr.getASPLog();
      searchURL = mgr.createSearchURL(headblk);

      trans.clear();
      q = trans.addQuery(headblk);
      
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());

      if (!Str.isEmpty(doc_class) && !Str.isEmpty(doc_no) && !Str.isEmpty(doc_sheet) && !Str.isEmpty(doc_rev))
      {
         q.addWhereCondition("DOC_CLASS = '" + doc_class + "'");
         q.addWhereCondition("DOC_NO = '" + doc_no + "'");
         q.addWhereCondition("DOC_SHEET = '" + doc_sheet + "'");
         q.addWhereCondition("DOC_REV = '" + doc_rev + "'");
      }
      
      // 1. Document scope
      q.addWhereCondition("DOC_CLASS IN " + DocmawConstants.getLibarayDocClasses() + " ");
      
      q.addWhereCondition("OBJSTATE != 'Obsolete'");
      
      // 2. Tree conditions
      if (!Str.isEmpty(nodeAddress))
         q.addWhereCondition(nodeAddress);

      // 3. Data isolation
      String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
      StringBuffer sb = new StringBuffer("(");
      if (!"()".equals(tempPersonZones)) {
         sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
      } else {
         sb.append("(1=1)");
      }
      sb.append(")");
      q.addWhereCondition(sb.toString());

      // Order by
      q.setOrderByClause("DT_CRE DESC");
      q.includeMeta("ALL");

      mgr.querySubmit(trans, headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENODATA: No data found."));
         eval(headset.syncItemSets());
         return;
      }
      else
         okFindITEM2();

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
   }
   
   public void countFind()
   {
      ASPManager mgr = getASPManager();
      boolean bValueExists = true;
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      
      if (!Str.isEmpty(doc_class) && !Str.isEmpty(doc_no) && !Str.isEmpty(doc_sheet) && !Str.isEmpty(doc_rev))
      {
         q.addWhereCondition("DOC_CLASS = '" + doc_class + "'");
         q.addWhereCondition("DOC_NO = '" + doc_no + "'");
         q.addWhereCondition("DOC_SHEET = '" + doc_sheet + "'");
         q.addWhereCondition("DOC_REV = '" + doc_rev + "'");
      }
      
      // 1. Document scope
      q.addWhereCondition("DOC_CLASS IN " + DocmawConstants.getLibarayDocClasses() + " ");
      
      q.addWhereCondition("OBJSTATE != 'Obsolete'");
      
      // 2. Tree conditions
      if (!Str.isEmpty(nodeAddress))
         q.addWhereCondition(nodeAddress);

      // 3. Data isolation
      String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
      StringBuffer sb = new StringBuffer("(");
      if (!"()".equals(tempPersonZones)) {
         sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
      } else {
         sb.append("(1=1)");
      }
      sb.append(")");
      q.addWhereCondition(sb.toString());
      
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }
   
   protected String getTitle()
   {
      return "DOCISSUESTRUCTUREQUERYTITLE: Doc Issue Structure Query";
   }
   
   
   protected void customizeHeadlay()
   {
      ASPManager mgr = getASPManager();
      
      mgr.getASPField("ATTACH_TYPE").setLOVProperty("WHERE", "COMP_DOC='TRUE'");
      mgr.getASPField("SUB_CLASS").setLOVProperty("WHERE", "Doc_Class_API.Get_Comp_Doc(DOC_CLASS) = 'TRUE'");
      
      String group1 = "REV_TITLE,TRANSLATE_TITLE,DOC_CODE,INNER_DOC_CODE,TRANSFER_NO,DOC_STATE,DOC_REV,DOC_CLASS,DOC_CLASS_NAME,SUB_CLASS,SUB_CLASS_NAME,HEAD_VIEW_FILE";
      String group2 = "PROJ_NO,PROJ_NAME,MACH_GRP_NO,MACH_GRP_DESC,SPECIALTY_NO,SPECIALTY_DESC,DOC_LOC_NO,COMPONENT_TYPE,COMPONENT_TYPE_DESC,COMPONENT_NO," +
      "ROOM_NO,ROOM_DESC,GRADE_NO,GRADE_DESC,LOT_NO,LOT_DESC,WBS_NO,PROFESSION_NO,PROFESSION_NAME," +
      "PURPOSE_NO,PURPOSE_NAME,MEETING_TYPE,MEETING_TYPE_NAME," +
      "COPIES,PAGES,ATTACH_PAGES,RECEIPT,RECEIPT_REQUEST,BOOKLET_NO,BOOKLET_NAME,PAGE_SIZE,SIZE_NAME,SEC_LEVEL,LEVEL_NAME,FOR_USE_NOTICE,FOR_USE,FOR_USE_DATE," +
      "SEND_UNIT_NO,SEND_UNIT_NAME,SEND_DEPT,SEND_DEPT_DESC,SIGN_PERSON,SIGN_PERSON_NAME,COMPLETE_DATE,MAIN_SEND,CO_SEND,INNER_SEND,EXTAND_DOC_CODE1," +
      "SEND_DATE,RECEIVE_DATE,RECEIVE_UNIT_NO,RECEIVE_UNIT_NAME,USER_CREATED,USER_CREATED_NAME,DT_CRE," +
      "OBJID,OBJVERSION,OBJSTATE,OBJEVENTS,REV_NO,ALTDOCNO,LU_NAME,KEY_REF,DOC_CLASS,DOC_NO,DOC_SHEET,STATE";

      String overview_group1 = "HEAD_VIEW_FILE,REV_TITLE,TRANSLATE_TITLE,DOC_CODE,INNER_DOC_CODE,TRANSFER_NO,DOC_STATE,DOC_REV,DOC_CLASS,DOC_CLASS_NAME,SUB_CLASS,SUB_CLASS_NAME";
      String overview_group2 = group2;
      
      headlay.defineGroup("Main", group1, false, true);
      
      headlay.defineGroup(mgr.translate("DOCISSUESTRUCTUREQUERYDOCINFOGROUP: Document Info"), group2, true, false);
      
      headlay.setFieldOrder(overview_group1 + "," + overview_group2);
      
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("TRANSLATE_TITLE", 5);
   }
   
   /*protected void customizeTabs()
   {
      ASPManager mgr = getASPManager();
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEOBJECTS: Objects"), "javascript:commandSet('HEAD.activateObjects','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUECONSISTS: Consists Of"), "javascript:commandSet('HEAD.activateConsistsOf','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEWHEREUSED: Where Used"), "javascript:commandSet('HEAD.activateWhereUsed','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEACCESS: Access"), "javascript:commandSet('HEAD.activateAccess','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEHISTORY: History"), "javascript:commandSet('HEAD.activateHistory','')");
      tabs.addTab("FILE_REF", mgr.translate("DOCMAWDOCISSUEFILEREFERENCES: File Refs."), "javascript:commandSet('HEAD.activateFileReferences','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEHISTORYREVISION: History Revison"), "javascript:commandSet('HEAD.activateHistoryRevision','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUETARGETORG: Target Org"), "javascript:commandSet('HEAD.activateSendDept','')");
   }*/
   
   /*public void activateSendDept()
   {
      tabs.setActiveTab(8);
      okFindITEM11();
   }*/
   
   protected void customizeHeadbar()
   {
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.OVERVIEWEDIT);
      headbar.disableCommand("setObsolete");
      headbar.disableCommand("checkInDocument");
      headbar.disableCommand("viewOriginal");
      headbar.disableCommand("editDocument");
      headbar.disableCommand("checkInSelectDocument");
      headbar.disableCommand("undoCheckOut");
      headbar.disableCommand("printDocument");
      headbar.disableCommand("deleteDocument");
      headbar.disableCommand("deleteDocumentFile");
      headbar.disableCommand("deleteSelectDocFile");
      headbar.disableCommand("setToCopyFile");
      headbar.disableCommand("userSettings");
      headbar.disableCommand("CreateDocConnection");
      headbar.disableCommand("CreateDocAttachment");
      headbar.disableCommand("CreateUnit");
      headbar.disableCommand("setStructAttribAll");
      headbar.disableCommand("unsetStructAttribAll");
      headbar.disableCommand("createNewRevision");
//      headbar.disableCommand("createNewSheet");
//      headbar.disableCommand("mandatorySettings");
      headbar.disableCommand("sign");
      
      itembar2.disableCommand(ASPCommandBar.NEWROW);
      itembar2.disableCommand(ASPCommandBar.DUPLICATEROW);
      itembar2.disableCommand(ASPCommandBar.DELETE);
      itembar2.disableCommand(ASPCommandBar.EDITROW);
      itembar2.disableCommand(ASPCommandBar.OVERVIEWEDIT);
      
      itembar3.disableCommand(ASPCommandBar.NEWROW);
      itembar3.disableCommand(ASPCommandBar.DUPLICATEROW);
      itembar3.disableCommand(ASPCommandBar.DELETE);
      itembar3.disableCommand(ASPCommandBar.EDITROW);
      itembar3.disableCommand(ASPCommandBar.OVERVIEWEDIT);
      
      itembar4.disableCommand(ASPCommandBar.NEWROW);
      itembar4.disableCommand(ASPCommandBar.DUPLICATEROW);
      itembar4.disableCommand(ASPCommandBar.DELETE);
      itembar4.disableCommand(ASPCommandBar.EDITROW);
      itembar4.disableCommand(ASPCommandBar.OVERVIEWEDIT);
      
      itembar6.disableCommand(ASPCommandBar.NEWROW);
      itembar6.disableCommand(ASPCommandBar.DUPLICATEROW);
      itembar6.disableCommand(ASPCommandBar.DELETE);
      itembar6.disableCommand(ASPCommandBar.EDITROW);
      itembar6.disableCommand(ASPCommandBar.OVERVIEWEDIT);
      
      itembar7.disableCommand(ASPCommandBar.NEWROW);
      itembar7.disableCommand(ASPCommandBar.DUPLICATEROW);
      itembar7.disableCommand(ASPCommandBar.DELETE);
      itembar7.disableCommand(ASPCommandBar.EDITROW);
      itembar7.disableCommand(ASPCommandBar.OVERVIEWEDIT);
      
      itembar9.disableCommand(ASPCommandBar.NEWROW);
      itembar9.disableCommand(ASPCommandBar.DUPLICATEROW);
      itembar9.disableCommand(ASPCommandBar.DELETE);
      itembar9.disableCommand(ASPCommandBar.EDITROW);
      itembar9.disableCommand(ASPCommandBar.OVERVIEWEDIT);
      
      doc_issue_callback_bar.disableCommand(ASPCommandBar.NEWROW);
      doc_issue_callback_bar.disableCommand(ASPCommandBar.DUPLICATEROW);
      doc_issue_callback_bar.disableCommand(ASPCommandBar.DELETE);
      doc_issue_callback_bar.disableCommand(ASPCommandBar.EDITROW);
      doc_issue_callback_bar.disableCommand(ASPCommandBar.OVERVIEWEDIT);
   }

   private void changeFieldVisibleByDocClass(String doc_class)
   {
      ASPManager mgr = getASPManager();
      String sub_class = "";
      if (headset.countRows() > 0)
         sub_class = headset.getValue("SUB_CLASS");
      
      // LOV fields
      mgr.getASPField("DOC_CLASS").unsetHidden();
      mgr.getASPField("SUB_CLASS").unsetHidden();
      mgr.getASPField("PROJ_NO").unsetHidden();
      mgr.getASPField("SEND_UNIT_NO").unsetHidden();
      mgr.getASPField("SIGN_PERSON").unsetHidden();
      mgr.getASPField("USER_CREATED").unsetHidden();
      
      // Non-LOV fields
      mgr.getASPField("REV_TITLE").unsetHidden();
      mgr.getASPField("DOC_CODE").unsetHidden();
      mgr.getASPField("TRANSFER_NO").unsetHidden();
      mgr.getASPField("HEAD_VIEW_FILE").unsetHidden();
      mgr.getASPField("PAGES").unsetHidden();
      mgr.getASPField("COMPLETE_DATE").unsetHidden();
      mgr.getASPField("DT_CRE").unsetHidden();
      
      if (DocmawConstants.PROJ_RECEIVE.equals(doc_class))
      {
         if (DocmawConstants.PROJ_RECEIVE_FAX.equals(sub_class) || DocmawConstants.PROJ_RECEIVE_MEETING.equals(sub_class))
         {
            mgr.getASPField("PURPOSE_NO").unsetHidden();
            if (DocmawConstants.PROJ_RECEIVE_MEETING.equals(sub_class))
            {
               mgr.getASPField("MEETING_TYPE").unsetHidden();
               mgr.getASPField("TRANSFER_NO").setLabel("DOCISSUERECEIVEANCESTORINNERMEETINGCODE: Inner Meeting Code");
            }
            else
            {
               mgr.getASPField("EXTAND_DOC_CODE1").unsetHidden();
            }
         }
         
         mgr.getASPField("INNER_DOC_CODE").unsetHidden().setLabel("DOCISSUESTRUCTUREQUERYINNERDOCCODE: Receive Trans Id");
         mgr.getASPField("RECEIVE_DATE").unsetHidden();
         mgr.getASPField("ATTACH_PAGES").unsetHidden();
         mgr.getASPField("RECEIPT").unsetHidden();
         mgr.getASPField("RECEIPT_REQUEST").unsetHidden();
         mgr.getASPField("MAIN_SEND").unsetHidden();
         mgr.getASPField("CO_SEND").unsetHidden();
         mgr.getASPField("INNER_SEND").unsetHidden();
      }
      else if (DocmawConstants.PROJ_SEND.equals(doc_class))
      {
         if (DocmawConstants.PROJ_SEND_FAX.equals(sub_class) || DocmawConstants.PROJ_SEND_MEETING.equals(sub_class))
         {
            mgr.getASPField("PURPOSE_NO").unsetHidden();
            if (DocmawConstants.PROJ_RECEIVE_MEETING.equals(sub_class))
            {
               mgr.getASPField("MEETING_TYPE").unsetHidden();
               mgr.getASPField("TRANSFER_NO").setLabel("DOCISSUERECEIVEANCESTORINNERMEETINGCODE: Inner Meeting Code");
            }
         }
         
         mgr.getASPField("SEND_DATE").unsetHidden();
         mgr.getASPField("ATTACH_PAGES").unsetHidden();
         mgr.getASPField("RECEIPT").unsetHidden();
         mgr.getASPField("RECEIPT_REQUEST").unsetHidden();
         // mgr.getASPField("MAIN_SEND").unsetHidden();
         if (DocmawConstants.PROJ_SEND_MEMO.equals(sub_class))
            mgr.getASPField("MAIN_SEND").unsetHidden();
         else
            mgr.getASPField("RECEIVE_UNIT_NO").unsetHidden();
         mgr.getASPField("CO_SEND").unsetHidden();
         mgr.getASPField("INNER_SEND").unsetHidden();
         mgr.getASPField("SEND_DEPT").unsetHidden();
      }
      else if (DocmawConstants.PROJ_PROPHASE.equals(doc_class))
      {
         mgr.getASPField("DOC_CODE").setLabel("DOCISSUESTRUCTUREQUERYCONSDOCCODE: Doc Code");
         mgr.getASPField("TRANSFER_NO").setLabel("DOCISSUESTRUCTUREQUERYCONSTRANSNO: Transfer No");
         mgr.getASPField("DOC_REV").unsetHidden();
         mgr.getASPField("DOC_STATE").unsetHidden();
         mgr.getASPField("TRANSLATE_TITLE").unsetHidden();
         mgr.getASPField("MACH_GRP_NO").unsetHidden();
         mgr.getASPField("TRANSLATE_TITLE").unsetHidden();
         mgr.getASPField("SPECIALTY_NO").unsetHidden();
         mgr.getASPField("DOC_LOC_NO").unsetHidden();
         mgr.getASPField("WBS_NO").unsetHidden();
         mgr.getASPField("SEC_LEVEL").unsetHidden();
      }
      else if (DocmawConstants.PROJ_CONSTRUCT.equals(doc_class))
      {
         mgr.getASPField("DOC_CODE").setLabel("DOCISSUESTRUCTUREQUERYCONSDOCCODE: Doc Code");
         mgr.getASPField("TRANSFER_NO").setLabel("DOCISSUESTRUCTUREQUERYCONSTRANSNO: Transfer No");
         
         mgr.getASPField("DOC_STATE").unsetHidden();
         mgr.getASPField("MACH_GRP_NO").unsetHidden();
         mgr.getASPField("DOC_LOC_NO").unsetHidden().setLabel("DOCISSUESTRUCTUREQUERYCONSDOCLOCNO: Doc Loc No");
         mgr.getASPField("SPECIALTY_NO").unsetHidden();
         mgr.getASPField("ROOM_NO").unsetHidden();
         mgr.getASPField("LOT_NO").unsetHidden();
         mgr.getASPField("GRADE_NO").unsetHidden();
         mgr.getASPField("DOC_REV").unsetHidden();
         mgr.getASPField("SEND_DEPT").unsetHidden();
         mgr.getASPField("SEND_DATE").unsetHidden();
         mgr.getASPField("RECEIVE_UNIT_NO").unsetHidden();
      }
      else if (DocmawConstants.PROJ_DESIGN.equals(doc_class))
      {
         mgr.getASPField("DOC_CODE").setLabel("DOCISSUESTRUCTUREQUERYDESIDOCCODE: Doc Code");
         mgr.getASPField("TRANSFER_NO").setLabel("DOCISSUESTRUCTUREQUERYDESITRANSFERNO: Transfer No");
         mgr.getASPField("REV_TITLE").setLabel("DOCISSUESTRUCTUREQUERYDESIREVTITLE: Rev Title");
         
         mgr.getASPField("TRANSLATE_TITLE").unsetHidden();
         mgr.getASPField("DOC_STATE").unsetHidden();
         mgr.getASPField("INNER_DOC_CODE").unsetHidden();
         mgr.getASPField("DOC_REV").unsetHidden();
         mgr.getASPField("MACH_GRP_NO").unsetHidden();
         mgr.getASPField("SPECIALTY_NO").unsetHidden();
         mgr.getASPField("DOC_LOC_NO").unsetHidden();
         mgr.getASPField("WBS_NO").unsetHidden();
         mgr.getASPField("PROFESSION_NO").unsetHidden();
         mgr.getASPField("COPIES").unsetHidden();
         mgr.getASPField("BOOKLET_NO").unsetHidden();
         mgr.getASPField("PAGE_SIZE").unsetHidden();
         mgr.getASPField("SEC_LEVEL").unsetHidden();
         mgr.getASPField("FOR_USE_NOTICE").unsetHidden();
         mgr.getASPField("FOR_USE").unsetHidden();
         mgr.getASPField("FOR_USE_DATE").unsetHidden();
      }
      else if (DocmawConstants.PROJ_EQUIPMENT.equals(doc_class))
      {
         mgr.getASPField("DOC_CODE").setLabel("DOCISSUESTRUCTUREQUERYEQUIDOCCODE: Doc Code");
         mgr.getASPField("TRANSFER_NO").setLabel("DOCISSUESTRUCTUREQUERYEQUITRANSFERNO: Transfer No");
         mgr.getASPField("REV_TITLE").setLabel("DOCISSUESTRUCTUREQUERYEQUIREVTITLE: Rev Title");
         
         mgr.getASPField("TRANSLATE_TITLE").unsetHidden();
         mgr.getASPField("DOC_STATE").unsetHidden();
         mgr.getASPField("INNER_DOC_CODE").unsetHidden();
         mgr.getASPField("DOC_REV").unsetHidden();
         mgr.getASPField("MACH_GRP_NO").unsetHidden();
         mgr.getASPField("SPECIALTY_NO").unsetHidden();
         mgr.getASPField("COMPONENT_TYPE").unsetHidden();
         mgr.getASPField("DOC_LOC_NO").unsetHidden();
         mgr.getASPField("COMPONENT_NO").unsetHidden();
         mgr.getASPField("LOT_NO").unsetHidden();
         mgr.getASPField("WBS_NO").unsetHidden();
         mgr.getASPField("PROFESSION_NO").unsetHidden();
         mgr.getASPField("COPIES").unsetHidden();
         mgr.getASPField("BOOKLET_NO").unsetHidden();
         mgr.getASPField("PAGE_SIZE").unsetHidden();
         mgr.getASPField("SEC_LEVEL").unsetHidden();
      }
      else if (DocmawConstants.PROJ_STANDARD.equals(doc_class))
      {
         mgr.getASPField("DOC_CODE").setLabel("DOCISSUESTRUCTUREQUERYCONSDOCCODE: Doc Code");
         mgr.getASPField("TRANSFER_NO").setLabel("DOCISSUESTRUCTUREQUERYCONSTRANSNO: Transfer No");
         mgr.getASPField("TRANSLATE_TITLE").unsetHidden();
         mgr.getASPField("DOC_REV").unsetHidden();
         mgr.getASPField("SPECIALTY_NO").unsetHidden();
         mgr.getASPField("SEC_LEVEL").unsetHidden();
      }
      else
      {
         mgr.getASPField("TRANSLATE_TITLE").unsetHidden();
         mgr.getASPField("INNER_DOC_CODE").unsetHidden();
         mgr.getASPField("DOC_STATE").unsetHidden();
         mgr.getASPField("DOC_REV").unsetHidden();
         mgr.getASPField("MACH_GRP_NO").unsetHidden();
         mgr.getASPField("SPECIALTY_NO").unsetHidden();
         mgr.getASPField("DOC_LOC_NO").unsetHidden();
         mgr.getASPField("COMPONENT_TYPE").unsetHidden();
         mgr.getASPField("COMPONENT_NO").unsetHidden();
         mgr.getASPField("ROOM_NO").unsetHidden();
         mgr.getASPField("GRADE_NO").unsetHidden();
         mgr.getASPField("LOT_NO").unsetHidden();
         mgr.getASPField("WBS_NO").unsetHidden();
         mgr.getASPField("PROFESSION_NO").unsetHidden();
         mgr.getASPField("PURPOSE_NO").unsetHidden();
         mgr.getASPField("MEETING_TYPE").unsetHidden();
         mgr.getASPField("COPIES").unsetHidden();
         mgr.getASPField("ATTACH_PAGES").unsetHidden();
         mgr.getASPField("RECEIPT").unsetHidden();
         mgr.getASPField("RECEIPT_REQUEST").unsetHidden();
         mgr.getASPField("BOOKLET_NO").unsetHidden();
         mgr.getASPField("PAGE_SIZE").unsetHidden();
         mgr.getASPField("SEC_LEVEL").unsetHidden();
         mgr.getASPField("MAIN_SEND").unsetHidden();
         mgr.getASPField("CO_SEND").unsetHidden();
         mgr.getASPField("INNER_SEND").unsetHidden();
         mgr.getASPField("EXTAND_DOC_CODE1").unsetHidden();
         mgr.getASPField("SEND_DATE").unsetHidden();
         mgr.getASPField("RECEIVE_DATE").unsetHidden();
         mgr.getASPField("RECEIVE_UNIT_NO").unsetHidden();
         mgr.getASPField("SEND_DEPT").unsetHidden();
         mgr.getASPField("FOR_USE_NOTICE").unsetHidden();
         mgr.getASPField("FOR_USE").unsetHidden();
         mgr.getASPField("FOR_USE_DATE").unsetHidden();
      }
      displayFuncFields(doc_class);
   }
   
   private void changeFieldVisibleSingle()
   {
      changeFieldVisibleByDocClass(headset.getValue("DOC_CLASS")); 
   }
   
   public void changeFieldVisibleMulitiple()
   {
      String doc_class = getHeadsetDocClass();
      changeFieldVisibleByDocClass(doc_class);
   }
   
   public void changeFieldVisibleFind()
   {
      changeFieldVisibleByDocClass("");
   }
   
   public void displayFuncFields(String doc_class)
   {
      ASPManager mgr = getASPManager();
      if(headlay.isFindLayout() || headlay.isSingleLayout() || headlay.isEditLayout() || headlay.isNewLayout() || headlay.isCustomLayout())
      {
         // unsetHidden general function fields
         mgr.getASPField("DOC_CLASS_NAME").unsetHidden();
         mgr.getASPField("SUB_CLASS_NAME").unsetHidden();
         mgr.getASPField("PROJ_NAME").unsetHidden();
         mgr.getASPField("SEND_UNIT_NAME").unsetHidden();
         mgr.getASPField("SIGN_PERSON_NAME").unsetHidden();
         mgr.getASPField("USER_CREATED_NAME").unsetHidden();
         
         String sub_class = "";
         if (headset.countRows() > 0)
            sub_class = headset.getValue("SUB_CLASS");
         
         //unsetHidden function field, case doc class
         if (Str.isEmpty(doc_class))
         {
            // Display all function fields
            mgr.getASPField("MACH_GRP_DESC").unsetHidden();
            mgr.getASPField("SPECIALTY_DESC").unsetHidden();
            mgr.getASPField("COMPONENT_TYPE_DESC").unsetHidden();
            mgr.getASPField("ROOM_DESC").unsetHidden();
            mgr.getASPField("GRADE_DESC").unsetHidden();
            mgr.getASPField("PROFESSION_NAME").unsetHidden();
            mgr.getASPField("LOT_DESC").unsetHidden();
            mgr.getASPField("PURPOSE_NAME").unsetHidden();
            mgr.getASPField("MEETING_TYPE_NAME").unsetHidden();
            mgr.getASPField("BOOKLET_NAME").unsetHidden();
            mgr.getASPField("SIZE_NAME").unsetHidden();
            mgr.getASPField("LEVEL_NAME").unsetHidden();
            mgr.getASPField("RECEIVE_UNIT_NAME").unsetHidden();
            mgr.getASPField("SEND_DEPT_DESC").unsetHidden();
         }
         else if (DocmawConstants.PROJ_RECEIVE.equals(doc_class) || DocmawConstants.PROJ_SEND.equals(doc_class))
         {
            if (DocmawConstants.PROJ_SEND.equals(doc_class))
            {
               if (!DocmawConstants.PROJ_SEND_MEMO.equals(sub_class))
                  mgr.getASPField("RECEIVE_UNIT_NAME").unsetHidden();
               mgr.getASPField("SEND_DEPT_DESC").unsetHidden();
            }
            // Project receive
            if (DocmawConstants.PROJ_RECEIVE_FAX.equals(sub_class) || DocmawConstants.PROJ_RECEIVE_MEETING.equals(sub_class) ||
                DocmawConstants.PROJ_SEND_FAX.equals(sub_class) || DocmawConstants.PROJ_SEND_MEETING.equals(sub_class))
            {
               mgr.getASPField("PURPOSE_NAME").unsetHidden();
               if (DocmawConstants.PROJ_RECEIVE_MEETING.equals(sub_class))
               {
                  mgr.getASPField("MEETING_TYPE_NAME").unsetHidden();
               }
            }
               
         }
         else if (DocmawConstants.PROJ_CONSTRUCT.equals(doc_class))
         {
            mgr.getASPField("MACH_GRP_DESC").unsetHidden();
            mgr.getASPField("SPECIALTY_DESC").unsetHidden();
            mgr.getASPField("ROOM_DESC").unsetHidden();
            mgr.getASPField("GRADE_DESC").unsetHidden();
            mgr.getASPField("LOT_DESC").unsetHidden();
            mgr.getASPField("RECEIVE_UNIT_NAME").unsetHidden();
            mgr.getASPField("SEND_DEPT_DESC").unsetHidden();
         }
         else if (DocmawConstants.PROJ_DESIGN.equals(doc_class))
         {
            mgr.getASPField("MACH_GRP_DESC").unsetHidden();
            mgr.getASPField("SPECIALTY_DESC").unsetHidden();
            mgr.getASPField("PROFESSION_NAME").unsetHidden();
            mgr.getASPField("BOOKLET_NAME").unsetHidden();
            mgr.getASPField("SIZE_NAME").unsetHidden();
            mgr.getASPField("LEVEL_NAME").unsetHidden();
         }
         else if (DocmawConstants.PROJ_EQUIPMENT.equals(doc_class))
         {
            mgr.getASPField("MACH_GRP_DESC").unsetHidden();
            mgr.getASPField("SPECIALTY_DESC").unsetHidden();
            mgr.getASPField("PROFESSION_NAME").unsetHidden();
            mgr.getASPField("COMPONENT_TYPE_DESC").unsetHidden();
            mgr.getASPField("LOT_DESC").unsetHidden();
            mgr.getASPField("BOOKLET_NAME").unsetHidden();
            mgr.getASPField("SIZE_NAME").unsetHidden();
            mgr.getASPField("LEVEL_NAME").unsetHidden();
         }
         else if (DocmawConstants.PROJ_STANDARD.equals(doc_class))
         {
            mgr.getASPField("SPECIALTY_DESC").unsetHidden();
            mgr.getASPField("LEVEL_NAME").unsetHidden();
         }
         else if (DocmawConstants.PROJ_PROPHASE.equals(doc_class))
         {
            mgr.getASPField("MACH_GRP_DESC").unsetHidden();
            mgr.getASPField("SPECIALTY_DESC").unsetHidden();
            mgr.getASPField("LEVEL_NAME").unsetHidden();
         }
      }
   }

   public void adjust() throws FndException
   {
      ASPManager mgr = getASPManager();
      super.adjust();
      
      if (!mgr.isEmpty(mgr.getQueryStringValue("NODE_ADDRESS")))
      {
         headlay.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      }
      
      // Disable find command when transfered doc
      if (!Str.isEmpty(doc_class) && !Str.isEmpty(doc_no) && !Str.isEmpty(doc_sheet) && !Str.isEmpty(doc_rev))
      {
         headbar.disableCommand(headbar.FIND);
      }
      
      //
      // When headset has no rows
      //
      if (headset.countRows() <= 0)
      {
         headset.clear();
         headlay.setLayoutMode(ASPBlockLayout.FIND_LAYOUT);
      }
      
      //
      // Field visibility depending on layout
      //
      if (headlay.isFindLayout())
      {
         changeFieldVisibleFind();
         mgr.getASPField("HEAD_VIEW_FILE").setHidden();
         /*
         mgr.getASPField("DOC_CLASS").setHidden();
         mgr.getASPField("DOC_CLASS_NAME").setHidden();
         mgr.getASPField("SUB_CLASS").setHidden();
         mgr.getASPField("SUB_CLASS_NAME").setHidden();
         */
      }
      else if (headlay.isMultirowLayout())
      {
         changeFieldVisibleMulitiple();
      }
      else if (headlay.isSingleLayout() )
      {
         changeFieldVisibleSingle();
      }
      else if (headlay.isEditLayout() )
      {
         String docClass = headset.getValue("DOC_CLASS");
         changeFieldVisibleByDocClass(docClass);
      }
      
      //
      //tabs change here.
      //
      /*if (headlay.isSingleLayout() && headset.countRows() > 0)
      {  
         tabs.setTabRemoved(7, true);
         tabs.setTabRemoved(8, true);
         
         String docClass = headset.getValue("DOC_CLASS");
         if(DocmawConstants.PROJ_SEND.equals(docClass))
         {
            tabs.setTabRemoved(8, false);
         }
         else if (DocmawConstants.PROJ_DESIGN.equals(docClass) || DocmawConstants.PROJ_EQUIPMENT.equals(docClass))
         {
            tabs.setTabRemoved(7, false);
         }
      }*/
      
      //
      //change command here.
      //
      if (headset.countRows() == 0)
      { 
         // If no data in the master then remove unwanted buttons
         if (( headlay.getLayoutMode() == 0 )|| ( headlay.isSingleLayout() ))
         {
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.FORWARD);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand(headbar.BACK);
         }
      }
      
      if (headlay.getLayoutMode() == ASPBlockLayout.SINGLE_LAYOUT || headlay.getLayoutMode() == ASPBlockLayout.NONE)
      {
         if (headset.countRows() == 0)
         {
            headlay.setLayoutMode(headlay.NONE);
            headbar.disableCommand(headbar.OKFIND);
            headbar.disableCommand(headbar.COUNTFIND);
            headbar.disableCommand(headbar.SAVERETURN);
            headbar.disableCommand(headbar.SAVENEW);
            headbar.disableCommand(headbar.CANCELNEW);
            headbar.disableCommand(headbar.CANCELEDIT);
            headbar.disableCommand(headbar.BACK);
         }
         else
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }
      
      customizeHeadbar();
   }

   public String getHeadsetDocClass()
   {
      String first_doc_class = "";
      int headset_count = headset.countRows();
      int curr_row_number = headset.getCurrentRowNo();
      if (headset_count > 0)
      {
         headset.first();
         first_doc_class = headset.getRow().getValue("DOC_CLASS");
         
         if (headset_count > 1)
         {
            headset.next();
            do
            {
               if (!first_doc_class.equals(headset.getRow().getValue("DOC_CLASS")))
               {
                  first_doc_class = "";
                  break;
               }
            }
            while (headset.next());
         }
      }
      headset.goTo(curr_row_number);
      return first_doc_class;
   }

   @Override
   protected String getCurrentPageDocClass()
   {
      if (headset != null && headset.countRows() > 0)
         return headset.getValue("DOC_CLASS");

      return "";
   }

   @Override
   protected void lastMethod() {
      // TODO Auto-generated method stub
   }
}
