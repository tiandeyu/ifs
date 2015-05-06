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
*  File        : ActiveSeparateDlg.java 
*  Created     : ASP2JAVA Tool  010305
*  Modified    :
*  SHFELK  010305  Corrected some conversion errors.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  GACOLK  021204  Set Max Length of SERIAL_NO to 50
*  NIMHLK  030109  Added two fields Condition Code and Condition Code Description according to specification W110 - Condition Codes.
*  CHAMLK  031014  Used INVENTORY_PART_COST_API.Get_Cost method to fetch the correct inventory value for the part. 
*  SAPRLK  031222  Web Alignment - removed methods clone() and doReset().
*  ARWILK  041005  LCS Merge: 45565.
*  NAMELK  041104  Duplicated Translation Tags Corrected.
*  ARWILK  041115  Replaced getContents with printContents.
*  SHAFLK  050107  Bug 48647, Modified methods ok() and predefine().
*  NIJALK  050121  Merged bug 48647. 
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  RaKalk  051114  Added Cost details functionality. Added fields COST_DETAIL_INFO and DUMMY
*  NIJALK  060317  Call 136370: Increased the length of Array isSecure[] upto 6.
*  SULILK  060321  Call 135197: Modified startup() and validate().
*  AMNILK  060726  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding. 
*  AMNILK  060807  Merged Bug Id: 58214.
*  ILSOLK  070706  Eliminated XSS.
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071214  ILSOLK  Bug Id 68773, Eliminated XSS.
* -----------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.lang.reflect.Method;

public class ActiveSeparateDlg extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateDlg");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPBlock tempblk;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private String wo_no;
   private String swono;
   private String err_descr;
   private String contract;
   private String mch_code;
   private String site;
   private String location_no;
   private String condition_code;
   private String condition_desc;
   private ASPTransactionBuffer trans;
   private ASPBuffer buff;
   private ASPBuffer row;
   private ASPTransactionBuffer secBuff;
   private ASPBuffer newBuff;
   private ASPCommand cmd;
   private String insuarancepart;
   private String clientval;
   private String mchname;
   private String serial_no;
   private String part_no;
   private String inventoryvalue;
   private String sitedescription;
   private String val;
   private String name;
   private String strSerialNo;
   private String strPartNo;
   private double nInvenValue;
   private ASPBuffer buffer;
   private String title;
   private String isSecure[]=new String[6]; 
   private String fmtdnInvenValue; 
   private String txt; 
   private String movedOk;
   private String cancelOk;
   private String con_type;
   private String space;

   //===============================================================
   // Construction 
   //===============================================================
   public ActiveSeparateDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      wo_no = "";
      swono = "";
      err_descr = "";
      contract = "";
      mch_code = "";
      site = "";
      location_no = "";
      isSecure =  new String[6] ;
      ASPManager mgr = getASPManager();


      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      wo_no  = ctx.readValue("CTX1",wo_no);
      err_descr= ctx.readValue("CTX2",err_descr);
      contract = ctx.readValue("CTX3",contract);
      mch_code = ctx.readValue("CTX4",mch_code);
      movedOk = ctx.readValue("MOVEDOK","");
      cancelOk = ctx.readValue("CANCELOK","");
      con_type = ctx.readValue("CONTYPE","");
      space = ctx.readValue("SPACE","");

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.buttonPressed("OK"))
         ok();
      else if (mgr.buttonPressed("CANCEL"))
         cancel();
      else if (mgr.dataTransfered())
      {
         buff = mgr.getTransferedData();

         row = buff.getBufferAt(0); 
         wo_no = row.getValue("WO_NO");
         row = buff.getBufferAt(1); 
         err_descr= row.getValue("ERR_DESCR");
         row = buff.getBufferAt(2); 
         contract = row.getValue("CONTRACT");
         row = buff.getBufferAt(3); 
         mch_code = row.getValue("MCH_CODE");
         startup();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
         wo_no = mgr.readValue("WO_NO","");
         err_descr = mgr.readValue("ERR_DESCR","");
         contract = mgr.readValue("CONTRACT","");
         mch_code = mgr.readValue("MCH_CODE","");
         con_type = mgr.readValue("CONNECTION_TYPE","");
         startup();
      }
      else
         startup();

      adjust();

      ctx.writeValue("CTX1",wo_no);
      ctx.writeValue("CTX2",err_descr);
      ctx.writeValue("CTX3",contract);
      ctx.writeValue("CTX4",mch_code);
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

   public boolean  checksec( String method,int ref)
   {

      isSecure = new String[6];  

      ASPManager mgr = getASPManager();

      isSecure[ref] = "false" ;
      String splitted[] = split(method,".");

      String first = splitted[0].toString();
      String Second = splitted[1].toString();

      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery(first,Second);
      secBuff = mgr.perform(secBuff);
      if (secBuff.getSecurityInfo().itemExists(method))
      {
         isSecure[ref] = "true";
         return true; 
      }
      else
         return false;
   }

   public void startup()
   {
      double invValue;
      ASPManager mgr = getASPManager();

      // All the data for this dialog are retrieved by these functions and set to the corresponding fields.

      newBuff = mgr.newASPBuffer();

      trans.clear();
      if (!mgr.isEmpty(mch_code))
      {
         if ("VIM".equals(con_type))
         {
            cmd = trans.addCustomCommand("CONTR", "Maintenance_Spare_API.Get_Default_Contract");
            cmd.addParameter("SITE");

            if (checksec("Work_Order_From_Vim_API.Get_Serial",1))
            {
               cmd = trans.addCustomCommand("SERIAL","Work_Order_From_Vim_API.Get_Serial");
               cmd.addParameter("PART_NO");
               cmd.addParameter("SERIAL_NO");
               cmd.addParameter("WO_NO",wo_no);
            }
            trans = mgr.perform(trans);

            site = trans.getValue("CONTR/DATA/SITE");
            if (isSecure[1] =="true")
            {
               part_no = trans.getValue("SERIAL/DATA/PART_NO");
               serial_no = trans.getValue("SERIAL/DATA/SERIAL_NO");
            }
            else
            {
               part_no = "";
               serial_no= "";
            }

            trans.clear();

            cmd = trans.addCustomFunction("MCHNAM","Active_Separate_API.Get_Mch_Code_Description","MCHNAME");
            cmd.addParameter("WO_NO",wo_no);

            cmd = trans.addCustomFunction("CONDITION","Part_Serial_Catalog_API.Get_Condition_Code","CONDITION_CODE");
            cmd.addParameter("PART_NO",part_no);
            cmd.addParameter("SERIAL_NO",serial_no);

            cmd = trans.addCustomFunction("CONDITIONDESC","Condition_Code_API.Get_Description","CONDDESC");
            cmd.addReference("CONDITION_CODE","CONDITION/DATA");

            trans = mgr.perform(trans);

            mchname= trans.getValue("MCHNAM/DATA/MCHNAME");
            condition_code = trans.getValue("CONDITION/DATA/CONDITION_CODE");
            condition_desc = trans.getValue("CONDITIONDESC/DATA/CONDDESC");
         }
         else
         {
            trans.clear();

            cmd = trans.addCustomCommand("CONTR", "Maintenance_Spare_API.Get_Default_Contract");
            cmd.addParameter("SITE");

            cmd = trans.addCustomFunction("MCHNAM","Maintenance_Object_API.Get_Mch_Name","MCHNAME");
            cmd.addParameter("CONTRACT",contract);
            cmd.addParameter("MCH_CODE",mch_code);

            cmd = trans.addCustomFunction("SERIAL","Maintenance_Object_API.Get_Serial_No","SERIAL_NO");
            cmd.addParameter("CONTRACT",contract);
            cmd.addParameter("MCH_CODE",mch_code);

            cmd = trans.addCustomFunction("PART","Maintenance_Object_API.Get_Part_No","PART_NO");
            cmd.addParameter("CONTRACT",contract);
            cmd.addParameter("MCH_CODE",mch_code);

            cmd = trans.addCustomFunction("CONDITION","Part_Serial_Catalog_API.Get_Condition_Code","CONDITION_CODE");
            cmd.addReference("PART_NO","PART/DATA");
            cmd.addReference("SERIAL_NO","SERIAL/DATA");

            cmd = trans.addCustomFunction("CONDITIONDESC","Condition_Code_API.Get_Description","CONDDESC");
            cmd.addReference("CONDITION_CODE","CONDITION/DATA");

            trans = mgr.perform(trans);

            mchname= trans.getValue("MCHNAM/DATA/MCHNAME");
            serial_no = trans.getValue("SERIAL/DATA/SERIAL_NO");
            part_no = trans.getValue("PART/DATA/PART_NO");
            site = trans.getValue("CONTR/DATA/SITE");
            condition_code = trans.getValue("CONDITION/DATA/CONDITION_CODE");
            condition_desc = trans.getValue("CONDITIONDESC/DATA/CONDDESC");
         }
      }

      newBuff.setValue("SITE",site);
      newBuff.setValue("MCH_CODE",mch_code);
      newBuff.setValue("MCHNAME",mchname);
      newBuff.setValue("CONTRACT",contract);
      newBuff.setValue("PART_NO",part_no);
      newBuff.setValue("SERIAL_NO",serial_no);
      newBuff.setValue("CONDITION_CODE",condition_code);
      newBuff.setValue("CONDDESC",condition_desc);

      if (!mgr.isEmpty(part_no))
      {
         trans.clear();
         /*if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",1))
         {
            cmd = trans.addCustomFunction("INVENTVALUE","Inventory_Part_API.Get_Inventory_Value_By_Method","INVENTORYVALUE");
            cmd.addReference("SITE","CONTR/DATA");
            cmd.addParameter("PART_NO",part_no);
         }
         
         if (isSecure[1] =="true")
         {
            inventoryvalue = trans.getValue("INVENTVALUE/DATA/INVENTORYVALUE");
         }
         else
            inventoryvalue	= "";*/

         //trans = mgr.perform(trans);

         // Begin Invent cost fetching

         cmd = trans.addCustomFunction("OWNERSHIPCLI","PART_SERIAL_CATALOG_API.Get_Part_Ownership","PART_OWNERSHIP");
         cmd.addParameter("PART_NO",part_no);
         cmd.addParameter("SERIAL_NO",serial_no);

         cmd = trans.addCustomFunction("OWNERSHIPDB","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP_DB");
         cmd.addReference("PART_OWNERSHIP","OWNERSHIPCLI/DATA");

         trans = mgr.perform(trans);

         String sConfigId = ((mgr.isEmpty(mgr.readValue("CONFIGURATION_ID")))?"*":mgr.readValue("CONFIGURATION_ID"));
         String sCustOwned = trans.getValue("OWNERSHIPDB/DATA/PART_OWNERSHIP_DB");

         if ("CUSTOMER OWNED".equals(sCustOwned))
         {
            invValue = 0;
            inventoryvalue = mgr.getASPField("INVENTORYVALUE").formatNumber(invValue);
         }
         else
         {
            /*if (checksec("INVENTORY_PART_COST_API.Get_Cost",2))
            {
               trans.clear();
               cmd = trans.addCustomFunction("GETINVVAL","INVENTORY_PART_COST_API.Get_Cost","INVENTORYVALUE");
               cmd.addParameter("SITE",site);
               cmd.addParameter(      "PART_NO",part_no);
               cmd.addParameter("CONFIGURATION_ID", sConfigId);
               cmd.addParameter("CONDITION_CODE", condition_code);

               trans = mgr.perform(trans);

               invValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORYVALUE");
               inventoryvalue = mgr.getASPField("INVENTORYVALUE").formatNumber(invValue);
            } */

            if (checksec("Active_Separate_API.Get_Inventory_Value",2))
            {
               trans.clear();
               cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
               cmd.addParameter("INVENTORYVALUE");
               cmd.addParameter("SITE",site);
               cmd.addParameter("PART_NO",part_no);
               cmd.addParameter("SERIAL_NO",serial_no);
               cmd.addParameter("CONFIGURATION_ID",sConfigId);
               cmd.addParameter("CONDITION_CODE", condition_code);

               trans = mgr.perform(trans);

               invValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORYVALUE");
               inventoryvalue = mgr.getASPField("INVENTORYVALUE").formatNumber(invValue);
            } 
         }

         if (isSecure[2] !="true")
            inventoryvalue = "0";

         // End of invent cost fetching
         newBuff.setValue("INVENTORYVALUE",inventoryvalue);
         newBuff.setValue("PART_OWNERSHIP_DB",sCustOwned);
      }
      trans.clear();
      if (checksec("Inventory_Part_API.Get_Zero_Cost_Flag",3))
      {
         cmd = trans.addCustomFunction("INSUAR","Inventory_Part_API.Get_Zero_Cost_Flag","INSUARANCEPART");
         cmd.addParameter("CONTRACT",contract);
         cmd.addReference("PART_NO",part_no);
      }

      if (checksec("Inventory_Part_Zero_Cost_API.Get_Client_Value",4))
      {
         cmd = trans.addCustomFunction("IND","Inventory_Part_Zero_Cost_API.Get_Client_Value","CLIENTVAL");
         cmd.addParameter("INDEX","0");
      }

      trans = mgr.perform(trans);

      if (isSecure[3] =="true")
      {
         insuarancepart = trans.getValue("INSUAR/DATA/INSUARANCEPART");
      }
      else
         insuarancepart  = "";
      if (isSecure[4] =="true")
      {
         clientval = trans.getValue("IND/DATA/CLIENTVAL");
      }
      else
         clientval = "";

      if (clientval.equals(insuarancepart))
         newBuff.setValue("CBACCOUNTED","TRUE");

      if ((!mgr.isEmpty(part_no)) && (!mgr.isEmpty(site)))
      {
         trans.clear();

         cmd = trans.addCustomCommand("CONTR", "Maintenance_Spare_API.Get_Default_Contract");
         cmd.addParameter("SITE");

         cmd = trans.addCustomFunction("SITEDESCR","Site_API.Get_Description","SITEDESCRIPTION");
         cmd.addReference("SITE","CONTR/DATA");
         if (checksec("Inventory_Part_Def_Loc_API.Get_Location_No",5))
         {
            cmd = trans.addCustomFunction("LOCATION","Inventory_Part_Def_Loc_API.Get_Location_No","LOCATION_NO");
            cmd.addReference("SITE","CONTR/DATA");
            cmd.addParameter("PART_NO",part_no);
         }


         trans = mgr.perform(trans);
         if (isSecure[5] =="true")
         {
            location_no = trans.getValue("LOCATION/DATA/LOCATION_NO");
         }
         else
            location_no = "";
         sitedescription = trans.getValue("SITEDESCR/DATA/SITEDESCRIPTION");

         newBuff.setValue("LOCATION_NO",location_no);
         newBuff.setValue("SITEDESCRIPTION",sitedescription);

      }

      headset.addRow(newBuff);
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
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

      double inventValue;
      String sConfigurationId;
      String sConditionCode;
      String sPartOwnershipDb;

      if ("MCH_CODE".equals(val))
      {
         cmd = trans.addCustomFunction("MCHNAM","Maintenance_Object_API.Get_Mch_Name","MCHNAME");
         cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT",""));
         cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE",""));

         cmd = trans.addCustomFunction("SERIALV","Maintenance_Object_API.Get_Serial_No","SERIAL_NO");
         cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT",""));
         cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE",""));

         cmd = trans.addCustomFunction("PARTV","Maintenance_Object_API.Get_Part_No","PART_NO");
         cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT",""));
         cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE",""));
         /*if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",1))
         {
            cmd = trans.addCustomFunction("INVENVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","INVENTORYVALUE");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT",""));
            cmd.addReference("PART_NO","PARTV/DATA");
         }*/

         cmd = trans.addCustomFunction("OWNERSHIPCLI","PART_SERIAL_CATALOG_API.Get_Part_Ownership","PART_OWNERSHIP");
         cmd.addReference("PART_NO","PARTV/DATA");
         cmd.addReference("SERIAL_NO","SERIALV/DATA");

         cmd = trans.addCustomFunction("OWNERSHIPDB","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP_DB");
         cmd.addReference("PART_OWNERSHIP","OWNERSHIPCLI/DATA");

         cmd = trans.addCustomFunction("CONDITIONCODE","PART_SERIAL_CATALOG_API.Get_Condition_Code","CONDITION_CODE");
         cmd.addReference("PART_NO","PARTV/DATA");
         cmd.addReference("SERIAL_NO","SERIALV/DATA");

         sConfigurationId = ((mgr.isEmpty(mgr.readValue("CONFIGURATION_ID")))?"*":mgr.readValue("CONFIGURATION_ID"));

         trans = mgr.validate(trans);

         name = trans.getValue("MCHNAM/DATA/MCHNAME");
         strSerialNo= trans.getValue("SERIALV/DATA/SERIAL_NO");
         strPartNo= trans.getValue("PARTV/DATA/PART_NO");
         sConditionCode = trans.getValue("CONDITIONCODE/DATA/CONDITION_CODE");
         sPartOwnershipDb = trans.getValue("OWNERSHIPDB/DATA/PART_OWNERSHIP_DB");

         if ("CUSTOMER OWNED".equals(sPartOwnershipDb))
         {
            inventValue = 0.00;
            fmtdnInvenValue = mgr.formatNumber("INVENTORYVALUE",inventValue);
         }
         else
         {
            /*if (checksec("INVENTORY_PART_COST_API.Get_Cost",1))
            {
               trans.clear();
               cmd = trans.addCustomFunction("GETINVVAL","INVENTORY_PART_COST_API.Get_Cost","INVENTORYVALUE");
               cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT",""));
               cmd.addParameter("PART_NO",strPartNo);
               cmd.addParameter("CONFIGURATION_ID", sConfigurationId);
               cmd.addParameter("CONDITION_CODE", sConditionCode);

               trans = mgr.validate(trans);

               inventValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORYVALUE");
               fmtdnInvenValue = mgr.formatNumber("INVENTORYVALUE",inventValue);
            } */

            if (checksec("Active_Separate_API.Get_Inventory_Value",1))
            {
               trans.clear();
               cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
               cmd.addParameter("INVENTORYVALUE");
               cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT",""));
               cmd.addParameter("PART_NO",strPartNo);
               cmd.addParameter("SERIAL_NO",strSerialNo);
               cmd.addParameter("CONFIGURATION_ID", sConfigurationId);
               cmd.addParameter("CONDITION_CODE", sConditionCode);

               trans = mgr.validate(trans);

               inventValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORYVALUE");
               fmtdnInvenValue = mgr.formatNumber("INVENTORYVALUE",inventValue);
            }
         }

         if (isSecure[1] !="true")
         {
            inventValue = 0.00;
            fmtdnInvenValue = mgr.formatNumber("INVENTORYVALUE",inventValue);
         }
         /*if (isSecure[1] =="true")
         {
            nInvenValue = trans.getNumberValue("INVENVAL/DATA/INVENTORYVALUE");
         }
         else
            nInvenValue	= 0;*/

         //fmtdnInvenValue = mgr.formatNumber("INVENTORYVALUE",nInvenValue);

         txt = (mgr.isEmpty(name) ? "" :name) + "^"  + (mgr.isEmpty( strSerialNo) ? "" : strSerialNo) + "^"  +  (mgr.isEmpty(strPartNo) ? "" :strPartNo) + "^"  +  (mgr.isEmpty(fmtdnInvenValue) ? "" :fmtdnInvenValue) + "^" + (mgr.isEmpty(sConditionCode) ? "" :sConditionCode) + "^" + 
               (mgr.isEmpty(sPartOwnershipDb) ? "" :sPartOwnershipDb) + "^" ;

         mgr.responseWrite(txt);
      }
      else if ("SITE".equals(val))
      {
         if (checksec("Inventory_Part_Def_Loc_API.Get_Location_No",1))
         {
            cmd = trans.addCustomFunction("LOCATION","Inventory_Part_Def_Loc_API.Get_Location_No","LOCATION_NO");
            cmd.addParameter("SITE",mgr.readValue("SITE",""));
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO",""));
         }

         cmd = trans.addCustomFunction("SITEDES","Site_API.Get_Description","SITEDESCRIPTION");
         cmd.addParameter("SITE",mgr.readValue("SITE",""));

         trans = mgr.validate(trans);

         sitedescription = trans.getValue("SITEDES/DATA/SITEDESCRIPTION");
         if (isSecure[1] =="true")
         {
            location_no= trans.getValue("LOCATION/DATA/LOCATION_NO");
         }
         else
            location_no= ""; 
         txt = (mgr.isEmpty(sitedescription) ? "" : (sitedescription)) + "^" + (mgr.isEmpty(location_no) ? "" : location_no) + "^" ;

         mgr.responseWrite(txt);

      }
      else if ("CONDITION_CODE".equals(val))
      {
         trans.clear();

         cmd = trans.addCustomFunction("CONDCODEDESC","Condition_Code_API.Get_Description","CONDDESC");
         cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
         
         trans = mgr.validate(trans);
         String strCondCodeDesc = trans.getValue("CONDCODEDESC/DATA/CONDDESC");

         txt = (mgr.isEmpty(strCondCodeDesc) ? "" :strCondCodeDesc) + "^";

         mgr.responseWrite(txt);
      }
      else if ("COST_DETAIL_INFO".equals(val))
      {
         trans.clear();

         if ("CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
            txt = "";
         else
         {
            cmd = trans.addCustomFunction("CHECK_INV_PART","Inventory_Part_API.Part_Exist","COST_DETAIL_INFO");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

            cmd = trans.addCustomFunction("GET_ZERO_COST","Inventory_Part_API.Get_Zero_Cost_Flag","DUMMY");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

            cmd = trans.addCustomFunction("ENCODE_ZERO_COST","Inventory_Part_Zero_Cost_API.Encode","COST_DETAIL_INFO");
            cmd.addReference("DUMMY","GET_ZERO_COST/DATA");

            cmd = trans.addCustomFunction("CHECK_STD_COST","Inventory_Part_Unit_Cost_API.Standard_Cost_Exist","COST_DETAIL_INFO");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
            cmd.addParameter("CONFIGURATION_ID","*");
            cmd.addParameter("DUMMY","*");
            cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

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

               addQueryStringField(sb,"CONTRACT",mgr.readValue("CONTRACT"));
               addQueryStringField(sb,"PART_NO",mgr.readValue("PART_NO"));
               addQueryStringField(sb,"SERIAL_NO",mgr.readValue("SERIAL_NO"));
               addQueryStringField(sb,"CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
               addQueryStringField(sb,"CONFIGURATION_ID","*");
               addQueryStringField(sb,"LOT_BATCH_NO","*");
               addQueryStringField(sb,"CALLING_PROCESS","WORK ORDER RECEIPT");

               txt = sb.toString();
            }
         }

         mgr.responseWrite(txt);
      }

      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void ok()
   {
      ASPManager mgr = getASPManager();

      headset.changeRow();

      wo_no  = ctx.readValue("CTX1",wo_no);
      contract = mgr.readValue("CONTRACT","");
      mch_code = mgr.readValue("MCH_CODE","");
      space = " ";

      //Bug 45565 Changed Condituion
      if (!((mgr.isEmpty(mgr.readValue("MCH_CODE"))) || (mgr.isEmpty(mgr.readValue("SITE"))) || (mgr.isEmpty(mgr.readValue("LOCATION_NO"))) || (mgr.isEmpty(mgr.readValue("PART_NO"))) || (mgr.isEmpty(mgr.readValue("SERIAL_NO"))) ))
      {
         cmd = trans.addCustomCommand("MOVESTI", "Active_Separate_API.Move_Serial_To_Inventory");
         cmd.addParameter("WO_NO",wo_no);
         cmd.addParameter("SITE",mgr.readValue("SITE",""));
         cmd.addParameter("PART_NO",mgr.readValue("PART_NO",""));
         cmd.addParameter("LOCATION_NO",mgr.readValue("LOCATION_NO",""));
         cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO",""));
         cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE",""));
         cmd.addParameter("CONTRACT",contract);
         cmd.addParameter("MCH_CODE",mch_code);
         cmd.addParameter("COST_DETAIL_INFO",mgr.readValue("COST_DETAIL_INFO",""));
         cmd.addParameter("NOTE",mgr.readValue("NOTE",""));

         trans = mgr.perform(trans);
         movedOk = "TRUE";

         trans.clear();

         swono  = ctx.readValue("CTX1",swono);
         err_descr= ctx.readValue("CTX2",err_descr);
         contract = ctx.readValue("CTX3",contract);
         mch_code = ctx.readValue("CTX4",mch_code);
      }
   }

   public void cancel()
   {
      cancelOk = "TRUE";
   }

   public void preDefine()throws FndException
   {
      ASPManager mgr = getASPManager();

      mgr.beginASPEvent();

      //----------------- block used for setcheckbox() funtion ----------------

      tempblk = mgr.newASPBlock("TEMP");
      f = tempblk.addField("INDEX");
      f.setHidden();

      //-------------------------------------------------------------------------
      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("WO_NO");
      f.setHidden();

      f = headblk.addField("ERR_DESCR");
      f.setHidden();

      f = headblk.addField("CONTRACT");
      f.setSize(9);
      f.setLabel("PCMWACTIVESEPARATEDLGCONTRACT: Site");
      f.setUpperCase();
      f.setReadOnly();

      f = headblk.addField("MCH_CODE");
      f.setSize(25);
      f.setMaxLength(100);
      f.setDynamicLOV("EQUIPMENT_SERIAL","CONTRACT",600,445);
      f.setLOVProperty("WHERE","INVENTORY_PART_API.Part_Exist(CONTRACT,PART_NO) = 1 AND PART_SERIAL_CATALOG_API.Get_Objstate(PART_NO,SERIAL_NO) !='InInventory' AND PART_SERIAL_CATALOG_API.Get_Objstate(PART_NO,SERIAL_NO) !='Issued'");
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATEDLGOBJECTID1: List of Object ID"));
      f.setLabel("PCMWACTIVESEPARATEDLGMCH_CODE: Object ID");
      f.setUpperCase();
      f.setCustomValidation("CONTRACT,MCH_CODE,PART_OWNERSHIP,CONFIGURATION_ID,CONDITION_CODE","MCHNAME,SERIAL_NO,PART_NO,INVENTORYVALUE,CONDITION_CODE,PART_OWNERSHIP_DB");

      f = headblk.addField("MCHNAME");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPARATEDLGMCHNAME: Object Description");
      f.setFunction("''");   
      f.setReadOnly();

      f = headblk.addField("PART_NO");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPARATEDLGPART_NO: Part No");
      f.setUpperCase();
      f.setFunction("''");   
      f.setReadOnly();

      f = headblk.addField("SERIAL_NO");
      f.setSize(9);
      f.setMaxLength(50);
      f.setLabel("PCMWACTIVESEPARATEDLGSERIAL_NO: Serial No");
      f.setUpperCase();
      f.setFunction("''");   
      f.setReadOnly();

      f = headblk.addField("CONDITION_CODE");
      f.setLabel("PCMWACTIVESEPARATEDLGCONDITIONCODE: Condition Code");
      f.setDynamicLOV("CONDITION_CODE",600,445);
      f.setCustomValidation("CONDITION_CODE","CONDDESC");
      f.setSize(15);
      f.setMaxLength(10);
      f.setUpperCase();
      

      f = headblk.addField("CONDDESC");
      f.setLabel("PCMWACTIVESEPARATEDLGCONDDESC: Condition Code Description");
      f.setFunction("''");
      f.setSize(31);
      f.setReadOnly();

      f = headblk.addField("SITE");
      f.setSize(10);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLabel("PCMWACTIVESEPARATEDLGSITE: Site");
      f.setUpperCase();
      f.setCustomValidation("PART_NO,SITE","SITEDESCRIPTION,LOCATION_NO");
      f.setFunction("''");   

      f = headblk.addField("SITEDESCRIPTION");
      f.setSize(34);
      f.setFunction("''");   
      f.setReadOnly();

      f = headblk.addField("LOCATION_NO");
      f.setSize(40);
      f.setUpperCase();
      f.setDynamicLOV("INVENTORY_LOCATION13","SITE CONTRACT",600,445);
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATEDLGLOCAT: List of Location"));
      f.setLabel("PCMWACTIVESEPARATEDLGLOCATION_NO: Location");
      f.setFunction("''");   

      f = headblk.addField("INVENTORYVALUE", "Number","0");
      f.setSize(15);
      f.setLabel("PCMWACTIVESEPARATEDLGINVENTORYVALUE: Inventory Value");
      f.setFunction("''");   

      f = headblk.addField("CBACCOUNTED");
      f.setLabel("PCMWACTIVESEPARATEDLGCBACCOUNTED: Accounted");
      f.setCheckBox("FALSE,TRUE");
      f.setFunction("''");   
      f.setReadOnly();

      f = headblk.addField("NOTE");
      f.setSize(45);
      f.setLabel("PCMWACTIVESEPARATEDLGNOTE: Note");

      f = headblk.addField("INSUARANCEPART");
      f.setHidden();
      f.setFunction("''");   

      f = headblk.addField("CLIENTVAL");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("PART_OWNERSHIP");
      f.setSize(20);
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("PART_OWNERSHIP_DB");
      f.setSize(20);
      f.setHidden();   
      f.setFunction("''");

      f = headblk.addField("CONFIGURATION_ID");
      f.setSize(50);
      f.setHidden();
      f.setFunction("''");

      //Bug 46565, start
      f = headblk.addField("CONNECTION_TYPE");
      f.setSize(50);
      f.setHidden();
      f.setFunction("''");

      headblk.addField("COST_DETAIL_INFO").
      setFunction("NULL").
      setCustomValidation("CONTRACT,PART_NO,CONFIGURATION_ID,SERIAL_NO,CONDITION_CODE,PART_OWNERSHIP_DB","COST_DETAIL_INFO");

      headblk.addField("DUMMY").
      setHidden().
      setFunction("NULL");

      headblk.setView("ACTIVE_SEPARATE");
      headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.SAVERETURN,"ok","checkBeforeSave");
      headbar.defineCommand(headbar.CANCELEDIT,"cancel");
      headbar.enableCommand(headbar.SAVERETURN);
      headbar.enableCommand(headbar.CANCELEDIT);

      headtbl = mgr.newASPTable(headblk);

      headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATEDLGSERIALTOINVONWO: Move Serial To Inventory on Work Order"));
      headlay = headblk.getASPBlockLayout();
      headlay.setFieldOrder("MCH_CODE,MCHNAME,CONTRACT,PART_NO,SERIAL_NO");
      headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
      headlay.setEditable();
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEDLGGRPLABEL1: General"),"MCH_CODE,MCHNAME,CONTRACT,PART_NO,SERIAL_NO,CONDITION_CODE,CONDDESC",true,true);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEDLGGRPLABEL2: Inventory Location"),"SITE,SITEDESCRIPTION,LOCATION_NO,INVENTORYVALUE,CBACCOUNTED,NOTE",true,true);
      headlay.setSimple("SITEDESCRIPTION");
      headlay.setSimple("CONDDESC");

      //Ganerate the java scripts for the Define Cost Structure dialog
      //Since the inventory is a dynnamic for PCM we need to use reflects
      if (mgr.isPresentationObjectInstalled("invenw/DefineCostStructure.page"))
      {
         try
         {
            Class fourStrings[] = {String.class,String.class,String.class,String.class};

         Class defineCostDetails = Class.forName("ifs.invenw.DefineCostStructure");

            Method m = defineCostDetails.getMethod("getPopupWindowScript",fourStrings);

            String script = (String)m.invoke(null,
                                             new Object[]{
                                                "defineCostStructure",
                                                "COST_DETAIL_INFO",
                                                "validateCostDetailInfo",
                                                "defineCostStructureCallback"
                                             });
            appendJavaScript(script);

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
            throw new RuntimeException(mgr.translate("PCMWACTIVESEPARATEGENCOSTDIALOGCODEERROR: An error occured while generating Javascript code for Define Cost Structure dialog"),e);
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

      if (!mgr.isEmpty(wo_no))
      {
         title = mgr.translate("PCMWACTIVESEPARATEDLGMSTIWO: Move Serial To Inventory on Work Order ");
         title = title + wo_no + " - " + err_descr;
      }
      else
         title  = mgr.translate("PCMWACTIVESEPARATEDLGSERIALTOINV: Move Serial To Inventory");

      mgr.getASPField("COST_DETAIL_INFO").setHidden();
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "PCMWACTIVESEPARATEDLGSERIALTOINV: Move Serial To Inventory";
   }

   protected String getTitle()
   {
      return title;
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      if (headlay.isVisible())
      {
         appendToHTML(headbar.showBar());
         appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
      }

      appendDirtyJavaScript("function validateInventoryvalue(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkInventoryvalue(i) ) return;\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED' && f.INVENTORYVALUE != 0) \n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEDLGINVVAL: Inventory Value must be 0 for Customer Owned Parts."));
      appendDirtyJavaScript("             ');\n");
      appendDirtyJavaScript("         f.INVENTORYVALUE.value = 0; \n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

      appendDirtyJavaScript("function checkAllFields(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	return checkHeadFields(i);\n");
      appendDirtyJavaScript("}\n");

      
      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(movedOk)); // Bug Id 68773
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("	SerialNo = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.readValue("MCH_CODE","")));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	newContract = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(contract));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	invlocation = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.readValue("LOCATION_NO","")));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	newspace = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(space));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("			alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPERATEDLGERRMSG1: The serial "));
      appendDirtyJavaScript("'+SerialNo+'");
      appendDirtyJavaScript("'+newspace+'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPERATEDLGERRMSG2:  is now moved to inventory location "));
      appendDirtyJavaScript("'+newContract+'");
      appendDirtyJavaScript("'+newspace+'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPERATEDLGERRMSG3:  - "));
      appendDirtyJavaScript("'+invlocation+'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPERATEDLGERRMSG4: ."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("	self.close();\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelOk)); // Bug Id 68773
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("window.close();");
      appendDirtyJavaScript("}\n");
   }
}
