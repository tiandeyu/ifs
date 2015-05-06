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
*  File        : CreateLink.java
*      KARALK   2006-12-26   Created
*      JANSLK   2007-03-22   Call 140541. Added java script function enableDisableRadioButtons and changed the title.
*                            Added label "Copy or save the link below:".
*      BAKALK   2007-05-23   Call Id: 141286, Change some labels and translation tags.
*      BAKALK   2007-06-01   Call Id: 145676, Made many changes.
*      BAKALK   2007-06-19   Call Id: 145676, Made some more changes.
*      BAKALK   2007-06-26   Call Id: 145676, Made some more changes.
*      BAKALK   2007-06-26   Call Id: 144778, Changed process_db for edit.
*      ILSOLK   20070808     XSS Correction.
* -------------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*   071213      ILSOLK   Bug Id 68773, Eliminated XSS.
*   080310      SHTHLK   Bug Id 71792, Modified setValueCheckIn(), setValueDocDetails() & setValueEdit() to 
*   080310               enable disable the radio buttons properly and to generate the correct hyperlink
* -----------------------------------------------------------------------
*/
package ifs.docmaw; 

import ifs.docmaw.edm.*;
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class CreateLink extends ASPPageProvider
{
        //===============================================================
	// Static constants
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.CreateLink");

	//===============================================================
	// Instances created on page creation (immutable attributes)
	//===============================================================

	private ASPBlock  dummyblk;

	//===============================================================
	// Transient temporary variables (never cloned)
	//===============================================================
	private DocumentTransferHandler     doc_hdlr;
	private ASPTransactionBuffer trans;
	private ASPHTMLFormatter fmt;
	private ASPContext       ctx;
	private ASPCommand cmd;

	private ASPBlock                    fileblk;
	private ASPBlockLayout              filelay;
	private ASPCommandBar               filebar;
	private ASPRowSet                   fileset;
	private boolean bView;
	private boolean bEdit;
	private boolean bCheckIn;
	private boolean bDetails;
	private boolean bOriginal;
	private boolean bViewCopy;
	private boolean bLatestRev;
	private boolean bLatestRelease;
	private boolean bUseCurrentRev = true;

	private String DocClass;
	private String DocNo;
	private String DocSheet;
	private String DocRev;
   private String SelectedDocRev;
   private String latestRevision;
   private String latestRevisionReleased;
	private String DocTitle;


	private String sView;
	private String sEdit;

	private String sCheckIn;
	private String sDocDetails;
	private String Soperation;

	

	public CreateLink(ASPManager mgr, String page_path)
	{
		super(mgr, page_path);
	}

	public void run() throws FndException
	{
	    ASPManager mgr = getASPManager();
	    fmt   = mgr.newASPHTMLFormatter();
	    ctx     = mgr.getASPContext();
	    bView = true;
            bOriginal = true;


       DocClass      = ctx.readValue("DOC_CLASS","");
       DocNo      	= ctx.readValue("DOC_NO",   "");
       DocSheet      = ctx.readValue("DOC_SHEET","");
       DocRev        = ctx.readValue("DOC_REV",  "");
       DocTitle      = ctx.readValue("TITLE",    "");

       latestRevision         = ctx.readValue("LATESTREVISION","");
       latestRevisionReleased = ctx.readValue("LATESTREVISIONRELEASED","");
	    
	    if (mgr.dataTransfered()) {
          ASPBuffer buf = mgr.getTransferedData();
          DocClass      = buf.getValue("DATA/DOC_CLASS");
          DocNo      	= buf.getValue("DATA/DOC_NO");
          DocSheet      = buf.getValue("DATA/DOC_SHEET");
          DocRev        = buf.getValue("DATA/DOC_REV");
          DocTitle      = buf.getValue("DATA/TITLE");
          latestRevision =  this.getLatestRevision();
          latestRevisionReleased = this.getReleasedRevision();
          
       }
          

	    

      if (!mgr.isEmpty(mgr.readValue("SELECTED_REV")))
      {
         if ("LATEST_REV".equals(mgr.readValue("SELECTED_REV"))) {

            SelectedDocRev = this.latestRevision;
            bLatestRev = true;
            bUseCurrentRev = false;

         }
         else if ("LATEST_RELEASED".equals(mgr.readValue("SELECTED_REV"))) {
            SelectedDocRev = this.latestRevisionReleased;
            bLatestRelease = true;
            bUseCurrentRev = false;
         }
         else if ("THIS_REV".equals(mgr.readValue("SELECTED_REV"))) {
            SelectedDocRev = DocRev;
         }

      }


      if (!mgr.isEmpty(mgr.readValue("SELECTED_TYPE")))
      {
         if ("ORIGINAL".equals(mgr.readValue("SELECTED_TYPE"))) {
            bOriginal = true;//&&
            bViewCopy = false;

         }
         else if ("VIEW".equals(mgr.readValue("SELECTED_TYPE"))) {
            bOriginal = false;//&&
            bViewCopy = true;
         }
      }

      //Bug Id 71792, Start
      if (!mgr.isEmpty(mgr.readValue("SELECTED_OPERATION")))
      {
	  if ("VIEW".equals(mgr.readValue("SELECTED_OPERATION"))) {
            bView = true;
            bEdit = false;
	    bCheckIn = false;
	    bDetails = false;

         }
         else if ("EDIT".equals(mgr.readValue("SELECTED_OPERATION"))) {
            bView = false;
            bEdit = true;
	    bCheckIn = false;
	    bDetails = false;
         }
         else if ("CHECKIN".equals(mgr.readValue("SELECTED_OPERATION"))) {
            bView = false;
            bEdit = false;
	    bCheckIn = true;
	    bDetails = false;
         }
         else if ("DOCDETAILS".equals(mgr.readValue("SELECTED_OPERATION"))) {
            bView = false;
            bEdit = false;
	    bCheckIn = false;
	    bDetails = true;
         }

      }
      //Bug Id 71792, End
      
      ctx.writeValue("DOC_CLASS", DocClass);
	   ctx.writeValue("DOC_NO",    DocNo);
	   ctx.writeValue("DOC_SHEET", DocSheet);
	   ctx.writeValue("DOC_REV",   DocRev);

      ctx.writeValue("LATESTREVISION",   latestRevision);
      ctx.writeValue("LATESTREVISIONRELEASED",   latestRevisionReleased);

	}

	public void preDefine() throws FndException
	{
	    ASPManager mgr = getASPManager();
	    

	    disableOptions();
	    disableHomeIcon();
	    disableNavigate();
	    disableHelp();

	    dummyblk = mgr.newASPBlock("DUMMY");

	    dummyblk.addField("DUMMY1");
	    dummyblk.addField("DUMMY2");
	    dummyblk.addField("DUMMY3");

	    super.preDefine();
	}
      
	protected String getDescription()
	{
	    return "DOCMAWCREATELINK: Create link for document";
	}

	protected String getTitle()
	{
	    return "DOCMAWCREATELINK: Create link for document";

	}

	protected String getLatestRevision()
  {
	    ASPManager mgr = getASPManager();
	    trans   = mgr.newASPTransactionBuffer();
	    
	    trans.clear();
	    
	    cmd = trans.addCustomFunction( "LATESTREV", "DOC_ISSUE_API.LATEST_REVISION", "DUMMY3" );
	    cmd.addParameter("DUMMY1",DocClass);
	    cmd.addParameter("DUMMY1", DocNo);
	    cmd.addParameter("DUMMY1",DocSheet);
	    trans = mgr.perform(trans);
	    String lRev = trans.getValue("LATESTREV/DATA/DUMMY3");
       return lRev;
   }

	protected String getReleasedRevision()
	{
	    ASPManager mgr = getASPManager();
	    trans   = mgr.newASPTransactionBuffer();
	    trans.clear();
	    cmd = trans.addCustomFunction( "RELEASEDREV", "DOC_ISSUE_API.Get_Released_Revision", "DUMMY3" );
	    cmd.addParameter("DUMMY1",DocClass);
	    cmd.addParameter("DUMMY1", DocNo);
	    cmd.addParameter("DUMMY1",DocSheet);
	    trans = mgr.perform(trans);
	    String rRev = trans.getValue("RELEASEDREV/DATA/DUMMY3");
	    if (!mgr.isEmpty(rRev)) 
		return rRev;
	    else
		return DocRev;

	}

	

	protected void printContents() throws FndException
	{

	
	  ASPManager mgr = getASPManager();
	  
	  sView = mgr.translate("LVIEW: View");
	  sEdit = mgr.translate("LEDIT: Edit");
	  sCheckIn = mgr.translate("LCHECKIN: Check In");
	  sDocDetails = mgr.translate("LDOCDETAILS: Document Details");

	  appendToHTML("<input type=\"hidden\" name=\"OPERATION\" value=\"VIEW\">");
     appendToHTML("<input type=\"hidden\" name=\"SELECTED_REV\" value=\"\">\n");
     appendToHTML("<input type=\"hidden\" name=\"SELECTED_TYPE\" value=\"\">\n");
     appendToHTML("<input type=\"hidden\" name=\"SELECTED_OPERATION\" value=\"\">\n"); //Bug Id 71792
          
	  appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"top\" height=\"10\" style=\"LEFT: 40px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
	  appendToHTML("       <td >"+fmt.drawWriteLabel(DocClass + " - " +DocNo + " - " + DocSheet + " - " + DocRev)+"</td>\n"); // XSS_Safe ILSOLK 20070808
	  appendToHTML("   </tr>\n");
	  appendToHTML(" </table>\n");
    
    
	  appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"10\" style=\"LEFT: 40px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
	  appendToHTML("       <td >"+fmt.drawWriteLabel("OPERATIONS: Operations:")+"</td>\n");
	  appendToHTML("   </tr>\n");
	  appendToHTML(" </table>\n");
    
	  appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"40\"></td>");  
     appendToHTML("       <td >"+fmt.drawRadio(mgr.translate("VIEW: View"),"VIEW_CHECK","ON",bView,"onClick=setValueView()")+ "</td>\n");
	  appendToHTML("   </tr>\n");
	  
	  appendToHTML("	<TR align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"40\"></td>");  
     appendToHTML("       <td >"+fmt.drawRadio(mgr.translate("EDIT: Edit"),"EDIT_CHECK","ON",bEdit,"onClick=setValueEdit()")+ "</td>\n");
	  appendToHTML("   </tr>\n");
    
	  appendToHTML("	<TR align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"40\"></td>");  
     appendToHTML("       <td >"+fmt.drawRadio(mgr.translate("CHECKIN: Check In"),"CHECKIN_CHECK","ON",bCheckIn,"onClick=setValueCheckIn()")+ "</td>\n");
	  appendToHTML("   </tr>\n");
    
	  appendToHTML("	<TR align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
     appendToHTML("       <td >"+fmt.drawRadio(mgr.translate("DOCUMENTDETAILS: Document Details"),"DOCDETAILS_CHECK","ON",bDetails,"onClick=setValueDocDetails()")+ "</td>\n");
	  appendToHTML("   </tr>\n");
    
    
	  appendToHTML(" </table>\n");
	  appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 20px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
	  appendToHTML("       <td >"+fmt.drawWriteLabel("DOCUMENTTYPE: Document Type:")+"</td>\n");
	  appendToHTML("   </tr>\n");
	  appendToHTML(" </table>\n");
    
	  appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"40\"></td>");  
     appendToHTML("       <td >"+fmt.drawRadio(mgr.translate("ORIGINAL: Original"),"ORIGINAL_CHECK","ON",bOriginal,"onClick=setValueOriginal()")+ "</td>\n");
	  appendToHTML("   </tr>\n");
    
	  appendToHTML("	<TR align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
     appendToHTML("       <td >"+fmt.drawRadio(mgr.translate("VIEWCOPY: View Copy"),"VIEWCOPY_CHECK","ON",bViewCopy,"onClick=setValueViewCopy()")+ "</td>\n");
	  appendToHTML("   </tr>\n");
    
	  appendToHTML(" </table>\n");
    
	  appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 20px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
	  appendToHTML("       <td >"+fmt.drawWriteLabel("REVISIONS: Revisions:")+"</td>\n");
	  appendToHTML("   </tr>\n");
	  appendToHTML(" </table>\n");
    
	  appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"40\"></td>");  
     appendToHTML("       <td >"+fmt.drawRadio(mgr.translate("LATESTREVISION: Latest Revision"),"LATESTREV_CHECK","ON",bLatestRev,"onClick=setValueLatestRev()")+ "</td>\n");
	  appendToHTML("   </tr>\n");
    
	  appendToHTML("	<TR align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
     appendToHTML("       <td >"+fmt.drawRadio(mgr.translate("LATESTRELEASED: Latest Released"),"LATESTRELEASED_CHECK","ON",bLatestRelease,"onClick=setValueLatestReleased()")+ "</td>\n");
	  appendToHTML("   </tr>\n");
    
	  appendToHTML("	<TR align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
     appendToHTML("       <td >"+fmt.drawRadio(mgr.translate("USETHISREVISION: Use This Revision"),"USECURRENTREV_CHECK","ON",bUseCurrentRev,"onClick=setValueUseCurrentRev()")+ "</td>\n");
	  appendToHTML("   </tr>\n");
	  appendToHTML("   </table>\n");
	  
	  appendToHTML(" </table>\n");
	  appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 20px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
	  appendToHTML("       <td >"+fmt.drawWriteLabel("COPYSAVELINK: Copy or save the link below:")+"</td>\n");
	  appendToHTML("   </tr>\n");
	  appendToHTML(" </table>\n");
	  
     appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"40\" style=\"LEFT: 80px; \">\n");
	  appendToHTML("       <td width=\"50\"></td>");  
     this.SelectedDocRev = mgr.isEmpty(SelectedDocRev)?DocRev:SelectedDocRev;
	  //appendToHTML("       <td ><a class=hyperLink href= \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\" id=myLink> "+ mgr.translate("DOCLINK: Document  ")+ mgr.HTMLEncode(DocTitle)+ " ( " + mgr.HTMLEncode(DocClass) + " - " +mgr.HTMLEncode(DocNo) + " - " + mgr.HTMLEncode(DocSheet) + " - " + mgr.HTMLEncode(DocRev)+ ")"+"</a></td>\n");

     String viewType = this.bViewCopy?"VIEW":"ORIGINAL";
     if (this.bLatestRelease) {
	 appendToHTML("       <td ><a class=hyperLink href= \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE="+viewType+"&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\" id=myLink> "+ mgr.translate("DOCLINK: Document  ")+ mgr.HTMLEncode(DocTitle)+ " ( " + mgr.HTMLEncode(DocClass) + " - " +mgr.HTMLEncode(DocNo) + " - " + mgr.HTMLEncode(DocSheet) + " - "+ mgr.translate("DOCMAWCREATELINKLATESTRELEASED: Latest Released")+  ")</a></td>\n");
      }
      else if (this.bLatestRev) {
	  appendToHTML("       <td ><a class=hyperLink href= \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE="+viewType+"&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\" id=myLink> "+ mgr.translate("DOCLINK: Document  ")+ mgr.HTMLEncode(DocTitle)+ " ( " + mgr.HTMLEncode(DocClass) + " - " +mgr.HTMLEncode(DocNo) + " - " + mgr.HTMLEncode(DocSheet) + " - " + mgr.translate("DOCMAWCREATELINKLATESTREVISION: Latest Revision")+ ")</a></td>\n");
       }
       else
	  appendToHTML("       <td ><a class=hyperLink href= \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE="+viewType+"&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+SelectedDocRev+"\" id=myLink> "+ mgr.translate("DOCLINK: Document  ")+ mgr.HTMLEncode(DocTitle)+ " ( " + mgr.HTMLEncode(DocClass) + " - " +mgr.HTMLEncode(DocNo) + " - " + mgr.HTMLEncode(DocSheet) + " - " + SelectedDocRev +")</a></td>\n");
     

	  appendToHTML("   </tr>\n");
	  appendToHTML(" </table>\n");
    
	  appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
	  appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"40\" style=\"LEFT: 20px; \">\n");
	  appendToHTML("       <td width=\"30\"></td>");  
	  appendToHTML("       <td width=\"20\"></td>");  
	  appendToHTML("       <td>"+fmt.drawButton("CLOSE",mgr.translate("DOCMAWNEWCREATELINKCLOSE: Close"),"OnClick=Close()")+"</td>\n");
	  appendToHTML("       <td width=\"140\"></td>");  
	  appendToHTML("   </tr>\n");
	  appendToHTML("   </table>\n");
	  
	            		

	  appendToHTML("   <script LANGUAGE=\"JavaScript\">\n");
	  //Bug Id 71792, Start
	  if (!this.bView) 
	  {
	     appendToHTML("   document.form.LATESTREV_CHECK.disabled = true;\n");
	     appendToHTML("   document.form.LATESTRELEASED_CHECK.disabled = true;\n");
	     appendToHTML("   document.form.USECURRENTREV_CHECK.disabled = true;\n");
	     appendToHTML("   document.form.ORIGINAL_CHECK.checked=true;\n");
	     appendToHTML("   document.form.VIEWCOPY_CHECK.checked=false;\n");
	     appendToHTML("   document.form.VIEWCOPY_CHECK.disabled=true;\n");
	     appendToHTML("   var link = document.getElementById(\"myLink\"); \n");
	     if (this.bEdit)
		 appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	     if (this.bCheckIn) 
		 appendToHTML("               link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	     if (this.bDetails) 
	     {
		  appendToHTML("            link.href = \"DocIssue.page?&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
		  appendToHTML("            document.form.ORIGINAL_CHECK.disabled = true;\n"); 
		  appendToHTML("            document.form.ORIGINAL_CHECK.checked = false;\n");
	     }
	  }
	  //Bug Id 71792, End
	  appendToHTML("      function setValueView()\n");
	  appendToHTML("      { \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("         var link = document.getElementById(\"myLink\"); \n");
	  //Bug Id 71792, Start
	  appendToHTML("       if ((document.form.ORIGINAL_CHECK.checked!=true) && (document.form.VIEWCOPY_CHECK.checked!=true))\n");
	  appendToHTML("       { \n");
	  appendToHTML("            document.form.ORIGINAL_CHECK.checked = true;\n");
	  appendToHTML("            document.form.ORIGINAL_CHECK.disabled = false;\n");
	  appendToHTML("       } \n");
	  //Bug Id 71792, End
	  appendToHTML("         if (document.form.VIEW_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.EDIT_CHECK.checked = false;\n");
	  appendToHTML("            document.form.CHECKIN_CHECK.checked = false;\n");
	  appendToHTML("            document.form.DOCDETAILS_CHECK.checked = false;\n");
     appendToHTML("            if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("         	{ \n");
     appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("              	    else if (document.form.LATESTRELEASED_CHECK.checked==true) \n");
	  appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("            	    else \n");
	  appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else \n");
     appendToHTML("         	{ \n");
	  appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("              	    else if (document.form.LATESTRELEASED_CHECK.checked==true) \n");
	  appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n"); //Bug Id 68773
	  appendToHTML("            	    else \n");
	  appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("         } \n");
	  appendToHTML("         else \n");
	  appendToHTML("            link.href = \"\"; \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("      }\n");

	  appendToHTML("      function setValueEdit()\n");
	  appendToHTML("      { \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("         var link = document.getElementById(\"myLink\"); \n");
	  appendToHTML("         if (document.form.EDIT_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.VIEW_CHECK.checked = false;\n");
	  appendToHTML("            document.form.CHECKIN_CHECK.checked = false;\n");
	  appendToHTML("            document.form.DOCDETAILS_CHECK.checked = false;\n");
	  appendToHTML("            document.form.ORIGINAL_CHECK.checked=true;\n");
	  appendToHTML("            document.form.VIEWCOPY_CHECK.checked=false;\n");
	  appendToHTML("            document.form.VIEWCOPY_CHECK.hide=true;\n");
	  appendToHTML("            if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("         	{ \n");
	  //Bug Id 71792, Start
	  appendToHTML("                     document.form.SELECTED_REV.value=\"THIS_REV\";\n");
	  appendToHTML("                     setDocType();\n");
	  appendToHTML("                     setOperation();\n");
	  appendToHTML("                     submit();\n");
	  //Bug Id 71792, End
	  appendToHTML("         	} \n");
	  appendToHTML("            else \n");
     appendToHTML("         	{ \n");
     appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("              	    else if (document.form.LATESTRELEASED_CHECK.checked==true) \n");
	  appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("            	    else \n");
	  appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");

	  appendToHTML("         } \n");
	  appendToHTML("         else \n");
	  appendToHTML("            link.href = \"\"; \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("      }\n");
    
	  appendToHTML("      function setValueCheckIn()\n");
	  appendToHTML("      { \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("         var link = document.getElementById(\"myLink\"); \n");
	  appendToHTML("         if (document.form.CHECKIN_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.ORIGINAL_CHECK.checked=true;\n");
	  appendToHTML("            document.form.VIEWCOPY_CHECK.checked=false;\n");
	  appendToHTML("            document.form.VIEW_CHECK.checked = false;\n");
	  appendToHTML("            document.form.EDIT_CHECK.checked = false;\n");
	  appendToHTML("            document.form.DOCDETAILS_CHECK.checked = false;\n");
     appendToHTML("            if (document.form.ORIGINAL_CHECK.checked==true)\n");
     appendToHTML("         	{ \n");
	  //Bug Id 71792, Start
	  appendToHTML("                     document.form.SELECTED_REV.value=\"THIS_REV\";\n");
	  appendToHTML("                     setDocType();\n");
	  appendToHTML("                     setOperation();\n");
	  appendToHTML("                     submit();\n");
	  //Bug Id 71792, End
	  appendToHTML("         	} \n");
	  appendToHTML("            else \n");
	  appendToHTML("               link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	{ \n");
	  appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("              	    else if (document.form.LATESTRELEASED_CHECK.checked==true) \n");
	  appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("            	    else \n");
	  appendToHTML("               		link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("         } \n");
	  appendToHTML("         else \n");
	  appendToHTML("            link.href = \"\"; \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("      }\n");
    
	  appendToHTML("      function setValueDocDetails()\n");
	  appendToHTML("      { \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("         var link = document.getElementById(\"myLink\"); \n");
	  appendToHTML("         if (document.form.DOCDETAILS_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.VIEW_CHECK.checked = false;\n");
	  appendToHTML("            document.form.EDIT_CHECK.checked = false;\n");
	  appendToHTML("            document.form.CHECKIN_CHECK.checked = false;\n");
	  //Bug Id 71792, Start
	  appendToHTML("            document.form.SELECTED_REV.value=\"THIS_REV\";\n");
	  appendToHTML("            setDocType();\n");
	  appendToHTML("            setOperation();\n");
	  appendToHTML("            submit();\n");
	  //Bug Id 71792, End
	  appendToHTML("         } \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("      }\n");
    
	  appendToHTML("      function setValueOriginal()\n");
	  appendToHTML("      { \n");
	  appendToHTML("         var link = document.getElementById(\"myLink\"); \n");
	  appendToHTML("         if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.VIEWCOPY_CHECK.checked = false;\n");
	  appendToHTML("            if (document.form.VIEW_CHECK.checked==true)\n");
	  appendToHTML("         	{ \n");
	  appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("            	    else if (document.form.LATESTRELEASED_CHECK.checked==true)\n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	    else \n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.EDIT_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("            	    else if (document.form.LATESTRELEASED_CHECK.checked==true)\n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	    else \n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.CHECKIN_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("            	    else if (document.form.LATESTRELEASED_CHECK.checked==true)\n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	    else \n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else \n");
	  appendToHTML("            link.href = \"DocIssue.page?&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         } \n");
	  appendToHTML("         else \n");
	  appendToHTML("            link.href = \"\"; \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("      }\n");
    
	  appendToHTML("      function setValueViewCopy()\n");
	  appendToHTML("      { \n");
	  appendToHTML("         var link = document.getElementById(\"myLink\"); \n");
	  appendToHTML("         if (document.form.VIEWCOPY_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.ORIGINAL_CHECK.checked = false;\n");
	  appendToHTML("            if (document.form.VIEW_CHECK.checked==true)\n");
	  appendToHTML("         	{ \n");
	  appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("            	    else if (document.form.LATESTRELEASED_CHECK.checked==true)\n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	    else \n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.EDIT_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("            	    else if (document.form.LATESTRELEASED_CHECK.checked==true)\n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	    else \n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.CHECKIN_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("            	    if (document.form.LATESTREV_CHECK.checked==true)\n");
     appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("            	    else if (document.form.LATESTRELEASED_CHECK.checked==true)\n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	    else \n");
	  appendToHTML("                        link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else \n");
	  appendToHTML("            link.href = \"DocIssue.page?&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         } \n");
	  appendToHTML("         else \n");
	  appendToHTML("            link.href = \"\"; \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
	  appendToHTML("      }\n");
	  //------------------- Revisions -----------------
	  appendToHTML("      function setValueLatestRev()\n");
	  appendToHTML("      { \n");
	  appendToHTML("         var link = document.getElementById(\"myLink\"); \n");
	  appendToHTML("         if (document.form.LATESTREV_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.LATESTRELEASED_CHECK.checked = false;\n");
	  appendToHTML("            document.form.USECURRENTREV_CHECK.checked = false;\n");
	  appendToHTML("            if (document.form.VIEW_CHECK.checked==true)\n");
	  appendToHTML("         	{ \n");
	  appendToHTML("         		if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("         	    	else \n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.EDIT_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("         		if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("         	    	else \n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.CHECKIN_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("         		if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
	  appendToHTML("         	    	else \n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&LATEST_REV=TRUE\"; \n");
     appendToHTML("         	} \n");
	  appendToHTML("            else \n");
	  appendToHTML("            link.href = \"DocIssue.page?&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         } \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
     appendToHTML("document.form.SELECTED_REV.value=\"LATEST_REV\";\n");
     appendToHTML("setDocType();\n");
     appendToHTML("submit();\n");
	  appendToHTML("      }\n");
    
	  appendToHTML("      function setValueLatestReleased()\n");
	  appendToHTML("      { \n");
	  appendToHTML("         var link = document.getElementById(\"myLink\"); \n");
	  appendToHTML("         if (document.form.LATESTRELEASED_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.LATESTREV_CHECK.checked = false;\n");
	  appendToHTML("            document.form.USECURRENTREV_CHECK.checked = false;\n");
	  appendToHTML("            if (document.form.VIEW_CHECK.checked==true)\n");
	  appendToHTML("         	{ \n");
	  appendToHTML("         		if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	    	else \n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.EDIT_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("         		if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	    	else \n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.CHECKIN_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("         		if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
	  appendToHTML("         	    	else \n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&RELEASED=TRUE\"; \n");
     appendToHTML("         	} \n");
	  appendToHTML("            else \n");
	  appendToHTML("            link.href = \"DocIssue.page?&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         } \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
     appendToHTML("document.form.SELECTED_REV.value=\"LATEST_RELEASED\";\n");
     appendToHTML("setDocType();\n");
     appendToHTML("submit();\n");
	  appendToHTML("      }\n");
    
	  appendToHTML("      function setValueUseCurrentRev()\n");
	  appendToHTML("      { \n");
	  appendToHTML("         if (document.form.USECURRENTREV_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.LATESTREV_CHECK.checked = false;\n");
	  appendToHTML("            document.form.LATESTRELEASED_CHECK.checked = false;\n");
	  appendToHTML("            if (document.form.VIEW_CHECK.checked==true)\n");
	  appendToHTML("         	{ \n");
	  appendToHTML("         		if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	    	else \n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.EDIT_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("         		if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	    	else \n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKOUT&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	} \n");
	  appendToHTML("            else if (document.form.CHECKIN_CHECK.checked==true) \n");
     appendToHTML("         	{ \n");
	  appendToHTML("         		if (document.form.ORIGINAL_CHECK.checked==true)\n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=ORIGINAL&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");
	  appendToHTML("         	    	else \n");
	  appendToHTML("                           link.href = \"EdmMacro.page?PROCESS_DB=CHECKIN&DOC_TYPE=VIEW&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");//Bug Id 68773
     appendToHTML("         	} \n");
	  appendToHTML("            else \n");
	  appendToHTML("            link.href = \"DocIssue.page?&DOC_CLASS="+mgr.HTMLEncode(DocClass)+"&DOC_NO="+mgr.HTMLEncode(DocNo)+"&DOC_SHEET="+mgr.HTMLEncode(DocSheet)+"&DOC_REV="+mgr.HTMLEncode(DocRev)+"\"; \n");//Bug Id 68773
	  appendToHTML("         } \n");
     appendToHTML("         enableDisableRadioButtons(); \n");
     appendToHTML("document.form.SELECTED_REV.value=\"THIS_REV\";\n");
     appendToHTML("setDocType();\n");
     appendToHTML("submit();\n");
     appendToHTML("      }\n");


	  appendToHTML("      function enableDisableRadioButtons()\n");
	  appendToHTML("      { \n");
	  appendToHTML("         if (document.form.EDIT_CHECK.checked==true \n");
	  appendToHTML("                || document.form.CHECKIN_CHECK.checked==true\n");
	  appendToHTML("                    || document.form.DOCDETAILS_CHECK.checked==true)\n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.VIEWCOPY_CHECK.disabled = true;\n");
     appendToHTML("            document.form.LATESTREV_CHECK.disabled = true;\n");
     appendToHTML("            document.form.LATESTRELEASED_CHECK.disabled = true;\n");
     appendToHTML("            document.form.USECURRENTREV_CHECK.disabled = true;\n");
	  appendToHTML("            if (document.form.DOCDETAILS_CHECK.checked==true )\n");
	  appendToHTML("            { \n");
	  appendToHTML("                document.form.ORIGINAL_CHECK.disabled = true;\n"); 
     appendToHTML("                document.form.ORIGINAL_CHECK.checked = false;\n");
     appendToHTML("                document.form.VIEWCOPY_CHECK.checked = false;\n");
	  appendToHTML("            } \n");
     appendToHTML("            else \n");
	  appendToHTML("                document.form.ORIGINAL_CHECK.disabled = false;\n");
     appendToHTML("            document.form.LATESTREV_CHECK.checked = false;\n");
     appendToHTML("            document.form.LATESTRELEASED_CHECK.checked = false;\n");
     appendToHTML("            document.form.USECURRENTREV_CHECK.checked = true;\n");
     appendToHTML("         } \n");
     appendToHTML("         else \n");
	  appendToHTML("         { \n");
	  appendToHTML("            document.form.VIEWCOPY_CHECK.disabled = false;\n");
	  appendToHTML("            document.form.LATESTREV_CHECK.disabled = false;\n");
	  appendToHTML("            document.form.LATESTRELEASED_CHECK.disabled = false;\n");
	  appendToHTML("            document.form.USECURRENTREV_CHECK.disabled = false;\n");
	  appendToHTML("         } \n");
	  appendToHTML("      }\n");

     appendToHTML("      function Close()\n");
	  appendToHTML("      { \n");
	  appendToHTML("        window.close();\n");
	  appendToHTML("      }\n");

     appendToHTML("function setDocType()\n");
     appendToHTML("{ \n");
     appendToHTML("   if (document.form.ORIGINAL_CHECK.checked==true)\n");
     appendToHTML("      document.form.SELECTED_TYPE.value=\"ORIGINAL\";\n");
     appendToHTML("   if (document.form.VIEWCOPY_CHECK.checked==true)\n");
     appendToHTML("      document.form.SELECTED_TYPE.value=\"VIEW\";\n");
     appendToHTML("}\n");
     
     //Bug Id 71792, Start
     appendToHTML("function setOperation()\n");
     appendToHTML("{ \n");
     appendToHTML("   if (document.form.VIEW_CHECK.checked==true)\n");
     appendToHTML("      document.form.SELECTED_OPERATION.value=\"VIEW\";\n");
     appendToHTML("   if (document.form.EDIT_CHECK.checked==true)\n");
     appendToHTML("      document.form.SELECTED_OPERATION.value=\"EDIT\";\n");
     appendToHTML("   if (document.form.CHECKIN_CHECK.checked==true)\n");
     appendToHTML("      document.form.SELECTED_OPERATION.value=\"CHECKIN\";\n");
     appendToHTML("   if (document.form.DOCDETAILS_CHECK.checked==true)\n");
     appendToHTML("      document.form.SELECTED_OPERATION.value=\"DOCDETAILS\";\n");
     appendToHTML("}\n");
     //Bug Id 71792, End
	  appendToHTML("   </script>\n");

     
     
	}


}
