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
*  File        : TaxLiabilityLov.java 
*  Created     : Dagalk  2002-12-18 salsa ESFI109E 
*  MAWELK      2005-04-09    FIPR364 - Corrected web tags.
*  Jakalk      : 2005-09-07 - Code Cleanup, Removed doReset and clone methods.
*  Maselk      : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class TaxLiabilityLov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.TaxLiabilityLov");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   
   private ASPContext ctx;
   private ASPBlock taxLiabilityblk;
   private ASPRowSet taxLiabilityset;
   private ASPCommandBar taxLiabilitybar;
   private ASPTable taxLiabilitytbl;

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
   
   public TaxLiabilityLov(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      title = ctx.readValue("TITLE","");
   
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("COUNTRY_CODE")) )
      {
         okFind1();   
      }
      else
         okFind();
      
      adjust();
      ctx.writeValue("TITLE", title);
   
   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind1()
   {
      ASPManager mgr = getASPManager();
      String countrycode = "*";
      String countrycode1 = null;
      String company_ = null ;
      trans.clear();

      countrycode1 =  mgr.getQueryStringValue("COUNTRY_CODE") ;
      trans.clear();
      qry = trans.addEmptyQuery(taxLiabilityblk);
      qry.addWhereCondition("COUNTRY_CODE = ? or COUNTRY_CODE = ?");
      qry.addParameter("COUNTRY_CODE", countrycode);
      qry.addParameter("COUNTRY_CODE", countrycode1);
      qry.includeMeta("ALL");
   
      mgr.querySubmit(trans,taxLiabilityblk);
   
      if (taxLiabilityset.countRows() == 0) 
      {
         mgr.showAlert("ENTERWTAXLIABILITYLOVOVWNODATA: No data found.");
         taxLiabilityset.clear();
      }
   }  
   
   
   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      qry = trans.addQuery(taxLiabilityblk);
      qry.includeMeta("ALL");
   
      mgr.querySubmit(trans,taxLiabilityblk);

      if (taxLiabilityset.countRows() == 0) 
      {
         mgr.showAlert("ENTERWTAXLIABILITYLOVNODATA1: No data found.");
         taxLiabilityset.clear();
      }
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      taxLiabilityblk = mgr.newASPBlock("COMPANYEMP");
      
      taxLiabilityblk.addField("LIABILITY_TYPE").
         setLabel("ENTERWTAXLIABILITYLOVTAXLIABILITY: Tax Liability");
      
      taxLiabilityblk.addField("COUNTRY_CODE").
         setLabel("ENTERWTAXLIABILITYLOVCURRCODEY: Country Code");
      
      taxLiabilityblk.addField("COMPANY").setHidden().
         setFunction("''");
       
      taxLiabilityblk.setView("TAX_LIABILITY_LOV");
      
      taxLiabilityset = taxLiabilityblk.getASPRowSet();  
      
      taxLiabilitybar = mgr.newASPCommandBar(taxLiabilityblk);
      
      taxLiabilitytbl = mgr.newASPTable(taxLiabilityblk);
      taxLiabilitytbl.setTitle("ENTERWTAXLIABILITYLOVTITLETAX: Tax Liability"); 

   }

   public void  adjust()
   {
       ASPManager mgr = getASPManager();

       title =  mgr.translate("ENTERWTAXLIABILITYLOVITLE1: Tax Liability"); 
       
   }


//===============================================================
//  HTML
//===============================================================
   
   protected String getDescription()
   {
      return  title; 

   }

   protected String getTitle()
   {
      return title;
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("ENTERWTAXLIABILITYLOVISTTITLE: List of Tax Liability "));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      out.append(mgr.startPresentation(title));
      out.append(taxLiabilitytbl.populateLov());
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }    
}
