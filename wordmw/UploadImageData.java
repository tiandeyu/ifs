package ifs.wordmw;

import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

public class UploadImageData extends ASPPageProvider{

	
	  private String SignatureID;
	  
	  private String __UPLOADCOMMAND;
	  
	  private boolean upload=false;
	  
	  private int mMarkSize;
	  private String mMarkType;
	  private byte[] mMarkBody;
	  
	  public UploadImageData(ASPManager mgr, String page_path)
	  {
	    super(mgr, page_path);
	  }
	  
	  public void run()
	  {
		  ASPManager mgr = getASPManager();
		  ASPContext ctx = mgr.getASPContext();
		  SignatureID = mgr.isEmpty(mgr.getQueryStringValue("SignatureID")) ? ctx.readValue("SignatureID_") : mgr.getQueryStringValue("SignatureID");
		  if (SignatureID != null || !"".equals(SignatureID)) {
				ctx.writeValue("SignatureID_", SignatureID);
		  }
		  
		  __UPLOADCOMMAND = mgr.isEmpty(mgr.getQueryStringValue("__UPLOADCOMMAND")) ? "" : mgr.getQueryStringValue("__UPLOADCOMMAND");
		  if (__UPLOADCOMMAND != null || !"".equals(__UPLOADCOMMAND)) {
				upload();
		  }
		  if(upload){
			  mgr.redirectTo("/b2e/secured/wordmw/GoldSignature.page?SIGNATUREID="+SignatureID);
		  }
	  }
	  
	  
	  
	  private void upload()
	  {
          ASPManager mgr = getASPManager();
		  
		  String contentType=mgr.getRequestContentType();
		  
		  int contentLength=mgr.getRequestLength();
		  if(contentType!=null && contentType.indexOf("multipart/form-data")!=-1 && contentLength > 0)
	         {
	           try{
	              processMultipartRequest(contentType);
	           }catch(Exception e)
	           {
	        	   mgr.responseWrite("<html>Error: "+e+"</html>");
	        	   mgr.endResponse();
	           }
	         }

	  }
	  
	  private void processMultipartRequest(String contentType)throws Exception{
		  
		  ASPManager mgr = getASPManager();
		  DBstep.iFileUpLoad2000 FileObj=new DBstep.iFileUpLoad2000(mgr.getAspRequest());
		  
		  String mSignatureID=FileObj.Request("SignatureID");
		  
		  
			if (FileObj.FileName("MarkFile").equalsIgnoreCase("")) {
			  mMarkSize=0;
			}
			else {
			  mMarkSize=FileObj.FileSize("MarkFile");
			  mMarkType=FileObj.ExtName("MarkFile");
			  mMarkBody=FileObj.FileBody("MarkFile");
			}
		  
			//grant select,update,insert,delete on ifsapp.SIGNATURE to FND_RUNTIME
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPCommand cmd = trans.addCustomCommand("UPDATE","Update &AO.SIGNATURE Set MarkSize=?,MarkType=?,MarkBody=?  Where SignatureID=?");
	      cmd.setItem("CATEGORY","Sql");
          cmd.addParameter("MarkSize","N","IN",mMarkSize);
          cmd.addParameter("MarkType","S","IN",mMarkType);
          cmd.addParameter("MarkBody","R","IN",Util.toBase64Text(mMarkBody));
          cmd.addParameter("SignatureID","N","IN",mSignatureID+"");    

          mgr.perform(trans);
          
          upload=true;
	      
	  }
	  
	  
	  protected String getDescription()
	  {
	    return "上传图片";
	  }

	  protected String getTitle()
	  {
	    return "上传图片";
	  }
	  
	  
	  protected AutoString getContents() throws FndException
	  {
	    AutoString out = getOutputStream();
	    out.clear();
	    ASPManager mgr = getASPManager();

	    out.append("<html>\n");
	    out.append("<head>");
	    out.append(mgr.generateHeadTag(getDescription()));
	    out.append("<title>");
	    out.append(getTitle());
	    out.append("</title>\n");
	    out.append("</head>\n");
	    out.append("<body ");
	    out.append(mgr.generateBodyTag());
	    out.append(">\n");
	    out.append("<form ");

	    out.append("id=\"form1\" name=\"form1\" method=\"post\" action=\"/b2e/secured/fndlw/UploadImageData.page\" enctype=\"multipart/form-data\"");
	    
	    out.append(">\n");
	    out.append(mgr.startPresentation(getDescription()));
 
	    out.append("<input type=\"hidden\" name=\"__UPLOADCOMMAND\" value=\"__UPLOADCOMMAND\">\n");
	    out.append("<input type=\"hidden\" name=\"SignatureID\" value=\""+SignatureID+"\">\n");
	    out.append("<table width=\"90%\" border=\"0\">\n");
	    out.append("  <tr>\n");
	    out.append("    <th width=\"6%\" scope=\"row\">&nbsp;</th>\n");
	    out.append("    <td colspan=\"4\">请选择要上传的图片文件</td>\n");
	    out.append("    <td width=\"11%\">&nbsp;</td>\n");
	    out.append("  </tr>\n");
	    out.append("  <tr>\n");
	    out.append("    <th scope=\"row\">&nbsp;</th>\n");
	    out.append("    <td>图片文件</td>\n");
	    out.append("    <td colspan=\"3\"><label>\n");
	    out.append("      <input name=\"MarkFile\" type=\"file\" id=\"MarkFile\" size=\"60\" />\n");
	    out.append("    </label></td>\n");
	    out.append("    <td>&nbsp;</td>\n");
	    out.append("  </tr>\n");
	    out.append("  <tr>\n");
	    out.append("    <th scope=\"row\">&nbsp;</th>\n");
	    out.append("    <td>&nbsp;</td>\n");
	    out.append("    <td width=\"20%\" align=\"right\"><label>\n");
	    out.append("      <input type=\"button\" name=\"IMPORT\" id=\"IMPORT\" value=\"  上  传  \" onclick=\"doImport()\" />\n");
	    out.append("    </label></td>\n");
	    out.append("    <td width=\"5%\" align=\"center\">&nbsp;</td>\n");

	    out.append("    <td width=\"50%\" align=\"left\"><input type=\"button\" name=\"Close\" id=\"Close\" value=\"  返 回  \" onclick=\"javascript:window.location.href='/b2e/secured/wordmw/GoldSignature.page?SIGNATUREID="+SignatureID+"';\" /></td>\n");
	    out.append("    <td>&nbsp;</td>\n");
	    out.append("  </tr>\n");
	    out.append("</table>\n");

	    appendDirtyJavaScript("\n\n\n");
	    appendDirtyJavaScript("function doImport()\n");
	    appendDirtyJavaScript("{\n");
	    appendDirtyJavaScript("\t\tfld = getField_('MarkFile',-1);\n");
	    appendDirtyJavaScript("\t\tif(!checkMandatory_(fld,'图片文件','请选择要上传的图片文件！'))\n");
	    appendDirtyJavaScript("\t\t   return;\n");

//	    appendDirtyJavaScript("    if(getValue_('MarkFile',-1).toUpperCase().indexOf('.XLS') < 0){\n");
//	    appendDirtyJavaScript("       alert(\"请选择图片文件！\");\n");
//	    appendDirtyJavaScript("       return;\n");
//	    appendDirtyJavaScript("       }\n");
        
	    appendDirtyJavaScript("    with(document.getElementById(\"form1\")){\n");
	    appendDirtyJavaScript("       method='post';\n");
	    appendDirtyJavaScript("       action='/b2e/secured/wordmw/UploadImageData.page?__UPLOADCOMMAND=__UPLOADCOMMAND&SignatureID="+SignatureID+"';\n");
	    //appendDirtyJavaScript("       action='http://192.166.12.204/b2e/WordServer.do?__UPLOADCOMMAND=__UPLOADCOMMAND';\n");
	    appendDirtyJavaScript("       submit();\n");
	    appendDirtyJavaScript("       }\n");
	    appendDirtyJavaScript("       document.getElementById(\"IMPORT\").value=\" 上传中... \";\n");
	    appendDirtyJavaScript("       document.getElementById(\"IMPORT\").disabled=true;\n");
	    appendDirtyJavaScript("       document.getElementById(\"Close\").disabled=true;\n");
	    appendDirtyJavaScript("\t\t}\n");

	    out.append(mgr.endPresentation());
	    out.append("</form>\n");
	    out.append("</body>\n");
	    out.append("</html>");

	    return out;
	  }
}
