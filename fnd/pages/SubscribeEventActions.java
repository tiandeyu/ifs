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
 * File        : SubscribeEventActions.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Suneth M 2001-Oct-09 - Created.
 *    Suneth M 2001-Oct-22 - Replaced SUBSCRIBED check box from a field with values YES/NO.
 *    ChandanaD2003-Sep-17 - Changed the Title to 'Subscriptions'.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class SubscribeEventActions extends ASPPageProvider
{
   /* Static constants */

   public static boolean DEBUG = Util.isDebugEnabled("ifs.orderw.SubscribeEventActions");

   /* Instances created on page creation (immutable attributes) */

   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar cmdbar;
   private ASPTable tbl;
   private ASPBlockLayout lay;

   /* Transient temporary variables (never cloned) */

   private ASPTransactionBuffer trans;
   private ASPQuery q;

   /* Construction */

   public SubscribeEventActions(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   protected void doReset() throws FndException
   {
      /* Resetting mutable attributes */
      
      trans   = null;
      q   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      SubscribeEventActions page = (SubscribeEventActions)(super.clone(obj));

      /* Initializing mutable attributes */
      
      page.trans   = null;
      page.q   = null;
      
      /* Cloning immutable attributes */
      
      page.frm = page.getASPForm();
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();
      page.blk = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.cmdbar = page.blk.getASPCommandBar();
      page.tbl = page.getASPTable(tbl.getName());
      page.lay = page.blk.getASPBlockLayout();

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
      
      frm  = mgr.getASPForm();
      fmt  = mgr.newASPHTMLFormatter();
      ctx  = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      
      adjust();
   }


   /* Command Bar Search Group functions */

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(blk);
      q.includeMeta("ALL");
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      
      lay.setCountValue(toInt(rowset.getValue("N")));
      rowset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(blk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,blk);
      
      if (rowset.countRows() == 0) 
      {
         rowset.clear();
         mgr.showAlert(mgr.translate("FNDPAGESUBSCRIBEEVENTACTIONSNODATA: No data found."));
      }
      mgr.createSearchURL(blk);
   }


   /* CMDBAR CUSTOM FUNCTIONS */

   public void  subscribeAction()
   {
      ASPManager mgr = getASPManager();
      
      if (lay.isMultirowLayout()) 
      {
         rowset.selectRows();
         rowset.setFilterOn();
      } 
      String sEventLuName = rowset.getValue("EVENT_LU_NAME");
      String sEventId     = rowset.getValue("EVENT_ID");
      String sActionNo    = rowset.getValue("ACTION_NUMBER");
      if (lay.isMultirowLayout()) 
         rowset.setFilterOff();

      ASPCommand  cmdbuf = mgr.newASPCommand();

      cmdbuf.defineCustomFunction("Fnd_Session_API.Get_Fnd_User", "FND_USER");
      trans.addCommand("SESSION", cmdbuf);     
                                                    
      cmdbuf = trans.addCustomCommand("SUBS", "Fnd_Event_Action_Subscribe_API.Subscribe");
      cmdbuf.addParameter("EVENT_LU_NAME",sEventLuName);
      cmdbuf.addParameter("EVENT_ID",sEventId);
      cmdbuf.addParameter("ACTION_NUMBER",sActionNo);
      cmdbuf.addReference("IDENTITY","SESSION/DATA","FND_USER");
      mgr.perform(trans);
      rowset.refreshRow();
   }


   public void  unsubscribeAction()
   {
      ASPManager mgr = getASPManager();

      if (lay.isMultirowLayout()) 
      {
         rowset.selectRows();
         rowset.setFilterOn();
      }
      String sEventLuName = rowset.getValue("EVENT_LU_NAME");
      String sEventId     = rowset.getValue("EVENT_ID");
      String sActionNo    = rowset.getValue("ACTION_NUMBER");
      if (lay.isMultirowLayout()) 
         rowset.setFilterOff();

      ASPCommand  cmdbuf = mgr.newASPCommand();

      cmdbuf.defineCustomFunction("Fnd_Session_API.Get_Fnd_User", "FND_USER");
      trans.addCommand("SESSION", cmdbuf);     
                                                    
      cmdbuf = trans.addCustomCommand("SUBS", "Fnd_Event_Action_Subscribe_API.Unsubscribe");
      cmdbuf.addParameter("EVENT_LU_NAME",sEventLuName);
      cmdbuf.addParameter("EVENT_ID",sEventId);
      cmdbuf.addParameter("ACTION_NUMBER",sActionNo);
      cmdbuf.addReference("IDENTITY","SESSION/DATA","FND_USER");
      mgr.perform(trans);
      rowset.refreshRow();
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      setVersion(3);
      
      blk = mgr.newASPBlock("MAIN");
      
      blk.addField("OBJID").
          setHidden();
      
      blk.addField("EVENT_LU_NAME").
          setHidden();

      blk.addField("EVENT_ID").
          setHidden();

      blk.addField("ACTION_NUMBER").
          setHidden();
      
      blk.addField("FND_EVENT_ACTION_TYPE").
          setLabel("FNDEVEACTTYPEOFMESS: Type of Message");

      blk.addField("DESCRIPTION").
          setLabel("FNDEVEACTDESCRIPTION: Description");

      blk.addField("SUBSCRIBED").
          setCheckBox("FALSE,TRUE").
          setHidden();

      blk.addField("SUBSCRIBED_TXT").
          setFunction("decode(SUBSCRIBED,'TRUE','"+mgr.translate("FNDEVEACTSUBSYES: YES")+"','"+mgr.translate("FNDEVEACTSUBSNO: NO")+"')").
          setLabel("FNDEVEACTSUBSCRIBED: Subscribed");

      blk.addField("FND_EVENT_ACTION_TYPE_DB").
          setHidden();

      blk.addField("FND_USER").
          setFunction("''").
          setHidden();

      blk.addField("IDENTITY").
          setFunction("''").
          setHidden();

      blk.setView("FND_EVENT_ACTION_SUBSCRIBABLE");
      rowset = blk.getASPRowSet();
      
      cmdbar = mgr.newASPCommandBar(blk);
      cmdbar.addCustomCommand("subscribeAction","FNDSUBEVEACTSUBSC: Subscribe");
      cmdbar.addCustomCommand("unsubscribeAction","FNDSUBEVEACTUNSUBSC: Unsubscribe");
      cmdbar.addCommandValidConditions("subscribeAction","SUBSCRIBED","Enable","FALSE");
      cmdbar.addCommandValidConditions("unsubscribeAction","SUBSCRIBED","Enable","TRUE");
      
      tbl = mgr.newASPTable( blk );
      tbl.setTitle(mgr.translate("FNDSUBEVEACTTBLDESC: Subscribe Event Actions"));
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
   }

   public void  adjust()
   {
      cmdbar.removeCustomCommand(cmdbar.DUPLICATEROW);
      cmdbar.removeCustomCommand(cmdbar.DELETE);
      cmdbar.removeCustomCommand(cmdbar.NEWROW);
      cmdbar.removeCustomCommand(cmdbar.EDITROW);
   }

   /* HTML */

   protected String getDescription()
   {
      return "FNDSUBEVEACTDESC: Subscriptions";
   }

   protected String getTitle()
   {
      return "FNDSUBEVEACTDESC: Subscriptions";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (lay.isVisible())
      {
         appendToHTML(lay.show());
      }
   }
}
