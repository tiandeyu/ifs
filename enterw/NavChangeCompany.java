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
*  File        : NavChangeCompany.java 
*  Created     : Nilan Mallawarachchi
*  Date        : 2001-04-30
*  Modified    : 2001-05-25 # 65350 set the Global ledger id for the selected company
*  Modified    : 2001-08-28 Enmalk added find functionality.
*  2004-12-09  Prdilk LCS Patch Merge - Bug 46750
*  2005-08-16  Gadalk Added code part A  
*  2005-09-07  Jakalk Code Cleanup, Removed doReset and clone methods.
*  2008-02-06  Jakalk Bug 70332, Added mgr.URLEncode to missing places.
*  2008-05-24  Kanslk Bug 72353, Added translation constants for 'Current value of Global Company'
*  2010-04-07  Maaylk Bug 89908, Instead of checking security on entire package checked it for each method
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class NavChangeCompany extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.demorw.DefineCompany");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;
   private ASPHTMLFormatter fmt;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar cmdbar;
   private ASPTable tbl;
   private ASPBlockLayout lay;

   private ASPBlock ref;
   private ASPBlock idblk;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private String company;
   private ASPQuery q;
   private ASPQuery qry;
   private ASPBuffer sec;
   private ASPCommand cmd;
   private String code;
   private String value0;
   private String value1;
   private String value2;
   private String value3;
   private String value4;
   private String value5;
   private String value6;
   private String value7;
   private String value8;
   private String value9;
   private String name0;
   private String name1;
   private String name2;
   private String name3;
   private String name4;
   private String name5;
   private String name6;
   private String name7;
   private String name8;
   private String name9;

   private String currency;  
   private String currency_code;  
   private String pracc; 
   private String faacc; 
   private String chkvalue;
   private boolean bChangeLed= false;
   private String ledgerid; //# 65350

   //===============================================================
   // Construction 
   //===============================================================

   public NavChangeCompany(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx   = getASPContext();
      fmt   = getASPHTMLFormatter();
      company = mgr.readValue("NEWCOMPANY");

      chkvalue = ctx.readValue("CHKVALUE",mgr.getQueryStringValue("FROMPAGEINFINANCE"));
      ctx.writeValue("CHKVALUE",chkvalue);

      if( mgr.commandBarActivated() )
      {
         eval(mgr.commandBarFunction());
      }
      else if( !mgr.isEmpty(company) )
      {
         ctx.setGlobal("COMPANY",company);
         submit();

         if( mgr.isEmpty(chkvalue) )
            mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"Navigator.page?MAINMENU=Y&NEW=Y");
         else
            mgr.redirectFrom();
      }
      else if( mgr.isEmpty(ctx.findGlobal("COMPANY", "")) )
      {
         trans.clear();
         cmd = trans.addCustomFunction("GLOBAL","User_Profile_SYS.Get_Default('COMPANY',Fnd_Session_API.Get_Fnd_User)", "COMPANY");   
         trans = mgr.perform(trans);     
         company = trans.getValue("GLOBAL/DATA/COMPANY");  
         trans.clear();
      }
      else
      {
         company = ctx.findGlobal("COMPANY","");
         if( mgr.isEmpty(company) )
            mgr.showAlert("ENTERWNAVCHANGECOMPANYNOCOMPANY: The current company is undefined. Please select a value from the table.");
      }

      if( !mgr.commandBarActivated() )
      {
         okFind();
      }
      
      adjust();
   }  

   public void  submit()
   {
      ASPManager mgr = getASPManager();

      trans.clear();   

      //Bug 89908, Begin. Change addSecurityQuery() to check security on methods rather than on packages.
      //Checking if packages available
      trans.addSecurityQuery("ACCOUNTING_CODE_PARTS_API", "IS_CODE_USED,GET_NAME,GET_CODEPART_FUNCTION");   
      trans.addSecurityQuery("COMPANY_FINANCE_API", "GET_PARALLEL_ACC_CURRENCY,GET_CURRENCY_CODE");
      trans.addSecurityQuery("INTERNAL_LEDGER_USER_API", "GET_DEFAULT_LEDGER");   
      //Bug 89908, End.
      trans = mgr.perform(trans);
      sec = trans.getSecurityInfo(); 

      //Bug 89908, Begin. Change itemExists() to check security on methods rather than on entire packages.
      if ((sec.itemExists("ACCOUNTING_CODE_PARTS_API.IS_CODE_USED")) && (sec.itemExists("ACCOUNTING_CODE_PARTS_API.GET_NAME")) &&
          (sec.itemExists("ACCOUNTING_CODE_PARTS_API.GET_CODEPART_FUNCTION")) &&
          (sec.itemExists("COMPANY_FINANCE_API.GET_PARALLEL_ACC_CURRENCY")) && (sec.itemExists("COMPANY_FINANCE_API.GET_CURRENCY_CODE")))
      {
         trans.clear();

         code = "A";
         cmd = trans.addCustomFunction("MAINA","ACCOUNTING_CODE_PARTS_API.Is_Code_Used","VALUE0");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN0","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME0");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         code = "B";
         cmd = trans.addCustomFunction("MAINB","ACCOUNTING_CODE_PARTS_API.Is_Code_Used","VALUE1");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN1","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME1");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         code = "C";
         cmd = trans.addCustomFunction("MAINC","Accounting_Code_Parts_API.Is_Code_Used","VALUE2");     
         cmd.addParameter("COMPANY",  company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN2","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME2");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         code = "D";
         cmd = trans.addCustomFunction("MAIND","Accounting_Code_Parts_API.Is_Code_Used","VALUE3");     
         cmd.addParameter("COMPANY",  company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN3","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME3");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         code = "E";
         cmd = trans.addCustomFunction("MAINE","Accounting_Code_Parts_API.Is_Code_Used","VALUE4");     
         cmd.addParameter("COMPANY",  company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN4","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME4");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         code = "F";
         cmd = trans.addCustomFunction("MAINF","Accounting_Code_Parts_API.Is_Code_Used","VALUE5");     
         cmd.addParameter("COMPANY",  company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN5","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME5");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         code = "G";
         cmd = trans.addCustomFunction("MAING","Accounting_Code_Parts_API.Is_Code_Used","VALUE6");     
         cmd.addParameter("COMPANY",  company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN6","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME6");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         code = "H";
         cmd = trans.addCustomFunction("MAINH","Accounting_Code_Parts_API.Is_Code_Used","VALUE7");     
         cmd.addParameter("COMPANY",  company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN7","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME7");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         code = "I";
         cmd = trans.addCustomFunction("MAINI","Accounting_Code_Parts_API.Is_Code_Used","VALUE8");     
         cmd.addParameter("COMPANY",  company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN8","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME8");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         code = "J";
         cmd = trans.addCustomFunction("MAINJ","Accounting_Code_Parts_API.Is_Code_Used","VALUE9");     
         cmd.addParameter("COMPANY",  company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN9","ACCOUNTING_CODE_PARTS_API.Get_Name","NAME9");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CODE",code);

         cmd = trans.addCustomFunction("MAIN10","COMPANY_FINANCE_API.GET_PARALLEL_ACC_CURRENCY","CURRENCY");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("CURRENT_DATE", mgr.getASPField("CURRENT_DATE").getValue());

         cmd = trans.addCustomFunction("MAIN11","Company_Finance_API.Get_Currency_Code","CURRENCY_CODE");      
         cmd.addParameter("COMPANY",   company);

         cmd = trans.addCustomFunction("MAIN12","ACCOUNTING_CODE_PARTS_API.GET_CODEPART_FUNCTION","PRACC");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("FUNCTION","PRACC");

         cmd = trans.addCustomFunction("MAIN13","ACCOUNTING_CODE_PARTS_API.GET_CODEPART_FUNCTION","FAACC");     
         cmd.addParameter("COMPANY",   company);
         cmd.addParameter("FUNCTION", "FAACC"); 

         //Bug 89908, Begin. Checking security on methodrather than on the package
         if( sec.itemExists("INTERNAL_LEDGER_USER_API.GET_DEFAULT_LEDGER") )
            cmd = trans.addCustomFunction("MAIN14","Internal_Ledger_User_API.Get_Default_Ledger('"+company+"',Fnd_Session_API.Get_Fnd_User)", "LEDGER_ID");
         //Bug 89908, End.
         trans = mgr.perform(trans); 

         value0=trans.getValue("MAINA/DATA/VALUE0");
         value1=trans.getValue("MAINB/DATA/VALUE1");
         value2=trans.getValue("MAINC/DATA/VALUE2");
         value3=trans.getValue("MAIND/DATA/VALUE3");
         value4=trans.getValue("MAINE/DATA/VALUE4");
         value5=trans.getValue("MAINF/DATA/VALUE5");
         value6=trans.getValue("MAING/DATA/VALUE6");
         value7=trans.getValue("MAINH/DATA/VALUE7");
         value8=trans.getValue("MAINI/DATA/VALUE8");
         value9=trans.getValue("MAINJ/DATA/VALUE9");   

         name0=trans.getValue("MAIN0/DATA/NAME0");
         name1=trans.getValue("MAIN1/DATA/NAME1");       
         name2=trans.getValue("MAIN2/DATA/NAME2");       
         name3=trans.getValue("MAIN3/DATA/NAME3");       
         name4=trans.getValue("MAIN4/DATA/NAME4");       
         name5=trans.getValue("MAIN5/DATA/NAME5");       
         name6=trans.getValue("MAIN6/DATA/NAME6");       
         name7=trans.getValue("MAIN7/DATA/NAME7");       
         name8=trans.getValue("MAIN8/DATA/NAME8");       
         name9=trans.getValue("MAIN9/DATA/NAME9");

         currency=trans.getValue("MAIN10/DATA/CURRENCY");     
         currency_code=trans.getValue("MAIN11/DATA/CURRENCY_CODE");     

         pracc=trans.getValue("MAIN12/DATA/PRACC");     
         faacc=trans.getValue("MAIN13/DATA/FAACC");

         ledgerid = trans.getValue("MAIN14/DATA/LEDGER_ID");

         if( !mgr.isEmpty(value0) )
            ctx.setGlobal("VALUEA", value0 );
         else
            ctx.setGlobal("VALUEA", "" );
         
         if( !mgr.isEmpty(value1) )
            ctx.setGlobal("VALUEB", value1 );
         else
            ctx.setGlobal("VALUEB", "" ); 

         if( !mgr.isEmpty(value2) )
            ctx.setGlobal("VALUEC", value2 );
         else
            ctx.setGlobal("VALUEC", "" );

         if( !mgr.isEmpty(value3) )
            ctx.setGlobal("VALUED", value3 );
         else
            ctx.setGlobal("VALUED", "" );

         if( !mgr.isEmpty(value4) )
            ctx.setGlobal("VALUEE", value4 );
         else
            ctx.setGlobal("VALUEE", "" );

         if( !mgr.isEmpty(value5) )
            ctx.setGlobal("VALUEF", value5 );
         else
            ctx.setGlobal("VALUEF", "" );

         if( !mgr.isEmpty(value6) )
            ctx.setGlobal("VALUEG", value6 );
         else
            ctx.setGlobal("VALUEG", "" );

         if( !mgr.isEmpty(value7) )
            ctx.setGlobal("VALUEH", value7 );
         else
            ctx.setGlobal("VALUEH", "" );

         if( !mgr.isEmpty(value8) )
            ctx.setGlobal("VALUEI", value8 );
         else
            ctx.setGlobal("VALUEI", "" );

         if( !mgr.isEmpty(value9) )
            ctx.setGlobal("VALUEJ", value9 );
         else
            ctx.setGlobal("VALUEJ", "" );

         if( !mgr.isEmpty(name0) )
            ctx.setGlobal("NAMEA", name0);
         else
            ctx.setGlobal("NAMEA", "");
         
         if( !mgr.isEmpty(name1) )
            ctx.setGlobal("NAMEB", name1);
         else
            ctx.setGlobal("NAMEB", "");

         if( !mgr.isEmpty(name2) )
            ctx.setGlobal("NAMEC", name2);
         else
            ctx.setGlobal("NAMEC", "");

         if( !mgr.isEmpty(name3) )
            ctx.setGlobal("NAMED", name3);
         else
            ctx.setGlobal("NAMED", "");

         if( !mgr.isEmpty(name4) )
            ctx.setGlobal("NAMEE", name4);
         else
            ctx.setGlobal("NAMEE", "");

         if( !mgr.isEmpty(name5) )
            ctx.setGlobal("NAMEF", name5);
         else
            ctx.setGlobal("NAMEF", "");

         if( !mgr.isEmpty(name6) )
            ctx.setGlobal("NAMEG", name6);
         else
            ctx.setGlobal("NAMEG", ""); 

         if( !mgr.isEmpty(name7) )
            ctx.setGlobal("NAMEH", name7);
         else
            ctx.setGlobal("NAMEH", "");

         if( !mgr.isEmpty(name8) )
            ctx.setGlobal("NAMEI", name8);
         else
            ctx.setGlobal("NAMEI", "");

         if( !mgr.isEmpty(name9) )
            ctx.setGlobal("NAMEJ", name9);
         else
            ctx.setGlobal("NAMEJ", "");

         if( !mgr.isEmpty(currency) )
            ctx.setGlobal("THIRDCURRENCY", currency);
         else
            ctx.setGlobal("THIRDCURRENCY", "");

         if( !mgr.isEmpty(currency_code) )
            ctx.setGlobal("THIRDCURRENCY_CODE", currency_code);
         else
            ctx.setGlobal("THIRDCURRENCY_CODE", "");

         if( !mgr.isEmpty(pracc) )
            ctx.setGlobal("PRACC", pracc);
         else
            ctx.setGlobal("PRACC", "");

         if( !mgr.isEmpty(faacc) )
            ctx.setGlobal("FAACC", faacc);
         else
            ctx.setGlobal("FAACC", "");
         
         if( !mgr.isEmpty(ledgerid) )
         {
            ctx.setGlobal("LEDGER_ID", ledgerid );
            bChangeLed = true;
         }
         else
            ctx.setGlobal("LEDGER_ID", " " );
      }
      //Bug 89908, End.
      ctx.setGlobal("COMPANY", company );
   } 

   public void  countFind()
   {
      String n;
      ASPManager mgr = getASPManager();
      q = trans.addQuery(blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      n = rowset.getRow().getValue("N");
      lay.setCountValue(toInt(n));
      rowset.clear();
   }
   
   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(blk);
      q.setOrderByClause("COMPANY");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,blk);

      if( rowset.countRows() == 0 )
      {
         mgr.showAlert("ENTERWNAVCHANGECOMPANYNODATA: No data found.");
         rowset.clear();
      }
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      ref = mgr.newASPBlock("REF");
      ref.addField( "CURRENT_DATE","Date").
      setFunction("SYSDATE");
      ref.setView("DUAL");  

      blk = mgr.newASPBlock("MAIN");

      blk.addField("OBJID").
         setHidden();

      blk.addField("OBJVERSION").
         setHidden();

      blk.addField("COMPANY").
         setLabel("ENTERWNAVCHANGECOMPANYID: Company").
         setSize(25);

      blk.addField("DESCRIPTION").
         setLabel("ENTERWNAVCHANGECOMPANYNAME: Name").
         setSize(25);

      blk.setView("COMPANY_FINANCE");
      blk.setTitle("Press the arrow icon to choose the Global Company");

      rowset = blk.getASPRowSet();

      cmdbar = mgr.newASPCommandBar(blk);
      cmdbar.setCounterDbMode(); 

      tbl = mgr.newASPTable(blk); 
      tbl.disableEditProperties();
      tbl.disableOutputChannels();

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);

      idblk = mgr.newASPBlock("IDD");

      idblk.addField("CODE");
      idblk.addField("FUNCTION");
      idblk.addField("VALUE0");
      idblk.addField("VALUE1");
      idblk.addField("VALUE2");
      idblk.addField("VALUE3");
      idblk.addField("VALUE4");
      idblk.addField("VALUE5");
      idblk.addField("VALUE6");
      idblk.addField("VALUE7");
      idblk.addField("VALUE8");
      idblk.addField("VALUE9");   

      idblk.addField("NAME0");
      idblk.addField("NAME1");   
      idblk.addField("NAME2");   
      idblk.addField("NAME3");   
      idblk.addField("NAME4");   
      idblk.addField("NAME5");   
      idblk.addField("NAME6");   
      idblk.addField("NAME7");   
      idblk.addField("NAME8");   
      idblk.addField("NAME9");

      idblk.addField("CURRENCY"); 
      idblk.addField("CURRENCY_CODE"); 
      idblk.addField("SYSDATE");   

      idblk.addField("PRACC"); 
      idblk.addField("FAACC");

      idblk.addField("LEDGER_ID");
   }


   public void  adjust()
   {
      String title;
      ASPManager mgr = getASPManager();

      if( !mgr.isEmpty(company) )
      {
         // Bug 72353, Begin
         title = mgr.translate("ENTERWNAVCHANGECOMPANYCURRENTGLOBALCOMPANY: Current value of Global Company: ") + company;
         // Bug 72353, End
         blk.setTitle(title);
      }
   }


//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "ENTERWNAVCHANGECOMPANYTITLE: Define Company";
   }


   protected String getTitle()
   {
      return "ENTERWNAVCHANGECOMPANYTITLE: Define Company";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      String pathandDomain = "";
      pathandDomain = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      appendToHTML(fmt.drawHidden("NEWCOMPANY",""));
      
      if( lay.isMultirowLayout () )
      {
         appendToHTML(cmdbar.showBar());
         appendToHTML(tbl.populateLov());
      }
      else
      {
         appendToHTML(lay.show()); 
      }
      
      appendDirtyJavaScript("function setValue(selected_value)");
      appendDirtyJavaScript("{");
      appendDirtyJavaScript("   f.NEWCOMPANY.value = selected_value;");
      // Bug 70332, Begin
      appendDirtyJavaScript("window.location.href='"+pathandDomain+"enterw/DefineCompany.page?NEWSELCOMPANY='+URLClientEncode(selected_value);");
      // Bug 70332, End
      appendDirtyJavaScript("}"); 
   }
}
