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
 *  File         : ConnectToAllSites.java 
 *  Description  : ConnectToAllSites
 *  Notes        : 
 * ----------------------------------------------------------------------------
 *  Modified     :
 * ----------------------------------------------------------------------------                                   
 *     RaKalk 09-May-2007 - Created.
 * ----------------------------------------------------------------------------
 */


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ConnectToAllSites extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.ConnectToAllSites");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //===============================================================
   // Construction 
   //===============================================================
   public ConnectToAllSites(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      if ( mgr.commandBarActivated() )
      {
         eval(mgr.commandBarFunction());
      }
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("USER_ID")) )
      {
         startUp();
      }
      else if (mgr.buttonPressed("BUTTON_OK"))
      {
         connectToAllSitesInCompany();
      }
      else if (mgr.buttonPressed("BUTTON_CANCEL"))
      {
         backToUserPage();
      }
      adjust();
   }


   public void startUp(){
      ASPManager mgr = getASPManager();
      ASPBuffer buffer = mgr.newASPBuffer();

      buffer.addItem("USER_ID",mgr.readValue("USER_ID"));
      buffer.addItem("COMPANY","");

      headset.clear();
      headset.addRow(buffer);
      headset.first();
   }

   private void connectToAllSitesInCompany(){
      ASPManager mgr = getASPManager();
      headset.store();
      String company = mgr.readValue("COMPANY");
      String userid = mgr.readValue("USER_ID");

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomCommand("CONNECALLSITES","User_Allowed_Site_API.Connect_All_Sites_In_Company");
      cmd.addParameter("COMPANY",company);
      cmd.addParameter("USER_ID",userid);

      trans = mgr.perform(trans);
      trans.clear();

      backToUserPage();
   }

   private void backToUserPage(){
      ASPManager mgr = getASPManager();
      mgr.redirectTo("UsersDefaults.page?SEARCH=USERID&USERID=" +mgr.URLEncode(mgr.readValue("USER_ID")));
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("USER_ID").
      setFunction("NULL");

      headblk.addField("COMPANY").
      setLabel("MPCCOWCONNECTALLSITESCOMPANY: Company").
      setSize(20).
      setFunction("NULL").
      setDynamicLOV("COMPANY_HAVING_SITE").
      setMandatory();

      headblk.setView("USER_ALLOWED_SITE");
      headblk.defineCommand("USER_ALLOWED_SITE_API","");

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
      headlay.unsetAutoLayoutSelect();

      headset = headblk.getASPRowSet();

      headtbl = mgr.newASPTable( headblk );
      headtbl.setTitle(mgr.translate("MPCCOWCONNECTTOALLSITESTABLETITLE: Connect To All Sites In Company"));

      headbar = mgr.newASPCommandBar(headblk);

      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.SAVE);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.SAVERETURN);
      headbar.disableCommand(headbar.CANCELEDIT);
      headbar.disableCommand(headbar.BACK);
      headbar.disableCommand(headbar.BACKWARD);

   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      mgr.getASPField("USER_ID").setHidden();

      if (headset.countRows()>0)
      {
         mgr.getASPField("COMPANY").
         setLOVProperty("WHERE","company IN (SELECT company FROM USER_FINANCE WHERE userid = '" + headset.getValue("USER_ID") + "')");
      }
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "MPCCOWCONNECTTOALLSITESDESCRIPTION: Connect To All Sites In Company";
   }

   protected String getTitle()
   {
      return "MPCCOWCONNECTTOALLSITESTITLE: Connect To All Sites In Company";
   }

   protected void printContents() throws FndException
   {
      if (headlay.isVisible())
      {

         appendToHTML(headlay.show());
         printSubmitButton("BUTTON_OK","MPCCOWCONNECTTOALLSITESBUTTONOK: OK","");
         printSpaces(5);
         printSubmitButton("BUTTON_CANCEL","MPCCOWCONNECTTOALLSITESBUTTONCANCEL: Cancel","");
      }
   }

}
