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
*  File        : AppsrwNavigator.java 
*  Modified    : CHCR  2001-03-19  Created.
* ----------------------------------------------------------------------------
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;

public class AppsrwNavigator implements Navigator
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.AppsrwNavigator");

   public ASPNavigatorNode add(ASPManager mgr)
   {
      if(DEBUG) Util.debug("!!AppsrwNavigator!!");

      ASPNavigatorNode nod = mgr.newASPNavigatorNode("APPSRWNAVHED: Application Services");

      nod.addItem("APPSRWNAV0: Units of Measure", "appsrw/IsoUnit.page","ISO_UNIT");
      nod.addItem("APPSRWNAV1: Unit Relationships", "appsrw/UnitRelationships.page","ISO_UNIT");

      ASPNavigatorNode n = nod.addNode("APPSRWNAV2: Characteristics");
      n.addItem("APPSRWNAV21: Best Fit Search", "appsrw/BestFitSearchWiz.page?WNDFLAG=OpenInNewWin","TECHNICAL_CLASS");

      ASPNavigatorNode n1 = n.addNode("APPSRWNAV22: Basic Data");
      n1.addItem("APPSRWNAV221: Characteristic Basic","appsrw/CharacteristicBasic.page","TECHNICAL_CLASS");
      n1.addItem("APPSRWNAV222: Template","appsrw/Template.page","TECHNICAL_CLASS");
      n1.addItem("APPSRWNAV223: Technical Class Groups","appsrw/TechnicalClassGroups.page","TECHNICAL_CLASS");
   
      n = nod.addNode("APPSRWNAV3: Manager");
      n.addItem("APPSRWNAV31: ISO Code Definition","appsrw/IsoCodeDefinition.page","ISO_COUNTRY_DEF");
      n.addItem("APPSRWNAV32: System Definition","appsrw/SystemDefinition.page","ATTRIBUTE_DEFINITION");
      n.addItem("APPSRWNAV33: Printer Definition","appsrw/PrinterDefinition.page","LOGICAL_PRINTER");

      n = nod.addNode("APPSRWNAV4: Work Time Calendar");
      n.addItem("APPSRWNAV41: Period","appsrw/WorkTimePeriodOvw.page","WORK_TIME_PERIOD");  
      n.addItem("APPSRWNAV42: Day Type","appsrw/WorkTimeDayType.page","WORK_TIME_DAY_TYPE");
      n.addItem("APPSRWNAV43: Schedule","appsrw/WorkTimeSchedule.page","WORK_TIME_SCHEDULE");
      n.addItem("APPSRWNAV44: Schedule Exception","appsrw/WorkTimeException.page","WORK_TIME_EXCEPTION");
      n.addItem("APPSRWNAV45: Calendar","appsrw/WorkTimeCalendar.page","WORK_TIME_CALENDAR"); 

      n1 = n.addNode("APPSRWNAV46: Generated Calendar");
      n1.addItem("APPSRWNAV461: Overview - Generated Calendar","appsrw/WorkTimeCounterOvw.page","WORK_TIME_COUNTER");  
      n1.addItem("APPSRWNAV462: Overview - Generated Calendar Details","appsrw/WorkTimeCounterDescOvw.page","WORK_TIME_COUNTER_DESC");	  
	  
	  return nod;
   }
}
