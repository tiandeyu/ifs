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
* File                          :
* Description                   :
* Notes                         :
* Other Programs Called :
* ----------------------------------------------------------------------------
* Modified    : Automatically generated by IFS/Design
* ----------------------------------------------------------------------------
*/

//-----------------------------------------------------------------------------
//-----------------------------   Package def  ------------------------------
//-----------------------------------------------------------------------------

package ifs.docmaw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.*;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class DocIssueTargetOrg extends ASPPageProvider
{
   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------
   
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocIssueTargetOrg");
   
   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------
   
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------
   
   public  DocIssueTargetOrg (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   public void run()
   {
      ASPManager mgr = getASPManager();
      
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("ORG_NO")) )
         okFind();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------
   
   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      
      // Data isolation
      q.addWhereCondition("DOC_CLASS IN ('" + DocmawConstants.EXCH_SEND + "', '" + DocmawConstants.PROJ_SEND + "')");
      String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
      StringBuffer sb = new StringBuffer("(");
      if (!"()".equals(tempPersonZones)) {
         sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
      } else {
         sb.append("(1=2)");
      }
      sb.append(")");
      q.addWhereCondition(sb.toString());

      q.setOrderByClause("SEND_DATE DESC");
      
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("DOCISSUETARGETORGNODATA: No data found.");
         headset.clear();
      }
   }
   
   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      
      // Data isolation
      q.addWhereCondition("DOC_CLASS IN ('" + DocmawConstants.EXCH_SEND + "', '" + DocmawConstants.PROJ_SEND + "')");
      String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
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
   
   public void newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;
      
      cmd = trans.addEmptyCommand("HEAD","DOC_ISSUE_TARGET_ORG_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }
   
   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      
      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
      setHidden();
      
      headblk.addField("OBJVERSION").
      setHidden();
      
      headblk.addField("VIEW_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      setLabel("DOCISSUETARGETORGVIEWFILE: View File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV", "NEWWIN").
      setAsImageField();
      
      headblk.addField("DOC_CODE").
      setInsertable().
      setFieldHyperlink("DocIssue.page", "PAGE_URL","DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV", "NEWWIN").
      setLabel("DOCISSUETARGETORGDOCCODE: Doc Code").
      setSize(20);
      
      headblk.addField("REV_TITLE").
      setInsertable().
      setFieldHyperlink("DocIssue.page", "PAGE_URL","DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV", "NEWWIN").
      setLabel("DOCISSUETARGETORGREVTITLE: Rev Title").
      setSize(50);
      
      headblk.addField("PURPOSE_NO").
      setInsertable().
      setDynamicLOV("DOC_COMMUNICATION_SEQ").
      setLabel("DOCISSUETARGETORGPURPOSENO: Purpose No").
      setSize(10);

      /*
      headblk.addField("PURPOSE_NAME").
      setLabel("DOCISSUETARGETORGPURPOSENAME: Purpose Name").
      setFunction("DOC_COMMUNICATION_SEQ_API.Get_Purpose_Name(:PURPOSE_NO)").
      setReadOnly().
      setSize(10);
      mgr.getASPField("PURPOSE_NO").setValidation("PURPOSE_NAME");
      */

      headblk.addField("SEND_UNIT_NO").
      setInsertable().
      setLabel("DOCISSUETARGETORGSENDUNITNO: Send Unit No").
      setDynamicLOV("GENERAL_ZONE_LOV").
      setSize(10);

      /*
      headblk.addField("SEND_UNIT_NAME").
      setLabel("DOCISSUETARGETORGSENDUNITNAME: Receive Unit Name").
      setFunction("GENERAL_ZONE_API.GET_ZONE_DESC(:SEND_UNIT_NO)").
      setReadOnly().
      setSize(20);
      mgr.getASPField("SEND_UNIT_NO").setValidation("SEND_UNIT_NAME");
      */

      headblk.addField("SEND_DATE", "Date").
      setInsertable().
      setLabel("DOCISSUETARGETORGSENDDATE: Send Date").
      setSize(10);
      
      headblk.addField("ORG_NO").
      setMandatory().
      setInsertable().
      setReadOnly().
      setDynamicLOV("GENERAL_ZONE_LOV").
      setLabel("DOCISSUETARGETORGORGNO: Org No").
      setSize(10);
      
      headblk.addField("ORG_NAME").
      setReadOnly().
      setLabel("DOCISSUETARGETORGORGNAME: Org Name").
      setSize(20);
      
      headblk.addField("SIGN_STATUS").
      setInsertable().
      setReadOnly().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCISSUETARGETORGSIGNSTATUS: Sign Status").
      setSize(5);
      
      headblk.addField("IS_MAIN").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCISSUETARGETORGISMAIN: Is Main").
      setSize(5);
      
      headblk.addField("DOC_CLASS").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("DOCISSUETARGETORGDOCCLASS: Doc Class").
      setSize(10);
      
      headblk.addField("DOC_NO").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("DOCISSUETARGETORGDOCNO: Doc No").
      setSize(10);
      
      headblk.addField("DOC_SHEET").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("DOCISSUETARGETORGDOCSHEET: Doc Sheet").
      setSize(10);
      
      headblk.addField("DOC_REV").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("DOCISSUETARGETORGDOCREV: Doc Rev").
      setSize(10);
      
      headblk.addField("PAGE_URL").
      setFunction("Doc_Issue_API.Get_Page_Url(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
      setHidden();
      
      headblk.addField("IS_ELE_DOC").
      setFunction("EDM_FILE_API.Have_Edm_File(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
      setHidden();
      
      headblk.setView("DOC_ISSUE_TARGET_ORG_INFO");
      headblk.defineCommand("DOC_ISSUE_TARGET_ORG_API", "");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("DOCISSUETARGETORGTBLHEAD: Doc Issue Target Orgs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      headlay.setSimple("PURPOSE_NAME");
      headlay.setSimple("SEND_UNIT_NAME");
      headlay.setSimple("ORG_NAME");
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
               return "<img src=\"" + imgSrc + "\" height=\"16\" width=\"16\" border=\"0\">";
            }
         }
      }
      return "";
   }
   
   public void  adjust()
   {
      // fill function body
   }
   
   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------
   
   protected String getDescription()
   {
      return "DOCISSUETARGETORGDESC: Doc Issue Target Org";
   }
   
   
   protected String getTitle()
   {
      return getDescription();
   }
   
   
   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
         appendToHTML(headlay.show());
   }
}
