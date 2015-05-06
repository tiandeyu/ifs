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
*  File        : Corporateform.java 
*  Modified    :
*   Dagalk  - Created 2002-02- 13
*  ANPALK      : 2002-06-28   Call id 30171
*  MAWELK      : 2005-04-09   FIPR364 - Corrected web tags.
*  Jakalk      : 2005-09-06 - Code Cleanup, Removed doReset and clone methods.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Corporateform extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.Corporateform");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay; 
   private ASPField fCOUNTRY_CODE;
   private ASPField fCOUNTRYCODEDESC;
   private ASPField fCORPORATE_FORM;
   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean texts;
   private boolean isnotes;
   private String title;
   private String val;
   private ASPQuery query;
   private String company;
   private ASPCommand cmd;
   private ASPBuffer buff;
   private ASPBuffer row; 

   //===============================================================
   // Construction 
   //===============================================================
   public Corporateform(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();


      fmt = mgr.newASPHTMLFormatter();   
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      texts = ctx.readFlag("TEXTS",false);

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();

      title =  mgr.translate("ENTERWCORPORATEFORMTITLE: Forms of Business");

      adjust();
      ctx.writeFlag("TEXTS",texts);   
   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      mgr.createSearchURL(headblk);

      query = trans.addQuery(headblk);

      query.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if (  headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("ENTERWCORPORATEFORMNODATAFOUND: No data found.")); 
         headset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      query = trans.addQuery(headblk);
      query.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }

   public void  newRow()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer data;

      cmd = trans.addEmptyCommand("HEAD","CORPORATE_FORM_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").
         setHidden();

      fCOUNTRY_CODE = headblk.addField("COUNTRY_CODE").
                     setSize(20).
                     setUpperCase().
                     setDynamicLOV("ISO_COUNTRY","",650,445).
                     setLabel("ENTERWCORPORATEFORMCOUNTRYCODE: Country Code");

      fCOUNTRYCODEDESC = headblk.addField("COUNTRYCODEDESC").
                        setSize(20).
                        setFunction("ISO_COUNTRY_API.Get_Description(:COUNTRY_CODE)").
                        setReadOnly().
                        setLabel("ENTERWCORPORATEFORMCOUNTRYCODEDESC: Check Amount");
      mgr.getASPField("COUNTRY_CODE").setValidation("COUNTRYCODEDESC");

      fCORPORATE_FORM = headblk.addField("CORPORATE_FORM").
                       setSize(20).
                       setLabel("ENTERWCORPORATEFORMCORPORATEFORM: Corporate Form");

      headblk.addField("CORPORATE_FORM_DESC").
         setSize(80).
         setLabel("ENTERWCORPORATEFORMCORPORATEFORMDESC: Description");

      headblk.setView("CORPORATE_FORM");
      headset = headblk.getASPRowSet();
      headblk.defineCommand("CORPORATE_FORM_API","New__,Modify__,Remove__");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("ENTERWCORPORATEFORMFORMQSD: Forms of Business");

      headbar = mgr.newASPCommandBar(headblk);

      headlay = headblk.getASPBlockLayout();    
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);       
   }


   public void  adjust()
   {
      if ( headset.countRows() == 0 )
      {
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.BACK);
      }

      if (headlay.isEditLayout())
      {
         fCOUNTRY_CODE.setReadOnly();
         fCOUNTRYCODEDESC.setReadOnly();
         fCORPORATE_FORM.setReadOnly();

      }
   }

//===============================================================
//  HTML
//===============================================================

   protected String getTitle()
   {
      return "ENTERWCORPORATEFORMTITLE1: Forms of Business";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("ENTERWCORPORATEFORMDTITLE2: Forms of Business"));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(title));
      if (headlay.isVisible() )
         out.append(headlay.show());
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
