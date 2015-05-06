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
*  File        : PersonInfo.java 
*  Modified    :
*    ASP2JAVA Tool  2001-01-23  - Created Using the ASP file PersonInfo.asp
*    Disali K. : 2001-02-09 - Made the file compilable after converting to Java
*   Madhu      :Call # 68595 - Added saveReturnITEM0(), saveNewITEM0() and the protected person's check box. edited the okFindITEM2().
*  Kupelk      :18/01/2002   IID 10997 . Added LOV view for State column.  
*  Anpalk               :03/07/2002       Call Id 29991
*  Gacolk      :07/02/2003   Removed deprecated method splitToVector() with split() and did other necessary changes
*  MAWELK      : 2005-04-09   FIPR364 - Corrected web tags.
*  Jakalk      : 2005-09-06 - Code Cleanup, Removed doReset and clone methods.
*  Maamlk      : 2006-02-10 - Call Id 133724 Replaced .asp with .page
*  Maselk      : 2006-07-31 - FIPL610 - Modified predefine(),okFindITEM0(),printContents().Added editItem0(),detailedAddress().
*  Maselk      : 2006-09-18 - B139313,B139454,B139455 Modified printContents(),okFindITEM0(),detailedAddress().
*  Maselk      : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.
*  Maselk      : 2007-03-26 - B139313, Modified Predefine(),validate(),newRowITEM0().
*  Nimalk      : 2007-06-01 - Bug 65033, Removed overridden browse functions from headbar.
*  			      Modified okFindITEM0 to support correct browsing of data.
*  Shwilk      :2007-07-20  - Done web security corrections.
*  Thpelk      : 2007-08-02 - Call Id 146997, Corrected PERSON_ID to allow only 20 characters.
*  Kanslk      : 2007-11-30 - Bug 65132, Modified adjust() so that row gets refreshed every time after new row is inserted.
*  Kanslk      : 2008-07-14 - Bug 73244, Modified saveReturnITEM1() and printContents()
*  Hiralk      : 2008-09-29 - Bug 76755, Modified preDefine() and added  saveReturn()
*  Fashlk      : 2008-10-28 - Bug 77886, Changed View used to "PERSON_INFO_ALL" in the head block
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class PersonInfo extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.PersonInfo");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet rowset;
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
   private ASPField f;
   private ASPBlock b;

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
   private boolean blnaddr;
   private boolean blncomMethod;
   private boolean blnaddType;
   private ASPQuery qry;
   private ASPCommand cmd;
   private ASPBuffer data;
   private int intheadrowno;
   private int addRowNo;
   private String detAdd;
   private String detFlag;
   private String detailAdd;
   // Bug 76755, Begin
   private String company;
   // Bug 76755, End

   //===============================================================
   // Construction 
   //===============================================================
   public PersonInfo(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   public void run() 
   {
      String adrress_id = null;
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter(); 
      ctx = mgr.getASPContext();
      blnaddr = ctx.readFlag("ADDR",false);
      blncomMethod = ctx.readFlag("COMMETHOD", false);
      blnaddType   = ctx.readFlag("ADDTYPE", false); 
      detFlag      = ctx.readValue("DETFLAG",  "false");
      detFlag      = "false";
      // Bug 76755, Begin
      company = ctx.getGlobal("COMPANY");
      // Bug 76755, End

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
         
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFind();
         okFindITEM0();
         okFindITEM1();
         okFindITEM2();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PERSON_ID")))
         transfer();
      adjust();
      
      ctx.writeFlag("ADDR",blnaddr);
      ctx.writeFlag("COMMETHOD", blncomMethod); 
      ctx.writeFlag("ADDTYPE", blnaddType);
      ctx.writeValue("DETFLAG", detFlag);
      
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");
      String description = null;
      if ("COUNTRY_CODE".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("ISODE","ISO_COUNTRY_API.decode", "ADDRESS_COUNTRY" );
         cmd.addParameter("COUNTRY_CODE");   

         trans = mgr.validate(trans);    

         description = trans.getValue("ISODE/DATA/ADDRESS_COUNTRY");

         mgr.responseWrite(description + "^" + description  + "^"); 
      }
      else if("ITEM0_COUNTRY".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("ISOCODE","ISO_COUNTRY_API.encode", "ADDRESS_COUNTRY" );
         cmd.addParameter("ITEM0_COUNTRY");

         trans = mgr.validate(trans);    

         description = trans.getValue("ISOCODE/DATA/ADDRESS_COUNTRY");
         mgr.responseWrite(description + "^"); 
      }

      else if("COUNTRY".equals(val) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("DETADD","Enterp_Address_Country_API.Get_Detailed_Address", "DUMMY1" );
         cmd.addParameter("COUNTRY_CODE");

         trans = mgr.validate(trans);    

         description = trans.getValue("ISOCODE/DATA/DUMMY1");
         mgr.responseWrite(description + "^"); 
      }
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  transfer()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      qry = trans.addEmptyQuery(headblk);
      qry.includeMeta("ALL");

      if ( mgr.dataTransfered() )
         qry.addOrCondition( mgr.getTransferedData() );
      else
      {
         qry.addWhereCondition("PERSON_ID = ?");
         qry.addParameter("PERSON_ID", mgr.readValue("PERSON_ID"));
      }

      mgr.submit(trans);

      okFindITEM0();    
      okFindITEM1();    
      okFindITEM2();  
   }

//================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      itemset0.clear();
      qry = trans.addQuery(headblk);
      qry.includeMeta("ALL");

      if (mgr.dataTransfered())
      {
         qry.addOrCondition(mgr.getTransferedData());
         headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      }

      mgr.querySubmit(trans,headblk);
      mgr.createSearchURL(headblk);

      if ( rowset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("ENTERWPERSONINFONODATAFOUND: No data found."));
         rowset.clear();     
      }

      if ( mgr.dataTransfered() )
      {
         okFindITEM0();
      }

      rowset.syncItemSets(); 
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(headblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(rowset.getValue("N")));
      rowset.clear(); 
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","PERSON_INFO_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      rowset.addRow(data);
   }

//==============================================================================================

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      if (rowset.countRows() > 0)
      {
         trans.clear();
         itemset0.clear();

         
         intheadrowno = rowset.getCurrentRowNo();
         
         qry = trans.addQuery(itemblk0);
         qry.addWhereCondition("PERSON_ID = ?");
         qry.addParameter("PERSON_ID", rowset.getValue("PERSON_ID"));
         qry.includeMeta("ALL");
         mgr.querySubmit(trans,itemblk0);
         rowset.goTo(intheadrowno);
         
        if (blnaddr)
            searchCommAddType();
         else
            okFindITEM1();  
         
      }
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();
      String country_code;

      intheadrowno = rowset.getCurrentRowNo();
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","PERSON_INFO_ADDRESS_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PERSON_ID", rowset.getValue("PERSON_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_COUNTRY",rowset.getValue("COUNTRY"));
      
      trans.clear();
      cmd = trans.addCustomFunction("CONCODE", "ISO_COUNTRY_API.Encode", "ADDRESS_COUNTRY" );
      cmd.addParameter("ITEM0_COUNTRY", rowset.getValue("COUNTRY"));
      trans = mgr.validate(trans);
      country_code = trans.getValue("CONCODE/DATA/ADDRESS_COUNTRY");
      data.setFieldItem("COUNTRY_CODE", country_code);

      itemset0.addRow(data);
      trans.clear();
   }
   
   // 76755, Begin
   public void saveReturn()
   {   
      ASPManager mgr = getASPManager();
      boolean newrecord = false;
      int currrow = 0;
      
      rowset.changeRow();
      currrow = rowset.getCurrentRowNo();
      
      if ("New__".equals(rowset.getRowStatus()))
      {
          newrecord = true;   
      }
      mgr.submit(trans);
      trans.clear();

      if (newrecord) 
      {
          cmd = trans.addCustomCommand("PERSONCOMPANY", "PERSON_COMPANY_ACCESS_API.New");
          cmd.addParameter("COMPANY", company);
          cmd.addParameter("PERSON_ID", mgr.readValue("PERSON_ID"));
          trans = mgr.perform(trans);
          trans.clear();
      }
      rowset.goTo(currrow);
   }
   // 76755, End

   public void saveReturnITEM0()
   {
      ASPManager mgr = getASPManager();
      String[] AddTyprArray;
      String types, defValue;
      int hRowNo, arraySize, i;
      ASPTransactionBuffer trans1 = null;

      itemset0.changeRow();
      itemset2.changeRows(); 
      addRowNo = itemset0.getCurrentRowNo();
      hRowNo = rowset.getCurrentRowNo();
      if ("New__".equals(itemset0.getRowStatus()))
      {
         cmd = trans.addCustomCommand("ADDTYPES","Address_Type_Code_API.Enumerate_Type");
         cmd.addParameter("DEFVALUE");
         cmd.addParameter("PARTY_TYPE_DB","PERSON");

         trans = mgr.perform(trans);

         types = trans.getValue("ADDTYPES/DATA/DEFVALUE"); 
         trans.clear();

         if (!mgr.isEmpty(types))
         {
            AddTyprArray = split(types, String.valueOf((char)31));
            arraySize = AddTyprArray.length;

            itemset2.clear();
            for (i=0; i<arraySize; ++i)
            {
               cmd = trans.addEmptyCommand("ITEM2"+i,"PERSON_INFO_ADDRESS_TYPE_API.New__",itemblk2);
               cmd.setOption("ACTION","PREPARE"); 
               cmd.setParameter("ITEM2_PERSON_ID",rowset.getValue("PERSON_ID"));
               cmd.setParameter("ITEM2_ADDRESS_ID",itemset0.getValue("ADDRESS_ID"));
            }

            trans = mgr.perform(trans);

            for (i=0; i<arraySize; ++i)
            {
               data = trans.getBuffer("ITEM2"+i+"/DATA"); 
               itemset2.addRow(data);
            }

            trans.clear();
            itemset2.first(); 

            for (i=0; i<arraySize; i++)
            {
               cmd = trans.addCustomFunction("DEFEXIST"+i,"Person_Info_Address_Type_API.Default_Exist","DEFVALUE");
               cmd.addParameter("ITEM0_PERSON_ID", rowset.getValue("PERSON_ID"));
               cmd.addParameter("ADDRESS_ID", itemset0.getValue("ADDRESS_ID"));
               cmd.addParameter("ADDRESS_TYPE_CODE",AddTyprArray[i]);
               itemset2.next();   
            }

            trans1 = mgr.perform(trans);

            trans.clear(); 
            itemset2.first(); 

            for (i=0; i<arraySize; i++)
            {
               data = itemset2.getRow(); 
               data.setValue("ADDRESS_TYPE_CODE",AddTyprArray[i]);
               defValue = trans1.getValue("DEFEXIST"+i+"/DATA/DEFVALUE");            
               if ("TRUE".equals(defValue))
                  data.setValue("DEF_ADDRESS","FALSE");
               else
                  data.setValue("DEF_ADDRESS","TRUE");  

               itemset2.setRow(data);  
               itemset2.next();     
            }
         }
      }
      trans.clear();

      cmd = trans.addCustomFunction("DETADD","Enterp_Address_Country_API.Get_Detailed_Address", "DUMMY1" );
      cmd.addParameter("COUNTRY_CODE", itemset0.getRow().getValue("COUNTRY_CODE"));
      trans = mgr.perform(trans);

      detAdd = trans.getValue("DETADD/DATA/DUMMY1");
      if("TRUE".equals(detAdd))
      {
         detFlag = "true";
      }
         
      else
         detFlag = "false";
      trans.clear();
      mgr.submit(trans);
      
      rowset.goTo(hRowNo); 
      itemset0.goTo(addRowNo);
      itemset0.refreshRow();
   }


   public void saveNewITEM0()
   {
      saveReturnITEM0();
      newRowITEM0();
   }


   public void saveReturnITEM1()
   {
      ASPManager mgr = getASPManager();

      //Bug 73244, Begin
      int rowNo = rowset.getCurrentRowNo();
      //Bug 73244, End
      String validFrom = null;
      String validUntil = null;
      String valDate = null;
      int curComMethodRow, intvalDate1 =0; 
      itemset1.changeRow();
      curComMethodRow = itemset1.getCurrentRowNo(); 

      validFrom  = mgr.formatDate("VALID_FROM", itemset1.getDateValue("VALID_FROM"));
      validUntil = mgr.formatDate("VALID_TO", itemset1.getDateValue("VALID_TO"));

      if ( (validFrom != null) && (validUntil != null) )
      {
         qry = trans.addQuery("VALDT","DUAL","(to_date(?,'yyyy-mm-dd') - to_date(?,'yyyy-mm-dd')) VALIDDATE","","");
         qry.addParameter("VALID_TO", validUntil);
         qry.addParameter("VALID_FROM", validFrom);
         trans = mgr.perform(trans);
         valDate = trans.getValue("VALDT/DATA/VALIDDATE");
         trans.clear();
      }

      if (valDate != null)
         intvalDate1 = toInt(valDate);
      else
         intvalDate1    = 0;

      if (intvalDate1 < 0 )
      {
         mgr.showError(mgr.translate("ENTERWPERSONINFODATECHK: Date of 'Valid To' should not be less than Date of 'Valid From'!"));
      }

      mgr.submit(trans);
      itemset1.goTo(curComMethodRow);
      //Bug 73244, Begin
      rowset.goTo(rowNo);
      //Bug 73244, End

   } 


   public void saveNewITEM1()
   {
      ASPManager mgr = getASPManager();

      String strvalidFrom, strvalidUntil, strvalDate = null;
      int curComMethodRow, intvalDate1 =0; 
      itemset1.changeRow();
      curComMethodRow = itemset1.getCurrentRowNo(); 

      strvalidFrom = itemset1.getValue("VALID_FROM");
      strvalidUntil = itemset1.getValue("VALID_TO");

      if (!mgr.isEmpty(strvalidFrom))
         strvalidFrom = strvalidFrom.substring(0,strvalidFrom.length()-9);
      if (!mgr.isEmpty(strvalidUntil))
         strvalidUntil = strvalidUntil.substring(0,strvalidUntil.length()-9);

      if ( (strvalidFrom != null) && (strvalidUntil != null) )
      {
         qry = trans.addQuery("VALDT","DUAL","(to_date(?,'yyyy-mm-dd') - to_date(?,'yyyy-mm-dd')) VALIDDATE","","");
	 qry.addParameter("STR_DATE", strvalidUntil);
	 qry.addParameter("STR_DATE", strvalidFrom);
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
         mgr.showError(mgr.translate("ENTERWPERSONINFODATECHK: Date of 'Valid To' should not be less than Date of 'Valid From'!"));
      }

      mgr.submit(trans);
      itemset1.goTo(curComMethodRow);
      newRowITEM1();

   }  


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","PERSON_INFO_COMM_METHOD_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM1_PERSON_ID", rowset.getValue("PERSON_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }

//=======================================================
//Search function For Add Type & Comm.Method Hyperlinks
//=======================================================

   public void  searchCommAddType()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      itemset1.clear();
      itemset2.clear(); 
      blncomMethod = true;
      blnaddType = true ;

      if (itemset0.countRows() > 0)
      {
         qry = trans.addEmptyQuery(itemblk1);  
         qry.addWhereCondition("PERSON_ID = ? and ADDRESS_ID = ?");
         qry.addParameter("PERSON_ID", rowset.getValue("PERSON_ID"));
         qry.addParameter("ADDRESS_ID", itemset0.getValue("ADDRESS_ID"));
         qry.includeMeta("ALL"); 
         qry = trans.addEmptyQuery(itemblk2);   
         qry.addWhereCondition("PERSON_ID = ? and ADDRESS_ID = ?");
         qry.addParameter("PERSON_ID", rowset.getValue("PERSON_ID"));
         qry.addParameter("ADDRESS_ID", itemset0.getValue("ADDRESS_ID"));
         qry.includeMeta("ALL");
      }
      int intcurrrow = rowset.getCurrentRowNo();
      int intcurrrow01 = itemset2.getCurrentRowNo();
      int intcurrrow1 = itemset1.getCurrentRowNo(); 

      mgr.submit(trans);

      rowset.goTo(intcurrrow);    
      itemset2.goTo(intcurrrow01);
      itemset1.goTo(intcurrrow1);

      if ( itemset0.countRows() == 0 )
      {
         blncomMethod = false;
         blnaddType = false ;
         itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
         if (( itemset1.countRows() == 0 ))
            itemset1.clear();
         else if (( itemset2.countRows() == 0 ))
            itemset2.clear();
      }
      itemtbl2.clearQueryRow();
      itemtbl1.clearQueryRow();
   }

//-----------------------------------------------------------------------------
//----------------  Comm.Method Tab Added COMMANDBAR FUNCTION -----------------
//-----------------------------------------------------------------------------

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      if (rowset.countRows() > 0)
      {
         trans.clear();   
         itemset1.clear();
         qry = trans.addQuery(itemblk1);   
         qry.addWhereCondition("PERSON_ID = ?"); 
         qry.addParameter("PERSON_ID", rowset.getValue("PERSON_ID"));
         qry.includeMeta("ALL");

         intheadrowno = rowset.getCurrentRowNo();
         mgr.submit(trans);
         rowset.goTo(intheadrowno);
      }
   }

//-----------------------------------------------------------------------------
//------------  Address Type HyperLink Added COMMANDBAR FUNCTION --------------
//-----------------------------------------------------------------------------

   public void okFindITEM2()
   {

      ASPManager mgr = getASPManager();
      int headrowno, itemrowno0;
      if ((rowset.countRows() > 0) && (itemset0.countRows()>0))
      {
         trans.clear();   
         itemset2.clear();
         qry = trans.addQuery(itemblk2);   

         qry.addWhereCondition("PERSON_ID = ? and ADDRESS_ID = ?");
         qry.addParameter("PERSON_ID", rowset.getValue("PERSON_ID"));
         qry.addParameter("ADDRESS_ID", itemset0.getValue("ADDRESS_ID"));
         qry.includeMeta("ALL");

         headrowno = rowset.getCurrentRowNo();
         itemrowno0 = itemset0.getCurrentRowNo();

         mgr.querySubmit(trans,itemblk2);
         itemtbl2.clearQueryRow();

         rowset.goTo(headrowno); 
         itemset0.goTo(itemrowno0);
      }
   }  


   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      String personid = rowset.getValue("PERSON_ID");
      String addressid = itemset0.getValue("ADDRESS_ID");
      boolean addrType = true;      

      cmd = trans.addEmptyCommand("ITEM2","PERSON_INFO_ADDRESS_TYPE_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE"); 
      cmd.setParameter("ITEM2_PERSON_ID",personid);
      cmd.setParameter("ITEM2_ADDRESS_ID",addressid);
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");

      itemset2.addRow(data);
   }


//-----------------------------------------------------------------------------
//-------------------------  OVERVIEWMODE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  address()
   {
      blnaddr= !blnaddr;
      okFindITEM0();
      searchCommAddType();
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.NEWROW);
   }


   public void  address2()
   {
      blnaddr= !blnaddr;
      okFindITEM1();
      blncomMethod = false;
      blnaddType = false ;
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      mgr.beginASPEvent();

      //=============================================
      //This Blocks refers to General Tab
      //=============================================

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").
         setHidden();

      headblk.addField("PERSON_ID").
         setSize(25).
         setMaxLength(20).
         setMandatory().
         setLabel("ENTERWPERSONINFOPERSONID: Person Id").
         setUpperCase();

      headblk.addField("NAME").
         setSize(25).
         setMandatory().
         setLabel("ENTERWPERSONINFONAME: Name");

      headblk.addField("USER_ID").
         setSize(25).
         setDynamicLOV("PERSON_INFO_FREE_USER",600,445).
         setLabel("ENTERWPERSONINFOUSERID: User ID.").  
         setUpperCase();

      headblk.addField("DEFAULT_LANGUAGE").
         setSize(25).
         enumerateValues("ISO_LANGUAGE_API").
         unsetSearchOnDbColumn(). 
         setSelectBox().
         setLabel("ENTERWPERSONINFODEFAULTLANGUAGE: Default Language");

      headblk.addField("COUNTRY").
         setSize(25).
         enumerateValues("ISO_COUNTRY_API").
         unsetSearchOnDbColumn(). 
         setSelectBox().
         setLabel("ENTERWPERSONINFOCOUNTRY: Country");

      headblk.addField("CREATION_DATE","Date").
         setSize(25).
         setLabel("ENTERWPERSONINFOCREATIONDATE: Creation Date").
         setReadOnly();

      headblk.addField("PARTY").
         setSize(11).
         setHidden(); 

      headblk.addField("DEFAULT_DOMAIN").
         setSize(14).
         setMandatory().
         setHidden().
         setLabel("ENTERWPERSONINFODEFAULTDOMAIN: Default Domain").
         setCheckBox("FALSE,TRUE");

      headblk.addField("PARTY_TYPE").
         setSize(11).
         setMandatory().
         setHidden();

      headblk.addField("PROTECTED").
         setSize(14).
         setMandatory().
         setLabel("ENTERWPERSONINFOPROTECTED: Protected").
         setCheckBox("FALSE,TRUE");  

      headblk.addField("DEFVALUE").
         setHidden().
         setFunction("' '");

      headblk.addField("PARTY_TYPE_DB").
         setHidden();

      headblk.addField("STR_DATE").
	 setFunction("' '").
         setHidden();

      // Bug 76755, Begin
      headblk.addField("COMPANY").
         setHidden().
         setFunction("' '");
      // Bug 76755, End
      // Bug 77886, Begin
      headblk.setView("PERSON_INFO_ALL");
      // Bug 77886, End
      headblk.defineCommand("PERSON_INFO_API","New__,Modify__,Remove__");

      rowset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("ENTERWPERSONINFOPERHEAD: Person - General Info");

      headbar.addCustomCommand("address", mgr.translate("ENTERWPERSONINFODTAIL: Address...")); 
      headbar.addCustomCommand("address2", mgr.translate("ENTERWPERSONINFODTAIL2: General Info...")); 
      headbar.defineCommand(headbar.VIEWDETAILS,"viewDetailsHEAD");
      // Bug 76755, Begin
      headbar.defineCommand(headbar.SAVERETURN,"saveReturn");
      // Bug 76755, End

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);


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
         setLabel("ENTERWPERSONINFOADDRESSID: Address Identity").
         setUpperCase();

      itemblk0.addField("ADDRESS").
         setHidden(). 
         setSize(29). 
         setMandatory().
         setLabel("ENTERWPERSONINFOADDRESS: Address");

      itemblk0.addField("ITEM0_COUNTRY").
         setSize(29).
         setMandatory().
         enumerateValues("ISO_COUNTRY_API").
         setSelectBox().
         unsetSearchOnDbColumn(). 
         setLabel("ENTERWPERSONINFOITEM0COUNTRY: Country").
         setCustomValidation("ITEM0_COUNTRY","COUNTRY_CODE").
         setDbName("COUNTRY");

      fCOUNTRY = itemblk0.addField("ADDRESS_COUNTRY").
                 setSize(25).
                 setMandatory().
                 setLabel("ENTERWPERSONINFOITEM0COUNTRY: Country").
                 setFunction("COUNTRY");

      fADDRESS1 = itemblk0.addField("ADDRESS1").
                  setSize(29).
                  setMandatory().
                  setLabel("ENTERWPERSONINFOADDRESS1: Address 1");

      fADDRESS2 = itemblk0.addField("ADDRESS2").
                  setSize(29).
                  setLabel("ENTERWPERSONINFOADDRESS2: Address 2"); 

      fZIP_CODE = itemblk0.addField("ZIP_CODE").
                  setSize(29).
                  setLabel("ENTERWPERSONINFOZIPCODE: Postal Code");

      fCITY = itemblk0.addField("CITY").
              setSize(29).
              setLabel("ENTERWPERSONINFOCITY: City");

      fCOUNTY = itemblk0.addField("COUNTY").
                setSize(25).
                setLabel("ENTERWPERSONINFOCOUNTY: County");  

      fSTATE = itemblk0.addField("STATE").
               setSize(29).
               setLabel("ENTERWPERSONINFOSTATE: State"). 
               setDynamicLOV("STATE_CODES",650,450);      


      fCOUNTRY_CODE = itemblk0.addField("COUNTRY_CODE").
                      setSize(25).
                      setMandatory().
                      unsetSearchOnDbColumn().
                      setLabel("ENTERWPERSONINFOCOUNTRYCODE: CountryCode").
                      setCustomValidation("COUNTRY_CODE","ADDRESS_COUNTRY,ITEM0_COUNTRY").
                      setFunction("ISO_COUNTRY_API.encode(:COUNTRY)");
      
      itemblk0.addField("VALID_FROM","Date").
         setSize(29).
         setLabel("ENTERWPERSONINFOVALIDFROM: Valid From");

      itemblk0.addField("VALID_TO","Date").
         setSize(29).   
         setLabel("ENTERWPERSONINFOVALIDTO: Valid To");

      itemblk0.addField("ITEM0_PERSON_ID").
         setSize(14). 
         setLOV("PersonLov.page",600,445).
         setMandatory().
         setHidden().
         setDbName("PERSON_ID").
         setUpperCase();

      itemblk0.addField("ITEM0_PARTY").
         setSize(14).
         setHidden().
         setDbName("PARTY");

      itemblk0.addField("ITEM0_DEFAULT_DOMAIN").
         setSize(5).
         setMandatory().
         setHidden().
         setLabel("ENTERWPERSONINFOITEM0DEFAULTDOMAIN: Default Domain").
         setDbName("DEFAULT_DOMAIN").
         setCheckBox("FALSE,TRUE");

      itemblk0.addField("ITEM0_PARTY_TYPE").
         setSize(14). 
         setMandatory().
         setHidden(). 
         setDbName("PARTY_TYPE");

      
      itemblk0.addField("DUMMY1").
         setFunction("Enterp_Address_Country_API.Get_Detailed_Address(ISO_COUNTRY_API.encode(COUNTRY))").
         setHidden();
      
      // Bug 76755, Begin, Changed the view to PERSON_INFO_ADDRESS1 
      itemblk0.setView("PERSON_INFO_ADDRESS1");
      // Bug 76755, End
      itemblk0.defineCommand("PERSON_INFO_ADDRESS_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);
      itemblk0.setTitle(mgr.translate("ENTERWPERSONINFOADDRE: Address"));

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0); 
      itembar0.addCustomCommand("detailedAddress", mgr.translate("ENTERWPERSONINFOVIEWDETADD: View Detailed Address...")); 
      itembar0.defineCommand(itembar0.OKFIND,   "okFindITEM0"); 
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW, "newRowITEM0");
      itembar0.defineCommand(itembar0.FORWARD,"forwardITEM0");
      itembar0.defineCommand(itembar0.BACKWARD, "backwardITEM0");
      itembar0.defineCommand(itembar0.FIRST,"firstITEM0");
      itembar0.defineCommand(itembar0.LAST,"lastITEM0");
      itembar0.defineCommand(itembar0.VIEWDETAILS,"viewDetailsITEM0");
      itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0");
      itembar0.defineCommand(itembar0.SAVENEW,"saveNewITEM0");
      itemtbl0 = mgr.newASPTable(itemblk0);
      itembar0.defineCommand(itembar0.EDITROW,"editItem0");
      itembar0.addCommandValidConditions("detailedAddress","DUMMY1","Enable","TRUE");
      
      itemlay0 = itemblk0.getASPBlockLayout(); 
      itemlay0.setDialogColumns(3);
      itemlay0.setDefaultLayoutMode(itemlay0.SINGLE_LAYOUT); 
      itemlay0.setAddressFieldList(fADDRESS1,fADDRESS2,fZIP_CODE,fCITY,fSTATE,fCOUNTY,fCOUNTRY_CODE,fCOUNTRY,"ENTERWPERSONINFOSADD: Address","ifs.enterw.LocalizedEnterwAddress");  

      //==============================================
      //This Blocks refers to HyperLink Adress Type
      //==============================================

      itemblk2 = mgr.newASPBlock("ITEM2");

      itemblk2.addField("ITEM2_OBJID").
         setHidden().
         setDbName("OBJID");

      itemblk2.addField("ITEM2_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      itemblk2.addField("ITEM2_PERSON_ID"). 
         setSize(11).
         setMandatory().
         setHidden().
         setLabel("ENTERWPERSONINFOITEM2PERSONID: Person Id").
         setDbName("PERSON_ID").
         setUpperCase();

      itemblk2.addField("ITEM2_ADDRESS_ID").
         setSize(11). 
         setDynamicLOV("PERSON_INFO_ADDRESS","PERSON_ID",600,445).
         setHidden().
         setLabel("ENTERWPERSONINFOITEMADDRESSID: Comp Address Id").
         setDbName("ADDRESS_ID").
         setUpperCase();

      itemblk2.addField("ADDRESS_TYPE_CODE").
         setSize(14).
         setMandatory(). 
         setLabel("ENTERWPERSONINFOADDRESSTYPECODE: Address Type").
         enumerateValues("Address_Type_Code_API").
         unsetSearchOnDbColumn(). 
         setSelectBox();

      itemblk2.addField("ITEM2_PARTY").
         setSize(11).
         setHidden().
         setLabel("ENTERWPERSONINFOITEM0PARTY: Party").
         setDbName("PARTY");

      itemblk2.addField("DEF_ADDRESS").
         setSize(8).
         setMandatory().
         setCheckBox("FALSE,TRUE").
         setLabel("ENTERWPERSONINFODEFADDRESS: Default");

      itemblk2.addField("ITEM2_DEFAULT_DOMAIN").
         setSize(11).
         setMandatory().
         setHidden().
         setLabel("ENTERWPERSONINFOITEM2DEFAULTDOMAIN: Default Domain").
         setDbName("DEFAULT_DOMAIN"); 

      itemblk2.setView("PERSON_INFO_ADDRESS_TYPE");
      itemblk2.defineCommand("PERSON_INFO_ADDRESS_TYPE_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(itemblk0);
      itemblk2.setTitle(mgr.translate("ENTERWPERSONINFOADDRESSTYPE: Address Types"));

      itemset2 = itemblk2.getASPRowSet(); 

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.OKFIND,   "okFindITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,    "newRowITEM2");

      itemtbl2 = mgr.newASPTable(itemblk2);

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(3);
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
      
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

      itemblk1.addField("ITEM1_PERSON_ID").
         setSize(11).
         setDynamicLOV("PERSON_INFO",600,445).
         setMandatory().
         setHidden().
         setLabel("ENTERWPERSONINFOITEM1PERSONID: Person Id").
         setDbName("PERSON_ID").
         setUpperCase();

      itemblk1.addField("ITEM1_COMM_ID","Number").
         setSize(15).
         setHidden().
         setLabel("ENTERWPERSONINFOITEM1COMMID: Comp Comm Id").
         setDbName("COMM_ID");
   
      itemblk1.addField("ITEM1_NAME").
         setSize(15).
         setLabel("ENTERWPERSONINFOITEM1NAME: Name").
         setDbName("NAME");
   
      itemblk1.addField("ITEM1_DESCRIPTION").
         setSize(11).
         setLabel("ENTERWPERSONINFOITEM1DESCRIPTION: Description").
         setDbName("DESCRIPTION");

      itemblk1.addField("ITEM1_METHOD_ID").
         setSize(13).
         setMandatory().
         enumerateValues("COMM_METHOD_CODE_API").
         unsetSearchOnDbColumn(). 
         setSelectBox().
         setLabel("ENTERWPERSONINFOITEM1METHODID: Comm. Method").
         setDbName("METHOD_ID");

      itemblk1.addField("ITEM1_VALUE").
         setSize(11).
         setMandatory().
         setLabel("ENTERWPERSONINFOITEM1VALUE: Value").
         setDbName("VALUE");

      itemblk1.addField("ITEM1_ADDRESS_ID").
         setSize(13).
         setDynamicLOV("PERSON_INFO_ADDRESS","PERSON_ID",600,445).
         setLabel("ENTERWPERSONINFOITEM1ADDRESSID: Address Id.").
         setDbName("ADDRESS_ID").
         setUpperCase();

      itemblk1.addField("ITEM1_METHOD_DEFAULT").
         setSize(15).
         setMandatory().
         setCheckBox("FALSE,TRUE").
         setLabel("ENTERWPERSONINFOITEM1METHODDEFAULT: Method Default").
         setDbName("METHOD_DEFAULT");

      itemblk1.addField("ITEM1_ADDRESS_DEFAULT").
         setSize(15).
         setMandatory(). 
         setCheckBox("FALSE,TRUE").
         setLabel("ENTERWPERSONINFOITEM1ADDRESSDEFAULT: Address Default").
         setDbName("ADDRESS_DEFAULT");

      itemblk1.addField("ITEM1_VALID_FROM","Date").
         setSize(13).
         setLabel("ENTERWPERSONINFOITEM1VALIDFROM: Valid From").
         setDbName("VALID_FROM");

      itemblk1.addField("ITEM1_VALID_TO","Date").
         setSize(11).
         setLabel("ENTERWPERSONINFOITEM1VALIDTO: Valid To").
         setDbName("VALID_TO");

      itemblk1.setView("PERSON_INFO_COMM_METHOD");
      itemblk1.defineCommand("PERSON_INFO_COMM_METHOD_API","New__,Modify__,Remove__");
      itemblk1.setMasterBlock(headblk);
      itemblk1.setTitle(mgr.translate("ENTERWPERSONINFOCOMMMT: Communication Methods"));

      itemset1 = itemblk1.getASPRowSet(); 

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnITEM1");  
      itembar1.defineCommand(itembar1.SAVENEW,"saveNewITEM1");  


      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle("ENTERWPERSONINFOCOMMED: Communication Methods");

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDialogColumns(3);
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);

      b = mgr.newASPBlock("DEFAULT_LANGUAGE");

      b.addField("CLIENT_VALUES0");

      b = mgr.newASPBlock("COUNTRY");

      b.addField("CLIENT_VALUES1");

      b = mgr.newASPBlock("ITEM0_COUNTRY");

      b.addField("CLIENT_VALUES2");

      b = mgr.newASPBlock("ITEM1_METHOD_ID");

      b.addField("CLIENT_VALUES3");
   }


   public void  adjust()
   {
      ASPManager mgr= getASPManager();

      if ( rowset.countRows() == 0 )
      {
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);  
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.removeCustomCommand("address");
         headbar.removeCustomCommand("address2");

      }

      //Bug 65132, Begin
      if (headlay.isSingleLayout()) {
          if(rowset.countRows() > 0){
              rowset.refreshRow();
          }
             
      }
      //Bug 65132, End

      if ( itemset0.countRows() == 0 )
      {
         itembar0.disableCommand(itembar0.FORWARD);
         itembar0.disableCommand(itembar0.BACK);
         itembar0.disableCommand(itembar0.DUPLICATEROW);
         itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
      }

      if (blnaddr)
      {
         headbar.removeCustomCommand("address");
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.NEWROW);
      }
      else
         headbar.removeCustomCommand("address2");

      if (headlay.isMultirowLayout())
         headbar.removeCustomCommand("address");

      if (headlay.isEditLayout())
      {   
          mgr.getASPField("PERSON_ID").setReadOnly();
      }
   }

   

   public void forwardITEM0()
   {
      ASPManager mgr = getASPManager();

      itemset0.next();
      int rowitem0No = itemset0.getCurrentRowNo();
      searchCommAddType();
      itemset0.goTo(rowitem0No);
   }

   


   public void backwardITEM0()
   {
      ASPManager mgr =  getASPManager();

      itemset0.previous();
      int rowbackNo = itemset0.getCurrentRowNo();
      searchCommAddType();
      itemset0.goTo(rowbackNo);
   }


   public void firstITEM0()
   {
      itemset0.first();
      searchCommAddType();
   }


   public void lastITEM0()
   {
      ASPManager mgr = getASPManager();

      itemset0.last();
      searchCommAddType();
      int intcurrrow1 = itemset0.countRows(); 
      itemset0.goTo(intcurrrow1);
   } 



   public void viewDetailsHEAD()
   {
      ASPManager mgr = getASPManager();

      rowset.goTo(rowset.getRowSelected());
      headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      if (blnaddr)
      {
         okFindITEM0();
         searchCommAddType();
      }
      else
         okFindITEM1();      
   }


   public void viewDetailsITEM0()
   {
      ASPManager mgr = getASPManager();

      itemset0.goTo(itemset0.getRowSelected());
      itemlay0.setLayoutMode(itemlay0.SINGLE_LAYOUT);
      searchCommAddType();
   }

   public void editItem0()
   {
      itemlay0.setLayoutMode(itemlay0.EDIT_LAYOUT);
   }

   public void detailedAddress()
   {
      detFlag = "true";

      if ( itemlay0.isSingleLayout() )
      {
         itemset0.selectRows();
         addRowNo = itemset0.getCurrentRowNo();
      }

      if ( itemlay0.isMultirowLayout() )
         addRowNo = itemset0.getRowSelected();

      itemset0.goTo(addRowNo);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      if (blnaddr)
         return "ENTERWPERSONINFOGENTITLE: Person - Address Info";
      else
         return "ENTERWPERSONINFOADDRTITLE: Person - General Info";   
   }

   protected String getTitle()
   {
      if (blnaddr)
         return "ENTERWPERSONINFOGENTITLE: Person - Address Info";
      else
         return "ENTERWPERSONINFOADDRTITLE: Person - General Info";   
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }
      if (itemlay1.isVisible() && !blnaddr && (rowset.countRows() > 0))
      {
         appendToHTML(itemlay1.show());
      }
      if (itemlay0.isVisible() && blnaddr && (rowset.countRows() > 0))
      {
         appendToHTML(itemlay0.show());
      }

      if (blnaddType && itemlay2.isVisible() && itemlay0.isVisible() && (itemset0.countRows() > 0) || itemlay2.isEditLayout() || itemlay2.isNewLayout())
      {
         appendToHTML(itemlay2.show());
      }
      if (blncomMethod && itemlay1.isVisible() && !itemlay0.isMultirowLayout())
      {
         appendToHTML(itemlay1.show());
      }

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

      int current_row = rowset.getCurrentRowNo();
      int current_row0;
      if ( itemlay0.isMultirowLayout() ){
         itemset0.goTo(addRowNo);
         current_row0 = itemset0.getRowSelected();
      }
         
      else
      {
         current_row0 = itemset0.getCurrentRowNo();
         itemset0.goTo(current_row0);
      }
         
      rowset.goTo(current_row);
      

      if (itemset0.countRows()>0)
      {
         appendDirtyJavaScript("function newWindow()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   url_ = \"DlgPersonInfoAddress.page?PERSON_ID=\" + escape('");
         if (rowset.countRows()>0)
         {
            appendDirtyJavaScript(mgr.URLEncode(rowset.getRow().getFieldValue("PERSON_ID")));
            appendDirtyJavaScript("') + \n");
         }

         appendDirtyJavaScript("\"&ADDRESS_ID=\" + escape('");
         appendDirtyJavaScript(mgr.URLEncode(itemset0.getRow().getFieldValue("ADDRESS_ID")));
         appendDirtyJavaScript("') + ");

         appendDirtyJavaScript("\"&PARTY=\" + escape('");
         appendDirtyJavaScript(mgr.URLEncode(rowset.getRow().getFieldValue("PARTY")));
         appendDirtyJavaScript("') + ");

         appendDirtyJavaScript("\"&DEFAULT_DOMAIN=\" + escape('");
         appendDirtyJavaScript(mgr.URLEncode(rowset.getRow().getFieldValue("DEFAULT_DOMAIN")));
         appendDirtyJavaScript("') + ");

         appendDirtyJavaScript("\"&COUNTRY=\" + escape('");
         appendDirtyJavaScript(mgr.URLEncode(itemset0.getRow().getFieldValue("ITEM0_COUNTRY")));
         appendDirtyJavaScript("') + ");

         appendDirtyJavaScript("\"&PARTY_TYPE=\" + escape('");
         appendDirtyJavaScript(mgr.URLEncode(rowset.getRow().getFieldValue("PARTY_TYPE")));
         appendDirtyJavaScript("');\n");

         appendDirtyJavaScript("   window.open(url_,\"Structure\",\"resizable=yes,toolbar=no,scrollbars=yes,screenX=500,screenY=100,width=600,height=300\");\n");
         appendDirtyJavaScript("   return true;\n");
         appendDirtyJavaScript("}\n");
         if ("true".equals(detFlag))
         {
            appendDirtyJavaScript("newWindow()");
         }
      } 
      // Bug 73244, Changed Errornous code
      if (itemlay0.isMultirowLayout())
          itemset0.goTo(current_row0);        
   }

}
