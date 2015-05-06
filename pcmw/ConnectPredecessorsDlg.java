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
*  File        : ConnectPredecessorsDlg.java 
*  Created     : THWILK  050623  Created
*  Modified    :
*  THWILK  050707  Modified methods run() getValues() and printContents().  
*  THWILK  050719  Modified printContents(). 
*  THWILK  060126  Corrected localization errors.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  ILSOLK  070709  Eliminated XSS.
*  AMNILK  070713  Eliminated SQL Injections.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ConnectPredecessorsDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ConnectPredecessorsDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPForm frm;
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
        private ASPCommand cmd;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

        private ASPBlock itemblk;
	private ASPRowSet itemset;
	private ASPCommandBar itembar;
        private ASPTable itemtbl;
	private ASPBlockLayout itemlay;
        private ASPQuery q;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
        private boolean saveCloseFlag;
        private ASPBuffer rowBuff;
        private String rowNo;
        private String keyNo1;
        private String keyNo2;
        private String keyNo3;
        private String strLu;
        private String desc;
        private String block;
        private String sPredicessorList;
        private String frameName;
        private String qryStr;
        private String calling_url;
        
	//===============================================================
	// Construction 
	//===============================================================
	public ConnectPredecessorsDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();
		frm = mgr.getASPForm();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
                
                rowBuff     = ctx.readBuffer("ROWBUFF");
                rowNo       = ctx.readValue("ROW_NO","");
                keyNo1      = ctx.readValue("KEY_NO1","");
                keyNo2      = ctx.readValue("KEY_NO2","");
                keyNo3      = ctx.readValue("KEY_NO3","");
                block       = ctx.readValue("BLOCK","");
                qryStr      = ctx.readValue("QRYSTR","");
                frameName   = ctx.readValue("FRMNAME","");
                desc        = ctx.readValue("DESC","");
                strLu       = ctx.readValue("STR_LU","");
                calling_url = ctx.readValue("CALLING_URL","");
                sPredicessorList =  ctx.readValue("PREDLIST","");
                
                if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
                if (mgr.dataTransfered())
                {
                    rowBuff = mgr.getTransferedData();
                    calling_url=ctx.getGlobal("CALLING_URL");
                    getValues();
                }
                else if ("TRUE".equals(mgr.readValue("REFRESHCHILD")))
                    refreshChild();
                else if (mgr.buttonPressed("APPLY"))
		    submit();
                else if (mgr.buttonPressed("SAVECLOSE"))
                {
                    submit();
                    close();
                }
                else if (mgr.buttonPressed("CLOSE"))
                    close();
                else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
		    validate();
                else
		  clear();

                ctx.writeBuffer("ROWBUFF",rowBuff);  
                ctx.writeValue("ROW_NO",rowNo);
                ctx.writeValue("KEY_NO1",keyNo1);
                ctx.writeValue("KEY_NO2",keyNo2);
                ctx.writeValue("KEY_NO3",keyNo3);
                ctx.writeValue("BLOCK",block);
                ctx.writeValue("QRYSTR",qryStr);
                ctx.writeValue("FRMNAME",frameName);
                ctx.writeValue("DESC",desc);
                ctx.writeValue("STR_LU",strLu);
                ctx.writeValue("PREDLIST",sPredicessorList);
                ctx.writeValue("CALLING_URL",calling_url);
         }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

        public void  okFind()
	{
		ASPManager mgr = getASPManager();
                trans.clear();
                q = trans.addQuery(headblk);

		// SQLInjection_Safe AMNILK 20070713

	        q.addWhereCondition("STR_LU  = ? ");
                q.addWhereCondition("KEY_NO1 = ? ");
		q.addWhereCondition("KEY_NO2 = ? ");
                q.addWhereCondition("KEY_NO3 = ? ");
                q.addWhereCondition("ROW_NO  = ? ");

		q.addParameter("STR_LU",strLu);
		q.addParameter("KEY_NO1",keyNo1);
		q.addParameter("KEY_NO2",keyNo2);
		q.addParameter("KEY_NO3",keyNo3);
		q.addParameter("ROW_NO",rowNo);

                q.includeMeta("ALL");
		mgr.querySubmit(trans, headblk);
                eval(headset.syncItemSets());

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWCONNECTPREDECESSORDLGNODATA: No data found."));
			headset.clear();
		}
                else if (headset.countRows()>0 && headlay.isSingleLayout())
                   okFindITEM();
        }
        
        public void okFindITEM()
        {

             ASPManager mgr = getASPManager();
             if (headset.countRows()>0)
              {
                int currrow = headset.getCurrentRowNo();  

                trans.clear();
		
		// SQLInjection_Safe AMNILK 20070713

                q = trans.addQuery(itemblk);
	        q.addWhereCondition("STR_LU  = ? ");
                q.addWhereCondition("KEY_NO1 = ? ");
		q.addWhereCondition("KEY_NO2 = ? ");
                q.addWhereCondition("KEY_NO3 = ? ");
                q.addWhereCondition("ROW_NO  = ? ");

		q.addParameter("STR_LU",strLu);
		q.addParameter("KEY_NO1",keyNo1);
		q.addParameter("KEY_NO2",keyNo2);
		q.addParameter("KEY_NO3",keyNo3);
		q.addParameter("ROW_NO",rowNo);

                q.includeMeta("ALL");
                mgr.submit(trans);
                headset.goTo(currrow);
             }
       } 
       
        public void refreshChild()
        {

            ASPManager mgr = getASPManager();
            trans.clear();
            rowNo = mgr.readValue("ROWVAL");
            if (headset.countRows()>0)
             {

                okFindITEM();

		// SQLInjection_Safe AMNILK 20070713

		q = trans.addQuery("GETEVENT","SELECT DESCRIPTION FROM OPERATIONS_UTIL WHERE KEY_NO1= ? AND KEY_NO2= ? AND KEY_NO3= ? AND ROW_NO= ? ");
		q.addParameter("KEY_NO1",keyNo1);
		q.addParameter("KEY_NO2",keyNo2);
		q.addParameter("KEY_NO3",keyNo3);
		q.addParameter("ROW_NO",rowNo);

                trans = mgr.perform(trans);
                ASPBuffer getEvent = trans.getBuffer("GETEVENT/DATA");
                desc = getEvent.getValue("DESCRIPTION");
                
                if (mgr.isEmpty(desc)) 
                   desc="";
             }
          }

       
        public void close()
        {
           ASPManager mgr = getASPManager();
           saveCloseFlag = true;
           trans.clear();
           if ("WorkOrderRole".equals(strLu)) {
              cmd = trans.addCustomFunction("PRED","Wo_Role_Dependencies_API.Get_Predecessors","PREDLIST");
              cmd.addParameter("KEY_NO1",keyNo1);
              cmd.addParameter("ROW_NO",rowNo);
              trans = mgr.perform(trans);
              sPredicessorList = trans.getValue("PRED/DATA/PREDLIST");
           }
           else if ("PmActionRole".equals(strLu)) {
           
              cmd = trans.addCustomFunction("PRED","Pm_Role_Dependencies_API.Get_Predecessors","PREDLIST");
              cmd.addParameter("KEY_NO1",keyNo1);
              cmd.addParameter("KEY_NO2",keyNo2);
              cmd.addParameter("ROW_NO",rowNo);
              trans = mgr.perform(trans);
              sPredicessorList = trans.getValue("PRED/DATA/PREDLIST");
           }
           else if ("StandardJobRole".equals(strLu)) {
           
              cmd = trans.addCustomFunction("PRED","Std_Job_Role_Dependencies_API.Get_Predecessors","PREDLIST");
              cmd.addParameter("KEY_NO1",keyNo1);
              cmd.addParameter("KEY_NO2",keyNo2);
              cmd.addParameter("KEY_NO3",keyNo3);
              cmd.addParameter("ROW_NO",rowNo);
              trans = mgr.perform(trans);
              sPredicessorList = trans.getValue("PRED/DATA/PREDLIST");
           }
           
           if (mgr.isEmpty(sPredicessorList)) 
              sPredicessorList="-1";
        }


        public void getValues()
        {
            ASPManager mgr     = getASPManager();
            ASPBuffer data_buf =  rowBuff.getBufferAt(0);
            rowNo      = data_buf.getValue("ROW_NO");
            keyNo1     = ctx.getGlobal("KEY_NO1");
            keyNo2     = ctx.getGlobal("KEY_NO2");
            keyNo3     = ctx.getGlobal("KEY_NO3");
            strLu      = ctx.getGlobal("STR_LU");
            block      = mgr.readValue("BLOCK","");
            qryStr     = mgr.readValue("QRYSTR","");
            frameName  = mgr.readValue("FRMNAME","");
            
            okFind();
            desc = headset.getValue("DESCRIPTION");
            if (mgr.isEmpty(desc)) 
                desc="";
           
            if ("WorkOrderRole".equals(strLu)) {
                calling_url= calling_url+"?&WO_NO="+keyNo1;
            }
            else if ("PmActionRole".equals(strLu)) {
                calling_url= calling_url+"?&PM_NO="+keyNo1+"&PM_REVISION="+keyNo2;
            }
            else if ("StandardJobRole".equals(strLu)) {
                calling_url= calling_url+"?&STD_JOB_ID="+keyNo1+"&STD_JOB_CONTRACT="+keyNo2+"&STD_JOB_REVISION="+keyNo3;
            }
         }
        
        public void clear()
	{
		headset.clear();
	}   


//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void submit()
         {
         ASPManager mgr = getASPManager();
         trans.clear();
         itemset.changeRows();
         mgr.submit(trans);
         itemset.refreshAllRows();
         } 


//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

       	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

                headblk.addField("STR_LU").
                setHidden().
                setSize(25);
                
                headblk.addField("KEY_NO1").
                setSize(50).
                setMaxLength(40).
                setHidden();

                headblk.addField("KEY_NO2").
                setSize(20).
                setMaxLength(6).
                setHidden();

                headblk.addField("KEY_NO3").
                setSize(20).
                setMaxLength(6).
                setHidden();

                headblk.addField("ROW_NO","Number").
                setLabel("PCMWCONNECTPREDECESSORSSDLGROWNO: Operation No").
                setHidden().
                setReadOnly();

                headblk.addField("DESCRIPTION").
                setSize(60).
                setHidden();
                
                headblk.setView("OPERATIONS_UTIL");
                headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
                headbar.disableCommand(headbar.FORWARD);
		headbar.disableCommand(headbar.BACKWARD);
                headbar.disableCommand(headbar.FIND);

		headtbl = mgr.newASPTable(headblk);
                headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
		headlay.setDialogColumns(2);
		headlay.setEditable();   
        
                //-----------------ITEMBLK0-----------------------------

                itemblk = mgr.newASPBlock("ITEM");
                
                itemblk.addField("OBJID").
                setHidden();
                
                itemblk.addField("OBJVERSION").
                setHidden();
                
                itemblk.addField("ITEM_STR_LU").
                setHidden().
                setDbName("STR_LU").
                setSize(25);
                
                itemblk.addField("ITEM_KEY_NO1").
                setDbName("KEY_NO1").
                setSize(50).
                setMaxLength(40).
                setHidden();

                itemblk.addField("ITEM_KEY_NO2").
                setDbName("KEY_NO2").
                setSize(20).
                setMaxLength(6).
                setHidden();

                itemblk.addField("ITEM_KEY_NO3").
                setDbName("KEY_NO3").
                setSize(20).
                setMaxLength(6).
                setHidden();

                itemblk.addField("PREDECESSOR").
                setInsertable().
                setCheckBox("FALSE,TRUE");
                
                itemblk.addField("PARENT_ROW_NO","Number").
                setLabel("PCMWCONNECTPREDECESSORSDLGCBPRED: Predecessor").
                setReadOnly();
                 
                itemblk.addField("PARENT_ROW_DESC").
                setLabel("PCMWCONNECTPREDECESSORSDLGPARENTROWDESC: Description").
                setSize(60).
                setReadOnly();
                
                itemblk.addField("PRED_ROW_ID").
                setSize(60).
                setHidden();
                
                itemblk.addField("PREDLIST").
                setFunction("''").
                setHidden();

                
                itemblk.setView("PREDECESSOR_UTIL");
                itemblk.defineCommand("PREDECESSOR_UTIL_API","Modify__");
                itemset = itemblk.getASPRowSet();

                itemblk.setMasterBlock(headblk);
                itembar = mgr.newASPCommandBar(itemblk);
                itembar.disableCommand(itembar.EDITROW);
                
                itemtbl = mgr.newASPTable(itemblk);
                itemtbl.setEditable();
                itemtbl.setWrap();
                itemtbl.disableQuickEdit();
                
                itemlay = itemblk.getASPBlockLayout();
                itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
                itemlay.setDialogColumns(2);  
                
	}

       
//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCONPREDECESSORSDLGTITLE: Predecessors";
	}

	protected String getTitle()
	{
		return "PCMWCONPREDECESSORSDLGTITLE: Predecessors";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
                printHiddenField("ROWVAL","");
                printHiddenField("REFRESHCHILD","FALSE");
                appendToHTML(headbar.showBar());
                appendToHTML("<table id=\"TBL\" border=0 class=\"BlockLayoutTable\" cellspacing=4 cellpadding=1 width= 100%>\n");
                appendToHTML("       <tr>\n");
                appendToHTML("           <td \n");
                appendToHTML("	           <table id=\"TBL1\" border=0 cellspacing=10 cellpadding=1 >\n");
                appendToHTML("	           <tr>\n");
                appendToHTML("              <td nowrap align=\"left\" >");
                appendToHTML(fmt.drawWriteLabel("PCMWCONNECTPREDECESSORSDLGOPERNO: Operation No:"));
                appendToHTML("	           </td>\n");
                appendToHTML("              <td nowrap align=\"left\" >");
                appendToHTML(fmt.drawWriteLabel("PCMWCONNECTPREDECESSORSDLGDESC: Description :"));
                appendToHTML("	           </td>\n");
                appendToHTML("	           </tr>\n");
                appendToHTML("	           <tr>\n");
                appendToHTML("              <td nowrap height=\"10\" align=\"left\" >");
                appendToHTML(fmt.drawSelect("ROW_NO",rowBuff,rowNo,"onChange='populateChild()'"));
                appendToHTML("</td>\n");
                appendToHTML("      <td nowrap align=\"left\" >");
                appendToHTML(fmt.drawTextField("DESCRIPTION",desc,"readOnly",30));
                appendToHTML("        </td>\n");
                appendToHTML("        </tr>\n");
                appendToHTML("        </table>\n");
                appendToHTML("   </td>\n");
                appendToHTML("</tr>\n");
                appendToHTML("</table>\n");
                
                if (headlay.isSingleLayout() && (headset.countRows() > 0))
                   appendToHTML(itemlay.show());
                                
                appendToHTML("<table id=\"SND\" border=\"0\">\n");
		appendToHTML("<tr>\n");
		appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawSubmit("APPLY",mgr.translate("PCMWCONPREDECESSORSDLGAPPLY: Apply"),"APPLY"));
		appendToHTML("</td>\n");
                appendToHTML("<br><br>\n");
                appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawSubmit("SAVECLOSE",mgr.translate("PCMWCONPREDECESSORSDLGSAVECLOSE: Save and Close"),"SAVECLOSE"));
		appendToHTML("</td>\n");
                appendToHTML("<br><br>\n");
                appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawSubmit("CLOSE",mgr.translate("PCMWCONPREDECESSORSDLGCLOSE: Close"),"CLOSE"));
		appendToHTML("</td>\n");
                appendToHTML("</tr>\n");
		appendToHTML("</table>\n");

                if (!mgr.isEmpty(block))
                {
                   // XSS_Safe ILSOLK 20070709
                   appendDirtyJavaScript("if (");
                   appendDirtyJavaScript(saveCloseFlag);
                   appendDirtyJavaScript(")\n");
                   appendDirtyJavaScript("{\n");
                   appendDirtyJavaScript("opener.setPredValues('"+mgr.encodeStringForJavascript(sPredicessorList)+"','"+mgr.encodeStringForJavascript(block)+"');\n");
                   appendDirtyJavaScript("window.close();\n");
                   appendDirtyJavaScript("}\n");
                }
                else
                {
                   appendDirtyJavaScript("if (");
                   appendDirtyJavaScript(saveCloseFlag);
                   appendDirtyJavaScript(")\n");
                   appendDirtyJavaScript("{\n");
                   appendDirtyJavaScript("if (");
                   appendDirtyJavaScript(!mgr.isEmpty(qryStr));
                   appendDirtyJavaScript(")\n");
                   appendDirtyJavaScript("{\n");
                   appendDirtyJavaScript("  window.open('");
                   appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr)); // XSS_Safe ILSOLK 20070709
                   appendDirtyJavaScript("' + \"&PREDECESSORS=1\",'");
                   appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName)); // XSS_Safe ILSOLK 20070709
                   appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                   appendDirtyJavaScript("	self.close();\n");  
                   appendDirtyJavaScript("}\n");
                   appendDirtyJavaScript("else\n");
                   appendDirtyJavaScript("{\n");
                   appendDirtyJavaScript("  window.open('");
                   appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url)); // XSS_Safe ILSOLK 20070709
                   appendDirtyJavaScript("','");
                   appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName)); // XSS_Safe ILSOLK 20070709
                   appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                   appendDirtyJavaScript("	self.close();\n");  
                   appendDirtyJavaScript("}\n");
                   appendDirtyJavaScript("}\n");

                }

                appendDirtyJavaScript("function populateChild() {\n");
                appendDirtyJavaScript(" n = f.ROW_NO.options[f.ROW_NO.selectedIndex].value \n");
                appendDirtyJavaScript(" if (n=='') \n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("      f.APPLY.disabled = true;\n");
                appendDirtyJavaScript("      f.SAVECLOSE.disabled = true;\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("  else \n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("   document.form.ROWVAL.value = n;\n");
                appendDirtyJavaScript("   document.form.REFRESHCHILD.value = \"TRUE\";\n");
                appendDirtyJavaScript("   submit();\n");  
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}\n");
                
        }
}
