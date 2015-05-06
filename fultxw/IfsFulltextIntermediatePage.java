package ifs.fultxw;

import ifs.docmaw.DocmawConstants;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;

public class IfsFulltextIntermediatePage extends ASPPageProvider {

   public IfsFulltextIntermediatePage(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }
   
   public void run() 
   {
      ASPManager mgr = getASPManager();
      String docClass = mgr.readValue("DOC_CLASS");
      String docSubClass = mgr.readValue("SUB_CLASS");
      String qryString = mgr.getQueryString();
      String urlPrefix = mgr.getASPConfig().getProtocol()+"://"+ mgr.getASPConfig().getApplicationDomain()+""+mgr.getASPConfig().getApplicationContext();
      
      String targetPage = null;
      final String securedPath = "/secured/docmaw/";
      targetPage = DocmawConstants.getCorrespondingDocIssuePage(docClass, docSubClass);
      targetPage = urlPrefix + securedPath +  targetPage + "?" + qryString;
      mgr.redirectTo(targetPage);  
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

