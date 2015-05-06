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
*  File        : DocumentClassBasic.java
*  Modified    :
*    Bakalk    2001-08-24   Change the lable "Mediums" into "Media".
*    Bakalk    2002-11-06   Added Scale tab.
*    Bakalk    2002-11-27   Added ASPManger in newRowITEM5, okFindITEM5 and countFindITEM5.
*                           converted string into int in countFindITEM5. spelled properly in uppercase in setDynamicLOV("DOC_SCALE");
*    Nisilk    2003-01-31   Added New Tab "Macro"
*    MDAHSE    2003-02-26   Changed getDescription() and getTitle()
*    Nisilk    2003-03-27   Removed Scale tab
*    NiSilk    2003-08-01   Fixed call 95769, modified methods run() and startup().
*    Bakalk    2003-08-25   Fixed Call 95412: Removed some lovs, now lov for file_type
*                           returns vlaue for process and action fields too.
*    InoSlk    2003-08-29   Call ID 101731: Modified method clone().
*    Bakalk    2003-09-02   Removed lov for MacroId too.
*    JAPALK    2003-09-06   making the override default check box greyed out (or in any other way disabled).
*    Bakalk    2003-09-09   Set uneditable some fields in macro tab. Modified validateItem6FileType client method.
*    Bakalk    2003-09-12   Modified client function validateItem6FileType.
*    Bakalk    2003-09-16   Modified predefine.
*    DIKALK    2003-10-18   Call ID 107500: Removed call to mgr.generateHeadTag() in printContents()
*    DIKALK    2003-10-21   Made a small change in adjust.. change itemset0.getClientValue to itemset0.getValue
*                           to make sure this page compiles in Web Client 3.5.1
*    Bakalk    2003-12-17   Web Alignment (Tab) done. only simple modification happened to be done.
*    Bakalk    2003-12-17   Multirow action in Web Alignment done
*    Bakalk    2004-01-06   Field order in Web Alignment done
*    Bakalk    2004-02-09   Modified preDefine(). Extened the max of "OVERRIDE_VALUE".
*    Dikalk    2004-03-23   SP1-Merge. Bug ID 40415. Modified "Media Description" and "Format Description" into "Description".
*    SukMlk    2005-03-16   Merged Bug 49235
*    ThWilk    2006-02-08   Call 133190,Modified predefine().
*    ThWilk    2006-02-13   Fixed Call 133382, Modifird Adjust().
*    ThWilk    2006-03-03   Fixed Call 135842, Modified Adjust().
*    BAKALK    2006-07-25   Bug ID 58216, Fixed Sql Injection.
*    BAKALK    2007-05-23   Call ID 144097, Widened length of "ENTRY_CODE".
*    BAKALK    2007-05-24   Call ID 142529, Modified where-condition in each count Methods for tabs.
*    AMNILK    2007-08-10   Eliminated SQL Injection Security Vulnerability.
*    DINHLK    2007-12-06   Bug 66536, Modified method adjust().
*    DINHLK    2007-12-11   Bug 66536, Modified method adjust() disable the xx for two more entry codes which are only to be set centrally.
*    SHTHLK    2008-03-08   Bug Id 72250, Added an Order by clause to okFindITEM0()
*    SHTHLK    2008-03-19   Bug ID 67105, Modifird Adjust() to include 'DOC_ISSUE_MANDATORY_FIELDS'.
*    SHTHLK    2008-04-16   Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*    DULOLK    2008-09-17   Bug Id 70808, Modified Adjust() to include 'LIMIT_NORMAL_STATE_CHANGES'.
*    AMNALK    2009-01-08   Bug Id 79146, Added new javascript function validateOverrideValue().
*    SHTHLK    2009-09-16   Bug Id 85876, Modified saveReturnITEM0() to display the warning message.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class DocumentClassBasic extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentClassBasic");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
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
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPTransactionBuffer trans1;
   private String activetab;
   private boolean dupitem0;
   private boolean dupitem3;
   private ASPQuery q;
   private ASPCommand cmd;
   private ASPBuffer data;
   private int headrowno;
   private String doc_class;
   private int mode;
   private String itemno;
   //Bug Id 79146, start
   private String client_action;
   private String err_msg;
   //Bug Id 79146, end

   //===============================================================
   // Construction
   //===============================================================
   public DocumentClassBasic(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans      = null;
      trans1     = null;
      activetab  = null;
      dupitem0   = false;
      dupitem3   = false;
      q          = null;
      cmd        = null;
      data       = null;
      headrowno  = 0;
      doc_class  = null;
      mode       = 0;
      itemno     = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocumentClassBasic page = (DocumentClassBasic)(super.clone(obj));

      // Initializing mutable attributes
      page.trans       = null;
      page.trans1      = null;
      page.activetab   = null;
      page.dupitem0    = false;
      page.dupitem3    = false;
      page.q           = null;
      page.cmd         = null;
      page.data        = null;
      page.headrowno   = 0;
      page.doc_class   = null;

      // Cloning immutable attributes
      page.frm      = page.getASPForm();
      page.fmt      = page.getASPHTMLFormatter();
      page.ctx      = page.getASPContext();
      page.headblk  = page.getASPBlock(headblk.getName());
      page.headset  = page.headblk.getASPRowSet();
      page.headbar  = page.headblk.getASPCommandBar();
      page.headtbl  = page.getASPTable(headtbl.getName());
      page.headlay  = page.headblk.getASPBlockLayout();
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

      page.tabs     = page.getASPTabContainer();
      page.f        = page.getASPField(f.getName());

      page.mode     = 0;
      page.itemno   = null;

      return page;
   }

   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();


      frm      = mgr.getASPForm();
      fmt      = mgr.newASPHTMLFormatter();
      ctx      = mgr.getASPContext();
      trans    = mgr.newASPTransactionBuffer();
      trans1   = mgr.newASPTransactionBuffer();
      mode     = (int)ctx.readNumber("MODE",0);
      dupitem0 = false;
      dupitem3 = false;


      if(mgr.commandBarActivated())
      {
         if  ( "ITEM0.DuplicateRow".equals(mgr.readValue("__COMMAND")) )
            dupitem0=true;
         if  ( "ITEM3.DuplicateRow".equals(mgr.readValue("__COMMAND")) )
            dupitem3=true;
         eval(mgr.commandBarFunction());
      }
      else if( mgr.commandLinkActivated() )
         eval(mgr.commandLinkFunction());		//EVALInjections_Safe AMNILK 20070810
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      //Bug Id 79146, start
      else if ( "FALSE".equals(mgr.readValue("ITEM0_ERROR")) )
      {
      	 itemlay0.setLayoutMode(itemlay0.EDIT_LAYOUT);
      	 itemset0.goTo((int)ctx.readNumber("CURRENT_ROW", 0));
      }
      //Bug Id 79146, end
      else
         okFind();    

      adjust();

      tabs.saveActiveTab();
      ctx.writeNumber("MODE",mode);

   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {


   }

//-----------------------------------------------------------------------------
//-------------------------  UTILITY FUNCTIONS  -------------------------------
//-----------------------------------------------------------------------------

   public void  assign()
   {

   }


//   public void  startup()
//   {
//    headlay.setLayoutMode(headlay.FIND_LAYOUT);
//   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR FUNCTIONS  ---------------------------------
//-----------------------------------------------------------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");

      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTCLASSBASICNODATA: No data found."));
      trans.clear();
   }


   public void cancelFind()
   {
      debug("calling cancel find");
      ASPManager mgr = getASPManager();
      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }

//------------------------------------------------------------------------------

   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM0","DOC_CLASS_DEFAULT_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_DOC_CLASS",headset.getRow().getFieldValue("DOC_CLASS"));
      if (dupitem0)
      {
         data.setFieldItem("ITEM0_OBJECT",itemset0.getRow().getValue("ITEM0_OBJECT"));
      }
      itemset0.addRow(data);
   }


   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      if(headset.countRows()>0)
      {
         q = trans.addQuery(itemblk0);
         //bug 58216 starts
         q.addWhereCondition("DOC_CLASS = ?");
         q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         //bug 58216 end
	 q.setOrderByClause ("ENTRY_CODE,LU_NAME");//Bug Id 72250

         q.includeMeta("ALL");
         headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
      else
         itemset0.clear();
      trans.clear();
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
      itemset0.clear();
   }

//-------------------------------------------------------------------------------

   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      cmd   = trans.addEmptyCommand("ITEM1","DOC_CLASS_FORMAT_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);

      data  = trans.getBuffer("ITEM1/DATA");
      data.setFieldItem("ITEM1_DOC_CLASS",headset.getRow().getFieldValue("DOC_CLASS"));
      itemset1.addRow(data);
   }


   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      if(headset.countRows()>0)
      {
         q = trans.addQuery(itemblk1);
         //bug 58216 starts
         q.addWhereCondition("DOC_CLASS = ?");
         q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         //bug 58216 end
         q.includeMeta("ALL");
         headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
      else
         itemset1.clear();
      trans.clear();
   }


   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk1);
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
      itemset1.clear();
   }

//-------------------------------------------------------------------------------

   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd=trans.addEmptyCommand("ITEM2","DOC_CLASS_MEDIUM_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans=mgr.perform(trans);

      data=trans.getBuffer("ITEM2/DATA");
      data.setFieldItem("ITEM2_DOC_CLASS",headset.getRow().getFieldValue("DOC_CLASS"));
      itemset2.addRow(data);
   }


   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      if(headset.countRows()>0)
      {
         q = trans.addQuery(itemblk2);
         //bug 58216 starts
         q.addWhereCondition("DOC_CLASS = ?");
         q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         //bug 58216 end
         q.includeMeta("ALL");
         headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
      else
         itemset2.clear();
      trans.clear();
   }


   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      doc_class = headset.getRow().getValue("DOC_CLASS");
      q         = trans.addQuery(itemblk2);
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
      itemset2.clear();
   }

//-------------------------------------------------------------------------------

   public void  newRowITEM3()
   {
      ASPManager mgr = getASPManager();

      cmd   = trans.addEmptyCommand("ITEM3","DOC_CLASS_HISTORY_SETTINGS_API.New__",itemblk3);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);

      data  = trans.getBuffer("ITEM3/DATA");
      data.setFieldItem("ITEM3_DOC_CLASS",headset.getRow().getFieldValue("DOC_CLASS"));
      if (dupitem3)
      {
         data.setFieldItem("ITEM3_DESCRIPTION",itemset3.getRow().getValue("ITEM3_DESCRIPTION"));
      }
      itemset3.addRow(data);
   }


   public void  okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      if(headset.countRows()>0)
      {
         q = trans.addQuery(itemblk3);
         //bug 58216 starts
         q.addWhereCondition("DOC_CLASS = ?");
         q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         //bug 58216 end
         q.includeMeta("ALL");
         headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
      else
         itemset3.clear();
      trans.clear();
   }


   public void  countFindITEM3()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk3);
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
      itemset3.clear();
   }

//-------------------------------------------------------------------------------

   public void  newRowITEM4()
   {
      ASPManager mgr = getASPManager();

      cmd   = trans.addEmptyCommand("ITEM4","DOC_CLASS_REASON_FOR_ISSUE_API.New__",itemblk4);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM4/DATA");
      data.setFieldItem("ITEM4_DOC_CLASS",headset.getRow().getFieldValue("DOC_CLASS"));
      itemset4.addRow(data);
   }


   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      if(headset.countRows()>0)
      {
         q = trans.addQuery(itemblk4);
         //bug 58216 starts
         q.addWhereCondition("DOC_CLASS = ?");
         q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         //bug 58216 end
         q.includeMeta("ALL");
         headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
      else
         itemset4.clear();
      trans.clear();
   }


   public void  countFindITEM4()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk4);
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));
      itemset4.clear();
   }

//-------------------------------------------------------------------------------

public void newRowITEM6()
{
   ASPManager mgr = getASPManager();
   cmd   = trans.addEmptyCommand("ITEM6","DOC_CLASS_EDM_MACRO_API.New__",itemblk6);
   cmd.setOption("ACTION","PREPARE");
   trans = mgr.perform(trans);

   data  = trans.getBuffer("ITEM6/DATA");
   data.setFieldItem("ITEM6_DOC_CLASS",headset.getRow().getFieldValue("DOC_CLASS"));
   itemset6.addRow(data);
}

public void okFindITEM6()
{
   ASPManager mgr = getASPManager();

   if(headset.countRows()>0)
   {
      q = trans.addQuery(itemblk6);
      //bug 58216 starts
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      //bug 58216 end
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
   }
   else
      itemset6.clear();
   trans.clear();
}

public void countFindITEM6()
{
   ASPManager mgr = getASPManager();
   q = trans.addQuery(itemblk6);
   q.addWhereCondition("DOC_CLASS = ?");
   q.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
   q.setSelectList("to_char(count(*)) N");
   mgr.submit(trans);
   itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
   itemset6.clear();
}



//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

  public void copyAllValues(String db_method,String method_to_be_called)
  {
     ASPManager mgr = getASPManager();
     trans.clear();
     int selectedRows = 0;
     if (headlay.isMultirowLayout())
     {
        headset.storeSelections();
        headset.setFilterOn();
        selectedRows = headset.countRows();
     }
     else
     {
        headset.selectRow();
        selectedRows = 1;
     }

     for (int k=0;k<selectedRows;k++)
     {
        cmd = trans.addCustomCommand("COPYPROPERTIES"+k,db_method);
        cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
        if (headlay.isMultirowLayout())
        {
           headset.next();
        }
     }
     trans = mgr.perform(trans);
     trans.clear();
     if (headlay.isSingleLayout())
     {
        eval(method_to_be_called);
     }
     else
     {
        headset.setFilterOff();
     }

  }

   public void  copyAllDefaults()
   {
      copyAllValues("DOC_CLASS_DEFAULT_API.Copy_All_Defaults","okFindITEM0();");
     /*
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addCustomCommand("COPYDEFAULTS","DOC_CLASS_DEFAULT_API.Copy_All_Defaults");
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));

      trans = mgr.perform(trans);

      trans.clear();

      okFindITEM0();*/
   }


   public void  copyAllFormats()
   {
      copyAllValues("DOC_CLASS_FORMAT_API.Copy_All_Formats","okFindITEM1();");
      /*ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand( "COPYFORMATS", "DOC_CLASS_FORMAT_API.Copy_All_Formats");
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));

      trans = mgr.perform(trans);
      trans.clear();

      okFindITEM1();*/
   }


   public void  copyAllMediums()
   {
      copyAllValues("DOC_CLASS_MEDIUM_API.Copy_All_Mediums","okFindITEM2();");
      /*ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand( "COPYMEDIUMS", "DOC_CLASS_MEDIUM_API.Copy_All_Mediums");
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));

      trans = mgr.perform(trans);
      trans.clear();

      okFindITEM2();*/
   }


   public void  copyAllHistory()
   {
      copyAllValues("DOC_CLASS_HISTORY_SETTINGS_API.Copy_All_History","okFindITEM3();");
      /*
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand( "COPYHISTORY", "DOC_CLASS_HISTORY_SETTINGS_API.Copy_All_History");
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));

      trans = mgr.perform(trans);
      trans.clear();

      okFindITEM3();*/

   }


   public void  copyAllReasonForIssue()
   {
      copyAllValues("DOC_CLASS_REASON_FOR_ISSUE_API.Copy_All_Reason_For_Issue","okFindITEM4();");
      /*
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand( "COPYREASON", "DOC_CLASS_REASON_FOR_ISSUE_API.Copy_All_Reason_For_Issue");
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));

      trans = mgr.perform(trans);
      trans.clear();

      okFindITEM4(); */
   }


   public void  copyAllMacro()
  {
     copyAllValues("DOC_CLASS_EDM_MACRO_API.Copy_All_Macro","okFindITEM6();");
    /* ASPManager mgr = getASPManager();

     trans.clear();
     cmd = trans.addCustomCommand( "COPYMACRO", "DOC_CLASS_EDM_MACRO_API.Copy_All_Macro");
     cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));

     trans = mgr.perform(trans);
     trans.clear();

     okFindITEM6();*/
  }

   //Bug Id 79146, start

   public void  saveReturnITEM0() 
   {
      ASPManager mgr = getASPManager();
      
      int override_default = 0;
      String override_value = "";
      try
      {
	 ctx.writeNumber("CURRENT_ROW",itemset0.getCurrentRowNo());

	 if ("1".equals(mgr.readValue("OVERRIDE_DEFAULT"))) 
	    override_default = 1;

         if (mgr.readValue("OVERRIDE_VALUE") != null) 
            override_value = mgr.readValue("OVERRIDE_VALUE");

	 String attr = "OVERRIDE_DEFAULT"+(char)31 + override_default + (char)30 + "OVERRIDE_VALUE" + (char)31 + override_value +(char)30;

         trans.clear();
	 ASPCommand cmd = trans.addCustomCommand ("ITEM0", "DOC_CLASS_DEFAULT_API.Modify__");
	 cmd.addParameter("DUMMY");
	 cmd.addParameter("ITEM0_OBJID",mgr.readValue("ITEM0_OBJID"));
	 cmd.addParameter("ITEM0_OBJVERSION",mgr.readValue("ITEM0_OBJVERSION"));
	 cmd.addParameter("DUMMY",attr);
	 cmd.addParameter("DUMMY","DO");

	 trans = mgr.performEx(trans);

	 itemset0.refreshRow();
         //Bug Id 85876, Start
         String info = trans.getValue("ITEM0/DATA/DUMMY");
         if (!mgr.isEmpty(info))
         {
            mgr.showAlert(info.substring(info.indexOf((char)31))); 
         }
         //Bug Id 85876, End
      }
      catch(ASPLog.ExtendedAbortException e)
      {
	 Buffer info = e.getExtendedInfo();
	 try
         {
	    err_msg = formatErrorMsg(info.getItem("ORACLE_ERROR_MESSAGE").getString());
         }
         catch(ItemNotFoundException expItem)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCCLASSBASIC: Invalid Data found in one or more field(s)."));
         }
	 client_action = "ERROR";
	 
      }
   }

   public static String formatErrorMsg(String error_message)
   {
      if (error_message.equals(error_message.substring(0,error_message.indexOf("\n"))))
      {
         error_message = error_message.substring(0,error_message.indexOf("\r"));
         error_message = error_message.replaceAll("\n", "\\\\n"); 
         error_message = error_message.replaceAll("\r", "\\\\r"); 
         return error_message.replaceAll("\"", "\\\\\""); 
      }
      else
      {
         error_message = error_message.substring(0,error_message.indexOf("\n"));
         error_message = error_message.replaceAll("\n", "\\\\n"); 
         error_message = error_message.replaceAll("\r", "\\\\r"); 
         return error_message.replaceAll("\"", "\\\\\"");
      }
   }
   //Bug Id 79146, end


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      setVersion(3);
      getASPInfoServices().addFields();

      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("DOC_CLASS");
      f.setSize(14);
      f.setMandatory();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICDOCCLASS: Document Class");
      f.setUpperCase();
      f.setMaxLength(10);
      f.setReadOnly();
      f.setInsertable();
      f.setDynamicLOV("DOC_CLASS");

      f = headblk.addField("DOC_NAME");
      f.setSize(24);
      f.setMandatory();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICDESC: Description");
      f.setMaxLength(35);



      headblk.setView("DOC_CLASS");
      headblk.defineCommand("DOC_CLASS_API","New__,Modify__,Remove__");


      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.CANCELFIND,"cancelFind");
      headbar.addCustomCommand("activateDefvalues", "Default values");
      headbar.addCustomCommand("activateFormats", "Formats");
      headbar.addCustomCommand("activateMedia", "Media");
      headbar.addCustomCommand("activateHistorySet", "History Settings");
      headbar.addCustomCommand("activateReasonForIss", "Reason for Issue");
      headbar.addCustomCommand("scale", "Scale");
      headbar.addCustomCommand("activateMacro", "Macro");

      headbar.addSecureCustomCommand("copyAllDefaults",mgr.translate("DOCMAWDOCUMENTCLASSBASICCOPYALLDEFAULTS: Copy all Defaults"),"DOC_CLASS_DEFAULT_API.Copy_All_Defaults"); //Bug Id 70286

      headbar.addSecureCustomCommand("copyAllFormats",mgr.translate("DOCMAWDOCUMENTCLASSBASICCOPYALLFORMATS: Copy all Formats"),"DOC_CLASS_FORMAT_API.Copy_All_Formats");  //Bug Id 70286
      headbar.addSecureCustomCommand("copyAllMediums",mgr.translate("DOCMAWDOCUMENTCLASSBASICCOPYALLMEDIA: Copy all Media"),"DOC_CLASS_MEDIUM_API.Copy_All_Mediums");  //Bug Id 70286
      headbar.addSecureCustomCommand("copyAllHistory",mgr.translate("DOCMAWDOCUMENTCLASSBASICCOPYALLHISTORY: Copy all History"),"DOC_CLASS_HISTORY_SETTINGS_API.Copy_All_History");  //Bug Id 70286
      headbar.addSecureCustomCommand("copyAllReasonForIssue",mgr.translate("DOCMAWDOCUMENTCLASSBASICCOPYALLREASON: Copy all Reason For Issue"),"DOC_CLASS_REASON_FOR_ISSUE_API.Copy_All_Reason_For_Issue");  //Bug Id 70286
      headbar.addSecureCustomCommand("copyAllMacro",mgr.translate("DOCMAWDOCUMENTCLASSBASICCOPYALLMACRO: Copy all Macro"),"DOC_CLASS_EDM_MACRO_API.Copy_All_Macro");  //Bug Id 70286

      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.EDITROW);

      headbar.removeCustomCommand("activateFormats");
      headbar.removeCustomCommand("activateMedia");
      headbar.removeCustomCommand("activateDefvalues");
      headbar.removeCustomCommand("activateReasonForIss");
      headbar.removeCustomCommand("activateHistorySet");
      headbar.removeCustomCommand("scale");
      headbar.removeCustomCommand("activateMacro");

      headbar.enableMultirowAction(); //w.a.

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCUMENTCLASSBASICDOCCLASSMNG: Basic Data - Document Class Management"));
      headtbl.enableRowSelect();//w.a.

      headlay    = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);  

   //-------------------------------------------------------------------------------------------------------------
      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);

      tabs.addTab(mgr.translate("DOCMAWDOCUMENTCLASSBASICDOCDEFVAL: Document Default Values"),"javascript:commandSet('HEAD.activateDefvalues','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTCLASSBASICFORMATS: Format"),"javascript:commandSet('HEAD.activateFormats','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTCLASSBASICMEDIA: Media"),"javascript:commandSet('HEAD.activateMedia','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTCLASSBASICHISTORY: History"),"javascript:commandSet('HEAD.activateHistorySet','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTCLASSBASICREASONFORISS: Reason for Issue"),"javascript:commandSet('HEAD.activateReasonForIss','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTCLASSBASICMACRO: Macro"),"javascript:commandSet('HEAD.activateMacro','')");

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

      f = itemblk0.addField("ITEM0_DOC_CLASS");
      f.setHidden();
      f.setDbName("DOC_CLASS");

      f = itemblk0.addField("ENTRY_CODE");
      f.setSize(120);
      f.setMaxLength(120);
      f.setMandatory();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICDBCOL: DB Column");
      f.setLOV("EntryCodeLov.page",500,500);
      f.setInsertable();
      f.setReadOnly();

      f = itemblk0.addField("OVERRIDE_DEFAULT","Number");
      f.setLabel("DOCMAWDOCUMENTCLASSBASICOVERRIDEDEFAULT: Override Default");
      f.setCheckBox("0,1");
     

      f = itemblk0.addField("ITEM0_LU_NAME");
      f.setDbName("LU_NAME");
      f.setSize(30);
      f.setReadOnly();
      //f.setHidden();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICOBJ: Object");

      f = itemblk0.addField("ITEM0_OBJECT");
      f.setSize(30);
      f.setReadOnly();
      f.setFunction("Language_SYS.Translate_Lu_Prompt_(:ITEM0_LU_NAME)");
      f.setHidden();
      //f.setLabel("DOCMAWDOCUMENTCLASSBASICOBJ: Object");

      f = itemblk0.addField("OVERRIDE_VALUE");
      f.setSize(30);
      f.setMaxLength(100);
      f.setLabel("DOCMAWDOCUMENTCLASSBASICOVERRIDEVALUE: Override Value");

      f = itemblk0.addField("DUMMY");
      f.setHidden();
      f.setFunction("' '");

      itemblk0.setView("DOC_CLASS_DEFAULT");
      itemblk0.defineCommand("DOC_CLASS_DEFAULT_API","New__,Modify__,Remove__");

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0");//Bug Id 79146

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWDOCUMENTCLASSBASICDEFVAL: Default Values"));
      itemblk0.setMasterBlock(headblk);

      itembar0.disableCommand(itembar0.DELETE);
      itembar0.disableCommand(itembar0.DUPLICATEROW);
      itembar0.disableCommand(itembar0.NEWROW);

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(2);
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

      f = itemblk1.addField("ITEM1_DOC_CLASS");
      f.setHidden();
      f.setDbName("DOC_CLASS");

      f = itemblk1.addField("FORMAT_SIZE");
      f.setSize(10);
      f.setMaxLength(6);
      f.setMandatory();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICDOCFORMAT: Format");
      f.setDynamicLOV("DOC_FORMAT");

      f = itemblk1.addField("ITEM1_DESCRIPTION");
      f.setSize(50);
      f.setMaxLength(250);
      f.setLabel("DOCMAWDOCUMENTCLASSBASICFORMATNAME: Description");
      f.setFunction("DOC_FORMAT_API.Get_Description(:FORMAT_SIZE)");
      mgr.getASPField("FORMAT_SIZE").setValidation("ITEM1_DESCRIPTION");
      f.setReadOnly();

      itemblk1.setView("DOC_CLASS_FORMAT");
      itemblk1.defineCommand("DOC_CLASS_FORMAT_API","New__,Modify__,Remove__");

      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");

      itembar1.disableCommand(itembar1.EDITROW);
      itembar1.disableCommand(itembar1.DUPLICATEROW);

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("DOCMAWDOCUMENTCLASSBASICDOCFORMATS: Formats"));
      itemblk1.setMasterBlock(headblk);

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

      f = itemblk2.addField("ITEM2_DOC_CLASS");
      f.setHidden();
      f.setDbName("DOC_CLASS");

      f = itemblk2.addField("MEDIUM");
      f.setSize(8);
      f.setMaxLength(2);
      f.setMandatory();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICDOCCMEDIA: Media");
      f.setDynamicLOV("DOC_MEDIUM");

      f = itemblk2.addField("MEDIUM_NAME");
      f.setSize(24);
      f.setMaxLength(24);
      f.setLabel("DOCMAWDOCUMENTCLASSBASICMEDIADESC: Description");
      f.setFunction("DOC_CLASS_MEDIUM_API.Get_Medium_Name(:MEDIUM)");
      mgr.getASPField("MEDIUM").setValidation("MEDIUM_NAME");
      f.setReadOnly();

      itemblk2.setView("DOC_CLASS_MEDIUM");
      itemblk2.defineCommand("DOC_CLASS_MEDIUM_API","New__,Modify__,Remove__");

      itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");

      itembar2.disableCommand(itembar2.EDITROW);
      itembar2.disableCommand(itembar2.DUPLICATEROW);

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("DOCMAWDOCUMENTCLASSBASICDOCMEDIA: Media"));
      itemblk2.setMasterBlock(headblk);

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(2);
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

      f = itemblk3.addField("ITEM3_DOC_CLASS");
      f.setHidden();
      f.setDbName("DOC_CLASS");

      f = itemblk3.addField("ITEM3_INFO_CATEGORY");
      f.setDbName("INFO_CATEGORY");
      f.setSize(50);
      f.setMaxLength(200);
      f.setInsertable();
      f.setReadOnly();
      f.setMandatory();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICINFOCAT: Info Category");
      f.setDynamicLOV("DOCUMENT_HISTORY_SETTINGS");

      f = itemblk3.addField("ITEM3_DESCRIPTION");
      f.setSize(50);
      f.setMaxLength(100);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICCATDESC: Description");
      f.setFunction("DOCUMENT_HISTORY_SETTINGS_API.Get_Description(:ITEM3_INFO_CATEGORY)");
      mgr.getASPField("ITEM3_INFO_CATEGORY").setValidation("ITEM3_DESCRIPTION");

      f = itemblk3.addField("LOGG_EVENT");
      f.setSize(5);
      f.setMaxLength(5);
      f.setCheckBox("FALSE,TRUE");
      f.setLabel("DOCMAWDOCUMENTCLASSBASICENABLED: Enabled");

      itemblk3.setView("DOC_CLASS_HISTORY_SETTINGS");
      itemblk3.defineCommand("DOC_CLASS_HISTORY_SETTINGS_API","New__,Modify__,Remove__");
      itemset3 = itemblk3.getASPRowSet();

      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");

      itembar3.disableCommand(itembar3.DELETE);
      itembar3.disableCommand(itembar3.DUPLICATEROW);
      itembar3.disableCommand(itembar3.NEWROW);

      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("DOCMAWDOCUMENTCLASSBASICHISTORYSET: History Settings"));
      itemblk3.setMasterBlock(headblk);

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDialogColumns(2);
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

      f = itemblk4.addField("ITEM4_DOC_CLASS");
      f.setHidden();
      f.setDbName("DOC_CLASS");

      f = itemblk4.addField("REASON_FOR_ISSUE");
      f.setSize(30);
      f.setUpperCase();
      f.setMaxLength(20);
      f.setUpperCase();
      f.setMandatory();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICREASON: Reason");
      f.setDynamicLOV("DOCUMENT_REASON_FOR_ISSUE");

      f = itemblk4.addField("ITEM4_DESCRIPTION");
      f.setSize(50);
      f.setMaxLength(2000);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTCLASSBASICREASONDESC: Description");
      f.setFunction("DOCUMENT_REASON_FOR_ISSUE_API.Get_Description(:REASON_FOR_ISSUE)");
      mgr.getASPField("REASON_FOR_ISSUE").setValidation("ITEM4_DESCRIPTION");

      itemblk4.setView("DOC_CLASS_REASON_FOR_ISSUE");
      itemblk4.defineCommand("DOC_CLASS_REASON_FOR_ISSUE_API","New__,Modify__,Remove__");

      itemset4 = itemblk4.getASPRowSet();

      itembar4 = mgr.newASPCommandBar(itemblk4);
      itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
      itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
      itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");

      itembar4.disableCommand(itembar4.EDITROW);
      itembar4.disableCommand(itembar4.DUPLICATEROW);

      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setTitle(mgr.translate("DOCMAWDOCUMENTCLASSBASICREASONFORISS: Reason for Issue"));
      itemblk4.setMasterBlock(headblk);

      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDialogColumns(2);
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);


//-------------- ITEM BLOCK - 6 ---------------------------------------
//---------------------------------------------------------------------


   itemblk6 = mgr.newASPBlock("ITEM6");

   f = itemblk6.addField("ITEM6_OBJID");
   f.setHidden();
   f.setDbName("OBJID");

   f = itemblk6.addField("ITEM6_OBJVERSION");
   f.setHidden();
   f.setDbName("OBJVERSION");

   f = itemblk6.addField("ITEM6_DOC_CLASS");
   f.setHidden();
   f.setDbName("DOC_CLASS");

   f = itemblk6.addField("ITEM6_FILE_TYPE");
   f.setDbName("FILE_TYPE");
   f.setSize(20);
   //f.setUpperCase();
   f.setMaxLength(30);
   f.setMandatory();
   f.setLabel("FILETYPE: File Type");
   //f.setDynamicLOV("EDM_MACRO_LOV","ITEM6_PROCESS PROCESS,ITEM6_ACTION ACTION,ITEM6_MACRO_ID MACRO_ID");
   //f.setDynamicLOV("EDM_MACRO");
   f.setLOV("EdmMacroFileType.page");//,"ITEM6_PROCESS PROCESS,ITEM6_ACTION ACTION,ITEM6_MACRO_ID MACRO_ID"


   f = itemblk6.addField("ITEM6_PROCESS");
   f.setDbName("PROCESS");
   f.setSize(20);
   //f.setUpperCase();
   f.setMaxLength(20);
   f.setMandatory();
   f.setReadOnly();
   f.setLabel("PROCESS: Process");
   //f.setDynamicLOV("EDM_MACRO","ITEM6_FILE_TYPE FILE_TYPE,ITEM6_MACRO_ID MACRO_ID,ITEM6_ACTION ACTION");


   f = itemblk6.addField("ITEM6_ACTION");
   f.setDbName("ACTION");
   f.setSize(20);
   f.setMaxLength(20);
   f.setMandatory();
   f.setReadOnly();
   f.setLabel("ACTION: Action");
   //f.setLOV("EdmMacroActionLov.page","ITEM6_PROCESS PROCESS,ITEM6_FILE_TYPE FILE_TYPE,ITEM6_MACRO_ID MACRO_ID");


   f = itemblk6.addField("ITEM6_MACRO_ID");
   f.setDbName("MACRO_ID");
   f.setSize(20);
   f.setHidden();
   f.setMaxLength(10);
   f.setMandatory();
   f.setReadOnly();
   f.setLabel("MACROID: Macro Id");
   //f.setLOV("EdmMacroMacroIdLov.page","ITEM6_FILE_TYPE FILE_TYPE,ITEM6_PROCESS PROCESS,ITEM6_ACTION ACTION");

   itemblk6.setView("DOC_CLASS_EDM_MACRO");
   itemblk6.defineCommand("DOC_CLASS_EDM_MACRO_API","New__,Modify__,Remove__");
   itemset6 = itemblk6.getASPRowSet();

   itembar6 = mgr.newASPCommandBar(itemblk6);
   itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
   itembar6.defineCommand(itembar6.NEWROW,"newRowITEM6");
   itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");

   itembar6.disableCommand(itembar6.EDITROW);
   itembar6.enableCommand(itembar6.DUPLICATEROW);

   itemtbl6 = mgr.newASPTable(itemblk6);
   itemtbl6.setTitle(mgr.translate("DOCMACRO: Macro"));
   itemblk6.setMasterBlock(headblk);

   itemlay6 = itemblk6.getASPBlockLayout();
   itemlay6.setDialogColumns(2);
   itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);
   itemlay6.setFieldOrder("ITEM6_OBJID,ITEM6_OBJVERSION,ITEM6_DOC_CLASS,ITEM6_PROCESS,ITEM6_FILE_TYPE,ITEM6_ACTION,ITEM6_MACRO_ID");
}



   public void  adjust() throws FndException
   {
//      if (headset.countRows()<=0)
//      {
//        /* headbar.disableCommand("copyAllDefaults");
//         headbar.disableCommand("copyAllFormats");
//         headbar.disableCommand("copyAllMediums");
//         headbar.disableCommand("copyAllHistory");
//         headbar.disableCommand("copyAllReasonForIssue");
//         headbar.disableCommand("copyAllMacro");*/
//         headbar.disableMultirowAction();//w.a. remove action button
//         headbar.disableCommand(headbar.BACK);
//         headlay.setLayoutMode(headlay.FIND_LAYOUT);
//      }  
               
      if (itemlay0.isEditLayout())
      {
         if (itemset0.getValue("ENTRY_CODE").compareTo("BRIEFCASE_TEMPLATE") == 0             || 
             itemset0.getValue("ENTRY_CODE").compareTo("DOC_CLASS") == 0                      ||
             itemset0.getValue("ENTRY_CODE").compareTo("SYSCFG_SHARED_PATH_APPSERVER") == 0   ||
             itemset0.getValue("ENTRY_CODE").compareTo("SYSCFG_SHARED_PATH_FNDWEB") == 0      ||
             itemset0.getValue("ENTRY_CODE").compareTo("SYSCFG_SOX_LOG_EXPIRE_DAYS") == 0     ||//Bug Id: 66536 starts
             itemset0.getValue("ENTRY_CODE").compareTo("FILE_PATH_FOR_BATCH_TRANSFER") == 0   ||
             itemset0.getValue("ENTRY_CODE").compareTo("ALLOW_INS_OBSOLETE_CHILD_DOC") == 0   ||
             itemset0.getValue("ENTRY_CODE").compareTo("WEB_SITE_ADDRESS") == 0               ||
             itemset0.getValue("ENTRY_CODE").compareTo("TRANSMITTAL_ACK_CLASS") == 0          ||
             itemset0.getValue("ENTRY_CODE").compareTo("TRANSMITTAL_REPORT_CLASS") == 0       ||  //Bug Id: 66536 ends  
             itemset0.getValue("ENTRY_CODE").compareTo("LIMIT_NORMAL_STATE_CHANGES") == 0     ||  //Bug Id 70808
	     itemset0.getValue("ENTRY_CODE").compareTo("DOC_ISSUE_MANDATORY_FIELDS") == 0 ) //Bug Id 67105
         {
            getASPField("OVERRIDE_DEFAULT").setReadOnly();
            getASPField("OVERRIDE_VALUE").setReadOnly();
            
         }
         
         appendDirtyJavaScript("disableOverrideValue();\n");

      }
   }


   public void  activateDefvalues()
   {
      tabs.setActiveTab(1);
   }

   public void  activateFormats()
   {
      tabs.setActiveTab(2);
   }

   public void  activateMedia()
   {
     tabs.setActiveTab(3);
   }

   public void  activateHistorySet()
   {
      tabs.setActiveTab(4);
   }

   public void  activateReasonForIss()
   {
      tabs.setActiveTab(5);
   }

   public void  activateMacro()
   {
      tabs.setActiveTab(6);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCUMENTCLASSBASICTITLE: Basic Data - Document Class Management";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCUMENTCLASSBASICTITLE: Basic Data - Document Class Management";
   }

   protected void printContents() throws FndException
   {

      ASPManager mgr = getASPManager();

      appendToHTML("  <input type=\"hidden\" name=\"ITEM0_ERROR\" value=\"\">\n");  //Bug Id 79146

      if(headlay.isVisible())
      {
      appendToHTML(headlay.show());
      }
      if (headset.countRows()>0)
      {
      if((headlay.isSingleLayout())&&(!headlay.isFindLayout()))
      {
         appendToHTML(tabs.showTabsInit());


         if (tabs.getActiveTab()==1)
         {
            appendToHTML(itemlay0.show());
         }


         if  (tabs.getActiveTab()==2)
         {
            appendToHTML(itemlay1.show());
         }


         if  (tabs.getActiveTab()==3)
         {
            appendToHTML(itemlay2.show());
         }

         if  (tabs.getActiveTab()==4)
         {
            appendToHTML(itemlay3.show());
         }

         if  (tabs.getActiveTab()==5)
         {
            appendToHTML(itemlay4.show());
         }


         if  (tabs.getActiveTab()==6)
         {
            appendToHTML(itemlay6.show());
         }

         appendToHTML(tabs.showTabsFinish());
      }
      }

      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------

      appendDirtyJavaScript("function validateOverrideDefault(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript(" setDirty();\n");
      appendDirtyJavaScript(" if( !checkOverrideDefault(i) ) return;\n");
      appendDirtyJavaScript(" if (document.form.OVERRIDE_DEFAULT.checked)           \n");
      appendDirtyJavaScript("    document.form.OVERRIDE_VALUE.readOnly=0;              \n");
      appendDirtyJavaScript(" else  \n");
      appendDirtyJavaScript("    document.form.OVERRIDE_VALUE.readOnly=1;\n");
      appendDirtyJavaScript("}\n");

      //Bug Id 79146, start
      appendDirtyJavaScript("function validateOverrideValue(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if ((document.form.ENTRY_CODE.value == 'KEEP_LAST_DOC_REV') || (document.form.ENTRY_CODE.value == 'MAKE_WASTE_REQ') ||(document.form.ENTRY_CODE.value == 'NUMBER_GENERATOR') ||           \n");
      appendDirtyJavaScript("     (document.form.ENTRY_CODE.value == 'OBJ_CONN_REQ') || (document.form.ENTRY_CODE.value == 'REP_ARCH_DEL_OBSOLETE_REVS') || (document.form.ENTRY_CODE.value == 'SAFETY_COPY_REQ') ||         \n");
      appendDirtyJavaScript("     (document.form.ENTRY_CODE.value == 'REP_ARCH_NO_DEL_ON_PARAM_CHG') || (document.form.ENTRY_CODE.value == 'REP_ARCH_SET_PREV_OBSOLETE') || (document.form.ENTRY_CODE.value == 'REVISION_STYLE') ||         \n");
      appendDirtyJavaScript("     (document.form.ENTRY_CODE.value == 'SET_APPROVED_SEC_CHKPT') || (document.form.ENTRY_CODE.value == 'UPD_APPR_OR_REL_ALLOWED') || (document.form.ENTRY_CODE.value == 'VIEW_FILE_REQ') ||          \n");
      appendDirtyJavaScript("     (document.form.ENTRY_CODE.value == 'NUMBER_COUNTER') || (document.form.ENTRY_CODE.value == 'STRUCTURE') || (document.form.ENTRY_CODE.value == 'ACCESS_TYPE') || (document.form.ENTRY_CODE.value == 'ALLOW_UPD_COMMENT_REL_DOC') || \n");
      appendDirtyJavaScript("     (document.form.ENTRY_CODE.value == 'UPDATE_DURING_APPROVAL'))         \n");
      appendDirtyJavaScript("    document.form.OVERRIDE_VALUE.value = document.form.OVERRIDE_VALUE.value.toUpperCase();              \n");
      appendDirtyJavaScript("}\n");

      if ("ERROR".equals(client_action))
      {
	 appendDirtyJavaScript("document.form.ITEM0_ERROR.value = 'FALSE';\n");
	 appendDirtyJavaScript("window.alert(\""+err_msg+"\");\n");
	 appendDirtyJavaScript("document.form.submit();\n");
         
      }
      //Bug Id 79146, end
     
      appendDirtyJavaScript("function disableOverrideValue()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if (document.form.OVERRIDE_DEFAULT.checked)           \n");
      appendDirtyJavaScript("    document.form.OVERRIDE_VALUE.readOnly=0;              \n");
      appendDirtyJavaScript(" else  \n");
      appendDirtyJavaScript("    document.form.OVERRIDE_VALUE.readOnly=1;\n");
      appendDirtyJavaScript("}\n");



      appendDirtyJavaScript("function validateItem6FileType(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   fld = getField_('ITEM6_FILE_TYPE',i);\n");
      appendDirtyJavaScript("   str = fld.value;\n");
      appendDirtyJavaScript("   if(str.indexOf(\"^\")>0)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      valueArr = str.split(\"^\");\n");
      appendDirtyJavaScript("      fld.value = valueArr[0].toUpperCase();\n");
      appendDirtyJavaScript("      if( getRowStatus_('ITEM6',i)=='QueryMode__' )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         return;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      fld2 = getField_('ITEM6_PROCESS',i);\n");
      appendDirtyJavaScript("      fld3 = getField_('ITEM6_ACTION',i);\n");
      appendDirtyJavaScript("      fld4 = getField_('ITEM6_MACRO_ID',i);\n");
      appendDirtyJavaScript("      fld2.value = valueArr[1];\n");
      appendDirtyJavaScript("      fld3.value = valueArr[2];\n");
      appendDirtyJavaScript("      fld4.value = valueArr[3];\n");
      appendDirtyJavaScript("      setDirty();\n");
      appendDirtyJavaScript("      return checkMandatory_(fld,'File Type','');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      alert(\"");
      appendDirtyJavaScript( mgr.translate("DOCUMENTCLASSBASICUSELOV: You must use List of values!"));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");



   }

}

