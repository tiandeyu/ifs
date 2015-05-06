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
*  File        : UnitRelationships.java 
*  Modified:
*                2003-12-15 Gacolk Web Alignment: Converted 3 blocks to tabs, removed obsolete functions
*                                  doReset and clone.
*                2003-10-12 Zahalk Call ID 106477, Did some changes to show all the records.
*                2001-08-21 CHCR   Removed depreciated methods.
*                2001-03-04 BUNI   Done some changes in preDefine() function and okFind functions.
*                2001-03-16 BUNI   Corrected some conversion errors.
*  ASP2JAVA Tool 2001-03-13  - Created Using the ASP file UnitRelationships.asp
* ----------------------------------------------------------------------------
 * New Comments:
 * 2007/05/04 buhilk Bug 65155, Modified newRowITEM2() to fix the error when duplicating UoM values.
 * 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class UnitRelationships extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.UnitRelationships");


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
   private ASPBlock itemblk4;
   private ASPRowSet itemset4;
   private ASPCommandBar itembar4;
   private ASPTable itemtbl4;
   private ASPBlockLayout itemlay4;
   private ASPField f;

   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPBuffer data;
   private ASPQuery q;
   
   //===============================================================
   // Construction 
   //===============================================================
   public UnitRelationships(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();

      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
   	 okFind();  
   	 okFindITEM1(); 
   	 okFindITEM2(); 
   	 okFindITEM3(); 
   	 okFindITEM4(); 
      }
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
      	 validate(); 
	  
      tabs.saveActiveTab();
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");
      String txt = "";
   
      if ("PRESENT_FACTOR".equals(val)) 
      {
         String Present_Fact = mgr.readValue("PRESENT_FACTOR","");
	   
	 ASPBuffer buff = mgr.newASPBuffer();
	 buff.addFieldItem("PRESENT_FACTOR_TEMP",Present_Fact);
	  
	 txt = Present_Fact;
         mgr.responseWrite(txt); 
      }
   
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addEmptyCommand("HEAD","ISO_UNIT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addEmptyCommand("ITEM2","ISO_UNIT_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      data.setFieldItem("ITEM2_BASE_UNIT",itemset1.getRow().getFieldValue("ITEM1_BASE_UNIT")); 
      itemset2.addRow(data);
      itemset2.setValue("PRESENT_FACTOR","1");      
   }


   public void  saveReturnITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = null;

      int currrow = itemset2.getCurrentRowNo();
      String lsAttr = "";
   
      if  ( "New__".equals(itemset2.getRowStatus()) ) 
      {
   	 lsAttr ="UNIT_CODE" + (char)31 + mgr.readValue("ITEM2_UNIT_CODE","") + (char)30;
         lsAttr = lsAttr + "DESCRIPTION" + (char)31 + mgr.readValue("ITEM2_DESCRIPTION","") + (char)30; 
   	 lsAttr = lsAttr + "PRESENT_FACTOR" + (char)31 + mgr.readValue("PRESENT_FACTOR","") + (char)30; 
   	 lsAttr = lsAttr + "BASE_UNIT" + (char)31 + mgr.readValue("ITEM2_BASE_UNIT","") + (char)30; 
   	 lsAttr = lsAttr + "UNIT_TYPE" + (char)31 + mgr.readValue("ITEM2_UNIT_TYPE","") + (char)30; 
   	 lsAttr = lsAttr + "USER_DEFINED" + (char)31 + mgr.readValue("ITEM2_USER_DEFINED","") + (char)30; 
   
   	 cmd = trans.addCustomCommand("NEWSAVE", "ISO_UNIT_API.New__"); 
   	 cmd.addParameter("INFO");    
   	 cmd.addParameter("OBJID", itemset2.getRow().getValue("OBJID"));
   	 cmd.addParameter("OBJVERSION", itemset2.getRow().getValue("OBJVERSION"));
   	 cmd.addParameter("ATTR",lsAttr);
   	 cmd.addParameter("ACTION","DO");
   
   	 mgr.perform(trans);		
   
   	 okFind1ITEM2();
   
         itemset2.goTo(currrow);
      }
      else 
      {
         itemset2.changeRow();
   
   	 lsAttr ="DESCRIPTION" + (char)31 + mgr.readValue("ITEM2_DESCRIPTION","") + (char)30;
   
   	 cmd = trans.addCustomCommand("ITEM2MODIFY", "ISO_UNIT_API.Modify__"); 
   	 cmd.addParameter("INFO");    
   	 cmd.addParameter("OBJID", itemset2.getRow().getValue("OBJID"));
   	 cmd.addParameter("OBJVERSION", itemset2.getRow().getValue("OBJVERSION"));
   	 cmd.addParameter("ATTR",lsAttr);
   	 cmd.addParameter("ACTION","DO");
   
   	 mgr.perform(trans);
   
   	 okFind1ITEM2();
   
   	 itemset2.goTo(currrow);
      }  
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("UNIT_CODE = BASE_UNIT");
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
      int itemrrow4 = itemset4.getCurrentRowNo();
   
      q = trans.addQuery(headblk);
      q.addWhereCondition("UNIT_CODE = BASE_UNIT");
      q.setOrderByClause("UNIT_CODE");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
   
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWUNITRELATIONSHIPSNODATA: No data found."));
         headset.clear();
      }  
      itemset1.goTo(itemrrow1);
      itemset2.goTo(itemrrow2);
      itemset3.goTo(itemrrow3);
      itemset4.goTo(itemrrow4);
   }


   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      q = trans.addQuery(itemblk1);
      q.addWhereCondition("UNIT_CODE =BASE_UNIT");
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
      int itemrrow4 = itemset4.getCurrentRowNo();
   
      q = trans.addQuery(itemblk1);
      q.addWhereCondition("UNIT_CODE =BASE_UNIT");
      q.setOrderByClause("UNIT_CODE");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk1);
   
      if ( itemset1.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWUNITRELATIONSHIPSNODATA: No data found."));
         itemset1.clear();
      }
             
      headset.goTo(currrow);
      itemset2.goTo(itemrrow2);
      itemset3.goTo(itemrrow3);
      itemset4.goTo(itemrrow4);
   }


   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      q = trans.addQuery(itemblk2);
      q.addWhereCondition("BASE_UNIT = ?");
      q.addParameter("BASE_UNIT", itemset1.getRow().getValue("BASE_UNIT"));
      q.addWhereCondition("UNIT_CODE <> BASE_UNIT");
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
      int itemrowno = itemset1.getCurrentRowNo();
      int itemrrow3 = itemset3.getCurrentRowNo();
      int itemrrow4 = itemset4.getCurrentRowNo();
   
      q = trans.addQuery(itemblk2);
      q.addWhereCondition("BASE_UNIT = ?");
      q.addParameter("BASE_UNIT", itemset1.getRow().getValue("BASE_UNIT"));
      q.addWhereCondition("UNIT_CODE <> BASE_UNIT");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk2);
   
      if ( itemset2.countRows() == 0 ) 
      {
	 mgr.showAlert(mgr.translate("APPSRWUNITRELATIONSHIPSNODATA: No data found.")); 
         itemset2.clear();
      }
      itemset1.goTo(itemrowno); 
      headset.goTo(currrow); 
      itemset3.goTo(itemrrow3);
      itemset4.goTo(itemrrow4);
   }


   public void  okFind1ITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();
      int itemrowno = itemset1.getCurrentRowNo();
      int itemrrow3 = itemset3.getCurrentRowNo();
      int itemrrow4 = itemset4.getCurrentRowNo();
   
      q = trans.addEmptyQuery(itemblk2);
      q.addWhereCondition("BASE_UNIT = ?");
      q.addParameter("BASE_UNIT", itemset1.getRow().getValue("BASE_UNIT"));
      q.addWhereCondition("UNIT_CODE <> BASE_UNIT");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk2);
   
      if ( itemset2.countRows() == 0 ) 
      {
         itemset2.clear();
      }
      itemset1.goTo(itemrowno); 
      headset.goTo(currrow); 
      itemset3.goTo(itemrrow3);
      itemset4.goTo(itemrrow4);
   }


   public void  countFindITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      q = trans.addQuery(itemblk3);
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
      int itemrrow4 = itemset4.getCurrentRowNo();
   
      q = trans.addQuery(itemblk3);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk3);
   
      if ( itemset3.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWUNITRELATIONSHIPSNODATA: No data found."));
         itemset3.clear();
      }
    
      headset.goTo(currrow);
      itemset1.goTo(itemrrow1);
      itemset2.goTo(itemrrow2);
      itemset4.goTo(itemrrow4);
   }


   public void  countFindITEM4()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      q = trans.addQuery(itemblk4);
      q.addWhereCondition("UNIT = ?");
      q.addParameter("UNIT", itemset3.getRow().getValue("UNIT"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));
      itemset4.clear();
   }


   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();
      int itemrrow1 = itemset1.getCurrentRowNo();
      int itemrrow2 = itemset2.getCurrentRowNo();
      int itemrowno = itemset3.getCurrentRowNo();
      q = trans.addQuery(itemblk4);
      q.addWhereCondition("UNIT = ?");
      q.addParameter("UNIT", itemset3.getRow().getValue("UNIT"));
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk4);
   
      if ( itemset4.countRows() == 0 ) 
      {
       	 mgr.showAlert(mgr.translate("APPSRWUNITRELATIONSHIPSNODATA: No data found."));  
         itemset4.clear();
      }
      headset.goTo(currrow);
      itemset1.goTo(itemrrow1);
      itemset2.goTo(itemrrow2);
      itemset3.goTo(itemrowno);  
   }

   public void  activateBase()
   {
      tabs.setActiveTab(1);
   }

   public void  activateDerived()
   {
      tabs.setActiveTab(2);
   }

   public void  activateConversion()
   {
      tabs.setActiveTab(3);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("UNIT_CODE");
      f.setSize(30);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(30);
      f.setLabel("APPSRWUNITRELATIONSHIPSUNITCODE: Unit Code");
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(50);
      f.setMandatory();
      f.setInsertable();
      f.setMaxLength(50);
      f.setLabel("APPSRWUNITRELATIONSHIPSDESC: Description");
   
      f = headblk.addField("BASE_UNIT");
      f.setSize(30);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(30);
      f.setHidden();
      f.setDynamicLOV("ISO_UNIT_BASE",600,450);
      f.setLOVProperty("TITLE",mgr.translate("APPSRWUNITRELATIONSHIPSBASE: Base Units"));
      f.setLabel("APPSRWUNITRELATIONSHIPSBASEUNIT: Base Unit");   
   
      f = headblk.addField("UNIT_TYPE");
      f.setSize(200);
      f.setInsertable();
      f.setReadOnly();
      f.setMandatory();
      f.setMaxLength(200);
      f.setLabel("APPSRWUNITRELATIONSHIPSUTYPE: Unit Type");
      f.setSize(50);
      f.setSelectBox();
      f.enumerateValues("ISO_UNIT_TYPE_API"); 
   
      f = headblk.addField("DUMMY");
      f.setSize(8);
      f.setHidden();
      f.setLabel("APPSRWUNITRELATIONSHIPSDUMMY: Dummy");
      f.setFunction("''"); 
      f.setUpperCase();
   
      f = headblk.addField("USER_DEFINED");
      f.setCheckBox("FALSE,TRUE");
      f.setSize(15);
      f.setReadOnly();
      f.setLabel("APPSRWUNITRELATIONSHIPSUSERDEFINED: User Defined");
   
      f = headblk.addField("MULTI_FACTOR","Number");
      f.setSize(5);
      f.setHidden();
      f.setReadOnly();
      f.setLabel("APPSRWUNITRELATIONSHIPSMULTIFACTOR: Multipl Factor");
   
      f = headblk.addField("DIV_FACTOR","Number");
      f.setSize(5);
      f.setHidden();
      f.setReadOnly();
      f.setLabel("APPSRWUNITRELATIONSHIPSDIVFACTOR: Div Factor");
   
      f = headblk.addField("TEN_POWER","Number");
      f.setSize(5);
      f.setReadOnly();
      f.setHidden();
      f.setLabel("APPSRWUNITRELATIONSHIPSTENPOWER: Pow of ten");
   
   
      headblk.setView("ISO_UNIT");
      headblk.defineCommand("ISO_UNIT_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
      
      headbar.addCustomCommand("activateBase","Base");
      headbar.addCustomCommand("activateDerived","Derived");
      headbar.addCustomCommand("activateConversion","Conversion");

      headbar.removeCustomCommand("activateBase");   
      headbar.removeCustomCommand("activateDerived");
      headbar.removeCustomCommand("activateConversion");
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWUNITRELATIONSHIPSHD: Base"));  
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      headlay.setDialogColumns(1);


   //-----------------------------------------------------------------------
   //-------------- This part belongs to DERIVED TAB ------------------------
   //-----------------------------------------------------------------------
   
      itemblk1 = mgr.newASPBlock("ITEM1");
   
      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk1.addField("ITEM1_UNIT_CODE");
      f.setDbName("UNIT_CODE");
      f.setSize(50);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(30);
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM1UNITCODE: Base Unit Code");
   
      f = itemblk1.addField("ITEM1_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(50);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(50);
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM1DESC: Description");
   
      f = itemblk1.addField("ITEM1_BASE_UNIT");
      f.setDbName("BASE_UNIT");
      f.setSize(30);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(30);
      f.setDynamicLOV("ISO_UNIT_BASE",600,450);
      f.setLabel("APPSRWUNITRELATIONSHIPSBASEUNIT: Base Unit");
      f.setHidden();
   
      f = itemblk1.addField("ITEM1_UNIT_TYPE");
      f.setDbName("UNIT_TYPE");
      f.setSize(200);
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(200);
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM1UNITTYPE: Unit Type");
      f.setSize(50);
      f.setSelectBox();
      f.enumerateValues("ISO_UNIT_TYPE_API"); 
   
      itemblk1.setView("ISO_UNIT");
   
      itemset1 = itemblk1.getASPRowSet();
   
      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
   
      itembar1.disableCommand(itembar1.EDIT);
      itembar1.disableCommand(itembar1.DELETE);
      itembar1.disableCommand(itembar1.NEWROW);
      itembar1.disableCommand(itembar1.DUPLICATEROW);  
   
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("APPSRWUNITRELATIONSHIPSITM1: Derived"));  
   
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      itemlay1.setDialogColumns(1);
   
   
      itemblk2 = mgr.newASPBlock("ITEM2");
   
      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk2.addField("ITEM2_UNIT_CODE");
      f.setSize(20);
      f.setReadOnly();
      f.setMandatory();
      f.setInsertable();
      f.setMaxLength(20);
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM2UNITCODE: Unit Code");
      f.setDbName("UNIT_CODE");
   
      f = itemblk2.addField("ITEM2_DESCRIPTION");
      f.setSize(50);
      f.setMandatory();
      f.setInsertable();
      f.setMaxLength(50);
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM2DESCR: Description");
      f.setDbName("DESCRIPTION");
   
      f = itemblk2.addField("ITEM2_BASE_UNIT");
      f.setSize(30);
      f.setHidden();
      f.setMaxLength(30);
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM2BASEUNIT: Base Unit");
      f.setDbName("BASE_UNIT");
   
      f = itemblk2.addField("PRESENT_FACTOR");
      f.setSize(20);
      f.setReadOnly();
      f.setMandatory();
      f.setInsertable();
      f.setMaxLength(20);
      f.setCustomValidation("PRESENT_FACTOR","PRESENT_FACTOR");
      f.setLabel("APPSRWUNITRELATIONSHIPSPRESENTFACTOR: Factor");
	  
      f = headblk.addField("PRESENT_FACTOR_TEMP","Number");
      f.setHidden();
      f.setFunction("0");
   
      f = itemblk2.addField("ITEM2_USER_DEFINED");
      f.setDbName("USER_DEFINED");
      f.setCheckBox("FALSE,TRUE");
      f.setSize(15);
      f.setReadOnly();
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM2USERDEFINED: User Defined");
   
      f = itemblk2.addField("ITEM2_MULTI_FACTOR","Number");
      f.setDbName("MULTI_FACTOR");
      f.setSize(9);
      f.setHidden();
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM2MULTIFACTOR: Multipl Factor");
   
      f = itemblk2.addField("ITEM2_DIV_FACTOR","Number");
      f.setDbName("DIV_FACTOR");
      f.setSize(9);
      f.setHidden();
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM2DIVFACTOR: Div Factor");
   
      f = itemblk2.addField("ITEM2_TEN_POWER","Number");
      f.setDbName("TEN_POWER");
      f.setSize(9);
      f.setHidden();
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM2TENPOWER: Pow of ten");
   
      f = itemblk2.addField("ITEM2_UNIT_TYPE");
      f.setSize(14);
      f.setMandatory();
      f.setHidden();
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM2UNITTYPE: Unit Type");
      f.setSize(50);
      f.setDbName("UNIT_TYPE");
   
      f = itemblk2.addField("ATTR");
      f.setHidden();
      f.setFunction("''");
   
      f = itemblk2.addField("INFO");
      f.setHidden();
      f.setFunction("''");
   
      f = itemblk2.addField("ACTION");
      f.setHidden();
      f.setFunction("''");
   
   
      itemblk2.setView("ISO_UNIT");
      itemblk2.defineCommand("ISO_UNIT_API","New__,Modify__,Remove__");
   
      itemset2 = itemblk2.getASPRowSet();
   
      itemblk2.setMasterBlock(itemblk1);
   
      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.enableCommand(itembar2.FIND);
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
   
      itembar2.defineCommand(itembar2.SAVERETURN,"saveReturnITEM2","checkItem2Fields(-1)");
   	  
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("APPSRWUNITRELATIONSHIPSITM2: Derived Units"));  
      itemtbl2.setWrap();
   
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);

      itemlay2.setDialogColumns(1);
   
   //-----------------------------------------------------------------------
   //-------------- This part belongs to CONVERSION TAB ------------------------
   //-----------------------------------------------------------------------   
   
      itemblk3 = mgr.newASPBlock("ITEM3");
   
      f = itemblk3.addField("ITEM3_OBJID");
      f.setDbName("OBJID");
      f.setHidden();
   
      f = itemblk3.addField("ITEM3_OBJVERSION");
      f.setDbName("OBJVERSION");
      f.setHidden();
   
      f = itemblk3.addField("UNIT");
      f.setSize(30);
      f.setMandatory();
      f.setLabel("APPSRWUNITRELATIONSHIPSUNIT: Selected Unit");
   
      f = itemblk3.addField("ITEM3_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(50);
      f.setMandatory();
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM3DESC: Description");
   
      itemblk3.setView("TECHNICAL_UNIT");
   
      itemset3 = itemblk3.getASPRowSet();
   
      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
   
      itembar3.disableCommand(itembar3.EDIT);
      itembar3.disableCommand(itembar3.DELETE);
      itembar3.disableCommand(itembar3.NEWROW);
      itembar3.disableCommand(itembar3.DUPLICATEROW);  
   
      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("APPSRWUNITRELATIONSHIPSITM3: Conversion"));  
   
      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);

      itemlay3.setDialogColumns(1);   
   
      itemblk4 = mgr.newASPBlock("ITEM4");
   
      f = itemblk4.addField("ITEM4_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk4.addField("ITEM4_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk4.addField("ITEM4_UNIT");
      f.setSize(30);
      f.setDynamicLOV("TECHNICAL_UNIT",600,450);
      f.setMandatory();
      f.setHidden();
      f.setLabel("APPSRWUNITRELATIONSHIPSITEM4_UNIT: Unit");
      f.setDbName("UNIT");
   
      f = itemblk4.addField("CONV_FACTOR");
      f.setSize(27);
      f.setMandatory();
      f.setLabel("APPSRWUNITRELATIONSHIPSCONV_FACTOR: Conversion Factor");
   
      f = itemblk4.addField("ALT_UNIT");
      f.setSize(50);
      f.setDynamicLOV("TECHNICAL_UNIT",600,450);
      f.setMandatory();
      f.setLabel("APPSRWUNITRELATIONSHIPSALT_UNIT: Alternative Unit");
   
      f = itemblk4.addField("TECHNICALUNITDESCRIPTION");
      f.setSize(50);
      f.setLabel("APPSRWUNITRELATIONSHIPSTECHNICALUNITDESCRIPTION: Description");
      f.setFunction("TECHNICAL_UNIT_API.Get_Description(:ALT_UNIT)");
   
   
      itemblk4.setView("TECHNICAL_UNIT_CONV");
   
      itemset4 = itemblk4.getASPRowSet();
   
      itembar4 = mgr.newASPCommandBar(itemblk4);
   
      itemblk4.setMasterBlock(itemblk3);
   
      itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
      itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
   
      itembar4.disableCommand(itembar4.EDIT);
      itembar4.disableCommand(itembar4.DELETE);
      itembar4.disableCommand(itembar4.NEWROW);
      itembar4.disableCommand(itembar4.DUPLICATEROW);
      itembar4.enableCommand(itembar4.FIND);
   
      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setTitle(mgr.translate("APPSRWUNITRELATIONSHIPSITM4: Conversion Details"));  
      itemtbl4.setWrap();
   
      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);

      itemlay4.setDialogColumns(1);
   

      //--------------------------------------------------------------------------------------------
      // Tabs
      //--------------------------------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.addTab("APPSRWUNITRELATIONSHIPSTABBASE: Base","javascript:commandSet('HEAD.activateBase','')");
      tabs.addTab("APPSRWUNITRELATIONSHIPSTABDRVD: Derived","javascript:commandSet('HEAD.activateDerived','')");
      tabs.addTab("APPSRWUNITRELATIONSHIPSTABCONV: Conversion","javascript:commandSet('HEAD.activateConversion','')");

      tabs.setTabWidth(100);

   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "APPSRWUNITRELATIONSHIPSTITLE: Unit Relationships";
   }

   protected String getTitle()
   {
      return "APPSRWUNITRELATIONSHIPSTITLE: Unit Relationships";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      if ((headlay.isVisible()) && (itemlay1.isVisible()) && (itemlay3.isVisible()))
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

            if ((itemset1.countRows() != 0) && (itemlay1.isSingleLayout()))
            {
               appendToHTML(itemlay2.show());
            }
            break;
         }
         case 3:
         {
            appendToHTML(itemlay3.show());
            if ((itemset3.countRows() != 0) && (itemlay3.isSingleLayout()))
            {
               appendToHTML(itemlay4.show());
            }

            break;
         }
      } 
      if ((headlay.isVisible()) && (itemlay1.isVisible()) && (itemlay3.isVisible()))
      {
         appendToHTML(tabs.showTabsFinish());
      }
   }
}
