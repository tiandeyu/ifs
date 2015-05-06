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
*  File        : PrepostingDlg.java 
*  Created     : ARWILK  010314
*  Modified    :
*  JEWILK  010403  Changed file extensions from .asp to .page
*  ARWILK  010411  Corrected the validation problem. 
*  JEWILK  010503  Modified method startup() to correctly enable and disable fields.
*  ARWILK  010618  Modified method SaveReturn and Cancel.
*  BUNILK  010627  Modified validation() method.
*  ARWILK  031218  Edge Developments - (Removed clone and doReset Methods)
*  THWILK  040316  Call ID 113081 , Added window.close() to close the preposting page after save functionality.
*  THWILK  040317  Call ID 112865 , Modified cancel() to close the window when CancelEdit button is pressed.
* ----------------------- EDGE - SP1 Merge -----------------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.  
*  VAGULK  040324  Merged with SP1.
*  ThWilk  040819  Call 116917,Modified javascripts.
*  ARWILK  041111  Replaced getContents with printContents.
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PrepostingDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PrepostingDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;
	private ASPField f;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPBlock tempblk;
	private ASPRowSet tempset;
	private ASPBlockLayout templay;
	private ASPBlock tempblk1;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String enabl0;
	private String enabl1;
	private String enabl2;
	private String enabl3;
	private String enabl4;
	private String enabl5;
	private String enabl6;
	private String enabl7;
	private String enabl8;
	private String enabl9;
	private String enabl10;
	private String mcon;
	private String wono;
	private String today;
	private String company1;
	private ASPTransactionBuffer trans;
	private String calling_url;
	private String pre_posting_id;
	private String sCodePartNameA;
	private String sCodePartNameB;
	private String sCodePartNameC;
	private String sCodePartNameD;
	private String sCodePartNameE;
	private String sCodePartNameF;
	private String sCodePartNameG;
	private String sCodePartNameH;
	private String sCodePartNameI;
	private String sCodePartNameJ;
	private String sFrmName;
	private String transferurl;
	private boolean bCloseWindow = false;

	//===============================================================
	// Construction 
	//===============================================================
	public PrepostingDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		wono = ctx.readValue("WO_NO1",""); 
		mcon = ctx.readValue("MCONNN","");        
		enabl0 = ctx.readValue("CTXENABL0","0");
		enabl1 = ctx.readValue("CTXENABL1","0");
		enabl2 = ctx.readValue("CTXENABL2","0");
		enabl3 = ctx.readValue("CTXENABL3","0");
		enabl4 = ctx.readValue("CTXENABL4","0");
		enabl5 = ctx.readValue("CTXENABL5","0");
		enabl6 = ctx.readValue("CTXENABL6","0");
		enabl7 = ctx.readValue("CTXENABL7","0");
		enabl8 = ctx.readValue("CTXENABL8","0");
		enabl9 = ctx.readValue("CTXENABL9","0");
		enabl10 = ctx.readValue("CTXENABL10","0");
		sFrmName = ctx.readValue("CTXSFRMNAME");

		calling_url = ctx.getGlobal("CALLING_URL");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
		{
			validate();
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("PRE_POSTING_ID")))
		{
			mgr.setPageExpiring();

			pre_posting_id=mgr.readValue("PRE_POSTING_ID","");
			mcon = mgr.readValue("CONTRACT","");
			wono = mgr.readValue("WO_NO","");
			enabl0 = mgr.readValue("ENABL0","");
			enabl1 = mgr.readValue("ENABL1","");
			enabl2 = mgr.readValue("ENABL2","");
			enabl3 = mgr.readValue("ENABL3","");
			enabl4 = mgr.readValue("ENABL4","");
			enabl5 = mgr.readValue("ENABL5","");
			enabl6 = mgr.readValue("ENABL6","");
			enabl7 = mgr.readValue("ENABL7","");
			enabl8 = mgr.readValue("ENABL8","");
			enabl9 = mgr.readValue("ENABL9","");
			enabl10 = mgr.readValue("ENABL10","");   
			sFrmName = mgr.readValue("FRMNAME","");
		}

		startup();
		adjust();

		ctx.writeValue("WO_NO1",wono); 
		ctx.writeValue("MCONNN",mcon);
		ctx.writeValue("CTXENABL0",enabl0);
		ctx.writeValue("CTXENABL1",enabl1);
		ctx.writeValue("CTXENABL2",enabl2);
		ctx.writeValue("CTXENABL3",enabl3);
		ctx.writeValue("CTXENABL4",enabl4);
		ctx.writeValue("CTXENABL5",enabl5);
		ctx.writeValue("CTXENABL6",enabl6);
		ctx.writeValue("CTXENABL7",enabl7);
		ctx.writeValue("CTXENABL8",enabl8);
		ctx.writeValue("CTXENABL9",enabl9);
		ctx.writeValue("CTXENABL10",enabl10); 
		ctx.writeValue("CTXSFRMNAME",sFrmName);
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

	public void  startup()
	{
		ASPManager mgr = getASPManager();

		String prepostingidexist;  

		trans.clear();

		ASPCommand cmd = trans.addCustomFunction("CON3","Maintenance_Site_Utility_API.Get_Site_Date","TODAY");
		cmd.addParameter("CONTRACT1",mcon);

		cmd = trans.addCustomFunction("CON4","MAINTENANCE_PRE_POSTING_API.PRE_POSTING_EXIST","PREPOSTINGIDEXIST");
		cmd.addParameter("PRE_POSTING_ID");

		cmd = trans.addCustomFunction("CON5","Site_API.Get_Company","COMPANY1");
		cmd.addParameter("CONTRACT1",mcon);

		trans = mgr.perform(trans);

		today = trans.getValue("CON3/DATA/TODAY");
		company1 = trans.getValue("CON5/DATA/COMPANY1");
		prepostingidexist = trans.getValue("CON4/DATA/PREPOSTINGIDEXIST");

		f = mgr.getASPField("ACCOUNT_NO");

		if ("0".equals(enabl0))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");


		f = mgr.getASPField("COST_CENTER");

		if ("0".equals(enabl1))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");


		f = mgr.getASPField("CODENO_C");

		if ("0".equals(enabl2))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");


		f = mgr.getASPField("CODENO_D");

		if ("0".equals(enabl3))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");


		f = mgr.getASPField("OBJECT_NO");

		if ("0".equals(enabl4))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");


		f = mgr.getASPField("PROJECT_NO");

		if ("0".equals(enabl5))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");


		f = mgr.getASPField("CODENO_G");

		if ("0".equals(enabl6))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");


		f = mgr.getASPField("CODENO_H");

		if ("0".equals(enabl7))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");


		f = mgr.getASPField("CODENO_I");

		if ("0".equals(enabl8))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");


		f = mgr.getASPField("CODENO_J");

		if ("0".equals(enabl9))
			f.setReadOnly();
		else
			f.setLOVProperty("WHERE","COMPANY1='"+company1+"'");

		trans.clear();

		okFind();

		ASPBuffer temp = tempset.getRow();

		temp.setValue("COMPANY1",company1);
		temp.setValue("TODAY",today);
		temp.setValue("WO_NO1",wono);
		tempset.setRow(temp);

		trans.clear();
	}

	public void  setLables()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		ASPCommand cmd = trans.addCustomFunction("CON5","Site_API.Get_Company","COMPANY1");
		cmd.addParameter("CONTRACT1",mcon);

		cmd = trans.addCustomFunction( "CODEPARTA","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEA" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "A");

		cmd = trans.addCustomFunction( "CODEPARTB","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEB" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "B");

		cmd = trans.addCustomFunction( "CODEPARTC","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEC" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "C");

		cmd = trans.addCustomFunction( "CODEPARTD","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMED" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "D");

		cmd = trans.addCustomFunction( "CODEPARTE","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEE" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "E");

		cmd = trans.addCustomFunction( "CODEPARTF","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEF" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "F");

		cmd = trans.addCustomFunction( "CODEPARTG","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEG" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "G");

		cmd = trans.addCustomFunction( "CODEPARTH","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEH" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "H");

		cmd = trans.addCustomFunction( "CODEPARTI","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEI" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "I");

		cmd = trans.addCustomFunction( "CODEPARTJ","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEJ" );
		cmd.addReference("COMPANY1", "CON5/DATA");
		cmd.addParameter("CODEPART", "J");

		trans = mgr.perform(trans);

		sCodePartNameA = trans.getValue("CODEPARTA/DATA/CODEPARTNAMEA");
		sCodePartNameB = trans.getValue("CODEPARTB/DATA/CODEPARTNAMEB");
		sCodePartNameC = trans.getValue("CODEPARTC/DATA/CODEPARTNAMEC");
		sCodePartNameD = trans.getValue("CODEPARTD/DATA/CODEPARTNAMED");
		sCodePartNameE = trans.getValue("CODEPARTE/DATA/CODEPARTNAMEE");
		sCodePartNameF = trans.getValue("CODEPARTF/DATA/CODEPARTNAMEF");
		sCodePartNameG = trans.getValue("CODEPARTG/DATA/CODEPARTNAMEG");
		sCodePartNameH = trans.getValue("CODEPARTH/DATA/CODEPARTNAMEH");
		sCodePartNameI = trans.getValue("CODEPARTI/DATA/CODEPARTNAMEI");
		sCodePartNameJ = trans.getValue("CODEPARTJ/DATA/CODEPARTNAMEJ");

		mgr.getASPField("COST_CENTER").setLOVProperty("TITLE",sCodePartNameB);
		mgr.getASPField("CODENO_C").setLOVProperty("TITLE",sCodePartNameC);
		mgr.getASPField("ACCOUNT_NO").setLOVProperty("TITLE",sCodePartNameA);
		mgr.getASPField("CODENO_D").setLOVProperty("TITLE",sCodePartNameD);
		mgr.getASPField("OBJECT_NO").setLOVProperty("TITLE",sCodePartNameE);
		mgr.getASPField("PROJECT_NO").setLOVProperty("TITLE",sCodePartNameF);
		mgr.getASPField("CODENO_G").setLOVProperty("TITLE",sCodePartNameG);
		mgr.getASPField("CODENO_H").setLOVProperty("TITLE",sCodePartNameH);
		mgr.getASPField("CODENO_I").setLOVProperty("TITLE",sCodePartNameI);
		mgr.getASPField("CODENO_J").setLOVProperty("TITLE",sCodePartNameJ);

		mgr.getASPField("ACCOUNT_NO").setLabel(sCodePartNameA);   
		mgr.getASPField("COST_CENTER").setLabel(sCodePartNameB);   
		mgr.getASPField("CODENO_C").setLabel(sCodePartNameC);   
		mgr.getASPField("CODENO_D").setLabel(sCodePartNameD);   
		mgr.getASPField("OBJECT_NO").setLabel(sCodePartNameE);   
		mgr.getASPField("PROJECT_NO").setLabel(sCodePartNameF);   
		mgr.getASPField("CODENO_G").setLabel(sCodePartNameG);   
		mgr.getASPField("CODENO_H").setLabel(sCodePartNameH);   
		mgr.getASPField("CODENO_I").setLabel(sCodePartNameI);   
		mgr.getASPField("CODENO_J").setLabel(sCodePartNameJ);  

		trans.clear();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		ASPCommand cmd;
		String val;
		String txt;

		val = mgr.readValue("VALIDATE");

		if ("ACCOUNT_NO".equals(val))
		{
			if (("0".equals(mgr.readValue("FEENABL0")))&&(!mgr.isEmpty("ACCOUNT_NO")))
			{

				String strAccNo = mgr.readValue("ACCOUNT_NO","");

				cmd = trans.addCustomCommand("VAL1","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",mgr.readValue("ACCOUNT_NO"));
				cmd.addParameter("CODE_PART","A");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strAccNo) ? "":strAccNo)+ "^";
				mgr.responseWrite(txt);
			}
		}

		else if ("COST_CENTER".equals(val))
		{
			if (("1".equals(mgr.readValue("FEENABL1")))&&(!mgr.isEmpty("COST_CENTER")))
			{
				String strCostCenter = mgr.readValue("COST_CENTER");

				cmd = trans.addCustomCommand("VAL2","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",strCostCenter);
				cmd.addParameter("CODE_PART","B");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strCostCenter) ? "":strCostCenter)+ "^";
				mgr.responseWrite(txt);
			}
		}

		else if ("CODENO_C".equals(val))
		{
			if (("1".equals(mgr.readValue("FEENABL2")))&&(!mgr.isEmpty("CODENO_C")))
			{

				String strCodeC = mgr.readValue("CODENO_C");

				cmd = trans.addCustomCommand("VAL3","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",mgr.readValue("CODENO_C"));
				cmd.addParameter("CODE_PART","C");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strCodeC) ? "":strCodeC)+ "^";

				mgr.responseWrite(txt);   
			}
		}

		else if ("CODENO_D".equals(val))
		{
			if (("1".equals(mgr.readValue("FEENABL3")))&&(!mgr.isEmpty("CODENO_D")))
			{
				String strCodeD = mgr.readValue("CODENO_D");

				cmd = trans.addCustomCommand("VAL4","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",mgr.readValue("CODENO_D"));
				cmd.addParameter("CODE_PART","D");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strCodeD) ? "":strCodeD)+ "^";

				mgr.responseWrite(txt);      
			}
		}

		else if ("OBJECT_NO".equals(val))
		{
			if (("1".equals(mgr.readValue("FEENABL4")))&&(!mgr.isEmpty("CODENO_E")))
			{
				String strObjectNo = mgr.readValue("OBJECT_NO");

				cmd = trans.addCustomCommand("VAL5","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",mgr.readValue("OBJECT_NO"));
				cmd.addParameter("CODE_PART","E");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strObjectNo) ? "":strObjectNo)+ "^";
				mgr.responseWrite(txt);         
			}
		}

		else if ("PROJECT_NO".equals(val))
		{
			if (("1".equals(mgr.readValue("FEENABL5")))&&(!mgr.isEmpty("CODENO_F")))
			{
				String strProjectNo = mgr.readValue("PROJECT_NO");

				cmd = trans.addCustomCommand("VAL6","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",mgr.readValue("PROJECT_NO"));
				cmd.addParameter("CODE_PART","F");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strProjectNo) ? "":strProjectNo)+ "^";

				mgr.responseWrite(txt);    
			}
		}

		else if ("CODENO_G".equals(val))
		{
			if (("1".equals(mgr.readValue("FEENABL6")))&&(!mgr.isEmpty("CODENO_G")))
			{
				String strCodeG = mgr.readValue("CODENO_G");

				cmd = trans.addCustomCommand("VAL7","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",mgr.readValue("CODENO_G"));
				cmd.addParameter("CODE_PART","G");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strCodeG) ? "":strCodeG)+ "^";
				mgr.responseWrite(txt);       
			}
		}

		else if ("CODENO_H".equals(val))
		{
			if (("1".equals(mgr.readValue("FEENABL7")))&&(!mgr.isEmpty("CODENO_H")))
			{
				String strCodeH = mgr.readValue("CODENO_H");

				cmd = trans.addCustomCommand("VAL8","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",mgr.readValue("CODENO_H"));
				cmd.addParameter("CODE_PART","H");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strCodeH) ? "":strCodeH)+ "^";
				mgr.responseWrite(txt);        
			}
		}

		else if ("CODENO_I".equals(val))
		{
			if (("1".equals(mgr.readValue("FEENABL8")))&&(!mgr.isEmpty("CODENO_I")))
			{
				String strCodeI = mgr.readValue("CODENO_I");

				cmd = trans.addCustomCommand("VAL9","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",mgr.readValue("CODENO_I"));
				cmd.addParameter("CODE_PART","I");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strCodeI) ? "":strCodeI)+ "^";
				mgr.responseWrite(txt);     
			}
		}

		else if ("CODENO_J".equals(val))
		{
			if (("1".equals(mgr.readValue("FEENABL9")))&&(!mgr.isEmpty("CODENO_J")))
			{
				String strCodeJ = mgr.readValue("CODENO_J");

				cmd = trans.addCustomCommand("VAL10","MAINTENANCE_PRE_POSTING_API.VALIDATE_CODEPART");
				cmd.addParameter("CODEVALUE",mgr.readValue("CODENO_J"));
				cmd.addParameter("CODE_PART","J");
				cmd.addParameter("VOUCHER_DATE",mgr.readValue("TODAY"));
				cmd.addParameter("COMPANY",mgr.readValue("COMPANY1"));

				trans = mgr.validate(trans);

				txt = (mgr.isEmpty(strCodeJ) ? "":strCodeJ)+ "^";
				mgr.responseWrite(txt);     
			}
		}

		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  submit()
	{
		ASPManager mgr = getASPManager();

		String obj;
		String att1;
		String att2;
		String att3;
		String att4;
		String att5;
		String att6;
		String att7;
		String att8;
		String att9;
		String att10;
		String att11;
		String attri;        

		int currrow = headset.getCurrentRowNo();

		trans.clear();

		ASPBuffer temp = headset.getRow();
		temp.setValue("COMPANY",tempset.getRow().getValue("COMPANY1"));
		headset.setRow(temp);

		att1 = "ACCOUNT_NO" + (char)31 + mgr.readValue("ACCOUNT_NO","") + (char)30;
		att2 = "COST_CENTER" + (char)31 + mgr.readValue("COST_CENTER","") + (char)30;
		att3 = "CODENO_C" + (char)31 + mgr.readValue("CODENO_C","") + (char)30;
		att4 = "CODENO_D" + (char)31 + mgr.readValue("CODENO_D","") + (char)30;
		att5 = "OBJECT_NO" + (char)31 + mgr.readValue("OBJECT_NO","") + (char)30;
		att6 = "PROJECT_NO" + (char)31 + mgr.readValue("PROJECT_NO","") + (char)30;
		att7 = "CODENO_G" + (char)31 + mgr.readValue("CODENO_G","") + (char)30;
		att8 = "CODENO_H" + (char)31 + mgr.readValue("CODENO_H","") + (char)30;
		att9 = "CODENO_I" + (char)31 + mgr.readValue("CODENO_I","") + (char)30;
		att10 = "CODENO_J" + (char)31 + mgr.readValue("CODENO_J","") + (char)30;
		att11 = "COMPANY" + (char)31 + tempset.getRow().getValue("COMPANY1") + (char)30;
		attri = att1 + att2 + att3 + att4 + att5 + att6 + att7 + att8 + att9 + att10 + att11;

		headset.setEdited("CODENO_C");
		headset.setEdited("CODENO_D");
		headset.setEdited("CODENO_G");
		headset.setEdited("CODENO_H");
		headset.setEdited("CODENO_I");
		headset.setEdited("CODENO_J");
		headset.setEdited("ACCOUNT_NO");
		headset.setEdited("COST_CENTER");
		headset.setEdited("OBJECT_NO");
		headset.setEdited("PROJECT_NO");
		headset.setEdited("COMPANY");

		ASPCommand cmd = trans.addCustomCommand("MODI1","MAINTENANCE_PRE_POSTING_API.Modify__");
		cmd.addParameter("INFO");
		cmd.addParameter("OBJID");
		cmd.addParameter("OBJVERSION");
		cmd.addParameter("ATTR",attri);
		cmd.addParameter("ACTION","CHECK");

		cmd = trans.addCustomCommand("MODI2","MAINTENANCE_PRE_POSTING_API.Modify__");
		cmd.addParameter("INFO");
		cmd.addParameter("OBJID");
		cmd.addParameter("OBJVERSION");
		cmd.addParameter("ATTR",attri);
		cmd.addParameter("ACTION","DO");

		trans = mgr.perform(trans);

		trans.clear();

		obj = headset.getRow().getValue("OBJVERSION");

		cmd = trans.addCustomCommand("PPOST","Active_Work_Order_API.Modify_Wo_Posting");
		cmd.addParameter("WO_NO1",wono);
		cmd.addParameter("PRE_POSTING_ID");

		trans = mgr.perform(trans);

		transferurl = calling_url+"?WO_NO="+ wono;

		trans.clear();
		bCloseWindow = true;
	}        

	public void  cancel()
	{
		ASPManager mgr = getASPManager();
		bCloseWindow = true;
		transferurl = calling_url+"?WO_NO="+ wono;
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		ASPQuery q = trans.addQuery(headblk);
		q.includeMeta("ALL");

		q = trans.addQuery(tempblk);
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWPREPOSTINGDLGNODATA: No data found."));
			headset.clear();
		}

	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();


		tempblk = mgr.newASPBlock("TEMP");

		f = tempblk.addField("COMPANY1");
		f.setHidden();
		f.setFunction("''");

		f = tempblk.addField("TODAY");
		f.setHidden();
		f.setFunction("''");

		f = tempblk.addField("WO_NO1","Number");
		f.setHidden();
		f.setFunction("''");  

		tempblk.setView("DUAL");
		tempset = tempblk.getASPRowSet();

		//------------------------------------------------------------------------------------------------------------------------------

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("PRE_POSTING_ID","Number");
		f.setSize(10);
		f.setMandatory();
		f.setHidden();

		f = headblk.addField("ACCOUNT_NO");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_A","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,ACCOUNT_NO,FEENABL0","ACCOUNT_NO");

		f = headblk.addField("COST_CENTER");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_B","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,COST_CENTER,CODE_PART,FEENABL1","COST_CENTER");

		f = headblk.addField("CODENO_C");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_C","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,CODENO_C,CODE_PART,FEENABL2","CODENO_C");

		f = headblk.addField("CODENO_D");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_D","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,CODENO_D,CODE_PART,FEENABL3","CODENO_D");

		f = headblk.addField("OBJECT_NO");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_E","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,OBJECT_NO,CODE_PART,FEENABL4","OBJECT_NO");

		f = headblk.addField("PROJECT_NO");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_F","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,PROJECT_NO,CODE_PART,FEENABL5","PROJECT_NO");

		f = headblk.addField("CODENO_G");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_G","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,CODENO_G,CODE_PART,FEENABL6","CODENO_G");

		f = headblk.addField("CODENO_H");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_H","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,CODENO_H,CODE_PART,FEENABL7","CODENO_H");

		f = headblk.addField("CODENO_I");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_I","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,CODENO_I,CODE_PART,FEENABL8","CODENO_I");

		f = headblk.addField("CODENO_J");
		f.setSize(8);
		f.setDynamicLOV("PRE_ACCOUNTING_CODEPART_J","COMPANY1",600,450);
		f.setCustomValidation("COMPANY1,TODAY,CODENO_J,CODE_PART,FEENABL9","CODENO_J");

		f = headblk.addField("CONTRACT1");
		f.setSize(6);
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("MODULE");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("FEENABL0");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("FEENABL1");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("FEENABL2");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("FEENABL3");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("FEENABL4");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("FEENABL5");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("FEENABL6");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("FEENABL7");
		f.setHidden();
		f.setFunction("''");


		f = headblk.addField("FEENABL8");
		f.setHidden();
		f.setFunction("''");


		f = headblk.addField("FEENABL9");
		f.setHidden();
		f.setFunction("''");


		f = headblk.addField("FEENABL10");
		f.setHidden();
		f.setFunction("''");


		f = headblk.addField("STR_CODE");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CONTROL_TYPE");
		f.setSize(6);
		f.setHidden();
		f.setFunction(  "''");

		f = headblk.addField("CODENO_A");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODENO_B");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODENO_E");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODENO_F");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMEA");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMEB");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMEC");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMED");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMEE");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMEF");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMEG");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMEH");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMEI");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPARTNAMEJ");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("PREPOSTINGIDEXIST");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CODEPART");
		f.setSize(6);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("COMPANY");
		f.setHidden();

		f = headblk.addField("CODEVALUE");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("CODE_PART");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("VOUCHER_DATE");
		f.setFunction("''");
		f.setHidden();

		//----------------------------------------------------------------------------------------------------------------------------------------------

		tempblk1 = mgr.newASPBlock("TEMP1");

		tempblk1.addField("INFO");
		tempblk1.addField("ATTR");
		tempblk1.addField("ACTION");  

		//-----------------------------------------------------------------------------------------------------------------------------------------------

		headblk.setView("MAINTENANCE_PRE_POSTING");
		headblk.defineCommand("MAINTENANCE_PRE_POSTING_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel","checkHeadFields(-1)");

		headtbl = mgr.newASPTable(headblk);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWPREPOSTINGDLGCODPART: Code Parts"),"ACCOUNT_NO,COST_CENTER,CODENO_C,CODENO_D,OBJECT_NO,PROJECT_NO,CODENO_G,CODENO_H,CODENO_I,CODENO_J",true,true);   
		headlay.setDialogColumns(5);   
	}         

	public void  adjust()
	{
		setLables();
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWPREPOSTINGDLGTITLE: Preposting";
	}

	protected String getTitle()
	{
		return "PCMWPREPOSTINGDLGTITLE: Preposting";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		appendDirtyJavaScript("if('");
		appendDirtyJavaScript(transferurl);
		appendDirtyJavaScript("' != \"\")\n");
		appendDirtyJavaScript("{   \n");
		appendDirtyJavaScript("   window.location = \"");
		appendDirtyJavaScript(transferurl);
		appendDirtyJavaScript("\";\n");
		appendDirtyJavaScript("   transferurl = \"\";\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovAccountNo(i)\n");
		appendDirtyJavaScript("{\n");  
		appendDirtyJavaScript("			openLOVWindow('ACCOUNT_NO',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_A&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameA));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		,600,450,'validateAccountNo');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovCostCenter(i)\n");
		appendDirtyJavaScript("{\n");

		appendDirtyJavaScript("    openLOVWindow('COST_CENTER',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_B&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameB));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));						
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,450,'validateCostCenter');\n");		//XSS_Safe AMNILK 20070525	
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovCodenoC(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	openLOVWindow('CODENO_C',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_C&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameC));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));			//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,450,'validateCodenoC');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovCodenoD(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	openLOVWindow('CODENO_D',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_D&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameD));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));			//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,450,'validateCodenoD');		\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovObjectNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	openLOVWindow('OBJECT_NO',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_E&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameE));		//XSS_Safe AMNILK 20070725 
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));	 		//XSS_Safe AMNILK 20070725       	
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,450,'validateObjectNo');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovProjectNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	openLOVWindow('PROJECT_NO',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_F&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameF));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));			//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,450,'validateProjectNo');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovCodenoG(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	openLOVWindow('CODENO_G',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_G&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameG));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));			//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,450,'validateCodenoG');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovCodenoH(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	openLOVWindow('CODENO_H',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_H&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameH));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));			//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,450,'validateCodenoH');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovCodenoI(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	openLOVWindow('CODENO_I',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_I&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameI));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));			//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,450,'validateCodenoI');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovCodenoJ(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	openLOVWindow('CODENO_J',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PRE_ACCOUNTING_CODEPART_J&__FIELD='+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sCodePartNameJ));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("')\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));			//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,450,'validateCodenoJ');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function validateAccountNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkAccountNo(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('ACCOUNT_NO',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('ACCOUNT_NO',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=ACCOUNT_NO'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");	
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&ACCOUNT_NO=' + URLClientEncode(getValue_('ACCOUNT_NO',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL0=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl0));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'ACCOUNT_NO',i,'ACCOUNT_NO') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('ACCOUNT_NO',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");    

		appendDirtyJavaScript("function validateCostCenter(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCostCenter(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('COST_CENTER',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('COST_CENTER',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=COST_CENTER'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));	  	//XSS_Safe AMNILK 20070725      	
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&COST_CENTER=' + URLClientEncode(getValue_('COST_CENTER',i))\n");
		appendDirtyJavaScript("		+ '&CODE_PART=' + URLClientEncode(getValue_('CODE_PART',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'COST_CENTER',i,'COST_CENTER') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('COST_CENTER',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");    

		appendDirtyJavaScript("function validateCodenoC(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCodenoC(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('CODENO_C',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('CODENO_C',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CODENO_C'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&CODENO_C=' + URLClientEncode(getValue_('CODENO_C',i))\n");
		appendDirtyJavaScript("		+ '&CODE_PART=' + URLClientEncode(getValue_('CODE_PART',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL2=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl2));  		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'CODENO_C',i,'CODENO_C') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('CODENO_C',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");    

		appendDirtyJavaScript("function validateCodenoD(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCodenoD(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('CODENO_D',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('CODENO_D',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CODENO_D'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&CODENO_D=' + URLClientEncode(getValue_('CODENO_D',i))\n");
		appendDirtyJavaScript("		+ '&CODE_PART=' + URLClientEncode(getValue_('CODE_PART',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL3=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl3));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'CODENO_D',i,'CODENO_D') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('CODENO_D',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");    

		appendDirtyJavaScript("function validateObjectNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkObjectNo(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('OBJECT_NO',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('OBJECT_NO',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=OBJECT_NO'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&OBJECT_NO=' + URLClientEncode(getValue_('OBJECT_NO',i))\n");
		appendDirtyJavaScript("		+ '&CODE_PART=' + URLClientEncode(getValue_('CODE_PART',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL4=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl4));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'OBJECT_NO',i,'OBJECT_NO') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('OBJECT_NO',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");    

		appendDirtyJavaScript("function validateProjectNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkProjectNo(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('PROJECT_NO',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('PROJECT_NO',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=PROJECT_NO'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&PROJECT_NO=' + URLClientEncode(getValue_('PROJECT_NO',i))\n");
		appendDirtyJavaScript("		+ '&CODE_PART=' + URLClientEncode(getValue_('CODE_PART',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL5=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl5));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'PROJECT_NO',i,'PROJECT_NO') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('PROJECT_NO',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");    

		appendDirtyJavaScript("function validateCodenoG(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCodenoG(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('CODENO_G',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('CODENO_G',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CODENO_G'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));	        //XSS_Safe AMNILK 20070725 	
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&CODENO_G=' + URLClientEncode(getValue_('CODENO_G',i))\n");
		appendDirtyJavaScript("		+ '&CODE_PART=' + URLClientEncode(getValue_('CODE_PART',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL6=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl6));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'CODENO_G',i,'CODENO_G') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('CODENO_G',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");    

		appendDirtyJavaScript("function validateCodenoH(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCodenoH(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('CODENO_H',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('CODENO_H',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CODENO_H'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&CODENO_H=' + URLClientEncode(getValue_('CODENO_H',i))\n");
		appendDirtyJavaScript("		+ '&CODE_PART=' + URLClientEncode(getValue_('CODE_PART',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL7=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl7));
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'CODENO_H',i,'CODENO_H') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('CODENO_H',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");    

		appendDirtyJavaScript("function validateCodenoI(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCodenoI(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('CODENO_I',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('CODENO_I',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CODENO_I'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");	
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&CODENO_I=' + URLClientEncode(getValue_('CODENO_I',i))\n");
		appendDirtyJavaScript("		+ '&CODE_PART=' + URLClientEncode(getValue_('CODE_PART',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL8=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl8));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'CODENO_I',i,'CODENO_I') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('CODENO_I',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");    

		appendDirtyJavaScript("function validateCodenoJ(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkCodenoJ(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('CODENO_J',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('CODENO_J',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CODENO_J'\n");
		appendDirtyJavaScript("		+ '&COMPANY1=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(company1));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&TODAY=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(today));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&CODENO_J=' + URLClientEncode(getValue_('CODENO_J',i))\n");
		appendDirtyJavaScript("		+ '&CODE_PART=' + URLClientEncode(getValue_('CODE_PART',i))\n");
		appendDirtyJavaScript("		+ '&FEENABL9=' + '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(enabl9));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("	if( checkStatus_(r,'CODENO_J',i,'CODENO_J') )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		assignValue_('CODENO_J',i,0);\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("}\n");

		if (bCloseWindow)
			appendDirtyJavaScript("  window.close();\n");
	}
}
