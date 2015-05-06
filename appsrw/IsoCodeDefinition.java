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
*  File        : IsoCodeDefinition.java 
*  Modified    :
*                 2003-12-16  - Gacolk - Web Alignment: Converted the 4 blocks to tabs and removed
*                                        obsolete methods.
*                 2003-10-12  - Zahalk - Call ID 106477, Did some changes to show all the records.
*  ASP2JAVA Tool  2001-03-12  - VAGU   - Created Using the ASP file IsoCodeDefinition.asp
* ----------------------------------------------------------------------------
 * 04052007 rahelk - call id 143002. Added missing columns and added LOVs to them
 * ----------------------------------------------------------------------------
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class IsoCodeDefinition extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.IsoCodeDefinition");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPBlock itemblk1;
   private ASPRowSet itemset1;
   private ASPCommandBar itembar1;
   private ASPTable itemtbl1;
   private ASPBlockLayout itemlay1;
   private ASPBlock itemblk2;
   private ASPRowSet itemset2;
   private ASPCommandBar itembar2;
   private ASPTable itemtbl2;
   private ASPBlockLayout itemlay2;
   private ASPBlock itemblk3;
   private ASPRowSet itemset3;
   private ASPCommandBar itembar3;
   private ASPTable itemtbl3;
   private ASPBlockLayout itemlay3;
   private ASPField f;

   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   //===============================================================
   // Construction 
   //===============================================================
   public IsoCodeDefinition(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();
   
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFind();  
   	 okFindITEM1(); 
   	 okFindITEM2(); 
   	 okFindITEM3(); 
      }
      adjust();
      tabs.saveActiveTab();
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  saveReturnITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = itemset3.getCurrentRowNo();
	  
      itemset3.changeRow();
   
      String lsAttr ="DESCRIPTION" + (char)31 + mgr.readValue("ITEM3_DESCRIPTION") + (char)30 + "USED_IN_APPL" + (char)31 + mgr.readValue("ITEM3_USED_IN_APPL") + (char)30 ;
		
      ASPCommand cmd = trans.addCustomCommand("MODIFY", "ISO_UNIT_API.Modify__"); 
      cmd.addParameter("INFO");    
      cmd.addParameter("OBJID", itemset3.getRow().getValue("OBJID"));
      cmd.addParameter("OBJVERSION", itemset3.getRow().getValue("OBJVERSION"));
      cmd.addParameter("ATTR",lsAttr);
      cmd.addParameter("ACTION","DO");

      mgr.perform(trans);		
   
      okFind1ITEM3();
   
      itemset3.goTo(currrow);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int itemrrow1 = itemset1.getCurrentRowNo();
      int itemrrow2 = itemset2.getCurrentRowNo();
      int itemrrow3 = itemset3.getCurrentRowNo();
   
      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
   
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWISOCODEDEFINITIONNODATA: No data found."));
         headset.clear();
      } 
   
      itemset1.goTo(itemrrow1);
      itemset2.goTo(itemrrow2);
      itemset3.goTo(itemrrow3);
   }


   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();   
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
      itemset1.clear();
   }


   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();
      int itemrrow2 = itemset2.getCurrentRowNo();
      int itemrrow3 = itemset3.getCurrentRowNo();
 
      ASPQuery q = trans.addQuery(itemblk1);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk1);
   
      if ( itemset1.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWISOCODEDEFINITIONNODATA: No data found."));
         itemset1.clear();
      }
      headset.goTo(currrow);
      itemset2.goTo(itemrrow2);
      itemset3.goTo(itemrrow3);
   }


   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
      itemset2.clear();
   }


   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();
      int itemrrow1 = itemset1.getCurrentRowNo();
      int itemrrow3 = itemset3.getCurrentRowNo();
      
      ASPQuery q = trans.addQuery(itemblk2);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk2);
   
      if ( itemset2.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWISOCODEDEFINITIONNODATA: No data found."));
         itemset2.clear();
      }
      headset.goTo(currrow);
      itemset1.goTo(itemrrow1);
      itemset3.goTo(itemrrow3);
   
   }


   public void  countFindITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
      itemset3.clear();
   }


   public void  okFindITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();
      int itemrrow1 = itemset1.getCurrentRowNo();
      int itemrrow2 = itemset2.getCurrentRowNo();
      
      ASPQuery q = trans.addQuery(itemblk3);
      q.setOrderByClause("BASE_UNIT");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk3);
   
      if ( itemset3.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWISOCODEDEFINITIONNODATA: No data found."));
         itemset3.clear();
      }
      headset.goTo(currrow);
      itemset1.goTo(itemrrow1);
      itemset2.goTo(itemrrow2);
   }


   public void  okFind1ITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();
      int itemrrow1 = itemset1.getCurrentRowNo();
      int itemrrow2 = itemset2.getCurrentRowNo();

      ASPQuery q = trans.addEmptyQuery(itemblk3);
      q.setOrderByClause("BASE_UNIT");
      q.includeMeta("ALL");
      mgr.submit(trans);
   
      if ( itemset3.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWISOCODEDEFINITIONNODATA: No data found."));
         itemset3.clear();
      }
      headset.goTo(currrow);
      itemset1.goTo(itemrrow1);
      itemset2.goTo(itemrrow2);
   }

   public void activateCountry()
   {   
      tabs.setActiveTab(1);   
   }

   public void activateCurrency()
   {   
      tabs.setActiveTab(2);   
   }

   public void activateLanguage()
   {   
      tabs.setActiveTab(3);   
   }

   public void activateUnitOfMeasure()
   {   
      tabs.setActiveTab(4);   
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("COUNTRY_CODE");
      f.setSize(15);
      f.setMandatory();
      f.setReadOnly();
      f.setMaxLength(2);
      f.setLabel("APPSRWISOCODEDEFINITIONCOUNTRYCODE: Country Code");
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(25);
      f.setMandatory();
      f.setMaxLength(50);
      f.setLabel("APPSRWISOCODEDEFINITIONDESCRIPTION: Description");
   
      f = headblk.addField("FULL_NAME");
      f.setSize(25);
      f.setMaxLength(100);
      f.setLabel("APPSRWISOCODEDEFINITIONFULLNAME: Country Name");
   
      f = headblk.addField("USED_IN_APPL");
      f.setCheckBox("FALSE,TRUE");
      f.setSize(30);
      f.setLabel("APPSRWISOCODEDEFINITIONUSEDINAPPL: Used in Application");
   
      f = headblk.addField("EU_MEMBER_DB");
      f.setCheckBox("N,Y");
      f.setSize(20);
      f.setLabel("APPSRWISOCODEDEFINITIONEUMEMBERDB: EU Member");
      
      f = headblk.addField("DEFAULT_LOCALE");
      f.setSize(10);
      f.setMaxLength(25);
      f.setDynamicLOV("LANG_CODE_RFC3066");
      f.setLabel("APPSRWISOCODEDEFINITIONDEFLOCALE: Default Locale");
   
      headblk.setView("ISO_COUNTRY_DEF");
      headblk.defineCommand("ISO_COUNTRY_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);  

      headbar.addCustomCommand("activateCountry","Country");
      headbar.addCustomCommand("activateCurrency","Currency");
      headbar.addCustomCommand("activateLanguage","Language");
      headbar.addCustomCommand("activateUnitOfMeasure","Unit Of Measure");

      headbar.removeCustomCommand("activateCountry");
      headbar.removeCustomCommand("activateCurrency");
      headbar.removeCustomCommand("activateLanguage");
      headbar.removeCustomCommand("activateUnitOfMeasure");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWISOCODEDEFINITIONHD: Country"));  
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(1);
   
   //-----------------------------------------------------------------------
   //-------------- This part belongs to CURRENCY TAB ------------------------
   //-----------------------------------------------------------------------
   
      itemblk1 = mgr.newASPBlock("ITEM1");
   
      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk1.addField("CURRENCY_CODE");
      f.setSize(30);
      f.setMandatory();
      f.setReadOnly();
      f.setMaxLength(3);
      f.setLabel("APPSRWISOCODEDEFINITIONCURRENCYCODE: Currency Code"); 
   
      f = itemblk1.addField("ITEM1_DESCRIPTION");
      f.setSize(44);
      f.setMandatory();
      f.setMaxLength(50);
      f.setDbName("DESCRIPTION");
      f.setLabel("APPSRWISOCODEDEFINITIONITEM1DESCRIPTION: Description");
      f.setReadOnly();
      f.setInsertable(); 
   
      f = itemblk1.addField("ITEM1_USED_IN_APPL");
      f.setCheckBox("FALSE,TRUE");
      f.setSize(30);
      f.setDbName("USED_IN_APPL");
      f.setLabel("APPSRWISOCODEDEFINITIONITEM1USEDINAPPL: Used in Application");

      f = itemblk1.addField("DEFAULT_COUNTRY");
      f.setSize(25);
      f.setMaxLength(50);
      f.setDynamicLOV("ISO_COUNTRY");
      f.setLabel("APPSRWISOCODEDEFINITIONDEFCOUNTRY: Default Country");
   
      itemblk1.setView("ISO_CURRENCY_DEF");
      itemblk1.defineCommand("ISO_CURRENCY_API","New__,Modify__,Remove__");
   
      itemset1 = itemblk1.getASPRowSet();
   
      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
   
      itembar1.disableCommand(itembar1.DELETE);
      itembar1.disableCommand(itembar1.NEWROW);
      itembar1.disableCommand(itembar1.DUPLICATEROW);  
   
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("APPSRWISOCODEDEFINITIONITM1: Currency"));  
   
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      itemlay1.setDialogColumns(1);
   
   //-----------------------------------------------------------------------
   //-------------- This part belongs to LANGUAGE TAB ------------------------
   //-----------------------------------------------------------------------	
   
      itemblk2 = mgr.newASPBlock("ITEM2");
   
      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk2.addField("LANGUAGE_CODE");
      f.setSize(30);
      f.setMandatory();
      f.setReadOnly();
      f.setMaxLength(2);
      f.setLabel("APPSRWISOCODEDEFINITIONLANGUAGECODE: Language Code");
   
      f = itemblk2.addField("ITEM2_DESCRIPTION");
      f.setSize(44);
      f.setMandatory();
      f.setMaxLength(50);
      f.setDbName("DESCRIPTION");
      f.setLabel("APPSRWISOCODEDEFINITIONITEM2DESCRIPTION: Description");
   
      f = itemblk2.addField("ITEM2_USED_IN_APPL");
      f.setCheckBox("FALSE,TRUE");
      f.setSize(30);
      f.setDbName("USED_IN_APPL");
      f.setLabel("APPSRWISOCODEDEFINITIONITEM2USEDINAPPL: Used in Application");
   
      itemblk2.setView("ISO_LANGUAGE_DEF");
      itemblk2.defineCommand("ISO_LANGUAGE_API","New__,Modify__,Remove__");
   
      itemset2 = itemblk2.getASPRowSet();
   
      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
   
      itembar2.disableCommand(itembar2.DELETE);
      itembar2.disableCommand(itembar2.NEWROW);
      itembar2.disableCommand(itembar2.DUPLICATEROW);  
   
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("APPSRWISOCODEDEFINITIONITM2: Language"));  
   
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
      itemlay2.setDialogColumns(1);
   
   //-----------------------------------------------------------------------
   //-------------- This part belongs to UNITS OF MEASURE TAB ------------------------
   //-----------------------------------------------------------------------
   
   
      itemblk3 = mgr.newASPBlock("ITEM3");
   
      f = itemblk3.addField("ITEM3_OBJID");
      f.setDbName("OBJID");
      f.setHidden();
   
      f = itemblk3.addField("ITEM3_OBJVERSION");
      f.setDbName("OBJVERSION");
      f.setHidden();
   
      f = itemblk3.addField("UNIT_CODE");
      f.setSize(50);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(30);
      f.setLabel("APPSRWISOCODEDEFINITIONUNITCODE: Unit Code");
   
      f = itemblk3.addField("ITEM3_DESCRIPTION");
      f.setSize(50);
      f.setMandatory();
      f.setInsertable();
      f.setMaxLength(50);
      f.setDbName("DESCRIPTION");
      f.setLabel("APPSRWISOCODEDEFINITIONITEM3DESCRIPTION: Description");	  
   
      f = itemblk3.addField("PRESENT_FACTOR");
      f.setSize(20);
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(20);
      f.setLabel("APPSRWISOCODEDEFINITIONPRESENTFACTOR: Factor");
   
      f = itemblk3.addField("BASE_UNIT");
      f.setSize(20);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(30);
      f.setDynamicLOV("ISO_UNIT_BASE",600,450);
      f.setLOVProperty("TITLE",mgr.translate("APPSRWISOCODEDEFINITIONBASE: Base Units"));
      f.setLabel("APPSRWISOCODEDEFINITIONBASEUNIT: Base Unit");
   
      f = itemblk3.addField("UNIT_TYPE");
      f.setSize(20);
      f.setInsertable();
      f.setReadOnly();
      f.setMandatory();
      f.setMaxLength(200);
      f.setLabel("APPSRWISOCODEDEFINITIONUTYPE: Unit Type");
      f.setSelectBox();
      f.enumerateValues("ISO_UNIT_TYPE_API"); 
   
      f = itemblk3.addField("ITEM3_USED_IN_APPL");
      f.setCheckBox("FALSE,TRUE");
      f.setDbName("USED_IN_APPL");
      f.setSize(30);
      f.setLabel("APPSRWISOCODEDEFINITIONITEM3USEDINAPPL: Used in Application");
	  
      f = itemblk3.addField("USER_DEFINED");
      f.setCheckBox("FALSE,TRUE");
      f.setSize(30);
      f.setReadOnly();
      f.setLabel("APPSRWISOCODEDEFINITIONUSERDEFINED: User Defined");
   
      f = itemblk3.addField("ATTR");
      f.setHidden();
      f.setFunction("''");
   
      f = itemblk3.addField("INFO");
      f.setHidden();
      f.setFunction("''");
   
      f = itemblk3.addField("ACTION");
      f.setHidden();
      f.setFunction("''");
   
      itemblk3.setView("ISO_UNIT_DEF");
      itemblk3.defineCommand("ISO_UNIT_API","New__,Modify__,Remove__");
   
      itemset3 = itemblk3.getASPRowSet();   
      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.SAVERETURN ,"saveReturnITEM3","checkItem3Fields(-1)");
   
      itembar3.disableCommand(itembar3.NEWROW);
      itembar3.disableCommand(itembar3.DUPLICATEROW);  
   
      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("APPSRWISOCODEDEFINITIONITM3: Unit of Measure"));  
   
      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
      itemlay3.setDialogColumns(1);

      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("APPSRWISOCODEDEFINITIONCOUNTRY: Country"),"javascript: commandSet('HEAD.activateCountry','')");
      tabs.addTab(mgr.translate("APPSRWISOCODEDEFINITIONCURRENCY: Currency"),"javascript:commandSet('HEAD.activateCurrency','')");
      tabs.addTab(mgr.translate("APPSRWISOCODEDEFINITIONLANGUAGE: Language"),"javascript: commandSet('HEAD.activateLanguage','')");
      tabs.addTab(mgr.translate("APPSRWISOCODEDEFINITIONUNITMEA: Unit of Measure"),"javascript:commandSet('HEAD.activateUnitOfMeasure','')");
      
      tabs.setTabWidth(100); 

   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      if (itemlay3.isEditLayout())
      {
         if ("TRUE".equals(itemset3.getRow().getValue("USER_DEFINED")))
            mgr.getASPField("ITEM3_USED_IN_APPL").setReadOnly();	   
         else
	    mgr.getASPField("ITEM3_USED_IN_APPL").unsetReadOnly();	      
      }
   }
  


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "APPSRWISOCODEDEFINITIONTITLE: ISO Code Definition";
   }

   protected String getTitle()
   {
      return "APPSRWISOCODEDEFINITIONTITLE: ISO Code Definition";
   }

   protected void printContents() throws FndException
   {
      if ((headlay.isVisible()) && (itemlay1.isVisible()) && (itemlay2.isVisible()) && (itemlay3.isVisible())) 
      {
         appendToHTML(tabs.showTabsInit()); 
      }

      switch (tabs.getActiveTab()) 
      {
         case 1:
         {
            appendToHTML(headlay.show());
            break;
         }
         case 2:
         {
            appendToHTML(itemlay1.show());
            break;
         }
         case 3:
         {
            appendToHTML(itemlay2.show());
            break;
         }
         case 4:
         {
            appendToHTML(itemlay3.show());
            break;
         }
      }
      if ((headlay.isVisible()) && (itemlay1.isVisible()) && (itemlay2.isVisible()) && (itemlay3.isVisible())) 
      {
         appendToHTML(tabs.showTabsFinish()); 
      }
   }
}
