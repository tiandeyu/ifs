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
*  File        : Template.java 
*  Modified    :
*                   2006-02-07  mapelk - Unit LOV error 132572 fixed. Remove TECHNICAL_CLASS from the query condition 
*                   2003-12-16  Gacolk - Web Alignment: converted 2 blocks to tabs and removed obsolete functions.
*                   2003-10-12  Zahalk - Call ID 106477, Did some changes to show all the records.
*                   2001-08-21  CHCR Removed depreciated methods.
*    ASP2JAVA Tool  2001-03-12  - Created Using the ASP file Template.asp
* ----------------------------------------------------------------------------
* New Comments:
* 2010/08/12 amiklk Bug 92386, Changed preDefine(), okFindItem1() to order by 'order' field.
* 2009/10/26 sumelk Bug 86740, Changed okFindITEM0(), okFindITEM1() & okFindITEM2() to include order by clause.
* 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Template extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.Template");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;
   private ASPBlockLayout itemlay0;
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
   private ASPField f;
   private ASPBlock b;

   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   //===============================================================
   // Construction 
   //===============================================================
   public Template(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();
   
       
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
   
      adjust();
      tabs.saveActiveTab();

   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------


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

//-----------------------------------------------------------------------------
//-----------------------------  PERFORM FUNCTION  ----------------------------
//-----------------------------------------------------------------------------

   public void  ok()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("NEWREC","TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc");
      cmd.addParameter("ATTRIBUTE");
   
      cmd = trans.addCustomCommand("NEWREC","TECHNICAL_UNIT_API.Get_Description");
      cmd.addParameter("UNIT");
   
      mgr.perform(trans);      
   
      headset.clear();
   
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(headblk);
   
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
   
      q.includeMeta("ALL");
   
      mgr.querySubmit(trans,headblk);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWTEMPLATENODATA: No data found."));
         headset.clear();
      }
      else if ( headset.countRows() == 1 ) 
      {
        okFindITEM0();
        okFindITEM1();
        okFindITEM2();
      } 
   
     eval(headset.syncItemSets());
     eval(itemset1.syncItemSets());
   }


//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addEmptyCommand("HEAD","TECHNICAL_ATTRIB_TEXT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);   
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addEmptyCommand("ITEM0","TECHNICAL_ATTRIB_NUMERIC_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_TECHNICAL_CLASS",headset.getRow().getFieldValue("TECHNICAL_CLASS")); 
      itemset0.addRow(data);
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addEmptyCommand("ITEM1","TECHNICAL_ATTRIB_NUMERIC_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("ITEM1/DATA");
      data.setFieldItem("ITEM1_TECHNICAL_CLASS",headset.getRow().getFieldValue("TECHNICAL_CLASS")); 
      itemset1.addRow(data);
   }


   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addEmptyCommand("ITEM2","TECHNICAL_ATTRIB_TEXT_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("ITEM2/DATA");
   
      data.setFieldItem("ITEM2_TECHNICAL_CLASS",itemset1.getRow().getFieldValue("ITEM1_TECHNICAL_CLASS")); 
      data.setFieldItem("ITEM2_ATTRIBUTE",itemset1.getRow().getFieldValue("ITEM1_ATTRIBUTE")); 
   
      itemset2.addRow(data);
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
   
      int currrow = headset.getCurrentRowNo();   
   
      ASPQuery q = trans.addQuery(itemblk0);
      q.addWhereCondition("TECHNICAL_CLASS = ?");
      q.addParameter("TECHNICAL_CLASS", headset.getValue("TECHNICAL_CLASS"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
   
      itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
      itemset0.clear();
      headset.goTo(currrow);     
   }


   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
   
      int currrow = headset.getCurrentRowNo();   
   
      ASPQuery q = trans.addQuery(itemblk1);
      q.addWhereCondition("TECHNICAL_CLASS = ?");
      q.addParameter("TECHNICAL_CLASS", headset.getValue("TECHNICAL_CLASS"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
   
      itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
      itemset1.clear();
      headset.goTo(currrow);     
   }


   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();   
   
      ASPQuery q = trans.addQuery(itemblk1);
      q.addWhereCondition("TECHNICAL_CLASS = ?");
      q.addParameter("TECHNICAL_CLASS", headset.getValue("TECHNICAL_CLASS"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
   
      itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
      itemset2.clear();
      headset.goTo(currrow);     
   }


   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
   
      int currrow = headset.getCurrentRowNo();
   
      ASPQuery q = trans.addQuery(itemblk0);
      q.addWhereCondition("TECHNICAL_CLASS = ?");
      q.addParameter("TECHNICAL_CLASS", headset.getValue("TECHNICAL_CLASS"));
      q.setOrderByClause("ATTRIB_NUMBER");
      q.includeMeta("ALL");
   
      mgr.querySubmit(trans,itemblk0);
   
      headset.goTo(currrow);
   
   }


   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
   
      int currrow = headset.getCurrentRowNo();
   
      ASPQuery q = trans.addQuery(itemblk1);
      q.addWhereCondition("TECHNICAL_CLASS = ?");
      q.addParameter("TECHNICAL_CLASS", headset.getValue("TECHNICAL_CLASS"));
      q.setOrderByClause("ATTRIB_NUMBER");
      q.includeMeta("ALL");
   
      mgr.querySubmit(trans,itemblk1);
      headset.goTo(currrow);
   
      eval(itemset1.syncItemSets());
   }


   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      if ( itemset1.countRows() != 0 ) 
      {
         int currrowh = headset.getCurrentRowNo();
         int currrow = itemset1.getCurrentRowNo();
      
         ASPQuery q = trans.addQuery(itemblk2);
         q.addWhereCondition("TECHNICAL_CLASS = ?");
         q.addWhereCondition("ATTRIBUTE = ?");
         q.addParameter("TECHNICAL_CLASS", itemset1.getRow().getFieldValue("ITEM1_TECHNICAL_CLASS"));
         q.addParameter("ATTRIBUTE", itemset1.getRow().getFieldValue("ITEM1_ATTRIBUTE"));
         q.setOrderByClause("VALUE_TEXT");
         q.includeMeta("ALL");
   
         mgr.querySubmit(trans,itemblk2);
   
         headset.goTo(currrowh);
         itemset1.goTo(currrow);
      }
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------


   public void  count()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      String n = headset.getRow().getValue("N");
      mgr.setStatusLine("APPSRWTEMPLATEQRYCNT: Query will retrieve &1 rows",n);
   }


//-----------------------------------------------------------------------------
//------------------------  CMDBAR BROWSE FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  adjust()
   {
      if ( itemset1.countRows() == 0 ) 
         itembar2.disableCommand(itembar2.NEWROW);
   }

   public void  activateNumeric()
   {
      tabs.setActiveTab(1);
   }

   public void  activateAlphaNumeric()
   {
      tabs.setActiveTab(2);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

   //==========================================================
   //==================Head Block==============================
   //==========================================================
   
      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("TECHNICAL_CLASS");
      f.setSize(28);
      f.setMandatory();
      f.setLabel("APPSRWTEMPLATETECHNICAL_CLASS: Technical Class");
      f.setUpperCase();
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(35);
      f.setLabel("APPSRWTEMPLATEDESCRIPTION: Description");
      
      headblk.setView("TECHNICAL_CLASS");
      headblk.defineCommand("TECHNICAL_CLASS_API","New__,Remove__");
      headset = headblk.getASPRowSet();      
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWTEMPLATEHD: Template"));  
      headtbl.setWrap();
   
      headbar = mgr.newASPCommandBar(headblk);
      headbar.setBorderLines(false,true);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.DUPLICATEROW);
   
      headbar.enableCommand(headbar.SAVERETURN); 
      headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");  
      headbar.enableCommand(headbar.SAVENEW);
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

      headbar.addCustomCommand("activateNumeric","Numeric");
      headbar.addCustomCommand("activateAlphaNumeric","AlphaNumeric");

      headbar.removeCustomCommand("activateNumeric");   
      headbar.removeCustomCommand("activateAlphaNumeric");

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
   
   //======================================================================
   //=================== Block0 refers to Numeric tab   ===================
   //======================================================================
   
      itemblk0 = mgr.newASPBlock("ITEM0");
   
      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk0.addField("ATTRIB_NUMBER","Number");
      f.setSize(8);
      f.setLabel("APPSRWTEMPLATEITEM0_ATTRIB_NUM: Order");
   
      f = itemblk0.addField("ATTRIBUTE");
      f.setSize(32);
      f.setDynamicLOV("TECHNICAL_ATTRIB_STD_NUMERIC",600,445);
      f.setMandatory();
      f.setLabel("APPSRWTEMPLATEITEM0_ATTRIB: Attribute");
      f.setReadOnly();
      f.setInsertable(); 
   
      f = itemblk0.addField("ATTRIB_DESC");
      f.setSize(40);
      f.setReadOnly();
      f.setLabel("APPSRWTEMPLATESATTRIBDESC: Attribute Description");
      f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ATTRIBUTE)");
      mgr.getASPField("ATTRIBUTE").setValidation("ATTRIB_DESC");  
   
      f = itemblk0.addField("UNIT");
      f.setLabel("APPSRWTEMPLATEITEM0_UNT: Unit");
      f.setSize(30);
      f.setMandatory();
      f.setDynamicLOV("TECHNICAL_UNIT","",600,445);
      f.setMaxLength(25); 
      f.setReadOnly();
      f.setInsertable(); 
   
      f = itemblk0.addField("UNIT_DESC");
      f.setSize(40);
      f.setReadOnly();
      f.setLabel("APPSRWTEMPLATEUNITDESC: Unit Description");
      f.setFunction("TECHNICAL_UNIT_API.Get_Description(:UNIT)");
      mgr.getASPField("UNIT").setValidation("UNIT_DESC");  
   
      f = itemblk0.addField("SUMMARY");
      f.setSize(13);
      f.setLabel("APPSRWTEMPLATEITEM0_SUMM: Summary");
      f.setSelectBox();
      f.enumerateValues("TECHNICAL_ATTRIB_SUMMARY_API"); 
   
      f = itemblk0.addField("SUMMARY_DB");
      f.setHidden();   
   
      f = itemblk0.addField("SUMMARY_PREFIX");
      f.setSize(9);
      f.setLabel("APPSRWTEMPLATESUMMARY_PREFIX: Prefix");
      f.setUpperCase();
   
      f = itemblk0.addField("INFO");
      f.setSize(7);
      f.setLabel("APPSRWTEMPLATEINFO: Info");
   
      f = itemblk0.addField("ITEM0_TECHNICAL_CLASS");
      f.setSize(21);
      f.setMandatory();
      f.setHidden();
      f.setLabel("APPSRWTEMPLATEITEM1_TECHNICAL_CLASS: TechnicalClass");
      f.setUpperCase();
      f.setDbName("TECHNICAL_CLASS");   
   
      itemblk0.setView("TECHNICAL_ATTRIB_NUMERIC");
      itemblk0.defineCommand("TECHNICAL_ATTRIB_NUMERIC_API","New__,Modify__,Remove__");   
      itemset0 = itemblk0.getASPRowSet();
      itemblk0.setMasterBlock(headblk);
   
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("APPSRWTEMPLATEITM0: Numeric"));  
      itemtbl0.setWrap();
   
      itembar0 = mgr.newASPCommandBar(itemblk0);
   
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0"); 
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");   
   
      itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
      itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");
   
      itembar0.setBorderLines(true,true);
      itembar0.removeCommandGroup(itembar0.CMD_GROUP_SEARCH);
   
      itembar0.enableCommand(itembar0.FIND);
   
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT); 
   
   
   //=================================================================
   //========   Block1 refers to Alpha Numeric tab   =================
   //=================================================================
   
   
      itemblk1 = mgr.newASPBlock("ITEM1");
   
      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();  
      f.setDbName("OBJID"); 
   
      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk1.addField("ITEM1_ATTRIB_NUMBER","Number");
      f.setSize(8);
      f.setLabel("APPSRWTEMPLATEATTRIB_NUMBER: Order");
      f.setDbName("ATTRIB_NUMBER");
   
      f = itemblk1.addField("ITEM1_ATTRIBUTE");
      f.setSize(32);
      f.setDynamicLOV("TECHNICAL_ATTRIB_STD_ALPHANUM",600,450);
      f.setMandatory();
      f.setLabel("APPSRWTEMPLATEITEM1_ATTRIBUTE: Attribute");
      f.setDbName("ATTRIBUTE");
      f.setReadOnly();
      f.setInsertable(); 
   
      f = itemblk1.addField("SATTRIBDESC");
      f.setSize(40);
      f.setReadOnly();
      f.setLabel("APPSRWTEMPLATESATTRID: Attribute Description");
      f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ITEM1_ATTRIBUTE)");
      mgr.getASPField("ITEM1_ATTRIBUTE").setValidation("SATTRIBDESC");  
   
      f = itemblk1.addField("ITEM1_SUMMARY");
      f.setSize(13);
      f.setLabel("APPSRWTEMPLATEITEM1_SUMMARY: Summary");
      f.setSelectBox();
      f.enumerateValues("TECHNICAL_ATTRIB_SUMMARY_API"); 
      f.setDbName("SUMMARY");
   
      f = itemblk1.addField("ITEM1_SUMMARY_PREFIX");
      f.setSize(9);
      f.setLabel("APPSRWTEMPLATEITEM1_SUMMARY_PREFIX: Prefix");
      f.setUpperCase();
      f.setDbName("SUMMARY_PREFIX");
   
      f = itemblk1.addField("ITEM1_INFO");
      f.setSize(7);
      f.setLabel("APPSRWTEMPLATEITEM1_INFO: Info");
      f.setDbName("INFO");
   
      f = itemblk1.addField("ITEM1_TECHNICAL_CLASS");
      f.setSize(21);
      f.setMandatory();
      f.setHidden();
      f.setLabel("APPSRWTEMPLATEITEM1_TECHNICAL_CLASS: TechnicalClass");
      f.setUpperCase();
      f.setDbName("TECHNICAL_CLASS");   
   
      itemblk1.setView("TECHNICAL_ATTRIB_ALPHANUM");
      itemblk1.defineCommand("TECHNICAL_ATTRIB_ALPHANUM_API","New__,Modify__,Remove__");
      itemset1 = itemblk1.getASPRowSet();
   
      itemblk1.setMasterBlock(headblk);
   
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("APPSRWTEMPLATEITM1: Alpha Numeric"));  
      itemtbl1.setWrap();
   
      itembar1 = mgr.newASPCommandBar(itemblk1);
   
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1"); 
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");   
   
      itembar1.defineCommand(itembar1.SAVERETURN,null,"checkItem1Fields(-1)");
      itembar1.defineCommand(itembar1.SAVENEW,null,"checkItem1Fields(-1)");
   
      itembar1.setBorderLines(true,true);
      itembar1.removeCommandGroup(itembar1.CMD_GROUP_SEARCH);
   
      itembar1.enableCommand(itembar1.FIND);
   
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT); 
   
   
      b = mgr.newASPBlock("ITEM1_SUMMARY");
   
      b.addField("CLIENT_VALUES0");  
   
   
      itemblk2 = mgr.newASPBlock("ITEM2");
   
      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk2.addField("VALUE_TEXT");
      f.setSize(20);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setLabel("APPSRWTEMPLATEVALUE_TEXT: Value");
      f.setMaxLength(20);
   
      f = itemblk2.addField("ITEM2_TECHNICAL_CLASS");
      f.setSize(21);
      f.setMandatory();
      f.setHidden();
      f.setLabel("APPSRWTEMPLATEITEM2_TECHNICAL_CLASS: Technical Class");
      f.setUpperCase();
      f.setDbName("TECHNICAL_CLASS");  
   
      f = itemblk2.addField("ITEM2_ATTRIBUTE");
      f.setSize(32);
      f.setDynamicLOV("TECHNICAL_ATTRIB_STD_NUMERIC",600,445);
      f.setMandatory();
      f.setLabel("APPSRWTEMPLATEITEM2_ATTRIB: Attribute");
      f.setDbName("ATTRIBUTE");
      f.setHidden(); 
   
   
      itemblk2.setView("TECHNICAL_ATTRIB_TEXT");
      itemblk2.defineCommand("TECHNICAL_ATTRIB_TEXT_API","New__,Modify__,Remove__");
      itemset2 = itemblk2.getASPRowSet();
   
      itemblk2.setMasterBlock(itemblk1);
   
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("APPSRWTEMPLATEITM2: Value"));  
      itemtbl2.setWrap();
   
      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.disableCommand(itembar2.EDITROW);
   
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2"); 
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");   
   
      itembar2.defineCommand(itembar2.SAVERETURN,null,"checkItem2Fields(-1)");
      itembar2.defineCommand(itembar2.SAVENEW,null,"checkItem2Fields(-1)");
   
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT); 
   

      tabs = mgr.newASPTabContainer();
     
      tabs.addTab(mgr.translate("APPSRWTEMPLATENUMERIC: Numeric"),"javascript:commandSet('HEAD.activateNumeric','')");
      tabs.addTab(mgr.translate("APPSRWTEMPLATEALPHANUMERIC: Alpha Numeric"),"javascript:commandSet('HEAD.activateAlphaNumeric','')");

      tabs.setTabWidth(100);

   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "APPSRWTEMPLATETITLE: Template";
   }

   protected String getTitle()
   {
      return "APPSRWTEMPLATETITLE: Template";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      if (headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }

      if (itemlay0.isVisible() && itemlay1.isVisible())
      {
         appendToHTML(tabs.showTabsInit()); 
      }

      if (itemlay0.isVisible() && tabs.getActiveTab()==1)
      {
         appendToHTML(itemlay0.show());
      }
      else if (tabs.getActiveTab()==2) 
      {
         if (itemlay1.isVisible()) 
         {
            appendToHTML(itemlay1.show());
         }
         if (itemlay2.isVisible())
         {
            appendToHTML(itemlay2.show());
         }
      }

      if (itemlay0.isVisible() && itemlay1.isVisible())
      {
         appendToHTML(tabs.showTabsFinish());
      }

   }

}
