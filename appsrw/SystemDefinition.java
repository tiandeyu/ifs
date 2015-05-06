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
*  File        : SystemDefinition.java 
*  Modified    : 13-03-2001  CHDELK  Converted ti Java
*    ASP2JAVA Tool  2001-03-12  - Created Using the ASP file SystemDefinition.asp
*                   2001-08-21  CHCR Removed depreciated methods.
*                   2008-01-29  SUMELK Bug 69372, Added new field MAX_LENGTH.
*                   2008-06-20  SUMELK Bug 74937, Increased the maximum length of Property Value to 2000.
*                   2010-06-10  SUMELK Bug 91287, Changed okFind() & okFindITEM1().
* ----------------------------------------------------------------------------
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class SystemDefinition extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.SystemDefinition");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
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
   private ASPField f;
   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private String val;
   private ASPQuery q;
   private int itemrrow1;
   private int currrow;

   //===============================================================
   // Construction 
   //===============================================================
   public SystemDefinition(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      val   = null;
      q   = null;
      itemrrow1   = 0;
      currrow   = 0;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      SystemDefinition page = (SystemDefinition)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.val   = null;
      page.q   = null;
      page.itemrrow1   = 0;
      page.currrow   = 0;
      
      // Cloning immutable attributes
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk1 = page.getASPBlock(itemblk1.getName());
      page.itemset1 = page.itemblk1.getASPRowSet();
      page.itembar1 = page.itemblk1.getASPCommandBar();
      page.itemtbl1 = page.getASPTable(itemtbl1.getName());
      page.itemlay1 = page.itemblk1.getASPBlockLayout();
      page.f = page.getASPField(f.getName());

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
   
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
   
      if(mgr.commandBarActivated())
     	eval(mgr.commandBarFunction());
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFind();  
   	 okFindITEM1(); 		
      }
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
       	 validate(); 

      tabs.saveActiveTab();
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   public void  startup()
   {
      // Insert startup code here 
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      itemrrow1 = itemset1.getCurrentRowNo();
   
      trans.clear();
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
   
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert("APPSRWSYSTEMDEFINITIONNODATA: No data found.");
         headset.clear();
      } 
      itemset1.goTo(itemrrow1);
   }


   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
      itemset1.clear();
   }


   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      currrow = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk1);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk1);
   
      if ( itemset1.countRows() == 0 ) 
      {
         mgr.showAlert("APPSRWSYSTEMDEFINITIONNODATA: No data found.");
         itemset1.clear();
      }
      headset.goTo(currrow);
   }

   public void activateAttributeDefinition()
   {   
      tabs.setActiveTab(1);   
   }

   public void activateObjectProperty()
   {   
      tabs.setActiveTab(2);   
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("BASE_LU");
      f.setSize(50);
      f.setMandatory();
      f.setLabel("APPSRWSYSTEMDEFINITIONBASE_LU: Base LU Name");
      f.setReadOnly();
   
      f = headblk.addField("ATTRIBUTE_NAME");
      f.setSize(17);
      f.setMandatory();
      f.setLabel("APPSRWSYSTEMDEFINITIONATTRIBUTE_NAME: Attribute Name");
      f.setReadOnly();
      f.setUpperCase();
      f.setMaxLength(20);
   
      f = headblk.addField("PARAMETER_NAME");
      f.setSize(16);
      f.setMandatory();
      f.setLabel("APPSRWSYSTEMDEFINITIONPARAMETER_NAME: Parameter Name");
      f.setReadOnly();
      f.setUpperCase();
      f.setMaxLength(16);
   
      f = headblk.addField("LENGTH","Number");
      f.setSize(10);
      f.setMandatory();
      f.setLabel("APPSRWSYSTEMDEFINITIONLENGTH: Length");
      f.setMaxLength(4);

      f = headblk.addField("MAX_LENGTH","Number");
      f.setSize(10);
      f.setReadOnly();
      f.setLabel("APPSRWSYSTEMDEFINITIONMAXLENGTH: Maximum Length");

      f = headblk.addField("ROWS_COUNT","Number");
      f.setSize(10);
      f.setLabel("APPSRWSYSTEMDEFINITIONROWS_COUNT: Rows Count");
      f.setMaxLength(2);
   
   
      headblk.setView("ATTRIBUTE_DEFINITION");
      headblk.defineCommand("ATTRIBUTE_DEFINITION_API","New__,Modify__,Remove__");
         
      //headblk.setTitle(mgr.translate("APPSRWSYSTEMDEFINITIONHEADTITLE: Attribute Definition"));
   
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW); 
      headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");   
   
      headbar.addCustomCommand("activateAttributeDefinition","Attribute Definition");
      headbar.addCustomCommand("activateObjectProperty","Object Property");
      headbar.removeCustomCommand("activateAttributeDefinition");
      headbar.removeCustomCommand("activateObjectProperty");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWSYSTEMDEFINITIONHD: Attribute Definition"));  
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(1);
   
   //-----------------------------------------------------------------------
   //-------------- This part belongs to Object Definition TAB ------------------------
   //-----------------------------------------------------------------------
   
      itemblk1 = mgr.newASPBlock("ITEM1");
   
      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk1.addField("OBJECT_LU");
      f.setSize(50);
      f.setMandatory();
      f.setLabel("APPSRWSYSTEMDEFINITIONOBJECT_LU: Object LU");
      f.setReadOnly();
   
      f = itemblk1.addField("OBJECT_KEY");
      f.setSize(16);
      f.setMandatory();
      f.setLabel("APPSRWSYSTEMDEFINITIONOBJECT_KEY: Object Key");
      f.setReadOnly();
      f.setMaxLength(16);
   
      f = itemblk1.addField("PROPERTY_NAME");
      f.setSize(15);
      f.setMandatory();
      f.setLabel("APPSRWSYSTEMDEFINITIONPROPERTY_NAME: Property Name");
      f.setUpperCase();
      f.setReadOnly();
      f.setMaxLength(20);
   
      f = itemblk1.addField("PROPERTY_VALUE");
      f.setSize(50);
      f.setLabel("APPSRWSYSTEMDEFINITIONPROPERTY_VALUE: Property Value");
      f.setMaxLength(2000);
   
      itemblk1.setView("OBJECT_PROPERTY");
      itemblk1.defineCommand("OBJECT_PROPERTY_API","New__,Modify__,Remove__");
   
      //itemblk1.setTitle(mgr.translate("APPSRWSYSTEMDEFINITIONBLK1TITLE: Object Definition")); 
   
      itemset1 = itemblk1.getASPRowSet();
   
      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
   
      itembar1.disableCommand(itembar1.DELETE);
      itembar1.disableCommand(itembar1.NEWROW);
      itembar1.disableCommand(itembar1.DUPLICATEROW); 
      itembar1.defineCommand(itembar1.SAVERETURN,null,"checkItem1Fields(-1)"); 
   
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("APPSRWSYSTEMDEFINITIONITM1: Object Definition"));  
   
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      itemlay1.setDialogColumns(1);
   
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("APPSRWSYSTEMDEFINITIONTABATTRIBUTEDEF: Attribute Definition"),"javascript: commandSet('HEAD.activateAttributeDefinition','')");
      tabs.addTab(mgr.translate("APPSRWSYSTEMDEFINITIONTABOBJECTPROPER: Object Property"),"javascript:commandSet('HEAD.activateObjectProperty','')");
   }



//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "APPSRWSYSTEMDEFINITIONTITLE: System Definition";
   }

   protected String getTitle()
   {
      return "APPSRWSYSTEMDEFINITIONTITLE: System Definition";
   }

   protected void printContents() throws FndException
   {
      appendToHTML(tabs.showTabsInit()); 
      switch (tabs.getActiveTab()) 
      {
         case 1:
         {
            if(headlay.isVisible())
               appendToHTML(headlay.show());
            break;
         }
         case 2:
         {
            if (itemlay1.isVisible())
               appendToHTML(itemlay1.show());
            break;
         }
      }
      appendToHTML(tabs.showTabsFinish()); 

   }

}
