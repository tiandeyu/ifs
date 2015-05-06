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
*  File        : Module.java
*  Modified    :
*   SlawekF 2001-06-06 Created
*   Chandana2003-09-04 Disabled Home menu.
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Module extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.Module");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private String n;


   //===============================================================
   // Construction
   //===============================================================
   public Module(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      q   = null;
      n   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      Module page = (Module)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.q   = null;
      page.n   = null;

      // Cloning immutable attributes
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();

// Module window should be displayed in separate browser so no need to navigate or configure from here
      page.disableNavigate();
      page.disableConfiguration();

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else{
          okFind();
      }

   }

//-----------------------------------------------------------------------------
//-----------------------------  MISC FUNCTIONS     ---------------------------
//-----------------------------------------------------------------------------

   public void  clear()
   {
      headset.clear();
      headtbl.clearQueryRow();
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("VERSION IS NOT NULL");
      mgr.submit(trans);
      n = headset.getRow().getValue("N");
      headlay.setCountValue(toInt(n));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.addWhereCondition("VERSION IS NOT NULL");
      q.setOrderByClause("MODULE");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
      {
         mgr.showAlert("FNDPAGESAGENDANODATA: No data found.");
      }
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("Module");

      headblk.addField("MODULE").
              setSize(8).
              setLabel("FNDPAGESMODULEMODULE: Module");

      headblk.addField("NAME").
              setSize(32).
              setLabel("FNDPAGESMODULENAME: Name");

      headblk.addField("VERSION").
              setSize(16).
              setLabel("FNDPAGESMODULEVERSION: Version");

      headblk.setView("MODULE");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);

// we want to browse in overview mode only
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.CANCELFIND);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.VIEWDETAILS);
      headbar.disableCommand(headbar.EDITROW);

// Module window should be displayed in separate browser so no need to navigate or configure from here
      disableHomeIcon();
      disableNavigate();
      disableConfiguration();

      headtbl = mgr.newASPTable(headblk);

// Module window should be displayed in separate browser and we don't want to customize from here
// as it can cause unwanted behaviour
      headtbl.disableOutputChannels(); 
      headtbl.disableEditProperties();

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESMODULELIST: Installed Modules";
   }

   protected String getTitle()
   {
      return "FNDPAGESMODULELIST: Installed Modules";
   }

   protected void printContents() throws FndException
   {
      appendToHTML(headlay.show());
   }

}
