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
*  File        : HistWorkOrderReportPage.java 
*  Modified    :
*    ARWILK  2001-02-19  - Created.
*    GACOLK  2002-12-04  - Set Max Length of MCH_CODE to 100
*    SAPRLK  2003-12-30  - Web Alignment - removed methods clone() and doReset().
*    SAPRLK  2004-02-11  - Web Alignment - remove unnecessary method calls for hidden fields, 
*                                          removed back button functionality,removed unnecessary global variables.  
*    DIAMLK  2006-08-15  - Bug 58216, Eliminated SQL Injection security vulnerability.
*    AMNILK  2006-09-06  - Merged Bug Id: 58216.
*    AMNILK  2007-07-16  - Eliminated Secirity Vulnerability.
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class HistWorkOrderReportPage extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.HistWorkOrderReportPage");


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
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private String sWoNo;
   private String callingUrl;
   private String val;
   private ASPQuery q;
   private String n;

   //===============================================================
   // Construction 
   //===============================================================
   public HistWorkOrderReportPage(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();


      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      sWoNo = ctx.readValue("CTXWONO","2");
      callingUrl = ctx.getGlobal("CALLING_URL");


      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
         sWoNo = mgr.readValue("WO_NO");
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();
         okFindITEM0();
      }

      ctx.writeValue("CTXWONO",sWoNo);
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      //Bug 58216 start
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",sWoNo);
      //Bug 58216 end
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWHISTWORKORDERREPORTPAGENODATAFOUND: No data found."));
         headset.clear();
      }
   }


   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      //Bug 58216 start
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",sWoNo);
      //Bug 58216 end
      q.includeMeta("ALL");

      mgr.submit(trans);
   }


   public void  okFindITEM0Alert()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      //Bug 58216 start
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",sWoNo);
      //Bug 58216 end
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (itemset0.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWHISTWORKORDERREPORTPAGENODATAFOUND: No data found."));
         itemset0.clear();
      }
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      //Bug 58216 start
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",sWoNo);
      //Bug 58216 end
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      n = itemset0.getRow().getValue("N");
      itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
      itemset0.clear();
   }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      mgr.beginASPEvent();

      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("HEAD_WO_NO","Number");
      f.setDbName("WO_NO");
      f.setSize(13);
      f.setLabel("PCMWHISTWORKORDERREPORTPAGEHEADWONO: WO No");
      f.setReadOnly();
      f.setMaxLength(8);

      f = headblk.addField("MCH_CODE");
      f.setSize(13);
      f.setMaxLength(100);
      f.setLabel("PCMWHISTWORKORDERREPORTPAGEMCH_CODE: Object ID");
      f.setUpperCase();
      f.setReadOnly();

      f = headblk.addField("DESCRIPTION");
      f.setSize(28);
      f.setLabel("PCMWHISTWORKORDERREPORTPAGEDESCRIPTION: Description");
      f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)");
      f.setReadOnly();
      f.setMaxLength(2000);

      f = headblk.addField("CONTRACT");
      f.setSize(5);
      f.setLabel("PCMWHISTWORKORDERREPORTPAGECONTRACT: Site");
      f.setUpperCase();
      f.setReadOnly();

      f = headblk.addField("WO_STATUS_ID");
      f.setHidden();

      f = headblk.addField("STATUS");
      f.setSize(5);
      f.setLabel("PCMWHISTWORKORDERREPORTPAGESTATE: Status");
      f.setFunction("Active_Separate_API.Finite_State_Decode__(WO_STATUS_ID)");
      f.setReadOnly();    


      headblk.setView("HISTORICAL_SEPARATE");

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.FIND);
      headbar.disableCommand(headbar.BACK);

      headset = headblk.getASPRowSet();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("PCMWHISTWORKORDERREPORTPAGEHD: Free Notes"));
      headtbl.setWrap();

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);


      itemblk0 = mgr.newASPBlock("ITEM0");

      f = itemblk0.addField("OBJID");
      f.setHidden();

      f = itemblk0.addField("OBJVERSION");
      f.setHidden();

      f = itemblk0.addField("PAGE_NO","Number");
      f.setSize(14);
      f.setMandatory();
      f.setLabel("PCMWHISTWORKORDERREPORTPAGEPAGE_NO: Page No");
      f.setReadOnly();
      f.setInsertable();

      f = itemblk0.addField("TITLE");
      f.setSize(25);
      f.setLabel("PCMWHISTWORKORDERREPORTPAGETITLE: Title");
      f.setMaxLength(25);

      f = itemblk0.addField("NOTE");
      f.setSize(50);
      f.setLabel("PCMWHISTWORKORDERREPORTPAGENOTE: Note");
      f.setMaxLength(2000);

      f = itemblk0.addField("WO_NO","Number");
      f.setMandatory();
      f.setHidden();

      itemblk0.setView("WORK_ORDER_REPORT_PAGE");
      itemblk0.setMasterBlock(headblk);

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0Alert");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.enableCommand(itembar0.FIND);

      itembar0.disableCommand(itembar0.NEWROW);
      itembar0.disableCommand(itembar0.EDITROW);
      itembar0.disableCommand(itembar0.DUPLICATEROW);
      itembar0.disableCommand(itembar0.DELETE);      

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("PCMWHISTWORKORDERREPORTPAGEITM0: Work Order Report Page"));
      itemtbl0.setWrap();

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "PCMWHISTWORKORDERREPORTPAGEFREENOTES: Free Notes";
   }

   protected String getTitle()
   {
      return "PCMWHISTWORKORDERREPORTPAGEFREENOTES: Free Notes";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendToHTML("<table cellpadding=0 width=");
      appendToHTML(mgr.HTMLEncode(Integer.toString(frm.getFormWidth())));   //XSS_Safe AMNILK 20070716
      appendToHTML(" border=\"0\">\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>\n");
      appendToHTML(headlay.generateDataPresentation());
      appendToHTML("</td>			\n");
      appendToHTML("</tr>	\n");
      appendToHTML("</table>	\n");

      if (itemlay0.isVisible())
         appendToHTML(itemlay0.show());

      appendToHTML("<br>\n");
   }

}
