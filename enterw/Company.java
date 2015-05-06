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
 *  File                    : Company.java
 *  Description             :
 *  Notes                   :
 * --------------------------------- Wings Merge  Start ------------------------------------
 *  Created    : 2006-11-08   Haunlk   Created.
 *  Modify     : 2007-01-17   Haunlk   Modified Some of the Transation Constants.
 *               2007-01-22   Haunlk   B129741 Added the disableDocMan to disable the RMB option "Document".
 *               2007-01-22   Haunlk   B129737 Modified the enumerateValues at HEAD_COUNTRY and DEFAULT_LANGUAGE.
 *               2007-01-26   Haunlk   Renamed the FormOfBusinessLov.page with upperCase "F".
 *               2007-01-31   Haunlk   Merged Wings Code.
 * --------------------------------- Wings Merge End ------------------------------------
 * ----------------------------------------------------------------------------
*/
package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class Company extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.Company");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext      ctx;
   private ASPLog log;
   private ASPBlock        headblk;
   private ASPRowSet       headset;
   private ASPCommandBar   headbar;
   private ASPTable        headtbl;
   private ASPBlockLayout  headlay;



   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery qry;
   private int i;
   private int arraySize;

   private String val;
   private ASPCommand cmd;
   private ASPBuffer buf;
   private ASPQuery q;
   private ASPBuffer data;

   //===============================================================
   // Construction
   //===============================================================
   public Company(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {

      ASPManager mgr = getASPManager();
      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());

      adjust();

   }

   public void getClientValues()
   {
      ASPManager mgr = getASPManager();
      trans.clear();

      cmd = trans.addCustomCommand("COLCOMPLEX","Address_Type_Code_API.Enumerate_Type");
      cmd.addParameter("CLIENTVALUE");
      cmd.addParameter("PARTY_TYPE_DB","COMPANY");

      cmd = trans.addCustomFunction("GETLANG","LANGUAGE_SYS.Get_Language()","METHOD");

      trans = mgr.perform(trans);
   }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(headblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);
      if ( headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("ENTERWCOMPANYNODATA: No data found."));
         headset.clear();
      }
      mgr.createSearchURL(headblk);
      eval(headset.syncItemSets());
   }

   public void  SaveReturnHEAD() throws FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      String sAsNoBefore = (mgr.isEmpty(headset.getValue("ASSOCIATION_NO")) ? "" : headset.getValue("ASSOCIATION_NO"));
      headset.changeRow();
      String sAsNoAfter = (mgr.isEmpty(headset.getValue("ASSOCIATION_NO")) ? "" : headset.getValue("ASSOCIATION_NO"));

      if ( !sAsNoBefore.equals(sAsNoAfter) )
      {
         // get the conformation from the user.
         trans.clear();
         cmd = trans.addCustomFunction( "GETASSOCI", "Association_Info_API.Association_No_Exist", "METHODS" );
         cmd.addParameter("ASSOCIATION_NO",headset.getValue("ASSOCIATION_NO"));
         cmd.addParameter("COMPANY","COMPANY");
         trans = mgr.perform(trans);
         String getAssosiationExists = trans.getValue("GETASSOCI/DATA/METHODS");
         if ( "TRUE".equals(getAssosiationExists) )
         {
            String message = mgr.translate("ENTERWCOMPANYANOTHERBUSINESSINFO: Another business partner with the same association number is already registered. Do you want to use the same Association No?");
            askUserAndPerform("_Ifs_Applications_Enterw_CookieName_", message, "HEAD.showWarningOk","HEAD.showWarningCancel");
            return;
         }
      }
      else
      {
         mgr.submit(trans);
      }
   }

   public void showWarningOk()
   {
      ASPManager mgr = getASPManager();
      // this is to complete the saveReturnHead after getting the user confermation.
      mgr.submit(trans);
      return;
   }

   public void showWarningCancel()
   {
      ASPManager mgr = getASPManager();
      headlay.setLayoutMode(2);
      if ( headset.countRows() > 0 )
      {
         headset.setValue("ASSOCIATION_NO","");
      }
      return;
   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      if ( headset.countRows() == 0 )
      {
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DUPLICATEROW);
      }
   }

   protected void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("COMPANY").
      setUpperCase().
      setSize(10).
      setLabel("ENTERWCOMPANYCOMPANY: Identity").
      setReadOnly();

      headblk.addField("PARTY_TYPE_DB").
      setHidden();

      headblk.addField("NAME").
      setLabel("ENTERWCOMPANYNAME: Name").
      setSize(40);

      headblk.addField("ASSOCIATION_NO").
      setLabel("ENTERWCOMPANYASSOCIATIONNO: Association No").
      setSize(20).
      setDynamicLOV("ASSOCIATION_INFO","PARTY_TYPE_DB");

      headblk.addField("CREATED_BY").
      setLabel("ENTERWCOMPANYCREATEDBY: Created By").
      setReadOnly().
      setSize(10);

      headblk.addField("METHODS").
      setHidden().
      setFunction("''");

      headblk.addField("CREATION_FINISHED").
      setHidden().
      setFunction("Company_Finance_API.Get_Creation_Finished(:COMPANY)");

      //------------ General Tab ------------------------------

      headblk.addField("DEFAULT_LANGUAGE").
      setSize(20).
      setMandatory().
      setLabel("ENTERWCOMPANYDEFAULTLANGUAGE: Default Language").
      setSelectBox().
      enumerateValues("ISO_LANGUAGE_API");

      headblk.addField("HEAD_COUNTRY").
      setSize(20).setDbName("COUNTRY").
      setMandatory().
      setLabel("ENTERWCOMPANYHEADCOUNTRY: Country").
      setSelectBox().
      enumerateValues("ISO_COUNTRY_API");

      headblk.addField("COUNTRY_DB").
      setHidden();

      headblk.addField("FORM_BUSINESS").
      setLabel("ENTERWCOMPANYFORMOFBUSINESS: Form of Business").
      setSize(20).
      setDbName("CORPORATE_FORM").
      setLOV("FormOfBusinessLov.page","COUNTRY_DB",600,445);


      headblk.addField("CORPORATE_FORM_DES").
      setLabel("ENTERWCOMPANYCORPORATEDES: Form of Business Description").
      setReadOnly().
      setSize(30).
      setFunction("CORPORATE_FORM_API.Get_Corporate_Form_Desc(:COUNTRY_DB, :FORM_BUSINESS)");

      mgr.getASPField("FORM_BUSINESS").setValidation("CORPORATE_FORM_DES");

      headblk.addField("CREATION_DATE","Date").
      setLabel("ENTERWCOMPANYCREATIONDATE: Creation Date").
      setReadOnly().
      setSize(15);

      headblk.addField("AUTHORIZATION_ID").
      setLabel("ENTERWCOMPANYAUTHORIZATIONID: Authorization ID").
      setSize(20);

      headblk.addField("IDENTIFIER_REFERENCE").
      setLabel("ENTERWCOMPANYIDENTIFIERREFERENCE: Identifier Reference").
      setSize(20);

      headblk.addField("LOGOTYPE").
      setLabel("ENTERWCOMPANYLOGOTYPE: Document Logotype").
      setSize(20);


      headblk.addField("ACTIVITY_START_DATE","Date").
      setLabel("ENTERWCOMPANYACTIVITYSTARTDATE: Activiry Start Date").
      setSize(15);

      headblk.addField("AUTH_ID_EXPIRE_DATE","Date").
      setLabel("ENTERWCOMPANYAUTHIDEXPIREDATE: Authorization ID Expiration Date").
      setSize(15);

      headblk.addField("FROM_COMPANY").
      setLabel("ENTERWCOMPANYFROMCOMPANY: Source Company").
      setSize(15).
      setReadOnly();

      headblk.addField("FROM_TEMPLATE_ID").
      setLabel("ENTERWCOMPANYFROMTEMPLATEID: Source Template ID").
      setSize(15).
      setReadOnly();

      headblk.addField("TEMPLATE_COMPANY").
      setLabel("ENTERWCOMPANYTEMPLATECOMPANY: Template Company").
      setCheckBox("FALSE,TRUE").
      setReadOnly();

      headblk.addField("IDENTIFIER_REF_VALIDATION").
      setSize(20).
      setMandatory().
      setLabel("ENTERWCOMPANYIDENTIFIERVALIDATION: Identifier Ref Validation").
      setSelectBox().
      enumerateValues("IDENTIFIER_REF_VALIDATION_API.Enumerate"," ");


      headblk.setView("COMPANY");
      headblk.defineCommand("COMPANY_API","Modify__");
      headblk.disableDocMan();

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(ASPCommandBar.DELETE);
      headbar.defineCommand(headbar.SAVERETURN,"SaveReturnHEAD");
      headbar.addCustomCommand("showWarningOk","");
      headbar.disableCommand("showWarningOk");
      headbar.defineCommand(headbar.COUNTFIND,"countFind");

      headbar.addCustomCommand("showWarningCancel","");
      headbar.disableCommand("showWarningCancel");

      headtbl = mgr.newASPTable( headblk );
      headtbl.setTitle(mgr.translate("ENTERWCOMPANYTITLE: Company"));

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);

      headlay.setDialogColumns(4);
      headlay.setDataSpan("DEFAULT_LANGUAGE",2);
      headlay.defineGroup("Main","COMPANY,NAME,ASSOCIATION_NO,CREATED_BY,COUNTRY_DB,PARTY_TYPE_DB",false,true);
      headlay.defineGroup(mgr.translate("ENTERWCOMPANYDETAILGRP: Detail"),"DEFAULT_LANGUAGE,HEAD_COUNTRY,LOGOTYPE,FORM_BUSINESS,CORPORATE_FORM_DES,CREATION_DATE,ACTIVITY_START_DATE,AUTHORIZATION_ID,AUTH_ID_EXPIRE_DATE,FROM_COMPANY,FROM_TEMPLATE_ID,TEMPLATE_COMPANY,IDENTIFIER_REFERENCE,IDENTIFIER_REF_VALIDATION",true,false,2);

      appendJavaScript("function askAndPerformAtClient(sCookieName,sMsg,sYesAction,sNoAction)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var sCookieValue = readCookie(sCookieName);\n");
      appendJavaScript("   removeCookie(sCookieName,APP_PATH);\n");
      appendJavaScript("   if ( sCookieValue != '' )\n");
      appendJavaScript("      if (confirm(sMsg))\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (sYesAction != '')\n");
      appendJavaScript("            commandSet(sYesAction, '');\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (sNoAction != '')\n");
      appendJavaScript("            commandSet(sNoAction, '');\n");
      appendJavaScript("      }\n");
      appendJavaScript("}\n");

   }

   private void askUserAndPerform(String sCookieName, String sMessage, String sYesAction,String sNoAction) throws FndException
   {
      ctx.setCookie(sCookieName, "*");

      appendDirtyJavaScript("askAndPerformAtClient('", sCookieName, "','");
      appendDirtyJavaScript(sMessage, "','");
      appendDirtyJavaScript(sYesAction, "','");
      appendDirtyJavaScript(sNoAction, "');\n");

   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWCOMPANYDESCRIPTION: Company";
   }

   protected String getTitle()
   {
      return "ENTERWCOMPANYTITLEDESC: Company";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(headlay.show());
   }
}
