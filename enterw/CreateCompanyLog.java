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
 *  File                    : CreateCompanyLog.java
 *  Description             :
 *  Notes                   :
 *  ---------------------------- Wings Merge Start ---------------------------------------
 *  Created    : 2007-01-19   RuRalk   Created.
 *               2007-01-25   Haunlk   Modified the duplicating Translation Constants.
 *               2007-01-31   Haunlk   Merged Wings Code.
 *  --------------------------- Wings Merge End ----------------------------------------
 * ----------------------------------------------------------------------------
*/
package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreateCompanyLog extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CreateCompanyLog");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext      ctx;
   private ASPLog log;
   private ASPBlock        headblk;
   private ASPRowSet       headset;
   private ASPCommandBar   headbar;
   private ASPTable        headtbl;
   private ASPBlockLayout  headlay;



   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery qry;
   private int i;
   private int arraySize;

   private String val;
   private ASPCommand cmd;
   private ASPBuffer buf;
   private ASPQuery q;
   private ASPBuffer data;

   //===============================================================
   // Construction
   //===============================================================
   public CreateCompanyLog(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {

      ASPManager mgr = getASPManager();
      ctx   = mgr.getASPContext();

      trans = mgr.newASPTransactionBuffer();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("COMPANY")))
         okFind();


      adjust();

   }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(headblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);
      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("ENTERWCREATECOMPANYLOGNODATA: No data found."));
         headset.clear();
      }
      mgr.createSearchURL(headblk);
      eval(headset.syncItemSets());
   }

   public void adjust()
   {
   }


   protected void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("COMPANY").
      setUpperCase().
      setSize(10).
      setLabel("ENTERWCREATECOMPANYLOGCREATECOMPANYLOG: Company").
      setReadOnly();

      headblk.addField("MODULE").
      setLabel("ENTERWCREATECOMPANYLOGMODULE: Module").
      setSize(10);

      headblk.addField("STATUS").
      setLabel("ENTERWCREATECOMPANYLOGSTATUS: Status").
      setReadOnly().
      setSize(20);

      headblk.setView("CREATE_COMPANY_LOG3");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);

      headtbl = mgr.newASPTable( headblk );
      headtbl.setTitle(mgr.translate("ENTERWCREATECOMPANYLOGHEADBLKTITLE: Create Company Component Log"));

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWCREATECOMPANYLOGDESCRIPTION: Create Company Component Log";
   }

   protected String getTitle()
   {
      return "ENTERWCREATECOMPANYLOGTITLEDES: Create Company Component Log";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(headlay.show());


   }

}
