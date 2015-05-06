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
*  File        : CityCode.java 
*  Created     : 20031003   Gepelk  IID ARFI124N. Argentinean Sales Tax
*  MAWELK      : 2005-04-09 - FIPR364 - Corrected web tags.
*  Jakalk      : 2005-09-06 - Code Cleanup, Removed doReset and clone methods.
*  Maselk      : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CityCode extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CityCode");


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
   public CityCode(ASPManager mgr, String page_path)
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
      String city_name;
      val = mgr.readValue("VALIDATE");

      if ( "CITY_CODE".equals(val) ) 
      {
         cmd = trans.addCustomFunction("CITNAME","CITY_CODE_API.Get_City_Name","CITY_NAME");
         cmd.addParameter("ITEM0_COUNTRY_CODE");
         cmd.addParameter("ITEM0_STATE_CODE");
         cmd.addParameter("ITEM0_COUNTY_CODE");
         cmd.addParameter("CITY_CODE");
         
         trans = mgr.validate(trans);
         city_name = trans.getValue("CITNAME/DATA/CITY_NAME");
         
         if (mgr.isEmpty(city_name))
            city_name = "";
   
         mgr.responseWrite(city_name +"^");
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
      cmd = trans.addEmptyCommand("HEAD","COUNTY_CODE_API.New__",headblk);
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
         mgr.showAlert(mgr.translate("ENTERWCITYCODENODATAFOUND: No data found."));
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

      cmd = trans.addEmptyCommand("ITEM0","CITY_CODE_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
   
      cmd.setParameter("ITEM0_COUNTRY_CODE",headset.getValue("COUNTRY_CODE")); 
      cmd.setParameter("ITEM0_STATE_CODE",headset.getValue("STATE_CODE")); 
      cmd.setParameter("ITEM0_COUNTY_CODE",headset.getValue("COUNTY_CODE")); 
      
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

      qry.addWhereCondition("COUNTRY_CODE = ? and STATE_CODE = ? and COUNTY_CODE = ?");
      qry.addParameter("COUNTRY_CODE", headset.getValue("COUNTRY_CODE"));
      qry.addParameter("STATE_CODE",   headset.getValue("STATE_CODE"));
      qry.addParameter("COUNTY_CODE",  headset.getValue("COUNTY_CODE"));
      
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

          qry.addWhereCondition("COUNTRY_CODE = ? and STATE_CODE = ? and COUNTY_CODE = ?");
          qry.addParameter("COUNTRY_CODE", headset.getValue("COUNTRY_CODE"));
          qry.addParameter("STATE_CODE",   headset.getValue("STATE_CODE"));
          qry.addParameter("COUNTY_CODE",  headset.getValue("COUNTY_CODE"));
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
      cmd.setParameter("ITEM0_STATE_CODE",itemset0.getValue("STATE_CODE"));
      cmd.setParameter("ITEM0_COUNTY_CODE",itemset0.getValue("COUNTY_CODE"));
      cmd.setParameter("CITY_CODE",itemset0.getValue("CITY_CODE"));
      cmd.setParameter("CITY_NAME",itemset0.getValue("CITY_NAME"));
         
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
         setLabel("ENTERWCITYCODECOUNTRYCODE1: Country Code").
         setDynamicLOV("ISO_COUNTRY",650,450).
         setUpperCase();

      headblk.addField("DESCRIPTION").
         setLabel("ENTERWCITYCODEDESC: Description").
         setSize(30).
         setReadOnly().
         setFunction("ISO_COUNTRY_API.Get_Description(:COUNTRY_CODE)");

      headblk.addField("STATE_CODE").
         setSize(35).
         setMandatory().
         setLabel("ENTERWCITYCODESTATECODEHEAD: State Code").
         setDynamicLOV("STATE_CODES2",650,450).
         setUpperCase();

      headblk.addField("STATE_NAME").
         setSize(35).
         setLabel("ENTERWCITYCODECITYSTATENAME: State Name").
         setReadOnly().
         setFunction("STATE_CODES_API.Get_State_Name(:COUNTRY_CODE,:STATE_CODE)");

      headblk.addField("COUNTY_CODE").
         setSize(35).
         setMandatory().
         setLabel("ENTERWCITYCODECITYCOUNTYCODE: County Code").
         setDynamicLOV("COUNTY_CODE2",650,450).
         setUpperCase();

      headblk.addField("COUNTY_NAME").
         setSize(35).
         setLabel("ENTERWCITYCODECOUNTYNAME: County Name").
         setReadOnly();

      headblk.addField("CITY_PRESENTATION").
         setSize(10).
         setLabel("ENTERWCITYCODEPRESENTATION: City Presentation").
         setReadOnly().
         setFunction("ENTERP_ADDRESS_COUNTRY_API.Get_City_Presentation(:COUNTRY_CODE)");

      headblk.setView("COUNTY_CODE2");
      headblk.defineCommand("COUNTY_CODE_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.COUNTFIND,"countFind");

      headbar.disableCommand(headbar.NEWROW); 
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.DUPLICATEROW); 
         
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("ENTERWCITYCODEHEAD: Cities");
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
         setLabel("ENTERWCITYCODECOUNTRYCODE: Country Code").
         setDbName("COUNTRY_CODE").
         setUpperCase();
   
      itemblk0.addField("ITEM0_STATE_CODE").
         setSize(35).
         setMandatory().
         setHidden().
         setLabel("ENTERWCITYCODESTATECODE: State Code").
         setDbName("STATE_CODE").
         setUpperCase();

      itemblk0.addField("ITEM0_COUNTY_CODE").
         setSize(35).
         setMandatory().
         setHidden().
         setLabel("ENTERWCITYCODECOUNTYCODE: County Code").
         setDbName("COUNTY_CODE").
         setUpperCase();

      itemblk0.addField("CITY_CODE").
         setSize(35).
         setMandatory().
         setInsertable().
         setLabel("ENTERWCITYCODECITYCODE: City Code");
         
      itemblk0.addField("CITY_NAME").
         setSize(35).
         setMandatory().
         setInsertable().
         setLabel("ENTERWCITYCODENAME: City Name"); 

      itemblk0.setView("CITY_CODE2");
      itemblk0.defineCommand("CITY_CODE_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk); 
   
      itemset0 = itemblk0.getASPRowSet();
   
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.enableCommand(itembar0.FIND );   
      itembar0.defineCommand(itembar0.OKFIND,   "okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,    "newRowITEM0");
      itembar0.defineCommand(itembar0.DUPLICATEROW,"duplicateRow");
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle("ENTERWCITYCODEDETAIL: City Code Detail");
   
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
      else
      {
         mgr.getASPField("STATE_CODE").setLOVProperty("WHERE","COUNTRY_CODE='"+headset.getValue("COUNTRY_CODE")+"'");
         mgr.getASPField("COUNTY_CODE").setLOVProperty("WHERE","COUNTRY_CODE='"+headset.getValue("COUNTRY_CODE")+"' AND STATE_CODE='"+headset.getValue("STATE_CODE")+"'");
      }

      if(itemset0.countRows() == 0)
      {
         itembar0.disableCommand(headbar.BACK);
         itembar0.disableCommand(headbar.BACKWARD); 
      }
      
      strformTitle = mgr.translate("ENTERWCITYCODEHEAD2: Cities");  
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
      return "ENTERWCITYCODETITLE1: Cities";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("ENTERWCITYCODECITYES: Cities"));
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
