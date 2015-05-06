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
*  File        : ProjectActivityInfoDlg.java
* ---------------------------------------------------------------------------- 
*  050503  NIJALK  Bug 123677: Created.
*  Modified:
*  060210  NIJALK  Bug 132956: Modified run(),showRow().Added testing script at the end
*                  of the printContents().
*  060215  NIJALK  Bug 132956: Removed testing script.
*  061116  NAMELK  modified showRow().
*  070725  AMNILK  Eliminated XSS Security Vulnerability.
*  080202  NIJALK  Bug 66456, Modified showRow().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ProjectActivityInfoDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ProjectActivityInfoDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPBlockLayout headlay;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPField f;
	private ASPHTMLFormatter fmt;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================

	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
        
        private String wo_no;
        private String acivity_seq;

        private double activity_sequence;

	private boolean win_cls;


	//===============================================================
	// Construction 
	//===============================================================
	public ProjectActivityInfoDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                wo_no = "";
                acivity_seq = "";
                activity_sequence = 0;
                     
		ASPManager mgr = getASPManager();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();
                              
                wo_no = ctx.readValue("WO_NO","");
                acivity_seq = ctx.readValue("ACTIVITY_SEQ",""); 
                activity_sequence = ctx.readNumber("ACTIVITY_SEQ_NUM",activity_sequence); 
		win_cls = ctx.readFlag("WINCLS",win_cls);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("ACTIVITY_SEQ")))
		{
			wo_no = mgr.readValue("WO_NO");
			acivity_seq = mgr.readValue("ACTIVITY_SEQ");
                        activity_sequence = mgr.readNumberValue("ACTIVITY_SEQ");

			showRow();
		}
                else if (mgr.buttonPressed("CLOSE"))
                    close();

		ctx.writeValue("WO_NO",wo_no);
		ctx.writeValue("ACTIVITY_SEQ",acivity_seq);
                ctx.writeNumber("ACTIVITY_SEQ_NUM",activity_sequence);
		ctx.writeFlag("WINCLS",win_cls);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

        public void showRow()
        {
            ASPManager mgr = getASPManager();
            String sProjLuName = "ASWO";

            //Bug 66456, Start, Added check on PROJ
            if (mgr.isModuleInstalled("PROJ"))
            {
                cmd = trans.addCustomFunction("GETPROGRAMID","Activity_API.Get_Program_Id","PROGARM_ID");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETPROGRAMDESC","Activity_API.Get_Program_Description","PROGARM_DESC");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETPROJECTID","Activity_API.Get_Project_Id","PROJECT_ID");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETPROJECTDESC","Activity_API.Get_Project_Name","PROJECT_DESC");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETSUBSUBPROJECTID","Activity_API.Get_Sub_Project_Id","SUB_PROJECT_ID");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETSUBSUBPROJECTDESC","Activity_API.Get_Sub_Project_Description","SUB_PROJECT_DESC");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
                    
                cmd = trans.addCustomFunction("GETACTIVITYID","Activity_API.Get_Activity_No","ACTIVITY_ID");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETACTIVITYIDESC","Activity_API.Get_Description","ACTIVITY_DESC");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETSTATUS","Activity_API.Get_State","STATUS");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETEARLYSTART","Activity_API.Get_Early_Start","EARLY_START");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETEARLYFINISH","Activity_API.Get_Early_Finish","EARLY_FINISH");
                cmd.addParameter("ACTIVITY_SEQ",acivity_seq);
    
                cmd = trans.addCustomFunction("GETEXCEPTION","Activity_API.Object_Exceptions_Exist","EXCEPTION");
                cmd.addParameter("PROJECT_LU",sProjLuName);
                cmd.addParameter("WO_NO",wo_no);
                cmd.addParameter("KEY_REF1","");
                cmd.addParameter("KEY_REF2","");
                    
                trans = mgr.perform(trans);
    
                ASPBuffer temp = mgr.newASPBuffer();
                temp.setFieldItem("PROGARM_ID",trans.getValue("GETPROGRAMID/DATA/PROGARM_ID"));
                temp.setFieldItem("PROGARM_DESC",trans.getValue("GETPROGRAMDESC/DATA/PROGARM_DESC"));
                temp.setFieldItem("PROJECT_ID",trans.getValue("GETPROJECTID/DATA/PROJECT_ID"));
                temp.setFieldItem("PROJECT_DESC",trans.getValue("GETPROJECTDESC/DATA/PROJECT_DESC"));
                temp.setFieldItem("SUB_PROJECT_ID",trans.getValue("GETSUBSUBPROJECTID/DATA/SUB_PROJECT_ID"));
                temp.setFieldItem("SUB_PROJECT_DESC",trans.getValue("GETSUBSUBPROJECTDESC/DATA/SUB_PROJECT_DESC"));
                temp.setFieldItem("ACTIVITY_ID",trans.getValue("GETACTIVITYID/DATA/ACTIVITY_ID"));
                temp.setFieldItem("ACTIVITY_DESC",trans.getValue("GETACTIVITYIDESC/DATA/ACTIVITY_DESC"));
                temp.setNumberValue("ACTIVITY_SEQ",activity_sequence);
                temp.setFieldItem("STATUS",trans.getValue("GETSTATUS/DATA/STATUS"));
                temp.setFieldItem("EARLY_START",trans.getValue("GETEARLYSTART/DATA/EARLY_START"));
                temp.setFieldItem("EARLY_FINISH",trans.getValue("GETEARLYFINISH/DATA/EARLY_FINISH"));
                if (trans.getNumberValue("GETEXCEPTION/DATA/EXCEPTION") == 1)
                    temp.setFieldItem("EXCEPTION","TRUE"); 
                else
                    temp.setFieldItem("EXCEPTION","FALSE"); 
    
                headset.addRow(temp);
            }
            //Bug 66456, End
        }

	public void  close()
	{
		win_cls = true;
	}  

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("PROGARM_ID");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGPROGARMID: Program ID");
                f.setReadOnly();

		f = headblk.addField("PROGARM_DESC");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGPROGARMDESC: Program Description");
                f.setReadOnly();

		f = headblk.addField("PROJECT_ID");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGPROJECTID: Project ID");
                f.setReadOnly();

		f = headblk.addField("PROJECT_DESC");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGPROJECTDESC: Project Description");
                f.setReadOnly();

		f = headblk.addField("SUB_PROJECT_ID");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGSUBPROJECTID: Sub Project ID");
                f.setReadOnly();

		f = headblk.addField("SUB_PROJECT_DESC");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGSUBPROJECTDESC: Sub Project Description");
                f.setReadOnly();

		f = headblk.addField("ACTIVITY_ID");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGACTIVITYID: Activity ID");
                f.setReadOnly();

		f = headblk.addField("ACTIVITY_DESC");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGACTIVITYDESC: Activity Description");
                f.setReadOnly();

		f = headblk.addField("ACTIVITY_SEQ","Number");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGACTIVITYSEQ: Activity Sequence");
                f.setReadOnly();

		f = headblk.addField("STATUS");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGSTATUS: Activity Status");
                f.setReadOnly();

		f = headblk.addField("EARLY_START");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGEARLYSTART: Early Start");
                f.setReadOnly();

		f = headblk.addField("EARLY_FINISH");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGEARLYFINISH: Early Finish");
                f.setReadOnly();

		f = headblk.addField("EXCEPTION");
		f.setLabel("PCMWPROJECTACTIVITYINFODLGEARLYEXCEPTION: Connected Work Order has caused a Project Exception.");
                f.setCheckBox("FALSE,TRUE");
                f.setReadOnly();

		f = headblk.addField("KEY_REF1");
                f.setHidden();
                f.setFunction("''");

		f = headblk.addField("KEY_REF2");
                f.setHidden();
                f.setFunction("''");

		f = headblk.addField("PROJECT_LU");
                f.setHidden();
                f.setFunction("''");

		f = headblk.addField("WO_NO");
                f.setHidden();
                f.setFunction("''");

		headblk.setView("DUAL");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
                headbar.disableMinimize();
		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
                
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setDialogColumns(2);
		headtbl.disableRowStatus();
                this.disableHomeIcon();
                this.disableOptions();
                this.disableNavigate();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
            ASPManager mgr = getASPManager();
            return (mgr.translate("PCMWPROJECTACTIVITYINFODLGTITLE: Activity Information for Work Order - &1 ",wo_no));
	}

	protected String getTitle()
	{
            ASPManager mgr = getASPManager();
            return (mgr.translate("PCMWPROJECTACTIVITYINFODLGTITLE: Activity Information for Work Order - &1 ",wo_no));
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		
                appendToHTML(headlay.show());
                
		appendToHTML("<table>\n");
		appendToHTML("   <tr>\n");
		appendToHTML("      <td>");
                appendToHTML(fmt.drawSubmit("CLOSE",mgr.translate("PCMWCLOSE:  Close "),"CLOSE"));
		appendToHTML("      </td>\n");
		appendToHTML("   </tr>\n");
		appendToHTML("</table>\n");

                appendDirtyJavaScript("window.name = \"ProjectActivityInfoDlg\"\n");

		appendDirtyJavaScript("if (");
                appendDirtyJavaScript(win_cls);		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");
                                
	}
}
