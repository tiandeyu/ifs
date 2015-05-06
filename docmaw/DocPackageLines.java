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
*  File        : DocPackageLines.java
*  Created     :
*    2001-09-10   Bakalk -for Web Alingment.
*    2006-10-13   NIJALK  Bug 61028, Set the max length to DOC_SHEET.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocPackageLines extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocPackageLines");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext       ctx;   
   private ASPBlock       headblk;
   private ASPRowSet      headset;
   private ASPCommandBar  headbar;
   private ASPTable       headtbl;
   private ASPBlockLayout headlay;

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

   private ASPTransactionBuffer trans;
   private ASPCommand           cmd;
   private ASPQuery             q;
   private ASPBuffer            data;
   private ASPBuffer            keys;

   //===============================================================
   // Construction
   //===============================================================
   public DocPackageLines(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   

   public void run()
   {
      ASPManager mgr = getASPManager();
      ctx     = mgr.getASPContext();

      trans = mgr.newASPTransactionBuffer();
      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      

      adjust();

   }

   //=============================================================================
   //   VALIDATE FUNCTION
   //=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");
      //u can find any value here

      mgr.endResponse();
   }

   //=============================================================================
   //   CMDBAR FUNCTIONS
   //=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      searchURL = mgr.createSearchURL(headblk);


      trans.clear();
      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
      {
         ASPBuffer buff = mgr.getTransferedData();
         q.addOrCondition(buff);
      }

      //q.addOrCondition( "DOC_CLASS='"+mgr.readValue("DOC_CLASS")+"' AND DOC_NO='"+ mgr.readValue("DOC_NO")+"'");

      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCPACKAGELINESNODATA: No data found."));
         headset.clear();
      }
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



   //=============================================================================
   //   CMDBAR CUSTOM FUNCTIONS
   //=============================================================================

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      headblk = mgr.newASPBlock("HEAD");

      headblk.disableDocMan();

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      
      headblk.addField("PACKAGE_NO").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_PACKAGE_ID").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDocPackageLinespackagelist: List of Document Packages")).
      setLabel("DOCMAWDOCPACKAGELINESPACKAGENO: Package No");

      headblk.addField("PACKAGE_DESC").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setFunction("DOC_PACKAGE_ID_API.Get_Description(PACKAGE_NO)").
      setLabel("DOCMAWDOCPACKAGELINESPACKAGEDESC: Package Description");

      headblk.addField("PACKAGE_ORDER_NO").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setHidden();
      //setLabel("DOCMAWDOCPACKAGELINESPACKAGENO: Package No");


      headblk.addField("MILESTONE_NO").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_MILESTONE","MILESTONE_PROFILE").
      setLabel("DOCMAWDOCPACKAGELINESMILESNO: Milestone No");


      headblk.addField("MILESTONE_PROGRESS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setFunction("DOC_MILESTONE_API.Get_Progress(MILESTONE_PROFILE,MILESTONE_NO)").
      setLabel("DOCMAWDOCPACKAGELINESCUMULPROG: Cumulative Progress");


      
      headblk.addField("MILESTONE_DURATION").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setFunction("DOC_MILESTONE_API.Get_Duration(milestone_profile,MILESTONE_NO)").
      setLabel("DOCMAWDOCPACKAGELINESCUMULDURATN: Cumulative Duration");


      headblk.addField("PROGRESS_WEIGHT").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setLabel("DOCMAWDOCPACKAGELINESPLANNEDH: Planned Hours");

      headblk.addField("DESCRIPTION").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setLabel("DOCMAWDOCPACKAGELINESDESC: Doc Package Line Description");


      headblk.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWDOCCLASS1: List of Document Class")).
      setLabel("DOCMAWDOCPACKAGELINESDOCCLASS: Doc Class");

      
      headblk.addField("DOC_NO").
      setSize(20).
      setUpperCase().
      setDynamicLOV("DOC_TITLE","DOC_CLASS").
      setLabel("DOCMAWDOCPACKAGELINESDOCNO: Doc No");


      headblk.addField("DOC_SHEET").
      setSize(20).
      //Bug 61028, Start
      setMaxLength(10).
      //Bug 61028, End
      setUpperCase().
      setLabel("DOCMAWDOCPACKAGELINESDOCSHEET: Doc Sheet");



      headblk.addField("DOC_REV").
      setSize(20).
      setUpperCase().
      setDynamicLOV("DOC_ISSUE","DOC_CLASS,DOC_NO,DOC_SHEET").
      setLabel("DOCMAWDOCPACKAGELINESDOCREV: Doc Rev");

      headblk.addField("ASSOCIATED_REVISION_TYPE").
      setSize(20).
      setUpperCase().
      setLabel("DOCMAWDOCPACKAGELINESASSOREVTYPE: Associated Revision Type");

      headblk.addField("LATEST_REVISION").
      setSize(20).
      setUpperCase().
      setLabel("DOCMAWDOCPACKAGELINESLATESTREV: Latest Revision");

      
      headblk.addField("MILESTONE_PROFILE").
      setSize(20).
      setUpperCase().
      setDynamicLOV("DOC_MILESTONE_PROFILE").
      setLabel("DOCMAWDOCPACKAGELINESMILEPROFILE: Milestone Profile");


      headblk.addField("DATE_CHANGED","Date").
      setSize(20).
      setUpperCase().
      setLabel("DOCMAWDOCPACKAGELINESDATECHANGED: Date Changed");

      headblk.addField("USER_CHANGED").
      setSize(20).
      setUpperCase().
      setDynamicLOV("PERSON_INFO_USER").
      setLabel("DOCMAWDOCPACKAGELINESUSERCHANGED: User Changed");


      headblk.addField("DATE_CREATED","Date").
      setSize(20).
      setUpperCase().
      setLabel("DOCMAWDOCPACKAGELINESDATECREATED: Date Created");


      headblk.addField("USER_CREATED").
      setSize(20).
      setUpperCase().
      setDynamicLOV("PERSON_INFO_USER").
      setLabel("DOCMAWDOCPACKAGELINESUSERCREATED: User Created");


      
      headblk.addField("RESPONSIBLE").
      setSize(20).
      setUpperCase().
      setFunction("DOC_PACKAGE_ID_API.Get_Responsible(PACKAGE_NO)").
      setLabel("DOCMAWDOCPACKAGELINESRESPONSIBLE: Responsible"); 


      headblk.setView("DOC_PACKAGE_TEMPLATE_EXTRA");
      //headblk.defineCommand("DOC_TITLE_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      
      headbar.addCustomCommand("goToDocPackage",mgr.translate("DOCMAWDOCTITLEOVWDOCPACKAGE: Document Package..."));
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCPACKAGELINESOVEDOCPACKLINES: Overview - Document Package Lines"));
      headtbl.enableRowSelect();
      
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      
    }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows()<= 0) {
         
         headbar.disableMultirowAction();
         headlay.setLayoutMode(headlay.FIND_LAYOUT);
      }
      else{
         headbar.enableMultirowAction();
      }


   }


   public void goToDocPackage(){
      ASPManager mgr = getASPManager();
      if (headlay.isMultirowLayout()) {
         headset.storeSelections();
      }
      else{
         headset.selectRow();
      }
      mgr.transferDataTo("DocPackageId.page",headset.getSelectedRows("PACKAGE_NO"));

   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCPACKAGELINESTITLE: Overview - Document Package Lines";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCPACKAGELINESTITLE: Overview - Document Package Lines";
   }

   protected void printContents() throws FndException
   {
      appendToHTML(headlay.show());
      
   }

}
