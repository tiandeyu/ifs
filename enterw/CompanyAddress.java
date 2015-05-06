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
 *  File                    : CompanyAddress.java
 *  Description             :
 *  Notes                   :
 * --------------------------------- Wings Merge  Start ------------------------------------
 *  Created    : 2006-11-08   Haunlk   Created.
 *  Modify     : 2007-01-17   Haunlk   Modified Some of the Transation Constants.
 *               2007-01-22   Haunlk   B129741 Added the disableDocMan to disable the RMB option "Document".
 *               2007-01-22   Haunlk   B129737 Modified the enumerateValues at HEAD_COUNTRY and DEFAULT_LANGUAGE.
 *               2007-01-25   Haunlk   Modified the duplicating Translation Constants.
 *               2007-01-31   Haunlk   Merged Wings Code.
 * --------------------------------- Wings Merge End ---------------------------------------
 *               2007-07-20   Shwilk   Done web security corrections.
 *               2010-04-30   Chgulk   Bug 85354, Add validation for default address.
 * ----------------------------------------------------------------------------
*/
package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CompanyAddress extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CompanyAddress");


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

   private ASPBlock        addressblk;
   private ASPRowSet       addressset;
   private ASPCommandBar   addressbar;
   private ASPTable        addresstbl;
   private ASPBlockLayout  addresslay;

   private ASPBlock        addressGeneralblk;
   private ASPRowSet       addressGeneralset;
   private ASPCommandBar   addressGeneralbar;
   private ASPTable        addressGeneraltbl;
   private ASPBlockLayout  addressGenerallay;

   private ASPBlock        addressComMethodblk;
   private ASPRowSet       addressComMethodset;
   private ASPCommandBar   addressComMethodbar;
   private ASPTable        addressComMethodtbl;
   private ASPBlockLayout  addressComMethodlay;

   private ASPBlock        disblk;
   private ASPRowSet       disset;
   private ASPCommandBar   disbar;
   private ASPTable        distbl;
   private ASPBlockLayout  dislay;

   private ASPBlock        taxcodeblk;
   private ASPRowSet       taxcodeset;
   private ASPCommandBar   taxcodebar;
   private ASPTable        taxcodetbl;
   private ASPBlockLayout  taxcodelay;

   private ASPBlock        taxesblk;
   private ASPRowSet       taxesset;
   private ASPCommandBar   taxesbar;
   private ASPTable        taxestbl;
   private ASPBlockLayout  taxeslay;

   private ASPBlock        taxExemptblk;
   private ASPRowSet       taxExemptset;
   private ASPCommandBar   taxExemptbar;
   private ASPTable        taxExempttbl;
   private ASPBlockLayout  taxExemptlay;

   private ASPBlock        ref;
   private ASPRowSet       refset;


   private ASPField ADDRESS1;
   private ASPField ADDRESS2;
   private ASPField ZIP_CODE;
   private ASPField CITY;
   private ASPField COUNTRY;
   private ASPField STATE;
   private ASPField COUNTY;
   private ASPField ADD_COUNTRY;


   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPTabContainer tabs;
   private ASPTabContainer addressTabs;
   private ASPTabContainer taxcodeTabs;
   private ASPQuery qry;
   private int i;
   private int arraySize;

   private String laymode;
   private String firstRequest = "TRUE";
   private String clientValues = "";
   private String sLanguage = "";
   private String val;
   private ASPCommand cmd;
   private String name;
   private double discount;
   private ASPBuffer buf;
   private ASPQuery q;
   private ASPBuffer data;
   private String activetab;
   private String activeAddressTab;
   private String inventInstalled = "FALSE";
   //===============================================================
   // Construction
   //===============================================================
   public CompanyAddress(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {

      ASPManager mgr = getASPManager();
      ctx   = mgr.getASPContext();
      activetab  = ctx.readValue("ACTIVETAB","1");
      activeAddressTab = ctx.readValue("ACTIVEADDRESSTAB","1");
      arraySize = toInt(ctx.readValue("ARRAYSIZE","0"));
      trans = mgr.newASPTransactionBuffer();
      firstRequest = ctx.readValue("FIRSTREQUEST","TRUE");
      clientValues = ctx.readValue("CLIENTVALUES","");
      inventInstalled = ctx.readValue("INVENTINSTALLED","FALSE");
      sLanguage = ctx.readValue("LANGUAGE","");

      if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();

      if ( "TRUE".equals(firstRequest) )
      {
         if ( mgr.isPresentationObjectInstalled("invenw/InventoryPart.page") )
         {
            inventInstalled = "TRUE";
         }

         getClientValues();
      }

      adjust();

      activetab  = ctx.readValue("ACTIVETAB","1");

      tabs.saveActiveTab();
      addressTabs.saveActiveTab();
      taxcodeTabs.saveActiveTab();

      ctx.writeValue("ACTIVETAB",activetab);
      ctx.writeValue("ACTIVEADDRESSTAB",activeAddressTab);
      ctx.writeValue("ARRAYSIZE",Integer.toString(arraySize));
      ctx.writeValue("FIRSTREQUEST",firstRequest);
      ctx.writeValue("CLIENTVALUES",clientValues);
      ctx.writeValue("LANGUAGE",sLanguage);
      ctx.writeValue("INVENTINSTALLED",inventInstalled);

   }

   public void newRowADDRESS()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans1 = null;
      String[] recArray;
      String isDefaultAddress = null;
      String colcomplex = null;
      trans.clear();

      cmd = trans.addEmptyCommand("HEAD","COMPANY_ADDRESS_API.New__",addressblk);
      cmd.setOption("ACTION","PREPARE");

      cmd = trans.addCustomCommand("COLCOMPLEX","Address_Type_Code_API.Enumerate_Type");
      cmd.addParameter("CLIENTVALUE");
      cmd.addParameter("PARTY_TYPE_DB","COMPANY");

      cmd = trans.addCustomFunction("GETCOMPANYDB","COMPANY_API.Get_Country_Db","METHOD");
      cmd.addParameter("COMPANY",headset.getValue("COMPANY"));

      trans = mgr.perform(trans);

      colcomplex =  trans.getValue("COLCOMPLEX/DATA/CLIENTVALUE");
      String companyDB =  trans.getValue("GETCOMPANYDB/DATA/METHOD");

      data = trans.getBuffer("HEAD/DATA");
      data.setFieldItem("ADDRESS_COMPANY",headset.getValue("COMPANY"));
      data.setFieldItem("ADD_GENERAL_COUNTRY",headset.getValue("COUNTRY"));
      data.setFieldItem("COUNTRY",companyDB);
      addressset.addRow(data);

      recArray = split(colcomplex, String.valueOf((char)31));
      arraySize=recArray.length;

      if ( mgr.isEmpty(colcomplex) )
      {
         addressset.next();
         colcomplex = "";
      }
      else
      {
         addressGeneralset.clear();
         trans.clear();

         for ( i = 0; i < arraySize; ++i )
         {
            cmd = trans.addCustomFunction("COLCOMP"+i,"Company_Address_Type_API.Default_Exist","CLIENTVALUE");
            cmd.addParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
            cmd.addParameter("ADD_GENERAL_ADDRESS_ID","");
            cmd.addParameter("ADDGENERALTYPECODE",recArray[i]);
         }

         trans1 = mgr.perform(trans);
         trans.clear();

         for ( i = 0; i < arraySize; ++i )
         {
            isDefaultAddress = trans1.getValue("COLCOMP"+i+"/DATA/CLIENTVALUE");

            cmd = trans.addEmptyCommand("ADDRESSTYPE"+i,"COMPANY_ADDRESS_TYPE_API.New__",addressGeneralblk);
            cmd.setOption("ACTION","PREPARE");
            cmd.setParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
            cmd.setParameter("ADD_GENERAL_ADDRESS_ID", "");
            cmd.setParameter("ADDGENERALTYPECODE",recArray[i]);
            cmd.setParameter("ADD_GENERAL_DEFAULT_DOMAIN","FALSE");

            if ( "FALSE".equals(isDefaultAddress) )
               cmd.setParameter("DEF_ADDRESS","TRUE");
            else
               cmd.setParameter("DEF_ADDRESS","FALSE");
         }

         trans = mgr.perform(trans);
         for ( i = 0; i < arraySize; ++i )
         {
            data = trans.getBuffer("ADDRESSTYPE"+i+"/DATA");
            addressGeneralset.addRow(data);
         }

         trans.clear();
      }


   }
   public void getClientValues()
   {
      ASPManager mgr = getASPManager();
      trans.clear();

      cmd = trans.addCustomCommand("COLCOMPLEX","Address_Type_Code_API.Enumerate_Type");
      cmd.addParameter("CLIENTVALUE");
      cmd.addParameter("PARTY_TYPE_DB","COMPANY");

      cmd = trans.addCustomFunction("GETLANG","LANGUAGE_SYS.Get_Language()","METHOD");

      trans = mgr.perform(trans);
      clientValues =  trans.getValue("COLCOMPLEX/DATA/CLIENTVALUE");
      sLanguage =  trans.getValue("GETLANG/DATA/METHOD");
      firstRequest = "FALSE";
   }

   public void  newRowADDRESSGENERAL()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ADDRESSTYPE","COMPANY_ADDRESS_TYPE_API.New__",addressGeneralblk);     cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
      cmd.setParameter("ADD_GENERAL_ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ADDRESSTYPE/DATA");
      addressGeneralset.addRow(data);
   }

   public void  newRowADDRESSCOMMETHOD()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ADDRESSCOMMETHOD","COMPANY_COMM_METHOD_API.NEW__",addressComMethodblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
      cmd.setParameter("ADD_GENERAL_ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ADDRESSCOMMETHOD/DATA");
      addressComMethodset.addRow(data);
   }

   public void  newRowDISTRIBUTION()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ADDRESSTYPE","COMPANY_ADDRESS_DELIV_INFO_API.New__",addressGeneralblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
      cmd.setParameter("ADD_GENERAL_ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ADDRESSTYPE/DATA");
      disset.addRow(data);
   }

   public void  newRowTAXES()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("TAXESNEW","DELIVERY_FEE_CODE_COMPANY_API.New__",taxesblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
      cmd.setParameter("ADD_GENERAL_ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("TAXESNEW/DATA");
      taxesset.addRow(data);
   }

   public void  newRowTAXEXEMPT()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("TAXEXEMPT","COMPANY_DELIVERY_TAX_EXEMP_API.New__",taxExemptblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
      cmd.setParameter("ADD_GENERAL_ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("TAXEXEMPT/DATA");
      taxExemptset.addRow(data);
   }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

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
         mgr.showAlert(mgr.translate("ENTERWCOMPANYADDRESSNODATA: No data found."));
         headset.clear();
      }
      mgr.createSearchURL(headblk);
      eval(headset.syncItemSets());
   }

   public void  okFindADDRESS()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      trans.clear();
      q = trans.addEmptyQuery(addressblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));

      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
   }

   public void  okFindADDRESSGENERAL()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
      int itemrowno1;

      if ( addressset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addEmptyQuery(addressGeneralblk);

         if ( addresslay.isMultirowLayout() )
         {
            qry.addWhereCondition("COMPANY = ?");
            qry.addParameter("COMPANY", headset.getValue("COMPANY"));
         }
         else
         {
            qry.addWhereCondition("COMPANY = ? and ADDRESS_ID = ?");
            qry.addParameter("COMPANY", headset.getValue("COMPANY"));
            qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
         }
         qry.includeMeta("ALL");
         headrowno = headset.getCurrentRowNo();
         itemrowno = addressset.getCurrentRowNo();
         itemrowno1 = addressComMethodset.getCurrentRowNo();

         mgr.querySubmit(trans,addressGeneralblk);

         headset.goTo(headrowno);
         addressset.goTo(itemrowno);
         addressComMethodset.goTo(itemrowno1);
      }
   }
   public void okFindTAXCODE()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;

      if ( addressset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addEmptyQuery(taxcodeblk);
         qry.addWhereCondition("COMPANY = ? and ADDRESS_ID = ?");
         qry.addParameter("COMPANY", headset.getValue("COMPANY"));
         qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));

         qry.includeMeta("ALL");
         headrowno = headset.getCurrentRowNo();
         itemrowno = addressset.getCurrentRowNo();

         mgr.querySubmit(trans,taxcodeblk);

         headset.goTo(headrowno);
         addressset.goTo(itemrowno);

      }
   }

   public void  okFindADDRESSCOMMETHOD()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
      int itemrowno1;

      if ( addressset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addEmptyQuery(addressComMethodblk);
         if ( addresslay.isMultirowLayout() )
         {
            qry.addWhereCondition("COMPANY = ?");
            qry.addParameter("COMPANY", headset.getValue("COMPANY"));
         }
         else
         {
            if ( addressset.countRows() > 0 )
            {
               qry.addWhereCondition("COMPANY = ? and ADDRESS_ID = ?");
               qry.addParameter("COMPANY", headset.getValue("COMPANY"));
               qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
            }
         }


         qry.includeMeta("ALL");
         headrowno = headset.getCurrentRowNo();
         itemrowno = addressset.getCurrentRowNo();
         itemrowno1 = addressComMethodset.getCurrentRowNo();

         mgr.querySubmit(trans,addressComMethodblk);
         headset.goTo(headrowno);
         addressset.goTo(itemrowno);
         addressComMethodset.goTo(itemrowno1);
      }
   }

   public void  okFindDISTRIBUTION()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;

      if ( addressset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addEmptyQuery(disblk);
         if ( addressset.countRows() > 0 )
         {
            qry.addWhereCondition("COMPANY = ? and ADDRESS_ID = ?");
            qry.addParameter("COMPANY", headset.getValue("COMPANY"));
            qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
            qry.includeMeta("ALL");
         }
         headrowno = headset.getCurrentRowNo();
         itemrowno = addressset.getCurrentRowNo();
         mgr.querySubmit(trans,disblk);
         headset.goTo(headrowno);
         addressset.goTo(itemrowno);

      }
   }


   public void okFindTAXES()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
      int itemrowno1;

      if ( taxcodeset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addEmptyQuery(taxesblk);
         if ( addressset.countRows() > 0 )
         {
            qry.addWhereCondition("COMPANY = ? and ADDRESS_ID = ?");
            qry.addParameter("COMPANY", headset.getValue("COMPANY"));
            qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
            qry.includeMeta("ALL");
         }
         headrowno = headset.getCurrentRowNo();
         itemrowno = addressset.getCurrentRowNo();
         itemrowno1 = taxcodeset.getCurrentRowNo();
         mgr.querySubmit(trans,taxesblk);
         headset.goTo(headrowno);
         addressset.goTo(itemrowno);
         taxcodeset.goTo(itemrowno1);

      }
   }

   public void  okFindTAXEXEMPT()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
      int itemrowno1;

      if ( taxcodeset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addEmptyQuery(taxExemptblk);
         if ( addressset.countRows() > 0 )
         {
            qry.addWhereCondition("COMPANY = ? and ADDRESS_ID = ?");
            qry.addParameter("COMPANY", headset.getValue("COMPANY"));
            qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
            qry.includeMeta("ALL");
         }
         headrowno = headset.getCurrentRowNo();
         itemrowno = addressset.getCurrentRowNo();
         itemrowno1 = taxcodeset.getCurrentRowNo();

         mgr.querySubmit(trans,taxExemptblk);

         headset.goTo(headrowno);
         addressset.goTo(itemrowno);
         taxcodeset.goTo(itemrowno1);
      }
   }

   public void  saveReturnADDRESSGENERAL()throws FndException
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
      int itemrowno1;
      
      //Bug 85354, Begin
      ASPTransactionBuffer trans1 = null;
      String tempaddressvalidation = null;
      String validflag = null;
      //Bug 85354, End
      
      trans.clear();
      addressGeneralset.changeRow();
      //Bug 85354, Begin validate default address. check whether company contain default address.
      if("Modify__".equals(addressGeneralset.getRowStatus()))
      {
         cmd = trans.addCustomCommand("CHECKDEFADDR","Company_Address_Type_API.Check_Def_Address_Exist");
         cmd.addParameter("CLIENTVALUE");
         cmd.addParameter("CLIENTVALUEDEFADDR");
         cmd.addParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
         cmd.addParameter("DEF_ADDRESS", addressGeneralset.getValue("DEF_ADDRESS"));
         cmd.addParameter("ADDGENERALTYPECODE", addressGeneralset.getValue("ADDRESS_TYPE_CODE"));
         cmd.addParameter("ADD_GENERAL_OBJID", addressGeneralset.getValue("OBJID"));

      if ( "FALSE".equals(addressGeneralset.getValue("DEF_ADDRESS")) )
      {
          cmd.addParameter("VALID_FROM"," ");
          cmd.addParameter("VALID_TO"," ");
      }
      else
      {
          cmd.addParameter("VALID_FROM",mgr.readValue("VALID_FROM"));
          cmd.addParameter("VALID_TO", mgr.readValue("VALID_TO"));
      }
      
      trans1 = mgr.perform(trans);
      tempaddressvalidation =  trans1.getValue("CHECKDEFADDR/DATA/CLIENTVALUE");
      validflag = trans1.getValue("CHECKDEFADDR/DATA/CLIENTVALUEDEFADDR");
      
      if("TRUE".equals(addressGeneralset.getValue("DEF_ADDRESS")))
      {
          if("FALSE".equals(tempaddressvalidation))
          {
             ctx.writeValue("tempDefRow",Integer.toString(addressGeneralset.getCurrentRowNo()));
             String message1 = mgr.translate("ENTERWCOMPANYDEFAULTADDRESSEXIST: A default address ID already exists for &1 Address Type(s) for this time period. Do you want to set the current address ID as default instead?",addressGeneralset.getValue("ADDRESS_TYPE_CODE"));
             askUserAndPerform("_Ifs_Applications_Enterw_CookieName_", message1, "HEAD.showWarningOk","HEAD.showWarningCancel");
             return;
          }
      }
      else if("FALSE".equals(addressGeneralset.getValue("DEF_ADDRESS")))
      {
         if("TRUE".equals(validflag))
         {
            String message2 = mgr.translate("ENTERWCOMPANYADEFAULTADDRESSVALIDATE: This is the default &1 Address Type(s) for &2 &3. If removed, there will be no default address for this Address Type(s). Do you want to continue?",addressGeneralset.getValue("ADDRESS_TYPE_CODE"),addressset.getValue("PARTY_TYPE"),addressset.getValue("COMPANY"));
            askUserAndPerform("_Ifs_Applications_Enterw_CookieName_", message2, "HEAD.showWarningRemoveOk","HEAD.showWarningCancel");
            return;
         }
      }
      }
      //Bug 85354, End
      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);

      okFindADDRESSGENERAL();

   }
   
   // Bug 85354, Begin 
   // perform the validation when the row is delete
   public void deleteRowADDRESSGENERAL()throws FndException
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
      int itemrowno1;   
      ASPTransactionBuffer trans1 = null;
      String validflag = null;
      if(addressGenerallay.isMultirowLayout())
      {
         addressGeneralset.goTo(addressGeneralset.getRowSelected());
      }
      else
      {
         addressGeneralset.goTo(addressGeneralset.getCurrentRowNo());
      }
      trans.clear();   
      if("TRUE".equals(addressGeneralset.getValue("DEF_ADDRESS")))
      {
          cmd = trans.addCustomCommand("CHECKDEFADDRDELETE","Company_Address_Type_API.Check_Def_Address_Exist");
          cmd.addParameter("CLIENTVALUE");
          cmd.addParameter("CLIENTVALUEDEFADDR");
          cmd.addParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
          cmd.addParameter("DEF_ADDRESS", addressGeneralset.getValue("DEF_ADDRESS"));
          cmd.addParameter("ADDGENERALTYPECODE", addressGeneralset.getValue("ADDRESS_TYPE_CODE"));
          cmd.addParameter("ADD_GENERAL_OBJID", addressGeneralset.getValue("OBJID"));
          cmd.addParameter("VALID_FROM"," ");
          cmd.addParameter("VALID_TO"," ");
      
          trans1 = mgr.perform(trans);
          validflag =  trans1.getValue("CHECKDEFADDRDELETE/DATA/CLIENTVALUEDEFADDR");
      
          if("TRUE".equals(validflag))
          {
             if(addressGenerallay.isMultirowLayout())
             {
                ctx.writeValue("deleteRoNo",Integer.toString(addressGeneralset.getRowSelected()));
             }
             else
             {
                ctx.writeValue("deleteRoNo",Integer.toString(addressGeneralset.getCurrentRowNo()));
             }
             String message = mgr.translate("ENTERWCOMPANYADEFAULTADDRESSVALIDATE: This is the default &1 Address Type(s) for &2 &3. If removed, there will be no default address for this Address Type(s). Do you want to continue?",addressGeneralset.getValue("ADDRESS_TYPE_CODE"),addressset.getValue("PARTY_TYPE"),addressset.getValue("COMPANY"));
             askUserAndPerform("_Ifs_Applications_Enterw_CookieName_", message, "HEAD.showWarningDeleteOk","HEAD.showWarningCancel");
             return;
           }
        }
     
      addressGeneralset.setRemoved();
      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);

      okFindADDRESSGENERAL();
      
      
   }
   
   public void showWarningDeleteOk()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
      int tempcurrentrowno;
                  
      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      
      tempcurrentrowno = Integer.parseInt(ctx.readValue("deleteRoNo"));
      addressGeneralset.goTo(tempcurrentrowno);
      addressGeneralset.setRemoved();
      
      mgr.submit(trans);
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);
      okFindADDRESSGENERAL();
      
   }
   private void askUserAndPerform(String sCookieName, String sMessage, String sYesAction,String sNoAction) throws FndException
   {
      ctx.setCookie(sCookieName, "*");

      appendDirtyJavaScript("askAndPerformAtClient('", sCookieName, "','");
      appendDirtyJavaScript(sMessage, "','");
      appendDirtyJavaScript(sYesAction, "','");
      appendDirtyJavaScript(sNoAction, "');\n");

   }
   
  // Remove the Default address
   public void showWarningOk()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer transRem = null;
      int headrowno;
      int itemrowno;
                  
      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      
      addressGeneralset.goTo(Integer.parseInt(ctx.readValue("tempDefRow")));
      cmd = trans.addCustomCommand("REMDEFADDR","Company_Address_Type_API.Check_Def_Addr_Temp");
      cmd.addParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
      cmd.addParameter("ADDGENERALTYPECODE", addressGeneralset.getValue("ADDRESS_TYPE_CODE"));
      cmd.addParameter("DEF_ADDRESS", addressGeneralset.getValue("DEF_ADDRESS"));
      cmd.addParameter("ADD_GENERAL_OBJID", addressGeneralset.getValue("OBJID"));
      cmd.addParameter("VALID_FROM", mgr.readValue("VALID_FROM"));
      cmd.addParameter("VALID_TO", mgr.readValue("VALID_TO"));
      
      transRem = mgr.perform(trans);
      transRem.clear();
      
      mgr.submit(trans);
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);
      okFindADDRESSGENERAL();
      
   }

   public void showWarningCancel()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
    
      
      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);
      okFindADDRESSGENERAL();
      
   }
   
   public void showWarningRemoveOk()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
    
      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
    
      mgr.submit(trans);
      
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);
      
      okFindADDRESSGENERAL();
      
   }
   // Bug 85354, End
   public void saveReturnADDRESSCOMMETHOD()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      String strvalidFrom, strvalidUntil, strvalDate = null;
      int curComMethodRow, curAddRow, currHeadRow, intvalDate1 =0;

      addressComMethodset.changeRow();
      currHeadRow = headset.getCurrentRowNo();
      curAddRow = addressset.getCurrentRowNo();
      curComMethodRow = addressComMethodset.getCurrentRowNo();
      strvalidFrom  = mgr.formatDate("VALID_FROM", addressComMethodset.getDateValue("VALID_FROM"));
      strvalidUntil = mgr.formatDate("VALID_TO", addressComMethodset.getDateValue("VALID_TO"));


      if ( !mgr.isEmpty(strvalidFrom) && !mgr.isEmpty(strvalidUntil) )
      {
         qry = trans.addQuery("VALDT","DUAL","(to_date(?,'yyyy-mm-dd') - to_date(?,'yyyy-mm-dd')) VALIDDATE","","");
         qry.addParameter("VALID_TO", strvalidUntil);
         qry.addParameter("VALID_FROM", strvalidFrom);
         trans = mgr.perform(trans);
         strvalDate = trans.getValue("VALDT/DATA/VALIDDATE");
         trans.clear();
      }

      if ( strvalDate != null )
         intvalDate1 = toInt(strvalDate);
      else
         intvalDate1 = 0;

      if ( intvalDate1 < 0 )
      {
         mgr.showError(mgr.translate("ENTERWCOMPANYADDRESSDATECHK: Date of 'Valid To' should not be less than Date of 'Valid From'!"));
      }

      mgr.submit(trans);
      addressComMethodtbl.clearQueryRow();
      headset.goTo(currHeadRow);
      addressset.goTo(curAddRow);
      addressComMethodset.goTo(curComMethodRow);
   }


   public void  saveReturnADDRESS()
   {
      ASPManager mgr = getASPManager();
      String colcomplex1 = null;
      ASPTransactionBuffer trans1 = null;
      int headrowno;
      int itemrowno;

      addressset.changeRow();
      addressGeneralset.changeRows();
      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      
      if ( "New__".equals(addressset.getRowStatus()) )
      {
         addressGeneralset.first();
         for ( i = 0; i < arraySize; ++i )
         {
            cmd = trans.addCustomFunction("COLCOMP"+i,"Company_Address_Type_API.Default_Exist","CLIENTVALUE");
            cmd.addParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
            cmd.addParameter("ADD_GENERAL_ADDRESS_ID", mgr.readValue("ADD_GENERAL_ADDRESS_ID"));
            cmd.addParameter("ADDGENERALTYPECODE", addressGeneralset.getValue("ADDRESS_TYPE_CODE"));
            addressGeneralset.next();
         }

         trans1 = mgr.perform(trans);
         trans1.clear();

         addressGeneralset.first();

         for ( int i=0;i<arraySize;++i )
         {
            data = addressGeneralset.getRow();
            colcomplex1 =  trans1.getValue("COLCOMP"+i+"/DATA/CLIENTVALUE");
            data.setFieldItem("ADD_GENERAL_ADDRESS_ID",addressset.getValue("ADDRESS_ID"));

            if ( "FALSE".equals(colcomplex1) )
               data.setValue("DEF_ADDRESS","TRUE");
            else
               data.setValue("DEF_ADDRESS","FALSE");

            addressGeneralset.setRow(data);
            addressGeneralset.next();
         }
      }

      mgr.submit(trans);
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);
      okFindADDRESSGENERAL();
      okFindTAXCODE();
      addresslay.setLayoutMode(addresslay.SINGLE_LAYOUT);

   }

   public void  saveReturnDISTRIBUTION()
   {
      ASPManager mgr = getASPManager();
      String colcomplex1 = null;
      ASPTransactionBuffer trans1 = null;
      int headrowno;
      int itemrowno;

      trans.clear();
      disset.changeRow();
      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);

      okFindDISTRIBUTION();

      dislay.setLayoutMode(dislay.SINGLE_LAYOUT);
   }

   public void  saveReturnTAXES()
   {
      ASPManager mgr = getASPManager();
      String colcomplex1 = null;
      ASPTransactionBuffer trans1 = null;
      int headrowno;
      int itemrowno;
      int itemrowno1;

      trans.clear();
      taxesset.changeRow();

      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      itemrowno1 = taxcodeset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);
      taxcodeset.goTo(itemrowno1);
      okFindTAXES();
      taxeslay.setLayoutMode(taxeslay.MULTIROW_LAYOUT);

   }

   public void  saveReturnTAXEXEMPT()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
      int itemrowno1;
      trans.clear();
      taxExemptset.changeRow();

      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      itemrowno1 = taxcodeset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);
      taxcodeset.goTo(itemrowno1);
      okFindTAXES();

      taxeslay.setLayoutMode(taxeslay.MULTIROW_LAYOUT);
      taxExemptlay.setLayoutMode(taxExemptlay.MULTIROW_LAYOUT);
   }


   public void saveNewADDRESS()
   {
      ASPManager mgr = getASPManager();
      String colcomplex1 = null;
      ASPTransactionBuffer trans1 = null;
      int headrowno;
      int itemrowno;
      addressset.changeRow();
      addressGeneralset.changeRows();
      headrowno = headset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();

      if ( "New__".equals(addressset.getRowStatus()) )
      {
         addressGeneralset.first();
         for ( i = 0; i < arraySize; ++i )
         {
            cmd = trans.addCustomFunction("COLCOMP"+i,"Company_Address_Type_API.Default_Exist","CLIENTVALUE");
            cmd.addParameter("ADDRESS_COMPANY", headset.getValue("COMPANY"));
            cmd.addParameter("ADD_GENERAL_ADDRESS_ID", mgr.readValue("ADD_GENERAL_ADDRESS_ID"));
            cmd.addParameter("ADDGENERALTYPECODE", addressGeneralset.getValue("ADDRESS_TYPE_CODE"));
            addressGeneralset.next();
         }

         trans1 = mgr.perform(trans);
         trans1.clear();

         addressGeneralset.first();

         for ( int i=0;i<arraySize;++i )
         {
            data = addressGeneralset.getRow();
            colcomplex1 =  trans1.getValue("COLCOMP"+i+"/DATA/CLIENTVALUE");
            data.setFieldItem("ADD_GENERAL_ADDRESS_ID",addressset.getValue("ADDRESS_ID"));

            if ( "FALSE".equals(colcomplex1) )
               data.setValue("DEF_ADDRESS","TRUE");
            else
               data.setValue("DEF_ADDRESS","FALSE");

            addressGeneralset.setRow(data);
            addressGeneralset.next();
         }
      }
      mgr.submit(trans);
      headset.goTo(headrowno);
      addressset.goTo(itemrowno);
      newRowADDRESS();
   }

   // to convert a string to the server date formate.
   public String formatToClientDate(String inDate)
   {
      ASPManager mgr = getASPManager();
      ASPBuffer dateBuff = mgr.newASPBuffer();
      dateBuff.setValue("TEMP_DATE",inDate);
      refset.addRow(dateBuff);
      return refset.getClientValue("TEMP_DATE");
   }

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String val;
      String txt;
      ASPQuery qry;
      trans.clear();

      val = mgr.readValue("VALIDATE");
      if ( "DIS_SHIP_VIA_CODE".equals(val) )
      {
         String sShipCode = mgr.readValue("DIS_SHIP_VIA_CODE");
         qry = trans.addQuery("MPCCOMDESC","SELECT Mpccom_Ship_Via_Desc_API.Get_Description(LANGUAGE_SYS.Get_Language() ,?) GETDESC FROM DUAL ");
	 qry.addParameter("DIS_SHIP_VIA_CODE",sShipCode);
         trans = mgr.perform(trans);
         String sDesc = trans.getValue("MPCCOMDESC/DATA/GETDESC");
         txt = sShipCode+"^"+sDesc+"^";
         mgr.responseWrite(txt);

      }
      if ( "DIS_DELIVERY_TERMS".equals(val) )
      {
         String sDeliveryTerm = mgr.readValue("DIS_DELIVERY_TERMS");
         qry = trans.addQuery("ORDERDESC","select Order_Delivery_Term_Desc_API.Get_Description(LANGUAGE_SYS.Get_Language() ,?) GETORDERDESC FROM DUAL ");
	 qry.addParameter("DIS_DELIVERY_TERMS",sDeliveryTerm);
         trans = mgr.perform(trans);
         String sOrderDesc = trans.getValue("ORDERDESC/DATA/GETORDERDESC");
         txt =sDeliveryTerm+"^"+sOrderDesc+"^";
         mgr.responseWrite(txt);

      }
      if ( "FEE_CODE".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETDES", "STATUTORY_FEE_API.Get_Description", "STATUTORYFEEDESCRIPTION" );
         cmd.addParameter("TAXES_COMPANY");
         cmd.addParameter("FEE_CODE");

         cmd = trans.addCustomFunction("GETPER", "STATUTORY_FEE_API.Get_Percentage", "STATUTORYFEEPERCENTAGE" );
         cmd.addParameter("TAXES_COMPANY");
         cmd.addParameter("FEE_CODE");

         cmd = trans.addCustomFunction("GETFROM", "STATUTORY_FEE_API.Get_Valid_From", "TAXES_VALID_FROM" );
         cmd.addParameter("TAXES_COMPANY");
         cmd.addParameter("FEE_CODE");

         cmd = trans.addCustomFunction("GETTO", "STATUTORY_FEE_API.Get_Valid_Until", "TAXES_VALID_TO" );
         cmd.addParameter("TAXES_COMPANY");
         cmd.addParameter("FEE_CODE");

         trans = mgr.validate(trans);

         String description = trans.getValue("GETDES/DATA/STATUTORYFEEDESCRIPTION");
         String percentage = trans.getValue("GETPER/DATA/STATUTORYFEEPERCENTAGE");
         // to convert the sEffDate to the server date formate.
         String from = formatToClientDate(trans.getValue("GETFROM/DATA/TAXES_VALID_FROM"));
         String to = formatToClientDate(trans.getValue("GETTO/DATA/TAXES_VALID_TO"));
         refset.clear();

         trans.clear();

         if ( percentage == null )
            percentage = "";

         if ( description == null )
            description = "";

         mgr.responseWrite(description + "^" + toDouble(percentage)  + "^" + from + "^" + to + "^");
      }
      mgr.endResponse();

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

      if ( addressset.countRows() == 0 )
      {
         addressbar.disableCommand(addressbar.BACK);
         addressbar.disableCommand(addressbar.FORWARD);
         addressbar.disableCommand(addressbar.BACKWARD);
         addressbar.disableCommand(addressbar.DELETE);
         addressbar.disableCommand(addressbar.EDITROW);
         addressbar.disableCommand(addressbar.DUPLICATEROW);
      }

      if ( addressComMethodset.countRows() == 0 )
      {
         addressComMethodbar.disableCommand(addressComMethodbar.BACK);
         addressComMethodbar.disableCommand(addressComMethodbar.FORWARD);
         addressComMethodbar.disableCommand(addressComMethodbar.BACKWARD);
         addressComMethodbar.disableCommand(addressComMethodbar.DELETE);
         addressComMethodbar.disableCommand(addressComMethodbar.EDITROW);
         addressComMethodbar.disableCommand(addressComMethodbar.DUPLICATEROW);
      }
      if ( mgr.isModuleInstalled("INVENT") )
      {
         if ( disset.countRows() == 0 )
         {
            disbar.disableCommand(disbar.BACK);
            disbar.disableCommand(disbar.FORWARD);
            disbar.disableCommand(disbar.BACKWARD);
            disbar.disableCommand(disbar.DELETE);
            disbar.disableCommand(disbar.EDITROW);
            disbar.disableCommand(disbar.DUPLICATEROW);
         }
         mgr.getASPField("DIS_SHIP_VIA_CODE").setLOVProperty("WHERE","language_code = '"+sLanguage+"'");
         mgr.getASPField("DIS_DELIVERY_TERMS").setLOVProperty("WHERE","language_code = '"+sLanguage+"'");

         if ( disset.countRows() > 0 )
         {
            disbar.disableCommand(disbar.NEWROW);
         }

      }

      if ( addressGeneralset.countRows() == 0 )
      {
         addressGeneralbar.disableCommand(addressGeneralbar.BACK);
         addressGeneralbar.disableCommand(addressGeneralbar.FORWARD);
         addressGeneralbar.disableCommand(addressGeneralbar.BACKWARD);
         addressGeneralbar.disableCommand(addressGeneralbar.DELETE);
         addressGeneralbar.disableCommand(addressGeneralbar.EDITROW);
         addressGeneralbar.disableCommand(addressGeneralbar.DUPLICATEROW);
      }

      if ( taxcodeset.countRows() == 0 )
      {
         taxcodebar.disableCommand(taxcodebar.BACK);
         taxcodebar.disableCommand(taxcodebar.FORWARD);
         taxcodebar.disableCommand(taxcodebar.BACKWARD);
         taxcodebar.disableCommand(taxcodebar.DELETE);
         // taxcodebar.disableCommand(taxcodebar.EDITROW);
         taxcodebar.disableCommand(taxcodebar.DUPLICATEROW);
      }

      if ( taxesset.countRows() == 0 )
      {
         taxesbar.disableCommand(taxesbar.BACK);
         taxesbar.disableCommand(taxesbar.FORWARD);
         taxesbar.disableCommand(taxesbar.BACKWARD);
         taxesbar.disableCommand(taxesbar.DELETE);
         taxesbar.disableCommand(taxesbar.EDITROW);
         taxesbar.disableCommand(taxesbar.DUPLICATEROW);
      }

      if ( taxExemptset.countRows() == 0 )
      {
         taxExemptbar.disableCommand(taxExemptbar.BACK);
         taxExemptbar.disableCommand(taxExemptbar.FORWARD);
         taxExemptbar.disableCommand(taxExemptbar.BACKWARD);
         taxExemptbar.disableCommand(taxExemptbar.DELETE);
         taxExemptbar.disableCommand(taxExemptbar.EDITROW);
         taxExemptbar.disableCommand(taxExemptbar.DUPLICATEROW);
      }


      // Remove tab commands from actions
      headbar.removeCustomCommand("activateAddress");
      addressbar.removeCustomCommand("activateGeneralAddress");
      addressbar.removeCustomCommand("activateDisTab");
      addressbar.removeCustomCommand("activateTaxTab");
      taxcodebar.removeCustomCommand("activateTaxes");
      taxcodebar.removeCustomCommand("activateTaxExemption");



   }

   public void activateAddress()
   {
      tabs.setActiveTab(1);
      activetab = "1";
   }

   public void activateGeneralAddress()
   {
      activeAddressTab = "1";
      addressTabs.setActiveTab(1);
   }

   public void activateDisTab()
   {
      activeAddressTab = "2";
      addressTabs.setActiveTab(2);
   }
   public void activateTaxTab()
   {
      activeAddressTab = "3";
      addressTabs.setActiveTab(3);
   }

   public void activateTaxes()
   {
      activeAddressTab = "1";
      taxcodeTabs.setActiveTab(1);
   }

   public void activateTaxExemption()
   {
      activeAddressTab = "2";
      taxcodeTabs.setActiveTab(2);
   }

   public String tabsInit()
   {
      return(tabs.showTabsInit());
   }

   public String tabsFinish()
   {
      return(tabs.showTabsFinish());
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
      setLabel("ENTERWCOMPANYADDRESSCOMPANY: Identity").
      setReadOnly();

      headblk.addField("PARTY_TYPE_DB").
      setHidden();

      headblk.addField("NAME").
      setLabel("ENTERWCOMPANYADDRESSNAME: Name").
      setSize(40);

      headblk.addField("ASSOCIATION_NO").
      setLabel("ENTERWCOMPANYADDRESSASSOCIATIONNO: Association No").
      setSize(20).
      setDynamicLOV("ASSOCIATION_INFO","PARTY_TYPE_DB");

      headblk.addField("CREATED_BY").
      setLabel("ENTERWCOMPANYADDRESSCREATEDBY: Created By").
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
      setLabel("ENTERWCOMPANYADDRESSDEFAULTLANGUAGE: Default Language").
      setSelectBox().
      enumerateValues("ISO_LANGUAGE_API");

      headblk.addField("HEAD_COUNTRY").
      setSize(20).setDbName("COUNTRY").
      setMandatory().
      setLabel("ENTERWCOMPANYADDRESSHEADCOUNTRY: Country").
      setSelectBox().
      enumerateValues("ISO_COUNTRY_API");


      headblk.setView("COMPANY");
      headblk.disableDocMan();
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(ASPCommandBar.DELETE);
      headbar.defineCommand(headbar.COUNTFIND,"countFind");
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      // Bug 85354, Begin
      headbar.addCustomCommand("showWarningOk","");
      headbar.disableCommand("showWarningOk");
      headbar.addCustomCommand("showWarningCancel","");
      headbar.disableCommand("showWarningCancel");
      headbar.addCustomCommand("showWarningRemoveOk","");
      headbar.disableCommand("showWarningRemoveOk");
      headbar.addCustomCommand("showWarningDeleteOk","");
      headbar.disableCommand("showWarningDeleteOk");
      // Bug 85354, End

      headtbl = mgr.newASPTable( headblk );
      headtbl.setTitle(mgr.translate("ENTERWCOMPANYADDRESSTITLE: Company Address"));

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);

      headlay.setDialogColumns(4);

      //------ Commands for tabs ----------------------
      headbar.addCustomCommand("activateAddress",mgr.translate("ENTERWCOMPANYADDRESSACTIVEADDRESS: Address"));


      // -------------------------- Address Tab  --------------------------------------

      addressblk = mgr.newASPBlock("ADDRESS");

      addressblk.addField("ADDRESS_OBJID").
      setHidden().
      setDbName("OBJID");

      addressblk.addField("ADDRESS_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      addressblk.addField("ADDRESS_ID").
      setLabel("ENTERWCOMPANYADDRESSTABADDRESSID: Address Identity").
      setSize(15).
      setUpperCase();
      //setReadOnly();

      addressblk.addField("EAN_LOCATION").
      setLabel("ENTERWCOMPANYADDRESSTABEANLOCATION: Company's Own Address Id").
      setSize(15);

      // ------- Hidden fields


      addressblk.addField("ADDRESS_COMPANY").
      setHidden().
      setDbName("COMPANY");

      addressblk.addField("ADDRESS_PARTY_TYPE").
      setHidden().
      setDbName("PARTY_TYPE");

      addressblk.addField("ADDRESS_DEFAULT_DOMAIN").
      setHidden().
      setDbName("DEFAULT_DOMAIN");

      addressblk.addField("ADD_GENERAL_COUNTRY").
      setSize(20).
      setMandatory().
      setDbName("COUNTRY").
      setLabel("ENTERWCOMPANYADDRESSADDGENCOUNTRY: Country").
      setSelectBox().
      enumerateValues("ISO_COUNTRY_API.Enumerate"," ");

      ADDRESS1  =    addressblk.addField("ADDRESS1").
                     setSize(29).
                     setMandatory().
                     setLabel("ENTERWCOMPANYADDRESSADDRESS1: Address1");

      ADDRESS2  =    addressblk.addField("ADDRESS2").
                     setSize(25).
                     setLabel("ENTERWCOMPANYADDRESSADDRESS2: Address2");

      ZIP_CODE  =    addressblk.addField("ZIP_CODE").
                     setSize(29).
                     setLabel("ENTERWCOMPANYADDRESSZIPCODE: Zip Code");

      CITY      =    addressblk.addField("CITY").
                     setSize(25).
                     setLabel("ENTERWCOMPANYADDRESSCITY: City").
                     setDynamicLOV("CITY_CODE1_LOV","COUNTRY,STATE,COUNTY");

      COUNTY    =    addressblk.addField("COUNTY").
                     setSize(25).
                     setLabel("ENTERWCOMPANYADDRESSCOUNTY: County").
                     setDynamicLOV("COUNTY_CODE1_LOV","COUNTRY,STATE");


      STATE     =    addressblk.addField("STATE").
                     setSize(29).
                     setLabel("ENTERWCOMPANYADDRESSSTATE: State").
                     setDynamicLOV("STATE_CODE_LOV","COUNTRY");

      ADD_COUNTRY   =addressblk.addField("ADDRESS_COUNTRY").
                     setSize(25).
                     setMandatory().
                     setLabel("ENTERWCOMPANYADDRESSADDRESSCOUNTRY: Country").
                     //setFunction("COUNTRY");
                     setFunction("''");


      COUNTRY =      addressblk.addField("COUNTRY").
                     setSize(25).
                     setMandatory().
                     setDbName("COUNTRY_DB").
                     setDynamicLOV("ISO_COUNTRY").
                     setLabel("ENTERWCOMPANYADDRESSADDRESSCOUNTRYCODE: Country Code");
      
      // Bug 85354, begin
      addressblk.addField("VALID_FROM","Date").
      setSize(29).
      setLabel("ENTERWCOMPANYADDRESSVALIDFROM: Valid From");

      addressblk.addField("VALID_TO","Date").
      setSize(25).
      setLabel("ENTERWCOMPANYADDRESSVALIDTO: Valid To");
      
      addressblk.addField("CLIENTVALUEDEFADDR").
      setFunction("''").
      setHidden();

      // Bug 85354, End

      addressblk.addField("CLIENTVALUE").
      setFunction("''").
      setHidden();

      addressblk.addField("METHOD").
      setFunction("''").
      setHidden();


      addressblk.setView("COMPANY_ADDRESS");
      addressblk.defineCommand("COMPANY_ADDRESS_API","New__,Modify__,Remove__");

      addressblk.setMasterBlock(headblk);
      addressblk.disableDocMan();

      addressset = addressblk.getASPRowSet();

      addressbar = mgr.newASPCommandBar(addressblk);
      addresstbl = mgr.newASPTable(addressblk);

      addresslay = addressblk.getASPBlockLayout();
      addresslay.setAddressFieldList(ADDRESS1,ADDRESS2,CITY,STATE,ZIP_CODE,COUNTY,COUNTRY,ADD_COUNTRY,"ENTERWCOMPANYADDRESSADD: Address",null);

      addresslay.setDefaultLayoutMode(addresslay.SINGLE_LAYOUT);
      addressbar.addCustomCommand("activateGeneralAddress",mgr.translate("ENTERWCOMPANYADDRESSGENERALADDRESSTAB: General Address Info"));
      addressbar.addCustomCommand("activateDisTab",mgr.translate("ENTERWCOMPANYADDRESSDISTRIBUTIONDATA: Distribution Data"));
      addressbar.addCustomCommand("activateTaxTab",mgr.translate("ENTERWCOMPANYADDRESSTAXDATA: Tax"));
      addressbar.defineCommand(addressbar.SAVERETURN,"saveReturnADDRESS");
      addressbar.defineCommand(addressbar.NEWROW,"newRowADDRESS");
      addressbar.defineCommand(addressbar.SAVENEW,"saveNewADDRESS");

      //----------------------- Address General Tab --------------------------

      addressGeneralblk = mgr.newASPBlock("ADDRESSGENERAL");

      addressGeneralblk.addField("ADD_GENERAL_OBJID").
      setHidden().
      setDbName("OBJID");

      addressGeneralblk.addField("ADD_GENERAL_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      addressGeneralblk.addField("ADD_GENERAL_COMPANY").
      setHidden().
      setDbName("COMPANY");

      addressGeneralblk.addField("ADD_GENERAL_ADDRESS_ID").
      setHidden().
      setDbName("ADDRESS_ID");

      addressGeneralblk.addField("ADDGENERALTYPECODE").
      setMandatory().
      setLabel("ENTERWCOMPANYADDRESSADDRESSTYPECODE: Address Type").
      setSelectBox().
      enumerateValues("ADDRESS_TYPE_CODE_API.Enumerate","").
      unsetSearchOnDbColumn().
      setDbName("ADDRESS_TYPE_CODE").
      setSize(20);

      addressGeneralblk.addField("ADD_GENERAL_PARTY").
      setDbName("PARTY").
      setHidden();

      addressGeneralblk.addField("DEF_ADDRESS").
      setLabel("ENTERWCOMPANYADDRESSDEFADDRESS: Default").
      setCheckBox("FALSE,TRUE").
      setMandatory();

      addressGeneralblk.addField("ADD_GENERAL_DEFAULT_DOMAIN").
      setDbName("DEFAULT_DOMAIN").
      setHidden();
      
      // Bug 85354, begin
      appendJavaScript("function askAndPerformAtClient(sCookieName,sMsg,sYesAction,sNoAction)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var sCookieValue = readCookie(sCookieName);\n");
      appendJavaScript("   removeCookie(sCookieName,APP_PATH);\n");
      appendJavaScript("   if ( sCookieValue != '' )\n");
      appendJavaScript("      if (confirm(sMsg))\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (sYesAction != '')\n");
      appendJavaScript("            commandSet(sYesAction, '');\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (sNoAction != '')\n");
      appendJavaScript("            commandSet(sNoAction, '');\n");
      appendJavaScript("      }\n");
      appendJavaScript("}\n");
      // Bug 85354, end

      addressGeneralblk.setView("COMPANY_ADDRESS_TYPE");
      addressGeneralblk.defineCommand("COMPANY_ADDRESS_TYPE_API","New__,Modify__,Remove__");
      addressGeneralblk.setMasterBlock(addressblk);
      addressGeneralblk.disableDocMan();
      addressGeneralset = addressGeneralblk.getASPRowSet();
      addressGeneralbar = mgr.newASPCommandBar(addressGeneralblk);
      addressGeneralbar.defineCommand(addressGeneralbar.SAVERETURN,"saveReturnADDRESSGENERAL");
      addressGeneralbar.defineCommand(addressGeneralbar.OKFIND,"okFindADDRESSGENERAL");
      addressGeneralbar.defineCommand(addressGeneralbar.NEWROW,"newRowADDRESSGENERAL");
      // Bug 85354, Begin, Define the command
      addressGeneralbar.defineCommand(addressGeneralbar.DELETE,"deleteRowADDRESSGENERAL");
      // Bug 85354, End
      addressGeneraltbl = mgr.newASPTable(addressGeneralblk);
      addressGenerallay = addressGeneralblk.getASPBlockLayout();
      addressGenerallay.setDefaultLayoutMode(addressGenerallay.MULTIROW_LAYOUT);

      //---------------------- Address Comm Method Block --------------------------------

      addressComMethodblk = mgr.newASPBlock("ADDRESSCOMMETHOD");

      addressComMethodblk.addField("ADD_COMM_OBJID").
      setHidden().
      setDbName("OBJID");

      addressComMethodblk.addField("ADD_COMM_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      addressComMethodblk.addField("ADD_COMM_COMPANY").
      setHidden().
      setDbName("COMPANY");

      addressComMethodblk.addField("ADD_COMM_ID").
      setHidden().
      setDbName("COMM_ID");

      addressComMethodblk.addField("ADD_COMM_NAME").
      setLabel("ENTERWCOMPANYADDRESSCOMMETHODNAME: Name").
      setDbName("NAME");

      addressComMethodblk.addField("ADD_COMM_DESCRIPTION").
      setLabel("ENTERWCOMPANYADDRESSCOMMETHODDESCRIPTION: Description").
      setDbName("DESCRIPTION");

      addressComMethodblk.addField("ADD_COMM_METHOD_ID").
      setSelectBox().
      enumerateValues("COMM_METHOD_CODE_API.Enumerate","").
      setLabel("ENTERWCOMPANYADDRESSCOMMETHODID: Comm Method").
      setDbName("METHOD_ID").
      setMandatory();

      addressComMethodblk.addField("ADD_COMM_VALUE").
      setLabel("ENTERWCOMPANYADDRESSCOMMETHODVALUE: Value").
      setDbName("VALUE").
      setMandatory();

      addressComMethodblk.addField("ADD_COMM_ADDRESS_DEFAULT").
      setLabel("ENTERWCOMPANYADDRESSCOMMETHODADDRESSDEF: Address Def").
      setCheckBox("FALSE,TRUE").
      setDbName("ADDRESS_DEFAULT");

      addressComMethodblk.addField("ADD_COMM_METHOD_DEFAULT").
      setLabel("ENTERWCOMPANYADDRESSCOMMETHODMETHODDEF: Method Def").
      setCheckBox("FALSE,TRUE").
      setDbName("METHOD_DEFAULT");

      addressComMethodblk.addField("ADD_COMM_ADDRESS_ID").
      setHidden().
      setDbName("ADDRESS_ID");

      addressComMethodblk.addField("ADD_COMM_VALID_FROM","Date").
      setLabel("ENTERWCOMPANYADDRESSCOMMETHODVALIDFROM: Valid From").
      setDbName("VALID_FROM");

      addressComMethodblk.addField("ADD_COMM_VALID_TO","Date").
      setLabel("ENTERWCOMPANYADDRESSCOMMETHODVALIDTO: Valid To").
      setDbName("VALID_TO");


      addressComMethodblk.setView("COMPANY_COMM_METHOD");
      addressComMethodblk.defineCommand("COMPANY_COMM_METHOD_API","New__,Modify__,Remove__");
      addressComMethodblk.setMasterBlock(addressblk);
      addressComMethodblk.disableDocMan();
      addressComMethodset = addressComMethodblk.getASPRowSet();
      addressComMethodbar = mgr.newASPCommandBar(addressComMethodblk);
      addressComMethodbar.defineCommand(addressComMethodbar.OKFIND,"okFindADDRESSCOMMETHOD");
      addressComMethodbar.defineCommand(addressComMethodbar.NEWROW,"newRowADDRESSCOMMETHOD");
      addressComMethodtbl = mgr.newASPTable(addressComMethodblk);
      addressComMethodlay = addressComMethodblk.getASPBlockLayout();
      addressComMethodlay.setDefaultLayoutMode(addressComMethodlay.MULTIROW_LAYOUT);

      //----------------------- Distribution Data Block -----------------------
      if ( mgr.isModuleInstalled("INVENT") )
      {

         disblk = mgr.newASPBlock("DISTRIBUTION");

         disblk.addField("DIS_OBJID").
         setHidden().
         setDbName("OBJID");

         disblk.addField("DIS_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

         disblk.addField("DIS_COMPANY").
         setHidden().
         setDbName("COMPANY");

         disblk.addField("DIS_ADDRESS_ID").
         setHidden().
         setDbName("ADDRESS_ID");

         disblk.addField("DIS_ADDRESS_NAME").
         setLabel("ENTERWCOMPANYDISTRIBUTIONADDRESSNAME: Address Name").
         setDbName("ADDRESS_NAME").
         setSize(30);

         disblk.addField("DIS_CONTACT").
         setLabel("ENTERWCOMPANYDISTRIBUTIONCONTACT: Reference").
         setDynamicLOV("COMPANY_CONTACT_LOV","COMPANY,ADDRESS_ID").
         setDbName("CONTACT").
         setSize(30);

         disblk.addField("DIS_LANG").
         setFunction("LANGUAGE_SYS.Get_Language()").
         setHidden();

         disblk.addField("DIS_DELIVERY_TERMS").
         setLabel("ENTERWCOMPANYDISTRIBUTIONDELIVERYTERMS: Delivery Terms").
         setDynamicLOV("ORDER_DELIVERY_TERM_DESC","DIS_LANG").
         setCustomValidation("DIS_DELIVERY_TERMS","DIS_DELIVERY_TERMS,DIS_DELIVERY_TERMS_DESC").
         setDbName("DELIVERY_TERMS").
         setSize(30);

         disblk.addField("DIS_DELIVERY_TERMS_DESC").
         setFunction("Order_Delivery_Term_Desc_API.Get_Description(LANGUAGE_SYS.Get_Language(),:DIS_DELIVERY_TERMS)").
         setSize(30).
         setReadOnly();

         disblk.addField("DIS_SHIP_VIA_CODE").
         setLabel("ENTERWCOMPANYDISTRIBUTIONDSHIPVIACODE: Ship Via").
         setDynamicLOV("MPCCOM_SHIP_VIA_DESC","DIS_LANG").
         setDbName("SHIP_VIA_CODE").
         setCustomValidation("DIS_SHIP_VIA_CODE","DIS_SHIP_VIA_CODE,DIS_SHIP_VIA_CODE_DES").
         setSize(15);

         disblk.addField("DIS_SHIP_VIA_CODE_DES").
         setFunction("Mpccom_Ship_Via_Desc_API.Get_Description(LANGUAGE_SYS.Get_Language(),:DIS_SHIP_VIA_CODE)").
         setSize(30).
         setReadOnly();

         disblk.addField("DIS_INTRASTAT_EXEMPT_DB").
         setDbName("INTRASTAT_EXEMPT_DB").
         setCheckBox("INCLUDE,EXEMPT").
         setLabel("ENTERWCOMPANYDISTRIBUTIONDEXEMPTDB: Intrastat Exemption").
         setSize(30);


         disblk.setView("COMPANY_ADDRESS_DELIV_INFO");
         disblk.defineCommand("COMPANY_ADDRESS_DELIV_INFO_API","New__,Modify__,Remove__");

         disblk.setMasterBlock(addressblk);
         disblk.disableDocMan();

         disset = disblk.getASPRowSet();

         disbar = mgr.newASPCommandBar(disblk);

         disbar.defineCommand(disbar.NEWROW,"newRowDISTRIBUTION");
         disbar.defineCommand(disbar.OKFIND,"okFindDISTRIBUTION");
         disbar.defineCommand(disbar.SAVERETURN,"saveReturnDISTRIBUTION");
         disbar.disableCommand(disbar.SAVENEW);
         distbl = mgr.newASPTable(disblk);

         dislay = disblk.getASPBlockLayout();
         dislay.setDefaultLayoutMode(dislay.SINGLE_LAYOUT);
         dislay.defineGroup("Main","DIS_ADDRESS_NAME,DIS_CONTACT",false,true);
         dislay.defineGroup(mgr.translate("ENTERWCOMPANYDISTRIBUTIONDATAGRP: Internal Inventory Movements Info"),"DIS_DELIVERY_TERMS,DIS_DELIVERY_TERMS_DESC,DIS_SHIP_VIA_CODE,DIS_SHIP_VIA_CODE_DES,DIS_INTRASTAT_EXEMPT_DB",true,false);
         dislay.setSimple("DIS_DELIVERY_TERMS_DESC");
         dislay.setSimple("DIS_SHIP_VIA_CODE_DES");
         dislay.setDataSpan("DIS_DELIVERY_TERMS",2);
         dislay.setDataSpan("DIS_SHIP_VIA_CODE",2);
      }


      // ------------------------ Tax Code tab --------------------------------

      taxcodeblk = mgr.newASPBlock("TAXCODE");

      taxcodeblk.addField("TAX_OBJID").
      setHidden().
      setDbName("OBJID").
      setSize(20);

      taxcodeblk.addField("TAX_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION").
      setSize(20);

      taxcodeblk.addField("TAX_COMPANY").
      setHidden().
      setDbName("COMPANY").
      setSize(20);

      taxcodeblk.addField("TAX_ADDRESS_ID").
      setHidden().
      setDbName("ADDRESS_ID").
      setSize(20);

      taxcodeblk.addField("TAX_PAY_TAX").
      setLabel("ENTERWCOMPANYTAXDELIVERYADDRESS: Delivery Address Taxable").
      setDbName("PAY_TAX").
      setCheckBox("FALSE,TRUE").
      setSize(5);

      taxcodeblk.setView("COMPANY_ADDRESS");
      taxcodeblk.defineCommand("COMPANY_ADDRESS_API","Modify__");


      taxcodeblk.setMasterBlock(addressblk);
      taxcodeblk.disableDocMan();
      taxcodeset = taxcodeblk.getASPRowSet();
      taxcodebar = mgr.newASPCommandBar(taxcodeblk);
      taxcodebar.defineCommand(taxcodebar.OKFIND,"okFindTAXCODE");
      taxcodebar.addCustomCommand("activateTaxes",mgr.translate("ENTERWCOMPANYADDRESSTAXESDATA: Taxes"));
      taxcodebar.addCustomCommand("activateTaxExemption",mgr.translate("ENTERWCOMPANYADDRESSTAXESCODEDATA: Tax Code Exemption"));

      taxcodetbl = mgr.newASPTable(taxcodeblk);

      taxcodelay = taxcodeblk.getASPBlockLayout();
      taxcodelay.setDefaultLayoutMode(taxcodelay.SINGLE_LAYOUT);
      taxcodelay.setDialogColumns(1);

      // --------------------- TAXES block -------------------------------------------

      taxesblk = mgr.newASPBlock("TAXES");

      taxesblk.addField("TAXES_OBJID").
      setDbName("OBJID").
      setHidden();

      taxesblk.addField("TAXES_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      taxesblk.addField("TAXES_COMPANY").
      setHidden().
      setDbName("COMPANY");

      taxesblk.addField("TAXES_ADDRESS_ID").
      setHidden().
      setDbName("ADDRESS_ID");

      taxesblk.addField("FEE_CODE").
      setSize(10).
      setDynamicLOV("STATUTORY_FEE","TAXES_COMPANY COMPANY",650,450).
      setMandatory().
      setLabel("ENTERWCOMPANYADDRESSFEECODE: Tax Code").
      setUpperCase().
      setReadOnly().
      setCustomValidation("TAXES_COMPANY,FEE_CODE","STATUTORYFEEDESCRIPTION,STATUTORYFEEPERCENTAGE,TAXES_VALID_FROM,TAXES_VALID_TO").
      setInsertable();

      taxesblk.addField("STATUTORYFEEDESCRIPTION").
      setSize(60).
      setLabel("ENTERWCOMPANYADDRESSSTATUTORYFEEDESCRIPTION: Description").
      setFunction("STATUTORY_FEE_API.Get_Description(:TAXES_COMPANY,:FEE_CODE)").
      setReadOnly();

      taxesblk.addField("STATUTORYFEEPERCENTAGE","Number","#.##").
      setSize(17).
      setLabel("ENTERWCOMPANYADDRESSSTATUTORYFEEPERCENTAGE: Percentage").
      setFunction("STATUTORY_FEE_API.Get_Percentage(:TAXES_COMPANY,:FEE_CODE)").
      setReadOnly().
      setAlignment("RIGHT");

      taxesblk.addField("TAXES_VALID_FROM","Date").
      setSize(60).
      setLabel("ENTERWCOMPANYADDRESSSTAXESVALIDFROM: Valid From").
      setFunction("STATUTORY_FEE_API.Get_Valid_From(:TAXES_COMPANY,:FEE_CODE)").
      setReadOnly();

      taxesblk.addField("TAXES_VALID_TO","Date").
      setSize(60).
      setLabel("ENTERWCOMPANYADDRESSSTABTAXESVALIDTO: Valid To").
      setFunction("STATUTORY_FEE_API.Get_Valid_Until(:TAXES_COMPANY,:FEE_CODE)").
      setReadOnly();

      taxesblk.setView("DELIVERY_FEE_CODE_COMPANY");
      taxesblk.defineCommand("DELIVERY_FEE_CODE_COMPANY_API","New__,Modify__,Remove__");

      taxesblk.setMasterBlock(addressblk);
      taxesblk.disableDocMan();

      taxesset = taxesblk.getASPRowSet();

      taxesbar = mgr.newASPCommandBar(taxesblk);
      taxesbar.disableCommand(taxesbar.DUPLICATEROW);
      taxesbar.disableCommand(taxesbar.EDITROW);
      taxesbar.defineCommand(taxesbar.OKFIND,"okFindTAXES");
      taxesbar.disableCommand(taxesbar.COUNTFIND);
      taxesbar.defineCommand(taxesbar.NEWROW,"newRowTAXES");
      taxesbar.defineCommand(taxesbar.SAVERETURN,"saveReturnTAXES");

      taxestbl = mgr.newASPTable(taxesblk);
      taxestbl.setTitle(mgr.translate("ENTERWCUSTOMERADDRESSTAXESD: Taxes"));

      taxeslay = taxesblk.getASPBlockLayout();
      taxeslay.setDefaultLayoutMode(taxeslay.MULTIROW_LAYOUT);
      taxeslay.unsetAutoLayoutSelect();


      //====================================================================================
      //  THIS BLOCK REFFERS TO TAX CODE EXEMPTION BLOCK
      //====================================================================================

      taxExemptblk = mgr.newASPBlock("TAXEXEMPT");

      taxExemptblk.addField("TAXEXEMPT_OBJID").
      setDbName("OBJID").
      setHidden();

      taxExemptblk.addField("TAXEXEMPT_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      taxExemptblk.addField("TAXEXEMPT_COMPANY").
      setDbName("COMPANY").
      setMandatory().
      setHidden();

      taxExemptblk.addField("TAXEXEMPT_ADDRESS_ID").
      setHidden().
      setDbName("ADDRESS_ID");


      taxExemptblk.addField("TAX_EXEMPTION_CERT_NO").
      setUpperCase().
      setSize(25).
      setLabel("ENTERWCOMPANYRADDRESSEXEMPTIONCERTNO: Tax Exemption Certification No.").
      setMandatory();

      taxExemptblk.addField("EXEMPT_CERTIFICATE_TYPE").
      setSize(25).
      setLabel("ENTERWCOMPANYADDRESSCERTIFICATETYPE: Exempt Certificate Type").
      setSelectBox().
      enumerateValues("EXEMPT_CERTIFICATE_TYPE_API.Enumerate"," ");


      taxExemptblk.addField("EXEMPT_ISSUE_DATE","Date").
      setSize(25).
      setLabel("ENTERWCOMPANYADDRESSISSUEDATE: Expiration Date");

      taxExemptblk.addField("EXEMPT_EXPIRY_DATE","Date").
      setLabel("ENTERWCOMPANYRADDRESSEXPIRYDATE: Certification Date").
      setSize(25);

      taxExemptblk.addField("CERTIFICATE_LOCATION").
      setSize(25).
      setLabel("ENTERWCOMPANYADDRESSJURISDICTION: Certificate Jurisdiction").
      setMandatory();

      taxExemptblk.setView("COMPANY_DELIVERY_TAX_EXEMP");
      taxExemptblk.defineCommand("COMPANY_DELIVERY_TAX_EXEMP_API","New__,Modify__,Remove__");
      taxExemptblk.setMasterBlock(addressblk);
      taxExemptblk.disableDocMan();
      taxExemptset = taxExemptblk.getASPRowSet();
      taxExemptbar = mgr.newASPCommandBar(taxExemptblk);
      taxExemptbar.disableCommand(taxExemptbar.DUPLICATEROW);
      taxExemptbar.defineCommand(taxExemptbar.OKFIND,"okFindTAXEXEMPT");
      taxExemptbar.disableCommand(taxExemptbar.COUNTFIND);
      taxExemptbar.defineCommand(taxExemptbar.NEWROW,"newRowTAXEXEMPT");
      taxExemptbar.defineCommand(taxExemptbar.SAVERETURN,"saveReturnTAXEXEMPT");
      taxExempttbl = mgr.newASPTable(taxExemptblk);
      taxExempttbl.setTitle(mgr.translate("ENTERWCUSTOMERADDRESSTAXEXEMPD: Tax Code Exemption"));
      taxExemptlay = taxExemptblk.getASPBlockLayout();
      taxExemptlay.setDefaultLayoutMode(taxExemptlay.MULTIROW_LAYOUT);
      taxExemptlay.unsetAutoLayoutSelect();

      //----------- Temp Block (used to get the client date format)-------------------------------------

      ref = mgr.newASPBlock("REF");
      ref.addField( "TEMP_DATE", "Date" ,"MM/dd/yyyy" );

      refset = ref.getASPRowSet();

      // ---------------------------------------------------------------------

      // ----------------------------------------------------------------------
      //                         Tabs
      // ----------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("COMPANYADDRESSADDRESSTAB: Address"),"javascript:commandSet('HEAD.activateAddress','')");


      addressTabs = newASPTabContainer("ADDTESSTAB");
      addressTabs.addTab("COMPANYADDRESSTABGENERALADDRESS: General Address Info","javascript:commandSet('ADDRESS.activateGeneralAddress','')");
      if ( mgr.isModuleInstalled("INVENT") )
      {
         addressTabs.addTab("COMPANYADDRESSTABDISTAB: Distribution Data","javascript:commandSet('ADDRESS.activateDisTab','')");
      }
      addressTabs.addTab("COMPANYADDRESSTABTAXTAB: Tax","javascript:commandSet('ADDRESS.activateTaxTab','')");


      taxcodeTabs = newASPTabContainer("TAXESTAB");
      taxcodeTabs.addTab("COMPANYADDRESSTABTEXCODE: Taxes","javascript:commandSet('TAXCODE.activateTaxes','')");
      taxcodeTabs.addTab("COMPANYADDRESSTABTEXCODEEXEMPTION: Tax Code Exemption","javascript:commandSet('TAXCODE.activateTaxExemption','')");


   }



//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWCOMPANYADDRESSDESCRIPTION: Company Address";
   }

   protected String getTitle()
   {
      return "ENTERWCOMPANYADDRESSTITLEDES: Company Address";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      int activetab = tabs.getActiveTab();
      int activeAddressTab = addressTabs.getActiveTab();
      int activeTaxcodeTab = taxcodeTabs.getActiveTab();
      if ( headlay.isVisible() )
         appendToHTML(headlay.show());

      if ( headset.countRows()>0 )
      {
         if ( headlay.isSingleLayout() )
         {
            if ( headlay.isVisible() )
            {
               appendToHTML(tabs.showTabsInit());
            }

            if ( addresslay.isSingleLayout() && addressset.countRows() > 0 )
            {
               if ( "FALSE".equals(inventInstalled) )
               {
                  addressTabs.setTabEnabled(2,false);
               }
               if ( activetab == 1 && activeAddressTab == 1 )
               {
                  if ( addresslay.isVisible() )
                  {
                     appendToHTML(addresslay.show());
                     appendToHTML(tabs.showTabsFinish());
                     appendToHTML(addressTabs.showTabsInit());
                     appendToHTML(addressGenerallay.show());
                     appendToHTML(addressTabs.showTabsFinish());
                     appendToHTML(addressComMethodlay.show());
                  }
                  else
                  {
                     if ( addressGenerallay.isEditLayout() || addressGenerallay.isNewLayout() )
                     {
                        appendToHTML(addressGenerallay.show());
                        // this is to remove the unwanted list values from the ADDGENERALTYPECODE select box
                        appendDirtyJavaScript("  var length = document.form.ADDGENERALTYPECODE.length;\n");
                        appendDirtyJavaScript("  for (var z = 0; z < document.form.ADDGENERALTYPECODE.length; z++)\n");
                        appendDirtyJavaScript("  {\n");
                        appendDirtyJavaScript("  var text = document.form.ADDGENERALTYPECODE.options[z].value;\n");
                        appendDirtyJavaScript("  var clientVal='");
                        appendDirtyJavaScript(mgr.encodeStringForJavascript(clientValues));
                        appendDirtyJavaScript("'; \n");
                        appendDirtyJavaScript("  if ( clientVal.search(text) == -1) {\n");
                        appendDirtyJavaScript(" document.form.ADDGENERALTYPECODE.remove(z); \n");
                        appendDirtyJavaScript("  z = 0 ;\n");
                        appendDirtyJavaScript("  }\n");
                        appendDirtyJavaScript("  }\n");
                     }
                     else if ( addressComMethodlay.isEditLayout() || addressComMethodlay.isNewLayout() )
                     {
                        appendToHTML(addressComMethodlay.show());
                     }

                  }
               }
               else if ( activetab == 1 && activeAddressTab == 2 )
               {
                  if ( addresslay.isVisible() )
                  {
                     appendToHTML(addresslay.show());
                     appendToHTML(tabs.showTabsFinish());
                     appendToHTML(addressTabs.showTabsInit());
                     if ( mgr.isModuleInstalled("INVENT") )
                     {
                        appendToHTML(dislay.show());
                     }
                     appendToHTML(addressTabs.showTabsFinish());
                  }
                  else
                  {
                     if ( mgr.isModuleInstalled("INVENT") )
                     {
                        appendToHTML(dislay.show());
                     }
                  }
               }
               else if ( activetab == 1 && activeAddressTab ==3 )
               {
                  if ( addresslay.isVisible() )
                  {
                     appendToHTML(addresslay.show());
                     appendToHTML(tabs.showTabsFinish());
                     appendToHTML(addressTabs.showTabsInit());
                     appendToHTML(taxcodelay.show());
                     appendToHTML(addressTabs.showTabsFinish());
                  }
                  else if ( taxcodelay.isEditLayout() || taxcodelay.isNewLayout() )
                  {
                     appendToHTML(taxcodelay.show());
                  }

                  if ( activeTaxcodeTab == 1 )
                  {
                     if ( addresslay.isVisible() )
                     {
                        appendToHTML(taxcodeTabs.showTabsInit());
                        appendToHTML(taxeslay.show());
                        appendToHTML(taxcodeTabs.showTabsFinish());
                     }
                     else if ( taxeslay.isEditLayout() || taxeslay.isNewLayout() )
                     {
                        appendToHTML(taxeslay.show());
                     }
                  }
                  else if ( activeTaxcodeTab == 2 )
                  {
                     if ( addresslay.isVisible() )
                     {
                        appendToHTML(taxcodeTabs.showTabsInit());
                        appendToHTML(taxExemptlay.show());
                        appendToHTML(taxcodeTabs.showTabsFinish());
                     }
                     else if ( taxExemptlay.isEditLayout() || taxExemptlay.isNewLayout() )
                     {
                        appendToHTML(taxExemptlay.show());
                     }
                  }
               }
            }
            else
            {
               appendToHTML(addresslay.show());
               appendToHTML(tabs.showTabsFinish());

            }
         }
      }

   }

}
