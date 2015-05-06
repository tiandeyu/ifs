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
 *  File                    : CompanyInvoice.java
 *  Description             :
 *  Notes                   :
 * -------------------------- Wings Merge Start --------------------------------
 *  Created    : 2006-11-15   Haunlk   Created.
 *  Modify     : 2007-01-17   Haunlk   Modified Some of the Transation Constants.
 *               2007-01-22   Haunlk   B129741 Added the disableDocMan to disable the RMB option "Document".
 *               2007-01-25   Haunlk   Modified the duplicating Translation Constants.
 *               2007-01-31   Haunlk   Merged Wings Code.
 * -------------------------- Wings Merge Start ---------------------------------
 *               2007-04-04   Cpeilk   Added fields DEF_CO_PREPAY_DEB_INV_TYPE,DEF_CO_PREPAY_CRE_INV_TYPE,DEF_CO_COR_INV_TYPE,DEF_COL_COR_INV_TYPE,DEF_CORR_INST_INV_TYPE.
 * ----------------------------------------------------------------------------
 */
package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CompanyInvoice extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CompanyInvoice");


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

   private ASPBlock        invoiceblk;
   private ASPRowSet       invoiceset;
   private ASPCommandBar   invoicebar;
   private ASPTable        invoicetbl;
   private ASPBlockLayout  invoicelay;


   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery qry;
   private int i;
   private int arraySize;

   private String val;
   private ASPCommand cmd;
   private String name;
   private ASPBuffer buf;
   private ASPQuery q;
   private ASPBuffer data;
   private String isOkFind = "FALSE";

   //===============================================================
   // Construction
   //===============================================================
   public CompanyInvoice(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {

      ASPManager mgr = getASPManager();
      ctx   = mgr.getASPContext();
      isOkFind     = ctx.readValue("OKFIND","FALSE");
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());

      adjust();
      ctx.writeValue("OKFIND",isOkFind);
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
      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("ENTERWCOMPANYINVOICEINFONODATA: No data found."));
         headset.clear();
      }
      mgr.createSearchURL(headblk);
      eval(headset.syncItemSets());
      isOkFind = "TRUE" ;
   }

   public void  okFindINVOICE()
   {
      ASPManager mgr = getASPManager();
      int headrowno;

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(invoiceblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY", headset.getValue("COMPANY"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,invoiceblk);
      headset.goTo(headrowno);

   }

   public void  newRowINVOICE()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      cmd = trans.addEmptyCommand("INVOICENEW","COMPANY_INVOICE_INFO_API.New__",invoiceblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("COMPANY", headset.getValue("COMPANY"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("INVOICENEW/DATA");
      invoiceset.addRow(data);

   }

   public void  saveReturnINVOICE()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      int headrowno;
      int itemrowno;
      int itemrowno1;

      String sPreviousIncomeTypeId = invoiceset.getValue("INCOME_TYPE_ID");
      String sPreviousCompany =  invoiceset.getValue("COMPANY");
      String tempAuthoLevel = invoiceset.getValue("AUTHORIZATION_LEVEL");
      String tempTolAcc = invoiceset.getValue("TOLERANCE_ACC_TO_POST_CTRL");

      invoiceset.changeRow();
      invoiceset.setValue("AUTHORIZATION_LEVEL",tempAuthoLevel);
      invoiceset.setValue("TOLERANCE_ACC_TO_POST_CTRL",tempTolAcc);

      if (mgr.isEmpty(sPreviousIncomeTypeId))
      {
         sPreviousIncomeTypeId = " ";

      }
      if (mgr.isEmpty(sPreviousCompany))
      {
         sPreviousCompany = " ";

      }
      if ((!sPreviousIncomeTypeId.equals(invoiceset.getValue("INCOME_TYPE_ID"))) ||
          (!sPreviousCompany.equals(invoiceset.getValue("COMPANY"))))
      {
         if (mgr.isEmpty(invoiceset.getValue("INCOME_TYPE_ID")))
         {
            invoiceset.setValue("INCOME_TYPE_ID_DES", "");
            invoiceset.setValue("INTERNAL_INCOME_TYPE", "");
         }
         else
         {
            cmd = trans.addCustomFunction("GETINCOMETYPE", "Income_Type_API.Get_Internal_Income_Type", "INTERNAL_INCOME_TYPE" );
            cmd.addParameter("INCOME_TYPE_ID",invoiceset.getValue("INCOME_TYPE_ID"));
            cmd.addParameter("CURRENCY_CODE",invoiceset.getValue("CURRENCY_CODE"));
            cmd.addParameter("COUNTRY_CODE",invoiceset.getValue("COUNTRY_CODE"));
            trans = mgr.perform(trans);
            String tempIncometype  = trans.getValue("GETINCOMETYPE/DATA/INTERNAL_INCOME_TYPE");
            if (mgr.isEmpty(tempIncometype))
            {
               mgr.showError("ENTERWCOMPANYINVOICENOINCOMETYPE: Income Type does not exist");
            }
            trans.clear();
            cmd = trans.addCustomFunction("GETDESC", "Income_Type_API.Get_Description", "INCOME_TYPE_ID_DES" );
            cmd.addReference("INTERNAL_INCOME_TYPE",tempIncometype);
            trans = mgr.perform(trans);
            String tempTypeDes = trans.getValue("GETDESC/DATA/INCOME_TYPE_ID_DES");
            trans.clear();
            invoiceset.setValue("INCOME_TYPE_ID_DES",tempTypeDes);
            invoiceset.setValue("INTERNAL_INCOME_TYPE", tempTypeDes);
         }

      }
      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);

      invoicelay.setLayoutMode(invoicelay.SINGLE_LAYOUT);

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
      if (invoiceset.countRows() > 0)
      {
         invoicebar.disableCommand(invoicebar.NEWROW);

         mgr.getASPField("LIABILITY_TYPE").setLOVProperty("WHERE","(COUNTRY_CODE = '"+invoiceset.getValue("COUNTRY_CODE")+"') OR COUNTRY_CODE = '*'");
         mgr.getASPField("TAX_ID_TYPE").setLOVProperty("WHERE","(COUNTRY_CODE = '"+invoiceset.getValue("COUNTRY_CODE")+"') OR COUNTRY_CODE = '*'");

      }
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
      setLabel("ENTERWCOMPANYINVOICECOMPANY: Identity").
      setReadOnly();

      headblk.addField("PARTY_TYPE_DB").
      setHidden();

      headblk.addField("NAME").
      setLabel("ENTERWCOMPANYINVOICECOMPANYNAME: Name").
      setSize(40);

      headblk.addField("ASSOCIATION_NO").
      setLabel("ENTERWCOMPANYINVOICEASSOCITTIONNO: Association No").
      setSize(20).
      setDynamicLOV("ASSOCIATION_INFO","PARTY_TYPE_DB");

      headblk.addField("CREATED_BY").
      setLabel("ENTERWCOMPANYINVOICECREATEDBY: Created By").
      setReadOnly().
      setSize(10);

      headblk.addField("METHODS").
      setHidden().
      setFunction("''");

      headblk.addField("PROPOSAL_EXIST").
      setHidden().
      setFunction("Posting_Proposal_Head_API.Check_Proposal_Exist(:COMPANY)");

      headblk.addField("CREATION_FINISHED").
      setHidden().
      setFunction("Company_Finance_API.Get_Creation_Finished(:COMPANY)");

      headblk.addField("DEFAULT_LANGUAGE").
      setSize(20).
      setMandatory().
      setLabel("ENTERWCOMPANYINVOICEDEFAULTLANGUAGE: Default Language").
      setSelectBox().
      enumerateValues("ISO_LANGUAGE_API");

      headblk.addField("HEAD_COUNTRY").
      setSize(20).setDbName("COUNTRY").
      setMandatory().
      setLabel("ENTERWCOMPANYINVOICECOUNTRY: Country").
      setSelectBox().
      enumerateValues("ISO_COUNTRY_API");

      headblk.setView("COMPANY");
      headblk.defineCommand("COMPANY_API","Modify__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(ASPCommandBar.DELETE);
      headbar.defineCommand(headbar.COUNTFIND,"countFind");
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headblk.disableDocMan();

      headtbl = mgr.newASPTable( headblk );
      headtbl.setTitle(mgr.translate("ENTERWCOMPANYINVOICETITLE: Company Invoice"));

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);

      headlay.setDialogColumns(4);

      //----------------------- General invoice block ---------------------

      invoiceblk = mgr.newASPBlock("INVOICE");

      invoiceblk.addField("INV_OBJID").
      setDbName("OBJID").
      setHidden();

      invoiceblk.addField("INV_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      invoiceblk.addField("INV_COMPANY").
      setDbName("COMPANY").
      setHidden();

      invoiceblk.addField("DEF_INSTANT_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEINSTANTINV: Instant Invoices").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_MAN_CUST_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEMANCUSTINV: Manual Customer Invoices").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_MAN_SUPP_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEMANSUPPINV: Manual Supplier Invoices").
      setDynamicLOV("INVOICE_TYPE3","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_AUTO_INVOICE_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEAUTOINVOICETYPE: Self Billing Supplier Invoices").
      setDynamicLOV("INVOICE_TYPE3","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_RECADV_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICERECADVINVTYPEE: RecAdv Self Billing Supplier Invoices").
      setDynamicLOV("INVOICE_TYPE3","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_REC_INSTANT_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEINSTANTINVTYPEE: Recurring Instant Invoices").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_REC_MAN_SUPP_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICESUPPINVTYPETYPEE: Recurring Manual Supplier Invoices").
      setDynamicLOV("INVOICE_TYPE3","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_ADV_CO_DR_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEADVCODRINVTYPE: Advance CO Debit Invoices").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_ADV_CO_CR_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEADVCOCRINVTYPE: Advance CO Credit Invoices").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_CO_PREPAY_DEB_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEPREPAYDEBINVTYPE: Prepayment Based Debit Invoice").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_CO_PREPAY_CRE_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEPREPAYCREINVTYPE: Prepayment Based Credit Invoice").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_CO_COR_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICECOCORINVTYPE: CO Correction Invoice").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_COL_COR_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICECOLCORINVTYPE: CO Collective Correction Invoice").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setUpperCase();

      invoiceblk.addField("DEF_CORR_INST_INV_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICECORR_INST_INV_TYPE: Correction Instant Invoices").
      setDynamicLOV("INVOICE_TYPE2","COMPANY").
      setLOVProperty("WHERE","CORRECTION_INVOICE = 'TRUE'").
      setUpperCase();

      //-------------Group Box: Misc Parameters

      invoiceblk.addField("BASE_PLANNED_PAY_DATE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEBASEPLANNEDPAYDATE: Base for Calculation of Planned Payment Date").
      setSelectBox().
      enumerateValues("BASE_PLANNED_PAY_DATE_API");

      invoiceblk.addField("SEP_INV_AUTH").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYINVOICESEPINVAUTH: Supplier Invoice Authorization Only by Separate Function");

      invoiceblk.addField("GROSS_CALC").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYINVOICGROSSCALC: Cash Discount Based on Gross Amount");

      invoiceblk.addField("NUMBERING_CUST_INV_DATE_ORDER").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYINVOICENUMBERINGINVDATEORDER: Set Invoice Date to Current Date when Printing Customer Invoices");

      invoiceblk.addField("PRINT_ADV_PAY_ON_CU_INV").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYINVOICEPRINTADVPAY: Print Advance Payments information on Customer Invoice");

      //------------- Group Box: Invoice Payment Reference No

      invoiceblk.addField("NCF_REFERENCE_METHOD").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICENCFREFERENCEMETHOD: Cust Inv Creation Method").
      setSelectBox().
      enumerateValues("NCF_REFERENCE_METHOD_API");
      /* Remember to delete the 7th entry appendDirtyJavaScript(" document.form.ADDGENERALTYPECODE.remove(z); \n");
         Implement this at the print contents where u show the invoicelay.
       */

      invoiceblk.addField("NCF_REFERENCE_METHOD_SUPP").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICENCFREFERENCEMETHODSUPP: Supp Inv Validation Method").
      setSelectBox().
      enumerateValues("NCF_REFERENCE_METHOD_SUPP_API");

      // -------- Group Box: Credit

      invoiceblk.addField("CREDIT_LIMIT","Number").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICECREDITLIMIT: Customer Credit Limit");

      // ------- Group Box: Posting Parameters

      invoiceblk.addField("VOUCHER_AT_INV_ENTRY").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYINVOICEVOUCHERATINVENTRY: Create Posting at Invoice Entry");

      invoiceblk.addField("SAME_VOUCHER_NO").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYINVOICESSAMEVOUCHERNO: Same No on Prel. and Final Suppl. Invoice Voucher");

      invoiceblk.addField("DISPLAY_DATE").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYINVOICESEDISPLAYDATE: Same Date on Prel. and Final Suppl. Invoice Voucher");

      invoiceblk.addField("USE_POSTING_PROPOSAL").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYINVOICESEUSEPOSTINGPROPOSAL: Use Posting Proposal").
      setValidateFunction("validateProposal");

      // ---------  Group Box -----------------------
      invoiceblk.addField("INTERNAL_INCOME_TYPE","Number").
      setHidden();

      invoiceblk.addField("CURRENCY_CODE").
      setHidden();

      invoiceblk.addField("COUNTRY_CODE").
      setHidden();

      invoiceblk.addField("TAX_REGIME").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICETAXREGIME: Tax Regime").
      setSelectBox().
      enumerateValues("TAX_REGIME_API");
      //setCustomValidation("TAX_REGIME","TAX_REGIME_TEMP");

      invoiceblk.addField("LIABILITY_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICETAXLIABILITYTYPE: Tax Liability").
      setDynamicLOV("TAX_LIABILITY_LOV");

      invoiceblk.addField("AMOUNT_METHOD").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEAMOUNTMETHOD: Amount Method").
      setSelectBox().
      enumerateValues("AMOUNT_METHOD_API");

      invoiceblk.addField("INV_FEE_VAT_CODE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEINVFEEVATCODE: Invoice Fee Tax Code").
      setDynamicLOV("STATUTORY_FEE","COMPANY");

      invoiceblk.addField("TAX_ID_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICETAXIDTYPE: Tax ID Type").
      setDynamicLOV("TAX_ID_TYPE");

      invoiceblk.addField("VAT_NO").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEVATNO: Tax ID Number").
      setUpperCase();

      invoiceblk.addField("INCOME_TYPE_ID").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEINCOMETYPEID: Income Type ID").
      setDynamicLOV("INCOME_CUST_LOV","COUNTRY_CODE,CURRENCY_CODE");

      invoiceblk.addField("INCOME_TYPE_ID_DES").
      setSize(20).
      setReadOnly().
      setFunction("Income_Type_API.Get_Description(:INTERNAL_INCOME_TYPE)");

      mgr.getASPField("INCOME_TYPE_ID").setValidation("INCOME_TYPE_ID_DES");

      invoiceblk.addField("DEFAULT_TAX_WITHHOLDING_CODE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEDEFAULTWITHHOLDINGCODE: Default Tax Withholding Code").
      setDynamicLOV("STATUTORY_FEE_TAX_WITHHOLD","COMPANY").
      setUpperCase();

      invoiceblk.addField("WITHHOLDING_CODE_DES").
      setSize(20).
      setReadOnly().
      setFunction("Statutory_Fee_API.Get_Description(:COMPANY, :DEFAULT_TAX_WITHHOLDING_CODE)");

      mgr.getASPField("DEFAULT_TAX_WITHHOLDING_CODE").setValidation("WITHHOLDING_CODE_DES");

      invoiceblk.addField("SALES_TAX_CALC_METHOD").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICESALESTAXCALCMETHOD: Sales Tax Calculation Method").
      setSelectBox().
      enumerateValues("SALES_TAX_CALC_METHOD_API");

      invoiceblk.addField("TAX_ROUNDING_LEVEL").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICETAXROUNDINGLEVEL: Tax Rounding Level").
      setSelectBox().
      enumerateValues("TAX_ROUNDING_LEVEL_API");

      invoiceblk.addField("TAX_CORRESPONDENCE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICETAXCORR: Tax Correspondence, Invoice Lines - Postings").
      setCheckBox("FALSE,TRUE").
      setValidateFunction("validateTax");

      invoiceblk.addField("TAX_AMOUNT_LIMIT").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICETAXAMO: Tax Amount Limit, Customer Invoice Lines").
      setCheckBox("FALSE,TRUE").
      setValidateFunction("validateAmount");

      invoiceblk.addField("TYPE1099_REQUIRED").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICETYPE1099: Supplement Report Codes to Tax Withholding Lines").
      setCheckBox("FALSE,TRUE").
      setValidateFunction("validateTYPE1099");

      invoiceblk.addField("USE_TAX_INVOICE").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICEUSETAX: Use Tax Invoice").
      setCheckBox("FALSE,TRUE");

      invoiceblk.addField("POST_PREL_TAX_WITHH").
      setSize(20).
      setLabel("ENTERWCOMPANYINVOICETAXWITHH: Post Preliminary Tax Withholding").
      setCheckBox("FALSE,TRUE");



      // ---------------Group Box PO Matching----------------------
      invoiceblk.addField("PERCENT_TOLERANCE","Number").
      setLabel("ENTERWCOMPANYINVOICEPERCENTTOLERANCE: Tolerance %").
      setSize(20);

      invoiceblk.addField("AMOUNT_TOLERANCE","Number").
      setLabel("ENTERWCOMPANYINVOICEAMOUNTTOLERANCE: Tolerance Amount").
      setSize(20);

      invoiceblk.addField("PO_CURRENCY_CODE").
      setLabel("ENTERWCOMPANYINVOICE: Tolerance Currency").
      setSize(20).
      setFunction("Company_Finance_API.Get_Currency_Code(:COMPANY)").
      setUpperCase().
      setReadOnly();

      invoiceblk.addField("AUTHORIZATION_LEVEL").
      setLabel("ENTERWCOMPANYINVOICEAUTHORIZATIONLEVEL: Posting Authorization").
      setSize(20).
      setSelectBox().
      enumerateValues("AUTHORIZATION_LEVEL_API");


      invoiceblk.addField("TOLERANCE_ACC_TO_POST_CTRL").
      setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYINVOICETOLERANCEACCCTRL: Create Tolerance Posting according to Posting Control").
      setSize(20);

      if (mgr.isModuleInstalled("SINWOF"))
      {
         mgr.getASPField("TOLERANCE_ACC_TO_POST_CTRL").isQueryable();
      }


      // ----------------------------------------------------------
      invoiceblk.addField("PAGE_FOOTING").
      setLabel("ENTERWCOMPANYINVOICEPAGEFOOTING: Page Footing").
      setMaxLength(2000).
      setHeight(5).
      setSize(70);

      if (mgr.isModuleInstalled("JINSUI"))
      {
         invoiceblk.addField("SALETXDECODE").
         setFunction("Tax_Regime_API.Decode('SALETX')").
         setHidden();
      }

      invoiceblk.setView("COMPANY_INVOICE_INFO");
      invoiceblk.defineCommand("COMPANY_INVOICE_INFO_API","New__,Modify__,Remove__");
      invoiceblk.setMasterBlock(headblk);

      invoiceset = invoiceblk.getASPRowSet();

      invoicebar = mgr.newASPCommandBar(invoiceblk);
      invoicebar.defineCommand(invoicebar.OKFIND,"okFindINVOICE");
      invoicebar.defineCommand(invoicebar.SAVERETURN,"saveReturnINVOICE");
      invoicebar.defineCommand(invoicebar.NEWROW,"newRowINVOICE");
      invoicebar.disableCommand(invoicebar.SAVENEW);
      invoicebar.disableCommand(invoicebar.DUPLICATEROW);


      invoicetbl = mgr.newASPTable(invoiceblk );
      invoicetbl.setTitle(mgr.translate("ENTERWCOMPANYINVOICEINFOTITLE: Company Invoice Info"));
      invoicelay = invoiceblk.getASPBlockLayout();
      invoicelay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      invoicelay.defineGroup(mgr.translate("ENTERWCOMPANYINVOICEGRP1: Default Invoice Type"),"DEF_INSTANT_INV_TYPE,DEF_MAN_CUST_INV_TYPE,DEF_MAN_SUPP_INV_TYPE,DEF_AUTO_INVOICE_TYPE,DEF_RECADV_INV_TYPE,DEF_REC_INSTANT_INV_TYPE,DEF_REC_MAN_SUPP_INV_TYPE,DEF_ADV_CO_DR_INV_TYPE,DEF_ADV_CO_CR_INV_TYPE,DEF_CO_PREPAY_DEB_INV_TYPE,DEF_CO_PREPAY_CRE_INV_TYPE,DEF_CO_COR_INV_TYPE,DEF_COL_COR_INV_TYPE,DEF_CORR_INST_INV_TYPE",true,true,2);
      invoicelay.defineGroup(mgr.translate("ENTERWCOMPANYINVOICEGRP2: Misc Parameters"),"BASE_PLANNED_PAY_DATE,SEP_INV_AUTH,GROSS_CALC,NUMBERING_CUST_INV_DATE_ORDER,PRINT_ADV_PAY_ON_CU_INV",true,false,2);
      invoicelay.defineGroup(mgr.translate("ENTERWCOMPANYINVOICEGRP3: Invoice Payment Reference No"),"NCF_REFERENCE_METHOD,NCF_REFERENCE_METHOD_SUPP",true,false,2);
      invoicelay.defineGroup(mgr.translate("ENTERWCOMPANYINVOICEGRP4: Credit"),"CREDIT_LIMIT",true,false,2);
      invoicelay.defineGroup(mgr.translate("ENTERWCOMPANYINVOICEGRP5: Posting Parameters"),"VOUCHER_AT_INV_ENTRY,SAME_VOUCHER_NO,DISPLAY_DATE,USE_POSTING_PROPOSAL",true,false,2);
      invoicelay.defineGroup(mgr.translate("ENTERWCOMPANYINVOICEGRP6: Tax Information"),"TAX_REGIME,LIABILITY_TYPE,AMOUNT_METHOD,INV_FEE_VAT_CODE,TAX_ID_TYPE,VAT_NO,INCOME_TYPE_ID,INCOME_TYPE_ID_DES,DEFAULT_TAX_WITHHOLDING_CODE,WITHHOLDING_CODE_DES,SALES_TAX_CALC_METHOD,TAX_ROUNDING_LEVEL,TAX_CORRESPONDENCE,TAX_AMOUNT_LIMIT,TYPE1099_REQUIRED,USE_TAX_INVOICE,POST_PREL_TAX_WITHH",true,false,2);
      invoicelay.defineGroup(mgr.translate("ENTERWCOMPANYINVOICEGRP7: PO Matching Parameters"),"PERCENT_TOLERANCE,AMOUNT_TOLERANCE,PO_CURRENCY_CODE,AUTHORIZATION_LEVEL,TOLERANCE_ACC_TO_POST_CTRL",true,false,2);

      invoicelay.defineGroup(mgr.translate("ENTERWCOMPANYINVOICEGRP8: Default"),"PAGE_FOOTING",false,true,2);
      invoicelay.setSimple("INCOME_TYPE_ID_DES");
      invoicelay.setSimple("WITHHOLDING_CODE_DES");
      invoicelay.setDataSpan("INCOME_TYPE_ID",2);
      invoicelay.setDataSpan("DEFAULT_TAX_WITHHOLDING_CODE",2);
      invoicelay.setDataSpan("TAX_CORRESPONDENCE",2);
      invoicelay.setDataSpan("TAX_AMOUNT_LIMIT",2);
      invoicelay.setDataSpan("TYPE1099_REQUIRED",2);
      invoicelay.setDataSpan("USE_TAX_INVOICE",2);
      invoicelay.setDataSpan("POST_PREL_TAX_WITHH",2);

   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWCOMPANYINVOICEINFODESCRIPTION: Company Invoice Info";
   }

   protected String getTitle()
   {
      return "ENTERWCOMPANYINVOICEINFOTITLEDES: Company Invoice Info";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();


      if (headlay.isVisible())
         appendToHTML(headlay.show());

      if (headset.countRows()>0)
      {
         if (headlay.isSingleLayout())
         {
            appendToHTML(invoicelay.show());


            if (invoicelay.isEditLayout() || invoicelay.isNewLayout())
            {
               appendDirtyJavaScript(" document.form.TAX_ROUNDING_LEVEL.remove(3); \n");

               if (!mgr.isModuleInstalled("JINSUI"))
               {
                  appendDirtyJavaScript(" document.form.NCF_REFERENCE_METHOD.remove(6); \n");
               }
               if ("TRUE".equals(isOkFind))
               {
                  appendDirtyJavaScript("if(f.USE_POSTING_PROPOSAL.checked == false)\n");
                  appendDirtyJavaScript("{" );
                  appendDirtyJavaScript("  f.AUTHORIZATION_LEVEL.disabled = true;\n");
                  appendDirtyJavaScript("  f.TOLERANCE_ACC_TO_POST_CTRL.disabled = true;\n");
                  appendDirtyJavaScript("}" );
               }
               if (mgr.isModuleInstalled("JINSUI"))
               {
                  appendDirtyJavaScript("var taxRegime = f.SALETXDECODE.value;\n");
               }
               appendDirtyJavaScript("function validateTax() {\n" );

               appendDirtyJavaScript(" if (document.form.TAX_CORRESPONDENCE.checked == false )" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript(" if (document.form.TAX_REGIME.value != taxRegime )" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript(" if (confirm(\"");
               appendDirtyJavaScript(mgr.translate("ENTERWCOMPANYINVOICEERROR5: Unchecking this box will turn off the validation that, when posting an invoice,the total net amount (tax base) per tax code corresponds between postings and lines on the invoice. This may result in differences between tax base amounts on transactions in Tax Ledger and net (tax base) amounts on transactions in General Ledger. Do you want to continue?'"));
               appendDirtyJavaScript("  \")) \n");
               appendDirtyJavaScript("       { \n ");
               appendDirtyJavaScript("  f.TAX_CORRESPONDENCE.checked = false;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("  f.TAX_CORRESPONDENCE.checked = true;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("  f.TAX_CORRESPONDENCE.checked = false;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("  f.TAX_CORRESPONDENCE.checked = true;\n");
               appendDirtyJavaScript("}" );

               appendDirtyJavaScript("function validateAmount() {\n" );
               appendDirtyJavaScript(" if (document.form.TAX_AMOUNT_LIMIT.checked == false )" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript(" if (confirm(\"");
               appendDirtyJavaScript(mgr.translate("ENTERWCOMPANYINVOICEERROR4: Unchecking this box will turn off the Minimum Tax Amount functionality for this company.Do you want to continue?'"));
               appendDirtyJavaScript("  \")) \n");
               appendDirtyJavaScript("       { \n ");
               appendDirtyJavaScript("  f.TAX_AMOUNT_LIMIT.checked = false;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.TAX_AMOUNT_LIMIT.checked = true;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.TAX_AMOUNT_LIMIT.checked = true;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("}" );


               appendDirtyJavaScript("function validateTYPE1099() {\n" );
               appendDirtyJavaScript(" if (document.form.TYPE1099_REQUIRED.checked == false )" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript(" if (confirm(\"");
               appendDirtyJavaScript(mgr.translate("ENTERWCOMPANYINVOICEERROR3: Report Codes must be used in order to get correct information in the reports, which are used when reporting withheld supplier income tax. This reports are available via Info Services. Unchecking this box will result in problems when running the reports. Do you want to continue?'"));
               appendDirtyJavaScript("  \")) \n");
               appendDirtyJavaScript("       { \n ");
               appendDirtyJavaScript("  f.TYPE1099_REQUIRED.checked = false;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.TYPE1099_REQUIRED.checked = true;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.TYPE1099_REQUIRED.checked = true;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("}" );


               appendDirtyJavaScript("function validateProposal () {\n" );
               appendDirtyJavaScript(" if (document.form.PROPOSAL_EXIST.value ==\"TRUE\")" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("if(f.USE_POSTING_PROPOSAL.checked == false)\n");
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("      if (confirm(\"");
               appendDirtyJavaScript(mgr.translate("ENTERWCOMPANYINVOICEERROR2: It is not allowed to uncheck Use Posting Proposal since posting proposals exist for the company"));
               appendDirtyJavaScript("  \")) \n");
               appendDirtyJavaScript("       { \n ");
               appendDirtyJavaScript("  f.USE_POSTING_PROPOSAL.checked = false;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("if(f.USE_POSTING_PROPOSAL.checked == false)\n");
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.AUTHORIZATION_LEVEL.disabled = true;\n");
               appendDirtyJavaScript("  f.TOLERANCE_ACC_TO_POST_CTRL.disabled = true;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("      if (confirm(\"");
               appendDirtyJavaScript(mgr.translate("ENTERWCOMPANYINVOICEERROR1: This will make it mandatory to use Posting Proposals in this company. It won't be possible to unselect this after creating Posting Proposals within the company. To create Posting Proposals for existing invoices that are not finally posted, please use the RMB Upgrade Invoices with Posting Proposals.Do you want to continue?"));
               appendDirtyJavaScript("  \")) \n");
               appendDirtyJavaScript("       { \n ");
               appendDirtyJavaScript("  f.AUTHORIZATION_LEVEL.disabled = false;\n");
               appendDirtyJavaScript("  f.TOLERANCE_ACC_TO_POST_CTRL.disabled = false;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.USE_POSTING_PROPOSAL.checked = false;\n");
               appendDirtyJavaScript("  f.AUTHORIZATION_LEVEL.disabled = true;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("}" );
            }
         }
      }
   }
}
