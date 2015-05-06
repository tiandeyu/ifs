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
 *  File         : RerunErroneousAccountingsDlg.java
 *  Description  : Rerun Erroneous Accountings
 *  Notes        : 
 * ----------------------------------------------------------------------------
 *  Modified     :
 *   Cpeilk  - 2007-06-20 - Created.
 * ----------------------------------------------------------------------------
 */


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class RerunErroneousAccountingsDlg extends ASPPageProvider
{
   /* Static constants */
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.RerunErroneousAccountingsDlg");

   /* Instances created on page creation (immutable attributes) */
   private ASPBlock       headblk;
   private ASPBlockLayout headlay;
   private ASPCommandBar  headbar;

   /* Construction */
   public RerunErroneousAccountingsDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.buttonPressed("SUBMIT"))
         submit();
      else if ( mgr.buttonPressed("CANCEL") )
         cancel();
      
   }

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand           cmd;

      String val = mgr.readValue("VALIDATE");
      String txt = "";

      if ("CONTRACT".equals(val))
      {
         String contract = mgr.readValue("CONTRACT");

         cmd = trans.addCustomFunction( "CONT","Site_API.Get_Description", "DESCRIPTION" );
         cmd.addParameter( "CONTRACT", contract );

         cmd = trans.addCustomFunction("FNDUSR", "Fnd_Session_API.Get_Fnd_User", "FND_USER");

         cmd = trans.addCustomCommand("USERALL","User_Allowed_Site_API.Exist");
         cmd.addReference("FND_USER","FNDUSR/DATA");
         cmd.addParameter("CONTRACT", contract);

         trans = mgr.validate(trans);

         String site_desc = trans.getValue("CONT/DATA/DESCRIPTION");
         txt = ( mgr.isEmpty(site_desc) ? "" :site_desc) + "^";
      }
      mgr.responseWrite( txt );
      mgr.endResponse();
   }
   
   public void  submit()
   {
      ASPManager           mgr   = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand           cmd;
      String error_msg ="";

      if (mgr.isEmpty(mgr.readValue("CONTRACT")))
      {
         error_msg = mgr.translate("MPCCOWRETURNERRONEOUSACCDLGNOCON: The Site object does not exist.");
      }
      else if (mgr.isEmpty(mgr.readValue("DESCRIPTION")))
      {
         error_msg = mgr.translate("MPCCOWRETURNERRONEOUSACCDLGINVCON: The Site &1 does not exist.",mgr.readValue("CONTRACT"));
      }
      else
      {
         cmd = trans.addCustomCommand("REDOERROR","Mpccom_Accounting_API.Redo_Error_Bookings__");
         cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
         trans = mgr.perform(trans);
      }

      if (!mgr.isEmpty(error_msg))
      {
         try
         {
            appendDirtyJavaScript("   alert('",error_msg,"');\n");
         }
         catch (FndException e)
         {
         }
      }
     
      try
      {
         appendDirtyJavaScript("window.location =APP_ROOT+'Navigator.page?MAINMENU=Y&NEW=Y';\n");
      }
      catch (FndException e)
      {
      }
   }

   /**
    * Takes the user back to tree navigator.
    */
   public void  cancel()
   {
      ASPManager mgr = getASPManager();

      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableValidation();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("CONTRACT").
      setCustomValidation("CONTRACT","DESCRIPTION").
      setSize(8).
      setMaxLength(5).
      setLabel("MPCCOWRETURNERRONEOUSACCDLGCONTRACT: Site").
      setDynamicLOV("USER_ALLOWED_SITE_LOV").
      setLOVProperty("TITLE", mgr.translate("MPCCOWRETURNERRONEOUSACCDLGCONTRACT: Site")).
      setFunction("''");

      headblk.addField("DESCRIPTION").
      setSize(35).
      setReadOnly().
      setLabel("MPCCOWRETURNERRONEOUSACCDLGDESCRIPTION: Site Description").
      setFunction("''");

      headblk.addField("FND_USER").
      setHidden().
      setFunction("''");

      headblk.addField("ALLOWED").
      setHidden().
      setFunction("''");

      headblk.setView("");
      headbar = mgr.newASPCommandBar(headblk);
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.CUSTOM_LAYOUT);
      headlay.setEditable();
   }


   protected String getDescription()
   {
      return "MPCCOWRETURNERRONEOUSACCDLGTITLE1: Rerun Erroneous Accountings";
   }


   protected String getTitle()
   {
      return "MPCCOWRETURNERRONEOUSACCDLGTITLE2: Rerun Erroneous Accountings";
   }


   public void printContents() throws FndException
   {
      appendToHTML(headlay.show());

      printNewLine();
      beginDataPresentation();
      printSubmitButton("SUBMIT", "MPCCOWRETURNERRONEOUSACCDLGOKBUTTON:   OK    ", "");
      printSpaces(1);
      printSubmitButton("CANCEL", "MPCCOWRETURNERRONEOUSACCDLGCANCELBUTTON:  Cancel ", "");
      endDataPresentation(false);

      appendDirtyJavaScript("function validateContract(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkContract(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CONTRACT',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("	if( getValue_('CONTRACT',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("        	getField_('DESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	 r = __connect(\n");
      appendDirtyJavaScript("		APP_ROOT+ 'mpccow/RerunErroneousAccountingsDlg.page'+'?VALIDATE=CONTRACT'\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'CONTRACT',i,'Site') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('DESCRIPTION',i,0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");
   }
}
