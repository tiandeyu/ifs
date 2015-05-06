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
*  File        : PartyAdminUser.java 
*  Modified    : Anil Padmajeewa
*                anpalk    21 Feb 2001    Code Review, Rename Blocks descriptivelly.
*    ASP2JAVA Tool  2001-01-26  - Created Using the ASP file PartyAdminUser.asp
*  Jakalk      : 2005-09-07 - Code Cleanup, Removed doReset and clone methods.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class PartyAdminUser extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.PartyAdminUser");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPBlock partyAdminUserblk;
   private ASPRowSet partyAdminUserset;
   private ASPCommandBar partyAdminUserbar;
   private ASPTable partyAdminUsertbl;
   private ASPBlockLayout partyAdminUserlay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private ASPQuery qry;
   private ASPCommand cmd;
   private ASPBuffer data;

   //===============================================================
   // Construction 
   //===============================================================

   public PartyAdminUser(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      adjust(); 
   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(partyAdminUserblk);
      qry.setOrderByClause("USER_ID");
      qry.includeMeta("ALL");

      mgr.querySubmit(trans,partyAdminUserblk);
      if ( partyAdminUserset.countRows() == 0 )
      {
         mgr.showAlert("ENTERWPARTYADMINUSERNODATA: No data found.");
         partyAdminUserset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(partyAdminUserblk);
      qry.setSelectList("to_char(count(*))N");
      mgr.submit(trans);
      partyAdminUserlay.setCountValue(toInt(partyAdminUserset.getValue("N")));
      partyAdminUserset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ADMINUSER","Party_Admin_User_API.New__",partyAdminUserblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ADMINUSER/DATA");
      partyAdminUserset.addRow(data);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      partyAdminUserblk = mgr.newASPBlock("ADMINUSER");

      partyAdminUserblk.addField("OBJID").
         setHidden();

      partyAdminUserblk.addField("OBJVERSION").
         setHidden();

      partyAdminUserblk.addField("DOMAIN_ID").
         setMandatory().
         setHidden().
         setUpperCase();

      partyAdminUserblk.addField("USER_ID").
         setSize(17).
         setDynamicLOV("APPLICATION_USER",650,450).
         setMandatory(). 
         setReadOnly().
         setInsertable().
         setLabel("ENTERWPARTYADMINUSERUSERID: User Id").
         setUpperCase();

      partyAdminUserblk.addField("APPLICATIONUSERDESCRIPTION").
         setSize(47).
         setReadOnly().
         setInsertable().
         setLabel("ENTERWPARTYADMINUSERAPPLICATIONUSERDESCRIPTION: Description").
         setFunction("APPLICATION_USER_API.Get_Description(:USER_ID)");

      mgr.getASPField("USER_ID").setValidation("APPLICATIONUSERDESCRIPTION");

      partyAdminUserblk.setView("PARTY_ADMIN_USER");
      partyAdminUserblk.defineCommand("PARTY_ADMIN_USER_API","New__,Modify__,Remove__");
      partyAdminUserset = partyAdminUserblk.getASPRowSet();

      partyAdminUserbar = mgr.newASPCommandBar(partyAdminUserblk);
      partyAdminUserbar.disableCommand("EditRow"); 

      partyAdminUsertbl = mgr.newASPTable(partyAdminUserblk);
      partyAdminUsertbl.setTitle("ENTERWPARTYADMINUSERPARTYADMINUSER: Access To Protected Persons");

      partyAdminUserlay = partyAdminUserblk.getASPBlockLayout();
      partyAdminUserlay.setFieldOrder("USER_ID");
      partyAdminUserlay.setDefaultLayoutMode(partyAdminUserlay.MULTIROW_LAYOUT);
   }


   public void  adjust()
   {
      if (partyAdminUserset.countRows() == 0 &&  partyAdminUserlay.getLayoutMode() == 5)
         partyAdminUserbar.disableCommand(partyAdminUserbar.DELETE);


      if (partyAdminUserset.countRows() == 0 )
      {
         partyAdminUserbar.disableCommand(partyAdminUserbar.FORWARD);
         partyAdminUserbar.disableCommand(partyAdminUserbar.BACKWARD);
         partyAdminUserbar.disableCommand(partyAdminUserbar.DELETE);
         partyAdminUserbar.disableCommand(partyAdminUserbar.DUPLICATEROW);
         partyAdminUserbar.disableCommand(partyAdminUserbar.EDITROW);
         partyAdminUserbar.disableCommand(partyAdminUserbar.BACK);
      }
   }

//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "ENTERWPARTYADMINUSERTITLE: Access To Protected Persons";
   }

   protected String getTitle()
   {
      return "ENTERWPARTYADMINUSERTITLE: Access To Protected Persons";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(partyAdminUserlay.show());
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
   }
}
