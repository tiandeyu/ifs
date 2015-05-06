package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.*;

public class DocEdmControlVue extends DocSrv {

	// -----------------------------------------------------------------------------
	// ---------- Static constants ------------------------------------------------
	// -----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocEdmControlVue");
	
	private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPBlockLayout headlay;
	
   private ASPHTMLFormatter fmt;
	
   private String doc_class;
   private String doc_no;
   private String doc_sheet;
   private String doc_rev;
   private String doc_type;
   private String file_no;
	
	private String download_access;
	private String print_access;
	private String show_file_path;
	
	public DocEdmControlVue(ASPManager mgr, String page_path) {
		super(mgr, page_path);
	}
	
   public void run() throws FndException {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();

      doc_class = ctx.readValue("DOC_CLASS", mgr.readValue("DOC_CLASS"));
      doc_no = ctx.readValue("DOC_NO", mgr.readValue("DOC_NO"));
      doc_sheet = ctx.readValue("DOC_SHEET", mgr.readValue("DOC_SHEET"));
      doc_rev = ctx.readValue("DOC_REV", mgr.readValue("DOC_REV"));
      doc_type = ctx.readValue("DOC_TYPE", mgr.readValue("DOC_TYPE"));
      file_no = ctx.readValue("FILE_NO", mgr.readValue("FILE_NO"));
      
      doc_type = Str.isEmpty(doc_type) ? EdmMacro.DEFAULT_DOC_TYPE : doc_type;
      file_no = Str.isEmpty(file_no) ? "1" : file_no;

      download_access = ctx.readValue("DOWNLOAD_ACCESS");
      print_access = ctx.readValue("PRINT_ACCESS");
      
      if (mgr.dataTransfered())
         getTransferedData();

      if (Str.isEmpty(download_access) || Str.isEmpty(print_access))
         getDocAccess();
      
      okFind();
      
      adjust();
      
      if (!Str.isEmpty(doc_class))
         ctx.writeValue("DOC_CLASS", doc_class);
      if (!Str.isEmpty(doc_no))
         ctx.writeValue("DOC_NO", doc_no);
      if (!Str.isEmpty(doc_sheet))
         ctx.writeValue("DOC_SHEET", doc_sheet);
      if (!Str.isEmpty(doc_rev))
         ctx.writeValue("DOC_REV", doc_rev);
      if (!Str.isEmpty(doc_type))
         ctx.writeValue("DOC_TYPE", doc_type);
      if (!Str.isEmpty(file_no))
         ctx.writeValue("FILE_NO", file_no);
      
      ctx.writeValue("DOWNLOAD_ACCESS", download_access);
      ctx.writeValue("PRINT_ACCESS", print_access);
   }
	
   protected void getTransferedData() throws FndException
   {
      ASPManager mgr = getASPManager();

      ASPBuffer buf = mgr.getTransferedData();

      doc_class = buf.getValue("DOC_CLASS");
      doc_no = buf.getValue("DOC_NO");
      doc_sheet = buf.getValue("DOC_SHEET");
      doc_rev = buf.getValue("DOC_REV");
      doc_type = buf.getValue("DOC_TYPE");
      file_no = buf.getValue("FILE_NO");

      if (mgr.isEmpty(doc_type))
         doc_type = EdmMacro.DEFAULT_DOC_TYPE;
   }
   
   private void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      
      q.addWhereCondition("DOC_CLASS = ?");
      q.addWhereCondition("DOC_NO = ?");
      q.addWhereCondition("DOC_SHEET = ?");
      q.addWhereCondition("DOC_REV = ?");
      q.addWhereCondition("DOC_TYPE = ?");
      q.addParameter("DOC_CLASS", doc_class);
      q.addParameter("DOC_NO", doc_no);
      q.addParameter("DOC_SHEET", doc_sheet);
      q.addParameter("DOC_REV", doc_rev);
      q.addParameter("DOC_TYPE", doc_type);
      q.setOrderByClause("FILE_NO");
      
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
      if (headset.countRows() == 0)
         headset.clear();
   }
	
	private void getDocAccess()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		
		String user_id = mgr.getFndUser();
		
		ASPCommand cmd = trans.addCustomCommand("USERGETACCEXT", "Document_Issue_Access_API.User_Get_Access_Ext");
		cmd.addParameter("IN_STR1", doc_class);
		cmd.addParameter("IN_STR1", doc_no);
		cmd.addParameter("IN_STR1", doc_sheet);
		cmd.addParameter("IN_STR1", doc_rev);
		cmd.addParameter("IN_STR1", user_id);
		cmd.addParameter("OUT_1");
		cmd.addParameter("OUT_2");
		
		trans = mgr.perform(trans);

		download_access = (mgr.isEmpty(trans.getValue("USERGETACCEXT/DATA/OUT_1")) ? "FALSE" : trans.getValue("USERGETACCEXT/DATA/OUT_1"));
		print_access = (mgr.isEmpty(trans.getValue("USERGETACCEXT/DATA/OUT_2")) ? "FALSE" : trans.getValue("USERGETACCEXT/DATA/OUT_2"));
	}
	
	public void preDefine() throws FndException
	{
		ASPManager mgr = getASPManager();
		
		fmt = mgr.newASPHTMLFormatter();

		disableOptions();
		disableHomeIcon();
		disableNavigate();

		headblk = mgr.newASPBlock("FILE");
		
		headblk.addField("OBJID").
      setHidden();
      
      headblk.addField("OBJVERSION").
      setHidden();
      
      headblk.addField("DOC_CLASS").
      setHidden();
      
      headblk.addField("DOC_NO").
      setHidden();
      
      headblk.addField("DOC_SHEET").
      setHidden();
      
      headblk.addField("DOC_REV").
      setHidden();
      
      headblk.addField("DOC_TYPE").
      setHidden();
      
      headblk.addField("FILE_NO", "Number").
      setHidden();
      
      headblk.addField("FILE_NAME").
      setHidden();
      
      headblk.addField("PATH").
      setHidden();
      
      headblk.addField("REMOTE_FILE_PATH").
      setFunction("'Server:/' || PATH || FILE_NAME").
      setHidden();
      
      headblk.addField("USER_FILE_NAME").
      setHidden();
		
		headblk.addField("IN_STR1").
		setFunction("''").
		setHidden();
		
		headblk.addField("OUT_1").
		setFunction("''").
		setHidden();
		
		headblk.addField("OUT_2").
		setFunction("''").
		setHidden();
		
		disableApplicationSearch();
		
		headblk.setView("EDM_FILE");
		headblk.defineCommand("EDM_FILE_API", "");
		headblk.disableDocMan();
		headblk.disableHistory();
		headset = headblk.getASPRowSet();
		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommandGroupExtra(1);
		headbar.disableCommandGroupExtra(2);
		headbar.disableCommandGroupExtra(3);
		headbar.disableCommandGroupExtra(4);
		headbar.disableCommandGroupExtra(5);
		headbar.disableCommandGroupExtra(7);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.BACK);
		headbar.disableCommand(headbar.NOTES);
		headbar.disableCommand(headbar.PROPERTIES);
		headbar.disableMinimize();
		headbar.disableCmdShowFrameSpace();
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
	}
	
	protected void adjust()
	{
	   if ("FALSE".equals(download_access) || Str.isEmpty(download_access))
	   {
	      headbar.disableCommand(headbar.FILESAVE);
	      headbar.disableCommand(headbar.FILESAVEALL);
	   }
	   if ("FALSE".equals(print_access) || Str.isEmpty(print_access))
	   {
	      headbar.disableCommand(headbar.FILEPRINT);
         headbar.disableCommand(headbar.FILEPRINTALL);
	   }
	}
	
	// ===============================================================
	// HTML
	// ===============================================================

	protected String getDescription() 
	{
		return "DOCEDMCONTROLVUEDESC: Doc Edm Control - Vue";
	}

	protected String getTitle()
	{
		return getDescription();
	}
	
	protected String appendVuePresentation()
	{
		AutoString str = new AutoString();
		ASPManager mgr = getASPManager();
		
		String autovue_code_address = mgr.getConfigParameter("DOCMAW/AUTOVUE_CODE_ADDRESS");
      String autovue_code_port = mgr.getConfigParameter("DOCMAW/AUTOVUE_CODE_PORT", "80");
      String autovue_server_address = mgr.getConfigParameter("DOCMAW/AUTOVUE_SERVER_ADDRESS");
      String autovue_servlet_port = mgr.getConfigParameter("DOCMAW/AUTOVUE_SERVLET_PORT", "5098");
      String autovue_socket_port = mgr.getConfigParameter("DOCMAW/AUTOVUE_SOCKET_PORT", "5099");
      if (Str.isEmpty(autovue_code_address))
         return "";
      
      String autovue_code_http = "http://" + autovue_code_address;
      String autovue_http = "http://" + autovue_server_address;
      String autovue_socket = "socket://" + autovue_server_address;
      
      str.append("<CENTER>\n");
      str.append("<!-- BEGIN IFS Edm Control for Java Applet -->\n");
      str.append("  <APPLET ID=\"JVue\"\n");
      str.append("\n");
      str.append("    CODE=\"com.cimmetry.jvue.JVue.class\"\n");
      str.append("    CODEBASE=\"" + autovue_code_http + ":" + autovue_code_port +"/b2e/secured/docvuw\"\n");
      // str.append("    CODEBASE=\"http://ufifs-terry:8080/jVue\"\n");
      str.append("    ARCHIVE=\"jvue.jar,jogl.jar,gluegen-rt.jar\"\n");
      str.append("\n");
      str.append("    HSPACE=\"0\" VSPACE=\"0\"\n");
      str.append("    WIDTH=\"100%\" HEIGHT=\"96%\" MAYSCRIPT>\n");
      str.append("\n");
      str.append("    <PARAM NAME=\"EMBEDDED\"     VALUE=\"true\">\n");
      str.append("    <PARAM NAME=\"VERBOSE\"      VALUE=\"false\">\n");
      str.append("\n");
      str.append("    <!-- Optional: To call a Javascript function after IFS Edm Control has initialized -->\n");
      str.append("    <PARAM NAME=\"ONINIT\"       VALUE=\"onAppletInit();\">\n");
      str.append("    <PARAM NAME=\"GUIFILE\"      VALUE=\"viewonly.gui\">\n");
      str.append("    \n");
      str.append("    <!-- Try direct socket connection  and  servlet tunnelling -->\n");
      str.append("    <PARAM NAME=\"JVUESERVER\"   VALUE=\"" + autovue_http + ":" + autovue_servlet_port + "/servlet/VueServlet;" + autovue_socket + ":" + autovue_socket_port + "\">\n");
      if (!Str.isEmpty(show_file_path))
         str.append("    <PARAM NAME=\"FILENAME\"     VALUE=\"" + show_file_path + "\">\n");
      str.append("\n");
      str.append("    <p><b>" + mgr.translate("DOWMAWDOCEDMCONTROLVUEREQJAVA: Requires a browser that supports Java.") + "</b></p>\n");
      str.append("    <br/>");
      str.append("    <a href=/b2e/jre-7u45-windows-i586.exe>" + mgr.translate("DOWMAWDOCEDMCONTROLVUEDOWNLOADJRE: Download Java Runtime Enviroment.") + "</a>");
      str.append("\n");
      str.append("  </APPLET>\n");
      str.append("  <!-- END IFS Edm Control for Java Applet -->\n");
      str.append("</CENTER>\n");
      return str.toString();
	}
	
	protected String appendVueFunction()
   {
      AutoString str = new AutoString();
      ASPManager mgr = getASPManager();
      str.append("\n<script>\n");
      str.append("// Define document object.\n");
      str.append("function EdmFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no)\n");
      str.append("{\n");
      str.append("   this.doc_class = doc_class;\n");
      str.append("   this.doc_no = doc_no;\n");
      str.append("   this.doc_sheet = doc_sheet;\n");
      str.append("   this.doc_rev = doc_rev;\n");
      str.append("   this.doc_type = doc_type;\n");
      str.append("   this.file_no = file_no;\n");
      str.append("   this.getDocClass = function()\n");
      str.append("   {\n");
      str.append("      return this.doc_class;\n");
      str.append("   }\n");
      str.append("   this.getDocNo = function()\n");
      str.append("   {\n");
      str.append("      return this.doc_no;\n");
      str.append("   }\n");
      str.append("   this.getDocSheet = function()\n");
      str.append("   {\n");
      str.append("      return this.doc_sheet;\n");
      str.append("   }\n");
      str.append("   this.getDocRev = function()\n");
      str.append("   {\n");
      str.append("      return this.doc_rev;\n");
      str.append("   }\n");
      str.append("   this.getDocType = function()\n");
      str.append("   {\n");
      str.append("      return this.doc_type;\n");
      str.append("   }\n");
      str.append("   this.getFileNo = function()\n");
      str.append("   {\n");
      str.append("      return this.file_no;\n");
      str.append("   }\n");
      str.append("}\n");
      
      str.append("var __current_file_no = 0;\n");
      if (headset.countRows() > 0)
      {
         str.append("var __remote_file_path = new Array();\n");
         str.append("var __edm_file_obj = new Array();\n");
         for (int i = 0; i < headset.countRows(); i++)
         {
            str.append("__remote_file_path[" + i + "]='" + headset.getRow(i).getValue("REMOTE_FILE_PATH") + "'\n");
            String doc_class = headset.getRow(i).getValue("DOC_CLASS");
            String doc_no = headset.getRow(i).getValue("DOC_NO");
            String doc_sheet = headset.getRow(i).getValue("DOC_SHEET");
            String doc_rev = headset.getRow(i).getValue("DOC_REV");
            String doc_type = headset.getRow(i).getValue("DOC_TYPE");
            String file_no = headset.getRow(i).getValue("FILE_NO");
            str.append("var edm_file_obj = new EdmFile('" + doc_class + "','" + doc_no + "','" + doc_sheet + "','" + doc_rev + "','" + doc_type + "','" + file_no + "');\n");
            str.append("__edm_file_obj[" + i + "] = edm_file_obj;\n");
         }
         str.append("var __total_files = " + headset.countRows() + ";\n");
      }
      else
         str.append("var __total_files = 0;\n");
      
      str.append("var __cmd_file_first = document.getElementById(\"CMD_FILE_FIRST\").innerHTML;\n");
      str.append("var __cmd_file_pres = document.getElementById(\"CMD_FILE_PRES\").innerHTML;\n");
      str.append("var __cmd_file_next = document.getElementById(\"CMD_FILE_NEXT\").innerHTML;\n");
      str.append("var __cmd_file_last = document.getElementById(\"CMD_FILE_LAST\").innerHTML;\n");
      
      str.append("changeCmdVisibility(__current_file_no,false);\n");
      
      str.append("function onAppletInit()\n");
      str.append("{\n");
      str.append("   window.jVueLoaded = true;\n");
      str.append("   window.status = \"" + mgr.translate("DOCMAWDOCEDMCONTROLVUEAPPLETLOADED: IFS Document Edm Control Loaded.") + "\";\n");
      str.append("}\n");
      
      // Command bar functions
      str.append("function firstFile()\n");
      str.append("{\n");
      str.append("   if (window.jVueLoaded) {\n");
      str.append("      __current_file_no = 0;\n");
      str.append("      setFileWithNumber(__current_file_no,true);\n");
      str.append("   } else {\n");
      str.append("      alert(\"" + mgr.translate("DOCMAWDOCEDMCONTROLVUEAPPWAIT: Please wait for IFS Edm Control to finish loading.") + "\");\n");
      str.append("   }\n");
      str.append("}\n");
      
      str.append("function presFile()\n");
      str.append("{\n");
      str.append("   if (window.jVueLoaded) {\n");
      str.append("      if (__current_file_no >= 1)\n");
      str.append("         __current_file_no = __current_file_no - 1;\n");
      str.append("      else\n");
      str.append("         __current_file_no = 0;\n");
      str.append("      setFileWithNumber(__current_file_no,true);\n");
      str.append("   } else {\n");
      str.append("      alert(\"" + mgr.translate("DOCMAWDOCEDMCONTROLVUEAPPWAIT: Please wait for IFS Edm Control to finish loading.") + "\");\n");
      str.append("   }\n");
      str.append("}\n");
      
      str.append("function nextFile()\n");
      str.append("{\n");
      str.append("   if (window.jVueLoaded) {\n");
      str.append("      if (__current_file_no < __total_files - 1)\n");
      str.append("         __current_file_no = __current_file_no + 1;\n");
      str.append("      else\n");
      str.append("         __current_file_no = __total_files - 1;\n");
      str.append("      setFileWithNumber(__current_file_no,true);\n");
      str.append("   } else {\n");
      str.append("      alert(\"" + mgr.translate("DOCMAWDOCEDMCONTROLVUEAPPWAIT: Please wait for IFS Edm Control to finish loading.") + "\");\n");
      str.append("   }\n");
      str.append("}\n");
      
      str.append("function lastFile()\n");
      str.append("{\n");
      str.append("   if (window.jVueLoaded) {\n");
      str.append("      __current_file_no = __total_files - 1;\n");
      str.append("      setFileWithNumber(__current_file_no,true);\n");
      str.append("   } else {\n");
      str.append("      alert(\"" + mgr.translate("DOCMAWDOCEDMCONTROLVUEAPPWAIT: Please wait for IFS Edm Control to finish loading.") + "\");\n");
      str.append("   }\n");
      str.append("}\n");
      
      str.append("function changeCmdVisibility(fileNo,changeGoto)\n");
      str.append("{\n");
      str.append("   if (__total_files == 1)\n");
      str.append("   {\n");
      str.append("      // Only have one file\n");
      str.append("      document.getElementById(\"CMD_FILE_FIRST\").innerHTML = \"\";\n");
      str.append("      document.getElementById(\"CMD_FILE_PRES\").innerHTML = \"\";\n");
      str.append("      document.getElementById(\"CMD_FILE_NEXT\").innerHTML = \"\";\n");
      str.append("      document.getElementById(\"CMD_FILE_LAST\").innerHTML = \"\";\n");
      str.append("   }\n");
      str.append("   else if (fileNo == 0)\n");
      str.append("   {\n");
      str.append("      document.getElementById(\"CMD_FILE_FIRST\").innerHTML = \"\";\n");
      str.append("      document.getElementById(\"CMD_FILE_PRES\").innerHTML = \"\";\n");
      str.append("      document.getElementById(\"CMD_FILE_NEXT\").innerHTML = __cmd_file_next;\n");
      str.append("      document.getElementById(\"CMD_FILE_LAST\").innerHTML = __cmd_file_last;\n");
      str.append("   }\n");
      str.append("   else if (fileNo == __total_files - 1)\n");
      str.append("   {\n");
      str.append("      document.getElementById(\"CMD_FILE_FIRST\").innerHTML = __cmd_file_first;\n");
      str.append("      document.getElementById(\"CMD_FILE_PRES\").innerHTML = __cmd_file_pres;\n");
      str.append("      document.getElementById(\"CMD_FILE_NEXT\").innerHTML = \"\";\n");
      str.append("      document.getElementById(\"CMD_FILE_LAST\").innerHTML = \"\";\n");
      str.append("   }\n");
      str.append("   else\n");
      str.append("   {\n");
      str.append("      document.getElementById(\"CMD_FILE_FIRST\").innerHTML = __cmd_file_first;\n");
      str.append("      document.getElementById(\"CMD_FILE_PRES\").innerHTML = __cmd_file_pres;\n");
      str.append("      document.getElementById(\"CMD_FILE_NEXT\").innerHTML = __cmd_file_next;\n");
      str.append("      document.getElementById(\"CMD_FILE_LAST\").innerHTML = __cmd_file_last;\n");
      str.append("   }\n");
      str.append("   if (changeGoto)\n");
      str.append("      document.getElementById(\"CMD_FILE_GOTO\").options[fileNo].selected = true;\n");
      str.append("}\n");
      
      /*
      str.append("function setFile(fileURL)\n");
      str.append("{\n");
      str.append("   if (window.jVueLoaded) {\n");
      str.append("      // Load file on a seperate thread.\n");
      str.append("      window.document.applets[\"JVue\"].setFileThreaded(fileURL);\n");
      str.append("   } else {\n");
      str.append("      alert(\"" + mgr.translate("DOCMAWDOCEDMCONTROLVUEAPPWAIT: Please wait for IFS Edm Control to finish loading.") + "\");\n");
      str.append("   }\n");
      str.append("}\n");
      */
      str.append("function setFileWithNumber(fileNo,changeGoto)\n");
      str.append("{\n");
      str.append("   if (window.jVueLoaded) {\n");
      str.append("      // Load file on a seperate thread.\n");
      str.append("      var file_no_ = parseInt(fileNo);\n");
      str.append("      changeCmdVisibility(file_no_,changeGoto);\n");
      str.append("      window.document.applets[\"JVue\"].setFileThreaded(__remote_file_path[file_no_]);\n");
      str.append("      __current_file_no = file_no_;\n");
      str.append("   } else {\n");
      str.append("      alert(\"" + mgr.translate("DOCMAWDOCEDMCONTROLVUEAPPWAIT: Please wait for IFS Edm Control to finish loading.") + "\");\n");
      str.append("   }\n");
      str.append("}\n");
      
      str.append("function saveFile()\n");
      str.append("{\n");
      str.append("   showNewBrowser('");
      str.append(mgr.getApplicationPath(), "/docmaw/EdmMacro.page?PROCESS_DB=DOWNLOAD&SAME_ACTION_TO_ALL=YES&LAUNCH_FILE=NO&");
      str.append("DOC_CLASS='+__edm_file_obj[__current_file_no].doc_class+'&DOC_NO='+__edm_file_obj[__current_file_no].doc_no+'&");
      str.append("DOC_SHEET='+__edm_file_obj[__current_file_no].doc_sheet+'&DOC_REV='+__edm_file_obj[__current_file_no].doc_rev+'&");
      str.append("DOC_TYPE='+__edm_file_obj[__current_file_no].doc_type+'&FILE_NO='+__edm_file_obj[__current_file_no].file_no);\n");
      str.append("}\n");
      
      str.append("function saveAllFile()\n");
      str.append("{\n");
      str.append("   showNewBrowser('");
      str.append(mgr.getApplicationPath(), "/docmaw/EdmMacro.page?PROCESS_DB=DOWNLOAD&SAME_ACTION_TO_ALL=YES&LAUNCH_FILE=NO&");
      str.append("DOC_CLASS='+__edm_file_obj[__current_file_no].doc_class+'&DOC_NO='+__edm_file_obj[__current_file_no].doc_no+'&");
      str.append("DOC_SHEET='+__edm_file_obj[__current_file_no].doc_sheet+'&DOC_REV='+__edm_file_obj[__current_file_no].doc_rev+'&");
      str.append("DOC_TYPE='+__edm_file_obj[__current_file_no].doc_type);\n");
      str.append("}\n");
      
      str.append("function printFile()\n");
      str.append("{\n");
      str.append("   if (!window.jVueLoaded)\n");
      str.append("   {\n");
      str.append("      return;\n");
      str.append("   }\n");
      str.append("   var japplet = window.document.applets[\"JVue\"];\n");
      str.append("   if (japplet == null)\n");
      str.append("   {\n");
      str.append("      return;\n");
      str.append("   }\n");
      str.append("\n");
      str.append("   // Create a PrintProperties class\n");
      str.append("   var pPropsClass = japplet.getClass(\"com.cimmetry.common.PrintProperties\");\n");
      str.append("\n");
      str.append("   // Instantiate the object\n");
      str.append("   var pProps = pPropsClass.newInstance();\n");
      str.append("\n");
      str.append("   // Set headers and footers here\n");
      // str.append("   pProps.getHeaders().setTopLeftText(\"Left Header\");\n");
      // str.append("   pProps.getHeaders().setTopCenterText(\"Center Header\");\n");
      // str.append("   pProps.getHeaders().setTopRightText(\"Right Header\");\n");
      // str.append("   pProps.getHeaders().setBottomLeftText(\"Left Footer\");\n");
      // str.append("   pProps.getHeaders().setBottomCenterText(\"Center Footer\");\n");
      // str.append("   pProps.getHeaders().setBottomRightText(\"Right Footer\");\n");
      str.append("\n");
      str.append("   // Load default properties from the users preferences\n");
      str.append("   pProps.setProfile(japplet.getActiveVueBean().getProfile());\n");
      str.append("\n");
      str.append("   // Print the extents of the drawing (PrintOptions.AREA_EXTENTS==0)\n");
      str.append("   pProps.getOptions().setArea(0);\n");
      str.append("\n");
      str.append("   // Print all pages (PrintOptions.PAGES_ALL==0)\n");
      str.append("   pProps.getOptions().setPages(0);\n");
      str.append("\n");
      str.append("   // Set Highresolution by default\n");
      str.append("   pProps.getOptions().setHighResolution(true);\n");
      str.append("\n");
      str.append("   // Start print.\n");
      str.append("   //  Set second argument to false to display print options dialog,\n");
      str.append("   //  Set second argument to true  to not display print options dialog,\n");
      str.append("   japplet.printFile(pProps, false);\n");
      str.append("}\n");
      
      str.append("function printAllFiles()\n");
      str.append("{\n");
      str.append("   if (!window.jVueLoaded)\n");
      str.append("   {\n");
      str.append("      return;\n");
      str.append("   }\n");
      str.append("   var japplet = window.document.applets[\"JVue\"];\n");
      str.append("   if (japplet == null)\n");
      str.append("   {\n");
      str.append("      return;\n");
      str.append("   }\n");
      str.append("\n");
      str.append("   // Create a PrintProperties class\n");
      str.append("   var pPropsClass = japplet.getClass(\"com.cimmetry.common.PrintProperties\");\n");
      str.append("\n");
      str.append("   // Instantiate the object\n");
      str.append("   var pProps = pPropsClass.newInstance();\n");
      str.append("\n");
      str.append("   // Load default properties from the users preferences\n");
      str.append("   pProps.setProfile(japplet.getActiveVueBean().getProfile());\n");
      str.append("   // Print the extents of the drawing (PrintOptions.AREA_EXTENTS==0)\n");
      str.append("   pProps.getOptions().setArea(0);\n");
      str.append("   // Print all pages (PrintOptions.PAGES_ALL==0)\n");
      str.append("   pProps.getOptions().setPages(0);\n");
      str.append("   // Create a java.util.Vector class\n");
      str.append("   var vectorClass = japplet.getClass(\"java.util.Vector\");\n");
      str.append("   // Instantiate the file list object\n");
      str.append("   var fileList = vectorClass.newInstance();\n");
      str.append("   //Go through each file\n");
      str.append("   for (var i = 0; i < __total_files; i++)\n");
      str.append("      fileList.addElement(__remote_file_path[i]);\n");
      str.append("   // Start print.\n");
      str.append("   //  Set fourth argument to false to display print options dialog,\n");
      str.append("   //  Set fourth argument to true  to not display print options dialog,\n");
      str.append("   japplet.batchPrint(fileList, pProps, true, false);\n");
      str.append("}\n");
      
      str.append("</script>\n");
      return str.toString();
   }
	
	protected AutoString getContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		AutoString out = getOutputStream();
		ASPHTMLFormatter fmt = mgr.newASPHTMLFormatter();
		
		out.clear();
		
		out.append("<html>\n");
		out.append("<head>\n");
		out.append("<script language=\"javascript\">\n");
		out.append("var width=screen.availWidth;\n");
		out.append("var height=screen.availHeight;\n");
		out.append("self.resizeTo(width, height);\n");
		out.append("self.moveTo(0,0);\n");
		out.append("</script>\n");
		out.append("<style>\n");
		out.append("html,body{\n");
		out.append("    padding:0;\n");
		out.append("    margin:0;\n");
		out.append("}\n");
		out.append("</style>\n");
		out.append(mgr.generateHeadTag(getDescription()));
		out.append("</head>\n");
		out.append("<body oncontextmenu=self.event.returnValue=false onselectstart=\"return false\" scroll=\"no\">\n");
		out.append("<body>\n");
		out.append("<form ");
		out.append(mgr.generateFormTag());
		out.append(">\n");
		
		if (headset.countRows() > 0)
		{
		   String file_name = headset.getValue("FILE_NAME");
		   String path = headset.getValue("PATH");
		   if (!Str.isEmpty(file_name) && !Str.isEmpty(path))
		   {
		      if (!path.startsWith("/"))
		         path = path + "/";
		      
		      show_file_path = "Server:/" + path.replace("\\", "/") + file_name;
		   }
		}
		
		out.append(headbar.showBar());

		// Add IFS Edm Control need functions
      out.append(appendVueFunction());
      
      out.append(appendVuePresentation());
		
		out.append(mgr.generateClientScript());
		out.append("</form>\n");
		out.append("</body>\n");
		out.append("</html>\n");
		
		return out;
	}
}
