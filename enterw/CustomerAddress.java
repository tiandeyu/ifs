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
*  File        : CustomerAddress.java 
*  Modified    : Anil Padmajeewa
*  Sign           Date           History
*------------------------------------------------------------------------------
*  anpalk         2001/02/23     Code Review, Rename Blocks.
*  anpalk         2001/03/26     Overwrite SAVERETURN & SAVENEW Funtion in customerInfobar
*                                because Mandatory Field "Language" does not handle as a
*                                Mandatory field in server side.
*  ASP2JAVA Tool  2001-01-22     Created Using the ASP file CustomerAddress.asp
*  Madhu          23-05-2001     Call # 65300
*  anpalk         12/09/2001     Bug Id 68523
*  anpalk         24/09/2001     Bug Id 69505
*  anpalk         25/09/2001     Bug Id 69497
*  Enmalk         27/09/2001     Removed the invoice module dependencies. 
*  Samblk         03/01/2002     IID 10115 : Added the Combo Box to the taxExemptblk. 
*  Kupelk         18/01/2002     IID 10997 . Added LOV view for State column.
*  Anpalk         19/02/2002     IID 10220 : Added ComboBox and field to addressblk.
*  Anpalk         03/07/2002     Call Id 29991
*  raselk         10/12/2002     SP3 - Merge
*  Jakalk         18/11/2002     Salsa-IIDITFI101E. Added new fields to taxCodeblk.
*  DaGalk         17/12/2002     Salsa-IIDESFI109E. Added new fields to taxCodeblk.
*  Jakalk         27/12/2002     SP3 Merge - Merged Bug Id 31428.
*  Gacolk         07/02/2003     Removed deprecated method splitToVector() with split() and did other necessary changes
*  DaGalk         13/02/2003     Takeoff merge and delete commmented unnesasary codes.
*  KuPelk         24/02/2003     IID ARFI124N Added new field as TAX_ID_TYPE.
*  Nimalk         01/11/2003     B108053 - enable/disable NEWROW Button changes in adjust()
*  reanpl         05/10/2004     FIJP344 Japanese Tax Rounding, added tax_rounding_level, tax_rounding_method
*  NaLslk         16/03/2005     FITH352, added tax_withholding and rearrange fields.
*  Jakalk         06/09/2005     Code Cleanup, Removed doReset and clone methods.
*  Maselk         2006-12-27     Merged LCS Bug 58216, Fixed SQL Injection threats.
*  Thpelk         2007-04-06     Merged LCS Bug 64316,Removed data transfered check to enabel redirecting.
*  Chhulk         2007-04-19     Merged LCS Bug 63858,Added tab names as parameter in method newASPTabContainer() to avoid cloning error 
*                                and dissabled properties command in customerInfobar.
*  Chgulk         2010-04-30     Bug 85354, Add validation for default address.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class CustomerAddress extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CustomerAddress");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;
   private ASPLog log;
   private ASPBlock customerInfoblk;
   private ASPRowSet customerInfoset;
   private ASPCommandBar customerInfobar;
   private ASPTable customerInfotbl;
   private ASPBlockLayout customerInfolay;
   private ASPBlock addressblk;
   private ASPRowSet addressset;
   private ASPCommandBar addressbar;
   private ASPTable addresstbl;
   private ASPBlockLayout addresslay;
   private ASPBlock comMethodblk;
   private ASPRowSet comMethodset;
   private ASPCommandBar comMethodbar;
   private ASPTable comMethodtbl;
   private ASPBlockLayout comMethodlay;
   private ASPBlock addressTypeblk;
   private ASPRowSet addressTypeset;
   private ASPCommandBar addressTypebar;
   private ASPTable addressTypetbl;
   private ASPBlockLayout addressTypelay;
   private ASPBlock taxCodeblk;
   private ASPRowSet taxCodeset;
   private ASPCommandBar taxCodebar;
   private ASPTable taxCodetbl;
   private ASPBlockLayout taxCodelay;
   private ASPBlock taxesblk;
   private ASPRowSet taxesset;
   private ASPCommandBar taxesbar;
   private ASPTable taxestbl;
   private ASPBlockLayout taxeslay;
   private ASPBlock taxExemptblk;
   private ASPRowSet taxExemptset;
   private ASPCommandBar taxExemptbar;
   private ASPTable taxExempttbl;
   private ASPBlockLayout taxExemptlay;

   private ASPField fCOUNTRY;
   private ASPField fADDRESS1;
   private ASPField fADDRESS2;
   private ASPField fZIP_CODE;
   private ASPField fCITY;
   private ASPField fCOUNTRY_CODE;
   private ASPField fSTATE;   
   private ASPField fCOUNTY;


   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private int arraySize;
   private String readOnlyPrim;
   private String readOnlySecond;
   private ASPCommand cmd;
   private ASPQuery qry;
   private ASPBuffer data;
   private int headrowno;
   private int i;
   private int itemrowno;
   private int itemrowno1;
   private int selectedRow;
   private int recs;
   private int itemrowno2;
   private int headrow;
   private int currow;
   private int itemrowno3;
   private ASPBuffer buff;
   private ASPBuffer row; 
   private boolean isInvoiceFlag = true; 
   //***** tabs 
   private ASPTabContainer tabCus;
   private ASPTabContainer TabTax;
   private ASPTabContainer TabAdd;

   //===============================================================
   // Construction 
   //===============================================================

   public CustomerAddress(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();   
      log = mgr.getASPLog();

      arraySize = toInt(ctx.readValue("ARRAYSIZE","0"));      
      readOnlyPrim = ctx.readValue("READONLYPRIM",""); 
      readOnlySecond = ctx.readValue("READONLYSECOND","");    

      if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());

      else if ( mgr.dataTransfered() )
         okFind();

      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();

      else if ( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();

      else if ( !mgr.isEmpty(mgr.getQueryStringValue("CUSTOMER_ID")) )
      {
         okFind();   
         customerInfolay.setLayoutMode(customerInfolay.SINGLE_LAYOUT);
      }
      adjust();

      tabCus.saveActiveTab();
      TabTax.saveActiveTab();
      TabAdd.saveActiveTab();

      ctx.writeValue("ARRAYSIZE",Integer.toString(arraySize));  
      ctx.writeValue("READONLYPRIM",readOnlyPrim); 
      ctx.writeValue("READONLYSECOND",readOnlySecond); 
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String percentage = null;
      String description = null;
      String struct = null;
      String val = null;
      String taxRegime = null;

      val = mgr.readValue("VALIDATE");

      if ( "FEE_CODE".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETDES", "STATUTORY_FEE_API.Get_Description", "STATUTORYFEEDESCRIPTION" );
         cmd.addParameter("TAXES_COMPANY");
         cmd.addParameter("FEE_CODE");       

         cmd = trans.addCustomFunction("GETPER", "STATUTORY_FEE_API.Get_Percentage", "STATUTORYFEEPERCENTAGE" );
         cmd.addParameter("TAXES_COMPANY");
         cmd.addParameter("FEE_CODE"); 

         trans = mgr.validate(trans);    

         description = trans.getValue("GETDES/DATA/STATUTORYFEEDESCRIPTION");
         percentage = trans.getValue("GETPER/DATA/STATUTORYFEEPERCENTAGE");
         trans.clear();      

         if ( percentage == null )
            percentage = "";

         if ( description == null )
            description = "";

         mgr.responseWrite(description + "^" + toDouble(percentage)  + "^");         
      }
      if ("COUNTRY_CODE".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("ISODE","ISO_COUNTRY_API.decode", "ADDRESS_COUNTRY" );
         cmd.addParameter("COUNTRY_CODE");   

         trans = mgr.validate(trans);    

         description = trans.getValue("ISODE/DATA/ADDRESS_COUNTRY");

         mgr.responseWrite(description + "^" + description  + "^"); 
      }

      if ("ADDRESS_COUNTRY1".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("ISOCODE","ISO_COUNTRY_API.encode", "COUNTRY_CODE" );
         cmd.addParameter("ADDRESS_COUNTRY1");   

         cmd = trans.addCustomCommand("SETLOV","ENTERP_ADDRESS_COUNTRY_API.Set_Lov_Reverence" );
         cmd.addParameter("ADDRESS_COUNTRY1");   
         cmd.addParameter("STATE_LOV");   
         cmd.addParameter("COUNTY_LOV");   
         cmd.addParameter("CITY_LOV");

         trans = mgr.validate(trans);    

         description = trans.getValue("ISOCODE/DATA/COUNTRY_CODE");
         String state_lov = trans.getValue("SETLOV/DATA/STATE_LOV");
         String county_lov = trans.getValue("SETLOV/DATA/COUNTY_LOV");
         String city_lov = trans.getValue("SETLOV/DATA/CITY_LOV");
         state_lov = state_lov.substring(0,(state_lov.length()-12));
         county_lov = county_lov.substring(0,(county_lov.length()-19));
         city_lov = city_lov.substring(0,(city_lov.length()-27));
         mgr.responseWrite( description + "^" + state_lov + "^" + county_lov + "^" + city_lov + "^" ); 
      }
      if ("VAT_FREE_VAT_CODE".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("FEEDESC","STATUTORY_FEE_API.Get_Description", "VAT_DESCRIPTION" );
         cmd.addParameter("COMPANY");   
         cmd.addParameter("VAT_FREE_VAT_CODE");   

         trans = mgr.validate(trans);

         description = trans.getValue("FEEDESC/DATA/VAT_DESCRIPTION");

         mgr.responseWrite(description + "^" ); 
      }
      if ("COMPANY".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("TAXREGIME", "Company_invoice_info_api.get_tax_regime","TAX_REGIME");
         cmd.addParameter("COMPANY");   

         trans = mgr.validate(trans);
         taxRegime = trans.getValue("TAXREGIME/DATA/TAX_REGIME");                      

         mgr.responseWrite(taxRegime + "^" ); 
      }
      if ("TAX_BOOK_ID".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("TAXREF","TAX_BOOK_API.Get_Tax_Ref_Desc", "TAX_REF_DESCRIPTION" );
         cmd.addParameter("COMPANY");   
         cmd.addParameter("TAX_BOOK_ID");   

         trans = mgr.validate(trans);
         description = trans.getValue("TAXREF/DATA/TAX_REF_DESCRIPTION");

         trans.clear();

         cmd = trans.addCustomFunction("STRUCTUREID","TAX_BOOK_API.Get_Structure_Id", "TAX_STRUCTURE_ID" );
         cmd.addParameter("COMPANY");   
         cmd.addParameter("TAX_BOOK_ID");   

         trans = mgr.validate(trans);
         struct = trans.getValue("STRUCTUREID/DATA/TAX_STRUCTURE_ID");

         mgr.responseWrite(struct + "^"  + description + "^"); 
      }
      mgr.endResponse();
   }

//=============================================================================
//  COMMAND BAR EDIT GROUP FUNCTIONS
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPBuffer sec = null;
      trans.addSecurityQuery("CUSTOMER_INFO_VAT,DELIVERY_TAX_EXEMPTION");   
      trans = mgr.perform(trans);
      sec = trans.getSecurityInfo(); 


      if ( sec.itemExists("CUSTOMER_INFO_VAT") && sec.itemExists("DELIVERY_TAX_EXEMPTION") )
         isInvoiceFlag = true;
      else
         isInvoiceFlag = false;
      trans.clear();

      qry = trans.addQuery(customerInfoblk);

      if ( mgr.dataTransfered() )
      {
         qry.addOrCondition(mgr.getTransferedData());
         customerInfolay.setLayoutMode(customerInfolay.SINGLE_LAYOUT);
      }

      qry.includeMeta("ALL");

      mgr.querySubmit(trans,customerInfoblk);

      if ( customerInfoset.countRows() == 0 )
         mgr.showAlert(mgr.translate("ENTERWCUSTOMERADDRESSNODATAFOUND: No data found."));
      else
      {
         okFindADDRESS();
         okFindCOMMETHOD();
         okFindADDRESSTYPE();

         if ( isInvoiceFlag )
         {
            okFindTAXCODE();
            okFindTAXES();
            okFindTAXEXEMPT();
         }
      }   
      customerInfoset.syncItemSets();
         
   }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(customerInfoblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      customerInfolay.setCountValue(toInt(customerInfoset.getValue("N")));
      customerInfoset.clear();
   }

   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("CUSTOMERINFO","Customer_Info_Api.New__",customerInfoblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("CUSTOMERINFO/DATA");
      customerInfoset.addRow(data);
   }

   public void saveReturnCustomerInfo()
   {
      ASPManager mgr = getASPManager();
      String language = null;
      headrowno = customerInfoset.getCurrentRowNo();

      language = mgr.readValue("DEFAULT_LANGUAGE");

      if ( mgr.isEmpty(language) )
         mgr.showError(mgr.translate("ENTERWCUSTOMERADDRESSLANGU: The field Language must have a value."));

      customerInfoset.changeRow();   
      mgr.submit(trans); 
      customerInfoset.goTo(headrowno);
   }
   

   public void saveNewCustomerInfo()
   {
      saveReturnCustomerInfo();
      trans.clear();
      newRow();
   }

//==============
// ADDRESS BLOCK
//==============

   public void  okFindADDRESS()
   {
      ASPManager mgr = getASPManager();

      if ( customerInfoset.countRows() != 0 )
      {
         addressset.clear();   
         comMethodset.clear();
         addressTypeset.clear();
         taxCodeset.clear();
         taxesset.clear();   
         taxExemptset.clear();  
         trans.clear();
         qry = trans.addQuery(addressblk);
         qry.addWhereCondition("CUSTOMER_ID = ?");   
         qry.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
         qry.includeMeta("ALL");
         headrowno = customerInfoset.getCurrentRowNo();
         mgr.querySubmit(trans,addressblk);
         customerInfoset.goTo(headrowno);  
         getView();
         okFindADDRESSTYPE();
      }
   }

   public void  countFindADDRESS()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(addressblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      addresslay.setCountValue(toInt(addressset.getValue("N")));
      addressset.clear();
   }

   public void  newRowADDRESS()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans1 = null;
      String[] recArray;
      String isDefaultAddress = null;
      String colcomplex = null;

      trans.clear();
      cmd = trans.addEmptyCommand("ADDRESS","CUSTOMER_INFO_ADDRESS_API.New__",addressblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ADDRESS_CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID")); 

      cmd = trans.addCustomCommand("COLCOMPLEX","Address_Type_Code_API.Enumerate_Type");
      cmd.addParameter("CLIENTVALUE");  
      cmd.addParameter("PARTY_TYPE_DB","CUSTOMER");

      trans = mgr.perform(trans);

      colcomplex =  trans.getValue("COLCOMPLEX/DATA/CLIENTVALUE");

      data = trans.getBuffer("ADDRESS/DATA");   
      data.setFieldItem("ADDRESS_COUNTRY",customerInfoset.getValue("COUNTRY"));  
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
         addressTypeset.clear();
         trans.clear();         

         for ( i = 0; i < arraySize; ++i )
         {
            cmd = trans.addCustomFunction("COLCOMP"+i,"Customer_Info_Address_Type_API.Default_Exist","CLIENTVALUE");
            cmd.addParameter("ADDRESSTYPE_CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
            cmd.addParameter("ADDRESSTYPE_ADDRESS_ID", "1");
            cmd.addParameter("ADDRESSTYPE_ADDRESS_TYPE_CODE",recArray[i]);
         }

         trans1 = mgr.perform(trans);
         trans.clear();

         for ( i = 0; i < arraySize; ++i )
         {
            isDefaultAddress = trans1.getValue("COLCOMP"+i+"/DATA/CLIENTVALUE");

            cmd = trans.addEmptyCommand("ADDRESSTYPE"+i,"CUSTOMER_INFO_ADDRESS_TYPE_API.New__",addressTypeblk);
            cmd.setOption("ACTION","PREPARE");
            cmd.setParameter("ADDRESSTYPE_CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
            cmd.setParameter("ADDRESSTYPE_ADDRESS_ID", "1");
            cmd.setParameter("ADDRESSTYPE_ADDRESS_TYPE_CODE",recArray[i]);
            cmd.setParameter("ADDRESSTYPE_DEFAULT_DOMAIN","FALSE"); 

            if ( "FALSE".equals(isDefaultAddress) )
               cmd.setParameter("DEF_ADDRESS","TRUE");
            else
               cmd.setParameter("DEF_ADDRESS","FALSE");   
         }

         trans = mgr.perform(trans);

         for ( i = 0; i < arraySize; ++i )
         {
            data = trans.getBuffer("ADDRESSTYPE"+i+"/DATA"); 
            addressTypeset.addRow(data);
         }

         trans.clear();
      }        
   }


   public void  saveReturnADDRESS()
   {
      ASPManager mgr = getASPManager();
      String colcomplex1 = null;
      ASPTransactionBuffer trans1 = null;
      addressset.changeRow();
      addressTypeset.changeRows();  
      headrowno = customerInfoset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();

      if ( "New__".equals(addressset.getRowStatus()) )
      {
         addressTypeset.first();
         for ( i = 0; i < arraySize; ++i )
         {
            cmd = trans.addCustomFunction("COLCOMP"+i,"Customer_Info_Address_Type_API.Default_Exist","CLIENTVALUE");
            cmd.addParameter("ADDRESSTYPE_CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
            cmd.addParameter("ADDRESSTYPE_ADDRESS_ID", "1");
            cmd.addParameter("ADDRESSTYPE_ADDRESS_TYPE_CODE", addressTypeset.getValue("ADDRESS_TYPE_CODE"));
            addressTypeset.next();
         }

         trans1 = mgr.perform(trans);
         trans.clear();

         addressTypeset.first();

         for ( int i=0;i<arraySize;++i )
         {
            data = addressTypeset.getRow();
            colcomplex1 =  trans1.getValue("COLCOMP"+i+"/DATA/CLIENTVALUE");
            data.setFieldItem("ADDRESSTYPE_ADDRESS_ID",addressset.getValue("ADDRESS_ID"));

            if ( "FALSE".equals(colcomplex1) )
               data.setValue("DEF_ADDRESS","TRUE");
            else
               data.setValue("DEF_ADDRESS","FALSE");

            addressTypeset.setRow(data);
            addressTypeset.next();
         }
      }

      mgr.submit(trans); 
      customerInfoset.goTo(headrowno);    
      addressset.goTo(itemrowno);     

      okFindADDRESSTYPE();

      addresslay.setLayoutMode(addresslay.SINGLE_LAYOUT);

   }


   public void  saveNewADDRESS()
   {
      ASPManager mgr = getASPManager();
      String colcomplex1 = null;
      ASPTransactionBuffer trans1 = null;
      addressset.changeRow();
      addressTypeset.changeRows(); 
      headrowno = customerInfoset.getCurrentRowNo();     
      itemrowno = addressset.getCurrentRowNo(); 
      if ( "New__".equals(addressset.getRowStatus()) )
      {

         addressTypeset.first();
         for ( i = 0; i < arraySize; ++i )
         {
            cmd = trans.addCustomFunction("COLCOMP"+i,"Customer_Info_Address_Type_API.Default_Exist","CLIENTVALUE");
            cmd.addParameter("ADDRESSTYPE_CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
            cmd.addParameter("ADDRESSTYPE_ADDRESS_ID", "1");
            cmd.addParameter("ADDRESSTYPE_ADDRESS_TYPE_CODE", addressTypeset.getValue("ADDRESS_TYPE_CODE"));
            addressTypeset.next();
         }

         trans1 = mgr.perform(trans);
         trans.clear();

         addressTypeset.first();

         for ( int i=0;i<arraySize;++i )
         {
            data = addressTypeset.getRow();
            colcomplex1 =  trans1.getValue("COLCOMP"+i+"/DATA/CLIENTVALUE");
            data.setFieldItem("ADDRESSTYPE_ADDRESS_ID",addressset.getValue("ADDRESS_ID"));

            if ( "FALSE".equals(colcomplex1) )
               data.setValue("DEF_ADDRESS","TRUE");
            else
               data.setValue("DEF_ADDRESS","FALSE");

            addressTypeset.setRow(data);
            addressTypeset.next();
         }
      }

      mgr.submit(trans); 
      customerInfoset.goTo(headrowno); 
      addressset.goTo(itemrowno);

      newRowADDRESS();
   }

   public void cancelNewADDRESS()
   {
      ASPManager mgr = getASPManager();
      headrowno = customerInfoset.getCurrentRowNo();

      addressset.last();
      addressset.setRemoved();
      addresstbl.clearQueryRow();
      okFindADDRESS();
      okFindADDRESSTYPE();
      customerInfoset.goTo(headrowno);    
      addresslay.setLayoutMode(addresslay.SINGLE_LAYOUT);

   }
//===========================
// COMMUNICATION METHOD BLOCK
//===========================

   public void  okFindCOMMETHOD()
   {
      ASPManager mgr = getASPManager();

      if ( addressset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addQuery(comMethodblk);

         if ( addresslay.isMultirowLayout() )
         {
            qry.addWhereCondition("CUSTOMER_ID = ?");
            qry.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
         }
         else
         {
            qry.addWhereCondition("CUSTOMER_ID = ? and ADDRESS_ID = ?");
            qry.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
            qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
         }

         qry.includeMeta("ALL");
         headrowno = customerInfoset.getCurrentRowNo();   
         itemrowno = addressset.getCurrentRowNo();

         mgr.querySubmit(trans,comMethodblk);

         customerInfoset.goTo(headrowno);   
         addressset.goTo(itemrowno);
      }
   }


   public void  countFindCOMMETHOD()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(comMethodblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      comMethodlay.setCountValue(toInt(comMethodset.getValue("N")));
      comMethodset.clear();
   }


   public void  newRowCOMMETHOD()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("COMMETHOD","CUSTOMER_INFO_COMM_METHOD_API.New__",comMethodblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("COMMETHOD_CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
      cmd.setParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));    
      trans = mgr.perform(trans);
      data = trans.getBuffer("COMMETHOD/DATA");
      comMethodset.addRow(data);
   }


   public void saveReturnCOMMETHOD()
   {
      ASPManager mgr = getASPManager();

      String strvalidFrom, strvalidUntil, strvalDate = null;
      int curComMethodRow, curAddRow, currHeadRow, intvalDate1 =0;

      comMethodset.changeRow();
      currHeadRow = customerInfoset.getCurrentRowNo(); 
      curAddRow = addressset.getCurrentRowNo(); 
      curComMethodRow = comMethodset.getCurrentRowNo(); 
      strvalidFrom  = mgr.formatDate("VALID_FROM", comMethodset.getDateValue("VALID_FROM"));
      strvalidUntil = mgr.formatDate("VALID_TO", comMethodset.getDateValue("VALID_TO"));
      
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
         mgr.showError(mgr.translate("ENTERWCUSTOMERADDRESSDATECHK: Date of 'Valid To' should not be less than Date of 'Valid From'!"));
      }

      mgr.submit(trans);
      comMethodtbl.clearQueryRow(); 
      customerInfoset.goTo(currHeadRow);
      addressset.goTo(curAddRow);
      comMethodset.goTo(curComMethodRow);        
   }


   public void saveNewCOMMETHOD()
   {
      saveReturnCOMMETHOD();
      newRowCOMMETHOD();
   }

//====================
// ADDRESS TYPE BLOCK
//====================

   public void  okFindADDRESSTYPE()
   {
      ASPManager mgr = getASPManager();

      if ( addressset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addEmptyQuery(addressTypeblk);

         if ( addresslay.isMultirowLayout() )
         {
            qry.addWhereCondition("CUSTOMER_ID = ?");
            qry.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
         }
         else
         {
            qry.addWhereCondition("CUSTOMER_ID = ? and ADDRESS_ID = ?");
            qry.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
            qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
         }

         qry.includeMeta("ALL");
         headrowno = customerInfoset.getCurrentRowNo();   
         itemrowno = addressset.getCurrentRowNo();
         itemrowno1 = comMethodset.getCurrentRowNo();

         mgr.querySubmit(trans,addressTypeblk);

         customerInfoset.goTo(headrowno);
         addressset.goTo(itemrowno);   
         comMethodset.goTo(itemrowno1);   
      }
   }


   public void  countFindADDRESSTYPE()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(addressTypeblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      addressTypelay.setCountValue(toInt(addressTypeset.getValue("N")));
      addressTypeset.clear();
   }


   public void  newRowADDRESSTYPE()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ADDRESSTYPE","CUSTOMER_INFO_ADDRESS_API.New__",addressTypeblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));     
      cmd.setParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));     
      trans = mgr.perform(trans);
      data = trans.getBuffer("ADDRESSTYPE/DATA");
      addressTypeset.addRow(data);
   }


   public void  deleteADDRESSTYPE()throws FndException 
   {
      ASPManager mgr = getASPManager();
      // Bug 85354, begin
      ASPTransactionBuffer trans1 = null;
      String validflag = null;
      // Bug 85354, end

      if ( addressTypelay.isMultirowLayout() )
         selectedRow = addressTypeset.getRowSelected();
      else
         selectedRow = addressTypeset.getCurrentRowNo();

      addressTypeset.goTo(selectedRow);
      // Bug 85354, begin,validate the address to check for the default address
      if("TRUE".equals(addressTypeset.getValue("DEF_ADDRESS")))
      {
          cmd = trans.addCustomCommand("CHECKDEFADDRDELETE","Customer_Info_Address_Type_API.Check_Def_Address_Exist");
          cmd.addParameter("CLIENTVALUE");
          cmd.addParameter("CLIENTVALUEDEFADDR");
          cmd.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
          cmd.addParameter("DEF_ADDRESS", addressTypeset.getValue("DEF_ADDRESS"));
          cmd.addParameter("ADDRESSTYPE_ADDRESS_TYPE_CODE", addressTypeset.getValue("ADDRESS_TYPE_CODE"));
          cmd.addParameter("ADDRESSTYPE_OBJID", addressTypeset.getValue("OBJID"));
          cmd.addParameter("VALID_FROM"," ");
          cmd.addParameter("VALID_TO"," ");
      
          trans1 = mgr.perform(trans);
          validflag =  trans1.getValue("CHECKDEFADDRDELETE/DATA/CLIENTVALUEDEFADDR");
      
          if("TRUE".equals(validflag))
          {

             ctx.writeValue("deleteCustomerRoNo",Integer.toString(selectedRow));
             String message = mgr.translate("ENTERWCUSTOMERDEFAULTADDRESSVALIDATE: This is the default &1 Address Type(s) for &2 &3. If removed, there will be no default address for this Address Type(s). Do you want to continue?",addressTypeset.getValue("ADDRESS_TYPE_CODE"),addressset.getValue("PARTY_TYPE"),addressset.getValue("CUSTOMER_ID"));
             askUserAndPerform("_Ifs_Applications_Enterw_CookieName_", message, "CUSTOMERINFO.showWarningDeleteOk","CUSTOMERINFO.showWarningCancel");
             return;
           }
        }
      // Bug 85354, end

      addressTypeset.setRemoved();

      headrowno = customerInfoset.getCurrentRowNo();     
      itemrowno = addressset.getCurrentRowNo();    
      itemrowno1 = comMethodset.getCurrentRowNo();    

      mgr.submit(trans);

      customerInfoset.goTo(headrowno);  
      addressset.goTo(itemrowno);         
      comMethodset.goTo(itemrowno1);

      okFindADDRESSTYPE();
      setPrimarySecondary();  
   }

   // Bug 85354, begin,Remove Default address
   public void showWarningDeleteOk()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
      int tempcurrentrowno;
                  
      headrowno = customerInfoset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      
      tempcurrentrowno = Integer.parseInt(ctx.readValue("deleteCustomerRoNo"));
      addressTypeset.goTo(tempcurrentrowno);
      addressTypeset.setRemoved();
      
      mgr.submit(trans);
      customerInfoset.goTo(headrowno);
      addressset.goTo(itemrowno);
      okFindADDRESSTYPE();
      
   }
   // Bug 85354, end
   public void  saveReturnADDRESSTYPE()throws FndException
   {
      ASPManager mgr = getASPManager();
      //Bug 85354, Begin
      ASPTransactionBuffer trans1 = null;
      String tempaddressvalidation = null;
      String validflag = null;
      //Bug 85354, End

      addressTypeset.changeRow();
      headrowno = customerInfoset.getCurrentRowNo();     
      itemrowno = addressset.getCurrentRowNo();    
      itemrowno1 = comMethodset.getCurrentRowNo(); 
      //Bug 85354, Begin validate default address. check whether company contain default address.
      if("Modify__".equals(addressTypeset.getRowStatus()))
      {                                               
         cmd = trans.addCustomCommand("CHECKDEFADDR","Customer_Info_Address_Type_API.Check_Def_Address_Exist");
         cmd.addParameter("CLIENTVALUE");
         cmd.addParameter("CLIENTVALUEDEFADDR");
         cmd.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
         cmd.addParameter("DEF_ADDRESS", addressTypeset.getValue("DEF_ADDRESS"));
         cmd.addParameter("ADDRESSTYPE_ADDRESS_TYPE_CODE", addressTypeset.getValue("ADDRESS_TYPE_CODE"));
         cmd.addParameter("ADDRESSTYPE_OBJID", addressTypeset.getValue("OBJID"));

      if ( "FALSE".equals(addressTypeset.getValue("DEF_ADDRESS")) )
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
      
      if("TRUE".equals(addressTypeset.getValue("DEF_ADDRESS")))
      {
          if("FALSE".equals(tempaddressvalidation))
          {
             ctx.writeValue("tempCustomerDefRow",Integer.toString(addressTypeset.getCurrentRowNo()));
             String message1 = mgr.translate("ENTERWCUSTOMERDEFAULTADDRESSEXIST: A default address ID already exists for &1 Address Type(s) for this time period. Do you want to set the current address ID as default instead?",addressTypeset.getValue("ADDRESS_TYPE_CODE"));
             askUserAndPerform("_Ifs_Applications_Enterw_CookieName_", message1, "CUSTOMERINFO.showWarningOk","CUSTOMERINFO.showWarningCancel");
             return;
          }
      }
      else if("FALSE".equals(addressTypeset.getValue("DEF_ADDRESS")))
      {
         if("TRUE".equals(validflag))
         {
            String message2 = mgr.translate("ENTERWCUSTOMERDEFAULTADDRESSVALIDATE: This is the default &1 Address Type(s) for &2 &3. If removed, there will be no default address for this Address Type(s). Do you want to continue?",addressTypeset.getValue("ADDRESS_TYPE_CODE"),addressset.getValue("PARTY_TYPE"),addressset.getValue("CUSTOMER_ID"));
            askUserAndPerform("_Ifs_Applications_Enterw_CookieName_", message2, "CUSTOMERINFO.showWarningRemoveOk","CUSTOMERINFO.showWarningCancel");
            return;
         }
      }
      }
      //Bug 85354, End

      mgr.submit(trans);

      customerInfoset.goTo(headrowno);  
      addressset.goTo(itemrowno);         
      comMethodset.goTo(itemrowno1);

      okFindADDRESSTYPE();
      setPrimarySecondary();      
   }
   
   // Bug 85354, begin
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
                  
      headrowno = customerInfoset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      
      addressTypeset.goTo(Integer.parseInt(ctx.readValue("tempCustomerDefRow")));
      cmd = trans.addCustomCommand("REMDEFADDR","Customer_Info_Address_Type_API.Check_Def_Addr_Temp");
      cmd.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
      cmd.addParameter("ADDRESSTYPE_ADDRESS_TYPE_CODE", addressTypeset.getValue("ADDRESS_TYPE_CODE"));
      cmd.addParameter("DEF_ADDRESS", addressTypeset.getValue("DEF_ADDRESS"));
      cmd.addParameter("ADDRESSTYPE_OBJID", addressTypeset.getValue("OBJID"));
      cmd.addParameter("VALID_FROM", mgr.readValue("VALID_FROM"));
      cmd.addParameter("VALID_TO", mgr.readValue("VALID_TO"));
      
      transRem = mgr.perform(trans);
      transRem.clear();
      
      mgr.submit(trans);
      customerInfoset.goTo(headrowno);
      addressset.goTo(itemrowno);
      okFindADDRESSTYPE();
      
   }

   public void showWarningCancel()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
    
      
      headrowno = customerInfoset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
      
      customerInfoset.goTo(headrowno);
      addressset.goTo(itemrowno);
      okFindADDRESSTYPE();
      
   }
   
   public void showWarningRemoveOk()
   {
      ASPManager mgr = getASPManager();
      int headrowno;
      int itemrowno;
    
      headrowno = customerInfoset.getCurrentRowNo();
      itemrowno = addressset.getCurrentRowNo();
    
      mgr.submit(trans);
      
      customerInfoset.goTo(headrowno);
      addressset.goTo(itemrowno);
      
      okFindADDRESSTYPE();
      
   }
   // Bug 85354, end

   public void  setPrimarySecondary()
   {
      ASPManager mgr = getASPManager();
      String isSecondaryReadOnly = null;
      String isPrimaryReadOnly = null;

      if ( addressset.countRows() != 0 )
      {
         trans.clear();
         isPrimaryReadOnly = "TRUE";
         isSecondaryReadOnly = "TRUE";

         recs = addressTypeset.countRows();

         addressTypeset.first();

         for ( i=0;i<recs;++i )
         {
            if ( "PRIMARY".equals(addressTypeset.getValue("ADDRESS_TYPE_CODE_DB")) &&   "TRUE".equals(addressTypeset.getValue("DEF_ADDRESS")) )
               isPrimaryReadOnly = "FALSE";

            if ( "SECONDARY".equals(addressTypeset.getValue("ADDRESS_TYPE_CODE_DB")) &&   "TRUE".equals(addressTypeset.getValue("DEF_ADDRESS")) )
               isSecondaryReadOnly = "FALSE";

            addressTypeset.next();
         }

         headrowno = customerInfoset.getCurrentRowNo();     
         itemrowno = addressset.getCurrentRowNo();    
         itemrowno1 = comMethodset.getCurrentRowNo();    

         data = addressset.getRow();

         if ( "TRUE".equals(isPrimaryReadOnly) )
         {
            data.setFieldItem("PRIMARY_CONTACT","");
            mgr.getASPField("PRIMARY_CONTACT").setReadOnly();
         }
         else
            mgr.getASPField("PRIMARY_CONTACT").unsetReadOnly();

         if ( "TRUE".equals(isSecondaryReadOnly) )
         {
            data.setFieldItem("SECONDARY_CONTACT","");
            mgr.getASPField("SECONDARY_CONTACT").setReadOnly();
         }
         else
            mgr.getASPField("SECONDARY_CONTACT").unsetReadOnly();

         addressset.setRow(data);

         mgr.submit(trans);

         customerInfoset.goTo(headrowno);  
         addressset.goTo(itemrowno);         
         comMethodset.goTo(itemrowno1);
      }
   }

//===============
// TAX CODE BLOCK
//===============

   public void  okFindTAXCODE()
   {
      ASPManager mgr = getASPManager();


      if ( addressset.countRows() != 0 )
      {
         taxCodeset.clear();
         taxesset.clear();   
         taxExemptset.clear();     
         trans.clear();
         qry = trans.addQuery(taxCodeblk);

         if ( addresslay.isMultirowLayout() )
         {
            qry.addWhereCondition("CUSTOMER_ID = ?");
            qry.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
         }
         else
         {
            qry.addWhereCondition("CUSTOMER_ID = ? and ADDRESS_ID = ?");
            qry.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
            qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
         }

         qry.includeMeta("ALL");
         headrowno = customerInfoset.getCurrentRowNo();   
         itemrowno = addressset.getCurrentRowNo();
         itemrowno1 = comMethodset.getCurrentRowNo();
         itemrowno2 = addressTypeset.getCurrentRowNo();

         mgr.querySubmit(trans,taxCodeblk);

         customerInfoset.goTo(headrowno);
         addressset.goTo(itemrowno);   
         comMethodset.goTo(itemrowno1);   
         addressTypeset.goTo(itemrowno2);         
      }
   }


   public void  countFindTAXCODE()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(taxCodeblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      taxCodelay.setCountValue(toInt(taxCodeset.getValue("N")));
      taxCodeset.clear();
   }


   public void  newRowTAXCODE()
   {
      ASPManager mgr = getASPManager();
      String defCompany = null;
      String userid = null; 

      cmd = trans.addCustomFunction("FDUSR","Fnd_Session_API.Get_Fnd_User","USERID");

      cmd = trans.addCustomFunction("DEFCOMP", "User_Profile_SYS.Get_Default","COMPANY");
      cmd.addParameter("ENTRY_CODE", "COMPANY");        
      cmd.addReference("USERID","FDUSR/DATA");  

      cmd = trans.addEmptyCommand("TAXCODE","CUSTOMER_INFO_VAT_API.New__",taxCodeblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.addReference("COMPANY","DEFCOMP/DATA");    
      cmd.setParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));     
      cmd.setParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID")); 

      trans = mgr.perform(trans);

      userid = trans.getValue("FDUSR/DATA/USERID");

      defCompany = trans.getValue("DEFCOMP/DATA/COMPANY");   
      data = trans.getBuffer("TAXCODE/DATA");
      taxCodeset.addRow(data);
   }


   public void  okFindTAXES()
   {
      ASPManager mgr = getASPManager();

      if ( taxCodeset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addQuery(taxesblk);
         qry.addWhereCondition("COMPANY = ? and IDENTITY = ? and ADDRESS_ID = ? and PARTY_TYPE_DB = ?");
         qry.addParameter("COMPANY", taxCodeset.getValue("COMPANY"));
         qry.addParameter("IDENTITY", customerInfoset.getValue("CUSTOMER_ID"));
         qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
         qry.addParameter("PARTY_TYPE_DB", customerInfoset.getValue("PARTY_TYPE_DB"));

         qry.includeMeta("ALL");
         headrowno = customerInfoset.getCurrentRowNo();   
         itemrowno = addressset.getCurrentRowNo();
         itemrowno1 = comMethodset.getCurrentRowNo();
         itemrowno2 = addressTypeset.getCurrentRowNo();
         itemrowno3 = taxCodeset.getCurrentRowNo();   

         mgr.querySubmit(trans,taxesblk);

         customerInfoset.goTo(headrowno);
         addressset.goTo(itemrowno);   
         comMethodset.goTo(itemrowno1);   
         addressTypeset.goTo(itemrowno2);   
         taxCodeset.goTo(itemrowno3);   
      }
   }


   public void  countFindTAXES()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(taxesblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      taxeslay.setCountValue(toInt(taxesset.getValue("N")));
      taxesset.clear();
   }


   public void  newRowTAXES()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("TAXES","DELIVERY_FEE_CODE_API.New__",taxesblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("PARTY_TYPE_DB", "CUSTOMER"); 
      cmd.setParameter("COMPANY", taxCodeset.getValue("COMPANY"));          
      cmd.setParameter("IDENTITY", customerInfoset.getValue("CUSTOMER_ID")); 
      cmd.setParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));   
      trans = mgr.perform(trans);
      data = trans.getBuffer("TAXES/DATA");
      taxesset.addRow(data);
   }

//===========================
// TAX EXEMPTION METHOD BLOCK
//===========================

   public void  okFindTAXEXEMPT()
   {
      ASPManager mgr = getASPManager();
      int itemrowno4 = 0;

      if ( taxCodeset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addQuery(taxExemptblk);
         qry.addWhereCondition("COMPANY = ? and CUSTOMER_ID = ? and ADDRESS_ID = ?");
         qry.addParameter("COMPANY", taxCodeset.getValue("COMPANY"));
         qry.addParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));
         qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));

         qry.includeMeta("ALL");
         headrowno = customerInfoset.getCurrentRowNo();   
         itemrowno = addressset.getCurrentRowNo();
         itemrowno1 = comMethodset.getCurrentRowNo();     
         itemrowno2 = addressTypeset.getCurrentRowNo();
         itemrowno3 = taxCodeset.getCurrentRowNo();
         itemrowno4 = taxesset.getCurrentRowNo();

         mgr.submit(trans);

         customerInfoset.goTo(headrowno);
         addressset.goTo(itemrowno);  
         comMethodset.goTo(itemrowno1);
         addressTypeset.goTo(itemrowno2);
         taxCodeset.goTo(itemrowno3);
         taxesset.goTo(itemrowno4);     
      }
   }


   public void  countFindTAXEXEMPT()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(taxExemptblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      taxExemptlay.setCountValue(toInt(taxExemptset.getValue("N")));
      taxExemptset.clear();
   }


   public void  newRowTAXEXEMPT()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("TAXEXEMPT","DELIVERY_TAX_EXEMPTION_API.New__",taxExemptblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("CUSTOMER_ID", customerInfoset.getValue("CUSTOMER_ID"));     
      cmd.setParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));     
      cmd.setParameter("COMPANY", taxCodeset.getValue("COMPANY"));    
      trans = mgr.perform(trans);
      data = trans.getBuffer("TAXEXEMPT/DATA");
      taxExemptset.addRow(data);
   }


   public void  saveReturnTAXEXEMPT()
   {
      ASPManager mgr = getASPManager();
      int currow5 = 0;

      headrow = customerInfoset.getCurrentRowNo();
      currow =  taxCodeset.getCurrentRowNo();
      currow5 =  taxExemptset.getCurrentRowNo();

      taxExemptset.changeRow();
      mgr.submit(trans);

      customerInfoset.goTo(headrow);
      taxCodeset.goTo(currow);  
      taxExemptset.goTo(currow5);  
      taxeslay.setLayoutMode(taxeslay.MULTIROW_LAYOUT); 
      taxExemptlay.setLayoutMode(taxExemptlay.MULTIROW_LAYOUT);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  invoice()
   {
      ASPManager mgr = getASPManager();

      if ( customerInfolay.isMultirowLayout() )
         selectedRow = customerInfoset.getRowSelected();
      else
         selectedRow = customerInfoset.getCurrentRowNo();

      customerInfoset.goTo(selectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"invoiw/CustomerInvoice.page",buff);       
   }


   public void  credit()
   {
      ASPManager mgr = getASPManager();
      int curr_row = 0;

      curr_row = customerInfoset.getRowSelected();
      customerInfoset.goTo(curr_row);

      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"paylew/CustomerCreditInfo.page",buff);    
   }


   public void  general()
   {
      ASPManager mgr = getASPManager();

      if ( customerInfolay.isMultirowLayout() )
         selectedRow = customerInfoset.getRowSelected();
      else
         selectedRow = customerInfoset.getCurrentRowNo();

      customerInfoset.goTo(selectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"enterw/CustomerInfo.page",buff);  
   }


   public void  payment()
   {
      ASPManager mgr = getASPManager();

      if ( customerInfolay.isMultirowLayout() )
         selectedRow = customerInfoset.getRowSelected();
      else
         selectedRow = customerInfoset.getCurrentRowNo();

      customerInfoset.goTo(selectedRow);        
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"paylew/CustomerPayment.page",buff);       
   }


   public void  orderaddressinfo()
   {
      ASPManager mgr = getASPManager();

      if ( customerInfolay.isMultirowLayout() )
         selectedRow = customerInfoset.getRowSelected();
      else
         selectedRow = customerInfoset.getCurrentRowNo();

      customerInfoset.goTo(selectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("CUSTOMER_ID",customerInfoset.getValue("CUSTOMER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"orderw/CustomerOrderAddress.page",buff);  
   }


   public void  orderinfo()
   {
      ASPManager mgr = getASPManager();

      if ( customerInfolay.isMultirowLayout() )
         selectedRow = customerInfoset.getRowSelected();
      else
         selectedRow = customerInfoset.getCurrentRowNo();

      customerInfoset.goTo(selectedRow);
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

      if ( !mgr.isPresentationObjectInstalled("paylew/CustomerPayment.page") )
         customerInfobar.removeCustomCommand("Payment");

      if ( !mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page") )
         customerInfobar.removeCustomCommand("General");

      if ( !mgr.isPresentationObjectInstalled("invoiw/CustomerInvoice.page") )
         customerInfobar.removeCustomCommand("Invoice");

      if ( !mgr.isPresentationObjectInstalled("paylew/CustomerCreditInfo.page") )
         customerInfobar.removeCustomCommand("credit");

      if ( !mgr.isPresentationObjectInstalled("orderw/CustomerOrderCustomer.page") )
         customerInfobar.removeCustomCommand("orderinfo");

      if ( !mgr.isPresentationObjectInstalled("orderw/CustomerOrderAddress.page") )
         customerInfobar.removeCustomCommand("orderaddressinfo");
   }
   public void  getView()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer temp = null;      
      int selectedrows;
      String state_lov,county_lov,city_lov;

      selectedrows=addressset.countRows();
      trans.clear();
      for (int count=1;count<=selectedrows;++count)
      {
         cmd = trans.addCustomCommand("SETLOV2"+count,"ENTERP_ADDRESS_COUNTRY_API.Set_Lov_Reverence" );
         cmd.addParameter("ADDRESS_COUNTRY1");   
         cmd.addParameter("STATE_LOV");   
         cmd.addParameter("COUNTY_LOV");   
         cmd.addParameter("CITY_LOV");        
      }

      int currrow=customerInfoset.getCurrentRowNo();
      trans = mgr.perform(trans);      
      for (int count=1;count<=selectedrows; ++count)
      {
         state_lov = trans.getValue("SETLOV2"+count+"/DATA/STATE_LOV");
         county_lov = trans.getValue("SETLOV2"+count+"/DATA/COUNTY_LOV");
         city_lov = trans.getValue("SETLOV2"+count+"/DATA/CITY_LOV");
         state_lov = state_lov.substring(0,(state_lov.length()-12));
         county_lov = county_lov.substring(0,(county_lov.length()-19));
         city_lov = city_lov.substring(0,(city_lov.length()-27));

         temp = addressset.getRow();
         temp.setValue("STATE_LOV",state_lov);
         temp.setValue("COUNTY_LOV",county_lov);
         temp.setValue("CITY_LOV",city_lov);
         addressset.setRow(temp);
         addressset.next();
      }
      customerInfoset.goTo(currrow);
      eval(addressblk.generateAssignments());
      addressset.first();
   }
   public void  preDefine()throws FndException
   {
      ASPManager mgr = getASPManager();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer sec = null;
      boolean isInvoiceModulePresent = true;

      trans.addSecurityQuery("CUSTOMER_INFO_VAT,DELIVERY_TAX_EXEMPTION");   
      trans = mgr.performConfig(trans);
      sec = trans.getSecurityInfo();

      if ( sec.itemExists("CUSTOMER_INFO_VAT") && sec.itemExists("DELIVERY_TAX_EXEMPTION") )
      {
         isInvoiceModulePresent = true;
      }
      else
      {
         isInvoiceModulePresent = false;   
      } 

      customerInfoblk = mgr.newASPBlock("CUSTOMERINFO");

      customerInfoblk.addField("OBJID").
         setHidden();

      customerInfoblk.addField("OBJVERSION").
         setHidden();

      customerInfoblk.addField("CUSTOMER_ID").
         setLabel("ENTERWCUSTOMERADDRESSCUSTOMERID: Customer Id.").
         setUpperCase().    
         setSize(25).
         setReadOnly().
         setInsertable();

      customerInfoblk.addField("PARTY").
         setHidden();

      customerInfoblk.addField("NAME").
         setMandatory().
         setLabel("ENTERWCUSTOMERADDRESSNAME: Name").
         setSize(25);

      customerInfoblk.addField("ASSOCIATION_NO").
         setLabel("ENTERWCUSTOMERADDRESSASSOCIATIONNO: Association No.").
         setSize(25);

      customerInfoblk.addField("PARTY_TYPE").
         setMandatory().
         setHidden();

      customerInfoblk.addField("PARTY_TYPE_DB").
         setMandatory().   
         setHidden();

      customerInfoblk.addField("DEFAULT_LANGUAGE").
         setMandatory().                                                   
         setLabel("ENTERWCUSTOMERADDRESSDEFAULTLANGUAGE: Default Language").
         setSelectBox().
         enumerateValues("ISO_LANGUAGE_API").
         unsetSearchOnDbColumn().
         setSize(25);

      customerInfoblk.addField("COUNTRY").
         setLabel("ENTERWCUSTOMERADDRESSCOUNTRY: Country").
         setSelectBox().
         enumerateValues("ISO_COUNTRY_API").
         unsetSearchOnDbColumn().
         setSize(25);

      customerInfoblk.addField("CREATION_DATE", "Date").
         setLabel("ENTERWCUSTOMERADDRESSCREATIONDATE: Creation Date").
         setSize(25).
         setReadOnly();

      customerInfoblk.addField("DEFAULT_DOMAIN").
         setSize(14).
         setMandatory().
         setHidden().
         setLabel("ENTERWCUSTOMERADDRESSDEFAULTDOMAIN: Default Domain").
         setCheckBox("FALSE,TRUE");

      customerInfoblk.addField("CLIENTVALUE").
         setFunction("''").
         setHidden();
      // Bug 85354, begin
      customerInfoblk.addField("CLIENTVALUEDEFADDR").
         setFunction("''").
         setHidden();
      // Bug 85354, end

      customerInfoblk.setView("CUSTOMER_INFO");
      customerInfoblk.defineCommand("CUSTOMER_INFO_API","New__,Modify__,Remove__");
      customerInfoset = customerInfoblk.getASPRowSet();

      customerInfotbl = mgr.newASPTable(customerInfoblk);
      customerInfotbl.setTitle(mgr.translate("ENTERWCUSTOMERADDRESSCUST: Customer"));

      customerInfobar = mgr.newASPCommandBar(customerInfoblk);
      customerInfobar.disableCommand(customerInfobar.PROPERTIES);
      customerInfobar.disableCommand(customerInfobar.DUPLICATEROW); 
      customerInfobar.defineCommand(customerInfobar.SAVERETURN,"saveReturnCustomerInfo");       
      customerInfobar.defineCommand(customerInfobar.SAVENEW,"saveNewCustomerInfo"); 
      customerInfobar.disableCommand(customerInfobar.DELETE); 
      customerInfobar.disableCommand(customerInfobar.NEWROW);  
      // Bug 85354, Begin
      customerInfobar.addCustomCommand("showWarningOk","");
      customerInfobar.disableCommand("showWarningOk");
      customerInfobar.addCustomCommand("showWarningCancel","");
      customerInfobar.disableCommand("showWarningCancel");
      customerInfobar.addCustomCommand("showWarningRemoveOk","");
      customerInfobar.disableCommand("showWarningRemoveOk");
      customerInfobar.addCustomCommand("showWarningDeleteOk","");
      customerInfobar.disableCommand("showWarningDeleteOk");
      // Bug 85354, End

      customerInfobar.addCustomCommand("General",mgr.translate("ENTERWCUSTOMERADDRESSGENINFO: General Info..."));  
      customerInfobar.addCustomCommand("Invoice",mgr.translate("ENTERWCUSTOMERADDRESSINOINFO: Invoice Info..."));
      customerInfobar.addCustomCommand("Payment",mgr.translate("ENTERWCUSTOMERADDRESSPAYINFO: Payment Info..."));
      customerInfobar.addCustomCommand("credit",mgr.translate("ENTERWCUSTOMERADDRESSCRENFO: Credit Info..."));
      customerInfobar.addCustomCommand("orderinfo",mgr.translate("ENTERWCUSTOMERADDRESSORDINFO: Order Info..."));
      customerInfobar.addCustomCommand("orderaddressinfo",mgr.translate("ENTERWCUSTOMERADDRESSORDADDINFO: Order Address Info..."));     

      customerInfolay = customerInfoblk.getASPBlockLayout();   
      customerInfolay.setDefaultLayoutMode(customerInfolay.SINGLE_LAYOUT);     
      customerInfolay.unsetAutoLayoutSelect();

      addressblk = mgr.newASPBlock("ADDRESS");

      addressblk.addField("ADDRESS_OBJID").
         setHidden().
         setDbName("OBJID");

      addressblk.addField("ADDRESS_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      addressblk.addField("ADDRESS_PARTY_TYPE").
         setMandatory().
         setHidden().
         setDbName("PARTY_TYPE");

      addressblk.addField("ADDRESS_PARTY").
         setHidden().
         setDbName("PARTY");

      addressblk.addField("ADDRESS_DEFAULT_DOMAIN").
         setMandatory().
         setHidden().
         setDbName("DEFAULT_DOMAIN");

      addressblk.addField("ADDRESS_ID").
         setSize(29).
         setMandatory().
         setLabel("ENTERWCUSTOMERADDRESSADDRESSID: Address Identity").
         setReadOnly().
         setUpperCase().                   
         setInsertable();

      addressblk.addField("EAN_LOCATION").
         setSize(25).
         setLabel("ENTERWCUSTOMERADDRESSEANLOCATION: Customers Own Address Id.");

      addressblk.addField("ADDRESS").
         setSize(29).
         setMandatory().
         setHidden().          
         setLabel("ENTERWCUSTOMERADDRESSADDRESS: Address");

      addressblk.addField("ADDRESS_COUNTRY1").
         setSize(25).
         setMandatory().
         setLabel("ENTERWCUSTOMERADDRESSADDRESSCOUNTRY: Country").
         setSelectBox().
         enumerateValues("ISO_COUNTRY_API").
         unsetSearchOnDbColumn().
         setCustomValidation("ADDRESS_COUNTRY1","COUNTRY_CODE,STATE_LOV,COUNTY_LOV,CITY_LOV").     
         setDbName("COUNTRY");

      fCOUNTRY = addressblk.addField("ADDRESS_COUNTRY").
                 setSize(25).
                 setMandatory().
                 setLabel("ENTERWCUSTOMERADDRESSADDRESSCOUNTRY: Country").
                 setFunction("COUNTRY");


      fADDRESS1 = addressblk.addField("ADDRESS1").
                  setSize(29).
                  setMandatory().
                  setLabel("ENTERWCUSTOMERADDRESSADDRESS1: Address1"); 

      fADDRESS2 = addressblk.addField("ADDRESS2").
                  setSize(25).
                  setLabel("ENTERWCUSTOMERADDRESSADDRESS2: Address2");   

      fZIP_CODE = addressblk.addField("ZIP_CODE").
                  setSize(29).
                  setLabel("ENTERWCUSTOMERADDRESSZIPCODE: Postal Code");    

      fCITY = addressblk.addField("CITY").
              setSize(25).
              setLabel("ENTERWCUSTOMERADDRESSCITY: City");

      fCOUNTY = addressblk.addField("COUNTY").
                setSize(25).
                setLabel("ENTERWCUSTOMERADDRESSCOUNTY: County");


      fSTATE = addressblk.addField("STATE").
               setSize(29).
               setLabel("ENTERWCUSTOMERADDRESSSTATE: State");

      fCOUNTRY_CODE = addressblk.addField("COUNTRY_CODE").
                      setSize(25).
                      setMandatory().
                      unsetSearchOnDbColumn().
                      setLabel("ENTERWCUSTOMERADDRESSADDRESSCOUNTRYCODE: Country Code").
                      setCustomValidation("COUNTRY_CODE","ADDRESS_COUNTRY,ADDRESS_COUNTRY1").
                      setFunction("ISO_COUNTRY_API.encode(:COUNTRY)");    

      addressblk.addField("IN_CITY").
         setLabel("ENTERWCUSTOMERADDRESSWITHINCITY: Within City Limit").
         setCheckBox("FALSE,TRUE").
         setMandatory(); 

      addressblk.addField("JURISDICTION_CODE").
         setLabel("ENTERWCUSTOMERADDRESSJURICODE: Jurisdiction Code"). 
         setReadOnly(); 

      addressblk.addField("VALID_FROM","Date").
         setSize(29).
         setLabel("ENTERWCUSTOMERADDRESSVALIDFROM: Valid From");

      addressblk.addField("VALID_TO","Date").
         setSize(25).
         setLabel("ENTERWCUSTOMERADDRESSVALIDTO: Valid To");

      addressblk.addField("PRIMARY_CONTACT").
         setSize(29).
         setLabel("ENTERWCUSTOMERADDRESSPRIMCONT: Primary Contact");

      addressblk.addField("SECONDARY_CONTACT").
         setSize(27).
         setLabel("ENTERWCUSTOMERADDRESSSECCONT: Secondary Contact");

      addressblk.addField("ADDRESS_CUSTOMER_ID").
         setMandatory().
         setHidden().
         setDbName("CUSTOMER_ID");

      addressblk.addField("STATE_LOV").
         setMandatory().
         setHidden().
         setFunction("''");

      addressblk.addField("COUNTY_LOV").
         setMandatory().
         setHidden().
         setFunction("''");

      addressblk.addField("CITY_LOV").
         setMandatory().
         setHidden().
         setFunction("''");

      addressblk.setView("CUSTOMER_INFO_ADDRESS");
      addressblk.defineCommand("CUSTOMER_INFO_ADDRESS_API","New__,Modify__,Remove__");
      addressblk.setMasterBlock(customerInfoblk);   
      addressset = addressblk.getASPRowSet();

      addressbar = mgr.newASPCommandBar(addressblk);
      addressbar.disableCommand(addressbar.DUPLICATEROW);     
      addressbar.defineCommand(addressbar.OKFIND,"okFindADDRESS");
      addressbar.defineCommand(addressbar.COUNTFIND,"countFindADDRESS");
      addressbar.defineCommand(addressbar.NEWROW,"newRowADDRESS");    
      addressbar.defineCommand(addressbar.SAVERETURN,"saveReturnADDRESS");   
      addressbar.defineCommand(addressbar.SAVENEW,"saveNewADDRESS");
      addressbar.defineCommand(addressbar.CANCELNEW,"cancelNewADDRESS");     // SP3 Merge -- Makalk - Bug Id 31248 

      addresstbl = mgr.newASPTable(addressblk); 
      addresstbl.setTitle(mgr.translate("ENTERWCUSTOMERADDRESSADD: Address"));

      addresslay = addressblk.getASPBlockLayout(); 
      addresslay.setDialogColumns(2);     
      addresslay.setDefaultLayoutMode(addresslay.SINGLE_LAYOUT);           
      addresslay.unsetAutoLayoutSelect();
      addresslay.setAddressFieldList(fADDRESS1,fADDRESS2,fZIP_CODE,fCITY,fSTATE,fCOUNTY,fCOUNTRY_CODE,fCOUNTRY,"ENTERWCUSTOMERADDRESSADD: Address","ifs.enterw.LocalizedEnterwAddress");  


      comMethodblk = mgr.newASPBlock("COMMETHOD");

      comMethodblk.addField("COMMETHOD_OBJID").
         setHidden().
         setDbName("OBJID");

      comMethodblk.addField("COMMETHOD_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      comMethodblk.addField("COMMETHOD_COMM_ID","Number").
         setHidden().
         setDbName("COMM_ID");

      comMethodblk.addField("COMMETHOD_NAME").
         setSize(11).
         setLabel("ENTERWCUSTOMERADDRESSCOMMETHODNAME: Name").
         setDbName("NAME");

      comMethodblk.addField("COMMETHOD_DESCRIPTION").
         setSize(15).
         setLabel("ENTERWCUSTOMERADDRESSCOMMETHODDESCRIPTION: Description").
         setDbName("DESCRIPTION");

      comMethodblk.addField("COMMETHOD_METHOD_ID").
         setSize(16).
         setMandatory().
         setLabel("ENTERWCUSTOMERADDRESSCOMMETHODMETHODID: Comm. Method").
         setSelectBox().
         enumerateValues("COMM_METHOD_CODE_API").
         unsetSearchOnDbColumn().
         setDbName("METHOD_ID"); 

      comMethodblk.addField("COMMETHOD_CUSTOMER_ID").
         setMandatory().
         setHidden().
         setDbName("CUSTOMER_ID");

      comMethodblk.addField("COMMETHOD_VALUE").
         setSize(17).
         setMandatory().
         setLabel("ENTERWCUSTOMERADDRESSCOMMETHODVALUE: Value").
         setDbName("VALUE");

      comMethodblk.addField("COMMETHOD_ADDRESS_ID").
         setSize(17).
         setLabel("ENTERWCUSTOMERADDRESSCOMMETHODADDRESSID: Address Id.").
         setDbName("ADDRESS_ID").
         setUpperCase().
         setHidden();

      comMethodblk.addField("COMMETHOD_ADDRESS_DEFAULT").
         setSize(20).      
         setLabel("ENTERWCUSTOMERADDRESSCOMMETHODADDRESSDEFAULT: Address Default").
         setCheckBox("FALSE,TRUE").
         setDbName("ADDRESS_DEFAULT");
   
      comMethodblk.addField("COMMETHOD_METHOD_DEFAULT").
         setSize(20).
         setLabel("ENTERWCUSTOMERADDRESSCOMMETHODMETHODDEFAULT: Method Default").
         setCheckBox("FALSE,TRUE").
         setDbName("METHOD_DEFAULT");

      comMethodblk.addField("COMMETHOD_VALID_FROM","Date").
         setSize(17).
         setLabel("ENTERWCUSTOMERADDRESSCOMMETHODVALIDFROM: Valid From").
         setDbName("VALID_FROM");

      comMethodblk.addField("COMMETHOD_VALID_TO","Date").
         setSize(14).
         setLabel("ENTERWCUSTOMERADDRESSCOMMETHODVALIDTO: Valid To").
         setDbName("VALID_TO");

      comMethodblk.setView("CUSTOMER_INFO_COMM_METHOD");
      comMethodblk.defineCommand("CUSTOMER_INFO_COMM_METHOD_API","New__,Modify__,Remove__");
      comMethodblk.setMasterBlock(addressblk);    
      comMethodset = comMethodblk.getASPRowSet();

      comMethodbar = mgr.newASPCommandBar(comMethodblk); 
      comMethodbar.disableCommand(comMethodbar.DUPLICATEROW);       
      comMethodbar.defineCommand(comMethodbar.OKFIND,"okFindCOMMETHOD");
      comMethodbar.defineCommand(comMethodbar.COUNTFIND,"countFindCOMMETHOD");
      comMethodbar.defineCommand(comMethodbar.NEWROW,"newRowCOMMETHOD");   
      comMethodbar.defineCommand(comMethodbar.SAVERETURN,"saveReturnCOMMETHOD");  
      comMethodbar.defineCommand(comMethodbar.SAVENEW,"saveNewCOMMETHOD");  

      comMethodtbl = mgr.newASPTable(comMethodblk);
      comMethodtbl.setTitle(mgr.translate("ENTERWCUSTOMERADDRESSCOMMETHD: Communication Method"));

      comMethodlay = comMethodblk.getASPBlockLayout();    
      comMethodlay.setDefaultLayoutMode(comMethodlay.MULTIROW_LAYOUT);     
      comMethodlay.unsetAutoLayoutSelect();

      addressTypeblk = mgr.newASPBlock("ADDRESSTYPE");

      addressTypeblk.addField( "ADDRESSTYPE_OBJID" ).
         setDbName("OBJID").
         setHidden();

      addressTypeblk.addField( "ADDRESSTYPE_OBJVERSION" ).
         setDbName("OBJVERSION").
         setHidden();

      addressTypeblk.addField("ADDRESSTYPE_CUSTOMER_ID").
         setDbName("CUSTOMER_ID").
         setUpperCase().
         setMandatory().
         setHidden();

      addressTypeblk.addField("ADDRESSTYPE_ADDRESS_ID").
         setUpperCase().
         setDbName("ADDRESS_ID").
         setHidden();

      addressTypeblk.addField("ADDRESSTYPE_ADDRESS_TYPE_CODE").
         setMandatory().
         setLabel("ENTERWCUSTOMERADDRESSADDRESSTYPECODE: Address Type").
         setSelectBox().
         enumerateValues("ADDRESS_TYPE_CODE_API").
         unsetSearchOnDbColumn().
         setDbName("ADDRESS_TYPE_CODE").
         setSize(20);   

      addressTypeblk.addField("ADDRESSTYPE_ADDRESS_TYPE_CODE_DB").
         setUpperCase().
         setDbName("ADDRESS_TYPE_CODE_DB").
         setHidden();

      addressTypeblk.addField("DEF_ADDRESS").
         setLabel("ENTERWCUSTOMERADDRESSDEFADDRESS: Default").
         setCheckBox("FALSE,TRUE").
         setMandatory();
   
      addressTypeblk.addField("ADDRESSTYPE_PARTY").
         setDbName("PARTY").
         setHidden();

      addressTypeblk.addField("ADDRESSTYPE_DEFAULT_DOMAIN").
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

      addressTypeblk.setView("CUSTOMER_INFO_ADDRESS_TYPE");
      addressTypeblk.defineCommand("CUSTOMER_INFO_ADDRESS_TYPE_API","New__,Modify__,Remove__");  
      addressTypeblk.setMasterBlock(addressblk);  
      addressTypeset = addressTypeblk.getASPRowSet();

      addressTypebar = mgr.newASPCommandBar(addressTypeblk);
      addressTypebar.disableCommand(addressTypebar.DUPLICATEROW);        
      addressTypebar.defineCommand(addressTypebar.OKFIND,"okFindADDRESSTYPE");
      addressTypebar.defineCommand(addressTypebar.COUNTFIND,"countFindADDRESSTYPE");
      addressTypebar.defineCommand(addressTypebar.NEWROW,"newRowADDRESSTYPE");     
      addressTypebar.defineCommand(addressTypebar.SAVERETURN,"saveReturnADDRESSTYPE");     
      addressTypebar.defineCommand(addressTypebar.DELETE,"deleteADDRESSTYPE");

      addressTypetbl = mgr.newASPTable(addressTypeblk);
      addressTypetbl.setTitle(mgr.translate("ENTERWCUSTOMERADDRESSADDTYPED: Address Type"));

      addressTypelay = addressTypeblk.getASPBlockLayout();    
      addressTypelay.setDefaultLayoutMode(addressTypelay.MULTIROW_LAYOUT);     
      addressTypelay.unsetAutoLayoutSelect();

      //====================================================================================
      //  THIS BLOCK REFFERS TO TAX CODE BLOCK
      //====================================================================================

      taxCodeblk = mgr.newASPBlock("TAXCODE");

      taxCodeblk.addField("TAXCODE_OBJID").
         setDbName("OBJID").
         setHidden();

      taxCodeblk.addField("TAXCODE_OBJVERSION").
         setDbName("OBJVERSION").
         setHidden();

      taxCodeblk.addField("TAXCODE_CUSTOMER_ID").
         setSize(2).
         setDbName("CUSTOMER_ID").
         setMandatory().
         setHidden().
         setLabel("ENTERWCUSTOMERADDRESSIDENTITY: Identity");

      taxCodeblk.addField("TAXCODE_ADDRESS_ID").
         setUpperCase().
         setDbName("ADDRESS_ID").
         setHidden();          

      taxCodeblk.addField("COMPANY").
         setSize(20).
         setMandatory().
         setUpperCase().
         setDynamicLOV("COMPANY_INVOICE_INFO",650,450).
         setReadOnly().
         setInsertable().
         setCustomValidation("COMPANY","TAX_REGIME").
         setLabel("ENTERWCUSTOMERADDRESSCOMPANY: Company");         

      taxCodeblk.addField("TAX_REGIME").
         setMandatory().   
         setSelectBox().
         enumerateValues("TAX_REGIME_API").
         setLabel("ENTERWCUSTOMERADDRESSTAXREGIME: Tax Regime");

      taxCodeblk.addField("LIABILITY_TYPE").
         setLabel("ENTERWCUSTOMERADDRESSTAXLIABILITY: Tax Liability").
         setLOV("TaxLiabilityLov.page","COUNTRY_CODE",600,445).
         setSize(20);  

      taxCodeblk.addField("TAX_WITHHOLDING").
         setMandatory().
         setSelectBox().
         enumerateValues("Cust_Tax_Withhold_API").
         setLabel("ENTERWCUSTOMERADDRESSTAXWITHHOLD: Tax Withholding");

      taxCodeblk.addField("VAT_FREE_VAT_CODE").
         setUpperCase().
         setLabel("ENTERWCUSTOMERADDRESSVATFREECODE: Tax Free Tax Code").
         setCustomValidation("COMPANY, VAT_FREE_VAT_CODE","VAT_DESCRIPTION").
         setSize(20).
         setDynamicLOV("STATUTORY_FEE","COMPANY",650,450);

      taxCodeblk.addField("VAT_DESCRIPTION").
         setLabel("ENTERWCUSTOMERADDRESSVATDESCRIPTION: Description"). 
         setSize(60).       
         setFunction("STATUTORY_FEE_API.Get_Description(:COMPANY,:VAT_FREE_VAT_CODE)").        
         setReadOnly();

      taxCodeblk.addField("TAX_ID_TYPE").
         setSize(20).
         setLabel("ENTERWCUSTOMERADDRESSTAXIDTYPE: Tax Id Type").
         setDynamicLOV("TAX_ID_TYPE","COUNTRY_CODE",650,450);

      taxCodeblk.addField("VAT_NO").
         setLabel("ENTERWCUSTOMERADDRESSVATNO: Tax Id Number").
         setUpperCase().
         setSize(20); 

      taxCodeblk.addField("TAX_BOOK_ID").
         setSize(20).
         setLabel("ENTERWCUSTOMERADDRESSTAXREFID: Tax Book Ref.").
         setCustomValidation("COMPANY, TAX_BOOK_ID","TAX_STRUCTURE_ID, TAX_REF_DESCRIPTION").
         setDynamicLOV("TAX_BOOK_LOV","COMPANY",650,450);

      taxCodeblk.addField("TAX_STRUCTURE_ID").
         setSize(20).
         setLabel("ENTERWCUSTOMERADDRESSSTRUCTURE: Structure Id.").
         setHidden();

      taxCodeblk.addField("TAX_REF_DESCRIPTION").
         setLabel("ENTERWCUSTOMERADDRESSTAXREFDESCRIPTION: Tax Book Ref. Description"). 
         setSize(60).       
         setFunction("TAX_BOOK_API.Get_Tax_Ref_Desc(:COMPANY,:TAX_BOOK_ID)").        
         setReadOnly();      

      taxCodeblk.addField("TAX_ROUNDING_METHOD").
         setMandatory().   
         setSelectBox().
         enumerateValues("TAX_ROUNDING_METHOD_API").
         setLabel("ENTERWCUSTOMERADDRESSTAXROUNDINGMETHOD: Tax Rounding Method");

      taxCodeblk.addField("TAX_ROUNDING_LEVEL").
         setMandatory().   
         setSelectBox().
         enumerateValues("TAX_ROUNDING_LEVEL_API").
         setLabel("ENTERWCUSTOMERADDRESSTAXROUNDINGLEVEL: Tax Rounding Level");

      taxCodeblk.addField("USERID").
         setHidden().
         setFunction("' '");

      taxCodeblk.addField("DUMMY").
         setHidden().
         setFunction("' '");

      taxCodeblk.addField("USER_NAME").
         setHidden().
         setFunction("' '");

      taxCodeblk.addField("ENTRY_CODE").
         setHidden().
         setFunction("' '");      

      taxCodeblk.setView("CUSTOMER_INFO_VAT");
      taxCodeblk.defineCommand("CUSTOMER_INFO_VAT_API","New__,Modify__,Remove__");

      if ( isInvoiceModulePresent )
         taxCodeblk.setMasterBlock(addressblk);

      taxCodeset = taxCodeblk.getASPRowSet();

      taxCodebar = mgr.newASPCommandBar(taxCodeblk);
      taxCodebar.disableCommand(taxCodebar.DUPLICATEROW);    
      taxCodebar.defineCommand(taxCodebar.OKFIND,"okFindTAXCODE");
      taxCodebar.defineCommand(taxCodebar.COUNTFIND,"countFindTAXCODE");
      taxCodebar.defineCommand(taxCodebar.NEWROW,"newRowTAXCODE"); 

      taxCodetbl = mgr.newASPTable(taxCodeblk);
      taxCodetbl.setTitle(mgr.translate("ENTERWCUSTOMERADDRESSTAXCODED: Tax Code"));

      taxCodelay = taxCodeblk.getASPBlockLayout();    
      taxCodelay.setDefaultLayoutMode(taxCodelay.SINGLE_LAYOUT); 
      taxCodelay.unsetAutoLayoutSelect(); 

      taxesblk = mgr.newASPBlock("TAXES");

      taxesblk.addField("TAXES_OBJID").
         setDbName("OBJID").
         setHidden(); 

      taxesblk.addField("TAXES_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      taxesblk.addField("INFO").
         setFunction("''").
         setHidden();

      taxesblk.addField("ATTR").
         setFunction("''").
         setHidden();

      taxesblk.addField("ACTION").
         setFunction("''").
         setHidden();

      taxesblk.addField("TAXES_PARTY_TYPE_DB").
         setHidden().
         setDbName("PARTY_TYPE_DB");            

      taxesblk.addField("TAXES_COMPANY").
         setHidden().
         setDbName("COMPANY");          

      taxesblk.addField("FEE_CODE").
         setSize(10).
         setDynamicLOV("STATUTORY_FEE","TAXES_COMPANY COMPANY",650,450).
         setMandatory().
         setLabel("ENTERWCUSTOMERADDRESSFEECODE: Tax Code").
         setUpperCase().
         setReadOnly().
         setCustomValidation("TAXES_COMPANY,FEE_CODE","STATUTORYFEEDESCRIPTION,STATUTORYFEEPERCENTAGE").
         setInsertable();    

      taxesblk.addField("IDENTITY").
         setHidden();

      taxesblk.addField("TAXES_ADDRESS_ID").
         setHidden().
         setDbName("ADDRESS_ID");                      

      taxesblk.addField("STATUTORYFEEDESCRIPTION").
         setSize(60).
         setLabel("ENTERWCUSTOMERADDRESSSTATUTORYFEEDESCRIPTION: Description").
         setFunction("STATUTORY_FEE_API.Get_Description(:TAXES_COMPANY,:FEE_CODE)").
         setReadOnly();          

      taxesblk.addField("STATUTORYFEEPERCENTAGE","Number","#.##").
         setSize(17).
         setLabel("ENTERWCUSTOMERADDRESSSTATUTORYFEEPERCENTAGE: Percentage").
         setFunction("STATUTORY_FEE_API.Get_Percentage(:TAXES_COMPANY,:FEE_CODE)").
         setReadOnly().
         setAlignment("RIGHT"); 

      taxesblk.addField("TAX_ID_NUMBER").
         setSize(10).
         setLabel("ENTERWCUSTOMERADDRESSTAXIDNUMBER: Tax Id Number");
            
      taxesblk.setView("DELIVERY_FEE_CODE");
      taxesblk.defineCommand("DELIVERY_FEE_CODE_API","New__,Modify__,Remove__");

      if ( isInvoiceModulePresent )
         taxesblk.setMasterBlock(taxCodeblk);

      taxesset = taxesblk.getASPRowSet();

      taxesbar = mgr.newASPCommandBar(taxesblk);
      taxesbar.disableCommand(taxesbar.DUPLICATEROW);
      taxesbar.disableCommand(taxesbar.EDITROW);       
      taxesbar.defineCommand(taxesbar.OKFIND,"okFindTAXES");
      taxesbar.defineCommand(taxesbar.COUNTFIND,"countFindTAXES");
      taxesbar.defineCommand(taxesbar.NEWROW,"newRowTAXES"); 

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

      taxExemptblk.addField("TAXEXEMPT_CUSTOMER_ID").
         setDbName("CUSTOMER_ID").
         setMandatory().
         setHidden();

      taxExemptblk.addField("TAXEXEMPT_ADDRESS_ID").
         setHidden().
         setDbName("ADDRESS_ID");

      taxExemptblk.addField("TAXEXEMPT_COMPANY").
         setDbName("COMPANY").
         setMandatory().
         setHidden();

      taxExemptblk.addField("TAX_EXEMPTION_CERT_NO").
         setUpperCase().
         setSize(25).
         setLabel("ENTERWCUSTOMERADDRESSEXEMPTIONCERTNO: Tax Exemption Certification No."). 
         setMandatory();

      taxExemptblk.addField("CERTIFICATE_JURISDICTION").
         setSize(25).
         setLabel("ENTERWCUSTOMERADDRESSJURISDICTION: Certificate Jurisdiction"). 
         setMandatory();

      taxExemptblk.addField("EXEMPT_CERTIFICATE_TYPE").
         setSize(25).
         setLabel("ENTERWCUSTOMERADDRESSCERTIFICATE_TYPE: Exempt Certificate Type"). 
         setSelectBox().
         enumerateValues("EXEMPT_CERTIFICATE_TYPE_API").
         unsetSearchOnDbColumn();

      taxExemptblk.addField("EXEMPT_CERTIFICATE_TYPE_DB").
         setHidden();

      taxExemptblk.addField("CERTIFICATION_DATE","Date").
         setSize(25).
         setLabel("ENTERWCUSTOMERADDRESSCERT_DATE: Certification Date");

      taxExemptblk.addField("EXPIRATION_DATE","Date").
         setLabel("ENTERWCUSTOMERADDRESSEXP_DATE: Expiration Date").
         setSize(25);          

      taxExemptblk.setView("DELIVERY_TAX_EXEMPTION");
      taxExemptblk.defineCommand("DELIVERY_TAX_EXEMPTION_API","New__,Modify__,Remove__");

      if ( isInvoiceModulePresent )
         taxExemptblk.setMasterBlock(taxCodeblk);

      taxExemptset = taxExemptblk.getASPRowSet();

      taxExemptbar = mgr.newASPCommandBar(taxExemptblk);
      taxExemptbar.disableCommand(taxExemptbar.DUPLICATEROW);    
      taxExemptbar.defineCommand(taxExemptbar.OKFIND,"okFindTAXEXEMPT");
      taxExemptbar.defineCommand(taxExemptbar.COUNTFIND,"countFindTAXEXEMPT");
      taxExemptbar.defineCommand(taxExemptbar.NEWROW,"newRowTAXEXEMPT");   

      taxExempttbl = mgr.newASPTable(taxExemptblk);   
      taxExempttbl.setTitle(mgr.translate("ENTERWCUSTOMERADDRESSTAXEXEMPD: Tax Code Exemption"));   

      taxExemptlay = taxExemptblk.getASPBlockLayout();    
      taxExemptlay.setDefaultLayoutMode(taxExemptlay.MULTIROW_LAYOUT); 
      taxExemptlay.unsetAutoLayoutSelect();

      //****   Added tabs to the form 

      customerInfobar.addCustomCommand("activateAddress","Address");
      customerInfobar.addCustomCommand("activateCommMethod","Communication Method");
      addressbar.addCustomCommand("activateAddType","Address Type");
      addressbar.addCustomCommand("activateTaxInfom","Tax Infomation");
      taxCodebar.addCustomCommand("activateTaxes","Taxes");
      taxCodebar.addCustomCommand("activateTaxExemption","Tax Code Exemption");

      tabCus = newASPTabContainer("ENTERWCUSTADDRINFO");  // <tab within tab> initializing the main tab container
      TabTax = newASPTabContainer("ENTERWCUSTADDRTAX");
      TabAdd = newASPTabContainer("ENTERWCUSTADDR");
            
      tabCus.addTab(mgr.translate("ENTERWCUSTOMERADDRESSADDR: Address"),"javascript:commandSet('CUSTOMERINFO.activateAddress','')");
      tabCus.addTab(mgr.translate("ENTERWCUSTOMERADDRESSCOMMETH: Communication Method"),"javascript:commandSet('CUSTOMERINFO.activateCommMethod','')");  
      TabTax.addTab(mgr.translate("ENTERWCUSTOMERADDRESSTAXES: Taxes"),"javascript:commandSet('TAXCODE.activateTaxes','')");                             
      TabTax.addTab(mgr.translate("ENTERWCUSTOMERADDRESSTAXEXEMP: Tax Code Exemption"),"javascript:commandSet('TAXCODE.activateTaxExemption','')");                             
      TabAdd.addTab(mgr.translate("ENTERWCUSTOMERADDRESSADDTYPE: Address Type"),"javascript:commandSet('ADDRESS.activateAddType','')");                             
      TabAdd.addTab(mgr.translate("ENTERWCUSTOMERADDRESSTAXINFO: Tax Infomation"),"javascript:commandSet('ADDRESS.activateTaxInfom','')");                             

      customerInfobar.removeCustomCommand("activateAddress");
      customerInfobar.removeCustomCommand("activateCommMethod");
      addressbar.removeCustomCommand("activateAddType");
      addressbar.removeCustomCommand("activateTaxInfom");
      taxCodebar.removeCustomCommand("activateTaxes");
      taxCodebar.removeCustomCommand("activateTaxExemption");

   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      setDynamicRmb();

      if ( customerInfoset.countRows() == 0 )
      {
         customerInfobar.disableCommand(customerInfobar.BACK);
         customerInfobar.disableCommand(customerInfobar.FORWARD);
         customerInfobar.disableCommand(customerInfobar.BACKWARD);
         customerInfobar.disableCommand(customerInfobar.DELETE);
         customerInfobar.disableCommand(customerInfobar.EDITROW);
         customerInfobar.disableCommand(customerInfobar.DUPLICATEROW);
         customerInfobar.disableCustomCommand("General");
         customerInfobar.disableCustomCommand("Invoice");
         customerInfobar.disableCustomCommand("Payment");
         customerInfobar.disableCustomCommand("credit");
         customerInfobar.disableCustomCommand("orderinfo");
         customerInfobar.disableCustomCommand("orderaddressinfo");
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
      else
      {

         fSTATE.setDynamicLOV(addressset.getValue("STATE_LOV"),650,450); 
         fCOUNTY.setDynamicLOV(addressset.getValue("COUNTY_LOV"),650,450);      
         fCITY.setDynamicLOV(addressset.getValue("CITY_LOV"),650,450);                   

         fSTATE.setLOVProperty("WHERE","COUNTRY_CODE='"+addressset.getValue("COUNTRY_CODE")+"'");
         fCOUNTY.setLOVProperty("WHERE","COUNTRY_CODE='"+addressset.getValue("COUNTRY_CODE")+"' AND STATE='"+addressset.getValue("STATE")+"'");      
         fCITY.setLOVProperty("WHERE","COUNTRY_CODE='"+addressset.getValue("COUNTRY_CODE")+"' AND STATE='"+addressset.getValue("STATE")+"' AND COUNTY='"+addressset.getValue("COUNTY")+"'");         
      }

      if ( comMethodset.countRows() == 0 )
      {
         comMethodbar.disableCommand(comMethodbar.BACK); 
         comMethodbar.disableCommand(comMethodbar.FORWARD);
         comMethodbar.disableCommand(comMethodbar.BACKWARD);
         comMethodbar.disableCommand(comMethodbar.DELETE);
         comMethodbar.disableCommand(comMethodbar.EDITROW);
         comMethodbar.disableCommand(comMethodbar.DUPLICATEROW);
      }

      if ( addressTypeset.countRows() == 0 )
      {
         addressTypebar.disableCommand(addressTypebar.BACK); 
         addressTypebar.disableCommand(addressTypebar.FORWARD);
         addressTypebar.disableCommand(addressTypebar.BACKWARD);
         addressTypebar.disableCommand(addressTypebar.DELETE);
         addressTypebar.disableCommand(addressTypebar.EDITROW);
         addressTypebar.disableCommand(addressTypebar.DUPLICATEROW);
      }

      if ( taxCodeset.countRows() == 0 )
      {
         taxCodebar.disableCommand(taxCodebar.BACK); 
         taxCodebar.disableCommand(taxCodebar.FORWARD);
         taxCodebar.disableCommand(taxCodebar.BACKWARD);
         taxCodebar.disableCommand(taxCodebar.DELETE);
         taxCodebar.disableCommand(taxCodebar.EDITROW);
         taxCodebar.disableCommand(taxCodebar.DUPLICATEROW);
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


      if ( taxExemptlay.isNewLayout() )
      {
         taxCodebar.disableCommand(taxCodebar.BACK); 
         taxCodebar.disableCommand(taxCodebar.FORWARD);
         taxCodebar.disableCommand(taxCodebar.BACKWARD);
         taxCodebar.disableCommand(taxCodebar.FIRST);
         taxCodebar.disableCommand(taxCodebar.LAST);
         taxCodebar.disableCommand(taxCodebar.NEWROW);
         taxCodebar.disableCommand(taxCodebar.DELETE);
         taxCodebar.disableCommand(taxCodebar.EDITROW);
         taxesbar.disableCommand(taxesbar.NEWROW);
         taxesbar.disableCommand(taxesbar.DELETE);
      }

      if ( taxeslay.isNewLayout() )
      {
         taxCodebar.disableCommand(taxCodebar.BACK); 
         taxCodebar.disableCommand(taxCodebar.FORWARD);
         taxCodebar.disableCommand(taxCodebar.BACKWARD);
         taxCodebar.disableCommand(taxCodebar.FIRST);
         taxCodebar.disableCommand(taxCodebar.LAST);
         taxCodebar.disableCommand(taxCodebar.NEWROW);
         taxCodebar.disableCommand(taxCodebar.DELETE);
         taxCodebar.disableCommand(taxCodebar.EDITROW);
         taxExemptbar.disableCommand(taxExemptbar.NEWROW);
         taxExemptbar.disableCommand(taxExemptbar.DELETE);
         taxExemptbar.disableCommand(taxExemptbar.EDITROW);
      }

      if ( taxCodelay.isEditLayout() )
      {
         taxExemptbar.disableCommand(taxExemptbar.NEWROW);
         taxesbar.disableCommand(taxesbar.NEWROW);
      }

      taxExemptbar.disableCommand(taxExemptbar.SAVENEW);

      if (taxCodeset.countRows()>0 )
      {
         trans.clear();
         String ss = taxCodeset.getValue("LIABILITY_TYPE");
         cmd = trans.addCustomFunction("TAXREF","TAX_LIABILITY_API.Get_Taxable", "LIABILITY_TYPE" );
         cmd.addParameter("LIABILITY_TYPE",ss);   
         cmd.addParameter("COUNTRY_CODE",addressset.getValue("COUNTRY_CODE"));
         cmd = trans.addCustomFunction("TAXREF1","TAXABLE_API.Encode", "LIABILITY_TYPE" );
         cmd.addReference("LIABILITY_TYPE","TAXREF/DATA");

         trans = mgr.validate(trans);
         String description = trans.getValue("TAXREF1/DATA/LIABILITY_TYPE");

         trans.clear();
         if ( "EXM".equals(description))
         {
            taxesbar.disableCommand(taxCodebar.NEWROW);
         }

         cmd = trans.addCustomFunction("CHECKVALUE2","Tax_Regime_API.Encode","DUMMY");
         cmd.addParameter("DUMMY", taxCodeset.getValue("TAX_REGIME"));

         trans = mgr.perform(trans);                                       
         String taxreg = trans.getValue("CHECKVALUE2/DATA/DUMMY");
         trans.clear();
         if ( "VAT".equals(taxreg) )
         {
            taxesbar.disableCommand(taxCodebar.NEWROW);
            if (!("EXM".equals(description)))
            {
               taxExemptbar.disableCommand(taxExemptbar.NEWROW);
            }

         }
         if (( "MIX".equals(taxreg)) || ("SALETX".equals(taxreg)))
         {
            if (!("EXM".equals(description)))
            {
               taxExemptbar.disableCommand(taxExemptbar.NEWROW);
            }

         }

      }

      mgr.setPageNonExpiring();    
   }

   /* Returns html for tabs init */
   public String  tabCusInit()
   {
      return tabCus.showTabsInit();
   }

   public String  TabTaxInit()
   {
      return TabTax.showTabsInit();
   }

   public String  TabAddInit()
   {
      return TabAdd.showTabsInit();
   }


   /* Returns html for tabs finish */
   public String  tabCusFinish()
   {
      return tabCus.showTabsFinish();
   }

   public String  TabTaxFinish()
   {
      return TabTax.showTabsFinish();
   } 

   public String  TabAddFinish()
   {
      return TabAdd.showTabsFinish();
   } 


   public void  activateAddress()
   {
      tabCus.setActiveTab(1);
   }

   public void  activateCommMethod()
   {
      tabCus.setActiveTab(2);
   }

   public void  activateTaxes()
   {
      TabTax.setActiveTab(1);
   }

   public void  activateTaxExemption()
   {
      TabTax.setActiveTab(2);
   } 

   public void  activateAddType()
   {
      TabAdd.setActiveTab(1);
   }

   public void  activateTaxInfom()
   {
      TabAdd.setActiveTab(2);
   } 


//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "ENTERWCUSTOMERADDRESSTITLE: Customer - Address Info.";
   } 

   protected String getTitle()
   {
      return "ENTERWCUSTOMERADDRESSTITLE: Customer - Address Info.";
   }


   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("ENTERWCUSTOMERADDRESSTITLE: Customer - Address Info."));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("  <input type=\"hidden\" name=\"READONLYPRIM\" value=\"");
      out.append(readOnlyPrim);
      out.append("\"><input type=\"hidden\"\n");
      out.append("  name=\"READONLYSECOND\" value=\"");
      out.append(readOnlySecond);
      out.append("\">");
      out.append(mgr.startPresentation("ENTERWCUSTOMERADDRESSTITLE: Customer - Address Info."));

      if ( customerInfolay.isVisible() )
         out.append(customerInfolay.show());

      if ( addresslay.isVisible() && comMethodlay.isVisible() && customerInfoset.countRows() != 0 )
         appendToHTML(tabCusInit());

      if ( tabCus.getActiveTab() == 1 && customerInfoset.countRows() != 0)
      {
         if ( addresslay.isVisible() )
            appendToHTML(addresslay.show());
      }
      if ( tabCus.getActiveTab() == 2 )
      {
         if (( comMethodlay.isVisible()  && addressset.countRows() != 0 &&   addresslay.isVisible() ) ||   comMethodlay.isNewLayout()  ||   comMethodlay.isEditLayout() )
            appendToHTML(comMethodlay.show());
      }

      if ( addresslay.isVisible() && comMethodlay.isVisible() )
         appendToHTML(tabCusFinish());

      if (tabCus.getActiveTab() == 1)
      {
         if ( addressTypelay.isVisible() && taxCodelay.isVisible() && customerInfoset.countRows() != 0 )
            appendToHTML(TabAddInit());

         if ( TabAdd.getActiveTab() == 1 )
         {
            if ( addressTypelay.isVisible() && ( addressset.countRows() != 0 &&   addresslay.isVisible() ) ||   addressTypelay.isNewLayout()  ||   addressTypelay.isEditLayout())
               appendToHTML(addressTypelay.show());
         }
         if ( TabAdd.getActiveTab() == 2 )
         {
            if ( taxCodelay.isVisible() && ( addressset.countRows() != 0 &&   addresslay.isVisible() ) ||   taxCodelay.isNewLayout()  ||   taxeslay.isNewLayout() ||   taxExemptlay.isNewLayout()  ||   taxExemptlay.isEditLayout()  ||   taxCodelay.isEditLayout())
               appendToHTML(taxCodelay.show());
         }

         if ( addressTypelay.isVisible() && taxCodelay.isVisible() )
            appendToHTML(TabAddFinish());
      }

      if ( isInvoiceFlag )
      {
         if (TabAdd.getActiveTab() == 2 && tabCus.getActiveTab() != 2 )
         {
            if ( taxCodelay.isVisible()  &&  ( addressset.countRows() != 0 &&   addresslay.isVisible() ) ||   taxCodelay.isNewLayout()  ||   taxeslay.isNewLayout() ||   taxExemptlay.isNewLayout()  ||   taxExemptlay.isEditLayout()  ||   taxCodelay.isEditLayout() )
            {
               if ( taxeslay.isVisible() && taxExemptlay.isVisible() )
                  appendToHTML(TabTaxInit());

               if ( TabTax.getActiveTab() == 1 && taxCodeset.countRows() != 0 || taxCodelay.isEditLayout() ||   taxExemptlay.isNewLayout() ||   taxExemptlay.isEditLayout() )
               {
                  if ( taxeslay.isVisible() && taxCodeset.countRows() != 0 ||   taxCodelay.isEditLayout() ||   taxExemptlay.isNewLayout() ||   taxExemptlay.isEditLayout() )
                     appendToHTML(taxeslay.show());
               }
               if ( TabTax.getActiveTab() == 2 && taxCodeset.countRows() != 0 || taxCodelay.isEditLayout() ||   taxeslay.isNewLayout() ||   taxExemptlay.isEditLayout() )
               {
                  if ( taxExemptlay.isVisible() )
                     appendToHTML(taxExemptlay.show());
               }
               if ( taxeslay.isVisible() && taxExemptlay.isVisible() )
                  appendToHTML(TabTaxFinish());
            }

         }

      }

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("function validatePrimaryContact(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkPrimaryContact(i) ) return;\n");
      appendDirtyJavaScript("   if( getValue_('READONLYPRIM',i)=='True' )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("           document.form.PRIMARY_CONTACT.value = '';        \n");
      appendDirtyJavaScript("           alert('");
      appendDirtyJavaScript(mgr.translate("ENTERWCUSTOMERADDRESSERRORMES: This Field is Read Only"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("           }       \n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function validateSecondaryContact(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkSecondaryContact(i) ) return;\n");
      appendDirtyJavaScript("   if( getValue_('READONLYSECOND',i)=='True' )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("           document.form.SECONDARY_CONTACT.value = '';        \n");
      appendDirtyJavaScript("           alert('");
      appendDirtyJavaScript(mgr.translate("ENTERWCUSTOMERADDRESSERRORMES: This Field is Read Only"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("        }  \n");
      appendDirtyJavaScript("}\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");

      return out;
   }
}
