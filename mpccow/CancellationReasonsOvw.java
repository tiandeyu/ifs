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
*  File        : CancellationReasonsOvw.java 
*  Description : Basic Data - Cancellation Reasons
*  Notes       :
* ----------------------------------------------------------------------------
*  Modified    :
*     Cpeilk    2007-06-12 - Created.
* ----------------------------------------------------------------------------
*/


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CancellationReasonsOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.CancellationReasonsOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout lay;

   //===============================================================
   // Construction 
   //===============================================================
   public CancellationReasonsOvw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager(); 

      if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if ( mgr.dataTransfered() )
         okFind();
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();

      adjust();

   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addEmptyCommand("HEAD","ORDER_CANCEL_REASON_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);

      lay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
      {
         mgr.showAlert("MPCCOWCANCELLATIONREASONOVWNODATA: No data found.");
         headset.clear();
      }
      mgr.createSearchURL(headblk);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();   

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("CANCEL_REASON").
      setSize(14).
      setMaxLength(10).
      setMandatory().
      setLabel("MPCCOWCANCELLATIONREASONOVWCANCELREASON: Cancellation Reason").
      setUpperCase().
      setReadOnly().
      setInsertable();

      headblk.addField("REASON_DESCRIPTION").
      setSize(30).
      setMaxLength(100).
      setMandatory().
      setInsertable().
      setLabel("MPCCOWCANCELLATIONREASONOVWREASONDESC: Cancellation Reason Description");

      headblk.setView("ORDER_CANCEL_REASON");
      headblk.defineCommand("ORDER_CANCEL_REASON_API","New__,Modify__,Remove__");
      lay = headblk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);

      headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("MPCCOWCANCELLATIONREASONOVWCANCELREASONS: Cancellation Reasons"));
   }


   public void  adjust()
   {
      if ( headset.countRows() == 0 )
      {
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.DELETE);     
         headbar.disableCommand(headbar.DUPLICATEROW);
      }
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "MPCCOWCANCELLATIONREASONOVWTITLE: Cancellation Reasons";
   }

   protected String getTitle()
   {
      return "MPCCOWCANCELLATIONREASONOVWTITLE: Cancellation Reasons";
   }

   protected void printContents() throws FndException
   {
      appendToHTML(lay.show());
   }
}
