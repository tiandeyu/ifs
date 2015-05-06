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
*  File        : PrintAuthReleaseCertificateWiz.java 
*  Created     : VAGULK  041004  Created.
*  Modified    :
*  NAMELK 041110 Duplicated Translation Tags Corrected.
*  ARWILK 041115 Replaced getContents with printContents. 
*  VAGULK 041116 Modified Finish(),Call ID 118854.
*  BUNILK 050704 Altered finish() and prepareForm() methods.
*  BUNILK 050714 Added new field Emplpyee ID to step 2. 
*  SHAFLK 070312 Bug 64068, Removed extra mgr.translate().   
*  AMDILK 070330 Merged bug id 64068
*  AMDILK 070404 Modified Predefine() to remove the extra line shown at runtime
*  AMNILK 070725 Eliminated XSS Security Vulnerability.
*  CHANLK 080206 Bug 70426, Change getPreviousValues.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PrintAuthReleaseCertificateWiz extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PrintAuthReleaseCertificateWiz");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;
	private ASPForm frm;
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
	private boolean step1;
	private boolean step2;
	private boolean step3;

	private int Step;
	private int Wizpage;

	private String CancelFlag;
	private String FinishFlag;
	private String WarningFlag;
	private String warningString;

	private ASPBuffer row;
	private String val;
	private ASPCommand cmd;
	private ASPBuffer data;
	private String sysDate;
	private String lsAttr;

	private boolean checkboxVal;
	private String frommaintProgId;
	private String frommaintProgRev;
	private String objid1;
	private String objversion1;

	private boolean showHtmlPart;
	private ASPBuffer buff;
	private boolean chkFlag;
	private String woNo;
	private String dbState;
	private String url;
	private String authoriztionNo;
	private String getFormType;
	private String authforDept;
	private String getState;
	private String rem1;
	private String rem2;
	private String rem3;
	private String rem4;
	private String rem5;
	private String employeeName;

	//===============================================================
	// Construction 
	//===============================================================
	public PrintAuthReleaseCertificateWiz(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();               

		fmt = mgr.newASPHTMLFormatter();
		ctx  = mgr.getASPContext();
		frm = mgr.getASPForm();
		trans = mgr.newASPTransactionBuffer();

		mgr.setPageExpiring();
		Step   = ctx.readNumber("STEP",1);

		woNo = ctx.readValue("WONO","");
		getFormType = ctx.readValue("GETFORMTYPE","");
		authforDept = ctx.readValue("AUTHFORDEPT","");
		getState = ctx.readValue("GETSTATE","");
		rem1 = ctx.readValue("REMARK1","");
		rem2 = ctx.readValue("REM2","");
		rem3 = ctx.readValue("REM3","");
		rem4 = ctx.readValue("REM4","");
		rem5 = ctx.readValue("REM5","");
		authoriztionNo = ctx.readValue("AUTHNO","");
		objid1 = ctx.readValue("OBJID1","");
		objversion1 = ctx.readValue("OBJVERSION1","");

		showHtmlPart = ctx.readFlag("CTSSHHTML",true);

		CancelFlag = ""; 


		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.buttonPressed("PREV"))
			prev();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WNDFLAG")))
		{
			if ("PrintAuthReleaseCertificateWiz".equals(mgr.readValue("WNDFLAG","")))
			{
				showHtmlPart = false;
			}
		}
		else if (mgr.buttonPressed("NXT"))
			nxt();
		else if (mgr.buttonPressed("FINISH"))
			finish();
		else if (mgr.buttonPressed("CANCEL"))
			cancel();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("STATUS_DB")))
		{
			woNo= mgr.getQueryStringValue("WO_NO");
			prepareForm();
		}
		else
		{
			startup();
		}

		adjust();

		ctx.writeNumber("STEP",Step);

		ctx.writeValue("WONO",woNo);
		ctx.writeValue("GETFORMTYPE",getFormType);
		ctx.writeValue("AUTHFORDEPT",authforDept);
		ctx.writeValue("GETSTATE",getState);

		ctx.writeValue("REMARK1",rem1);
		ctx.writeValue("REM2",rem2);
		ctx.writeValue("REM3",rem3);
		ctx.writeValue("REM4",rem4);
		ctx.writeValue("REM5",rem5);
		ctx.writeValue("AUTHNO",authoriztionNo);
		ctx.writeValue("OBJID1",objid1);
		ctx.writeValue("OBJVERSION1",objversion1);

	}


	public void  validate()
	{
                ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		String txt = null;
                String emplyeeId = "";
                String validationAttrAtr = "";
                String tempStr = "";

		if ("AUTHORIZATION_NO".equals(val))
		{
                        
			if (mgr.readValue("AUTHORIZATION_NO").indexOf("~") > -1)
			{
                                authoriztionNo = mgr.readValue("AUTHORIZATION_NO").substring(0,mgr.readValue("AUTHORIZATION_NO").indexOf("~"));
                                tempStr = mgr.readValue("AUTHORIZATION_NO").substring(mgr.readValue("AUTHORIZATION_NO").indexOf("~")+1,mgr.readValue("AUTHORIZATION_NO").length());
                                employeeName =  tempStr.substring(0,tempStr.indexOf("~"));
                                tempStr = tempStr.substring(tempStr.indexOf("~")+1,tempStr.length());
                                emplyeeId =  tempStr.substring(0,tempStr.length());
                                
				validationAttrAtr += (mgr.isEmpty(authoriztionNo)?"":authoriztionNo)+"^";
				validationAttrAtr += (mgr.isEmpty(employeeName)?"":employeeName)+"^";  
                                validationAttrAtr += (mgr.isEmpty(emplyeeId)?"":emplyeeId)+"^";  

			}
                        else
			{
				validationAttrAtr = mgr.readValue("AUTHORIZATION_NO")+"^^";
			}
                        mgr.responseWrite(validationAttrAtr);
                }
                if ("EMPLOYEE_ID".equals(val))
		{

                        if (mgr.readValue("EMPLOYEE_ID").indexOf("~") > -1)
                        {
                            authoriztionNo = mgr.readValue("EMPLOYEE_ID").substring(0,mgr.readValue("EMPLOYEE_ID").indexOf("~"));
        
        
                            tempStr = mgr.readValue("EMPLOYEE_ID").substring(mgr.readValue("EMPLOYEE_ID").indexOf("~")+1,mgr.readValue("EMPLOYEE_ID").length());
                            employeeName =  tempStr.substring(0,tempStr.indexOf("~"));
                            tempStr = tempStr.substring(tempStr.indexOf("~")+1,tempStr.length());
                            emplyeeId =  tempStr.substring(0,tempStr.length());
                                
                            validationAttrAtr += (mgr.isEmpty(authoriztionNo)?"":authoriztionNo)+"^";
                            validationAttrAtr += (mgr.isEmpty(employeeName)?"":employeeName)+"^";  
                            validationAttrAtr += (mgr.isEmpty(emplyeeId)?"":emplyeeId)+"^";  
        
                        }
                        else
                        {
                            validationAttrAtr = mgr.readValue("AUTHORIZATION_NO")+"^^";
                        }
        
                                
                        mgr.responseWrite(validationAttrAtr);
                }
		mgr.endResponse();
	}


//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

	public void  startup()
	{
		prepareForm();  
	}


	public void prepareForm()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		woNo= mgr.getQueryStringValue("WO_NO");

                
		cmd = trans.addCustomFunction("GETAUTHFORWO","Authority_Per_Department_api.Get_Authority_Id_For_Wo","AUTHORITY_ID_FOR_DEPT");
		cmd.addParameter("WO_NO",woNo);

		cmd = trans.addCustomFunction("GETFORMTYPE","Authority_Api.Get_Form_Type_Db","GET_FORM_TYPE");
		cmd.addReference("AUTHORITY_ID_FOR_DEPT","GETAUTHFORWO/DATA");

		trans = mgr.perform(trans);

		authforDept = trans.getValue("GETAUTHFORWO/DATA/AUTHORITY_ID_FOR_DEPT");
		getFormType = trans.getValue("GETFORMTYPE/DATA/GET_FORM_TYPE");  

                
		trans.clear();

		cmd = trans.addEmptyCommand("BLKONE","RELEASE_CERTIFICATE_API.New__", headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);

		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}


	public void  prev()
	{
		ASPManager mgr = getASPManager();

		Step = 1;
	}

	public void  nxt()
	{
		ASPManager mgr = getASPManager();
		String getclientStatus = mgr.readValue("STATUS");

		cmd = trans.addCustomFunction("GETSTATUSENCODE","release_certificate_status_api.encode","GET_RESULT1");
		cmd.addParameter("STATUS",getclientStatus);

		trans = mgr.perform(trans);

		String getStatus = trans.getValue("GETSTATUSENCODE/DATA/GET_RESULT1");

		if (!mgr.isEmpty(getStatus))
		{
			if ("OVERHAULED".equals(getStatus))
			{
				if ("FAA_FORM_8130-3".equals(getFormType))
				{
					Step = 2;

				}
				else if ("EASA_FORM_ONE".equals(getFormType))
				{
					Step = 3;
				}
			}
			else
			{
				if ("FAA_FORM_8130-3".equals(getFormType))
				{
					Step = 4;
				}
				else if ("EASA_FORM_ONE".equals(getFormType))
				{
					Step = 5;
				}
			}
		}

		getPreviousValues();


	}

	public void getPreviousValues()
	{
		ASPManager mgr = getASPManager();

		getState = mgr.readValue("STATUS");
//		Bug 70426, Start
		rem1 = mgr.readValue("REMARK1");
//		Bug 70426, End
		rem2 = mgr.readValue("REMARK2");
		rem3 = mgr.readValue("REMARK3");
		rem4 = mgr.readValue("REMARK4");
		rem5 = mgr.readValue("REMARK_LONG");

		headset.changeRow();

		row = headset.getRow();
		row.setValue("STATUS",getState);
		row.setValue("REMARK1",rem1);
		row.setValue("REMARK2",rem2);
		row.setValue("REMARK3",rem3);
		row.setValue("REMARK4",rem4);
		row.setValue("REMARK_LONG",rem5);

	}

	public void  cancel()
	{

		CancelFlag = "true";
	}


	public void getRadioValues()
	{
		ASPManager mgr = getASPManager();

	}

	public void printReleaseCertificate()
	{
		ASPManager mgr = getASPManager();

		//Web Alignment - multirow action
		int count = 0;
		ASPBuffer print;
		ASPBuffer printBuff;
		String attr1;
		String attr2;
		String attr3;
		String attr4;

		trans.clear();

		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
			count = headset.countSelectedRows();
		}
		else
		{
			headset.unselectRows();
			count = 1;

		}

		for (int i = 0;i < count; i++)
		{
			attr1 = "REPORT_ID" + (char)31 + "RELEASE_CERTIFICATE_REP" + (char)30;
			//attr2 = "WO_NO" + (char)31 + headset.getValue("WO_NO") + (char)30;
			attr2 = "WO_NO" + (char)31 + woNo + (char)30;
                        attr3 =  "";
			attr4 =  "";

			cmd = trans.addCustomCommand("PRNT" + i,"Archive_API.New_Client_Report");
			cmd.addParameter("ATTR0");                       
			cmd.addParameter("ATTR1",attr1);       
			cmd.addParameter("ATTR2",attr2);              
			cmd.addParameter("ATTR3",attr3);      
			cmd.addParameter("ATTR4",attr4);  

			if (headlay.isMultirowLayout())
				headset.next();
		}

		trans = mgr.perform(trans);

		if (headlay.isMultirowLayout())
			headset.setFilterOff();

		print = mgr.newASPBuffer();

		for (int i = 0;i < count;i++)
		{
			printBuff = print.addBuffer("DATA");
			printBuff.addItem("RESULT_KEY", trans.getValue("PRNT" + i + "/DATA/ATTR0"));
		}

		callPrintDlg(print,true);
	}

	public void  finish()
	{
		ASPManager mgr = getASPManager();
		FinishFlag = "false";
		String sAttr = "";

                
		if (Step == 2)
		{
			String conf1;

			if ("RBCONFIRMITY1".equals(mgr.readValue("RADIOWIZPAGE1")))
			{
				conf1 = "Y";
				authoriztionNo = mgr.readValue("AUTHORIZATION_NO");
				employeeName = mgr.readValue("EMPLOYEE_NAME");

				sAttr = sAttr   + "WO_NO" + (char)31 +woNo+ (char)30
						+ "STATUS" + (char)31 +getState+ (char)30
						+ "REMARK1" + (char)31 +rem1+ (char)30
						+ "REMARK2" + (char)31 +rem2+ (char)30
						+ "REMARK3" + (char)31 +rem3+ (char)30
						+ "REMARK4" + (char)31 +rem4+ (char)30
						+ "REMARK_LONG" + (char)31 +rem5+ (char)30
						+ "CONFORMITY_TO_DESIGN1" + (char)31 +'Y'+ (char)30
						+ "AUTHORIZATION_NO" + (char)31 +authoriztionNo+ (char)30
						+ "EMPLOYEE_NAME" + (char)31 +employeeName+ (char)30;
			}
			else if ("RBCONFIRMITY2".equals(mgr.readValue("RADIOWIZPAGE1")))
			{
				conf1 = "Y";
				authoriztionNo = mgr.readValue("AUTHORIZATION_NO");
				employeeName = mgr.readValue("EMPLOYEE_NAME");

				sAttr = sAttr   + "WO_NO" + (char)31 +woNo+ (char)30
						+ "STATUS" + (char)31 +getState+ (char)30
						+ "REMARK1" + (char)31 +rem1+ (char)30
						+ "REMARK2" + (char)31 +rem2+ (char)30
						+ "REMARK3" + (char)31 +rem3+ (char)30
						+ "REMARK4" + (char)31 +rem4+ (char)30
						+ "REMARK_LONG" + (char)31 +rem5+ (char)30
						+ "CONFORMITY_TO_DESIGN2" + (char)31 +'Y'+ (char)30
						+ "AUTHORIZATION_NO" + (char)31 +authoriztionNo+ (char)30
						+ "EMPLOYEE_NAME" + (char)31 +employeeName+ (char)30;
			}

		}


		if (Step == 3)
		{
			String conf2;

			if ("RBCONFIRMITY3".equals(mgr.readValue("AIRWORTHWIZPAGE2")))
			{
				conf2 = "Y";
				authoriztionNo = mgr.readValue("AUTHORIZATION_NO");
                                employeeName = mgr.readValue("EMPLOYEE_NAME");

				sAttr = sAttr   + "WO_NO" + (char)31 +woNo+ (char)30
						+ "STATUS" + (char)31 +getState+ (char)30
						+ "REMARK1" + (char)31 +rem1+ (char)30
						+ "REMARK2" + (char)31 +rem2+ (char)30
						+ "REMARK3" + (char)31 +rem3+ (char)30
						+ "REMARK4" + (char)31 +rem4+ (char)30
						+ "REMARK_LONG" + (char)31 +rem5+ (char)30
						+ "CONFORMITY_TO_DESIGN3" + (char)31 +'Y'+ (char)30
						+ "AUTHORIZATION_NO" + (char)31 +authoriztionNo+ (char)30
						+ "EMPLOYEE_NAME" + (char)31 +employeeName+ (char)30;
			}
			else if ("RBCONFIRMITY4".equals(mgr.readValue("AIRWORTHWIZPAGE2"))) 
                        { 
                                conf2 = "Y";
				authoriztionNo = mgr.readValue("AUTHORIZATION_NO");
                                employeeName = mgr.readValue("EMPLOYEE_NAME");

				sAttr = sAttr   + "WO_NO" + (char)31 +woNo+ (char)30
						+ "STATUS" + (char)31 +getState+ (char)30
						+ "REMARK1" + (char)31 +rem1+ (char)30
						+ "REMARK2" + (char)31 +rem2+ (char)30
						+ "REMARK3" + (char)31 +rem3+ (char)30
						+ "REMARK4" + (char)31 +rem4+ (char)30
						+ "REMARK_LONG" + (char)31 +rem5+ (char)30
						+ "CONFORMITY_TO_DESIGN4" + (char)31 +'Y'+ (char)30
						+ "AUTHORIZATION_NO" + (char)31 +authoriztionNo+ (char)30
						+ "EMPLOYEE_NAME" + (char)31 +employeeName+ (char)30;
			}

		}

		if (Step == 4)
		{
			String conf3;

                        if ("RBCONFIRMITY5".equals(mgr.readValue("OTHERWIZPAGE4")))      
			{
				conf3 = "Y";
			        authoriztionNo = mgr.readValue("AUTHORIZATION_NO");
                                employeeName = mgr.readValue("EMPLOYEE_NAME");

				sAttr = sAttr   + "WO_NO" + (char)31 +woNo+ (char)30
						+ "STATUS" + (char)31 +getState+ (char)30
						+ "REMARK1" + (char)31 +rem1+ (char)30
						+ "REMARK2" + (char)31 +rem2+ (char)30
						+ "REMARK3" + (char)31 +rem3+ (char)30
						+ "REMARK4" + (char)31 +rem4+ (char)30
						+ "REMARK_LONG" + (char)31 +rem5+ (char)30
						+ "RETURN_TO_SERVICE1" + (char)31 +'Y'+ (char)30
						+ "AUTHORIZATION_NO" + (char)31 +authoriztionNo+ (char)30
						+ "EMPLOYEE_NAME" + (char)31 +employeeName+ (char)30;

                                
			}
			else if ("RBCONFIRMITY6".equals(mgr.readValue("OTHERWIZPAGE4")))
			{
				conf3 = "Y";
				authoriztionNo = mgr.readValue("AUTHORIZATION_NO");
                                employeeName = mgr.readValue("EMPLOYEE_NAME");

				sAttr = sAttr   + "WO_NO" + (char)31 +woNo+ (char)30
						+ "STATUS" + (char)31 +getState+ (char)30
						+ "REMARK1" + (char)31 +rem1+ (char)30
						+ "REMARK2" + (char)31 +rem2+ (char)30
						+ "REMARK3" + (char)31 +rem3+ (char)30
						+ "REMARK4" + (char)31 +rem4+ (char)30
						+ "REMARK_LONG" + (char)31 +rem5+ (char)30
						+ "RETURN_TO_SERVICE2" + (char)31 +'Y'+ (char)30
						+ "AUTHORIZATION_NO" + (char)31 +authoriztionNo+ (char)30
						+ "EMPLOYEE_NAME" + (char)31 +employeeName+ (char)30;

                        }

		}

		if (Step == 5)
		{
			String conf4;

			if ("RBCONFIRMITY7".equals(mgr.readValue("LASTPAGERADIO")))
			{
				conf4 = "Y";
				authoriztionNo = mgr.readValue("AUTHORIZATION_NO");
                                employeeName = mgr.readValue("EMPLOYEE_NAME");

				sAttr = sAttr   + "WO_NO" + (char)31 +woNo+ (char)30
						+ "STATUS" + (char)31 +getState+ (char)30
						+ "REMARK1" + (char)31 +rem1+ (char)30
						+ "REMARK2" + (char)31 +rem2+ (char)30
						+ "REMARK3" + (char)31 +rem3+ (char)30
						+ "REMARK4" + (char)31 +rem4+ (char)30
						+ "REMARK_LONG" + (char)31 +rem5+ (char)30
						+ "RETURN_TO_SERVICE3" + (char)31 +'Y'+ (char)30
						+ "AUTHORIZATION_NO" + (char)31 +authoriztionNo+ (char)30
						+ "EMPLOYEE_NAME" + (char)31 +employeeName+ (char)30;
			}
			else if ("RBCONFIRMITY8".equals(mgr.readValue("LASTPAGERADIO")))
			{
				conf4 = "Y";
				authoriztionNo = mgr.readValue("AUTHORIZATION_NO");
                                employeeName = mgr.readValue("EMPLOYEE_NAME");

				sAttr = sAttr   + "WO_NO" + (char)31 +woNo+ (char)30
						+ "STATUS" + (char)31 +getState+ (char)30
						+ "REMARK1" + (char)31 +rem1+ (char)30
						+ "REMARK2" + (char)31 +rem2+ (char)30
						+ "REMARK3" + (char)31 +rem3+ (char)30
						+ "REMARK4" + (char)31 +rem4+ (char)30
						+ "REMARK_LONG" + (char)31 +rem5+ (char)30
						+ "RETURN_TO_SERVICE4" + (char)31 +'Y'+ (char)30
						+ "AUTHORIZATION_NO" + (char)31 +authoriztionNo+ (char)30
						+ "EMPLOYEE_NAME" + (char)31 +employeeName+ (char)30;
			}

		}

		trans.clear();

                cmd = trans.addCustomCommand("FINISHFRM","RELEASE_CERTIFICATE_API.New__");
		cmd.addParameter("INFO","");
		cmd.addParameter("OBJID",objid1);
		cmd.addParameter("OBJVERSION",objversion1);
		cmd.addParameter("ATTR",sAttr);
		cmd.addParameter("ACTION","DO");

		trans = mgr.perform(trans);

		objid1 = trans.getValue("FINISHFRM/DATA/OBJID");
		objversion1 = trans.getValue("FINISHFRM/DATA/OBJVERSION");

		printReleaseCertificate();

		FinishFlag = "true";
	}


	public void setWarningString(String attr)
	{
		WarningFlag = "true";
		warningString = attr.replace((char)31,(char)32);
		warningString = warningString.replace((char)30,(char)32);
	}


	public void  back2()
	{
		ASPManager mgr = getASPManager();

		step1=true;
		step2=false;
		step3=false;

		ASPBuffer data = mgr.newASPBuffer();
		headset.addRow(data);

		row = headset.getRow();

		headset.setRow(row);
	}

	public void  back3()
	{
		ASPManager mgr = getASPManager();

		step1=false;
		step2=true;
		step3=false;

	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("WO_NO").
		setHidden();

		headblk.addField("STATUS").
		setSize(20).
		setSelectBox().
		enumerateValues("RELEASE_CERTIFICATE_STATUS_API").
		setLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZSTATBLK12: Status (Block 12)").
		setInsertable().
		setMaxLength(200);

		headblk.addField("REMARK1").
		setInsertable().
		setLabel(" ").
		setSize(60).
		setMaxLength(80);

		headblk.addField("REMARK2").
		setInsertable().
		setLabel(" ").
		setSize(60).
		setMaxLength(80);

		headblk.addField("REMARK3").
		setInsertable().
		setLabel(" ").
		setSize(60).
		setMaxLength(80);

		headblk.addField("REMARK4").
		setInsertable().
		setLabel(" ").
		setSize(60).
		setMaxLength(80);

		headblk.addField("REMARK_LONG").
		setInsertable().
		setLabel(" ").
		setSize(60).
		setHeight(4).
		setMaxLength(2000);

		headblk.addField("AUTHORIZATION_NO").
		setSize(14).
		setLabel(" ").
		setMandatory().
		setInsertable().
		setLOV("ReleaseCertAuthIdLov.page","EMPLOYEE_ID",600,445).
		setCustomValidation("AUTHORIZATION_NO","EMPLOYEE_ID,AUTHORIZATION_NO,EMPLOYEE_NAME").
		setMaxLength(20);

                headblk.addField("EMPLOYEE_ID").
                setLOV("ReleaseCertAuthIdLov.page",600,445).
                setLOVProperty("TITLE","PCMWPRINTAUTHRELEASECERTIFICATEWIZLISTTITLE: List of Employee ID").
                setCustomValidation("EMPLOYEE_ID","EMPLOYEE_ID,AUTHORIZATION_NO,EMPLOYEE_NAME");
                
		headblk.addField("EMPLOYEE_NAME").
		setFunction("''").
		setSize(30).
		setReadOnly();

		headblk.addField("AUTHORITY_ID_FOR_DEPT").
		setFunction("''");

		headblk.addField("GET_FORM_TYPE").
		setFunction("''");

		headblk.addField("GET_RESULT1").
		setFunction("''");

		headblk.addField("AUTHORITY_ID").
		setHidden(); 

		headblk.addField("RBCONFIRMITY3").
		setInsertable().
		setLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZRBCONFIRMITY3: Airworthiness").
		setSize(30).
		setFunction("''");

		headblk.addField("RBCONFIRMITY4").
		setInsertable().
		setLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZRBCONFIRMITY4: Conformity Only").
		setSize(30).
		setFunction("''");

		headblk.addField("RBCONFIRMITY5").
		setInsertable().
		setLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZRBCONFIRMITY5: 14 CFR 43.9 Return to Service").
		setSize(30).
		setFunction("''");

		headblk.addField("RBCONFIRMITY6").
		setInsertable().
		setLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZOTHRREG: Other regulation specified in Block 13").
		setSize(30).
		setFunction("''");

		headblk.addField("RBCONFIRMITY7").
		setInsertable().
		setLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZRBCONFIRMITY7: JAR 145.50 Release to Service").
		setSize(30).
		setFunction("''");

		headblk.addField("RBCONFIRMITY8").
		setInsertable().
		setLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZOTHRREG: Other regulation specified in Block 13").
		setSize(30).
		setFunction("''");

		f = headblk.addField("INFO");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ATTR");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ACTION");
		f.setFunction("''");
		f.setHidden();

		//Fields to handle PRINT methods

		f = headblk.addField("ATTR0");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ATTR1");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ATTR2");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ATTR3");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("ATTR4");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("HEAD_TEMP");
		f.setHidden();
		f.setFunction("''");

		// End PRINT method


		headblk.setView("RELEASE_CERTIFICATE");
		headblk.defineCommand("RELEASE_CERTIFICATE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();
		headtbl = mgr.newASPTable(headblk);
		headblk.setTitle("PCMWPRINTAUTHRELEASECERTIFICATEWIZTITLE1: Print Authorized Release Certificate");

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.SAVERETURN);
		headbar.disableCommand(headbar.CANCELEDIT);
		headbar.disableCommand(headbar.FORWARD);
		headbar.disableModeLabel();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
		headlay.setEditable();
		headlay.setDialogColumns(1);

		headlay.defineGroup(mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZGENTAB: Gen"),"STATUS",false,true);
		headlay.defineGroup(mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZTAB2: Remarks (Block 13)"),"REMARK1,REMARK2,REMARK3,REMARK4,REMARK_LONG",true,true);

		headlay.showBottomLine(false);

                setWizardOptions();
	}

        private void setWizardOptions()
	{
		disableHomeIcon();
		disableNavigate();
		disableOptions();
	}



	public void  adjust()
	{
		ASPManager mgr = getASPManager();

		frm.setFormWidth(792);
		headbar.setWidth(frm.getFormWidth());  

		if (Step == 1)
		{
			mgr.getASPField("RBCONFIRMITY3").setHidden();
			mgr.getASPField("RBCONFIRMITY4").setHidden();
			mgr.getASPField("RBCONFIRMITY5").setHidden();
			mgr.getASPField("RBCONFIRMITY6").setHidden();
			mgr.getASPField("RBCONFIRMITY7").setHidden();
			mgr.getASPField("RBCONFIRMITY8").setHidden();

			headlay.setDialogColumns(1);

		}

		if (Step == 2)
		{

			mgr.getASPField("STATUS").setHidden();
			mgr.getASPField("REMARK1").setHidden();
			mgr.getASPField("REMARK2").setHidden();
			mgr.getASPField("REMARK3").setHidden();
			mgr.getASPField("REMARK4").setHidden();
			mgr.getASPField("REMARK_LONG").setHidden();


			headlay.setDialogColumns(1);

		}
		if (Step == 3)
		{
			mgr.getASPField("STATUS").setHidden();
			mgr.getASPField("REMARK1").setHidden();
			mgr.getASPField("REMARK2").setHidden();
			mgr.getASPField("REMARK3").setHidden();
			mgr.getASPField("REMARK4").setHidden();
			mgr.getASPField("REMARK_LONG").setHidden();

			headlay.setDialogColumns(1);

		}
		if (Step == 4)
		{

			mgr.getASPField("STATUS").setHidden();
			mgr.getASPField("REMARK1").setHidden();
			mgr.getASPField("REMARK2").setHidden();
			mgr.getASPField("REMARK3").setHidden();
			mgr.getASPField("REMARK4").setHidden();
			mgr.getASPField("REMARK_LONG").setHidden();

			headlay.setDialogColumns(1);

		}
		if (Step == 5)
		{

			mgr.getASPField("STATUS").setHidden();
			mgr.getASPField("REMARK1").setHidden();
			mgr.getASPField("REMARK2").setHidden();
			mgr.getASPField("REMARK3").setHidden();
			mgr.getASPField("REMARK4").setHidden();
			mgr.getASPField("REMARK_LONG").setHidden();

			headlay.setDialogColumns(1);

		}

		url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");




	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWPRINTAUTHRELEASECERTIFICATEWIZTITLE: Print Authorized Release Certificate";
	}

	protected String getTitle()
	{
		return "PCMWPRINTAUTHRELEASECERTIFICATEWIZTITLE: Print Authorized Release Certificate";
	}

	protected void printContents() throws FndException
	{ 
		ASPManager mgr = getASPManager();

		if (showHtmlPart)
		{
			appendToHTML("<table id=\"TBL\" width=\"85%\" border=0 bgcolor=green class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1>\n");
			appendToHTML("		   <tr>\n");
			appendToHTML("		     <td nowrap  align=\"left\"><img src = \"../pcmw/images/bmpPcmReleaseCertificate.gif\"></td>\n");
			appendToHTML("		     <td nowrap  align=\"left\">\n");
			if (Step == 1)
			{
				//appendToHTML(headbar.showBar());
				appendToHTML("			<table border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '60%'>\n");
				appendToHTML("				<tr>	\n");
				appendToHTML("					<td><B>\n"); 
				appendToHTML(headlay.generateDialog());				//XSS_Safe AMNILK 20070725
				appendToHTML("					   </br></br>\n");
				appendToHTML("					</td>			   \n");
				appendToHTML("				</tr>				\n");
				appendToHTML("			 </table>\n");
				appendToHTML("			 </br></br>		\n");
			}
           
			if (Step == 2)
			{
				appendToHTML("  <table width= '550'>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE1: Conformity to Design (Block 14)"));
				appendToHTML("	 </br></br>\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\" width = '69%'>      \n");
				appendToHTML(fmt.drawRadio("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE1RADIO1: Approved design data and are in a condition for safe operation",
										 "RADIOWIZPAGE1",  
										 "RBCONFIRMITY1",
										 true,
										 ""));
				appendToHTML("&nbsp;\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawRadio("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE1ELSE: Non-approved design data specified in Block 13",
										 "RADIOWIZPAGE1",
										 "RBCONFIRMITY2",
										 false,
										 ""));
				appendToHTML("&nbsp;\n");
				appendToHTML("&nbsp;\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
                                appendToHTML("	 </br>\n");
				appendToHTML("  <table width= '500'>\n");
                                appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZEMPLYEEID: Employee ID"));
                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
				appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZAUTHAPPPAGE1: Authorized Approver (Block 16-17)"));
				appendToHTML("	 </br>\n");
				appendToHTML("   <tr>\n");
                                appendToHTML("      <td nowrap height=\"20\" align=\"left\" width = '30%'>");
				appendToHTML(fmt.drawTextField("EMPLOYEE_ID","","",20));
                                appendToHTML("<img src=\"../common/images/table_empty_image.gif\" width=\"5\" height=\"1\"><a href=\"javascript:lovEmployeeId(-1)\"><img src=\"../common/images/lov.gif\" width=20 height=15 border=0 alt=\"List of values\"></a><font class=\"TextLabel\">*</font></td>\n");
				appendToHTML("      <td nowrap height=\"20\" align=\"left\" width = '30%'>");
				appendToHTML(fmt.drawTextField("AUTHORIZATION_NO","","",20));
                                appendToHTML("<img src=\"../common/images/table_empty_image.gif\" width=\"5\" height=\"1\"><a href=\"javascript:lovAuthorizationNo(-1)\"><img src=\"../common/images/lov.gif\" width=20 height=15 border=0 alt=\"List of values\"></a><font class=\"TextLabel\">*</font></td>\n");
				appendToHTML("   <td align=\"left\" width = '100%'>      \n");
				appendToHTML(fmt.drawReadOnlyTextField("EMPLOYEE_NAME","","",40));
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
			}


			if (Step == 3)
			{
				appendToHTML("  <table width= '500'>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE2CONDESIGN: Conformity to Design (Block 14)"));
				appendToHTML("	 </br></br>\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawRadio("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE2AIRW: Airworthiness",
										 "AIRWORTHWIZPAGE2",  
										 "RBCONFIRMITY3",
										 true,
										 ""));
				appendToHTML("&nbsp;\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawRadio("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE2CONFONLY: Conformity Only",
										 "AIRWORTHWIZPAGE2",
										 "RBCONFIRMITY4",
										 false,
										 ""));
				appendToHTML("&nbsp;\n");
				appendToHTML("&nbsp;\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
                                appendToHTML("	 </br>\n");
				appendToHTML("  <table width= '500'>\n");
                                appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZEMPLYEEID: Employee ID"));
                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
				appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE2AUTHAPP: Authorized Approver (Block 16-17)"));
				appendToHTML("	 </br>\n");
				appendToHTML("   <tr>\n");
                                appendToHTML("      <td nowrap height=\"20\" align=\"left\" width = '30%'>");
				appendToHTML(fmt.drawTextField("EMPLOYEE_ID","","",20));
                                appendToHTML("<img src=\"../common/images/table_empty_image.gif\" width=\"5\" height=\"1\"><a href=\"javascript:lovEmployeeId(-1)\"><img src=\"../common/images/lov.gif\" width=20 height=15 border=0 alt=\"List of values\"></a><font class=\"TextLabel\">*</font></td>\n");
                                appendToHTML("      <td nowrap height=\"20\" align=\"left\" width = '30%'>");
				appendToHTML(fmt.drawTextField("AUTHORIZATION_NO","","",20));
				appendToHTML("<img src=\"../common/images/table_empty_image.gif\" width=\"5\" height=\"1\"><a href=\"javascript:lovAuthorizationNo(-1)\"><img src=\"../common/images/lov.gif\" width=20 height=15 border=0 alt=\"List of values\"></a><font class=\"TextLabel\">*</font></td>\n");
				appendToHTML("   <td align=\"left\" width = '100%'>      \n");
				appendToHTML(fmt.drawReadOnlyTextField("EMPLOYEE_NAME","","",40));
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
			}
			if (Step == 4)
			{
				appendToHTML("  <table width= '500'>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE3RET: Return to Service (Block 19)"));
				appendToHTML("	 </br></br>\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawRadio("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE3CFR: 14 CFR 43.9 Return to Service",
										 "OTHERWIZPAGE4",  
										 "RBCONFIRMITY5",
										 true,
										 ""));
				appendToHTML("&nbsp;\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawRadio("PCMWPRINTAUTHRELEASECERTIFICATEWIZOTHRREG: Other regulation specified in Block 13",
										 "OTHERWIZPAGE4",
										 "RBCONFIRMITY6",
										 false,
										 ""));
				appendToHTML("&nbsp;\n");
				appendToHTML("&nbsp;\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
                                appendToHTML("	 </br>\n");
				appendToHTML("  <table width= '500'>\n");
                                appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZEMPLYEEID: Employee ID"));
                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
				appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE3ATHAPPR: Authorized Approver (Block 21-22)"));
				appendToHTML("	 </br>\n");
				appendToHTML("   <tr>\n");
                                appendToHTML("      <td nowrap height=\"20\" align=\"left\" width = '30%'>");
				appendToHTML(fmt.drawTextField("EMPLOYEE_ID","","",20));
                                appendToHTML("<img src=\"../common/images/table_empty_image.gif\" width=\"5\" height=\"1\"><a href=\"javascript:lovEmployeeId(-1)\"><img src=\"../common/images/lov.gif\" width=20 height=15 border=0 alt=\"List of values\"></a><font class=\"TextLabel\">*</font></td>\n");
                                appendToHTML("      <td nowrap height=\"20\" align=\"left\" width = '30%'>");
				appendToHTML(fmt.drawTextField("AUTHORIZATION_NO","","",20));
				appendToHTML("<img src=\"../common/images/table_empty_image.gif\" width=\"5\" height=\"1\"><a href=\"javascript:lovAuthorizationNo(-1)\"><img src=\"../common/images/lov.gif\" width=20 height=15 border=0 alt=\"List of values\"></a><font class=\"TextLabel\">*</font></td>\n");
				appendToHTML("   <td align=\"left\" width = '100%'>      \n");
				appendToHTML(fmt.drawReadOnlyTextField("EMPLOYEE_NAME","","",40));
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
				
			}
			if (Step == 5)
			{
				appendToHTML("  <table width= '500'>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE4RETSERV: Return to Service (Block 19)"));
				appendToHTML("	 </br></br>\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawRadio("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE4RELSER: JAR 145.50 Release to Service",
										 "LASTPAGERADIO",  
										 "RBCONFIRMITY7",
										 true,
										 ""));
				appendToHTML("&nbsp;\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"left\">      \n");
				appendToHTML(fmt.drawRadio("PCMWPRINTAUTHRELEASECERTIFICATEWIZOTHRREG: Other regulation specified in Block 13",
										 "LASTPAGERADIO",
										 "RBCONFIRMITY8",
										 false,
										 ""));
				appendToHTML("&nbsp;\n");
				appendToHTML("&nbsp;\n");
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
                                appendToHTML("	 </br>\n");
				appendToHTML("  <table width= '500'>\n");
                                appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZEMPLYEEID: Employee ID"));
                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
				appendToHTML(fmt.drawReadLabel("PCMWPRINTAUTHRELEASECERTIFICATEWIZPAGE4AUTAPPV: Authorized Approver (Block 21-22)"));
				appendToHTML("	 </br>\n");
				appendToHTML("   <tr>\n");
                                appendToHTML("      <td nowrap height=\"20\" align=\"left\" width = '30%'>");
				appendToHTML(fmt.drawTextField("EMPLOYEE_ID","","",20));
                                appendToHTML("<img src=\"../common/images/table_empty_image.gif\" width=\"5\" height=\"1\"><a href=\"javascript:lovEmployeeId(-1)\"><img src=\"../common/images/lov.gif\" width=20 height=15 border=0 alt=\"List of values\"></a><font class=\"TextLabel\">*</font></td>\n");
                                appendToHTML("      <td nowrap height=\"20\" align=\"left\" width = '30%'>");
				appendToHTML(fmt.drawTextField("AUTHORIZATION_NO","","",20));
				appendToHTML("<img src=\"../common/images/table_empty_image.gif\" width=\"5\" height=\"1\"><a href=\"javascript:lovAuthorizationNo(-1)\"><img src=\"../common/images/lov.gif\" width=20 height=15 border=0 alt=\"List of values\"></a><font class=\"TextLabel\">*</font></td>\n");
				appendToHTML("   <td align=\"left\" width = '100%'>      \n");
				appendToHTML(fmt.drawReadOnlyTextField("EMPLOYEE_NAME","","",40));
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
			}

			appendToHTML("		  </td>\n");
			appendToHTML("		 </tr>\n");
			appendToHTML("		</table> \n");
			appendToHTML("<table id=\"lineTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=85%>\n");
			appendToHTML(" <tr>\n");
			appendToHTML(fmt.drawReadValue("....................................................."));
			appendToHTML(fmt.drawReadValue("....................................................."));
			appendToHTML(fmt.drawReadValue("....................................................."));
			appendToHTML(" </tr>\n");
			appendToHTML("</table> \n");
			appendToHTML("<table id=\"TBL2\" border=0 cellspacing=10 cellpadding=1 width= 87%>\n");
			appendToHTML("  <tr>		\n");
			appendToHTML("    <td align=\"right\">\n");
			if (Step == 1)
			{
				appendToHTML("  <table width= '700'>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"right\"> \n");
				appendToHTML(fmt.drawSubmit("NXT",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZNEXT1: Next>"),""));
				appendToHTML("&nbsp;     	\n");
				appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZCANCEL1: Cancel"),"cancel"));
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table>   \n");
			}
			if (Step == 2)
			{
				appendToHTML("  <table width= '700'>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"right\">      \n");
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("PREV",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZBACK1: <Back"),"prev"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZCANCEL1: Cancel"),"cancel"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZFINISH1: Finish"),"finish"));
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
			}
			if (Step == 3)
			{
				appendToHTML("  <table width= '700'>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"right\">      \n");
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("PREV",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZBACK3: <Back"),"prev"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZCANCEL3: Cancel"),"cancel"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZFINISH3: Finish"),"finish"));
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
			}
			if (Step == 4)
			{
				appendToHTML("  <table width= '700'>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"right\">      \n");
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("PREV",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZBACK4: <Back"),"prev"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZCANCEL4: Cancel"),"cancel"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZFINISH4: Finish"),"finish"));
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
			}
			if (Step == 5)
			{
				appendToHTML("  <table width= '700'>\n");
				appendToHTML("   <tr>\n");
				appendToHTML("   <td align=\"right\">      \n");
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("PREV",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZBACK5: <Back"),"prev"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZCANCEL5: Cancel"),"cancel"));
				appendToHTML("&nbsp;\n");
				appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWPRINTAUTHRELEASECERTIFICATEWIZFINISH5: Finish"),"finish"));
				appendToHTML("   </td>\n");
				appendToHTML("   </tr>\n");
				appendToHTML("  </table> \n");
			}

			appendToHTML("    </td>\n");
			appendToHTML("  </tr>\n");
			appendToHTML("</table>						\n");
			appendToHTML("<p>\n");

			appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
			appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
			appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

			appendDirtyJavaScript("function validateAuthorizationNo(i)\n");
			appendDirtyJavaScript("{\n");
                        appendDirtyJavaScript("window.status='Please wait for validation';\n");
                        appendDirtyJavaScript("r = __connect(\n");
			appendDirtyJavaScript("'../pcmw/PrintAuthReleaseCertificateWiz.page?VALIDATE=AUTHORIZATION_NO'\n");
			appendDirtyJavaScript("+ '&AUTHORIZATION_NO=' + URLClientEncode(getValue_('AUTHORIZATION_NO',i))\n");
			appendDirtyJavaScript(");\n");
                        appendDirtyJavaScript("window.status='';\n");
			appendDirtyJavaScript("if( checkStatus_(r,'AUTHORIZATION_NO',i,' ') )\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("assignValue_('AUTHORIZATION_NO',i,0);\n");
			appendDirtyJavaScript("assignValue_('EMPLOYEE_NAME',i,1);        \n");
                        appendDirtyJavaScript("assignValue_('EMPLOYEE_ID',i,2);        \n");
			appendDirtyJavaScript("}\n");
			appendDirtyJavaScript("}\n");

                        appendDirtyJavaScript("function validateEmployeeId(i)\n");
                        appendDirtyJavaScript("{\n");
                        appendDirtyJavaScript("window.status='Please wait for validation';\n");
                        appendDirtyJavaScript("r = __connect(\n");
                        appendDirtyJavaScript("'../pcmw/PrintAuthReleaseCertificateWiz.page?VALIDATE=EMPLOYEE_ID'\n");
                        appendDirtyJavaScript("+ '&EMPLOYEE_ID=' + URLClientEncode(getValue_('EMPLOYEE_ID',i))\n");
                        appendDirtyJavaScript(");\n");
                        appendDirtyJavaScript("window.status='';\n");
                        appendDirtyJavaScript("if( checkStatus_(r,'EMPLOYEE_ID',i,' ') )\n");
                        appendDirtyJavaScript("{\n");
                        appendDirtyJavaScript("assignValue_('AUTHORIZATION_NO',i,0);\n");
                        appendDirtyJavaScript("assignValue_('EMPLOYEE_NAME',i,1);        \n");
                        appendDirtyJavaScript("assignValue_('EMPLOYEE_ID',i,2);        \n");
                        appendDirtyJavaScript("}\n");
                        appendDirtyJavaScript("}\n");

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
			//appendDirtyJavaScript("   window.opener.refreshData();\n"); 
			appendDirtyJavaScript("   self.close();\n");
			appendDirtyJavaScript("}\n");

		}
	}
}