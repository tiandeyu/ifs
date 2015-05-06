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
*  File        : NewCustomerDlg.java 
*  Modified    :
*    ARWILK       2001-03-14  - Created.
*    JEWI         2001-04-03  - Changed file extensions from .asp to .page
*    Chamlk       2003-10-29  - Call Id 106316 - Modified function createCustomer()
*    ARWILK       031222        Edge Developments - (Removed clone and doReset Methods)
*    BUNILK       2004-01-30    Edge Developments - (Remove uneccessary global variables, Enhance Performance)
*    NAMELK      2004-11-08    Non Standard and Duplicated Translation Tags Corrected. 
*    NEKOLK      2006-03-01   Call 135708.Modified createCustomer()  and run().
*    RANFLK      2006-03-03   Call 135710 Modified the predefine()
*    ILSOLK      060817       Set the MaxLength of Address1 as 100.
*    AMNILK      070719       Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class NewCustomerDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.NewCustomerDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
        private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPField f;
        
	private ASPField fVISITADDRESS1;
	private ASPField fVISITADDRESS2;
	private ASPField fVISITADDRESS3;
	private ASPField fVISITADDRESS4;
	private ASPField fVISITADDRESS5;
	private ASPField fVISITADDRESS6;
	private ASPField fVISITADDRESS7;
	private ASPField fVISITADDRESS8;

	private ASPField fDELIVERYADDRESS1;
	private ASPField fDELIVERYADDRESS2;
	private ASPField fDELIVERYADDRESS3;
	private ASPField fDELIVERYADDRESS4;
	private ASPField fDELIVERYADDRESS5;
	private ASPField fDELIVERYADDRESS6;
	private ASPField fDELIVERYADDRESS7;
	private ASPField fDELIVERYADDRESS8;
        
	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String newWinFlag;
	private String selfCloseFlag;
	private String newCustId;
	private ASPTransactionBuffer trans;
	private String callingUrl;
	private String sCompany;
	private String sQuotationId;
	private ASPBuffer buffer;
        private ASPCommand cmd;
        private String calling_url;
        private boolean closeWindow;
        private boolean closeCallWin;


	//===============================================================
	// Construction 
	//===============================================================
	public NewCustomerDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		newWinFlag = selfCloseFlag;
		selfCloseFlag = newCustId;
		newCustId = "";
		ASPManager mgr = getASPManager();

                ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		callingUrl = ctx.getGlobal("CALLING_URL");
		sCompany = ctx.readValue("SITECOMP","");
		sQuotationId = ctx.readValue("QUOTID","");
                calling_url = ctx.readValue("CALLIAUR",calling_url);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());

		else if (mgr.dataTransfered())
		{
                    calling_url = ctx.getGlobal("CALLING_URL");
                    buffer = mgr.getTransferedData();
                    sCompany = buffer.getBuffer("DATA").getValue("SITECOMPANY");
                    sQuotationId = buffer.getBuffer("DATA").getValue("QUOTATION_ID");

                    prepare();
		}
                else if (!mgr.isEmpty(mgr.getQueryStringValue("QUOTATION_ID")))
                {
                    calling_url = ctx.getGlobal("CALLING_URL");

                    sCompany = mgr.readValue("SITECOMPANY");
                    sQuotationId = mgr.readValue("QUOTATION_ID");
                    prepare();
                }

		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		
		ctx.writeValue("SITECOMP",sCompany);
		ctx.writeValue("QUOTID",sQuotationId);
                ctx.writeValue("CALLIAUR",calling_url);
        }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//---------------------------  UTILITY FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void  prepare()
	{
            ASPManager mgr = getASPManager();

            cmd = trans.addCustomFunction("GETCCODE","COMPANY_API.Get_Country","COUNTRYCODE");
            cmd.addParameter("COMPANY",sCompany);     

            trans = mgr.perform(trans);
            String contcode = trans.getValue("GETCCODE/DATA/COUNTRYCODE");

                
            ASPBuffer buff = mgr.newASPBuffer();
            buff.addFieldItem("COMPANY",sCompany);
                
            headset.addRow(buff);
	}


//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void  createCustomer()
	{
		ASPManager mgr = getASPManager();
                if (headlay.isMultirowLayout())
                    headset.goTo(headset.getRowSelected());
                else
                    headset.selectRow(); 
		headset.changeRow();

		if (!mgr.isEmpty(mgr.readValue("CUSTOMERID")))
		{
			String custId = mgr.readValue("CUSTOMERID");
			ASPCommand cmd = trans.addCustomCommand("CREATECUST","Work_Order_Quotation_API.Create_Customer");
			cmd.addParameter("CUSTOMERID",mgr.readValue("CUSTOMERID"));
			cmd.addParameter("CUSTOMERGROUP",mgr.readValue("CUSTOMERGROUP"));
			cmd.addParameter("NAME",mgr.readValue("NAME"));
			cmd.addParameter("CUSTREF",mgr.readValue("CUSTREF"));
			cmd.addParameter("COUNTRYCODE",mgr.readValue("COUNTRYCODE"));
			cmd.addParameter("VISITADDRESS1",mgr.readValue("VISITADDRESS1"));
			cmd.addParameter("VISITADDRESS2",mgr.readValue("VISITADDRESS2"));
			cmd.addParameter("VISITADDRESS3",mgr.readValue("VISITADDRESS3"));
			cmd.addParameter("VISITADDRESS4",mgr.readValue("VISITADDRESS4"));
			cmd.addParameter("VISITADDRESS5",mgr.readValue("VISITADDRESS5"));
			cmd.addParameter("VISITADDRESS6",mgr.readValue("VISITADDRESS6"));
			cmd.addParameter("DELIVERYADDRESS1",mgr.readValue("DELIVERYADDRESS1"));
			cmd.addParameter("DELIVERYADDRESS2",mgr.readValue("DELIVERYADDRESS2"));
			cmd.addParameter("DELIVERYADDRESS3",mgr.readValue("DELIVERYADDRESS3"));
			cmd.addParameter("DELIVERYADDRESS4",mgr.readValue("DELIVERYADDRESS4"));
			cmd.addParameter("DELIVERYADDRESS5",mgr.readValue("DELIVERYADDRESS5"));
			cmd.addParameter("DELIVERYADDRESS6",mgr.readValue("DELIVERYADDRESS6"));
			cmd.addParameter("COMPANY",sCompany);
			cmd.addParameter("DISCOUNTCLASS",mgr.readValue("DISCOUNTCLASS"));
			cmd.addParameter("CURRENCY",mgr.readValue("CURRENCY"));
			cmd.addParameter("PAYMENTTERM",mgr.readValue("PAYMENTTERM"));
			cmd.addParameter("VATCODE",mgr.readValue("VATCODE"));
			
			cmd = trans.addCustomFunction("CREATEDCUST","CUSTOMER_INFO_API.Check_Exist","CREATED_CUST");
			cmd.addParameter("CUSTOMERID",mgr.readValue("CUSTOMERID"));

			trans = mgr.perform(trans);
			String sCreated = trans.getValue("CREATEDCUST/DATA/CREATED_CUST");

			if ("TRUE".equals(sCreated))
			{
				trans.clear();
				cmd = trans.addCustomCommand("MODIFYCUST","Work_Order_Quotation_API.Modify_Customer");
				cmd.addParameter("PARENT_QUOTATION",sQuotationId);
				cmd.addParameter("CUSTOMERID",custId);
				trans = mgr.perform(trans);
                               
                                closeCallWin = true;
		        }
		}
	}


        public void cancel()
        {
        
        closeWindow = true;
         }


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("COMPANY");
		f.setSize(11);
		f.setMandatory();
		f.setUpperCase();
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("CUSTOMERID");
		f.setSize(11);
		f.setMandatory();
		f.setLabel("PCMWNEWCUSTOMERDLGCUSTOMERID: Customer No");
		f.setFunction("''");
		f.setUpperCase();
		f.setInsertable();

		f = headblk.addField("CUSTOMERGROUP");
		f.setSize(11);
		f.setDynamicLOV("CUSTOMER_GROUP",600,445);
		f.setLabel("PCMWNEWCUSTOMERDLGCUSTOMERGROUP: Customer Group");
		f.setFunction("''");              
		f.setUpperCase();

		f = headblk.addField("NAME");
		f.setSize(47);
		f.setMandatory();
		f.setLabel("PCMWNEWCUSTOMERDLGNAME: Name");
		f.setFunction("''");

		f = headblk.addField("CUSTREF");
		f.setSize(16);
		f.setLabel("PCMWNEWCUSTOMERDLGCUSTREF: Reference");
		f.setFunction("''");

		f = headblk.addField("COUNTRYCODE");
		f.setSize(6);
		f.setDynamicLOV("ISO_COUNTRY",600,445);
		f.setLabel("PCMWNEWCUSTOMERDLGCOUNTRYCODE: Country");
		f.setFunction("''");
		f.setUpperCase();


		fVISITADDRESS1 = headblk.addField("VISITADDRESS1").
						 setSize(29).
						 setFunction("''").
                                                 setMaxLength(100).
						 setLabel("PCMWNEWCUSTOMERDLGADDRESS11: Address 1"); 

		fVISITADDRESS2 = headblk.addField("VISITADDRESS2").
						 setSize(25).
						 setFunction("''").
						 setLabel("PCMWNEWCUSTOMERDLGADDRESS12: Address 2"); 

		fVISITADDRESS3 = headblk.addField("VISITADDRESS3").
						 setSize(29).
						 setFunction("''").
						 setLabel("PCMWNEWCUSTOMERDLGADDRESS13: Address 3"); 

		fVISITADDRESS4 = headblk.addField("VISITADDRESS4").
						 setSize(25).
						 setFunction("''").
						 setLabel("PCMWNEWCUSTOMERDLGADDRESS14: Address 4"); 

		fVISITADDRESS5 = headblk.addField("VISITADDRESS5").
						 setSize(29).
						 setFunction("''").
						 setLabel("PCMWNEWCUSTOMERDLGADDRESS15: Address 5"); 

		fVISITADDRESS6 = headblk.addField("VISITADDRESS6").
						 setSize(25).
						 setFunction("''").
						 setLabel("PCMWNEWCUSTOMERDLGADDRESS16: Address 6");

		fVISITADDRESS7 = headblk.addField("VISITADDRESS7").
						 setSize(25).
						 setFunction("''").
						 setLabel("PCMWNEWCUSTOMERDLGADDRESS17: Address 7"); 

		fVISITADDRESS8 = headblk.addField("VISITADDRESS8").
						 setSize(25).
						 setFunction("''").
						 setLabel("PCMWNEWCUSTOMERDLGADDRESS18: Address 8"); 

		fDELIVERYADDRESS1 = headblk.addField("DELIVERYADDRESS1").
							setSize(29).
							setFunction("''").
							setLabel("PCMWNEWCUSTOMERDLGADDRESS11: Address 1"); 

		fDELIVERYADDRESS2 = headblk.addField("DELIVERYADDRESS2").
							setSize(25).
							setFunction("''").
							setLabel("PCMWNEWCUSTOMERDLGADDRESS12: Address 2"); 

		fDELIVERYADDRESS3 = headblk.addField("DELIVERYADDRESS3").
							setSize(29).
							setFunction("''").
							setLabel("PCMWNEWCUSTOMERDLGADDRESS13: Address 3"); 

		fDELIVERYADDRESS4 = headblk.addField("DELIVERYADDRESS4").
							setSize(25).
							setFunction("''").
							setLabel("PCMWNEWCUSTOMERDLGADDRESS14: Address 4"); 

		fDELIVERYADDRESS5 = headblk.addField("DELIVERYADDRESS5").
							setSize(29).
							setFunction("''").
							setLabel("PCMWNEWCUSTOMERDLGADDRESS15: Address 5"); 

		fDELIVERYADDRESS6 = headblk.addField("DELIVERYADDRESS6").
							setSize(25).
							setFunction("''").
							setLabel("PCMWNEWCUSTOMERDLGADDRESS16: Address 6"); 

		fDELIVERYADDRESS7 = headblk.addField("DELIVERYADDRESS7").
							setSize(25).
							setFunction("''").
							setLabel("PCMWNEWCUSTOMERDLGADDRESS17: Address 7"); 

		fDELIVERYADDRESS8 = headblk.addField("DELIVERYADDRESS8").
							setSize(25).
							setFunction("''").
							setLabel("PCMWNEWCUSTOMERDLGADDRESS18: Address 8"); 


		f = headblk.addField("PAYMENTTERM");
		f.setSize(6);
		f.setDynamicLOV("PAYMENT_TERM","COMPANY",600,445);
		f.setLabel("PCMWNEWCUSTOMERDLGPAYMENTTERM: Payment Term");
		f.setFunction("''");
		f.setUpperCase();
		f.setMandatory();

		f = headblk.addField("VATCODE");
		f.setSize(6);
		f.setDynamicLOV("STATUTORY_FEE","COMPANY",600,445);
		f.setLabel("PCMWNEWCUSTOMERDLGVATCODE: Vat Code");
		f.setFunction("''");    
		f.setMandatory();

		f = headblk.addField("DISCOUNTCLASS", "Number");
		f.setSize(6);
		f.setLabel("PCMWNEWCUSTOMERDLGDISCOUNTCLASS: Discount Type");
		f.setDynamicLOV("SALES_DISCOUNT_TYPE",600,445);
		f.setFunction("''");

		f = headblk.addField("CURRENCY");
		f.setSize(6);
		f.setDynamicLOV("ISO_CURRENCY",600,445);
		f.setLabel("PCMWNEWCUSTOMERDLGCURRENCY: Currency");
		f.setFunction("''");
		f.setUpperCase();
		f.setMandatory();

		f = headblk.addField("CREATED_CUST");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("PARENT_QUOTATION");
		f.setFunction("''");
		f.setHidden();

		f.setUpperCase();
		headblk.setView("DUAL");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.defineCommand(headbar.SAVERETURN,"createCustomer","checkHeadFields(-1)"); 
		headbar.defineCommand(headbar.CANCELNEW,"cancel");
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELNEW);
		headbar.disableCommand(headbar.SAVENEW);
		headbar.disableCommand(headbar.FORWARD);
		headbar.disableModeLabel();

		headtbl = mgr.newASPTable(headblk);           

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.NEW_LAYOUT);
		headlay.setEditable();

		headlay.defineGroup("","CUSTOMERID,CUSTOMERGROUP,NAME,CUSTREF,COUNTRYCODE",false,true);
		headlay.defineGroup(mgr.translate("PCMWNEWCUSTOMERDLGGRPLABEL1: Addresses"),"VISITADDRESS1,VISITADDRESS2,VISITADDRESS3,VISITADDRESS4,VISITADDRESS5,VISITADDRESS6,DELIVERYADDRESS1,DELIVERYADDRESS2,DELIVERYADDRESS3,DELIVERYADDRESS4,DELIVERYADDRESS5,DELIVERYADDRESS6",true,true);
		headlay.defineGroup(mgr.translate("PCMWNEWCUSTOMERDLGGRPLABEL2: Conditions"),"PAYMENTTERM,VATCODE,DISCOUNTCLASS,CURRENCY",true,true);
	        headlay.setAddressFieldList(fVISITADDRESS1,fVISITADDRESS2,fVISITADDRESS3,fVISITADDRESS4,fVISITADDRESS5,fVISITADDRESS6,fVISITADDRESS7,fVISITADDRESS8,"PCMWNEWCUSTOMERDLG: Visit Address","ifs.pcmw.LocalizedPcmwAddress");
		headlay.setAddressFieldList(fDELIVERYADDRESS1,fDELIVERYADDRESS2,fDELIVERYADDRESS3,fDELIVERYADDRESS4,fDELIVERYADDRESS5,fDELIVERYADDRESS6,fDELIVERYADDRESS7,fDELIVERYADDRESS8,"PCMWNEWCUSTOMERDLG2: Delivary Address","ifs.pcmw.LocalizedPcmwAddress");

                enableConvertGettoPost();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWNEWCUSTOMERDLGMASTERTITLE: New Customer";
	}

	protected String getTitle()
	{
		return "PCMWNEWCUSTOMERDLGMASTERTITLE: New Customer";
	}

        protected void printContents() throws FndException		   
	{
		ASPManager mgr = getASPManager();
                appendToHTML(headlay.show());

                appendDirtyJavaScript("   if (");
                appendDirtyJavaScript(closeWindow);
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript(" window.close();\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("   if (");
                appendDirtyJavaScript(closeCallWin);
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");

                appendDirtyJavaScript("     \n\nwindow.opener.location='"+mgr.encodeStringForJavascript(calling_url)+"';\n\n"); //XSS_Safe AMNILK 20070718
                appendDirtyJavaScript("     self.close();\n");
                appendDirtyJavaScript(" window.close();\n");
                appendDirtyJavaScript("}\n");


               // appendDirtyJavaScript("      window.opener.document.form.submit();\n");
               // appendDirtyJavaScript("      window.close();\n");
                //appendDirtyJavaScript("if('");
	       // appendDirtyJavaScript(closeCallWin);
	       // appendDirtyJavaScript(")\n");
	       // appendDirtyJavaScript("{ \n");
	       // appendDirtyJavaScript("     \n\nwindow.opener.location='"+calling_url+"';\n\n");
	       // appendDirtyJavaScript("     self.close();\n");
	       // appendDirtyJavaScript("} \n");


        }
}
