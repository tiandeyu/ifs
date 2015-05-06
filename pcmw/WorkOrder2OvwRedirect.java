
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
*  File        : WorkOrder2OvwRedirect.java 
*  Modified    :
*    ASP2JAVA Tool  2001-05-16  - Created Using the ASP file WorkOrder2OvwRedirect.asp (chdelk)
*    THWILK         2003-10-20  - Call Id 108383,Modified the redirection of .asp into .page within the run method.
*    ARWILK         031223        Edge Developments - (Removed clone and doReset Methods)
*    AMNILK 	    060731	  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*    AMDILK         060807        Merged with Bug Id 58214
*    AMNILK         070727        Eliminated SQLInjections Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrder2OvwRedirect extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrder2OvwRedirect");

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
	private String nWoNo;
	private ASPCommand cmd;
	private String isActive;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrder2OvwRedirect(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();

		if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			nWoNo = mgr.readValue("WO_NO");
			okFind();
		}

		trans.clear();    
		cmd = trans.addCustomFunction( "ISACT","Work_Order_API.Is_Active","ACTIVE");
		cmd.addParameter("WO_NO",nWoNo);

		trans = mgr.perform(trans);

		isActive = trans.getValue("ISACT/DATA/ACTIVE");
		trans.clear();

		if ("TRUE".equals(isActive))
		{
			mgr.redirectTo("ActiveSeparate2light.page?WO_NO="+mgr.URLEncode(nWoNo));
		}
		else
		{
			mgr.redirectTo("HistoricalSeparateRMB.page?WO_NO="+mgr.URLEncode(nWoNo));
		}
		mgr.endResponse();
	}

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");

		mgr.submit(trans);
		trans.clear();
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("ACTIVE");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("WO_NO");
		f.setHidden();
		f.setFunction("''");

		headblk.setView("DUAL");
		headset = headblk.getASPRowSet();
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "";
	}

	protected String getTitle()
	{
		return "";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML("<html>\n");
		appendToHTML("<head>\n");
		appendToHTML("<title></title>\n");
		appendToHTML("</head>\n");
		appendToHTML(headblk.generateHiddenFields());	//XSS_Safe AMNILK 20070726
		appendToHTML("<body>\n");
		appendToHTML("</body>\n");
		appendToHTML("</html>\n");
	}
}
