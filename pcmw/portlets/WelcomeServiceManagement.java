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
 * File        : WelcomeServiceManagement.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *  PJONSE   	00-05-15 - Created.
 *  JIJO	000904     Changed to NavigatorFrameSet.asp
 *  JIJO	001116     Localization errors
 *  VAGU        001121   - Call ID 53203
 *  Chamlk      03-10-29   Modified function printContents() to change the general 
 *                         text displayed at the top of the portlet
 *  VAGULK      040311     Web Alignment ,removed Clone() and doReset() methods.
 * ----------------------------------------------------------------------------
 *
 */


package ifs.pcmw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;


public class WelcomeServiceManagement extends ASPPortletProvider
{
	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	private ASPContext ctx; 

	private String calling_url;


	public WelcomeServiceManagement ( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
	}

	public String getTitle( int mode )
	{
		return translate(getDescription());
	}


	public static String getDescription()
	{
		return "PCMWWELSMTITLE: Welcome to IFS/Service Management";
	}


	public void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (mgr.isEmpty(mgr.getQueryString()))
			calling_url = mgr.getURL();
		else
			calling_url	= mgr.getURL()+"?"+mgr.getQueryString();

		ctx = getASPContext();
		ctx.setGlobal("CALLING_URL",calling_url);

		printNewLine();
		printText( "PCMWSMINFOTXTT1: This is your entrance to IFS Applications. You can configure your start page by choosing the 'Manage Portal' button in the page header above and then by choosing the " );
		printLink( "PCMWSMINFOLNK1: Configuration", "javascript:customizePage()" );
		printText( "PCMWSMINFOTXTT2:  menu item. You can navigate to other pages by choosing the " );
		printLink( "PCMWSMINFOLNKK2: Navigate", getASPManager().getConfigParameter("APPLICATION/LOCATION/NAVIGATOR"));
		printText( "PCMWSMINFOTXTT3:  button in the page header above." );
		printText( "PCMWSMINFOTXT4:  Below are a few other useful links." );
		printSpaces(25);
		printNewLine();

		printImageLink( "pcmw/images/PMDrawingIconSmall.gif",  "equipw/FramesetCad.page");
		printSpaces(3);
		printText( "PCMWGENSM1: Find object information through " );
		printLink( "PCMWGENSMLNK1: drawings", "equipw/FramesetCad.page" );
		printNewLine();

		printImageLink( "pcmw/images/PMTreeStructureIconSmall.gif",  "equipw/NavigatorFrameSet.page");
		printSpaces(3);
		printText( "PCMWGENSM6: Navigate through object " );
		printLink( "PCMWGENSMLNK2: structure", "equipw/NavigatorFrameSet.page" );
		printNewLine();

		printImageLink( "pcmw/images/PMFRIconSmall.gif",  "pcmw/ActiveSeparateWizB2E.page?WNDFLAG=ActiveSeparateWizB2EFromPortal");
		printSpaces(3);
		printText( "PCMWGENSM2: Register a " );
		printLink( "PCMWGENSMLNK6: Service Request", "pcmw/ActiveSeparateWizB2E.page?WNDFLAG=ActiveSeparateWizB2EFromPortal" );
		printNewLine();

		printImageLink( "pcmw/images/PMQuickIconSmall.gif",  "pcmw/ReportInWorkOrderWiz.page?WNDFLAG=ReportInWorkOrderWizFromPortal");
		printSpaces(3);
		printLink( "PCMWGENSMLNK5: Report In Work ", "pcmw/ReportInWorkOrderWiz.page?WNDFLAG=ReportInWorkOrderWizFromPortal" );
		printText( "PCMWGENSM3: on an existing Work Order, " );
		printNewLine();
		printSpaces(13);
		printText( "PCMWGENSM44: directly to history(SM)");
		printNewLine();

		printImageLink( "pcmw/images/PMQuickIconSmall.gif",  "pcmw/QuickReportInIntroSM.page?WNDFLAG=QuickReportInIntroSMFromPortal");
		printSpaces(3);
		printLink( "PCMWGENSMLNK3: Report In Work ", "pcmw/QuickReportInIntroSM.page?WNDFLAG=QuickReportInIntroSMFromPortal" );
		printText( "PCMWGENSM4: directly to history(SM)" );
		printNewLine();

		printImageLink( "pcmw/images/PMQuotIconSmall.gif",  "pcmw/WorkOrderQuotationWiz.page?WNDFLAG=WorkOrderQuotationWizFromPortal");
		printSpaces(3);
		printLink( "PCMWGENSMLNK4: Follow up ", "pcmw/WorkOrderQuotationWiz.page?WNDFLAG=WorkOrderQuotationWizFromPortal" );
		printText( "PCMWGENSM5: on Work Order Quotation"); 
		printNewLine();
		printNewLine();
	}


}


