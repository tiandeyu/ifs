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
*  File        : IncomeType.java 
*  Author      : Anil Padmajeewa
*  Date	      : 01/03/08
*  Modified    :
*  KuPelk      : 2003-03-03   ARFI124N 
*  MAWELK      : 2005-04-09   FIPR364 - Corrected of web tags.
*  Jakalk      : 2005-09-06 - Code Cleanup, Removed doReset and clone methods.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class IncomeType extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.IncomeType");

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
   private ASPQuery qry;
   private String titleStr;

   //===============================================================
   // Construction 
   //===============================================================

   public IncomeType(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes

      trans   = null;
      qry   = null;
      titleStr = null;

      super.doReset();
   }


   public ASPPoolElement clone(Object obj) throws FndException
   {
      IncomeType page = (IncomeType)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.qry   = null;
      page.titleStr = null;

      // Cloning immutable attributes   
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.headblk = page.getASPBlock(headblk.getName());

      return page;
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();

      if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());

      adjust();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      mgr.createSearchURL(headblk);

      trans.clear();
      qry = trans.addQuery(headblk);
      qry.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);

      if ( headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("ENTERWINCOMETYPENODATA: No data found."));
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


   public void newRow()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer data = null;

      ASPCommand cmd = trans.addEmptyCommand("HEAD","INCOME_TYPE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);

      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").       
         setHidden();

      headblk.addField("INTERNAL_INCOME_TYPE").
         setSize(20).
         setHidden();

      headblk.addField("COUNTRY_CODE").
         setMandatory().
         setSize(2).
         setDynamicLOV("ISO_COUNTRY", 600,445).
         setLabel("ENTERWINCOMETYPECOUNTCODE: Country Code");

      headblk.addField("INCOME_TYPE_ID").
         setMandatory().
         setSize(20).
         setReadOnly().
         setInsertable().
         setLabel("ENTERWINCOMETYPEIMCOMETYPEID: Income Type ID");

      headblk.addField("DESCRIPTION").
         setSize(50).
         setLabel("ENTERWINCOMETYPEDESCRIPTION: Description");

      headblk.addField("CURRENCY_CODE").
         setSize(3).
         setMandatory().
         setDynamicLOV("ISO_CURRENCY", 600,445).
         setReadOnly().
         setInsertable().
         setLabel("ENTERWINCOMETYPETHCURRCODE: Threshold Currency Code");

      headblk.addField("THRESHOLD_AMOUNT", "Money").
         setMandatory().
         setLabel("ENTERWINCOMETYPETHRESHAMOUNT: Threshold Amount");

      headblk.addField("INCOME_REPORTING_CODE").
         setSize(20).
         setLabel("ENTERWINCOMETYPEINCOMEREPORTCODE: Income Reporting Code");

      headblk.addField("BLOCKED").
         setCheckBox("FALSE,TRUE").
         setSize(05).
         setHidden();

      headblk.setView("INCOME_TYPE");
      headblk.defineCommand("INCOME_TYPE_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows() == 0 )
      {
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.BACK);
      }

      titleStr = mgr.translate("ENTERWINCOMETYPEINCOMETITLE: Income Type");
   }

//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return titleStr;
   }

   protected String getTitle()
   {
      return "ENTERWINCOMETYPESUPPLIERTITLE: Supplier Income Type";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(titleStr));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(titleStr));

      if (headlay.isVisible() )
         out.append(headlay.show());
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");

      return out;
   }
}     