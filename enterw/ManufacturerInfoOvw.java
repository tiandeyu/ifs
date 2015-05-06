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
*  File        : ManufacturerInfoOvw.java 
*  Modified    :
*    ASP2JAVA Tool  2001-01-23  - Created Using the ASP file ManufacturerInfoOvw.asp
*    Disali K. : 2001-02-09 - Made the file compilable after converting to Java
*    Disali K. : 2001-03-22 - Modifications for new webkit.
*    KuPelk    : 2004-08-17   Added multirow Select.
*    Jakalk    : 2005-06-16   B124919 Corrected.
*    Jakalk    : 2005-09-07 - Code Cleanup, Removed doReset and clone methods.
*    Thpelk    : 2007-08-02   Call Id 146997, Corrected MANUFACTURER_ID to allow only 20 characters.
*    Kanslk    : 2008-02-25 - Bug 70581, Modified okFind(),instead of 'submit()','querySubmit()' is used.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ManufacturerInfoOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.ManufacturerInfoOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;

   //===============================================================
   // Construction 
   //===============================================================
   public ManufacturerInfoOvw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();


      trans = mgr.newASPTransactionBuffer();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      adjust();
   }

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT GROUP FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = null;
      q = trans.addQuery(headblk);
      q.setOrderByClause("MANUFACTURER_ID");
      q.includeMeta("ALL");

      // Bug 70581, Begin
      mgr.querySubmit(trans,headblk);
      // Bug 70581, End.

      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("ENTERWMANUFACTURERINFOOVWNODATA: No data found.");
         headset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = null;
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      ASPCommand cmd = null;
      ASPBuffer data = null;
      cmd = trans.addEmptyCommand("MAIN","Manufacturer_Info_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MAIN/DATA");
      headset.addRow(data);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  details()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer buff = null;
      ASPBuffer row = null;

      if (headlay. isMultirowLayout())
      {
         headset.store();
         mgr.transferDataTo("ManufacturerInfo.page",
                            headset.getSelectedRows("MANUFACTURER_ID"));
      }
      else
      {
         buff = mgr.newASPBuffer();
         row = buff.addBuffer("1");

         row.addItem("MANUFACTURER_ID",headset.getValue("MANUFACTURER_ID"));
         mgr.transferDataTo("ManufacturerInfo.page",buff);
      } 

   } 

   public void adjust()
   {

      if (headset.countRows() == 0)
      {
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.BACK);
         headbar.removeCustomCommand("details");

      }

   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").
         setHidden();

      headblk.addField("PARTY_TYPE").
         setHidden();

      headblk.addField("DEFAULT_DOMAIN").
         setHidden();  

      headblk.addField("MANUFACTURER_ID").
         setSize(25).
         setMaxLength(20).
         setMandatory().
         setReadOnly().
         setInsertable().
         setLabel("ENTERWMANUFACTURERINFOOVWMANUFACTURERID: Identity").
         setUpperCase();

      headblk.addField("NAME").
         setSize(25).
         setMandatory().
         setLabel("ENTERWMANUFACTURERINFOOVWNAME: Name");

      headblk.addField("ASSOCIATION_NO").
         setSize(25).
         setLabel("ENTERWMANUFACTURERINFOOVWASSOCIATIONNO: Association No.");

      headblk.addField("DEFAULT_LANGUAGE").
         setSize(15).
         enumerateValues("ISO_LANGUAGE_API").
         unsetSearchOnDbColumn().
         setSelectBox().
         setLabel("ENTERWMANUFACTURERINFOOVWDEFAULTLANGUAGE: Default Language");

      headblk.addField("COUNTRY").
         setSize(12).
         enumerateValues("ISO_COUNTRY_API").
         unsetSearchOnDbColumn().
         setSelectBox().
         setLabel("ENTERWMANUFACTURERINFOOVWCOUNTRY: Country");

      headblk.addField("CREATION_DATE","Date").
         setSize(25).
         setReadOnly().
         setInsertable().
         setLabel("ENTERWMANUFACTURERINFOOVWCREATIONDATE: Creation Date");

      headblk.setView("MANUFACTURER_INFO");
      headblk.defineCommand("MANUFACTURER_INFO_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("details", mgr.translate("ENTERWMANUFACTURERINFOOVWDTAIL: Details..."));
      headbar.enableMultirowAction();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("ENTERWMANUFACTURERINFOOVWMANUFACTUREROVW: Manufacturer Overview");
      headtbl.enableRowSelect();

      headlay = headblk.getASPBlockLayout();
      headlay.setFieldOrder("MANUFACTURER_ID");
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWMANUFACTURERINFOOVWTITLE: Manufacturer Overview";
   }

   protected String getTitle()
   {
      return "ENTERWMANUFACTURERINFOOVWTITLE: Manufacturer Overview";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(headlay.show());
   }

}
