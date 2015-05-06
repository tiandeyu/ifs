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
*  File        : AllocateEmployees.java 
*  Created     : THWILK  050809  Created
*  Modified    :
*  THWILK  050920  Added some required functionality under search for and allocate employees spec.
*  THWILK  050923  Modified addToOperationLines(),searchResource(),predefine() & added some java script functionality.
*  THWILK  051013  Added the required functionality under AMEC114:Multiple Allocations.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  THWILK  060126  Corrected localization errors.
*  SHAFLK  060721  Bug 59115, Modified addToOperationLines and validate().
* ----------------------------------------------------------------------------
*  AMDILK  060725  Merged with the Bug ID 59115
*  JEWILK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged Bug 58216.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  ILSOLK  070709  Eliminated XSS.
*  ILSOLK  070611  Date format not supported by oracle.(Call ID 148650)
*  INROLK  09115   Changed function 'handleButtons' to dissable Search Button when DATE FROM AND DATE TO are null.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.math.*;
import java.util.*;

public class AllocateEmployees extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.AllocateEmployees");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
        private ASPHTMLFormatter fmt;
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
        //===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
        private String val;
        private ASPBuffer row;
	private ASPCommand cmd;
        private ASPBuffer data;
	private ASPQuery q;
	private String n;
        private String txt;  
        private String rowNo;
        private String woNo;
        private String desc;
        private String woDesc;
        private ASPBuffer rowBuff;
        private String sSearchSeq;
        private String bItemlayShow;
        private String sSimpleSearchSeq;
        private boolean bOpenNewWindow;
        private String urlString;
        private String newWinHandle;
        private String frameName;
        private String qryStr;
        private String calling_url;
        private boolean bCloseWindow;
        private int noOfRows;
	private ASPBuffer buf;
        
        

	//===============================================================
	// Construction 
	//===============================================================
	public AllocateEmployees(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();
                fmt = mgr.newASPHTMLFormatter();
		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();
                rowBuff     = ctx.readBuffer("ROWBUFF");
                rowNo       = ctx.readValue("ROW_NO","");
                woNo        = ctx.readValue("WONO","");
                desc        = ctx.readValue("DESC","");
                woDesc      = ctx.readValue("WODESC","");
                sSimpleSearchSeq = ctx.readValue("CTXSIMPLESSEQ", "");
                sSearchSeq       = ctx.readValue("CTXSEARCHSEQ", "");
                bItemlayShow     = ctx.readValue("CTXITEMLAY","FALSE");
                frameName        = ctx.readValue("FRAMENAME","");
		qryStr           = ctx.readValue("QRYSTR","");
                calling_url      = ctx.readValue("CALLING_URL","");
                noOfRows         = ctx.readNumber("ROWSCOUNT", 0);
                
		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
                else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
                else if (mgr.buttonPressed("APPLY"))
                   addToOperationLines(); 
                else if (mgr.buttonPressed("OK"))
                {
                   addToOperationLines(); 
                   close();
                }
                else if (mgr.buttonPressed("CANCEL"))
                   close();
                else if (mgr.buttonPressed("ADDQUAL"))
                   additionalQualifications();
                else if (mgr.buttonPressed("SEARCHRESOURCE"))
                            searchResource();
                else if ("TRUE".equals(mgr.readValue("REFRESHCHILD")))
                    refreshChild();

		else if (mgr.dataTransfered())
                {
                    if (!mgr.isEmpty(mgr.getQueryStringValue("RES_TYPE")))
                       getInitialData();
                    else
                       setHeadData();
               }
                adjust();
                ctx.writeBuffer("ROWBUFF",rowBuff);  
                ctx.writeValue("ROW_NO",rowNo);
                ctx.writeValue("DESC",desc);          
                ctx.writeValue("WONO",woNo);
                ctx.writeValue("WODESC",woDesc);
                ctx.writeValue("CTXSIMPLESSEQ", sSimpleSearchSeq);
                ctx.writeValue("CTXSEARCHSEQ", sSearchSeq);
                ctx.writeValue("CTXITEMLAY", bItemlayShow);
                ctx.writeValue("FRAMENAME",frameName);
		ctx.writeValue("QRYSTR",qryStr);
                ctx.writeValue("CALLING_URL",calling_url);
                ctx.writeNumber("ROWSCOUNT", noOfRows);

                tabs.saveActiveTab();
                
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------
        public void getInitialData()
         {
                ASPManager mgr     = getASPManager();
                trans.clear();
                woNo  = mgr.getQueryStringValue("WO_NO");
                rowBuff = mgr.getTransferedData();
                frameName   = mgr.readValue("FRMNAME","");
                qryStr      = mgr.readValue("QRYSTR","");
                calling_url = ctx.getGlobal("CALLING_URL");
                ctx.setGlobal("CALLING_URL", calling_url);
                ctx.setGlobal("FRAMENAME", frameName);
                ctx.setGlobal("QRYSTR", qryStr);
                
                cmd = trans.addCustomFunction("GETERRDESC","Active_Work_Order_API.get_err_descr","WODESC");
                cmd.addParameter("WO_NO",woNo);

                trans = mgr.perform(trans);
                
                woDesc = trans.getValue("GETERRDESC/DATA/WODESC");
                trans.clear();
                if (rowBuff.countItems()>0)
                {
                   ASPBuffer data_buf = rowBuff.getBufferAt(0);
                   rowNo = data_buf.getValue("ROW_NO");
                   okFind();
                   
                   if (!mgr.isEmpty(headset.getValue("DESCRIPTION"))) 
                      desc = headset.getValue("DESCRIPTION");
                   else
                      desc="";
                    getCommonData();
                }
         }


        public void getCommonData()
        {
                ASPManager mgr     = getASPManager();
                row = headset.getRow();
                if (mgr.isEmpty(headset.getValue("DATE_FROM")) || mgr.isEmpty(headset.getValue("DATE_TO")))
                {
                   trans.clear();
                   
                   cmd = trans.addQuery("SYSDATE","SELECT SYSDATE DATE_FROM FROM DUAL");
                   trans = mgr.perform(trans);
                   String sysDate = trans.getBuffer("SYSDATE/DATA").getFieldValue("DATE_FROM");
                   if (mgr.isEmpty(headset.getValue("DATE_FROM")))
                      row.setFieldItem("DATE_FROM",sysDate);
                   if (mgr.isEmpty(headset.getValue("DATE_TO")))
                      row.setFieldItem("DATE_TO",sysDate);
                   

                }

                trans.clear();
                cmd = trans.addCustomFunction("SSEQ","Resource_Booking_Util_Api.Get_Next_Simple_Search_Seq","SIMPLE_SEARCH_SEQ");
                cmd = trans.addCustomCommand("COPYQUAL","Resource_Booking_Util_API.Copy_Qualifications_From_Wo");
                cmd.addParameter("WO_NO",woNo);
                cmd.addParameter("ROW_NO",rowNo);
                cmd.addParameter("MAINT_ORG_CONTRACT");
                cmd.addParameter("NULL");
                cmd.addReference("SIMPLE_SEARCH_SEQ","SSEQ/DATA");
            
                cmd=trans.addCustomFunction("HASQUALIFICATION","RESOURCE_BOOKING_UTIL_API.Has_Qualifications","CBADDQUALIFICATION");
                cmd.addParameter("NULL");
                cmd.addParameter("NULL");
                cmd.addReference("SIMPLE_SEARCH_SEQ","SSEQ/DATA");

                trans = mgr.perform(trans);
                                        
                sSimpleSearchSeq  = trans.getValue("SSEQ/DATA/SIMPLE_SEARCH_SEQ");
                String cbQualifications = trans.getValue("HASQUALIFICATION/DATA/CBADDQUALIFICATION");
                row.setFieldItem("CBADDQUALIFICATION",cbQualifications);
                row.setFieldItem("SIMPLE_SEARCH_SEQ",sSimpleSearchSeq);
                row.setFieldItem("DURATION",headset.getValue("PLAN_HRS"));
                row.setFieldItem("DESCRIPTION",desc);
                row.setFieldItem("REMAININGMANHOURS",headset.getValue("TOTMANHOURS"));
                headset.setRow(row);


        }

     public void setHeadData()
     {
    
        ASPManager mgr=getASPManager();
        ASPBuffer getAllValues  = mgr.getTransferedData();
        ASPBuffer headBuff    = getAllValues.getBufferAt(0);
        rowBuff = getAllValues.getBufferAt(1);
        rowNo = headBuff.getValue("ROW_NO");
        desc  = headBuff.getValue("DESCRIPTION");
        woNo  = headBuff.getValue("WO_NO");
        String cbQualifications="";
        sSimpleSearchSeq = ctx.getGlobal("SIMPLESEARCHSEQ");
        woDesc = ctx.getGlobal("WODESC");
        bItemlayShow = ctx.getGlobal("CTXITEMLAY");
        
        if ("TRUE".equals(bItemlayShow)) 
           sSearchSeq = ctx.getGlobal("QUERYSEQ");
        
        cmd=trans.addCustomFunction("HASQUALIFICATION","RESOURCE_BOOKING_UTIL_API.Has_Qualifications","CBADDQUALIFICATION");
        cmd.addParameter("NULL");
        cmd.addParameter("NULL");
        cmd.addParameter("SIMPLE_SEARCH_SEQ",sSimpleSearchSeq);
        trans=mgr.perform(trans);
        cbQualifications = trans.getValue("HASQUALIFICATION/DATA/CBADDQUALIFICATION");
        headBuff.setFieldItem("CBADDQUALIFICATION",cbQualifications);
        setRowBack(headBuff);
        okFindITEM0();
     }

     public void saveReturnItem0()
     {
       ASPManager mgr = getASPManager();
       trans.clear();
       String att1="";
       String maintIdList[] = split(mgr.readValue("SELLIST"), "~");
       String allocIdList[] = split(mgr.readValue("ALLOCLIST"), "~");
       
       if (noOfRows > 0)
       {
          itemset0.first();
          
          for (int i=0; i<noOfRows+1; ++i)
          {
              
             if (!mgr.isEmpty(maintIdList[i]) && !mgr.isEmpty(allocIdList[i]))
             {
              
             if ("1".equals(allocIdList[i])) 
              {
                 att1="";
                 att1 = "ALLOCATE" + (char)31 + allocIdList[i] + (char)30;
                 att1 = att1 + "PLANNED_HOURS" + (char)31 + maintIdList[i] + (char)30;
                 cmd = trans.addCustomCommand("MODI"+i,"RESULT_DETAILS_API.Modify__");
                 cmd.addParameter("INFO");
                 cmd.addParameter("OBJID",itemset0.getValue("OBJID"));
                 cmd.addParameter("OBJVERSION",itemset0.getValue("OBJVERSION"));
                 cmd.addParameter("ATTR",att1);
                 cmd.addParameter("ACTION","DO");
              }
             }
              itemset0.next();    
          }

        
         }
         trans = mgr.perform(trans);
 
     }


    public void addToOperationLines()
    {
      
      ASPManager mgr = getASPManager();
      trans.clear();
      String sRankList="";
      itemset0.changeRows();
      double planHrs =0;
      int plannedMen =0;
      double remainingManHrs=0;
      double allocatedManHrs=0;
      int icountSelectedRows = 0;
      double totManHrs = 0;
      headset.changeRow();
      ASPBuffer data = headset.getRow();
      itemset0.first();
     
         for (int i=0 ; i<itemset0.countRows(); i++)
         {
             
             if ("1".equals(itemset0.getValue("ALLOCATE"))) 
             {
               
                   sRankList = sRankList + "^" + itemset0.getValue("RANK");    
                   icountSelectedRows=icountSelectedRows +1;
             }

             itemset0.next();
         }
         sRankList = sRankList + "^";
         int endpos = sRankList.length();
         sRankList = sRankList.substring(1,endpos);
         plannedMen         = Integer.parseInt(headset.getValue("PLAN_MEN"));
         remainingManHrs    = headset.getNumberValue("REMAININGMANHOURS");
         allocatedManHrs    = headset.getNumberValue("ALLOCATEDMANHOURS");
         totManHrs          = headset.getNumberValue("TOTMANHOURS"); 

         if (allocatedManHrs < totManHrs )
         {
             if (icountSelectedRows >=plannedMen )
             {
                plannedMen = 1;
                
             }
             else
             {
               plannedMen =  plannedMen - icountSelectedRows;
             } 
         
            planHrs = remainingManHrs/plannedMen;

         } 
         
         data.setFieldItem("PLAN_MEN", mgr.getASPField("PLAN_MEN").formatNumber(plannedMen));
         data.setFieldItem("PLAN_HRS", mgr.getASPField("PLAN_HRS").formatNumber(planHrs));
         data.setFieldItem("TOTMANHOURS", mgr.getASPField("TOTMANHOURS").formatNumber(remainingManHrs));
         setRowBack(data);
         saveReturnItem0();
         trans.clear();
         cmd = trans.addCustomCommand("APPLYCHANGE","Resource_Allocation_Util_API.Allocate_Employees");
         cmd.addParameter("INFO");
         cmd.addParameter("WO_NO");
         cmd.addParameter("ROW_NO");
         cmd.addParameter("OBJVERSION");
         cmd.addParameter("QUERY_SEQ",sSearchSeq);
         cmd.addParameter("NULL",sRankList);
         cmd.addParameter("DATE_FROM");
         cmd.addParameter("DATE_TO");
         cmd.addParameter("TEAM_ID");
         cmd.addParameter("TEAM_CONTRACT");
         
         mgr.perform(trans);
         
         okFindITEM0();
         if (itemset0.countRows()==0)
           bItemlayShow = "FALSE";

    }

    public void close()
    {
     qryStr       = ctx.getGlobal("QRYSTR");
     frameName    = ctx.getGlobal("FRAMENAME");
     calling_url  = ctx.getGlobal("CALLING_URL");
     bCloseWindow = true;

     }
     

    public void validate()
	{
	    ASPManager mgr = getASPManager();

            val = mgr.readValue("VALIDATE");         
            String teamDesc;
            

            if ("ALLOCATE".equals(val))
            {
                double allocatedManHrs = mgr.readNumberValue("ALLOCATEDMANHOURS");
                double totManHrs       = mgr.readNumberValue("TOTMANHOURS");
                double duration        = mgr.readNumberValue("ITEM0_DURATION"); 
                
                if (isNaN(allocatedManHrs))
                    allocatedManHrs = 0;
                if (isNaN(totManHrs))
                    totManHrs = 0;
                
                if ("1".equals(mgr.readValue("ALLOCATE"))) 
                {
                   allocatedManHrs += duration;   
                }
                else
                {
                   allocatedManHrs = allocatedManHrs - duration;   
                 
                }
                
                double remainingManHrs = totManHrs - allocatedManHrs;
                
                String alocateManHrs = mgr.getASPField("ALLOCATEDMANHOURS").formatNumber(allocatedManHrs);
                String remainManHrs  = mgr.getASPField("ALLOCATEDMANHOURS").formatNumber(remainingManHrs);
                
                txt = (mgr.isEmpty(alocateManHrs) ? "" : (alocateManHrs)) + "^" +
                      (mgr.isEmpty(remainManHrs) ? "" : (remainManHrs)) + "^";
                      
                   
                mgr.responseWrite(txt);

            }
            else if ("MAINT_ORG".equals(val))
            {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[6];

            String sOrgCode = "";
            String sOrgContract = "";
            String new_org_code = mgr.readValue("MAINT_ORG","");
            
            if (new_org_code.indexOf("^",0)>0)
            {
               for (i=0 ; i<2; i++)
               {
                  endpos = new_org_code.indexOf("^",startpos);
                  reqstr = new_org_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;
               }
                  sOrgCode = ar[0];
                  sOrgContract = ar[1];
            }
            else
            {
                sOrgCode = mgr.readValue("MAINT_ORG");
                sOrgContract = mgr.readValue("MAINT_ORG_CONTRACT"); 
            }

                txt = (mgr.isEmpty(sOrgCode)?"":sOrgCode)+"^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract)+"^";

            mgr.responseWrite(txt);
           }

            else if ("ROLE_CODE".equals(val))
            {
               String reqstr = null;
               int startpos = 0;
               int endpos = 0;
               int i = 0;
               String ar[] = new String[2];
               String sCompetence = "";

               String new_competence = mgr.readValue("ROLE_CODE","");

               if (new_competence.indexOf("^",0)>0)
               {
                for (i=0 ; i<1; i++)
                {
                    endpos = new_competence.indexOf("^",startpos);
                    reqstr = new_competence.substring(startpos,endpos);
                    ar[i] = reqstr;
                    startpos= endpos+1;
                }
                sCompetence = ar[0];
               }
               else
                sCompetence = new_competence;

               trans.clear();

               cmd = trans.addCustomFunction("GETDESCR","Role_API.Get_Description","ROLEDESC");
               cmd.addParameter("ROLE_CODE",sCompetence);

               cmd = trans.addCustomFunction("GETSUBCRAFT","Role_To_Site_API.Is_In_Craft_Group","CBSUBCRAFTS");
               cmd.addParameter("ROLE_CODE",sCompetence);
               cmd.addParameter("MAINT_ORG_CONTRACT");

                           
               trans = mgr.validate(trans);
               String sDescr    = trans.getValue("GETDESCR/DATA/ROLEDESC");
               String sSubCraft = trans.getValue("GETSUBCRAFT/DATA/CBSUBCRAFTS");
               
               boolean bSubCraft;
               if ("TRUE".equals(sSubCraft)) 
                  bSubCraft = true;
               else
                  bSubCraft = false;
               

               txt = (mgr.isEmpty(sCompetence) ? "" : (sCompetence)) + "^" +
                   (mgr.isEmpty(sDescr) ? "" : (sDescr)) + "^" +
                   (mgr.isEmpty(sSubCraft) ? "" : (sSubCraft)) + "^" ;

               mgr.responseWrite(txt);
           }

	    else if ("DATE_FROM".equals(val))
	    {
		buf = mgr.newASPBuffer();
		buf.addFieldItem("DATE_FROM",mgr.readValue("DATE_FROM",""));      
		DateFormat df= DateFormat.getInstance();
		Date dt = null;
		try
		{
		    dt = df.parse(mgr.readValue("DATE_FROM",""));
		}
		catch (ParseException e)
		{
		    e.printStackTrace();
		}
		buf.addFieldDateItem("DATE_FROM_MASKED", dt);
                txt =buf.getFieldValue("DATE_FROM_MASKED") + "^";
		mgr.responseWrite(txt);
	    }

            else if ("TEAM_ID".equals(val))
            {
            cmd = trans.addCustomFunction("TDESC", "Maint_Team_API.Get_Description", "TEAMDESC" );    
            cmd.addParameter("TEAM_ID");
            cmd.addParameter("TEAM_CONTRACT");
            trans = mgr.validate(trans);   
            teamDesc  = trans.getValue("TDESC/DATA/TEAMDESC");

            txt =  (mgr.isEmpty(teamDesc) ? "" : (teamDesc)) + "^";
            mgr.responseWrite(txt);

            }
                       
         mgr.endResponse();

	       
	}

    public void refreshChild()
    {
            ASPManager mgr = getASPManager();
            trans.clear();
            rowNo = mgr.readValue("ROWVAL");
            
            if (headset.countRows()>0)
             {
                okFind();
                q = trans.addQuery("GETEVENT","SELECT DESCRIPTION FROM WORK_ORDER_ROLE WHERE WO_NO = ? AND ROW_NO = ?");
                q.addParameter("WO_NO",woNo);
                q.addParameter("ROW_NO",rowNo);
                trans = mgr.perform(trans);
                ASPBuffer getEvent = trans.getBuffer("GETEVENT/DATA");
                desc = getEvent.getValue("DESCRIPTION");
                
                if (mgr.isEmpty(desc)) 
                   desc="";
                getCommonData();
                
                if (itemset0.countRows()>0) 
                {
                   itemset0.clear();
                   bItemlayShow = "FALSE";
                }
                

             }
    }

    
    private void searchResource()
    {
        ASPManager mgr = getASPManager();

        String infoMsg;
        
        trans.clear();
        
        cmd = trans.addCustomFunction("GETSEQVAL","Resource_Booking_Util_API.Get_Next_Qry_Seq","QUERY_SEQ");
        String  bSubCraft;
        if ("TRUE".equals(mgr.readValue("CBSUBCRAFTS")))
               bSubCraft = "1";
           else
               bSubCraft = "0";
           
           cmd = trans.addCustomCommand( "SYNCRONIZE", "Resource_Booking_Util_API.Synchronize_Qualifications");  
           cmd.addParameter("SIMPLE_SEARCH_SEQ",sSimpleSearchSeq);
           cmd.addReference("QUERY_SEQ","GETSEQVAL/DATA");
           cmd.addParameter("NULL");
           cmd.addParameter("ROLE_CODE");
           cmd.addParameter("MAINT_ORG_CONTRACT");
           cmd.addParameter("COMPANY");
           cmd.addParameter("NULL");
           
            cmd = trans.addCustomCommand("PERFSIMPSEARCH","Resource_Allocation_Util_API.Search_For_Allocate_Employees");
            cmd.addParameter("INFO");
            cmd.addReference("QUERY_SEQ","GETSEQVAL/DATA");
            cmd.addParameter("DURATION");
            cmd.addParameter("DATE_FROM");
            cmd.addParameter("DATE_TO");
            cmd.addParameter("ROLE_CODE");
            cmd.addParameter("MAINT_ORG");
            cmd.addParameter("MAINT_ORG_CONTRACT");
            cmd.addParameter("TEAM_ID");
            cmd.addParameter("TEAM_CONTRACT");
            cmd.addParameter("CBEXCLUDESCHEDULE");
            cmd.addParameter("CBSUBCRAFTS",bSubCraft);
            cmd.addParameter("CBSHOWALL");

            
            trans = mgr.perform(trans);

            sSearchSeq        = trans.getValue("GETSEQVAL/DATA/QUERY_SEQ");
            infoMsg = trans.getValue("PERFSIMPSEARCH/DATA/INFO");
            
            if ("1".equals(mgr.readValue("CBSHOWALL")))
               mgr.getASPField("ALLOCATED").unsetHidden();
                
            headset.changeRow();
            ASPBuffer data = headset.getRow();
            data.setFieldItem("ALLOCATEDMANHOURS","0");
            data.setFieldItem("REMAININGMANHOURS",headset.getValue("TOTMANHOURS"));
            setRowBack(data);
            okFindITEM0();
            
            if (itemset0.countRows()>0)
               bItemlayShow = "TRUE";
                          
           
            if ((itemset0.countRows() == 0) && (!mgr.isEmpty(infoMsg)))
            {
               clearSearchResults();
               mgr.showAlert(infoMsg.substring(4));
            }

            tabs.setActiveTab(2);
        
     }

     private void setRowBack(ASPBuffer valueBuffer)
     {
             ASPManager mgr = getASPManager();

             headset.addRow(valueBuffer);

     }

     private void clearSearchResults()
     {
        ASPManager mgr = getASPManager();

        if (itemset0.countRows() > 0)
        {
            itemset0.clear();
            tabs.setActiveTab(2);
            itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
        }
     }


     private void additionalQualifications()
     {

           ASPManager mgr = getASPManager();
           headset.changeRow();
           ASPBuffer headsetValues = headset.getRow();
           String calling_url = mgr.getURL();
           
           if (mgr.isEmpty(sSimpleSearchSeq))
           {
              cmd = trans.addCustomFunction("SSEQ","Resource_Booking_Util_API.Get_Next_Simple_Search_Seq","SIMPLE_SEARCH_SEQ");
              trans = mgr.perform(trans);
              sSimpleSearchSeq = trans.getValue("SSEQ/DATA/SIMPLE_SEARCH_SEQ");
           }
           ctx.setGlobal("SIMPLESEARCHSEQ", sSimpleSearchSeq);
           if (!mgr.isEmpty(sSearchSeq)) 
              ctx.setGlobal("QUERYSEQ", sSearchSeq);
           
           
           ctx.setGlobal("CALLING_URL", calling_url);
           ctx.setGlobal("WODESC", woDesc);
           ctx.setGlobal("CTXITEMLAY", bItemlayShow);
           ASPBuffer addAllValues = mgr.newASPBuffer();;
           addAllValues.addBuffer("HEAD",headsetValues);
           addAllValues.addBuffer("ROWS",rowBuff);
           bOpenNewWindow = true;
           urlString= createTransferUrl("SearchQualificationsDlg.page?&RES_TYPE=ALLOCATEEMP"+
                        "&SIMPLE_SEARCH_SEQ="+sSimpleSearchSeq,addAllValues);

           newWinHandle = "AllocateEmployees"; 
     }


     public void workOrdersForCraftsEmp()
     {
        ASPManager mgr = getASPManager();
        bOpenNewWindow = true;
        urlString = "WorkOrderRoleOvw.page?&RES_TYPE=ALLOCEMP"+
                     "&COMPANY=" + mgr.URLEncode(headset.getValue("COMPANY")) +
                     "&EMP_NO=" + mgr.URLEncode(itemset0.getValue("EMP_NO")) +
                     "&DATE_FROM=" + mgr.URLEncode(headset.getValue("DATE_FROM")) +
                     "&DATE_TO=" + mgr.URLEncode(headset.getValue("DATE_TO"));   
        newWinHandle = "WorkOrderRoleOvw"; 
         
     }

     public void workOrdersForCraftsEmp1()
     {
        ASPManager mgr = getASPManager();
        bOpenNewWindow = true;
        urlString = "WorkOrderRoleOvw.page?&RES_TYPE=ALLOCEMP"+
                     "&COMPANY=" + mgr.URLEncode(headset.getValue("COMPANY")) +
                     "&EMP_NO=" + mgr.URLEncode(itemset1.getValue("SIGN_ID")) +
                     "&DATE_FROM=" + mgr.URLEncode(headset.getValue("DATE_FROM")) +
                     "&DATE_TO=" + mgr.URLEncode(headset.getValue("DATE_TO"));   
        newWinHandle = "WorkOrderRoleOvw"; 
         
     }
     
     public void clearEmployeeAllocation()
     {
      
        ASPManager mgr = getASPManager();
        int headRowNo = headset.getCurrentRowNo();   

        int count,currentRow;
        String rowString="";
           if (itemlay1.isMultirowLayout())
           {
              itemset1.storeSelections();
              itemset1.setFilterOn();
              count = itemset1.countSelectedRows();
              
           }
           else
           {
                 itemset1.unselectRows();
                 itemset1.selectRow();
                 count = 1;   
           } 

           if (itemlay1.isMultirowLayout())
           {
              itemset1.first();
              for (int i = 0; i < count; i++)
              {     
                                    
                 if (i==0) 
                    rowString =itemset1.getValue("OP_ROW_NO"); 
                 else
                    rowString = rowString + "^" + itemset1.getValue("OP_ROW_NO");  
                 if (itemlay1.isMultirowLayout())
                    itemset1.next();
              }
           }
           else
              rowString =itemset1.getRow().getValue("OP_ROW_NO");

           rowString = rowString + "^";
           
           if (itemlay1.isMultirowLayout())
              itemset1.setFilterOff();
          
           cmd = trans.addCustomCommand("APPLYCHANGE","WORK_ORDER_ROLE_API.Clear_Employee_Allocation");
           cmd.addParameter("WO_NO");
           cmd.addParameter("NULL",rowString);
           mgr.perform(trans);
           okFindITEM1();
           
     } 

     public void activateSearchResults()
     {
         tabs.setActiveTab(2);
         okFindITEM0();
     }

     public void activateMultiAllocations()
     {
         tabs.setActiveTab(1);
         okFindITEM1();
     }


    private String getDateTimeFormat(String type)
    {
        if("JAVA".equals(type)){
            return "yyyy-MM-dd HH:mm:ss";
        }
        else if ("SQL".equals(type)){
            return "yyyy-MM-dd HH24:mi:ss";
        }
        else
            return getASPManager().getFormatMask("Datetime",true);
    }
    
//-----------------------------------------------------------------------------
//---------------------------  CMDBAR FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

       public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		n = headset.getRow().getValue("N");
		headlay.setCountValue(toInt(n));
		headset.clear();
	}


	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");
                q.addWhereCondition("WO_NO = ? AND ROW_NO = ?");
                q.addParameter("WO_NO",woNo);
                q.addParameter("ROW_NO",rowNo);
                        
		mgr.submit(trans);
                
                if (headset.countRows() == 1)
                {
                   if (tabs.getActiveTab() == 1)
                      okFindITEM1();
                   else if (tabs.getActiveTab() == 2) 
                      okFindITEM0();
                } 
		else if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWALLOCATEEMPLOYEENODATA: No data found."));
		}
		
	}

        public void okFindITEM0()
        {
        ASPManager mgr = getASPManager();

        clearSearchResults();

        trans.clear();
        ASPBuffer headsetValues = headset.getRow();
        headset.clear();

        itemset0.clear(); 
        ASPQuery q = trans.addQuery(itemblk0);
        q.addWhereCondition("QUERY_SEQ = ? AND NVL(ALLOCATE,0)=0");
        q.addParameter("QUERY_SEQ",sSearchSeq);
        q.includeMeta("ALL"); 

        mgr.submit(trans);
        setRowBack(headsetValues);
        noOfRows = itemset0.countRows();


       }

        public void okFindITEM1()
         {
             ASPManager mgr = getASPManager();

             trans.clear();
             ASPBuffer headsetValues = headset.getRow();
             headset.clear();

             int currrow = itemset1.getCurrentRowNo();
             
             q = trans.addQuery(itemblk1);
             q.addWhereCondition("WO_NO = ? AND ROW_NO = ?");
             q.addParameter("WO_NO",woNo);
             q.addParameter("ROW_NO",rowNo);
             q.includeMeta("ALL");
             mgr.submit(trans);

             if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
                 {
                     mgr.showAlert(mgr.translate("PCMWALLOCATEEMPLOYEESNODATA: No data found."));
                     itemset1.clear();
                 }
             
             setRowBack(headsetValues);
             itemset1.goTo(currrow);
         }


        private String createTransferUrl(String url, ASPBuffer object)
        {
         ASPManager mgr = getASPManager();

         try
         {
             String pkg = mgr.pack(object,1900 - url.length());
             char sep = url.indexOf('?')>0 ? '&' : '?';
             urlString = url + sep + "__TRANSFER=" + pkg ;
             return urlString;
         }
         catch (Throwable any)
         {
             return null;
         }
        }

        public void adjust()
        {
          ASPManager mgr = getASPManager();
          if ("1".equals(mgr.readValue("CBEXCLUDESCHEDULE"))) {
              mgr.getASPField("SCHEDULE_START").setHidden();
              mgr.getASPField("SCHEDULE_END").setHidden();
         
          }

          headbar.removeCustomCommand("activateSearchResults");
          headbar.removeCustomCommand("activateMultiAllocations");

           if (itemset0.countRows()>0)
            {
             itembar0.disableCommand(itembar0.DUPLICATEROW);
             itembar0.disableCommand(itembar0.DELETE);
           }
          

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
                
                headblk.addField("ROW_NO","Number").
                setHidden();
                
                headblk.addField("DESCRIPTION").
                setSize(60).
                setHidden();
                
                headblk.addField("WO_NO","Number","#").
                setDynamicLOV("ACTIVE_WORK_ORDER",600,445).
                setMandatory().
                setHidden().
                setInsertable();

                headblk.addField("COMPANY").
		setSize(8).
		setHidden().
                setFunction("Work_Order_API.Get_Company(:WO_NO)").
                setMaxLength(20).
		setLabel("PCMWALLOCATEEMPLOYEESCOMPANY: Company").
		setUpperCase();

                
                headblk.addField("PLAN_MEN","Number").
                setLabel("PCMWALLOCATEEMPLOYEESPLANMEN: Planned Men").
                setReadOnly();

                headblk.addField("PLAN_HRS","Number").
                setLabel("PCMWALLOCATEEMPLOYEESPLANHRS: Planned Hours").
                setReadOnly();
                

                headblk.addField("DURATION","Number").
                setLabel("PCMWALLOCATEEMPLOYEESDURATION: Duration").
                setMandatory().
                setValidateFunction("handleButtons").
                setFunction("''");

                headblk.addField("TOTMANHOURS","Number").
                setLabel("PCMWALLOCATEEMPLOYEESTOTOTHRS: Total Hours").
                setReadOnly().
                setMandatory().
                setFunction(":PLAN_MEN *:PLAN_HRS");

                headblk.addField("ALLOCATEDMANHOURS","Number").
                setLabel("PCMWALLOCATEEMPLOYEESTALLOCHRS: Allocated Hours").
                setReadOnly().
                setMandatory().
                setFunction("''");

                headblk.addField("REMAININGMANHOURS","Number").
                setLabel("PCMWALLOCATEEMPLOYEESREMAINHRS: Remaining Hours").
                setReadOnly().
                setMandatory().
                setFunction("''");
                
                headblk.addField("DATE_FROM","Datetime").
                setSize(22).
                setMandatory().
                setValidateFunction("handleButtons").        
                setCustomValidation("DATE_FROM","DATE_FROM_MASKED").
                setLabel("PCMWALLOCATEEMPLOYEESDATEFROM: Date From");

                headblk.addField("DATE_FROM_MASKED","Datetime", getDateTimeFormat("JAVA")).
                setHidden().
                setFunction("''");
                
                headblk.addField("DATE_TO","Datetime").
                setMandatory().
                setSize(22).
                setValidateFunction("handleButtons").
                setLabel("PCMWALLOCATEEMPLOYEESDATETO: Date To");
                
                headblk.addField("ROLE_CODE").
                setSize(10).
                setDynamicLOV("ROLE_TO_SITE_LOV","MAINT_ORG_CONTRACT",600,445).
                setCustomValidation("ROLE_CODE,MAINT_ORG_CONTRACT","ROLE_CODE,ROLEDESC,CBSUBCRAFTS").
                setLabel("PCMWALLOCATEEMPLOYEESROLECODE: Craft ID").
                setUpperCase().
                setMaxLength(10);

                headblk.addField("ROLEDESC").
                setFunction("Role_Api.Get_Description(ROLE_CODE)").
                setSize(40).
                setMaxLength(200).
                setReadOnly().
                setLabel("PCMWALLOCATEEMPLOYEESROLEDESC: Description");

                headblk.addField("MAINT_ORG_CONTRACT").
                setSize(8).
                setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
                setLabel("PCMWALLOCATEEMPLOYEESCONTRACT: Maint.Org. Site").
                setUpperCase().
                setMaxLength(5);

                headblk.addField("MAINT_ORG").
                setSize(8).
                setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","MAINT_ORG_CONTRACT",600,450).
                setLabel("PCMWALLOCATEEMPLOYEESORGCODE: Maint.Org.").
                setCustomValidation("MAINT_ORG,MAINT_ORG_CONTRACT","MAINT_ORG,MAINT_ORG_CONTRACT").
                setUpperCase().
                setMaxLength(8);

                headblk.addField("ORGCODEDESCR").
                setSize(29).
                setLabel("PCMWALLOCATEEMPLOYEESORGDESC: Description").
                setFunction("Organization_API.Get_Description(:MAINT_ORG_CONTRACT,:MAINT_ORG)").
                setReadOnly();
                              
                headblk.addField("TEAM_CONTRACT").
                setSize(7).
                setDynamicLOV("USER_ALLOWED_SITE_LOV","COMPANY",600,450).
                setLabel("PCMWALLOCATEEMPLOYEESCONT: Team Site").
                setMaxLength(5).
                setInsertable().
                setUpperCase();

                headblk.addField("TEAM_ID").
                setSize(20).
                setCustomValidation("TEAM_ID,TEAM_CONTRACT","TEAMDESC").
                setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450).
                setQueryable().
                setMaxLength(100).
                setInsertable().
                setLabel("PCMWALLOCATEEMPLOYEESTID: Team ID").
                setUpperCase();

                headblk.addField("TEAMDESC").
                setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)").
                setSize(40).
                setMaxLength(200).
                setReadOnly().
                setLabel("PCMWALLOCATEEMPLOYEESTEAMDESC: Description");

                headblk.addField("CBSHOWALL").
                setLabel("PCMWALLOCATEEMPLOYEESCBSHOWALL: Show All").
                setFunction("''").
                setCheckBox("0,1");

                headblk.addField("CBEXCLUDESCHEDULE").
                setLabel("PCMWALLOCATEEMPLOYEESCBADDEXSCHE: Exclude Schedule").
                setCheckBox("0,1").
                setFunction("''");
                
                headblk.addField("CBADDQUALIFICATION").
                setLabel("PCMWALLOCATEEMPLOYEESCBADDQUAL: Additional Qualifications").
                setCheckBox("FALSE,TRUE").
                setFunction("''").
                setReadOnly();

                headblk.addField("CBSUBCRAFTS").
                setLabel("PCMWALLOCATEEMPLOYEESCBADDCRAFT: Include Sub Crafts").
                setCheckBox("FALSE,TRUE").
                setFunction("Role_To_Site_API.Is_In_Craft_Group(:ROLE_CODE,:MAINT_ORG_CONTRACT)");
                
                headblk.addField("WODESC").
                setHidden().
                setFunction("''");
                
                headblk.addField("INFO").
                setFunction("''").
                setHidden();

                headblk.addField("SIMPLE_SEARCH_SEQ","Number").
                setFunction("''").
                setHidden();

                f = headblk.addField("NULL");
                f.setHidden();

                headblk.setView("EMPLOYEE_ALOCATION_HEADER");
		headblk.defineCommand("RESOURCE_ALLOCATION_UTIL_API","");
		
                headblk.disableDocMan();


		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
                headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWALLOCATEEMPLOYEESTBL: AllocateEmployees"));
		headtbl.setWrap();
                headbar.disableMinimize();
                
                headlay = headblk.getASPBlockLayout();
                headlay.defineGroup("","PLAN_MEN,PLAN_HRS,DURATION,WO_NO,COMPANY,ROW_NO,DESCRIPTION,WODESC",true,true);
                headlay.defineGroup(mgr.translate("PCMWALLOCATEEMPLOYEESGP1: Where To Search"),"TEAM_CONTRACT,TEAM_ID,TEAMDESC,MAINT_ORG_CONTRACT,MAINT_ORG,ORGCODEDESCR",true,true);
                headlay.defineGroup(mgr.translate("PCMWALLOCATEEMPLOYEESGP2: Skill"),"ROLE_CODE,ROLEDESC",true,true);
                headlay.defineGroup(mgr.translate("PCMWALLOCATEEMPLOYEESGP3: Search Time Internval"),"DATE_FROM,DATE_TO",true,true);
                headlay.defineGroup(mgr.translate("PCMWALLOCATEEMPLOYEESGP4: Hours Left"),"TOTMANHOURS,ALLOCATEDMANHOURS,REMAININGMANHOURS",true,true);
                headlay.defineGroup("","CBSHOWALL,CBEXCLUDESCHEDULE,CBADDQUALIFICATION,CBSUBCRAFTS",true,true);
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
                headlay.setEditable();

                headbar.addCustomCommand("activateSearchResults", "");
                headbar.addCustomCommand("activateMultiAllocations", "");

                tabs = mgr.newASPTabContainer();
                tabs.addTab(mgr.translate("PCMWALLOCATEEMPLOYEESTAB1: Allocated Employees"), "javascript:commandSet('HEAD.activateMultiAllocations','')");
                tabs.addTab(mgr.translate("PCMWALLOCATEEMPLOYEESTAB2: Search Results"), "javascript:commandSet('HEAD.activateSearchResults','')");

       //--------------------------ITEMBLK0-------------------------------------------------
                 
                itemblk0 = mgr.newASPBlock("ITEM0");

                itemblk0.addField("ITEM0_OBJID").
                setDbName("OBJID").
                setHidden();

                itemblk0.addField("ITEM0_OBJVERSION").
                setDbName("OBJVERSION").
                setHidden();

                itemblk0.addField("QUERY_SEQ","Number").
                setHidden();

                itemblk0.addField("ORDER_NO","Number").
                setLabel("PCMWALLOCATEEMPLOYEESORDERNO: Sequence").
                setSize(10).
                setHidden();
                
                itemblk0.addField("ALLOCATE").
                setLabel("PCMWALLOCATEEMPLOYEESALLOCATE: Allocate").
                setValidateFunction("handleButtons").
                setCheckBox("0,1").
                setQueryable().
                setCustomValidation("ALLOCATEDMANHOURS,ITEM0_DURATION,TOTMANHOURS,ALLOCATE","ALLOCATEDMANHOURS,REMAININGMANHOURS");
                
                itemblk0.addField("EMP_NO").
                setSize(15).
                setLabel("PCMWALLOCATEEMPLOYEESEMPID: Employee ID").
                setUpperCase().
                setReadOnly().
                setMaxLength(40).
                setReadOnly();

                itemblk0.addField("NAME").
                setReadOnly().
                setSize(15).
                setLabel("PCMWALLOCATEEMPLOYEESEMPNAME: Name").
                setUpperCase().
                setFunction("Employee_API.Get_Name(Site_API.Get_Company(:CONTRACT),:EMP_NO)").
                setReadOnly();
               
                itemblk0.addField("DATE_TIME_FROM", "Datetime").
                setReadOnly().
                setSize(18).
                setMandatory().
                setLabel("PCMWALLOCATEEMPLOYEESDATEFROM: Date From").
                setInsertable();

                itemblk0.addField("DATE_TIME_TO", "Datetime").
                setReadOnly().
                setSize(18).
                setMandatory().
                setLabel("PCMWALLOCATEEMPLOYEESDATETO: Date To");

                itemblk0.addField("ITEM0_DURATION", "Number").
                setSize(5).
                setMandatory().
                setInsertable().
                setDbName("PLANNED_HOURS").
                setLabel("PCMWALLOCATEEMPLOYEESDURATION: Duration");

                itemblk0.addField("ALLOCATED").
                setLabel("PCMWALLOCATEEMPLOYEESALLOCATED: Allocated").
                setHidden().
                setReadOnly().
                setCheckBox("FALSE,TRUE").
                setQueryable();
                
                itemblk0.addField("ITEM0_ROLE_CODE").
                setSize(10).
                setDbName("ROLE_CODE").
                setReadOnly().
                setLabel("PCMWALLOCATEEMPLOYEESITEM0ROLECODE: Craft ID").
                setUpperCase().
                setMaxLength(10);

                itemblk0.addField("SIGNATURE").  
                setSize(20).
                setReadOnly().
                setMaxLength(200).
                setFunction("Employee_API.Get_Signature(Site_API.Get_Company(:CONTRACT),:EMP_NO)").
                setReadOnly().
                setLabel("PCMWALLOCATEEMPLOYEESDESC: Signature");

                itemblk0.addField("CONTRACT").
                setSize(8).
                setReadOnly().
                setLabel("PCMWALLOCATEEMPLOYEESCONTRACT: Maint.Org. Site").
                setUpperCase().
                setMaxLength(5);

                itemblk0.addField("ORG_CODE").
                setSize(8).
                setReadOnly().
                setLabel("PCMWALLOCATEEMPLOYEESORGCODE: Maint.Org.").
                setUpperCase().
                setMaxLength(8);

                itemblk0.addField("RANK","Number").
                setLabel("PCMWALLOCATEEMPLOYEESRANK: Rank").
                setReadOnly();

                itemblk0.addField("TOT_HOURS_AVAILABLE", "Number").
                setLabel("PCMWALLOCATEEMPLOYEESTOTALHRSAVAILABLE: Total Hours Available").
                setReadOnly();
                
                itemblk0.addField("SCHEDULE_START", "Datetime").
                setFunction("RESOURCE_ALLOCATION_UTIL_API.Get_Schedule_Date(Site_API.Get_Company(:CONTRACT),:EMP_NO,:CONTRACT,:ORG_CODE,:DATE_TIME_FROM,'0')").
                setReadOnly().
                setSize(18).
                setLabel("PCMWALLOCATEEMPLOYEESCHDSTART: Schedule Start");

                itemblk0.addField("SCHEDULE_END", "Datetime").
                setReadOnly().
                setFunction("RESOURCE_ALLOCATION_UTIL_API.Get_Schedule_Date(Site_API.Get_Company(:CONTRACT),:EMP_NO,:CONTRACT,:ORG_CODE,:DATE_TIME_FROM,'1')").
                setSize(18).
                setLabel("PCMWALLOCATEEMPLOYEESCHDEND: Schedule End");
                
                itemblk0.addField("ATTR").
                setFunction("''").
                setHidden();
                
                itemblk0.addField("ACTION").
                setFunction("''").
                setHidden();
                
                itemblk0.setView("EMPLOYEE_ALOCATION_RESULTS");
                itemblk0.defineCommand("RESULT_DETAILS_API","Modify__");
                itemset0 = itemblk0.getASPRowSet();

                itembar0 = mgr.newASPCommandBar(itemblk0);
                itembar0.disableCommand(itembar0.FIND);
                itembar0.disableMinimize();
                itembar0.defineCommand(itembar0.SAVERETURN,null,"checkMandoItem0()");
                itembar0.disableCommand(itembar0.EDITROW);
                itembar0.disableCommand(itembar0.NEWROW);
                itembar0.disableMinimize();
                itembar0.addCustomCommand("workOrdersForCraftsEmp",mgr.translate("PCMWALLOCATEEMPLOYEESWOFORCRAFTEMP: Work Orders for Craft/Employee..."));          
                itemtbl0 = mgr.newASPTable(itemblk0);
                itemtbl0.setWrap();
                itemtbl0.setEditable();

                itemblk0.setMasterBlock(headblk);
                itemlay0 = itemblk0.getASPBlockLayout();
                itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
                itemlay0.setEditable();
                itemlay0.setDialogColumns(2);  

    
        //------------Allocation on Operations - child of operations-------------

        itemblk1 = mgr.newASPBlock("ITEM1");

        itemblk1.addField("ITEM1_ROW_NO","Number").
        setLabel("PCMWALLOCATEEMPLOYEESITEM1ROWNO: Operation No").
        setDbName("ROW_NO").
        setHidden().
        setInsertable();

        itemblk1.addField("OP_ROW_NO","Number").
        setHidden();
        
        itemblk1.addField("ALLOCATION_NO","Number").
        setLabel("PCMWALLOCATEEMPLOYEESALLOCNO: Allocation No");
        

        itemblk1.addField("ITEM1_DESCRIPTION").
        setDbName("DESCRIPTION").
        setLabel("PCMWALLOCATEEMPLOYEESALLOCDESC: Description");

        itemblk1.addField("ITEM1_SIGN").
        setSize(20).
        setLabel("PCMWALLOCATEEMPLOYEESITEM1SIGN: Executed By").
        setDbName("SIGN").
        setUpperCase().
        setMaxLength(20);

        itemblk1.addField("ITEM1_SIGN_ID").
        setSize(18).
        setMaxLength(11).
        setLabel("PCMWALLOCATEEMPLOYEESITEM1SIGNID: Employee ID").
        setDbName("SIGN_ID").
        setUpperCase().
        setReadOnly();

        itemblk1.addField("ITEM1_ORG_CODE").
        setSize(8).
        setLabel("PCMWALLOCATEEMPLOYEESITEM1ORGCODE: Maint.Org.").
        setUpperCase().
        setDbName("ORG_CODE").
        setMaxLength(8);
        
        itemblk1.addField("ITEM1_CONTRACT").
        setSize(8).
        setLabel("PCMWALLOCATEEMPLOYEESITEM1CONTRACT: Maint.Org. Site").
        setUpperCase().
        setDbName("CONTRACT").
        setMaxLength(5);

        itemblk1.addField("ITEM1_ROLE_CODE").
        setSize(10).
        setLabel("PCMWALLOCATEEMPLOYEESITEM1ROLECODE: Craft ID").
        setDbName("ROLE_CODE").
        setUpperCase().
        setMaxLength(10);

        itemblk1.addField("ALLOCATED_HOURS","Number").
        setLabel("PCMWALLOCATEEMPLOYEESALLOCHRS: Allocated Hours");
        

        itemblk1.addField("ITEM1_TEAM_CONTRACT").
        setSize(7).
        setLabel("PCMWALLOCATEEMPLOYEESITEM1TCONT: Team Site").
        setMaxLength(5).
        setDbName("TEAM_CONTRACT").
        setInsertable().
        setUpperCase();

        itemblk1.addField("ITEM1_TEAM_ID").
        setSize(13).
        setMaxLength(20).
        setDbName("TEAM_ID").
        setInsertable().
        setLabel("PCMWALLOCATEEMPLOYEESITEM1TEAMID: Team ID").
        setUpperCase();

        itemblk1.addField("ITEM1_TEAMDESC").
        setFunction("Maint_Team_API.Get_Description(:ITEM1_TEAM_ID,:ITEM1_TEAM_CONTRACT)").
        setSize(40).
        setMaxLength(200).
        setReadOnly().
        setLabel("PCMWALLOCATEEMPLOYEESITEM1DESC: Description");

        itemblk1.addField("ITEM1_DATE_FROM","Datetime").
        setSize(22).
        setDbName("DATE_FROM").
        setLabel("PCMWALLOCATEEMPLOYEESDATEFROM: Date From");
        
        itemblk1.addField("ITEM1_DATE_TO","Datetime").
        setSize(22).
        setDbName("DATE_TO").
        setLabel("PCMWALLOCATEEMPLOYEESDATETO: Date To");
        
        itemblk1.addField("ITEM1_WO_NO","Number","#").
        setMandatory().
        setHidden().
        setDbName("WO_NO");
        
        itemblk1.setView("WO_ROLE_ALLOCATIONS");
        itemblk1.defineCommand("WORK_ORDER_ROLE_API","");
        itemset1 = itemblk1.getASPRowSet();

        itembar1 = mgr.newASPCommandBar(itemblk1);
        itembar1.enableCommand(itembar1.FIND);
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
        
        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setWrap();
        itemtbl1.enableRowSelect();
        itembar1.enableMultirowAction();
        itembar1.addCustomCommand("clearEmployeeAllocation",mgr.translate("PCMWALLOCATEEMPLOYEESCLEAREMPALLOC: Clear Employee Allocation..."));          
        itembar1.addCustomCommand("workOrdersForCraftsEmp1",mgr.translate("PCMWALLOCATEEMPLOYEESWOFORCRAFTEMP1: Work Orders for Craft/Employee..."));          
        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
        itemlay1.setDialogColumns(3);  

  
           
 }


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
	   return "PCMWALLOCATEEMPLOYEES1: Allocate Employees for Work Order - "+woNo+" ,"+woDesc+"";
                
	}

	protected String getTitle()
	{
	   return "PCMWALLOCATEEMPLOYEES1: Allocate Employees for Work Order - "+woNo+" ,"+woDesc+"";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
                /*String sOracleFormat = mgr.replace(mgr.getFormatMask("Datetime",true),"mm","MI");
                if (sOracleFormat.indexOf("HH")>-1)
                {
                    if ("HH".equals(sOracleFormat.substring(sOracleFormat.indexOf("HH"),sOracleFormat.indexOf("HH")+2)))
                        sOracleFormat = mgr.replace(sOracleFormat,"HH","HH24");
                }*/

                printHiddenField("ROWVAL","");
                printHiddenField("REFRESHCHILD","FALSE");
		appendDirtyJavaScript("window.name = \"AllocateEmployees\";\n");
                                
               if (bOpenNewWindow)
               {
                  appendDirtyJavaScript("window.open(\"");
                  appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString)); // XSS_Safe ILSOLK 20070709
                  appendDirtyJavaScript("\", \"");
                  appendDirtyJavaScript(newWinHandle);
                  appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars,dependent=yes,width=800,height=550\");\n");  

               }

               if (bCloseWindow)
                {
                   appendDirtyJavaScript("  if (");
                   appendDirtyJavaScript(!mgr.isEmpty(qryStr));
                   appendDirtyJavaScript(")\n");
                   appendDirtyJavaScript("{\n");
                   appendDirtyJavaScript("  window.open('");
                   appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr)); // XSS_Safe ILSOLK 20070709
                   appendDirtyJavaScript("' + \"&EMPLOYEE=1\",'");
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
                }

                  appendToHTML(headbar.showBar());
                  appendToHTML("<table id=\"TBL\" border=0 class=\"BlockLayoutTable\" cellspacing=4 cellpadding=1 width= 100%>\n");
                  appendToHTML("       <tr>\n");
                  appendToHTML("           <td \n");
                  appendToHTML("	           <table id=\"TBL1\" border=0 cellspacing=10 cellpadding=1 >\n");
                  appendToHTML("	           <tr>\n");
                  appendToHTML("              <td align=\"left\" >");
                  appendToHTML(fmt.drawWriteLabel("PCMWALLOCATEEMPLOYEEWONO: Wo No"));
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML(fmt.drawReadOnlyTextField("HEAD_WO_NO",woNo,"readOnly",30));
                  appendToHTML("</td>\n");
                  appendToHTML("              <td width= 51% align=\"left\" >");
                  appendToHTML(fmt.drawWriteLabel("PCMWALLOCATEEMPLOYEESDIRECTIVE: Directive"));
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML(fmt.drawReadOnlyTextField("DIRECTIVE",woDesc,"readOnly",30));
                  appendToHTML("        </td>\n");
                  appendToHTML("        </tr>\n");

                  appendToHTML("	           <tr>\n");
                  appendToHTML("              <td align=\"left\" >");
                  appendToHTML(fmt.drawWriteLabel("PCMWALLOCATEEMPLOYEESDLGOPER: Operation No"));
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML(fmt.drawSelect("HEAD_ROW_NO",rowBuff,rowNo,"onChange='populateChild()'"));
                  appendToHTML("</td>\n");
                  appendToHTML("              <td width= 51% align=\"left\" >");
                  appendToHTML(fmt.drawWriteLabel("PCMWALLOCATEEMPLOYEESDLGDESC: Description"));
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  appendToHTML(fmt.drawReadOnlyTextField("DESCRIPTION",desc,"readOnly",30));
                  appendToHTML("        </td>\n");
                  appendToHTML("        </tr>\n");
                  appendToHTML("        </table>\n");
                  appendToHTML("   </td>\n");
                  appendToHTML("</tr>\n");
                  appendToHTML("</table>\n");
                  appendToHTML(headlay.generateDataPresentation());
                  appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
                  appendToHTML(            "<tr align=\"right\">\n");
                  appendToHTML("<td width=\"100%\" align=\"right\">\n");
                  appendToHTML(fmt.drawSubmit("ADDQUAL",mgr.translate("PCMWALLOCATEEMPLOYEESENGADDQUALIFICATIONS: Additional Qualifications"), ""));
                  appendToHTML("&nbsp;&nbsp;"); 
                  appendToHTML(fmt.drawSubmit("OK",mgr.translate("PCMWALLOCATEEMPLOYEESADDQUALSOK: OK "), "onClick=setSelecRowList()"));
                  appendToHTML("&nbsp;&nbsp;"); 
                  appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWALLOCATEEMPLOYEESENGCONSTDJOBS: Cancel"), ""));
                  appendToHTML("&nbsp;&nbsp;"); 
                  appendToHTML(fmt.drawSubmit("APPLY",mgr.translate("PCMWALLOCATEEMPLOYEESENGADDTOROW:  Apply "), "onClick=setSelecRowList()"));
                  appendToHTML("&nbsp;&nbsp;"); 

                  appendToHTML("<td width=\"100%\" align=\"right\">\n");
                  appendToHTML(fmt.drawSubmit("SEARCHRESOURCE",mgr.translate("PCMWALLOCATEEMPLOYEESENGSEARCH: Search"),""));
                  appendToHTML("&nbsp;&nbsp;");
                  appendToHTML(fmt.drawHidden("SELLIST",""));
                  appendToHTML(fmt.drawHidden("ALLOCLIST",""));
                  appendToHTML("</td>\n</tr>\n</table>\n");
                  
                  if (headset.countRows() > 0)
                  {
                     appendToHTML(tabs.showTabsInit());
                     if ((tabs.getActiveTab() == 1) && (itemset1.countRows() > 0))
                        appendToHTML(itemlay1.show());
                     if ("TRUE".equals(bItemlayShow))
                     {
                       if (tabs.getActiveTab() == 2) 
                          appendToHTML(itemlay0.show());
                     }
                  }
                  
                  appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		  appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		  appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
                  appendDirtyJavaScript("      f.APPLY.disabled = true;\n");
                  appendDirtyJavaScript("      f.OK.disabled = true;\n");
                
                  appendDirtyJavaScript("function handleButtons(i)\n");
                  appendDirtyJavaScript("{\n");
                  appendDirtyJavaScript("   if (f.DURATION.value == '' || f.DATE_FROM.value == '' || f.DATE_TO.value == '')\n");
                  appendDirtyJavaScript("      f.SEARCHRESOURCE.disabled = true;\n");
                  appendDirtyJavaScript("  else \n");
                  appendDirtyJavaScript("      f.SEARCHRESOURCE.disabled = false;\n");
                  
                  if ("TRUE".equals(bItemlayShow))
                  {
                   appendDirtyJavaScript("   validateAllocate(i);\n"); 
                   appendDirtyJavaScript("  j = ");
                   appendDirtyJavaScript(noOfRows); // XSS_Safe ILSOLK 20070713
                   appendDirtyJavaScript(";\n");
                   appendDirtyJavaScript("	x=0;\n");
                   appendDirtyJavaScript(" for(i=1;i<j+1;i++)\n");
                   appendDirtyJavaScript("{\n");
                   appendDirtyJavaScript(" if (getValue_('ALLOCATE',i) == '1') \n");
                   appendDirtyJavaScript("	x=x+1;\n");
                   appendDirtyJavaScript("}\n");
                   appendDirtyJavaScript(" if (x==0) \n");
                   appendDirtyJavaScript("{\n");
                   appendDirtyJavaScript("      f.APPLY.disabled = true;\n");
                   appendDirtyJavaScript("      f.OK.disabled = true;\n");
                   appendDirtyJavaScript("}\n");
                   appendDirtyJavaScript("  else \n");
                   appendDirtyJavaScript("{\n");
                   appendDirtyJavaScript("   f.APPLY.disabled = false;\n");
                   appendDirtyJavaScript("   f.OK.disabled = false;\n");
                   appendDirtyJavaScript("}\n");
                  }
                  appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function populateChild() {\n");
                appendDirtyJavaScript(" n = f.HEAD_ROW_NO.options[f.HEAD_ROW_NO.selectedIndex].value \n");
                appendDirtyJavaScript(" if (n=='') \n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("      f.SEARCHRESOURCE.disabled = true;\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("  else \n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("   document.form.ROWVAL.value = n;\n");
                appendDirtyJavaScript("   document.form.REFRESHCHILD.value = \"TRUE\";\n");
                appendDirtyJavaScript("   submit();\n");  
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function validateItem1WoNo(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
                appendDirtyJavaScript("	setDirty();\n");
                appendDirtyJavaScript("	if( !checkItem1WoNo(i) ) return;\n");
                appendDirtyJavaScript("	if( getValue_('ITEM1_WO_NO',i)=='' )\n");
                appendDirtyJavaScript("	{\n");
                appendDirtyJavaScript("		getField_('ITEM1_WO_NO',i).value = '';\n");
                appendDirtyJavaScript("	}\n");
                appendDirtyJavaScript("	window.status='Please wait for validation';\n");
                appendDirtyJavaScript("	r = __connect(\n");
                appendDirtyJavaScript("		'");
                appendDirtyJavaScript(mgr.getURL());
                appendDirtyJavaScript("?VALIDATE=ITEM1_WO_NO'\n");
                appendDirtyJavaScript("		+ '&ITEM1_WO_NO=' + URLClientEncode(getValue_('ITEM1_WO_NO',i))\n");
                appendDirtyJavaScript("		);\n");
                appendDirtyJavaScript("	if (Trim(r)=='TRUE')\n");
                appendDirtyJavaScript("      f.ADDHEADER.disabled = true;\n");
                appendDirtyJavaScript("	else\n");
                appendDirtyJavaScript("      f.ADDHEADER.disabled = false;\n");
                appendDirtyJavaScript("	window.status='';\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function validateAllocate(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
                appendDirtyJavaScript("setDirty();\n");
                appendDirtyJavaScript("if( !checkAllocate(i) ) return;\n");
                appendDirtyJavaScript("if( getValue_('ALLOCATEDMANHOURS',i).indexOf('%') != -1) return;\n");
                appendDirtyJavaScript("if( getValue_('ITEM0_DURATION',i).indexOf('%') != -1) return;\n");
                appendDirtyJavaScript("if( getValue_('TOTMANHOURS',i).indexOf('%') != -1) return;\n");
                appendDirtyJavaScript("if( getValue_('ALLOCATE',i).indexOf('%') != -1) return;\n");
                appendDirtyJavaScript("window.status='Please wait for validation';\n");
                appendDirtyJavaScript("	r = __connect(\n");
                appendDirtyJavaScript("		'");
                appendDirtyJavaScript(mgr.getURL());
                appendDirtyJavaScript("?VALIDATE=ALLOCATE'\n");
                appendDirtyJavaScript("+ '&ALLOCATEDMANHOURS=' + URLClientEncode(getValue_('ALLOCATEDMANHOURS',i))\n");
                appendDirtyJavaScript("+ '&ITEM0_DURATION=' + URLClientEncode(getValue_('ITEM0_DURATION',i))\n");
                appendDirtyJavaScript("+ '&TOTMANHOURS=' + URLClientEncode(getValue_('TOTMANHOURS',i))\n");
                appendDirtyJavaScript("+ '&ALLOCATE=' + URLClientEncode(getValue_('ALLOCATE',i))\n");
                appendDirtyJavaScript(");\n");
                appendDirtyJavaScript("window.status='';\n");
                appendDirtyJavaScript("	if (Trim(getValue_('ALLOCATE',i))==1)\n");
                appendDirtyJavaScript(" getField_('ITEM0_DURATION',i).disabled = true;\n");
                appendDirtyJavaScript("else\n");
                appendDirtyJavaScript(" getField_('ITEM0_DURATION',i).disabled = false;\n");
                appendDirtyJavaScript("if( checkStatus_(r,'ALLOCATE',i,'Allocate') )\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("assignValue_('ALLOCATEDMANHOURS',i,0);\n");
                appendDirtyJavaScript("assignValue_('REMAININGMANHOURS',i,1);\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function lovMaintOrgContract(i,params)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if(params) param = params;\n");
                appendDirtyJavaScript("	else param = '';\n");
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript(" if (document.form.COMPANY.value != '') \n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('MAINT_ORG_CONTRACT',i).indexOf('%') !=-1)? getValue_('MAINT_ORG_CONTRACT',i):'';\n");
                appendDirtyJavaScript("                  openLOVWindow('MAINT_ORG_CONTRACT',i,\n");
                appendDirtyJavaScript("'");
                appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Maint.Org.+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('MAINT_ORG_CONTRACT',i))\n");
                appendDirtyJavaScript("+ '&MAINT_ORG_CONTRACT=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript(",550,500,'validateMaintOrgContract');\n");
                appendDirtyJavaScript("}\n");


                appendDirtyJavaScript("function lovTeamContract(i,params)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if(params) param = params;\n");
                appendDirtyJavaScript("	else param = '';\n");
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript(" if (document.form.COMPANY.value != '') \n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('TEAM_CONTRACT',i):'';\n");
                appendDirtyJavaScript("                  openLOVWindow('TEAM_CONTRACT',i,\n");
                appendDirtyJavaScript("'");
                appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_CONTRACT',i))\n");
                appendDirtyJavaScript("+ '&TEAM_CONTRACT=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript(",550,500,'validateTeamContract');\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function lovTeamId(i,params)"); 
                appendDirtyJavaScript("{"); 
                appendDirtyJavaScript("	if(params) param = params;\n"); 
                appendDirtyJavaScript("	else param = '';\n"); 
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript("	whereCond2 = '';\n");
                appendDirtyJavaScript(" if(document.form.TEAM_CONTRACT.value != '')\n");
                appendDirtyJavaScript("		whereCond1 = \"CONTRACT = '\" +URLClientEncode(document.form.TEAM_CONTRACT.value)+\"' \";\n"); 
                appendDirtyJavaScript(" else\n");
                appendDirtyJavaScript("		whereCond1 = \"CONTRACT IS NOT NULL \";\n"); 
                appendDirtyJavaScript(" if(document.form.COMPANY.value != '')\n");
                appendDirtyJavaScript(" if( whereCond1=='')\n");
                appendDirtyJavaScript("		whereCond1 = \"COMPANY = '\" +URLClientEncode(document.form.COMPANY.value)+\"' \";\n"); 
                appendDirtyJavaScript(" else\n");
                appendDirtyJavaScript("		whereCond1 += \" AND COMPANY = '\" +URLClientEncode(document.form.COMPANY.value)+\"' \";\n"); 
                appendDirtyJavaScript(" if( whereCond1 !='')\n");
                appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
                appendDirtyJavaScript(" whereCond1 += \" nvl(to_date(to_char(to_date('\" +URLClientEncode(document.form.DATE_FROM_MASKED.value)+\"','" +getDateTimeFormat("SQL") + "'),\'YYYYMMDD\'),\'YYYYMMDD\'),to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\')) BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
                appendDirtyJavaScript(" if(document.form.MAINT_ORG.value != '')\n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript(" if( whereCond2=='')\n");
                appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.MAINT_ORG.value)+\"' \";\n"); 
                appendDirtyJavaScript(" else \n");
                appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.MAINT_ORG.value)+\"' \";\n"); 
                appendDirtyJavaScript(" }\n");
                appendDirtyJavaScript(" if(document.form.MAINT_ORG_CONTRACT.value != '')\n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript(" if(whereCond2=='' )\n");
                appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.MAINT_ORG_CONTRACT.value)+\"' \";\n"); 
                appendDirtyJavaScript(" else \n");
                appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.MAINT_ORG_CONTRACT.value)+\"' \";\n"); 
                appendDirtyJavaScript(" }\n");
                appendDirtyJavaScript(" if(whereCond2 !='' )\n");
                appendDirtyJavaScript("     {\n");
                appendDirtyJavaScript("        if(whereCond1 !='' )\n");
                appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
                appendDirtyJavaScript("        if(document.form.ROLE_CODE.value == '' )\n");
                appendDirtyJavaScript("	           whereCond1 += \" TEAM_ID IN (SELECT TEAM_ID FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
                appendDirtyJavaScript("        else \n");
                appendDirtyJavaScript("	           whereCond1 += \" (TEAM_ID,1) IN (SELECT TEAM_ID,ROLE FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
                appendDirtyJavaScript("     }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n"); 
                appendDirtyJavaScript("	var key_value = (getValue_('TEAM_ID',i).indexOf('%') !=-1)? getValue_('TEAM_ID',i):'';\n"); 
                appendDirtyJavaScript("	openLOVWindow('TEAM_ID',i,\n"); 
                appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_TEAM&__FIELD=Team+Id&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''"); 
                appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_ID',i))"); 
                appendDirtyJavaScript("		+ '&TEAM_ID=' + URLClientEncode(key_value)"); 
                appendDirtyJavaScript("		,550,500,'validateTeamId');\n"); 
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function lovRoleCode(i,params)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if(params) param = params;\n");
                appendDirtyJavaScript("	else param = '';\n");
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript("	whereCond2 = '';\n");
                appendDirtyJavaScript(" if (document.form.COMPANY.value != '') \n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n"); 
                appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n"); 
                appendDirtyJavaScript(" }\n");
                appendDirtyJavaScript(" if (document.form.TEAM_ID.value != '')\n");
                appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT ROLE FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.TEAM_CONTRACT.value) +\"' AND \" +whereCond2 +\")\";\n"); 
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
                appendDirtyJavaScript("                  openLOVWindow('ROLE_CODE',i,\n");
                appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
                appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
                appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('MAINT_ORG_CONTRACT',i))\n");       
                appendDirtyJavaScript("       	             ,550,500,'validateRoleCode');\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function lovMaintOrg(i,params)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if(params) param = params;\n");
                appendDirtyJavaScript("	else param = '';\n");
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript("	whereCond2 = '';\n");
                appendDirtyJavaScript("    if (document.form.MAINT_ORG_CONTRACT.value != '') \n");
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.MAINT_ORG_CONTRACT.value)+\"' \";\n");  
                appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.MAINT_ORG_CONTRACT.value)+\"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("    else\n");
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n"); 
                appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n");         //appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript(" }\n");
                appendDirtyJavaScript(" if (document.form.TEAM_ID.value != '')\n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript(" if(whereCond1 != '' )\n");
                appendDirtyJavaScript("	           whereCond1 += \" AND \";\n");
                appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' \";\n");
                appendDirtyJavaScript(" if(whereCond2 != '' )\n");
                appendDirtyJavaScript("	           whereCond1 += \" AND \" +whereCond2 +\" \";\n"); 
                appendDirtyJavaScript("	whereCond1 += \")\";\n"); 
                appendDirtyJavaScript(" }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('MAINT_ORG',i).indexOf('%') !=-1)? getValue_('MAINT_ORG',i):'';\n");
                appendDirtyJavaScript("                  openLOVWindow('MAINT_ORG',i,\n");
                appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maint.+Org.&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('MAINT_ORG',i))\n"); 
                appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
                appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('MAINT_ORG_CONTRACT',i))\n");       
                appendDirtyJavaScript("       	             ,550,500,'validateMaintOrg');\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function validateMaintOrg(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
                appendDirtyJavaScript("	setDirty();\n");
                appendDirtyJavaScript("	if( !checkMaintOrg(i) ) return;\n");
                appendDirtyJavaScript("	window.status='Please wait for validation';\n");
                appendDirtyJavaScript("	r = __connect(\n");
                appendDirtyJavaScript("		'");
                appendDirtyJavaScript(mgr.getURL());
                appendDirtyJavaScript("?VALIDATE=MAINT_ORG'\n");
                appendDirtyJavaScript("		+ '&MAINT_ORG=' + URLClientEncode(getValue_('MAINT_ORG',i))\n");
                appendDirtyJavaScript("		+ '&MAINT_ORG_CONTRACT=' + URLClientEncode(getValue_('MAINT_ORG_CONTRACT',i))\n");
                appendDirtyJavaScript("		);\n");
                appendDirtyJavaScript("	window.status='';\n");
                appendDirtyJavaScript("	if( checkStatus_(r,'MAINT_ORG',i,'Maintenance Organization') )\n");
                appendDirtyJavaScript("	{\n");
                appendDirtyJavaScript("		assignValue_('MAINT_ORG',i,0);\n");
                appendDirtyJavaScript("		assignValue_('MAINT_ORG_CONTRACT',i,1);\n");
                appendDirtyJavaScript("	}\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function setSelecRowList(i)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	document.form.SELLIST.value = \"\"\n");
                appendDirtyJavaScript("	document.form.ALLOCLIST.value = \"\"\n");
                appendDirtyJavaScript("  j = ");
                appendDirtyJavaScript(noOfRows); // XSS_Safe ILSOLK 20070713
                appendDirtyJavaScript(";\n");
                appendDirtyJavaScript("      for(i=1;i<j+1;i++)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	document.form.SELLIST.value += getValue_('ITEM0_DURATION',i) + '~';\n");
                appendDirtyJavaScript("	document.form.ALLOCLIST.value += getValue_('ALLOCATE',i) + '~';\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}\n");

             }
}
