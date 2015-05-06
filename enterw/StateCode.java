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
*  File        : StateCode.java 
*  Created     : 20031003   Gepelk  IID ARFI124N. Argentinean Sales Tax
*  Modified    :
*  MAWELK      2005-04-09   - FIPR364 - Corrected of web tags.
*  Jakalk      : 2005-09-07 - Code Cleanup, Removed doReset and clone methods.
*  Maselk      : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class StateCode extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.StateCode");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
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
   private ASPBlock idblk;
   private ASPField f;
   private ASPBlock b;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean blniscount;
   private ASPCommand cmd;
   private ASPTransactionBuffer cmd1;
   private ASPBuffer data;
   private ASPQuery qry;
   private String val;
   private int intcurrrow;
   private String strformTitle;

   //===============================================================
   // Construction 
   //===============================================================
   public StateCode(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

   
      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      blniscount = ctx.readFlag("ISCOUNT",false);
   
      if(mgr.commandBarActivated())
          eval(mgr.commandBarFunction()); 
      else if(mgr.dataTransfered())
          okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
          validate();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
          okFind();
          okFindITEM0();
      }
      else
         submitHead();

      adjust(); 
       
      ctx.writeFlag("ISCOUNT",blniscount);
   
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   public void  submitHead()
   {
      itembar0.enableCommand(headbar.SUBMIT);
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

  public void  validate()
  {
      ASPManager mgr = getASPManager();
      String state_name;
      val = mgr.readValue("VALIDATE");

      if ( "STATE_CODE".equals(val) ) 
      {
         cmd = trans.addCustomFunction("STANAME","STATE_CODES_API.Get_State_Name","STATE_NAME");
         cmd.addParameter("ITEM0_COUNTRY_CODE");
         cmd.addParameter("STATE_CODE");
         
         trans = mgr.validate(trans);
         state_name = trans.getValue("STANAME/DATA/STATE_NAME");
         
         if (mgr.isEmpty(state_name))
            state_name = "";
   
         mgr.responseWrite(state_name +"^");
         mgr.endResponse();    
      }
   }
      
//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      itemset0.clear();
      cmd = trans.addEmptyCommand("HEAD","ENTERP_ADDRESS_COUNTRY_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
   
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");  
   
      headset.addRow(data);
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      headset.clear();
      itemset0.clear();
      qry = trans.addQuery(headblk);
   
      headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      qry.includeMeta("ALL");
   
      mgr.querySubmit(trans,headblk);
      mgr.createSearchURL(headblk);

      
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("ENTERWSTATECODEENODATAFOUND: No data found."));
         headset.clear();
      }

      else
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFindITEM0();
      }
      if(mgr.dataTransfered())
      {
         okFindITEM0();
      }

   }
       
   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      String n;
      qry = trans.addQuery(headblk);

      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      n = headset.getRow().getValue("N");

      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
      trans.clear();
   } 
   

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM0","STATE_CODES_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
   
      cmd.setParameter("ITEM0_COUNTRY_CODE",headset.getValue("COUNTRY_CODE")); 
      
      int saveFlag = 1; 
      int selectedrows= itemset0.countRows();
   
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
         
      itemset0.addRow(data);
   }
   
   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(itemblk0);
      qry.addWhereCondition("COUNTRY_CODE = ? and STATE_CODE = ?"); 
      qry.addParameter("COUNTRY_CODE", headset.getValue("COUNTRY_CODE"));
      qry.addParameter("STATE_CODE", headset.getValue("STATE_CODE"));
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getValue("N")));
      itemset0.clear();
   }

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      itemset0.clear();
      trans.clear();
      if ( headset.countRows() != 0 ) 
      { 
          qry = trans.addQuery(itemblk0); 
          qry.addWhereCondition("COUNTRY_CODE = ?");
          qry.addParameter("COUNTRY_CODE", headset.getValue("COUNTRY_CODE"));
          qry.includeMeta("ALL");
          intcurrrow = headset.getCurrentRowNo();
          mgr.querySubmit(trans,itemblk0);
          headset.goTo(intcurrrow);
      }
   }

   public void  duplicateRow()
   {
      ASPManager mgr = getASPManager();

   
      itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
    
   
      itemset0.goTo(itemset0.getRowSelected()); 
   
      cmd = trans.addEmptyCommand("ITEM0","STATE_CODES_API.New__",itemblk0);   
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_COUNTRY_CODE",itemset0.getValue("COUNTRY_CODE"));  
      cmd.setParameter("STATE_CODE",itemset0.getValue("STATE_CODE"));
      cmd.setParameter("STATE_NAME",itemset0.getValue("STATE_NAME"));
         
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      itemset0.addRow(data);
   
   }   
//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  clear()
   {
      int status_line;
      int rowcount;

      headset.clear();
      headtbl.clearQueryRow();
      itemset0.clear();
      itemtbl0.clearQueryRow();
      status_line = rowcount = intcurrrow = 0;
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

   public void  ok()
   {
      ASPManager mgr = getASPManager();

      boolean multirow = false;
    
      trans = mgr.perform(trans);
      trans.clear();
         
      multirow = true;
      itemtbl0.clearQueryRow();
      itemset0.setFilterOff();
      itemset0.unselectRows();
   
   }

//-----------------------------------------------------------------------------
//-------------------------  OVERVIEWMODE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  overviewMode()
   {

      headset.setFilterOff();
      headset.unselectRows();
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      mgr.beginASPEvent();
   
      headblk = mgr.newASPBlock("HEAD");
   
      headblk.addField("OBJID").
         setHidden();
   
      headblk.addField("OBJVERSION").
         setHidden();
     
      headblk.addField("COUNTRY_CODE").
         setSize(02).
         setMandatory().
         setLabel("ENTERWSTATECODEECOUNTRYCODE: Country Code").
         setDynamicLOV("ISO_COUNTRY",650,450).
         setUpperCase();

      headblk.addField("DESCRIPTION").
         setLabel("ENTERWSTATECODESTATECOUNTRYNAME: Description").
         setSize(30).
         setReadOnly().
         setFunction("ISO_COUNTRY_API.Get_Description(:COUNTRY_CODE)");

      headblk.addField("STATE_PRESENTATION").
         setSize(10).
         setLabel("ENTERWSTATECODESTATEPRESENTATION: State Presentation").
         setReadOnly().
         setSelectBox().
         enumerateValues("PRESENTATION_TYPE_API");
      
      headblk.setView("ENTERP_ADDRESS_COUNTRY2");
      headblk.defineCommand("ENTERP_ADDRESS_COUNTRY_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.COUNTFIND,"countFind");

      headbar.disableCommand(headbar.NEWROW); 
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.DUPLICATEROW); 
         
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("ENTERWSTATECODESTATECODEHEAD: State Code");
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
      itemblk0 = mgr.newASPBlock("ITEM0");
   
      itemblk0.addField("ITEM0_OBJID").
         setHidden().
         setDbName("OBJID");
   
      itemblk0.addField("ITEM0_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");
      
      itemblk0.addField("ITEM0_COUNTRY_CODE").
         setSize(2).
         setMandatory().
         setHidden().
         setLabel("ENTERWSTATECODEITEM0COUNTRYCODE: Country Code").
         setDbName("COUNTRY_CODE").
         setUpperCase();
   
      itemblk0.addField("STATE_CODE").
         setSize(35).
         setMandatory().
         setInsertable().
         setDynamicLOV("STATE_CODES","COUNTRY_CODE",675,600).
         setLabel("ENTERWSTATECODEITEM0STATECODE: State Code");
         
      itemblk0.addField("STATE_NAME").
         setSize(35).
         setMandatory().
         setInsertable().
         setLabel("ENTERWSTATECODEITEM0STATENAME: State Name"); 

      itemblk0.setView("STATE_CODES2");
      itemblk0.defineCommand("STATE_CODES_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk); 
   
      itemset0 = itemblk0.getASPRowSet();
   
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.enableCommand(itembar0.FIND );   
      itembar0.defineCommand(itembar0.OKFIND,   "okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,    "newRowITEM0");
      itembar0.defineCommand(itembar0.DUPLICATEROW,"duplicateRow");
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle("ENTERWSTATECODESTATECODEDETAIL: State Code Detail");
   
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(3);
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
                        
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();
	      
      if(headset.countRows() == 0)
      {
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.BACKWARD); 
      }
		if(itemset0.countRows() == 0)
      {
         itembar0.disableCommand(headbar.BACK);
         itembar0.disableCommand(headbar.BACKWARD); 
      }
      
      strformTitle = mgr.translate("ENTERWSTATECODESTATES: States");  
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return strformTitle;
   }

   protected String getTitle()
   {
      return "ENTERWSTATECODESTATECODETITLE1: States";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("ENTERWSTATECODESTATECODETITLE2: States"));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(strformTitle));
      if(headlay.isVisible())
      {
      out.append(headlay.show());
      }
      if(itemlay0.isVisible()&& headset.countRows()>0)
      {
      out.append(headblk.generateHiddenFields());
      out.append(itemlay0.show());
      }
      appendDirtyJavaScript("//---------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//----------------------------------------------------------------------------\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
