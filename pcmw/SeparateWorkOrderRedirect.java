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
*  File        : SeparateWorkOrderRedirect.java 
*  Modified    :
*    ARWILK  2001-02-19  - Created.
*    JEWI           2001-04-03  - Changed file extensions from .asp to .page
*    ARWILK         031222        Edge Developments - (Removed clone and doReset Methods)
*    AMNILK         060728        Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*    AMDILK         060807        Merged with Bug Id 58214
*    AMDILK         060814        Bug 58216, Eliminated SQL errors in web applications. Modified method okFind()
*    AMDILK         060904        Merged with the Bug Id 58216
*    AMNILK         070725        Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class SeparateWorkOrderRedirect extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.SeparateWorkOrderRedirect");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String isHis;
	private String sState;
	private String nWoNo;
	private String fieldVal;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public SeparateWorkOrderRedirect(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		isHis = "";
		sState = "";

		if (!mgr.isEmpty(mgr.getQueryStringValue("PARENTWONO")))
			nWoNo = mgr.readValue("PARENTWONO");
		else if ((!mgr.isEmpty(mgr.getQueryStringValue("WO_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("ISHISTORICAL"))))
		{
			nWoNo = mgr.readValue("WO_NO");
			isHis = mgr.readValue("ISHISTORICAL");
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			nWoNo = mgr.readValue("WO_NO");
			sState = mgr.readValue("STATE");
		}

		fieldVal = sState; 

		if ("TRUE".equals(isHis))
		{
			mgr.redirectTo("HistoricalSeparateRMB.page?WO_NO="+mgr.URLEncode(nWoNo));
		}
		else
		{
			mgr.redirectTo("ActiveSeparate2light.page?WO_NO="+mgr.URLEncode(nWoNo));
		}

		mgr.endResponse();
	}

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.addWhereCondition("WO_NO = ? and STATE = ? ");
		q.addParameter("WO_NO", nWoNo);
		q.addParameter("STATE", sState);

		q.includeMeta("ALL");

		mgr.submit(trans);
		eval(headblk.generateAssignments());
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("WO_NO");                   
		f.setHidden();

		f = headblk.addField("CONTRACT");
		f.setHidden();

		f = headblk.addField("STATE");
		f.setHidden();

		headblk.setView("ACTIVE_SEPARATE");
		headset = headblk.getASPRowSet();
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWSEPWOREDIRECT: Redirect Page";
	}

	protected String getTitle()
	{
		return "PCMWSEPWOREDIRECT: Redirect Page";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML("<html>\n");
		appendToHTML("<head>\n");
		appendToHTML("<title></title>\n");
		appendToHTML("</head>\n");
		appendToHTML(headblk.generateHiddenFields());		//XSS_Safe AMNILK 20070726
		appendToHTML("<body>\n");
		appendToHTML("</body>\n");
		appendToHTML("</html>\n");
	}

}
