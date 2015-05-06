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
*  File        : QuickReportShowSQL.java 
*  Modified    :
*    ASP2JAVA Tool  2001-08-21  - Created Using the ASP file QuickReportShowSQL.asp
*    Ramila H  2001-08-21 - Converted.
*    Suneth M  2001-11-21 - Added drawTable() to change the graphical presentation 
*   			    of the table according to new GUI.
*    Rifki R   2002-Apr-08 - Fixed translation problems caused by trailing spaces.
*    Ramila    2002-06-18  - Bug 31080. Changed &parameter depending on if empty.
*    Suneth M  2003-02-12  - Bug 35707, Changed getContents(), Removed unnecessary script functions.
*    Chanaka A 2003-Mar-25 - Removed the setDefined() statement inside isDefined() check from run()
*    Suneth M  2003-Apr-03 - Bug 36517, Added new method replaceAliases(). Removed clone() & doReset().
*                            Changed populate(),prePopulate() & proper().
*    Chanaka A 2003-Apr-09 - Log 850. Add function to export web based QuickReports to Excel.
*    Suneth M  2003-Apr-21 - Bug 36814, Added modifyExpression() & replaceAlias(). 
*                            Changed populate() & prePopulate(). Removed replaceAliases().
*    Suneth M  2003-Jun-03 - Changed run(), populate() & exportToExcel().
*    Chanaka A 2003-Oct-11 - Modified to show the menu bar in IE. Bug ID:106512
*    Chanaka A 2003-Oct-14 - Changed drawTable() to show the correct background color for 
*                            cells. Bug ID:107631
*    Rifki R   2004-Feb-20 - Bug 40903, added CONTEXT_ID in query string when exporting to 
*                            excel, to work with new context solution. 
*    Chandana  2004-Jun-10 - Removed all absolute URLs.
*    Suneth M  2004-Jul-26 - Merged Bug 45285, Changed substVarExtract(), isNewParam() & substVarSubstitute(). 
*
* ----------------------------------------------------------------------------
* New Comments:
* 2010-09-15 - VOHELK - Bug 93000, Introduced new view, inorder to filter out new QR types 
* 2010-07-06 - amiklk - Bug 91691, Added getSSXMLHeader(),exportToSSXMLExcel()-> export excel as xml, changed exportToExcel() to call the appropriate.
* 2009-02-13 - sumelk - Bug 80461, Changed exportToExcel() to open the Excel file inside the browser.
* 2008-12-30 - dusdlk - Bug 79544, Updated exportToExcel() to call writeContentToBrowser() with file name parameter and updated getContents() to call window.open with unique window name.
* 20081218 rahelk Bug 79378, replaced parameters in case default statement too.
* 2008-12-15 - sumelk - Bug 79301, Changed getContents() to enable resizing after exported to Excel.
* 2008-12-08 - sumelk - Bug 78939, Changed run() to remove a security vulnerability in Quick Reports.
* 20081113 rahelk Bug 78060, changed substVarExtract to replace all P0..Pn parameters with value
* 2008-09-24 - sumelk - Changed run() & isNewParam() to handle SQL statements with &APPOWNER.
* 2008-04-22 - sumelk - Bug 72664, Changed exportToExcel() to append the functionality for
*                       remove group separator when exporting to Excel.
* 2008-01-24 - sumelk - Bug 70601, Changed drawTable() to avoid the string index out of range error when a field value
*                       contains a url with "http".
* 2007-08-08 - sumelk - Changed exportToExcel() to avoid the error when exporting the quick reports with empty number fields.
*
* Revision 1.7 2007-01-11            sadhlk
* Bug id 62773, Changed substVarSubstitute().
*
* Revision 1.6 2006-11-02            gegulk
* Bug id 61275, Modified the method exportToExcel() to export with the proper formatting
*
* Revision 1.5  2006/09/22 11.55.14  buhilk
* Bug id 59842, Fixed run() method to replace CSV values within sql expressions
*
* Revision 1.4  2006/09/22           sumelk
* Bug id 60578, Changed prePopulate() to send the sql statement as a parameter instead
*               of quick_report_id to the server method which has changed by FNDBAS bug 59922.
*
* Revision 1.3  2005/11/11 10:01:37  rahelk
* fixed bug with writing output channel content to browser
*
* Revision 1.2  2005/11/10 10:47:09  mapelk
* Remove CR dependencies from Quick Reports
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.3  2005/05/04 04:37:33  sumelk
* Merged the corrections for Bug 50893, Changed exportToExcel() to avoid formatting errors after exporting.
* ----------------------------------------------------------------------------
* New Comments:
* Revision 1.8  2006/07/21           sumelk
* Bug 59490, Added getFieldTypes() and preDefine(). Changed run(), prePopulate(),drawTable() and exportToExcel().
*
* Revision 1.7  2006/07/03 17:03:22  buhilk
* Bug 58216, Fixed SQL Injection threats
*
* Revision 1.6  2006/05/03           sumelk
* Bug 56859, Changed modifyExpression() & drawTable().
*
* Revision 1.5  2006/02/13           sumelk
* Merged the corrections for Bug 55543, Changed substVarSubstitute().
*
* Revision 1.4  2005/12/27 16:48:55  sumelk
* Merged the corrections for Bug 54658, Changed substVarSubstitute().
*
* Revision 1.3  2005/11/11 10:01:37  rahelk
* fixed bug with writing output channel content to browser
*
* Revision 1.2  2005/11/10 10:47:09  mapelk
* Remove CR dependencies from Quick Reports
* 
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class QuickReportShowSQL extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.QuickReportShowSQL");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPBlock utilblk;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPBuffer data;
   private ASPBuffer names;
   private String all_names;
   private String delim;
   private int position;
   private int i;
   private String CHARS_Separators;
   private ASPBuffer export_to_excel_data;
   private String export_to_excel_names;

   //================================================================================
   // ASP2JAVA WARNING: Types of the following variables are not identified by
   // the ASP2JAVA converter. Define them and remove these comments.
   //================================================================================
   private String[] paramNames;
   private String[] paramDefaultValues;
   private int paramCount; 
   private String sql_expression;
   private String description;
   private ASPQuery q;
   private int nIndex;
   private int nPos; 
   private HashMap col_alias;
   private HashMap temp_map;
   private String data_types;
   private HashMap field_types;

   //===============================================================
   // Construction 
   //===============================================================
   public QuickReportShowSQL(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }
   
   public void run() 
   {
      ASPManager mgr = getASPManager();
      // Initialize WebKit objects

      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      export_to_excel_data  = ctx.readBuffer("EXCEL_DATA");
      export_to_excel_names = ctx.readValue("EXCEL_NAMES");
      data_types            = ctx.readValue("DATA_TYPES"); 

      if (!isDefined()) 
      {
         setVersion(3);
         disableNavigate();
         disableOptions();
      }

      if ("Y".equals(mgr.getQueryStringValue("EXPORT")))
          exportToExcel();
      else if (mgr.commandLinkActivated())
      {
         eval(mgr.commandLinkFunction());
      }
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();
      else
      {
      
         // Parameter stuff
         paramNames = new String[10];
         paramDefaultValues = new String[10];
   
         sql_expression = mgr.parseCSVQueryString(ctx.readValue("SQLEXPRESSION"));
         sql_expression = mgr.replace(sql_expression,"<DQ>","\"");
            
         description = mgr.readValue("__description");
   
         //Find and replace the parameters
         sql_expression = substVarSubstitute(sql_expression);
   
         prePopulate();

         ctx.writeBuffer("EXCEL_DATA",data);
      }
   }

   protected void validate()
   {
      ASPManager mgr = getASPManager();
      int count = 100;
      
      if ("SQL_EXPRES".equals(mgr.readValue("VALIDATE")) )
      {
         String sqlExpression = getSqlStatement(mgr.readValue("REPORT_ID"));
         sqlExpression = mgr.parseCSVQueryString(sqlExpression, true);
         trans.clear();
         if(!mgr.isEmpty(sqlExpression))
         {
            //Find and replace the parameters
            sqlExpression = substVarSubstitute(sqlExpression);
            sqlExpression = modifyExpression(sqlExpression);

            //Construct count statement
            sqlExpression = constructCountExpression(sqlExpression);
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
      trans = mgr.perform(trans);
      
      return trans.getValue("MASTER/DATA/SQL_EXPRESSION");
   }
   
   public void modifyExpression()
   {
      sql_expression = modifyExpression(sql_expression);
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

      sql_statement = new_stmt.toString();
      
      ASPManager mgr = getASPManager();
      sql_statement = mgr.replace(sql_statement, "&AO..", "");
      sql_statement = mgr.replace(sql_statement, "&APPOWNER..", "");
      sql_statement = mgr.replace(sql_statement, "&IAL.", mgr.getConfigParameter("APPLICATION/IAL_OWNER"));

      return sql_statement;
   }

   public String replaceAlias(String alias)
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

         for (i=1;i < (30-num_length); i++)
            new_alias.append("0");
          
         new_alias.appendInt(col_alias.size()+1);
          
         col_alias.put(new_alias.toString(),alias);
         temp_map.put(alias,new_alias.toString());
      }
      
      return new_alias.toString();
   }
   
   public void  prePopulate()
   {
      modifyExpression();

      ASPManager mgr = getASPManager();
      int bufferSize = Integer.valueOf( mgr.getConfigParameter("ADMIN/BUFFER_SIZE") ).intValue();
      if ( !mgr.isEmpty( mgr.readValue("__BUFFER_SIZE")) )
         bufferSize = Integer.valueOf(mgr.readValue("__BUFFER_SIZE")).intValue();
      
      // Fetch the information from the database
      trans.clear();
      q=trans.addQuery("MASTER", sql_expression);
      q.setBufferSize( bufferSize );

      cmd = trans.addCustomFunction("QRDT", "Quick_Report_API.Get_Datatypes__","DATATYPES");
      cmd.addParameter("SQL_STATEMENT",sql_expression);

      trans= mgr.perform(trans);

      data = trans.getBuffer("MASTER");

      data_types = trans.getValue("QRDT/DATA/DATATYPES");
      ctx.writeValue("DATA_TYPES",data_types);
   }

   public void getFieldTypes(String input_string)
   {
      StringTokenizer field_list = new StringTokenizer(input_string,"$");
      field_types       = new HashMap();
      String field      = "";
      String field_name = "";
      String field_type = "";
      
      while( field_list.hasMoreTokens() )
      {    
         field = field_list.nextToken(); 
         if( field.indexOf("=") > 0 )
         {
            field_name = field.substring(0,field.indexOf("="));
            field_type = field.substring(field.indexOf("=")+1,field.length());  
            
            field_types.put(field_name, field_type.trim());
         }
      }
   }    
  
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      utilblk = mgr.newASPBlock("HEAD");
      utilblk.addField("DATATYPES");
      utilblk.addField("SQL_STATEMENT");
      utilblk.addField("DUMMY_NUMBER_FIELD","Number");
   }
   
   public void  populate() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      if (data.countItems() == 0)
         mgr.responseWrite(mgr.translate("FNDPAGESQUICKREPORTSHOWSQLTEXT_NO_DATA_FOUND: No data found"));
      else
      {
         names = data.getBufferAt(0);

         if (q.countRows(data) > 0)
         {
            int count = names.countItems();
            all_names = "";
            delim = "";
            position= 0;
            
            while (count > position)
            {
               if (col_alias.containsKey(names.getNameAt(position).toLowerCase())) 
                  all_names = all_names + delim + col_alias.get(names.getNameAt(position).toLowerCase());
               else
                  all_names = all_names+delim+proper(mgr.replace(names.getNameAt(position), "_", " "));

               delim=",";
               position = position+1;
            }

            ctx.writeValue("EXCEL_NAMES",all_names);
            drawTable(data,all_names);
         }
      }
   }

   public void drawTable(ASPBuffer aspbuf, String header) throws FndException
   {
    ASPManager mgr = getASPManager();
    
      int i,j, rows, cols;
      String str = null;
      StringTokenizer strtok = new StringTokenizer(header,",");
      String link = "";
      String value = "";

      getFieldTypes(data_types); 
      // Create the table heading
      rows = aspbuf.countItems(); 
         
      beginTable();
         beginTableTitleRow();
         
         //unpack and set user defined header items
         while (strtok.hasMoreTokens())
         {
             beginTableCell(true);
             printText(strtok.nextToken());
             endTableCell();
         }
         endTableTitleRow();
         beginTableBody();
         
         // Create the table contents
         for (i=0;i < (aspbuf.countItems()); i++)
         {
            ASPBuffer row = aspbuf.getBufferAt(i);
            if ("DATA".equals(aspbuf.getNameAt(i)))
            {
               cols = row.countItems();
                
               for (j=0;j < cols ; j++ )
               {
                  str = row.getValueAt(j);

                  if ("NUMBER".equals(field_types.get(row.getNameAt(j))))
                  {    
                     str = getASPField("DUMMY_NUMBER_FIELD").convertToClientString(str);
                     beginTableCell(0,0,RIGHT);
                  }
                  else
                     beginTableCell(true);

                  if(str==null)
                  {
                     printSpaces(1);
                  }
                  else
                  {
                     if ((str.trim().startsWith("<a")) && (str.trim().endsWith("</a>")))
                     {  
                        link = str.substring(str.indexOf("http"),str.indexOf(">"));
                        value = str.substring(str.indexOf(">")+1,str.indexOf("</a>"));
                        printLink(value,link);
                     }
                     else
                        printText(str);
                  }
                  endTableCell();
               }
               nextTableRow();
            }
         }
         endTableBody();
      endTable();
   }


   public boolean isNewParam(Vector v, String s) 
   {
      if ("IAL".equals(s) || "AO".equals(s) || "APPOWNER".equals(s))
         return false;
      for (i = 0; i < paramCount; i++)
      {
         if (s.equals(v.elementAt(i)))
            return false;
      }
      return true;
   }


   public int searchStringForSubstring(String strToSearch,String strToLookFor,int nStart)
   {
      return strToSearch.indexOf(strToLookFor,nStart);
   }


   public int strFindFirstIn(String strText,int nIndex,String strSeparators,int nStep) 
   {
      int nLength;
      nLength = strText.length();


      if (nStep == 0)  
         nStep++;

      while ((nIndex >= 0) && (nIndex < nLength) && (strSeparators.indexOf(String.valueOf(strText.charAt(nIndex)),0) == -1 ))
         nIndex = nIndex + nStep;

      if ((nIndex >= 0) && (nIndex < nLength))
         return nIndex;
      else
         return -1;
   }
   
   /*public void  extractParametersFromSQL()
   {
      ASPManager mgr = getASPManager();

      // Get all parameters from sql statement
      substVarExtract(sql_expression);

      // Add one more parameter for "no of rows to return"
      paramNames[paramCount] = "__BUFFER_SIZE";
      paramPrompts[paramCount] = mgr.translate("FNDPAGESQUICKREPORTSHOWSQLMAX_RECORDS_TO_FETCH: Max. no. of rows to show");
      paramDefaultValues[paramCount] = mgr.getConfigParameter("ADMIN/BUFFER_SIZE");
      paramCount++;
   }*/
   
   /*public String substVarExtract(String sqlExpression) 
   {
      String sWhereExpression = sqlExpression;
      int nStartPos; 
      int nEndPos; 
      String sVar;

      //CHARS_Separators = "',. ()[]{}\&!<>|=\+-*@%;:\"\r\n";
      CHARS_Separators = "',. ()[]!*@;:<>|-+&{}=\"\r\n%";
      nStartPos = sWhereExpression.indexOf("&");
      Vector vparamDefaultValues = new Vector();
      
      while (nStartPos >= 0)
      {
         nEndPos = strFindFirstIn( sWhereExpression, nStartPos + 1, new String(CHARS_Separators), 1 );
         if (nEndPos == -1)
         {
            nEndPos = sWhereExpression.length();
         }
         if (nEndPos > nStartPos)
         {
            // sVar = sWhereExpression.substring(nStartPos + 1, nEndPos - nStartPos - 1);
            sVar = sWhereExpression.substring(nStartPos + 1, nEndPos);
            if (isNewParam(sVar))
            {
               vparamNames.add(paramCount, sVar); 
               vparamDefaultValues.add(paramCount,"");
               paramCount++;
            }
            sWhereExpression = sWhereExpression.substring(nStartPos + 1);
            nStartPos = sWhereExpression.indexOf("&");         
         }
      }
      String[] tempArray1 = new String[paramCount+1];
      String[] tempArray2 = new String[paramCount+1];
      String[] tempArray3 = new String[paramCount+1];
      
      for (int i = 0; i < paramCount; i++)
      {
         tempArray1[i] = (String)vparamNames.elementAt(i);
         tempArray2[i] = (String)vparamDefaultValues.elementAt(i);
      }
      
      paramNames = tempArray1;
      paramDefaultValues = tempArray2;
      paramPrompts = tempArray3;
      
      return sqlExpression;
   }*/
   
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
      Vector vparamNames = new Vector();
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
            if (isNewParam(vparamNames, sVar))
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
      }
      
      paramNames = tempArray1;
      paramDefaultValues = tempArray2;
      
      return sqlExpression;
   }
   
   public String substVarSubstitute(String sqlExpression) 
   {
      String sqlExpr = sqlExpression+" ";

      ASPManager mgr = getASPManager();
      String value = "";

      sqlExpr = substVarExtract(sqlExpr);
      for (i = 0; i < paramCount; i++)
      {
         int length = paramNames[i].length(); //size of the parameter
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
                     sqlExpr = mgr.replace(sqlExpr, "&" + paramNames[i], value);
                     break;
               }
            }
            else
               sqlExpr = mgr.replace(sqlExpr, "&" + paramNames[i], value);
         }
      }
      return sqlExpr.toString();
   }


   public String proper(String sText) 
   {
      String sReturn = sText;
      String res = "";
      sReturn = sReturn.toLowerCase();
      String[] temp = split(sReturn," ");
      
      for (int i=0; i <temp.length; i++)
      {
         sReturn = temp[i];
         sReturn = String.valueOf(sReturn.charAt(0)).toUpperCase() + sReturn.substring(1);
           
         res += sReturn + " ";
      }
      return res;
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return getASPManager().translate("FNDPAGESQUICKREPORTSHOWSQLTITLE: Quick Report Preview - &1",description);
   }

   protected String getTitle()
   {
      return getASPManager().translate("FNDPAGESQUICKREPORTSHOWSQLTEXT_QR_PREVIEW_TITLE: Quick Report Preview - &1",description);
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag(mgr.translate("FNDPAGESQUICKREPORTSHOWSQLTEXT_QR_PREVIEW_TITLE: Quick Report Preview - &1",description)));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(mgr.translate("FNDPAGESQUICKREPORTSHOWSQLTITLE: Quick Report Preview - &1",description)));
      
      beginDataPresentation();

      if (data.countItems() > 0)
      {
         if (mgr.isExplorer())
         {
            printLink(mgr.translate("FNDPAGESQUICKREPORTSHOWSQLEXPORTTOEXCEL: Export to Excel"),"javascript:exportFileToExcel()");
         }
         else
         {
            printCommandLink("exportToExcel",mgr.translate("FNDPAGESQUICKREPORTSHOWSQLEXPORTTOEXCEL: Export to Excel"));
         }
         out.append("<br>");
         printNewLine();
      }

      populate(); 
      
      endDataPresentation(false);
      
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      //appendDirtyJavaScript("function exportFileToExcel(){window.open(\"QuickReportShowSQL.page?__CONTEXT_ID=\"+ f.__CONTEXT_ID.value+\"&__PAGE_ID=\" + f.__PAGE_ID.value + \"&EXPORT=Y\",\"exportWindow\",\"menubar=yes,resizable=yes left=\"+(parent.screenLeft-2)+\", top=\"+(parent.screenTop-23)+\"\");parent.close();}");
      appendDirtyJavaScript("function exportFileToExcel(){window.open(\"QuickReportShowSQL.page?__CONTEXT_ID=\"+ f.__CONTEXT_ID.value+\"&__PAGE_ID=\" + f.__PAGE_ID.value + \"&EXPORT=Y\",\"exportWindow\"+ f.__PAGE_ID.value,\"menubar=yes,resizable=yes left=\"+(parent.screenLeft-2)+\", top=\"+(parent.screenTop-23)+\"\");parent.close();}");      
      return out;

   }

   private String getSSXMLHeader()
   {
      StringBuffer xmlheadbuf = new StringBuffer();
      xmlheadbuf.append("<?xml version='1.0'?>\n");
      xmlheadbuf.append("<Workbook xmlns='urn:schemas-microsoft-com:office:spreadsheet'\n");
      xmlheadbuf.append("xmlns:o='urn:schemas-microsoft-com:office:office'\n");
      xmlheadbuf.append("xmlns:x='urn:schemas-microsoft-com:office:excel'\n");
      xmlheadbuf.append("xmlns:ss='urn:schemas-microsoft-com:office:spreadsheet'\n");
      xmlheadbuf.append("xmlns:html='http://www.w3.org/TR/REC-html40'>\n");

      return xmlheadbuf.toString();
   }

   /*---------------------------------------------------------------------------
    * Exports to Excel as XML
    * */

   public void exportToSSXMLExcel()
   {
      ASPManager mgr = getASPManager();

      getFieldTypes(data_types);

      try
      {
         int itemcount;
         int cellcount;
         ASPBuffer row;
         StringBuffer data = new StringBuffer();
         String item_str="";
         StringTokenizer st = new StringTokenizer(export_to_excel_names,",");
         String remove_group_sep = mgr.getConfigParameter("APPLICATION/QUICK_REPORTS/EXPORT_TO_EXCEL/REMOVE_DIGIT_GROUPING","N");
         String charset = mgr.getConfigParameter("APPLICATION/QUICK_REPORTS/EXPORT_TO_EXCEL/CHARSET","Unicode");
         String encoding = mgr.getConfigParameter("APPLICATION/QUICK_REPORTS/EXPORT_TO_EXCEL/ENCODING","Unicode");

         data.append( getSSXMLHeader() );
         data.append(" <Styles>\n");
         data.append("  <Style ss:ID='sFontBold'>\n");
         data.append("   <Font ss:Bold='1'/>\n");
         data.append("  </Style>\n");
         data.append("  <Style ss:ID='sNumber'> <NumberFormat ss:Format='Standard'/></Style>\n");
         data.append("  <Style ss:ID='sString'> <NumberFormat ss:Format='@'/></Style>\n");
         data.append(" </Styles>\n");

         //START WORKSHEET
         data.append(" <Worksheet ss:Name='IFS'>\n");
         data.append(" <Table>\n");

         data.append("  <Row ss:StyleID='sFontBold'>\n");

         while (st.hasMoreTokens())
         {
            data.append("   <Cell>");
            data.append("<Data ss:Type='String'> <![CDATA[" + st.nextToken() + "]]></Data>");
            data.append("</Cell>\n");
         }

         data.append("  </Row>\n");

         //WRITE DATA
         for (itemcount=0;itemcount < export_to_excel_data.countItems();itemcount++)
         {
            if ("DATA".equals(export_to_excel_data.getNameAt(itemcount)))
            {
               data.append("  <Row>\n");
               
               row = export_to_excel_data.getBufferAt(itemcount);

               for (cellcount=0;cellcount < row.countItems();cellcount++)
               {
                  item_str = (mgr.isEmpty(row.getValueAt(cellcount))?"":row.getValueAt(cellcount));
                  item_str = item_str.replace('\n',' '); // filter new line char
                  item_str = item_str.replace('\r',' '); // filter return char

                  
                  String cell_type = "";
                  if (("NUMBER".equals(field_types.get(row.getNameAt(cellcount)))) && !mgr.isEmpty(item_str))
                  {
                     cell_type = "Number";
                     item_str = mgr.formatNumber("DUMMY_NUMBER_FIELD",toDouble(item_str));

                     if ("Y".equals(remove_group_sep))
                     {
                        StringBuffer tempBuf = new StringBuffer(item_str);
                        char group_sep = getASPManager().getGroupSeparator(DataFormatter.NUMBER);

                        for (int n=0; n<tempBuf.length(); n++)
                        {
                           if (tempBuf.charAt(n)==group_sep)
                              tempBuf.deleteCharAt(n);
                        }
                        item_str = tempBuf .toString();
                     }                     
                  }
                  else
                     cell_type = "String";

                  String xmlCell ="   <Cell";
                  xmlCell += " ss:StyleID='s" + cell_type + "'>";
                  xmlCell += "<Data ss:Type='" + cell_type + "'><![CDATA[" + item_str + "]]></Data>";
                  xmlCell += "</Cell>\n";

                  data.append( xmlCell );
               }
               data.append("  </Row>\n");
            }
         }

         data.append(" </Table>\n");
         data.append("</Worksheet>\n");
         data.append("</Workbook>\n");

         mgr.writeContentToBrowser(data.toString().getBytes(encoding),"application/x-msexcel;charset="+charset);
      }

      catch(Throwable e)
      {
         error(e);
      }

      mgr.endResponse();
   }
   //---------------------------------------------------------------------------

   public void exportToExcel()
   {  
      ASPManager mgr = getASPManager();

      String exportAsXml = mgr.getConfigParameter("APPLICATION/QUICK_REPORTS/EXPORT_TO_EXCEL/EXPORT_AS_XML","Y");
      //config says xml?, xml it is
      if( "Y".equals(exportAsXml) )
      {
         exportToSSXMLExcel();
         return;
      }
      
      getFieldTypes(data_types); 

      try
      {  
         int itemcount;
         int cellcount;
         ASPBuffer row;
         String data="";
         String heading="";
         String item_str="";
         StringTokenizer st = new StringTokenizer(export_to_excel_names,",");
         String remove_group_sep = mgr.getConfigParameter("APPLICATION/QUICK_REPORTS/EXPORT_TO_EXCEL/REMOVE_DIGIT_GROUPING","N");
         String charset = mgr.getConfigParameter("APPLICATION/QUICK_REPORTS/EXPORT_TO_EXCEL/CHARSET","Unicode");
         String encoding = mgr.getConfigParameter("APPLICATION/QUICK_REPORTS/EXPORT_TO_EXCEL/ENCODING","Unicode");

         while (st.hasMoreTokens())
         {
            data += st.nextToken() + "\t";
         }

         /*for (cellcount=0;cellcount < export_to_excel_data.getBufferAt(0).countItems();cellcount++)
         {
            data += proper(mgr.replace(export_to_excel_data.getBufferAt(0).getNameAt(cellcount), "_", " ")) + "\t";
         }*/
         data +="\n";

         for (itemcount=0;itemcount < export_to_excel_data.countItems();itemcount++)
         {  
            if ("DATA".equals(export_to_excel_data.getNameAt(itemcount)))
            {  
               row = export_to_excel_data.getBufferAt(itemcount);

               for (cellcount=0;cellcount < row.countItems();cellcount++)
               {  
                  item_str = (mgr.isEmpty(row.getValueAt(cellcount))?"":row.getValueAt(cellcount));
                  item_str = item_str.replace('\n',' '); // filter new line char
                  item_str = item_str.replace('\r',' '); // filter return char

                  if (("NUMBER".equals(field_types.get(row.getNameAt(cellcount)))) && !mgr.isEmpty(item_str))
                  {
                     item_str = mgr.formatNumber("DUMMY_NUMBER_FIELD",toDouble(item_str)); 
                     
                     if ("Y".equals(remove_group_sep))
                     {
                        StringBuffer tempBuf = new StringBuffer(item_str); 
                        char group_sep = getASPManager().getGroupSeparator(DataFormatter.NUMBER);
                           
                        for (int n=0; n<tempBuf.length(); n++)
                        {
                           if (tempBuf.charAt(n)==group_sep)
                              tempBuf.deleteCharAt(n);
                        }
                        item_str = tempBuf .toString();                         
                     } 
                  }
                  data += item_str + "\t"; 
               }
               data +="\n";
            }
         }
         
         mgr.writeContentToBrowser(data.getBytes(encoding),"application/x-msexcel;charset="+charset);
      }
      
      catch(Throwable e)
      {
         error(e);
      }

      mgr.endResponse();
   }

}
