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
*  File        : GeneralConfigurations.java 
*  Modified    :
*    Suneth M   2003-07-28 - Created.
*    Chandana D 2003-09-04 - Improved GUI.
*    Chandana D 2004-05-12 - Updated for the use of new style sheets.
* ----------------------------------------------------------------------------
* New Comments:
* 2010/02/10 sumelk Bug 88916, Changed startup() & getContents() to fetch the printer list from the database by executing a query.
* 2008/09/02 buhilk Bug 76759, Modified setTheme() & getThemeName() to read theme settigs from new common location
* 2008/08/14 buhilk Bug 76337, Modified preDefine() & getContents() to change RWC to Enterprise Explorer
* 2008/08/12 buhilk Bug 76287, Modified preDefine() to show/hide RWC integration depending on the Aurora global switch
* 2008/07/15 dusdlk Bug 75633, Changed applyRegionalSettings(boolean reset) added validation to check decimal and group seperator.
* 2008/07/02 sumelk Bug 74487, Changed printerSettings().
* 2008/04/28 sumelk Bug 73344, Added instructions for 24 hour time format.
* 2008/04/04 buhilk bug 72854, Added RWC intefration settings tab.
* 2007/07/26 sadhlk Merged bug 66808, Added validateSupportedDateTime() and modified validateData(),validate()
* 2007/04/11 buhilk bug 64422, Fixed gui errors in getContents().
* 2007/02/07 rahelk bug 63366, Added more details to instructions
* 2007/02/06 buhilk Bug 63250, Improved code for themes.
* 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
*
*               2006/07/21          gegulk
* Bug 59203; Added a note to the Printer Settings Tab, in the getContents().
*
* 2006/02/14 MAPELK Added support for masks with no separators
*
*               2006/01/25           mapelk
* Removed errorneous translation keys
*                2006/01/10           mapelk
* Added Rest facility and instructions to the page. 
*
*                2006/01/09           mapelk
* Improved regional settings support in the profile
*
*                2005/12/12          mapelk
* 129732 - Spelling corrections 
*                2005/12/01          mapelk
* Bug fix in prfile formats 
* Revision 1.5  2005/11/02 12:22:33  mapelk
* Saving Regional Settings to profile even the Regional Setting profile is missing.
*
* Revision 1.4  2005/11/02 04:55:12  mapelk
* Saving Regional Setting changes to the profile.
*
* Revision 1.3  2005/10/25 11:06:14  mapelk
* Introduced different validations for Number & Money. Also replaces ASCII 160 with 32 which returns as group seperator for some languages.
*
* Revision 1.2  2005/10/14 09:08:16  mapelk
* Added common profile elements. And removed language specific formats and read from locale.
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.3  2005/04/08 06:05:37  riralk
* Merged changes made to PKG12 back to HEAD
*
* Revision 1.2  2005/02/02 08:22:19  riralk
* Adapted global profile functionality to new profile changes
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.record.FndBoolean;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GeneralConfigurations extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.GeneralConfigurations");
   
   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   
   private ASPForm frm;
   private ASPContext ctx;
   private ASPBlock blk_print, blk_rs;
   private ASPBlockLayout lay_rs;
   private ASPRowSet rowset_rs;
   private ASPInfoServices inf;
   private ASPConfig cfg;
   private ASPCommandBar bar;
   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPQuery q;
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private String language;
   private String default_printer;
   private String default_theme;
   private String default_language;
   private boolean show_links;
   private ASPBuffer all_printers;

   //===============================================================
   // Construction 
   //===============================================================
   public GeneralConfigurations(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }
   
   public void run() 
   {
      ASPManager mgr = getASPManager();

      mgr.setPageNonExpiring();
      frm   = mgr.getASPForm();
      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      cfg = getASPConfig();
      if (DEBUG) debug("Call run..");
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();
      else if ( mgr.buttonPressed("PS_APPLY") ) //Printer Settings
         set_default_printer();
      else if (mgr.buttonPressed("RS_APPLY")) //Regional Settings
         applyRegionalSettings(false);
      else if (mgr.buttonPressed("RS_RESET")) //Regional Settings - Reset
         resetRegionalSettings();
      else if (mgr.buttonPressed("THEMEAPPLY")) //Theme Apply
         setTheme();
      else if(mgr.buttonPressed("RWC_APPLY")) //Apply RWC integration settings
         setRWCIntegration();
      else
      {
         tabs.setActiveTab(1);
         readFormats();
      }
      startup();
      tabs.saveActiveTab();
   }

//=============================================================================
//  Utility functions
//=============================================================================

   public void  startup()
   {
      ASPManager mgr = getASPManager();

      default_printer  = readGlobalProfileValue("Defaults/Printer"+ProfileUtils.ENTRY_SEP+"Default",false);
      default_language = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
      default_theme    = mgr.getUserThemeID()+"";
      show_links =Boolean.parseBoolean(readGlobalProfileValue("Defaults/RWCIntegration"+ProfileUtils.ENTRY_SEP+"ShowLinks","FALSE",false));
      
      trans.clear();
      // Used a query since enumerate method returns an error with large number of printers
      trans.addQuery("PRINTER_LIST", "SELECT printer_id,Logical_Printer_API.Get_Description(printer_id)||',SERVER,'|| printer_id FROM LOGICAL_PRINTER").setBufferSize(2000);
      trans = mgr.perform(trans);

      all_printers = trans.getBuffer("PRINTER_LIST");

      if (mgr.isEmpty(default_language)) 
         language = mgr.getLanguageCode();
      else
         language = default_language;
      
            
      if (DEBUG)
      {
          debug(" Default Printer: " + default_printer);
          debug(" Default Language: " + default_language);
          debug(" Default Theme: " + default_theme);
      }
   }
   
   public void readFormats()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf = mgr.newASPBuffer();
      NumberFormatter formatter;
      
      char ch_group_sep = mgr.getGroupSeparator(DataFormatter.NUMBER);
      String group_sep = ch_group_sep==' '?"SPACE":ch_group_sep+"";

      buf.addItem("DECIMAL_DIGITS_NUMBER", mgr.getDecimalDigitsSize(DataFormatter.NUMBER)+""); 
      buf.addItem("DECIMAL_SEPARATOR_NUMBER", mgr.getDecimalSeparator(DataFormatter.NUMBER)+"");
      buf.addItem("GROUP_SEPARATOR_NUMBER", group_sep);
      buf.addItem("GROUP_SIZES_NUMBER", mgr.getGroupingSizes(DataFormatter.NUMBER));
      
      try
      {
         formatter = new NumberFormatter(mgr.getLanguageCode(), DataFormatter.NUMBER, mgr.getNumericFormatMask(mgr.getGroupingSizes(DataFormatter.NUMBER),mgr.getDecimalDigitsSize(DataFormatter.NUMBER),DataFormatter.NUMBER));
         formatter.setDecimalSeparator(mgr.getDecimalSeparator(DataFormatter.NUMBER));
         formatter.setGroupingSeparator(ch_group_sep);
         buf.addItem("NUMBER_VALIDATE",formatter.format(new Double(123456789.123456)));      
      }
      catch(Exception e)
      {
         buf.addItem("NUMBER_VALIDATE", mgr.translate("FNDGENERALCONFIGERRORVALIDATENUMBER: Error while validating number."));
      }
      
      
      ch_group_sep = mgr.getGroupSeparator(DataFormatter.MONEY);
      group_sep = ch_group_sep==' '?"SPACE":ch_group_sep+"";
      
      buf.addItem("DECIMAL_DIGITS_CURRENCY", mgr.getDecimalDigitsSize(DataFormatter.MONEY)+"");
      buf.addItem("GROUP_SEPARATOR_CURRENCY", group_sep);
      buf.addItem("DECIMAL_SEPARATOR_CURRENCY", mgr.getDecimalSeparator(DataFormatter.MONEY)+""); 
      buf.addItem("GROUP_SIZES_CURRENCY", mgr.getGroupingSizes(DataFormatter.MONEY));

      try
      {
         formatter = new NumberFormatter(mgr.getLanguageCode(), DataFormatter.MONEY, mgr.getNumericFormatMask(mgr.getGroupingSizes(DataFormatter.MONEY),mgr.getDecimalDigitsSize(DataFormatter.MONEY),DataFormatter.MONEY));
         formatter.setDecimalSeparator(mgr.getDecimalSeparator(DataFormatter.MONEY));
         formatter.setGroupingSeparator(ch_group_sep);
         buf.addItem("CURRENCY_VALIDATE",formatter.format(new Double(123456789.123456)));         
      }
      catch(Exception e)
      {
         buf.addItem("NUMBER_VALIDATE", mgr.translate("FNDGENERALCONFIGERRORVALIDATENUMBER: Error while validating number."));
      }
      
      buf.addItem("DATE_PATTERN", mgr.getFormatMask("Date"));
      buf.addItem("TIME_PATTERN", mgr.getFormatMask("Time"));
      SimpleDateFormat date = new SimpleDateFormat(mgr.getFormatMask("Date") + " " + mgr.getFormatMask("Time"));
      buf.addItem("DATE_VALIDATE",date.format(new Date())); 
      rowset_rs.clear(); 
      rowset_rs.addRow(buf);
   }
   
   public void validate()
   {
      
      ASPManager mgr = getASPManager();
      String val;

      val = mgr.readValue("VALIDATE"); 
      if (  "DATE_PATTERN".equals(val) || "TIME_PATTERN".equals(val)) 
      {
         try
         {  
            String date_pattern =mgr.readValue("DATE_PATTERN");
            String time_pattern =mgr.readValue("TIME_PATTERN");
            if(validateSupportedDateTime(date_pattern) && validateSupportedDateTime(time_pattern)){
            
               SimpleDateFormat date = new SimpleDateFormat(date_pattern + " " + time_pattern);
               mgr.responseWrite( date.format(new Date()) + "^" );
            }
            else
               mgr.responseWrite( "No_Data_Found" +
                            mgr.translate("FNDGENERALCONFIGILLEAGLEDATEPATTERN: Illegal Date/Time mask.") );
         }
         catch(Exception e)
         {
            mgr.responseWrite( "No_Data_Found" +
                            mgr.translate("FNDGENERALCONFIGILLEAGLEDATEPATTERN: Illegal Date/Time mask.") );
         }
      }
      else if (  "DECIMAL_DIGITS_CURRENCY".equals(val) || "DECIMAL_SEPARATOR_CURRENCY".equals(val) || "GROUP_SEPARATOR_CURRENCY".equals(val) || "GROUP_SIZES_CURRENCY".equals(val)) 
      {
         try
         {
            int decimal_size = Integer.parseInt(mgr.readValue("DECIMAL_DIGITS_CURRENCY"));
            String group_size = mgr.readValue("GROUP_SIZES_CURRENCY");
            char decimal_sep = mgr.readValue("DECIMAL_SEPARATOR_CURRENCY").charAt(0);
            
            String group_sep_value = mgr.readValue("GROUP_SEPARATOR_CURRENCY");
            
            char group_sep = "SPACE".equals(group_sep_value)?' ':mgr.readValue("GROUP_SEPARATOR_CURRENCY").charAt(0);
            
            NumberFormatter formatter = new NumberFormatter(mgr.getLanguageCode(), DataFormatter.MONEY, mgr.getNumericFormatMask(group_size,decimal_size,DataFormatter.MONEY));
            formatter.setDecimalSeparator(decimal_sep);
            formatter.setGroupingSeparator(group_sep);
            mgr.responseWrite( formatter.format(new Double(123456789.123456))); 
         }
         catch(Exception e)
         {
            mgr.responseWrite( "No_Data_Found" +
                            mgr.translate("FNDGENERALCONFIGILLEAGLENUMBERPATTERN: Illegal values for Number pattern.") );
         }
      }
      else if (  "DECIMAL_DIGITS_NUMBER".equals(val) || "DECIMAL_SEPARATOR_NUMBER".equals(val) || "GROUP_SEPARATOR_NUMBER".equals(val) || "GROUP_SIZES_NUMBER".equals(val)) 
      {
         try
         {
            int decimal_size = Integer.parseInt(mgr.readValue("DECIMAL_DIGITS_NUMBER"));
            String group_size = mgr.readValue("GROUP_SIZES_NUMBER");
            char decimal_sep = mgr.readValue("DECIMAL_SEPARATOR_NUMBER").charAt(0);
            
            String group_sep_value = mgr.readValue("GROUP_SEPARATOR_NUMBER");
            
            char group_sep = "SPACE".equals(group_sep_value)?' ':mgr.readValue("GROUP_SEPARATOR_NUMBER").charAt(0);
            
            NumberFormatter formatter = new NumberFormatter(mgr.getLanguageCode(), DataFormatter.NUMBER, mgr.getNumericFormatMask(group_size,decimal_size,DataFormatter.NUMBER));
            formatter.setDecimalSeparator(decimal_sep);
            formatter.setGroupingSeparator(group_sep);
            mgr.responseWrite( formatter.format(new Double(123456789.123456))); 
         }
         catch(Exception e)
         {
            mgr.responseWrite( "No_Data_Found" +
                            mgr.translate("FNDGENERALCONFIGILLEAGLENUMBERPATTERN: Illegal values for Number pattern.") );
         }
      }
      mgr.endResponse();
   }
   
   private boolean validateData()
   {
      ASPManager mgr = getASPManager();
      String date_pattern = rowset_rs.getValue("DATE_PATTERN");
      String time_pattern = rowset_rs.getValue("TIME_PATTERN");
      
      try
      { 
         if(validateSupportedDateTime(date_pattern) && validateSupportedDateTime(time_pattern)){
            SimpleDateFormat date = new SimpleDateFormat(date_pattern + " " + time_pattern);
            date.format(new Date()); 
         }
         else{
            mgr.showAlert("FNDGENERALCONFIGINVALIDDATE: Invalid date/time mask.");
            return false;
         }
      }
      catch(Exception e)
      {
         mgr.showAlert("FNDGENERALCONFIGINVALIDDATE: Invalid date/time mask.");
         return false;
      }
      return true;
   }

   

//=============================================================================
//  Button functions
//=============================================================================

   public void  set_default_printer()
   {
       ASPManager mgr = getASPManager();

       writeGlobalProfileValue("Defaults/Printer"+ProfileUtils.ENTRY_SEP+"Default", mgr.readValue("PRINTER_LIST"), false );
       writeGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default", mgr.readValue("LANGUAGE"), false );
   }

   private void setTheme()
   {
      ASPManager mgr = getASPManager();
      if (DEBUG)
          debug("Seelected Theam Id: " + mgr.readValue("THEMES"));
      int new_theme = Integer.parseInt(mgr.readValue("THEMES","4"));
      String _theme = getThemeName(new_theme,null);
      writeGlobalProfileValue(ProfileUtils.SELECTED_THEME, _theme, false );      
      tabs.setActiveTab(3);
   }

   private void setRWCIntegration()
   {
      ASPManager mgr = getASPManager();
      if (DEBUG)
      {
         debug("Show Enterprise Explorer links in standard browsers: " + mgr.readValue("SHWRWCLINKSINBRW"));
         debug("Show Enterprise Explorer navigator items: " + mgr.readValue("SHWRWCNAVITEMS"));
      }
      writeGlobalProfileValue("Defaults/RWCIntegration"+ProfileUtils.ENTRY_SEP+"ShowLinks", mgr.readValue("SHWRWCLINKSINBRW","FALSE"), false );
      writeGlobalProfileValue("Defaults/RWCIntegration"+ProfileUtils.ENTRY_SEP+"ShowNavigatorItems", mgr.readValue("SHWRWCNAVITEMS","FALSE"), false );
      tabs.setActiveTab(4);
   }
   
   private void resetRegionalSettings()
   {
      rowset_rs.clear();
      applyRegionalSettings(true);
      readFormats();
   }
   
   private void applyRegionalSettings(boolean reset)
   {
      ASPManager mgr = getASPManager();
      String str_dec_digit,ngsep,ndsep,cgsep,cdsep;
      
      if (!reset)
      {
         rowset_rs.store();
         if (!validateData())
            return;
      }
      
      //Get the seperators
      ngsep = reset?null:rowset_rs.getValue("GROUP_SEPARATOR_NUMBER"); 
      ndsep = reset?null:rowset_rs.getValue("DECIMAL_SEPARATOR_NUMBER");
      cgsep = reset?null:rowset_rs.getValue("GROUP_SEPARATOR_CURRENCY"); 
      cdsep = reset?null:rowset_rs.getValue("DECIMAL_SEPARATOR_CURRENCY");
      
      if ((cgsep != null) && (cgsep.equals(cdsep))){
         mgr.showAlert("FNDCURRSEPERATORINVALIDDATE: Decimal and Group Seperators for Currency must be unique.");
         return;
      }
      
     if ((ngsep != null) && (ngsep.equals(ndsep))){
         mgr.showAlert("FNDNUMMSEPERATORINVALIDDATE: Decimal and Group Seperators for Number must be unique.");
         return;
     } 
      
      ASPBuffer buf = readGlobalProfileBuffer("Regional Settings", false);
      if (buf==null)
         buf = getASPManager().newASPBuffer();
      
      ASPBuffer buff_num = buf.getBuffer("NumberFormats^Numeric");
      if (buff_num==null)
         buff_num = buf.addBuffer("NumberFormats^Numeric");

      buff_num.setValue("GROUP_SIZES",reset?null:rowset_rs.getValue("GROUP_SIZES_NUMBER"));
      buff_num.setValue("DECIMAL_SEPARATOR",ndsep);
      buff_num.setValue("GROUP_SEPARATOR","SPACE".equalsIgnoreCase(ngsep)?" ":ngsep);
      str_dec_digit = reset?null:rowset_rs.getValue("DECIMAL_DIGITS_NUMBER");
      str_dec_digit = (str_dec_digit==null)?"2":str_dec_digit;
      buff_num.setIntegerItem("DECIMAL_DIGITS", Integer.parseInt(str_dec_digit));

      ASPBuffer buff_curr = buf.getBuffer("NumberFormats^Currency");
      if (buff_curr==null)
         buff_curr = buf.addBuffer("NumberFormats^Currency");

      buff_curr.setValue("GROUP_SIZES",reset?null:rowset_rs.getValue("GROUP_SIZES_CURRENCY"));
      buff_curr.setValue("DECIMAL_SEPARATOR",cdsep);
      buff_curr.setValue("GROUP_SEPARATOR","SPACE".equalsIgnoreCase(cgsep)?" ":cgsep);
      str_dec_digit = reset?null:rowset_rs.getValue("DECIMAL_DIGITS_CURRENCY");
      str_dec_digit = (str_dec_digit==null)?"2":str_dec_digit;
      buff_curr.setIntegerItem("DECIMAL_DIGITS", Integer.parseInt(str_dec_digit));
      
      String date_mask=null,time_mask=null;
      char date_separator=' ',time_separator=' ';
      String str_date_separator = null, str_time_separator=null;
      if(!reset)
      {
         date_mask = rowset_rs.getValue("DATE_PATTERN").trim();
         time_mask = rowset_rs.getValue("TIME_PATTERN").replaceAll("a","tt").trim();

         date_separator = '/';
         int i = 0;
         int len = date_mask.length();
         while(i<len && Character.isLetter(date_mask.charAt(i)))
            i++;
         if (i<len)
         {
            date_separator = date_mask.charAt(i);
            date_mask = date_mask.replace(date_separator, '/');
            str_date_separator = date_separator+"";
         }
         else
            str_date_separator = null;

         i = 0;
         String temp_time_mask = time_mask;
         if (temp_time_mask.indexOf("tt")>0)
            temp_time_mask = temp_time_mask.substring(0,temp_time_mask.indexOf("tt")).trim();
         len = temp_time_mask.length();
         time_separator = ':';
         while(i<len && Character.isLetter(temp_time_mask.charAt(i)))
            i++;
         if (i<len)
         {
            time_separator = temp_time_mask.charAt(i);
            time_mask = time_mask.replace(time_separator, ':');
            str_time_separator = time_separator+"";
         }
         else
            str_time_separator = null;
         
      }
      
      ASPBuffer buff_date = buf.getBuffer("DateTimeFormats^DateTime");
      if (buff_date == null)
         buff_date = buf.addBuffer("DateTimeFormats^DateTime");
      buff_date.setValue("SHORT_DATE_PATTERN",reset?null:date_mask);
      buff_date.setValue("DATE_SEPARATOR",reset?null:str_date_separator);
      buff_date.setValue("SHORT_TIME_PATTERN",reset?null:time_mask);
      buff_date.setValue("TIME_SEPARATOR",reset?null:str_time_separator);
      
      writeGlobalProfileBuffer("Regional Settings", buf,false);
   }
//=============================================================================
//  Definition
//=============================================================================

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      // Head Block

      blk_print = mgr.newASPBlock("PRNT");
      blk_print.addField("CLIENT_VALUES0");
      blk_print.addField("DEFAULT_PRINTER"); 
      blk_print.addField("PROFILE"); 
      blk_print.addField("URL"); 
      
      blk_rs = mgr.newASPBlock("REGIONALSETTINGS");
      
      blk_rs.addField("DECIMAL_DIGITS_NUMBER","Number")
            .setLabel("FNDGENERALCONFIGDECIMALDIGIT: Decimal Digits")
            .setMandatory()
            .setCustomValidation("DECIMAL_DIGITS_NUMBER,DECIMAL_SEPARATOR_NUMBER,GROUP_SEPARATOR_NUMBER,GROUP_SIZES_NUMBER","NUMBER_VALIDATE");

      blk_rs.addField("DECIMAL_SEPARATOR_NUMBER")
            .setLabel("FNDGENERALCONFIGDECIMALSEP: Decimal Separator")
            .setMandatory()
            .setCustomValidation("DECIMAL_DIGITS_NUMBER,DECIMAL_SEPARATOR_NUMBER,GROUP_SEPARATOR_NUMBER,GROUP_SIZES_NUMBER","NUMBER_VALIDATE");

      
      blk_rs.addField("GROUP_SEPARATOR_NUMBER")
            .setLabel("FNDGENERALCONFIGGROUPSEP: Group Separator")
            .setMandatory()
            .setCustomValidation("DECIMAL_DIGITS_NUMBER,DECIMAL_SEPARATOR_NUMBER,GROUP_SEPARATOR_NUMBER,GROUP_SIZES_NUMBER","NUMBER_VALIDATE");
      
      blk_rs.addField("GROUP_SIZES_NUMBER")
            .setLabel("FNDGENERALCONFIGGROUPSIZE: Group Size")
            .setMandatory()
            .setCustomValidation("DECIMAL_DIGITS_NUMBER,DECIMAL_SEPARATOR_NUMBER,GROUP_SEPARATOR_NUMBER,GROUP_SIZES_NUMBER","NUMBER_VALIDATE");

      blk_rs.addField("NUMBER_VALIDATE")
            .setLabel("FNDGENERALCONFIGNUMBERVALIDATE: Preview")
            .setReadOnly();

      blk_rs.addField("DECIMAL_DIGITS_CURRENCY","Number")
            .setLabel("FNDGENERALCONFIGDECIMALDIGIT: Decimal Digits")
            .setMandatory()
            .setCustomValidation("DECIMAL_DIGITS_CURRENCY,DECIMAL_SEPARATOR_CURRENCY,GROUP_SEPARATOR_CURRENCY,GROUP_SIZES_CURRENCY","CURRENCY_VALIDATE");            

      blk_rs.addField("DECIMAL_SEPARATOR_CURRENCY")
            .setMandatory()
            .setLabel("FNDGENERALCONFIGDECIMALSEP: Decimal Separator")
            .setCustomValidation("DECIMAL_DIGITS_CURRENCY,DECIMAL_SEPARATOR_CURRENCY,GROUP_SEPARATOR_CURRENCY,GROUP_SIZES_CURRENCY","CURRENCY_VALIDATE");            
            
      
      blk_rs.addField("GROUP_SEPARATOR_CURRENCY")
            .setLabel("FNDGENERALCONFIGGROUPSEP: Group Separator")
            .setMandatory()
            .setCustomValidation("DECIMAL_DIGITS_CURRENCY,DECIMAL_SEPARATOR_CURRENCY,GROUP_SEPARATOR_CURRENCY,GROUP_SIZES_CURRENCY","CURRENCY_VALIDATE");            
      
      blk_rs.addField("GROUP_SIZES_CURRENCY")
            .setLabel("FNDGENERALCONFIGGROUPSIZE: Group Size")
            .setMandatory()
            .setCustomValidation("DECIMAL_DIGITS_CURRENCY,DECIMAL_SEPARATOR_CURRENCY,GROUP_SEPARATOR_CURRENCY,GROUP_SIZES_CURRENCY","CURRENCY_VALIDATE");            
      
      blk_rs.addField("CURRENCY_VALIDATE")
            .setLabel("FNDGENERALCONFIGNUMBERVALIDATE: Preview")
            .setReadOnly();
      
      blk_rs.addField("DATE_PATTERN")
            .setLabel("FNDGENERALCONFIGDATEPATTERN: Date Pattern")
            .setMandatory()
            .setCustomValidation("DATE_PATTERN,TIME_PATTERN","DATE_VALIDATE");
            

      blk_rs.addField("TIME_PATTERN")
            .setLabel("FNDGENERALCONFIGTIMEPATTERN: Time Pattern")
            .setMandatory()
            .setCustomValidation("DATE_PATTERN,TIME_PATTERN","DATE_VALIDATE");

      blk_rs.addField("DATE_VALIDATE")
            .setLabel("FNDGENERALCONFIGNUMBERVALIDATE: Preview")
            .setReadOnly();      
      
      
      bar = blk_rs.newASPCommandBar();
      
      bar.addCustomCommand("printerSettings","Printer Settings");
      bar.addCustomCommand("regionalSettings","Regional Settings");
      bar.addCustomCommand("themeSettings","Themes");
      
      bar.removeCustomCommand("printerSettings");
      bar.removeCustomCommand("regionalSettings");
      bar.removeCustomCommand("themeSettings");

      lay_rs = blk_rs.getASPBlockLayout();
      lay_rs.setDefaultLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
      lay_rs.showBottomLine(false);
      
      lay_rs.defineGroup("FNDGENCONFIGCURRENCY: Currency","DECIMAL_SEPARATOR_CURRENCY,GROUP_SEPARATOR_CURRENCY,DECIMAL_DIGITS_CURRENCY,GROUP_SIZES_CURRENCY,CURRENCY_VALIDATE",true,true,1);
      lay_rs.defineGroup("FNDGENCONFIGNUMBER: Number","DECIMAL_SEPARATOR_NUMBER,GROUP_SEPARATOR_NUMBER,DECIMAL_DIGITS_NUMBER,GROUP_SIZES_NUMBER,NUMBER_VALIDATE",true,true,1);
      lay_rs.defineGroup("FNDGENCONFIGDATE: Date","DATE_PATTERN,TIME_PATTERN,DATE_VALIDATE",true,true,1);

      rowset_rs = blk_rs.getASPRowSet();
      inf = mgr.newASPInfoServices();
      inf.addFields();
      
      tabs = newASPTabContainer("MAIN_TAB");
      tabs.addTab("FNDGENCONFIGREGIONALTAB: Regional Settings","javascript: commandSet('REGIONALSETTINGS.regionalSettings','')");
      tabs.addTab("FNDGENCONFIGPRINTERTAB: Printer Settings","javascript: commandSet('REGIONALSETTINGS.printerSettings','')");
      tabs.addTab("FNDGENCONFIGTHEMES: Themes","javascript: commandSet('REGIONALSETTINGS.themeSettings','')");
      
      if(mgr.isAuroraFeaturesEnabled())
      {
         bar.addCustomCommand("rwcSettings","Enterprise Explorer Integration");
         bar.removeCustomCommand("rwcSettings");
         tabs.addTab("FNDGENCONFIGRWCINTEGRATION: Enterprise Explorer Integration","javascript: commandSet('REGIONALSETTINGS.rwcSettings','')");
      }

      tabs.setTabWidth(100); 
      
   }
   
   public void regionalSettings()
   {   
      tabs.setActiveTab(1);
   }

   public void printerSettings()
   {   
      tabs.setActiveTab(2); 
   }

   public void themeSettings()
   {
      tabs.setActiveTab(3); 
   }

   public void rwcSettings()
   {
      tabs.setActiveTab(4); 
   }

//===============================================================
//  HTML
//===============================================================
   
   protected String getDescription()
   {
      return "FNDPAGESSETTINGS: Settings";
   }

   protected String getTitle()
   {
      return "FNDPAGESSETTINGS: Settings";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getDescription()));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(getTitle()));
      appendToHTML(tabs.showTabsInit()); 
    
      if (tabs.getActiveTab()==1)
      {
         beginDataPresentation();
         drawSimpleCommandBar("");
         appendToHTML("<table border='0' width='100%' cellpadding=0 class=pageFormWithBorder>");
         appendToHTML("<tr><td width='60%' valign='top'>");
         appendToHTML(lay_rs.generateDataPresentation());
         appendToHTML("</td><td>&nbsp;&nbsp;&nbsp;</td><td width='40%' valign='top' align='left'>");
         printNewLine();
         printBoldText("FNDPAGESINSTRUCTIONHEAD: Instructions:");
         printNewLine();printNewLine();
         printText("FNDPAGESINSTRUCTION: You can customize Regional Settings to display data as you wish. This will affect your global profile and it will be used by all IFS client applications.");
         printNewLine();printNewLine();
         appendToHTML("<ul><li>");
         printText("FNDPAGESINSTRUCTION1: You can remove your customized Regional Settings from IFS Web Client using the Reset button.");
         appendToHTML("</li><br><li>");
         printText("FNDPAGESINSTRUCTION3: If you want to use SPACE character as group separator then type the word 'SPACE'.");
         appendToHTML("</li><br><li>");
         printText("FNDPAGESINSTRUCTION4: You can group data by giving the size of the group. To disable grouping set the size to 0.");
         appendToHTML("</li>");         
         appendToHTML("</li><br><li>");
         printText("FNDPAGESINSTRUCTION5: Date patterns adhere to standard Java date patterns.");
         appendToHTML("<p>");
         appendToHTML("<b><i>");
         printText("FNDPAGESINSTRUCTION51: where:");
         appendToHTML("</b></i>");
         appendToHTML("<br>");         
         printText("FNDPAGESINSTRUCTION52: d - day of the month (1 through 31).");appendToHTML("<br>");
         printText("FNDPAGESINSTRUCTION53: M - month (1 through 12).");appendToHTML("<br>");
         printText("FNDPAGESINSTRUCTION54: yy - year, as two digits.");appendToHTML("<br>");
         printText("FNDPAGESINSTRUCTION55: h - hour of the day (1 through 12).");appendToHTML("<br>");
         printText("FNDPAGESINSTRUCTION55A: H - hour of the day (1 through 24).");appendToHTML("<br>");
         printText("FNDPAGESINSTRUCTION56: mm - minute within the hour (00 through 59).");appendToHTML("<br>");
         printText("FNDPAGESINSTRUCTION57: ss - second within the minute (00 through 59).");appendToHTML("<br>");
         printText("FNDPAGESINSTRUCTION58: a - AM/PM marker.");appendToHTML("<br>");
         appendToHTML("<br>");
         printText("FNDPAGESINSTRUCTION59: Check help on page for more details.");
         appendToHTML("<br>");
         appendToHTML("<br>");
         appendToHTML("<br>");
         appendToHTML("</p>");         
         appendToHTML("</li>");         
         appendToHTML("</td></tr></table>");
         printNewLine();
         printSubmitButton("RS_APPLY",mgr.translate("FNDAPPLYTHEME: Apply"),"");
         printSpaces(2);
         printSubmitButton("RS_RESET",mgr.translate("FNDPAGESGENERALCONFRESETBUT: Reset"),"");
         endDataPresentation(false);
      }
      
      else if (tabs.getActiveTab()==2)
      {
         beginDataPresentation();
         drawSimpleCommandBar("");
         
         appendToHTML("<table border='0' width='100%' class=pageFormWithBorder>");
         appendToHTML("<tr><td width='60%'>");
         
         beginTable("",true,true,"");
         beginTableBody();
         beginTableCell();
         printSpaces(1);
         endTableCell();
         nextTableRow();
         beginTableCell();
         printReadLabel("FNDPAGESGENERALCONFPRINTER: Printer:");
         endTableCell();
         beginTableCell();
         printMandatorySelectBox("PRINTER_LIST",all_printers,default_printer);
         endTableCell();
         nextTableRow();
         beginTableCell();
         printSpaces(1);
         endTableCell();
         nextTableRow();
         beginTableCell();
         printReadLabel("FNDPAGESGENERALCONFLANGUAGE: Language:");
         endTableCell();
         beginTableCell();
         printField("LANGUAGE",language,"");
         endTableCell();
         nextTableRow();
         beginTableCell();
         printSpaces(1);
         endTableCell();
         nextTableRow();
         beginTableCell();
         endTableCell();
         endTableBody();
         endTable();
         
         String note = mgr.translate("FNDPAGESDEFPRINTERNOTE: The Default printer selected here will be overwritten by the Default Printer set in the Application Services Module");
         
         appendToHTML("</td><td width='10%'>&nbsp;&nbsp;&nbsp;</td><td width='30%' valign='top' align='left'>");
         printNewLine();
         printBoldText("FNDPAGESDEFPRINTERHEAD: Note:");
         printNewLine();printNewLine();
         printText(note);
               
         appendToHTML("</td></tr></table>");
         printNewLine();
         printSubmitButton("PS_APPLY",mgr.translate("FNDAPPLYTHEME: Apply"),"onclick=\"javascript:alert('"+ note +"');\"");
         endDataPresentation(false);
      }
      else if (tabs.getActiveTab()==3)
         
      {
         beginDataPresentation();
         drawSimpleCommandBar("");
         appendToHTML("<table border='0' width='100%' class=pageFormWithBorder>");
         appendToHTML("<tr><td width='30%' valign='top'>");
         printNewLine();
         printText("FNDCHANGETHEMEINSTRUCTIONS: Select from the available themes listed below.");
         printNewLine();printNewLine();
         String[][] themes = mgr.getASPConfig().getThemes();
         for (int i=0; i<themes.length; i++)
         {
            if (!mgr.isEmpty(themes[i][1]))
            {
               String index = (i+1)+"";
               String name = getThemeName(i+1,themes[i][0]);
               printRadioButton (name,"THEMES",index,index.equals(default_theme)," OnClick=\"javascript:window.open('ThemeExample.page?__TEMP_THEME="+index+"','mock');\" ", i);
               printNewLine();
            }
         }
         printNewLine();
         appendToHTML("</td><td width='70%' height='400'><iframe src=\"ThemeExample.page\" name=\"mock\" id=\"mock\" frameborder=1 scrolling=\"no\" height=\"100%\" width=\"100%\"></iframe></td></tr>");
         appendToHTML("<tr><td colspan=\"2\">");
         appendToHTML("</table>");
         printNewLine();
         printSubmitButton("THEMEAPPLY","FNDAPPLYTHEME: Apply","");

         
         endDataPresentation(false);
      }
      else if(mgr.isAuroraFeaturesEnabled())
      {
         beginDataPresentation();
         drawSimpleCommandBar("");
         appendToHTML("<table border='0' width='100%' class=pageFormWithBorder>");
         appendToHTML("<tr><td valign='top'>");
         printCheckBox("FNDRWCINTEGRATIONSHOWLINKS: Show Enterprise Explorer links in standard browsers.","SHWRWCLINKSINBRW","TRUE",show_links,"");
         appendToHTML("</td></tr>");
         appendToHTML("</table>");
         printNewLine();
         printSubmitButton("RWC_APPLY","FNDAPPLYTHEME: Apply","");
         endDataPresentation(false);         
      }

      appendToHTML(tabs.showTabsFinish()); 
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out; 
   }

   private String getThemeName(int i, String default_name)
   {
       String name;
       ASPManager mgr = getASPManager();
       if(!mgr.isEmpty(default_name)) return default_name;
       String lng = mgr.getLanguageCode();
       //Admin can provide translated names for any themes
       name = mgr.getConfigParameter("THEMES/THEME"+i+"/NAME",null);
       if (!mgr.isEmpty(name))
           return name;
       switch (i)
       {
           //translating pre defined themes
           case 1: return mgr.translate("FNDGENCONFIGTHEME1: White and Blue");
           case 2: return mgr.translate("FNDGENCONFIGTHEME2: White and Green");
           case 3: return mgr.translate("FNDGENCONFIGTHEME3: Black and Blue");
           case 4: return mgr.translate("FNDGENCONFIGTHEME4: Black and Green");
           default: return default_name;
       }
   }
   
   private boolean validateSupportedDateTime(String input)
   {
      if(input !=null && input !="")
      {
         Pattern p = Pattern.compile("[GwWDFEkKSzZ]");
         Matcher m = p.matcher(input);
         return !m.find();
      }
      return true;
   }

}
