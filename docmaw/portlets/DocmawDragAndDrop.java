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
 * File        : DocmawDragAndDrop.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 *  Date       Sign      Descripiton
 *  ----       ----      -----------
 *  051213    CHODLK     Created [Ver 1.0.0] from DocmawDragAndDrop.java of stattnet [originally created by nisilk].
 *  060103    DIKALK     Made changes to UI. Added customizable option for importing a single file using the Create 
 *                       New Document Wizard
 *  070504    BAKALK     Merged Bug Id 58113.
 *  070813    NaLrlk     XSS Correction.
 *  090804    SHTHLK     Bug Id 83898, Added portal id for the dropArea and HTML fields control
 * ----------------------------------------------------------------------------
 */


package ifs.docmaw.portlets;


import ifs.fnd.asp.*;
import ifs.docmaw.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;


public class DocmawDragAndDrop extends ASPPortletProvider
{

	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.portlets.DocmawDragAndDrop");


	private ASPContext ctx;


	private transient String title;   
   private transient String pre_title;
	private transient ASPTransactionBuffer trans;
	private transient String my_profile_info;
   private transient boolean use_create_new_wiz;



	public DocmawDragAndDrop( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
	}


   public ASPPage construct() throws FndException
   {
      // Nothing special to do here..
      return super.construct();
   }


	public static String getDescription()
	{
		return "DOCMAWDNDPORTLETHANDLEDOCS: Import Documents";
	}


	public static int getMinWidth()
	{
		return 144;
	}


	public String getTitle( int mode )
	{
		if ( mode==MINIMIZED || mode==MAXIMIZED )
			return title;
		else
			return translate("DOCMAWDNDPORTLETCUSTOMIZE: Customize Import Documents");
	}


	protected void preDefine() throws FndException
	{
		ctx = getASPContext();
		title = translate(getDescription());
		init();
	}


	protected void init()
	{
      use_create_new_wiz = readProfileFlag("USE_CREATE_NEW_WIZ", false);
	}


	protected void run()
	{
		trans = getASPManager().newASPTransactionBuffer();
		ASPManager mgr = getASPManager();

 
		if ("YES".equals(mgr.readValue("DRAG_AND_DROP_"+getId())))//Bug Id 83898
		{
         importFiles();
		}
	}


   private void importFiles()
   {
      ASPManager mgr = getASPManager();

      String fileNames = (mgr.readValue("FILES_DROPPED_"+getId())); //Bug Id 83898
      fileNames = fileNames.replaceAll("\\\\", "\\\\\\\\");

      if (use_create_new_wiz && fileNames.indexOf("|") == -1)
      {
         mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "docmaw\\CreateNewDocument.page?FILE_NAME=" + mgr.URLEncode(fileNames) + "&REFERRER=" + mgr.URLEncode(mgr.getProtocol() + "://" + mgr.getApplicationDomain() + mgr.getASPConfig().getApplicationContext()));
      }
      else
      {         
         ASPBuffer fileBuffer = mgr.newASPBuffer();
         fileBuffer.addItem("FILE_NAMES", fileNames);         
         mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "docmaw\\FileImport.page?FROM_OTHER=YES", fileBuffer);
      }
   }


	public void printContents()
	{
		ASPManager mgr = getASPManager();
		//Bug Id 83898, Start
		appendToHTML("  <input type=\"hidden\" NAME=\"DRAG_AND_DROP_"+getId()+"\" value=\"\">\n");
		appendToHTML("  <input type=\"hidden\" NAME=\"PATH_"+getId()+"\" value=\"\">\n");
		appendToHTML("  <input type=\"hidden\" NAME=\"FILES_DROPPED_"+getId()+"\" value=\"\">\n");
		//Bug Id 83898, End

      if (mgr.isExplorer())
      {
         //Bug Id 58113, Start
         String unicode_msg = mgr.translate("DOCMAWDOCREFERENCEUNICODECHARS: The Drag and Drop Area does not support adding files with Unicode characters and any such file will be excluded. Do you want to continue?");
         //Bug Id 83898, Start - Added portal id to DropArea 
         appendToHTML("<script type=\"text/JavaScript\" for=\"DropArea_"+getId()+"\" event=\"DocmanDragDrop()\">\n");
         appendToHTML("function document.form.DropArea_"+getId()+"::OLEDragDrop(Data){\n");
         appendToHTML("     var filesDropped = \"\";\n");
         appendToHTML("     var path = \"\";\n");
         appendToHTML("     var fullFileName;\n");
         appendToHTML("     var foundUnicodeFiles = false;\n");
         appendToHTML("     if(Data.GetFormat(15)){\n");
         appendToHTML("         var e = new Enumerator(Data.Files);\n");
         appendToHTML("         while(!e.atEnd()){\n");
         appendToHTML("             fullFileName =  \"\" + e.item(); \n");
         appendToHTML("             if (fullFileName.indexOf(\"?\") != -1)\n");
         appendToHTML("                 foundUnicodeFiles = true;\n");
         appendToHTML("             else\n");
         appendToHTML("                 filesDropped += fullFileName + \"|\"; \n");
         appendToHTML("             e.moveNext();\n");
         appendToHTML("         }\n");
         appendToHTML("         filesDropped = filesDropped.substr(0,filesDropped.length-1);\n"); //remove last '|'
         appendToHTML("         document.form.DropArea_"+getId()+".Backcolor = oldBackColor;\n");
         appendToHTML("         if (foundUnicodeFiles)\n");
         appendToHTML("         {\n");
         appendToHTML("             if (!confirm(\"" + mgr.encodeStringForJavascript(unicode_msg) + "\"))\n");
         appendToHTML("                 return;\n");
         appendToHTML("         }\n");
         appendToHTML("         if (filesDropped != \"\")\n");
         appendToHTML("         {\n");
         appendToHTML("             document.form.DRAG_AND_DROP_"+getId()+".value =\"YES\";\n");   
         appendToHTML("             document.form.FILES_DROPPED_"+getId()+".value =filesDropped;\n");   
         appendToHTML("             submitPortlet('" + getId() + "');\n");
         appendToHTML("         }\n");
         appendToHTML("     }\n");
         appendToHTML("}\n");
          //Bug Id 83898, End
         appendToHTML(DocmawUtil.writeOleDragOverFunction(mgr.translate("DOCMAWDNDPORTLETDROPOBJECTDROPHERE: Drag and drop files to start importing them into Document Management"),
                                                          mgr.translate("DOCMAWDNDPORTLETDROPAREAACCEPTEDMSG: Files to drop"),
                                                          mgr.translate("DOCMAWDNDPORTLETDROPAREAILLEGALMSG: Only files are accepted"),getId()));//Bug Id 83898                                                                 
         appendToHTML("</script>  \n");

         appendToHTML("<table width=100% height=80>"); 
         appendToHTML("  <tr>");
         appendToHTML("    <td align=\"center\">");
         appendToHTML(DocmawUtil.drawDnDArea("borders", mgr.getApplicationPath() + "/docmaw/",getId() ));//Bug Id 83898
         appendToHTML("    </td>\n");
         appendToHTML("    <td width=1></td>\n");
         appendToHTML("  </tr>");
         appendToHTML("</table >");
         //Bug Id 58113, End
      }
      else
      {
         printText("DOCMAWDNDPORTLETNONIEMESSAGE: This portlet supports Internet Explorer only");
      }
	}       


	public boolean canCustomize()
	{
		return true;
	}


	public void runCustom()
	{
		trans = getASPManager().newASPTransactionBuffer();
	}


	public void printCustomBody() throws FndException
	{
      ASPManager mgr = getASPManager();

      printNewLine();
      printText(mgr.translate("DOCMAWDNDPORTLETCONFIGTITLE: Configure default portlet behaviour:"));
      printNewLine();
   
      beginTable(null,true,false);
         beginTableBody();
            beginTableCell();
               printCheckBox("USE_CREATE_NEW_WIZ", use_create_new_wiz);
               printText("For single file, use Create New Document Wizard");
            endTableCell();
         endTableBody();
      endTable();
	}


	public void submitCustomization()
	{
      use_create_new_wiz = "TRUE".equals(readValue("USE_CREATE_NEW_WIZ"));
      writeProfileFlag("USE_CREATE_NEW_WIZ", use_create_new_wiz);
	}

}
