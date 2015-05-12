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
*  File        : DateFieldFormatter.java
*  Description : Formats the date according to the mask.
*  Notes       : Called by Calendar.js to format Date, Datetime fields. 
* ----------------------------------------------------------------------------
*  Modified    :
*     Ramila H    2002-07-30 - Created file.
* ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;
import java.util.Calendar;
import java.text.*;

public class DateFieldFormatter extends ASPPageProvider
{
   private Calendar cal;
   private SimpleDateFormat dateformatter;
   private String year;
   private String month;
   private String day;
   private String hours;
   private String min;
   private String sec;
   private String mask;

   //===============================================================
   // Construction
   //===============================================================
   public DateFieldFormatter(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      year = mgr.getQueryStringValue("YEAR");
      month = mgr.getQueryStringValue("MONTH");
      day = mgr.getQueryStringValue("DAY");
      hours = mgr.getQueryStringValue("HOURS");
      min = mgr.getQueryStringValue("MIN");
      sec = mgr.getQueryStringValue("SEC");
      mask = mgr.getQueryStringValue("MASK");
      
      try{
         mgr.responseWrite(FormatDate(year,month,day,hours,min,sec,mask)+"^");
      }
      catch (Exception e)
      {
         mgr.responseWrite("No_Data_Found"+e);
      }
      
      mgr.endResponse();

   }
   
   public String FormatDate(String sYear,
                            String sMonth, 
                            String sDay, 
                            String sHours,
                            String sMin,
                            String sSec,
                            String mask)
   {
      cal = Calendar.getInstance();
      int year = Integer.valueOf(sYear).intValue();
      int month = Integer.valueOf(sMonth).intValue();
      int day = Integer.valueOf(sDay).intValue();
      int hour = Integer.valueOf(sHours).intValue();
      int min = Integer.valueOf(sMin).intValue();
      int sec = Integer.valueOf(sSec).intValue();
      
      cal.set(year,month,day,hour,min,sec);
      
      dateformatter = new SimpleDateFormat(mask);
      
      return dateformatter.format(cal.getTime());

   }
   
}
