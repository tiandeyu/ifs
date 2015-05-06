package ifs.fndlw;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.fnd.util.Str;


public class AttachUpload extends ASPPageProvider{

   
     private String logonInfoId;
     private String lov;
     private String lovValue;
     
     private String fileUploadPath;
     
     private String __UPLOADCOMMAND;
     private String fileName = "";
     
     private boolean upload=false;
     
     private int mMarkSize;
     private String mMarkType;
     private byte[] mMarkBody;
     
     public AttachUpload(ASPManager mgr, String page_path)
     {
       super(mgr, page_path);
     }
     
     public void run()
     {
        ASPManager mgr = getASPManager();
        ASPContext ctx = mgr.getASPContext();
        String qryStr = mgr.getQueryString();
        lov = mgr.isEmpty(mgr.getQueryStringValue("LOV")) ? ctx.readValue("LOV") : mgr.getQueryStringValue("LOV");
        logonInfoId = mgr.isEmpty(mgr.getQueryStringValue("INFO_ID")) ? ctx.readValue("INFO_ID") : mgr.getQueryStringValue("INFO_ID");
        if (logonInfoId != null || !"".equals(logonInfoId)) {
            ctx.writeValue("INFO_ID", logonInfoId);
            ctx.writeValue("LOV", lov);
        }
        
        __UPLOADCOMMAND = mgr.isEmpty(mgr.getQueryStringValue("__UPLOADCOMMAND")) ? "" : mgr.getQueryStringValue("__UPLOADCOMMAND");
        if (__UPLOADCOMMAND != null || !"".equals(__UPLOADCOMMAND)) {
            upload();
        }
        if(upload){
           //setValue();
        }
     }
     
     
     
   private void upload() {
      ASPManager mgr = getASPManager();
      String contentType = mgr.getRequestContentType();
      int contentLength = mgr.getRequestLength();
      if (contentType != null && contentType.indexOf("multipart/form-data") != -1 && contentLength > 0) {
         try {
            processMultipartRequest(contentType);
         } catch (Exception e) {
            mgr.responseWrite("<html>Error: " + e + "</html>");
            mgr.endResponse();
         }
      }
   }
     
   private void processMultipartRequest(String contentType) throws Exception {
      ASPManager mgr = getASPManager();
      HttpServletRequest request = mgr.getAspRequest();
      request.setCharacterEncoding("UTF-8");
      DBstep.iFileUpLoad2000 FileObj = new DBstep.iFileUpLoad2000( mgr.getAspRequest());
      String mSignatureID = FileObj.Request("INFO_ID");
      fileName = FileObj.FileName("MarkFile","UTF-8");//new String(FileObj.FileName("MarkFile").getBytes(),"UTF-8");
      System.out.println("lqw: fileName: " + fileName);
      if (fileName.equalsIgnoreCase("")) {
         mMarkSize = 0;
      } else {
         mMarkSize = FileObj.FileSize("MarkFile");
         mMarkType = FileObj.ExtName("MarkFile");
         mMarkBody = FileObj.FileBody("MarkFile");
      }
      
      lovValue = java.util.UUID.randomUUID().toString().replace("-", "") + mMarkType;
      if(Str.isEmpty(fileUploadPath)){
         Properties props = loadProperties();
         fileUploadPath = props.getProperty("fndlw.base.path");
      }
      ifs.fnd.service.Util.writeFile(fileUploadPath + java.io.File.separator + "upload" + java.io.File.separator  + lovValue, mMarkBody);
      
      
      
      
//      FileObj.SaveFile("TAG" + lovValue, lovValue);

      // grant select,update,insert,delete on ifsapp.SIGNATURE to FND_RUNTIME
      // ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      // ASPCommand cmd =
      // trans.addCustomCommand("UPDATE","Update &AO.SIGNATURE Set MarkSize=?,MarkType=?,MarkBody=?  Where SignatureID=?");
      // cmd.setItem("CATEGORY","Sql");
      // cmd.addParameter("MarkSize","N","IN",mMarkSize);
      // cmd.addParameter("MarkType","S","IN",mMarkType);
      // cmd.addParameter("MarkBody","R","IN",Util.toBase64Text(mMarkBody));
      // cmd.addParameter("INFO_ID","N","IN",mSignatureID+"");
      //
      // mgr.perform(trans);

      upload = true;
   }
     
     
     protected String getDescription()
     {
       return getTitle();
     }

     protected String getTitle()
     {
       return  "";
     }
     
     private  Properties loadProperties() {
        ASPManager mgr = getASPManager();
        String base_path = mgr.getAspRequest().getSession().getServletContext().getRealPath("/") 
                           + ifs.fnd.os.OSInfo.OS_SEPARATOR + "WEB-INF"
                           + ifs.fnd.os.OSInfo.OS_SEPARATOR +"config"
                           + ifs.fnd.os.OSInfo.OS_SEPARATOR + "server.properties";
        Properties props = new Properties();
        try {
           InputStream in = new BufferedInputStream(new FileInputStream(base_path));
           props.load(in);
        } catch (Exception e) {
           throw new RuntimeException("Error loading Gold Grid Db Configuration File, Please Check if file [" + base_path + "] exists or not.");
        }
        return props;
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

       out.append("id=\"form1\" name=\"form1\" method=\"post\" action=\"/b2e/secured/fndlw/AttachUpload.page\" enctype=\"multipart/form-data\"");
       
       out.append(">\n");
       out.append(mgr.startPresentation(getDescription()));
 
       out.append("<input type=\"hidden\" name=\"__UPLOADCOMMAND\" value=\"__UPLOADCOMMAND\">\n");
       out.append("<table width=\"90%\" border=\"0\">\n");
       out.append("  <tr>\n");
       out.append("    <th width=\"6%\" scope=\"row\">&nbsp;</th>\n");
       out.append("    <td colspan=\"4\">" + mgr.translate("ATTACHUPLOADPLEASESELECTFILETOUPLOAD: Please select file to upload") +":</td>\n");
       out.append("    <td width=\"11%\">&nbsp;</td>\n");
       out.append("  </tr>\n");
       out.append("  <tr>\n");
       out.append("    <th scope=\"row\">&nbsp;</th>\n");
       out.append("    <td>" + mgr.translate("ATTACHUPLOADFILE: File") + "</td>\n");
       out.append("    <td colspan=\"3\"><label>\n");
       out.append("      <input name=\"MarkFile\" type=\"file\" id=\"MarkFile\" size=\"60\" />\n");
       out.append("    </label></td>\n");
       out.append("    <td>&nbsp;</td>\n");
       out.append("  </tr>\n");
       out.append("  <tr>\n");
       out.append("    <th scope=\"row\">&nbsp;</th>\n");
       out.append("    <td>&nbsp;</td>\n");
       out.append("    <td width=\"20%\" align=\"right\"><label>\n");
       out.append("      <input type=\"button\" name=\"IMPORT\" id=\"IMPORT\" value=\"  "+mgr.translate("ATTACHUPLOADUPLOAD: Upload")+"  \" onclick=\"doImport()\" />\n");
       out.append("    </label></td>\n");
       out.append("    <td width=\"5%\" align=\"center\">&nbsp;</td>\n");

       out.append("    <td width=\"50%\" align=\"left\"><input type=\"button\" name=\"Close\" id=\"Close\" value=\"  "+mgr.translate("ATTACHUPLOADCLOSE: Close")+" \" onclick=\"javascript:window.close();\" /></td>\n");
       out.append("    <td>&nbsp;</td>\n");
       out.append("  </tr>\n");
       out.append("</table>\n");

       appendDirtyJavaScript("\n\n\n");
       appendDirtyJavaScript("function doImport()\n");
       appendDirtyJavaScript("{\n");
       appendDirtyJavaScript("\t\tfld = getField_('MarkFile',-1);\n");
       appendDirtyJavaScript("\t\tif(!checkMandatory_(fld,'" + mgr.translate("ATTACHUPLOADFILE: File") + "','" + mgr.translate("ATTACHUPLOADPLEASESELECTFILETOUPLOAD: Please select file to upload") +"'))\n");
       appendDirtyJavaScript("\t\t   return;\n");

       appendDirtyJavaScript("    with(document.getElementById(\"form1\")){\n");
       appendDirtyJavaScript("       method='post';\n");
       appendDirtyJavaScript("       document.form1.submit();\n");
       appendDirtyJavaScript("       }\n");
       appendDirtyJavaScript("       document.getElementById(\"IMPORT\").value=\" " + mgr.translate("ATTACHUPLOADUPLOADING: Uploading...") + " \";\n");
       appendDirtyJavaScript("       document.getElementById(\"IMPORT\").disabled=true;\n");
       appendDirtyJavaScript("       document.getElementById(\"Close\").disabled=true;\n");
       appendDirtyJavaScript("\t\t}\n");

       out.append(mgr.endPresentation());
       
       if(upload){
    	   appendDirtyJavaScript("var attachNamefld = opener.getField_('INFO_DOC_NAME',lov_row_nr);");
    	   appendDirtyJavaScript("attachNamefld.value = '" +fileName+ "';");
           appendDirtyJavaScript("setValue('" + lovValue + "');");
       }else{
//          appendDirtyJavaScript("document.form1.MarkFile.click();");
       }
       out.append("</form>\n");
       out.append("</body>\n");
       out.append("</html>");

       return out;
     }
}
