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
*  File        : CompanyEmpOvw.java 
*  Modified    : Anil Padmajeewa
*    ASP2JAVA Tool  2001-01-26  - Created Using the ASP file CompanyEmpOvw.asp
*  Modified    :
*  Madhu       : 2001-05-28   Call # 65349
*  MAWELK      : 2005-04-09   FIPR364 - Corrected of web tags.
*  Jakalk      : 2005-09-06 - Code Cleanup, Removed doReset and clone methods.
*  Thpelk      : 2007-08-02 - Call Id 146997, Corrected EMPLOYEE_ID to allow only 11 characters.
* ----------------------------------------------------------------------------
*/

package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CompanyEmpOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CompanyEmpOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;
   private ASPBlock companyEmpblk;
   private ASPRowSet companyEmpset;
   private ASPCommandBar companyEmpbar;
   private ASPTable companyEmptbl;
   private ASPBlockLayout companyEmplay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private String title;
   private ASPQuery qry;
   private ASPCommand cmd;
   private ASPBuffer data;
   private int currrow;

   //===============================================================
   // Construction 
   //===============================================================

   public CompanyEmpOvw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      title = ctx.readValue("TITLE","");

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      adjust();
      ctx.writeValue("TITLE", title);

   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(companyEmpblk);
      qry.setOrderByClause("PERSON_ID");
      qry.includeMeta("ALL");

      mgr.querySubmit(trans,companyEmpblk);

      if (companyEmpset.countRows() == 0)
      {
         mgr.showAlert("ENTERWCOMPANYEMPOVWNODATA: No data found.");
         companyEmpset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(companyEmpblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      companyEmplay.setCountValue(toInt(companyEmpset.getValue("N")));
      companyEmpset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("COMPANYEMP","Company_Emp_API.New__",companyEmpblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("COMPANYEMP/DATA");
      companyEmpset.addRow(data);
   }


   public void  saveReturn()
   {
      ASPManager mgr = getASPManager();

      companyEmpset.changeRow();
      currrow = companyEmpset.getCurrentRowNo();
      String person_id = companyEmpset.getValue("PERSON_ID");

      if (mgr.isEmpty(person_id))
      {
         mgr.showAlert(mgr.translate("ENTERWCOMPANYEMPOVWDUEDA1: The field [Person Id] must have a value."));
         companyEmplay.setLayoutMode(companyEmplay.NEW_LAYOUT);
      }
      else
         mgr.submit(trans);

      companyEmpset.goTo(currrow);
   }


   public void  details()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer keys = null;

      companyEmpset.selectRows();
      currrow = companyEmpset.getCurrentRowNo();
      companyEmpset.setFilterOn();
      keys = companyEmpset.getSelectedRows("PERSON_ID");
      mgr.transferDataTo("PersonInfo.page",keys);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      companyEmpblk = mgr.newASPBlock("COMPANYEMP");

      companyEmpblk.addField("OBJID").
         setHidden();

      companyEmpblk.addField("OBJVERSION").
         setHidden();

      companyEmpblk.addField("COMPANY").
         setLabel("ENTERWCOMPANYEMPOVWCOMPANY: Company").
         setSize(15).
         setMandatory().
         setHidden().
         setGlobalConnection("COMPANY").
         setUpperCase();

      companyEmpblk.addField("EMPLOYEE_ID").
         setLabel("ENTERWCOMPANYEMPOVWEMPLOYEEID: Employee Id.").
         setSize(27).
         setMaxLength(11).
         setMandatory().							   
         setReadOnly().
         setInsertable().
         setUpperCase();

      companyEmpblk.addField("PERSON_ID").
         setLabel("ENTERWCOMPANYEMPOVWPERSONID: Person Id.").
         setMandatory().
         setSize(27).
         setDynamicLOV("PERSON_INFO",650,450).
         setReadOnly().
         setInsertable().
         setUpperCase();

      companyEmpblk.addField("SNAME").
         setLabel("ENTERWCOMPANYEMPOVWSNAME: Name").
         setSize(27).
         setReadOnly().
         setInsertable().
         setFunction("PERSON_INFO_API.Get_Name(:PERSON_ID)");
      mgr.getASPField("PERSON_ID").setValidation("SNAME");  

      companyEmpblk.addField("EXPIRE_DATE", "Date").
         setLabel("ENTERWCOMPANYEMPOVWEXPIRE_DATE: Expire Date"). 
         setSize(27).
         setReadOnly().
         setInsertable();

      companyEmpblk.setView("COMPANY_EMP");
      companyEmpblk.defineCommand("COMPANY_EMP_API","New__,Modify__,Remove__");
      companyEmpset = companyEmpblk.getASPRowSet();

      companyEmpbar = mgr.newASPCommandBar(companyEmpblk);
      companyEmpbar.addCustomCommand("details", mgr.translate("ENTERWCOMPANYEMPOVWDTAIL: Details..."));
      companyEmpbar.disableCommand(companyEmpbar.EDITROW);
      companyEmpbar.disableCommand(companyEmpbar.SAVENEW);
      companyEmpbar.defineCommand(companyEmpbar.SAVERETURN,"saveReturn");

      companyEmptbl = mgr.newASPTable(companyEmpblk);
      companyEmptbl.setTitle("ENTERWCOMPANYEMPOVWTITLE: Employees Overview");

      companyEmplay = companyEmpblk.getASPBlockLayout();
      companyEmplay.setFieldOrder("EMPLOYEE_ID");
      companyEmplay.setDefaultLayoutMode(companyEmplay.MULTIROW_LAYOUT);
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      title = ctx.findGlobal("COMPANY","") + " - "+ mgr.translate("ENTERWCOMPANYEMPOVWTITLE: Employees Overview"); 

      if (companyEmpset.countRows() == 0 && companyEmplay.getLayoutMode() == 5)
         companyEmpbar.disableCommand(companyEmpbar.DELETE);

      if (companyEmpset.countRows() == 0 )
      {
         companyEmpbar.disableCommand(companyEmpbar.FORWARD);
         companyEmpbar.disableCommand(companyEmpbar.BACKWARD);
         companyEmpbar.disableCommand(companyEmpbar.DELETE);
         companyEmpbar.disableCommand(companyEmpbar.DUPLICATEROW);
         companyEmpbar.disableCommand(companyEmpbar.EDITROW);
         companyEmpbar.disableCommand(companyEmpbar.BACK);
         companyEmpbar.removeCustomCommand("details");
      }
   }

//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return title;
   }

   protected String getTitle()
   {
      return "ENTERWCOMPANYEMPOVWTITLE: Employees Overview";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("ENTERWCOMPANYEMPOVWTITLE: Employees Overview"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      out.append(mgr.startPresentation(title));
      out.append(companyEmplay.show());
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
}
