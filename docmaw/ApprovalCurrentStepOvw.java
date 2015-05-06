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
*  File        : ApprovalCurrentStepOvw.java
*  Modified    :
*
*
*    2001-08-07    Shamal    Call ID 65589 - Corrected the transfer path of EngPartRevisionOvw.page.
*    2002-12-26    Dikalk    2002-2 SP3 Merge: "Bug 31903 2002-09-19 -  Larelk   Increased the field length in person_id."
*    2004-12-06    DiKalk    Merged bug 46184
*    2005-03-23    SukMlk    Merged Bug 49753
*    2005-08-01    SHTHLK    Merged bug Id 52098 - Added new field Description(Approval Description).
*    2006-03-15    CHODLK    Merged Bug ID 55249 Change to  the desc. field on a change request does not change the Object Desc. in Overview Approval
*    2006-07-17    BAKALK    Bug ID 58216, Fixed Sql Injection.
*    2006-09-11    BAKALK    Bug ID 58216, Fixed some spelling mistake.
*    2006-10-11    BAKALK    Bug 61103, fixed Sql errors
*    2006-10-17    BAKALK    Bug 61103, added sql funtion to an aspfield.
*    2007-05-22    BAKALK    Call Id: 143697.
*    20070807      ILSOLK    Eliminated SQLInjection.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;//Bug 61103


public class ApprovalCurrentStepOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.ApprovalCurrentStepOvw");


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
   private ASPBlock dummyblk;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPTransactionBuffer trans1;
   private boolean bConfirm;
   private String bHeadShow;
   private String bAddNote;
   private String bAddNote2;
   private String note;
   private String confirm_func;
   private String val;
   private String searchURL;
   private ASPQuery q;
   private ASPCommand cmd;
   private ASPBuffer data;
   private ASPBuffer tempBuff;
   private ASPBuffer tempRow;
   private ASPBuffer transferedBuffer;
   private String sDocInfo;
   private int pos1;
   private int pos2;
   private String doc_class;
   private String doc_no;
   private String doc_rev;
   private String sEngPartRevInfo;
   private String part_no;
   private String part_rev;
   private int currrow;
   private String sReturnVal;
   private ASPCommand cmdbuf;
   private String sMessage;
   private String bodyTag;
   private String user_where;
   private int trans_row;
   private String re;
   private boolean is_pdmcon_installed;
   private boolean bDataTransfered;
   //Bug 61103
   private String user_parameters;


   //===============================================================
   // Construction
   //===============================================================
   public ApprovalCurrentStepOvw(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();


      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      trans1 = mgr.newASPTransactionBuffer();
      fmt    = mgr.newASPHTMLFormatter();

      bConfirm = false;
      bHeadShow = "TRUE";
      bAddNote = "FALSE";
      bAddNote2 = "FALSE";
      note ="";
      user_where = ctx.readValue("USER_WHERE","");
      trans_row =ctx.readNumber("SELECTED_ROW",0);
      //Bug 61103
      user_parameters = ctx.readValue("USER_PARAMETERS","");

      bDataTransfered  = ctx.readFlag("DATA_TRANSFERED",false);
      transferedBuffer = ctx.readBuffer("TRANSFERED_BUFFER");



      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
      {
         bDataTransfered  = true;
         transferedBuffer = mgr.getTransferedData();
         okFind();
      }
         
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.buttonPressed("CANCEL_NOTE"))
         CancelNote();
      else if (mgr.buttonPressed("OK_NOTE"))
         OkNote();
      else if (mgr.buttonPressed("CANCEL_NOTE2"))
         CancelNote();
      else if (mgr.buttonPressed("OK_NOTE2"))
         OkNote2();
      else if ("OK".equals(mgr.readValue("CONFIRM")))
      {
         confirm_func = ctx.readValue("CONFIRMFUNC","");
         eval(confirm_func+";"); // SQLInjection_Safe ILSOLK 20070807
      }
      else if ("CANCEL".equals(mgr.readValue("CONFIRM")))
         commandCanceled();

      //rejectStep

      ctx.writeValue("USER_WHERE",user_where);
      ctx.writeNumber("SELECTED_ROW", trans_row);
      //Bug 61103
      ctx.writeValue("USER_PARAMETERS",user_parameters);

      if (bDataTransfered)
         ctx.writeBuffer("TRANSFERED_BUFFER",transferedBuffer);
      ctx.writeFlag("DATA_TRANSFERED",bDataTransfered);

        


      adjust();
   }


   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      String where_stmt;

      searchURL = mgr.createSearchURL(headblk);

      q = trans.addQuery(headblk);
      makeQuery();

      if(mgr.dataTransfered())
         q.addOrCondition( mgr.getTransferedData() );

      where_stmt = "LU_NAME NOT IN ('EcoRequest','EngPartRevision') OR (LU_NAME = 'EcoRequest' AND Eco_Request_API.Get_Ecr_State(Client_SYS.Get_Key_Reference_Value(KEY_REF,'REQUEST_NO')) != 'Cancelled')";

      if(is_pdmcon_installed)
         where_stmt = where_stmt + "OR (LU_NAME = 'EngPartRevision' AND (Client_SYS.Get_Key_Reference_Value(KEY_REF,'PART_NO')  IN ( SELECT PART_NO FROM ENG_PART_REVISION WHERE( PART_NO = Client_SYS.Get_Key_Reference_Value(KEY_REF,'PART_NO')  AND PART_REV = Client_SYS.Get_Key_Reference_Value(KEY_REF,'PART_REV')AND OBJSTATE  != 'Obsolete' ))))";

      q.addWhereCondition(where_stmt);

      q.setOrderByClause(" LU_NAME,STEP_NO ");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("DOCMAWAPPROVALCURRENTSTEPOVWNODATA: No data found.");
         headset.clear();
      }
   }


   //bug 58216 starts
   public void makeQuery()
   {
      user_where = "";
      String temp_where = "";
      ASPManager mgr = getASPManager();

      if (!mgr.isEmpty(mgr.readValue("SOBJECTTYPE")) && mgr.getASPField("SOBJECTTYPE").isQueryable())
      {
         // Check if case sensitive
         if (mgr.isEmpty(mgr.readValue("__CASESS")))
            //temp_where = "UPPER(OBJECT_CONNECTION_SYS.Get_Logical_Unit_Description(LU_NAME)) LIKE UPPER('" + mgr.readValue("SOBJECTTYPE") + "')";
            temp_where = "UPPER(OBJECT_CONNECTION_SYS.Get_Logical_Unit_Description(LU_NAME)) LIKE UPPER(?)";
         else
            temp_where = "OBJECT_CONNECTION_SYS.Get_Logical_Unit_Description(LU_NAME) LIKE ?";

         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      if (!mgr.isEmpty(mgr.readValue("SOBJECTKEY")) && mgr.getASPField("SOBJECTKEY").isQueryable())
      {
         // Check if case sensitive
         if (mgr.isEmpty(mgr.readValue("__CASESS")))
            temp_where = "UPPER(OBJECT_CONNECTION_SYS.Get_Instance_Description(LU_NAME,NULL,KEY_REF)) LIKE UPPER(?)";
         else
            temp_where = "OBJECT_CONNECTION_SYS.Get_Instance_Description(LU_NAME,NULL,KEY_REF) LIKE ?";

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
            temp_where="CONNECTION_DESCRIPTION LIKE ?";

         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      if (!mgr.isEmpty(mgr.readValue("APPROVAL_STATUS")) && mgr.getASPField("APPROVAL_STATUS").isQueryable())
      {
         if (mgr.isEmpty(mgr.readValue("__CASESS")))
            temp_where = "UPPER(APPROVAL_STATUS) LIKE UPPER(?)";
         else
            temp_where="APPROVAL_STATUS LIKE ?";

         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      if (!mgr.isEmpty(mgr.readValue("STEP_NO")) && mgr.getASPField("STEP_NO").isQueryable())
      {
         temp_where = " STEP_NO LIKE ?";
         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }
      if (!mgr.isEmpty(mgr.readValue("PERSON_ID")) && mgr.getASPField("PERSON_ID").isQueryable())
      {
         temp_where = " PERSON_ID LIKE ?";
         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      if (!mgr.isEmpty(mgr.readValue("SROUTESIGNDESCRIPTION")) && mgr.getASPField("SROUTESIGNDESCRIPTION").isQueryable())
      {
         if (mgr.isEmpty(mgr.readValue("__CASESS")))
            temp_where = "UPPER(Eng_Person_API.Get_Name_For_User(PERSON_ID)) LIKE UPPER(?)";
         else
            temp_where="Eng_Person_API.Get_Name_For_User(PERSON_ID) LIKE ?";

         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      if (!mgr.isEmpty(mgr.readValue("GROUP_ID")) && mgr.getASPField("GROUP_ID").isQueryable())
      {
         if (mgr.isEmpty(mgr.readValue("__CASESS")))
            temp_where = "UPPER(GROUP_ID) LIKE UPPER(?)";
         else
            temp_where="GROUP_ID LIKE ?";

         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      if (!mgr.isEmpty(mgr.readValue("APPROVAL_STEPS")) && mgr.getASPField("APPROVAL_STEPS").isQueryable())
      {
         if (mgr.isEmpty(mgr.readValue("__CASESS")))
            temp_where = "UPPER(APPROVAL_ROUTING_API.Get_Approved_Status(LU_NAME,KEY_REF)) LIKE UPPER(?)";
         else
            temp_where="APPROVAL_ROUTING_API.Get_Approved_Status(LU_NAME,KEY_REF) LIKE ?";

         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      if (!mgr.isEmpty(mgr.readValue("DOCUMENT_STATE")) && mgr.getASPField("DOCUMENT_STATE").isQueryable())
      {
         if (mgr.isEmpty(mgr.readValue("__CASESS")))
            temp_where = "UPPER(DOC_ISSUE_API.Get_State_By_Keyref( KEY_REF )) LIKE UPPER(?)";
         else
            temp_where="DOC_ISSUE_API.Get_State_By_Keyref( KEY_REF ) LIKE ?";

         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      if (!mgr.isEmpty(mgr.readValue("PREV_APPROVAL_DATE")) && mgr.getASPField("PREV_APPROVAL_DATE").isQueryable())
      {
         temp_where = " PREV_APPROVAL_DATE LIKE ?";
         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      if (!mgr.isEmpty(mgr.readValue("NAPPROVALDT")) && mgr.getASPField("NAPPROVALDT").isQueryable())
      {
         if (mgr.isEmpty(mgr.readValue("__CASESS")))
            temp_where = "UPPER(TRUNC(APPROVAL_DT,0)) LIKE UPPER(?)";
         else
            temp_where="TRUNC(APPROVAL_DT,0) LIKE ?";

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
            temp_where="NOTE LIKE ?";

         if (mgr.isEmpty(user_where))
            user_where = temp_where;
         else
            user_where = user_where + "AND " + temp_where;
      }

      q.addWhereCondition(user_where); // SQLInjection_Safe ILSOLK 20070807

      //to add parameters
      user_parameters = "";//bug 61103

      if (!mgr.isEmpty(mgr.readValue("SOBJECTTYPE")) && mgr.getASPField("SOBJECTTYPE").isQueryable())
      {
        
         //Bug 61103
         user_parameters += "SOBJECTTYPE^" + mgr.readValue("SOBJECTTYPE") + "|";
      }

      if (!mgr.isEmpty(mgr.readValue("SOBJECTKEY")) && mgr.getASPField("SOBJECTKEY").isQueryable())
      {
         //Bug 61103
         user_parameters += "SOBJECTKEY^" + mgr.readValue("SOBJECTKEY") + "|";
      }

      if (!mgr.isEmpty(mgr.readValue("CONNECTION_DESCRIPTION")) && mgr.getASPField("CONNECTION_DESCRIPTION").isQueryable())
      {
         //Bug 61103
         user_parameters += "CONNECTION_DESCRIPTION^" + mgr.readValue("CONNECTION_DESCRIPTION") + "|";
      }

      if (!mgr.isEmpty(mgr.readValue("APPROVAL_STATUS")) && mgr.getASPField("APPROVAL_STATUS").isQueryable())
      {
         //Bug 61103
         user_parameters += "APPROVAL_STATUS^" + mgr.readValue("APPROVAL_STATUS") + "|";
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

      if (!mgr.isEmpty(mgr.readValue("SROUTESIGNDESCRIPTION")) && mgr.getASPField("SROUTESIGNDESCRIPTION").isQueryable())
      {
         //Bug 61103
         user_parameters += "SROUTESIGNDESCRIPTION^" + mgr.readValue("SROUTESIGNDESCRIPTION") + "|";
      }

      if (!mgr.isEmpty(mgr.readValue("GROUP_ID")) && mgr.getASPField("GROUP_ID").isQueryable())
      {
         //Bug 61103
         user_parameters += "GROUP_ID^" + mgr.readValue("GROUP_ID") + "|";
      }

      if (!mgr.isEmpty(mgr.readValue("APPROVAL_STEPS")) && mgr.getASPField("APPROVAL_STEPS").isQueryable())
      {
         //Bug 61103
         user_parameters += "APPROVAL_STEPS^" + mgr.readValue("APPROVAL_STEPS") + "|";
      }

      if (!mgr.isEmpty(mgr.readValue("DOCUMENT_STATE")) && mgr.getASPField("DOCUMENT_STATE").isQueryable())
      {
         //Bug 61103
         user_parameters += "DOCUMENT_STATE^" + mgr.readValue("DOCUMENT_STATE") + "|";
      }

      if (!mgr.isEmpty(mgr.readValue("PREV_APPROVAL_DATE")) && mgr.getASPField("PREV_APPROVAL_DATE").isQueryable())
      {
         //Bug 61103
         user_parameters += "PREV_APPROVAL_DATE^" + mgr.readValue("PREV_APPROVAL_DATE") + "|";
      }

      if (!mgr.isEmpty(mgr.readValue("NAPPROVALDT")) && mgr.getASPField("NAPPROVALDT").isQueryable())
      {
         //Bug 61103
         user_parameters += "NAPPROVALDT^" + mgr.readValue("NAPPROVALDT") + "|";
      }

      if (!mgr.isEmpty(mgr.readValue("NOTE")) && mgr.getASPField("NOTE").isQueryable())
      {
         //Bug 61103
         user_parameters += "NOTE^" + mgr.readValue("NOTE") + "|";
      }
      //end of adding parameters
      //Bug 61103
      addParameters(q,user_parameters);
      //bug 58216 ends
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

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("MAIN","APPROVAL_ROUTING_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MAIN/DATA");
      headset.addRow(data);
      trans.clear();
   }


   public void  transferToDocInfo()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      tempBuff = mgr.newASPBuffer();
      tempRow = tempBuff.addBuffer("DATA");

      sDocInfo = headset.getRow().getValue("SOBJECTKEY");
      pos1 = sDocInfo.indexOf(": ") +2;
      pos2 = sDocInfo.indexOf(",",pos1);
      doc_class = sDocInfo.substring(pos1,pos2);

      pos1 = sDocInfo.indexOf(": ",pos2) +2;
      pos2 = sDocInfo.indexOf(",",pos1);
      doc_no = sDocInfo.substring(pos1,pos2);

      pos1 = sDocInfo.indexOf(": ",pos2) +2;
      doc_rev = sDocInfo.substring(pos1);

      tempRow.addItem("DOC_CLASS",doc_class);
      tempRow.addItem("DOC_NO",doc_no);
      tempRow.addItem("DOC_REV",doc_rev);

      mgr.transferDataTo("DocIssue.page",tempBuff);
   }


   public void  transferToEngPartRev()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      tempBuff = mgr.newASPBuffer();
      tempRow = tempBuff.addBuffer("DATA");

      sEngPartRevInfo = headset.getRow().getValue("SOBJECTKEY");
      pos1 = sEngPartRevInfo.indexOf(": ") +2;
      pos2 = sEngPartRevInfo.indexOf(", ");
      part_no = sEngPartRevInfo.substring(pos1,pos2);

      pos1 = sEngPartRevInfo.indexOf(": ",pos2) +2;
      part_rev = sEngPartRevInfo.substring(pos1);

      tempRow.addItem("PART_NO",part_no);
      tempRow.addItem("PART_REV",part_rev);

      mgr.transferDataTo("../pdmcow/EngPartRevisionOvw.page",tempBuff);
   }


   public void  addNote()
   {

     if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }

      bHeadShow = "FALSE";
      bAddNote  = "TRUE";
      bAddNote2 = "FALSE";
   }


   public void  OkNote()
   {
      ASPManager mgr = getASPManager();


       trans1.clear();
       data = headset.getRow();
       data.setValue("NOTE",mgr.readValue("NOTE"));
       headset.setRow(data);
       mgr.submit(trans1);
       trans1.clear();
       headset.goTo(trans_row);
       confirmedApproveStep();
   }


   public void  CancelNote()
   {
      ASPManager mgr = getASPManager();

    trans.clear();
    headset.unselectRows();
    headset.setFilterOff();
    mgr.submit(trans);
    trans.clear();
   }


   public void  addNote2()
   {

     if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }

      bHeadShow = "FALSE";
      bAddNote  = "FALSE";
      bAddNote2 = "TRUE";

   }


   public void  OkNote2()
   {
      ASPManager mgr = getASPManager();


       trans1.clear();
       data = headset.getRow();
       data.setValue("NOTE",mgr.readValue("NOTE"));
       headset.setRow(data);
       mgr.submit(trans1);
       trans1.clear();
       headset.goTo(trans_row);
       confirmedRejectStep();
   }


   public void  approveStepClicked()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         trans_row=headset.getRowSelected();
      }
      else
          trans_row = headset.getCurrentRowNo();

      trans.clear();
      cmd = trans.addCustomFunction("CHECKRIGHTS","APPROVAL_ROUTING_API.Check_Ack_Rights","RETURN");
      cmd.addParameter("LINE_NO",  headset.getValue("LINE_NO"));
      cmd.addParameter("STEP_NO",  headset.getValue("STEP_NO"));
      cmd.addParameter("PERSON_ID",headset.getValue("PERSON_ID"));
      cmd.addParameter("LU_NAME",  headset.getValue("LU_NAME"));
      cmd.addParameter("KEY_REF",  headset.getValue("KEY_REF"));
      cmd.addParameter("GROUP_ID", headset.getValue("GROUP_ID"));
      trans = mgr.perform(trans);
      sReturnVal = trans.getValue("CHECKRIGHTS/DATA/RETURN");
      trans.clear();

      if  ( "TRUE".equals(sReturnVal) )
      {
         bHeadShow = "FALSE";
         bAddNote  = "TRUE";
         bAddNote2 = "FALSE";
      }
      else
      {
         mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWNORIGHTSAPPROVE: You do not have rights to approve this step"));
         headset.unselectRows();
         headset.setFilterOff();
      }
   }



   public void  rejectStep()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         trans_row=headset.getRowSelected();
      }
      else
      {
         trans_row = headset.getCurrentRowNo();
         headset.selectRow();
      }


      if  ( "REJ".equals(headset.getValue("APPROVAL_STATUS_DB")) )
      {
         mgr.showAlert("This step has already been rejected!");
      }
      else
      {
         trans.clear();
         cmd = trans.addCustomFunction("CHECKRIGHTS","APPROVAL_ROUTING_API.Check_Ack_Rights","RETURN");
         cmd.addParameter("LINE_NO",headset.getValue("LINE_NO"));
         cmd.addParameter("STEP_NO",headset.getValue("STEP_NO"));
         cmd.addParameter("PERSON_ID",headset.getValue("PERSON_ID"));
         cmd.addParameter("LU_NAME",headset.getValue("LU_NAME"));
         cmd.addParameter("KEY_REF",headset.getValue("KEY_REF"));
         cmd.addParameter("GROUP_ID",headset.getValue("GROUP_ID"));
         trans = mgr.perform(trans);
         sReturnVal = trans.getValue("CHECKRIGHTS/DATA/RETURN");
         trans.clear();


         if  ( "TRUE".equals(sReturnVal) )
         {
	   	 	 bHeadShow = "FALSE";
	   	 	 bAddNote  = "FALSE";
             bAddNote2 = "TRUE";
         }
         else
         {
            mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWNORIGHTSREJECT: You do not have rights to reject this step"));
            headset.unselectRows();
            headset.setFilterOff();
         }
      }
   }


   public void  confirmedApproveStep()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmdbuf = trans.addCustomCommand("APPROVESTEPS", "APPROVAL_ROUTING_API.Set_Next_App_Step");
      cmdbuf.addParameter("LU_NAME",headset.getValue("LU_NAME"));
      cmdbuf.addParameter("KEY_REF",headset.getValue("KEY_REF"));
      cmdbuf.addParameter("LINE_NO",headset.getValue("LINE_NO"));
      cmdbuf.addParameter("STEP_NO",headset.getValue("STEP_NO"));
      cmdbuf.addParameter("ATTR","APP");
      trans = mgr.perform(trans);
      trans.clear();

      headset.unselectRows();

      q = trans.addEmptyQuery(headblk);

      if(bDataTransfered  )
         q.addOrCondition( transferedBuffer);

      q.addWhereCondition(user_where); // SQLInjection_Safe ILSOLK 20070807
      //Bug 61103
      addParameters(q,user_parameters);
      q.addWhereCondition("LU_NAME NOT LIKE 'EcoRequest' OR (LU_NAME = 'EcoRequest' AND Eco_Request_API.Get_Ecr_State(Client_SYS.Get_Key_Reference_Value(KEY_REF,'REQUEST_NO')) != 'Cancelled')");
      q.setOrderByClause(" LU_NAME,STEP_NO ");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if (  headset.countRows() == 0 && !bDataTransfered)
      {
         mgr.showAlert("DOCMAWAPPROVALCURRENTSTEPOVWNODATA: No data found.");
         headset.clear();
      }

   }


   public void  confirmedRejectStep()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmdbuf = trans.addCustomCommand ("CHECK_STEPS", "APPROVAL_ROUTING_API.Set_Next_App_Step");
      cmdbuf.addParameter("LU_NAME",headset.getValue("LU_NAME"));
      cmdbuf.addParameter("KEY_REF",headset.getValue("KEY_REF"));
      cmdbuf.addParameter("LINE_NO",headset.getValue("LINE_NO"));
      cmdbuf.addParameter("STEP_NO",headset.getValue("STEP_NO"));
      cmdbuf.addParameter("APPSTAT_DUMMY","REJ");
      trans = mgr.perform(trans);
      trans.clear();

      headset.unselectRows();

      q = trans.addEmptyQuery(headblk);
      if(bDataTransfered  )
         q.addOrCondition( transferedBuffer);
      q.addWhereCondition(user_where); // SQLInjection_Safe ILSOLK 20070807
      //Bug 61103
      addParameters(q,user_parameters);
      q.addWhereCondition("LU_NAME NOT LIKE 'EcoRequest' OR (LU_NAME = 'EcoRequest' AND Eco_Request_API.Get_Ecr_State(Client_SYS.Get_Key_Reference_Value(KEY_REF,'REQUEST_NO')) != 'Cancelled')");
      q.setOrderByClause(" LU_NAME,STEP_NO ");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);
      headset.goTo(trans_row);

      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("DOCMAWAPPROVALCURRENTSTEPOVWNODATA: No data found.");
         headset.clear();
      }

      headset.setFilterOff();
   }


   public void  commandCanceled()
   {

      bConfirm = false;
      sMessage = "";
      ctx.writeValue("CONFIRMFUNC","");
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
              setHidden();

      headblk.addField("OBJVERSION").
              setHidden();

      headblk.addField("SOBJECTTYPE").
              setSize(20).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWSOBJECTTYPE: Object").
              setLOV("ObjectConnectionLov.page").
              setReadOnly().
              setMaxLength(2000).
              setFunction("OBJECT_CONNECTION_SYS.Get_Logical_Unit_Description(LU_NAME)");

      headblk.addField("SOBJECTKEY").
              setSize(20).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWSOBJECTKEY: Object Key").
              setMaxLength(2000).
              setReadOnly().
              setFunction("OBJECT_CONNECTION_SYS.Get_Instance_Description(LU_NAME,NULL,KEY_REF)");

      headblk.addField("CONNECTION_DESCRIPTION").
              setSize(20).
              setMaxLength(100).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWCONNECTIONDESCRIPTION: Object Description").
              setReadOnly().
              setFunction("APPROVAL_ROUTING_API.Get_Connection_Desc(LU_NAME,KEY_REF)");
    
      headblk.addField("APPROVAL_STATUS").
              setSize(20).
              setMaxLength(200).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWAPPSTATUS: Approval Status").
              setReadOnly();

      headblk.addField("APPROVAL_STATUS_DB").
              setFunction("Approval_Status_API.Encode(:APPROVAL_STATUS)").
              setHidden();

      headblk.addField("STEP_NO","Number").
              setSize(20).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWSTEPNO: Approval Step No").
              setAlignment("RIGHT").
              setMaxLength(3).
              setReadOnly();

      headblk.addField("DESCRIPTION").
              setSize(20).
              setMaxLength(120).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWDESCRIPTION: Approval Description").
              setReadOnly();

      headblk.addField("PERSON_ID").
              setSize(20).
              setDynamicLOV("PERSON_INFO_USER").
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWPERSONID: Approver").
              setMaxLength(30).
              setReadOnly().
              setUpperCase();

      headblk.addField("SROUTESIGNDESCRIPTION").
              setSize(20).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWSROUTESIGNDESCRIPTION: Approver Name").
              setMaxLength(2000).
              setReadOnly().
              setFunction("Eng_Person_API.Get_Name_For_User(PERSON_ID)");

      headblk.addField("GROUP_ID").
              setSize(20).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWGROUPID: Group ID").
              setReadOnly();

      headblk.addField("APPROVAL_STEPS").
              setSize(20).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWAPPSTEPS: Approval Steps").
              setReadOnly().
              setDefaultNotVisible().
              setFunction("APPROVAL_ROUTING_API.Get_Approved_Status(LU_NAME,KEY_REF)");

      headblk.addField("DOCUMENT_STATE").
              setSize(20).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWDOCSTATE: Document Status").
              setReadOnly().
              setDefaultNotVisible().
              setFunction("DOC_ISSUE_API.Get_State_By_Keyref( KEY_REF )");

      headblk.addField("PREV_APPROVAL_DATE","Date").
              setSize(20).
              setReadOnly().
              setDefaultNotVisible().
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWPREVAPPROVALDATE: Earliest Date For Approval");

      headblk.addField("NAPPROVALDT","Number").
              setSize(20).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWNAPPROVALDT: Days Elapsed").
              setReadOnly().
              setAlignment("RIGHT").
              setDefaultNotVisible().
              setFunction("TRUNC(APPROVAL_DT,0)");

      headblk.addField("NOTE").
              setSize(20).
              setMaxLength(2000).
              setDefaultNotVisible().
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWNOTE: Note").
              setMaxLength(60);

      headblk.addField("NAPPROVED", "Number").
              setSize(20).
              setLabel("DOCMAWAPPROVALCURRENTSTEPOVWNAPPROVED: Approved").
              setReadOnly().
              setCheckBox("1,0").
              setFunction("''").
              setDefaultNotVisible();

      headblk.addField("LINE_NO","Number").
              setHidden();

      headblk.addField("SEDMINFORMATION").
              setHidden().
              setFunction("EDM_FILE_API.Get_Edm_Information2(LU_NAME,KEY_REF,'ORIGINAL')");

      headblk.addField("LU_NAME").
              setHidden();

      headblk.addField("KEY_REF").
              setHidden();

      //bug 58216 
      //bug 61103
      headblk.addField("MODULE").setFunction("''").
              setHidden();
      

      headblk.setView("APPROVAL_CURRENT_STEP");
      headblk.defineCommand("APPROVAL_ROUTING_API","Modify__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.DELETE);
      headbar.addCustomCommand("approveStepClicked",mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWAPPSTEP: Approve Step"));
      headbar.addCustomCommand("rejectStep",mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWREJSTEP: Reject Step"));
      headbar.addCustomCommand("transferToDocInfo",mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWDOCINFO: Document Info..."));
      headbar.addCustomCommand("transferToEngPartRev",mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWENGPARTREV: Engineering Part Revision Info..."));

      headbar.addCommandValidConditions("transferToDocInfo","SOBJECTTYPE","Enable","DocIssue");
      headbar.addCommandValidConditions("transferToEngPartRev","SOBJECTTYPE","Enable","Engineering Part Revision");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("DOCMAWAPPROVALCURRENTSTEPOVWOVAPP: Approvals");

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);



      // DUMMY BLOCK
      dummyblk = mgr.newASPBlock("DUMMY");
      dummyblk.addField("RETURN");
      dummyblk.addField("ATTR");
      dummyblk.addField("APPSTAT_DUMMY");

      is_pdmcon_installed = isModuleInstalled("PDMCON");
   }

   private boolean isModuleInstalled(String module)
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      double n;
      //bug 58216 starts
      ASPQuery query = trans.addQuery("CNT","SELECT COUNT(*) N FROM MODULE WHERE MODULE = ? AND VERSION is not null");
      query.addParameter("MODULE", module);
      //bug 58216 ends

      trans = mgr.submit(trans);
      n = trans.getNumberValue("CNT/DATA/N");

        if( n > 0 )
                return true;
        else
                return false;

   }

   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if ( "HEAD.NewRow".equals(mgr.readValue("__COMMAND")) )
         mgr.getASPField("SOBJECTTYPE").deactivateLOV();

      mgr.getASPField("SOBJECTTYPE").setLOVProperty("WHERE","service_list = '*' or'^' || service_list like '^%ApprovalRouting^%' or service_list is null");

      // Following code is used to replace the default ONLOAD function, with 'displayConfirmBox'.
      // ('onLoad()' is called from the new function).
      bodyTag = mgr.generateBodyTag();
      if (bConfirm)
      {
         bConfirm = false;
         re =" /javascript:onLoad/gi";
         bodyTag = replaceString(bodyTag,re, "javascript:displayConfirmBox");
      }

   }


   protected String getDescription()
   {
      return "DOCMAWAPPROVALCURRENTSTEPOVWDES: Overview - Approvals";
   }


   protected String getTitle()
   {
      return "DOCMAWAPPROVALCURRENTSTEPOVWTITLE: Overview Approvals";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWAPPROVALCURRENTSTEPOVWTITLE: Overview Approvals"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(bodyTag);
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("   <input type=\"hidden\" name=\"CONFIRM\" value=\"\">\n");
      out.append("   <input type=\"hidden\" name=\"APPROVE\" value=\"\">\n");
      out.append(mgr.startPresentation("DOCMAWAPPROVALCURRENTSTEPOVWTITLE: Overview Approvals"));

      if  ("TRUE".equals(bHeadShow))
      {
         out.append(headlay.show());
      }

      else if ("TRUE".equals(bAddNote))
      {
         out.append("  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"200\" width=\"240\">\n");
         out.append("   <tr>\n");
         out.append("    <td height=\"17\"><font class=WriteTextValue><textarea class=WriteTextValue rows=\"15\" name=\"NOTE\" cols=\"75\">");
         out.append(note);
         out.append("</textarea></font></td>\n");
         out.append("    <td height=\"36\"></td>\n");
         out.append("   </tr>       \n");
         out.append("  </table>  \n");
         out.append("  <table>\n");
         out.append("   <tr>    \n");
         out.append("         <td align=\"left\">");
         out.append(fmt.drawSubmit("OK_NOTE",mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWOKNOTE:   OK  "),""));
         out.append("&nbsp;                           \n");
         out.append(fmt.drawSubmit("CANCEL_NOTE",mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWCANCELNOTE: Cancel"),""));
         out.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
         out.append("    </tr>\n");
         out.append("  </table>   \n");
      }

      else if ("TRUE".equals(bAddNote2))
      {
         out.append("  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"200\" width=\"240\">\n");
         out.append("   <tr>\n");
         out.append("    <td height=\"17\"><font class=WriteTextValue><textarea class=WriteTextValue rows=\"15\" name=\"NOTE\" cols=\"75\">");
         out.append(note);
         out.append("</textarea></font></td>\n");
         out.append("    <td height=\"36\"></td>\n");
         out.append("   </tr>       \n");
         out.append("  </table>  \n");
         out.append("  <table>\n");
         out.append("   <tr>    \n");
         out.append("         <td align=\"left\">");
         out.append(fmt.drawSubmit("OK_NOTE2",mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWOKNOTE2:   OK  "),""));
         out.append(" &nbsp;                           \n");
         out.append(fmt.drawSubmit("CANCEL_NOTE2",mgr.translate("DOCMAWAPPROVALCURRENTSTEPOVWCANCELNOTE2: Cancel"),""));
         out.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
         out.append("    </tr>\n");
         out.append("  </table>   \n");
      }


      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------


      appendDirtyJavaScript("function validateSobjecttype(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if( !checkSobjecttype(i) ) return;\n");
      appendDirtyJavaScript("}\n");


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


      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }


   private int stringIndex(String mainString,String subString)
   {
      int a=mainString.length();
      int index=-1;
      for(int i=0;i<a;i++)
         if(mainString.startsWith(subString,i))
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
      while(posi!=-1)
      {
         retString+=mainString.substring(0,posi)+replaceString;
         mainString=mainString.substring(posi+subString.length(),mainString.length());
         posi = stringIndex(mainString, subString);
      }

      return retString+mainString;
   }


   public int howManyOccurance(String str,char c)
   {
      int strLength=str.length();
      int occurance=0;
      for(int index=0;index<strLength;index++)
       if(str.charAt(index)==c)
        occurance++;
      return occurance;
   }


   private String[] split(String str,char c)
   {
      int length_=howManyOccurance(str,c);
      int strLength=str.length();
      int occurance=0;
      int index=0;

      String[] tempString= new String[length_+1];
      while(strLength>0)
      {
         occurance=str.indexOf(c);
         if(occurance==-1)
         {
           tempString[index]=str;
           break;
         }
         else
         {
            tempString[index++]=str.substring(0,occurance);
            str=str.substring(occurance+1,strLength);
            strLength=str.length();
         }
      }
      return tempString;
   }


}
