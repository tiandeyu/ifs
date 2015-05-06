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
*  File        : SupplierInfoOvw.java 
*  Modified    : Anil Padmajeewa
*    ASP2JAVA Tool  2001-01-22  - Created Using the ASP file SupplierInfoOvw.asp
*  KuPelk     : 2004-08-17   Added multirow Select.
*  Jakalk     : 2005-09-07 - Code Cleanup, Removed doReset and clone methods.
*  Maselk     : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.   
*  Thpelk     : 2007-08-02 - Call Id 146997, Corrected SUPPLIER_ID to allow only 20 characters.
*  Laselk     : 2009-11-04 - Bug 86284, Removed coding that changes whole applications global company.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class SupplierInfoOvw extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.SupplierInfoOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;
   private ASPBlock supplierInfoblk;
   private ASPRowSet supplierInfoset;
   private ASPCommandBar supplierInfobar;
   private ASPTable supplierInfotbl;
   private ASPBlockLayout supplierInfolay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private ASPQuery qry;
   private ASPBuffer data;

   //===============================================================
   // Construction 
   //===============================================================

   public SupplierInfoOvw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();  

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("IDENTITY")))
         okFind();
      adjust();
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      String company;

      qry = trans.addQuery(supplierInfoblk);

      if (!mgr.isEmpty(mgr.getQueryStringValue("IDENTITY")))
      {
         company = mgr.readValue("COMPANY");
         // Bug 86284, Removed coding that changes whole applications global company.        
         trans.clear();
         qry = trans.addEmptyQuery(supplierInfoblk);
         qry.addWhereCondition("SUPPLIER_ID = ?");
         qry.addParameter("SUPPLIER_ID", mgr.readValue("IDENTITY"));
      }

      qry.setOrderByClause("SUPPLIER_ID");
      qry.includeMeta("ALL");

      mgr.querySubmit(trans,supplierInfoblk);

      if (supplierInfoset.countRows() == 0)
      {
         mgr.showAlert("ENTERWSUPPLIERINFOOVWNODATA: No data found.");
         supplierInfoset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(supplierInfoblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      supplierInfolay.setCountValue(toInt(supplierInfoset.getValue("N")));
      supplierInfoset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd = null;

      cmd = trans.addEmptyCommand("MAIN","Supplier_Info_API.New__",supplierInfoblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MAIN/DATA");
      supplierInfoset.addRow(data);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  details()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer buff = null;
      ASPBuffer row = null;

      if (supplierInfolay. isMultirowLayout())
      {
         supplierInfoset.store();
         mgr.transferDataTo("SupplierInfoGeneral.page",
                            supplierInfoset.getSelectedRows("SUPPLIER_ID"));
      }
      else
      {
         buff = mgr.newASPBuffer();
         row = buff.addBuffer("1");

         row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));
         mgr.transferDataTo("SupplierInfoGeneral.page",buff);
      } 
   }  

   public void adjust()
   {
      if (supplierInfoset.countRows() == 0)
      {
         supplierInfobar.disableCommand(supplierInfobar.FORWARD);
         supplierInfobar.disableCommand(supplierInfobar.BACKWARD);
         supplierInfobar.disableCommand(supplierInfobar.DELETE);
         supplierInfobar.disableCommand(supplierInfobar.DUPLICATEROW);
         supplierInfobar.disableCommand(supplierInfobar.EDITROW);
         supplierInfobar.disableCommand(supplierInfobar.BACK);
         supplierInfobar.removeCustomCommand("details");         
      }

   } 

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ASPField f;

      supplierInfoblk = mgr.newASPBlock("SUPPLIER");

      supplierInfoblk.addField("OBJID").
         setHidden();

      supplierInfoblk.addField("OBJVERSION").
         setHidden();

      supplierInfoblk.addField("SUPPLIER_ID").
         setSize(25).
         setMaxLength(20).
         setMandatory().
         setLabel("ENTERWSUPPLIERINFOOVWSUPPLIERID: Identity").
         setReadOnly().
         setInsertable().
         setUpperCase();

      supplierInfoblk.addField("NAME").
         setSize(25).
         setMandatory().
         setLabel("ENTERWSUPPLIERINFOOVWNAME: Name");

      supplierInfoblk.addField("ASSOCIATION_NO").
         setSize(25).
         setLabel("ENTERWSUPPLIERINFOOVWASSOCIATIONNO: Association No");

      supplierInfoblk.addField("DEFAULT_LANGUAGE").
         setSize(15).
         enumerateValues("ISO_LANGUAGE_API").
         setSelectBox().
         unsetSearchOnDbColumn().
         setLabel("ENTERWSUPPLIERINFOOVWDEFAULTLANGUAGE: Default Language");

      supplierInfoblk.addField("COUNTRY").
         setSize(15).
         enumerateValues("ISO_COUNTRY_API").
         setSelectBox().
         unsetSearchOnDbColumn().
         setLabel("ENTERWSUPPLIERINFOOVWCOUNTRY: Country");

      supplierInfoblk.addField("CREATION_DATE","Date").
         setSize(25).
         setMandatory().
         setReadOnly().
         setInsertable().
         setLabel("ENTERWSUPPLIERINFOOVWCREATIONDATE: Creation Date");

      supplierInfoblk.addField("PARTY").
         setSize(8).
         setHidden().
         setLabel("ENTERWSUPPLIERINFOOVWPARTY: Party");

      supplierInfoblk.addField("PARTY_TYPE").
         setSize(8).
         setMandatory().
         setHidden().
         setLabel("ENTERWSUPPLIERINFOOVWPARTYTYPE: Party Type");

      supplierInfoblk.addField("DEFAULT_DOMAIN").
         setSize(8).
         setMandatory().
         setHidden().
         setLabel("ENTERWSUPPLIERINFOOVWDEFAULTDOMAIN: Default Domain");

      supplierInfoblk.setView("SUPPLIER_INFO");
      supplierInfoblk.defineCommand("SUPPLIER_INFO_API","New__,Modify__,Remove__");
      supplierInfoset = supplierInfoblk.getASPRowSet();

      supplierInfobar = mgr.newASPCommandBar(supplierInfoblk);
      supplierInfobar.setBorderLines(false,false); 
      supplierInfobar.addCustomCommand("details",mgr.translate("ENTERWSUPPLIERINFOOVWDETAILS: Details..."));
      supplierInfobar.enableMultirowAction();

      supplierInfotbl = mgr.newASPTable(supplierInfoblk);
      supplierInfotbl.setTitle("ENTERWSUPPLIERINFOOVWSUPPLIEROVW: Supplier Overview");
      supplierInfotbl.enableRowSelect();

      supplierInfolay = supplierInfoblk.getASPBlockLayout();
      supplierInfolay.setDefaultLayoutMode(supplierInfolay.MULTIROW_LAYOUT);
   }

//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "ENTERWSUPPLIERINFOOVWTITLE: Supplier Overview";
   }

   protected String getTitle()
   {
      return "ENTERWSUPPLIERINFOOVWTITLE: Supplier Overview";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(supplierInfolay.show());
   }
}
