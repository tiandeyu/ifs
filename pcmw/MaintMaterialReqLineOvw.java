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
* File                          :  MaintMaterialReqLineOvw.java
* Description                   :
* Notes                         :
* Other Programs Called :
* ----------------------------------------------------------------------------
* Created     : ASSALK 06/07/2007
* Modified    :
* 27-07-07  ASSALK  Call 146946. Modified preDefine(). hid PART_OWNERSHIP_DB.
* 01-08-07  ASSALK  NOIID: Modified okFind(). made select list simple.
* 10-09-07  ASSALK  148510, Modified preDefine(), issue(). Note for AD2.1: This change only for sparx 
* 04-02-08  NIJALK  Bug 66456, Modified GetInventoryQuantity().
* 26-04-10  SHAFLK  Bug 89978, Modified run(), okFind() and preDefine().
* ----------------------------------------------------------------------------
*/

//-----------------------------------------------------------------------------
//-----------------------------   Package def  ------------------------------
//-----------------------------------------------------------------------------

package ifs.pcmw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class MaintMaterialReqLineOvw extends ASPPageProvider {

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintMaterialReqLineOvw");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPCommand cmd;
   private ASPContext ctx;
   private String sOpenNewWindow = "FALSE";
   private String sMaintMatOrdNo = "FALSE";
   private String sNewWindowName = "NULL";
   private String sUrlString = "";
   private boolean bOpenNewWindow;
   private String sNewWinHandle = "";
   private String sOpenCreRepNonSer = "";
   private String sCreRepNonSerPath = "";
   private String qrystr = "";
   private String sSecOk1 = "FALSE";
   private String sSecOk2 = "FALSE";
   private String sSecOk3 = "FALSE";
   private String sSecOk4 = "FALSE";
   private String sSecurityChecked = "FALSE";

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  MaintMaterialReqLineOvw (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();

      qrystr = ctx.readValue("QRYSTR", "");
      sSecOk1 = ctx.readValue("SECOK1", "FALSE");
      sSecOk2 = ctx.readValue("SECOK2", "FALSE");
      sSecOk3 = ctx.readValue("SECOK3", "FALSE");
      sSecOk4 = ctx.readValue("SECOK4", "FALSE");
      sSecurityChecked = ctx.readValue("SECURITYCHECKED","FALSE");

      if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         okFind();
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("LINE_ITEM_NO")) )
         okFind();
      else if ( !mgr.isEmpty(mgr.readValue("REFRESH_FLAG")))
         refreshRow();
      //Bug 89978, start
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
          okFind();
      //Bug 89978, end
      if (!"TRUE".equals(sSecurityChecked)) {
         checkSecurity();
      }

      adjust();

      ctx.writeValue("QRYSTR", qrystr);
      ctx.writeValue("SECOK1",sSecOk1);
      ctx.writeValue("SECOK2",sSecOk2);
      ctx.writeValue("SECOK3",sSecOk3);
      ctx.writeValue("SECOK4",sSecOk4);
      ctx.writeValue("SECURITYCHECKED",sSecurityChecked);
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      trans.clear();

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      String sSelectList = "";
      String sSelectPart = ""; 

      if ("TRUE".equals(sSecOk1))
         if ("TRUE".equals(sSecOk2))
            sSelectPart = "DECODE(SUPPLY_CODE_DB,'INVENT_ORDER'," +
                          "INVENTORY_PART_IN_STOCK_API.Get_Inventory_Quantity(SPARE_CONTRACT,PART_NO,NULL,'ONHAND',NULL,NULL,PART_OWNERSHIP_DB," +
                          "NULL,NULL,NULL,OWNER,NULL,'PICKING','F','PALLET','DEEP','BUFFER','DELIVERY','SHIPMENT','MANUFACTURING'," +
                          "NULL,NULL,NULL,NULL,'TRUE','FALSE','0',NULL,NULL,NULL,NULL,NULL,CONDITION_CODE)," +
                          "INVENTORY_PART_IN_STOCK_API.Get_Inventory_Quantity(" +
                          "SPARE_CONTRACT,PART_NO,NULL,'ONHAND',NULL,NULL,PART_OWNERSHIP_DB," +
                          "NULL,NULL,NULL,OWNER,NULL,'PICKING','F','PALLET','DEEP','BUFFER','DELIVERY','SHIPMENT','MANUFACTURING'," +
                          "NULL,NULL,NULL,NULL,'FALSE','TRUE',NULL," +
                          "ACTIVITY_API.GET_PROJECT_ID(" +
                          "MAINT_MATERIAL_REQUISITION_API.GET_ACTIVITY_SEQ(MAINT_MATERIAL_ORDER_NO))," +
                          "NULL,NULL,NULL,NULL,CONDITION_CODE) )";
         else
            sSelectPart = "DECODE(SUPPLY_CODE_DB,'INVENT_ORDER',INVENTORY_PART_IN_STOCK_API.Get_Inventory_Quantity(SPARE_CONTRACT,PART_NO,NULL,'ONHAND',NULL,NULL,PART_OWNERSHIP_DB," +
                          "NULL,NULL,NULL,OWNER,NULL,'PICKING','F','PALLET','DEEP','BUFFER','DELIVERY','SHIPMENT','MANUFACTURING'," +
                          "NULL,NULL,NULL,NULL,'TRUE','FALSE','0',NULL,NULL,NULL,NULL,NULL,CONDITION_CODE),INVENTORY_PART_IN_STOCK_API.Get_Inventory_Quantity(" +
                          "SPARE_CONTRACT,PART_NO,NULL,'ONHAND',NULL,NULL,PART_OWNERSHIP_DB," +
                          "NULL,NULL,NULL,OWNER,NULL,'PICKING','F','PALLET','DEEP','BUFFER','DELIVERY','SHIPMENT','MANUFACTURING'," +
                          "NULL,NULL,NULL,NULL,'FALSE','TRUE',NULL,NULL,NULL,NULL,NULL,NULL,CONDITION_CODE) )";
      else
         sSelectPart = "''";

      sSelectList = headblk.getSelectList().replaceAll("'' QTY_ONHAND",sSelectPart+" QTY_ONHAND");

      q.setSelectList(sSelectList);
      q.addWhereCondition("WORK_ORDER_API.GET_CONTRACT(WO_NO) = User_Allowed_Site_API.Authorized(WORK_ORDER_API.GET_CONTRACT(WO_NO))");
      q.includeMeta("ALL");
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 ) {
         mgr.showAlert("PCMWMAINTMATERIALREQLINENODATA: No data found.");
         headset.clear();
      } else {
         int curr = headset.getCurrentRowNo();
         // calculate sales price amount
         trans.clear();
         double dPriceConvFac = 0.00;
         double dConvFac = 0.00;

         ASPBuffer tempBuff;

         for (int i=0; i<headset.countRows(); i++) {
            headset.goTo(i);

            if ("TRUE".equals(sSecOk3)) {
               trans.clear();
               cmd = trans.addCustomFunction("GETPRICECONF","SALES_PART_API.Get_Price_Conv_Factor","PRICE_CONV_FACTOR");
               cmd.addParameter("CATALOG_CONTRACT",headset.getValue("CATALOG_CONTRACT"));
               cmd.addParameter("CATALOG_NO",headset.getValue("CATALOG_NO"));

               trans = mgr.perform(trans);
               String val = trans.getValue("GETPRICECONF/DATA/PRICE_CONV_FACTOR");

               if (!mgr.isEmpty(val))
                  dPriceConvFac = Double.parseDouble(val);
               else
                  dPriceConvFac = 1;
            }
            if ("TRUE".equals(sSecOk4)) {
               trans.clear();
               cmd = trans.addCustomFunction("GETCONVFAC","SALES_PART_API.Get_Conv_Factor","CONV_FACTOR");
               cmd.addParameter("CATALOG_CONTRACT",headset.getValue("CATALOG_CONTRACT"));
               cmd.addParameter("CATALOG_NO",headset.getValue("CATALOG_NO"));

               trans = mgr.perform(trans);
               String val = trans.getValue("GETPRICECONF/DATA/CONV_FACTOR");

               if (!mgr.isEmpty(val))
                  dConvFac = Double.parseDouble(val);
               else
                  dConvFac = 1.00;
            }

            double dListPrice = (mgr.isEmpty(headset.getDbValue("LIST_PRICE")) ? 0 : Double.parseDouble(headset.getDbValue("LIST_PRICE")));
            double dPlanQty = (mgr.isEmpty(headset.getDbValue("PLAN_QTY")) ? 0 : Double.parseDouble(headset.getDbValue("PLAN_QTY")));
            double dDiscount = (mgr.isEmpty(headset.getDbValue("DISCOUNT")) ? 0 : Double.parseDouble(headset.getDbValue("DISCOUNT")));

            double dSalesPrice = dListPrice * dPriceConvFac * dPlanQty / dConvFac;
            dSalesPrice = dSalesPrice - (dDiscount / 100 * dListPrice);
            double dDiscountedPrice = dListPrice - (dDiscount / 100 * dListPrice);

            tempBuff = headset.getRow();

            tempBuff.setNumberValue("SALES_PRICE_AMOUNT",dSalesPrice );
            tempBuff.setNumberValue("DISCOUNT_PRICE",dDiscountedPrice );

            headset.setRow(tempBuff);
            headset.goTo(curr);
         }
      }

      qrystr = mgr.createSearchURL(headblk); 
   }



   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WORK_ORDER_API.GET_CONTRACT(WO_NO) = User_Allowed_Site_API.Authorized(WORK_ORDER_API.GET_CONTRACT(WO_NO))");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }

   public void maintMatReq()
   {
      int curr = headset.getCurrentRowNo();
      if (headlay.isMultirowLayout()) {
         headset.goTo(headset.getRowSelected());
      }

      sMaintMatOrdNo = headset.getValue("MAINT_MATERIAL_ORDER_NO");
      sOpenNewWindow = "TRUE";
      sNewWindowName = "MaintMaterialRequisition.page";
      headset.goTo(curr);
   }

   public void reserve()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      int curr = headset.getCurrentRowNo();

      double qtyOnHand = GetInventoryQuantity("ONHAND");
      double nRes = GetInventoryQuantity("RESERVED");
      double nAvalToRes =  qtyOnHand - nRes;
      String sAvalToRes = mgr.getASPField("QTY_ASSIGNED").formatNumber(nAvalToRes);
      double plan_qty = headset.getRow().getNumberValue("PLAN_QTY");

      cmd = trans.addCustomCommand("RESSHORT_","MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Short");
      cmd.addParameter("QTY_LEFT");
      cmd.addParameter("MAINT_MATERIAL_ORDER_NO", headset.getValue("MAINT_MATERIAL_ORDER_NO"));
      cmd.addParameter("LINE_ITEM_NO", headset.getValue("LINE_ITEM_NO"));
      cmd.addParameter("LOCATION_NO", "");    
      cmd.addParameter("LOT_BATCH_NO", "");
      cmd.addParameter("SERIAL_NO", "");
      cmd.addParameter("ENG_CHG_LEVEL", "");
      cmd.addParameter("WAIV_DEV_REJ_NO", "");
      cmd.addParameter("ACTIVITY_SEQ", "");
      cmd.addParameter("PROJECT_ID", "");
      cmd.addParameter("QTY_TO_ASSIGN", "");

      if (plan_qty > nAvalToRes) {
         mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQLINEOVWAVAIL: Available quantity for part &1 was &2.",headset.getRow().getValue("PART_NO"), sAvalToRes)); 
      }

      trans = mgr.perform(trans);

      double nQtyShort = trans.getNumberValue("RESSHORT/DATA/QTY_LEFT");
      if (isNaN(nQtyShort))
         nQtyShort = 0;

      if (nQtyShort > 0)
         mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQLINEOVWCOULDNOTALL: All material could not be allocated for part &1. Remaining quantity: &2.", headset.getRow().getValue("PART_NO"),nQtyShort+""));

      headset.refreshRow();
      headset.setRow(getQtyOnHand(headset.getRow()));

      headset.goTo(curr);
   }

   public ASPBuffer getQtyOnHand(ASPBuffer buff)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      String sQtyOnHand = "";

      if ("TRUE".equals(sSecOk1)) {
         if ("TRUE".equals(sSecOk2)) {
            cmd = trans.addCustomFunction("GETACTSEQ","MAINT_MATERIAL_REQUISITION_API.GET_ACTIVITY_SEQ","DUMMY_FLD4");
            cmd.addParameter("MAINT_MATERIAL_ORDER_NO",buff.getValue("MAINT_MATERIAL_ORDER_NO"));

            cmd = trans.addCustomFunction("GETPRJID","ACTIVITY_API.GET_PROJECT_ID","DUMMY_FLD3");
            cmd.addReference("DUMMY_FLD4","GETACTSEQ/DATA");

            cmd = trans.addCustomFunction("GETINTQTY1","INVENTORY_PART_IN_STOCK_API.Get_Inventory_Quantity","DUMMY_NUM1");
            cmd.addParameter("SPARE_CONTRACT",buff.getValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",buff.getValue("PART_NO"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","ONHAND");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("PART_OWNERSHIP_DB",buff.getValue("PART_OWNERSHIP_DB"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("OWNER",buff.getValue("OWNER"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","PICKING");
            cmd.addParameter("DUMMY_FLD2","F");
            cmd.addParameter("DUMMY_FLD2","PALLET");
            cmd.addParameter("DUMMY_FLD2","DEEP");
            cmd.addParameter("DUMMY_FLD2","BUFFER");
            cmd.addParameter("DUMMY_FLD2","DELIVERY");
            cmd.addParameter("DUMMY_FLD2","SHIPMENT");
            cmd.addParameter("DUMMY_FLD2","MANUFACTURING");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","TRUE");
            cmd.addParameter("DUMMY_FLD2","FALSE");
            cmd.addParameter("DUMMY_FLD2","0");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("CONDITION_CODE",buff.getValue("CONDITION_CODE"));

            cmd = trans.addCustomFunction("GETINTQTY2","INVENTORY_PART_IN_STOCK_API.Get_Inventory_Quantity","DUMMY_NUM2");
            cmd.addParameter("SPARE_CONTRACT",buff.getValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",buff.getValue("PART_NO"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","ONHAND");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("PART_OWNERSHIP_DB",buff.getValue("PART_OWNERSHIP_DB"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("OWNER",buff.getValue("OWNER"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","PICKING");
            cmd.addParameter("DUMMY_FLD2","F");
            cmd.addParameter("DUMMY_FLD2","PALLET");
            cmd.addParameter("DUMMY_FLD2","DEEP");
            cmd.addParameter("DUMMY_FLD2","BUFFER");
            cmd.addParameter("DUMMY_FLD2","DELIVERY");
            cmd.addParameter("DUMMY_FLD2","SHIPMENT");
            cmd.addParameter("DUMMY_FLD2","MANUFACTURING");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","FALSE");
            cmd.addParameter("DUMMY_FLD2","TRUE");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addReference("DUMMY_FLD3","GETPRJID/DATA");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("CONDITION_CODE",buff.getValue("CONDITION_CODE"));
         } else {
            cmd = trans.addCustomFunction("GETINTQTY1","INVENTORY_PART_IN_STOCK_API.Get_Inventory_Quantity","DUMMY_NUM1");
            cmd.addParameter("SPARE_CONTRACT",buff.getValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",buff.getValue("PART_NO"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","ONHAND");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("PART_OWNERSHIP_DB",buff.getValue("PART_OWNERSHIP_DB"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("OWNER",buff.getValue("OWNER"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","PICKING");
            cmd.addParameter("DUMMY_FLD2","F");
            cmd.addParameter("DUMMY_FLD2","PALLET");
            cmd.addParameter("DUMMY_FLD2","DEEP");
            cmd.addParameter("DUMMY_FLD2","BUFFER");
            cmd.addParameter("DUMMY_FLD2","DELIVERY");
            cmd.addParameter("DUMMY_FLD2","SHIPMENT");
            cmd.addParameter("DUMMY_FLD2","MANUFACTURING");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","TRUE");
            cmd.addParameter("DUMMY_FLD2","FALSE");
            cmd.addParameter("DUMMY_FLD2","0");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("CONDITION_CODE",buff.getValue("CONDITION_CODE"));

            cmd = trans.addCustomFunction("GETINVQTY2","INVENTORY_PART_IN_STOCK_API.Get_Inventory_Quantity","DUMMY_NUM2");
            cmd.addParameter("SPARE_CONTRACT",buff.getValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",buff.getValue("PART_NO"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","ONHAND");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("PART_OWNERSHIP_DB",buff.getValue("PART_OWNERSHIP_DB"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("OWNER",buff.getValue("OWNER"));
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","PICKING");
            cmd.addParameter("DUMMY_FLD2","F");
            cmd.addParameter("DUMMY_FLD2","PALLET");
            cmd.addParameter("DUMMY_FLD2","DEEP");
            cmd.addParameter("DUMMY_FLD2","BUFFER");
            cmd.addParameter("DUMMY_FLD2","DELIVERY");
            cmd.addParameter("DUMMY_FLD2","SHIPMENT");
            cmd.addParameter("DUMMY_FLD2","MANUFACTURING");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","FALSE");
            cmd.addParameter("DUMMY_FLD2","TRUE");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("DUMMY_FLD2","");
            cmd.addParameter("CONDITION_CODE",buff.getValue("CONDITION_CODE"));
         }
         trans = mgr.perform(trans);

         sQtyOnHand = ("INVENT_ORDER".equals(buff.getValue("SUPPLY_CODE_DB"))) ? trans.getValue("GETINTQTY1/DATA/DUMMY_NUM1"):trans.getValue("GETINTQTY2/DATA/DUMMY_NUM2");         
      } else
         sQtyOnHand = "0";

      buff.setValue("QTY_ONHAND",sQtyOnHand);
      return buff;
   }

   public void issueManually()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();

      String creRepWO = "FALSE";
      String nQtyLeft = "";

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      trans.clear();

      cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
      cmd.addParameter("DB_STATE","Released"); 

      cmd = trans.addCustomFunction("SERIALTRA","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","SERIAL_TRACK");
      cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));

      cmd = trans.addCustomFunction("AUTOREP","MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
      cmd.addParameter("SPARE_CONTRACT",headset.getRow().getValue("SPARE_CONTRACT"));
      cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));

      cmd = trans.addCustomFunction("REPAIR","MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
      cmd.addParameter("SPARE_CONTRACT",headset.getRow().getValue("SPARE_CONTRACT"));
      cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));

      cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","ISS_ALLO");
      cmd.addParameter("WO_NO",headset.getRow().getFieldValue("WO_NO"));

      trans = mgr.perform(trans);

      String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
      String hasSerialNum = trans.getValue("SERIALTRA/DATA/SERIAL_TRACK");
      String isAutoRepairable = trans.getValue("AUTOREP/DATA/AUTO_REPAIRABLE");
      String isRepairable = trans.getValue("REPAIR/DATA/REPAIRABLE");
      String bIssAllowed = trans.getValue("ISSALLOW/DATA/ISS_ALLO");
      String dfStatus = headset.getRow().getValue("STATE");

      double plan_qty = headset.getRow().getNumberValue("PLAN_QTY");
      if (isNaN(plan_qty))
         plan_qty = 0;

      double qty = headset.getRow().getNumberValue("QTY");
      if (isNaN(qty))
         qty = 0;

      double qty_return = headset.getRow().getNumberValue("QTY_RETURNED");
      if ( isNaN(qty_return) )
         qty_return = 0;

      double nQtyLeftNum = plan_qty - qty;

      if (nQtyLeftNum < 0)
         nQtyLeft = "0";
      else
         nQtyLeft = String.valueOf(nQtyLeftNum);   

      if (("TRUE".equals(isAutoRepairable)) && ("TRUE".equals(isRepairable)) && ("NOT SERIAL TRACKING".equals(hasSerialNum)))
         creRepWO = "TRUE";

      bOpenNewWindow = true;

      trans.clear();
      cmd = trans.addCustomFunction("GETMCHCODE","WORK_ORDER_API.Get_Mch_Code","DUMMY_FLD1");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      cmd = trans.addCustomFunction("GETMCHDESC","Maintenance_Object_API.Get_Mch_Name","DUMMY_FLD2");
      cmd.addParameter("WO_CONTRACT",headset.getRow().getValue("WO_CONTRACT"));
      cmd.addReference("DUMMY_FLD1","GETMCHCODE/DATA");

      cmd = trans.addCustomFunction("GETCONDESC","CONDITION_CODE_API.Get_Description","DUMMY_FLD3");
      cmd.addParameter("CONDITION_CODE",headset.getRow().getValue("CONDITION_CODE"));

      trans = mgr.perform(trans);

      sUrlString = "InventoryPartLocationDlg.page?PART_NO=" + mgr.URLEncode(headset.getValue("PART_NO")) + 
                   "&CONTRACT=" + mgr.URLEncode(headset.getValue("SPARE_CONTRACT")) + 
                   "&FRMNAME=" + mgr.URLEncode("maintMatReq") +
                   "&QRYSTR=" + mgr.URLEncode("MaintMaterialReqLineOvw.page") +
                   "&WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) +
                   "&LINE_ITEM_NO=" + mgr.URLEncode(headset.getValue("LINE_ITEM_NO")) +
                   "&DESCRIPTION=" + mgr.URLEncode(trans.getValue("GETMCHDESC/DATA/DUMMY_FLD2")) +
                   "&HEAD_CONDITION_CODE=" + mgr.URLEncode(headset.getRow().getValue("CONDITION_CODE")) +
                   "&HEAD_CONDITION_CODE_DESC=" + mgr.URLEncode(trans.getValue("GETCONDESC/DATA/DUMMY_FLD3")) +
                   "&QTYLEFT=" + mgr.URLEncode(nQtyLeft) +
                   "&MAINTMATORDNO=" + mgr.URLEncode(headset.getValue("MAINT_MATERIAL_ORDER_NO"))+
                   "&OWNERSHIP=" + mgr.URLEncode(headset.getRow().getValue("PART_OWNERSHIP")) +
                   "&OWNER=" + mgr.URLEncode(headset.getRow().getValue("OWNER")) +
                   "&OWNERNAME=" + mgr.URLEncode(headset.getRow().getValue("OWNER_NAME"));

      sNewWinHandle = "manIssue"; 

      headset.goTo(currrow);
   }

   public void refreshRow()
   {
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      int curr = headset.getCurrentRowNo();
      headset.refreshRow();
      headset.setRow(getQtyOnHand(headset.getRow()));
      headset.goTo(curr);
   }

   public void checkSecurity()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      trans.addSecurityQuery("INVENTORY_PART_IN_STOCK_API","Get_Inventory_Quantity");
      trans.addSecurityQuery("ACTIVITY_API","GET_PROJECT_ID");
      trans.addSecurityQuery("SALES_PART_API","Get_Price_Conv_Factor");
      trans.addSecurityQuery("SALES_PART_API","Get_Conv_Factor");

      trans = mgr.perform(trans);

      ASPBuffer availableObj = trans.getSecurityInfo();

      if (availableObj.itemExists("INVENTORY_PART_IN_STOCK_API.Get_Inventory_Quantity"))
         sSecOk1 = "TRUE";

      if (availableObj.itemExists("ACTIVITY_API.GET_PROJECT_ID"))
         sSecOk2 = "TRUE";

      if (availableObj.itemExists("SALES_PART_API.Get_Price_Conv_Factor"))
         sSecOk3 = "TRUE";

      if (availableObj.itemExists("SALES_PART_API.Get_Conv_Factor"))
         sSecOk4 = "TRUE";
   }

   public void printPickList()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPBuffer print;
      ASPBuffer printBuff;
      String attr1;
      String attr2;
      String attr3;
      String attr4;

      if ( headlay.isMultirowLayout() )
         headset.goTo(headset.getRowSelected());

      String orderNo = headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO");
      String orderList =  orderNo +";";

      trans.clear();

      cmd = trans.addCustomFunction("RESEXIST","MAINT_MATERIAL_REQUISITION_API.New_Assign_Exist","DUMMY_FLD1"); 
      cmd.addParameter("DUMMY_FLD2",orderList); 

      trans = mgr.perform(trans);

      String res_exist = trans.getValue("RESEXIST/DATA/DUMMY_FLD1");
      trans.clear();
      if ( !mgr.isEmpty(orderNo) ) {
         if ("1".equals(res_exist)) {

            attr1 = "REPORT_ID" + (char)31 + "MAINT_MATERIAL_REQUISITION_REP" + (char)30;
            attr2 = "MAINT_MTRL_ORDER_NO" + (char)31 + headset.getValue("MAINT_MATERIAL_ORDER_NO") + (char)30;
            attr3 =  "";
            attr4 =  "";

            cmd = trans.addCustomCommand("PRINTPICKLIST","Archive_API.New_Client_Report");
            cmd.addParameter("ATTR0");                       
            cmd.addParameter("ATTR1",attr1);       
            cmd.addParameter("ATTR2",attr2);              
            cmd.addParameter("ATTR3",attr3);      
            cmd.addParameter("ATTR4",attr4);  

            trans = mgr.perform(trans);

            String attr0 = trans.getValue("PRINTPICKLIST/DATA/ATTR0");

            print = mgr.newASPBuffer();

            printBuff = print.addBuffer("DATA");
            printBuff.addItem("RESULT_KEY", attr0);

            callPrintDlg(print,true);
         } else

            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQLINEOVWNONEW: No new Assigned stock for Material Order &1.",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO")));
      } else
         mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQLINEOVWCANNOT: Cannot perform on selected line"));
   }

   public void issue()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      double qtyOnHand = 0.0;
      double nQtyAvblToIssue = 0.0;
      double nTotQtyRes = 0.0;
      double nTotQtyPlanable = 0.0;
      double nCount = 0.0;

      ASPTransactionBuffer secBuff;
      boolean securityOk = false;
      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Inventory_Part_In_Stock_API");
      secBuff = mgr.perform(secBuff);

      if ( secBuff.getSecurityInfo().itemExists("Inventory_Part_In_Stock_API") )
         securityOk = true;

      int currrow = headset.getCurrentRowNo();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      int currRowItem = headset.getCurrentRowNo(); 

      trans.clear();

      cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DUMMY_FLD1"); 
      cmd.addParameter("DUMMY_FLD1","Released"); 

      cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","DUMMY_FLD2");
      cmd.addParameter("WO_NO",headset.getRow().getFieldValue("WO_NO"));

      cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");

      trans = mgr.perform(trans);

      String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DUMMY_FLD1");
      String bIssAllowed = trans.getValue("ISSALLOW/DATA/DUMMY_FLD2");
      String dfStatus = headset.getRow().getValue("STATE");
      String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");

      trans.clear();

      cmd = trans.addCustomFunction("AUTOREP","MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
      cmd.addParameter("SPARE_CONTRACT",headset.getRow().getValue("SPARE_CONTRACT"));
      cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));

      cmd = trans.addCustomFunction("REPAIR","MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
      cmd.addParameter("SPARE_CONTRACT",headset.getRow().getValue("SPARE_CONTRACT"));
      cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));

      cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
      cmd.addParameter("PART_OWNERSHIP",headset.getRow().getValue("PART_OWNERSHIP"));

      if ( securityOk ) {
         cmd = trans.addCustomFunction("INVONHANDRES","Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Res","QTYRES");
         cmd.addParameter("SPARE_CONTRACT",headset.getRow().getFieldValue("SPARE_CONTRACT"));
         cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));
         cmd.addParameter("CONFIGURATION","");

         cmd = trans.addCustomFunction("INVONHANDPLAN","Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Onhand","QTYPLANNABLE");
         cmd.addParameter("SPARE_CONTRACT",headset.getRow().getFieldValue("SPARE_CONTRACT"));
         cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));
         cmd.addParameter("CONFIGURATION","");
      }

      trans = mgr.perform(trans);

      String isAutoRepairable = trans.getValue("AUTOREP/DATA/AUTO_REPAIRABLE");
      String isRepairable = trans.getValue("REPAIR/DATA/REPAIRABLE");

      if ( securityOk ) {
         nTotQtyRes = trans.getNumberValue("INVONHANDRES/DATA/QTYRES");
         if ( isNaN(nTotQtyRes) )
            nTotQtyRes = 0;

         nTotQtyPlanable = trans.getNumberValue("INVONHANDPLAN/DATA/QTYPLANNABLE");
         if ( isNaN(nTotQtyPlanable) )
            nTotQtyPlanable = 0;
      } else {
         nTotQtyRes = 0;
         nTotQtyPlanable = 0;
      }

      double plan_qty = headset.getRow().getNumberValue("PLAN_QTY");
      if (isNaN(plan_qty))
         plan_qty = 0;

      double qty = headset.getRow().getNumberValue("QTY");
      if (isNaN(qty))
         qty = 0;

      double qty_return = headset.getRow().getNumberValue("QTY_RETURNED");
      if ( isNaN(qty_return) )
         qty_return = 0;

      double qty_outstanding = plan_qty - qty; 
      double qty_assign = headset.getRow().getNumberValue("QTY_ASSIGNED");
      if ( isNaN(qty_assign) )
         qty_assign = 0;

      double qty_on = headset.getRow().getNumberValue("QTYONHAND");
      if ( isNaN(qty_on) )
         qty_on = 0;

      qtyOnHand = GetInventoryQuantity("ONHAND");
      double nRes = GetInventoryQuantity("RESERVED");
      double nAvailToIss = qtyOnHand - nRes + qty_assign;
      String sAvailToIss = mgr.getASPField("QTY_ASSIGNED").formatNumber(nAvailToIss);

      trans.clear();

      cmd = trans.addCustomCommand("MAKEISSUDETA","MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail");
      cmd.addParameter("DUMMY_ACT_QTY_ISSUED");
      cmd.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
      cmd.addParameter("LINE_ITEM_NO",headset.getRow().getValue("LINE_ITEM_NO"));
      cmd.addParameter("LOCATION_NO","");    
      cmd.addParameter("LOT_BATCH_NO","");
      cmd.addParameter("SERIAL_NO","");
      cmd.addParameter("ENG_CHG_LEVEL","");
      cmd.addParameter("WAIV_DEV_REJ_NO","");
      cmd.addParameter("PROJECT_ID","");
      cmd.addParameter("ACTIVITY_SEQ","");
      cmd.addParameter("QTY_TO_ISSUE","");

      trans = mgr.perform(trans);

      if (plan_qty > nAvailToIss)
         mgr.showAlert(mgr.translate("PCMWAMAINTMATERIALREQLINEOVWAVAIL: Available quantity for part &1 was &2.",
                                     headset.getRow().getValue("PART_NO"),sAvailToIss));

      if (("TRUE".equals(isAutoRepairable)) &&  ("TRUE".equals(isRepairable))) {
         double nQtyPlanToIssue = (plan_qty - qty);
         double nAvablQty =  (nTotQtyPlanable - nTotQtyRes);
         sOpenCreRepNonSer = "FALSE";
         if (qty_assign==0) {
            if (nQtyPlanToIssue <= nAvablQty) {
               sOpenCreRepNonSer = "TRUE";
               nCount = nQtyPlanToIssue;
            } else {
               sOpenCreRepNonSer = "TRUE";
               nCount = nAvablQty;
            }

         } else if (qty_assign > 0 ) {

            if (qty_assign == plan_qty) {
               sOpenCreRepNonSer = "TRUE";
               nCount = qty_assign;
            } else if ((qty_assign < plan_qty)) {
               if ((nAvablQty == 0)) {
                  sOpenCreRepNonSer = "TRUE";
                  nCount = qty_assign;
               } else if ((nAvablQty >= (plan_qty - qty_assign))) {
                  sOpenCreRepNonSer = "TRUE";
                  nCount = plan_qty;
               } else if ((nAvablQty < (plan_qty - qty_assign))) {
                  sOpenCreRepNonSer = "TRUE";
                  nCount = (qty_assign + nAvablQty);
               }
            }
         }
         if (nCount == 0) {
            sOpenCreRepNonSer = "FALSE";
         }
         sCreRepNonSerPath = "CreateRepairWorkOrderForNonSerialParts.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+      
                             "&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("CONTRACT"))+
                             "&MCH_CODE="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE"))+
                             "&DESCRIPTION="+mgr.URLEncode(headset.getRow().getValue("DESCRIPTION"))+
                             "&PART_NO="+mgr.URLEncode(headset.getRow().getValue("PART_NO"))+
                             "&SPAREDESCRIPTION="+mgr.URLEncode(headset.getRow().getValue("SPAREDESCRIPTION"))+
                             "&SPARE_CONTRACT="+mgr.URLEncode(headset.getRow().getValue("SPARE_CONTRACT"))+
                             "&COUNT="+ mgr.URLEncode(new Double(nCount).toString()) ;                
      }

      headset.refreshRow();
      headset.setRow(getQtyOnHand(headset.getRow()));
      headset.goTo(currRowItem); 
   }

   public double GetInventoryQuantity(String sQtyType)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      String sOnhand = "ONHAND";
      String sPicking = "PICKING";
      String sF = "F";
      String sPallet = "PALLET";
      String sDeep = "DEEP";
      String sBuffer = "BUFFER";
      String sDelivery = "DELIVERY";
      String sShipment = "SHIPMENT";
      String sManufacturing = "MANUFACTURING";
      String sOwnershipDb = "COMPANY OWNED";
      String sClientOwnershipConsignment = "CONSIGNMENT";
      double nQty = 0.0;
      String sTrue = "TRUE";
      String sFalse = "FALSE";

      ASPTransactionBuffer secBuff;
      boolean securityOk = false;
      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Inventory_Part_In_Stock_API");
      secBuff = mgr.perform(secBuff);

      if ( secBuff.getSecurityInfo().itemExists("Inventory_Part_In_Stock_API") )
         securityOk = true;

      trans.clear();

      cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
      cmd.addParameter("PART_OWNERSHIP",headset.getRow().getValue("PART_OWNERSHIP"));
      trans = mgr.validate(trans);
      String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
      trans.clear();

      if ( securityOk ) {
         trans.clear();
         cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
         trans = mgr.validate(trans);
         String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
         trans.clear();

         if ("COMPANY OWNED".equals(sClientOwnershipDefault))
            sClientOwnershipConsignment="CONSIGNMENT";
         else
            sClientOwnershipConsignment=null;

         if (sStandardInv.equals(headset.getRow().getValue("SUPPLY_CODE"))) {
            trans.clear();
            cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("CONTRACT",headset.getRow().getFieldValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));
            cmd.addParameter("CONFIGURATION",headset.getRow().getValue("CONFIGURATION"));
            cmd.addParameter("QTY_TYPE",sQtyType);
            cmd.addParameter("EXPIRATION");
            cmd.addParameter("SUPPLY_CONTROL");
            cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
            cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
            cmd.addParameter("OWNERSHIP_TYPE3");
            cmd.addParameter("OWNERSHIP_TYPE4");
            cmd.addParameter("OWNER");
            cmd.addParameter("OWNER_VENDOR");
            cmd.addParameter("LOCATION_TYPE1",sPicking);
            cmd.addParameter("LOCATION_TYPE2",sF);
            cmd.addParameter("LOCATION_TYPE3",sPallet);
            cmd.addParameter("LOCATION_TYPE4",sDeep);
            cmd.addParameter("LOCATION_TYPE5",sBuffer);
            cmd.addParameter("LOCATION_TYPE6",sDelivery);
            cmd.addParameter("LOCATION_TYPE7",sShipment);
            cmd.addParameter("LOCATION_TYPE8",sManufacturing);
            cmd.addParameter("LOT_BATCH_NO");
            cmd.addParameter("SERIAL_NO");
            cmd.addParameter("ENG_CHG_LEVEL");
            cmd.addParameter("WAIV_DEV_REJ_NO");
            cmd.addParameter("INCLUDE_STANDARD",sTrue);
            cmd.addParameter("INCLUDE_PROJECT",sFalse);
            cmd.addParameter("ACTIVITY_SEQ","0");
            cmd.addParameter("PROJECT_ID","");
            cmd.addParameter("LOCATION_NO");
            cmd.addParameter("ORDER_ISSUE");
            cmd.addParameter("AUTOMAT_RESERV");
            cmd.addParameter("MANUAL_RESERV");
            cmd.addParameter("CONDITION_CODE",headset.getRow().getFieldValue("CONDITION_CODE"));

         } else {
            trans.clear();
            //Bug 66456, Start
            if ("TRUE".equals(sSecOk2))
            {
                cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                cmd.addParameter("ACTIVITY_SEQ",mgr.readValue("ACTIVITY_SEQ"));
            }
            //Bug 66456, End

            cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("CONTRACT",headset.getRow().getFieldValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));
            cmd.addParameter("CONFIGURATION");
            cmd.addParameter("QTY_TYPE",sQtyType);
            cmd.addParameter("EXPIRATION");
            cmd.addParameter("SUPPLY_CONTROL");
            cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
            cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
            cmd.addParameter("OWNERSHIP_TYPE3");
            cmd.addParameter("OWNERSHIP_TYPE4");
            cmd.addParameter("OWNER");
            cmd.addParameter("OWNER_VENDOR");
            cmd.addParameter("LOCATION_TYPE1",sPicking);
            cmd.addParameter("LOCATION_TYPE2",sF);
            cmd.addParameter("LOCATION_TYPE3",sPallet);
            cmd.addParameter("LOCATION_TYPE4",sDeep);
            cmd.addParameter("LOCATION_TYPE5",sBuffer);
            cmd.addParameter("LOCATION_TYPE6",sDelivery);
            cmd.addParameter("LOCATION_TYPE7",sShipment);
            cmd.addParameter("LOCATION_TYPE8",sManufacturing);
            cmd.addParameter("LOT_BATCH_NO");
            cmd.addParameter("SERIAL_NO");
            cmd.addParameter("ENG_CHG_LEVEL");
            cmd.addParameter("WAIV_DEV_REJ_NO");
            cmd.addParameter("INCLUDE_STANDARD",sFalse);
            cmd.addParameter("INCLUDE_PROJECT",sTrue);
            cmd.addParameter("ACTIVITY_SEQ","0");
            //Bug 66456, Start
            if ("TRUE".equals(sSecOk2))
                cmd.addReference("PROJECT_ID", "PROJID/DATA");
            else
                cmd.addReference("PROJECT_ID", "");
            //Bug 66456, End
            cmd.addParameter("LOCATION_NO");
            cmd.addParameter("ORDER_ISSUE");
            cmd.addParameter("AUTOMAT_RESERV");
            cmd.addParameter("MANUAL_RESERV");
            cmd.addParameter("CONDITION_CODE",headset.getRow().getFieldValue("CONDITION_CODE"));
         }    
      }
      trans = mgr.perform(trans);


      if ( securityOk ) {
         nQty = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
         if ( isNaN(nQty) )
            nQty = 0;
      } else {
         nQty = 0.0;
      }

      return nQty;
   }

   public void reserveManually()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      int curr = headset.getCurrentRowNo();

      double plan_qty = headset.getRow().getNumberValue("PLAN_QTY");
      if (isNaN(plan_qty))
         plan_qty = 0;

      double qty = headset.getRow().getNumberValue("QTY");
      if (isNaN(qty))
         qty = 0;

      double qty_assign  = headset.getRow().getNumberValue("QTY_ASSIGNED");
      if (isNaN(qty_assign))
         qty_assign = 0;

      double qty_return = headset.getRow().getNumberValue("QTY_RETURNED");
      if (isNaN(qty_return))
         qty_return = 0;

      double nQtyLeftNum = plan_qty - qty - qty_assign;
      double nQuantityLeft = nQtyLeftNum;

      cmd = trans.addCustomFunction("GETMCHCODE","WORK_ORDER_API.Get_Mch_Code","DUMMY_FLD1");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      cmd = trans.addCustomFunction("GETMCHDESC","Maintenance_Object_API.Get_Mch_Name","DUMMY_FLD2");
      cmd.addParameter("WO_CONTRACT",headset.getRow().getValue("WO_CONTRACT"));
      cmd.addReference("DUMMY_FLD1","GETMCHCODE/DATA");

      cmd = trans.addCustomFunction("GETCONDESC","CONDITION_CODE_API.Get_Description","DUMMY_FLD3");
      cmd.addParameter("CONDITION_CODE",headset.getRow().getValue("CONDITION_CODE"));

      trans = mgr.perform(trans);

      sUrlString = "MaterialRequisReservatDlg.page?WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) + 
                   "&LINE_ITEM_NO=" + mgr.URLEncode(headset.getRow().getValue("LINE_ITEM_NO")) + 
                   "&FRMNAME=" + mgr.URLEncode("maintMatReq") +
                   "&QRYSTR=" + mgr.URLEncode("MaintMaterialReqLineOvw.page") +
                   "&PART_NO=" + mgr.URLEncode(headset.getValue("PART_NO")) +
                   "&CONTRACT=" + mgr.URLEncode(headset.getValue("SPARE_CONTRACT")) +
                   "&DESCRIPTION=" + trans.getValue("GETMCHDESC/DATA/DUMMY_FLD2") +
                   "&CONDITION_CODE=" + mgr.URLEncode(headset.getRow().getValue("CONDITION_CODE")) +
                   "&CONDITION_CODE_DESC=" + trans.getValue("GETCONDESC/DATA/DUMMY_FLD3") +
                   "&QTYLEFT=" + mgr.URLEncode(new Double(nQuantityLeft).toString()) +
                   "&MAINTMATORDNO=" + mgr.URLEncode(headset.getValue("MAINT_MATERIAL_ORDER_NO")) +
                   "&CONDITION_CODE=" + mgr.URLEncode(mgr.isEmpty(headset.getValue("CONDITION_CODE"))? "" :headset.getValue("CONDITION_CODE") ) +
                   "&CONDITION_CODE_DESC=" + trans.getValue("GETCONDESC/DATA/DUMMY_FLD3") +
                   "&OWNERSHIP=" + mgr.URLEncode(headset.getRow().getValue("PART_OWNERSHIP")) +
                   "&OWNER=" + mgr.URLEncode(headset.getRow().getValue("OWNER")) +
                   "&OWNERNAME=" + mgr.URLEncode(headset.getRow().getValue("OWNER_NAME")) ;
      bOpenNewWindow = true;
      sNewWinHandle = "manReserve";
      headset.goTo(curr);
   }

   public void unissue()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      int curr = headset.getCurrentRowNo();

      bOpenNewWindow = true;

      sUrlString = "ActiveWorkOrder.page?WO_NO=" + mgr.URLEncode(headset.getRow().getFieldValue("WO_NO")) + 
                   "&MAINTMATORDNO=" + mgr.URLEncode(headset.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO")) + 
                   "&FRMNAME=" + mgr.URLEncode("maintMatReq") +
                   "&QRYSTR=" + mgr.URLEncode("MaintMaterialReqLineOvw.page") +
                   "&PART_NO=" + mgr.URLEncode(headset.getRow().getValue("PART_NO")) +
                   "&CONTRACT=" + mgr.URLEncode(headset.getRow().getValue("SPARE_CONTRACT"))+
                   "&LINE_ITEM_NO="+ mgr.URLEncode(headset.getRow().getValue("LINE_ITEM_NO"));

      sNewWinHandle = "matReqUnissue"; 

      headset.goTo(curr);
   }

   public void unreserve()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      int curr = headset.getCurrentRowNo();

      double qty_assign = headset.getRow().getNumberValue("QTY_ASSIGNED");
      bOpenNewWindow = true;

      cmd = trans.addCustomFunction("GETMCHCODE","WORK_ORDER_API.Get_Mch_Code","DUMMY_FLD1");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      cmd = trans.addCustomFunction("GETMCHDESC","Maintenance_Object_API.Get_Mch_Name","DUMMY_FLD2");
      cmd.addParameter("WO_CONTRACT",headset.getRow().getValue("WO_CONTRACT"));
      cmd.addReference("DUMMY_FLD1","GETMCHCODE/DATA");

      trans = mgr.perform(trans);

      sUrlString = "MaterialRequisReservatDlg2.page?WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) + 
                   "&LINE_ITEM_NO=" + mgr.URLEncode(headset.getValue("LINE_ITEM_NO")) + 
                   "&FRMNAME=" + mgr.URLEncode("maintMatReq") +
                   "&QRYSTR=" + mgr.URLEncode("MaintMaterialReqLineOvw.page") +
                   "&PART_NO=" + mgr.URLEncode(headset.getValue("PART_NO")) +
                   "&CONTRACT=" + mgr.URLEncode(headset.getValue("SPARE_CONTRACT")) +
                   "&DESCRIPTION=" + trans.getValue("GETMCHDESC/DATA/DUMMY_FLD2") +
                   "&QTYLEFT=" + mgr.URLEncode(String.valueOf(qty_assign)) +
                   "&MAINTMATORDNO=" + mgr.URLEncode(headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

      sNewWinHandle = "unreserve";
      headset.goTo(curr);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("MAINT_MATERIAL_ORDER_NO","Number").
      setMandatory().
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEMAINTMATERIALORDERNO: Mat Req Order No").
      setSize(30);

      headblk.addField("MATERIAL_REQ_ORDER_NO").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEREQORDNO: Requisition Site").
      setFunction("MAINT_MATERIAL_REQUISITION_API.Get_Req_Site(:MAINT_MATERIAL_ORDER_NO)").
      setSize(40);

      headblk.addField("WO_NO","Number","#").
      setInsertable().
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEWONO: WO No").
      setSize(30);

      headblk.addField("WO_CONTRACT").
      setLabel("PCMWMAINTMATERIALREQLINEWOCONTRACT: WO Site").
      setReadOnly().
      setFunction("WORK_ORDER_API.Get_Contract(:WO_NO)").
      setSize(8);

      headblk.addField("WO_MCH_CODE").
      setLabel("PCMWMAINTMATERIALREQLINEMCHCODE: Object ID").
      setReadOnly().
      setFunction("WORK_ORDER_API.GET_MCH_CODE(:WO_NO)").
      setSize(50);

      headblk.addField("WO_MCH_CODE_CONTRACT").
      setLabel("PCMWMAINTMATERIALREQLINEMCHSITE: Object Site").
      setReadOnly().
      setFunction("WORK_ORDER_API.Get_Mch_Code_Contract(:WO_NO)").
      setSize(8);

      headblk.addField("WO_ERR_DESC").
      setLabel("PCMWMAINTMATERIALREQLINEWOERRDESC: Directive").
      setSize(60).
      setFunction("WORK_ORDER_API.GET_ERR_DESCR(:WO_NO)").
      setReadOnly();

      headblk.addField("STATUS").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINESTATUS: Status").
      setFunction("MAINT_MATERIAL_REQUISITION_API.Get_Obj_State(:MAINT_MATERIAL_ORDER_NO)").
      setSize(40);

      headblk.addField("LINE_ITEM_NO","Number").
      setMandatory().
      setReadOnly().          
      setLabel("PCMWMAINTMATERIALREQLINELINEITEMNO: Line No").
      setSize(30);

      headblk.addField("ORG_CODE").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEORGCODE: Maint. Org.").
      setSize(30).
      setFunction("ACTIVE_SEPARATE_API.Get_Org_Code(:WO_NO)");

      headblk.addField("SIGNATURE").
      setLabel("PCMWMAINTMATERIALREQLINESIGNATURE: Signature").
      setSize(10).
      setReadOnly().
      setFunction("MAINT_MATERIAL_REQUISITION_API.Get_Signature(:MAINT_MATERIAL_ORDER_NO)");

      headblk.addField("ROLE_CODE").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEROLECODE: Craft").
      setSize(15).
      setFunction("WORK_ORDER_ROLE_API.Get_Role_Code(:WO_NO,:CRAFT_LINE_NO)");

      headblk.addField("PART_NO").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEPARTNO: Part No").
      setSize(25);

      headblk.addField("PART_DESC").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEPARTDESC: Part Description").
      setSize(40).
      setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");

      headblk.addField("CONDITION_CODE").
      setReadOnly().
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINECONDITIONCODE: Condition Code").
      setSize(25);

      headblk.addField("CONDITION_DESC").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINECONDITIONDESC: Condition Code Description").
      setSize(40).
      setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

      headblk.addField("PART_OWNERSHIP").
      enumerateValues("Part_Ownership_API").
      setSelectBox().
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINEPARTOWNERSHIP: Ownership").
      setSize(20);

      headblk.addField("OWNER").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEOWNER: Owner").
      setSize(20);

      headblk.addField("OWNER_NAME").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEOWNERNAME: Owner Name").
      setSize(40).
      setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

      headblk.addField("SPARE_CONTRACT").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINESPARECONTRACT: Site").
      setSize(5);

      headblk.addField("HAS_SPARE_STRUCT").
      setReadOnly().
      setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO, :SPARE_CONTRACT)").
      setSize(10).
      setLabel("PCMWMAINTMATERIALREQLINEHASSPARE: Structure");

      headblk.addField("JOB_ID","Number").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEJOBID: Job ID").
      setSize(20);

      headblk.addField("CRAFT_LINE_NO","Number").
      setReadOnly().
      setSize(15).
      setLabel("PCMWMAINTMATERIALREQLINECRAFTLINENO: Operation No");

      headblk.addField("DIM_QTY").
      setReadOnly().
      setSize(15).
      setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)").
      setLabel("PCMWMAINTMATERIALREQLINEDIMQTY: Dimension/Quality");

      headblk.addField("PRICE_DESIG").
      setReadOnly().
      setSize(25).
      setLabel("PCMWMAINTMATERIALREQLINEPRICEDESIG: Type Designation").
      setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");

      headblk.addField("DATE_REQUIRED","Date").
      setReadOnly().
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINEDATEREQUIRED: Date Required").
      setSize(30);

      headblk.addField("PLAN_QTY","Number").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINEPLANQTY: Quantity Required").
      setSize(30);

      headblk.addField("QTY","Number").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINEQTY: Quantity Issued").
      setSize(30);

      headblk.addField("QTY_SHORT","Number").
      setInsertable().
      setHidden().
      setLabel("PCMWMAINTMATERIALREQLINEQTYSHORT: Quantity Short").
      setSize(30);

      headblk.addField("QTY_ONHAND","Number").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEQTYONHAND: Quantity on Hand").
      setFunction("0").
      setSize(10);

      headblk.addField("QTY_AVAILABLE","Number").
      setReadOnly().
      setFunction("MAINT_MATERIAL_REQ_LINE_API.Get_Inventory_Quantity_Onhand(:SPARE_CONTRACT, :PART_NO, :PART_OWNERSHIP_DB, :OWNER, :CONDITION_CODE,:SUPPLY_CODE_DB)-MAINT_MATERIAL_REQ_LINE_API.Get_Inventory_Qty_Reserved(:SPARE_CONTRACT, :PART_NO, :PART_OWNERSHIP_DB, :OWNER, :CONDITION_CODE,:SUPPLY_CODE_DB)").
      setSize(15).
      setLabel("PCMWMAINTMATERIALREQLINEQTYAVAILABLE: Quantity Available");

      headblk.addField("QTY_ASSIGNED","Number").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINEQTYASSIGNED: Quantity Assigned").
      setSize(30);

      headblk.addField("QTY_RETURNED","Number").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINEQTYRETURNED: Quantity Returned").
      setSize(30);

      headblk.addField("UNIT_MEAS").
      setReadOnly().
      setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT, :PART_NO)").
      setLabel("PCMWMAINTMATERIALREQLINEMEASUNIT: Unit").
      setSize(7);

      headblk.addField("CATALOG_CONTRACT").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINECATALOGCONTRACT: Sales Part Site").
      setSize(5);

      headblk.addField("CATALOG_NO").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINECATALOGNO: Sales Part Number").
      setSize(25);

      headblk.addField("CATALOG_DESC").
      setLabel("PCMWMAINTMATERIALREQLINECATALOGDESC: Sales Part Description").
      setReadOnly().
      setSize(40).
      setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");

      headblk.addField("PRICE_LIST_NO").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINEPRICELISTNO: Price List No").
      setSize(10);

      headblk.addField("LIST_PRICE","Money").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINELISTPRICE: Sales Price").
      setSize(30);

      headblk.addField("DISCOUNT","Number").
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINEDISCOUNT: Discount %").
      setSize(30);

      headblk.addField("DISCOUNT_PRICE","Money").
      setSize(20).
      setLabel("PCMWMAINTMATERIALREQLINEDISCOUNTPRICE: Discounted Price").
      setFunction("''").
      setReadOnly();

      headblk.addField("SALES_PRICE_AMOUNT","Money").
      setSize(20).
      setLabel("PCMWMAINTMATERIALREQLINESALESPRICEAMT: Price Amount").
      setFunction("''").
      setReadOnly();

      headblk.addField("COST","Money").
      setSize(20).
      setLabel("PCMWMAINTMATERIALREQLINECOST: Cost").
      setReadOnly();

      headblk.addField("AMOUNT_COST","Money").
      setSize(15).
      setFunction(":COST*:PLAN_QTY").
      setReadOnly().
      setLabel("PCMWMAINTMATERIALREQLINEAMOUNTCOST: Cost Amount");

      headblk.addField("SUPPLY_CODE").
      enumerateValues("Maint_Mat_Req_Sup_API").
      setSelectBox().
      setMandatory().
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINESUPPLYCODE: Supply Code").
      setSize(30);

      headblk.addField("SALES_PRICE_GROUP_ID").
      setHidden().
      setFunction("SALES_PART_API.Get_Sales_Price_Group_Id(:CATALOG_CONTRACT, :CATALOG_NO)");

      headblk.addField("CURRENCY_CODE").
      setHidden().
      setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:WO_NO)");

      headblk.addField("PLAN_LINE_NO","Number").
      setInsertable().
      setHidden().
      setLabel("PCMWMAINTMATERIALREQLINEPLANLINENO: Plan Line No").
      setSize(15);

      headblk.addField("REQUISITION_NO").
      setHidden();

      headblk.addField("LINE_NO").
      setInsertable().
      setHidden().
      setLabel("PCMWMAINTMATERIALREQLINELINENO: Line No").
      setSize(4);

      headblk.addField("RELEASE_NO").
      setInsertable().
      setHidden().
      setLabel("PCMWMAINTMATERIALREQLINERELEASENO: Release No").
      setSize(4);

      headblk.addField("SALE_UNIT_PRICE","Number").
      setMandatory().
      setHidden().
      setInsertable().
      setLabel("PCMWMAINTMATERIALREQLINESALEUNITPRICE: Sale Unit Price").
      setSize(30);

      headblk.addField("SUPPLY_CODE_DB").
      setHidden();

      headblk.addField("PART_OWNERSHIP_DB").
      setLabel("PCMWMAINTMATERIALREQLINEPARTOWNERSHIPDB: Part ownership db").
      setHidden().
      setSize(30);

      headblk.addField("ACTIVITY_SEQ","Number").
      setLabel("PCMWMAINTMATERIALREQLINEACTIVITYSEQ: Activity Seq").
      setFunction("MAINT_MATERIAL_REQUISITION_API.Get_Activity_seq(:MAINT_MATERIAL_ORDER_NO)").
      setSize(30);

      headblk.addField("CONTRACT").setHidden().setFunction("MAINT_MATERIAL_REQUISITION_API.Get_Req_Site(:MAINT_MATERIAL_ORDER_NO)");
      headblk.addField("PRICE_CONV_FACTOR","Number").setHidden().setFunction("0");
      headblk.addField("CONV_FACTOR","Number").setHidden().setFunction("0");
      headblk.addField("DUMMY_FLD1").setHidden().setFunction("''");
      headblk.addField("DUMMY_FLD2").setHidden().setFunction("''");
      headblk.addField("DUMMY_FLD3").setHidden().setFunction("''");
      headblk.addField("DUMMY_FLD4").setHidden().setFunction("''");
      headblk.addField("DUMMY_FLD5").setHidden().setFunction("''");
      headblk.addField("DUMMY_NUM1").setHidden().setFunction("''");
      headblk.addField("DUMMY_NUM2").setHidden().setFunction("''");
      headblk.addField("QTY_LEFT").setFunction("''").setHidden();
      headblk.addField("LOCATION_NO").setFunction("''").setHidden();
      headblk.addField("LOT_BATCH_NO").setFunction("''").setHidden();
      headblk.addField("SERIAL_NO").setFunction("''").setHidden();
      headblk.addField("ENG_CHG_LEVEL").setFunction("''").setHidden();
      headblk.addField("WAIV_DEV_REJ_NO").setFunction("''").setHidden();
      headblk.addField("PROJECT_ID").setFunction("''").setHidden();
      headblk.addField("QTY_TO_ASSIGN").setFunction("''").setHidden();
      headblk.addField("QTYONHAND").setFunction("''").setHidden();
      headblk.addField("CONFIGURATION").setFunction("''").setHidden();
      headblk.addField("QTY_TYPE").setFunction("''").setHidden();
      headblk.addField("EXPIRATION").setFunction("''").setHidden();
      headblk.addField("AUTO_REPAIRABLE").setFunction("''").setHidden();
      headblk.addField("REPAIRABLE").setFunction("''").setHidden();
      headblk.addField("QTYRES").setFunction("''").setHidden();
      headblk.addField("QTYPLANNABLE").setFunction("''").setHidden();
      headblk.addField("QTY_TO_ISSUE").setFunction("''").setHidden();
      headblk.addField("ISS_ALLO").setFunction("''").setHidden();
      headblk.addField("SERIAL_TRACK").setFunction("''").setHidden();
      headblk.addField("DB_STATE").setFunction("''").setHidden();
      headblk.addField("SUPPLY_CONTROL").setFunction("''").setHidden();
      headblk.addField("OWNERSHIP_TYPE2").setFunction("''").setHidden();
      headblk.addField("OWNERSHIP_TYPE3").setFunction("''").setHidden();
      headblk.addField("OWNERSHIP_TYPE4").setFunction("''").setHidden();
      headblk.addField("OWNER_VENDOR").setFunction("''").setHidden();
      headblk.addField("LOCATION_TYPE1").setFunction("''").setHidden();
      headblk.addField("LOCATION_TYPE2").setFunction("''").setHidden();
      headblk.addField("LOCATION_TYPE3").setFunction("''").setHidden();
      headblk.addField("LOCATION_TYPE4").setFunction("''").setHidden();
      headblk.addField("LOCATION_TYPE5").setFunction("''").setHidden();
      headblk.addField("LOCATION_TYPE6").setFunction("''").setHidden();
      headblk.addField("LOCATION_TYPE7").setFunction("''").setHidden();
      headblk.addField("LOCATION_TYPE8").setFunction("''").setHidden();
      headblk.addField("INCLUDE_STANDARD").setFunction("''").setHidden();
      headblk.addField("INCLUDE_PROJECT").setFunction("''").setHidden();
      headblk.addField("ORDER_ISSUE").setFunction("''").setHidden();
      headblk.addField("MANUAL_RESERV").setFunction("''").setHidden();
      headblk.addField("AUTOMAT_RESERV").setFunction("''").setHidden();
      headblk.addField("ATTR0").setFunction("''").setHidden();
      headblk.addField("ATTR1").setFunction("''").setHidden();
      headblk.addField("ATTR2").setFunction("''").setHidden();
      headblk.addField("ATTR3").setFunction("''").setHidden();
      headblk.addField("ATTR4").setFunction("''").setHidden();
      headblk.addField("RESULT_KEY").setFunction("''").setHidden();
      headblk.addField("DUMMY_ACT_QTY_ISSUED","Number").setFunction("0").setHidden();


      headblk.setView("MAINT_MATERIAL_REQ_LINE");
      headblk.defineCommand("MAINT_MATERIAL_REQ_LINE_API","");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("PCMWMAINTMATERIALREQLINETBLHEAD: Maint Material Req Lines");
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      headbar.addCustomCommand("maintMatReq",mgr.translate("PCMWMAINTMATERIALREQLINEOVWMATREQ: Maintenance Material Requisition..."));
      headbar.addSecureCustomCommand("reserve",mgr.translate("PCMWMAINTMATERIALREQLINEOVWRESER: Reserve"),"MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Short"); 
      headbar.addCustomCommand("reserveManually",mgr.translate("PCMWMAINTMATERIALREQLINEOVWRESERMANUAL: Reserve Manually..."));
      headbar.addCustomCommand("unreserve",mgr.translate("PCMWMAINTMATERIALREQLINEOVWUNRESER: Unreserve..."));
      headbar.addSecureCustomCommand("issue",mgr.translate("PCMWMAINTMATERIALREQLINEOVWISSUE: Issue"),"MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail");
      headbar.addSecureCustomCommand("issueManually",mgr.translate("PCMWMAINTMATERIALREQLINEOVWISSUEMANUAL: Issue manually..."),"MAINT_MATERIAL_REQ_LINE_API.Make_Manual_Issue_Detail");
      headbar.addCustomCommand("unissue",mgr.translate("PCMWMAINTMATERIALREQLINEOVWUNISSUE: Unissue..."));
      headbar.addCustomCommand("printPickList",mgr.translate("PCMWMAINTMATERIALREQLINEOVWPICKLIST: Pick List For Maint Material Req - Printout..."));

      headbar.removeFromMultirowAction("maintMatReq");
      headbar.removeFromMultirowAction("reserve");
      headbar.removeFromMultirowAction("reserveManually");
      headbar.removeFromMultirowAction("unreserve");
      headbar.removeFromMultirowAction("issue");
      headbar.removeFromMultirowAction("issueManually");
      headbar.removeFromMultirowAction("unissue");
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      if (headset.countRows() > 0) {
         boolean bReserve = true;
         boolean bManualReserve = true;
         boolean bUnReserve = true;
         boolean bIssue = true;
         boolean bManualIssue = true;
         boolean bUnIssue = true;

         if (headlay.isMultirowLayout()) {
            headset.goTo(headset.getRowSelected());
         }

         double dPlanQty = (mgr.isEmpty(headset.getValue("PLAN_QTY"))) ? 0:Double.parseDouble(headset.getValue("PLAN_QTY"));
         double dQty = (mgr.isEmpty(headset.getValue("QTY"))) ? 0:Double.parseDouble(headset.getValue("QTY"));
         double dQtyAssigned = (mgr.isEmpty(headset.getValue("QTY_ASSIGNED"))) ? 0:Double.parseDouble(headset.getValue("QTY_ASSIGNED"));

         if (dPlanQty == 0 && dQty == 0 && dQtyAssigned == 0) {
            bReserve = false;
            bManualReserve = false;
         }

         if (dPlanQty <= dQty + dQtyAssigned ) {
            bReserve = false;
            bManualReserve = false;
         }

         if (dQtyAssigned <= 0)
            bUnReserve = false;

         if (dPlanQty - dQty <= 0) {
            bIssue = false;
            bManualIssue = false;
         }

         if (dQty <= 0)
            bUnIssue = false;

         trans.clear();
         cmd = trans.addCustomFunction("GETSTATUS","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DUMMY_FLD1");
         cmd.addParameter("DUMMY_FLD1","Released");

         cmd = trans.addCustomFunction("GETOBJSTATUS","MAINT_MATERIAL_REQUISITION_API.Get_Obj_State","DUMMY_FLD2");
         cmd.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getValue("MAINT_MATERIAL_ORDER_NO"));

         cmd = trans.addCustomFunction("GETRESALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Reservation_Allowed","DUMMY_FLD3");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         cmd = trans.addCustomFunction("GETISSSTATE","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","DUMMY_FLD4");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         cmd = trans.addCustomFunction("GETWOSTATUS","Active_Separate_API.Get_Obj_State","DUMMY_FLD5");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         String sStatus = trans.getValue("GETSTATUS/DATA/DUMMY_FLD1");
         String sObjStatus = trans.getValue("GETOBJSTATUS/DATA/DUMMY_FLD2");
         String sResAllowed = trans.getValue("GETRESALLOW/DATA/DUMMY_FLD3");
         String sIssAllowed = trans.getValue("GETISSSTATE/DATA/DUMMY_FLD4");
         String sWoStatus = trans.getValue("GETWOSTATUS/DATA/DUMMY_FLD5");

         if (!(!mgr.isEmpty(sStatus) && sStatus.equals(sObjStatus) && "TRUE".equals(sResAllowed))) {
            bReserve = false;
            bManualReserve = false;
         }

         if (!(!mgr.isEmpty(sStatus) && sStatus.equals(sObjStatus) && "TRUE".equals(sIssAllowed))) {
            bIssue = false;
            bManualIssue = false;
         }

         if (!("WORKDONE".equals(sWoStatus) || "REPORTED".equals(sWoStatus) || "STARTED".equals(sWoStatus) || "RELEASED".equals(sWoStatus)))
            bUnIssue = false;

         if (!bReserve)
            headbar.removeCustomCommand("reserve");
         if (!bManualReserve)
            headbar.removeCustomCommand("reserveManually");
         if (!bUnReserve)
            headbar.removeCustomCommand("unreserve");
         if (!bIssue)
            headbar.removeCustomCommand("issue");
         if (!bManualIssue)
            headbar.removeCustomCommand("issueManually");
         if (!bUnIssue)
            headbar.removeCustomCommand("unissue");
      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "PCMWMAINTMATERIALREQLINEOVWDESC: Overview - Maintenance Material Requisition Lines";
   }


   protected String getTitle()
   {
      return "PCMWMAINTMATERIALREQLINEOVWTITLE: Overview - Maintenance Material Requisition Lines";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
         appendToHTML(headlay.show());

      printHiddenField("REFRESH_FLAG","");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sOpenNewWindow));
      appendDirtyJavaScript("' == 'TRUE')\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sNewWindowName));
      appendDirtyJavaScript("' == 'MaintMaterialRequisition.page')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       jMaintMatOrdNo = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sMaintMatOrdNo));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("       window.open(\"MaintMaterialRequisition.page?SEARCH=Y&MAINT_MATERIAL_ORDER_NO=\"+URLClientEncode(jMaintMatOrdNo)," +
                            "\"mainFrame\",\"alwaysRaised=yes,resizable," +
                            "status=yes,scrollbars=yes,width=800,height=500\");   \n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sOpenCreRepNonSer));
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.CREREPNONSER.value == \"TRUE\")\n");
      appendDirtyJavaScript("      window.open('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sCreRepNonSerPath));
      appendDirtyJavaScript("',\"createRepWONonSer\",\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=790,height=575\");\n");
      appendDirtyJavaScript("   document.form.CREREPNONSER.value = \"FALSE\";\n");
      appendDirtyJavaScript("}\n");

      if (bOpenNewWindow) {
         appendDirtyJavaScript("  window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sUrlString));
         appendDirtyJavaScript("\", \"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sNewWinHandle));
         appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
      }

      appendDirtyJavaScript("function refresh()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   f.REFRESH_FLAG.value = 'TRUE';\n");
      appendDirtyJavaScript("   f.submit();\n");
      appendDirtyJavaScript("}\n");
   }
}
