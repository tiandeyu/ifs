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
*  File        : CharacteristicBasic.java 
*  Modified    :
*                 2006-09-13  sumelk - Bug 59368, Corrected erroneous translation constants. 
*                 2006-02-07  mapelk - Call 132572 fixed. set insertable property for Technical Class & Attributes
*                 2003-12-15  Gacolk - Web Alignment: Converted the 2 blocks to tabs and removed
*                                      obsolete methods clone and doReset.
*                 2003-10-12  Zahalk - Call ID 106477, Did some changes to show all the records.
*                 2003-09-16  pranlk - Call 102961
*                 2001-05-17  VAGU - Code Review
*  ASP2JAVA Tool  2001-03-12  VAGU - Created Using the ASP file CharacteristicBasic.asp
* ----------------------------------------------------------------------------
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CharacteristicBasic extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.CharacteristicBasic");


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
   private ASPField f;

   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   

   //===============================================================
   // Construction 
   //===============================================================
   public CharacteristicBasic(ASPManager mgr, String page_path)
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
      }

      tabs.saveActiveTab();
   }
       

   //-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
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


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();   
      
      int currrow = itemset1.getCurrentRowNo();
   
      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
   
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert("APPSRWCHARACTERISTICBASICNODATA: No data found.");
         headset.clear();
      } 
      itemset1.goTo(currrow);
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
   
      ASPQuery q = trans.addQuery(itemblk1);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk1);
   
      if ( itemset1.countRows() == 0 ) 
      {
         mgr.showAlert("APPSRWCHARACTERISTICBASICNODATA: No data found.");
         itemset1.clear();
      }
      headset.goTo(currrow);
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();   
      
      ASPCommand cmd = trans.addEmptyCommand("HEAD","TECHNICAL_CLASS_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
   
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();   

      ASPCommand cmd = trans.addEmptyCommand("ITEM1","TECHNICAL_ATTRIB_STD_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
   
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }

   public void  activateClasses()
   {
      tabs.setActiveTab(1);
   }

   public void  activateAttributes()
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
   
      f = headblk.addField("TECHNICAL_CLASS");
      f.setSize(15);
      f.setMandatory();
      f.setLabel("APPSRWCHARACTERISTICBASICTECHCLASS: Technical Class");
      f.setUpperCase();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(8);
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(35);
      f.setMandatory();
      f.setLabel("APPSRWCHARACTERISTICBASICDESCRIPTION: Description");
      f.setMaxLength(40);
   
      f = headblk.addField("DESCRIPTION_LONG");
      f.setSize(40);
      f.setLabel("APPSRWCHARACTERISTICBASICLONGDESC: Long Description");
      f.setMaxLength(50);
   
      headblk.setView("TECHNICAL_CLASS");
      headblk.defineCommand("TECHNICAL_CLASS_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWCHARACTERISTICBASICHD: Classes"));  
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
      headbar.enableCommand(headbar.SAVERETURN); 
      headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");  
      headbar.enableCommand(headbar.SAVENEW);
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

      headbar.addCustomCommand("activateClasses","Classes");
      headbar.addCustomCommand("activateAttributes","Attributes");

      headbar.removeCustomCommand("activateClasses");   
      headbar.removeCustomCommand("activateAttributes");


   
   //-----------------------------------------------------------------------
   //--------------------------- Attribute TAB -----------------------------
   //-----------------------------------------------------------------------
   
   
      itemblk1 = mgr.newASPBlock("ITEM1");
   
      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
        
      f = itemblk1.addField("ATTRIBUTE");
      f.setSize(15);
      f.setMandatory();
      f.setLabel("APPSRWCHARACTERISTICBASICATTRIB: Attribute");
      f.setUpperCase();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(15);
   
      f = itemblk1.addField("ATTRIB_TYPE");
      f.setSize(20);
      f.setMandatory();
      f.setLabel("APPSRWCHARACTERISTICBASICATTRIBTYPE: Type");
      f.setSelectBox();
      f.enumerateValues("TECHNICAL_ATTRIB_TYPE_API");
      f.setMaxLength(15);
   
      f = itemblk1.addField("ATTRIB_DESC");
      f.setSize(45);
      f.setMandatory();
      f.setLabel("APPSRWCHARACTERISTICBASICATTRIBDESC: Description");
      f.setMaxLength(40);
   
      itemblk1.setView("TECHNICAL_ATTRIB_STD");
      itemblk1.defineCommand("TECHNICAL_ATTRIB_STD_API","New__,Modify__,Remove__");
   
      itemset1 = itemblk1.getASPRowSet();
   
      itembar1 = mgr.newASPCommandBar(itemblk1);
   
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
   
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("APPSRWCHARACTERISTICBASICITM1: Attributes"));  
      itemtbl1.setWrap();
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);   
   
      itembar1.enableCommand(itembar1.SAVERETURN); 
      itembar1.defineCommand(itembar1.SAVERETURN,null,"checkItem1Fields(-1)");  
      itembar1.enableCommand(itembar1.SAVENEW);
      itembar1.defineCommand(itembar1.SAVENEW,null,"checkItem1Fields(-1)"); 
   
      //=================== Tabs =========================

      tabs = mgr.newASPTabContainer();
     
      tabs.addTab(mgr.translate("APPSRWCHARACTERISTICBASICCLASSES: Classes"),"javascript:commandSet('HEAD.activateClasses','')");
      tabs.addTab(mgr.translate("APPSRWCHARACTERISTICBASICATTRIBS: Attributes"),"javascript:commandSet('HEAD.activateAttributes','')");
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "APPSRWCHARACTERISTICBASICTITLE: Characteristic Basic";
   }

   protected String getTitle()
   {
      return "APPSRWCHARACTERISTICBASICTITLE: Characteristic Basic";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("APPSRWCHARACTERISTICBASICTITLE: Characteristic Basic"));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("APPSRWCHARACTERISTICBASICTITLE: Characteristic Basic"));


      if ((headlay.isVisible()) && ((itemlay1.isVisible())))
      {
         out.append(tabs.showTabsInit());
      }
      
      switch (tabs.getActiveTab())
      {
         case 1:
         {
            out.append(headlay.show());
            break;
         }
         case 2:
         {
            out.append(itemlay1.show());
            break;
         }
      }
      if ((headlay.isVisible()) && ((itemlay1.isVisible())))
      {
         out.append(tabs.showTabsFinish());
      }

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
