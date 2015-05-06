/*
 *                  IFS Research & Development
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
 * File        : ProjwMyTasks.java
 * Description :
 * Notes       : 
 * Files Calld :Activity.asp,ChildTree.asp,
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Shamal De Silva   2000-Oct-24  Created.
 *    KeFe              2000-Nov-16  Modified PrintCustomBody() to fit in Netscape. 
 *    Shamal            2000-Nov-21  Call Id 53865 - Displayed the activity Description under Activity column and added Sub Project column.
 *    Shamal            2000-Nov-28  Added Customize this portlet message.
 *    Jagath            2001-01-12   Call Id - 58700.
 *    Gunasiri          2001-04-30   Changed Translation Constants.
 *    Gunasiri          2001-05-31 - Project Portal Check is added.
 *    Muneera           2001-09-07 - Removed the duplicated translation constants and stndardized the non standard constants.
 *    CHAG              2002-02-11   Bug 27815, Errors occur when view descriptions contains " Project Portal " is corrected.
 *                                   and include the bug correction for Bug 27726,of Query Error.
 *    Cpeilk            2002-03-29   Bug 27938 Added Task ID to the portlet with a link to ActivityTaskOvw.page.
 *    RuRalk            2004-02-16   Bug 40534, Extracted project id from the profile variable saved under the portal.
 *
 * ----------------------------------------------------------------------------
 * ------------------------------------ EDGE ----------------------------------
 * ----------------------------------------------------------------------------
 *
 *    Saallk   2005-12-09 - Call 128800, Modified method call readGlobalProfileValue accordingly to new Web Client Foundation. 
 * ----------------------------------------------------------------------------
 *    karalk   2006-11-14 - Bug, 58216. Merged, SQL Injection.
 *		Rucslk   2007-03-21 - Platform Tidy up in Sparx - Update the method submitCustomization - use the new function readAbsoluteValue()
 *    SAALLK   2007-09-15 - Bug ID 66104, Modified connected view to block to use new view created through 66104 server correction
 *                          to improve performance.
 *    RAEKLK   2009-07-08 - Bug ID 83724, Modified init(), run(), printCustomBody() and submitCustomization()
 *                          to add a configuration options to make only non completed tasks visible.
 *    RAEKLK   2009-07-22 - Bug ID 83724, Modified printCustomBody() to change the label names.
 *
 */

package ifs.projw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;


/**
 * This is a portlet template. Put the description of your portlet here.
 * Change the name of the package and the class name. Also rename the file.
 * The file name and the class name must be identical (remember case).
 */
public class ProjwMyTasks extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.projw.portlets.ProjwMyTasks");   
   private final static int size = 10;

   //==========================================================================
   //  Instances created on page creation (immutable attributes)
   //==========================================================================

   private ASPContext ctx;
   private ASPBlock   blk;
   private ASPRowSet  rowset;
   private ASPTable   tbl;

   //==========================================================================
   //  Mutable attributes
   //==========================================================================

   private transient String  where_stmt;
   private transient int db_rows;
   private transient int  skip_rows;
   
   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient ASPTransactionBuffer trans;   
   private transient boolean  find;   
   

   //==========================================================================
   //  Profile variables (temporary)
   //==========================================================================
   
   private transient boolean isstart = true;
   private transient boolean isprogress = false;
   private transient boolean iscompleted = false;
   private transient boolean isresponsible = false;
   private transient boolean ismytask = false;
   private transient boolean isdate = false;
   //Bug ID 83724, Start
   private transient boolean isnotcompleted = false;
   //Bug ID 83724, End
   private transient String date_period;
   private transient String my_title;
   private boolean projectportal;
   private String project_id;

   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * The only mandatory function - constructor.
    * Usually you do not need to perform any action here -
    * just rename the functions to match your portlet name.
    */ 
   public ProjwMyTasks( ASPPortal portal, String clspath )
   {
      super(portal,clspath);
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Very important function. Called after the execution is completed just before
    * releasing (unlock) the page in the page pool. All temporary and user depended
    * data should be cleared here. All mutable attributes should be restored to
    * their original values here.
    */
   protected void doReset() throws FndException
   {      
      trans = null;
      where_stmt = null;
      date_period = "";
      my_title = null;
      project_id = null;

      super.doReset();
   }


   /**
    * Copy the current value of every mutable attribute to
    * its default value (pre-variable).
    * Not necessary if you do not have any mutable attributes
    * (attributes that obtain their default values during portlet definition,
    * e.g. in preDefine(), and can be changed during execution).
    */
   protected void doFreeze() throws FndException
   {     
      super.doFreeze();
   }


   /**
    * Create a new instance if all existing instances of the same page are already
    * locked in the pool. Cloning is done by copying and cloning values and objects
    * from another (currently locked) instance. All temporary variables and mutable attributes
    * should be initialized here. All page components that inherit from the 
    * ASPPoolElement class are already cloned by the super class. Just initialize
    * corresponding variables.
    */
   public ASPPoolElement clone( Object obj ) throws FndException
   {
      DEBUG = true;
      if(DEBUG) debug(this+": TemplatePortlet.clone("+obj+")");

      // clone the instance by calling super class
      ProjwMyTasks page = (ProjwMyTasks)(super.clone(obj));          
      page.trans           = null;
      page.where_stmt = null;
      page.date_period = "";
      page.my_title = null;
      page.project_id = null;

      // initialise immutable attributes
      page.ctx = page.getASPContext();
      page.blk    = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl    = page.getASPTable(tbl.getName());  
      return page;
   }

   //==========================================================================
   //  Portlet description
   //==========================================================================

   /**
    * Description that will be shown on the 'Customize Portal' page.
    * Because the key for localization must be unique for the whole IFS Application,
    * it should consist of the component name, class name and topic name,
    * e.g. ENTERPPARTYDESC. 
    */ 
   public static String getDescription()
   {
      return "PROJWPORTLETSPROJWMYTASKSSDES: My Tasks";
   }


   /**
    * Return the minimum width for the portlet.
    * This function is needed only if your portlets minimum width is different from 288,
    * which correspond to a wide portal column. This information will be used by portal
    * engine during the portal configuration.
    */
   public static int getMinWidth()
   {
      return 144;
   }

   /**
    * Create the portlet title for different modes.
    */
   public String getTitle( int mode )
   {  
      if(DEBUG) debug(this+": ProjwMyTasks.getTitle("+mode+")");

      if(mode==ASPPortal.MINIMIZED)
      {
         if (db_rows==1)
            return translate(my_title) + translate("PROJWPORTLETSPROJWMYTASKSTONE:  - You have &1 Task.", Integer.toString(db_rows));
         else
            return translate(my_title) + translate("PROJWPORTLETSPROJWMYTASKSMORE:  - You have &1 Tasks.", Integer.toString(db_rows));
      }
      else
         return translate(my_title);
   }

   //==========================================================================
   //  Configuration and initialisation
   //==========================================================================

   /**
    * Creation of all page objects. Runs only once, the first time.
    * All statements here are executed as a config_user and if you need to
    * perform any statement here against the database, the only allowed call
    * is performConfig(). The information obtain in that way will be shared
    * among all users.
    */ 
   protected void preDefine() throws FndException
   {
      if(DEBUG) debug(this+": ProjwMyTasks.preDefine()");

      ctx = getASPContext();
      
      blk = newASPBlock("MAIN");
      
      addField(blk, "PROJECT_ID").
      setLabel("PROJWPORTLETSPROJWMYTASKSROJID: Project").
      setHyperlink("projw/ChildTree.page","PROJECT_ID");
      
      addField(blk,"SUB_PROJECT_ID").
      setLabel("PROJWPORTLETSPROJWMYTASKSPROID: Sub Project");
      
      addField(blk, "ACTIVITY_SEQ").
      setHidden();
      
      addField(blk, "ACTIVITY_DESCRIPTION").				// Bug 66104
      setLabel("PROJWPORTLETSPROJWMYTASKSVITY: Activity").
      setHyperlink("projw/Activity.page","ACTIVITY_SEQ");

      addField(blk, "TASK_ID").
      setLabel("PROJWPORTLETSPROJWMYTASKSTASKID: Task ID").
      setHyperlink("projw/ActivityTaskOvw.page","TASK_ID");
      
      addField(blk, "NAME").
      setLabel("PROJWPORTLETSPROJWMYTASKSTASK: Task");                          
      
      addField(blk, "OFFSET").
      setHidden();
      
      addField(blk, "TASK_START_DATE","Date").				// Bug 66104
      setLabel("PROJWPORTLETSPROJWMYTASKSSTART: Start");
      
      addField(blk, "PROGRESS","Number","0.00##%").
      setLabel("PROJWPORTLETSPROJWMYTASKSPROGRESS: Progress");                          
      
      addField(blk, "STATUS").  
      setLabel("PROJWPORTLETSPROJWMYTASKSCOMPLETED: Completed").
      setCheckBox("0,1");
      
      addField(blk, "RESPONSIBLE").
      setHidden();
      
      addField(blk, "RESPONSIBLE_NAME").					// Bug 66104
      setLabel("PROJWPORTLETSPROJWMYTASKSRESPONSE: Responsible");

      blk.setView("ACTIVITY_TASK_EXT2");					// Bug 66104

      tbl = newASPTable( blk );

      tbl.disableQueryRow();
      tbl.disableQuickEdit();
      tbl.disableRowCounter();
      tbl.disableRowSelect();
      tbl.disableRowStatus();
      tbl.unsetSortable();
      getASPManager().newASPCommandBar(blk);
      rowset = blk.getASPRowSet(); 
     
      init();
      appendJavaScript(
      "function prevMes(obj,id)"+
      "{"+
      "   getPortletField(id,'CMD').value = 'PREV';"+
      "}\n"+
      "function nextMes(obj,id)"+
      "{"+
      "   getPortletField(id,'CMD').value = 'NEXT';"+
      "}\n");   
      appendJavaScript(
      "function findMes(obj,id)"+
      "{"+
      "   getPortletField(id,'CMD').value = 'FIND';"+
      "}\n");
   }

   /**
    * Initialisation of variables and fetching the profile information.
    * Do not perform any database related actions here due to error handling.
    */
   protected void init()
   {      
      isstart   = readProfileFlag("ISSTART", true);
      isprogress   = readProfileFlag("ISPROGRESS",false);
      iscompleted   = readProfileFlag("ISCOMPLETED", false);
      isresponsible   = readProfileFlag("ISRESPONSIBLE", false);
      ismytask = readProfileFlag("ISMYTASK", false);
      isdate  = readProfileFlag("ISDATE",false);
      date_period  = readProfileValue("DATE_PERIOD","");
      project_id  = readProfileValue("PROJECT_ID","");
      //Bug ID 83724, Start
      isnotcompleted  = readProfileFlag("ISNOTCOMPLETED", false);
      //Bug ID 83724, End

      my_title = readProfileValue("MY_TITLE",translate("PROJWPORTLETSPROJWMYTASKSTITLE: My Tasks"));
      find       = true;
      ASPContext ctx = getASPContext();

      skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
      db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );
      String cmd = readValue("CMD");

      if( "FIND".equals(cmd) )
      {
         skip_rows = 0;
         find = true;
      }
      else if( "PREV".equals(cmd) && skip_rows>=size )
      {
         skip_rows -= size;
         find = true;
      }
      else if( "NEXT".equals(cmd) && skip_rows<=db_rows-size )
      {
         skip_rows += size;
         find = true;
      }   
      ctx.writeValue("SKIP_ROWS",skip_rows+"");
   }

   //==========================================================================
   //  Normal mode
   //==========================================================================

   /**
    * Run the business logic here. Do not call submit() or perform() located
    * in ASPManager class. Use versions from the super class.
    */
   protected void run()
   {
      if(DEBUG) debug(this+": ProjwMyTasks.run");
          
          
      ASPManager mgr = getASPManager();
          
          
      if (!isstart)
         getASPField("TASK_START_DATE").setHidden();    
      else
         getASPField("TASK_START_DATE").unsetHidden();
          
      if (!isprogress)
         getASPField("PROGRESS").setHidden();
      else
         getASPField("PROGRESS").unsetHidden();
          
      if (!iscompleted)
         getASPField("STATUS").setHidden();       
      else
         getASPField("STATUS").unsetHidden(); 
          
      if (!isresponsible)
         getASPField("RESPONSIBLE_NAME").setHidden();      
      else
         getASPField("RESPONSIBLE_NAME").unsetHidden();  
          
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      ASPQuery qry   = trans.addQuery(blk);
      String myURL   = getASPManager().getPortalPage().getURL();         
      qry.includeMeta("ALL");
          
      where_stmt = null;
      date_period  = readProfileValue("DATE_PERIOD", date_period);
          
      where_stmt = ""; 
      if( ismytask )
      {
         if (Str.isEmpty(where_stmt))
            where_stmt =  "PERSON_INFO_API.Get_User_Id(RESPONSIBLE) = Fnd_Session_API.Get_Fnd_User";
         else
            where_stmt = where_stmt +"AND PERSON_INFO_API.Get_User_Id(RESPONSIBLE) = Fnd_Session_API.Get_Fnd_User";
      }                 
      else
      {         
         if (Str.isEmpty(where_stmt))
            where_stmt = "'1' = '1'";
         else
            where_stmt = where_stmt+ " AND '1' = '1'";
      }
      if(isdate)
      {
         if ("UpToDay".equals(date_period) )
         {
            if (Str.isEmpty(where_stmt))
               where_stmt = " NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate AND ACTIVITY_API.GET_EARLY_FINISH(ACTIVITY_SEQ) >= sysdate";
            else
               where_stmt = where_stmt + " AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate AND ACTIVITY_API.GET_EARLY_FINISH(ACTIVITY_SEQ) >= sysdate ";
         }
         else if ("NextWeek".equals(date_period) )
         { 
            if (Str.isEmpty(where_stmt))
               where_stmt = " NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 7 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) >= sysdate";
            else
               where_stmt = where_stmt + " AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 7 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) >= sysdate";
         }
         else if ("Next2Week".equals(date_period) )
         {
            if (Str.isEmpty(where_stmt))
               where_stmt = " NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 14 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) >= sysdate ";
            else
               where_stmt = where_stmt + " AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 14 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) >= sysdate ";
         }
         else if ("Next3Week".equals(date_period) )
         {
            if (Str.isEmpty(where_stmt))
               where_stmt = " NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ))  <= sysdate + 21 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ))  >= sysdate";
            else
               where_stmt = where_stmt + " AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 21 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ))  >= sysdate ";
         }
         else if ("NextMonth".equals(date_period) )
         {
            if (Str.isEmpty(where_stmt))
               where_stmt = " NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 30 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) >= sysdate";
            else
               where_stmt = where_stmt + " AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 30 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) >= sysdate";
         }
         else if ("Next2Month".equals(date_period) )
         {
            if (Str.isEmpty(where_stmt))
               where_stmt = " NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 60 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) >= sysdate";
            else
               where_stmt = where_stmt + " AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 60 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ))  >= sysdate ";
         }
         else if ("Next3Month".equals(date_period) )
         {
            if (Str.isEmpty(where_stmt))
               where_stmt = " NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 90 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) >= sysdate";
            else
               where_stmt = where_stmt + " AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) <= sysdate + 90 AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) >= sysdate ";
         }
         else if ("More3Month".equals(date_period) )
         {
            if (Str.isEmpty(where_stmt))
               where_stmt = "NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) - sysdate >= 120 ";
            else
               where_stmt = where_stmt + " AND NVL(ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)+OFFSET,ACTIVITY_API.GET_EARLY_START(ACTIVITY_SEQ)) - sysdate >= 120 ";
         }
      }
      //Bug ID 83724, Start
      if( isnotcompleted )
      {
         if (Str.isEmpty(where_stmt))
            where_stmt =  "STATUS = 0";
         else
            where_stmt = where_stmt +" AND STATUS = 0";
      }
      //Bug ID 83724, End
      String view_name = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Current"+ProfileUtils.ENTRY_SEP+"Page Node", false);
      project_id = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Available/"+view_name+ProfileUtils.ENTRY_SEP+"Page Node"+"/PROJID", false);
      if (!(mgr.isEmpty(project_id)))
      {
         projectportal= true;
         qry.addWhereCondition("PROJECT_ID = ? ");
         qry.addParameter(this,"PROJECT_ID",project_id);

      }
      else
      {
         project_id = readProfileValue("PROJECT_ID");
         projectportal = false;
      }
      DEBUG = true;
      if(DEBUG) debug("\tinit(): ======== current values =======:\n\tisDate="+date_period);
      if (Str.isEmpty(where_stmt))
         qry.addWhereCondition("'1' = '2'");// display no Tasks
      else
         qry.addWhereCondition( where_stmt );
  
      qry.setBufferSize(10);
      qry.skipRows(skip_rows);
      submit(trans);
      db_rows = blk.getASPRowSet().countDbRows();         
      getASPContext().writeValue("DB_ROWS", db_rows+"" );
      trans.clear();       
   }

   /**
    * Print the HTML contents of the portlet. Do not put business logic here.
    * Use functions located in the super class for creation of the HTML code.
    */
   public void printContents()
   {      
      if(DEBUG) debug(this+": ProjwMyTasks.printContents");
      printHiddenField("CMD","");         
      if (find) 
      {
         if (rowset.countRows() == 0 || rowset == null) 
         {
            printNewLine();
            appendToHTML("<a href=\"javascript:customizeBox('"+getId()+"')\">");
            printText("PROJWPORTLETSPROJWMYTASKSCUST: Customize this portlet");
            appendToHTML("</a>");                                
            printNewLine();;
         }
         else
         {
            printTable(tbl);
            printNewLine();
            if(skip_rows>0)
               printSubmitLink("PROJWPORTLETSPROJWMYTASKSPRV: Previous","prevMes");
            else
               printText("PROJWPORTLETSPROJWMYTASKSPRV: Previous");
            printSpaces(5);
            int tempval;
            if((skip_rows<db_rows)&& ((db_rows - skip_rows)<10))
               tempval = db_rows -skip_rows;
            else
               tempval = 10; 
            String rows_state = translate("PROJWPORTLETSPROJWMYTASKSROWS: (Rows &1 to &2 of &3)",
                                                                        (skip_rows+1)+"",
                                                                        (skip_rows<db_rows?skip_rows+tempval:db_rows)+"",
                                                                        db_rows+"");
            printText( rows_state );
            printSpaces(5);
            if(skip_rows+tempval<db_rows)
               printSubmitLink("PROJWPORTLETSPROJWMYTASKSNXT: Next","nextMes");
            else
               printText("PROJWPORTLETSPROJWMYTASKSNXT: Next");
            printNewLine();
            printNewLine();
         }
      }
      else 
      printNewLine();             

   }

   //==========================================================================
   //  Customize mode
   //==========================================================================

   /**
    * If the portlet should be customisable this function must return true.
    * Remove the function otherwise.
    */
   public boolean canCustomize()
   {
      return true;
   }
  

   /**
    * Run the business logic for the customize mode. See the run() method.
    */
   public void runCustom()
   {
      trans = getASPManager().newASPTransactionBuffer();     
   }


   /**
    * Print the HTML code for the customize mode. See the printContents() function.
    */
   public void printCustomBody()throws FndException                                                                             
   {
          if(DEBUG) debug(this+": ProjwMyTasks.printCustomBody");
      ASPManager           mgr   = getASPManager();
      trans = mgr.newASPTransactionBuffer();
           
      ASPBuffer  buf = mgr.newASPTransactionBuffer();
      ASPBuffer  b   = mgr.newASPTransactionBuffer();
           
      b = buf.addBuffer("Data");
      b.setValue("Label", "UpToDay");   
      b.setValue("Value", translate("PROJWPORTLETSPROJWMYTASKSONGOING: Ongoing"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "NextWeek");   
      b.setValue("Value", translate("PROJWPORTLETSPROJWMYTASKSNEXTWEEKS: Next Week"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Next2Week");   
      b.setValue("Value", translate("PROJWPORTLETSPROJWMYTASKSNEXT2WEEKS: Next 2 Weeks"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Next3Week");   
      b.setValue("Value", translate("PROJWPORTLETSPROJWMYTASKSNEXT3WEEKS: Next 3 Weeks"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "NextMonth");   
      b.setValue("Value", translate("PROJWPORTLETSPROJWMYTASKSNEXTMONTH: Next Month"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Next2Month");   
      b.setValue("Value", translate("PROJWPORTLETSPROJWMYTASKSNEXT2MOTHS: Next 2 Months"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Next3Month");   
      b.setValue("Value", translate("PROJWPORTLETSPROJWMYTASKSNEXT3MOTHS: Next 3 Months"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "More3Month");   
      b.setValue("Value", translate("PROJWPORTLETSPROJWMYTASKSMORE3MOTHS: More than 3 Months"));      
           
          beginTable();
          beginTableBody();
                
                                                   
                beginTableCell();
                beginTable();
                beginTableBody();
                        printNewLine();
                        setFontStyle(BOLD);
                        printSpaces(5);                 
                        printText("PROJWPORTLETSPROJWMYTASKSCUSTOM: Make Your Selections: ");                   
                        printNewLine();
                        endFont();      
           
                        printNewLine();
                        printSpaces(5);
                        printText("PROJWPORTLETSPROJWMYTASKSLABEL: Portlet Label:");
                        printSpaces(5);
                        printField("MY_TITLE",my_title,20,40);
                        printNewLine();
                        
                        //Bug ID 83724, Start
                        printNewLine();
                        printSpaces(5);
                        printCheckBox("ISNOTCOMPLETED",isnotcompleted);
                        printText("PROJWPORTLETSPROJWMYTASKSNOTCOMPLETE: Show only the tasks that are not completed yet"); 
                                                
                        printNewLine();
                        printSpaces(5);
                        printCheckBox("ISMYTASK",ismytask);
                        printText("PROJWPORTLETSPROJWMYTASKSRESPONACT: Show only the tasks that I am responsible for"); 
                        //Bug ID 83724, End
                        
                        printNewLine();
                        printSpaces(5);
                        printCheckBox("ISDATE",isdate);
                        printText("PROJWPORTLETSPROJWMYTASKSDATESEL: Task Start Date planned in period"); 
                        printNewLine();
                        
                        printNewLine();
                        printSpaces(5);    
                        printText("PROJWPORTLETSPROJWMYTASKSPERIOD: Selected period:"); 
                        printSpaces(5);
                        printSelectBox("DATE_PERIOD", buf, date_period);

                        printNewLine();
                endTableBody();
                endTable();
                endTableCell();
                   
                beginTableCell();
                beginTable();      
                beginTableBody();
                        printNewLine();
                        printText("PROJWPORTLETSPROJWMYTASKSFILDSEL: Select fields to shown in the table: ");
                        printNewLine();
                        printNewLine();
          
                        printCheckBox("ISSTART",isstart);
                        printText("PROJWPORTLETSPROJWMYTASKSTARFLD: Start"); 
                        printNewLine();
          
                        printCheckBox("ISPROGRESS",isprogress);
                        printText("PROJWPORTLETSPROJWMYTASKSPROGFLD: Progress"); 
                        printNewLine();
          
                        printCheckBox("ISCOMPLETED",iscompleted);
                        printText("PROJWPORTLETSPROJWMYTASKSCOMPFLD: Completed"); 
                        printNewLine();
          
                        printCheckBox("ISRESPONSIBLE",isresponsible);
                        printText("PROJWPORTLETSPROJWMYTASKSRESPFLD: Responsible"); 
                        printNewLine();
                        printNewLine();
           
                endTableBody();
                endTable();
                endTableCell();
                   
          endTableBody();        
          endTable();
   }


   /**
    * Save values of variables to profile and context if the user presses OK button.
    * This function will be run during the next request before the ordinary run() function.
    */
   public void submitCustomization()
   {
          skip_rows = 0;
      getASPContext().writeValue("SKIP_ROWS","0");
          
          my_title = readValue("MY_TITLE", my_title);
      writeProfileValue("MY_TITLE", readAbsoluteValue("MY_TITLE"));
          
          isstart   = "TRUE".equals(readValue("ISSTART"));
      writeProfileFlag("ISSTART", isstart);
          
          isprogress   = "TRUE".equals(readValue("ISPROGRESS"));
      writeProfileFlag("ISPROGRESS", isprogress);
          
          iscompleted   = "TRUE".equals(readValue("ISCOMPLETED"));
      writeProfileFlag("ISCOMPLETED", iscompleted);
          
          isresponsible   = "TRUE".equals(readValue("ISRESPONSIBLE"));
      writeProfileFlag("ISRESPONSIBLE", isresponsible);
          
          ismytask   = "TRUE".equals(readValue("ISMYTASK"));
      writeProfileFlag("ISMYTASK", ismytask);
          
          isdate   = "TRUE".equals(readValue("ISDATE"));
      writeProfileFlag("ISDATE", isdate);
          
          date_period = readValue("DATE_PERIOD", date_period);
      writeProfileValue("DATE_PERIOD", date_period);      
      
      //Bug ID 83724, Start
      isnotcompleted = "TRUE".equals(readValue("ISNOTCOMPLETED"));
      writeProfileFlag("ISNOTCOMPLETED", isnotcompleted);
      //Bug ID 83724, End
   }
   
   //==========================================================================
   //  Other methods
   //==========================================================================

   private String nvl( String str, String instead_of_empty )
   {
      return Str.isEmpty(str) ? instead_of_empty : str;
   }
   
}
