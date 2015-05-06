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
 *  File        : EdmMacroProcess.java
 *  Description :
 *
 *  History
 *
 *  Date        Sign    Descripiton
 *  ----        ----    -----------
 *
 *  2003-01-29  MDAHSE  Added debug method and constant.
 *  2003-03-04  DIKALK  Removed method hasActionMacroProcess and getActionMacroProcess
 *  2003-03-04  DIKALK  Added public static method getEdmMacroProcess
 *  2003-03-04  DIKALK  Added method to get the macro description of the First macro in the buffer
 *  2007-08-09  AMNILK  Eliminated SQL Injection Security Vulnerability.
 *  2008-05-12  SHTHLK  Bug Id 67436, Modified EdmMacroProcess(), selectAndRunMacro(), runIfOnlyOneMacro() and runNoMacro()
 */


package ifs.docmaw.edm;

import ifs.fnd.asp.*;
import ifs.fnd.service.Util;
import ifs.fnd.util.Str;
import ifs.fnd.service.FndException;
import java.util.StringTokenizer;

/**
 * An instance of this class represents the set of macros that can be executed
 * for a given document.
 *
 */

public class EdmMacroProcess
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.edm.EdmMacroProcess");

   private final static String[] macro_processes = getSupportedMacroProcesses();


   private ASPManager mgr;
   private ASPLog log;
   private ASPBuffer macro_buf;
   private int macro_option;
   private String macro_launch;//Bug Id 67436

   public EdmMacroProcess(ASPManager mgr, String doc_class, String process,
                          String file_type)
   {
      this.mgr = mgr;
      log = mgr.getASPLog();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      //Bug Id 67436, Start
      StringBuffer query = new StringBuffer("SELECT Doc_Class_Proc_Action_Head_Api.Launch_Macro('");
      query.append(doc_class);
      query.append("','','','','','");
      query.append(process);
      query.append("','");
      query.append(file_type);
      query.append("') LAUNCH_OPTION FROM DUAL");
      trans.addQuery("LAUNCH_OPTION", query.toString());
      //Bug Id 67436, End

      query = new StringBuffer("SELECT Doc_Class_Edm_Macro_Api.Get_Macros('");
      query.append(doc_class);
      query.append("','");
      query.append(process);
      query.append("','");
      query.append(file_type);
      query.append("') MACRO_LIST FROM DUAL");
      trans.addQuery("MACRO_LIST", query.toString());		//SQLInjections_Safe AMNILK 20070810 (In this macro no ASP fields are used. So no way to come up with a vulnerable code)

      trans = mgr.perform(trans);
      //Bug Id 67436, Start
      macro_launch = trans.getValue("LAUNCH_OPTION/DATA/LAUNCH_OPTION");
      if (macro_launch.indexOf("^") > 0) 
	  macro_launch = macro_launch.substring(0,macro_launch.indexOf("^"));

      String macro_list = null;
      if (!("NoMacro".equals(macro_launch))) 
        macro_list = trans.getValue("MACRO_LIST/DATA/MACRO_LIST");
      //Bug Id 67436, End

      if (!Str.isEmpty(macro_list))
      {
         StringTokenizer list = new StringTokenizer(macro_list, String.valueOf((char)30));
         StringTokenizer items;
         String description;

         ASPBuffer data_buf;
         macro_buf = mgr.newASPBuffer();

         while (list.hasMoreTokens())
         {
            items = new StringTokenizer(list.nextToken(), String.valueOf((char)31));

            data_buf = macro_buf.addBuffer("DATA");
            data_buf.addItem("VALUE",  items.nextToken() + "^" + items.nextToken());

            description = items.nextToken();
            data_buf.addItem("DESCRIPTION", description.substring(description.indexOf("=") + 1));
         }
      }
   }


   private static String[] getSupportedMacroProcesses()
   {
      String[] processes = new String[8];

      processes[0] = "CHECKIN";
      processes[1] = "CHECKOUT";
      processes[2] = "DELETE";
      processes[3] = "PRINT";
      processes[4] = "VIEW";
      processes[5] = "CREATENEW";
      processes[6] = "UNDOCHECKOUT";
      processes[7] = "OTHER"; // unused today!

      return processes;
   }


   public static String[] getEdmMacroProcesses()
   {
      return macro_processes;
   }


   public boolean selectAndRunMacro()
   {
      //Bug Id 67436, Start
      if (("ShowDialogAdvanced".equals(macro_launch)) || ("ShowDialogStandard".equals(macro_launch)))
	  return true;
      else
	  return false;
      //Bug Id 67436, End
   }


   public boolean runIfOnlyOneMacro()
   {
      //Bug Id 67436, Start
      if (("RunMacro".equals(macro_launch)) || ("RunMacroStandard".equals(macro_launch)))
	  return true;
      else
	  return false;
      //Bug Id 67436, End
   }


   public boolean runNoMacro()
   {
      //Bug Id 67436, Start
      if ("NoMacro".equals(macro_launch))
	  return true;
      else
	  return false;
      //Bug Id 67436, End
   }


   public String getFirstMacro()
   {
      return macro_buf.getBufferAt(0).getValue("VALUE");
   }


   public String getFirstMacroDescription()
   {
      return macro_buf.getBufferAt(0).getValue("DESCRIPTION");
   }


   public ASPBuffer getMacros()
   {
      return macro_buf;
   }


   public static String getEmptyMacro()
   {
      return "ACTION=^MAIN_FUNCTION=";
   }


   /**
    * Prints out a debug message
    */
   private void debug(String debug_message)
   {
      StringBuffer msg;
      if (DEBUG)
      {
         msg = new StringBuffer(getClass().getName());
         msg.append(": ");
         msg.append(debug_message);
         log.debug(msg.toString());
      }
   }
}
