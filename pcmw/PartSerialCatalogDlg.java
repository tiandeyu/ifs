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
*  File        : PartSerialCatalogDlg.java 
*  Created     : ASP2JAVA Tool  010308
*  Modified    :
*  SHFELK  010308  Corrected some conversion errors.
*  JEWILK  010403  Changed file extensions from .asp to .page
*  KUWILK  020322  Call ID 79668 corrected.
*  GACOLK  021204  Set Max Length of SERIAL_NO1 to 50
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  SAPRLK  040401  Made Changes for Web Alignment.
*  ARWILK  041111  Replaced getContents with printContents.
*  AMNILK  070719  Eliminated XSS Security Vulnerability.
*  IMGULK  090304  BUG ID 81035 Corrected initial values on fields.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class PartSerialCatalogDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PartSerialCatalogDlg");

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
	private String wo_no;
	private String err_descr;
	private String swono;
	private String serrdescr;
	private String mchcode;
	private String equipmentobjectmchname;
	private String equipmentobjectmchname_tag;
	private String contract;
	private String replacedescription;
	private ASPTransactionBuffer trans;
	private String calling_url;
	private String updateval;
	private ASPBuffer buff;
	private ASPBuffer row;
	private ASPCommand cmd;
	private String serial_no1;
	private String part_no;
	private String isRepairFlag;
	private ASPBuffer temp;
	private String val;
	private String strMchName;
	private int startpos;
	private String new_part_no;
	private int i;
	private int endpos;
	private String reqstr;
	private String strRepDesc;
	private boolean overview;
	private boolean multirow;
	private ASPBuffer data;
	private ASPBuffer buffer;
	private ASPQuery q;
	private String n;
	private String title_;
	private String xx;
	private String yy;
	private String txt; 
	private String strPartNo; 
	private String strSerialNo; 
	private boolean checkok;
	private String frmPath;

	//===============================================================
	// Construction 
	//===============================================================
	public PartSerialCatalogDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		wo_no = "";
		err_descr = "";
		swono = "";
		serrdescr = "";
		mchcode = "";
		equipmentobjectmchname = "";
		equipmentobjectmchname_tag = "";
		contract = "";
		replacedescription = "";
		checkok = false;

		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		calling_url=ctx.getGlobal("CALLING_URL");
		wo_no  = ctx.readValue("CTX1",wo_no);
		err_descr= ctx.readValue("CTX2",err_descr);
		updateval=mgr.readValue("UPDATEVAL","");
		checkok = ctx.readFlag("CHECKOK",false);


		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
		{
			buff = mgr.getTransferedData();
			row = buff.getBufferAt(0); 
			wo_no = row.getFieldValue("WO_NO");
			err_descr = row.getFieldValue("ERR_DESCR");
			startup();
		}
		adjust();
		ctx.writeFlag("CHECKOK",checkok);

		ctx.writeValue("CTX1",wo_no);
		ctx.writeValue("CTX2",err_descr);
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------


	private String createTransferUrl(String url, ASPBuffer object)
	{
		ASPManager mgr = getASPManager();
		try
		{
			String pkg = mgr.pack(object,1900-url.length());
			char sep = url.indexOf('?')>0 ? '&' : '?';
			frmPath = url + sep + "__TRANSFER=" + pkg ;
			return frmPath;
		}
		catch (Throwable any)
		{
			return null;
		}
	}

	public void startup()
	{
		ASPManager mgr = getASPManager();
		isRepairFlag="";

		trans.clear();
		eval(headblk.generateAssignments("MCHNAME,CONTRACT,MCHCODE,PART_NO",headset.getRow()));

		cmd = trans.addCustomFunction("ISREPFLAG","Active_Work_Order_API.Get_Repair_Flag","ISREPAREFLAG");
		cmd.addParameter("WO_NO",wo_no);

		cmd = trans.addCustomFunction("CONTR","ACTIVE_SEPARATE_API.Get_Contract","CONTRACT");
		cmd.addParameter("WO_NO",wo_no);

		cmd = trans.addCustomFunction("MCHCOD","ACTIVE_SEPARATE_API.Get_Mch_Code","MCHCODE");
		cmd.addParameter("WO_NO",wo_no);

		cmd = trans.addCustomFunction("MCNAM","Maintenance_Object_API.Get_Mch_Name","MCHNAME");
		cmd.addReference("CONTRACT","CONTR/DATA");
		cmd.addReference("MCHCODE","MCHCOD/DATA");

		cmd = trans.addCustomFunction("PARTNO","Equipment_Serial_API.Get_Part_No","PART_NO");
		cmd.addReference("CONTRACT","CONTR/DATA");
		cmd.addReference("MCHCODE","MCHCOD/DATA");

		cmd = trans.addCustomFunction("PARTDESCX","Part_Catalog_API.Get_Description","REPLACEDESCRIPTION");
		cmd.addReference("PART_NO","PARTNO/DATA");

		cmd = trans.addCustomFunction("SERIALNOX","Equipment_Serial_API.Get_Serial_No","SERIAL_NO1");
		cmd.addReference("CONTRACT","CONTR/DATA");
		cmd.addReference("MCHCODE","MCHCOD/DATA");

		trans = mgr.perform(trans);

        // BUG ID: 81035 START
		replacedescription = trans.getValue("PARTDESCX/DATA/REPLACEDESCRIPTION");
		
		serial_no1 = trans.getValue("SERIALNOX/DATA/SERIAL_NO1");
		part_no = trans.getValue("PARTNO/DATA/PART_NO");		
		mchcode = trans.getValue("MCHCOD/DATA/MCHCODE");
		equipmentobjectmchname = trans.getValue("MCNAM/DATA/MCHNAME");
		contract = trans.getValue("CONTR/DATA/CONTRACT");		
		isRepairFlag = trans.getValue("ISREPFLAG/DATA/ISREPAREFLAG");

		if("TRUE".equals(isRepairFlag))
		{
			mchcode = "";
			equipmentobjectmchname = "";
		}
		else
		{
			part_no = "";
			serial_no1 = "";
			replacedescription = "";
		}

		headset.addRow(trans.getBuffer("HEAD/DATA"));
		temp = headset.getRow();	
		
		temp.setValue("CONTRACT", contract);
		temp.setValue("MCHCODE", mchcode);
		temp.setValue("MCHNAME", equipmentobjectmchname);
		
		temp.setValue("SERIAL_NO1",serial_no1);
		temp.setValue("PART_NO",part_no);
		temp.setValue("REPLACEDESCRIPTION",replacedescription);
					
		temp.setValue("ISREPAREFLAG", isRepairFlag);                
		headset.setRow(temp);
        // BUG ID: 81035 END
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("MCHCODE".equals(val))
		{
			cmd = trans.addCustomFunction("MCHNA","Maintenance_Object_API.Get_Mch_Name","MCHNAME");
			cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT",""));
			cmd.addParameter("MCHCODE");

			trans = mgr.validate(trans);

			strMchName = trans.getValue("MCHNA/DATA/MCHNAME");

			txt = (mgr.isEmpty(strMchName) ? "" :strMchName) + "^" ;

			mgr.responseWrite(txt);
		}

		if ("PART_NO".equals(val))
		{

			String ar[] = new String[2];
			startpos = 0;
			new_part_no =mgr.readValue("PART_NO","");
			if (new_part_no.indexOf("^",0)>0)
			{
				for (i=0 ; i<2; i++)
				{
					endpos = new_part_no.indexOf("^",startpos);
					reqstr = new_part_no.substring(startpos,endpos);
					ar[i] = reqstr;
					startpos= endpos+1;
				}
				strPartNo = ar[0];
				strSerialNo = ar[1];
			}
			else
			{
				strPartNo=new_part_no;
				strSerialNo ="";
			}

			cmd = trans.addCustomFunction("REPDESC","Part_Catalog_API.Get_Description","REPLACEDESCRIPTION");
			cmd.addParameter("PART_NO",strPartNo);

			trans = mgr.validate(trans);

			strRepDesc = trans.getValue("REPDESC/DATA/REPLACEDESCRIPTION");

			txt = (mgr.isEmpty(strRepDesc ) ? "" :strRepDesc ) + "^"  + (mgr.isEmpty( strSerialNo) ? "" : strSerialNo) + "^"  +  (mgr.isEmpty(strPartNo ) ? "" :strPartNo ) + "^";

			mgr.responseWrite(txt);
		}
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void prepare()
	{
		ASPManager mgr = getASPManager();

		overview = false;
		multirow = false;
		cmd = trans.addEmptyCommand("HEAD","PART_SERIAL_CATALOG_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

	public void ok()
	{
		ASPManager mgr = getASPManager();

		headset.changeRow();
		part_no = mgr.readValue("PART_NO","");
		contract = mgr.readValue("CONTRACT","");
		mchcode  =  mgr.readValue("MCHCODE","");
		serial_no1 = mgr.readValue("SERIAL_NO1","");

		trans.clear();
		cmd = trans.addCustomCommand("PARTSER", "Equipment_Object_API.Move_From_Invent_To_Facility");
		cmd.addParameter("CONTRACT",contract);
		cmd.addParameter("MCHCODE",mchcode);
		cmd.addParameter("PART_NO",part_no);
		cmd.addParameter("SERIAL_NO1",serial_no1);
		trans = mgr.perform(trans);
		trans.clear();

		swono  = ctx.readValue("CTX1",swono);
		serrdescr= ctx.readValue("CTX2",serrdescr);

		buffer=mgr.newASPBuffer();
		row=buffer.addBuffer("0");
		row.addItem("WO_NO",swono);
		row.addItem("ERR_DESCR",serrdescr);

		checkok = true;
		frmPath = createTransferUrl("ActiveSeperateReportInWorkOrder.page", buffer);
		//mgr.transferDataTo("ActiveSeperateReportInWorkOrder.page",buffer);      
	}

	public void cancel()
	{
		ASPManager mgr = getASPManager();

		swono  = ctx.readValue("CTX1",swono);
		serrdescr= ctx.readValue("CTX2",serrdescr);

		buffer=mgr.newASPBuffer();
		row=buffer.addBuffer("0");
		row.addItem("WO_NO",swono);

		//mgr.redirectTo(calling_url+"?WO_NO="+wo_no);
		checkok = true;
		frmPath = calling_url+"?WO_NO="+mgr.URLEncode(wo_no);

	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void clear()
	{

		headset.clear();
		headtbl.clearQueryRow();
	}

	public void count()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.setStatusLine("PCMWPARTSERIALCATALOGDLGNODATA: No data found.");
		}
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
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("ERR_DESCR");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("MCHCODE");
		f.setSize(20);
		f.setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450);
		f.setLabel("PCMWPARTSERIALCATALOGDLGMCHCODE: Object ID");
		f.setCustomValidation("MCHCODE,CONTRACT","MCHNAME");
		f.setUpperCase();
		f.setFunction("''");

		f = headblk.addField("CONTRACT");
		f.setSize(8);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
		f.setLabel("PCMWPARTSERIALCATALOGDLGCONTRACT: Site");
		f.setUpperCase();
		f.setMandatory();
		f.setFunction("''");   

		f = headblk.addField("MCHNAME ");
		f.setSize(38);
		f.setLabel("PCMWPARTSERIALCATALOGDLGMCHNAME: Description");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("REPLACEDESCRIPTION");
		f.setSize(38);
		f.setLabel("PCMWPARTSERIALCATALOGDLGREPLACEDESC: Description");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("PART_NO");
		f.setSize(20);
		f.setLOV("PartSerialIssueLov.page",600,450);
		f.setLabel("PCMWPARTSERIALCATALOGDLGPART_NO: Part No");
		f.setUpperCase();
		f.setMandatory();
		f.setCustomValidation("PART_NO","REPLACEDESCRIPTION,SERIAL_NO1,PART_NO");
		f.setFunction("''");

		f = headblk.addField("SERIAL_NO1");
		f.setSize(8);
		f.setMaxLength(50);
		f.setDynamicLOV("PART_SERIAL_CATALOG","PART_NO",600,450);
		f.setLabel("PCMWPARTSERIALCATALOGDLGSERIALNO1: Serial No");
		f.setMandatory();
		f.setFunction("''");

		f = headblk.addField("SERIAL_NO");
		f.setHidden(); 

		f = headblk.addField("ALTERNATE_ID");
		f.setSize(8);
		f.setHidden();

		f = headblk.addField("ISREPAREFLAG");
		f.setHidden();
		f.setFunction("''");


		headblk.setView("PART_SERIAL_CATALOG");
		headblk.defineCommand("PART_SERIAL_CATALOG_API","New__,Modify__,Remove__");


		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DELETE);


		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWPARTSERIALCATALOGDLGGRPLABEL1: Object To Place Serial In"),"MCHCODE,CONTRACT,MCHNAME",true,true);
		headlay.defineGroup(mgr.translate("PCMWPARTSERIALCATALOGDLGGRPLABEL2: Serial To Place"),"PART_NO,SERIAL_NO1,REPLACEDESCRIPTION",true,true);

		headbar.defineCommand(headbar.SAVERETURN,"ok","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");        
	}


	public void adjust()
	{
		ASPManager mgr = getASPManager();

		title_ = mgr.translate("PCMWPARTSERIALCATALOGDLGPLCSERINES: Place Serial in Equipment Structure for Work Order - ");
		xx = wo_no;
		yy = err_descr;
		
        // BUG ID: 81035 START
		mgr.setInitialFocus("PART_NO");
        // BUG ID: 81035 END
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return title_ + xx + " " + yy;
	}

	protected String getTitle()
	{
		return "PCMWPARTSERIALCATALOGDLGPLCSERINEQS: Place Serial in Equipment Structure for Work Order";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		if (checkok)
		{
    			//XSS_Safe AMNILK 20070718
			appendDirtyJavaScript("  window.opener.location = '" + mgr.encodeStringForJavascript(frmPath) + "';\n");
			appendDirtyJavaScript("  window.close();\n");
		}
	}
}
