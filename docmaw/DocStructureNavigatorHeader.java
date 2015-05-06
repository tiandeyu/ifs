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
*  File         : DocStructureNavigatorHeader.java
*  Description  : Container for the standard navigation buttons
*
*
*  04-07-2004    Dikalk    Created.
*  ------------------------------------------------------------------------------
*/



package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;


public class DocStructureNavigatorHeader extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocStructureNavigatorHeader");

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private String search_str;
   private String sGenerateClientScript;

   public DocStructureNavigatorHeader(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }


   public void run() throws FndException
   {
      // Nothing to do..
   }


   public String search_str()
   {
      ASPManager mgr = getASPManager();
                                      		 
      return search_str;  
   }
   
   public String  modifiedClientScript(ASPManager mgr,String sGenerateClientScript)
  {    
     sGenerateClientScript = mgr.replace(sGenerateClientScript,"document.location=","window.parent.location=");
     return sGenerateClientScript;
  }


   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendJavaScript("var arrLinks = new Array(2);\n");

      appendJavaScript("function modifyAnchors()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var links = document.links;\n");
      appendJavaScript("   var count = 0;\n");
      appendJavaScript("   for (var x = 0; x < links.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (links[x].href.indexOf(\"" + mgr.getApplicationPath() + "\") > 0)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         arrLinks[count] = links[x].href;\n");
      appendJavaScript("         links[x].name = count++;\n");
      appendJavaScript("         links[x].href = \"javascript:\";\n");
      appendJavaScript("         links[x].onclick=setLink;\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setLink(link)\n");
      appendJavaScript("{\n");
      appendJavaScript("   window.parent.location = arrLinks[eval(this.name)];\n");
      appendJavaScript("}\n");
   }


   protected String getDescription()
   {
      return getTitle();
   }


   protected String getTitle()
   {
      return "DOCMAWDOCSTRUCTURENAVIGATORHEADERTITLE: Document Info - Navigate Structure";
   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getDescription()));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      out.append(mgr.startPresentation(getTitle()));
      out.append(modifiedClientScript(mgr,mgr.endPresentation()));
      //out.append(mgr.generateClientScript());
      out.append("</form>\n");
      out.append("</body>\n");
      //out.append("</body>\n");
      out.append("</html>\n");


      // Function call to modify urls in header to redirect
      // the entire window, and not just the frame this header
      // is contained in..
      appendDirtyJavaScript("modifyAnchors();\n");

      return out;
   }

}

