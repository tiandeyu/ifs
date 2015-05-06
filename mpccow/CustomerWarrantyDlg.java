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
*  File        : CustomerWarrantyDlg.java
*  Created     : Haunlk 2006/11/23
*  Description :
*  Notes       :
* ----------------------------------------------------------------------------
* ---------------------- Wings Merge Start -----------------------------------
*  Modified    : Haunlk 2007-01-11  Modified the Translation Constants.
*                Haunlk 2007-01-23  Added the RMB Document Texts and related code.
*                ChJalk 2007-01-30  Merged Wingd Code.
*                Haunlk 2007-03-13  B141014,Added new group box for NOTE_TEXT
* ---------------------- Wings Merge End -------------------------------------
*                NiDalk 2007-07-10  Performed XSS corrections.
*/

package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class CustomerWarrantyDlg extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.CustomerWarrantyDlg");



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

   private ASPBlock itemblk2;
   private ASPRowSet itemset2;
   private ASPCommandBar itembar2;
   private ASPTable itemtbl2;
   private ASPBlockLayout itemlay2;

   private ASPTabContainer tabs;
   private String activetab;
   private ASPBuffer data;

   private ASPBlock        ref;
   private ASPRowSet       refset;

   private ASPCommand cmd;
   private ASPContext ctx;
   private ASPLog log;
   private ASPField f;

   //===============================================================
   // Transient temporary variables
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean bOkPressed = false;
   private boolean closeWindow = false;
   private boolean openTemplate = false;
   private boolean okPressed = false;
   private int currRow;
   private ASPQuery q;
   private int headrowno;

   private String sMode;
   private String sWarrantyId;
   private String newWarrantyId;
   private String sPartNo;
   private String sContract;
   private String sEngChgLevel;
   private String sBomType;
   private String sUrl;
   private String sObjstate;
   private String sOldWarrantyId;
   private String sOldObjstate;
   private String windowPath;
   private String url;
   private boolean firstRequest=false;

   String sRmbUrl;
   boolean isDocText;


   //===============================================================
   // Construction
   //===============================================================
   public CustomerWarrantyDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();

      log = mgr.getASPLog();
      trans = mgr.newASPTransactionBuffer();

      if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
      {
         validate();
      }

      if ( "OK".equals(mgr.readValue("FIRST_REQUEST")) || !mgr.isExplorer() ) //this was added to pop up the oracal error messages as there is a bug in IE
      {
         activetab  = ctx.readValue("ACTIVETAB","1");
         sMode  = ctx.readValue("MODE","");
         sWarrantyId = ctx.readValue("WARRANTYID");
         newWarrantyId = ctx.readValue("NEWWARRANTYID");
         sPartNo = ctx.readValue("PARTNO");
         sContract = ctx.readValue("CONTRACT");
         sEngChgLevel = ctx.readValue("ENGCHGLEVEL");
         sBomType = ctx.readValue("BOMTYPEDB");
         sObjstate = ctx.readValue("STATE");
         sUrl = ctx.readValue("URL");


         if ( mgr.commandBarActivated() )
         {
            eval(mgr.commandBarFunction());
         }
         else if ( !mgr.isEmpty(mgr.getQueryStringValue("MODE")) )
         {
            sUrl  = mgr.getURL();
            sMode = mgr.getQueryStringValue("MODE");
            sWarrantyId = mgr.getQueryStringValue("CUST_WARRANTY_ID");
            sContract =  mgr.getQueryStringValue("CONTRACT");
            sPartNo =  mgr.getQueryStringValue("PART_NO");
            sBomType = mgr.getQueryStringValue("BOM_TYPE_DB");
            sEngChgLevel = mgr.getQueryStringValue("ENG_CHG_LEVEL");


            if ( !mgr.isEmpty(sWarrantyId) )
            {
               getWarrantyState();
            }
            sPartNo = mgr.getQueryStringValue("PART_NO");
            okFind();

         }
         else if ( mgr.buttonPressed("TEMPLATE") )
         {
            openTemplate = true;
            url ="WARRANTY_ID="+mgr.URLEncode((mgr.isEmpty(sWarrantyId) ? "" : sWarrantyId))+"&PART_NO="+mgr.URLEncode(sPartNo)+"&SERIAL_NO=*&CONTRACT="+mgr.URLEncode(sContract)+"&ENG_CHG_LEVEL="+mgr.URLEncode(sEngChgLevel)+"&BOM_TYPE_DB="+mgr.URLEncode(sBomType);

            sOldWarrantyId = sWarrantyId;
            sOldObjstate   = sObjstate;

         }
         else if ( mgr.buttonPressed("OK") || mgr.buttonPressed("CANCEL") )
         {
            okPressed = true ;
         }
         else if ( "TRUE".equals(mgr.readValue("REFRESHROWSET")) )
            okFindWARRANTYLANG();


         adjust();
         tabs.saveActiveTab();
         ctx.writeValue("ACTIVETAB",activetab);
         ctx.writeValue("MODE",sMode);
         ctx.writeValue("WARRANTYID",sWarrantyId);
         ctx.writeValue("NEWWARRANTYID",newWarrantyId);
         ctx.writeValue("PARTNO",sPartNo);
         ctx.writeValue("CONTRACT",sContract);
         ctx.writeValue("BOMTYPEDB",sBomType);
         ctx.writeValue("ENGCHGLEVEL",sEngChgLevel);
         ctx.writeValue("STATE",sObjstate);
         ctx.writeValue("URL",sUrl);


      }
      else
      {
         firstRequest = true;
      }

   }
   public void  newRowHEAD()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      if ( mgr.isEmpty(sWarrantyId) )
      {

         cmd = trans.addCustomCommand("GETID","Cust_Warranty_API.New");
         cmd.addParameter("WARRANTY_ID",sWarrantyId);
         cmd = trans.addEmptyCommand("HEADNEW","CUST_WARRANTY_TYPE_API.New__",headblk);
         cmd.setOption("ACTION","PREPARE");
         cmd.addReference("WARRANTY_ID","GETID/DATA");
         trans = mgr.perform(trans);
         data = trans.getBuffer("HEADNEW/DATA");
         headset.addRow(data);
         headset.setValue("WARRANTY_ID",trans.getValue("GETID/DATA/WARRANTY_ID"));
         newWarrantyId = trans.getValue("GETID/DATA/WARRANTY_ID");

      }
      else
      {
         cmd = trans.addEmptyCommand("HEADNEW","CUST_WARRANTY_TYPE_API.New__",headblk);
         cmd.setOption("ACTION","PREPARE");
         cmd.setParameter("WARRANTY_ID", sWarrantyId);
         trans = mgr.perform(trans);
         data = trans.getBuffer("HEADNEW/DATA");
         headset.addRow(data);
      }
   }

   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addEmptyCommand("ITEM1NEW","CUST_WARRANTY_CONDITION_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM1_WARRANTY_ID", sWarrantyId);
      cmd.setParameter("ITEM1_WARRANTY_TYPE_ID", headset.getValue("WARRANTY_TYPE_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1NEW/DATA");
      itemset1.addRow(data);
   }

   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addEmptyCommand("ITEM2NEW","WARRANTY_LANG_DESC_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM2_WARRANTY_ID", sWarrantyId);
      cmd.setParameter("ITEM2_WARRANTY_TYPE_ID", headset.getValue("WARRANTY_TYPE_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2NEW/DATA");
      itemset2.addRow(data);
   }

   public void  saveReturnHEAD()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      headset.changeRow();
      mgr.submit(trans);
      trans.clear();

      if ( mgr.isEmpty(sWarrantyId) )
      {
         cmd = trans.addCustomCommand("NEWWARRANTI","PROD_STRUCTURE_HEAD_API.Modify_Warranty");
         cmd.addParameter("CONTRACT",sContract);
         cmd.addParameter("PART_NO",sPartNo);
         cmd.addParameter("ENG_CHG_LEVEL",sEngChgLevel);
         cmd.addParameter("BOM_TYPE_DB",sBomType);
         cmd.addParameter("WARRANTY_ID",newWarrantyId);
         trans = mgr.perform(trans);
         sWarrantyId = newWarrantyId;
      }
   }

   public void  saveNewHEAD()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      headset.changeRow();
      mgr.submit(trans);
      trans.clear();

      if ( mgr.isEmpty(sWarrantyId) )
      {
         cmd = trans.addCustomCommand("NEWWARRANTI","PROD_STRUCTURE_HEAD_API.Modify_Warranty");
         cmd.addParameter("CONTRACT",sContract);
         cmd.addParameter("PART_NO",sPartNo);
         cmd.addParameter("ENG_CHG_LEVEL",sEngChgLevel);
         cmd.addParameter("BOM_TYPE_DB",sBomType);
         cmd.addParameter("WARRANTY_ID",newWarrantyId);
         trans = mgr.perform(trans);
         sWarrantyId = newWarrantyId;
      }
      newRowHEAD();
   }

   public void docText()
   {
      ASPManager mgr = getASPManager();

      if ( itemlay2.isMultirowLayout() )
      {
         itemset2.storeSelections();
         itemset2.setFilterOn();
      }
      else
      {
         itemset2.unselectRows();
         itemset2.selectRow();
      }

      isDocText =true;
      sRmbUrl = "MODE="+mgr.URLEncode(sMode)+"&NOTE_ID="+mgr.URLEncode(itemset2.getValue("NOTE_ID"))+ "&WARRANTY_TYPE_ID="+mgr.URLEncode(itemset2.getValue("WARRANTY_TYPE_ID"))+"&LANGUAGE_CODE="+mgr.URLEncode(itemset2.getValue("LANGUAGE_CODE"));

      if ( itemlay2.isMultirowLayout() )
      {
         itemset2.setFilterOff();
      }
   }



   // --------------------------- Populating Methods ---------------------------

   public void getWarrantyState()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomFunction("GETSTATE","Cust_Warranty_API.Get_Objstate","STATE");
      cmd.addParameter("WARRANTY_ID",sWarrantyId);

      trans = mgr.perform(trans);
      sObjstate  = trans.getValue("GETSTATE/DATA/STATE");

   }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WARRANTY_ID = ?");
      q.addParameter("WARRANTY_ID",sWarrantyId);
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      if ( !mgr.isEmpty(mgr.getQueryStringValue("WARRANTY_ID")) )
      {
         q = trans.addEmptyQuery(headblk);
         q.addWhereCondition("WARRANTY_ID = ?");
         q.addParameter("WARRANTY_ID",mgr.getQueryStringValue("WARRANTY_ID"));
         sWarrantyId = mgr.getQueryStringValue("WARRANTY_ID");
      }
      else
      {
         q = trans.addEmptyQuery(headblk);
         q.addWhereCondition("WARRANTY_ID = ?");
         q.addParameter("WARRANTY_ID",sWarrantyId);
      }
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("MPCCOWSHOPORDNODATA: No data found."));
         headset.clear();
      }
      else
      {
         okFindCUSTWARRANTY();
         okFindWARRANTYLANG();
      }

      eval(headset.syncItemSets());
   }

   public void  okFindCUSTWARRANTY()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      trans.clear();
      q = trans.addEmptyQuery(itemblk1);
      q.addWhereCondition("WARRANTY_ID = ?");
      q.addParameter("WARRANTY_ID",headset.getValue("WARRANTY_ID"));

      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
   }

   public void  okFindWARRANTYLANG()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      trans.clear();
      q = trans.addEmptyQuery(itemblk2);
      q.addWhereCondition("WARRANTY_ID = ?");
      q.addParameter("WARRANTY_ID",headset.getValue("WARRANTY_ID"));

      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
   }

   public void  deleteRowHEAD()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      headset.refreshAllRows();
      headset.store();
      headset.setRemoved();

      cmd = trans.addCustomCommand("DELETEWARRANTY","PROD_STRUCTURE_HEAD_API.Modify_Warranty");
      cmd.addParameter("CONTRACT",sContract);
      cmd.addParameter("PART_NO",sPartNo);
      cmd.addParameter("ENG_CHG_LEVEL",sEngChgLevel);
      cmd.addParameter("BOM_TYPE_DB",sBomType);
      cmd.addParameter("WARRANTY_ID","");
      mgr.submit(trans);

   }


   public void  validate()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();
      String txt;
      String data;
      String val = mgr.readValue("VALIDATE");
      if ( "CONDITION_ID".equals(val) )
      {
         cmd = trans.addCustomFunction("GETDES","Warranty_Condition_API.Get_Condition_Description","CONDITION_DES");
         cmd.addParameter("CONDITION_ID",mgr.readValue("CONDITION_ID"));

         cmd = trans.addCustomFunction("GETMINVALUE","warranty_condition_api.Get_Min_Value","MIN_VALUE");
         cmd.addParameter("CONDITION_ID",mgr.readValue("CONDITION_ID"));

         cmd = trans.addCustomFunction("GETMAXVALUE","warranty_condition_api.Get_Max_Value","MAX_VALUE");
         cmd.addParameter("CONDITION_ID",mgr.readValue("CONDITION_ID"));

         trans = mgr.validate(trans);

         String sDes      = trans.getValue("GETDES/DATA/CONDITION_DES");
         String sMinValue = trans.getValue("GETMINVALUE/DATA/MIN_VALUE");
         String sMaxValue = trans.getValue("GETMAXVALUE/DATA/MAX_VALUE");

         txt = sDes+"^"+(mgr.isEmpty(sMinValue) ? "" : sMinValue)+"^"+(mgr.isEmpty(sMaxValue) ? "" : sMaxValue)+"^";
         mgr.responseWrite(txt);

      }
      mgr.endResponse();
   }

   public void activateWarrantyLang()
   {

      tabs.setActiveTab(2);
      activetab = "2";
   }

   public void activateWarrantyCondition()
   {
      activetab = "1";
      tabs.setActiveTab(1);
   }


   //  -------------------------- Pre-Define ---------------------------------------
   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      disableNavigate();
      disableHomeIcon();

      headblk = mgr.newASPBlock("HEAD");

      // ---------------- Source Revision ----------------------
      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("WARRANTY_ID","Number");
      f.setHidden();

      f = headblk.addField("STATE");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("CONTRACT");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("PART_NO");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("ENG_CHG_LEVEL");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("BOM_TYPE_DB");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("WARRANTY_TYPE_ID");
      f.setLabel("MPCCOWCUSTOMERWARRANTYDLGWARRANTYTYPEID: Warranty Type Id");
      f.setUpperCase();
      f.setSize(20);

      f = headblk.addField("WARRANTY_DESCRIPTION");
      f.setLabel("MPCCOWCUSTOMERWARRANTYDLGWARRANTYDESCRIPTION: Description");
      f.setSize(30);

      f = headblk.addField("NOTE_TEXT");
      f.setLabel("MPCCOWCUSTOMERWARRANTYDLGNOTETEXT: Note");
      f.setMaxLength(2000);
      f.setHeight(5);
      f.setSize(50);


      // ----------------------- Group Box: Cost Types -------------------------------------

      f = headblk.addField("MATERIAL_COST_TYPE_DB");
      f.setLabel("MPCCOWCUSTOMERWARRANTYDLGMATERIALCOSTTYPEDB: Material");
      f.setCheckBox("NOT MATERIAL,MATERIAL");

      f = headblk.addField("EXPENSES_COST_TYPE_DB");
      f.setLabel("MPCCOWCUSTOMERWARRANTYDLGEXPENSESCOSTTYPEDB: Expenses");
      f.setCheckBox("NOT EXPENSES,EXPENSES");

      f = headblk.addField("FIXED_PRICE_COST_TYPE_DB");
      f.setLabel("MPCCOWCUSTOMERWARRANTYDLGFIXEDPRICECOSTTYPEDB: Fixed Price");
      f.setCheckBox("NOT FIXED PRICE,FIXED PRICE");

      f = headblk.addField("PERSONNEL_COST_TYPE_DB");
      f.setLabel("MPCCOWCUSTOMERWARRANTYDLGPERSONNELCOSTTYPEDB: Personnel");
      f.setCheckBox("NOT PERSONNEL,PERSONNEL");

      f = headblk.addField("EXTERNAL_COST_TYPE_DB");
      f.setLabel("MPCCOWCUSTOMERWARRANTYDLGEXTERNALCOSTTYPEDB: External");
      f.setCheckBox("NOT EXTERNAL,EXTERNAL");



      // ---------------------------------------------


      headblk.setView("CUST_WARRANTY_TYPE");
      headblk.defineCommand("CUST_WARRANTY_TYPE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headtbl = mgr.newASPTable(headblk);
      headbar = mgr.newASPCommandBar(headblk);

      headlay =  headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headbar.defineCommand(headbar.OKFIND,"okFind");
      headbar.defineCommand(headbar.NEWROW,"newRowHEAD");
      headbar.defineCommand(headbar.SAVERETURN,"saveReturnHEAD");
      headbar.defineCommand(headbar.SAVENEW,"saveNewHEAD");
      headbar.defineCommand(headbar.DELETE,"deleteRowHEAD");
      headbar.disableCommand(headbar.DUPLICATEROW);

      headlay.defineGroup(mgr.translate("MPCCOWCUSTOMERWARRANTYDLGGENERALGRP:General"),"WARRANTY_TYPE_ID,WARRANTY_DESCRIPTION",false,true,2);
      headlay.defineGroup(mgr.translate("MPCCOWCUSTOMERWARRANTYDLGNOTEGRP: Note"),"NOTE_TEXT",true,false,1);
      headlay.defineGroup(mgr.translate("MPCCOWCUSTOMERWARRANTYDLGCOSTGRP: Cost Types"),"MATERIAL_COST_TYPE_DB,EXPENSES_COST_TYPE_DB,FIXED_PRICE_COST_TYPE_DB,PERSONNEL_COST_TYPE_DB,EXTERNAL_COST_TYPE_DB",true,true,3);

      //------ Commands for tabs ----------------------
      headbar.addCustomCommand("activateWarrantyCondition",mgr.translate("MPCCOWCUSTOMERWARRANTYDLGWARRENTYCONDITIONTAB: Warranty Condition"));
      headbar.addCustomCommand("activateWarrantyLang",mgr.translate("MPCCOWCUSTOMERWARRANTYDLGWARRENTYLANGTAB: Warranty Language Description"));


      ///////////////////// new block for Warranty Condition /////////////////////

      itemblk1 = mgr.newASPBlock("CUSTWARRANTY");

      itemblk1.addField("ITEM_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk1.addField("ITEM_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk1.addField("ITEM1_WARRANTY_ID").
      setDbName("WARRANTY_ID").
      setHidden();

      itemblk1.addField("ITEM1_WARRANTY_TYPE_ID").
      setDbName("WARRANTY_TYPE_ID").
      setHidden();

      itemblk1.addField("CONDITION_ID","Number").
      setLabel("MPCCOWCUSTOMERWARRANTYDLGCONDITIONID: Condition ID").
      setDynamicLOV("WARRANTY_CONDITION").
      setCustomValidation("CONDITION_ID","CONDITION_DES,MIN_VALUE,MAX_VALUE").
      setSize(20);

      itemblk1.addField("CONDITION_DES").
      setLabel("MPCCOWCUSTOMERWARRANTYDLGCONDITIONDES: Description").
      setSize(20).
      setFunction("Warranty_Condition_API.Get_Condition_Description(:CONDITION_ID)");


      itemblk1.addField("VALID_FROM","Date").
      setSize(20).
      setHidden().
      setFunction("''");


      itemblk1.addField("VALID_TO","Date").
      setSize(20).
      setHidden().
      setFunction("''");


      itemblk1.addField("MIN_VALUE","Number").
      setLabel("MPCCOWCUSTOMERWARRANTYDLGMINVALUE: Min Value").
      setSize(20);

      itemblk1.addField("MAX_VALUE","Number").
      setLabel("MPCCOWCUSTOMERWARRANTYDLGMAXVALUE: Max From").
      setSize(20);


      itemblk1.addField("TIME_UNIT").
      setLabel("MPCCOWCUSTOMERWARRANTYDLGTIMEUNIT: Time Unit").
      setSize(20).
      setSelectBox().setFunction("Warranty_Condition_API.Get_Time_Unit(:CONDITION_ID)").
      enumerateValues("Time_Unit_API");


      itemblk1.addField("OTHER_UM").
      setLabel("MPCCOWCUSTOMERWARRANTYDLGOTHERUM: Other U/M").
      setSize(20).
      setDynamicLOV("ISO_UNIT").
      setFunction("Warranty_Condition_API.Get_Unit_Code(:CONDITION_ID)");


      itemblk1.setView("CUST_WARRANTY_CONDITION");
      itemblk1.defineCommand("CUST_WARRANTY_CONDITION_API","New__,Modify__,Remove__");
      itemblk1.setMasterBlock(headblk);

      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.OKFIND,"okFindCUSTWARRANTY");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");

      itembar1.disableCommand(itembar1.FIND);


      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle("MPCCOWCOPYSTRUCTREVISIONDLGITEMTITLE: Structure Alternate");
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      itemtbl1.enableRowSelect();

      ///////////////////// new block for Warranty Language Description /////////////////////

      itemblk2 = mgr.newASPBlock("WARRANTYLANG");

      itemblk2.addField("ITEM2_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk2.addField("ITEM2_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk2.addField("ITEM2_WARRANTY_ID").
      setDbName("WARRANTY_ID").
      setHidden();

      itemblk2.addField("ITEM2_WARRANTY_TYPE_ID").
      setDbName("WARRANTY_TYPE_ID").
      setHidden();

      itemblk2.addField("ITEM_NOTE_ID").
      setDbName("NOTE_ID").
      setHidden();

      itemblk2.addField("LANGUAGE_CODE").
      setLabel("MPCCOWCUSTOMERWARRANTYDLGLANGUAGECODE: Language Code").
      setSize(20).
      setDynamicLOV("APPLICATION_LANGUAGE");

      itemblk2.addField("WARRANTY_TYPE_DESC").
      setLabel("MPCCOWCUSTOMERWARRANTYDLGWARRANTYTYPEDESC: Warranty Type Description").
      setSize(20);

      itemblk2.addField("DOC_TEST").
      setLabel("MPCCOWCUSTOMERWARRANTYDLGDOCUMENTTEXT: Document Text").
      setSize(20).
      setFunction("Document_Text_API.Note_Id_Exist(:ITEM_NOTE_ID)").
      setCheckBox("0,1").
      setReadOnly();

      mgr.getASPField("ITEM_NOTE_ID").setValidation("DOC_TEST");

      itemblk2.setView("WARRANTY_LANG_DESC");
      itemblk2.defineCommand("WARRANTY_LANG_DESC_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(headblk);

      itemset2 = itemblk2.getASPRowSet();
      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.addCustomCommand("docText",mgr.translate("MPCCOWCUSTOMERWARRANTYDLGDOCTEXT: Document Texts..."));
      itembar2.disableCommand(itembar2.FIND);
      itembar2.defineCommand(itembar2.OKFIND,"okFindWARRANTYLANG");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.disableCommand(itembar2.DUPLICATEROW);
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle("MPCCOWCOPYSTRUCTREVISIONDLGITEMTITLE: Structure Alternate");
      itemtbl2.enableRowSelect();
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);



      // ----------------------------------------------------------------------
      //                         Tabs
      // ----------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("MPCCOWCUSTOMERWARRANTYDLGTAB1: Warranty Condition"),"javascript:commandSet('HEAD.activateWarrantyCondition','')");
      tabs.addTab(mgr.translate("MPCCOWCUSTOMERWARRANTYDLGTAB2:  Warranty Language Description"),"javascript:commandSet('HEAD.activateWarrantyLang','')");

   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();
      if ( "view".equals(sMode) )
      {
         headbar.disableCommand(headbar.NEWROW);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.EDITROW);
         itembar1.disableCommand(itembar1.NEWROW);
         itembar1.disableCommand(itembar1.EDITROW);
         itembar1.disableCommand(itembar1.DUPLICATEROW);
         itembar1.disableCommand(itembar1.DELETE);
         itembar2.disableCommand(itembar2.NEWROW);
         itembar2.disableCommand(itembar2.DELETE);
         itembar2.disableCommand(itembar2.EDITROW);
         itembar2.disableCommand(itembar2.DUPLICATEROW);
      }
      if ( itemlay1.isEditLayout() )
      {
         mgr.getASPField("CONDITION_ID").setReadOnly();
         mgr.getASPField("CONDITION_DES").setReadOnly();
         mgr.getASPField("TIME_UNIT").setReadOnly();
         mgr.getASPField("OTHER_UM").setReadOnly();
      }
      if ( itemlay2.isEditLayout() )
      {
         mgr.getASPField("LANGUAGE_CODE").setReadOnly();
         mgr.getASPField("DOC_TEST").setReadOnly();
      }

      /*if (mgr.isEmpty(sWarrantyId))
      {
         headlay.setLayoutMode(headlay.NEW_LAYOUT);
         headlay.setEditable();
      }
      */

      headbar.removeCustomCommand("activateWarrantyCondition");
      headbar.removeCustomCommand("activateWarrantyLang");


   }



   protected String getDescription()
   {
      return "MPCCOWCUSTOMERWARRANTYDLGDESCRIPTION: Customer Warranty";
   }

   protected String getTitle()
   {
      ASPManager mgr = getASPManager();
      return "MPCCOWCUSTOMERWARRANTYDLGTITLE: Customer Warranty ";

   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      ASPManager mgr = getASPManager();
      String queryString = mgr.getURL()+"?"+mgr.getQueryString();

      if ( firstRequest && mgr.isExplorer() ) ////this was added to pop up the oracal error messages as there is a bug in IE
      {
         out.append("<html>\n");
         out.append("<head></head>\n");
         out.append("<body>");
         out.append("<form name='form' method='POST' action='"+queryString+"'>");
         out.append(" <input type=\"hidden\" name=\"FIRST_REQUEST\" >");
         out.append("</form></body></html>");
         appendDirtyJavaScript("document.form.FIRST_REQUEST.value='OK';document.form.submit();");
         return out;
      }
      else
      {
         out.append("<html>\n");
         out.append("<head>\n");
         out.append(mgr.generateHeadTag(getDescription()));
         out.append("</head>\n");
         out.append("<body ");
         out.append(mgr.generateBodyTag());
         out.append(" >\n");
         out.append("<div id=\"tooltip\" class=\"tooltip\" style=\"border:1px solid black;position:absolute;visibility:hidden;\"></div>");
         out.append("<form ");
         out.append(mgr.generateFormTag());
         out.append(" >\n");
         out.append(" <input type=\"hidden\" name=\"FIRST_REQUEST\" value=\"OK\">\n");
         out.append(" <input type=\"hidden\" name=\"REFRESHROWSET\" value=\"OK\">\n");
         out.append("  <input type=\"hidden\" name=\"REFRESH\" value=\"FALSE\">\n");
         out.append(mgr.startPresentation(getTitle()));
         printContents();

         if ( openTemplate )
         {
            appendDirtyJavaScript("      window.open(\"../mpccow/CustomerTemplateDlg.page?");
            appendDirtyJavaScript(url);
            appendDirtyJavaScript("\",\"DLGWINTEMP\",\"status,resizable,scrollbars,width=550,height=300,left=150,top=300\");\n");
         }
         if ( okPressed )
         {
            appendDirtyJavaScript("window.close();\n");

         }

         appendDirtyJavaScript("function repopulate(sTemplateId,sContract,sPartNo,sBomType,sEngChgLevel)");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("  window.location =\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sUrl));
         appendDirtyJavaScript("\"+\"?MODE=");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sMode));
         appendDirtyJavaScript("&CONTRACT=\"+sContract+\"&PART_NO=\"+sPartNo+\"&WARRANTY_ID=\"+sTemplateId+\"&ENG_CHG_LEVEL=\"+sEngChgLevel+\"&BOM_TYPE_DB=\"+sBomType;\n");
         appendDirtyJavaScript("} \n");

         appendDirtyJavaScript("function refresh()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   document.form.REFRESHROWSET.value = \"TRUE\";\n");
         appendDirtyJavaScript("   submit();\n");
         appendDirtyJavaScript("}\n");

         if ( isDocText )
         {

            appendDirtyJavaScript("      window.open(\"WarrantyNoteTextDlg.page?");
            appendDirtyJavaScript(sRmbUrl);
            appendDirtyJavaScript("\",\"DLGWIN\",\"status,resizable,scrollbars,width=600,height=400,left=200,top=200\");\n");

            isDocText =false;
         }


         out.append(mgr.endPresentation());
         out.append("</form>\n");
         out.append("</body>\n");
         out.append("</html>\n");
         return out;
      }
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      int activetab = tabs.getActiveTab();

      if ( headlay.isVisible() )
         out.append(headlay.show());
      if ( headset.countRows() > 0 )
      {
         if ( headset.countRows()>0 )
         {
            if ( headlay.isSingleLayout() )
            {
               if ( headlay.isVisible() )
               {

                  appendToHTML(tabs.showTabsInit());
                  if ( activetab == 1 )
                  {
                     out.append(itemlay1.show());
                  }
                  if ( activetab == 2 )
                  {
                     out.append(itemlay2.show());
                  }
                  appendToHTML(tabs.showTabsFinish());
               }
               else if ( itemlay1.isEditLayout() || itemlay1.isNewLayout() )
               {
                  out.append(itemlay1.show());

               }
               else if ( itemlay2.isEditLayout() || itemlay2.isNewLayout() )
               {
                  out.append(itemlay2.show());

               }

            }
         }
      }

      beginTable();
      beginTableBody();
      nextTableRow();
      beginTableCell("right");
      if ( headlay.isVisible() && itemlay1.isVisible() )
      {
         printSubmitButton("TEMPLATE",mgr.translate("MPCCOWCUSTOMERWARRANTYDLGTEMPLATEBUTTON:       Template      "),"");
         printSpaces(1);
         printSubmitButton("CANCEL",mgr.translate("MPCCOWCUSTOMERWARRANTYDLGCANCELBUTTON:    Close    "),"");
         printSpaces(1);
         printSubmitButton("OK",mgr.translate("MPCCOWCUSTOMERWARRANTYDLGOKBUTTON:       Ok      "),"");
         printSpaces(8);
      }
      endTableCell();
      endTableBody();
      endTable();
   }

}

