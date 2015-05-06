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
 *  File                    : CreateCompanyWizard.java
 *  Description             :
 *  Notes                   :
 *  Created    : 2006-01-08   RuRaLK   Created.
 *               2007-01-24   Haunlk   B129738, Made the TEMPLATE_COMPANY and USE_VO_NO_PERIOD to readOnly at the 5th step,
 *                                     Made PARCUR and ACCCUR fields to upperCase.
 *               2007-01-25   Haunlk   Modified the duplicating Translation Constants.
 *               2007-05-30   Haunlk   B143310, Modified the getLangIds and finish().
 *               2007-07-20   Shwilk   Done web security corrections.
 *               2007-07-30   Thpelk   Fixed localization errors in tranlation constants.
 *               2007-08-23   Maselk   Modified printContents().
 * ----------------------------------------------------------------------------
*/
package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreateCompanyWizard extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CreateCompanyWizard");


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

   private ASPBlock wizblk0;
   private ASPRowSet wizset0;
   private ASPCommandBar wizbar0;
   private ASPTable wiztbl0;
   private ASPBlockLayout wizlay0;

   private ASPBlock wizblk2;
   private ASPRowSet wizset2;
   private ASPCommandBar wizbar2;
   private ASPTable wiztbl2;
   private ASPBlockLayout wizlay2;

   private ASPBlock wizblk4;
   private ASPRowSet wizset4;
   private ASPCommandBar wizbar4;
   private ASPTable wiztbl4;
   private ASPBlockLayout wizlay4;

   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;
   private ASPBlockLayout itemlay0;

   private ASPBlock itemblk1;
   private ASPRowSet itemset1;
   private ASPCommandBar itembar1;
   private ASPTable itemtbl1;
   private ASPBlockLayout itemlay1;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery qry;
   private int i;
   private int arraySize;

   private String val;
   private ASPCommand cmd;
   private ASPBuffer buf;
   private ASPQuery q;
   private ASPBuffer data;

   private ASPBuffer wiz0buff;
   private ASPBuffer wiz2buff;
   private ASPBuffer wiz4buff;
   private ASPBuffer langBuff;

   private int pageWiz;
   private boolean bCreateTemp;
   private boolean bFinish = false;
   private boolean bFinishErr = false;
   private String sTempId;
   private String sTempName;
   private String sCreateTemp;
   private String sFinishAlert;

   //===============================================================
   // Construction
   //===============================================================
   public CreateCompanyWizard(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {

      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();
      pageWiz            = Integer.parseInt(ctx.readValue("PAGENO","0"));
      sCreateTemp        = ctx.readValue("CREATE_TEMP","");
      sTempId            = ctx.readValue("TEMPID","");
      sTempName          = ctx.readValue("TEMPNAME","");


      wiz0buff           = ctx.readBuffer("WIZ0BUFF");
      wiz2buff           = ctx.readBuffer("WIZ2BUFF");
      wiz4buff           = ctx.readBuffer("WIZ4BUFF");
      langBuff           = ctx.readBuffer("LANGBUFF");
      bCreateTemp = ctx.readFlag("BCRETEMP",false);

      trans = mgr.newASPTransactionBuffer();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.buttonPressed("NEXT"))
         next();
      else if (mgr.buttonPressed("BACK"))
         back();
      else if (mgr.buttonPressed("FINISH"))
         finish();
      else if (mgr.buttonPressed("COMP_TYPE"))
         radio();

      adjust();

      ctx.writeValue("PAGENO", String.valueOf(pageWiz));
      ctx.writeValue("CREATE_TEMP",sCreateTemp);
      ctx.writeValue("TEMPID",sTempId);
      ctx.writeValue("TEMPNAME",sTempName);

      ctx.writeFlag("BCRETEMP",bCreateTemp);
      if (wiz0buff != null)
          ctx.writeBuffer("WIZ0BUFF",wiz0buff);
      if (wiz2buff != null)
          ctx.writeBuffer("WIZ2BUFF",wiz2buff);
      if (wiz4buff != null)
          ctx.writeBuffer("WIZ4BUFF",wiz4buff);
      if (langBuff != null)
          ctx.writeBuffer("LANGBUFF",langBuff);

   }

   public void  next()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;
      ASPTransactionBuffer trans= mgr.newASPTransactionBuffer();

      if (pageWiz == 0)
      {

         wizset0.changeRow();
         wiz0buff = wizset0.getRow();

         if (mgr.isEmpty(wiz0buff.getValue("COMPANY")) || mgr.isEmpty(wiz0buff.getValue("NAME")))
         {
             mgr.showAlert("ENTERWCREATECOMPANYWIZARDVALIDATE1: Please enter data for all fields.");
         }
         else
         {
             cmd = trans.addCustomFunction("CREFINISHED","Company_Finance_API.Get_Creation_Finished","DUMMY");
             cmd.addParameter("COMPANY",wiz0buff.getValue("COMPANY"));

             trans = mgr.perform(trans);

             if ("TRUE".equals(trans.getValue("CREFINISHED/DATA/DUMMY")))
                 mgr.showAlert("ENTERWCREATECOMPANYWIZARDCOMPALREADYCREATED: This Company is already created use Update Company instead.");
             else
             {
                 pageWiz = 1;
                 wizset0.clear();
             }
         }
      }
      else if (pageWiz == 1)
      {
          pageWiz = 2;
          if (wiz0buff.getValue("COMPANY").length() > 2)
          {
              cmd = trans.addCustomCommand("CONSCHECK","CREATE_COMPANY_API.CONSISTENCY_CHECK");
              cmd.addParameter("EXEC_ID","0");
              cmd.addParameter("COMPANY",wiz0buff.getValue("COMPANY"));

              trans = mgr.perform(trans);
          }
          if (bCreateTemp)
              mgr.getASPField("FROM_TEMPLATE_ID").setReadOnly();
          else
              mgr.getASPField("FROM_COMPANY").setReadOnly();


      }
      else if (pageWiz == 2)
      {
          wizset2.changeRow();
          wiz2buff = wizset2.getRow();

          if (mgr.isEmpty(wiz2buff.getValue("FROM_TEMPLATE_ID")) && mgr.isEmpty(wiz2buff.getValue("FROM_COMPANY")))
          {
              mgr.showAlert("ENTERWCREATECOMPANYWIZARDVALIDATE2: Please enter data for all fields.");
          }
          else
          {

              if (!(mgr.isEmpty(wiz2buff.getValue("FROM_COMPANY"))))
              {
                  cmd = trans.addCustomCommand("COMPEXIST","Company_API.Exist");
                  cmd.addParameter("FROM_COMPANY",wiz2buff.getValue("FROM_COMPANY"));

                  mgr.perform(trans);
              }
              else
              {
                  cmd = trans.addCustomCommand("TEMPEXIST","Create_Company_Tem_API.Exist");
                  cmd.addParameter("FROM_TEMPLATE_ID",wiz2buff.getValue("FROM_TEMPLATE_ID"));
              }

              mgr.perform(trans);

              wizset2.clear();

              sCreateTemp = mgr.readValue("COMP_TYPE");

              if ("1".equals(sCreateTemp))
              {
                  okFindITEM0();
                  itemset1.clear();
                  itemset1.selectRows();
                  itemset0.switchSelections();
                  bCreateTemp = false;
              }
              else
              {
                  okFindITEM1();
                  itemset0.clear();
                  itemset1.selectRows();
                  itemset1.switchSelections();
                  bCreateTemp = true;
              }

              pageWiz = 3;

          }

      }
      else if (pageWiz == 3)
      {
          if ("1".equals(sCreateTemp))
          {
              itemset0.store();
              langBuff = itemset0.getSelectedRows();
          }
          else
          {
              itemset1.store();
              langBuff = itemset1.getSelectedRows();
          }

          pageWiz = 4;
      }
      else if (pageWiz == 4)
      {
          wizset4.changeRow();
          wiz4buff = wizset4.getRow();
          if (mgr.isEmpty(wiz4buff.getValue("DEFAULT_LANGUAGE")) || mgr.isEmpty(wiz4buff.getValue("COUNTRY")) || mgr.isEmpty(wiz4buff.getValue("ACCCUR")) || mgr.isEmpty(wiz4buff.getValue("ACCCUR_VALID_FROM")) || mgr.isEmpty(wiz4buff.getValue("CORRECTION_TYPE")))
          {
              mgr.showAlert("ENTERWCREATECOMPANYWIZARDVALIDATE3: Please enter data for all fields.");
          }
          else if ( (!(mgr.isEmpty(wiz4buff.getValue("PARCUR")))) && (mgr.isEmpty(wiz4buff.getValue("PARCUR_VALID_FROM"))))
          {
              mgr.showAlert("ENTERWCREATECOMPANYWIZARDVALIDFROMPAR: Valid From Date must be specified for Parallel Accounting Currency.");
          }
          else
          {
              cmd = trans.addCustomCommand("ACCCUREXIST","Iso_Currency_API.Exist");
              cmd.addParameter("ACCCUR",wiz4buff.getValue("ACCCUR"));

              if (!(mgr.isEmpty(wiz4buff.getValue("PARCUR"))))
              {
                  cmd = trans.addCustomCommand("PARCUREXIST","Iso_Currency_API.Exist");
                  cmd.addParameter("PARCUR",wiz4buff.getValue("PARCUR"));
              }

              if (!(mgr.isEmpty(wiz2buff.getValue("FROM_COMPANY"))))
              {
                  cmd = trans.addCustomCommand("COMPACCCUREXIST","Currency_Code_API.Get_Currency_Code_Attributes");
                  cmd.addParameter("CUR_CHECK","");
                  cmd.addParameter("CUR_ROUND","");
                  cmd.addParameter("DEC_RATE","");
                  cmd.addParameter("FROM_COMPANY",wiz2buff.getValue("FROM_COMPANY"));
                  cmd.addParameter("ACCCUR",wiz4buff.getValue("ACCCUR"));

                  if (!(mgr.isEmpty(wiz4buff.getValue("PARCUR"))))
                  {
                      cmd = trans.addCustomCommand("COMPPARCUREXIST","Currency_Code_API.Get_Currency_Code_Attributes");
                      cmd.addParameter("CUR_CHECK","");
                      cmd.addParameter("CUR_ROUND","");
                      cmd.addParameter("DEC_RATE","");
                      cmd.addParameter("FROM_COMPANY",wiz2buff.getValue("FROM_COMPANY"));
                      cmd.addParameter("PARCUR",wiz4buff.getValue("PARCUR"));
                  }

                  trans = mgr.perform(trans);

                  if (mgr.isEmpty(trans.getValue("COMPACCCUREXIST/DATA/CUR_CHECK")))
                  {
                      mgr.showAlert(mgr.translate("ENTERWCREATECOMPANYWIZARDINVALIDCURCOMP: Cannot create accounting currency &1. Currency does not exist in source company &2",wiz4buff.getValue("ACCCUR"),wiz2buff.getValue("FROM_COMPANY")));
                  }
                  else if ((!(mgr.isEmpty(wiz4buff.getValue("PARCUR")))) && (mgr.isEmpty(trans.getValue("COMPPARCUREXIST/DATA/CUR_CHECK"))))
                  {
                      mgr.showAlert(mgr.translate("ENTERWCREATECOMPANYWIZARDINVALIDPCURCOMP: Cannot create parallel currency &0. Currency does not exist in source company &1",wiz4buff.getValue("PARCUR"),wiz2buff.getValue("FROM_COMPANY")));
                  }
                  else
                  {
                      pageWiz = 5;
                      wizset4.clear();
                  }

              }
              else
              {
                  cmd = trans.addCustomFunction("TEMPACCCUREXIST","Create_Company_Tem_API.Exist_Curr_Code_In_Template__","CUR_CHECK");
                  cmd.addParameter("FROM_TEMPLATE_ID",wiz2buff.getValue("FROM_TEMPLATE_ID"));
                  cmd.addParameter("ACCCUR",wiz4buff.getValue("ACCCUR"));

                  if (!(mgr.isEmpty(wiz4buff.getValue("PARCUR"))))
                  {
                      cmd = trans.addCustomFunction("TEMPPARCUREXIST","Create_Company_Tem_API.Exist_Curr_Code_In_Template__","CUR_CHECK");
                      cmd.addParameter("FROM_TEMPLATE_ID",wiz2buff.getValue("FROM_TEMPLATE_ID"));
                      cmd.addParameter("PARCUR",wiz4buff.getValue("PARCUR"));
                  }

                  trans = mgr.perform(trans);

                  if (!("TRUE".equals(trans.getValue("TEMPACCCUREXIST/DATA/CUR_CHECK"))))
                  {
                      mgr.showAlert(mgr.translate("ENTERWCREATECOMPANYWIZARDINVALIDCURTEMP: Cannot create accounting currency &1. Currency does not exist in template &2",wiz4buff.getValue("ACCCUR"),wiz2buff.getValue("FROM_TEMPLATE_ID")));
                  }
                  else if ((!(mgr.isEmpty(wiz4buff.getValue("PARCUR")))) && (!("TRUE".equals(trans.getValue("TEMPPARCUREXIST/DATA/CUR_CHECK")))))
                  {
                      mgr.showAlert(mgr.translate("ENTERWCREATECOMPANYWIZARDINVALIDPCURTEMP: Cannot create parallel currency &1. Currency does not exist in template &2 ",wiz4buff.getValue("PARCUR"),wiz2buff.getValue("FROM_TEMPLATE_ID")));
                  }
                  else
                  {
                      pageWiz = 5;
                      wizset4.clear();
                  }

              }

          }

      }

   }

   public void radio()
   {
       ASPManager mgr = getASPManager();

       wizset2.clear();
       wiz2buff.clear();

       sCreateTemp = mgr.readValue("COMP_TYPE");
       if ("1".equals(sCreateTemp))
       {
           bCreateTemp = false;
           mgr.getASPField("FROM_COMPANY").setReadOnly();
           wiz2buff.setValue("FROM_TEMPLATE_ID",sTempId);
           wiz2buff.setValue("TEMPLATE_NAME",sTempName);
       }
       else
       {
           bCreateTemp = true;
           mgr.getASPField("FROM_TEMPLATE_ID").setReadOnly();
       }

   }

   public void  back()
   {
      ASPManager mgr = getASPManager();
      if (pageWiz == 1)
          pageWiz = 0;
      else if (pageWiz == 2)
      {
          wizset2.changeRow();
          wiz2buff = wizset2.getRow();
          wizset2.clear();
          pageWiz = 1;

      }
      else if (pageWiz == 3)
      {
          pageWiz = 2;
      }
      else if (pageWiz == 4)
      {
          wizset4.changeRow();
          wiz4buff = wizset4.getRow();
          wizset4.clear();
          pageWiz = 3;
      }
      else if (pageWiz == 5)
          pageWiz = 4;


   }

   public void finish()
   {
       ASPManager mgr = getASPManager();
       ASPCommand cmd;
       ASPTransactionBuffer trans= mgr.newASPTransactionBuffer();

       String ch31 = String.valueOf((char)31);
       String ch30 = String.valueOf((char)30);
       String sAttrEnt = null;
       String sAttrAcc = null;
       String sAttrCre = null;
       String sAttrMod = null;

       sAttrEnt  = "COMPANY"+ch31+wiz0buff.getValue("COMPANY")+ch30;
       sAttrEnt += "NAME"+ch31+wiz0buff.getValue("NAME")+ch30;
       sAttrEnt += "DEFAULT_LANGUAGE"+ch31+wiz4buff.getValue("DEFAULT_LANGUAGE")+ch30;
       sAttrEnt += "COUNTRY"+ch31+wiz4buff.getValue("COUNTRY")+ch30;
       sAttrEnt += "TEMPLATE_COMPANY"+ch31+wiz0buff.getValue("TEMPLATE_COMPANY")+ch30;
       sAttrEnt += "FROM_COMPANY"+ch31+(mgr.isEmpty(wiz2buff.getValue("FROM_COMPANY"))?"":wiz2buff.getValue("FROM_COMPANY"))+ch30;
       sAttrEnt += "FROM_TEMPLATE_ID"+ch31+(mgr.isEmpty(wiz2buff.getValue("FROM_TEMPLATE_ID"))?"":wiz2buff.getValue("FROM_TEMPLATE_ID"))+ch30;

       sAttrAcc  = "COMPANY"+ch31+wiz0buff.getValue("COMPANY")+ch30;
       sAttrAcc += "VALID_FROM"+ch31+wiz4buff.getValue("ACCCUR_VALID_FROM")+ch30;
       sAttrAcc += "CURRENCY_CODE"+ch31+wiz4buff.getValue("ACCCUR")+ch30;
       sAttrAcc += "CORRECTION_TYPE"+ch31+wiz4buff.getValue("CORRECTION_TYPE")+ch30;
       sAttrAcc += "PARALLEL_ACC_CURRENCY"+ch31+(mgr.isEmpty(wiz4buff.getValue("PARCUR"))?"":wiz4buff.getValue("PARCUR"))+ch30;
       sAttrAcc += "TIME_STAMP"+ch31+(mgr.isEmpty(wiz4buff.getValue("PARCUR_VALID_FROM"))?"":wiz4buff.getValue("PARCUR_VALID_FROM"))+ch30;
       sAttrAcc += "USE_VOU_NO_PERIOD"+ch31+wiz4buff.getValue("USE_VO_NO_PERIOD")+ch30;

       sAttrCre  = "NEW_COMPANY"+ch31+wiz0buff.getValue("COMPANY")+ch30;
       sAttrCre += "PROCESS"+ch31+"ONLINE"+ch30;

       sAttrMod  = "NEW_COMPANY"+ch31+wiz0buff.getValue("COMPANY")+ch30;
       sAttrMod += "VALID_FROM"+ch31+wiz4buff.getValue("ACCCUR_VALID_FROM")+ch30;
       if (bCreateTemp)
           sAttrMod += "ACTION"+ch31+"DUPLICATE"+ch30;
       else
           sAttrMod += "ACTION"+ch31+"NEW"+ch30;
       sAttrMod += "TEMPLATE_ID"+ch31+(mgr.isEmpty(wiz2buff.getValue("FROM_TEMPLATE_ID"))?"":wiz2buff.getValue("FROM_TEMPLATE_ID"))+ch30;
       sAttrMod += "DUPL_COMPANY"+ch31+(mgr.isEmpty(wiz2buff.getValue("FROM_COMPANY"))?"":wiz2buff.getValue("FROM_COMPANY"))+ch30;
       sAttrMod += "MAKE_COMPANY"+ch31+"IMPORT"+ch30;
       sAttrMod += "FROM_WINDOW"+ch31+"CREATE_COMPANY"+ch30;


       cmd = trans.addCustomCommand("NEWCOMPENT","COMPANY_API.NEW_COMPANY");
       cmd.addParameter("ATTR_ENT",sAttrEnt);

       cmd = trans.addCustomCommand("NEWCOMPACC","COMPANY_FINANCE_API.NEW");
       cmd.addParameter("ATTR_ACC",sAttrAcc);

       cmd = trans.addCustomCommand("NEWCOMPMOD","CREATE_COMPANY_API.NEW_COMPANY__");
       cmd.addParameter("ATTR_CRE",sAttrCre);

       trans = mgr.perform(trans);

       String sModuleList = trans.getValue("NEWCOMPMOD/DATA/ATTR_CRE");
       String sModule = "";
       String sPackage = "";
       int n = 0;

       trans.clear();

       while (n >= 0)
       {

          sModule = sModuleList.substring((sModuleList.indexOf("MODULE",n) + 7),(sModuleList.indexOf("PACKAGE",n) - 1));
           n = sModuleList.indexOf("PACKAGE",n) - 1;
           if (sModuleList.indexOf("MODULE",n) <= -1)
               sPackage = sModuleList.substring((sModuleList.indexOf("PACKAGE",n) + 8));
           else
               sPackage = sModuleList.substring((sModuleList.indexOf("PACKAGE",n) + 8),(sModuleList.indexOf("MODULE",n+1) - 1));

           n = sModuleList.indexOf("MODULE",n) - 1;

           cmd = trans.addCustomCommand("NEWCOMP"+sModule,"CREATE_COMPANY_API.CREATE_NEW_COMPANY__");
           cmd.addParameter("ERROR_CREATED","");
           cmd.addParameter("COMPANY",wiz0buff.getValue("COMPANY"));
           cmd.addParameter("MODULE",sModule);
           cmd.addParameter("PACKAGE",sPackage);
           cmd.addParameter("ATTR_MOD",sAttrMod);
           cmd.addParameter("LANGUAGE_CODE",getLangIds());

           if ("ACCRUL".equals(sModule))
           {
               cmd = trans.addCustomCommand("SETCREFINISHED","Company_Finance_API.Set_Creation_Finished");
               cmd.addParameter("COMPANY",wiz0buff.getValue("COMPANY"));
           }

       }

       cmd = trans.addCustomCommand("ADDIMPTAB","CREATE_COMPANY_LOG_API.ADD_TO_IMP_TABLE__");
       cmd.addParameter("COMPANY",wiz0buff.getValue("COMPANY"));

       cmd = trans.addCustomCommand("UPDLOGTAB","CREATE_COMPANY_LOG_API.UPDATE_LOG_TAB_TO_COMMENTS__");
       cmd.addParameter("COMPANY",wiz0buff.getValue("COMPANY"));

       trans = mgr.perform(trans);

       if ("TRUE".equals(trans.getValue("NEWCOMP"+sModule+"/DATA/ERROR_CREATED")))
       {
           sFinishAlert = mgr.translate("ENTERWCREATECOMPANYWIZARDCOMPCREATEDWERR: Company Created with Error, check the Company Log.");
           bFinishErr = false;
       }
       else if (("FALSE".equals(trans.getValue("NEWCOMP"+sModule+"/DATA/ERROR_CREATED"))) || ("COMMENTS".equals(trans.getValue("NEWCOMP"+sModule+"/DATA/ERROR_CREATED"))) )
       {
           bFinish = true;
           sFinishAlert = mgr.translate("ENTERWCREATECOMPANYWIZARDCOMPCREATEDSUCC: Company Successfully Created.");
       }



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
         mgr.showAlert(mgr.translate("No data found."));
         headset.clear();
      }
      mgr.createSearchURL(headblk);
      eval(headset.syncItemSets());
   }

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(itemblk0);
      q.addWhereCondition("KEY_NAME = 'TemplKeyLu' AND key_value =  ?");
      q.addParameter("FROM_TEMPLATE_ID",wiz2buff.getValue("FROM_TEMPLATE_ID"));
      q.setGroupByClause("LANGUAGE_CODE");
      q.setOrderByClause("2");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,itemblk0);

   }

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(itemblk1);
      q.addWhereCondition("KEY_NAME = 'CompanyKeyLu' AND key_value =  ?");
      q.addParameter("FROM_COMPANY",wiz2buff.getValue("FROM_COMPANY"));
      q.setGroupByClause("LANGUAGE_CODE");
      q.setOrderByClause("2");

      q.includeMeta("ALL");

      mgr.querySubmit(trans,itemblk1);

   }

   public String getLanguages()
   {
       String langList = "";
       if (langBuff.countItems()>0)
       {
           for (int i=0; i<langBuff.countItems(); i++)
           {
               langList+=langBuff.getBufferAt(i).getValueAt(1)+";";
           }
       }
       return langList;
   }

   public String getLangIds()
   {
       String langList = "";
       String ch31 = String.valueOf((char)31);
       if (langBuff.countItems()>0)
       {
           for (int i=0; i<langBuff.countItems(); i++)
           {
               langList+=langBuff.getBufferAt(i).getValueAt(0)+ch31;
           }
       }
       return langList;
   }



   public void adjust()
   {
      ASPManager mgr = getASPManager();

      if (pageWiz == 0)
      {
          if (wiz0buff == null)
          {
              wiz0buff = mgr.newASPBuffer();
              wizset0.addRow(wiz0buff);
          }
          else
              wizset0.addRow(wiz0buff);

      }
      else if (pageWiz == 2)
      {
          if (wiz2buff == null)
          {
              wiz2buff = mgr.newASPBuffer();
              if (mgr.isEmpty(sTempId))
              {
                  cmd = trans.addCustomCommand("DEFTEMPID","CREATE_COMPANY_TEM_API.Get_Default_Template__");
                  cmd.addParameter("FROM_TEMPLATE_ID","");

                  cmd = trans.addCustomFunction("DEFTEMPNAME","CREATE_COMPANY_TEM_API.Get_Description","TEMPLATE_NAME");
                  cmd.addReference("FROM_TEMPLATE_ID","DEFTEMPID/DATA");

                  trans = mgr.perform(trans);

                  sTempId = trans.getValue("DEFTEMPID/DATA/FROM_TEMPLATE_ID");
                  sTempName = trans.getValue("DEFTEMPNAME/DATA/TEMPLATE_NAME");
              }

              wiz2buff.setValue("FROM_TEMPLATE_ID",sTempId);
              wiz2buff.setValue("TEMPLATE_NAME",sTempName);

              wizset2.addRow(wiz2buff);
          }
          else
              wizset2.addRow(wiz2buff);
      }
      else if (pageWiz == 3)
      {
          if (langBuff == null)
          {
              langBuff = mgr.newASPBuffer();
          }
      }
      else if (pageWiz == 4)
      {
          if (wiz4buff == null)
          {
              wiz4buff = mgr.newASPBuffer();
              wizset4.addRow(wiz4buff);
          }
          else
              wizset4.addRow(wiz4buff);
      }
   }


   protected void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      disableNavigate();
      disableConfiguration();
      disableHomeIcon();
      mgr.setPageExpiring();

      wizblk0 = mgr.newASPBlock("WIZ0");

      wizblk0.addField("COMPANY").
      setUpperCase().
      setSize(20).
      setDynamicLOV("COMPANY").
      setLabel("ENTERWCREATECOMPANYWIZARDCOMPID: Company Id:").
      setMandatory().
      setValidation("Company_API.Get_Name","COMPANY","NAME");


      wizblk0.addField("NAME").
      setLabel("ENTERWCREATECOMPANYWIZARCOMPNAME: Company Name:").
      setMandatory().
      setSize(20);

      wizblk0.addField("TEMPLATE_COMPANY").
      setLabel("ENTERWCREATECOMPANYWIZARDCREATEASTEMP: Create as Template Company").
      setCheckBox("FALSE,TRUE");

      wizblk0.addField("DUMMY").
      setHidden();

      wizblk0.addField("EXEC_ID").
      setHidden();

      wizblk0.addField("ATTR_ENT").
      setHidden();

      wizblk0.addField("ATTR_ACC").
      setHidden();

      wizblk0.addField("ATTR_CRE").
      setHidden();

      wizblk0.addField("ATTR_MOD").
      setHidden();

      wizblk0.addField("ERROR_CREATED").
      setHidden();

      wizblk0.addField("MODULE").
      setHidden();

      wizblk0.addField("PACKAGE").
      setHidden();

      wizblk0.addField("CUR_CHECK").
      setHidden();

      wizblk0.addField("CUR_ROUND").
      setHidden();

      wizblk0.addField("DEC_RATE").
      setHidden();

      wizblk0.setView("COMPANY");
      wizblk0.defineCommand("COMPANY_API","New__,Modify__");

      wizset0 = wizblk0.getASPRowSet();

      wizbar0 = mgr.newASPCommandBar(wizblk0);

      wiztbl0 = mgr.newASPTable(wizblk0);

      wizlay0 = wizblk0.getASPBlockLayout();
      wizlay0.setDefaultLayoutMode(wizlay0.EDIT_LAYOUT);
      wizlay0.setDialogColumns(1);
      wizlay0.showBottomLine(false);

      wizblk2 = mgr.newASPBlock("WIZ2");

      wizblk2.addField("FROM_COMPANY").
      setUpperCase().
      setSize(20).
      setDynamicLOV("COMPANY").
      setMandatory().
      setLabel("ENTERWCREATECOMPANYWIZARSOCOMPID: Source Company ID:");


      wizblk2.addField("SOURCE_NAME").
      setFunction("Company_API.Get_Name(:FROM_COMPANY)").
      setLabel("ENTERWCREATECOMPANYWIZARCOMPNAME2: Company Name:").
      setSize(20).
      setReadOnly();
      getASPField("FROM_COMPANY").setValidation("SOURCE_NAME");

      wizblk2.addField("FROM_TEMPLATE_ID").
      setLabel("ENTERWCREATECOMPANYWIZARDFROMTEMPLATEID: Template ID").
      setDynamicLOV("CREATE_COMPANY_TEM_VALID").
      setMandatory().
      setSize(20);

      wizblk2.addField("TEMPLATE_NAME").
      setLabel("ENTERWCREATECOMPANYWIZARDTEMPNAME: Template Name:").
      setFunction("Create_Company_Tem_API.Get_Description(:FROM_TEMPLATE_ID)").
      setSize(20).
      setReadOnly();

      mgr.getASPField("FROM_TEMPLATE_ID").setValidation("TEMPLATE_NAME");


      wizblk2.setView("COMPANY");
      wizblk2.defineCommand("COMPANY_API","New__,Modify__");

      wizset2 = wizblk2.getASPRowSet();

      wizbar2 = mgr.newASPCommandBar(wizblk2);


      wiztbl2 = mgr.newASPTable(wizblk2);

      wizlay2 = wizblk2.getASPBlockLayout();
      wizlay2.setDefaultLayoutMode(wizlay2.EDIT_LAYOUT);
      wizlay2.setDialogColumns(1);
      wizlay2.showBottomLine(false);

      itemblk0 = mgr.newASPBlock("ITEM0");
      itemblk0.addField("LANGUAGE_CODE").
      setHidden();

      itemblk0.addField("DESCRIPTION").
      setFunction("Iso_Language_API.Decode(:LANGUAGE_CODE)").
      setLabel("").
      setSize(20);

      itemblk0.setView("TEMPL_KEY_LU_TRANSLATION");

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.enableRowStatus();
      itembar0.setWidth(30);

      itembar0.disableCommand(itembar0.VIEWDETAILS);
      itembar0.disableCommand(itembar0.FIND);
      itembar0.disableCommand(itembar0.FORWARD);
      itembar0.disableCommand(itembar0.BACKWARD);

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.enableRowSelect();
      itemblk0.setTitle("ENTERWCREATECOMPANYWIZARDAVAILLANG: Available Languages");

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);


      itemblk1 = mgr.newASPBlock("ITEM1");

      itemblk1.addField("ITEM1_LANGUAGE_CODE").
      setDbName("LANGUAGE_CODE").
      setHidden();

      itemblk1.addField("ITEM1_DESCRIPTION").
      setFunction("Iso_Language_API.Decode(:LANGUAGE_CODE)").
      setLabel("").
      setSize(20);

      itemblk1.setView("COMPANY_KEY_LU_TRANSLATION");

      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.enableRowStatus();
      itembar1.setWidth(30);

      itembar1.disableCommand(itembar1.VIEWDETAILS);
      itembar1.disableCommand(itembar1.FIND);
      itembar1.disableCommand(itembar1.FORWARD);
      itembar1.disableCommand(itembar1.BACKWARD);

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.enableRowSelect();
      itemblk1.setTitle("ENTERWCREATECOMPANYWIZARDAVAILLANG2: Available Languages");

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);

      wizblk4 = mgr.newASPBlock("WIZ4");

      wizblk4.addField("DEFAULT_LANGUAGE").
      setSize(30).
      setMandatory().
      setLabel("ENTERWCREATECOMPANYWIZARDDEFAULTLANGUAGE: Default Language").
      setSelectBox().
      enumerateValues("ISO_LANGUAGE_API.Enumerate"," ");

      wizblk4.addField("COUNTRY").
      setSize(20).
      setMandatory().
      setLabel("ENTERWCREATECOMPANYWIZARDDEFAULTCOUNTRY: Country").
      setSelectBox().
      enumerateValues("ISO_COUNTRY_API.Enumerate"," ");

      wizblk4.addField("ACCCUR").
      setLabel("ENTERWCREATECOMPANYWIZARDACCCUR: Accounting Currency:").
      setMandatory().
      setUpperCase().
      setDynamicLOV("ISO_CURRENCY").
      setSize(20);

      wizblk4.addField("ACCCUR_VALID_FROM","Date").
      setLabel("ENTERWCREATECOMPANYWIZARDACCCURVALFR: Valid From:").
      setMandatory().
      setSize(20);

      wizblk4.addField("PARCUR").
      setLabel("ENTERWCREATECOMPANYWIZARDPARCUR: Parallel Currency:").
      setDynamicLOV("ISO_CURRENCY").
      setUpperCase().
      setSize(20);

      wizblk4.addField("PARCUR_VALID_FROM","Date").
      setLabel("ENTERWCREATECOMPANYWIZARDPARCURVALFR: Valid From:").
      setSize(20);

      wizblk4.addField("CORRECTION_TYPE").
      setSize(20).
      setMandatory().
      setLabel("ENTERWCREATECOMPANYWIZARDCORRECTIONTYPE: Cancel/Rollback Posting Method:").
      setSelectBox().
      enumerateValues("CORRECTION_TYPE_API.Enumerate"," ");


      wizblk4.addField("USE_VO_NO_PERIOD").
      setLabel("ENTERWCREATECOMPANYWIZARDUSEVONOPER: Use Voucher No Series Per Period").
      setCheckBox("FALSE,TRUE");

      wizblk4.setView("COMPANY");
      wizblk4.defineCommand("COMPANY_API","New__,Modify__");

      wizset4 = wizblk4.getASPRowSet();

      wizbar4 = mgr.newASPCommandBar(wizblk4);


      wiztbl4 = mgr.newASPTable(wizblk4);

      wizlay4 = wizblk4.getASPBlockLayout();
      wizlay4.setDefaultLayoutMode(wizlay4.EDIT_LAYOUT);
      wizlay4.setDialogColumns(1);
      wizlay4.showBottomLine(false);



   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWCREATECOMPANYWIZARDTITLE: Create Company Wizard";

   }

   protected String getTitle()
   {
       if (pageWiz == 0)
          return "ENTERWCREATECOMPANYWIZARDDESCRIPTIONSTEP1: Create Company Wizard - Step 1/6";
       else if (pageWiz == 1)
          return "ENTERWCREATECOMPANYWIZARDDESCRIPTIONSTEP2: Create Company Wizard - Step 2/6";
       else if (pageWiz == 2)
          return "ENTERWCREATECOMPANYWIZARDDESCRIPTIONSTEP3: Create Company Wizard - Step 3/6";
       else if (pageWiz == 3)
          return "ENTERWCREATECOMPANYWIZARDDESCRIPTIONSTEP4: Create Company Wizard - Step 4/6";
       else if (pageWiz == 4)
          return "ENTERWCREATECOMPANYWIZARDDESCRIPTIONSTEP5: Create Company Wizard - Step 5/6";
       else
          return "ENTERWCREATECOMPANYWIZARDDESCRIPTIONSTEP6: Create Company Wizard - Step 6/6";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      // This has to be done to obtain the correct colour in the back ground.
      appendToHTML("<table class = 'BlockLayoutTable' width='100%'> \n <tr> \n\t <td> \n\t\t");
      beginTransparentTable();
      beginTableBody();
      beginTableCell();
      printImage("images/CreateCompany.bmp",130,260);
      endTableCell();
      beginTableCell();
      printSpaces(4);
      endTableCell();


      //beginTableCell();
      //beginTransparentTable();
      //beginTableBody();

      if (pageWiz == 0)
      {
         beginTableCell();
         printWriteLabel("ENTERWCREATECOMPANYWIZARDWLCOME: Welcome to the Create Company Wizard");
         printNewLine();
         printWriteLabel("ENTERWCREATECOMPANYWIZARDINTROMSG: This Wizard will create a new company.");
         printNewLine();
         printWriteLabel("ENTERWCREATECOMPANYWIZARDENTCOMP: 1) Enter Company ID and Company Name.");
         printWriteLabel("ENTERWCREATECOMPANYWIZARDNOVOUCHERS: 2) If it should not be possible to create vouchers in the Company then check Create as Template Company");
         printNewLine();
         beginTable();
         appendDirtyJavaScript("f.TEMPLATE_COMPANY.disabled = false ;\n");
         beginTableTitleRow();
         beginTableCell("LEFT");
         appendToHTML(wizlay0.generateDialog());
         endTableCell();
         beginTableCell();
         printSpaces(60);
         endTableCell();
         endTableTitleRow();
         endTable();
         endTableCell();

      }
      else if (pageWiz == 1)
      {
          appendToHTML("<td>");
          appendToHTML("<font class=normalTextLabel>");
          appendToHTML("<fieldset style='padding: 2'>");
          appendToHTML("<legend>"+mgr.translate("ENTERWCREATECOMPANYWIZARDCHKDBCONS: Check Database Consistency")+"</legend>");
          appendToHTML("<table height=20>");
          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDCHKDBCONSTEXT1: The step will check the consistency of the database to make sure that the supplied company identity can be created in all affected components.");
          printReadLabel("ENTERWCREATECOMPANYWIZARDCHKDBCONSTEXT2: If some tables are found in the database that are considered as non-consistent candidates, then a dialog will be opened that presents these tables as well as associated component, column name and column length.");
          printReadLabel("ENTERWCREATECOMPANYWIZARDCHKDBCONSTEXT3: It is recommended to abort this activity and to contact the system administrator if any tables are found.");
          appendToHTML("</td>");
          appendToHTML("</tr>");
          appendToHTML("</table>");
          appendToHTML("</fieldset>");
          appendToHTML("</font>");
          appendToHTML("</td>");
      }
      else if (pageWiz == 2)
      {
          beginTableCell();
          printWriteLabel("ENTERWCREATECOMPANYWIZARDCHOOSECOMTYPE: Choose if you want the Company to be created from an Existing Company or a Template");
          printNewLine();
          beginTable();
          beginTableTitleRow();
          appendToHTML("<td>");
          appendToHTML("<font class=normalTextLabel>");
          appendToHTML("<fieldset style='padding: 2'>");
          appendToHTML("<legend>"+mgr.translate("ENTERWCREATECOMPANYWIZARDCREATEFROM: Create From")+"</legend>");
          appendToHTML("<table height=20>");
          appendToHTML("<tr>");
          appendToHTML("<td>");
          printRadioButton("","COMP_TYPE", "0", bCreateTemp, "onClick='javascript:document.form.submit()'");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDCREATEFROMEXCOM: Existing Company");
          appendToHTML("</td>");
          appendToHTML("</tr>");
          appendToHTML("<tr>");
          appendToHTML("<td>");
          printRadioButton("","COMP_TYPE", "1", !bCreateTemp,  "onClick='javascript:document.form.submit()'");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDCREATEFROMTEMP: Template");
          appendToHTML("</td>");
          appendToHTML("</tr>");
          appendToHTML("</table>");
          appendToHTML("</fieldset>");
          appendToHTML("</font>");
          appendToHTML("</td>");
          endTableTitleRow();
          beginTableTitleRow();
          beginTableCell();
          printNewLine();
          endTableCell();
          endTableTitleRow();
          endTable();
          beginTable();
          beginTableTitleRow();
          beginTableCell("LEFT");
          appendToHTML(wizlay2.generateDialog());
          endTableCell();
          endTableTitleRow();
          endTable();
          endTableCell();
          beginTableCell();
          printSpaces(60);
          endTableCell();

      }
      else if (pageWiz == 3)
      {
          beginTableCell("LEFT");
          if (itemset0.countRows() > 0)
              appendToHTML(itemlay0.show());
          else
              appendToHTML(itemlay1.show());
          endTableCell();
          beginTableCell("LEFT");
          printWriteLabel("ENTERWCREATECOMPANYWIZARDCOMSELLAN: Company related translations will in the new company be created in selected source languages.");
          printNewLine();
          printWriteLabel("ENTERWCREATECOMPANYWIZARDDEFALLLAN: By default translations will be created in the new company for all available source languages.");
          endTableCell();

      }
      else if (pageWiz == 4)
      {
          appendToHTML("<td>");
          appendToHTML("<font class=normalTextLabel>");
          appendToHTML("<fieldset style='padding: 2'>");
          appendToHTML("<legend>"+mgr.translate("ENTERWCREATECOMPANYWIZARDDETAILINFO: Detail Information")+"</legend>");
          appendToHTML("<table height=20>");
          appendToHTML("<tr>");
          appendToHTML("<td>");
          appendToHTML(wizlay4.generateDialog());
          appendToHTML("</td>");
          appendToHTML("<td>");
          appendToHTML("</td>");
          appendToHTML("</tr>");
          appendToHTML("</table>");
          appendDirtyJavaScript("f.USE_VO_NO_PERIOD.disabled = false ;\n");
          appendToHTML("</fieldset>");
          appendToHTML("</font>");
          appendToHTML("</td>");
      }
      else if (pageWiz == 5)
      {
          appendToHTML("<td>");

          appendToHTML("<font class=normalTextLabel>");
          appendToHTML("<fieldset style='padding: 2'>");
          appendToHTML("<legend>"+mgr.translate("ENTERWCREATECOMPANYWIZARDNEWCOMPANY: New Company")+"</legend>");

          appendToHTML("<table height=20>");

          appendToHTML("<tr>");

          appendToHTML("<td>");

          appendToHTML("<table>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDCOMPID2: Company Id:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("COMPANY5",wiz0buff.getValue("COMPANY"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("NAME5",wiz0buff.getValue("NAME"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");
          appendToHTML("</table>");
          appendToHTML("</td>");

          appendToHTML("</tr>");

          appendToHTML("<tr>");

          appendToHTML("<td>");

          appendToHTML("<table>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDACCCUR2: Accounting Currency:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("ACCCUR5",wiz4buff.getValue("ACCCUR"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDACCCURVALFR2: Valid From:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("ACCCUR_VALID_FROM5",wiz4buff.getValue("ACCCUR_VALID_FROM"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDCORRECTIONTYPE2: Cancel/Rollback Posting Method:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("CORRECTION_TYPE5",wiz4buff.getValue("CORRECTION_TYPE"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDPARCUR2: Parallel Currency:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("PARCUR5",wiz4buff.getValue("PARCUR"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDPARCURVALFR2: Valid From:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("PARCUR_VALID_FROM5",wiz4buff.getValue("PARCUR_VALID_FROM"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDDEFAULTLANGUAGE2: Default Language:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("DEFAULT_LANGUAGE5",wiz4buff.getValue("DEFAULT_LANGUAGE"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDDEFAULTCOUNTRY2: Country:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("COUNTRY5",wiz4buff.getValue("COUNTRY"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDUSEVONOPER2: Use Voucher No Series Per Period");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printCheckBox("USE_VO_NO_PERIOD",wiz4buff.getValue("USE_VO_NO_PERIOD"), "TRUE".equals(wiz4buff.getValue("USE_VO_NO_PERIOD")),"");
          appendToHTML("</td>");
          appendToHTML("</tr>");
          appendDirtyJavaScript("f.USE_VO_NO_PERIOD.disabled = true ;\n");

          appendToHTML("</table>");

          appendToHTML("</td>");

          appendToHTML("<td>");

          appendToHTML("<table>");

          appendToHTML("<tr>");
          appendToHTML("<td>");

          appendToHTML("<table>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDDEFAULTLANGUAGES: Languages:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("LANGUAGES5",getLanguages(),"",25,100,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("</table>");
          appendToHTML("</td>");

          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          appendToHTML("<font class=normalTextLabel>");
          appendToHTML("<fieldset style='padding: 2'>");
          appendToHTML("<legend>"+mgr.translate("ENTERWCREATECOMPANYWIZARDCREATEFROM2: Create From")+"</legend>");
          appendToHTML("<table>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARSOCOMPID2: Source Company ID:");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("FROM_COMPANY5",wiz2buff.getValue("FROM_COMPANY"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDFROMTEMPLATEID2: Template ID");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printReadOnlyField("FROM_TEMPLATE_ID5",wiz2buff.getValue("FROM_TEMPLATE_ID"),"",20,20,false);
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("</table>");
          appendToHTML("</fieldset>");
          appendToHTML("</font>");
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("<tr>");
          appendToHTML("<td>");

          appendToHTML("<table>");
          appendToHTML("<tr>");
          appendToHTML("<td>");
          printReadLabel("ENTERWCREATECOMPANYWIZARDCREATEASTEMP1: Create as Template Company");
          appendToHTML("</td>");
          appendToHTML("<td>");
          printCheckBox("TEMPLATE_COMPANY",wiz0buff.getValue("TEMPLATE_COMPANY"), "TRUE".equals(wiz0buff.getValue("TEMPLATE_COMPANY")),"");
          appendToHTML("<td>");
          appendToHTML("</tr>");
          appendToHTML("</table>");
          appendDirtyJavaScript("f.TEMPLATE_COMPANY.disabled = true ;\n");
          appendToHTML("</td>");
          appendToHTML("</tr>");

          appendToHTML("</table>");
          appendToHTML("</td>");

          appendToHTML("</tr>");

          appendToHTML("</table>");
          appendToHTML("</fieldset>");
          appendToHTML("</font>");
          appendToHTML("</td>");


      }

      endTableBody();
      endTable();
      appendToHTML("\n\t\t </td> \n\t </tr>\n </table> \n ");

      if ((!bFinish) && (!bFinishErr))
      {   

          beginTransparentTable();
          beginTableBody();
          beginTableCell("CENTER");
          printButton("CANCEL", "ENTERWCREATECOMPANYWIZARDCANCEL: Cancel  ","onClick='javascript:window.close()'");
          printSpaces(2);
          printSubmitButton("BACK", "ENTERWCREATECOMPANYWIZARDBACK: Previous","");
          printSubmitButton("NEXT", "ENTERWCREATECOMPANYWIZARDNEXT: Next    ", "");
          printSpaces(2);
          printSubmitButton("FINISH", "ENTERWCREATECOMPANYWIZARDFINISH: Finish  ", "");
          mgr.setInitialFocus("NEXT");
          if (pageWiz == 0)
          {
              appendDirtyJavaScript("      document.form.BACK.disabled = true;\n");
          }
          if (pageWiz != 5)
          {
              appendDirtyJavaScript("      document.form.FINISH.disabled = true;\n");
          }
          if (pageWiz == 5)
          {
              appendDirtyJavaScript("      document.form.NEXT.disabled = true;\n");
          }

          endTableCell();
          endTableBody();
          endTable();
      }
      else
      {
          beginTransparentTable();
          beginTableBody();
          beginTableCell("CENTER");
          printNewLine();
          printReadLabel(sFinishAlert);
          printNewLine();
          printSubmitButton("OK", "ENTERWCREATECOMPANYWIZARDOK: OK  ", "onClick='javascript:window.close()'");
          endTableCell();
          endTableBody();
          endTable();
      }


      appendDirtyJavaScript("function validateCompany(i)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("   if( getRowStatus_('WIZ0',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkCompany(i) ) return;\n");
      appendDirtyJavaScript("   if( getValue_('COMPANY',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("   if( getValue_('COMPANY',i)=='' )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("           getField_('NAME',i).value = '';\n");
      appendDirtyJavaScript("           return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   window.status='Please wait for validation';\n");
      appendDirtyJavaScript("   r = __lookup(\n");
      appendDirtyJavaScript("           APP_ROOT+ 'common/scripts/Lookup.page?',\n");
      appendDirtyJavaScript("           'DUAL',\n");
      appendDirtyJavaScript("            URLClientEncode('Company_API.Get_Name('\n");
      appendDirtyJavaScript("           + \"'\" +getValue_('COMPANY',i) + \"'\"\n");
      appendDirtyJavaScript("           + ') NAME'), '', '');\n");
      appendDirtyJavaScript("   window.status='';\n");
      appendDirtyJavaScript("  \n");
      appendDirtyJavaScript("   if (r!='F')\n");
      appendDirtyJavaScript("           assignValue_('NAME',i,0);\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("           getField_('NAME',i).value = '';\n");
      appendDirtyJavaScript("  }\n");

      if (bFinishErr)
      {
          appendDirtyJavaScript("window.open(\"CreateCompanyLog.page?");
          appendDirtyJavaScript("COMPANY="+ mgr.encodeStringForJavascript(wiz0buff.getValue("COMPANY")));
          appendDirtyJavaScript("\",\"DLGWIN\",\"status,resizable,scrollbars,width=400,height=600,left=200,top=200\");\n");
      }


   }

}
