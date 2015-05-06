package ifs.genbaw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import java.util.*;
import java.io.*;
import java.lang.*;

/**
 */
public class GenbawNotificationCenter extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int size = 10;
	// MDAHSE, 2001-08-16
	public static boolean DEBUG = Util.isDebugEnabled("ifs.genbaw.portlets.GenbawNotificationCenter");

	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;
   private ASPBuffer data;  //Bug Id 56231.


	//==========================================================================
	//  Mutable attributes
	//==========================================================================


	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================

	private transient int      skip_rows;
	private transient int      db_rows;

	//==========================================================================
	//  Construction
	//==========================================================================

	public GenbawNotificationCenter( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
	}

	public ASPPage construct() throws FndException
	{
		return super.construct();
	}

	//==========================================================================
	//  Methods for implementation of pool
	//==========================================================================

	protected void doReset() throws FndException
	{
		super.doReset();
		//   no reset of variables since they should be "remembered".
	}

	public ASPPoolElement clone( Object mgr ) throws FndException
	{
	   GenbawNotificationCenter page = (GenbawNotificationCenter)(super.clone(mgr));

		page.ctx    = page.getASPContext();
		page.blk    = page.getASPBlock(blk.getName());
		page.rowset = page.blk.getASPRowSet();
		page.tbl    = page.getASPTable(tbl.getName());

		return page;
	}


	protected void preDefine() throws FndException
	{
		ctx = getASPContext();

		blk = newASPBlock("MAIN");

		addField(blk, "TITLE").
		setLabel("GENERALNOTIFICATIONTITLE: Title").
		setHyperlink("genbaw/GeneralNotificationQuery.page", "NOTIFICATION_NO", "NEWWIN").
		// setFieldWrap(2).
		setSize(30);

		addField(blk, "SCOPE_TYPE").
//      enumerateValues("General_Nofification_Type_API").
//      setSelectBox().
      setLabel("GENERALNOTIFICATIONCENTERSCOPETYPE: Scope Type").
      setSize(10);
		
		addField(blk, "EMERGENCY").
      setLabel("GENERALNOTIFICATIONEMERGENCY: Emergency").
      setSize(10);
		
		addField(blk, "EMERGENCY_DB").
		setLabel("GENERALNOTIFICATIONEMERGENCYDB: Emergency Db").
		setHidden().
		setSize(10);
		
		addField(blk, "CREATE_PERSON_NAME").
      setFunction("PERSON_INFO_API.GET_NAME(CREATE_PERSON)").
      setLabel("GENERALNOTIFICATIONCENTERCREATEPERSONNAME: Create Person").
      setSize(20);
		
		addField(blk, "CREATE_DATE").
		setFunction("TO_CHAR(CREATE_DATE, 'YYYY-MM-DD')").
      setLabel("GENERALNOTIFICATIONCREATEDATE: Create Date").
      setSize(10);
		
		addField(blk, "VIEW_BUSINESS").
		setFunction("''").
      setLabel("GENERALNOTIFICATIONCENTERVIEWBUSINESS: Business").
      setAsImageField().
      setHyperlink("genbaw/GenbawIntermediatePage.page", "CONNECTED_PAGE_URL,CONNECTED_KEY_REF", "NEWWIN").
      setSize(10);
		
		addField(blk, "EXC_VIEW").
      setFunction("''").
      setLabel("GENERALNOTIFICATIONEXCVIEW: Exc").
      setAsImageField().
      setHyperlink("genbaw/GenbawIntermediatePage.page?GENBAW_COMMAND=EXC_NOTIFICATION&GENBAW_PREFIX=" + addProviderPrefix(), "NOTIFICATION_NO", "NEWWIN").
      setSize(10);
		
		addField(blk, "NOTIFICATION_NO").
		setHidden().
		setLabel("GENERALNOTIFICATIONNOTIFICATIONNO: Notification No").
      setSize(10);
		
		addField(blk, "CONNECTED_PAGE_URL").
      setHidden().
      setLabel("GENERALNOTIFICATIONCONNECTEDPAGEURL: Connected Page Url").
      setSize(50);
		
		addField(blk, "CONNECTED_KEY_REF").
      setHidden().
      setLabel("GENERALNOTIFICATIONCONNECTEDKEYREF: Connected Key Ref").
      setSize(50);
		
		blk.setView("GENERAL_NOTIFICATION_CU");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

		rowset = blk.getASPRowSet();
		getASPManager().newASPCommandBar(blk);

		appendJavaScript( "function firstCust(obj,id)",
		      "{",
		      "   getPortletField(id,'CMD').value = 'FIRST';",
		      "}\n");
		
		appendJavaScript( "function prevCust(obj,id)",
		      "{",
		      "   getPortletField(id,'CMD').value = 'PREV';",
		      "}\n");
		
		appendJavaScript( "function lastCust(obj,id)",
		      "{",
		      "   getPortletField(id,'CMD').value = 'LAST';",
		      "}\n");
		
		appendJavaScript( "function nextCust(obj,id)",
		      "{",
		      "   getPortletField(id,'CMD').value = 'NEXT';",
		      "}\n");
		
		init();
	}

	protected String getImageFieldTag(ASPField imageField, ASPRowSet rowset, int rowNum) throws FndException
	{
      ASPManager mgr = getASPManager();
      String imgSrc = mgr.getASPConfig().getImagesLocation();
      if (rowset.countRows() > 0)
      {
         if ("VIEW_BUSINESS".equals(imageField.getName()))
         {
            if (!Str.isEmpty(rowset.getValueAt(rowNum, "CONNECTED_KEY_REF")) && !Str.isEmpty(rowset.getValueAt(rowNum, "CONNECTED_PAGE_URL")))
            {
               imgSrc += "app_view.gif";
               return "<img src=\"" + imgSrc + "\" height=\"16\" width=\"16\" border=\"0\">";
            }
         }
         else if ("EXC_VIEW".equals(imageField.getName()))
         {
            imgSrc += "general_delete.gif";
            return "<img src=\"" + imgSrc + "\" height=\"16\" width=\"16\" border=\"0\">";
         }
      }
      return "";
   }
	
	protected void init()
	{
		blk    = getASPBlock("MAIN");
		rowset = blk.getASPRowSet();
		tbl    = getASPTable(blk.getName());
		ASPContext ctx = getASPContext();

		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );
		String cmd = readValue("CMD");

		if ("FIRST".equals(cmd))
      {
         skip_rows = 0;
      }
      else if ("PREV".equals(cmd))
      {
         if (skip_rows>=size)
         {
            skip_rows -= size;
         }
         else
         {
            skip_rows = 0;
         }
      }
      else if ("NEXT".equals(cmd) && skip_rows<=db_rows-size)
      {
         skip_rows += size;
      }
      else if ("LAST".equals(cmd))
      {
         skip_rows = ((db_rows + size - 1)/size - 1) * size;
      }
      else
      {
         skip_rows = 0;
      }

		ctx.writeValue("SKIP_ROWS", skip_rows + "");
	}


	protected void run() throws FndException
	{
		String commd = readValue("CMD");
		String notification_no = readValue("EXC_NOTIFICATION_NO");
		ASPCommand cmd;

		if (Str.isEmpty(commd) || "NEXT".equals(commd) || "PREV".equals(commd) || "FIRST".equals(commd) || "LAST".equals(commd))
		{
			ASPManager           mgr    = getASPManager();
			ASPTransactionBuffer trans  = mgr.newASPTransactionBuffer();
			
			if (!Str.isEmpty(notification_no))
	      {
	         cmd = trans.addCustomCommand("CREEXC", "GENERAL_NOTIFICATION_EXC_API.Create_Exc_Notification_CP");
	         cmd.addParameter(this, "NOTIFICATION_NO", notification_no);
	         mgr.perform(trans);
	         trans.clear();
	      }
			
			ASPQuery             qry    = trans.addQuery(blk);
			qry.includeMeta("ALL");
			qry.setBufferSize(size);
			
			if (Str.isEmpty(commd))
				qry.skipRows(0);
			else
				qry.skipRows(skip_rows);
			
			submit(trans);
			db_rows = blk.getASPRowSet().countDbRows();
			getASPContext().writeValue("DB_ROWS", db_rows + "" );
			// getASPContext().writeValue("SKIP_ROWS", skip_rows + "");
		}
	}

	public String getTitle( int mode )
	{
		String name = "";
		
		if (Str.isEmpty(name))
      {
         name = translate(getDescription());
      }
		
      if (mode == ASPPortal.MINIMIZED)
      {
         return name + " " + translate("GENBAWNOTIFICATIONCENTERTITLE: - You have ") + db_rows + " " + translate("GENBAWNOTIFICATIONCENTERMESSAGES: Notification Messages");
      }
      else
         return name; 
	}
	
	public void printEmptyTable() throws FndException
	{
	   beginCustomTable(null, false, " border=0 CELLPADDING=0 CELLSPACING=0 width=100% ");
      beginTableTitleRow();
      printTableCell(translate("GENBAWNOTIFICATIONCENTERNODATA: No Notification Message Found."), "LEFT");
      endTableTitleRow();
      endTable();
	}
	
	public void printCustomTable() throws FndException
   {
      ASPManager mgr = getASPManager();
      String imageurl;
      String href = "";
      String value = "";
      int field_size;
      String img_loc = mgr.getASPConfig().getImagesLocation();

      beginCustomTable(null, false, " CLASS=\"multirowTable\" border=0 CELLPADDING=1 CELLSPACING=1 width=100% ");
      beginTableTitleRow();
      printTableCell(translate("GENERALNOTIFICATIONTITLE: Title"));
      printTableCell(translate("GENERALNOTIFICATIONCENTERSCOPETYPE: Scope Type"));
      printTableCell(translate("GENERALNOTIFICATIONEMERGENCY: Emergency"));
      printTableCell(translate("GENERALNOTIFICATIONCENTERCREATEPERSONNAME: Create Person"));
      printTableCell(translate("GENERALNOTIFICATIONCREATEDATE: Create Date"));
      printTableCell(translate("GENERALNOTIFICATIONCENTERVIEWBUSINESS: Business"));
      printTableCell(translate("GENERALNOTIFICATIONEXCVIEW: Exc"));
      endTableTitleRow();
      beginTableBody();

      rowset.first();
      for (int i=0; i<rowset.countRows(); i++)
      {
         String query_url = "javascript:showNewBrowser('genbaw/GeneralNotificationQuery.page" + 
                            "&NOTIFICATION_NO=" + mgr.URLEncode(rowset.getValue("NOTIFICATION_NO")) + "')";
         String view_url =  "javascript:showNewBrowser('genbaw/GenbawIntermediatePage.page" +
                            "?CONNECTED_PAGE_URL=" + mgr.URLEncode(rowset.getValue("CONNECTED_PAGE_URL")) +
                            "&CONNECTED_KEY_REF=" + mgr.URLEncode(rowset.getValue("CONNECTED_KEY_REF")) + "')";
         /*String exc_url =   "javascript:showNewBrowser('genbaw/GenbawIntermediatePage.page" +
                            "?GENBAW_COMMAND=EXC_NOTIFICATION" +
                            "&GENBAW_PREFIX=" + mgr.URLEncode(addProviderPrefix()) +
                            "&NOTIFICATION_NO=" + mgr.URLEncode(rowset.getValue("NOTIFICATION_NO")) + "')";*/
         String exc_url  =  "javascript:" + addProviderPrefix() + "setNotificationNo('" + rowset.getValue("NOTIFICATION_NO") + "')";
         
         value = rowset.getValue("TITLE");
         field_size = blk.getASPField("TITLE").getSize();
         if(value.length() > field_size )
            value = (field_size < 3 ? "" : value.substring(0, field_size-3)) + "...";
         beginTableCell(0,0,null,null,true);
         printLink(value, query_url);
         endTableCell();

         beginTableCell(0,0,null,null,true);
         printText(rowset.getValue("SCOPE_TYPE"));
         endTableCell();

         beginTableCell(0,0,null,null,true);
         if ("CRITICAL".equals(rowset.getValue("EMERGENCY_DB")))
            printHiliteText(rowset.getValue("EMERGENCY"));
         else
            printText(rowset.getValue("EMERGENCY"));
         endTableCell();
         
         beginTableCell(0,0,null,null,true);
         printText(rowset.getValue("CREATE_PERSON_NAME"));
         endTableCell();
         
         beginTableCell(0,0,null,null,true);
         printText(rowset.getValue("CREATE_DATE"));
         endTableCell();
         
         beginTableCell(0,0,"center",null,true);
         if (!Str.isEmpty(rowset.getValue("CONNECTED_PAGE_URL")) && !Str.isEmpty(rowset.getValue("CONNECTED_KEY_REF")))
         {
            imageurl = img_loc + "app_view.gif";
            printImageLink(imageurl, view_url, "", translate("GENERALNOTIFICATIONVIEWBUSINESS: Business"));
         }
         else
         {
            printSpaces(1);
         }
         endTableCell();
         
         beginTableCell(0,0,"center",null,true);
         imageurl = img_loc + "general_delete.gif";
         printImageLink(imageurl, exc_url, "", translate("GENERALNOTIFICATIONEXCVIEW: Exc"));
         endTableCell();
         
         nextTableRow();
         rowset.next();
      }
      rowset.first();

      endTableBody();
      endTable();
   }

	public void printContents() throws FndException
	{
	   printHiddenField("CMD","");
	   printHiddenField("EXC_NOTIFICATION_NO","");
      int     nRows;
      nRows = rowset.countRows();
      
      if (nRows > 0)
      {
         // printTable(tbl);
         printCustomTable();
         
         if (size < db_rows)
         {
            printNewLine();

            if (skip_rows>0)
               printSubmitLink("GENBAWNOTIFICATIONCENTERFIRST: First", "firstCust");
            else
               printText("GENBAWNOTIFICATIONCENTERFIRST: First");

            printSpaces(5);
            
            if (skip_rows > 0)
               printSubmitLink("GENBAWNOTIFICATIONCENTERPRES: Previous", "prevCust");
            else
               printText("GENBAWNOTIFICATIONCENTERPRES: Previous");

            printSpaces(5);
            String rows = translate("GENBAWNOTIFICATIONCENTERROWS: (Row &1 to &2 total &3)",
                                    (skip_rows+1)+"",
                                    (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                    db_rows+"");
            printText( rows );
            printSpaces(5);

            if (skip_rows<db_rows-size)
               printSubmitLink("GENBAWNOTIFICATIONCENTERNEXT: Next", "nextCust");
            else
               printText("GENBAWNOTIFICATIONCENTERNEXT: Next");
            
            printSpaces(5);
            
            if (skip_rows<db_rows-size)
               printSubmitLink("GENBAWNOTIFICATIONCENTERLAST: Last", "lastCust");
            else
               printText("GENBAWNOTIFICATIONCENTERLAST: Last");

            printNewLine();
         }
      }
      else
      {
         // printCustomTable();
         printNewLine();
//         printNewLine();
         printEmptyTable();
         // printText("GENBAWNOTIFICATIONCENTERNODATA: No Notification Message Found.");
         // printNewLine();
      }
      printNewLine();
      
      appendDirtyJavaScript("function " + addProviderPrefix() + "refreshPortlet()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   submitPortlet('" + getId() + "');\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function " + addProviderPrefix() + "setNotificationNo( notification_no )\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   getPortletField('" + getId() + "','EXC_NOTIFICATION_NO').value = notification_no;\n");
      appendDirtyJavaScript("   submitPortlet('" + getId() + "');\n");
      appendDirtyJavaScript("}\n");
	}

	public static String getDescription()
	{
		return "GENBAWNOTIFICATIONCENTERDESC: Notification Center";
	}
	
	public boolean canCustomize()
   {
      return false;
   }

}
