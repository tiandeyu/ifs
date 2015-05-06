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
 *  File        : ScheduledTask.java 
 *  Modified    :
 *  rahelk  2004-05-18 created.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision x.x  2008/08/18 buhilk
 * Bug id 76424, Modified printContents to resize iframe for scheduled task header.
 *
 * Revision x.x  2007/02/15 sadhlk
 * Bug id 63229, Overrided predefine() to enable what's this functionality in child frames.
 *
 * Revision x.x  2006/09/13 gegulk 
 * Bug id 60473, Modified the method printContents() to format properly in RTL mode
 *
 * Revision x.x  2006/03/09 rahelk
 * Added static constant for use when passing parameters to param step from pages
 *
 * Revision 1.2  2005/10/13 04:23:24  rahelk
 * fixed vertical scrollbar not seen in NS 7
 *
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/02/08 08:12:55  rahelk
 * Merged Call id 121571. Reimplemented scheduled reports to be similar to general tasks.
 *
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;



public class ScheduledTask extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ScheduledTask");
	private String introFlag;
   
   public static final String SCHED_REP_METHOD_NAME = "ARCHIVE_API.CREATE_AND_PRINT_REPORT__";
   public static final String CLIENT_PARAM_SEPARATOR = "^";

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	//===============================================================
	// Construction 
	//===============================================================
	public ScheduledTask(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}


	public void run() 
	{
		ASPManager mgr = getASPManager();

	}

   public void  preDefine() throws FndException
   {
      setChildFrameNames("main;navigator",true);
   }



//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "FNDSCHEDTASKDESC: Scheduled Tasks";
	}

	protected String getTitle()
	{
		return "FNDSCHEDTASKTITLE: Scheduled Tasks";
	}


	protected void printContents() throws FndException
	{ 
		ASPManager mgr = getASPManager();

		
      
      String schedule_type = "";
      
      if ("REPORTS".equals(mgr.getQueryStringValue("SCHEDULE_TYPE")))
         schedule_type = "?SCHEDULE_TYPE=REPORTS";

      beginDataPresentation();
      
      appendToHTML("<table border=0 width=100% cellspacing=0 cellpadding=0 ><tr>\n");
      appendToHTML("<td colspan=2>\n");
      drawSimpleCommandBar(mgr.translate(getTitle()));
      appendToHTML("</td></tr>\n");
      appendToHTML("<tr><td valign=top width='400px'>\n");
      if (mgr.isRTL())
         appendToHTML("  <iframe height=500 width=\"100%\" name=\"navigator\" id=\"navigator\" src = \"ScheduledTaskTreeList.page"+schedule_type);
      else
         appendToHTML("  <iframe width=\"100%\" height=500 name=\"navigator\" id=\"navigator\" src = \"ScheduledTaskTreeList.page"+schedule_type);
		appendToHTML("\" scrolling=\"auto\"\n");
		appendToHTML("  frameborder=\"0\" class=borders >\n");
      appendToHTML(mgr.translate("FNDSCHEDULETASKNOIFRAME: This page uses iframes, but your browser doesn't support them."));
      appendToHTML("  </iframe>\n");
		
      appendToHTML("</td><td valign=top >\n");
		appendToHTML(" <iframe width='100%' align=\"left\" height=502 name=\"main\" id=\"main\" src=\"ScheduledTaskHeader.page\" FRAMEBORDER=\"0\" scrolling=\"no\"> \n");
      appendToHTML(mgr.translate("FNDSCHEDULETASKNOIFRAME: This page uses iframes, but your browser doesn't support them."));
      appendToHTML("  </iframe>\n");
		
		appendToHTML("</td></tr></table>\n");
      endDataPresentation(false);
	}



}




