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
*  File        : FreeTimeSearchEngine.java 
*  Created     : 041028  ARWILK  (AMEC500 - Search Engine)
*  Modified    :
*  041216  ARWILK  Modified help options of dlg AddToWo and dlg CreateWorkOrder.
*  041231  ARWILK  Call 120980.
*  050216  NIJALK  Modified createWo(),AddToWo(),printContents() & added connectTnF() to
*                  call dlg ConnectTnF.
*  050218  NIJALK  Call 122078: Modified LOV and validation to COMPETENCE.
*  050218  NIJALK  Call 122071: Renamed field "Order No" to "Sequence".
*  050218  NIJALK  Call 122069: Changed the field order in Search parameters & Result Details.
*  050316  JAPALK  Added Standard Job Connecting functionality.
*  050511  JAPALK  Call ID 123866.Set the default Resource type as Employee.
*  050512  JAPALK  Call ID 123867. Set Wo site when creating WO from search result.
*  050616  JAPALK  Call ID 124510. Set Latest_Date_time Using System parameter.
*  050715  CHODLK  Added create handover wo via rp functionality for CALLC [AMEC101I].
*  050805  THWILK  Added required functionality under AMEC114: Search for and Allocate Employees.
*  050809  THWILK  Added some missing functionality under AMEC114: Search for and Allocate Employees.
*  050920  THWILK  Modified functionality relating to Add to/Modify/Create work order RMBs.
*  051228  NIJALK  Call 130002: Modified methods run(),setHeadData(),additionalQualifications().
*  060126  THWILK  Corrected localization errors.
*  060628  NIJALK  Bug 58872, Modified validations to CONTRACT,COMPETENCE. Added validations to ORG_CODE,COMPETENCE_GROUP,
*                  TOOL_FACILITY_TYPE,TOOL_FACILITY_ID,ITEM1_ORG_CODE,ITEM1_ROLE_GROUP,ITEM1_ROLE_CODE,
*                  ITEM1_TOOL_FACILITY_TYPE,ITEM1_RESOURCE_ID. Modified preDefine(), adjust(), printContents().
*  060706  NIJALK  Bug 58872, Added java script functions to validate fields CONTRACT,COMPETENCE,ORG_CODE,COMPETENCE_GROUP,
*                  TOOL_FACILITY_TYPE,TOOL_FACILITY_ID,ITEM1_ORG_CODE,ITEM1_ROLE_GROUP,ITEM1_ROLE_CODE,
*                  ITEM1_TOOL_FACILITY_TYPE,ITEM1_RESOURCE_ID. 
*  060713  NIJALK  Bug 58872, Modified LOV properties for ITEM1_ROLE_CODE,COMPETENCE,ITEM1_RESOURCE_ID.
*  060725  AMNILK  Merged Bug ID: 58872.
*  060821  DIAMLK  Bug 58216, Eliminated SQL Injection security vulnerability.
*  060905  AMDILK  Merged with the Bug Id 58216
*  070507  AMDILK  Call ID 143805: Modified run(), newRow() to fetch the calling formname
*  070620  AMDILK  Call Id 146302: Set the correct default values when the res.type is 'Tools n facilities'
*  070622  AMDILK  Call ID 146122, 146123: Correct the functionality of the checkbox "Add to header" 
*  070626  AMDILK  Call Id 146122: Disable the check boz "Add to Header" when the resource type is "Tools n Facilities"
*  070711  ILSOLK    Eliminated XSS.
*  070831  ASSALK  Call ID 148009. Modified createWorkOrder(). Set values for maint org and reported by fields.
*  070911  ASSALK  Call ID 148640. Modified setHeadData().
*  070911  ASSALK  Call ID 148642, 148643. Modified printContents(), preDefine().
*  070913  ASSALK  Call ID 148641, Modified refreshCombineLines(),addNewRow(),searchResource(),setHeadData().
*  070918  ASSALK  Call ID 148640, Modified lineAddQualifications().
*  080305  NIJALK  Bug 71866, Modified searchResource(),adjust(), run() and preDefine().
*  080421  NIJALK  Bug 71861, Added new Button 'New Search'.
*  080424  NIJALK  Bug 71867, Modified validations of Craft, Craft Group, Tool Facility ID and Tool Facility Type
*                  in both headder and child table. Modified validate(), preDefine() and printcontents().
*  080505  NIJALK  Bug 71867, Modified LOV User Where of Craft fields.
*  081031  NIJALK  Bug 71865 ,Modified printContents(),searchResource(),adjust(), run() and preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.math.*;
import java.util.*;

public class FreeTimeSearchEngine extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.FreeTimeSearchEngine");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPForm frm;
    private ASPHTMLFormatter fmt;
    private ASPContext ctx;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock itemblk1;
    private ASPRowSet itemset1;
    private ASPCommandBar itembar1;
    private ASPTable itemtbl1;
    private ASPBlockLayout itemlay1;

    private ASPBlock itemblk2;
    private ASPRowSet itemset2;
    private ASPCommandBar itembar2;
    private ASPTable itemtbl2;
    private ASPBlockLayout itemlay2;

    private ASPBlock itemblk3;
    private ASPRowSet itemset3;
    private ASPCommandBar itembar3;
    private ASPTable itemtbl3;
    private ASPBlockLayout itemlay3;

    private ASPBlock itemblk4;
    private ASPRowSet itemset4;
    private ASPCommandBar itembar4;
    private ASPTable itemtbl4;
    private ASPBlockLayout itemlay4;

    private ASPBlock itemblk5;
    private ASPRowSet itemset5;
    private ASPCommandBar itembar5;
    private ASPTable itemtbl5;
    private ASPBlockLayout itemlay5;

    //======= Start - CALLC =========//
    private ASPBlock itemblk6;
    private ASPRowSet itemset6;
    private ASPCommandBar itembar6;
    private ASPTable itemtbl6;
    private ASPBlockLayout itemlay6;
    //======= End - CALLC =========//

    private ASPBlock itemblk7;
    private ASPRowSet itemset7;
    private ASPCommandBar itembar7;
    private ASPTable itemtbl7;
    private ASPBlockLayout itemlay7;

    private ASPTabContainer tabs;

    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String val;
    private ASPCommand cmd;
    private ASPBuffer data;
    private ASPBuffer buf;
    private String sSearchSeq;
    private String sDlgName;
    private String sExecutionInfo;
    private String sConfirmationText;
    private String sConfirmedUrlString;
    private String sConfirmedNewWinHandle;
    private String sResourceType;
    private boolean bDisableSiteField;
    private boolean bLineDataTransfered;
    private String sLineResourceType;
    private String sOriginalSite;
    private int iCombinedNoOfRows;
    private String sTranslatedEmpType;
    private String sTranslatedToolType;
    private String sWoNo;
    private String sRowNo;
    private String sObjId;
    private String sObjVersion;
    private boolean bCombinedSearchPerformed;
    private String sSimpleSearchSeq;
    private boolean bCallingFromHead;
    private String sQual;

    private String sContract;

    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    private boolean bCombineLineExist;
    private boolean bneedToSetDefaultResource=true;
    private String sSysParamNofDays;
    private String sCallFormName;
    //Bug 71866, Start
    private String sShowAll;
    //Bug 71866, End
    //Bug 71861, Start
    private String initialQryStr;
    //Bug 71861, End

    //=====Start - CALLC====//
    private boolean bFromCallc = false;
    private String  sCaseId;
    private String  sTaskId;
    private String  sCaseLocalId;
    private String  sHandoverRef;
    //=====End - CALLC======//


    //===============================================================
    // Construction 
    //===============================================================
    public FreeTimeSearchEngine(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        frm = mgr.getASPForm();
        fmt = mgr.newASPHTMLFormatter();
        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        
        sSimpleSearchSeq          = ctx.readValue("CTXSIMPLESSEQ", "");
        sSearchSeq                = ctx.readValue("CTXSEARCHSEQ", "");
        bDisableSiteField         = ctx.readFlag("CTXDISABLESITEFIELD", false);
        bLineDataTransfered       = ctx.readFlag("CTXBLINEDATATRANSFERED", false);
        sOriginalSite             = ctx.readValue("CTXORIGINALSITE", "");
        iCombinedNoOfRows         = ctx.readNumber("CTXCOMBNOOFROWS", 0);
        sTranslatedEmpType        = ctx.readValue("CTXTRANSEMP", "");
        sTranslatedToolType       = ctx.readValue("CTXTRANSTOOL", "");
        sWoNo                     = ctx.readValue("CTXSWONO", "");
        sRowNo                    = ctx.readValue("CTXSROWNO", "");
        sObjId                    = ctx.readValue("CTXSOBJID", "");
        sObjVersion               = ctx.readValue("CTXSOBJVERSION", "");
        sLineResourceType         = ctx.readValue("CTXSLINERESOURCETYPE", "");
        bCombinedSearchPerformed  = ctx.readFlag("CTXBCOMBSEARCHPER",false);
        //=========Start CALLC===========/
        bFromCallc                = ctx.readFlag("CTXFROMCALLC",false);
        bCallingFromHead          = ctx.readFlag("CTXCALLHEAD",false);

        sCaseId                   = ctx.readValue("CTXCALLC_CASEID","");
        sTaskId                   = ctx.readValue("CTXCALLC_TASKID","");
        sCaseLocalId              = ctx.readValue("CTXCALLC_CASELOCALID","");
        sHandoverRef              = ctx.readValue("CTXCALLC_HANDOVRREF","");
        sQual                     = ctx.readValue("QUAL","FALSE");
        //=========End CALLC ============/

        sCallFormName             = ctx.readValue("FRMNAME","");
        //Bug 71866, Start
        sShowAll                  = ctx.readValue("CTXSHOWALL","");
        //Bug 71866, End
        //Bug 71861, Start
        initialQryStr             = ctx.readValue("INITIALQRYSTR","");
        //Bug 71861, End

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.buttonPressed("SEARCHRESOURCE"))
            searchResource();
        else if (mgr.buttonPressed("ADDTOROW"))
           addNewRow();
        //Bug 71861, Start
        else if (mgr.buttonPressed("NEWSEARCH"))
            newSearch();
        //Bug 71861, End
        
        else if (mgr.buttonPressed("OKCREATE"))
        {
            //==========Start -CALLC============//

            if (bFromCallc)
                createWoViaRP();
            else
                createWo();

            //==========End -CALLC============//

        }

        else if (mgr.buttonPressed("OKADD"))
            addToWo();
        else if (mgr.buttonPressed("OKMODIFY"))
            modifyWo();
        else if (mgr.buttonPressed("CANCELCREATE") || mgr.buttonPressed("CANCELADD") || mgr.buttonPressed("CANCELMODIFY"))
            cancelWoDlg();
        else if (mgr.buttonPressed("CONSTDJOBS"))
            connectStandardJobs();
        else if (mgr.buttonPressed("ADDQUAL"))
           additionalQualifications();
        
        else if (mgr.buttonPressed("OKSTDJOB"))
            refreshCombineLines();
        else if (mgr.dataTransfered())
        {
	   if ("HEAD".equals(mgr.getQueryStringValue("CALLING_FROM")))
	      bCallingFromHead = true;
           else 
	      bCallingFromHead = false;
	   setHeadData();
           refreshChild();
        }
	else
	   newRow();

        //==========Start -CALLC============//
        /* query string field FROM_CALLC is used to indicate that the page is requested from 'CALLC'*/

        if (!mgr.isEmpty(mgr.getQueryStringValue("FROM_CALLC")))
        {
            bFromCallc = true;

            initCallcData(mgr);
        }
        //==========End -CALLC============//


        adjust();

        tabs.saveActiveTab();
        ctx.writeValue("CTXSIMPLESSEQ", sSimpleSearchSeq);
        ctx.writeValue("CTXSEARCHSEQ", sSearchSeq);
        ctx.writeFlag("CTXDISABLESITEFIELD", bDisableSiteField);
        ctx.writeFlag("CTXBLINEDATATRANSFERED", bLineDataTransfered);
        ctx.writeValue("CTXORIGINALSITE", sOriginalSite);
        ctx.writeNumber("CTXCOMBNOOFROWS", iCombinedNoOfRows);
        ctx.writeValue("CTXTRANSEMP", sTranslatedEmpType);
        ctx.writeValue("CTXTRANSTOOL", sTranslatedToolType);
        ctx.writeValue("CTXSWONO", sWoNo);
        ctx.writeValue("CTXSROWNO", sRowNo);
        ctx.writeValue("CTXSOBJID", sObjId);
        ctx.writeValue("CTXSOBJVERSION", sObjVersion);
        ctx.writeValue("CTXSLINERESOURCETYPE", sLineResourceType);
        ctx.writeFlag("CTXBCOMBSEARCHPER", bCombinedSearchPerformed);
        ctx.writeFlag("CTXCALLHEAD", bCallingFromHead);
        
        //=========Start CALLC===========/
        ctx.writeFlag("CTXFROMCALLC", bFromCallc);

        ctx.writeValue("CTXCALLC_CASEID", sCaseId);
        ctx.writeValue("CTXCALLC_TASKID", sTaskId);
        ctx.writeValue("CTXCALLC_CASELOCALID", sCaseLocalId);
        ctx.writeValue("CTXCALLC_HANDOVRREF", sHandoverRef);
        ctx.writeValue("QUAL", sQual);
        //=========End CALLC ============/

        ctx.writeValue("FRMNAME", sCallFormName);
        //Bug 71866, Start
        ctx.writeValue("CTXSHOWALL", sShowAll);
        //Bug 71866, End
        //Bug 71861, Start
        ctx.writeValue("INITIALQRYSTR", initialQryStr);
        //Bug 71861, End
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        String txt;

        val = mgr.readValue("VALIDATE");

        if ("CONTRACT".equals(val))
        {
            trans.clear();

            cmd = trans.addCustomCommand("SITEEXIST","Site_API.Exist");
            cmd.addParameter("CONTRACT");

            cmd = trans.addCustomFunction("GETFNDUSER","Fnd_Session_API.Get_Fnd_User","FND_USER");

            cmd = trans.addCustomCommand("USERALLOWEDSITE","User_Allowed_Site_API.Exist");
            cmd.addReference("FND_USER","GETFNDUSER/DATA");
            cmd.addParameter("CONTRACT");
            
	    cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT");

            cmd = trans.addCustomFunction("GETSITEDESC","Site_API.Get_Description","SITE_DESCRIPTION");
            cmd.addParameter("CONTRACT");

            cmd = trans.addCustomFunction("GETEMPNAME","Employee_API.Get_Name","EMPLOYEE_NAME");
            cmd.addReference("COMPANY","GETCOMP/DATA");
            cmd.addParameter("EMPLOYEE");

            trans = mgr.validate(trans);

            String sCompany = trans.getValue("GETCOMP/DATA/COMPANY");
            String sSiteDesc = trans.getValue("GETSITEDESC/DATA/SITE_DESCRIPTION");
            String sEmpName = trans.getValue("GETEMPNAME/DATA/EMPLOYEE_NAME");

            txt = (mgr.isEmpty(sCompany) ? "" : (sCompany)) + "^" +
                  (mgr.isEmpty(sSiteDesc) ? "" : (sSiteDesc)) + "^" +
                  (mgr.isEmpty(sEmpName) ? "" : (sEmpName)) + "^" ;

            mgr.responseWrite(txt);
        }
        if ("ORG_CODE".equals(val))
        {
            trans.clear();

            cmd = trans.addCustomCommand("ORGCODEEXIST","Organization_API.Exist");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");

            cmd = trans.addCustomFunction("GETORGDESC","Organization_API.Get_Description","ORG_DESCRIPTION");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.validate(trans);

            String sOrgDesc = trans.getValue("GETORGDESC/DATA/ORG_DESCRIPTION");

            txt = (mgr.isEmpty(sOrgDesc) ? "" : (sOrgDesc)) + "^" ;

            mgr.responseWrite(txt);
        }

        else if ("EMPLOYEE".equals(val))
        {
            trans.clear();

            cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT");

            cmd = trans.addCustomFunction("GETEMPNAME","Employee_API.Get_Name","EMPLOYEE_NAME");
            cmd.addReference("COMPANY","GETCOMP/DATA");
            cmd.addParameter("EMPLOYEE");

            trans = mgr.validate(trans);

            String sEmpName = trans.getValue("GETEMPNAME/DATA/EMPLOYEE_NAME");

            txt = (mgr.isEmpty(sEmpName) ? "" : (sEmpName)) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("EARLIEST_DATE_TIME".equals(val) || "LATEST_DATE_TIME".equals(val) || "EXECUTION_TIME".equals(val))
        {
            trans.clear();

            if (!mgr.isEmpty(mgr.readValue("EARLIEST_DATE_TIME")) && !mgr.isEmpty(mgr.readValue("LATEST_DATE_TIME")) && !mgr.isEmpty(mgr.readValue("EXECUTION_TIME")))
            {
                cmd = trans.addCustomFunction("GETVALIDTIME","Resource_Booking_Util_API.Get_Valid_To_Time","LATEST_DATE_TIME");
                cmd.addParameter("EARLIEST_DATE_TIME");
                cmd.addParameter("LATEST_DATE_TIME");
                cmd.addParameter("EXECUTION_TIME");

                trans = mgr.validate(trans);

                String sNewToTime = trans.getBuffer("GETVALIDTIME/DATA").getFieldValue("LATEST_DATE_TIME");
                txt = (mgr.isEmpty(sNewToTime) ? "" : (sNewToTime)) + "^" ;
            }
            else
                txt = mgr.readValue("LATEST_DATE_TIME");

            mgr.responseWrite(txt);
        }

        else if ("COMPETENCE_GROUP".equals(val))
        {
            //Bug 71867, Start
            String sCompetenceGrp = mgr.readValue("COMPETENCE_GROUP","");
            String sCompetence = mgr.readValue("COMPETENCE","");
            String sRoleDesc = "";

            trans.clear();

            cmd = trans.addCustomFunction("GETROLEGRP","Role_To_Site_API.Get_Belongs_To_Role","COMPETENCE_GROUP");
            cmd.addParameter("COMPETENCE");
            cmd.addParameter("CONTRACT");

            cmd = trans.addCustomFunction("GETROLEDESC","Role_API.Get_Description","COMPETENCE_DESCRIPTION");
            cmd.addParameter("COMPETENCE");

            trans = mgr.validate(trans);

            if (!(mgr.isEmpty(sCompetenceGrp) || sCompetenceGrp.equals(trans.getValue("GETROLEGRP/DATA/COMPETENCE_GROUP"))))
            {
                sCompetence = "";
                sRoleDesc = "";
            }
            else
                sRoleDesc = trans.getValue("GETROLEDESC/DATA/COMPETENCE_DESCRIPTION");
            //Bug 71867, End

            trans.clear();

            cmd = trans.addCustomCommand("ROLEGRPEXIST","Role_To_Site_API.Craft_Group_Exist");
            cmd.addParameter("COMPETENCE_GROUP");
            cmd.addParameter("CONTRACT");

            //Bug 71867, Start, Modified parameters
            cmd = trans.addCustomCommand("ISROLEINGRP","Role_To_Site_API.Check_Craft_In_Group");
            cmd.addParameter("COMPETENCE",sCompetence);
            cmd.addParameter("COMPETENCE_GROUP");
            cmd.addParameter("CONTRACT");
            //Bug 71867, End

            cmd = trans.addCustomFunction("GETCOMPETENCEDESC","Role_API.Get_Description","COMPETENCE_GRP_DESCRIPTION");
            cmd.addParameter("COMPETENCE_GROUP");

            trans = mgr.validate(trans);

            String sCompetenceDesc = trans.getValue("GETCOMPETENCEDESC/DATA/COMPETENCE_GRP_DESCRIPTION");

            //Bug 71867, Start
            txt = (mgr.isEmpty(sCompetenceDesc) ? "" : (sCompetenceDesc)) + "^" +
                  (mgr.isEmpty(sCompetence) ? "" : (sCompetence)) + "^" +
                  (mgr.isEmpty(sRoleDesc) ? "" : (sRoleDesc)) + "^" ;
            //Bug 71867, End

            mgr.responseWrite(txt);
        }

        else if ("COMPETENCE".equals(val))
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[2];
            String sCompetence = "";
            //Bug 71867, Start
            String sCompetenceGrp = mgr.readValue("COMPETENCE_GROUP","");
            //Bug 71867, End

            String new_competence = mgr.readValue("COMPETENCE","");

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

            cmd = trans.addCustomCommand("ROLEEXIST","Role_API.Exist");
            cmd.addParameter("COMPETENCE",sCompetence);

            cmd = trans.addCustomCommand("ROLEGRPEXIST","Role_To_Site_API.Check_Role_Exist");
            cmd.addParameter("COMPETENCE",sCompetence);
            cmd.addParameter("CONTRACT");

            cmd = trans.addCustomCommand("ISROLEINGRP","Role_To_Site_API.Check_Craft_In_Group");
            cmd.addParameter("COMPETENCE",sCompetence);
            cmd.addParameter("COMPETENCE_GROUP");
            cmd.addParameter("CONTRACT");

            cmd = trans.addCustomFunction("GETDESCR","Role_API.Get_Description","COMPETENCE_DESCRIPTION");
            cmd.addParameter("COMPETENCE",sCompetence);

            //Bug 71867, Start
            if (mgr.isEmpty(sCompetenceGrp))
            {
                cmd = trans.addCustomFunction("GETROLEGRP","Role_To_Site_API.Get_Belongs_To_Role","COMPETENCE_GROUP");
                cmd.addParameter("COMPETENCE",sCompetence);
                cmd.addParameter("CONTRACT");

                cmd = trans.addCustomFunction("GETROLEGRPDESC","Role_API.Get_Description","COMPETENCE_GRP_DESCRIPTION");
                cmd.addReference("COMPETENCE_GROUP","GETROLEGRP/DATA");
            }
            else
            {
                cmd = trans.addCustomFunction("GETROLEGRPDESC","Role_API.Get_Description","COMPETENCE_GRP_DESCRIPTION");
                cmd.addParameter("COMPETENCE_GROUP",sCompetenceGrp);
            }
            //Bug 71867, End

            trans = mgr.validate(trans);
            String sDescr = trans.getValue("GETDESCR/DATA/COMPETENCE_DESCRIPTION");
            //Bug 71867, Start
            if (mgr.isEmpty(sCompetenceGrp))
                sCompetenceGrp = trans.getValue("GETROLEGRP/DATA/COMPETENCE_GROUP");

            String sCompetenceGrpDesc = trans.getValue("GETROLEGRPDESC/DATA/COMPETENCE_GRP_DESCRIPTION");

            txt = (mgr.isEmpty(sCompetence) ? "" : (sCompetence)) + "^" +
                  (mgr.isEmpty(sDescr) ? "" : (sDescr)) + "^" +
                  (mgr.isEmpty(sCompetenceGrp) ? "" : (sCompetenceGrp)) + "^" +
                  (mgr.isEmpty(sCompetenceGrpDesc) ? "" : (sCompetenceGrpDesc)) + "^";
            //Bug 71867, End

            mgr.responseWrite(txt);
        }
         else if ("WO_NO".equals(val))
           {
            trans.clear();
           cmd = trans.addCustomFunction("ADDHEAD", "Active_Separate_API.Has_Role", "ADDHEADER" );    
           cmd.addParameter("WO_NO");
           trans = mgr.validate(trans);   
           String addHead  = trans.getValue("ADDHEAD/DATA/ADDHEADER");
           
           txt =  (mgr.isEmpty(addHead) ? "" : (addHead));
           mgr.responseWrite(txt);

         } 

         else if ("TOOL_FACILITY_TYPE".equals(val))
         {
             //Bug 71867, Start
             String sToolFacilityType = mgr.readValue("TOOL_FACILITY_TYPE","");
             String sToolFacility = mgr.readValue("TOOL_FACILITY_ID","");
             String sToolFacilityDesc = "";

             trans.clear();

             cmd = trans.addCustomFunction("GETTFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","TOOL_FACILITY_TYPE");
             cmd.addParameter("TOOL_FACILITY_ID");

             cmd = trans.addCustomFunction("GETDESCR","Tool_Facility_API.Get_Tool_Facility_Description","TOOL_FACILITY_DESC");
             cmd.addParameter("TOOL_FACILITY_ID");

             trans = mgr.validate(trans);

             if (!(mgr.isEmpty(sToolFacilityType) || sToolFacilityType.equals(trans.getValue("GETTFTYPE/DATA/TOOL_FACILITY_TYPE"))))
             {
                 sToolFacility = "";
                 sToolFacilityDesc = "";
             }
             else
                 sToolFacilityDesc = trans.getValue("GETDESCR/DATA/TOOL_FACILITY_DESC");
             //Bug 71867, End

             trans.clear();

             cmd = trans.addCustomCommand("TFTYPEEXIST","Tool_Facility_Type_API.Exist");
             cmd.addParameter("TOOL_FACILITY_TYPE");

             //Bug 71867, Start
             cmd = trans.addCustomCommand("TFEXISTINTYPE","Tool_Facility_API.Check_Tool_Facility_Type");
             cmd.addParameter("TOOL_FACILITY_ID",sToolFacility);
             cmd.addParameter("TOOL_FACILITY_TYPE");
             //Bug 71867, End

             cmd = trans.addCustomFunction("GETDESCR","Tool_Facility_Type_API.Get_Type_Description","TOOL_FACILITY_TYPE_DESC");
             cmd.addParameter("TOOL_FACILITY_TYPE");

             trans = mgr.validate(trans);
             String sDescr = trans.getValue("GETDESCR/DATA/TOOL_FACILITY_TYPE_DESC");

             //Bug 71867, Start
             txt = (mgr.isEmpty(sDescr) ? "" : (sDescr)) + "^" +
                   (mgr.isEmpty(sToolFacility) ? "" : (sToolFacility)) + "^" +
                   (mgr.isEmpty(sToolFacilityDesc) ? "" : (sToolFacilityDesc)) + "^" ;
             //Bug 71867, End

             mgr.responseWrite(txt);
         }
         else if ("TOOL_FACILITY_ID".equals(val))
         {
             //Bug 71867, Start
             String sToolFacilityType = mgr.readValue("TOOL_FACILITY_TYPE","");
             //Bug 71867, End

             trans.clear();

             cmd = trans.addCustomCommand("TFIDEXIST","Tool_Facility_API.Exist");
             cmd.addParameter("TOOL_FACILITY_ID");

             cmd = trans.addCustomCommand("TFEXISTINTYPE","Tool_Facility_API.Check_Tool_Facility_Type");
             cmd.addParameter("TOOL_FACILITY_ID");
             cmd.addParameter("TOOL_FACILITY_TYPE");

             cmd = trans.addCustomCommand("TFEXISTINORG","Connect_Tools_Facilities_API.Check_TF_In_Org");
             cmd.addParameter("TOOL_FACILITY_ID");
             cmd.addParameter("CONTRACT");
             cmd.addParameter("ORG_CODE");

             cmd = trans.addCustomFunction("GETDESCR","Tool_Facility_API.Get_Tool_Facility_Description","TOOL_FACILITY_DESC");
             cmd.addParameter("TOOL_FACILITY_ID");

             //Bug 71867, Start
             if (mgr.isEmpty(sToolFacilityType))
             {
                 cmd = trans.addCustomFunction("GETTFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","TOOL_FACILITY_TYPE");
                 cmd.addParameter("TOOL_FACILITY_ID");

                 cmd = trans.addCustomFunction("GETTFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TOOL_FACILITY_TYPE_DESC");
                 cmd.addReference("TOOL_FACILITY_TYPE","GETTFTYPE/DATA");
             }
             else
             {
                 cmd = trans.addCustomFunction("GETTFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TOOL_FACILITY_TYPE_DESC");
                 cmd.addParameter("TOOL_FACILITY_TYPE",sToolFacilityType);
             }
             //Bug 71867, End

             trans = mgr.validate(trans);
             String sDescr = trans.getValue("GETDESCR/DATA/TOOL_FACILITY_DESC");

             //Bug 71867, Start
             if (mgr.isEmpty(sToolFacilityType))
                 sToolFacilityType = trans.getValue("GETTFTYPE/DATA/TOOL_FACILITY_TYPE");

             String sToolFacilityTypeDesc = trans.getValue("GETTFTYPEDESC/DATA/TOOL_FACILITY_TYPE_DESC"); 

             txt = (mgr.isEmpty(sDescr) ? "" : (sDescr)) + "^" +
                   (mgr.isEmpty(sToolFacilityType) ? "" : (sToolFacilityType)) + "^" +
                   (mgr.isEmpty(sToolFacilityTypeDesc) ? "" : (sToolFacilityTypeDesc)) + "^";
             //Bug 71867, End

             mgr.responseWrite(txt);
         }
         else if ("ITEM1_ORG_CODE".equals(val))
         {
             trans.clear();

             cmd = trans.addCustomCommand("ORGCODEEXIST","Organization_API.Exist");
             cmd.addParameter("ITEM1_CONTRACT");
             cmd.addParameter("ITEM1_ORG_CODE");

             trans = mgr.validate(trans);

             String sOrgCode = mgr.readValue("ITEM1_ORG_CODE");

             txt = (mgr.isEmpty(sOrgCode) ? "" : (sOrgCode)) + "^" ;

             mgr.responseWrite(txt);
         }
         else if ("ITEM1_ROLE_GROUP".equals(val))
         {
             //Bug 71867, Start
             String sRoleGroup = mgr.readValue("ITEM1_ROLE_GROUP","");
             String sRole = mgr.readValue("ITEM1_ROLE_CODE","");

             trans.clear();

             cmd = trans.addCustomFunction("GETROLEGRP","Role_To_Site_API.Get_Belongs_To_Role","ITEM1_ROLE_GROUP");
             cmd.addParameter("ITEM1_ROLE_CODE");
             cmd.addParameter("ITEM1_CONTRACT");

             trans = mgr.validate(trans);

             if (!(mgr.isEmpty(sRoleGroup) || sRoleGroup.equals(trans.getValue("GETROLEGRP/DATA/ROLE_GROUP"))))
                 sRole = "";
             //Bug 71867, End

             trans.clear();

             cmd = trans.addCustomCommand("ROLEGRPEXIST","Role_To_Site_API.Craft_Group_Exist");
             cmd.addParameter("ITEM1_ROLE_GROUP");
             cmd.addParameter("ITEM1_CONTRACT");

             //Bug 71867, Start, Modified parameter
             cmd = trans.addCustomCommand("ISROLEINGRP","Role_To_Site_API.Check_Craft_In_Group");
             cmd.addParameter("ITEM1_ROLE_CODE",sRole);
             cmd.addParameter("ITEM1_ROLE_GROUP");
             cmd.addParameter("ITEM1_CONTRACT");
             //Bug 71867, End

             trans = mgr.validate(trans);

             //Bug 71867, Start
             txt = (mgr.isEmpty(sRoleGroup) ? "" : (sRoleGroup)) + "^" +
                   (mgr.isEmpty(sRole) ? "" : (sRole)) + "^";
             //Bug 71867, End

             mgr.responseWrite(txt);
         }
         else if ("ITEM1_ROLE_CODE".equals(val))
         {
             String reqstr = null;
             int startpos = 0;
             int endpos = 0;
             int i = 0;
             String ar[] = new String[2];
             String sRoleCode = "";
             //Bug 71867, Start
             String sRoleGrp = mgr.readValue("ITEM1_ROLE_GROUP","");
             //Bug 71867, End

             String new_role_code = mgr.readValue("ITEM1_ROLE_CODE","");

             if (new_role_code.indexOf("^",0)>0)
             {
                 for (i=0 ; i<1; i++)
                 {
                     endpos = new_role_code.indexOf("^",startpos);
                     reqstr = new_role_code.substring(startpos,endpos);
                     ar[i] = reqstr;
                     startpos= endpos+1;
                 }
                 sRoleCode = ar[0];
             }
             else
                 sRoleCode = new_role_code;

             trans.clear();

             cmd = trans.addCustomCommand("ROLEEXIST","Role_API.Exist");
             cmd.addParameter("ITEM1_ROLE_CODE",sRoleCode);

             cmd = trans.addCustomCommand("ROLEGRPEXIST","Role_To_Site_API.Check_Role_Exist");
             cmd.addParameter("ITEM1_ROLE_CODE",sRoleCode);
             cmd.addParameter("ITEM1_CONTRACT");

             cmd = trans.addCustomCommand("ISROLEINGRP","Role_To_Site_API.Check_Craft_In_Group");
             cmd.addParameter("ITEM1_ROLE_CODE",sRoleCode);
             cmd.addParameter("ITEM1_ROLE_GROUP");
             cmd.addParameter("ITEM1_CONTRACT");

             //Bug 71867, Start
             if (mgr.isEmpty(sRoleGrp))
             {
                 cmd = trans.addCustomFunction("GETROLEGRP","Role_To_Site_API.Get_Belongs_To_Role","COMPETENCE_GROUP");
                 cmd.addParameter("COMPETENCE",sRoleCode);
                 cmd.addParameter("ITEM1_CONTRACT");
             }
             //Bug 71867, End

             trans = mgr.validate(trans);

             //Bug 71867, Start
             if (mgr.isEmpty(sRoleGrp))
                 sRoleGrp = trans.getValue("GETROLEGRP/DATA/COMPETENCE_GROUP");

             txt = (mgr.isEmpty(sRoleCode) ? "" : (sRoleCode)) + "^" +
                   (mgr.isEmpty(sRoleGrp) ? "" : (sRoleGrp)) + "^";
             //Bug 71867, End

             mgr.responseWrite(txt);
         }
         else if ("ITEM1_TOOL_FACILITY_TYPE".equals(val))
         {
             //Bug 71867, Start
             String sToolFacilityType = mgr.readValue("ITEM1_TOOL_FACILITY_TYPE","");
             String sToolFacility = mgr.readValue("ITEM1_RESOURCE_ID","");
             String sToolFacilityDesc = "";

             trans.clear();

             cmd = trans.addCustomFunction("GETTFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","ITEM1_TOOL_FACILITY_TYPE");
             cmd.addParameter("ITEM1_RESOURCE_ID");

             cmd = trans.addCustomFunction("GETDESCR","Tool_Facility_API.Get_Tool_Facility_Description","RESOURCE_DESCRIPTION");
             cmd.addParameter("ITEM1_RESOURCE_ID");

             trans = mgr.validate(trans);

             if (!(mgr.isEmpty(sToolFacilityType) || sToolFacilityType.equals(trans.getValue("GETTFTYPE/DATA/ITEM1_TOOL_FACILITY_TYPE"))))
             {
                 sToolFacility = "";
                 sToolFacilityDesc = "";
             }
             else
                 sToolFacilityDesc = trans.getValue("GETDESCR/DATA/RESOURCE_DESCRIPTION");
             //Bug 71867, End

             trans.clear();

             cmd = trans.addCustomCommand("TFTYPEEXIST","Tool_Facility_Type_API.Exist");
             cmd.addParameter("ITEM1_TOOL_FACILITY_TYPE");

             //Bug 71867, Start
             cmd = trans.addCustomCommand("TFEXISTINTYPE","Tool_Facility_API.Check_Tool_Facility_Type");
             cmd.addParameter("ITEM1_RESOURCE_ID",sToolFacility);
             cmd.addParameter("ITEM1_TOOL_FACILITY_TYPE");
             //Bug 71867, End

             trans = mgr.validate(trans);

             //Bug 71867, Start
             txt = (mgr.isEmpty(sToolFacilityType) ? "" : (sToolFacilityType)) + "^" +
                   (mgr.isEmpty(sToolFacility) ? "" : (sToolFacility)) + "^" +
                   (mgr.isEmpty(sToolFacilityDesc) ? "" : (sToolFacilityDesc)) + "^" ;
             //Bug 71867, End

             mgr.responseWrite(txt);
         }
         else if ("ITEM1_RESOURCE_ID".equals(val))
         {
             //Bug 71867, Start
             String sToolFacilityType = mgr.readValue("ITEM1_TOOL_FACILITY_TYPE","");
             //Bug 71867, End

             if ("TOOLANDFACILITY".equals(mgr.readValue("ITEM1_RESOURCE_TYPE_DB")))
             {   
                 trans.clear();

                 cmd = trans.addCustomCommand("TFIDEXIST","Tool_Facility_API.Exist");
                 cmd.addParameter("ITEM1_RESOURCE_ID");

                 cmd = trans.addCustomCommand("TFEXISTINTYPE","Tool_Facility_API.Check_Tool_Facility_Type");
                 cmd.addParameter("ITEM1_RESOURCE_ID");
                 cmd.addParameter("ITEM1_TOOL_FACILITY_TYPE");

                 cmd = trans.addCustomCommand("TFEXISTINORG","Connect_Tools_Facilities_API.Check_TF_In_Org");
                 cmd.addParameter("ITEM1_RESOURCE_ID");
                 cmd.addParameter("ITEM1_CONTRACT");
                 cmd.addParameter("ITEM1_ORG_CODE");

                 //Bug 71867, Start
                 if (mgr.isEmpty(sToolFacilityType))
                 {
                     cmd = trans.addCustomFunction("GETTFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","TOOL_FACILITY_TYPE");
                     cmd.addParameter("ITEM1_RESOURCE_ID");
                 }
                 //Bug 71867, End

                 trans = mgr.validate(trans);

                 //Bug 71867, Start
                 if (mgr.isEmpty(sToolFacilityType))
                     sToolFacilityType = trans.getValue("GETTFTYPE/DATA/TOOL_FACILITY_TYPE");
                 //Bug 71867, End
             }

             String sResourceId = mgr.readValue("ITEM1_RESOURCE_ID");

             //Bug 71867, Start
             txt = (mgr.isEmpty(sResourceId) ? "" : (sResourceId)) + "^" +
                   (mgr.isEmpty(sToolFacilityType) ? "" : (sToolFacilityType)) + "^" ;
             //Bug 71867, End

             mgr.responseWrite(txt);
         }
	 mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR SEARCH FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    private void searchResource()
    {
        ASPManager mgr = getASPManager();

        String infoMsg;

        trans.clear();
            
        if (mgr.isEmpty(sSearchSeq)) 
        {
           cmd = trans.addCustomFunction("GETSEQVAL3","Resource_Booking_Util_API.Get_Next_Qry_Seq","QUERY_SEQ");
             
           trans = mgr.perform(trans);
           sSearchSeq        = trans.getValue("GETSEQVAL3/DATA/QUERY_SEQ");
           trans.clear();
        }

        String sResTypeDb;
        cmd = trans.addCustomFunction("GETRESDBVAL","Res_Type_API.Encode","ITEM1_RESOURCE_TYPE_DB");
        cmd.addParameter("RESOURCE_TYPE");
        trans = mgr.perform(trans);
        sResTypeDb = trans.getValue("GETRESDBVAL/DATA/RESOURCE_TYPE_DB");
                
        trans.clear();
        String sCompany;

        if (mgr.isEmpty(mgr.readValue("EMPLOYEE")))
            sCompany = "";
        else
            sCompany = mgr.readValue("COMPANY");

        if (itemset1.countRows() == 0) 
           addNewRow();

        trans.clear();
        
        cmd = trans.addCustomCommand("PERFCOMBSEARCH","RESOURCE_BOOKING_UTIL_API.Combine_Search");
        cmd.addParameter("INFO");
        cmd.addParameter("QUERY_SEQ",sSearchSeq);
        cmd.addParameter("EARLIEST_DATE_TIME");
        cmd.addParameter("LATEST_DATE_TIME");
        cmd.addParameter("EXCLUDE_SCHEDULE");
        //Bug 71866, Start
        cmd.addParameter("CBSHOWALL");
        //Bug 71866, End
        
        trans = mgr.perform(trans);
         
        infoMsg = trans.getValue("PERFCOMBSEARCH/DATA/INFO");
        
        bCombinedSearchPerformed = true;
            
        if (("EMPLOYEE".equals(sResTypeDb) && !"EMPLOYEE".equals(sLineResourceType))
             ||("TOOLANDFACILITY".equals(sResTypeDb) && !"TOOLS_AND_FACILITY".equals(sLineResourceType)))
           itembar2.disableCommand("modifyWorkOrder");

        bCombinedSearchPerformed = false;

        //Bug 71866, Start
        if ("1".equals(mgr.readValue("CBSHOWALL")))
            sShowAll = "1";
        else
            sShowAll = "0";
        //Bug 71866, End

        headset.changeRow();

        okFindITEM2();
        if (itemset2.countRows() > 0) 
           okFindITEM3();
        if ((itemset2.countRows() == 0) && (!mgr.isEmpty(infoMsg)))
        {
            clearSearchResults();
            mgr.showAlert(infoMsg.substring(4));
        }

        tabs.setActiveTab(2);
    }

    private void addNewRow()
    {
        ASPManager mgr = getASPManager();
        clearSearchResults();
        trans.clear();
        if (mgr.isEmpty(sSearchSeq))

        {
            
            cmd = trans.addCustomFunction("GETSEQVAL","Resource_Booking_Util_API.Get_Next_Qry_Seq","QUERY_SEQ");
            trans = mgr.perform(trans);

            sSearchSeq  = trans.getValue("GETSEQVAL/DATA/QUERY_SEQ");
            trans.clear();
        }
        

        headset.changeRow();
        ASPBuffer headsetValues = headset.getRow();

        if (!bDisableSiteField)
            sOriginalSite = headset.getValue("CONTRACT");

        String resourceId = mgr.isEmpty(headset.getValue("EMPLOYEE"))?headset.getValue("TOOL_FACILITY_ID"):headset.getValue("EMPLOYEE");
        
        trans.clear();
        cmd = trans.addCustomFunction("GETRESDBVAL","Res_Type_API.Encode","ITEM1_RESOURCE_TYPE_DB");
        cmd.addParameter("RESOURCE_TYPE",mgr.readValue("RESOURCE_TYPE"));
        trans = mgr.perform(trans);
        
        String sResTypeDb = trans.getValue("GETRESDBVAL/DATA/RESOURCE_TYPE_DB");
        
        trans.clear();
        
        data = mgr.newASPBuffer();
        data.addFieldItem("ITEM1_QUERY_SEQ", sSearchSeq);
        data.addFieldItem("ITEM1_CONTRACT", sOriginalSite);
        data.addFieldItem("ITEM1_ORDER_NO", headset.getValue("ORDER_NO"));
        data.addFieldItem("ITEM1_ORG_CODE", headset.getValue("ORG_CODE"));
        data.addFieldItem("ITEM1_HOURS", headset.getValue("EXECUTION_TIME"));
        data.addFieldItem("ITEM1_RESOURCE_TYPE", headset.getValue("RESOURCE_TYPE"));
        data.addFieldItem("ITEM1_RESOURCE_TYPE_DB", sResTypeDb);
        data.addFieldItem("ITEM1_RESOURCE_ID", resourceId);
        data.addFieldItem("ITEM1_ROLE_GROUP", headset.getValue("COMPETENCE_GROUP"));
        data.addFieldItem("ITEM1_ROLE_CODE", headset.getValue("COMPETENCE"));
        data.addFieldItem("ITEM1_TOOL_FACILITY_TYPE", headset.getValue("TOOL_FACILITY_TYPE"));
        if ("EMPLOYEE".equals(sResTypeDb)) 
           data.addFieldItem("ITEMCBADDQUALIFICATION",sQual);
        itemset1.addRow(data);
        
        headset.clear();
        
        if (checkOrderSequenceForNew())
        {
            int currrowItem = itemset1.getCurrentRowNo();
            trans.clear();
            mgr.submit(trans);
            itemset1.goTo(currrowItem);
        }
        else
            itemset1.clearRow();
        
        String sCompany;
        trans.clear();    
        
        sCompany = mgr.readValue("COMPANY");
                 
        if ("EMPLOYEE".equals(sResTypeDb)) {
           cmd = trans.addCustomCommand( "SYNCRONIZE", "Resource_Booking_Util_API.Synchronize_Qualifications");  
           cmd.addParameter("SIMPLE_SEARCH_SEQ",sSimpleSearchSeq);
           cmd.addParameter("QUERY_SEQ",sSearchSeq);
           cmd.addParameter("ITEM1_ROW_NO",itemset1.getValue("ROW_NO"));
           cmd.addParameter("COMPETENCE");
           cmd.addParameter("CONTRACT");
           cmd.addParameter("COMPANY",sCompany);
           cmd.addParameter("EMPLOYEE");
           mgr.perform(trans);
        } 
        
        
        setRowBack(headsetValues);
        bDisableSiteField = true;
        iCombinedNoOfRows += 1;  
        
    }  

    //Bug 71861, Start
    private void newSearch()
    {
        bOpenNewWindow = true;
        urlString= "FreeTimeSearchEngine.page?"+initialQryStr+"";
        newWinHandle = "frmSearchEngine"; 
    }
    //Bug 71861, End
    
    public void okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        clearSearchResults();

        trans.clear();
        ASPBuffer headsetValues = headset.getRow();
        headset.clear();

        itemset2.clear(); 

        ASPQuery q = trans.addQuery(itemblk2);
        q.addWhereCondition("QUERY_SEQ = ?");
        q.addParameter("QUERY_SEQ",sSearchSeq);
        q.includeMeta("ALL"); 

        mgr.submit(trans);

        if (itemset2.countRows() == 1)
        {
            okFindITEM3();
            itemlay2.setLayoutMode(itemlay2.SINGLE_LAYOUT);
        }

        setRowBack(headsetValues);


    }

    public void okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        ASPBuffer headsetValues = headset.getRow();
        headset.clear();

        int item2rowno = itemset2.getCurrentRowNo();
        itemset3.clear(); 

        ASPQuery q = trans.addQuery(itemblk3);
        q.addWhereCondition("QUERY_SEQ = ?");
        q.addWhereCondition("RANK = ?");
        q.addParameter("QUERY_SEQ",itemset2.getRow().getValue("QUERY_SEQ"));
        q.addParameter("RANK",itemset2.getRow().getValue("RANK"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        itemset2.goTo(item2rowno);
        setRowBack(headsetValues);
    }

    public void okFindITEM5()
    {

        ASPManager mgr=getASPManager();
        trans.clear();
        headset.clear();

        trans= mgr.newASPTransactionBuffer();
        ASPQuery q=trans.addEmptyQuery(itemblk5);

        ASPBuffer  head= ctx.readBuffer("HEADDATA");

        if ((bDisableSiteField)&&(!mgr.isEmpty(sOriginalSite)))
        {
            q.addWhereCondition("CONTRACT=?");
            q.addParameter("CONTRACT",sOriginalSite);
        }
        else if (head.getValue("CONTRACT")!=null)
        {
            q.addWhereCondition("CONTRACT=?");
            q.addParameter("CONTRACT",head.getValue("CONTRACT"));
        }

        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL)");
        q.addWhereCondition("Standard_Job_API.Is_Active_Std_Job(std_job_id,contract,std_job_revision)='TRUE' ");
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (itemset5.countRows()==0)
        {
            mgr.showAlert("PCMWCONNSTDJOBSNODATA: No data found");
            itemset5.clear();
        }


    }


    public void refreshCombineLines()
    {

        ASPManager mgr=getASPManager();
        headset.clear();

        if (mgr.isEmpty(sSearchSeq))
        {
            trans.clear();
            cmd = trans.addCustomFunction("GETSEQVAL1","Resource_Booking_Util_API.Get_Next_Qry_Seq","QUERY_SEQ");
            trans = mgr.perform(trans);
            sSearchSeq  = trans.getValue("GETSEQVAL1/DATA/QUERY_SEQ");
        }

        trans.clear();
        itemset5.storeSelections();
        itemset5.setFilterOn();
        int count=itemset5.countSelectedRows(); 

        itemset5.first();
        for (int i=0; i<count; i++)
        {
            cmd=trans.addCustomCommand("ADDLINE"+i,"Resource_Booking_Util_Api.Connect_Standard_Job");
            cmd.addInParameter("STD_JOB_ID",itemset5.getValue("STD_JOB_ID"));
            cmd.addInParameter("STD_JOB_REVISION",itemset5.getValue("STD_JOB_REVISION"));
            cmd.addInParameter("STD_JOB_CONTRACT",itemset5.getValue("CONTRACT"));
            cmd.addInParameter("QRY_SEQ",sSearchSeq);
            itemset5.next();

        }

        trans=mgr.perform(trans);

        trans.clear();
        ASPQuery q=trans.addQuery(itemblk1);
        q.addWhereCondition("QUERY_SEQ=?");
        q.addParameter("QUERY_SEQ",sSearchSeq);
        q.includeMeta("ALL");
        mgr.submit(trans);

            
        ASPBuffer head=ctx.readBuffer("HEADDATA");

        String sContract=null;
        if (mgr.isEmpty(head.getValue("CONTRACT")))
        {
        
            for (int i=0;i<itemset5.countRows();i++)
            {
             if (!mgr.isEmpty(itemset1.getValue("CONTRACT")))
                    sContract=itemset1.getValue("CONTRACT");
            }

            head.setValue("CONTRACT",sContract);
        }

        if (itemset5.countRows()>0)
            bCombineLineExist=true;
        setRowBack(head);

    }

   public void setHeadData()
     {
    
        ASPManager mgr=getASPManager();
        ASPBuffer head  = mgr.getTransferedData();
        ctx.writeBuffer("HEADDATA",head);
        String cbQualifications="";

        //if (!mgr.isEmpty(sResourceType))
           sSimpleSearchSeq = ctx.getGlobal("SIMPLESEARCHSEQ");

	sResourceType = mgr.getQueryStringValue("RES_TYPE");
	
        if (bCallingFromHead) {
           cmd=trans.addCustomFunction("HASQUALIFICATION","RESOURCE_BOOKING_UTIL_API.Has_Qualifications","CBADDQUALIFICATION");
           cmd.addParameter("NULL");
           cmd.addParameter("NULL");
           cmd.addParameter("SIMPLE_SEARCH_SEQ",sSimpleSearchSeq);
           trans=mgr.perform(trans);
           cbQualifications = trans.getValue("HASQUALIFICATION/DATA/CBADDQUALIFICATION");
           head.setFieldItem("CBADDQUALIFICATION",cbQualifications);
           if (cbQualifications.equals("TRUE")) 
               sQual = "TRUE";
           else
               sQual = "FALSE";

           sSearchSeq  = ctx.findGlobal("QUERYSEQ","");
           ctx.removeGlobal("QUERYSEQ");
           if (!mgr.isEmpty(sSearchSeq))
           {
               trans.clear();
               ASPQuery q=trans.addEmptyQuery(itemblk1);
               q.addWhereCondition("QUERY_SEQ=?");
               q.addParameter("QUERY_SEQ",sSearchSeq);
               q.includeMeta("ALL");
               mgr.submit(trans);
           }
        }
        else
        {
           cbQualifications = head.getValue("CBADDQUALIFICATION");
           if (cbQualifications.equals("TRUE")) 
               sQual = "TRUE";
           else
               sQual = "FALSE";

            sSearchSeq       = ctx.getGlobal("QUERYSEQ");
            trans.clear();
            ASPQuery q=trans.addEmptyQuery(itemblk1);
            q.addWhereCondition("QUERY_SEQ=?");
            q.addParameter("QUERY_SEQ",sSearchSeq);
            q.includeMeta("ALL");
            mgr.submit(trans);
       }
        setRowBack(head);
      }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    private void newRow()
    {
        ASPManager mgr = getASPManager();

        //Bug 71861, Start
        initialQryStr = mgr.getQueryString();
        //Bug 71861, End

        trans.clear();
        String cbQualifications="";
        sResourceType = mgr.getQueryStringValue("RES_TYPE");
	
	sWoNo = mgr.getQueryStringValue("WO_NO");
        sRowNo = mgr.getQueryStringValue("ROW_NO");
        String sContract = mgr.getQueryStringValue("CONTRACT");
        String sEmployee = mgr.getQueryStringValue("EMPLOYEE");
        sCallFormName    = mgr.getQueryStringValue("FRMNAME");
        
        if (!mgr.isEmpty(sResourceType))
        {
            sLineResourceType = new String(sResourceType);
        
            cmd = trans.addCustomFunction("SSEQ","Resource_Booking_Util_Api.Get_Next_Simple_Search_Seq","SIMPLE_SEARCH_SEQ");
            cmd = trans.addCustomCommand("COPYQUAL","Resource_Booking_Util_API.Copy_Qualifications_From_Wo");
            cmd.addParameter("WO_NO",sWoNo);
            cmd.addParameter("ROW_NO",sRowNo);
            cmd.addParameter("CONTRACT",sContract);
            cmd.addParameter("EMPLOYEE",sEmployee);
            cmd.addReference("SIMPLE_SEARCH_SEQ","SSEQ/DATA");
            
            cmd=trans.addCustomFunction("HASQUALIFICATION","RESOURCE_BOOKING_UTIL_API.Has_Qualifications","CBADDQUALIFICATION");
            cmd.addParameter("NULL");
            cmd.addParameter("NULL");
            cmd.addReference("SIMPLE_SEARCH_SEQ","SSEQ/DATA");
            
            trans = mgr.perform(trans);
                                         
            sSimpleSearchSeq  = trans.getValue("SSEQ/DATA/SIMPLE_SEARCH_SEQ");
            cbQualifications = trans.getValue("HASQUALIFICATION/DATA/CBADDQUALIFICATION");
            
            trans.clear();
         }
            
        
        String sOrgCode = mgr.getQueryStringValue("ORG_CODE");
        String sOrgCodeDesc = "";
        String sCompany;
        String sSiteDesc;

        String sCompetence = mgr.getQueryStringValue("COMPETENCE");
        String sCompetenceDesc = "";
        String sEmployeeName = "";

        String sToolFacilityType = mgr.getQueryStringValue("TOOL_FACILITY_TYPE");
        String sToolFacilityTypeDesc = "";
        String sToolFacilityId = mgr.getQueryStringValue("TOOL_FACILITY_ID");
        String sToolFacilityDesc = "";

        
        sObjId = mgr.getQueryStringValue("OBJ_ID");
        sObjVersion = mgr.getQueryStringValue("OBJ_VERSION");

        String sEarliestDateTime = mgr.getQueryStringValue("PLAN_S_DATE");
        String sLatestDateTime = mgr.getQueryStringValue("PLAN_F_DATE");
        String sExecutionTime = mgr.getQueryStringValue("PLAN_HRS");

        if (mgr.isEmpty(sContract))
        {
            cmd = trans.addCustomFunction("GETCONT","User_Allowed_Site_API.Get_Default_Site","CONTRACT");

            cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
            cmd.addReference("CONTRACT","GETCONT/DATA");

            cmd = trans.addCustomFunction("GETSITEDESC","Site_API.Get_Description","SITE_DESCRIPTION");
            cmd.addReference("CONTRACT","GETCONT/DATA");
        }
        else
        {
            cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT",sContract);

            cmd = trans.addCustomFunction("GETSITEDESC","Site_API.Get_Description","SITE_DESCRIPTION");
            cmd.addParameter("CONTRACT",sContract);

            if (!mgr.isEmpty(sOrgCode))
            {
                cmd = trans.addCustomFunction("GETORGDESC","Organization_API.Get_Description","ORG_DESCRIPTION");
                cmd.addParameter("CONTRACT",sContract);  
                cmd.addParameter("ORG_CODE",sOrgCode);      
            }

            if (!mgr.isEmpty(sEmployee))
            {
                cmd = trans.addCustomFunction("GETEMPNAME","Employee_API.Get_Name","EMPLOYEE_NAME");
                cmd.addReference("COMPANY","GETCOMP/DATA");  
                cmd.addParameter("EMPLOYEE",sEmployee);      
            }
        }

        cmd = trans.addCustomFunction("GETEMPTRANS","Res_Type_API.Decode","ITEM1_RESOURCE_TYPE");
        cmd.addParameter("ITEM1_RESOURCE_TYPE_DB","EMPLOYEE");

        cmd = trans.addCustomFunction("GETTOOLTRANS","Res_Type_API.Decode","ITEM1_RESOURCE_TYPE");
        cmd.addParameter("ITEM1_RESOURCE_TYPE_DB","TOOLANDFACILITY");

        cmd = trans.addQuery("GETSYSDATE","SELECT SYSDATE EARLIEST_DATE_TIME FROM DUAL");

        if (!mgr.isEmpty(sCompetence))
        {
            cmd = trans.addCustomFunction("GETCOMPDESC","Role_API.Get_Description","COMPETENCE_DESCRIPTION");
            cmd.addParameter("COMPETENCE",sCompetence);
        }

        if (!mgr.isEmpty(sToolFacilityType))
        {
            cmd = trans.addCustomFunction("GETTOOLTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TOOL_FACILITY_TYPE_DESC");
            cmd.addParameter("TOOL_FACILITY_TYPE",sToolFacilityType);
        }

        if (!mgr.isEmpty(sToolFacilityId))
        {
            cmd = trans.addCustomFunction("GETTOOLDESC","Tool_Facility_API.Get_Tool_Facility_Description","TOOL_FACILITY_DESC");
            cmd.addParameter("TOOL_FACILITY_ID",sToolFacilityId);
        }

        if ("EMPLOYEE".equals(sResourceType))
        {
            cmd = trans.addCustomFunction("GETEARLIEST","Work_Order_Role_API.Get_Date_From","EARLIEST_DATE_TIME");
            cmd.addParameter("WO_NO", sWoNo);
            cmd.addParameter("ROW_NO", sRowNo);

            cmd = trans.addCustomFunction("GETLATEST","Work_Order_Role_API.Get_Date_To","LATEST_DATE_TIME");
            cmd.addParameter("WO_NO", sWoNo);
            cmd.addParameter("ROW_NO", sRowNo);

            cmd = trans.addCustomFunction("GETEXECUTION","Work_Order_Role_API.Get_Plan_Hrs","EXECUTION_TIME");
            cmd.addParameter("WO_NO", sWoNo);
            cmd.addParameter("ROW_NO", sRowNo);
        }
        else if ("TOOLS_AND_FACILITY".equals(sResourceType))
        {
            cmd = trans.addCustomFunction("GETEARLIEST","Work_Order_Tool_Facility_API.Get_From_Date_Time","EARLIEST_DATE_TIME");
            cmd.addParameter("WO_NO", sWoNo);
            cmd.addParameter("ROW_NO", sRowNo);

            cmd = trans.addCustomFunction("GETLATEST","Work_Order_Tool_Facility_API.Get_To_Date_Time","LATEST_DATE_TIME");
            cmd.addParameter("WO_NO", sWoNo);
            cmd.addParameter("ROW_NO", sRowNo);

            cmd = trans.addCustomFunction("GETEXECUTION","Work_Order_Tool_Facility_API.Get_Planned_Hour","EXECUTION_TIME");
            cmd.addParameter("WO_NO", sWoNo);
            cmd.addParameter("ROW_NO", sRowNo);
        }


        cmd=trans.addCustomFunction("GETSERACHLIMIT","MAINTENANCE_CONFIGURATION_API.Get_Search_Limit","NDUMMY");
        trans = mgr.perform(trans);

        sTranslatedEmpType  = trans.getValue("GETEMPTRANS/DATA/RESOURCE_TYPE");
        sTranslatedToolType  = trans.getValue("GETTOOLTRANS/DATA/RESOURCE_TYPE");
        sSysParamNofDays=trans.getValue("GETSERACHLIMIT/DATA/NDUMMY");

        if (mgr.isEmpty(sContract))
            sContract = trans.getValue("GETCONT/DATA/CONTRACT");

        if (!mgr.isEmpty(sContract) && !mgr.isEmpty(sOrgCode))
            sOrgCodeDesc = trans.getValue("GETORGDESC/DATA/ORG_DESCRIPTION");

        if (!mgr.isEmpty(sContract) && !mgr.isEmpty(sEmployee))
            sEmployeeName = trans.getValue("GETEMPNAME/DATA/EMPLOYEE_NAME");

        if (!mgr.isEmpty(sCompetence))
            sCompetenceDesc = trans.getValue("GETCOMPDESC/DATA/COMPETENCE_DESCRIPTION");

        if (!mgr.isEmpty(sToolFacilityType))
            sToolFacilityTypeDesc = trans.getValue("GETTOOLTYPEDESC/DATA/TOOL_FACILITY_TYPE_DESC");

        if (!mgr.isEmpty(sToolFacilityId))
            sToolFacilityDesc = trans.getValue("GETTOOLDESC/DATA/TOOL_FACILITY_DESC");

        if ("EMPLOYEE".equals(sResourceType) || "TOOLS_AND_FACILITY".equals(sResourceType))
        {
            sEarliestDateTime = trans.getBuffer("GETEARLIEST/DATA").getFieldValue("EARLIEST_DATE_TIME");
            sLatestDateTime = trans.getBuffer("GETLATEST/DATA").getFieldValue("LATEST_DATE_TIME");
            sExecutionTime = trans.getValue("GETEXECUTION/DATA/EXECUTION_TIME");
            bLineDataTransfered = true;
        }
        else
            bLineDataTransfered = false;

        if (mgr.isEmpty(sEarliestDateTime))
            sEarliestDateTime = trans.getBuffer("GETSYSDATE/DATA").getFieldValue("EARLIEST_DATE_TIME");

        if (mgr.isEmpty(sLatestDateTime))
            sLatestDateTime = trans.getBuffer("GETSYSDATE/DATA").getFieldValue("EARLIEST_DATE_TIME");

        sCompany = trans.getValue("GETCOMP/DATA/COMPANY");
        sSiteDesc = trans.getValue("GETSITEDESC/DATA/SITE_DESCRIPTION");

        ASPBuffer dummyBuffer = mgr.newASPBuffer();
        dummyBuffer.addFieldItem("EARLIEST_DATE_TIME", sEarliestDateTime);
        dummyBuffer.addFieldItem("LATEST_DATE_TIME", sLatestDateTime);

        trans.clear();
        cmd = trans.addQuery("GETNEWTIMES","SELECT (TRUNC(TO_DATE(?,'YYYY-MM-DD-HH24-MI-SS') + (59/(24*60*60)),'MI')) EARLIEST_DATE_TIME,(TRUNC(TO_DATE(?,'YYYY-MM-DD-HH24-MI-SS') + (59/(24*60*60)+ ?),'MI')) LATEST_DATE_TIME FROM DUAL");
        cmd.addParameter("DUMMY",dummyBuffer.getValue("EARLIEST_DATE_TIME"));
        cmd.addParameter("DUMMY",dummyBuffer.getValue("LATEST_DATE_TIME"));
        cmd.addParameter("DUMMY",(mgr.isEmpty(sSysParamNofDays)?"0":sSysParamNofDays));
	trans = mgr.perform(trans);

        sEarliestDateTime = trans.getBuffer("GETNEWTIMES/DATA").getFieldValue("EARLIEST_DATE_TIME");
        sLatestDateTime = trans.getBuffer("GETNEWTIMES/DATA").getFieldValue("LATEST_DATE_TIME");

        data = mgr.newASPBuffer();
        if (!mgr.isEmpty(sResourceType)){
           data.setFieldItem("CBADDQUALIFICATION",cbQualifications);
           data.setFieldItem("SIMPLE_SEARCH_SEQ",sSimpleSearchSeq);
           if (cbQualifications.equals("TRUE")) 
               sQual = "TRUE";
           else
               sQual = "FALSE";
        }

        data.addFieldItem("CONTRACT", sContract);
        data.addFieldItem("HEAD_WO_NO", sWoNo);
        data.addFieldItem("COMPANY", sCompany);
        data.addFieldItem("ORG_CODE", sOrgCode);
        data.addFieldItem("ORG_DESCRIPTION", sOrgCodeDesc);
        data.addFieldItem("SITE_DESCRIPTION", sSiteDesc);
        data.addFieldItem("COMPETENCE", sCompetence);
        data.addFieldItem("COMPETENCE_DESCRIPTION", sCompetenceDesc);
        data.addFieldItem("EMPLOYEE", sEmployee);
        data.addFieldItem("EMPLOYEE_NAME", sEmployeeName);
        data.addFieldItem("TOOL_FACILITY_TYPE", sToolFacilityType);
        data.addFieldItem("TOOL_FACILITY_TYPE_DESC", sToolFacilityTypeDesc);
        data.addFieldItem("TOOL_FACILITY_ID", sToolFacilityId);
        data.addFieldItem("TOOL_FACILITY_DESC", sToolFacilityDesc);
        data.addFieldItem("EARLIEST_DATE_TIME", sEarliestDateTime);
        data.addFieldItem("LATEST_DATE_TIME", sLatestDateTime);
        data.addFieldItem("EXECUTION_TIME", (mgr.isEmpty(sExecutionTime)?"1":sExecutionTime));
        headset.addRow(data);

    }

    private void setRowBack(ASPBuffer valueBuffer)
    {
        ASPManager mgr = getASPManager();

        headset.addRow(valueBuffer);

    }

    public void createWorkOrder()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
            itemset2.goTo(itemset2.getRowSelected());

        //===================== Start - CALLC =========================//
        if (bFromCallc)
        {
            sDlgName = "CreateWoForCallc";

            ASPTransactionBuffer callcBuf = mgr.newASPTransactionBuffer();
            ASPCommand cmd = null;

            cmd = callcBuf.addCustomFunction("CMDCUSTID","CC_CASE_API.Get_Customer_Id","CALLC_CUSTOMER_NO");
            cmd.addParameter("CALLC_DUMMY",sCaseId);

            cmd = callcBuf.addCustomFunction("CMDDESC","CC_CASE_API.Get_Title","CALLC_DIRECTIVE");
            cmd.addParameter("CALLC_DUMMY",sCaseId);

            cmd = callcBuf.addCustomFunction("CMDPERSON","PERSON_INFO_API.Get_Id_For_User","CALLC_REPORTED_BY");
            cmd.addParameter("CALLC_DUMMY",mgr.getFndUser());

            callcBuf = mgr.perform(callcBuf);

            data = mgr.newASPBuffer();
            data.addFieldItem("CALLC_CONTRACT", headset.getValue("CONTRACT"));
            data.addFieldItem("CALLC_CUSTOMER_NO", callcBuf.getValue("CMDCUSTID/DATA/CALLC_CUSTOMER_NO"));
            data.addFieldItem("CALLC_DIRECTIVE", callcBuf.getValue("CMDDESC/DATA/CALLC_DIRECTIVE"));
            data.addFieldItem("CALLC_REPORTED_BY", callcBuf.getValue("CMDPERSON/DATA/CALLC_REPORTED_BY"));
            data.addFieldItem("CALLC_CONTRACT", callcBuf.getValue("CMDCUSTID/DATA/CALLC_CUSTOMER_NO"));
            data.addFieldItem("CALLC_REFERENCE",mgr.translate("PCMWFREETSEARCCALLCREFERENCEHEAD: Case ID: ") + sCaseLocalId);

            itemset6.addRow(data);
        }
        //===================== End - CALLC =========================//

        else
        {


            sDlgName = "CreateWo";

            mgr.getASPField("WO_NO").setHidden();
            mgr.getASPField("ADDHEADER").setHidden();
            mgr.getASPField("WO_SITE").unsetHidden();
            mgr.getASPField("REPORTED_BY").unsetHidden();
            mgr.getASPField("MAINT_ORG").unsetHidden();
            mgr.getASPField("MAINT_ORG_DESC").unsetHidden();
            mgr.getASPField("DIRECTIVE").unsetHidden();

            data = mgr.newASPBuffer();
            data.addFieldItem("WO_SITE", headset.getValue("CONTRACT"));
            data.addFieldItem("ITEM4_COMPANY", headset.getValue("COMPANY"));
            data.addFieldItem("MAINT_ORG", headset.getValue("ORG_CODE"));
            data.addFieldItem("MAINT_ORG_DESC", headset.getValue("ORG_DESCRIPTION"));

            trans.clear();
            cmd = trans.addQuery("REPBY","SELECT Fnd_Session_API.Get_Fnd_User FROM DUAL");
            trans = mgr.perform(trans);

            String fndUser = trans.getValue("REPBY/DATA/GET_FND_USER");

            trans.clear();
            cmd = trans.addCustomFunction("PERSONINFO", "Person_Info_API.Get_Id_For_User", "REPORTED_BY" );
            cmd.addParameter("REPORTED_BY",fndUser);

            trans = mgr.perform(trans);
            String repBy = trans.getValue("PERSONINFO/DATA/REPORTED_BY");
            data.addFieldItem("REPORTED_BY", repBy);
            trans.clear();
                        
            if (iCombinedNoOfRows==0) {
               if (!mgr.isEmpty(itemset2.getValue("DATE_TIME_FROM"))) 
                  data.addFieldDateItem("ITEM4_EARLIEST_DATE_TIME", itemset2.getDateValue("DATE_TIME_FROM"));
               else
                  data.addFieldItem("ITEM4_EARLIEST_DATE_TIME", headset.getValue("EARLIEST_DATE_TIME"));
       
               if (!mgr.isEmpty(itemset2.getValue("DATE_TIME_TO"))) 
                 data.addFieldDateItem("ITEM4_LATEST_DATE_TIME", itemset2.getDateValue("DATE_TIME_TO"));
               else
               {
                  cmd = trans.addQuery("GETNEWTIMES","SELECT (TRUNC(TO_DATE(?,'YYYY-MM-DD-HH24-MI-SS') + (59/(24*60*60)+ ?),'MI')) LATEST_DATE_TIME FROM DUAL");
                  cmd.addParameter("DUMMY",headset.getValue("EARLIEST_DATE_TIME"));
                  cmd.addParameter("DUMMY",(mgr.isEmpty(headset.getValue("EXECUTION_TIME"))?"0":headset.getValue("EXECUTION_TIME")));
                  trans = mgr.perform(trans);
                  String sLatestDateTime = trans.getBuffer("GETNEWTIMES/DATA").getFieldValue("LATEST_DATE_TIME");
                  data.addFieldItem("ITEM4_LATEST_DATE_TIME", sLatestDateTime);
               }
        
               if (!mgr.isEmpty(itemset3.getValue("PLANNED_HOURS"))) 
                   data.addFieldItem("ITEM4_EXECUTION_TIME", itemset3.getValue("PLANNED_HOURS"));
               else
                   data.addFieldItem("ITEM4_EXECUTION_TIME", headset.getValue("EXECUTION_TIME"));
            }   
            else
            {
                mgr.getASPField("ITEM4_EARLIEST_DATE_TIME").setReadOnly();
                mgr.getASPField("ITEM4_LATEST_DATE_TIME").setReadOnly();
                mgr.getASPField("ITEM4_EXECUTION_TIME").setReadOnly();
          
            }
        //---
        
            itemset4.addRow(data);


        }

   }

    public void addToWorkOrder()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
            itemset2.goTo(itemset2.getRowSelected());

        sDlgName = "AddToWo";

        mgr.getASPField("WO_NO").unsetHidden();
        mgr.getASPField("WO_SITE").setHidden();
        mgr.getASPField("REPORTED_BY").setHidden();
        mgr.getASPField("MAINT_ORG").setHidden();
        mgr.getASPField("MAINT_ORG_DESC").setHidden();
        mgr.getASPField("DIRECTIVE").setHidden();

        trans.clear();

        String stmt = "SELECT to_char(count(*)) NDUMMY FROM RESULT_DETAILS1 " +
                      "WHERE QUERY_SEQ = ?" +
                      "AND RANK = ? " +
                      "AND RESOURCE_TYPE_DB = 'TOOLANDFACILITY'";

        cmd = trans.addQuery("GETTOOLCOUNT", stmt);
        cmd.addParameter("QUERY_SEQ",itemset2.getValue("QUERY_SEQ"));
        cmd.addParameter("RANK",itemset2.getValue("RANK"));

        trans = mgr.perform(trans);

        int nToolCount = Integer.parseInt(mgr.isEmpty(trans.getValue("GETTOOLCOUNT/DATA/NDUMMY"))?"0":trans.getValue("GETTOOLCOUNT/DATA/NDUMMY"));

        if (nToolCount > 0)
            mgr.getASPField("WO_NO").setLOVProperty("WHERE", "CONNECTION_TYPE_DB = 'EQUIPMENT'");
        
        data = mgr.newASPBuffer();
        
        if (iCombinedNoOfRows==0) {
           
           if (!mgr.isEmpty(itemset2.getValue("DATE_TIME_FROM"))) 
             data.addFieldDateItem("ITEM4_EARLIEST_DATE_TIME", itemset2.getDateValue("DATE_TIME_FROM"));
           else
             data.addFieldItem("ITEM4_EARLIEST_DATE_TIME", headset.getValue("EARLIEST_DATE_TIME"));
       
           if (!mgr.isEmpty(itemset2.getValue("DATE_TIME_TO"))) 
             data.addFieldDateItem("ITEM4_LATEST_DATE_TIME", itemset2.getDateValue("DATE_TIME_TO"));
           else
           {
             cmd = trans.addQuery("GETNEWTIMES","SELECT (TRUNC(TO_DATE(?,'YYYY-MM-DD-HH24-MI-SS') + (59/(24*60*60)+ ?),'MI')) LATEST_DATE_TIME FROM DUAL");
             cmd.addParameter("DUMMY",headset.getValue("EARLIEST_DATE_TIME"));
             cmd.addParameter("DUMMY",(mgr.isEmpty(headset.getValue("EXECUTION_TIME"))?"0":headset.getValue("EXECUTION_TIME")));
             trans = mgr.perform(trans);
             String sLatestDateTime = trans.getBuffer("GETNEWTIMES/DATA").getFieldValue("LATEST_DATE_TIME");
             data.addFieldItem("ITEM4_LATEST_DATE_TIME", sLatestDateTime);
           }
        
          if (!mgr.isEmpty(itemset3.getValue("PLANNED_HOURS"))) 
             data.addFieldItem("ITEM4_EXECUTION_TIME", itemset3.getValue("PLANNED_HOURS"));
          else
             data.addFieldItem("ITEM4_EXECUTION_TIME", headset.getValue("EXECUTION_TIME"));

          
        }   
        else
        {
          mgr.getASPField("ITEM4_EARLIEST_DATE_TIME").setReadOnly();
          mgr.getASPField("ITEM4_LATEST_DATE_TIME").setReadOnly();
          mgr.getASPField("ITEM4_EXECUTION_TIME").setReadOnly();
          //mgr.getASPField("ADDHEADER").setReadOnly();
          
        }
        //---
        
        trans.clear();

	cmd = trans.addCustomFunction("HASROLE","Active_Separate_API.Has_Role","ADDHEADER");
        cmd.addParameter("WO_NO", mgr.readValue("HEAD_WO_NO"));
        trans=mgr.perform(trans);

	String sAddToHeader =  trans.getValue("HASROLE/DATA/ADDHEADER");
   
	String sCurrentResourceType = mgr.readValue("TEMP_RES_TYEP");
	
	if ("TOOLS_AND_FACILITY".equals(sCurrentResourceType)) 
	   mgr.getASPField("ADDHEADER").setReadOnly();
	else if ( "TRUE".equals(sAddToHeader) )
           mgr.getASPField("ADDHEADER").setReadOnly();
	else if ( "FALSE".equals(sAddToHeader))
	   mgr.getASPField("ADDHEADER").setInsertable();

        data.addFieldItem("WO_NO", mgr.readValue("HEAD_WO_NO"));
        data.addFieldItem("WO_SITE", headset.getValue("CONTRACT"));
        data.addFieldItem("ITEM4_COMPANY", headset.getValue("COMPANY"));
        itemset4.addRow(data);

   }

    public void modifyWorkOrder()
   {
     ASPManager mgr = getASPManager();
     if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());

     sDlgName = "ModifyWo";
     
     data = mgr.newASPBuffer();

     if (!mgr.isEmpty(itemset2.getValue("DATE_TIME_FROM"))) 
        data.addFieldDateItem("ITEM7_EARLIEST_DATE_TIME", itemset2.getDateValue("DATE_TIME_FROM"));
     else
        data.addFieldItem("ITEM7_EARLIEST_DATE_TIME", headset.getValue("EARLIEST_DATE_TIME"));


     if (!mgr.isEmpty(itemset2.getValue("DATE_TIME_TO"))) 
        data.addFieldDateItem("ITEM7_LATEST_DATE_TIME", itemset2.getDateValue("DATE_TIME_TO"));
     else
     {
         cmd = trans.addQuery("GETNEWTIMES","SELECT (TRUNC(TO_DATE(?,'YYYY-MM-DD-HH24-MI-SS') + (59/(24*60*60)+ ?),'MI')) LATEST_DATE_TIME FROM DUAL");
         cmd.addParameter("DUMMY",headset.getValue("EARLIEST_DATE_TIME"));
         cmd.addParameter("DUMMY",(mgr.isEmpty(headset.getValue("EXECUTION_TIME"))?"0":headset.getValue("EXECUTION_TIME")));
         trans = mgr.perform(trans);
         String sLatestDateTime = trans.getBuffer("GETNEWTIMES/DATA").getFieldValue("LATEST_DATE_TIME");
         data.addFieldItem("ITEM7_LATEST_DATE_TIME", sLatestDateTime);
     }

     if (!mgr.isEmpty(itemset3.getValue("PLANNED_HOURS"))) 
        data.addFieldItem("ITEM7_EXECUTION_TIME", itemset3.getValue("PLANNED_HOURS"));
     else
        data.addFieldItem("ITEM7_EXECUTION_TIME", headset.getValue("EXECUTION_TIME"));

     itemset7.addRow(data);

   }

    public void modifyWo()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomCommand("MODIFYWO","RESOURCE_BOOKING_UTIL_API.Modify_Wo_Line");
        cmd.addParameter("INFO");
        cmd.addParameter("WO_NO", sWoNo);
        cmd.addParameter("OBJID", sObjId);
        cmd.addParameter("OBJVERSION", sObjVersion);
        cmd.addParameter("ROW_NO", sRowNo);
        cmd.addParameter("QUERY_SEQ", itemset2.getValue("QUERY_SEQ"));
        cmd.addParameter("RANK", itemset2.getValue("RANK"));
        cmd.addParameter("ITEM7_EARLIEST_DATE_TIME");
        cmd.addParameter("ITEM7_LATEST_DATE_TIME");
        cmd.addParameter("ITEM7_EXECUTION_TIME");


        trans = mgr.perform(trans);

        sExecutionInfo = trans.getValue("MODIFYWO/DATA/INFO");

         itemset7.clear();

        if (mgr.isEmpty(sWoNo))
            sConfirmationText = "";
        else
        {
            sConfirmationText   = mgr.translate("PCMWFREETSEARCHMODIFYWOCONF: Existing line(s) was modified in Work Order &1. Do you want to view this work order?", sWoNo);
            sConfirmedUrlString = "ActiveSeparate2ServiceManagement.page?WO_NO=" + mgr.URLEncode(sWoNo);
            sConfirmedNewWinHandle = "modifiedwoform";
            connectTnF(sWoNo,sConfirmationText,sConfirmedUrlString,sConfirmedNewWinHandle);
        }

    } 

    private void createWo()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomCommand("GETNEWWONO","RESOURCE_BOOKING_UTIL_API.New_Wo");
        cmd.addParameter("INFO");
        cmd.addParameter("WO_NO");
        cmd.addParameter("QUERY_SEQ", itemset2.getValue("QUERY_SEQ"));
        cmd.addParameter("RANK", itemset2.getValue("RANK"));
        cmd.addParameter("WO_SITE");
        cmd.addParameter("MAINT_ORG");
        cmd.addParameter("REPORTED_BY");
        cmd.addParameter("DIRECTIVE");
        cmd.addParameter("ITEM4_EARLIEST_DATE_TIME");
        cmd.addParameter("ITEM4_LATEST_DATE_TIME");
        cmd.addParameter("ITEM4_EXECUTION_TIME");

        trans = mgr.perform(trans);

        String sWoNo = trans.getValue("GETNEWWONO/DATA/WO_NO");

        sExecutionInfo = trans.getValue("GETNEWWONO/DATA/INFO");

        itemset4.clear();

        if (mgr.isEmpty(sWoNo))
            sConfirmationText = "";
        else
        {
            sConfirmationText   = mgr.translate("PCMWFREETSEARCHCREWOCONF: Work Order &1 was created. Do you want to view this work order?", sWoNo);
            sConfirmedUrlString = "ActiveSeparate2ServiceManagement.page?WO_NO=" + mgr.URLEncode(sWoNo);
            sConfirmedNewWinHandle = "createdwoform";
            connectTnF(sWoNo,sConfirmationText,sConfirmedUrlString,sConfirmedNewWinHandle);
        }
    }

    //======================================= Start - CALLC ===========================================//

    private void createWoViaRP()
    {

        //validate fields  against empty values.
        ASPManager mgr = getASPManager();

        trans.clear();

        itemset6.changeRow();

        if (checkFields(mgr))
        {

            cmd = trans.addCustomCommand("GETNEWWONO","CC_CASE_TASK_API.Create_Handover_To_Wo_Via_Rp");
            cmd.addParameter("INFO");
            cmd.addParameter("WO_NO");
            cmd.addParameter("CALLC_CASE_ID",sCaseId);
            cmd.addParameter("CALLC_TASK_ID",sTaskId);
            cmd.addParameter("QUERY_SEQ", itemset2.getValue("QUERY_SEQ"));
            cmd.addParameter("RANK", itemset2.getValue("RANK"));
            cmd.addParameter("CALLC_CONTRACT",itemset6.getValue("CALLC_CONTRACT"));
            cmd.addParameter("CALLC_MAINT_ORG",itemset6.getValue("CALLC_MAINT_ORG"));
            cmd.addParameter("CALLC_REPORTED_BY",itemset6.getValue("CALLC_REPORTED_BY"));
            cmd.addParameter("CALLC_DIRECTIVE",itemset6.getValue("CALLC_DIRECTIVE"));
            cmd.addParameter("CALLC_CUSTOMER_NO",itemset6.getValue("CALLC_CUSTOMER_NO"));
            cmd.addParameter("CALLC_REFERENCE",itemset6.getValue("CALLC_REFERENCE"));
            cmd.addParameter("CALLC_HANDOVER",sHandoverRef);

            trans = mgr.perform(trans);

            String sWoNo = trans.getValue("GETNEWWONO/DATA/WO_NO");

            sExecutionInfo = trans.getValue("GETNEWWONO/DATA/INFO");

            itemset6.clear();

            if (mgr.isEmpty(sWoNo))
                sConfirmationText = "";
            else
            {
                sConfirmationText   = mgr.translate("PCMWFREETSEARCHCREWOCONF: Work Order &1 was created. Do you want to view this work order?", sWoNo);
                sConfirmedUrlString = "ActiveSeparate2ServiceManagement.page?WO_NO=" + mgr.URLEncode(sWoNo);
                sConfirmedNewWinHandle = "createdwoform";

                connectTnF(sWoNo,sConfirmationText,sConfirmedUrlString,sConfirmedNewWinHandle);
            }

        }
    }

    private boolean checkFields(ASPManager mgr)
    {
        boolean validated = true;
        String fieldName = "";

        if (mgr.isEmpty(itemset6.getValue("CALLC_MAINT_ORG")))
        {
            fieldName = "PCMWFREETSEARCHCREWOMAINT: Maint. Org";
            validated = false;
        }
        else if (mgr.isEmpty(itemset6.getValue("CALLC_DIRECTIVE")))
        {
            fieldName = "PCMWFREETSEARCHCREWODIR: Directive";
            validated = false;
        }
        else if (mgr.isEmpty(itemset6.getValue("CALLC_REFERENCE")))
        {
            fieldName = "PCMWFREETSEARCHCREWOREFER: Reference No";
            validated = false;
        }

        if (!validated)
        {
            fieldName = mgr.translate(fieldName);
            mgr.showAlert(mgr.translate("PCMWFREETSEARCHCREWOCVALIDATED: Required value for field [&1]",fieldName));
            sDlgName = "CreateWoForCallc";
        }

        return validated;
    }

    private void initCallcData(ASPManager mgr)
    {
        sCaseId = mgr.getQueryStringValue("CASE_ID");
        sTaskId = mgr.getQueryStringValue("TASK_ID");
        sCaseLocalId = mgr.getQueryStringValue("CASE_LOCAL_ID");
        sHandoverRef = mgr.getQueryStringValue("HAND_OVER_REF");

        ASPTransactionBuffer callcBuf = mgr.newASPTransactionBuffer();
        callcBuf.addCustomFunction("CALLCSITE","User_Default_API.Get_Contract","CALLC_CONTRACT");
        callcBuf = mgr.perform(callcBuf);

        headset.setValue("CONTRACT",callcBuf.getValue("CALLCSITE/DATA/CALLC_CONTRACT"));
    }

    //============================================== End - CALLC =================================================//
     
    
    private void addToWo()
    {
	ASPManager mgr = getASPManager();
        trans.clear();
        String addHeader="0";
        if ("TRUE".equals(mgr.readValue("ADDHEADER"))) 
           addHeader = "1";
        else
           addHeader = "0";
           
        cmd = trans.addCustomCommand("ADDTOEXTWO","RESOURCE_BOOKING_UTIL_API.New_Wo_Lines");
        cmd.addParameter("INFO");
        cmd.addParameter("WO_NO");
        cmd.addParameter("QUERY_SEQ", itemset2.getValue("QUERY_SEQ"));
        cmd.addParameter("RANK", itemset2.getValue("RANK"));
        cmd.addParameter("ITEM4_EARLIEST_DATE_TIME");
        cmd.addParameter("ITEM4_LATEST_DATE_TIME");
        cmd.addParameter("ITEM4_EXECUTION_TIME");
        cmd.addParameter("ADDHEADER",addHeader);

        trans = mgr.perform(trans);

        String sWoNo = trans.getValue("ADDTOEXTWO/DATA/WO_NO");

        sExecutionInfo = trans.getValue("ADDTOEXTWO/DATA/INFO");

        itemset4.clear();

        if (mgr.isEmpty(sWoNo))
            sConfirmationText = "";
        else
        {
            sConfirmationText   = mgr.translate("PCMWFREETSEARCHADDWOCONF: New line(s) was added to Work Order &1. Do you want to view this work order?", sWoNo);
            
            if ( "prepareWorkOrd".equals(sCallFormName) ) 
	       sConfirmedUrlString = "ActiveSeparate2.page?WO_NO=" + mgr.URLEncode(sWoNo);
	    else
	       sConfirmedUrlString = "ActiveSeparate2ServiceManagement.page?WO_NO=" + mgr.URLEncode(sWoNo);

	    sConfirmedNewWinHandle = "modifiedwoform";
            connectTnF(sWoNo,sConfirmationText,sConfirmedUrlString,sConfirmedNewWinHandle);
        }
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

    private void connectStandardJobs()
    {

        headset.changeRow();
        ASPBuffer headsetValues = headset.getRow();
        ctx.writeBuffer("HEADDATA",headsetValues);
        sDlgName="ConnectStdJob";
        okFindITEM5();


    }

    private void additionalQualifications()
    {

       ASPManager mgr = getASPManager();
       headset.changeRow();
       ASPBuffer headsetValues = headset.getRow();
       String calling_url = mgr.getURL();
       ctx.setGlobal("CALLING_URL", calling_url);
       if (mgr.isEmpty(sSimpleSearchSeq))
       {
          cmd = trans.addCustomFunction("SSEQ","Resource_Booking_Util_API.Get_Next_Simple_Search_Seq","SIMPLE_SEARCH_SEQ");
          trans = mgr.perform(trans);
          sSimpleSearchSeq = trans.getValue("SSEQ/DATA/SIMPLE_SEARCH_SEQ");
       }
       ctx.setGlobal("SIMPLESEARCHSEQ", sSimpleSearchSeq);
       if (itemset1.countRows()>0)
           ctx.setGlobal("QUERYSEQ",mgr.isEmpty(itemset1.getValue("QUERY_SEQ"))?"":itemset1.getValue("QUERY_SEQ"));
       ASPBuffer addAllValues = mgr.newASPBuffer();;
       addAllValues.addBuffer("HEAD",headsetValues);
       bOpenNewWindow = true;
       urlString= createTransferUrl("SearchQualificationsDlg.page?&RES_TYPE=HEADQUAL"+
                    "&SIMPLE_SEARCH_SEQ="+sSimpleSearchSeq,addAllValues);
                            
       newWinHandle = "frmSearchEngine"; 
    }
    
    public void lineAddQualifications()
    {
      ASPManager mgr = getASPManager();
      ASPBuffer data_buff;
      ASPBuffer rowBuff;
      int count;
      headset.changeRow();
      ASPBuffer headsetValues = headset.getRow();
      headsetValues.setFieldItem("CBADDQUALIFICATION",sQual);
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);
      ctx.setGlobal("QUERYSEQ", itemset1.getValue("QUERY_SEQ"));
      if (mgr.isEmpty(sSimpleSearchSeq))
      {
         trans.clear();
         cmd = trans.addCustomFunction("SSEQ","Resource_Booking_Util_API.Get_Next_Simple_Search_Seq","SIMPLE_SEARCH_SEQ");
         trans = mgr.perform(trans);
         sSimpleSearchSeq = trans.getValue("SSEQ/DATA/SIMPLE_SEARCH_SEQ");
      }
      ctx.setGlobal("SIMPLESEARCHSEQ",sSimpleSearchSeq );
      if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.setFilterOn();
            count = itemset1.countSelectedRows();
        }
        else
        {
            itemset1.unselectRows();
            count = 1;
        }

        data_buff = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
           rowBuff = mgr.newASPBuffer();
          
           if (i == 0)
           {   
               rowBuff.addItem("ROW_NO", itemset1.getValue("ROW_NO"));
               rowBuff.addItem("ROW_NO", itemset1.getValue("ROW_NO"));
           }
           else
           {
               rowBuff.addItem(null, itemset1.getValue("ROW_NO"));
               rowBuff.addItem(null, itemset1.getValue("ROW_NO"));
           }

           data_buff.addBuffer("DATA", rowBuff);
           
           if (itemlay1.isMultirowLayout())
               itemset1.next();
       }

        if (itemlay1.isMultirowLayout())
           itemset1.setFilterOff();
      
      ASPBuffer addAllValues = mgr.newASPBuffer();
      addAllValues.addBuffer("HEAD",headsetValues);
      addAllValues.addBuffer("ROWS",data_buff);
      bOpenNewWindow = true;
      urlString= createTransferUrl("SearchQualificationsDlg.page?&SIMPLE_SEARCH_SEQ="+sSimpleSearchSeq+"&QUERY_SEQ="+ (mgr.isEmpty(itemset1.getValue("QUERY_SEQ"))?"":itemset1.getValue("QUERY_SEQ")),addAllValues);

      newWinHandle = "frmSearchEngine"; 
    }


    private void cancelWoDlg()
    {
        ASPManager mgr = getASPManager();

        sDlgName = "";
        itemset4.clear();
    }

    public void connectTnF(String sNewWoNo, String sConTxt, String sConUrl, String sConWin)
    {
        ASPManager mgr = getASPManager();
        boolean bTnFExist = false;
        String toolType = "TOOLANDFACILITY";

        trans.clear();
        cmd = trans.addCustomFunction("GETTOOLTYPE","Res_Type_API.Decode","DUMMY1");
        cmd.addParameter("DUMMY2",toolType);
        trans = mgr.validate(trans);
        String sToolType = trans.getValue("GETTOOLTYPE/DATA/DUMMY1");

        if (itemlay2.isMultirowLayout())
        {
            itemset2.goTo(itemset2.getRowSelected());
            this.okFindITEM3();
        }

        for (int i=0;i<itemset3.countRows();i++)
        {
            itemset3.goTo(i);
            if (sToolType.equals(itemset3.getRow().getValue("RESOURCE_TYPE")))
            {
                bTnFExist = true;
                break;
            }
        }

        if (bTnFExist)
        {
            trans.clear();
            cmd = trans.addCustomFunction("GETROLE","Active_Work_Order_API.Has_Role","DUMMY1");
            cmd.addParameter("DUMMY2",sNewWoNo);
            trans = mgr.validate(trans);
            String sHasRole = trans.getValue("GETROLE/DATA/DUMMY1");

            if ("TRUE".equals(sHasRole))
            {
                bOpenNewWindow = true;
                urlString = "ConnectTnFDlg.page?WO_NO=" + mgr.URLEncode(sNewWoNo)+
                            "&CONTXT="+sConTxt+"&CONURL="+sConUrl+"&CONWIN="+sConWin;
                newWinHandle = "ConnectTnF"; 
            }
        }
    }

    public void deleteITEM1()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer headsetValues = headset.getRow();
        headset.clear();

        clearSearchResults();

        if (itemlay1.isMultirowLayout())
            itemset1.goTo(itemset1.getRowSelected());

        boolean bproceedWithDelete = checkOrderSequenceForDeleteAndEdit("Delete");

        if (bproceedWithDelete)
        {
            trans.clear();
            itemset1.setSelectedRowsRemoved();
            mgr.submit(trans);
        }

        iCombinedNoOfRows = itemset1.countRows();

        if (iCombinedNoOfRows == 0)
        {
            bDisableSiteField = false;
            headsetValues.setValue("CONTRACT", sOriginalSite);
        }

        setRowBack(headsetValues);
    }

    public void saveReturnITEM1()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer headsetValues = headset.getRow();
        headset.clear();

        clearSearchResults();

        if (checkOrderSequenceForDeleteAndEdit("SaveReturn"))
        {
            trans.clear();
            mgr.submit(trans);
        }

        iCombinedNoOfRows = itemset1.countRows();

        if (iCombinedNoOfRows == 0)
        {
            bDisableSiteField = false;
            headsetValues.setValue("CONTRACT", sOriginalSite);
        }
            setRowBack(headsetValues); 
    }

    private void clearSearchResults()
    {
        ASPManager mgr = getASPManager();

        // This code block is called when a simple search is perfomred and the user wishes to 
        // go for a combined serach afterwards.
        if (itemset2.countRows() > 0)
        {
            itemset2.clear();
            itemset3.clear();
            tabs.setActiveTab(1);
            itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
        }
    }

    private boolean checkOrderSequenceForDeleteAndEdit(String sOperationMode)
    {
        ASPManager mgr = getASPManager();

        String sCurrOrdNo;
        int iCurrOrdNo;
        int iMaxOrderNo;
        int count = 0;
        int currRowNo = 0;
        ASPBuffer newRowValues = mgr.newASPBuffer();

        if ("SaveReturn".equals(sOperationMode))
        {
            count = itemset1.countRows();
            currRowNo =itemset1.getCurrentRowNo();
            itemset1.changeRow();
            newRowValues = itemset1.getRow();
        }
        else if ("Delete".equals(sOperationMode))
        {
            itemset1.switchSelections();
            itemset1.setFilterOn();
            count = itemset1.countSelectedRows();
        }

        iMaxOrderNo = 0;

        for (int iLoopCounter = 1; iLoopCounter <= count; iLoopCounter++)
        {
            sCurrOrdNo = itemset1.getValue("ORDER_NO");
            iCurrOrdNo = Integer.parseInt(mgr.isEmpty(sCurrOrdNo)?"0":sCurrOrdNo);

            if (iCurrOrdNo > iMaxOrderNo)
                iMaxOrderNo = iCurrOrdNo;

            itemset1.next();
        }

        boolean bSequenceFollowed = true;

        for (int iOuterLoopCounter = 1; iOuterLoopCounter <= iMaxOrderNo; iOuterLoopCounter++)
        {
            itemset1.first();
            bSequenceFollowed = false;

            for (int iInnerLoopCounter = 1; iInnerLoopCounter <= count; iInnerLoopCounter++)
            {
                sCurrOrdNo = itemset1.getValue("ORDER_NO");
                iCurrOrdNo = Integer.parseInt(mgr.isEmpty(sCurrOrdNo)?"0":sCurrOrdNo);

                if (iOuterLoopCounter == iCurrOrdNo)
                {
                    bSequenceFollowed = true;
                    break;
                }

                itemset1.next();
            }

            if (!bSequenceFollowed)
                break;
        }

        if ("SaveReturn".equals(sOperationMode))
        {
            itemset1.goTo(currRowNo);

            if (!bSequenceFollowed)
                itemset1.resetRow();
        }
        else if ("Delete".equals(sOperationMode))
        {
            itemset1.first();
            itemset1.setFilterOff();
            itemset1.switchSelections();
        }

        if (!bSequenceFollowed)
        {
            mgr.showAlert("PCMWFREETSEARCHORDSEQMAINT: Order No(s) must be in a sequence.");
            return false;
        }
        else
            return true;
    }

    private boolean checkOrderSequenceForNew()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addQuery("GETMAXORDNO","SELECT MAX(ORDER_NO) NDUMMY FROM COMBINE_SEARCH_LINES WHERE QUERY_SEQ = ?");
        cmd.addParameter("QUERY_SEQ",sSearchSeq);
	trans = mgr.perform(trans);

        String sMaxOrderNo = trans.getValue("GETMAXORDNO/DATA/NDUMMY");

        int iMaxOrderNo = Integer.parseInt(mgr.isEmpty(sMaxOrderNo)?"0":sMaxOrderNo);
        String sCurrOrderNo;
        int iCurrOrderNo;

        sCurrOrderNo = itemset1.getValue("ORDER_NO");
        iCurrOrderNo = Integer.parseInt(mgr.isEmpty(sCurrOrderNo)?"0":sCurrOrderNo);

        if ((iMaxOrderNo + 1) < iCurrOrderNo)
        {
            mgr.showAlert("PCMWFREETSEARCHORDSEQMAINT: Order No(s) must be in a sequence.");
            return false;
        }
        else
            return true;
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();   

        f = headblk.addField("COMPANY");
        f.setHidden();    
        
        f = headblk.addField("HEAD_WO_NO");
        f.setHidden();    
        
        f = headblk.addField("CONTRACT");
        f.setSize(5);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setMaxLength(5);
        f.setLabel("PCMWFREETSEARCHENGCONTRACT: Site");
        f.setUpperCase();
        f.setMandatory();
        f.setCustomValidation("CONTRACT,EMPLOYEE","COMPANY,SITE_DESCRIPTION,EMPLOYEE_NAME");
        f.setValidateFunction("handleButtons");

        f = headblk.addField("SITE_DESCRIPTION");
        f.setSize(20);
        f.setMaxLength(20);
        f.setFunction("Site_API.Get_Description(:CONTRACT)");
        f.setReadOnly();

        f = headblk.addField("ORG_CODE");
        f.setSize(8);
        f.setMaxLength(8);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
        f.setLabel("PCMWFREETSEARCHENGORGCODE: Maintenance Organization");
        f.setCustomValidation("CONTRACT,ORG_CODE","ORG_DESCRIPTION");
        f.setUpperCase();

        f = headblk.addField("ORG_DESCRIPTION");
        f.setSize(20);
        f.setMaxLength(40);
        f.setFunction("Organization_API.Get_Description(:CONTRACT,:ORG_CODE)");
        f.setReadOnly();

        f = headblk.addField("EARLIEST_DATE_TIME","Datetime");
        f.setSize(20);
        f.setMandatory();
        f.setLabel("PCMWFREETSEARCHEARLIESTDATETIME: Earliest Date/Time");
        f.setCustomValidation("EARLIEST_DATE_TIME,LATEST_DATE_TIME,EXECUTION_TIME","LATEST_DATE_TIME");
        f.setValidateFunction("handleButtons");

        f = headblk.addField("LATEST_DATE_TIME","Datetime");
        f.setSize(25);
        f.setMandatory();
        f.setLabel("PCMWFREETSEARCHLATESTDATETIME: Latest Date/Time");
        f.setCustomValidation("EARLIEST_DATE_TIME,LATEST_DATE_TIME,EXECUTION_TIME","LATEST_DATE_TIME");
        f.setValidateFunction("handleButtons");

        f = headblk.addField("EXECUTION_TIME","Number");
        f.setSize(10);
        f.setMandatory();
        f.setLabel("PCMWFREETSEARCHEXECUTIONTIME: Execution Time");
        f.setCustomValidation("EARLIEST_DATE_TIME,LATEST_DATE_TIME,EXECUTION_TIME","LATEST_DATE_TIME");
        f.setValidateFunction("handleButtons");

        f = headblk.addField("EXCLUDE_SCHEDULE","Number");
        f.setCheckBox("0,1");
        f.setLabel("PCMWFREETSEARCHEXECLUSCHED: Exclude Schedule");

        f = headblk.addField("CBSHOWALL");
        f.setLabel("PCMWFREETIMESEARCHENGINECBSHOWALL: Show All");
        f.setCheckBox("0,1");
        
        f = headblk.addField("RESOURCE_TYPE");
        f.enumerateValues("RES_TYPE_API");
        f.setSelectBox();
        f.setMandatory();
        f.setLabel("PCMWFREETSEARCHRESOURCETYPE: Resource Type");
        f.setValidateFunction("handleFieldStatus");

        f = headblk.addField("CBADDQUALIFICATION").
        setLabel("PCMWFREETIMESEARCHENGINECBADDQUAL: Additional Qualifications").
        setCheckBox("FALSE,TRUE").
        setQueryable();

        //Bug 71867, Start, Modified validation
        f = headblk.addField("COMPETENCE_GROUP");
        f.setDynamicLOV("BELONGS_TO_ROLE_LOV","CONTRACT SITE",600,445);
        f.setLabel("PCMWFREETSEARCHRCOMPGRP: Craft Group");
        f.setSize(10);
        f.setUpperCase();
        f.setMaxLength(10);
        f.setCustomValidation("COMPETENCE_GROUP,COMPETENCE,CONTRACT","COMPETENCE_GRP_DESCRIPTION,COMPETENCE,COMPETENCE_DESCRIPTION");
        //Bug 71867, End

        f = headblk.addField("COMPETENCE_GRP_DESCRIPTION");
        f.setFunction("Role_API.Get_Description(:COMPETENCE_GROUP)");
        f.setReadOnly();

        //Bug 71867, Start, Modified validation
        f = headblk.addField("COMPETENCE");
        f.setLOV("../mscomw/RoleToSiteLov.page","CONTRACT",600,445);
        f.setLabel("PCMWFREETSEARCHCOMPETENCE: Craft");
        f.setSize(10);
        f.setCustomValidation("COMPETENCE,COMPETENCE_GROUP,CONTRACT","COMPETENCE,COMPETENCE_DESCRIPTION,COMPETENCE_GROUP,COMPETENCE_GRP_DESCRIPTION");
        f.setUpperCase();
        f.setMaxLength(10);
        //Bug 71867, End

        f = headblk.addField("COMPETENCE_DESCRIPTION");
        f.setFunction("Role_API.Get_Description(:COMPETENCE");
        f.setReadOnly();

        f = headblk.addField("EMPLOYEE");
        f.setDynamicLOV("EMPLOYEE_NO","CONTRACT,ORG_CODE",600,445);
        f.setLabel("PCMWFREETSEARCHEMPLOYEE: Employee");
        f.setCustomValidation("CONTRACT,EMPLOYEE","EMPLOYEE_NAME");
        f.setSize(20);
        f.setMaxLength(40);

        f = headblk.addField("EMPLOYEE_NAME");
        f.setFunction("Employee_API.Get_Name(Site_API.Get_Company(:CONTRACT),:EMPLOYEE)");
        f.setReadOnly();

        //Bug 71867, Start, Modified validation
        f = headblk.addField("TOOL_FACILITY_TYPE");
        f.setDynamicLOV("TOOL_FACILITY_TYPE",600,445);
        f.setLabel("PCMWFREETSEARCHTOOLFACILITYTYPE: Tool and Facility Type");
        f.setSize(20);
        f.setMaxLength(40);
        f.setUpperCase();
        f.setCustomValidation("TOOL_FACILITY_TYPE,TOOL_FACILITY_ID","TOOL_FACILITY_TYPE_DESC,TOOL_FACILITY_ID,TOOL_FACILITY_DESC");
        //Bug 71867, End

        f = headblk.addField("TOOL_FACILITY_TYPE_DESC");
        f.setFunction("Tool_Facility_Type_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
        f.setReadOnly();

        //Bug 71867, Start, Modified validation
        f = headblk.addField("TOOL_FACILITY_ID");
        f.setDynamicLOV("TOOL_FACILITY","CONTRACT,TOOL_FACILITY_TYPE",600,445);
        f.setLabel("PCMWFREETSEARCHTOOLFACILITYID: Tool and Facility ID");
        f.setSize(20);
        f.setMaxLength(40);
        f.setUpperCase();
        f.setCustomValidation("TOOL_FACILITY_TYPE,TOOL_FACILITY_ID,CONTRACT,ORG_CODE","TOOL_FACILITY_DESC,TOOL_FACILITY_TYPE,TOOL_FACILITY_TYPE_DESC");
        //Bug 71867, End

	f = headblk.addField("TOOL_FACILITY_DESC");
        f.setFunction("Tool_Facility_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
        f.setReadOnly();

        f = headblk.addField("ORDER_NO");
        f.setLabel("PCMWFREETSEARCHSEQNO: Sequence");
        f.setSize(10);

        f = headblk.addField("INFO");
        f.setHidden();

        f = headblk.addField("NDUMMY");
        f.setHidden();

        f = headblk.addField("ROW_NO");
        f.setHidden();
        
        f = headblk.addField("SIMPLE_SEARCH_SEQ","Number");
        f.setHidden();

        f = headblk.addField("NULL","Number");
        f.setHidden();

        f = headblk.addField("FND_USER");
        f.setHidden();

        f = headblk.addField("DUMMY", "String");
        f.setHidden();
        f.setFunction("''");

        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);
        headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWFREETIMESEARCHENGINEFTS: Free Time Search"));    

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setEditable();
        
        headlay.defineGroup(mgr.translate("PCMWFREETSEARCHENGWHTOSEARCH: Where to Search"),"CONTRACT,SITE_DESCRIPTION,ORG_CODE,ORG_DESCRIPTION", true, true);
        headlay.defineGroup(mgr.translate("PCMWFREETSEARCHENGSEACHTINTE: Search Time Interval"),"EARLIEST_DATE_TIME,LATEST_DATE_TIME,EXECUTION_TIME,EXCLUDE_SCHEDULE,CBSHOWALL", true, true);
        headlay.defineGroup(mgr.translate("PCMWFREETSEARCHENGRESINFORMA: Resource Information"),"RESOURCE_TYPE,CBADDQUALIFICATION,COMPETENCE_GROUP,COMPETENCE_GRP_DESCRIPTION,COMPETENCE,COMPETENCE_DESCRIPTION,EMPLOYEE,EMPLOYEE_NAME,TOOL_FACILITY_TYPE,TOOL_FACILITY_TYPE_DESC,TOOL_FACILITY_ID,TOOL_FACILITY_DESC,ORDER_NO", true, true);
        
        headbar.addCustomCommand("activateSearchParam","");
        headbar.addCustomCommand("activateResults","");

        headlay.setSimple("SITE_DESCRIPTION");
        headlay.setSimple("ORG_DESCRIPTION");
        headlay.setSimple("COMPETENCE_GRP_DESCRIPTION");
        headlay.setSimple("COMPETENCE_DESCRIPTION");
        headlay.setSimple("EMPLOYEE_NAME");
        headlay.setSimple("TOOL_FACILITY_TYPE_DESC");
        headlay.setSimple("TOOL_FACILITY_DESC");

        headbar.disableMinimize();
        headblk.disableDocMan();

        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWFREETSEARCHENGSEARCHPARAM: Search Parameters"), "javascript:commandSet('HEAD.activateSearchParam', '')");
        tabs.addTab(mgr.translate("PCMWFREETSEARCHENGRESULTS: Results"), "javascript:commandSet('HEAD.activateResults', '')");

        // Complex Search Block
        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk1.addField("ITEM1_QUERY_SEQ","Number");
        f.setDbName("QUERY_SEQ");
        f.setHidden();

        f = itemblk1.addField("ITEM1_CONTRACT");
        f.setDbName("CONTRACT");
        f.setHidden();

        f = itemblk1.addField("ITEM1_ROW_NO","Number");
        f.setDbName("ROW_NO");
        f.setLabel("PCMWFREETSEARCHITEM1ROWNO: Row No");
        
        f = itemblk1.addField("ITEM1_ORDER_NO","Number");
        f.setDbName("ORDER_NO");
        f.setLabel("PCMWFREETSEARCHITEM1ORDERNO: Sequence");
        f.setSize(10);

        //Bug 71867, Start, Modified validation
        f = itemblk1.addField("ITEM1_RESOURCE_ID");
        f.setDbName("RESOURCE_ID");
        f.setDynamicLOV("EMPLOYEE_NO","ITEM1_CONTRACT CONTRACT,ITEM1_ORG_CODE ORG_CODE",600,445);
        f.setLabel("PCMWFREETSEARCHITEM1RESID: Resource ID");
        f.setSize(20);
        f.setMaxLength(40);
        f.setCustomValidation("ITEM1_RESOURCE_ID,ITEM1_TOOL_FACILITY_TYPE,ITEM1_ORG_CODE,ITEM1_CONTRACT,ITEM1_RESOURCE_TYPE_DB","ITEM1_RESOURCE_ID,ITEM1_TOOL_FACILITY_TYPE");
        f.setUpperCase();
        //Bug 71867, End

        f = itemblk1.addField("RESOURCE_DESCRIPTION");
        f.setLabel("PCMWFREETSEARCHRESDESCR: Description");
        f.setFunction("Resource_Booking_Util_API.Get_Resource_Description(:ITEM1_CONTRACT,:ITEM1_RESOURCE_ID,:ITEM1_RESOURCE_TYPE)");
        f.setReadOnly();

        //Bug 71867, Start, Modified validation
        f = itemblk1.addField("ITEM1_ROLE_CODE");
        f.setDbName("ROLE_CODE");
        f.setDynamicLOV("ROLE_TO_SITE_LOV","CONTRACT",600,445);
        f.setLabel("PCMWFREETSEARCHITEM1COMPTETENCE: Craft");
        f.setSize(10);
        f.setMaxLength(10);
        f.setUpperCase();
        f.setCustomValidation("ITEM1_ROLE_CODE,ITEM1_ROLE_GROUP,ITEM1_CONTRACT","ITEM1_ROLE_CODE,ITEM1_ROLE_GROUP");
        //Bug 71867, End

        f = itemblk1.addField("ITEM1_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM1_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWFREETSEARCHITEM1MAINTORG: Maintenance Organization");
        f.setSize(8);
        f.setMaxLength(8);
        f.setUpperCase();
        f.setCustomValidation("ITEM1_ORG_CODE,ITEM1_CONTRACT","ITEM1_ORG_CODE");

        f = itemblk1.addField("ITEM1_HOURS","Number");
        f.setDbName("HOURS");
        f.setLabel("PCMWFREETSEARCHITEM1EXECTIME: Execution Time");
        f.setSize(5);

        f = itemblk1.addField("ITEM1_RESOURCE_TYPE");
        f.setDbName("RESOURCE_TYPE");
        f.setLabel("PCMWFREETSEARCHITEM1RESTYPE: Resource Type");
        f.setReadOnly();

        f = itemblk1.addField("ITEM1_RESOURCE_TYPE_DB");
        f.setDbName("RESOURCE_TYPE_DB");
        f.setHidden();

        //Bug 71867, Start, Modified validation
        f = itemblk1.addField("ITEM1_ROLE_GROUP");
        f.setDbName("ROLE_GROUP");
        f.setDynamicLOV("BELONGS_TO_ROLE_LOV","CONTRACT SITE",600,445);
        f.setLabel("PCMWFREETSEARCHITEM1COMP_GRP: Craft Group");
        f.setSize(10);
        f.setMaxLength(10);
        f.setCustomValidation("ITEM1_ROLE_GROUP,ITEM1_CONTRACT,ITEM1_ROLE_CODE","ITEM1_ROLE_GROUP,ITEM1_ROLE_CODE");
        f.setUpperCase();

        f = itemblk1.addField("ITEM1_TOOL_FACILITY_TYPE");
        f.setDbName("TOOL_FACILITY_TYPE");
        f.setDynamicLOV("TOOL_FACILITY_TYPE",600,445);
        f.setLabel("PCMWFREETSEARCHITEM1TOOFACTYPE: Tool Facility Type");
        f.setSize(20);
        f.setMaxLength(40);
        f.setUpperCase();
        f.setCustomValidation("ITEM1_TOOL_FACILITY_TYPE,ITEM1_RESOURCE_ID","ITEM1_TOOL_FACILITY_TYPE,ITEM1_RESOURCE_ID,RESOURCE_DESCRIPTION");
        //Bug 71867, End

        f = itemblk1.addField("ITEMCBADDQUALIFICATION").
        setLabel("PCMWFREETIMESEARCHENGINECBADDQUAL: Additional Qualifications").
        setCheckBox("FALSE,TRUE").
        setFunction("''").
        setQueryable();

        itemblk1.setView("COMBINE_SEARCH_LINES");
        itemblk1.defineCommand("COMBINE_SEARCH_LINES_API","New__,Modify__,Remove__");
        itemblk1.disableDocMan();

        itemset1 = itemblk1.getASPRowSet();
        
        itembar1 = mgr.newASPCommandBar(itemblk1);
        itembar1.disableCommand(itembar1.FIND);
        itembar1.disableCommand(itembar1.NEWROW);
        itembar1.disableCommand(itembar1.DUPLICATEROW);
        itembar1.disableMinimize();

        itembar1.defineCommand(itembar1.DELETE,"deleteITEM1");
        itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnITEM1");
        itembar1.addCustomCommand("lineAddQualifications",mgr.translate("PCMWFREETIMESEARCHENGINELINEADDQUALIFICATIONS: Additional Qualifications..."));
        itembar1.addCommandValidConditions("lineAddQualifications",   "RESOURCE_TYPE_DB",  "Enable",  "EMPLOYEE");
        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.enableRowSelect();
        itembar1.enableMultirowAction();
        itemtbl1.setWrap();

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);

        // Result Rank Block
        itemblk2 = mgr.newASPBlock("ITEM2");

        f = itemblk2.addField("ITEM2_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk2.addField("ITEM2_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();   

        f = itemblk2.addField("QUERY_SEQ","Number");
        f.setHidden();                

        f = itemblk2.addField("RANK","Number");
        f.setLabel("PCMWFREETSEARCHITEM2RANK: Rank");

        f = itemblk2.addField("DATE_TIME_FROM","Datetime");
        f.setLabel("PCMWFREETSEARCHITEM2DTFROM: Date Time From");

        f = itemblk2.addField("DATE_TIME_TO","Datetime");
        f.setLabel("PCMWFREETSEARCHITEM2DTTO: Date Time To");

        f = itemblk2.addField("ALLOCATED");
        f.setLabel("PCMWFREETIMESEARCHENGINEALLOCATED: Allocated");
        f.setCheckBox("FALSE,TRUE");
        f.setQueryable();
        f.setHidden();

        f = itemblk2.addField("DUMMY1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk2.addField("DUMMY2");
        f.setFunction("''");
        f.setHidden();

        itemblk2.setView("RESULT_RANK2");
        itemblk2.defineCommand("RESULT_RANK_API","");
        itemblk2.disableDocMan();

        itemset2 = itemblk2.getASPRowSet();

        itembar2 = mgr.newASPCommandBar(itemblk2);
        itembar2.disableCommand(itembar2.FIND);
        itembar2.disableMinimize();

        itemtbl2 = mgr.newASPTable(itemblk2);

        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);

        itembar2.addCustomCommand("createWorkOrder",mgr.translate("PCMWFREETSEARCHITEM2CREWO: Create Work Order..."));
        itembar2.addCustomCommand("addToWorkOrder",mgr.translate("PCMWFREETSEARCHITEM2ADDTOWO: Add To Work Order..."));
        itembar2.addCustomCommand("modifyWorkOrder",mgr.translate("PCMWFREETSEARCHITEM2MODIFYWO: Modify Work Order"));


        //itembar2.defineCommand("modifyWorkOrder","modifyWorkOrder","modifyWorkOrderClientMsg");

        // Result Detail Block

        itemblk3 = mgr.newASPBlock("ITEM3");

        f = itemblk3.addField("ITEM3_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk3.addField("ITEM3_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();   

        f = itemblk3.addField("ITEM3_QUERY_SEQ","Number");
        f.setDbName("QUERY_SEQ");
        f.setHidden(); 

        f = itemblk3.addField("ITEM3_RANK","Number");
        f.setDbName("RANK");
        f.setHidden(); 

        f = itemblk3.addField("ITEM3_ORDER_NO","Number");
        f.setDbName("ORDER_NO");
        f.setLabel("PCMWFREETSEARCHITEM3ORDERNO: Sequence");

        f = itemblk3.addField("RESOURCE_ID");
        f.setSize(20);
        f.setLabel("PCMWFREETSEARCHITEM3RESOURCEID: Resource ID");

        f = itemblk3.addField("ITEM3_RESOURCE_DESCRIPTION");
        f.setLabel("PCMWFREETSEARCHITEM3RESDESCR: Description");
        f.setFunction("Resource_Booking_Util_API.Get_Resource_Description(:ITEM3_CONTRACT,:RESOURCE_ID,:ITEM3_RESOURCE_TYPE)");
        f.setReadOnly();

        f = itemblk3.addField("ROLE_CODE");
        f.setSize(10);
        f.setLabel("PCMWFREETSEARCHITEM3ROLECODE: Craft");

        f = itemblk3.addField("ITEM3_RESOURCE_TYPE");
        f.setDbName("RESOURCE_TYPE");
        f.setSize(20);
        f.setLabel("PCMWFREETSEARCHITEM3RESOURCETYPE: Resource Type");

        f = itemblk3.addField("ITEM3_RESOURCE_TYPE_DB");
        f.setDbName("RESOURCE_TYPE_DB");
        f.setHidden();

        f = itemblk3.addField("ITEM3_CONTRACT");
        f.setDbName("CONTRACT");
        f.setSize(5);
        f.setLabel("PCMWFREETSEARCHITEM3CONTRACT: Site");

        f = itemblk3.addField("ITEM3_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setSize(8);
        f.setLabel("PCMWFREETSEARCHITEM3ORGCODE: Maintenance Organization");

        f = itemblk3.addField("PLANNED_HOURS","Number");
        f.setLabel("PCMWFREETSEARCHITEM3PLANNEDHOURS: Planned Hours");

        f = itemblk3.addField("ITEM3_DATE_TIME_FROM","Datetime");
        f.setDbName("DATE_TIME_FROM");
        f.setLabel("PCMWFREETSEARCHITEM3DATETIMEFROM: Date Time From");

        f = itemblk3.addField("ITEM3_DATE_TIME_TO","Datetime");
        f.setDbName("DATE_TIME_TO");
        f.setLabel("PCMWFREETSEARCHITEM3DATETIMETO: Date Time To");

        f = itemblk3.addField("ITEM3_CBALLOCATED");
        f.setLabel("PCMWFREETIMESEARCHENGINECBALLOCATED: Allocated");
        f.setDbName("ALLOCATED");
        f.setCheckBox("FALSE,TRUE");
        f.setQueryable();
        f.setHidden();

        itemblk3.setView("RESULT_DETAILS1");
        itemblk3.defineCommand("RESULT_DETAILS_API","");
        itemblk3.setMasterBlock(itemblk2);
        itemblk3.disableDocMan();

        itemset3 = itemblk3.getASPRowSet();

        itembar3 = mgr.newASPCommandBar(itemblk3);
        itembar3.disableMinimize();

        itemtbl3 = mgr.newASPTable(itemblk3);

        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);

        // Dlg Info Block.

        itemblk4 = mgr.newASPBlock("ITEM4");

        f = itemblk4.addField("ITEM4_COMPANY");
        f.setHidden();  

        f = itemblk4.addField("WO_NO");
        f.setLabel("PCMWFREETSEARCHITEM4WONO: WO No");
        f.setCustomValidation("WO_NO","ADDHEADER");
        f.setDynamicLOV("ACT_SEP_SEARCH_ENGINE_LOV","WO_SITE CONTRACT", 600, 445);

        f = itemblk4.addField("ADDHEADER");
        f.setLabel("PCMWFREETSEARCHADDHEADER: Add Header");
        f.setCheckBox("FALSE,TRUE");
                
        f = itemblk4.addField("ITEM4_EARLIEST_DATE_TIME","Datetime");
        f.setSize(20);
        f.setReadOnly();
        f.setLabel("PCMWALLOCATEEMPLOYEESEDATETIME: Date From");
             
        f = itemblk4.addField("ITEM4_LATEST_DATE_TIME","Datetime");
        f.setSize(25);
        f.setLabel("PCMWALLOCATEEMPLOYEESLDATETIME: Date To");

        f = itemblk4.addField("ITEM4_EXECUTION_TIME","Number");
        f.setSize(10);
        f.setLabel("PCMWALLOCATEEMPLOYEESEXECTIME: Execution Time");

        f = itemblk4.addField("WO_SITE");
        f.setLabel("PCMWFREETSEARCHITEM4WOSITE: WO Site");
        f.setReadOnly();

        f = itemblk4.addField("REPORTED_BY");
        f.setLabel("PCMWFREETSEARCHITEM4REPORTEDBY: Reported By");
        f.setDynamicLOV("EMPLOYEE_LOV","ITEM4_COMPANY COMPANY", 600, 445);
        f.setUpperCase();

        f = itemblk4.addField("MAINT_ORG");
        f.setLabel("PCMWFREETSEARCHITEM4MAINTORG: Maintenance Organization");
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","WO_SITE CONTRACT", 600, 445);

        f = itemblk4.addField("MAINT_ORG_DESC");
        f.setReadOnly();
        mgr.getASPField("MAINT_ORG").setValidation("Organization_API.Get_Description","WO_SITE,MAINT_ORG","MAINT_ORG_DESC");

        f = itemblk4.addField("DIRECTIVE");
        f.setLabel("PCMWFREETSEARCHITEM4DIRECTIVE: Directive");

        itemblk4.setView("DUAL");
        itemblk4.disableDocMan();

        itemset4 = itemblk4.getASPRowSet();

        itembar4 = mgr.newASPCommandBar(itemblk4);
        itembar4.disableMinimize();

        itemtbl4 = mgr.newASPTable(itemblk4);

        itemlay4 = itemblk4.getASPBlockLayout();
        itemlay4.defineGroup("","WO_NO,ADDHEADER,WO_SITE,REPORTED_BY,MAINT_ORG,MAINT_ORG_DESC,DIRECTIVE",true,true);
        itemlay4.defineGroup(mgr.translate("PCMWFREEETIMESEARCHGP1: Operation"),"ITEM4_EARLIEST_DATE_TIME,ITEM4_LATEST_DATE_TIME,ITEM4_EXECUTION_TIME",true,true);
        itemlay4.setDefaultLayoutMode(itemlay4.CUSTOM_LAYOUT);
        itemlay4.setEditable();

        itemlay4.setSimple("MAINT_ORG_DESC");
        

        ////////////// Connect StdJob Dialogue////////////////

        itemblk5=mgr.newASPBlock("ITEM5");

        itemblk5.addField("ITEM5_OBJID").setDbName("OBJID").setHidden();
        itemblk5.addField("ITEM5_OBJVERSION").setDbName("OBJVERSION").setHidden();
        itemblk5.addField("JOB_CONTRACT").setDbName("CONTRACT").setLabel("PCMWCONNSTDJOBSCONTRACT: Site");
        itemblk5.addField("STD_JOB_ID").setLabel("PCMWCONNSTDJOBSID: Standard Job");
        itemblk5.addField("DEFINITION").setLabel("PCMWCONNSTDJOBSDEF: Definision");
        itemblk5.addField("STD_JOB_REVISION").setLabel("PCMWCONNSTDJOBSREV: Revision");
        itemblk5.addField("STATE").setLabel("PCMWCONNSTDJOBSSTAT: State");
        itemblk5.addField("TYPE_ID").setLabel("PCMWCONNSTDJOBSTYPE: Type");
        itemblk5.addField("ACTION_CODE_ID").setLabel("PCMWCONNSTDJOBSACTION: Action");
        itemblk5.addField("STD_JOB_CONTRACT").setFunction("''").setHidden();
        itemblk5.addField("QRY_SEQ").setFunction("''").setHidden();
        itemblk5.setView("SEPARATE_STANDARD_JOB");

        itemset5=itemblk5.getASPRowSet();
        itembar5=mgr.newASPCommandBar(itemblk5);

        itembar5.disableCommand(itembar5.OKFIND);
        itemlay5=itemblk5.getASPBlockLayout();
        itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);

        itemtbl5=mgr.newASPTable(itemblk5);
        itemtbl5.enableRowSelect();
        itemtbl5.disableRowCounter();


        //////////////////////////////////////////////////////

        //======================== start - CALLC ==============================/

        itemblk6 = mgr.newASPBlock("ITEM6");

        f = itemblk6.addField("CALLC_CONTRACT");
        f.setLabel("PCMWFREETSEARCHITEM6WOSITE: WO Site:");
        f.setReadOnly();

        f = itemblk6.addField("CALLC_REPORTED_BY");
        f.setLabel("PCMWFREETSEARCHITEM6REPORTEDBY: Reported By:");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk6.addField("CALLC_MAINT_ORG");
        f.setLabel("PCMWFREETSEARCHITEM6MAINTORG: Maint. Org:");
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CALLC_CONTRACT CONTRACT", 600, 445);
        f.setValidation("Organization_API.Get_Description","CALLC_CONTRACT,CALLC_MAINT_ORG","CALLC_MAINT_ORG_DESC");

        f = itemblk6.addField("CALLC_MAINT_ORG_DESC");
        f.setReadOnly();

        f = itemblk6.addField("CALLC_DIRECTIVE");
        f.setLabel("PCMWFREETSEARCHITEM6DIRECTIVE: Directive:");
        f.setMaxLength(60);

        f = itemblk6.addField("CALLC_CUSTOMER_NO");
        f.setLabel("PCMWFREETSEARCHITEM6CUSTOMERNO: Customer No:");
        f.setReadOnly();

        f = itemblk6.addField("CALLC_REFERENCE");
        f.setLabel("PCMWFREETSEARCHITEM6REFERENCE: Reference No:");

        f = itemblk6.addField("CALLC_DUMMY");
        f.setHidden();
        f = itemblk6.addField("CALLC_CASE_ID");
        f.setHidden();
        f = itemblk6.addField("CALLC_TASK_ID");
        f.setHidden();
        f = itemblk6.addField("CALLC_HANDOVER");
        f.setHidden();


        itemblk6.setView("DUAL");
        itemblk6.disableDocMan();

        itemset6 = itemblk6.getASPRowSet();

        itembar6 = mgr.newASPCommandBar(itemblk6);
        itembar6.disableMinimize();

        itemtbl6 = mgr.newASPTable(itemblk6);

        itemlay6 = itemblk6.getASPBlockLayout();
        itemlay6.setDefaultLayoutMode(itemlay6.CUSTOM_LAYOUT);
        itemlay6.setEditable();

        itemlay6.setSimple("CALLC_MAINT_ORG_DESC");

        //--------------------------------------------------------------------------------------------
        // Dlg Modify Block.
        itemblk7 = mgr.newASPBlock("ITEM7");
              
        f = itemblk7.addField("ITEM7_EARLIEST_DATE_TIME","Datetime");
        f.setSize(20);
        f.setReadOnly();
        f.setLabel("PCMWFREETIMESEARCHEDATETIME: Date From");
                
        f = itemblk7.addField("ITEM7_LATEST_DATE_TIME","Datetime");
        f.setSize(25);
        f.setLabel("PCMWFREETIMESEARCHLDATETIME: Date To");
               
        f = itemblk7.addField("ITEM7_EXECUTION_TIME","Number");
        f.setSize(10);
        f.setLabel("PCMWFREETIMESEARCHEXECTIME: Execution Time");
                
                               
        itemblk7.setView("DUAL");
        itemblk7.disableDocMan();

        itemset7 = itemblk7.getASPRowSet();
        itembar7 = mgr.newASPCommandBar(itemblk7);
        itembar7.disableMinimize();

        itemtbl7 = mgr.newASPTable(itemblk7);
        itemlay7 = itemblk7.getASPBlockLayout();
        itemlay7.setDefaultLayoutMode(itemlay7.CUSTOM_LAYOUT);
        itemlay7.setEditable();
        
        //======================== end - CALLC ==============================/

        enableConvertGettoPost();

    }

    public void refreshChild()
    {
        ASPManager mgr = getASPManager();
        trans.clear();
        ASPBuffer head=ctx.readBuffer("HEADDATA");

        String sContract=null;
        int count=itemset1.countRows(); 
        if (mgr.isEmpty(headset.getValue("CONTRACT")))
        {
         for (int i=0;i<count;i++)
         {
          if (!mgr.isEmpty(itemset1.getValue("CONTRACT")))
             sContract=itemset1.getValue("CONTRACT");
         }

            head.setValue("CONTRACT",sContract);
        }

         setRowBack(head);
         
         buf = mgr.newASPBuffer();
              
         itemset1.first();
         for (int i=0; i<count; i++)
          {
             cmd=trans.addCustomFunction("ADDLINE"+i,"Resource_Booking_Util_Api.Has_Qualifications","ITEMCBADDQUALIFICATION");
             cmd.addInParameter("QUERY_SEQ",itemset1.getValue("QUERY_SEQ"));
             cmd.addInParameter("ROW_NO",itemset1.getValue("ROW_NO"));
             cmd.addInParameter("SIMPLE_SEARCH_SEQ",headset.getValue("SIMPLE_SEARCH_SEQ"));
             buf.setFieldItem("ROW_NO",itemset1.getValue("ROW_NO"));
             itemset1.next();
          }
          trans=mgr.perform(trans);
          itemset1.first();
          for (int i=0; i<count; i++)
          {
              buf = itemset1.getRow();
              String cbQualifications = trans.getValue("ADDLINE"+i+"/DATA/ITEMCBADDQUALIFICATION");
              buf.setValue("ITEMCBADDQUALIFICATION",cbQualifications);
              itemset1.setRow(buf);
              itemset1.next();
          }
          
    }

    public void activateSearchParam()
    {
        tabs.setActiveTab(1);
    }

    public void activateResults()
    {
        tabs.setActiveTab(2);
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (!mgr.buttonPressed("CONSTDJOBS"))
            if (!mgr.isEmpty(headset.getValue("RESOURCE_TYPE")))
                bneedToSetDefaultResource=false;

        headbar.removeCustomCommand("activateSearchParam");
        headbar.removeCustomCommand("activateResults");

        if (bCombinedSearchPerformed)
            itembar2.disableCommand("modifyWorkOrder");

        
        if (itemlay1.isEditLayout())
            tabs.setTabEnabled(2, false);
        else
            tabs.setTabEnabled(1, true);

        if (itemlay1.isEditLayout() && itemset1.countRows()>0)
        {
            if ("TOOLANDFACILITY".equals(itemset1.getRow().getValue("RESOURCE_TYPE_DB")))
            {
                mgr.getASPField("ITEM1_RESOURCE_ID").setDynamicLOV("TOOL_FACILITY","ITEM1_TOOL_FACILITY_TYPE TOOL_FACILITY_TYPE",600,445);
            }
        }

        //Bug 71866, Start
        if ("1".equals(sShowAll))
        {
            mgr.getASPField("ALLOCATED").unsetHidden();
            mgr.getASPField("ITEM3_CBALLOCATED").unsetHidden();
        }
        else
        {
            mgr.getASPField("ALLOCATED").setHidden();
            mgr.getASPField("ITEM3_CBALLOCATED").setHidden();
        }   
        //Bug 71866, End
   }



//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        if ("CreateWoForCallc".equals(sDlgName))
            return "PCMWFREETSEARCHCREWORKORDERVIARPDESC: Create Handover Work Order";
        else if ("CreateWo".equals(sDlgName))
            return "PCMWFREETSEARCHCREWORKORDER: Create Work Order";
        else if ("AddToWo".equals(sDlgName))
            return "PCMWFREETSEARCHADDTOWORKORDER: Add To Work Order";
        else if ("ConnectStdJob".equals(sDlgName))
            return "PCMWCONNSTDJOBSDES: Connect Standard Jobs";
        else
            return "PCMWFREETIMESEARCHENGINEFTS: Free Time Search";
    }

    protected String getTitle()
    {
        if ("CreateWoForCallc".equals(sDlgName))
            return "PCMWFREETSEARCHCREWORKORDERVIARPTITL: Create Handover Work Order";
        else if ("CreateWo".equals(sDlgName))
            return "PCMWFREETSEARCHCREWORKORDER: Create Work Order";
        else if ("AddToWo".equals(sDlgName))
            return "PCMWFREETSEARCHADDTOWORKORDER: Add To Work Order";
        else if ("ConnectStdJob".equals(sDlgName))
            return "PCMWCONNSTDJOBSTITLE: Connect Standard Jobs";
        else
            return "PCMWFREETIMESEARCHENGINEFTS: Free Time Search";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("TRANS_RES_EMP", sTranslatedEmpType);
        printHiddenField("TRANS_RES_TOOL", sTranslatedToolType);

        printHiddenField("REFRESH", "FALSE");
        printHiddenField("QUAL", sQual);
	printHiddenField("TEMP_RES_TYEP","");
        
        if ("CreateWo".equals(sDlgName))
        {
            appendToHTML(itemlay4.show());

            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML("<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            appendToHTML(fmt.drawSubmit("OKCREATE",mgr.translate("PCMWFREETIMESEARCHENGINEOKCREATE: OK"), ""));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML(fmt.drawSubmit("CANCELCREATE",mgr.translate("PCMWFREETSEARCHENGCANCELCREATE: Cancel"),""));
            appendToHTML("&nbsp;&nbsp;");
            appendToHTML("</td>\n</tr>\n</table>\n");

        }
        //=====================Start - CALLC =======================//
        else if ("CreateWoForCallc".equals(sDlgName))
        {
            appendToHTML(itemlay6.show());

            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML("<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            printSubmitButton("OKCREATE",mgr.translate("PCMWFREETIMESEARCHENGINEOKCREATE: OK"),"");
            appendToHTML("&nbsp;&nbsp;"); 
            printSubmitButton("CANCELCREATE",mgr.translate("PCMWFREETSEARCHENGCANCELCREATE: Cancel"),"");
            appendToHTML("&nbsp;&nbsp;");
            appendToHTML("</td>\n</tr>\n</table>\n");
        }
        //=====================End - CALLC =======================//

        else if ("AddToWo".equals(sDlgName))
        {
            appendToHTML(itemlay4.show());
            //appendDirtyJavaScript("      f.ADDHEADER.disabled = true;\n");
            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML("<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            appendToHTML(fmt.drawSubmit("OKADD",mgr.translate("PCMWFREETSEARCHENGOKADD:  OK "), ""));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML(fmt.drawSubmit("CANCELADD",mgr.translate("PCMWFREETSEARCHENGCANCELADD: Cancel"),""));
            appendToHTML("&nbsp;&nbsp;");
            appendToHTML("</td>\n</tr>\n</table>\n");
        }
        else if ("ModifyWo".equals(sDlgName))
        {
            appendToHTML(itemlay7.show());
            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML("<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            appendToHTML(fmt.drawSubmit("OKMODIFY",mgr.translate("PCMWALLOCATEEMPLOYEESENGOKMODIFY:  OK "), ""));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML(fmt.drawSubmit("CANCELMODIFY",mgr.translate("PCMWALLOCATEEMPLOYEESENGCANCELMODIFY: Cancel"),""));
            appendToHTML("&nbsp;&nbsp;");
            appendToHTML("</td>\n</tr>\n</table>\n");

       }
       else if ("ConnectStdJob".equals(sDlgName))
        {
            appendToHTML(itemlay5.show());

            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML("<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            appendToHTML(fmt.drawSubmit("OKSTDJOB",mgr.translate("PCMWCONNSTDJOBSOK:  OK "), ""));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML(fmt.drawSubmit("CANCELSTDJOB",mgr.translate("PCMWCONNSTDJOBSCANCEL: Cancel"),""));
            appendToHTML("&nbsp;&nbsp;");
            appendToHTML("</td>\n</tr>\n</table>\n");


        }
        else
        {
            appendToHTML(headlay.show());

            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML(            "<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            appendToHTML(fmt.drawSubmit("ADDQUAL",mgr.translate("PCMWFREETSEARCHENGADDQUALIFICATIONS: Additional Qualifications "), ""));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML(fmt.drawSubmit("CONSTDJOBS",mgr.translate("PCMWFREETSEARCHENGCONSTDJOBS: Add Standard jobs"), ""));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML(fmt.drawSubmit("ADDTOROW",mgr.translate("PCMWFREETSEARCHENGADDTOROW:  Add to Row "), ""));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML(fmt.drawSubmit("SEARCHRESOURCE",mgr.translate("PCMWFREETSEARCHENGSEARCH: Search"),""));
            appendToHTML("&nbsp;&nbsp;");
            //Bug 71861, Start
            appendToHTML(fmt.drawSubmit("NEWSEARCH",mgr.translate("PCMWFREETSEARCHENGNEWSEARCH: New Search"),""));
            appendToHTML("&nbsp;&nbsp;");
            //Bug 71861, End
            appendToHTML("</td>\n</tr>\n</table>\n");

            appendToHTML(tabs.showTabsInit());

            if (tabs.getActiveTab() == 1)
                appendToHTML(itemlay1.show());
            else if (tabs.getActiveTab() == 2)
            {
                appendToHTML(itemlay2.show());

                if (itemset2.countRows() != 0 && itemlay2.isSingleLayout())
                    appendToHTML(itemlay3.show());
            }
            //}

            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
            appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

            appendDirtyJavaScript("window.name=\"frmSearchEngine\";\n");
            

            if (bDisableSiteField)
            {
                appendDirtyJavaScript("f.CONTRACT.disabled = true;\n");
                appendDirtyJavaScript("f.CONTRACT.value = '" + mgr.encodeStringForJavascript(sOriginalSite) + "';\n"); // XSS_Safe ILSOLK 20070711
            }

            if (itemlay1.isEditLayout())
            {
                appendDirtyJavaScript("   if (f.ITEM1_RESOURCE_TYPE.value == f.TRANS_RES_TOOL.value)");
                appendDirtyJavaScript("    {");
                appendDirtyJavaScript("      f.ITEM1_TOOL_FACILITY_TYPE.disabled = false;");
                appendDirtyJavaScript("      f.ITEM1_ROLE_GROUP.disabled = true;");
                appendDirtyJavaScript("      f.ITEM1_ROLE_CODE.disabled = true;");
                appendDirtyJavaScript("    }");
                appendDirtyJavaScript("   else if (f.ITEM1_RESOURCE_TYPE.value == f.TRANS_RES_EMP.value)");
                appendDirtyJavaScript("    {");
                appendDirtyJavaScript("      f.ITEM1_TOOL_FACILITY_TYPE.disabled = true;");
                appendDirtyJavaScript("      f.ITEM1_ROLE_GROUP.disabled = false;");
                appendDirtyJavaScript("      f.ITEM1_ROLE_CODE.disabled = false;");
                appendDirtyJavaScript("    }");

            }
            
	    if ("EMPLOYEE".equals(sResourceType))
                appendDirtyJavaScript("\n\nf.RESOURCE_TYPE.selectedIndex = 1;\n");
            else if ("TOOLS_AND_FACILITY".equals(sResourceType)) {
	        appendDirtyJavaScript("\n\nf.RESOURCE_TYPE.selectedIndex = 2;\n");
		bneedToSetDefaultResource = false;
	    }
            
	    if (bneedToSetDefaultResource)
            {
                appendDirtyJavaScript("\n\nf.RESOURCE_TYPE.selectedIndex = 1;\n"); 
            }

            
                        
            appendDirtyJavaScript("\n\nhandleFieldStatus();\n\n");

            appendDirtyJavaScript("function handleFieldStatus()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   if (f.RESOURCE_TYPE.selectedIndex == 1)\n");
            appendDirtyJavaScript("   {\n");
            appendDirtyJavaScript("      f.ADDQUAL.disabled = false;\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_TYPE.disabled = true;\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_ID.disabled = true;\n");
            appendDirtyJavaScript("      f.COMPETENCE_GROUP.disabled = false;\n");
            appendDirtyJavaScript("      f.COMPETENCE.disabled = false;\n");
            appendDirtyJavaScript("      f.EMPLOYEE.disabled = false;\n");
	    appendDirtyJavaScript("      f.TEMP_RES_TYEP.value = 'EMPLOYEE';\n");
            appendDirtyJavaScript("   if (document.form.QUAL.value == \"TRUE\")\n");
            appendDirtyJavaScript("      f.CBADDQUALIFICATION.checked=true ;\n");
            appendDirtyJavaScript("else\n");
            appendDirtyJavaScript("      f.CBADDQUALIFICATION.checked=false ;\n");
            appendDirtyJavaScript(" f.CBADDQUALIFICATION.disabled = true ;\n");
            appendDirtyJavaScript("\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_TYPE.value = '';\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_ID.value = '';\n");
            appendDirtyJavaScript("\n");
            appendDirtyJavaScript("      validateToolFacilityType();\n");
            appendDirtyJavaScript("      validateToolFacilityId();\n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("   else if (f.RESOURCE_TYPE.selectedIndex == 2)\n");
            appendDirtyJavaScript("   {\n");
            appendDirtyJavaScript("      f.ADDQUAL.disabled = true;\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_TYPE.disabled = false;\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_ID.disabled = false;\n");
            appendDirtyJavaScript("      f.COMPETENCE_GROUP.disabled = true;\n");
            appendDirtyJavaScript("      f.COMPETENCE.disabled = true;\n");
            appendDirtyJavaScript("      f.EMPLOYEE.disabled = true;\n");
            appendDirtyJavaScript("      f.CBADDQUALIFICATION.disabled = false ;\n");
	    appendDirtyJavaScript("      f.TEMP_RES_TYEP.value = 'TOOLS_AND_FACILITY';\n");
            appendDirtyJavaScript("      if (f.CBADDQUALIFICATION.checked == true)\n");
            appendDirtyJavaScript("      {\n");
            appendDirtyJavaScript("      document.form.QUAL.value = \"TRUE\";\n");
            appendDirtyJavaScript("      f.CBADDQUALIFICATION.checked = false ;\n");
            appendDirtyJavaScript("      }\n");
            appendDirtyJavaScript("       f.CBADDQUALIFICATION.disabled = true ;\n");
            appendDirtyJavaScript("      f.COMPETENCE_GROUP.value = '';\n");
            appendDirtyJavaScript("      f.COMPETENCE.value = '';\n");
            appendDirtyJavaScript("      f.EMPLOYEE.value = '';\n");
            appendDirtyJavaScript("\n");
            appendDirtyJavaScript("      validateCompetenceGroup();\n");
            appendDirtyJavaScript("      validateCompetence();\n");
            appendDirtyJavaScript("      validateEmployee();\n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("   {\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_TYPE.disabled = false;\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_ID.disabled = false;\n");
            appendDirtyJavaScript("      f.COMPETENCE_GROUP.disabled = false;\n");
            appendDirtyJavaScript("      f.COMPETENCE.disabled = false;\n");
            appendDirtyJavaScript("      f.EMPLOYEE.disabled = false;\n");
            appendDirtyJavaScript("\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_TYPE.value = '';\n");
            appendDirtyJavaScript("      f.TOOL_FACILITY_ID.value = '';\n");
            appendDirtyJavaScript("      f.COMPETENCE_GROUP.value = '';\n");
            appendDirtyJavaScript("      f.COMPETENCE.value = '';\n");
            appendDirtyJavaScript("      f.EMPLOYEE.value = '';\n");
            appendDirtyJavaScript("\n");
            appendDirtyJavaScript("      validateToolFacilityType();\n");
            appendDirtyJavaScript("      validateToolFacilityId();\n");
            appendDirtyJavaScript("      validateCompetenceGroup();\n");
            appendDirtyJavaScript("      validateCompetence();\n");
            appendDirtyJavaScript("      validateEmployee();\n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("   handleButtons();\n");
            appendDirtyJavaScript("}\n\n");   

            appendDirtyJavaScript("function handleButtons(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   validateContract(i);\n");
            appendDirtyJavaScript("   validateEarliestDateTime(i);\n");
            appendDirtyJavaScript("   validateLatestDateTime(i);\n");
            appendDirtyJavaScript("   validateExecutionTime(i);\n");
            appendDirtyJavaScript("   if (f.CONTRACT.value == '' || f.EARLIEST_DATE_TIME.value == '' || f.LATEST_DATE_TIME.value == '' || f.EXECUTION_TIME.value == '' || f.RESOURCE_TYPE.selectedIndex == 0)\n");
            appendDirtyJavaScript("   {\n");
            appendDirtyJavaScript("      f.ADDTOROW.disabled = true;\n");

            
            if (!bCombineLineExist)
            {
                appendDirtyJavaScript("      f.SEARCHRESOURCE.disabled = true;\n");
            }

            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("   {\n");
            appendDirtyJavaScript("      f.ADDTOROW.disabled = false;\n");
            appendDirtyJavaScript("      f.SEARCHRESOURCE.disabled = false;\n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("   return true;\n");
            appendDirtyJavaScript("}\n\n");

            appendDirtyJavaScript("function preLovToolFacilityType(i,params)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   if (f.RESOURCE_TYPE.selectedIndex == 1)\n");
            appendDirtyJavaScript("      return;\n\n");
            appendDirtyJavaScript("   if(params)\n");
            appendDirtyJavaScript("      PARAM = params;\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("      PARAM = '';\n");
            appendDirtyJavaScript("   var enable_multichoice = (true && HEAD_IN_FIND_MODE);\n");
            appendDirtyJavaScript("   MULTICH = \"\" + enable_multichoice;\n");
            appendDirtyJavaScript("   MASK = \"\";\n");
            appendDirtyJavaScript("   KEY_VALUE = (getValue_('TOOL_FACILITY_TYPE',i).indexOf('%') !=-1)? getValue_('TOOL_FACILITY_TYPE',i):'';\n");
            appendDirtyJavaScript("   FIELDKEYVALUE = '&__KEY_VALUE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i));\n");
            appendDirtyJavaScript("   lovToolFacilityType(i,params);");
            appendDirtyJavaScript("}\n\n");

            appendDirtyJavaScript("function preLovToolFacilityId(i,params)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   if (f.RESOURCE_TYPE.selectedIndex == 1)\n");
            appendDirtyJavaScript("      return;\n\n");
            appendDirtyJavaScript("   if(params)\n");
            appendDirtyJavaScript("      PARAM = params;\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("      PARAM = '';\n");
            appendDirtyJavaScript("   var enable_multichoice = (true && HEAD_IN_FIND_MODE);\n");
            appendDirtyJavaScript("   MULTICH = \"\" + enable_multichoice;\n");
            appendDirtyJavaScript("   MASK = \"\";\n");
            appendDirtyJavaScript("   KEY_VALUE = (getValue_('TOOL_FACILITY_ID',i).indexOf('%') !=-1)? getValue_('TOOL_FACILITY_ID',i):'';\n");
            appendDirtyJavaScript("   FIELDKEYVALUE = '&__KEY_VALUE=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i));\n");
            appendDirtyJavaScript("   lovToolFacilityId(i,params);");
            appendDirtyJavaScript("}\n\n");

            appendDirtyJavaScript("function preLovCompetenceGroup(i,params)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   if (f.RESOURCE_TYPE.selectedIndex == 2)\n");
            appendDirtyJavaScript("      return;\n\n");
            appendDirtyJavaScript("   if(params)\n");
            appendDirtyJavaScript("      PARAM = params;\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("      PARAM = '';\n");
            appendDirtyJavaScript("   var enable_multichoice = (true && HEAD_IN_FIND_MODE);\n");
            appendDirtyJavaScript("   MULTICH = \"\" + enable_multichoice;\n");
            appendDirtyJavaScript("   MASK = \"\";\n");
            appendDirtyJavaScript("   KEY_VALUE = (getValue_('COMPETENCE_GROUP',i).indexOf('%') !=-1)? getValue_('COMPETENCE_GROUP',i):'';\n");
            appendDirtyJavaScript("   FIELDKEYVALUE = '&__KEY_VALUE=' + URLClientEncode(getValue_('COMPETENCE_GROUP',i));\n");
            appendDirtyJavaScript("   lovCompetenceGroup(i,params);");
            appendDirtyJavaScript("}\n\n");

            appendDirtyJavaScript("function preLovCompetence(i,params)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   if (f.RESOURCE_TYPE.selectedIndex == 2)\n");
            appendDirtyJavaScript("      return;\n\n");
            appendDirtyJavaScript("   if(params)\n");
            appendDirtyJavaScript("      PARAM = params;\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("      PARAM = '';\n");
            appendDirtyJavaScript("   var enable_multichoice = (true && HEAD_IN_FIND_MODE);\n");
            appendDirtyJavaScript("   MULTICH = \"\" + enable_multichoice;\n");
            appendDirtyJavaScript("   MASK = \"\";\n");
            appendDirtyJavaScript("   KEY_VALUE = (getValue_('COMPETENCE',i).indexOf('%') !=-1)? getValue_('COMPETENCE',i):'';\n");
            appendDirtyJavaScript("   FIELDKEYVALUE = '&__KEY_VALUE=' + URLClientEncode(getValue_('COMPETENCE',i));\n");
            appendDirtyJavaScript("   lovCompetence(i,params);");
            appendDirtyJavaScript("}\n\n");

            appendDirtyJavaScript("function preLovEmployee(i,params)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   if (f.RESOURCE_TYPE.selectedIndex == 2)\n");
            appendDirtyJavaScript("      return;\n\n");
            appendDirtyJavaScript("   if(params)\n");
            appendDirtyJavaScript("      PARAM = params;\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("      PARAM = '';\n");
            appendDirtyJavaScript("   var enable_multichoice = (true && HEAD_IN_FIND_MODE);\n");
            appendDirtyJavaScript("   MULTICH = \"\" + enable_multichoice;\n");
            appendDirtyJavaScript("   MASK = \"\";\n");
            appendDirtyJavaScript("   KEY_VALUE = (getValue_('EMPLOYEE',i).indexOf('%') !=-1)? getValue_('EMPLOYEE',i):'';\n");
            appendDirtyJavaScript("   FIELDKEYVALUE = '&__KEY_VALUE=' + URLClientEncode(getValue_('EMPLOYEE',i));\n");
            appendDirtyJavaScript("   lovEmployee(i,params);");
            appendDirtyJavaScript("}\n\n");

            appendDirtyJavaScript("function preLovContract(i,params)\n");
            appendDirtyJavaScript("{\n");
           
            if (bDisableSiteField)
                appendDirtyJavaScript("      return;\n\n");
            else
            {
                appendDirtyJavaScript("   if(params)\n");
                appendDirtyJavaScript("      PARAM = params;\n");
                appendDirtyJavaScript("   else\n");
                appendDirtyJavaScript("      PARAM = '';\n");
                appendDirtyJavaScript("   var enable_multichoice = (true && HEAD_IN_FIND_MODE);\n");
                appendDirtyJavaScript("   MULTICH = \"\" + enable_multichoice;\n");
                appendDirtyJavaScript("   MASK = \"\";\n");
                appendDirtyJavaScript("   KEY_VALUE = (getValue_('CONTRACT',i).indexOf('%') !=-1)? getValue_('CONTRACT',i):'';\n");
                appendDirtyJavaScript("   FIELDKEYVALUE = '&__KEY_VALUE=' + URLClientEncode(getValue_('CONTRACT',i));\n");
                appendDirtyJavaScript("   lovContract(i,params);");
            }
            appendDirtyJavaScript("}\n\n");

            appendDirtyJavaScript("function preLovItem1ToolFacilityType(i,params)\n");
            appendDirtyJavaScript("{\n");
            //To be used if multirow edit is enabled.
            appendDirtyJavaScript("   if (f.ITEM1_RESOURCE_TYPE.value == f.TRANS_RES_EMP.value)\n");
            appendDirtyJavaScript("      return;\n\n");
            appendDirtyJavaScript("   if(params)\n");
            appendDirtyJavaScript("      PARAM = params;\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("      PARAM = '';\n");
            appendDirtyJavaScript("   var enable_multichoice = (true && ITEM1_IN_FIND_MODE);\n");
            appendDirtyJavaScript("   MULTICH = \"\" + enable_multichoice;\n");
            appendDirtyJavaScript("   MASK = \"\";\n");
            appendDirtyJavaScript("   KEY_VALUE = (getValue_('ITEM1_TOOL_FACILITY_TYPE',i).indexOf('%') !=-1)? getValue_('ITEM1_TOOL_FACILITY_TYPE',i):'';\n");
            appendDirtyJavaScript("   FIELDKEYVALUE = '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_TOOL_FACILITY_TYPE',i));\n");
            appendDirtyJavaScript("   lovItem1ToolFacilityType(i,params);");
            appendDirtyJavaScript("}\n\n");

            if ("frmSearchEngine".equals(newWinHandle))
            {
                appendDirtyJavaScript("window.open(\"");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString)); // XSS_Safe ILSOLK 20070711
                appendDirtyJavaScript("\", \"");
                appendDirtyJavaScript(newWinHandle);
                appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars,dependent=yes,width=800,height=550\");\n");  
            }   

            if (!mgr.isEmpty(sExecutionInfo))
            {
                appendDirtyJavaScript("alert('");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(sExecutionInfo)); // XSS_Safe ILSOLK 20070711
                appendDirtyJavaScript("');\n");
            }
            else if (!mgr.isEmpty(sConfirmationText))
            {
                if (bOpenNewWindow)
                {
                    appendDirtyJavaScript("window.open(\"");
                    appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString)); // XSS_Safe ILSOLK 20070711
                    appendDirtyJavaScript("\", \"");
                    appendDirtyJavaScript(newWinHandle);
                    appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars,dependent=yes,width=800,height=550\");\n");  

                }
                else
                {
                    appendDirtyJavaScript("if (confirm('");
                    appendDirtyJavaScript(sConfirmationText);
                    appendDirtyJavaScript("'))\n");
                    appendDirtyJavaScript("{\n");
                    appendDirtyJavaScript("  window.open(\"");
                    appendDirtyJavaScript(sConfirmedUrlString);
                    appendDirtyJavaScript("\", \"");
                    appendDirtyJavaScript(sConfirmedNewWinHandle);
                    appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\");\n");      
                    appendDirtyJavaScript("}\n");
                } 
            }

            
            appendDirtyJavaScript("\n\nfunction isNotDirty()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   return true;\n");
            appendDirtyJavaScript("}\n");
            
        }
            appendDirtyJavaScript("function validateWoNo(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM4',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkWoNo(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('WO_NO',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('WO_NO',i).value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=WO_NO'\n");
            appendDirtyJavaScript("		+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
            appendDirtyJavaScript("		);\n");
            //appendDirtyJavaScript("alert(r)\n");
            appendDirtyJavaScript("	if (Trim(r)=='TRUE')\n");
            appendDirtyJavaScript("      f.ADDHEADER.disabled = true;\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("      f.ADDHEADER.disabled = false;\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateContract(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("     sendValidations = 'FALSE';\n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkContract(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('EMPLOYEE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=CONTRACT'\n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&EMPLOYEE=' + URLClientEncode(getValue_('EMPLOYEE',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'CONTRACT',i,'Site') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('COMPANY',i,0);\n");
            appendDirtyJavaScript("		assignValue_('SITE_DESCRIPTION',i,1);\n");
            appendDirtyJavaScript("		assignValue_('EMPLOYEE_NAME',i,2);\n");
            appendDirtyJavaScript("             sendValidations = 'TRUE';\n");
            appendDirtyJavaScript("	}\n");  
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.CONTRACT.value = '';\n");
            appendDirtyJavaScript("		f.SITE_DESCRIPTION.value = '';\n");
            appendDirtyJavaScript("		f.COMPANY.value = '';\n");
            appendDirtyJavaScript("		f.EMPLOYEE_NAME.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	if( sendValidations == 'TRUE' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		validateOrgCode(i);\n");
            appendDirtyJavaScript("		validateCompetenceGroup(i);\n");
            appendDirtyJavaScript("		validateCompetence(i);\n");
            appendDirtyJavaScript("	}\n");  
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateOrgCode(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkOrgCode(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ORG_CODE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ORG_CODE',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('ORG_DESCRIPTION',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=ORG_CODE'\n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ORG_CODE',i,'Maintenance Organization') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ORG_DESCRIPTION',i,0);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.ORG_CODE.value = '';\n");
            appendDirtyJavaScript("		f.ORG_DESCRIPTION.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateCompetenceGroup(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkCompetenceGroup(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('COMPETENCE_GROUP',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('COMPETENCE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('COMPETENCE_GROUP',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('COMPETENCE_GRP_DESCRIPTION',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=COMPETENCE_GROUP'\n");
            appendDirtyJavaScript("		+ '&COMPETENCE_GROUP=' + URLClientEncode(getValue_('COMPETENCE_GROUP',i))\n");
            appendDirtyJavaScript("		+ '&COMPETENCE=' + URLClientEncode(getValue_('COMPETENCE',i))\n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'COMPETENCE_GROUP',i,'Craft Group') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('COMPETENCE_GRP_DESCRIPTION',i,0);\n");
            //Bug 71867, Start
            appendDirtyJavaScript("		assignValue_('COMPETENCE',i,1);\n");
            appendDirtyJavaScript("		assignValue_('COMPETENCE_DESCRIPTION',i,2);\n");
            //Bug 71867, End
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.COMPETENCE_GROUP.value = '';\n");
            appendDirtyJavaScript("		f.COMPETENCE_GRP_DESCRIPTION.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");
            
            appendDirtyJavaScript("function validateCompetence(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkCompetence(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('COMPETENCE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('COMPETENCE_GROUP',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('COMPETENCE',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("   	getField_('COMPETENCE',i).value = '';\n");
            appendDirtyJavaScript("		getField_('COMPETENCE_DESCRIPTION',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=COMPETENCE'\n");
            appendDirtyJavaScript("		+ '&COMPETENCE=' + URLClientEncode(getValue_('COMPETENCE',i))\n");
            appendDirtyJavaScript("		+ '&COMPETENCE_GROUP=' + URLClientEncode(getValue_('COMPETENCE_GROUP',i))\n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'COMPETENCE',i,'Craft') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('COMPETENCE',i,0);\n");
            appendDirtyJavaScript("		assignValue_('COMPETENCE_DESCRIPTION',i,1);\n");
            //Bug 71867, Start
            appendDirtyJavaScript("		assignValue_('COMPETENCE_GROUP',i,2);\n");
            appendDirtyJavaScript("		assignValue_('COMPETENCE_GRP_DESCRIPTION',i,3);\n");
            //Bug 71867, End
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.COMPETENCE.value = '';\n");
            appendDirtyJavaScript("		f.COMPETENCE_DESCRIPTION.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateToolFacilityType(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkToolFacilityType(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('TOOL_FACILITY_TYPE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('TOOL_FACILITY_ID',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('TOOL_FACILITY_TYPE',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('TOOL_FACILITY_TYPE_DESC',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=TOOL_FACILITY_TYPE'\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'TOOL_FACILITY_TYPE',i,'Tool/Facility Type') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_TYPE_DESC',i,0);\n");
            //Bug 71867, Start
            appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_ID',i,1);\n");
            appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_DESC',i,2);\n");
            //Bug 71867, End
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.TOOL_FACILITY_TYPE.value = '';\n");
            appendDirtyJavaScript("		f.TOOL_FACILITY_TYPE_DESC.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateToolFacilityId(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("        if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("        setDirty();\n");
            appendDirtyJavaScript("        if( !checkToolFacilityId(i) ) return;\n");
            appendDirtyJavaScript("        if( getValue_('TOOL_FACILITY_TYPE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("        if( getValue_('TOOL_FACILITY_ID',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("        if( getValue_('CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("        if( getValue_('ORG_CODE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("        if( getValue_('TOOL_FACILITY_ID',i)=='' )\n");
            appendDirtyJavaScript("        {\n");
            appendDirtyJavaScript("                getField_('TOOL_FACILITY_DESC',i).value = '';\n");
            appendDirtyJavaScript("                return;\n");
            appendDirtyJavaScript("        }\n");
            appendDirtyJavaScript("        window.status='Please wait for validation';\n");
            appendDirtyJavaScript("         r = __connect(\n");
            appendDirtyJavaScript("                APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=TOOL_FACILITY_ID'\n");
            appendDirtyJavaScript("                + '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
            appendDirtyJavaScript("                + '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
            appendDirtyJavaScript("                + '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("                + '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
            appendDirtyJavaScript("                );\n");
            appendDirtyJavaScript("        window.status='';\n");
            appendDirtyJavaScript("        if( checkStatus_(r,'TOOL_FACILITY_ID',i,'Tool/Facility ID') )\n");
            appendDirtyJavaScript("        {\n");
            appendDirtyJavaScript("                assignValue_('TOOL_FACILITY_DESC',i,0);\n");
            //Bug 71867, Start
            appendDirtyJavaScript("	           assignValue_('TOOL_FACILITY_TYPE',i,1);\n");
            appendDirtyJavaScript("	           assignValue_('TOOL_FACILITY_TYPE_DESC',i,2);\n");
            //Bug 71867, End
            appendDirtyJavaScript("        }\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.TOOL_FACILITY_ID.value = '';\n");
            appendDirtyJavaScript("		f.TOOL_FACILITY_DESC.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateItem1OrgCode(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem1OrgCode(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_ORG_CODE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_ORG_CODE',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('ITEM1_ORG_CODE',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=ITEM1_ORG_CODE'\n");
            appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_ORG_CODE',i,'Maintenance Organization') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_ORG_CODE',i,0);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.ITEM1_ORG_CODE.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateItem1RoleGroup(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem1RoleGroup(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_ROLE_GROUP',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_ROLE_CODE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_ROLE_GROUP',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('ITEM1_ROLE_GROUP',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=ITEM1_ROLE_GROUP'\n");
            appendDirtyJavaScript("		+ '&ITEM1_ROLE_GROUP=' + URLClientEncode(getValue_('ITEM1_ROLE_GROUP',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_ROLE_CODE=' + URLClientEncode(getValue_('ITEM1_ROLE_CODE',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_ROLE_GROUP',i,'Craft Group') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_ROLE_GROUP',i,0);\n");
            //Bug 71867, Start
            appendDirtyJavaScript("		assignValue_('ITEM1_ROLE_CODE',i,1);\n");
            //Bug 71867, End
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.ITEM1_ROLE_GROUP.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateItem1RoleCode(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem1RoleCode(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_ROLE_CODE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_ROLE_GROUP',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_ROLE_CODE',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('ITEM1_ROLE_CODE',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=ITEM1_ROLE_CODE'\n");
            appendDirtyJavaScript("   	+ '&ITEM1_ROLE_CODE=' + URLClientEncode(getValue_('ITEM1_ROLE_CODE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_ROLE_GROUP=' + URLClientEncode(getValue_('ITEM1_ROLE_GROUP',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_ROLE_CODE',i,'Craft') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_ROLE_CODE',i,0);\n");
            //Bug 71867, Start
            appendDirtyJavaScript("		assignValue_('ITEM1_ROLE_GROUP',i,1);\n");
            //Bug 71867, End
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.ITEM1_ROLE_CODE.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateItem1ToolFacilityType(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem1ToolFacilityType(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_TOOL_FACILITY_TYPE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_RESOURCE_ID',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_TOOL_FACILITY_TYPE',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('ITEM1_TOOL_FACILITY_TYPE',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=ITEM1_TOOL_FACILITY_TYPE'\n");
            appendDirtyJavaScript("		+ '&ITEM1_TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('ITEM1_TOOL_FACILITY_TYPE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_RESOURCE_ID=' + URLClientEncode(getValue_('ITEM1_RESOURCE_ID',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_TOOL_FACILITY_TYPE',i,'Tool/Facility Type') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_TOOL_FACILITY_TYPE',i,0);\n");
            //Bug 71867, Start
            appendDirtyJavaScript("		assignValue_('ITEM1_RESOURCE_ID',i,1);\n");
            appendDirtyJavaScript("		assignValue_('RESOURCE_DESCRIPTION',i,2);\n");
            //Bug 71867, End
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.ITEM1_TOOL_FACILITY_TYPE.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateItem1ResourceId(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem1ResourceId(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_RESOURCE_ID',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_TOOL_FACILITY_TYPE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_ORG_CODE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_RESOURCE_TYPE_DB',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM1_RESOURCE_ID',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('ITEM1_RESOURCE_ID',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");                                 
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/FreeTimeSearchEngine.page'+'?VALIDATE=ITEM1_RESOURCE_ID'\n");
            appendDirtyJavaScript("		+ '&ITEM1_RESOURCE_ID=' + URLClientEncode(getValue_('ITEM1_RESOURCE_ID',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('ITEM1_TOOL_FACILITY_TYPE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_RESOURCE_TYPE_DB=' + URLClientEncode(getValue_('ITEM1_RESOURCE_TYPE_DB',i))\n");
            appendDirtyJavaScript("		);\n");                                                                                        
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_RESOURCE_ID',i,'Resource ID') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_RESOURCE_ID',i,0);\n");
            //Bug 71867, Start
            appendDirtyJavaScript("		assignValue_('ITEM1_TOOL_FACILITY_TYPE',i,1);\n");
            //Bug 71867, End
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		f.ITEM1_RESOURCE_ID.value = '';\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function lovCompetence(i,params)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if(params) param = params;\n");
            appendDirtyJavaScript("	else param = '';\n");
            appendDirtyJavaScript("	whereCond = '';\n");
            appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
            appendDirtyJavaScript("	var key_value = (getValue_('COMPETENCE',i).indexOf('%') !=-1)? getValue_('COMPETENCE',i):'';\n");
            appendDirtyJavaScript("	if ((document.form.CONTRACT.value != '') && (document.form.COMPETENCE_GROUP.value != ''))\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		whereCond = \" ROLE_CODE IN (SELECT ROLE_CODE\"\n");
            appendDirtyJavaScript("		whereCond = whereCond + \" FROM ROLE_TO_SITE\"\n");
            //Bug 71867, Start
            appendDirtyJavaScript("		whereCond = whereCond + \" WHERE BELONGS_TO_ROLE ='\" + document.form.COMPETENCE_GROUP.value +\"')\"\n");
            //Bug 71867, End
            appendDirtyJavaScript("	openLOVWindow('COMPETENCE',i,\n");
            appendDirtyJavaScript("		'../mscomw/RoleToSiteLov.page' + '?__VIEW=DUMMY&__INIT=1&MULTICHOICE='+enable_multichoice+'&__WHERE='+whereCond+''\n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		,600,445,'validateCompetence');\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("	openLOVWindow('COMPETENCE',i,\n");
            appendDirtyJavaScript("		'../mscomw/RoleToSiteLov.page' + '?__VIEW=DUMMY&__INIT=1&MULTICHOICE='+enable_multichoice+''\n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		,600,445,'validateCompetence');\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function lovItem1RoleCode(i,params)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if(params) param = params;\n");
            appendDirtyJavaScript("	else param = '';\n");
            appendDirtyJavaScript("	whereCond = '';\n");
            appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
            appendDirtyJavaScript("	var key_value = (getValue_('ITEM1_ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ITEM1_ROLE_CODE',i):'';\n");
            appendDirtyJavaScript("	if (document.form.ITEM1_ROLE_GROUP.value != '')\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		whereCond = \" ROLE_CODE IN (SELECT ROLE_CODE\"\n");
            appendDirtyJavaScript("		whereCond = whereCond + \" FROM ROLE_TO_SITE\"\n");
            //Bug 71867, Start
            appendDirtyJavaScript("		whereCond = whereCond + \" WHERE BELONGS_TO_ROLE ='\" + document.form.ITEM1_ROLE_GROUP.value +\"')\"\n");
            //Bug 71867, End
            appendDirtyJavaScript("	openLOVWindow('ITEM1_ROLE_CODE',i,\n");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=ROLE_TO_SITE_LOV&__FIELD=Craft&__INIT=1' + '&MULTICHOICE='+enable_multichoice+'&__WHERE='+whereCond+''\n");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ROLE_CODE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_ROLE_CODE=' + URLClientEncode(key_value)\n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		,600,445,'validateItem1RoleCode');\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("	openLOVWindow('ITEM1_ROLE_CODE',i,\n");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=ROLE_TO_SITE_LOV&__FIELD=Craft&__INIT=1' + '&MULTICHOICE='+enable_multichoice+''\n");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ROLE_CODE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_ROLE_CODE=' + URLClientEncode(key_value)\n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		,600,445,'validateItem1RoleCode');\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");   

    }
}
