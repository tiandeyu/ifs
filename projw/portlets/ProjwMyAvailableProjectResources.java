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
 * File         : ProjwMyAvailableProjectResources.java
 * Description  : My Available Project Resources
 * Notes        :
 * ----------------------------------------------------------------------------
 * Author       : Jagath Jayasinghe.
 * Date created : 30-10-2000
 * Files Called : 
 * ----------------------------------------------------------------------------
 * Modified By	:
 *    Date 		   Sign				      History 
 *  28-Nov-2000   Shamal         Added Customize this portlet message.
 *  28-Dec-2000   Jagath         Fixed Call Id - 58078.
 *  30-April-2001 Gunasiri  Changed Translation Constants.
 *  21-May-2001   Enocha    Marasinghe bug id. #64714 
 *  07-Sep-2001   Muneera   Removed the duplicated translation constants and stndardized the non standard constants.
 *  24-Oct-2003   KrSilk    Call 109070; Replaced the profile contant "TITLE" with "MY_TITLE" to make 
 *                          the Portlet Label value retained.
 *  23-Aug-2004   Ishelk    Restriction of maximum no of periods were increased to 12. 
 *  14-Nov-2006   KARALK    Bug 58216. Merged SQL Injection.
 *	 21-Mar-2007	Rucslk	 Platform Tidy up in Sparx - Update the method submitCustomization - use the new function readAbsoluteValue() 	
 * ----------------------------------------------------------------------------
 *
 */

package ifs.projw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

public class ProjwMyAvailableProjectResources extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.projw.portlets.ProjwMyAvailableProjectResources");

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

   private transient String title;

   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient ASPTransactionBuffer trans;
   private transient ASPTransactionBuffer transW;
   private transient ASPTransactionBuffer transQ;
   private String where_stmt;
   private String myURL;
   private String url;
   private String weekMonth;
   private boolean whileFlag;

   private ASPBuffer buff;



   //==========================================================================
   //  Profile variables (temporary)
   //==========================================================================

   private transient String project_id;
   private transient String resource_category;
   private transient String cus_title;
   private transient int no_of_periods;
   private transient String str_no_of_periods;
   private transient String green;
   private transient String red;

   private double ngreen;
   private double nred;


   private int nCalcWeek;

   private int nCurrentWeek;
   private int nCurrentMonth;

   //==========================================================================
   //  Construction
   //==========================================================================

   public ProjwMyAvailableProjectResources( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }   

   //==========================================================================
   //  Portlet description
   //==========================================================================

   public static String getDescription()
   {
      return "PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESAVAILPROJ: My Available Project Resources";
   }  

   public String getTitle( int mode )
   {
      if ( mode==MINIMIZED || mode==MAXIMIZED )
         return title;
      else
         return translate("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESCUSTT: Customize My Available Project Resources");      
   }

   public String getBoxStyle( int mode )
   {
      return "";
   }

   //==========================================================================
   //  Configuration and initialisation
   //==========================================================================

   protected void preDefine()
   {
      ctx = getASPContext();

      blk = newASPBlock("MAIN");

      addField(blk, "VERSION").
      setHyperlink("Projw/ResourceCapacityCalculation.page","VERSION").
      setLabel("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESVERSION: Resource Calculation");

      addField(blk, "PERIOD_TYPE").setFunction("''").
      setLabel("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESTYPE: Period Type");

      addField(blk, "PERIOD_NO","Number").
      setLabel("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESDNO: period No");

      addField(blk, "RESOURCE_ID").
      setLabel("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESRCEID: Resource Group");

      addField(blk, "COMPANY").
      setLabel("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESCOMPANY: Company");

      addField(blk, "RESOURCE_ID_DESC").
      setFunction("PROJ_RESOURCE_GROUP_API.Get_Description(RESOURCE_ID,COMPANY)");

      addField(blk, "UTILISATION","Number","#");

      addField(blk, "DUMMRS").setFunction("''");

      addField(blk, "WEEK_NO").setFunction("''");

      addField(blk, "CURRENT_WEEK_NO","Number").setFunction("Prjrep_Web_Util_API.Get_Current_Week");

      addField(blk, "PERIOD_YEAR");

      addField(blk, "DRID","Number").setFunction("''");

      addField(blk, "CURRMONTH","Number").setFunction("''");

      addField(blk, "CURRYEAR","Number").setFunction("''");

      blk.setView("PROJ_RESOURCE_SUMMARY");  

      tbl = newASPTable(blk);
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

   protected void init()
   {
      project_id = readProfileValue("VERSION", "");
      green = readProfileValue("GREEN", "");
      red = readProfileValue("RED", "");
      resource_category = readProfileValue("RESOURCE_CATEGORY_VALUE", ""); 
      cus_title = readProfileValue("MY_TITLE", translate("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESAVAILPROJ: My Available Project Resources"));
      str_no_of_periods = readProfileValue("NO_OF_PERIODS", "");   



      if (Str.isEmpty(str_no_of_periods))
         no_of_periods = 12;
      else
         no_of_periods = (int)(Integer.parseInt(str_no_of_periods.valueOf(str_no_of_periods)));    
   }

   //==========================================================================
   //  Normal mode
   //==========================================================================

   protected void run()
   {
      ASPManager mgr  = getASPManager(); 
      trans      = mgr.newASPTransactionBuffer();
      transW    = mgr.newASPTransactionBuffer();
      myURL     = mgr.getPortalPage().getURL();   
      int currYear;
      int nextYear;
      String strCategWstmt;


      if (Str.isEmpty(cus_title))
         title = translate("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESPROJRES: My Available Project Resources");
      else
         title = cus_title;

      if (Str.isEmpty(resource_category))
         strCategWstmt = "AND RESOURCE_ID IN (select RESOURCE_ID from PROJ_RESOURCE_GROUP  where (RESOURCE_CATEGORY is null))";
      else if (resource_category.equals("%"))
         strCategWstmt = "";
      else
         strCategWstmt = "AND RESOURCE_ID IN (select RESOURCE_ID from PROJ_RESOURCE_GROUP  where (RESOURCE_CATEGORY = '"+resource_category+"'))"; 

      if (!Str.isEmpty(project_id))
      {
         transW.addQuery("WEEKMONTH","SELECT WEEK_MONTH_FLAG from PROJ_RES_PROCESS WHERE VERSION = ?").addParameter(this,"VERSION",project_id);
         transW.addQuery("WEEK_NO","SELECT Prjrep_Web_Util_API.Get_Current_Week WEEK_NO from dual");  
         transW.addQuery("CURRMONTH","select TO_CHAR(sysdate,'MM') CURRMONTH from dual");
         transW.addQuery("CURRYEAR","select TO_CHAR(sysdate,'YYYY') CURRYEAR from dual");

         transW = mgr.perform(transW);

         weekMonth = transW.getValue("WEEKMONTH/DATA/WEEK_MONTH_FLAG");
         nCurrentWeek = Integer.parseInt(transW.getValue("WEEK_NO/DATA/WEEK_NO"));
         nCurrentMonth = Integer.parseInt(transW.getValue("CURRMONTH/DATA/CURRMONTH"));
         currYear = Integer.parseInt(transW.getValue("CURRYEAR/DATA/CURRYEAR"));
         nextYear = currYear+1;
         transW.clear();

         if (no_of_periods > 12)
           no_of_periods = 12;

         ASPQuery qry = trans.addQuery(blk);
         qry.includeMeta("ALL");
         if (weekMonth.equals("W"))
         {

            if ((nCurrentWeek + no_of_periods)>52)
            {
               int nrest= no_of_periods - (52 - nCurrentWeek +1);
               qry.addWhereCondition("(VERSION= ? "+strCategWstmt+" AND PERIOD_YEAR = ?  AND PERIOD_NO >= (SELECT Prjrep_Web_Util_API.Get_Current_Week from dual) AND PERIOD_NO <= (SELECT Prjrep_Web_Util_API.Get_Current_Week + ? from dual)) OR (VERSION = ? "+strCategWstmt+" AND PERIOD_YEAR = ?  AND PERIOD_NO <= ?)");
               qry.addParameter(this,"VERSION",project_id);
               qry.addParameter(this,"CURRYEAR",Integer.toString(currYear));
               qry.addParameter(this,"CURRYEAR",Integer.toString(no_of_periods-nrest));
               qry.addParameter(this,"VERSION",project_id);
               qry.addParameter(this,"CURRYEAR",Integer.toString(nextYear));
               qry.addParameter(this,"CURRYEAR",Integer.toString(nrest));

            }
            else
	    {
               qry.addWhereCondition("VERSION= ? "+strCategWstmt+" AND PERIOD_YEAR= (select TO_CHAR(sysdate,'YYYY') from dual)  AND PERIOD_NO >= (SELECT Prjrep_Web_Util_API.Get_Current_Week from dual) AND PERIOD_NO <= (SELECT Prjrep_Web_Util_API.Get_Current_Week + ? -1 from dual)");
               qry.addParameter(this,"VERSION",project_id);
               qry.addParameter(this,"CURRYEAR",Integer.toString(no_of_periods));
            }

         }
         //==========If the calculation is on  monthly basis ==================
         else
         {
            if ((nCurrentMonth + no_of_periods)>12)
            {
               int nMonthrest= no_of_periods - (12 - nCurrentMonth +1);
               qry.addWhereCondition("VERSION= ?  AND PERIOD_YEAR= ?  AND PERIOD_NO >= (select TO_CHAR(sysdate,'MM') from dual) AND PERIOD_NO <= (select TO_CHAR(sysdate,'MM')  + ? from dual) OR (VERSION = ? AND PERIOD_YEAR = ?  AND PERIOD_NO <= ?)");
               qry.addParameter(this,"VERSION",project_id);
               qry.addParameter(this,"CURRYEAR",Integer.toString(currYear));
               qry.addParameter(this,"CURRYEAR",Integer.toString(no_of_periods));
               qry.addParameter(this,"VERSION",project_id);
               qry.addParameter(this,"CURRYEAR",Integer.toString(nextYear));
               qry.addParameter(this,"CURRYEAR",Integer.toString(nMonthrest));

            }
            else
	    {
               qry.addWhereCondition("VERSION= ?  AND PERIOD_YEAR= (select TO_CHAR(sysdate,'YYYY') from dual)  AND PERIOD_NO >= (select TO_CHAR(sysdate,'MM') from dual) AND PERIOD_NO <= (select TO_CHAR(sysdate,'MM')  + ? -1 from dual)");
               qry.addParameter(this,"VERSION",project_id);
               qry.addParameter(this,"CURRYEAR",Integer.toString(no_of_periods));
            }

            int nMonthrest= no_of_periods - (12 - nCurrentMonth +1);

         }


         qry.setOrderByClause("RESOURCE_ID,COMPANY,PERIOD_YEAR,PERIOD_NO"); 

         qry.setBufferSize(10000);

         submit(trans);
         trans.clear();
      }
   }

   public void printContents() throws FndException
   {  
      if (Str.isEmpty(project_id) || rowset.countRows() == 0)
      {
         printNewLine();    
         appendToHTML("<a href=\"javascript:customizeBox('"+getId()+"')\">");
         printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESILSEL2: Customize this portlet");
         appendToHTML("</a>");       
         printNewLine();
      }
      else
         printCustomTable();    
   }

   //==========================================================================
   //  Customize mode
   //==========================================================================   

   public boolean canCustomize()
   {
      return true;
   }

   public void runCustom()
   {
      ASPManager mgr  = getASPManager(); 
      transQ           = mgr.newASPTransactionBuffer();
      myURL              = mgr.getPortalPage().getURL();  

      if (myURL.lastIndexOf("ProjectPortal")==-1)
      {
         ASPQuery qryQ;

         qryQ = transQ.addQuery("VERSIONLIST", "PROJ_RES_PROCESS",  "VERSION,VERSION", "", "");
         qryQ = transQ.addQuery("RESOURCE_LIST", "PROJ_RESOURCE_CATEGORY",  "RESOURCE_CATEGORY,RESOURCE_CATEGORY","", "");    

         qryQ.setBufferSize(500);
         qryQ.includeMeta("ALL");
         transQ = mgr.perform(transQ); 

         buff  = mgr.newASPBuffer();

         buff = transQ.getBuffer("RESOURCE_LIST").addBufferAt("DATA",0);
         buff.addItem("RESOURCE_CATEGORY","");
         buff.addItem("RESOURCE_CATEGORY","");
         buff = transQ.getBuffer("RESOURCE_LIST").addBufferAt("DATA",1);
         buff.addItem("RESOURCE_CATEGORY","%");
         buff.addItem("RESOURCE_CATEGORY","%");


      }

      if (Str.isEmpty(str_no_of_periods))
         str_no_of_periods = "12";

      if (Str.isEmpty(green))
         green = "80";
      if (Str.isEmpty(red))
         red = "100";

   }

   public void printCustomBody()throws FndException
   {
      beginTable();
      beginTableBody();

      printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESSLABEL: Portlet Label: ");
      printSpaces(21);
      printField("MY_TITLE",cus_title,35);

      if (myURL.lastIndexOf("ProjectPortal")==-1)
      {
         printNewLine();
         printNewLine();
         printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESALTXT: Resource Calculation: ");
         printSpaces(10);
         printMandatorySelectBox("TEMP_PROJECT", transQ.getBuffer("VERSIONLIST"), project_id);
      }
      printNewLine();
      printNewLine();

      printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESSTXT: No of period to display: ");
      printSpaces(8);
      printField("NO_OF_PERIODS",str_no_of_periods,2,"numberValidate"+addProviderPrefix());
      printSpaces(8);
      printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESMAX: (Maximum 12 periods can be displayed)");
      printNewLine();
      printNewLine();

      if (myURL.lastIndexOf("ProjectPortal")==-1)
      {

         printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESORYTXT: Resource Category: ");
         printSpaces(12);
         printMandatorySelectBox("RESOURCE_CATEGORY_VALUE", transQ.getBuffer("RESOURCE_LIST"), resource_category);
         printNewLine();
         printNewLine();
         printNewLine();
         printNewLine();
      }
      printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESSETUPTXT: Indicator set up for resource load: ");
      printNewLine();
      printNewLine();
      printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESVALTXT: Green - less than: ");
      printSpaces(13);
      printField("GREEN",green,5,"numberValidate"+addProviderPrefix());
      printText("%");

      printNewLine();
      printNewLine();
      printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESLTXT: Yellow - between ");
      printText(green);
      printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESXT: % and ");
      printText(red);
      printText("%");

      printNewLine();
      printNewLine();
      printText("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESRED: Red - more than: ");
      printSpaces(14);
      printField("RED",red,5,"numberValidate"+addProviderPrefix());
      printText("%");


      endTableBody();
      endTable();


      appendDirtyJavaScript("function numberValidate"+addProviderPrefix()+"(val,obj)\n"+
                            "{\n"+
                            "   green	 = eval('document.form."+addProviderPrefix()+"GREEN');\n"+
                            "   red	 = eval('document.form."+addProviderPrefix()+"RED');\n"+
                            "   str_no_of_periods	 = eval('document.form."+addProviderPrefix()+"NO_OF_PERIODS');\n"+
                            "   if(!(green.value>=0))\n"+
                            "   { \n"+   
                            "	 	alert('Number Value is Required');\n"+
                            "		green.value = '';\n"+
                            "   }\n "+                         
                            "   if(!(red.value>=0))\n"+
                            "   { \n"+   
                            "	 	alert('Number Value is Required');\n"+
                            "		red.value = '';\n"+
                            "   }\n "+
                            "   if(!(str_no_of_periods.value>=0))\n"+
                            "   { \n"+   
                            "	 	alert('Number Value is Required');\n"+
                            "		str_no_of_periods.value = '';\n"+
                            "   }\n "+
                            "}\n");


   }

   public void submitCustomization()
   { 

      project_id  = readValue("TEMP_PROJECT");
      cus_title = readValue("MY_TITLE");
      resource_category = readValue("RESOURCE_CATEGORY_VALUE"); 
      str_no_of_periods = readValue("NO_OF_PERIODS");
      if (Str.isEmpty(str_no_of_periods))
         no_of_periods = 12;
      else
         no_of_periods = (int)(Integer.parseInt(str_no_of_periods.valueOf(str_no_of_periods)));

      green  = readValue("GREEN");
      red  = readValue("RED");

      writeProfileValue("VERSION",readAbsoluteValue("TEMP_PROJECT"));   
      writeProfileValue("RESOURCE_CATEGORY_VALUE",resource_category);
      writeProfileValue("MY_TITLE",readAbsoluteValue("MY_TITLE")); 
      writeProfileValue("NO_OF_PERIODS",str_no_of_periods);   
      writeProfileValue("GREEN",readAbsoluteValue("GREEN"));
      writeProfileValue("RED",readAbsoluteValue("RED"));      
   }

   //==========================================================================
   //  Other methods
   //==========================================================================

   public void printCustomTable() throws FndException
   {
      ASPManager mgr= getASPManager(); 
      myURL = mgr.getPortalPage().getURL();  

      if (no_of_periods > 12)
         no_of_periods = 12;

      if (Str.isEmpty(green))
         ngreen = 80;
      else
         ngreen = new Double(green).doubleValue();

      if (Str.isEmpty(red))
         nred = 100;
      else
         nred  = new Double(red).doubleValue();

      rowset.first();
      beginTable();    
      beginTableTitleRow();         
      printTableCell(translate("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESALC: Resource Calculation: "));
      String str;
      if (myURL.lastIndexOf("ProjectPortal")!=-1)
         str = "projw/ResourceCapacityCalculation.page?" + "VERSION=" +rowset.getValue("VERSION");
      else
         str = "projw/ResourceCapacityCalculation.page?" + "VERSION=" +rowset.getValue("VERSION");
      beginTableCell();
      printLink(rowset.getValue("VERSION"), str);
      endTableCell();
      printTableCell("");
      printTableCell("");
      printTableCell("");    
      printTableCell(translate("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCEST: Period"));

      if (weekMonth.equals("W"))
         printTableCell("(Weekly): ");
      else
         printTableCell("(Monthly): ");
      endTableTitleRow();      
      endTable();
      if (weekMonth.equals("W"))
      {
         nCalcWeek = nCurrentWeek;
      }
      else
      {
         nCalcWeek = nCurrentMonth;
      }

      beginTable();
      beginTableTitleRow();
      printTableCell(translate("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESROUP: Resource Group:"));
      printTableCell(translate("PROJWPORTLETSPROJWMYAVAILABLEPROJECTRESOURCESOPMTXT: Company:"));
      rowset.first();
      String curPeriod = "";;
      int j=0;
      for (int i = 0; i<no_of_periods;i++)
      {
         /* If the period no repeated, that means no_of_periods > no_of_remaining_periods
            in project. Therefore the printing of headings should be terminated. The index(i)
            is retained to make same no of repetitions when images are printed.
         */ 
         if (i==0)
         {
            curPeriod = rowset.getValue("PERIOD_NO"); 
         }
         else if (curPeriod.equalsIgnoreCase(rowset.getValue("PERIOD_NO")))
         {
            j = i;
            break;
         }
         printTableCell(rowset.getValue("PERIOD_NO"));
         rowset.forward(1);
      }
      endTableTitleRow();                   

      rowset.first();
      beginTableBody();  
      whileFlag = true; 

      while (whileFlag)
      {
         printTableCell(rowset.getValue("RESOURCE_ID_DESC"));
         printTableCell(rowset.getValue("COMPANY"));
         for (int i = 0; i<no_of_periods;i++)
         {
            beginTableCell();
            if (myURL.lastIndexOf("ProjectPortal")!=-1)
            {
               if (rowset.getNumberValue("UTILISATION")*100 <ngreen)
                  printImage(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"images/_greendot.gif");
               else if (rowset.getNumberValue("UTILISATION")*100 >nred)
                  printImage(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"projw/images/_reddot.gif");
               else
                  printImage(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"images/_yellowdot.gif"); 
            }
            else
            {
               if (rowset.getNumberValue("UTILISATION")*100 <ngreen)
                  printImage("projw/images/_greendot.gif");
               else if (rowset.getNumberValue("UTILISATION")*100 >nred)
                  printImage("projw/images/_reddot.gif");
               else
                  printImage("projw/images/_yellowdot.gif"); 
            }              
            rowset.forward(1);
            endTableCell(); 
            // The no of periods remaining expired. The loop must terminate
            if (i+1==j)
            {
               break;
            } 
         }     
         if (rowset.getCurrentRowNo()+1 == rowset.countRows())
            whileFlag = false;
         nextTableRow();
      }
      endTableBody();
      endTable();   
      //============================End of the Custom Table======================================			
   }
}