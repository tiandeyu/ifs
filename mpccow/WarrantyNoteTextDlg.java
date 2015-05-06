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
*  File        : WarrantyNoteTextDlg.java
*  Created     : Haunlk   2007/01/23
*  Description :
*  Notes       :
* ----------------------------------------------------------------------------
*  Modified    :
* ---------------------- Wings Merge Start -----------------------------------
*                ChJalk  2007-01-30  Merged Wingd Code.
* ---------------------- Wings Merge End -------------------------------------
*/

package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class WarrantyNoteTextDlg extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.WarrantyNoteTextDlg");



   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPBuffer data;

   private ASPCommand cmd;
   private ASPContext ctx;
   private ASPLog log;
   private ASPField f;

   //===============================================================
   // Transient temporary variables
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean bOkPressed = false;
   private boolean closeWindow = false;
   private boolean closeDlg = false;
   private boolean newWarrenty = false;
   private int currRow;
   private ASPQuery q;
   private int headrowno;
   private String sURL ;

   private boolean firstRequest=false;
   private String sMode;
   private String sNoteId;
   private String sLangCode;
   private String sWarrantyCode;

   //===============================================================
   // Construction
   //===============================================================
   public WarrantyNoteTextDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();
      log = mgr.getASPLog();
      trans = mgr.newASPTransactionBuffer();

      if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
      {
         validate();
      }

      if ( "OK".equals(mgr.readValue("FIRST_REQUEST")) || !mgr.isExplorer() ) //this was added to pop up the oracal error messages as there is a bug in IE
      {


         sMode = ctx.readValue("MODE");
         sNoteId = ctx.readValue("NOTENO");
         sLangCode = ctx.readValue("LANGUAGECODE");
         sWarrantyCode = ctx.readValue("WARRANTYTYPEID");

         if ( mgr.commandBarActivated() )
         {
            eval(mgr.commandBarFunction());
         }
         else if ( mgr.buttonPressed("CANCEL") )
         {
            closeDlg = true;
         }
         else if ( !mgr.isEmpty(mgr.getQueryStringValue("MODE")) )
         {
            sMode = mgr.getQueryStringValue("MODE");
            sNoteId = mgr.getQueryStringValue("NOTE_ID");
            sLangCode = mgr.getQueryStringValue("LANGUAGE_CODE");
            sWarrantyCode = mgr.getQueryStringValue("WARRANTY_TYPE_ID");
            okFind();

         }
         adjust();

         ctx.writeValue("MODE",sMode);
         ctx.writeValue("NOTENO",sNoteId);
         ctx.writeValue("LANGUAGECODE",sLangCode);
         ctx.writeValue("WARRANTYTYPEID",sWarrantyCode);
      }
      else
      {
         firstRequest = true;
      }

   }

   // --------------------------- Populating Methods ---------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();
      String txt;
      String data;
      String val = mgr.readValue("VALIDATE");
      if ( "TEMPLATE_ID".equals(val) )
      {
         cmd = trans.addCustomFunction("GETTEMPLATEDES","Cust_Warranty_Type_Temp_API.Get_Warranty_Description","DESCRIPTION");
         cmd.addParameter("TEMPLATE_ID",mgr.readValue("TEMPLATE_ID"));
         trans = mgr.validate(trans);

         String sTemplateDes      = trans.getValue("GETTEMPLATEDES/DATA/DESCRIPTION");
         txt = sTemplateDes+"^";
         mgr.responseWrite(txt);

      }
      mgr.endResponse();
   }


   //  -------------------------- Pre-Define ---------------------------------------
   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      disableNavigate();
      disableHomeIcon();

      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();


      f = headblk.addField("OUTPUT_TYPE");
      f.setLabel("MPCCOWWARRANTYNOTETEXTDLGOUTPUTTYPE: Output Type");
      f.setUpperCase();
      f.setDynamicLOV("OUTPUT_TYPE_LOV");
      f.setSize(20);

      f = headblk.addField("NOTE_TEXT");
      f.setLabel("MPCCOWWARRANTYNOTETEXTDLGNOTETEXT: Note");
      f.setSize(50);

      f = headblk.addField("NOTE_ID");
      f.setHidden();


      headblk.setView("DOCUMENT_TEXT");
      headblk.defineCommand("DOCUMENT_TEXT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headtbl = mgr.newASPTable(headblk);
      headbar = mgr.newASPCommandBar(headblk);

      headlay =  headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.defineCommand(headbar.NEWROW,"newRowHead");
      headlay.setEditable();



   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      if ( "view".equals(sMode) )
      {
         headbar.disableCommand(headbar.NEWROW);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.EDITROW);

      }

   }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("NOTE_ID = ? AND output_type != 'PURCHASE'");
      q.addParameter("NOTE_ID",sNoteId);
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addEmptyQuery(headblk);
      q.addWhereCondition("NOTE_ID = ? AND output_type != 'PURCHASE'");
      q.addParameter("NOTE_ID",sNoteId);

      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("MPCCOWWARRANTYNOTETEXTDLGDATA: No data found."));
         headset.clear();
      }
   }

   public void  newRowHead()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addEmptyCommand("HEADNEW","DOCUMENT_TEXT_API.NEW__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("NOTE_ID", sNoteId);
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEADNEW/DATA");
      headset.addRow(data);
   }

   protected String getDescription()
   {
      ASPManager mgr = getASPManager();
      return mgr.translate("MPCCOWWARRANTYNOTETEXTDLGTITLE: Document Texts for Customer warranty ID - &1 and Language Code - &2",sWarrantyCode,sLangCode);
   }

   protected String getTitle()
   {
      ASPManager mgr = getASPManager();
      return mgr.translate("MPCCOWWARRANTYNOTETEXTDLGTITLE: Document Texts for Customer warranty ID - &1 and Language Code - &2",sWarrantyCode,sLangCode);
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      ASPManager mgr = getASPManager();
      String queryString = mgr.getURL()+"?"+mgr.getQueryString();

      if ( firstRequest && mgr.isExplorer() ) ////this was added to pop up the oracal error messages as there is a bug in IE
      {
         out.append("<html>\n");
         out.append("<head></head>\n");
         out.append("<body>");
         out.append("<form name='form' method='POST' action='"+queryString+"'>");
         out.append(" <input type=\"hidden\" name=\"FIRST_REQUEST\" >");
         out.append("</form></body></html>");
         appendDirtyJavaScript("document.form.FIRST_REQUEST.value='OK';document.form.submit();");
         return out;
      }
      else
      {
         out.append("<html>\n");
         out.append("<head>\n");
         out.append(mgr.generateHeadTag(getDescription()));
         out.append("</head>\n");
         out.append("<body ");
         out.append(mgr.generateBodyTag());
         out.append(" >\n");
         out.append("<div id=\"tooltip\" class=\"tooltip\" style=\"border:1px solid black;position:absolute;visibility:hidden;\"></div>");
         out.append("<form ");
         out.append(mgr.generateFormTag());
         out.append(" >\n");
         out.append(" <input type=\"hidden\" name=\"FIRST_REQUEST\" value=\"OK\">\n");
         out.append("  <input type=\"hidden\" name=\"REFRESH\" value=\"FALSE\">\n");
         out.append(mgr.startPresentation(getTitle()));
         printContents();

         if ( closeDlg )
         {
            appendDirtyJavaScript("   window.opener.refresh();\n");
            appendDirtyJavaScript("   window.close(); \n");
         }


         out.append(mgr.endPresentation());

         out.append("</form>\n");
         out.append("</body>\n");
         out.append("</html>\n");
         return out;
      }
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.append(headlay.show());

      beginTable();
      beginTableBody();
      nextTableRow();
      beginTableCell("right");

      printSubmitButton("CANCEL",mgr.translate("MPCCOWWARRANTYNOTETEXTDLGCANCELBUTTON:    Close    "),"");
      printSpaces(8);
      endTableCell();
      endTableBody();
      endTable();
   }

}

