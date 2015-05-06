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
*  File        : DocIssueGeneral.java
*  Modified    :
*    2002-12-26   Dikalk   2002-2 SP3 Merge: "Shthlk - Bug  Id 33650  Modified the method setApproved()."
*    2003-08-27   InoSlk   Call Id 101731: Modified doReset() and clone().
*    2003-10-22   NiSilk   Call ID 107798: Added Doc Sheet
*    2004-12-06   Dikalk   Merged bug 47706
*    2005-08-01   SHTHLK   Merged Bug Id  51788: Removed No_of_Sheets field and added new field SCALE.
*     2006-07-18  BAKALK   Bug ID 58216, Fixed Sql Injection.
*     2007-05-23  BAKALK   Call ID 144114, Increased the height of Sub window for New revision Wizard.
*     2007-05-24  CHSELK   Call ID 142726. Disabled 'Documents' command.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocIssueGeneral extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocIssueGeneral");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPTabContainer tabs;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private ASPCommand cmd;

   private int activetab;

   private boolean confirm;

   private String message;
   private String confirm_func;
   private String val;
   private String doc_no;
   private String doc_sheet;
   private String doc_rev;
   private String doc_class;
   private String url;
   private String bodyTag;
   private String activatetab;
   private String parent_lu_name;
   private String parent_key_ref;
   private boolean bOpenWizardWindow;
   private String sUrl;


   //===============================================================
   // Construction
   //===============================================================
   public DocIssueGeneral(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();

      activetab=ctx.readNumber("ACTIVETAB",0);
      message = "";
      confirm = false;

      parent_lu_name=mgr.getQueryStringValue("LU_NAME");
      if (parent_lu_name == null)
         parent_lu_name=ctx.readValue("PARENT_LU_NAME");

      parent_key_ref=mgr.getQueryStringValue("KEY_REF");
      if (parent_key_ref==null)
         parent_key_ref=ctx.readValue("PARENT_KEY_REF");

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO")))
         okFind();
      else if ("OK".equals(mgr.readValue("CONFIRM")))
      {
         confirm_func = ctx.readValue("CONFIRMFUNC","");

         if ("onConfirmObsolete()".equals(confirm_func))
            onConfirmObsolete();
         else if ("onConfirmStartApp()".equals(confirm_func))
            onConfirmStartApp();
         else if ("onConfirmRelease()".equals(confirm_func))
            onConfirmRelease();
         else if ("onConfirmApprove()".equals(confirm_func))
            onConfirmApprove();
      }
      else if ("CANCEL".equals(mgr.readValue("CONFIRM")))
         onUnconfirm();

      if (!mgr.isEmpty(mgr.readValue("DOC_CLASS_FROM_WIZ")))
         okFindRev();


      if (!mgr.isEmpty(mgr.readValue("DOC_CLASS_FROM_WIZ")))
         okFindRev();

      ctx.writeValue("PARENT_LU_NAME", parent_lu_name);
      ctx.writeValue("PARENT_KEY_REF", parent_key_ref);

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

      trans.clear();
      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
         q.addOrCondition( mgr.getTransferedData() );
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);
   }


   private void okFindRev()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer rec_set = headset.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      trans.clear();
      q = trans.addQuery(headblk);
      //bug 58216 starts
      q.addWhereCondition("DOC_CLASS = ? and DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", mgr.readValue("DOC_CLASS_FROM_WIZ"));
      q.addParameter("DOC_NO",    mgr.readValue("DOC_NO_FROM_WIZ"));
      q.addParameter("DOC_SHEET", mgr.readValue("DOC_SHEET_FROM_WIZ"));
      q.addParameter("DOC_REV",   mgr.readValue("DOC_REV_FROM_WIZ"));
      //bug 58216 end
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);
   }


   public void  startApproval()
   {
      ASPManager mgr = getASPManager();

      if (!("Preliminary".equals(headset.getValue("OBJSTATE"))))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEGENERALCANT: Cannot perform on the selected record."));
      else
      {
         confirm = true;
         message = mgr.translate("DOCMAWDOCISSUEGENERALMESSTARTAPP: Do you wish to begin the approval process?");
         ctx.writeValue("CONFIRMFUNC","onConfirmStartApp()");
      }
   }


   public void  onConfirmStartApp()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomCommand("STARTAPPROVAL","DOC_ISSUE_API.PROMOTE_TO_APP_IN_PROGRESS__");
      cmd.addParameter("INFO");
      cmd.addParameter("OBJID",headset.getValue("OBJID"));
      cmd.addParameter("OBJVERSION",headset.getValue("OBJVERSION"));
      cmd.addParameter("ATTR");
      cmd.addParameter("ACTION","DO");
      trans = mgr.perform(trans);
      trans.clear();
      activateIssue();
   }


   public void  setApproved()
   {
      ASPManager mgr = getASPManager();

      String state = headset.getValue("OBJSTATE");

      if (( "Approved".equals(state) )|| ( "Released".equals(state) )|| ( "Obsolete".equals(state) ))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEGENERALCANT: Cannot perform on the selected record."));
      else
      {
         confirm = true;
         message = mgr.translate("DOCMAWDOCISSUEGENERALMESAPPROVE: Do you wish to approve the Document Revision?");
         ctx.writeValue("CONFIRMFUNC","onConfirmApprove()");
      }
   }


   public void  onConfirmApprove()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomCommand("SET_APPROVED","DOC_ISSUE_API.PROMOTE_TO_APPROVED__");
      cmd.addParameter("INFO");
      cmd.addParameter("OBJID",headset.getValue("OBJID"));
      cmd.addParameter("OBJVERSION",headset.getValue("OBJVERSION"));
      cmd.addParameter("ATTR");
      cmd.addParameter("ACTION","DO");
      trans = mgr.perform(trans);
      trans.clear();
      activateIssue();
   }


   public void  setReleased()
   {
      ASPManager mgr = getASPManager();

      if (!("Approved".equals(headset.getValue("OBJSTATE"))))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEGENERALCANT: Cannot perform on the selected record."));
      else
      {
         confirm = true;
         message = mgr.translate("DOCMAWDOCISSUEGENERALMESRELEASE: Do you wish to release the Document Issue?");
         ctx.writeValue("CONFIRMFUNC","onConfirmRelease()");
      }
   }


   public void  onConfirmRelease()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomCommand("SET_RELEASED","DOC_ISSUE_API.PROMOTE_TO_RELEASED__");
      cmd.addParameter("INFO");
      cmd.addParameter("OBJID",headset.getValue("OBJID"));
      cmd.addParameter("OBJVERSION",headset.getValue("OBJVERSION"));
      cmd.addParameter("ATTR");
      cmd.addParameter("ACTION","DO");
      trans = mgr.perform(trans);
      trans.clear();
      activateIssue();
   }


   public void  setObsolete()
   {
      ASPManager mgr = getASPManager();

      if ("Obsolete".equals(headset.getValue("OBJSTATE")))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEGENERALCANT: Cannot perform on the selected record."));
      else
      {
         confirm = true;
         message = mgr.translate("DOCMAWDOCISSUEGENERALMESOBSOLETE: Do you wish set the Document Issue to obsolete?");
         ctx.writeValue("CONFIRMFUNC","onConfirmObsolete()");
      }
   }


   public void  onConfirmObsolete()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomCommand("SET_OBSOLETE","DOC_ISSUE_API.PROMOTE_TO_OBSOLETE__");
      cmd.addParameter("INFO");
      cmd.addParameter("OBJID",headset.getValue("OBJID"));
      cmd.addParameter("OBJVERSION",headset.getValue("OBJVERSION"));
      cmd.addParameter("ATTR");
      cmd.addParameter("ACTION","DO");
      trans = mgr.perform(trans);
      trans.clear();
      activateIssue();
   }


   public void  activateIssue()
   {
      ASPManager mgr = getASPManager();

      String head_doc_class = headset.getValue("DOC_CLASS");
      doc_no = headset.getValue("DOC_NO");
      doc_sheet = headset.getValue("DOC_SHEET");
      doc_rev = headset.getValue("DOC_REV");

      trans.clear();
      q = trans.addEmptyQuery(headblk);
      //Bug ID 45944, inoslk, start
      q.addWhereCondition("DOC_CLASS = ? and DOC_NO = ? and DOC_SHEET = ? and DOC_REV = ?");
      q.addParameter("DOC_CLASS",head_doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV",doc_rev);
      q.includeMeta("ALL");
      mgr.submit(trans);
      trans.clear();
   }


   public void  onUnconfirm()
   {
      confirm = false;
      message = "";
      ctx.writeValue("CONFIRMFUNC","");
   }


   public void  newRevision()
   {
      ASPManager mgr = getASPManager();

      if (!headlay.isSingleLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      doc_no =    headset.getRow().getValue("DOC_NO");
      doc_sheet =    headset.getRow().getValue("DOC_SHEET");
      doc_class =   headset.getRow().getValue("DOC_CLASS");
      doc_rev =   headset.getRow().getValue("DOC_REV");

      headset.setFilterOff();
      url = mgr.getURL();
      sUrl = ("../docmaw/NewRevisionWizard.page?DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_SHEET="+mgr.URLEncode(doc_sheet)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&DOC_REV="+mgr.URLEncode(doc_rev)+"&OBJECT_LU_NAME="+ mgr.URLEncode(parent_lu_name)+"&OBJECT_KEY_REF="+mgr.URLEncode(parent_key_ref)+"&SEND_URL="+mgr.URLEncode(url));
      bOpenWizardWindow = true;

   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
      headblk.disableDocMan();

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("OBJSTATE");
      f.setHidden();

      f = headblk.addField("DOC_CLASS");
      f.setSize(10);
      f.setMaxLength(12);
      f.setDynamicLOV("DOC_CLASS");
      f.setMandatory();
      f.setLabel("DOCMAWDOCISSUEGENERALDOCCLASS: Class");
      f.setUpperCase();
      f.setReadOnly();

      f = headblk.addField("SDOCNAME");
      f.setSize(17);
      f.setLabel("DOCMAWDOCISSUEGENERALSDOCNAME: Class Name");
      f.setMaxLength(2000);
      f.setFunction("DOC_CLASS_API.Get_Name(:DOC_CLASS)");
      f.setReadOnly();

      f = headblk.addField("DOC_NO");
      f.setSize(28);
      f.setMaxLength(120);
      f.setDynamicLOV("DOC_TITLE","DOC_CLASS");
      f.setMandatory();
      f.setLabel("DOCMAWDOCISSUEGENERALDOCNO: Number");
      f.setUpperCase();
      f.setReadOnly();

      f = headblk.addField("DOC_SHEET");
      f. setSize(20);
      f. setMaxLength(10);
      f. setReadOnly();
      f. setUpperCase();
      f. setDynamicLOV("DOC_ISSUE_LOV1");
      f. setLabel("DOCMAWDOCISSUEGENERALDOCSHEET: Doc Sheet");

      f = headblk.addField("DOC_REV");
      f.setSize(8);
      f.setMaxLength(6);
      f.setMandatory();
      f.setLabel("DOCMAWDOCISSUEGENERALDOCREV: Revision");
      f.setUpperCase();
      f.setReadOnly();

      f = headblk.addField("DT_DOC_REV","Date");
      f.setSize(7);
      f.setLabel("DOCMAWDOCISSUEGENERALDTDOCREV: Rev Date");
      f.setReadOnly();

      f = headblk.addField("REV_NO","Number");
      f.setSize(5);
      f.setMandatory();
      f.setLabel("DOCMAWDOCISSUEGENERALREVNO: Rev No");
      f.setReadOnly();

      f = headblk.addField("DOC_REV_TEXT");
      f.setSize(28);
      f.setMaxLength(2000);
      f.setLabel("DOCMAWDOCISSUEGENERALDOCREVTEXT: Rev Text");
      f.setReadOnly();

      f = headblk.addField("INFO");
      f.setSize(28);
      f.setMaxLength(2000);
      f.setLabel("DOCMAWDOCISSUEGENERALINFO: Note");

      f = headblk.addField("SEDMSTATUS");
      f.setSize(28);
      f.setMaxLength(253);
      f.setLabel("DOCMAWDOCISSUEGENERALSEDMSTATUS: File State");
      f.setFunction("Edm_File_API.Get_Document_State(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')");
      mgr.getASPField("DOC_REV").setValidation("SEDMSTATUS");
      f.setReadOnly();

      f = headblk.addField("DOC_RESP_DEPT");
      f.setSize(9);
      f.setMaxLength(4);
      f.setLabel("DOCMAWDOCISSUEGENERALDOCRESPDEPT: Department");
      f.setUpperCase();

      f = headblk.addField("DOC_RESP_SIGN");
      f.setSize(9);
      f.setMaxLength(30);
      f.setLabel("DOCMAWDOCISSUEGENERALDOCRESPSIGN: Person");

      f = headblk.addField("STATE");
      f.setSize(8);
      f.setMaxLength(253);
      f.setMandatory();
      f.setLabel("DOCMAWDOCISSUEGENERALDOCSTATUS: State");
      f.setReadOnly();

      f = headblk.addField("DT_RELEASED","Date");
      f.setSize(7);
      f.setLabel("DOCMAWDOCISSUEGENERALDTRELEASED: Date Released");
      f.setReadOnly();

      f = headblk.addField("DT_OBSOLETE","Date");
      f.setSize(7);
      f.setLabel("DOCMAWDOCISSUEGENERALDTOBSOLETE: Date Obsolete");
      f.setReadOnly();

      f = headblk.addField("LANGUAGE_CODE");
      f.setSize(5);
      f.setDynamicLOV("APPLICATION_LANGUAGE");
      f.setLOVProperty("TITLE","List of Language");
      f.setLabel("DOCMAWDOCISSUEGENERALLANGUAGECODE: Language");

      f = headblk.addField("SLANGUAGEDESC");
      f.setSize(10);
      f.setMaxLength(2);
      f.setLabel("DOCMAWDOCISSUEGENERALSLANGUAGEDESC: Language Desc");
      f.setFunction("APPLICATION_LANGUAGE_API.Get_Description(:LANGUAGE_CODE)");
      mgr.getASPField("LANGUAGE_CODE").setValidation("SLANGUAGEDESC");
      f.setReadOnly();

      f = headblk.addField("FORMAT_SIZE");
      f.setSize(5);
      f.setMaxLength(6);
      f.setDynamicLOV("DOC_CLASS_FORMAT_LOV","DOC_CLASS");
      f.setLabel("DOCMAWDOCISSUEGENERALFORMATSIZE: Format");
      f.setUpperCase();

      f = headblk.addField("SFORMATSIZEDESC");
      f.setSize(10);
      f.setMaxLength(2000);
      f.setLabel("DOCMAWDOCISSUEGENERALSFORMATSIZEDESC: Format Desc");
      f.setFunction("DOC_FORMAT_API.Get_Description(:FORMAT_SIZE)");
      mgr.getASPField("FORMAT_SIZE").setValidation("SFORMATSIZEDESC");
      f.setReadOnly();

      f = headblk.addField("ACCESS_CONTROL");
      f.setSize(16);
      f.setMaxLength(20);
      f.setMandatory();
      f.setLabel("DOCMAWDOCISSUEGENERALACCESSCONTROL: Access");
      f.setReadOnly();

      f = headblk.addField("SCALE");
      f.setSize(8);
      f.setMaxLength(5);
      f.setDynamicLOV("DOC_SCALE_LOV1");
      f.setLabel("DOCMAWDOCISSUEGENERALSCALE: Scale");

      f = headblk.addField("SACCESSCODE");
      f.setSize(2);
      f.setHidden();
      f.setFunction("DOC_USER_ACCESS_API.Encode(:ACCESS_CONTROL)");
      mgr.getASPField("DOC_REV").setValidation("SACCESSCODE");
      f.setReadOnly();

      f = headblk.addField("SCHANGEACCESSCODE");
      f.setSize(5);
      f.setHidden();
      f.setFunction("DOC_ISSUE_API.Check_Set_Access_Control__(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");
      mgr.getASPField("ACCESS_CONTROL").setValidation("SCHANGEACCESSCODE");
      f.setReadOnly();

      f = headblk.addField("ATTR");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("ACTION");
      f.setHidden();
      f.setFunction("''");
      f.setReadOnly();

      headblk.setView("DOC_ISSUE");
      headblk.defineCommand("DOC_ISSUE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.BACK);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.FIND);
      headbar.disableCommand(headbar.FORWARD);
      headbar.disableCommand(headbar.BACKWARD);
      headbar.addCustomCommand("startApproval",mgr.translate("DOCMAWDOCISSUEGENERALSTARTAPPROVE: Start Approval"));
      headbar.addCustomCommand("setApproved",mgr.translate("DOCMAWDOCISSUEGENERALAPPDOC: Approve"));
      headbar.addCustomCommand("setReleased",mgr.translate("DOCMAWDOCISSUEGENERALRELDOC: Release"));
      headbar.addCustomCommand("setObsolete",mgr.translate("DOCMAWDOCISSUEGENERALSETOBS: Set Obsolete"));
      headbar.addCustomCommand("newRevision",mgr.translate("DOCMAWDOCISSUEGENERALNEWREV: Create New Revision"));
      headbar.addCustomCommand("activateGeneral","General");

      headtbl = mgr.newASPTable(headblk);

      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);

      //---------------------------------------------------------------------------------------------------
      // Tabs

      tabs = mgr.newASPTabContainer();
      tabs.addTab("DOCMAWDOCISSUEGENERALGENERAL: General","javascript:commandSet('HEAD.activateGeneral','')");
      tabs.setLeftTabSpace(3);
      tabs.setContainerSpace(6);
      tabs.setTabWidth(125);
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      headbar.removeCustomCommand("activateGeneral");

      bodyTag = mgr.generateBodyTag();
   }


   public void  tabsInit()
   {
      ASPManager mgr = getASPManager();

      tabs.setActiveTab(activetab+1);
      mgr.responseWrite(tabs.showTabsInit());
   }


   public void  tabsFinish()
   {
      ASPManager mgr = getASPManager();

      mgr.responseWrite(tabs.showTabsFinish());
   }


   public void  activateGeneral()
   {
      activatetab="0";
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "";
   }

   protected String getTitle()
   {
      return "";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      tabsInit();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCISSUEGENERALTITLE: Document General"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(bodyTag);
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("  <input type=\"hidden\" name=\"CONFIRM\" value>");

      out.append("  <input type=\"hidden\" name=\"DOC_CLASS_FROM_WIZ\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"DOC_NO_FROM_WIZ\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"DOC_SHEET_FROM_WIZ\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"DOC_REV_FROM_WIZ\" value=\"\">\n");

      out.append(headlay.show());


      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------


      if (bOpenWizardWindow)
      {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(sUrl);
         appendDirtyJavaScript("\",\"anotherWindow\",\"status, resizable, scrollbars, width=727, height=612, left=100, top=100\");\n");
      }

      appendDirtyJavaScript("function getPageName()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" return \"docissuegeneral\"\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function refeshParent(doc_class, doc_no, doc_sheet, doc_rev)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.DOC_CLASS_FROM_WIZ.value=doc_class\n");
      appendDirtyJavaScript(" document.form.DOC_NO_FROM_WIZ.value=doc_no\n");
      appendDirtyJavaScript(" document.form.DOC_SHEET_FROM_WIZ.value=doc_sheet\n");
      appendDirtyJavaScript(" document.form.DOC_REV_FROM_WIZ.value=doc_rev\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function refreshTree()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("      this.parent.frames[\"childnavwinfrm\"].refresh('FALSE');\n");
      appendDirtyJavaScript("}\n");

      if (confirm)
      {
         appendDirtyJavaScript("displayConfirmBox();");
      }
      appendDirtyJavaScript("function displayConfirmBox()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   unconfirm = (readCookie(f.__PAGE_ID.value).length>1);\n");
      appendDirtyJavaScript("   onLoad();\n");
      appendDirtyJavaScript("   if ((!unconfirm)&&(document.form.CONFIRM.value==\"\"))\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (confirm('");
      appendDirtyJavaScript(message);
      appendDirtyJavaScript("'))   \n");
      appendDirtyJavaScript("         document.form.CONFIRM.value='OK';\n");
      appendDirtyJavaScript("      else\n");
      appendDirtyJavaScript("         document.form.CONFIRM.value='CANCEL';\n");
      appendDirtyJavaScript("      submit();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else \n");
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
      for (int i=0;i<a;i++)
         if (mainString.startsWith(subString,i))
         {
            index=i;
            break;
         }
      return index;
   }

   private String replaceString(String mainString,String subString,String changeString)
   {
      int posi;
      posi = stringIndex(mainString, subString);
      while (posi!=-1)
      {
         mainString=mainString.substring(0,posi)+ changeString + mainString.substring(posi+subString.length(),mainString.length());
         posi = stringIndex(mainString, subString);
      }

      return mainString;

   }//repstring

}
