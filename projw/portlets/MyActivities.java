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
 * File        : MyActivities.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Anne S  2000-Apr-12 - Created.
 *            2000-Jun-06 - Modified. Adapted webkit release 5. Added link. Added start/finish check with selection box.
 *    Sand    2000-Jun-22 - Modified. Call ID 44300, Convert progress into ##0.00% format.
 *                                    Corrected bug in the 'previous' link.
 *    Sand    2000-Jun-22 - Modified. Corrected minimized title to display no fo db rows.
 *    Sand    2000-Jul-14 - Modified. Call ID - 45887 .
 *    MaLa    2000-Oct-30 - Modified. (CPS Solution), added 4 columns (Planned, Actual, Earned, CPI)
 *                                    and option to select between hours or cost
 *    KeFe    2000-Nov-07 - Modified. Modified according to the Project Portal concept.
 *    JaJa    2000-Nov-21 - Modified. Call ID - 53866.
 *    Shamal  2000-Nov-28 - Added Customize this portlet message.
 *    KeFe    2001-Jan-12 - CID 58700 Change the code belongs to Project Portal concept in order to compatible with 300b3.
 *    30-April-2001 Gunasiri Changed Translation Constants.
 *    Gunasiri 2001-05-31 -  Project Portal Check is added.
 *    Muneera  2001-09-07    Removed the duplicated translation constants and stndardized the non standard constants.
 *    CHAG     2002-02-11    Bug 27815, Errors occur when view descriptions contains " Project Portal " is corrected.
 *    020821 ThAblk Bug 28975, This was corrected in PROJW 2.0.0. Just got the correction and merged it. Added project_id to where condition.
 *    020902 ThAblk Bug 32466, Added skip_rows and db_rows to the context. Moved a snippet of code from init() to run().
 *    020902        Commented doFreeze() to avoid some variables being reinitialised after being initialised in init();
 *    021008 Larelk Bug 33365 added activity state closed.
 *    021211 Gacolk Merged bug fixes in 2002-3 SP3
 *   
 *    17-Oct-2003   Nimhlk - Call ID:108367 - Removed golbally defined Integer varable and defined it locally.
 *    24-Oct-2003   KrSilk - Call 109070; Replaced all references to "CUS_TITLE" with "MY_TITLE"
 *                         - to make a translatable default value appear in Portlet Label.
 *    16-Feb-2004  RuRalk     Bug 40534, Extracted project id from the profile variable saved under the portal. 
 *    20-Jul-2004  Ishelk  - Changed the reference from Activity_Detail_api to project_connection_detail_api.
 *    26-Jul-2005  Gacolk  - Merged Patched Bug 51725 (ACTIVITY_DETAIL_API was replaced by PROJECT_CONNECTION_DETAILS_API)
 *                           09-Jun-2005  GaColk  - Bug 51725, Used ACTIVITY_DETAIL_API.Get_Actual() to get the value for Actual column.
 *    22-Sep-2005  Saallk  - Replaced now Obsolete Get_Planned_Revised method with new Get_Planned_Baseline method.
 *    09-Dec-2005  Saallk  - Call 128800, Modified method call readGlobalProfileValue accordingly to new Web Client Foundation. 
 *    15-Mar-2006  Raselk  - Modified the CPI calculation to use "USED" value except fro "ACTUAL" and replaced Actual column with used column.
 *    13-Nov-2006  karalk  - Bug 58216. merged SQL Injection.
 *		21-Mar-2007	 Rucslk	- Platform Tidy up in Sparx - Update the method submitCustomization - use the new function readAbsoluteValue() 
 *    13-Aug-2007  Janslk  - Changed db call PROJECT_CONNECTION_DETAILS_API.Get_Used_Value to PROJECT_CONNECTION_DETAILS_API.Get_Used in run(). 
 *    24-Sep-2009  Raeklk  - Bug 85691, Modified preDefine() and printCustomTable() to handle a convertion error in "ACTIVITY_SEQ".
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

/**
 */
public class MyActivities extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = true;
   private final static String transl_prefix = "PROJWMYACT";
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

   private String  date_int;

   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient int      skip_rows;
   private transient int      db_rows;
   private String  responsible;
   private String  where_stmt;
   boolean onlymine  = true;
   boolean incstart  = false;
   boolean incfini   = false;
   boolean incplan   = false;
   boolean increl    = true;
   boolean inccomp   = false;
   boolean incclose  = false;
   boolean inccan    = false;
   private String  sHideStart;
   private String  sHideFinish;
   private String  sHidePlanned;
   private String  sHideUsed;
   private String  sHideEarned;
   private String  sHideProgress;
   private String  sHideCPI;
   private String  sHideStatus;
   private String  sHideResId;
   private String  sHideResName;
   private String  sBase;
   private String  sBaseValue="H";
   private String sBaseHeader;
   private transient String  my_title =  null;
   private transient String project_id;
   private boolean projectportal;
         
   //==========================================================================
   //  Construction
   //==========================================================================

   public MyActivities( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
      if(DEBUG) debug(this+": MyActivities.<init> :"+portal+","+clspath);
   }


   public ASPPage construct() throws FndException
   {
      if(DEBUG) debug(this+": MyActivities.construct()");
      return super.construct();
   }

   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      if(DEBUG) debug(this+": MyActivities.clone("+mgr+")");

      MyActivities page = (MyActivities)(super.clone(mgr));

      date_int    = "";

      page.ctx    = page.getASPContext();
      page.blk    = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl    = page.getASPTable(tbl.getName());

      page.onlymine  = true;
      page.incstart  = false;
      page.incfini   = false;
      page.incplan   = false;
      page.increl    = true;
      page.inccomp   = false;
      page.incclose  = false;
      page.inccan    = false;
      page.sHideStart = "F";
      page.sHideFinish = "F";
      page.sHidePlanned = "F";
      page.sHideUsed  = "F";
      page.sHideEarned  = "F";
      page.sHideProgress = "F";
      page.sHideCPI = "F";
      page.sHideStatus = "T";
      page.sHideResId = "T";
      page.sHideResName = "T";

      page.sBase = "H";

     
      return page;
   }

   //==========================================================================
   //  
   //==========================================================================

   protected void preDefine()
   {
      if(DEBUG) debug(this+": MyActivities.preDefine()");

      ctx = getASPContext();
      
      blk = newASPBlock("MAIN");

      //Bug Id 85691, Start
      addField(blk, "ACTIVITY_SEQ","Number").
      setHidden();
      //Bug Id 85691, End
     
      addField(blk, "PROJECT_ID").
      setLabel("PROJWPORTLETSMYACTIVITIESPROJECT: Project").
      setHyperlink("projw/ChildTree.page","PROJECT_ID");
     
      addField(blk, "SUB_PROJECT_ID").
      setLabel("PROJWPORTLETSMYACTIVITIESSUBPROJECT: Sub Project");

      addField(blk, "ACTIVITY").
      setFunction("ACTIVITY_API.Get_Description(:ACTIVITY_SEQ)").
      setHyperlink("projw/Activity.page","ACTIVITY_SEQ").
      setLabel("PROJWPORTLETSMYACTIVITIESACTIV: Activity");

      addField(blk, "EARLY_START", "Date").          
      setLabel("PROJWPORTLETSMYACTIVITIESSTART: Start");

      addField(blk, "EARLY_FINISH", "Date").
      setLabel("PROJWPORTLETSMYACTIVITIESFIN: Finish");

      addField(blk, "PLANNED","Number","############0.0");
      
      addField(blk, "USED","Number","############0.0");
      
      addField(blk, "EARNED","Number","############0.0").
      setFunction("Null");
      
      addField(blk, "PROGRESS_HOURS_WEIGHTED","Number","##0%").
      setFunction("NVL(PROGRESS_HOURS_WEIGHTED,0)").
      setAlignment("RIGHT").
      setLabel("PROJWPORTLETSMYACTIVITIESPROG: Progress");

      addField(blk, "CPI","Number").
      setFunction("Null");
      
      addField(blk, "STATE").
      setLabel("PROJWPORTLETSMYACTIVITIESSTATE: Status");

      addField(blk, "ACTIVITY_RESPONSIBLE").
      setLabel("PROJWPORTLETSMYACTIVITIESRESP: Responsible Id");

      addField(blk, "RESPONSIBLE_NAME").
      setFunction("PERSON_INFO_API.Get_Name(:ACTIVITY_RESPONSIBLE)").
      setLabel("PROJWPORTLETSMYACTIVITIESRESPNAME: Responsible Name");

      blk.setView("ACTIVITY");

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
      if(DEBUG) debug(this+": MyActivities.init()");
      my_title = readProfileValue("MY_TITLE", translate("PROJWPORTLETSMYACTIVITIESYACT: My Activities"));
     
      date_int  = readProfileValue("DATE_INT", "");
      incstart  = readProfileFlag("INCSTART", false);
      incfini   = readProfileFlag("INCFINI", false);
      onlymine  = readProfileFlag("ONLYMINE", true);
      incplan   = readProfileFlag("INCPLAN", false);
      increl    = readProfileFlag("INCREL", true);
      inccomp   = readProfileFlag("INCCOMP", false);
      incclose  = readProfileFlag("INCCLOSE", false);
      inccan    = readProfileFlag("INCCAN", false);

      sHideStart = readProfileValue("HSTART");
      sHideFinish = readProfileValue("HFINISH");
      sHidePlanned = readProfileValue("HPLANNED");
      sHideUsed  = readProfileValue("HUSED");
      sHideEarned  = readProfileValue("HEARNED");
      sHideProgress= readProfileValue("HPROGRESS");
      sHideCPI = readProfileValue("HCPI");
      sHideStatus = readProfileValue("HSTATUS");
      sHideResId = readProfileValue("HRESID");
      sHideResName = readProfileValue("HRESNAME");
      
      sBase  = readProfileValue("HBASE", "H");
      
      project_id = readProfileValue("PROJECT_ID", "");	

   }

   protected void run()
   {
      if(DEBUG) debug(this+": MyActivities.run()");      

      ASPManager mgr = getASPManager();
     
      sHideStart = readProfileValue("HSTART", "F");
      sHideFinish = readProfileValue("HFINISH", "F");
      sHidePlanned = readProfileValue("HPLANNED", "F");
      sHideUsed = readProfileValue("HUSED", "F");
      sHideEarned = readProfileValue("HEARNED", "F");
      sHideProgress = readProfileValue("HPROGRESS", "F");
      sHideCPI = readProfileValue("HCPI", "F");
      sHideStatus = readProfileValue("HSTATUS", "T");
      sHideResId = readProfileValue("HRESID", "T");
      sHideResName = readProfileValue("HRESNAME", "T");

      sBase = readProfileValue("HBASE", "H");

      skip_rows = ctx.readNumber("SKIPROWS", 0);
      db_rows = ctx.readNumber("DBROWS", 0);

      String cmd = readValue("CMD");

      if( "PREV".equals(cmd) && skip_rows>=size )
      {
         skip_rows -= size;
      }
      else if( "NEXT".equals(cmd) && skip_rows<=db_rows-size )
      {
         skip_rows += size;
      }

      if (sHideStart.equalsIgnoreCase("T"))
         getASPField("EARLY_START").setHidden();	  
      else
         getASPField("EARLY_START").unsetHidden();
     
      if (sHideFinish.equalsIgnoreCase("T"))
         getASPField("EARLY_FINISH").setHidden();
      else
         getASPField("EARLY_FINISH").unsetHidden();

      if (sHidePlanned.equalsIgnoreCase("T"))
         getASPField("PLANNED").setHidden();
      else
         getASPField("PLANNED").unsetHidden();

      if (sHideUsed.equalsIgnoreCase("T"))
         getASPField("USED").setHidden();
      else
         getASPField("USED").unsetHidden();
      
      if (sHideEarned.equalsIgnoreCase("T"))
         getASPField("EARNED").setHidden();
      else
         getASPField("EARNED").unsetHidden();
     
      if (sHideProgress.equalsIgnoreCase("T"))
         getASPField("PROGRESS_HOURS_WEIGHTED").setHidden();	  
      else
         getASPField("PROGRESS_HOURS_WEIGHTED").unsetHidden();

      if (sHideCPI.equalsIgnoreCase("T"))
         getASPField("CPI").setHidden();
      else
         getASPField("CPI").unsetHidden();
     
      if (sHideStatus.equalsIgnoreCase("T"))
         getASPField("STATE").setHidden();	  
      else
         getASPField("STATE").unsetHidden();	
     
      if (sHideResId.equalsIgnoreCase("T"))
         getASPField("ACTIVITY_RESPONSIBLE").setHidden();	  
      else
         getASPField("ACTIVITY_RESPONSIBLE").unsetHidden();	
     
      if (sHideResName.equalsIgnoreCase("T"))
         getASPField("RESPONSIBLE_NAME").setHidden();	  
      else
         getASPField("RESPONSIBLE_NAME").unsetHidden();	  
     
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      ASPQuery             qry   = trans.addQuery(blk);

      qry.setOrderByClause("PROJECT_ID,ACTIVITY_NO");
      qry.setBufferSize(size);
      qry.skipRows(skip_rows);
      qry.includeMeta("ALL");

      where_stmt = null;
      if (incplan)
      {
         if (Str.isEmpty(where_stmt))
            where_stmt = " 'Planned'";
         else
            where_stmt = where_stmt + " ,'Planned'";
      }
      if (increl)
      {
         if (Str.isEmpty(where_stmt))
            where_stmt = " 'Released'";
         else
            where_stmt = where_stmt + " ,'Released'";
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
            qry.addWhereCondition("'1' = '2'");
         else
            where_stmt = " PERSON_INFO_API.Get_User_Id(ACTIVITY_RESPONSIBLE) = Fnd_Session_API.Get_Fnd_User AND objstate in (" + where_stmt + ")";
      }
      else
      {
         if (Str.isEmpty(where_stmt))
            qry.addWhereCondition("'1' = '2'");
         else
            where_stmt = " objstate in (" + where_stmt + ") ";
      }

      if (incstart)
      {
          if ("UpToDay".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " EARLY_START <= sysdate AND EARLY_FINISH >= sysdate ";
               else
                  where_stmt = where_stmt + " AND EARLY_START <= sysdate AND EARLY_FINISH >= sysdate ";
            }
         else if ("NextWeek".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " EARLY_START <= sysdate + 7 AND EARLY_START >= sysdate ";
               else
                  where_stmt = where_stmt + " AND EARLY_START <= sysdate + 7 AND EARLY_START >= sysdate ";
            }
         else if ("Next2Week".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " EARLY_START <= sysdate + 14 AND EARLY_START >= sysdate ";
               else
                  where_stmt = where_stmt + " AND EARLY_START <= sysdate + 14 AND EARLY_START >= sysdate ";
            }
         else if ("Next3Week".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " EARLY_START  <= sysdate + 21 AND EARLY_START >= sysdate ";
               else
                  where_stmt = where_stmt + " AND EARLY_START <= sysdate + 21 AND EARLY_START >= sysdate ";
            }
         else if ("NextMonth".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " EARLY_START <= sysdate + 30 AND EARLY_START >= sysdate ";
               else
                  where_stmt = where_stmt + " AND EARLY_START <= sysdate + 30 AND EARLY_START >= sysdate ";
            }
         else if ("Next2Month".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " EARLY_START <= sysdate + 60 AND EARLY_START >= sysdate ";
               else
                  where_stmt = where_stmt + " AND EARLY_START <= sysdate + 60 AND EARLY_START >= sysdate ";
            }
         else if ("Next3Month".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " EARLY_START <= sysdate + 90 AND EARLY_START >= sysdate ";
               else
                  where_stmt = where_stmt + " AND EARLY_START <= sysdate + 90 AND EARLY_START >= sysdate ";
            }
         else if ("More3Month".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = "EARLY_START - sysdate >= 120 ";
               else
                  where_stmt = where_stmt + " AND EARLY_START - sysdate >= 120 ";
            }
      }
      if (incfini)
      {
          if ("UpToDay".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " EARLY_FINISH <= SYSDATE ";
               else
                  where_stmt = where_stmt + " AND EARLY_FINISH <= SYSDATE ";
            }
         else if ("NextWeek".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " sysdate + 7 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
               else
                  where_stmt = where_stmt + " AND sysdate + 7 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
            }
         else if ("Next2Week".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " sysdate + 14 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
               else
                  where_stmt = where_stmt + " AND sysdate + 14 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
            }
         else if ("Next3Week".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " sysdate + 21 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
               else
                  where_stmt = where_stmt + " AND sysdate + 21 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
            }
         else if ("NextMonth".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " sysdate + 30 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
               else
                  where_stmt = where_stmt + " AND sysdate + 30 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
            }
         else if ("Next2Month".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " sysdate + 60 <= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
               else
                  where_stmt = where_stmt + " AND sysdate + 60 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
            }
         else if ("Next3Month".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " sysdate + 90 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
               else
                  where_stmt = where_stmt + " AND sysdate + 90 >= EARLY_FINISH AND EARLY_FINISH >= SYSDATE ";
            }
         else if ("More3Month".equals(date_int) )
            {
               if (Str.isEmpty(where_stmt))
                  where_stmt = " sysdate + 120 <= EARLY_FINISH ";
               else
                  where_stmt = where_stmt + " AND sysdate + 120 <= EARLY_FINISH ";
            }
      }

      String view_name = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Current"+ProfileUtils.ENTRY_SEP+"Page Node", false);
      project_id = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Available/"+view_name+ProfileUtils.ENTRY_SEP+"Page Node"+"/PROJID", false);
      if (!(mgr.isEmpty(project_id)))
         projectportal= true;
      else
         project_id = readProfileValue("PROJECT_ID");

      if( projectportal )
      {
         if (Str.isEmpty(where_stmt))
	 {
             qry.addWhereCondition("PROJECT_ID = ? ");
             qry.addParameter("PROJECT_ID",project_id);
         }
         else
	 {
	     where_stmt = " PROJECT_ID = ? AND (" + where_stmt + ")";
             qry.addParameter(this,"PROJECT_ID",project_id);
	 }
	    
      }

      if(DEBUG) debug("\tinit(): ======== current values =======:\n\t\twhere_stmt="+where_stmt);

      if (Str.isEmpty(where_stmt))
         qry.addWhereCondition("'1' = '2'");
      else
         qry.addWhereCondition( where_stmt );
       
      qry.setSelectExpression("PLANNED","PROJECT_CONNECTION_DETAILS_API.Get_Planned_Baseline(ACTIVITY_SEQ,'" + sBase + "')");
      qry.setSelectExpression("USED","PROJECT_CONNECTION_DETAILS_API.Get_Used(ACTIVITY_SEQ,'" + sBase + "')");

      submit(trans);
      db_rows = blk.getASPRowSet().countDbRows();

      trans.clear();

      ctx.writeNumber("SKIPROWS", skip_rows);
      ctx.writeNumber("DBROWS", db_rows);

   }

   //==========================================================================
   //  
   //==========================================================================

   public String getTitle( int mode )
   {
      if(DEBUG) debug(this+": MyActivities.getTitle("+mode+")");


      if ("H".equalsIgnoreCase(sBase))
       sBaseHeader = translate("PROJWPORTLETSMYACTIVITIESHOURSHEADER: Hour Based");
     else
       sBaseHeader = translate("PROJWPORTLETSMYACTIVITIESCOSHEADERT: Cost Based");
      
     if (Str.isEmpty(my_title))
     {
          if(mode==ASPPortal.MINIMIZED)
         {
            if (db_rows==1)
               return translate(getDescription()) + " - " + sBaseHeader + " " + translate("PROJWPORTLETSMYACTIVITIESONE:  - You have &1 Activity.", Integer.toString(db_rows));
            else
               return translate(getDescription()) + " - " + sBaseHeader + " " + translate("PROJWPORTLETSMYACTIVITIESMORE:  - You have &1 Activities.", Integer.toString(db_rows));
         }
          else
         {
           return translate(getDescription()) + " - " + sBaseHeader;
        }
     }
     else
     {
        if(mode==ASPPortal.MINIMIZED)
         {
            if (db_rows==1)
               return my_title + translate("PROJWPORTLETSMYACTIVITIESONE:  - You have &1 Activity.", Integer.toString(db_rows));
            else
               return my_title + translate("PROJWPORTLETSMYACTIVITIESMORE:  - You have &1 Activities.", Integer.toString(db_rows));
         }
          else
         {
            return my_title;
        }
     } 
   }
   
   public void printContents() throws FndException
   {
      if(DEBUG)
      {
         debug(this+": MyActivities.getContents()");
      }

      // hidden field for next and previous links
      printHiddenField("CMD","");
      if (rowset.countRows() == 0 || rowset == null) 
     {
       printNewLine();
       appendToHTML("<a href=\"javascript:customizeBox('"+getId()+"')\">");
       printText("PROJWPORTLETSMYACTIVITIESCUST: Customize this portlet");
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
            printSubmitLink("PROJWPORTLETSMYACTIVITIESPRV: Previous","prevCust");
         else
            printText("PROJWPORTLETSMYACTIVITIESTPRV: Previous");

         printSpaces(5);
         String rows = translate("PROJWPORTLETSMYACTIVITIESROWS: (Rows &1 to &2 of &3)",
                                 (skip_rows+1)+"",
                                 (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                 db_rows+"");
         printText( rows );
         printSpaces(5);

         if(skip_rows<db_rows-size)
            printSubmitLink("PROJWPORTLETSMYACTIVITIESNXT: Next","nextCust");
         else
            printText("PROJWPORTLETSMYACTIVITIESNXT: Next");

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
     appendToHTML("<table border=0 cellpadding=0 cellspacing=0 width='100%'>");
     appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'>");

     appendToHTML("<table border=0 cellpadding=0 cellspacing=0>");
     appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'>");
     
      ASPManager           mgr   = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPBuffer  buf = mgr.newASPTransactionBuffer();
      ASPBuffer  b   = mgr.newASPTransactionBuffer();

     printNewLine();
     printText("PROJWPORTLETSMYACTIVITIESTITLE: Portlet Label: ");
     printField("MY_TITLE",my_title,40,60);	
     printNewLine();
     
      printNewLine();
      printText("PROJWPORTLETSMYACTIVITIESETHEAD:  Make your selections:");
      printNewLine();
      printCheckBox("ONLYMINE", onlymine);
      printText("PROJWPORTLETSMYACTIVITIESMINE:  Show only Activities that I am responsible for");
      printNewLine();
      printNewLine();

      printText("PROJWPORTLETSMYACTIVITIESHEAD2:  Select statuses to include:");
      printNewLine();

      printCheckBox("INCPLAN", incplan);
      printText("PROJWPORTLETSMYACTIVITIESPLAN:  Planned");
      printNewLine();

      printCheckBox("INCREL", increl);
      printText("PROJWPORTLETSMYACTIVITIESWAPP:  Released");
      printNewLine();

      printCheckBox("INCCOMP", inccomp);
      printText("PROJWPORTLETSMYACTIVITIESCOMP:  Completed");
      printNewLine();
      
      printCheckBox("INCCLOSE", incclose);
      printText("PROJWPORTLETSMYACTIVITIESCLOSE:  Closed");
      printNewLine();

      printCheckBox("INCCAN", inccan);
      printText("PROJWPORTLETSMYACTIVITIESWCANC:  Cancelled");
      printNewLine();

      printNewLine();
      printNewLine();

      printCheckBox("INCSTART", incstart);
      printText("PROJWPORTLETSMYACTIVITIESRLYSTART:  Early Start planned in period");
      printNewLine();

      printCheckBox("INCFINI", incfini);
      printText("PROJWPORTLETSMYACTIVITIESYFINISH:  Early Finish planned in period");
      printNewLine();


      b = buf.addBuffer("Data");
      b.setValue("Label", "UpToDay");   
      b.setValue("Value", translate(transl_prefix+"PROJWPORTLETSMYACTIVITIESONGOING: On Going/Finished"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "NextWeek");   
      b.setValue("Value", translate(transl_prefix+"PROJWPORTLETSMYACTIVITIESNEXTWEEKS: Next Week"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Next2Week");   
      b.setValue("Value", translate(transl_prefix+"PROJWPORTLETSMYACTIVITIESNEXT2WEEKS: Next 2 Weeks"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Next3Week");   
      b.setValue("Value", translate(transl_prefix+"PROJWPORTLETSMYACTIVITIESNEXT3WEEKS: Next 3 Weeks"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "NextMonth");   
      b.setValue("Value", translate(transl_prefix+"PROJWPORTLETSMYACTIVITIESNEXTMONTH: Next Month"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Next2Month");   
      b.setValue("Value", translate(transl_prefix+"PROJWPORTLETSMYACTIVITIESNEXT2MOTHS: Next 2 Months"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Next3Month");   
      b.setValue("Value", translate(transl_prefix+"PROJWPORTLETSMYACTIVITIESNEXT3MOTHS: Next 3 Months"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "More3Month");   
      b.setValue("Value", translate(transl_prefix+"PROJWPORTLETSMYACTIVITIESMORE3MOTHS: More than 3 Months"));   

      printNewLine();
      printSpaces(5);
      printText(transl_prefix+": Select period: ");
      printNewLine();
      printNewLine();
      printSpaces(5);
      printSelectBox("DATE_INT", buf, date_int);
      printNewLine();
      printNewLine();

      trans = mgr.perform(trans);
     
     appendToHTML("</td></tr></tbody></table>");


      // Customize table fileds.
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     appendToHTML("<table border=0 cellpadding=0 cellspacing=0>");
     appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'><br>");
     printNewLine();
     printText("PROJWPORTLETSMYACTIVITIESMODE: Select mode:");
     printNewLine();

      printRadioButton("BASE","H", "Hour Based", "H".equalsIgnoreCase(sBase)?true:false);
     printNewLine();
     printRadioButton("BASE","C", "Cost Based", "C".equalsIgnoreCase(sBase)?true:false);
     printNewLine();
     printNewLine();
     
     
     printText("PROJWPORTLETSMYACTIVITIESIELDS: Select fields to show in the table:");
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'><br>");
     
     printText("PROJWPORTLETSMYACTIVITIESSTART: Start");
     appendToHTML("</td><td style='FONT: 8pt Verdana'><br>");
     printCheckBox("CHECK_START","T".equalsIgnoreCase(sHideStart)?false:true);
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
     
     printText("PROJWPORTLETSMYACTIVITIESFIN: Finish");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_FINISH","T".equalsIgnoreCase(sHideFinish)?false:true);
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      printText("PROJWPORTLETSMYACTIVITIESPLANNEDCUS: Planned");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_PLANNED","T".equalsIgnoreCase(sHidePlanned)?false:true);
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      printText("PROJWPORTLETSMYACTIVITIESUSEDCUS: Used");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_USED","T".equalsIgnoreCase(sHideUsed)?false:true);
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      printText("PROJWPORTLETSMYACTIVITIESRNEDCUS: Earned");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_EARNED","T".equalsIgnoreCase(sHideEarned)?false:true);
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      printText("PROJWPORTLETSMYACTIVITIESPROG: Progress");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_PROGRESS","T".equalsIgnoreCase(sHideProgress)?false:true);
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      printText("PROJWPORTLETSMYACTIVITIESCPICUS: CPI");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_CPI","T".equalsIgnoreCase(sHideCPI)?false:true);
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
     
     printText("PROJWPORTLETSMYACTIVITIESTATE: Status");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_STATUS","T".equalsIgnoreCase(sHideStatus)?false:true);
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
     
     printText("PROJWPORTLETSMYACTIVITIESRESP: Responsible Id");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_RES_ID","T".equalsIgnoreCase(sHideResId)?false:true);
     appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");
     
     printText("PROJWPORTLETSMYACTIVITIESSPNAME: Responsible Name");
     appendToHTML("</td><td style='FONT: 8pt Verdana'>");
     printCheckBox("CHECK_RES_NAME","T".equalsIgnoreCase(sHideResName)?false:true);
     appendToHTML("</td></tr></tbody></table>");

     appendToHTML("</td></tr></tbody></table>");	  
   }


   public void submitCustomization()
   {
     my_title	  = readValue("MY_TITLE");
     writeProfileValue("MY_TITLE", readAbsoluteValue("MY_TITLE"));
      
      String value = readValue("ONLYMINE");
      onlymine = "TRUE".equals(value);
      writeProfileFlag("ONLYMINE", onlymine);

      incplan   = "TRUE".equals(readValue("INCPLAN"));
      writeProfileFlag("INCPLAN", incplan);
      increl    = "TRUE".equals(readValue("INCREL"));
      writeProfileFlag("INCREL", increl);
      inccomp   = "TRUE".equals(readValue("INCCOMP"));
      writeProfileFlag("INCCOMP", inccomp);
      incclose  = "TRUE".equals(readValue("INCCLOSE"));
      writeProfileFlag("INCCLOSE", incclose);
      
      inccan    = "TRUE".equals(readValue("INCCAN"));
      writeProfileFlag("INCCAN", inccan);

      incstart   = "TRUE".equals(readValue("INCSTART"));
      writeProfileFlag("INCSTART", incstart);
      incfini    = "TRUE".equals(readValue("INCFINI"));
      writeProfileFlag("INCFINI", incfini);

      date_int = readValue("DATE_INT", date_int);
      writeProfileValue("DATE_INT", date_int );
     
     sHideStart     = "TRUE".equalsIgnoreCase(readValue("CHECK_START"))?"F":"T";
     sHideFinish    = "TRUE".equalsIgnoreCase(readValue("CHECK_FINISH"))?"F":"T";
     sHidePlanned   = "TRUE".equalsIgnoreCase(readValue("CHECK_PLANNED"))?"F":"T";
     sHideUsed      = "TRUE".equalsIgnoreCase(readValue("CHECK_USED"))?"F":"T";
     sHideEarned    = "TRUE".equalsIgnoreCase(readValue("CHECK_EARNED"))?"F":"T";
     sHideProgress  = "TRUE".equalsIgnoreCase(readValue("CHECK_PROGRESS"))?"F":"T";
     sHideCPI       = "TRUE".equalsIgnoreCase(readValue("CHECK_CPI"))?"F":"T";
     sHideStatus    = "TRUE".equalsIgnoreCase(readValue("CHECK_STATUS"))?"F":"T";
     sHideResId     = "TRUE".equalsIgnoreCase(readValue("CHECK_RES_ID"))?"F":"T";
     sHideResName   = "TRUE".equalsIgnoreCase(readValue("CHECK_RES_NAME"))?"F":"T";

      sBase          = readValue("BASE");


      writeProfileValue("HSTART", sHideStart );
      writeProfileValue("HFINISH", sHideFinish );
     writeProfileValue("HPLANNED", sHidePlanned );
     writeProfileValue("HUSED", sHideUsed );
     writeProfileValue("HEARNED", sHideEarned );
      writeProfileValue("HPROGRESS", sHideProgress );
     writeProfileValue("HCPI", sHideCPI );
      writeProfileValue("HSTATUS", sHideStatus );
      writeProfileValue("HRESID", sHideResId );
      writeProfileValue("HRESNAME", sHideResName );

      writeProfileValue("HBASE", sBase);
   }
   
   public static String getDescription()
   {
      return "PROJWPORTLETSMYACTIVITIESYACT: My Activities";
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
      ASPManager mgr = getASPManager();
      Integer null_int = new Integer(0);

      if ("H".equalsIgnoreCase(sBase))
       sBaseValue = translate("PROJWPORTLETSMYACTIVITIESHOURS: Hours");
     else
       sBaseValue = translate("PROJWPORTLETSMYACTIVITIESCOST: Cost");

      beginTable();
     beginTableTitleRow();
        printTableCell(translate("PROJWPORTLETSMYACTIVITIESPROJECT: Project"));
       printTableCell(translate("PROJWPORTLETSMYACTIVITIESPSUBROJECT: Sub Project"));
       printTableCell(translate("PROJWPORTLETSMYACTIVITIESACTIV: Activity"));
       if (!getASPField("EARLY_START").isHidden())
          printTableCell(translate("PROJWPORTLETSMYACTIVITIESSTART: Start"));
       if (!getASPField("EARLY_FINISH").isHidden())
          printTableCell(translate("PROJWPORTLETSMYACTIVITIESFIN: Finish"));
       if (!getASPField("PLANNED").isHidden())
          printTableCell(translate("PROJWPORTLETSMYACTIVITIESPLANNED: Planned &1", sBaseValue));
       if (!getASPField("USED").isHidden())
          printTableCell(translate("PROJWPORTLETSMYACTIVITIESUSED: Used &1", sBaseValue));
       if (!getASPField("EARNED").isHidden())
          printTableCell(translate("PROJWPORTLETSMYACTIVITIESEARNED: Earned &1", sBaseValue));
       if (!getASPField("PROGRESS_HOURS_WEIGHTED").isHidden())
          printTableCell(translate("PROJWPORTLETSMYACTIVITIESPROG: Progress"));
       if (!getASPField("CPI").isHidden()){
          if ("H".equalsIgnoreCase(sBase))
             printTableCell(translate("PROJWPORTLETSMYACTIVITIESCPIBASE: CPI &1",sBaseValue));
         else
            printTableCell(translate("PROJWPORTLETSMYACTIVITIESCPI: CPI"));
       }	    
       if (!getASPField("STATE").isHidden())
          printTableCell(translate("PROJWPORTLETSMYACTIVITIESSTATE: Status"));
       if (!getASPField("ACTIVITY_RESPONSIBLE").isHidden())
          printTableCell(translate("PROJWPORTLETSMYACTIVITIESRESP: Responsible Id"));
       if (!getASPField("RESPONSIBLE_NAME").isHidden())
          printTableCell(translate("PROJWPORTLETSMYACTIVITIESRESPNAME: Responsible Name"));
       printTableCell("");
     endTableTitleRow();
     beginTableBody();
    
     rowset.first();
     for (int i=0; i<rowset.countRows(); i++)
     {
                    
             String url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"projw/ChildTree.page"+
                  "?PROJECT_ID="+mgr.URLEncode(rowset.getValue("PROJECT_ID"));
       
         beginTableCell();
          printLink(rowset.getValue("PROJECT_ID"),url);
         endTableCell();
         
         printTableCell(rowset.getRow().getFieldValue(this,"SUB_PROJECT_ID"));
       
              //Bug Id 85691, Start 
              String act_seq = getASPField("ACTIVITY_SEQ").convertToClientString(rowset.getValue("ACTIVITY_SEQ"));
              String url2 = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"projw/Activity.page"+ "?ACTIVITY_SEQ="+mgr.URLEncode(act_seq);
              //Bug Id 85691, End
         beginTableCell();
          printLink(rowset.getValue("ACTIVITY"),url2);
         endTableCell();


                        String earned = getASPField("EARNED").formatNumber(rowset.getRow().getNumberValue("PROGRESS_HOURS_WEIGHTED")*rowset.getRow().getNumberValue("PLANNED")); 
       
       if (!getASPField("EARLY_START").isHidden())
          printTableCell(rowset.getRow().getFieldValue(this,"EARLY_START"));
       if (!getASPField("EARLY_FINISH").isHidden())
          printTableCell(rowset.getRow().getFieldValue(this,"EARLY_FINISH"));
       if (!getASPField("PLANNED").isHidden())
          printTableCell(rowset.getRow().getFieldValue(this,"PLANNED"), "RIGHT");
       if (!getASPField("USED").isHidden())
          printTableCell(rowset.getRow().getFieldValue(this,"USED"), "RIGHT");
       if (!getASPField("EARNED").isHidden()){
          printTableCell(earned, "RIGHT");
       }
       if (!getASPField("PROGRESS_HOURS_WEIGHTED").isHidden())
          printTableCell(rowset.getRow().getFieldValue(this,"PROGRESS_HOURS_WEIGHTED"),"RIGHT");
       if (!getASPField("CPI").isHidden()){
         if ( rowset.getRow().getFieldValue(this,"USED").equals(getASPField("USED").formatNumber(null_int.doubleValue())))
         {
             if (earned.equals(getASPField("EARNED").formatNumber(null_int.doubleValue())))
               printTableCell(getASPField("CPI").formatNumber(null_int.doubleValue()),"RIGHT");
             else
               printTableCell("-","CENTER");
         }
         else
         {
            printTableCell(getASPField("CPI").formatNumber(
                                rowset.getRow().getNumberValue("PROGRESS_HOURS_WEIGHTED")*
            rowset.getRow().getNumberValue("PLANNED")/
            rowset.getRow().getNumberValue("USED")), "RIGHT");
         }
       }
       if (!getASPField("STATE").isHidden())
          printTableCell(rowset.getValue("STATE"));
       if (!getASPField("ACTIVITY_RESPONSIBLE").isHidden())
          printTableCell(rowset.getValue("ACTIVITY_RESPONSIBLE"));
       if (!getASPField("RESPONSIBLE_NAME").isHidden())
          printTableCell(rowset.getValue("RESPONSIBLE_NAME"));

       nextTableRow();
       rowset.next();
      }
     rowset.first();

     endTableBody();
     endTable();
   }   
}
