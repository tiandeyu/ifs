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
*  File        : ActiveSeperateRedirect.java 
*  Modified    :
*    THWILK     2003-10-21  - Call ID 108136 - Created.
*    ARWILK     031222        Edge Developments - (Removed clone and doReset Methods)
*    JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*    AMNILK  060807  Merged Bug Id: 58214.
*    JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*    NAMELK  060906  Merged Bug 58216.
*    ILSOLK  070730  Eliminated LocalizeErrors.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveSeperateRedirect extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeperateRedirect");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock itemblk;
	private ASPField f;
	private ASPRowSet itemset;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String swoNo;
	private ASPBuffer buffer;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveSeperateRedirect(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();   
		trans = mgr.newASPTransactionBuffer();   

		if ( !mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) )
		{

			swoNo = mgr.getQueryStringValue("WO_NO");

			trans.clear();

			ASPQuery q = trans.addEmptyQuery(itemblk);
			q.addWhereCondition("WO_NO = ?");
                        q.addParameter("WO_NO",swoNo);


			q.includeMeta("ALL");

			mgr.submit(trans);


			if ( itemset.countRows() == 0 )
			{

                                mgr.redirectTo("ActiveRound.page?WO_NO="+mgr.URLEncode(swoNo)); 

			}
			else
			{

                                mgr.redirectTo("ActiveSeperateReportInWorkOrder.page?WO_NO="+mgr.URLEncode(swoNo)); 
			}
		}
		mgr.endResponse(); 
	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		itemblk = mgr.newASPBlock("ITEM");

		f = itemblk.addField("WO_NO");
		f.setHidden();
		f.setFunction("''");

		itemblk.setView("ACTIVE_SEPARATE");
		itemset = itemblk.getASPRowSet();
	}
//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWACTIVESEPARATE3REDDESC: Redirect Page";
	}

	protected String getTitle()
	{
		return "PCMWACTIVESEPARATE3REDTITLE: Redirect Page";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
	}

}

