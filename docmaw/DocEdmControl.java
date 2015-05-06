package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.*;

public class DocEdmControl extends DocSrv {

	// -----------------------------------------------------------------------------
	// ---------- Static constants ------------------------------------------------
	// -----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocEdmControl");
	
	protected ASPBlock fileblk;
	
	protected ASPContext ctx;
	
	protected ASPHTMLFormatter fmt;
	
	protected String doc_class;
	protected String doc_no;
	protected String doc_sheet;
	protected String doc_rev;
	protected String doc_type;
	protected String file_no;
	
	protected String location_address;
	protected String location_user;
	protected String location_password;
	protected String location_type;
	protected String location_port;
	protected String remote_path;
	protected String user_file_name;
	protected String user_access;
	
	protected String remote_doc_name;
	protected String download_access;
	protected String print_access;
	protected String autovue_page_url = "";
	protected String record_hist_url;
	
	public DocEdmControl(ASPManager mgr, String page_path) {
		super(mgr, page_path);
	}
	
   public void run() throws FndException {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();

      doc_class = "";
      doc_no = "";
      doc_sheet = "";
      doc_rev = "";
      doc_type = "";
      file_no = "";

      user_access = "$ACS1:";

      record_hist_url = mgr.getProtocol() + "://" + mgr.getApplicationDomain()
            + mgr.getASPConfig().getApplicationContext() + "/DocDownLoadServer";

      if (mgr.dataTransfered())
         getTransferedData();
      else if (!mgr.isEmpty(mgr.readValue("DOC_CLASS"))
            && !mgr.isEmpty(mgr.readValue("DOC_NO"))
            && !mgr.isEmpty(mgr.readValue("DOC_SHEET"))
            && !mgr.isEmpty(mgr.readValue("DOC_REV")))
         getDocInfoTransfer();

      getDocEdmRepository();
      getDocAccess();
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
	
	protected void getDocInfoTransfer()
	{
		ASPManager mgr = getASPManager();
		
		doc_class = mgr.readValue("DOC_CLASS");
		doc_no = mgr.readValue("DOC_NO");
		doc_sheet = mgr.readValue("DOC_SHEET");
		doc_rev = mgr.readValue("DOC_REV");
		doc_type = mgr.readValue("DOC_TYPE");
		file_no = mgr.readValue("FILE_NO");

		if (mgr.isEmpty(doc_type))
			doc_type = EdmMacro.DEFAULT_DOC_TYPE;
	}
	
	protected void getDocEdmRepository() throws FndException
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		String edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
		location_address = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
		location_user = crypt(getStringAttribute(edm_rep_info, "LOCATION_USER"), "yang");
		location_password = crypt(decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD")), "yang");
		location_type = "2".equals(getStringAttribute(edm_rep_info, "LOCATION_TYPE")) ? "ftp" : "";
		location_port = getStringAttribute(edm_rep_info, "LOCATION_PORT");
		
		ASPCommand cmd = trans.addCustomCommand("EDMFILENAMES", "Edm_File_API.Get_File_Names_");
		cmd.addParameter("IN_STR1", doc_class);
		cmd.addParameter("IN_STR1", doc_no);
		cmd.addParameter("IN_STR1", doc_sheet);
		cmd.addParameter("IN_STR1", doc_rev);
		cmd.addParameter("IN_STR1", doc_type);
		cmd.addParameter("OUT_1", "S", "OUT", "");
		cmd.addParameter("OUT_2", "S", "OUT", "");
		
		trans = perform(trans);
		remote_path = trans.getValue("EDMFILENAMES/DATA/OUT_1");
		user_file_name = trans.getValue("EDMFILENAMES/DATA/OUT_2");
	}
	
	protected void getDocAccess()
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

		String downloadAccess = (mgr.isEmpty(trans.getValue("USERGETACCEXT/DATA/OUT_1")) ? "FALSE" : trans.getValue("USERGETACCEXT/DATA/OUT_1"));
		String printAccess = (mgr.isEmpty(trans.getValue("USERGETACCEXT/DATA/OUT_2")) ? "FALSE" : trans.getValue("USERGETACCEXT/DATA/OUT_2"));
		
		if ("FALSE".equals(downloadAccess))
		{
			user_access = user_access + "0:";
		}
		else
		{
			user_access = user_access + "1:";
		}
		
		if ("FALSE".equals(printAccess))
		{
			user_access = user_access + "0";
		}
		else
		{
			user_access = user_access + "1";
		}
	}
	
	public String crypt(String texti, String salasana)
	{
		byte[] sana;
		int x1 = 0;
		int y1;
		int z1;
		String crypted = "";
		for (int i = 0; i < salasana.length(); i++)
		{
			sana = salasana.substring(i, i + 1).getBytes();
			x1 = x1 + (int)sana[0];
		}
		
		x1 = (int) Math.floor((x1 * 0.1 / 6));
		y1 = x1;
		z1 = 0;
		for (int j = 0; j < texti.length(); j++)
		{
			sana = texti.substring(j, j + 1).getBytes();
			z1 = z1 + 1;
			if (z1 == 6)
			{
				z1 = 0;
			}
			x1 = 0;
			if (z1 == 0) x1 = (int)sana[0] - (y1 - 2);
			if (z1 == 1) x1 = (int)sana[0] + (y1 - 5);
			if (z1 == 2) x1 = (int)sana[0] - (y1 - 4);
			if (z1 == 3) x1 = (int)sana[0] + (y1 - 2);
			if (z1 == 4) x1 = (int)sana[0] - (y1 - 3);
			if (z1 == 5) x1 = (int)sana[0] + (y1 - 5);
			x1 = x1 + z1;
			crypted = crypted + (char)x1;
		}
		return crypted;
	}

	public void preDefine() throws FndException
	{

		ASPManager mgr = getASPManager();
		
		fmt = mgr.newASPHTMLFormatter();

		disableOptions();
		disableHomeIcon();
		disableNavigate();

		fileblk = mgr.newASPBlock("FILE");
		
		fileblk.addField("IN_STR1").
				setHidden();
		
		fileblk.addField("IN_NUM","Number").
				setHidden();
		
		fileblk.addField("OUT_1").
				setHidden();
		
		fileblk.addField("OUT_2").
				setHidden();
		
		disableApplicationSearch();
		
		appendJavaScript("function selectDocument(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no)\n");
		appendJavaScript("{\n");
		appendJavaScript("    document.form.DOC_CLASS.value = doc_class;\n");
		appendJavaScript("    document.form.DOC_NO.value = doc_no;\n");
		appendJavaScript("    document.form.DOC_SHEET.value = doc_sheet;\n");
		appendJavaScript("    document.form.DOC_REV.value = doc_rev;\n");
		appendJavaScript("    document.form.DOC_TYPE.value = doc_type;\n");
		appendJavaScript("    document.form.FILE_NO.value = file_no;\n");
		appendJavaScript("    submit();\n");
		appendJavaScript("}\n");
	}
	
	// ===============================================================
	// HTML
	// ===============================================================

	protected String getDescription() 
	{
		return "DOCEDMCONTROLDESC: Doc Edm Control";
	}

	protected String getTitle()
	{
		return getDescription();
	}
	
	protected String appendOcxPresentation()
	{
		StringBuffer out = new StringBuffer();
		ASPManager mgr = getASPManager();
		
		// out.append("<OBJECT ID=\"IFSReader\" CLASSID=\"CLSID:009355A6-6227-4683-B501-55CF0DDB2E5A\" width=\"100%\" height=\"100%\"> \n");
		out.append("<OBJECT ID=\"IFSReader\" CLASSID=\"CLSID:1A790BA5-13E4-4EDE-9B4A-3CE7DA80AD9A\" width=\"100%\" height=\"100%\"> \n");
		out.append("   <param name=\"pAuthASPName\" value=\"" + user_access + "\" />\n");
		out.append("   <param name=\"pServerName\" value=\"" + location_address + "\" />\n");
		out.append("   <param name=\"pUserId\" value=\"" + location_user + "\" />\n");
		out.append("   <param name=\"pPassword\" value=\"" + location_password + "\" />\n");
		out.append("   <param name=\"pFilePath\" value=\"" + remote_path + "\" />\n");
		out.append("   <param name=\"pIfsRealFileName\" value=\"" + user_file_name + "\" />\n");
		out.append("   <param name=\"pProtocol\" value=\"" + location_type + "\" />\n");
		out.append("   <param name=\"pFileSrvPort\" value=\"" + location_port + "\" />\n");
		out.append("   <param name=\"pAutoVuePageURL\" value=\"" + autovue_page_url + "\" />\n");
		out.append("   <param name=\"pIsDispSetButton\" value=\"false\" />\n");
		// out.append("   <param name=\"pOperaUserId\" value=\"" + mgr.getUserId() + "\" />\n");
		// out.append("   <param name=\"pRecordHistURL\" value=\"" + record_hist_url + "\" />\n");
		out.append("</OBJECT>\n");
		
		return out.toString();
	}
	
	protected AutoString getContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		AutoString out = getOutputStream();
		ASPHTMLFormatter fmt = mgr.newASPHTMLFormatter();
		
		out.clear();
		
		out.append("<html>\n");
		out.append("<head>\n");
		out.append("<style>\n");
		out.append("html,body{\n");
		out.append("    padding:0;\n");
		out.append("    margin:0;\n");
		out.append("}\n");
		out.append("</style>\n");
		out.append("<script language=\"javascript\">\n");
		out.append("var width=screen.availWidth - 2;\n");
		out.append("var height=screen.availHeight;\n");
		out.append("self.resizeTo(width, height);\n");
		out.append("self.moveTo(0, 0);\n");
		out.append("</script>\n");
		out.append(mgr.generateHeadTag(getDescription()));
		out.append("</head>\n");
		out.append("<body leftMargin=0 topMargin=0 bottomMargin=0 rightMargin=0>\n");
		out.append("<form ");
		out.append(mgr.generateFormTag());
		out.append(">\n");
		
		out.append("<input type=\"hidden\" name=\"DOC_CLASS\" value=\"" + doc_class + "\">\n");
		out.append("<input type=\"hidden\" name=\"DOC_NO\" value=\"" + doc_no + "\">\n");
		out.append("<input type=\"hidden\" name=\"DOC_SHEET\" value=\"" + doc_sheet + "\">\n");
		out.append("<input type=\"hidden\" name=\"DOC_REV\" value=\"" + doc_rev + "\">\n");
		out.append("<input type=\"hidden\" name=\"DOC_TYPE\" value=\"" + doc_type + "\">\n");
		out.append("<input type=\"hidden\" name=\"FILE_NO\" value=\"" + file_no + "\">\n");
		
		if ("ORIGINAL".equals(doc_type))
			out.append(appendOcxPresentation());
		
		out.append(mgr.generateClientScript());
		out.append("</form>\n");
		out.append("</body>\n");
		out.append("</html>\n");
		
		return out;
	}
}
