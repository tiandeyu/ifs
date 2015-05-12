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
*  File        : CopyPage.java
*  Modified    :
* ----------------------------------------------------------------------------
* New Comments:
* 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.2  2005/06/13 10:12:33  rahelk
* CSL 2: private settings - bug fix: added sequence control
*
* Revision 1.1  2005/06/09 11:23:11  rahelk
* CSL 2: private settings
*
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CopyPage extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.CopyPage");

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPConfig cfg;
   private ASPContext ctx;
   private ASPBlock blk;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String link_name;
   private String original_page;
   private String def_key;
   private boolean is_submit;
   
   //===============================================================
   // Construction
   //===============================================================
   public CopyPage(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }



   public void run()
   {
      ASPManager mgr = getASPManager();

      cfg = getASPConfig();
      ctx = getASPContext();

      link_name     = ctx.readValue("LINK_NAME", mgr.getQueryStringValue("HEADTITLE") );
      original_page = ctx.readValue("ORIGINAL_PAGE", mgr.getQueryStringValue("ORIGINAL") );
      def_key       = ctx.readValue("DEF_KEY", mgr.getQueryStringValue("DEF_KEY"));
      
      if (DEBUG)
      {
         debug("  original_page=" + original_page );
         debug("  link_name=" + link_name );
         debug("  def_key="  + def_key );
      }

      
      if (mgr.buttonPressed("SAVE"))
      {
         is_submit = true;
         saveCopy();
      }

      ctx.writeValue("LINK_NAME", link_name );
      ctx.writeValue("ORIGINAL_PAGE", original_page );
      ctx.writeValue("DEF_KEY", def_key);
   }
   
   private String getCopySquence()
   {
      ASPManager mgr = getASPManager();
   
      //User/Web/b2ejap/Pages/CUSTOMIZED_PAGES/demorw.order/LastCopy
      String copy_seq = readGlobalProfileValue(ProfileSectionKey(),false);
      
      if (mgr.isEmpty(copy_seq))
         copy_seq = "1";      
      
      return copy_seq;
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableNavigate();
      disableConfiguration();
      disableValidation();
      disableOptions();

      blk = mgr.newASPBlock("DUMMY");
      blk.addField("PROFILE");
      blk.addField("URL");
   } 
   
   private void saveCopy()
   {
     ASPManager mgr = getASPManager(); 
     ASPBuffer links = readGlobalProfileBuffer("Links",false);
     int order=0;
     if (links!=null)
       order=links.countItems(); 
     
     String link = original_page + "?";
     
     if (!mgr.isEmpty(def_key))
        link = link + "__DYNAMIC_DEF_KEY="+def_key+"&";
     
     String copy_seq = getCopySquence();
     link += mgr.PAGE_COPY_KEY +"=" + mgr.PAGE_COPY_VALUE + copy_seq;
     
     writeGlobalProfileValue("Links/"+"Link_"+order+ProfileUtils.ENTRY_SEP+mgr.readValue("LINK_NAME"),link, false);

     int seq = 0;
     try
     {
        seq = Integer.valueOf(copy_seq).intValue();
     }
     catch (NumberFormatException e)
     {
     }
     seq++;
     
     writeGlobalProfileValue(ProfileSectionKey(),""+seq, false);
   }

   private String ProfileSectionKey()
   {
      String tmp = original_page;
      String page_name = tmp.substring(tmp.lastIndexOf('/')+1,tmp.lastIndexOf('.')).toLowerCase();
      
      tmp = tmp.substring(0,tmp.lastIndexOf('/'));
      
      String mod_name = tmp.substring(tmp.lastIndexOf('/')+1).toLowerCase();

      return "CustomizedPages/"+(mod_name+"."+page_name)+ ProfileUtils.ENTRY_SEP+"LastCopy";
      
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESCOPYPAGETITLE: Copy Page";
   }

   protected String getTitle()
   {
      return "FNDPAGESCOPYPAGETITLE: Copy Page";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      if (is_submit)
      {
         appendDirtyJavaScript("window.close();");
         return;
      }
      
      beginDataPresentation();
      drawSimpleCommandBar("FNDPAGECOPYPAGECMDBAR: Copy Page");
      
      appendToHTML("<table border=0 width='100%' cellpadding=0 cellspacing=0 class=pageFormWithBorder>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>&nbsp;");
      printText("FNDPAGECOPYPAGE: The link to the page copy will be added to 'My Links' portlet repository for customization.");
      appendToHTML("</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td>&nbsp;</td>\n");
      appendToHTML("</tr>\n");

      
      appendToHTML("<tr>\n");
      appendToHTML("<td>&nbsp;\n");
      printReadLabel("FNDPAGESCOPYPAGENAMEOFLINK: name of the link");
      appendToHTML("&nbsp;&nbsp;\n");
      printField("LINK_NAME",link_name + " (copy "+getCopySquence()+")","",30,50);
      appendToHTML("</td>\n");
      appendToHTML("</tr>\n");
      
      appendToHTML("<tr>\n");
      appendToHTML("<td>&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      
      appendToHTML("<tr>\n");
      appendToHTML("<td>&nbsp;\n");
      printSubmitButton("SAVE","FNDPAGESCOPYPAGESAVE: Save Copy","");
      appendToHTML("&nbsp;\n");
      printButton("CANCEL","FNDPAGESCOPYPAGECANCEL: Cancel","onclick='javascript:window.close();'");
      appendToHTML("</td>\n");
      appendToHTML("</tr>\n");
      
      

      appendToHTML("</table>\n");
      
      printHiddenField("ORIGINAL_PAGE",original_page);
      endDataPresentation(false);
   }

}
