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
*  File        : dlgUserSettings.java
*  Converted   : Baka using ASP2JAVA Tool  2001-03-05
*  Created     : Using the ASP file dlgUserSettings.asp
*
*
*  Dikalk   2001-05-23   Call ID 65249 - changed old class id to the one used by the newer ocx
*  Dikalk   2003-04-02   Replaced calls to getOCXString() with DocmawUtil.getClientMgrObjectStr()
*  Dikalk   2003-04-21   Added a new field for specifying the briefcase root path
*  InoSlk   2003-08-14   Call ID 100767: Added method close(). Modified methods getContents() and run();
*  ThWilk   2003-08-19   Call ID 103379: Added method adjust() and eval2().Modified method close().
*  BaKalk   2003-11-04   Call ID 110044: Modified run() and getContents(), added getPreviousValue().
*  DiKalk   2004-17-03   Cleaned this page.
*  DiKalk   2004-17-03   Call ID 113253: Removed dependancy on DirectorySelect.page. Uses ocx's
*                        BrowseForFolder method to select directories.
*  Karalk   2005-15-11  call id 123090 corrected in getcontents().
*  KARALK   2005-15-27  bug id 50053  fixed in get content.
*  ASSALK   2007-08-15  Merged Bug 58526, Modified method drawLabel(). Added preDefine(). Removed translations where drawLabel()
*                      is calles in getContents().
*  BaKalk   2007-09-20  Call ID 148575: Modified getContents().
*  DULOLK   2008-01-17  Bug 69735, Modified getContents() and preDefine(). Added new Drop Down list for all available system fonts 
*                                  in client machine to be set for the Drop Area. Needed in cases where unicode is used.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.lang.reflect.*;



public class dlgUserSettings extends ASPPageProvider
{

   //
   // Static constants
   //
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.dlgUserSettings");


   private ASPContext ctx;
   private ASPHTMLFormatter fmt;
   private String param;


   // Instances created on page creation (immutable attributes)
   private ASPBlock   headblk;

   //
   // Construction
   //
   public dlgUserSettings(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      param        = null;
      super.doReset();
   }


   public ASPPoolElement clone(Object obj) throws FndException
   {
      dlgUserSettings page = (dlgUserSettings)(super.clone(obj));

      // Initializing mutable attributes
      page.param = null;

      // Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.fmt = page.getASPHTMLFormatter();
      return page;
   }


   public void run()
   {
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();

      if (!mgr.isEmpty(mgr.getQueryStringValue("PARAM")))
          param = mgr.getQueryStringValue("PARAM");

      adjust();
   }


   public void  preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("LOCAL_CHECKOUT_PATH").
      setLabel("DOCMAWDLGUSERSETTINGSCHECKOUTPATH: Local Checkout Path:").
      setHidden();

      headblk.addField("BC_ROOT_PATH").
      setLabel("DOCMAWDLGUSERSETTINGSBCROOTPATH: Briefcase Root Path:").
      setHidden();

      headblk.addField("EXT_VIEWER_APP").
      setLabel("DOCMAWDLGUSERSETTINGSEXTERNALVIEWERAPP: External Viewer Application:").
      setHidden();

      headblk.addField("EXT_REDLINE_APP").
      setLabel("DOCMAWDLGUSERSETTINGSEXTERNALREDLINEAPP: External Redline Application:").
      setHidden();

      //Bug Id 69735, Start
      headblk.addField("SEL_DA_FONT").
      setLabel("DOCMAWDLGUSERSETTINGSDROPAREAFONT: Client Manager Font:").
      setHidden();
      //Bug Id 69735, End

      headblk.addField("DEL_LOCAL_FILE").
      setLabel("DOCMAWDLGUSERSETTINGSLABELDELETEFILE: After Initial File Check-In, Delete Local File:").
      setHidden();

      headblk.addField("OVW_READONLY_FILES").
      setLabel("DOCMAWDLGUSERSETTINGSOVERWRITEREADONLYFILES: Overwrite Read-Only Files:").
      setHidden();

      headblk.setView("DUAL");
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      if ("DOCISSUE".equals(mgr.getQueryStringValue("PARAM"))) {
         disableHelp();
         disableNavigate();
         try{
             eval2("disableHomeIcon");
             eval2("disableOptions");
         }
         catch(NoSuchMethodException e){}
      }
   }


   private void eval2(String method) throws NoSuchMethodException
   {
       //This method was added since disabling the home icon was not supported by version 3.5.1
       Method m = getClass().getMethod(method, null);
       eval(method);
   }


   protected String getDescription()
   {
      return "DOCMAWDLGUSERSETTINGSTITLE: User Settings";
   }


   protected String getTitle()
   {
      return "DOCMAWDLGUSERSETTINGSTITLE: User Settings";
   }


   private String drawLabel(String label)
   {
      ASPManager mgr = getASPManager();
      String usage_id ="";
      int i = 0;
      
      if (!mgr.isEmpty(label))
         i = label.indexOf(":");
      if (i>0)
      { 
         String tr_constant = label.substring(0,i);         
         usage_id = mgr.getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
      }
         
      return "<span OnClick=\"showHelpTag('"+usage_id+"')\"><font class=\"pageSubTitle\">&nbsp;" + mgr.translate(label) + "</font></span>";
   }


   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      ASPManager mgr = getASPManager();

      out.append("<html>\n");
      out.append("<head  >\n");
      out.append(mgr.generateHeadTag("DOCMAWDLGUSERSETTINGSTITLE: User Settings"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append(DocmawUtil.getClientMgrObjectStr());
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("DOCMAWDLGUSERSETTINGSTITLE: User Settings"));

      out.append("<table border='0' >\n");
      out.append("   <tr>\n");
      out.append("      <td>\n");
      out.append("&nbsp;&nbsp;&nbsp");
      out.append("      </td>\n");
      out.append("      <td>\n");
      out.append("   <table border=0>\n");
      out.append("   <tr>\n");
      out.append("   <td colspan='2'>\n");
      out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDLGUSERSETTINGSLABELDIRECTORYAPP: Directories & Applications")));
      out.append("   </td>\n");
      out.append("   </tr>\n");
      out.append("   <tr>\n");
      out.append("      <td>\n");
      out.append("&nbsp;&nbsp;&nbsp");
      out.append("      </td>\n");
      out.append("      <td>\n");

      // Container for directories and applications..
      out.append("<table cols='6' border='0' >\n");
      out.append("   <tr height=50>\n");
      out.append("      <td colspan='1' rowspan='1' width='180'>\n");
      out.append(drawLabel("DOCMAWDLGUSERSETTINGSCHECKOUTPATH: Local Checkout Path:"));
      out.append("      </td>\n");
      out.append("      <td colspan='3' rowspan='1' width='500'>\n");
      out.append(fmt.drawTextField("LOCAL_CHECKOUT_PATH", "", "", 60, 120));
      out.append(fmt.drawButton("BTN_CHOOSE_PATH", mgr.translate("DOCMAWDLGUSERSETTINGSPATH:  Path... "),"OnClick=\"showFolderBrowser('LOCAL_CHECKOUT_PATH');\""));
      out.append("      </td>\n");
      out.append("   </tr>\n");
      
      // Added by Terry 20121019
      // Draw select download folder directory
      out.append("   <tr height=50>\n");
      out.append("      <td colspan='1' rowspan='1' width='180'>\n");
      out.append(drawLabel("DOCMAWDLGUSERSETTINGSDOWNROOTPATH: Document Download Path:"));
      out.append("      </td>\n");
      out.append("      <td colspan='3' rowspan='1' width='500'>\n");
      out.append(fmt.drawTextField("LOCAL_DOWNLOAD_PATH", "", "", 60, 120));
      out.append(fmt.drawButton("BTN_CHOOSE_PATH", mgr.translate("DOCMAWDLGUSERSETTINGSPATH:  Path... "),"OnClick=\"showFolderBrowser('LOCAL_DOWNLOAD_PATH');\""));
      out.append("      </td>\n");
      out.append("   </tr>\n");
      // Added end
      
      out.append("   <tr height=50>\n");
      out.append("      <td colspan='1' rowspan='1' width='180'>\n");
      out.append(drawLabel("DOCMAWDLGUSERSETTINGSBCROOTPATH: Briefcase Root Path:"));
      out.append("      </td>\n");
      out.append("      <td colspan='3' rowspan='1' width='500'>\n");
      out.append(fmt.drawTextField("BRIEFCASE_ROOT_PATH", "", "", 60, 120));
      out.append(fmt.drawButton("BTN_CHOOSE_PATH", mgr.translate("DOCMAWDLGUSERSETTINGSPATH:  Path... "),"OnClick=\"showFolderBrowser('BRIEFCASE_ROOT_PATH');\""));
      out.append("      </td>\n");
      out.append("   </tr>\n");
      
      
      out.append("   <tr height=50>\n");
      out.append("      <td colspan='1' rowspan='1' width='180'>\n");
      out.append(drawLabel("DOCMAWDLGUSERSETTINGSEXTERNALVIEWERAPP: External Viewer Application:"));
      out.append("      </td>\n");
      out.append("      <td colspan='3' rowspan='1' width='500'>\n");
      out.append(fmt.drawTextField("EXTERNAL_VIEWER_APPLICATION", "", "", 60, 120));
      out.append(fmt.drawButton("BTN_CHOOSE_FILE", mgr.translate("DOCMAWDLGUSERSETTINGSFILE: Browse..."),"OnClick=\"showOpenFileDialog('EXTERNAL_VIEWER_APPLICATION');\""));
      out.append("      </td>\n");
      out.append("   </tr>\n");
      
      
      out.append("   <tr height=50>\n");
      out.append("      <td colspan='1' rowspan='1' width='180'>\n");
      out.append(drawLabel("DOCMAWDLGUSERSETTINGSEXTERNALREDLINEAPP: External Redline Application:"));
      out.append("      </td>\n");
      out.append("      <td colspan='3' rowspan='1'>\n");
      out.append(fmt.drawTextField("EXTERNAL_REDLINE_APPLICATION", "", "", 60, 120));
      out.append(fmt.drawButton("BTN_CHOOSE_FILE", mgr.translate("DOCMAWDLGUSERSETTINGSFILE: Browse..."),"OnClick=\"showOpenFileDialog('EXTERNAL_REDLINE_APPLICATION');\""));
      out.append("      </td>\n");
      out.append("   </tr>\n");
      out.append("</table>\n");

      out.append("      </td>\n");
      out.append("   </tr>\n");
      out.append("   <tr>\n");
      out.append("      <td colspan='2'>\n");
      out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDLGUSERSETTINGSLABELOPTIONS: Options")));
      out.append("      </td>\n");
      out.append("   </tr>\n");
      out.append("   <tr>\n");
      out.append("      <td>\n");
      out.append("&nbsp;&nbsp;&nbsp");
      out.append("      </td>\n");
      out.append("      <td>\n");

      // Container for other options..
      out.append("<table border='0' >\n");
      //Bug Id 69735, Start
      out.append("   <tr height=50>\n");
      out.append("      <td colspan='1' rowspan='1' width='180'>\n");
      out.append(drawLabel("DOCMAWDLGUSERSETTINGSDROPAREAFONT: Client Manager Font:"));
      out.append("      </td>\n");
      out.append("      <td>\n");
      out.append(fmt.drawSelect("SELECT_DA_FONT", null, "", "", false));      
      out.append("      </td>\n");
      out.append("   </tr>\n");
      //Bug Id 69735, End
      out.append("   <tr height=50>\n");
      out.append("      <td colspan='1' rowspan='1' width='180'>\n");
      out.append(drawLabel("DOCMAWDLGUSERSETTINGSLABELDELETEFILE: After Initial File Check-In, Delete Local File:"));
      out.append("      </td>\n");
      out.append("      <td>\n");
      out.append(fmt.drawCheckbox("DELETE_LOCAL_FILES", "", false, ""));
      out.append("      </td>\n");
      out.append("   </tr>\n");
      out.append("   <tr height=50>\n");
      out.append("      <td colspan='1' rowspan='1' width='180'>\n");
      out.append(drawLabel("DOCMAWDLGUSERSETTINGSOVERWRITEREADONLYFILES: Overwrite Read-Only Files:"));
      out.append("      </td>\n");
      out.append("      <td>\n");
      out.append(fmt.drawCheckbox("OVERWRITE_READONLY", "", false, ""));
      out.append("      </td>\n");
      out.append("   </tr>\n");
      out.append("</table>\n");

      // Container for control buttons..
      out.append("<table border=0 cols=3>\n");
      out.append("   <tr height=50 >\n");
      out.append("      <td colspan='1' rowspan='1' width='185'>\n");
      out.append("      </td>\n");
      out.append("      <td width='100'>\n");
      out.append(fmt.drawButton("OK", mgr.translate("DOCMAWDLGUSERSETTINGSOK:    Ok   "), "OnClick=\"__ok();\""));
      out.append("      </td>\n");
      out.append("      <td>\n");
      out.append(fmt.drawButton("CANCEL", mgr.translate("DOCMAWDLGUSERSETTINGSCANCEL: Cancel"), "OnClick=\"cancel();\""));
      out.append("      </td>\n");
      out.append("   </tr>\n");
      out.append("</table>\n");

      out.append("      </td>\n");
      out.append("   </tr>\n");
      out.append("   </table>\n");
      out.append("   </td>\n");
      out.append("   </tr>\n");
      out.append("</table>\n");


      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");


      appendDirtyJavaScript("loadValues();\n");

      //Bug Id 69735, Start
      appendDirtyJavaScript("var OrigIndex;\n");
      appendDirtyJavaScript("function popFontList(selectedFont, fontList)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    var fontArray;\n");
      appendDirtyJavaScript("    var x;\n");
      appendDirtyJavaScript("    var regIndex;\n");
      appendDirtyJavaScript("    var defaultIndex;\n");      
      appendDirtyJavaScript("    fontArray = fontList.split(\"^\");\n");
      appendDirtyJavaScript("    fontArray.sort();\n");
      appendDirtyJavaScript("    regIndex = -1;\n");
      appendDirtyJavaScript("    for (x in fontArray) \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       document.form.SELECT_DA_FONT.options[x] = new Option(fontArray[x],fontArray[x]);\n");      
      appendDirtyJavaScript("       if (fontArray[x]==\"MS Sans Serif\")\n");
      appendDirtyJavaScript("          defaultIndex = x;\n");
      appendDirtyJavaScript("       if (fontArray[x]==selectedFont)\n");
      appendDirtyJavaScript("          regIndex = x;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if (regIndex==-1)\n");
      appendDirtyJavaScript("       OrigIndex = defaultIndex;\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("       OrigIndex = regIndex;\n");
      appendDirtyJavaScript("    document.form.SELECT_DA_FONT.selectedIndex = OrigIndex;\n");
      appendDirtyJavaScript("}\n");
      //Bug Id 69735, End

      appendDirtyJavaScript("function loadValues()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    popFontList(oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"IFSDropAreaFont\"), oCliMgr.GetFontList());\n");  //Bug Id 69735
      appendDirtyJavaScript("    document.form.LOCAL_CHECKOUT_PATH.value    = oCliMgr.GetDocumentFolder();\n");
      // Added by Terry 20121019
      // Load download path from reg.
      appendDirtyJavaScript("    document.form.LOCAL_DOWNLOAD_PATH.value  = oCliMgr.GetLocalDownloadPath();\n");
      // Added end
      appendDirtyJavaScript("    document.form.BRIEFCASE_ROOT_PATH.value  = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\");\n");
      appendDirtyJavaScript("    document.form.EXTERNAL_VIEWER_APPLICATION.value  = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"ExternalViewerApplication\");\n");
      appendDirtyJavaScript("    document.form.EXTERNAL_REDLINE_APPLICATION.value = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"RedlineViewerApplication\");\n");
      appendDirtyJavaScript("    document.form.DELETE_LOCAL_FILES.value = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"DeleteLocalFiles\");\n");
      appendDirtyJavaScript("    document.form.OVERWRITE_READONLY.value = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"OverwriteReadonlyFiles\");\n");
      appendDirtyJavaScript("    if (document.form.DELETE_LOCAL_FILES.value == \"ON\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       document.form.DELETE_LOCAL_FILES.checked = true;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if (document.form.OVERWRITE_READONLY.value == \"ON\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       document.form.OVERWRITE_READONLY.checked = true;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function showFolderBrowser(field_name)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var path;\n");
      appendDirtyJavaScript("   if (field_name == \"LOCAL_CHECKOUT_PATH\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       path = oCliMgr.BrowseForFolder(\"" + mgr.translate("DOCMAWDLGUSERSETTINGSCHOOSELOCALCHECKOUTPATH: Select local checkout path:") +"\");\n");
      appendDirtyJavaScript("       if (path != \"\") document.form.LOCAL_CHECKOUT_PATH.value = path;\n");
      appendDirtyJavaScript("   }\n");
      // Added by Terry 20121019
      // Show choose download folder selector, and save selection in field.
      appendDirtyJavaScript("   else if (field_name == \"LOCAL_DOWNLOAD_PATH\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       path = oCliMgr.BrowseForFolder(\"" + mgr.translate("DOCMAWDLGUSERSETTINGSCHOOSELOCALDOWNLOADPATH: Select local download path:") +"\");\n");
      appendDirtyJavaScript("       if (path != \"\") document.form.LOCAL_DOWNLOAD_PATH.value = path;\n");
      appendDirtyJavaScript("   }\n");
      // Added end
      appendDirtyJavaScript("   else if (field_name == \"BRIEFCASE_ROOT_PATH\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       path = oCliMgr.BrowseForFolder(\"" + mgr.translate("DOCMAWDLGUSERSETTINGSCHOOSEBRIEFCASEROOTPATH: Select briefcase root path:") +"\");\n");
      appendDirtyJavaScript("       if (path != \"\") document.form.BRIEFCASE_ROOT_PATH.value = path;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function showOpenFileDialog(field_name)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var file;\n");
      appendDirtyJavaScript("   var file_filter = \"" + mgr.translate("DOCMAWDLGUSERSETTINGSEXECUTABLE: EXE Files") + "^EXE" + "\";\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       file = oCliMgr.OpenFileDialog(file_filter, \"C:\\\\\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if (field_name == \"EXTERNAL_VIEWER_APPLICATION\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (file != \"\") document.form.EXTERNAL_VIEWER_APPLICATION.value = file;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else if (field_name == \"EXTERNAL_REDLINE_APPLICATION\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (file != \"\") document.form.EXTERNAL_REDLINE_APPLICATION.value = file;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("var path_is_valid = true;\n");
      appendDirtyJavaScript("function __ok()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    saveUserSettings();\n");
      appendDirtyJavaScript("    if (path_is_valid)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       returnToCallingPage();\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    path_is_valid = true ;\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function cancel()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    returnToCallingPage();\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function saveUserSettings()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("      if ((document.form.LOCAL_CHECKOUT_PATH.value != '') && (!oCliMgr.FolderExists(document.form.LOCAL_CHECKOUT_PATH.value)))\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         alert(\"" + mgr.translate("DOCMAWUSERSTINGCHKPATHINVALID: The local checkout path is either invalid or it does not exist. Please select a valid path.") + "\");\n");
      appendDirtyJavaScript("         path_is_valid = false ;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else \n");
      appendDirtyJavaScript("         oCliMgr.SetDocumentFolder(document.form.LOCAL_CHECKOUT_PATH.value);\n");

      // Added by Terry 20121019
      // Check and save local download path in reg.
      appendDirtyJavaScript("   if (path_is_valid)\n");
      appendDirtyJavaScript("   {     \n");
      appendDirtyJavaScript("      if ((document.form.LOCAL_DOWNLOAD_PATH.value != '') && (!oCliMgr.FolderExists(document.form.LOCAL_DOWNLOAD_PATH.value)))\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         alert(\"" + mgr.translate("DOCMAWUSERSTINGLOCALDOWNLOADPATHVALID: The Local download path is either invalid or it does not exist. Please select a valid path.") + "\");\n");
      appendDirtyJavaScript("         path_is_valid = false;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else \n");
      appendDirtyJavaScript("         oCliMgr.SetLocalDownloadPath(document.form.LOCAL_DOWNLOAD_PATH.value);\n");
      appendDirtyJavaScript("   }     \n");
      // Added end
      
      appendDirtyJavaScript("   if (path_is_valid)\n");
      appendDirtyJavaScript("   {     \n");
      appendDirtyJavaScript("      if ((document.form.BRIEFCASE_ROOT_PATH.value != '') && (!oCliMgr.FolderExists(document.form.BRIEFCASE_ROOT_PATH.value)))\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         alert(\"" + mgr.translate("DOCMAWUSERSTINGBRIEFCASEROOTPATHVALID: The Briefcase root path is either invalid or it does not exist. Please select a valid path.") + "\");\n");
      appendDirtyJavaScript("         path_is_valid = false ;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else \n");
      appendDirtyJavaScript("          oCliMgr.RegSetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\", document.form.BRIEFCASE_ROOT_PATH.value);\n");
      appendDirtyJavaScript("   }     \n");

      appendDirtyJavaScript("   if (path_is_valid)\n");
      appendDirtyJavaScript("   {     \n");
      appendDirtyJavaScript("      if ((document.form.EXTERNAL_VIEWER_APPLICATION.value != '') && (!oCliMgr.FileExists(document.form.EXTERNAL_VIEWER_APPLICATION.value)))\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         alert(\"" + mgr.translate("DOCMAWUSERSTINGEXTVIEWAPPPATHVALID: The External Viewer application does not exist. Please select a valid application.") + "\");\n");
      appendDirtyJavaScript("         path_is_valid = false ;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else \n");
      appendDirtyJavaScript("          oCliMgr.RegSetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"ExternalViewerApplication\", document.form.EXTERNAL_VIEWER_APPLICATION.value);\n");
      appendDirtyJavaScript("   }     \n");

      appendDirtyJavaScript("   if (path_is_valid)\n");
      appendDirtyJavaScript("   {     \n");
      appendDirtyJavaScript("      if ((document.form.EXTERNAL_REDLINE_APPLICATION.value != '') && (!oCliMgr.FileExists(document.form.EXTERNAL_REDLINE_APPLICATION.value)))\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         alert(\"" + mgr.translate("DOCMAWUSERSTINGEXTREDAPPPATHVALID: The External Redline application does not exist. Please select a valid application.") + "\");\n");
      appendDirtyJavaScript("         path_is_valid = false ;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else \n");
      appendDirtyJavaScript("          oCliMgr.RegSetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"RedlineViewerApplication\", document.form.EXTERNAL_REDLINE_APPLICATION.value);\n");
      appendDirtyJavaScript("   }     \n");

      appendDirtyJavaScript("   oCliMgr.RegSetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"IFSDropAreaFont\", document.form.SELECT_DA_FONT.options[document.form.SELECT_DA_FONT.selectedIndex].value);\n");   //Bug Id 69735

      appendDirtyJavaScript("   oCliMgr.RegSetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"DeleteLocalFiles\", document.form.DELETE_LOCAL_FILES.checked ? \"ON\" : \"OFF\");\n");
      appendDirtyJavaScript("   oCliMgr.RegSetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"OverwriteReadonlyFiles\", document.form.OVERWRITE_READONLY.checked ? \"ON\" : \"OFF\");\n");
      appendDirtyJavaScript("}\n");



      appendDirtyJavaScript("function returnToCallingPage()\n");
      appendDirtyJavaScript("{\n");
      if (mgr.isEmpty(param))
      {
         //Bug Id 69735, Start - Should go to Navigator         
         appendDirtyJavaScript("   window.location = \"" + mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "Navigator.page?MAINMENU=Y&NEW=Y" + "\";\n");
         //Bug Id 69735, End
      }
      else
      {
         //Bug Id 69735, Start - Should refresh only if user changed font. Other changes don't affect doc issue.
         appendDirtyJavaScript("  if (document.form.SELECT_DA_FONT.selectedIndex != OrigIndex)\n");
         appendDirtyJavaScript("       eval(\"opener.refreshParentRowsSelected()\");\n");
         //Bug Id 69735, End
         appendDirtyJavaScript("  window.close();\n");
      }
      appendDirtyJavaScript("}\n");

      return out;
   }
}
