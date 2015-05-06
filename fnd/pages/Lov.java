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
*  File        : Lov.java 
*  Modified    :
*    ASP2JAVA Tool  2001-03-01  - Created Using the ASP file Lov.asp
*    Kingsly P      2001-03-01  - Upgarde to WK3.5 Environment
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Lov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.Lov");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPLog log;
   private ASPContext ctx;
   private ASPTable tbl;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar bar;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer buf;
   private int skipped_rows;
   private int buf_pos;
   private int db_rows;
   private int buf_rows;
   private int skip_rows;
   private String deffile;
   private int specifiedWidth;
   private int specifiedHeight;
   private String where;
   private ASPQuery qry;
   private String n;
   private int buffer_size;

   //===============================================================
   // Construction 
   //===============================================================
   public Lov(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
      String auto = null;
   
      frm = mgr.getASPForm();
      buf = mgr.newASPTransactionBuffer();
      log = mgr.getASPLog();
      ctx = mgr.getASPContext();
   
      define();
      init();
   
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else
      {
         auto = mgr.readValue("AUTO_SEARCH");

         if (  mgr.isEmpty(auto)  ||   "Y".equals(auto) ) 
            search();
         else
            clear();
      }
   
      ctx.writeObject("LOVDEF",tbl);
      ctx.writeNumber("WIDTH",specifiedWidth);
      ctx.writeNumber("HEIGHT",specifiedHeight);
      ctx.writeValue("WHERE",where);
   
      if( !mgr.isEmpty(deffile) )
         mgr.removeTempFile(deffile);
   }


   public void  init()
   {
      int buffer_left = 0;
      int db_pos = 0;
      int page_size = 0;
      
      page_size   = tbl.getPageSize();
      buffer_size = 6*page_size;
      buffer_left = 2*page_size;
   
      skipped_rows = rowset.countSkippedDbRows();
      buf_pos      = rowset.getCurrentRowNo();
      db_pos       = skipped_rows + buf_pos;
      db_rows      = rowset.countDbRows();
      buf_rows     = rowset.countRows();
   
      log.trace("   skipped_rows = "+skipped_rows);
      log.trace("   buf_pos      = "+buf_pos     );
      log.trace("   db_pos       = "+db_pos      );
      log.trace("   db_rows      = "+db_rows     );
      log.trace("   buf_rows     = "+buf_rows    );
   
      skip_rows = 0;
   }


   public void  define()
   {
      ASPManager mgr = getASPManager();

      deffile = mgr.readValue("DEFFILE");
      log.trace("DEFFILE='"+deffile+"'");
   
      if( !mgr.isEmpty(deffile) )
      {
         log.trace("deffile="+deffile);
         tbl = mgr.newASPTable();
         tbl.loadFromFile(deffile);
         specifiedWidth  = Integer.parseInt(mgr.readValue("WIDTH"));
         specifiedHeight = Integer.parseInt(mgr.readValue("HEIGHT"));
         where = mgr.readValue("WHERE");
      }
      else
      {
         tbl = (ASPTable)ctx.readObject("LOVDEF");
         specifiedWidth  = ctx.readNumber("WIDTH",0);
         specifiedHeight = ctx.readNumber("HEIGHT",0);
         where           = ctx.readValue("WHERE");
      }
      log.trace("   specifiedWidth  = "+specifiedWidth  );
      log.trace("   specifiedHeight = "+specifiedHeight );
   
      frm.setFormWidth(specifiedWidth-45);
      frm.setFormHeight(specifiedHeight-30);
   
      blk = tbl.getBlock();
      rowset = blk.getASPRowSet();
      tbl.enableQueryRow();
      tbl.enableRowSelect();
   
      bar = mgr.newASPCommandBar(blk);
      bar.removeCommandGroup(bar.CMD_GROUP_EDIT);
      bar.removeCommandGroup(bar.CMD_GROUP_CUSTOM);
      bar.setCounterDbMode();
   }


   public void  search()
   {
      ASPManager mgr = getASPManager();

      log.trace("in search()");
      qry = buf.addQuery(blk);
      if( !mgr.isEmpty(where) )
         qry.addWhereCondition(where);
      qry.setOrderByClause(tbl.getKeyColumnName());
      qry.includeMeta("ALL");
      qry.setBufferSize(buffer_size);
      qry.skipRows(skip_rows);
   
      mgr.submit(buf);
   
      if (  rowset.countRows() == 0 ) 
         mgr.setStatusLine("FNDPAGESLOVNODATA: No data found.");
      eval(blk.regenerateAssignments());
   }


   public void  count()
   {
      ASPManager mgr = getASPManager();

      qry = buf.addQuery(blk);
      qry.setSelectList("to_char(count(*)) N");
      if( !mgr.isEmpty(where) )
         qry.addWhereCondition(where);
   
      mgr.submit(buf);
   
      n = rowset.getRow().getValue("N");
      mgr.setStatusLine("FNDPAGESLOVCOUNTRESULT: Query will retrieve &1 rows",n);
      eval(blk.regenerateAssignments());
      rowset.clear();
   }


   public void  clear()
   {
      rowset.clear();
      tbl.clearDisplayedQueryRow();
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESLOVWINDOWTITLE: IFS/Applications - List of values";
   }

   protected String getTitle()
   {
      return "FNDPAGESLOVWINDOWTITLE: IFS/Applications - List of values";
   }

   protected void printContents() throws FndException
   {
      appendToHTML(bar.showBar());
      appendToHTML(tbl.populateLov());
   }

}
