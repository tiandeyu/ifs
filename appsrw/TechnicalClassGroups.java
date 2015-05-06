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
*  File        : TechnicalClassGroups.java 
*  Modified    :
*    ASP2JAVA Tool  2001-03-12  - Created Using the ASP file TechnicalClassGroups.asp
*                   2001-08-21  CHCR   Removed depreciated methods.
*                   2002-06-27  bakalk Set validation on Attribute over Template for Technical class.
*                   2003-06-13  NaSalk Removed calls to ClientUtil applet.
*                   2003-10-12  Zahalk - Call ID 106477, Did some changes to show all the records.
* ----------------------------------------------------------------------------
 * New Comments:
 * 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class TechnicalClassGroups extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.TechnicalClassGroups");


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
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private int currrow;
   private int itemcurrrow;
   private ASPCommand cmd;
   private ASPBuffer data;
   private String n;

   //===============================================================
   // Construction 
   //===============================================================
   public TechnicalClassGroups(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      q   = null;
      currrow   = 0;
      itemcurrrow   = 0;
      cmd   = null;
      data   = null;
      n   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      TechnicalClassGroups page = (TechnicalClassGroups)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.q   = null;
      page.currrow   = 0;
      page.itemcurrrow   = 0;
      page.cmd   = null;
      page.data   = null;
      page.n   = null;
      
      // Cloning immutable attributes
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk0 = page.getASPBlock(itemblk0.getName());
      page.itemset0 = page.itemblk0.getASPRowSet();
      page.itembar0 = page.itemblk0.getASPCommandBar();
      page.itemtbl0 = page.getASPTable(itemtbl0.getName());
      page.itemlay0 = page.itemblk0.getASPBlockLayout();
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
      trans = mgr.newASPTransactionBuffer();
      
      if(mgr.commandBarActivated())
        eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else
      {
         startup();
      }
   
      adjust();
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
      String val            = null;   
      String sSqlSelect     = null;
      String sSqlSelect2     = null;
      
      val = mgr.readValue("VALIDATE");
      
       if  (  "ATTRIBUTE".equals(val) ) 
      {  
         String descriptionNumeric  = null;
         String descriptionAlhpaNum = null;
         String technicalClass      = null;
         String attribute           = null;
         
         attribute = mgr.readValue( "ATTRIBUTE");
         technicalClass = mgr.readValue( "TECHNICAL_CLASS");
         
         trans.clear();
         
         sSqlSelect = "SELECT TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(ATTRIBUTE) NUMERIC from TECHNICAL_ATTRIB_NUMERIC where TECHNICAL_CLASS=? AND ATTRIBUTE=?";
         q          = trans.addQuery("GETNUMERICATT",sSqlSelect);
         q.addParameter("TECHNICAL_CLASS", technicalClass);
         q.addParameter("ATTRIBUTE", attribute);
         
         sSqlSelect2 = "SELECT TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(ATTRIBUTE) ALPHA from TECHNICAL_ATTRIB_ALPHANUM where TECHNICAL_CLASS=? AND ATTRIBUTE=?";
         q          = trans.addQuery("GETALPHAATT",sSqlSelect2);
         q.addParameter("TECHNICAL_CLASS", technicalClass);
         q.addParameter("ATTRIBUTE", attribute);
         
        trans = mgr.perform(trans);
         
         data                = trans.getBuffer("GETNUMERICATT/DATA");
         descriptionNumeric  = trans.getValue("GETNUMERICATT/DATA/NUMERIC");
         
         data                = trans.getBuffer("GETALPHAATT/DATA");
         descriptionAlhpaNum = trans.getValue("GETALPHAATT/DATA/ALPHA");
         
         trans.clear();
        
        if(mgr.isEmpty(descriptionNumeric) && mgr.isEmpty(descriptionAlhpaNum))
        {
            
             mgr.showError(mgr.translate("INVALIDVALUEFORFIELDATTRIBUTE: Invalid value for field[Attribute]"));
        }
        else if(!mgr.isEmpty(descriptionNumeric))
        {
            mgr.responseWrite( descriptionNumeric + "^" );
        }
        else
        {
            mgr.responseWrite( descriptionAlhpaNum + "^" );
        }
      }   
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
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


   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
   
      currrow = headset.getCurrentRowNo();
   
      q = trans.addQuery(itemblk0);
      q.addWhereCondition("TECHNICAL_CLASS = ?");
      q.addParameter("TECHNICAL_CLASS", headset.getValue("TECHNICAL_CLASS"));
      q.includeMeta("ALL");
   
      mgr.querySubmit(trans,itemblk0);
   
      headset.goTo(currrow);
      eval(itemset0.syncItemSets()); 
   }


   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      if ( itemset0.countRows() != 0 ) 
      { 
      trans.clear();
   
      currrow = headset.getCurrentRowNo();
      itemcurrrow = itemset0.getCurrentRowNo();
   
      q = trans.addQuery(itemblk1);
      q.addWhereCondition("TECHNICAL_CLASS = ?");
      q.addWhereCondition("GROUP_NAME = ?");
      q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
      q.addParameter("GROUP_NAME", itemset0.getRow().getValue("GROUP_NAME"));
      q.includeMeta("ALL");
   
      mgr.querySubmit(trans,itemblk1);
   
      headset.goTo(currrow);
      itemset0.goTo(itemcurrrow);
   
      }
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
   
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
   
      q.includeMeta("ALL");
   
      mgr.querySubmit(trans,headblk);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWTECHNICALCLASSGROUPSNODATA: No data found."));
         headset.clear();
      }
      else if ( headset.countRows() == 1 ) 
      {
        okFindITEM0(); 
        okFindITEM1(); 
      } 
   
      eval(headset.syncItemSets());
      eval(itemset0.syncItemSets());
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM0","TECHNICAL_GROUP_API.New__",itemblk0);
   
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);   
      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_TECHNICAL_CLASS",headset.getRow().getFieldValue("TECHNICAL_CLASS")); 
      itemset0.addRow(data);  
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM1","TECHNICAL_GROUP_SPEC_API.New__",itemblk1);
   
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);   
      data = trans.getBuffer("ITEM1/DATA");
      data.setFieldItem("ITEM1_TECHNICAL_CLASS",headset.getRow().getFieldValue("TECHNICAL_CLASS")); 
      data.setFieldItem("ITEM1_GROUP_NAME",itemset0.getRow().getFieldValue("GROUP_NAME")); 
      itemset1.addRow(data);  
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  clear()
   {

   
   }


   public void  count()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      n = headset.getRow().getValue("N");
      mgr.setStatusLine("APPSRWTECHNICALCLASSGROUPSQRYCNT: Query will retrieve &1 rows",n);
      
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
   
      currrow = headset.getCurrentRowNo();   
   
      q = trans.addQuery(itemblk0);
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

      trans.clear();
   
      currrow = headset.getCurrentRowNo();   
   
      q = trans.addQuery(itemblk1);
      q.addWhereCondition("TECHNICAL_CLASS = ?");
      q.addWhereCondition("GROUP_NAME = ?"); 
      q.addParameter("TECHNICAL_CLASS", headset.getValue("TECHNICAL_CLASS"));
      q.addParameter("GROUP_NAME", mgr.readValue("GROUP_NAME"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
   
      itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
      itemset1.clear();
      headset.goTo(currrow);     
   }


   public void  adjust()
   {

    if ( itemset0.countRows() == 0 ) 
        itembar1.disableCommand(itembar1.NEWROW);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

   
      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("TECHNICAL_CLASS");
      f.setSize(28);
      f.setMandatory();
      f.setLabel("APPSRWTECHNICALCLASSGROUPSTECHNICAL_CLASS: Technical Class");
      f.setMaxLength(25);
      f.setReadOnly();
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(32);
      f.setLabel("APPSRWTECHNICALCLASSGROUPSDESCRIPTION: Class Description");
      f.setMaxLength(40); 
      f.setReadOnly();  
   
      headblk.setView("TECHNICAL_CLASS");
      headset = headblk.getASPRowSet();
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWTECHNICALCLASSGROUPSHD: Technical Class Groups"));  
      headtbl.setWrap();
   
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.EDIT);
        headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);  
   
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
   
   //========================================================================
   //==================  ITEM0  =============================================
   //========================================================================
   
   
      itemblk0 = mgr.newASPBlock("ITEM0");
   
      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk0.addField("GROUP_NAME");
      f.setSize(18);
      f.setMandatory();
      f.setLabel("APPSRWTECHNICALCLASSGROUPSGROUP_NAME: Group Name");
      f.setMaxLength(20);
     f.setReadOnly();
     f.setInsertable(); 
   
      f = itemblk0.addField("GROUP_DESC");
      f.setSize(18);
      f.setMandatory();
      f.setLabel("APPSRWTECHNICALCLASSGROUPSGROUP_DESC: Group Desc");
      f.setMaxLength(30);
   
      f = itemblk0.addField("GROUP_ORDER");
      f.setSize(10);
      f.setLabel("APPSRWTECHNICALCLASSGROUPSGROUP_ORDER: Group Order");
      f.setMandatory();
      f.setMaxLength(25);
   
      f = itemblk0.addField("STD_SQ","Number");
      f.setSize(10);
      f.setLabel("APPSRWTECHNICALCLASSGROUPSSTD_SQ: Std Sq");
      f.setMaxLength(25);
   
      f = itemblk0.addField("ITEM0_TECHNICAL_CLASS");
      f.setSize(18);
      f.setHidden();   
      f.setDbName("TECHNICAL_CLASS");   
   
      itemblk0.setView("TECHNICAL_GROUP");
      itemblk0.defineCommand("TECHNICAL_GROUP_API","New__,Modify__,Remove__");
      itemset0 = itemblk0.getASPRowSet();
   
      itemblk0.setMasterBlock(headblk);
   
      itemblk0.setTitle(mgr.translate("APPSRWTECHNICALCLASSGROUPSBLK0TITLE: Group")); 
   
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("APPSRWTECHNICALCLASSGROUPSITM0: Group"));  
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
   
   //==============================================================================
   //================== Block1 ====================================================   
   //==============================================================================   
   
      itemblk1 = mgr.newASPBlock("ITEM1");
   
      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk1.addField("ATTRIBUTE");
      f.setSize(32);
      f.setDynamicLOV("TECHNICAL_ATTRIB_BOTH","TECHNICAL_CLASS",600,445);
      f.setMandatory();
      f.setLabel("APPSRWTECHNICALCLASSGROUPSATTRIB: Attribute");
      f.setMaxLength(30);
      f.setReadOnly();
      f.setInsertable(); 
      f.setCustomValidation("ATTRIBUTE,TECHNICAL_CLASS", "ITEM1_DESC");
   
      f = itemblk1.addField("SPEC_ORDER");
      f.setSize(10);
      f.setLabel("APPSRWTECHNICALCLASSGROUPSSPEC_ORDER: Spec Order");
      f.setMandatory();
      f.setMaxLength(15);
   
      f = itemblk1.addField("ITEM1_TECHNICAL_CLASS");
      f.setSize(18);
      f.setHidden();
      f.setDbName("TECHNICAL_CLASS");
   
      f = itemblk1.addField("ITEM1_GROUP_NAME");
      f.setSize(18);
      f.setMandatory();
      f.setLabel("APPSRWTECHNICALCLASSGROUPSGROUP_NAME: Group Name");
      f.setDbName("GROUP_NAME");
      f.setHidden();
   
      f = itemblk1.addField("ITEM1_DESC");
      f.setSize(10);
      f.setLabel("APPSRWTECHNICALCLASSGROUPSSATTRID: Description");
      f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ATTRIBUTE)"); 
      f.setMaxLength(25);
      f.setReadOnly();
   
      itemblk1.setView("TECHNICAL_GROUP_SPEC");
      itemblk1.defineCommand("TECHNICAL_GROUP_SPEC_API","New__,Modify__,Remove__");
      itemset1 = itemblk1.getASPRowSet();
   
      itemblk1.setMasterBlock(itemblk0);
   
      itemblk1.setTitle(mgr.translate("APPSRWTECHNICALCLASSGROUPSBLK1TITLE: Attributes")); 
   
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("APPSRWTECHNICALCLASSGROUPSITM1: Attributes"));  
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
   
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "APPSRWTECHNICALCLASSGROUPSTITLE: Technical Class Groups";
   }

   protected String getTitle()
   {
      return "APPSRWTECHNICALCLASSGROUPSTITLE: Technical Class Groups";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      String    root = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
      
      appendToHTML(headlay.show());
      if(itemlay0.isVisible() || itemlay1.isNewLayout())
      {
      appendToHTML(itemlay0.show());
      }
      if(itemlay1.isVisible()&& headlay.isSingleLayout())
      {
      appendToHTML(itemlay1.show());
      }
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//-----validation on Attribute-------\n");
      appendDirtyJavaScript("function validateAttribute(i)                                               \n");
      appendDirtyJavaScript("{                                                                           \n");
      appendDirtyJavaScript("   if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;                    \n");
      appendDirtyJavaScript("   setDirty();                                                              \n");
      appendDirtyJavaScript("   if( !checkAttribute(i) ) return;                                         \n");
      appendDirtyJavaScript("   window.status='Please wait for validation';                              \n");
      appendDirtyJavaScript("   root = '"+root+"';                                                       \n");
      //appendDirtyJavaScript("   if(root.charAt(root.length-1)==\"/\")                                    \n");  
      //appendDirtyJavaScript("      root = root.substring(0,root.length-2);                               \n");  
      appendDirtyJavaScript("   r = __connect(                                         \n");
      appendDirtyJavaScript("   root +'appsrw/TechnicalClassGroups.page?VALIDATE=ATTRIBUTE'             \n");
      appendDirtyJavaScript("    + '&ATTRIBUTE=' + URLClientEncode(getValue_('ATTRIBUTE',i))             \n");
      appendDirtyJavaScript("    + '&TECHNICAL_CLASS=' + URLClientEncode(getValue_('TECHNICAL_CLASS',i)) \n");
      appendDirtyJavaScript("    );                                                                      \n");
      appendDirtyJavaScript("   window.status='';                                                        \n");
      appendDirtyJavaScript("   if( checkStatus_(r,'ATTRIBUTE',i,'Attribute') )                          \n");
      appendDirtyJavaScript("   {                                                                        \n");
      appendDirtyJavaScript("   assignValue_('ITEM1_DESC',i,0);                                          \n");
      appendDirtyJavaScript("   }                                                                        \n");
      appendDirtyJavaScript("   else                                                                     \n");
      appendDirtyJavaScript("   {                                                                        \n");
      appendDirtyJavaScript("     document.forms[0].ATTRIBUTE.value=\"\";                                \n");
      appendDirtyJavaScript("     document.forms[0].ITEM1_DESC.value=\"\";                               \n");
      appendDirtyJavaScript("   }                                                                        \n");
      appendDirtyJavaScript("}                                                                           \n");
      appendDirtyJavaScript("\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      
   }

}
