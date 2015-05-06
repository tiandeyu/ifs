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
* File : DlgPersonInfoAddress.java
* Modified : 
* 2006-07-30 Maselk FIPL610 - Created.
* 2006-09-18 Maselk B139313,B139454,B139455 Modified PreDefine(),okFind().
* ----------------------------------------------------------------------------
*/

package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class DlgPersonInfoAddress extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.DlgPersonInfoAddress");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  DlgPersonInfoAddress (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
      if(mgr.buttonPressed("OK"))
      {
         saveReturn();
         return;
      }
      okFind();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind() 
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");

      q.addWhereCondition("PERSON_ID = ? ");
      q.addParameter("PERSON_ID",mgr.readValue("PERSON_ID"));
      q.addWhereCondition("ADDRESS_ID = ? ");
      q.addParameter("ADDRESS_ID",mgr.readValue("ADDRESS_ID"));
      q.addWhereCondition("PARTY = ? ");
      q.addParameter("PARTY",mgr.readValue("PARTY"));
      q.addWhereCondition("DEFAULT_DOMAIN = ? ");
      q.addParameter("DEFAULT_DOMAIN",mgr.readValue("DEFAULT_DOMAIN"));
      q.addWhereCondition("COUNTRY = ? ");
      q.addParameter("COUNTRY",mgr.readValue("COUNTRY"));
      q.addWhereCondition("PARTY_TYPE = ? ");
      q.addParameter("PARTY_TYPE",mgr.readValue("PARTY_TYPE"));

      mgr.querySubmit(trans,headblk);
      if ( headset.countRows() == 0 )
      {
         mgr.showAlert("ENTERWPERSONINFOADDRESSDLGNODATA: No data found.");
         headset.clear();
      }
      
   }

   public void saveReturn()
   {
      ASPManager mgr  = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      headset.changeRow();     
      mgr.submit(trans);
      trans.clear();

   }


   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("PERSON_ID").
              setHidden();
      headblk.addField("ADDRESS_ID").
              setHidden();
      headblk.addField("PARTY").
              setHidden();
      headblk.addField("DEFAULT_DOMAIN").
              setHidden();
      headblk.addField("COUNTRY").
              setHidden();
      headblk.addField("PARTY_TYPE").
              setHidden();
      headblk.addField("STREET").
              setInsertable().
              setLabel("ENTERWPERSONINFOADDRESSDLGSTREET: Street").
              setSize(50);
      headblk.addField("HOUSE_NO").
              setInsertable().
              setLabel("ENTERWPERSONINFOADDRESSDLGHOUSENO: House No").
              setSize(20);
      headblk.addField("FLAT_NO").
              setInsertable().
              setLabel("ENTERWPERSONINFOADDRESSDLGFLATNO: Flat No").
              setSize(20);
      headblk.addField("COMMUNITY").
              setInsertable().
              setLabel("ENTERWPERSONINFOADDRESSDLGCOMMUNITY: Community").
              setSize(35);
      headblk.addField("DISTRICT").
              setInsertable().
              setLabel("ENTERWPERSONINFOADDRESSDLGDISTRICT: District").
              setSize(35);
      headblk.setView("PERSON_INFO_ADDRESS");
      headblk.defineCommand("PERSON_INFO_ADDRESS_API","Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("ENTERWPERSONINFOADDRESSDLGTBLHEAD: Person Info Addresss");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
      disableBar();
      disableHomeIcon();
      disableOptions();
      disableNavigate();
      
      headbar.disableMinimize();
      headbar.disableCommand(headbar.PROPERTIES);
      headbar.disableCommand(headbar.SAVERETURN);
      headbar.disableCommand(headbar.CANCELEDIT);
   }

   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "ENTERWPERSONINFOADDRESSDLGDESC: Person Address Details";
   }


   protected String getTitle()
   {
      return "ENTERWPERSONINFOADDRESSDLGTITLE: Person Address Details";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      
      printSubmitButton("OK", "ENTERWPERSONINFOADDRESSDLGOK: Ok", "OnClick='javascript:window.close()'");
      this.printSpaces(1);
      printButton("CANCEL", "ENTERWPERSONINFOADDRESSDLGCANCEL: Cancel","OnClick='javascript:window.close()'");
      
   }
}
