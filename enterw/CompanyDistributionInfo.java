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
 *  File                    : CompanyDistributionInfo.java
 *  Description             :
 *  Notes                   :
 * -------------------- Wings Merge Start -----------------------------------------
 *  Created    : 2006-11-08   Haunlk   Created.
 *  Modify     : 2007-01-17   Haunlk   Modified Some of the Transation Constants.
 *               2007-01-22   Haunlk   B129741 Added the disableDocMan to disable the RMB option "Document".
 *               2007-01-22   Haunlk   B129737 Modified the enumerateValues at HEAD_COUNTRY and DEFAULT_LANGUAGE, disabled the duplicate RMB option.
 *               2007-01-25   Haunlk   Modified the duplicating Translation Constants.
 *               2007-01-31   Haunlk   Merged Wings Code.
 * -------------------- Wings Merge End -------------------------------------------
 *               2007-04-04   Cpeilk   Added two new fields PREPAYMENT_INV_METHOD and POST_NON_INV_PURCH_RCPT_DB.
 * --------------------------------------------------------------------------------
 */

package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CompanyDistributionInfo extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CompanyDistributionInfo");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext      ctx;
   private ASPLog log;

   private ASPBlock        headblk;
   private ASPRowSet       headset;
   private ASPCommandBar   headbar;
   private ASPTable        headtbl;
   private ASPBlockLayout  headlay;

   private ASPBlock        disblk;
   private ASPRowSet       disset;
   private ASPCommandBar   disbar;
   private ASPTable        distbl;
   private ASPBlockLayout  dislay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPTabContainer tabs;
   private ASPQuery qry;
   private int i;
   private int arraySize;

   private String val;
   private ASPCommand cmd;
   private String name;
   private ASPBuffer buf;
   private ASPQuery q;
   private ASPBuffer data;
   private String activetab;
   private final static String TRANSFER_PARAM_NAME = "__TRANSFER";

   //===============================================================
   // Construction
   //===============================================================
   public CompanyDistributionInfo(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {

      ASPManager mgr = getASPManager();
      ctx   = mgr.getASPContext();
      activetab  = ctx.readValue("ACTIVETAB","1");

      if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();

      adjust();

      activetab  = ctx.readValue("ACTIVETAB","1");
      tabs.saveActiveTab();
      ctx.writeValue("ACTIVETAB",activetab);
   }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      qry = trans.addQuery(headblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);
      if ( headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("ENTERWCOMPANYDISTRIBUTIONINFONODATA: No data found."));
         headset.clear();
      }
      mgr.createSearchURL(headblk);
      eval(headset.syncItemSets());
   }

   public void  okFindDIS()
   {
      ASPManager mgr = getASPManager();
      int headrowno;

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(disblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY", headset.getValue("COMPANY"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,disblk);
      headset.goTo(headrowno);

   }
   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String val;
      String txt;
      ASPQuery qry;
      trans.clear();
      val = mgr.readValue("VALIDATE");
   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      if ( headset.countRows() == 0 )
      {
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DUPLICATEROW);

      }

      if ( disset.countRows() > 0 )
      {
         if ( "VAT".equals(disset.getValue("TAX_REGIME")) || "MIX".equals(disset.getValue("TAX_REGIME")) )
         {
            mgr.getASPField("TAX_CODE").activateLOV();
            mgr.getASPField("TAX_FREE_TAX_CODE").activateLOV();
            mgr.getASPField("TAX_CODE").setLOVProperty("WHERE","fee_type_db = 'VAT'");
            mgr.getASPField("TAX_FREE_TAX_CODE").setLOVProperty("WHERE","fee_type_db IN ('CALCVAT','NOTAX') OR (fee_type_db IN ('VAT') AND fee_rate = 0)");
         }
         else
         {
            mgr.getASPField("TAX_CODE").deactivateLOV();
            mgr.getASPField("TAX_FREE_TAX_CODE").deactivateLOV();
         }

         if ("PREPAYMENT_BASED_INVOICE".equals(disset.getValue("PREPAYMENT_INV_METHOD_DB")))
         {
            mgr.getASPField("BASE_FOR_ADV_INVOICE").setHidden();
         }
         else
         {
            mgr.getASPField("BASE_FOR_ADV_INVOICE").unsetHidden();
         }
      }

      // Remove tab commands from actions
      headbar.removeCustomCommand("activateDis");


   }

   public void activateDis()
   {
      tabs.setActiveTab(1);
      activetab = "1";
   }

   protected void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("COMPANY").
      setUpperCase().
      setSize(10).
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOCOMPANY: Identity").
      setReadOnly();

      headblk.addField("PARTY_TYPE_DB").
      setHidden();

      headblk.addField("NAME").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOCOMPANYNAME: Name").
      setSize(40);

      headblk.addField("ASSOCIATION_NO").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOASSOCITTIONNO: Association No").
      setSize(20).
      setDynamicLOV("ASSOCIATION_INFO","PARTY_TYPE_DB");

      headblk.addField("CREATED_BY").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOCREATEDBY: Created By").
      setReadOnly().
      setSize(10);

      headblk.addField("METHODS").
      setHidden().
      setFunction("''");

      headblk.addField("CREATION_FINISHED").
      setHidden().
      setFunction("Company_Finance_API.Get_Creation_Finished(:COMPANY)");

      headblk.addField("DEFAULT_LANGUAGE").
      setSize(20).
      setMandatory().
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFODEFLANGUAGE: Default Language").
      setSelectBox().
      enumerateValues("ISO_LANGUAGE_API");

      headblk.addField("HEAD_COUNTRY").
      setSize(20).setDbName("COUNTRY").
      setMandatory().
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOCOUNTRY: Country").
      setSelectBox().
      enumerateValues("ISO_COUNTRY_API");

      headblk.setView("COMPANY");
      headblk.disableDocMan();
      headblk.defineCommand("COMPANY_API","Modify__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(ASPCommandBar.DELETE);
      headbar.defineCommand(headbar.COUNTFIND,"countFind");
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);

      headtbl = mgr.newASPTable( headblk );
      headtbl.setTitle(mgr.translate("ENTERWCOMPANYDISTRIBUTIONINFOTITLE: Company General Data for Distribution"));

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);

      headlay.setDialogColumns(4);

      //----------------------- Disposition block ---------------------

      disblk = mgr.newASPBlock("DIS");

      disblk.addField("DIS_OBJID").
      setDbName("OBJID").
      setHidden();

      disblk.addField("DIS_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      disblk.addField("DIS_COMPANY").
      setDbName("COMPANY").
      setHidden();

      disblk.addField("PREPAYMENT_INV_METHOD").
      setSize(35).
      setMandatory().
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOPREPAYINVMETHOD: Method For Invoicing Prepayment").
      setSelectBox().
      enumerateValues("PREPAYMENT_INV_METHOD_API.Enumerate","");

      disblk.addField("PREPAYMENT_INV_METHOD_DB").
      setFunction("Prepayment_Inv_Method_API.Encode(:PREPAYMENT_INV_METHOD)").
      setHidden();

      disblk.addField("BASE_FOR_ADV_INVOICE").
      setSize(20).
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOBASEFORINVOICED: Base For Advance Invoice").
      setSelectBox().
      enumerateValues("BASE_FOR_ADV_INVOICE_API.Enumerate","");

      disblk.addField("OWNERSHIP_TRANSFER_POINT").
      setSize(20).
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOOWNERSHIPTRANSFER: Ownership Transfer Point").
      setSelectBox().
      enumerateValues("OWNERSHIP_TRANSFER_POINT_API.Enumerate"," ");

      disblk.addField("TAX_CODE").
      setSize(20).
      setDynamicLOV("STATUTORY_FEE","COMPANY").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOTAXCODE: Tax Code");

      disblk.addField("TAX_CODE_DES").
      setSize(20).
      setFunction("STATUTORY_FEE_API.Get_Description(:COMPANY,:TAX_CODE)").
      setReadOnly();

      mgr.getASPField("TAX_CODE").setValidation("TAX_CODE_DES");

      disblk.addField("TAX_FREE_TAX_CODE").
      setSize(20).
      setDynamicLOV("STATUTORY_FEE","COMPANY").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOFREETAXCODE: Tax Free Tax Code");

      disblk.addField("TAX_FREE_TAX_CODE_DES").
      setSize(20).
      setFunction("STATUTORY_FEE_API.Get_Description(:COMPANY, :TAX_FREE_TAX_CODE)").
      setReadOnly();

      mgr.getASPField("TAX_FREE_TAX_CODE").setValidation("TAX_FREE_TAX_CODE_DES");

      disblk.addField("POST_PRICE_DIFF_AT_ARRIVAL_DB").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOPOSTPRICEDIFF: Post Price Difference at Arrival");

      disblk.addField("ORDER_TAXABLE_DB").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOORDERTAXABLE: Taxable in Customer Order");

      disblk.addField("DELAY_COGS_TO_DELIV_CONF_DB").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFDELAYCOGSTO: Delay Cost of Goods Sold to Delivery Confirmation");

      disblk.addField("PURCH_TAXABLE_DB").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOPURCHTAXABLEDBE: Taxable in Purchasing");

      disblk.addField("USE_TRANSIT_BALANCE_POSTING_DB").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFUSETRANSITO: Use Transit Balancing Postings");

      disblk.addField("INTERSITE_PROFITABILITY_DB").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOPROFITA: Inter-site Profitability");

      disblk.addField("POST_NON_INV_PURCH_RCPT_DB").
      setMandatory().
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYDISTRIBUTIONINFOPOSTNONINVPURCHRCPT: Create Postings for Receipt of non-Inventory Purchase Order Lines");

      disblk.addField("TAX_REGIME").
      setFunction("Tax_Regime_API.Encode(Company_Invoice_Info_API.Get_Tax_Regime(COMPANY))").
      setHidden();

      disblk.setView("COMPANY_DISTRIBUTION_INFO");
      disblk.defineCommand("COMPANY_DISTRIBUTION_INFO_API","New__,Modify__,Remove__");
      disblk.setMasterBlock(headblk);
      disblk.disableDocMan();

      disset = disblk.getASPRowSet();

      disbar = mgr.newASPCommandBar(disblk);
      disbar.defineCommand(disbar.OKFIND,"okFindDIS");
      disbar.disableCommand(disbar.NEWROW);
      disbar.disableCommand(disbar.DELETE);
      disbar.disableCommand(disbar.DUPLICATEROW);


      distbl = mgr.newASPTable(disblk );
      distbl.setTitle(mgr.translate("ENTERWCOMPANYDISTRIBUTIONINFODISTITLE: Company General Data for Distribution"));
      dislay = disblk.getASPBlockLayout();
      dislay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      dislay.setSimple("TAX_CODE_DES");
      dislay.setSimple("TAX_FREE_TAX_CODE_DES");
      dislay.setDataSpan("TAX_FREE_TAX_CODE",2);
      dislay.setDataSpan("TAX_CODE",2);

      //------ Commands for tabs ----------------------
      headbar.addCustomCommand("activateDis",mgr.translate("ENTERWCOMPANYDISTRIBUTIONINFOTABDIS: General Data for Distribution"));
      // ----------------------------------------------------------------------
      //                         Tabs
      // ----------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("COMPANYCOMPANYDISTRIBUTIONINFODISTRTAB: General Data for Distribution"),"javascript:commandSet('HEAD.activateDis','')");

   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWCOMPANYDISTRIBUTIONINFOEDESCRIPTION: Company General Data for Distribution";
   }

   protected String getTitle()
   {
      return "ENTERWCOMPANYDISTRIBUTIONINFOTITLEDES: Company General Data for Distribution";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      int activetab = tabs.getActiveTab();
      if ( headlay.isVisible() )
         appendToHTML(headlay.show());

      if ( headset.countRows()>0 )
      {
         if ( headlay.isSingleLayout() )
         {
            if ( !(headlay.isNewLayout() || headlay.isEditLayout()) )
            {
               appendToHTML(tabs.showTabsInit());
               appendToHTML(dislay.show());
               appendToHTML(tabs.showTabsFinish());
            }
         }
      }
   }
}
