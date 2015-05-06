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
*  File        : CustomerInfoOvw.java 
*  Modified    : Anil Padmajeewa
*                anpalk    21 Feb 2001    Code Review, Rename the Block descriptivelly
*    ASP2JAVA Tool  2001-01-22  - Created Using the ASP file CustomerInfoOvw.asp
*    Disali K  : 2001-04-27 - Modified the syntax of the "if" comdition that check wether 
*                             Global company is equal to the company coming form portlet CustomerCreditAlert.java
*                             in "okFind" (correct the syntax of comparing two strings  )
*   KuPelk     : 2004-08-17   Added multirow Select.
*  Jakalk      : 2005-09-06   Code Cleanup, Removed doReset and clone methods.
*  Maselk      : 2006-12-27   Merged LCS Bug 58216, Fixed SQL Injection threats.
*  Thpelk      : 2007-08-02   Call Id 146997, Corrected CUSTOMER_ID to allow only 20 characters.
* ----------------------------------------------------------------------------
*/

package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CustomerInfoOvw extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CustomerInfoOvw");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;
   private ASPBlock customerInfoblk;
   private ASPRowSet customerInfoset;
   private ASPCommandBar customerInfobar;
   private ASPTable customerInfotbl;
   private ASPBlockLayout customerInfolay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private ASPQuery qry;

   //===============================================================
   // Construction 
   //===============================================================

   public CustomerInfoOvw(ASPManager mgr, String page_path)
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
      else if (!mgr.isEmpty(mgr.getQueryStringValue("CUSTOMER_ID")))
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

      qry = trans.addQuery(customerInfoblk);

      if (!mgr.isEmpty(mgr.getQueryStringValue("IDENTITY")))
      {
         strCompany = mgr.readValue("COMPANY");
         if (!ctx.findGlobal("COMPANY","!@#").equals(strCompany))
            mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"enterw/DefineCompany.page?COMPANY=" + strCompany);

         trans.clear();
         qry = trans.addEmptyQuery(customerInfoblk);
         qry.addWhereCondition("CUSTOMER_ID = ?");
         qry.addParameter("CUSTOMER_ID", mgr.readValue("IDENTITY"));
      }

      qry.setOrderByClause("CUSTOMER_ID");
      qry.includeMeta("ALL");
      mgr.querySubmit(trans,customerInfoblk);

      if (customerInfoset.countRows() == 0)
      {
         mgr.showAlert("ENTERWCUSTOMERINFOOVWNODATA: No data found.");
         customerInfoset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(customerInfoblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      customerInfolay.setCountValue(toInt(customerInfoset.getValue("N")));
      customerInfoset.clear();
   }

   public void  newRow()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer data = null;
      ASPCommand cmd = null;

      cmd = trans.addEmptyCommand("MAIN", "Customer_Info_API.New__", customerInfoblk);
      cmd.setOption("ACTION", "PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MAIN/DATA");
      customerInfoset.addRow(data);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  details()
   {
      ASPManager mgr = getASPManager();
      int selectedRowForRMB = 0;

      if ( customerInfolay.isSingleLayout() )
      {
         selectedRowForRMB = customerInfoset.getCurrentRowNo();
         customerInfoset.goTo(selectedRowForRMB);
         mgr.transferDataTo("CustomerInfo.page",customerInfoset.getSelectedRows("CUSTOMER_ID"));
      }
      else
      {
         customerInfoset.store();
         mgr.transferDataTo("CustomerInfo.page",customerInfoset.getSelectedRows("CUSTOMER_ID"));
      }
   } 

   public void adjust()
   {
      if (customerInfoset.countRows() == 0)
      {
         customerInfobar.disableCommand(customerInfobar.FORWARD);
         customerInfobar.disableCommand(customerInfobar.BACKWARD);
         customerInfobar.disableCommand(customerInfobar.DELETE);
         customerInfobar.disableCommand(customerInfobar.DUPLICATEROW);
         customerInfobar.disableCommand(customerInfobar.EDITROW);
         customerInfobar.disableCommand(customerInfobar.BACK);
         customerInfobar.removeCustomCommand("details");

      }

   }  

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ASPField f;

      customerInfoblk = mgr.newASPBlock("CUSTOMER");

      customerInfoblk.addField("OBJID").
         setHidden();

      customerInfoblk.addField("OBJVERSION").
         setHidden();

      customerInfoblk.addField("CUSTOMER_ID").
         setSize(25).
         setMaxLength(20).
         setMandatory().
         setLabel("ENTERWCUSTOMERINFOOVWCUSTOMERID: Identity").
         setReadOnly().
         setInsertable().
         setUpperCase();

      customerInfoblk.addField("NAME").
         setSize(25).
         setMandatory().
         setLabel("ENTERWCUSTOMERINFOOVWNAME: Name");

      customerInfoblk.addField("ASSOCIATION_NO").
         setSize(25).
         setLabel("ENTERWCUSTOMERINFOOVWASSOCIATIONNO: Association No");

      customerInfoblk.addField("DEFAULT_LANGUAGE").
         setSize(15).
         enumerateValues("ISO_LANGUAGE_API").
         setSelectBox().
         unsetSearchOnDbColumn().
         setLabel("ENTERWCUSTOMERINFOOVWDEFAULTLANGUAGE: Default Language");

      customerInfoblk.addField("COUNTRY").
         setSize(12).
         enumerateValues("ISO_COUNTRY_API").
         setSelectBox().
         unsetSearchOnDbColumn().
         setLabel("ENTERWCUSTOMERINFOOVWCOUNTRY: Country");

      customerInfoblk.addField("CREATION_DATE","Date").
         setSize(25). 
         setMandatory().
         setReadOnly().
         setLabel("ENTERWCUSTOMERINFOOVWCREATIONDATE: Creation Date");

      customerInfoblk.addField("PARTY").
         setSize(8).
         setHidden().
         setLabel("ENTERWCUSTOMERINFOOVWPARTY: Party");

      customerInfoblk.addField("PARTY_TYPE").
         setSize(8).
         setMandatory().
         setHidden().
         setLabel("ENTERWCUSTOMERINFOOVWPARTYTYPE: Party Type");

      customerInfoblk.addField("DEFAULT_DOMAIN").
         setSize(8).
         setMandatory().
         setHidden().
         setLabel("ENTERWCUSTOMERINFOOVWDEFAULTDOMAIN: Default Domain");

      customerInfoblk.setView("CUSTOMER_INFO");
      customerInfoblk.defineCommand("CUSTOMER_INFO_API","New__,Modify__,Remove__");
      customerInfoset = customerInfoblk.getASPRowSet();

      customerInfobar = mgr.newASPCommandBar(customerInfoblk);
      customerInfobar.setBorderLines(false,true); 
      customerInfobar.addCustomCommand("details", mgr.translate("ENTERWCUSTOMERINFOOVWDETAILS: Details..."));
      customerInfobar.enableMultirowAction();

      customerInfotbl = mgr.newASPTable(customerInfoblk);
      customerInfotbl.setTitle("ENTERWCUSTOMERINFOOVWCUSTOMEROVW: Customer Overview");
      customerInfotbl.enableRowSelect();

      customerInfolay = customerInfoblk.getASPBlockLayout();
      customerInfolay.setDefaultLayoutMode(customerInfolay.MULTIROW_LAYOUT);
   }

//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "ENTERWCUSTOMERINFOOVWTITLE: Customer Overview";
   }

   protected String getTitle()
   {
      return "ENTERWCUSTOMERINFOOVWTITLE: Customer Overview";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("ENTERWCUSTOMERINFOOVWTITLE: Customer Overview"));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("ENTERWCUSTOMERINFOOVWTITLE: Customer Overview"));
      out.append(customerInfolay.show());
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
}
