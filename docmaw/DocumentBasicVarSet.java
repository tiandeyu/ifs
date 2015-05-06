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
*  File        : DocumentBasicVarSet.java
*  Modified    :
*  23/05/2001  Shdilk  Call Id 65240 : Changed labels in format tab
*  30/12/2002  Nisilk  2002-2-SP3 Merge. Replaced the Base file in Salsa with file in SP3 - Track
*  2003-02-26  MDAHSE  Added ugly hack in printContents() to get PO-scanning to work ok
*  2003-08-01  NISILK  Fixed call 95769, added method adjust() and modified method run().
*  2003-10-18  DIKALK  Call ID 107500: Removed call to mgr.generateHeadTag() in printContents()
*  2004-02-09  BAKALK  Modified predefine(). Extended max. length of "ITEM8_DEFAULT_VALUE".
*  2004-03-23  DIKALK  Did not merge sp1. Left column length of DEFAULT_VALUT at 30
*  2005-03-16  SukMlk  Merged Bug 49235
*  2006-02-08  Karalk  call is 133015.
*  2006-02-13  ThWilk  Fixed Call 133382, Added methods saveReturnITEM8 and hasPermission.Modified predefine().
*  2006-05-31  Dikalk  Bug 57779, Removed methods hasPermission() and saveReturnITEM8()
*  2006-07-25  BAKALK  Bug ID 58216, Fixed Sql Injection.
*  2008-03-08  SHTHLK  Bug Id 72250, Added an Order by clause to countFindITEM8()
*  2008-05-25  SHTHLK  Bug Id 73601, Set the fields mentioned in the bug description as mandatory.
*  2008-10-27  VIRALK  Bug Id 78032 Specify tab id when adding tabs to the page.
*  2009-01-08  AMNALK  Bug Id 79146, Added new javascript function validateItem8DefaultValue().
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocumentBasicVarSet extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentBasicVarSet");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
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
   private ASPBlock itemblk7;
   private ASPRowSet itemset7;
   private ASPCommandBar itembar7;
   private ASPTable itemtbl7;
   private ASPBlockLayout itemlay7;
   private ASPBlock itemblk8;
   private ASPRowSet itemset8;
   private ASPCommandBar itembar8;
   private ASPTable itemtbl8;
   private ASPBlockLayout itemlay8;
   private ASPBlock itemblk9;
   private ASPRowSet itemset9;
   private ASPCommandBar itembar9;
   private ASPTable itemtbl9;
   private ASPBlockLayout itemlay9;
   private ASPTabContainer tabs;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private String activetab;
   private String val;
   private ASPCommand cmd;
   private String className;
   private String cumul_prgs1;
   private String cumul_prgs;
   private String cumul_dur1;
   private String cumul_dur;
   private ASPQuery q;
   private ASPBuffer data;
   private int headrowno;
   private String mil_prof;
   private int currrow;
   private String req_prof;
   private String txt;


   //===============================================================
   // Construction
   //===============================================================
   public DocumentBasicVarSet(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      activetab   = null;
      val   = null;
      cmd   = null;
      className   = null;
      cumul_prgs1   = null;
      cumul_prgs   = null;
      cumul_dur1   = null;
      cumul_dur   = null;
      q   = null;
      data   = null;
      headrowno   = 0;
      mil_prof   = null;
      currrow   = 0;
      req_prof   = null;
      txt   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocumentBasicVarSet page = (DocumentBasicVarSet)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.activetab   = null;
      page.val   = null;
      page.cmd   = null;
      page.className   = null;
      page.cumul_prgs1   = null;
      page.cumul_prgs   = null;
      page.cumul_dur1   = null;
      page.cumul_dur   = null;
      page.q   = null;
      page.data   = null;
      page.headrowno   = 0;
      page.mil_prof   = null;
      page.currrow   = 0;
      page.req_prof   = null;
      page.txt   = null;

      // Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk0 = page.getASPBlock(itemblk0.getName());
      page.itemset0 = page.itemblk0.getASPRowSet();
      page.itembar0 = page.itemblk0.getASPCommandBar();
      page.itemtbl0 = page.getASPTable(itemtbl0.getName());
      page.itemlay0 = page.itemblk0.getASPBlockLayout();
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
      page.itembar6 = page.itemblk6.getASPCommandBar();
      page.itemtbl6 = page.getASPTable(itemtbl6.getName());
      page.itemlay6 = page.itemblk6.getASPBlockLayout();
      page.itemblk7 = page.getASPBlock(itemblk7.getName());
      page.itemset7 = page.itemblk7.getASPRowSet();
      page.itembar7 = page.itemblk7.getASPCommandBar();
      page.itemtbl7 = page.getASPTable(itemtbl7.getName());
      page.itemlay7 = page.itemblk7.getASPBlockLayout();
      page.itemblk8 = page.getASPBlock(itemblk8.getName());
      page.itemset8 = page.itemblk8.getASPRowSet();
      page.itembar8 = page.itemblk8.getASPCommandBar();
      page.itemtbl8 = page.getASPTable(itemtbl8.getName());
      page.itemlay8 = page.itemblk8.getASPBlockLayout();
      page.itemblk9 = page.getASPBlock(itemblk9.getName());
      page.itemset9 = page.itemblk9.getASPRowSet();
      page.itembar9 = page.itemblk9.getASPCommandBar();
      page.itemtbl9 = page.getASPTable(itemtbl9.getName());
      page.itemlay9 = page.itemblk9.getASPBlockLayout();
      page.tabs = page.getASPTabContainer();
      page.f = page.getASPField(f.getName());

      return page;
   }

   public void adjust()
   {
      if  ( tabs.getActiveTab()==7 )
         if (itemset8.countRows()==0)
         {
            itemlay8.setLayoutMode(itemlay8.FIND_LAYOUT);
         }
   }

   public void cancelFindITEM8()
   {
      debug("calling cancel find");
      ASPManager mgr = getASPManager();
      tabs.setActiveTab(1);

   }


   public void run()
   {
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
     tabs.saveActiveTab();
     adjust();
   }

//=============================================================================
//  Validation
//=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

      if  ( "ITEM5_DOC_CLASS".equals(val) )
      {
         cmd = trans.addCustomFunction("DOCNAME","Doc_Class_API.Get_Name","ITEM5_CLASS_NAME");
         cmd.addParameter("ITEM5_DOC_CLASS");
         trans = mgr.validate(trans);
         className = trans.getValue("DOCNAME/DATA/ITEM5_CLASS_NAME");
         trans.clear();

         txt = (mgr.isEmpty(className) ? "" : className) + "^";

         mgr.responseWrite(txt);
      }

     else if  ( "ITEM1_PROGRESS_TEMP".equals(val) )
      {
          double cumul_prgs1 = Integer.valueOf(mgr.readValue("ITEM1_PROGRESS_TEMP")).intValue();
          double cumul_prgs = (cumul_prgs1)/100;

         txt = (mgr.isEmpty(Double.toString(cumul_prgs)) ? "" : Double.toString(cumul_prgs)) + "^";
         mgr.responseWrite(txt);
      }

      else if  ( "ITEM1_DURATION_TEMP".equals(val) )
      {
          double cumul_dur1 =Integer.valueOf(mgr.readValue("ITEM1_DURATION_TEMP")).intValue();
          double cumul_dur = cumul_dur1 / 100;
         txt = (mgr.isEmpty(Double.toString(cumul_dur)) ? "" : Double.toString(cumul_dur)) + "^";
         mgr.responseWrite(txt);
      }

     mgr.endResponse();
   }

//=============================================================================
//  Command functions
//=============================================================================
//=============================================================================
// ITEM0

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk0);

        if ( itemset0.countRows() == 0 )
        {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSETNODATAFOUND: No data found"));
         itemset0.clear();
        }
//        else
//        {
//         itemlay0.setLayoutMode(itemlay0.SINGLE_LAYOUT);
//         okFindITEM1();
//        }
        eval(itemset1.syncItemSets());    
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","DOC_MILESTONE_PROFILE_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      itemset0.addRow(data);
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk0);
      itemlay0.setCountValue(toInt(itemset0.getValue("N")));
      itemset0.clear();
   }

//=============================================================================
// ITEM1

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addEmptyQuery(itemblk1);
      //bug 58216 starts
      q.addWhereCondition("MILESTONE_PROFILE = ?");
      q.addParameter("ITEM0_MILESTONE_PROFILE",itemset0.getValue("MILESTONE_PROFILE")); 
      //bug 58216 end
      q.includeMeta("ALL");
      headrowno = itemset0.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk1);

      itemset0.goTo(headrowno);
   }


   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      q.addWhereCondition("MILESTONE_PROFILE = ?");
      q.addParameter("ITEM0_MILESTONE_PROFILE",itemset0.getValue("MILESTONE_PROFILE"));
      //bug 58216 end
      mgr.querySubmit(trans,itemblk1);
      itemlay1.setCountValue(toInt(itemset1.getValue("N")));
      itemset1.clear();
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","DOC_MILESTONE_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");

      mil_prof = itemset0.getRow().getValue("MILESTONE_PROFILE");
      data.setFieldItem("ITEM1_MILESTONE_PROFILE",mil_prof);

      itemset1.addRow(data);
   }


   public void  saveReturnITEM1()
   {
      ASPManager mgr = getASPManager();

        currrow = itemset0.getCurrentRowNo();
        itemset1.changeRow();
      // mgr.querySubmit(trans,itemblk1);       Here querySubmit gives an error.
        mgr.submit(trans);
      itemset0.goTo(currrow);
   }


   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk2);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk2);

        if ( itemset2.countRows() == 0 )
        {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSETNODATAFOUND: No data found"));
         itemset2.clear();
        }
   }


   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk2);
      itemlay2.setCountValue(toInt(itemset2.getValue("N")));
      itemset2.clear();
   }


   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM2","DOC_FORMAT_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      itemset2.addRow(data);
   }

//=============================================================================
// ITEM3

   public void  okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk3);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk3);

        if ( itemset3.countRows() == 0 )
        {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSETNODATAFOUND: No data found"));
         itemset3.clear();
        }
   }


   public void  countFindITEM3()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk3);
      itemlay3.setCountValue(toInt(itemset3.getValue("N")));
      itemset3.clear();
   }


   public void  newRowITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM3","DOCUMENT_REASON_FOR_ISSUE_API.New__",itemblk3);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      itemset3.addRow(data);
   }

//=============================================================================
// ITEM4

   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk4);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk4);

        if ( itemset4.countRows() == 0 )
        {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSETNODATAFOUND: No data found"));
         itemset4.clear();
        }
        eval(itemset5.syncItemSets());  
   }


   public void  countFindITEM4()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk4);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk4);
      itemlay4.setCountValue(toInt(itemset4.getValue("N")));
      itemset4.clear();
   }


   public void  newRowITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM4","DOC_REQUIREMENT_PROFILE_API.New__",itemblk4);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM4/DATA");
      itemset4.addRow(data);
   }

//=============================================================================
// ITEM5

   public void  okFindITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk5);
      //bug 58216 starts
      q.addWhereCondition("DOC_REQ_PROFILE = ?");
      q.addParameter("ITEM4_DOC_REQ_PROFILE",itemset4.getValue("DOC_REQ_PROFILE"));
      //bug 58216 end
      q.includeMeta("ALL");
      headrowno = itemset4.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk5);
      itemset4.goTo(headrowno);
   }


   public void  countFindITEM5()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk5);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      q.addWhereCondition("DOC_REQ_PROFILE = ?");
      q.addParameter("ITEM4_DOC_REQ_PROFILE",itemset4.getValue("DOC_REQ_PROFILE"));
      //bug 58216 end
      mgr.querySubmit(trans,itemblk5);
      itemlay5.setCountValue(toInt(itemset5.getValue("N")));
      itemset5.clear();
   }


   public void  newRowITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM5","DOC_REQUIREMENT_STD_LIST_API.New__",itemblk5);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM5/DATA");

      req_prof = itemset4.getRow().getValue("DOC_REQ_PROFILE");
      data.setFieldItem("ITEM5_DOC_REQ_PROFILE",req_prof);

      itemset5.addRow(data);
   }

//=============================================================================
// ITEM6

   public void  okFindITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk6);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk6);

        if ( itemset6.countRows() == 0 )
        {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSETNODATAFOUND: No data found"));
         itemset6.clear();
        }
   }


   public void  countFindITEM6()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk6);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk6);
      itemlay6.setCountValue(toInt(itemset6.getValue("N")));
      itemset6.clear();
   }


   public void  newRowITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM6","DOC_MEDIUM_API.New__",itemblk6);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM6/DATA");
      itemset6.addRow(data);
   }

//=============================================================================
// ITEM7

   public void  okFindITEM7()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk7);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk7);

        if ( itemset7.countRows() == 0 )
        {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSETNODATAFOUND: No data found"));
         itemset7.clear();
        }
   }


   public void  countFindITEM7()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk7);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk7);
      itemlay7.setCountValue(toInt(itemset7.getValue("N")));
      itemset7.clear();
   }


   public void  newRowITEM7()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM7","DOCUMENT_HISTORY_SETTINGS_API.New__",itemblk7);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM7/DATA");
      itemset7.addRow(data);
   }

//=============================================================================
// ITEM8

   public void  okFindITEM8()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk8);
      q.setOrderByClause("ENTRY_CODE,LU_DESCRIPTION");//Bug Id 72250
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk8);

        if ( itemset8.countRows() == 0 )
        {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSETNODATAFOUND: No data found"));
         itemset8.clear();
        }
   }


   public void  countFindITEM8()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk8);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk8);
      itemlay8.setCountValue(toInt(itemset8.getValue("N")));
      itemset8.clear();
   }

//=============================================================================
// ITEM9

   public void  okFindITEM9()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk9);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk9);

       if ( itemset9.countRows() == 0 )
       {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSETNODATAFOUND: No data found"));
         itemset9.clear();
       }
   }


   public void  countFindITEM9()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk9);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk9);
      itemlay9.setCountValue(toInt(itemset9.getValue("N")));
      itemset9.clear();
   }


   public void  newRowITEM9()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM9","DOC_REFERENCE_CATEGORY_API.New__",itemblk9);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM9/DATA");
      itemset9.addRow(data);
   }

//=============================================================================
// Tab activate functions
//=============================================================================

   public void  activateDocMilTempl()
   {
      tabs.setActiveTab(1);
      okFindITEM0();  
   }


   public void  activateFormats()
   {
     tabs.setActiveTab(2);
     okFindITEM2();  
   }


   public void  activateReasonForIss()
   {
       tabs.setActiveTab(3);
       okFindITEM3();  
   }


   public void  activateDocReq()
   {
       tabs.setActiveTab(4);
       okFindITEM4();
   }


   public void  activateMedia()
   {
       tabs.setActiveTab(5);
       okFindITEM6();
   }


   public void  activateHistorySet()
   {
       tabs.setActiveTab(6);
       okFindITEM7();
   }


   public void  activateDocDefVal()
   {
       tabs.setActiveTab(7);
       okFindITEM8();  
   }


   public void  activateAssocCat()
   {
       tabs.setActiveTab(8);
       okFindITEM9();  
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableConfiguration();

      headblk = mgr.newASPBlock("HEAD");

      headbar = mgr.newASPCommandBar(headblk);

      headbar.disableCommand(headbar.FIND);
      headbar.disableCommand(headbar.BACK);
      headbar.disableCommand(headbar.FORWARD);
      headbar.disableCommand(headbar.BACKWARD);
      headbar.disableCommand(headbar.NEWROW);


      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);


   // tab commands
      headbar.addCustomCommand("activateFormats", mgr.translate("DOCMAWDOCUMENTBASICVARSETFORMATS: Formats"));
      headbar.addCustomCommand("activateMedia", mgr.translate("DOCMAWDOCUMENTBASICVARSETMEDIA: Media"));
      headbar.addCustomCommand("activateAssocCat", mgr.translate("DOCMAWDOCUMENTBASICVARSETASSOCCAT: Assoc. Cat."));
      headbar.addCustomCommand("activateDocMilTempl", mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCMILTEMPLATE: Milestone Template"));
      headbar.addCustomCommand("activateDocReq", mgr.translate("DOCMAWDOCUMENTBASICVARSETREQUIR: Requirement"));//2
      headbar.addCustomCommand("activateDocDefVal", mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCDEFVAL: Def. Values"));
      headbar.addCustomCommand("activateReasonForIss", mgr.translate("DOCMAWDOCUMENTBASICVARSETREASONFORISS: Reason for Issue"));
      headbar.addCustomCommand("activateHistorySet", mgr.translate("DOCMAWDOCUMENTBASICVARSETHISTORYSET: Hist. Settings"));


   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 0 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk0 = mgr.newASPBlock("ITEM0");

      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk0.addField("ITEM0_MILESTONE_PROFILE");
      f.setDbName("MILESTONE_PROFILE");
      f.setSize(15);
      f.setMaxLength(10);
      f.setMandatory();
      f.setInsertable();
      f.setUpperCase();
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETMILEPROF: Milestone Templ:");

      f = itemblk0.addField("ITEM0_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(50);
      f.setMaxLength(200);
      f.setInsertable();
      f.setMandatory();//Bug Id 73601
      f.setLabel("DOCMAWDOCUMENTBASICVARSETTEMPLDESC: Template Description");

      itemblk0.setView("DOC_MILESTONE_PROFILE");
      itemblk0.defineCommand("DOC_MILESTONE_PROFILE_API","New__,Modify__,Remove__");

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.disableCommand(itembar0.DUPLICATEROW);
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCMILTEMPLATE: Milestone Template"));

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(1);
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 1 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk1 = mgr.newASPBlock("ITEM1");

      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk1.addField("ITEM1_MILESTONE_PROFILE");
      f.setDbName("MILESTONE_PROFILE");
      f.setSize(10);
      f.setHidden();
      f.setMandatory();
      f.setInsertable();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETMILEPROF: Milestone Templ:");

      f = itemblk1.addField("ITEM1_MILESTONE_NO");
      f.setDbName("MILESTONE_NO");
      f.setSize(12);
      f.setMaxLength(10);
      f.setReadOnly();
      f.setMandatory();
      f.setInsertable();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETMILESTONENO: Milestone No.");

      f = itemblk1.addField("ITEM1_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(50);
      f.setMaxLength(200);
      f.setInsertable();
      f.setMandatory();//Bug Id 73601
      f.setLabel("DOCMAWDOCUMENTBASICVARSETDESC: Description");

      f = itemblk1.addField("ITEM1_PROGRESS");
      f.setDbName("PROGRESS");
      f.setInsertable();
      f.setSize(50);
      f.setHidden();

      f = itemblk1.addField("ITEM1_PROGRESS_TEMP", "Number", "0.00");
      f.setSize(50);
      f.setMaxLength(200);
      f.setFunction("(nvl(PROGRESS,0)*100)");
      f.setCustomValidation("ITEM1_PROGRESS_TEMP", "ITEM1_PROGRESS");
      f.setMandatory();//Bug Id 73601
      f.setLabel("DOCMAWDOCUMENTBASICVARSETPROGRESS: Cumulative Progress %");

      f = itemblk1.addField("ITEM1_DURATION");
      f.setDbName("DURATION");
      f.setInsertable();
      f.setSize(50);
      f.setMandatory();//Bug Id 73601
      f.setHidden();

      f = itemblk1.addField("ITEM1_DURATION_TEMP", "Number", "0.00");
      f.setSize(50);
      f.setMaxLength(200);
      f.setFunction("(nvl(DURATION,0)*100)");
      f.setCustomValidation("ITEM1_DURATION_TEMP", "ITEM1_DURATION");
      f.setMandatory();//Bug Id 73601
      f.setLabel("DOCMAWDOCUMENTBASICVARSETDURATION: Cumulative Duration %");

      f = itemblk1.addField("ITEM1_RELATIVE_PROGRESS");
      f.setDbName("RELATIVE_PROGRESS");
      f.setInsertable();
      f.setSize(50);
      f.setReadOnly();
        f.setHidden();

      f = itemblk1.addField("ITEM1_RELATIVE_PROGRESS_TEMP", "Number", "0.00");
      f.setSize(50);
      f.setReadOnly();
      f.setMaxLength(200);
      f.setReadOnly();
      f.setFunction("(nvl(RELATIVE_PROGRESS,0)*100)");
      f.setLabel("DOCMAWDOCUMENTBASICVARSETRELATIVEPROGRESS: Relative Progress %");

      itemblk1.setView("DOC_MILESTONE");
      itemblk1.defineCommand("DOC_MILESTONE_API","New__,Modify__,Remove__");
      itemblk1.setMasterBlock(itemblk0);

      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnITEM1");
      itembar1.disableCommand(itembar1.DUPLICATEROW);

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCMILTEMPLDETAIL: Milestone Template Detail"));

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDialogColumns(2);
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);


   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 2 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk2 = mgr.newASPBlock("ITEM2");

      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk2.addField("ITEM2_FORMAT_SIZE");
      f.setDbName("FORMAT_SIZE");
      f.setSize(6);
      f.setReadOnly();
      f.setMandatory();
      f.setInsertable();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETFORMAT: Format");

      f = itemblk2.addField("ITEM2_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(50);
      f.setMaxLength(250);
      f.setInsertable();
      f.setMandatory();//Bug Id 73601
      f.setLabel("DOCMAWDOCUMENTBASICVARSETFORMATDESC: Format Description");

        itemblk2.setView("DOC_FORMAT");
        itemblk2.defineCommand("DOC_FORMAT_API","New__,Modify__,Remove__");

        itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
        itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.disableCommand(itembar2.DUPLICATEROW);

        itemtbl2 = mgr.newASPTable(itemblk2);
        itemtbl2.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCFORMAT: Formats"));

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(1);
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);

   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 3 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk3 = mgr.newASPBlock("ITEM3");

      f = itemblk3.addField("ITEM3_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk3.addField("ITEM3_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk3.addField("ITEM3_REASON_FOR_ISSUE");
      f.setDbName("REASON_FOR_ISSUE");
      f.setSize(20);
      f.setUpperCase();
      f.setReadOnly();
      f.setUpperCase();
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETREASON: Reason");

      f = itemblk3.addField("ITEM3_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(50);
      f.setMaxLength(2000);
      f.setInsertable();
      f.setMandatory();//Bug Id 73601
      f.setLabel("DOCMAWDOCUMENTBASICVARSETREASONDESC: Description");

      itemblk3.setView("DOCUMENT_REASON_FOR_ISSUE");
      itemblk3.defineCommand("DOCUMENT_REASON_FOR_ISSUE_API","New__,Modify__,Remove__");

      itemset3 = itemblk3.getASPRowSet();

      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.disableCommand(itembar3.DUPLICATEROW);

      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETREASONFORISS: Reason for Issue"));

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDialogColumns(1);
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);

   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 4 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk4 = mgr.newASPBlock("ITEM4");

      f = itemblk4.addField("ITEM4_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk4.addField("ITEM4_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk4.addField("ITEM4_DOC_REQ_PROFILE");
      f.setDbName("DOC_REQ_PROFILE");
      f.setSize(10);
      f.setReadOnly();
      f.setMandatory();
      f.setInsertable();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETDOCREQID: Doc Requirement Id");

      f = itemblk4.addField("ITEM4_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(35);
      f.setMaxLength(35);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETDOCREQDESC: Description");

      f = itemblk4.addField("ITEM4_INFORMATION");
      f.setDbName("INFORMATION");
      f.setSize(50);
      f.setMaxLength(2000);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETNOTE: Note:");

        itemblk4.setView("DOC_REQUIREMENT_PROFILE");
        itemblk4.defineCommand("DOC_REQUIREMENT_PROFILE_API","New__,Modify__,Remove__");

        itemset4 = itemblk4.getASPRowSet();

      itembar4 = mgr.newASPCommandBar(itemblk4);
        itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
        itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
        itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
      itembar4.disableCommand(itembar4.DUPLICATEROW);

        itemtbl4 = mgr.newASPTable(itemblk4);
        itemtbl4.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCREQ: Document Requirements"));//5

      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDialogColumns(2);
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);

   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 5 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk5 = mgr.newASPBlock("ITEM5");

      f = itemblk5.addField("ITEM5_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk5.addField("ITEM5_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk5.addField("ITEM5_DOC_REQ_PROFILE");
      f.setDbName("DOC_REQ_PROFILE");
      f.setSize(10);
      f.setMandatory();
      f.setInsertable();
      f.setHidden();

      f = itemblk5.addField("ITEM5_DOC_CLASS");
      f.setDbName("DOC_CLASS");
      f.setSize(13);
      f.setMaxLength(12);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setUpperCase();
      f.setDynamicLOV("DOC_CLASS");
      f.setCustomValidation("ITEM5_DOC_CLASS", "ITEM5_CLASS_NAME");
      f.setLabel("DOCMAWDOCUMENTBASICVARSETDOCCLASS: Document Class");

      f = itemblk5.addField("ITEM5_CLASS_NAME");
      f.setSize(25);
      f.setMaxLength(24);
      f.setReadOnly();
      f.setFunction("Doc_Class_API.Get_Name(:ITEM5_DOC_CLASS)");
      f.setLabel("DOCMAWDOCUMENTBASICVARSETCLASSNAME: Document Class Description");

      f = itemblk5.addField("ITEM5_DOC_CATEGORY");
      f.setDbName("DOC_CATEGORY");
      f.setSize(19);
      f.setMaxLength(5);
      f.setInsertable();
      f.setLOV("CategoryCodeLov.page");
      f.setLabel("DOCMAWDOCUMENTBASICVARSETDOCCATEGORY: Association Category");

      f = itemblk5.addField("ITEM5_IN_OUT_DIRECTION");
      f.setDbName("IN_OUT_DIRECTION");
      f.setSize(50);
      f.setMaxLength(200);
      f.setInsertable();
      f.setSelectBox();
      f.setMandatory();
      f.enumerateValues("DOC_REQ_IN_OUT_DIRECTION_API");
      f.setLabel("DOCMAWDOCUMENTBASICVARSETINOUTDIRECTION: Document Flow");

      f = itemblk5.addField("ITEM5_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(35);
      f.setMaxLength(35);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETDOCDESCRIPTION: Description");

      itemblk5.setView("DOC_REQUIREMENT_STD_LIST");
      itemblk5.defineCommand("DOC_REQUIREMENT_STD_LIST_API","New__,Modify__,Remove__");
      itemblk5.setMasterBlock(itemblk4);

      itemset5 = itemblk5.getASPRowSet();

      itembar5 = mgr.newASPCommandBar(itemblk5);
      itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");
      itembar5.defineCommand(itembar5.NEWROW,"newRowITEM5");
      itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
      itembar5.disableCommand(itembar5.DUPLICATEROW);

      itemtbl5 = mgr.newASPTable(itemblk5);
      itemtbl5.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCREQDET: Document Requirements Details"));

      itemlay5 = itemblk5.getASPBlockLayout();
      itemlay5.setDialogColumns(2);
      itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);

   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 6 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk6 = mgr.newASPBlock("ITEM6");

      f = itemblk6.addField("ITEM6_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk6.addField("ITEM6_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk6.addField("ITEM6_MEDIUM");
      f.setDbName("MEDIUM");
      f.setSize(2);
      f.setMaxLength(2);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETMEDIUM: Media");

      f = itemblk6.addField("ITEM6_MEDIUM_NAME");
      f.setDbName("MEDIUM_NAME");
      f.setSize(24);
      f.setMaxLength(24);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETMEDIUMDESC: Media Description");

        itemblk6.setView("DOC_MEDIUM");
        itemblk6.defineCommand("DOC_MEDIUM_API","New__,Modify__,Remove__");

        itemset6 = itemblk6.getASPRowSet();

      itembar6 = mgr.newASPCommandBar(itemblk6);
        itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
        itembar6.defineCommand(itembar6.NEWROW,"newRowITEM6");
        itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
      itembar6.disableCommand(itembar6.DUPLICATEROW);

        itemtbl6 = mgr.newASPTable(itemblk6);
        itemtbl6.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCMEDIA: Media"));

      itemlay6 = itemblk6.getASPBlockLayout();
      itemlay6.setDialogColumns(1);
      itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);


   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 7 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk7 = mgr.newASPBlock("ITEM7");

      f = itemblk7.addField("ITEM7_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk7.addField("ITEM7_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk7.addField("ITEM7_INFO_CATEGORY");
      f.setDbName("INFO_CATEGORY");
      f.setSize(50);
      f.setMaxLength(200);
      f.setInsertable();
      f.setReadOnly();
      f.setSelectBox();
      f.setMandatory();
      f.enumerateValues("DOCUMENT_ISSUE_HISTORY_CAT_API");
      f.setLabel("DOCMAWDOCUMENTBASICVARSETINFOCAT: Info Category");

      f = itemblk7.addField("ITEM7_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(50);
      f.setMaxLength(100);
      f.setInsertable();
      f.setMandatory();//Bug Id 73601
      f.setLabel("DOCMAWDOCUMENTBASICVARSETCATDESC: Description");

      f = itemblk7.addField("ITEM7_ENABLED");
      f.setDbName("ENABLED");
      f.setSize(6);
      f.setMaxLength(5);
      f.setInsertable();
      f.setCheckBox("FALSE,TRUE");
      f.setLabel("DOCMAWDOCUMENTBASICVARSETENABLED: Enabled");

        itemblk7.setView("DOCUMENT_HISTORY_SETTINGS");
        itemblk7.defineCommand("DOCUMENT_HISTORY_SETTINGS_API","New__,Modify__,Remove__");

        itemset7 = itemblk7.getASPRowSet();

      itembar7 = mgr.newASPCommandBar(itemblk7);
        itembar7.defineCommand(itembar7.OKFIND,"okFindITEM7");
        itembar7.defineCommand(itembar7.NEWROW,"newRowITEM7");
        itembar7.defineCommand(itembar7.COUNTFIND,"countFindITEM7");
      itembar7.disableCommand(itembar7.DUPLICATEROW);
      itembar7.disableCommand(itembar7.DELETE);

        itemtbl7 = mgr.newASPTable(itemblk7);
        itemtbl7.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETHISTORYSETTINGS: History Settings"));

      itemlay7 = itemblk7.getASPBlockLayout();
      itemlay7.setDialogColumns(2);
      itemlay7.setDefaultLayoutMode(itemlay7.MULTIROW_LAYOUT);

   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 8 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk8 = mgr.newASPBlock("ITEM8");

      f = itemblk8.addField("ITEM8_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk8.addField("ITEM8_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk8.addField("ITEM8_ENTRY_CODE");
      f.setDbName("ENTRY_CODE");
      f.setSize(30);
      f.setMaxLength(30);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETDBCOL: DB Column");

      f = itemblk8.addField("ITEM8_LU_NAME");
      f.setDbName("LU_NAME");
      f.setSize(30);
      f.setReadOnly();
        f.setHidden();

      f = itemblk8.addField("ITEM8_OBJECT");
      f.setDbName("LU_DESCRIPTION");
      f.setSize(30);
      f.setMaxLength(30);
      f.setReadOnly();
      //f.setFunction("Language_SYS.Translate_Lu_Prompt_(:ITEM8_LU_NAME)");
      f.setLabel("DOCMAWDOCUMENTBASICVARSETOBJ: Object");

      f = itemblk8.addField("ITEM8_DEFAULT_VALUE");
      f.setDbName("DEFAULT_VALUE");
      f.setSize(30);
      f.setMaxLength(100);
      f.setLabel("DOCMAWDOCUMENTBASICVARSETDEFVAL: DB Col Default Value");

      f = itemblk8.addField("INFO");
      f.setHidden();
      f.setFunction("''");

      f = itemblk8.addField("ATTR");
      f.setHidden();
      f.setFunction("''");

      f = itemblk8.addField("ACTION");
      f.setHidden();
      f.setFunction("''");

      itemblk8.setView("DOCMAN_DEFAULT");
      itemblk8.defineCommand("DOCMAN_DEFAULT_API","Modify__"); // Bug 57779

      itemset8 = itemblk8.getASPRowSet();

      itembar8 = mgr.newASPCommandBar(itemblk8);
      itembar8.defineCommand(itembar8.OKFIND,"okFindITEM8");
      itembar8.defineCommand(itembar8.CANCELFIND,"cancelFindITEM8");
      itembar8.disableCommand(itembar8.DUPLICATEROW);
      itembar8.defineCommand(itembar8.COUNTFIND,"countFindITEM8");
      itembar8.disableCommand(itembar8.NEWROW);
      
      itemtbl8 = mgr.newASPTable(itemblk8);
      itemtbl8.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCDEFVALUES: Default Values"));

      itemlay8 = itemblk8.getASPBlockLayout();
      itemlay8.setDialogColumns(2);
      itemlay8.setDefaultLayoutMode(itemlay8.MULTIROW_LAYOUT);

   //---------------------------------------------------------------------
   //-------------- ITEM BLOCK - 9 ---------------------------------------
   //---------------------------------------------------------------------

      itemblk9 = mgr.newASPBlock("ITEM9");

      f = itemblk9.addField("ITEM9_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk9.addField("ITEM9_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk9.addField("ITEM9_CATEGORY");
      f.setDbName("CATEGORY");
      f.setSize(20);
      f.setMaxLength(1);
      f.setAlignment(f.ALIGN_CENTER);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETCATEGORY: Association Category");

      f = itemblk9.addField("ITEM9_CATEGORY_NAME");
      f.setDbName("CATEGORY_NAME");
      f.setSize(24);
      f.setMaxLength(24);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSETCATNAME: Description");

        itemblk9.setView("DOC_REFERENCE_CATEGORY");
        itemblk9.defineCommand("DOC_REFERENCE_CATEGORY_API","New__,Modify__,Remove__");

        itemset9 = itemblk9.getASPRowSet();

      itembar9 = mgr.newASPCommandBar(itemblk9);
        itembar9.defineCommand(itembar9.OKFIND,"okFindITEM9");
        itembar9.defineCommand(itembar9.NEWROW,"newRowITEM9");
        itembar9.defineCommand(itembar9.COUNTFIND,"countFindITEM9");
      itembar9.disableCommand(itembar9.DUPLICATEROW);

        itemtbl9 = mgr.newASPTable(itemblk9);
        itemtbl9.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSETASSOCCATEGORY: Association Category"));

      itemlay9 = itemblk9.getASPBlockLayout();
      itemlay9.setDialogColumns(1);
      itemlay9.setDefaultLayoutMode(itemlay9.MULTIROW_LAYOUT);


   //---------------------------------------------------------------------
   //-------------- DEFINITIONS OF TABS ----------------------------------
   //---------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();


      
      tabs.addTab("MILESTONE_TEMPL",mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCMILTEMPL: Milestone Templ."),"javascript:commandSet('HEAD.activateDocMilTempl','')"); //Bug Id 78032
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICVARSETFORMATS: Formats"),"javascript:commandSet('HEAD.activateFormats','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICVARSETREASONFORISS: Reason for Issue"),"javascript:commandSet('HEAD.activateReasonForIss','')");
      tabs.addTab("REC",mgr.translate("DOCMAWDOCUMENTBASICVARSETREQ: Req."),"javascript:commandSet('HEAD.activateDocReq','')");//6 //Bug Id 78032
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICVARSETMEDIA: Media"),"javascript:commandSet('HEAD.activateMedia','')");
      tabs.addTab("HIST_SETTINGS",mgr.translate("DOCMAWDOCUMENTBASICVARSETHISTORYSET: Hist. Settings"),"javascript:commandSet('HEAD.activateHistorySet','')");//Bug Id 78032
      tabs.addTab("DEF_VALUES",mgr.translate("DOCMAWDOCUMENTBASICVARSETDOCDEFVAL: Def. Values"),"javascript:commandSet('HEAD.activateDocDefVal','')");//Bug Id 78032
      tabs.addTab("ASSOC_CAT",mgr.translate("DOCMAWDOCUMENTBASICVARSETASSOCCAT: Assoc. Cat."),"javascript:commandSet('HEAD.activateAssocCat','')");//Bug Id 78032

      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCUMENTBASICVARSETTITLE: Basic Data - Various Settings";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCUMENTBASICVARSETTITLE: Basic Data - Various Settings";
   }

   protected void printContents() throws FndException
   {

      ASPManager mgr = getASPManager();
       appendToHTML(tabs.showTabsInit());

     if  ( tabs.getActiveTab()==1 ) {
      appendToHTML(itemlay0.show());
      if (itemset0.countRows()>0 && itemlay0.isSingleLayout())
         appendToHTML(itemlay1.show());
     }
     else if  ( tabs.getActiveTab()==2 ) {
      appendToHTML(itemlay2.show());
     }
     else if  ( tabs.getActiveTab()==3 ) {
      appendToHTML(itemlay3.show());
     }
     else if  ( tabs.getActiveTab()==4 ) {
      appendToHTML(itemlay4.show());
      if (itemset4.countRows()>0 && itemlay4.isSingleLayout())
         appendToHTML(itemlay5.show());
     }
     else if  ( tabs.getActiveTab()==5 ) {
      appendToHTML(itemlay6.show());
     }
     else if  ( tabs.getActiveTab()==6 ) {
      appendToHTML(itemlay7.show());
     }
     else if  ( tabs.getActiveTab()==7 ) {
      appendToHTML(itemlay8.show());
     }
     else if  ( tabs.getActiveTab()==8 ) {
      appendToHTML(itemlay9.show());
     }
     appendToHTML(tabs.showTabsFinish());

     //Bug Id 79146, start
     //-----------------------------------------------------------------------------
     //----------------------------  CLIENT FUNCTIONS  -----------------------------
     //-----------------------------------------------------------------------------

     appendDirtyJavaScript("function validateItem8DefaultValue(i)\n");
     appendDirtyJavaScript("{\n");
     appendDirtyJavaScript(" if ((document.form.ITEM8_ENTRY_CODE.value == 'ALLOW_INS_OBSOLETE_CHILD_DOC') || (document.form.ITEM8_ENTRY_CODE.value == 'KEEP_LAST_DOC_REV') || (document.form.ITEM8_ENTRY_CODE.value == 'MAKE_WASTE_REQ') ||          \n");
     appendDirtyJavaScript("     (document.form.ITEM8_ENTRY_CODE.value == 'NUMBER_GENERATOR') || (document.form.ITEM8_ENTRY_CODE.value == 'OBJ_CONN_REQ') || (document.form.ITEM8_ENTRY_CODE.value == 'REP_ARCH_DEL_OBSOLETE_REVS') ||         \n");
     appendDirtyJavaScript("     (document.form.ITEM8_ENTRY_CODE.value == 'REP_ARCH_NO_DEL_ON_PARAM_CHG') || (document.form.ITEM8_ENTRY_CODE.value == 'REP_ARCH_SET_PREV_OBSOLETE') || (document.form.ITEM8_ENTRY_CODE.value == 'REVISION_STYLE') ||         \n");
     appendDirtyJavaScript("     (document.form.ITEM8_ENTRY_CODE.value == 'SAFETY_COPY_REQ') || (document.form.ITEM8_ENTRY_CODE.value == 'SET_APPROVED_SEC_CHKPT') || (document.form.ITEM8_ENTRY_CODE.value == 'UPD_APPR_OR_REL_ALLOWED') ||          \n");
     appendDirtyJavaScript("     (document.form.ITEM8_ENTRY_CODE.value == 'VIEW_FILE_REQ') || (document.form.ITEM8_ENTRY_CODE.value == 'NUMBER_COUNTER') || (document.form.ITEM8_ENTRY_CODE.value == 'DOC_CLASS') ||          \n");
     appendDirtyJavaScript("     (document.form.ITEM8_ENTRY_CODE.value == 'STRUCTURE') || (document.form.ITEM8_ENTRY_CODE.value == 'ACCESS_TYPE') || (document.form.ITEM8_ENTRY_CODE.value == 'ALLOW_UPD_COMMENT_REL_DOC') || \n");
     appendDirtyJavaScript("	 (document.form.ITEM8_ENTRY_CODE.value == 'UPDATE_DURING_APPROVAL') || (document.form.ITEM8_ENTRY_CODE.value == 'TRANSMITTAL_ACK_CLASS') || (document.form.ITEM8_ENTRY_CODE.value == 'TRANSMITTAL_REPORT_CLASS'))         \n");
     appendDirtyJavaScript("    document.form.ITEM8_DEFAULT_VALUE.value = document.form.ITEM8_DEFAULT_VALUE.value.toUpperCase();              \n");
     appendDirtyJavaScript("}\n");

     //Bug Id 79146, end
   }

}
