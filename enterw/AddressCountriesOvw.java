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
*  File        : AddressCountriesOvw.java 
*
*  Created     : Gepelk      09/01/2002 IID ARFI124N - Argentinean Sales Tax
*  Modify      :
*  MAWELK      : 2005-04-09 - FIPR364 - Corrected web tags.
*  Jakalk      : 2005-09-06 - Code Cleanup, Removed doReset and clone methods.
*  Maselk      : 2006-07-31 - FIPL610 - Added check box Detailed Address.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class AddressCountriesOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.AddressCountriesOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;
   private ASPBlock addressCountryblk;
   private ASPRowSet addressCountryset;
   private ASPCommandBar addressCountrybar;
   private ASPTable addressCountrytbl;
   private ASPBlockLayout addressCountrylay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private String title;
   private ASPQuery qry;
   private ASPCommand cmd;
   private ASPBuffer data;
   //private int currrow;

   //===============================================================
   // Construction 
   //===============================================================

   public AddressCountriesOvw(ASPManager mgr, String page_path)
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

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------
//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(addressCountryblk);
      qry.includeMeta("ALL");
      mgr.querySubmit(trans,addressCountryblk);

      if (addressCountryset.countRows() == 0)
      {
         mgr.showAlert("ENTERWADDRESSCOUNTRIESOVWNODATA: No data found.");
         addressCountryset.clear();
      }
   }              


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(addressCountryblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      addressCountrylay.setCountValue(toInt(addressCountryset.getValue("N")));
      addressCountryset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("addressCountry","ENTERP_ADDRESS_COUNTRY_API.New__",addressCountryblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("addressCountry/DATA");
      addressCountryset.addRow(data);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      addressCountryblk = mgr.newASPBlock("addressCountry");

      addressCountryblk.addField("OBJID").
         setHidden();

      addressCountryblk.addField("OBJVERSION").
         setHidden();

      addressCountryblk.addField("COUNTRY_CODE").
         setLabel("ENTERWADDRESSCOUNTRIESOVWCOUNTRYCODE: Country Code").
         setSize(02).
         setMandatory().
         setInsertable().
         setDynamicLOV("ISO_COUNTRY",650,450).
         setUpperCase();

      addressCountryblk.addField("DESCRIPTION").
         setLabel("ENTERWADDRESSCOUNTRIESOVWDESCRIPTION: Description").
         setSize(30).
         setReadOnly().
         setFunction("ISO_COUNTRY_API.Get_Description(:COUNTRY_CODE)");
         mgr.getASPField("COUNTRY_CODE").setValidation("DESCRIPTION");

      addressCountryblk.addField("DETAILED_ADDRESS").
         setLabel("ENTERWADDRESSCOUNTRIESOVWDETADD: Detailed Address").
         setCheckBox("FALSE,TRUE").
         setSize(5).
         setMandatory().
         setInsertable();

      addressCountryblk.addField("STATE_PRESENTATION").
         setLabel("ENTERWADDRESSCOUNTRIESOVWSTATE: State Presentation").
         setSize(20).
         enumerateValues("PRESENTATION_TYPE_API").
         setSelectBox().
         setMandatory().
         setInsertable();

      addressCountryblk.addField("COUNTY_PRESENTATION").
         setLabel("ENTERWADDRESSCOUNTRIESOVWCOUNTYPRE: County Presentation").
         setSize(20).
         enumerateValues("PRESENTATION_TYPE_API").
         setSelectBox().
         setMandatory().
         setInsertable();

      addressCountryblk.addField("CITY_PRESENTATION").
         setLabel("ENTERWADDRESSCOUNTRIESOVWCITY: City Presentation").
         setSize(20).
         enumerateValues("PRESENTATION_TYPE_API").
         setSelectBox().
         setMandatory().
         setInsertable();

      addressCountryblk.addField("VALIDATE_STATE_CODE").
         setLabel("ENTERWADDRESSCOUNTRIESOVWVALIDATESTATE: Validate State Code").
         setCheckBox("FALSE,TRUE").
         setSize(5).
         setMandatory().
         setInsertable();

      addressCountryblk.addField("VALIDATE_COUNTY_CODE").
         setLabel("ENTERWADDRESSCOUNTRIESOVWVALIDATECOUNTY: Validate County Code").
         setCheckBox("FALSE,TRUE").
         setSize(5).
         setMandatory().
         setInsertable();

      addressCountryblk.addField("VALIDATE_CITY_CODE").
         setLabel("ENTERWADDRESSCOUNTRIESOVWVALIDATECITY: Validate City Code").
         setCheckBox("FALSE,TRUE").
         setSize(5).
         setMandatory().
         setInsertable();

      addressCountryblk.setView("ENTERP_ADDRESS_COUNTRY2");
      addressCountryblk.defineCommand("ENTERP_ADDRESS_COUNTRY_API","New__,Modify__,Remove__");
      addressCountryset = addressCountryblk.getASPRowSet();

      addressCountrybar = mgr.newASPCommandBar(addressCountryblk);
      addressCountrytbl = mgr.newASPTable(addressCountryblk);
      addressCountrytbl.setTitle("ENTERWADDRESSCOUNTRIESOVWTITLEADD: Overview - Address Countries");

      addressCountrylay = addressCountryblk.getASPBlockLayout();
      addressCountrylay.setFieldOrder("COUNTRY_CODE");
      addressCountrylay.setDefaultLayoutMode(addressCountrylay.MULTIROW_LAYOUT);
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      title = mgr.translate("ENTERWADDRESSCOUNTRIESOVWTITLE1: Overview - Address Countries"); 

      if (addressCountryset.countRows() == 0 && addressCountrylay.getLayoutMode() == 5)
         addressCountrybar.disableCommand(addressCountrybar.DELETE);

      if (addressCountryset.countRows() == 0 )
      {
         addressCountrybar.disableCommand(addressCountrybar.FORWARD);
         addressCountrybar.disableCommand(addressCountrybar.BACKWARD);
         addressCountrybar.disableCommand(addressCountrybar.DELETE);
         addressCountrybar.disableCommand(addressCountrybar.DUPLICATEROW);
         addressCountrybar.disableCommand(addressCountrybar.EDITROW);
         addressCountrybar.disableCommand(addressCountrybar.BACK);

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
      return "ENTERWADDRESSCOUNTRIESOVWTITLE2: Overview - Address Countries";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("ENTERWADDRESSCOUNTRIESOVWTITLE3: Overview - Address Countries"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      out.append(mgr.startPresentation(title));
      out.append(addressCountrylay.show());
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
}
