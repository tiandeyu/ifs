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
*  File        : InventoryPartInStockDlg.java
*  Created     : CHCRLK  030626
*  Modified    : 
*  CHCRLK  030626  Added column QTY_ONHAND to itemblk0.  
*  CHAMLK  030826  Added Owner Name to the dialog. Added confirmation messages 
*                  when returning parts to inventory when owner is not equal 
*                  to the work order customer.
*  ARWILK  031218  Edge Developments - (Removed clone and doReset Methods)
* --------------------------------- Edge - SP1 Merge---------------------------
*  CHAMLK  040130  Bug 42684, Modified function okFindITEM0() to prevent the display of all serials of the part in inventory.
*                  Modified function countFindITEM0() as well. Created new function newRowITEM0() to allow the user
*                  to return the serial to a new location. Modified function ok() for the same purpose. Added a lov to the 
*                  location no field and made it editable in new mode only. Added custom validation for location no field to
*                  fetch warehouse, bay, row, tier and bin.
*  CHAMLK  040323  Merge with SP1.
*  ARWILK  041111  Replaced getContents with printContents.
*  SHAFLK  050310  Bug 50032, Modified function  ok().
*  NAMELK  050322  Merged Bug 50032.
*  SHAFLK  050321  Bug 49819, Modified function  ok() and newRowITEM0().
*  NIJALK  050411  Merged bug 49819.
*  DiAmlk  050411  Bug ID:123329 - Modified the method ok().
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830.
*  RaKalk  051123  Aded Cost details functionality. Added field COST_DETAIL_INFO and DUMMY
*          051123  Modified methods validate,preDefine,okFileITEM.
*  DIAMLK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060906  Merged Bug Id: 58216.
*  AMNILK  143993  Call Id: 143993. Modified method ok().
*  AMNILK  070717  Eliminated XSS Security Vulnerability.
*  CHANLK  090907  Bug 85710, Modified printContents().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.lang.reflect.Method;

public class InventoryPartInStockDlg extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.InventoryPartInStockDlg");

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

   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;
   private ASPBlockLayout itemlay0;

   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private boolean okClose;   
   private boolean refreshMain;
   private String frmname;
   private String qrystr;
   private ASPBuffer buff;
   private ASPBuffer row;
   private ASPQuery q;
   private ASPBuffer temp;
   private ASPCommand cmd;
   private ASPBuffer data;
   private String sWoNo;  
   private String sLineNo; 
   private String sPartNo;     
   private String sPartDesc;
   private String sContract;
   private String sSerialNo;
   private String sOwnership;
   private String sOwner;
   private String sOwnerName;
   private String sWoCust;
   private String sLotBatchNo;
   private String sCondCode;
   private String sCondCodeDesc;
   private String sConfigId;
   private String sQtyLeft;
   private String sInvenVal;
   private String sQtyRet;

   //===============================================================
   // Construction 
   //===============================================================
   public InventoryPartInStockDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      mgr.setPageExpiring();
      fmt = mgr.newASPHTMLFormatter();   
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      okClose = ctx.readFlag("OKCLOSE",false);
      refreshMain = ctx.readFlag("REFRESHMAIN",false);      
      sWoNo = ctx.readValue("CTXWONO",sWoNo);
      sLineNo = ctx.readValue("CTXLINE",sLineNo);
      frmname= ctx.readValue("FRMNAME","");
      qrystr = ctx.readValue("QRYSTR","");
      sPartNo = ctx.readValue("CTXPART",sPartNo);
      sPartDesc = ctx.readValue("CTXPARTDESC",sPartDesc);
      sContract = ctx.readValue("CTXCON",sContract);
      sSerialNo = ctx.readValue("SERNO",sSerialNo);
      sOwnership = ctx.readValue("OWNSHIP",sOwnership);
      sOwner = ctx.readValue("OWN",sOwner);
      sOwnerName = ctx.readValue("OWNNANE",sOwnerName);
      sWoCust = ctx.readValue("WOCUST",sWoCust);
      sLotBatchNo = ctx.readValue("LBNO",sLotBatchNo);
      sConfigId = ctx.readValue("CONFID",sConfigId);
      sCondCode = ctx.readValue("CONDCODE",sCondCode);
      sCondCodeDesc = ctx.readValue("CONDCODEDESC",sCondCodeDesc);
      sQtyLeft = ctx.readValue("QTYLEFT",sQtyLeft);
      sInvenVal = ctx.readValue("INVVAL",sInvenVal);
      sQtyRet = ctx.readValue("QTYRET",sQtyRet);

      if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
         sWoNo = mgr.readValue("WO_NO");
         sLineNo = mgr.readValue("LINE_NO");
         sContract = mgr.readValue("CONTRACT");
         sPartNo = mgr.readValue("PART_NO");
         sPartDesc = mgr.readValue("PARTDESCRIPTION");
         sSerialNo = mgr.readValue("SERIAL_NO");
         sLotBatchNo = mgr.readValue("LOT_BATCH_NO");
         sConfigId = mgr.readValue("CONFIGURATION_ID");
         sOwnership = mgr.readValue("PART_OWNERSHIP");
         sOwner = mgr.readValue("OWNER");
         sOwnerName = mgr.readValue("OWNER_NAME");
         sWoCust = mgr.readValue("WO_CUST");
         sCondCode = mgr.readValue("CONDITION_CODE");
         sCondCodeDesc = mgr.readValue("CONDITIONCODEDESC");
         sInvenVal = mgr.readValue("INVENTORY_VALUE");
         sQtyLeft = mgr.readValue("QTY_LEFT");
         sQtyRet = mgr.readValue("QTY_RETURNED");
         qrystr = mgr.readValue("QRYSTR");     
         frmname = mgr.readValue("FRMNAME");

         okFind();
      }
      else if (mgr.buttonPressed("BACK"))
         back();

      adjust();

      ctx.writeFlag("OKCLOSE",okClose);
      ctx.writeFlag("REFRESHMAIN",refreshMain);
      ctx.writeValue("CTXWONO",sWoNo);
      ctx.writeValue("CTXLINE",sLineNo);
      ctx.writeValue("FRMNAME",frmname);
      ctx.writeValue("QRYSTR",qrystr);
      ctx.writeValue("CTXPART",sPartNo);
      ctx.writeValue("CTXPARTDESC",sPartDesc);
      ctx.writeValue("CTXCON",sContract);
      ctx.writeValue("SERNO",sSerialNo);
      ctx.writeValue("OWNSHIP",sOwnership);
      ctx.writeValue("OWN",sOwner);
      ctx.writeValue("OWNNAME",sOwnerName);
      ctx.writeValue("WOCUST",sWoCust);
      ctx.writeValue("LBNO",sLotBatchNo);
      ctx.writeValue("CONFID",sConfigId);
      ctx.writeValue("CONDCODE",sCondCode);
      ctx.writeValue("CONDCODEDESC",sCondCodeDesc);
      ctx.writeValue("QTYLEFT",sQtyLeft);
      ctx.writeValue("INVVAL",sInvenVal);
      ctx.writeValue("QTYRET",sQtyRet);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addEmptyQuery(headblk);
      q.includeMeta("ALL");
      mgr.submit(trans);

      temp = headset.getRow();

      temp.setValue("WO_NO",sWoNo);
      temp.setValue("LINE_NO",sLineNo);
      temp.setValue("HEAD_CONTRACT",sContract);
      temp.setValue("HEAD_PART_NO",sPartNo);
      temp.setValue("DESCRIPTION",sPartDesc);
      temp.setValue("HEAD_SERIAL_NO",sSerialNo);
      temp.setValue("HEAD_LOT_BATCH_NO",sLotBatchNo);
      temp.setValue("HEAD_CONFIGURATION_ID",sConfigId);
      temp.setValue("PART_OWNERSHIP",sOwnership);
      temp.setValue("OWNER",sOwner);
      temp.setValue("OWNING_CUSTOMER_NO",sOwner);
      temp.setValue("OWNER_NAME",sOwnerName);
      temp.setValue("WO_CUST",sWoCust);
      temp.setValue("HEAD_CONDITION_CODE",sCondCode);
      temp.setValue("HEAD_CONDITION_CODE_DESC",sCondCodeDesc);
      temp.setValue("INVENTORY_VALUE",sInvenVal);
      temp.setValue("QTY_LEFT",sQtyLeft);
      temp.setValue("QTY_RETURNED",sQtyRet);

      headset.setRow(temp);

      okFindITEM0();
   }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addEmptyQuery(itemblk0);
      

      /*if (mgr.isEmpty(sCondCode) || "null".equals(sCondCode))
          q.addWhereCondition("PART_NO='"+sPartNo+"' AND CONTRACT='"+sContract+"'");
      else
          q.addWhereCondition("PART_NO='"+sPartNo+"' AND CONTRACT='"+sContract+"' AND Condition_Code_Manager_API.Get_Condition_Code( PART_NO, SERIAL_NO, LOT_BATCH_NO) = '"+sCondCode+"'");
              */

      if (!mgr.isEmpty(sSerialNo) && (!"null".equals(sSerialNo)))
      {
         //Bug 58216 start
         q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND SERIAL_NO= ?");
         q.addParameter("PART_NO",sPartNo);
         q.addParameter("CONTRACT",sContract);
         q.addParameter("SERIAL_NO",sSerialNo);
         //Bug 58216 end
         if (!mgr.isEmpty(sOwnership) && (!"null".equals(sOwnership)))
         {
            //Bug 58216 start
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND SERIAL_NO= ? AND PART_OWNERSHIP= ?");
            q.addParameter("PART_NO",sPartNo);
            q.addParameter("CONTRACT",sContract);
            q.addParameter("SERIAL_NO",sSerialNo);
            q.addParameter("PART_OWNERSHIP",sOwnership);
            //Bug 58216 end
            
            if (!mgr.isEmpty(sOwner) && (!"null".equals(sOwner)))
            {
               //Bug 58216 start
               q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND SERIAL_NO= ? AND PART_OWNERSHIP= ? AND OWNING_CUSTOMER_NO= ?");
               q.addParameter("PART_NO",sPartNo);
               q.addParameter("CONTRACT",sContract);
               q.addParameter("SERIAL_NO",sSerialNo);
               q.addParameter("PART_OWNERSHIP",sOwnership);
               q.addParameter("OWNING_CUSTOMER_NO",sOwner);
               //Bug 58216 end
            }
               
         }
      }

      else if (!mgr.isEmpty(sLotBatchNo) && (!"null".equals(sLotBatchNo)))
      {
         //Bug 58216 start
         q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND LOT_BATCH_NO= ?");
         q.addParameter("PART_NO",sPartNo);
         q.addParameter("CONTRACT",sContract);
         q.addParameter("LOT_BATCH_NO",sLotBatchNo);
         //Bug 58216 end
         
         if (!mgr.isEmpty(sOwnership) && (!"null".equals(sOwnership)))
         {
            //Bug 58216 start
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND LOT_BATCH_NO= ? AND PART_OWNERSHIP= ?");
            q.addParameter("PART_NO",sPartNo);
            q.addParameter("CONTRACT",sContract);
            q.addParameter("LOT_BATCH_NO",sLotBatchNo);
            q.addParameter("PART_OWNERSHIP",sOwnership);
            //Bug 58216 end
            
            if (!mgr.isEmpty(sOwner) && (!"null".equals(sOwner)))
            {
               //Bug 58216 start
               q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND LOT_BATCH_NO= ? AND PART_OWNERSHIP= ? AND OWNING_CUSTOMER_NO= ?");
               q.addParameter("PART_NO",sPartNo);
               q.addParameter("CONTRACT",sContract);
               q.addParameter("LOT_BATCH_NO",sLotBatchNo);
               q.addParameter("PART_OWNERSHIP",sOwnership);
               q.addParameter("OWNING_CUSTOMER_NO",sOwner);
               //Bug 58216 end
            }
               
         }
      }
      else
      {
         //Bug 58216 start
         q.addWhereCondition("PART_NO= ? AND CONTRACT= ?");
         q.addParameter("PART_NO",sPartNo);
         q.addParameter("CONTRACT",sContract);
         //Bug 58216 end

         if (!mgr.isEmpty(sOwnership) && (!"null".equals(sOwnership)))
         {
            //Bug 58216 start
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND PART_OWNERSHIP= ?");
            q.addParameter("PART_NO",sPartNo);
            q.addParameter("CONTRACT",sContract);
            q.addParameter("PART_OWNERSHIP",sOwnership);
            //Bug 58216 end

            if (!mgr.isEmpty(sOwner) && (!"null".equals(sOwner)))
            {
               //Bug 58216 start
               q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND PART_OWNERSHIP= ? AND OWNING_CUSTOMER_NO= ?");
               q.addParameter("PART_NO",sPartNo);
               q.addParameter("CONTRACT",sContract);
               q.addParameter("PART_OWNERSHIP",sOwnership);
               q.addParameter("OWNING_CUSTOMER_NO",sOwner);
               //Bug 58216 end
            }
               
         }
      }

      q.setSelectExpression("HEAD_PART_OWNERSHIP","?");
      q.addParameter("HEAD_PART_OWNERSHIP",headset.getValue("PART_OWNERSHIP"));

      q.includeMeta("ALL");
      q.setOrderByClause("SERIAL_NO");
      mgr.submit(trans);

      trans.clear();
   }

   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      /*if (mgr.isEmpty(sCondCode) || "null".equals(sCondCode))
          q.addWhereCondition("PART_NO='"+sPartNo+"' AND CONTRACT='"+sContract+"'");
      else
          q.addWhereCondition("PART_NO='"+sPartNo+"' AND CONTRACT='"+sContract+"' AND Condition_Code_Manager_API.Get_Condition_Code( PART_NO, SERIAL_NO, LOT_BATCH_NO) = '"+sCondCode+"'");
              */

      if (!mgr.isEmpty(sSerialNo) && (!"null".equals(sSerialNo)))
      {
         //Bug 58216 start
         q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND SERIAL_NO= ?");
         q.addParameter("PART_NO",sPartNo);
         q.addParameter("CONTRACT",sContract);
         q.addParameter("SERIAL_NO",sSerialNo);
         //Bug 58216 end

         if (!mgr.isEmpty(sOwnership) && (!"null".equals(sOwnership)))
         {
            //Bug 58216 start
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND SERIAL_NO= ? AND PART_OWNERSHIP= ?");
            q.addParameter("PART_NO",sPartNo);
            q.addParameter("CONTRACT",sContract);
            q.addParameter("SERIAL_NO",sSerialNo);
            q.addParameter("PART_OWNERSHIP",sOwnership);
            //Bug 58216 end

            if (!mgr.isEmpty(sOwner) && (!"null".equals(sOwner)))
            {
               //Bug 58216 start
               q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND SERIAL_NO= ? AND PART_OWNERSHIP= ? AND OWNING_CUSTOMER_NO= ?");
               q.addParameter("PART_NO",sPartNo);
               q.addParameter("CONTRACT",sContract);
               q.addParameter("SERIAL_NO",sSerialNo);
               q.addParameter("PART_OWNERSHIP",sOwnership);
               q.addParameter("OWNING_CUSTOMER_NO",sOwner);
               //Bug 58216 end
            }
               
         }
      }

      else if (!mgr.isEmpty(sLotBatchNo) && (!"null".equals(sLotBatchNo)))
      {
         //Bug 58216 start
         q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND LOT_BATCH_NO= ?");
         q.addParameter("PART_NO",sPartNo);
         q.addParameter("CONTRACT",sContract);
         q.addParameter("LOT_BATCH_NO",sLotBatchNo);
         //Bug 58216 end

         if (!mgr.isEmpty(sOwnership) && (!"null".equals(sOwnership)))
         {
            //Bug 58216 start
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND LOT_BATCH_NO= ? AND PART_OWNERSHIP= ?");
            q.addParameter("PART_NO",sPartNo);
            q.addParameter("CONTRACT",sContract);
            q.addParameter("LOT_BATCH_NO",sLotBatchNo);
            q.addParameter("PART_OWNERSHIP",sOwnership);
            //Bug 58216 end

            if (!mgr.isEmpty(sOwner) && (!"null".equals(sOwner)))
            {
               //Bug 58216 start
               q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND LOT_BATCH_NO= ? AND PART_OWNERSHIP= ? AND OWNING_CUSTOMER_NO= ?");
               q.addParameter("PART_NO",sPartNo);
               q.addParameter("CONTRACT",sContract);
               q.addParameter("LOT_BATCH_NO",sLotBatchNo);
               q.addParameter("PART_OWNERSHIP",sOwnership);
               q.addParameter("OWNING_CUSTOMER_NO",sOwner);
               //Bug 58216 end
            }
               
         }
      }
      else
      {
         //Bug 58216 Start
         q.addWhereCondition("PART_NO= ? AND CONTRACT= ?");
         q.addParameter("PART_NO",sPartNo);
         q.addParameter("CONTRACT",sContract);
         //Bug 58216 end

         if (!mgr.isEmpty(sOwnership) && (!"null".equals(sOwnership)))
         {
            //Bug 58216 start
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND PART_OWNERSHIP= ?");
            q.addParameter("PART_NO",sPartNo);
            q.addParameter("CONTRACT",sContract);
            q.addParameter("PART_OWNERSHIP",sOwnership);
            //Bug 58216 end

            if (!mgr.isEmpty(sOwner) && (!"null".equals(sOwner)))
            {
               //Bug 58216 start
               q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND PART_OWNERSHIP= ? AND OWNING_CUSTOMER_NO= ?");
               q.addParameter("PART_NO",sPartNo);
               q.addParameter("CONTRACT",sContract);
               q.addParameter("PART_OWNERSHIP",sOwnership);
               q.addParameter("OWNING_CUSTOMER_NO",sOwner);
               //Bug 58216 end
            }
         }
      }

      q.includeMeta("ALL");

      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      String n = itemset0.getRow().getValue("N");
      itemset0.clear();
      itemlay0.setCountValue(toInt(n));
   }

   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
      headlay.setEditable();

      String sDefVal = "*";
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","INVENTORY_PART_IN_STOCK_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("PART_NO",sPartNo);
      data.setFieldItem("CONTRACT",sContract);
      data.setFieldItem("CONFIGURATION_ID",sConfigId);
      if (!mgr.isEmpty(sLotBatchNo) && (!"null".equals(sLotBatchNo)))
      {
         data.setFieldItem("LOT_BATCH_NO",sLotBatchNo);
      }
      else
         data.setFieldItem("LOT_BATCH_NO",sDefVal);
      if (!mgr.isEmpty(sSerialNo) && (!"null".equals(sSerialNo)))
         data.setFieldItem("SERIAL_NO",sSerialNo);
      else
         data.setFieldItem("SERIAL_NO",sDefVal);
      if (isModuleInst1("INVENT"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("ENG_CHG","INVENTORY_PART_REVISION_API.Get_Latest_Eng_Chg_Level","ITEM0_ENG_CHG");
         cmd.addParameter("CONTRACT",sContract);
         cmd.addParameter("PART_NO",sPartNo);
         trans = mgr.perform(trans);
         String sEngChg = trans.getValue("ENG_CHG/DATA/ITEM0_ENG_CHG");
         data.setFieldItem("ENG_CHG_LEVEL",sEngChg);
      }
      data.setFieldItem("WAIV_DEV_REJ_NO",sDefVal);
      itemset0.addRow(data);
   }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

   public void  ok()
   {
      String sOldSerialNo = "";
      String sOldConditionCode = "";
      String sOldConfigId = "";
      String sOldPartOwnership = "";
      String sOldOwner = "";
      String sOldLotBatchNo = "";
      String sChanged = "false";

      ASPManager mgr = getASPManager();

      ASPBuffer buff = itemset0.getRow();
      itemset0.changeRow();

      double nRetQty = mgr.readNumberValue("RETURN_QTY");   //from itemset0
      if (isNaN(nRetQty))
         nRetQty = 0;

      double nQtyToRet = headset.getNumberValue("QTY_LEFT");  //from headset


      trans.clear();
      cmd = trans.addCustomFunction("GETSER","Work_Order_Returns_API.Get_Serial_No","HEAD_SERIAL_NO");
      cmd.addParameter("WO_NO",sWoNo);
      cmd.addParameter("LINE_NO",sLineNo);
      cmd = trans.addCustomFunction("GETCON","Work_Order_Returns_API.Get_Condition_Code","CONDITION_CODE");
      cmd.addParameter("WO_NO",sWoNo);
      cmd.addParameter("LINE_NO",sLineNo);
      cmd = trans.addCustomFunction("GETCONFIG","Work_Order_Returns_API.Get_Configuration_Id","CONFIGURATION_ID");
      cmd.addParameter("WO_NO",sWoNo);
      cmd.addParameter("LINE_NO",sLineNo);
      cmd = trans.addCustomFunction("GETPARTOWN","Work_Order_Returns_API.Get_Part_Ownership","PART_OWNERSHIP");
      cmd.addParameter("WO_NO",sWoNo);
      cmd.addParameter("LINE_NO",sLineNo);
      cmd = trans.addCustomFunction("GETOWNER","Work_Order_Returns_API.Get_Owner","OWNER");
      cmd.addParameter("WO_NO",sWoNo);
      cmd.addParameter("LINE_NO",sLineNo); 
      cmd = trans.addCustomFunction("GETLOT","Work_Order_Returns_API.Get_Lot_Batch_No","LOT_BATCH_NO");
      cmd.addParameter("WO_NO",sWoNo);
      cmd.addParameter("LINE_NO",sLineNo);

      trans = mgr.perform(trans);         
      sOldSerialNo = trans.getValue("GETSER/DATA/HEAD_SERIAL_NO");
      if ("null".equals(sOldSerialNo))
         sOldSerialNo = "";
      if ((!mgr.isEmpty(sOldSerialNo)) && (!sOldSerialNo.equals(sSerialNo)))
         sChanged = "true";
      sOldConditionCode = trans.getValue("GETCON/DATA/CONDITION_CODE");
      if ("null".equals(sOldConditionCode))
         sOldConditionCode = "";
      if ((!mgr.isEmpty(sOldConditionCode)) && (!sOldConditionCode.equals(sCondCode)))
         sChanged = "true";
      sOldConfigId = trans.getValue("GETCONFIG/DATA/CONFIGURATION_ID");
      if ("null".equals(sOldConfigId))
         sOldConfigId = "";
      if ((!mgr.isEmpty(sOldConfigId)) && (!sOldConfigId.equals(sConfigId)))
         sChanged = "true";
      sOldPartOwnership = trans.getValue("GETPARTOWN/DATA/PART_OWNERSHIP");
      if ("null".equals(sOldPartOwnership))
         sOldPartOwnership = "";
      if ((!mgr.isEmpty(sOldPartOwnership)) && (!sOldPartOwnership.equals(sOwnership)))
         sChanged = "true";
      sOldOwner = trans.getValue("GETOWNER/DATA/OWNER");
      if ("null".equals(sOldOwner))
         sOldOwner = "";
      if ((!mgr.isEmpty(sOldOwner))  && (!sOldOwner.equals(sOwner)))
         sChanged = "true";
      sOldLotBatchNo = trans.getValue("GETLOT/DATA/LOT_BATCH_NO");
      if ("null".equals(sOldLotBatchNo))
         sOldLotBatchNo = "";
      if ((!mgr.isEmpty(sOldLotBatchNo)) && (!sOldLotBatchNo.equals(sLotBatchNo)))
         sChanged = "true";

      if ("true".equals(sChanged))
      {
         itemlay0.setLayoutMode(itemlay0.EDIT_LAYOUT);
         mgr.showAlert(mgr.translate("PCMWINVENTORYPARTINSTOCKDLGOBJMODIFIED: The Work Order Returns object has been modified by another user"));
      }
      else if (nQtyToRet<nRetQty)
         mgr.showAlert(mgr.translate("PCMWINVENTORYPARTINSTOCKDLGQTYEXCEED: Return Quantity cannot exceed Quantity Left!"));
      else
      {
         trans.clear();
         String sOwningCust = "";
         String sOwningSupp = "";

         cmd = trans.addCustomFunction("POWNDB","Part_Ownership_API.Encode","PART_OWNERSHIP_DB");
         cmd.addParameter("PART_OWNERSHIP",sOwnership);

         trans = mgr.perform(trans);         
         String sPartOwnDB = trans.getValue("POWNDB/DATA/PART_OWNERSHIP_DB");         
         trans.clear();

         if ("CUSTOMER OWNED".equals(sPartOwnDB))
            sOwningCust = mgr.readValue("OWNER");
         else if ("SUPPLIER LOANED".equals(sPartOwnDB))
            sOwningSupp = mgr.readValue("OWNER");

         trans.clear();

         // Procedure WORK_ORDER_RETURNS_API.Return_To_Inventory 
         cmd = trans.addCustomCommand("RETTOINV","WORK_ORDER_RETURNS_API.Return_To_Inventory");
         cmd.addParameter("WO_NO", sWoNo);
         cmd.addParameter("LINE_NO", sLineNo);
         cmd.addParameter("CONTRACT", sContract);
         cmd.addParameter("PART_NO", sPartNo);
         //Adding parameter SERIAL_NO
         if ("null".equals(sSerialNo))
            cmd.addParameter("HEAD_SERIAL_NO");
         else
            cmd.addParameter("HEAD_SERIAL_NO", sSerialNo);
         //Adding parameter CONFIGURATION_ID
         if ("null".equals(sConfigId))
            cmd.addParameter("CONFIGURATION_ID");
         else
            cmd.addParameter("CONFIGURATION_ID", sConfigId);
         cmd.addParameter("LOCATION_NO", itemset0.getRow().getValue("LOCATION_NO"));
         //Adding parameter LOT_BATCH_NO
         if ("null".equals(sLotBatchNo))
            cmd.addParameter("LOT_BATCH_NO");
         else
            cmd.addParameter("LOT_BATCH_NO", sLotBatchNo);
         //Adding parameter CONDITION_CODE
         if ("null".equals(sCondCode))
            cmd.addParameter("CONDITION_CODE");
         else
            cmd.addParameter("CONDITION_CODE", sCondCode);            
         cmd.addParameter("RETURN_QTY", mgr.readValue("RETURN_QTY"));         
         //Adding parameter COST_DETAIL_ID
         cmd.addParameter("COST_DETAIL_INFO", mgr.readValue("COST_DETAIL_INFO"));

         cmd.addParameter("PART_OWNERSHIP_DB", sPartOwnDB);
         cmd.addParameter("OWNER", sOwningSupp);
         cmd.addParameter("OWNER", sOwningCust);
	 
	 trans = mgr.perform(trans);
         trans.clear();

         refreshMain = true;
      } 
   }

   public boolean isModuleInst1(String module_)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      ASPCommand cmd = mgr.newASPCommand();
      String modVersion;

      cmd = trans1.addCustomFunction("MODVERS","module_api.get_version","MODULENAME");
      cmd.addParameter("MODULENAME",module_);

      trans1 = mgr.perform(trans1);
      modVersion = trans1.getValue("MODVERS/DATA/MODULENAME");

      if (!mgr.isEmpty(modVersion))
         return true;
      else
         return false;
   }

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");
      String txt;

      if ("LOCATION_NO".equals(val))
      {
         if (isModuleInst1("INVENT"))
         {
            trans.clear();
            cmd = trans.addCustomFunction("WAREHOUS","Inventory_Location_API.Get_Warehouse","WAREHOUSE");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("LOCATION_NO",mgr.readValue("LOCATION_NO"));

            cmd = trans.addCustomFunction("BAYNUM","Inventory_Location_API.Get_Bay_No","BAY_NO");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("LOCATION_NO",mgr.readValue("LOCATION_NO"));

            cmd = trans.addCustomFunction("ROWNUM","Inventory_Location_API.Get_Row_No","ROW_NO");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("LOCATION_NO",mgr.readValue("LOCATION_NO"));

            cmd = trans.addCustomFunction("TIERNUM","Inventory_Location_API.Get_Tier_No","TIER_NO");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("LOCATION_NO",mgr.readValue("LOCATION_NO"));

            cmd = trans.addCustomFunction("BINNUM","Inventory_Location_API.Get_Bin_No","BIN_NO");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("LOCATION_NO",mgr.readValue("LOCATION_NO"));

            trans = mgr.perform(trans);
            String sWareHouse = trans.getValue("WAREHOUS/DATA/WAREHOUSE");
            String sBayNum = trans.getValue("BAYNUM/DATA/BAY_NO");
            String sRowNum = trans.getValue("ROWNUM/DATA/ROW_NO");
            String sTierNum = trans.getValue("TIERNUM/DATA/TIER_NO");
            String sBinNum = trans.getValue("BINNUM/DATA/BIN_NO");

            txt = (mgr.isEmpty(sWareHouse) ? "" : (sWareHouse))+ "^" + 
                  (mgr.isEmpty(sBayNum) ? "" : (sBayNum))+ "^" + 
                  (mgr.isEmpty(sRowNum) ? "" : (sRowNum))+ "^" +
                  (mgr.isEmpty(sTierNum) ? "" : (sTierNum))+ "^" + 
                  (mgr.isEmpty(sBinNum) ? "" : (sBinNum))+ "^" ;

            mgr.responseWrite(txt);
         }
      }
      else if ("COST_DETAIL_INFO".equals(val))
      {
         cmd = trans.addCustomFunction("ENCODE_PART_OWNERSHIP","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP_DB");
         cmd.addParameter("HEAD_PART_OWNERSHIP",mgr.readValue("HEAD_PART_OWNERSHIP"));

         cmd = trans.addCustomFunction("ZERO_COST_FLAG","Inventory_Part_API.Get_Zero_Cost_Flag","COST_DETAIL_INFO");
         cmd.addParameter("CONTRACT",   mgr.readValue("CONTRACT"));
         cmd.addParameter("PART_NO",    mgr.readValue("PART_NO"));

         cmd = trans.addCustomFunction("ENCODE_ZERO_COST","Inventory_Part_Zero_Cost_API.Encode","DUMMY");
         cmd.addReference("COST_DETAIL_INFO","ZERO_COST_FLAG/DATA");

         cmd = trans.addCustomFunction("STD_COST_EXIST","Inventory_Part_Unit_Cost_API.Standard_Cost_Exist","COST_DETAIL_INFO");
         cmd.addParameter("CONTRACT",        mgr.readValue("CONTRACT"));
         cmd.addParameter("PART_NO",         mgr.readValue("PART_NO"));
         cmd.addParameter("CONFIGURATION_ID",mgr.readValue("CONFIGURATION_ID"));
         cmd.addParameter("LOT_BATCH_NO",    mgr.readValue("LOT_BATCH_NO"));
         cmd.addParameter("SERIAL_NO",       mgr.readValue("SERIAL_NO"));
         cmd.addParameter("CONDITION_CODE",  mgr.readValue("CONDITION_CODE"));

         trans = mgr.validate(trans);

         String partOwnershipDB = trans.getValue("ENCODE_PART_OWNERSHIP/DATA/PART_OWNERSHIP_DB");
         String zeroCostFlag    = trans.getValue("ENCODE_ZERO_COST/DATA/DUMMY");
         String stdCostExist    = trans.getValue("STD_COST_EXIST/DATA/COST_DETAIL_INFO");

         if ("CUSTOMER OWNED".equals(partOwnershipDB) ||
             "0".equals(zeroCostFlag)                 ||
             !"FALSE".equals(stdCostExist))
         {
            txt = "";
         }
         else
         {
            StringBuffer sb = new StringBuffer();

            addQueryStringField(sb,"CONTRACT",           mgr.readValue("CONTRACT"));
            addQueryStringField(sb,"PART_NO",            mgr.readValue("PART_NO"));
            addQueryStringField(sb,"CONFIGURATION_ID",   mgr.readValue("CONFIGURATION_ID"));
            addQueryStringField(sb,"LOT_BATCH_NO",       mgr.readValue("LOT_BATCH_NO"));
            addQueryStringField(sb,"SERIAL_NO",          mgr.readValue("SERIAL_NO"));
            addQueryStringField(sb,"CONDITION_CODE",     mgr.readValue("CONDITION_CODE"));
            addQueryStringField(sb,"CALLING_PROCESS",    "WORK ORDER RECEIPT");

            txt = sb.toString();
         }

         mgr.responseWrite(txt);
      }
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


   public void  back()
   {
      okClose = true;
   }

   public void  preDefine()throws FndException
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("WO_NO");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("LINE_NO");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("HEAD_PART_NO");
      f.setSize(15);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGHEADPARTNO: Part No");
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("DESCRIPTION");
      f.setSize(25);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGDESCRIPTION: Part Description");
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("HEAD_SERIAL_NO");
      f.setSize(12);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGSERIALNO: Serial No");
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("PART_OWNERSHIP");
      f.setSize(15);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGPARTOWNERSHIP: Ownership");
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("PART_OWNERSHIP_DB");
      f.setHidden();
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("OWNER");
      f.setSize(25);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGOWNER: Owner");
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("OWNING_CUSTOMER_NO");
      f.setSize(25);
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("OWNER_NAME");
      f.setSize(20);
      f.setMaxLength(100);
      f.setReadOnly();
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGOWNERNAME: Owner Name");
      f.setFunction("''");

      f = headblk.addField("WO_CUST");
      f.setSize(20);
      f.setHidden();
      f.setFunction("''");
      //f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:WO_NO)");

      f = headblk.addField("HEAD_LOT_BATCH_NO");
      f.setSize(12);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGLOTBATCHNO: Lot/Batch No");
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("HEAD_CONDITION_CODE");
      f.setSize(15);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGCONDITIONCODE: Condition Code");
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("HEAD_CONDITION_CODE_DESC");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGCONDCODEDESC: Condition Code Description");
      f.setFunction("''");

      f = headblk.addField("HEAD_CONFIGURATION_ID");
      f.setSize(12);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGHEADCONFIGURATIONID: Config Id");
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("QTY_LEFT", "Number");
      f.setSize(15);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGQTYLEFT: Quantity Left");
      f.setReadOnly();
      f.setFunction("''");

      f = headblk.addField("INVENTORY_VALUE", "Number");
      f.setSize(15);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGINVENTORYVALUE: Inventory Value");
      f.setFunction("''");
      f.setReadOnly();

      f = headblk.addField("HEAD_CONTRACT");
      f.setReadOnly();
      f.setHidden();
      f.setFunction("''");  

      f = headblk.addField("QTY_RETURNED", "Number");
      f.setReadOnly();
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("DUMMY");
      f.setHidden();
      f.setFunction("NULL");

      headblk.setView("DUAL");
      headset = headblk.getASPRowSet();

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headlay.setDialogColumns(3);

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("PCMWINVENTORYPARTINSTOCKDLGTITLE: Return Material to Inventory");

      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.BACK,"cancel");
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW); 
      headbar.disableCommand(headbar.DELETE); 
      headbar.disableCommand(headbar.FIND); 
      headbar.disableCommand(headbar.BACK);


      itemblk0 = mgr.newASPBlock("ITEM0");

      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk0.addField("PART_NO");
      f.setReadOnly();      
      f.setHidden();

      f = itemblk0.addField("CONFIGURATION_ID");
      f.setReadOnly();
      f.setHidden();

      f = itemblk0.addField("LOT_BATCH_NO");
      f.setReadOnly();
      f.setHidden();
      f.setUpperCase();

      f = itemblk0.addField("SERIAL_NO");
      f.setReadOnly();
      f.setHidden();
      f.setUpperCase();

      f = itemblk0.addField("WAIV_DEV_REJ_NO");
      //f.setReadOnly();
      f.setHidden();

      f = itemblk0.addField("ENG_CHG_LEVEL");
      //f.setReadOnly();
      f.setHidden();

      f = itemblk0.addField("CONDITION_CODE");
      f.setFunction("Condition_Code_Manager_API.Get_Condition_Code(:PART_NO,:SERIAL_NO,:LOT_BATCH_NO)");
      f.setHidden();

      f = itemblk0.addField("CODE_DESC");
      f.setFunction("CONDITION_CODE_API.GET_DESCRIPTION(Condition_Code_Manager_API.Get_Condition_Code(:PART_NO,:SERIAL_NO,:LOT_BATCH_NO))");
      f.setHidden();

      f = itemblk0.addField("RETURN_QTY", "Number");
      f.setSize(15);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGRETURNQTY: Return Qty");
      f.setFunction("''");

      f = itemblk0.addField("CONTRACT");
      f.setSize(11);
      f.setMandatory();
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGCONTRACT: Site");
      f.setUpperCase();
      f.setReadOnly();

      f = itemblk0.addField("LOCATION_NO");
      f.setSize(11);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGLOCATIONNO: Location");
      //f.setReadOnly();
      f.setUpperCase();
      f.setDynamicLOV("INVENTORY_LOCATION5","CONTRACT",600,445);
      f.setCustomValidation("LOCATION_NO,CONTRACT","WAREHOUSE,BAY_NO,ROW_NO,TIER_NO,BIN_NO");

      f = itemblk0.addField("QTY_ONHAND","Number");
      f.setSize(14);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGQTYONHAND: Qty on Hand");
      f.setReadOnly();

      f = itemblk0.addField("WAREHOUSE");
      f.setSize(13);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGLOCWAREHOUSE: Warehouse");
      //f.setFunction("Inventory_Location_API.Get_Warehouse(:CONTRACT,:LOCATION_NO)");
      f.setReadOnly();

      f = itemblk0.addField("BAY_NO");
      f.setSize(6);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGLOCBAY: Bay");
      //f.setFunction("Inventory_Location_API.Get_Bay_No(:CONTRACT,:LOCATION_NO)");
      f.setReadOnly();

      f = itemblk0.addField("ROW_NO");
      f.setSize(6);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGLOCROW: Row");
      //f.setFunction("Inventory_Location_API.Get_Row_No(:CONTRACT,:LOCATION_NO)");
      f.setReadOnly();

      f = itemblk0.addField("TIER_NO");
      f.setSize(6);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGLOCTIER: Tier");
      //f.setFunction("Inventory_Location_API.Get_Tier_No(:CONTRACT,:LOCATION_NO)");
      f.setReadOnly();

      f = itemblk0.addField("BIN_NO");
      f.setSize(6);
      f.setLabel("PCMWINVENTORYPARTINSTOCKDLGLOCBIN: Bin");
      //f.setFunction("Inventory_Location_API.Get_Bin_No(:CONTRACT,:LOCATION_NO)");
      f.setReadOnly();

      f = itemblk0.addField("ITEM0_ENG_CHG");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("MODULENAME");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("HEAD_PART_OWNERSHIP");
      f.setHidden();

      f = itemblk0.addField("COST_DETAIL_INFO");
      f.setFunction("NULL");
      f.setCustomValidation("CONTRACT,PART_NO,CONFIGURATION_ID,SERIAL_NO,LOT_BATCH_NO,CONDITION_CODE,HEAD_PART_OWNERSHIP","COST_DETAIL_INFO");

      itemblk0.setView("INVENTORY_PART_IN_STOCK_DELIV");

      //itemblk0.setView("INVENTORY_PART_IN_STOCK");
      itemblk0.defineCommand("INVENTORY_PART_IN_STOCK_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);
      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      //itembar0.defineCommand(itembar0.SAVERETURN,"ok","checkItem0Fields()");
      itembar0.defineCommand(itembar0.SAVERETURN,"ok","checkBeforeSave()");
      //itembar0.disableCommand(itembar0.NEWROW);
      itembar0.disableCommand(itembar0.DUPLICATEROW);
      itembar0.disableCommand(itembar0.DELETE); 
      itembar0.enableCommand(itembar0.FIND);

      itembar0.enableRowStatus();

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle("PCMWINVENTORYPARTINSTOCKDLGITM: Return Material to Inventory Items");

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
      itemlay0.setDialogColumns(2);


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
                                         "ITEM0.SaveReturn",
                                         ""
                                      });
            appendJavaScript(script);

            appendJavaScript("function checkBeforeSave(){\n");
            appendJavaScript("   return checkItemOwner() && !defineCostStructure(-1);\n");
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
         appendJavaScript("   return checkItemOwner();\n");
         appendJavaScript("}\n"); 
      }
   }

   public void adjust(){
      ASPManager mgr = getASPManager();
      mgr.getASPField("COST_DETAIL_INFO").setHidden();
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "PCMWINVENTORYPARTINSTOCKDLGTITLE: Return Material to Inventory";
   }

   protected String getTitle()
   {
      return "PCMWINVENTORYPARTINSTOCKDLGTITLE: Return Material to Inventory";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendToHTML(headlay.show());

      if ((headlay.isSingleLayout() || headlay.isCustomLayout()) && (headset.countRows() > 0))
      {
         appendToHTML(itemlay0.show());
      }

      appendToHTML("<br>\n");
      appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWINVENTORYPARTINSTOCKDLGTITLEBUTEXT3: <Back"),""));

      if (okClose)
      {
         appendDirtyJavaScript("  window.close();\n");
      }
      // Bug 85710, Start
      //refreshMain = true;
      // Bug 85710, End

      if (refreshMain)
      {
         ctx.writeFlag("REFRESHMAIN",false);
         appendDirtyJavaScript("  window.open('");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr));	   //XSS_Safe AMNILK 20070716
         appendDirtyJavaScript("' + \"&OKMANISSUE=1\",'");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));  //XSS_Safe AMNILK 20070716
         appendDirtyJavaScript("',\"\");\n");       
         appendDirtyJavaScript("  window.close();\n");
      }

      appendDirtyJavaScript("function checkItemOwner()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (checkItem0Fields())\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (f.WO_CUST.value != '' && f.OWNER.value != '' && f.WO_CUST.value != f.OWNER.value) \n");
      appendDirtyJavaScript("         return confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWINVENTORYPARTINSTOCKDLGDIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      else if (f.WO_CUST.value == '' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("         return confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWINVENTORYPARTINSTOCKDLGNOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      else \n");
      appendDirtyJavaScript("         return true;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
   }
}
