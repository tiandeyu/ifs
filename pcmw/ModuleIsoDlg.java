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
*  File        : ModuleIsoDlg.java 
*  Created     : 06/06/05 NEKOLK AMUT 115:Isolation and permits
*  Modified    :
*  NEKOLK    150605 added  whereset().
*  NEKOLK    110705 Modified File to get error message pops up.
*  NIJALK    051012 B127816: Modified printContents(), run(),submit().
*  THWILK    060126 Corrected localization errors.
*  NEKOLK    060320 Call 137151.Modified predefine(),modify().
*  AMNILK    060726 Bug Id: 58214. Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK    060807 Merged Bug Id: 58214.
*  AMNILK    070718 Eliminated XSS Security Vulnerability.
*  AMDILK    070802 Modified printContents() in order to function properly in other web browsers apart from ie
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ModuleIsoDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ModuleIsoDlg");

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

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String closeOk;
	private String cancelWin;
	private String CurrDelim;
	private ASPCommand cmd;
	private String strDeliDesc;
	private String mNewDlO;
	private double nNewDlo;
        private String permit_seq;
        private String permit_seq1;
        private String permit_type;
        private String permit_type1;
	private String qrystr;
        private ASPBuffer buff;
	private ASPBuffer row;
        private boolean newWin;
        private ASPQuery q;
	private ASPBuffer temp;	

        private boolean first_request = false;

	//===============================================================
	// Construction 
	//===============================================================
	public ModuleIsoDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager(); 
                permit_seq = "";
		permit_seq1 = "";
		permit_type = "";
		permit_type1 = "";

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		closeOk = ctx.readValue("CLOSEOK","");
		cancelWin = ctx.readValue("CANCELWIN","");
                permit_seq =ctx.readValue("PERMITSEQ",permit_seq);
                permit_seq1 =ctx.readValue("PERMITSEQ1",permit_seq1);
                permit_type =ctx.readValue("PERMITTYPEID",permit_type);
                permit_type1 =ctx.readValue("PERMITTYPEID1",permit_type1);
		qrystr = ctx.readValue("QRYSTR","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
                else if (mgr.dataTransfered())
		{
                        buff = mgr.getTransferedData();
			row = buff.getBufferAt(0); 
			permit_seq = row.getValue("PERMIT_SEQ");
			permit_seq1 = permit_seq;
			row = buff.getBufferAt(1); 
			permit_type = row.getValue("PERMIT_TYPE_ID");
			permit_type = permit_type;
 			okFind();
		}
                else if (!mgr.isEmpty(mgr.getQueryStringValue("PERMIT_SEQ")))
                {
                    first_request=true;
                }
                else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) || !mgr.isExplorer())
                {
			
                        newWin = true;
			permit_seq = mgr.readValue("PERMIT_SEQ");
			permit_seq1 = permit_seq;
			permit_type = mgr.readValue("PERMIT_TYPE_ID");
			permit_type1 = permit_type;
			qrystr = mgr.readValue("QRYSTR");     
			okFind();
                        whereset();
		}

		ctx.writeValue("QRYSTR",qrystr);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  cancel()
	{
		cancelWin = "TRUE";
	}

	public void  submit()
	{
		ASPManager mgr = getASPManager();

		CurrDelim = mgr.readValue("DELIMITATION_ID");
                if (!mgr.isEmpty(CurrDelim))
		{
			cmd = trans.addCustomFunction("DELIDESC","Delimitation_API.Get_Description","DESCRIPTION");
			cmd.addParameter("DELIMITATION_ID",mgr.readValue("DELIMITATION_ID"));

			cmd = trans.addCustomCommand("NEWDLO","Delimitation_Order_Utility_API.Create_Dlo_From_Template");
			cmd.addParameter("DELIMITATION_ORDER_NO");
			cmd.addParameter("DELIMITATION_ID",mgr.readValue("DELIMITATION_ID"));
			cmd.addParameter("PERMIT_TYPE_ID",mgr.readValue("PERMIT_TYPE_ID"));
			cmd.addParameter("PERMIT_SEQ",mgr.readValue("PERMIT_SEQ"));

			trans=mgr.perform(trans);

			strDeliDesc = trans.getValue("DELIDESC/DATA/DESCRIPTION");
		        nNewDlo = trans.getNumberValue("NEWDLO/DATA/DELIMITATION_ORDER_NO");
		        mNewDlO = String.valueOf(nNewDlo);

                        if (mNewDlO.indexOf(".") != -1)
                            mNewDlO = mNewDlO.substring(0,mNewDlO.indexOf("."));

			if (mgr.isEmpty(strDeliDesc))
                                mgr.showError(mgr.translate("PCMWMODULEISODLGNOTREG: This Isolation is not registered!"));
		        else if ((!mgr.isEmpty(strDeliDesc)) && (!this.isNaN(nNewDlo)))
				closeOk = "TRUE";
		}      
	}

        public void okFind()
        {
            ASPManager mgr = getASPManager();

            q = trans.addEmptyQuery(headblk);
            q.includeMeta("ALL");
            mgr.submit(trans);
            if (mgr.dataTransfered())
                q.addOrCondition( mgr.getTransferedData() );

            trans.clear();
            
            temp = headset.getRow();

            temp.setValue("PERMIT_SEQ",permit_seq);
            temp.setValue("PERMIT_TYPE_ID",permit_type);
            headset.setRow(temp);

            if (headset.countRows() == 0)
            {
                mgr.setStatusLine("EQUIPWCOPYWORKORDERDLGNODATA: No data found.");
                headset.clear();
            }
        }



    public void  preDefine()
            {
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("DELIMITATION_ORDER_NO","Number","#");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("DESCRIPTION");
		f.setHidden();
		f.setFunction("''");

                f = headblk.addField("PERMIT_TYPE_ID");
                f.setHidden();
                f.setFunction("''");

                f = headblk.addField("PERMIT_SEQ","Number","#");
                f.setHidden();
                f.setFunction("''");

		f = headblk.addField("DELIMITATION_ID","Number","#");
                f.setSize(22);
		f.setDynamicLOV("CONN_DELIMITATION",600,445);
		//f.setLOVProperty("WHERE","APPROVED_BY IS NOT NULL");
		f.setFunction("''");
		f.setLabel("PCMWMODULEDISOLGDELIMITATION_ID: Isolation ID");

		headblk.setView("MODULE");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"submit");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT); 
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWMODULEISODLGISOLATION: Isolation"),"DELIMITATION_ID",true,true);
	}

    public void whereset()
    {
        ASPManager mgr = getASPManager();
        String permitTyp = headset.getRow().getValue("PERMIT_TYPE_ID");                
        String sWhereStr = "ISOLATION_TYPE IN (SELECT isolation_type FROM conn_permit_isolation  u WHERE u.permit_type_id ='" + permitTyp + "')";
        mgr.getASPField("DELIMITATION_ID").setLOVProperty("WHERE", sWhereStr);

    }

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWMODULEISODLGTITLE: Create Isolation Order from Isolation";
	}

	protected String getTitle()
	{
		return "PCMWMODULEISODLGTITLE: Create Isolation Order from Isolation";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

                if ((first_request)) // && mgr.isExplorer())
              {
                 appendToHTML("<html>\n");
                 appendToHTML("<head></head>\n"); 
                 appendToHTML("<body>"); 
                 appendToHTML("<form name='form' method='POST' action='"+mgr.getURL()+"'>"); 
                 appendToHTML("  <input type=\"hidden\" value=\"OK\" name=\"FIRST_REQUEST\" >"); 
                 appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("PERMIT_SEQ"))+"\" name=\"PERMIT_SEQ\" >"); 
                 appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("PERMIT_TYPE_ID"))+"\" name=\"PERMIT_TYPE_ID\" >"); 
                 appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("QRYSTR"))+"\" name=\"QRYSTR\" >"); 
                 appendToHTML("</form></body></html>"); 

                 appendDirtyJavaScript("document.form.submit();"); 
              }
              else
              {

                appendToHTML(headlay.show());

		appendDirtyJavaScript("if('");     
		appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelWin));		//XSS_Safe AMNILK 20070717
		appendDirtyJavaScript("' == 'TRUE')\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  self.close();\n");
		appendDirtyJavaScript("} \n");

                if (!mgr.isEmpty(qrystr))
                {
                    appendDirtyJavaScript("if('");
                    appendDirtyJavaScript(mgr.encodeStringForJavascript(closeOk));		//XSS_Safe AMNILK 20070717
                    appendDirtyJavaScript("' == 'TRUE')\n");
                    appendDirtyJavaScript("{ \n");
                    appendDirtyJavaScript("alert('");
                    appendDirtyJavaScript(mgr.translateJavaScript("PCMWMODISOTYPNEWISOORD: New Isolation Order No: &1",mNewDlO));
                    appendDirtyJavaScript("');\n");
                    appendDirtyJavaScript("  window.open('");
                    appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr));		//XSS_Safe AMNILK 20070717
                    appendDirtyJavaScript("' + \"&ISOCREATED=1\",'frmPermit',\"\");\n");
                    appendDirtyJavaScript("  self.close();\n");
                    appendDirtyJavaScript("}\n");
                }
                else
                {
                    appendDirtyJavaScript("if('");
                    appendDirtyJavaScript(mgr.encodeStringForJavascript(closeOk));		//XSS_Safe AMNILK 20070717
                    appendDirtyJavaScript("' == 'TRUE')\n");
                    appendDirtyJavaScript("{ \n");
                    appendDirtyJavaScript("  jnNewDlo='");
                    appendDirtyJavaScript(mgr.encodeStringForJavascript(mNewDlO));		//XSS_Safe AMNILK 20070717
                    appendDirtyJavaScript("';\n");
                    appendDirtyJavaScript("  window.open(\"DelimitationOrderTab.page?DELIMITATION_ORDER_NO=\"+URLClientEncode(jnNewDlo),\"delimitation\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                    appendDirtyJavaScript("  self.close();\n");
                    appendDirtyJavaScript("}\n");
                }
              }
	}
}
