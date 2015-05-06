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
 * File        : DocumentPackages.java
 * Description : Overview Design Document Packages Portlet
 * Notes       :
 * ----------------------------------------------------------------------------
 * Author       : Sandaruwan Wijenayake
 * Date created : 19-Apr-2000
 * Files Called : 
 * Modified By :
 *    23-May-2000  Sandaruwan Wijenayake  Translations. Changed the title
 *    22-June-2000 Sandaruwan Wijenayake  Added next/previous functionality.
 *    22-June-2000 Sandaruwan Wijenayake  Corrected minimized title to display no fo db rows.
 *
 *   Chanaka    Webkit 300A Beta1 change of printTableCell used for 
 *   other operation than in pure text.
 *   28-Nov-2000  Shamal     Added Customize this portlet message.
 *   30-April-2001 Gunasiri  Changed Translation Constants.
 *   03-May-2001  Shamal     Add PackageId field.
 *   03-May-2001  Shamal     Call Id - 64679 Corrected the setfunction which used to get the Sub Project Description.
 *   07-Sep-2001  Muneera    Removed the duplicated translation constants and stndardized the non standard constants.
 *   03-sep-2002  Larelk     Bug 32335 midified run()
 *
 *   29-Jul-2003  Nimhlk     ----------------- Merged bug fixes in SP4 ------------------------------------------   
 *   12-Nov-2002  Gacolk     Bug 33842 Modified the WHERE condition in Run()
 *   16-Feb-2004  RuRalk     Bug 40534, Extracted project id from the profile variable saved under the portal. 
 *   ------------------------------------------------------------------------------------------------------------
 *   21-Jul-2004  Ishelk     Changed the coding according to the changes in DESIGN_DOCUMENT_PACKAGE view.
 *   09-Dec-2005  Saallk     Call 128800, Modified method call readGlobalProfileValue accordingly to new Web Client Foundation.
 *   22-Mar-2006  Saallk     Call 137269, Removed check for user access as it is already done in ACTIVITY_PUB view.
 *   06-Nov-2006  KARALK     Bug 60924, Merged, db_rows and skip_rows values written to the page context.
 *   13-Nov-2006  Chselk     Bug 58216. merged SQL Injection.
 *   26-Jul-2007  WYRALK     Merged Bug 64343
 * ----------------------------------------------------------------------------
 */

package ifs.projw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 */
public class DocumentPackages extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = true; 
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
   
   private transient int      skip_rows;
   private transient int      db_rows;
   private String  sHideProject;
   private String  sHideProjectName;
   private String  sHideSubProj;
   private String  sHideSubProjName;
   private String  sHideForApp;
   private String  sHideComplete;
   private String  hide_responsible;

   
   //==========================================================================
   //  Construction
   //==========================================================================

   public DocumentPackages( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
      if(DEBUG) debug(this+": DocumentPackages.<init> :"+portal+","+clspath);
   }

   public ASPPage construct() throws FndException
   {
      if(DEBUG) debug(this+": DocumentPackages.construct()");
      return super.construct();
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected void doFreeze() throws FndException
   {
      if(DEBUG) debug(this+": DocumentPackages.doFreeze()");
      super.doFreeze();
   }

   protected void doReset() throws FndException
   {
      if(DEBUG) debug(this+": DocumentPackages.doReset()");
      super.doReset();
   }

   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      if(DEBUG) debug(this+": DocumentPackages.clone("+mgr+")");

      DocumentPackages page = (DocumentPackages)(super.clone(mgr));

      page.ctx  = page.getASPContext();
      page.blk = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl   = page.getASPTable(tbl.getName());
      page.sHideProject = "F";
      page.sHideProjectName = "T";
      page.sHideSubProj = "F";
      page.sHideSubProjName = "T";
      page.sHideForApp = "F";
      page.sHideComplete = "F";    
      return page;
   }

   //==========================================================================
   //  
   //==========================================================================

   protected void preDefine()
   {
      if(DEBUG) debug(this+": DocumentPackages.preDefine()");
      //Bug Id 60924, Start
      ctx = getASPContext();
      //Bug Id 60924, End
        blk = newASPBlock("DOCPACK");
        
        addField(blk, "PROJECT_ID").setLabel("PROJWPORTLETDOCUMENTPACKAGESPROJ: Project");
        
        addField(blk, "PROJECT_DES").setLabel("PROJWPORTLETDOCUMENTPACKAGESROJNAME: Project Name").
        setFunction("PROJECT_API.Get_description(:PROJECT_ID)");
        
        addField(blk, "SUB_PROJECT_ID").setLabel("PROJWPORTLETDOCUMENTPACKAGESSPROJ: Sub Project");
        
        addField(blk, "SUB_PROJECT_DES").setLabel("PROJWPORTLETDOCUMENTPACKAGESSPROJNA: Sub Project Name").
        setFunction("SUB_PROJECT_API.Get_description(:PROJECT_ID,:SUB_PROJECT_ID)");
        
        addField(blk, "PACKAGE_NO").
        setLabel("PROJWPORTLETDOCUMENTPACKAGESPACKID: Package ID").
        setHyperlink("projw/DesignDocumentPackage.page","PROJECT_ID,SUB_PROJECT_ID,PACKAGE_NO");

        addField(blk, "PROJ_LU_NAME_DB").setHidden();
        addField(blk, "KEYREF1").setHidden();
        addField(blk, "KEYREF2").setHidden();
        addField(blk, "KEYREF3").setHidden();
        addField(blk, "KEYREF4").setHidden();
        addField(blk, "KEYREF5").setHidden();
        addField(blk, "KEYREF6").setHidden();

        
        addField(blk, "DESCRIPTION").
        setFunction("DOC_PACKAGE_ID_API.Get_Description(:PACKAGE_NO)").
        setLabel("PROJWPORTLETDOCUMENTPACKAGESDOCPACK: Document Package");                                                     
        
        
        addField(blk, "AVAIL_FOR_APP").
        setFunction("DOC_PACKAGE_TEMPLATE_API.Get_Total_To_Approve(:PACKAGE_NO)||'('||DOC_PACKAGE_TEMPLATE_API.Get_Num_Documents(:PACKAGE_NO)||')'").
        setLabel("PROJWPORTLETDOCUMENTPACKAGESAVAPP122: Available for Approval").                                                       
        setAlignment("RIGHT");   
        
        addField(blk, "COMPLETE","Number","##0%").
        setFunction("DOC_PACKAGE_ID_API.Calculate_Progress(:PACKAGE_NO)").
        setLabel("PROJWPORTLETSDOCUMENTPACKAGESCOMP1: Complete").
        setAlignment("RIGHT"); 

        addField(blk, "RESPONSIBLE").
        setLabel("PROJWPORTLETSDOCUMENTPACKAGESREPON: Responsible").
        setAlignment("RIGHT");

        addField(blk, "RESPONSIBLE_USER").
        setLabel("PROJWPORTLETSDOCUMENTPACKAGESRESPONUSR: Reponsible User").
        setAlignment("RIGHT");

        blk.setView("DESIGN_DOCUMENT_PACKAGE");
        
        tbl = newASPTable( blk );
        tbl.disableQueryRow();
        tbl.disableQuickEdit();
        tbl.disableRowCounter();
        tbl.disableRowSelect();
        tbl.disableRowStatus();
        tbl.unsetSortable();
        tbl.unsetEditable();
        
        rowset = blk.getASPRowSet();
        getASPManager().newASPCommandBar(blk);
        
        init();
   }
   
   protected void init()
   {
      if(DEBUG) debug(this+": DocumentPackages.init()");

      hide_responsible = readProfileValue("HDRESPON", "F");

      /*Moved to Run().
     String cmd = readValue("CMD");
      db_rows = Integer.parseInt(ctx.readValue("DB_ROWS","0"));
      skip_rows = Integer.parseInt(ctx.readValue("SKIP_ROWS","0"));

      if( "PREV".equals(cmd) && skip_rows>=size )
      {
         skip_rows -= size;
      }
      else if( "NEXT".equals(cmd) && skip_rows<=db_rows-size )
      {
         skip_rows += size;
      }  
      ctx.writeValue("SKIP_ROWS", Integer.toString(skip_rows));
      */
   }

   protected void run()
   {
      if(DEBUG) debug(this+": DocumentPackages.run()");

      sHideProject = readProfileValue("HDPROJ", "F");
      sHideProjectName = readProfileValue("HDPROJNA", "T");
      sHideSubProj = readProfileValue("HDSUBPROJ", "F");
      sHideSubProjName = readProfileValue("HDSUBPROJNA", "T");
      sHideForApp = readProfileValue("HDFORAPP", "F");
      sHideComplete = readProfileValue("HDCOMP", "F");

      hide_responsible = readProfileValue("HDRESPON", "F");

      skip_rows  = ctx.readNumber("SKIP_ROWS",0);
      db_rows    = ctx.readNumber("DB_ROWS",0);

      String cmd = readValue("CMD");

      if( "PREV".equals(cmd) && skip_rows>=size )
      {
         skip_rows -= size;
      }
      else if( "NEXT".equals(cmd) && skip_rows<=db_rows-size )
      {
         skip_rows += size;
      } 

     if ("T".equalsIgnoreCase(sHideProject))
        getASPField("PROJECT_ID").setHidden();    
     else
        getASPField("PROJECT_ID").unsetHidden();     
     
     if ("T".equalsIgnoreCase(sHideProjectName))
        getASPField("PROJECT_DES").setHidden();   
     else
        getASPField("PROJECT_DES").unsetHidden();    
     
     if ("T".equalsIgnoreCase(sHideSubProj))
        getASPField("SUB_PROJECT_ID").setHidden();   
     else
        getASPField("SUB_PROJECT_ID").unsetHidden();
     
     if ("T".equalsIgnoreCase(sHideSubProjName))
        getASPField("SUB_PROJECT_DES").setHidden();     
     else
        getASPField("SUB_PROJECT_DES").unsetHidden();   
     
     if ("T".equalsIgnoreCase(sHideForApp))
        getASPField("AVAIL_FOR_APP").setHidden();    
     else
        getASPField("AVAIL_FOR_APP").unsetHidden();     
     
     if ("T".equalsIgnoreCase(sHideComplete))
        getASPField("COMPLETE").setHidden();   
     else
        getASPField("COMPLETE").unsetHidden();
     
     ASPManager mgr             = getASPManager();
     ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
     trans.clear();
     ASPQuery qry = trans.addQuery(blk);
     qry.setOrderByClause("PROJECT_ID,SUB_PROJECT_ID,PACKAGE_NO");
     qry.setBufferSize(size);
     qry.skipRows(skip_rows);
     qry.includeMeta("ALL");

     String view_name = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Current"+ProfileUtils.ENTRY_SEP+"Page Node", false);
     String portal_project_id = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Available/"+view_name+ProfileUtils.ENTRY_SEP+"Page Node"+"/PROJID", false);

     if (!(mgr.isEmpty(portal_project_id)))
     {
        qry.addWhereCondition("PROJECT_ID= ?");
        qry.addParameter(this,"PROJECT_ID",portal_project_id);

     }
     if("T".equalsIgnoreCase(hide_responsible))
      {
         qry.addWhereCondition("RESPONSIBLE_USER = ?");
         qry.addParameter(this,"RESPONSIBLE_USER", mgr.getFndUser());
      }

     ctx.writeNumber("SKIP_ROWS",skip_rows); 
     submit(trans);
     db_rows = blk.getASPRowSet().countDbRows();
     trans.clear();
     //Bug Id 60924, Start
      ctx.writeValue("DB_ROWS",Integer.toString(db_rows));
      //Bug Id 60924, End
     
   }

   //==========================================================================
   //  
   //==========================================================================

   public boolean canCustomize()
   {
      return true;
   }

   public static String getDescription()
   {
      return "PROJWPORTLETDOCUMENTPACKAGESTITLE: Document Packages"; 
   }

   public String getTitle( int mode )
   {
      if(DEBUG) debug(this+": DocumentPackages.getTitle("+mode+")");

      if(mode==ASPPortal.MINIMIZED)
         return translate(getDescription()) + translate("PROJWPORTLETDOCUMENTPACKAGESNTXT:  - You have &1 document package(s).", Integer.toString(db_rows)); 
      else
         return translate(getDescription());
   }
   
   public void printContents() throws FndException
   {
      if(DEBUG)
         debug(this+": DocumentPackages.printContents()");

      // hidden field for next and previous links
      printHiddenField("CMD","");
      if (rowset.countRows() == 0 || rowset == null) 
       {
          printNewLine();
          appendToHTML("<a href=\"javascript:customizeBox('"+getId()+"')\">");
          printText("PROJWPORTLETDOCUMENTPACKAGESTCUST: Customize this portlet");
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
            printSubmitLink("PROJWPORTLETDOCUMENTPACKAGESTPRV: Previous","prevCust");
         else
            printText("PROJWPORTLETDOCUMENTPACKAGESPRV: Previous");

         printSpaces(5);
         String rows = translate("PROJWPORTLETDOCUMENTPACKAGESROWS: (Rows &1 to &2 of &3)",
                                 (skip_rows+1)+"",
                                 (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                 db_rows+"");
         printText( rows );
         printSpaces(5);

         if(skip_rows<db_rows-size)
            printSubmitLink("PROJWPORTLETDOCUMENTPACKAGESNXT: Next","nextCust");
         else
            printText("PROJWPORTLETDOCUMENTPACKAGESNXT: Next");

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
   
   public void printCustomBody()  throws FndException
   {
     // Customize table fileds.
     appendToHTML("<table border=0 cellpadding=0 cellspacing=0>");
     appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'><br>");
     printText("PROJWPORTLETDOCUMENTPACKAGESFIELDS: Select fields to show in the table:");

     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'><br>");
     printText("PROJWPORTLETDOCUMENTPACKAGESPROJ: Project");
     appendToHTML("</td><td style='FONT: 8pt Verdana'><br>");
     printCheckBox("CHECK_PROJ","T".equalsIgnoreCase(sHideProject)?false:true);

     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
     printText("PROJWPORTLETDOCUMENTPACKAGESPROJNA: Project Name");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_PROJNA","T".equalsIgnoreCase(sHideProjectName)?false:true);

     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
     printText("PROJWPORTLETDOCUMENTPACKAGESSPROJ: Sub Project");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_SUBPROJ","T".equalsIgnoreCase(sHideSubProj)?false:true);

     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
     printText("PROJWPORTLETDOCUMENTPACKAGESSPROJNA: Sub Project Name");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_SUBPROJNA","T".equalsIgnoreCase(sHideSubProjName)?false:true);    
     
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
     printText("PROJWPORTLETDOCUMENTPACKAGESCOMP124: Available for Approval");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_FORAPP","T".equalsIgnoreCase(sHideForApp)?false:true);
     
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
     printText("PROJWPORTLETDOCUMENTPACKAGESRAPP2: Complete");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_COMP","T".equalsIgnoreCase(sHideComplete)?false:true);

      appendToHTML("</td></tr><tr><th style='FONT: 8pt Verdana' colspan='2' align='left'><br>");
      printText("PROJWPORTLETDOCUMENTPACKAGESMKSEL: Make your selection :");
      appendToHTML("<br></th></tr>");
      appendToHTML("<tr><td style='FONT: 8pt Verdana'>");
      printText("PROJWPORTLETDOCUMENTPACKAGESSHOWRES: Show only document packages that I am responsible for");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_RESPON","T".equalsIgnoreCase(hide_responsible)?true:false);

     appendToHTML("</td></tr></tbody></table>");
   }

   public void submitCustomization()
   {
     if(DEBUG) 
        debug(this+": DocumentPackages.submitCustomization()");

     sHideProject      = "TRUE".equalsIgnoreCase(readValue("CHECK_PROJ"))?"F":"T";
     sHideProjectName  = "TRUE".equalsIgnoreCase(readValue("CHECK_PROJNA"))?"F":"T";
     sHideSubProj      = "TRUE".equalsIgnoreCase(readValue("CHECK_SUBPROJ"))?"F":"T";
     sHideSubProjName  = "TRUE".equalsIgnoreCase(readValue("CHECK_SUBPROJNA"))?"F":"T";
     sHideForApp       = "TRUE".equalsIgnoreCase(readValue("CHECK_FORAPP"))?"F":"T";
     sHideComplete     = "TRUE".equalsIgnoreCase(readValue("CHECK_COMP"))?"F":"T";
     hide_responsible   = "TRUE".equalsIgnoreCase(readValue("CHECK_RESPON"))?"T":"F";
     writeProfileValue("HDPROJ", sHideProject );
     writeProfileValue("HDPROJNA", sHideProjectName );
     writeProfileValue("HDSUBPROJ", sHideSubProj );
     writeProfileValue("HDSUBPROJNA", sHideSubProjName );
     writeProfileValue("HDFORAPP", sHideForApp );
     writeProfileValue("HDCOMP", sHideComplete );
     writeProfileValue("HDRESPON", hide_responsible );
   } 
   
   public void printCustomTable() throws FndException
   {
      ASPManager mgr = getASPManager();

      beginTable();
      beginTableTitleRow();
      if (!getASPField("PROJECT_ID").isHidden())
         printTableCell(translate("PROJWPORTLETDOCUMENTPACKAGESPROJ: Project")); 
         
      if (!getASPField("PROJECT_DES").isHidden())
          printTableCell(translate("PROJWPORTLETDOCUMENTPACKAGESPROJNAME: Project Name"));
      if (!getASPField("SUB_PROJECT_ID").isHidden())
          printTableCell(translate("PROJWPORTLETDOCUMENTPACKAGESSPROJ: Sub Project"));
      if (!getASPField("SUB_PROJECT_DES").isHidden())
          printTableCell(translate("PROJWPORTLETDOCUMENTPACKAGESSPROJNA: Sub Project Name"));
      printTableCell(translate("PROJWPORTLETDOCUMENTPACKAGESDOCPACKID: Package ID"));
      printTableCell(translate("PROJWPORTLETDOCUMENTPACKAGESDOCPACK: Document Package"));
      if (!getASPField("AVAIL_FOR_APP").isHidden())
          printTableCell(translate("PROJWPORTLETDOCUMENTPACKAGESAVAPP15: Available for Approval"));
      if (!getASPField("COMPLETE").isHidden())
          printTableCell(translate("PROJWPORTLETDOCUMENTPACKAGESCOMP3: Complete"));
      endTableTitleRow();
      beginTableBody();
    
      rowset.first();
      for (int i=0; i<rowset.countRows(); i++)   
      {
         if (!getASPField("PROJECT_ID").isHidden())
         { 
                      
            String url = "projw/ChildTree.page"+"?PROJECT_ID="+mgr.URLEncode(rowset.getValue("PROJECT_ID"));
            beginTableCell();
            printLink(rowset.getValue("PROJECT_ID"),url);
            endTableCell();
         
         }
         if (!getASPField("PROJECT_DES").isHidden())
         {
            beginTableCell();
            printText(rowset.getValue("PROJECT_DES"));
            endTableCell();
         
         }
         if (!getASPField("SUB_PROJECT_ID").isHidden())
         {
            beginTableCell();
            printText(rowset.getValue("SUB_PROJECT_ID"));
            endTableCell();
         }
         if (!getASPField("SUB_PROJECT_DES").isHidden())
         {
                      
           beginTableCell();
           printText(rowset.getValue("SUB_PROJECT_DES"));
           endTableCell();         
         } 


         String url2 = "projw/DesignDocumentPackage.page"+
                "?PROJECT_ID="+mgr.URLEncode(rowset.getValue("PROJECT_ID"))+
                "&SUB_PROJECT_ID="+mgr.URLEncode(rowset.getValue("SUB_PROJECT_ID"))+
                "&PACKAGE_NO="+mgr.URLEncode(rowset.getValue("PACKAGE_NO"));

         beginTableCell();
         printLink(rowset.getValue("PACKAGE_NO"),url2);
         endTableCell();
       
         beginTableCell();
         printText(rowset.getValue("DESCRIPTION"));
         endTableCell();
             
         if (!getASPField("AVAIL_FOR_APP").isHidden())
         {  
            beginTableCell("RIGHT");
            printText(rowset.getValue("AVAIL_FOR_APP"));
            endTableCell();
         }
         if (!getASPField("COMPLETE").isHidden())
         {
                      
            beginTableCell("RIGHT");
            printText(rowset.getRow().getFieldValue(this, "COMPLETE"));
            endTableCell();
         }
         nextTableRow();
         rowset.next();
      }
      rowset.first();

      endTableBody();
      endTable();
   }  


}
