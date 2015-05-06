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
*  File        : CustomerInfo.java 
*  Modified    : Anil Padmajeewa
*  ASP2JAVA Tool 2001-01-22  - Created Using the ASP file CustomerInfo.asp
*  anpalk      : Code Review, Rename Blocks descriptivelly.
*  anpalk      : 06 June 2001 #64872 and changes in "getContent" method 
*  anpalk               : 2001-09-04 - #68520 Did Changes in "okFind()" function.
*  anpalk               : 2001-09-04 - #68519 Overwrite saveReturnCUSTOMERINFO & saveNewCUSTOMERINFO .
*  samblk      : 2002-03-21 : Call # 80071 Removed the hardcode HTML part from the Copy Customer Dlg. 
*  KuPelk      : 2003-02-24 : IID ARFI124N Added new field as CORPORATE_FORM & CORPORATE_FORM_DESC.
*  Jakalk      : 2005-09-06 : Code Cleanup, Removed doReset and clone methods.
*  Chhulk      : 2006-01-27 : Used COUNTRY_CODE (instead of COUNTRY_DB) in LOV for field CORPORATE_FORM.
*  Chhulk      : 2006-02-20 : Call 133585  Changed Validation method for Editing Customer Info.
*  Chhulk      : 2006-03-08 : Call 131663 Added  javascript lovCorporateForm() and validateCountry()
*  Maselk      : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.
*  Thpelk      : 2007-08-02 : Call Id 146997, Corrected CUSTOMER_ID to allow only 20 characters.
* ------------------------------------------------------------------------------------------
*/         

package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.Vector;


public class CustomerInfo extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CustomerInfo");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBlock customerInfoblk;
   private ASPRowSet customerInfoset;
   private ASPCommandBar customerInfobar;
   private ASPTable customerInfotbl;
   private ASPBlockLayout customerInfolay;
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
   private String cus_id;
   private String cus_name;
   private boolean dia_ok;
   private String customerNo;
   private ASPCommand cmd;
   private ASPBuffer buff;
   private ASPBuffer data;
   private ASPBuffer row;
   private ASPQuery qry;
   private String customer_id;
   private int curr_row;
   private String msg;
   private String stringAssoBuff;
   private String cus_id_tag;
   private String asso_no_tag;
   private ASPField fCUS_ID;
   private ASPField fASSO_NO;

   //===============================================================
   // Construction 
   //===============================================================
   
   public CustomerInfo(ASPManager mgr, String page_path)
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
      cus_id = ctx.readValue("CID","");
      cus_name = ctx.readValue("CNAME","");             
      dia_ok = ctx.readFlag("DIA_OK",false);
      stringAssoBuff = ctx.readValue("ASSOBUFF","");
      
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
    
      else if(mgr.dataTransfered())
         okFind();
      
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      
      else if( mgr.commandLinkActivated() )
         eval(mgr.commandLinkFunction());
      
      else if (!mgr.isEmpty(mgr.getQueryStringValue("CUSTOMER_ID")))
      {
         okFind();
         customerInfolay.setLayoutMode(customerInfolay.SINGLE_LAYOUT);
         okFindMSGSETUP();
         okFindOURID();
      }
      
      else if (!mgr.isEmpty(mgr.getQueryStringValue("CUSTOMER_NO")))
      {
         customerNo = mgr.readValue("CUSTOMER_NO");
         searchZoom();   
         customerInfolay.setLayoutMode(customerInfolay.SINGLE_LAYOUT);
         okFindMSGSETUP();
         okFindOURID();
      } 
      
      else if(!mgr.isEmpty(mgr.getQueryStringValue("IDENTITY")))
      {
         okFind();
         okFindOURID();      
      } 
      
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFind();
         okFindOURID();
      }
      else if( mgr.buttonPressed("CANCEL") )
         cancel_button();
      else if("OK".equals(mgr.readValue("COPYOK")))
         ok_button();

      adjust();
      
      ctx.writeFlag("DIA_OK", dia_ok);
      ctx.writeValue("CID", cus_id);
      ctx.writeValue("CNAME", cus_name);        
      ctx.writeValue("ASSOBUFF", stringAssoBuff);
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String strVal = null;
      String assoNo = null;
      String corp_desc;    
      String count_db = null;
      String exist;
      String sBeforeEditAssNo;

      strVal = mgr.readValue("VALIDATE");
 
      if  ( "CUS_ID".equals(strVal) ) 
      {
         cmd = trans.addCustomFunction("VALID","Customer_Info_API.Get_Name","COMPANY");
         cmd.addParameter("CUS_ID",mgr.readValue("CUS_ID"));
         trans = mgr.validate(trans);
         buff = trans.getBuffer("VALID/DATA");
         trans.clear();
         cus_name = buff.getValue("COMPANY");
         mgr.responseWrite(cus_name+"^");
      }
      
      else if ("ASSOCIATION_NO".equals(strVal))
      {
         assoNo = mgr.readValue("ASSOCIATION_NO");         
         sBeforeEditAssNo = mgr.readValue("ASS_BEFORE_EDIT");  

         cmd = trans.addCustomFunction("ASSOCIATENO","Association_Info_API.Association_No_Exist","DUMMY");
         cmd.addParameter("ASSOCIATION_NO",assoNo);
         cmd.addParameter("PARTY_TYPE_DB","CUSTOMER");
         
         trans = mgr.perform(trans);

         exist = trans.getValue("ASSOCIATENO/DATA/DUMMY");

         if (assoNo.equals(sBeforeEditAssNo)) 
            stringAssoBuff = "0"; 
         else 
         {
            if ("TRUE".equals(exist))
               stringAssoBuff = "1";
            else
               stringAssoBuff = "0";
         }
         mgr.responseWrite(stringAssoBuff);
      }

      if ("ASSNO".equals(strVal)) 
      {
         cmd = trans.addCustomFunction("ANOEXIST","Association_Info_API.Association_No_Exist","DUMMY");
         cmd.addParameter("ASSO_NO",mgr.readValue("ASSO_NO"));
         cmd.addParameter("PARTY_TYPE_DB","SUPPLIER");

         trans = mgr.perform(trans);

         exist = trans.getValue("ANOEXIST/DATA/DUMMY");

         if ("TRUE".equals(exist))
            stringAssoBuff = "1";
         else
            stringAssoBuff = "0";  

         mgr.responseWrite(stringAssoBuff);
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

      cmd = trans.addEmptyCommand("CUSTOMER","CUSTOMER_INFO_API.New__",customerInfoblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("CUSTOMER/DATA");
      customerInfoset.addRow(data);
   }           


   public void  newRowMSGSETUP()
   {
      ASPManager mgr = getASPManager();
      String strCustomerId = null;

      strCustomerId = customerInfoset.getValue("CUSTOMER_ID");
   
      cmd = trans.addEmptyCommand("MSGSETUP","CUSTOMER_INFO_MSG_SETUP_API.New__",msgSetupblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MSGSETUP/DATA");
      data.setFieldItem("MSGSETUP_CUSTOMER_ID", strCustomerId);
      data.setFieldItem("MSGSETUP_METHOD_DEFAULT","FALSE");
      msgSetupset.addRow(data);
   }


   public void  newRowOURID()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("OURID","CUSTOMER_INFO_OUR_ID_API.New__",ourIdblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("OURID/DATA");
      data.setFieldItem("OURID_CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      ourIdset.addRow(data);
   }
   
   public void saveReturnCUSTOMERINFO()
   {
      ASPManager mgr = getASPManager();
      int curRowNo = 0;
      int countRow = 0;
      String defLang = null;
         
      customerInfoset.setValue("ASS_BEFORE_EDIT","");

      countRow = customerInfoset.countRows();
      curRowNo = customerInfoset.getCurrentRowNo();
      customerInfoset.changeRow();
      defLang = customerInfoset.getValue("DEFAULT_LANGUAGE");
      defLang = ( defLang == null? "": defLang);
      
      if(defLang.equals(""))
         mgr.showError(mgr.translate("ENTERWCUSTOMERINFODEFLANG: The field [Default Language] must have a value."));    
      
      mgr.submit(trans);
      customerInfotbl.clearQueryRow();  
      customerInfoset.goTo(curRowNo);
   }


   public void saveNewCUSTOMERINFO()
   {
       saveReturnCUSTOMERINFO();
       trans.clear();
       newRow();
   }
   

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      String strCompany = null;

      mgr.createSearchURL(customerInfoblk);
   
      qry = trans.addQuery(customerInfoblk);
      qry.setOrderByClause("CUSTOMER_ID");
   
      if( mgr.dataTransfered() )
      {  
         qry.addOrCondition( mgr.getTransferedData() );
         customerInfolay.setLayoutMode(customerInfolay.SINGLE_LAYOUT);
      }
      
      else if(!mgr.isEmpty(mgr.getQueryStringValue("IDENTITY")))
      { 
         strCompany = mgr.getQueryStringValue("COMPANY");
         
         if  (!(ctx.findGlobal("COMPANY","").equals(strCompany))) 
            mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"enterw/DefineCompany.page?COMPANY="+ strCompany);       
   
         trans.clear();      
         qry = trans.addEmptyQuery(customerInfoblk);
         customer_id = mgr.readValue("IDENTITY");
         qry.addWhereCondition("CUSTOMER_ID = ?");    
         qry.addParameter("CUSTOMER_ID", customer_id);
      }
      
      qry.includeMeta("ALL");
      mgr.querySubmit(trans,customerInfoblk);

      if  (customerInfoset.countRows() == 0) 
      {
         mgr.showAlert("ENTERWCUSTOMERINFONODATA: No data found.");
         customerInfoset.clear();
      }
      
      if( mgr.dataTransfered())
      {
         okFindMSGSETUP();
         okFindOURID();
      }
      
      else if(!mgr.isEmpty(mgr.getQueryStringValue("IDENTITY")))
         okFindMSGSETUP();
      okFindOURID();   
   }


   public void  okFindMSGSETUP()
   {
      ASPManager mgr = getASPManager();
      int intCurrrow = 0;

      trans.clear();
      msgSetupset.clear();
   
      if (customerInfoset.countRows() > 0)
      {
         customer_id = customerInfoset.getValue("CUSTOMER_ID");      
         qry = trans.addQuery(msgSetupblk);
         qry.addWhereCondition("CUSTOMER_ID = ?");    
         qry.addParameter("CUSTOMER_ID", customer_id);
         qry.includeMeta("ALL");
         intCurrrow = customerInfoset.getCurrentRowNo();
         mgr.submit(trans);
         customerInfoset.goTo(intCurrrow);
      }
   }


   public void  okFindOURID()
   {
      ASPManager mgr = getASPManager();
      int intCurrrow = 0;

      eval(customerInfoblk.generateAssignments());
   
      if (customerInfoset.countRows() > 0)
      {
         customer_id = customerInfoset.getValue("CUSTOMER_ID");   
         trans.clear();
         qry = trans.addQuery(ourIdblk);
         qry.addWhereCondition("CUSTOMER_ID = ?");    
         qry.addParameter("CUSTOMER_ID", customer_id);
         qry.includeMeta("ALL");
         intCurrrow = customerInfoset.getCurrentRowNo();
         mgr.submit(trans);
         customerInfoset.goTo(intCurrrow);
      }
   }

//=========================================
//Search function For Zoom from Maintenance
//=========================================

   public void  searchZoom()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(customerInfoblk);
      qry.addWhereCondition("CUSTOMER_ID = ?");    
      qry.addParameter("CUSTOMER_ID", customerNo);
      qry.includeMeta("ALL");
      mgr.submit(trans);
   
      if  (customerInfoset.countRows() == 0) 
      {
         mgr.showAlert("ENTERWCUSTOMERINFONODATA: No data found.");
         customerInfoset.clear();
      }
      
      mgr.createSearchURL(customerInfoblk);
   }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(customerInfoblk);   
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      customerInfolay.setCountValue(toInt(customerInfoset.getValue("N")));
      customerInfoset.clear();
   }


   public void  countFindMSGSETUP()
   {
      ASPManager mgr = getASPManager();

      customer_id = customerInfoset.getValue("CUSTOMER_ID");   
      qry = trans.addQuery(msgSetupblk);
      qry.setSelectList("to_char(count(*)) N");
      qry.addWhereCondition("CUSTOMER_ID = ?");    
      qry.addParameter("CUSTOMER_ID", customer_id);
      mgr.submit(trans);
      msgSetuplay.setCountValue(toInt(msgSetupset.getValue("N")));
      msgSetupset.clear();
   }


   public void  countFindOURID()
   {
      ASPManager mgr = getASPManager();

      customer_id = customerInfoset.getValue("CUSTOMER_ID");
      qry = trans.addQuery(ourIdblk);   
      qry.addWhereCondition("CUSTOMER_ID = ?");    
      qry.addParameter("CUSTOMER_ID", customer_id);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      ourIdlay.setCountValue(toInt(ourIdset.getValue("N")));
      ourIdset.clear();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  credit()
   {
      ASPManager mgr = getASPManager();

      if(customerInfolay.isSingleLayout())            
         curr_row = customerInfoset.getCurrentRowNo();
      else
         curr_row = customerInfoset.getRowSelected();
   
      customerInfoset.goTo(curr_row);
   
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"paylew/CustomerCreditInfo.page",buff); 
   }


   public void  invoice()
   {
      ASPManager mgr = getASPManager();

      if(customerInfolay.isSingleLayout())            
         curr_row = customerInfoset.getCurrentRowNo();
      else
         curr_row = customerInfoset.getRowSelected();
   
      customerInfoset.goTo(curr_row);
   
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"invoiw/CustomerInvoice.page",buff);    
   }


   public void  address()
   {
      ASPManager mgr = getASPManager();

      if(customerInfolay.isSingleLayout())            
         curr_row = customerInfoset.getCurrentRowNo();
      else
         curr_row = customerInfoset.getRowSelected();
   
      customerInfoset.goTo(curr_row);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"enterw/CustomerAddress.page",buff);    
   }


   public void  payment()
   {
      ASPManager mgr = getASPManager();

      curr_row = customerInfoset.getRowSelected();
      
      if (customerInfolay.isMultirowLayout())
         customerInfoset.goTo(curr_row);
   
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"paylew/CustomerPayment.page",buff);    
   }


   public void  orderaddressinfo()
   {
      ASPManager mgr = getASPManager();

      if(customerInfolay.isSingleLayout())            
         curr_row = customerInfoset.getCurrentRowNo();
      else
         curr_row = customerInfoset.getRowSelected();
   
      customerInfoset.goTo(curr_row);
   
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"orderw/CustomerOrderAddress.page",buff);   
   }


   public void  orderinfo()
   {
      ASPManager mgr = getASPManager();

      if(customerInfolay.isSingleLayout())            
         curr_row = customerInfoset.getCurrentRowNo();
      else
         curr_row = customerInfoset.getRowSelected();
   
      customerInfoset.goTo(curr_row);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"orderw/CustomerOrderCustomer.page",buff);
   }

//-----------------------------------------------------------------------------
//----------------------------  DYNAMIC RMB FUNCTION  ------------------------------
//-----------------------------------------------------------------------------

   public void  setDynamicRmb()
   {
      ASPManager mgr = getASPManager();

      if(!mgr.isPresentationObjectInstalled("paylew/CustomerPayment.page"))
         customerInfobar.removeCustomCommand("Payment"); 
   
      if(!mgr.isPresentationObjectInstalled("enterw/CustomerAddress.page"))
         customerInfobar.removeCustomCommand("Address"); 
   
      if(!mgr.isPresentationObjectInstalled("invoiw/CustomerInvoice.page"))
         customerInfobar.removeCustomCommand("Invoice"); 
   
      if(!mgr.isPresentationObjectInstalled("paylew/CustomerCreditInfo.page")) 
         customerInfobar.removeCustomCommand("credit");     
   
      if(!mgr.isPresentationObjectInstalled("orderw/CustomerOrderCustomer.page")) 
         customerInfobar.removeCustomCommand("orderinfo");      
   
      if(!mgr.isPresentationObjectInstalled("orderw/CustomerOrderAddress.page")) 
         customerInfobar.removeCustomCommand("orderaddressinfo");       
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ASPField f;

      customerInfoblk = mgr.newASPBlock("CUSTOMER");
   
      customerInfoblk.addField("OBJID").
         setHidden();
   
      customerInfoblk.addField( "OBJVERSION" ).
         setHidden();
      
      customerInfoblk.addField("CUSTOMER_ID").
         setUpperCase().
         setMandatory().
         setLabel("ENTERWCUSTOMERINFOCUSTOMERID: Customer Id.").
         setReadOnly().
         setInsertable().
         setSize(35).
         setMaxLength(20);
   
      customerInfoblk.addField("PARTY").
         setHidden();
   
      customerInfoblk.addField("PARTY_TYPE").
         setMandatory().
         setHidden();
   
      customerInfoblk.addField("PARTY_TYPE_DB").
         setHidden();
   
      customerInfoblk.addField("NAME").
         setMandatory().
         setLabel("ENTERWCUSTOMERINFONAME: Name").
         setSize(35);
   
      customerInfoblk.addField("ASSOCIATION_NO").
         setLabel("ENTERWCUSTOMERINFOASSOCIATIONNO: Association No.").
         setSize(35);

      customerInfoblk.addField("ASS_BEFORE_EDIT").
         setFunction("''").
         setHidden();
   
      customerInfoblk.addField("DEFAULT_LANGUAGE").
         setLabel("ENTERWCUSTOMERINFODEFAULTLANGUAGE: Default Language").
         setSelectBox().
         enumerateValues("ISO_LANGUAGE_API").
         setSize(35).
         setMandatory().
         unsetSearchOnDbColumn(); 
   
      customerInfoblk.addField("COUNTRY").
         setLabel("ENTERWCUSTOMERINFOCOUNTRY: Country").
         setSelectBox().
         enumerateValues("ISO_COUNTRY_API").
         setSize(35).
         unsetSearchOnDbColumn().
         setCustomValidation("COUNTRY","COUNTRY_CODE");
   
      customerInfoblk.addField("CREATION_DATE", "Date").
         setLabel("ENTERWCUSTOMERINFOCREATIONDATE: Creation Date").
         setSize(35).
         setReadOnly();
   
      customerInfoblk.addField("DEFAULT_DOMAIN").
         setSize(14).
         setMandatory().
         setHidden().
         setLabel("ENTERWCUSTOMERINFODEFAULTDOMAIN: Default Domain").
         setCheckBox("FALSE,TRUE");
   
      customerInfoblk.addField("CLIENTVALUE").
         setFunction("''").
         setHidden();

      customerInfoblk.addField("DUMMY").
         setFunction("''").
         setHidden();

      customerInfoblk.addField("COUNTRY_CODE").
         setHidden(). 
         setFunction("ISO_COUNTRY_API.ENCODE(:COUNTRY)");

      customerInfoblk.addField("CORPORATE_FORM").
         setSize(25).
         setLabel("ENTERWCUSTOMERINFOCORFORM: Form of Business").
         setDynamicLOV("CORPORATE_FORM","COUNTRY_CODE",600,445).    
         setInsertable().
         setCustomValidation("COUNTRY_CODE,CORPORATE_FORM","CORPORATE_FORM_DESC");

      customerInfoblk.addField("CORPORATE_FORM_DESC").
         setSize(25).
         setLabel("ENTERWCUSTOMERINFOCORFORMDES: Form of Business Description").
         setFunction("CORPORATE_FORM_API.Get_Corporate_Form_Desc(ISO_COUNTRY_API.Encode(:COUNTRY), :CORPORATE_FORM)");

      customerInfoblk.addField("DUMMY_NUM","Number").
         setFunction("''").
         setHidden();
      
      customerInfoblk.addField("IDENTIFIER_REFERENCE").
         setLabel("ENTERWCUSTOMERINFOIDENTIFIERREF: Identifier Reference").
         setSize(30);
    
      customerInfoblk.addField("IDENTIFIER_REF_VALIDATION").
         setLabel("ENTERWCUSTOMERINFOIDENTIFIERREFVAL: Identifier Ref Validation").
         setSelectBox().
         setSize(30).
         enumerateValues("IDENTIFIER_REF_VALIDATION_API");

      customerInfoblk.setView("CUSTOMER_INFO");
      customerInfoblk.defineCommand("CUSTOMER_INFO_API","New__,Modify__,Remove__");
      customerInfoset = customerInfoblk.getASPRowSet();
      customerInfobar = mgr.newASPCommandBar(customerInfoblk);
      customerInfotbl = mgr.newASPTable(customerInfoblk);
   
      customerInfotbl.setTitle("ENTERWCUSTOMERINFOCUSINFO: Customer Information");
      customerInfolay = customerInfoblk.getASPBlockLayout();
      customerInfolay.setDefaultLayoutMode(customerInfolay.SINGLE_LAYOUT);
      
   
      customerInfobar.addCustomCommand("Address", mgr.translate("ENTERWCUSTOMERINFOADDINFO: Address Info..."));   
      customerInfobar.addCustomCommand("Invoice", mgr.translate("ENTERWCUSTOMERINFOINOINFO: Invoice Info..."));   
      customerInfobar.addCustomCommand("Payment", mgr.translate("ENTERWCUSTOMERINFOPAYINFO: Payment Info..."));
      customerInfobar.addCustomCommand("credit", mgr.translate("ENTERWCUSTOMERINFOCRENFO: Credit Info..."));
      customerInfobar.addCustomCommand("orderinfo", mgr.translate("ENTERWCUSTOMERINFOORDINFO: Order Info..."));
      customerInfobar.addCustomCommand("orderaddressinfo", mgr.translate("ENTERWCUSTOMERINFOORDADDINFO: Order Address Info...")); 
      customerInfobar.addCustomCommand("show_dialog", mgr.translate("ENTERWCUSTOMERINFOCPYCUS: Copy Customer..."));     
      customerInfobar.disableCommand(customerInfobar.DUPLICATEROW);
      customerInfobar.defineCommand(customerInfobar.SAVERETURN, "saveReturnCUSTOMERINFO", "checkRemove");
      customerInfobar.defineCommand(customerInfobar.SAVENEW,"saveNewCUSTOMERINFO", "checkRemove");
         
// ------------------------- MESSAGE SETUP BLOCK ---------------------------------------------------------------//
      
      msgSetupblk = mgr.newASPBlock("MSGSETUP");
   
      msgSetupblk.addField("MSGSETUP_OBJID").
         setHidden().
         setDbName("OBJID");
   
      msgSetupblk.addField("MSGSETUP_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");
   
      msgSetupblk.addField("MESSAGE_CLASS").
         setSize(20).
         setDynamicLOV("MESSAGE_CLASS",600,445).
         setMandatory().
         setLabel("ENTERWCUSTOMERINFOMESSAGECLASS: Message Class").
         setUpperCase().
         setReadOnly().
         setInsertable();
   
      msgSetupblk.addField("MEDIA_CODE").
         setSize(20).
         setDynamicLOV("MESSAGE_MEDIA",600,445).
         setMandatory().
         setLabel("ENTERWCUSTOMERINFOMEDIACODE: Media Code").
         setUpperCase().
         setReadOnly().
         setInsertable();
   
      msgSetupblk.addField("MSGSETUP_ADDRESS").
         setSize(20).
         setDynamicLOV("MESSAGE_RECEIVER",600,445).
         setMandatory().
         setLabel("ENTERWCUSTOMERINFOMSGSETUPADDRESS: Address").
         setDbName("ADDRESS").
         setUpperCase().
         setReadOnly().
         setInsertable();
   
      msgSetupblk.addField("MSGSETUP_METHOD_DEFAULT").
         setSize(14).
         setMandatory().
         setLabel("ENTERWCUSTOMERINFOMSGSETUPMETHODDEFAULT: Method Default").
         setDbName("METHOD_DEFAULT").
         setCheckBox("FALSE,TRUE");
   
      msgSetupblk.addField("MSGSETUP_CUSTOMER_ID").
         setMandatory().
         setHidden().
         setDbName("CUSTOMER_ID").
         setUpperCase();
   
      msgSetupblk.setView("CUSTOMER_INFO_MSG_SETUP");
      msgSetupblk.defineCommand("CUSTOMER_INFO_MSG_SETUP_API","New__,Modify__,Remove__");
      msgSetupblk.setMasterBlock(customerInfoblk);
      msgSetupset = msgSetupblk.getASPRowSet();
   
      msgSetuplay = msgSetupblk.getASPBlockLayout();
      msgSetuplay.setDefaultLayoutMode(msgSetuplay.MULTIROW_LAYOUT);
   
      msgSetupblk.setTitle(mgr.translate("ENTERWCUSTOMERINFOMSGSETUP: Message Setup"));
   
      msgSetupbar = mgr.newASPCommandBar(msgSetupblk);
   
      msgSetupbar.defineCommand(msgSetupbar.OKFIND,"okFindMSGSETUP");
      msgSetupbar.defineCommand(msgSetupbar.COUNTFIND,"countFindMSGSETUP");
      msgSetupbar.defineCommand(msgSetupbar.NEWROW,"newRowMSGSETUP");
   
      msgSetuptbl = mgr.newASPTable(msgSetupblk);
      msgSetuptbl.setTitle(mgr.translate("ENTERWCUSTOMERINFOMSGSETUP: Message Setup"));
      
   //----------------------------MSG SETUP BLOCK END----------------------- 
      
      ourIdblk = mgr.newASPBlock("OURID");
   
      ourIdblk.addField("OURID_OBJID").
         setHidden().
         setDbName("OBJID");
   
      ourIdblk.addField("OURID_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");
   
      ourIdblk.addField("COMPANY").
         setSize(20).
         setDynamicLOV("COMPANY",650,450).
         setMandatory().
         setLabel("ENTERWCUSTOMERINFOCOMPANY: Company").   
         setUpperCase();
   
      ourIdblk.addField("OUR_ID").
         setSize(20).
         setMandatory().
         setLabel("ENTERWCUSTOMERINFOOURID: Our Id.").   
         setUpperCase();
   
      ourIdblk.addField("OURID_CUSTOMER_ID").
         setSize(11).
         setDynamicLOV("CUSTOMER_INFO",650,450).
         setMandatory().
         setHidden().
         setLabel("ENTERWCUSTOMERINFOOURIDCUSTOMSID: Customs Id").
         setDbName("CUSTOMER_ID").
         setUpperCase();
   
      ourIdblk.setView("CUSTOMER_INFO_OUR_ID");
      ourIdblk.defineCommand("CUSTOMER_INFO_OUR_ID_API","New__,Modify__,Remove__");
      ourIdblk.setMasterBlock(customerInfoblk);
   
      ourIdset = ourIdblk.getASPRowSet();
   
      ourIdbar = mgr.newASPCommandBar(ourIdblk);
      ourIdbar.defineCommand(ourIdbar.OKFIND,"okFindOURID");
      ourIdbar.defineCommand(ourIdbar.COUNTFIND,"countFindOURID");
      ourIdbar.defineCommand(ourIdbar.NEWROW,"newRowOURID");
      ourIdbar.defineCommand(ourIdbar.SAVERETURN,null,"checkOurId()");
      
      ourIdblk.setTitle(mgr.translate("ENTERWCUSTOMERINFOOID: Our ID at Customer"));
      ourIdtbl = mgr.newASPTable(ourIdblk);  
      ourIdtbl.setTitle(mgr.translate("ENTERWCUSTOMERINFOOID: Our ID at Customer"));
      ourIdlay = ourIdblk.getASPBlockLayout();
      ourIdlay.setDialogColumns(1);
      ourIdlay.setDefaultLayoutMode(ourIdlay.MULTIROW_LAYOUT);
   
      diablk1 = mgr.newASPBlock("DIA1");
   
      fCUS_ID = diablk1.addField("CUS_ID").
         setSize(20).
         setDynamicLOV("CUSTOMER_INFO",650,450).
         setLabel("ENTERWCUSTOMERINFOCUSID: Customer Id.").
         setCustomValidation("CUS_ID","CUS_NAME").
         setUpperCase().
         setFunction("''");
   
      diablk1.addField("CUS_NAME").
         setSize(20).
         setLabel("ENTERWCUSTOMERINFOCUSNAME: Customer Name").
         setReadOnly().
         setFunction("''");
   
      diablk1.addField("NEW_ID").
         setSize(20).
         setLabel("ENTERWCUSTOMERINFONEWID: New Customer Id.").
         setUpperCase().
         setFunction("''");
   
      diablk1.addField("NEW_NAME").
         setSize(20).
         setLabel("ENTERWCUSTOMERINFONEWNAME: New Customer Name").
         setFunction("''");
   
      fASSO_NO = diablk1.addField("ASSO_NO").
         setSize(20).
         setLabel("ENTERWCUSTOMERINFOASSONO: Associaton No.").
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
      ASPManager mgr = getASPManager();
      setDynamicRmb();

      if (customerInfoset.countRows() != 0) 
      {
         if( customerInfolay.isEditLayout()  ) 
            customerInfoset.setValue("ASS_BEFORE_EDIT",customerInfoset.getValue("ASSOCIATION_NO"));
         else 
            customerInfoset.setValue("ASS_BEFORE_EDIT","");  
      }

      if (customerInfoset.countRows() == 0) 
      {
         customerInfobar.disableCommand(customerInfobar.FORWARD);
         customerInfobar.disableCommand(customerInfobar.BACKWARD);   
         customerInfobar.disableCommand(customerInfobar.DELETE);
         customerInfobar.disableCommand(customerInfobar.EDITROW);
         customerInfobar.disableCommand(customerInfobar.BACK);
         customerInfobar.removeCustomCommand("Address");
         customerInfobar.removeCustomCommand("Invoice");
         customerInfobar.removeCustomCommand("Payment");
         customerInfobar.removeCustomCommand("credit");
         customerInfobar.removeCustomCommand("orderinfo");
         customerInfobar.removeCustomCommand("orderaddressinfo");
         customerInfobar.removeCustomCommand("show_dialog");
      }
      
      if  (ourIdset.countRows() == 0) 
      {
         ourIdbar.disableCommand(ourIdbar.DUPLICATEROW);
         ourIdbar.disableCommand(ourIdbar.DELETE);
         ourIdbar.disableCommand(ourIdbar.EDITROW);
      }
   
      if  ((msgSetupset.countRows() == 0 ) && !( msgSetuplay.isFindLayout())) 
      {
         msgSetupbar.disableCommand(msgSetupbar.DUPLICATEROW);
         msgSetupbar.disableCommand(msgSetupbar.DELETE);
         msgSetupbar.disableCommand(msgSetupbar.EDITROW);
         msgSetupbar.disableCommand(msgSetupbar.FORWARD);
         msgSetupbar.disableCommand(msgSetupbar.BACKWARD);     
      }
      
      if(dia_ok)   
         eval(diablk1.generateAssignments());   
         
         
      mgr.getASPField("ASSOCIATION_NO").setDynamicLOV("ASSOCIATION_INFO",600,445);
      mgr.getASPField("ASSOCIATION_NO").setLOVProperty("ORDER_BY","PARTY_TYPE");
            
      mgr.getASPField("ASSO_NO").setDynamicLOV("ASSOCIATION_INFO",600,445);
      mgr.getASPField("ASSO_NO").setLOVProperty("ORDER_BY","PARTY_TYPE");

      msg = mgr.translate("ENTERWCUSTOMERINFOASSONOMSG: Another business partner with the same association number is already registered. Do you want to use the same Association No.");
   }
   
   public void  ok_button()
   {
      ASPManager mgr = getASPManager();
      String new_id, comp, new_name, asso_no; 
      double nCusId;

      dia_ok = false;
      dialay1.setLayoutMode(dialay1.MULTIROW_LAYOUT);
      
      cus_id = mgr.readValue("CUS_ID");
      new_id = mgr.readValue("NEW_ID");
      comp = ctx.findGlobal("COMPANY");
      if(mgr.isEmpty(comp))
         cmd = trans.addCustomFunction("GLOBAL","User_Profile_SYS.Get_Default('COMPANY',Fnd_Session_API.Get_Fnd_User)", "COMPANY");         
   
      new_name = mgr.readValue("NEW_NAME");
      asso_no = mgr.readValue("ASSO_NO");   

      cmd = trans.addCustomCommand("COPYCUST","Customer_Info_API.Copy_Existing_Customer");
      cmd.addParameter("CUS_ID",cus_id);
      cmd.addParameter("NEW_ID",new_id);
      
      if(mgr.isEmpty(comp))
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
         cmd.addParameter("DUMMY","CUSTOMER");

         cmd = trans.addCustomFunction("GETNEWCUSID","Party_Identity_Series_Api.Get_Next_Value","DUMMY_NUM");
         cmd.addReference("CLIENTVALUE","GETPARTYTYPE/DATA");
      
         trans = mgr.perform(trans);
   
         nCusId = trans.getNumberValue("GETNEWCUSID/DATA/DUMMY_NUM");
         trans.clear();

         if (isNaN(nCusId))
            new_id = "";
         else
            new_id = toInt(nCusId-1)+"";
      }

      mgr.createSearchURL(customerInfoblk);
      qry = trans.addQuery(customerInfoblk);
      qry.addWhereCondition("CUSTOMER_ID = ?");    
      qry.addParameter("CUSTOMER_ID", new_id.toUpperCase());
      qry.includeMeta("ALL");
      mgr.submit(trans); 

      okFindMSGSETUP();    
   }


   public void  cancel_button()
   {
      dia_ok = false;
      dialay1.setLayoutMode(dialay1.MULTIROW_LAYOUT);
   }


   public void  show_dialog()
   {
      dia_ok = true;
      dialay1.setLayoutMode(dialay1.FIND_LAYOUT);
      customerInfoset.store();
      cus_id = customerInfoset.getValue("CUSTOMER_ID");
      cus_name = customerInfoset.getValue("NAME");
      dummy();
   }

   public void dummy()
   {
      ASPManager mgr = getASPManager();
      diaset1.addRow(mgr.newASPBuffer());  
      eval(diablk1.generateAssignments());   
      cus_id_tag = fCUS_ID.getTag();
      asso_no_tag = fASSO_NO.getTag();
      diaset1.clear(); 
   }


//===============================================================
//  HTML
//===============================================================
   
   protected String getDescription()
   {
      return "ENTERWCUSTOMERINFOTITLE: Customer - General Info.";
   }

   protected String getTitle()
   {
      return "ENTERWCUSTOMERINFOTITLE: Customer - General Info.";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("ENTERWCUSTOMERINFOTITLE: Customer - General Info."));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("  <input type=\"hidden\" name=\"__DIA1_ROWSTATUS\" value=\"QueryMode__\">");
      out.append("<input type=\"hidden\" name=\"COPYOK\" value=\"\">");
      out.append(mgr.startPresentation("ENTERWCUSTOMERINFOTITLE: Customer - General Info."));
      
      if(customerInfolay.isVisible() && (!dia_ok))
         out.append(customerInfolay.show());
      
      if(msgSetuplay.isVisible() && (customerInfoset.countRows() > 0))
         out.append(msgSetuplay.show());
     
      if(ourIdlay.isVisible() && (customerInfoset.countRows() > 0))
         out.append(ourIdlay.show());
      
      if(dia_ok)
      {
         out.append("<!--");
         out.append(dialay1.show());
         out.append("-->\n");
         out.append("<table border=\"0\" cellspacing=\"4\">\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWCUSTOMERINFOCUSTOMID: Customer Id"));
         out.append("      </td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("CUS_ID",cus_id,cus_id_tag+" readOnly OnChange='validateCusId(-1)'",20));
         out.append("      <td>");
         out.append("      <td width=\"30\"></td>\n");
         out.append("    </tr>\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWCUSTOMERINFOCUSTOMNAM: Customer Name"));
         out.append("      </td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("CUS_NAME",cus_name," readOnly OnChange='validateCusName(-1)'",20));
         out.append("      <td>");
         out.append("      <td width=\"30\"></td>\n");
         out.append("    </tr>\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWCUSTOMERINFONEWCUSTOMID: New Customer Id"));
         out.append("      </td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("NEW_ID","","OnChange='validateNewId(-1)'",20));
         out.append("      <td>");
         out.append("      <td width=\"30\"></td>\n");
         out.append("    </tr>\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWCUSTOMERINFONEWCUSTOMNAM: New Customer Name"));
         out.append("      </td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("NEW_NAME","","OnChange='validateNewName(-1)'",20));
         out.append("      <td>");
         out.append("      <td width=\"30\"></td>\n");
         out.append("    </tr>\n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("ENTERWCUSTOMERINFOASSONUM: Associaton No"));
         out.append("      </td>\n");
         out.append("      <td width=\"10\"></td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("ASSO_NO","",asso_no_tag+"OnChange='validateAssoNo(-1)'",20));
         out.append("      <td>");
         out.append("      <td width=\"30\"></td>\n");
         out.append("    </tr>\n");
         out.append("  </table>\n");            
         out.append(fmt.drawButton("OK",mgr.translate("ENTERWCUSTOMERINFOOK:    Ok   "),"onClick=\"checkAssNo()\""));
         out.append(" &nbsp;");   
         out.append(fmt.drawSubmit("CANCEL",mgr.translate("ENTERWCUSTOMERINFOCANCEL:    Cancel   "),""));
         out.append("</font>");
      }
      
      appendDirtyJavaScript("function validateCusId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getValue_('CUS_ID',i)=='' )\n");
      appendDirtyJavaScript("   {   \n");
      appendDirtyJavaScript("        getField_('CUS_NAME',i).value = '';\n");
      appendDirtyJavaScript("        return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   r = __connect(\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CUS_ID'\n");
      appendDirtyJavaScript("        + '&CUS_ID=' + getValue_('CUS_ID',i)\n");
      appendDirtyJavaScript("        );\n");
      appendDirtyJavaScript("   if( checkStatus_(r,'CUS_ID',i,'Customer Id.') )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("        assignValue_('CUS_NAME',-1,0);    \n");
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
      appendDirtyJavaScript("        + '&ASSOCIATION_NO=' + getValue_('ASSOCIATION_NO',1)+'&ASS_BEFORE_EDIT='+getValue_('ASS_BEFORE_EDIT',1)\n");
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

      appendDirtyJavaScript("function validateNewId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("       setDirty();\n");
      appendDirtyJavaScript("       if( !checkNewId(i) ) return;\n");
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
