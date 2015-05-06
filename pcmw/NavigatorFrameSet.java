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
*  File        : NavigatorFrameSet.java 
*  Created     : ASP2JAVA Tool 010307 Created Using the ASP file NavigatorFrameSet.asp
*  Modified    :
*  SHFELK  010307  Corrected some conversion errors.
*  JEWILK  010403  Changed file extensions from .asp to .page
*  ARWILK  031223  Edge Developments - (Removed clone and doReset Methods)
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug ID: 58214.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class NavigatorFrameSet extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.equipw.NavigatorFrameSet");

	private String introFlag;
	private String woNo_;

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	//===============================================================
	// Construction 
	//===============================================================
	public NavigatorFrameSet(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		introFlag = "0";

		if ((!(mgr.isEmpty(mgr.readValue("WO_NO","")))))
			woNo_ = mgr.URLEncode(mgr.readValue("WO_NO",""));

		if ((!(mgr.isEmpty(woNo_))))
			introFlag = "1";

	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return null ;
	}

	protected String getTitle()
	{
		return null;
	}

	protected AutoString getContents() throws FndException
	{ 
		AutoString out = getOutputStream();
		out.clear();
		ASPManager mgr = getASPManager();

		out.append("<html>\n");
		out.append("<head>\n");
		out.append(mgr.generateHeadTag("NAVFRAMESETTITLE: Work Order Structure"));
		out.append("<title></title>\n");
		out.append("</head>\n");
		out.append("<frameset cols=\"24%,*\">\n");
		out.append("  <frame name=\"navigator\" target=\"main\" src = \"WorkOrderNavigator.page?WO_NO=");
		out.append(mgr.URLEncode(woNo_));
		out.append("\" scrolling=\"auto\"\n");
		out.append("  BORDER=\"1\">\n");

		if ("1".equals(introFlag))
		{
			out.append("       <frame name=\"main\" src=\"ActiveSeparate2WOStructure.page?WO_NO=");
                        out.append(mgr.URLEncode(woNo_));
			out.append("\" BORDER=\"0\" FRAMEBORDER=\"0\">\n");
		}
		else
			out.append("       <frame name=\"main\" src=\"ActiveSeparate2WOStructure.page\" BORDER=\"0\" FRAMEBORDER=\"0\">    \n");

		out.append("  <body><noframes>\n");

		out.append("  <form name=\"frmWorkOrderStructure\" id=\"frmWorkOrderStructure\">\n");
		out.append("  <p>This page uses frames, but your browser doesn't support them.</p>\n");

		printHiddenField("recentDwf",mgr.readValue("recentDwf"));

		out.append("  </form>\n");

		out.append("  </noframes></body>\n");
		out.append("</frameset>\n");
		out.append("</html>\n");

		return out;
	}
}




