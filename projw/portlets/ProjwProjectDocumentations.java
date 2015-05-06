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
 * File        : ProjwProjectDocumentations.java
 * Description : Document Selection Portlet
 * Notes       :
 * ----------------------------------------------------------------------------
 * Author       : Sandaruwan Wijenayake
 * Date created : 01-Apr-2000
 * Files Called : 
 * Modified By  :
 *    Date                 Sign                               History 
 *    14-Apr-2000  Sandaruwan Wijenayake  Add changes to support IFS Webkit 300b2
 *    10-May-2000  Sandaruwan Wijenayake  Change code according to server code.
 *    10-May-2000  Sandaruwan Wijenayake  Change code according to server code.
 *    23-May-2000  Sandaruwan Wijenayake  Translations.
 *    08-June-2000 Sandaruwan Wijenayake  Improve efficiency by directly calling View Document and Remark Document from javascript.
 *    22-June-2000 Sandaruwan Wijenayake  Added next/previous functionality.
 *    22-June-2000 Sandaruwan Wijenayake  Corrected minimized title to display no fo db rows.
 *    27-June-2000 Sandaruwan Wijenayake  Call ID 45340 - Changed the name of the portlet and remove doc class selection.
 *    07-Nov-2000  KeFe                   Modified according to the Project Portal concept.
 *    28-Nov-2000  Shamal                 Added Customize this portlet message.
 *    15-Jan-2001  Jagath                 Call Id - 58700 - Modified according to the new Project Portal concept.
 *    Gunasiri 2001-05-31 - Project Portal Check is added.
 *    Muneera  2001-09-07 - Removed the duplicated translation constants and stndardized the non standard constants.
 *    Shamal   2001-09-20 - CID - 65968 Added a Lov to Project Id field in printCustomBody() method and remove the select box.
 *                                      Also disable the Project selection option when at the Project portal page.
 *    CHAG     2002-02-11   Bug 27815, Errors occur when view descriptions contains " Project Portal " is corrected.
 *    CHAG     2002-03-28   Bug 28637, Reference to ../docmaw/DocAccess.page.changed to../docmaw/DocIssue.page 
 *
 *    Nimhlk   2003-07-29   ------------------ Merged bug fixes in SP4 -------------------------------------
 *    LARELK   2002-12-06   Bug 34437 .corrected in void run()
 *    Larelk   2002-12-23   Bug 34437  corrected in runCustom()
 *    ------------------------------------------------------------------------------------------------------
 *
 *    Nimhlk   2003-10-17   Call ID: 108367 - Modified preDefine() & run().
 *    Mushlk   2003-10-27   Call ID: 109316 - Added the field 'Doc_Sheet' to all relevant ASP blocks and methods.
 *    RuRalk   2004-02-16   Bug 40534, Extracted project id from the profile variable saved under the portal.
 *
 * ----------------------------------------------------------------------------
 * ------------------------------------ EDGE ----------------------------------
 * ----------------------------------------------------------------------------
 *
 *    Saallk   2005-12-09 - Call 128800, Modified method call readGlobalProfileValue accordingly to new Web Client Foundation. 
 * ----------------------------------------------------------------------------
 *    karalk   2006-11-14 - Bug, 58216. Merged, SQL Injection.
 *              Rucslk  2007-03-21 - Platform Tidy up in Sparx - Update the method submitCustomization - use the new function readAbsoluteValue() 
 *    ASSALK   2007-08-07 - Merged Bug ID 65396, Modified run() to save skip_rows,db_rows in page context to function next,previose buttons properly.
 *    ASSALK   2007-08-07  Merged Bug 64068. removed additional mgr.translate() statements.
 *    RASELK   2008-03-19  Bug 71943, Modified run() to remove the condition checking access for the documents.
 *    CHRALK   2009-12-03  Bug 87284, Modified printCustomTable() method to avoid backslash problem.
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
public class ProjwProjectDocumentations extends ASPPortletProvider
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

   private transient ASPTransactionBuffer trans;
   private transient int      skip_rows;
   private transient int      db_rows;
   private String url;
   private String  project_id;
   private String  project_name;
   private String  sHideDocClass;
   private String  sHideDocNo;
   private String  sHideDocSheet;
   private String  sHideDocRev;
   private String  sHideDesc;
   private String  sHideCreBy;
   private String  sHideDtCre;
   private String  sHideModBy;
   private String  sHideDtMod;
   private boolean projectportal;

   //==========================================================================
   //  Construction
   //==========================================================================

   public ProjwProjectDocumentations( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
      if (DEBUG) debug(this+": ProjwProjectDocumentations.<init> :"+portal+","+clspath);
   }

   public ASPPage construct() throws FndException
   {
      if (DEBUG) debug(this+": ProjwProjectDocumentations.construct()");
      return super.construct();
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected void doFreeze() throws FndException
   {
      if (DEBUG) debug(this+": ProjwProjectDocumentations.doFreeze()");
      super.doFreeze();
   }

   protected void doReset() throws FndException
   {
      if (DEBUG) debug(this+": ProjwProjectDocumentations.doReset()");
      url        = null;
      super.doReset();
   }

   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      if (DEBUG) debug(this+": ProjwProjectDocumentations.clone("+mgr+")");

      ProjwProjectDocumentations page = (ProjwProjectDocumentations)(super.clone(mgr));

      page.project_id = null;
      page.project_name = null;
      page.sHideDocClass = "T";
      page.sHideDocNo = "T";
      page.sHideDocSheet = "T";
      page.sHideDocRev = "T";
      page.sHideDesc = "T";
      page.sHideCreBy = "T";
      page.sHideDtCre = "T";
      page.sHideModBy = "T";
      page.sHideDtMod = "T";

      page.ctx    = page.getASPContext();
      page.blk    = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl    = page.getASPTable(tbl.getName());

      return page;
   }

   //==========================================================================
   //  
   //==========================================================================

   protected void preDefine()
   {
      if (DEBUG) debug(this+": ProjwProjectDocumentations.preDefine()");

      blk = newASPBlock("MAIN");

      addField(blk, "PROJECT_ID").setHidden();

      addField(blk, "NAME").
      setFunction("Project_API.Get_Name(:PROJECT_ID)").
      setHidden();

      addField(blk, "OBJSTATE").setHidden();

      addField(blk, "DOC_KEY").setFunction("DOC_CLASS||'-'||DOC_NO||'-'||DOC_SHEET||'-'||DOC_REV").setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSKEY: Doc Key");

      addField(blk, "TITLE").
      setFunction("Doc_Title_API.Get_Title(:DOC_CLASS,:DOC_NO)").
      setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSFTITLE: Title");

      addField(blk, "DT_RELEASED","Date").
      setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDTREL: Date Released").
      setAlignment("CENTER");  

      addField(blk, "DOC_CLASS").setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDOCCLASS: Doc Class");

      addField(blk, "DOC_NO").setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDOCNO: Doc No");

      addField(blk, "DOC_SHEET").setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDOSHEET: Doc Sheet");

      addField(blk, "DOC_REV").setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDOCREV: Doc Rev");

      addField(blk, "CLASSDESC").
      setFunction("DOC_CLASS_API.Get_Name(:DOC_CLASS)").
      setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSCLSDES: Class Description");

      addField(blk, "USER_CREATED").setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSCREBY: Created By");

      addField(blk, "DT_CRE","Date")
      .setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDTCRE: Date Created").
      setAlignment("CENTER");  

      addField(blk, "USER_SIGN").setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSMODBY: Modified By");

      addField(blk, "DT_CHG","Date")
      .setLabel("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDTMOD: Date Modified").
      setAlignment("CENTER");  

      addField(blk, "FILE_TYPE").
      setFunction("EDM_FILE_API.GET_FILE_TYPE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')").  
      setHidden();

      getASPField("DOC_KEY").setHyperlink("docmaw/DocIssue.page","DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

      blk.setView("DOC_PROJECT_CONNECTION_REF");

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
      if (DEBUG) debug(this+": ProjwProjectDocumentations.init()");      
   }

   protected void run()
   {
      if (DEBUG) debug(this+": ProjwProjectDocumentations.run()");

      ASPManager mgr = getASPManager();
      ASPContext ctx = getASPContext();
      String cmd = readValue("CMD");
      trans = mgr.newASPTransactionBuffer();
      project_id = readProfileValue("PROJECTID", "");
      sHideDocClass = readProfileValue("HDOCCLASS", "T");
      sHideDocNo    = readProfileValue("HDOCNO", "T");
      sHideDocSheet = readProfileValue("HDOCSHEET", "T");
      sHideDocRev   = readProfileValue("HDOCCREV", "T");
      sHideDesc     = readProfileValue("HDESC", "T");
      sHideCreBy    = readProfileValue("HCREBY", "T");
      sHideDtCre    = readProfileValue("HDTCRE", "T");
      sHideModBy    = readProfileValue("HMODBY", "T");
      sHideDtMod    = readProfileValue("HDTMOD", "T");

      skip_rows  = ctx.readNumber("SKIP_ROWS",0);
      db_rows    = ctx.readNumber("DB_ROWS",0);

      if ( "PREV".equals(cmd) && skip_rows>=size )
      {
         skip_rows -= size;
      }
      else if ( "NEXT".equals(cmd) && skip_rows<=db_rows-size )
      {
         skip_rows += size;
      }

      if (sHideDocClass.equalsIgnoreCase("T"))
         getASPField("DOC_CLASS").setHidden();
      else
         getASPField("DOC_CLASS").unsetHidden();     
      if (sHideDocNo.equalsIgnoreCase("T"))
         getASPField("DOC_NO").setHidden();
      else
         getASPField("DOC_NO").unsetHidden();
      if (sHideDocSheet.equalsIgnoreCase("T"))
         getASPField("DOC_SHEET").setHidden();
      else
         getASPField("DOC_SHEET").unsetHidden();
      if (sHideDocRev.equalsIgnoreCase("T"))
         getASPField("DOC_REV").setHidden();
      else
         getASPField("DOC_REV").unsetHidden();    
      if (sHideDesc.equalsIgnoreCase("T"))
         getASPField("CLASSDESC").setHidden();
      else
         getASPField("CLASSDESC").unsetHidden();     
      if (sHideCreBy.equalsIgnoreCase("T"))
         getASPField("USER_CREATED").setHidden();
      else
         getASPField("USER_CREATED").unsetHidden();     
      if (sHideDtCre.equalsIgnoreCase("T"))
         getASPField("DT_CRE").setHidden();
      else
         getASPField("DT_CRE").unsetHidden();     
      if (sHideModBy.equalsIgnoreCase("T"))
         getASPField("USER_SIGN").setHidden();
      else
         getASPField("USER_SIGN").unsetHidden();     
      if (sHideDtMod.equalsIgnoreCase("T"))
         getASPField("DT_CHG").setHidden();
      else
         getASPField("DT_CHG").unsetHidden();          

      String view_name = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Current"+ProfileUtils.ENTRY_SEP+"Page Node", false);
      String prof_proj_id = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Available/"+view_name+ProfileUtils.ENTRY_SEP+"Page Node"+"/PROJID", false);
      if (!(mgr.isEmpty(prof_proj_id)))
      {
         projectportal= true;
         project_id = prof_proj_id;
      }
      else
      {
         projectportal = false;
      }


      if ((!Str.isEmpty(project_id)))
      {
         ASPCommand cmd1 = mgr.newASPCommand();
         cmd1.defineCustomFunction(this, "Project_API.Get_Name", "NAME");
         cmd1.setParameter(this,"PROJECT_ID",project_id);
         trans.addCommand("GETPROJNAME", cmd1);
         trans = mgr.perform(trans);
         project_name = trans.getValue("GETPROJNAME/DATA/NAME");
         trans.clear();
      }

      ASPQuery qry = trans.addQuery(blk);
      qry.setBufferSize(size);
      qry.skipRows(skip_rows);
      qry.setOrderByClause("DT_RELEASED DESC");
      qry.includeMeta("ALL");
      qry.addWhereCondition("OBJSTATE='Released'");
      //Bug ID 71943,Start
      //qry.addWhereCondition("Doc_Issue_API.Get_View_Access_(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV)='TRUE'"); 
      //Bug ID 71943,End
      qry.addWhereCondition("PROJECT_ID= ? ");
      qry.addParameter(this,"PROJECT_ID",project_id);

      submit(trans);
      db_rows = blk.getASPRowSet().countDbRows();
      ctx.writeNumber("SKIP_ROWS",skip_rows);
      ctx.writeNumber("DB_ROWS", db_rows);
      trans.clear();
   }

   //==========================================================================
   //  
   //==========================================================================


   public boolean canCustomize()
   {
      return true;
   }

   public void runCustom()
   {
      ASPManager mgr = getASPManager(); 
      trans = mgr.newASPTransactionBuffer();
      ASPQuery qry;
      run();      
   }

   public static String getDescription()
   {
      return "PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDESCRIP: Project Documentations"; 
   }

   public String getTitle( int mode )
   {
      if (DEBUG) debug(this+": ProjwProjectDocumentations.getTitle("+mode+")");

      if (mode==ASPPortal.MINIMIZED)
         return translate(getDescription()) + translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSMINTXT:  - You have &1 released document(s).", Integer.toString(db_rows));
      else
         return translate(getDescription());  
   }

   public void printContents() throws FndException
   {
      if (DEBUG)
      {
         debug(this+": ProjwProjectDocumentations.printContents()");
         debug("\tgetContents(): current values:\n\t\tproject_id="+project_id+"\n");
      }

      if (Str.isEmpty(project_id))
      {
         printNewLine();     
         appendToHTML("<a href=\"javascript:customizeBox('"+getId()+"')\">");
         printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSCUST: Customize this portlet");
         appendToHTML("</a>");       
         printNewLine();
      }
      else
      {
         // hidden field for next and previous links
         printHiddenField("CMD","");
         url = "projw/ChildTree.page"+"?PROJECT_ID="+project_id;

         printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSCPROJ: Project: ");
         printLink(project_id,url);
         if (Str.isEmpty(project_name))
            project_name = "";
         printText("  "+project_name);
         printNewLine();
         printNewLine();
         printCustomTable();

         if (size < db_rows )
         {
            printNewLine();
            printSpaces(5);
            if (skip_rows>0)
               printSubmitLink("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSPRV: Previous","prevCust");
            else
               printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSPRV: Previous");

            printSpaces(5);
            String rows = translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSROWS: (Rows &1 to &2 of &3)",
                                    (skip_rows+1)+"",
                                    (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                    db_rows+"");
            printText( rows );
            printSpaces(5);

            if (skip_rows<db_rows-size)
               printSubmitLink("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSNXT: Next","nextCust");
            else
               printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSNXT: Next");

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

         appendDirtyJavaScript(
                              "function "+addProviderPrefix()+"openInNewWindow( file_path )\n"+
                              "{ \n"+
                              "   window.open(file_path,'anotherWindow','status,resizable,scrollbars,width=500,height=500,left=100,top=100');\n"+
                              "} \n");
      }
   }

   public void printCustomBody()
   {
      ASPManager mgr = getASPManager();
      appendToHTML("<table border=0 cellpadding=0 cellspacing=0 width='100%'>");
      appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'>");

      appendToHTML("<table border=0 cellpadding=0 cellspacing=0>");
      appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'>");   
      if (Str.isEmpty(project_id))
         printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSNOPRJSEL: No project selected.");
      else
      {
         printSpaces(5);
         printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSCUPROJ: Project: ");
         printText(project_id+"  ");
         printText( project_name );
      }
      printNewLine();
      printNewLine();
      printSpaces(5);
      if (!projectportal)
      {
         printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSELANPRJ: Select another project: ");              
         printSpaces(5);
         printField("TEMP_PROJECT",project_id,15);
         printDynamicLOV("TEMP_PROJECT","PROJECT","PROJWPORTLETSPROJWPROJECTDOCUMENTATIONPROJLOV: List of Projects");
      }
      printNewLine();
      printNewLine();
      appendToHTML("</td></tr></tbody></table>");


      // Customize table fileds.
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      appendToHTML("<table border=0 cellpadding=0 cellspacing=0>");
      appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'><br>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSELFIELDS: Select fields to show in the table:");
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'><br>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSFDOCCLS: Doc Class");
      appendToHTML("</td><td style='FONT: 8pt Verdana'><br>");
      printCheckBox("CHECK_DOC_CLASS","T".equalsIgnoreCase(sHideDocClass)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSFDOCNO: Doc No");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_DOC_NO","T".equalsIgnoreCase(sHideDocNo)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSFDOCSHEET: Doc Sheet");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_DOC_SHEET","T".equalsIgnoreCase(sHideDocSheet)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSFDOCREV: Doc Rev");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_DOC_REV","T".equalsIgnoreCase(sHideDocRev)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSFCLSDES: Class Description");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_CLASS_DESC","T".equalsIgnoreCase(sHideDesc)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSFCREBY: Created By");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_USER_CREATED","T".equalsIgnoreCase(sHideCreBy)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSFDTCRE: Date Created");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_DT_CRE","T".equalsIgnoreCase(sHideDtCre)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSFMODBY: Modified By");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_USER_SIGN","T".equalsIgnoreCase(sHideModBy)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
      printText("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSSFDTMOD: Date Modified");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_DT_CHG","T".equalsIgnoreCase(sHideDtMod)?false:true);
      appendToHTML("</td></tr></tbody></table>");

      appendToHTML("</td></tr></tbody></table>");
   }

   public void submitCustomization()
   {
      if (DEBUG)
      {
         debug(this+": DemoOrderProvider.submitCustomization()");
         debug("\tsubmitCustomization(): current values:\n\t\tproject_id="+project_id+"\n");
      }

      project_id  = readValue("TEMP_PROJECT");
      project_name  = "";
      sHideDocClass = "TRUE".equalsIgnoreCase(readValue("CHECK_DOC_CLASS"))?"F":"T";
      sHideDocNo    = "TRUE".equalsIgnoreCase(readValue("CHECK_DOC_NO"))?"F":"T";
      sHideDocSheet = "TRUE".equalsIgnoreCase(readValue("CHECK_DOC_SHEET"))?"F":"T";
      sHideDocRev   = "TRUE".equalsIgnoreCase(readValue("CHECK_DOC_REV"))?"F":"T";
      sHideDesc     = "TRUE".equalsIgnoreCase(readValue("CHECK_CLASS_DESC"))?"F":"T";
      sHideCreBy    = "TRUE".equalsIgnoreCase(readValue("CHECK_USER_CREATED"))?"F":"T";
      sHideDtCre    = "TRUE".equalsIgnoreCase(readValue("CHECK_DT_CRE"))?"F":"T";
      sHideModBy    = "TRUE".equalsIgnoreCase(readValue("CHECK_USER_SIGN"))?"F":"T";
      sHideDtMod    = "TRUE".equalsIgnoreCase(readValue("CHECK_DT_CHG"))?"F":"T";

      writeProfileValue("PROJECTID",  readAbsoluteValue("TEMP_PROJECT"));
      writeProfileValue("PROJECTNAME", "" );
      writeProfileValue("DOCNAME", "" );
      writeProfileValue("HDOCCLASS", sHideDocClass );
      writeProfileValue("HDOCNO", sHideDocNo );
      writeProfileValue("HDOCSHEET", sHideDocSheet );
      writeProfileValue("HDOCCREV", sHideDocRev );
      writeProfileValue("HDESC", sHideDesc );
      writeProfileValue("HCREBY", sHideCreBy );
      writeProfileValue("HDTCRE", sHideDtCre );
      writeProfileValue("HMODBY", sHideModBy );
      writeProfileValue("HDTMOD", sHideDtMod );
      skip_rows = 0;
      getASPContext().writeNumber("SKIP_ROWS",skip_rows);
   }

   public void printCustomTable() throws FndException
   {
      ASPManager mgr = getASPManager();
      //Bug 87284, Start
      String imageurl;
      String href = "";
      //Bug 87284, End

      beginTable();
      beginTableTitleRow();
      printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDOCKEY: Doc Key"));
      printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSFTITLE: Title"));
      printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSRELE: Released"));
      if (!getASPField("DOC_CLASS").isHidden())
         printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDOCCLS: Doc Class"));
      if (!getASPField("DOC_NO").isHidden())
         printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDOCNO: Doc No"));
      if (!getASPField("DOC_SHEET").isHidden())
         printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDOCSHEET: Doc Sheet"));
      if (!getASPField("DOC_REV").isHidden())
         printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDOCREV: Doc Rev"));
      if (!getASPField("CLASSDESC").isHidden())
         printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSCLSDES: Class Description"));
      if (!getASPField("USER_CREATED").isHidden())
         printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSCREBY: Created By"));
      if (!getASPField("DT_CRE").isHidden())
         printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDTCRE: Date Created"));
      if (!getASPField("USER_SIGN").isHidden())
         printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSMODBY: Modified By"));
      if (!getASPField("DT_CHG").isHidden())
         printTableCell(translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSDTMOD: Date Modified"));
      printTableCell("");
      endTableTitleRow();
      beginTableBody();

      rowset.first();
      for (int i=0; i<rowset.countRows(); i++)
      {
         String url2 = "docmaw/DocIssue.page"+
                       "?DOC_CLASS="+mgr.URLEncode(rowset.getValue("DOC_CLASS"))+
                       "&DOC_NO="+mgr.URLEncode(rowset.getValue("DOC_NO"))+
                       "&DOC_SHEET="+mgr.URLEncode(rowset.getValue("DOC_SHEET"))+
                       "&DOC_REV="+mgr.URLEncode(rowset.getValue("DOC_REV"));

         beginTableCell();
         printLink(rowset.getValue("DOC_KEY"),url2);
         endTableCell();

         printTableCell(rowset.getValue("TITLE"));
         printTableCell(rowset.getRow().getFieldValue(this, "DT_RELEASED") ,"CENTER");
         if (!getASPField("DOC_CLASS").isHidden())
            printTableCell(rowset.getValue("DOC_CLASS"));
         if (!getASPField("DOC_NO").isHidden())
            printTableCell(rowset.getValue("DOC_NO"));
         if (!getASPField("DOC_SHEET").isHidden())
            printTableCell(rowset.getValue("DOC_SHEET"));
         if (!getASPField("DOC_REV").isHidden())
            printTableCell(rowset.getValue("DOC_REV"));
         if (!getASPField("CLASSDESC").isHidden())
            printTableCell(rowset.getValue("CLASSDESC"));
         if (!getASPField("USER_CREATED").isHidden())
            printTableCell(rowset.getValue("USER_CREATED"));
         if (!getASPField("DT_CRE").isHidden())
            printTableCell(rowset.getRow().getFieldValue(this,"DT_CRE"),"CENTER");
         if (!getASPField("USER_SIGN").isHidden())
            printTableCell(rowset.getValue("USER_SIGN"));
         if (!getASPField("DT_CHG").isHidden())
            printTableCell(rowset.getRow().getFieldValue(this,"DT_CHG"),"CENTER");
         
         //Bug 87284, Start
         String sFilePath  = "docmaw/EdmMacro.page?DOC_CLASS="+mgr.URLEncode(rowset.getValue("DOC_CLASS").replaceAll("\\\\","\\\\\\\\"));
         sFilePath += "&DOC_NO="+mgr.URLEncode(rowset.getValue("DOC_NO").replaceAll("\\\\","\\\\\\\\"));
         sFilePath += "&DOC_SHEET="+mgr.URLEncode(rowset.getValue("DOC_SHEET").replaceAll("\\\\","\\\\\\\\"));
         sFilePath += "&DOC_REV="+mgr.URLEncode(rowset.getValue("DOC_REV").replaceAll("\\\\","\\\\\\\\"));
         sFilePath += "&DOC_TYPE=ORIGINAL";
         sFilePath += "&FILE_TYPE="+mgr.URLEncode(rowset.getValue("FILE_TYPE"));
         sFilePath += "&PROCESS_DB=VIEW";
         //Bug 87284, End

         beginTableCell();
         //Bug 87284, Start
         imageurl = "docmaw/images/open.gif";
         href = "javascript:showNewBrowser('"+ mgr.encodeStringForJavascript(sFilePath) +"')";
         printImageLink(imageurl,href,"",translate("PROJWPORTLETSPROJWPROJECTDOCUMENTATIONSALTVIEW: View"));         
         //Bug 87284, End
         endTableCell();      

         nextTableRow();
         rowset.next();
      }
      rowset.first();

      endTableBody();
      endTable();
   }
}
