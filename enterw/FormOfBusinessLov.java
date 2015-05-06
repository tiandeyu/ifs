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
*  File        : FormOfBusinessLov.java.java
*  Modified    :
* -------------------- Wings Merge Start -------------------------------------------
*     2006-10-30  Haunlk  Created.
*     2007-01-31  Haunlk  Merged Wings Code.
* -------------------- Wings Merge End ----------------------------------------------
*
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class FormOfBusinessLov extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.FormOfBusinessLov");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock headblk;
   private ASPTable headtbl;
   private ASPCommandBar headbar;
   private ASPBlockLayout headlay;
   private ASPRowSet headset;

   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private String sCountryDB;

   //===============================================================
   // Construction
   //===============================================================
   public FormOfBusinessLov(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      sCountryDB = ctx.readValue("COUNTRY_DB", mgr.getQueryStringValue("COUNTRY_DB"));

      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else
         okFind();

      ctx.writeValue("COUNTRY_DB", sCountryDB);
   }

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q = trans.addQuery(headblk);
      q.addWhereCondition("COUNTRY_CODE = ? ");
      q.addParameter("COUNTRY_DB", sCountryDB);
      q.includeMeta("ALL");

      trans = mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
        mgr.showAlert(mgr.translate("ENTERWFORMOFBUSINESSLOVNODATA: No data found."));
        headset.clear();
      }
   }

   public void countFind()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("COUNTRY_CODE = ? ");
      q.addParameter("COUNTRY_DB", sCountryDB);
      mgr.submit(trans);

      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }



   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.setView("CORPORATE_FORM");

      headblk.addField("CORPORATE_FORM").
         setSize(25).
         setLabel("ENTERWFORMOFBUSINESSLOVCORPORATEFORM: Form Of Business");

      headblk.addField("CORPORATE_FORM_DESC").
         setSize(25).
         setLabel("ENTERWFORMOFBUSINESSLOVCORPORATEFORMDESC: Description");

     headblk.addField("COUNTRY_DB").
         setLabel(" ").
         setFunction("NULL").
         unsetQueryable().
         setReadOnly();


      headblk.addField("DATA_OUT").
         setHidden().
         setFunction("CORPORATE_FORM").
         setSize(10);

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("ENTERWFORMOFBUSINESSLOVTITLE: Form of Business");
      headtbl.setKey("DATA_OUT");
      headtbl.disableQuickEdit();

      headset = headblk.getASPRowSet();

      disableHelp();
      disableNavigate();
      disableHomeIcon();
      disableOptions();
      disableFooter();
      disableHeader();
      disableConfiguration();

      headbar = headblk.newASPCommandBar();

      headtbl.disableEditProperties();
      headtbl.disableRowCounter();
      headtbl.disableQuickEdit();

      headbar.setCounterDbMode();
      headbar.disableCommand(ASPCommandBar.NEWROW);
      headbar.disableMinimize();

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   }

   protected String getDescription()
   {
      return ("ENTERWFORMOFBUSINESSLOVDSECRIPTION: IFS/Applications - List of values");
   }


   protected String getTitle()
   {
      return ("ENTERWFORMOFBUSINESSLOVMAINTITLE: IFS/Applications - List of Form of Business");
   }

   protected void printContents() throws FndException
   {
      appendToHTML(headbar.showBar());
      if( headlay.isMultirowLayout())
         appendToHTML( headtbl.populateLov() );
      else
         appendToHTML( headlay.generateDialog());
   }
}


