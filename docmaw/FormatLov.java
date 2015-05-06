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
*  File        : FormatLov.java 
*  Created     : 12-05-2008 DULOLK Bug Id 70553
* ----------------------------------------------------------------------------
*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class FormatLov extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.FormatLov");

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPForm frm;
   private ASPContext ctx;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar bar;
   private ASPTable tbl;
   private ASPBlockLayout lay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPQuery q;

   private boolean defined;

   private int skipped;
   private int buff_size;
   private boolean go_fwd_bwd;

   //===============================================================
   // Construction 
   //===============================================================
   public FormatLov(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      fmt   = mgr.newASPHTMLFormatter();
      frm   = mgr.getASPForm();
      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      skipped       = ctx.readNumber("SKIPPED",0);
      buff_size     = ctx.readNumber("BUFF_SIZE",0);
      go_fwd_bwd = ctx.readFlag ("COMMAND_FW_BW",false);

      if (mgr.commandBarActivated()) // fields already defined.
      {
         String command = mgr.readValue("__COMMAND");
         if( "HEAD.Backward".equals(command))
         {
            skipped = skipped - buff_size;
            go_fwd_bwd = true;
         }
         else if ( "HEAD.Forward".equals(command) )
         {
            skipped = skipped + buff_size;
            go_fwd_bwd = true;
         }        
         
         ctx.writeNumber("SKIPPED",skipped);
         
         eval(mgr.commandBarFunction());
      }
      else if (defined) {
         checkPopulate();
      }

      ctx.writeFlag("COMMAND_FW_BW",go_fwd_bwd); 
      ctx.writeNumber("BUFF_SIZE",buff_size);       
      ctx.writeNumber("SKIPPED",skipped);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ASPField f;

      disableHeader();
      disableOptions();
      disableHomeIcon();
      disableNavigate();
      disableHelp();

      blk = mgr.newASPBlock("DOC_CLASS_FORMAT");      
      
      blk.setView("DOC_CLASS_FORMAT_LOV");

      f = blk.addField( "DOC_CLASS" );
      f.setSize(10);
      f.setMaxLength(12);
      f.setHidden();

      f = blk.addField( "FORMAT_SIZE" );
      f.setLabel("DOCMAWFORMATLOVFORMATSIZE: Format");
      f.setSize(10);
      f.setMaxLength(6);
   
      f = blk.addField( "DESCRIPTION" );
      f.setLabel("DOCMAWFORMATLOVFORMATDESC: Description");
      f.setSize(40);
      f.setMaxLength(4000);

      tbl = mgr.newASPTable(blk);
      tbl.setKey("FORMAT_SIZE"); 
      tbl.disableQuickEdit();

      bar = mgr.newASPCommandBar(blk);

      rowset = blk.getASPRowSet();
      lay = blk.getASPBlockLayout();

      lay.setDialogColumns(2);
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      bar.disableCommand(bar.NEWROW);
      
      tbl.disableRowCounter();

      defined = true;
   }

   public void checkPopulate()
   {
      ASPManager mgr = getASPManager();
      String sSQLStmt;
      int count;
      int max_lov_rows;
      max_lov_rows = toInt(mgr.getConfigParameter("ADMIN/MAX_LOV_ROWS"));

      trans.clear ();
      q = trans.addQuery(blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      count = toInt(rowset.getRow().getValue("N"));
      rowset.clear();

      okFind();
   }

   public void  okFind()
    {  
      okFind(0);
      skipped=0;
    }
   
   public void  okFind(int skipRows )
   {
      ASPManager mgr = getASPManager();
      
      trans.clear();
      q = trans.addQuery(blk);
      
      q.setSelectList("FORMAT_SIZE,DESCRIPTION");
      q.setGroupByClause(tbl.getKeyColumnName());
      q.setOrderByClause(tbl.getKeyColumnName());
      q.includeMeta("ALL");
      q.skipRows(skipRows);
      
      mgr.querySubmit(trans,blk);

      if (rowset.countRows() == 0)
         mgr.showAlert(mgr.translate("DOCMAWFORMATLOVNODATA: No data found."));
   }

   protected void printContents() throws FndException
   {      
      ASPManager mgr = getASPManager();
      
      appendToHTML("  <table border=\"0\" width=\"500\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\" class=\"main\">\n");
      appendToHTML("    <tr>\n");
      appendToHTML("      <td width=\"10\"></td>\n");
      appendToHTML("      <td class=\"pageTitle\"><font class=\"pageTitle\">");
      appendToHTML(mgr.translate("DOCMAWFORMATLOVTITLE: List of Formats"));
      appendToHTML("</font></td>\n");
      appendToHTML("    </tr>\n");
      appendToHTML("  </table>\n");
      
      if (defined)
      {
         if (lay.isMultirowLayout())
         {
            if (rowset.countRows()== 0 && go_fwd_bwd)
            {
                go_fwd_bwd=false;
                okFind(skipped);
            }
            appendToHTML(bar.showBar());

            if (!lay.isFindLayout() && "true".equals(mgr.readValue("MULTICHOICE")))
            {
               appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
               appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"20\">\n");
               appendToHTML("       <td width=\"10\"></td>");  
               appendToHTML("       <td >"+fmt.drawReadLabel(mgr.translate("DOCMAWFORMATLOVSELECT: Select the Format(s) to be inserted. "))+"</td>\n");               
               appendToHTML("       <td align = right>"+fmt.drawButton("OK"," " + mgr.translate("DOCMAWFORMATLOVOK: OK" + " "),"OnClick=ok() align=left")+"</td>\n");
               appendToHTML("       <td width=\"20\"></td>");  
               appendToHTML("   </tr>\n");
               appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"20\">\n");
               appendToHTML("   </tr>\n");
               appendToHTML("   </table>\n");
               tbl.enableRowSelect();
            }

            appendToHTML(tbl.populateLov());
         }
         else
         {
            appendToHTML(lay.show());
         }
      }
      appendToHTML("  <p>");
     
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      
      appendDirtyJavaScript("function ok()\n");
      appendDirtyJavaScript("{ \n");     
      appendDirtyJavaScript("   var f = document.forms[0]; \n");
      appendDirtyJavaScript("   checkbox_array = f.__SELECTED1; \n");
      appendDirtyJavaScript("   str = \"\"; \n");
      appendDirtyJavaScript("   if(checkbox_array != null) \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      size = checkbox_array.length; \n");
      appendDirtyJavaScript("      for (i=0;i<size; i++) \n");
      appendDirtyJavaScript("      { \n");
      appendDirtyJavaScript("         if (checkbox_array[i].checked) \n");
      appendDirtyJavaScript("         { \n");      
      appendDirtyJavaScript("            str += checkbox_array[i].value + \";\"; \n");
      appendDirtyJavaScript("         } \n");
      appendDirtyJavaScript("      } \n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   str = str.substr(0,str.length-1)\n");
      appendDirtyJavaScript("   setValue(str);\n");
      appendDirtyJavaScript("}\n");    
   }
}
