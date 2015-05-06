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
*  File        : MacroBasic.java 
*  Modified    :
*    ASP2JAVA Tool  2001-03-01  - Created Using the ASP file MacroBasic.asp
*    22/10/2003 : Shtolk Call Id 108082, Added javascript error message. When the user entered more than 2000   
*                                characters for the field 'MB_SCRIPT' message comes up. 
*    25/07/2006 : Bakalk Bug ID 58216, Fixed Sql Injection.
*    22/09/2006 : ThAblk Bug ID 58000, Added a translation constant to label Process.
*    09/11/2006 : NIJALK Bug ID 58421, Added new field MAC_TIMEOUT.
*    08/07/2008 : DULOLK Bug ID 74354, Modified newRowITEM0() to work properly for duplicate.
*    26/12/2008 : AMCHLK Bug Id 78639, Increased length of script column to 4000 
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.asp.ASPTabContainer;

public class MacroBasic extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.MacroBasic");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
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
   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private String val;
   private ASPCommand cmd;
   private String script;
   private ASPQuery q;
   private ASPBuffer data;
   private int item0rowno;



   public MacroBasic(ASPManager mgr, String page_path)
   { 
      super(mgr, page_path);
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();

      tabs.saveActiveTab();
   }



   public void validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
 
      if  ( "MAC1_BLK_NAME".equals(val) ) 
      {
         cmd = trans.addCustomFunction("MCBLNAM" , "EDM_MACRO_BLOCK_API.Get_Script", "MAC1_SCRIPT" );
         cmd.addParameter("MAC1_BLK_NAME");
         trans = mgr.validate(trans);
         script = trans.getValue("MCBLNAM/DATA/MAC1_SCRIPT");
   
         if (mgr.isEmpty(script))
            script = "";
   
         mgr.responseWrite(script + "^");
   
      }
   
      mgr.endResponse();
   }



   public void okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk0);
   
      if ( itemset0.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("DOCMAWMACROBASICNODATA: No data found."));
         itemset1.clear();
      }
      else
      {
         itemlay0.setLayoutMode(itemlay0.SINGLE_LAYOUT);
         okFindITEM1();
         mgr.createSearchURL(itemblk0);
      }

      eval(itemset0.syncItemSets());   
   }


   public void countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getValue("N")));
      itemset0.clear();
   }


   public void newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","EDM_MACRO_API.New__",itemblk0); //Bug Id 74354
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");  //Bug Id 74354
      itemset0.addRow(data);
      //Bug Id 74354, Start
      if (! mgr.isEmpty(trans.getValue("ITEM0/DATA/MACRO_ID")))
	  itemset0.setValue("MACRO_ID", trans.getValue("ITEM0/DATA/MACRO_ID"));
      //Bug Id 74354, End
   }


   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk1);
      //bug 58216 starts
      q.addWhereCondition("MACRO_ID = ? and PROCESS = ?");
      q.addParameter("MAC_ID",itemset0.getValue("MACRO_ID"));
      q.addParameter("MAC_PROCESS",itemset0.getValue("PROCESS"));
      //bug 58216 end
   
   
      q.includeMeta("ALL");
      item0rowno = itemset0.getCurrentRowNo();
      mgr.submit(trans);
      itemset0.goTo(item0rowno);
   
      itemlay1.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
   }


   public void countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      q.addWhereCondition("MACRO_ID = ?");
      q.addParameter("MAC_ID",itemset1.getValue("MACRO_ID"));
      //bug 58216 end
      mgr.submit(trans);
      itemlay1.setCountValue(toInt(itemset1.getValue("N")));
      itemset1.clear();
   }


   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
   
      cmd = trans.addEmptyCommand("ITEM1","EDM_MACRO_BLOCK_REL_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
   
      itemset1.addRow(data);
      itemset1.setValue("MACRO_ID", itemset0.getRow().getValue("MACRO_ID"));
      itemset1.setValue("PROCESS", itemset0.getRow().getValue("PROCESS"));
   }


   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk2);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk2);
   
      if (itemset2.countRows() == 0) 
      {
         mgr.showAlert(mgr.translate("DOCMAWMACROBASICNODATA: No data found."));
         itemset2.clear();
      }
   }


   public void countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getValue("N")));
      itemset2.clear();
   }


   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM2","EDM_MACRO_BLOCK_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      itemset2.addRow(data);
   }


   public void cancelNewITEM2()
   {
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
      okFindITEM2();
   }


   public void activateDocMacro()
   {
      debug("Tab 1 activated..");
      tabs.setActiveTab(1);
   }


   public void activateMacroBlock()
   {
      debug("Tab 2 activated..");
      tabs.setActiveTab(2);
   }


   public void preDefine()
   {
      ASPManager mgr = getASPManager();

   
      headblk = mgr.newASPBlock("HEAD");
   
      headbar = mgr.newASPCommandBar(headblk);
   
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
   
   
      headbar.addCustomCommand("activateDocMacro", "Document Macros");
      headbar.addCustomCommand("activateMacroBlock", "Document Macro Blocks");
   


      itemblk0 = mgr.newASPBlock("ITEM0");
   
      itemblk0.addField("ITEM0_OBJID").
               setHidden().
               setDbName("OBJID");
   
   
      itemblk0.addField("ITEM0_OBJVERSION").
               setHidden().
               setDbName("OBJVERSION");
   
   
      itemblk0.addField("MAC_ID").
               setHidden().
               setDbName("MACRO_ID");


      itemblk0.addField("MAC_FILE_TYPE").
               setDbName("FILE_TYPE").
               setSize(20).
               setMaxLength(30).
               setInsertable().
               setReadOnly().
               setMandatory().
               setDynamicLOV("EDM_APPLICATION").
               setLabel("DOCMAWMACROBASICMACFILETYPE: Application");
   
   
      itemblk0.addField("MAC_SCRIPT_LAN").
               setDbName("SCRIPT_LANGUAGE").
               setSize(20).
               setMaxLength(200).
               setSelectBox().
               setMandatory().
               enumerateValues("EDM_SCRIPT_LANGUAGE_API").
               setLabel("DOCMAWMACROBASICMACSCRIPTLAN: Script Language");
   
      itemblk0.addField("MAC_PROCESS").
               setDbName("PROCESS").
               setSize(20).
               setMaxLength(200).
               setSelectBox().
               setMandatory().
               setInsertable().
               setReadOnly().
               enumerateValues("EDM_MACRO_PROCESS_API").
               setLabel("DOCMAWMACROBASICMACPROCESS: Process"); // Bug ID 58000
   
      itemblk0.addField("MAC_ACTION").
               setDbName("ACTION").
               setSize(20).
               setMaxLength(20).
               setInsertable().
               setMandatory().
               setReadOnly().
               setLabel("DOCMAWMACROBASICMACACTION: Action");

      // Bug 58421 ,Start
      itemblk0.addField("MAC_TIMEOUT", "Number").
               setDbName("TIMEOUT").
               setSize(20).
               setMaxLength(20).
               setInsertable().
               setAlignment(ASPField.ALIGN_RIGHT).  
               setLabel("DOCMAWMACROBASICMACTIMEOUT: Timeout");
      // Bug 58421, End
   
      itemblk0.addField("MAC_MAIN_FUN").
               setDbName("MAIN_FUNCTION").
               setSize(50).
               setMaxLength(50).
               setInsertable().
               setMandatory().
               setLabel("DOCMAWMACROBASICMACMAINFUN: Main Function");


      itemblk0.addField("MAC_DESCRIPTION").
               setDbName("DESCRIPTION").
               setSize(60).
               setMaxLength(2000).
               setInsertable().
               setLabel("DOCMAWMACROBASICMACDESC: Description");
   
   
      itemblk0.setView("EDM_MACRO");
      itemblk0.defineCommand("EDM_MACRO_API","New__,Modify__,Remove__");
   
      itemset0 = itemblk0.getASPRowSet();
   
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
   
   
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWMACROBASICEDMMACROTITLE: Document Macros"));
   
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(2);
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
   
   
   
      itemblk1 = mgr.newASPBlock("ITEM1");
   
      itemblk1.addField("ITEM1_OBJID").
               setDbName("OBJID").
               setHidden();
   
      itemblk1.addField("ITEM1_OBJVERSION").
               setDbName("OBJVERSION").
               setMandatory().
               setHidden();
   
      itemblk1.addField("MAC1_ID").
               setDbName("MACRO_ID").
               setHidden();
   
      itemblk1.addField("MAC1_PROCESS").
               setDbName("PROCESS").
               setHidden();
   
      itemblk1.addField("MAC1_BLK_NAME").
               setDbName("BLOCK_NAME").
               setDynamicLOV("EDM_MACRO_BLOCK").
               setSize(25).
               setMaxLength(50).
               setInsertable().
               setUpperCase().
               setCustomValidation("MAC1_BLK_NAME" , "MAC1_SCRIPT").
               setLabel("DOCMAWMACROBASICMAC1BLKNAME: Block Name");
   
      itemblk1.addField("MAC1_BLANK").
               setReadOnly().
               setHidden().
     		      setLabel("DOCMAWMACROBASICBLANK1:  ").
      		   setFunction("' '");
   
      itemblk1.addField("MAC1_SCRIPT").
      	      setSize(80).
      	      setReadOnly().
               setFunction("EDM_MACRO_BLOCK_API.Get_Script(:MAC1_BLK_NAME)").
      	      setLabel("DOCMAWMACROBASICMAC1SCRIPT: Script");
   
      itemblk1.setView("EDM_MACRO_BLOCK_REL");
      itemblk1.defineCommand("EDM_MACRO_BLOCK_REL_API","New__,Modify__,Remove__");
      itemblk1.setMasterBlock(itemblk0);
   
      itemset1 = itemblk1.getASPRowSet();
   
      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
   
   
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("DOCMAWMACROBASICEDMMLBRELTITLE: Document Macro Detail"));
   
      itemlay1 = itemblk1.getASPBlockLayout();
                 itemlay1.setDialogColumns(1);
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
   
   
      itemblk2 = mgr.newASPBlock("ITEM2");
   
      itemblk2.addField("ITEM2_OBJID").
               setDbName("OBJID").
               setHidden();
   
      itemblk2.addField("ITEM2_OBJVERSION").
               setDbName("OBJVERSION").
               setMandatory().
               setHidden();
   
      itemblk2.addField("MB_BLK_NAME").
               setDbName("BLOCK_NAME").
               setSize(25).
               setMaxLength(50).
               setReadOnly().
               setInsertable().
               setUpperCase().
               setLabel("DOCMAWMACROBASICMACBLKNAME: Block Name");

      itemblk2.addField("MB_SCRIPT").
               setDbName("SCRIPT").
               setSize(100).
               setMaxLength(4000). // Bug Id 78639
   	         setHeight(12).
               setInsertable().
               setValidateFunction("checkScriptLength").
               setLabel("DOCMAWMACROBASICMBSCRIPT: Script");
      

      itemblk2.setView("EDM_MACRO_BLOCK");
      itemblk2.defineCommand("EDM_MACRO_BLOCK_API","New__,Modify__,Remove__");
   
      itemset2 = itemblk2.getASPRowSet();
   
      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.disableCommand(itembar2.DUPLICATEROW);
   
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("DOCMAWMACROBASICEDMMACROBLOCKTITLE: Document Macro Blocks"));
   
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(1);
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
   
   
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("DOCMAWMACROBASICDOCMACRO: Document Macros"), "javascript:commandSet('HEAD.activateDocMacro','')");
      tabs.addTab(mgr.translate("DOCMAWMACROBASICDOCMACROBLOCK: Document Macro Blocks"),"javascript:commandSet('HEAD.activateMacroBlock','')");
   }


   public void adjust()
   {
      ASPManager mgr = getASPManager();      

      // Ok, this is extremely buggy, but it seems to be the only way to get the scan tool to
      // fetch the correct PO-name for this page:
      String dummy = mgr.translate("DOCMAWMACROBASICTITLE: Basic Data - Macro Basic");
   }


   protected String getDescription()
   {
      return "DOCMAWMACROBASICTITLE: Basic Data - Macro Basic";
   }


   protected String getTitle()
   {
      return "DOCMAWMACROBASICTITLE: Basic Data - Macro Basic";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendToHTML(tabs.showTabsInit());
      
      if (tabs.getActiveTab() == 1)
      {
         debug("Showing Tab 1..");

         appendToHTML(itemlay0.show());
   
         if (itemset0.countRows() > 0 && itemlay0.isSingleLayout()) 
         {
            appendToHTML(itemlay1.show());
         }
      } 
      else if (tabs.getActiveTab() == 2)
      {
         debug("Showing Tab 2..");
         appendToHTML(itemlay2.show());
      }

      appendToHTML(tabs.showTabsFinish());

      //Bug Id 78639, Start
    appendDirtyJavaScript("function checkScriptLength()\n");
    appendDirtyJavaScript("{\n");
    appendDirtyJavaScript("    var scriptLength = document.form.MB_SCRIPT.value.length; \n");
    appendDirtyJavaScript("    if (scriptLength > 4000)\n");
    appendDirtyJavaScript("       alert(\"");
    appendDirtyJavaScript(mgr.translate("DOCMAWMACROBASICSCRIPTMAXIMUMLENGTH: Maximum number of 4000 characters for the Script field has been exceeded. You have entered "));
    appendDirtyJavaScript("\"+ scriptLength +\".");
    appendDirtyJavaScript("\");\n");
    appendDirtyJavaScript("}\n");
    //Bug Id 78639, End
   }
}
