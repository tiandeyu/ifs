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
*  File        : DimensionColumnLov.java 
*  Modified    :
*  ASP2JAVA Tool  2002-01-29  - Created Using the ASP file DimensionColumnLov.asp
*  MAWELK      2002-09-17   Made modification for Salsa project.(For both char and table this lov is used now)
*  MAAMLK      2002-12-02   Call Id 92063 Corrected the labels
*  RAHELK      2003-01-29   Code review to add portlet to fnd component.
*  RAHELK      2003-01-30   added find functionality.
*  RAHELK      2003-03-07   implemeted code as required for Call 93639.
*  RAHELK      2003-03-19   added count method and corrected okFind.
* ----------------------------------------------------------------------------
*
*  New Comments:
*  2006/06/30 buhilk Bug 58216, Fixed SQL Injection threats
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DimensionColumnLov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.kpivw.DimensionColumnLov");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar cmdbar;
   private ASPTable tbl;
   private ASPBlockLayout lay;
   private ASPField f;
   private ASPTransactionBuffer trans;
   private String temp_ial;
   private String temp_dim;
   private String tilte_val;
   private ASPCommand cmd;
   private String strIal;
   private String strAppowner;
   private ASPQuery q;
   
   public DimensionColumnLov(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();
   
      trans = mgr.newASPTransactionBuffer();
      ctx   = mgr.getASPContext();
   
      //mgr.setPageExpiring();
      temp_ial = mgr.readValue("IAL_VAL"); 
      temp_dim = mgr.readValue("DIM_VAL"); 
      tilte_val =temp_dim.toLowerCase();
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());   
      else if(mgr.isEmpty(temp_ial))
          mgr.showAlert(mgr.translate("KPIVWDIMENSIONCOLUMNLOVNODATA: No data found."));
      else
         okFind();
   
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("SIIAL1","FND_SETTING_API.GET_VALUE","IAL_ALIAS");
      cmd.addParameter("DIMENTION_KEY","IAL_USER");       	
      trans = mgr.perform(trans);
      strIal = trans.getValue("SIIAL1/DATA/IAL_ALIAS");

      trans.clear();
      
      String select_string = "DIM_" + temp_dim + "_DESC1 DIMENTION_DESCRIPTION, DIM_" + temp_dim + "_KEY1 DIMENTION_KEY";
      String query_string = strIal +"." + temp_ial;
      q = trans.addQuery("MAIN",query_string, blk);
      
      q.setSelectList(select_string);
      q.setGroupByClause("DIM_" + temp_dim + "_DESC1, DIM_" + temp_dim + "_KEY1 ");
      q.setBufferSize(10000);

      trans.generateBlockCommands(blk);

      String where_condition = trans.getValue("MAIN/WHERE"); 
      if (!mgr.isEmpty(where_condition))
      {
         String temp = "";
         int i;
         i = where_condition.indexOf("DIMENTION_DESCRIPTION");
         if (i >-1)
         {
            temp = where_condition.substring(0,i);
            temp += "DIM_" + temp_dim + "_DESC1 ";
            temp += where_condition.substring(i+21);
            
            where_condition = temp;
         }

         i = where_condition.indexOf("DIMENTION_KEY");
         if (i >-1)
         {
            temp = where_condition.substring(0,i);
            temp += "DIM_" + temp_dim + "_KEY1 ";
            temp += where_condition.substring(i+13);
            
            where_condition = temp;
         }
         trans.setValue("MAIN/WHERE",where_condition);
      }
      
      mgr.querySubmit(trans,blk); 
   }

   
   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("SIIAL1","FND_SETTING_API.GET_VALUE","IAL_ALIAS");
      cmd.addParameter("DIMENTION_KEY","IAL_USER");       	
      trans = mgr.perform(trans);
      strIal = trans.getValue("SIIAL1/DATA/IAL_ALIAS");

      trans.clear();
      String query_string = strIal +"." + temp_ial;
      q = trans.addQuery("MAIN",query_string,blk);
      q.setSelectList("to_char(count(*)) N");

      String where_condition = trans.getValue("MAIN/WHERE"); 
      if (!mgr.isEmpty(where_condition))
      {
         String temp = "";
         int i;
         i = where_condition.indexOf("DIMENTION_DESCRIPTION");
         if (i >-1)
         {
            temp = where_condition.substring(0,i);
            temp += "DIM_" + temp_dim + "_DESC1 ";
            temp += where_condition.substring(i+21);
            
            where_condition = temp;
         }

         i = where_condition.indexOf("DIMENTION_KEY");
         if (i >-1)
         {
            temp = where_condition.substring(0,i);
            temp += "DIM_" + temp_dim + "_KEY1 ";
            temp += where_condition.substring(i+13);
            
            where_condition = temp;
         }
         trans.setValue("MAIN/WHERE",where_condition);
      }
      
      trans = mgr.perform(trans);

      lay.setCountValue(toInt(trans.getValue("MAIN/DATA/N")));
   }
   
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("MAIN");
   
      f = blk.addField ("DIMENTION_DESCRIPTION");
      f.setLabel("KPIVWDIMENSIONCOLUMNLOVDESCRIPTION: Description");
      f.setSize(20);
   
      f = blk.addField ("DIMENTION_KEY");
      f.setLabel("KPIVWDIMENSIONCOLUMNLOVIDENTITY: Identity"); 
      f.setSize(20);
   
      f = blk.addField ("IAL_ALIAS");
      f.setSize(20);
      f.setFunction("''"); 
      f.setHidden();
   
      blk.setView("DUAL");
      rowset = blk.getASPRowSet();
      cmdbar = mgr.newASPCommandBar(blk);
      tbl = mgr.newASPTable(blk);
      tbl.disableEditProperties();
      tbl.disableQuickEdit();
      tbl.disableOutputChannels();
      tbl.disableRowCounter();

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);

      disableHeader();
      disableBar();
      disableHelp();
      disableNavigate();
      disableFooter();
      cmdbar.disableMinimize();
   
      String width            = mgr.getQueryStringValue("WIDTH");
      int specifiedWidth  = (int)Double.parseDouble(width);
      getASPForm().setFormWidth(specifiedWidth  - 45);
      
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "KPIVWDIMENSIONCOLUMNLOVDIMENSIONSELECTION: Dimension Selection";
   }

   protected String getTitle()
   {
      return "KPIVWDIMENSIONCOLUMNLOVDIMENSION: Dimension ";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
     
      appendToHTML("<font class=pageTitle>"+mgr.translate("KPIVWDIMENSIONCOLUMNLOVTITLE: List of valid Dimension")+"</font>");
      
      if (lay.isMultirowLayout())
      {
         appendToHTML(cmdbar.showBar());
         appendToHTML(tbl.populateLov());
      }
      else
         appendToHTML(lay.show());
      
      printHiddenField("IAL_VAL",temp_ial); 
      printHiddenField("DIM_VAL",temp_dim); 

   }

}
