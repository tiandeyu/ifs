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
*  File        : EDMBasic.java 
*  Modified    :
*    ASP2JAVA Tool  2001-03-01  - Created Using the ASP file EDMBasic.asp
*    prsalk	2001-05-02 - Fixed Call Id 64144
*    nisilk	2003-01-24 - Added LOV for Document Type
*    nisilk	2003-01-27 - Added new tab Document Type     
*    mdahse     2003-02-26 - Changed getTitle()
*    dhpelk     2003-06-20 - Fixed Call 96010
*    Shtolk    2003-07-31 - Fixed Call ID 99902, Changed tab name from 'Document Type' to 'Document Types'. 
*    NiSilk    2003-08-01 - Fixed call 95769, modified method adjust().
*    InoSlk    2003-08-29 - Call ID 101731: Modified doReset() and clone().
*    Bakalk    2003-09-03 - Call ID 102202: Added * to mandatory fields in Repository User Id
*                           and modified saveReturnITEM5() and saveNewITEM5().
*    InoSlk    2003-10-22 - Call ID 107656: Removed data type NUMBER in ASPField FT_DOC_NO.
*    BaKalk    2003-12-17 - Web Alignment (Tabs) done.
*    BaKalk    2004-01-27 - Modified encryptPassword() and decryptPassword();
*    ThWilk    2006-02-07 - Modified methods predefine,adjust and redesigned the page by removing HTML code and java script methods;
*   KARALK     2006-02-09 - call 133178. rename the tab as "Repository Address and User".
*    BAKALK    2006-07-25 - Bug ID 58216, Fixed Sql Injection.
*    KARALK    2006-12-15 - DMPR303 added template class column to itemblk2.
*    SANSLK    2008-09-01 - Bug Id 75038 Set Mandotory For field DESCRIPTION and field FILETYPE In Application 
*    SANSLK    2008-09-02 - Bug Id 75038 Set Mandotory For feild DOCUMENT TYPE
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class EDMBasic extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.EDMBasic");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPHTMLFormatter fmt;
   private ASPLog log;
   private ASPForm frm;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
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
   private ASPBlock itemblk3;
   private ASPRowSet itemset3;
   private ASPCommandBar itembar3;
   private ASPTable itemtbl3;
   private ASPBlockLayout itemlay3;
   private ASPBlock itemblk4;
   private ASPRowSet itemset4;
   private ASPCommandBar itembar4;
   private ASPTable itemtbl4;
   private ASPBlockLayout itemlay4;
   private ASPBlock itemblk5;
   private ASPRowSet itemset5;
   private ASPCommandBar itembar5;
   private ASPTable itemtbl5;
   private ASPBlockLayout itemlay5;
   private ASPBlock itemblk6;
   private ASPRowSet itemset6;
   private ASPCommandBar itembar6;
   private ASPTable itemtbl6;
   private ASPBlockLayout itemlay6;
   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPQuery q;
   private ASPBuffer data;
   private ASPBuffer keys;

   private String temp;
   private String val;
   private String lanDescipt;
   private String formatDescipt;
   private String docTitle;
   private String className;
   private String loc_address;
   private String password;

   private int headrowno;
   private int currrow;
   private int currRow;

   private boolean duplicate=false;

   //===============================================================
   // Construction 
   //===============================================================
   public EDMBasic(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      cmd   = null;
      q   = null;
      data   = null;
      keys   = null;

      temp   = null;
      val   = null;
      lanDescipt   = null;
      formatDescipt   = null;
      docTitle   = null;
      className   = null;
      loc_address   = null;
      password   = null;

      headrowno   = 0;
      currrow   = 0;
      currRow   = 0;

      duplicate = false;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      EDMBasic page = (EDMBasic)(super.clone(obj));

      //Initializing mutable attributes
      page.trans   = null;
      page.cmd   = null;
      page.q   = null;
      page.data   = null;
      page.keys   = null;

      page.temp   = null;
      page.val   = null;
      page.lanDescipt   = null;
      page.formatDescipt   = null;
      page.docTitle   = null;
      page.className   = null;
      page.loc_address   = null;
      page.password   = null;

      page.headrowno   = 0;
      page.currrow   = 0;
      page.currRow   = 0;

      page.duplicate = false;

      //Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.fmt = page.getASPHTMLFormatter();
      page.log = page.getASPLog();
      page.frm = page.getASPForm();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk1 = page.getASPBlock(itemblk1.getName());
      page.itemset1 = page.itemblk1.getASPRowSet();
      page.itembar1 = page.itemblk1.getASPCommandBar();
      page.itemtbl1 = page.getASPTable(itemtbl1.getName());
      page.itemlay1 = page.itemblk1.getASPBlockLayout();
      page.itemblk2 = page.getASPBlock(itemblk2.getName());
      page.itemset2 = page.itemblk2.getASPRowSet();
      page.itembar2 = page.itemblk2.getASPCommandBar();
      page.itemtbl2 = page.getASPTable(itemtbl2.getName());
      page.itemlay2 = page.itemblk2.getASPBlockLayout();
      page.itemblk3 = page.getASPBlock(itemblk3.getName());
      page.itemset3 = page.itemblk3.getASPRowSet();
      page.itembar3 = page.itemblk3.getASPCommandBar();
      page.itemtbl3 = page.getASPTable(itemtbl3.getName());
      page.itemlay3 = page.itemblk3.getASPBlockLayout();
      page.itemblk4 = page.getASPBlock(itemblk4.getName());
      page.itemset4 = page.itemblk4.getASPRowSet();
      page.itembar4 = page.itemblk4.getASPCommandBar();
      page.itemtbl4 = page.getASPTable(itemtbl4.getName());
      page.itemlay4 = page.itemblk4.getASPBlockLayout();
      page.itemblk5 = page.getASPBlock(itemblk5.getName());
      page.itemset5 = page.itemblk5.getASPRowSet();
      page.itembar5 = page.itemblk5.getASPCommandBar();
      page.itemtbl5 = page.getASPTable(itemtbl5.getName());
      page.itemlay5 = page.itemblk5.getASPBlockLayout();
      page.itemblk6 = page.getASPBlock(itemblk6.getName());
      page.itemset6 = page.itemblk6.getASPRowSet();
      page.itembar6 = page.itemblk4.getASPCommandBar();
      page.itemtbl6 = page.getASPTable(itemtbl6.getName());
      page.itemlay6 = page.itemblk6.getASPBlockLayout();

      page.tabs = page.getASPTabContainer();

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();
      log = mgr.getASPLog();

      frm = mgr.getASPForm();
      log = mgr.getASPLog();
      temp="";

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else
         okFindITEM1();

      adjust();
      tabs.saveActiveTab();//w.a.
   }

   //================================================================================================
   // 			Tab activate functions
   //================================================================================================

   public void  activateDocFileTemplate()
   {

      tabs.setActiveTab(1);
      okFindITEM1();  
   }


   public void  activateRepository()
   {

      tabs.setActiveTab(2);
      okFindITEM3();
   }


   public void  activateApplication()
   {

      tabs.setActiveTab(3);
      okFindITEM4(); 
   }


   public void  activateRepositoryAddressAndUser()
   {

      tabs.setActiveTab(4);
      okFindITEM5();
   }

   public void  activateDocumentType()
   {

      tabs.setActiveTab(5);
      okFindITEM6();
   }


   //================================================================================================
   // 			Tab activate functions
   //================================================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

      if ("FT_LAN_CODE".equals(val))
      {
         cmd = trans.addCustomFunction("LANDES","APPLICATION_LANGUAGE_API.Get_Description","FT_LAN_DESC");
         cmd.addParameter("FT_LAN_CODE");
         trans = mgr.validate(trans);
         lanDescipt = trans.getValue("LANDES/DATA/FT_LAN_DESC");
         trans.clear();
         if (mgr.isEmpty(lanDescipt))
            lanDescipt = "";
         mgr.responseWrite( lanDescipt + "^" );
      }

      else if ("FT_FORMAT_SIZE".equals(val))
      {
         cmd = trans.addCustomFunction("FORMATDES","DOC_FORMAT_API.Get_Description","FT_FORMAT_DESC");
         cmd.addParameter("FT_FORMAT_SIZE");
         trans = mgr.validate(trans);
         formatDescipt = trans.getValue("FORMATDES/DATA/FT_FORMAT_DESC");
         trans.clear();
         if (mgr.isEmpty(formatDescipt))
            formatDescipt = "";
         mgr.responseWrite( formatDescipt + "^" );
      }

      else if ("FT_DOC_NO".equals(val))
      {
         cmd = trans.addCustomFunction("DTITLE","DOC_TITLE_API.Get_Title","FT_TITLE");
         cmd.addParameter("CDOC_CLASS", mgr.readValue("CDOC_CLASS"));
         cmd.addParameter("FT_DOC_NO");
         trans = mgr.validate(trans);
         docTitle = trans.getValue("DTITLE/DATA/FT_TITLE");
         trans.clear();
         if (mgr.isEmpty(docTitle))
            docTitle = "";
         mgr.responseWrite( docTitle + "^" );
      }

      else if ("LOC_DOC_CLASS".equals(val))
      {
         cmd = trans.addCustomFunction("LOCDOCCL","DOC_CLASS_API.Get_Name","LOC_CLASS_DESC");
         cmd.addParameter("LOC_DOC_CLASS");
         trans = mgr.validate(trans);
         className = trans.getValue("LOCDOCCL/DATA/LOC_CLASS_DESC");
         trans.clear();
         if (mgr.isEmpty(className))
            className = "";
         mgr.responseWrite( className + "^" );
      }
      mgr.endResponse();
   }

   //================================================================================================
   // 			Command Bar functions
   //================================================================================================
   /////////////////////////////////////////////////////////////////  ITEM1  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk1);
      q.includeMeta("ALL");
      mgr.submit(trans);
      if (itemset1.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWEDMBASICNODATAFOUND: No data found."));
         itemset1.clear();
      }
//      else
//      {
//         itemlay1.setLayoutMode(itemlay1.SINGLE_LAYOUT);
//         okFindITEM2();
//         mgr.createSearchURL(itemblk1);
//      }
      eval(itemset1.syncItemSets());
   }


   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay1.setCountValue(toInt(itemset1.getValue("N")));
      itemset1.clear();
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","DOC_CLASS_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }


   public void  cancelNewITEM1()
   {

      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      okFindITEM1();
   }

   /////////////////////////////////////////////////////////////////  ITEM2  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk2);
      //bug 58216 starts
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("CDOC_CLASS",itemset1.getValue("DOC_CLASS"));
      //bug 58216 end
      q.includeMeta("ALL");
      headrowno = itemset1.getCurrentRowNo();
      mgr.submit(trans);
      itemset1.goTo(headrowno);
      itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
   }


   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getValue("N")));
      itemset2.clear();
   }


   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM2","EDM_FILE_TEMPLATE_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      itemset2.addRow(data);
      itemset2.setValue("DOC_CLASS", itemset1.getRow().getValue("DOC_CLASS"));
   }

   /////////////////////////////////////////////////////////////////  ITEM3  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void  okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk3);
      q.includeMeta("ALL");
      mgr.submit(trans);
      if (itemset3.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWEDMBASICNODATAFOUND: No data found."));
         itemset3.clear();
      }
   }


   public void  countFindITEM3()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getValue("N")));
      itemset3.clear();
   }


   public void  newRowITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM3","EDM_LOCATION_API.New__",itemblk3);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      itemset3.addRow(data);
   }

   /////////////////////////////////////////////////////////////////  ITEM4  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk4);
      q.includeMeta("ALL");
      mgr.submit(trans);
      if (itemset4.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWEDMBASICNODATAFOUND: No data found."));
         itemset4.clear();
      }
   }


   public void  countFindITEM4()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk4);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay4.setCountValue(toInt(itemset4.getValue("N")));
      itemset4.clear();
   }


   public void  newRowITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM4","EDM_APPLICATION_API.New__",itemblk4);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM4/DATA");
      itemset4.addRow(data);
   }

   /////////////////////////////////////////////////////////////////  ITEM5  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void  okFindITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk5);
      q.includeMeta("ALL");
      mgr.submit(trans);
      if (itemset5.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWEDMBASICNODATAFOUND: No data found."));
         itemset5.clear();
      }
   }


   public void  countFindITEM5()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk5);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay5.setCountValue(toInt(itemset5.getValue("N")));
      itemset5.clear();
   }


   public void  newRowITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM5","EDM_LOCATION_USER_API.New__",itemblk5);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM5/DATA");
      itemset5.addRow(data);
   }




   public String encryptPassword(String password)
   {
      String enPass = new String();
      int nLen;
      int nTemp;
      int nAsciiVal;
      String str = new String();
      char charStr;

      nLen=password.length();

      while (nLen>0)
      {
         charStr = password.charAt(nLen-1);  //extract individual characters 
         nAsciiVal=(int)charStr;

         //add if even else subtract
         if ((nLen % 2)==0)    //if even then add otherwise(odd) subtract
            nAsciiVal = nAsciiVal + nLen;
         else
            nAsciiVal = nAsciiVal - nLen;

         if (nAsciiVal==94) //remove illegal char - '=' and '^'
            nAsciiVal=1;

         if (nAsciiVal==61)
            nAsciiVal=2;

         str = str.valueOf(nAsciiVal);

         /*if (str.length()==2)
            str="0" + str;

         if (str.length() == 1)
            str="00" + str;*/
         while(str.length()<5){
            str="0" + str;
         }

         enPass = enPass + str;  //concat characters   
         --nLen;
      }


      return enPass; //return encrypted pasword

   }



   public String decryptPassword( String password )
   {

      String dePass = new String();
      int nLen;
      int nTemp;
      int nAsciiVal;
      String str = new String();
      char charStr;
      int conLen;
      int x;
      String ascStr = new String();
      Integer intObj= new Integer(0);

      //decode ascii string

      nLen=password.length();


      x = 0;

      while (x<nLen)
      {
         ascStr = password.substring(x, x + 5); 
         nAsciiVal = intObj.parseInt(ascStr);
         dePass = dePass + dePass.valueOf((char)nAsciiVal);
         x = x + 5; 
      }  


      password = dePass;

      dePass="";

      nLen=password.length();    
      conLen=nLen;


      while (nLen>0)
      {
         charStr = password.charAt(nLen-1);
         nAsciiVal=(int)(charStr);

         if (nAsciiVal==1)
            nAsciiVal=94;

         if (nAsciiVal==2)
            nAsciiVal=61;

         //subtract if even else add
         if (( (conLen - nLen + 1) % 2)==0)
            nAsciiVal = nAsciiVal - (conLen - nLen + 1);
         else
            nAsciiVal = nAsciiVal + (conLen - nLen + 1);


         str = str.valueOf((char)nAsciiVal);       
         dePass = dePass + str; 
         nLen--;

      }

      return dePass;
   }


   public String validateITEM5()
   {
      ASPManager mgr = getASPManager();
      String Error_Msg="";
      
        
      if(mgr.isEmpty(mgr.readValue("LOCATION_ADDRESS")))
      {
         Error_Msg = mgr.translate("DOCMAWEDMBAICMANDATORYFIELDLOCATION_ADDRESS: Field[LOCATION_ADDRESS] must have a value");
      }
      else if(mgr.isEmpty(mgr.readValue("LOCATION_USER")))
      {
         Error_Msg = mgr.translate("DOCMAWEDMBAICMANDATORYFIELDLOCATION_USER: Field[LOCATION_USER] must have a value");
      }
      else if(mgr.isEmpty(mgr.readValue("LOCATION_PASSWORD")))
      {
         Error_Msg = mgr.translate("DOCMAWEDMBAICMANDATORYFIELDLOCATION_PASSWORD: Field[LOCATION_PASSWORD] must have a value");
      }

      return Error_Msg;

   }



   public void  saveReturnITEM5()
   {
      ASPManager   mgr = getASPManager();
      String Error_Msg = validateITEM5();
      

      if(!"".equals(Error_Msg))
      {
         mgr.showError(Error_Msg);
         return;
      }


      currrow = itemset5.getCurrentRowNo();

      

      itemset5.changeRow();
      
      temp = encryptPassword(itemset5.getRow().getValue("LOCATION_PASSWORD"));
      
      loc_address = itemset5.getRow().getValue("LOCATION_ADDRESS");

      itemset5.setValue("LOCATION_PASSWORD","");

      mgr.submit(trans);
      trans.clear();

      //update password field   

      cmd = trans.addCustomCommand("UPDATEPASSWORD","EDM_LOCATION_USER_API.Save_Web_Password");
      cmd.addParameter("LOCATION_ADDRESS",loc_address);
      cmd.addParameter("LOCATION_PASSWORD",temp);

      mgr.submit(trans);
      trans.clear();
      itemset5.goTo(currrow);
   }


   public void  saveNewITEM5()
   {
      ASPManager mgr = getASPManager();
      String Error_Msg = validateITEM5();
      
      if(!"".equals(Error_Msg))
      {
         mgr.showError(Error_Msg);
         return;
      }

      trans.clear();
      currRow = itemset5.getCurrentRowNo();
      itemset5.changeRow();

      temp = encryptPassword(itemset5.getRow().getValue("LOCATION_PASSWORD"));
      loc_address = itemset5.getRow().getValue("LOCATION_ADDRESS");      
      itemset5.setValue("LOCATION_PASSWORD","");
      mgr.submit(trans);
      trans.clear();

      cmd = trans.addCustomCommand("UPDATEPASSWORD","EDM_LOCATION_USER_API.Save_Web_Password");
      cmd.addParameter("LOCATION_ADDRESS",loc_address);
      cmd.addParameter("LOCATION_PASSWORD",temp);

      mgr.submit(trans);
      trans.clear();
      itemset5.goTo(currRow);
      newRowITEM5();
   }


   public void  editRowITEM5()
   {
      ASPManager mgr = getASPManager();

      if (itemlay5.isMultirowLayout())
      {
         itemset5.storeSelections(); 
         itemset5.setFilterOn();
      }
      else
         itemset5.selectRow();

      cmd = trans.addCustomFunction("GETPASSWORD","EDM_LOCATION_USER_API.Get_Web_Password","DUMMY");
      cmd.addParameter("LOCATION_ADDRESS",itemset5.getRow().getValue("LOCATION_ADDRESS"));   
      trans = mgr.perform(trans);

      password = trans.getValue("GETPASSWORD/DATA/DUMMY");

      itemset5.setFilterOff();   

      temp="";
      
      if (!mgr.isEmpty(password))
         temp=decryptPassword(password);
      
      itemset5.setValue("LOCATION_PASSWORD",temp);    

      itemlay5.setLayoutMode(itemlay5.EDIT_LAYOUT);   
      trans.clear();

      itemset5.unselectRows();
   }


   public void duplicateITEM5()
   {
      ASPManager mgr = getASPManager();
      itemset5.storeSelections();
      itemset5.setFilterOn();

      String repAdd = itemset5.getRow().getValue("LOCATION_ADDRESS");
      String repUser = itemset5.getRow().getValue("LOCATION_USER");
      String repPass;
      String repDesc = itemset5.getRow().getValue("DESCRIPTION");

      trans.clear();
      cmd = trans.addCustomFunction("GETPASSWORD","EDM_LOCATION_USER_API.Get_Web_Password","DUMMY");
      cmd.addParameter("LOCATION_ADDRESS",itemset5.getRow().getValue("LOCATION_ADDRESS"));   
      trans = mgr.perform(trans);

      password = trans.getValue("GETPASSWORD/DATA/DUMMY");

      
      if (!mgr.isEmpty(password))
         repPass=decryptPassword(password);
      else
         repPass="";
      
      newRowITEM5(); // adds a new row to the rowset

      //set values to the new row
      itemset5.setValue("LOCATION_ADDRESS",repAdd); 
      itemset5.setValue("LOCATION_USER",repUser);
      itemset5.setValue("DESCRIPTION",repDesc);
      itemset5.setValue("LOCATION_PASSWORD",repPass);
      itemlay5.setLayoutMode(itemlay5.NEW_LAYOUT); 
      itemset5.setFilterOff();
      duplicate = true;
   }



   /////////////////////////////////////////////////////////////////  ITEM6  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void  okFindITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk6);
      q.includeMeta("ALL");
      mgr.submit(trans);
      if (itemset6.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWEDMBASICNODATAFOUND: No data found."));
         itemset6.clear();
      }
   }


   public void  countFindITEM6()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk6);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay6.setCountValue(toInt(itemset6.getValue("N")));
      itemset6.clear();
   }


   public void  newRowITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM6","DOCUMENT_TYPE_API.New__",itemblk6);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM6/DATA");
      itemset6.addRow(data);
   }



   //================================================================================================
   // 		                    Custom Commands
   //================================================================================================

   public void  documentInfo()
   {
      ASPManager mgr = getASPManager();

      itemset2.storeSelections();
      if (itemlay2.isSingleLayout())
      {
         itemset2.selectRow();
      }
      keys=itemset2.getSelectedRows("FT_DOC_CLASS, FT_DOC_NO, FT_DOC_REV");
      ctx.setGlobal("CALLING_URL", mgr.getURL());
      mgr.transferDataTo("DocIssue.page",keys);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("activateDocFileTemplate", "Document File Template");
      headbar.addCustomCommand("activateRepository", "Repositories");
      headbar.addCustomCommand("activateApplication", "Application");
      headbar.addCustomCommand("activateRepositoryAddressAndUser", "Repository Address and User");
      headbar.addCustomCommand("activateDocumentType", "Document Type");

      /////////////////////////// ITEMBLK1 ////////////////////////////////////
      // Master block for: Documnet File Template
      itemblk1 = mgr.newASPBlock("ITEM1");
      itemblk1.addField("ITEM1_OBJID").
      setHidden().
      setDbName("OBJID");
      itemblk1.addField("ITEM1_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      itemblk1.addField("CDOC_CLASS").
      setDbName("DOC_CLASS").
      setSize(13).
      setReadOnly().
      setMaxLength(12).
      setLabel("DOCMAWEDMBASICDOCCLASS: Document Class");
      itemblk1.addField("CDOC_NAME").
      setDbName("DOC_NAME").
      setSize(24).
      setMaxLength(24).
      setReadOnly().
      setLabel("DOCMAWEDMBASICDOCNAME: Description");
      itemblk1.setView("DOC_CLASS");
      itemblk1.defineCommand("DOC_CLASS_API","New__,Modify__,Remove__");
      itemset1 = itemblk1.getASPRowSet();
      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.CANCELNEW,"cancelNewITEM1");
      itembar1.disableCommand(itembar1.NEWROW);
      itembar1.disableCommand(itembar1.DUPLICATEROW);
      itembar1.disableCommand(itembar1.DELETE);
      itembar1.disableCommand(itembar1.EDITROW);
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("DOCMAWEDMBASICDOCCLASSTITLE: Document File Template"));
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDialogColumns(1);
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      /////////////////////////// ITEMBLK2 ////////////////////////////////////
      // Details block for: Documnet File Template
      itemblk2 = mgr.newASPBlock("ITEM2");
      itemblk2.addField("ITEM2_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk2.addField("ITEM2_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk2.addField("FT_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();

      itemblk2.addField("FT_LAN_CODE").
      setDbName("LANGUAGE_CODE").
      setDynamicLOV("APPLICATION_LANGUAGE","FT_LAN_CODE").
      setLOVProperty("TITLE",mgr.translate("DOCMAWEDMBASICLISTOFLAN: List of languages")).
      setSize(20).
      setMaxLength(2).
      setMandatory().
      setInsertable().
      setReadOnly().
      setCustomValidation("FT_LAN_CODE", "FT_LAN_DESC").
      setLabel("DOCMAWEDMBASICFTLANCODE: Language Code");

      itemblk2.addField("FT_LAN_DESC").
      setFunction("APPLICATION_LANGUAGE_API.Get_Description(:FT_LAN_CODE)").
      setSize(20).
      setMaxLength(50).
      setReadOnly().
      setLabel("DOCMAWEDMBASICFTLANDESC: Language Description");

      itemblk2.addField("FT_FORMAT_SIZE").
      setDbName("FORMAT_SIZE").
      setDynamicLOV("DOC_FORMAT").
      setSize(6).
      setInsertable().
      setReadOnly().
      setCustomValidation("FT_FORMAT_SIZE", "FT_FORMAT_DESC").
      setLabel("DOCMAWEDMBASICFTFORMATSIZE: Format");

      itemblk2.addField("FT_FORMAT_DESC").
      setFunction("DOC_FORMAT_API.Get_Description(:FT_FORMAT_SIZE)").
      setSize(20).
      setMaxLength(250).
      setReadOnly().
      setLabel("DOCMAWEDMBASICFTFORMATDESC: Format Description");

      //DMPR303 START
      itemblk2.addField("TEMPLATE_CLASS").
      setDynamicLOV("DOC_CLASS").
      setSize(20).
      setMaxLength(120).
      setMandatory().
      setInsertable().
      setLabel("DOCMAWEDMBASICFTTEMPCLASS: Template Class");


      itemblk2.addField("FT_DOC_NO").
      setDbName("DOC_NO").
      setDynamicLOV("DOC_TITLE","TEMPLATE_CLASS DOC_CLASS, FT_DOC_NO DOC_NO").
      setSize(20).
      setMaxLength(120).
      setMandatory().
      setInsertable().
      setCustomValidation("TEMPLATE_CLASS, FT_DOC_NO", "FT_TITLE").
      setLabel("DOCMAWEDMBASICFTDOCNO: Document No");

      itemblk2.addField("FT_DOC_SHEET").
      setDbName("DOC_SHEET").
      setDynamicLOV("DOC_ISSUE_LOV1", "TEMPLATE_CLASS DOC_CLASS, FT_DOC_NO DOC_NO, FT_DOC_SHEET DOC_SHEET").
      setMandatory().
      setInsertable().
      setSize(8).
      setMaxLength(6).
      setLabel("DOCMAWEDMBASICFTDOCSHEET: Sheet");

      itemblk2.addField("FT_DOC_REV").
      setDbName("DOC_REV").
      setDynamicLOV("DOC_ISSUE", "TEMPLATE_CLASS DOC_CLASS, FT_DOC_NO DOC_NO, FT_DOC_SHEET DOC_SHEET").
      setMandatory().
      setInsertable().
      setSize(8).
      setMaxLength(6).
      setLabel("DOCMAWEDMBASICFTDOCREV: Revision");

      itemblk2.addField("FT_TITLE").
      setFunction("DOC_TITLE_API.Get_Title(:TEMPLATE_CLASS, :FT_DOC_NO)").
      setSize(20).
      setMaxLength(250).
      setReadOnly().
      setLabel("DOCMAWEDMBASICFTTITLE: Title");
      itemblk2.addField("FT_DESC").
      setDbName("DESCRIPTION").
      setSize(34).
      setMaxLength(40).
      setInsertable().
      setLabel("DOCMAWEDMBASICFTDESC: Document File Template Description");
      //DMPR303 END.
      itemblk2.setView("EDM_FILE_TEMPLATE");
      itemblk2.defineCommand("EDM_FILE_TEMPLATE_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(itemblk1);
      itemset2 = itemblk2.getASPRowSet();
      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.addCustomCommand("documentInfo",mgr.translate("DOCMAWEDMBASICDOCUMENTINFO: Document Info..."));
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("DOCMAWEDMBASICDOCFILETEMPTITLE: Document File Template Details"));
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(2);
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
      /////////////////////////// ITEMBLK3 ////////////////////////////////////
      // Block for: Repositories
      itemblk3 = mgr.newASPBlock("ITEM3");
      itemblk3.addField("ITEM3_OBJID").
      setDbName("OBJID").
      setHidden();
      itemblk3.addField("ITEM3_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();
      itemblk3.addField("LOC_NAME").
      setDbName("LOCATION_NAME").
      setSize(24).
      setMaxLength(30).
      setUpperCase().
      setMandatory().
      setReadOnly().
      setInsertable().
      setLabel("DOCMAWEDMBASICLOCREPOSI: Repository ID");
      itemblk3.addField("LOC_DOC_CLASS").
      setDbName("DOC_CLASS").
      setDynamicLOV("DOC_CLASS").
      setSize(12).
      setMaxLength(12).
      setInsertable().
      setReadOnly().
      setMandatory().
      setCustomValidation("LOC_DOC_CLASS", "LOC_CLASS_DESC").
      setLabel("DOCMAWEDMBASICLOCDOCCLASS: Document Class");
      itemblk3.addField("LOC_CLASS_DESC").
      setSize(24).
      setMaxLength(24).
      setReadOnly().
      setFunction("DOC_CLASS_API.Get_Name(:LOC_DOC_CLASS)").
      setLabel("DOCMAWEDMBASICLOCCLASSDESC: Document Class Description");
      itemblk3.addField("LOC_TYPE").
      setDbName("LOCATION_TYPE").
      setSize(24).
      setMaxLength(200).
      setSelectBox().
      setInsertable().
      setReadOnly().
      setMandatory().
      enumerateValues("EDM_LOCATION_TYPE_API").
      setLabel("DOCMAWEDMBASICLOCTYPE: Repository Type");
      itemblk3.addField("LOC_ADDRESS").
      setDbName("LOCATION_ADDRESS").
      setDynamicLOV("EDM_LOCATION_USER").
      setSize(24).
      setMaxLength(250).
      setInsertable().
      setReadOnly().
      setLabel("DOCMAWEDMBASICLOCADDRESS: Repository Address");
      itemblk3.addField("LOC_PATH").
      setDbName("PATH").
      setSize(24).
      setMaxLength(254).
      setInsertable().
      setReadOnly().
      setLabel("DOCMAWEDMBASICLOCPATH: Path");
      itemblk3.addField("LOC_PORT").
      setDbName("LOCATION_PORT").
      setSize(10).
      setMaxLength(10).
      setInsertable().
      setLabel("DOCMAWEDMBASICLOCPORT: Repository Port");
      itemblk3.addField("LOC_CURRENT_LOC").
      setDbName("CURRENT_LOCATION").
      setSize(24).
      setMaxLength(200).
      setSelectBox().
      setMandatory().
      setInsertable().
      enumerateValues("EDM_CURRENT_LOCATION_API").
      setLabel("DOCMAWEDMBASICLOCCURRENTLOC: Status");
      itemblk3.addField("LOC_REPOSI_DESC").
      setDbName("DESCRIPTION").
      setSize(24).
      setMaxLength(250).
      setInsertable().
      setLabel("DOCMAWEDMBASICLOCREPOSIDESC: Repository Description");
      itemblk3.setView("EDM_LOCATION");
      itemblk3.defineCommand("EDM_LOCATION_API","New__,Modify__,Remove__");
      itemset3 = itemblk3.getASPRowSet();
      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("DOCMAWEDMBASICEDMLOCATIONTITLE: Repositories"));
      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDialogColumns(2);
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
      /////////////////////////// ITEMBLK4 ////////////////////////////////////
      // Block for: Application
      itemblk4 = mgr.newASPBlock("ITEM4");
      itemblk4.addField("ITEM4_OBJID").
      setDbName("OBJID").
      setHidden();
      itemblk4.addField("ITEM4_OBJVERSION").
      setDbName("OBJVERSION").
      setMandatory().
      setHidden();
      itemblk4.addField("APP_FILE_TYPE").
      setDbName("FILE_TYPE").
      setSize(25).
      setMaxLength(30).
      setMandatory().
      setReadOnly().
      setInsertable().
      setUpperCase().
      setLabel("DOCMAWEDMBASICAPPFILETYPE: Application");
      itemblk4.addField("APP_FILE_EXTENTION").
      setDbName("FILE_EXTENTION").
      setSize(35).
      setMaxLength(254).
      setMandatory(). //bug id 75038 
      setInsertable().
      setUpperCase().
      setLabel("DOCMAWEDMBASICAPPFILEEXTENSION: File Type");
      itemblk4.addField("APP_DESCRIPTION").
      setDbName("DESCRIPTION").
      setSize(35).
      setMaxLength(250).
      setMandatory(). //bug id 75038 
      setInsertable().
      setLabel("DOCMAWEDMBASICAPPDESC: Description");
      itemblk4.addField("APP_DOCUMENT_TYPE").
      setDbName("DOCUMENT_TYPE").
      setSize(12).
      setMaxLength(200).
      setMandatory(). //bug id 75038
      setUpperCase().
      setInsertable().
      setDynamicLOV("DOCUMENT_TYPE") .
      setLabel("DOCMAWEDMBASICAPPDOCUMENTTYPE: Document Type");
      itemblk4.setView("EDM_APPLICATION");
      itemblk4.defineCommand("EDM_APPLICATION_API","New__,Modify__,Remove__");
      itemset4 = itemblk4.getASPRowSet();
      itembar4 = mgr.newASPCommandBar(itemblk4);
      itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
      itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
      itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setTitle(mgr.translate("DOCMAWEDMBASICEDMAPPLICATIONTITLE: Application"));
      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDialogColumns(1);
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
      /////////////////////////// ITEMBLK5 ////////////////////////////////////
      // Block for: Repository User ID
      itemblk5 = mgr.newASPBlock("ITEM5");

      itemblk5.addField("ITEM5_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk5.addField("ITEM5_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk5.addField("INFO").
      setSize(0).
      setLabel("DOCMAWEDMBASICINFO:  ").
      setReadOnly().
      setHidden().
      setFunction("''").
      setDefaultNotVisible();

      itemblk5.addField("ATTR").
      setSize(0).
      setLabel("DOCMAWEDMBASICATTR:  ").
      setReadOnly().
      setHidden().
      setFunction("''").
      setDefaultNotVisible();

      itemblk5.addField("LOCATION_ADDRESS").            
      setInsertable().
      setReadOnly().
      setSize(30).
      setMandatory().
      setMaxLength(250).            
      setLabel("DOCMAWEDMBASICUSERLOCADD: Repository Address");
      
      itemblk5.addField("LOCATION_USER").           
      setInsertable().
      setSize(30).
      setMaxLength(30).
      setMandatory().
      setLabel("DOCMAWEDMBASICUSERLOCUSER: Repository User ID");

      itemblk5.addField("LOCATION_PASSWORD").            
      setPasswordField().
      setInsertable().
      setSize(30).            
      setMandatory().
      setHidden().
      setLabel("DOCMAWEDMBASICUSERLOCPW: Repository Password");

      itemblk5.addField( "DUMMY" ).
      setHidden().
      setFunction("''");
      itemblk5.addField("USER_DESCRIPTION").
      setDbName("DESCRIPTION").
      setSize(30).
      setMaxLength(250).
      setInsertable().
      setLabel("DOCMAWEDMBASICUSERDESC: Repository Address Description");

      itemblk5.setView("EDM_LOCATION_USER");
      itemblk5.defineCommand("EDM_LOCATION_USER_API","New__,Modify__,Remove__");

      itemset5 = itemblk5.getASPRowSet();
      itembar5 = mgr.newASPCommandBar(itemblk5);
      itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");
      itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
      itembar5.defineCommand(itembar5.NEWROW,"newRowITEM5");
      itembar5.defineCommand(itembar5.SAVERETURN,"saveReturnITEM5");
      itembar5.defineCommand(itembar5.SAVENEW,"saveNewITEM5");
      itembar5.defineCommand(itembar5.EDITROW,"editRowITEM5"); 
      itembar5.defineCommand(itembar5.DUPLICATEROW,"duplicateITEM5"); 


      itemtbl5 = mgr.newASPTable(itemblk5);
      itemtbl5.setTitle(mgr.translate("DOCMAWEDMBASICEDMUSERTITLE: Repository User ID"));
      itemlay5 = itemblk5.getASPBlockLayout();
      itemlay5.setDialogColumns(1);
      itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);


      /////////////////////////// ITEMBLK6 ////////////////////////////////////
      // Block for: Document Type
      itemblk6 = mgr.newASPBlock("ITEM6");
      itemblk6.addField("ITEM6_OBJID").
      setDbName("OBJID").
      setHidden();
      itemblk6.addField("ITEM6_OBJVERSION").
      setDbName("OBJVERSION").
      setMandatory().
      setHidden();
      itemblk6.addField("ITEM6_DOCUMENT_TYPE").
      setDbName("DOCUMENT_TYPE").
      setSize(12).
      setMaxLength(12).
      setMandatory().
      setReadOnly().
      setInsertable().
      setUpperCase().
      setLabel("DOCMAWEDMBASICDOCUMENTTYPE: Document Type");
      itemblk6.addField("ITEM6_DESCRIPTION").
      setDbName("DESCRIPTION").
      setSize(75).
      setMaxLength(250).
      setInsertable().
      setLabel("DOCMAWEDMBASICDOCUMENTDESCRIPTION: Description");


      itemblk6.setView("DOCUMENT_TYPE");
      itemblk6.defineCommand("DOCUMENT_TYPE_API","New__,Modify__,Remove__");
      itemset6 = itemblk6.getASPRowSet();
      itembar6 = mgr.newASPCommandBar(itemblk6);
      itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
      itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
      itembar6.defineCommand(itembar6.NEWROW,"newRowITEM6");
      itemtbl6 = mgr.newASPTable(itemblk6);
      itemtbl6.setTitle(mgr.translate("DOCMAWEDMBASICDOCUMENTTYPES: Document Types"));
      itemlay6 = itemblk6.getASPBlockLayout();
      itemlay6.setDialogColumns(1);
      itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);


      ////////////////////////////// TAB DEFINITION //////////////////////////////////////////////
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("DOCMAWEDMBASICDOCFILETEMPL: Document File Template"), "javascript:commandSet('HEAD.activateDocFileTemplate','')");
      tabs.addTab(mgr.translate("DOCMAWEDMBASICREPOSITORY: Repositories"),"javascript:commandSet('HEAD.activateRepository','')");
      tabs.addTab(mgr.translate("DOCMAWEDMBASICAPPLICATION: Application"),"javascript:commandSet('HEAD.activateApplication','')");
      tabs.addTab(mgr.translate("DOCMAWEDMBASICREPOSIADDRESSANDUSER: Repository Address and User"),"javascript:commandSet('HEAD.activateRepositoryAddressAndUser','')");
      tabs.addTab(mgr.translate("DOCMAWEDMBASICDOCUMENTTYPES: Document Types"),"javascript:commandSet('HEAD.activateDocumentType','')");
   }


   

   public String  tabsFinish()
   {
      ASPManager mgr = getASPManager();

      return tabs.showTabsFinish();
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      
//      if (tabs.getActiveTab()==1)
//         if (itemset1.countRows() == 0)
//         {
//            itemlay1.setLayoutMode(itemlay1.FIND_LAYOUT);
//         }
        
      if (itemlay5.isNewLayout() || itemlay5.isEditLayout())
         mgr.getASPField("LOCATION_PASSWORD").unsetHidden();
      
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWEDMBASICTITLEEDMBASIC: Basic Data - EDM Basic";
   }

   protected String getTitle()
   {
      return "DOCMAWEDMBASICTITLEBASICDATA: Basic Data - EDM Basic";
   }

   protected AutoString getContents() throws FndException
   { 
      String desc = "";
      String pswd = ""; 
      String loc = "";
      String strObj = "";

      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWEDMBASICTITLEBASICDATA: Basic Data - EDM Basic"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("DOCMAWEDMBASICTITLEEDMBASIC: Basic Data - EDM Basic"));
      
      out.append(tabs.showTabsInit());


      if (tabs.getActiveTab()==1)
      {
         if (itemlay1.isVisible())
         {
            out.append(itemlay1.show());
         }
         if (itemset1.countRows()>0 && itemlay1.isSingleLayout())
            out.append(itemlay2.show());


      }
      else if (tabs.getActiveTab()==2)
      {
         out.append(itemlay3.show());


      }
      else if (tabs.getActiveTab()==3)
      {
         out.append(itemlay4.show());

      }
      else if (tabs.getActiveTab()==5)
      {
         out.append(itemlay6.show());



      }
      else if (tabs.getActiveTab()==4)
      {
         out.append(itemlay5.show());
         
      }
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
