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
 *  File        : PreAccountingDlg1.java 
 *  Description : Pre Posting Detail Dialog 
 *  Notes       : 
 * ----------------------------------------------------------------------------
 *  Modified    :
 *     ErFelk   2010-Aug-12   Bug 90261, Modified Where clause of all code parts in setlovto() to remove the expired code parts been displayed.
 *     KaDilk   2009-Dec-07 - Bug 72392,Added column titles to code parts in setlov(). 
 *     MalLlk   2009-01-07    Bug 79293, Changed the value from CONTRACT to CONTRACT1, 
 *              2009-01-07    when using it as a parameter for queries.
 *     NiDalk   2007-07-10    Performed AQL Injection corrections.
 *     NaLrlk   2006-08-16    Bug 58216 Correcting SQL errors.
 *     JaJalk   2006-03-28    Set the integer mask for ACTIVITY_SEQ.
 *     JaJalk   2006-03-15    Modified the ACTIVITY_SEQ.
 *     KaDilk   2005-03-17    Bug 48389,Changed lov references which has used for code parts in PreAccountingDlg1.
 *     Cpeilk   2005-01-11    Bug 48963, Modified method run. Added a format mask to PRE_ACCOUNTING_ID.  
 *     JaJalk   2003-12-31    Enhanced the layout.
 *     JEWILK   2003-09-05    Call 100501. Modified run() to read PRE_ACCOUNTING_ID by method readNumberValue().
 *     JaMise   2002-07-29    Bug 25860 Added 'Order By' condition in method deffSearch()
 *     Usheera  2001-Oct-22 - Added ctx.removeContextFromCache to the run() 
 *     Suneth   2001-Jan-30 - Created.
 * ----------------------------------------------------------------------------
 */

package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PreAccountingDlg1 extends ASPPageProvider
{
   /* Static constants */

   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.PreAccountingDlg1");

   /* Instances created on page creation (immutable attributes) */

   private ASPForm frm;
   private ASPContext ctx;
   private ASPField f;
   private ASPBlock ref1;
   private ASPRowSet rowset1;
   private ASPBlockLayout reflay;
   private ASPBlock blk3;
   private ASPBlockLayout blk3lay;
   private ASPBlock blk4;
   private ASPBlockLayout blk4lay;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPTable tbl;
   private ASPBlockLayout lay;
   private ASPBlock refnew;
   private ASPBlockLayout refnlay;
   private ASPBlock blkPost;
   private ASPRowSet rowsetpost;
   private ASPBlock ref;
   private ASPBlock nameblk;
   private ASPRowSet nameset;
   private ASPBlockLayout namelay;
   private ASPCommandBar cmdbar1;

   /* Transient temporary variables (never cloned) */

   private ASPTransactionBuffer trans;
   private boolean tempflag;
   private boolean preposting;
   private String order_no;
   private String string_code;
   private String pre_accounting_id_sent;
   private String pre_accounting_id;
   private String enabl10;
   private String objstate;
   private String contract;
   private String scompany;
   private String company;
   private ASPCommand cmd;
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
   private ASPBuffer sec;
   private String id_exist;
   private String company_old;
   private String company1;
   private ASPBuffer r;
   private ASPQuery q;
   private boolean readonly;
   private String one;
   private String two;
   private String three;
   private String four;
   private String five;
   private String six;
   private String seven;
   private String eight;
   private String nine;
   private String ten;
   // Bug 90261, start
   private String scontract;
   // Bug 90261, end

   /* Construction */

   public PreAccountingDlg1(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
      
      frm   = mgr.getASPForm();
      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      
      tempflag = ctx.readFlag("TEMPFLAG", false);
      preposting = ctx.readFlag("PREPOSTING", false);  
      order_no = ctx.readValue("ORDER_NO", " ");
      string_code = ctx.readValue("STRING_CODE", " ");
      pre_accounting_id_sent = ctx.readValue("PREACCSENT", " ");
      pre_accounting_id  = ctx.readValue("PREACC", " ");
      enabl10 = ctx.readValue("ENABL10", " ");
      objstate = ctx.readValue("SOBJSTATE", " ");
      contract = ctx.readValue("CONTRACT"," ");
      scompany = ctx.readValue("SCOMPANY"," ");
      
      getidexist();
      
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         okFind();
      else if (mgr.buttonPressed("OK1"))
         submit();
      else if (mgr.buttonPressed("CANCEL"))
         cancel();
      else if (mgr.buttonPressed("OK"))
         ok();
      else 
      {
         order_no = mgr.readValue("ORDER_NO");
         string_code = mgr.readValue("STRING_CODE");
         pre_accounting_id_sent = mgr.formatNumber("PRE_ACCOUNTING_ID",mgr.readNumberValue("PRE_ACCOUNTING_ID"));
         pre_accounting_id = mgr.formatNumber("PRE_ACCOUNTING_ID",mgr.readNumberValue("PRE_ACCOUNTING_ID"));
         enabl10 = mgr.readValue("ENABL10");
         objstate = mgr.readValue("OBJSTATE");
         contract = mgr.readValue("CONTRACT");
         
         if (preposting)
            validateCodePart();
         
         getPreAccountingIdExist();
         deffSearch();
         
         if ("1".equals(id_exist)) 
         {
            okFind();
            getcompany();
         }
         else
         {
            launchNew();
            pre_accounting_id=pre_accounting_id_sent;
            company=company1;
         } 
      }	 
      adjust();
      
      ctx.writeValue("ORDER_NO",order_no);
      ctx.writeValue("STRING_CODE",string_code);
      ctx.writeValue("PREACCSENT",pre_accounting_id_sent);
      ctx.writeValue("PREACC",pre_accounting_id);
      ctx.writeValue("ENABL10",enabl10);
      ctx.writeValue("SOBJSTATE",objstate);
      ctx.writeValue("CONTRACT",contract);
      ctx.writeValue("SCOMPANY",scompany);
      ctx.writeFlag("PREPOSTING", preposting);
      ctx.writeFlag("TEMPFLAG", tempflag);

      ctx.removeContextFromCache(); // To fetch a new page, instead of the one in browser cache
   }

   public void  GetEnabledFields()
   {
      ASPQuery qry;
      qry = trans.addQuery("REFNEW","DUAL","Site_API.Get_Company(?) COMP,? STR_CODE","","");
      // Bug 79293, Changed the value to CONTRACT1.
      qry.addParameter("CONTRACT1", contract);
      qry.addParameter("DUMMY", string_code);
      
      cmd = trans.addCustomCommand( "POST", "Pre_Accounting_API.Execute_Accounting");
      cmd.addParameter("CODE_A,CODE_B,CODE_C,CODE_D,CODE_E,CODE_F,CODE_G,CODE_H,CODE_I,CODE_J");
      cmd.addReference("STR_CODE","REFNEW/DATA","STR_CODE");
      cmd.addParameter("CONTROL_TYPE");
      cmd.addReference("COMPANY", "REFNEW/DATA", "COMP");
   }

   public void  doenabledisable()
   {
      ASPManager mgr = getASPManager();

      eval(blkPost.generateAssignments());
      trans.clear();
      cmd = trans.addCustomFunction("COMPA", "Site_API.Get_Company", "COMPANY" );
      cmd.addParameter("CONTRACT1",contract);  
      
      trans=mgr.perform(trans);
      company = trans.getValue("COMPA/DATA/COMPANY");
      trans.clear();
      
      if ("0".equals(mgr.getASPField("CODE_A").getValue()))  
         enabl0 = "0"; 
      else 
         enabl0 = "1";
      if ("0".equals(mgr.getASPField("CODE_B").getValue()))  
         enabl1 = "0"; 
      else 
         enabl1 = "1";
      if ("0".equals(mgr.getASPField("CODE_C").getValue()))
         enabl2 = "0"; 
      else 
         enabl2 = "1";
      if ("0".equals(mgr.getASPField("CODE_D").getValue()))  
         enabl3 = "0"; 
      else 
         enabl3 = "1";
      if ("0".equals(mgr.getASPField("CODE_E").getValue()))  
         enabl4 = "0"; 
      else 
         enabl4 = "1";
      if ("0".equals(mgr.getASPField("CODE_F").getValue()))  
         enabl5 = "0"; 
      else 
         enabl5 = "1";
      if ("0".equals(mgr.getASPField("CODE_G").getValue()))  
         enabl6 = "0"; 
      else 
         enabl6 = "1";
      if ("0".equals(mgr.getASPField("CODE_H").getValue()))  
         enabl7 = "0"; 
      else 
         enabl7 = "1";
      if ("0".equals(mgr.getASPField("CODE_I").getValue()))  
         enabl8 = "0"; 
      else 
         enabl8 = "1";
      if ("0".equals(mgr.getASPField("CODE_J").getValue()))  
         enabl9 = "0"; 
      else 
         enabl9 = "1"; 
      
      if ("Closed".equals(objstate))
      { 
         enabl0 = "0"; 
         enabl1 = "0"; 
         enabl2 = "0"; 
         enabl3 = "0"; 
         enabl4 = "0"; 
         enabl5 = "0"; 
         enabl6 = "0"; 
         enabl7 = "0"; 
         enabl8 = "0"; 
         enabl9 = "0"; 
      }
      
      if (("1".equals(enabl0)) || ("1".equals(enabl1)) || ("1".equals(enabl2)) || ("1".equals(enabl3)) || ("1".equals(enabl4)) || ("1".equals(enabl5)) || ("1".equals(enabl6)) || ("1".equals(enabl7)) || ("1".equals(enabl8))|| ("1".equals(enabl9))) 
         preposting = true;
      
      setlovto();
   }

   public void  getAndDoEnable()
   {
      ASPManager mgr = getASPManager();
      ASPQuery qry;

      trans.clear();
      qry = trans.addQuery("REFNEW","DUAL","Site_API.Get_Company(?) COMP,? STR_CODE","","");
      // Bug 79293, Changed the value to CONTRACT1.
      qry.addParameter("CONTRACT1", contract);
      qry.addParameter("DUMMY", string_code);

      cmd = trans.addCustomFunction("COMPA", "Site_API.Get_Company", "COMPANY" );
      cmd.addParameter("CONTRACT1",contract);   
      
      cmd = trans.addCustomCommand( "POST", "Pre_Accounting_API.Execute_Accounting");
      cmd.addParameter("CODE_A,CODE_B,CODE_C,CODE_D,CODE_E,CODE_F,CODE_G,CODE_H,CODE_I,CODE_J");
      cmd.addReference("STR_CODE","REFNEW/DATA","STR_CODE");
      cmd.addParameter("CONTROL_TYPE");
      cmd.addReference("COMPANY", "REFNEW/DATA", "COMP");
      
      trans = mgr.perform(trans);
      
      double d_enabl0 = trans.getNumberValue("POST/DATA/CODE_A");
      double d_enabl1 = trans.getNumberValue("POST/DATA/CODE_B");
      double d_enabl2 = trans.getNumberValue("POST/DATA/CODE_C");
      double d_enabl3 = trans.getNumberValue("POST/DATA/CODE_D");
      double d_enabl4 = trans.getNumberValue("POST/DATA/CODE_E");
      double d_enabl5 = trans.getNumberValue("POST/DATA/CODE_F");
      double d_enabl6 = trans.getNumberValue("POST/DATA/CODE_G");
      double d_enabl7 = trans.getNumberValue("POST/DATA/CODE_H");
      double d_enabl8 = trans.getNumberValue("POST/DATA/CODE_I");
      double d_enabl9 = trans.getNumberValue("POST/DATA/CODE_J");
      
      enabl0 = mgr.getASPField("CODE_A").formatNumber(d_enabl0);
      enabl1 = mgr.getASPField("CODE_B").formatNumber(d_enabl1);
      enabl2 = mgr.getASPField("CODE_C").formatNumber(d_enabl2);
      enabl3 = mgr.getASPField("CODE_D").formatNumber(d_enabl3);
      enabl4 = mgr.getASPField("CODE_E").formatNumber(d_enabl4);
      enabl5 = mgr.getASPField("CODE_F").formatNumber(d_enabl5);
      enabl6 = mgr.getASPField("CODE_G").formatNumber(d_enabl6);
      enabl7 = mgr.getASPField("CODE_H").formatNumber(d_enabl7);
      enabl8 = mgr.getASPField("CODE_I").formatNumber(d_enabl8);
      enabl9 = mgr.getASPField("CODE_J").formatNumber(d_enabl9);
      
      company = trans.getValue("COMPA/DATA/COMPANY");
      
      if ("Closed".equals(objstate)) 
      {
         enabl0 = "0"; 
         enabl1 = "0"; 
         enabl2 = "0"; 
         enabl3 = "0"; 
         enabl4 = "0"; 
         enabl5 = "0"; 
         enabl6 = "0"; 
         enabl7 = "0"; 
         enabl8 = "0"; 
         enabl9 = "0"; 
      }
      setlovto();     
   }

   public void  setlovto()
   {
      ASPManager mgr = getASPManager();
      // Bug 90261, start
      scontract = mgr.readValue("CONTRACT");
      // Bug 90261, end

      getNames();
      
      f = mgr.getASPField("ACCOUNT_NO");
      f.setLabel(one);
      if ("0".equals(enabl0))  
         f.setReadOnly();
      else
      {  
         f.setDynamicLOV("PRE_ACCOUNT_CODEPART_A_MPCCOM", 600,450);
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"'");             	
         f.setLOVProperty("TITLE",one);
      }
      f = mgr.getASPField("COST_CENTER");
      f.setLabel(two);
      if ("0".equals(enabl1))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_B", 600,450); 
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",two);
         f.setLOVProperty("COLUMN_TITLES", "CODE_B=" + two );
      }
      f = mgr.getASPField("CODENO_C");
      f.setLabel(three);
      if ("0".equals(enabl2))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_C", 600,450);
          // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",three);
         f.setLOVProperty("COLUMN_TITLES", "CODE_C=" + three );

      }
      f = mgr.getASPField("CODENO_D");
      f.setLabel(four);
      if ("0".equals(enabl3))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_D", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",four);
         f.setLOVProperty("COLUMN_TITLES", "CODE_D=" + four );
      }
      f = mgr.getASPField("OBJECT_NO");
      f.setLabel(five);
      if ("0".equals(enabl4))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_E", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",five);
         f.setLOVProperty("COLUMN_TITLES", "CODE_E=" + five );
      }      
      f = mgr.getASPField("PROJECT_NO");
      f.setLabel(six);
      if ("0".equals(enabl5))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_F", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",six);
         f.setLOVProperty("COLUMN_TITLES", "CODE_F=" + six );
      }                   
      f = mgr.getASPField("CODENO_G");
      f.setLabel(seven);
      if ("0".equals(enabl6))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_G", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",seven);
         f.setLOVProperty("COLUMN_TITLES", "CODE_G=" + seven );
      }
      f = mgr.getASPField("CODENO_H");
      f.setLabel(eight);
      if ("0".equals(enabl7))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_H", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",eight);
         f.setLOVProperty("COLUMN_TITLES", "CODE_H=" + eight );
      }
      f = mgr.getASPField("CODENO_I");
      f.setLabel(nine);
      if ("0".equals(enabl8))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_I", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",nine);
         f.setLOVProperty("COLUMN_TITLES", "CODE_I=" + nine );

      }
      f = mgr.getASPField("CODENO_J");
      f.setLabel(ten);
      if ("0".equals(enabl9))  
         f.setReadOnly();
      else
      {
         f.setDynamicLOV("CODE_J", 600,450);
         // Bug 90261, Modified to filter the expired code parts from the LOV
         f.setLOVProperty("WHERE", "COMPANY = '"+company+"' AND trunc(Site_API.Get_Site_Date( '" + scontract + "')) BETWEEN VALID_FROM AND VALID_UNTIL");
         f.setLOVProperty("TITLE",ten);
         f.setLOVProperty("COLUMN_TITLES", "CODE_J=" + ten );
      }  
      trans.clear();
      
      trans.addSecurityQuery("ACTIVITY");
      trans = mgr.validate(trans);
      sec = trans.getSecurityInfo();
      
      if (!(sec.itemExists("ACTIVITY")) || ("0".equals(enabl10))) 
         mgr.getASPField("ACTIVITY_SEQ").setReadOnly();
      else
      {
         String value = rowset.getRow().getValue("PROJECT_NO");
         mgr.getASPField("ACTIVITY_SEQ").setLOVProperty("TITLE","Activity Seq");
         
         if ("".equals(value)) 
            mgr.getASPField("ACTIVITY_SEQ").setLOVProperty("WHERE","PROJECT_ID IS NULL");
         else
            mgr.getASPField("ACTIVITY_SEQ").setLOVProperty("PROJECT_ID", rowset.getRow().getValue("PROJECT_NO"));
      } 
      assign();
   }

   public void  getPreAccountingIdExist()
   {
      ASPManager mgr = getASPManager();
      ASPQuery qry;
      ASPQuery qry1;

      ASPTransactionBuffer xx;
      qry = trans.addQuery("REF1","DUAL","Site_API.Get_Company(?) COMPANY","","");
      // Bug 79293, Changed the value to CONTRACT1.
      qry.addParameter("CONTRACT1", contract);

      qry1 = trans.addQuery("MAIN3","DUAL","PRE_ACCOUNTING_API.Check_Exist(?) ID_EXIST","","");
      qry1.addParameter("DUMMY", pre_accounting_id_sent);

      xx =mgr.perform(trans);

      id_exist = xx.getValue("MAIN3/DATA/ID_EXIST");
      company_old = xx.getValue("REF1/DATA/COMPANY");
      scompany = company_old;
   }

   public void  launchNew()
   {
      newRow();
      getAndDoEnable();
      company1=company_old;
   }

   public void  cancel()
   {
   }

   /* Utility functions */

   public void  assign()
   {
      eval(nameblk.generateAssignments());
      eval(blk.generateAssignments());
      eval(ref1.generateAssignments());
      eval(blk3.generateAssignments());
      eval(refnew.generateAssignments());
      
      r = rowset.getRow();
      if (tempflag) 
         tbl.fitToPage();
   }

   public void  singleRowMode()
   {
      tempflag = false;
      if (rowset.countSelectedRows() > 0)
         rowset.setFilterOn();
   }

   /* Command Bar Edit Group functions */

   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("MAIN","PRE_ACCOUNTING_API.New__",blk);
      cmd.setOption("ACTION","PREPARE");
      
      trans.clear();  
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("MAIN/DATA");
      singleRowMode();
      rowset.addRow(data);
   }

   /* Command Bar Search Group functions */

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear(); 
      GetEnabledFields();
      
      trans.addQuery("REFDATE","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
      q = trans.addQuery(blk);
      if (mgr.dataTransfered())
         q.addOrCondition( mgr.getTransferedData() );
      q.includeMeta("ALL");
      mgr.submit(trans);
      doenabledisable();
   }

   /* Command Bar Browse Group functions */

   public void  getcompany()
   {
      ASPBuffer buf; 
      String com = rowset1.getRow().getValue("COMPANY");
      buf = rowset.getRow();
      rowset.setRow(buf);
      assign();
   }

   public void  getidexist()
   {
      eval(blk3.generateAssignments());
   }

   /* Button functions */

   public void  ok()
   {
      submit();
   }

   public void  submit()
   {
      ASPManager mgr = getASPManager();

      validateCodePart();   
      rowset.changeRow();
      trans.clear();
      
      mgr.getASPField("ACCOUNT_NO").setReadOnly();
      mgr.getASPField("COST_CENTER").setReadOnly();
      mgr.getASPField("CODENO_C").setReadOnly();
      mgr.getASPField("CODENO_D").setReadOnly();
      mgr.getASPField("OBJECT_NO").setReadOnly();
      mgr.getASPField("PROJECT_NO").setReadOnly();
      mgr.getASPField("CODENO_G").setReadOnly();
      mgr.getASPField("CODENO_H").setReadOnly();
      mgr.getASPField("CODENO_I").setReadOnly();
      mgr.getASPField("CODENO_J").setReadOnly();
      
      r = rowset.getRow();
      r.setFieldItem("PRE_ACCOUNTING_ID",pre_accounting_id);
      r.setFieldItem("CONTRACT1",contract);
      r.setFieldItem("COMPANY1",scompany);
      rowset.setRow(r);
      
      rowset.setEdited("CONTRACT");
      rowset.setEdited("COMPANY");
      mgr.submit(trans);
      
      readonly = true;
      tempflag = true;
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      setVersion(3);
      
      ref1 =  mgr.newASPBlock("REF1");
      f= ref1.addField("COMPANY");
      f.setUpperCase();
      rowset1 = ref1.getASPRowSet();
      
      blk3 =  mgr.newASPBlock("MAIN3");
      f = blk3 .addField("ID_EXIST","Number");
      blk3.setView("DUAL");
      
      blk3lay = blk3.getASPBlockLayout();
      blk3lay.setDefaultLayoutMode(blk3lay.MULTIROW_LAYOUT);      
      
      blk4 =  mgr.newASPBlock("MAIN4");  
      blk4.addField("VOUCHER_DATE");
      blk4.addField("CODE_PART");
      blk4.addField("CODEVALUE");
      
      blk4lay = blk4.getASPBlockLayout();
      blk4lay.setDefaultLayoutMode(blk3lay.MULTIROW_LAYOUT);      
      
      blk = mgr.newASPBlock("MAIN");
      
      f = blk.addField("OBJID");
      f.setHidden();
      
      f = blk.addField("OBJVERSION");
      f.setHidden();
      
      f = blk.addField("PRE_ACCOUNTING_ID","Number","##########");
      f.setHidden();
      
      f = blk.addField("ACCOUNT_NO");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1ACNO: Account");
      f.setSize(12);
      
      f = blk.addField("COST_CENTER");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1COCN: Cost cent");  
      f.setSize(12);
      
      f = blk.addField("CODENO_C","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1CODC: Code C");   
      f.setSize(12);
      
      f = blk.addField("CODENO_D","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1CODD: Code D");      
      f.setSize(12);
      
      f = blk.addField("OBJECT_NO","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1CODE: Code E");      
      f.setSize(12);
      
      f = blk.addField("PROJECT_NO","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1CODF: Code F");
      f.setSize(12);    
      
      f = blk.addField("CODENO_G","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1CODG: Code G");      
      f.setSize(12);
      
      f = blk.addField("CODENO_H","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1CODH: Code H");      
      f.setSize(12);
      
      f = blk.addField("CODENO_I","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1CODI: Code I");      
      f.setSize(12);
      
      f = blk.addField("CODENO_J","String");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1CODJ: Code J");      
      f.setSize(12);
      
      f = blk.addField("ACTIVITY_SEQ","Number", "###############");
      f.setUpperCase();
      f.setLabel("MPCCOWPREACCOUNTINGDLG1ACSQ: Activity Seq");
      f.setSize(12);
      
      f = blk.addField("COMPANY1");
      f.setDbName("COMPANY");
      f.setUpperCase();
      f.setHidden();
      
      f = blk.addField("CONTRACT1");
      f.setDbName("CONTRACT");
      f.setUpperCase();
      f.setHidden();

      f = blk.addField("DUMMY");
      f.setFunction("''");
      f.setHidden();
      
      blk.setView("PRE_ACCOUNTING");
      blk.defineCommand("PRE_ACCOUNTING_API","New__,Modify__,Remove__");
      blk.setTitle("MPCCOWPREACCOUNTINGDLG1TEMMSG: Enter value for known code parts");
      
      cmdbar1 = mgr.newASPCommandBar(blk);
      
      rowset = blk.getASPRowSet();
      
      tbl = mgr.newASPTable(blk);
      
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.CUSTOM_LAYOUT);
      lay.setEditable();
      
      refnew=  mgr.newASPBlock("REFNEW");
      f= refnew.addField("COMP");
      
      refnlay = refnew.getASPBlockLayout();
      refnlay.setDefaultLayoutMode(refnlay.EDIT_LAYOUT);  
      
      blkPost = mgr.newASPBlock("POST");
      f = blkPost.addField("CODE_A","Integer");
      f = blkPost.addField("CODE_B","Integer");
      f = blkPost.addField("CODE_C","Integer");
      f = blkPost.addField("CODE_D","Integer");
      f = blkPost.addField("CODE_E","Integer");
      f = blkPost.addField("CODE_F","Integer");
      f = blkPost.addField("CODE_G","Integer");
      f = blkPost.addField("CODE_H","Integer");
      f = blkPost.addField("CODE_I","Integer");
      f = blkPost.addField("CODE_J","Integer");
      f = refnew.addField("STR_CODE");
      f = blkPost.addField("CONTROL_TYPE");
      
      rowsetpost = blkPost.getASPRowSet();
      
      ref = mgr.newASPBlock("REFDATE");
      ref.addField( "CURRENT_DATE", "Date" );
      
      nameblk = mgr.newASPBlock("NAMES");
      
      f = nameblk.addField("CODE_NAME");
      f.setHidden();
      
      f = nameblk.addField("ONE");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("TWO");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("THREE");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("FOUR");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("FIVE");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("SIX");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("SEVEN");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("EIGHT");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("NINE");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("TEN");
      f.setHidden();
      f.setFunction("NULL");  
      
      f = nameblk.addField("ENABLA");
      f.setHidden();
      f.setFunction("NULL");
      
      f = nameblk.addField("OBJSTATEA");
      f.setHidden();
      f.setFunction("NULL");
      
      nameblk.setView("ACCOUNTING_CODE_PARTS");

      nameset = nameblk.getASPRowSet();
      
      namelay = nameblk.getASPBlockLayout();
      namelay.setDefaultLayoutMode(namelay.MULTIROW_LAYOUT);   
   }

   public void  adjust()
   {
      r=rowset.getRow();					
      r.setValue("PRE_ACCOUNTING_ID",pre_accounting_id);	
      r.setValue("CONTRACT",contract); 
      getNames();
   }

   public void  getNames()
   {
      eval(nameblk.generateAssignments());
      
      nameset.first();
      
      String one__= nameset.getRow().getValue("CODE_NAME");
      one = one__ ;
      nameset.next();
      
      String two__= nameset.getRow().getValue("CODE_NAME");
      two = two__ ;
      nameset.next();
      
      String three__= nameset.getRow().getValue("CODE_NAME");
      three = three__ ;
      nameset.next();
      
      String four__= nameset.getRow().getValue("CODE_NAME");
      four = four__ ;
      nameset.next();
      
      String five__= nameset.getRow().getValue("CODE_NAME");
      five = five__ ;
      nameset.next();
      
      String six__= nameset.getRow().getValue("CODE_NAME");
      six = six__ ;
      nameset.next();
      
      String seven__= nameset.getRow().getValue("CODE_NAME");
      seven = seven__ ;
      nameset.next();
      
      String eight__= nameset.getRow().getValue("CODE_NAME");
      eight = eight__ ;
      nameset.next();
      
      String nine__= nameset.getRow().getValue("CODE_NAME");
      nine = nine__ ;
      nameset.next();
      
      String ten__= nameset.getRow().getValue("CODE_NAME");
      ten = ten__ ;
      nameset.next();
   }

   public void  deffSearch()
   {
      ASPManager mgr = getASPManager();
      ASPQuery qry;

      ASPTransactionBuffer xx;
      qry = trans.addQuery("REF11","DUAL","Site_API.Get_Company(?) COMPANY","","");
      // Bug 79293, Changed the value to CONTRACT1.
      qry.addParameter("CONTRACT1", contract);
      xx =mgr.perform(trans);

      String company_=xx.getValue("REF11/DATA/COMPANY");
      q = trans.addQuery(nameblk);
      // Bug ID 58216, Applied the SQL Injection security patch, added .addparameter and modified where condition
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY", company_);
      q.setOrderByClause("CODE_PART");                  //Bug fix 25860; added order by
      q.includeMeta("ALL");
      
      mgr.submit(trans);
      trans.clear();
      
      eval(nameblk.generateAssignments());
      
      r = nameset.getRow();
      r.setFieldItem("ENABLA",enabl10);
      nameset.setRow(r);
      
      r = nameset.getRow();
      r.setFieldItem("OBJSTATEA",objstate);
      nameset.setRow(r);
      
      eval(nameblk.generateAssignments());
   }

   /* validation */

   public void  validateCodePart()
   {
      ASPManager mgr = getASPManager();

      getAndDoEnable();
      trans.clear();
      
      String check0 = mgr.readValue("ACCOUNT_NO");
      String check1 = mgr.readValue("COST_CENTER");
      String check2 = mgr.readValue("CODENO_C");
      String check3 = mgr.readValue("CODENO_D");
      String check4 = mgr.readValue("OBJECT_NO");
      String check5 = mgr.readValue("PROJECT_NO");
      String check6 = mgr.readValue("CODENO_G");
      String check7 = mgr.readValue("CODENO_H");
      String check8 = mgr.readValue("CODENO_I");
      String check9 = mgr.readValue("CODENO_J");
      
      if (mgr.isEmpty(check0))
         enabl0 = "0" ; 
      if (mgr.isEmpty(check1))
         enabl1 = "0" ; 
      if (mgr.isEmpty(check2))
         enabl2 = "0" ; 
      if (mgr.isEmpty(check3))
         enabl3 = "0" ; 
      if (mgr.isEmpty(check4))
         enabl4 = "0" ; 
      if (mgr.isEmpty(check5))
         enabl5 = "0" ; 
      if (mgr.isEmpty(check6))
         enabl6 = "0" ; 
      if (mgr.isEmpty(check7))
         enabl7 = "0" ; 
      if (mgr.isEmpty(check8))
         enabl8 = "0" ; 
      if (mgr.isEmpty(check9))
         enabl9 = "0" ;               

      if ("1".equals(enabl0)) 
      { 
         trans.addQuery("REFDATE0","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry0 = trans.addQuery("REF0","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry0.addParameter("CONTRACT1", contract);
         
         ASPCommand cmd0;
         cmd0=trans.addCustomCommand("V0","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd0.addParameter("ACCOUNT_NO");
         cmd0.addParameter("CODE_PART","A");
         cmd0.addReference("VOUCHER_DATE", "REFDATE0/DATA", "CURRENT_DATE");
         cmd0.addReference("COMPANY", "REF0/DATA", "COMPANY");
      }
      
      if ("1".equals(enabl1)) 
      { 
         trans.addQuery("REFDATE1","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry1 = trans.addQuery("REF11","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry1.addParameter("CONTRACT1", contract);

         ASPCommand cmd1;
         cmd1=trans.addCustomCommand("V1","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd1.addParameter("COST_CENTER");
         cmd1.addParameter("CODE_PART","B");
         cmd1.addReference("VOUCHER_DATE", "REFDATE1/DATA", "CURRENT_DATE");
         cmd1.addReference("COMPANY", "REF11/DATA", "COMPANY");
      }
      
      if ("1".equals(enabl2)) 
      { 
         trans.addQuery("REFDATE2","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry2 = trans.addQuery("REF2","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry2.addParameter("CONTRACT1", contract);
         
         ASPCommand cmd2;
         cmd2=trans.addCustomCommand("V2","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd2.addParameter("CODENO_C");
         cmd2.addParameter("CODE_PART","C");
         cmd2.addReference("VOUCHER_DATE", "REFDATE2/DATA", "CURRENT_DATE");
         cmd2.addReference("COMPANY", "REF2/DATA", "COMPANY");
      }
      
      if ("1".equals(enabl3)) 
      { 
         trans.addQuery("REFDATE3","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry3 = trans.addQuery("REF3","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry3.addParameter("CONTRACT1", contract);
         
         ASPCommand cmd3;
         cmd3=trans.addCustomCommand("V3","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd3.addParameter("CODENO_D");
         cmd3.addParameter("CODE_PART","D");
         cmd3.addReference("VOUCHER_DATE", "REFDATE3/DATA", "CURRENT_DATE");
         cmd3.addReference("COMPANY", "REF3/DATA", "COMPANY");
      }
      
      if ("1".equals(enabl4)) 
      { 
         trans.addQuery("REFDATE4","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry4 = trans.addQuery("REF4","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry4.addParameter("CONTRACT1", contract);
         
         ASPCommand cmd4;
         cmd4=trans.addCustomCommand("V4","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd4.addParameter("OBJECT_NO");
         cmd4.addParameter("CODE_PART","E");
         cmd4.addReference("VOUCHER_DATE", "REFDATE4/DATA", "CURRENT_DATE");
         cmd4.addReference("COMPANY", "REF4/DATA", "COMPANY");
      } 
      
      if ("1".equals(enabl5)) 
      { 
         trans.addQuery("REFDATE5","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry5 = trans.addQuery("REF5","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry5.addParameter("CONTRACT1", contract);
         
         ASPCommand cmd5;
         cmd5=trans.addCustomCommand("V5","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd5.addParameter("PROJECT_NO");
         cmd5.addParameter("CODE_PART","F");
         cmd5.addReference("VOUCHER_DATE", "REFDATE5/DATA", "CURRENT_DATE");
         cmd5.addReference("COMPANY", "REF5/DATA", "COMPANY");
      }
      
      if ("1".equals(enabl6)) 
      { 
         trans.addQuery("REFDATE6","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry6 = trans.addQuery("REF6","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry6.addParameter("CONTRACT1", contract);
         
         ASPCommand cmd6;
         cmd6=trans.addCustomCommand("V6","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd6.addParameter("CODENO_G");
         cmd6.addParameter("CODE_PART","G");
         cmd6.addReference("VOUCHER_DATE", "REFDATE6/DATA", "CURRENT_DATE");
         cmd6.addReference("COMPANY", "REF6/DATA", "COMPANY");
      }
      
      if ("1".equals(enabl7)) 
      { 
         trans.addQuery("REFDATE7","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry7 = trans.addQuery("REF7","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry7.addParameter("CONTRACT1", contract);
         
         ASPCommand cmd7;
         cmd7=trans.addCustomCommand("V7","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd7.addParameter("CODENO_H");
         cmd7.addParameter("CODE_PART","H");
         cmd7.addReference("VOUCHER_DATE", "REFDATE7/DATA", "CURRENT_DATE");
         cmd7.addReference("COMPANY", "REF7/DATA", "COMPANY");
      }
      
      if ("1".equals(enabl8)) 
      { 
         trans.addQuery("REFDATE8","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry8 = trans.addQuery("REF8","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry8.addParameter("CONTRACT1", contract);
         
         ASPCommand cmd8;
         cmd8=trans.addCustomCommand("V8","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd8.addParameter("CODENO_I");
         cmd8.addParameter("CODE_PART","I");
         cmd8.addReference("VOUCHER_DATE", "REFDATE8/DATA", "CURRENT_DATE");
         cmd8.addReference("COMPANY", "REF8/DATA", "COMPANY");
      }
      
      if ("1".equals(enabl9)) 
      { 
         trans.addQuery("REFDATE9","DUAL","trunc(SYSDATE) CURRENT_DATE","","");
         ASPQuery qry9 = trans.addQuery("REF9","DUAL","Site_API.Get_Company(?) COMPANY","","");
	 // Bug 79293, Changed the value to CONTRACT1.
         qry9.addParameter("CONTRACT1", contract);

         ASPCommand cmd9;
         cmd9=trans.addCustomCommand("V9","PRE_ACCOUNTING_API.VALIDATE_CODEPART"); 
         cmd9.addParameter("CODENO_J");
         cmd9.addParameter("CODE_PART","J");
         cmd9.addReference("VOUCHER_DATE", "REFDATE9/DATA", "CURRENT_DATE");
         cmd9.addReference("COMPANY", "REF9/DATA", "COMPANY");
      }
      trans = mgr.perform(trans);  
   }

   /* HTML */

   protected String getDescription()
   {
      return "MPCCOWPREACCOUNTINGDLG1TITLE: Pre Posting";
   }

   protected String getTitle()
   {
      return "MPCCOWPREACCOUNTINGDLG1TITLE: Pre Posting";
   }

   protected void printContents() throws FndException
   {
      appendToHTML(lay.show());
      
      if (tempflag) 
      {
         appendDirtyJavaScript("window.close();\n");
      }
      else
      {
         printSpaces(1);
         printSubmitButton("OK","MPCCOWPREACCOUNTINGDLG1OKBUTTON:    OK   ","");
         printSpaces(1);
         printSubmitButton("CANCEL","MPCCOWPREACCOUNTINGDLG1CANCELBUTTON: Cancel","onClick='window.close()'");
      }
   }
}
