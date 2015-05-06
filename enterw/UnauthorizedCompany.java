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
*  File        : UnauthorizedCompany.java
*  Author		: Anil Padmajeewa
*  Date			: 2006/12/05
* ----------------------------------------------------------------------------
*  Date      Sign    History
* ----------------------------------------------------------------------------
*  20061205  Vohelk  LCS merge 61590, Corrected
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class UnauthorizedCompany extends ASPPageProvider
{
	//===============================================================
   // Static constants 
   //===============================================================
   
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.UnauthorizedCompany");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   
   private ASPHTMLFormatter fmt;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private String titleStr;
   private String company;

   
   //===============================================================
   // Construction 
   //===============================================================
   
   public UnauthorizedCompany(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes

      titleStr   = null;
      company = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      UnauthorizedCompany page = (UnauthorizedCompany)(super.clone(obj));

      // Initializing mutable attributes
      
      page.titleStr   = null;
      page.company = null;  

      // Cloning immutable attributes
      
      page.fmt = page.getASPHTMLFormatter();
      
      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      fmt = mgr.newASPHTMLFormatter();
      ASPBuffer buff;

      if( mgr.commandLinkActivated() )
         eval(mgr.commandLinkFunction());

      if(mgr.dataTransfered())
      {
         buff = mgr.getTransferedData();
         company = buff.getBufferAt(0).getValue("COMPANY");
         mgr.showAlert(mgr.translate("ENTERWUNAUTHORIZEDCOMPANYINVALIDUSER: You are not a registered user in this company: &1", company));
      }

      adjust();
   }

	
	public void  preDefine()
	{
	}
  

   public void changeCompany()
   {
      ASPManager mgr = getASPManager();
      mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"enterw/NavChangeCompany.page?");
   }
  
	public void  adjust()
	{
		ASPManager mgr = getASPManager();

      company = mgr.isEmpty(company) ? "" :company;

		titleStr = mgr.translate("ENTERWUNAUTHORIZEDCOMPANYTITLE: Unauthorized Company: ") + company;
	}
	
	
//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return titleStr;
   }

   protected String getTitle()
   {
      ASPManager mgr = getASPManager();
      return mgr.translate("ENTERWUNAUTHORIZEDCOMPANYTITLE1: Unauthorized Company");
   }

   protected AutoString getContents() throws FndException
   {
		AutoString out = getOutputStream();
		out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(titleStr));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(titleStr));
      //out.append("<font size=\"1\" face=Verdana>");
      out.append(fmt.showCommandLink("changeCompany",mgr.translate("ENTERWUNAUTHORIZEDCOMPANYCHANGECOMP: Change Company")));
      //out.append(" </font>\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
}