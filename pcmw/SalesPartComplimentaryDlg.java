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
*  File        : SalesPartComplimentaryDlg.java 
*  Created     : ASP2JAVA Tool  010416  Created Using the ASP file SalesPartComplimentaryDlg.asp
*  Modified    :  
*  BUNILK  010416  Corrected some conversion errors.
*  SHFELK  010509  Added validation for LIST_PRICE.
*  CHCRLK  010815  Modified method submit(). 
*  JEWILK  010822  Modified to return to the original layout of the calling form.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  SHAFLK  050401  Bug 50156,Modified to take an return discount as well.
*  NIJALK  050531  Merged bug 50156. 
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class SalesPartComplimentaryDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.SalesPartComplimentaryDlg");

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

    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String cat_no;
    private String list_pri;
    private String cont;
    private String linedesc;
    private String up_wo_no;
    private String row_no;
    private String cus_no;
    private String agree_id;
    private String callingurl1;
    private ASPTransactionBuffer trans;
    private String calling_url;
    private String cancelFlag;
    private String okFlag;
    private ASPBuffer buff;
    private ASPBuffer row;
    private ASPCommand cmd;
    private String strWoNo;
    private String singleRowNo;
    private ASPQuery q;
    private ASPBuffer r;
    private String discount;
    private String price_list;

    //===============================================================
    // Construction 
    //===============================================================
    public SalesPartComplimentaryDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        cat_no = "";
        list_pri = "";
        cont = "";
        linedesc = "";
        up_wo_no = "";
        row_no = "";
        cus_no = "";
        agree_id = "";
        callingurl1 =  "";
        discount = "";
        price_list = null;

        ASPManager mgr = getASPManager();

        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        calling_url = ctx.getGlobal("CALLING_URL");

        cat_no = ctx.readValue("CAT_NO",cat_no);  
        list_pri = ctx.readValue("LIST_PRI",list_pri);
        cont = ctx.readValue("CONT",cont);
        linedesc = ctx.readValue("LINEDESC",linedesc);
        up_wo_no = ctx.readValue("UP_WO_NO",up_wo_no);
        row_no = ctx.readValue("ROW_NO",row_no);
        cus_no = ctx.readValue("CUS_NO",cus_no);
        agree_id = ctx.readValue("AGREE_ID",agree_id);
        cancelFlag =  ctx.readValue("CANCELFLAG","FALSE");                         
        okFlag =  ctx.readValue("OKFLAG","FALSE");                        
        callingurl1 =  ctx.readValue("CALLINGURL1",callingurl1); 
        singleRowNo = ctx.readValue("CTXSINGLEROWNO","");
        discount = ctx.readValue("CTXDISCOUNT","");
        price_list = ctx.readValue("CTXPRICELIST","");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
        {
            buff= mgr.getTransferedData();
            row = buff.getBufferAt(0);
            cat_no = row.getValue("CATALOG_NO");
            row = buff.getBufferAt(1);
            list_pri = row.getValue("LIST_PRICE");
            row = buff.getBufferAt(2);
            cont = row.getValue("CONTRACT");
            row = buff.getBufferAt(3);
            linedesc = row.getValue("LINE_DESCRIPTION");
            row = buff.getBufferAt(4);
            up_wo_no = row.getValue("WO_NO");
            row = buff.getBufferAt(5);
            row_no = row.getValue("ROW_NO");
            row = buff.getBufferAt(6);
            cus_no = row.getValue("CUSTOMER_NO");
            row = buff.getBufferAt(7);
            agree_id = row.getValue("AGREEMENT_ID");
            row = buff.getBufferAt(8);
            callingurl1 = row.getValue("FORM2");
            row = buff.getBufferAt(9);
            singleRowNo = row.getValue("SINGLE_ROW_NO");
            row = buff.getBufferAt(10);
            price_list = row.getValue("PRICE_LIST_NO");
            row = buff.getBufferAt(11);
            discount = row.getValue("DISCOUNT");
            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();

        adjust();

        ctx.writeValue("CAT_NO",cat_no);
        ctx.writeValue("LIST_PRI",list_pri);
        ctx.writeValue("CONT",cont);
        ctx.writeValue("LINEDESC",linedesc);
        ctx.writeValue("UP_WO_NO",up_wo_no);
        ctx.writeValue("ROW_NO",row_no);
        ctx.writeValue("CUS_NO",cus_no);
        ctx.writeValue("AGREE_ID",agree_id);
        ctx.writeValue("CANCELFLAG",cancelFlag);
        ctx.writeValue("OKFLAG",okFlag);
        ctx.writeValue("CALLINGURL1",callingurl1);
        ctx.writeValue("CTXSINGLEROWNO",singleRowNo);
        ctx.writeValue("CTXDISCOUNT",discount);
        ctx.writeValue("CTXPRICELIST",price_list);

    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        String txt = "";

        String val = mgr.readValue("VALIDATE");

        if ("CATALOG_NO".equals(val))
        {
            cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("CATALOG_NO");

            trans = mgr.validate(trans);

            String strCatalogDeac= trans.getValue("CATDESC/DATA/CATALOGDESC");

            trans.clear();

            cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE");
            cmd.addParameter("SALE_PRICE");
            cmd.addParameter("DISCOUNT");
            cmd.addParameter("CURRENCY_RATE");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter ("CUSTOMER_NO");
            cmd.addParameter ("AGREEMENT_ID");
            cmd.addParameter("PRICE_LIST_NO");
            cmd.addParameter ("QTY_TO_INVOICE","1");
            cmd.addParameter ("WO_NO");

            trans = mgr.validate(trans);

            double list_price = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
            if (isNaN(list_price))
                list_price = 0;
            double discount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if ( isNaN(discount) )
                discount = 0;


            String strListPrice = mgr.formatNumber("LIST_PRICE",list_price);
            String strDiscount = mgr.formatNumber("DISCOUNT",discount);

            txt =  (mgr.isEmpty(strCatalogDeac) ? "" :strCatalogDeac) + "^" +  (mgr.isEmpty(strListPrice) ? "" :strListPrice)+ "^" +  (mgr.isEmpty(strDiscount) ? "" :strDiscount)+ "^" ;
            mgr.responseWrite(txt);

        }
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  submit()
    {
        ASPManager mgr = getASPManager();

        headset.changeRow();

        cmd = trans.addCustomCommand("MODCODE","Work_Order_Coding_API.Modify_Sales_Part_Compl");
        cmd.addParameter("CATALOG_NO",mgr.readValue("CATALOG_NO",""));
        cmd.addParameter("CONTRACT",cont);
        cmd.addParameter("ROW_NO",row_no);
        cmd.addParameter("WO_NO",up_wo_no);
        cmd.addParameter("LINE_DESCRIPTION",mgr.readValue("CATALOGDESC",""));
        cmd.addParameter("LIST_PRICE",mgr.readValue("LIST_PRICE",""));
        cmd.addParameter("DISCOUNT",mgr.readValue("DISCOUNT",""));

        mgr.submit(trans);

        strWoNo=headset.getRow().getFieldValue("HEAD_CONTRACT");

        okFlag = "TRUE";
    }


    public void  cancel()
    {

        strWoNo=headset.getRow().getValue("HEAD_CONTRACT");
        cancelFlag = "TRUE";
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        r = headset.getRow();
        r.setValue("CATALOG_NO",cat_no);
        r.setValue("LIST_PRICE",list_pri);
        r.setValue("CONTRACT",cont);
        r.setValue("LINE_DESCRIPTION",linedesc);
        r.setValue("HEAD_CONTRACT",up_wo_no);
        r.setValue("ROW_NO",row_no);
        r.setValue("CUSTOMER_NO",cus_no);
        r.setValue("AGREEMENT_ID",agree_id);
        r.setValue("CATALOGDESC",linedesc);
        r.setValue("DISCOUNT",discount);
        r.setValue("PRICE_LIST_NO",price_list);
        headset.setRow(r);
    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("OBJVERSION");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CATALOG_NO");
        f.setSize(25);
        f.setDynamicLOV("SALES_PART","CONTRACT",600,445);
        f.setLabel("PCMWSALESPARTCOMPLIMENTARYDLGCATALOG_NO: Sales Part Number");
        f.setUpperCase();
        f.setFunction("''");
        f.setCustomValidation("CUSTOMER_NO,AGREEMENT_ID,CATALOG_NO,PRICE_LIST_NO,CONTRACT,QTY_TO_INVOICE,LIST_PRICE,DISCOUNT","CATALOGDESC,LIST_PRICE,DISCOUNT");

        f = headblk.addField("LINE_DESCRIPTION");
        f.setSize(25);
        f.setLabel("PCMWSALESPARTCOMPLIMENTARYDLGLINE_DESCRIPTION: Description");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("LIST_PRICE","Number");
        f.setSize(25);
        f.setLabel("PCMWSALESPARTCOMPLIMENTARYDLGLIST_PRICE: Price");
        f.setFunction("''");

        f = headblk.addField("DISCOUNT","Number");
        f.setSize(25);
        f.setLabel("PCMWSALESPARTCOMPLIMENTARYDLGDISCOUNT: Discount");
        f.setFunction("''");

        f = headblk.addField("CONTRACT");
        f.setSize(18);
        f.setLOV("UserAllowedSiteLovLov.page",600,445);
        f.setHidden();
        f.setUpperCase();
        f.setFunction("''");

        f = headblk.addField("HEAD_CONTRACT","Number","#");
        f.setSize(18);
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("ROW_NO","Number");
        f.setSize(18);
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CATALOGDESC");
        f.setSize(25);
        f.setFunction("''");
        f.setLabel("PCMWSALESPARTCOMPLIMENTARYDLGCATALOGDESC: Description");

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(18);
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("AGREEMENT_ID");
        f.setSize(18);
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("WO_NO","Number");
        f.setSize(18);
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("BASE_PRICE","Number");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("SALE_PRICE","Number");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("PRICE_LIST_NO");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("QTY_TO_INVOICE","Number");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CURRENCY_RATE","Number");
        f.setFunction("''");
        f.setHidden();


        headblk.setView("DUAL");
        headblk.defineCommand("","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELEDIT);
        headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)"); 
        headbar.defineCommand(headbar.CANCELEDIT,"cancel");

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setEditable();
        headlay.defineGroup(mgr.translate("PCMWSALESPARTCOMPLIMENTARYDLGSAPACOMP: Sales Part Complimentary"),"CATALOG_NO,CATALOGDESC,LIST_PRICE,DISCOUNT",true,true);
        headlay.setDialogColumns(1);

        headtbl = mgr.newASPTable(headblk);
    }


    public void  adjust()
    {


    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWSALESPARTCOMPLIMENTARYDLGTITLE: Sales Part Complimentary";
    }

    protected String getTitle()
    {
        return "PCMWSALESPARTCOMPLIMENTARYDLGTITLE: Sales Part Complimentary";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        appendDirtyJavaScript("window.name = \"SalesPartComp\";\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(okFlag));		//XSS_Safe AMNILK 20070725
        appendDirtyJavaScript("' == 'TRUE')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   str = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(callingurl1));	//XSS_Safe AMNILK 20070725
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("   if (str.search(\"ActiveRound\") != -1)\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("        window.open(\"ActiveRound.page?WO_NO=\"+URLClientEncode(");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strWoNo));		//XSS_Safe AMNILK 20070725

        if (!mgr.isEmpty(singleRowNo))
        {
            appendDirtyJavaScript(")+\"&SINGLE_ROW_NO=\"+URLClientEncode(");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(singleRowNo));	//XSS_Safe AMNILK 20070725
        }

        appendDirtyJavaScript(")+\"\",\"ActiveRound\",\"\");\n");
        appendDirtyJavaScript("        window.close();\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      window.open(\"WorkOrderCoding1.page?WO_NO=\"+URLClientEncode(");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strWoNo));		//XSS_Safe AMNILK 20070725
        appendDirtyJavaScript(")+\"&REFRESH=\"+URLClientEncode(");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(row_no));		//XSS_Safe AMNILK 20070725

        if (!mgr.isEmpty(singleRowNo))
        {
            appendDirtyJavaScript(")+\"&SINGLE_ROW_NO=\"+URLClientEncode(");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(singleRowNo));	//XSS_Safe AMNILK 20070725
        }

        appendDirtyJavaScript(")+\"&FORM1=\"+URLClientEncode('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(callingurl1));	//XSS_Safe AMNILK 20070725
        appendDirtyJavaScript("'),\"WorkOrCoding1\",\"alwaysRaised,resizable,scrollbars=yes,width=770,height=460\");  \n");
        appendDirtyJavaScript("      window.close();\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelFlag));	//XSS_Safe AMNILK 20070725
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   window.close();\n");
        appendDirtyJavaScript("}\n");
    }
}
