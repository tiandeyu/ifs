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
*  File        : MoveNonSerialToInventDlg.java 
*  Created     : ASP2JAVA Tool  010425
*  Modified    :
*  JEWILK  010426  Modified and done necessary changes.
*  JEWILK  010815  Increased length of field 'REPAIR_PART_DESCR'.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
* ------------------------------  EDGE - SP1 Merge ---------------------------
*  SHAFLK  040223  Bug Id 42844,Modified saveReturn() and preDefine() methods.   
*  VAGULK  040324  Merged with SP1.
*  ARWILK  041111  Replaced getContents with printContents.
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  RaKalk  051118  Added Cost detail functionality. Added fields COST_DETAIL_INFO, DUMMY1..DUMMY3
*          051118  Modified functions preDefine, valite, saveReturn
*  RaKalk  051122  Made LSCLIENTVALUE and LSINSUPART hidden to avoid the javascript errors.
*  ranflk  060306  Call 136359  Modified predefine()
*  NIJALK  060306  Call 136366, Modified preDefine().
*  SULILK  060308  Call 136361: Modified invParts().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.lang.reflect.Method;

public class MoveNonSerialToInventDlg extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MoveNonSerialToInventDlg");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private String CancelFlag;
   private String FinishFlag;
   private String FinishText;
   private String val;
   private ASPQuery q;
   private ASPCommand cmd;
   private String lsInsurancePart;
   private String lsClientValue;
   private String IsLocation;
   private ASPBuffer r;
   private String varNote;
   private String woNo;
   private String dfPartNo;
   private String dfSite;
   private String dfLocation;
   private String part1;
   private String part2;
   private String formTitle;
   private String woNoForTitle;
   private String err_descr;
   private String noteText; 

   //===============================================================
   // Construction 
   //===============================================================
   public MoveNonSerialToInventDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();

      CancelFlag = ""; 
      FinishFlag = ""; 
      FinishText = "";


      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
         err_descr = mgr.getQueryStringValue("ERR_DESCR");
         okFind();      
      }

      adjust();
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void validate()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String txt="";
      val = mgr.readValue("VALIDATE");

      if ("COST_DETAIL_INFO".equals(val))
      {
         trans.clear();

         //You do not have to check the availability of the modules here
         //since the validation will not be invoked if the invent is not available
         cmd = trans.addCustomFunction("CHECK_INV_PART","Inventory_Part_API.Part_Exist","COST_DETAIL_INFO");
         cmd.addParameter("REPAIR_PART_CONTRACT",mgr.readValue("REPAIR_PART_CONTRACT"));
         cmd.addParameter("REPAIR_PART_NO",mgr.readValue("REPAIR_PART_NO"));

         cmd = trans.addCustomFunction("GET_ZERO_COST","Inventory_Part_API.Get_Zero_Cost_Flag","DUMMY1");
         cmd.addParameter("REPAIR_PART_CONTRACT",mgr.readValue("REPAIR_PART_CONTRACT"));
         cmd.addParameter("REPAIR_PART_NO",mgr.readValue("REPAIR_PART_NO"));

         cmd = trans.addCustomFunction("ENCODE_ZERO_COST","Inventory_Part_Zero_Cost_API.Encode","COST_DETAIL_INFO");
         cmd.addReference("DUMMY1","GET_ZERO_COST/DATA");

         cmd = trans.addCustomFunction("CHECK_STD_COST","Inventory_Part_Unit_Cost_API.Standard_Cost_Exist","COST_DETAIL_INFO");
         cmd.addParameter("REPAIR_PART_CONTRACT",mgr.readValue("REPAIR_PART_CONTRACT"));
         cmd.addParameter("REPAIR_PART_NO",mgr.readValue("REPAIR_PART_NO"));
         cmd.addParameter("DUMMY2","*");
         cmd.addParameter("LOT_BATCH_NO",mgr.readValue("LOT_BATCH_NO"));
         cmd.addParameter("DUMMY3","*");
         cmd.addParameter("DUMMY1","");

         trans = mgr.validate(trans);

         String isInventoryPart  = trans.getValue("CHECK_INV_PART/DATA/COST_DETAIL_INFO");
         String stdCostExist     = trans.getValue("CHECK_STD_COST/DATA/COST_DETAIL_INFO");
         String zeroCostFlag     = trans.getValue("ENCODE_ZERO_COST/DATA/COST_DETAIL_INFO");

         trans.clear();

         if ("0".equals(isInventoryPart) ||
             "0".equals(zeroCostFlag)    ||
             "TRUE".equals(stdCostExist))
            txt = "";
         else
         {
            StringBuffer sb = new StringBuffer();

            addQueryStringField(sb,"CONTRACT",mgr.readValue("REPAIR_PART_CONTRACT"));
            addQueryStringField(sb,"PART_NO",mgr.readValue("REPAIR_PART_NO"));
            addQueryStringField(sb,"SERIAL_NO","*");
            addQueryStringField(sb,"CONDITION_CODE","");
            addQueryStringField(sb,"CONFIGURATION_ID","*");
            addQueryStringField(sb,"LOT_BATCH_NO",mgr.readValue("LOT_BATCH_NO"));
            addQueryStringField(sb,"CALLING_PROCESS","WORK ORDER RECEIPT");

            txt = sb.toString();
         }
      }
      mgr.responseWrite(txt);
      mgr.endResponse();
   }

   private void addQueryStringField(StringBuffer sb, String name, String value)
   {
      ASPManager mgr = getASPManager();
      name = mgr.URLEncode(name);
      value = mgr.URLEncode(mgr.nvl(value,""));
      if (sb.length()>0)
         sb.append("&");
      sb.append(name + "=" + value);
   }


//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.submit(trans);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWMOVENONSERIALTOINVENTDLGNODATA: No data found."));
         headset.clear();
      }

      invParts();  
   }

   public void invParts()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows()>0)
      {
          trans.clear();
          cmd = trans.addCustomFunction("INSUPART","Inventory_Part_API.Get_Zero_Cost_Flag","LSINSUPART");
          cmd.addParameter("REPAIR_PART_CONTRACT",headset.getRow().getValue("REPAIR_PART_CONTRACT"));
          cmd.addParameter("REPAIR_PART_NO",headset.getRow().getValue("REPAIR_PART_NO"));

          cmd = trans.addCustomFunction("CLIENTVAL","Inventory_Part_Zero_Cost_API.Get_Client_Value(2)","LSCLIENTVALUE" );

          cmd = trans.addCustomFunction("DEFLOC","Inventory_Part_Def_Loc_API.Get_Location_No","LOCATION");
          cmd.addParameter("REPAIR_PART_CONTRACT",headset.getRow().getValue("REPAIR_PART_CONTRACT"));
          cmd.addParameter("REPAIR_PART_NO",headset.getRow().getValue("REPAIR_PART_NO"));

          cmd = trans.addCustomFunction("GETINVVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","INVENTORYVALUE");
          cmd.addParameter("REPAIR_PART_CONTRACT",headset.getRow().getValue("REPAIR_PART_CONTRACT"));
          cmd.addParameter("REPAIR_PART_NO",headset.getRow().getValue("REPAIR_PART_NO"));

          trans=mgr.perform(trans);

          lsInsurancePart = trans.getValue("INSUPART/DATA/LSINSUPART");
          lsClientValue = trans.getValue("CLIENTVAL/DATA/LSCLIENTVALUE");
          IsLocation = trans.getValue("DEFLOC/DATA/LOCATION");

          double invValue = trans.getBuffer("GETINVVAL/DATA").getNumberValue("INVENTORYVALUE");

          r = headset.getRow();

          if (lsClientValue.equals(lsInsurancePart))
          {
             r.setValue("CBACCOUNTED","TRUE"); 
          }
          else
          {
             r.setValue("CBACCOUNTED","FALSE"); 

             if(isNaN(invValue))
                 r.setNumberValue("INVENTORYVALUE",0);
             else
                 r.setNumberValue("INVENTORYVALUE",invValue);
          }  
                                                      
          if (!mgr.isEmpty(IsLocation))
              r.setValue("LOCATION",IsLocation);

          varNote = mgr.translate("PCMWMOVENONSERIALTOINVENTDLGTEXT: Auto Repair WO ");
          woNo = headset.getRow().getValue("WO_NO");
          noteText = varNote+woNo;

          r.setValue("NOTE",noteText); 
          headset.setRow(r);
          trans.clear();
      }
   }   

   public void cancel()
   {

      CancelFlag = "true";
   }

   public void saveReturn()
   {
      ASPManager mgr = getASPManager();

      headset.changeRow();
      ASPTransactionBuffer secBuf = mgr.newASPTransactionBuffer();
      secBuf.addSecurityQuery("Active_Separate_API","Move_Non_Serial_To_Inventory");

      secBuf = mgr.perform(secBuf);

      if (!(secBuf.getSecurityInfo().itemExists("Active_Separate_API.Move_Non_Serial_To_Inventory")))
         mgr.showAlert(mgr.translate("PCMWMOVENONSERIALTOINVENTDLGNOTPER: Cannot move Non Serial to Inventory."));
      else
      {
         cmd = trans.addCustomCommand("VALUE1","Active_Separate_API.Move_Non_Serial_To_Inventory");
         cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
         cmd.addParameter("REPAIR_PART_CONTRACT",headset.getRow().getValue("REPAIR_PART_CONTRACT"));
         cmd.addParameter("REPAIR_PART_NO",headset.getRow().getValue("REPAIR_PART_NO"));
         cmd.addParameter("LOCATION",headset.getRow().getValue("LOCATION"));
         cmd.addParameter("MCH_CODE_CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
         cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
         cmd.addParameter("COST_DETAIL_INFO",headset.getRow().getValue("COST_DETAIL_INFO"));
         cmd.addParameter("NOTE",headset.getRow().getValue("NOTE"));  
         cmd.addParameter("LOT_BATCH_NO",mgr.readValue("LOT_BATCH_NO"));
      }

      trans=mgr.perform(trans);  

      trans.clear();

      dfPartNo = headset.getRow().getValue("REPAIR_PART_NO");
      dfSite = headset.getRow().getValue("REPAIR_PART_CONTRACT");
      dfLocation = headset.getRow().getValue("LOCATION");
      part1 = mgr.translate("PCMWMOVENONSERIALTOINVENTDLGPART1: The Inventory Part ");
      part2 = mgr.translate("PCMWMOVENONSERIALTOINVENTDLGPART2: is now moved to inventory location ");
      FinishText = part1 + dfPartNo + " " + part2 + dfSite + " - " + dfLocation; 
      FinishFlag = "true";
   }

   public void preDefine()throws FndException
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("MCH_CODE");
      f.setSize(25);
      f.setMaxLength(100);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGMCH_CODE: Object ID");
      f.setUpperCase();
      f.setReadOnly();

      f = headblk.addField("CONTRACT");
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGORGCON: Maint.Org. Site");
      f.setHidden();

      f = headblk.addField("MCH_CODE_CONTRACT");
      f.setSize(9);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGOBJCON: Site");
      f.setUpperCase();
      f.setReadOnly();   

      f = headblk.addField("MCHNAME");
      f.setSize(25);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGMCHNAME: Object Description");
      f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:MCH_CODE_CONTRACT,:MCH_CODE)");
      mgr.getASPField("MCH_CODE_CONTRACT").setValidation("MCHNAME");
      f.setReadOnly();   

      f = headblk.addField("REPAIR_PART_NO");
      f.setSize(25);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGREPAIR_PART_NO: Part No");
      f.setUpperCase();
      f.setReadOnly();   

      f = headblk.addField("REPAIR_PART_DESCR");
      f.setSize(50);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGREPAIR_PART_DESCR: Part Description");
      f.setReadOnly();   

      f = headblk.addField("REPAIR_PART_CONTRACT");
      f.setSize(9);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGREPAIR_PART_CONTRACT: Site");
      f.setUpperCase();
      f.setReadOnly();   

      f = headblk.addField("LOCATION");
      f.setSize(45);
      f.setDynamicLOV("INVENTORY_LOCATION13","CONTRACT",600,445);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGLOCATION: Location");
      f.setFunction("''");

      f = headblk.addField("INVENTORYVALUE", "Number");
      f.setSize(15);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGINVENTORYVALUE: Inventory Value");
      f.setReadOnly();   
      f.setFunction("''");
      f.setReadOnly();

      f = headblk.addField("LOT_BATCH_NO");
      f.setSize(20);
      f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSLOTBATCHNO: Lot Batch No");
      f.setMaxLength(20);

      f = headblk.addField("CBACCOUNTED");
      f.setSize(10);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGCBACCOUNTED: Accounted");
      f.setFunction("''");
      f.setReadOnly();
      f.setCheckBox("FALSE,TRUE");

      f = headblk.addField("NOTE");
      f.setSize(45);
      f.setMaxLength(25);
      f.setLabel("PCMWMOVENONSERIALTOINVENTDLGNOTE: Note");
      f.setFunction("''");

      f = headblk.addField("WO_NO", "Number");
      f.setSize(8);
      f.setHidden();

      f = headblk.addField("DESCRIPTION");
      f.setSize(45);
      f.setHidden();
      f.setFunction("''");   

      f = headblk.addField("LSINSUPART");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("LSCLIENTVALUE");
      f.setFunction("''"); 
      f.setHidden();

      f = headblk.addField("COST_DETAIL_INFO");
      f.setFunction("NULL");
      f.setCustomValidation("REPAIR_PART_NO,REPAIR_PART_CONTRACT,LOT_BATCH_NO","COST_DETAIL_INFO");

      headblk.addField("DUMMY1").
      setFunction("NULL").
      setHidden();

      headblk.addField("DUMMY2").
      setFunction("NULL").
      setHidden();

      headblk.addField("DUMMY3").
      setFunction("NULL").
      setHidden();

      headblk.setView("ACTIVE_SEPARATE_REPAIR");
      headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);

      headtbl = mgr.newASPTable(headblk);
      headlay = headblk.getASPBlockLayout();

      headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
      headbar.enableCommand(headbar.SAVERETURN);
      headbar.enableCommand(headbar.CANCELNEW);

      headlay.setEditable();

      headlay.defineGroup(mgr.translate("PCMWMOVENONSERIALTOINVENTDLGGRPLABEL1: Object on Repair WO"), "MCH_CODE,MCH_CODE_CONTRACT,MCHNAME", true, true);
      headlay.defineGroup(mgr.translate("PCMWMOVENONSERIALTOINVENTDLGGRPLABEL2: Repaired Part"), "REPAIR_PART_NO,REPAIR_PART_DESCR,REPAIR_PART_CONTRACT", true, true);
      headlay.defineGroup(mgr.translate("PCMWMOVENONSERIALTOINVENTDLGGRPLABEL3: Inventory Location"), "LOCATION,INVENTORYVALUE,LOT_BATCH_NO,CBACCOUNTED,NOTE", true, true);
      headbar.defineCommand(headbar.SAVERETURN, "saveReturn","checkBeforeSave");
      headbar.defineCommand(headbar.CANCELNEW, "cancel"); 

      //Ganerate the java scripts for the Define Cost Structure dialog
      //Since the inventory is a dynnamic for PCM we need to use reflects
      if (mgr.isPresentationObjectInstalled("invenw/DefineCostStructure.page"))
      {
         try
         {
            //This array of classes is used to identify the method segnature
            Class fourStrings[] = {String.class,String.class,String.class,String.class};

            Class defineCostDetails = Class.forName("ifs.invenw.DefineCostStructure");

            //Generate the script which loads the popup window
            Method m = defineCostDetails.getMethod("getPopupWindowScript",fourStrings);

            String script = (String)m.invoke(null,
                                             new Object[]{
                                                "defineCostStructure",
                                                "COST_DETAIL_INFO",
                                                "validateCostDetailInfo",
                                                "defineCostStructureCallback"
                                             });
            appendJavaScript(script);

            //Generate the script for call back function
            m = defineCostDetails.getMethod("getDefaultCallbackFunctionScript",fourStrings);

            script = (String)m.invoke(null,
                                      new Object[]{
                                         "defineCostStructureCallback",
                                         "COST_DETAIL_INFO",
                                         "HEAD.SaveReturn",
                                         ""
                                      });
            appendJavaScript(script);

            appendJavaScript("function checkBeforeSave(){\n");
            appendJavaScript("   return checkHeadFields(-1) && !defineCostStructure(-1);\n");
            appendJavaScript("}\n");
         }
         catch (Exception e)
         {
            e.printStackTrace();
            throw new RuntimeException(mgr.translate("PCMWMOVENONSERIALTOINVGENCOSTDIALOGCODEERROR: An error occured while generating Javascript code for Define Cost Structure dialog"),e);
         }
      }
      else
      {
         appendJavaScript("function checkBeforeSave(){\n");
         appendJavaScript("   return checkHeadFields(-1);\n");
         appendJavaScript("}\n"); 
      }

      enableConvertGettoPost();

   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      formTitle = mgr.translate("PCMWMOVENONSERIALTOINVENTDLGMOVE: Move Part To Inventory on Work Order - ");

      woNoForTitle = "";

      if (headset.countRows() > 0)
         woNoForTitle = headset.getRow().getValue("WO_NO") + " " + err_descr;

      mgr.getASPField("COST_DETAIL_INFO").setHidden();
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return formTitle + woNoForTitle;
   }

   protected String getTitle()
   {
      return "PCMWMOVENONSERIALTOINVENTDLGTITLE: Move Part To Inventory on Work Order";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendToHTML(headlay.show());

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(CancelFlag);
      appendDirtyJavaScript("'=='true')\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    self.close();\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(FinishFlag);
      appendDirtyJavaScript("'=='true')\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    alert('");
      appendDirtyJavaScript(mgr.translateJavaScript(FinishText));
      appendDirtyJavaScript("'); \n");
      appendDirtyJavaScript("    self.close();\n");
      appendDirtyJavaScript("}\n");
   }
}
