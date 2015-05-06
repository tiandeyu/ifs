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
*  File         : DocStructureNavigator.java
*  Description  : Container for the Structure Navigator on the left pane
*                 and Document Info in the right
*
*
*
*  04-07-2004    Dikalk    Created.
*  ------------------------------------------------------------------------------
*/



package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.docmaw.edm.DocumentTransferHandler;


public class DocStructureNavigator extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocStructureNavigator");

   String doc_issue_url;
   String navigator_url;


   public DocStructureNavigator(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf = mgr.getTransferedData();

      doc_issue_url = getDataTransferUrl("DocIssue.page", buf);
      navigator_url = getDataTransferUrl("DocStructureNavigatorTree.page", buf);
   }


   public String getDataTransferUrl(String transfer_page, ASPBuffer buf)
   {
      ASPManager mgr = getASPManager();
      String serialized_data = mgr.pack(buf);
      String url = transfer_page + "?" + "MODE=EXPLORE_STRUCTURE&__TRANSFER=" + serialized_data;
      return url;
   }


   protected String getDescription()
   {
      return getTitle();
   }


   protected String getTitle()
   {
      return "DOCMAWDOCSTRUCTURENAVIGATORTITLE: Document Info - Navigate Structure";
   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getTitle()));
      out.append("</head>\n");
      out.append("<frameset id=\"FrameHeader\" rows='15%, *'>\n");
      out.append("   <frame src=\"DocStructureNavigatorHeader.page\" name=\"DocStructureNavigatorHeader\" scrolling=\"no\" frameborder=\"0\">\n");
      out.append("   <frameset id=\"FrameBody\" cols='25%, *'>\n");
      out.append("      <frame src=\"" + navigator_url + "\" id=\"DocStructureNavigatorTree\" name=\"DocStructureNavigatorTree\" frameborder=\"1\" marginheight=10>\n");
      out.append("      <frame src=\"" + doc_issue_url + "\" id=\"DocIssue\" name=\"DocIssue\" frameborder=\"1\" marginheight=10>\n");
      out.append("   </frameset>\n");
      out.append("</frameset>\n");
      out.append("</html>\n");
      return out;
   }

}
