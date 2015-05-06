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
*  File        : StdJobQualificationsDlg.java 
*              : 050531 THWILK Created.
*  Modified    :
*  THWILK  050614  Modified javascript method lovCompetencyElementName.	
*  THWILK  050617  Modified printContents().	
*  THWILK  050622  Modified run(),printContents() and removed method back().	
*  THWILK  050707  Modified methods run() and printContents(). 
*  THWILK  050808  Modified methods predefine() and validate(). 
*  THWILK  060126  Corrected localization errors.
*  AMDILK  060811  Bug Id 58216, Eliminated SQL errors in web applications. Modified methods 
*                  countFindITEM0(), countFindITEM1(), okFindITEM0(), okFindITEM1()
*  AMDILK  060904  Merged with the Bug Id 58216
*  AMNILK  070727  Eliminated SQLInjections Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.math.*;
import java.util.*;

public class StdJobQualificationsDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.StdJobQualificationsDlg");

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
        
        private ASPBlock itemblk0;
        private ASPRowSet itemset0;
        private ASPCommandBar itembar0;
        private ASPTable itemtbl0;
        private ASPBlockLayout itemlay0;

        private ASPBlock itemblk1;
        private ASPRowSet itemset1;
        private ASPCommandBar itembar1;
        private ASPTable itemtbl1;
        private ASPBlockLayout itemlay1;

        private ASPTabContainer tabs;
        private ASPBuffer buff;
        private ASPHTMLFormatter fmt;


	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
        private String val;
	private ASPCommand cmd;
        private ASPBuffer data;
	private ASPQuery q;
	private String n;
        private String sStdJobNo;
        private String sStdJobRevision;
        private String sStdJobContract;
        private String frameName;
        private String qryStr;
        private String calling_url;
        
	//===============================================================
	// Construction 
	//===============================================================
	public StdJobQualificationsDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();
                fmt = mgr.newASPHTMLFormatter();
                sStdJobNo       = ctx.readValue("STD_JOB_ID", "");
                sStdJobRevision = ctx.readValue("STD_JOB_REVISION", "");
                sStdJobContract = ctx.readValue("STD_JOB_CONTRACT", "");
                frameName       = ctx.readValue("FRAMENAME","");
		qryStr          = ctx.readValue("QRYSTR","");
                calling_url     = ctx.readValue("CALLING_URL","");

                if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
                else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
                        validate();
                else if (!mgr.isEmpty(mgr.getQueryStringValue("RES_TYPE"))) {
                        sStdJobNo       = mgr.getQueryStringValue("STD_JOB_ID");
                        sStdJobRevision = mgr.getQueryStringValue("STD_JOB_REVISION");
                        sStdJobContract = mgr.getQueryStringValue("STD_JOB_CONTRACT");
                        frameName       = mgr.readValue("FRMNAME","");
                        qryStr          = mgr.readValue("QRYSTR","");
                        calling_url     = ctx.getGlobal("CALLING_URL");
                        calling_url     = calling_url+"?&STD_JOB_ID="+sStdJobNo+"&STD_JOB_CONTRACT="+sStdJobContract+"&STD_JOB_REVISION="+sStdJobRevision;
                        okFind();
                }
                else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (mgr.dataTransfered())
			okFind();
                
                tabs.saveActiveTab();
                adjust();
                ctx.writeValue("STD_JOB_ID", sStdJobNo);
                ctx.writeValue("STD_JOB_REVISION", sStdJobRevision);
                ctx.writeValue("STD_JOB_CONTRACT", sStdJobContract);
                ctx.writeValue("FRAMENAME",frameName);
		ctx.writeValue("QRYSTR",qryStr);
                ctx.writeValue("CALLING_URL",calling_url);
       
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

        public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");
                String txt;
                String strCompGrpName;
                String strCompGrpDesc;
                String strCompEleName;
                String strLicenseName;
                String strCompeLevelName;

                

              if ("GROUP_STRUCTURE".equals(val))
                {
                      cmd = trans.addCustomFunction("COMPGRPNAME", "Competency_Group_API.Get_Group_Name", "COMPETENCY_GROUP_NAME" );    
                      cmd.addParameter("GROUP_STRUCTURE",mgr.readValue("GROUP_STRUCTURE"));

                      cmd = trans.addCustomFunction("COMPGRPDESC", "QUALIFICATIONS_UTIL_API.Get_Group_Description", "GROUP_DESC" );    
                      cmd.addReference("GROUP_STRUCTURE","COMPGRPNAME/DATA");
                      
                      
                      trans = mgr.validate(trans);
                      strCompEleName="";
                      strCompeLevelName="";
                      strCompGrpDesc="";
                      
                      strCompGrpName = trans.getValue("COMPGRPNAME/DATA/COMPETENCY_GROUP_NAME");
                      strCompGrpDesc = trans.getValue("COMPGRPDESC/DATA/GROUP_DESC");
                      txt =  (mgr.isEmpty(strCompGrpName) ? "" : (strCompGrpName)) + "^" +
                             (mgr.isEmpty(strCompGrpDesc) ? "" : (strCompGrpDesc)) + "^";
                      mgr.responseWrite(txt);
                      
               }
               
               else if ("COMPETENCY_ELEMENT_NAME".equals(val))
                {    
                      cmd = trans.addCustomFunction("COMPELEDESC", "COMPETENCY_ELEMENT_API.Get_Description", "COMPETENCY_ELEMENT_DESC" );    
                      cmd.addParameter("COMPETENCY_GROUP_NAME",mgr.readValue("COMPETENCY_GROUP_NAME"));
                      cmd.addParameter("COMPETENCY_ELEMENT_NAME",mgr.readValue("COMPETENCY_ELEMENT_NAME"));
                      
                      trans = mgr.validate(trans);
                      
                      strCompEleName = trans.getValue("COMPELEDESC/DATA/COMPETENCY_ELEMENT_DESC");
                      txt =  (mgr.isEmpty(strCompEleName) ? "" : (strCompEleName)) + "^";
                      mgr.responseWrite(txt);
                      
               }

               else if ("LICENSE_NAME".equals(val))
                {    
                      cmd = trans.addCustomFunction("LICENSEDESC", "LICENSE_TYPE_API.Get_Description", "LICENSE_DESC" );    
                      cmd.addParameter("LICENSE_NAME",mgr.readValue("LICENSE_NAME"));
                      
                      trans = mgr.validate(trans);
                      
                      strLicenseName = trans.getValue("LICENSEDESC/DATA/LICENSE_DESC");
                      txt =  (mgr.isEmpty(strLicenseName) ? "" : (strLicenseName)) + "^";
                      mgr.responseWrite(txt);
                      
               }


              mgr.endResponse();

	}  

//-----------------------------------------------------------------------------
//---------------------------  CMDBAR FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

       public void countFindITEM0()
        {
                ASPManager mgr = getASPManager();

                q = trans.addQuery(itemblk0);
                q.setSelectList("to_char(count(*)) N");
                q.addWhereCondition("STD_JOB_ID = ? AND STD_JOB_REVISION = ? AND STD_JOB_CONTRACT = ? AND ROW_NO= ?");
                q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
		q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
		q.addParameter("STD_JOB_CONTRACT", headset.getRow().getValue("STD_JOB_CONTRACT"));
		q.addParameter("ROW_NO", headset.getRow().getValue("ROW_NO"));
		int headrowno = headset.getCurrentRowNo();
                mgr.submit(trans);
                itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
                itemset0.clear();
                headset.goTo(headrowno);
        }     

        public void countFindITEM1()
        {
                ASPManager mgr = getASPManager();

                q = trans.addQuery(itemblk1);
                q.setSelectList("to_char(count(*)) N");
                q.addWhereCondition("STD_JOB_ID = ? AND STD_JOB_REVISION = ? AND STD_JOB_CONTRACT = ? AND ROW_NO= ?");
                q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
		q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
		q.addParameter("STD_JOB_CONTRACT", headset.getRow().getValue("STD_JOB_CONTRACT"));
		q.addParameter("ROW_NO", headset.getRow().getValue("ROW_NO"));
		int headrowno = headset.getCurrentRowNo();
                mgr.submit(trans);
                itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
                itemset1.clear();
                headset.goTo(headrowno);
        }     


	public void okFind()
	{
		ASPManager mgr = getASPManager();
                trans.clear();
		q = trans.addQuery(headblk);
                q.includeMeta("ALL");
                mgr.submit(trans);
                
                eval(headset.syncItemSets());
                if (headset.countRows() ==1)
                {
                    if (tabs.getActiveTab() == 1)
                       okFindITEM0();
                    else if (tabs.getActiveTab() == 2)
                       okFindITEM1();
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                }
                else if (headset.countRows() == 0)
                    mgr.showAlert(mgr.translate("PCMWSTDJOBQUALIFICATIONSNODATA: No data found."));
		
	         mgr.createSearchURL(headblk); 
	}

        public void okFindITEM0()
        {

              ASPManager mgr = getASPManager();

              if (headset.countRows()>0)
              {
                 int currrow = headset.getCurrentRowNo();  

                 trans.clear();
                 q = trans.addQuery(itemblk0);
                 q.addWhereCondition("STD_JOB_ID = ? AND STD_JOB_REVISION = ? AND STD_JOB_CONTRACT = ? AND ROW_NO= ?");
                 q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
		 q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
		 q.addParameter("STD_JOB_CONTRACT", headset.getRow().getValue("STD_JOB_CONTRACT"));
		 q.addParameter("ROW_NO", headset.getRow().getValue("ROW_NO"));
		 q.includeMeta("ALL");
                 mgr.submit(trans);
                 headset.goTo(currrow);
              }
        } 

        public void okFindITEM1()
        {

              ASPManager mgr = getASPManager();

              if (headset.countRows()>0)
              {
                 int currrow = headset.getCurrentRowNo();  

                 trans.clear();
                 q = trans.addQuery(itemblk1);
                 q.addWhereCondition("STD_JOB_ID = ? AND STD_JOB_REVISION = ? AND STD_JOB_CONTRACT = ? AND ROW_NO= ?");
                 q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
		 q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
		 q.addParameter("STD_JOB_CONTRACT", headset.getRow().getValue("STD_JOB_CONTRACT"));
		 q.addParameter("ROW_NO", headset.getRow().getValue("ROW_NO"));
		 q.includeMeta("ALL");
                 mgr.submit(trans);
                 headset.goTo(currrow);
              }
        } 

        
        public void newRowITEM0()
	{
		ASPCommand cmd; 
		ASPBuffer data;
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addEmptyCommand("ITEM0","STD_JOB_COMPETENCY_API.New__",itemblk0);
                cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM0/DATA");
                data.setFieldItem("ITEM0_STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID")); 
                data.setFieldItem("ITEM0_STD_JOB_REVISION",headset.getRow().getValue("STD_JOB_REVISION")); 
                data.setFieldItem("ITEM0_STD_JOB_CONTRACT",headset.getRow().getValue("STD_JOB_CONTRACT")); 
                data.setFieldItem("ITEM0_ROW_NO",headset.getRow().getValue("ROW_NO")); 
                itemset0.addRow(data);
	} 

       

        public void newRowITEM1()
	{
		ASPCommand cmd; 
		ASPBuffer data;
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addEmptyCommand("ITEM1","STD_JOB_LICENSE_API.New__",itemblk1);
                cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM1/DATA");
                data.setFieldItem("ITEM1_STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID")); 
                data.setFieldItem("ITEM1_STD_JOB_REVISION",headset.getRow().getValue("STD_JOB_REVISION")); 
                data.setFieldItem("ITEM1_STD_JOB_CONTRACT",headset.getRow().getValue("STD_JOB_CONTRACT")); 
                data.setFieldItem("ITEM1_ROW_NO",headset.getRow().getValue("ROW_NO")); 
                itemset1.addRow(data);
	}  


        public void duplicateRowITEM0()
         {
              ASPManager mgr = getASPManager();

              if (itemlay0.isMultirowLayout())
                 itemset0.goTo(itemset0.getRowSelected());
              else
                 itemset0.selectRow();

              itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);

              trans.clear();

              cmd = trans.addEmptyCommand("ITEM0","STD_JOB_COMPETENCY_API.New__",itemblk0);
              cmd.setOption("ACTION","PREPARE");
              trans = mgr.perform(trans);
              data = trans.getBuffer("ITEM0/DATA");
              data.setFieldItem("ITEM0_STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID")); 
              data.setFieldItem("ITEM0_STD_JOB_REVISION",headset.getRow().getValue("STD_JOB_REVISION")); 
              data.setFieldItem("ITEM0_STD_JOB_CONTRACT",headset.getRow().getValue("STD_JOB_CONTRACT")); 
              data.setFieldItem("ITEM0_ROW_NO",headset.getRow().getValue("ROW_NO")); 
              data.setFieldItem("GROUP_STRUCTURE",itemset0.getRow().getValue("GROUP_STRUCTURE")); 
              data.setFieldItem("COMPETENCY_ELEMENT_NAME",itemset0.getRow().getValue("COMPETENCY_ELEMENT_NAME")); 
              data.setFieldItem("COMPETENCY_LEVEL_NAME",itemset0.getRow().getValue("COMPETENCY_LEVEL_NAME")); 
              data.setFieldItem("STD_JOB_COMPETENCY_SEQ_NO",trans.getValue("ITEM0/DATA/STD_JOB_COMPETENCY_SEQ_NO"));
              itemset0.addRow(data);
         }

        public void duplicateRowITEM1()
         {
              ASPManager mgr = getASPManager();

              if (itemlay1.isMultirowLayout())
                 itemset1.goTo(itemset1.getRowSelected());
              else
                 itemset1.selectRow();

              itemlay1.setLayoutMode(itemlay1.NEW_LAYOUT);

              trans.clear();

              cmd = trans.addEmptyCommand("ITEM1","STD_JOB_LICENSE_API.New__",itemblk1);
              cmd.setOption("ACTION","PREPARE");
              trans = mgr.perform(trans);
              data = trans.getBuffer("ITEM1/DATA");
              data.setFieldItem("ITEM1_STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID")); 
              data.setFieldItem("ITEM1_STD_JOB_REVISION",headset.getRow().getValue("STD_JOB_REVISION")); 
              data.setFieldItem("ITEM1_STD_JOB_CONTRACT",headset.getRow().getValue("STD_JOB_CONTRACT")); 
              data.setFieldItem("ITEM1_ROW_NO",headset.getRow().getValue("ROW_NO")); 
              data.setFieldItem("LICENSE_NAME",itemset1.getRow().getValue("LICENSE_NAME")); 
              data.setFieldItem("STD_JOB_LICENSE_SEQ_NO",trans.getValue("ITEM1/DATA/STD_JOB_LICENSE_SEQ_NO"));
              itemset1.addRow(data);
         }


       
       public void activateCompetencies()
       {
           tabs.setActiveTab(1);
           okFindITEM0();
       }

       public void activateLicenses()
       {
           tabs.setActiveTab(2);
           okFindITEM1();
       }

      public void adjust()
       {
        ASPManager mgr = getASPManager();

        headbar.removeCustomCommand("activateCompetencies");
        headbar.removeCustomCommand("activateLicenses");

       }  
//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

        
	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

                headblk.addField("STD_JOB_ID").
		setSize(15).
		setMandatory().
		setReadOnly().
                setUpperCase().
                setLabel("PCMWSTDJOBQUALIFICATIONSSTDJOBID: Standard Job ID");
		
                headblk.addField("STD_JOB_REVISION").
                setMandatory().
                setUpperCase().
                setHidden();
		
                headblk.addField("STD_JOB_CONTRACT").
                setMandatory().
                setUpperCase().
                setHidden();
                
                headblk.addField("ROW_NO","Number").
                setMandatory().
                setLabel("PCMWSTDJOBQUALIFICATIONSROWNO: Operation No").
                setReadOnly();

                headblk.addField("DESCRIPTION").
                setLabel("PCMWSTDJOBQUALIFICATIONSDESC: Description").
                setReadOnly();
                 
                              
                headblk.addField("ROLE_CODE").
                setLabel("PCMWSTDJOBQUALIFICATIONSRCODE: Craft ID").
                setUpperCase().
                setReadOnly();
                
                headblk.addField("HR_JOB_ID").
                setLabel("PCMWSTDJOBQUALIFICATIONSHRJOBID: HR Job ID").
                setReadOnly().
                setUpperCase().
                setFunction("Role_To_Site_API.Get_Job_Id(:ROLE_CODE,:STD_JOB_CONTRACT)");
                
                headblk.setView("STANDARD_JOB_ROLE");
		headblk.defineCommand("STANDARD_JOB_ROLE_API","");
		headblk.disableDocMan();

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
                headbar.addCustomCommand("activateCompetencies", "");
                headbar.addCustomCommand("activateLicenses", "");
                headbar.disableCommand(headbar.FIND);
                
                headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWSTDJOBQUALIFICATIONSHD: Additional Qualifications"));
		headtbl.setWrap();
                
		headlay = headblk.getASPBlockLayout();
                headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
                headlay.setDialogColumns(2);  

                tabs = mgr.newASPTabContainer();
                tabs.addTab(mgr.translate("PCMWSTDJOBQUALIFICATIONSCOMPETENCYTAB: Competency"), "javascript:commandSet('HEAD.activateCompetencies','')");
                tabs.addTab(mgr.translate("PCMWSTDJOBQUALIFICATIONSLICENCETAB: License"), "javascript:commandSet('HEAD.activateLicenses','')");
                

		//------------------------------ ITEM0 BLOCK BEGIN ---------------------------------------------------
                //------------------competencies--------------------------------------------------------------------
		
                itemblk0 = mgr.newASPBlock("ITEM0");
                
                itemblk0.addField("ITEM0_OBJID").
                setDbName("OBJID").
                setHidden();

                itemblk0.addField("ITEM0_OBJVERSION").
                setDbName("OBJVERSION").
                setHidden();
                
                itemblk0.addField("STD_JOB_COMPETENCY_SEQ_NO", "Number").
                setMandatory().
                setInsertable().
                setHidden();
                
                itemblk0.addField("ITEM0_STD_JOB_ID").
		setSize(15).
		setMandatory().
		setReadOnly().
                setDbName("STD_JOB_ID").
                setUpperCase().
                setHidden();
		
                itemblk0.addField("ITEM0_STD_JOB_REVISION").
                setDbName("STD_JOB_REVISION").
                setMandatory().
		setReadOnly().
                setUpperCase().
                setHidden();
		
                itemblk0.addField("ITEM0_STD_JOB_CONTRACT").
                setDbName("STD_JOB_CONTRACT").
                setUpperCase().
                setMandatory().
                setHidden();

                itemblk0.addField("ITEM0_ROW_NO","Number","#").
                setMandatory().
                setDbName("ROW_NO").
                setHidden();

                itemblk0.addField("GROUP_STRUCTURE").
                setLabel("PCMWSTDJOBQUALIFICATIONSGROUPSTRUCNAME: Group Name").
                setInsertable().
                setMandatory().
                setSize(30).
                setDynamicLOV("COMPETENCY_GROUP_LOV1",600,450).
                setCustomValidation("GROUP_STRUCTURE","COMPETENCY_GROUP_NAME,GROUP_DESC").
                setMaxLength(250);

                itemblk0.addField("GROUP_DESC").
		setSize(40).
                setMaxLength(2000).
                setLabel("PCMWSTDJOBQUALIFICATIONSGROUPDESC: Group Description").
                setFunction("QUALIFICATIONS_UTIL_API.Get_Group_Description(:COMPETENCY_GROUP_NAME)").
                setQueryable().
                setReadOnly();


                itemblk0.addField("COMPETENCY_GROUP_NAME").
                setInsertable().
                setHidden();
                
                itemblk0.addField("COMPETENCY_ELEMENT_NAME").
                setMandatory().
                setDynamicLOV("COMPETENCY_ELEMENT_LOV1",600,450).
                setCustomValidation("COMPETENCY_GROUP_NAME,COMPETENCY_ELEMENT_NAME","COMPETENCY_ELEMENT_DESC").
                setSize(30).
                setMaxLength(200).
                setInsertable().
		setLabel("PCMWSTDJOBQUALIFICATIONSELEMENTNAME: Element Name");
                

                itemblk0.addField("COMPETENCY_ELEMENT_DESC").
		setSize(40).
                setMaxLength(2000).
                setLabel("PCMWSTDJOBQUALIFICATIONSELEMENTDESC: Element Description").
                setFunction("COMPETENCY_ELEMENT_API.Get_Description(:COMPETENCY_GROUP_NAME,:COMPETENCY_ELEMENT_NAME)").
                setReadOnly();
                
                itemblk0.addField("COMPETENCY_LEVEL_NAME").
                setSize(30).
                setDynamicLOV("COMPETENCY_SCALE_LOV","GROUP_STRUCTURE",600,450).
                setInsertable().
                setMaxLength(40).
                setLabel("PCMWSTDJOBQUALIFICATIONSCOMPLEVELNAME: Level");
                
                
                itemblk0.setView("STD_JOB_COMPETENCY1");
		itemblk0.defineCommand("STD_JOB_COMPETENCY_API","New__,Modify__,Remove__");
		itemset0 = itemblk0.getASPRowSet();
		itemblk0.setMasterBlock(headblk);

		itembar0 = mgr.newASPCommandBar(itemblk0);
                itembar0.enableMultirowAction();
                
                itembar0.enableCommand(itembar0.FIND);
		itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
		itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
		itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
                itembar0.defineCommand(itembar0.DUPLICATEROW,"duplicateRowITEM0");
		itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
		itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");
                
		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.setTitle(mgr.translate("PVMWCOMPETENCYITEM0: Competency"));
		itemtbl0.setWrap();

		itemlay0 = itemblk0.getASPBlockLayout();
		itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
		itemlay0.setDialogColumns(2);

       //------------------------------ ITEM1 BLOCK BEGIN ---------------------------------------------------
       //------------------License--------------------------------------------------------------------

                itemblk1 = mgr.newASPBlock("ITEM1");

                itemblk1.addField("ITEM1_OBJID").
                setDbName("OBJID").
                setHidden();

                itemblk1.addField("ITEM1_OBJVERSION").
                setDbName("OBJVERSION").
                setHidden();

                itemblk1.addField("STD_JOB_LICENSE_SEQ_NO", "Number").
                setMandatory().
                setInsertable().
                setHidden();
                
                itemblk1.addField("ITEM1_STD_JOB_ID").
		setSize(15).
		setMandatory().
		setReadOnly().
                setDbName("STD_JOB_ID").
                setUpperCase().
                setHidden();
		
                itemblk1.addField("ITEM1_STD_JOB_REVISION").
                setDbName("STD_JOB_REVISION").
                setMandatory().
		setReadOnly().
                setUpperCase().
                setHidden();
		
                itemblk1.addField("ITEM1_STD_JOB_CONTRACT").
                setDbName("STD_JOB_CONTRACT").
                setUpperCase().
                setMandatory().
                setHidden();

                itemblk1.addField("ITEM1_ROW_NO","Number","#").
                setDbName("ROW_NO").
                setHidden();

                itemblk1.addField("LICENSE_NAME").
                setLabel("PCMWSTDJOBQUALIFICATIONLICENSENAME: License Name").
                setInsertable().
                setCustomValidation("LICENSE_NAME","LICENSE_DESC").
                setMandatory().
                setDynamicLOV("LICENSE_TYPE",600,450).
                setMaxLength(200);
                
                itemblk1.addField("LICENSE_DESC").
                setMaxLength(2000).
                setLabel("PCMWSTDJOBQUALIFICATIONSLKICENSEDESC: License Description").
                setFunction("LICENSE_TYPE_API.Get_Description(:LICENSE_NAME)").
                setUpperCase().
                setReadOnly();

                itemblk1.setView("STD_JOB_LICENSE1");
                itemblk1.defineCommand("STD_JOB_LICENSE_API","New__,Modify__,Remove__");
                itemset1 = itemblk1.getASPRowSet();
                itemblk1.setMasterBlock(headblk);

                itembar1 = mgr.newASPCommandBar(itemblk1);
                itembar1.enableMultirowAction();
                itembar1.enableCommand(itembar1.FIND);
                itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
                itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
                itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
                itembar1.defineCommand(itembar1.DUPLICATEROW,"duplicateRowITEM1");
                itembar1.defineCommand(itembar1.SAVERETURN,null,"checkItem1Fields(-1)");
                itembar1.defineCommand(itembar1.SAVENEW,null,"checkItem1Fields(-1)");
                
                itemtbl1 = mgr.newASPTable(itemblk1);
                itemtbl1.setTitle(mgr.translate("PVMWCOMPETENCYITEM1: License"));
                itemtbl1.setWrap();

                itemlay1 = itemblk1.getASPBlockLayout();
                itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
                itemlay1.setDialogColumns(2);
                
                
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWSTDJOBQUALIFICATIONS: Additional Qualifications";
	}

	protected String getTitle()
	{
		return "PCMWSTDJOBQUALIFICATIONS: Additional Qualifications";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());
                if (headlay.isSingleLayout() && headset.countRows() > 0)
                   appendToHTML(tabs.showTabsInit());

                if (headlay.isSingleLayout() && (headset.countRows() > 0))
                {
                
                    if (tabs.getActiveTab() == 1)
                      appendToHTML(itemlay0.show());
                    else if (tabs.getActiveTab() == 2)
                    appendToHTML(itemlay1.show());
                }
                
                appendToHTML("<table id=\"SND\" border=\"0\">\n");
		appendToHTML("<tr>\n");
		appendToHTML("<td><br>\n");
		appendToHTML(fmt.drawButton("CLOSE",mgr.translate("PCMWPMQUALIFICATIONSDLGCLOSE: Close"),"onClick='executeOk();'"));
		appendToHTML("</td>\n");
                appendToHTML("</tr>\n");
		appendToHTML("</table>\n");

                appendDirtyJavaScript("function executeOk() {\n");
                appendDirtyJavaScript("  if (");
                appendDirtyJavaScript(!mgr.isEmpty(qryStr));
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.open('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' + \"&STDQUALIFICATION=1\",'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                appendDirtyJavaScript("	self.close();\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("else\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.open('");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url));	//XSS_Safe AMNILK 20070726
                appendDirtyJavaScript("','");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName));	//XSS_Safe AMNILK 20070726
                appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                appendDirtyJavaScript("	self.close();\n");  
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function lovCompetencyElementName(i,params)");
                appendDirtyJavaScript("{");
	        appendDirtyJavaScript("if(params) param = params;\n");
                appendDirtyJavaScript("else param = '';\n");
                appendDirtyJavaScript("var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
                appendDirtyJavaScript("var key_value = (getValue_('COMPETENCY_ELEMENT_NAME',i).indexOf('%') !=-1)? getValue_('COMPETENCY_ELEMENT_NAME',i):'';\n");
                appendDirtyJavaScript(" if(document.form.GROUP_STRUCTURE.value != '')\n");
                appendDirtyJavaScript("{\n");
	        appendDirtyJavaScript("		whereCond1 = \"COMPETENCY_GROUP_NAME = '\" + URLClientEncode(document.form.COMPETENCY_GROUP_NAME.value)  +\"' AND GROUP_STRUCTURE = '\" +URLClientEncode(document.form.GROUP_STRUCTURE.value)+\"' \";\n"); 
                appendDirtyJavaScript("openLOVWindow('COMPETENCY_ELEMENT_NAME',i,\n");
                appendDirtyJavaScript("'");
                appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=COMPETENCY_ELEMENT_LOV1&__FIELD=Competency+Element+Name&__INIT=1&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('COMPETENCY_ELEMENT_NAME',i))");
                appendDirtyJavaScript(",600,450,'validateCompetencyElementName');\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("else\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("openLOVWindow('COMPETENCY_ELEMENT_NAME',i,\n");
                appendDirtyJavaScript("'");
                appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=COMPETENCY_ELEMENT_LOV1&__FIELD=Competency+Element+Name&__INIT=1&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('COMPETENCY_ELEMENT_NAME',i))");
                appendDirtyJavaScript("+ '&COMPETENCY_ELEMENT_NAME=' + URLClientEncode(key_value)");
                appendDirtyJavaScript("+ '&COMPETENCY_GROUP_NAME=' + URLClientEncode(getValue_('COMPETENCY_GROUP_NAME',i))");
                appendDirtyJavaScript(",600,450,'validateCompetencyElementName');\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}\n");
                
        }  
}
