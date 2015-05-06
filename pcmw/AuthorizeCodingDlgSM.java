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
*  File        : AuthorizeCodingDlgSM.java 
*  Created     : SHFELK  010427  Created
*  Modified    :
*  CHCRLK  010828  Modified and corrected functionality. 
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods) 
* --------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies. 
*  THWILK  040325  Merge with SP1.
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  THWILK  060310  Call 136653,Modified run(),submit() and javascript code in printContents().
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged bug Id: 58214.
*  JEWILK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged Bug 58216.
*  AMNILK  060920  Modified the run() method.
*  AMNILK  060921  MTPR904 Hink tasks. Request Id: 45082. Modified methods printContents(), submit().
*		   Added new method getDefaultUser().
*  AMDILK  061101  MTPR904: Request id 45082.  Modified the submit() to notify 
                   when there is no valid employee attched to the logged in user    
*  ILSOLK  070709  Eliminated XSS.  
*  ILSOLK  070730  Eliminated LocalizationErrors.    	   
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071215  ILSOLK  Bug Id 68773, Eliminated XSS.
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class AuthorizeCodingDlgSM extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.AuthorizeCodingDlgSM");

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

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String strWoNos;
	private String strSeperator;
	private ASPTransactionBuffer trans;
	private String calling_url;
	private String returnFlag;
	private String cancelFlag;
	private String strSign;
	private ASPCommand cmd;
	private int rows;
	private double nBasePriceVal;
	private String nSalesPriceVal;
	private String nQtyReqd;
	private String nPriceListNo;
	private String prceListno;
	private ASPBuffer r;
	private String val;
	private double listval;
	private double calclistval;
	private ASPQuery q;
	private String comand_;
	private String company;
	private String strRowNos;  
	private String date_fmt;
	private double diff; 
	private String txt; 
	private String strRowNo1;
	private int posSeparator; 
        private ASPBuffer transferedDataBuffer;
        private String qrystr;
	private String defaultUser;


	//===============================================================
	// Construction 
	//===============================================================
	public AuthorizeCodingDlgSM(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		company = "";
		strWoNos = "";
		strRowNos = "";
		strSeperator = "";

		ASPManager mgr = getASPManager();

		frm = mgr.getASPForm();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		calling_url=ctx.getGlobal("CALLING_URL");
                qrystr = ctx.readValue("QRYSTR","");

		date_fmt = mgr.getFormatMask("Date",true);

		company = ctx.readValue("CTX1", company);
		strWoNos = ctx.readValue("CTX2", strWoNos);
		strRowNos = ctx.readValue("CTX3", strRowNos);
		strSeperator = ctx.readValue("CTX4", strSeperator);

		returnFlag = ctx.readValue("RETURNFLAG", "FALSE");
		cancelFlag = ctx.readValue("CANCELFLAG", "FALSE");
                transferedDataBuffer = ctx.readBuffer("CTXTRANSBUFF");

		// creates headblk,headset,headbar, headtbl, itemblk,itemset,itembar, tbl
                if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.buttonPressed("FINISH"))
			submit();
		else if (mgr.buttonPressed("CANCEL"))
			cancelFlag = "TRUE";
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
                else if (mgr.dataTransfered()){
                    
                    if (!mgr.isEmpty(mgr.getQueryStringValue("QRYSTR"))){
			qrystr=mgr.getQueryStringValue("QRYSTR");
			ctx.writeValue("QRYSTR",qrystr);  
		    }
                   transferedDataBuffer = mgr.getTransferedData();
                   company = transferedDataBuffer.getBufferAt(0).getValue("COMPANY2");
                   strWoNos = transferedDataBuffer.getBufferAt(0).getValue("WO_NO");
                   strSign = transferedDataBuffer.getBufferAt(0).getValue("SIGNATURE");
                   okFind();
  	           startup();
		   ctx.writeBuffer("CTXTRANSBUFF", transferedDataBuffer);
                }

                else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
                        company = mgr.getQueryStringValue("COMPANY");
			strWoNos = mgr.readValue("WO_NO");
			strRowNos = mgr.getQueryStringValue("ROW_NO");
			strSign = mgr.getQueryStringValue("SIGNATURE");

			okFind();
			startup();
		}
		else
			clear();

		adjust();
                
		ctx.writeValue("CTX1", company);
		ctx.writeValue("CTX2", strWoNos);
		ctx.writeValue("CTX3", strRowNos);
		ctx.writeValue("CTX4", strSeperator);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void startup()
	{
		int count;
		ASPManager mgr = getASPManager();

		cmd = trans.addCustomFunction("CONTRA","User_Default_API.Get_Contract","CONTRACT");

		cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
		cmd.addParameter("WO_NO");

		rows = headset.countRows();

		headset.first();

		for (count = 1; count <= rows; ++count)
		{
			trans.clear();

			cmd = trans.addCustomCommand("GETPRICEINF","Work_Order_Coding_API.Get_Price_Info");
			cmd.addParameter("BASEPRICE");          
			cmd.addParameter("SALEPRICE");          
			cmd.addParameter("DISCOUNT");          
			cmd.addParameter("CURRATE");          
			cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));          
			cmd.addParameter("CATALOG_NO",headset.getRow().getValue("CATALOG_NO"));          
			cmd.addParameter("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));          
			cmd.addReference("AGREEMENT_ID","AGREID/DATA");    
			cmd.addParameter("PRICE_LIST_NO",headset.getRow().getValue("PRICE_LIST_NO")); 
			cmd.addParameter("QTY",headset.getRow().getValue("QTY"));    

			trans = mgr.perform(trans);

			nBasePriceVal = trans.getNumberValue("GETPRICEINF/DATA/BASEPRICE");
			if (isNaN(nBasePriceVal))
				nBasePriceVal=0;
			nSalesPriceVal = trans.getValue("GETPRICEINF/DATA/SALEPRICE");
			nQtyReqd = trans.getValue("GETPRICEINF/DATA/DISCOUNT");
			nPriceListNo = trans.getValue("GETPRICEINF/DATA/PRICE_LIST_NO");

			if (mgr.isEmpty(headset.getRow().getValue("PRICE_LIST_NO")))
				prceListno = nPriceListNo;

			else
				prceListno = headset.getRow().getValue("PRICE_LIST_NO");

			diff = (headset.getRow().getNumberValue("LIST_PRICE") - nBasePriceVal);


			r = headset.getRow();
			r.setValue("PRICE_LIST_NO", prceListno);
			r.setNumberValue("CALC_LIST_PRICE", nBasePriceVal);
			r.setNumberValue("DIFFERENCE", diff);
			headset.setRow(r);


			if ((headset.getNumberValue("DIFFERENCE")) == 0)
				headset.setValue("CONFIRM", "TRUE");

			headset.next();
		}
	}

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");


		if ("LIST_PRICE".equals(val))
		{
			listval = mgr.readNumberValue("LIST_PRICE");
			calclistval = mgr.readNumberValue("CALC_LIST_PRICE");
			if (isNaN(listval))
				listval = 0;
			if (isNaN(calclistval))
				calclistval = 0;

			diff = listval - calclistval;
			String diffStr = mgr.getASPField("DIFFERENCE").formatNumber(diff);

			txt = (mgr.isEmpty(diffStr) ? "" : (diffStr)) + "^" + ((diff == 0)?"TRUE":"FALSE") + "^";

			mgr.responseWrite(txt);
		}

		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addEmptyQuery(headblk);
		q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",strWoNos);
		q.addWhereCondition("SIGNATURE IS NULL");
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGSMNODATAFOUND: No data found."));
			headset.clear();
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}
	}

	public void submit()
	{
		ASPManager mgr = getASPManager();

		headset.changeRows();
		getDefaultUser();
		trans.clear();

		cmd = trans.addCustomFunction("PERSON", "Person_Info_API.Get_Id_For_User", "SIGNATURE");
	        cmd.addParameter("SIGN", defaultUser);

		cmd = trans.addCustomFunction("EMP1", "Company_Emp_API.Get_Max_Employee_Id","SIGN_ID");
		cmd.addParameter("COMPANY",company);
		cmd.addReference("SIGNATURE","PERSON/DATA");

		cmd = trans.addCustomFunction("EMP2", "Person_Info_API.Get_Name","NAME");
		cmd.addParameter("SIGN",defaultUser);
		trans=mgr.perform(trans);

		String strIsName = trans.getValue("EMP2/DATA/NAME");
		String strAuthId = trans.getValue("EMP1/DATA/SIGN_ID");
                int numOfRows = headset.countRows();
		headset.first();   

		if (!mgr.isEmpty(strIsName))
		{
		        if (mgr.isEmpty(strAuthId)) 
			{
			    mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGNOVALIDEMP: A valid employee id does not exists for the logged in user " + defaultUser));
			}
			else 
			{
                            for (int count = 1; count <= numOfRows; ++count)
			    {
				    trans.clear();
    
				    cmd = trans.addCustomCommand("MODIFYSLSPARTCOMP"+count, "Work_Order_Coding_API.Modify_Sales_Part_Compl");
				    cmd.addParameter("CATALOG_NO",headset.getRow().getValue("CATALOG_NO"));
				    cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
				    cmd.addParameter("ROW_NO",headset.getRow().getValue("ROW_NO"));
				    cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
				    cmd.addParameter("LINE_DESCRIPTION",headset.getRow().getValue("LINE_DESCRIPTION"));
				    cmd.addParameter("LIST_PRICE",headset.getRow().getValue("LIST_PRICE"));                                     
				    cmd.addParameter("DISCOUNT",headset.getRow().getValue("DISCOUNT"));                                     
    
				    if ("TRUE".equals(headset.getRow().getValue("CONFIRM")))
				    {
					    cmd = trans.addCustomCommand("ADELIVER"+count, "Work_Order_Coding_API.Authorize");
					    cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
					    cmd.addParameter("ROW_NO",headset.getRow().getValue("ROW_NO"));
					    cmd.addParameter("SIGN_ID",strAuthId);
				    }
    
				    mgr.perform(trans);
				    headset.next();
			    }
    
			    returnFlag = "TRUE";  
			}
			
		}
		else
			mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGAUTHNOTREG: The authorizer is not registered."));
	}

	public void getDefaultUser(){
	    ASPManager mgr = getASPManager();

	    trans.clear();
	    ASPCommand cmd = trans.addCustomFunction("USER_SIGN","Fnd_Session_API.Get_Fnd_User","SIGNAT");
	    trans = mgr.perform(trans);

	    defaultUser = trans.getValue("USER_SIGN/DATA/SIGNAT");
	}


	public String  CurrentValue( String ParamNos,int flag) 
	{
		String myString;

		strSeperator = ctx.readValue("CTX4", strSeperator);
		myString = new String(ParamNos);

		posSeparator = myString.indexOf(strSeperator);

		if (flag == 1)
			strRowNos = myString.substring(posSeparator+1);

		return(myString.substring(0, posSeparator));
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void clear()
	{

		headset.clear();
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("WO_NO");
		f.setLabel("PCMWAUTHORIZECODINGDLGSMWONO: WO NO");
                f.setHidden();


		f = headblk.addField("ROW_NO","Number");
		f.setHidden();

		f = headblk.addField("CONFIRM");
		f.setSize(8);
		f.setFunction("''");
		f.setCheckBox("FALSE,TRUE");
		f.setLabel("PCMWAUTHORIZECODINGDLGSMCONFIRMLBL: Confirmed");

		f = headblk.addField("CONTRACT");
		f.setSize(8);
		f.setUpperCase();
		f.setLabel("PCMWAUTHORIZECODINGDLGSMCONTRACTLBL: Site");
		f.setReadOnly();

		f = headblk.addField("CATALOG_NO");
		f.setSize(8);
		f.setUpperCase();
		f.setLabel("PCMWAUTHORIZECODINGDLGSMCATALOGNOLBL: Sales Part No");
		f.setReadOnly();

		f = headblk.addField("LIST_PRICE","Number");
		f.setSize(8);
		f.setLabel("PCMWAUTHORIZECODINGDLGSMLISTPRICELBL: Sales Price");
		f.setCustomValidation("LIST_PRICE,CALC_LIST_PRICE","DIFFERENCE,CONFIRM");

                f = headblk.addField("CALC_LIST_PRICE","Number");
		f.setFunction("''");
		f.setLabel("PCMWAUTHORIZECODINGDLGSMCALCLISTPRICELBL: Calculated List Price");
		f.setReadOnly();

		f = headblk.addField("DIFFERENCE","Number");
		f.setFunction("''");
		f.setLabel("PCMWAUTHORIZECODINGDLGSMDIFFERENCELBL: Difference");
		f.setReadOnly();

		f = headblk.addField("PRICE_LIST_NO");
		f.setHidden();

		f = headblk.addField("QTY");
		f.setHidden();

		f = headblk.addField("AGREEMENT_PRICE_FLAG");
		f.setHidden();

		f = headblk.addField("SIGN");
		f.setSize(9);
		f.setLabel("PCMWAUTHORIZECODINGDLGSMSIGN: Signature");
		f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
		f.setUpperCase();
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("SIGN_ID");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("SIGNATURE_ID");
		f.setHidden();

		f = headblk.addField("SIGNATURE");
		f.setSize(8);
		f.setLabel("PCMWAUTHORIZECODINGDLGSMSIGN: Signature");
		f.setHidden();

		f = headblk.addField("SIGNAT");
		f.setSize(8);
		f.setLabel("PCMWAUTHORIZECODINGDLGSMSIGN: Signature");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("COMPANY");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("NAME");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("LINE_DESCRIPTION");
		f.setHidden();

		f = headblk.addField("BASEPRICE");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("SALEPRICE");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("DISCOUNT");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CURRATE");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("AGREEMENT_ID");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CUSTOMER_NO");
		f.setHidden();

		headblk.setView("WORK_ORDER_CODING");
		headblk.defineCommand("WORK_ORDER_CODING_API","Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.disableCommand(headbar.FORWARD);
		headbar.disableCommand(headbar.BACKWARD);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.VIEWDETAILS);

		headbar.defineCommand(headbar.SAVERETURN,"submit"); 
		headbar.defineCommand(headbar.CANCELNEW,"cancel");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setEditable();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setDialogColumns(2);
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();
                mgr.getASPField("SIGNATURE").setDynamicLOV("EMPLOYEE_LOV",600,445);
		mgr.getASPField("SIGNATURE").setLOVProperty("WHERE","COMPANY= '"+company+"'"); 
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWAUTHORIZECODINGDLGSMTITLE: Authorize and Confirm Price Difference";
	}

	protected String getTitle()
	{
		return "PCMWAUTHORIZECODINGDLGSMTITLE: Authorize and Confirm Price Difference";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		if (headlay.isMultirowLayout())
		{
			appendToHTML("<br>");
			appendToHTML("  <table  border=0 bgcolor=green class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>\n");
			appendToHTML("   <tr>\n");
			//removed the signature field ;amnilk
			appendToHTML("      <td nowrap height=\"26\" align=\"right\" width = '8%' >");
			appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWAUTHORIZECODINGDLGSMFINISH1: Ok"),"submit"));
			appendToHTML("</td>   \n");
			appendToHTML("	    <td nowrap height=\"26\" alain=\"left\" width = '8%'>");
			appendToHTML("</td>   \n");
			appendToHTML("      <td nowrap height=\"26\" align=\"left\" width = '8%' >");
			appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWAUTHORIZECODINGDLGSMCANCEL: Cancel"),"submit"));
			appendToHTML("</td>   \n");
			appendToHTML("   </tr>\n");
			appendToHTML("  </table>\n");
		}

                appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(returnFlag)); // Bug Id 68773
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  str = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url)); // XSS_Safe ILSOLK 20070709
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  re  = \"ActiveRound\";\n");
		appendDirtyJavaScript("  if (str.search(re) != -1) \n");
		appendDirtyJavaScript("  {\n");
                appendDirtyJavaScript("     window.opener.location=\""+mgr.encodeStringForJavascript(qrystr)+"&START=Authorize&WONO=\"+'"); // XSS_Safe ILSOLK 20070709
                appendDirtyJavaScript(mgr.URLEncode(strWoNos));
		appendDirtyJavaScript("'+\"\";\n");
		appendDirtyJavaScript("  }\n");
		appendDirtyJavaScript("  else if (str.search(\"PostingsOvw\") != -1)   \n");
		appendDirtyJavaScript("  {\n");
		appendDirtyJavaScript("      window.open(\"WorkOrderPostingsOvw.page?WO_NO=\"+'");
                appendDirtyJavaScript(mgr.URLEncode(strWoNos));
		appendDirtyJavaScript("'+\"\",\"WorkOrdPostings\",\"\");  \n");
		appendDirtyJavaScript("  }\n");
		appendDirtyJavaScript("  else\n");
		appendDirtyJavaScript("  {\n");
		appendDirtyJavaScript("      window.open(\"WorkOrderCoding1.page?WO_NO=\"+'");
                appendDirtyJavaScript(mgr.URLEncode(strWoNos));
		appendDirtyJavaScript("'+\"\",\"WorkOrCoding1\",\"\");\n");
		appendDirtyJavaScript("  }\n");
		appendDirtyJavaScript("   self.close();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelFlag)); // Bug Id 68773
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   self.close();\n");
		appendDirtyJavaScript("}\n");

		//Validation for list price
		appendDirtyJavaScript("function validateListPrice(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkListPrice(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('LIST_PRICE',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('DIFFERENCE',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("       '");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=LIST_PRICE'\n");
		appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
		appendDirtyJavaScript("		+ '&CALC_LIST_PRICE=' + URLClientEncode(getValue_('CALC_LIST_PRICE',i))\n");
		appendDirtyJavaScript("       	);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript(" if( checkStatus_(r,'LIST_PRICE',i,'Sales Price') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('DIFFERENCE',i,0);\n");
		appendDirtyJavaScript("		assignValue_('CONFIRM',i,1);\n");
		appendDirtyJavaScript("         if (__getValidateValue(1) == \"TRUE\")\n");
		appendDirtyJavaScript("	        {\n");
		appendDirtyJavaScript("                 if(");
		appendDirtyJavaScript(                  headset.countRows());
		appendDirtyJavaScript("                 == 1)");
		appendDirtyJavaScript("                         document.form._CONFIRM.checked = true;\n");
		appendDirtyJavaScript("                 else\n");
		appendDirtyJavaScript("                         document.form._CONFIRM[i-1].checked = true;\n");
		appendDirtyJavaScript("	        }\n");
		appendDirtyJavaScript("         else\n");
		appendDirtyJavaScript("	        {\n");
		appendDirtyJavaScript("                 if(");
		appendDirtyJavaScript(                  headset.countRows());
		appendDirtyJavaScript("                 == 1)");
		appendDirtyJavaScript("                         document.form._CONFIRM.checked = false;\n");
		appendDirtyJavaScript("                 else\n");
		appendDirtyJavaScript("                         document.form._CONFIRM[i-1].checked = false;\n");
		appendDirtyJavaScript("	        }\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");
	}
}
