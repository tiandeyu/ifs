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
*  File        : ObjectConnection.java
*  Converted   : Bakalk using ASP2JAVA Tool on  2001-03-15
*  Created     : Using the ASP file ObjectConnection.asp
*
* Change history
* ----------------------------------------------------------------------------
* Date         Sign     Comment
* ----         ----     -------
* 2003-04-07   MDAHSE   Removed call to f.setSize so that each column get the size
*                       the value in it needs. Please redirect all complaints about
*                       this to BOOTSE and STNISE.
* 2003-07-01   NISILK   Fixed call 92253 - Modified method getContents().
* 2003-09-02   INOSLK   Call ID 101731: Modified doReset() and clone().
* 2003-09-10   NISILK   Call ID: 102843. Modified methods run() and getContents().
* 2003-10-21   DIKALK   Overrode ASPPage.useLovContextSlot() to solve logout problem
* 2003-11-05   DIKALK   Added mgr.endPresentation() in getContents()
* 2004-03-01   DHPELK   Removed substrb from selectComboValues
* 2006-03-15   AMNALK   Fixed call 327270: Modified getFieldInfo() - call public method Reference_SYS.Get_Lov_Properties
*			instead of Reference_SYS.Get_Lov_Properties_.
* 2006-03-17   BAKALK   Fixed call id:135018. Made many modifications.
* 2006-03-18   CHODLK   Merged Patch No : 56049 -Bad performance in Document Info/Objects.
* 2006-07-28   BAKALK   Bug ID 58216, Fixed Sql Injection.
* 2006-09-11   BAKALK   Bug ID 58216, Moved creation of "Temp" block from getFieldInfo() to preDefine().
* 2006-10-05   WYRALK   Bug ID 60438, set the mgr value for ser_list after setting the ctx value
* 2006-11-01   WYRALK   Bug ID 60438, Fixed refresh problem in drop down list by passing the SER_LIST 
*                             with the Query string for the new page. Expanded the buffer size of the 
*                             drop down list according to the number of records in the dropdown list.
*                             Query for drop down list was modified so that the LU's with null LU_desc 
*                             would not be added to the list as blank entries. OkFind() was modified and
*                             okFind(int skipRows ) was added to keep track of record set. 
* 2007-01-17   KARALK   DMPR303 ENABLE MULTIPLE SELECT OF OBJECT AND HANDLE NEW "OK" BUTTON.
* 2007-02-07   CHODLK   DMPR303 modified method printContents(), modified some variable names according to standards.
* 2007-08-13   NaLrlk   XSS Correction.
* 2007-12-13   DINHLK   Bug ID 65462, Modified methods predefine(), run() and getContents(). 
*                       Please note that this is a custom fix for Plant Design.
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------------
*   071213     ILSOLK   Bug Id 68773, Eliminated XSS.
*   071226     DINHLK   Bug ID 65462, Modifdied run(), selectObjects(), preDefine(), printContents().
* 2008-02-05   DULOLK   Bug Id 65462, Modified run(), preDefine(), printContents(). Removed selectObjects().
*                                     Multiple object selection was implemeted using javascript rather than doing a submit.
* 2009-01-20   AMNALK   Bug Id 79909, Modified javascript function ok() to support multiple object selections.
* 2009-04-22   AMNALK   Bug Id 82277, Modified run() and javascript function ok().
* ----------------------------------------------------------------------------
*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;



public class ObjectConnection extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.ObjectConnection");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPForm frm;
   private ASPContext ctx;
   private ASPBlock tempblk;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar bar;
   private ASPTable tbl;
   private ASPBlockLayout lay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPQuery q;
   private ASPBuffer data;

   private boolean defined;
   private boolean bHasViewComments;
   private boolean bMultiKey;
   private boolean bFromCompare;

   private String lu_name;
   private String view_name;
   private String key_names;
   private String col_names;
   private String col_prompts;
   private String col_types;
   private String ser_list;
   private String sEndPresentation;
   private String sKeyStr;
   private String str;
   private String key_name;
   private String sColName;
   private String[] aLabel;
   private String[] aTypes;

   private int n;
   private int index;

   // Bug 60438 start,
   private int skipped;
   private int buff_size;
   private boolean commant_fw_bw;
   // Bug 60438 end,
   //dmpr303 start
   private boolean bObjSelect;
   private String returnToParent;
   //dmpr303 end.

   //Bug Id 65462, Start
   private boolean bIsPlantLu;
   private boolean bFromDocIssue;
   //Bug Id 65462, End

   //Bug Id 82277, start
   private String sNoRowsSelected;
   //Bug Id 82277, end

   //===============================================================
   // Construction
   //===============================================================
   public ObjectConnection(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      fmt = mgr.newASPHTMLFormatter();
      frm = mgr.getASPForm();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      data          = ctx.readBuffer("DATA");
      ser_list      = ctx.readValue("SERVICE_LIST","");
      bFromCompare  = ctx.readFlag ("FROM_COMPARE",false); 

      //Bug Id 65462, Start
      bIsPlantLu    = ctx.readFlag ("IS_PLANT_LU",false);
      bFromDocIssue    = ctx.readFlag ("FROM_DOCISSUE",false);
      //Bug Id 65462, End

      // Bug 60438 start,
      skipped       = ctx.readNumber("SKIPPED",0);
      buff_size     = ctx.readNumber("BUFF_SIZE",0);
      commant_fw_bw = false;  //Bug Id 65462

      //Bug Id 82277, start
      sNoRowsSelected = mgr.translate("DOCMAWOBJCON_NOROWSSELECTED: No Rows Selected");
      //Bug Id 82277, end

      buff_size= Integer.parseInt(mgr.getConfigParameter("ADMIN/BUFFER_SIZE"));

      if (!mgr.isEmpty(mgr.readValue("SERVICE_LIST")))
         ser_list = mgr.readValue("SERVICE_LIST");
      // Bug 60438 end,

      //if (mgr.isEmpty(data))

      //Bug Id 65462, Start
      if (data==null)
         selectComboValues();

      if (!mgr.isEmpty(mgr.getQueryStringValue("LU_FROM_COMPARE")))
      {
         bFromCompare = true;
         if (lu_name.equals("PlantArticle") || lu_name.equals("PlantCable") || lu_name.equals("PlantChannel") || lu_name.equals("PlantCircuit") || lu_name.equals("PlantConnectionPoint") || lu_name.equals("PlantIoCard") || lu_name.equals("PlantObject") || lu_name.equals("PlantSignal"))
            bIsPlantLu = true;
         checkPopulate();
      }
      else if (mgr.commandBarActivated()) // fields already defined.
      {

         // Bug 60438 start,
         String command = mgr.readValue("__COMMAND");
         if ( "HEAD.Backward".equals(command))
         {
            skipped = skipped - buff_size;
            commant_fw_bw = true;
         }
         else if ( "HEAD.Forward".equals(command) )
         {
            skipped = skipped + buff_size;
            commant_fw_bw = true;
         }
         ctx.writeNumber("SKIPPED",skipped);
         // Bug 60438 end,
         
         if (lu_name.equals("PlantArticle") || lu_name.equals("PlantCable") || lu_name.equals("PlantChannel") || lu_name.equals("PlantCircuit") || lu_name.equals("PlantConnectionPoint") || lu_name.equals("PlantIoCard") || lu_name.equals("PlantObject") || lu_name.equals("PlantSignal"))
            bIsPlantLu = true;
         
         eval(mgr.commandBarFunction());
      }
      else if (defined)
      {
         checkPopulate();
      }
      //dmpr303 end.
      //Bug Id 65462, End

      // Bug 60438 start,
      ctx.writeNumber("BUFF_SIZE",buff_size);       
      ctx.writeNumber("SKIPPED",skipped);
      ctx.writeValue("SERVICE_LIST",ser_list); 
      // Bug 60438 end,

      ctx.writeBuffer("DATA",data);
      ctx.writeFlag("FROM_COMPARE",bFromCompare); 

      //Bug Id 65462, Start
      ctx.writeFlag("IS_PLANT_LU",bIsPlantLu);
      ctx.writeFlag("FROM_DOCISSUE",bFromDocIssue);
      //Bug Id 65462, End

      // Remove Gif from the mgr.endPresentation()
      sEndPresentation  = "<table width='"+frm.getFormContentWidth()+"' cellpadding=0 cellspacing=0><tr><td>";
      sEndPresentation += "<table cellpadding=0 cellspacing=0 border=0 width='100%'>";
      sEndPresentation += "<tr><td width='100%' height='1' bgcolor='#000000'></td></tr></table><br></td></tr></table>";
   }

   /**
    * Overriding ASPPage.useLovContextSlot in order to force the use of
    * the Lov slot in the session table. This solves the logout problem
    */
   protected boolean useLovContextSlot()
   {
      return true;
   }


   public void  selectComboValues()
   {
      ASPManager mgr = getASPManager();
      String sSelect,sWhere,sOrderBy,sFrom;

      sSelect = "lu_name, substr( lu_desc, 1, 250 )";
      sFrom = "object_connection";
      sOrderBy = "lu_desc";
      //bug 58216 starts
      // Bug 60438 start,
      sWhere = "(service_list = '*' or '^' || service_list like ? or service_list is null) and lu_desc is not null";

      q = trans.addQuery("COUNTDATA", sFrom,"to_char(count(*)) N", sWhere, "");
      q.addParameter("SERVICE_LIST","^%"+ser_list+"^%");
      trans = mgr.submit(trans);
      int n = Integer.parseInt(trans.getValue("COUNTDATA/DATA/N"));
      trans.clear();
      // Bug 60438 end,

      q = trans.addQuery("GETLULIST", sFrom,sSelect, sWhere, sOrderBy);
      q.addParameter("SERVICE_LIST","^%"+ser_list+"^%");
      q.setBufferSize(n);
      //bug 58216 end

      trans = mgr.submit(trans);
      data = trans.getBuffer("GETLULIST");
   }


   public void  getFieldInfo()
   {
      ASPManager mgr = getASPManager();
      String sSQLStmt;
      trans = mgr.newASPTransactionBuffer();


      trans.clear();
      //bug 58216 starts
      sSQLStmt = "SELECT VIEW_NAME FROM OBJECT_CONNECTION WHERE LU_NAME = ?";
      q = trans.addQuery("GETVIEW",sSQLStmt);
      q.addParameter("LU_NAME",lu_name);
      //bug 58216 end
      q.includeMeta("ALL");
      trans = mgr.submit(trans);
      view_name = trans.getValue("GETVIEW/DATA/VIEW_NAME");
      trans.clear();



      cmd = trans.addCustomCommand("CHILDFILEDS", "Reference_SYS.Get_Lov_Properties");
      cmd.addParameter("VIEW_NAME",view_name);
      cmd.addParameter("KEY_NAMES");
      cmd.addParameter("COL_NAMES");
      cmd.addParameter("COL_PROMPTS");
      cmd.addParameter("COL_TYPES");

      // performConfig is needed below since we do this prior to setDefined():bakalk
      trans = mgr.performConfig(trans);

      key_names = trans.getValue("CHILDFILEDS/DATA/KEY_NAMES");
      col_names = trans.getValue("CHILDFILEDS/DATA/COL_NAMES");
      col_prompts = trans.getValue("CHILDFILEDS/DATA/COL_PROMPTS");
      col_types = trans.getValue("CHILDFILEDS/DATA/COL_TYPES");
      trans.clear();

      bHasViewComments = mgr.isEmpty(col_names)? false : true;

   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      
      //Bug Id 65462, Start
      boolean bPlantKeyAFound = false;
      boolean bPlantKeyFound = false;
      String sKeyNames = "";
      String sKeyVal = "";
      int nNum;

      ctx = mgr.getASPContext();
      
      bFromCompare  = ctx.readFlag ("FROM_COMPARE",false); 
      bIsPlantLu    = ctx.readFlag ("IS_PLANT_LU",false);         
      bFromDocIssue = ctx.readFlag ("FROM_DOCISSUE",false);
      //Bug Id 65462, End

      disableHeader();
      disableOptions();
      disableHomeIcon();
      disableNavigate();
      disableHelp();

      //bug 58216 starts moved the creation of block from getFieldInfo() hereto.
      tempblk = mgr.newASPBlock("TEMP");
      tempblk.addField("VIEW_NAME");
      tempblk.addField("KEY_NAMES");
      tempblk.addField("COL_NAMES");
      tempblk.addField("COL_PROMPTS");
      tempblk.addField("COL_TYPES");
      tempblk.addField("SORT_INFO");
      tempblk.addField("SERVICE_LIST");
      tempblk.addField("LU_NAME");
      //bug 58216 ends.

      //Bug Id 65462, Start
      if (!mgr.isEmpty(mgr.getQueryStringValue("FROM_DOCISSUE"))) 
         bFromDocIssue = true;
      else if (!mgr.isEmpty(mgr.getQueryStringValue("__DYNAMIC_DEF_KEY")))
      {
         lu_name = mgr.getQueryStringValue("__DYNAMIC_DEF_KEY");
         String[] sDynamicDefKeys = split(lu_name,"^");
         lu_name = sDynamicDefKeys[0];              
      }
      //Bug Id 65462, End

      if (!mgr.isEmpty(mgr.getQueryStringValue("__DYNAMIC_DEF_KEY")))
      {
         bHasViewComments = true;
         getFieldInfo();

         //Bug Id 65462, Start
         if(!mgr.isEmpty(mgr.getQueryStringValue("LU_FROM_COMPARE")))
            bFromCompare = true;         
         
         if (lu_name.equals("PlantArticle") || lu_name.equals("PlantCable") || lu_name.equals("PlantChannel") || lu_name.equals("PlantCircuit") || lu_name.equals("PlantConnectionPoint") || lu_name.equals("PlantIoCard") || lu_name.equals("PlantObject") || lu_name.equals("PlantSignal"))
             bIsPlantLu = true;
         //Bug Id 65462, End 
         
         if (!bHasViewComments)
         {
            mgr.showAlert(mgr.translate("DOCMAWOBJECTCONNECTIONNOVIEWCOMM: View comments not defined for the selected object. Select another object."));
            defined = false;   // fields not defined.
         }

         setVersion(3);
         blk = mgr.newASPBlock("HEAD");
         blk.setView(view_name);

         bMultiKey = true;
                  
         //Bug Id 65462, Start - For plant design concatonate KEYAs
         if (bFromCompare && bIsPlantLu)
             sKeyStr = "KEYA";
         else
             sKeyStr = "'"+lu_name+"~'";
         //Bug Id 65462, End
         
         str = key_names+",";
         index = str.indexOf(',');
         ASPField f;
         while (index != -1)
         {
            key_name = str.substring(0,index);
            
            //Bug Id 65462, Start - Not required for plant design since only KEYA is required
            if (!(bFromCompare && bIsPlantLu))
               sKeyStr += "||'"+key_name+"='||"+key_name+"||'^'";
            //Bug Id 65462, End
                        
            if ((","+col_names+",").indexOf(","+key_name+",")==-1)
            {
               f = blk.addField(key_name);
               f.setHidden();
            }
            str = str.substring(index+1,str.length());
            index = str.indexOf(",");
         }

         sKeyStr += "||'||'"; //Bug Id 65462

         if (bMultiKey)
         {
            f = blk.addField( "DATA_OUT" );
            f.setFunction(sKeyStr);
            f.setHidden();
         }
         
         //Bug Id 65462, Start - Put field KEYA in front since the customer would be identifying rows using that.

         aLabel = split((col_prompts+"^"),'^');
         aTypes = split((col_types+"^"),'^');
         
         if (bFromCompare && bIsPlantLu)
         {
            str = col_names+",";
            n = 0;
            index = str.indexOf(",");
            while (index != -1 && !bPlantKeyAFound) 
            {
               sColName = str.substring(0,index);           
            
               if ("KEYA".equals(sColName)) 
               {
                  f = blk.addField(sColName);
                  f.setLabel("COLLAB"+n+": "+aLabel[n]);
                  bPlantKeyAFound = true;
               }
               str = str.substring(index+1,str.length());
               index = str.indexOf(",");
               n++;
            }
         }
         str = col_names+",";
         //Bug Id 65462, End

         n = 0;
         index = str.indexOf(",");
         
         //Bug Id 65462, Start - Check whether column is a key and hide since only KEYA is required. 
         //                      Also, hide the keys since the customer doesn't require them.

         
         while (index != -1)
         {
            sColName = str.substring(0,index);
            
            if (bFromCompare && bIsPlantLu) 
            {
               sKeyNames = key_names+",";            
               nNum = sKeyNames.indexOf(',');            
               while (nNum != -1 && !bPlantKeyFound)
               {
                  sKeyVal = sKeyNames.substring(0,nNum);
                  if (sKeyVal.equals(sColName)) 
                  {                    
                     bPlantKeyFound = true;
                  }
                  sKeyNames = sKeyNames.substring(nNum+1,sKeyNames.length());
                  nNum = sKeyNames.indexOf(",");
               }
            }

            if (!(bFromCompare && bIsPlantLu && "KEYA".equals(sColName))) 
            {            
               if (aTypes[n].indexOf("DATE")!=-1)
                  f = blk.addField(sColName,"Date");
               else if (aTypes[n].indexOf("NUMBER")!=-1)
                  f = blk.addField(sColName,"Number" );
               else
                  f = blk.addField(sColName);
               
               f.setLabel("COLLAB"+n+": "+aLabel[n]);
               f.setSize(20);
               
               if (bPlantKeyFound) 
               {                 
                  f.setHidden();
                  bPlantKeyFound = false;
               }
            }

            str = str.substring(index+1,str.length());
            index = str.indexOf(",");
            n++;
         }
         //Bug Id 65462, End

         tbl = mgr.newASPTable(blk);
         tbl.setTitle("DOCMAWOBJECTCONNECTIONLOVTITLE: Objects");

         //Bug Id 65462, Start
         if (bMultiKey)
         {
            tbl.setKey("DATA_OUT");
         }
         else
         {
            tbl.setKey(key_names);
         }
         //Bug Id 65462,End

         tbl.disableQuickEdit();
         tbl.disableRowCounter();   //Bug Id 65462         
         tbl.enableRowSelect();//dmpr303
         bar = mgr.newASPCommandBar(blk);
         rowset = blk.getASPRowSet();
         lay = blk.getASPBlockLayout();

         lay.setDialogColumns(2);
         lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
         bar.disableCommand(bar.NEWROW);

         defined = true;
      }
      //else: do nothing.
      //Bug Id 65462, Start
      ctx.writeFlag("FROM_COMPARE",bFromCompare);  
      ctx.writeFlag("IS_PLANT_LU",bIsPlantLu);     
      //Bug Id 65462, End
   }

   public void   checkPopulate()
   {
      ASPManager mgr = getASPManager();
      int count;
      int max_lov_rows;
      max_lov_rows = toInt(mgr.getConfigParameter("ADMIN/MAX_LOV_ROWS"));

      trans.clear ();
      q = trans.addQuery(blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      count = toInt(rowset.getRow().getValue("N"));
      rowset.clear();
      //Check whether the number of rows returned is more than MAX_LOV_ROWS
      //show the find layout or populate the LOV accrodingly
      if (count > max_lov_rows)
         lay.setLayoutMode(lay.FIND_LAYOUT);
      else
         okFind();
   } 

   // Bug 60438 start,
   public void  okFind()
   {
      okFind(0);
      skipped=0;
   }

   public void  okFind(int skipRows )
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(blk);
      q.setOrderByClause(tbl.getKeyColumnName());
      q.includeMeta("ALL");
      q.skipRows(skipRows);

      mgr.querySubmit(trans,blk);

      if (rowset.countRows() == 0)
         mgr.showAlert(mgr.translate("DOCMAWOBJECTCONNECTIONNODATA: No data found."));
   }
   // Bug 60438 end,

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      lay.setCountValue(toInt(rowset.getRow().getValue("N")));
      rowset.clear();
      lay.setLayoutMode(lay.FIND_LAYOUT);
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      ASPManager mgr= getASPManager();
      return  mgr.translate("OBJECTCONNECTION: Object Connection");
   }

   protected String getTitle()
   {
      ASPManager mgr= getASPManager();
      return  mgr.translate("OBJECTCONNECTION: Object Connection");
   }

   protected void printContents() throws FndException
   {      
      ASPManager mgr = getASPManager();
      appendToHTML("  <table border=\"0\" width=\"500\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\" class=\"main\">\n");
      appendToHTML("    <tr>\n");
      appendToHTML("      <td width=\"10\"></td>\n");
      appendToHTML("      <td class=\"pageTitle\"><font class=\"pageTitle\">");
      appendToHTML(mgr.translate("DOCMAWOBJECTCONNECTIONTITLE: Select object to be inserted"));
      appendToHTML("</font></td>\n");
      appendToHTML("    </tr>\n");
      appendToHTML("  </table>\n");
      appendToHTML(sEndPresentation);
      if (!bFromCompare)
      {
         appendToHTML("  <table border=\"0\" class=\"BlockLayoutTable\">\n");
         appendToHTML("    <tr>\n");
         appendToHTML("      <td  colspan=\"2\"></td>\n");
         appendToHTML("    </tr>\n");
         appendToHTML("    <tr>\n");
         appendToHTML("      <td align=left>");
         appendToHTML(fmt.drawReadLabel("DOCMAWOBJECTCONNECTIONOBJECT: Object: "));
         appendToHTML("      </td>\n");
         appendToHTML("      <td ><font face=\"Arial\" size=\"2\"><select name=\"LU_DESC\" size=\"1\"\n");
         appendToHTML("      OnChange=\"selectLuDesc()\">\n");
         appendToHTML(fmt.populateListBox(data,lu_name));
         appendToHTML("</select></font></td>\n");
         appendToHTML("    </tr>\n");
         appendToHTML("    <tr>\n");
         appendToHTML("      <td  colspan=\"2\"></td>\n");
         appendToHTML("    </tr>\n");
         appendToHTML("  </table>\n");

      }
      if (defined)
      {
         if (lay.isMultirowLayout())
         {
            // Bug 60438 start,
            if (rowset.countRows()== 0 && commant_fw_bw)
            {
               commant_fw_bw=false;
               okFind(skipped);
            }
            // Bug 60438 end,
            appendToHTML(bar.showBar());
            
            //Bug Id 65462, Start - Add separate Ok button
            if (!lay.isFindLayout())
            {
               appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
               appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"20\" style=\"LEFT: 20px; \">\n");
               appendToHTML("       <td width=\"10\"></td>");  
               appendToHTML("       <td >"+fmt.drawReadLabel(mgr.translate("DOCMAWOBJECTCONNECTIONSELECT: select the object(s) you want to connect and click OK. "))+"</td>\n");
               appendToHTML("       <td align = right>"+fmt.drawButton("OK"," " + mgr.translate("DOCMAWOBJECTCONNECTIONOK: OK" + " "),"OnClick=ok() align=left")+"</td>\n");
               appendToHTML("       <td width=\"20\"></td>");  
               appendToHTML("   </tr>\n");
               appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"20\" style=\"LEFT: 20px; \">\n");
               appendToHTML("   </tr>\n");
               appendToHTML("   </table>\n");
            }
           
            appendToHTML(tbl.populateLov());
            //Bug Id 65462, End
         }
         else
         {
            appendToHTML(lay.show());
         }
      }
      appendToHTML("  <p>");

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      
      appendDirtyJavaScript("function selectLuDesc()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   field = document.form.LU_DESC;\n");
      appendDirtyJavaScript("   lu_name = field.options[field.selectedIndex].value;\n");
      // Bug 60438 start,
      appendDirtyJavaScript("  this_ser_list = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(ser_list));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("   window.location=\"../docmaw/ObjectConnection.page?SERVICE_LIST=\"+this_ser_list+\"&__DYNAMIC_DEF_KEY=\"+lu_name;\n");
      // Bug 60438 end,
      appendDirtyJavaScript("}\n");
      // return out;
      
      
      //Bug Id 65462, Start - Code is similar to getAllSelectedValues() in clientscript.js but we concatonate differently.
      appendDirtyJavaScript("function ok()\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("   var f = document.forms[0]; \n");
      appendDirtyJavaScript("   var val; \n");
      appendDirtyJavaScript("   var arr; \n");
      appendDirtyJavaScript("   var count = 0; \n");
      appendDirtyJavaScript("   checkbox_array = f.__SELECTED1; \n");
      appendDirtyJavaScript("   value_array = f.__SELECTED_VALUE1; \n"); //Bug Id 79909
      appendDirtyJavaScript("   str = \"\"; \n");
      appendDirtyJavaScript("   if(checkbox_array != null) \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      size = checkbox_array.length; \n");
      //Bug Id 82277, start
      appendDirtyJavaScript("      if (size > 0) \n");
      appendDirtyJavaScript("      { \n");
      appendDirtyJavaScript("         for (i=0;i<size; i++) \n");
      appendDirtyJavaScript("         { \n");
      appendDirtyJavaScript("            if (checkbox_array[i].checked) \n");
      appendDirtyJavaScript("            { \n");
      appendDirtyJavaScript("               value = value_array[i].value; \n");  //Bug Id 79909
      appendDirtyJavaScript("               if (count >= 1) \n");
      appendDirtyJavaScript("               { \n");
      appendDirtyJavaScript("                  arr = value.split(\"~\");\n");
      appendDirtyJavaScript("                  if(arr.length > 1) \n");
      appendDirtyJavaScript("                     value = arr[1];\n");
      appendDirtyJavaScript("               } \n");
      appendDirtyJavaScript("               str += value; \n");
      appendDirtyJavaScript("               count++; \n");
      appendDirtyJavaScript("            } \n");
      appendDirtyJavaScript("         } \n");
      appendDirtyJavaScript("      } \n");
      appendDirtyJavaScript("      else\n");
      appendDirtyJavaScript("      { \n");
      appendDirtyJavaScript("         if (checkbox_array.checked)\n");
      appendDirtyJavaScript("         { \n");
      appendDirtyJavaScript("            str = value_array.value;\n");
      appendDirtyJavaScript("            count++; \n");
      appendDirtyJavaScript("         } \n");
      appendDirtyJavaScript("      } \n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if (count > 0 ) \n");
      appendDirtyJavaScript("      setValue(str);\n");
      appendDirtyJavaScript("   else \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(sNoRowsSelected);
      appendDirtyJavaScript("           ');\n");
      //Bug Id 82277, end
      appendDirtyJavaScript("}\n");
      //Bug Id 65462, End

   }

   //removed define() method : bakalk

   //==========================================================================
   //  Methods on Handling Strings
   //==========================================================================
   // 1
   private int stringIndex(String mainString,String subString)
   {
      int a=mainString.length();
      int index=-1;
      for (int i=0;i<a;i++)
         if (mainString.startsWith(subString,i))
         {
            index=i;
            break;
         }
      return index;
   }//end of stringIndex

   // 2

   private String replaceString(String mainString,String subString,String replaceString)
   {
      String retString = "";
      int posi;
      posi = stringIndex(mainString, subString);
      while (posi!=-1)
      {
         retString+=mainString.substring(0,posi)+replaceString;
         mainString=mainString.substring(posi+subString.length(),mainString.length());
         posi = stringIndex(mainString, subString);
      }

      return retString+mainString;

   }//repstring

   // 3
   public int howManyOccurance(String str,char c)
   {
      int strLength=str.length();
      int occurance=0;
      for (int index=0;index<strLength;index++)
         if (str.charAt(index)==c)
            occurance++;
      return occurance;

   }

   // 4
   private String[] split(String str,char c)
   {
      int length_=howManyOccurance(str,c);
      int strLength=str.length();
      int occurance=0;
      int index=0;

      String[] tempString= new String[length_+1];
      while (strLength>0)
      {
         occurance=str.indexOf(c);
         if (occurance==-1)
         {
            tempString[index]=str;
            break;
         }
         else
         {
            tempString[index++]=str.substring(0,occurance);
            str=str.substring(occurance+1,strLength);
            strLength=str.length();
         }


      }
      return tempString;
   }

}
