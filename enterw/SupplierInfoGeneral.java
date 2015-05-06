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
*  File        : SupplierInfoGeneral.java 
*  Modified    : Anil Padmajeewa
*  ASP2JAVA Tool  2001-01-22  - Created Using the ASP file SupplierInfoGeneral.asp
*  anpalk      : 21 Feb 2001    Code Review, Rename Blocks.
*  Modified    : Madhu  30-05-2001  Call # 65719
*  anpalk      : 06 June 2001  #64872 and changes in "getContent" method.      
*  Kupelk      : 2001-09-21 - # 69494 Added LOV for Association No.
*  anpalk      : 2001-09-24 - Add Confirm Box for Association No.
*  makalk      : 2001-09-25   Call # 69496 and changed the validation for Association No in the save return.
*       anpalk          : 2002-06-19    Call # 29625
*  KuPelk      : 2003-02-24 IID ARFI124N Added new fields as CORPORATE_FORM and 
*                CORPORATE_FORM_DESC  .
*  Nimalk      : 2003-07-29 SP4 Merge
*  MAWELK   : 2005-04-09   FIPR364 - Corrected web tags.
* ----------------------------------------------------------------------------
*  Date:   Sign:   Histroy:
* ------   ------  ---------------------------------------------------------
*  041022  samblk  FIJP335, added identifier reference   
*  050909  Jakalk  Code Cleanup, Removed doReset and clone methods.
*  060309  Chhulk  Call 131663 Added  javascript lovCorporateForm() and validateCountry()  
*  061227  Maselk  Merged LCS Bug 58216, Fixed SQL Injection threats.
*  070802  Thpelk  Call Id 146997, Corrected SUPPLIER_ID to allow only 20 characters.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class SupplierInfoGeneral extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.SupplierInfoGeneral");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBlock supplierInfoblk;
   private ASPRowSet supplierInfoset;
   private ASPCommandBar supplierInfobar;
   private ASPTable supplierInfotbl;
   private ASPBlockLayout supplierInfolay;
   private ASPBlock msgSetupblk;
   private ASPRowSet msgSetupset;
   private ASPCommandBar msgSetupbar;
   private ASPTable msgSetuptbl;
   private ASPBlockLayout msgSetuplay;
   private ASPBlock ourIdblk;
   private ASPRowSet ourIdset;
   private ASPCommandBar ourIdbar;
   private ASPTable ourIdtbl;
   private ASPBlockLayout ourIdlay;
   private ASPBlock diablk1;
   private ASPRowSet diaset1;
   private ASPCommandBar diabar1;
   private ASPTable diatbl1;
   private ASPBlockLayout dialay1;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private String activetab;
   private String arraySize;
   private boolean dia_ok;
   private String cus_id;
   private boolean asso_exist;
   private String cus_name;
   private ASPCommand cmd;
   private ASPBuffer buff;
   private String idname;
   private ASPBuffer data;
   private String supplierid;
   private ASPQuery qry;
   private ASPBuffer row;
   private String checkCondition;
   private String strCondition;
   private String supplier_id;
   private int selectedRow;
   private String msg;
   private String stringAssoBuff;

   //===============================================================
   // Construction 
   //===============================================================

   public SupplierInfoGeneral(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   protected void doReset() throws FndException
   {
      //Resetting mutable attributes

      trans   = null;
      activetab   = null;
      arraySize   = null;
      dia_ok   = false;
      cus_id   = null;
      asso_exist = false;
      cus_name   = null;
      cmd   = null;
      buff   = null;
      idname   = null;
      data   = null;
      supplierid   = null;
      qry   = null;
      row   = null;
      checkCondition   = null;
      strCondition   = null;
      supplier_id   = null;
      selectedRow   = 0;
      msg = null;
      stringAssoBuff = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      SupplierInfoGeneral page = (SupplierInfoGeneral)(super.clone(obj));

      // Initializing mutable attributes

      page.trans   = null;
      page.activetab   = null;
      page.arraySize   = null;
      page.dia_ok   = false;
      page.asso_exist = false;
      page.cus_id   = null;
      page.cus_name   = null;
      page.cmd   = null;
      page.buff   = null;
      page.idname   = null;
      page.data   = null;
      page.supplierid   = null;
      page.qry   = null;
      page.row   = null;
      page.checkCondition   = null;
      page.strCondition   = null;
      page.supplier_id   = null;
      page.selectedRow   = 0;
      page.msg = null;
      page.stringAssoBuff = null;

      // Cloning immutable attributes

      page.frm = page.getASPForm();
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();
      page.supplierInfoblk = page.getASPBlock(supplierInfoblk.getName());
      page.supplierInfoset = page.supplierInfoblk.getASPRowSet();
      page.supplierInfobar = page.supplierInfoblk.getASPCommandBar();
      page.supplierInfotbl = page.getASPTable(supplierInfotbl.getName());
      page.supplierInfolay = page.supplierInfoblk.getASPBlockLayout();
      page.msgSetupblk = page.getASPBlock(msgSetupblk.getName());
      page.msgSetupset = page.msgSetupblk.getASPRowSet();
      page.msgSetupbar = page.msgSetupblk.getASPCommandBar();
      page.msgSetuptbl = page.getASPTable(msgSetuptbl.getName());
      page.msgSetuplay = page.msgSetupblk.getASPBlockLayout();
      page.ourIdblk = page.getASPBlock(ourIdblk.getName());
      page.ourIdset = page.ourIdblk.getASPRowSet();
      page.ourIdbar = page.ourIdblk.getASPCommandBar();
      page.ourIdtbl = page.getASPTable(ourIdtbl.getName());
      page.ourIdlay = page.ourIdblk.getASPBlockLayout();
      page.diablk1 = page.getASPBlock(diablk1.getName());
      page.diaset1 = page.diablk1.getASPRowSet();
      page.diabar1 = page.diablk1.getASPCommandBar();
      page.diatbl1 = page.getASPTable(diatbl1.getName());
      page.dialay1 = page.diablk1.getASPBlockLayout();

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();


      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      activetab = ctx.readValue("ACTIVETAB","0");
      arraySize = ctx.readValue("ARRAYSIZE","0");
      dia_ok = ctx.readFlag("DIA_OK",false);
      cus_id = ctx.readValue("CID","");
      cus_name = ctx.readValue("CNAME","");
      stringAssoBuff = ctx.readValue("ASSOBUFF","");
      asso_exist = ctx.readFlag("ASSEXIST",false);

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());

      else if (mgr.dataTransfered())
         transSearch();

      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();

      else if (!mgr.isEmpty(mgr.getQueryStringValue("SUPPLIER_ID"))) //Code for Zoom function from Maintenanc
      {
         okFind();
         okFindMSGSETUP();
         okFindOURID();
      }

      else if (!mgr.isEmpty(mgr.getQueryStringValue("VENDOR_NO")))
         okFind();

      else if ( mgr.commandLinkActivated() )
         eval(mgr.commandLinkFunction());

      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFind();
         okFindMSGSETUP();
         okFindOURID();
      }
      else if ( mgr.buttonPressed("CANCEL") )
         cancel_button();
      else if ("OK".equals(mgr.readValue("COPYOK")))
         ok_button();
      adjust();

      ctx.writeValue("ACTIVETAB",activetab);
      ctx.writeValue("ARRAYSIZE",arraySize);  
      ctx.writeFlag("DIA_OK",dia_ok);
      ctx.writeFlag("ASSEXIST",asso_exist);
      ctx.writeValue("CID",cus_id);
      ctx.writeValue("CNAME",cus_name);
      ctx.writeValue("ASSOBUFF", stringAssoBuff);
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String value; 
      String corp_desc = null;
      String count_db = null;

      String strVal = mgr.readValue("VALIDATE");

      if ("CUS_ID".equals(strVal))
      {
         cmd = trans.addCustomFunction("VALID","Supplier_Info_API.Get_Name","COMPANY");
         cmd.addParameter("CUS_ID",mgr.readValue("CUS_ID"));
         trans = mgr.validate(trans);
         buff = trans.getBuffer("VALID/DATA");
         trans.clear();
         cus_name = buff.getValue("COMPANY");
         mgr.responseWrite(cus_name+"^");
      }

      if ("ASSOCIATION_NO".equals(strVal))
      {
         cmd = trans.addCustomFunction("ANOEXIST","Association_Info_API.Association_No_Exist","DUMMY");
         cmd.addParameter("ASSOCIATION_NO",mgr.readValue("ASSOCIATION_NO"));
         cmd.addParameter("PARTY_TYPE_DB","SUPPLIER");

         trans = mgr.perform(trans);

         stringAssoBuff = trans.getValue("ANOEXIST/DATA/DUMMY");

         if ("TRUE".equals(stringAssoBuff))
            value = "1";
         else
            value = "0";  

         mgr.responseWrite(value);
      }

      if ("ASSNO".equals(strVal))
      {
         cmd = trans.addCustomFunction("ANOEXIST","Association_Info_API.Association_No_Exist","DUMMY");
         cmd.addParameter("ASSO_NO",mgr.readValue("ASSO_NO"));
         cmd.addParameter("PARTY_TYPE_DB","SUPPLIER");

         trans = mgr.perform(trans);

         stringAssoBuff = trans.getValue("ANOEXIST/DATA/DUMMY");

         if ("TRUE".equals(stringAssoBuff))
            value = "1";
         else
            value = "0";  

         mgr.responseWrite(value);
      }

      if ( "COUNTRY".equals(strVal) )
      {
         cmd = trans.addCustomFunction("CONTRYDB","ISO_COUNTRY_API.Encode","COUNTRY_CODE");
         cmd.addParameter("COUNTRY_CODE",mgr.readValue("COUNTRY")); 

         trans = mgr.validate(trans);

         count_db = trans.getValue("CONTRYDB/DATA/COUNTRY_CODE");
         if (mgr.isEmpty(count_db))
            count_db = "";

         mgr.responseWrite(count_db +"^");               
      }


      if ( "CORPORATE_FORM".equals(strVal) )
      {
         cmd = trans.addCustomFunction("CORPDESC","CORPORATE_FORM_API.Get_Corporate_Form_Desc","CORPORATE_FORM_DESC");
         cmd.addParameter("COUNTRY_CODE");
         cmd.addParameter("CORPORATE_FORM");

         trans = mgr.validate(trans);

         corp_desc = trans.getValue("CORPDESC/DATA/CORPORATE_FORM_DESC");
         if (mgr.isEmpty(corp_desc))
            corp_desc = "";

         mgr.responseWrite(corp_desc +"^");               
      }

      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      ourIdset.clear(); 
      ourIdtbl.clearQueryRow();
      msgSetupset.clear();
      msgSetuptbl.clearQueryRow();

      cmd = trans.addEmptyCommand("SUPPLIER","SUPPLIER_INFO_API.New__",supplierInfoblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("SUPPLIER/DATA");
      supplierInfoset.addRow(data);
   }


   public void  newRowMSGSETUP()
   {
      ASPManager mgr = getASPManager();

      supplierid = supplierInfoset.getValue("SUPPLIER_ID");

      cmd = trans.addEmptyCommand("MSGSETUP","SUPPLIER_INFO_MSG_SETUP_API.New__",msgSetupblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MSGSETUP/DATA");
      data.setFieldItem("MSGSETUP_SUPPLIER_ID",supplierid);
      data.setFieldItem("MSGSETUP_METHOD_DEFAULT","FALSE");
      msgSetupset.addRow(data);
   }


   public void  newRowOURID()
   {
      ASPManager mgr = getASPManager();

      supplierid = supplierInfoset.getValue("SUPPLIER_ID");

      cmd = trans.addEmptyCommand("OURID","SUPPLIER_INFO_OUR_ID_API.New__",ourIdblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("OURID/DATA");
      data.setFieldItem("OURID_SUPPLIER_ID",supplierid);
      ourIdset.addRow(data);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(supplierInfoblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      supplierInfolay.setCountValue(toInt(supplierInfoset.getValue("N")));
      supplierInfoset.clear();   
   }


   public void  transSearch()
   {
      ASPManager mgr = getASPManager();

      buff = mgr.getTransferedData();
      row=buff.getBufferAt(0);
      if (row.itemExists("CHKITEM"))
      {

         checkCondition = row.getValue("CHKITEM");
         
         if ("InquiryLineRevision".equals(checkCondition))
         {
            row=buff.getBufferAt(0);
            strCondition = row.getValue("SUPPID");
            strCondition = strCondition.substring(2, strCondition.length()-2);
            
            qry = trans.addQuery(supplierInfoblk);
            qry.addWhereCondition("SUPPLIER_ID = ?");
            qry.addParameter("SUPPLIER_LIST", strCondition);
            qry.includeMeta("ALL");

            mgr.querySubmit(trans,supplierInfoblk);
            searchDetail();
         }
         else if ("InquiryLineRevisionCom".equals(checkCondition))
         {
            row=buff.getBufferAt(0);
            strCondition= row.getValue("SUPPID");
            strCondition = strCondition.substring(2, strCondition.length()-2);
            
            qry = trans.addQuery(supplierInfoblk);
            qry.addWhereCondition("SUPPLIER_ID = ?");
            qry.addParameter("SUPPLIER_LIST", strCondition);
            qry.includeMeta("ALL");

            mgr.querySubmit(trans,supplierInfoblk);
            searchDetail();
         }
         else
            okFind();
      }
      else
         okFind();
   }


   public void  searchDetail()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      if (supplierInfoset.countRows()>0)
      {
         supplier_id = supplierInfoset.getValue("SUPPLIER_ID");
         qry = trans.addQuery(ourIdblk);
         qry.addWhereCondition("SUPPLIER_ID = ?");
         qry.addParameter("SUPPLIER_ID", supplier_id);
         qry = trans.addQuery(msgSetupblk);
         qry.addWhereCondition("SUPPLIER_ID = ?");
         qry.addParameter("SUPPLIER_ID", supplier_id);
         qry.includeMeta("ALL");
         mgr.querySubmit(trans,ourIdblk);
      }
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      mgr.createSearchURL(supplierInfoblk);

      trans.clear();

      qry = trans.addQuery(supplierInfoblk);
      qry.addWhereCondition("PARTY_TYPE_DB = ?");
      qry.addParameter("PARTY_TYPE_DB", "SUPPLIER");
      qry.setOrderByClause("SUPPLIER_ID");

      if (mgr.dataTransfered())
      {
         dia_ok = false;
         qry.addOrCondition(mgr.getTransferedData());   
         supplierInfolay.setLayoutMode(supplierInfolay.SINGLE_LAYOUT);  
      }
      qry.includeMeta("ALL");

      mgr.querySubmit(trans,supplierInfoblk);

      if (supplierInfoset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("ENTERWSUPPLIERINFOGENERALNODATAFOUND: No data found."));
         supplierInfoset.clear();
      }

      if (mgr.dataTransfered())
      {
         okFindMSGSETUP();
         okFindOURID();
      }
   }


   public void  okFindMSGSETUP()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      msgSetupset.clear();

      if (supplierInfoset.countRows() > 0)
      {
         supplier_id = supplierInfoset.getValue("SUPPLIER_ID");

         qry = trans.addQuery(msgSetupblk);
         qry.addWhereCondition("SUPPLIER_ID = ?");
         qry.addParameter("SUPPLIER_ID", supplier_id);
         qry.includeMeta("ALL");

         int currrow = supplierInfoset.getCurrentRowNo();
         mgr.querySubmit(trans,msgSetupblk);

         supplierInfoset.goTo(currrow);
      }
   }


   public void  okFindOURID()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ourIdset.clear();

      if (supplierInfoset.countRows() > 0)
      {
         supplier_id = supplierInfoset.getValue("SUPPLIER_ID");

         qry = trans.addQuery(ourIdblk);
         qry.addWhereCondition("SUPPLIER_ID = ?");
         qry.addParameter("SUPPLIER_ID", supplier_id);
         qry.includeMeta("ALL");

         int currrow = supplierInfoset.getCurrentRowNo();
         mgr.querySubmit(trans,ourIdblk);

         supplierInfoset.goTo(currrow);
      }
   }         

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

   public void  countFindOURID()
   {
      ASPManager mgr = getASPManager();

      supplier_id = supplierInfoset.getValue("SUPPLIER_ID");

      qry = trans.addQuery(ourIdblk);
      qry.setSelectList("to_char(count(*)) N");
      qry.addWhereCondition("SUPPLIER_ID = ?");
      qry.addParameter("SUPPLIER_ID", supplier_id);
      mgr.submit(trans);
      ourIdlay.setCountValue(toInt(ourIdset.getValue("N")));
      ourIdset.clear();
   }


   public void  countFindMSGSETUP()
   {
      ASPManager mgr = getASPManager();

      supplier_id = supplierInfoset.getValue("SUPPLIER_ID");

      qry = trans.addQuery(msgSetupblk);
      qry.setSelectList("to_char(count(*)) N");
      qry.addWhereCondition("SUPPLIER_ID = ?");
      qry.addParameter("SUPPLIER_ID", supplier_id);
      mgr.submit(trans);
      msgSetuplay.setCountValue(toInt(msgSetupset.getValue("N")));
      msgSetupset.clear();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  supplierinvoice()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);

      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"invoiw/SupplierInvoice.page",buff);
   }


   public void  supplieraddress()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);

      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));
      mgr.transferDataTo("SupplierAddress.page",buff);
   }


   public void  supplierpayment()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);

      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"paylew/SupplierPayment.page",buff);
   }

//============================================call 55285 ==========  

   public void  purchaseaddressinfo()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"purchw/SupplierPurchaseAddress.page",buff);
   }


   public void  purchaseinfo()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));

      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"purchw/SupplierPurchase.page",buff);
   }

//-----------------------------------------------------------------------------
//----------------------------  DYNAMIC RMB FUNCTION  ------------------------------
//-----------------------------------------------------------------------------

   public void  setDynamicRmb()
   {
      ASPManager mgr = getASPManager();

      if (!mgr.isPresentationObjectInstalled("paylew/SupplierPayment.page"))
         supplierInfobar.removeCustomCommand("supplierpayment");

      if (!mgr.isPresentationObjectInstalled("enterw/SupplierAddress.page"))
         supplierInfobar.removeCustomCommand("supplieraddress");

      if (!mgr.isPresentationObjectInstalled("invoiw/SupplierInvoice.page"))
         supplierInfobar.removeCustomCommand("supplierinvoice");

      if (!mgr.isPresentationObjectInstalled("purchw/SupplierPurchaseAddress.page"))
         supplierInfobar.removeCustomCommand("purchaseaddressinfo");

      if (!mgr.isPresentationObjectInstalled("purchw/SupplierPurchase.page"))
         supplierInfobar.removeCustomCommand("purchaseinfo");
   }

//-----------------------------------------------------------------------------
//-------------------------  OVERVIEWMODE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      supplierInfoblk = mgr.newASPBlock("SUPPLIER");

      supplierInfoblk.addField("OBJID").
         setHidden();

      supplierInfoblk.addField("OBJVERSION").
         setHidden();

      supplierInfoblk.addField("SUPPLIER_ID").
         setSize(30).
         setMaxLength(20).
         setLabel("ENTERWSUPPLIERINFOGENERALSUPPLIERID: Supplier Id.").
         setReadOnly().
         setInsertable().    
         setUpperCase();

      supplierInfoblk.addField("NAME").
         setSize(30).
         setMandatory().
         setLabel("ENTERWSUPPLIERINFOGENERALNAME: Name");

      supplierInfoblk.addField("ASSOCIATION_NO").
         setSize(30).        
         setLabel("ENTERWSUPPLIERINFOGENERALASSOCIATIONNO: Association No");

      supplierInfoblk.addField("DEFAULT_LANGUAGE").
         setLabel("ENTERWSUPPLIERINFOGENERALDEFAULTLANGUAGE: Default Language").
         setSelectBox().
         setSize(30).
         enumerateValues("ISO_LANGUAGE_API").
         unsetSearchOnDbColumn();

      supplierInfoblk.addField("COUNTRY").
         setLabel("ENTERWSUPPLIERINFOGENERALCOUNTRY: Country").
         setSelectBox().
         setSize(30).
         enumerateValues("ISO_COUNTRY_API").
         unsetSearchOnDbColumn().
         setCustomValidation("COUNTRY","COUNTRY_CODE");

      supplierInfoblk.addField("SUPPLIERS_OWN_ID").
         setLabel("ENTERWSUPPLIERINFOGENERALOWNID: Suppliers Own ID").
         setSize(30);

      supplierInfoblk.addField("CREATION_DATE","Date").
         setSize(30).
         setLabel("ENTERWSUPPLIERINFOGENERALCREATIONDATE: Creation Date").
         setReadOnly();

      supplierInfoblk.addField("PARTY").
         setSize(11).
         setHidden();

      supplierInfoblk.addField("DEFAULT_DOMAIN").
         setMandatory().
         setHidden().
         setCheckBox("FALSE,TRUE");

      supplierInfoblk.addField("PARTY_TYPE").
         setSize(11).
         setMandatory().
         setHidden();

      supplierInfoblk.addField("PARTY_TYPE_DB").
         setSize(11).
         setMandatory().
         setHidden();

      supplierInfoblk.addField("BUSINESS_PARTNER").
         setHidden().
         setFunction("''");

      supplierInfoblk.addField("DUMMY").
         setHidden().
         setFunction("''");

      supplierInfoblk.addField("CLIENTVALUE").
         setHidden().
         setFunction("' '");

      supplierInfoblk.addField("SUPPLIER_LIST").
         setHidden().
         setFunction("' '");

      supplierInfoblk.addField("COUNTRY_CODE").
         setHidden(). 
         setFunction("ISO_COUNTRY_API.Encode(:COUNTRY)");

      supplierInfoblk.addField("CORPORATE_FORM").
         setSize(30).
         setLabel("ENTERWSUPPLIERINFOGENERALCORFORM: Form of Business").
         setDynamicLOV("CORPORATE_FORM","COUNTRY_CODE",675,600).    
         setInsertable().
         setCustomValidation("COUNTRY_CODE,CORPORATE_FORM","CORPORATE_FORM_DESC");

      supplierInfoblk.addField("CORPORATE_FORM_DESC").
         setSize(30).
         setLabel("ENTERWSUPPLIERINFOGENERALCORFORMDES: Form of Business Description").
         setFunction("CORPORATE_FORM_API.Get_Corporate_Form_Desc(ISO_COUNTRY_API.Encode(:COUNTRY), :CORPORATE_FORM)");

      supplierInfoblk.addField("DUMMY_NUM","Number").
         setFunction("''").
         setHidden();

      supplierInfoblk.addField("IDENTIFIER_REFERENCE").
         setLabel("ENTERWSUPPLIERINFOGENERALIDENTIFIERREF: Identifier Reference").
         setSize(30);

      supplierInfoblk.addField("IDENTIFIER_REF_VALIDATION").
         setLabel("ENTERWSUPPLIERINFOGENERALIDENTIFIERREFVAL: Identifier Ref Validation").
         setSelectBox().
         setSize(30).
         enumerateValues("IDENTIFIER_REF_VALIDATION_API");

      supplierInfoblk.setView("SUPPLIER_INFO");
      supplierInfoblk.defineCommand("SUPPLIER_INFO_API","New__,Modify__,Remove__");
      supplierInfoset = supplierInfoblk.getASPRowSet();

      supplierInfotbl = mgr.newASPTable(supplierInfoblk);
      supplierInfotbl.setTitle("ENTERWSUPPLIERINFOGENERALTITLE: Supplier - General Info.");
      supplierInfolay = supplierInfoblk.getASPBlockLayout();
      supplierInfolay.setDefaultLayoutMode(supplierInfolay.SINGLE_LAYOUT);  

      supplierInfobar = mgr.newASPCommandBar(supplierInfoblk);

      supplierInfobar.addCustomCommand("supplieraddress",mgr.translate("ENTERWSUPPLIERINFOGENERALSUPADDINFO: Address Info..."));
      supplierInfobar.addCustomCommand("supplierinvoice",mgr.translate("ENTERWSUPPLIERINFOGENERALSUPINVINFO: Invoice Info..."));
      supplierInfobar.addCustomCommand("supplierpayment",mgr.translate("ENTERWSUPPLIERINFOGENERALSUPPMTINFO: Payment Info..."));
      supplierInfobar.addCustomCommand("show_dialog",mgr.translate("ENTERWSUPPLIERINFOGENERALORDADDINFO: Copy Supplier...")); 
      supplierInfobar.addCustomCommand("purchaseaddressinfo",mgr.translate("ENTERWSUPPLIERINFOGENERALPURADDRINFO: Purchase Address Info..."));
      supplierInfobar.addCustomCommand("purchaseinfo",mgr.translate("ENTERWSUPPLIERINFOGENERALPURCHINFO: Purchase Info..."));
      supplierInfobar.defineCommand(supplierInfobar.SAVERETURN, null, "checkRemove");
      supplierInfobar.defineCommand(supplierInfobar.SAVENEW, null, "checkRemove");

      supplierInfobar.disableCommand(supplierInfobar.DUPLICATEROW);

      msgSetupblk = mgr.newASPBlock("MSGSETUP");

      msgSetupblk.addField("MSGSETUP_OBJID").
         setHidden().
         setDbName("OBJID");

      msgSetupblk.addField("MSGSETUP_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      msgSetupblk.addField("MESSAGE_CLASS").
         setSize(25).
         setDynamicLOV("MESSAGE_CLASS",600,445).
         setMandatory().
         setLabel("ENTERWSUPPLIERINFOGENERALMESSAGECLASS: Message Class").
         setUpperCase().
         setReadOnly().
         setInsertable();

      msgSetupblk.addField("MEDIA_CODE").
         setSize(25).
         setDynamicLOV("MESSAGE_MEDIA",600,445).
         setMandatory().
         setLabel("ENTERWSUPPLIERINFOGENERALMEDIACODE: Media Code").
         setUpperCase().
         setReadOnly().
         setInsertable();

      msgSetupblk.addField("MSGSETUP_ADDRESS").
         setSize(25).
         setDynamicLOV("MESSAGE_RECEIVER",600,445).
         setMandatory().
         setLabel("ENTERWSUPPLIERINFOGENERALMSGSETUPADDRESS: Address").
         setDbName("ADDRESS").
         setUpperCase().
         setReadOnly().
         setInsertable();

      msgSetupblk.addField("MSGSETUP_METHOD_DEFAULT").
         setSize(14).
         setMandatory().
         setLabel("ENTERWSUPPLIERINFOGENERALMSGSETUPMETHODDEFAULT: Method Default").
         setDbName("METHOD_DEFAULT").
         setCheckBox("FALSE,TRUE");

      msgSetupblk.addField("MSGSETUP_SUPPLIER_ID").
         setMandatory().
         setHidden().
         setDbName("SUPPLIER_ID").
         setUpperCase();

      msgSetupblk.setView("SUPPLIER_INFO_MSG_SETUP");
      msgSetupblk.defineCommand("SUPPLIER_INFO_MSG_SETUP_API","New__,Modify__,Remove__");
      msgSetupblk.setMasterBlock(supplierInfoblk);
      msgSetupset = msgSetupblk.getASPRowSet();

      msgSetuplay = msgSetupblk.getASPBlockLayout();
      msgSetuplay.setDefaultLayoutMode(msgSetuplay.MULTIROW_LAYOUT);

      msgSetupblk.setTitle(mgr.translate("ENTERWSUPPLIERINFOGENERALMSGSETUP: Message Setup"));

      msgSetupbar = mgr.newASPCommandBar(msgSetupblk);

      msgSetupbar.defineCommand(msgSetupbar.OKFIND,"okFindMSGSETUP");
      msgSetupbar.defineCommand(msgSetupbar.COUNTFIND,"countFindMSGSETUP");
      msgSetupbar.defineCommand(msgSetupbar.NEWROW,"newRowMSGSETUP");

      msgSetuptbl = mgr.newASPTable(msgSetupblk);
      msgSetuptbl.setTitle(mgr.translate("ENTERWSUPPLIERINFOGENERALMSGSETUP: Message Setup"));


      ourIdblk = mgr.newASPBlock("OURID");

      ourIdblk.addField("OURID_OBJID").
         setHidden().
         setDbName("OBJID");

      ourIdblk.addField("OURID_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      ourIdblk.addField("COMPANY").
         setSize(20).
         setDynamicLOV("COMPANY",600,445).
         setMandatory().
         setLabel("ENTERWSUPPLIERINFOGENERALCOMPANY: Company").
         setUpperCase();

      ourIdblk.addField("OUR_ID").
         setSize(20).
         setMandatory().
         setLabel("ENTERWSUPPLIERINFOGENERALOURID: Our ID").
         setUpperCase();

      ourIdblk.addField("OURID_SUPPLIER_ID").
         setMandatory().
         setHidden().
         setDbName("SUPPLIER_ID").
         setUpperCase();

      ourIdblk.setView("SUPPLIER_INFO_OUR_ID");
      ourIdblk.defineCommand("SUPPLIER_INFO_OUR_ID_API","New__,Modify__,Remove__");
      ourIdblk.setMasterBlock(supplierInfoblk);
      ourIdset = ourIdblk.getASPRowSet();

      ourIdbar = mgr.newASPCommandBar(ourIdblk);

      ourIdbar.defineCommand(ourIdbar.OKFIND,"okFindOURID");
      ourIdbar.defineCommand(ourIdbar.COUNTFIND,"countFindOURID");
      ourIdbar.defineCommand(ourIdbar.NEWROW,"newRowOURID");

      ourIdlay = ourIdblk.getASPBlockLayout();
      ourIdlay.setDefaultLayoutMode(ourIdlay.MULTIROW_LAYOUT);

      ourIdblk.setTitle(mgr.translate("ENTERWSUPPLIERINFOGENERALOURIDSUPP: Our ID at Supplier"));

      ourIdtbl = mgr.newASPTable(ourIdblk);
      ourIdtbl.setTitle(mgr.translate("ENTERWSUPPLIERINFOGENERALOURIDSUPP: Our ID at Supplier"));

      diablk1 = mgr.newASPBlock("DIA1");

      diablk1.addField("CUS_ID").
         setSize(20).
         setDynamicLOV("SUPPLIER_INFO",650,450).
         setLabel("ENTERWSUPPLIERINFOGENERALCUSID: Sipplier Id.").
         setCustomValidation("CUS_ID","CUS_NAME").
         setUpperCase().
         setFunction("''");

      diablk1.addField("CUS_NAME").
         setSize(20).
         setLabel("ENTERWSUPPLIERINFOGENERALCUSNAME: Supplier Name").
         setReadOnly().
         setFunction("''");

      diablk1.addField("NEW_ID").
         setSize(20).
         setLabel("ENTERWSUPPLIERINFOGENERALNEWID: New Supplier Id.").
         setUpperCase().
         setFunction("''");

      diablk1.addField("NEW_NAME").
         setSize(20).
         setLabel("ENTERWSUPPLIERINFOGENERALNEWNAME: New Supplier Name").
         setFunction("''");

      diablk1.addField("ASSO_NO").
         setSize(20).
         setLabel("ENTERWSUPPLIERINFOGENERALASSONO: Associaton No.").
         setFunction("''");

      diablk1.setView("DUAL");

      diaset1 = diablk1.getASPRowSet();
      diabar1 = mgr.newASPCommandBar(diablk1);
      diatbl1 = mgr.newASPTable(diablk1);

      diabar1.disableCommand(diabar1.OKFIND);
      diabar1.disableCommand(diabar1.CANCELFIND);
      diabar1.disableCommand(diabar1.COUNTFIND);

      dialay1 = diablk1.getASPBlockLayout();
      dialay1.setDialogColumns(1);
      dialay1.setDefaultLayoutMode(dialay1.MULTIROW_LAYOUT);
   }


   public void  adjust()
   {
      setDynamicRmb();
      ASPManager mgr = getASPManager();

      if (supplierInfoset.countRows() == 0)
      {
         supplierInfobar.disableCommand(supplierInfobar.FORWARD);
         supplierInfobar.disableCommand(supplierInfobar.BACKWARD);    
         supplierInfobar.disableCommand(supplierInfobar.DELETE);
         supplierInfobar.disableCommand(supplierInfobar.EDITROW);
         supplierInfobar.disableCommand(supplierInfobar.BACK);
         supplierInfobar.removeCustomCommand("supplieraddress");
         supplierInfobar.removeCustomCommand("supplierinvoice");
         supplierInfobar.removeCustomCommand("supplierpayment");
         supplierInfobar.removeCustomCommand("show_dialog");
         supplierInfobar.removeCustomCommand("purchaseaddressinfo");
         supplierInfobar.removeCustomCommand("purchaseinfo");
      }

      if ((msgSetupset.countRows() == 0 ) &&  !( msgSetuplay.isFindLayout()))
      {
         msgSetupbar.disableCommand(msgSetupbar.DUPLICATEROW);
         msgSetupbar.disableCommand(msgSetupbar.DELETE);
         msgSetupbar.disableCommand(msgSetupbar.EDITROW);
      }

      if (ourIdset.countRows() == 0)
      {
         ourIdbar.disableCommand(ourIdbar.DUPLICATEROW);
         ourIdbar.disableCommand(ourIdbar.DELETE);
         ourIdbar.disableCommand(ourIdbar.EDITROW);
      }

      mgr.getASPField("ASSOCIATION_NO").setDynamicLOV("ASSOCIATION_INFO",600,445);
      mgr.getASPField("ASSOCIATION_NO").setLOVProperty("ORDER_BY","PARTY_TYPE");      
      mgr.getASPField("ASSO_NO").setDynamicLOV("ASSOCIATION_INFO",600,445);
      mgr.getASPField("ASSO_NO").setLOVProperty("ORDER_BY","PARTY_TYPE");

      msg = mgr.translate("ENTERWSUPPLIERINFOGENERALASSONOERROR: Another business partner with the same association number is already registered. Do you want to use the same Association No.");
   }

   public void  ok_button()
   {
      ASPManager mgr = getASPManager();
      String new_id, comp, new_name, asso_no; 
      double nSuppId;

      dia_ok = false;
      asso_exist = false;
      dialay1.setLayoutMode(dialay1.MULTIROW_LAYOUT);

      cus_id = mgr.readValue("CUS_ID");
      new_id = mgr.readValue("NEW_ID");
      comp = ctx.findGlobal("COMPANY");
      new_name = mgr.readValue("NEW_NAME");
      asso_no = mgr.readValue("ASSO_NO");

      if (mgr.isEmpty(comp))
         cmd = trans.addCustomFunction("GLOBAL","User_Profile_SYS.Get_Default('COMPANY',Fnd_Session_API.Get_Fnd_User)","COMPANY");

      cmd = trans.addCustomCommand("COPYCUST","Supplier_Info_API.Copy_Existing_Supplier_Inv");
      cmd.addParameter("CUS_ID",cus_id);
      cmd.addParameter("NEW_ID",new_id);
      if (mgr.isEmpty(comp))
         cmd.addReference("COMPANY","GLOBAL/DATA");
      else
         cmd.addParameter("COMPANY",comp);

      cmd.addParameter("NEW_NAME",new_name);
      cmd.addParameter("ASSO_NO",asso_no);

      trans = mgr.perform(trans);
      trans.clear(); 

      if (mgr.isEmpty(new_id))
      {
         cmd = trans.addCustomFunction("GETPARTYTYPE","Party_Type_Api.Decode","CLIENTVALUE");
         cmd.addParameter("DUMMY","SUPPLIER");

         cmd = trans.addCustomFunction("GETNEWSUPPID","Party_Identity_Series_Api.Get_Next_Value","DUMMY_NUM");
         cmd.addReference("CLIENTVALUE","GETPARTYTYPE/DATA");

         trans = mgr.perform(trans);

         nSuppId = trans.getNumberValue("GETNEWSUPPID/DATA/DUMMY_NUM");
         trans.clear();

         if (isNaN(nSuppId))
            new_id = "";
         else
            new_id = toInt(nSuppId-1)+"";
      }

      qry = trans.addQuery(supplierInfoblk);
      qry.addWhereCondition("SUPPLIER_ID = ?");
      qry.addParameter("SUPPLIER_ID", new_id.toUpperCase());
      qry.includeMeta("ALL");
      mgr.submit(trans); 
   }


   public void  cancel_button()
   {
      dia_ok = false;
      asso_exist = false;
      dialay1.setLayoutMode(dialay1.MULTIROW_LAYOUT);
   }

//-----------------------------------------------------------------------------
//------------------------  SHOW ITEM BAR FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  show_dialog()
   {
      dia_ok = true;
      dialay1.setLayoutMode(dialay1.FIND_LAYOUT);

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);

      cus_id = supplierInfoset.getValue("SUPPLIER_ID");
      cus_name = supplierInfoset.getValue("NAME");
   }

//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "ENTERWSUPPLIERINFOGENERALTITLE: Supplier - General Info.";
   }

   protected String getTitle()
   {
      return "ENTERWSUPPLIERINFOGENERALTITLE: Supplier - General Info.";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("ENTERWSUPPLIERINFOGENERALTITLE: Supplier - General Info."));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("  <input type=\"hidden\" name=\"__DIA1_ROWSTATUS\" value=\"QueryMode__\">");
      out.append("<input type=\"hidden\" name=\"COPYOK\" value=\"\">");
      out.append(mgr.startPresentation("ENTERWSUPPLIERINFOGENERALTITLE: Supplier - General Info."));

      if (supplierInfolay.isVisible() && !dia_ok)
      {
         out.append("<!--");
         out.append(supplierInfoblk.generateHiddenFields());
         out.append("-->\n");
         out.append(supplierInfolay.show());
      }

      if (msgSetuplay.isVisible() && !dia_ok  && (supplierInfoset.countRows() > 0))
         out.append(msgSetuplay.show());

      if (ourIdlay.isVisible() && !dia_ok  && (supplierInfoset.countRows() > 0))
         out.append(ourIdlay.show());

      if (dia_ok)
      {
         out.append("<table border=\"0\"\n");
         out.append("  width=\"100%\" id=\"cntDIA1\" bgcolor=\"green\" class=\"BlockLayoutTable\" cellspacing=\"0\"\n");
         out.append("  cellpadding=\"4\">\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWSUPPLIERINFOGENERALCUSTOMID: Supplier Id"));
         out.append("</td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td nowrap><font class=\"WriteTextValue\"><input class=\"WriteTextValue\" type=\"text\"\n");
         out.append("      size=\"30\" value=\"");
         out.append(cus_id);
         out.append("\" name=\"CUS_ID\" OnChange=\"validateCusId(-1)\"><a\n");
         out.append("      href=\"javascript:lovCusId(-1)\"><img src=\"");                                              // 35962
         out.append(mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES")+"lov.gif\" width=\"20\" height=\"16\"\n");
         out.append("      border=\"0\" alt=\"List of values\"></a></font></td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("    </tr>\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWSUPPLIERINFOGENERALCUSTOMNAM: Supplier Name"));
         out.append("</td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td nowrap><font class=\"WriteTextValue\"><input class=\"WriteTextValue\" type=\"text\"\n");
         out.append("      size=\"30\" value=\"");
         out.append(cus_name);
         out.append(" \" name=\"CUS_NAME\" OnChange=\"validateCusName(-1)\" ReadOnly></font></td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("    </tr>\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWSUPPLIERINFOGENERALNEWCUSTOMID: New Supplier Id"));
         out.append("</td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td nowrap><font class=\"WriteTextValue\"><input class=\"WriteTextValue\" type=\"text\"\n");
         out.append("      size=\"30\" value name=\"NEW_ID\" OnChange=\"validateNewId(-1)\"></font></td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("    </tr>\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWSUPPLIERINFOGENERALNEWSUPMNAM: New Supplier Name"));
         out.append("</td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td nowrap><font class=\"WriteTextValue\"><input class=\"WriteTextValue\" type=\"text\"\n");
         out.append("      size=\"30\" value name=\"NEW_NAME\" OnChange=\"validateNewName(-1)\"></font></td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("    </tr>\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWSUPPLIERINFOGENERALASSOCNO: Associaton No"));
         out.append("</td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td nowrap><font class=\"WriteTextValue\"><input class=\"WriteTextValue\" type=\"text\"\n");
         out.append("      size=\"30\" value name=\"ASSO_NO\" OnChange=\"validateAssoNo(-1)\"><a\n");                   // 35962
         out.append("      href=\"javascript:lovAssoNo(-1)\"><img src=\"");                                             // 35962
         out.append(mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES")+"lov.gif\" width=\"20\" height=\"16\"\n");
         out.append("      border=\"0\" alt=\"List of values\"></a></font></td>\n");
         out.append("      <td width=\"10\"></td>\n");

         out.append("    </tr>\n");
         out.append("  </table>\n");
         out.append("  <font size=\"1\" face=\"Verdana\">");
         out.append(fmt.drawButton("OK",mgr.translate("ENTERWSUPPLIERINFOGENERALOK:    Ok   "),"onClick=\"checkAssNo()\""));
         out.append(" &nbsp;"); 
         out.append(fmt.drawSubmit("CANCEL",mgr.translate("ENTERWSUPPLIERINFOGENERALCANCEL:    Cancel   "),""));
         out.append("</font>"); 
      }


      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("function validateCusId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getValue_('CUS_ID',i)=='' )\n");
      appendDirtyJavaScript("   {   \n");
      appendDirtyJavaScript("           alert(\"ddd\");\n");
      appendDirtyJavaScript("           getField_('CUS_NAME',i).value = '';\n");
      appendDirtyJavaScript("           return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   r = __connect(\n");
      appendDirtyJavaScript("           '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CUS_ID'\n");
      appendDirtyJavaScript("           + '&CUS_ID=' + getValue_('CUS_ID',i)\n");
      appendDirtyJavaScript("           );\n");
      appendDirtyJavaScript("   if( checkStatus_(r,'CUS_ID',i,'Customer Id.') )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("           assignValue_('CUS_NAME',-1,0);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkRemove()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" assocNo = getValue_('ASSOCIATION_NO') \n");
      appendDirtyJavaScript(" if (assocNo == '' )\n ");
      appendDirtyJavaScript("   return true\n");
      appendDirtyJavaScript("   if( getValue_('ASSOCIATION_NO',1)=='' )\n");
      appendDirtyJavaScript("   {   \n");
      appendDirtyJavaScript("        getField_('ASSOCIATION_NO',1).value = '';\n");
      appendDirtyJavaScript("        return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   r = __connect(\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ASSOCIATION_NO'\n");
      appendDirtyJavaScript("        + '&ASSOCIATION_NO=' + getValue_('ASSOCIATION_NO',1)\n");
      appendDirtyJavaScript("        );\n");
      appendDirtyJavaScript("if( r > 0 )\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("       if (confirm('"+msg+"'))\n ");
      appendDirtyJavaScript("           return true\n");
      appendDirtyJavaScript("       else \n");
      appendDirtyJavaScript("           return false\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("    return true\n");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function checkAssNo()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if ( getValue_('ASSO_NO',1)=='' )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.COPYOK.value = 'OK';\n");
      appendDirtyJavaScript("      submit();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("        r = __connect(\n");
      appendDirtyJavaScript("           '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ASSNO'\n");
      appendDirtyJavaScript("           + '&ASSO_NO=' + getValue_('ASSO_NO',1)\n");
      appendDirtyJavaScript("           );\n");
      appendDirtyJavaScript("      if( r > 0 )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         if (confirm('"+msg+"'))\n ");
      appendDirtyJavaScript("         {\n");
      appendDirtyJavaScript("            document.form.COPYOK.value = 'OK';\n");
      appendDirtyJavaScript("            submit();\n");
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("         else \n"); 
      appendDirtyJavaScript("            return true;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else \n");
      appendDirtyJavaScript("      { \n");
      appendDirtyJavaScript("         document.form.COPYOK.value = 'OK';\n"); 
      appendDirtyJavaScript("         submit();\n");
      appendDirtyJavaScript("      } \n");
      appendDirtyJavaScript("   }\n");     
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateCountry(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   getField_('CORPORATE_FORM',i).value = ''; \n");
      appendDirtyJavaScript("   getField_('CORPORATE_FORM_DESC',i).value = ''; \n");
      appendDirtyJavaScript("   if( getValue_('COUNTRY',i)=='' ) \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      getField_('COUNTRY_CODE',i).value = ''; \n");
      appendDirtyJavaScript("      return; \n");
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   r = __connect(\n");
      appendDirtyJavaScript("      '");
      appendDirtyJavaScript(    mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=COUNTRY'+ '&COUNTRY=' + getValue_('COUNTRY',i) );  \n");
      appendDirtyJavaScript("   if( checkStatus_(r,'COUNTRY',i,'Country') ) \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("        assignValue_('COUNTRY_CODE',i,0); \n");
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("function lovCorporateForm(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   valCountryCode = getValue_('COUNTRY_CODE',i);\n");
      appendDirtyJavaScript("   if (valCountryCode == '' || valCountryCode == null )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('CORPORATE_FORM',i,\n");
      appendDirtyJavaScript("           APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CORPORATE_FORM&__FIELD=Form+of+Business&__INIT=1'\n");
      appendDirtyJavaScript("           + '&CORPORATE_FORM=' + getValue_('CORPORATE_FORM',i)\n");
      appendDirtyJavaScript("           + '&COUNTRY_CODE=NULL' \n");
      appendDirtyJavaScript("           ,600,445,'validateCorporateForm');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('CORPORATE_FORM',i,\n");
      appendDirtyJavaScript("           APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CORPORATE_FORM&__FIELD=Form+of+Business&__INIT=1'\n");
      appendDirtyJavaScript("           + '&CORPORATE_FORM=' + getValue_('CORPORATE_FORM',i)\n");
      appendDirtyJavaScript("           + '&COUNTRY_CODE=' + valCountryCode\n");
      appendDirtyJavaScript("           ,600,445,'validateCorporateForm');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");

      return out;
   }
}
