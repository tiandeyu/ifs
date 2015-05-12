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
 * File        : MyMessages.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified     :
 *    Chandana D: 2001-Sep-21 - Created.
 *    Suneth M  : 2003-Jul-18 - Added checkReference(). Changed run() & preDefine().
 *    Chandana D: 2003-Sep-17 - Changed the Title to 'Personal Messages'.
 * ----------------------------------------------------------------------------
 *
 * New Comments:
 *
 * 2006-Jun-30 buhilk Bug 58216, Fixed SQL Injection threats
 * 2006-Sep-08 sumelk Bug 60388, Changed submitCustomization().
 */

package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import java.util.*;


/**
 * This example shows how to create a simple block with a table, how to customize
 * a portlet and how to browse between record sets in a table. Even example of
 * customized auto search function and using of doReset().
 * Using of JavaScript functions.
 */
public class MyMessages extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.MyMessages");

   private static int size = 5;


   //==========================================================================
   //  Instances created on page creation (immutable attributes)
   //==========================================================================

   private ASPBlock   blk;
   private ASPBlock dummy_blk;
   private ASPRowSet  rowset;
   private ASPTable   tbl;
   private String details_url;
   private ASPField dateField;
   
   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient int                  skip_rows;
   private transient int                  db_rows;
   private transient boolean              find;
   private transient ASPTransactionBuffer trans;
   private transient String               usercmd;
   

   //==========================================================================
   //  Profile variables (temporary)
   //==========================================================================

   private transient String   user_id;
   private transient boolean  autosearch;
   private transient String   mintitle;
   private transient String   maxtitle;
   private transient String   maxrows;
   private transient boolean  unreadOnly;
   private transient boolean  hideDate;
   private transient boolean  showAllRadio;
   private transient boolean  forLastDaysRadio;
   private transient String   forLastDays;
   
 
   /**
    * The only mandatory function - constructor
    */
   public MyMessages( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }

   public ASPPoolElement clone( Object obj ) throws FndException
   {
      if(DEBUG) debug(this+": MyMessages.clone()");

      MyMessages page = (MyMessages)(super.clone(obj));

      page.mintitle   = null;
      page.maxtitle   = null;
      page.maxrows    = null;
      page.details_url= null;
      page.unreadOnly = true;
      page.hideDate   = false;
      page.showAllRadio     =true;
      page.forLastDaysRadio=false;
      page.forLastDays=null;
      page.dateField= page.getASPField(dateField.getName());
    
      return page;
   }
   
   /**
    * Very important function. Called after the execution is completed just before
    * releasing (unlock) the page in the page pool. All temporary and user depended
    * data should be cleared here. All mutable attributes should be restored to
    * their original values here.
    */
   protected void doReset() throws FndException
   {
      super.doReset();
      trans      = null;
      usercmd    = null;
      mintitle   = null;
      maxtitle   = null;
      maxrows    = null;
      details_url= null;
      unreadOnly = true;
      hideDate   = false;
      showAllRadio = true;
      forLastDaysRadio= true;
      forLastDays=null;
    }


   /**
    * Description that will be shown on the 'Customize Portal' page.
    */
   public static String getDescription()
   {
      return "FNDMYMSGDESC: Personal Messages";
   }

   public String getTitle(int mode)
   {
      if(DEBUG) debug(this+": MyMessages.getTitle()");

      if( mode==MINIMIZED )
         return mintitle;
      else if( mode==MAXIMIZED )
         return maxtitle;
      else
         return translate("FNDMYMSGCUSTTITLE: Customize Personal Messages");
   }


   /**
    * Creation of all page objects.
    */
   protected void preDefine() throws FndException
   {
      blk = newASPBlock("MAIN");
      ASPManager mgr=getASPManager();
      ASPCommandBar cmdbar=blk.newASPCommandBar();

      addField(blk, "OBJID").setHidden();

      addField(blk, "OBJVERSION").setHidden();

      addField(blk, "MESSAGE_ID").setHidden();
      
      addField(blk, "IDENTITY").setHidden();
      
      addField(blk, "PARAMETERS").setHidden();

      addField(blk, "REFERENCE").setHidden();
      
      addField(blk, "MESSAGE").
         setLabel("FNDMYMSMESSAGE: Message");
      
      addField(blk, "READ").
         setCheckBox("FALSE,TRUE").
         setHidden();
      
      addField(blk, "ISREAD").
         setFunction("decode(READ,'TRUE','"+mgr.translate("FNDMYMSREADYES: YES")+"','"+mgr.translate("FNDMYMSREADNO: NO")+"')").
         setLabel("FNDMYMSREAD: Read");
      
      addField(blk, "ISREFAVAIL").
          setFunction("NULL").
          setHidden();

      dateField=addField(blk, "CREATE_DATE","Date").
         setLabel("FNDMYMSGDATECREATED: Date");

      blk.setView("FND_EVENT_MY_MESSAGES");
      
      tbl = newASPTable( blk );
       
      cmdbar.addCustomCommand("SETASREAD","FNDMYMSGSETASREAD: Read");
      cmdbar.addCommandValidConditions("SETASREAD","READ","Enable","FALSE");
      
      cmdbar.addCustomCommand("DETAILS","FNDMYMSGDETAILS: Details");
      cmdbar.addCommandValidConditions("DETAILS","REFERENCE","Disable","null");
      cmdbar.addCommandValidConditions("DETAILS","ISREFAVAIL","Disable","FALSE");
      
      tbl.disableNoWrap();

      dummy_blk =  newASPBlock("DUMMY");
         addField(dummy_blk, "FND_USER");
         
         addField(dummy_blk, "FROM_DATE","Date");

         addField(dummy_blk, "TO_DATE","Date");

         addField(dummy_blk, "MSG_HISTORY", "Number");

      appendJavaScript( "function prevCust(obj,user_id)",
                        "{",
                        "   getPortletField(user_id,'CMD').value = 'PREV';",
                        "}\n");

      appendJavaScript( "function nextCust(obj,user_id)",
                        "{",
                        "   getPortletField(user_id,'CMD').value = 'NEXT';",
                        "}\n");

      appendJavaScript( "function findCust(obj,user_id)",
                        "{",
                        "   getPortletField(user_id,'CMD').value = 'FIND';",
                        "}\n");

      appendJavaScript( "function customizeMyMesseges(obj,user_id)",
                        "{",
                        "   customizeBox(user_id);",
                        "}\n");

      init();
   }


   /**
    * Initialization of variables. There are no mutable attributes so there is no
    * need to implement the clone() functions. All page objects (ASPBlock,ASPField,etc)
    * will be automatically cloned by the superclass.
    * Variables skip_rows, db_rows and user_id should be reset in the doReset() function
    * before unlock in the page pool, but this function is not implemented in this basic
    * example.
    */
   protected void init()
   {
      blk    = getASPBlock("MAIN");
      rowset = blk.getASPRowSet();
      tbl    = getASPTable(blk.getName());
      usercmd = readValue("CMD");

      try
      {
         if (Str.isEmpty(usercmd))
         {
            if (cmdBarCustomCommandActivated())
               usercmd = getCmdBarCustomCommandId();
            else if (cmdBarStandardCommandActivated())
               usercmd = getCmdBarStandardCommandId();
         }
      
      }
      catch(Throwable any)
      {
         user_id = null;
      }
     
      mintitle = readProfileValue("MINTITLE", translate(getDescription()) );
      maxtitle = readProfileValue("MAXTITLE", translate(getDescription()) );
      maxrows = readProfileValue("NOROWS", "5" );
      size=Integer.parseInt(maxrows);
      unreadOnly = readProfileFlag("UNREADONLY",true);
      hideDate  = readProfileFlag("HIDEDATE",false);
      showAllRadio  = readProfileFlag("FROMTO",true);
      forLastDays= readProfileValue("FORLASTDAYS");
      
      ASPContext ctx = getASPContext();

      skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
      db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

      String cmd = readValue("CMD");
      
      if( "PREV".equals(cmd) && skip_rows>=size )
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


   /**
    * Run the business logic here. Just query the database in this case.
    */
   protected void run() throws FndException
   {
         ASPManager mgr = getASPManager();
         trans = getASPManager().newASPTransactionBuffer();
         
         if(hideDate)
            dateField.setHidden();
            
         
         if ("DETAILS".equals(usercmd))
         {
            rowset.selectRows();
               rowset.setFilterOn();
               String sReference = rowset.getValue("REFERENCE");
               String sParameters= rowset.getValue("PARAMETERS");
               appendDirtyJavaScript("submitPortlet('"+getId()+"');");//To refresh portlet to clear CMD
               rowset.setFilterOff();
               rowset.unselectRows();
               details(sReference,sParameters);
         }
         else
         {

            if ("SETASREAD".equals(usercmd))
            {
            rowset.selectRows();
               rowset.setFilterOn();
               String sMessageId = rowset.getValue("MESSAGE_ID");
               String sIdentity     = rowset.getValue("IDENTITY");
               rowset.setFilterOff();
      
               ASPCommand  cmdbuf = mgr.newASPCommand();
   
               cmdbuf = trans.addCustomCommand("SUBS", "Fnd_Event_My_Messages_API.Mark_As_Read");
               cmdbuf.addParameter(this,"MESSAGE_ID",sMessageId);
               cmdbuf.addParameter(this,"IDENTITY",sIdentity);
            }
            
            ASPQuery qry = trans.addQuery(blk);
            qry.addWhereCondition("IDENTITY = Fnd_Session_API.Get_Fnd_User");
            qry.setOrderByClause("CREATE_DATE DESC");

            if(!showAllRadio)
            {  
               if(forLastDays!=null)
                  qry.addWhereCondition("CREATE_DATE >= SYSDATE - ? - 1");
                  qry.addParameter(this, "MSG_HISTORY",forLastDays);
            }
            
            if(unreadOnly)
               qry.addWhereCondition("READ ='FALSE'");
            qry.includeMeta("ALL");
            qry.setBufferSize(size);
            qry.skipRows(skip_rows);
   
            submit(trans);
         }
         checkReference();
         db_rows = blk.getASPRowSet().countDbRows();
         getASPContext().writeValue("DB_ROWS", db_rows+"" );
      }

   /**
    * Print the HTML contents of the portlet.
    */
   public void printContents() throws FndException
   {
      printHiddenField("CMD","");
      printNewLine();
      if (rowset.countRows()>0)
      {
         printTable(tbl);
   
         if(details_url!=null)
            appendDirtyJavaScript("window.open('"+details_url+"','details_page','scrollbars,resizable,status=yes,width=770,height=500')");
         
         if(size < db_rows )
         {
            printNewLine();
   
            if(skip_rows>0)
               printSubmitLink("FNDMYMSGPRVLNK: Previous","prevCust");
            else
               printText("FNDMYMSGPRVTXT: Previous");
            
            printSpaces(5);
            String rows = translate("FNDMYMSGROWS: (Rows &1 to &2 of &3)",
                                    (skip_rows+1)+"",
                                    (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                    db_rows+"");
            printText( rows );
            printSpaces(5);
   
            if(skip_rows<db_rows-size)
               printSubmitLink("FNDMYMSGNEXTLNK: Next","nextCust");
            else
               printText("FNDMYMSGNEXTTXT: Next");
   
            printNewLine();
            printNewLine();
         }
      }
      else
      {
         printText("FNDMYMSGNORECORDS: No records available");
      }
   }


   /**
    * If the portlet should be customizable this function must return true.
    */
   public boolean canCustomize()
   {
      return true;
   }

   /**
    * Print the HTML code for the customize mode.
    */
   public void printCustomBody() throws FndException
   {

       printNewLine();

       beginTransparentTable();
         beginTableTitleRow();
           printTableCell("FNDMYMSGTITLEMSG: Choose your own title for this portlet",3,0,LEFT);
         endTableTitleRow();
         beginTableBody();
           beginTableCell();
             printSpaces(6);
             printText("FNDMYMSGMAXTITLE: when maximized:");
           endTableCell();
           beginTableCell(2);
             printField("MAXTITLE",maxtitle,50);
           endTableCell();
         nextTableRow();
           beginTableCell();
             printSpaces(6);
             printText("FNDMYMSGMINTITLE: and when minimized:");
           endTableCell();
           beginTableCell(2);
             printField("MINTITLE",mintitle,50);
           endTableCell();
         nextTableRow();
           beginTableCell();
             printText("FNDMYMSGNOROWS: Max rows to display:");
           endTableCell();
           beginTableCell(2);
             printField("NOROWS",maxrows,10);
           endTableCell();
         nextTableRow();
           beginTableCell();
             printText("FNDMYMSGDISPUNREAD: Display unread messages only:");
           endTableCell();
           beginTableCell(2);
             printCheckBox("UNREADONLY",unreadOnly);
         endTableCell();
         nextTableRow();
            beginTableCell();
               printText("FNDMYMSGDHIDEDATE: Hide Date:");
            endTableCell();
            beginTableCell(2);
               printCheckBox("HIDEDATE",hideDate);
            endTableCell();
         nextTableRow();
            beginTableCell();
               printText("FNDMYMSGSHWFROMTO: Show");
            endTableCell();
         nextTableRow();
            beginTableCell();
               printSpaces(6);
               printRadioButton("FROMTO","FORLASTDAYSVAL","FNDMYMSGSHWFORLASTDAYSRADIO: messages received for the last",!showAllRadio);
            endTableCell();
            beginTableCell();
               printField("FORLASTDAYS",forLastDays,20);
               printText("FNDMYMSGFORTHELASTDAYSTXT:  day(s)");
            endTableCell();
         nextTableRow();
            beginTableCell();
               printSpaces(6);
               printRadioButton("FROMTO","FROMTOVAL","FNDMYMSGSHWFROMTORADIO: all messages",showAllRadio);
            endTableCell();
         endTableBody();
       endTable();

       printNewLine();
  
   }


   /**
    * Save values of variables to profile and context if the user press OK button.
    */
   public void submitCustomization()
   {
      mintitle  = readValue("MINTITLE");
      maxtitle  = readValue("MAXTITLE");
      maxrows   =readValue("NOROWS");
            
      unreadOnly="TRUE".equals(readValue("UNREADONLY"));
      hideDate  ="TRUE".equals(readValue("HIDEDATE"));
      writeProfileValue("MINTITLE", mintitle );
      writeProfileValue("MAXTITLE", maxtitle );
      writeProfileFlag("UNREADONLY", unreadOnly);
      writeProfileFlag("HIDEDATE", hideDate);

      showAllRadio="FROMTOVAL".equals(readValue("FROMTO"));
      forLastDays  =readValue("FORLASTDAYS");
      writeProfileFlag("FROMTO", showAllRadio);
      writeProfileValue("FORLASTDAYS", forLastDays);
      
      try 
      {
         if ( !Str.isEmpty(maxrows))
            size = Integer.parseInt(maxrows);
         else 
            size = 5; 
      }
      catch(NumberFormatException e) 
      {
      // when maxrows is not a number
         size = 5;
      }

      writeProfileValue("NOROWS", size+"");

      skip_rows = 0;
      getASPContext().writeValue("SKIP_ROWS","0");
   }

   public void details(String ref,String param)
   {
      String tok1=null;
      String tok2=null;
      
      // change these constants as required by the query string
      final String attribute_separator=",";
      final String value_separator="=";

      if(param==null)
         details_url = getASPManager().getConfigParameter("APPLICATION/LOCATION/ROOT")+ref;
      else
      {
         try
         {
            StringTokenizer st1=new StringTokenizer(param,attribute_separator);
            StringBuffer sb=new StringBuffer();
            
            while(st1.hasMoreTokens())
            {
              tok1= st1.nextToken();
              StringTokenizer st2=new StringTokenizer(tok1,value_separator);
              while(st2.hasMoreTokens())
              {
                tok2= st2.nextToken();
                sb.append(tok2);
                sb.append("=");
                tok2= st2.nextToken();
                sb.append(getASPManager().URLEncode(tok2));
                sb.append("&");
              }
      
            }
   
         details_url=getASPManager().getConfigParameter("APPLICATION/LOCATION/ROOT")+
                     ref+"?"+sb.toString().substring(0,sb.length()-1);
         }catch(Exception e){getASPManager().showAlert("Invalid format in Parameter String\n"+param);}
      }
   }

   public void checkReference()
   {
       int count;
       String avail;
       String ref;
       ASPBuffer r;

       rowset.first();
       
       for ( count=0; count < rowset.countRows(); ++count)
       { 
          r = rowset.getRow();
          ref = r.getValue("REFERENCE");

          if ((!getASPManager().isEmpty(ref)) && (ref.endsWith(".asp") || ref.endsWith(".page")) && getASPManager().isPresentationObjectInstalled(ref))
              avail = "TRUE";
          else
              avail = "FALSE";
          
          r.setValue("ISREFAVAIL",avail);
          
          rowset.setRow(r);
          rowset.next();
       }
   }
}
