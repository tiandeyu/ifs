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
 *  File                    : CompanyAccouting.java
 *  Description             :
 *  Notes                   :
 * --------------------------------- Wings Merge  Start ------------------------------------
 *  Created    : 2006-11-16   Haunlk   Created.
 *  Modify     : 2007-01-17   Haunlk   Modified Some of the Transation Constants.
 *               2007-01-22   Haunlk   B129741 Added the disableDocMan to disable the RMB option "Document".
 *               2007-01-22   Haunlk   B129743 Made the CURRENCY_CODE and ISO_CURRENCY to upper case.
 *               2007-01-25   Haunlk   Modified the duplicating Translation Constants.
 *               2007-01-31   Haunlk   Merged Wings Code.
 * --------------------------------- Wings Merge End ------------------------------------
 * ----------------------------------------------------------------------------

 *
 * ----------------------------------------------------------------------------
 */
package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CompanyAccouting extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CompanyAccouting");


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

   private ASPBlock        accblk;
   private ASPRowSet       accset;
   private ASPCommandBar   accbar;
   private ASPTable        acctbl;
   private ASPBlockLayout  acclay;

   private ASPBlock        currencyblk;
   private ASPRowSet       currencyset;
   private ASPCommandBar   currencybar;
   private ASPTable        currencytbl;
   private ASPBlockLayout  currencylay;



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

   private ASPTabContainer tabs;
   private String activetab;




   //===============================================================
   // Construction
   //===============================================================
   public CompanyAccouting(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {

      ASPManager mgr = getASPManager();
      ctx   = mgr.getASPContext();
      activetab  = ctx.readValue("ACTIVETAB","1");
      isOkFind     = ctx.readValue("OKFIND","FALSE");

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();

      adjust();

      tabs.saveActiveTab();
      ctx.writeValue("ACTIVETAB",activetab);
      ctx.writeValue("OKFIND",isOkFind);


   }

   public void activateCurrency()
   {
      tabs.setActiveTab(1);
      activetab = "1";
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


   public void validate()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String val;
      String txt;
      String data;
      ASPQuery qry;

      val = mgr.readValue("VALIDATE");
      if ("BUY".equals(val))
      {
         cmd = trans.addCustomFunction("GETBUYDES","CURRENCY_TYPE_API.GET_DESCRIPTION","BUY_DES");
         cmd.addParameter("COMPANY", mgr.readValue("COMPANY"));
         cmd.addParameter("BUY", mgr.readValue("BUY"));
         trans = mgr.validate(trans);

         data = trans.getValue("GETBUYDES/DATA/BUY_DES");
         txt = data+"^";
         mgr.responseWrite(txt);


      }
      else if ("SELL".equals(val))
      {
         cmd = trans.addCustomFunction("GETSELLDES","CURRENCY_TYPE_API.GET_DESCRIPTION","SELL_DES");
         cmd.addParameter("COMPANY", mgr.readValue("COMPANY"));
         cmd.addParameter("SELL", mgr.readValue("SELL"));
         trans = mgr.validate(trans);

         data = trans.getValue("GETSELLDES/DATA/SELL_DES");
         txt = data+"^";
         mgr.responseWrite(txt);

      }
      else if ("TAX_BUY".equals(val))
      {
         if ("TRUE".equals(mgr.readValue("USE_TAX_RATES")))
         {
            cmd = trans.addCustomFunction("GETTAXBUYDES","CURRENCY_TYPE_API.GET_DESCRIPTION","TAX_BUY_DES");
            cmd.addParameter("COMPANY", mgr.readValue("COMPANY"));
            cmd.addParameter("TAX_BUY", mgr.readValue("TAX_BUY"));
            trans = mgr.validate(trans);

         data = trans.getValue("GETTAXBUYDES/DATA/TAX_BUY_DES");
         txt = mgr.readValue("TAX_BUY")+"^"+data+"^";
         }
         else
         {

            txt = ""+"^"+""+"^";
         }
         mgr.responseWrite(txt);


      }
      else if ("TAX_SELL".equals(val))
      {
         if ("TRUE".equals(mgr.readValue("USE_TAX_RATES")))
         {
            cmd = trans.addCustomFunction("GETTAXSELLDES","CURRENCY_TYPE_API.GET_DESCRIPTION","TAX_SELL_DES");
            cmd.addParameter("COMPANY", mgr.readValue("COMPANY"));
            cmd.addParameter("TAX_SELL", mgr.readValue("TAX_SELL"));
            trans = mgr.validate(trans);

            data = trans.getValue("GETTAXSELLDES/DATA/TAX_SELL_DES");
            txt = mgr.readValue("TAX_SELL")+"^"+data+"^";
         }
         else
         {
            txt = ""+"^"+""+"^";
         }
         mgr.responseWrite(txt);

      }
      mgr.endResponse();

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
         mgr.showAlert(mgr.translate("ENTERWCOMPANYACCOUTINGINFONODATA: No data found."));
         headset.clear();
      }
      mgr.createSearchURL(headblk);
      eval(headset.syncItemSets());
      isOkFind = "TRUE" ;

   }

   public void  okFindACC()
   {
      ASPManager mgr = getASPManager();
      int headrowno;

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(accblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY", headset.getValue("COMPANY"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,accblk);
      headset.goTo(headrowno);

   }

   public void  okFindCURRENCY()
   {
      ASPManager mgr = getASPManager();
      int headrowno;

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(currencyblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY", headset.getValue("COMPANY"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,currencyblk);
      headset.goTo(headrowno);

   }
    public void  saveReturnCURRENCY()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      int headrowno;
      int itemrowno;
      int itemrowno1;
      String isValid;

      currencyset.changeRow();
      cmd = trans.addCustomFunction("GETREFCURRCODEBUY", "Currency_Type_API.Get_Ref_Currency_Code", "REF_CURR_CODE" );
      cmd.addParameter("COMPANY",headset.getValue("COMPANY"));
      cmd.addParameter("BUY",currencyset.getValue("BUY"));

      cmd = trans.addCustomFunction("GETREFCURRCODETAXBUY", "Currency_Type_API.Get_Ref_Currency_Code", "REF_CURR_CODE" );
      cmd.addParameter("COMPANY",headset.getValue("COMPANY"));
      cmd.addParameter("TAX_BUY",currencyset.getValue("TAX_BUY"));

      cmd = trans.addCustomFunction("GETREFCURRCODETAXSELL", "Currency_Type_API.Get_Ref_Currency_Code", "REF_CURR_CODE" );
      cmd.addParameter("COMPANY",headset.getValue("COMPANY"));
      cmd.addParameter("TAX_SELL",currencyset.getValue("TAX_SELL"));

      cmd = trans.addCustomFunction("GETREFCURRCODESELL", "Currency_Type_API.Get_Ref_Currency_Code", "REF_CURR_CODE" );
      cmd.addParameter("COMPANY",headset.getValue("COMPANY"));
      cmd.addParameter("SELL",currencyset.getValue("SELL"));

      trans = mgr.perform(trans);

      String sCurrCodeBuy      = trans.getValue("GETREFCURRCODEBUY/DATA/REF_CURR_CODE");
      String sCurrCodeTaxBuy   = trans.getValue("GETREFCURRCODETAXBUY/DATA/REF_CURR_CODE");
      String sCurrCodeTaxSell  = trans.getValue("GETREFCURRCODETAXSELL/DATA/REF_CURR_CODE");
      String sCurrCodeSell     = trans.getValue("GETREFCURRCODESELL/DATA/REF_CURR_CODE");

      if(!mgr.isEmpty(currencyset.getValue("BUY")))
      {
         if ("TRUE".equals(headset.getValue("GET_EMU")) && "EUR".equals(sCurrCodeBuy))
            isValid = "TRUE";
         else if(!(sCurrCodeBuy.equals(headset.getValue("GET_CURRENCY_CODE"))))
         {
            mgr.showError("ENTERWCOMPANYACCOUNTINGBUYVALIDATIONERROR: Buy currency rate type must have accounting currency as reference currency.");
         }
      }
      if(!mgr.isEmpty(currencyset.getValue("TAX_BUY")))
      {
         if ("TRUE".equals(headset.getValue("GET_EMU")) && "EUR".equals(sCurrCodeTaxBuy))
            isValid = "TRUE";
         else if(!(sCurrCodeTaxBuy.equals(headset.getValue("GET_CURRENCY_CODE"))))
         {
            mgr.showError("ENTERWCOMPANYACCOUNTINGTAXBUYVALIDATIONERROR: Tax Buy currency rate type must have accounting currency as reference currency.");
         }
      }

      if(!mgr.isEmpty(currencyset.getValue("TAX_SELL")))
      {
         if ("TRUE".equals(headset.getValue("GET_EMU")) && "EUR".equals(sCurrCodeTaxSell))
            isValid = "TRUE";
         else if(!(sCurrCodeTaxSell.equals(headset.getValue("GET_CURRENCY_CODE"))))
         {
            mgr.showError("ENTERWCOMPANYACCOUNTINGTAXSELLVALIDATIONERROR: Tax Sell currency rate type must have accounting currency as reference currency.");
         }
      }
      if(!mgr.isEmpty(currencyset.getValue("SELL")))
      {
         if ("TRUE".equals(headset.getValue("GET_EMU")) && "EUR".equals(sCurrCodeSell))
            isValid = "TRUE";
         else if(!(sCurrCodeSell.equals(headset.getValue("GET_CURRENCY_CODE"))))
         {
            mgr.showError("ENTERWCOMPANYACCOUNTINGSELLVALIDATIONERROR: Sell currency rate type must have accounting currency as reference currency.");
         }
      }

      trans.clear();

      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);

      currencylay.setLayoutMode(currencylay.SINGLE_LAYOUT);

   }

    public void  newRowCURRENCY()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      cmd = trans.addEmptyCommand("CURRENCYNEW","CURRENCY_TYPE_BASIC_DATA_API.New__",currencyblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("COMPANY", headset.getValue("COMPANY"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("CURRENCYNEW/DATA");
      currencyset.addRow(data);

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
      if (accset.countRows() > 0)
      {
         if (mgr.isEmpty(accset.getValue("PARALLEL_ACC_CURRENCY")))
            mgr.getASPField("PARALLEL_ACC_CURRENCY").unsetReadOnly();
         else
            mgr.getASPField("PARALLEL_ACC_CURRENCY").setReadOnly();

      }

     if (currencyset.countRows() > 0 )
     {
        currencybar.disableCommand(currencybar.NEWROW);
     }

     if (headset.countRows() > 0 )
     {
        if ("TRUE".equals(headset.getValue("GET_EMU")))
        {
           String sEUR = "EUR";
           mgr.getASPField("TAX_BUY").setLOVProperty("WHERE","REF_CURRENCY_CODE='"+headset.getValue("GET_CURRENCY_CODE")+"'OR REF_CURRENCY_CODE='EUR'");
           mgr.getASPField("BUY").setLOVProperty("WHERE","REF_CURRENCY_CODE='"+headset.getValue("GET_CURRENCY_CODE")+"'OR REF_CURRENCY_CODE='EUR'");
           mgr.getASPField("TAX_SELL").setLOVProperty("WHERE","REF_CURRENCY_CODE='"+headset.getValue("GET_CURRENCY_CODE")+"'OR REF_CURRENCY_CODE='EUR'");
           mgr.getASPField("SELL").setLOVProperty("WHERE","REF_CURRENCY_CODE='"+headset.getValue("GET_CURRENCY_CODE")+"'OR REF_CURRENCY_CODE='EUR'");
        }
        else
        {
           mgr.getASPField("BUY").setLOVProperty("WHERE","REF_CURRENCY_CODE='"+headset.getValue("GET_CURRENCY_CODE")+"'");
           mgr.getASPField("TAX_BUY").setLOVProperty("WHERE","REF_CURRENCY_CODE='"+headset.getValue("GET_CURRENCY_CODE")+"'");
           mgr.getASPField("TAX_SELL").setLOVProperty("WHERE","REF_CURRENCY_CODE='"+headset.getValue("GET_CURRENCY_CODE")+"'");
           mgr.getASPField("SELL").setLOVProperty("WHERE","REF_CURRENCY_CODE='"+headset.getValue("GET_CURRENCY_CODE")+"'");
        }
     }



      // Remove tab commands from actions
      accbar.removeCustomCommand("activateCurrency");

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
      setLabel("ENTERWCOMPANYACCOUTINGCOMPANY: Identity").
      setReadOnly();

      headblk.addField("PARTY_TYPE_DB").
      setHidden();

      headblk.addField("NAME").
      setLabel("ENTERWCOMPANYACCOUTINGCOMPANYNAME: Name").
      setSize(40);

      headblk.addField("ASSOCIATION_NO").
      setLabel("ENTERWCOMPANYACCOUTINGASSOCITTIONNO: Association No").
      setSize(20).
      setDynamicLOV("ASSOCIATION_INFO","PARTY_TYPE_DB");

      headblk.addField("CREATED_BY").
      setLabel("ENTERWCOMPANYACCOUTINGCREATEDBY: Created By").
      setReadOnly().
      setUpperCase().
      setSize(10);

      headblk.addField("METHODS").
      setHidden().
      setFunction("''");

      headblk.addField("DEFAULT_LANGUAGE").
      setSize(20).
      setMandatory().
      setLabel("ENTERWCOMPANYACCOUTINGDEFAULTLANGUAGE: Default Language").
      setSelectBox().
      enumerateValues("ISO_LANGUAGE_API");

      headblk.addField("HEAD_COUNTRY").
      setSize(20).setDbName("COUNTRY").
      setMandatory().
      setLabel("ENTERWCOMPANYACCOUTINGCOUNTRY: Country").
      setSelectBox().
      enumerateValues("ISO_COUNTRY_API");

      headblk.addField("GET_CURRENCY_CODE").
      setFunction("COMPANY_FINANCE_API.Get_Currency_Code(COMPANY)").
      setHidden();

      headblk.addField("GET_EMU").
      setFunction("Currency_Code_API.Get_Emu(COMPANY,COMPANY_FINANCE_API.Get_Currency_Code(COMPANY))").
      setHidden();

      headblk.setView("COMPANY");
      headblk.defineCommand("COMPANY_API","Modify__");
      headblk.disableDocMan();

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(ASPCommandBar.DELETE);
      headbar.defineCommand(headbar.COUNTFIND,"countFind");
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);

      headtbl = mgr.newASPTable( headblk );
      headtbl.setTitle(mgr.translate("ENTERWCOMPANYACCOUTINGTITLE: Company Accouting"));

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);

      headlay.setDialogColumns(4);

      //----------------------- General invoice block ---------------------

      accblk = mgr.newASPBlock("ACC");

      accblk.addField("ACC_OBJID").
      setDbName("OBJID").
      setHidden();

      accblk.addField("ACC_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      accblk.addField("ACC_COMPANY").
      setDbName("COMPANY").
      setHidden();

      accblk.addField("CURRENCY_CODE").
      setSize(20).
      setUpperCase().
      setLabel("ENTERWCOMPANYACCOUTINGCURRENCYCODE: Accounting Currency").
      setReadOnly();

      accblk.addField("VALID_FROM","Date").
      setSize(20).
      setReadOnly().
      setLabel("ENTERWCOMPANYACCOUTINGVALIDFROM:  Valid From");

      accblk.addField("PARALLEL_ACC_CURRENCY").
      setSize(20).
      setUpperCase().
      setDynamicLOV("ISO_CURRENCY").
      setLabel("ENTERWCOMPANYACCOUTINGACCCURRENCY:  Parallel Currency");

      accblk.addField("TIME_STAMP","Date").
      setSize(20).
      setReadOnly().
      setLabel("ENTERWCOMPANYACCOUTINGTIMESTAMP: Valid From");

      accblk.addField("CORRECTION_TYPE").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGCORRECTIONTYPE: Cancel/Rollback Posting Method").
      setSelectBox().
      enumerateValues("CORRECTION_TYPE_API");

      accblk.addField("DEF_AMOUNT_METHOD").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGDEFAMOUNTMETHOD: Default Amount Method").
      setSelectBox().
      enumerateValues("DEF_AMOUNT_METHOD_API");

      accblk.addField("TAX_ROUNDING_METHOD").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGTAXROMETHOD: Tax Rounding Method").
      setSelectBox().
      enumerateValues("TAX_ROUNDING_METHOD_API");

      // --------------------- Group Box: Max Overwriting Level on Tax----------------------

      accblk.addField("LEVEL_IN_PERCENT","Number").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGLEVELINPERCENT: In Percent");

      accblk.addField("LEVEL_IN_ACC_CURRENCY","Number").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGLEVELINACCCURRENCY: In Acc Currency");

      //------------------------ Group Box: Default Tax Codes for Vertex Sales Tax Q Series ---------------------------

      accblk.addField("CITY_TAX_CODE").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGCITYTAXCODE: City Code").
      setDynamicLOV("STATUTORY_FEE_NON_VAT","COMPANY").
      setUpperCase();

      accblk.addField("STATE_TAX_CODE").
      setSize(20).
      setUpperCase().
      setLabel("ENTERWCOMPANYACCOUTINGSTATETAXCODE: State Code").
      setDynamicLOV("STATUTORY_FEE_NON_VAT","COMPANY");

      accblk.addField("COUNTY_TAX_CODE").
      setSize(20).
      setUpperCase().
      setLabel("ENTERWCOMPANYACCOUTINGCOUNTYTAXCODE: County Code").
      setDynamicLOV("STATUTORY_FEE_NON_VAT","COMPANY");

      accblk.addField("DISTRICT_TAX_CODE").
      setSize(20).
      setUpperCase().
      setLabel("ENTERWCOMPANYACCOUTINGDISTRICTTAXCODE: District Code").
      setDynamicLOV("STATUTORY_FEE_NON_VAT","COMPANY");

      accblk.addField("USE_VOU_NO_PERIOD").
      setSize(20).
      setCheckBox("FALSE,TRUE").
      setReadOnly().
      setLabel("ENTERWCOMPANYACCOUTINGUSEVOUNOPERIOD: Use Voucher Number Series Per Period");

      accblk.addField("CREATION_FINISHED").
      setSize(20).
      setHidden();

      accblk.setView("COMPANY_FINANCE");
      accblk.defineCommand("COMPANY_FINANCE_API","New__,Modify__");
      accblk.setMasterBlock(headblk);
      accblk.disableDocMan();

      accset = accblk.getASPRowSet();

      accbar = mgr.newASPCommandBar(accblk);
      accbar.defineCommand(accbar.OKFIND,"okFindACC");
      accbar.disableCommand(accbar.NEWROW);
      accbar.disableCommand(accbar.SAVENEW);
      accbar.disableCommand(accbar.DUPLICATEROW);
      accbar.disableCommand(accbar.DELETE);


      acctbl = mgr.newASPTable(accblk );
      acctbl.setTitle(mgr.translate("ENTERWCOMPANYACCOUTINGACCBLKTITLE: Company Invoice Info"));
      acclay = accblk.getASPBlockLayout();
      acclay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      acclay.defineGroup(mgr.translate("ENTERWCOMPANYACCOUTINGGRP1: Default"),"CURRENCY_CODE,VALID_FROM,PARALLEL_ACC_CURRENCY,TIME_STAMP,CORRECTION_TYPE,DEF_AMOUNT_METHOD,TAX_ROUNDING_METHOD,USE_VOU_NO_PERIOD",false,true,2);
      acclay.defineGroup(mgr.translate("ENTERWCOMPANYACCOUTINGGRP2: Max Overwriting Level on Tax"),"LEVEL_IN_PERCENT,LEVEL_IN_ACC_CURRENCY",true,false,2);
      acclay.defineGroup(mgr.translate("ENTERWCOMPANYACCOUTINGGRP3: Default Tax Codes for Vertex Sales Tax Q Series"),"CITY_TAX_CODE,STATE_TAX_CODE,COUNTY_TAX_CODE,DISTRICT_TAX_CODE",true,false,2);

      accbar.addCustomCommand("activateCurrency",mgr.translate("ENTERWCOMPANYACCOUTINGTAB: Currency Rate Type Information"));

      // ------------------  Currency Rate Type Information ---------------------------

      currencyblk = mgr.newASPBlock("CURRENCY");

      currencyblk.addField("CURR_OBJID").
      setDbName("OBJID").
      setHidden();

      currencyblk.addField("CURR_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      currencyblk.addField("CURR_COMPANY").
      setDbName("COMPANY").
      setHidden();

      currencyblk.addField("BUY").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGBUY: Buy").
      setDynamicLOV("CURRENCY_TYPE","COMPANY").
      setUpperCase().
      setCustomValidation("BUY,COMPANY","BUY_DES");

      currencyblk.addField("BUY_DES").
      setFunction("CURRENCY_TYPE_API.GET_DESCRIPTION(COMPANY,BUY)").
      setSize(20);

      currencyblk.addField("SELL").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGSELL: Sell").
      setDynamicLOV("CURRENCY_TYPE","COMPANY").
      setUpperCase().
      setCustomValidation("SELL,COMPANY","SELL_DES");

      currencyblk.addField("SELL_DES").
      setFunction("CURRENCY_TYPE_API.GET_DESCRIPTION(COMPANY,SELL)").
      setSize(20);

      // -------------- Group Box: Default Currency Rate Types for TAX -----------------------

      currencyblk.addField("USE_TAX_RATES").
      setSize(20).setCheckBox("FALSE,TRUE").
      setLabel("ENTERWCOMPANYACCOUTINGUSETAXRATES: Allow Specific Currency Rates for Tax Transactions").
      setValidateFunction("validateTaxRates");

      currencyblk.addField("TAX_BUY").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGTAXBUY: Tax Buy").
      setDynamicLOV("CURRENCY_TYPE","COMPANY").
      setUpperCase().
      setCustomValidation("TAX_BUY,COMPANY,USE_TAX_RATES","TAX_BUY,TAX_BUY_DES").
      setValidateFunction("validateTax");

      currencyblk.addField("TAX_BUY_DES").
      setFunction("CURRENCY_TYPE_API.Get_Description(COMPANY,TAX_BUY)").
      setSize(20).
      setReadOnly();

      currencyblk.addField("TAX_SELL").
      setSize(20).
      setLabel("ENTERWCOMPANYACCOUTINGTAXSELL: Tax Sell").
      setDynamicLOV("CURRENCY_TYPE","COMPANY").
      setCustomValidation("TAX_SELL,COMPANY,USE_TAX_RATES","TAX_SELL,TAX_SELL_DES").
      setValidateFunction("validateTS");

      currencyblk.addField("TAX_SELL_DES").
      setFunction("CURRENCY_TYPE_API.Get_Description(COMPANY,TAX_SELL)").
      setSize(20).
      setReadOnly();

      currencyblk.addField("REF_CURR_CODE").
      setFunction("''").
      setHidden();

      currencyblk.setView("CURRENCY_TYPE_BASIC_DATA");
      currencyblk.defineCommand("CURRENCY_TYPE_BASIC_DATA_API","New__,Modify__");
      currencyblk.setMasterBlock(headblk);
      currencyblk.disableDocMan();

      currencyset = currencyblk.getASPRowSet();

      currencybar = mgr.newASPCommandBar(currencyblk);
      currencybar.defineCommand(currencybar.OKFIND,"okFindCURRENCY");
      currencybar.defineCommand(currencybar.NEWROW,"newRowCURRENCY");
      currencybar.defineCommand(currencybar.SAVERETURN,"saveReturnCURRENCY");
      currencybar.disableCommand(currencybar.SAVENEW);
      currencybar.disableCommand(currencybar.DUPLICATEROW);
      currencybar.disableCommand(currencybar.DELETE);


      currencytbl = mgr.newASPTable(currencyblk );
      currencytbl.setTitle(mgr.translate("ENTERWENTERWCOMPANYACCOUTINGTITLE: Company Invoice Info"));
      currencylay = currencyblk.getASPBlockLayout();
      currencylay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      currencylay.defineGroup(mgr.translate("ENTERWCOMPANYACCOUTINGDEFULTGRP1: Default"),"BUY,BUY_DES,SELL,SELL_DES",false,true,2);
      currencylay.defineGroup(mgr.translate("ENTERWCOMPANYACCOUTINGGROUP2: Currency Rate Types for TAX"),"USE_TAX_RATES,TAX_BUY,TAX_BUY_DES,TAX_SELL,TAX_SELL_DES",true,false,1);

      currencylay.setSimple("BUY_DES");
      currencylay.setSimple("SELL_DES");
      currencylay.setSimple("TAX_SELL_DES");
      currencylay.setSimple("TAX_BUY_DES");

      currencylay.setDataSpan("BUY",2);
      currencylay.setDataSpan("SELL",2);



      // ----------------------------------------------------------------------
      //                         Tabs
      // ----------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("ENTERWCOMPANYACCOUNTINGTAB: Currency Rate Type Information"),"javascript:commandSet('ACC.activateCurrency','')");
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWCOMPANYACCOUNTINGINFODESCRIPTION: Company Accounting Info";
   }

   protected String getTitle()
   {
      return "ENTERWCOMPANYACCOUNTINGINFOTITLE: Company Accounting Info";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      int activetab = tabs.getActiveTab();


      if (headlay.isVisible())
         appendToHTML(headlay.show());

      if (headset.countRows()>0)
      {
         if (acclay.isVisible())
         {
            appendToHTML(acclay.show());
         }
         else if (acclay.isEditLayout() || acclay.isNewLayout())
         {
            appendToHTML(acclay.show());
         }
         if (activetab == 1 )
         {

            if (currencylay.isEditLayout() || currencylay.isNewLayout())
            {
               appendToHTML(currencylay.show());
               appendDirtyJavaScript("function validateTax() {\n" );
               appendDirtyJavaScript("if(f.USE_TAX_RATES.checked == false)\n");
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.TAX_BUY.value = '';\n");
               appendDirtyJavaScript("       alert('");
               appendDirtyJavaScript(mgr.translate("ENTERWCOMPANYACCOUNTINGTEABUYREADONLY: Allow Specific Currency Rates for Tax Transactions should be checked "));
               appendDirtyJavaScript("');\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("validateTaxBuy(i);\n");
               appendDirtyJavaScript("}" );

               appendDirtyJavaScript("function validateTS() {\n" );
               appendDirtyJavaScript("if(f.USE_TAX_RATES.checked == false)\n");
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.TAX_SELL.value = '';\n");
               appendDirtyJavaScript("       alert('");
               appendDirtyJavaScript(mgr.translate("ENTERWCOMPANYACCOUNTINGTEABUYREADONLY2: Allow Specific Currency Rates for Tax Transactions should be checked "));
               appendDirtyJavaScript("');\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("validateTaxSell(i);\n");
               appendDirtyJavaScript("}" );

               appendDirtyJavaScript("function validateTaxRates() {\n" );
               appendDirtyJavaScript("if(f.USE_TAX_RATES.checked == false)\n");
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.TAX_SELL.value = '';\n");
               appendDirtyJavaScript("  f.TAX_SELL.readOnly = true;\n");
               appendDirtyJavaScript("  f.TAX_SELL_DES.value = '';\n");
               appendDirtyJavaScript("  f.TAX_BUY.value = '';\n");
               appendDirtyJavaScript("  f.TAX_BUY.readOnly = true;\n");
               appendDirtyJavaScript("  f.TAX_BUY_DES.value = '';\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("else" );
               appendDirtyJavaScript("{" );
               appendDirtyJavaScript("  f.TAX_SELL.readOnly = false;\n");
               appendDirtyJavaScript("  f.TAX_BUY.readOnly = false;\n");
               appendDirtyJavaScript("}" );
               appendDirtyJavaScript("}" );

               if ("TRUE".equals(isOkFind))
               {

                  appendDirtyJavaScript("if(f.USE_TAX_RATES.checked == true)\n");
                  appendDirtyJavaScript("{" );
                  appendDirtyJavaScript("  f.TAX_BUY.readOnly = false;\n");
                  appendDirtyJavaScript("  f.TAX_SELL.readOnly = false;\n");
                  appendDirtyJavaScript("}" );
                  appendDirtyJavaScript("else" );
                  appendDirtyJavaScript("{" );
                  appendDirtyJavaScript("  f.TAX_BUY.readOnly = true;\n");
                  appendDirtyJavaScript("  f.TAX_SELL.readOnly = true;\n");
                  appendDirtyJavaScript("}" );
               }
            }
            else if (currencylay.isVisible())
            {
               appendToHTML(tabs.showTabsInit());
               appendToHTML(currencylay.show());
               appendToHTML(tabs.showTabsFinish());

            }
          }
      }

   }
}
