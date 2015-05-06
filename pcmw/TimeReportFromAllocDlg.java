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
 *  File        : TimeReportFromAllocDlg.java 
 *  Created     : THWILK  051019  Created
 *  Modified    :
 *  AMNILK    060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
 *  AMDILK    060807  Merged with Bug Id 58214
 *  AMDILK    060811  Bug 58216, Eliminated SQL errors in web applications. Modified methods okFind(), okFindITEM()
 *  AMDILK    060904  Merged with the Bug Id 58216
 *  SHAFLK    070312  Bug 64068, Removed extra mgr.translate().   
 *  AMDILK    070330  Merged bug id 64068
 *  CHANLK    070522  Field values not fetched properly when load.modified addToReportInWO().
 *  AMNILK    070727  Eliminated SQLInjections Security Vulnerability.
 *  ASSALK    070917  call 148864. Modified preDefine().
 *  ASSALK    070919  call 148987. Modified preDefine(), printContents().
 *  ASSALK    070920  quick correction to call 148987. Modified okFind(), printContents(). 
 * ----------------------------------------------------------------------------
 */

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class TimeReportFromAllocDlg extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.TimeReportFromAllocDlg");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPCommand cmd;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPCommandBar itembar;
   private ASPTable itemtbl;
   private ASPBlockLayout itemlay;
   private ASPQuery q;

   private ASPBlock itemblk1;
   private ASPRowSet itemset1;
   private ASPCommandBar itembar1;
   private ASPTable itemtbl1;
   private ASPBlockLayout itemlay1;

   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean saveCloseFlag;
   private boolean closeFlag;
   private boolean fieldState;
   private String woNo;
   private String frameName;
   private String qryStr;
   private String txt;
   private String attr;
   private String sDlgName;
   private String sContract;
   private int nRowCount;

   //===============================================================
   // Construction 
   //===============================================================
   public TimeReportFromAllocDlg(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      woNo = ctx.readValue("WO_NO", "");
      qryStr = ctx.readValue("QRYSTR", "");
      frameName = ctx.readValue("FRMNAME", "");
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
         woNo = mgr.readValue("WO_NO");
         qryStr = mgr.readValue("QRYSTR", "");
         frameName = mgr.readValue("FRMNAME", "");
         okFind();
         okFindITEM();
      }

      else if (mgr.buttonPressed("OK"))
         submit();
      else if (mgr.buttonPressed("REPOK"))
         reportInSubmit();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else
         trans.clear();

      ctx.writeValue("WO_NO", woNo);
      ctx.writeValue("QRYSTR", qryStr);
      ctx.writeValue("FRMNAME", frameName);
   }

   //-----------------------------------------------------------------------------
   //-----------------------------  VALIDATE FUNCTION  ---------------------------
   //-----------------------------------------------------------------------------

   public void validate()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer secBuff;
      String val = mgr.readValue("VALIDATE");

      if ("SIGN".equals(val))
      {
         cmd = trans.addCustomFunction("SIGNID", "Employee_API.Get_Max_Maint_Emp", "SIGN_ID");
         cmd.addParameter("COMPANY", mgr.readValue("COMPANY"));
         cmd.addParameter("SIGNATURE", mgr.readValue("SIGN"));

         trans = mgr.validate(trans);
         String emp_id = trans.getValue("SIGNID/DATA/SIGN_ID");
         txt = (mgr.isEmpty(emp_id) ? "" : (emp_id)) + "^";
         mgr.responseWrite(txt);
      } else if ("EMP_NO".equals(val))
      {
         boolean securityOk = false;
         String strCatalogNo = null;
         String sDefRole = "";
         String sDefOrg = mgr.readValue("ITEM1_ORG_CODE");

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff = mgr.perform(secBuff);

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[4];
         String emp_id = "";
         String emp_sign = "";
         String site = "";
         String new_sign = mgr.readValue("EMP_NO", "");

         if (new_sign.indexOf("^", 0) > 0)
         {
            for (i = 0; i < 4; i++)
            {
               endpos = new_sign.indexOf("^", startpos);
               reqstr = new_sign.substring(startpos, endpos);
               ar[i] = reqstr;
               startpos = endpos + 1;
            }
            emp_sign = ar[0];
            emp_id = ar[1];
            site = ar[3];

         } else
         {
            emp_id = mgr.readValue("EMP_NO");
            ;
            emp_sign = mgr.readValue("EMP_SIGNATURE");
            site = mgr.readValue("ITEM1_CONTRACT");
         }
         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            securityOk = true;
         cmd = trans.addCustomFunction("PARA1", "Employee_Role_API.Get_Default_Role", "ROLE_CODE");
         cmd.addParameter("ITEM1_COMPANY", mgr.readValue("ITEM1_COMPANY", ""));
         cmd.addParameter("EMP_NO", emp_id);

         cmd = trans.addCustomFunction("PARA2", "Role_API.Get_Description", "CMNT");
         cmd.addReference("ROLE_CODE", "PARA1/DATA");

         cmd = trans.addCustomFunction("PARA3", "Employee_API.Get_Organization", "ITEM1_ORG_CODE");
         cmd.addParameter("ITEM1_COMPANY");
         cmd.addParameter("EMP_NO", emp_id);

         cmd = trans.addCustomFunction("PARA4", "Company_Emp_API.Get_Person_Id", "EMP_SIGNATURE");
         cmd.addParameter("ITEM1_COMPANY");
         cmd.addParameter("EMP_NO", emp_id);

         cmd = trans.addCustomFunction("PARA5", "Person_Info_API.Get_Name", "NAME");
         cmd.addReference("EMP_SIGNATURE", "PARA4/DATA");

         if (securityOk)
         {
            cmd = trans.addCustomFunction("CATALO", "Role_Sales_Part_API.Get_Def_Catalog_No", "CATALOG_NO");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addReference("ROLE_CODE", "PARA1/DATA");

         }

         trans = mgr.validate(trans);

         if (securityOk)
         {
            strCatalogNo = trans.getValue("CATALO/DATA/CATALOG_NO");

         }

         String strRoleCode = trans.getValue("PARA1/DATA/ROLE_CODE");
         if (mgr.isEmpty(strRoleCode))
            strRoleCode = sDefRole;

         String strDesc = trans.getValue("PARA2/DATA/CMNT");
         String strOrgCode = trans.getValue("PARA3/DATA/ORG_CODE");
         if (mgr.isEmpty(strOrgCode))
            strOrgCode = sDefOrg;

         String strSig = trans.getValue("PARA4/DATA/EMP_SIGNATURE");
         String strName = trans.getValue("PARA5/DATA/NAME");
         String strConcatDesc = (mgr.isEmpty(strName) ? "" : (strName + ", ")) + (mgr.isEmpty(strOrgCode) ? "" : strOrgCode) + ",(" + strDesc + ")";

                    txt = (mgr.isEmpty(emp_id)? "" : emp_id) +"^"+
                          (mgr.isEmpty(strRoleCode)? "" : strRoleCode) +"^"+ 
                          (mgr.isEmpty(strSig)? "" : strSig) +"^"+ 
                          (mgr.isEmpty(strName)? "" : strName) +"^"+ 
                          (mgr.isEmpty(strOrgCode)? "" : strOrgCode) +"^"+ 
                          (mgr.isEmpty(site)? "" : site) +"^"+ 
                          (mgr.isEmpty(strConcatDesc)? "" : strConcatDesc) +"^"+ 
                          (mgr.isEmpty(strCatalogNo)? "" : strCatalogNo) +"^";

         mgr.responseWrite(txt);
      } else if ("PLAN_LINE_NO".equals(val))
      {
         cmd = trans.addCustomFunction("PARA10", "Work_Order_Role_API.Get_Plan_Row_No", "CRAFT_ROW");
         cmd.addParameter("ITEM1_WO_NO");
         cmd.addParameter("PLAN_LINE_NO");

         trans = mgr.validate(trans);

         String strCraftRow = trans.getValue("PARA10/DATA/CRAFT_ROW");

         txt = (mgr.isEmpty(strCraftRow) ? "" : strCraftRow) + "^";

         mgr.responseWrite(txt);
      } else if ("ITEM1_ORG_CODE".equals(val))
      {
         double colAmount = 0;
         String strColAmount = null;
         String colOrgCode;
         String colCmnt = null;
         String colSalesPart = null;
         boolean securityOk = false;
         double colSalesPartCost = 0;
         String strCost = null;

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String sOrgCode = "";
         String sOrgContract = "";
         String new_org_code = mgr.readValue("ITEM1_ORG_CODE", "");

         if (new_org_code.indexOf("^", 0) > 0)
         {
            if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            {
               for (i = 0; i < 5; i++)
               {
                  endpos = new_org_code.indexOf("^", startpos);
                  reqstr = new_org_code.substring(startpos, endpos);
                  ar[i] = reqstr;
                  startpos = endpos + 1;
               }
               sOrgCode = ar[3];
               sOrgContract = ar[4];
            } else
            {
               if (!mgr.isEmpty(mgr.readValue("EMP_NO")))
               {
                  for (i = 0; i < 4; i++)
                  {
                     endpos = new_org_code.indexOf("^", startpos);
                     reqstr = new_org_code.substring(startpos, endpos);
                     ar[i] = reqstr;
                     startpos = endpos + 1;
                  }
                  sOrgCode = ar[2];
                  sOrgContract = ar[3];
               } else
               {
                  for (i = 0; i < 2; i++)
                  {
                     endpos = new_org_code.indexOf("^", startpos);
                     reqstr = new_org_code.substring(startpos, endpos);
                     ar[i] = reqstr;
                     startpos = endpos + 1;
                  }
                  sOrgCode = ar[0];
                  sOrgContract = ar[1];
               }
            }
         } else
         {
            sOrgCode = mgr.readValue("ITEM1_ORG_CODE");
            sOrgContract = mgr.readValue("ITEM1_CONTRACT");

         }

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            securityOk = true;

         if (securityOk && mgr.isEmpty(mgr.readValue("ROLE_CODE")))
         {
            cmd = trans.addCustomFunction("DEFCAT", "Organization_Sales_Part_API.Get_Def_Catalog_No", "CATALOG_NO");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_ORG_CODE", sOrgCode);

         } else
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            {
               cmd = trans.addCustomCommand("CALAMOUNT", "Work_Order_Coding_API.Calc_Amount");
               cmd.addParameter("AMOUNT");
               cmd.addParameter("QTY");
               cmd.addParameter("ITEM1_ORG_CODE", sOrgCode);
               cmd.addParameter("ROLE_CODE");
               cmd.addParameter("ITEM1_CONTRACT");
            }

            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")))
            {
               cmd = trans.addCustomFunction("ROLEDESC", "Role_API.Get_Description", "CMNT");
               cmd.addParameter("ROLE_CODE");
            } else
            {
               cmd = trans.addCustomFunction("GETORGCODE", "Active_Work_Order_API.Get_Org_Code", "ORCODE");
               cmd.addParameter("ITEM1_WO_NO");
            }
         }

         cmd = trans.addCustomFunction("GETCOST", "Work_Order_Planning_Util_Api.Get_Cost", "COST3");
         cmd.addParameter("ITEM1_ORG_CODE", sOrgCode);
         cmd.addParameter("ROLE_CODE");
         cmd.addParameter("ITEM1_CONTRACT");
         cmd.addParameter("CATALOG_NO");
         cmd.addParameter("ITEM1_CONTRACT");

         trans = mgr.validate(trans);

         if (securityOk && mgr.isEmpty(mgr.readValue("ROLE_CODE")))
         {
            colSalesPart = trans.getValue("DEFCAT/DATA/CATALOG_NO");
            colOrgCode = mgr.readValue("ITEM1_ORG_CODE", "");
            colCmnt = mgr.readValue("CMNT", "");
            colAmount = mgr.readNumberValue("AMOUNT");
         } else
         {
            colSalesPart = mgr.readValue("CATALOG_NO", "");

            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
               colAmount = trans.getBuffer("CALAMOUNT/DATA").getNumberValue("AMOUNT");

            else if (!mgr.isEmpty(mgr.readValue("QTY")))
            {
               if (mgr.isEmpty(mgr.readValue("AMOUNT")))
                  colAmount = mgr.readNumberValue("AMOUNT");
               else
                  colAmount = mgr.readNumberValue("AMOUNT");
            }

            else if (mgr.isEmpty(mgr.readValue("QTY")))
               colAmount = 0;

            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")))
            {
               String sEmpName = mgr.readValue("NAME", "");
               colOrgCode = mgr.readValue("ITEM1_ORG_CODE", "");
               colCmnt = (mgr.isEmpty(sEmpName) ? "" : sEmpName) + ", " + mgr.readValue("ITEM1_ORG_CODE") + ", (" + trans.getValue("ROLEDESC/DATA/CMNT") + ")";
            } else
            {
               String sEmpName = mgr.readValue("NAME", "");
               colOrgCode = trans.getValue("GETORGCODE/DATA/ORCODE");
               colCmnt = (mgr.isEmpty(sEmpName) ? "" : sEmpName) + ", " + colOrgCode;
            }
         }

         colSalesPartCost = trans.getNumberValue("GETCOST/DATA/COST3");

         if (!isNaN(colAmount))
            strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);

         if (!isNaN(colSalesPartCost))
            strCost = mgr.getASPField("AMOUNT").formatNumber(colSalesPartCost);

        
                   txt = (mgr.isEmpty(sOrgCode)?"":sOrgCode) +"^"+
                         (mgr.isEmpty(sOrgContract)?"":sOrgContract) +"^"+
                         (mgr.isEmpty(colCmnt)?"":colCmnt) +"^"+ 
                         (mgr.isEmpty(strColAmount)?"":strColAmount) +"^"+ 
                         (mgr.isEmpty(colSalesPart)?"":colSalesPart) +"^"+ 
                         (mgr.isEmpty(strCost)?"":strCost) +"^" ;

         mgr.responseWrite(txt);
      } else if ("ITEM1_CONTRACT".equals(val))
      {
         cmd = trans.addCustomFunction("COMP", "Site_API.Get_Company", "COMPANY");
         cmd.addParameter("ITEM1_CONTRACT");

         trans = mgr.validate(trans);

         String strCompany = trans.getValue("COMP/DATA/COMPANY");
         txt = (mgr.isEmpty(strCompany) ? "" : strCompany) + "^";
         mgr.responseWrite(txt);
      } else if ("CATALOG_NO".equals(val))
      {
         double nBuyQtyDue;
         double colListPrice;
         double colDiscount;
         double colAmountSales;
         double nSalesPriceAmount;
         double colSalesPartCost;
         double colAmount;
         double numSalesPartCost;
         String strColAmountSales = null;
         String strColListPrice = null;
         String strColAmount = null;
         String colPriceListNo = null;
         String strColSalesPartCost = null;
         String colCmnt = null;
         boolean securityOk = false;
         boolean salesSecOk = false;

         numSalesPartCost = 0;

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff.addSecurityQuery("Company_Finance_API", "Get_Currency_Code");
         secBuff.addSecurityQuery("Customer_Order_Pricing_API", "Get_Valid_Price_List,Get_Sales_Part_Price_Info");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            salesSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code")
               && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
            securityOk = true;

         if (securityOk)
         {
            if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
               nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
            else
               nBuyQtyDue = mgr.readNumberValue("QTY");

            if (isNaN(nBuyQtyDue) || nBuyQtyDue == 0)
               nBuyQtyDue = 1;

            cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE");
            cmd.addParameter("SALE_PRICE");
            cmd.addParameter("DISCOUNT");
            cmd.addParameter("CURRENCY_RATE");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("PRICE_LIST_NO");
            cmd.addParameter("QTY1", mgr.formatNumber("QTY1", nBuyQtyDue));
         }

         cmd = trans.addCustomFunction("ROLEDESC", "Role_API.Get_Description", "CMNT");
         cmd.addParameter("ROLE_CODE");

         if (salesSecOk)
         {

            cmd = trans.addCustomFunction("PARTCOST", "Sales_Part_API.Get_Cost", "COST1");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");

            cmd = trans.addCustomFunction("ROLECOST", "Role_API.Get_Role_Cost", "COST2");
            cmd.addParameter("ROLE_CODE");

            cmd = trans.addCustomFunction("ORGCOST", "Organization_API.Get_Org_Cost", "COST3");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_ORG_CODE");
         } else
         {
            cmd = trans.addCustomFunction("ROLECOST", "Role_API.Get_Role_Cost", "COST2");
            cmd.addParameter("ROLE_CODE");

            cmd = trans.addCustomFunction("ORGCOST", "Organization_API.Get_Org_Cost", "COST3");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_ORG_CODE");
         }

         trans = mgr.validate(trans);

         if (securityOk)
         {
            colListPrice = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
            if (isNaN(colListPrice))
               colListPrice = 0;

            colPriceListNo = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");

            colDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if (isNaN(colDiscount))
               colDiscount = 0;

            double qty1 = trans.getNumberValue("PRICEINFO/DATA/QTY1");
            if (isNaN(qty1))
               qty1 = 0;

            colAmountSales = colListPrice * qty1;

            if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
            {
               double qtyToInvoice = mgr.readNumberValue("QTY_TO_INVOICE");
               if (isNaN(qtyToInvoice))
                  qtyToInvoice = 0;

               nSalesPriceAmount = colListPrice * qtyToInvoice;
               colAmountSales = nSalesPriceAmount - (colDiscount / 100 * nSalesPriceAmount);
            } else
            {
               if (!mgr.isEmpty(mgr.readValue("QTY")))
               {
                  double qty = mgr.readNumberValue("QTY");
                  if (isNaN(qty))
                     qty = 0;

                  nSalesPriceAmount = colListPrice * qty;
                  colAmountSales = nSalesPriceAmount - (colDiscount / 100 * nSalesPriceAmount);
               } else
                  colAmountSales = 0;
            }
         } else
         {
            colListPrice = mgr.readNumberValue("LIST_PRICE");
            colAmountSales = mgr.readNumberValue("AMOUNTSALES");

            colPriceListNo = mgr.readValue("PRICE_LIST_NO", "");
         }

         String sComments = trans.getValue("ROLEDESC/DATA/CMNT");
         colCmnt = (mgr.isEmpty(mgr.readValue("NAME", "")) ? "" : (mgr.readValue("NAME", "") + ",")) + mgr.readValue("ITEM1_ORG_CODE", "") + (mgr.isEmpty(sComments) ? "" : (",(" + sComments + ")"));

         colSalesPartCost = 0;
         if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
         {
            colSalesPartCost = trans.getBuffer("ROLECOST/DATA").getNumberValue("COST2");
            numSalesPartCost = trans.getNumberValue("ROLECOST/DATA/COST2");
         }
         if ((colSalesPartCost == 0) && !mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")))
         {
            colSalesPartCost = trans.getBuffer("ORGCOST/DATA").getNumberValue("COST3");
            numSalesPartCost = trans.getNumberValue("ORGCOST/DATA/COST3");
         }

         if ((colSalesPartCost == 0) && !mgr.isEmpty(mgr.readValue("CATALOG_NO")) && salesSecOk)
         {
            colSalesPartCost = trans.getBuffer("PARTCOST/DATA").getNumberValue("COST1");
            numSalesPartCost = trans.getNumberValue("PARTCOST/DATA/COST1");
         }

         if (mgr.isEmpty(mgr.readValue("QTY")))
            colAmount = 0;
         else
            colAmount = numSalesPartCost * mgr.readNumberValue("QTY");

         if (!isNaN(colListPrice))
            strColListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
         if (!isNaN(colAmount))
            strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);
         if (!isNaN(colAmountSales))
            strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);

         txt = (mgr.isEmpty(strColListPrice) ? "" : strColListPrice) + "^" + (mgr.isEmpty(strColAmount) ? "" : strColAmount) + "^" + (mgr.isEmpty(strColAmountSales) ? "" : strColAmountSales) + "^"
               + (mgr.isEmpty(colPriceListNo) ? "" : colPriceListNo) + "^" + (mgr.isEmpty(colCmnt) ? "" : colCmnt) + "^" + (mgr.isEmpty(strColSalesPartCost) ? "" : strColSalesPartCost) + "^";

         mgr.responseWrite(txt);
      }

      else if ("PRICE_LIST_NO".equals(val))
      {
         double nBuyQtyDue;
         double colListPrice;
         double colDiscount;
         double colAmountSales;
         double nSalesPriceAmount;
         double listPrice;
         String strColAmountSales = null;
         String strColListPrice = null;
         String colPriceListNo;
         String colcAgreement;
         boolean securityOk = false;
         boolean salesSecOk = false;

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff.addSecurityQuery("Company_Finance_API", "Get_Currency_Code");
         secBuff.addSecurityQuery("Customer_Order_Pricing_API", "Get_Valid_Price_List,Get_Sales_Part_Price_Info");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            salesSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code")
               && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
            securityOk = true;

         if (securityOk)
         {
            if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
               nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
            else
               nBuyQtyDue = mgr.readNumberValue("QTY");

            if (isNaN(nBuyQtyDue) || nBuyQtyDue == 0)
               nBuyQtyDue = 1;

            cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE");
            cmd.addParameter("SALE_PRICE");
            cmd.addParameter("DISCOUNT");
            cmd.addParameter("CURRENCY_RATE");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("PRICE_LIST_NO");
            cmd.addParameter("QTY1", mgr.formatNumber("QTY1", nBuyQtyDue));
         }

         trans = mgr.validate(trans);

         if (securityOk)
         {
            colListPrice = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
            if (isNaN(colListPrice))
               colListPrice = 0;

            colPriceListNo = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");

            colDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if (isNaN(colDiscount))
               colDiscount = 0;

            double qty1 = trans.getNumberValue("PRICEINFO/DATA/QTY1");
            if (isNaN(qty1))
               qty1 = 0;

            colAmountSales = colListPrice * qty1;

            if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
            {
               double qtyToInvoice = mgr.readNumberValue("QTY_TO_INVOICE");
               if (isNaN(qtyToInvoice))
                  qtyToInvoice = 0;

               nSalesPriceAmount = colListPrice * qtyToInvoice;
               colAmountSales = nSalesPriceAmount - (colDiscount / 100 * nSalesPriceAmount);
            } else
            {
               if (!mgr.isEmpty(mgr.readValue("QTY")))
               {
                  double qty = mgr.readNumberValue("QTY");
                  if (isNaN(qty))
                     qty = 0;

                  nSalesPriceAmount = colListPrice * qty;
                  colAmountSales = nSalesPriceAmount - (colDiscount / 100 * nSalesPriceAmount);
               } else
                  colAmountSales = 0;
            }
         } else
         {
            colListPrice = mgr.readNumberValue("LIST_PRICE");
            colAmountSales = mgr.readNumberValue("AMOUNTSALES");

            colPriceListNo = mgr.readValue("PRICE_LIST_NO", "");
         }

         colcAgreement = "1";

         listPrice = mgr.readNumberValue("LIST_PRICE");

         if (!isNaN(colListPrice))
            strColListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
         if (!isNaN(colAmountSales))
            strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);

         txt = (mgr.isEmpty(strColListPrice) ? "" : strColListPrice) + "^" + (mgr.isEmpty(strColAmountSales) ? "" : strColAmountSales) + "^" + (mgr.isEmpty(colPriceListNo) ? "" : colPriceListNo)
               + "^" + colcAgreement + "^";

         mgr.responseWrite(txt);
      } else if ("QTY".equals(val))
      {
         txt = null;
         double colAmount = 0;
         double nBuyQtyDue;
         double colSalesPartCost;
         double colAmountSales;
         double colListPrice;
         double colDiscount = mgr.readNumberValue("DISCOUNT");
         if (isNaN(colDiscount))
            colDiscount = 0;

         double nSalesPriceAmount;
         String strColAmount = null;
         String strColAmountSales = null;
         String strColListPrice = null;
         String strColSalesPartCost = null;
         String colPriceListNo;
         boolean securityOk = false;
         boolean salesSecOk = false;
         boolean roleSecOk = false;
         boolean isError = false;
         double colDiscountPrice = 0;
         String strDisplayDiscount = "";

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff.addSecurityQuery("ROLE_SALES_PART");
         secBuff.addSecurityQuery("Company_Finance_API", "Get_Currency_Code");
         secBuff.addSecurityQuery("Customer_Order_Pricing_API", "Get_Valid_Price_List,Get_Sales_Part_Price_Info");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            salesSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("ROLE_SALES_PART"))
            roleSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code")
               && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
            securityOk = true;

         if (salesSecOk)
         {
            cmd = trans.addCustomFunction("PARTCOST", "Sales_Part_API.Get_Cost", "COST1");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");

            cmd = trans.addCustomFunction("ROLECOST", "Role_API.Get_Role_Cost", "COST2");
            cmd.addParameter("ROLE_CODE");

            cmd = trans.addCustomFunction("ORGCOST", "Organization_API.Get_Org_Cost", "COST3");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_ORG_CODE");
         } else
         {
            cmd = trans.addCustomFunction("ROLECOST", "Role_API.Get_Role_Cost", "COST2");
            cmd.addParameter("ROLE_CODE");

            cmd = trans.addCustomFunction("ORGCOST", "Organization_API.Get_Org_Cost", "COST3");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_ORG_CODE");
         }

         if (!mgr.isEmpty(mgr.readValue("QTY")))
         {
            if (roleSecOk && !mgr.isEmpty(mgr.readValue("CATALOG_NO")))
            {
               double colQty;

               colSalesPartCost = mgr.readNumberValue("SALESPARTCOST");
               if (isNaN(colSalesPartCost))
                  colSalesPartCost = 0;

               colQty = mgr.readNumberValue("QTY");
               if (isNaN(colQty))
                  colQty = 0;

               colAmount = colSalesPartCost * colQty;
            } else
            {
               cmd = trans.addCustomCommand("CALAMOUNT", "Work_Order_Coding_API.Calc_Amount");
               cmd.addParameter("AMOUNT");
               cmd.addParameter("QTY");
               cmd.addParameter("ITEM1_ORG_CODE");
               cmd.addParameter("ROLE_CODE");
               cmd.addParameter("ITEM1_CONTRACT");
            }
         }

         if (securityOk)
         {
            nBuyQtyDue = mgr.readNumberValue("QTY");
            if (isNaN(nBuyQtyDue) || (nBuyQtyDue == 0))
               nBuyQtyDue = 1;

            cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE", "0");
            cmd.addParameter("SALE_PRICE", "0");
            cmd.addParameter("DISCOUNT", "0");
            cmd.addParameter("CURRENCY_RATE", "0");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("PRICE_LIST_NO");
            cmd.addParameter("QTY1", mgr.formatNumber("QTY1", nBuyQtyDue));
         }

         trans = mgr.validate(trans);

         colSalesPartCost = 0;

         if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            colSalesPartCost = trans.getBuffer("ROLECOST/DATA").getNumberValue("COST2");

         if ((isNaN(colSalesPartCost) || colSalesPartCost == 0) && !mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")))
            colSalesPartCost = trans.getBuffer("ORGCOST/DATA").getNumberValue("COST3");

         if ((isNaN(colSalesPartCost) || colSalesPartCost == 0) && !mgr.isEmpty(mgr.readValue("CATALOG_NO")) && salesSecOk)
            colSalesPartCost = trans.getBuffer("PARTCOST/DATA").getNumberValue("COST1");

         if (isNaN(colSalesPartCost))
            colSalesPartCost = 0;

         if (!mgr.isEmpty(mgr.readValue("QTY")) && (mgr.readNumberValue("QTY") <= 24) && (!roleSecOk || mgr.isEmpty(mgr.readValue("CATALOG_NO"))))
            colAmount = trans.getBuffer("CALAMOUNT/DATA").getNumberValue("AMOUNT");

         if (isNaN(colAmount))
            colAmount = 0;

         if (securityOk)
         {
            colListPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE");
            if (isNaN(colListPrice))
               colListPrice = 0;

            colDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if (isNaN(colDiscount))
               colDiscount = 0;

            double qty = mgr.readNumberValue("QTY");
            if (isNaN(qty))
               qty = 0;

            colDiscountPrice = colListPrice - (colDiscount / 100 * colListPrice);
            colAmountSales = colDiscountPrice * qty;
         } else
         {
            colListPrice = mgr.readNumberValue("LIST_PRICE");
            colAmountSales = mgr.readNumberValue("AMOUNTSALES");
            colPriceListNo = mgr.readValue("PRICE_LIST_NO", "");
         }

         if (!isError)
         {
            if (!isNaN(colListPrice))
               strColListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
            if (!isNaN(colAmountSales))
               strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
            if (!isNaN(colSalesPartCost))
               strColSalesPartCost = mgr.getASPField("SALESPARTCOST").formatNumber(colSalesPartCost);
            if (!isNaN(colAmount))
               strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);
            if (!isNaN(colDiscount))
               strDisplayDiscount = mgr.getASPField("DISCOUNT").formatNumber(colDiscount);

                        txt = (mgr.isEmpty(strColListPrice)?"":strColListPrice) +"^"+
                              (mgr.isEmpty(strColAmountSales)?"":strColAmountSales) +"^"+
                              (mgr.isEmpty(strColSalesPartCost)?"":strColSalesPartCost) +"^"+
                              (mgr.isEmpty(strColAmount)?"":strColAmount)+"^"+
                              (mgr.isEmpty(strDisplayDiscount)?"":strDisplayDiscount) +"^"+"^"; 
         }

         mgr.responseWrite(txt);
      } else if ("ROLE_CODE".equals(val))
      {
         double colAmount;
         double colSalesPartCost;
         double colQty;
         double nBuyQtyDue;
         double colListPrice;

         double colDiscount = mgr.readNumberValue("DISCOUNT");
         if (isNaN(colDiscount))
            colDiscount = 0;

         double colAmountSales;
         double nSalesPriceAmount;
         String strColAmount = null;
         String strColAmountSales = null;
         String strColListPrice = null;
         String colPriceListNo;
         String colCmnt = null;
         String colCatalogNo = null;
         boolean securityOk = false;
         boolean salesSecOk = false;
         double colDiscountPrice = 0;
         String strDisplayDiscount = null;

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String sRoleCode = "";
         String sOrgContract = "";
         String new_role_code = mgr.readValue("ROLE_CODE", "");

         if (new_role_code.indexOf("^", 0) > 0)
         {
            if (!mgr.isEmpty(mgr.readValue("EMP_NO")))
            {
               for (i = 0; i < 5; i++)
               {
                  endpos = new_role_code.indexOf("^", startpos);
                  reqstr = new_role_code.substring(startpos, endpos);
                  ar[i] = reqstr;
                  startpos = endpos + 1;
               }
               sRoleCode = ar[2];
               sOrgContract = ar[4];
            } else
            {
               if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")))
               {
                  for (i = 0; i < 5; i++)
                  {
                     endpos = new_role_code.indexOf("^", startpos);
                     reqstr = new_role_code.substring(startpos, endpos);
                     ar[i] = reqstr;
                     startpos = endpos + 1;
                  }
                  sRoleCode = ar[2];
                  sOrgContract = ar[4];
               } else
               {
                  for (i = 0; i < 2; i++)
                  {
                     endpos = new_role_code.indexOf("^", startpos);
                     reqstr = new_role_code.substring(startpos, endpos);
                     ar[i] = reqstr;
                     startpos = endpos + 1;
                  }
                  sRoleCode = ar[0];
                  sOrgContract = ar[1];
               }
            }
         } else
         {
            sRoleCode = mgr.readValue("ROLE_CODE");
            ;
            sOrgContract = mgr.readValue("ITEM1_CONTRACT");
         }

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff.addSecurityQuery("Company_Finance_API", "Get_Currency_Code");
         secBuff.addSecurityQuery("Customer_Order_Pricing_API", "Get_Valid_Price_List,Get_Sales_Part_Price_Info");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            salesSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code")
               && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
            securityOk = true;

         if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
         {
            cmd = trans.addCustomFunction("ROLEDESC", "Role_API.Get_Description", "CMNT");
            cmd.addParameter("ROLE_CODE", sRoleCode);

            if (salesSecOk)
            {
               cmd = trans.addCustomFunction("DEFCATNO", "Role_Sales_Part_API.Get_Def_Catalog_No", "CATALOG_NO");
               cmd.addParameter("ITEM1_CONTRACT");
               cmd.addParameter("ROLE_CODE", sRoleCode);

            } else
            {
               if (!mgr.isEmpty(mgr.readValue("CATALOG_NO")))
               {
                  if (mgr.isEmpty(mgr.readValue("QTY")))
                     colAmount = 0;
                  else
                  {
                     colSalesPartCost = mgr.readNumberValue("SALESPARTCOST");
                     if (isNaN(colSalesPartCost))
                        colSalesPartCost = 0;

                     colQty = mgr.readNumberValue("QTY");
                     if (isNaN(colQty))
                        colQty = 0;

                     colAmount = colSalesPartCost * colQty;
                  }
               }
            }
         }

         if (securityOk)
         {
            if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
               nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
            else
               nBuyQtyDue = mgr.readNumberValue("QTY");

            if (isNaN(nBuyQtyDue) || nBuyQtyDue == 0)
               nBuyQtyDue = 1;

            cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE", "0");
            cmd.addParameter("SALE_PRICE", "0");
            cmd.addParameter("DISCOUNT", "0");
            cmd.addParameter("CURRENCY_RATE", "0");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addReference("CATALOG_NO", "DEFCATNO/DATA");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("PRICE_LIST_NO");
            cmd.addParameter("QTY1", mgr.formatNumber("QTY1", nBuyQtyDue));
         }

         if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
         {
            cmd = trans.addCustomCommand("CALAMOUNT", "Work_Order_Coding_API.Calc_Amount");
            cmd.addParameter("AMOUNT");
            cmd.addParameter("QTY");
            cmd.addParameter("ITEM1_ORG_CODE");
            cmd.addParameter("ROLE_CODE", sRoleCode);
            cmd.addParameter("ITEM1_CONTRACT");
         }

         colAmount = mgr.readNumberValue("AMOUNT");
         if (isNaN(colAmount))
            colAmount = 0;

         trans = mgr.validate(trans);

         if (securityOk)
         {
            colListPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE");
            if (isNaN(colListPrice))
               colListPrice = 0;

            colDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if (isNaN(colDiscount))
               colDiscount = 0;

            double qtyInv = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(qtyInv))
               qtyInv = 0;

            colDiscountPrice = colListPrice - (colDiscount / 100 * colListPrice);
            colAmountSales = colDiscountPrice * qtyInv;

         } else
         {
            colListPrice = mgr.readNumberValue("LIST_PRICE");
            colAmountSales = mgr.readNumberValue("AMOUNTSALES");

            colPriceListNo = mgr.readValue("PRICE_LIST_NO", "");
         }

         if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            colAmount = trans.getBuffer("CALAMOUNT/DATA").getNumberValue("AMOUNT");

         if (!mgr.isEmpty(mgr.readValue("ROLE_CODE", sRoleCode)))
         {
            String sComment = trans.getValue("ROLEDESC/DATA/CMNT");
            colCmnt = (mgr.isEmpty(mgr.readValue("NAME", "")) ? "" : (mgr.readValue("NAME", "") + ", ")) + mgr.readValue("ITEM1_ORG_CODE", "") + (mgr.isEmpty(sComment) ? "" : (",(" + sComment + ")"));

            if (salesSecOk)
            {
               colCatalogNo = trans.getValue("DEFCATNO/DATA/CATALOG_NO");

            } else
            {
               colCatalogNo = mgr.readValue("CATALOG_NO", "");

            }
         } else
         {
            colCatalogNo = mgr.readValue("CATALOG_NO", "");
            colCmnt = (mgr.isEmpty(mgr.readValue("NAME", "")) ? "" : (mgr.readValue("NAME", "") + ", ")) + mgr.readValue("ITEM1_ORG_CODE", "");
         }

         if (!isNaN(colListPrice))
            strColListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
         if (!isNaN(colAmount))
            strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);
         if (!isNaN(colAmountSales))
            strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
         if (!isNaN(colDiscount))
            strDisplayDiscount = mgr.getASPField("DISCOUNT").formatNumber(colDiscount);

                    txt = (mgr.isEmpty(sRoleCode)?"":sRoleCode) +"^"+ 
                          (mgr.isEmpty(sOrgContract)?"":sOrgContract) +"^"+
                          (mgr.isEmpty(strColListPrice)?"":strColListPrice) +"^"+ 
                          (mgr.isEmpty(strColAmount)?"":strColAmount) +"^"+ 
                          (mgr.isEmpty(strColAmountSales)?"":strColAmountSales) +"^"+
                          (mgr.isEmpty(colCmnt)?"":colCmnt) +"^"+
                          (mgr.isEmpty(colCatalogNo)?"":colCatalogNo) +"^"+"^";

         mgr.responseWrite(txt);
      }

      mgr.endResponse();
   }

   //-----------------------------------------------------------------------------
   //-------------------------  SEARCH FUNCTIONS  ---------------------------------
   //------------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addEmptyQuery(headblk);
      q.setSelectList("DISTINCT WO_NO,ROW_NO,WORK_ORDER_ROLE_API.Get_Description(WO_NO,ROW_NO) DESCRIPTION,ACTIVE_SEPARATE_API.Get_Contract(WO_NO) CONTRACT_HEAD,Site_API.Get_Company(ACTIVE_SEPARATE_API.Get_Contract(WO_NO)) COMPANY_HEAD");
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO", woNo);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
      eval(headset.syncItemSets());

      nRowCount = headset.countRows();

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWTIMEREPORTFROMALLOCDLGNODATA: No data found."));
         headset.clear();
      }
      else
         sContract = headset.getValue("CONTRACT_HEAD");
   }

   public void okFindITEM()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addQuery(itemblk);
      q.includeMeta("ALL");
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO", woNo);

      mgr.submit(trans);
   }

   //-----------------------------------------------------------------------------
   //-------------------------  USER DEFINED FUNCTIONS  ---------------------------
   //-----------------------------------------------------------------------------

   public void submit()
   {
      ASPManager mgr = getASPManager();
      sDlgName = "ReportInWo";
      int count, currentRow;
      String rowString = "";
      attr = "";
      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         count = headset.countSelectedRows();

      } else
      {
         headset.unselectRows();
         headset.selectRow();
         count = 1;
      }
      if (count > 0)
      {

         if (headlay.isMultirowLayout())
         {
            headset.first();
            for (int i = 0; i < count; i++)
            {
               if (i == 0)
                  rowString = headset.getValue("ROW_NO");
               else
                  rowString = rowString + "^" + headset.getValue("ROW_NO");
               if (headlay.isMultirowLayout())
                  headset.next();
            }
         } else
            rowString = headset.getRow().getValue("ROW_NO");

         rowString = rowString + "^";

         if (headlay.isMultirowLayout())
            headset.setFilterOff();

         cmd = trans.addCustomFunction("GETROWS", "Work_Order_Role_API.Get_Row_List", "ROW_LIST");
         cmd.addParameter("WO_NO", woNo);
         cmd.addParameter("NULL", rowString);
         cmd.addParameter("SIGN_ID");
         trans = mgr.perform(trans);

         String strAttr = trans.getValue("GETROWS/DATA/ROW_LIST");
         if (!mgr.isEmpty(strAttr))
         {
            trans.clear();
            String rowList[] = split(strAttr, "^");
            int noOfRows = rowList.length - 1;
            for (int i = 0; i < noOfRows; ++i)
            {
               cmd = trans.addCustomFunction("GETATTR", "Work_Order_Role_API.Create_Attr_From_Allocation", "ROW_LIST");
               cmd.addParameter("WO_NO", woNo);
               cmd.addParameter("NULL", rowList[i]);
               trans = mgr.perform(trans);
               attr = trans.getValue("GETATTR/DATA/ROW_LIST");
               addToReportInWO(attr);
               trans.clear();
            }
         } else
         {
            sDlgName = "";
            closeFlag = true;
         }
      } else
      {
         sDlgName = "";
         closeFlag = true;
      }
   }

   public void addToReportInWO(String attr)
   {
      ASPManager mgr = getASPManager();
      String fieldTitle, fieldValue, strVal;
      trans.clear();

      cmd = trans.addCustomFunction("ORGCO", "Active_Work_Order_API.Get_Org_Code", "ITEM1_ORG_CODE");
      cmd.addParameter("ITEM1_WO_NO", headset.getValue("WO_NO"));

      cmd = trans.addCustomFunction("CON", "Active_Work_Order_API.Get_Contract", "ITEM1_CONTRACT");
      cmd.addParameter("ITEM1_WO_NO", headset.getValue("WO_NO"));

      cmd = trans.addCustomFunction("CST", "Organization_API.Get_Org_Cost", "SALESPARTCOST");
      cmd.addReference("ITEM1_CONTRACT", "CON/DATA");
      cmd.addReference("ITEM1_ORG_CODE", "ORGCO/DATA");

      cmd = trans.addCustomFunction("SP21", "Work_Order_Cost_Type_Api.Get_Client_Value(0)", "WORK_ORDER_COST_TYPE");
      cmd = trans.addCustomFunction("SP22", "Work_Order_Account_Type_API.Get_Client_Value(0)", "WORK_ORDER_ACCOUNT_TYPE");

      cmd = trans.addQuery("SYSDATE", "SELECT SYSDATE CRE_DATE FROM DUAL");

      cmd = trans.addEmptyCommand("ITEM1", "WORK_ORDER_CODING_API.New__", itemblk1);
      cmd.setOption("ACTION", "PREPARE");

      trans = mgr.perform(trans);

      String strOrgCode = trans.getValue("ORGCO/DATA/ORG_CODE");
      String strSite = trans.getValue("CON/DATA/CONTRACT");
      String strComment = strOrgCode;
      double cost = trans.getNumberValue("CST/DATA/SALESPARTCOST");
      String strWorkOrdCost = trans.getValue("SP21/DATA/WORK_ORDER_COST_TYPE");
      String strWorkAccount = trans.getValue("SP22/DATA/WORK_ORDER_ACCOUNT_TYPE");
      String sysDate = trans.getBuffer("SYSDATE/DATA").getFieldValue("CRE_DATE");

      ASPBuffer data = trans.getBuffer("ITEM1/DATA");
      data.setFieldItem("ITEM1_WO_NO", headset.getValue("WO_NO"));
      data.setFieldItem("ITEM1_CONTRACT", strSite);
      data.setFieldItem("ITEM1_ORG_CODE", strOrgCode);
      data.setFieldItem("WORK_ORDER_COST_TYPE", strWorkOrdCost);
      data.setFieldItem("WORK_ORDER_ACCOUNT_TYPE", strWorkAccount);
      data.setFieldItem("CMNT", strComment);
      data.setFieldItem("CMNT", strComment);
      data.setNumberValue("SALESPARTCOST", cost);
      data.setFieldItem("CRE_DATE", sysDate);

      String rowList[] = split(attr, new Character((char) 30).toString());
      int noOfSets = rowList.length - 1;

      String empNo, operNo, comp;

      empNo = null;
      operNo = null;
      comp = null;

      for (int i = 0; i < noOfSets; ++i)
      {
         strVal = rowList[i];
         String itemList[] = split(strVal, new Character((char) 31).toString());
         fieldTitle = itemList[0];
         fieldValue = itemList[1];

         if (fieldTitle.equals("COMPANY"))
            comp = fieldValue;
         else if (fieldTitle.equals("EMP_NO"))
            empNo = fieldValue;
         else if (fieldTitle.equals("PLAN_LINE_NO"))
            operNo = fieldValue;

         if ((empNo != null) && (operNo != null) && (comp != null))
         {
            trans.clear();
            cmd = trans.addCustomFunction("SIGN", "Company_Emp_API.Get_Person_Id", "EMP_SIGNATURE");
            cmd.addParameter("ITEM1_COMPANY", comp);
            cmd.addParameter("EMP_NO", empNo);

            cmd = trans.addCustomFunction("CRAFT", "Work_Order_Role_API.Get_Plan_Row_No", "CRAFT_ROW");
            cmd.addParameter("ITEM1_WO_NO", headset.getValue("WO_NO"));
            cmd.addParameter("PLAN_LINE_NO", operNo);

            trans = mgr.perform(trans);
            empNo = null;
            operNo = null;
            comp = null;
            data.setValue("EMP_SIGNATURE", trans.getValue("SIGN/DATA/EMP_SIGNATURE"));
            data.setNumberValue("CRAFT_ROW", trans.getNumberValue("CRAFT/DATA/CRAFT_ROW"));
         }
         if (data.itemExists(fieldTitle))
            data.setValue(fieldTitle, fieldValue);
         else
            data.addItem(fieldTitle, fieldValue);
      }
      itemset1.addRow(data);
   }

   public void reportInSubmit()
   {
      ASPManager mgr = getASPManager();
      checkMandatory();
      if (!fieldState)
      {
         mgr.submit(trans);
         saveCloseFlag = true;
      }

      sDlgName = "ReportInWo";
   }

   public void checkMandatory()
   {
      ASPManager mgr = getASPManager();
      int n = itemset1.countRows();
      itemset1.changeRows();
      if (n > 0)
      {
         itemset1.first();
         for (int i = 0; i < n; ++i)
         {
            if (mgr.isEmpty(itemset1.getValue("ORG_CODE")))
            {
               mgr.showAlert(mgr.translate("PCMWTIMEREPORTFROMALLOCDLG1: Required value for field [Maintenance Organization]."));
               fieldState = true;
               break;
            } else if (mgr.isEmpty(itemset1.getValue("CONTRACT")))
            {
               mgr.showAlert(mgr.translate("PCMWTIMEREPORTFROMALLOCDLG2: Required value for field [Maint. Org Site]."));
               fieldState = true;
               break;
            } else if (mgr.isEmpty(itemset1.getValue("QTY")))
            {
               mgr.showAlert(mgr.translate("PCMWTIMEREPORTFROMALLOCDLG3: Required value for field [Hours]."));
               fieldState = true;
               break;
            }
            itemset1.next();
         }
      }
   }

   public void deleteItem()
   {
      ASPManager mgr = getASPManager();

      if (itemlay1.isMultirowLayout())
         itemset1.goTo(itemset1.getRowSelected());
      itemset1.unselectRows();
      itemset1.selectRow();
      itemset1.setRemoved();
      if (itemset1.countRows() > 0)
         itemset1.refreshAllRows();
      sDlgName = "ReportInWo";

   }

   //-----------------------------------------------------------------------------
   //------------------------  PREDEFINE------------------------------------------
   //-----------------------------------------------------------------------------

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

        headblk.addField("WO_NO","Number","##").
        setHidden();
        
        headblk.addField("ROW_NO","Number").
        setLabel("PCMWTIMEREPORTFROMALLOCDLGROWNO: Operation No").
        setReadOnly();

        headblk.addField("DESCRIPTION").
        setFunction("WORK_ORDER_ROLE_API.Get_Description(:WO_NO,:ROW_NO)"). 
        setLabel("PCMWTIMEREPORTFROMALLOCDLGROWDESC: Description").
        setReadOnly().
        setSize(60);

        headblk.addField("CONTRACT_HEAD").setFunction("ACTIVE_SEPARATE_API.Get_Contract(:WO_NO)").setHidden();
        headblk.addField("COMPANY_HEAD").setFunction("Site_API.Get_Company(ACTIVE_SEPARATE_API.Get_Contract(:WO_NO))").setHidden();

      headblk.setView("WO_ROLE_ALLOCATIONS");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.FORWARD);
      headbar.disableCommand(headbar.BACKWARD);
      headbar.disableCommand(headbar.FIND);

      headtbl = mgr.newASPTable(headblk);
      headtbl.setEditable();
      headtbl.enableRowSelect();
      headbar.enableMultirowAction();
      headlay = headblk.getASPBlockLayout();
      headtbl.disableRowCounter();
      headlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(2);
      headlay.setEditable();

      //-----------------ITEMBLK0-----------------------------

      itemblk = mgr.newASPBlock("ITEM");

        itemblk.addField("ITEM_WO_NO","Number").
        setDbName("WO_NO").
        setHidden().
        setReadOnly();
        
        itemblk.addField("SIGN_ID").
        setSize(15).
        setLabel("PCMWTIMEREPORTFROMALLOCDLGEMPLOYEEID: Employee ID").
        setUpperCase().
        setMaxLength(11).
        setHidden();

        itemblk.addField("COMPANY").
        setSize(15).
        setUpperCase().
        setHidden();

        itemblk.addField("SIGNATURE").
        setFunction("''").
        setHidden();

        itemblk.addField("NULL").
        setHidden();

        itemblk.addField("ROW_LIST").
        setFunction("''").
        setHidden();

      itemblk.setView("WORK_ORDER_ROLE");
      itemset = itemblk.getASPRowSet();
      itembar = mgr.newASPCommandBar(itemblk);
      itembar.disableCommand(itembar.EDITROW);

      itemtbl = mgr.newASPTable(itemblk);
      itemtbl.setWrap();

      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDefaultLayoutMode(itemlay.EDIT_LAYOUT);
      itemlay.setDialogColumns(2);

      //=====================Time Report=============================

      itemblk1 = mgr.newASPBlock("ITEM1");

      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk1.addField("CRE_DATE", "Date");
      f.setSize(12);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGCREDATE: Creation Date");
      f.setReadOnly();
      f.setInsertable();
      f.setDefaultNotVisible();

      f = itemblk1.addField("ITEM1_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");

      f = itemblk1.addField("EMP_NO");
      f.setSize(10);
      f.setLOV("../mscomw/MaintEmployeeLov.page", "ITEM1_COMPANY COMPANY", 600, 450);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGEMPNO1: Employee ID");
      f.setUpperCase();
      f.setCustomValidation("EMP_NO,ITEM1_COMPANY,CUSTOMER_NO,ITEM1_CONTRACT,CATALOG_NO,ROLE_CODE,ITEM1_ORG_CODE", "EMP_NO,ROLE_CODE,EMP_SIGNATURE,NAME,ITEM1_ORG_CODE,ITEM1_CONTRACT,CMNT,CATALOG_NO");
      f.setMaxLength(11);

      f = itemblk1.addField("EMP_SIGNATURE");
      f.setSize(8);
      f.setReadOnly();
      f.setDynamicLOV("EMPLOYEE_LOV", 600, 445);
      f.setLOVProperty("WHERE","Employee_Id IN (SELECT Sign_Id FROM &AO.WORK_ORDER_ROLE)");
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGEMPSIGNATURE: Signature");
      f.setFunction("Company_Emp_API.Get_Person_Id(ITEM1_COMPANY,EMP_NO)");
      f.setUpperCase();

      f = itemblk1.addField("NAME");
      f.setSize(16);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGNAME: Name");
      f.setFunction("Person_Info_API.Get_Name(:EMP_SIGNATURE)");
      f.setDefaultNotVisible();

      f = itemblk1.addField("PLAN_LINE_NO", "Number");
      f.setSize(8);
      f.setLOV("WorkOrderRolePlanningLovLov.page", "ITEM1_WO_NO WO_NO", 600, 450);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGEMPPLANLINNO: Plan Line No");
      f.setUpperCase();
      f.setCustomValidation("ITEM1_WO_NO,PLAN_LINE_NO", "CRAFT_ROW");

      f = itemblk1.addField("CRAFT_ROW", "Number");
      f.setSize(8);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGCRAFTROW: Operation No");
      f.setFunction("Work_Order_Role_API.Get_Plan_Row_No(ITEM1_WO_NO,PLAN_LINE_NO)");
      f.setReadOnly();

      f = itemblk1.addField("ITEM1_ORG_CODE");
      f.setSize(11);
      f.setLOV("../mscomw/OrgCodeAllowedSiteLov.page", "ITEM1_CONTRACT CONTRACT", 600, 445);
      f.setCustomValidation("ITEM1_ORG_CODE,ITEM1_WO_NO,ITEM1_CONTRACT,AMOUNT,QTY,ROLE_CODE,CATALOG_NO,CMNT,NAME", "ITEM1_ORG_CODE,ITEM1_CONTRACT,AMOUNT,CMNT,CATALOG_NO,SALESPARTCOST");
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGERITEM1ORGCODE: Maintenance Organization");
      f.setMandatory();
      f.setDbName("ORG_CODE");
      f.setUpperCase();
      f.setMaxLength(8);
      f.setLOVProperty("TITLE", mgr.translate("PCMWTIMEREPORTFROMALLOCSLGORCO: List of Maintenance Organization"));

      f = itemblk1.addField("ITEM1_CONTRACT");
      f.setSize(11);
      f.setCustomValidation("ITEM1_CONTRACT", "ITEM1_COMPANY");
      f.setMandatory();
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGITEM1CONTRACT: Maint. Org Site");
      f.setDbName("CONTRACT");
      f.setUpperCase();

      f = itemblk1.addField("ROLE_CODE");
      f.setSize(10);
      f.setLOV("../mscomw/RoleToSiteLov.page", "ITEM1_CONTRACT CONTRACT", 600, 445);
      f.setCustomValidation("CUSTOMER_NO,ITEM1_CONTRACT,CATALOG_NO,PRICE_LIST_NO,AGREEMENT_ID,QTY1,QTY_TO_INVOICE,QTY,ITEM1_ORG_CODE,ROLE_CODE,AMOUNT,CMNT,NAME,SALESPARTCOST,LIST_PRICE",
            "ROLE_CODE,ITEM1_CONTRACT,LIST_PRICE,PRICE_LIST_NO,AMOUNT,AMOUNTSALES,CMNT,CATALOG_NO");
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGROLECODE: Craft");
      f.setUpperCase();
      f.setMaxLength(10);
      f.setLOVProperty("TITLE", mgr.translate("PCMWTIMEREPORTFROMALLOCSLGCRAFT: List of Craft"));

      f = itemblk1.addField("CATALOG_NO");
      f.setSize(17);
      f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM1_CONTRACT CONTRACT", 600, 445);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGCATALOGNO: Sales Part Number");
      f.setUpperCase();
      f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM1_CONTRACT,CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY1,NAME,ITEM1_ORG_CODE,ROLE_CODE",
            "LIST_PRICE,AMOUNT,AMOUNTSALES,PRICE_LIST_NO,CMNT,SALESPARTCOST");
      f.setReadOnly();
      f.setHidden();
      f.setLOVProperty("TITLE", mgr.translate("PCMWTIMEREPORTFROMALLOCSLGCANO: List of Sales Part Number"));

      f = itemblk1.addField("COST1", "Money");
      f.setHidden();
      f.setFunction("''");

      f = itemblk1.addField("COST2", "Money");
      f.setHidden();
      f.setFunction("''");

      f = itemblk1.addField("COST3", "Money");
      f.setHidden();
      f.setFunction("''");

      f = itemblk1.addField("PRICE_LIST_NO");
      f.setSize(13);
      f.setDynamicLOV("SALES_PRICE_LIST", "ITEM1_CONTRACT CONTRACT", 600, 445);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGPRICELISTNO: Price List No");
      f.setUpperCase();
      f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM1_CONTRACT,CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY1,LIST_PRICE", "LIST_PRICE,AMOUNTSALES,PRICE_LIST_NO,AGREEMENT_PRICE_FLAG");
      f.setMaxLength(10);
      f.setLOVProperty("TITLE", mgr.translate("PCMWTIMEREPORTFROMALLOCSLGPRLINO: List of Price List No"));
      f.setHidden();

      f = itemblk1.addField("QTY", "Number");
      f.setSize(11);
      f.setMandatory();
      f.setInsertable();
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGITM1QTY: Hours");
      f.setCustomValidation("ITEM1_CONTRACT,CATALOG_NO,ROLE_CODE,ITEM1_ORG_CODE,QTY,SALESPARTCOST,QTY_TO_INVOICE,CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY,LIST_PRICE",
            "LIST_PRICE,AMOUNTSALES,SALESPARTCOST,AMOUNT,DISCOUNT");

      f = itemblk1.addField("LIST_PRICE", "Money");
      f.setSize(11);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGLISTPRICE: Sales Price");
      f.setCustomValidation("LIST_PRICE", "AGREEMENT_PRICE_FLAG");
      f.setReadOnly();
      f.setHidden();

      f = itemblk1.addField("SALESPARTCOST", "Money");
      f.setSize(11);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGSALESPARTCOST: Cost");
      f.setFunction("WORK_ORDER_PLANNING_UTIL_API.Get_Cost(:ITEM1_ORG_CODE,:ROLE_CODE,:ITEM1_CONTRACT,:CATALOG_NO,:ITEM1_CONTRACT)");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk1.addField("AMOUNT", "Money");
      f.setSize(11);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGAMOUNT: Cost Amount");
      f.setDefaultNotVisible();
      f.setReadOnly();

      f = itemblk1.addField("AMOUNTSALES", "Money");
      f.setSize(17);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGAMOUNTSALES: Price Amount");
      f.setFunction("(LIST_PRICE*QTY)");
      f.setReadOnly();
      f.setHidden();

      f = itemblk1.addField("CMNT");
      f.setCustomValidation("CMNT", "CMNT");
      f.setSize(30);
      f.setHeight(4);
      f.setLabel("PCMWTIMEREPORTFROMALLOCDLGCMNT: Comment");
      f.setDefaultNotVisible();
      f.setMaxLength(80);

      f = itemblk1.addField("ITEM1_WO_NO", "Number", "#");
      f.setMandatory();
      f.setHidden();
      f.setDbName("WO_NO");

      f = itemblk1.addField("ORCODE");
      f.setHidden();
      f.setFunction("''");

      f = itemblk1.addField("AGREEMENT_PRICE_FLAG", "Number");
      f.setHidden();

      f = itemblk1.addField("BASE_PRICE", "Money");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("SALE_PRICE", "Money");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("DISCOUNT", "Number");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("CURRENCY_RATE", "Number");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("QTY1", "Number");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("QTY_TO_INVOICE", "Number");
      f.setHidden();

      f = itemblk1.addField("CUSTOMER_NO");
      f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:ITEM1_WO_NO)");
      f.setHidden();

      f = itemblk1.addField("AGREEMENT_ID");
      f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:ITEM1_WO_NO)");
      f.setHidden();

      f = itemblk1.addField("WORK_ORDER_COST_TYPE");
      f.setMandatory();
      f.setHidden();

      f = itemblk1.addField("WORK_ORDER_ACCOUNT_TYPE");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();

      itemblk1.setView("WORK_ORDER_CODING");
      itemblk1.defineCommand("WORK_ORDER_CODING_API", "New__,Modify__,Remove__");
      itemset1 = itemblk1.getASPRowSet();
      itembar1 = mgr.newASPCommandBar(itemblk1);

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setEditable();
      itemtbl1.setWrap();
      itemtbl1.disableQuickEdit();
      itembar1.disableCommand(itembar1.NEWROW);
      itembar1.disableCommand(itembar1.FIND);
      itembar1.disableCommand(itembar1.EDITROW);
      itembar1.disableCommand(itembar1.DUPLICATEROW);
      itembar1.enableCommand(itembar1.DELETE);
      itembar1.defineCommand(itembar1.DELETE, "deleteItem");
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      itemlay1.setDialogColumns(3);

   }

   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      if ("ReportInWo".equals(sDlgName))
         return "PCMWTIMEREPORTFROMALLOCDLGTITLE1: Report In Work Order";
      else
         return "PCMWTIMEREPORTFROMALLOCDLGTITLE2: Create Time Report From Allocation";
   }

   protected String getTitle()
   {
      if ("ReportInWo".equals(sDlgName))
         return "PCMWTIMEREPORTFROMALLOCDLGTITLE1: Report In Work Order";
      else
         return "PCMWTIMEREPORTFROMALLOCDLGTITLE2: Create Time Report From Allocation";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if ("ReportInWo".equals(sDlgName))
      {
         appendToHTML(itembar1.showBar());
         appendToHTML(itemlay1.generateDataPresentation());
         appendToHTML("<table>\n");
         appendToHTML("<tr><td></td></tr></table>\n");
         appendToHTML("<tr>		\n");
         appendToHTML("       <td align=\"left\"> \n");
         appendToHTML(fmt.drawSubmit("REPOK", mgr.translate("PCMWTIMEREPORTFROMALLOCDLGREPOK:   OK   "), ""));
         appendToHTML("&nbsp;\n");
         appendToHTML(fmt.drawSubmit("REPCANCEL", mgr.translate("PCMWTIMEREPORTFROMALLOCDLGREPCANCEL: Cancel"), "onClick='window.close();'"));
         appendToHTML("&nbsp;\n");
         appendToHTML("</td>\n");
         appendToHTML("</tr>\n");

      } else
      {
         appendToHTML("<table bgcolor=\"white\" width=\"727\" id=\"cntHEAD\" class=\"BlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
         appendToHTML("<tr><td>\n");
         appendToHTML(headlay.generateDataPresentation());
         appendToHTML(itemlay.generateDataPresentation());
         appendToHTML("</td></tr>\n");
         appendToHTML("<!-- table cell for radio buttons    -->\n");
         appendToHTML("<tr><td>\n");
         appendToHTML("<table border=0 width=100%  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
         appendToHTML("	<tr><td>\n");
         appendToHTML("	<table width=100% cellspacing=0 cellpadding=0 class=group>\n");
         appendToHTML("		<tr>\n");
         appendToHTML("			<td width=10 height=\"17\" noWrap vAlign=\"bottom\"><img src=\"../common/images/block_topleft.gif\"></td><td align=left width=25 height=\"17\" noWrap vAlign=\"center\"><td align=left height=\"17\" noWrap vAlign=\"center\"><font class=groupBoxTitleText>Time Scale</font></td><td width=10>&nbsp;</td>		<td width=100% align=\"right\" height=\"17\" noWrap vAlign=\"bottom\" background=\"../common/images/block_topmiddle.gif\"><img src=\"../common/images/table_empty_image.gif\" width=\"0\" height=\"0\"></td><td align=\"right\" vAlign=\"bottom\" width=1><img valign=bottom src=\"../common/images/block_topright.gif\">\n");
         appendToHTML("			</td>\n");
         appendToHTML("		</tr>\n");
         appendToHTML("</table>\n");
         appendToHTML("<table width=100% border=0  class=\"SubBlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
         appendToHTML("<tr>\n");
         appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
         appendToHTML(fmt.drawRadio("PCMWTIMEREPORTFROMALLOCDLGRADIO1: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Signature", "OPTION", "ONE", true, "onClick=handleClickRadio(1);"));
         appendToHTML("</td><td width=10>&nbsp</td>\n");
         appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
         appendToHTML(fmt.drawTextField("SIGN", "", "onChange='assignEmpId();'", 15));
         appendToHTML("<img src=\"../common/images/table_empty_image.gif\" width=\"5\" height=\"1\"><a href=\"javascript:lovSign(1)\"><img src=\"../common/images/lov.gif\" width=20 height=15 border=0 alt=\"List of values\"></a></td>\n");
         appendToHTML("</td><td width=10>&nbsp</td>\n");
         appendToHTML("	<tr>\n");
         appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
         appendToHTML(fmt.drawRadio("PCMWTIMEREPORTFROMALLOCDLGRADIO2: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;All", "OPTION", "ALL", false, "onClick=handleClickRadio(2);"));
         appendToHTML("</td><td width=10>&nbsp</td>\n");
         appendToHTML("</table>	\n");
         appendToHTML("<table  cellpadding=0 cellspacing=0 width=100%>");
         appendToHTML("<tr><td height=9 width=1><img src=\"../common/images/block_bottomleft.gif\"></td><td width=100% height=9 background=\"../common/images/block_bottommiddle.gif\"><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"1\"></td><td height=9 width=1><img src=\"../common/images/block_bottomleft.gif\"></td></tr></table><p>\n");
         appendToHTML("</td></tr>\n");
         appendToHTML("</table>\n");
         appendToHTML("</td></tr>\n");
         appendToHTML("<!-- end of table cell for radio buttons    -->\n");
         appendToHTML("<tr><td>\n");
         appendToHTML("</td></tr>\n");
         appendToHTML("<tr>		\n");
         appendToHTML("       <td align=\"left\"> \n");
         appendToHTML(fmt.drawSubmit("OK", mgr.translate("PCMWTIMEREPORTFROMALLOCDLGOK:   OK   "), ""));
         appendToHTML("&nbsp;\n");
         appendToHTML(fmt.drawSubmit("CANCEL", mgr.translate("PCMWTIMEREPORTFROMALLOCDLGCANCEL: Cancel"), "onClick='window.close();'"));
         appendToHTML("&nbsp;\n");
         appendToHTML(fmt.drawButton("SELECTALL", mgr.translate("PCMWTIMEREPORTFROMALLOCDLGSELECTALL: All"), "onClick=\"selectAllRows('__SELECTED1')\""));
         appendToHTML("&nbsp;\n");
         appendToHTML(fmt.drawButton("DESELECTALL", mgr.translate("PCMWTIMEREPORTFROMALLOCDLGDESELECTALL: None"), "onClick=\"deselectAllRows('__SELECTED1')\""));
         appendToHTML("&nbsp;\n");
         appendToHTML("       </td>   \n");
         appendToHTML("</tr>\n");
         appendToHTML("</table>\n");

         if (headset.countRows() == 0)
         {
            appendDirtyJavaScript("document.form.SELECTALL.disabled = true;\n");
            appendDirtyJavaScript("document.form.DESELECTALL.disabled = true;\n");
            appendDirtyJavaScript("document.form.OK.disabled = true;\n");
         }
      }

      appendDirtyJavaScript("function assignEmpId()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("var row_string = document.form.SIGN.value;\n");
      appendDirtyJavaScript("var row_array  = row_string.split('^');\n");
      appendDirtyJavaScript("if (row_array.length>1)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("document.form.SIGN.value = row_array[0];\n");
      appendDirtyJavaScript("document.form.SIGN_ID.value = row_array[1];\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("else{;\n");
      appendDirtyJavaScript("document.form.SIGN.value = document.form.SIGN.value.toUpperCase();");
      appendDirtyJavaScript("validateSign(-1);\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateSign(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( getValue_('SIGN',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('SIGN_ID',i).value = '';\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=SIGN'\n");
      appendDirtyJavaScript("		+ '&SIGN=' + URLClientEncode(getValue_('SIGN',i))\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'SIGN',i,'Signature') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('SIGN_ID',i,0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovSign(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if (document.form.OPTION[1].checked) \n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("if(params) param = params;\n");
      appendDirtyJavaScript("else param = '';\n");
      appendDirtyJavaScript("var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
      appendDirtyJavaScript("var key_value = (getValue_('SIGN',i).indexOf('%') !=-1)? getValue_('SIGN',i):'';\n");
      appendDirtyJavaScript("var jLovWhere = '';\n");
      appendDirtyJavaScript("var jContract = '" + mgr.encodeStringForJavascript(sContract) + "';\n");
      appendDirtyJavaScript("jLovWhere = 'Employee_Id IN (SELECT Sign_Id FROM WORK_ORDER_ROLE ' + \n");
      appendDirtyJavaScript("  	'where WO_NO = ' + getValue_('WO_NO',i) + ' ' + \n");
      appendDirtyJavaScript("  	'and OP_CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(\\'' + jContract + '\\') ' + \n");
      appendDirtyJavaScript("  	'FROM dual) AND PARENT_ROW_NO IS NOT NULL and ( PARENT_ROW_NO IN (';\n");
      appendDirtyJavaScript("   var jRowCount = " + mgr.encodeStringForJavascript(nRowCount+"") + ";\n");
      appendDirtyJavaScript("   var selected = false;\n");
      appendDirtyJavaScript("   if(jRowCount > 1)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      var first = true;\n");      
      appendDirtyJavaScript("      for(__x=0;__x<f.__SELECTED1.length;__x++)\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("  	      if(f.__SELECTED1[__x].checked==true)\n");
      appendDirtyJavaScript("         {\n");
      appendDirtyJavaScript("             selected = true;\n");
      appendDirtyJavaScript("             if(first==true)\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                first = false;\n");
      appendDirtyJavaScript("                jLovWhere = jLovWhere + getValue_('ROW_NO',(__x+1));\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("             else\n");
      appendDirtyJavaScript("                jLovWhere = jLovWhere + ', ' + getValue_('ROW_NO',(__x+1));\n");
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("      }\n");      
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else if(jRowCount == 1)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       if(f.__SELECTED1.checked==true)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          selected = true;\n");
      appendDirtyJavaScript("          jLovWhere = jLovWhere + getValue_('ROW_NO',1);\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if(selected==false)\n");
      appendDirtyJavaScript("      jLovWhere = jLovWhere + '\\'\\'';\n");
      appendDirtyJavaScript("   jLovWhere = jLovWhere + ')))';\n");
      appendDirtyJavaScript("openLOVWindow('SIGN',i,\n");
      appendDirtyJavaScript("'../mscomw/MaintEmployeeLov.page' + '?__VIEW=DUMMY&__INIT=1&__WHERE=' + jLovWhere + '&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('SIGN',i))\n");
      appendDirtyJavaScript("                      + '&SIGN=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript(",600,450,'assignEmpId');\n"); 
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function handleClickRadio(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("		if (i == '1')\n");
      appendDirtyJavaScript("		       	document.form.SIGN.disabled = false;\n");
      appendDirtyJavaScript("		else\n");
      appendDirtyJavaScript("		       	document.form.SIGN.disabled = true;\n");
      appendDirtyJavaScript("}\n");

      if (closeFlag)
         appendDirtyJavaScript("	self.close();\n");

      appendDirtyJavaScript("if (");
      appendDirtyJavaScript(saveCloseFlag);
      appendDirtyJavaScript(")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if (");
      appendDirtyJavaScript(!mgr.isEmpty(qryStr));
      appendDirtyJavaScript(")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  window.open('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr));	//XSS_Safe AMNILK 20070726
      appendDirtyJavaScript("' + \"&TIMEREPORT=1\",'");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName));	//XSS_Safe AMNILK 20070726
      appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
      appendDirtyJavaScript("	self.close();\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateEmpNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("    if (i == -1)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        j = i;\n");
      appendDirtyJavaScript("        i = -1;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkEmpNo(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('EMP_NO',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('EMP_SIGNATURE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('NAME',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM1_ORG_CODE',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=EMP_NO'\n");
      appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'EMP_NO',i,'Employee ID') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('EMP_NO',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('EMP_SIGNATURE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('NAME',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_ORG_CODE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_CONTRACT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('CMNT',i,6);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("    if ( j == -1 )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        validateItem1OrgCode(1);\n");
      appendDirtyJavaScript("        validateRoleCode(1);\n");
      appendDirtyJavaScript("        validateCatalogNo(1);\n");
      appendDirtyJavaScript("        validateCmnt(1);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem1OrgCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("    if (i == -1)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        j = i;\n");
      appendDirtyJavaScript("        i = -1;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem1OrgCode(i) ) return;\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM1_ORG_CODE'\n");
      appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_WO_NO=' + URLClientEncode(getValue_('ITEM1_WO_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + getValue_('ITEM1_CONTRACT',i)\n");
      appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
      appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&CMNT=' + URLClientEncode(getValue_('CMNT',i))\n");
      appendDirtyJavaScript("		+ '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_ORG_CODE',i,'Maintenance Organization') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_ORG_CODE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_CONTRACT',i,1);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('CMNT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,4);\n");
      appendDirtyJavaScript("		assignValue_('SALESPARTCOST',i,6);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("    if (j == -1)\n");
      appendDirtyJavaScript("        validateCatalogNo(1);\n");
      appendDirtyJavaScript("        validateCmnt(1);\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateCatalogNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("    if (i == -1)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        j = i;\n");
      appendDirtyJavaScript("        i = -1;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CATALOG_NO'\n");
      appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
      appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
      appendDirtyJavaScript("		+ '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'CATALOG_NO',i,'Sales Part Number') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNT',i,1);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,2);\n");
      appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,3);\n");
      appendDirtyJavaScript("		assignValue_('CMNT',i,4);\n");
      appendDirtyJavaScript("		assignValue_('SALESPARTCOST',i,6);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("    if (j == -1)\n");
      appendDirtyJavaScript("        validatePriceListNo(1);\n");
      appendDirtyJavaScript("        validateCmnt(1);\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validatePriceListNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PRICE_LIST_NO'\n");
      appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
      appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
      appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PRICE_LIST_NO',i,'Price List No') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,1);\n");
      appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,2);\n");
      appendDirtyJavaScript("		assignValue_('AGREEMENT_PRICE_FLAG',i,3);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateRoleCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("    if (i == -1)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        j = i;\n");
      appendDirtyJavaScript("        i = -1;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkRoleCode(i) ) return;\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ROLE_CODE'\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
      appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
      appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
      appendDirtyJavaScript("		+ '&CMNT=' + URLClientEncode(getValue_('CMNT',i))\n");
      appendDirtyJavaScript("		+ '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
      appendDirtyJavaScript("		+ '&SALESPARTCOST=' + URLClientEncode(getValue_('SALESPARTCOST',i))\n");
      appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_CONTRACT',i,1);\n");
      appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,4);\n");
      appendDirtyJavaScript("		assignValue_('CMNT',i,5);\n");
      appendDirtyJavaScript("         assignValue_('DISCOUNT',i,8);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("    if (j == -1)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        validateCatalogNo(1);\n");
      appendDirtyJavaScript("        validatePriceListNo(1);\n");
      appendDirtyJavaScript("        validateCmnt(1);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovRoleCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('EMP_NO',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("	            if( getValue_('ITEM1_ORG_CODE',i)=='' )\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                  openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("       	             ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("	            else\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                 openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/EmployeeRoleLov.page?&__FIELD=Craft+ID&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("       	            ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("              openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("       	   ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

   }
}
