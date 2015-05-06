package ifs.docmaw;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;
import ifs.hzwflw.util.URL;

public class DocmawIntermediatePage extends ASPPageProvider {

   public DocmawIntermediatePage(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }
   
   
   public void run() 
   {
      ASPManager mgr = getASPManager();
      String urlPrefix = mgr.getASPConfig().getProtocol()+"://"+ mgr.getASPConfig().getApplicationDomain()+""+mgr.getASPConfig().getApplicationContext();
      
      String docClass = null;
      String docNo = null;
      String docSheet = null;
      String docRev = null;
      String docSubClass = null;
      String fromFlag =  mgr.readValue("FROMFLAG",null);
      if("DOCSENDTRANS".equals(fromFlag)){
         docClass = mgr.readValue("SUB_DOC_CLASS");
         docNo = mgr.readValue("SUB_DOC_NO");
         docSheet = mgr.readValue("SUB_DOC_SHEET");
         docRev = mgr.readValue("SUB_DOC_REV");
         docSubClass = mgr.readValue("SUB_CLASS",null);
      }
      else if ("NORMAL".equals(fromFlag))
      {
         docClass = mgr.readValue("DOC_CLASS");
         docNo = mgr.readValue("DOC_NO");
         docSheet = mgr.readValue("DOC_SHEET");
         docRev = mgr.readValue("DOC_REV");
         docSubClass = mgr.readValue("SUB_CLASS",null);
      }
      else
      {
         docClass = mgr.readValue("ITEM13_DOC_CLASS");
         docNo = mgr.readValue("ITEM13_DOC_NO");
         docSheet = mgr.readValue("ITEM13_DOC_SHEET");
         docRev = mgr.readValue("ITEM13_DOC_REV");
         docSubClass = mgr.readValue("ITEM13_SUB_CLASS",null);
      }
      
      String tempUrl = null;
      final String securedPath = "/secured/docmaw/";
      tempUrl = DocmawConstants.getCorrespondingDocIssuePage(docClass, docSubClass);
      tempUrl = urlPrefix + securedPath +  tempUrl + "?";

      
      URL targetUrl =new URL(tempUrl);
      targetUrl.addParameters("DOC_CLASS", docClass);
      targetUrl.addParameters("DOC_NO", docNo);
      targetUrl.addParameters("DOC_SHEET", docSheet);
      targetUrl.addParameters("DOC_REV", docRev);
      mgr.redirectTo(targetUrl.toString());  
   }
   public String getDescription()
   {
      return "" ;
   }

   public String getTitle()
   {
      return  getDescription();
   }
   
   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      return out;
   }

}
