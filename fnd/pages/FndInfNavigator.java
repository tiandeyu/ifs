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
 * File        : FndInfNavigator.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Artur K  2001-Mar-03 - Created
 *    Jacek P  2001-Mar-28 - Removed deprecated functions. Changed .asp to .page
 *    Ramila H 2003-Aug-01 - Log Id 1052, changed link name.
 *    Ramila H  2004-05-18 - Added scheduled reports node
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010-09-15  VOHELK - Bug 93000, Introduced new view, inorder to filter out new QR types 
 *
 * Revision 1.3  2005/11/10 04:34:58  rahelk
 * Online report execution for web implemented
 *
 * Revision 1.2  2005/09/22 12:39:23  japase
 * Merged in PKG 16 changes
 *
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 *
 * Revision 1.2.4.1  2005/08/26 09:55:28  rahelk
 * Removed old order report wizard from navigator
 *
 * Revision 1.2  2005/06/07 07:52:00  mapelk
 * Removed Agenda page
 *
 * ----------------------------------------------------------------------------
 */


package ifs.fnd.pages;

import ifs.fnd.asp.*;

public class FndInfNavigator implements ifs.fnd.asp.Navigator
{
   public ASPNavigatorNode add(ASPManager mgr)
   {
      ASPNavigatorNode nod = mgr.newASPNavigatorNode( "FNDINFNAVINFOSERVICE: Info Services" );
      nod.addItem( "FNDINFNAVREPORTORDER: Order Report Wizard", "common/scripts/ReportOrder.page", "ARCHIVE" );
      nod.addItem( "FNDINFNAVPRINTMANAGER: Print Manager", "common/scripts/PrintManager.page", "PRINT_JOB" );
      nod.addItem( "FNDINFNAVREPORTARCHIVATE: Report Archive","common/scripts/ReportArchive.page", "ARCHIVE" );
      nod.addItem( "FNDINFNAVQUICKREPORT: Quick Report", "common/scripts/QuickReport.page", "QUICK_REPORT_NON_BI,REPORT_CATEGORY" );
      nod.addItem( "FNDSERNAVSCHREPORTS: Scheduled Reports", "common/scripts/ScheduledTask.page?SCHEDULE_TYPE=REPORTS");
      return nod;
   }
}
