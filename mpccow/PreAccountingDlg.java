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
 *  File        : PreAccountingDlg.java 
 *  Description : Pre Posting Header Dialog 
 *  Notes       : 
 * ----------------------------------------------------------------------------
 *  Modified    :
 *     ErFelk   2010-Aug-12 - Bug 90261, Modified Where clause of all code parts in setlov() to remove the expired code parts been displayed.
 *     ChJalk   2010-Jun-22 - Bug 90400, Modified the data type of PRE_ACCOUNTING_ID to String from Number.
 *     KaDilk   2009-Dec-07 - Bug 72392,Added column titles to code parts in setlov().
 *     RaKalk   2007-Jun-12 - Modified the data type of ACTIVITY_SEQ field to Number and set an integer mask 
 *     NaLrlk   2006-Aug-24 - Bug 58216 Correcting SQL errors.
 *     JaJalk   2005-Nov-22 - Removed command bar buttons as the page is using custom buttons.
 *     KaDilk   2005-Mar-10 - Bug 48389,Changed lov references which has used for code parts in PreAccountingDlg.
 *     ThGulk   2004-Dec-03 - Bug 47767, implemented work around for IE related problem. added a dummy client submit before actual form submit. Modified Run() and getContents()
 *     JaJalk   2003-Dec-30 - Performed some code clean up.
 *     Usheera  2001-Oct-22 - Added ctx.removeContextFromCache to the run()
 *     Suneth   2001-Jan-29 - Created.
 * ----------------------------------------------------------------------------
 */

package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PreAccountingDlg extends ASPPageProvider
{
   /* Static constants */

   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.PreAccountingDlg");

   /* Instances created on page creation (immutable attributes) */

   private ASPForm frm;
   private ASPContext ctx;
   private ASPField f;
   private ASPBlock nameblk;
   private ASPRowSet nameset;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar cmdbar;
   private ASPTable tbl;
   private ASPBlockLayout lay;
   private ASPBlock ref;

   /* Transient temporary variables (never cloned) */

   private ASPTransactionBuffer trans;
   private String scompany;
   private ASPBuffer buf;
   private String calling_url;
   private ASPBuffer dataBuffer;
   private String pre_accounting_id;
   private String scontract;
   private String id_exist;
   private String enabl0;
   private String enabl1;
   private String enabl2;
   private String enabl3;
   private String enabl4;
   private String enabl5;
   private String enabl6;
   private String enabl7;
   private String enabl8;
   private String enabl9;
   private String enabl10;
   private String one__;
   private String two__;
   private String three__;
   private String four__;
   private String five__;
   private String six__;
   private String seven__;
   private String eight__;
   private String nine__;
   private String ten__;
   private boolean first_request;
   /* Construction */

   public PreAccountingDlg(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
      
      frm   = mgr.getASPForm();
      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      
      scompany = ctx.readValue("SCOMPANY", " ");
      pre_accounting_id  = ctx.readValue("PREACC", " ");
      buf = ctx.readBuffer("BUF");
      calling_url = ctx.getGlobal("CALLING_URL");
      
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.buttonPressed("CANCEL"))
         cancel();
      else if (mgr.buttonPressed("OK"))
         ok();
      else if (mgr.dataTransfered())
      {
         buf = mgr.getTransferedData();
         getPreAccountingIdExist();
         nameSearch();
         
         if ("1".equals(id_exist)) 
         {
            okFind();
            setlov();
         }
         else
         {
            launchNew();
            setlov();
         }
      first_request = true;
      }
      else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) || !mgr.isExplorer()) 
	 setlov();

      ctx.writeValue("SCOMPANY",scompany);
      ctx.writeValue("PREACC",pre_accounting_id);
      ctx.writeBuffer("BUF",buf);

   }

   public void  getPreAccountingIdExist()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q;
      dataBuffer = buf.getBuffer("dataBuffer");
      pre_accounting_id = dataBuffer.getValue("PRE_ACCOUNTING_ID");
      scontract = dataBuffer.getValue("CONTRACT");
      // Bug ID 58216, Applied the SQL Injection security patch, added .addparameter and modified where condition
      q = trans.addQuery("MAIN3","SELECT PRE_ACCOUNTING_API.Check_Exist(?) ID_EXIST FROM DUAL");
      q.addParameter("PRE_ACCOUNTING_ID",pre_accounting_id);

      q = trans.addQuery("REF1","SELECT Site_API.Get_Company(?) COMPANY FROM DUAL");
      q.addParameter("CONTRACT1",scontract);

      trans = mgr.perform(trans);
      
      id_exist = trans.getValue("MAIN3/DATA/ID_EXIST");
      scompany = trans.getValue("REF1/DATA/COMPANY");
   }

   public void  launchNew()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;
         
      trans.clear();
      
      cmd = trans.addEmptyCommand("MAIN","PRE_ACCOUNTING_API.New__",blk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      
      ASPBuffer data = trans.getBuffer("MAIN/DATA");
      data.setFieldItem("PRE_ACCOUNTING_ID",pre_accounting_id);
      rowset.addRow(data);
   }

   public void  cancel()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer return_buffer = buf.getBuffer("return_buffer");
      mgr.transferDataTo(calling_url,return_buffer);
   }

   public void  setlov()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      getNames();
      
      dataBuffer = buf.getBuffer("dataBuffer");
      
      enabl0 = dataBuffer.getValue("ENABL0");
      enabl1 = dataBuffer.getValue("ENABL1");
      enabl2 = dataBuffer.getValue("ENABL2");
      enabl3 = dataBuffer.getValue("ENABL3");
      enabl4 = dataBuffer.getValue("ENABL4");
      enabl5 = dataBuffer.getValue("ENABL5");
      enabl6 = dataBuffer.getValue("ENABL6");
      enabl7 = dataBuffer.getValue("ENABL7");
      enabl8 = dataBuffer.getValue("ENABL8");
      enabl9 = dataBuffer.getValue("ENABL9");
      enabl10 = dataBuffer.getValue("ENABL10");
      // Bug 90261, start
      scontract = dataBuffer.getValue("CONTRACT");
      // Bug 90261, end
      
      f = mgr.getASPField("ACCOUNT_NO");
      f.setLabel(one__);    

      if ("0".equals(enabl0))  

         f.setReadOnly();
      else
      {
         f.setDynamicLOV("PRE_ACCOUNT_CODEPART_A_MPCCOM", 600,450);
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"'");
         f.setLOVProperty("TITLE",one__);
      }
      f = mgr.getASPField("COST_CENTER");
      f.setLabel(two__);   

      if ("0".equals(enabl1))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_B", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",two__);
         f.setLOVProperty("COLUMN_TITLES", "CODE_B=" + two__ );
      }
      f = mgr.getASPField("CODENO_C");
      f.setLabel(three__);   

      if ("0".equals(enabl2))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_C", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",three__);
         f.setLOVProperty("COLUMN_TITLES", "CODE_C=" + three__ );

      }
      f = mgr.getASPField("CODENO_D");
      f.setLabel(four__);   

      if ("0".equals(enabl3))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_D", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",four__);
         f.setLOVProperty("COLUMN_TITLES", "CODE_D=" + four__ );
      }
      f = mgr.getASPField("OBJECT_NO");
      f.setLabel(five__);   

      if ("0".equals(enabl4))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_E", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",five__);
         f.setLOVProperty("COLUMN_TITLES", "CODE_E=" + five__ );
      }
      f = mgr.getASPField("PROJECT_NO");
      f.setLabel(six__);   

      if ("0".equals(enabl5))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_F", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",six__);
         f.setLOVProperty("COLUMN_TITLES", "CODE_F=" + six__ );
      }
      f = mgr.getASPField("CODENO_G");
      f.setLabel(seven__);   

      if ("0".equals(enabl6))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_G", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",seven__);
         f.setLOVProperty("COLUMN_TITLES", "CODE_G=" + seven__ );
      }
      f = mgr.getASPField("CODENO_H");
      f.setLabel(eight__);   

      if ("0".equals(enabl7))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_H", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",eight__);
         f.setLOVProperty("COLUMN_TITLES", "CODE_H=" + eight__ );
      }
      f = mgr.getASPField("CODENO_I");
      f.setLabel(nine__);   

      if ("0".equals(enabl8))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_I", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",nine__);
         f.setLOVProperty("COLUMN_TITLES", "CODE_I=" + nine__ );
      }
      f = mgr.getASPField("CODENO_J");
      f.setLabel(ten__);   

      if ("0".equals(enabl9))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_J", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+scompany+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",ten__);
         f.setLOVProperty("COLUMN_TITLES", "CODE_J=" + ten__ );
      }
      trans.clear();
      
      trans.addSecurityQuery("ACTIVITY");
      trans = mgr.validate(trans);
      ASPBuffer sec = trans.getSecurityInfo();
      
      if (!(sec.itemExists("ACTIVITY")) || ("0".equals(enabl10))) 
         mgr.getASPField("ACTIVITY_SEQ").setReadOnly();
      else
      {
         String value = rowset.getRow().getValue("PROJECT_NO");
         
         mgr.getASPField("ACTIVITY_SEQ").setDynamicLOV("PROJECT_ACTIVITY","PROJECT_NO PROJECT_ID",600,450);
         mgr.getASPField("ACTIVITY_SEQ").setLOVProperty("TITLE","Activity Seq");

         if ("".equals(value)) 
            mgr.getASPField("ACTIVITY_SEQ").setLOVProperty("WHERE","PROJECT_ID IS NULL");
         else
            mgr.getASPField("ACTIVITY_SEQ").setLOVProperty("PROJECT_ID", rowset.getRow().getValue("PROJECT_NO"));
      }
   }

   public void  getNames()
   {
      nameset.first();
      
      one__= nameset.getValue("CODE_NAME");
      nameset.next();
      
      two__= nameset.getValue("CODE_NAME");
      nameset.next();

      debug("Testing..................." + two__);

      three__= nameset.getRow().getValue("CODE_NAME");
      nameset.next();

      debug("Testing..................." + three__);

      
      four__= nameset.getValue("CODE_NAME");
      nameset.next();
      
      five__= nameset.getValue("CODE_NAME");
      nameset.next();
      
      six__= nameset.getValue("CODE_NAME");
      nameset.next();
      
      seven__= nameset.getValue("CODE_NAME");
      nameset.next();
      
      eight__= nameset.getValue("CODE_NAME");
      nameset.next();
      
      nine__= nameset.getValue("CODE_NAME");
      nameset.next();
      
      ten__= nameset.getValue("CODE_NAME");
      nameset.next();
   }

   public void  nameSearch()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q;
      
      trans.clear();
      q = trans.addQuery(nameblk);
      q.addWhereCondition("COMPANY  = ? ");
      q.addParameter("COMPANY", scompany);
      q.setOrderByClause("CODE_PART");                  
      q.includeMeta("ALL");
      mgr.submit(trans);
      
      trans.clear();
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q;
   
      trans.clear();
      q = trans.addQuery(blk);
      q.includeMeta("ALL");
      q.addWhereCondition("PRE_ACCOUNTING_ID  = ? ");
      q.addParameter("PRE_ACCOUNTING_ID", pre_accounting_id);

      mgr.submit(trans);
   }

   /* Button functions */

   public void  ok()
   {
      rowset.changeRow();
      validateCodePart();
      trans.clear();
      submit();
      cancel();
   }

   public void  submit()
   {
      ASPManager mgr = getASPManager();

      dataBuffer = buf.getBuffer("dataBuffer");
      scontract = dataBuffer.getValue("CONTRACT");
      
      ASPBuffer r = rowset.getRow();
      r.setFieldItem("COMPANY1",scompany);
      r.setFieldItem("CONTRACT1",scontract);
      rowset.setRow(r);
      
      rowset.setEdited("ACCOUNT_NO");
      rowset.setEdited("COST_CENTER");
      rowset.setEdited("CODENO_C");
      rowset.setEdited("CODENO_D");
      rowset.setEdited("OBJECT_NO");
      rowset.setEdited("PROJECT_NO");
      rowset.setEdited("CODENO_G");
      rowset.setEdited("CODENO_H");
      rowset.setEdited("CODENO_I");
      rowset.setEdited("CODENO_J");
      rowset.setEdited("ACTIVITY_SEQ");
      
      mgr.submit(trans);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      
      nameblk = mgr.newASPBlock("NAMES");
      
      f = nameblk.addField("CODE_NAME");
      f.setHidden();
      
      nameblk.setView("ACCOUNTING_CODE_PARTS");
      nameset = nameblk.getASPRowSet();
      
      blk = mgr.newASPBlock("MAIN");
      
      f = blk.addField( "OBJID" );
      f.setHidden();
      
      f = blk.addField( "OBJVERSION" );
      f.setHidden();
      
      f = blk.addField("COMPANY1");
      f.setDbName("COMPANY");
      f.setHidden();   
      
      f = blk.addField("CONTRACT1");
      f.setDbName("CONTRACT");
      f.setHidden();   
      
      // Bug 90400, Removed data type "Number".
      f = blk.addField("PRE_ACCOUNTING_ID");
      f.setHidden();   
      
      f = blk.addField("ACCOUNT_NO","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGACNO: Account");
      f.setSize(12);
      
      f = blk.addField("COST_CENTER","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGCOCN: Cost cent");
      f.setSize(12);
      
      f = blk.addField("CODENO_C","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGCODC: Code C");
      f.setSize(12);
      
      f = blk.addField("CODENO_D","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGCODD: Code D");
      f.setSize(12);
      
      f = blk.addField("OBJECT_NO","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGCODE: Code E");
      f.setSize(12);
      
      f = blk.addField("PROJECT_NO","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGCODF: Code F");
      f.setSize(12);
      
      f = blk.addField("CODENO_G","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGCODG: Code G");
      f.setSize(12);
      
      f = blk.addField("CODENO_H","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGCODH: Code H");
      f.setSize(12);
      
      f = blk.addField("CODENO_I","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGCODI: Code I");
      f.setSize(12);
      
      f = blk.addField("CODENO_J","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGCODJ: Code J");
      f.setSize(12);
      
      f = blk.addField("ACTIVITY_SEQ","Number","###############");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLGACSQ: Activity Seq");
      f.setSize(12);
      
      blk.setView("PRE_ACCOUNTING");
      blk.defineCommand("PRE_ACCOUNTING_API","New__,Modify__,Remove__");
      blk.setTitle("MPCCOWPREACCOUNTINGDLGTEMMSG: Enter value for known code parts");
      
      rowset = blk.getASPRowSet();
      tbl = mgr.newASPTable(blk);
      cmdbar = mgr.newASPCommandBar(blk);
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.EDIT_LAYOUT);
      cmdbar.disableCommand(cmdbar.SAVENEW);
      cmdbar.disableCommand(cmdbar.SAVERETURN);
      cmdbar.disableCommand(cmdbar.CANCELEDIT);
      
      ref = mgr.newASPBlock("REF");
      
      f = ref.addField( "CURRENT_DATE", "Date" );
      f = ref.addField("VOUCHER_DATE");
      f = ref.addField("CODE_PART");
      f = ref.addField("ID_EXIST");
      f = ref.addField("COMPANY");
   }

   /* validation */

   public void  validateCodePart()
   {
      ASPManager mgr = getASPManager();
      
      String check0 = rowset.getValue("ACCOUNT_NO");
      String check1 = rowset.getValue("COST_CENTER");
      String check2 = rowset.getValue("CODENO_C");
      String check3 = rowset.getValue("CODENO_D");
      String check4 = rowset.getValue("OBJECT_NO");
      String check5 = rowset.getValue("PROJECT_NO");
      String check6 = rowset.getValue("CODENO_G");
      String check7 = rowset.getValue("CODENO_H");
      String check8 = rowset.getValue("CODENO_I");
      String check9 = rowset.getValue("CODENO_J");
      
      if (mgr.isEmpty(check0))
         enabl0 = "0" ;
      else
         enabl0 = "1" ;
      
      if (mgr.isEmpty(check1))
         enabl1 = "0" ;
      else
         enabl1 = "1" ;
      
      if (mgr.isEmpty(check2))
         enabl2 = "0" ;
      else
         enabl2 = "1" ;
      
      if (mgr.isEmpty(check3))
         enabl3 = "0" ;
      else
         enabl3 = "1" ;
      
      if (mgr.isEmpty(check4))
         enabl4 = "0" ;
      else
         enabl4 = "1" ;
      
      if (mgr.isEmpty(check5))
         enabl5 = "0" ;
      else
         enabl5 = "1" ;
      
      if (mgr.isEmpty(check6))
         enabl6 = "0" ;
      else
         enabl6 = "1" ;
      
      if (mgr.isEmpty(check7))
         enabl7 = "0" ;
      else
         enabl7 = "1" ;
      
      if (mgr.isEmpty(check8))
         enabl8 = "0" ;
      else
         enabl8 = "1" ;
      
      if (mgr.isEmpty(check9))
         enabl9 = "0" ;
      else
         enabl9 = "1" ;
      
      if ("1".equals(enabl0)) 
      {
         trans.addQuery("REFDATE0","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         
         ASPCommand cmd0;
         cmd0=trans.addCustomCommand("V0","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd0.addParameter("ACCOUNT_NO");
         cmd0.addParameter("CODE_PART","A");
         cmd0.addReference("VOUCHER_DATE", "REFDATE0/DATA", "CURRENT_DATE");
         cmd0.addParameter("COMPANY",scompany);
      }
      if ("1".equals(enabl1)) 
      {
         trans.addQuery("REFDATE1","DUAL","trunc(SYSDATE) CURRENT_DATE","","");

         ASPCommand cmd1;
         cmd1=trans.addCustomCommand("V1","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd1.addParameter("COST_CENTER");
         cmd1.addParameter("CODE_PART","B");
         cmd1.addReference("VOUCHER_DATE", "REFDATE1/DATA", "CURRENT_DATE");
         cmd1.addParameter("COMPANY",scompany);
      }
      if ("1".equals(enabl2)) 
      {
         trans.addQuery("REFDATE2","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         
         ASPCommand cmd2;         
         cmd2=trans.addCustomCommand("V2","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd2.addParameter("CODENO_C");
         cmd2.addParameter("CODE_PART","C");
         cmd2.addReference("VOUCHER_DATE", "REFDATE2/DATA", "CURRENT_DATE");
         cmd2.addParameter("COMPANY",scompany);
      }
      if ("1".equals(enabl3)) 
      {
         trans.addQuery("REFDATE3","DUAL","trunc(SYSDATE) CURRENT_DATE","","");

         ASPCommand cmd3;
         cmd3=trans.addCustomCommand("V3","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd3.addParameter("CODENO_D");
         cmd3.addParameter("CODE_PART","D");
         cmd3.addReference("VOUCHER_DATE", "REFDATE3/DATA", "CURRENT_DATE");
         cmd3.addParameter("COMPANY",scompany);
      }
      if ("1".equals(enabl4)) 
      {
         trans.addQuery("REFDATE4","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         
         ASPCommand cmd4;
         cmd4=trans.addCustomCommand("V4","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd4.addParameter("OBJECT_NO");
         cmd4.addParameter("CODE_PART","E");
         cmd4.addReference("VOUCHER_DATE", "REFDATE4/DATA", "CURRENT_DATE");
         cmd4.addParameter("COMPANY",scompany);
      }
      if ("1".equals(enabl5)) 
      {
         trans.addQuery("REFDATE5","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         
         ASPCommand cmd5;
         cmd5=trans.addCustomCommand("V5","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd5.addParameter("PROJECT_NO");
         cmd5.addParameter("CODE_PART","F");
         cmd5.addReference("VOUCHER_DATE", "REFDATE5/DATA", "CURRENT_DATE");
         cmd5.addParameter("COMPANY",scompany);
      }
      if ("1".equals(enabl6)) 
      {
         trans.addQuery("REFDATE6","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         
         ASPCommand cmd6;
         cmd6=trans.addCustomCommand("V6","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd6.addParameter("CODENO_G");
         cmd6.addParameter("CODE_PART","G");
         cmd6.addReference("VOUCHER_DATE", "REFDATE6/DATA", "CURRENT_DATE");
         cmd6.addParameter("COMPANY",scompany);
      }
      if ("1".equals(enabl7)) 
      {
         trans.addQuery("REFDATE7","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
            
         ASPCommand cmd7;
         cmd7=trans.addCustomCommand("V7","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd7.addParameter("CODENO_H");
         cmd7.addParameter("CODE_PART","H");
         cmd7.addReference("VOUCHER_DATE", "REFDATE7/DATA", "CURRENT_DATE");
         cmd7.addParameter("COMPANY",scompany);
      }
      if ("1".equals(enabl8)) 
      {
         trans.addQuery("REFDATE8","DUAL","trunc(SYSDATE) CURRENT_DATE","","");

         ASPCommand cmd8;
         cmd8=trans.addCustomCommand("V8","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd8.addParameter("CODENO_I");
         cmd8.addParameter("CODE_PART","I");
         cmd8.addReference("VOUCHER_DATE", "REFDATE8/DATA", "CURRENT_DATE");
         cmd8.addParameter("COMPANY",scompany);
      }
      if ("1".equals(enabl9)) 
      {
         trans.addQuery("REFDATE9","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         
         ASPCommand cmd9;
         cmd9=trans.addCustomCommand("V9","PRE_ACCOUNTING_API.VALIDATE_CODEPART");
         cmd9.addParameter("CODENO_J");
         cmd9.addParameter("CODE_PART","J");
         cmd9.addReference("VOUCHER_DATE", "REFDATE9/DATA", "CURRENT_DATE");
         cmd9.addParameter("COMPANY",scompany);
      }
      trans = mgr.perform(trans);
   }

   protected String getDescription()
   {
      return "MPCCOWPREACCOUNTINGDLGTITLE: Pre Posting";
   }

   protected String getTitle()
   {
      return "MPCCOWPREACCOUNTINGDLGTITLE: Pre Posting";
   }

   protected void printContents() throws FndException
   { 
      ASPManager mgr = getASPManager();
      appendToHTML(lay.show());
      
      if (first_request && mgr.isExplorer())
      {  
	 printHiddenField("FIRST_REQUEST", "OK");
	 appendDirtyJavaScript("document.form.submit();");
      }
      printSpaces(1);
      printSubmitButton("OK","MPCCOWPREACCOUNTINGDLGOKBUTTON:    OK   ","");
      printSpaces(1);
      printSubmitButton("CANCEL","MPCCOWPREACCOUNTINGDLGCANCELBUTTON: Cancel","");
      
   }
}
