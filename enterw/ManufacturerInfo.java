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
*  File        : ManufacturerInfo.java 
*  Modified    :
*    ASP2JAVA Tool  2001-01-23  - Created Using the ASP file ManufacturerInfo.asp
*    Disali K. : 2001-02-09 - Made the file compilable after converting to Java
*    Disali K  : 2001-03-22 - Modifications for new webkit
*    Disali K  : 2001-03-23 - Modified the title (Call Id 63601)
*    Dagalk    : 2002-03-18 - Call Id 78900 & 78901 
*    raselk    : 2002-12-17 - Added the block Part Manufacturer ,
*              : 2003-01-24 - code review- modified PreDefine()
*    MAWELK    : 2005-04-09 - FIPR364 - Corrected of web tags.
*    Jakalk    : 2005-09-07 - Code Cleanup, Removed doReset and clone methods.
*    Maselk    : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.
*    Shwilk    : 2007-07-20 - Done web security corrections. 
*    Thpelk    : 2007-08-02 : Call Id 146997, Corrected MANUFACTURER_ID to allow only 20 characters.
*    Maanlk    : 2009-07-24 - Bug 84593, Modified preDefine() to make MANUFACTURER_ID non mandatory.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ManufacturerInfo extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.ManufacturerInfo");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPBlock headblk1;
   private ASPRowSet headset1;
   private ASPCommandBar headbar1;
   private ASPTable headtbl1;
   private ASPBlockLayout headlay1;
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
   private ASPContext ctx;
   private ASPBlock itemblk3;
   private ASPRowSet itemset3;
   private ASPCommandBar itembar3;
   private ASPBlockLayout itemlay3;
   private ASPTable itemtbl3;
   private ASPBlock b;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery qry;
   private ASPCommand cmd;
   private String partHist; 
   private String partNo;
   private String manuNo;
   private ASPBuffer sec;
   private boolean checkSec;
   private boolean isManuPartNoHistoryRMB;

   //===============================================================
   // Construction 
   //===============================================================
   public ManufacturerInfo(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      checkSec = ctx.readFlag("CHECKSEC",false);
      isManuPartNoHistoryRMB = ctx.readFlag("ISMANUPARTHISTORYRMB",false);
      if (!checkSec)
         checkSecurety();
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("MANUFACTURER_ID")))  //Code for Zoom function from Maintenance
      {
         okFind();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();

      adjust();
      ctx.writeFlag("CHECKSEC",checkSec);
      ctx.writeFlag("ISMANUPARTHISTORYRMB",isManuPartNoHistoryRMB);

   }

//=============================================================================
//    CHECK   FUNCTION  
//=============================================================================


   public void  checkSecurety()
   {
      ASPManager mgr = getASPManager();

      checkSec = true;
      trans.clear();

      trans.addPresentationObjectQuery("partcw/PartManuPartHistOvw.page");
      trans.addSecurityQuery("PART_MANU_PART_HIST");

      trans = mgr.submit(trans);
      sec = trans.getSecurityInfo(); 

      if ((sec.getItemPosition("partcw/PartManuPartHistOvw.page")!= -1) &&   sec.itemExists("PART_MANU_PART_HIST"))
         isManuPartNoHistoryRMB = true;

      trans.clear();        
   }   

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");

      if ( "PART_NO".equals(val) )
      {
         trans.clear ();

         cmd = trans.addCustomFunction( "PARTNO1","PART_CATALOG_API.Get_Description","PART_DESCRIPTION");
         cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

         cmd = trans.addCustomFunction( "STDID","PART_CATALOG_API.Get_Std_Name_Id","STANDARD_ID");
         cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

         cmd = trans.addCustomFunction( "PARTNO2","STANDARD_NAMES_API.Get_Std_Name","PART_NAME");
         cmd.addReference("STANDARD_ID","STDID/DATA","STANDARD_ID");

         trans = mgr.validate(trans);
         String name1 = trans.getValue("PARTNO1/DATA/PART_DESCRIPTION");
         String name2 = trans.getValue("PARTNO2/DATA/PART_NAME");
         String txt = (mgr.isEmpty(name1) ? "":name1)+ "^" + (mgr.isEmpty(name2) ? "":name2)+ "^";
         mgr.responseWrite(txt);  

      }
      else if ( "MTBF_MTTR_UNIT".equals(val) )
      {
         trans.clear ();

         cmd = trans.addCustomFunction( "MTBFUNIT","ISO_UNIT_API.Get_Description","DESCRIPTION");
         cmd.addParameter("MTBF_MTTR_UNIT",mgr.readValue("MTBF_MTTR_UNIT"));

         trans = mgr.validate(trans);
         String name1 = trans.getValue("MTBFUNIT/DATA/DESCRIPTION");
         String txt = (mgr.isEmpty(name1) ? "":name1)+ "^" ;
         mgr.responseWrite(txt);   

      }
      mgr.endResponse();
   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      headset1.clear();  
      itemset1.clear();
      itemset2.clear(); 
      itemset3.clear();


      qry = trans.addQuery(headblk);
      if ( mgr.dataTransfered() )
      {
         qry.addOrCondition(mgr.getTransferedData());
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT); 
      }
      qry.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if (  headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("ENTERWMANUFACTURERINFONODATAFOUND: No data found.")); 
         headset.clear();
      }

      okFindITEM0();
      okFindITEM1();         
      okFindITEM2();
      okFindITEM3();

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


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer data = null;
      cmd = trans.addEmptyCommand("MAIN","MANUFACTURER_INFO_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MAIN/DATA");
      headset.addRow(data);
   }

//=====================
// ITEMS BLOCK - ITEM0
//=====================

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      int intheadrowno =0;
      if ( headset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addQuery(headblk1);
         qry.addWhereCondition("MANUFACTURER_ID = ?");
         qry.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
         qry.includeMeta("ALL");
         intheadrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(intheadrowno);  
      }
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer data = null;
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","MANUFACTURER_INFO_OUR_ID_API.New__",headblk1);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      headset1.addRow(data);
   }

//=============
// ITEMS BLOCK1
//=============

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      int intheadrowno =0;
      if ( headset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addQuery(itemblk1);
         qry.addWhereCondition("MANUFACTURER_ID = ?");
         qry.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
         qry.includeMeta("ALL");
         intheadrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(intheadrowno);  
      }
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer data = null;
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","MANUF_INFO_COMM_METHOD_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }

   public void saveReturnITEM1()
   {
      ASPManager mgr = getASPManager();

      String validFrom, validUntil, valDate = null;
      int curComMethodRow, intvalDate1 =0; 
      itemset1.changeRow();
      curComMethodRow = itemset1.getCurrentRowNo(); 

      validFrom  = mgr.formatDate("ITEM1_VALID_FROM", itemset1.getDateValue("ITEM1_VALID_FROM"));
      validUntil = mgr.formatDate("ITEM1_VALID_TO", itemset1.getDateValue("ITEM1_VALID_TO"));

      qry = trans.addQuery("VALDT","DUAL","(to_date(?,'yyyy-mm-dd') - to_date(?,'yyyy-mm-dd')) VALIDDATE","","");
      qry.addParameter("ITEM1_VALID_TO", validUntil);
      qry.addParameter("ITEM1_VALID_FROM", validFrom);
      trans = mgr.perform(trans);
      valDate = trans.getValue("VALDT/DATA/VALIDDATE");
      trans.clear();

      if (valDate != null)
         intvalDate1 = toInt(valDate);
      else
         intvalDate1 = 0;

      if (intvalDate1 < 0 )
      {
         mgr.showError(mgr.translate("ENTERWMANUFACTURERINFODATECHK: Date of 'Valid To' should not be less than Date of 'Valid From'!"));
      }

      mgr.submit(trans);
      itemset1.goTo(curComMethodRow);

   }

   public void saveNewITEM1()
   {
      saveReturnITEM1();
      newRowITEM1();    
   }  


//=============
// ITEMS BLOCK2
//=============

   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      int intheadrowno =0;
      if ( headset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addQuery(itemblk2);
         qry.addWhereCondition("MANUFACTURER_ID = ?");
         qry.addParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
         qry.includeMeta("ALL");
         intheadrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(intheadrowno);  
      }
   }


   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer data = null;
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM2","MANUF_INFO_MSG_SETUP_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("MANUFACTURER_ID", headset.getValue("MANUFACTURER_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      itemset2.addRow(data);
   }

//============
//ITEMBLOCK3
//============

   public void  okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      int intheadrowno =0;
      if ( headset.countRows() != 0 )
      {
         trans.clear();
         qry = trans.addQuery(itemblk3);
         qry.addWhereCondition("MANUFACTURER_NO = ?");
         qry.addParameter("MANUFACTURER_NO",headset.getValue("MANUFACTURER_ID"));
         qry.includeMeta("ALL");
         intheadrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(intheadrowno);  
      }
   } 

   public void  newRowITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer data = null;

      trans.clear();

      cmd = trans.addEmptyCommand("ITEM3","PART_MANU_PART_NO_API.New__",itemblk3);
      cmd.setOption("ACTION","PREPARE"); 
      cmd.setParameter("MANUFACTURER_NO",headset.getValue("MANUFACTURER_ID"));

      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      itemset3.addRow(data);
   }


//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  manufacturerAddress()
   {
      ASPManager mgr = getASPManager();

      int intSelectedRow =0;
      ASPBuffer buff, row = null;
      if (headlay.isMultirowLayout())
         intSelectedRow = headset.getRowSelected();
      else
         intSelectedRow = headset.getCurrentRowNo();

      headset.goTo(intSelectedRow);
      buff=mgr.newASPBuffer();
      row = buff.addBuffer("1");
      row.addItem("MANUFACTURER_ID",headset.getValue("MANUFACTURER_ID"));

      mgr.transferDataTo("ManufacturerAddress.page",buff);
   }

   public void  setPrefManuPartRMB()
   {   
      ASPManager mgr = getASPManager();

      if ( itemlay3.isMultirowLayout() )
         itemset3.goTo(itemset3.getRowSelected());

      trans.clear();
      cmd = trans.addCustomCommand("MANUPART","PART_MANU_PART_NO_API.Set_Preferred_Manu_Part");
      cmd.addParameter("PART_NO",itemset3.getValue("PART_NO"));
      cmd.addParameter("MANUFACTURER_NO",itemset3.getValue("MANUFACTURER_NO"));
      cmd.addParameter("MANU_PART_NO",itemset3.getValue("MANU_PART_NO"));

      trans = mgr.perform(trans);
      itemset3.refreshRow();

   } 


   public void  manuPartNoHistoryRMB()
   {
      ASPManager mgr = getASPManager();

      if ( itemlay3.isMultirowLayout() )
         itemset3.goTo(itemset3.getRowSelected());

      partNo = itemset3.getValue("PART_NO"); 
      manuNo = itemset3.getValue("MANUFACTURER_NO"); 
      partHist = "TRUE";
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      //=============================================
      //This Blocks refers to General Tab
      //=============================================

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").
         setHidden();

      // Bug 84593, Begin
      headblk.addField("MANUFACTURER_ID").
         setSize(25).
         setMaxLength(20).
         setLabel("ENTERWMANUFACTURERINFOMANUFACTURERID: Manufacturer Id").
         setUpperCase().
         setReadOnly().
         setInsertable();
      // Bug 84593, End

      headblk.addField("NAME").
         setSize(25).
         setMandatory().
         setLabel("ENTERWMANUFACTURERINFONAME: Name");

      headblk.addField("ASSOCIATION_NO").
         setSize(25).
         setLabel("ENTERWMANUFACTURERINFOASSOCIATIONNO: Association No.");

      headblk.addField("DEFAULT_LANGUAGE").
         enumerateValues("ISO_LANGUAGE_API").
         setSelectBox().
         setSize(25).
         setLabel("ENTERWMANUFACTURERINFODEFAULTLANGUAGE: Default Language");

      headblk.addField("COUNTRY").
         enumerateValues("ISO_COUNTRY_API").
         setSelectBox().
         setSize(25).
         setLabel("ENTERWMANUFACTURERINFOCOUNTRY: Country");

      headblk.addField("CREATION_DATE","Date").
         setSize(25).
         setMandatory().
         setLabel("ENTERWMANUFACTURERINFOCREATIONDATE: Creation Date").
         setReadOnly(); 

      headblk.addField("DEFAULT_DOMAIN").
         setSize(14).
         setMandatory().
         setHidden().
         setLabel("ENTERWMANUFACTURERINFODEFAULTDOMAIN: Default Domain");

      headblk.addField("PARTY_TYPE").
         setSize(11).
         setMandatory().
         setHidden();

      headblk.setView("MANUFACTURER_INFO");
      headblk.defineCommand("MANUFACTURER_INFO_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet(); 

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("ENTERWMANUFACTURERINFOMANFAC: Manufacturer");

      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("ManufacturerAddress","Address Info..."); 
      headbar.disableCommand(headbar.DUPLICATEROW);     
      headbar.defineCommand(headbar.OKFIND,   "okFind");
      headbar.defineCommand(headbar.COUNTFIND,"countFind");
      headbar.defineCommand(headbar.NEWROW,    "newRow");      

      headlay = headblk.getASPBlockLayout();   
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);       

      //=============================================
      //This Blocks refers to General Tab(Our Id)
      //=============================================

      headblk1 = mgr.newASPBlock("ITEM0");

      headblk1.addField("ITEM0_OBJID").
         setHidden().
         setDbName("OBJID");

      headblk1.addField("ITEM0_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      headblk1.addField("COMPANY").
         setSize(23).
         setDynamicLOV("COMPANY",600,445).
         setMandatory().
         setLabel("ENTERWMANUFACTURERINFOCOMPANY: Company").
         setUpperCase();

      headblk1.addField("OUR_ID").
         setSize(25).
         setMandatory().
         setLabel("ENTERWMANUFACTURERINFOOURID: Our Id.").
         setUpperCase();

      headblk1.addField("ITEM0_MANUFACTURER_ID").
         setMandatory().
         setHidden().
         setDbName("MANUFACTURER_ID");

      headblk1.setView("MANUFACTURER_INFO_OUR_ID");
      headblk1.defineCommand("MANUFACTURER_INFO_OUR_ID_API","New__,Modify__,Remove__");
      headblk1.setMasterBlock(headblk);    
      headblk1.setTitle(mgr.translate("ENTERWMANUFACTURERINFOOURIDTITLE: Our ID"));         
      headset1 = headblk1.getASPRowSet();

      headbar1 = mgr.newASPCommandBar(headblk1);
      headbar1.disableCommand(headbar1.DUPLICATEROW);   
      headbar1.defineCommand(headbar1.OKFIND,   "okFindITEM0");
      headbar1.defineCommand(headbar1.COUNTFIND,"countFindITEM0");
      headbar1.defineCommand(headbar1.NEWROW,    "newRowITEM0");      

      headtbl1 = mgr.newASPTable(headblk1);
      headtbl1.setTitle("ENTERWMANUFACTURERINFOOURIDTITLE: Our ID");

      headlay1 = headblk1.getASPBlockLayout();   
      headlay1.setDefaultLayoutMode(headlay1.MULTIROW_LAYOUT);     

      //=============================================
      //This Blocks refers to Comm.Method Tab
      //=============================================

      itemblk1 = mgr.newASPBlock("ITEM1");

      itemblk1.addField("ITEM1_OBJID").
         setHidden().
         setDbName("OBJID");

      itemblk1.addField("ITEM1_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      itemblk1.addField("ITEM1_COMM_ID","Number").
         setHidden().
         setMandatory().
         setDbName("COMM_ID");

      itemblk1.addField("ITEM1_NAME").
         setSize(15).
         setLabel("ENTERWMANUFACTURERINFOITEM1NAME: Name").
         setDbName("NAME");         

      itemblk1.addField("ITEM1_DESCRIPTION").
         setSize(11).
         setLabel("ENTERWMANUFACTURERINFOITEM1DESCRIPTION: Description").
         setDbName("DESCRIPTION");

      itemblk1.addField("ITEM1_METHOD_ID").
         setSize(14).
         setMandatory().
         enumerateValues("COMM_METHOD_CODE_API").
         setSelectBox().
         setLabel("ENTERWMANUFACTURERINFOITEM1METHODID: Comm. Method").
         setDbName("METHOD_ID");

      itemblk1.addField("ITEM1_MANUFACTURER_ID").
         setMandatory().
         setHidden().
         setDbName("MANUFACTURER_ID");

      itemblk1.addField("ITEM1_VALUE").
         setSize(11).
         setMandatory().
         setLabel("ENTERWMANUFACTURERINFOITEM1VALUE: Value").
         setDbName("VALUE");

      itemblk1.addField("ITEM1_ADDRESS_ID").
         setSize(11).
         setDynamicLOV("MANUFACTURER_INFO_ADDRESS","ITEM1_MANUFACTURER_ID MANUFACTURER_ID",600,445).
         setLabel("ENTERWMANUFACTURERINFOITEM1ADDRESSID: Address Id.").
         setDbName("ADDRESS_ID").
         setUpperCase();

      itemblk1.addField("ITEM1_METHOD_DEFAULT").
         setSize(15).
         setCheckBox("FALSE,TRUE").
         setLabel("ENTERWMANUFACTURERINFOITEM1METHODDEFAULT: Method Default").
         setDbName("METHOD_DEFAULT");

      itemblk1.addField("ITEM1_ADDRESS_DEFAULT").
         setSize(16).
         setCheckBox("FALSE,TRUE").
         setLabel("ENTERWMANUFACTURERINFOITEM1ADDRESSDEFAULT: Address Default").
         setDbName("ADDRESS_DEFAULT");

      itemblk1.addField("ITEM1_VALID_FROM","Date").
         setSize(11).
         setLabel("ENTERWMANUFACTURERINFOITEM1VALIDFROM: Valid From").
         setDbName("VALID_FROM");

      itemblk1.addField("ITEM1_VALID_TO","Date").
         setSize(11).
         setLabel("ENTERWMANUFACTURERINFOITEM1VALIDTO: Valid To").
         setDbName("VALID_TO");

      itemblk1.setView("MANUF_INFO_COMM_METHOD");
      itemblk1.defineCommand("MANUF_INFO_COMM_METHOD_API","New__,Modify__,Remove__");
      itemblk1.setMasterBlock(headblk);     
      itemblk1.setTitle(mgr.translate("ENTERWMANUFACTURERINFOCOMMETH: Communication Method"));    
      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.disableCommand(itembar1.DUPLICATEROW);
      itembar1.defineCommand(itembar1.OKFIND,   "okFindITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,    "newRowITEM1");
      itembar1.defineCommand(itembar1.SAVERETURN, "saveReturnITEM1");
      itembar1.defineCommand(itembar1.SAVENEW, "saveNewITEM1");     

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle("ENTERWMANUFACTURERINFOCOMMET: Communication Method ");

      itemlay1 = itemblk1.getASPBlockLayout();   
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);    

      //=============================================
      //This Blocks refers to Message Setup Tab
      //=============================================

      itemblk2 = mgr.newASPBlock("ITEM2");

      itemblk2.addField("ITEM2_OBJID").
         setHidden().
         setDbName("OBJID");

      itemblk2.addField("ITEM2_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      itemblk2.addField("MESSAGE_CLASS").
         setSize(16).
         setDynamicLOV("MESSAGE_CLASS",600,445).
         setMandatory().
         setLabel("ENTERWMANUFACTURERINFOMESSAGECLASS: Message Class").
         setUpperCase();

      itemblk2.addField("MEDIA_CODE").
         setSize(11).
         setDynamicLOV("MESSAGE_MEDIA",600,445).
         setMandatory().
         setLabel("ENTERWMANUFACTURERINFOMEDIACODE: Media Code").
         setUpperCase();

      itemblk2.addField("ITEM2_ADDRESS").
         setSize(16).
         setDynamicLOV("MESSAGE_RECEIVER",600,445).
         setMandatory().
         setLabel("ENTERWMANUFACTURERINFOITEM2ADDRESS: Address").
         setDbName("ADDRESS");

      itemblk2.addField("ITEM2_METHOD_DEFAULT").
         setSize(15).
         setCheckBox("FALSE,TRUE").
         setLabel("ENTERWMANUFACTURERINFOITEM2METHODDEFAULT: Method Default").
         setDbName("METHOD_DEFAULT");

      itemblk2.addField("ITEM2_MANUFACTURER_ID").
         setMandatory().
         setHidden().
         setDbName("MANUFACTURER_ID");

      itemblk2.setView("MANUF_INFO_MSG_SETUP");
      itemblk2.defineCommand("MANUF_INFO_MSG_SETUP_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(headblk);   
      itemblk2.setTitle(mgr.translate("ENTERWMANUFACTURERINFOMSGSET: Message Setup"));       
      itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.disableCommand(itembar2.DUPLICATEROW);  
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");    

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle("ENTERWMANUFACTURERINFOMESSET: Message Setup");

      itemlay2 = itemblk2.getASPBlockLayout();   
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);      

      b = mgr.newASPBlock("DEFAULT_LANGUAGE");

      b.addField("CLIENT_VALUES0");

      b = mgr.newASPBlock("COUNTRY");

      b.addField("CLIENT_VALUES1");

      b = mgr.newASPBlock("PARTY_TYPE");

      b.addField("CLIENT_VALUES2");

      b = mgr.newASPBlock("ITEM0_COUNTRY");

      b.addField("CLIENT_VALUES3");

      b = mgr.newASPBlock("ITEM0_PARTY_TYPE");

      b.addField("CLIENT_VALUES4");

      b = mgr.newASPBlock("ITEM1_METHOD_ID");

      b.addField("CLIENT_VALUES5");

      //raselk - Start

      //=============================================
      //This Blocks refers to Part Manufacturer
      //=============================================

      itemblk3 = mgr.newASPBlock("ITEM3");

      itemblk3.addField("ITEM3_OBJID").
         setDbName("OBJID").
         setHidden();

      itemblk3.addField("ITEM3_OBJVERSION").
         setDbName("OBJVERSION").
         setHidden();

      itemblk3.addField("PART_NO").
         setLabel("ENTERWMANUFACTURERINFOPARTNO: Part Number").
         setUpperCase().
         setMandatory().
         setDynamicLOV("PART_CATALOG").
         setLOVProperty("TITLE",mgr.translate("ENTERWMANUFACTURERINFOPARTNOLOV: Part Numbers")).
         setCustomValidation("PART_NO","PART_DESCRIPTION,PART_NAME").
         setSize(30);   

      itemblk3.addField("PART_DESCRIPTION").
         setLabel("ENTERWMANUFACTURERINFOPARTDESCRIPTION: Part Description").
         setFunction("PART_CATALOG_API.Get_Description(:PART_NO)").
         setReadOnly().
         setSize(30); 

      itemblk3.addField("PART_NAME").
         setLabel("ENTERWMANUFACTURERINFOPARTNAME: Part Standard Name").
         setFunction("STANDARD_NAMES_API.Get_Std_Name(PART_CATALOG_API.Get_Std_Name_Id(:PART_NO))").
         setReadOnly().
         setSize(30);

      itemblk3.addField("MANUFACTURER_NO").
         setHidden().
         setSize(30);

      itemblk3.addField("MANU_PART_NO").
         setLabel("ENTERWMANUFACTURERINFOMANUPARTNO: Manufacturer Part No").
         setUpperCase().
         setMandatory().
         setSize(30);

      itemblk3.addField("PREFERRED_MANU_PART").
         setFunction("PREFERRED_MANU_PART").
         setLabel("ENTERWMANUFACTURERINFOPREFERREDMANUPART: Preferred Manufacturer Part").
         setCheckBox("FALSE,TRUE")
         .setReadOnly();

      itemblk3.addField("COMM_GEN_DESCRIPTION").
         setLabel("ENTERWMANUFACTURERINFOCOMMGENDESCRIPTION: Commercial Generic Description").
         setMaxLength(50).
         setDefaultNotVisible().
         setSize(30);

      itemblk3.addField("APPROVED").
         setLabel("ENTERWMANUFACTURERINFOAPPROVED: Approved").
         enumerateValues("PART_MANU_APPROVED_API").
         setSelectBox().
         setSize(15);

      itemblk3.addField("APPROVED_NOTE").
         setLabel("ENTERWMANUFACTURERINFOAPPROVEDNOTE: Approved Note").
         setDefaultNotVisible(). 
         setSize(30);

      itemblk3.addField("APPROVED_DATE","Datetime").
         setLabel("ENTERWMANUFACTURERINFOAPPROVEDDATE: Approved Date").
         setReadOnly().
         setDefaultNotVisible().
         setSize(30);

      itemblk3.addField("APPROVED_USER").
         setLabel("ENTERWMANUFACTURERINFOAPPROVEDBY: Approved By").
         setReadOnly().
         setUpperCase().
         setDefaultNotVisible().
         setSize(30);

      itemblk3.addField("MANUFACTURER_MTBF","Number","####").
         setLabel("ENTERWMANUFACTURERINFOMANUMTBF: Manufacturer MTBF").
         setAlignment("RIGHT").
         setSize(30);

      itemblk3.addField("MANUFACTURER_MTTR","Number","####").
         setLabel("ENTERWMANUFACTURERINFOMANUMTTR: Manufacturer MTTR").
         setAlignment("RIGHT").
         setSize(30);

      itemblk3.addField("EXPERIENCED_MTBF","Number","####").
         setLabel("ENTERWMANUFACTURERINFOEXPERIENCEDMTBF: Experienced MTBF").
         setAlignment("RIGHT").
         setSize(30);

      itemblk3.addField("EXPERIENCED_MTTR","Number","####").
         setLabel("ENTERWMANUFACTURERINFOEXPERIENCEDMTTR: Experienced MTTR").
         setAlignment("RIGHT").
         setSize(30);

      itemblk3.addField("MTBF_MTTR_UNIT").
         setLabel("ENTERWMANUFACTURERINFOMTBFMTTRUNIT: MTBF MTTR Unit").
         setDynamicLOV("ISO_UNIT").
         setLOVProperty("TITLE",mgr.translate("ENTERWMANUFACTURERINFOMTBFMTTRUNITLOV: MTBF MTTR Unit")).
         setCustomValidation("MTBF_MTTR_UNIT","DESCRIPTION").
         setDefaultNotVisible().
         setSize(30);

      itemblk3.addField("DESCRIPTION").
         setLabel("ENTERWMANUFACTURERINFODESCRIPTION: Description").
         setFunction("ISO_UNIT_API.Get_Description(:MTBF_MTTR_UNIT)").
         setReadOnly().
         setDefaultNotVisible().
         setSize(30);

      itemblk3.addField("CATALOG_PRICE","Money").
         setLabel("ENTERWMANUFACTURERINFOCATALOGPRICE: Catalog Price").
         setAlignment("RIGHT").
         setDefaultNotVisible().
         setSize(30);


      itemblk3.addField("CATALOG_CURRENCY").
         setLabel("ENTERWMANUFACTURERINFOCATALOGCURRENCY: Catalog Currency").
         setUpperCase().
         setDefaultNotVisible().
         setSize(30);

      itemblk3.addField("NATOSTOCK_NO").
         setLabel("ENTERWMANUFACTURERINFONATOSTOCK_NO: NATO Stock Number").
         setFunction("Nato_Stock_Part_Catalog_API.Get_Stock_Number(:PART_NO)").
         setReadOnly().
         setSize(30);


      itemblk3.addField("DATE_CREATED","Datetime").
         setLabel("ENTERWMANUFACTURERINFODATECREATED:  Date Created").
         setReadOnly().
         setDefaultNotVisible().
         setSize(30);

      itemblk3.addField("USER_CREATED").
         setLabel("ENTERWMANUFACTURERINFOUSERCREATED: Created By").
         setUpperCase().
         setReadOnly().
         setDefaultNotVisible().
         setSize(30);

      itemblk3.addField("STANDARD_ID","Number").setHidden ().setFunction ("''");
      itemblk3.addField("GETAPPROVE").setHidden ().setFunction ("''");


      itemblk3.setView("PART_MANU_PART_NO");
      itemblk3.defineCommand("PART_MANU_PART_NO_API","New__,Remove__");
      itemblk3.setMasterBlock(headblk);
      itemblk3.setTitle(mgr.translate("ENTERWMANUFACTURERINFOPARTTITLE: Part Manufacturer")); 
      itemset3 = itemblk3.getASPRowSet();

      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3"); 
      itembar3.defineCommand(itembar3.SAVERETURN,null,"checkItem3Fields");
      itembar3.defineCommand(itembar3.SAVENEW,null,"checkItem3Fields");

      itembar3.addCustomCommand("setPrefManuPartRMB",mgr.translate("ENTERWMANUFACTURERINFOSETPREFMANU: Set Preferred Manufacturer Part No")); 
      itembar3.addCustomCommand("manuPartNoHistoryRMB",mgr.translate("ENTERWMANUFACTURERINFOMANUPARTMANU: Manufacturer  Part Number History..."));

      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle("ENTERWMANUFACTURERINFOTBLTITLE: Part Manufacturer");

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT); 
      
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if ( headset.countRows() == 0 )
      {
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.BACK);  
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCustomCommand("ManufacturerAddress");
      }

      if ( headset1.countRows() == 0 &&   headlay1.isSingleLayout() )
      {
         headlay1.setLayoutMode(headlay1.MULTIROW_LAYOUT);
      }

      if ( itemset1.countRows() == 0 &&   itemlay1.isSingleLayout() )
      {
         itemlay1.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
      }

      if ( itemset2.countRows() == 0 &&   itemlay2.isSingleLayout() )
      {
         itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
      }
      
      if ( itemset3.countRows() == 0 &&   itemlay3.isSingleLayout() )
      {
         itemlay3.setLayoutMode(itemlay3.MULTIROW_LAYOUT);
      }
      if (!isManuPartNoHistoryRMB)
         itembar3.removeCustomCommand("manuPartNoHistoryRMB");

      mgr.setPageNonExpiring();  
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWMANUFACTURERINFOTITLE1: Manufacturer - General Info";
   }

   protected String getTitle()
   {
      return "ENTERWMANUFACTURERINFOTITLE2: Manufacturer - General Info";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }
      if ( headlay1.isVisible()  &&   headset.countRows() != 0 )
      {
         appendToHTML(headlay1.show());
      }
      if ( itemlay1.isVisible()  &&   headset.countRows() != 0 )
      {
         appendToHTML(itemlay1.show());
      }
      if ( itemlay2.isVisible()  &&   headset.countRows() != 0 )
      {
         appendToHTML(itemlay2.show());
      }
      
      if ( itemlay3.isVisible()  &&   headset.countRows() != 0 )
      {
         appendToHTML(itemlay3.show());
      }

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(partHist);
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  PartNo = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(partNo));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("  ManuNo = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(manuNo));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("partHist = \"FALSE\";\n");
      appendDirtyJavaScript("   window.open(\"../partcw/PartManuPartHistOvw.page?PART_NO=\"+URLClientEncode(PartNo)+\"&MANUFACTURER_NO=\"+URLClientEncode(ManuNo),\"impProdStruc\",\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=800,height=500\");   \n");
      appendDirtyJavaScript("}\n");
      
   }
}
