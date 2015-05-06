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
 * File        : ProjwMyProjects.java
 * Description :
 * Notes       : 
 * ----------------------------------------------------------------------------
 * Modified    :
 *    TOFJ   2000-APR-7  - Created.
 *    ToFj   2000-JUN-6  - Minor modifications.
 *    Sand   2000-Jun-22 - Modified. Call ID 44284, Tranfer to Project Info.
 *    Sand   2000-Jun-22 - Modified. Corrected minimized title to display no fo db rows.
 *    KeFe   2000-Nov-07 - Modified according to the Project Portal concept.
 *    Shamal 2000-Nov-28 - Added Customize this portlet message.
 *    KeFe   2001-Jan-15 - CID 58700 - Change the code belongs to Project Portal concept in order to compatible with 300b3.
 *    Gunasiri 2001-04-25 - Modified the code to adhere to Add/Remove Portal Page Functionality.
 *    Gunasiri 2001-05-31 - CID 65786 -Removed Conditional Link from ProjectId.
 *    Gunasiri 2001-05-31 - Project Portal Check is added.
 *    Muneera  2001-09-07 - Removed the duplicated translation constants and stndardized the non standard constants.
 *    Kamtlk   2003-11-07 - Call 110349 : modified for Next and Previous links.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.projw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;
public class ProjwMyProjects extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

//   public static boolean DEBUG = true;//Util.isDebugEnabled("ifs.portal.ProjwMyProjects");
   private final static int size = 10;


   //==========================================================================
   //  instances created on page creation (immutable attributes)
   //==========================================================================
   
   private ASPContext ctx;
   private ASPBlock   blk;
   private ASPRowSet  rowset;
   private ASPTable   tbl;


   //==========================================================================
   //  Mutable attributes
   //==========================================================================


   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================
   
   private transient ASPTransactionBuffer trans;
   private transient int      skip_rows;
   private transient int      db_rows;
   private String  order_id;
   private String  company_id;
   private String  customer_id;
   private String  where_stmt;
   private boolean qrystr;
   boolean incinit   = true;
   boolean onlymine  = false;
   boolean incapp    = true;
   boolean incstart  = true;
   boolean inccomp   = true;
   boolean incclose  = true;
   boolean inccan    = true;
   private String url;
   private transient String project_id;


   //==========================================================================
   //  Construction
   //==========================================================================

   public ProjwMyProjects( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }


   public ASPPage construct() throws FndException
   {
      return super.construct();
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected void doReset() throws FndException
   {
       url  = null;
       project_id = null;
       super.doReset();
   }   
   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      ProjwMyProjects page = (ProjwMyProjects)(super.clone(mgr));

      page.ctx    = page.getASPContext();
      page.blk    = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl    = page.getASPTable(tbl.getName());
	  
      page.project_id = null;
      incinit   = true;
      onlymine  = false;
      incapp    = true;
      incstart  = true;
      inccomp   = true;
      incclose  = true;
      inccan    = true;

      return page;
   }

   //==========================================================================
   //  
   //==========================================================================

   protected void preDefine()
   {
      ctx = getASPContext();
      
      blk = newASPBlock("MAIN");

      addField(blk, "PROJECT_ID").
      setLabel("PROJWPORTLETSPROJWMYPROJECTSPROID: Project ID");

      addField(blk, "NAME").
      setLabel("PROJWPORTLETSPROJWMYPROJECTSPRONA: Project Name");

      addField(blk, "STATE").setLabel("PROJWPORTLETSPROJWMYPROJECTSOSTATE: Status");

      addField(blk, "MANAGER").setLabel("PROJWPORTLETSPROJWMYPROJECTSMANG: Manager");


      blk.setView("PROJECT");

      tbl = newASPTable( blk );
      tbl.disableQueryRow();
      tbl.disableQuickEdit();
      tbl.disableRowCounter();
      tbl.disableRowSelect();
      tbl.disableRowStatus();
      tbl.unsetSortable();

      rowset = blk.getASPRowSet();
      getASPManager().newASPCommandBar(blk);
      init();
   }
   
   
   protected void init()// throws FndException
   {
      incinit   = readProfileFlag("INCINIT", true);
      onlymine  = readProfileFlag("ONLYMINE", true);
      incapp    = readProfileFlag("INCAPP", true);
      incstart  = readProfileFlag("INCSTART", true);
      inccomp   = readProfileFlag("INCCOMP", true);
      incclose  = readProfileFlag("INCCLOSE", true);
      inccan    = readProfileFlag("INCCAN", true);
      skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
      db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );


      String cmd = readValue("CMD");

      if( "PREV".equals(cmd) && skip_rows>=size )
      {
         skip_rows -= size;
      }
      else if( "NEXT".equals(cmd) && skip_rows<=db_rows-size )
      {
         skip_rows += size;
      }
      ctx.writeValue("SKIP_ROWS",skip_rows+"");
	  project_id = readProfileValue("PROJECT_ID", "");	
   }

   protected void run()
   {
      ASPManager mgr  = getASPManager(); 
      trans = mgr.newASPTransactionBuffer();
    	  
      ASPQuery  qry  = trans.addQuery(blk);
      qry.setOrderByClause("PROJECT_ID");
      qry.setBufferSize(size);
      qry.skipRows(skip_rows);
      qry.includeMeta("ALL");
                      	
                        where_stmt = null;
			if (incinit)
			{
				if (Str.isEmpty(where_stmt))
	   			where_stmt = " 'Initialized'";
				else
				   where_stmt = where_stmt + " ,'Initialized'";
			}
			if (incapp)
			{
				if (Str.isEmpty(where_stmt))
	   			where_stmt = " 'Approved'";
				else
				   where_stmt = where_stmt + " ,'Approved'";
			}
			if (incstart)
			{
				if (Str.isEmpty(where_stmt))
	   			where_stmt = " 'Started'";
				else
				   where_stmt = where_stmt + " ,'Started'";
			}
			if (inccomp)
			{
				if (Str.isEmpty(where_stmt))
	   			where_stmt = " 'Completed'";
				else
				   where_stmt = where_stmt + " ,'Completed'";
			}
			if (incclose)
			{
				if (Str.isEmpty(where_stmt))
	   			where_stmt = " 'Closed'";
				else
				   where_stmt = where_stmt + " ,'Closed'";
			}
			if (inccan)
			{
				if (Str.isEmpty(where_stmt))
	   			where_stmt = " 'Cancelled'";
				else
				   where_stmt = where_stmt + " ,'Cancelled'";
			}
                        if( onlymine )
		        {
		           if (Str.isEmpty(where_stmt))
                            qry.addWhereCondition("'1' = '2'");// display no projects
		           else
                            qry.addWhereCondition("PERSON_INFO_API.Get_User_Id(MANAGER) = Fnd_Session_API.Get_Fnd_User AND objstate in (" + where_stmt + ")");
		        }
                        else
			{
		          if (Str.isEmpty(where_stmt))
                           qry.addWhereCondition("'1' = '2'");// display no projects
                          else
                           qry.addWhereCondition("objstate in (" + where_stmt + ")");
                        }
       submit(trans);
      db_rows = blk.getASPRowSet().countDbRows();
      getASPContext().writeValue("DB_ROWS", db_rows+"" );
      trans.clear();
   }
   
   //==========================================================================
   //  
   //==========================================================================

   public String getTitle( int mode )
   {
      if(mode==ASPPortal.MINIMIZED)
         return translate(getDescription()) + translate("PROJWPORTLETSPROJWMYPROJECTSROJ1:  - You have &1 project(s).", Integer.toString(db_rows));
      else
         return translate(getDescription());
   }
   
   public void printContents() throws FndException
   {
      // hidden field for next and previous links
      printHiddenField("CMD","");
	  	   
	  if (rowset.countRows() == 0 || rowset == null) 
	  {
		 printNewLine();
		 appendToHTML("<a href=\"javascript:customizeBox('"+getId()+"')\">");
		 printText("PROJWPORTLETSPROJWMYPROJECTSPROJCUST: Customize this portlet");
		 appendToHTML("</a>");		 		 
		 printNewLine();		 
	  }
	  else
	     printCustomTable();

      if(size < db_rows )
      {
         printNewLine();
         printSpaces(5);
         if(skip_rows>0)
            printSubmitLink("PROJWPORTLETSPROJWMYPROJECTSPRV: Previous","prevCust");
         else
            printText("PROJWPORTLETSPROJWMYPROJECTSTPRV: Previous");

         printSpaces(5);
         String rows = translate("PROJWPORTLETSPROJWMYPROJECTSOWS: (Rows &1 to &2 of &3)",
                                 (skip_rows+1)+"",
                                 (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                 db_rows+"");
         printText( rows );
         printSpaces(5);

         if(skip_rows<db_rows-size)
            printSubmitLink("PROJWPORTLETSPROJWMYPROJECTSCTNXT: Next","nextCust");
         else
            printText("PROJWPORTLETSPROJWMYPROJECTSTNXT: Next");

         printNewLine();
         printNewLine();

         appendDirtyJavaScript(
            "function prevCust(obj,id)"+
            "{"+
            "   getPortletField(id,'CMD').value = 'PREV';"+
            "}\n"+
            "function nextCust(obj,id)"+
            "{"+
            "   getPortletField(id,'CMD').value = 'NEXT';"+
            "}\n");

      }
      else
         printNewLine();
   }

   public String getBoxStyle( int mode )
   {
      return "";
   }

   public void printCustomBody()
   {
      ASPManager           mgr   = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	  	  
      printNewLine();
		printText("PROJWPORTLETSPROJWMYPROJECTSHEAD:  Make your selections:");
      printNewLine();
		printCheckBox("ONLYMINE", onlymine);
      printText("PROJWPORTLETSPROJWMYPROJECTSMINE:   Projects that I am managing");
      printNewLine();
      printNewLine();

      printText("PROJWPORTLETSPROJWMYPROJECTSHEAD2:  Select statuses to include:");
      printNewLine();
     
      printCheckBox("INCINIT", incinit);
      printText("PROJWPORTLETSPROJWMYPROJECTSINIT:  Initialized");
      printNewLine();

      printCheckBox("INCAPP", incapp);
      printText("PROJWPORTLETSPROJWMYPROJECTSAPP:  Approved");
      printNewLine();

      printCheckBox("INCSTART", incstart);
      printText("PROJWPORTLETSPROJWMYPROJECTSSTART:  Started");
      printNewLine();

      printCheckBox("INCCOMP", inccomp);
      printText("PROJWPORTLETSPROJWMYPROJECTSWCOMP:  Completed");
      printNewLine();

      printCheckBox("INCCLOSE", incclose);
      printText("PROJWPORTLETSPROJWMYPROJECTSWCLOS:  Closed");
      printNewLine();

      printCheckBox("INCCAN", inccan);
      printText("PROJWPORTLETSPROJWMYPROJECTSOWCANL:  Cancelled");
      printNewLine();

      trans = mgr.perform(trans);

   }


   public void submitCustomization()
   {
      String value = readValue("ONLYMINE");
      onlymine = "TRUE".equals(value);
      writeProfileFlag("ONLYMINE", onlymine);

      incinit   = "TRUE".equals(readValue("INCINIT"));
      writeProfileFlag("INCINIT", incinit);
      incapp    = "TRUE".equals(readValue("INCAPP"));
      writeProfileFlag("INCAPP", incapp);
      incstart  = "TRUE".equals(readValue("INCSTART"));
      writeProfileFlag("INCSTART", incstart);
      inccomp   = "TRUE".equals(readValue("INCCOMP"));
      writeProfileFlag("INCCOMP", inccomp);
      incclose  = "TRUE".equals(readValue("INCCLOSE"));
      writeProfileFlag("INCCLOSE", incclose);
      inccan    = "TRUE".equals(readValue("INCCAN"));
      writeProfileFlag("INCCAN", inccan);
		  

      skip_rows = 0;
      getASPContext().writeValue("SKIP_ROWS","0");
		  
      writeProfileValue("PROJECT_ID",project_id ); 
   }
   
   public static String getDescription()
   {
      return "PROJWPORTLETSPROJWMYPROJECTSPROJ: My Projects";
   }


   public boolean canCustomize()
   {
      return true;
   }


   private String nvl( String str, String instead_of_empty )
   {
      return Str.isEmpty(str) ? instead_of_empty : str;
   }
   
   public void printCustomTable() throws FndException
   {
          ASPManager mgr    = getASPManager();
       	 
	  beginTable();
	  beginTableTitleRow();
	  printTableCell(translate("PROJWPORTLETSPROJWMYPROJECTSPROID: Project ID"));
	  printTableCell(translate("PROJWPORTLETSPROJWMYPROJECTSPRONA: Project Name"));
	  printTableCell(translate("PROJWPORTLETSPROJWMYPROJECTSSTATE: Status"));
          printTableCell(translate("PROJWPORTLETSPROJWMYPROJECTSMANG: Manager"));
	  endTableTitleRow();
	  beginTableBody();
	 	  
	  rowset.first();
          
          
	  for (int i=0; i<rowset.countRows(); i++)
	  {
            
	         url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"projw/ChildTree.page"+"?PROJECT_ID="+getASPManager().URLEncode(rowset.getValue("PROJECT_ID"));			

	         
                 beginTableCell();
		 printLink(rowset.getValue("PROJECT_ID"),url);
		 endTableCell();
              
		 printTableCell(rowset.getValue("NAME"));
		 printTableCell(rowset.getValue("STATE"));
                 printTableCell(rowset.getValue("MANAGER"));
		 nextTableRow();
		 rowset.next();
          }
	  rowset.first();

	  endTableBody();
	  endTable();
   }
}