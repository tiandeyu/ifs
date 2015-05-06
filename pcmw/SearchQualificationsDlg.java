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
*  File        : SearchQualificationsDlg.java 
*              : 050729 THWILK Created.
*  Modified    :
*  050809  THWILK  Modified method run().
*  050825  THWILK  Modified to incorporate required functionality under AMEC114:Search for & Allocate Employees spec.
*  050923  THWILK  Modified run().
*  060126  THWILK  Corrected localization errors.
*  060814  AMDILK  Bug Id 58216, Eliminated SQL errors in web applications. Modified methods countFindITEM0(), 
*                  countFindITEM1(), okFindITEM0(), okFindITEM1()
*  060904  AMDILK  Merged with the Bug Id 58216
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.math.*;
import java.util.*;

public class SearchQualificationsDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.SearchQualificationsDlg");

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
        private ASPBuffer getAllValues;
        private ASPBuffer headBuff;
	private ASPQuery q;
	private String n;
        String sWoNo;
        private boolean bDisable;
        private String rowNo;
        private String calling_url;
        private String sSimpleSearchSeqNo;
        private String sQuerySeqNo;
        private ASPBuffer rowBuff;
        private String sResType;

	//===============================================================
	// Construction 
	//===============================================================
	public SearchQualificationsDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
                fmt = mgr.newASPHTMLFormatter();
		ctx   = mgr.getASPContext();
                sSimpleSearchSeqNo  = ctx.readValue("SIMPLE_SEARCH_SEQ", "");
                sQuerySeqNo         = ctx.readValue("QUERY_SEQ", "");
                rowNo               = ctx.readValue("ROW_NO","");
                sResType            = ctx.readValue("RES_TYPE","");
                rowBuff             = ctx.readBuffer("ROWBUFF");
                headBuff            = ctx.readBuffer("HEADDATA");
                getAllValues        = ctx.readBuffer("ALLDATA");
                calling_url         = ctx.readValue("CALLING_URL","");
                bDisable            = ctx.readFlag("DISABLE",false);
                
                if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
                else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
                else if (mgr.buttonPressed("BACK"))
                    back();
                else if ("TRUE".equals(mgr.readValue("REFRESHCHILD")))
                    refreshChild();
                else if (mgr.dataTransfered())
                {
                    getAllValues = mgr.getTransferedData();
                    
                    calling_url  = ctx.getGlobal("CALLING_URL");
                    headBuff     = getAllValues.getBufferAt(0);
                    sSimpleSearchSeqNo = mgr.getQueryStringValue("SIMPLE_SEARCH_SEQ");
                    sResType = mgr.getQueryStringValue("RES_TYPE");
                    if ("HEADQUAL".equals(mgr.getQueryStringValue("RES_TYPE"))) 
                    {
                        //originated from the header.
                        bDisable = true;
                        
                    }    
                    else
                    {
                        //this should get fired only when comming from search engine
                        if (!"ALLOCATEEMP".equals(mgr.getQueryStringValue("RES_TYPE"))) 
                        {
                           rowBuff = getAllValues.getBufferAt(1);
                           ASPBuffer dataBuff =  rowBuff.getBufferAt(0);
                           rowNo       =  dataBuff.getValue("ROW_NO");
                           sQuerySeqNo = mgr.getQueryStringValue("QUERY_SEQ");
                        }
                        else 
                            bDisable = true;
                     }
                    okFindITEM0();
                    okFindITEM1();
                }

		tabs.saveActiveTab();
                adjust();
                
                ctx.writeValue("SIMPLE_SEARCH_SEQ", sSimpleSearchSeqNo);
                ctx.writeValue("QUERY_SEQ", sQuerySeqNo);
                ctx.writeValue("ROW_NO",rowNo);
                ctx.writeValue("RES_TYPE",sResType);
                ctx.writeValue("CALLING_URL",calling_url);
                ctx.writeFlag("DISABLE",bDisable);
                ctx.writeBuffer("HEADDATA",headBuff);
                ctx.writeBuffer("ALLDATA",getAllValues);

                if (!bDisable) 
                   ctx.writeBuffer("ROWBUFF",rowBuff);
          
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------
        public void back()
        {
            ASPManager mgr     = getASPManager();
            ASPBuffer dataBuff =ctx.readBuffer("HEADDATA");
            if ((bDisable) && ("HEADQUAL".equals(sResType)))
                calling_url = calling_url+"?&CALLING_FROM=HEAD";
            
           if ("ALLOCATEEMP".equals(sResType))
            {
               getAllValues =ctx.readBuffer("ALLDATA");
               mgr.transferDataTo(calling_url,getAllValues);
            }
            else
               mgr.transferDataTo(calling_url,dataBuff);
             
             
        }

        public void refreshChild()
        {
            ASPManager mgr = getASPManager();
            trans.clear();
            rowNo = mgr.readValue("ROWVAL");
            okFindITEM0();
            okFindITEM1();
        }


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
                if ((bDisable) || ("ALLOCATEEMP".equals(sResType)))
		{
		     q.addWhereCondition("SIMPLE_SEARCH_SEQ_NO= ? AND CORE_COMPETENCY='F'");
		     q.addParameter("ITEM0_SIMPLE_SEARCH_SEQ_NO", sSimpleSearchSeqNo);
		}
                else
		{
		     q.addWhereCondition("ROW_NO= ? AND QUERY_SEQ = ? AND CORE_COMPETENCY='F'");
		     q.addParameter("ITEM0_ROW_NO", rowNo);
		     q.addParameter("ITEM0_QUERY_SEQ", sQuerySeqNo);
		}
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
                if ((bDisable) || ("ALLOCATEEMP".equals(sResType)))
		{
		     q.addWhereCondition("SIMPLE_SEARCH_SEQ_NO= ? AND CORE_LICENSE='F'");
		     q.addParameter("ITEM1_SIMPLE_SEARCH_SEQ_NO", sSimpleSearchSeqNo );
		}
		else
		{
		     q.addWhereCondition("ROW_NO = ? AND QUERY_SEQ = ? AND CORE_LICENSE='F'");
		     q.addParameter("ITEM1_ROW_NO", rowNo );
		     q.addParameter("ITEM1_QUERY_SEQ", sQuerySeqNo );
		}
		int headrowno = headset.getCurrentRowNo();
                mgr.submit(trans);
                itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
                itemset1.clear();
                headset.goTo(headrowno);
        }     


	
        public void okFindITEM0()
        {

              ASPManager mgr = getASPManager();
              int currrow = headset.getCurrentRowNo();  

                 trans.clear();
                 q = trans.addQuery(itemblk0);
                 if ((bDisable) || ("ALLOCATEEMP".equals(sResType)))
		 {
		     q.addWhereCondition("SIMPLE_SEARCH_SEQ_NO= ? AND CORE_COMPETENCY='F'");
		     q.addParameter("ITEM0_SIMPLE_SEARCH_SEQ_NO", sSimpleSearchSeqNo );
		 }
		 else
		 {
		     q.addWhereCondition("ROW_NO = ? AND QUERY_SEQ = ? AND CORE_COMPETENCY='F'");
		     q.addParameter("ITEM0_ROW_NO", rowNo );
		     q.addParameter("ITEM0_QUERY_SEQ", sQuerySeqNo );
		 }
		 q.includeMeta("ALL");
                 mgr.submit(trans);
                 headset.goTo(currrow);
              
        } 

        public void okFindITEM1()
        {

              ASPManager mgr = getASPManager();
              int currrow = headset.getCurrentRowNo();  

                 trans.clear();
                 q = trans.addQuery(itemblk1);
                 if ((bDisable) || ("ALLOCATEEMP".equals(sResType)))
		 {
		     q.addWhereCondition("SIMPLE_SEARCH_SEQ_NO= ? AND CORE_LICENSE='F'");
		     q.addParameter("ITEM1_SIMPLE_SEARCH_SEQ_NO", sSimpleSearchSeqNo );
		 }
		 else
		 {
		     q.addWhereCondition("ROW_NO = ? AND QUERY_SEQ = ? AND CORE_LICENSE='F'");
		     q.addParameter("ITEM1_ROW_NO", rowNo );
		     q.addParameter("ITEM1_QUERY_SEQ", sQuerySeqNo );
		 }
		 q.includeMeta("ALL");
                 mgr.submit(trans);
                 headset.goTo(currrow);
              
        } 

        
        public void newRowITEM0()
	{
		ASPCommand cmd; 
		ASPBuffer data;
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addEmptyCommand("ITEM0","SEARCH_COMPETENCY_API.New__",itemblk0);
                cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM0/DATA");
                data.setFieldItem("ITEM0_ROW_NO",rowNo); 
                data.setFieldItem("ITEM0_QUERY_SEQ",sQuerySeqNo); 
                data.setFieldItem("ITEM0_SIMPLE_SEARCH_SEQ_NO",sSimpleSearchSeqNo); 
                itemset0.addRow(data);

	}

        
        public void newRowITEM1()
	{
		ASPCommand cmd; 
		ASPBuffer data;
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addEmptyCommand("ITEM1","SEARCH_LICENSE_API.New__",itemblk1);
                cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM1/DATA");
                data.setFieldItem("ITEM1_ROW_NO",rowNo); 
                data.setFieldItem("ITEM1_QUERY_SEQ",sQuerySeqNo); 
                data.setFieldItem("ITEM1_SIMPLE_SEARCH_SEQ_NO",sSimpleSearchSeqNo); 
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

              cmd = trans.addEmptyCommand("ITEM0","SEARCH_COMPETENCY_API.New__",itemblk0);
              cmd.setOption("ACTION","PREPARE");
              trans = mgr.perform(trans);
              data = trans.getBuffer("ITEM0/DATA");
              data.setFieldItem("ITEM0_ROW_NO",rowNo); 
              data.setFieldItem("ITEM0_QUERY_SEQ",sQuerySeqNo); 
              data.setFieldItem("ITEM0_SIMPLE_SEARCH_SEQ_NO",sSimpleSearchSeqNo);
              data.setFieldItem("GROUP_STRUCTURE",itemset0.getRow().getValue("GROUP_STRUCTURE")); 
              data.setFieldItem("COMPETENCY_ELEMENT_NAME",itemset0.getRow().getValue("COMPETENCY_ELEMENT_NAME")); 
              data.setFieldItem("COMPETENCY_LEVEL_NAME",itemset0.getRow().getValue("COMPETENCY_LEVEL_NAME")); 
              data.setFieldItem("SEARCH_COMPETENCY_SEQ_NO",trans.getValue("ITEM0/DATA/SEARCH_COMPETENCY_SEQ_NO"));
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

              cmd = trans.addEmptyCommand("ITEM1","SEARCH_LICENSE_API.New__",itemblk1);
              cmd.setOption("ACTION","PREPARE");
              trans = mgr.perform(trans);
              data = trans.getBuffer("ITEM1/DATA");
              data.setFieldItem("ITEM1_ROW_NO",rowNo); 
              data.setFieldItem("ITEM1_QUERY_SEQ",sQuerySeqNo); 
              data.setFieldItem("ITEM1_SIMPLE_SEARCH_SEQ_NO",sSimpleSearchSeqNo);
              data.setFieldItem("LICENSE_NAME",itemset1.getRow().getValue("LICENSE_NAME")); 
              data.setFieldItem("SEARCH_LICENSE_SEQ_NO",trans.getValue("ITEM1/DATA/SEARCH_LICENSE_SEQ_NO"));
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
                

                headblk.addField("ROW_NO","Number").
                setReadOnly().
                setHidden();

                headblk.addField("QUERY_SEQ","Number").
                setReadOnly().
                setHidden();
                
                headblk.setView("COMBINE_SEARCH_LINES");
		headblk.defineCommand("COMBINE_SEARCH_LINES_API","");
		headblk.disableDocMan();

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
                headbar.addCustomCommand("activateCompetencies", "");
                headbar.addCustomCommand("activateLicenses", "");
                headbar.disableCommand(headbar.FORWARD);
		headbar.disableCommand(headbar.BACKWARD);
                headbar.disableCommand(headbar.FIND);
                headbar.disableCommand(headbar.BACK);

                
                headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWAQUALIFICATIONHD: Additional Qualifications"));
		headtbl.setWrap();
                
		headlay = headblk.getASPBlockLayout();
                headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
                headlay.setDialogColumns(2);  

                tabs = mgr.newASPTabContainer();
                tabs.addTab(mgr.translate("PCMWSEARCHQUALIFICATIONSCOMPETENCYTAB: Competency"), "javascript:commandSet('HEAD.activateCompetencies','')");
                tabs.addTab(mgr.translate("PCMWSEARCHQUALIFICATIONSLICENCETAB: License"), "javascript:commandSet('HEAD.activateLicenses','')");
                

		//------------------------------ ITEM0 BLOCK BEGIN ---------------------------------------------------
                //------------------competencies--------------------------------------------------------------------
		
                itemblk0 = mgr.newASPBlock("ITEM0");
                
                itemblk0.addField("ITEM0_OBJID").
                setDbName("OBJID").
                setHidden();

                itemblk0.addField("ITEM0_OBJVERSION").
                setDbName("OBJVERSION").
                setHidden();
                
                itemblk0.addField("SEARCH_COMPETENCY_SEQ_NO", "Number").
                setMandatory().
                setInsertable().
                setQueryable().
                setHidden();

                itemblk0.addField("ITEM0_SIMPLE_SEARCH_SEQ_NO", "Number").
                setDbName("SIMPLE_SEARCH_SEQ_NO").
                setInsertable().
                setHidden();
                
                itemblk0.addField("ITEM0_QUERY_SEQ","Number").
                setDbName("QUERY_SEQ").
                setHidden();

                itemblk0.addField("ITEM0_ROW_NO","Number","#").
                setDbName("ROW_NO").
                setHidden();

                
                itemblk0.addField("GROUP_STRUCTURE").
                setLabel("PCMWSEARCHQUALIFICATIONSGROUPSTRUCNAME: Group Name").
                setMandatory().
                setSize(30).
                setInsertable().
                setDynamicLOV("COMPETENCY_GROUP_LOV1",600,450).
                setCustomValidation("GROUP_STRUCTURE","COMPETENCY_GROUP_NAME,GROUP_DESC").
                setMaxLength(250);

                itemblk0.addField("GROUP_DESC").
		setSize(40).
                setMaxLength(2000).
                setLabel("PCMWSEARCHQUALIFICATIONSGROUPSTRUCDESC: Group Description").
                setFunction("QUALIFICATIONS_UTIL_API.Get_Group_Description(:COMPETENCY_GROUP_NAME)").
                setQueryable().
                setReadOnly();

                itemblk0.addField("CORE_COMPETENCY").
                setMandatory().
                setHidden().
                setInsertable();
                
                itemblk0.addField("COMPETENCY_GROUP_NAME").
                setDynamicLOV("COMPETENCY_GROUP",600,450).
                setMandatory().
                setLabel("PCMWSEARCHQUALIFICATIONSCOMPGROUPNAME: Competency Group Name").
                setHidden();
                
                itemblk0.addField("COMPETENCY_ELEMENT_NAME").
                setMandatory().
                setDynamicLOV("COMPETENCY_ELEMENT_LOV1",600,450).
                setCustomValidation("COMPETENCY_GROUP_NAME,COMPETENCY_ELEMENT_NAME","COMPETENCY_ELEMENT_DESC").
                setSize(30).
                setMaxLength(200).
                setInsertable().
		setLabel("PCMWSEARCHQUALIFICATIONSELEMENTNAME: Element Name");
                

                itemblk0.addField("COMPETENCY_ELEMENT_DESC").
		setSize(40).
                setMaxLength(2000).
                setLabel("PCMWSEARCHQUALIFICATIONSELEMENTDESC: Element Description").
                setFunction("COMPETENCY_ELEMENT_API.Get_Description(:COMPETENCY_GROUP_NAME,:COMPETENCY_ELEMENT_NAME)").
                setReadOnly();
                
                itemblk0.addField("COMPETENCY_LEVEL_NAME").
                setSize(30).
                setMandatory().
                setInsertable().
                setDynamicLOV("COMPETENCY_SCALE_LOV","GROUP_STRUCTURE",600,450).
                setMaxLength(40).
                setLabel("PCMWSEARCHQUALIFICATIONSCOMPLEVELNAME: Level");
                
                
                itemblk0.setView("SEARCH_COMPETENCY1");
		itemblk0.defineCommand("SEARCH_COMPETENCY_API","New__,Modify__,Remove__");
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

                itemblk1.addField("SEARCH_LICENSE_SEQ_NO", "Number").
                setMandatory().
                setInsertable().
                setHidden();

                itemblk1.addField("ITEM1_SIMPLE_SEARCH_SEQ_NO", "Number").
                setDbName("SIMPLE_SEARCH_SEQ_NO").
                setInsertable().
                setHidden();

                itemblk1.addField("ITEM1_ROW_NO","Number","#").
                setDbName("ROW_NO").
                setHidden();

                itemblk1.addField("ITEM1_QUERY_SEQ","Number").
                setDbName("QUERY_SEQ").
                setHidden();
                 
                itemblk1.addField("LICENSE_NAME").
                setLabel("PCMWSEARCHQUALIFICATIONLICENSENAME: License Name").
                setCustomValidation("LICENSE_NAME","LICENSE_DESC").
                setInsertable().
                setMandatory().
                setDynamicLOV("LICENSE_TYPE",600,450).
                setMaxLength(200);
                
                itemblk1.addField("LICENSE_DESC").
                setMaxLength(2000).
                setLabel("PCMWSEARCHQUALIFICATIONSLKICENSEDESC: License Description").
                setFunction("LICENSE_TYPE_API.Get_Description(:LICENSE_NAME)").
                setUpperCase().
                setReadOnly();

                itemblk1.addField("CORE_LICENSE").
                setMandatory().
                setHidden().
                setInsertable();


                itemblk1.setView("SEARCH_LICENSE1");
                itemblk1.defineCommand("SEARCH_LICENSE_API","New__,Modify__,Remove__");
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
		return "PCMWSEARCHQUALIFICATIONS: Additional Qualifications";
	}

	protected String getTitle()
	{
		return "PCMWSEARCHQUALIFICATIONS: Additional Qualifications";
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
                appendToHTML(fmt.drawWriteLabel("PCMWSEARCHQUALIFICATIONSDLGOPER: Row No:"));
                appendToHTML("	           </td>\n");
                appendToHTML("	           </tr>\n");
                appendToHTML("	           <tr>\n");
                appendToHTML("              <td nowrap height=\"10\" align=\"left\" >");
                appendToHTML(fmt.drawSelect("ROW_NO",rowBuff,rowNo,"onChange='populateChild()'"));
                if (bDisable)
                   appendDirtyJavaScript("      f.ROW_NO.disabled = true;\n");
                
                
                appendToHTML("</td>\n");
                appendToHTML("        </tr>\n");
                appendToHTML("        </table>\n");
                appendToHTML("   </td>\n");
                appendToHTML("</tr>\n");
                appendToHTML("</table>\n");
                appendToHTML(tabs.showTabsInit());
                if (tabs.getActiveTab() == 1)
                   appendToHTML(itemlay0.show());
                else if (tabs.getActiveTab() == 2)
                   appendToHTML(itemlay1.show());
                
                
                appendToHTML("<table id=\"SND\" border=\"0\">\n");
		appendToHTML("<tr>\n");
		appendToHTML("<td><br>\n");
                appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWSEARCHQUALIFICATIONSDLGCLOSE: Back"),""));
		appendToHTML("</td>\n");
                appendToHTML("</tr>\n");
		appendToHTML("</table>\n");

                appendDirtyJavaScript("window.name=\"frmSearchEngine\";\n");

                
                appendDirtyJavaScript("function populateChild() {\n");
                appendDirtyJavaScript(" n = f.ROW_NO.options[f.ROW_NO.selectedIndex].value \n");
                appendDirtyJavaScript(" if (n!='') \n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("   document.form.ROWVAL.value = n;\n");
                appendDirtyJavaScript("   document.form.REFRESHCHILD.value = \"TRUE\";\n");
                appendDirtyJavaScript("   submit();\n");  
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
                appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=COMPETENCY_ELEMENT_LOV1&__FIELD=Competency+Element+Name&__INIT=1&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''");
                appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('COMPETENCY_ELEMENT_NAME',i))");
                appendDirtyJavaScript(",600,450,'validateCompetencyElementName');\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("else\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("openLOVWindow('COMPETENCY_ELEMENT_NAME',i,\n");
                appendDirtyJavaScript("'");
                appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=COMPETENCY_ELEMENT_LOV1&__FIELD=Competency+Element+Name&__INIT=1&MULTICHOICE='+enable_multichoice+''");
                appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('COMPETENCY_ELEMENT_NAME',i))");
                appendDirtyJavaScript("+ '&COMPETENCY_ELEMENT_NAME=' + URLClientEncode(key_value)");
                appendDirtyJavaScript("+ '&COMPETENCY_GROUP_NAME=' + URLClientEncode(getValue_('COMPETENCY_GROUP_NAME',i))");
                appendDirtyJavaScript(",600,450,'validateCompetencyElementName');\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}");
                
        }  
}
