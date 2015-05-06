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
*  File        : PostingTypesPerSysEventsOvw.java 
*  Modified    :
*       Cpeilk  25-May-2007 Made AUTHORIZE_ID hidden.
*	SenSlk	20-Apr-2007 Created. 
* ----------------------------------------------------------------------------
*/

package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class PostingTypesPerSysEventsOvw extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.PostingTypesPerSysEventsOvw");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private ASPQuery qry;

   //===============================================================
   // Construction 
   //===============================================================

   public PostingTypesPerSysEventsOvw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();   

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("IDENTITY")))
         okFind();
    
      adjust();
   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      String strCompany = null;

      qry = trans.addQuery(headblk);
      qry.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert("MPCCOWPOSTINGEVENTSPERSYSEVENTSOVWALERT: No data found.");
         headset.clear();
      }
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

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   

   public void adjust()
   {
      if (headset.countRows() == 0)
      {
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.BACK);
      }

   }  

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ASPField f;

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").
         setHidden();

      headblk.addField("EVENT_CODE").
         setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWSYSEVENTID: System Event ID").
         setReadOnly().
         setUpperCase();

      headblk.addField("DESCRIPTION").
	 setFunction("MPCCOM_SYSTEM_EVENT_API.Get_Description(:EVENT_CODE)").
         setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWDESCRIPTION: System Event Description");

      headblk.addField("AUTHORIZE_ID").
	 setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWAUTHORIZEID: Authorize ID").
         setHidden();

      headblk.addField("ONLINE_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSONLINEFLAGDB: Posting Online").
        setCheckBox("N,Y");

      headblk.addField("MATERIAL_ADDITION_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSMATERIALADDITIONFLAGDB: Material Addition").
	setMandatory().
        setCheckBox("N,Y");

      headblk.addField("OH1_BURDEN_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSOH1BURDENFLAGDB: Overhead Cost 1").
        setCheckBox("N,Y");

      headblk.addField("OH2_BURDEN_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSOH2BURDENFLAGDB: Overhead Cost 2").
        setCheckBox("N,Y");

      headblk.addField("SALES_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSSALESOVERHEADFLAGDB: Sales Overhead").
        setCheckBox("FALSE,TRUE");

      headblk.addField("MS_ADDITION_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSMSADDITIONFLAGDB: Administration Overhead").
        setCheckBox("N,Y");

      headblk.addField("LABOR_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWSPOSTEVENTSLABOROVERHEADFLAGDB: Default Labor Overhead").
        setCheckBox("N,Y");

      headblk.addField("GENERAL_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSGENERALOVERHEADFLAGDB: General Overhead").
        setCheckBox("N,Y");

      headblk.addField("DELIVERY_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSDELIVERYOVERHEADFLAGDB: Delivery Overhead").
        setCheckBox("N,Y");

      headblk.addField("CONSIGNMENT_EVENT").
	 setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWCONSEVENT: Consignment Event");
      
      headblk.addField("STR_CODE").
	 setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWSTRCODE: Posting Type");

      headblk.addField("STR_CODE_DESC").
	 setFunction("Posting_Ctrl_API.Get_Posting_Type_Desc(SITE_API.GET_COMPANY(USER_ALLOWED_SITE_API.Get_Default_Site),:STR_CODE)").
	 setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWSTRCODEDESC: Posting Type Description");

      headblk.addField("DEBIT_CREDIT").
	 setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWDEBITCREDIT: Debit/Credit");
      
      headblk.addField("PRE_ACCOUNTING_FLAG_DB").
	 setCheckBox("N,Y").
	 setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWPROJACCFLAGDB: Pre Posting");

      headblk.addField("PROJECT_ACCOUNTING_FLAG").
	 setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWPREACCFLAGDB: Project Posting");
      

      headblk.setView("ACC_EVENT_POSTING_TYPE_ALL");
      headblk.defineCommand("ACC_EVENT_POSTING_TYPE_API","");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.setBorderLines(false,true); 
      headbar.enableMultirowAction();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MPCCOWPOSTINGTYPESPERSYSEVENTSOVWTITLE: Query - Posting Types per System Event");
      headtbl.enableRowSelect();

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   }

//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "MPCCOWPOSTINGTYPESPERSYSEVENTSOVWTITLE: Query - Posting Types per System Event";
   }

   protected String getTitle()
   {
      return "MPCCOWPOSTINGTYPESPERSYSEVENTSOVWTITLE: Query - Posting Types per System Event";
   }

   protected void printContents() throws FndException
   { 
     if(headlay.isVisible()) 
       appendToHTML(headlay.show());
   }
}
