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
*  File        : AddLink.java
*  Modified    :
*    ASP2JAVA Tool  2001-03-04  - Created Using the ASP file AddLink.asp
*    Kingsly P      2001-03-04  - Upgarde to WK3.5 Environment
*    Kingsly P      2001-04-27  - Add support for case sensitive queries
*    Suneth M       2001-Sep-11 - Changed duplicated localization tags.
*    Suneth M       2001-Oct-12 - Changed saveLink().
*    Ramila H       2002-Nov-05 - Disabled the applet.
*    Sampath W.     2003-May-12 - Changed the interface according to new specification in Build 4.
*    Chandana D     2004-May-12 - Updated for the use of new style sheets.
* ----------------------------------------------------------------------------
* New Comments:
* 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
* Revision 1.3  2007/01/23  buhilk
* Bug id 63032, Modifed the sanity check inside the saveLink() method.
*
* Revision 1.2  2006/09/29  buhilk          
* Bug id 60891, Modifed printContents() to fix inconsistent label styles.
*
* Revision 1.1  2006/09/19  buhilk          
* Bug id 59842, Modifed saveLink() and run() to be CSV compatible.
*
*               2006/08/21  gegulk
* Bug id 60018 - removed the character "^" from the error message in saveLink()
*
* Revision 1.1  2006/02/24  riralk          
* Modifed saveLink() to show an error if the name of the link contains / or ^, to avoid corrupt profile.
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.5  2005/08/18 09:06:09  sumelk
* Merged the corrections for Bug 51767, Changed printContents().
*
* Revision 1.4  2005/04/08 06:05:37  riralk
* Merged changes made to PKG12 back to HEAD
*
* Revision 1.3  2005/02/11 09:12:11  mapelk
* Remove ClientUtil applet and it's usage from the framework
*
* Revision 1.2  2005/02/02 08:22:19  riralk
* Adapted global profile functionality to new profile changes
*
* ----------------------------------------------------------------------------
*/

 



package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.Str;


public class AddLink extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.AddLink");

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPLog log;
   private ASPHTMLFormatter fmt;
   private ASPConfig cfg;
   private ASPContext ctx;
   private ASPBlock blk;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private String searchbase;
   private String searchparam;
   private String headtitle;
   private String formlabel;
   private String searchlabel;
   private String casesensitive;
   private boolean is_submit;
   
   //===============================================================
   // Construction
   //===============================================================
   public AddLink(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      searchbase   = null;
      searchparam   = null;
      headtitle   = null;
      formlabel   = null;
      searchlabel   = null;
      casesensitive = null;
      is_submit = false;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      AddLink page = (AddLink)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.searchbase   = null;
      page.searchparam   = null;
      page.headtitle   = null;
      page.formlabel   = null;
      page.searchlabel   = null;
      page.casesensitive = null;
      page.is_submit = false;

      // Cloning immutable attributes
      page.log = page.getASPLog();
      page.fmt = page.getASPHTMLFormatter();
      page.cfg = page.getASPConfig();
      page.ctx = page.getASPContext();
      page.blk = page.getASPBlock(blk.getName());

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      log = mgr.getASPLog();
      fmt = getASPHTMLFormatter();
      cfg = getASPConfig();
      ctx = getASPContext();

      searchbase  = mgr.encodedURLWithCSV(ctx.readValue("SEARCHBASE",  mgr.getQueryStringValue("SEARCHBASE")));
      searchparam = ctx.readValue("SEARCHPARAM", mgr.getQueryStringValue("SEARCHPARAM") );
      headtitle   = ctx.readValue("HEADTITLE",   mgr.getQueryStringValue("HEADTITLE") );
      casesensitive = ctx.readValue("CASESENCETIVE", mgr.getQueryStringValue("CASESENCETIVE") );
      formlabel   = mgr.readValue("FORMMYLINKSLABEL",   headtitle );
      searchlabel = mgr.readValue("SEARCHMYLINKSLABEL", headtitle );

      log.debug("  searchbase=" + searchbase );
      log.debug("  searchparam="+ searchparam );
      log.debug("  headtitle="  + headtitle );
      log.debug("  formlabel="  + formlabel );
      log.debug("  searchlabel="+ searchlabel );
      log.debug("  casesensitive="+ casesensitive );

      if(!mgr.isEmpty(mgr.readValue("__LINKTYPE"))) 
      {
         is_submit = true;
         if(mgr.readValue("__LINKTYPE").equals("ADDLINK")) 
            saveLink("FORMLINK","FORMMYLINKSLABEL");
         else if (mgr.readValue("__LINKTYPE").equals("WITHDATA"))
               saveLink("SEARCHLINK","SEARCHMYLINKSLABEL");
         else if(mgr.readValue("__LINKTYPE").equals("ADDBOTH"))
         {
            saveLink("FORMLINK","FORMMYLINKSLABEL");
            saveLink("SEARCHLINK","SEARCHMYLINKSLABEL");
         }
      }

      ctx.writeValue("SEARCHBASE",  searchbase );
      ctx.writeValue("SEARCHPARAM", searchparam );
      ctx.writeValue("HEADTITLE",   headtitle );
      ctx.writeValue("CASESENCETIVE", casesensitive );
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableNavigate();
      disableConfiguration();
      disableValidation();
      disableOptions();
      enableConvertGettoPost(); //required for mgr.showError() to work in IE

      blk = mgr.newASPBlock("DUMMY");
      blk.addField("PROFILE");
      blk.addField("URL");
   } 
   
   public void  saveLink( String linkfld,String labelfld)
   {
     ASPManager mgr = getASPManager(); 
     ASPBuffer links = readGlobalProfileBuffer("Links",false);
     int order=0;
     if (links!=null)
       order=links.countItems(); 
     String label = mgr.readValue(labelfld);
     //sanity check
     if ( label.indexOf("/")>-1 )
        mgr.showError(mgr.translate("FNDPAGESADDLINKINVALIDCHARACTER: The character, &1 is not allowed within the name of the link", "/"));
     else if (label.indexOf("^")>-1 )
        mgr.showError(mgr.translate("FNDPAGESADDLINKINVALIDCHARACTER: The character, &1 is not allowed within the name of the link", "^"));
     else
       writeGlobalProfileValue("Links/"+"Link_"+order+ProfileUtils.ENTRY_SEP+label,mgr.readAbsoluteValue(linkfld), false);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESADDLINKTITLE: Add Link";
   }

   protected String getTitle()
   {
      return "FNDPAGESADDLINKTITLE: Add Link";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      beginDataPresentation();
      appendToHTML("<table width='100%' border=0 cellpadding=0 cellspacing=0 class=pageCommandBar>\n");
      appendToHTML("<tbody>\n"); 
      appendToHTML("<tr>\n");
      appendToHTML("<td nowrap height=19>&nbsp;\n");
      appendToHTML(mgr.translate("FNDPAGESADDLINKCMDBAR: Add Link")+ "&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("</tbody>\n");
      appendToHTML("</table>\n");
      appendToHTML("<table border=0 width='100%' cellpadding=0 cellspacing=0 class=pageFormWithBorder>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>");
      appendToHTML(fmt.drawReadValue("FNDPAGESADDLINKMYLINKSDESC1: Use the links on this page to add links to your link Repository"));
      appendToHTML("</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>");
      appendToHTML(fmt.drawReadValue("FNDPAGESADDLINKMYLINKSDESC2: that you can use later on for customizing of your 'My Links' portlet."));
      appendToHTML("</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>\n");
      appendToHTML("<table>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>");
      if(!mgr.isEmpty(searchparam))
         appendToHTML(fmt.drawReadValue("1.  "));
      appendToHTML(fmt.drawReadValue("<a href=javascript:selectlink('ADDLINK')>"+mgr.translate("FNDPAGESADDLINKADDLINKASIS: Add Link as is")+"</a>  (empty form)"));
      appendToHTML(fmt.drawHidden("FORMLINK",searchbase));
      appendToHTML("</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>\n");
      appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
      appendToHTML(fmt.drawReadValue("FNDPAGESADDLINKFORMMYLINKSNAMEOFLINK: name of the link"));
      appendToHTML("&nbsp;");
      appendToHTML(fmt.drawTextField("FORMMYLINKSLABEL",formlabel,null,25,50));
      appendToHTML("</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>&nbsp;</td>\n");
      appendToHTML("</tr>\n");

      if(!mgr.isEmpty(searchparam))
      {
         if(searchparam.indexOf("&REG") != -1)
            searchparam = mgr.replace(searchparam,"&REG", "&__REG");
          
         appendToHTML("<tr>\n");
         appendToHTML("<td>");
         appendToHTML(fmt.drawReadValue("2.  "));
         appendToHTML(fmt.drawReadValue("<a href=javascript:selectlink('WITHDATA')>"+mgr.translate("FNDPAGESADDLINKADDQRYLINK: Add Link with data")+"</a>\n"));
         appendToHTML("</td>\n");
         appendToHTML("</tr>\n");
         appendToHTML("<tr>\n");
         appendToHTML("<td>\n");
         appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
         appendToHTML(fmt.drawReadValue("FNDPAGESADDLINKFORMMYLINKSNAMEOFTHELINK: name of the link :"));
         appendToHTML(fmt.drawTextField("SEARCHMYLINKSLABEL",searchlabel,null,25,50));
         appendToHTML(fmt.drawHidden("SEARCHLINK",searchbase+searchparam+casesensitive));
         appendToHTML("</td>\n");
         appendToHTML("</tr>\n");
         appendToHTML("<tr>\n");
         appendToHTML("<td>&nbsp;</td>\n");
         appendToHTML("</tr>\n");
         appendToHTML("<tr>\n");
         appendToHTML("<td>\n");
         appendToHTML(fmt.drawReadValue("3.  "));
         appendToHTML(fmt.drawReadValue("<a href=javascript:selectlink('ADDBOTH')>"+mgr.translate("FNDPAGESADDLINKADDBOTHLINKS: Add both links")+"</a>\n"));
         appendToHTML("</td>\n");
         appendToHTML("</tr>\n");
         appendToHTML("<tr>\n");
         appendToHTML("<td>\n");
         appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
         appendToHTML(fmt.drawReadValue("FNDPAGESADDLINKFORMMYLINKSUSEFIELDSABOVE: use fields above to set the names of the links."));
         appendToHTML("</td>\n");
         appendToHTML("</tr>\n");
      }
      appendToHTML("<tr>\n");
      appendToHTML(fmt.drawHidden("__LINKTYPE","")); 
      appendToHTML("</tr>\n");
      appendToHTML("<SCRIPT LANGUAGE='JavaScript'>\n");
      appendToHTML("\tfunction selectlink(cmd)\n");
      appendToHTML("\t{\n");
      appendToHTML("\t\tvar f = document.forms[0];\n");
      appendToHTML("\t\tf.__LINKTYPE.value = cmd;\n");
      appendToHTML("\t\tsubmit();\n");
      appendToHTML("\t}\n");
      if (is_submit)
         appendToHTML("\t\t window.close();\n");
      
      appendToHTML("</SCRIPT>\n");
      appendToHTML("</table>\n");
      appendToHTML("</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("</table>\n");
      endDataPresentation(false);
   }

}
