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
*  File        : DummyWNDPmActionOvw2.java 
*  Modified    :
*	SHFELK  2001-Feb-15  - Created.
*   ARWILK  031223         Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class DummyWNDPmActionOvw2 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.DummyWNDPmActionOvw2");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	//===============================================================
	// Construction 
	//===============================================================
	public DummyWNDPmActionOvw2(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return null;
	}

	protected String getTitle()
	{
		return null;
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML("<html>\n");
		appendToHTML("<head>\n");
		appendToHTML("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n");
		appendToHTML("<title>New Page </title>\n");
		appendToHTML("</head>\n");
		appendToHTML("<body>\n");
		appendToHTML("</body>\n");
		appendToHTML("</html>\n");

		appendDirtyJavaScript(" window.open('");
		appendDirtyJavaScript(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"));
		appendDirtyJavaScript("'+\"pcmw/QueryEventPlanDlg.page\",\"frmQryEventPlan\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");

		appendDirtyJavaScript(" window.location = '");
		appendDirtyJavaScript(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"));
		appendDirtyJavaScript("'+\"pcmw/PmActionOvw2.page\";\n");
	}
}
