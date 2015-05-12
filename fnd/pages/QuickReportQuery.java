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
*  File        : QuickReportQuery.java 
*  Modified    :
*     Suneth M     2001-Aug-21 - Created Using the ASP file QuickReportQuery.asp
*     Suneth M     2001-Nov-20 - Log Id #807. Changed generateParamTableContents() &
*                             getContents().
*     Rifki R      2002-Apr-08 - Fixed translation problems caused by trailing spaces.
*     Kingsly P    2002-Oct-29 - Added Crystal Report Support.
*     Chanaka A    2003-Mar-06 - Add code to remove the navigator icon and home icon
*                              Add code to retrive and store the path of Logoff.csp file in 
*                              cr_logoff_path variable
*                              Add code to onClick event of the Submit button
*     Chanaka A    2003-Mar-19 - Fixed the Logout.csp been called for SQL Report from 
*                              QuickReportQuery.page
*     Chanaka A    2003-Mar-25 - Removed the isDefined() check from run()
*     Prashantha W 2003-Jul-31 - Restructure the page to add support CR9.
*     Chanaka A    2003-Jul-31 - Add support to view Crystal Analysis reports
*     Prashantha W 2003-Aug-07 - Add support for Plant Desing
*     Prashantha W 2003-Aug-14 - Fixed the bug of showing an error when viewing
*                                SQL reports
*     Chanaka A    2003-Aug-14 - Fixed the bug of showing an error when closing
*                                a non published Crystal Analysis report
*     Prashantha W 2003-Aug-27 - Fix error in URL parameters when BYPASS_PAGE=TRUE
*     Chanaka A    2003-Sep-22 - Workaround for scan tool bug, used concatenation instead
*                                of '&1' 
*     Chanaka A    2003-Oct-06 - Fixed bug for BP where values are not displayed for
*                                multiple parameter types when values are passed through URL
*     Chanaka A    2003-Oct-10 - Fixed translation problems
*     Jacek P      2004-Apr-20 - First synchronization with the Montgomery project
*     Mangala P    2004-May-07 - Merge patch for bug 43681
*     Mangala P    2004-May-26 - Merge bug id 44587.
*     Chandana D   2004-Jun-10 - Removed all absolute URLs.
*     Suneth M     2004-Jul-26 - Merged Bug 45285, Changed substVarExtract() & isNewParam(). 
*
*  Note:
*   Example of a url string received to the page should be as follows
*
*   QuickReportQuery.page?QUICK_REPORT_ID=172
*   &ORDERBY=ENVIRONMENT"+(char)30+"SCORECARD"+(char)30+"MEASURE
*   &BYPASS_PAGE=FALSE
*   &ENVIRONMENT=BANKS&SCORECARD=HSBC&MEASURE=FIRST"+(char)30+"SECOND"+"&START_DATE=2003-09-09";                              
* ----------------------------------------------------------------------------
* New Comments:
* 2010-09-15 VOHELK Bug 93000, Introduced new view, inorder to filter out new QR types 
* 2010/09/20 amiklk Bug 93083, Added hidden field "Submit" to ensure it pass when submit on any browser
* 20081218 rahelk Bug 79378, removed duplicate code to QuickReportShowSQL.java
* 20081113 rahelk Bug 78060, changed substVarExtract to replace all parameters with P0..Pn names to support non-ASCII chars
* 2008/09/24 sumelk Bug 77316, Changed validate() & isNewParam() to handle SQL statements with &APPOWNER.
* 2008/09/05 sumelk Bug 76730, Changed generateParamTableContents().
* 2007/12/06           sadhlk
* Bug 67296, Modified getContents() to fix proxy related issue.
* 2007/11/30           sumelk
* Bug 67400, Changed getContents() to format the comments properly.
* 2007/06/07           sumelk
* Merged the corrections for Bug 65806, Changed generateLogOnTableContents() & generateParamTableContents().

* Revision 1.11 2007/05/10           buhilk
* Bug 65289, Changed validate() to replace CSV's in sql statement.
*
* Revision 1.10 2007/01/11           sadhlk
* Bug 62773, Changed substVarSubstitute() and constructCountExpression().
*
* Revision 1.9  2006/11/09           sadhlk
* Bug 61059, Added 'Get all rows' button to get Total row count. 
*
* Revision 1.8  2006/10/16           chaalk
* Bug 60734, Reinstate Crystal web integration - Supplementary
*
* Revision 1.7  2006/09/19           sumelk
* Bug 60578, Removed the hidden field QUICK_REPORT_ID.
*
* Revision 1.6  2006/09/18 04:30:00  chaalk
* Bug 58703, Reinstate Crystal web integration
*
* Revision 1.5  2006/07/21           sumelk
* Bug 59490, Added the hidden field QUICK_REPORT_ID.
*
* Revision 1.4  2006/07/03 17:01:13  buhilk
* Bug 58216, Fixed SQL Injection threats
*
* Revision 1.3  2005/11/15 14:09:27  mapelk
* Minor GUI bug fix
*
* Revision 1.2  2005/11/11 10:17:22  sumelk
* Merged the corrections for Bug 53544, Changed getContents() & extractParametersFromSQL(). Added allParamsValuesExist().
*
* Revision 1.1  2005/11/10 10:47:09  mapelk
* Remove CR dependencies from Quick Reports
*                             
* ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.*;
import ifs.fnd.cr.*;

import java.io.*;
import java.util.*;


public class QuickReportQuery extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.QuickReportQuery");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPPage page;
   private ASPContext ctx;
   private ASPTransactionBuffer trans;
   private ASPBuffer sec;
   private CrystalReport crManager;
   private ASPHTMLFormatter fmt;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private int paramCount;
   private String description;
   private String sql_expression;
   private String file_name;
   private String comments;
   private String qr_type;

   private String[] paramNames;
   private String[] paramPrompts;
   private String[] paramDefaultValues;

   private String[] paramValueType;
   private Hashtable tbl; 
   private String crPath;
   private String[] tablesLogOnInfoName;
   private String[] tablesLogOnInfoServerName;
   private String[] tablesLogOnInfoDatabaseName;
   private String[] tablesLogOnInfoUserID;
   private String[] tablesLogOnInfoPassword;
   private String[] usedTables;
   private int nUsedTables;
   private int nOtherTables;
   private ArrayList paramDefaultValuesList;
   private String[] ismultiple;
   private ASPBuffer tablesinfobuffer;
   private String bypassPage;
   private boolean hasURLParameters;
   private String[] bypassTrueURLParams;
   private ASPBuffer urlParamBuffer; 
   ASPBuffer formulaNameBuf;
   ASPBuffer orderByBuffer;
   private Vector vparamNames;
   private String quick_report_id;
   private String val;
   private HashMap col_alias;
   private HashMap temp_map;

   //===============================================================
   // Construction 
   //===============================================================
   public QuickReportQuery(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return getASPManager().translate("FNDPAGESQUICKREPORTQUERYTEXTQRTITLE: Quick Report") + " - " + description;
   }

   protected String getTitle()
   {

      return getASPManager().translate("FNDPAGESQUICKREPORTQUERYTEXTQRTITLE: Quick Report") + " - " + description;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      page  = mgr.getASPPage();
      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      fmt = mgr.newASPHTMLFormatter();

      paramDefaultValues = new String[10];
      sec = mgr.newASPBuffer();
      tablesLogOnInfoPassword = new String[10];
      nOtherTables = 0;
      nUsedTables = 0;
      vparamNames = new Vector();

      page.disableHomeIcon();
      page.disableNavigate();
      page.disableOptions();

      formulaNameBuf =  ctx.readBuffer("PARANAMESBUFFER");
      tablesinfobuffer = ctx.readBuffer("TABLESLOGONINFO");
      urlParamBuffer= ctx.readBuffer("URLPARAMBUFFER");
      orderByBuffer= ctx.readBuffer("ORDERBYBUFFER");
      bypassPage = ctx.readValue("BYPASSVAL","FALSE");

      file_name = ctx.readValue("FILENAME","");
      nOtherTables = ctx.readNumber("OTHERTABLES",0); 
      ASPBuffer temp_buff = mgr.newASPBuffer();
      String fullAppPath = "/cr/QuickReportFrames.jsp";
      ArrayList tempParamDefaultValuesList=null;

      /*if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();
      
      else*/ if (!mgr.isEmpty(mgr.readValue("QUICK_REPORT_ID")))
      {
         
         orderByBuffer = mgr.newASPBuffer();
         quick_report_id =  mgr.readValue("QUICK_REPORT_ID");
         ASPQuery qry = trans.addQuery("MASTER", "SELECT DESCRIPTION, SQL_EXPRESSION, COMMENTS, FILE_NAME, QR_TYPE_DB FROM QUICK_REPORT_NON_BI WHERE QUICK_REPORT_ID = ?");
         qry.addParameter("QUICK_REPORT_ID","N","IN",quick_report_id);
         perform(); 

         // Intitalize page variables
         description =  trans.getValue("MASTER/DATA/DESCRIPTION");
         sql_expression = trans.getValue("MASTER/DATA/SQL_EXPRESSION");
         file_name = trans.getValue("MASTER/DATA/FILE_NAME");
         comments = trans.getValue("MASTER/DATA/COMMENTS");
         qr_type = trans.getValue("MASTER/DATA/QR_TYPE_DB");
         String strQueryString="" ;

         /*if ("CA".equals(qr_type))
         {
            caManager = new CrystalAnalysis(getASPManager());
            caManager.openReport(file_name);
            caManager.closeReport();

            temp_buff.addBuffer("COMMON").addItem("FILE_NAME",file_name);
            temp_buff.getBuffer("COMMON").addItem("QR_TYPE_DB",qr_type);
            fullAppPath = "/common/cr/QuickReportFrames.jsp";
            mgr.transferDataTo(fullAppPath,temp_buff);
         }
         else*/ 
         
         if ("SQL".equals(qr_type))
            extractParametersFromSQL();
         else// IF CRYSTAL REPORTS
         {
            quick_report_id = "";
            crManager = new CrystalReport(getASPManager());
            
            if (!mgr.isEmpty(mgr.readValue("BYPASS_PAGE")))
            {
               if (!mgr.isEmpty(mgr.readValue("ORDERBY")))
               {
                  orderByBuffer =  getOrderbyBuffer(orderByBuffer,mgr.URLDecode(mgr.readValue("ORDERBY")));
               }

               bypassPage =mgr.readValue("BYPASS_PAGE");
               strQueryString = mgr.URLDecode(mgr.getQueryString());

               if ("TRUE".equals(bypassPage))
               {
                  strQueryString = strQueryString.substring(strQueryString.indexOf("BYPASS_PAGE=")+17,strQueryString.length());
               }
               else
               {
                  strQueryString = strQueryString.substring(strQueryString.indexOf("BYPASS_PAGE=")+18,strQueryString.length());
               }
            }// END IF BYPASS            

            if(crManager.openReport(file_name)){

               String sSecuredReportRequired = mgr.getConfigParameter("APPLICATION/CRYSTAL_REPORTS/SECURED_REPORTS_REQUIRED");
               if ("TRUE".equals(sSecuredReportRequired.toUpperCase())){
                  String crWebInitPass = crManager.checkCrWebInit(file_name);
                  if (!"CR_WEB_INIT_PASSED".equals(crWebInitPass)){
                     if ("CR_WEB_INIT_NOT_FOUND".equals(crWebInitPass)){
                           StringBuffer tempMessgeBuffer = new StringBuffer();
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG1: The CR_WEB_INIT stored procedure is not found in the layout."));
                           tempMessgeBuffer.append("\n");
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG2: Layout Name :"));                           
                           tempMessgeBuffer.append(" ");
                           tempMessgeBuffer.append(file_name);
                           mgr.showError(tempMessgeBuffer.toString());                     
                     }
                     else if ("NOT_LINKED_PROPERLY".equals(crWebInitPass)){
                           StringBuffer tempMessgeBuffer = new StringBuffer();
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG3: The CR_WEB_INIT stored procedure is not linked or linked properly with the main report view."));
                           tempMessgeBuffer.append("\n");
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG2: Layout Name :"));                           
                           tempMessgeBuffer.append(" ");
                           tempMessgeBuffer.append(file_name);
                           mgr.showError(tempMessgeBuffer.toString());                     
                     }
                     else if ("LINK_FIELDS_NOT_CORRECT".equals(crWebInitPass)){
                           StringBuffer tempMessgeBuffer = new StringBuffer();
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG4: The CR_WEB_INIT stored procedure is not linked with the OBJVERSION field."));
                           tempMessgeBuffer.append("\n");
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG2: Layout Name :"));                           
                           tempMessgeBuffer.append(" ");
                           tempMessgeBuffer.append(file_name);
                           mgr.showError(tempMessgeBuffer.toString());                     
                     }
                     else if ("WRONG_LINK_TYPE".equals(crWebInitPass)){
                           StringBuffer tempMessgeBuffer = new StringBuffer();
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG5: The CR_WEB_INIT stored procedure is linked with a wrong link type. The correct type should be 'Not Equal'."));
                           tempMessgeBuffer.append("\n");
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG2: Layout Name :"));                           
                           tempMessgeBuffer.append(" ");
                           tempMessgeBuffer.append(file_name);
                           mgr.showError(tempMessgeBuffer.toString());                     
                     }
                     else if ("OBJVERSION_NOT_FOUND".equals(crWebInitPass)){
                           StringBuffer tempMessgeBuffer = new StringBuffer();
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG6: The OBJVERSION field in the CR_WEB_INIT stored procedure is not inserted in the report header."));
                           tempMessgeBuffer.append("\n");
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTCHECKSTOREDPROCEDUREMSG2: Layout Name :"));                           
                           tempMessgeBuffer.append(" ");
                           tempMessgeBuffer.append(file_name);
                           mgr.showError(tempMessgeBuffer.toString());                     
                     }
                  }
               }
               
               String logonBuffer = crManager.extractLogOnInfo(file_name);
               readLogonInofValues(logonBuffer);
               
               int parameterCount =  crManager.getParameterCount(file_name);
               
               if (parameterCount == 0)
               {
                  if (nOtherTables == 0){
                     String fileName = crManager.getSavedReport(file_name);  
                     if ((!"".equals(fileName))&&( fileName != null)){
                        temp_buff.addBuffer("COMMON").addItem("FILE_NAME",fileName);
                        mgr.transferDataTo(fullAppPath,temp_buff);       
                     }else{
                        StringBuffer tempMessgeBuffer = new StringBuffer();
                        tempMessgeBuffer.append(file_name);
                        tempMessgeBuffer.append(" ");
                        tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTOPENREP: report is not found in the Crystal layout location."));
                        mgr.showError(tempMessgeBuffer.toString());
                     }
                  }
               }
               else// Paramters exist
               {
                  String parameterBuffer = crManager.extractParameters(file_name);
                  readParamValues(parameterBuffer,parameterCount);
                  if (strQueryString.length()>0)// url parameters exist
                  {
                     tempParamDefaultValuesList = getQueryStringValues(strQueryString);
                     paramDefaultValuesList = new ArrayList(parameterCount);
                     paramDefaultValuesList = tempParamDefaultValuesList;
                     hasURLParameters = true;
                  }  
                  if ("TRUE".equals(bypassPage))
                  {
                     if (nOtherTables == 0)
                     {
                        String parameterString = "";    
                        for (int count=0; count < bypassTrueURLParams.length; count++){
                           parameterString = parameterString + bypassTrueURLParams[count].toString() + (char)29;
                        }

                        String fileName = crManager.getSavedReportWithParametersAndlogonInfo(file_name,parameterString,"");

                        if ((!"".equals(fileName))&&( fileName != null)){
                           temp_buff.addBuffer("COMMON").addItem("FILE_NAME",fileName);
                           mgr.transferDataTo(fullAppPath,temp_buff);       
                        }else{
                           StringBuffer tempMessgeBuffer = new StringBuffer();
                           tempMessgeBuffer.append(file_name);
                           tempMessgeBuffer.append(" ");
                           tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTOPENREP: report is not found in the Crystal layout location."));
                           mgr.showError(tempMessgeBuffer.toString());
                        }
                     }
                  }
               }
            }
            else{
               StringBuffer tempMessgeBuffer = new StringBuffer();
               tempMessgeBuffer.append(file_name);
               tempMessgeBuffer.append(" ");
               tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTOPENREP: report is not found in the Crystal layout location."));
               mgr.showError(tempMessgeBuffer.toString());
            }
         }// END IF CR

      }
     else if ( mgr.buttonPressed("Submit") )
     {
         crManager = new CrystalReport(getASPManager());
         String[] paramValues;
         
         if(crManager.openReport(file_name)){
            String crlogonInfo = readLogonInfoFormValues();
            String parameterString = "";
            int parameterCount = crManager.getParameterCount(file_name);  
            if (parameterCount > 0)
            {
               if ("TRUE".equals(bypassPage)){
                  paramValues = crManager.getStringArrayFrombuffer(urlParamBuffer,"TEMPPARAMETERVALUES");
               }
               else{
                  paramValues = getParameterReadValues();
               }
               for (int count=0; count < paramValues.length; count++){
                  parameterString = parameterString + paramValues[count] + (char)29;
               }
            }
            else{
               parameterString = "";
            }
               String fileName = crManager.getSavedReportWithParametersAndlogonInfo(file_name,parameterString,crlogonInfo);

               if ((!"".equals(fileName))&&( fileName != null)){
                  temp_buff.addBuffer("COMMON").addItem("FILE_NAME",fileName);
                  mgr.transferDataTo(fullAppPath,temp_buff);       
               }else{
                  StringBuffer tempMessgeBuffer = new StringBuffer();
                  tempMessgeBuffer.append(file_name);
                  tempMessgeBuffer.append(" ");
                  tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTOPENREP: report is not found in the Crystal layout location."));
                  mgr.showError(tempMessgeBuffer.toString());
               }
         }
         else{
            StringBuffer tempMessgeBuffer = new StringBuffer();
            tempMessgeBuffer.append(file_name);
            tempMessgeBuffer.append(" ");
            tempMessgeBuffer.append(mgr.translate("FNDCRCRYSTALREPORTOPENREP: report is not found in the Crystal layout location."));
            mgr.showError(tempMessgeBuffer.toString());
         }
      }
      ctx.writeBuffer("ORDERBYBUFFER",orderByBuffer);
      ctx.writeNumber("OTHERTABLES",nOtherTables);
      ctx.writeValue("SQLEXPRESSION", sql_expression);
      ctx.writeValue("FILENAME",file_name);
      ctx.writeValue("BYPASSVAL",bypassPage);

   } 

   private ASPBuffer getOrderbyBuffer(ASPBuffer subbuf,String concatstring)
   {
      Vector v;
      v = separateAttr(concatstring,(char)30);
      for (int i=0; i < v.size(); i++)
      {
         subbuf.addItem("ITEM"+i,(String)v.elementAt(i));
      }
      return subbuf;
   }

   private ArrayList getQueryStringValues(String str)
   {  
      Vector v =separateAttr(str,'&');
      ASPManager mgr = getASPManager();
      bypassTrueURLParams = new String[v.size()];
      ArrayList URLparamList=null;
      if (v.size() >0)
      {
         URLparamList = new ArrayList(v.size()) ; 
      }

      for (int i=0; i < v.size(); i++)
      {
         hasURLParameters = true;
         String[] elementArray;
         String element = (String)separateAttr(String.valueOf(v.elementAt(i)),'=').elementAt(1);
         bypassTrueURLParams[i] = (String)separateAttr(String.valueOf(v.elementAt(i)),'=').elementAt(1); 

         if (element.indexOf((char)30)>= 0)
         {
            Vector tempArray = separateAttr(element,(char)30);
            elementArray = new String[tempArray.size()];

            for (int j=0;j<tempArray.size();j++)

            {
               elementArray[j] = (String)tempArray.elementAt(j);
            }
         }
         else
         {
            elementArray = new String[1];
            elementArray[0]    = (String)separateAttr(String.valueOf(v.elementAt(i)),'=').elementAt(1);           
         }
         URLparamList.add(elementArray);
      }
      
      if (hasURLParameters)
      {
         urlParamBuffer =  mgr.newASPBuffer();
         crManager.addStringArrayToBuffer(urlParamBuffer ,"TEMPPARAMETERVALUES",bypassTrueURLParams);
         ctx.writeBuffer("URLPARAMBUFFER",urlParamBuffer);
      }

      return URLparamList;
   }

   private Vector separateAttr(String str, char delim)
   {

      ASPManager mgr = getASPManager();
      Vector v = new Vector();

      if (!mgr.isEmpty(str))
      {
         StringTokenizer st = new StringTokenizer(str,String.valueOf(delim));
         while (st.hasMoreElements())
         {
            v.addElement(st.nextToken());
         }
      }
      return v;
   } 

   private String readLogonInfoFormValues()
   {
      ASPManager mgr = getASPManager();
      String logonInformation = "";
      for (int i = 0 ; i<tablesinfobuffer.countItems() ;i++ )
      {
         String name = tablesinfobuffer.getValueAt(i);
         logonInformation = logonInformation + name + (char)28 
                 + mgr.readValue(name+".LogOnServerName") + (char)28  
                 + mgr.readValue(name+".LogOnDatabaseName") + (char)28 
                 + mgr.readValue(name+".LogOnUserID") + (char)28 
                 + mgr.readValue(name+".LogOnPassword") + (char)29; 
      }
      return logonInformation;
   }
   
   public String[] getParameterReadValues()
   {
      ASPManager mgr = getASPManager();
      String[] strBuff;
      strBuff = new String[formulaNameBuf.countItems()];

      String name;
      for (int i = 0; i<formulaNameBuf.countItems();i++)
      {
         name = formulaNameBuf.getValueAt(i);
         strBuff[i] =  mgr.readValue(mgr.URLEncode(name));
      }

      return strBuff;
   }

   public void readParamValues(String buf,int parameterCount)
   {
      ASPManager mgr = getASPManager();
      StringTokenizer st = new StringTokenizer(buf.substring(buf.indexOf("PARAMNAMES=")+11,buf.indexOf((char)29)),""+(char)28);
      int i=0;
      paramNames = new String[st.countTokens()];
      paramCount =  st.countTokens();
      ASPBuffer tempbuf = mgr.newASPBuffer();
      while (st.hasMoreTokens()) {
         paramNames[i]=st.nextToken().toString();
         tempbuf.addItem("ITEM"+i,paramNames[i]);
         i++;
     }
      i=0;
      st = new StringTokenizer(buf.substring(buf.indexOf("PARAMPROMPTS=")+13,buf.indexOf((char)29,buf.indexOf("PARAMPROMPTS="))),""+(char)28);
      paramPrompts = new String[st.countTokens()];      
      while (st.hasMoreTokens()) {
         paramPrompts[i]=st.nextToken().toString();
         i++;
     }
     i=0;
     st = new StringTokenizer(buf.substring(buf.indexOf("PARAMVALUETYPE=")+15,buf.indexOf((char)29,buf.indexOf("PARAMVALUETYPE="))),""+(char)28);
     paramValueType = new String[st.countTokens()];      
     while (st.hasMoreTokens()) {
        paramValueType[i]=st.nextToken().toString();
        i++;
     }
      
     i=0;
     st = new StringTokenizer(buf.substring(buf.indexOf("ISMULTIPLE=")+11,buf.indexOf((char)29,buf.indexOf("ISMULTIPLE="))),""+(char)28);
     ismultiple = new String[st.countTokens()];        
     while (st.hasMoreTokens()) {
        ismultiple[i]=st.nextToken().toString();
        i++;
     }
      
      st = new StringTokenizer(buf.substring(buf.indexOf("PARAMDEFAULTVALUES=")+19),""+(char)29);
      String defaultParamListString;
      paramDefaultValuesList = new ArrayList(st.countTokens()); 
      while (st.hasMoreTokens()) {
         defaultParamListString = null;
         defaultParamListString = st.nextToken().toString();
         StringTokenizer st1 = new StringTokenizer(defaultParamListString,""+(char)30);
         String[] tempStrArry = new String[st1.countTokens()==0 ? 1 : st1.countTokens()];
         i = 0;
         while (st1.hasMoreTokens()) {
            tempStrArry[i] = st1.nextToken().toString();
            i++;
         }
         paramDefaultValuesList.add(tempStrArry);
     }

     formulaNameBuf = tempbuf;
     ctx.writeBuffer("PARANAMESBUFFER",tempbuf);
   } 

   public void readLogonInofValues(String logbuf)
   {
      ASPManager mgr = getASPManager();
      ASPBuffer tempbuf = mgr.newASPBuffer();

      nOtherTables = Integer.parseInt(logbuf.substring(logbuf.indexOf("NOTHERTABLES=")+13,logbuf.indexOf((char)29)));
      
      int i=0;
      StringTokenizer st = new StringTokenizer(logbuf.substring(logbuf.indexOf("TABLENAMES=")+11,logbuf.indexOf((char)29,logbuf.indexOf("TABLENAMES="))),""+(char)28);
      tablesLogOnInfoName = new String[st.countTokens()];      
      while (st.hasMoreTokens()) {
         tablesLogOnInfoName[i]=st.nextToken().toString();
         tempbuf.addItem("ITEM"+i,tablesLogOnInfoName[i]);
         i++;
      }
      
      i=0;
      st = new StringTokenizer(logbuf.substring(logbuf.indexOf("USERIDS=")+8,logbuf.indexOf((char)29,logbuf.indexOf("USERIDS="))),""+(char)28);
      tablesLogOnInfoUserID = new String[st.countTokens()];      
      while (st.hasMoreTokens()) {
         tablesLogOnInfoUserID[i]=st.nextToken().toString();
         i++;
     }
      
      i=0;
      st = new StringTokenizer(logbuf.substring(logbuf.indexOf("DATABASENAMES=")+14,logbuf.indexOf((char)29,logbuf.indexOf("DATABASENAMES="))),""+(char)28);
      tablesLogOnInfoDatabaseName = new String[st.countTokens()];      
      while (st.hasMoreTokens()) {
         tablesLogOnInfoDatabaseName[i]=st.nextToken().toString();
         i++;
     }
      
      i=0;
      st = new StringTokenizer(logbuf.substring(logbuf.indexOf("SERVERNAMES=")+12,logbuf.indexOf((char)29,logbuf.indexOf("SERVERNAMES="))),""+(char)28);
      tablesLogOnInfoServerName = new String[st.countTokens()];      
      while (st.hasMoreTokens()) {
         tablesLogOnInfoServerName[i]=st.nextToken().toString();
         i++;
     }

      i=0;
      st = new StringTokenizer(logbuf.substring(logbuf.indexOf("USEDTABLES=")+11,logbuf.indexOf((char)29,logbuf.indexOf("USEDTABLES="))),""+(char)28);
      usedTables = new String[st.countTokens()];      
      while (st.hasMoreTokens()) {
         usedTables[i]=st.nextToken().toString();
         i++;
     }
     tablesinfobuffer = tempbuf;
     ctx.writeBuffer("TABLESLOGONINFO",tempbuf);
   }


   public boolean perform()
   {
      ASPManager mgr = getASPManager();

      trans.trace("Request");
      trans = mgr.perform(trans);
      trans.trace("Response");
      return true;
   } 

   public String getMultipleValues(String[] parameterval)
   {
      String str = "";
      for (int i = 0 ;i<parameterval.length;i++)
      {
         str  += parameterval[i] + (char)30; 
      } 
      str=str.substring(0,str.length()-1);
      return str;
   }
   public void generateLogOnTableContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendToHTML("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageCommandBar>\n");
      appendToHTML("<tbody>\n"); 
      appendToHTML("<tr>\n");
      appendToHTML("<td nowrap height=22>&nbsp;\n");
      appendToHTML(mgr.translate("FNDPAGESQUICKREPORTQUERYSPECIFYSOURCEPWD: You need to specify password for the following data sources"));
      appendToHTML("</tr>\n");
      appendToHTML("</tbody>\n");
      appendToHTML("</table>\n");
      appendToHTML("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageFormWithBorder >\n");
      appendToHTML("<tr><td>");
      appendToHTML("&nbsp;&nbsp;");
      appendToHTML("</td><td>");

      beginTable(null,false,true);
      beginTableTitleRow();
      printTableCell(mgr.translate("FNDPAGESQUICKREPORTQUERYTABLENAME: Table Name"));
      printTableCell(mgr.translate("FNDPAGESQUICKREPORTQUERYSERVERNAME: Server Name"));
      printTableCell(mgr.translate("FNDPAGESQUICKREPORTQUERYDBNAME: Database Name"));
      printTableCell(mgr.translate("FNDPAGESQUICKREPORTQUERYUSERID: User ID"));
      printTableCell(mgr.translate("FNDPAGESQUICKREPORTQUERYPASSWD: Password"));
      endTableTitleRow();
      beginTableBody();

      for (int i = 0; i < nOtherTables; i++ )
      {
         printHiddenField( "TableAlias", tablesLogOnInfoName[i] );

         printTableCell(tablesLogOnInfoName[i]);
         beginTableCell();
         printField( tablesLogOnInfoName[i] + ".LogOnServerName", tablesLogOnInfoServerName[i], null, 13);
         endTableCell();

         if ( tablesLogOnInfoDatabaseName[i] != "" )
         {
            beginTableCell();
            printField( tablesLogOnInfoName[i] + ".LogOnDatabaseName", " ".equals(tablesLogOnInfoDatabaseName[i])?null:tablesLogOnInfoDatabaseName[i], null);
            endTableCell();
         }
         else
         {

            beginTableCell();
            printHiddenField( tablesLogOnInfoName[i] + ".LogOnDatabaseName", tablesLogOnInfoDatabaseName[i] );
            printHiddenField( tablesLogOnInfoName[i] + ".LogOnDatabaseName", "" );
            endTableCell();
         }


         beginTableCell();
         printField( tablesLogOnInfoName[i] + ".LogOnUserID", tablesLogOnInfoUserID[i], null);
         endTableCell();

         beginTableCell();
         printPasswordField( tablesLogOnInfoName[i] + ".LogOnPassword", "", null);
         endTableCell();

         nextTableRow();
      }            

      endTableBody();
      endTable();
      appendToHTML("</td><td>");
      appendToHTML("&nbsp;&nbsp;");
      appendToHTML("</td></tr>");
      appendToHTML("</table>");
   }


   // codes are implemented for crystal and sql duplicating the orginal codes just for clarity. 
   public void generateParamTableContents(AutoString out)throws FndException
   {
      ASPManager mgr = getASPManager();

      String defaultValue = "";

      if (paramCount > 0)
      {
         appendToHTML("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageCommandBar>\n");
         appendToHTML("<tbody>\n"); 
         appendToHTML("<tr>\n");
         appendToHTML("<td nowrap height=22>&nbsp;\n");
         appendToHTML(mgr.translate("FNDPAGESQUICKREPORTQUERYTEXTPARAMETERS: Parameters"));
         appendToHTML("</tr>\n");
         appendToHTML("</tbody>\n");
         appendToHTML("</table>\n");
         appendToHTML("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageFormWithBorder >\n");
         appendToHTML("<tr><td>");
         appendToHTML("&nbsp;&nbsp;");
         appendToHTML("</td><td>");

         beginTable(null,false,false);
         beginTableTitleRow();
         printTableCell(mgr.translate("FNDPAGESQUICKREPORTQUERYTEXT_ITEM: Item"));
         printTableCell(mgr.translate("FNDPAGESQUICKREPORTQUERYTEXT_VALUE: Value"));
         endTableTitleRow();
         beginTableBody();

         if (!("SQL".equals(qr_type)))
         {
            for (int i = 0 ; i < paramCount ; i++)
            {

               paramDefaultValues = (String[])paramDefaultValuesList.get(i); // to support multiple valuses
               defaultValue = (mgr.readValue(paramNames[i]) != null) ? defaultValue = mgr.readValue(paramNames[i]) : paramDefaultValues[0];
               
               if (!"SQL".equals(qr_type))
               {
                  if ("false".equals(ismultiple[i]))
                  {
                     if (Integer.parseInt(String.valueOf(paramValueType[i])) == CrystalReport.crBooleanField)
                     {
                        printTableCell(((paramPrompts.length > 0) && (paramPrompts.length == paramNames.length) && (paramPrompts[i] != "") && (paramPrompts[i] != null)) ? paramPrompts[i] : paramNames[i]);
                        beginTableCell();
                        printCheckBox(mgr.URLEncode(paramNames[i]), "on", (defaultValue == "true" || defaultValue == "on") , null);
                        endTableCell();

                     }
                     else if (Integer.parseInt(String.valueOf(paramValueType[i])) == CrystalReport.crDateField
                              || Integer.parseInt(String.valueOf(paramValueType[i])) == CrystalReport.crDateTimeField
                              || Integer.parseInt(String.valueOf(paramValueType[i])) == CrystalReport.crTimeField)
                     {

                        String  sLanguage = mgr.getLanguageCode();
                        String sDateMask =mgr.translate("QUCIKREPORTQUERYDATEMASK: "+ mgr.getConfigParameter("LANGUAGE/" + sLanguage + "/Date/MASK", "yyyy-MM-dd"));
                        String sDateTimeMask =mgr.translate("QUCIKREPORTQUERYDATETIMEMASK: "+ mgr.getConfigParameter("LANGUAGE/" + sLanguage + "/datetime/MASK", "yyyy-MM-dd HH:mm:ss"));
                        String sTimeMask =mgr.translate("QUCIKREPORTQUERYDATETIMEMASK: "+ mgr.getConfigParameter("LANGUAGE/" + sLanguage + "/time/MASK", "HH:mm"));

                        printTableCell(((paramPrompts.length > 0) && (paramPrompts.length == paramNames.length) && (paramPrompts[i] != "") && (paramPrompts[i] != null)) ? paramPrompts[i] : paramNames[i]);
                        beginTableCell();
                        printField(mgr.URLEncode(paramNames[i]),defaultValue,null);
                        endTableCell();
                        beginTableCell();
                        if (Integer.parseInt(String.valueOf(paramValueType[i])) == CrystalReport.crDateField)
                           printImageLink(mgr.getApplicationPath()+"/common/images/calendar.gif", "javascript:showCalendar('"+paramNames[i]+"','','','','"+sDateMask+"',-1,'','false')");
                        else if (Integer.parseInt(String.valueOf(paramValueType[i])) == CrystalReport.crDateTimeField)
                           printImageLink(mgr.getApplicationPath()+"/common/images/calendar.gif", "javascript:showCalendar('"+paramNames[i]+"','','','','"+sDateTimeMask+"',-1,'','false')");
                        else
                           printImageLink(mgr.getApplicationPath()+"/common/images/calendar.gif", "javascript:showCalendar('"+paramNames[i]+"','','','','"+sTimeMask+"',-1,'','false')");
                        endTableCell();
                        if (Integer.parseInt(String.valueOf(paramValueType[i])) == CrystalReport.crDateField)
                           printTableCell(sDateMask);
                        else if (Integer.parseInt(String.valueOf(paramValueType[i])) == CrystalReport.crDateTimeField)
                           printTableCell(sDateTimeMask);
                        else
                           printTableCell(sTimeMask);


                     }
                     else
                     {
                        printTableCell(((paramPrompts.length > 0) && (paramPrompts.length == paramNames.length) && (paramPrompts[i] != "") && (paramPrompts[i] != null)) ? paramPrompts[i] : paramNames[i]);
                        beginTableCell();
                        printField(mgr.URLEncode(paramNames[i]),defaultValue,null);
                        endTableCell();

                     }


                  }
                  else
                  {
                     printTableCell(mgr.translate("FNDPAGESQUICKREPORTQUERYTEXTADDVAL: Add Value Here"));
                     beginTableCell();
                     printField("PARAMETER_VALUE"+i,"",null);
                     endTableCell();

                     beginTableCell();
                     printImageLink(mgr.getApplicationPath()+"/common/images/portlet_down.gif", "javascript:addValues("+i+")");
                     endTableCell();

                     beginTableCell();
                     printImageLink(mgr.getApplicationPath()+"/common/images/delete.gif", "javascript:removeValues("+i+")");
                     endTableCell();

                     nextTableRow();

                     printTableCell(((paramPrompts.length > 0) && (paramPrompts.length == paramNames.length) && (paramPrompts[i] != "") && (paramPrompts[i] != null)) ? paramPrompts[i] : paramNames[i]);
                     beginTableCell();
                     out.append(fmt.drawPreparedSelect("MULTIPLE_LIST"+i,fmt.populateMandatoryListBox(getListBoxBuffer(paramDefaultValues)),"size=6 multiple style='width:150;'",false ));
                     endTableCell();
                     printHiddenField(mgr.URLEncode(paramNames[i]),getMultipleValues(paramDefaultValues));
                     nextTableRow();

                  }


               }
               else
               {
               }

               nextTableRow();
            }


         }
         else
         {
            for (int i = 0 ; i < paramCount ; i++)
            {
               defaultValue = (mgr.readValue(paramNames[i]) != null) ? defaultValue = mgr.readValue(paramNames[i]) : paramDefaultValues[i];
               printTableCell(((paramPrompts[i] != "") && (paramPrompts[i] != null)) ? paramPrompts[i] : paramNames[i]);
               beginTableCell();
               printField(paramNames[i],defaultValue,null);
               endTableCell();
               
               if(paramNames[i] == "__BUFFER_SIZE")
               {
                  beginTableCell();
                  printButton("Count",mgr.translate("FNDPAGEQUICKREPORTQUERYCOUNTBUTTON: Get row count"),"Onclick=\"javascript:validateCount()\"");
                  endTableCell();
               }
               
               nextTableRow();
            }

         }


         endTableBody();
         endTable();

         appendToHTML("</td><td>");
         appendToHTML("&nbsp;&nbsp;");
         appendToHTML("</td></tr>");
         appendToHTML("</table>");

         if ( nOtherTables > 0 )
            out.append("<p><br></p>");
      }
   }
   
   /**
    * Add multiple values to the list box.
   */
   public ASPBuffer getListBoxBuffer(String[]  array)
   {
      ASPManager mgr = getASPManager();  
      ASPBuffer buf = mgr.newASPBuffer();  

      for (int i=0;i<array.length;i++)
      {
         ASPBuffer row = buf.addBuffer("LIST_BUFFER"+i);
         row.addItem("ITEM"+i,array[i]);
         row.addItem("VALUE"+i,array[i]);
      }
      return buf;
   }

   public boolean isNewParam(String s)
   {
      if ("IAL".equals(s) || "AO".equals(s) || "APPOWNER".equals(s))
         return false;
      for (int i = 0 ; i < paramCount ; i++)
      {
         if (s.equals(vparamNames.elementAt(i)))
            return false;
      }
      return true;
   }

   public int searchStringForSubstring(String strToSearch, String strToLookFor, int nStart)
   {
      for (int i = nStart ; i <= (strToSearch.length() - strToLookFor.length()) ; i++)
      {
         if (strToLookFor.equals(strToSearch.substring(i,(i + strToLookFor.length()))))
         {
            return i;
         }
      }          
      return -1; 
   }

   public int strFindFirstIn(String strText,int nIndex,String strSeparators,int nStep)
   {
      int nLength = strText.length();
      if (nStep == 0)
         nStep++;

      while (( nIndex >= 0) && (nIndex < nLength) && (searchStringForSubstring(strSeparators,strText.charAt(nIndex)+"",0) == -1))
         nIndex = nIndex + nStep;

      if ((nIndex >= 0 ) && (nIndex < nLength))
         return nIndex;
      else
         return -1;
   }

   public void extractParametersFromSQL()
   {
      ASPManager mgr = getASPManager();
      // Get all parameters from sql statement
      sql_expression = substVarExtract(sql_expression);

      // Add one more parameter for "no of rows to return"
      String max_rows = mgr.readValue("MAXROWS");
      paramNames[paramCount] = "__BUFFER_SIZE";
      paramPrompts[paramCount] = mgr.translate("FNDPAGESQUICKREPORTQUERYMAXRECFETCH: Max. no. of rows to show");
      if (mgr.isEmpty(max_rows))
         paramDefaultValues[paramCount] = mgr.getConfigParameter("ADMIN/BUFFER_SIZE");
      else
         paramDefaultValues[paramCount] = max_rows;
      paramCount++;
   }

   private boolean allParamsValuesExist()
   {
      ASPManager mgr = getASPManager();
      
      for (int i = 0; i < paramCount; i++)
      {
         if (mgr.isEmpty(paramDefaultValues[i]) && mgr.isEmpty(mgr.readValue(paramNames[i])))
            return false;
      }
      return true;
   }    
   
   public String substVarExtract(String sqlExpression)
   {
      ASPManager mgr = getASPManager();

      String sVar;
      int nStartPos;
      int nEndPos;
      String CHARS_Separators = "',. ()[]{}&!<>|=+-*@%;:\"\r\n";
      String sWhereExpression = new String(sqlExpression);
      Vector vparamDefaultValues = new Vector();
      Vector vparamHTMLName = new Vector();
      String charAt;

      nStartPos = sWhereExpression.indexOf("&");

      while (nStartPos >= 0)
      {
         nEndPos = strFindFirstIn( sWhereExpression, nStartPos + 1, new String(CHARS_Separators), 1 );

         if (nEndPos == -1)
         {
            nEndPos = sWhereExpression.length();
         }
         
         if (nEndPos > nStartPos)
         {
            charAt = ((nEndPos < sWhereExpression.length())?sWhereExpression.charAt(nEndPos)+"":"");
            int counter = 0;
            sVar = sWhereExpression.substring(nStartPos + 1, nEndPos);
            if (isNewParam(sVar))
            {
               vparamNames.add(paramCount, sVar); 
               vparamDefaultValues.add(paramCount,"");
               vparamHTMLName.add(paramCount, "P"+paramCount); 
               
               counter = paramCount;
               paramCount++;
            }
            else
            {
               for (int j=0; j<vparamNames.size(); j++)
               {
                  if (sVar.equals(""+vparamNames.elementAt(j)))
                  {
                     counter = j;
                     break;                 
                  }
               }
            }
            
            sqlExpression = sqlExpression.replaceAll("&"+sVar+charAt,"&P"+counter+charAt);
            sWhereExpression = sWhereExpression.substring(nStartPos + 1);
            nStartPos = sWhereExpression.indexOf("&");                  
         }
      }
      String[] tempArray1 = new String[paramCount+1];
      String[] tempArray2 = new String[paramCount+1];
      String[] tempArray3 = new String[paramCount+1];
      
      for (int i = 0; i < paramCount; i++)
      {
         tempArray1[i] = (String)vparamHTMLName.elementAt(i);
         tempArray2[i] = (String)vparamDefaultValues.elementAt(i);
         tempArray3[i] = (String)vparamNames.elementAt(i);
      }
      
      paramNames = tempArray1;
      paramDefaultValues = tempArray2;
      paramPrompts = tempArray3;
      
      return sqlExpression;
   }
   
   /*protected void validate()
   {
      ASPManager mgr = getASPManager();
      val = mgr.readValue("VALIDATE");
      
      int count = 100;
      if ("SQL_EXPRES".equals(val) )
      {
         
         String report_id = mgr.readValue("REPORT_ID");
         String sqlExpression = getSqlStatement(report_id);
         sqlExpression = mgr.parseCSVQueryString(sqlExpression, true);
         trans.clear();
         if(!mgr.isEmpty(sqlExpression))
         {
            
            //Construct count statement
            sqlExpression = constructCountExpression(sqlExpression);
            //Find and replace the parameters
            sqlExpression = substVarSubstitute(sqlExpression);
            sqlExpression = modifyExpression(sqlExpression);
            sqlExpression = mgr.replace(sqlExpression, "&AO..", "");
            sqlExpression = mgr.replace(sqlExpression, "&APPOWNER..", "");
            sqlExpression = mgr.replace(sqlExpression, "&IAL.", mgr.getConfigParameter("APPLICATION/IAL_OWNER"));
            ASPQuery q = trans.addQuery("COUNT", sqlExpression);
            trans = mgr.validate(trans);
            
            String rows = trans.getValue("COUNT/DATA/N");
            if(rows != null)
               mgr.responseWrite( rows + "^" );
            else
               mgr.responseWrite("0^");
         }
      }
      mgr.endResponse();
   }
   
   private String constructCountExpression(String sql_expression)
   {      
      return "select to_char(count(*)) N from (" + sql_expression + " )";
   }

   private String getSqlStatement(String report_id)
   {
      ASPManager mgr = getASPManager();
      ASPQuery qry = trans.addQuery("MASTER", "SELECT SQL_EXPRESSION " + "FROM QUICK_REPORT_NON_BI WHERE QUICK_REPORT_ID = " + report_id);
      perform();
      
      return trans.getValue("MASTER/DATA/SQL_EXPRESSION");
   }
   
   private String modifyExpression(String sql_statement)
   {
      
      boolean in_alias = false;
      AutoString new_stmt = new AutoString();
      AutoString alias    = new AutoString();
      col_alias = new HashMap();
      temp_map = new HashMap();
      
      for(int i=0; i<sql_statement.length(); i++)
      {
         
         char ch = sql_statement.charAt(i);
         
         if(ch=='"' && (sql_statement.charAt(i-1)=='=' || sql_statement.charAt(i+1)=='>'))
         {
            in_alias=false;
         }
         else if(ch=='"' && !in_alias)
         {
            in_alias=true;
         }
         else if(ch=='"' && in_alias)
         {
            in_alias=false;
            
            if( IfsNames.isId(alias+"") )
               new_stmt.append("\"",alias+"","\"");
            else
               new_stmt.append( replaceAlias(alias+"") );
            
            alias.clear();
         }
         else if(in_alias)
         {
            alias.append(ch);
         }
         else
         {
            new_stmt.append(ch);
         }
      }
      
      return new_stmt.toString();
   }
   
   protected String replaceAlias(String alias)
   {
      AutoString new_alias = new AutoString();
      int num_length = 0;
      
      if (col_alias.containsValue(alias))
      {
         new_alias.append(temp_map.get(alias).toString());
      }
      else
      {
         new_alias.append("c");
         num_length = ((col_alias.size()+1)+"").length();
         
         for (int i=1;i < (30-num_length); i++)
            new_alias.append("0");
         
         new_alias.appendInt(col_alias.size()+1);
         
         col_alias.put(new_alias.toString(),alias);
         temp_map.put(alias,new_alias.toString());
      }
      
      return new_alias.toString();
   }
   
   protected String substVarSubstitute(String sqlExpression)
   {
      String sqlExpr = sqlExpression+" ";
      
      ASPManager mgr = getASPManager();
      String value = "";
      
      sqlExpr = substVarExtract(sqlExpr);
      for (int i = 0; i < paramCount; i++)
      {
         int length = paramNames[i].length();
         value = mgr.readValue(paramNames[i]);
         
         if (mgr.isEmpty(value)) value = "";
         
         if ( sqlExpr.indexOf("'&") >0)
         {
            if (( sqlExpr.indexOf("%'") >0) && ( sqlExpr.indexOf("'%'") <0))
               sqlExpr = mgr.replace(sqlExpr, "'&" + paramNames[i] + "%'",  "'" + value + "%'");
            else
               sqlExpr = mgr.replace(sqlExpr, "'&" + paramNames[i] + "'",  "'" + value + "'");
         }

         int index = sqlExpr.indexOf("&");
         if ( index >0)
         {
            if(sqlExpr.length() > index+length+1)
            {
               char c = sqlExpr.charAt(index+length+1);
               switch(c)
               {
                  case '%':
                     sqlExpr = mgr.replace(sqlExpr, "&" + paramNames[i] +"%", value+"%");
                     break;
                  case '\'':
                     sqlExpr = mgr.replace(sqlExpr, "&" + paramNames[i] +"'", value+"'");
                     break;
                  case ')':
                     sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] +")", value+")");
                     if(sqlExpr.indexOf("&" + paramNames[i] + "\n")>0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + "\n", value + "\n");
                     }
                     if(sqlExpr.indexOf("&" + paramNames[i] + "\r")>0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + "\r", value + "\r");
                     }
                     if(sqlExpr.indexOf("&" + paramNames[i])>0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + " ", value + " ");
                     }
                     break;
                  case ' ':
                     sqlExpr =  mgr.replace(sqlExpr,"&" + paramNames[i] +" ", value+" ");
                     if(sqlExpr.indexOf("&" + paramNames[i] + ")")>0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + ")", value + ")");
                     }
                     if(sqlExpr.indexOf("&" + paramNames[i] + "\n")>0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + "\n", value + "\n");
                     }
                     if(sqlExpr.indexOf("&" + paramNames[i] + "\r")>0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + "\r", value + "\r");
                     }
                     break;
                  case '\n':
                     sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + "\n", value + "\n");
                     if(sqlExpr.indexOf("&" + paramNames[i] + ")") >0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + ")", value + ")");
                     }
                     if(sqlExpr.indexOf("&" + paramNames[i] + "\r") >0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + "\r", value + "\r");
                     }
                     if(sqlExpr.indexOf("&" + paramNames[i]) >0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + " ", value + " ");
                     }
                     break;
                  case '\r':
                     sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + "\r", value + "\r");
                     if(sqlExpr.indexOf("&" + paramNames[i] + ")") >0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + ")", value + ")");
                     }
                     if(sqlExpr.indexOf("&" + paramNames[i] + "\n") >0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + "\n", value + "\n");
                     }
                     if(sqlExpr.indexOf("&" + paramNames[i]) >0)
                     {
                        sqlExpr = mgr.replace(sqlExpr,"&" + paramNames[i] + " ", value + " ");
                     }
                     break;
                  default:
                     break;
               }
            }
            else
               sqlExpr = mgr.replace(sqlExpr, "&" + paramNames[i], value);
         }
            
      }
      return sqlExpr.toString();
   }*/

   protected AutoString getContents() throws FndException
   { 
      ASPManager mgr = getASPManager();

      String targetPage = "";
      String hiddenName = "";
      String hiddenValue = "";
      
      String path = mgr.getURL();//mgr.getProtocol() + "://" +  mgr.getApplicationDomain() + mgr.getPath();
      
      AutoString out = getOutputStream();
      out.clear();

      if ("SQL".equals(qr_type))
      {
         targetPage = "QuickReportShowSQL.page";
         //hiddenName = "__sql_expression";
         //hiddenValue = mgr.HTMLEncode(mgr.replace(sql_expression,"\"","<DQ>"));
      }
      else
      {
         targetPage = mgr.getURL();
         hiddenName = "__FILE_NAME";
         hiddenValue = file_name;
      }

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(mgr.translate("FNDPAGESQUICKREPORTQUERYTEXTQRTITLE: Quick Report") + " - " + description));
      out.append("</head>\n");

      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");

      out.append("<SCRIPT LANGUAGE=\"JavaScript\">");

      if ("SQL".equals(qr_type))
      {
         out.append("var isSQL=true;");
      }
      else
      {
         out.append("var isSQL=false;"); 
      }

      out.append("</SCRIPT>");
      out.append("<form name=\"form\" method=\"POST\" autocomplete=\"off\" action=\"");
      out.append(targetPage);
      out.append("\">\n");

      printHiddenField("__description",description);
      printHiddenField(hiddenName,hiddenValue);
      out.append(" <input type=\"hidden\" name=\"FIRST_REQUEST\" >"); 

      out.append(mgr.startPresentation(mgr.translate("FNDPAGESQUICKREPORTQUERYTEXTQRTITLE: Quick Report") + " - " + description));

      beginDataPresentation();

      if (!"TRUE".equals(bypassPage))
      {
         generateParamTableContents(out);
      }
      
      if (("SQL".equals(qr_type)) && ("TRUE".equals(mgr.readValue("BYPASS_PAGE"))))
      {    
         if (allParamsValuesExist())
         {    
            appendDirtyJavaScript("var cookie_name = f.__PAGE_ID.value;\n");
            appendDirtyJavaScript("message = new String(decodeUnicode(readCookie(cookie_name)));\n");
            appendDirtyJavaScript("if (message=='*')\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   if ( IS_EXPLORER )\n");
            appendDirtyJavaScript("      window.document.form.Submit.click(); \n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("      window.document.form.submit();\n");
            appendDirtyJavaScript("}\n");
         }
      }

      if ( nOtherTables > 0 )
         generateLogOnTableContents();

      printNewLine();
      printHiddenField("Submit", "Submit");
      printSubmitButton("Submit",mgr.translate("FNDPAGESQUICKREPORTQUERYSUMITBUTTON: Submit"),"SQL".equals(qr_type)?"":"Onclick=\"javascript:collectParamValues()\"");

      printNewLine();
      printNewLine();
      if (!mgr.isEmpty(comments))
         printReadOnlyTextArea("",comments,"",20,120,false);

      //addValues after checking the null values and remove them
      appendDirtyJavaScript("\n");
      appendDirtyJavaScript("function addValues(index)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var dstbox = eval('f.MULTIPLE_LIST'+index);\n");
      appendDirtyJavaScript("   var scrbox = eval('f.PARAMETER_VALUE'+index);\n");
      appendDirtyJavaScript("   for(ix=dstbox.length-1; ix>=0; ix--)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(dstbox.options[ix].text=='' || dstbox.options[ix].text==null)\n");
      appendDirtyJavaScript("      {  \n");
      appendDirtyJavaScript("            dstbox.options[ix] = null;\n");
      appendDirtyJavaScript("      } \n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("   dstbox.options[dstbox.length] = new Option(scrbox.value,scrbox.value);            \n");
      appendDirtyJavaScript(" scrbox.value = '';\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function removeValues(index)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var srcbox = eval('f.MULTIPLE_LIST'+index);\n");
      appendDirtyJavaScript("   for(ix=srcbox.length-1; ix>=0; ix--)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(srcbox.options[ix].selected==true)\n");
      appendDirtyJavaScript("      {  \n");
      appendDirtyJavaScript("            srcbox.options[ix] = null;\n");
      appendDirtyJavaScript("      } \n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function collectParamValues(index)\n");
      appendDirtyJavaScript("{\n");
      if (!"SQL".equals(qr_type))
         if (!"TRUE".equals(bypassPage))
         {
            
            {
               if (paramCount > 0)
               {
                  for (int i = 0 ; i < paramCount ; i++)
                  {
                     if ("true".equals(ismultiple[i]))
                     {
                        appendDirtyJavaScript("var srcbox = f.MULTIPLE_LIST",String.valueOf(i),";\n");
                        appendDirtyJavaScript("var hiddenmultiple = f.",paramNames[i],";\n");
                        appendDirtyJavaScript(" hiddenmultiple.value=''   \n");
                        appendDirtyJavaScript(" hiddenmultiple.value=getConcatValues(srcbox)   \n");

                     }
                  }
               }
            }
         }
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function getConcatValues(box)\n");
      appendDirtyJavaScript("{\n");
      if (!"TRUE".equals(bypassPage))
      {
         appendDirtyJavaScript("   var values ='' ;\n");
         appendDirtyJavaScript("   for (i=0; i<box.length; i++)\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("       values += box.options[i].text + String.fromCharCode(30);\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript(" values =values.substr(0,values.length-1);\n");
         appendDirtyJavaScript("return values ;\n");

      }
      appendDirtyJavaScript("}\n");
      
      if ("SQL".equals(qr_type))
      {
         appendDirtyJavaScript("function validateCount()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   r = __connect(\n");
         path = mgr.getASPConfig().getScriptsLocation()+ "QuickReportShowSQL.page";
         appendDirtyJavaScript(" \"",path,"?VALIDATE=SQL_EXPRES");
         appendDirtyJavaScript("&REPORT_ID=",quick_report_id,"");
         for (int i = 0 ; i < paramCount ; i++)
         {
            appendDirtyJavaScript("&"+ paramNames[i]+"=\"");
            appendDirtyJavaScript(" + encodeUnicode(window.document.form."+paramNames[i]+".value)+ \"");
         }
         appendDirtyJavaScript("\"\n");
         appendDirtyJavaScript("   );\n");
         appendDirtyJavaScript("   if( checkStatus_(r,'__BUFFER_SIZE',-1,'Max. no. of rows to show') ){\n");
         appendDirtyJavaScript("      assignValue_('__BUFFER_SIZE',-1,0);\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("}\n");
      }

      endDataPresentation(false);
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;


   }

}
