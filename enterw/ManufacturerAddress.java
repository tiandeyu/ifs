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
*  File        : ManufacturerAddress.java 
*  Modified    :
*    ASP2JAVA Tool  2001-01-23  - Created Using the ASP file ManufacturerAddress.asp
*    Disali K. : 2001-02-09 - Made the file compilable after converting to Java
*    Disali K  : 2001-03-22 - Modifications for new webkit
*    Disali K  : 2001-03-23 - Added the field Name (Call Id: 63600)
*                           - Modified the Title (Call Id: 63601)
*    Disali K  : 2001-01-24 - Added itemset1.clear() to saveNewITEM0
*    Disali K  : 2001-01-25 - Removed itemset1.clear() from saveNewItem0 and added
*                             it to newRowITEM0();
*                           - Changed mgr.getQueryString to mgr.getQueryStringValue
*
*    Madhu    : 2001-09-10 - Call # 68588 
*    Kumudu   : 2002-01-18 -  IID 10997 .Added LOV view for State column.
*    Anpalk   : 03/07/2002	-	Call Id 29991
*    MAWELK   : 2005-04-09   FIPR364 - Corrected web tags.
*    Jakalk   : 2005-09-07 - Code Cleanup, Removed doReset and clone methods.
*    Maselk   : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;
import java.io.*;


public class ManufacturerAddress extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.ManufacturerAddress");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
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
   private ASPBlock itemblk2;
   private ASPRowSet itemset2;
   private ASPCommandBar itembar2;
   private ASPTable itemtbl2;
   private ASPBlockLayout itemlay2;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private int intArraySize;
   private ASPCommand cmd;

   private ASPField fCOUNTRY;
   private ASPField fADDRESS1;
   private ASPField fADDRESS2;
   private ASPField fZIP_CODE;
   private ASPField fCITY;
   private ASPField fCOUNTRY_CODE;
   private ASPField fSTATE;   
   private ASPField fCOUNTY;

   //===============================================================
   // Construction 
   //===============================================================
   public ManufacturerAddress(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();      
      trans = mgr.newASPTransactionBuffer();   
      intArraySize = ctx.readNumber("ARRAYSIZE",0);    

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();

      adjust();

      ctx.writeNumber("ARRAYSIZE",intArraySize);         
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String percentage = null;
      String description = null;
      String val = null;

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

      if("ITEM0_COUNTRY".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("ISOCODE","ISO_COUNTRY_API.encode", "ADDRESS_COUNTRY" );
         cmd.addParameter("ITEM0_COUNTRY");   

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
      ASPQuery q ;
      itemset0.clear();
      itemset1.clear();
      itemset2.clear();   
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");

      if ( mgr.dataTransfered() )
      {
         q.addOrCondition(mgr.getTransferedData());
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);  
      }

      mgr.querySubmit(trans,headblk);
      mgr.createSearchURL(headblk);

      if (  headset.countRows() == 0 )
      {
         headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
         mgr.showAlert(mgr.translate("ENTERWMANUFACTURERADDRESSNODATAFOUND: No data found.")); 
         headset.clear();
      }
      else if ( headset.countRows() < headset.countDbRows() )
      {
         mgr.showAlert(mgr.translate( "ENTERWMANUFACTURERADDRESSMOREDATA: You have fetched the first &1 of &2 lines.",
                                      String.valueOf(headset.countRows()),String.valueOf(headset.countDbRows())));     
      }
      else
         okFindITEM0();
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = null;
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }

//=============
// ITEMS BLOCK0
//=============

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q = null;

      if ( headset.countRows() != 0 )
      {
         itemset1.clear();
         itemset2.clear();
         trans.clear();
         q = trans.addQuery(itemblk0);
         q.addWhereCondition("MANUFACTURER_ID = ?");   
         q.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
         q.includeMeta("ALL");
         int intheadrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(intheadrowno);  
      }
      okFindITEM1();
      okFindITEM2();
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer data =null;
      ASPTransactionBuffer trans1 = null;
      int f = 0;
      String strcolcomplex,strrec,colcomplex1 = null;
      String[] recArray;

      trans.clear();

      cmd = trans.addEmptyCommand("ITEM0","MANUFACTURER_INFO_ADDRESS_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));

      cmd = trans.addCustomCommand("COLCOMPLEX","Address_Type_Code_API.Enumerate_Type");
      cmd.addParameter("CLIENTVALUE");  
      cmd.addParameter("PARTY_TYPE_DB",headset.getRow().getValue("PARTY_TYPE_DB"));

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_COUNTRY",headset.getValue("COUNTRY"));   
      itemset0.addRow(data);  

      strcolcomplex =  trans.getValue("COLCOMPLEX/DATA/CLIENTVALUE");   
      strrec = ((char)(31)+"");
      recArray = split(strcolcomplex,strrec);
      intArraySize=recArray.length;

      if (mgr.isEmpty(strcolcomplex))
      {
         itemset0.next();
         strcolcomplex = "";
      }
      else
      {
         itemset1.clear();
         trans.clear();

         for (int i=0; i<intArraySize; ++i)
         {
            cmd = trans.addCustomFunction("COLCOMPLEX1"+i,"Manuf_Info_Addr_Type_API.Default_Exist","CLIENTVALUE");
            cmd.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));  
            cmd.addParameter("ADDRESS_ID","1");
            cmd.addParameter("ADDRESS_TYPE_CODE",recArray[i]);  
         }

         trans1 = mgr.perform(trans);
         trans.clear();

         for (int i=0; i<intArraySize; ++i)
         {
            colcomplex1 =  trans1.getValue("COLCOMPLEX1"+i+"/DATA/CLIENTVALUE");

            cmd = trans.addEmptyCommand("ITEM1"+i,"MANUF_INFO_ADDR_TYPE_API.New__",itemblk1);
            cmd.setOption("ACTION","PREPARE");
            cmd.setParameter("ITEM1_MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
            cmd.setParameter("ITEM1_ADDRESS_ID", "1");
            cmd.setParameter("ADDRESS_TYPE_CODE",recArray[i]);
            cmd.setParameter("ITEM1_DEFAULT_DOMAIN","FALSE");

            if ( "FALSE".equals(colcomplex1) )
               cmd.setParameter("DEF_ADDRESS","TRUE");
            else
               cmd.setParameter("DEF_ADDRESS","FALSE");
         }

         trans = mgr.perform(trans);

         for (int i=0; i<intArraySize; ++i)
         {
            data = trans.getBuffer("ITEM1"+i+"/DATA"); 
            itemset1.addRow(data);
         }
      }      
   }


   public void  saveReturnITEM0()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer data =null;
      ASPTransactionBuffer trans1 = null;
      String colcomplex1;

      itemset0.changeRow();
      itemset1.changeRows();  
      int intheadrowno = headset.getCurrentRowNo();
      int intitemrowno = itemset0.getCurrentRowNo();

      if ( "New__".equals(itemset0.getRowStatus()) )
      {
         itemset1.first();

         for (int i=0; i<intArraySize; ++i)
         {
            cmd = trans.addCustomFunction("COLCOMPLEX1"+i,"Manuf_Info_Addr_Type_API.Default_Exist","CLIENTVALUE");
            cmd.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));  
            cmd.addParameter("ADDRESS_ID","1");
            cmd.addParameter("ADDRESS_TYPE_CODE",itemset1.getValue("ADDRESS_TYPE_CODE")); 
            itemset1.next(); 
         }

         trans1 = mgr.perform(trans);
         trans.clear();

         itemset1.first();

         for (int i=0;i<intArraySize;++i)
         {
            data = itemset1.getRow();
            colcomplex1 =  trans1.getValue("COLCOMPLEX1"+i+"/DATA/CLIENTVALUE");
            data.setFieldItem("ITEM1_ADDRESS_ID",itemset0.getValue("ADDRESS_ID"));

            if ( "FALSE".equals(colcomplex1) )
               data.setValue("DEF_ADDRESS","TRUE");
            else
               data.setValue("DEF_ADDRESS","FALSE");

            itemset1.setRow(data);
            itemset1.next();
         }
      }

      mgr.submit(trans); 
      headset.goTo(intheadrowno);    
      itemset0.goTo(intitemrowno);     
   }


   public void  saveNewITEM0()
   {
      saveReturnITEM0();
      newRowITEM0();
   }

//=============
// ITEMS BLOCK1
//=============

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q = null;
      if ( itemset0.countRows() != 0 )
      {
         trans.clear();
         q = trans.addQuery(itemblk1);

         if (itemlay0.isMultirowLayout() )
         {
            q.addWhereCondition("MANUFACTURER_ID = ?");
            q.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
         }
         else
         {
            q.addWhereCondition("MANUFACTURER_ID = ? and ADDRESS_ID = ?");
            q.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
            q.addParameter("ADDRESS_ID", itemset0.getValue("ADDRESS_ID"));
         }

         q.includeMeta("ALL");
         int intheadrowno = headset.getCurrentRowNo();
         int intitemrowno = itemset0.getCurrentRowNo();   
         mgr.submit(trans);
         headset.goTo(intheadrowno);  
         itemset0.goTo(intitemrowno);     
      }
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer data =null;

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","MANUF_INFO_ADDR_TYPE_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
      cmd.setParameter("ADDRESS_ID", itemset0.getValue("ADDRESS_ID"));      
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }

//=============
// ITEMS BLOCK2
//=============

   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q = null;

      if ( itemset0.countRows() != 0 )
      {
         trans.clear();
         q = trans.addQuery(itemblk2);

         if (itemlay0.isMultirowLayout() )
         {
            q.addWhereCondition("MANUFACTURER_ID = ?");
            q.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
         }
         else
         {
            q.addWhereCondition("MANUFACTURER_ID = ? and ADDRESS_ID = ?");
            q.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
            q.addParameter("ADDRESS_ID", itemset0.getValue("ADDRESS_ID"));
         }

         q.includeMeta("ALL");
         int intheadrowno = headset.getCurrentRowNo();
         int intitemrowno = itemset0.getCurrentRowNo();   
         mgr.submit(trans);
         headset.goTo(intheadrowno);  
         itemset0.goTo(intitemrowno);   
      }
   }


   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer data =null;
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM2","MANUF_INFO_COMM_METHOD_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
      cmd.setParameter("ADDRESS_ID", itemset0.getValue("ADDRESS_ID"));        
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      itemset2.addRow(data);
   }


   public void saveReturnITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q = null;

      String validFrom, validUntil, valDate = null;
      int curComMethodRow, curAddRow, currHeadRow, intvalDate1 =0; 

      itemset2.changeRow();
      currHeadRow = headset.getCurrentRowNo();
      curAddRow = itemset0.getCurrentRowNo();
      curComMethodRow = itemset2.getCurrentRowNo(); 

      validFrom  = mgr.formatDate("VALID_FROM", itemset2.getDateValue("VALID_FROM"));
      validUntil = mgr.formatDate("VALID_TO", itemset2.getDateValue("VALID_TO"));

      if ((!mgr.isEmpty(validFrom)) && (!mgr.isEmpty(validUntil)))
      {
         q = trans.addQuery("VALDT","DUAL","(to_date(?,'yyyy-mm-dd') - to_date(?,'yyyy-mm-dd')) VALIDDATE","","");
         q.addParameter("VALID_TO", validUntil);
         q.addParameter("VALID_FROM", validFrom);
         trans = mgr.perform(trans);
         valDate = trans.getValue("VALDT/DATA/VALIDDATE");
         trans.clear();
      }

      if (valDate != null)
         intvalDate1 = toInt(valDate);
      else
         intvalDate1	= 0;

      if (intvalDate1 < 0 )
         mgr.showError(mgr.translate("ENTERWMANUFACTURERADDRESSDATECHK: Date of 'Valid To' should not be less than Date of 'Valid From'!"));

      mgr.submit(trans);
      itemtbl2.clearQueryRow();
      headset.goTo(currHeadRow);
      itemset0.goTo(curAddRow); 
      itemset2.goTo(curComMethodRow);        
   }


   public void saveNewITEM2()
   {
      saveReturnITEM2();
      newRowITEM2();    
   }     

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  manufacturerInfo()
   {
      ASPManager mgr = getASPManager();

      int intSelectedRow = 0;
      ASPBuffer buff, row = null;

      if (headlay.isMultirowLayout())
         intSelectedRow = headset.getRowSelected();
      else
         intSelectedRow	= headset.getCurrentRowNo();

      headset.goTo(intSelectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("MANUFACTURER_ID",headset.getValue("MANUFACTURER_ID"));

      mgr.transferDataTo("ManufacturerInfo.page",buff);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").
         setHidden();

      headblk.addField("MANUFACTURER_ID").
         setSize(25).
         setMandatory().
         setLabel("ENTERWMANUFACTURERADDRESSMANUFACTURERID: Manufacturer Id").
         setUpperCase().
         setReadOnly();

      headblk.addField("NAME").
         setSize(25).
         setMandatory().
         setLabel("ENTERWMANUFACTURERADDRESSNAME: Name");

      headblk.addField("ASSOCIATION_NO").
         setSize(25).
         setLabel("ENTERWMANUFACTURERADDRESSASSOCIATIONNO: Association No.");

      headblk.addField("DEFAULT_LANGUAGE").
         enumerateValues("ISO_LANGUAGE_API").
         setSelectBox().
         setSize(25).
         setLabel("ENTERWMANUFACTURERADDRESSDEFAULTLANGUAGE: Default Language");

      headblk.addField("COUNTRY").
         enumerateValues("ISO_COUNTRY_API").
         setSelectBox().
         setSize(25).
         setLabel("ENTERWMANUFACTURERADDRESSCOUNTRY: Country");

      headblk.addField("CREATION_DATE","Date").
         setSize(25).
         setLabel("ENTERWMANUFACTURERADDRESSCREATIONDATE: Creation Date");

      headblk.addField("DEFAULT_DOMAIN").
         setSize(14).
         setMandatory().
         setHidden().
         setLabel("ENTERWMANUFACTURERADDRESSDEFAULTDOMAIN: Default Domain");

      headblk.addField("PARTY_TYPE").
         setSize(11).
         setMandatory().
         setHidden();

      headblk.addField("PARTY_TYPE_DB").
         setSize(11).
         setMandatory().
         setHidden();           

      headblk.addField("CLIENTVALUE").
         setHidden().
         setFunction("' '");  

      headblk.setView("MANUFACTURER_INFO");
      headblk.defineCommand("MANUFACTURER_INFO_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet(); 

      headtbl = mgr.newASPTable(headblk);

      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("ManufacturerInfo",mgr.translate("ENTERWMANUFACTURERADDRESSMANUFACT: General Info...")); 
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.NEWROW);

      headlay = headblk.getASPBlockLayout();   
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);        

      //=============================================
      //This Blocks refers to Adress Tab
      //=============================================

      itemblk0 = mgr.newASPBlock("ITEM0");

      itemblk0.addField("ITEM0_OBJID").
         setHidden().
         setDbName("OBJID");

      itemblk0.addField("ITEM0_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      itemblk0.addField("ADDRESS_ID").
         setSize(29).
         setMandatory().
         setLabel("ENTERWMANUFACTURERADDRESSADDRESSID: Address Identity").
         setUpperCase().
         setReadOnly().
         setInsertable();

      itemblk0.addField("EAN_LOCATION").
         setSize(25).
         setLabel("ENTERWMANUFACTURERADDRESSEANLOCATION: Manufacturers Own Address Id.");

      itemblk0.addField("ITEM0_COUNTRY").
         setSize(29).
         setMandatory().
         setLabel("ENTERWMANUFACTURERADDRESSITEM0COUNTRY: Country").
         enumerateValues("ISO_COUNTRY_API").
         setSelectBox().
         setCustomValidation("ITEM0_COUNTRY","COUNTRY_CODE").
         setDbName("COUNTRY");
      
      fCOUNTRY = itemblk0.addField("ADDRESS_COUNTRY").
                 setSize(25).
                 setMandatory().
                 setLabel("ENTERWMANUFACTURERADDRESSITEM0COUNTRY: Country").
                 setFunction("COUNTRY");

      fADDRESS1 = itemblk0.addField("ADDRESS1").
                  setSize(29).
                  setLabel("ENTERWMANUFACTURERADDRESSADDRESS1: Address1"); 

      fADDRESS2 = itemblk0.addField("ADDRESS2").
                  setSize(25).
                  setLabel("ENTERWMANUFACTURERADDRESSADDRESS2: Address2");   

      fZIP_CODE = itemblk0.addField("ZIP_CODE").
                  setSize(29).
                  setLabel("ENTERWMANUFACTURERADDRESSZIPCODE: Postal Code");    

      fCITY = itemblk0.addField("CITY").
              setSize(25).
              setLabel("ENTERWMANUFACTURERADDRESSCITY: City");  

      fCOUNTY = itemblk0.addField("COUNTY").
                setSize(25).
                setLabel("ENTERWMANUFACTURERADDRESSCOUNTY: County");                

      fSTATE = itemblk0.addField("STATE").
               setSize(29).
               setLabel("ENTERWMANUFACTURERADDRESSSTATE: State").
               setDynamicLOV("STATE_CODES",650,450);
      
      fCOUNTRY_CODE = itemblk0.addField("COUNTRY_CODE").
                      setSize(25).
                      setMandatory().
                      unsetSearchOnDbColumn().
                      setLabel("ENTERWMANUFACTURERADDRESSCOUNTRYCODE: Country Code").
                      setCustomValidation("COUNTRY_CODE","ADDRESS_COUNTRY,ITEM0_COUNTRY").
                      setFunction("ISO_COUNTRY_API.encode(:COUNTRY)");

      itemblk0.addField("VALID_FROM","Date").
         setSize(29).
         setLabel("ENTERWMANUFACTURERADDRESSVALIDFROM: Valid From");

      itemblk0.addField("VALID_TO","Date").
         setSize(25).
         setLabel("ENTERWMANUFACTURERADDRESSVALIDTO: Valid To");

      itemblk0.addField("ITEM0_MANUFACTURER_ID").
         setSize(0).
         setLOV("ManufacturerInfoLov.page",600,445).
         setMandatory().
         setHidden().
         setDbName("MANUFACTURER_ID").
         setUpperCase();

      itemblk0.addField("ITEM0_DEFAULT_DOMAIN").
         setSize(5).
         setMandatory().
         setHidden().
         setLabel("ENTERWMANUFACTURERADDRESSITEM0DEFAULTDOMAIN: Default Domain").
         setDbName("DEFAULT_DOMAIN").
         setCheckBox("FALSE,TRUE");

      itemblk0.addField("ITEM0_PARTY_TYPE").
         setSize(14).
         setMandatory(). 
         setHidden().
         setDbName("PARTY_TYPE");

      itemblk0.setView("MANUFACTURER_INFO_ADDRESS");
      itemblk0.defineCommand("MANUFACTURER_INFO_ADDRESS_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);  
      itemblk0.setTitle(mgr.translate("ENTERWMANUFACTURERADDRESSADDRINFO: Address"));        
      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.disableCommand(itembar0.DUPLICATEROW);   
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");    
      itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0");   
      itembar0.defineCommand(itembar0.SAVENEW,"saveNewITEM0");     

      itemtbl0 = mgr.newASPTable(itemblk0);

      itemlay0 = itemblk0.getASPBlockLayout();   
      itemlay0.setDefaultLayoutMode(itemlay0.SINGLE_LAYOUT); 
      
      itemlay0.setAddressFieldList(fADDRESS1,fADDRESS2,fZIP_CODE,fCITY,fSTATE,fCOUNTY,fCOUNTRY_CODE,fCOUNTRY,"ENTERWMANUFACTURERADDRESSADD: Address","ifs.enterw.LocalizedEnterwAddress");  
      

      //=============================================
      //This Blocks refers to HyperLink Adress Type
      //==============================================

      itemblk1 = mgr.newASPBlock("ITEM1");

      itemblk1.addField("ITEM01_OBJID").
         setHidden().
         setDbName("OBJID");

      itemblk1.addField("ITEM01_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      itemblk1.addField("ITEM1_MANUFACTURER_ID").
         setMandatory().
         setHidden().
         setDbName("MANUFACTURER_ID");

      itemblk1.addField("ITEM1_ADDRESS_ID").
         setHidden().
         setDbName("ADDRESS_ID");

      itemblk1.addField("ADDRESS_TYPE_CODE").
         setSize(14).
         setMandatory().
         setLabel("ENTERWMANUFACTURERADDRESSADDRESSTYPECODE: Address Type").
         enumerateValues("Address_Type_Code_API").
         setSelectBox();

      itemblk1.addField("DEF_ADDRESS").
         setSize(8).
         setMandatory().
         setCheckBox("FALSE,TRUE").
         setLabel("ENTERWMANUFACTURERADDRESSDEFADDRESS: Default");

      itemblk1.addField("ITEM1_DEFAULT_DOMAIN").
         setMandatory().
         setHidden().
         setDbName("DEFAULT_DOMAIN");

      itemblk1.setView("MANUF_INFO_ADDR_TYPE");
      itemblk1.defineCommand("MANUF_INFO_ADDR_TYPE_API","New__,Modify__,Remove__");
      itemblk1.setMasterBlock(itemblk0); 
      itemblk1.setTitle(mgr.translate("ENTERWMANUFACTURERADDRESSADDRTYPE: Address Types"));        
      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.disableCommand(itembar1.DUPLICATEROW);
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");     

      itemtbl1 = mgr.newASPTable(itemblk1);


      itemlay1 = itemblk1.getASPBlockLayout();   
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);    

      //=============================================
      //This Blocks refers to Comm.Method Tab
      //=============================================

      itemblk2 = mgr.newASPBlock("ITEM2");

      itemblk2.addField("ITEM2_OBJID").
         setHidden().
         setDbName("OBJID");

      itemblk2.addField("ITEM2_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      itemblk2.addField("ITEM2_COMM_ID","Number").
         setHidden().
         setDbName("COMM_ID");

      itemblk2.addField("ITEM2_NAME").
         setDbName("NAME").
         setSize(30).
         setLabel("ENTERWMANUFACTURERADDRESSITEM2NAME: Name");

      itemblk2.addField("ITEM2_DESCRIPTION").
         setSize(11).
         setLabel("ENTERWMANUFACTURERADDRESSITEM2DESCRIPTION: Description").
         setDbName("DESCRIPTION");

      itemblk2.addField("ITEM2_METHOD_ID").
         setSize(14).
         setMandatory().
         enumerateValues("COMM_METHOD_CODE_API").
         setSelectBox().
         setLabel("ENTERWMANUFACTURERADDRESSITEM2METHODID: Comm. Method").
         setDbName("METHOD_ID");

      itemblk2.addField("ITEM2_MANUFACTURER_ID").
         setSize(16).
         setLOV("ManufacturerInfoLov.page",600,445).
         setMandatory().
         setHidden().
         setLabel("ENTERWMANUFACTURERADDRESSITEM2MANUFACTURERID: Manufacturer Id.").
         setDbName("MANUFACTURER_ID").
         setUpperCase();

      itemblk2.addField("ITEM2_VALUE").
         setSize(11).
         setMandatory().
         setLabel("ENTERWMANUFACTURERADDRESSITEM2VALUE: Value").
         setDbName("VALUE");

      itemblk2.addField("ITEM2_ADDRESS_ID").
         setSize(11).
         setHidden().
         setLabel("ENTERWMANUFACTURERADDRESSITEM2ADDRESSID: Address Id.").
         setDbName("ADDRESS_ID").
         setUpperCase();

      itemblk2.addField("ITEM2_METHOD_DEFAULT").
         setSize(15).
         setCheckBox("FALSE,TRUE").
         setLabel("ENTERWMANUFACTURERADDRESSITEM2METHODDEFAULT: Method Default").
         setDbName("METHOD_DEFAULT");

      itemblk2.addField("ITEM1_ADDRESS_DEFAULT").
         setSize(16).
         setCheckBox("FALSE,TRUE").
         setLabel("ENTERWMANUFACTURERADDRESSITEM1ADDRESSDEFAULT: Address Default").
         setDbName("ADDRESS_DEFAULT");

      itemblk2.addField("ITEM1_VALID_FROM","Date").
         setSize(11).
         setLabel("ENTERWMANUFACTURERADDRESSITEM1VALIDFROM: Valid From").
         setDbName("VALID_FROM");

      itemblk2.addField("ITEM1_VALID_TO","Date").
         setSize(11).
         setLabel("ENTERWMANUFACTURERADDRESSITEM1VALIDTO: Valid To").
         setDbName("VALID_TO");

      itemblk2.setView("MANUF_INFO_COMM_METHOD");
      itemblk2.defineCommand("MANUF_INFO_COMM_METHOD_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(itemblk0); 
      itemblk2.setTitle(mgr.translate("ENTERWMANUFACTURERADDRESSCOMMETH: Communication Methods"));        
      itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.disableCommand(itembar2.DUPLICATEROW);
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.defineCommand(itembar2.SAVERETURN, "saveReturnITEM2");
      itembar2.defineCommand(itembar2.SAVENEW, "saveNewITEM2");     

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle("ENTERWMANUFACTURERADDRESSCOMMMETHOD: Communication Methods");      

      itemlay2 = itemblk2.getASPBlockLayout();   
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);        
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if (itemlay0.isMultirowLayout())
         mgr.getASPField("EAN_LOCATION").setHidden();

      if ( itemset0.countRows() == 0 )
         itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);

      if ( itemset1.countRows() == 0 )
         itemlay1.setLayoutMode(itemlay1.MULTIROW_LAYOUT);

      if ( itemset2.countRows() == 0 )
         itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);

      if (headset.countRows() == 0)
      {
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.FORWARD);
         headbar.removeCustomCommand("ManufacturerInfo");
      }
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWMANUFACTURERADDRESSTITLE: Manufacturer - Address Info";
   }

   protected String getTitle()
   {
      return "ENTERWMANUFACTURERADDRESSTITLE: Manufacturer - Address Info";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }
      if (itemlay0.isVisible() && (headset.countRows()!= 0))
      {
         appendToHTML(itemlay0.show());
      }
      if ((itemlay1.isVisible() && itemlay0.isVisible() && (itemset0.countRows()!= 0) ) || itemlay1.isNewLayout() || itemlay1.isEditLayout())
      {
         appendToHTML(itemlay1.show());
      }
      if ((itemlay2.isVisible() && itemlay0.isVisible() && (itemset0.countRows()!= 0) ) || itemlay2.isNewLayout() || itemlay2.isEditLayout())
      {
         appendToHTML(itemlay2.show());
      }
   }

}
