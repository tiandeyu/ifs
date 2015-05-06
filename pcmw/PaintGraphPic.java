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
*  File        : PaintGraphPic.java 
*  Created     : ARWILK  010308
*  Modified    :
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  060120  Call 131388: Modified run().
*  AMNILK  070719  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PaintGraphPic extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PaintGraphPic");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private int sImageHeight;
	private String sBackGroundColor;
	private String sTextColor;
	private String sTitleOfGraph;
	private String sFullPath;
	private String sVirtualPath;
	private String sImagePath; 
	private String sImageExt;
	private int sImageWidth;

	//===============================================================
	// Construction 
	//===============================================================
	public PaintGraphPic(ASPManager mgr, String page_path)
	{
		super(mgr, page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();

		sImagePath = ctx.readValue("CTXPATH", sImagePath);
		sImageExt = ctx.readValue("CTXEXT", sImageExt);
		sImageWidth = ctx.readNumber("CTXWIDTH", sImageWidth);
		sImageHeight = ctx.readNumber("CTXHEIGHT", sImageHeight);

		sBackGroundColor = mgr.getConfigParameter("SCHEME/FORM/BGCOLOR");
		sTextColor = mgr.getConfigParameter("SCHEME/FORM/FONT/COLOR/NORMAL");
		sFullPath = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"dynacache/"; 


		if (!mgr.isEmpty(mgr.getQueryStringValue("COGRAPHPATH")))
		{
			sImagePath = mgr.readValue("COGRAPHPATH");
			sImageExt = "." + mgr.readValue("COGRAPHEXT");
			sTitleOfGraph = mgr.readValue("TITLEOFGRAPH");
			sImageWidth = toInt(mgr.readNumberValue("COGRAPHWIDTH"));
			sImageHeight = toInt(mgr.readNumberValue("COGRAPHHEIGHT"));

			sImagePath = sFullPath + sImagePath + sImageExt;
		}

		ctx.writeValue("CTXPATH", sImagePath);
		ctx.writeValue("CTXEXT", sImageExt);
		ctx.writeNumber("CTXWIDTH", sImageWidth);
		ctx.writeNumber("CTXHEIGHT", sImageHeight);

		sTitleOfGraph = mgr.translate("PCMWPAINTGRAPHPICTITLE: " + sTitleOfGraph);
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("TESTFIELD");
		f.setHidden();

		headblk.setView("DUAL"); 

		disableHomeIcon();
		disableNavigate();
		disableOptions();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return sTitleOfGraph;
	}

	protected String getTitle()
	{
		return sTitleOfGraph;
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML("<table align=center width=100");
		appendToHTML("<tr>\n");
		appendToHTML("<td>\n");
		appendToHTML("<H1 ALIGN=center><IMG NAME=\"CreatedGraph\" ALIGN=\"middle\" HSPACE=30 VSPACE=30 SRC=");
		appendToHTML(mgr.HTMLEncode(sImagePath));			//XSS_Safe AMNILK 20070718
		appendToHTML(" WIDTH=");
		appendToHTML(mgr.HTMLEncode(Integer.toString(sImageWidth)));	//XSS_Safe AMNILK 20070718
		appendToHTML(" HEIGHT=");
		appendToHTML(mgr.HTMLEncode(Integer.toString(sImageHeight)));	//XSS_Safe AMNILK 20070718
		appendToHTML("></H1> \n");
		appendToHTML("</td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("</table>\n");

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
	}
}
