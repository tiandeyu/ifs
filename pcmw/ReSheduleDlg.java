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
*  File        : ReSheduleDlg.java 
*  Created     : 050720  THWILK  Created.
*  Modified    :  
*  THWILK      051109  Modified Predefine().
*  AMNILK      070725  Eliminated XSS Security Vulnerability.
*  NIJALK      070802  Bug 66822, Modified run(), printContents(), submit(), preDefine() & startup(). Added method validateDateCombination().
*  AMDILK      070810  Merged bug 66822
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
import java.util.*;

public class ReSheduleDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ReSheduleDlg");

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
	
        private ASPTransactionBuffer trans;
        private ASPBuffer row;
        private ASPBuffer data;
	private ASPCommand cmd;
        private String val;
        private String startDate;
        private String endDate;
        private String woNo;
        private String rowNo;
        private String strLu;
        private String frameName;
        private String qryStr;
        private String calling_url;
        private boolean closewin;
        private boolean temp;
        private String sValidatePass;

	//===============================================================
	// Construction 
	//===============================================================
	public ReSheduleDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                
		ASPManager mgr = getASPManager();
                ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

                closewin      = ctx.readFlag("CLOSEWIN",false);
                woNo          = ctx.readValue("WO_NO","");
		rowNo         = ctx.readValue("ROW_NO","");
                strLu         = ctx.readValue("PARENT_LU","");
                qryStr        = ctx.readValue("QRYSTR","");
                frameName     = ctx.readValue("FRMNAME","");
                calling_url   = ctx.readValue("CALLING_URL","");
                sValidatePass = ctx.readValue("VALPASS","TRUE");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
	      
		else if (!mgr.isEmpty(mgr.getQueryStringValue("RES_TYPE")))
		{
                        woNo      = mgr.readValue("WO_NO"); 
			rowNo     = mgr.readValue("ROW_NO",""); 
                        strLu     = mgr.readValue("PARENT_LU"); 
                        qryStr    = mgr.readValue("QRYSTR","");
                        frameName = mgr.readValue("FRMNAME","");
                        startup();
		}
                else if (mgr.buttonPressed("OK"))
		   submit();
                else if ("TRUE".equals(mgr.readValue("VALIDATEDATES")))
                    temp = validateDateCombination();
	       

                ctx.writeValue("WO_NO",woNo);
		ctx.writeValue("ROW_NO",rowNo);
                ctx.writeValue("PARENT_LU",strLu);
                ctx.writeFlag("CLOSEWIN",closewin);
                ctx.writeValue("QRYSTR",qryStr);
                ctx.writeValue("FRMNAME",frameName);
                ctx.writeValue("CALLING_URL",calling_url);
                ctx.writeValue("VALPASS",sValidatePass);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

        public boolean validateDateCombination()
        {
            ASPManager mgr = getASPManager(); 
            String sOracleFormat = "YYYY-MM-DD-HH24.MI.SS";
            
            ASPBuffer buf = mgr.newASPBuffer();
            buf.setFieldItem("PLAN_S_DATE",mgr.readValue("PLAN_S_DATE"));
            buf.setFieldItem("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));

            trans.clear();
            ASPQuery q = trans.addQuery("GETDIFF","SELECT (TO_DATE('"+buf.getValue("PLAN_F_DATE")+"','"+sOracleFormat+"') - TO_DATE('"+buf.getValue("PLAN_S_DATE")+"','"+sOracleFormat+"')) DIFF FROM DUAL");
            trans = mgr.perform(trans);

            headset.changeRow();
            headset.setValue("CBINCLUDESCHEDULE","1");

            if (trans.getNumberValue("GETDIFF/DATA/DIFF") < 0)
            {
                sValidatePass = "FALSE";
                return false;
            }
            else
            {
                sValidatePass = "TRUE";
                return true;
            }
        }

	public void submit()
	{
		ASPManager mgr = getASPManager();   
                trans.clear();
		String operations;
		String tools;
		String compDurations;
                String empAlloc;
                String workTimeCompliance;
                String includeSchedule;
		
                if ((!mgr.isEmpty(mgr.readValue("PLAN_S_DATE"))) && (!mgr.isEmpty(mgr.readValue("PLAN_F_DATE"))))
                {
                    if (!validateDateCombination())
                        return;
                }

                if ("1".equals(mgr.readValue("CBOPTIMIZEOPERATIONS")))
			operations = "TRUE";
		else
			operations = "FALSE"; 

		if ("1".equals(mgr.readValue("CBOPTIMIZETOOLS")))
                        tools = "TRUE"; 
		else
			tools = "FALSE"; 

		if ("1".equals(mgr.readValue("CBCOMPRESSDURATIONS")))
			compDurations = "TRUE";
		else
			compDurations = "FALSE"; 

		if ("1".equals(mgr.readValue("CBEMPALLOCATIONS")))
			empAlloc = "TRUE";
		else
			empAlloc = "FALSE"; 

		if ("1".equals(mgr.readValue("CBWORKTIMECOMPLIANCE")))
			workTimeCompliance = "TRUE";
		else
			workTimeCompliance = "FALSE"; 

		if ("1".equals(mgr.readValue("CBINCLUDESCHEDULE")))
			includeSchedule = "TRUE";
		else
			includeSchedule = "FALSE"; 

               ASPBuffer buf = mgr.newASPBuffer();
               buf.setFieldItem("PLAN_S_DATE",mgr.nvl(mgr.readValue("PLAN_S_DATE"),mgr.readValue("PLANSDATE_DUMMY")));
               buf.setFieldItem("PLAN_F_DATE",mgr.nvl(mgr.readValue("PLAN_F_DATE"),mgr.readValue("PLANFDATE_DUMMY")));

               String attr = "WO_NO" + (char)31 + woNo + (char)30 +
                             "ROW_NO" + (char)31 + rowNo + (char)30 +
                             "START_DATE" + (char)31 + buf.getValue("PLAN_S_DATE") + (char)30 +
                             "END_DATE" + (char)31 + buf.getValue("PLAN_F_DATE") + (char)30 +
                             "OPTIMIZE_OPERATIONS" + (char)31 + operations + (char)30 +
                             "OPTIMIZE_TOOLS" + (char)31 + tools + (char)30 +
                             "COMPRESS_DURATIONS" + (char)31 + compDurations + (char)30 +
                             "EMP_ALLOCATIONS" + (char)31 + empAlloc + (char)30 +
                             "WORK_TIME_COMPLIANCE" + (char)31 + workTimeCompliance + (char)30 +
                             "INCLUDE_SCHEDULE" + (char)31 + includeSchedule + (char)30;


	        trans.clear();
                cmd = trans.addCustomCommand( "SCHEDULEOP","Work_Order_Role_API.Re_Schedule_Operations");
                cmd.addParameter("ATTR",attr);
                
		trans = mgr.perform(trans);  
                closewin = true;
	} 
       
	public void validate()
	{
		ASPManager mgr = getASPManager();
		val = mgr.readValue("VALIDATE");  

		mgr.endResponse();
	}

       public void startup()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		
                cmd = trans.addCustomCommand( "DATES","Work_Order_Role_API.Get_Schedule_Dates");
                cmd.addParameter("PLAN_S_DATE");
		cmd.addParameter("PLAN_F_DATE");
		cmd.addParameter("WO_NO",woNo);
		cmd.addParameter("ROW_NO",rowNo);
		cmd.addParameter("PARENT_LU",strLu); 
		
		trans = mgr.submit(trans);  
                data = mgr.newASPBuffer();
                
                data.setFieldDateItem("PLAN_S_DATE",trans.getBuffer("DATES/DATA").getFieldDateValue("PLAN_S_DATE"));
                data.setFieldDateItem("PLAN_F_DATE",trans.getBuffer("DATES/DATA").getFieldDateValue("PLAN_F_DATE"));
                data.setValue("CBOPTIMIZEOPERATIONS","1");
                data.setValue("CBCOMPRESSDURATIONS","1");
                data.setValue("CBEMPALLOCATIONS","1");
                data.setValue("CBINCLUDESCHEDULE","1");
                
                headset.addRow(data);
	}

       	public void preDefine()
	{
		ASPManager mgr = getASPManager();        

		headblk = mgr.newASPBlock("HEAD");

                headblk.addField("WO_NO").
                setHidden();
                
                headblk.addField("ROW_NO").
                setHidden();
                                     
                headblk.addField("PARENT_LU").
                setHidden();

                headblk.addField("ATTR").
                setHidden();

                headblk.addField("CBOPTIMIZEOPERATIONS").
		setLabel("PCMWRESHEDULEDLGCBOPERATIONS: Operations").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("CBOPTIMIZETOOLS").
		setLabel("PCMWRESHEDULEDLGCBTOOLS: Tools and Facilities").
		setFunction("''").
		setCheckBox("0,1");    

		headblk.addField("CBCOMPRESSDURATIONS").
		setLabel("PCMWRESHEDULEDLGCBCOMPDURATION: Compress Durations").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("CBEMPALLOCATIONS").
		setLabel("PCMWRESHEDULEDLGCBEMPALLOC: Employee Allocations").
		setFunction("''").
		setCheckBox("0,1");

                if (mgr.isModuleInstalled("WRKSCH"))
                {

                headblk.addField("CBWORKTIMECOMPLIANCE").
		setLabel("PCMWRESHEDULEDLGCBCOMPLIANCE: Work Time Compliance").
		setFunction("''").
		setCheckBox("0,1").
                setHidden();
                }

		headblk.addField("CBINCLUDESCHEDULE").
		setLabel("PCMWRESHEDULEDLGCBINCSCHEDULE: Include Schedule").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("PLAN_S_DATE","Datetime").
                setSize(30).
                setLabel("PCMWRESHEDULEDLGPLANSDATE: Earliest Start Date");
                
                headblk.addField("PLAN_F_DATE","Datetime").
                setSize(30).
                setLabel("PCMWRESHEDULEDLGPLANFDATE: Latest End Date");
                
		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
                headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWRESHEDULEDLG: Reshedule Work Order Operations"));
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
                headlay.defineGroup(mgr.translate("PCMWRESHEDULEDLGGROUP1: Optimize Start Dates/Times"),"CBOPTIMIZEOPERATIONS,CBOPTIMIZETOOLS",true,true);
                headlay.defineGroup(mgr.translate("PCMWRESHEDULEDLGGROUP2: Operations"),"CBCOMPRESSDURATIONS,CBEMPALLOCATIONS,CBWORKTIMECOMPLIANCE",true,true);
                headlay.defineGroup(mgr.translate("PCMWRESHEDULEDLGGROUP3: Schedules"),"CBINCLUDESCHEDULE,PLAN_S_DATE,PLAN_F_DATE",true,true);
		headlay.setEditable();
		headlay.setDialogColumns(1);   

                enableConvertGettoPost();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWRESCHEDULEDLGTITLE: Reshedule Work Order Operations";
	}

	protected String getTitle()
	{
		return "PCMWRESCHEDULEDLGTITLE: Reshedule Work Order Operations";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

                printHiddenField("VALIDATEDATES","FALSE");
                printHiddenField("PLANSDATE_DUMMY","");
                printHiddenField("PLANFDATE_DUMMY","");

                appendToHTML(headlay.show());
                appendToHTML("<table id=\"SND\" border=\"0\">\n");
                appendToHTML("<tr>\n");
                appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawSubmit("OK",mgr.translate("PCMWRESCHEDULEDLGBACK: OK"),"OK"));
                appendToHTML("</td>\n");
                appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawButton("CANCEL",mgr.translate("PCMWRESCHEDULEDLGCANCEL: Cancel"),"onClick='window.close();'"));
                appendToHTML("</td>\n");
                appendToHTML("</tr>\n");
                appendToHTML("</table>\n");
                
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
                
                appendDirtyJavaScript("function validateCbincludeschedule(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n"); 
                appendDirtyJavaScript("setDirty();\n");  
                appendDirtyJavaScript("if( !checkCbincludeschedule(i) ) return;\n");  
                appendDirtyJavaScript("sVal = '");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(sValidatePass)); // Bug Id 68773
                appendDirtyJavaScript("';\n");
                appendDirtyJavaScript("if (sVal == 'FALSE')\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("   validationFailed();\n");
                appendDirtyJavaScript("   f.CBINCLUDESCHEDULE.checked = true;\n");
                appendDirtyJavaScript("   return;\n");
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("if(document.form.CBINCLUDESCHEDULE.checked)\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("      document.form.PLAN_S_DATE.disabled = false;\n"); 
                appendDirtyJavaScript("      document.form.PLAN_F_DATE.disabled = false;\n"); 
                appendDirtyJavaScript("      if ((f.PLAN_S_DATE.value == '') && (f.PLAN_F_DATE.value == ''))\n"); 
                appendDirtyJavaScript("         f.OK.disabled = true;\n");
                appendDirtyJavaScript("      else\n");
                appendDirtyJavaScript("         f.OK.disabled = false;\n");
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("   else\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("      f.PLANSDATE_DUMMY.value = f.PLAN_S_DATE.value;\n"); 
                appendDirtyJavaScript("      f.PLANFDATE_DUMMY.value = f.PLAN_F_DATE.value;\n"); 
                appendDirtyJavaScript("      f.PLAN_S_DATE.disabled = true;\n"); 
                appendDirtyJavaScript("      f.PLAN_F_DATE.disabled = true;\n"); 
                appendDirtyJavaScript("      f.OK.disabled = false;\n");
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("}\n");
                
                appendDirtyJavaScript("function validatePlanFDate(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n"); 
                appendDirtyJavaScript("setDirty();\n");  
                appendDirtyJavaScript("if( !checkPlanFDate(i) ) return;\n");
                appendDirtyJavaScript("if(document.form.CBINCLUDESCHEDULE.checked)\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("      if ((f.PLAN_S_DATE.value == '') || (f.PLAN_F_DATE.value == ''))\n"); 
                appendDirtyJavaScript("         f.OK.disabled = true;\n");
                appendDirtyJavaScript("      else\n");
                appendDirtyJavaScript("         f.OK.disabled = false;\n");
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("   else\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("      f.OK.disabled = false;\n"); 
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("   if (f.PLAN_S_DATE.value != '' && f.PLAN_F_DATE.value != '')\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("         f.VALIDATEDATES.value = \"TRUE\";\n");
                appendDirtyJavaScript("         submit();\n");  
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function validatePlanSDate(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n"); 
                appendDirtyJavaScript("setDirty();\n");  
                appendDirtyJavaScript("if( !checkPlanSDate(i) ) return;\n");
                appendDirtyJavaScript("if(document.form.CBINCLUDESCHEDULE.checked)\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("      if ((f.PLAN_S_DATE.value == '') || (f.PLAN_F_DATE.value == ''))\n"); 
                appendDirtyJavaScript("         f.OK.disabled = true;\n");
                appendDirtyJavaScript("      else\n");
                appendDirtyJavaScript("         f.OK.disabled = false;\n");
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("else\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("      f.OK.disabled = false;\n"); 
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("if (f.PLAN_S_DATE.value != '' && f.PLAN_F_DATE.value != '')\n");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("         f.VALIDATEDATES.value = \"TRUE\";\n");
                appendDirtyJavaScript("         submit();\n");  
                appendDirtyJavaScript("   }\n");
                appendDirtyJavaScript("}\n");

                if ("FALSE".equals(sValidatePass))
                    appendDirtyJavaScript("validationFailed();\n");

                appendDirtyJavaScript("function validationFailed()");
                appendDirtyJavaScript("   {\n");
                appendDirtyJavaScript("     alert('"); 
                appendDirtyJavaScript(mgr.translateJavaScript("PCMWRESCHINVALIDDATES: Earliest Start Date can not be greater than  the Latest End Date."));
                appendDirtyJavaScript(" ');\n"); 
                appendDirtyJavaScript("   }\n");
                               
                appendDirtyJavaScript("if (");
                appendDirtyJavaScript(closewin);		//XSS_Safe AMNILK 20070725
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("if (");
                appendDirtyJavaScript(!mgr.isEmpty(qryStr));
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.open('");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr));
                appendDirtyJavaScript("' + \"&PREDECESSORS=1\",'");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName));
                appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                appendDirtyJavaScript("	self.close();\n");  
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("else\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.open('");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url));
                appendDirtyJavaScript("','");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName));
                appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                appendDirtyJavaScript("	self.close();\n");  
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}\n");


	       	}
}
