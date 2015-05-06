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
 * File        : WelcomeMaintenance.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *  PJONSE   	00-05-15 - Created.
 *	JIJO    000904     Changed to NavigatorFrameSet.asp
 *      Chamlk  03-10-29   Modified function printContents() to change the general 
 *                         text displayed at the top of the portlet
 *      VAGULK  040311     Web Alignment ,Removed Clone() and doReset() methods.
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


public class WelcomeMaintenance extends ASPPortletProvider
{
	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	private ASPContext ctx; 

	private String calling_url;


	public WelcomeMaintenance( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
	}

	public String getTitle( int mode )
	{
		return translate(getDescription());
	}


	public static String getDescription()
	{
		return "PCMWWELMAINTITLE: Welcome to IFS/Maintenance";
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
		printText( "PCMWGENINFOTXTT1: This is your entrance to IFS Applications. You can configure your start page by choosing the 'Manage Portal' button in the page header above and then by choosing the " );
		printLink( "PCMWGENINFOLNK1: Configuration", "javascript:customizePage()" );
		printText( "PCMWGENINFOTXTT2:  menu item. You can navigate to other pages by choosing the " );
		printLink( "PCMWGENINFOLNKK2: Navigate", getASPManager().getConfigParameter("APPLICATION/LOCATION/NAVIGATOR"));
		printText( "PCMWGENINFOTXTT3:  button in the page header above." );
		printText( "PCMWGENINFOTXT4:  Below are a few other useful links." );
		printSpaces(25);
		printNewLine();

		printImageLink( "pcmw/images/PMDrawingIconSmall.gif",  "equipw/FramesetCad.page");
		printSpaces(3);
		printText( "PCMWGENMAINT1: Find object information through " );
		printLink( "PCMWGENMTLNK1: drawings", "equipw/FramesetCad.page" );
		printNewLine();

		printImageLink( "pcmw/images/PMTreeStructureIconSmall.gif",  "equipw/NavigatorFrameSet.page");
		printSpaces(3);
		printText( "PCMWGENMAINT5: Navigate through object " );
		printLink( "PCMWGENMTLNK2: structure", "equipw/NavigatorFrameSet.page" );
		printNewLine();

		printImageLink( "pcmw/images/PMFRIconSmall.gif",  "pcmw/ActiveSeparateWiz.page?WNDFLAG=ActiveSeparateWizFromPortal");
		printSpaces(3);
		printText( "PCMWGENMAINT2: Make a " );     
		printLink( "PCMWGENMTLNK3: Fault Report", "pcmw/ActiveSeparateWiz.page?WNDFLAG=ActiveSeparateWizFromPortal" );
		printNewLine();

		printImageLink( "pcmw/images/PMQuickIconSmall.gif",  "pcmw/ReportInWorkOrderWiz.page?WNDFLAG=ReportInWorkOrderWizFromPortal");
		printSpaces(3);
		printLink( "PCMWGENMTLNK4: Report In Work ", "pcmw/ReportInWorkOrderWiz.page?WNDFLAG=ReportInWorkOrderWizFromPortal" );
		printText( "PCMWGENMAINT3: on an existing Work Order" );
		printNewLine();

		printImageLink( "pcmw/images/PMQuickIconSmall.gif",  "pcmw/QuickReportToHistory.page?WNDFLAG=QuickReportToHistoryFromPortal");
		printSpaces(3);
		printLink( "PCMWGENMTLNK5: Report In Work ", "pcmw/QuickReportToHistory.page?WNDFLAG=QuickReportToHistoryFromPortal" );
		printText( "PCMWGENMAINT4: directly to history" );
		printNewLine();


	}


}


