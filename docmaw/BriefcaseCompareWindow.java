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
*  File        : BriefcaseCompareWindow.java
*  InoSlk   2003-05-13  Created.
*  InoSlk   2003-07-17  Implemented action 'Close' and miscellaneous corrections.
*  InoSlk   2003-07-17  Modified method 'okFind'.
*  InoSlk   2003-08-26  Call ID 101731: Modified doReset() and clone().
*  Bakalk   2003-09-22  Call Id 103650: Modification done to support Edit when the page 
*                        being loaded in a sperate window.  
*  Inoslk   2004-04-01  Bug ID 43593: Modified preDefine(),clone() and getOldRevision().
*  InoSlk   2004-04-07  Bug ID 43625: Added DOC_TITLE in preDefine() and getChangedAttributes().
*  InoSlk   2004-07-19  Bug ID 45944: Modified okFind() to handle the apostrophe.
*  Shthlk   2004-08-27  Bug Id 46667: Modified the translation constants in preDefine().
*  Shthlk   2004-09-07  Bug Id 46667: Modified preDefine() to align the description fields. 
*                       Also removed DOCMAWBCCOMPAREWINDOWUNPACKED and DOCMAWBCCOMPAREWINDOWEXISTINGDATA.
*  InoSlk   2004-11-02  Bug ID 47637: Added columns FORMAT_SIZE,SCALE & REASON_FOR_ISSUE in preDefine(), getChangedAttributes().
*  Shthlk   2004-11-11  Bug Id 46686: Modified getChangedAttributes method to add translation constants.
*  BAKALK   2006-07-17  Bug ID 58216, Fixed Sql Injection.
*  ILSOLK   20070807    Eliminated SQLInjection.
*  ILSOLK   20070808    XSS Correction.
* ----------------------------------------------------------------------------
*/     
package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;
import java.lang.reflect.*;


public class BriefcaseCompareWindow extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.BriefcaseCompareWindow");

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext ctx;
   private ASPHTMLFormatter fmt;
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
   // Bug ID 43593, inoslk, start
   private ASPBlock dummyblk;
   // Bug ID 43593, inoslk, end

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPQuery q; 

   public  String checkVal;

   private String sBriefcaseNo;
   private String sLineNo;
   private String sDocClass;
   private String sDocNo;
   private String sDocSheet;
   private String sDocRev;
   private String sOriginalRev;
   private String[][] changed_attr;

   private int changed_attr_count;

   //===============================================================
   // Construction
   //===============================================================
   public BriefcaseCompareWindow(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }   

   public ASPPoolElement clone(Object obj) throws FndException
   {
      BriefcaseCompareWindow page = (BriefcaseCompareWindow)(super.clone(obj));

      // Initialize mutable attributes
      page.trans        = null;
      page.cmd          = null;
      page.q            = null;

      page.checkVal     = null;
      page.sBriefcaseNo = null;
      page.sLineNo      = null;
      page.sDocClass    = null;
      page.sDocNo       = null;
      page.sDocSheet    = null;
      page.sDocRev      = null;
      page.sOriginalRev = null;
      page.changed_attr = null;

      page.changed_attr_count = 0;

      // Clone immutable attributes
      page.ctx     = page.getASPContext();
      page.fmt     = page.getASPHTMLFormatter();

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

      // Bug ID 43593, inoslk, start
      page.dummyblk = page.getASPBlock(dummyblk.getName());
      // Bug ID 43593, inoslk, end

      return page;
   }

   protected void doReset() throws FndException
   {
      trans        = null;
      cmd          = null;
      q            = null;

      checkVal     = null;
      sBriefcaseNo = null;
      sLineNo      = null;
      sDocClass    = null;
      sDocNo       = null;
      sDocSheet    = null;
      sDocRev      = null;
      sOriginalRev = null;
      changed_attr = null;

      changed_attr_count = 0;

      super.doReset();
   }

   public void  showDisplayMode()
   {
      ASPManager mgr = getASPManager();

      String sShowMode = mgr.readValue("CHECKMODE");

      if (mgr.isEmpty(sShowMode) || "1".equals(sShowMode))
         showAttributes();
      else if ("2".equals(sShowMode))
         showColumns();
   }

   public void showAttributes()
   {
      getChangedAttributes();
      itemlay1.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      itemlay2.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      checkVal = "1";
   }

   public void showColumns()
   {
      itemlay1.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      itemlay2.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      checkVal = "2";
   }

   
   

   public void getChangedAttributes()
   {
      ASPManager mgr = getASPManager();
      changed_attr_count = 0;

      //Bug ID 43625, inoslk, start
      if (!mgr.isEmpty(itemset1.getValue("DOC_TITLE")) || !mgr.isEmpty(itemset2.getValue("ITEM2_DOC_TITLE")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWDOCTITLE: Title"),itemset1.getValue("DOC_TITLE"),itemset2.getValue("ITEM2_DOC_TITLE")); //Bug Id 46686
      //Bug ID 43625, inoslk, end
      if (!mgr.isEmpty(itemset1.getValue("NEXT_SHEET_NUMBER")) || !mgr.isEmpty(itemset2.getValue("NEXT_DOC_SHEET")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWNEXTSHEETNUMBER: Next Sheet Number"),itemset1.getValue("NEXT_SHEET_NUMBER"),itemset2.getValue("NEXT_DOC_SHEET")); //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("BOOKING_LIST")) || !mgr.isEmpty(itemset2.getValue("ITEM2_BOOKING_LIST")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWBOOKINGLIST: Booking List"), itemset1.getValue("BOOKING_LIST"),itemset2.getValue("ITEM2_BOOKING_LIST")); //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("FILE_NAME")) || !mgr.isEmpty(itemset2.getValue("ITEM2_FILE_NAME")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWFILENAME: File Name"), itemset1.getValue("FILE_NAME"), itemset2.getValue("ITEM2_FILE_NAME"));  //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("REVISION_DATE")) || !mgr.isEmpty(itemset2.getValue("DT_DOC_REV")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWREVDATE: Revision Date"),itemset1.getValue("REVISION_DATE"),itemset2.getValue("DT_DOC_REV"));  //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("REVISION_TEXT")) || !mgr.isEmpty(itemset2.getValue("DOC_REV_TEXT")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWREVISIONTEXT: Revision Text"),itemset1.getValue("REVISION_TEXT"),itemset2.getValue("DOC_REV_TEXT"));   //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("REVISION_SIGN")) || !mgr.isEmpty(itemset2.getValue("USER_SIGN")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWREVISIONSIGN: Revision Sign"),itemset1.getValue("REVISION_SIGN"),itemset2.getValue("USER_SIGN"));  //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("OBJECT_ID")) || !mgr.isEmpty(itemset2.getValue("ITEM2_OBJECT_ID")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWREVISIONOBJECTID: Object Id"),itemset1.getValue("OBJECT_ID"),itemset2.getValue("ITEM2_OBJECT_ID"));    //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("UPDATE_REVISION")) || !mgr.isEmpty(itemset2.getValue("ITEM2_UPDATED_REVISION")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWREVISIONKEEPLASTDOCREV: Update Revision"),itemset1.getValue("UPDATE_REVISION"),itemset2.getValue("ITEM2_UPDATED_REVISION"));    //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("ASSOCIATED_CATEGORY")) || !mgr.isEmpty(itemset2.getValue("ITEM2_ASSOCIATED_CATEGORY")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWREVISIONCATEGORY: Associated Category"),itemset1.getValue("ASSOCIATED_CATEGORY"),itemset2.getValue("ITEM2_ASSOCIATED_CATEGORY"));  //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("DESCRIPTION1")) || !mgr.isEmpty(itemset2.getValue("DESCRIPTION1")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWDESCRIPTION1: Description 1"),itemset1.getValue("DESCRIPTION1"),itemset2.getValue("DESCRIPTION1")); //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("DESCRIPTION2")) || !mgr.isEmpty(itemset2.getValue("DESCRIPTION2")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWDESCRIPTION2: Description 2"),itemset1.getValue("DESCRIPTION2"),itemset2.getValue("DESCRIPTION2"));  //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("DESCRIPTION3")) || !mgr.isEmpty(itemset2.getValue("DESCRIPTION3")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWDESCRIPTION3: Description 3"),itemset1.getValue("DESCRIPTION3"),itemset2.getValue("DESCRIPTION3")); //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("DESCRIPTION4")) || !mgr.isEmpty(itemset2.getValue("DESCRIPTION4")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWDESCRIPTION4: Description 4"),itemset1.getValue("DESCRIPTION4"),itemset2.getValue("DESCRIPTION4"));  //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("DESCRIPTION5")) || !mgr.isEmpty(itemset2.getValue("DESCRIPTION5")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWDESCRIPTION5: Description 5"),itemset1.getValue("DESCRIPTION5"),itemset2.getValue("DESCRIPTION5"));  //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("DESCRIPTION6")) || !mgr.isEmpty(itemset2.getValue("DESCRIPTION6")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWDESCRIPTION6: Description 6"),itemset1.getValue("DESCRIPTION6"),itemset2.getValue("DESCRIPTION6"));  //Bug Id 46686
      // Bug ID 47637, inoslk, start
      if (!mgr.isEmpty(itemset1.getValue("FORMAT_SIZE")) || !mgr.isEmpty(itemset2.getValue("FORMAT_SIZE")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWFORMATSIZE: Format Size"),itemset1.getValue("FORMAT_SIZE"),itemset2.getValue("FORMAT_SIZE"));    //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("SCALE")) || !mgr.isEmpty(itemset2.getValue("SCALE")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWSCALE: Scale"),itemset1.getValue("SCALE"),itemset2.getValue("SCALE"));  //Bug Id 46686
      if (!mgr.isEmpty(itemset1.getValue("REASON_FOR_ISSUE")) || !mgr.isEmpty(itemset2.getValue("REASON_FOR_ISSUE")))
         checkChanges(mgr.translate("DOCMAWBCCOMPAREWINDOWREASONFORISSUE: Reason For Issue"),itemset1.getValue("REASON_FOR_ISSUE"),itemset2.getValue("REASON_FOR_ISSUE")); //Bug Id 46686
      // Bug ID 47637, inoslk, end
   }

   public void checkChanges(String sTitle, String sItem1Value, String sItem2Value)
   {
      ASPManager mgr = getASPManager();      

      if (mgr.isEmpty(sItem1Value))
      {
         changed_attr[changed_attr_count][1] = "";
         changed_attr[changed_attr_count][2] = sItem2Value;        
      }
      else if (mgr.isEmpty(sItem2Value))
      {
         changed_attr[changed_attr_count][1] = sItem1Value;
         changed_attr[changed_attr_count][2] = "";        
      }
      else
      {
         if (!sItem1Value.equals(sItem2Value))
         {
            changed_attr[changed_attr_count][1] = sItem1Value;
            changed_attr[changed_attr_count][2] = sItem2Value;            
         }
         else
            return;
      }
      changed_attr[changed_attr_count][0] = sTitle;
      changed_attr_count++;
   }

   protected void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();
      changed_attr = new String[30][3];

      checkVal = ctx.readValue("SELECTED_RADIO","1");

      if (!mgr.isEmpty(mgr.getQueryStringValue("FROM_COMPARE")))//
      {
         //ASPBuffer buff = mgr.getTransferedData();

         sBriefcaseNo = mgr.getQueryStringValue("BRIEFCASE_NO");
         sLineNo      = mgr.getQueryStringValue("LINE_NO");
         sDocClass    = mgr.getQueryStringValue("DOC_CLASS");
         sDocNo       = mgr.getQueryStringValue("DOC_NO");
         sDocSheet    = mgr.getQueryStringValue("DOC_SHEET");
         sDocRev      = mgr.getQueryStringValue("DOC_REV");
         
         // Temporary check to see if doc key is null, can be romvoed once everything is fully implemented?
         if (!mgr.isEmpty(mgr.getQueryStringValue("DOCUMENT_KEY")))
            sOriginalRev = getOldRevision(mgr.getQueryStringValue("DOCUMENT_KEY"));

         okFind();
      }
      else if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if ("TRUE".equals(mgr.readValue("MODE_CHANGED"))) // display mode changed
         showDisplayMode();
      else if (!mgr.isEmpty(mgr.readValue("HYPERLINK_COMMAND"))) // command link activated
         eval(mgr.readValue("HYPERLINK_COMMAND")); // SQLInjection_Safe ILSOLK 20070807
      adjust();

      ctx.writeValue("SELECTED_RADIO",checkVal);
   }
   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      //disableHelp();
      disableNavigate();
      
      try
      {
         //disabling some icons not supported by version 3.5.1
         eval2("disableHomeIcon",null); //null for parameter set
         eval2("disableOptions",null);
      }catch(NoSuchMethodException e){}
      
    }
   private void eval2(String method,Class[] parms) throws NoSuchMethodException
   {
       
       Method m = getClass().getMethod(method,parms);  
       eval(method);
      
   }


   // Returns the value of Old Revision for new revisions.
   public String getOldRevision(String sDocKey)
   {
      // Bug ID 43593, inoslk, start
      String sDocRev = "";
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("GETKEYREF","Client_SYS.Get_Key_Reference_Value","OUT_1");
      cmd.addParameter("DUMMY1",sDocKey);
      cmd.addParameter("DUMMY2","DOC_REV");

      trans = mgr.perform(trans);
      sDocRev = trans.getValue("GETKEYREF/DATA/OUT_1");

      return sDocRev;
      // Bug ID 43593, inoslk, end
   }

   public void okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk1);
      //bug 58216 starts
      //q.addWhereCondition("BRIEFCASE_NO='" + sBriefcaseNo + "' AND LINE_NO='" + sLineNo + "'" );
      q.addWhereCondition("BRIEFCASE_NO= ? AND LINE_NO= ?" );
      q.addParameter("BRIEFCASE_NO", sBriefcaseNo);
      q.addParameter("LINE_NO", sLineNo);
      //bug 58216 ends
      q.includeMeta("ALL");
      mgr.submit(trans);

      trans.clear();
      q = trans.addQuery(itemblk2);      
      //Bug ID 45944, inoslk, start
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS",sDocClass);
      q.addParameter("DOC_NO",sDocNo);
      q.addParameter("DOC_SHEET",sDocSheet);
      if (itemset1.getValue("DOC_BC_CHANGE_STATUS_DB").equals("New Rev"))
         q.addParameter("ITEM2_DOC_REVISION",sOriginalRev);
      else
         q.addParameter("ITEM2_DOC_REVISION",sDocRev);
      //Bug ID 45944, inoslk, end
      q.includeMeta("ALL");
      mgr.submit(trans);

      showAttributes();
   }

   //=============================================================================
   //  HTML
   //=============================================================================

   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      AutoString presentation = new AutoString();
      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCBRIEFCASECOMPAREDOCUMENT: Compare Documents"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"MODE_CHANGED\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"HYPERLINK_COMMAND\" value=\"\"> \n");
      out.append(mgr.startPresentation("DOCMAWDOCBRIEFCASECOMPAREDOCUMENTDATA: Compare Document Data"));

      // Radio Buttons
      out.append("<table border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=2 cols=2 width=\"65%\">\n");
      out.append(" <table><tr><td colspan=\"1\">\n");
      out.append(fmt.drawRadio(mgr.translate("DOCMAWDOCBRIEFCASECOMPARESHOWATTRIBUTES: Show Attributes"), "CHECKMODE", "1",checkVal.equals("1") , "OnClick=\"modeChanged()\""));
      out.append("</td><td colspan=\"1\">");
      out.append(fmt.drawRadio(mgr.translate("DOCMAWDOCBRIEFCASECOMPARESHOWCOLUMNS: Show Columns"), "CHECKMODE", "2", checkVal.equals("2"),"OnClick=\"modeChanged()\""));
      out.append("</td></tr></table>\n");
      out.append("</td></tr></table>\n");

      // Hyper Links
      out.append("<table cellspacing=2> \n");
      out.append("<tr> \n");
      out.append("<td>"); 
      out.append("<A href=\"javascript:editUnpackedData()\">");
      out.append("<font class=\"WriteTextValue\">");
      out.append( mgr.translate("DOCMAWDOCBRIEFCASECOMPAREWINEDIT: Edit Unpacked Data"));
      out.append("</font></A>");
      out.append("</td> \n");
      out.append("<td>&nbsp;</td><td>");
      out.append("<A href=\"javascript:closeCurrentWindow()\">");
      out.append("<font class=\"WriteTextValue\">");
      out.append(mgr.translate("DOCMAWDOCBRIEFCASECOMPAREWINCLOSE: Close"));
      out.append("</font></A>");
      out.append("</td> \n");
      out.append("</tr> \n </table> \n");

      if (itemlay1.getLayoutMode() == ASPBlockLayout.MULTIROW_LAYOUT && itemlay2.getLayoutMode() == ASPBlockLayout.MULTIROW_LAYOUT)
      {
         out.append(itembar1.showBar());
         out.append(itemblk1.getASPTable().populate());
         out.append(itembar2.showBar());
         out.append(itemblk2.getASPTable().populate());
      }
      else
      {
         out.append("<table border=\"2\" width=\"80%\">\n");
         out.append("<tr>\n");
         out.append("<td width=\"25%\" COLSPAN=\"2\">");
         out.append("<font class=\"WriteTextValue\"><b>");
         out.append(mgr.translate("DOCMAWDOCBCCOMPAREWINDOWEXTDATA: Existing Data"));
         out.append("</b></td>\n");
         out.append("<td width=\"25%\" COLSPAN=\"2\">");
         out.append("<font class=\"WriteTextValue\"><b>");
         out.append(mgr.translate("DOCMAWDOCBCCOMPAREWINDOWUNPCKDATA: Unpacked Data"));
         out.append("</b></td>\n");
         out.append("</tr>\n");
         int i = 0;
         while (i< changed_attr_count)
         {
            out.append("<tr >");
            out.append("<td width=\"20%\">");
            out.append("<font class=\"WriteTextValue\">");
            out.append(changed_attr[i][0]);
            out.append("</font></td>");
            out.append("<td width=\"30%\">");
            out.append("<font class=\"WriteTextValue\">");
            if (mgr.isEmpty(changed_attr[i][2]))
               out.append("&nbsp;");
            else
               out.append(changed_attr[i][2]);
            out.append("</font></td>");
            out.append("<td width=\"20%\">");
            out.append("<font class=\"WriteTextValue\">");
            out.append(changed_attr[i][0]);
            out.append("</font></td>");
            out.append("<td width=\"30%\">");
            out.append("<font class=\"WriteTextValue\">");
            if (mgr.isEmpty(changed_attr[i][1]))
               out.append("&nbsp;");
            else
               out.append(changed_attr[i][1]);
            out.append("</font></td>");
            out.append("</tr>");
            i++;
         }
         out.append("</table>\n");
      }

      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------      

      appendDirtyJavaScript("function modeChanged()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.MODE_CHANGED.value = \"TRUE\";\n");
      appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function editUnpackedData() {\n");
      appendDirtyJavaScript("   opener.editComparingRow(\""+mgr.encodeStringForJavascript(sLineNo)+"\");\n");
      appendDirtyJavaScript("   window.close();\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function closeCurrentWindow() {\n");
      //appendDirtyJavaScript("   document.form.HYPERLINK_COMMAND.value = \"close();\";\n");
      //appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("   window.close();\n");
      appendDirtyJavaScript("}\n");

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

   protected void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      disableConfiguration();

      itemblk1 = mgr.newASPBlock("ITEM1");
      itemblk1.disableDocMan();

      itemblk1.addField("BRIEFCASE_NO").setHidden();
      itemblk1.addField("LINE_NO").setHidden();
      itemblk1.addField("OBJID").setHidden();
      itemblk1.addField("OBJVERSION").setHidden();

      itemblk1.addField("DOC_CLASS").setUpperCase().setSize(12)
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCCLASS: Doc Class");

      itemblk1.addField("DOC_NO").setUpperCase().setSize(120)
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCNO: Doc No");

      itemblk1.addField("DOC_SHEET").setSize(10)
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCSHEET: Doc Sheet");

      itemblk1.addField("DOC_REVISION").setSize(6)
      .setLabel("DOCMAWBCCOMPAREWINDOWDOC_REV: Doc Rev");

      //Bug ID 43625, inoslk, start
      itemblk1.addField("DOC_TITLE").setSize(250)
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCTITLE: Title");
      //Bug ID 43625, inoslk, end

      itemblk1.addField("NEXT_SHEET_NUMBER").setSize(10)
      .setLabel("DOCMAWBCCOMPAREWINDOWNEXTSHEETNO: Next Sheet No");

      itemblk1.addField("BOOKING_LIST").setSize(20)
      .setLabel("DOCMAWBCCOMPAREWINDOWBOOKINGLIST: Booking List");

      itemblk1.addField("FILE_NAME").setSize(254)
      .setLabel("DOCMAWBCCOMPAREWINDOWFILENAME: File Name");

      itemblk1.addField("DOCUMENT_KEY").setSize(200)
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCKEY: Document Key");

      itemblk1.addField("REVISION_DATE").setLabel("DOCMAWBCCOMPAREWINDOWREVDATE: Revision Date");

      itemblk1.addField("REVISION_TEXT").setMaxLength(2000)
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONTEXT: Revision Text");  //Bug ID 46667

      itemblk1.addField("REVISION_SIGN").setSize(30)
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONSIGN: Revision Sign");

      itemblk1.addField("OBJECT_ID").setMaxLength(2000)
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONOBJECTID3: Object ID");   //Bug ID 46667

      itemblk1.addField("UPDATE_REVISION").setSize(20)
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONKEEPLASTDOCREV3: Updated Revision");  //Bug ID 46667

      itemblk1.addField("ASSOCIATED_CATEGORY").setSize(2)
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONCATEGORY: Associated Category");  //Bug ID 46667

      //Bug ID 46667, Start - Moved the description fields
      itemblk1.addField("DESCRIPTION1").setMaxLength(2000)
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION1: Description 1");  //Bug ID 46667

      itemblk1.addField("DESCRIPTION2").setMaxLength(2000)
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION2: Description 2");  //Bug ID 46667

      itemblk1.addField("DESCRIPTION3").setMaxLength(2000)
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION3: Description 3");  //Bug ID 46667

      itemblk1.addField("DESCRIPTION4").setMaxLength(2000)
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION4: Description 4");  //Bug ID 46667

      itemblk1.addField("DESCRIPTION5").setMaxLength(2000)
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION5: Description 5");   //Bug ID 46667

      itemblk1.addField("DESCRIPTION6").setMaxLength(2000)
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION6: Description 6");   //Bug ID 46667
      
      //Bug ID 46667, End

      itemblk1.addField("SIGN1").setSize(30)
      .setLabel("DOCMAWBCCOMPAREWINDOWSIGN1: Sign 1");

      itemblk1.addField("DATE1").setLabel("DOCMAWBCCOMPAREWINDOWDATE1: Date 1");

      itemblk1.addField("SIGN2").setSize(30)
      .setLabel("DOCMAWBCCOMPAREWINDOWSIGN2: Sign 2");

      itemblk1.addField("DATE2").setLabel("DOCMAWBCCOMPAREWINDOWDATE2: Date 2");

      itemblk1.addField("SIGN3").setSize(30)
      .setLabel("DOCMAWBCCOMPAREWINDOWSIGN3: Sign 3");

      itemblk1.addField("DATE3").setLabel("DOCMAWBCCOMPAREWINDOWDATE3: Date 3");

      itemblk1.addField("SIGN4").setSize(30)
      .setLabel("DOCMAWBCCOMPAREWINDOWSIGN4: Sign 4");

      itemblk1.addField("DATE4").setLabel("DOCMAWBCCOMPAREWINDOWDATE4: Date 4");

      itemblk1.addField("DOC_BC_CHANGE_STATUS_DB").setHidden();

      itemblk1.addField("DOC_BC_CHANGE_STATUS")
      .setLabel("DOCMAWBCCOMPAREWINDOWCHANGESTATUS: Change Status");

      // Bug ID 47637, inoslk, start
      itemblk1.addField("FORMAT_SIZE").setMaxLength(6)
      .setLabel("DOCMAWBCCOMPAREWINDOWFORMATSIZE: Format Size");

      itemblk1.addField("SCALE").setMaxLength(60)
      .setLabel("DOCMAWBCCOMPAREWINDOWSCALE: Scale");

      itemblk1.addField("REASON_FOR_ISSUE").setMaxLength(20)
      .setLabel("DOCMAWBCCOMPAREWINDOWREASONFORISSUE: Reason For Issue");
      // Bug ID 47637, inoslk, end

      itemblk1.setView("DOC_BRIEFCASE_UNPACKED");
      itemblk1.defineCommand("Doc_Briefcase_Unpacked_API","New__,Modify__,Remove__");
      itemblk1.setTitle(mgr.translate("DOCMAWBCCOMPAREWINDOWUNPACKEDDATA: Unpacked Document Data")); //Bug Id 46667

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.disableCommand(ASPCommandBar.NEWROW);
      itembar1.disableCommand(ASPCommandBar.DUPLICATEROW);
      itembar1.disableCommand(ASPCommandBar.EDITROW);
      itembar1.disableCommand(ASPCommandBar.DELETE);
      itembar1.disableCommand(ASPCommandBar.FIND);

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("DOCMAWBCCOMPAREWINDOWUNPACKEDDATA: Unpacked Document Data"));
      itemtbl1.enableTitleNoWrap();

      itemset1 = itemblk1.getASPRowSet();
      itemlay1 = itemblk1.getASPBlockLayout();

      // Existing document

      itemblk2 = mgr.newASPBlock("ITEM2");

      disableConfiguration();
      itemblk2.disableDocMan();

      itemblk2.addField("ITEM2_OBJID").setHidden().setDbName("OBJID");
      itemblk2.addField("ITEM2_OBJVERSION").setHidden().setDbName("OBJVERSION");

      itemblk2.addField("ITEM2_DOC_CLASS").setUpperCase().setSize(12).setDbName("DOC_CLASS")
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCCLASS2: Doc Class");

      itemblk2.addField("ITEM2_DOC_NO").setUpperCase().setSize(120).setDbName("DOC_NO")
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCNO2: Doc No");

      itemblk2.addField("ITEM2_DOC_SHEET").setSize(10).setDbName("DOC_SHEET")
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCSHEET2: Doc Sheet");

      itemblk2.addField("ITEM2_DOC_REVISION").setSize(6).setDbName("DOC_REV")
      .setLabel("DOCMAWBCCOMPAREWINDOWDOC_REV2: Doc Rev");

      //Bug ID 43625, inoslk, start
      itemblk2.addField("ITEM2_DOC_TITLE").setSize(250).setFunction("Doc_Title_API.Get_Title(:ITEM2_DOC_CLASS, :ITEM2_DOC_NO)")
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCTITLE2: Title");
      //Bug ID 43625, inoslk, end

      itemblk2.addField("ITEM2_NEXT_SHEET_NUMBER").setSize(10).setDbName("NEXT_DOC_SHEET")
      .setLabel("DOCMAWBCCOMPAREWINDOWNEXTSHEETNO2: Next Sheet No");

      itemblk2.addField("ITEM2_BOOKING_LIST").setSize(20).setFunction("''")
      .setLabel("DOCMAWBCCOMPAREWINDOWBOOKINGLIST2: Booking List");

      itemblk2.addField("ITEM2_FILE_NAME").setMaxLength(2000)
      .setFunction("Edm_File_API.Get_Doc_Files_For_Bc(:ITEM2_DOC_CLASS,:ITEM2_DOC_NO,:ITEM2_DOC_SHEET,:ITEM2_DOC_REVISION)")      
      .setLabel("DOCMAWBCCOMPAREWINDOWFILENAME2: File Name");

      //Bug ID 43593, inoslk, Changed the document key to Key Reference format.
      itemblk2.addField("ITEM2_DOCUMENT_KEY").setSize(200)
      .setFunction("Client_SYS.Get_Key_Reference('DocIssue','DOC_CLASS',:ITEM2_DOC_CLASS,'DOC_NO',:ITEM2_DOC_NO,'DOC_SHEET',:ITEM2_DOC_SHEET,'DOC_REV',:ITEM2_DOC_REVISION)")
      .setLabel("DOCMAWBCCOMPAREWINDOWDOCKEY2: Document Key");

      itemblk2.addField("ITEM2_REVISION_DATE").setDbName("DT_DOC_REV")
      .setLabel("DOCMAWBCCOMPAREWINDOWREVDATE2: Revision Date");   

      itemblk2.addField("ITEM2_REVISION_TEXT").setMaxLength(2000).setDbName("DOC_REV_TEXT")
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONTEXT2: Revision Text");  //Bug Id 46667

      itemblk2.addField("ITEM2_REVISION_SIGN").setSize(30).setDbName("USER_SIGN")
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONSIGN2: Revision Sign");  

      itemblk2.addField("ITEM2_OBJECT_ID").setMaxLength(2000)
      .setFunction("Client_SYS.Get_Item_Value('OBJECT_ID', Doc_Reference_Object_API.Get_Object_Info_For_Bc(:ITEM2_DOC_CLASS,:ITEM2_DOC_NO,:ITEM2_DOC_SHEET,:ITEM2_DOC_REVISION))")
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONOBJECTID2: Object ID");  //Bug Id 46667

      itemblk2.addField("ITEM2_UPDATED_REVISION").setSize(20)
      .setFunction("Client_SYS.Get_Item_Value('KEEP_LAST_DOC_REV', Doc_Reference_Object_API.Get_Object_Info_For_Bc(:ITEM2_DOC_CLASS,:ITEM2_DOC_NO,:ITEM2_DOC_SHEET,:ITEM2_DOC_REVISION))")
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONKEEPLASTDOCREV2: Updated Revision");  //Bug Id 46667

      itemblk2.addField("ITEM2_ASSOCIATED_CATEGORY").setSize(2)
      .setFunction("Client_SYS.Get_Item_Value('ASSOCIATED_CATEGORY', Doc_Reference_Object_API.Get_Object_Info_For_Bc(:ITEM2_DOC_CLASS,:ITEM2_DOC_NO,:ITEM2_DOC_SHEET,:ITEM2_DOC_REVISION))")
      .setLabel("DOCMAWBCCOMPAREWINDOWREVISIONCATEGORY2: Associated Category");  //Bug Id 46667    

      itemblk2.addField("ITEM2_DESCRIPTION1").setMaxLength(2000).setDbName("DESCRIPTION1")
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION1ITEM2: Description 1");  //Bug Id 46667

      itemblk2.addField("ITEM2_DESCRIPTION2").setMaxLength(2000).setDbName("DESCRIPTION2")
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION2ITEM2: Description 2");  //Bug Id 46667

      itemblk2.addField("ITEM2_DESCRIPTION3").setMaxLength(2000).setDbName("DESCRIPTION3")
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION3ITEM2: Description 3");  //Bug Id 46667

      itemblk2.addField("ITEM2_DESCRIPTION4").setMaxLength(2000).setDbName("DESCRIPTION4")
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION4ITEM2: Description 4");  //Bug Id 46667

      itemblk2.addField("ITEM2_DESCRIPTION5").setMaxLength(2000).setDbName("DESCRIPTION5")
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION5ITEM2: Description 5");  //Bug Id 46667

      itemblk2.addField("ITEM2_DESCRIPTION6").setMaxLength(2000).setDbName("DESCRIPTION6")
      .setLabel("DOCMAWBCCOMPAREWINDOWDESCRIPTION6ITEM2: Description 6");   //Bug Id 46667

      // Bug ID 47637, inoslk, start
      itemblk2.addField("ITEM2_FORMAT_SIZE").setMaxLength(6).setDbName("FORMAT_SIZE")
      .setLabel("DOCMAWBCCOMPAREWINDOWFORMATSIZEITEM2: Format Size");

      itemblk2.addField("ITEM2_SCALE").setMaxLength(60).setDbName("SCALE")
      .setLabel("DOCMAWBCCOMPAREWINDOWSCALEITEM2: Scale");

      itemblk2.addField("ITEM2_REASON_FOR_ISSUE").setMaxLength(20).setDbName("REASON_FOR_ISSUE")
      .setLabel("DOCMAWBCCOMPAREWINDOWREASONFORISSUEITEM2: Reason For Issue");
      // Bug ID 47637, inoslk, end

      itemblk2.setView("DOC_ISSUE_REFERENCE");
      itemblk2.defineCommand("Doc_Issue_API","New__,Modify__,Remove__");
      itemblk2.setTitle(mgr.translate("DOCMAWBCCOMPAREWINDOWEXISTING: Existing Document Data"));

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.enableTitleNoWrap();
      itemtbl2.setTitle(mgr.translate("DOCMAWBCCOMPAREWINDOWEXISTING: Existing Document Data"));  //Bug Id 46667

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.disableCommand(ASPCommandBar.NEWROW);
      itembar2.disableCommand(ASPCommandBar.DUPLICATEROW);
      itembar2.disableCommand(ASPCommandBar.EDITROW);
      itembar2.disableCommand(ASPCommandBar.DELETE);
      itembar2.disableCommand(ASPCommandBar.FIND);

      itemset2 = itemblk2.getASPRowSet();
      itemlay2 = itemblk2.getASPBlockLayout();

      // Bug ID 43593, inoslk, start
      dummyblk = mgr.newASPBlock("DUMMY");
      dummyblk.addField("DUMMY1");
      dummyblk.addField("DUMMY2");
      dummyblk.addField("OUT_1");
      // Bug ID 43593, inoslk, end
   }
}

