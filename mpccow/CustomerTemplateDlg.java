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
*  File        : CustomerTemplateDlg.java
*  Created     : Haunlk 2006/11/28
*  Description :
*  Notes       :
* ----------------------------------------------------------------------------
*  Modified    :
* ---------------------- Wings Merge Start -----------------------------------
* ChJalk  30-Jan-2007  - Merged Wings Code.
* ---------------------- Wings Merge End -------------------------------------
* NiDalk  10-Jul-2007  - Performed XSS corrections.

*/

package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class CustomerTemplateDlg extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.CustomerTemplateDlg");



   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPBuffer data;

   private ASPCommand cmd;
   private ASPContext ctx;
   private ASPLog log;
   private ASPField f;

   //===============================================================
   // Transient temporary variables
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean bOkPressed = false;
   private boolean closeWindow = false;
   private boolean closeDlg = false;
   private boolean newWarrenty = false;
   private int currRow;
   private ASPQuery q;
   private int headrowno;
   private String sURL ;

   private boolean firstRequest=false;
   private String sObjstate;
   private String sWarrantyId;
   private String sSerialNo;
   private String sContract;
   private String sBomType;
   private String sEngChgLevel;
   private String sPartNo;
   private String newWarrantyId;


   //===============================================================
   // Construction
   //===============================================================
   public CustomerTemplateDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();
      log = mgr.getASPLog();
      trans = mgr.newASPTransactionBuffer();

      if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
      {
         validate();
      }

      if ( "OK".equals(mgr.readValue("FIRST_REQUEST")) || !mgr.isExplorer() ) //this was added to pop up the oracal error messages as there is a bug in IE
      {


         sURL = ctx.readValue("URL");
         sWarrantyId = ctx.readValue("WARRANTYID");
         sObjstate = ctx.readValue("STATE");
         sSerialNo = ctx.readValue("SERIAL");
         sPartNo = ctx.readValue("PARTNO");
         sBomType = ctx.readValue("BOMTYPE");
         sEngChgLevel = ctx.readValue("ENGCHGLEVEL");
         sContract = ctx.readValue("CONTRACT");

         if ( mgr.commandBarActivated() )
         {
            eval(mgr.commandBarFunction());
         }
         else if ( mgr.buttonPressed("OK") )
         {
            checkExist();
            CopyFromTemplate();
            closeDlg = true;
         }
         else if ( !mgr.isEmpty(mgr.getQueryStringValue("SERIAL_NO")) )
         {
            sWarrantyId = mgr.getQueryStringValue("WARRANTY_ID");
            sPartNo = mgr.getQueryStringValue("PART_NO");
            sSerialNo = mgr.getQueryStringValue("SERIAL_NO");
            sContract = mgr.getQueryStringValue("CONTRACT");
            sEngChgLevel = mgr.getQueryStringValue("ENG_CHG_LEVEL");
            sBomType = mgr.getQueryStringValue("BOM_TYPE_DB");

         }


         adjust();
         ctx.writeValue("URL",sURL);
         ctx.writeValue("WARRANTYID",sWarrantyId);
         ctx.writeValue("STATE",sObjstate);
         ctx.writeValue("SERIAL",sSerialNo);
         ctx.writeValue("PARTNO",sPartNo);
         ctx.writeValue("BOMTYPE",sBomType);
         ctx.writeValue("CONTRACT",sContract);
         ctx.writeValue("ENGCHGLEVEL",sEngChgLevel);

      }
      else
      {
         firstRequest = true;
      }

   }

   // --------------------------- Populating Methods ---------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();
      String txt;
      String data;
      String val = mgr.readValue("VALIDATE");
      if ( "TEMPLATE_ID".equals(val) )
      {
         cmd = trans.addCustomFunction("GETTEMPLATEDES","Cust_Warranty_Type_Temp_API.Get_Warranty_Description","DESCRIPTION");
         cmd.addParameter("TEMPLATE_ID",mgr.readValue("TEMPLATE_ID"));
         trans = mgr.validate(trans);

         String sTemplateDes      = trans.getValue("GETTEMPLATEDES/DATA/DESCRIPTION");
         txt = sTemplateDes+"^";
         mgr.responseWrite(txt);

      }
      mgr.endResponse();
   }


   //  -------------------------- Pre-Define ---------------------------------------
   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      disableNavigate();
      disableHomeIcon();

      headblk = mgr.newASPBlock("HEAD");


      f = headblk.addField("TEMPLATE_ID");
      f.setLabel("MFGSTDCUSTOMERTEMPLATEDLGTEMPLATEID: Template Id");
      f.setUpperCase();
      f.setDynamicLOV("CUST_WARRANTY_TYPE_TEMP");
      f.setSize(20);
      f.setCustomValidation("TEMPLATE_ID","DESCRIPTION");

      f = headblk.addField("DESCRIPTION");
      f.setLabel("MFGSTDCUSTOMERTEMPLATEDLGWARRANTYDESCRIPTION: Description");
      f.setFunction("Cust_Warranty_Type_Temp_API.Get_Warranty_Description(:TEMPLATE_ID)");
      f.setSize(30);
      f.setReadOnly();

      f = headblk.addField("EXIST");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("PART_NO");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("SERIAL_NO");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("WARRANTY_ID");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("CONTRACT");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("BOM_TYPE_DB");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("ENG_CHG_LEVEL");
      f.setFunction("''");
      f.setHidden();


      headblk.setView("CUST_WARRANTY_TYPE_TEMP");
      headblk.defineCommand("CUST_WARRANTY_TYPE_TEMP_API","New__");
      headset = headblk.getASPRowSet();
      headtbl = mgr.newASPTable(headblk);
      headbar = mgr.newASPCommandBar(headblk);

      headlay =  headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headlay.setEditable();



   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();

   }
   public void checkExist()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomCommand("CHECKEXIST","Cust_Warranty_Type_Temp_API.Exist");
      cmd.addParameter("TEMPLATE_ID",mgr.readValue("TEMPLATE_ID"));

      mgr.perform(trans);


   }

   public void CopyFromTemplate()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      String sOldObjstate;
      String sOldWarrantyId;
      boolean bSaveOk = true;

      sOldWarrantyId = sWarrantyId;
      sOldObjstate   = sObjstate;
      if ( mgr.isEmpty(sOldWarrantyId) )
      {
         getNewWarranty();
         newWarrenty = true ;
      }
      if ( bSaveOk )
      {
         CopyWarranty();
      }

   }

   private void CopyWarranty()
   {

      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      if ( "Shared".equals(sObjstate) )
      {
         cmd = trans.addCustomCommand("GETID","Cust_Warranty_API.Copy");
         cmd.addParameter("WARRANTY_ID",newWarrantyId);
         cmd.addParameter("WARRANTY_ID",sWarrantyId);
         cmd.addParameter("PART_NO",sPartNo);
         cmd.addParameter("SERIAL_NO",sSerialNo);

         sWarrantyId = newWarrantyId;
         sObjstate = "Unique";

         trans = mgr.perform(trans);
      }

      trans.clear();
      cmd = trans.addCustomCommand("GETID","Cust_Warranty_Type_API.Copy_From_Template");
      cmd.addParameter("WARRANTY_ID",sWarrantyId);
      cmd.addParameter("TEMPLATE_ID",mgr.readValue("TEMPLATE_ID"));

      if ( newWarrenty )
      {
         cmd = trans.addCustomCommand("NEWWARRANTI","PROD_STRUCTURE_HEAD_API.Modify_Warranty");
         cmd.addParameter("CONTRACT",sContract);
         cmd.addParameter("PART_NO",sPartNo);
         cmd.addParameter("ENG_CHG_LEVEL",sEngChgLevel);
         cmd.addParameter("BOM_TYPE_DB",sBomType);
         cmd.addParameter("WARRANTY_ID",sWarrantyId);
      }
      trans = mgr.perform(trans);

   }

   public void getNewWarranty()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomCommand("GETID","Cust_Warranty_API.New");
      cmd.addParameter("WARRANTY_ID",sWarrantyId);
      trans = mgr.perform(trans);
      newWarrantyId = trans.getValue("GETID/DATA/WARRANTY_ID");
      sWarrantyId = newWarrantyId;
   }



   protected String getDescription()
   {
      return "MFGSTDCUSTOMERTEMPLATEDLGDESCRIPTION: Copy From Template";
   }

   protected String getTitle()
   {
      ASPManager mgr = getASPManager();
      return "MFGSTDCUSTOMERTEMPLATEDLGTITLE: Copy From Template ";

   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      ASPManager mgr = getASPManager();
      String queryString = mgr.getURL()+"?"+mgr.getQueryString();

      if ( firstRequest && mgr.isExplorer() ) ////this was added to pop up the oracal error messages as there is a bug in IE
      {
         out.append("<html>\n");
         out.append("<head></head>\n");
         out.append("<body>");
         out.append("<form name='form' method='POST' action='"+queryString+"'>");
         out.append(" <input type=\"hidden\" name=\"FIRST_REQUEST\" >");
         out.append("</form></body></html>");
         appendDirtyJavaScript("document.form.FIRST_REQUEST.value='OK';document.form.submit();");
         return out;
      }
      else
      {
         out.append("<html>\n");
         out.append("<head>\n");
         out.append(mgr.generateHeadTag(getDescription()));
         out.append("</head>\n");
         out.append("<body ");
         out.append(mgr.generateBodyTag());
         out.append(" >\n");
         out.append("<div id=\"tooltip\" class=\"tooltip\" style=\"border:1px solid black;position:absolute;visibility:hidden;\"></div>");
         out.append("<form ");
         out.append(mgr.generateFormTag());
         out.append(" >\n");
         out.append(" <input type=\"hidden\" name=\"FIRST_REQUEST\" value=\"OK\">\n");
         out.append("  <input type=\"hidden\" name=\"REFRESH\" value=\"FALSE\">\n");
         out.append(mgr.startPresentation(getTitle()));
         printContents();

         if ( closeDlg )
         {
            appendDirtyJavaScript("   window.opener.repopulate('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(sWarrantyId));
            appendDirtyJavaScript("','");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(sContract));
            appendDirtyJavaScript("','");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(sPartNo));
            appendDirtyJavaScript("','");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(sBomType));
            appendDirtyJavaScript("','");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(sEngChgLevel));
            appendDirtyJavaScript("');\n");
            appendDirtyJavaScript("   window.close();     \n");
         }

         out.append(mgr.endPresentation());

         out.append("</form>\n");
         out.append("</body>\n");
         out.append("</html>\n");
         return out;
      }
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.append(headlay.show());

      beginTable();
      beginTableBody();
      nextTableRow();
      beginTableCell("right");

      printSubmitButton("OK",mgr.translate("MFGSTDCUSTOMERTEMPLATEDLGOKBUTTON:       Ok      "),"");
      printSpaces(1);
      printSubmitButton("CANCEL",mgr.translate("MFGSTDCUSTOMERTEMPLATEDLGCANCELBUTTON:    Close    "),"onClick=window.close();");
      printSpaces(8);
      endTableCell();
      endTableBody();
      endTable();
   }

}

