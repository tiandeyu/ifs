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
*  File        : SupplierAddress.java 
*  Modified    : Anil Padmajeewa
*                anpalk    21 Feb 2001    Code Review, Rename Blocks 
*    ASP2JAVA Tool  2001-01-23  - Created Using the ASP file SupplierAddress.asp
*  Modified    : Madhu  24-05-2001  Call # 65300 
*  Modified    : Madhu  30-05-2001  Call # 65717 & 65719
*  Modified    : anpelk 30-07-2001  Call # 67177 Remove steps of creating of address type from
*                newRowADDRESS() and call them when saving the new address. ( i.e call these
*                steps in saveNewADDRESS() and saveReturnADDRESS()
*  Modified    : anpelk 20-08-2001 Call # 67551 add a Column(Document Receiver) to communication method Tab
*  Modified    : Madhu 12-09-2001  Call # 68569
*  Modified    : anpalk 25/09/2001 Bug Id 69497 
*  Modified    : KuPelk 18/01/2002 IID 10997 .Added LOV view for State column.
*  Modified    : anpalk 18/03/2002 Bug Id 78533  
*  Modified             : anpalk 03/07/2002 Call Id 29991
*  Modified    : gacolk 07/02/2003 Removed deprecated method splitToVector() with split() and did other necessary changes
*  Modified    : MAWELK      2005-04-09   FIPR364 - Corrected web tags.
*  Jakalk      : 2005-09-07 - Code Cleanup, Removed doReset and clone methods.
*  Maaylk      : 2006-01-26 - B131526 Changed to_date() insaveReturnCOMMETHOD().
*  Maselk      : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class SupplierAddress extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.SupplierAddress");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;
   private ASPBlock supplierInfoblk;
   private ASPRowSet supplierInfoset;
   private ASPCommandBar supplierInfobar;
   private ASPTable supplierInfotbl;
   private ASPBlockLayout supplierInfolay;
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
   private ASPQuery qry;
   private ASPCommand cmd;
   private ASPBuffer data;
   private int headrowno;
   private String colcomplex;
   private int i;
   private String isAddress1;
   private int itemrowno;
   private int selectedRow;
   private ASPBuffer buff;
   private ASPBuffer row;

   //===============================================================
   // Construction 
   //===============================================================

   public SupplierAddress(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();   
      trans = mgr.newASPTransactionBuffer();

      arraySize = toInt(ctx.readValue("ARRAYSIZE","0"));   

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SUPPLIER_ID")))
         okFind();

      adjust();

      ctx.writeValue("ARRAYSIZE", Integer.toString(arraySize));   
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String val = null;
      String description = null;

      val = mgr.readValue("VALIDATE");
      if ("COUNTRY_CODE".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("ISODE","ISO_COUNTRY_API.decode", "ADDRESS_COUNTRY" );
         cmd.addParameter("COUNTRY_CODE");   

         trans = mgr.validate(trans);    

         description = trans.getValue("ISODE/DATA/ADDRESS_COUNTRY");

         mgr.responseWrite(description + "^" + description  + "^"); 
      }
      if("ADDRESS_COUNTRY1".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("ISOCODE","ISO_COUNTRY_API.encode", "ADDRESS_COUNTRY" );
         cmd.addParameter("ADDRESS_COUNTRY1");   

         trans = mgr.validate(trans);    

         description = trans.getValue("ISOCODE/DATA/ADDRESS_COUNTRY");

         mgr.responseWrite(description + "^" ); 
      }
      mgr.endResponse();
   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(supplierInfoblk);

      if (mgr.dataTransfered())
      {
         qry.addOrCondition(mgr.getTransferedData());   
         supplierInfolay.setLayoutMode(supplierInfolay.SINGLE_LAYOUT);  
      }
      qry.includeMeta("ALL");

      mgr.querySubmit(trans,supplierInfoblk);

      if (supplierInfoset.countRows() == 0)
      {
         addressset.clear();
         comMethodset.clear();
         addressTypeset.clear();
         mgr.showAlert(mgr.translate("ENTERWSUPPLIERADDRESSNODATAFOUND: No data found.")); 
         supplierInfoset.clear();
      }

      if (mgr.dataTransfered())
      {
         okFindADDRESS();
         okFindCOMMETHOD();
         okFindADDRESSTYPE();
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

      cmd = trans.addEmptyCommand("MAIN","SUPPLIER_INFO_API.New__",supplierInfoblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MAIN/DATA");
      supplierInfoset.addRow(data);
   }

//==============
// ADDRESS BLOCK
//==============

   public void  okFindADDRESS()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfoset.countRows() != 0)
      {
         trans.clear();
         qry = trans.addQuery(addressblk);
         qry.addWhereCondition("SUPPLIER_ID = ?");   
         qry.addParameter("SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));
         qry.includeMeta("ALL");
         headrowno = supplierInfoset.getCurrentRowNo();
         mgr.querySubmit(trans,addressblk);
         supplierInfoset.goTo(headrowno);  
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
      Vector recArray;

      trans.clear();

      cmd = trans.addEmptyCommand("ADDRESS","SUPPLIER_INFO_ADDRESS_API.New__",addressblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ADDRESS_SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ADDRESS/DATA");
      data.setFieldItem("ADDRESS_COUNTRY",supplierInfoset.getValue("COUNTRY")); 
      addressset.addRow(data);

      trans.clear();
   }


   public void  saveReturnADDRESS()
   {
      ASPManager mgr = getASPManager();
      String colcomplex1;
      String[] recArray;
      ASPTransactionBuffer trans1 = null;

      isAddress1 = mgr.readValue("ADDRESS1");
      if (mgr.isEmpty(isAddress1))
         mgr.showError(mgr.translate("ENTERWSUPPLIERADDRESSFMHV: The field [Address1] must have a value."));

      else
      {
         addressset.changeRow();
         addressTypeset.changeRows();  
         headrowno = supplierInfoset.getCurrentRowNo();
         itemrowno = addressset.getCurrentRowNo();
         if ("New__".equals(addressset.getRowStatus()))
         {
            cmd = trans.addCustomCommand("COLCOMPLEX","Address_Type_Code_API.Enumerate_Type");
            cmd.addParameter("CLIENTVALUE","COLCOMPLEX/DATA");  
            cmd.addParameter("PARTY_TYPE_DB","SUPPLIER");

            trans = mgr.perform(trans);

            colcomplex =  trans.getValue("COLCOMPLEX/DATA/CLIENTVALUE");
            trans.clear();
            recArray = split(colcomplex, String.valueOf((char)31));
            arraySize = recArray.length;

            if (mgr.isEmpty(colcomplex))
            {
               addressset.next();
               colcomplex = "";
            }
            else
            {
               addressTypeset.clear();
               for (i=0; i<arraySize; ++i)
               {
                  cmd = trans.addEmptyCommand("ADDRESSTYPE"+i,"SUPPLIER_INFO_ADDRESS_TYPE_API.New__",addressTypeblk);
                  cmd.setOption("ACTION","PREPARE");
                  cmd.setParameter("ADDRESSTYPE_SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));
                  cmd.setParameter("ADDRESSTYPE_ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
                  cmd.setParameter("ADDRESSTYPE_ADDRESS_TYPE_CODE", recArray[i]);
                  cmd.setParameter("ADDRESSTYPE_DEFAULT_DOMAIN","FALSE");

                  if (addressset.countRows() == 1)
                     cmd.setParameter("ADDRESSTYPE_DEF_ADDRESS","TRUE");
                  else
                     cmd.setParameter("ADDRESSTYPE_DEF_ADDRESS","FALSE");

               }

               trans = mgr.perform(trans);

               for (i=0; i<arraySize; ++i)
               {
                  data = trans.getBuffer("ADDRESSTYPE"+i+"/DATA"); 
                  addressTypeset.addRow(data);
               }

               trans.clear(); 
               addressTypeset.first();

               for (i=0; i<arraySize; ++i)
               {
                  cmd = trans.addCustomFunction("COLCOMPLEX1"+i,"SUPPLIER_INFO_ADDRESS_TYPE_API.Default_Exist","CLIENTVALUE");
                  cmd.setParameter("ADDRESSTYPE_SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));
                  cmd.setParameter("ADDRESSTYPE_ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
                  cmd.setParameter("ADDRESSTYPE_ADDRESS_TYPE_CODE", recArray[i]);
                  addressTypeset.next();
               } 

               trans1 = mgr.perform(trans);
               addressTypeset.first();

               for (i=0; i<arraySize; ++i)
               {
                  data = addressTypeset.getRow();
                  colcomplex1 =  trans1.getValue("COLCOMPLEX1"+i+"/DATA/CLIENTVALUE");

                  if ( "FALSE".equals(colcomplex1) )
                     data.setValue("DEF_ADDRESS","TRUE");
                  else
                     data.setValue("DEF_ADDRESS","FALSE");

                  addressTypeset.setRow(data);
                  addressTypeset.next();
               }
            }
         }
         trans.clear();
         mgr.submit(trans); 
         supplierInfoset.goTo(headrowno);    
         addressset.goTo(itemrowno);   
      }  
   }


   public void  saveNewADDRESS()
   {
      saveReturnADDRESS();
      newRowADDRESS();
   }

//===========================
// COMMUNICATION METHOD BLOCK
//===========================

   public void  okFindCOMMETHOD()
   {
      ASPManager mgr = getASPManager();

      if ((addressset.countRows() != 0) &&  (supplierInfoset.countRows() != 0 ))
      {
         trans.clear();
         qry = trans.addQuery(comMethodblk);

         if (addresslay.isMultirowLayout())
         {
            qry.addWhereCondition("SUPPLIER_ID = ?");
            qry.addParameter("SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));
         }
         else
         {
            qry.addWhereCondition("SUPPLIER_ID = ? and ADDRESS_ID = ?");
            qry.addParameter("SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));
            qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
         }

         qry.includeMeta("ALL");
         headrowno = supplierInfoset.getCurrentRowNo();   
         itemrowno = addressset.getCurrentRowNo();
         mgr.submit(trans);
         supplierInfoset.goTo(headrowno);   
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
      cmd = trans.addEmptyCommand("COMMETHOD","SUPPLIER_INFO_COMM_METHOD_API.New__",comMethodblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("COMMETHOD_SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));
      cmd.setParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));    
      trans = mgr.perform(trans);
      data = trans.getBuffer("COMMETHOD/DATA");
      comMethodset.addRow(data);
   }


   public void saveReturnCOMMETHOD()
   {
      ASPManager mgr = getASPManager();

      String strvalidFrom, strvalidUntil, strvalDate = null;
      int curComMethodRow, intvalDate1, addressRow, supplierRow=0;                       
      comMethodset.changeRow();
      supplierRow = supplierInfoset.getCurrentRowNo();                            
      addressRow = addressset.getCurrentRowNo();                                                  
      curComMethodRow = comMethodset.getCurrentRowNo(); 

      strvalidFrom  = mgr.formatDate("VALID_FROM", comMethodset.getDateValue("VALID_FROM"));
      strvalidUntil = mgr.formatDate("VALID_TO", comMethodset.getDateValue("VALID_TO"));

      if (!mgr.isEmpty(strvalidFrom) && !mgr.isEmpty(strvalidUntil))
      {
         qry = trans.addQuery("VALDT","DUAL","(to_date(?,'YYYY-MM-DD-HH24.MI.SS') - to_date(?,'YYYY-MM-DD-HH24.MI.SS')) VALIDDATE","","");
         qry.addParameter("VALID_TO", strvalidUntil);
         qry.addParameter("VALID_FROM", strvalidFrom);
         trans = mgr.perform(trans);
         strvalDate = trans.getValue("VALDT/DATA/VALIDDATE");
         trans.clear();
      }

      if (strvalDate != null)
         intvalDate1 = toInt(strvalDate);
      else
         intvalDate1    = 0;

      if (intvalDate1 < 0 )
      {
         mgr.showError(mgr.translate("ENTERWSUPPLIERADDRESSDATECHK: Date of 'Valid To' should not be less than Date of 'Valid From'!"));
      }
      mgr.submit(trans);
      comMethodtbl.clearQueryRow();                                              
      supplierInfoset.goTo(supplierRow);                                
      addressset.goTo(addressRow);                                              
      comMethodset.goTo(curComMethodRow);

   }


   public void saveNewCOMMETHOD()
   {
      saveReturnCOMMETHOD();
      newRowCOMMETHOD();
   }


//===================
// ADDRESS TYPE BLOCK
//===================

   public void  okFindADDRESSTYPE()
   {
      ASPManager mgr = getASPManager();

      if ((addressset.countRows() != 0 ) &&  ( supplierInfoset.countRows() != 0 ))
      {
         trans.clear();
         qry = trans.addQuery(addressTypeblk);

         if (addresslay.isMultirowLayout() )
         {
            qry.addWhereCondition("SUPPLIER_ID = ?");
            qry.addParameter("SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));
         }
         else
         {
            qry.addWhereCondition("SUPPLIER_ID = ? and ADDRESS_ID = ?");
            qry.addParameter("SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));
            qry.addParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));
         }

         qry.includeMeta("ALL");
         headrowno = supplierInfoset.getCurrentRowNo();   
         itemrowno = addressset.getCurrentRowNo();
         mgr.submit(trans);
         supplierInfoset.goTo(headrowno);
         addressset.goTo(itemrowno);   
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
      cmd = trans.addEmptyCommand("ADDRESSTYPE","SUPPLIER_INFO_ADDRESS_TYPE_API.New__",addressTypeblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ADDRESSTYPE_SUPPLIER_ID", supplierInfoset.getValue("SUPPLIER_ID"));     
      cmd.setParameter("ADDRESS_ID", addressset.getValue("ADDRESS_ID"));     
      trans = mgr.perform(trans);
      data = trans.getBuffer("ADDRESSTYPE/DATA");
      addressTypeset.addRow(data);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  suppliergeneral()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow    = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);        
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"enterw/SupplierInfoGeneral.page",buff);
   }


   public void  supplierinvoice()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow    = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"invoiw/SupplierInvoice.page",buff);
   }


   public void  supplierpayment()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow    = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"paylew/SupplierPayment.page",buff);
   }


   public void  purchaseaddressinfo()
   {
      ASPManager mgr = getASPManager();

      if (supplierInfolay.isMultirowLayout())
         selectedRow = supplierInfoset.getRowSelected();
      else
         selectedRow    = supplierInfoset.getCurrentRowNo();

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
         selectedRow    = supplierInfoset.getCurrentRowNo();

      supplierInfoset.goTo(selectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("SUPPLIER_ID",supplierInfoset.getValue("SUPPLIER_ID"));
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"purchw/SupplierPurchase.page",buff);
   }

   public void  taxcodeinfo()
   {
      ASPManager mgr = getASPManager();

      if (addresslay.isMultirowLayout())
          selectedRow = addressset.getRowSelected();
      else
          selectedRow   = addressset.getCurrentRowNo();

      addressset.goTo(selectedRow);        
      buff= mgr.newASPBuffer();
      
      buff.addItem("SUPPLIER_ID",addressset.getRow().getValue("SUPPLIER_ID"));
      buff.addItem("ADDRESS_ID",addressset.getRow().getValue("ADDRESS_ID")); 
      mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"invoiw/TaxCodeInfo.page",buff);
     
              
   }


//-----------------------------------------------------------------------------
//----------------------------  DYNAMIC RMB FUNCTION  -------------------------
//-----------------------------------------------------------------------------

   public void  setDynamicRmb()
   {
      ASPManager mgr = getASPManager();

      if (!mgr.isPresentationObjectInstalled("paylew/SupplierPayment.page"))
         supplierInfobar.removeCustomCommand("supplierpayment");

      if (!mgr.isPresentationObjectInstalled("enterw/SupplierInfoGeneral.page"))
         supplierInfobar.removeCustomCommand("suppliergeneral");

      if (!mgr.isPresentationObjectInstalled("invoiw/SupplierInvoice.page"))
         supplierInfobar.removeCustomCommand("supplierinvoice");

      if (!mgr.isPresentationObjectInstalled("purchw/SupplierPurchaseAddress.page"))
         supplierInfobar.removeCustomCommand("purchaseaddressinfo");

      if (!mgr.isPresentationObjectInstalled("purchw/SupplierPurchase.page"))
         supplierInfobar.removeCustomCommand("purchaseinfo");

      if (!mgr.isPresentationObjectInstalled("invoiw/TaxCodeInfo.page"))
         addressbar.removeCustomCommand("taxcodeinfo");

   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      supplierInfoblk = mgr.newASPBlock("SUPPLIER");

      supplierInfoblk.addField("OBJID").
         setHidden();

      supplierInfoblk.addField("OBJVERSION").
         setHidden();

      supplierInfoblk.addField("SUPPLIER_ID").
         setSize(25).
         setLabel("ENTERWSUPPLIERADDRESSSUPPLIERID: Supplier Id.").
         setReadOnly().
         setInsertable().
         setUpperCase();

      supplierInfoblk.addField("NAME").
         setSize(25).
         setMandatory().
         setLabel("ENTERWSUPPLIERADDRESSNAME: Name");

      supplierInfoblk.addField("ASSOCIATION_NO").
         setSize(25).
         setLabel("ENTERWSUPPLIERADDRESSASSOCIATIONNO: Association No.");

      supplierInfoblk.addField("DEFAULT_LANGUAGE").
         setLabel("ENTERWSUPPLIERADDRESSDEFAULTLANGUAGE: Default Language").
         setSelectBox().
         enumerateValues("ISO_LANGUAGE_API").
         unsetSearchOnDbColumn();

      supplierInfoblk.addField("COUNTRY").
         setLabel("ENTERWSUPPLIERADDRESSCOUNTRY: Country").
         setSelectBox().
         enumerateValues("ISO_COUNTRY_API").
         unsetSearchOnDbColumn();

      supplierInfoblk.addField("CREATION_DATE","Date").
         setSize(25).
         setLabel("ENTERWSUPPLIERADDRESSCREATIONDATE: Creation Date").
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

      supplierInfoblk.addField("CLIENTVALUE").
         setHidden().
         setFunction("' '");

      supplierInfoblk.setView("SUPPLIER_INFO");
      supplierInfoblk.defineCommand("SUPPLIER_INFO_API","New__,Modify__,Remove__");
      supplierInfoset = supplierInfoblk.getASPRowSet();

      supplierInfotbl = mgr.newASPTable(supplierInfoblk);
      supplierInfotbl.setTitle(mgr.translate("ENTERWSUPPLIERADDRESSAITBL: Address Info"));

      supplierInfobar = mgr.newASPCommandBar(supplierInfoblk);
      supplierInfobar.disableCommand(supplierInfobar.DUPLICATEROW);      
      supplierInfobar.addCustomCommand("suppliergeneral","General Info...");
      supplierInfobar.addCustomCommand("supplierinvoice","Invoice Info...");
      supplierInfobar.addCustomCommand("supplierpayment","Payment Info...");
      supplierInfobar.addCustomCommand("purchaseaddressinfo","Purchase Address Info...");
      supplierInfobar.addCustomCommand("purchaseinfo","Purchase Info...");
       
      supplierInfobar.disableCommand(supplierInfobar.DELETE);
      supplierInfobar.disableCommand(supplierInfobar.NEWROW);
      supplierInfobar.disableCommand(supplierInfobar.DUPLICATEROW);

      supplierInfolay = supplierInfoblk.getASPBlockLayout();    
      supplierInfolay.setDefaultLayoutMode(supplierInfolay.SINGLE_LAYOUT);    

      addressblk = mgr.newASPBlock("ADDRESS");

      addressblk.addField("ADDRESS_OBJID").
         setHidden().
         setDbName("OBJID");

      addressblk.addField("ADDRESS_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      addressblk.addField("ADDRESS_ID").
         setSize(25).
         setMandatory().
         setLabel("ENTERWSUPPLIERADDRESSADDRESSID: Address Identity").
         setReadOnly().
         setUpperCase().                              
         setInsertable();

      addressblk.addField("EAN_LOCATION").
         setSize(25).
         setLabel("ENTERWSUPPLIERADDRESSEANLOCATION: Suppliers Own Address Id."); 

      addressblk.addField("ADDRESS_COUNTRY1").
                 setSize(25).
                 setMandatory().
                 setLabel("ENTERWSUPPLIERADDRESSADDRESSCOUNTRY: Country").
                 setDbName("COUNTRY").
                 setSelectBox().
                 enumerateValues("ISO_COUNTRY_API").
                 setCustomValidation("ADDRESS_COUNTRY1","COUNTRY_CODE").                
                 unsetSearchOnDbColumn();
      
      fCOUNTRY = addressblk.addField("ADDRESS_COUNTRY").
                 setSize(25).
                 setMandatory().
                 setLabel("ENTERWSUPPLIERADDRESSADDRESSCOUNTRY: Country").
                 setFunction("COUNTRY");


      addressblk.addField("ADDRESS").
         setSize(25).
         setLabel("ENTERWSUPPLIERADDRESSADDRESS: Address").
         setHidden();

      fADDRESS1 = addressblk.addField("ADDRESS1").
                  setSize(25).
                  setLabel("ENTERWSUPPLIERADDRESSADDRESS1: Address1"); 

      fADDRESS2 = addressblk.addField("ADDRESS2").
                  setSize(25).
                  setLabel("ENTERWSUPPLIERADDRESSADDRESS2: Address2");   

      fZIP_CODE = addressblk.addField("ZIP_CODE").
                  setSize(25).
                  setLabel("ENTERWSUPPLIERADDRESSZIPCODE: Postal Code");    

      fCITY = addressblk.addField("CITY").
              setSize(25).
              setLabel("ENTERWSUPPLIERADDRESSCITY: City");    

      fCOUNTY = addressblk.addField("COUNTY").
                setSize(25).
                setLabel("ENTERWSUPPLIERADDRESSCOUNTY: County");  

      fSTATE = addressblk.addField("STATE").
               setSize(25).
               setLabel("ENTERWSUPPLIERADDRESSSTATE: State").
               setDynamicLOV("STATE_CODES",650,450);                     

      fCOUNTRY_CODE = addressblk.addField("COUNTRY_CODE").
                      setSize(25).
                      unsetSearchOnDbColumn().
                      setLabel("ENTERWSUPPLIERADDRESSCOUNTRYCODE: Country Code").
                      setCustomValidation("COUNTRY_CODE","ADDRESS_COUNTRY,ADDRESS_COUNTRY1").
                      setFunction("ISO_COUNTRY_API.encode(:COUNTRY)");   

      addressblk.addField("VALID_FROM","Date").
         setSize(25).
         setLabel("ENTERWSUPPLIERADDRESSVALIDFROM: Valid From");

      addressblk.addField("VALID_TO","Date").
         setSize(25).
         setLabel("ENTERWSUPPLIERADDRESSVALIDTO: Valid To");

      addressblk.addField("ADDRESS_SUPPLIER_ID").
         setMandatory().
         setHidden().
         setDbName("SUPPLIER_ID").
         setUpperCase();

      addressblk.addField("ADDRESS_PARTY").
         setHidden().
         setDbName("PARTY");

      addressblk.addField("ADDRESS_DEFAULT_DOMAIN").
         setMandatory().
         setHidden().
         setDbName("DEFAULT_DOMAIN");

      addressblk.addField("ADDRESS_PARTY_TYPE").
         setMandatory().
         setHidden().
         setDbName("PARTY_TYPE");

      addressblk.setView("SUPPLIER_INFO_ADDRESS");
      addressblk.defineCommand("SUPPLIER_INFO_ADDRESS_API","New__,Modify__,Remove__");
      addressblk.setMasterBlock(supplierInfoblk);    
      addressblk.setTitle(mgr.translate("ENTERWSUPPLIERADDRESSADDRINFO: Address"));     
      addressset = addressblk.getASPRowSet();

      addressbar = mgr.newASPCommandBar(addressblk);
      addressbar.disableCommand(addressbar.DUPLICATEROW);
     
      addressbar.addCustomCommand("taxcodeinfo",mgr.translate("ENTERWSUPPLIERADDRESSTAXINFO: Tax Code Info..."));

      addressbar.defineCommand(addressbar.OKFIND,"okFindADDRESS");
      addressbar.defineCommand(addressbar.COUNTFIND,"countFindADDRESS");
      addressbar.defineCommand(addressbar.NEWROW,"newRowADDRESS");    
      addressbar.defineCommand(addressbar.SAVERETURN,"saveReturnADDRESS");   
      addressbar.defineCommand(addressbar.SAVENEW,"saveNewADDRESS");  
      
      addresstbl = mgr.newASPTable(addressblk);
      addresstbl.setTitle(mgr.translate("ENTERWSUPPLIERADDRESSADDRINFOTBL: Address"));

      addresslay = addressblk.getASPBlockLayout();    
      addresslay.setDefaultLayoutMode(addresslay.SINGLE_LAYOUT);  
      
      addresslay.setAddressFieldList(fADDRESS1,fADDRESS2,fZIP_CODE,fCITY,fSTATE,fCOUNTY,fCOUNTRY_CODE,fCOUNTRY,"ENTERWSUPPLIERADDRESSRESSADD: Address","ifs.enterw.LocalizedEnterwAddress");  

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
         setLabel("ENTERWSUPPLIERADDRESSCOMMETHODNAME: Name").
         setDbName("NAME");

      comMethodblk.addField("COMMETHOD_DESCRIPTION").
         setSize(11).
         setLabel("ENTERWSUPPLIERADDRESSCOMMETHODDESCRIPTION: Description").
         setDbName("DESCRIPTION");

      comMethodblk.addField("COMMETHOD_METHOD_ID").
         setSize(15).
         setLabel("ENTERWSUPPLIERADDRESSCOMMETHODMETHODID: Comm. Method").
         setDbName("METHOD_ID").
         setSelectBox().
         enumerateValues("COMM_METHOD_CODE_API").
         unsetSearchOnDbColumn();

      comMethodblk.addField("COMMETHOD_SUPPLIER_ID").
         setMandatory().
         setHidden().
         setDbName("SUPPLIER_ID").
         setUpperCase();

      comMethodblk.addField("COMMETHOD_VALUE").
         setSize(11).
         setMandatory().
         setLabel("ENTERWSUPPLIERADDRESSCOMMETHODVALUE: Value").
         setDbName("VALUE");

      comMethodblk.addField("COMMETHOD_ADDRESS_ID").
         setDbName("ADDRESS_ID").
         setUpperCase().
         setHidden();

      comMethodblk.addField("COMMETHOD_ADDRESS_DEFAULT").
         setSize(16).
         setLabel("ENTERWSUPPLIERADDRESSCOMMETHODADDRESSDEFAULT: Address Default").
         setDbName("ADDRESS_DEFAULT").
         setCheckBox("FALSE,TRUE");

      comMethodblk.addField("COMMETHOD_METHOD_DEFAULT").
         setSize(15).
         setLabel("ENTERWSUPPLIERADDRESSCOMMETHODMETHODDEFAULT: Method Default").
         setDbName("METHOD_DEFAULT").
         setCheckBox("FALSE,TRUE");

      comMethodblk.addField("DOCUMENT_RECEIVER").
         setSize(15).
         setMandatory().
         setLabel("ENTERWSUPPLIERADDRESSCOMDOCUMENTRECEIVER: Document Receiver").
         setCheckBox("FALSE,TRUE");

      comMethodblk.addField("COMMETHOD_VALID_FROM","Date").
         setSize(11).
         setLabel("ENTERWSUPPLIERADDRESSCOMMETHODVALIDFROM: Valid From").
         setDbName("VALID_FROM");

      comMethodblk.addField("COMMETHOD_VALID_TO","Date").
         setSize(11).
         setLabel("ENTERWSUPPLIERADDRESSCOMMETHODVALIDTO: Valid To").
         setDbName("VALID_TO");

      comMethodblk.setView("SUPPLIER_INFO_COMM_METHOD");
      comMethodblk.defineCommand("SUPPLIER_INFO_COMM_METHOD_API","New__,Modify__,Remove__");
      comMethodblk.setMasterBlock(addressblk);     
      comMethodblk.setTitle(mgr.translate("ENTERWSUPPLIERADDRESSCOMMETH: Communication Method"));    
      comMethodset = comMethodblk.getASPRowSet();

      comMethodbar = mgr.newASPCommandBar(comMethodblk);
      comMethodbar.disableCommand(comMethodbar.DUPLICATEROW);

      comMethodbar.defineCommand(comMethodbar.OKFIND,"okFindCOMMETHOD");
      comMethodbar.defineCommand(comMethodbar.COUNTFIND,"countFindCOMMETHOD");
      comMethodbar.defineCommand(comMethodbar.NEWROW,"newRowCOMMETHOD");    
      comMethodbar.defineCommand(comMethodbar.SAVERETURN,"saveReturnCOMMETHOD");
      comMethodbar.defineCommand(comMethodbar.SAVENEW,"saveNewCOMMETHOD");

      comMethodtbl = mgr.newASPTable(comMethodblk);
      comMethodtbl.setTitle(mgr.translate("ENTERWSUPPLIERADDRESSCOMMETHTBLN: Communication Method"));

      comMethodlay = comMethodblk.getASPBlockLayout();    
      comMethodlay.setDefaultLayoutMode(comMethodlay.MULTIROW_LAYOUT);   

      addressTypeblk = mgr.newASPBlock("ADDRESSTYPE");

      addressTypeblk.addField("ADDRESSTYPE_OBJID").
         setHidden().
         setDbName("OBJID");

      addressTypeblk.addField("ADDRESSTYPE_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      addressTypeblk.addField("ADDRESSTYPE_SUPPLIER_ID").
         setMandatory().
         setHidden().
         setDbName("SUPPLIER_ID");
   
      addressTypeblk.addField("ADDRESSTYPE_PARTY").
         setHidden().
         setDbName("PARTY");

      addressTypeblk.addField("ADDRESSTYPE_DEFAULT_DOMAIN").
         setHidden().
         setDbName("DEFAULT_DOMAIN");

      addressTypeblk.addField("ADDRESSTYPE_ADDRESS_ID").
         setHidden().
         setDbName("ADDRESS_ID");

      addressTypeblk.addField("ADDRESSTYPE_ADDRESS_TYPE_CODE").
         setSize(15).
         setLabel("ENTERWSUPPLIERADDRESSADDRESSTYPEADDRESSTYPECODE: Address Type").
         setDbName("ADDRESS_TYPE_CODE").
         setSelectBox().
         enumerateValues("ADDRESS_TYPE_CODE_API").
         unsetSearchOnDbColumn();

      addressTypeblk.addField("ADDRESSTYPE_DEF_ADDRESS").
         setSize(14).
         setMandatory().
         setLabel("ENTERWSUPPLIERADDRESSADDRESSTYPEDEFADDRESS: Default").
         setDbName("DEF_ADDRESS").
         setCheckBox("FALSE,TRUE");

      addressTypeblk.setView("SUPPLIER_INFO_ADDRESS_TYPE");
      addressTypeblk.defineCommand("SUPPLIER_INFO_ADDRESS_TYPE_API","New__,Modify__,Remove__");
      addressTypeblk.setMasterBlock(addressblk);  
      addressTypeblk.setTitle(mgr.translate("ENTERWSUPPLIERADDRESSADDRTYPE: Address Type"));      
      addressTypeset = addressTypeblk.getASPRowSet();

      addressTypebar = mgr.newASPCommandBar(addressTypeblk);
      addressTypebar.disableCommand(addressTypebar.DUPLICATEROW);

      addressTypebar.defineCommand(addressTypebar.OKFIND,"okFindADDRESSTYPE");
      addressTypebar.defineCommand(addressTypebar.COUNTFIND,"countFindADDRESSTYPE");
      addressTypebar.defineCommand(addressTypebar.NEWROW,"newRowADDRESSTYPE");    

      addressTypetbl = mgr.newASPTable(addressTypeblk);
      addressTypetbl.setTitle(mgr.translate("ENTERWSUPPLIERADDRESSADDRTYPETBLN: Address Type"));

      addressTypelay = addressTypeblk.getASPBlockLayout();    
      addressTypelay.setDefaultLayoutMode(addressTypelay.MULTIROW_LAYOUT); 
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      String country_code = null;

      setDynamicRmb();
      mgr.setPageNonExpiring(); 

      if (supplierInfoset.countRows() == 0)
      {
         supplierInfobar.disableCommand(supplierInfobar.FORWARD);
         supplierInfobar.disableCommand(supplierInfobar.BACKWARD);
         supplierInfobar.disableCommand(supplierInfobar.BACK);

         supplierInfobar.disableCommand(supplierInfobar.EDITROW);

         supplierInfobar.disableCustomCommand("suppliergeneral");
         supplierInfobar.disableCustomCommand("supplierinvoice");
         supplierInfobar.disableCustomCommand("supplierpayment");
         supplierInfobar.disableCustomCommand("purchaseaddressinfo");
         supplierInfobar.disableCustomCommand("purchaseinfo");
      }
      if (addressset.countRows() != 0 )
      {
          country_code =  addressset.getValue("COUNTRY_CODE");
          if(!("AR".equals(country_code)))
              addressbar.disableCustomCommand("taxcodeinfo");
              
      }
               
   }


//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "ENTERWSUPPLIERADDRESSTITLE: Supplier - Address Info";
   }

   protected String getTitle()
   {
      return "ENTERWSUPPLIERADDRESSTITLE: Supplier - Address Info";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("ENTERWSUPPLIERADDRESSTITLE: Supplier - Address Info"));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("ENTERWSUPPLIERADDRESSTITLE: Supplier - Address Info"));

      if (supplierInfolay.isVisible())
         out.append(supplierInfolay.show());

      if (addresslay.isVisible() && supplierInfoset.countRows()>0)
         out.append(addresslay.show());

      if ((comMethodlay.isVisible() && addressset.countRows()!=0 && addresslay.isVisible()) || comMethodlay.isNewLayout() || comMethodlay.isEditLayout())
         out.append(comMethodlay.show());

      if (addressTypelay.isVisible() && (addressset.countRows()!=0 && addresslay.isVisible()) || addressTypelay.isNewLayout() || addressTypelay.isEditLayout())
         out.append(addressTypelay.show());

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");

      return out;
   }

}
