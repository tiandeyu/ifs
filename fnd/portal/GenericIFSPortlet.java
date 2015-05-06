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
 * File        : GenericIFSPortel.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2004-09-01 - Created.
 *    Ramila H 2004-09-27 - Merged with montgomary stuff. 
 *    Ramila H 2004-10-18 - Implemented JSR168 support
 *    Ramila H 2004-11-03 - Added renderURL to doView for HTML form.submit to work in view mode.
 *    Mangala  2004-11-09 - Added SSO capabilty for JSR168.
 *    Mangala  2004-11-10 - Added help functionality. But should be improved later.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2006/08/01 rahelk Bug id 59663, changed functionality according to websphere portal server
 *
 * Revision 1.1  2005/09/15 12:38:02  japase
 * *** empty log message ***
 *
 * Revision 1.3  2005/06/27 05:01:09  mapelk
 * Bug fixes for std portlets
 *
 * Revision 1.2  2005/05/06 09:56:43  mapelk
 * changes required to make standard portlets (JSR 168) to run on WebSphere
 * 
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.portal;

import java.io.*;
import javax.portlet.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

/**
 * @author Administrator
 *
 * A sample portlet based on PortletAdapter
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GenericIFSPortlet extends GenericPortlet
{
   /*=============================================================================================================*/
   /*=============================================================================================================*/

   public static final String STD_PORTLET_NAME  = "__STD_PORTLET_NAME";
   public static final String STD_PORTLET_STATE = "__STD_PORTLET_STATE";
   //public static final String USER_REF_KEY      = "__IFSWORLD_USER_KEY";
   //public static final String USER_ID           = "__IFSWORLD_USER_ID";
   public static final String DEFAULT_URL       =  "/DefaultStdPortlet.page";

   private static final String GENERIC_PORTLET  = "ifs.fnd.portal.GenericIFSPortlet";
   
   //Ramila - setTitle limitation in current release so no point in overriding doDispatch yet
   protected void doDispatch111 (RenderRequest request, RenderResponse response) throws PortletException,java.io.IOException
   {
      WindowState state = request.getWindowState();
      //request.getPortletSession().setAttribute(USER_ID,request.getRemoteUser(),PortletSession.APPLICATION_SCOPE);
    
      if ( ! state.equals(WindowState.MINIMIZED))
      {
          PortletMode mode = request.getPortletMode();
          if (mode.equals(PortletMode.VIEW)) {
             doView (request, response);
          }
          else if (mode.equals(PortletMode.EDIT)) {
             doEdit (request, response);
         }
         else if (mode.equals(PortletMode.HELP)) {
            doHelp (request, response);
         }
         else {
            throw new PortletException("unknown portlet mode: " + mode);
         }
      }
      else
         doMinizisedView (request, response);
   }


   /*=============================================================================================================*/
   /**
   /*=============================================================================================================*/


   /**
    * @see org.apache.jetspeed.portlet.Portlet#init(PortletConfig)
    */
/*
   public void init(PortletConfig portletConfig) throws org.apache.jetspeed.portlet.UnavailableException
   {
      super.init(portletConfig);
   }
*/

   private String portlet_name;
   private String dummy;

   private String portletName()
   {
      if(portlet_name==null)
      {
         String clsname = getClass().getName();

         if(GENERIC_PORTLET.equals(clsname))
            portlet_name = getInitParameter("ifsclass");
         else
            portlet_name = Str.replace( clsname, "ifs.portal", "ifs.fnd.portlets");
      }

      return portlet_name;
   }

   private String encodeIdentifier( String str )
   {
      if( str==null || str.length()==0 ) return null;

      AutoString out       = new AutoString();
      boolean    addprefix = false;

      for(int i=0; i<str.length(); i++)
      {
         char ch = str.charAt(i);
         if( (ch>='0' && ch<='9') || ch=='_' )
         {
            if(i==0)
               addprefix = true;
            out.append(ch);
         }
         else if( (ch>='a' && ch<='z') || (ch>='A' && ch<='Z') )
            out.append(ch);
         else
         {
            if(i==0)
               addprefix = true;
            out.append('_');
            out.append(Integer.toHexString(ch));
            out.append('_');
         }
      }
      return addprefix ? "ID_"+out.toString() : out.toString();
   }
   /*=============================================================================================================*/
   /* Portlet API */
   /*=============================================================================================================*/

   public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
   {
   }


   /**
    * @see org.apache.jetspeed.portlet.PortletAdapter#doView(PortletRequest, PortletResponse)
    */
   protected void doView (RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      response.setContentType("text/html;charset=utf-8");

      PortletContext ctx = this.getPortletConfig().getPortletContext();
      String nspc = encodeIdentifier(response.getNamespace());

      PortletURL url = response.createRenderURL();
      String attrname  = "renderURL";
      // needed if HTML form elements are submitted in view mode (eg: popups: __COMMAND)
      String attrvalue = url.toString();
      request.setAttribute(attrname, attrvalue);  


      String disp_url = DEFAULT_URL+"?"+STD_PORTLET_NAME+"="+portletName()+"&__PORTLET_ID="+nspc
                        +"&"+STD_PORTLET_STATE+"="+request.getWindowState();

      //String mode = (String)request.getParameter ("MODE");
      //Util.debug("==> "+portletName()+".doView(): get attribute MODE="+mode);
      //if( !Str.isEmpty(mode) )
      //   disp_url = disp_url + "&"+STD_PORTLET_MODE+"="+mode;

      PortletRequestDispatcher rd = ctx.getRequestDispatcher(disp_url);
      rd.include(request, response);
      
      
      //PortletURL url = response.createRenderURL();
      url.setPortletMode(PortletMode.EDIT); //for the configure link

      PrintWriter writer = response.getWriter();
      //writer.println("portlet body here");
      writer.print("<script language=\"JavaScript\">\n");
      writer.print("function editPortlet");
      writer.print(nspc.toUpperCase());
      writer.print("()\n{\n");
      writer.print("  location.replace('");
      writer.print(url.toString());
      writer.print("');\n");
      writer.print("}\n");
      writer.print("</script>\n");

      writer.print("\n</form> <!-- END OF doView -->\n");
   }


   protected void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      //response.setTitle( portletName() );

      response.setContentType("text/html;charset=utf-8");
      //PrintWriter writer = response.getWriter();

      String nspc = encodeIdentifier(response.getNamespace());

      //PortletSession ses = request.getPortletSession();
      //long lastacc = ses.getLastAccessedTime();
      //boolean first = true;//ses.getAttribute(""+lastacc, PortletSession.APPLICATION_SCOPE)==null;
      //if(first)
      //   ses.setAttribute(""+lastacc, new Object(), PortletSession.APPLICATION_SCOPE);

      PortletURL url = response.createRenderURL();
      url.setPortletMode(PortletMode.VIEW);
      String attrname  = "renderURL";
      String attrvalue = url.toString();
      Util.debug("==> "+portletName()+".doEdit(): setting request attribute '"+attrname+"' to '"+attrvalue+"'");
      request.setAttribute(attrname, attrvalue);

      PortletContext ctx = getPortletConfig().getPortletContext();
      String disp_url = DEFAULT_URL+"?"+STD_PORTLET_NAME+"="+portletName()+"&__PORTAL_MODE=C&__PORTLET_ID="+nspc
                        +"&"+STD_PORTLET_STATE+"="+request.getWindowState();

      PortletRequestDispatcher rd = ctx.getRequestDispatcher(disp_url);
      rd.include(request, response);

      PrintWriter writer = response.getWriter();
      
      url.setPortletMode(PortletMode.EDIT); 
      //writer.println("portlet body here");
      writer.print("<script language=\"JavaScript\">\n");
      writer.print("function editPortlet");
      writer.print(nspc.toUpperCase());
      writer.print("()\n{\n");
      //writer.print("  location.replace('");
      writer.print("f.action='" + url.toString() + "';");
      writer.print("f.submit();");
      //writer.print("');\n");
      writer.print("}\n");
      writer.print("</script>\n");    
      writer.print("\n</form> <!-- END OF doEdit -->\n");
   }


   protected void doHelp (RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      response.setContentType("text/html;charset=utf-8");
      PrintWriter writer = response.getWriter();
      //For Pluto - To be improved in later release.
      //writer.print("Click <a href=\"javascript:showHelp('"+ portletName() + "')\">here</a> to open help in new window.<br>");
      //writer.print("<br><script language=javascript src=\""+request.getContextPath()+"/secured/common/scripts/clientscript.js\"></script>");
      //writer.print("<br><script>HELP_URL =\"" + request.getContextPath()+"/secured/common/scripts/Help.page\";</script>");

      //***according to IBM support its ok to use iframe
      String help_url = request.getContextPath()+"/secured/common/scripts/Help.page?url="+portletName();
      writer.print("<iframe src='"+help_url+"' SCROLLING=\"auto\" FRAMEBORDER=0 Width='100%' height='250'>");
   }
   
   private void doMinizisedView (RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      /*
      response.setContentType("text/html;charset=utf-8");
      PortletContext ctx = this.getPortletConfig().getPortletContext();

      String nspc = encodeIdentifier(response.getNamespace());
      
      String disp_url = DEFAULT_URL+"?"+STD_PORTLET_NAME+"="+portletName()+"&__PORTLET_ID="+nspc
                        +"&"+STD_PORTLET_STATE+"="+WindowState.MINIMIZED;

      PortletRequestDispatcher rd = ctx.getRequestDispatcher(disp_url);
      rd.include(request, response);
      */
      response.setTitle("Minimized Title test");
      PrintWriter writer = response.getWriter();
      writer.print("\n</form> <!-- END OF doMinizisedView -->\n");
   }

   /*=============================================================================================================*/
}
