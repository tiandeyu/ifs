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
 * File        : Inbox.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Piotr Z   2001-Jan-15 - Created.
 *    Suneth M  2001-Sep-10 - Changed duplicated localization tags.
 *    Rifki R   2003-Jan-23 - Added codebase property for Outlook ActiveX Control.
 *    Sampath   2003-Mar-19 - disable customize page for Natscape.
 *
 * ----------------------------------------------------------------------------
 * New Comments:
 *    buhilk    2007-Feb-09 - Bug id: 63433, Added csv support
 *    sadhlk    2007-Aug-07 - Modified printCustomBody() and printContents() to avoid runtime error in
 *                            javascript.
 *
 */


package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.buffer.*;



public class OutlookView extends ASPPortletProvider
{
   private transient String   folder;
   private transient String   subfolder;
   private transient String   height;
   private final static String DEF_HEIGHT = "200";
   private final static String CODEBASE = "http://activex.microsoft.com/activex/controls/office/outlctlx.CAB#ver=9,0,0,3203";
   private ASPBuffer buf;

   public OutlookView( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }

   /**
    * Clear temporary variables.
    */
   protected void doReset() throws FndException
   {
      if(DEBUG) debug(this+": OutlookView.doReset()");

      super.doReset();

      folder    = null;
      subfolder = null;
      height    = DEF_HEIGHT;
   }

   /**
    * Define the logical structure of the portal.
    * Called only once to create the structure stored in the page pool for later use.
    */
   protected void preDefine() throws FndException
   {
      if(DEBUG) debug(this+": OutlookView.preDefine()");

      char sep = IfsNames.fieldSeparator;

      AutoString folders = new AutoString();

      folders.append(translate("FNDOUTVIEWFOLDER0: Calendar") + sep);
      folders.append(translate("FNDOUTVIEWFOLDER1: Contacts") + sep);
      folders.append(translate("FNDOUTVIEWFOLDER2: Deleted Items") + sep);
      folders.append(translate("FNDOUTVIEWFOLDER3: Drafts") + sep);
      folders.append(translate("FNDOUTVIEWFOLDER4: Inbox") + sep);
      folders.append(translate("FNDOUTVIEWFOLDER5: Journal") + sep);
      folders.append(translate("FNDOUTVIEWFOLDER6: Notes") + sep);
      folders.append(translate("FNDOUTVIEWFOLDER7: Outbox") + sep);
      folders.append(translate("FNDOUTVIEWFOLDER8: Sent Items") + sep);
      folders.append(translate("FNDOUTVIEWFOLDER9: Tasks") + sep);

      buf = getASPManager().newASPBuffer();
      buf.addBuffer("DATA").addItem("NAME", folders.toString());
      
      //includeJavaScriptFile("/OutlookView.js");
      
      init();
   }

   /**
    * Create a new instance if all existing instances of the same page are already
    * locked in the pool.
    */
   public ASPPoolElement clone( Object obj ) throws FndException
   {
      if(DEBUG) debug(this+": OutlookView.clone("+obj+")");

      OutlookView page = (OutlookView)(super.clone(obj));

      page.folder    = null;
      page.subfolder = null;
      page.height    = DEF_HEIGHT;
      page.buf       = buf;
      return page;
   }


   /**
    * Create the portlets title for different Outlook folders.
    */
   public String getTitle( int mode )
   {
      if (folder != null && !folder.equals("")) 
         return folder+( subfolder == null || subfolder.equals("") ? "" : "\\"+subfolder);

      if (subfolder != null && !subfolder.equals("")) 
         return subfolder;

      return translate(getDescription());
   }


   public static String getDescription()
   {
      return "FNDOUTVIEWTITLEDESC: Outlook View";
   }


   /**
    * If the portlet should be customizable this function must return true.
    */
   public boolean canCustomize()
   {
      if(DEBUG) debug(this+": OutlookView.canCustomize()");
      if(getASPManager().isExplorer())
         return true;
      else
         return false;
   }

   /**
    * Initialization of variables.
    */
   protected void init()
   {
      if(DEBUG) debug(this+": OutlookView.init()");

      folder     = readProfileValue("FOLDER");
      subfolder  = readProfileValue("SUBFOLDER");
      height     = readProfileValue("HEIGHT",DEF_HEIGHT);

      if(DEBUG) debug("  init:\n\t\t\t folder    = "+folder+"\n" +
                               "\t\t\t subfolder = "+subfolder+"\n" +
                               "\t\t\t height    = "+height+"\n");
   }

   public void printContents() throws FndException
   {
      if(getASPManager().isExplorer())
      {
         beginTransparentTable();

         beginTableBody();
            beginTableCell();
               appendToHTML("<OBJECT classid=CLSID:0006F063-0000-0000-C000-000000000046 id=ViewCtlFolder"+getId()+" width=100% height="+height);
               appendToHTML(" codebase=\""+CODEBASE+"\">\n");
               appendToHTML("\t<param name=\"View\" value>\n");               
               appendToHTML("\t<param name=\"Folder\" value=\""+(folder == null || folder.equals("") ? (subfolder==null || subfolder.equals("")?"":subfolder) : (folder + (subfolder == null || subfolder.equals("") ? "" : "\\\\" + subfolder)))+"\">\n");
               appendToHTML("\t<param name=\"Namespace\" value=\"MAPI\">\n");
               appendToHTML("\t<param name=\"Restriction\" value>\n");
               appendToHTML("\t<param name=\"DeferUpdate\" value=\"0\">\n");
               appendToHTML("</OBJECT>\n");
            endTableCell();
         endTableBody();
	 
	 appendDirtyJavaScript("document.form.ViewCtlFolder"+getId()+".Folder = \"" + (folder == null || folder.equals("") ? (subfolder==null || subfolder.equals("")?"":subfolder) : (folder + (subfolder == null || subfolder.equals("") ? "" : "\\\\" + subfolder))) + "\";");

         endTable();
      }
      else
      {
         printNewLine();
         setFontStyle(BOLD);
         printText("FNDINBOXERR: This portlet only works in Microsoft Internet Explorer");
         setFontStyle(NONE);
         printNewLine();
      }

   }

   /**
    * Print the HTML code for the customize mode.
    */
   public void printCustomBody() throws FndException
   {
      if(DEBUG) debug(this+": OutlookView.printCustomBody()");

      beginTransparentTable();
         beginTableTitleRow();
            printTableCell("FNDOUTVIEWDESC: You can choose folder which you want have shown in this portlet and specify portlet height.",3,0,LEFT);
         endTableTitleRow();
         beginTableBody();
            beginTableCell(3);
               printNewLine();
               printText("FNDOUTVIEWDESC1: You can set shown folder by choosing any value from the select list box, including empty value.");
               printNewLine();
               printText("FNDOUTVIEWDESC2: The next field is for manual specification of folder on any level in the Outlook folder list.");
               printNewLine();
               printText("FNDOUTVIEWDESC3: Use '\\\\' character as a level separator. Remember that folder name is case sensitive.");
               printNewLine();
               printText("FNDOUTVIEWDESC4: Finally, contents of both fields are concatenating to create an output folder path.");
               printNewLine();
               printNewLine();
               printText("FNDOUTVIEWDESC5: Examples:");
               printNewLine();
               printText(translate("FNDOUTVIEWDESC6: 1. To access the '&1' folder you can select it from the select list box or simply write in the next field.","Inbox"));
               printNewLine();
               printText(translate("FNDOUTVIEWDESC7: 2. To access the '&1' folder you can select '&2' folder from the select list box and write '&3' in the next field.","Inbox\\\\Private","Inbox","Private"));
               printNewLine();
               printSpaces(4);
               printText(translate("FNDOUTVIEWDESC8: Another possibility is writing whole path i.e. '&1' in the field next to the select list box.","Inbox\\\\Private"));
               printNewLine();
               printNewLine();
            endTableCell();
         nextTableRow();
            printTableCell("FNDOUTVIEWLIST: Folder:");
            beginTableCell();
               printSelectBox("FOLDER", buf, readAbsoluteProfileValue("FOLDER"));
            endTableCell();
         //nextTableRow();
            //printTableCell("FNDOUTVIEWSUB: Subfolder:");
            beginTableCell();
               printField("SUBFOLDER",readAbsoluteProfileValue("SUBFOLDER",""),50,100);
            endTableCell();
         nextTableRow();
            printTableCell("FNDOUTVIEWHEIGHT: View Height:");
            beginTableCell(2);
               printField("HEIGHT",readAbsoluteProfileValue("HEIGHT",DEF_HEIGHT),5);
            endTableCell();
         endTableBody();
      endTable();
   }

   /**
    * Save values of variables to profile and context if the user press OK button.
    */
   public void submitCustomization()
   {
      if(DEBUG) debug(this+": MyLinks.submitCustomization()");

      folder    = readValue("FOLDER");
      subfolder = readValue("SUBFOLDER");
      height    = readValue("HEIGHT");

      if(DEBUG) debug("  submitCustomization():\n"+
                      "\t\t\t folder    = "+folder+"\n"+
                      "\t\t\t subfolder = "+subfolder+"\n"+
                      "\t\t\t height    = "+height+"\n");

      writeProfileValue("FOLDER", readAbsoluteValue("FOLDER"));
      writeProfileValue("SUBFOLDER", readAbsoluteValue("SUBFOLDER"));
      writeProfileValue("HEIGHT", readAbsoluteValue("HEIGHT"));
   }

}


