/*
 *                 IFS Research & Development
 *
 *  THIS program is protected by copyright law and by international
 *  conventions. All licensing, renting, lending or copying (including
 *  for private use), and all other use of the program, which is not
 *  expressively permitted by IFS Research & Development (IFS), is a
 *  violation of the rights of IFS. Such violations will be reported to the
 *  appropriate authorities.
 *
 *  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
 *  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
 * ----------------------------------------------------------------------------
 * File        java
 * Description : Handles all the Context Substitution Variables and stores the values
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 * Amil       2010-Jan-21   - Bug id: 87966, Changed getValue() getCSVValue(),
 *                            convertToQueryOnlyCSVValue()
 * Buddika    2007-Mar-18   - Bug id: 81107, Added #NOW# csv value to variable set.
 * Buddika    2007-Mar-04   - Bug id: 81107, Added hasCSVNames() and getValue(). Modified getCSVValue().
 * Buddika    2007-Feb-07   - Bug id: 63433, Added supprt for macro csv's by modifying initDynamicCleintResolvedCSV()
 * Mangala    2006-Sep-24   - Improved CSV code Improved CSV code 
 * Buddika    2006-Sep-21   - Improved parseCSVQueryString() method
 * Buddika    2006-Sep-21   - removed all performConfig() method calls
 * Buddika    2006-Sep-11   - Added method parseQueryString() to replace CSV's inside query string
 * Mangala    2006-Sep-09   - Improved
 * Budhika    2006-Aug-22   - Created.
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import java.io.IOException;
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;

public class ContextSubstitutionVariables implements java.io.Serializable {

   //===============================================================
   // Static constants (Immutable)
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ContextSubstitutionVariables");

   public static final String VARIABLE_PREFIX = "#";
   public static final String VARIABLE_SUFFIX = "#";
   public static final String STATIC_CSV_SESSION = "STATIC_CSV_SESSION";

   public static final int LOW_END = 10;
   public static final int HIGH_END = 11;
   public static final int PERIOD = 12;
   
   public static final int DATE_TYPE = 1;
   public static final int STRING_TYPE = 2;
   public static final int NUMBER_TYPE = 3;
   
   public static final int All_CSV = 1;
   public static final int STATIC_CSV = 2;
   public static final int DYNAMIC_CSV = 3;
   public static final int SERVER_CSV = 4;
   public static final int CLIENT_CSV = 5;
   
   public static final int RESOLVED_BY_CLIENT = 1;
   public static final int RESOLVED_BY_SERVER = 2;
   
   public static final String TODAY = "#TODAY#";
   public static final String TOMORROW = "#TOMORROW#";
   public static final String YESTERDAY = "#YESTERDAY#";
   public static final String SYSDATE = "#SYSDATE#";
   
   public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd-HH.mm.ss";
   public static final String ORACLE_DATE_FORMAT = "yyyy-mm-dd-hh24.mi.ss";
   
   private static final String[] search_tips = new String[] {"..",">=","<=",">","<","!=","!","%",";"};
   
   //===============================================================
   // Mutable variables
   //===============================================================
   private HashMap static_cache;
   private HashMap dynamic_cache;
   
   /**
    * Initialise Context Substitution Variables 
    * @param mgr ASPManager to be used for the initialiSatin.
    */
   void init(ASPManager mgr)
   {
      if (DEBUG) debug(this+": init(ASPManager)");
      
      if (!ContextSubstitutionVariableRegistry.isInitialized()) 
         ContextSubstitutionVariableRegistry.init(mgr) ;
      
      initStaticCSV(mgr);
      initDynamicCSV(mgr);
   }
   
   /**
    * Reset Context Substitution Variables 
    * @param mgr ASPManager to be used for the reset.
    */
   void reset(ASPManager mgr)
   {
      if (DEBUG) debug(this+": reset(ASPManager)");
      
      if (ContextSubstitutionVariableRegistry.isInitialized()){
         ContextSubstitutionVariableRegistry.reset();
         HttpSession session = mgr.getAspSession();
         session.setAttribute(STATIC_CSV_SESSION, null);
         init(mgr);
      }
   }
   
   /**
    * Initialise Static Context Substitution Variables
    * @param mgr ASPManager to be used for the initialiSatin.
    */
   private void initStaticCSV(ASPManager mgr)
   {
      if (DEBUG) debug(this+": initStaticCSV(ASPManager)");
      
      HttpSession session = mgr.getAspSession();
      static_cache = (HashMap) session.getAttribute(STATIC_CSV_SESSION);
      
      if(static_cache==null){
         if (DEBUG) debug("static cache not found. initialize it!!");
         static_cache = new HashMap();
         initStaticClientResolvedCSV(mgr);
         initStaticServerResolvedCSV(mgr);
         session.setAttribute(STATIC_CSV_SESSION, static_cache);
      }

      if (DEBUG) {
         debug("===================================================");
         debug(this+": Static Context Substitution Variables");
         debug("===================================================");
         debugContextSubVars(static_cache);
         debug("===================== END =========================");
      }
   }
   
   /**
    * Register the Dynamic CSV according to DB values
    * @param name Name of Static CSV
    * @param obj Object to stored whithin the CSV HashMap
    * @param mgr ASPManager to manage obj
    */
   private void registerStaticCSV(String name, CSVObject obj, ASPManager mgr)
   {
      if (DEBUG) debug("Rigstering Static CSV: " + name + ":"+ obj);
      static_cache.put(name,obj);
   }   
   
   /**
    * Initialise the Static Client Resolved  CSVs
    * @param mgr ASPManager to init the static client resolved csv's
    */
   private void initStaticClientResolvedCSV(ASPManager mgr)
   {
      if (DEBUG) debug("Initialize Client Resolved Statics CSV!!");
      //if(ContextSubstitutionVariableRegistry.isClientCSV("BASE_URL"))
      registerStaticCSV("#BASE_URL#", new CSVObject(STRING_TYPE, mgr.getASPConfig().getProtocol()+"://"+mgr.getASPConfig().getApplicationDomain()+mgr.getASPConfig().getApplicationPath(), RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("USER_ID"))
         registerStaticCSV("#USER_ID#", new CSVObject(STRING_TYPE, mgr.getFndUser(), RESOLVED_BY_CLIENT), mgr );
   }
   
   /**
    * Initialise the Static Server Resolved  CSVs
    * @param mgr ASPManager to init the static server resolved csv's
    */
   private void initStaticServerResolvedCSV(ASPManager mgr)
   {
      if (DEBUG) debug("Initialize Server Resolved Statics CSV!!");   
      if (ContextSubstitutionVariableRegistry.getServerStaticCSVSet().size()==0) return;
       
      if (DEBUG) debug("CSV DB CALL:::::: Initialize Server Resolved Statics CSV!!");   
      //server value init here
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      Iterator it = ContextSubstitutionVariableRegistry.getServerStaticCSVSet().iterator();
      while(it.hasNext()){ //loop through the server csv and add functions to get value
         cmd = mgr.newASPCommand();
         cmd.defineCustomFunction("CONTEXT_SUBSTITUTION_VAR_API.Get_String__");
         cmd.addParameter("CSV_VALUE", "S", null, null);
         String csv = it.next().toString();
         cmd.addParameter("NAME", "S", "IN", csv);
         trans.addCommand(csv,cmd);
      }
      
      trans = mgr.perform(trans);
      
      it = ContextSubstitutionVariableRegistry.getServerStaticCSVSet().iterator();
      while(it.hasNext()){ //loop through the server csv and get value from transaction buffer
         String csv = it.next().toString();
         String value = trans.getBuffer(csv+"/DATA").getValue("CSV_VALUE");
         int data_type = STRING_TYPE;
         try{
            Message msg = new Message(value);
            String dt = "String";
            if(msg.toString().indexOf("VALUE")!=-1)
               value = msg.getStringAttribute("VALUE");
            else
               value = "";
            
            if(msg.toString().indexOf("DATATYPE")!=-1)
               dt = msg.getStringAttribute("DATATYPE");
            
            if("Date".equals(dt))
               data_type = DATE_TYPE;
            else if("String".equals(dt))
               data_type = STRING_TYPE;
            else if("Number".equals(dt))
               data_type = NUMBER_TYPE;
         }
         catch(Exception e)
         {
            value = "";
            mgr.showError(e.toString());
         }
         if(!Str.isEmpty(value))
            registerStaticCSV(toCSVName(csv), new CSVObject(data_type, value, RESOLVED_BY_SERVER), mgr);
      }
   }
   
   /**
    * Initialise Dynamic Context Substitution Variables
    * @param mgr ASPManager to init the dynamic csv's
    */
   private void initDynamicCSV(ASPManager mgr)
   {
      if (DEBUG) debug(this+": initDynamicCSV(ASPManager)");

      /** Putting Dynamic Context Substitution Variables into the HashMap */
      dynamic_cache = new HashMap();
      
      initDynamicCleintResolvedCSV(mgr);
      initDynamicServerResolvedCSV(mgr);

      if (DEBUG) {
         debug("===================================================");
         debug(this+": Dynamic Context Substitution Variables");
         debug("===================================================");
         debugContextSubVars(dynamic_cache);
      }
   }
   
   /**
    * Register the Dynamic CSV according to DB values
    * @param name Name of Dynamic CSV
    * @param obj Object to stored whithin the CSV HashMap
    * @param mgr ASPManager to manage obj
    */
   private void registerDynamicCSV(String name, CSVObject obj, ASPManager mgr)
   {
      if (DEBUG) debug("Rigstering Dynamic CSV: " + name + " : " + obj);
      dynamic_cache.put(name,obj);
   }

   /**
    * Initialise the Dynamic Client Resolved  CSVs
    * @param mgr ASPManager to init the dynamic client resolved csv's
    */
   private void initDynamicCleintResolvedCSV(ASPManager mgr)
   {
      //Here we have all known client resolved CSVs. But registration should chack whther it is defined
      if (DEBUG) debug("Initialize Client Resolved Dynamic CSV!!");   
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("USER_LANGUAGE"))
         registerDynamicCSV("#USER_LANGUAGE#", new CSVObject(STRING_TYPE, mgr.getUserLanguage(), RESOLVED_BY_CLIENT), mgr );

      // Calendar object to find Day, Month ot Year
      Calendar calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      SimpleDateFormat  csv_fmt = new SimpleDateFormat(SERVER_DATE_FORMAT);

      if(ContextSubstitutionVariableRegistry.isClientCSV("NOW"))
         registerDynamicCSV("#NOW#", new CSVObject(DATE_TYPE, csv_fmt.format(calendar.getTime()) ,RESOLVED_BY_CLIENT), mgr);
      
      /** Putting Dynamic Date Context Substitution Variables into the HashMap */
      String st_time  = csv_fmt.format(startOfTime(calendar).getTime());
      String end_time = csv_fmt.format(endOfTime(calendar).getTime());
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_TODAY"))
         registerDynamicCSV("#START_OF_TODAY#", new CSVObject(DATE_TYPE,st_time ,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_TODAY"))
         registerDynamicCSV("#END_OF_TODAY#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("TODAY"))
         registerDynamicCSV("#TODAY#", new CSVObject(DATE_TYPE, st_time +".."+end_time ,RESOLVED_BY_CLIENT,true), mgr);
      if(ContextSubstitutionVariableRegistry.isClientCSV("SYSDATE"))
         registerDynamicCSV("#SYSDATE#", new CSVObject(DATE_TYPE, st_time +".."+end_time ,RESOLVED_BY_CLIENT,true), mgr);

      calendar.set(Calendar.DAY_OF_WEEK,calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.set(Calendar.DAY_OF_WEEK,calendar.getActualMaximum(Calendar.DAY_OF_WEEK));      
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_THIS_WEEK"))
         registerDynamicCSV("#START_OF_THIS_WEEK#", new CSVObject(DATE_TYPE,st_time ,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_THIS_WEEK"))
         registerDynamicCSV("#END_OF_THIS_WEEK#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
         registerDynamicCSV("#THIS_WEEK#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);

      calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_THIS_MONTH"))
         registerDynamicCSV("#START_OF_THIS_MONTH#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_THIS_MONTH"))
         registerDynamicCSV("#END_OF_THIS_MONTH#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
         registerDynamicCSV("#THIS_MONTH#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);

      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_THIS_YEAR"))
         registerDynamicCSV("#START_OF_THIS_YEAR#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_THIS_YEAR"))
         registerDynamicCSV("#END_OF_THIS_YEAR#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
         registerDynamicCSV("#THIS_YEAR#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);

      // Calculating Past Time Periods
      calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      calendar.roll(Calendar.DAY_OF_YEAR,false);
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_YESTERDAY"))
         registerDynamicCSV("#START_OF_YESTERDAY#", new CSVObject(DATE_TYPE,st_time ,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_YESTERDAY"))
         registerDynamicCSV("#END_OF_YESTERDAY#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("YESTERDAY"))
         registerDynamicCSV("#YESTERDAY#", new CSVObject(DATE_TYPE, st_time +".."+end_time ,RESOLVED_BY_CLIENT,true), mgr);

      calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      calendar.roll(Calendar.WEEK_OF_YEAR,false);
      calendar.set(Calendar.DAY_OF_WEEK,calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.set(Calendar.DAY_OF_WEEK,calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_LAST_WEEK"))
         registerDynamicCSV("#START_OF_LAST_WEEK#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_LAST_WEEK"))
         registerDynamicCSV("#END_OF_LAST_WEEK#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
         registerDynamicCSV("#LAST_WEEK#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);
      
      calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      calendar.roll(Calendar.MONTH,false);
      calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_LAST_MONTH"))
         registerDynamicCSV("#START_OF_LAST_MONTH#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_LAST_MONTH"))
         registerDynamicCSV("#END_OF_LAST_MONTH#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
         registerDynamicCSV("#LAST_MONTH#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);
      
      calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      calendar.roll(Calendar.YEAR,false);
      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_LAST_YEAR"))
         registerDynamicCSV("#START_OF_LAST_YEAR#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_LAST_YEAR"))
         registerDynamicCSV("#END_OF_LAST_YEAR#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
         registerDynamicCSV("#LAST_YEAR#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);
      
      // Calculatin Future Time Periods
      calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      calendar.roll(Calendar.DAY_OF_YEAR,true);
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_TOMORROW"))
         registerDynamicCSV("#START_OF_TOMORROW#", new CSVObject(DATE_TYPE,st_time ,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_TOMORROW"))
         registerDynamicCSV("#END_OF_TOMORROW#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("TOMORROW"))
         registerDynamicCSV("#TOMORROW#", new CSVObject(DATE_TYPE, st_time +".."+end_time ,RESOLVED_BY_CLIENT,true), mgr);
      
      calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      calendar.roll(Calendar.WEEK_OF_YEAR,true);
      calendar.set(Calendar.DAY_OF_WEEK,calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.set(Calendar.DAY_OF_WEEK,calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_NEXT_WEEK"))
         registerDynamicCSV("#START_OF_NEXT_WEEK#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_NEXT_WEEK"))
         registerDynamicCSV("#END_OF_NEXT_WEEK#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
         registerDynamicCSV("#NEXT_WEEK#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);
      
      calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      calendar.roll(Calendar.MONTH,true);
      calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_NEXT_MONTH"))
         registerDynamicCSV("#START_OF_NEXT_MONTH#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_NEXT_MONTH"))
         registerDynamicCSV("#END_OF_NEXT_MONTH#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
         registerDynamicCSV("#NEXT_MONTH#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);

      calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      calendar.roll(Calendar.YEAR,true);
      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
      end_time = csv_fmt.format(endOfTime(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_NEXT_YEAR"))
         registerDynamicCSV("#START_OF_NEXT_YEAR#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_NEXT_YEAR"))
         registerDynamicCSV("#END_OF_NEXT_YEAR#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
         registerDynamicCSV("#NEXT_YEAR#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);

      // Added by Jack Zhang,20100712 17:12:00
      calendar = new GregorianCalendar(new Locale(mgr.getLanguageCode()));
      // First season
      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.add(Calendar.MONTH, 2);
      end_time = csv_fmt.format(endOfTime2(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_FIRST_SEASON"))
    	  registerDynamicCSV("#START_OF_FIRST_SEASON#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_FIRST_SEASON"))
    	  registerDynamicCSV("#END_OF_FIRST_SEASON#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
      registerDynamicCSV("#FIRST_SEASON#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);
      
      // Second season
      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
      calendar.add(Calendar.MONTH, 3);
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.add(Calendar.MONTH, 2);
      end_time = csv_fmt.format(endOfTime2(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_SECOND_SEASON"))
    	  registerDynamicCSV("#START_OF_SECOND_SEASON#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_SECOND_SEASON"))
    	  registerDynamicCSV("#END_OF_SECOND_SEASON#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
      registerDynamicCSV("#SECOND_SEASON#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);
      
      // Third season
      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
      calendar.add(Calendar.MONTH, 6);
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.add(Calendar.MONTH, 2);
      end_time = csv_fmt.format(endOfTime2(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_THIRD_SEASON"))
    	  registerDynamicCSV("#START_OF_THIRD_SEASON#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_THIRD_SEASON"))
    	  registerDynamicCSV("#END_OF_THIRD_SEASON#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
      registerDynamicCSV("#THIRD_SEASON#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);
      
      // Fourth season
      calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
      calendar.add(Calendar.MONTH, 9);
      st_time = csv_fmt.format(startOfTime(calendar).getTime());
      calendar.add(Calendar.MONTH, 2);
      end_time = csv_fmt.format(endOfTime2(calendar).getTime());
      
      if(ContextSubstitutionVariableRegistry.isClientCSV("START_OF_FOURTH_SEASON"))
    	  registerDynamicCSV("#START_OF_FOURTH_SEASON#", new CSVObject(DATE_TYPE, st_time,RESOLVED_BY_CLIENT), mgr );
      if(ContextSubstitutionVariableRegistry.isClientCSV("END_OF_FOURTH_SEASON"))
    	  registerDynamicCSV("#END_OF_FOURTH_SEASON#", new CSVObject(DATE_TYPE, end_time ,RESOLVED_BY_CLIENT), mgr );
      registerDynamicCSV("#FOURTH_SEASON#", new CSVObject(DATE_TYPE, st_time + ".." + end_time,RESOLVED_BY_CLIENT,true), mgr);
      // Added end
   }
   
   /**
    * Initialise the Dynamic Server Resolved  CSVs
    * @param mgr ASPManager to init the dynamic server resolved csv's
    */
   private void initDynamicServerResolvedCSV(ASPManager mgr)
   {
      if (DEBUG) debug("Initialize Server Resolved Dynamic CSV!!");   
      if (ContextSubstitutionVariableRegistry.getServerDynamicCSVSet().size()==0) return;
      if (DEBUG) debug("CSV DB CALL::::::Initialize Server Resolved Dynamic CSV!!");   
       
      //server value init here
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      Iterator it = ContextSubstitutionVariableRegistry.getServerDynamicCSVSet().iterator();
      while(it.hasNext()){
         cmd = mgr.newASPCommand();
         cmd.defineCustomFunction("CONTEXT_SUBSTITUTION_VAR_API.Get_String__");
         cmd.addParameter("CSV_VALUE", "S", null, null);
         String csv = it.next().toString();
         cmd.addParameter("NAME", "S", "IN", csv);
         trans.addCommand(csv,cmd);
      }

      trans = mgr.perform(trans);
      
      it = ContextSubstitutionVariableRegistry.getServerDynamicCSVSet().iterator();
      while(it.hasNext()){
         String csv = it.next().toString();
         String value = trans.getBuffer(csv+"/DATA").getValue("CSV_VALUE");
         int data_type = STRING_TYPE;
         try{
            Message msg = new Message(value);
            String dt = "String";
            if(msg.toString().indexOf("VALUE")!=-1)
               value = msg.getStringAttribute("VALUE");
            else
               value = "";
            
            if(msg.toString().indexOf("DATATYPE")!=-1)
               dt = msg.getStringAttribute("DATATYPE");

            if("Date".equals(dt))
               data_type = DATE_TYPE;
            else if("String".equals(dt))
               data_type = STRING_TYPE;
            else if("Number".equals(dt))
               data_type = NUMBER_TYPE;
         }
         catch(Exception e)
         {
            value = "";
            mgr.showError(e.toString());
         }
         
         if(!Str.isEmpty(value))
            registerDynamicCSV(toCSVName(csv), new CSVObject(data_type, value, RESOLVED_BY_SERVER), mgr);
      }
 
   }
   
   /**
    * Method to retrive a value for a given Context Subtitution Variable
    * Returns the value in client format, defaultObj if no csv is found.
    * @param name Name of the CSV
    * @param dev_value Default value to return if no CSV is found
    * @param field ASPField of the CSV
    * @param mgr ASPManager to access the CSV
    * @return String value of CSV
    */
   String getCSVValue(String name, String def_value, ASPField field, ASPManager mgr)
   { 
      if (DEBUG) debug("getiing CSV Value: "+ name); 
      String[] csv_array = new String[0];
      int index = -1;
      boolean isfirst = false;
      name = name.trim();
      for(int i=0; i<search_tips.length; i++){
         int pos = name.indexOf(search_tips[i]);
         if(pos==-1) continue;
         if(pos==0) isfirst = true;
         if(search_tips[i].equals("..") && name.length()>=pos+2)
            csv_array = new String[] {name.substring(0,pos), name.substring(pos+2)};
         else
            csv_array = Pattern.compile(search_tips[i]).split(name);
         index = i;
         break;
      }
      
      if(csv_array.length==0 && isCSVName(name)) return getValue(name, def_value, field, mgr);
      
      String value = "";
      
      for(int i=0; i<csv_array.length; i++){
         if(!mgr.isEmpty(csv_array[i]))
         {
            if(isfirst || i!=0){
               value += search_tips[index];
               isfirst = false;
            }
            csv_array[i] = csv_array[i].trim();
            if(isCSVName(csv_array[i]))
            {
               if( field.getTypeId() == DataFormatter.DATETIME )
                  value += getValue(csv_array[i], def_value, field, mgr, ((i==0)? LOW_END: HIGH_END) );
               else
               value += getValue(csv_array[i], def_value, field, mgr);
            }
            else if(hasCSVNames(csv_array[i]))
               value += getCSVValue(csv_array[i], def_value, field, mgr);
            else
               value += csv_array[i];
         }
      }
      
      if(index>-1 && csv_array.length==1) value += search_tips[index];
         
      return value;
   }
   
   /**
    * Method to retrive a value for a given Context Subtitution Variable
    * Returns the value in client format, defaultObj if no csv is found.
    * @param name Name of the CSV
    * @param dev_value Default value to return if no CSV is found
    * @param field ASPField of the CSV
    * @param mgr ASPManager to access the CSV
    * @return String value of CSV
    */
   private String getValue(String name, String def_value, ASPField field, ASPManager mgr)
   {
      return getValue(name, def_value, field, mgr, PERIOD);
   }

   /**
    * Method to retrive a value for a given Context Subtitution Variable
    * Returns the value in client format, defaultObj if no csv is found.
    * For Date/DateTime values, focus determines which value is returned from the period.
    * @param name Name of the CSV
    * @param dev_value Default value to return if no CSV is found
    * @param field ASPField of the CSV
    * @param mgr ASPManager to access the CSV
    * @param focus LOW_END, HIGH_END or PERIOD
    * @return String value of CSV
    */   
   private String getValue(String name, String def_value, ASPField field, ASPManager mgr, int focus)
   {
      String val = null;
      
      if (static_cache== null || dynamic_cache == null ) // to be more safe
          return name;
      
      CSVObject obj = (CSVObject) static_cache.get(name);
      if (obj==null)
         obj = (CSVObject) dynamic_cache.get(name);
      
      if (obj == null)
         return name; //No CSV value found. No Conversion.
      
      if (DEBUG) debug("CSV value found for " + name + " --------");

      if(field!=null && field.getTypeId()!=DataFormatter.STRING && obj.value!=null)
      {
         if (obj.isQueryOnlyCSV())
            return convertToQueryOnlyCSVValue(name,field,obj.getValue(), focus);
         
         try
         {
            val = field.convertToClientString(obj.getValue());
            if (DEBUG) debug(" Object converted to client format by ASPField " + field.getName() + " to " + val);
            return val;
         }
         catch (FndException e)
         {
            debug("Error occured while converting CSV for " +  field.getName() + " - " + e);
         }
      }
      
      val = (Str.isEmpty(obj.getValue())? def_value: getDefaultValue(name, obj.getValue()));
      if (DEBUG) debug("No CLIENT CONVERSION DONE. Value returned - " + val );
      
      return val;
   }

   private String getDefaultValue(String name, String value)
   {
      int pos = value.indexOf("..");
      String val = null;
      
      if(TODAY.equals(name) || SYSDATE.equals(name) || TOMORROW.equals(name) || YESTERDAY.equals(name))
         val = value.substring(0,pos);
      else
         val = value;
      return val;
   }

   /**
    * Converts a CSV into a Query Only CSV Value and retunrns the value
    * @param name Name of the CSV.
    * @param f ASPField of CSV.
    * @param value value of CSV.
    * @param focus which end of value needed for periods.
    * @return String CSV value converted to Query only CSV Vlaue.
    */
   private String convertToQueryOnlyCSVValue(String name, ASPField f, String value, int focus)
   {
      int pos = value.indexOf("..");
      String val = null;
      String val1 = value.substring(0,pos);
      String val2 = value.substring(pos+2);
      
      try
      {
         
         if ((f.getTypeId() == DateFormatter.DATE) && (TODAY.equals(name) || SYSDATE.equals(name) || TOMORROW.equals(name) || YESTERDAY.equals(name)))
         {
            val = f.convertToClientString(val1);
            if (DEBUG) debug(" Type is DATE: return " + val);
            return val;
         }
         else if ( (f.getTypeId() == DateFormatter.DATETIME) && (TODAY.equals(name) || SYSDATE.equals(name) || TOMORROW.equals(name) || YESTERDAY.equals(name)) )
         {
            if(focus == PERIOD)
               val = f.convertToClientString(val1) + ".." + f.convertToClientString(val2);
            else if(focus == LOW_END)
               val = f.convertToClientString(val1);
            else if(focus == HIGH_END)
               val = f.convertToClientString(val2);

            if (DEBUG) debug(" Type is not DATE (i: DATETIME: return " + val);
            return val;
         }
         else
         {
            val = f.convertToClientString(val1) + ".." + f.convertToClientString(val2);            
            return val;
         }
      }
      catch (FndException e)
      {
         debug("Error while converting CSV:" + value + " to client format");
         return value;
      }
      
   }
   
   /**
    * Replace all CSV names within a query string with the coresponding CSV value
    * @param query Query string with the CSV names
    * @param mgr ASPManager to manager CSV's
    * @return String CSV replaced query string
    */
   String parseCSVQueryString(String query, boolean include_query_only, ASPManager mgr)
   {
      if (DEBUG) debug(this+": parseCSVQueryString(String, boolean, ASPManager)");
      String csv[] = getCSVNames(All_CSV);
      HttpSession session = mgr.getAspSession();
      
      for (int i=0; i<csv.length; i++)
      {
         CSVObject obj = (CSVObject) static_cache.get(toCSVName(csv[i]));
         if (obj==null)
         {
            if (DEBUG) debug(" Object not found in Static Cache.");
            obj = (CSVObject) dynamic_cache.get(toCSVName(csv[i]));
         }

         if (obj != null)
         {
            String value = obj.getValue();
            if (include_query_only && obj.isQueryOnlyCSV())
            {
               if(obj.getType()==DATE_TYPE)
               {
                  int pos = value.indexOf("..");
                  String val1 = value.substring(0,pos);
                  String val2 = value.substring(pos+2);
                  value = "to_date('"+val1+"','"+ORACLE_DATE_FORMAT+"') and to_date('"+val2+"','"+ORACLE_DATE_FORMAT+"')";
               }
               query = Str.replace(query, toCSVName(csv[i]), value);
            }
            if(!obj.isQueryOnlyCSV())
            {
               if(obj.getType()==DATE_TYPE)
               {
                  value = "to_date('"+value+"','"+ORACLE_DATE_FORMAT+"')";
               }
               query = Str.replace(query, toCSVName(csv[i]), value);
            }
         }
      }
      return query;
   }
   
   /**
    * Gives the list of Context Substitution Variable Names
    * that are already in the session
    * @param mgr ASPManager to get CSVs
    * @return String[] Array of CSV names
    */
   String[] getCSVNames(int csv_type)
   {
      if (DEBUG) debug(this+": getCSVNames(int)");
      
      HashSet csvs = null;
      
      if(csv_type==All_CSV)
         csvs = ContextSubstitutionVariableRegistry.getCSVSet();
      else if(csv_type==CLIENT_CSV)
         csvs = ContextSubstitutionVariableRegistry.getClientCSVSet();
      else if(csv_type==SERVER_CSV)
         csvs = ContextSubstitutionVariableRegistry.getServerCSVSet();
      else if(csv_type==STATIC_CSV)
         csvs = ContextSubstitutionVariableRegistry.getStaticCSVSet();
      else if(csv_type==DYNAMIC_CSV)
         csvs = ContextSubstitutionVariableRegistry.getDynamicCSVSet();
      else
         return null;
      
      String[] var_names = new String[csvs.size()];
      
      Iterator it = csvs.iterator();
      int count = 0;
      
      while(it.hasNext()){
         String name = it.next().toString();
         var_names[count++] = name;
      }
      
      return var_names;
   }
   
   /**
    * Check if the name formated in as a 
    * Context Substitution Variable and return TRUE, FALSE if not.
    * @param name Name of CSV
    * @return boolean Either name is CSV or not
    */
   boolean isCSVName(String name)
   {
      if (name == null || name == "") return false;
      name = name.trim();
      if (!name.toUpperCase().equals(name)) return false;
      if (!name.startsWith(VARIABLE_PREFIX)) return false;
      if (!name.endsWith(VARIABLE_PREFIX)) return false;
      return true;
   }
   
   boolean hasCSVNames(String value)
   {
      if (value == null || value == "") return false;
      value = value.trim();
      for(int i=0; i<search_tips.length; i++){
         int pos = value.indexOf(search_tips[i]);
         if(pos == -1) continue;
         String[] arr = null;
         if(search_tips[i]=="..")
            arr = new String[] {value.substring(0,pos), value.substring(pos+2)};
         else
            arr = Pattern.compile(search_tips[i]).split(value);
         for(int x=0; x<arr.length; x++){
            if(arr[x] !=null && arr[x]!="" && isCSVName(arr[x])) return true;
         }
      }
      
      if(isCSVName(value)) return true;
      
      return false;
   }
   
   /**
    * Converts a given variable name into a CSV name
    * and returns it.
    * @return String
    */
   String toCSVName(String name)
   {
      return (!isCSVName(name))? VARIABLE_PREFIX+name.trim()+VARIABLE_SUFFIX: name;
   }
   
   /**
    * For Debugging puposes
    * Prints the contents of a given HashMap object onto the debug terminal
    * @param variable_map HashMap to be debuged
    */
   private void debugContextSubVars(HashMap variable_map)
   {
      if(variable_map==null || variable_map.isEmpty()){
         debug(this+": Empty");
         return;
      }
      Iterator it = variable_map.keySet().iterator();
      while(it.hasNext()){
         String key = it.next().toString();
         Object obj = variable_map.get(key);
         if (obj instanceof CSVObject){
            CSVObject csvo = (CSVObject) obj;
            debug(key + "   Type:  "+csvo.type + "   Value: "+csvo.value);
         }else
            debug(obj.toString());
      }
      debug("Size is KB" + getTotalSize(variable_map)/1024);
   }
   
   //===============================================================
   // Private Utility Methods
   //===============================================================

   /**
    * Prints debug string
    * @param text String to be printed
    */
   private void debug(String text )
   {
      Util.debug(text);
   }
   
   /**
    * set calendar objects time to start of clock
    * @param calendar Calendar object to set clock
    * @return Calendar
    */
   private Calendar startOfTime(Calendar calendar)
   {
      if(calendar==null) return calendar;
      calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
      calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
      calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
      return calendar;
   }
   
   /**
    * set calendar objects time to end of clock
    * @param calendar Calendar object to set clock
    * @return Calendar
    */
   private Calendar endOfTime(Calendar calendar)
   {
      if(calendar==null) return calendar;
      calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
      calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
      calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
      return calendar;
   }
   
   // Added by Jack Zhang,20100712 17:12:00
   private Calendar endOfTime2(Calendar calendar)
   {
      if(calendar==null) return calendar;
      calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      return endOfTime(calendar);
   }
   // Added end
   
   long getTotalSize(Object obj)
   {
      try
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         ObjectOutputStream    out  = new ObjectOutputStream(bout);

         out.writeObject(obj);
         out.close();

         byte[] bytes = bout.toByteArray();

         return bytes.length;
      }
      catch( IOException x )
      {
         String msg = "Exception while debugging profile cache:\n"+Str.getStackTrace(x);
         debug(msg);
         Alert.add(msg);
         return -1;
      }
   }   

   //===============================================================
   // Inner class to represent CSV Object
   //===============================================================
   class CSVObject implements java.io.Serializable {
      String value;
      int type;
      int resolved_by;
      boolean query_only;
      
      /**
       * Construct a CSVObject with given type, value, resolved_by values and query_only value as false
       * @param type Type of CSVObject
       * @param value Value of CSVObject
       * @param resolved_by If CSVObject is resolved by either Client or Server
       */
      CSVObject(  int type, String value, int resolved_by)
      {
         this.value = value;
         this.type =  type;
         this.resolved_by = resolved_by;
         query_only = false;
      }

      /**
       * Construct a CSVObject with given type, value, resolved_by, query_only values
       * @param type Type of CSVObject
       * @param value Value of CSVObject
       * @param resolved_by If CSVObject is resolved by either Client or Server
       * @param query_only Whether CSVObject is query only or not
       */
      CSVObject( int type, String value, int resolved_by, boolean query_only)
      {
         this.value = value;
         this.type = type;
         this.resolved_by = resolved_by;
         this.query_only = query_only;
      }
      
      /**
       * @return CSVObject's value
       */
      String getValue()
      {
         return value;
      }
      
      /**
       * @return CSVObject's query only status
       */
      boolean isQueryOnlyCSV()
      {
         return query_only;
      }
      
      /**
       * @return CSVObject's type
       */
      int getType()
      {
         return type;
      }
      
      /**
       * @return CSVObject's resolve type
       */
      int resolveBy()
      {
         return resolved_by;
      }
      
      public String toString()
      {
         return " { " + type + "," + resolved_by + "," + value + "," + query_only + "}";
      }
              
   }
   
}
