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
*  File        : ActiveWorkOrderDlg.java 
*  ASP2JAVA Tool  2001-03-07  - Created Using the ASP file ActiveWorkOrderDlg.asp
*  Modified    :  2001-03-07  BUNI    Corrected some conversion errors.
*                 2001-06-13  CHCR    Modified overwritten validations.
*                 2002-12-04  GACO    Set Max Length of MCH_CODE to 100
*                 031218      ARWILK  Edge Developments - (Removed clone and doReset Methods)
*                 2004-01-19  SHAFLK Bug Id 41815,Removed Java dependencies. 
*                 2004-03-25  SAPRLK  Merged with SP1.
*                 060803      JEWILK  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
* 	          060807      AMNILK  Merged Bug Id: 58214.
*                 070504      BUNILK  Implemented MTIS907 New Service Contract - Services changes.
*                 070530      AMDILK  Call Id 144903: Inserted Contract id and lineno to the head block and handle all the validations
*                 070605      AMDILK  Modified preDefine()
*                 070607      AMDILK  Call Id 145865: Inserted new service contract information; Contrat name, line description
* -------------------------------APP7.5 SP1---------------------------------------------
* 071219   NIJALK   Bug 69819, Removed method calls to Customer_Agreement_API.Get_Description.
* 090708   HARPLK   Bug 84436, Modified preDefine(),Validate().
* --------------------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveWorkOrderDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveWorkOrderDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPBlock sysblk;
   //Bug 84436,Start
    private ASPField f;
    //Bug 84436, End   

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String wo_no;
	private String mch_code;
	private String mch_code_contract;
	private String work_type_id;
	private String agreement_id;
	private String oldcustomerno;
	private String oldcustomerno_tag;
	private String oldcustname;
	private String oldcustname_tag;
	private String contract;
	private String customer_no;
	private String customer_no_tag;
	private String newcustname;
	private String work_type_id_tag;
	private String agreementdesc;
	private String mch_code_tag;
	private String mchname;
	private String mchname_tag;
	private String stragreementid;
	private String strworktypeid;
	private String strmchcode;
	private String strcontract;
	private String valid2;
	private String sWoNo;
	private String strTodate;
	private String strcustomerno;
	private ASPTransactionBuffer trans;
	private String calling_url;
	private ASPBuffer buff;
	private ASPBuffer row;
	private ASPTransactionBuffer secBuff;
	private ASPCommand cmd;
	private ASPQuery q;
	private String isageementId;
	private String scontract;
	private String scustomerNo;
	private String sagreementId;
	private String oldcustomerno1;
	private String StrAgreementId;
	private String agreementdesc1;
	private String newcustname1;
	private String oldcustname1;
	private String mchname1;
	private String sTodate;
	private String sContractId;
	private String sLineNo;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveWorkOrderDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		wo_no         =  "";
		mch_code      =  "";
		mch_code_contract = "";
		work_type_id  =  "";
		agreement_id  =  "";
		oldcustomerno =  "";
		oldcustomerno_tag =  "";
		oldcustname   = "";
		contract      =  "";
		customer_no   = "";
		customer_no_tag = "";
		newcustname   = "";
		work_type_id_tag = "";
		agreementdesc = "";
		mch_code_tag  =  "";
		mchname       = "";
		mchname_tag   = "";
		stragreementid= "";
		strworktypeid = "";
		strmchcode    = "";
		strcontract   = "";
		valid2        =  "";
		strcontract   = "";
		sWoNo         = "";
		strTodate     = "";
		strcustomerno = "";
		sContractId   = "";
		sLineNo       = "";

		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		calling_url=ctx.getGlobal("CALLING_URL");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
		{
			buff     = mgr.getTransferedData();

			row  = buff.getBufferAt(0); 
			wo_no    = row.getValue("WO_NO");
			row  = buff.getBufferAt(1); 
			mch_code = row.getValue("MCH_CODE");
			row  = buff.getBufferAt(2); 
			work_type_id= row.getValue("WORK_TYPE_ID");
			row  = buff.getBufferAt(3); 
			agreement_id = row.getValue("AGREEMENT_ID");
			row  = buff.getBufferAt(4); 
			oldcustomerno = row.getValue("CUSTOMER_NO");
			row  = buff.getBufferAt(5); 
			contract = row.getValue("CONTRACT");
			row  = buff.getBufferAt(6);
			mch_code_contract = row.getValue("MCH_CODE_CONTRACT");

			okFind();
			startup();
		}

		wo_no         = ctx.readValue("CTX1",wo_no);
		mch_code      = ctx.readValue("CTX2",mch_code);
		work_type_id  = ctx.readValue("CTX3",work_type_id);
		oldcustomerno = ctx.readValue("CTX5",oldcustomerno);
		contract      = ctx.readValue("CTX6",contract);
		strTodate     = ctx.readValue("CTX7",strTodate );

		ctx.writeValue("CTX1",wo_no);
		ctx.writeValue("CTX2",mch_code);
		ctx.writeValue("CTX3",work_type_id );
		ctx.writeValue("CTX5",oldcustomerno );
		ctx.writeValue("CTX6",contract);
		ctx.writeValue("CTX7",strTodate);

	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

	public boolean  checksec( String method,int ref)
	{
		ASPManager mgr = getASPManager();
		String isSecure[] = new String[6] ;

		isSecure[ref] = "false" ; 
		String splitted[] = split(method, "."); 

		secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery(splitted[0],splitted[1]);

		secBuff = mgr.perform(secBuff);

		if (secBuff.getSecurityInfo().itemExists(method))
		{
			isSecure[ref] = "true";
			return true; 
		}
		else
			return false;
	}


	public void  startup()
	{
		ASPManager mgr = getASPManager();

		String isSecure[] = new String[6] ;

		trans.clear(); 
		agreement_id = ctx.readValue("CTX4",agreement_id);

		cmd = trans.addCustomFunction("NAME","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCHNAME");
		cmd.addParameter("CONTRACT",contract);
		cmd.addParameter("MCH_CODE",mch_code);

		cmd = trans.addCustomFunction("OLDCUNAME","Customer_Info_API.Get_Name","OLDCUSTNAME");
		cmd.addParameter("OLDCUSTOMERNO",oldcustomerno);

		q = trans.addQuery("MASTER1","DUAL","SYSDATE","","");

		//commented by amdilk
		/*cmd = trans.addCustomFunction("AGREEMENT","Cust_Agreement_Object_API.Get_Valid_Agreement","IS_AGREEMENT_ID");
		cmd.addParameter("CUSTOMER_NO",customer_no);
		cmd.addParameter("CONTRACT",contract);
		cmd.addParameter("MCH_CODE",mch_code);
		cmd.addParameter("WORK_TYPE_ID",work_type_id);
		cmd.addReference("SYSDATE","MASTER1/DATA");*/

		trans = mgr.perform(trans);

		mchname = trans.getValue("NAME/DATA/MCHNAME");
		strTodate = trans.getValue("MASTER1/DATA/SYSDATE");

		oldcustname= trans.getValue("OLDCUNAME/DATA/OLDCUSTNAME");
		if (!mgr.isEmpty(isageementId))
		{
			agreement_id= isageementId;
		}
		else
		{
			agreement_id="";
			agreementdesc=""; 
		}
		strcontract = contract;
		ctx.writeValue("CTX7",strTodate);

		row = headset.getRow();

		row.setValue("OLDCUSTOMERNO",oldcustomerno);
		row.setValue("OLDCUSTNAME",oldcustname);
		row.setValue("MCH_CODE",mch_code);
		row.setValue("MCHNAME",mchname);
		row.setValue("WORK_TYPE_ID",work_type_id);
		row.setValue("CONTRACT", contract);
		row.setValue("MCH_CODE_CONTRACT", mch_code_contract);

		headset.setRow(row);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		String txt = "";

		if ("CUSTOMER_NO".equals(val))
		{
			cmd = trans.addCustomFunction("NEWCUNAME","CUSTOMER_INFO_API.Get_Name","NEWCUSTNAME");
			cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO",""));

			trans = mgr.validate(trans);

			String newcusname= trans.getValue("NEWCUNAME/DATA/NEWCUSTNAME");

			txt = (mgr.isEmpty(newcusname) ? "" :newcusname) + "^" ;

			mgr.responseWrite(txt);
		}
                else if ("CONTRACT_ID".equals(val))
		{
		    String sContractId   = mgr.readValue("CONTRACT_ID");
		    String sLineNo       = mgr.readValue("LINE_NO");
		    String sCustNo       = null;
		    String sCoordinator  = null;
		    String sAgreementId  = null;
		    String sAgreeDesc    = null;
                    String sContractName = null;
		    String sLineDesc     = null;
		    
		    if (sContractId.indexOf("^") > -1)
		    {
			String strAttr = sContractId;
			sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
			sLineNo = strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());
		    }
	
		    if (checksec("Sc_Service_Contract_API.Get_Customer_Id",1))
		    {
		       cmd = trans.addCustomFunction("GETCUSTOMERNO", "Sc_Service_Contract_API.Get_Customer_Id", "CUSTOMER_NO");
		       cmd.addParameter("CONTRACT_ID", sContractId);
		    }
		    
                    if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1))
		    {
		       cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
		       cmd.addParameter("CONTRACT_ID", sContractId);
		    }
	
		    if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1))
		    {
		       cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
		       cmd.addParameter("CONTRACT_ID", sContractId);
		       cmd.addParameter("LINE_NO", sLineNo);
		    }

		    trans = mgr.validate(trans);
	
		    sCustNo      = trans.getValue("GETCUSTOMERNO/DATA/CUSTOMER_NO");
		    sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
	            sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
		    
		    txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sLineNo)?"":sLineNo)+ "^" + 
			   (mgr.isEmpty(sCustNo)?"":sCustNo)+ "^" + (mgr.isEmpty(sContractName)?"":sContractName) + "^" +
		           (mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" ;

		    mgr.responseWrite(txt);
		}

		else if ("LINE_NO".equals(val))
		{
		    String sContractId   = mgr.readValue("CONTRACT_ID");
		    String sLineNo       = mgr.readValue("LINE_NO");
		    String sContractName = null;
		    String sLineDesc     = null;
		
		    if (sLineNo.indexOf("^") > -1)
		    {
			String strAttr = sLineNo;
			sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
			sLineNo =  strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());                
		    }

		    if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1))
		    {
		       cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
		       cmd.addParameter("CONTRACT_ID", sContractId);
		    }
	
		    if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1))
		    {
		       cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
		       cmd.addParameter("CONTRACT_ID", sContractId);
		       cmd.addParameter("LINE_NO", sLineNo);
		    }
        
		    trans = mgr.validate(trans);
		
		    sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
	            sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
		    
		    txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sLineNo)?"":sLineNo)+ "^" + 
			   (mgr.isEmpty(sContractName)?"":sContractName) + "^" + (mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" ;

		    mgr.responseWrite(txt);
		}

		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  ok()
	{
		ASPManager mgr = getASPManager();

		String sIsValidContract = null;
		String sIsValidCustomer = null;
		scustomerNo     = null;

		scontract   = ctx.readValue("CTX6",contract);
		contract    = ctx.readValue("CTX1",contract);
		scustomerNo = ctx.readValue("CTX5",customer_no);
		sWoNo       = ctx.readValue("CTX1",wo_no);
		sagreementId = ctx.readValue("CTX4",agreement_id);

		oldcustomerno1 = mgr.readValue("OLDCUSTOMERNO","");
		StrAgreementId = mgr.readValue("AGREEMENT_ID","");
		agreementdesc1 = mgr.readValue("AGREEMENTDESC","");
		newcustname1   = mgr.readValue("NEWCUSTNAME","");
		oldcustname1   = mgr.readValue("OLDCUSTNAME","");
		mchname1       = mgr.readValue("MCHNAME","");
		scustomerNo    = mgr.readValue("CUSTOMER_NO","");
		sContractId    = mgr.readValue("CONTRACT_ID", "");
		sLineNo        = mgr.readValue("LINE_NO", "");

		if (!mgr.isEmpty(sContractId) || !mgr.isEmpty(sLineNo))
		{
                        trans.clear();

		        if ( checksec("Customer_Info_API.Check_Exist",1) )
			{
			    cmd = trans.addCustomFunction("VALIDCUSTOMER", "Customer_Info_API.Check_Exist", "ISVALIDCUSTOMER");
			    cmd.addParameter("CUSTOMER_NO", scustomerNo);

			}

			if ( checksec("Psc_Contr_Product_Api.Is_Active_Valid_Serv_Line",1) )
			{
			   cmd = trans.addCustomFunction( "VALIDCONTRACT", "Psc_Contr_Product_Api.Is_Active_Valid_Serv_Line","ISVALIDCONTRACT");
			   cmd.addParameter("CONTRACT_ID",sContractId);
			   cmd.addParameter("LINE_NO", sLineNo);  
			   cmd.addParameter("CUSTOMER_NO",scustomerNo);
			   cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE",""));
			   cmd.addParameter("MCH_CODE_CONTRACT",mgr.readValue("MCH_CODE_CONTRACT",""));      
			}
			
			trans = mgr.perform(trans);
			
			sIsValidCustomer = trans.getValue("VALIDCONTRACT/DATA/ISVALIDCUSTOMER");
			sIsValidContract = trans.getValue("VALIDCONTRACT/DATA/ISVALIDCONTRACT");

			trans.clear();
			
		}


		if ( "FALSE".equals(sIsValidContract) ) { 
                   mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDERINVDCON: The Service Contract Line object does not exist."));
		}
		else if ( "FALSE".equals(sIsValidCustomer) ) {
                   mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDERINVDCUS: The customer id does not exist."));
		}
		else if ( ( sIsValidContract == null ) || ( "TRUE".equals(sIsValidContract)) ) 
		{
		    if ( ! mgr.isEmpty(scustomerNo) )
		    {
			    trans.clear();
			    cmd = trans.addCustomCommand("CHANGCUS", "Active_Work_Order_API.Change_Customer");
			    cmd.addParameter("WO_NO",sWoNo);
			    cmd.addParameter("CUSTOMER_NO",scustomerNo);
			    cmd.addParameter("AGREEMENT_ID",StrAgreementId);
			    cmd.addParameter("CONTRACT_ID",sContractId);
			    cmd.addParameter("LINE_NO",sLineNo);
			    trans = mgr.perform(trans);
			    trans.clear();
    
			    row = headset.getRow();
    
			    row.setValue("OLDCUSTOMERNO",oldcustomerno1);
			    row.setValue("OLDCUSTNAME",oldcustname1);
			    row.setValue("MCHNAME",mchname1);
			    row.setValue("CUSTOMER_NO",scustomerNo);
			    row.setValue("AGREEMENT_ID",StrAgreementId);
			    row.setValue("AGREEMENTDESC",agreementdesc1);
			    row.setValue("NEWCUSTNAME",newcustname1);
    
			    headset.setRow(row);
    
			    sWoNo          = ctx.readValue("CTX1",sWoNo);
			    strmchcode     = ctx.readValue("CTX2",strmchcode);
			    strworktypeid  = ctx.readValue("CTX3",strworktypeid);
			    stragreementid = ctx.readValue("CTX4",stragreementid);
			    strcustomerno  = ctx.readValue("CTX5",strcustomerno);
			    strcontract = ctx.readValue("CTX6",strcontract);
			    sContractId = ctx.readValue("CONTRACTID", sContractId );
			    sLineNo     = ctx.readValue("LINENO", sLineNo );
    
			    mgr.redirectTo(calling_url+"?WO_NO="+sWoNo+"&MCH_CODE="+strmchcode+"&WORK_TYPE_ID="+strworktypeid+"&AGREEMENT_ID="+
					   stragreementid + "&CONTRACT=" + strcontract + "&CONTRACT_ID=" + sContractId + "&LINE_NO=" + sLineNo );
    
		    }
		    else if ( mgr.isEmpty(scustomerNo) )
		    {
			mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDERNOCUS: Please enter a value for the new customer."));
		    }


		}
                
                agreement_id =StrAgreementId;  
		agreementdesc=agreementdesc1;
		newcustname=newcustname1;
		oldcustname = oldcustname1;
		oldcustomerno =oldcustomerno1;
		mchname=mchname1;
		contract = strcontract;
		customer_no = scustomerNo;
	}

	public void  cancel()
	{
		ASPManager mgr = getASPManager();

		sWoNo         =ctx.readValue("CTX1",sWoNo);
		strmchcode    = ctx.readValue("CTX2",strmchcode);
		strworktypeid = ctx.readValue("CTX3",strworktypeid);
		stragreementid= ctx.readValue("CTX4",stragreementid);
		strcustomerno = ctx.readValue("CTX5",strcustomerno);
		strcontract   = ctx.readValue("CTX6",strcontract);
                sContractId   = ctx.readValue("CONTRACTID", sContractId );
                sLineNo       = ctx.readValue("LINENO", sLineNo );

		mgr.redirectTo(calling_url+"?WO_NO="+sWoNo+"&MCH_CODE="+strmchcode+"&WORK_TYPE_ID="+strworktypeid+"&AGREEMENT_ID="+stragreementid+"&CONTRACT="+strcontract);

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
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setFunction("''").
		setHidden();

		headblk.addField("OBJVERSION").
		setFunction("''").
		setHidden();

		headblk.addField("OLDCUSTOMERNO").
		setSize(8).
		setLabel("PCMWACTIVEWORKORDERDLGOLDCUSTOMERNO: Old Customer No").
		setFunction("''").
		setReadOnly();

		headblk.addField("OLDCUSTNAME").
		setSize(26).
		setLabel("PCMWACTIVEWORKORDERDLGOLDCUSTOMERNAME: Old Customer Name").
		setFunction("''").
		setReadOnly();

		headblk.addField("CUSTOMER_NO").
		setSize(8).
		setDynamicLOV("CUSTOMER_INFO","CUSTOMER_NO",600,450).
		setLabel("PCMWACTIVEWORKORDERDLGCUSTOMER_NO: New Customer No").
		setCustomValidation("CUSTOMER_NO","NEWCUSTNAME").
		setFunction("''").
		setLOVProperty("TITLE",mgr.translate("PCMWACTIVEWORKORDERDLGNEWCUNO: List of New Customer No"));

		headblk.addField("NEWCUSTNAME").
		setSize(26).
		setLabel("PCMWACTIVEWORKORDERDLGNEWCUSTOMERNAME: New Customer Name").
		setFunction("''").
		setReadOnly();
                                                             
                headblk.addField("CONTRACT_ID").
                setUpperCase().
                setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,WORK_TYPE_ID").
                setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,LINE_NO,CUSTOMER_NO,CONTRACT_NAME,LINE_DESC").
                setLabel("PCMWACTIVEWORKORDERDLGCONTRACTID: Contract ID").
		setFunction("''").
                setSize(15);
                
      //Bug 84436, Start
       f = headblk.addField("CONTRACT_NAME");         
      if (mgr.isModuleInstalled("SRVCON"))
         f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
     else
            f.setFunction("''");      
		f.setReadOnly();
		f.setLabel("PCMWACTIVEWORKORDERDLGCONTRACTNAME: Contract Name");
		f.setSize(15);
      //Bug 84436, End 
      
                headblk.addField("LINE_NO","Number").
                setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,WORK_TYPE_ID,CONTRACT_ID").
                setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,LINE_NO,CONTRACT_NAME,LINE_DESC").
                setLabel("PCMWACTIVEWORKORDERDLGLINENO: Line No").
		setFunction("''").
                setSize(10);
      
      //Bug 84436,Start
      f = headblk.addField("LINE_DESC");                  
		f.setDefaultNotVisible();      
      if(mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
      else
            f.setFunction("''");   
		f.setReadOnly();
		f.setLabel("PCMWACTIVEWORKORDERDLGLINEDESC: Description");
		f.setSize(15);
      //Bug 84436,End
      
		headblk.addField("AGREEMENT_ID").
		setSize(8).
		setReadOnly().
		setLabel("PCMWACTIVEWORKORDERDLGAGREEMENT_ID: Agreement Id").
		setHidden().
		setFunction("''");

		headblk.addField("AGREEMENTDESC").
		setSize(26).
		setLabel("PCMWACTIVEWORKORDERDLGAGREEMENTDESC: Agreement Description").
		setFunction("''").
                setHidden().
		setReadOnly();

		headblk.addField("MCH_CODE").
		setSize(15).
		setMaxLength(100).
		setFunction("''").
		setLabel("PCMWACTIVEWORKORDERDLGMCH_CODE: Object Id").
		setReadOnly();

		headblk.addField("MCH_CODE_CONTRACT").
		setSize(10).
		setMaxLength(100).
		setFunction("''").
		setHidden();

		headblk.addField("MCHNAME").
		setSize(26).
		setLabel("PCMWACTIVEWORKORDERDLGMCHNAME: Object Name").
		setFunction("''").
		setReadOnly();

		headblk.addField("WORK_TYPE_ID").
		setSize(8).
		setFunction("''").
		setLabel("PCMWACTIVEWORKORDERDLGWORK_TYPE_ID: Work Type").
		setUpperCase().
		setReadOnly();

		headblk.addField("WO_NO","Number").
		setSize(8).
		setFunction("''").
		setHidden();

		headblk.addField("CONTRACT").
		setSize(10).
		setFunction("''").
		setHidden();

		headblk.addField("MASTER1").
		setSize(10).
		setFunction("''").
		setHidden();

		headblk.addField("DUAL").
		setSize(10).
		setFunction("''").
		setHidden();

		headblk.addField("IS_AGREEMENT_ID").
		setSize(10).
		setFunction("''").
		setHidden();

		headblk.addField("ISVALIDCUSTOMER").
		setSize(10).
		setHidden().
		setFunction("''");

		headblk.addField("ISVALIDCONTRACT").
		setSize(10).
		setHidden().
		setFunction("''");

		headblk.setView("ACTIVE_WORK_ORDER");
		headblk.defineCommand("ACTIVE_WORK_ORDER_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"ok","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
		headtbl = mgr.newASPTable(headblk);

		headlay = headblk.getASPBlockLayout();
		headlay.setDialogColumns(2);
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.defineGroup(mgr.translate("PCMWACTIVEWORKORDERDLGGRPLABEL1: Change Customer"),"OLDCUSTOMERNO,OLDCUSTNAME,CUSTOMER_NO,NEWCUSTNAME,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,AGREEMENT_ID,AGREEMENTDESC,MCH_CODE,MCHNAME,WORK_TYPE_ID",true,true);
		headlay.setEditable();
		headlay.setSimple("OLDCUSTNAME");
		headlay.setSimple("NEWCUSTNAME");
		headlay.setSimple("AGREEMENTDESC");
		headlay.setSimple("MCHNAME");
		headlay.setSimple("CONTRACT_NAME");
		headlay.setSimple("LINE_DESC");

		sysblk = mgr.newASPBlock("SYS");

		sysblk.addField("SYSDATE").
		setSize(10).
		setFunction("''").
		setHidden();

                enableConvertGettoPost();

	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWACTIVEWORKORDERDLGTITLE: Change Customer for Work Order";
	}

	protected String getTitle()
	{
		return "PCMWACTIVEWORKORDERDLGTITLE: Change Customer for Work Order";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		appendDirtyJavaScript("function validateCustomerNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCustomerNo(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('CUSTOMER_NO',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('NEWCUSTNAME',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript(" window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CUSTOMER_NO'\n");
                appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i)));\n");
		appendDirtyJavaScript(" window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'CUSTOMER_NO',i,'New Customer No') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('NEWCUSTNAME',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");
	}
}
